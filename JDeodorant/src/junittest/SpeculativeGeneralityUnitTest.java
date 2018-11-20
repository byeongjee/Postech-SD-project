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

public class SpeculativeGeneralityUnitTest {
	public ViewContentProvider makeViewContentProvider() {
		SpeculativeGenerality _SG = new SpeculativeGenerality();
		return _SG.new ViewContentProvider();
	}
	
	private ClassObject _classObject; 
	private ClassObjectCandidate _classObjectCandidate;
	private List<MethodObject> _methodObjectList;
	private MethodObject _methodObject;
	private ConstructorObject _constructorObject;
	
	public SpeculativeGeneralityUnitTest() {
		this._classObject = new ClassObject();
		this._classObject.setName("testClass");
		this._classObjectCandidate = new ClassObjectCandidate();
		this._methodObjectList = new ArrayList<MethodObject>();
		this._constructorObject = new ConstructorObject();
		this._constructorObject.setName("testConstructor");
		this._methodObject = new MethodObject(_constructorObject);
	}
	
	@Test
    public void testClassObjectCandidateConstructor() {
		assertEquals(_classObjectCandidate.getName(), "");

		this._classObjectCandidate = new ClassObjectCandidate(_classObject);
		assertEquals(_classObjectCandidate.getName(), _classObject.getName());
		assertEquals(_classObjectCandidate.getAccess(), _classObject.getAccess());
		assertEquals(_classObjectCandidate.isAbstract(), _classObject.isAbstract());
		assertEquals(_classObjectCandidate.isInterface(), _classObject.isInterface());
		assertEquals(_classObjectCandidate.isStatic(), _classObject.isStatic());
		assertEquals(_classObjectCandidate.isEnum(), _classObject.isEnum());
	}
	
	@Test
    public void testClassObjectCandidate() {
		// Code Smell Type
		this._classObjectCandidate.setCodeSmellType("Unnecessary Parameters");
		assertEquals(this._classObjectCandidate.getCodeSmellType(), "Unnecessary Parameters");
		
		//refactorType;
		this._classObjectCandidate.setRefactorType("Extract Method");
		assertEquals(this._classObjectCandidate.getRefactorType(), "Extract Method");
		
	    //numChild;
		this._classObjectCandidate.setNumChild(3);
		assertEquals(this._classObjectCandidate.getNumChild(), 3);
		
	    //numUnusedParameter;
		this._classObjectCandidate.addNumUnusedParameter(3);
		int answer = this._classObjectCandidate.getNumUnusedParameter().get(0);
		assertEquals(3, answer);
	}
	
	@Test
    public void testClassObjectCandidateSmellingMethods() {
		// Unnecessary Parameters
		this._classObjectCandidate.setCodeSmellType("Unnecessary Parameters");
		
		this._classObjectCandidate.addSmellingMethod(this._methodObject);
		assertEquals(this._classObjectCandidate.getSmellingMethods().get(0), this._methodObject);
		
		this._classObjectCandidate.setSmellingMethods(this._methodObjectList);
		assertEquals(this._classObjectCandidate.getSmellingMethods(), this._methodObjectList);
		
		// Not Unnecessary Parameters
		this._classObjectCandidate.setCodeSmellType("Unnecessary Parameter XXX");
		assertEquals(null, this._classObjectCandidate.getSmellingMethods());
	}
	
	@Test
    public void testClassObjectCandidateImportantDetails() {
		// ClassFullName
		assertEquals("class ", this._classObjectCandidate.getClassFullName());
		
		// FieldList
		assertEquals(null, this._classObjectCandidate.getFieldList());

		// ClassField
		/* System.out.println(this._classObjectCandidate.getClassField().size());
		assertEquals(null, this._classObjectCandidate.getClassField());	*/
	}
	
	@Test
    public void testgetChildren() {	
		Object[] result_null = makeViewContentProvider().getChildren(null);
	    assertTrue(result_null.length == 0);
	    
		Object[] result_String = makeViewContentProvider().getChildren("String");
	    assertTrue(result_String.length == 0);

	    this._classObjectCandidate.setCodeSmellType("Unnecessary Parameters");
		this._classObjectCandidate.addSmellingMethod(this._methodObject);
		Object[] result_ClassObjectCandidate = makeViewContentProvider().getChildren(_classObjectCandidate);
		assertEquals(result_ClassObjectCandidate[0], this._methodObject);
	}
	
	@Test
	public void testgetParent() {
		Object result_null = makeViewContentProvider().getParent(null);
	    assertTrue(result_null == null);
	    
		Object result_fail = makeViewContentProvider().getParent(_classObjectCandidate);
		assertEquals(result_fail, null);
	}
	
	@Test
	public void testApplyRefactoring() {
		
	}
	
	@Test
	public void testRefactroingNoChild() {
		
		// Abstract, Interface 둘 다
		
	}
	
	@Test
	public void testRefactoringOneChildInterface() {
		
		// Interface 지워진것 확인
		
	}
	
	@Test
	public void testRefactoringOneChildAbstract() {
		
		
		// 올바른 이름의 파일이 제대로 생겼는지
		
		// 그 안의 내용이랑 정답 비교
	
	}
	
	@Test
	public void testRefactoringOneChildAbstractwithExceptionalConstructor() {
		
		
		// 올바른 이름의 파일이 제대로 생겼는지
		
		// 그 안의 내용이랑 정답 비교
	
	}
	
	@Test
	public void testRefactoringUnnecessaryParameterMethod() {
		
		// 정답 확인
	}
	
	@Test
	public void testRefactoringMany() {
		
	}
}
