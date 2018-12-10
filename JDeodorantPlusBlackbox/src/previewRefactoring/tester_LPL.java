package previewRefactoring;

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
public class tester_LPL {
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

		testLPLProject.buildLPLProject();
		openPackageExplorer();
		SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
	}

	@AfterClass
	public static void afterClass() throws CoreException {
		//bot.sleep(100000);
		deleteTestProject();
		bot.resetWorkbench();
	}

	private void openLPLTab() {
		bot.menu("JDe5dorant").menu("Long Parameter List").click();
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

	private void openRefactoringPopUpFromDetectedSmell() {
		SWTBotView detectionView = bot.viewByTitle("Long Parameter List");
		detectionView.show();
		detectionView.bot().button("TEST").click();
	}

	private void detectCodeSmellAndOpenRefactoringPopUp() {
		openLPLTab();
		selectTargetPackage();
		applyDetection();
		openRefactoringPopUpFromDetectedSmell();
	}

	private void closeRefactoringPopUp() {
		bot.shell("Refactoring").close();
	}

	@Test
	public void testOpenPreviewTab() {
		try {
			//testLPLProject.buildLPLProject();
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("testLPLProject");
			IJavaProject javaProject = JavaCore.create(project);
			String originalSource = "";
			int LPLPkgIndex = 0;
			for(LPLPkgIndex = 0; LPLPkgIndex < javaProject.getPackageFragments().length; LPLPkgIndex++) {
				if(javaProject.getPackageFragments()[LPLPkgIndex].getElementName() == "LongParameterList")
					break;
			}
			LPLPkgIndex--;
			originalSource = javaProject.getPackageFragments()[LPLPkgIndex].getCompilationUnit("TestLPL.java").getSource();
			detectCodeSmellAndOpenRefactoringPopUp();
			SWTBotShell refactoringWizard = bot.shell("Refactoring");
			refactoringWizard.bot().table().getTableItem(0).check();
			refactoringWizard.bot().table().getTableItem(1).check();
			refactoringWizard.bot().button("Next >").click();
			// now in class name page
			refactoringWizard.bot().text().selectAll().typeText("TestClass");
			refactoringWizard.bot().button("Next >").click();
			// now in package selection page
			refactoringWizard.bot().table().getTableItem(0).check();
			// now in Preview Page
			refactoringWizard.bot().button("Next >").click();
			assertTrue(refactoringWizard.bot().button("Finish").isActive());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
		closeRefactoringPopUp();
		closeLPLTab();
		testLPLProject.buildLPLProject();
		deleteTestProject();
			} catch (Exception e) {
				
			}

		}

	}
	
	@Test
	public void testBackPreviewTab() {
		try {
			//testLPLProject.buildLPLProject();
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("testLPLProject");
			IJavaProject javaProject = JavaCore.create(project);
			String originalSource = "";
			int LPLPkgIndex = 0;
			for(LPLPkgIndex = 0; LPLPkgIndex < javaProject.getPackageFragments().length; LPLPkgIndex++) {
				if(javaProject.getPackageFragments()[LPLPkgIndex].getElementName() == "LongParameterList")
					break;
			}
			LPLPkgIndex--;
			originalSource = javaProject.getPackageFragments()[LPLPkgIndex].getCompilationUnit("TestLPL.java").getSource();
			detectCodeSmellAndOpenRefactoringPopUp();
			SWTBotShell refactoringWizard = bot.shell("Refactoring");
			refactoringWizard.bot().table().getTableItem(0).check();
			refactoringWizard.bot().table().getTableItem(1).check();
			refactoringWizard.bot().button("Next >").click();
			// now in class name page
			refactoringWizard.bot().text().selectAll().typeText("TestClass");
			refactoringWizard.bot().button("Next >").click();
			// now in package selection page
			refactoringWizard.bot().table().getTableItem(0).check();
			// now in Preview Page
			refactoringWizard.bot().button("Next >").click();
			assertTrue(refactoringWizard.bot().button("Back <").isActive());
			refactoringWizard.bot().button("Back <").click();
			
			// now in package selection page
			assertTrue(refactoringWizard.bot().button("Next >").isActive());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
		closeRefactoringPopUp();
		closeLPLTab();
		testLPLProject.buildLPLProject();
		deleteTestProject();
			} catch (Exception e) {
				
			}

		}

	}
	
	@Test
	public void testFinishPreviewTab() {
		try {
			//testLPLProject.buildLPLProject();
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("testLPLProject");
			IJavaProject javaProject = JavaCore.create(project);
			String originalSource = "";
			int LPLPkgIndex = 0;
			for(LPLPkgIndex = 0; LPLPkgIndex < javaProject.getPackageFragments().length; LPLPkgIndex++) {
				if(javaProject.getPackageFragments()[LPLPkgIndex].getElementName() == "LongParameterList")
					break;
			}
			LPLPkgIndex--;
			originalSource = javaProject.getPackageFragments()[LPLPkgIndex].getCompilationUnit("TestLPL.java").getSource();
			detectCodeSmellAndOpenRefactoringPopUp();
			SWTBotShell refactoringWizard = bot.shell("Refactoring");
			refactoringWizard.bot().table().getTableItem(0).check();
			refactoringWizard.bot().table().getTableItem(1).check();
			refactoringWizard.bot().button("Next >").click();
			// now in class name page
			refactoringWizard.bot().text().selectAll().typeText("TestClass");
			refactoringWizard.bot().button("Next >").click();
			// now in package selection page
			refactoringWizard.bot().table().getTableItem(0).check();
			// now in Preview Page
			refactoringWizard.bot().button("Next >").click();
			refactoringWizard.bot().button("Finish").click();
			
			IProject projectUpdated = ResourcesPlugin.getWorkspace().getRoot().getProject("testLPLProject");
			IJavaProject javaProjectUpdated = JavaCore.create(project);

			assertTrue(javaProjectUpdated.getPackageFragments()[LPLPkgIndex].getCompilationUnit("TestLPL.java").getSource()
					.length() < originalSource.length());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
		closeRefactoringPopUp();
		closeLPLTab();
		testLPLProject.buildLPLProject();
		deleteTestProject();
			} catch (Exception e) {
				
			}

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
