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

import javax.swing.event.TreeExpansionListener;

import gr.uom.java.ast.ASTReader;
import gr.uom.java.ast.AbstractMethodDeclaration;
import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.ClassObjectCandidate;
import gr.uom.java.ast.CompilationErrorDetectedException;
import gr.uom.java.ast.CompilationUnitCache;
import gr.uom.java.ast.LPLMethodObject;
import gr.uom.java.ast.MethodObject;
import gr.uom.java.ast.SystemObject;
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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.internal.corext.refactoring.ParameterInfo;
import org.eclipse.jdt.internal.corext.refactoring.structure.ChangeSignatureProcessor;
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
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.ResourceChangeChecker;
import org.eclipse.ltk.core.refactoring.participants.ValidateEditChecker;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
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

public class LongParameterList extends ViewPart {
	private static final String MESSAGE_DIALOG_TITLE = "Long Method";
	private TreeViewer treeViewer;
	private Action identifyBadSmellsAction;
	private Action applyRefactoringAction;
	private Action doubleClickAction;
	private Action saveResultsAction;
	// private Action evolutionAnalysisAction;
	private IJavaProject selectedProject;
	private IJavaProject activeProject;
	private IPackageFragmentRoot selectedPackageFragmentRoot;
	private IPackageFragment selectedPackageFragment;
	private ICompilationUnit selectedCompilationUnit;
	private IType selectedType;
	private IMethod selectedMethod;
	private LPLMethodObject[] methodObjectTable;

	private String PLUGIN_ID = "gr.uom.java.jdeodorant";
	
	private class LongParameterListRefactoringButtonUI extends RefactoringButtonUI {

		// To be implemented
		
		@Override
		public void makeRefactoringButtons(int columnIndex) {
			TreeItem[] items = tree.getItems();
			for(int i = 0; i < items.length; i++) {			
				TreeItem item1 = items[i];
				TreeEditor editor = new TreeEditor(item1.getParent());
				Button button = new Button(item1.getParent(), SWT.PUSH);

				button.setText("TEST");
				button.setSize(16, 16);
				button.pack();
				button.setData("index", i);
				
				editor.horizontalAlignment = SWT.RIGHT;
				editor.grabHorizontal = true;
				editor.minimumWidth = 50;
				editor.setEditor(button, item1, columnIndex);
				buttonList.add(button);
				button.addSelectionListener(new SelectionListener() {
					public void widgetSelected(SelectionEvent event) {
						pressRefactorButton((Integer) event.widget.getData("index"));
					}

					public void widgetDefaultSelected(SelectionEvent event) {
					}
				});
				
				button.addPaintListener(new PaintListener() {
					public void paintControl( PaintEvent event ) {
						  event.gc.setBackground( event.display.getSystemColor( SWT.COLOR_WHITE ) );
						  event.gc.fillRectangle( event.x, event.y, event.width, event.height );
						  Image image = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, "/icons/refactoring_button.png").createImage();
						  event.gc.drawImage( image, event.width/2-8, event.height/2-8 );
					}
				});
				
			}
		}
		
		
		
		public void pressRefactorButton(int index) {
			LPLMethodObject methodToRefactor = methodObjectTable[index];
			LPLRefactorWizard wizard = new LPLRefactorWizard(selectedProject, methodToRefactor);
			WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard); dialog.open();
		}
	}

	private LongParameterListRefactoringButtonUI refactorButtonMaker;


	class ViewContentProvider implements ITreeContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			if (methodObjectTable != null) {
				return methodObjectTable;
			} else {
				return new LPLMethodObject[] {};
			}
		}

		public Object[] getChildren(Object arg) {
			return null;
		}

		public Object getParent(Object arg0) {
			return null;
		}

		public boolean hasChildren(Object arg0) {
			return false;
		}
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			if (obj instanceof LPLMethodObject) {
				LPLMethodObject entry = (LPLMethodObject) obj;
				return entry.getColumnText(index);
			}
			return "";
		}

		public Image getColumnImage(Object obj, int index) {
			return null;
		}

		public Image getImage(Object obj) {
			return null;
		}
	}

	private ISelectionListener selectionListener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				Object element = structuredSelection.getFirstElement();
				IJavaProject javaProject = null;
				if (element instanceof IJavaProject) {
					javaProject = (IJavaProject) element;
					selectedPackageFragmentRoot = null;
					selectedPackageFragment = null;
					selectedCompilationUnit = null;
					selectedType = null;
					selectedMethod = null;
				} else if (element instanceof IPackageFragmentRoot) {
					IPackageFragmentRoot packageFragmentRoot = (IPackageFragmentRoot) element;
					javaProject = packageFragmentRoot.getJavaProject();
					selectedPackageFragmentRoot = packageFragmentRoot;
					selectedPackageFragment = null;
					selectedCompilationUnit = null;
					selectedType = null;
					selectedMethod = null;
				} else if (element instanceof IPackageFragment) {
					IPackageFragment packageFragment = (IPackageFragment) element;
					javaProject = packageFragment.getJavaProject();
					selectedPackageFragment = packageFragment;
					selectedPackageFragmentRoot = null;
					selectedCompilationUnit = null;
					selectedType = null;
					selectedMethod = null;
				} else if (element instanceof ICompilationUnit) {
					ICompilationUnit compilationUnit = (ICompilationUnit) element;
					javaProject = compilationUnit.getJavaProject();
					selectedCompilationUnit = compilationUnit;
					selectedPackageFragmentRoot = null;
					selectedPackageFragment = null;
					selectedType = null;
					selectedMethod = null;
				} else if (element instanceof IType) {
					IType type = (IType) element;
					javaProject = type.getJavaProject();
					selectedType = type;
					selectedPackageFragmentRoot = null;
					selectedPackageFragment = null;
					selectedCompilationUnit = null;
					selectedMethod = null;
				} else if (element instanceof IMethod) {
					IMethod method = (IMethod) element;
					javaProject = method.getJavaProject();
					selectedMethod = method;
					selectedPackageFragmentRoot = null;
					selectedPackageFragment = null;
					selectedCompilationUnit = null;
					selectedType = null;
				}
				if (javaProject != null && !javaProject.equals(selectedProject)) {
					selectedProject = javaProject;
					identifyBadSmellsAction.setEnabled(true);
				}
			}
		}
	};

	@Override
	public void createPartControl(Composite parent) {
		refactorButtonMaker = new LongParameterListRefactoringButtonUI();
		treeViewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		treeViewer.setContentProvider(new ViewContentProvider());
		treeViewer.setLabelProvider(new ViewLabelProvider());
		treeViewer.setInput(getViewSite());
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(40, true));
		layout.addColumnData(new ColumnWeightData(30, true));
		layout.addColumnData(new ColumnWeightData(40, true));
		layout.addColumnData(new ColumnWeightData(80, true));
		layout.addColumnData(new ColumnWeightData(20, true));
		layout.addColumnData(new ColumnWeightData(40, true));
		treeViewer.getTree().setLayout(layout);
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.getTree().setLinesVisible(true);
		treeViewer.getTree().setHeaderVisible(true);
		TreeColumn column0 = new TreeColumn(treeViewer.getTree(), SWT.LEFT);
		column0.setText("Refactoring Type");
		column0.setResizable(true);
		column0.pack();
		TreeColumn column1 = new TreeColumn(treeViewer.getTree(), SWT.LEFT);
		column1.setText("Source Method");
		column1.setResizable(true);
		column1.pack();
		TreeColumn column2 = new TreeColumn(treeViewer.getTree(), SWT.LEFT);
		column2.setText("Class");
		column2.setResizable(true);
		column2.pack();
		TreeColumn column3 = new TreeColumn(treeViewer.getTree(), SWT.LEFT);
		column3.setText("Parameter List");
		column3.setResizable(true);
		column3.pack();
		TreeColumn column4 = new TreeColumn(treeViewer.getTree(), SWT.LEFT);
		column4.setText("Number of Parameters");
		column4.setResizable(true);
		column4.pack();
		TreeColumn column5 = new TreeColumn(treeViewer.getTree(), SWT.LEFT);
		column5.setText("Do Refactoring");
		column5.setResizable(true);
		column5.pack();

		treeViewer.expandAll();
		treeViewer.expandAll();

		treeViewer.setColumnProperties(
				new String[] { "refactor type", "method name", "class", "parameter list", "number of parameters" });
		treeViewer.setCellEditors(new CellEditor[] { new TextCellEditor(), new TextCellEditor(), new TextCellEditor(),
				new TextCellEditor(), new TextCellEditor(), new MyComboBoxCellEditor(treeViewer.getTree(),
						new String[] { "0", "1", "2", "3", "4" }, SWT.READ_ONLY) });

		makeActions();
		hookDoubleClickAction();
		contributeToActionBars();
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(selectionListener);
		JavaCore.addElementChangedListener(ElementChangedListener.getInstance());
		getSite().getWorkbenchWindow().getWorkbench().getOperationSupport().getOperationHistory()
				.addOperationHistoryListener(new IOperationHistoryListener() {
					public void historyNotification(OperationHistoryEvent event) {
						int eventType = event.getEventType();
						if (eventType == OperationHistoryEvent.UNDONE || eventType == OperationHistoryEvent.REDONE
								|| eventType == OperationHistoryEvent.OPERATION_ADDED
								|| eventType == OperationHistoryEvent.OPERATION_REMOVED) {
							if (activeProject != null && CompilationUnitCache.getInstance().getAffectedProjects()
									.contains(activeProject)) {
								applyRefactoringAction.setEnabled(false);
								saveResultsAction.setEnabled(false);
								// evolutionAnalysisAction.setEnabled(false);
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
		// manager.add(evolutionAnalysisAction);
	}

	private void makeActions() {
		identifyBadSmellsAction = new Action() {
			public void run() {
				activeProject = selectedProject;
				CompilationUnitCache.getInstance().clearCache();
				methodObjectTable = getTable();
				treeViewer.setContentProvider(new ViewContentProvider());
				applyRefactoringAction.setEnabled(true);
				saveResultsAction.setEnabled(true);

				refactorButtonMaker.disposeButtons();

				Tree tree = treeViewer.getTree();
				refactorButtonMaker.setTree(tree);
				refactorButtonMaker.makeRefactoringButtons(5);

				tree.addListener(SWT.Expand, new Listener() {
					public void handleEvent(Event e) {
						refactorButtonMaker.makeChildrenRefactoringButtons(5);
					}
				});
			}
		};
		ImageDescriptor refactoringButtonImage = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, "/icons/search_button.png");
        identifyBadSmellsAction.setToolTipText("Identify Bad Smells");
        identifyBadSmellsAction.setImageDescriptor(refactoringButtonImage);
        identifyBadSmellsAction.setEnabled(false);

		saveResultsAction = new Action() {
			public void run() {
				saveResults();
			}
		};
		saveResultsAction.setToolTipText("Save Results");
		saveResultsAction.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT));
		saveResultsAction.setEnabled(false);

		applyRefactoringAction = new Action() {
			public void run() {
			}
		};
		applyRefactoringAction.setToolTipText("Apply Refactoring");
		applyRefactoringAction.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_DEF_VIEW));
		applyRefactoringAction.setEnabled(false);

		doubleClickAction = new Action() {
			public void run() {
				try {
					IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
					LPLMethodObject targetSmell = ((LPLMethodObject)selection.getFirstElement());
					IMethod convertedIMethod = targetSmell.toIMethod(selectedProject);
					ISourceRange methodRange = convertedIMethod.getSourceRange();
					if(targetSmell != null && methodRange.getOffset() != -1 && methodRange.getLength() != -1) {
						SystemObject systemObject = ASTReader.getSystemObject();
						if(systemObject != null) {
							IResource checkIfIFile = convertedIMethod.getUnderlyingResource();
							if(checkIfIFile.getType() != IResource.FILE)
				        		return;
							IFile fileWithCodeSmell = (IFile) checkIfIFile;
							IJavaElement sourceJavaElement = JavaCore.create(fileWithCodeSmell);
							try {
								ITextEditor sourceEditor = (ITextEditor) JavaUI.openInEditor(sourceJavaElement);
								AnnotationModel annotationModel = (AnnotationModel) sourceEditor.getDocumentProvider()
										.getAnnotationModel(sourceEditor.getEditorInput());
								Iterator<Annotation> annotationIterator = annotationModel.getAnnotationIterator();
								while (annotationIterator.hasNext()) {
									Annotation currentAnnotation = annotationIterator.next();
	
									annotationModel.removeAnnotation(currentAnnotation);
								}
								Position position = new Position(methodRange.getOffset(), (methodRange.getLength() + 1));
								SliceAnnotation annotation = null;
								annotation = new SliceAnnotation(SliceAnnotation.EXTRACTION, "We detect Long Parameter List!");
								annotationModel.addAnnotation(annotation, position);
						
								sourceEditor.setHighlightRange(position.getOffset(), position.getLength(), true);
	
							} catch (PartInitException e) {
								e.printStackTrace();
							} catch (JavaModelException e) {
								e.printStackTrace();
							}
	
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
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

	private LPLMethodObject[] getTable() {
		LPLMethodObject[] table = null;
		try {
			IWorkbench wb = PlatformUI.getWorkbench();
			IProgressService ps = wb.getProgressService();
			if (ASTReader.getSystemObject() != null && activeProject.equals(ASTReader.getExaminedProject())) {
				new ASTReader(activeProject, ASTReader.getSystemObject(), null);
			} else {
				ps.busyCursorWhile(new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						try {
							new ASTReader(activeProject, monitor);
						} catch (CompilationErrorDetectedException e) {
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									MessageDialog.openInformation(
											PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
											MESSAGE_DIALOG_TITLE,
											"Compilation errors were detected in the project. Fix the errors before using JDeodorant.");
								}
							});
						}
					}
				});
			}

			final SystemObject systemObject = ASTReader.getSystemObject();
			if (systemObject != null) {
				final Set<ClassObject> classObjectsToBeExamined = new LinkedHashSet<ClassObject>();
				final Set<LPLMethodObject> methodObjectsToBeExamined = new LinkedHashSet<LPLMethodObject>();
				if (selectedPackageFragmentRoot != null) {
					System.out.println("In GetTable : classObjectsToBeExamined Prj");
					classObjectsToBeExamined.addAll(systemObject.getClassObjects(selectedPackageFragmentRoot));
				} else if (selectedPackageFragment != null) {
					System.out.println("In GetTable : classObjectsToBeExamined Pac");
					classObjectsToBeExamined.addAll(systemObject.getClassObjects(selectedPackageFragment));
				} else if (selectedCompilationUnit != null) {
					System.out.println("In GetTable : classObjectsToBeExamined Compi");
					classObjectsToBeExamined.addAll(systemObject.getClassObjects(selectedCompilationUnit));
				} else {
					System.out.println("In GetTable : classObjectsToBeExamined else");
					classObjectsToBeExamined.addAll(systemObject.getClassObjects());
				}

				for (ClassObject classObject : classObjectsToBeExamined) {
					if (!classObject.isEnum() && !classObject.isInterface()
							&& !classObject.isGeneratedByParserGenenator()) {
						ListIterator<MethodObject> methodIterator = classObject.getMethodIterator();
						while (methodIterator.hasNext()) {
							MethodObject methodObject = methodIterator.next();
							methodObjectsToBeExamined.add(LPLMethodObject.createLPLMethodObjectFrom(methodObject));
						}
					}
				}

				final List<LPLMethodObject> detectedMethods = new ArrayList<LPLMethodObject>();

				ps.busyCursorWhile(new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						if (!methodObjectsToBeExamined.isEmpty()) {
							int workSize = methodObjectsToBeExamined.size();
							monitor.beginTask("Identification of Extract Method refactoring opportunities", workSize);
							for (LPLMethodObject methodObject : methodObjectsToBeExamined) {
								if (monitor.isCanceled())
									throw new OperationCanceledException();
								if (methodObject.isLongParamterListMethod()) {
									detectedMethods.add(methodObject);
								}
								monitor.worked(1);
							}
						}
						monitor.done();
					}
				});

				table = new LPLMethodObject[detectedMethods.size()];
				for (int i = 0; i < detectedMethods.size(); ++i) {
					table[i] = detectedMethods.get(i);
				}
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (CompilationErrorDetectedException e) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					MESSAGE_DIALOG_TITLE,
					"Compilation errors were detected in the project. Fix the errors before using JDeodorant.");
		}
		return table;
	}

	private void saveResults() {
		FileDialog fd = new FileDialog(getSite().getWorkbenchWindow().getShell(), SWT.SAVE);
		fd.setText("Save Results");
		String[] filterExt = { "*.txt" };
		fd.setFilterExtensions(filterExt);
		String selected = fd.open();
		if (selected != null) {
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(selected));
				Tree tree = treeViewer.getTree();
				for (int i = 0; i < tree.getItemCount(); i++) {
					TreeItem treeItem = tree.getItem(i);
					LPLMethodObject methodObject = (LPLMethodObject) treeItem.getData();
					out.write(methodObject.toString());
					out.newLine();
				}
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
