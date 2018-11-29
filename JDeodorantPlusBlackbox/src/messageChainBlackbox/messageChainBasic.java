package messageChainBlackbox;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
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
public class messageChainBasic {
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
   public void testOpenMessageChainTab() {
      bot.menu("Bad Smells").menu("Message Chain").click();
      bot.viewByTitle("Message Chain");
      assertTrue(bot.viewByTitle("Message Chain").isActive());
   }
   
   @Test
   public void testApplyingMCDetection() {
      bot.menu("Bad Smells").menu("Message Chain").click();
      bot.viewByTitle("Message Chain");
      SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
      packageExplorer.show();
      packageExplorer.bot().tree().getTreeItem("testProject").click();
      
      SWTBotView detectionApplier = bot.viewByTitle("Message Chain");
      detectionApplier.show();
      detectionApplier.getToolbarButtons().get(0).click();
      assertTrue(detectionApplier.bot().tree().getTreeItem("").isEnabled());
   }
   
   @Test
   public void testExpand() {
      bot.menu("Bad Smells").menu("Message Chain").click();
      bot.viewByTitle("Message Chain");
      SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
      packageExplorer.show();
      packageExplorer.bot().tree().getTreeItem("testProject").click();
      
      SWTBotView detectionApplier = bot.viewByTitle("Message Chain");
      detectionApplier.show();
      detectionApplier.getToolbarButtons().get(0).click();
      detectionApplier.bot().tree().getTreeItem("").select();
       detectionApplier.bot().tree().getTreeItem("").expand();
       assertTrue(detectionApplier.bot().tree().getTreeItem("").getNode("536").isEnabled());
   }
   
   // Add in Iteration 3
   @Test
   public void testDoubleClickToSeeHighlight() {
      bot.menu("Bad Smells").menu("Message Chain").click();
      bot.viewByTitle("Message Chain");
      SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
      packageExplorer.show();
      packageExplorer.bot().tree().getTreeItem("testProject").click();
      
      SWTBotView detectionApplier = bot.viewByTitle("Message Chain");
      detectionApplier.show();
      detectionApplier.getToolbarButtons().get(0).click();
      detectionApplier.bot().tree().getTreeItem("").select();
       detectionApplier.bot().tree().getTreeItem("").expand();
       detectionApplier.bot().tree().getTreeItem("").getNode(0).select();
       detectionApplier.bot().tree().getTreeItem("").getNode(0).doubleClick();
   }
   
   @Test
   public void testButtonClick() {
      bot.menu("Bad Smells").menu("Message Chain").click();
      bot.viewByTitle("Message Chain");
      SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
      packageExplorer.show();
      packageExplorer.bot().tree().getTreeItem("testProject").click();
      
      SWTBotView detectionApplier = bot.viewByTitle("Message Chain");
      detectionApplier.show();
      detectionApplier.getToolbarButtons().get(0).click();
      detectionApplier.bot().tree().getTreeItem("").select();
       detectionApplier.bot().tree().getTreeItem("").expand();
       detectionApplier.bot().tree().getTreeItem("").getNode(1).select();
       detectionApplier.bot().tree().getTreeItem("").getNode(1).doubleClick();
       detectionApplier.bot().button(1).click();
       bot.saveAllEditors();
       detectionApplier.close();
   }
}