package junittest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import gr.uom.java.ast.ConstructorObject;
import gr.uom.java.ast.LPLMethodObject;
import gr.uom.java.ast.MethodObject;
import gr.uom.java.ast.TypeObject;

public class LongParameterListTest {
	private static MethodObject createMockMethodObject1() {
		ConstructorObject co = new ConstructorObject() {
			@Override
			public String getName(){
				return "testMethod1";
			}
			@Override
			public String getClassName() {
				return "testClass";
			}
			@Override
			public List<String> getParameterList() {
				List<String> parameterList = new ArrayList<String>();
				parameterList.add("a");
				parameterList.add("b");
				return parameterList;
			}
			@Override
			public List<TypeObject> getParameterTypeList() {
				List<TypeObject> parameterTypeList = new ArrayList<TypeObject>();
				parameterTypeList.add(new TypeObject("int"));
				parameterTypeList.add(new TypeObject("char"));
				return parameterTypeList;
			}
		};
		return new MethodObject(co);
	}

	private static LPLMethodObject createMockLPLMethodObject1() {
		MethodObject mockMethodObject = createMockMethodObject1();
		return LPLMethodObject.createLPLMethodObjectFrom(mockMethodObject);
	}

	private static MethodObject createMockMethodObject2() {
			ConstructorObject co = new ConstructorObject() {
				@Override
				public String getName(){
					return "testMethod2";
				}
				@Override
				public String getClassName() {
					return "testClass";
				}
				@Override
				public List<String> getParameterList() {
					List<String> parameterList = new ArrayList<String>();
					parameterList.add("a");
					parameterList.add("b");
					parameterList.add("c");
					parameterList.add("d");
					return parameterList;
				}
				@Override
				public List<TypeObject> getParameterTypeList() {
					List<TypeObject> parameterTypeList = new ArrayList<TypeObject>();
					parameterTypeList.add(new TypeObject("int"));
					parameterTypeList.add(new TypeObject("char"));
					parameterTypeList.add(new TypeObject("String"));
					parameterTypeList.add(new TypeObject("boolean"));
					return parameterTypeList;
				}
			};
			return new MethodObject(co);
	}

	private static LPLMethodObject createMockLPLMethodObject2() {
		MethodObject mockMethodObject = createMockMethodObject2();
		return LPLMethodObject.createLPLMethodObjectFrom(mockMethodObject);
	}

	static LPLMethodObject mockLPLMethodObject1;
	static LPLMethodObject mockLPLMethodObject2;

	@BeforeAll
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
		assertEquals(mockLPLMethodObject1.getColumnText(4), "2");

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
