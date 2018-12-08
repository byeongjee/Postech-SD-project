package messageChain;
import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
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
import org.eclipse.jdt.core.CompletionRequestor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IBufferChangedListener;
import org.eclipse.jdt.core.IBufferFactory;
import org.eclipse.jdt.core.ICodeCompletionRequestor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ICompletionRequestor;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IProblemRequestor;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.UndoEdit;
import org.junit.Test;

import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.SystemObject;
import gr.uom.java.jdeodorant.refactoring.manipulators.MessageChainRefactoring;
import gr.uom.java.jdeodorant.refactoring.views.MessageChain;
import gr.uom.java.jdeodorant.refactoring.views.MessageChain.ViewContentProvider;
import gr.uom.java.jdeodorant.refactoring.views.MessageChain.ViewLabelProvider;
import gr.uom.java.jdeodorant.refactoring.views.MessageChainStructure;
public class messageChainJUnitTest {

   public ViewContentProvider makeViewContentProvider() {
      MessageChain msgChain = new MessageChain();
      return msgChain.new ViewContentProvider();
   }
   
   public ViewLabelProvider makeViewLabelProvider() {
	   MessageChain msgChain = new MessageChain();
	   return msgChain.new ViewLabelProvider(); 
   }
   @Test
    public void testgetChildren() {
      MessageChainStructure parent = new MessageChainStructure("ParentClass");
      MessageChainStructure child = new MessageChainStructure(15, parent, "A().B().C()");
      assertTrue(parent.addChild(child));
      
      Object[] result = makeViewContentProvider().getChildren(parent);
      
      Object[] result2 = makeViewContentProvider().getChildren(null);
      Object[] result3 = makeViewContentProvider().getChildren("String");
      
       assertTrue(((MessageChainStructure) result[0]).getName()=="A().B().C()");
       assertTrue(((MessageChainStructure) result[0]).getStart()==15);
       assertTrue(result2.length == 0);
       assertTrue(result3.length == 0);
       
   }
   
   @Test
   public void testgetParent() {
      MessageChainStructure parent = new MessageChainStructure("ParentClass");
      MessageChainStructure child = new MessageChainStructure(15, parent, "A().B().C()");
      assertTrue(parent.addChild(child));
      
      Object result = makeViewContentProvider().getParent(child);
       assertTrue(((MessageChainStructure) result).getName()=="ParentClass");
       assertTrue(((MessageChainStructure) result).getStart()==-1);
   }
   
   @Test
   public void testhasChildren() {
      MessageChainStructure parent = new MessageChainStructure("ParentClass");
      MessageChainStructure child = new MessageChainStructure(15, parent, "A().B().C()", 4);
      assertTrue(parent.addChild(child));
      
      boolean resultTrue = makeViewContentProvider().hasChildren(parent);
      boolean resultFalse = makeViewContentProvider().hasChildren(child);
      Object[] children = makeViewContentProvider().getChildren(parent);
      int length = ((MessageChainStructure)children[0]).getLength();
      parent.removeChild(child);
      int size = parent.getSize();
       assertTrue(resultTrue);
       assertFalse(resultFalse);
       assertTrue(length == 4);
       assertTrue(size == 0);
   }
   
   @Test
   public void testgetElement() {
      MessageChainStructure parent = new MessageChainStructure("ParentClass");
      MessageChainStructure child = new MessageChainStructure(15, parent, "A().B().C()");
      assertTrue(parent.addChild(child));
      MessageChainStructure[] target = new MessageChainStructure[1];
      target[0] = parent;
      MessageChain msgChain = new MessageChain();
      ViewContentProvider contentProvider =msgChain.new ViewContentProvider();
      msgChain.targets = target;
      Object[] result = contentProvider.getElements(parent);
      
      assertTrue(((MessageChainStructure) result[0]).getName()=="ParentClass");
      assertTrue(((MessageChainStructure) result[0]).getStart()==-1);
      contentProvider.dispose();
      msgChain.targets = null;
      assertTrue(contentProvider.getElements(parent).length == 0);
   }
   
   @Test
   public void testViewLabelProvider() {
	   ViewLabelProvider labelProvider = makeViewLabelProvider();
	   MessageChainStructure parent = new MessageChainStructure("ParentClass");
	   MessageChainStructure child = new MessageChainStructure(15, parent, "A().B().C()", 3);
	   MessageChainStructure child2 = new MessageChainStructure(-1, parent, "A().B().C()");
	   assertTrue(parent.addChild(child));
	   assertTrue(parent.addChild(child2));
	   
	   assertTrue(labelProvider.getColumnText(child2, 0).equals(""));
	   assertTrue(labelProvider.getColumnText(child2, 2).equals(""));
	   assertTrue(labelProvider.getColumnText(child2, 1).equals("A().B().C()"));
	   assertTrue(labelProvider.getColumnText(child2, 4).equals(""));
	   assertTrue(labelProvider.getColumnText(child, 0).equals("15"));
	   assertTrue(labelProvider.getColumnText("ff", 1).equals(""));
   }
   
   @Test
      public void testmakeNewMethodCode () {
        //MessageChain msgChain = new MessageChain();
         List<String> stringofArgumentType = new ArrayList<String>();
         stringofArgumentType.add("int");
         stringofArgumentType.add("double");
         stringofArgumentType.add("int");
         
         List<Integer> numOfArgumentOfEachMethod = new ArrayList<Integer>();
         numOfArgumentOfEachMethod.add(2);
         numOfArgumentOfEachMethod.add(1);
         
         List<String> stringOfMethodInvocation = new ArrayList<String>();
         stringOfMethodInvocation.add("method1");
         stringOfMethodInvocation.add("method2");
         
         //String result = msgChain.makeNewMethodCode ("newMethod", "int", stringofArgumentType, numOfArgumentOfEachMethod, stringOfMethodInvocation);
         String result = MessageChainRefactoring.makeNewMethodCode ("newMethod", "int", stringofArgumentType, numOfArgumentOfEachMethod, stringOfMethodInvocation);

         System.out.println(result);
         assertTrue(result.equals("public int newMethod(int x0, double x1, int x2) {\r\n" + 
               "\treturn method1(x0, x1).method2(x2);\r\n" + 
               "}\r\n"));
      }
   
   @Test
      public void testgetClassName () {
         String str1 = "homework5.simple";
         String str2 = "int";
         String str3 = "csed.homwork5.simple";
         
         int length1 = str1.length();
         int length2 = str2.length();
         int length3 = str3.length();
         
         String result1 = MessageChainRefactoring.getClassName(length1, str1);
         String result2 = MessageChainRefactoring.getClassName(length2, str2);
         String result3 = MessageChainRefactoring.getClassName(length3, str3);
         
         assertTrue(result1.equals("simple"));
         assertTrue(result2.equals("int"));
         assertTrue(result3.equals("simple"));
      }
   
   @Test
   public void testmakeNewRefactorCode () {
         List<String> stringofArgument = new ArrayList<String>();
         stringofArgument.add("x0");
         stringofArgument.add("x1");
         stringofArgument.add("x2");
         
         String result = MessageChainRefactoring.makeNewRefactorCode ("newMethod", stringofArgument);
         assertTrue(result.equals("newMethod(x0, x1, x2)"));
      }
   
   @Test
   public void testgetModifyPosition() {
      IBuffer buffer = new IBuffer() {

         public void addBufferChangedListener(IBufferChangedListener arg0) {
            // TODO Auto-generated method stub
            
         }

         public void append(char[] arg0) {
            // TODO Auto-generated method stub
            
         }

         public void append(String arg0) {
            // TODO Auto-generated method stub
            
         }

         public void close() {
            // TODO Auto-generated method stub
            
         }

         public char getChar(int arg0) {
            if (arg0 == 2) {
               return '}';
            } else {
               return ' ';
            }
         }

         public char[] getCharacters() {
            // TODO Auto-generated method stub
            return null;
         }

         public String getContents() {
            // TODO Auto-generated method stub
            return null;
         }

         public int getLength() {
            return 5;
         }

         public IOpenable getOwner() {
            // TODO Auto-generated method stub
            return null;
         }

         public String getText(int arg0, int arg1) throws IndexOutOfBoundsException {
            // TODO Auto-generated method stub
            return null;
         }

         public IResource getUnderlyingResource() {
            // TODO Auto-generated method stub
            return null;
         }

         public boolean hasUnsavedChanges() {
            // TODO Auto-generated method stub
            return false;
         }

         public boolean isClosed() {
            // TODO Auto-generated method stub
            return false;
         }

         public boolean isReadOnly() {
            // TODO Auto-generated method stub
            return false;
         }

         public void removeBufferChangedListener(IBufferChangedListener arg0) {
            // TODO Auto-generated method stub
            
         }

         public void replace(int arg0, int arg1, char[] arg2) {
            // TODO Auto-generated method stub
            
         }

         public void replace(int arg0, int arg1, String arg2) {
            // TODO Auto-generated method stub
            
         }

         public void save(IProgressMonitor arg0, boolean arg1) throws JavaModelException {
            // TODO Auto-generated method stub
            
         }

         public void setContents(char[] arg0) {
            // TODO Auto-generated method stub
            
         }

         public void setContents(String arg0) {
            // TODO Auto-generated method stub
            
         }};

         int pos = MessageChainRefactoring.getModifyPosition(buffer);
         assertTrue(pos==2);
   }
   
   @Test
   public void testcheckMethodIfFromRefactoring() {
	   MessageChain msgChain = new MessageChain();
	   msgChain.newRefactoringMethod = new ArrayList<String>();
	   msgChain.newRefactoringMethod.add("simple/newMethod1");
	   assertTrue(msgChain.checkMethodIfFromRefactoring("simple", "newMethod1"));
	   assertFalse(msgChain.checkMethodIfFromRefactoring("simple", "newMethod2"));
	   msgChain.newRefactoringMethod.add("simple/newMethod2");
	   assertTrue(msgChain.checkMethodIfFromRefactoring("simple", "newMethod2"));
   }
   
   @Test
   public void testmodifyCodeSmellFile() {
	   //MessageChain msgChain = new MessageChain();
	   IFile file = new IFile() {

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
			return null;
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
			
		}};
	   SystemObject sys = new SystemObject() {};
	   ClassObject cls = new ClassObject() {};
	   cls.setName("messageChain");
	   cls.setIFile(file);
	   sys.addClass(cls);
	   assertTrue(MessageChainRefactoring.getCompUnit(sys, "messageChain")==null);
   }
   
   @Test
   public void testmodifyMethodInvocationFile() {
	   final IBuffer buffer = new IBuffer() {

		public void addBufferChangedListener(IBufferChangedListener arg0) {
			// TODO Auto-generated method stub
			
		}

		public void append(char[] arg0) {
			// TODO Auto-generated method stub
			
		}

		public void append(String arg0) {
			// TODO Auto-generated method stub
			
		}

		public void close() {
			// TODO Auto-generated method stub
			
		}

		public char getChar(int arg0) {
			if(arg0 == 2) {
				return '}';
			} 
			return ' ';
		}

		public char[] getCharacters() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getContents() {
			// TODO Auto-generated method stub
			return null;
		}

		public int getLength() {
			return 5;
		}

		public IOpenable getOwner() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getText(int arg0, int arg1) throws IndexOutOfBoundsException {
			// TODO Auto-generated method stub
			return null;
		}

		public IResource getUnderlyingResource() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean hasUnsavedChanges() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isClosed() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isReadOnly() {
			// TODO Auto-generated method stub
			return false;
		}

		public void removeBufferChangedListener(IBufferChangedListener arg0) {
			// TODO Auto-generated method stub
			
		}

		public void replace(int arg0, int arg1, char[] arg2) {
			// TODO Auto-generated method stub
			
		}

		public void replace(int arg0, int arg1, String arg2) {
			assertTrue(arg0==2);
			
		}

		public void save(IProgressMonitor arg0, boolean arg1) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void setContents(char[] arg0) {
			// TODO Auto-generated method stub
			
		}

		public void setContents(String arg0) {
			// TODO Auto-generated method stub
			
		}};
	   
	   final ICompilationUnit workingCopy = new ICompilationUnit() {

		public IType findPrimaryType() {
			// TODO Auto-generated method stub
			return null;
		}

		public IJavaElement getElementAt(int arg0) throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public ICompilationUnit getWorkingCopy(WorkingCopyOwner arg0, IProgressMonitor arg1) throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean exists() {
			// TODO Auto-generated method stub
			return false;
		}

		public IJavaElement getAncestor(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public String getAttachedJavadoc(IProgressMonitor arg0) throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public IResource getCorrespondingResource() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public String getElementName() {
			// TODO Auto-generated method stub
			return null;
		}

		public int getElementType() {
			// TODO Auto-generated method stub
			return 0;
		}

		public String getHandleIdentifier() {
			// TODO Auto-generated method stub
			return null;
		}

		public IJavaModel getJavaModel() {
			// TODO Auto-generated method stub
			return null;
		}

		public IJavaProject getJavaProject() {
			// TODO Auto-generated method stub
			return null;
		}

		public IOpenable getOpenable() {
			// TODO Auto-generated method stub
			return null;
		}

		public IJavaElement getParent() {
			// TODO Auto-generated method stub
			return null;
		}

		public IPath getPath() {
			// TODO Auto-generated method stub
			return null;
		}

		public IJavaElement getPrimaryElement() {
			// TODO Auto-generated method stub
			return null;
		}

		public IResource getResource() {
			// TODO Auto-generated method stub
			return null;
		}

		public ISchedulingRule getSchedulingRule() {
			// TODO Auto-generated method stub
			return null;
		}

		public IResource getUnderlyingResource() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isReadOnly() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isStructureKnown() throws JavaModelException {
			// TODO Auto-generated method stub
			return false;
		}

		public <T> T getAdapter(Class<T> adapter) {
			// TODO Auto-generated method stub
			return null;
		}

		public IJavaElement[] getChildren() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean hasChildren() throws JavaModelException {
			// TODO Auto-generated method stub
			return false;
		}

		public void close() throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public String findRecommendedLineSeparator() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public IBuffer getBuffer() throws JavaModelException {
			return buffer;
		}

		public boolean hasUnsavedChanges() throws JavaModelException {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isConsistent() throws JavaModelException {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isOpen() {
			// TODO Auto-generated method stub
			return false;
		}

		public void makeConsistent(IProgressMonitor arg0) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void open(IProgressMonitor arg0) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void save(IProgressMonitor arg0, boolean arg1) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public ISourceRange getNameRange() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public String getSource() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public ISourceRange getSourceRange() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public void codeComplete(int arg0, ICodeCompletionRequestor arg1) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void codeComplete(int arg0, ICompletionRequestor arg1) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void codeComplete(int arg0, CompletionRequestor arg1) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void codeComplete(int arg0, CompletionRequestor arg1, IProgressMonitor arg2) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void codeComplete(int arg0, ICompletionRequestor arg1, WorkingCopyOwner arg2) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void codeComplete(int arg0, CompletionRequestor arg1, WorkingCopyOwner arg2) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void codeComplete(int arg0, CompletionRequestor arg1, WorkingCopyOwner arg2, IProgressMonitor arg3)
				throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public IJavaElement[] codeSelect(int arg0, int arg1) throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public IJavaElement[] codeSelect(int arg0, int arg1, WorkingCopyOwner arg2) throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public void commit(boolean arg0, IProgressMonitor arg1) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void destroy() {
			// TODO Auto-generated method stub
			
		}

		public IJavaElement findSharedWorkingCopy(IBufferFactory arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public IJavaElement getOriginal(IJavaElement arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public IJavaElement getOriginalElement() {
			// TODO Auto-generated method stub
			return null;
		}

		public IJavaElement getSharedWorkingCopy(IProgressMonitor arg0, IBufferFactory arg1, IProblemRequestor arg2)
				throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public IJavaElement getWorkingCopy() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public IJavaElement getWorkingCopy(IProgressMonitor arg0, IBufferFactory arg1, IProblemRequestor arg2)
				throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isBasedOn(IResource arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		public IMarker[] reconcile() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public void reconcile(boolean arg0, IProgressMonitor arg1) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void copy(IJavaElement arg0, IJavaElement arg1, String arg2, boolean arg3, IProgressMonitor arg4)
				throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void delete(boolean arg0, IProgressMonitor arg1) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void move(IJavaElement arg0, IJavaElement arg1, String arg2, boolean arg3, IProgressMonitor arg4)
				throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void rename(String arg0, boolean arg1, IProgressMonitor arg2) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public UndoEdit applyTextEdit(TextEdit arg0, IProgressMonitor arg1) throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public void becomeWorkingCopy(IProgressMonitor arg0) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void becomeWorkingCopy(IProblemRequestor arg0, IProgressMonitor arg1) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void commitWorkingCopy(boolean arg0, IProgressMonitor arg1) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public IImportDeclaration createImport(String arg0, IJavaElement arg1, IProgressMonitor arg2)
				throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public IImportDeclaration createImport(String arg0, IJavaElement arg1, int arg2, IProgressMonitor arg3)
				throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public IPackageDeclaration createPackageDeclaration(String arg0, IProgressMonitor arg1)
				throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public IType createType(String arg0, IJavaElement arg1, boolean arg2, IProgressMonitor arg3)
				throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public void discardWorkingCopy() throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public IJavaElement[] findElements(IJavaElement arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public ICompilationUnit findWorkingCopy(WorkingCopyOwner arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public IType[] getAllTypes() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public IImportDeclaration getImport(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public IImportContainer getImportContainer() {
			// TODO Auto-generated method stub
			return null;
		}

		public IImportDeclaration[] getImports() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public WorkingCopyOwner getOwner() {
			// TODO Auto-generated method stub
			return null;
		}

		public IPackageDeclaration getPackageDeclaration(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public IPackageDeclaration[] getPackageDeclarations() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public ICompilationUnit getPrimary() {
			// TODO Auto-generated method stub
			return null;
		}

		public IType getType(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public IType[] getTypes() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public ICompilationUnit getWorkingCopy(IProgressMonitor arg0) throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public ICompilationUnit getWorkingCopy(WorkingCopyOwner arg0, IProblemRequestor arg1, IProgressMonitor arg2)
				throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean hasResourceChanged() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isWorkingCopy() {
			// TODO Auto-generated method stub
			return false;
		}

		public CompilationUnit reconcile(int arg0, boolean arg1, WorkingCopyOwner arg2, IProgressMonitor arg3)
				throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public CompilationUnit reconcile(int arg0, int arg1, WorkingCopyOwner arg2, IProgressMonitor arg3)
				throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public CompilationUnit reconcile(int arg0, boolean arg1, boolean arg2, WorkingCopyOwner arg3,
				IProgressMonitor arg4) throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public void restore() throws JavaModelException {
			// TODO Auto-generated method stub
			
		}};
	   ICompilationUnit compUnit = new ICompilationUnit() {

		public IType findPrimaryType() {
			// TODO Auto-generated method stub
			return null;
		}

		public IJavaElement getElementAt(int arg0) throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public ICompilationUnit getWorkingCopy(WorkingCopyOwner arg0, IProgressMonitor arg1) throws JavaModelException {
			return workingCopy;
		}

		public boolean exists() {
			// TODO Auto-generated method stub
			return false;
		}

		public IJavaElement getAncestor(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public String getAttachedJavadoc(IProgressMonitor arg0) throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public IResource getCorrespondingResource() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public String getElementName() {
			// TODO Auto-generated method stub
			return null;
		}

		public int getElementType() {
			// TODO Auto-generated method stub
			return 0;
		}

		public String getHandleIdentifier() {
			// TODO Auto-generated method stub
			return null;
		}

		public IJavaModel getJavaModel() {
			// TODO Auto-generated method stub
			return null;
		}

		public IJavaProject getJavaProject() {
			// TODO Auto-generated method stub
			return null;
		}

		public IOpenable getOpenable() {
			// TODO Auto-generated method stub
			return null;
		}

		public IJavaElement getParent() {
			// TODO Auto-generated method stub
			return null;
		}

		public IPath getPath() {
			// TODO Auto-generated method stub
			return null;
		}

		public IJavaElement getPrimaryElement() {
			// TODO Auto-generated method stub
			return null;
		}

		public IResource getResource() {
			// TODO Auto-generated method stub
			return null;
		}

		public ISchedulingRule getSchedulingRule() {
			// TODO Auto-generated method stub
			return null;
		}

		public IResource getUnderlyingResource() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isReadOnly() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isStructureKnown() throws JavaModelException {
			// TODO Auto-generated method stub
			return false;
		}

		public <T> T getAdapter(Class<T> adapter) {
			// TODO Auto-generated method stub
			return null;
		}

		public IJavaElement[] getChildren() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean hasChildren() throws JavaModelException {
			// TODO Auto-generated method stub
			return false;
		}

		public void close() throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public String findRecommendedLineSeparator() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public IBuffer getBuffer() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean hasUnsavedChanges() throws JavaModelException {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isConsistent() throws JavaModelException {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isOpen() {
			// TODO Auto-generated method stub
			return false;
		}

		public void makeConsistent(IProgressMonitor arg0) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void open(IProgressMonitor arg0) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void save(IProgressMonitor arg0, boolean arg1) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public ISourceRange getNameRange() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public String getSource() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public ISourceRange getSourceRange() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public void codeComplete(int arg0, ICodeCompletionRequestor arg1) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void codeComplete(int arg0, ICompletionRequestor arg1) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void codeComplete(int arg0, CompletionRequestor arg1) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void codeComplete(int arg0, CompletionRequestor arg1, IProgressMonitor arg2) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void codeComplete(int arg0, ICompletionRequestor arg1, WorkingCopyOwner arg2) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void codeComplete(int arg0, CompletionRequestor arg1, WorkingCopyOwner arg2) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void codeComplete(int arg0, CompletionRequestor arg1, WorkingCopyOwner arg2, IProgressMonitor arg3)
				throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public IJavaElement[] codeSelect(int arg0, int arg1) throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public IJavaElement[] codeSelect(int arg0, int arg1, WorkingCopyOwner arg2) throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public void commit(boolean arg0, IProgressMonitor arg1) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void destroy() {
			// TODO Auto-generated method stub
			
		}

		public IJavaElement findSharedWorkingCopy(IBufferFactory arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public IJavaElement getOriginal(IJavaElement arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public IJavaElement getOriginalElement() {
			// TODO Auto-generated method stub
			return null;
		}

		public IJavaElement getSharedWorkingCopy(IProgressMonitor arg0, IBufferFactory arg1, IProblemRequestor arg2)
				throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public IJavaElement getWorkingCopy() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public IJavaElement getWorkingCopy(IProgressMonitor arg0, IBufferFactory arg1, IProblemRequestor arg2)
				throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isBasedOn(IResource arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		public IMarker[] reconcile() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public void reconcile(boolean arg0, IProgressMonitor arg1) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void copy(IJavaElement arg0, IJavaElement arg1, String arg2, boolean arg3, IProgressMonitor arg4)
				throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void delete(boolean arg0, IProgressMonitor arg1) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void move(IJavaElement arg0, IJavaElement arg1, String arg2, boolean arg3, IProgressMonitor arg4)
				throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void rename(String arg0, boolean arg1, IProgressMonitor arg2) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public UndoEdit applyTextEdit(TextEdit arg0, IProgressMonitor arg1) throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public void becomeWorkingCopy(IProgressMonitor arg0) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void becomeWorkingCopy(IProblemRequestor arg0, IProgressMonitor arg1) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public void commitWorkingCopy(boolean arg0, IProgressMonitor arg1) throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public IImportDeclaration createImport(String arg0, IJavaElement arg1, IProgressMonitor arg2)
				throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public IImportDeclaration createImport(String arg0, IJavaElement arg1, int arg2, IProgressMonitor arg3)
				throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public IPackageDeclaration createPackageDeclaration(String arg0, IProgressMonitor arg1)
				throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public IType createType(String arg0, IJavaElement arg1, boolean arg2, IProgressMonitor arg3)
				throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public void discardWorkingCopy() throws JavaModelException {
			// TODO Auto-generated method stub
			
		}

		public IJavaElement[] findElements(IJavaElement arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public ICompilationUnit findWorkingCopy(WorkingCopyOwner arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public IType[] getAllTypes() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public IImportDeclaration getImport(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public IImportContainer getImportContainer() {
			// TODO Auto-generated method stub
			return null;
		}

		public IImportDeclaration[] getImports() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public WorkingCopyOwner getOwner() {
			// TODO Auto-generated method stub
			return null;
		}

		public IPackageDeclaration getPackageDeclaration(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public IPackageDeclaration[] getPackageDeclarations() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public ICompilationUnit getPrimary() {
			// TODO Auto-generated method stub
			return null;
		}

		public IType getType(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public IType[] getTypes() throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public ICompilationUnit getWorkingCopy(IProgressMonitor arg0) throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public ICompilationUnit getWorkingCopy(WorkingCopyOwner arg0, IProblemRequestor arg1, IProgressMonitor arg2)
				throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean hasResourceChanged() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isWorkingCopy() {
			// TODO Auto-generated method stub
			return false;
		}

		public CompilationUnit reconcile(int arg0, boolean arg1, WorkingCopyOwner arg2, IProgressMonitor arg3)
				throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public CompilationUnit reconcile(int arg0, int arg1, WorkingCopyOwner arg2, IProgressMonitor arg3)
				throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public CompilationUnit reconcile(int arg0, boolean arg1, boolean arg2, WorkingCopyOwner arg3,
				IProgressMonitor arg4) throws JavaModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public void restore() throws JavaModelException {
			// TODO Auto-generated method stub
			
		}};
		
		MessageChainRefactoring.modifyMethodInvocationFile(compUnit,"void newMethod() {}");
   
   }
   
   @Test
   public void testfindMCSwithGivenName() {
	      MessageChainStructure parent = new MessageChainStructure("ParentClass");
	      MessageChainStructure child = new MessageChainStructure(15, parent, "A->B->C");
	      assertTrue(parent.addChild(child));
	      MessageChainStructure parent2 = new MessageChainStructure("ParentClass2");
	      MessageChainStructure[] target = new MessageChainStructure[1];	      
	      target[0] = parent;
	      MessageChainStructure[] target2 = new MessageChainStructure[1];
	      target2[0] = parent2;
	      MessageChain msgChain = new MessageChain();
	      msgChain.targets = target;
	      assertTrue(msgChain.findMCSwithGivenName("A->B->C").equals(child));
	      assertTrue(msgChain.findMCSwithGivenName("A->C->B")==null);
   }
   
   @Test
   public void testcheckChangePosibility() {
	   MessageChain msgChain = new MessageChain();
	   boolean result = true;
	   result = msgChain.isPossibleChange("java.asdf.as");
	   assertFalse(result);
	   result = msgChain.isPossibleChange("org.asdf.as");
	   assertFalse(result);
	   result = msgChain.isPossibleChange("simple.asdf");
	   assertTrue(result);
   }
}
