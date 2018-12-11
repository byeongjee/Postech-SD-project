package detectCodeSmell1_SG;

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

/**
 * Build Test Project with Code Smells(Spectulative Generaality),
 * and Match Them with Lists
 * @author JaeYeop Lee, JuYong Lee
 *
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class tester {
	private static SWTWorkbenchBot bot;
	private static boolean flagProjectOn = false;
	
	private static void openPackageExplorer() {
		bot.menu("Window").menu("Show View").menu("Other...").click();
		SWTBotShell dialog = bot.shell("Show View");
		dialog.activate();
		bot.tree().getTreeItem("Java").expand().getNode("Package Explorer").doubleClick();
	}

	public static void openSpeculativeGeneralityTab() {
		bot.menu("JDe5dorant").menu("Speculative Generality").click();
		bot.viewByTitle("Speculative Generality");
		assertTrue(bot.viewByTitle("Speculative Generality").isActive());
	}
	
	@BeforeClass
	public static void initBot() throws CoreException {
		bot = new SWTWorkbenchBot();
		//bot.viewByTitle("Welcome").close();
		openPackageExplorer();
		openSpeculativeGeneralityTab();
	}

	@AfterClass
	public static void afterClass() throws CoreException {
		bot.resetWorkbench();
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
	public void testApplyingSGDetection_NoChildInterface() throws CoreException {
		SWTBotView detectionApplier;
		
		turnOnProject(0);
		selectSGTarget();
		detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
    	assertTrue(detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.NoChildInterface").isEnabled());	
    	testProject.deleteProject();
	}

	@Test
	public void testApplyingSGDetection_OneChildAbstract() throws CoreException {
		SWTBotView detectionApplier;
		
    	turnOnProject(1);
		selectSGTarget();
		detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
		assertFalse(detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.OneChildInterface").isVisible());
    	testProject.deleteProject();
	}
	
	@Test
	public void testApplyingSGDetection_UnnecessaryParmeter() throws CoreException {
		SWTBotView detectionApplier;
		
    	turnOnProject(3);
		selectSGTarget();
		detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
    	assertTrue(detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.TC_UnnecessaryParameter").isVisible());
    	
    	// Expanding SG Entries
    	detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.TC_UnnecessaryParameter").expand();
    	assertTrue(detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.TC_UnnecessaryParameter").getNode("UncessaryParameter").isEnabled());

    	testProject.deleteProject();
	}
}
