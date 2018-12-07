package detectCodeSmell4_LPL;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.junit.*;
import org.junit.runner.RunWith;
import org.omg.CORBA.portable.Delegate;

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
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.eclipse.swt.SWT;
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
import static org.junit.Assert.fail;

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

	private static void openLPLTab() {
		bot.menu("JDe5dorant").menu("Long Parameter List").click();
	}

	@BeforeClass
	public static void initBot() throws CoreException {
		bot = new SWTWorkbenchBot();

		testLPLProject.buildLPLProject();
		openPackageExplorer();
		openLPLTab();
		SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
	}

	@AfterClass
	public static void afterClass() throws CoreException {
		deleteTestProject();
		bot.resetWorkbench();
	}

	private void closeLPLTab() {
		bot.viewByTitle("Long Parameter List").close();
	}

	private void selectTargetPackage() {
		SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
		packageExplorer.show();
		packageExplorer.bot().tree().getTreeItem("testLPLProject").doubleClick();
		packageExplorer.bot().tree().getTreeItem("testLPLProject").getNode("src").doubleClick();
		packageExplorer.bot().tree().getTreeItem("testLPLProject").getNode("src").getNode("LongParameterList").click();
	}

	private void applyDetection() {
		SWTBotView detectionView = bot.viewByTitle("Long Parameter List");
		detectionView.show();
		detectionView.getToolbarButtons().get(0).click();
	}

	@Test
	public void testApplyingLPLDetection() {
		try {
		selectTargetPackage();
		applyDetection();
		
		SWTBotView detectionView = bot.viewByTitle("Long Parameter List");
    	assertTrue(detectionView.bot().tree().getTreeItem("getVal4").isEnabled() || detectionView.bot().tree().getTreeItem("Long Parameter List").isEnabled());
		} finally {
		closeLPLTab();
		}
	}

	public static void deleteTestProject() {
    	bot.resetActivePerspective();
    	SWTBotView view = bot.viewByTitle("Project Explorer");
    	view.bot().tree().getTreeItem("testLPLProject").contextMenu("Delete").click();
    	SWTBotShell deleteShell = bot.shell("Delete Resources");
    	deleteShell.activate();
    	bot.checkBox("Delete project contents on disk (cannot be undone)").click();
    	bot.button("OK").click();
    }
}
