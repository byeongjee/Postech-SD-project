package gr.uom.java.jdeodorant.refactoring.views;

import org.eclipse.jface.wizard.Wizard;

public class LPLRefactorWizard extends Wizard {
	protected LPLRefactorInitialPage page;
	
	public LPLRefactorWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public String getWindowTitle() {
		return "Refactoring!!";
	}
	
	@Override
	public void addPages() {
		page = new LPLRefactorInitialPage();
		addPage(page);
	}
	
	@Override
	public boolean performFinish() {
		System.out.println("Finish");
		return true;
	}
}
