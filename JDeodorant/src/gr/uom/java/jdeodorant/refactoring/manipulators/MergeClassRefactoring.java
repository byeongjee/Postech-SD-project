//package gr.uom.java.jdeodorant.refactoring.manipulators;
//
//import gr.uom.java.ast.ASTReader;
//import gr.uom.java.ast.ClassObject;
//import gr.uom.java.ast.ClassObjectCandidate;
//import gr.uom.java.ast.CommentObject;
//import gr.uom.java.ast.CompilationUnitCache;
//import gr.uom.java.ast.ConstructorObject;
//import gr.uom.java.ast.FieldObject;
//import gr.uom.java.ast.MethodObject;
//import gr.uom.java.ast.decomposition.CompositeStatementObject;
//import gr.uom.java.ast.decomposition.MethodBodyObject;
//import gr.uom.java.ast.decomposition.cfg.MethodCallAnalyzer;
//import gr.uom.java.ast.util.ExpressionExtractor;
//import gr.uom.java.ast.util.MethodDeclarationUtility;
//import gr.uom.java.ast.util.StatementExtractor;
//import gr.uom.java.ast.util.TypeVisitor;
//
//import java.text.MessageFormat;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.eclipse.core.runtime.CoreException;
//import org.eclipse.core.runtime.IProgressMonitor;
//import org.eclipse.core.runtime.OperationCanceledException;
//import org.eclipse.jdt.core.ICompilationUnit;
//import org.eclipse.jdt.core.IType;
//import org.eclipse.jdt.core.JavaModelException;
//import org.eclipse.jdt.core.dom.AST;
//import org.eclipse.jdt.core.dom.ASTNode;
//import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
//import org.eclipse.jdt.core.dom.Assignment;
//import org.eclipse.jdt.core.dom.Block;
//import org.eclipse.jdt.core.dom.BodyDeclaration;
//import org.eclipse.jdt.core.dom.CastExpression;
//import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
//import org.eclipse.jdt.core.dom.ClassInstanceCreation;
//import org.eclipse.jdt.core.dom.CompilationUnit;
//import org.eclipse.jdt.core.dom.EnumDeclaration;
//import org.eclipse.jdt.core.dom.Expression;
//import org.eclipse.jdt.core.dom.ExpressionStatement;
//import org.eclipse.jdt.core.dom.FieldAccess;
//import org.eclipse.jdt.core.dom.FieldDeclaration;
//import org.eclipse.jdt.core.dom.IBinding;
//import org.eclipse.jdt.core.dom.IExtendedModifier;
//import org.eclipse.jdt.core.dom.IMethodBinding;
//import org.eclipse.jdt.core.dom.IPackageBinding;
//import org.eclipse.jdt.core.dom.ITypeBinding;
//import org.eclipse.jdt.core.dom.IVariableBinding;
//import org.eclipse.jdt.core.dom.InfixExpression;
//import org.eclipse.jdt.core.dom.InstanceofExpression;
//import org.eclipse.jdt.core.dom.Javadoc;
//import org.eclipse.jdt.core.dom.MethodDeclaration;
//import org.eclipse.jdt.core.dom.MethodInvocation;
//import org.eclipse.jdt.core.dom.Modifier;
//import org.eclipse.jdt.core.dom.PackageDeclaration;
//import org.eclipse.jdt.core.dom.ParenthesizedExpression;
//import org.eclipse.jdt.core.dom.QualifiedName;
//import org.eclipse.jdt.core.dom.ReturnStatement;
//import org.eclipse.jdt.core.dom.SimpleName;
//import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
//import org.eclipse.jdt.core.dom.Statement;
//import org.eclipse.jdt.core.dom.TagElement;
//import org.eclipse.jdt.core.dom.ThisExpression;
//import org.eclipse.jdt.core.dom.Type;
//import org.eclipse.jdt.core.dom.TypeDeclaration;
//import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
//import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
//import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
//import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
//import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
//import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
//import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
//import org.eclipse.text.edits.MultiTextEdit;
//import org.eclipse.text.edits.TextEdit;
//import org.eclipse.text.edits.TextEditGroup;
//
//import org.eclipse.ltk.core.refactoring.Change;
//import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
//import org.eclipse.ltk.core.refactoring.CompositeChange;
//import org.eclipse.ltk.core.refactoring.Refactoring;
//import org.eclipse.ltk.core.refactoring.RefactoringChangeDescriptor;
//import org.eclipse.ltk.core.refactoring.RefactoringStatus;
//
///**
// * Resolve Unnecessary Generalization in Class Hierarchy,
// * and Progress Refactoring 
// * @author 손태영, 이주용
// */
//public class MergeClassRefactoring extends Refactoring {
//	private ClassObjectCandidate targetClass;
//
//	protected List<FieldObject> fieldList;
//	protected List<MethodObject> constructorList;
//	protected List<MethodObject> methodList;
//	
//	public MergeClassRefactoring() {
//		targetClass = new ClassObjectCandidate();
//		
//		fieldList = new ArrayList<FieldObject>();
//		constructorList = new ArrayList<MethodObject>();
//		methodList = new ArrayList<MethodObject>();
//	}
//		
//	public MergeClassRefactoring(ClassObjectCandidate target) {
//		targetClass = target;
//		
//		fieldList = target.getFieldList();
//		allMethods = target.getMethodList();
//	}
//
//    /**
//     * @author Taeyoung Son
//     * @param to_be_parsed string to be parsed which contains dot
//     * @return the last string of given string split with dot
//     */
//	private String dotParser(String to_be_parsed) {
//		String[] tokens = to_be_parsed.split("\\.");
//		return tokens[tokens.length-1];
//	}
//
//    /**
//     * @author Taeyoung Son
//     * @return List<String> form of fieldList
//     */
//	private List<String> stringFieldList(){
//		List<String> ret = new ArrayList<String>();
//		//ret.add("\n");
//		for(FieldObject fo : this.fieldList) {
//			String s = fo.toString();
//			if(s.contains("protected")) {
//				String[] tokens = s.split("\\s");
//				s = "";
//				for(int i=0; i<tokens.length; i++) {
//					if(tokens[i].equals("protected")) {
//						tokens[i] = "private";
//					}
//					s = s + " " + tokens[i];
//				}
//				s = s.substring(1);
//			}
//			if(s.contains(".")) {
//				String[] tokens = s.split("\\s");
//				s = "";
//				for(int i=0; i<tokens.length; i++) {
//					if(tokens[i].contains(".")) {
//						tokens[i] = dotParser(tokens[i]);
//					}
//					s = s + " " + tokens[i];
//				}
//				s = s.substring(1);
//			}
//			ret.add("	" + s + ";");
//		}
//		return ret;
//	}
//
//    /**
//     * @author Taeyoung Son
//     * @return List<String> form of MethodList
//     */
//	private List<String> stringMethodList(){
//		List<String> ret = new ArrayList<String>();
//		for(MethodObject mo : this.methodList) {
//			ret.add(mo.toString());
//		}
//		
//		return ret;
//	}
//
//	/**
//     * @author Taeyoung Son
//     * @return List<String> form of ConstructorList
//     */
//	private List<String> stringConstructorList(){
//		List<String> ret = new ArrayList<String>();
//		for(ConstructorObject co : this.constructorList) {
//			ret.add(co.toString());
//		}
//		
//		return ret;
//	}
//	
//    /**
//     * @author Taeyoung Son
//     * @param fullContent the content collected by getContent()
//     * @param methodName the name of method you want from content
//     * @return content of specific method of given name
//     */
//	private List<String> methodContentCreator(List<String> fullContent, String methodName) {
//		List<String> ret = new ArrayList<String>();
//		int _brackets = 0;
//		boolean flag = false;
//		for(String s : fullContent) {
//			if(flag && _brackets > 0) {
//				if(s.contains("{")) {
//					_brackets++;
//				}
//				else if(s.contains("}")) {
//					_brackets--;
//				}
//				ret.add(s);
//			}
//			// WARN :: What if "methodName" used in other context?
//			else if(s.contains(methodName) && !flag) {
//				_brackets++;
//				flag = true;
//				ret.add(s);
//			}
//			else if(_brackets == 0 && flag) break;
//		}
//		return ret;
//	}
//
//    /**
//     * @author Taeyoung Son
//     * @param fullContent the content collected by getContent()
//     * @param methodName the name of method you want from content
//     * @return content of specific method of given name
//     */
//	private List<String> constructorContentCreator(List<String> fullContent) {
//		List<String> ret = new ArrayList<String>();
//		
//		return ret;
//	}
//	
//    /**
//     * @author Taeyoung Son, JuYong Lee
//     * @param child the ClassObjectCandidate to merge with this ClassObjectCandidate
//     */
//	public void mergeIntoChild(ClassObjectCandidate child){
//		assert this.numChild == 1;
//		List<String> newContent = new ArrayList<String>();
//		
//		// Abstract Class with One Child
//		if(this._abstract) {
//			List<String> myContent = this.content;
//			List<String> childContent = child.getContent();
//			
//			List<String> newFieldList = new ArrayList<String>();
//			for(String s : this.stringFieldList()) {
//				if(!child.stringFieldList().contains(s)) {
//					newFieldList.add(s);
//				}
//			}
//			newFieldList.addAll(child.stringFieldList());
//			
//			// Consider Constructors
//			List<String> newConstructorList = new ArrayList<String>();
//			List<String> myConstructorObjectList = this.stringConstructorList();
//			List<String> childConstructorObjectList = child.stringConstructorList();
//			for(ConstructorObject _constructor  : child.constructorList) {
//				// Consider super()
//				String content = _constructor.toString();
//
//			}
//			
//			// Consider Methods
//			List<String> newMethodList = new ArrayList<String>();
//			List<String> myMethodObjectList = this.stringMethodList();
//			List<String> childMethodObjectList = child.stringMethodList();			
//			for (String s : myMethodObjectList) {
//				if (childMethodObjectList.contains(s) || s.contains("abstract") || s.contains(dotParser(this.name))) {
//					// overriden method
//					if (s.contains("abstract")) {
//						int index = s.indexOf("abstract");
//						String former = s.substring(0, index - 1);
//						String latter = s.substring(index + 8, s.length());
//						s = former + latter;
//					} else if (s.contains(".")) {
//						String[] tokens = s.split("\\s");
//						s = "";
//						for (int i = 0; i < tokens.length; i++) {
//							if (tokens[i].contains(".")) {
//								tokens[i] = dotParser(tokens[i]);
//							}
//							s = s + " " + tokens[i];
//						}
//						s = s.substring(1);
//					} else if (s.contains(dotParser(this.name))) {
//						int sidx=0;
//						int fidx=0;
//						for(int i=0; i<s.length(); i++) {
//							if(s.charAt(i) == dotParser(this.name).charAt(0)) {
//								sidx = i;
//								fidx = i + dotParser(this.name).length();
//								break;
//							}
//						}
//						s = s.substring(0,sidx) + dotParser(child.getName()) + s.substring(fidx, s.length());
//
//					}
//					// System.out.println("overriden method... grabbing " + s);
//					for (String t : this.methodContentCreator(childContent, s)) {
//						// System.out.println(t);
//						newMethodList.add(t);
//					}
//					childMethodObjectList.remove(s);
//				} else {
//					// merge parent's
//					// System.out.println("parent's new method... grabbing " + s);
//					for (String t : this.methodContentCreator(myContent, s)) {
//						// System.out.println(t);
//						newMethodList.add(t);
//					}
//				}
//			}
//			for (String s : childMethodObjectList) {
//				if (s.contains(dotParser(child.getName()))) {
//					continue;
//				} else if (!myMethodObjectList.contains(s)) {
//					// merge child's
//					// System.out.println("child's new method... grabbing " + s);
//					for (String t : this.methodContentCreator(childContent, s)) {
//						// System.out.println(t);
//						newMethodList.add(t);
//					}
//				}
//			}
//
//			// Write the new Contents
//			newContent.add(childContent.get(0) + "\n");
//			newContent.add("public class " + dotParser(child.getName()) + "{");
//			newContent.addAll(newFieldList);
//			newContent.addAll(newMethodList);
//			newContent.add("}");
//		}
//		
//		// Interface Class with One Child
//		if(this._interface) {
//			List<String> childContent = child.getContent();
//			
//			// Find the Initial Part
//			String blank = " ";
//			String initialPart = "class" + blank + child.getName() + blank + "implements" + blank + this.name;
//			int initialIdx = 0;
//			for(int i = 0; i < childContent.size(); i++) {
//				if(childContent.get(i).contains(initialPart)) {
//					initialIdx = i;
//					break;
//				}
//			}
//			
//			for(int i = 0; i < childContent.size(); i++) {
//				if(i == initialIdx) {
//					// WARN :: What if the child implements other interface?
//					newContent.add(child.getAccess().toString() + blank + "class" + blank + child.getName() + blank + "{");
//				} else if(childContent.get(i).contains("@Override")) {
//					// Get Rid of @Override
//				} else if(childContent.get(i).contains("//")) {
//					// TODO :: Consider Comments!
//				} else {
//					newContent.add(childContent.get(i));
//				}
//			}
//			newContent.add("}");
//		}
//		
//		// set Contents
//		this.content.add(0, "/*");
//		this.content.add("} \r\n ");
//		this.setContent(this.content);
//		
//		child.setContent(newContent);
//		return;
//	}
//	
//	/**
//	TestClass * Check "content" contains "target"
//	 * 
//	 * @author JuYong Lee
//	 * @param method
//	 * @param var
//	 * @return
//	 */
//	public boolean checkContainance(String content, String target) {
//		String[] operator = { " ", "(", ")", "[", "]", ".",	"+", "-", "*", "/", "%",
//				"!", "~", "++", "--", "<<", ">>", ">>>", ">", "<", ">= ", "<=", "==", "!=",
//				"&", "^", "|", "&&", "||", "?", "=", "+=", "/=", "&=", "*=", "-=", 
//				"<<=", ">>=", ">>>=", "^=", "|=", "%=", ";"}; 
//		
//		for(int i = 0; i < operator.length; i++) {
//			for(int j = 0; j < operator.length; j++) {
//				if(content.contains(operator[i] + target + operator[j])) {
//					return true;
//				}
//			}
//		}
//		 
//		return false;
//	}
//	
//	@Override
//	public String getName() {
//		return this.getName();
//	}
//
//	@Override
//	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
//			throws CoreException, OperationCanceledException {
//		return null;
//	}
//
//	@Override
//	public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
//			throws CoreException, OperationCanceledException {
//		return null;
//	}
//
//	@Override
//	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
//		return null;
//	}
//}
//