package gr.uom.java.jdeodorant.refactoring.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.eclipse.jface.wizard.Wizard;

import gr.uom.java.ast.LPLMethodObject;

public class LPLRefactorWizard extends Wizard {
	private IJavaProject javaProject;
	private LPLMethodObject methodToRefactor;
	private LPLRefactorInitialPage initialPage;
	private LPLRefactorClassNamePage namePage;
	private LPLRefactorSelectPackagePage packagePage;
	
	public LPLRefactorWizard(IJavaProject javaProject, LPLMethodObject methodToRefactor) {
		super();
		setNeedsProgressMonitor(true);
		this.javaProject = javaProject;
		this.methodToRefactor = methodToRefactor;
	}
	
	@Override
	public String getWindowTitle() {
		return "Refactoring";
	}
	
	@Override
	public void addPages() {
		initialPage = new LPLRefactorInitialPage(methodToRefactor);
		namePage = new LPLRefactorClassNamePage();
		packagePage = new LPLRefactorSelectPackagePage(javaProject);
		addPage(initialPage);
		addPage(namePage);
		addPage(packagePage);
	}
	
	@Override
	public boolean performFinish() {
		System.out.println("Finish");
		System.out.println(initialPage.getParameterIndexList());
		System.out.println(packagePage.getPackageName());
		try {
			IMethod convertedIMethod = methodToRefactor.toIMethod(javaProject);
			ICompilationUnit workingCopy = convertedIMethod.getCompilationUnit()
					.getWorkingCopy(new WorkingCopyOwner() {
					}, null);
			IBuffer buffer = ((IOpenable) workingCopy).getBuffer();
			LPLMethodObject.editParameterFromBuffer(buffer, convertedIMethod, "", initialPage.getParameterIndexList());
			
			IPackageFragment pf = getIPackageFragment(packagePage.getPackageName());
			String className = namePage.getClassName();
			List<String> parameterTypes = initialPage.getExtractParameterTypes();
			List<String> parameterNames = initialPage.getExtractParameterNames();
			
			LPLMethodObject.createNewParameterClass(pf, className, parameterTypes, parameterNames);
			
			workingCopy.reconcile(ICompilationUnit.NO_AST, false, null, null);
			workingCopy.commitWorkingCopy(false, null);
			workingCopy.discardWorkingCopy();
			workingCopy.discardWorkingCopy();
		} catch (Exception e) {
		}
		return true;
	}
	
	@Override
	public boolean canFinish() {
		if(getContainer().getCurrentPage() == packagePage) {
			if(packagePage.getCanFinishPage())
				return true;
		}
		return false;
	}
	
	public IPackageFragment getIPackageFragment(String packageName) throws JavaModelException {
		IPackageFragment[] allPkg = javaProject.getPackageFragments();
		for(IPackageFragment myPackage : allPkg) {
			if(myPackage.getElementName().equals(packageName)) {
				return myPackage;
			}
		}
		return null;
	}
	
	/*static public void changeMethodsInProject(IJavaProject javaProject) {
		IPackageFragment[] allPkg = javaProject.getPackageFragments();
		List<IPackageFragment> srcPkgs = new ArrayList<IPackageFragment>();
		for(IPackageFragment myPackage : allPkg) {
			if(myPackage.getKind() == IPackageFragmentRoot.K_SOURCE && myPackage.getCompilationUnits().length != 0) {
				srcPkgs.add(myPackage);
			}
		}
		
		for(IPackageFragment srcPkg)
		if(parentElement instanceof IJavaProject) {
			IPackageFragment[] allPkg = ((IJavaProject) parentElement).getPackageFragments();
			List<IPackageFragment> ret = new ArrayList<>();
			for(IPackageFragment myPackage : allPkg) {
				if(myPackage.getKind() == IPackageFragmentRoot.K_SOURCE && myPackage.getCompilationUnits().length != 0) {
					ret.add(myPackage);
				}
			}
			return ret.toArray();
		}
		if(parentElement instanceof IPackageFragment) {
			return ((IPackageFragment) parentElement).getCompilationUnits();
		}
		if(parentElement instanceof ICompilationUnit) {
			IType[] allTypes = ((ICompilationUnit) parentElement).getTypes();
			List<IMember> ret = new ArrayList<>();
			for(IType t : allTypes) {
				ret.addAll(Arrays.asList(t.getFields()));
			}
			for(IType t : allTypes) {
				ret.addAll(Arrays.asList(t.getMethods()));
			}
			return ret.toArray();
		}
	}*/
	
}
