package junittest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.junit.jupiter.api.BeforeEach;

import gr.uom.java.jdeodorant.refactoring.views.RefactoringButtonUI;

public class UIButtonTest {

	private Tree tree;
	private RefactoringButtonUI ui;
	
	@Test
	public void RefactoringButtonUIConstructorTest() {
		setup();
		assertEquals(0, ui.getButtonList().size());
	}
	
	@Test
	public void GetTreeMethodTest() {
		setup();
		Tree testTree = ui.getTree();
		
		assertEquals(3, testTree.getItems().length);
		assertEquals(2, testTree.getItem(0).getItems().length);
		assertEquals(2, testTree.getItem(1).getItems().length);
	}
	
	
	@Test
	public void MakeRefactoringButtonsMethodTest() {
		setup();
		ui.makeRefactoringButtons(6);
		assertEquals(3, ui.getButtonList().size());
	}
	
	@Test
	public void MakeChildrenRefactoringButtonsMethodTest() {
		setup();
		ui.makeChildrenRefactoringButtons(6);
		assertEquals(4, ui.getButtonList().size());
		
	}
	
	public void setup() {
		tree = new Tree(new Shell(), SWT.SINGLE);
		TreeItem child1 = new TreeItem(tree, SWT.NONE, 0);
		child1.setText("1");
		TreeItem child2 = new TreeItem(tree, SWT.NONE, 1);
		child2.setText("2");
		TreeItem child3 = new TreeItem(tree, SWT.NONE, 2);
		child3.setText("3");
		
		TreeItem child4 = new TreeItem(child1, SWT.NONE, 0);
		child4.setText("4");
		TreeItem child5 = new TreeItem(child1, SWT.NONE, 1);
		child5.setText("5");

		TreeItem child6 = new TreeItem(child2, SWT.NONE, 0);
		child6.setText("6");
		TreeItem child7 = new TreeItem(child2, SWT.NONE, 1);
		child7.setText("7");
		
		ui = new RefactoringButtonUI();
		ui.setTree(tree);
	}

	
}
