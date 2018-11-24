package gr.uom.java.ast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;
import gr.uom.java.ast.decomposition.MethodBodyObject;
import gr.uom.java.jdeodorant.refactoring.manipulators.TypeCheckElimination;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * Extension of ClassObject for Speculative Generality Code Smell
 * @author 이주용, 이재엽, 손태영
 */
public class ClassObjectCandidate extends ClassObject {
    private String codeSmellType;
    private String refactorType;
    private List<MethodObject> smellingMethods;

    private int numChild = 0;
    private List<Integer> numUnusedParameter;
	private List<List<String>> unusedParameter;

    public ClassObjectCandidate() {
    	this.name = "";
    	this.methodList = null;
    	this.fieldList = null;
    	this.commentList = null;
		this.constructorList = new ArrayList<ConstructorObject>();
		this.interfaceList = new ArrayList<TypeObject>();
		this.enumConstantDeclarationList = new ArrayList<EnumConstantDeclarationObject>();
		this._abstract = false;
        this._interface = false;
        this._static = false;
        this._enum = false;
        this.access = Access.NONE;
        this.typeDeclaration = null;
    	this.iFile = null;

        // Ext : JuYongLee & JaeYeop Lee
        this.codeSmellType = "Speculative Generality";
        this.refactorType = "_";
        this.smellingMethods = new ArrayList<MethodObject>();
        this.unusedParameter = new ArrayList<List<String>>();
        this.numUnusedParameter  = new ArrayList<Integer>();
    }

    public ClassObjectCandidate(ClassObject co) {
    	this.name = co.name;
    	this.methodList = co.methodList;
    	this.fieldList = co.fieldList;
    	this.commentList = co.commentList;

		this.constructorList = co.constructorList;
		this.interfaceList = co.interfaceList;
		this.enumConstantDeclarationList = co.enumConstantDeclarationList;
		this._abstract = co._abstract;
        this._interface = co._interface;
        this._static = co._static;
        this._enum = co._enum;
        this.access = co.access;

        this.typeDeclaration = co.typeDeclaration;
    	this.iFile = co.iFile;

    	this.superclass = co.superclass;

        // Ext : JuYongLee & JaeYeop Lee
        this.codeSmellType = "Speculative Generality";
        this.refactorType = "_";
        this.smellingMethods = new ArrayList<MethodObject>();
        this.unusedParameter = new ArrayList<List<String>>();
        this.numUnusedParameter  = new ArrayList<Integer>();
    }

    /**
     * Methods Setting, and Getting Number of Child
     * @author JuYong Lee, JaeYeop Lee
     */
    public void setNumChild(int arg) {
    	this.numChild = arg;
    }

    public int getNumChild() {
    	return this.numChild;
    }


    /**
     * Methods Setting, and Getting Code Smell Type
     * @author JuYong Lee, JaeYeop Lee
     */
	public void setCodeSmellType(String arg) {
		this.codeSmellType = arg;
	}

	public String getCodeSmellType() {
		return this.codeSmellType;
	}
	

    /**
     * Methods Setting, and Getting Refactoring Type
     * @author JuYong Lee, JaeYeop Lee
     */
	public void setRefactorType(String arg) {
		this.refactorType = arg;
	}

	public String getRefactorType() {
		return this.refactorType;
	}

    /**
     * Methods Adding, Setting, and Getting Smelling Methods
     * @author JuYong Lee
     */
	public void addSmellingMethod(MethodObject target) {
		this.smellingMethods.add(target);
	}
	
	public void setSmellingMethods(List<MethodObject> arg) {
		this.smellingMethods = arg;
	}

	public List<MethodObject> getSmellingMethods() {
		if(codeSmellType.equals("Unnecessary Parameters")) {
			return this.smellingMethods;
		} else {
			return this.getMethodList();
		}
	}

    /**
     * Methods Setting, and Getting Number of Unused Parameter 
     * The Index are assumed to match to which of Method List
     * @author JuYong Lee, JaeYeop Lee
     */
    public void addNumUnusedParameter(int arg) {
    	this.numUnusedParameter.add(arg);
    }

    public List<Integer> getNumUnusedParameter() {
    	return this.numUnusedParameter;
    }

	/**
	 * Methods Adding and Getting Unused Parameter List
	 * The Index are assumed to match to which of Method List
	 * @author JuYong Lee
	 */
	public void addUnusedParameterList(List<String> arg) {
		this.unusedParameter.add(arg);
	}

	public List<List<String>> getUnusedParameterList() {
		return this.unusedParameter;
	}
	
	/**
	 * @author JuYong Lee
	 * @param target
	 * @return List of (type, name) of usedParameterList of target
	 */
	public List<List<String>> getUsedParameterList(MethodObject target) {
		// get Index of Target in MethodList
		int idx = 0;
		for(int i = 0; i < this.getMethodList().size(); i++) {
			if(this.getMethodList().get(i).getName().equals(target.getName())) {
				idx = i;
			}
		}
		System.out.println("Index : " + idx);
		System.out.println("Unused Parameter Number : " + this.numUnusedParameter.get(idx));
		
		// make Result
		List<String> pList = this.getMethodList().get(idx).getParameterList();

		System.out.println("All Parameters : ");
		for(int i = 0; i < pList.size(); i++) {
			System.out.println(target.getParameter(i));
		}

		System.out.println("Unused Parameters : ");
		for(String s : unusedParameter.get(idx)) {
			System.out.println(s);
		}
		
		List<List<String>> result = new ArrayList<List<String>>();
		for(int i = 0; i < pList.size(); i++) {
			List<String> pair = new ArrayList<String>();
			
			boolean flagUnused = false;
			for(String compare : this.unusedParameter.get(idx)) {
				if(compare.equals( target.getParameter(i).getName() )) {
					flagUnused = true;
				}
			}
			
			if(!flagUnused) {
				pair.add(this.getMethodList().get(idx).getParameterTypeList().get(i).toString());
				pair.add( target.getParameter(i).getName() );
				
				for(String s : pair) {
					System.out.println(s);
				}
				
				result.add(pair);
			}
		}
		
		return result;
	}
	
    /**
     * Methods getting Full Name of Class
     * including _static, _interface, and so forth
     * @author JaeYeop Lee
     */
	private String getClassFullName()
	{
		StringBuilder sb = new StringBuilder();
        if(!access.equals(Access.NONE))
            sb.append(access.toString()).append(" ");
        if(_static)
            sb.append("static").append(" ");
        if(_interface)
            sb.append("interface").append(" ");
        else if(_abstract)
            sb.append("abstract class").append(" ");
        else
            sb.append("class").append(" ");
        sb.append(this.dotParser(this.name));
        return sb.toString();
	}
	
	/**
	 * @author JaeYeop Lee, JuYong Lee
	 * @return every statement of class
	 */
	public List<String> getContent()
	{
		String filepath = iFile.getLocation().toString();
		List<String> result = new ArrayList<String>();
		
		Stack<Character> parenthesisChecker = new Stack<Character>();
		boolean readFlag = false;

		try {
			BufferedReader buffer = new BufferedReader(new FileReader(filepath));
			result.add(buffer.readLine());
			
	        while(true) {
	            String line = buffer.readLine();
	            if (line==null) { 
	            	break;
	            }
	            if(line.contains(this.getClassFullName())) {
	            	readFlag=true;
	            }

	            if(readFlag) {
	            	for(int i=0;i<line.length();i++) {
	            		if(line.charAt(i)=='{') {
	            			parenthesisChecker.push('{');
	            		} else if(line.charAt(i)=='}') {
	            			if(parenthesisChecker.isEmpty()) {
	            				return result;
	            			} else if(parenthesisChecker.peek()=='{') {
	            				parenthesisChecker.pop();
	            			} else {
	            				parenthesisChecker.push('}');
	            			}
	            		}
	            	}
	            }
	            
	            if(parenthesisChecker.isEmpty()) {
	            	readFlag=false;
	            }
	            
	            // Adding Each Lines to Result
	            if(readFlag) {
	            	result.add(line);
	            }
	        }
	        buffer.close();
		}catch(IOException e){
			e.printStackTrace();
		}

		return result;
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
     * @author Taeyoung Son
     * @return List<String> form of fieldList
     */
	private List<String> stringFieldList(){
		List<String> ret = new ArrayList<String>();
		//ret.add("\n");
		for(FieldObject fo : this.fieldList) {
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
	private List<String> stringMethodList(){
		List<String> ret = new ArrayList<String>();
		for(MethodObject mo : this.methodList) {
			ret.add(mo.toString());
		}
		for(ConstructorObject co : this.constructorList) {
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
				if(s.contains("{")) _brackets++;
				else if(s.contains("}")) _brackets--;
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
     * @author Taeyoung Son, JuYong Lee
     * @param child the ClassObjectCandidate to merge with this ClassObjectCandidate
     */
	public void mergeIntoChild(ClassObjectCandidate child){
		assert this.numChild == 1;
		List<String> newContent = new ArrayList<String>();
		
		// Abstract Class with One Child
		if(this._abstract) {
			List<String> myContent = this.getContent();
			List<String> childContent = child.getContent();
			
			List<String> newFieldList = new ArrayList<String>();
			newFieldList.addAll(this.stringFieldList());
			newFieldList.addAll(child.stringFieldList());
			
			List<String> newMethodList = new ArrayList<String>();
			List<String> myMethodObjectList = this.stringMethodList();
			List<String> childMethodObjectList = child.stringMethodList();
			for (String s : myMethodObjectList) {
				if (childMethodObjectList.contains(s) || s.contains("abstract") || s.contains(dotParser(this.name))) {
					// overriden method
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
					} else if (s.contains(dotParser(this.name))) {
						String[] tokens = s.split("\\s");
						s = "";
						for (int i = 0; i < tokens.length; i++) {
							if (tokens[i].contains(dotParser(this.name))) {
								tokens[i] = dotParser(child.getName());
							}
							s = s + " " + tokens[i];
						}
						s = s.substring(1);

					}
					System.out.println("overriden method... grabbing " + s);
					for (String t : this.methodContentCreator(childContent, s)) {
						System.out.println(t);
						newMethodList.add(t);
					}
					childMethodObjectList.remove(s);
				} else {
					// merge parent's
					System.out.println("parent's new method... grabbing " + s);
					for (String t : this.methodContentCreator(myContent, s)) {
						System.out.println(t);
						newMethodList.add(t);
					}
				}
			}

			for (String s : childMethodObjectList) {
				if (s.contains(dotParser(child.getName()))) {
					continue;
				} else if (!myMethodObjectList.contains(s)) {
					// merge child's
					System.out.println("child's new method... grabbing " + s);
					for (String t : this.methodContentCreator(childContent, s)) {
						System.out.println(t);
						newMethodList.add(t);
					}
				}
			}

			// Write the new Contents
			newContent.add(childContent.get(0) + "\n");
			newContent.add("public class " + dotParser(child.getName()) + "{");
			newContent.addAll(newFieldList);
			newContent.addAll(newMethodList);
			newContent.add("}");
		}
		
		// Interface Class with One Child
		if(this._interface) {
			List<String> childContent = child.getContent();
			
			// Find the Initial Part
			String blank = " ";
			String initialPart = "class" + blank + child.getName() + blank + "implements" + blank + this.name;
			int initialIdx = 0;
			for(int i = 0; i < childContent.size(); i++) {
				if(childContent.get(i).contains(initialPart)) {
					initialIdx = i;
					break;
				}
			}
			
			// Write New Content + TODO :: Check the Grammar
			for(int i = 0; i < childContent.size(); i++) {
				if(i == initialIdx) {
					// WARN :: What if the child implements other interface?
					newContent.add(child.getAccess().toString() + blank + "class" + blank + child.getName() + blank + "{");
				} else if(childContent.get(i).contains("@Override")) {
					// Get Rid of @Override
				} else if(childContent.get(i).contains("//")) {
					// TODO :: Consider Comments!
				} else {
					newContent.add(childContent.get(i));
				}
			}
		}
		
		// make New File
		try {
			File makeFile = new File(child.getIFile().getLocation().toString());
			FileWriter fileWriter = new FileWriter(makeFile);
			for (String content : newContent) {
				fileWriter.write(content + "\n");
				fileWriter.flush();
			}
			fileWriter.close();
			//this.getIFile().delete(true, false, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  Delete the Unnecessary Parameter Declaration Code in Method Defining Code
	 *  @author JuYong Lee
	 */
	public void resolveUnnecessaryParameters(MethodObject target) {
		assert this.codeSmellType == "Unnecessary Parameters";
		List<String> originalContent = this.getContent();
		List<String> newContent = new ArrayList<String>();
		
		// Find the target Method
		int declarationIdx = 0;
		target.getName(); 
		for(int i = 0; i < originalContent.size(); i++) {
			
			if(originalContent.get(i).contains(" " + target.getName()) &&
					(originalContent.get(i).contains(target.getName() + "(") || 
							(originalContent.get(i).contains(target.getName() + " (")))) {
				// WARN :: ( might come long after blanks or \n's \t's...
				
				declarationIdx = i;
				break;
			}
		}
		
		// Write New Content
		for(int i = 0; i < originalContent.size(); i++) {
			if(i == declarationIdx) {
				String newDeclarationCode = "\t";
				if(target.isStatic()) {
					newDeclarationCode += "static ";
				}
				newDeclarationCode += target.getAccess().toString();
				if(target.getAccess().toString() != "") {
					newDeclarationCode += " ";
				}
				newDeclarationCode += target.getReturnType().toString() + " ";
				newDeclarationCode += target.getName() + "(";
				
				List<List<String>> _usedPList = this.getUsedParameterList(target);
				for(int p = 0; p < _usedPList.size(); p++) {
					List<String> _pPair = _usedPList.get(p);
					newDeclarationCode += _pPair.get(0) + " " + _pPair.get(1);
					if(p < _usedPList.size() - 1) {
						newDeclarationCode += ", ";
					} else {
						newDeclarationCode += ") " + "{";
					}
				}
				if(_usedPList.size() == 0) {
					newDeclarationCode += ") " + "{";
				}
				
				newContent.add(newDeclarationCode);
			} else {
				newContent.add(originalContent.get(i));
			}
		}
		newContent.add("}");
		
		// DBG
		for(String c : newContent) {
			System.out.println(c);
		}
		
		// Make New File
		try {
			File makeFile = new File(this.getIFile().getLocation().toString());
			
			// Firstly, Delete Original Version
			try {
				this.getIFile().delete(true, false, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Secondly, Create New Version
			FileWriter fileWriter = new FileWriter(makeFile);
			for (String content : newContent) {
				fileWriter.write(content + "\n");
				fileWriter.flush();
			}
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
