package gr.uom.java.jdeodorant.refactoring.views;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import gr.uom.java.ast.ClassObjectCandidate;

/**
 * initial page of refactoring pop up wizard for speculative generality
 * @author Jaeyeop Lee, Taeyoung Son(referred from LPL Team)
 *
 */
public class SGRefactorInitialPage extends WizardPage {
	private Text text;
    private Composite container;

    public SGRefactorInitialPage(ClassObjectCandidate classToRefactor) {
        super("First Page");
        setTitle("Speculative Generality");
    }

    //@Override
    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        
        Label label = new Label(container, SWT.NONE);
        label.setText("Press Finish button to refactor the speculative generality code smell.");
        
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        setControl(container);
        setPageComplete(true);
    }

    public String getText() {
        return text.getText();
    }

}
