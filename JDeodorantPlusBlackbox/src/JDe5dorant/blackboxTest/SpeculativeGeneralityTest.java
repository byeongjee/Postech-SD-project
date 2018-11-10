package JDe5dorant.blackboxTest;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.*;
import org.junit.runner.RunWith;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;

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
		testProject.deleteProject();
		bot.resetWorkbench();
	}
	
	@Test
	public void testOpenSpeculativeGeneralityTab() {
		bot.menu("Bad Smells").menu("Speculative Generality").click();
		bot.viewByTitle("Speculative Generality");
	}
	
	@Test
	public void testApplyingSGDetection() {
		// Click Project
		
		// Click Applying C.S. Detection Button
		
	}

	@Test
	public void testExpandingSGEntries() {
		// Click Project
		
		// Click Applying C.S. Detection Button
		
		// Check The Result
		
		// Expand the Entries
		
		// Check Methods & Code Smell Type
		
		/*if(abstract) {
			all methods
		} else if(interface) {
			all methods
		} else {
			specific methods
		}*/
	}
	
	@Test
	public void testApplyingSGRefactoring() {
		// Click Project
		
		// Click Applying C.S. Detection Button
		
		// Click One Entry
		
		// Apply Refactoring by Clicking the Button
	}
}
