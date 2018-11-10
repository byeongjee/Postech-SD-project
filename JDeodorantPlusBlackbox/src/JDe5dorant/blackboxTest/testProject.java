package JDe5dorant.blackboxTest;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
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
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.*;
 
public class testProject {
	/**
	 *  Creating (kind of) Mock Project will be exploited during Black-Box Test
	 *  reference :: https://jaxenter.com/introduction-functional-testing-swtbot-123449.html 
	 */
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
    	
    	// Generate Java Class
    	IPackageFragment pack = 
    			javaProject.getPackageFragmentRoot(sourceFolder).createPackageFragment("homework5", false, null);
    	StringBuffer source = new StringBuffer();
    	source.append("package " + pack.getElementName() + ";\n");
    	source.append("\n public class simple{\n \t int a = 1; \n\n \t int test(int t) {\n\t\t return t; \n\t} \n}");
    	source.append("");
		ICompilationUnit _class = pack.createCompilationUnit("simple.java", source.toString(), false, null);
		
		
    }
    
    public static void deleteProject() throws CoreException {
    	IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    	IProject project = root.getProject("testProject");
    	
    	project.delete(true, null);
    }
}