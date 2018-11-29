package gr.uom.java.ast;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class LPLMethodObject extends MethodObject {

	static int NumParameterLimit = 3;
	
	public static void editParameterFromBuffer(IBuffer buffer, IMethod method, String parameterString, ArrayList<Integer> parameterIndexList) {
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
					refactoredArgumentString += s;
					refactoredArgumentString += ",";
				}
			}
			refactoredArgumentString = refactoredArgumentString.substring(0, refactoredArgumentString.length() - 1);
			String replaceSignature = "(";
			replaceSignature += refactoredArgumentString;
			replaceSignature += ")";
			
			System.out.println(refactoredArgumentString);
			
			buffer.replace(startPosition, endPosition - startPosition + 1, replaceSignature);
		} catch (Exception e) {
				e.printStackTrace();
		}
	}

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
