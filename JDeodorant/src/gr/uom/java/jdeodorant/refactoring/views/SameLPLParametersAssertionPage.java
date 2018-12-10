package gr.uom.java.jdeodorant.refactoring.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
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

public class SameLPLParametersAssertionPage extends WizardPage {
	private ArrayList<Integer> parameterIndexList;
    private Composite container;
    private ArrayList<String> extractParameterNames;
    private ArrayList<String> extractParameterTypes;
    private IMethod candidateMethod;

    public SameLPLParametersAssertionPage(IMethod candidateMethod) {
        super("Same Parameters Found");
        this.candidateMethod = candidateMethod;
        setTitle(candidateMethod.getElementName() + " has similar parameters.");
        setDescription("Would you also like to extract these parameters?");
        extractParameterNames = new ArrayList<String>();
        extractParameterTypes = new ArrayList<String>();
        parameterIndexList = new ArrayList<Integer>();
    }

    public void createControl(Composite parent) {
    	container = new Composite(parent, SWT.NONE);
    	container.setLayout(new FillLayout());
    	
    	Table table = new Table(container, SWT.BORDER | SWT.H_SCROLL);
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
		ArrayList<String> parameterTypeList = new ArrayList<String>();
		
		parameterTypeList = getSourceCodeParameterList(candidateMethod);
		
		for(int i = 0; i < parameterTypeList.size(); i++) {
			TableItem tableItem = new TableItem(table, SWT.NONE);
			String parameter = parameterTypeList.get(i);
			tableItem.setText(1, parameter.split(" ")[0]);
			tableItem.setText(2, parameter.split(" ")[1]);
		}
		
		setControl(container);
        setPageComplete(true);
    }
    
    public ArrayList<String> getSourceCodeParameterList(IMethod method) {
    	try {
			IMethod convertedIMethod = method;
			int startPosition = convertedIMethod.getSourceRange().getOffset();
			IBuffer buffer = convertedIMethod.getCompilationUnit().getBuffer();
			while (true) {
				if (buffer.getChar(startPosition) != '(') {
					startPosition += 1;
					continue;
				}
				break;
			}
			int numOfLeftPar = 0;
			int endPosition = startPosition;
			while (true) {
				if (buffer.getChar(endPosition) == '(') {
					numOfLeftPar += 1;
				} 
				else if (buffer.getChar(endPosition) == ')') {
					if (numOfLeftPar == 1)
						break;
					else
						numOfLeftPar -= 1;
				}
				endPosition += 1;
			}
			String argumentString = buffer.getContents().substring(startPosition + 1, endPosition);
			String argumentParts[] = argumentString.split(",");
			for(int i = 0; i < argumentParts.length; i++) {
				argumentParts[i] = argumentParts[i].trim();
			}
			ArrayList<String> ret = new ArrayList<String>();
			for(String s : argumentParts) {
				ret.add(s);
			}
			return ret;
		} catch (Exception e) {
				e.printStackTrace();
		}
    	return null;
    }

}
