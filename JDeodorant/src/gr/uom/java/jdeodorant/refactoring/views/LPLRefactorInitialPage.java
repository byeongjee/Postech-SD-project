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

import gr.uom.java.ast.LPLMethodObject;

public class LPLRefactorInitialPage extends WizardPage {
	private ArrayList<Integer> parameterIndexList;
    private Composite container;
    private LPLMethodObject methodToRefactor;
    private TableViewer tableViewer;
    private ArrayList<String> extractParameterNames;
    private ArrayList<String> extractParameterTypes;

    public LPLRefactorInitialPage(LPLMethodObject methodToRefactor) {
        super("First Page");
        this.methodToRefactor = methodToRefactor;
        setTitle(methodToRefactor.getName());
        setDescription("Select parameters to extract");
        extractParameterNames = new ArrayList<String>();
        extractParameterTypes = new ArrayList<String>();
        parameterIndexList = new ArrayList<Integer>();
    }

    //@Override
    public void createControl(Composite parent) {
    	container = new Composite(parent, SWT.NONE);
    	container.setLayout(new FillLayout());
    	
    	Table table = new Table(container, SWT.BORDER | SWT.CHECK | SWT.H_SCROLL);
    	table.setHeaderVisible(true);
    	TableColumn checkColumn = new TableColumn(table, SWT.CENTER);
    	checkColumn.setText("Select");
    	checkColumn.setWidth(80);
    	checkColumn.setAlignment(SWT.CENTER);
    	TableColumn tableTypeColumn = new TableColumn(table, SWT.LEFT);
		tableTypeColumn.setText("Type");
		tableTypeColumn.setWidth(200);
		TableColumn tableNameColumn = new TableColumn(table, SWT.LEFT);
		tableNameColumn.setText("Name");
		tableNameColumn.setWidth(200);
		
		for(int i = 0; i < methodToRefactor.getParameterTypeList().size(); i++) {
			TableItem tableItem = new TableItem(table, SWT.NONE);
			tableItem.setText(1, methodToRefactor.getParameterTypeList().get(i).toString());
			tableItem.setText(2, methodToRefactor.getParameterNameList().get(i));
		}
		
		table.addListener(SWT.Selection,  new Listener() {
			public void handleEvent(Event event) {
				boolean isChecked = false;
				if(event.detail == SWT.CHECK) {
					parameterIndexList.clear();
					extractParameterTypes.clear();
					extractParameterNames.clear();
					for(int i = 0; i < ((Table)event.widget).getItems().length; i++) {
						TableItem item = ((Table)event.widget).getItem(i);
						if(item.getChecked()) {
							parameterIndexList.add(i);
							extractParameterTypes.add(((Table)event.widget).getItem(i).getText(1));
							extractParameterNames.add(((Table)event.widget).getItem(i).getText(2));
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
		});
		setControl(container);
        setPageComplete(false);
    }
    
    public void handleEvent(Event event) {
    	System.out.println("checkbox!");
    }

    public ArrayList getParameterIndexList() {
        return parameterIndexList;
    }
    
    public ArrayList getExtractParameterNames() {
    	return extractParameterNames;
    }
    
    public ArrayList getExtractParameterTypes() {
    	return extractParameterTypes;
    }

}
