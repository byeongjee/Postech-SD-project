package gr.uom.java.jdeodorant.refactoring.views;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.wizard.Wizard;

public class SameLPLParametersWizard extends Wizard {
	private IMethod candidateMethod;
	private SameLPLParametersAssertionPage assertionPage;
	private boolean doExtraction;

	/**
	 * Constructor for wizard
	 * @param candidateMethod IMethod of method to refactor
	 */
	public SameLPLParametersWizard(IMethod candidateMethod) {
		super();
		setNeedsProgressMonitor(true);
		this.candidateMethod = candidateMethod;  
		doExtraction = false;
	}
	
	/**
	 * Set title of popup.
	 */
	@Override
	public String getWindowTitle() {
		return "Same parameters found";
	}
	
	/**
	 * Add a single page asking if the user wants to refactor this method
	 */
	@Override
	public void addPages() {
		assertionPage = new SameLPLParametersAssertionPage(candidateMethod);
		addPage(assertionPage);
	}
	
	/**
	 * If the finish button(labeled "Yes") is clicked, doExtraction is set to true.
	 */
	@Override
	public boolean performFinish() {
		doExtraction = true;
		return true;
	}
	
	/**
	 * Returns doExtraction
	 * @return
	 */
	public boolean getDoExtraction() {
		return this.doExtraction;
	}
}
