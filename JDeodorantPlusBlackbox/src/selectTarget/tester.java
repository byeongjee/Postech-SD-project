package selectTarget;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
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

/**
 * Simple Clicking and Expanding on Tree Itemrs in Package Explorer,
 * for Target Selection
 * @author SuKyung Oh, JuYong Lee
 *
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class tester {
	private static SWTWorkbenchBot bot;

	@BeforeClass
	public static void initBot() throws CoreException {
		bot = new SWTWorkbenchBot();
		//bot.viewByTitle("Welcome").close();

		testProject.buildProject();
		SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
	}

	@AfterClass
	public static void afterClass() throws CoreException {
		testProject.deleteProject();
		bot.resetWorkbench();
	}

	@Test
	public void testOpenPackageExplorer() {
		bot.menu("Window").menu("Show View").menu("Other...").click();
		SWTBotShell dialog = bot.shell("Show View");
		dialog.activate();
		assertTrue(dialog.isActive());
		
		bot.tree().getTreeItem("Java").expand().getNode("Package Explorer").doubleClick();
		
		SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
		assertTrue(packageExplorer.isActive());
	}

	@Test
	public void testSelectTarget() {
		bot.menu("Window").menu("Show View").menu("Other...").click();
		SWTBotShell dialog = bot.shell("Show View");
		dialog.activate();
		
		bot.tree().getTreeItem("Java").expand().getNode("Package Explorer").doubleClick();
		SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
		packageExplorer.show();
		
		assertTrue(packageExplorer.bot().tree().getTreeItem("testProject").isEnabled());
		packageExplorer.bot().tree().getTreeItem("testProject").expand();
		assertTrue(packageExplorer.bot().tree().getTreeItem("testProject").expand().getNode("src").isEnabled());
		packageExplorer.bot().tree().getTreeItem("testProject").expand().getNode("src").expand();
		assertTrue(packageExplorer.bot().tree().getTreeItem("testProject").expand().getNode("src").expand().getNode("selectTargetPackage").isEnabled());
		packageExplorer.bot().tree().getTreeItem("testProject").expand().getNode("src").expand().getNode("selectTargetPackage").expand();
		assertTrue(packageExplorer.bot().tree().getTreeItem("testProject").expand().getNode("src").expand().getNode("selectTargetPackage").expand().getNode("selectTargetJavaFile.java").isEnabled());
	}
}
