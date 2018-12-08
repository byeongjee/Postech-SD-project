package gr.uom.java.jdeodorant.refactoring.manipulators;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import gr.uom.java.ast.ASTReader;
import gr.uom.java.ast.AbstractMethodDeclaration;
import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.ClassObjectCandidate;
import gr.uom.java.ast.CompilationErrorDetectedException;
import gr.uom.java.ast.CompilationUnitCache;
import gr.uom.java.ast.MethodObject;
import gr.uom.java.ast.SystemObject;
import gr.uom.java.ast.TypeObject;
import gr.uom.java.jdeodorant.preferences.PreferenceConstants;
import gr.uom.java.jdeodorant.refactoring.Activator;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSlice;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSliceGroup;
import gr.uom.java.jdeodorant.refactoring.manipulators.ParameterMethodRefactoring;

import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.texteditor.ITextEditor;
import java.util.HashSet;
import gr.uom.java.ast.ClassObjectCandidate;
import gr.uom.java.ast.decomposition.CompositeStatementObject;
import gr.uom.java.ast.decomposition.MethodBodyObject;
import org.eclipse.jdt.core.dom.Statement;

/**
 * Resolve Unnecessary Parameter, and Progress Refactoring 
 * 
 * @Test : BlackBoxTest/SpeculatvieGenerality
 * @author JuYong Lee
 */
public class ParameterMethodRefactoring extends Refactoring {
	private ClassObjectCandidate targetClass;
	private MethodObject targetMethod;
	
    private Integer numUnusedParameter;
	private List<String> unusedParameterList;
	private List<String> usedParameterList;
	private List<Integer> unusedParameterIndex;
	private String originalContent;
	private String refactoredContent;
	
	public ParameterMethodRefactoring() {
        this.numUnusedParameter = 0;
        this.unusedParameterList = new ArrayList<String>();
        this.usedParameterList = new ArrayList<String>();
        this.unusedParameterIndex = new ArrayList<Integer>();
	}
	
	public ParameterMethodRefactoring(ClassObjectCandidate _class, MethodObject _method) {
		this.targetClass = _class;
		this.targetMethod = _method;
		
        this.numUnusedParameter = 0;
        this.unusedParameterList = new ArrayList<String>();
        this.usedParameterList = new ArrayList<String>();
        this.unusedParameterIndex = new ArrayList<Integer>();
        
        // Set Original Content
		MethodBodyObject _methodBody = targetMethod.getMethodBody();
		if (_methodBody != null) {
			CompositeStatementObject _compositeStatement = _methodBody.getCompositeStatement();
			if (_compositeStatement != null) {
				Statement _statement = _compositeStatement.getStatement();
				if (_statement != null) {
					this.originalContent = _statement.toString();
				}
			}
		}
	}

	public ParameterMethodRefactoring(MethodObject _method) {
		this.targetClass = null;
		this.targetMethod = _method;
		
        this.numUnusedParameter = 0;
        this.unusedParameterList = new ArrayList<String>();
        this.usedParameterList = new ArrayList<String>();
        this.unusedParameterIndex = new ArrayList<Integer>();
		MethodBodyObject _methodBody = targetMethod.getMethodBody();
		if (_methodBody != null) {
			CompositeStatementObject _compositeStatement = _methodBody.getCompositeStatement();
			if (_compositeStatement != null) {
				Statement _statement = _compositeStatement.getStatement();
				if (_statement != null) {
					this.originalContent = _statement.toString();
				}
			}
		}
	}

	public void setUnusedParameterList() {
		int unusedPNum = 0;
		List<String> unusedPList = new ArrayList<String>();
		List<Integer> unusedPIndex = new ArrayList<Integer>();
		if (this.originalContent != null) {
			// Get Parameters
			int pNum = targetMethod.getParameterList().size();

			for (int p = 0; p < pNum; p++) {
				// Check the Usage
				String pTarget = targetMethod.getParameter(p).getName();

				if (!checkContainance(originalContent, pTarget)) {
					unusedPList.add(pTarget);
					unusedPIndex.add(p);
					unusedPNum++;
				}
			}
		}
		
		this.numUnusedParameter = unusedPNum ;
		this.unusedParameterList = unusedPList;
		this.unusedParameterIndex = unusedPIndex;
	}
	
	public List<String> getUnusedParameterList() {
		return this.unusedParameterList;
	}
	
	/**
	 * Set List of (name, type) of usedParameterList of target
	 */
	public void setUsedParameterList() {
		List<String> res = new ArrayList<String>();
		List<String> pList = this.targetMethod.getParameterNameList();
	
		for(int i = 0; i < pList.size(); i++) {
			boolean flagUsed = false;
			String param = pList.get(i);

			if (this.checkContainance(originalContent, param)) {
				
				flagUsed = true;
			}

			if (flagUsed) {
				for (String paramDeclaration : this.targetMethod.getParameterTypeAndNameList()) {
					if (this.checkContainance(paramDeclaration + " ", param)) {
						res.add(paramDeclaration);
					}
				}
			}
		}
		
		this.usedParameterList = res;
		return;
	}
	
	public List<String> getUsedParameterList() {
		return this.usedParameterList;
	}
	
	/**
	 *  Delete the Unnecessary Parameter Declaration Code in Method Defining Code
	 *  @author JuYong Lee
	 *  @return Codes with out Unnecessary Parameter 
	 */
	public void resolveUnnecessaryParameters() {
		assert this.targetClass.getCodeSmellType() == "Unnecessary Parameters";
		List<String> orgContent = this.targetClass.getContent();
		List<String> newContent = new ArrayList<String>();
		
		
		// Find the target Method Poisition
		int declarationIdx = -1;
		for(int i = 0; i < orgContent.size(); i++) {
			if(this.checkContainance(orgContent.get(i), targetMethod.getName())
					&& this.checkContainance(orgContent.get(i), this.dotParser(targetMethod.getAccess().toString()))
					&& this.checkContainance(orgContent.get(i), this.dotParser(targetMethod.getReturnType().toString()))) {
				declarationIdx = i;
				break;
			}
		}
		
		// Write New Content
  		for(int i = 0; i < orgContent.size(); i++) {
			if(i == declarationIdx) {
				String newDeclarationCode = "\t";
				if(targetMethod.isStatic()) {
					newDeclarationCode += "static ";
				}
				newDeclarationCode += targetMethod.getAccess().toString();
				if(targetMethod.getAccess().toString() != "") {
					newDeclarationCode += " ";
				}
				newDeclarationCode += this.dotParser(targetMethod.getReturnType().toString()) + " ";
				newDeclarationCode += this.dotParser(targetMethod.getName()) + "(";
				
				for(int p = 0; p < this.usedParameterList.size(); p++) {
					String _par = this.usedParameterList.get(p);
					newDeclarationCode += _par;
					if(p < this.usedParameterList.size() - 1) {
						newDeclarationCode += ", ";
					} else {
						newDeclarationCode += ") " + "{";
					}
				}
				if(this.usedParameterList.size() == 0) {
					newDeclarationCode += ") " + "{";
				}
				
				newContent.add(newDeclarationCode);
			} else {
				newContent.add(orgContent.get(i));
			}
		}
		
  		// In One String
  		refactoredContent = "";
		for(String c : newContent) {
			refactoredContent += c + "\r\n";
		};
		
		return;
	}
	
	/**
	 *  Write "refactoredContent" On "target class" JavaFile
	 */
	public void processRefactoring() {
		SystemObject systemObject = ASTReader.getSystemObject();
		if (systemObject != null) {
			IFile _file = targetClass.getIFile();
			ICompilationUnit _compilationUnit = (ICompilationUnit) JavaCore.create(_file);

			try {
				ICompilationUnit _CUorigin = _compilationUnit.getWorkingCopy(new WorkingCopyOwner() {}, null);
				IBuffer _bufferOrigin = ((IOpenable) _CUorigin).getBuffer();

				_bufferOrigin.replace(0, _bufferOrigin.getLength(), this.refactoredContent);

				_CUorigin.reconcile(ICompilationUnit.NO_AST, false, null, null);
				_CUorigin.commitWorkingCopy(false, null);
				_CUorigin.discardWorkingCopy();
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
	}


	public String getOriginalContent() {
		String res = "";
		
		for(String c : this.targetClass.getContent()) {
			res += c + "\r\n";
		}
		
		return res;
	}
	
	public String getRefactoredContent() {
		return this.refactoredContent;
	}
	
    /**
     * @author Taeyoung Son
     * @param to_be_parsed string to be parsed which contains dot
     * @return the last string of given string split with dot
     */
	private String dotParser(String to_be_parsed) {
		String[] tokens = to_be_parsed.split("\\.");
		return tokens[tokens.length-1];
	}

	/**
	TestClass * Check "content" contains "target"
	 * 
	 * @author JuYong Lee
	 * @param method
	 * @param var
	 * @return
	 */
	public boolean checkContainance(String content, String target) {
		String[] operator = { " ", "(", ")", "[", "]", ".",	"+", "-", "*", "/", "%",
				"!", "~", "++", "--", "<<", ">>", ">>>", ">", "<", ">= ", "<=", "==", "!=",
				"&", "^", "|", "&&", "||", "?", "=", "+=", "/=", "&=", "*=", "-=", 
				"<<=", ">>=", ">>>=", "^=", "|=", "%=", ";", "\t", "\r", "\n"}; 
		
		for(int i = 0; i < operator.length; i++) {
			for(int j = 0; j < operator.length; j++) {
				if(content.contains(operator[i] + target + operator[j])) {
					return true;
				}
			}
		}
		 
		return false;
	}
	
	@Override
	public String getName() {
		return this.getName();
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		return null;
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		return null;
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		return null;
	}

	public List<Integer> getUnusedParameterIndex() {
		return unusedParameterIndex;
	}
}
