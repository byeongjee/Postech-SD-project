package gr.uom.java.ast;

import java.util.ArrayList;

public class LPLSmellContent {
	private LPLMethodObject methodObject;
	private ArrayList<Integer> indexList;
	private String newClassName;
	
	public LPLSmellContent(LPLMethodObject methodObject, ArrayList<Integer> indexList, String newClassName) {
		this.methodObject = methodObject;
		this.indexList = indexList;
		this.newClassName = newClassName;
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
}
