package JUnitTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gr.uom.java.jdeodorant.refactoring.views.SpeculativeGenerality;
import gr.uom.java.jdeodorant.refactoring.views.SpeculativeGenerality.*;
import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.ClassObjectCandidate;

public class SpeculativeGeneralityUnitTest {
	public ViewContentProvider makeViewContentProvider() {
		SpeculativeGenerality _SG = new SpeculativeGenerality();
		return _SG.new ViewContentProvider();
	}
	
	private ClassObject _classObject; 
	private ClassObjectCandidate _classObjectCandidate;
	
	public SpeculativeGeneralityUnitTest() {
		this._classObject =  new ClassObject();
		this._classObjectCandidate = new ClassObjectCandidate(this._classObject);
	}
	
	@Test
    public void testClassObjectCandidate() {	
		this._classObjectCandidate.setCodeSmellType("Unnecessary Paremeter");
	}
	
	@Test
    public void testgetChildren() {	
		
	}
	
	@Test
	public void testgetParent() {
		
	}
	
	@Test
	public void testhasChildren() {
		
	}

	@Test
	public void testgetElement() {
		
	}
}
