package gr.uom.java.jdeodorant.refactoring.views;

import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.Wizard;

import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.ClassObjectCandidate;
import gr.uom.java.ast.LPLMethodObject;
import gr.uom.java.ast.MethodObject;
import gr.uom.java.ast.TypeObject;
import gr.uom.java.jdeodorant.refactoring.manipulators.DeleteClassRefactoring;
import gr.uom.java.jdeodorant.refactoring.manipulators.MergeClassRefactoring;
import gr.uom.java.jdeodorant.refactoring.manipulators.ParameterMethodRefactoring;

public class SGRefactorWizard extends Wizard {

	private ClassObjectCandidate classToRefactor;
	private SGRefactorInitialPage initialPage;
	private Set<ClassObject> _classObjectToBeExamined;
	private Action identifyBadSmellsAction;	
	
	public SGRefactorWizard(ClassObjectCandidate classToRefactor, Set<ClassObject> _classObjectToBeExamined, Action identifyBadSmellsAction) {
		super();
		setNeedsProgressMonitor(true);
		this.classToRefactor = classToRefactor;
		this._classObjectToBeExamined=_classObjectToBeExamined;
		this.identifyBadSmellsAction=identifyBadSmellsAction;
	}

	@Override
	public String getWindowTitle() {
		return "Refactoring";
	}
	
	@Override
	public void addPages() {
		initialPage = new SGRefactorInitialPage(classToRefactor);
		addPage(initialPage);
	}
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		

		// Switch w.r.t smell type and Details
		if(classToRefactor.getCodeSmellType().equals("Abstract Class")) {
			if(classToRefactor.getNumChild() == 0) {
				DeleteClassRefactoring _refactor = new DeleteClassRefactoring(classToRefactor);
				_refactor.commentizeWholeContent();
				_refactor.processRefactoring();
			} else {
				// Integrate Child and Parent
				ClassObjectCandidate childClass;
				for(ClassObject examiningClass : _classObjectToBeExamined) {
					if(examiningClass.getName().equals(classToRefactor.getName())) continue;
					
					TypeObject superClass = examiningClass.getSuperclass();
					if(superClass != null) {
						if (superClass.getClassType().equals(classToRefactor.getName())) {
							childClass = new ClassObjectCandidate(examiningClass);
							
							MergeClassRefactoring _refactor = new MergeClassRefactoring(classToRefactor, childClass);
							_refactor.mergeIntoChild();
							_refactor.buildContentInOneString();
							_refactor.processRefactoringParent();
							_refactor.processRefactoringChild();
							
							break;
						}
					}
				}
			}
		} else if (classToRefactor.getCodeSmellType().equals("Interface Class")) {
			if(classToRefactor.getNumChild() == 0) {
				DeleteClassRefactoring _refactor = new DeleteClassRefactoring(classToRefactor);
				_refactor.commentizeWholeContent();
				_refactor.processRefactoring();
			} else {
				ClassObjectCandidate childClass;
				for (ClassObject examiningClass : _classObjectToBeExamined) {
					if (examiningClass.getName().equals(classToRefactor.getName()))
						continue;

					ListIterator<TypeObject> parentClasses = examiningClass.getInterfaceIterator();
					while(parentClasses.hasNext()) {
						TypeObject parentClass = parentClasses.next();
						if (parentClass.getClassType().equals(classToRefactor.getName())) {
							childClass = new ClassObjectCandidate(examiningClass);

							MergeClassRefactoring _refactor = new MergeClassRefactoring(classToRefactor, childClass);
							_refactor.mergeIntoChild();
							_refactor.buildContentInOneString();
							_refactor.processRefactoringParent();
							_refactor.processRefactoringChild();

							break;
						}
					}
				}
			}
		} else if (classToRefactor.getCodeSmellType().equals("Unnecessary Parameters")) {
			List<MethodObject> _smellingMethods = classToRefactor.getSmellingMethods();
			
			for(MethodObject target : _smellingMethods) {
				ParameterMethodRefactoring _refactor = new ParameterMethodRefactoring(classToRefactor, target);
				_refactor.setUnusedParameterList();
				_refactor.setUsedParameterList();
				_refactor.resolveUnnecessaryParameters();
				_refactor.processRefactoring();
			}
		}

		// Re-detection
		identifyBadSmellsAction.run();
		
		
		
		return true;
	}

}
