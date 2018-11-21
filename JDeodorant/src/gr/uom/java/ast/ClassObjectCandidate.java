package gr.uom.java.ast;

import gr.uom.java.ast.decomposition.MethodBodyObject;
import gr.uom.java.jdeodorant.refactoring.manipulators.TypeCheckElimination;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.text.Position;

public class ClassObjectCandidate extends ClassObject {
    // Ext : JuYongLee & JaeYeop Lee
	//private ClassObject _root;
    private String codeSmellType;
    private String refactorType;
    private List<MethodObject> smellingMethods;
    
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
        
        // Ext : JuYongLee & JaeYeop Lee
        this.codeSmellType = "Speculative Generality";
        this.refactorType = "_";
        this.smellingMethods = new ArrayList<MethodObject>();
    }

    /*public ClassObject getClassObject() {
    	return this._root;
    }*/
    public Object[] getHighlightPositions() {
    	Map<Position, String> annotationMap = new LinkedHashMap<Position, String>();
    	Position position = new Position(10, 20);
		annotationMap.put(position, "HELLO");
		return new Object[] {annotationMap};
		
    }
	public void setCodeSmellType(String arg) {
		this.codeSmellType = arg;
	}
	
	public void getRefactorType(String arg) {
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
	
    public ClassObjectCandidate getClassObjectCandidate() {
    	return this;
    }

	public String toString() {
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
        sb.append(name).append(" ");
        sb.append("extends ").append(superclass);
        if(!interfaceList.isEmpty()) {
            sb.append(" ").append("implements ");
            for(int i=0; i<interfaceList.size()-1; i++)
                sb.append(interfaceList.get(i)).append(", ");
            sb.append(interfaceList.get(interfaceList.size()-1));
        }
        sb.append("\n\n").append("Fields:");
        for(FieldObject field : fieldList)
            sb.append("\n").append(field.toString());

        sb.append("\n\n").append("Constructors:");
        for(ConstructorObject constructor : constructorList)
            sb.append("\n").append(constructor.toString());

        sb.append("\n\n").append("Methods:");
        for(MethodObject method : methodList)
            sb.append("\n").append(method.toString());

        //ToDo : Add some string for types and targets 
        
        return sb.toString();
    }
}