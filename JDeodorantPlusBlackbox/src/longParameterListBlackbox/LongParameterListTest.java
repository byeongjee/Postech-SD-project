package longParameterListBlackbox;

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
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.eclipse.swt.SWT;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.runner.*;

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertVisible;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

@RunWith(SWTBotJunit4ClassRunner.class)
public class LongParameterListTest {
	private static SWTWorkbenchBot bot;

	private static void openPackageExplorer() {
		bot.menu("Window").menu("Show View").menu("Other...").click();
		SWTBotShell dialog = bot.shell("Show View");
		dialog.activate();
		bot.tree().getTreeItem("Java").expand().getNode("Package Explorer").doubleClick();
	}

	/**
	 * this method initializes SWTBot, build demo project, and open package explorer
	 * Also, this method set SWTBot keyboard layout for .typeText() method
	 * @throws CoreException
	 */
	@BeforeClass
	public static void initBot() throws CoreException {
		bot = new SWTWorkbenchBot();
		bot.viewByTitle("Welcome").close();

		testLPLProject.buildLPLProject();
		openPackageExplorer();
		SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
	}

	/**
	 * this method deletes temporary project, and reset workbench for further testing
	 * @throws CoreException
	 */
	@AfterClass
	public static void afterClass() throws CoreException {
		//bot.sleep(100000);
		deleteTestProject();
		bot.resetWorkbench();
	}

	private void openLPLTab() {
		bot.menu("Bad Smells").menu("Long Parameter List").click();
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

	/**
	 * this test corresponds to 
	 * 	1.Smells list Opening user story (iteration 1)
	 * 	2.code smell select (iteration 2)
	 * test if user can open Long Parameter List tab
	 */
	@Test
	public void testOpenLPLTab() {
		try {
			openLPLTab();
			assertTrue(bot.viewByTitle("Long Parameter List").isActive());
		} catch (Exception e) {
			fail("test fail with exception " + e);
		} finally {
			closeLPLTab();
		}
	}

	/**
	 * this test corresponds to 
	 *	 1.Apply Detecting code-smell (iteration 2)
	 * test if user can click a button to detect code smells
	 */
	@Test
	public void testApplyingLPLDetection() {
		try {
			openLPLTab();
			selectTargetPackage();
			applyDetection();
		} catch (Exception e) {
			fail("test fail with exception " + e);
		} finally {
			closeLPLTab();
		}
	}

	/**
	 * this test corresponds to 
	 * 	1.Detect Code Smell 4 : LPL user story(iteration 2)
	 * 	2.Show code-smell Information 1 : Tab(iteration 2)
	 * test if user can see detected code smells after clicking a detection button 
	 */
	@Test
	public void testLPLRefactoringItemShownScenario() {
		try {
			openLPLTab();
			openPackageExplorer();
			selectTargetPackage();
			applyDetection();
			bot.viewByTitle("Long Parameter List").bot().tree().getTreeItem("Long Parameter List").getItems();
		} catch (Exception e) {
			fail("test fail with exception " + e);
		} finally {
			closeLPLTab();
		}
	}

	/**
	 * this test corresponds to
	 * 	1. Show Detected code-smell 1 : Highlight (iteration 3)
	 * 	2. Navigate to detected Code 1 : Tab Double Click (iteration 3)
	 * test if user can doubleclick a detected code smell to open the corresponding text editor, 
	 * and see the highlighted smell
	 */
	@Test
	public void testSmellHighlight() {
		openLPLTab();
		selectTargetPackage();
		applyDetection();
		SWTBotView detectionView = bot.viewByTitle("Long Parameter List");
		detectionView.show();
		detectionView.bot().tree().getTreeItem("Long Parameter List").doubleClick();
		SWTBotEditor editor = bot.editorByTitle("TestLPL.java");
		SWTBotEclipseEditor eclipseEditor = editor.toTextEditor();
	}

	/**
	 * this test corresponds to 
	 * 	1. Progress refactor UI : LPL (iteration 3)
	 * test if a user can click refactoring button to open the refactoring tab
	 */
	@Test
	public void testOpenRefactoringPopUp() {
		try {
			detectCodeSmellAndOpenRefactoringPopUp();
			SWTBotShell refactoringWizard = bot.shell("Refactoring");
			assertTrue(refactoringWizard.isVisible());
		}		catch (Exception e) {
			fail("test fail with exception "+e);
		} finally {
			try {
				closeRefactoringPopUp();
				closeLPLTab();
			} catch (Exception e) {

			}
		}
	}

	/**
	 * this test corresponds to an exceptional case of 
	 * 	1. Progress refactor UI : LPL (iteartion 3)
	 * assert that user cannot continue to the class name page without checking any arguments to extract
	 */
	@Test
	public void testRefactoringPopUpInitialPageExceptionScenario() {
		try {
			detectCodeSmellAndOpenRefactoringPopUp();
			SWTBotShell refactoringWizard = bot.shell("Refactoring");
			assertFalse(refactoringWizard.bot().button("Next >").isEnabled());
		}		catch (Exception e) {
			fail("test fail with exception "+e);
		} finally {
			try {
				closeRefactoringPopUp();
				closeLPLTab();
			} catch (Exception e) {

			}
		}
	}

	/**
	 * this test corresponds to an exceptional case of 
	 * 	1. Progress refactor UI : LPL (iteration 3)
	 * assert that user cannot continue to the package selection page without giving the new class name and parameter name
	 */
	@Test 
	public void testRefactoringPopUpUIClassNamePageExceptionScenario() {
		try {
			detectCodeSmellAndOpenRefactoringPopUp();
			SWTBotShell refactoringWizard = bot.shell("Refactoring");
			refactoringWizard.bot().table().getTableItem(0).check();
			refactoringWizard.bot().table().getTableItem(1).check();
			// now in class name page
			refactoringWizard.bot().button("Next >").click();
			assertFalse(refactoringWizard.bot().button("Next >").isEnabled());
		}		catch (Exception e) {
			fail("test fail with exception "+e);
		} finally {
			try {
				closeRefactoringPopUp();
				closeLPLTab();
			} catch (Exception e) {

			}
		}
	}
	
	/**
	 * this test corresponds to
	 * 	1. Progress refactor UI : LPL (iteration 3)
	 * 	2. Progress refactor 4 : LPL (iteration 4)
	 * assert that user can finish refactoring successfully with refactoring tabs
	 */
	@Test
	public void testLPLRefactoringSuccessScenario() {
		try {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("testLPLProject");
			IJavaProject javaProject = JavaCore.create(project);
			String originalSource = "";
			int LPLPkgIndex = 0;
			for (LPLPkgIndex = 0; LPLPkgIndex < javaProject.getPackageFragments().length; LPLPkgIndex++) {
				if (javaProject.getPackageFragments()[LPLPkgIndex].getElementName().equals("LongParameterList")) {
					break;
				}
			}
			originalSource = javaProject.getPackageFragments()[LPLPkgIndex].getCompilationUnit("TestLPL.java").getSource();
			detectCodeSmellAndOpenRefactoringPopUp();
			SWTBotShell refactoringWizard = bot.shell("Refactoring");
			refactoringWizard.bot().table().getTableItem(2).check();
			refactoringWizard.bot().table().getTableItem(3).check();
			refactoringWizard.bot().button("Next >").click();
			// now in class name page
			refactoringWizard.bot().text(0).selectAll().typeText("TestClass");
			refactoringWizard.bot().text(1).selectAll().typeText("testParameter");
			refactoringWizard.bot().button("Next >").click();
			// now in package selection page
			refactoringWizard.bot().table().getTableItem(0).check();
			refactoringWizard.bot().button("Finish").click();

			IJavaProject javaProjectUpdated = JavaCore.create(project);

			assertTrue(
					javaProjectUpdated.getPackageFragments()[LPLPkgIndex].getCompilationUnit("TestLPL.java").exists());
			assertFalse(javaProjectUpdated.getPackageFragments()[LPLPkgIndex].getCompilationUnit("TestLPL.java")
					.getSource().equals(originalSource));
			ICompilationUnit newCompilationUnit = javaProjectUpdated.getPackageFragments()[LPLPkgIndex]
					.getCompilationUnit("TestClass.java");
			assertTrue(newCompilationUnit != null);
			List<IField> fields = new ArrayList<IField>();
			for (IType type : newCompilationUnit.getTypes()) {
				for (IField field : type.getFields()) {
					fields.add(field);
				}
			}
			assertTrue(newCompilationUnit.exists() && fields.size() == 2);
		} catch (Exception e) {
			e.printStackTrace();
			fail("fail with Exception " + e);
		} finally {
			try {
				closeLPLTab();
				deleteTestProject();
				testLPLProject.buildLPLProject();
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * this test corresponds to
	 * 	1. Progress refactor UI : LPL (iteration 3)
	 * 	2. Progress refactor 4 : LPL (iteration 4)
	 * test if a user can refactor multiple methods with the similar parameters together through a popup dialog
	 */
	@Test
	public void testLPLRefactoringSameParameters() {
		try {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("testLPLProject");
			IJavaProject javaProject = JavaCore.create(project);
			String originalSource = "";
			int LPLPkgIndex = 0;
			for (LPLPkgIndex = 0; LPLPkgIndex < javaProject.getPackageFragments().length; LPLPkgIndex++) {
				if (javaProject.getPackageFragments()[LPLPkgIndex].getElementName().equals("LongParameterList")) {
					break;
				}
			}
			originalSource = javaProject.getPackageFragments()[LPLPkgIndex].getCompilationUnit("TestLPL.java").getSource();
			detectCodeSmellAndOpenRefactoringPopUp();
			SWTBotShell refactoringWizard = bot.shell("Refactoring");
			refactoringWizard.bot().table().getTableItem(0).check();
			refactoringWizard.bot().table().getTableItem(1).check();
			refactoringWizard.bot().button("Next >").click();
			// now in class name page
			refactoringWizard.bot().text(0).selectAll().typeText("TestClass");
			refactoringWizard.bot().text(1).selectAll().typeText("testParameter");
			refactoringWizard.bot().button("Next >").click();
			// now in package selection page
			refactoringWizard.bot().table().getTableItem(0).check();
			refactoringWizard.bot().button("Finish").click();
			
			for(int i = 0; i < 3; i++) {
				SWTBotShell sameParametersWizard = bot.shell("Same parameters found");
				if(i == 1)
					sameParametersWizard.bot().button("No").click();
				else
					sameParametersWizard.bot().button("Yes").click();
			}

			IJavaProject javaProjectUpdated = JavaCore.create(project);

			assertTrue(
					javaProjectUpdated.getPackageFragments()[LPLPkgIndex].getCompilationUnit("TestLPL.java").exists());
			assertFalse(javaProjectUpdated.getPackageFragments()[LPLPkgIndex].getCompilationUnit("TestLPL.java")
					.getSource().equals(originalSource));
			
			int methodVisit = 0;
			ICompilationUnit srcCompilationUnit = javaProjectUpdated.getPackageFragments()[LPLPkgIndex].getCompilationUnit("TestLPL.java");
			for(IType iT : srcCompilationUnit.getTypes()) {
				for(IMethod iM : iT.getMethods()) {
					String methodName = iM.getElementName();
					if(methodName.equals("getVal2")) {
						assertTrue(iM.getParameterNames().length == 1);
						assertTrue(iM.getParameterNames()[0].equals("testParameter"));
						methodVisit++;
					}
					else if(methodName.equals("getVal3")) {
						assertTrue(iM.getParameterNames().length == 3);
						List parameterNameList = Arrays.asList(iM.getParameterNames());
						assertTrue(parameterNameList.contains("x"));
						assertTrue(parameterNameList.contains("y"));
						assertTrue(parameterNameList.contains("z"));
						methodVisit++;
					}
					else if(methodName.equals("getVal4")) {
						assertTrue(iM.getParameterNames().length == 3);
						List parameterNameList = Arrays.asList(iM.getParameterNames());
						assertTrue(parameterNameList.contains("z"));
						assertTrue(parameterNameList.contains("w"));
						assertTrue(parameterNameList.contains("testParameter"));
						methodVisit++;
					}
				}
			}
			assertTrue(methodVisit == 3);
			ICompilationUnit newCompilationUnit = javaProjectUpdated.getPackageFragments()[LPLPkgIndex]
					.getCompilationUnit("TestClass.java");
			assertTrue(newCompilationUnit != null);
			List<IField> fields = new ArrayList<IField>();
			for (IType type : newCompilationUnit.getTypes()) {
				for (IField field : type.getFields()) {
					fields.add(field);
				}
			}
			assertTrue(newCompilationUnit.exists() && fields.size() == 2);
		} catch (Exception e) {
			e.printStackTrace();
			fail("fail with Exception " + e);
		} finally {
			try {
				closeLPLTab();
				deleteTestProject();
				testLPLProject.buildLPLProject();
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * this test corresponds to an exceptional case of 
	 * 	1. Progress refactor UI : LPL (iteration 3)
	 * assert that a warning is shown if a user tries to use a class name that does not start with a capital letter
	 */
	@Test
	public void testClassNameWarning() {
		try {
			detectCodeSmellAndOpenRefactoringPopUp();
			SWTBotShell refactoringWizard = bot.shell("Refactoring");
			refactoringWizard.bot().table().getTableItem(0).check();
			refactoringWizard.bot().table().getTableItem(1).check();
			refactoringWizard.bot().button("Next >").click();
			// now in class name page
			refactoringWizard.bot().text(0).selectAll().typeText("testClass");
			assertVisible(refactoringWizard.bot().label("* Class name does not start with capital letter"));

		} catch (Exception e) {
			fail("fail with Exception " + e);
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

	/**
	 * this test corresponds to an exceptional case of 
	 * 	1. Progress refactor UI : LPL (iteration 3)
	 * assert that user cannot continue to the package selection page
	 * if the name of the class that the user wants to create already exists
	 */
	@Test
	public void testSameClassWarning() {
		try {
			detectCodeSmellAndOpenRefactoringPopUp();
			SWTBotShell refactoringWizard = bot.shell("Refactoring");
			refactoringWizard.bot().table().getTableItem(0).check();
			refactoringWizard.bot().table().getTableItem(1).check();
			refactoringWizard.bot().button("Next >").click();
			// now in class name page
			refactoringWizard.bot().text(0).selectAll().typeText("TestLPL");
			refactoringWizard.bot().text(1).selectAll().typeText("testParameter");
			refactoringWizard.bot().button("Next >").click();
			// now in package selection page
			refactoringWizard.bot().table().getTableItem(0).check();
			assertVisible(refactoringWizard.bot().label("* Class with same name already exists!"));
			assertFalse(refactoringWizard.bot().button("Finish").isEnabled());
		} catch (Exception e) {
			fail("fail with Exception " + e);
		} finally {
			try {
				closeRefactoringPopUp();
				closeLPLTab();
			} catch (Exception e) {

			}
		}
	}

	/**
	 * this method deletes the demo project for further testings
	 */
	public static void deleteTestProject() {
		SWTBotView view = bot.viewByTitle("Project Explorer");
		view.bot().tree().getTreeItem("testLPLProject").contextMenu("Delete").click();
		SWTBotShell deleteShell = bot.shell("Delete Resources");
		deleteShell.activate();
		bot.checkBox("Delete project contents on disk (cannot be undone)").click();
		bot.button("OK").click();
	}

}
