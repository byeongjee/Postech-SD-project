package gr.uom.java.jdeodorant.refactoring.views;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import gr.uom.java.ast.ASTReader;
import gr.uom.java.ast.AbstractMethodDeclaration;
import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.ClassObjectCandidate;
import gr.uom.java.ast.CompilationErrorDetectedException;
import gr.uom.java.ast.CompilationUnitCache;
import gr.uom.java.ast.MethodObject;
import gr.uom.java.ast.SystemObject;
import gr.uom.java.ast.TypeObject;
import gr.uom.java.ast.decomposition.cfg.CFG;
import gr.uom.java.ast.decomposition.cfg.PDG;
import gr.uom.java.ast.decomposition.cfg.PDGObjectSliceUnion;
import gr.uom.java.ast.decomposition.cfg.PDGObjectSliceUnionCollection;
import gr.uom.java.ast.decomposition.cfg.PDGSliceUnion;
import gr.uom.java.ast.decomposition.cfg.PDGSliceUnionCollection;
import gr.uom.java.ast.decomposition.cfg.PlainVariable;
import gr.uom.java.ast.util.StatementExtractor;
import gr.uom.java.jdeodorant.preferences.PreferenceConstants;
import gr.uom.java.jdeodorant.refactoring.Activator;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSlice;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSliceGroup;
import gr.uom.java.jdeodorant.refactoring.manipulators.ExtractMethodRefactoring;

import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Detect Code Smell of Speculative Generality
 * @author 이재엽, 이주용
 *
 */
public class SpeculativeGenerality extends ViewPart {
	private static final String MESSAGE_DIALOG_TITLE = "Speculative Generality";
	private TreeViewer treeViewer;
	private Action identifyBadSmellsAction;
	private Action applyRefactoringAction;
	private Action doubleClickAction;
	private Action saveResultsAction;
	//private Action evolutionAnalysisAction;
	private IJavaProject selectedProject;
	private IJavaProject activeProject;
	private IPackageFragmentRoot selectedPackageFragmentRoot;
	private IPackageFragment selectedPackageFragment;
	private ICompilationUnit selectedCompilationUnit;
	private IType selectedType;
	private IMethod selectedMethod;
	
	// Exp : would-be extended to contain information of line, source-path, and so forth... (for UI)
	private ClassObjectCandidate[] classObjectTable; 
	
	
	class ViewContentProvider implements ITreeContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			if(classObjectTable!=null) {
				return classObjectTable;
			}
			else {
				return new ClassObjectCandidate[] {};
			}
		}
		// Warn : Quite Sure a/ JuYongLee, JaeYeopLee
		public Object[] getChildren(Object arg) {
			if(arg instanceof ClassObjectCandidate[]) {
				return (ClassObjectCandidate[])arg; 
			} else {
				ClassObjectCandidate[] res = { };
				return res; 
			}
		}
		// Warn : Quite Sure a/ JuYongLee, JaeYeopLee
		public Object getParent(Object arg0) {
			if(arg0 instanceof ClassObjectCandidate[]) {
				ClassObjectCandidate target = (ClassObjectCandidate)arg0;
				
				for(int i=0; i<classObjectTable.length; i++) {
					if(classObjectTable[i].getName().equals(target.getName())) {
						return classObjectTable[i];
					}
				}
			}
				
			return null;
		}
		public boolean hasChildren(Object arg0) {
			return getChildren(arg0).length > 0;
		}
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			if(obj instanceof ClassObjectCandidate) {
				ClassObjectCandidate entry = (ClassObjectCandidate)obj;
				switch(index){
				case 0:
					return entry.getRefactorType();
				case 1:
					return entry.getName();
				case 2:
					IFile sourceFile = entry.getIFile();
					return sourceFile.getFullPath().toString();
				case 3:
					return entry.getCodeSmellType();
				case 4:
					return "";
				case 5:
					return "";
				default:
					return "";
				}
			}
			return "";
		}
		
		public Image getColumnImage(Object obj, int index) {
			Image image = null;
			if(obj instanceof ASTSlice) {
				ASTSlice entry = (ASTSlice)obj;
				int rate = -1;
				Integer userRate = entry.getUserRate();
				if(userRate != null)
					rate = userRate;
				switch(index) {
				case 5:
					if(rate != -1) {
						image = Activator.getImageDescriptor("/icons/" + String.valueOf(rate) + ".jpg").createImage();
					}
				default:
					break;
				}
			}
			return image;
		}
		public Image getImage(Object obj) {
			return null;
		}
	}

	class NameSorter extends ViewerSorter {
		public int compare(Viewer viewer, Object obj1, Object obj2) {
			if(obj1 instanceof ClassObjectCandidate && obj2 instanceof ClassObjectCandidate) {
				ClassObjectCandidate classObject1 = (ClassObjectCandidate)obj1;
				ClassObjectCandidate classObject2 = (ClassObjectCandidate)obj2;
				return classObject1.getName().compareTo(classObject2.getName());
			}
			else if(obj1 instanceof ClassObject && obj2 instanceof ClassObject) {
				ClassObject classObject1 = (ClassObject)obj1;
				ClassObject classObject2 = (ClassObject)obj2;
				return classObject1.getName().compareTo(classObject2.getName());
			}
			else
			{
				System.out.println("In NameSorter : else");
				return 1;
			}
			
			/*
			if(obj1 instanceof ASTSliceGroup && obj2 instanceof ASTSliceGroup) {
				ASTSliceGroup sliceGroup1 = (ASTSliceGroup)obj1;
				ASTSliceGroup sliceGroup2 = (ASTSliceGroup)obj2;
				return sliceGroup1.compareTo(sliceGroup2);
			}
			else {
				ASTSlice slice1 = (ASTSlice)obj1;
				ASTSlice slice2 = (ASTSlice)obj2;
				//slices belong to the same group
				return Integer.valueOf(slice1.getBoundaryBlock().getId()).compareTo(Integer.valueOf(slice2.getBoundaryBlock().getId()));
			}
			*/
		}
	}
	
	private ISelectionListener selectionListener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection)selection;
				Object element = structuredSelection.getFirstElement();
				IJavaProject javaProject = null;
				if(element instanceof IJavaProject) {
					javaProject = (IJavaProject)element;
					selectedPackageFragmentRoot = null;
					selectedPackageFragment = null;
					selectedCompilationUnit = null;
					selectedType = null;
					selectedMethod = null;
				}
				else if(element instanceof IPackageFragmentRoot) {
					IPackageFragmentRoot packageFragmentRoot = (IPackageFragmentRoot)element;
					javaProject = packageFragmentRoot.getJavaProject();
					selectedPackageFragmentRoot = packageFragmentRoot;
					selectedPackageFragment = null;
					selectedCompilationUnit = null;
					selectedType = null;
					selectedMethod = null;
				}
				else if(element instanceof IPackageFragment) {
					IPackageFragment packageFragment = (IPackageFragment)element;
					javaProject = packageFragment.getJavaProject();
					selectedPackageFragment = packageFragment;
					selectedPackageFragmentRoot = null;
					selectedCompilationUnit = null;
					selectedType = null;
					selectedMethod = null;
				}
				else if(element instanceof ICompilationUnit) {
					ICompilationUnit compilationUnit = (ICompilationUnit)element;
					javaProject = compilationUnit.getJavaProject();
					selectedCompilationUnit = compilationUnit;
					selectedPackageFragmentRoot = null;
					selectedPackageFragment = null;
					selectedType = null;
					selectedMethod = null;
				}
				else if(element instanceof IType) {
					IType type = (IType)element;
					javaProject = type.getJavaProject();
					selectedType = type;
					selectedPackageFragmentRoot = null;
					selectedPackageFragment = null;
					selectedCompilationUnit = null;
					selectedMethod = null;
				}
				else if(element instanceof IMethod) {
					IMethod method = (IMethod)element;
					javaProject = method.getJavaProject();
					selectedMethod = method;
					selectedPackageFragmentRoot = null;
					selectedPackageFragment = null;
					selectedCompilationUnit = null;
					selectedType = null;
				}
				if(javaProject != null && !javaProject.equals(selectedProject)) {
					selectedProject = javaProject;
					/*if(sliceTable != null)
						tableViewer.remove(sliceTable);*/
					identifyBadSmellsAction.setEnabled(true);
				}
			}
		}
	};

	@Override
	public void createPartControl(Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		treeViewer.setContentProvider(new ViewContentProvider());
		treeViewer.setLabelProvider(new ViewLabelProvider());
		treeViewer.setSorter(new NameSorter());
		treeViewer.setInput(getViewSite());
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(20, true));
		layout.addColumnData(new ColumnWeightData(60, true));
		layout.addColumnData(new ColumnWeightData(40, true));
		layout.addColumnData(new ColumnWeightData(20, true));
		layout.addColumnData(new ColumnWeightData(20, true));
		layout.addColumnData(new ColumnWeightData(20, true));
		treeViewer.getTree().setLayout(layout);
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.getTree().setLinesVisible(true);
		treeViewer.getTree().setHeaderVisible(true);
		TreeColumn column0 = new TreeColumn(treeViewer.getTree(),SWT.LEFT);
		column0.setText("Refactoring Type");
		column0.setResizable(true);
		column0.pack();
		TreeColumn column1 = new TreeColumn(treeViewer.getTree(),SWT.LEFT);
		column1.setText("Source Class");
		column1.setResizable(true);
		column1.pack();
		TreeColumn column2 = new TreeColumn(treeViewer.getTree(),SWT.LEFT);
		column2.setText("Class Path");
//		column2.setText("Variable Criterion");
		column2.setResizable(true);
		column2.pack();
		TreeColumn column3 = new TreeColumn(treeViewer.getTree(),SWT.LEFT);
//		column3.setText("Block-Based Region");
		column3.setText("Code Smell Type");
		column3.setResizable(true);
		column3.pack();
		TreeColumn column4 = new TreeColumn(treeViewer.getTree(),SWT.LEFT);
		column4.setText("Duplicated/Extracted");
		column4.setResizable(true);
		column4.pack();
		
		TreeColumn column5 = new TreeColumn(treeViewer.getTree(),SWT.LEFT);
		column5.setText("Rate it!");
		column5.setResizable(true);
		column5.pack();
		treeViewer.expandAll();
		
		treeViewer.setColumnProperties(new String[] {"type", "source", "variable", "block", "duplicationRatio", "rate"});
		treeViewer.setCellEditors(new CellEditor[] {
				new TextCellEditor(), new TextCellEditor(), new TextCellEditor(), new TextCellEditor(), new TextCellEditor(),
				new MyComboBoxCellEditor(treeViewer.getTree(), new String[] {"0", "1", "2", "3", "4", "5"}, SWT.READ_ONLY)
		});
		
		treeViewer.setCellModifier(new ICellModifier() {
			public boolean canModify(Object element, String property) {
				System.out.println("In CanModify");
				return property.equals("rate");
			}

			public Object getValue(Object element, String property) {
				if(element instanceof ASTSlice) {
					ASTSlice slice = (ASTSlice)element;
					if(slice.getUserRate() != null)
						return slice.getUserRate();
					else
						return 0;
				}
				
				if(element instanceof ClassObject) {
				}
				
				return 0;
			}

			public void modify(Object element, String property, Object value) {
				TreeItem item = (TreeItem)element;
				Object data = item.getData();
				if(data instanceof ASTSlice) {
					ASTSlice slice = (ASTSlice)data;
					slice.setUserRate((Integer)value);
					IPreferenceStore store = Activator.getDefault().getPreferenceStore();
					boolean allowUsageReporting = store.getBoolean(PreferenceConstants.P_ENABLE_USAGE_REPORTING);
					if(allowUsageReporting) {
						Tree tree = treeViewer.getTree();
						int groupPosition = -1;
						int totalGroups = tree.getItemCount();
						for(int i=0; i<tree.getItemCount(); i++) {
							TreeItem treeItem = tree.getItem(i);
							ASTSliceGroup group = (ASTSliceGroup)treeItem.getData();
							if(group.getCandidates().contains(slice)) {
								groupPosition = i;
								break;
							}
						}
						try {
							boolean allowSourceCodeReporting = store.getBoolean(PreferenceConstants.P_ENABLE_SOURCE_CODE_REPORTING);
							String declaringClass = slice.getSourceTypeDeclaration().resolveBinding().getQualifiedName();
							String methodName = slice.getSourceMethodDeclaration().resolveBinding().toString();
							String sourceMethodName = declaringClass + "::" + methodName;
							String content = URLEncoder.encode("project_name", "UTF-8") + "=" + URLEncoder.encode(activeProject.getElementName(), "UTF-8");
							content += "&" + URLEncoder.encode("source_method_name", "UTF-8") + "=" + URLEncoder.encode(sourceMethodName, "UTF-8");
							content += "&" + URLEncoder.encode("variable_name", "UTF-8") + "=" + URLEncoder.encode(slice.getLocalVariableCriterion().resolveBinding().toString(), "UTF-8");
							content += "&" + URLEncoder.encode("block", "UTF-8") + "=" + URLEncoder.encode("B" + slice.getBoundaryBlock().getId(), "UTF-8");
							content += "&" + URLEncoder.encode("object_slice", "UTF-8") + "=" + URLEncoder.encode(slice.isObjectSlice() ? "1" : "0", "UTF-8");
							int numberOfSliceStatements = slice.getNumberOfSliceStatements();
							int numberOfDuplicatedStatements = slice.getNumberOfDuplicatedStatements();
							content += "&" + URLEncoder.encode("duplicated_statements", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(numberOfDuplicatedStatements), "UTF-8");
							content += "&" + URLEncoder.encode("extracted_statements", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(numberOfSliceStatements), "UTF-8");
							content += "&" + URLEncoder.encode("ranking_position", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(groupPosition), "UTF-8");
							content += "&" + URLEncoder.encode("total_opportunities", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(totalGroups), "UTF-8");
							if(allowSourceCodeReporting) {
								content += "&" + URLEncoder.encode("source_method_code", "UTF-8") + "=" + URLEncoder.encode(slice.getSourceMethodDeclaration().toString(), "UTF-8");
								content += "&" + URLEncoder.encode("slice_statements", "UTF-8") + "=" + URLEncoder.encode(slice.sliceToString(), "UTF-8");
							}
							content += "&" + URLEncoder.encode("rating", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(slice.getUserRate()), "UTF-8");
							content += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(System.getProperty("user.name"), "UTF-8");
							content += "&" + URLEncoder.encode("tb", "UTF-8") + "=" + URLEncoder.encode("2", "UTF-8");
							URL url = new URL(Activator.RANK_URL);
							URLConnection urlConn = url.openConnection();
							urlConn.setDoInput(true);
							urlConn.setDoOutput(true);
							urlConn.setUseCaches(false);
							urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
							DataOutputStream printout = new DataOutputStream(urlConn.getOutputStream());
							printout.writeBytes(content);
							printout.flush();
							printout.close();
							DataInputStream input = new DataInputStream(urlConn.getInputStream());
							input.close();
						} catch (IOException ioe) {
							ioe.printStackTrace();
						}
					}
					treeViewer.update(data, null);
				}
				
			}
			
		});
		makeActions();
		hookDoubleClickAction();
		contributeToActionBars();
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(selectionListener);
		JavaCore.addElementChangedListener(ElementChangedListener.getInstance());
		getSite().getWorkbenchWindow().getWorkbench().getOperationSupport().getOperationHistory().addOperationHistoryListener(new IOperationHistoryListener() {
			public void historyNotification(OperationHistoryEvent event) {
				int eventType = event.getEventType();
				if(eventType == OperationHistoryEvent.UNDONE  || eventType == OperationHistoryEvent.REDONE ||
						eventType == OperationHistoryEvent.OPERATION_ADDED || eventType == OperationHistoryEvent.OPERATION_REMOVED) {
					if(activeProject != null && CompilationUnitCache.getInstance().getAffectedProjects().contains(activeProject)) {
						applyRefactoringAction.setEnabled(false);
						saveResultsAction.setEnabled(false);
						//evolutionAnalysisAction.setEnabled(false);
					}
				}
			}
		});
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(identifyBadSmellsAction);
		manager.add(applyRefactoringAction);
		manager.add(saveResultsAction);
		//manager.add(evolutionAnalysisAction);
	}

	private void makeActions() {
		identifyBadSmellsAction = new Action() {
			public void run() {
				activeProject = selectedProject;
				CompilationUnitCache.getInstance().clearCache();
				classObjectTable = getTable();
				treeViewer.setContentProvider(new ViewContentProvider());
				applyRefactoringAction.setEnabled(true);
				saveResultsAction.setEnabled(true);
				//evolutionAnalysisAction.setEnabled(true);
			}
		};
		identifyBadSmellsAction.setToolTipText("Identify Bad Smells");
		identifyBadSmellsAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		identifyBadSmellsAction.setEnabled(false);
		
		saveResultsAction = new Action() {
			public void run() {
				saveResults();
			}
		};
		saveResultsAction.setToolTipText("Save Results");
		saveResultsAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT));
		saveResultsAction.setEnabled(false);

		applyRefactoringAction = new Action() {
			public void run() {
				System.out.println("In ApplyRefactoringAction run");
				
				
				IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
				if(selection != null && selection.getFirstElement() instanceof ClassObject) {
					IFile sourceFile = ((ClassObject) selection.getFirstElement()).getIFile();
					System.out.println(sourceFile.getFullPath().toString());
					//
				}
//					IPreferenceStore store = Activator.getDefault().getPreferenceStore();
//					boolean allowUsageReporting = store.getBoolean(PreferenceConstants.P_ENABLE_USAGE_REPORTING);
//					if(allowUsageReporting) {
//						Tree tree = treeViewer.getTree();
//						int groupPosition = -1;
//						int totalGroups = tree.getItemCount();
//						for(int i=0; i<tree.getItemCount(); i++) {
//							TreeItem treeItem = tree.getItem(i);
//							ASTSliceGroup group = (ASTSliceGroup)treeItem.getData();
//							if(group.getCandidates().contains(slice)) {
//								groupPosition = i;
//								break;
//							}
//						}
//						try {
//							boolean allowSourceCodeReporting = store.getBoolean(PreferenceConstants.P_ENABLE_SOURCE_CODE_REPORTING);
//							String declaringClass = slice.getSourceTypeDeclaration().resolveBinding().getQualifiedName();
//							String methodName = slice.getSourceMethodDeclaration().resolveBinding().toString();
//							String sourceMethodName = declaringClass + "::" + methodName;
//							String content = URLEncoder.encode("project_name", "UTF-8") + "=" + URLEncoder.encode(activeProject.getElementName(), "UTF-8");
//							content += "&" + URLEncoder.encode("source_method_name", "UTF-8") + "=" + URLEncoder.encode(sourceMethodName, "UTF-8");
//							content += "&" + URLEncoder.encode("variable_name", "UTF-8") + "=" + URLEncoder.encode(slice.getLocalVariableCriterion().resolveBinding().toString(), "UTF-8");
//							content += "&" + URLEncoder.encode("block", "UTF-8") + "=" + URLEncoder.encode("B" + slice.getBoundaryBlock().getId(), "UTF-8");
//							content += "&" + URLEncoder.encode("object_slice", "UTF-8") + "=" + URLEncoder.encode(slice.isObjectSlice() ? "1" : "0", "UTF-8");
//							int numberOfSliceStatements = slice.getNumberOfSliceStatements();
//							int numberOfDuplicatedStatements = slice.getNumberOfDuplicatedStatements();
//							content += "&" + URLEncoder.encode("duplicated_statements", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(numberOfDuplicatedStatements), "UTF-8");
//							content += "&" + URLEncoder.encode("extracted_statements", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(numberOfSliceStatements), "UTF-8");
//							content += "&" + URLEncoder.encode("ranking_position", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(groupPosition), "UTF-8");
//							content += "&" + URLEncoder.encode("total_opportunities", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(totalGroups), "UTF-8");
//							if(allowSourceCodeReporting) {
//								content += "&" + URLEncoder.encode("source_method_code", "UTF-8") + "=" + URLEncoder.encode(slice.getSourceMethodDeclaration().toString(), "UTF-8");
//								content += "&" + URLEncoder.encode("slice_statements", "UTF-8") + "=" + URLEncoder.encode(slice.sliceToString(), "UTF-8");
//							}
//							content += "&" + URLEncoder.encode("application", "UTF-8") + "=" + URLEncoder.encode(String.valueOf("1"), "UTF-8");
//							content += "&" + URLEncoder.encode("application_selected_name", "UTF-8") + "=" + URLEncoder.encode(slice.getExtractedMethodName(), "UTF-8");
//							content += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(System.getProperty("user.name"), "UTF-8");
//							content += "&" + URLEncoder.encode("tb", "UTF-8") + "=" + URLEncoder.encode("2", "UTF-8");
//							URL url = new URL(Activator.RANK_URL);
//							URLConnection urlConn = url.openConnection();
//							urlConn.setDoInput(true);
//							urlConn.setDoOutput(true);
//							urlConn.setUseCaches(false);
//							urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//							DataOutputStream printout = new DataOutputStream(urlConn.getOutputStream());
//							printout.writeBytes(content);
//							printout.flush();
//							printout.close();
//							DataInputStream input = new DataInputStream(urlConn.getInputStream());
//							input.close();
//						} catch (IOException ioe) {
//							ioe.printStackTrace();
//						}
//					}
//					Refactoring refactoring = new ExtractMethodRefactoring(sourceCompilationUnit, slice);
//					try {
//						IJavaElement sourceJavaElement = JavaCore.create(sourceFile);
//						JavaUI.openInEditor(sourceJavaElement);
//					} catch (PartInitException e) {
//						e.printStackTrace();
//					} catch (JavaModelException e) {
//						e.printStackTrace();
//					}
//					MyRefactoringWizard wizard = new MyRefactoringWizard(refactoring, applyRefactoringAction);
//					RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard); 
//					try { 
//						String titleForFailedChecks = ""; //$NON-NLS-1$ 
//						op.run(getSite().getShell(), titleForFailedChecks); 
//					} catch(InterruptedException e) {
//						e.printStackTrace();
//					}
//				}

				System.out.println("After ApplyRefactoringAction run : end after if");
			}
		};

		applyRefactoringAction.setToolTipText("Apply Refactoring");
		applyRefactoringAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_DEF_VIEW));
		applyRefactoringAction.setEnabled(false);
		
		doubleClickAction = new Action() {
			public void run() {
				IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
				if(selection.getFirstElement() instanceof ASTSlice) {
					ASTSlice slice = (ASTSlice)selection.getFirstElement();
					IFile sourceFile = slice.getIFile();
					try {
						IJavaElement sourceJavaElement = JavaCore.create(sourceFile);
						ITextEditor sourceEditor = (ITextEditor)JavaUI.openInEditor(sourceJavaElement);
						Object[] highlightPositionMaps = slice.getHighlightPositions();
						Map<Position, String> annotationMap = (Map<Position, String>)highlightPositionMaps[0];
						Map<Position, Boolean> duplicationMap = (Map<Position, Boolean>)highlightPositionMaps[1];
						AnnotationModel annotationModel = (AnnotationModel)sourceEditor.getDocumentProvider().getAnnotationModel(sourceEditor.getEditorInput());
						Iterator<Annotation> annotationIterator = annotationModel.getAnnotationIterator();
						while(annotationIterator.hasNext()) {
							Annotation currentAnnotation = annotationIterator.next();
							if(currentAnnotation.getType().equals(SliceAnnotation.EXTRACTION) || currentAnnotation.getType().equals(SliceAnnotation.DUPLICATION)) {
								annotationModel.removeAnnotation(currentAnnotation);
							}
						}
						for(Position position : annotationMap.keySet()) {
							SliceAnnotation annotation = null;
							String annotationText = annotationMap.get(position);
							boolean duplicated = duplicationMap.get(position);
							if(duplicated)
								annotation = new SliceAnnotation(SliceAnnotation.DUPLICATION, annotationText);
							else
								annotation = new SliceAnnotation(SliceAnnotation.EXTRACTION, annotationText);
							annotationModel.addAnnotation(annotation, position);
						}
						List<Position> positions = new ArrayList<Position>(annotationMap.keySet());
						Position firstPosition = positions.get(0);
						Position lastPosition = positions.get(positions.size()-1);
						int offset = firstPosition.getOffset();
						int length = lastPosition.getOffset() + lastPosition.getLength() - firstPosition.getOffset();
						sourceEditor.setHighlightRange(offset, length, true);
					} catch (PartInitException e) {
						e.printStackTrace();
					} catch (JavaModelException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	private void hookDoubleClickAction() {
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	@Override
	public void setFocus() {
		treeViewer.getControl().setFocus();
	}

	public void dispose() {
		super.dispose();
		getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(selectionListener);
	}

	private ClassObjectCandidate[] getTable() {
		ClassObjectCandidate[] table = null;
		System.out.println("In GetTable");
		try {
			System.out.println("In GetTable : Try");
			IWorkbench wb = PlatformUI.getWorkbench();
			IProgressService ps = wb.getProgressService();
			if(ASTReader.getSystemObject() != null && activeProject.equals(ASTReader.getExaminedProject())) {
				new ASTReader(activeProject, ASTReader.getSystemObject(), null);
			}
			else {
				ps.busyCursorWhile(new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						try {
							new ASTReader(activeProject, monitor);
						} catch (CompilationErrorDetectedException e) {
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), MESSAGE_DIALOG_TITLE,
											"Compilation errors were detected in the project. Fix the errors before using JDeodorant.");
								}
							});
						}
					}
				});
			}
			SystemObject systemObject = ASTReader.getSystemObject();
			if(systemObject != null) {
				System.out.println("In GetTable : Before Declaring classObjectsToBeExamined");
				Set<ClassObject> classObjectsToBeExamined = new LinkedHashSet<ClassObject>();
				if(selectedPackageFragmentRoot != null) {
					classObjectsToBeExamined.addAll(systemObject.getClassObjects(selectedPackageFragmentRoot));
				}
				else if(selectedPackageFragment != null) {
					classObjectsToBeExamined.addAll(systemObject.getClassObjects(selectedPackageFragment));
				}
				else if(selectedCompilationUnit != null) {
				}
				else if(selectedType != null) {
				}
				else {
					classObjectsToBeExamined.addAll(systemObject.getClassObjects());
				}

				final Set<String> classNamesToBeExamined = new LinkedHashSet<String>();
				for(ClassObject classObject : classObjectsToBeExamined) {
					if(!classObject.isEnum() && !classObject.isInterface() && !classObject.isGeneratedByParserGenenator())
						classNamesToBeExamined.add(classObject.getName());
				}
				
				System.out.println("In GetTable : Printing classObjectsToBeExamined");
				for(ClassObject target : classObjectsToBeExamined) {
					System.out.println(target.getName());
				}
				System.out.println("In GetTable : Printed classObjectsToBeExamined");
				
				table=processMethod(classObjectsToBeExamined);
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (CompilationErrorDetectedException e) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), MESSAGE_DIALOG_TITLE,
					"Compilation errors were detected in the project. Fix the errors before using JDeodorant.");
		}
		
		return table;	
	}

	/**
	 *  Examine the class objects
	 * @param classObjectsToBeExamined
	 * @return Array of ClassObject
	 */
	private ClassObjectCandidate[] processMethod(final Set<ClassObject> classObjectsToBeExamined){	
//		final List<ClassObject> classObjectswithSG = new ArrayList<ClassObject>();
		final List<ClassObjectCandidate> classObjectswithSG = new ArrayList<ClassObjectCandidate>();
		
		for(ClassObject _ClassObject : classObjectsToBeExamined) {
			ClassObjectCandidate targetClass = new ClassObjectCandidate(_ClassObject);
			System.out.println("In ProcessMethod : Type Conversion Test");
			System.out.println(targetClass.getName());
			
			// Unnecessary Abstraction
			if(targetClass.isAbstract()) {
				int childOfTargetNum = 0;
				
				for(ClassObject childCandidate : classObjectsToBeExamined) {
					TypeObject superClass = childCandidate.getSuperclass();
					if(superClass == null) {
						continue;
					}
				
					if(superClass.getClassType().equals(targetClass.getName())){
						childOfTargetNum++;
					}
					if(childOfTargetNum >= 2)
						break;
				}
				if(childOfTargetNum < 2) {
					targetClass.setCodeSmellType("Abstraction");
					classObjectswithSG.add(targetClass);
				}
			}
			else if(targetClass.isInterface()) {
				int childOfTargetNum = 0;
				
				for(ClassObject childCandidate : classObjectsToBeExamined) {
					ListIterator<TypeObject> myIter=childCandidate.getInterfaceIterator();
					while(myIter.hasNext()) {
						//
						if(myIter.next().getClassType().equals(targetClass.getName())) {
							childOfTargetNum++;
							break;
						}
					}
					if(childOfTargetNum >= 2)
						break;
				}
				if(childOfTargetNum < 2) {
					targetClass.setCodeSmellType("Interface");
					classObjectswithSG.add(targetClass);
				}
			}
			
			// Unnecsary Parameter
			
			//Method Iterator have critical problem currently
			
//			System.out.println("In ProcessMethod : for loop of methoditerator");
//			if(!targetClass.isEnum() && !targetClass.isInterface() && !targetClass.isGeneratedByParserGenenator()) {
//				ListIterator<MethodObject> methodIterator = targetClass.getMethodIterator();
//				while(methodIterator.hasNext()) {
//					// ToDo : find unnecessary parameter
//					MethodObject methodObject = methodIterator.next();
//					System.out.println(methodObject.getClassName()+"::"+methodObject.getName());
//					for(int i=0;i<methodObject.getParameterList().size();i++) {
//						System.out.println(methodObject.getParameter(i).getName());
//						System.out.println(methodObject.getClassName());
//					}
//					
//					if(!targetClass.codeSmellMethodList.isEmpty())
//					{
//						// ToDo : Add target for each smelling Method
//						targetClass.codeSmellType="Parameter";
//						classObjectswithSG.add(targetClass);
//					}
//				}
//			}
		}
		
		System.out.println("In ProcessMethod : Printing results which are smelling classes");
		ClassObjectCandidate[] res = new ClassObjectCandidate[classObjectswithSG.size()];
		for(int i=0;i<classObjectswithSG.size();i++) {
			res[i]= classObjectswithSG.get(i);
			System.out.println(res[i].getName());
		}
		System.out.println("In ProcessMethod : Printed results which are smelling classes");
		
		return res;
	}

	private void saveResults() {
		FileDialog fd = new FileDialog(getSite().getWorkbenchWindow().getShell(), SWT.SAVE);
		fd.setText("Save Results");
        String[] filterExt = { "*.txt" };
        fd.setFilterExtensions(filterExt);
        String selected = fd.open();
        if(selected != null) {
        	try {
        		BufferedWriter out = new BufferedWriter(new FileWriter(selected));
        		Tree tree = treeViewer.getTree();
        		/*TreeColumn[] columns = tree.getColumns();
        		for(int i=0; i<columns.length; i++) {
        			if(i == columns.length-1)
        				out.write(columns[i].getText());
        			else
        				out.write(columns[i].getText() + "\t");
        		}
        		out.newLine();*/
        		for(int i=0; i<tree.getItemCount(); i++) {
					TreeItem treeItem = tree.getItem(i);
					ASTSliceGroup group = (ASTSliceGroup)treeItem.getData();
					for(ASTSlice candidate : group.getCandidates()) {
						out.write(candidate.toString());
						out.newLine();
					}
				}
        		out.close();
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
        }
	}
}
