package applyRefactoring;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.*;
import org.junit.runner.RunWith;

import applyRefactoring.testMCProject;

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
public class tester_MC {
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
	
	private void turnOnProject() throws CoreException {
		if(flagProjectOn)
			testMCProject.deleteProject();
		testMCProject.buildProject();
		flagProjectOn = true;
	}
		
	@Test
	public void MC() throws CoreException {
		turnOnProject();
		bot.menu("JDe5dorant").menu("Message Chain").click();
		bot.viewByTitle("Message Chain");
		
		SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
		packageExplorer.show();
		packageExplorer.bot().tree().getTreeItem("testProject").click();

		SWTBotView detectionApplier = bot.viewByTitle("Message Chain");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
		
		detectionApplier.bot().tree().getTreeItem("MessageChain.Simple2").select();
		detectionApplier.bot().tree().getTreeItem("MessageChain.Simple2").expand().getNode(0).select();
		assertTrue(detectionApplier.bot().button("Child").isVisible());
		testMCProject.deleteProject();
	}
}
