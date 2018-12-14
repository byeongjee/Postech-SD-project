package gr.uom.java.jdeodorant.refactoring.manipulators;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

public class DeleteClassRefactoringDescriptor extends RefactoringDescriptor {

	public static final String REFACTORING_ID = "org.eclipse.extract.method";
	private CompilationUnit sourceCompilationUnit;
	
	protected DeleteClassRefactoringDescriptor(String project, String description, String comment, 
			CompilationUnit sourceCompilationUnit) {
		super(REFACTORING_ID, project, description, comment, RefactoringDescriptor.STRUCTURAL_CHANGE | RefactoringDescriptor.MULTI_CHANGE);
		this.sourceCompilationUnit = sourceCompilationUnit;
	}

	@Override
	public Refactoring createRefactoring(RefactoringStatus status)
			throws CoreException {
		Refactoring refactoring = new DeleteClassRefactoring(sourceCompilationUnit);
		RefactoringStatus refStatus = new RefactoringStatus();
		status.merge(refStatus);
		return refactoring;
	}
}
