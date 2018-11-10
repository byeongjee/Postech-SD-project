package gr.uom.java.jdeodorant.refactoring.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import gr.uom.java.jdeodorant.refactoring.Activator;

public class RefactoringButtonUI {
	private Tree tree;
	private List<Button> buttonList;
	
	public RefactoringButtonUI(){
		buttonList = new ArrayList<Button>();
	}
	
	public List getButtonList() {
		return buttonList;
	}
	
	public Tree getTree() {
		return tree;
	}
	
	public void setTree(Tree ntree) {
		tree = ntree;
	}
	
	public void pressRefactorButton(int index) {
		//System.out.println(index);
		/**
		 * Implement function to run when high level refactoring button is pressed
		 */
		
		//Example : Print text of column 1 of pressed button
		System.out.println(tree.getItem(index).getText(1));
	}

	
	/**
	 * @UI
	 * Add refactoring buttons for top level smells
	 */
	public void makeRefactoringButtons() {
		TreeItem[] items = tree.getItems();
		for(int i = 0; i < items.length; i++) {			
			TreeItem item1 = items[i];
			TreeEditor editor = new TreeEditor(item1.getParent());
			Button button = new Button(item1.getParent(), SWT.PUSH);					
			button.setText("TEST");
			button.setSize(16, 16);
			button.pack();
			button.setData("index", i);
			
			editor.horizontalAlignment = SWT.RIGHT;
			editor.grabHorizontal = true;
			editor.minimumWidth = 50;
			editor.setEditor(button, item1, 6);
			buttonList.add(button);
			button.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent event) {
					pressRefactorButton((Integer) event.widget.getData("index"));
				}
				
				public void widgetDefaultSelected(SelectionEvent event) {
				}
			});
			
		}
	}
	
	
	public void makeChildrenRefactoringButtons() {
		TreeItem[] items = tree.getItems();
		for(int i = 0; i < items.length; i++) {
			TreeEditor editor = new TreeEditor(tree);
			
			TreeItem item1 = items[i];
						
			for(int j = 0; j < item1.getItems().length; j++) {
				TreeEditor editor2 = new TreeEditor(item1.getItem(j).getParent());
				Button button = new Button(item1.getItem(j).getParent(), SWT.PUSH);	
				Image image = Activator.getImageDescriptor("/icons/green_button.png").createImage();
	  
				button.addPaintListener( new PaintListener() {
					  //@Override
					  public void paintControl( PaintEvent event ) {
						  event.gc.setBackground( event.display.getSystemColor( SWT.COLOR_WHITE ) );
						  event.gc.fillRectangle( event.x, event.y, event.width, event.height );
						  Image image = Activator.getImageDescriptor("/icons/green_button.png").createImage();;
						  //
						  event.gc.drawImage( image, event.width/2-8, event.height/2-8 );
					  }
				});
				
				//Image image = new Image(display, "yourFile.gif");
				//Image image = PaintEvent.event.display.getSystemImage( SWT.ICON_QUESTION );
				//button.setText("TEST");
				button.setSize(3, 3);
				button.setImage(image);
				button.pack();
				
				editor2.horizontalAlignment = SWT.RIGHT;
				editor2.grabHorizontal = true;
				editor2.minimumWidth = 50;
				editor2.setEditor(button, item1.getItem(j), 6);
				buttonList.add(button);
			}
		}
	}
	
}
