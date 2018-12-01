package gr.uom.java.jdeodorant.refactoring.manipulators;

import gr.uom.java.ast.ASTReader;
import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.ClassObjectCandidate;
import gr.uom.java.ast.CommentObject;
import gr.uom.java.ast.CompilationUnitCache;
import gr.uom.java.ast.ConstructorObject;
import gr.uom.java.ast.FieldObject;
import gr.uom.java.ast.MethodObject;
import gr.uom.java.ast.SystemObject;
import gr.uom.java.ast.decomposition.CompositeStatementObject;
import gr.uom.java.ast.decomposition.MethodBodyObject;
import gr.uom.java.ast.decomposition.cfg.MethodCallAnalyzer;
import gr.uom.java.ast.util.ExpressionExtractor;
import gr.uom.java.ast.util.MethodDeclarationUtility;
import gr.uom.java.ast.util.StatementExtractor;
import gr.uom.java.ast.util.TypeVisitor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.TextEditGroup;

import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

/**
 * Resolve Unnecessary Generalization in Class Hierarchy,
 * and Progress Refactoring 
 * @author 손태영, 이주용
 */
public class DeleteClassRefactoring extends Refactoring {
	private ClassObjectCandidate targetClass;
	
	private String originalContent;
	private String refactoredContent;
	
/*	public DeleteClassRefactoring() {
		targetClass = new ClassObjectCandidate();
		List<String> originalContentList = this.targetClass.getContent();
		for(String c : originalContentList) {
			originalContent += c + "\r\n";
		}
		refactoredContent = "";
	}*/
		
	public DeleteClassRefactoring(ClassObjectCandidate target) {
		targetClass = target;
		List<String> originalContentList = this.targetClass.getContent();
		for(String c : originalContentList) {
			originalContent += c + "\r\n";
		}
		refactoredContent = "";
	}

	/**
	 *  Rather than Deleting, Make the codes to be comments for stability issue
	 */
	public void commentizeWholeContent() {
		// Wrap as comments
		List<String> resolvedClassContent = targetClass.getContent();
		String newContent = "/*\r\n";
		for( String c : resolvedClassContent) {
			newContent += c + "\r\n";
		}
		newContent += "\r\n*/";
		
		this.refactoredContent = newContent;
		return;
	}
	
	/**
	 *  Rewrite The Contents
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
}