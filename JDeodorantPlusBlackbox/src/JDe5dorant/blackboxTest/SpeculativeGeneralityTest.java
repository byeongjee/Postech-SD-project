package JDe5dorant.blackboxTest;

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

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
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
		bot.viewByTitle("Welcome").close();
		
		testProject.buildProject();
		openPackageExplorer();
	}

	@AfterClass
	public static void afterClass() throws CoreException {
    	bot.sleep(10000000);
		testProject.deleteProject();
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
	public void testApplyingSGDetection() {
		selectSGTarget();
		
		SWTBotView detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
    	assertTrue(detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.NoChildInterface").isEnabled());
	}

	@Test
	public void testExpandingSGEntries() {
		selectSGTarget();
		
		SWTBotView detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
		
    	detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.NoChildInterface").expand();
    	assertTrue(detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.NoChildInterface").getNode("NoChildInterface_Method1").isEnabled());
	}
	
	@Test
	public void testApplyingSGRefactoring() {
		selectSGTarget();
		
		SWTBotView detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
		
    	detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.NoChildInterface").expand();
    	detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.NoChildInterface").getNode("NoChildInterface_Method").click();
    	
    	detectionApplier.getToolbarButtons().get(1).click();
    	
    	// Assertion Message Checking
    	SWTBotView assuranceChecker = bot.viewByTitle("Refactoring Assertion");
    	assertTrue(assuranceChecker.bot().button(1).isEnabled()); // Might be "cancel" button
	}
	
	@Test
	public void testAppliedSGRefacctoring() {
		// Applying Refactoring and Assure in Certain Steps
		SWTBotView detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
		
    	// Assertion 
		assertFalse(detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.NoChildInterface").isEnabled()); // deleted
	}
}
