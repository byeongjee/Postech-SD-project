package gr.uom.java.jdeodorant.refactoring.views;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.wizard.Wizard;

import gr.uom.java.ast.LPLMethodObject;

public class LPLRefactorWizard extends Wizard {
	private IJavaProject javaProject;
	private LPLMethodObject methodToRefactor;
	private LPLRefactorInitialPage page;
	
	public LPLRefactorWizard(IJavaProject javaProject, LPLMethodObject methodToRefactor) {
		super();
		setNeedsProgressMonitor(true);
		this.javaProject = javaProject;
		this.methodToRefactor = methodToRefactor;
	}
	
	@Override
	public String getWindowTitle() {
		return "Refactoring!!";
	}
	
	@Override
	public void addPages() {
		page = new LPLRefactorInitialPage(methodToRefactor);
		addPage(page);
	}
	
	@Override
	public boolean performFinish() {
		System.out.println("Finish");
		return true;
	}
}
