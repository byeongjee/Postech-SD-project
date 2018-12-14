package gr.uom.java.ast;

import java.util.List;

public class LPLSmellContent {
	private LPLMethodObject methodObject;
	private List<Integer> extractedParameterIndicesList;
	private String newClassName;
	private String newParameterName;
	
	public LPLSmellContent(LPLMethodObject methodObject, List<Integer> extractedParameterIndicesList, String newClassName, String newParamterName) {
		this.methodObject = methodObject;
		this.extractedParameterIndicesList = extractedParameterIndicesList;
		this.newClassName = newClassName;
		this.newParameterName = newParamterName;
	}
	
	public LPLMethodObject getLPLMethodObject() {
		return this.methodObject;
	}
	
	public List<Integer> getExtractedParameterIndicesList(){
		return this.extractedParameterIndicesList;
	}
	
	public String getNewClassName() {
		return this.newClassName;
	}
	
	public String getNewParameterName() {
		return this.newParameterName;
	}
}
