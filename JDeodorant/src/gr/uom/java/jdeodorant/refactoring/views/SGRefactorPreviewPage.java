package gr.uom.java.jdeodorant.refactoring.views;


import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import gr.uom.java.ast.ClassObjectCandidate;

/**
 * Preview page of refactoring pop up wizard for speculative generality
 * @author Jaeyeop Lee, Taeyoung Son(referred from LPL Team)
 *
 */
public class SGRefactorPreviewPage extends WizardPage {
	private Text text;
    private Composite container;
    
    private String originalSource = "<Original Source>";
    private String refactoredSource = "<Refactored Source>";

    public SGRefactorPreviewPage(ClassObjectCandidate classToRefactor, String origin, String refactor) {
        super("Preview Page");
        setTitle("Speculative Generality");
        
        originalSource = origin;
        refactoredSource = refactor;
    }

    //@Override
    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(2, true));
        (new Label(container, SWT.NULL)).setText("Original Source");
        (new Label(container, SWT.NULL)).setText("Refactored Source");
        
        Text originalText = new Text(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        originalText.setText(originalSource);
        Text refactoredText = new Text(container, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        refactoredText.setText(refactoredSource);
        
        GridData gridData = new GridData(GridData.FILL_BOTH);
        originalText.setLayoutData(gridData);
        gridData = new GridData(GridData.FILL_BOTH);
        refactoredText.setLayoutData(gridData);
        setControl(container);
        setPageComplete(true);
    }

    public String getText() {
        return text.getText();
    }
}
