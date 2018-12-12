package gr.uom.java.ast;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

import org.eclipse.core.runtime.IPath;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.jface.text.Position;

/**
 * Extension of ClassObject for Speculative Generality Code Smell
 * @author 이주용, 이재엽, 손태영
 */
public class ClassObjectCandidate extends ClassObject {
	private List<String> content;

    private String codeSmellType;
    private String refactorType;

    private List<MethodObject> smellingMethods;

    private int smell_start = 0;
    private int smell_length = 0;


    private int numChild = 0;

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

    	this.setContent();
        this.codeSmellType = "Speculative Generality";
        this.refactorType = "_";
        this.smellingMethods = new ArrayList<MethodObject>();
        this.setHighlightPositions();
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

    	this.setContent();
        this.codeSmellType = "Speculative Generality";
        this.refactorType = "_";
        this.smellingMethods = new ArrayList<MethodObject>();
        this.setHighlightPositions();
    }


    /**
     * Methods Setting, and Getting Number of Child
     * @author JuYong Lee, JaeYeop Lee
     */
    public void setNumChild(int arg) {
    	this.numChild = arg;
    }

    /**
     * @author Jaeyeop Lee
     * @return positions of highlight
     */
    public Object[] getHighlightPositions() {
    	Map<Position, String> annotationMap = new LinkedHashMap<Position, String>();
    	Position position = new Position(this.smell_start, this.smell_length);
    	String annotationMessage = "This smell has Speculative Generality in Class "+this.name;
		annotationMap.put(position, annotationMessage);
		return new Object[] {annotationMap};
    }
    /**
     * Methods Setting, and Getting Number of Child
     * @author JuYong Lee, JaeYeop Lee
     */
    public void setStart(int s){
        this.smell_start=s;
     }
    /**
     * @author Jaeyeop Lee
     * @return start position of highlight
     */
     public int getStart() {
        return smell_start;
     }

     /**
      * set length of code smell
      */
     public void setLength(int l)
     {
        this.smell_length=l;
     }
     /**
      * @author Jaeyeop Lee
      * get length of code smell
      */
     public int getLength()
     {
        return smell_length;
     }

     /**
      * @author Taeyoung Son
      * @return number of this class's child
      */
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

    /**
     * @author Jaeyeop Lee
     * @return returns refactortype
     */
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
    /**
     * Methods Setting, and Getting Number of Child
     * @author JuYong Lee, JaeYeop Lee
     */
	public void setSmellingMethods(List<MethodObject> arg) {
		this.smellingMethods = arg;
	}

    /**
     * @author JaeYeop Lee
     * @return list of code smells
     */
	public List<MethodObject> getSmellingMethods() {
		return this.smellingMethods;
	}

    /**
     * Methods getting Full Name of Class
     * including _static, _interface, and so forth
     * @author JaeYeop Lee
     */
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
	 * @author JaeYeop Lee, JuYong Lee
	 * @return every statement of class
	 */
	public void setContent()
	{
		List<String> result = new ArrayList<String>();

		if(this.iFile != null) {
			IPath _path = iFile.getLocation();
			String filepath = _path.toString();

			boolean readFlag = true;
			try {
				BufferedReader buffer = new BufferedReader(new FileReader(filepath));
				result.add(buffer.readLine());

				while (true) {
					String line = buffer.readLine();
					if (line == null) {
						break;
					}
					else if (line.length() > 1 && line.substring(0,2).equals("/*")) {
						readFlag = false;
					}

					else if(line.length() > 1 && line.substring(line.length()-2, line.length()).equals("*/")) {
						readFlag = true;
					}

					else if (line.length() > 1 && line.substring(0,2).equals("//")) {
						continue;
					}

					else if (readFlag) {
						result.add(line);
					}
				}
				buffer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		this.content =  result;
		return;
	}

    /**
     * @author Juyong Lee, Taeyoung Son
     * @param arg list of strings of class
     * set content of this class as given arg
     */
	public void setContent(List<String> arg) {
		this.content = arg;
	}

    /**
     * returns List of strings of this class's content
     */
	public List<String> getContent() {
		return this.content;
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
     * set Highlight Positions
     * @author Taeyoung Son
     */
    private void setHighlightPositions() {
    	List<String> myContent = getContent();
    	int startPos=0;
    	int length =0;
    	for(String s: myContent) {
    		for(int i=0; i<s.length(); i++) {
    			if(s.contains(getClassFullName())) {
    				this.smell_start = startPos;
    				this.smell_length = length + s.length();
    				return;
    			}
    		}
    		startPos += s.length() + 1;
    		length += 1;

    	}
    }
}
