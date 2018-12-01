package speculativeGeneralityBlackbox;

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
		bot.menu("Bad Smells").menu("Speculative Generality").click();
		bot.viewByTitle("Speculative Generality");
		assertTrue(bot.viewByTitle("Speculative Generality").isActive());
	}

	private void selectSGTarget() {
		SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
		packageExplorer.show();
		packageExplorer.bot().tree().getTreeItem("testProject").expand().getNode("src").expand().getNode("SpeculativeGenerality").click();
	}
	
	@Test
	public void testApplyingSGDetection() throws CoreException {
		SWTBotView detectionApplier;
		
		// NoChild
		testProject.buildProject(0);
		selectSGTarget();
		detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
    	assertTrue(detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.NoChildInterface").isEnabled());	
    	testProject.deleteProject();
    	
    	// OneChild
    	testProject.buildProject(1);
		selectSGTarget();
		detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
    	assertTrue(detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.OneChildInterface").isEnabled());
    	testProject.deleteProject();
    
    	// TwoChild & Unnecessary Parameter
    	testProject.buildProject(3);
		selectSGTarget();
		detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
    	assertTrue(detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.TC_UnnecessaryParameter").isVisible());
    	testProject.deleteProject();
	}

	@Test
	public void testExpandingSGEntries() throws CoreException {
		testProject.buildProject(3);
		selectSGTarget();
		
		SWTBotView detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
		
    	detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.TC_UnnecessaryParameter").expand();
    	assertTrue(detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.TC_UnnecessaryParameter").getNode("UncessaryParameter").isEnabled());

    	testProject.deleteProject();
	}
	
	@Ignore
	@Test
	public void testApplyingSGRefactoring_NoChildInterface() throws CoreException {
		testProject.buildProject(0);
		selectSGTarget();
		
		SWTBotView detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
		
		detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.TC_UnnecessaryParameter").select();
		detectionApplier.bot().button("TEST").click();
    	
    	// Rebuild
    	IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("testProject");
		IJavaProject javaProject = JavaCore.create(project);
		
    	ICompilationUnit _CUorigin;
		try {
			_CUorigin = testProject.getUnnecessaryParameterClass().getWorkingCopy(new WorkingCopyOwner() {}, null);
			
			IBuffer _bufferOrigin = ((IOpenable) _CUorigin).getBuffer();
			String answer = "/*" + "public interface NoChildInterface {\r\n" 
						+ "\t int NoChildInterface_Method(int input);\r\n"
						+ "}" + "*/";
	    	assertEquals(_bufferOrigin.toString(), answer);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
    	testProject.deleteProject();
	}
	
	@Ignore
	@Test
	public void testApplyingSGRefactoring_OneChildAbstract() throws CoreException {
		testProject.buildProject(1);
		selectSGTarget();
		
		SWTBotView detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
		
		detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.TC_UnnecessaryParameter").select();
		detectionApplier.bot().button("TEST").click();
    	
    	// Rebuild
    	IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("testProject");
		IJavaProject javaProject = JavaCore.create(project);
		
    	ICompilationUnit _CUorigin;
		try {
			_CUorigin = testProject.getUnnecessaryParameterClass().getWorkingCopy(new WorkingCopyOwner() {}, null);
			
			IBuffer _bufferOrigin = ((IOpenable) _CUorigin).getBuffer();
	    	assertTrue(_bufferOrigin.toString().contains("public class OC_abs {"));
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
    	testProject.deleteProject();
	}
	
	@Ignore
	@Test
	public void testApplyingSGRefactoring_OneChildInterface() throws CoreException {
		testProject.buildProject(2);
		selectSGTarget();
		
		SWTBotView detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
		
		detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.TC_UnnecessaryParameter").select();
		detectionApplier.bot().button("TEST").click();
    	
    	// Rebuild
    	IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("testProject");
		IJavaProject javaProject = JavaCore.create(project);
		
    	ICompilationUnit _CUorigin;
		try {
			_CUorigin = testProject.getUnnecessaryParameterClass().getWorkingCopy(new WorkingCopyOwner() {}, null);
			
			IBuffer _bufferOrigin = ((IOpenable) _CUorigin).getBuffer();
	    	assertTrue(_bufferOrigin.toString().contains("public class OC_int {"));
	    	assertFalse(_bufferOrigin.toString().contains("@override") || _bufferOrigin.toString().contains("@Override"));
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
    	testProject.deleteProject();
	}
	
	@Test
	public void testApplyingSGRefactoring_UnnecessaryParameters() throws CoreException {
		testProject.buildProject(3);
		selectSGTarget();
		
		SWTBotView detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
		
		detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.TC_UnnecessaryParameter").select();
		detectionApplier.bot().button("TEST").click();
    	
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
