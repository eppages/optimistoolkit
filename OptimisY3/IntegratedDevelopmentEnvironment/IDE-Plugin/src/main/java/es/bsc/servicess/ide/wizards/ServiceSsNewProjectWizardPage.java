/*
 *  Copyright 2011-2012 Barcelona Supercomputing Center (www.bsc.es)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package es.bsc.servicess.ide.wizards;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;

import es.bsc.servicess.ide.Checker;
import es.bsc.servicess.ide.TitlesAndConstants;

/**
 * The "New Project" wizard page allows to create a container providing name, main package, and Runtime location.
 * 
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 */
@SuppressWarnings("restriction")
public class ServiceSsNewProjectWizardPage extends WizardPage{// extends NewJavaProjectWizardPageOne {

	private final class NameGroup extends Observable implements IDialogFieldListener {

        protected final StringDialogField fNameField;

        public NameGroup() {
            // text field for project name
            fNameField= new StringDialogField();
            fNameField.setLabelText(NewWizardMessages.NewJavaProjectWizardPageOne_NameGroup_label_text);
            fNameField.setDialogFieldListener(this);
        }

        public Control createControl(Composite composite) {
            Composite nameComposite= new Composite(composite, SWT.NONE);
            nameComposite.setFont(composite.getFont());
            nameComposite.setLayout(new GridLayout(2, false));

            fNameField.doFillIntoGrid(nameComposite, 2);
            LayoutUtil.setHorizontalGrabbing(fNameField.getTextControl(null));

            return nameComposite;
        }

        protected void fireEvent() {
            setChanged();
            ((NewJavaProjectWizardPageOne)getNextPage()).setProjectName(fNameField.getText().trim());
            notifyObservers();
        }

        public String getName() {
            return fNameField.getText().trim();
        }

        public void postSetFocus() {
            fNameField.postSetFocusOnDialogField(getShell().getDisplay());
        }

        public void setName(String name) {
            fNameField.setText(name);
        }

        /* (non-Javadoc)
         * @see org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener#dialogFieldChanged(org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField)
         */
        public void dialogFieldChanged(DialogField field) {
            fireEvent();
        }
    }
	
	private final class RuntimeLocationGroup extends Observable implements
			Observer, IStringButtonAdapter, IDialogFieldListener {

		protected final StringButtonDialogField fLocation;

		public RuntimeLocationGroup(){ 
			fLocation = new StringButtonDialogField(this);
			fLocation.setDialogFieldListener(this);
			fLocation.setLabelText("Runtime Location");
			fLocation.setButtonLabel("Browse...");
		}

		public Control createControl(Composite composite) {
			final int numColumns = 3;

			final Group group = new Group(composite, SWT.NONE);
			group.setLayout(initGridLayout(new GridLayout(numColumns, false),
					true));
			group.setText("Runtime Location");

			fLocation.doFillIntoGrid(group, numColumns);
			LayoutUtil.setHorizontalGrabbing(fLocation.getTextControl(null));

			return group;
		}

		protected void fireEvent() {
			setChanged();
			notifyObservers();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 * java.lang.Object)
		 */
		public void update(Observable o, Object arg) {
			fireEvent();
		}

		public IPath getLocation() {
			return Path.fromOSString(fLocation.getText().trim());
		}

		public void setLocation(IPath path) {
			if (path != null) {
				fLocation.setText(path.toOSString());
				
			}
			fireEvent();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter
		 * #
		 * changeControlPressed(org.eclipse.jdt.internal.ui.wizards.dialogfields
		 * .DialogField)
		 */
		public void changeControlPressed(DialogField field) {
			final DirectoryDialog dialog = new DirectoryDialog(getShell());
			dialog.setMessage(NewWizardMessages.NewJavaProjectWizardPageOne_directory_message);
			String directoryName = fLocation.getText().trim();

			if (directoryName.length() > 0) {
				final File path = new File(directoryName);
				if (path.exists())
					dialog.setFilterPath(directoryName);
			}
			final String selectedDirectory = dialog.open();
			if (selectedDirectory != null) {
				fLocation.setText(selectedDirectory);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener
		 * #dialogFieldChanged(org.eclipse.jdt.internal.ui.wizards.dialogfields.
		 * DialogField)
		 */
		public void dialogFieldChanged(DialogField field) {
			fireEvent();
		}
	}

	private final class PackageGroup extends Observable implements
			IDialogFieldListener {

		protected final StringDialogField fNameField;

		public PackageGroup() {
			// text field for project name
			fNameField = new StringDialogField();
			fNameField.setLabelText("Main Package");
			fNameField.setDialogFieldListener(this);
		}

		public Control createControl(Composite composite) {
			Composite nameComposite = new Composite(composite, SWT.NONE);
			nameComposite.setFont(composite.getFont());
			nameComposite.setLayout(new GridLayout(3, false));
			nameComposite.setLayoutData(new GridData(
					GridData.HORIZONTAL_ALIGN_FILL));
			fNameField.doFillIntoGrid(nameComposite, 3);
			LayoutUtil.setHorizontalGrabbing(fNameField.getTextControl(null));

			return nameComposite;
		}

		protected void fireEvent() {
			setChanged();
			notifyObservers();
		}

		public String getName() {
			return fNameField.getText().trim();
		}

		public void postSetFocus() {
			fNameField.postSetFocusOnDialogField(getShell().getDisplay());
		}

		public void setName(String name) {
			fNameField.setText(name);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener
		 * #dialogFieldChanged(org.eclipse.jdt.internal.ui.wizards.dialogfields.
		 * DialogField)
		 */
		public void dialogFieldChanged(DialogField field) {
			fireEvent();
		}
	}

    /**
     * Validate this page and show appropriate warnings and error NewWizardMessages.
     */
    private final class Validator implements Observer {

        public void update(Observable o, Object arg) {

            final IWorkspace workspace= JavaPlugin.getWorkspace();

            final String name= fNameGroup.getName();
            final String packageName = fPackageGroup.getName();
            final String runtimeLocation = fRuntimeGroup.getLocation().toOSString();
            

            // check whether the project name field is empty
            if (name.length() == 0) {
                setErrorMessage(null);
                setMessage(NewWizardMessages.NewJavaProjectWizardPageOne_Message_enterProjectName);
                setPageComplete(false);
                return;
            }

            // check whether the project name is valid
            final IStatus nameStatus= workspace.validateName(name, IResource.PROJECT);
            if (!nameStatus.isOK()) {
                setErrorMessage(nameStatus.getMessage());
                setPageComplete(false);
                return;
            }
            
            final IStatus nameValidStatus= Checker.validateProjectName(name);
            if (!nameValidStatus.isOK()) {
                setErrorMessage(nameValidStatus.getMessage());
                setPageComplete(false);
                return;
            }
            
            // check whether project already exists
            final IProject handle= workspace.getRoot().getProject(name);
            if (handle.exists()) {
                setErrorMessage(NewWizardMessages.NewJavaProjectWizardPageOne_Message_projectAlreadyExists);
                setPageComplete(false);
                return;
            }

            IPath projectLocation= ResourcesPlugin.getWorkspace().getRoot().getLocation().append(name);
            if (projectLocation.toFile().exists()) {
                try {
                    //correct casing
                    String canonicalPath= projectLocation.toFile().getCanonicalPath();
                    projectLocation= new Path(canonicalPath);
                } catch (IOException e) {
                    JavaPlugin.log(e);
                }

            }
            
            if (packageName.length() == 0) {
                setErrorMessage(null);
                setMessage("Enter project main package");
                setPageComplete(false);
                return;
            }
            
            final IStatus packNameStatus= Checker.validatePackageName(packageName);
            if (!packNameStatus.isOK()) {
                setErrorMessage(nameValidStatus.getMessage());
                setPageComplete(false);
                return;
            }
            
            if (runtimeLocation.length() == 0) {
                setErrorMessage(null);
                setMessage("Enter runtime location");
                setPageComplete(false);
                return;
            }
            
            final IStatus runtimeStatus= Checker.validateRuntimeLocation(runtimeLocation);
            if (!runtimeStatus.isOK()) {
                setErrorMessage(nameValidStatus.getMessage());
                setPageComplete(false);
                return;
            }

            setPageComplete(true);

            setErrorMessage(null);
            setMessage(null);
        }

        private boolean canCreate(File file) {
            while (!file.exists()) {
                file= file.getParentFile();
                if (file == null)
                    return false;
            }

            return file.canWrite();
        }
    }
	
    private final NameGroup fNameGroup;
    private final PackageGroup fPackageGroup;
	private final RuntimeLocationGroup fRuntimeGroup;
	private final Validator fValidator;
	/**
	 * Wizard Page Constructor.
	 * 
	 */
	public ServiceSsNewProjectWizardPage() {
		super("NewServicessProjectWizardPage");
		setTitle(TitlesAndConstants.getNewProjectPageWizardTitle());
		setDescription(TitlesAndConstants.getNewProjectWizardDescription());
		fNameGroup = new NameGroup();
		fPackageGroup = new PackageGroup();
		fRuntimeGroup = new RuntimeLocationGroup();
		fValidator = new Validator();
		fNameGroup.addObserver(fValidator);
		fPackageGroup.addObserver(fValidator);
		fRuntimeGroup.addObserver(fValidator);
		
		
		
		// initialize defaults
		setPackageName(""); //$NON-NLS-1$
	}

	protected Control createNameControl(Composite composite) {
		return fNameGroup.createControl(composite);
	}
	
	protected Control createPackageControl(Composite composite) {
		return fPackageGroup.createControl(composite);
	}

	protected Control createRuntimeLocationControl(Composite composite) {
		return fRuntimeGroup.createControl(composite);
	}

	public void setProjectName(String name){
		if (name == null)
			throw new IllegalArgumentException();
			fNameGroup.setName(name);
	}
	
	public void setPackageName(String name) {
		if (name == null)
			throw new IllegalArgumentException();
			fPackageGroup.setName(name);
			
	}

	public String getPackageName() {
		return fPackageGroup.getName();
	}

	public String getRuntimeLocation() {
		return fRuntimeGroup.getLocation().toOSString().trim();
	}
	
	public void setRuntimeLocation(String name) {
		if (name == null)
			throw new IllegalArgumentException();

		fRuntimeGroup.setLocation(new Path(name));

	}
	
	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {

		initializeDialogUnits(parent);

		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayout(initGridLayout(new GridLayout(1, false), true));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		// create UI elements
		Control nameControl = createNameControl(composite);
		nameControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Control packageControl = createPackageControl(composite);
		nameControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Control runtimeLocationControl = createRuntimeLocationControl(composite);
		runtimeLocationControl.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));

		setControl(composite);
	}

	private GridLayout initGridLayout(GridLayout layout, boolean margins) {
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		if (margins) {
			layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		} else {
			layout.marginWidth = 0;
			layout.marginHeight = 0;
		}
		return layout;
	}

}