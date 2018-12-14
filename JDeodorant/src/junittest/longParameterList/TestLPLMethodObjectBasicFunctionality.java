package junittest.longParameterList;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.CompletionRequestor;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IBufferFactory;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICodeCompletionRequestor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ICompletionRequestor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IInitializer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IModularClassFile;
import org.eclipse.jdt.core.IModuleDescription;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IOrdinaryClassFile;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IProblemRequestor;
import org.eclipse.jdt.core.IRegion;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.IWorkingCopy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.eval.IEvaluationContext;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.UndoEdit;
import org.junit.BeforeClass;
import org.junit.Test;

import gr.uom.java.ast.ConstructorObject;
import gr.uom.java.ast.LPLMethodObject;
import gr.uom.java.ast.MethodObject;

public class TestLPLMethodObjectBasicFunctionality {
	private IJavaProject mockIJavaProject;
	private IPackageFragment mockIPackageFragmemt;
	private ICompilationUnit mockICompilationUnit;
	private IType mockIType;
	private IMethod mockIMethod;
	private static MethodObject createMockMethodObject1() {
		ConstructorObject co = new ConstructorObject() {
			@Override
			public String getName(){
				return "testMethod1";
			}
			@Override
			public String getClassName() {
				return "testPackage.testClass";
			}
			@Override
			public List<String> getParameterList() {
				List<String> parameterList = new ArrayList<String>();
				parameterList.add("a");
				parameterList.add("b");
				return parameterList;
			}
			@Override
			public List<String> getParameterTypeAndNameList() {
				List<String> parameterTypeList = new ArrayList<String>();
				parameterTypeList.add("int a");
				parameterTypeList.add("char b");
				return parameterTypeList;
			}
		};
		return new MethodObject(co);
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
		assertEquals(mockLPLMethodObject1.getColumnText(2), "testPackage.testClass");
		assertEquals(mockLPLMethodObject1.getColumnText(3), "[int a, char b]");
		assertEquals(mockLPLMethodObject1.getColumnText(4), "2");

	}

	@Test
	public void testIsLongParameterListMethod() {
		assertFalse(mockLPLMethodObject1.isLongParamterListMethod());
		assertTrue(mockLPLMethodObject2.isLongParamterListMethod());
	}

	@Test
	public void testCreateLPLMethodObjectFrom() {
		MethodObject methodObject = createMockMethodObject1();
		LPLMethodObject lplMethodObject = LPLMethodObject.createLPLMethodObjectFrom(methodObject);
		assertEquals("testMethod1", lplMethodObject.getName());
	}
	
	@Test
	public void testToIMethod() {
		MethodObject methodObject = createMockMethodObject1();
		LPLMethodObject lplMethodObject = LPLMethodObject.createLPLMethodObjectFrom(methodObject);
		try {
			setUpMockObject();
			lplMethodObject.toIMethod(mockIJavaProject);
		} catch (JavaModelException e) {
			fail();
		}
		assertEquals("testMethod1", lplMethodObject.getName());
	}
	
	public static LPLMethodObject createMockLPLMethodObject1() {
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
				public List<String> getParameterTypeAndNameList() {
					List<String> parameterTypeList = new ArrayList<String>();
					parameterTypeList.add("int a");
					parameterTypeList.add("char b");
					parameterTypeList.add("String c");
					parameterTypeList.add("boolean d");
					return parameterTypeList;
				}
			};
			return new MethodObject(co);
	}

	private static LPLMethodObject createMockLPLMethodObject2() {
		MethodObject mockMethodObject = createMockMethodObject2();
		return LPLMethodObject.createLPLMethodObjectFrom(mockMethodObject);
	}
	
	private void setUpMockObject() {
		mockIMethod = new IMethod() {

			public String[] getCategories() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IClassFile getClassFile() {
				// FIXME Auto-generated method stub
				return null;
			}

			public ICompilationUnit getCompilationUnit() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IType getDeclaringType() {
				// FIXME Auto-generated method stub
				return null;
			}

			public int getFlags() throws JavaModelException {
				// FIXME Auto-generated method stub
				return 0;
			}

			public ISourceRange getJavadocRange() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public int getOccurrenceCount() {
				// FIXME Auto-generated method stub
				return 0;
			}

			public IType getType(String arg0, int arg1) {
				// FIXME Auto-generated method stub
				return null;
			}

			public ITypeRoot getTypeRoot() {
				// FIXME Auto-generated method stub
				return null;
			}

			public boolean isBinary() {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean exists() {
				// FIXME Auto-generated method stub
				return false;
			}

			public IJavaElement getAncestor(int arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getAttachedJavadoc(IProgressMonitor arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IResource getCorrespondingResource() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public int getElementType() {
				// FIXME Auto-generated method stub
				return 0;
			}

			public String getHandleIdentifier() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaModel getJavaModel() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaProject getJavaProject() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IOpenable getOpenable() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaElement getParent() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IPath getPath() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaElement getPrimaryElement() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IResource getResource() {
				// FIXME Auto-generated method stub
				return null;
			}

			public ISchedulingRule getSchedulingRule() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IResource getUnderlyingResource() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public boolean isReadOnly() {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isStructureKnown() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public <T> T getAdapter(Class<T> adapter) {
				// FIXME Auto-generated method stub
				return null;
			}

			public ISourceRange getNameRange() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getSource() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public ISourceRange getSourceRange() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public void copy(IJavaElement arg0, IJavaElement arg1, String arg2, boolean arg3, IProgressMonitor arg4)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void delete(boolean arg0, IProgressMonitor arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void move(IJavaElement arg0, IJavaElement arg1, String arg2, boolean arg3, IProgressMonitor arg4)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void rename(String arg0, boolean arg1, IProgressMonitor arg2) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public IJavaElement[] getChildren() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public boolean hasChildren() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public IAnnotation getAnnotation(String arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public IAnnotation[] getAnnotations() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IMemberValuePair getDefaultValue() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getElementName() {
				// FIXME Auto-generated method stub
				return "testMethod1";
			}

			public String[] getExceptionTypes() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getKey() {
				// FIXME Auto-generated method stub
				return null;
			}

			public int getNumberOfParameters() {
				// FIXME Auto-generated method stub
				return 0;
			}

			public String[] getParameterNames() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public String[] getParameterTypes() {
				// FIXME Auto-generated method stub
				return null;
			}

			public ILocalVariable[] getParameters() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public String[] getRawParameterNames() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getReturnType() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getSignature() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public ITypeParameter getTypeParameter(String arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public String[] getTypeParameterSignatures() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public ITypeParameter[] getTypeParameters() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public boolean isConstructor() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isLambdaMethod() {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isMainMethod() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isResolved() {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isSimilar(IMethod arg0) {
				// FIXME Auto-generated method stub
				return false;
			}
			
		};
		mockIType = new IType() {

			public String[] getCategories() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public ICompilationUnit getCompilationUnit() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IType getDeclaringType() {
				// FIXME Auto-generated method stub
				return null;
			}

			public int getFlags() throws JavaModelException {
				// FIXME Auto-generated method stub
				return 0;
			}

			public ISourceRange getJavadocRange() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public int getOccurrenceCount() {
				// FIXME Auto-generated method stub
				return 0;
			}

			public IType getType(String arg0, int arg1) {
				// FIXME Auto-generated method stub
				return null;
			}

			public ITypeRoot getTypeRoot() {
				// FIXME Auto-generated method stub
				return null;
			}

			public boolean isBinary() {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean exists() {
				// FIXME Auto-generated method stub
				return false;
			}

			public IJavaElement getAncestor(int arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getAttachedJavadoc(IProgressMonitor arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IResource getCorrespondingResource() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public int getElementType() {
				// FIXME Auto-generated method stub
				return 0;
			}

			public String getHandleIdentifier() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaModel getJavaModel() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaProject getJavaProject() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IOpenable getOpenable() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaElement getParent() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IPath getPath() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaElement getPrimaryElement() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IResource getResource() {
				// FIXME Auto-generated method stub
				return null;
			}

			public ISchedulingRule getSchedulingRule() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IResource getUnderlyingResource() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public boolean isReadOnly() {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isStructureKnown() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public <T> T getAdapter(Class<T> adapter) {
				// FIXME Auto-generated method stub
				return null;
			}

			public ISourceRange getNameRange() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getSource() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public ISourceRange getSourceRange() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public void copy(IJavaElement arg0, IJavaElement arg1, String arg2, boolean arg3, IProgressMonitor arg4)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void delete(boolean arg0, IProgressMonitor arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void move(IJavaElement arg0, IJavaElement arg1, String arg2, boolean arg3, IProgressMonitor arg4)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void rename(String arg0, boolean arg1, IProgressMonitor arg2) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public IJavaElement[] getChildren() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public boolean hasChildren() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public IAnnotation getAnnotation(String arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public IAnnotation[] getAnnotations() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public void codeComplete(char[] arg0, int arg1, int arg2, char[][] arg3, char[][] arg4, int[] arg5,
					boolean arg6, ICompletionRequestor arg7) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void codeComplete(char[] arg0, int arg1, int arg2, char[][] arg3, char[][] arg4, int[] arg5,
					boolean arg6, CompletionRequestor arg7) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void codeComplete(char[] arg0, int arg1, int arg2, char[][] arg3, char[][] arg4, int[] arg5,
					boolean arg6, ICompletionRequestor arg7, WorkingCopyOwner arg8) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void codeComplete(char[] arg0, int arg1, int arg2, char[][] arg3, char[][] arg4, int[] arg5,
					boolean arg6, CompletionRequestor arg7, IProgressMonitor arg8) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void codeComplete(char[] arg0, int arg1, int arg2, char[][] arg3, char[][] arg4, int[] arg5,
					boolean arg6, CompletionRequestor arg7, WorkingCopyOwner arg8) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void codeComplete(char[] arg0, int arg1, int arg2, char[][] arg3, char[][] arg4, int[] arg5,
					boolean arg6, CompletionRequestor arg7, WorkingCopyOwner arg8, IProgressMonitor arg9)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public IField createField(String arg0, IJavaElement arg1, boolean arg2, IProgressMonitor arg3)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IInitializer createInitializer(String arg0, IJavaElement arg1, IProgressMonitor arg2)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IMethod createMethod(String arg0, IJavaElement arg1, boolean arg2, IProgressMonitor arg3)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IType createType(String arg0, IJavaElement arg1, boolean arg2, IProgressMonitor arg3)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IMethod[] findMethods(IMethod arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaElement[] getChildrenForCategory(String arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IOrdinaryClassFile getClassFile() {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getElementName() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IField getField(String arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public IField[] getFields() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getFullyQualifiedName() {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getFullyQualifiedName(char arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getFullyQualifiedParameterizedName() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IInitializer getInitializer(int arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public IInitializer[] getInitializers() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getKey() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IMethod getMethod(String arg0, String[] arg1) {
				// FIXME Auto-generated method stub
				return null;
			}

			public IMethod[] getMethods() throws JavaModelException {
				IMethod ret[] = {mockIMethod};
				return ret;
			}

			public IPackageFragment getPackageFragment() {
				// FIXME Auto-generated method stub
				return null;
			}

			public String[] getSuperInterfaceNames() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public String[] getSuperInterfaceTypeSignatures() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getSuperclassName() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getSuperclassTypeSignature() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IType getType(String arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public ITypeParameter getTypeParameter(String arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public String[] getTypeParameterSignatures() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public ITypeParameter[] getTypeParameters() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getTypeQualifiedName() {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getTypeQualifiedName(char arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public IType[] getTypes() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public boolean isAnnotation() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isAnonymous() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isClass() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isEnum() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isInterface() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isLambda() {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isLocal() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isMember() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isResolved() {
				// FIXME Auto-generated method stub
				return false;
			}

			public ITypeHierarchy loadTypeHierachy(InputStream arg0, IProgressMonitor arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public ITypeHierarchy newSupertypeHierarchy(IProgressMonitor arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public ITypeHierarchy newSupertypeHierarchy(ICompilationUnit[] arg0, IProgressMonitor arg1)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public ITypeHierarchy newSupertypeHierarchy(IWorkingCopy[] arg0, IProgressMonitor arg1)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public ITypeHierarchy newSupertypeHierarchy(WorkingCopyOwner arg0, IProgressMonitor arg1)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public ITypeHierarchy newTypeHierarchy(IProgressMonitor arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public ITypeHierarchy newTypeHierarchy(IJavaProject arg0, IProgressMonitor arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public ITypeHierarchy newTypeHierarchy(ICompilationUnit[] arg0, IProgressMonitor arg1)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public ITypeHierarchy newTypeHierarchy(IWorkingCopy[] arg0, IProgressMonitor arg1)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public ITypeHierarchy newTypeHierarchy(WorkingCopyOwner arg0, IProgressMonitor arg1)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public ITypeHierarchy newTypeHierarchy(IJavaProject arg0, WorkingCopyOwner arg1, IProgressMonitor arg2)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public String[][] resolveType(String arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public String[][] resolveType(String arg0, WorkingCopyOwner arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}
			
		};
		mockICompilationUnit = new ICompilationUnit() {

			public IType findPrimaryType() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaElement getElementAt(int arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public ICompilationUnit getWorkingCopy(WorkingCopyOwner arg0, IProgressMonitor arg1)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public boolean exists() {
				// FIXME Auto-generated method stub
				return false;
			}

			public IJavaElement getAncestor(int arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getAttachedJavadoc(IProgressMonitor arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IResource getCorrespondingResource() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getElementName() {
				// FIXME Auto-generated method stub
				return "testClass.java";
			}

			public int getElementType() {
				// FIXME Auto-generated method stub
				return 0;
			}

			public String getHandleIdentifier() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaModel getJavaModel() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaProject getJavaProject() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IOpenable getOpenable() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaElement getParent() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IPath getPath() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaElement getPrimaryElement() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IResource getResource() {
				// FIXME Auto-generated method stub
				return null;
			}

			public ISchedulingRule getSchedulingRule() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IResource getUnderlyingResource() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public boolean isReadOnly() {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isStructureKnown() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public <T> T getAdapter(Class<T> adapter) {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaElement[] getChildren() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public boolean hasChildren() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public void close() throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public String findRecommendedLineSeparator() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IBuffer getBuffer() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public boolean hasUnsavedChanges() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isConsistent() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isOpen() {
				// FIXME Auto-generated method stub
				return false;
			}

			public void makeConsistent(IProgressMonitor arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void open(IProgressMonitor arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void save(IProgressMonitor arg0, boolean arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public ISourceRange getNameRange() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getSource() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public ISourceRange getSourceRange() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public void codeComplete(int arg0, ICodeCompletionRequestor arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void codeComplete(int arg0, ICompletionRequestor arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void codeComplete(int arg0, CompletionRequestor arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void codeComplete(int arg0, CompletionRequestor arg1, IProgressMonitor arg2)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void codeComplete(int arg0, ICompletionRequestor arg1, WorkingCopyOwner arg2)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void codeComplete(int arg0, CompletionRequestor arg1, WorkingCopyOwner arg2)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void codeComplete(int arg0, CompletionRequestor arg1, WorkingCopyOwner arg2, IProgressMonitor arg3)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public IJavaElement[] codeSelect(int arg0, int arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaElement[] codeSelect(int arg0, int arg1, WorkingCopyOwner arg2) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public void commit(boolean arg0, IProgressMonitor arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void destroy() {
				// FIXME Auto-generated method stub
				
			}

			public IJavaElement findSharedWorkingCopy(IBufferFactory arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaElement getOriginal(IJavaElement arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaElement getOriginalElement() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaElement getSharedWorkingCopy(IProgressMonitor arg0, IBufferFactory arg1, IProblemRequestor arg2)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaElement getWorkingCopy() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaElement getWorkingCopy(IProgressMonitor arg0, IBufferFactory arg1, IProblemRequestor arg2)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public boolean isBasedOn(IResource arg0) {
				// FIXME Auto-generated method stub
				return false;
			}

			public IMarker[] reconcile() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public void reconcile(boolean arg0, IProgressMonitor arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void copy(IJavaElement arg0, IJavaElement arg1, String arg2, boolean arg3, IProgressMonitor arg4)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void delete(boolean arg0, IProgressMonitor arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void move(IJavaElement arg0, IJavaElement arg1, String arg2, boolean arg3, IProgressMonitor arg4)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void rename(String arg0, boolean arg1, IProgressMonitor arg2) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public UndoEdit applyTextEdit(TextEdit arg0, IProgressMonitor arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public void becomeWorkingCopy(IProgressMonitor arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void becomeWorkingCopy(IProblemRequestor arg0, IProgressMonitor arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void commitWorkingCopy(boolean arg0, IProgressMonitor arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public IImportDeclaration createImport(String arg0, IJavaElement arg1, IProgressMonitor arg2)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IImportDeclaration createImport(String arg0, IJavaElement arg1, int arg2, IProgressMonitor arg3)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IPackageDeclaration createPackageDeclaration(String arg0, IProgressMonitor arg1)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IType createType(String arg0, IJavaElement arg1, boolean arg2, IProgressMonitor arg3)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public void discardWorkingCopy() throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public IJavaElement[] findElements(IJavaElement arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public ICompilationUnit findWorkingCopy(WorkingCopyOwner arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public IType[] getAllTypes() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IImportDeclaration getImport(String arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public IImportContainer getImportContainer() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IImportDeclaration[] getImports() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public WorkingCopyOwner getOwner() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IPackageDeclaration getPackageDeclaration(String arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public IPackageDeclaration[] getPackageDeclarations() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public ICompilationUnit getPrimary() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IType getType(String arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public IType[] getTypes() throws JavaModelException {
				// FIXME Auto-generated method stub
				IType[] ret = {mockIType};
				return ret;
			}

			public ICompilationUnit getWorkingCopy(IProgressMonitor arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public ICompilationUnit getWorkingCopy(WorkingCopyOwner arg0, IProblemRequestor arg1, IProgressMonitor arg2)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public boolean hasResourceChanged() {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isWorkingCopy() {
				// FIXME Auto-generated method stub
				return false;
			}

			public CompilationUnit reconcile(int arg0, boolean arg1, WorkingCopyOwner arg2, IProgressMonitor arg3)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public CompilationUnit reconcile(int arg0, int arg1, WorkingCopyOwner arg2, IProgressMonitor arg3)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public CompilationUnit reconcile(int arg0, boolean arg1, boolean arg2, WorkingCopyOwner arg3,
					IProgressMonitor arg4) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public void restore() throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}
			
		};
		mockIPackageFragmemt = new IPackageFragment() {

			public IJavaElement[] getChildren() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public boolean hasChildren() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean exists() {
				// FIXME Auto-generated method stub
				return false;
			}

			public IJavaElement getAncestor(int arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getAttachedJavadoc(IProgressMonitor arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IResource getCorrespondingResource() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public int getElementType() {
				// FIXME Auto-generated method stub
				return 0;
			}

			public String getHandleIdentifier() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaModel getJavaModel() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaProject getJavaProject() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IOpenable getOpenable() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaElement getParent() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IPath getPath() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaElement getPrimaryElement() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IResource getResource() {
				// FIXME Auto-generated method stub
				return null;
			}

			public ISchedulingRule getSchedulingRule() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IResource getUnderlyingResource() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public boolean isReadOnly() {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isStructureKnown() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public <T> T getAdapter(Class<T> adapter) {
				// FIXME Auto-generated method stub
				return null;
			}

			public void close() throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public String findRecommendedLineSeparator() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IBuffer getBuffer() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public boolean hasUnsavedChanges() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isConsistent() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isOpen() {
				// FIXME Auto-generated method stub
				return false;
			}

			public void makeConsistent(IProgressMonitor arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void open(IProgressMonitor arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void save(IProgressMonitor arg0, boolean arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void copy(IJavaElement arg0, IJavaElement arg1, String arg2, boolean arg3, IProgressMonitor arg4)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void delete(boolean arg0, IProgressMonitor arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void move(IJavaElement arg0, IJavaElement arg1, String arg2, boolean arg3, IProgressMonitor arg4)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void rename(String arg0, boolean arg1, IProgressMonitor arg2) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public boolean containsJavaResources() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public ICompilationUnit createCompilationUnit(String arg0, String arg1, boolean arg2, IProgressMonitor arg3)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IClassFile[] getAllClassFiles() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IClassFile getClassFile(String arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public IClassFile[] getClassFiles() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public ICompilationUnit getCompilationUnit(String arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public ICompilationUnit[] getCompilationUnits() throws JavaModelException {
				ICompilationUnit ret[] = {mockICompilationUnit};
				return ret;
			}

			public ICompilationUnit[] getCompilationUnits(WorkingCopyOwner arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getElementName() {
				// FIXME Auto-generated method stub
				return "testPackage";
			}

			public int getKind() throws JavaModelException {
				// FIXME Auto-generated method stub
				return 0;
			}

			public IModularClassFile getModularClassFile() {
				// FIXME Auto-generated method stub
				return null;
			}

			public Object[] getNonJavaResources() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IOrdinaryClassFile getOrdinaryClassFile(String arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public IOrdinaryClassFile[] getOrdinaryClassFiles() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public boolean hasSubpackages() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isDefaultPackage() {
				// FIXME Auto-generated method stub
				return false;
			}
			
		};
		mockIJavaProject = new IJavaProject() {

			public IJavaElement[] getChildren() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public boolean hasChildren() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean exists() {
				// FIXME Auto-generated method stub
				return false;
			}

			public IJavaElement getAncestor(int arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getAttachedJavadoc(IProgressMonitor arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IResource getCorrespondingResource() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getElementName() {
				// FIXME Auto-generated method stub
				return null;
			}

			public int getElementType() {
				// FIXME Auto-generated method stub
				return 0;
			}

			public String getHandleIdentifier() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaModel getJavaModel() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaProject getJavaProject() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IOpenable getOpenable() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaElement getParent() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IPath getPath() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaElement getPrimaryElement() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IResource getResource() {
				// FIXME Auto-generated method stub
				return null;
			}

			public ISchedulingRule getSchedulingRule() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IResource getUnderlyingResource() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public boolean isReadOnly() {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isStructureKnown() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public <T> T getAdapter(Class<T> adapter) {
				// FIXME Auto-generated method stub
				return null;
			}

			public void close() throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public String findRecommendedLineSeparator() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IBuffer getBuffer() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public boolean hasUnsavedChanges() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isConsistent() throws JavaModelException {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isOpen() {
				// FIXME Auto-generated method stub
				return false;
			}

			public void makeConsistent(IProgressMonitor arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void open(IProgressMonitor arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void save(IProgressMonitor arg0, boolean arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public IClasspathEntry decodeClasspathEntry(String arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public Set<String> determineModulesOfProjectsWithNonEmptyClasspath() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public String encodeClasspathEntry(IClasspathEntry arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaElement findElement(IPath arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaElement findElement(IPath arg0, WorkingCopyOwner arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IJavaElement findElement(String arg0, WorkingCopyOwner arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IModuleDescription findModule(String arg0, WorkingCopyOwner arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IPackageFragment findPackageFragment(IPath arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IPackageFragmentRoot findPackageFragmentRoot(IPath arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IPackageFragmentRoot[] findPackageFragmentRoots(IClasspathEntry arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public IType findType(String arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IType findType(String arg0, IProgressMonitor arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IType findType(String arg0, WorkingCopyOwner arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IType findType(String arg0, String arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IType findType(String arg0, WorkingCopyOwner arg1, IProgressMonitor arg2) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IType findType(String arg0, String arg1, IProgressMonitor arg2) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IType findType(String arg0, String arg1, WorkingCopyOwner arg2) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IType findType(String arg0, String arg1, WorkingCopyOwner arg2, IProgressMonitor arg3)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IPackageFragmentRoot[] findUnfilteredPackageFragmentRoots(IClasspathEntry arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public IPackageFragmentRoot[] getAllPackageFragmentRoots() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IClasspathEntry getClasspathEntryFor(IPath arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IModuleDescription getModuleDescription() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public Object[] getNonJavaResources() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public String getOption(String arg0, boolean arg1) {
				// FIXME Auto-generated method stub
				return null;
			}

			public Map<String, String> getOptions(boolean arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public IPath getOutputLocation() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IPackageFragmentRoot getPackageFragmentRoot(String arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public IPackageFragmentRoot getPackageFragmentRoot(IResource arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public IPackageFragmentRoot[] getPackageFragmentRoots() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IPackageFragmentRoot[] getPackageFragmentRoots(IClasspathEntry arg0) {
				// FIXME Auto-generated method stub
				return null;
			}

			public IPackageFragment[] getPackageFragments() throws JavaModelException {
				IPackageFragment ret[] = {mockIPackageFragmemt};
				return ret;
			}

			public IProject getProject() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IClasspathEntry[] getRawClasspath() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IClasspathEntry[] getReferencedClasspathEntries() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public String[] getRequiredProjectNames() throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IClasspathEntry[] getResolvedClasspath(boolean arg0) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public boolean hasBuildState() {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean hasClasspathCycle(IClasspathEntry[] arg0) {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isOnClasspath(IJavaElement arg0) {
				// FIXME Auto-generated method stub
				return false;
			}

			public boolean isOnClasspath(IResource arg0) {
				// FIXME Auto-generated method stub
				return false;
			}

			public IEvaluationContext newEvaluationContext() {
				// FIXME Auto-generated method stub
				return null;
			}

			public ITypeHierarchy newTypeHierarchy(IRegion arg0, IProgressMonitor arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public ITypeHierarchy newTypeHierarchy(IRegion arg0, WorkingCopyOwner arg1, IProgressMonitor arg2)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public ITypeHierarchy newTypeHierarchy(IType arg0, IRegion arg1, IProgressMonitor arg2)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public ITypeHierarchy newTypeHierarchy(IType arg0, IRegion arg1, WorkingCopyOwner arg2,
					IProgressMonitor arg3) throws JavaModelException {
				// FIXME Auto-generated method stub
				return null;
			}

			public IPath readOutputLocation() {
				// FIXME Auto-generated method stub
				return null;
			}

			public IClasspathEntry[] readRawClasspath() {
				// FIXME Auto-generated method stub
				return null;
			}

			public void setOption(String arg0, String arg1) {
				// FIXME Auto-generated method stub
				
			}

			public void setOptions(Map<String, String> arg0) {
				// FIXME Auto-generated method stub
				
			}

			public void setOutputLocation(IPath arg0, IProgressMonitor arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void setRawClasspath(IClasspathEntry[] arg0, IProgressMonitor arg1) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void setRawClasspath(IClasspathEntry[] arg0, boolean arg1, IProgressMonitor arg2)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void setRawClasspath(IClasspathEntry[] arg0, IPath arg1, IProgressMonitor arg2)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void setRawClasspath(IClasspathEntry[] arg0, IPath arg1, boolean arg2, IProgressMonitor arg3)
					throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}

			public void setRawClasspath(IClasspathEntry[] arg0, IClasspathEntry[] arg1, IPath arg2,
					IProgressMonitor arg3) throws JavaModelException {
				// FIXME Auto-generated method stub
				
			}
			
		};
	}
}
