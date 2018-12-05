package gr.uom.java.jdeodorant.refactoring.views;

import javax.lang.model.SourceVersion;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class LPLRefactorClassNamePage extends WizardPage {
	private Text newClassName;
	private Text newParameterName;
    private Composite container;

    public LPLRefactorClassNamePage() {
        super("New Class Name");
        setTitle("New Class Name");
        setDescription("Enter name of new class");
    }

    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 2;
        Label label1 = new Label(container, SWT.NONE);
        label1.setText("New class name");

        newClassName = new Text(container, SWT.BORDER | SWT.SINGLE);
        newClassName.setText("");
        newClassName.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
            	if(isValidName(newClassName.getText()) && isValidName(newParameterName.getText())) {
            		setPageComplete(true);
            		return;
            	}
                setPageComplete(false);
            }

        });
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        newClassName.setLayoutData(gd);
        
        Label newParameterLabel = new Label(container, SWT.NONE);
        newParameterLabel.setText("New parameter name");

        newParameterName = new Text(container, SWT.BORDER | SWT.SINGLE);
        newParameterName.setText("");
        newParameterName.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
            	if(isValidName(newClassName.getText()) && isValidName(newParameterName.getText())) {
            		setPageComplete(true);
            		return;
            	}
                setPageComplete(false);
            }

        });
        GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
        newParameterName.setLayoutData(gd2);
        
        
        setControl(container);
        setPageComplete(false);

    }

    public String getClassName() {
        return newClassName.getText();
    }
    
    public String getParameterName() {
    	return newParameterName.getText();
    }
    
    private boolean isValidName(String name) {
    	return SourceVersion.isIdentifier(name) && !SourceVersion.isKeyword(name);
    }
}
