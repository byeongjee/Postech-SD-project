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
			//List<IPackageFragment> ret = new ArrayList<>();
			for(IPackageFragment myPackage : allPkg) {
				if(myPackage.getKind() == IPackageFragmentRoot.K_SOURCE && myPackage.getCompilationUnits().length != 0) {
					TableItem tableItem = new TableItem(table, SWT.NONE);
					tableItem.setText(1, myPackage.getElementName());
					//ret.add(myPackage);
				}
			}
		} catch (Exception e) {
		}
		/*table.addListener(SWT.Selection,  new Listener() {
			public void handleEvent(Event event) {
				boolean isChecked = false;
				if(event.detail == SWT.CHECK) {
					parameterIndexList.clear();
					for(int i = 0; i < ((Table)event.widget).getItems().length; i++) {
						TableItem item = ((Table)event.widget).getItem(i);
						if(item.getChecked()) {
							parameterIndexList.add(i);
							isChecked = true;
						}
					}
					if(isChecked) {
						setPageComplete(true);
					}
					else {
					setPageComplete(false);
					}
				}
			}
		});*/
		GridData tableGD = new GridData(GridData.FILL_HORIZONTAL);
        table.setLayoutData(tableGD);
		
		Label emptyLabel = new Label(container, SWT.NONE);
		//setControl(container);
        
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
