package junittest.speculatviveGenerality;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
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
		
		IFile _ifile = new IFile() {
			private IPath _path = new IPath() {
				public Object clone() {
					return null;
				}
				
				public String toString() {
					return "./src/junittest/speculatviveGenerality/test.txt";
					
				}
				
				public IPath addFileExtension(String extension) {
					// TODO Auto-generated method stub
					return null;
				}

				public IPath addTrailingSeparator() {
					// TODO Auto-generated method stub
					return null;
				}

				public IPath append(String path) {
					// TODO Auto-generated method stub
					return null;
				}

				public IPath append(IPath path) {
					// TODO Auto-generated method stub
					return null;
				}

				public String getDevice() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getFileExtension() {
					// TODO Auto-generated method stub
					return null;
				}

				public boolean hasTrailingSeparator() {
					// TODO Auto-generated method stub
					return false;
				}

				public boolean isAbsolute() {
					// TODO Auto-generated method stub
					return false;
				}

				public boolean isEmpty() {
					// TODO Auto-generated method stub
					return false;
				}

				public boolean isPrefixOf(IPath anotherPath) {
					// TODO Auto-generated method stub
					return false;
				}

				public boolean isRoot() {
					// TODO Auto-generated method stub
					return false;
				}

				public boolean isUNC() {
					// TODO Auto-generated method stub
					return false;
				}

				public boolean isValidPath(String path) {
					// TODO Auto-generated method stub
					return false;
				}

				public boolean isValidSegment(String segment) {
					// TODO Auto-generated method stub
					return false;
				}

				public String lastSegment() {
					// TODO Auto-generated method stub
					return null;
				}

				public IPath makeAbsolute() {
					// TODO Auto-generated method stub
					return null;
				}

				public IPath makeRelative() {
					// TODO Auto-generated method stub
					return null;
				}

				public IPath makeRelativeTo(IPath base) {
					// TODO Auto-generated method stub
					return null;
				}

				public IPath makeUNC(boolean toUNC) {
					// TODO Auto-generated method stub
					return null;
				}

				public int matchingFirstSegments(IPath anotherPath) {
					// TODO Auto-generated method stub
					return 0;
				}

				public IPath removeFileExtension() {
					// TODO Auto-generated method stub
					return null;
				}

				public IPath removeFirstSegments(int count) {
					// TODO Auto-generated method stub
					return null;
				}

				public IPath removeLastSegments(int count) {
					// TODO Auto-generated method stub
					return null;
				}

				public IPath removeTrailingSeparator() {
					// TODO Auto-generated method stub
					return null;
				}

				public String segment(int index) {
					// TODO Auto-generated method stub
					return null;
				}

				public int segmentCount() {
					// TODO Auto-generated method stub
					return 0;
				}

				public String[] segments() {
					// TODO Auto-generated method stub
					return null;
				}

				public IPath setDevice(String device) {
					// TODO Auto-generated method stub
					return null;
				}

				public File toFile() {
					// TODO Auto-generated method stub
					return null;
				}

				public String toOSString() {
					// TODO Auto-generated method stub
					return null;
				}

				public String toPortableString() {
					// TODO Auto-generated method stub
					return null;
				}

				public IPath uptoSegment(int count) {
					// TODO Auto-generated method stub
					return null;
				}
				
			};
			
			public void accept(IResourceProxyVisitor visitor, int memberFlags) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void accept(IResourceProxyVisitor visitor, int depth, int memberFlags) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void accept(IResourceVisitor visitor) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void accept(IResourceVisitor visitor, int depth, boolean includePhantoms) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void accept(IResourceVisitor visitor, int depth, int memberFlags) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void clearHistory(IProgressMonitor monitor) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void copy(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void copy(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void copy(IProjectDescription description, boolean force, IProgressMonitor monitor)
					throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void copy(IProjectDescription description, int updateFlags, IProgressMonitor monitor)
					throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public IMarker createMarker(String type) throws CoreException {
				// TODO Auto-generated method stub
				return null;
			}

			public IResourceProxy createProxy() {
				// TODO Auto-generated method stub
				return null;
			}

			public void delete(boolean force, IProgressMonitor monitor) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void delete(int updateFlags, IProgressMonitor monitor) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void deleteMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public boolean exists() {
				// TODO Auto-generated method stub
				return false;
			}

			public IMarker findMarker(long id) throws CoreException {
				// TODO Auto-generated method stub
				return null;
			}

			public IMarker[] findMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {
				// TODO Auto-generated method stub
				return null;
			}

			public int findMaxProblemSeverity(String type, boolean includeSubtypes, int depth) throws CoreException {
				// TODO Auto-generated method stub
				return 0;
			}

			public String getFileExtension() {
				// TODO Auto-generated method stub
				return null;
			}

			public long getLocalTimeStamp() {
				// TODO Auto-generated method stub
				return 0;
			}

			public IPath getLocation() {
				// TODO Auto-generated method stub
				return this._path;
			}

			public URI getLocationURI() {
				// TODO Auto-generated method stub
				return null;
			}

			public IMarker getMarker(long id) {
				// TODO Auto-generated method stub
				return null;
			}

			public long getModificationStamp() {
				// TODO Auto-generated method stub
				return 0;
			}

			public IPathVariableManager getPathVariableManager() {
				// TODO Auto-generated method stub
				return null;
			}

			public IContainer getParent() {
				// TODO Auto-generated method stub
				return null;
			}

			public Map<QualifiedName, String> getPersistentProperties() throws CoreException {
				// TODO Auto-generated method stub
				return null;
			}

			public String getPersistentProperty(QualifiedName key) throws CoreException {
				// TODO Auto-generated method stub
				return null;
			}

			public IProject getProject() {
				// TODO Auto-generated method stub
				return null;
			}

			public IPath getProjectRelativePath() {
				// TODO Auto-generated method stub
				return null;
			}

			public IPath getRawLocation() {
				// TODO Auto-generated method stub
				return null;
			}

			public URI getRawLocationURI() {
				// TODO Auto-generated method stub
				return null;
			}

			public ResourceAttributes getResourceAttributes() {
				// TODO Auto-generated method stub
				return null;
			}

			public Map<QualifiedName, Object> getSessionProperties() throws CoreException {
				// TODO Auto-generated method stub
				return null;
			}

			public Object getSessionProperty(QualifiedName key) throws CoreException {
				// TODO Auto-generated method stub
				return null;
			}

			public int getType() {
				// TODO Auto-generated method stub
				return 0;
			}

			public IWorkspace getWorkspace() {
				// TODO Auto-generated method stub
				return null;
			}

			public boolean isAccessible() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isDerived() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isDerived(int options) {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isHidden() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isHidden(int options) {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isLinked() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isVirtual() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isLinked(int options) {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isLocal(int depth) {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isPhantom() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isSynchronized(int depth) {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isTeamPrivateMember() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isTeamPrivateMember(int options) {
				// TODO Auto-generated method stub
				return false;
			}

			public void move(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void move(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void move(IProjectDescription description, boolean force, boolean keepHistory, IProgressMonitor monitor)
					throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void move(IProjectDescription description, int updateFlags, IProgressMonitor monitor)
					throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void refreshLocal(int depth, IProgressMonitor monitor) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void revertModificationStamp(long value) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void setDerived(boolean isDerived) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void setDerived(boolean isDerived, IProgressMonitor monitor) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void setHidden(boolean isHidden) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void setLocal(boolean flag, int depth, IProgressMonitor monitor) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public long setLocalTimeStamp(long value) throws CoreException {
				// TODO Auto-generated method stub
				return 0;
			}

			public void setPersistentProperty(QualifiedName key, String value) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void setReadOnly(boolean readOnly) {
				// TODO Auto-generated method stub
				
			}

			public void setResourceAttributes(ResourceAttributes attributes) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void setSessionProperty(QualifiedName key, Object value) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void setTeamPrivateMember(boolean isTeamPrivate) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void touch(IProgressMonitor monitor) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public <T> T getAdapter(Class<T> adapter) {
				// TODO Auto-generated method stub
				return null;
			}

			public boolean contains(ISchedulingRule rule) {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isConflicting(ISchedulingRule rule) {
				// TODO Auto-generated method stub
				return false;
			}

			public void appendContents(InputStream source, boolean force, boolean keepHistory, IProgressMonitor monitor)
					throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void appendContents(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void create(InputStream source, boolean force, IProgressMonitor monitor) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void create(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void createLink(IPath localLocation, int updateFlags, IProgressMonitor monitor) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void createLink(URI location, int updateFlags, IProgressMonitor monitor) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void delete(boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public String getCharset() throws CoreException {
				// TODO Auto-generated method stub
				return null;
			}

			public String getCharset(boolean checkImplicit) throws CoreException {
				// TODO Auto-generated method stub
				return null;
			}

			public String getCharsetFor(Reader reader) throws CoreException {
				// TODO Auto-generated method stub
				return null;
			}

			public IContentDescription getContentDescription() throws CoreException {
				// TODO Auto-generated method stub
				return null;
			}

			public InputStream getContents() throws CoreException {
				// TODO Auto-generated method stub
				return null;
			}

			public InputStream getContents(boolean force) throws CoreException {
				// TODO Auto-generated method stub
				return null;
			}

			public int getEncoding() throws CoreException {
				// TODO Auto-generated method stub
				return 0;
			}

			public IPath getFullPath() {
				// TODO Auto-generated method stub
				return null;
			}

			public IFileState[] getHistory(IProgressMonitor monitor) throws CoreException {
				// TODO Auto-generated method stub
				return null;
			}

			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}

			public boolean isReadOnly() {
				// TODO Auto-generated method stub
				return false;
			}

			public void move(IPath destination, boolean force, boolean keepHistory, IProgressMonitor monitor)
					throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void setCharset(String newCharset) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void setCharset(String newCharset, IProgressMonitor monitor) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void setContents(InputStream source, boolean force, boolean keepHistory, IProgressMonitor monitor)
					throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void setContents(IFileState source, boolean force, boolean keepHistory, IProgressMonitor monitor)
					throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void setContents(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {
				// TODO Auto-generated method stub
				
			}

			public void setContents(IFileState source, int updateFlags, IProgressMonitor monitor) throws CoreException {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		this.rootClassObject.setIFile(_ifile);
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
		
		// Set Content Explicitly
		answer = new ArrayList<String>();
		answer.add("Test");
		this.mainClassObjectCandidate.setContent(answer);
		content = this.mainClassObjectCandidate.getContent();
		
		assertEquals(answer, content);
	}
}
