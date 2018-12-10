package junittest.LongParameterList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IBufferChangedListener;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IModularClassFile;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IOrdinaryClassFile;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.Test;
import gr.uom.java.ast.ConstructorObject;
import gr.uom.java.ast.LPLMethodObject;
import gr.uom.java.ast.LPLSmellContent;
import gr.uom.java.ast.MethodObject;
import gr.uom.java.ast.TypeObject;
import gr.uom.java.jdeodorant.refactoring.views.LPLRefactorWizard;
import gr.uom.java.jdeodorant.refactoring.views.LongParameterList;

import static org.junit.jupiter.api.Assertions.*;

public class TestLPLMethodObjectTBufferFunctionality {

	String className = "ExtractedClass";
	String parameterObjName = "extractedParameter";
	List<String> extractedParameterTypes = Arrays.asList("int", "char");
	List<String> extractedParameterNames = Arrays.asList("c", "d");
	List<Integer> parameterToExtractIndexList = Arrays.asList(2, 3);
	IPackageFragment pf = getSourceIPackageFragment();
	IMethod method = getSourceIMethod();
	LPLSmellContent smellContent = getLPLSmellContent();

	@Test
	public void testFillNewParameterClass() {
		IBuffer buffer = getNewClassFileBuffer();
		LPLMethodObject.fillNewParameterClass(buffer, pf, className, extractedParameterTypes, extractedParameterNames);
		String expectedString = "package testPackage;\n" + "\n" + "public class ExtractedClass{ \n"
				+ "	private int c;\n" + "	private char d;\n" + "	public ExtractedClass(int c, char d) { \n"
				+ "		this.c = c;\n" + "		this.d = d;\n" + "\n" + "	}\n" + "	 public int getC() {\n"
				+ "		 return c;\n" + "	}\n" + "	 public char getD() {\n" + "		 return d;\n" + "	}\n"
				+ "	 public void setC(int c) {\n" + "		 this.c = c;\n" + "	}\n"
				+ "	 public void setD(char d) {\n" + "		 this.d = d;\n" + "	}\n" + "}";
		assertEquals(expectedString, buffer.getContents());
	}

	@Test
	public void testEditParameterFromBuffer() {
		IBuffer buffer = getOriginalSourceBuffer();
		String tempVarInitializeCode = LPLMethodObject.codeForInitializingTempVars(extractedParameterTypes,
				extractedParameterNames, parameterObjName);
		LPLMethodObject.editParameterFromBuffer(buffer, method, parameterToExtractIndexList, smellContent,
				tempVarInitializeCode);
		String expectedString = "package testPackage;\n" + "\n" + "public class TestClass {\n"
				+ "	public int testMethod(int a, char b, null null) {\n"
				+ "		int c = extractedParameter.getC();\n" + "		char d = extractedParameter.getD();\n" + "\n"
				+ "		return a + c;\n" + "	}\n" + "}";
		assertEquals(expectedString, buffer.getContents());
	}

	@Test
	public void testCodeForInitializingTempVars() {
		String tempVarInitializeCode = LPLMethodObject.codeForInitializingTempVars(extractedParameterTypes,
				extractedParameterNames, parameterObjName);
		String expectedString = "\n		int c = extractedParameter.getC();\n"
				+ "		char d = extractedParameter.getD();\n";
		assertEquals(expectedString, tempVarInitializeCode);
	}

	private static LPLSmellContent getLPLSmellContent() {
		LPLSmellContent smellContent = new LPLSmellContent(null, null, null, null);
		return smellContent;
	}

	// only getsourceRange().getOffset() is overridden
	private static IMethod getSourceIMethod() {
		IMethod method = new IMethod() {

			public String[] getCategories() throws JavaModelException {

				return null;
			}

			public IClassFile getClassFile() {

				return null;
			}

			public ICompilationUnit getCompilationUnit() {

				return null;
			}

			public IType getDeclaringType() {

				return null;
			}

			public int getFlags() throws JavaModelException {

				return 0;
			}

			public ISourceRange getJavadocRange() throws JavaModelException {

				return null;
			}

			public int getOccurrenceCount() {

				return 0;
			}

			public IType getType(String arg0, int arg1) {

				return null;
			}

			public ITypeRoot getTypeRoot() {

				return null;
			}

			public boolean isBinary() {

				return false;
			}

			public boolean exists() {

				return false;
			}

			public IJavaElement getAncestor(int arg0) {

				return null;
			}

			public String getAttachedJavadoc(IProgressMonitor arg0) throws JavaModelException {

				return null;
			}

			public IResource getCorrespondingResource() throws JavaModelException {

				return null;
			}

			public int getElementType() {

				return 0;
			}

			public String getHandleIdentifier() {

				return null;
			}

			public IJavaModel getJavaModel() {

				return null;
			}

			public IJavaProject getJavaProject() {

				return null;
			}

			public IOpenable getOpenable() {

				return null;
			}

			public IJavaElement getParent() {

				return null;
			}

			public IPath getPath() {

				return null;
			}

			public IJavaElement getPrimaryElement() {

				return null;
			}

			public IResource getResource() {

				return null;
			}

			public ISchedulingRule getSchedulingRule() {

				return null;
			}

			public IResource getUnderlyingResource() throws JavaModelException {

				return null;
			}

			public boolean isReadOnly() {

				return false;
			}

			public boolean isStructureKnown() throws JavaModelException {

				return false;
			}

			public <T> T getAdapter(Class<T> adapter) {

				return null;
			}

			public ISourceRange getNameRange() throws JavaModelException {

				return null;
			}

			public String getSource() throws JavaModelException {

				return null;
			}

			public ISourceRange getSourceRange() throws JavaModelException {
				return new ISourceRange() {

					public int getLength() {
						// for test
						return 0;
					}

					public int getOffset() {
						// for test
						return 0;
					}

				};
			}

			public void copy(IJavaElement arg0, IJavaElement arg1, String arg2, boolean arg3, IProgressMonitor arg4)
					throws JavaModelException {

			}

			public void delete(boolean arg0, IProgressMonitor arg1) throws JavaModelException {

			}

			public void move(IJavaElement arg0, IJavaElement arg1, String arg2, boolean arg3, IProgressMonitor arg4)
					throws JavaModelException {

			}

			public void rename(String arg0, boolean arg1, IProgressMonitor arg2) throws JavaModelException {

			}

			public IJavaElement[] getChildren() throws JavaModelException {

				return null;
			}

			public boolean hasChildren() throws JavaModelException {

				return false;
			}

			public IAnnotation getAnnotation(String arg0) {

				return null;
			}

			public IAnnotation[] getAnnotations() throws JavaModelException {

				return null;
			}

			public IMemberValuePair getDefaultValue() throws JavaModelException {

				return null;
			}

			public String getElementName() {

				return null;
			}

			public String[] getExceptionTypes() throws JavaModelException {

				return null;
			}

			public String getKey() {

				return null;
			}

			public int getNumberOfParameters() {

				return 0;
			}

			public String[] getParameterNames() throws JavaModelException {

				return null;
			}

			public String[] getParameterTypes() {

				return null;
			}

			public ILocalVariable[] getParameters() throws JavaModelException {

				return null;
			}

			public String[] getRawParameterNames() throws JavaModelException {

				return null;
			}

			public String getReturnType() throws JavaModelException {

				return null;
			}

			public String getSignature() throws JavaModelException {

				return null;
			}

			public ITypeParameter getTypeParameter(String arg0) {

				return null;
			}

			public String[] getTypeParameterSignatures() throws JavaModelException {

				return null;
			}

			public ITypeParameter[] getTypeParameters() throws JavaModelException {

				return null;
			}

			public boolean isConstructor() throws JavaModelException {

				return false;
			}

			public boolean isLambdaMethod() {

				return false;
			}

			public boolean isMainMethod() throws JavaModelException {

				return false;
			}

			public boolean isResolved() {

				return false;
			}

			public boolean isSimilar(IMethod arg0) {

				return false;
			}

		};
		return method;
	}

	// only getElementName is implemented. other methods are non-functioning
	private static IPackageFragment getSourceIPackageFragment() {
		IPackageFragment ipf = new IPackageFragment() {

			public IJavaElement[] getChildren() throws JavaModelException {

				return null;
			}

			public boolean hasChildren() throws JavaModelException {

				return false;
			}

			public boolean exists() {

				return false;
			}

			public IJavaElement getAncestor(int arg0) {

				return null;
			}

			public String getAttachedJavadoc(IProgressMonitor arg0) throws JavaModelException {

				return null;
			}

			public IResource getCorrespondingResource() throws JavaModelException {

				return null;
			}

			public int getElementType() {

				return 0;
			}

			public String getHandleIdentifier() {

				return null;
			}

			public IJavaModel getJavaModel() {

				return null;
			}

			public IJavaProject getJavaProject() {

				return null;
			}

			public IOpenable getOpenable() {

				return null;
			}

			public IJavaElement getParent() {

				return null;
			}

			public IPath getPath() {

				return null;
			}

			public IJavaElement getPrimaryElement() {

				return null;
			}

			public IResource getResource() {

				return null;
			}

			public ISchedulingRule getSchedulingRule() {

				return null;
			}

			public IResource getUnderlyingResource() throws JavaModelException {

				return null;
			}

			public boolean isReadOnly() {

				return false;
			}

			public boolean isStructureKnown() throws JavaModelException {

				return false;
			}

			public <T> T getAdapter(Class<T> adapter) {

				return null;
			}

			public void close() throws JavaModelException {

			}

			public String findRecommendedLineSeparator() throws JavaModelException {

				return null;
			}

			public IBuffer getBuffer() throws JavaModelException {

				return null;
			}

			public boolean hasUnsavedChanges() throws JavaModelException {

				return false;
			}

			public boolean isConsistent() throws JavaModelException {

				return false;
			}

			public boolean isOpen() {

				return false;
			}

			public void makeConsistent(IProgressMonitor arg0) throws JavaModelException {

			}

			public void open(IProgressMonitor arg0) throws JavaModelException {

			}

			public void save(IProgressMonitor arg0, boolean arg1) throws JavaModelException {

			}

			public void copy(IJavaElement arg0, IJavaElement arg1, String arg2, boolean arg3, IProgressMonitor arg4)
					throws JavaModelException {

			}

			public void delete(boolean arg0, IProgressMonitor arg1) throws JavaModelException {

			}

			public void move(IJavaElement arg0, IJavaElement arg1, String arg2, boolean arg3, IProgressMonitor arg4)
					throws JavaModelException {

			}

			public void rename(String arg0, boolean arg1, IProgressMonitor arg2) throws JavaModelException {

			}

			public boolean containsJavaResources() throws JavaModelException {

				return false;
			}

			public ICompilationUnit createCompilationUnit(String arg0, String arg1, boolean arg2, IProgressMonitor arg3)
					throws JavaModelException {

				return null;
			}

			public IClassFile[] getAllClassFiles() throws JavaModelException {

				return null;
			}

			public IClassFile getClassFile(String arg0) {

				return null;
			}

			public IClassFile[] getClassFiles() throws JavaModelException {

				return null;
			}

			public ICompilationUnit getCompilationUnit(String arg0) {

				return null;
			}

			public ICompilationUnit[] getCompilationUnits() throws JavaModelException {

				return null;
			}

			public ICompilationUnit[] getCompilationUnits(WorkingCopyOwner arg0) throws JavaModelException {

				return null;
			}

			public String getElementName() {
				// for test
				return "testPackage";
			}

			public int getKind() throws JavaModelException {

				return 0;
			}

			public IModularClassFile getModularClassFile() {

				return null;
			}

			public Object[] getNonJavaResources() throws JavaModelException {

				return null;
			}

			public IOrdinaryClassFile getOrdinaryClassFile(String arg0) {

				return null;
			}

			public IOrdinaryClassFile[] getOrdinaryClassFiles() throws JavaModelException {

				return null;
			}

			public boolean hasSubpackages() throws JavaModelException {

				return false;
			}

			public boolean isDefaultPackage() {

				return false;
			}

		};
		return ipf;
	}

	private static IBuffer getNewClassFileBuffer() {
		IBuffer mockBuffer = new IBuffer() {

			private String content = "";

			public void addBufferChangedListener(IBufferChangedListener arg0) {
			}

			public void append(char[] arg0) {
			}

			public void append(String string) {
				content += string;
			}

			public void close() {
			}

			public char getChar(int i) {

				return content.charAt(i);
			}

			public char[] getCharacters() {
				return null;
			}

			public String getContents() {
				return content;
			}

			public int getLength() {
				return 0;
			}

			public IOpenable getOwner() {
				return null;
			}

			public String getText(int arg0, int arg1) throws IndexOutOfBoundsException {
				return null;
			}

			public IResource getUnderlyingResource() {
				return null;
			}

			public boolean hasUnsavedChanges() {
				return false;
			}

			public boolean isClosed() {
				return false;
			}

			public boolean isReadOnly() {

				return false;
			}

			public void removeBufferChangedListener(IBufferChangedListener arg0) {

			}

			public void replace(int arg0, int arg1, char[] arg2) {

			}

			public void replace(int startIdx, int length, String replacement) {
				content = content.substring(0, startIdx) + replacement + content.substring(startIdx + length);
			}

			public void save(IProgressMonitor arg0, boolean arg1) throws JavaModelException {

			}

			public void setContents(char[] arg0) {

			}

			public void setContents(String arg0) {

			}
		};
		return mockBuffer;
	}

	private static IBuffer getOriginalSourceBuffer() {
		IBuffer mockBuffer = new IBuffer() {
			private String content = "package testPackage;\n" + "\n" + "public class TestClass {\n"
					+ "	public int testMethod(int a, char b, int c, char d) {\n" + "		return a + c;\n" + "	}\n"
					+ "}";

			public void addBufferChangedListener(IBufferChangedListener arg0) {
			}

			public void append(char[] arg0) {
			}

			public void append(String arg0) {
			}

			public void close() {
			}

			public char getChar(int i) {

				return content.charAt(i);
			}

			public char[] getCharacters() {
				return null;
			}

			public String getContents() {
				return content;
			}

			public int getLength() {
				return 0;
			}

			public IOpenable getOwner() {
				return null;
			}

			public String getText(int arg0, int arg1) throws IndexOutOfBoundsException {
				return null;
			}

			public IResource getUnderlyingResource() {
				return null;
			}

			public boolean hasUnsavedChanges() {
				return false;
			}

			public boolean isClosed() {
				return false;
			}

			public boolean isReadOnly() {

				return false;
			}

			public void removeBufferChangedListener(IBufferChangedListener arg0) {

			}

			public void replace(int arg0, int arg1, char[] arg2) {

			}

			public void replace(int startIdx, int length, String replacement) {
				content = content.substring(0, startIdx) + replacement + content.substring(startIdx + length);
			}

			public void save(IProgressMonitor arg0, boolean arg1) throws JavaModelException {

			}

			public void setContents(char[] arg0) {

			}

			public void setContents(String arg0) {

			}
		};
		return mockBuffer;
	}
}
