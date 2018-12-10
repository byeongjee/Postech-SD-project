package showCodeSmellInformation_Tab;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.*;
import org.junit.runner.RunWith;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IWorkingCopy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
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

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.CoreException;

@RunWith(SWTBotJunit4ClassRunner.class)
public class SpeculativeGeneralityTest {
	private static SWTWorkbenchBot bot;
	private static boolean flagProjectOn = false;
	
	private static void openPackageExplorer() {
		bot.menu("Window").menu("Show View").menu("Other...").click();
		SWTBotShell dialog = bot.shell("Show View");
		dialog.activate();
		bot.tree().getTreeItem("Java").expand().getNode("Package Explorer").doubleClick();
	}
	
	@BeforeClass
	public static void initBot() throws CoreException {
		bot = new SWTWorkbenchBot();
		//bot.viewByTitle("Welcome").close();
		openPackageExplorer();
	}

	@AfterClass
	public static void afterClass() throws CoreException {
		bot.resetWorkbench();
	}
	
	@Test
	public void testOpenSpeculativeGeneralityTab() {
		bot.menu("JDe5dorant").menu("Speculative Generality").click();
		bot.viewByTitle("Speculative Generality");
		assertTrue(bot.viewByTitle("Speculative Generality").isActive());
	}

	private void selectSGTarget() {
		SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
		packageExplorer.show();
		packageExplorer.bot().tree().getTreeItem("testProject").expand().getNode("src").expand().getNode("SpeculativeGenerality").click();
	}
	
	private void turnOnProject(int arg) throws CoreException {
		if(!flagProjectOn) {
			testProject.buildProject(arg);
			flagProjectOn = true;
		} else {
			testProject.deleteProject();
			testProject.buildProject(arg);
		}
	}

	@Test
	public void testApplyingSGDetection() throws CoreException {
		SWTBotView detectionApplier;
		
		// NoChild
		turnOnProject(0);
		selectSGTarget();
		detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
    	assertTrue(detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.NoChildInterface").isEnabled());	
    	testProject.deleteProject();
    	
    	// OneChild
    	turnOnProject(1);
		selectSGTarget();
		detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
    	assertTrue(detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.OneChildInterface").isEnabled());
    	testProject.deleteProject();
    
    	// TwoChild & Unnecessary Parameter
    	turnOnProject(3);
		selectSGTarget();
		detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
    	assertTrue(detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.TC_UnnecessaryParameter").isVisible());
    	testProject.deleteProject();
	}

	@Test
	public void testExpandingSGEntries() throws CoreException {
		turnOnProject(3);
		selectSGTarget();
		
		SWTBotView detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
		
    	detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.TC_UnnecessaryParameter").expand();
    	assertTrue(detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.TC_UnnecessaryParameter").getNode("UncessaryParameter").isEnabled());

    	testProject.deleteProject();
	}
	
	@Test
	public void testApplyingSGRefactoring_NoChildInterface() throws CoreException {
		turnOnProject(0);
		selectSGTarget();
		
		SWTBotView detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
		
		detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.NoChildInterface").select();
		detectionApplier.bot().button("TEST").click();
		
		bot.shell("Refactoring").activate();
		bot.button("Finish").click();
    	
    	// Rebuild
    	IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("testProject");
		IJavaProject javaProject = JavaCore.create(project);
		
    	ICompilationUnit _CUorigin;
		try {
			_CUorigin = testProject.getNoChildInterfaceClass().getWorkingCopy(new WorkingCopyOwner() {}, null);
			
			IBuffer _bufferOrigin = ((IOpenable) _CUorigin).getBuffer();
			String answer = "/*\r\n" + 
					"package SpeculativeGenerality;\r\n" + 
					"public interface NoChildInterface {\r\n" + 
					"	int NoChildInterface_Method(int input);\r\n" + 
					"}\r\n" + 
					"\r\n" + 
					"*/";
			assertEquals(_bufferOrigin.getContents(), answer);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
    	testProject.deleteProject();
	}
	
	@Test
	public void testApplyingSGRefactoring_OneChildInterface() throws CoreException {
		turnOnProject(1);
		selectSGTarget();
		
		SWTBotView detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
		
		detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.OneChildInterface").select();
		detectionApplier.bot().button("TEST").click();
		
		bot.shell("Refactoring").activate();
		bot.button("Finish").click();
    	
    	// Rebuild
    	IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("testProject");
		IJavaProject javaProject = JavaCore.create(project);
    	ICompilationUnit _CUorigin;
    	IBuffer _bufferOrigin;
		try {
			_CUorigin = testProject.getOneChild_InterfaceClass().getWorkingCopy(new WorkingCopyOwner() {}, null);
			_bufferOrigin = ((IOpenable) _CUorigin).getBuffer();
	    	assertTrue(_bufferOrigin.toString().contains("public class OC_Int {"));
	    	assertFalse(_bufferOrigin.toString().contains("@override") || _bufferOrigin.toString().contains("@Override"));
	    	
	    	_CUorigin = testProject.getOneChildInterfaceClass().getWorkingCopy(new WorkingCopyOwner() {}, null);
			_bufferOrigin = ((IOpenable) _CUorigin).getBuffer();
	    	assertTrue(_bufferOrigin.toString().contains("/*") && _bufferOrigin.toString().contains("*/"));
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
    	testProject.deleteProject();
	}
	
	@Test
	public void testApplyingSGRefactoring_OneChildAbstract() throws CoreException {
		turnOnProject(2);
		selectSGTarget();
		
		SWTBotView detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
		
		detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.OneChildAbstract").select();
		detectionApplier.bot().button("TEST").click();
		
		bot.shell("Refactoring").activate();
		bot.button("Finish").click();
    	
		// Rebuild
    	IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("testProject");
		IJavaProject javaProject = JavaCore.create(project);
		
    	ICompilationUnit _CUorigin;
    	IBuffer _bufferOrigin;
		try {
			_CUorigin = testProject.getOneChild_AbstractClass().getWorkingCopy(new WorkingCopyOwner() {}, null);
			_bufferOrigin = ((IOpenable) _CUorigin).getBuffer();
	    	assertTrue(_bufferOrigin.toString().contains("public class OC_Abs"));
	    	assertFalse(_bufferOrigin.toString().contains("@override") || _bufferOrigin.toString().contains("@Override"));
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testApplyingSGRefactoring_UnnecessaryParameters() throws CoreException {
		turnOnProject(3);
		selectSGTarget();
		
		SWTBotView detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
		
		detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.TC_UnnecessaryParameter").select();
		detectionApplier.bot().button("TEST").click();
		
		bot.shell("Refactoring").activate();
		bot.button("Finish").click();
    	
    	// Rebuild
    	IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("testProject");
		IJavaProject javaProject = JavaCore.create(project);
		
    	ICompilationUnit _CUorigin;
		try {
			_CUorigin = testProject.getUnnecessaryParameterClass().getWorkingCopy(new WorkingCopyOwner() {}, null);
			
			IBuffer _bufferOrigin = ((IOpenable) _CUorigin).getBuffer();
	    	assertFalse(_bufferOrigin.toString().contains("int UncessaryParameter(int a, int b, int c)"));
	    	assertTrue(_bufferOrigin.toString().contains("int UncessaryParameter(int a)"));
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
    	testProject.deleteProject();
	}
}
