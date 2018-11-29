package junittest;

import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.jface.text.Position;
//import org.junit.jupiter.api.Test;
import org.junit.Test;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import gr.uom.java.ast.ClassObjectCandidate;
import gr.uom.java.ast.ConstructorObject;
import gr.uom.java.ast.MethodObject;
import gr.uom.java.jdeodorant.refactoring.views.MessageChain;
import gr.uom.java.jdeodorant.refactoring.views.MessageChain.ViewContentProvider;
import gr.uom.java.jdeodorant.refactoring.views.MessageChainStructure;

public class MessageChainUnitTest {
	public ViewContentProvider makeViewContentProvider() {
		MessageChain msgChain = new MessageChain();
		return msgChain.new ViewContentProvider();
	}
	
	@Test
    public void testgetChildren() {
		MessageChainStructure parent = new MessageChainStructure("ParentClass");
		MessageChainStructure child = new MessageChainStructure(15, parent, "A().B().C()");
		assertTrue(parent.addChild(child));
		
		Object[] result = makeViewContentProvider().getChildren(parent);
		
		Object[] result2 = makeViewContentProvider().getChildren(null);
		Object[] result3 = makeViewContentProvider().getChildren("String");
		
	    assertTrue(((MessageChainStructure) result[0]).getName()=="A().B().C()");
	    assertTrue(((MessageChainStructure) result[0]).getStart()==15);
	    assertTrue(result2.length == 0);
	    assertTrue(result3.length == 0);
	}
	
	@Test
	public void testgetParent() {
		MessageChainStructure parent = new MessageChainStructure("ParentClass");
		MessageChainStructure child = new MessageChainStructure(15, parent, "A().B().C()");
		assertTrue(parent.addChild(child));
		
		Object result = makeViewContentProvider().getParent(child);
		
	    assertTrue(((MessageChainStructure) result).getName()=="ParentClass");
	    assertTrue(((MessageChainStructure) result).getStart()==-1);
	}
	
	@Test
	public void testhasChildren() {
		MessageChainStructure parent = new MessageChainStructure("ParentClass");
		MessageChainStructure child = new MessageChainStructure(15, parent, "A().B().C()", 4);
		assertTrue(parent.addChild(child));
		
		boolean resultTrue = makeViewContentProvider().hasChildren(parent);
		boolean resultFalse = makeViewContentProvider().hasChildren(child);
		Object[] children = makeViewContentProvider().getChildren(parent);
		int length = ((MessageChainStructure)children[0]).getLength();
		parent.removeChild(child);
		int size = parent.getSize();
	    assertTrue(resultTrue);
	    assertFalse(resultFalse);
	    assertTrue(length == 4);
	    assertTrue(size == 0);
	}
	
	@Test
	public void testgetElement() {
		MessageChainStructure parent = new MessageChainStructure("ParentClass");
		MessageChainStructure child = new MessageChainStructure(15, parent, "A().B().C()");
		assertTrue(parent.addChild(child));
		MessageChainStructure[] target = new MessageChainStructure[1];
		target[0] = parent;
		MessageChain msgChain = new MessageChain();
		ViewContentProvider contentProvider =msgChain.new ViewContentProvider();
		msgChain.targets = target;
		Object[] result = contentProvider.getElements(parent);
		
		assertTrue(((MessageChainStructure) result[0]).getName()=="ParentClass");
		assertTrue(((MessageChainStructure) result[0]).getStart()==-1);
	}

	
	@Test
	public void testgetHighlightPositions() {
		ClassObjectCandidate testcoc =  new ClassObjectCandidate();
		testcoc.setstart(11);
		testcoc.setlength(22);
		Object testObjectList[] = testcoc.getHighlightPositions();
		Map<Position, String> annotationMaps = (Map<Position, String>)testObjectList[0];
		Iterator<Position> annotationIterator = annotationMaps.keySet().iterator();
		Position testPosition=annotationIterator.next();
		assertTrue(testPosition.getLength()==22);
		assertTrue(testPosition.getOffset()==11);

		
		MethodObject testmo = new MethodObject(new ConstructorObject());
		testmo.setstart(6);
		testmo.setlength(12);
		Object testMethodObjectList[]=testmo.getHighlightPositions();
		annotationMaps = (Map<Position,String>)testMethodObjectList[0];
		annotationIterator = annotationMaps.keySet().iterator();
		testPosition=annotationIterator.next();
		assertTrue(testPosition.getLength()==12);
		assertTrue(testPosition.getOffset()==6);
	}

	
	
}
