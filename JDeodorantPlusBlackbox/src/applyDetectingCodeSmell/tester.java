package applyDetectingCodeSmell;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
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

import java.util.concurrent.CyclicBarrier;

import org.eclipse.core.runtime.CoreException;

@RunWith(SWTBotJunit4ClassRunner.class)
public class tester {
	private static SWTWorkbenchBot bot;

	private static void openPackageExplorer() {
		bot.menu("Window").menu("Show View").menu("Other...").click();
		SWTBotShell dialog = bot.shell("Show View");
		dialog.activate();
		bot.tree().getTreeItem("Java").expand().getNode("Package Explorer").doubleClick();
	}

	public void openGodClassTab() {
		bot.menu("JDe5dorant").menu("God Class").click();
		bot.viewByTitle("God Class");
		assertTrue(bot.viewByTitle("God Class").isActive());
	}
	
	public void openLongMethodTab() {
		bot.menu("JDe5dorant").menu("Long Method").click();
		bot.viewByTitle("Long Method");
		assertTrue(bot.viewByTitle("Long Method").isActive());
	}
	
	public void openMessageChainTab() {
		bot.menu("JDe5dorant").menu("Message Chain").click();
		bot.viewByTitle("Message Chain");
		assertTrue(bot.viewByTitle("Message Chain").isActive());
	}	
	
	public void openSpeculativeGeneralityTab() {
		bot.menu("JDe5dorant").menu("Speculative Generality").click();
		bot.viewByTitle("Speculative Generality");
		assertTrue(bot.viewByTitle("Speculative Generality").isActive());
	}
	
	@BeforeClass
	public static void initBot() throws CoreException {
		bot = new SWTWorkbenchBot();
		testProject.buildProject();
		openPackageExplorer();
	}

	@AfterClass
	public static void afterClass() throws CoreException {
		testProject.deleteProject();
		bot.resetWorkbench();
	}
	
	@Test
	public void testApplyingLongMethodDetection() throws CoreException {
		this.openLongMethodTab();
		
		SWTBotView detectionApplier = bot.viewByTitle("Long Method");
		detectionApplier.show();
		
		SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
		packageExplorer.show();
		packageExplorer.bot().tree().getTreeItem("testProject").click();

		detectionApplier = bot.viewByTitle("Long Method");
		detectionApplier.show();
		assertTrue(detectionApplier.getToolbarButtons().get(0).isEnabled());
	}
	
	@Test
	public void testApplyingSpeculativeGeneralityDetection() throws CoreException {
		this.openSpeculativeGeneralityTab();
		
		SWTBotView detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		
		SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
		packageExplorer.show();
		packageExplorer.bot().tree().getTreeItem("testProject").click();

		detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		assertTrue(detectionApplier.getToolbarButtons().get(0).isEnabled());
	}

	@Test
	public void testApplyingMCDetection() throws CoreException {
		this.openMessageChainTab();
		
		SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
		packageExplorer.show();
		packageExplorer.bot().tree().getTreeItem("testProject").click();

		SWTBotView detectionApplier = bot.viewByTitle("Message Chain");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
		assertTrue(detectionApplier.bot().tree().getTreeItem("").isEnabled());
	}
}