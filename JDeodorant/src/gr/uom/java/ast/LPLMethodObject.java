package gr.uom.java.ast;

public class LPLMethodObject extends MethodObject {
	
	static int NumParameterLimit = 3;
	
	
	private String codeSmellType;
	
	public LPLMethodObject(ConstructorObject co) {
		super(co);
		codeSmellType = "Long Parameter List";
	}

	public static LPLMethodObject createLPLMethodObjectFrom(MethodObject mo) {
		LPLMethodObject returnObject = new LPLMethodObject(mo.constructorObject);
		returnObject.returnType = mo.returnType;
		returnObject._abstract = mo._abstract;
		returnObject._static = mo._static;
		returnObject._synchronized = mo._synchronized;
		returnObject._native = mo._native;
		returnObject.constructorObject = mo.constructorObject;
		returnObject.testAnnotation = mo.testAnnotation;
		returnObject.hashCode = mo.hashCode;
		return returnObject;
	}

	
	public String getColumnText(int index) {
		switch(index){
		case 0:
			return codeSmellType;
		case 1:
			return getName();
		case 2:
			return getClassName();
		case 3:
			return getParameterTypeAndNameList().toString();
		case 4:
			return Integer.toString(getParameterList().size());
		default:
			return "";
		}
	}
	
	public int compareTo(LPLMethodObject that) {
		return this.getName().compareTo(that.getName());
	}
	
	public boolean isLongParamterListMethod() {
		return getParameterList().size() > NumParameterLimit;
	}
	

	
}
