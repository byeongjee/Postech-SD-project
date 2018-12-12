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
    private int currentCheckedIndex;
    private boolean canFinishPage;
    private String packageName;
    private Label existingWarningLabel;

    /**
     * Constructor for UI popup for selecting packages
     * @param javaProject
     */
    public LPLRefactorSelectPackagePage(IJavaProject javaProject) {
        super("Select package of new class");
        setTitle("Select package");
        setDescription("Select package of new class, or enter new package name");
        this.javaProject = javaProject;
        currentCheckedIndex = -1;
        canFinishPage = false;
    }

    /**
     * Makes a checkable table with the names of packages in project
     */
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
		
		table.addListener(SWT.Selection,  new Listener() {
			public void handleEvent(Event event) {
				boolean isChecked = false;
				if(event.detail == SWT.CHECK) {
					for(int i = 0; i < ((Table)event.widget).getItems().length; i++) {
						TableItem item = ((Table)event.widget).getItem(i);
						if(item.getChecked()) {
							if(currentCheckedIndex == -1) {
								currentCheckedIndex = i;
								packageName = ((Table)event.widget).getItem(currentCheckedIndex).getText(1);
							}
							else if(currentCheckedIndex != i) {
								((Table)event.widget).getItem(currentCheckedIndex).setChecked(false);
								currentCheckedIndex = i;
								packageName = ((Table)event.widget).getItem(currentCheckedIndex).getText(1);
							}
							isChecked = true;
						}
					}
					if(isChecked) {
						canFinishPage = true;
						setPageComplete(true);
					} else {
						currentCheckedIndex = -1;
						canFinishPage = false;
		                setPageComplete(false);
					}
				}
			}
		});
		
		GridData tableGD = new GridData(GridData.FILL_HORIZONTAL);
		tableGD.horizontalSpan = 2;
        table.setLayoutData(tableGD);
        
        
        existingWarningLabel = new Label(container, SWT.NONE);
        existingWarningLabel.setText("");
        GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
        gd2.horizontalSpan = 2;
        existingWarningLabel.setLayoutData(gd2);

        setControl(container);
        setPageComplete(false);

    }

    /**
     * returns the new package name that is input by the user
     * @return
     */
    public String getPackageName() {
    	return packageName;
    }
    
    /**
     * Returns the canFinishPage private variable
     * @return
     */
    public boolean getCanFinishPage() {
    	return canFinishPage;
    }
    
    /**
     * Sets the warning label if class name with same name exists.
     * @param set if true, set warning label
     */
    public void setExistingWarningLabel(boolean set) {
    	if(set) {
    		existingWarningLabel.setText("* Class with same name already exists!");
    	} else {
    		existingWarningLabel.setText("");
    	}
    }

}
