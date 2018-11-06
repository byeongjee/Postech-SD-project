package gr.uom.java.jdeodorant.refactoring.views;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.ListIterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressService;

import gr.uom.java.ast.TypeObject;
import gr.uom.java.ast.ASTReader;
import gr.uom.java.ast.AbstractMethodDeclaration;
import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.CompilationErrorDetectedException;
import gr.uom.java.ast.SystemObject;
import gr.uom.java.distance.CandidateRefactoring;
import gr.uom.java.distance.DistanceMatrix;
import gr.uom.java.distance.MoveMethodCandidateRefactoring;
import gr.uom.java.distance.MySystem;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSliceGroup;

/**
 * Detect Code Smell of Speculative Generality
 * @author 이재엽, 이주용
 *
 */
public class SpeculativeGenerality extends ViewPart {
	private IJavaProject activeProject;
	private IPackageFragmentRoot selectedPackageFragmentRoot;
	private IPackageFragment selectedPackageFragment;
	private ICompilationUnit selectedCompilationUnit;
	private IType selectedType;
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}
	
	private ClassObject[] getTable() {
		ClassObject[] table = null;
		try {
			IWorkbench wb = PlatformUI.getWorkbench();
			IProgressService ps = wb.getProgressService();
			if(ASTReader.getSystemObject() != null && activeProject.equals(ASTReader.getExaminedProject())) {
				new ASTReader(activeProject, ASTReader.getSystemObject(), null);
			}
			else {
				ps.busyCursorWhile(new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						try {
							new ASTReader(activeProject, monitor);
						} catch (CompilationErrorDetectedException e) {
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), MESSAGE_DIALOG_TITLE,
											"Compilation errors were detected in the project. Fix the errors before using JDeodorant.");
								}
							});
						}
					}
				});
			}
			SystemObject systemObject = ASTReader.getSystemObject();
			if(systemObject != null) {
				Set<ClassObject> classObjectsToBeExamined = new LinkedHashSet<ClassObject>();
				if(selectedPackageFragmentRoot != null) {
					classObjectsToBeExamined.addAll(systemObject.getClassObjects(selectedPackageFragmentRoot));
				}
				else if(selectedPackageFragment != null) {
					classObjectsToBeExamined.addAll(systemObject.getClassObjects(selectedPackageFragment));
				}
				else if(selectedCompilationUnit != null) {
					// ToDo :: Not Allowed!!!
				}
				else if(selectedType != null) {
					// ToDo :: Not Allowed!!!
				}
				else {
					// ToDo :: Not Allowed!!!
				}

				final Set<String> classNamesToBeExamined = new LinkedHashSet<String>();
				for(ClassObject classObject : classObjectsToBeExamined) {
					if(!classObject.isEnum() && !classObject.isInterface() && !classObject.isGeneratedByParserGenenator())
						classNamesToBeExamined.add(classObject.getName());
				}
				
				table=processMethod(classObjectsToBeExamined);
				
				/*
				final List<ClassObject> classObjectswithSG = new ArrayList<ClassObject>();
				for(ClassObject classObject : classObjectsToBeExamined) {
					if(classObject.isAbstract()) {
						int myCount=0;
						for(ClassObject childCandidate : classObjectsToBeExamined) {
							if(childCandidate.getSuperclass().getClassType().equals(classObject.getName())){
								myCount++;
							}
							if(myCount>=2)
								break;
						}
						if(myCount==1) {
							classObjectswithSG.add(classObject);
						}
					}
					else if(classObject.isInterface()) {
						int myCount=0;
						for(ClassObject childCandidate : classObjectsToBeExamined) {
							ListIterator<TypeObject> myIter=childCandidate.getInterfaceIterator();
							while(myIter.hasNext()) {
								//
								if(myIter.next().getClassType().equals(classObject.getName())) {
									myCount++;
									break;
								}
							}
							if(myCount>=2)
								break;
						}
						if(myCount==1) {
							classObjectswithSG.add(classObject);
						}
					}
				}
				
				table = new ClassObject[classObjectswithSG.size()];
				for(int i=0;i<classObjectswithSG.size();i++) {
					table[i]=classObjectswithSG.get(i);
				}
				*/
				
				
				/*
				MySystem system = new MySystem(systemObject, false);
				final DistanceMatrix distanceMatrix = new DistanceMatrix(system);
				final List<MoveMethodCandidateRefactoring> moveMethodCandidateList = new ArrayList<MoveMethodCandidateRefactoring>();

				ps.busyCursorWhile(new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						moveMethodCandidateList.addAll(distanceMatrix.getMoveMethodCandidateRefactoringsByAccess(classNamesToBeExamined, monitor));
					}
				});

				table = new CandidateRefactoring[moveMethodCandidateList.size()];
				int counter = 0;
				for(MoveMethodCandidateRefactoring candidate : moveMethodCandidateList) {
					table[counter] = candidate;
					counter++;
				}
				*/
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (CompilationErrorDetectedException e) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), MESSAGE_DIALOG_TITLE,
					"Compilation errors were detected in the project. Fix the errors before using JDeodorant.");
		}

		return table;	

	}

	/**
	 *  Examine the class objects
	 * @param classObjectsToBeExamined
	 * @return Array of ClassObject
	 */
	private ClassObject[] processMethod(final Set<ClassObject> classObjectsToBeExamined){	
		final List<ClassObject> classObjectswithSG = new ArrayList<ClassObject>();
		
		for(ClassObject targetClass : classObjectsToBeExamined) {
			if(targetClass.isAbstract()) {
				int childOfTargetNum = 0;
				
				for(ClassObject childCandidate : classObjectsToBeExamined) {
					if(childCandidate.getSuperclass().getClassType().equals(targetClass.getName())){
						childOfTargetNum++;
					}
					if(childOfTargetNum >= 2)
						break;
				}
				if(childOfTargetNum < 2) {
					classObjectswithSG.add(targetClass);
				}
			}
			else if(targetClass.isInterface()) {
				int childOfTargetNum = 0;
				
				for(ClassObject childCandidate : classObjectsToBeExamined) {
					ListIterator<TypeObject> myIter=childCandidate.getInterfaceIterator();
					while(myIter.hasNext()) {
						//
						if(myIter.next().getClassType().equals(targetClass.getName())) {
							childOfTargetNum++;
							break;
						}
					}
					if(childOfTargetNum >= 2)
						break;
				}
				if(childOfTargetNum < 2) {
					classObjectswithSG.add(targetClass);
				}
			}
		}
		
		ClassObject[] res = new ClassObject[classObjectswithSG.size()];
		for(int i=0;i<classObjectswithSG.size();i++) {
			res[i]=classObjectswithSG.get(i);
		}
		return res;
	}
}
	
