package gr.uom.java.ast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jface.text.Position;

public class LPLMethodObject extends MethodObject {

	static int NumParameterLimit = 3;
	
	public static void createNewParameterClass(IPackageFragment pf, String className, List<String> parameterTypes, List<String> parameterNames) {
		try {
			assert(pf != null);
			ICompilationUnit cu = pf.createCompilationUnit(className + ".java", "", false, null);
			ICompilationUnit workingCopy = cu
					.getWorkingCopy(new WorkingCopyOwner() {
					}, null);
			IBuffer buffer = ((IOpenable) workingCopy).getBuffer();
			
			fillNewParameterClass(buffer, pf, className, parameterTypes, parameterNames);
			
			workingCopy.reconcile(ICompilationUnit.NO_AST, false, null, null);
			workingCopy.commitWorkingCopy(false, null);
			workingCopy.discardWorkingCopy();
			workingCopy.discardWorkingCopy();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void fillNewParameterClass(IBuffer buffer, IPackageFragment pf, String className, List<String> parameterTypes, List<String> parameterNames) {
		try {
			assert(parameterTypes.size() == parameterNames.size());
			List<String> parameterTypeAndNames = new ArrayList<String>();
			for (int i = 0; i < parameterTypes.size(); ++i) {
				parameterTypeAndNames.add(parameterTypes.get(i) + " " + parameterNames.get(i));
			}
			
			String packageName = pf.getElementName();
			String packageDeclaration = "package " + packageName + ";\n\n";
			
			StringBuilder parameterDeclarationBuilder = new StringBuilder();
			for (String parameterTypeAndName: parameterTypeAndNames) {
				parameterDeclarationBuilder.append("\tprivate " + parameterTypeAndName + ";\n" );
			}
			String parameterDeclaration = parameterDeclarationBuilder.toString();
			
			StringBuilder constructorBuilder = new StringBuilder();
			constructorBuilder.append("\tpublic " + className);
			constructorBuilder.append("(");
			for (int i = 0; i < parameterTypeAndNames.size() - 1; ++i) {
				String parameterTypeAndName = parameterTypeAndNames.get(i);
				constructorBuilder.append(parameterTypeAndName);
				constructorBuilder.append(", ");
			}
			constructorBuilder.append(parameterTypeAndNames.get(parameterTypeAndNames.size() - 1));
			constructorBuilder.append(") ");
			constructorBuilder.append("{ \n");
			for (String parameterName: parameterNames) {
				constructorBuilder.append("\t\tthis."+parameterName+" = " +parameterName + ";\n");
			}
			constructorBuilder.append("\n\t}\n");
			String constructor = constructorBuilder.toString();
			
			StringBuilder getterBuilder = new StringBuilder();
			for (int i = 0; i < parameterTypeAndNames.size(); ++i) {
				StringBuilder aGetterBuilder = new StringBuilder();
				aGetterBuilder.append("\t public ");
				aGetterBuilder.append(parameterTypes.get(i) + " ");
				String parameterName = parameterNames.get(i);
				aGetterBuilder.append("get");
				aGetterBuilder.append(parameterName.substring(0, 1).toUpperCase() + parameterName.substring(1));
				aGetterBuilder.append("()");
				aGetterBuilder.append(" {\n");
				aGetterBuilder.append("\t\t return ");
				aGetterBuilder.append(parameterName);
				aGetterBuilder.append(";\n\t}\n");
				String aGetter = aGetterBuilder.toString();
				getterBuilder.append(aGetter);
			}
			String getter = getterBuilder.toString();
			
			StringBuilder setterBuilder = new StringBuilder();
			for (int i = 0; i < parameterTypeAndNames.size(); ++i) {
				StringBuilder aSetterBuilder = new StringBuilder();
				aSetterBuilder.append("\t public void ");
				String parameterName = parameterNames.get(i);
				aSetterBuilder.append("set");
				aSetterBuilder.append(parameterName.substring(0, 1).toUpperCase() + parameterName.substring(1));
				aSetterBuilder.append("(");
				aSetterBuilder.append(parameterTypeAndNames.get(i));
				aSetterBuilder.append(")");
				aSetterBuilder.append(" {\n");
				aSetterBuilder.append("\t\t this.");
				aSetterBuilder.append(parameterName);
				aSetterBuilder.append(" = ");
				aSetterBuilder.append(parameterName);
				aSetterBuilder.append(";\n\t}\n");
				String aSetter = aSetterBuilder.toString();
				setterBuilder.append(aSetter);
			}
			String setter = setterBuilder.toString();
			
			StringBuilder classDeclarationBuilder = new StringBuilder();
			classDeclarationBuilder.append("public class ");
			classDeclarationBuilder.append(className);
			classDeclarationBuilder.append("{ \n");
			classDeclarationBuilder.append(parameterDeclaration);
			classDeclarationBuilder.append(constructor);
			classDeclarationBuilder.append(getter);
			classDeclarationBuilder.append(setter);
			classDeclarationBuilder.append("}");
			
			String classDeclaration = classDeclarationBuilder.toString();
			
			buffer.append(packageDeclaration);
			buffer.append(classDeclaration);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void editParameterFromBuffer(IBuffer buffer, IMethod method, ArrayList<Integer> parameterIndexList, 
			LPLSmellContent smellContent, String tempVarInitializeCode) {
		try {
			IMethod convertedIMethod = method;
			int startPosition = convertedIMethod.getSourceRange().getOffset();
			
			while (true) {
				if (buffer.getChar(startPosition) != '(') {
					startPosition += 1;
					continue;
				}
				break;
			}
			int numOfLeftPar = 0;
			int endPosition = startPosition;
			while (true) {
				if (buffer.getChar(endPosition) == '(') {
					numOfLeftPar += 1;
				} 
				else if (buffer.getChar(endPosition) == ')') {
					if (numOfLeftPar == 1)
						break;
					else
						numOfLeftPar -= 1;
				}
				endPosition += 1;
			}
			String argumentString = buffer.getContents().substring(startPosition + 1, endPosition);
			String argumentParts[] = argumentString.split(",");
			for(int it : parameterIndexList) {
				
				argumentParts[it] = null;
			}
			String refactoredArgumentString = "";
			for(String s : argumentParts) {
				if(s != null) {
					refactoredArgumentString += s.trim();
					refactoredArgumentString += ", ";
				}
			}
			String replaceSignature = "(";
			replaceSignature += refactoredArgumentString;
			replaceSignature += smellContent.getNewClassName() + " " + smellContent.getNewParameterName();
			replaceSignature += ")";
			
			int defPosition = endPosition;
			while (buffer.getChar(defPosition) != '{') {
				defPosition += 1;
			}
			defPosition += 1;
			
			
			buffer.replace(defPosition, 0, tempVarInitializeCode);
			buffer.replace(startPosition, endPosition - startPosition + 1, replaceSignature);
		} catch (Exception e) {
				e.printStackTrace();
		}
	}
	
	public static String codeForInitializingTempVars(IMethod method, List<String>parameterTypes, List<String>parameterNames, String parameterObjName) {
		StringBuilder codeBuilder = new  StringBuilder();
		codeBuilder.append("\n");
		int parameterSize = parameterTypes.size();
		for (int i = 0; i < parameterSize; ++i) {
			codeBuilder.append("\t\t");
			codeBuilder.append(parameterTypes.get(i));
			codeBuilder.append(" ");
			codeBuilder.append(parameterNames.get(i));
			codeBuilder.append(" = ");
			codeBuilder.append(parameterObjName);
			codeBuilder.append(".get");
			String name = parameterNames.get(i);
			String nameWithUpperCase = name.substring(0, 1).toUpperCase() + name.substring(1);
			codeBuilder.append(nameWithUpperCase);
			codeBuilder.append("()");
			codeBuilder.append(";\n");
		}
		return codeBuilder.toString();
	}

	private String codeSmellType;

	public LPLMethodObject(ConstructorObject co) {
		super(co);
		codeSmellType = "Long Parameter List";
	}
	private int smell_start=10;
	private int smell_length=15;
    public Object[] getHighlightPositions() {
       Map<Position, String> annotationMap = new LinkedHashMap<Position, String>();
       Position position = new Position(smell_start, smell_length);
      annotationMap.put(position, "LPL_SMELL");
      return new Object[] {annotationMap};
    }
    public IFile getIFile()
    {
    	//TODO ::getIfile
    	return null;
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
		switch (index) {
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

	public IMethod toIMethod(IJavaProject javaProject) throws JavaModelException {
		String classNameToParse = this.getClassName();
		String[] fileWords = classNameToParse.split("\\.");
		String className = fileWords[fileWords.length - 1];
		String packageName = "";
		for (int i = 0; i < fileWords.length - 1; i++) {
			packageName += fileWords[i];
			if (i != fileWords.length - 2) {
				packageName += ".";
			}
		}

		List<ICompilationUnit> parentClassCandidates = new ArrayList<ICompilationUnit>();
		for (IPackageFragment packageFragment : javaProject.getPackageFragments()) {
			if (packageFragment.getElementName().equals(packageName)) {
				for (ICompilationUnit parentClassCandidate : packageFragment.getCompilationUnits()) {
					parentClassCandidates.add(parentClassCandidate);
				}
			}
		}

		ICompilationUnit parentClass = null;
		for (ICompilationUnit classCandidate : parentClassCandidates) {
			if (classCandidate.getElementName().equals(className + ".java")) {
				parentClass = classCandidate;
				break;
			}
		}
		assert (parentClass != null);

		IMethod methodFound = null;
		for (IType iType : parentClass.getTypes()) {
			for (IMethod method : iType.getMethods()) {
				if (method.getElementName().equals(this.getName())) {
					methodFound = method;
					break;
				}
			}
		}
		assert (methodFound != null);
		return methodFound;

	}

}
