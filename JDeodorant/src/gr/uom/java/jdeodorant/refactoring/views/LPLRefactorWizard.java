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
		initialPage = new LPLRefactorInitialPage(methodToRefactor, javaProject);
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
			LPLSmellContent smellContent = new LPLSmellContent(methodToRefactor, initialPage.getParameterIndexList(), namePage.getClassName(), namePage.getParameterName());
			
			IPackageFragment pf = getIPackageFragment(packagePage.getPackageName());
			String className = namePage.getClassName();
			String parameterName = namePage.getParameterName();
			List<String> parameterTypes = initialPage.getExtractParameterTypes();
			List<String> parameterNames = initialPage.getExtractParameterNames();	
			
			LPLMethodObject.createNewParameterClass(pf, className, parameterTypes, parameterNames);
			changeMethodsInProject(javaProject, smellContent);
			
			IMethod convertedIMethod = methodToRefactor.toIMethod(javaProject);
			ICompilationUnit workingCopy = convertedIMethod.getCompilationUnit()
					.getWorkingCopy(new WorkingCopyOwner() {
					}, null);
			IBuffer buffer = ((IOpenable) workingCopy).getBuffer();
			String tempVarInitializeCode = LPLMethodObject.codeForInitializingTempVars(convertedIMethod, parameterTypes, parameterNames, parameterName);
			LPLMethodObject.editParameterFromBuffer(buffer, convertedIMethod, initialPage.getParameterIndexList(), smellContent, tempVarInitializeCode);

			workingCopy.reconcile(ICompilationUnit.NO_AST, false, null, null);
			workingCopy.commitWorkingCopy(false, null);
			workingCopy.discardWorkingCopy();
			workingCopy.discardWorkingCopy();
			
			ArrayList<String> parameterStringList = new ArrayList<String>();
			for(int i = 0; i < parameterTypes.size(); i++) {
				parameterStringList.add(parameterTypes.get(i) + " " + parameterNames.get(i));
			}
			System.out.println("signatures!!");
			findMethodsWithSameSignatures(javaProject, smellContent, parameterStringList);
			
		} catch (Exception e) {
		}
		return true;
	}
	
	@Override
	public boolean canFinish() {
		if(getContainer().getCurrentPage() == packagePage) {
			if(packagePage.getCanFinishPage()) {
				//if(getIPackageFragment(packagePage.getPackageName()).getCompilationUnit(namePage.getClassName() + ".java") != null) {
				if(classExists(packagePage.getPackageName(), namePage.getClassName())) {
					packagePage.setExistingWarningLabel(true);
					return false;
				}
				else {
					packagePage.setExistingWarningLabel(false);
					return true;
				}
			}
			else {
				packagePage.setExistingWarningLabel(false);
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Checks if class with name newClassName exists in package
	 * @param packageName name of package to search
	 * @param newClassName name of class to search
	 * @return true if class exists, false otherwise
	 */
	public boolean classExists(String packageName, String newClassName) {
		try {
			IPackageFragment searchPackage = getIPackageFragment(packageName);
			if(searchPackage == null) {
				return false;
			}
			for(ICompilationUnit cu : searchPackage.getCompilationUnits()) {
				if(cu.getElementName().equals(newClassName + ".java")) {
					return true;
				}
			}
			return false;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Returns IPackageFragment of name packageName
	 * @param packageName name of package
	 * @return IPackageFragment if package exists, null otherwise
	 * @throws JavaModelException
	 */
	public IPackageFragment getIPackageFragment(String packageName) throws JavaModelException {
		IPackageFragment[] allPkg = javaProject.getPackageFragments();
		for(IPackageFragment myPackage : allPkg) {
			if(myPackage.getElementName().equals(packageName)) {
				return myPackage;
			}
		}
		return null;
	}
	
	/**
	 * Changes all methods called in javaProject to matched the refactored method 
	 * @param javaProject Project to search in
	 * @param smellContent contains information for changing method
	 * @throws JavaModelException
	 */
	static public void changeMethodsInProject(IJavaProject javaProject, final LPLSmellContent smellContent) throws JavaModelException {
		IPackageFragment[] allPkg = javaProject.getPackageFragments();
		List<IPackageFragment> srcPkgs = new ArrayList<IPackageFragment>();
		for(IPackageFragment myPackage : allPkg) {
			if(myPackage.getKind() == IPackageFragmentRoot.K_SOURCE && myPackage.getCompilationUnits().length != 0) {
				srcPkgs.add(myPackage);
			}
		}
		
		ArrayList<ICompilationUnit> srcCompilationUnits;
		srcCompilationUnits = new ArrayList<ICompilationUnit>();
		for(IPackageFragment srcPkg : srcPkgs) {
			srcCompilationUnits.addAll(Arrays.asList(srcPkg.getCompilationUnits()));
		}
		int i = 0;
		
		for(final ICompilationUnit iCu : srcCompilationUnits) {
			/*System.out.println(i);
			System.out.println("cu name: " + iCu.getElementName());
			i++;*/
			if(iCu.getUnderlyingResource() instanceof IFile) {
				ASTParser parser = ASTParser.newParser(AST.JLS8);
				parser.setResolveBindings(true);
				parser.setKind(ASTParser.K_COMPILATION_UNIT);
				parser.setBindingsRecovery(true);
				parser.setSource(iCu);
				CompilationUnit cu = (CompilationUnit) parser.createAST(null);
				final ArrayList<Integer> methodInvocationIndexes = new ArrayList<Integer>();
				ASTVisitor visitor = new ASTVisitor() {
					public boolean visit(MethodInvocation node) {
						if(node.getName().toString().equals(smellContent.getLPLMethodObject().getName())) {
							methodInvocationIndexes.add(node.getStartPosition());
							//changeMethodCall(iCu, node.getStartPosition(), smellContent);
						}
						return true;
					}
				};
				cu.accept(visitor);
				System.out.println(methodInvocationIndexes);
				for(int j = methodInvocationIndexes.size() - 1; j >= 0; j--) {
					changeMethodCall(iCu, methodInvocationIndexes.get(j), smellContent);
				}
			}
		}
	}
	
	static public void findMethodsWithSameSignatures(IJavaProject javaProject, LPLSmellContent smellContent, ArrayList<String> parameterStringList) throws JavaModelException {
		IPackageFragment[] allPkg = javaProject.getPackageFragments();
		List<IPackageFragment> srcPkgs = new ArrayList<IPackageFragment>();
		for(IPackageFragment myPackage : allPkg) {
			if(myPackage.getKind() == IPackageFragmentRoot.K_SOURCE && myPackage.getCompilationUnits().length != 0) {
				srcPkgs.add(myPackage);
			}
		}
		
		ArrayList<ICompilationUnit> srcCompilationUnits;
		srcCompilationUnits = new ArrayList<ICompilationUnit>();
		for(IPackageFragment srcPkg : srcPkgs) {
			srcCompilationUnits.addAll(Arrays.asList(srcPkg.getCompilationUnits()));
		}
		
		for(ICompilationUnit foundCu : srcCompilationUnits) {
			IType[] allTypes = foundCu.getTypes();
			ArrayList<IMethod> foundMethods = new ArrayList<IMethod>();
			for(IType t : allTypes) {
				foundMethods.addAll(Arrays.asList(t.getMethods()));
			}
			for(IMethod candidateMethod : foundMethods) {
				//System.out.println(candidateMethod.getElementName());
				/*ArrayList<String> p = new ArrayList<String>();
				p.add("int x");
				p.add("int y");*/
				
				/* Not working yet!! */
				if(hasExtractedParameters(candidateMethod, foundCu, parameterStringList)) {
					System.out.println(candidateMethod.getElementName());
				}
			}
		}
	}
	
	protected static boolean hasExtractedParameters(IMethod candidateMethod, ICompilationUnit foundCu, ArrayList<String> extractedParameters) {
		try {
			IBuffer buffer = ((IOpenable) foundCu).getBuffer();
			int startPosition = candidateMethod.getSourceRange().getOffset();
			while (true) {
				if (buffer.getChar(startPosition) != '(') {
					startPosition += 1;
					continue;
				}
				break;
			}
			int numOfLeftPar = 0;
			int endPosition = startPosition;
			while (true) {
				if (buffer.getChar(endPosition) == '(') {
					numOfLeftPar += 1;
				} 
				else if (buffer.getChar(endPosition) == ')') {
					if (numOfLeftPar == 1)
						break;
					else
						numOfLeftPar -= 1;
				}
				endPosition += 1;
			}
			String argumentString = buffer.getContents().substring(startPosition + 1, endPosition);
			String argumentParts[] = argumentString.split(",");
			ArrayList<String> argumentPartList = new ArrayList<String>(Arrays.asList(argumentParts));
			for(int i = 0; i < argumentParts.length; i++) {
				argumentParts[i] = argumentParts[i].trim();
			}
			for(String s : extractedParameters) {
				if(!Arrays.asList(argumentParts).contains(s)) {
					return false;
				}
			}
			foundCu.discardWorkingCopy();
			return true;
		} catch (Exception e) {
				e.printStackTrace();
		}
		return false;
	}
	

	protected static void changeMethodCall(ICompilationUnit iCu, int startPosition, LPLSmellContent smellContent) {
		try {
			ICompilationUnit workingCopy = iCu
					.getWorkingCopy(new WorkingCopyOwner() {
					}, null);
			IBuffer buffer = ((IOpenable) workingCopy).getBuffer();
			while (true) {
				if (buffer.getChar(startPosition) != '(') {
					startPosition += 1;
					continue;
				}
				break;
			}
			int numOfLeftPar = 0;
			int endPosition = startPosition;
			while (true) {
				if (buffer.getChar(endPosition) == '(') {
					numOfLeftPar += 1;
				} 
				else if (buffer.getChar(endPosition) == ')') {
					if (numOfLeftPar == 1)
						break;
					else
						numOfLeftPar -= 1;
				}
				endPosition += 1;
			}
			String argumentString = buffer.getContents().substring(startPosition + 1, endPosition);
			String argumentParts[] = argumentString.split(",");
			ArrayList<String> extractedArguments;
			extractedArguments = new ArrayList<String>();
			for(int it : smellContent.getIndexList()) {
				extractedArguments.add(argumentParts[it]);
				argumentParts[it] = null;
			}
			String refactoredArgumentString = "";
			for(String s : argumentParts) {
				if(s != null) {
					refactoredArgumentString += s.trim();
					refactoredArgumentString += ", ";
				}
			}

			String extractedArgumentString = "";
			for(String s : extractedArguments) {
				extractedArgumentString += s.trim();
				extractedArgumentString += ", ";
			}
			extractedArgumentString = extractedArgumentString.substring(0, extractedArgumentString.length() - 2);
			String replaceSignature = "(";
			replaceSignature += refactoredArgumentString;
			replaceSignature += "new " + smellContent.getNewClassName() + "(" + extractedArgumentString + ")";
			replaceSignature += ")";
			
			buffer.replace(startPosition, endPosition - startPosition + 1, replaceSignature);
			
			workingCopy.reconcile(ICompilationUnit.NO_AST, false, null, null);
			workingCopy.commitWorkingCopy(false, null);
			workingCopy.discardWorkingCopy();
			workingCopy.discardWorkingCopy();
		} catch (Exception e) {
		}
	}
	
	
	
	
}
