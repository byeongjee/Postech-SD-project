package applyRefactoring;

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
public class tester {
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
		openPackageExplorer();
	}

	@AfterClass
	public static void afterClass() throws CoreException {
		bot.resetWorkbench();
	}
	
	public void openSGTab() {
		bot.menu("JDe5dorant").menu("Speculative Generality").click();
		bot.viewByTitle("Speculative Generality");
	}

	private void selectSGTarget() {
		SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
		packageExplorer.show();
		packageExplorer.bot().tree().getTreeItem("testProject").expand().getNode("src").expand().getNode("SpeculativeGenerality").click();
	}
	
	private void turnOnProject(int arg) throws CoreException {
		if(!flagProjectOn) {
			testSGProject.buildProject(arg);
			flagProjectOn = true;
		} else {
			testSGProject.deleteProject();
			testSGProject.buildProject(arg);
		}
	}
	
	@Test
	public void SG_NoChild() throws CoreException {
		openSGTab();
		turnOnProject(0);
		selectSGTarget();
		
		SWTBotView detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
		
		detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.NoChildInterface").select();
		assertTrue(detectionApplier.bot().button("TEST").isEnabled());
		testSGProject.deleteProject();
	}
	
	@Test
	public void SG_WithChild() throws CoreException {
		turnOnProject(3);
		selectSGTarget();
		
		SWTBotView detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
		
		detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.TC_UnnecessaryParameter").select();
		detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.TC_UnnecessaryParameter").expand().getNode(0).select();
		
		assertTrue(detectionApplier.bot().button("Child").isVisible());
		testSGProject.deleteProject();
	}
	
	@Test
	public void MC() throws CoreException {
		testMCProject.buildProject();
		
		bot.menu("JDe5dorant").menu("Message Chain").click();
		bot.viewByTitle("Message Chain");
		
		SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
		packageExplorer.show();
		packageExplorer.bot().tree().getTreeItem("testProject").click();

		SWTBotView detectionApplier = bot.viewByTitle("Message Chain");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();

		detectionApplier.bot().tree().getTreeItem("").expand();
		assertTrue(detectionApplier.bot().button("Child").isVisible());
		
		testMCProject.deleteProject();
	}

	private static void openLPLTab() {
		bot.menu("JDe5dorant").menu("Long Parameter List").click();
	}

	private static void selectTargetPackage() {
		SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
		packageExplorer.show();
		packageExplorer.bot().tree().getTreeItem("testLPLProject").doubleClick();
		packageExplorer.bot().tree().getTreeItem("testLPLProject").getNode("src").doubleClick();
		packageExplorer.bot().tree().getTreeItem("testLPLProject").getNode("src").getNode("LongParameterList").click();
	}

	private static void applyDetection() {
		SWTBotView detectionView = bot.viewByTitle("Long Parameter List");
		detectionView.show();
		detectionView.getToolbarButtons().get(0).click();
	}
	
	@Test
	public void LPL() throws CoreException {
		testLPLProject.buildLPLProject();
		
		openLPLTab();
		selectTargetPackage();
		applyDetection();
		
		SWTBotView detectionView = bot.viewByTitle("Long Parameter List");
		assertTrue(detectionView.bot().button("TEST").isVisible());
		
		testLPLProject.deleteLPLProject();
	}
	
	@Test
	public void TC() throws CoreException {
		testTCProject.buildProject();
		
		bot.menu("JDe5dorant").menu("Type Checking").click();
		bot.viewByTitle("Type Checking");
		
		SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
		packageExplorer.show();
		packageExplorer.bot().tree().getTreeItem("testProject").click();

		SWTBotView detectionApplier = bot.viewByTitle("Type Checking");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();

		detectionApplier.bot().tree().getTreeItem("").expand();
		assertTrue(detectionApplier.bot().button("Child").isVisible());
		
		testTCProject.deleteProject();
	}
	
	@Test
	public void LM() throws CoreException {
		testLMProject.buildProject();
		
		bot.menu("JDe5dorant").menu("Long Method").click();
		bot.viewByTitle("Long Method");
		
		SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
		packageExplorer.show();
		packageExplorer.bot().tree().getTreeItem("testProject").click();

		SWTBotView detectionApplier = bot.viewByTitle("Long Method");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();

		detectionApplier.bot().tree().getTreeItem("").expand();
		assertTrue(detectionApplier.bot().button("Child").isVisible());
		
		testLMProject.deleteProject();
	}
	
	@Test
	public void GC() throws CoreException {
		testGCProject.buildProject();
		
		bot.menu("JDe5dorant").menu("God Class").click();
		bot.viewByTitle("God Class");
		
		SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
		packageExplorer.show();
		packageExplorer.bot().tree().getTreeItem("testProject").click();

		SWTBotView detectionApplier = bot.viewByTitle("God Class");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();

		detectionApplier.bot().tree().getTreeItem("").expand().getNode("").expand();
		assertTrue(detectionApplier.bot().button("Child").isVisible());
		
		testGCProject.deleteProject();
	}
	
	@Test
	public void FE() throws CoreException {
		testFEProject.buildProject();
		
		bot.menu("JDe5dorant").menu("Feature Envy").click();
		bot.viewByTitle("Feature Envy");
		
		SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
		packageExplorer.show();
		packageExplorer.bot().tree().getTreeItem("testProject").click();

		SWTBotView detectionApplier = bot.viewByTitle("Feature Envy");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();

		assertTrue(detectionApplier.bot().button("TEST").isVisible());
		
		testFEProject.deleteProject();
	}
}
