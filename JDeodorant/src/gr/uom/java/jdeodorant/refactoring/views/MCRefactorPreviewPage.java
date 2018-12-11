package gr.uom.java.jdeodorant.refactoring.views;

import javax.lang.model.SourceVersion;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import gr.uom.java.ast.ClassObjectCandidate;
import gr.uom.java.ast.LPLMethodObject;

/**
 * Preview page of refactoring pop up wizard for Long Parameter List
 * @author iju-yong
 *
 */
public class MCRefactorPreviewPage extends WizardPage {
	private Text text;
    private Composite container;
    
    private String originalSource;
    private String refactoredSource;
    
    public MCRefactorPreviewPage(String origin, String refactor) {
        super("Preview Page");
        setTitle("Message Chain");
        
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

