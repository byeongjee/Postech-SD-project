package gr.uom.java.jdeodorant.refactoring.views;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import gr.uom.java.ast.ClassObjectCandidate;

/**
 * initial page of refactoring pop up wizard for speculative generality
 * @author Jaeyeop Lee, Taeyoung Son(referred from LPL Team)
 *
 */
public class SGRefactorPreviewPage extends WizardPage {
	private Text text;
    private Composite container;
    
    private String originalSource = "<Original Source>";
    private String refactoredSource = "<Refactored Source>";

    public SGRefactorPreviewPage(ClassObjectCandidate classToRefactor, String origin, String refactor) {
        super("First Page");
        setTitle("Speculative Generality");
        
        originalSource = origin;
        refactoredSource = refactor;
    }

    //@Override
    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);
    	container.setLayout(new FillLayout());
    	
        Table table = new Table(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    	table.setHeaderVisible(true);
    	TableColumn originalColumn = new TableColumn(table, SWT.LEFT);
    	originalColumn.setText("Original Source");
    	originalColumn.setWidth(500);
    	TableColumn refactoredColumn = new TableColumn(table, SWT.LEFT);
    	refactoredColumn.setText("Refactored Source");
    	refactoredColumn.setWidth(500);
    	
    	TableItem tableItem = new TableItem(table, SWT.NONE);
		tableItem.setText(0, originalSource);
		tableItem.setText(1, refactoredSource);

		setControl(container);
        setPageComplete(true);
    }

    public String getText() {
        return text.getText();
    }
}
