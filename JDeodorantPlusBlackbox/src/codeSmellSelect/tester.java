package codeSmellSelect;

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

import java.util.List;

import org.eclipse.core.runtime.CoreException;

/**
 * Opening and Ensuring Detectable Code-smell List' Tab
 * @author SuKyung Oh, JuYong Lee
 *
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class tester {
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
	public void testDuplicatedCodeTab() {
		List<String> menuItems = bot.menu("JDe5dorant").menuItems();

		boolean flag_typeCheckingExist = false;
		for (String str : menuItems) {
			if (str.equals("Duplicated Code")) {
				flag_typeCheckingExist = true;
			}
		}

		assertTrue(flag_typeCheckingExist);
	}
	
	@Test
	public void testTypeCheckingTab() {
		List<String> menuItems = bot.menu("JDe5dorant").menuItems();
		
		boolean flag_typeCheckingExist = false;
		for(String str : menuItems) {
			if(str.equals("Type Checking")) {
				flag_typeCheckingExist = true;
			}
		}
		
		assertTrue(flag_typeCheckingExist);
	}
	
	@Test
	public void testOpenGodClassTab() {
		bot.menu("JDe5dorant").menu("God Class").click();
		bot.viewByTitle("God Class");
		assertTrue(bot.viewByTitle("God Class").isActive());
	}
	
	@Test
	public void testOpenLongMethodTab() {
		bot.menu("JDe5dorant").menu("Long Method").click();
		bot.viewByTitle("Long Method");
		assertTrue(bot.viewByTitle("Long Method").isActive());
	}
	
	@Test
	public void testOpenFeatureEnvyTab() {
		bot.menu("JDe5dorant").menu("Feature Envy").click();
		bot.viewByTitle("Feature Envy");
		assertTrue(bot.viewByTitle("Feature Envy").isActive());
	}
	
	@Test
	public void testOpenMessageChainTab() {
		bot.menu("JDe5dorant").menu("Message Chain").click();
		bot.viewByTitle("Message Chain");
		assertTrue(bot.viewByTitle("Message Chain").isActive());
	}	
	
	@Test
	public void testOpenLongParameterListTab() {
		bot.menu("JDe5dorant").menu("Long Parameter List").click();
		bot.viewByTitle("Long Parameter List");
		assertTrue(bot.viewByTitle("Long Parameter List").isActive());
	}

	@Test
	public void testOpenSpeculativeGeneralityTab() {
		bot.menu("JDe5dorant").menu("Speculative Generality").click();
		bot.viewByTitle("Speculative Generality");
		assertTrue(bot.viewByTitle("Speculative Generality").isActive());
	}

}
