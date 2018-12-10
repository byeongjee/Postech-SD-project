package junittest.longParameterList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import gr.uom.java.ast.LPLMethodObject;
import gr.uom.java.ast.LPLSmellContent;

public class TestLPLSmellContent {
	
	LPLSmellContent smellContent = getLPLSmellContent();
	private static LPLSmellContent getLPLSmellContent() {
		LPLMethodObject methodObject = TestLPLMethodObjectBasicFunctionality.createMockLPLMethodObject1();
		List<Integer> extractedParameterIndicesList = Arrays.asList(2,3);
		String newClassName = "ExtractedClass";
		String newParameterName = "extractedParameter";
		LPLSmellContent smellContent = new LPLSmellContent(methodObject, extractedParameterIndicesList, newClassName, newParameterName);
		return smellContent;
	}
	
	@Test
	public void testGetLPLObject() {
		String lplMethodObjectName = smellContent.getLPLMethodObject().getName();
		assertEquals("testMethod1", lplMethodObjectName);
	}
	
	@Test
	public void testGetExtractedParameterIndicesList() {
		List<Integer> list = smellContent.getExtractedParameterIndicesList();
		assertEquals(Arrays.asList(2,3), list);
	}
	
	@Test
	public void testGetNewClassName() {
		String className = smellContent.getNewClassName();
		assertEquals("ExtractedClass", className);
	}
	
	@Test
	public void testGetNewParameterName() {
		String parameterName = smellContent.getNewParameterName();
		assertEquals("extractedParameter", parameterName);
	}
}

