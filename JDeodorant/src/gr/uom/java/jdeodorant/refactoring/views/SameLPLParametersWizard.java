package gr.uom.java.jdeodorant.refactoring.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jface.wizard.Wizard;

import gr.uom.java.ast.LPLMethodObject;
import gr.uom.java.ast.LPLSmellContent;

public class SameLPLParametersWizard extends Wizard {
	//private IJavaProject javaProject;
	private IMethod candidateMethod;
	private SameLPLParametersAssertionPage assertionPage;
	private boolean doExtraction;
	
	public SameLPLParametersWizard(IMethod candidateMethod) {
		super();
		setNeedsProgressMonitor(true);
		//this.javaProject = javaProject;
		this.candidateMethod = candidateMethod;  
		doExtraction = false;
	}
	
	@Override
	public String getWindowTitle() {
		return "Same parameters found";
	}
	
	@Override
	public void addPages() {
		assertionPage = new SameLPLParametersAssertionPage(candidateMethod);
		addPage(assertionPage);
	}
	
	@Override
	public boolean performFinish() {
		doExtraction = true;
		return true;
	}
	
	/**
	 * Returns doExtraction
	 * @return
	 */
	public boolean getDoExtraction() {
		return this.doExtraction;
	}
}
