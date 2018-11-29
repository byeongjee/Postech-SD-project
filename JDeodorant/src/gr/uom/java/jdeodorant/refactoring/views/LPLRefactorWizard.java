package gr.uom.java.jdeodorant.refactoring.views;

import java.util.ArrayList;

import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jface.wizard.Wizard;

import gr.uom.java.ast.LPLMethodObject;

public class LPLRefactorWizard extends Wizard {
	private IJavaProject javaProject;
	private LPLMethodObject methodToRefactor;
	private LPLRefactorInitialPage initialPage;
	private LPLRefactorClassNamePage namePage;
	private LPLRefactorSelectPackagePage packagePage;
	
	public LPLRefactorWizard(IJavaProject javaProject, LPLMethodObject methodToRefactor) {
		super();
		setNeedsProgressMonitor(true);
		this.javaProject = javaProject;
		this.methodToRefactor = methodToRefactor;
	}
	
	@Override
	public String getWindowTitle() {
		return "Refactoring";
	}
	
	@Override
	public void addPages() {
		initialPage = new LPLRefactorInitialPage(methodToRefactor);
		namePage = new LPLRefactorClassNamePage();
		packagePage = new LPLRefactorSelectPackagePage(javaProject);
		addPage(initialPage);
		addPage(namePage);
		addPage(packagePage);
	}
	
	@Override
	public boolean performFinish() {
		System.out.println("Finish");
		System.out.println(initialPage.getParameterIndexList());
		try {
			IMethod convertedIMethod = methodToRefactor.toIMethod(javaProject);
			ICompilationUnit workingCopy = convertedIMethod.getCompilationUnit()
					.getWorkingCopy(new WorkingCopyOwner() {
					}, null);
			IBuffer buffer = ((IOpenable) workingCopy).getBuffer();
			editParameterFromBuffer(buffer, convertedIMethod, "", initialPage.getParameterIndexList());
			workingCopy.reconcile(ICompilationUnit.NO_AST, false, null, null);
			workingCopy.commitWorkingCopy(false, null);
			workingCopy.discardWorkingCopy();
			workingCopy.discardWorkingCopy();
		} catch (Exception e) {
		}
		return true;
	}
	
	public static void editParameterFromBuffer(IBuffer buffer, IMethod method, String parameterString, ArrayList<Integer> parameterIndexList) {
		try {
			IMethod convertedIMethod = method;
			
			int startPosition = convertedIMethod.getSourceRange().getOffset();
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
			for(int it : parameterIndexList) {
				argumentParts[it] = null;
			}
			String refactoredArgumentString = "";
			for(String s : argumentParts) {
				if(s != null) {
					refactoredArgumentString += s;
					refactoredArgumentString += ",";
				}
			}
			refactoredArgumentString = refactoredArgumentString.substring(0, refactoredArgumentString.length() - 1);
			String replaceSignature = "(";
			replaceSignature += refactoredArgumentString;
			replaceSignature += ")";
			
			System.out.println(refactoredArgumentString);
			
			buffer.replace(startPosition, endPosition - startPosition + 1, replaceSignature);
		} catch (Exception e) {
				e.printStackTrace();
		}
	}
}
