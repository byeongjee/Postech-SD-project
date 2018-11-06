import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.*;
import org.junit.runner.RunWith;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;

@RunWith(SWTBotJunit4ClassRunner.class)
public class TopLevelBlackBoxTest {
	private static SWTWorkbenchBot bot;

	@BeforeClass
	public static void initBot() {
		bot = new SWTWorkbenchBot();
		bot.viewByTitle("Welcome").close();
	}

	@AfterClass
	public static void afterClass() {
		bot.resetWorkbench();
	}
	
	@Test
	public void testOpenLongMethodTab() {
		bot.menu("Bad Smells").menu("Long Method").click();
	}

	@Test
	public void testOpenSpeculativeGeneralityTab() {
		bot.menu("Bad Smells").menu("Speculative Generality").click();
		bot.viewByTitle("Speculative Generality");
	}
	
	@Test
	public void testOpenMessageChainTab() {
		bot.menu("Bad Smells").menu("Message Chain").click();
		bot.viewByTitle("Message Chain");
	}
	
	@Test
	public void testOpenLongParameterListTab() {
		bot.menu("Bad Smells").menu("Long Parameter List").click();
		bot.viewByTitle("Long Parameter List");
	}
	

	private void openPackageExplorer() {
		bot.menu("Window").menu("Show View").menu("Other...").click();
		SWTBotShell dialog = bot.shell("Show View");
		dialog.activate();
		bot.tree().getTreeItem("Java").expand().getNode("Package Explorer").doubleClick();
	}
}
