package gr.uom.java.jdeodorant.refactoring.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.Wizard;

import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.ClassObjectCandidate;
import gr.uom.java.ast.LPLMethodObject;
import gr.uom.java.ast.LPLSmellContent;
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
	private IJavaProject activeProject;
	
	public SGRefactorWizard(ClassObjectCandidate classToRefactor, Set<ClassObject> _classObjectToBeExamined, Action identifyBadSmellsAction, IJavaProject activeProject) {
		super();
		setNeedsProgressMonitor(true);
		this.classToRefactor = classToRefactor;
		this._classObjectToBeExamined = _classObjectToBeExamined;
		this.identifyBadSmellsAction = identifyBadSmellsAction;
		this.activeProject = activeProject;
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
			try {
				for(MethodObject target : _smellingMethods) {
					ParameterMethodRefactoring _refactor = new ParameterMethodRefactoring(classToRefactor, target);
					_refactor.setUnusedParameterList();
					_refactor.setUsedParameterList();
					_refactor.resolveUnnecessaryParameters();
					_refactor.processRefactoring();
					changeMethodsInProject(activeProject, target, _refactor.getUnusedParameterIndex());
				}
			}catch(Exception e){
			}
		}

		// Re-detection
		identifyBadSmellsAction.run();
		
		
		
		return true;
	}
	
	
	static public void changeMethodsInProject(IJavaProject javaProject, final MethodObject smellContent, final List<Integer> indexList) throws JavaModelException {
		IPackageFragment[] allPkg = javaProject.getPackageFragments();
		List<IPackageFragment> srcPkgs = new ArrayList<IPackageFragment>();
		for(IPackageFragment myPackage : allPkg) {
			if(myPackage.getKind() == IPackageFragmentRoot.K_SOURCE && myPackage.getCompilationUnits().length != 0) {
				srcPkgs.add(myPackage);
			}
		}
		
		ArrayList<ICompilationUnit> srcCompilationUnits;
		srcCompilationUnits = new ArrayList<ICompilationUnit>();
		for(IPackageFragment srcPkg : srcPkgs) {
			srcCompilationUnits.addAll(Arrays.asList(srcPkg.getCompilationUnits()));
		}
		int i = 0;
		
		for(final ICompilationUnit iCu : srcCompilationUnits) {
			System.out.println(i);
			System.out.println("cu name: " + iCu.getElementName());
			i++;
			if(iCu.getUnderlyingResource() instanceof IFile) {
				ASTParser parser = ASTParser.newParser(AST.JLS8);
				parser.setResolveBindings(true);
				parser.setKind(ASTParser.K_COMPILATION_UNIT);
				parser.setBindingsRecovery(true);
				parser.setSource(iCu);
				CompilationUnit cu = (CompilationUnit) parser.createAST(null);
				//System.out.println(iCu.getElementName());
				ASTVisitor visitor = new ASTVisitor() {
					public boolean visit(MethodInvocation node) {
						System.out.println(node.getName().toString());
						if(node.getName().toString().equals(smellContent.getName()))
							changeMethodCall(iCu, node.getStartPosition(), smellContent, indexList);
						return true;
					}
				};
				cu.accept(visitor);
			}
		}
	}
	

	protected static void changeMethodCall(ICompilationUnit iCu, int startPosition, MethodObject smellContent, List<Integer> indexList) {
		try {
			ICompilationUnit workingCopy = iCu
					.getWorkingCopy(new WorkingCopyOwner() {
					}, null);
			IBuffer buffer = ((IOpenable) workingCopy).getBuffer();
			
			while (true) {
				if (buffer.getChar(startPosition) != '(') {
					startPosition += 1;
					continue;
				}
				break;
			}
			int numOfLeftPar = 0;
			int endPosition = startPosition;
			while (true) {
				if (buffer.getChar(endPosition) == '(') {
					numOfLeftPar += 1;
				} 
				else if (buffer.getChar(endPosition) == ')') {
					if (numOfLeftPar == 1)
						break;
					else
						numOfLeftPar -= 1;
				}
				endPosition += 1;
			}
			String argumentString = buffer.getContents().substring(startPosition + 1, endPosition);
			String argumentParts[] = argumentString.split(",");
			ArrayList<String> extractedArguments;
			extractedArguments = new ArrayList<String>();
			for(int it : indexList) {
				extractedArguments.add(argumentParts[it]);
				argumentParts[it] = null;
			}
			String refactoredArgumentString = "";
			for(String s : argumentParts) {
				if(s != null) {
					refactoredArgumentString += s.trim();
					refactoredArgumentString += ", ";
				}
			}
			
			refactoredArgumentString = "(" + refactoredArgumentString.substring(0, refactoredArgumentString.length()-2) + ")";
			System.out.println(refactoredArgumentString);
			
			buffer.replace(startPosition, endPosition - startPosition + 1, refactoredArgumentString);
			
			workingCopy.reconcile(ICompilationUnit.NO_AST, false, null, null);
			workingCopy.commitWorkingCopy(false, null);
			workingCopy.discardWorkingCopy();
			workingCopy.discardWorkingCopy();
		} catch (Exception e) {
		}
	}
	
	
	

}
