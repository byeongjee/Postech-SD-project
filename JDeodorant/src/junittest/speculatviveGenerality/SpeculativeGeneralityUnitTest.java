package junittest.speculatviveGenerality;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.junit.jupiter.api.Test;

import gr.uom.java.jdeodorant.refactoring.views.SpeculativeGenerality;
import gr.uom.java.jdeodorant.refactoring.views.SpeculativeGenerality.*;
import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.ClassObjectCandidate;
import gr.uom.java.ast.MethodObject;

public class SpeculativeGeneralityUnitTest {
	SpeculativeGenerality _SG = new SpeculativeGenerality();
	
	public ViewContentProvider makeViewContentProvider() {
		return _SG.new ViewContentProvider();
	}
	
	private ClassObject mainClassObject = new ClassObject(); 
	private ClassObjectCandidate mainClassObjectCandidate = new ClassObjectCandidate();
	
	@Test
	public void testClassObjectToBeExamined() {
		mainClassObject.setName("target");
		mainClassObject.setAbstract(true);
		Set<ClassObject> classObjectToBeExaminedSet = new HashSet<ClassObject>();
		classObjectToBeExaminedSet.add(mainClassObject);
		_SG.setClassObjectToBeExamined(classObjectToBeExaminedSet);
		assertEquals(classObjectToBeExaminedSet, _SG.getClassObjectToBeExamined());
		
	}
	
	@Test
	public void testSmellingClassEntries() {
		mainClassObject.setName("target");
		mainClassObject.setAbstract(true);
		Set<ClassObject> classObjectToBeExaminedSet = new HashSet<ClassObject>();
		classObjectToBeExaminedSet.add(mainClassObject);
		_SG.setClassObjectToBeExamined(classObjectToBeExaminedSet);
		_SG.setSmellingClassEntries(_SG.getClassObjectToBeExamined());
		assertEquals( 1, _SG.getSmellingClassEntries().length );
	}
	
	@Test
	public void testElements() {
		mainClassObject.setName("target");
		mainClassObject.setAbstract(true);
		Set<ClassObject> classObjectToBeExaminedSet = new HashSet<ClassObject>();
		classObjectToBeExaminedSet.add(mainClassObject);
		_SG.setClassObjectToBeExamined(classObjectToBeExaminedSet);
		_SG.setSmellingClassEntries(_SG.getClassObjectToBeExamined());
		mainClassObjectCandidate = new ClassObjectCandidate(mainClassObject);
		Object result_element = makeViewContentProvider().getElements(mainClassObjectCandidate);
		assertEquals(((ClassObjectCandidate[]) result_element)[0].getName(), mainClassObjectCandidate.getName());
	}
	
	@Test
    public void testChildren() {	
		Object[] result_null = makeViewContentProvider().getChildren(null);
	    assertEquals(result_null.length, 0);
	    
		Object[] result_String = makeViewContentProvider().getChildren("String");
	    assertEquals(result_String.length, 0);

	    List<MethodObject> methodObjectList = new ArrayList<MethodObject>();
	    MethodObject methodObject = new MethodObject(null);
	    methodObjectList.add(methodObject);
	    mainClassObjectCandidate.setSmellingMethods( methodObjectList );
		Object[] result_ClassObjectCandidate = makeViewContentProvider().getChildren(mainClassObjectCandidate);
		assertEquals(result_ClassObjectCandidate.length, methodObjectList.size());
	}
	
	@Test
	public void testParent() {
		Object result_null = makeViewContentProvider().getParent(null);
	    assertTrue(result_null == null);
	    
	    
	    mainClassObject.setName("target");
	    mainClassObject.setAbstract(true);
	    Set<ClassObject> classObjectToBeExaminedSet = new HashSet<ClassObject>();
	    classObjectToBeExaminedSet.add(mainClassObject);
	    _SG.setClassObjectToBeExamined(classObjectToBeExaminedSet);
	    _SG.setSmellingClassEntries(_SG.getClassObjectToBeExamined());
	    mainClassObjectCandidate = new ClassObjectCandidate(mainClassObject);
	    Object result_abstract = makeViewContentProvider().getParent(mainClassObjectCandidate);
		assertEquals(((ClassObjectCandidate) result_abstract).getName(), mainClassObjectCandidate.getName());
		

	    mainClassObject.setAbstract(false);
	    mainClassObject.setInterface(true);
	    classObjectToBeExaminedSet = new HashSet<ClassObject>();
	    classObjectToBeExaminedSet.add(mainClassObject);
	    _SG.setClassObjectToBeExamined(classObjectToBeExaminedSet);
	    _SG.setSmellingClassEntries(_SG.getClassObjectToBeExamined());
	    mainClassObjectCandidate = new ClassObjectCandidate(mainClassObject);
	    Object result_interface = makeViewContentProvider().getParent(mainClassObjectCandidate);
	    assertEquals(((ClassObjectCandidate) result_interface).getName(), mainClassObjectCandidate.getName());
	}
	
	@Test
	public void testPressRefactoring() {
		mainClassObject.setName("target");
		mainClassObject.setAbstract(true);
		Set<ClassObject> classObjectToBeExaminedSet = new HashSet<ClassObject>();
		classObjectToBeExaminedSet.add(mainClassObject);
		_SG.setClassObjectToBeExamined(classObjectToBeExaminedSet);
		_SG.setSmellingClassEntries(_SG.getClassObjectToBeExamined());
		mainClassObjectCandidate = new ClassObjectCandidate(mainClassObject);
	}
}
