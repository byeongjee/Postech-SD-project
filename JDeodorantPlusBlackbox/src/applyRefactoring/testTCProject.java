package applyRefactoring;

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
 
public class testTCProject {
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
    	
    	// Creating Package 
    	IPackageFragment _package = 
    			javaProject.getPackageFragmentRoot(sourceFolder).createPackageFragment("TypeChecking", false, null);
    	StringBuffer source;
    	
    	// Generate Java Class : Simple
    	source = new StringBuffer();
    	source.append("package " + _package.getElementName() + ";\n");
    	String strSimple = "public class Simple {\r\n" + 
    							  	 "	public int a;\r\n" +
    							  	 "	public int b;\r\n" +
    							  	 "  public Object test(int type) {\r\n" +
    							  	 "		switch (type){\n"+
    							  	 "		case 1:\n"+
    							  	 "			return 5;\n"+
    							  	 "  	case 3:\n"+
    							  	 "			return 2;\r\n"+
    							  	 "		}\n"+
    							  	 "		return null;\r\n"+
    							  	 "  }\r\n"+
    							  	 "}";
    	source.append(strSimple);
		ICompilationUnit classSimple = _package.createCompilationUnit("Simple.java", source.toString(), false, null);
		
		// Generate Java Class : Simple2
		source = new StringBuffer();
    	source.append("package " + _package.getElementName() + ";\n");
    	String strSimple2 = "import java.util.List;\r\n"+
    			"import java.util.ArrayList;\r\n\r\n"+
    			"public class Simple2 {\r\n" + 
    			"	public Simple test;\r\n" +
    			"	public Simple test3;\r\n" +
    			"	public Simple2(){\r\n" +
    			"		test = new Simple();\r\n" +
    			"		test3 = new Simple();\r\n" +
    			"	}\r\n" +
    			"	public Simple test_message(int a, int b){\r\n" +
    			"		return test;" +
    			"	}\r\n" +
    			"	public void test_message2(){\r\n" +
    			"		int a = test_message(1,2).method2().method3().method4();\r\n" +
    			"		test_message(test_message(5,6).method2().method3().method4(),7);\r\n" +
    			"		int b3 = test.method2().method3().method4();\r\n" +
    			"		int dd = test3.method3().method4();\r\n" +
    			"		Simple test2 = new Simple();\r\n" +
    			"		test2.method2().method3();\r\n" +
    			"		test_message(3,4).method2().method3();\r\n" +
    			"		List<String> str = new ArrayList<String> ();\r\n"+
    			"       str.add(\"HelloIt'sMe\");\r\n" + 
    			"       str.get(0).substring(0, 2).substring(6).length();\r\n" + 
    			"	}\r\n" +
    			"	public void test_message3(){\r\n" +
    			"		int a = test_message(11,22).method2().method3().method4();\r\n" +
    			"	}\r\n" + 
    			"}";
    	source.append(strSimple2);
    	//ICompilationUnit classOneChildAbstract = _package.createCompilationUnit("Simple2.java", source.toString(), false, null);
		
    }
    
    public static void deleteProject() throws CoreException {
    	IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    	IProject project = root.getProject("testProject");
    	
    	project.delete(true, null);
    }
}
