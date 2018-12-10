package gr.uom.java.jdeodorant.refactoring.views;

import java.util.ArrayList;

import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jface.wizard.Wizard;

import gr.uom.java.ast.LPLMethodObject;

public class MCPreviewWizard extends Wizard {
   private IJavaProject javaProject;
   private ICompilationUnit workingCompilationUnit;
   private MCRefactorPreviewPage previewPage;
   
   private String origin;
   private String refactor;
   
   private boolean flagCancel;
   
   public MCPreviewWizard(ICompilationUnit workingCopy, String origin, String refactor) {
		super();
		setNeedsProgressMonitor(true);
		this.workingCompilationUnit = workingCopy;
		
		this.origin = origin;
		this.refactor = refactor;
   }

   @Override
   public String getWindowTitle() {
      return "Refactoring";
   }
   
   @Override
   public void addPages() {
	  previewPage = new MCRefactorPreviewPage(this.origin, this.refactor);
      addPage(previewPage);
   }
   
   @Override
   public boolean performFinish() {
      try {
			workingCompilationUnit.reconcile(ICompilationUnit.NO_AST, false, null, null);
			workingCompilationUnit.commitWorkingCopy(false, null);
			workingCompilationUnit.discardWorkingCopy();
			flagCancel = false;
      } catch (JavaModelException e) {
			e.printStackTrace();
	}
      return true;
   }
   
   @Override
	public boolean performCancel() {
	  this.flagCancel = true;
      return true;
  }

   public boolean getFlagCancel() {
	return flagCancel;
}
   
}