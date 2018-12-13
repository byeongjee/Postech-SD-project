package gr.uom.java.jdeodorant.refactoring.views;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.wizard.Wizard;

public class MCRefactorWizard extends Wizard {
   private IJavaProject javaProject;
   private MCNewMethodPage initialPage;
   private String newMethodName;
   
   public MCRefactorWizard(IJavaProject javaProject) {
      super();
      setNeedsProgressMonitor(true);
      this.javaProject = javaProject;
      this.newMethodName = null;
   }
   
   @Override
   public String getWindowTitle() {
      return "Refactoring";
   }
   
   @Override
   public void addPages() {
      initialPage = new MCNewMethodPage();
      addPage(initialPage);
   }
   
   @Override
   public boolean performFinish() {
      newMethodName = initialPage.getText1();
      return true;
   }
   
   @Override
	public boolean performCancel() {
	   newMethodName = null;
      return true;
  }
   
   public String getNewMethodName()
   {
      return newMethodName;
   }
}