package junittest.speculatviveGenerality;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import gr.uom.java.ast.Access;
import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.ClassObjectCandidate;
import gr.uom.java.ast.ConstructorObject;
import gr.uom.java.ast.MethodObject;
import gr.uom.java.ast.TypeObject;
import gr.uom.java.jdeodorant.refactoring.views.SpeculativeGenerality;
import gr.uom.java.jdeodorant.refactoring.views.SpeculativeGenerality.ViewContentProvider;

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
	
	public ClassObjectCandidateUnitTest() {
		this.rootClassObject = new ClassObject();
		this.rootClassObject.setName("testClass");
		this.mainClassObjectCandidate = new ClassObjectCandidate(rootClassObject);
		
		this.smellingMethods = new ArrayList<MethodObject>();
		ConstructorObject _constructorObject = new ConstructorObject();
		_constructorObject.setName("testConstructor");
		this.unnParameterMethod = new MethodObject(_constructorObject);
		//this.allParameterMethod = new MethodObject(_constructorObject);
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
		
		assertEquals(0, __parent.getNumChild());
		
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
	
	
	/**
	 * Test Information Related to Refactoring
	 */
	@Test
	public void testFullName() {
		// Full Name
		this.rootClassObject.setAccess(Access.PUBLIC);
		this.rootClassObject.setAbstract(false);
		this.rootClassObject.setInterface(false);
		this.rootClassObject.setName("test");
		this.mainClassObjectCandidate = new ClassObjectCandidate(rootClassObject);
		assertEquals(mainClassObjectCandidate.getClassFullName(), "public class test");
		

		// Full Name
		this.rootClassObject.setAccess(Access.PUBLIC);
		this.rootClassObject.setAbstract(true);
		this.rootClassObject.setInterface(false);
		this.rootClassObject.setName("test");
		this.mainClassObjectCandidate = new ClassObjectCandidate(rootClassObject);
		assertEquals(mainClassObjectCandidate.getClassFullName(), "public abstract class test");
		

		// Full Name
		this.rootClassObject.setAccess(Access.PUBLIC);
		this.rootClassObject.setAbstract(false);
		this.rootClassObject.setInterface(true);
		this.rootClassObject.setName("test");
		this.mainClassObjectCandidate = new ClassObjectCandidate(rootClassObject);
		assertEquals(mainClassObjectCandidate.getClassFullName(), "public interface test");
		

		// Full Name
		this.rootClassObject.setAccess(Access.PUBLIC);
		this.rootClassObject.setAbstract(false);
		this.rootClassObject.setInterface(false);
		this.rootClassObject.setName("test");
		this.mainClassObjectCandidate = new ClassObjectCandidate(rootClassObject);
		assertEquals(mainClassObjectCandidate.getClassFullName(), "public class test");
	}
	
	@Test
	public void testHighlightPoint() {
		this.mainClassObjectCandidate.setStart(4);
		assertEquals(4, this.mainClassObjectCandidate.getStart());
		
		this.mainClassObjectCandidate.setLength(5);
		assertEquals(5, this.mainClassObjectCandidate.getLength());
	}
	
	@Test
	public void testContent() {
		List<String> content = new ArrayList<String>();
		List<String> answer = new ArrayList<String>();
		
		// Set Content Implicitly
		this.mainClassObjectCandidate.setContent();
		content = this.mainClassObjectCandidate.getContent();
		assertEquals(new ArrayList<String>(), content);
		
		// Set Content Explicitly
		answer = new ArrayList<String>();
		answer.add("Test");
		this.mainClassObjectCandidate.setContent(answer);
		content = this.mainClassObjectCandidate.getContent();
		
		assertEquals(answer, content);
	}
}
