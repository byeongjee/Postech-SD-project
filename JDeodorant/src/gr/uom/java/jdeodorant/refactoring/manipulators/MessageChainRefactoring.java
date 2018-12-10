package gr.uom.java.jdeodorant.refactoring.manipulators;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;

import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.SystemObject;

public class MessageChainRefactoring {

	/**
	 * New function for making codes about new method after user decides to Refactoring
	 * 
	 * <Arguments> String newMethodName: Name of new Method
	 * String returnType: Return type of new Method
	 * List<String> stringOfArgumentType: String List that contains whole of argument types in original methods
	 * List<Integer> numOfArgumentOfEachMethod: Integer List that contains number of arguments for each methods
	 * List<String> stringOfMethodInvocation: String List that contain whole of method invocations
	 **/
	public static String makeNewMethodCode(String newMethodName, String returnType, List<String> stringOfArgumentType,
			List<Integer> numOfArgumentOfEachMethod, List<String> stringOfMethodInvocation) {
		String strOfMethod = "";
		strOfMethod += "\r\n\tpublic ";
		strOfMethod += returnType;
		strOfMethod += " ";
		strOfMethod += newMethodName;
		strOfMethod += "(";
		for (int i = 0; i < stringOfArgumentType.size(); i++) {
			strOfMethod += stringOfArgumentType.get(i);
			strOfMethod += " ";
			strOfMethod += "x";
			strOfMethod += Integer.toString(i);
			strOfMethod += ", ";
		}
		if (stringOfArgumentType.size() > 0) {
			strOfMethod = strOfMethod.substring(0, strOfMethod.length() - 2);
		}
		strOfMethod += ")";
		strOfMethod += " {\r\n";
		strOfMethod += "\t\treturn ";
		int numOfArg = 0;
		int numOfMethod = 0;
		for (String method : stringOfMethodInvocation) {
			strOfMethod += method;
			strOfMethod += "(";
			for (int i = 0; i < numOfArgumentOfEachMethod.get(numOfMethod); i++, numOfArg++) {
				strOfMethod += "x";
				strOfMethod += Integer.toString(numOfArg);
				strOfMethod += ", ";
			}
			if (numOfArgumentOfEachMethod.get(numOfMethod) > 0) {
				strOfMethod = strOfMethod.substring(0, strOfMethod.length() - 2);
			}
			strOfMethod += ")";
			strOfMethod += ".";
			numOfMethod++;
		}
		strOfMethod = strOfMethod.substring(0, strOfMethod.length() - 1);
		strOfMethod += ";\r\n";
		strOfMethod += "\t}\r\n";
		return strOfMethod;
	}

	/**
	 * New function for modify code that contains new method after user decides to Refactoring
	 * 
	 * <Arguments> String newMethodName: Name of new method
	 * List<String> stringOfArgument: String List that contains whole of arguments
	 **/
	public static String makeNewRefactorCode(String newMethodName, List<String> stringOfArgument) {
		String strOfRefact = "";
		strOfRefact += newMethodName;
		strOfRefact += "(";
		for (String arg : stringOfArgument) {
			strOfRefact += arg;
			strOfRefact += ", ";
		}
		if (stringOfArgument.size() > 0) {
			strOfRefact = strOfRefact.substring(0, strOfRefact.length() - 2);
		}
		strOfRefact += ")";
		return strOfRefact;
	}

	/**
	 * New function for getting ICompilationUnit when program refactoring
	 * 
	 * <Arguments> SystemObject systemObject: String className:
	 **/
	public static ICompilationUnit getCompUnit(SystemObject systemObject, String className) {
		ClassObject classWithCodeSmell = systemObject.getClassObject(className);
		IFile fileWithCodeSmell = classWithCodeSmell.getIFile();
		ICompilationUnit compUnitWithCodeSmell = (ICompilationUnit) JavaCore.create(fileWithCodeSmell);
		return compUnitWithCodeSmell;
	}

	/**
	 * New function for Adding Extracted Method that is created by Rafactoring
	 *
	 * <Arguments> ICompilationUnit compUnitOfMethodInvocation: Information that java file (or class) that program add codes
	 * String strOfMethod: String that codes of Extracted Method
	 **/
	public static void modifyMethodInvocationFile(ICompilationUnit compUnitOfMethodInvocation, String strOfMethod) {
		try {
			ICompilationUnit workingCopyOfMethodInvocation = compUnitOfMethodInvocation
					.getWorkingCopy(new WorkingCopyOwner() {
					}, null);
			IBuffer bufferOfMethodInvocation = ((IOpenable) workingCopyOfMethodInvocation).getBuffer();

			int modifyPosition = getModifyPosition(bufferOfMethodInvocation);

			modifyCompUnit(workingCopyOfMethodInvocation, bufferOfMethodInvocation, modifyPosition, 0, strOfMethod);
		} catch (JavaModelException e) {
		}

	}

	/**
	 * New function for modifying position to modify original code properly
	 * 
	 * <Arguments> IBuffer bufferOfMethodInvocation: Buffer that contains whole codes about selected Java File
	 **/
	public static int getModifyPosition(IBuffer bufferOfMethodInvocation) {
		int length = bufferOfMethodInvocation.getLength();
		int count = 0;
		for (int i = length - 1; i >= 0; i--, count++) {
			if (bufferOfMethodInvocation.getChar(i) == '}')
				break;
		}
		return length - (count + 1);
	}

	/**
	 * New function for modifying java file codes using User's inputs
	 *
	 * <Arguments>
	 * ICompilationUnit workingCopy: workingCopy helps to change codes
	 * IBuffer buffer: Ibuffer that contains whole of codes in java file
	 * int startPos: Start position that program added replaced codes
	 * int len: Length of replaced codes String stringForChange: String that contains replaced codes
	 **/
	public static void modifyCompUnit(ICompilationUnit workingCopy, IBuffer buffer, int startPos, int len,
			String stringForChange) {
		try {
			buffer.replace(startPos, len, stringForChange);
			workingCopy.reconcile(ICompilationUnit.NO_AST, false, null, null);
			workingCopy.commitWorkingCopy(false, null);
			workingCopy.discardWorkingCopy();
		} catch (JavaModelException e) {
		}

	}

	/**
	 * New function for getting only class name if the string contains path of class
	 * 
	 * <Arguments>
	 * int sizeOfMethodInvocation: Length of string that is path of class
	 * String stringOfMethodInvocation: String that is path of class
	 **/
	public static String getClassName(int sizeOfMethodInvocation, String stringOfMethodInvocation) {
		int count = 0;
		for (int i = stringOfMethodInvocation.length() - 1; i >= 0; i--) {
			if (stringOfMethodInvocation.charAt(i) == '.') {
				count = i + 1;
				break;
			}
		}
		return stringOfMethodInvocation.substring(count, stringOfMethodInvocation.length());
	}

}
