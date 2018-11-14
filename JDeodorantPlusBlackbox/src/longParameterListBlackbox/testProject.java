package longParameterListBlackbox;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;

public class testProject {
	 public static void buildProject() throws CoreException {
	    	
	    	IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	    	
	    	// Creating Project
	    	IProject project = root.getProject("testProject");
	    	
	    	project.create(null); 
	    	project.open(null);
	    	
	    	IProjectDescription description = project.getDescription();
	    	description.setNatureIds(new String[] { JavaCore.NATURE_ID });
	    	project.setDescription(description, null);
	    	
	    	IJavaProject javaProject = JavaCore.create(project); 
	    	
	    	// Creating Folder
	    	IFolder binFolder = project.getFolder("bin");
	    	binFolder.create(false, true, null);
	    	javaProject.setOutputLocation(binFolder.getFullPath(), null);
	    	
	    	IFolder sourceFolder = project.getFolder("src");
	    	sourceFolder.create(false, true, null);
	    	
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
	    			javaProject.getPackageFragmentRoot(sourceFolder).createPackageFragment("LongParameterList", false, null);
	    	StringBuffer source;
	    	
	    	source = new StringBuffer();
	    	source.append("package " + _package.getElementName() + ";\n");
	    	String strSimple = "public class Simple {\r\n" + 
	    							  	 "	public int a;\r\n" +
	    							  	 "	public int b;\r\n" +
	    							  	 "  public Simple() {\r\n" +
	    							  	 "  	a = 0;\r\n"+
	    							  	 "      b = 1;\r\n"+
	    							  	 "  }\r\n"+
	    							  	 "  public void method1() {\r\n"+
	    							  	 "  	int c = 2;\r\n"+
	    							  	 "	}\r\n	"+
	    							  	 "  public Simple method2() {\r\n"+
	    							  	 "		a = 4;\r\n"+
	    							  	 "		return this;\r\n"+
	    							  	 "  }\r\n"+
	    							  	 "  public Simple method3() {\r\n"+
	    							  	 "		return this;\r\n"+
	    							  	 "  }\r\n"+
	    							  	 "  public int method4() {\r\n"+
	    							  	 "		return a;\r\n"+
	    							  	 "  }\r\n"+
	    							  	 "}";
	    	source.append(strSimple);
			ICompilationUnit classSimple = _package.createCompilationUnit("Simple.java", source.toString(), false, null);
			
	    }
	    
	    public static void deleteProject() throws CoreException {
	    	IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	    	IProject project = root.getProject("testProject");
	    	
	    	project.delete(true, null);
	    }
}
