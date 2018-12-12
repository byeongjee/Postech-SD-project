package showDetectedCodeSmell_Highlight;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;

public class testLPLProject {
	 public static void buildLPLProject() throws CoreException {
	    	
	    	IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	    	
	    	// Creating Project
	    	IProject project = root.getProject("testLPLProject");
	    	
	    	project.create(null); 
	    	project.open(null);
	    	
	    	IProjectDescription description = project.getDescription();
	    	description.setNatureIds(new String[] { JavaCore.NATURE_ID });
	    	project.setDescription(description, null);
	    	
	    	IJavaProject javaProject = JavaCore.create(project); 
	    	
	    	// Creating Folder
	    	IFolder binFolder = project.getFolder("bin");
	    	binFolder.create(true, true, null);
	    	javaProject.setOutputLocation(binFolder.getFullPath(), null);
	    	
	    	IFolder sourceFolder = project.getFolder("src");
	    	sourceFolder.create(true, true, null);
	    	
	    	// Creating Class-Path
	    	List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
	    	IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
	    	LibraryLocation[] locations = JavaRuntime.getLibraryLocations(vmInstall);
	    	for (LibraryLocation element : locations) {
	    	 entries.add(JavaCore.newLibraryEntry(element.getSystemLibraryPath(), null, null));
	    	}
	    	javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);
	    	
	    	// Creating Source Folder (Package)
	    	IPackageFragmentRoot fragRoot = javaProject.getPackageFragmentRoot(sourceFolder);
	    	IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
	    	IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
	    	System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
	    	newEntries[oldEntries.length] = JavaCore.newSourceEntry(fragRoot.getPath());
	    	javaProject.setRawClasspath(newEntries, null);
	    	
	    	// Creating Package 
	    	IPackageFragment _package = 
	    			javaProject.getPackageFragmentRoot(sourceFolder).createPackageFragment("LongParameterList", true, null);
	    	StringBuffer source;
	    	
	    	source = new StringBuffer();
	    	source.append("package " + _package.getElementName() + ";\n");
	    	String strSimple = "public class TestLPL {\r\n" + 
	    				"	 		public int a;\r\n" + 
	    				"	 		public int b;\r\n" + 
	    				"	 		public char c;\r\n" + 
	    				"	 		\r\n" + 
	    				"	 		public TestLPL() {\r\n" + 
	    				"	 			a = 1;\r\n" + 
	    				"	 			b = 2;\r\n" + 
	    				"	 			c = 'a';\r\n" + 
	    				"	 		}\r\n" + 
	    				"	 		public int getVal1 (int x) {\r\n" + 
	    				"	 			return a + x;\r\n" + 
	    				"	 		}\r\n" + 
	    				"	 		public int getVal2 (int x, int y) {\r\n" + 
	    				"	 			return b + x + y;\r\n" + 
	    				"	 		}\r\n" + 
	    				"	 		public int getVal3 (int x, int y, int z) {\r\n" + 
	    				"	 			return x + y + z;\r\n" + 
	    				"	 		}\r\n" + 
	    				"	 		public int getVal4 (int x, int y, int z, int w) {\r\n" + 
	    				"	 			return x + y + z + w;\r\n" + 
	    				"	 		}\r\n" + 
	    				"	 		public int getVal4_2 (int x, int y, char u, char v) {\r\n" + 
	    				"	 			return u + v;\r\n" + 
	    				"	 		}\r\n" + 
	    				"	 	}";
	    	source.append(strSimple);
			_package.createCompilationUnit("TestLPL.java", source.toString(), true, null);
			ICompilationUnit workingCopy = _package.getCompilationUnit("TestLPL.java").getWorkingCopy(new WorkingCopyOwner() {}, null);
			IBuffer buffer = workingCopy.getBuffer();
			buffer.replace(0, buffer.getLength(), "package " + _package.getElementName() + ";\n" + strSimple);
			workingCopy.reconcile(ICompilationUnit.NO_AST, false, null, null);
			workingCopy.commitWorkingCopy(false, null);
			workingCopy.discardWorkingCopy();
	    }
	    
	    public static void deleteLPLProject() throws CoreException {
	    	IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	    	IProject project = root.getProject("testProject");
	    	
	    	project.delete(true, null);
	    }
}
