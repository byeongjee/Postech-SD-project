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
	public void testOpenJDeodrantPlusTab() {
		openSpeculativeGeneralityTab();
		bot.viewByTitle("Speculative Generality");
	}
	
	private void openSpeculativeGeneralityTab() {
		bot.menu("Bad Smells").menu("Speculative Generality").click();
	}
}
