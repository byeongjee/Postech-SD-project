package gr.uom.java.jdeodorant.refactoring.views;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
import java.util.HashMap;
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
import gr.uom.java.ast.MethodInvocationObject;
import gr.uom.java.ast.MethodObject;
import gr.uom.java.ast.SystemObject;
import gr.uom.java.ast.TypeObject;
import gr.uom.java.ast.decomposition.cfg.AbstractVariable;
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
import org.eclipse.jdt.core.dom.MethodInvocation;
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

public class MessageChain extends ViewPart {
	private static final String MESSAGE_DIALOG_TITLE = "Message Chain";
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
	private ASTSliceGroup[] sliceGroupTable;
	// private MethodEvolution methodEvolution;
	/* Mine */
	public MessageChainStructure[] targets;
	private class MessageChainRefactoringButtonUI extends RefactoringButtonUI{
		public void pressRefactoringButton(int index) {
			//gg
		}
	}
	private MessageChainRefactoringButtonUI refactorButtonMaker;
	
	public class ViewContentProvider implements ITreeContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			if(targets!=null) {
				return targets;
			}
			else {
				return new MessageChainStructure[] {};
			}

	         /*if(parent != null && parent instanceof MessageChainStructure) {
	            return (MessageChainStructure[])((MessageChainStructure)parent).getChildren().toArray();
	         }
	         else {
	            return (MessageChainStructure[])new MessageChainStructure[] {};
	         }*/
	      }

	      public Object[] getChildren(Object arg) {
	    	  if(arg != null && arg instanceof MessageChainStructure) {
	    		  List<MessageChainStructure> temp = ((MessageChainStructure)arg).getChildren();
	    		  int len = temp.size();
	    		  MessageChainStructure[] childArray = new MessageChainStructure[len];
	    		  for(int i = 0; i<len; i++) {
	    			  childArray[i] = temp.get(i);
	    		  }
		            return childArray;
		         }
		         else {
		            return (MessageChainStructure[])new MessageChainStructure[] {};
		         }
	      }

	      public Object getParent(Object arg0) {
	         /*if (arg0 instanceof ASTSlice) {
	            ASTSlice slice = (ASTSlice) arg0;
	            for (int i = 0; i < sliceGroupTable.length; i++) {
	               if (sliceGroupTable[i].getCandidates().contains(slice))
	                  return sliceGroupTable[i];
	            }
	         }
	         return null;*/
	         return ((MessageChainStructure)arg0).getParent();
	      }

	      public boolean hasChildren(Object arg0) {
	         return getChildren(arg0).length > 0;
	      }

		
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			System.out.println("I am at Label Provider>>>>>>>>");
			/*switch(index){
			case 0:
				return "aaaa";
			case 1:
				return "ddcd";
			case 2:
				return "efef";
			case 3:
				return "aaaa";
			case 4:
				return "aaaa";
			case 5:
				return "aaaa";
			default:
				return "";
			}*/
			/*if (obj instanceof ASTSlice) {
				ASTSlice entry = (ASTSlice) obj;
				switch (index) {
				case 0:
					return "Extract Method";*/
				/*
				 * case 1: String declaringClass =
				 * entry.getSourceTypeDeclaration().resolveBinding().getQualifiedName(); String
				 * methodName = entry.getSourceMethodDeclaration().resolveBinding().toString();
				 * return declaringClass + "::" + methodName; case 2: return
				 * entry.getLocalVariableCriterion().getName().getIdentifier();
				 */
				/*case 3:
					return "B" + entry.getBoundaryBlock().getId();
				case 4:
					int numberOfSliceStatements = entry.getNumberOfSliceStatements();
					int numberOfDuplicatedStatements = entry.getNumberOfDuplicatedStatements();
					return numberOfDuplicatedStatements + "/" + numberOfSliceStatements;
				case 5:
					Integer userRate = entry.getUserRate();
					return (userRate == null) ? "" : userRate.toString();
				default:
					return "";
				}
			} else if (obj instanceof ASTSliceGroup) {
				ASTSliceGroup entry = (ASTSliceGroup) obj;
				switch (index) {
				case 1:
					String declaringClass = entry.getSourceTypeDeclaration().resolveBinding().getQualifiedName();
					String methodName = entry.getSourceMethodDeclaration().resolveBinding().toString();
					return declaringClass + "::" + methodName;
				case 2:
					return entry.getLocalVariableCriterion().getName().getIdentifier();
				default:
					return "";
				}
			}*/
			if(obj instanceof MessageChainStructure) {
				System.out.println("I am at Label Provider of mc");
				MessageChainStructure entry = (MessageChainStructure)obj;
				switch(index){
				case 0:
					if(entry.getStart() == -1) {
						return "";
					}
					return entry.getStart().toString();
				case 1:
					return entry.getName();
				case 2:
					if(entry.getLength() == -1) {
						return "";
					}
					return String.valueOf(entry.getLength());
				case 3:
					return "aaaa";
				case 4:
					return "aaaa";
				case 5:
					return "aaaa";
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
			/*if (obj1 instanceof ASTSliceGroup && obj2 instanceof ASTSliceGroup) {
				ASTSliceGroup sliceGroup1 = (ASTSliceGroup) obj1;
				ASTSliceGroup sliceGroup2 = (ASTSliceGroup) obj2;
				return sliceGroup1.compareTo(sliceGroup2);
			} else {
				ASTSlice slice1 = (ASTSlice) obj1;
				ASTSlice slice2 = (ASTSlice) obj2;
				// slices belong to the same group
				return Integer.valueOf(slice1.getBoundaryBlock().getId())
						.compareTo(Integer.valueOf(slice2.getBoundaryBlock().getId()));
			}*/
			return 1;
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
					/*
					 * if(sliceTable != null) tableViewer.remove(sliceTable);
					 */
					identifyBadSmellsAction.setEnabled(true);
				}
			}
		}
	};

	@Override
	public void createPartControl(Composite parent) {
		refactorButtonMaker = new MessageChainRefactoringButtonUI();
		treeViewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		treeViewer.setContentProvider(new ViewContentProvider());
		treeViewer.setLabelProvider(new ViewLabelProvider());
		treeViewer.setSorter(new NameSorter());
		treeViewer.setInput(getViewSite());
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(30, true));
		layout.addColumnData(new ColumnWeightData(60, true));
		layout.addColumnData(new ColumnWeightData(40, true));
		layout.addColumnData(new ColumnWeightData(20, true));
		/*layout.addColumnData(new ColumnWeightData(20, true));
		layout.addColumnData(new ColumnWeightData(20, true));*/
		treeViewer.getTree().setLayout(layout);
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.getTree().setLinesVisible(true);
		treeViewer.getTree().setHeaderVisible(true);
		TreeColumn column0 = new TreeColumn(treeViewer.getTree(), SWT.LEFT);
		column0.setText("Start Position");
		column0.setResizable(true);
		column0.pack();
		TreeColumn column1 = new TreeColumn(treeViewer.getTree(), SWT.LEFT);
		column1.setText("Name");
		column1.setResizable(true);
		column1.pack();
		TreeColumn column2 = new TreeColumn(treeViewer.getTree(), SWT.LEFT);
		column2.setText("Length");
		column2.setResizable(true);
		column2.pack();
		TreeColumn column3 = new TreeColumn(treeViewer.getTree(), SWT.LEFT);
		column3.setText("Refactoring");
		column3.setResizable(true);
		column3.pack();
		/*TreeColumn column4 = new TreeColumn(treeViewer.getTree(), SWT.LEFT);
		column4.setText("Duplicated/Extracted");
		column4.setResizable(true);
		column4.pack();

		TreeColumn column5 = new TreeColumn(treeViewer.getTree(), SWT.LEFT);
		column5.setText("Rate it!");
		column5.setResizable(true);
		column5.pack();*/
		treeViewer.expandAll();

		treeViewer.setColumnProperties(
				new String[] { "type", "source", "variable", "block" });
		treeViewer.setCellEditors(new CellEditor[] { new TextCellEditor(), new TextCellEditor(), new TextCellEditor(),
				new TextCellEditor(), new TextCellEditor(), new MyComboBoxCellEditor(treeViewer.getTree(),
						new String[] { "0", "1", "2", "3" }, SWT.READ_ONLY) });

		treeViewer.setCellModifier(new ICellModifier() {
			public boolean canModify(Object element, String property) {
				return property.equals("rate");
			}

			public Object getValue(Object element, String property) {
				if (element instanceof ASTSlice) {
					ASTSlice slice = (ASTSlice) element;
					if (slice.getUserRate() != null)
						return slice.getUserRate();
					else
						return 0;
				}
				if(element instanceof MessageChainStructure) {
				}

				return 0;
			}

			public void modify(Object element, String property, Object value) {
				TreeItem item = (TreeItem) element;
				Object data = item.getData();
				if (data instanceof ASTSlice) {
					ASTSlice slice = (ASTSlice) data;
					slice.setUserRate((Integer) value);
					IPreferenceStore store = Activator.getDefault().getPreferenceStore();
					boolean allowUsageReporting = store.getBoolean(PreferenceConstants.P_ENABLE_USAGE_REPORTING);
					if (allowUsageReporting) {
						Tree tree = treeViewer.getTree();
						int groupPosition = -1;
						int totalGroups = tree.getItemCount();
						for (int i = 0; i < tree.getItemCount(); i++) {
							TreeItem treeItem = tree.getItem(i);
							ASTSliceGroup group = (ASTSliceGroup) treeItem.getData();
							if (group.getCandidates().contains(slice)) {
								groupPosition = i;
								break;
							}
						}
						try {
							boolean allowSourceCodeReporting = store
									.getBoolean(PreferenceConstants.P_ENABLE_SOURCE_CODE_REPORTING);
							String declaringClass = slice.getSourceTypeDeclaration().resolveBinding()
									.getQualifiedName();
							String methodName = slice.getSourceMethodDeclaration().resolveBinding().toString();
							String sourceMethodName = declaringClass + "::" + methodName;
							String content = URLEncoder.encode("project_name", "UTF-8") + "="
									+ URLEncoder.encode(activeProject.getElementName(), "UTF-8");
							content += "&" + URLEncoder.encode("source_method_name", "UTF-8") + "="
									+ URLEncoder.encode(sourceMethodName, "UTF-8");
							content += "&" + URLEncoder.encode("variable_name", "UTF-8") + "=" + URLEncoder
									.encode(slice.getLocalVariableCriterion().resolveBinding().toString(), "UTF-8");
							content += "&" + URLEncoder.encode("block", "UTF-8") + "="
									+ URLEncoder.encode("B" + slice.getBoundaryBlock().getId(), "UTF-8");
							content += "&" + URLEncoder.encode("object_slice", "UTF-8") + "="
									+ URLEncoder.encode(slice.isObjectSlice() ? "1" : "0", "UTF-8");
							int numberOfSliceStatements = slice.getNumberOfSliceStatements();
							int numberOfDuplicatedStatements = slice.getNumberOfDuplicatedStatements();
							content += "&" + URLEncoder.encode("duplicated_statements", "UTF-8") + "="
									+ URLEncoder.encode(String.valueOf(numberOfDuplicatedStatements), "UTF-8");
							content += "&" + URLEncoder.encode("extracted_statements", "UTF-8") + "="
									+ URLEncoder.encode(String.valueOf(numberOfSliceStatements), "UTF-8");
							content += "&" + URLEncoder.encode("ranking_position", "UTF-8") + "="
									+ URLEncoder.encode(String.valueOf(groupPosition), "UTF-8");
							content += "&" + URLEncoder.encode("total_opportunities", "UTF-8") + "="
									+ URLEncoder.encode(String.valueOf(totalGroups), "UTF-8");
							if (allowSourceCodeReporting) {
								content += "&" + URLEncoder.encode("source_method_code", "UTF-8") + "="
										+ URLEncoder.encode(slice.getSourceMethodDeclaration().toString(), "UTF-8");
								content += "&" + URLEncoder.encode("slice_statements", "UTF-8") + "="
										+ URLEncoder.encode(slice.sliceToString(), "UTF-8");
							}
							content += "&" + URLEncoder.encode("rating", "UTF-8") + "="
									+ URLEncoder.encode(String.valueOf(slice.getUserRate()), "UTF-8");
							content += "&" + URLEncoder.encode("username", "UTF-8") + "="
									+ URLEncoder.encode(System.getProperty("user.name"), "UTF-8");
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
				// sliceGroupTable = getTable();
				/* Mine */
				targets = getTable();
				treeViewer.setContentProvider(new ViewContentProvider());
				applyRefactoringAction.setEnabled(true);
				saveResultsAction.setEnabled(true);
				// evolutionAnalysisAction.setEnabled(true);
				
				refactorButtonMaker.disposeButtons();
				
				Tree tree = treeViewer.getTree();
				refactorButtonMaker.setTree(tree);
				refactorButtonMaker.makeRefactoringButtons(3);

				tree.addListener(SWT.Expand, new Listener() {
					public void handleEvent(Event e) {
						refactorButtonMaker.makeChildrenRefactoringButtons(3);
					}
				});
			}
		};
		identifyBadSmellsAction.setToolTipText("Identify Bad Smells");
		identifyBadSmellsAction.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
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

		/*
		 * evolutionAnalysisAction = new Action() { public void run() { methodEvolution
		 * = null; IStructuredSelection selection =
		 * (IStructuredSelection)treeViewer.getSelection();
		 * if(selection.getFirstElement() instanceof ASTSlice) { final ASTSlice slice =
		 * (ASTSlice)selection.getFirstElement(); try { IWorkbench wb =
		 * PlatformUI.getWorkbench(); IProgressService ps = wb.getProgressService();
		 * ps.busyCursorWhile(new IRunnableWithProgress() { public void
		 * run(IProgressMonitor monitor) throws InvocationTargetException,
		 * InterruptedException { ProjectEvolution projectEvolution = new
		 * ProjectEvolution(selectedProject);
		 * if(projectEvolution.getProjectEntries().size() > 1) { methodEvolution = new
		 * MethodEvolution(projectEvolution,
		 * (IMethod)slice.getSourceMethodDeclaration().resolveBinding().getJavaElement()
		 * , monitor); } } }); if(methodEvolution != null) { EvolutionDialog dialog =
		 * new EvolutionDialog(getSite().getWorkbenchWindow(), methodEvolution,
		 * "Method Evolution", false); dialog.open(); } else
		 * MessageDialog.openInformation(getSite().getShell(), "Method Evolution",
		 * "Method evolution analysis cannot be performed, since only a single version of the examined project is loaded in the workspace."
		 * ); } catch (InvocationTargetException e) { e.printStackTrace(); } catch
		 * (InterruptedException e) { e.printStackTrace(); } } } };
		 * evolutionAnalysisAction.setToolTipText("Evolution Analysis");
		 * evolutionAnalysisAction.setImageDescriptor(PlatformUI.getWorkbench().
		 * getSharedImages(). getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT));
		 * evolutionAnalysisAction.setEnabled(false);
		 */

		applyRefactoringAction = new Action() {
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
				if (selection != null && selection.getFirstElement() instanceof ASTSlice) {
					ASTSlice slice = (ASTSlice) selection.getFirstElement();
					TypeDeclaration sourceTypeDeclaration = slice.getSourceTypeDeclaration();
					CompilationUnit sourceCompilationUnit = (CompilationUnit) sourceTypeDeclaration.getRoot();
					IFile sourceFile = slice.getIFile();
					IPreferenceStore store = Activator.getDefault().getPreferenceStore();
					boolean allowUsageReporting = store.getBoolean(PreferenceConstants.P_ENABLE_USAGE_REPORTING);
					if (allowUsageReporting) {
						Tree tree = treeViewer.getTree();
						int groupPosition = -1;
						int totalGroups = tree.getItemCount();
						for (int i = 0; i < tree.getItemCount(); i++) {
							TreeItem treeItem = tree.getItem(i);
							ASTSliceGroup group = (ASTSliceGroup) treeItem.getData();
							if (group.getCandidates().contains(slice)) {
								groupPosition = i;
								break;
							}
						}
						try {
							boolean allowSourceCodeReporting = store
									.getBoolean(PreferenceConstants.P_ENABLE_SOURCE_CODE_REPORTING);
							String declaringClass = slice.getSourceTypeDeclaration().resolveBinding()
									.getQualifiedName();
							String methodName = slice.getSourceMethodDeclaration().resolveBinding().toString();
							String sourceMethodName = declaringClass + "::" + methodName;
							String content = URLEncoder.encode("project_name", "UTF-8") + "="
									+ URLEncoder.encode(activeProject.getElementName(), "UTF-8");
							content += "&" + URLEncoder.encode("source_method_name", "UTF-8") + "="
									+ URLEncoder.encode(sourceMethodName, "UTF-8");
							content += "&" + URLEncoder.encode("variable_name", "UTF-8") + "=" + URLEncoder
									.encode(slice.getLocalVariableCriterion().resolveBinding().toString(), "UTF-8");
							content += "&" + URLEncoder.encode("block", "UTF-8") + "="
									+ URLEncoder.encode("B" + slice.getBoundaryBlock().getId(), "UTF-8");
							content += "&" + URLEncoder.encode("object_slice", "UTF-8") + "="
									+ URLEncoder.encode(slice.isObjectSlice() ? "1" : "0", "UTF-8");
							int numberOfSliceStatements = slice.getNumberOfSliceStatements();
							int numberOfDuplicatedStatements = slice.getNumberOfDuplicatedStatements();
							content += "&" + URLEncoder.encode("duplicated_statements", "UTF-8") + "="
									+ URLEncoder.encode(String.valueOf(numberOfDuplicatedStatements), "UTF-8");
							content += "&" + URLEncoder.encode("extracted_statements", "UTF-8") + "="
									+ URLEncoder.encode(String.valueOf(numberOfSliceStatements), "UTF-8");
							content += "&" + URLEncoder.encode("ranking_position", "UTF-8") + "="
									+ URLEncoder.encode(String.valueOf(groupPosition), "UTF-8");
							content += "&" + URLEncoder.encode("total_opportunities", "UTF-8") + "="
									+ URLEncoder.encode(String.valueOf(totalGroups), "UTF-8");
							if (allowSourceCodeReporting) {
								content += "&" + URLEncoder.encode("source_method_code", "UTF-8") + "="
										+ URLEncoder.encode(slice.getSourceMethodDeclaration().toString(), "UTF-8");
								content += "&" + URLEncoder.encode("slice_statements", "UTF-8") + "="
										+ URLEncoder.encode(slice.sliceToString(), "UTF-8");
							}
							content += "&" + URLEncoder.encode("application", "UTF-8") + "="
									+ URLEncoder.encode(String.valueOf("1"), "UTF-8");
							content += "&" + URLEncoder.encode("application_selected_name", "UTF-8") + "="
									+ URLEncoder.encode(slice.getExtractedMethodName(), "UTF-8");
							content += "&" + URLEncoder.encode("username", "UTF-8") + "="
									+ URLEncoder.encode(System.getProperty("user.name"), "UTF-8");
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
					Refactoring refactoring = new ExtractMethodRefactoring(sourceCompilationUnit, slice);
					try {
						IJavaElement sourceJavaElement = JavaCore.create(sourceFile);
						JavaUI.openInEditor(sourceJavaElement);
					} catch (PartInitException e) {
						e.printStackTrace();
					} catch (JavaModelException e) {
						e.printStackTrace();
					}
					MyRefactoringWizard wizard = new MyRefactoringWizard(refactoring, applyRefactoringAction);
					RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard);
					try {
						String titleForFailedChecks = ""; //$NON-NLS-1$
						op.run(getSite().getShell(), titleForFailedChecks);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		applyRefactoringAction.setToolTipText("Apply Refactoring");
		applyRefactoringAction.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_DEF_VIEW));
		applyRefactoringAction.setEnabled(false);

		doubleClickAction = new Action() {
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
				MessageChainStructure targetSmell = ((MessageChainStructure)selection.getFirstElement());
				
				if(targetSmell != null && targetSmell.getStart() != -1 && targetSmell.getLength() != -1) {
					SystemObject systemObject = ASTReader.getSystemObject();
					if(systemObject != null) {
						ClassObject classwithCodeSmell = systemObject.getClassObject(targetSmell.getParent().getName());
						IFile fileWithCodeSmell = classwithCodeSmell.getIFile();
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
							Position position = new Position(targetSmell.getStart(), (targetSmell.getLength() + 1));
							SliceAnnotation annotation = null;
							annotation = new SliceAnnotation(SliceAnnotation.EXTRACTION, "1");
							
							annotationModel.addAnnotation(annotation, position);
					
							sourceEditor.setHighlightRange(position.getOffset(), position.getLength(), true);

						} catch (PartInitException e) {
							e.printStackTrace();
						} catch (JavaModelException e) {
							e.printStackTrace();
						}
					}
				}				
			}

/*			public void run() {
				IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
				if(selection.getFirstElement() instanceof ASTSlice) {
					ASTSlice slice = (ASTSlice)selection.getFirstElement();
					IFile sourceFile = slice.getIFile();
					try {
						IJavaElement sourceJavaElement = JavaCore.create(sourceFile);
						ITextEditor sourceEditor = (ITextEditor)JavaUI.openInEditor(sourceJavaElement);
						Object[] highlightPositionMaps = slice.getHighlightPositions();
						Map<Position, String> annotationMap = (Map<Position, String>)highlightPositionMaps[0];
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
*/		};
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

	private MessageChainStructure[] getTable() {
		
		Map<String, Map<Integer, List<MethodInvocation>>> table = null;
		List<MessageChainStructure> listTargets = new ArrayList<MessageChainStructure> ();
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
				final Set<AbstractMethodDeclaration> methodObjectsToBeExamined = new LinkedHashSet<AbstractMethodDeclaration>();
				if (selectedPackageFragmentRoot != null) {
					classObjectsToBeExamined.addAll(systemObject.getClassObjects(selectedPackageFragmentRoot));
				} else if (selectedPackageFragment != null) {
					classObjectsToBeExamined.addAll(systemObject.getClassObjects(selectedPackageFragment));
				} else if (selectedCompilationUnit != null) {
					classObjectsToBeExamined.addAll(systemObject.getClassObjects(selectedCompilationUnit));
				} else if (selectedType != null) {
					classObjectsToBeExamined.addAll(systemObject.getClassObjects(selectedType));
				} else if (selectedMethod != null) {
					AbstractMethodDeclaration methodObject = systemObject.getMethodObject(selectedMethod);
					if (methodObject != null) {
						ClassObject declaringClass = systemObject.getClassObject(methodObject.getClassName());
						if (declaringClass != null && !declaringClass.isEnum() && !declaringClass.isInterface()
								&& methodObject.getMethodBody() != null)
							methodObjectsToBeExamined.add(methodObject);
					}
				} else {
					classObjectsToBeExamined.addAll(systemObject.getClassObjects());
				}
				for (ClassObject classObj : classObjectsToBeExamined) {
					for (AbstractMethodDeclaration methodObj : classObj.getMethodList()) {
						if (!methodObjectsToBeExamined.contains(methodObj)) {
							methodObjectsToBeExamined.add(methodObj);
						}
					}
				}
				
				table = processMethod(methodObjectsToBeExamined);
				listTargets = convertMap2MCS(table);
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
		int leng = listTargets.size();
		MessageChainStructure[] arrayTargets = new MessageChainStructure[leng];
		
		for(int i= 0; i <leng ;i++) {
			arrayTargets[i] = listTargets.get(i);
		}
		
		return arrayTargets;
		/*MessageChainStructure parent = new MessageChainStructure("ParentClass");
		MessageChainStructure parentSub = new MessageChainStructure("SubParentClass");
		MessageChainStructure child = new MessageChainStructure(15, parent, "A().B().C()");
		MessageChainStructure child2 = new MessageChainStructure(12, parent, "A().B().C()");
		parentSub.addChild(child);
		parent.addChild(child2);
		parent.addChild(parentSub);
		MessageChainStructure parent2 = new MessageChainStructure("ParentClass2");
		MessageChainStructure child21 = new MessageChainStructure(151, parent, "A().B()");
		MessageChainStructure child22 = new MessageChainStructure(112, parent, "A().B().C()");
		parent2.addChild(child21);
		parent2.addChild(child22);
		MessageChainStructure[] dummy = new MessageChainStructure[2];
		dummy[0] = parent;
		dummy[1] = parent2;*/
		//System.out.println(dummy[0].getName());
	}
	
	

	private Map<String, Map<Integer, List<MethodInvocation>>> processMethod(Set<AbstractMethodDeclaration> methodObjects) {
		Map<String, Map<Integer, List<MethodInvocation>>> store = new HashMap<String, Map<Integer, List<MethodInvocation>>>();
		MethodObject[] dummy = new MethodObject[3];
		//System.out.println("Test at process Method at given objects of " + methodObjects.size());
		for (AbstractMethodDeclaration methodObject : methodObjects) {
			if (methodObject.getMethodBody() != null) {
				List<MethodInvocationObject> test = methodObject.getMethodInvocations();
				
				for(MethodInvocationObject methodInvo : test) {
					MethodInvocation methodInvocation = methodInvo.getMethodInvocation();
					int startPos = methodInvocation.getStartPosition();
					String cls = methodObject.getClassName();
					
					//System.out.println(");
					/*System.out.println("class name : " + cls);
					System.out.println("StartPos   : " + startPos );
					System.out.println("method name: " + methodInvo.getMethodName());*/
					
					if(store.containsKey(cls)) {
						Map<Integer, List<MethodInvocation>> inner = store.get(cls);
						if(inner.containsKey(startPos)) {
							inner.get(startPos).add(methodInvocation);
							//System.out.println("if-if");
						}
						else {
							List<MethodInvocation> temp = new ArrayList<MethodInvocation>();
							temp.add(methodInvocation);
							inner.put(startPos, temp);
							//System.out.println("if-else");
						}
					}
					else {
						List<MethodInvocation> temp = new ArrayList<MethodInvocation>();
						temp.add(methodInvocation);
						Map<Integer, List<MethodInvocation>> tmp = new HashMap<Integer, List<MethodInvocation>>();
						tmp.put(startPos, temp);
						store.put(cls, tmp);
						//System.out.println("else");
					}
				}
				
				List<Integer> deleteList = new ArrayList<Integer>();
				for(String type : store.keySet()) {
					Map<Integer, List<MethodInvocation>> innerMap = store.get(type);
					for(Integer i : innerMap.keySet()) {
						if(innerMap.get(i).size() <= 2) {
							//System.out.println("startPos " + i + " has been deleted since its size is " + innerMap.get(i).size());
							deleteList.add(i);
						}
					}
				}
				
				for(String type : store.keySet()) {
					Map<Integer, List<MethodInvocation>> innerMap = store.get(type);
					for(Integer i: deleteList) {
						innerMap.remove(i);
					}
				}

				/*
				 * for (MethodInvocationObject methodInvo : test) { MethodInvocation
				 * methodInvocation = methodInvo.getMethodInvocation(); int startPos =
				 * methodInvocation.getStartPosition();
				 * 
				 * if (store.containsKey(startPos)) { store.get(startPos).add(methodInvocation);
				 * } else { List<MethodInvocation> temp = new ArrayList<MethodInvocation>();
				 * temp.add(methodInvocation); store.put(startPos, temp); } }
				 * 
				 * List<Integer> deleteList = new ArrayList<Integer>();
				 * System.out.println("Finish Map!!!"); for (Integer i : store.keySet()) { //
				 * System.out.println(i+": "); if (store.get(i).size() <= 2) { //
				 * System.out.println("It is not code smell>>>>>>>"); deleteList.add(i); } }
				 * 
				 * for (Integer del : deleteList) { store.remove(del); } for (Integer i :
				 * store.keySet()) { System.out.println(i + ": "); for (MethodInvocation met :
				 * store.get(i)) { System.out.println(met.getName());
				 * System.out.println(met.arguments().toString());
				 * System.out.println(met.getLength()); //
				 * System.out.println(met.getExpression().toString()); }
				 * 
				 * } }
				 */
				//System.out.println("Finish Print>>>>>>>>>>>!!!");
			}
		}
		/*for(String type: store.keySet()) {
			System.out.println("Code Smell under ==> " + type + " total of " + store.get(type).size());
			Map<Integer, List<MethodInvocation>> innerMap = store.get(type);
			for(Integer i: innerMap.keySet()) {
				for(MethodInvocation met : innerMap.get(i)) {
					System.out.println("	Method name : " + met.getName());
					System.out.println("	Method args : " + met.arguments().toString());
					System.out.println("	Method leng : " + met.getLength());
				}
			}
		}*/
		return store;
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
				/*
				 * TreeColumn[] columns = tree.getColumns(); for(int i=0; i<columns.length; i++)
				 * { if(i == columns.length-1) out.write(columns[i].getText()); else
				 * out.write(columns[i].getText() + "\t"); } out.newLine();
				 */
				for (int i = 0; i < tree.getItemCount(); i++) {
					TreeItem treeItem = tree.getItem(i);
					ASTSliceGroup group = (ASTSliceGroup) treeItem.getData();
					for (ASTSlice candidate : group.getCandidates()) {
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
	private List<MessageChainStructure> convertMap2MCS(Map<String, Map<Integer, List<MethodInvocation>>> arg){
		List<MessageChainStructure> ret = new ArrayList<MessageChainStructure>();
		for(String className : arg.keySet()) {
			Map<Integer, List<MethodInvocation>> innerMap = arg.get(className);
			MessageChainStructure cls = new MessageChainStructure(className);
			ret.add(cls);
			for(Integer i : innerMap.keySet()) {
				String methodName = "";
				int codeLength = -1;
				for(MethodInvocation methodinvocation : innerMap.get(i)) {
					methodName += methodinvocation.getName().toString() + "->";
					codeLength = codeLength > methodinvocation.getLength() ? codeLength : methodinvocation.getLength();
					System.out.println(codeLength);
				}
				methodName = methodName.substring(0, methodName.length()-2);
				MessageChainStructure method = new MessageChainStructure(i, cls, methodName,codeLength);
				cls.addChild(method);
				
			}
		}
		return ret;
	}
}
