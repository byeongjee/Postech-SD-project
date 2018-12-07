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
import org.eclipse.ui.plugin.AbstractUIPlugin;

import gr.uom.java.jdeodorant.refactoring.Activator;

public class RefactoringButtonUI {
	protected Tree tree;
	protected ArrayList<Button> buttonList;
	private ArrayList<List <Button>> childButtonList;
	
	private String PLUGIN_ID = "gr.uom.java.jdeodorant";
	
	public RefactoringButtonUI(){
		buttonList = new ArrayList<Button>();
		childButtonList = new ArrayList<List<Button>>();
	}
	
	public List getButtonList() {
		return buttonList;
	}
	
	public List getChildrenButtonList() {
		return childButtonList;
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
	 * 
	 * @param parentIndex index of parent smell
	 * @param childIndex index of child smell in parent smell
	 */
	public void pressChildRefactorButton(int parentIndex, int childIndex) {
		System.out.println("Child refactor button pressed");
		System.out.println("Parent index is " + parentIndex);
		System.out.println("Child index is " + childIndex);
	}

	
	/**
	 * @UI
	 * Add refactoring buttons for top level smells
	 */
	public void makeRefactoringButtons(int columnIndex) {
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
			editor.setEditor(button, item1, columnIndex);
			buttonList.add(button);
			button.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent event) {
					pressRefactorButton((Integer) event.widget.getData("index"));
				}

				public void widgetDefaultSelected(SelectionEvent event) {
				}
			});
			
			button.addPaintListener(new PaintListener() {
				public void paintControl( PaintEvent event ) {
					  event.gc.setBackground( event.display.getSystemColor( SWT.COLOR_WHITE ) );
					  event.gc.fillRectangle( event.x, event.y, event.width, event.height );
				}
			});
			
		}
	}
	
	
	public void makeChildrenRefactoringButtons(int columnIndex) {
		TreeItem[] items = tree.getItems();
		for(int i = 0; i < items.length; i++) {
			TreeEditor editor = new TreeEditor(tree);
			
			TreeItem item1 = items[i];
			
			childButtonList.add(new ArrayList<Button>());
						
			for(int j = 0; j < item1.getItems().length; j++) {
				TreeEditor editor2 = new TreeEditor(item1.getItem(j).getParent());
				Button button = new Button(item1.getItem(j).getParent(), SWT.PUSH);	
				button.setData("parentIndex", i);
				button.setData("childIndex", j);
	  
				button.addPaintListener( new PaintListener() {
					  //@Override
					  public void paintControl( PaintEvent event ) {
						  event.gc.setBackground( event.display.getSystemColor( SWT.COLOR_WHITE ) );
						  event.gc.fillRectangle( event.x, event.y, event.width, event.height );
						  Image image = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, "/icons/refactoring_button.png").createImage();
						  event.gc.drawImage( image, event.width/2-8, event.height/2-8 );
					  }
				});
				button.setText("Child");
				
				button.setSize(3, 3);
				button.pack();
				
				button.addSelectionListener(new SelectionListener() {
					public void widgetSelected(SelectionEvent event) {
						Integer parentIndex = (Integer) event.widget.getData("parentIndex");
						Integer childIndex = (Integer) event.widget.getData("childIndex");
						pressChildRefactorButton(parentIndex, childIndex);
					}
					
					public void widgetDefaultSelected(SelectionEvent event) {
					}
				});
				
				editor2.horizontalAlignment = SWT.RIGHT;
				editor2.grabHorizontal = true;
				editor2.minimumWidth = 50;
				editor2.setEditor(button, item1.getItem(j), columnIndex);
				childButtonList.get(i).add(button);
			}
		}
	}
	
	public void disposeButtons() {
		for(Button it : buttonList) {
			it.dispose();
		}
		buttonList.clear();
		for(List<Button> listIt : childButtonList) {
			for(Button it : listIt) {
				it.dispose();
			}
			listIt.clear();
		}
		childButtonList.clear();
	}
	
}
