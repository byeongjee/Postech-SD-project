package gr.uom.java.jdeodorant.refactoring.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
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

public class LPLRefactorSelectPackagePage extends WizardPage {
	private Text newPackageText;
    private Composite container;
    private IJavaProject javaProject;

    public LPLRefactorSelectPackagePage(IJavaProject javaProject) {
        super("Select package of new class");
        setTitle("Select package");
        setDescription("Select package of new class, or enter new package name");
        this.javaProject = javaProject;
    }

    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);
        layout.numColumns = 2;
        
        Table table = new Table(container, SWT.BORDER | SWT.CHECK | SWT.H_SCROLL);
    	table.setHeaderVisible(true);
    	TableColumn checkColumn = new TableColumn(table, SWT.CENTER);
    	checkColumn.setText("Select");
    	checkColumn.setWidth(80);
    	checkColumn.setAlignment(SWT.CENTER);
    	TableColumn packageNameColumn = new TableColumn(table, SWT.LEFT);
		packageNameColumn.setText("Package name");
		packageNameColumn.setWidth(800);
		
		try {
			IPackageFragment[] allPkg = javaProject.getPackageFragments();
			for(IPackageFragment myPackage : allPkg) {
				if(myPackage.getKind() == IPackageFragmentRoot.K_SOURCE && myPackage.getCompilationUnits().length != 0) {
					TableItem tableItem = new TableItem(table, SWT.NONE);
					tableItem.setText(1, myPackage.getElementName());
				}
			}
		} catch (Exception e) {
		}
		
		GridData tableGD = new GridData(GridData.FILL_HORIZONTAL);
        table.setLayoutData(tableGD);
		
		Label emptyLabel = new Label(container, SWT.NONE);
        
        Label label1 = new Label(container, SWT.NONE);
        label1.setText("New package name");

        newPackageText = new Text(container, SWT.BORDER | SWT.SINGLE);
        newPackageText.setText("");
        newPackageText.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
                if (!newPackageText.getText().isEmpty()) {
                    setPageComplete(true);
                    return;
                }
                setPageComplete(false);
            }

        });
        GridData textGD = new GridData(GridData.FILL_HORIZONTAL);
        newPackageText.setLayoutData(textGD);
        setControl(container);
        setPageComplete(true);

    }

    public String getNewPackageName() {
        return newPackageText.getText();
    }

}
