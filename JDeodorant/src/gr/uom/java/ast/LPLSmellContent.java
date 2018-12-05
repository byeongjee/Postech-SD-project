package gr.uom.java.ast;

import java.util.ArrayList;

public class LPLSmellContent {
	private LPLMethodObject methodObject;
	private ArrayList<Integer> indexList;
	private String newClassName;
	private String newParameterName;
	
	public LPLSmellContent(LPLMethodObject methodObject, ArrayList<Integer> indexList, String newClassName, String newParamterName) {
		this.methodObject = methodObject;
		this.indexList = indexList;
		this.newClassName = newClassName;
		this.newParameterName = newParamterName;
	}
	
	public LPLMethodObject getLPLMethodObject() {
		return this.methodObject;
	}
	
	public ArrayList<Integer> getIndexList(){
		return this.indexList;
	}
	
	public String getNewClassName() {
		return this.newClassName;
	}
	
	public String getNewParameterName() {
		return this.newParameterName;
	}
}
