package junittest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

import gr.uom.java.ast.LPLMethodObject;
import gr.uom.java.ast.MethodObject;

public class LongParameterListTest {
	private static MethodObject createMockMethodObject1() {
		return null;
	}

	private static LPLMethodObject createMockLPLMethodObject1() {
		MethodObject mockMethodObject = createMockMethodObject1();
		return LPLMethodObject.createLPLMethodObjectFrom(mockMethodObject);
	}

	private static MethodObject createMockMethodObject2() {
		return null;
	}

	private static LPLMethodObject createMockLPLMethodObject2() {
		MethodObject mockMethodObject = createMockMethodObject2();
		return LPLMethodObject.createLPLMethodObjectFrom(mockMethodObject);
	}

	static LPLMethodObject mockLPLMethodObject1;
	static LPLMethodObject mockLPLMethodObject2;

	@BeforeClass
	public static void setUpBeforeClass() {
		mockLPLMethodObject1 = createMockLPLMethodObject1();
		mockLPLMethodObject2 = createMockLPLMethodObject2();
	}

	@Test
	public void testGetColumnText() {
		assertEquals(mockLPLMethodObject1.getColumnText(0), "Long Parameter List");
		assertEquals(mockLPLMethodObject1.getColumnText(1), "testMethod1");
		assertEquals(mockLPLMethodObject1.getColumnText(2), "testClass");
		assertEquals(mockLPLMethodObject1.getColumnText(3), "int a, char b");
		assertEquals(mockLPLMethodObject1.getColumnText(4), "4");

	}

	@Test
	public void testCompareTo() {
		assertTrue(mockLPLMethodObject1.compareTo(mockLPLMethodObject2) < 0);
	}

	@Test
	public void testIsLongParameterListMethod() {
		assertFalse(mockLPLMethodObject1.isLongParamterListMethod());
		assertTrue(mockLPLMethodObject2.isLongParamterListMethod());
	}
}
