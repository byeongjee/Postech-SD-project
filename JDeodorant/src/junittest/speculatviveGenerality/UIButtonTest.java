package junittest.speculatviveGenerality;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.eclipse.jface.text.Position;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.junit.jupiter.api.BeforeEach;

import gr.uom.java.ast.ClassObjectCandidate;
import gr.uom.java.ast.ConstructorObject;
import gr.uom.java.ast.MethodObject;
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
		assertEquals(3, ui.getChildrenButtonList().size());
		assertEquals(2, ((List) (ui.getChildrenButtonList()).get(0)).size());
		assertEquals(2, ((List) (ui.getChildrenButtonList()).get(1)).size());
	}
	
	
	@Test
	public void testParentButtonData() {
		setup();
		ui.makeRefactoringButtons(6);
		assertEquals(2, ((Button) ui.getButtonList().get(2)).getData("index"));
	}
	
	@Test
	public void testChildButtonData() {
		setup();
		ui.makeChildrenRefactoringButtons(6);
		assertEquals(0, ((Button) ((List) ui.getChildrenButtonList().get(0)).get(0)).getData("parentIndex"));
		assertEquals(0, ((Button) ((List) ui.getChildrenButtonList().get(0)).get(0)).getData("childIndex"));
		assertEquals(1, ((Button) ((List) ui.getChildrenButtonList().get(1)).get(0)).getData("parentIndex"));
		assertEquals(0, ((Button) ((List) ui.getChildrenButtonList().get(1)).get(0)).getData("childIndex"));
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

	@Test
	public void testgetHighlightPositions() {
		ClassObjectCandidate testcoc =  new ClassObjectCandidate();
		testcoc.setStart(11);
		testcoc.setLength(22);
		Object testObjectList[] = testcoc.getHighlightPositions();
		Map<Position, String> annotationMaps = (Map<Position, String>)testObjectList[0];
		Iterator<Position> annotationIterator = annotationMaps.keySet().iterator();
		Position testPosition=annotationIterator.next();
		assertTrue(testPosition.getLength()==22);
		assertTrue(testPosition.getOffset()==11);
		
		
		MethodObject testmo = new MethodObject(new ConstructorObject());
		testmo.setSmellStart(6);
		testmo.setSmellLength(12);
		Object testMethodObjectList[]=testmo.getHighlightPositions();
		annotationMaps = (Map<Position,String>)testMethodObjectList[0];
		annotationIterator = annotationMaps.keySet().iterator();
		testPosition=annotationIterator.next();
		assertTrue(testPosition.getLength()==12);
		assertTrue(testPosition.getOffset()==6);
	}
	
	@Test
	public void testNavigationScrollBar()
	{
		ClassObjectCandidate testcoc =  new ClassObjectCandidate();
		testcoc.setStart(11);
		testcoc.setLength(22);
		Object testObjectList[] = testcoc.getHighlightPositions();
		Map<Position, String> annotationMaps = (Map<Position, String>)testObjectList[0];
		String answer = "This smell has Speculative Generality in Class ";
		assertEquals(answer,annotationMaps.get(new Position(11,22)));
	}


}
