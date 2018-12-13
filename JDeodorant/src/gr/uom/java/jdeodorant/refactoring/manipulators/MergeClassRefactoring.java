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
public class MergeClassRefactoring extends Refactoring {
	private ClassObjectCandidate parentClass;
	private ClassObjectCandidate childClass;

	private List<FieldObject> parentFieldList;
	private List<ConstructorObject> parentConstructorList;
	private List<MethodObject> parentMethodList;
	private List<FieldObject> childFieldList;
	private List<ConstructorObject> childConstructorList;
	private List<MethodObject> childMethodList;

	private List<String> parentOriginalContentList = new ArrayList<String>();
	private List<String> childOriginalContentList = new ArrayList<String>();
	private String parentOriginalContent = "";
	private String childOriginalContent = "";

	private List<String> parentRefactoredContentList = new ArrayList<String>();
	private List<String> childRefactoredContentList = new ArrayList<String>();
	private String parentRefactoredContent = "";
	private String childRefactoredContent = "";

	/*
 	public MergeClassRefactoring() {
		targetClass = new ClassObjectCandidate();

		fieldList = new ArrayList<FieldObject>();
		constructorList = new ArrayList<MethodObject>();
		methodList = new ArrayList<MethodObject>();
	}
	*/

	public MergeClassRefactoring(ClassObjectCandidate parent, ClassObjectCandidate child) {
		parentClass = parent;
		childClass = child;

		parentFieldList = parent.getFieldList();
		parentConstructorList = parent.getConstructorList();
		parentMethodList = parent.getMethodList();

		childFieldList = child.getFieldList();
		childConstructorList = child.getConstructorList();
		childMethodList = child.getMethodList();

		parentOriginalContentList = parent.getContent();
		childOriginalContentList = child.getContent();
	}

    /**
     * @author Taeyoung Son
     * @return List<String> form of fieldList
     */
	private List<String> stringFieldList(ClassObjectCandidate arg){
		List<String> ret = new ArrayList<String>();
		//ret.add("\n");
		for(FieldObject fo : arg.getFieldList()) {
			String s = fo.toString();
			if(s.contains("protected")) {
				String[] tokens = s.split("\\s");
				s = "";
				for(int i=0; i<tokens.length; i++) {
					if(tokens[i].equals("protected")) {
						tokens[i] = "private";
					}
					s = s + " " + tokens[i];
				}
				s = s.substring(1);
			}
			if(s.contains(".")) {
				String[] tokens = s.split("\\s");
				s = "";
				for(int i=0; i<tokens.length; i++) {
					if(tokens[i].contains(".")) {
						tokens[i] = dotParser(tokens[i]);
					}
					s = s + " " + tokens[i];
				}
				s = s.substring(1);
			}
			ret.add("	" + s + ";");
		}
		return ret;
	}

    /**
     * @author Taeyoung Son
     * @return List<String> form of MethodList
     */
	private List<String> stringMethodList(ClassObjectCandidate arg){
		List<String> ret = new ArrayList<String>();
		for(MethodObject mo : arg.getMethodList()) {
			ret.add(mo.toString());
		}

		return ret;
	}

	/**
     * @author Taeyoung Son
     * @return List<String> form of ConstructorList
     */
	private List<String> stringConstructorList(ClassObjectCandidate arg){
		List<String> ret = new ArrayList<String>();
		for(ConstructorObject co : arg.getConstructorList()) {
			ret.add(co.toString());
		}

		return ret;
	}

    /**
     * @author Taeyoung Son
     * @param fullContent the content collected by getContent()
     * @param methodName the name of method you want from content
     * @return content of specific method of given name
     */
	private List<String> methodContentCreator(List<String> fullContent, String methodName) {
		List<String> ret = new ArrayList<String>();
		int _brackets = 0;
		boolean flag = false;
		for(String s : fullContent) {
			if(flag && _brackets > 0) {
				if(s.contains("{")) {
					_brackets++;
				}
				else if(s.contains("}")) {
					_brackets--;
				}
				ret.add(s);
			}
			// WARN :: What if "methodName" used in other context?
			else if(s.contains(methodName) && !flag) {
				_brackets++;
				flag = true;
				ret.add(s);
			}
			else if(_brackets == 0 && flag) break;
		}
		return ret;
	}

    /**
     * @author Taeyoung Son
     * @param fullContent the content collected by getContent()
     * @param methodName the name of method you want from content
     * @return content of specific method of given name
     */
	private List<String> constructorContentCreator(List<String> fullContent) {
		List<String> ret = new ArrayList<String>();

		return ret;
	}

    /**
     * merge parent and child together for abstract, interface code smell
     * @author Taeyoung Son, JuYong Lee
     */
	public void mergeIntoChild(){
		assert this.parentClass.getNumChild() == 1;

		// Abstract Class with One Child
		if(this.parentClass.isAbstract()) {
			List<String> myContent = this.parentOriginalContentList;
			List<String> childContent = this.childOriginalContentList;

			List<String> newFieldList = new ArrayList<String>();
			for(String s : stringFieldList(parentClass)) {
				if(!stringFieldList(childClass).contains(s)) {
					newFieldList.add(s);
				}
			}
			newFieldList.addAll(stringFieldList(childClass));

			// Consider Constructors
			List<String> newConstructorList = new ArrayList<String>();
			for(ConstructorObject _constructor  : this.childConstructorList) {
				// Consider super()
				String content = _constructor.toString();

			}

            // Conside packages
            List<String> newImportList = new ArrayList<String>();
            for(String s : myContent){
                if(s.length() > 5 && s.substring(0,6).equals("import")){
                    newImportList.add(s);
                }
            }
            for(String s : childContent){
                if(s.length() > 5 && s.substring(0,6).equals("import")){
                    newImportList.add(s);
                }
            }

			// Consider Methods
			List<String> newMethodList = new ArrayList<String>();
			List<String> myMethodObjectList = stringMethodList(parentClass);
			List<String> childMethodObjectList = stringMethodList(childClass);
			for (String s : myMethodObjectList) {
				if (childMethodObjectList.contains(s) || s.contains("abstract") || s.contains(dotParser(parentClass.getName()))) {
					if (s.contains("abstract")) {
						int index = s.indexOf("abstract");
						String former = s.substring(0, index - 1);
						String latter = s.substring(index + 8, s.length());
						s = former + latter;
					} else if (s.contains(".")) {
						String[] tokens = s.split("\\s");
						s = "";
						for (int i = 0; i < tokens.length; i++) {
							if (tokens[i].contains(".")) {
								tokens[i] = dotParser(tokens[i]);
							}
							s = s + " " + tokens[i];
						}
						s = s.substring(1);
					} else if (s.contains(dotParser(parentClass.getName()))) {
						int sidx=0;
						int fidx=0;
						for(int i=0; i<s.length(); i++) {
							if(s.charAt(i) == dotParser(parentClass.getName()).charAt(0)) {
								sidx = i;
								fidx = i + dotParser(parentClass.getName()).length();
								break;
							}
						}
						s = s.substring(0,sidx) + dotParser(childClass.getName()) + s.substring(fidx, s.length());

					}
					for (String t : this.methodContentCreator(childContent, s)) {
						newMethodList.add(t);
					}
					childMethodObjectList.remove(s);
				} else {
					for (String t : this.methodContentCreator(myContent, s)) {
						newMethodList.add(t);
					}
				}
			}
			for (String s : childMethodObjectList) {
				if (s.contains(dotParser(childClass.getName()))) {
					continue;
				} else if (!myMethodObjectList.contains(s)) {
					for (String t : this.methodContentCreator(childContent, s)) {
						newMethodList.add(t);
					}
				}
			}

			this.childRefactoredContentList.add(childContent.get(0) + "\n");
            this.childRefactoredContentList.addAll(newImportList);
			this.childRefactoredContentList.add("public class " + dotParser(childClass.getName()) + "{");
			this.childRefactoredContentList.addAll(newFieldList);
			this.childRefactoredContentList.addAll(newMethodList);
			this.childRefactoredContentList.add("}");

			this.parentRefactoredContentList.add("/*");
			for(String c : this.parentOriginalContentList) {
				this.parentRefactoredContentList.add(c);
			}
			this.parentRefactoredContentList.add("*/");
		}

		// Interface Class with One Child
		if(this.parentClass.isInterface()) {
			String blank = " ";
			String childName = dotParser(childClass.getName());
			String parentName = dotParser(parentClass.getName());
			String initialPart = "class" + blank + childName + blank + "implements" + blank + parentName;
			int initialIdx = -1;
			for(int i = 0; i < this.childOriginalContentList.size(); i++) {
				if(this.childOriginalContentList.get(i).contains(initialPart)) {
					initialIdx = i;
					break;
				}
			}

			// Child Refactoring
			if(initialIdx == -1) {
				this.childRefactoredContentList.add("/*");
				for(String c : this.childOriginalContentList) {
					this.childRefactoredContentList.add(c);
				}
				this.childRefactoredContentList.add("*/");
			} else {
				for (int i = 0; i < this.childOriginalContentList.size(); i++) {
					if (i == initialIdx) {
						this.childRefactoredContentList.add(this.childClass.getAccess().toString() + blank + "class"
								+ blank + childName + blank + "{");
					} else if (this.childOriginalContentList.get(i).contains("@Override")) {
						// Get Rid of @Override
					} else {
						this.childRefactoredContentList.add(this.childOriginalContentList.get(i));
					}
				}
			}


			// Parent Refactoring
			this.parentRefactoredContentList.add("/*");
			for(String c : this.parentOriginalContentList) {
				this.parentRefactoredContentList.add(c);
			}
			this.parentRefactoredContentList.add("*/");
		}
	}

    /**
     * @author Taeyoung Son
     * process child and parents' refactoring
     */
	public void processRefactoring() {
		processRefactoringParent();
		processRefactoringChild();
	}

    /**
     * @author Taeyoung Son
     * process parents' refactoring
     */
	public void processRefactoringParent() {
		SystemObject systemObject = ASTReader.getSystemObject();
		if (systemObject != null) {
			IFile _file = parentClass.getIFile();
			ICompilationUnit _compilationUnit = (ICompilationUnit) JavaCore.create(_file);

			try {
				ICompilationUnit _CUorigin = _compilationUnit.getWorkingCopy(new WorkingCopyOwner() {}, null);
				IBuffer _bufferOrigin = ((IOpenable) _CUorigin).getBuffer();

				_bufferOrigin.replace(0, _bufferOrigin.getLength(), this.parentRefactoredContent);

				_CUorigin.reconcile(ICompilationUnit.NO_AST, false, null, null);
				_CUorigin.commitWorkingCopy(false, null);
				_CUorigin.discardWorkingCopy();
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
	}
/**
     * @author Taeyoung Son
     * process childs' refactoring
     */
	public void processRefactoringChild() {
		SystemObject systemObject = ASTReader.getSystemObject();
		if (systemObject != null) {
			IFile _file = childClass.getIFile();
			ICompilationUnit _compilationUnit = (ICompilationUnit) JavaCore.create(_file);

			try {
				ICompilationUnit _CUorigin = _compilationUnit.getWorkingCopy(new WorkingCopyOwner() {}, null);
				IBuffer _bufferOrigin = ((IOpenable) _CUorigin).getBuffer();

				_bufferOrigin.replace(0, _bufferOrigin.getLength(), this.childRefactoredContent);

				_CUorigin.reconcile(ICompilationUnit.NO_AST, false, null, null);
				_CUorigin.commitWorkingCopy(false, null);
				_CUorigin.discardWorkingCopy();
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 *  Link List of String of Contents into one String line
	 */
	public void buildContentInOneString() {
		for(String c : this.parentOriginalContentList) {
			this.parentOriginalContent += c + "\r\n";
		}
		for(String c : this.childOriginalContentList) {
			this.childOriginalContent += c + "\r\n";
		}

		for(String c : this.parentRefactoredContentList) {
			this.parentRefactoredContent += c + "\r\n";
		}
		for(String c : this.childRefactoredContentList) {
			this.childRefactoredContent += c + "\r\n";
		}
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
				"<<=", ">>=", ">>>=", "^=", "|=", "%=", ";"};

		for(int i = 0; i < operator.length; i++) {
			for(int j = 0; j < operator.length; j++) {
				if(content.contains(operator[i] + target + operator[j])) {
					return true;
				}
			}
		}

		return false;
	}

    /**
     * @author Juyong Lee
     * @return this class's name
     */
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

    /**
     * @author Juyong Lee
     * @return the String form of child's original content
     */
	public String getOriginalContent() {
		return this.childOriginalContent;
	}

    /**
     * @author Juyong Lee
     * @return the String form of refactored content
     */
	public String getRefactoredContent() {
		return this.childRefactoredContent;
	}
}
