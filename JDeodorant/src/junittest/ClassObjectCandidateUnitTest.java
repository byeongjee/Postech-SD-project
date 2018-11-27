package junittest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import gr.uom.java.jdeodorant.refactoring.views.MessageChainStructure;
import gr.uom.java.jdeodorant.refactoring.views.SpeculativeGenerality;
import gr.uom.java.jdeodorant.refactoring.views.SpeculativeGenerality.*;
import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.ClassObjectCandidate;
import gr.uom.java.ast.ConstructorObject;
import gr.uom.java.ast.MethodObject;
import gr.uom.java.ast.TypeObject;

public class ClassObjectCandidateUnitTest {
	public ViewContentProvider makeViewContentProvider() {
		SpeculativeGenerality _SG = new SpeculativeGenerality();
		return _SG.new ViewContentProvider();
	}
	
	private ClassObject rootClassObject; 
	private ClassObjectCandidate mainClassObjectCandidate;
	
	private ClassObjectCandidate oneAbsClassObjectCandidate;
	private ClassObjectCandidate oneIntClassObjectCandidate;
	
	private ClassObjectCandidate firstChildClassObjectCandidate;
	private ClassObjectCandidate secondChildClassObjectCandidate;
	
	private List<MethodObject> smellingMethods;
	private MethodObject unnParameterMethod;
	private MethodObject allParameterMethod;
	
	public ClassObjectCandidateUnitTest() {
		this.rootClassObject = new ClassObject();
		this.rootClassObject.setName("testClass");
		this.mainClassObjectCandidate = new ClassObjectCandidate(rootClassObject);
		
		this.smellingMethods = new ArrayList<MethodObject>();
		ConstructorObject _constructorObject = new ConstructorObject();
		_constructorObject.setName("testConstructor");
		this.unnParameterMethod = new MethodObject(_constructorObject);
		this.allParameterMethod = new MethodObject(_constructorObject);
	}
	
	@Test
    public void testConstructor() {
		this.mainClassObjectCandidate = new ClassObjectCandidate();
		assertEquals(mainClassObjectCandidate.getName(), "");
		
		this.mainClassObjectCandidate = new ClassObjectCandidate(rootClassObject);
		assertEquals(mainClassObjectCandidate.getName(), rootClassObject.getName());
		assertEquals(mainClassObjectCandidate.getAccess(), rootClassObject.getAccess());
		assertEquals(mainClassObjectCandidate.isAbstract(), rootClassObject.isAbstract());
		assertEquals(mainClassObjectCandidate.isInterface(), rootClassObject.isInterface());
		assertEquals(mainClassObjectCandidate.isStatic(), rootClassObject.isStatic());
		assertEquals(mainClassObjectCandidate.isEnum(), rootClassObject.isEnum());
	}
	
	/**
	 * Test Information Related to Code Smell Detection
	 */
	@Test
	public void testNumChild() {
		// Set Hierarchy of Classes
		TypeObject _type = new TypeObject("Parent");
		
		ClassObject _parent = new ClassObject();
		ClassObject _child = new ClassObject();
		
		_child.setSuperclass(_type);
		
		ClassObjectCandidate __parent = new ClassObjectCandidate(_parent);
		
		assertEquals(1, __parent.getNumChild());
		
		// Set Actively
		__parent.setNumChild(2);
		assertEquals(2, __parent.getNumChild());
	}
	
	@Test
    public void testCodeSmellType() {
		this.mainClassObjectCandidate.setCodeSmellType("Unnecessary Parameters");
		assertEquals(this.mainClassObjectCandidate.getCodeSmellType(), "Unnecessary Parameters");
	}
	
	@Test
    public void testRefactorType() {
		this.mainClassObjectCandidate.setRefactorType("Extract Method");
		assertEquals(this.mainClassObjectCandidate.getRefactorType(), "Extract Method");
	}
		
	@Test
    public void testSmellingMethods() {
		// Unnecessary Parameters
		this.mainClassObjectCandidate.setCodeSmellType("Unnecessary Parameters");
		
		this.mainClassObjectCandidate.addSmellingMethod(this.unnParameterMethod);
		assertEquals(this.mainClassObjectCandidate.getSmellingMethods().get(0), this.unnParameterMethod);
		
		this.mainClassObjectCandidate.setSmellingMethods(this.smellingMethods);
		assertEquals(this.mainClassObjectCandidate.getSmellingMethods(), this.smellingMethods);
		
		// Not Unnecessary Parameters
		this.mainClassObjectCandidate.setCodeSmellType("Unnecessary Parameter XXX");
		assertEquals(0, this.mainClassObjectCandidate.getSmellingMethods().size());
	}
	
	@Test
    public void testUnusedParameter() {
	    //Number of unusedParameter
		this.mainClassObjectCandidate.addNumUnusedParameter(3);
		int answer1 = this.mainClassObjectCandidate.getNumUnusedParameter().get(0);
		assertEquals(3, answer1);
		
		//List of unusedParameter
		List<String> answer2 = this.mainClassObjectCandidate.getUnusedParameterList().get(0);
		assertEquals("int", answer2.get(0));
		assertEquals("a", answer2.get(0));
	}
	
	@Test
	public void testUsedParameter() {
		List<String> answer2 = this.mainClassObjectCandidate.getUnusedParameterList().get(0);
		assertEquals("int", answer2.get(0));
		assertEquals("a", answer2.get(0));
	}
	
	/**
	 * Test Information Related to Refactoring
	 */
	@Test
	public void testContent() {
		// Set Content
		List<String> answer = new ArrayList<String>();
		answer.add("");
		
		this.mainClassObjectCandidate.setContent(answer);
		List<String> content = this.mainClassObjectCandidate.getContent();
		
		// Get Content
		assertEquals(answer, content);
	}
	
	@Test
	public void testMergeIntoChild() {
		List<String> answerInt = new ArrayList<String>();
		List<String> answerAbs = new ArrayList<String>();
		answerInt.add("");
		answerAbs.add("");
		
		// set child and parent relationship
		assertTrue(this.oneIntClassObjectCandidate.getNumChild() == 1);
		assertTrue(this.oneAbsClassObjectCandidate.getNumChild() == 1);
		
		// operate Merging
		this.oneIntClassObjectCandidate.mergeIntoChild(this.firstChildClassObjectCandidate);
		this.oneAbsClassObjectCandidate.mergeIntoChild(this.secondChildClassObjectCandidate);
		
		// get Content and Compare
		assertEquals(answerAbs, this.firstChildClassObjectCandidate.getContent());
		assertEquals(answerInt, this.secondChildClassObjectCandidate.getContent());
		
		assertEquals(null, this.oneIntClassObjectCandidate.getIFile());
		assertEquals(null, this.oneAbsClassObjectCandidate.getIFile());
	}
	
	@Test
	public void testResolveUnnecessaryParameters() {
		// set Content with Unnecessary Parameters
		List<String> primer = new ArrayList<String>();
		this.mainClassObjectCandidate.setContent(primer);
				
		// resolve the code smell
		this.mainClassObjectCandidate.resolveUnnecessaryParameters(this.smellingMethods.get(0));
		List<String> prediction = this.mainClassObjectCandidate.getContent();
		
		List<String> answer = new ArrayList<String>();
		answer.add("");
		assertEquals(answer, prediction);
	}
}
