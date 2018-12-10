package gr.uom.java.jdeodorant.refactoring.views;

import java.util.ArrayList;

import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jface.wizard.Wizard;

import gr.uom.java.ast.LPLMethodObject;

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