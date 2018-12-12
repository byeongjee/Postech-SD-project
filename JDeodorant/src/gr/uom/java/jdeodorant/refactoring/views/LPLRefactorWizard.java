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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import gr.uom.java.ast.LPLMethodObject;
import gr.uom.java.ast.LPLSmellContent;

public class LPLRefactorWizard extends Wizard {
	private IJavaProject javaProject;
	private LPLMethodObject methodToRefactor;
	private LPLRefactorInitialPage initialPage;
	private LPLRefactorClassNamePage namePage;
	private LPLRefactorSelectPackagePage packagePage;
	
	public static class MyCustomDialog extends WizardDialog {

		/**
		 * Constructor for MyCustonDialog
		 * @param parentShell
		 * @param newWizard
		 */
		public MyCustomDialog(Shell parentShell, IWizard newWizard) {
		    super(parentShell, newWizard);
		}

		/**
		 * Sets the names of the original finish and cancel buttons to 'Yes' and 'No' buttons
		 */
		@Override
		public void createButtonsForButtonBar(Composite parent){
		    super.createButtonsForButtonBar(parent);
		    Button finishButton = getButton(IDialogConstants.FINISH_ID);
		    finishButton.setText("Yes");
		    Button noButton = getButton(IDialogConstants.CANCEL_ID);
		    noButton.setText("No");
		}
	}
	
	/**
	 * Constructor for LPLRefactorWizard.
	 * @param javaProject the IJavaProject to refactor
	 * @param methodToRefactor the LPLMethodObject of the method to refactor
	 */
	public LPLRefactorWizard(IJavaProject javaProject, LPLMethodObject methodToRefactor) {
		super();
		setNeedsProgressMonitor(true);
		this.javaProject = javaProject;
		this.methodToRefactor = methodToRefactor;
	}
	
	/**
	 * Returns the title of the popup.
	 */
	@Override
	public String getWindowTitle() {
		return "Refactoring";
	}
	
	/**
	 * Add popup pages to the wizard. There are three pages in total
	 */
	@Override
	public void addPages() {
		initialPage = new LPLRefactorInitialPage(methodToRefactor, javaProject);
		namePage = new LPLRefactorClassNamePage();
		packagePage = new LPLRefactorSelectPackagePage(javaProject);
		addPage(initialPage);
		addPage(namePage);
		addPage(packagePage);
	}
	
	/**
	 * The function that is called when the popup is finished. It refactors the project based
	 * on the input the user gave.
	 */
	@Override
	public boolean performFinish() {
		try {
			LPLSmellContent smellContent = new LPLSmellContent(methodToRefactor, initialPage.getParameterIndexList(), namePage.getClassName(), namePage.getParameterName());
			
			IPackageFragment pf = getIPackageFragment(packagePage.getPackageName()); 
			assert(pf != null);
			String className = namePage.getClassName();
			String parameterName = namePage.getParameterName();
			List<String> parameterTypes = initialPage.getExtractParameterTypes();
			List<String> parameterNames = initialPage.getExtractParameterNames();
			IMethod convertedIMethod = methodToRefactor.toIMethod(javaProject);
			String tempVarInitializeCode = LPLMethodObject.codeForInitializingTempVars(parameterTypes, parameterNames, parameterName);

			createNewParameterClass(pf, className, parameterTypes, parameterNames);

			changeMethodsInProject(javaProject, smellContent.getLPLMethodObject().getName(), smellContent.getExtractedParameterIndicesList(), smellContent.getNewClassName());		

			changeOriginalMethodSource(smellContent, convertedIMethod, tempVarInitializeCode);
			
			ArrayList<String> parameterStringList = new ArrayList<String>();
			for(int i = 0; i < parameterTypes.size(); i++) {
				parameterStringList.add(parameterTypes.get(i) + " " + parameterNames.get(i));
			}
			findAndChangeMethodsWithSameSignatures(javaProject, smellContent, parameterStringList, tempVarInitializeCode);
			
		} catch (Exception e) {
		}
		return true;
	}

	private void changeOriginalMethodSource(LPLSmellContent smellContent, IMethod convertedIMethod,
			String tempVarInitializeCode) throws JavaModelException {
		ICompilationUnit cu = convertedIMethod.getCompilationUnit();
		ICompilationUnit workingCopy = createWorkingCopy(cu);
		IBuffer buffer = ((IOpenable) workingCopy).getBuffer();
		LPLMethodObject.editParameterFromBuffer(buffer, convertedIMethod, initialPage.getParameterIndexList(), smellContent, tempVarInitializeCode);
		finishEditingWorkingCopy(workingCopy);
	}

	private void createNewParameterClass(IPackageFragment pf, String className, List<String> parameterTypes,
			List<String> parameterNames) throws JavaModelException {
		ICompilationUnit cu = pf.createCompilationUnit(className + ".java", "", false, null);
		ICompilationUnit workingCopy = createWorkingCopy(cu);
		IBuffer buffer = ((IOpenable) workingCopy).getBuffer();
		LPLMethodObject.fillNewParameterClass(buffer, pf, className, parameterTypes, parameterNames);
		finishEditingWorkingCopy(workingCopy);
	}
	
	private static void finishEditingWorkingCopy(ICompilationUnit workingCopy) throws JavaModelException {
		workingCopy.reconcile(ICompilationUnit.NO_AST, false, null, null);
		workingCopy.commitWorkingCopy(false, null);
		workingCopy.discardWorkingCopy();
	}

	private ICompilationUnit createWorkingCopy(ICompilationUnit cu) throws JavaModelException {
		ICompilationUnit workingCopy = cu
				.getWorkingCopy(new WorkingCopyOwner() {
				}, null);
		return workingCopy;
	}
	
	/**
	 * Activates/Deactivates the finish button.
	 * If the refactoring to be performed is invalid (ex: class already exists),
	 * the finish button is turned off.
	 */
	@Override
	public boolean canFinish() {
		if(getContainer().getCurrentPage() == packagePage) {
			if(packagePage.getCanFinishPage()) {
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
	 * @param methodName the name of the method to search in project
	 * @param extractedParameterIndices The index of the parameters to extract
	 * @param className the name of the new class
	 * @throws JavaModelException
	 */
	static public void changeMethodsInProject(IJavaProject javaProject, final String methodName, List<Integer> extractedParameterIndices, String newClassName) throws JavaModelException {
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
						if(node.getName().toString().equals(methodName)) {
							methodInvocationIndexes.add(node.getStartPosition());
						}
						return true;
					}
				};
				cu.accept(visitor);
				for(int j = methodInvocationIndexes.size() - 1; j >= 0; j--) {
					changeMethodCall(iCu, methodInvocationIndexes.get(j), extractedParameterIndices, newClassName);
				}
			}
		}
	}
	
	/**
	 * Finds methods in the project that have the same extacted parameters, and extractes them too.
	 * @param javaProject project to search
	 * @param smellContent contains information about new class
	 * @param parameterStringList list of parameters that are extracted
	 * @param tempVarInitializeCode string to insert in method body after parameters are extracted
	 * @throws JavaModelException
	 */
	static public void findAndChangeMethodsWithSameSignatures(IJavaProject javaProject, LPLSmellContent smellContent, 
			ArrayList<String> parameterStringList, String tempVarInitializeCode) throws JavaModelException {
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
			if(foundCu.getElementName().equals(smellContent.getNewClassName() + ".java"))
				continue;
			IType[] allTypes = foundCu.getTypes();
			ArrayList<IMethod> foundMethods = new ArrayList<IMethod>();
			for(IType t : allTypes) {
				foundMethods.addAll(Arrays.asList(t.getMethods()));
			}
			for(IMethod candidateMethod : foundMethods) {
				if(hasExtractedParameters(candidateMethod, foundCu, parameterStringList)) {
					SameLPLParametersWizard wizard = new SameLPLParametersWizard(candidateMethod);
					//WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
					MyCustomDialog dialog = new MyCustomDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
					dialog.open();
					if(wizard.getDoExtraction()) {
						List<Integer> extractedParameterIndices = getExtractedParameterIndicesFrom(candidateMethod, parameterStringList);
						changeMethodDeclarationWithSameParameters(candidateMethod, parameterStringList, smellContent, tempVarInitializeCode);
						changeMethodsInProject(javaProject, candidateMethod.getElementName(), extractedParameterIndices, smellContent.getNewClassName());
					}
				}
			}
		}
	}
	
	private static List<Integer> getExtractedParameterIndicesFrom(IMethod candidateMethod,
			ArrayList<String> parameterStringList) {
		try {
			IMethod convertedIMethod = candidateMethod;
			int startPosition = convertedIMethod.getSourceRange().getOffset();
			ICompilationUnit workingCopy = convertedIMethod.getCompilationUnit().getWorkingCopy(new WorkingCopyOwner() {}, null);
			IBuffer buffer = workingCopy.getBuffer();
			
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
			
			List<Integer> parameterIndicesList = new ArrayList<Integer>();
			
			for(int i = 0; i < argumentParts.length; i++) {
				argumentParts[i] = argumentParts[i].trim();
				if(parameterStringList.contains(argumentParts[i])) {
					parameterIndicesList.add(i);
				}
			}
			finishEditingWorkingCopy(workingCopy);
			return parameterIndicesList;

		} catch (Exception e) {
				e.printStackTrace();
				return new ArrayList<Integer>();
		}
	}

	protected static void changeMethodDeclarationWithSameParameters(IMethod method, ArrayList<String> parameterList, 
			LPLSmellContent smellContent, String tempVarInitializeCode) {
		try {
			IMethod convertedIMethod = method;
			int startPosition = convertedIMethod.getSourceRange().getOffset();
			ICompilationUnit workingCopy = convertedIMethod.getCompilationUnit().getWorkingCopy(new WorkingCopyOwner() {}, null);
			IBuffer buffer = workingCopy.getBuffer();
			
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
			for(int i = 0; i < argumentParts.length; i++) {
				argumentParts[i] = argumentParts[i].trim();
				if(parameterList.contains(argumentParts[i])) {
					argumentParts[i] = null;
				}
			}
			String refactoredArgumentString = "";
			for(String s : argumentParts) {
				if(s != null) {
					refactoredArgumentString += s;
					refactoredArgumentString += ", ";
				}
			}
			String replaceSignature = "(";
			replaceSignature += refactoredArgumentString;
			replaceSignature += smellContent.getNewClassName() + " " + smellContent.getNewParameterName();
			replaceSignature += ")";
			
			int defPosition = endPosition;
			while (buffer.getChar(defPosition) != '{') {
				defPosition += 1;
			}
			defPosition += 1;
			
			
			buffer.replace(defPosition, 0, tempVarInitializeCode);
			buffer.replace(startPosition, endPosition - startPosition + 1, replaceSignature);
			
			finishEditingWorkingCopy(workingCopy);
		} catch (Exception e) {
				e.printStackTrace();
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
	

	protected static void changeMethodCall(ICompilationUnit iCu, int startPosition, List<Integer> extractedParameterIndices, String newClassName) {
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
			for(int it : extractedParameterIndices) {
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
			replaceSignature += "new " + newClassName + "(" + extractedArgumentString + ")";
			replaceSignature += ")";
			
			buffer.replace(startPosition, endPosition - startPosition + 1, replaceSignature);
			
			finishEditingWorkingCopy(workingCopy);
			workingCopy.discardWorkingCopy();
		} catch (Exception e) {
		}
	}
	
	
	
	
}
