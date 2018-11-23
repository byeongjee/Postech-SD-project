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
	//private ClassObject _root;
    private String codeSmellType;
    private String refactorType;
    private List<MethodObject> smellingMethods;

    private int numChild = 0;
    private List<Integer> numUnusedParameter;

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
        this.numUnusedParameter  = new ArrayList<Integer>();
    }

    /*public ClassObject getClassObject() {
    	return this._root;
    }*/

    public void setNumChild(int arg) {
    	this.numChild = arg;
    }

    public int getNumChild() {
    	return this.numChild;
    }

    public void addNumUnusedParameter(int arg) {
    	this.numUnusedParameter.add(arg);
    }

    public List<Integer> getNumUnusedParameter() {
    	return this.numUnusedParameter;
    }

	public void setCodeSmellType(String arg) {
		this.codeSmellType = arg;
	}

	public void setRefactorType(String arg) {
		this.refactorType = arg;
	}

	public void setSmellingMethods(List<MethodObject> arg) {
		this.smellingMethods = arg;
	}

	public String getCodeSmellType() {
		return this.codeSmellType;
	}

	public String getRefactorType() {
		return this.refactorType;
	}

	public List<MethodObject> getSmellingMethods() {
		if(codeSmellType.equals("Unnecessary Parameters")) {
			return this.smellingMethods;
		} else {
			return this.getMethodList();
		}
	}

	public void addSmellingMethod(MethodObject target) {
		this.smellingMethods.add(target);
	}

	public List<FieldObject> getFieldList()
	{
		return this.fieldList;
	}

	public List<String> getContent()
	{
		String filepath = iFile.getLocation().toString();
		List<String> result = new ArrayList<String>();
		Stack<Character> stack = new Stack<Character>();
		boolean readFlag = false;

		try {
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			result.add(br.readLine());
	        while(true) {
	            String line = br.readLine();
	            System.out.println(line);
	            if (line==null) break;
	            if(line.contains(this.getClassFullName()))
	            {
	            	readFlag=true;
	            }

	            if(readFlag)
	            {
	            	for(int i=0;i<line.length();i++)
	            	{
	            		if(line.charAt(i)=='{')
	            		{
	            			stack.push('{');
	            		}
	            		else if(line.charAt(i)=='}')
	            		{
	            			if(stack.isEmpty())
	            			{
	            				return result;
	            			}
	            			else if(stack.peek()=='{')
	            			{
	            				stack.pop();
	            			}
	            			else
	            			{
	            				stack.push('}');
	            			}
	            		}
	            	}
	            }
	            if(readFlag)
	            	result.add(line);
	            if(stack.isEmpty())
	            {
	            	readFlag=false;
	            }
	        }
	        br.close();
		}catch(IOException e){
			e.printStackTrace();
		}

		return result;
	}

	public String getClassFullName()
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
     * @author Taeyoung Son
     * @param to_be_parsed string to be parsed which contains dot
     * @return the last string of given string split with dot
     */
	private String dotParser(String to_be_parsed) {
		String[] tokens = to_be_parsed.split("\\.");
		/*for(String s: tokens) {
			System.out.println(s);
		}*/
		return tokens[tokens.length-1];
	}

    /**
     * @author Taeyoung Son
     * @return List<String> form of fieldList
     */
	private List<String> StringFieldList(){
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
	private List<String> StringMethodList(){
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
	private List<String> contentCreator(List<String> fullContent, String methodName) {
		List<String> ret = new ArrayList<String>();
		int _brackets = 0;
		boolean flag = false;
		for(String s : fullContent) {
			if(flag && _brackets > 0) {
				if(s.contains("{")) _brackets++;
				else if(s.contains("}")) _brackets--;
				ret.add(s);
			}
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
     * @param child the ClassObjectCandidate to merge with this ClassObjectCandidate
     */
	public void MergeContents(ClassObjectCandidate child){
		List<String> myContent = this.getContent();
		List<String> childContent = child.getContent();
		List<String> myMethodObjectList = this.StringMethodList();
		List<String> childMethodObjectList = child.StringMethodList();

		List<String> newFieldList = new ArrayList<String>();
		List<String> newMethodList = new ArrayList<String>();
		List<String> newFile = new ArrayList<String>();

		newFieldList.addAll(this.StringFieldList());
		newFieldList.addAll(child.StringFieldList());

		for(String s: myMethodObjectList) {
			if(childMethodObjectList.contains(s) || s.contains("abstract") || s.contains(dotParser(this.name))) {
				//overriden method
				if(s.contains("abstract")) {
					int index = s.indexOf("abstract");
					String former = s.substring(0, index - 1);
					String latter = s.substring(index + 8, s.length());
					s = former + latter;
				}
				else if(s.contains(".")) {
					String[] tokens = s.split("\\s");
					s = "";
					for(int i=0; i<tokens.length; i++) {
						if(tokens[i].contains(".")){
							tokens[i] = dotParser(tokens[i]);
						}
						s = s + " " + tokens[i];
					}
					s = s.substring(1);
				}
				else if(s.contains(dotParser(this.name))) {
					String[] tokens = s.split("\\s");
					s = "";
					for(int i=0; i<tokens.length; i++) {
						if(tokens[i].contains(dotParser(this.name))){
							tokens[i] = dotParser(child.getName());
						}
						s = s + " " + tokens[i];
					}
					s = s.substring(1);

				}
				System.out.println("overriden method... grabbing " + s);
				for(String t : this.contentCreator(childContent, s)) {
					System.out.println(t);
					newMethodList.add(t);
				}
				childMethodObjectList.remove(s);
			}
			else {
				//merge parent's
				System.out.println("parent's new method... grabbing " + s);
				for(String t : this.contentCreator(myContent, s)) {
					System.out.println(t);
					newMethodList.add(t);
				}
			}
		}

		for(String s: childMethodObjectList) {
			if(s.contains(dotParser(child.getName()))) {
				continue;
			}
			else if(!myMethodObjectList.contains(s)) {
				//merge child's
				System.out.println("child's new method... grabbing " + s);
				for(String t : this.contentCreator(childContent, s)) {
					System.out.println(t);
					newMethodList.add(t);
				}
			}
		}

		newFile.add(childContent.get(0) + "\n");
		newFile.add("public class " + dotParser(child.getName()) + "{");
		newFile.addAll(newFieldList);
		newFile.addAll(newMethodList);
		newFile.add("}");

		try {
			File makeFile = new File(child.getIFile().getLocation().toString());
			FileWriter fileWriter = new FileWriter(makeFile);
			for(String s : newFile) {
				fileWriter.write(s + "\n");
				fileWriter.flush();
			}
			fileWriter.close();
			this.getIFile().delete(true, false, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
