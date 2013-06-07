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

package es.bsc.servicess.ide.dialogs;

import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import es.bsc.servicess.ide.Activator;

/** Dialog for deploying a service in a localhost
 * @author jorgee
 *
 */
public class DeployServiceDialog extends Dialog {
	private IJavaProject project;
	private String tomcatLocation;
	private String coresLocation;
	private Text projectText;
	private Text serverText;
	private Text coreFolderText;
	private boolean created;

	public DeployServiceDialog(Shell parentShell) {
		super(parentShell);
		created = false;
	}

	public void setProject(IJavaProject javaProject) {
		project = javaProject;
		if (created) {
			if (project != null)
				projectText.setText(project.getElementName());
			else
				projectText.setText("");
		}

	}

	public Composite createDialogArea(Composite parent) {

		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(3, false));
		Label projectLabel = new Label(composite, SWT.NONE);
		projectLabel.setText("Project");
		projectText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		projectText.setEditable(false);
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 200;
		projectText.setLayoutData(rd);
		Button projectButton = new Button(composite, SWT.NORMAL);
		projectButton.setText("Select");
		projectButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				selectProject();

			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selectProject();

			}

		});

		Label serverLabel = new Label(composite, SWT.NONE);
		serverLabel.setText("Server Folder");
		serverText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 200;
		serverText.setLayoutData(rd);
		Button serverButton = new Button(composite, SWT.NORMAL);
		serverButton.setText("Select");
		serverButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				selectServerLocation();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selectServerLocation();
			}
		});
		Label coreElementsLabel = new Label(composite, SWT.NONE);
		coreElementsLabel.setText("Core Elements Folder");
		coreFolderText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		rd.minimumWidth = 200;
		coreFolderText.setLayoutData(rd);
		Button coreFolderButton = new Button(composite, SWT.NORMAL);
		coreFolderButton.setText("Select");
		coreFolderButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				selectCoreFolder();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selectCoreFolder();
			}
		});
		created = true;
		if (project != null)
			projectText.setText(project.getElementName());
		else
			projectText.setText("");
		return composite;

	}

	protected void selectCoreFolder() {
		final DirectoryDialog dialog = new DirectoryDialog(getShell());
		dialog.setMessage("Select Core Element Folder");
		String directoryName = coreFolderText.getText().trim();
		if (directoryName.length() > 0) {
			final File path = new File(directoryName);
			if (path.exists())
				dialog.setFilterPath(directoryName);
		}
		final String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			coreFolderText.setText(selectedDirectory);
			coresLocation = selectedDirectory;
		}
	}

	protected void selectServerLocation() {
		final DirectoryDialog dialog = new DirectoryDialog(getShell());
		dialog.setMessage("Select Server Installation Folder");
		String directoryName = serverText.getText().trim();
		if (directoryName.length() > 0) {
			final File path = new File(directoryName);
			if (path.exists())
				dialog.setFilterPath(directoryName);
		}
		final String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			serverText.setText(selectedDirectory);
			tomcatLocation = selectedDirectory;
		}
	}

	private void selectProject() {
		IJavaProject[] projects;
		try {
			projects = JavaCore
					.create(ResourcesPlugin.getWorkspace().getRoot())
					.getJavaProjects();

			ElementListSelectionDialog dialog = new ElementListSelectionDialog(
					this.getShell(), new JavaElementLabelProvider(
							JavaElementLabelProvider.SHOW_DEFAULT));
			dialog.setIgnoreCase(false);
			dialog.setTitle("Choose Project");
			dialog.setMessage("Choose Project to deploy");
			dialog.setEmptyListMessage("Empty");
			dialog.setElements(projects);
			dialog.setHelpAvailable(false);
			if (dialog.open() == Window.OK) {
				project = (IJavaProject) dialog.getFirstResult();
				projectText.setText(project.getElementName());
			} else {
				ErrorDialog.openError(this.getShell(), "Error",	"Closing window",
						new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Window does not return when closing"));
			}
		} catch (JavaModelException e) {
			ErrorDialog.openError(this.getShell(), "Error", e.getMessage(), 
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Exception opeing dialog"));
			e.printStackTrace();
		}

	}

	public IJavaProject getProject() {
		return project;
	}

	public String getServerLocation() {
		return tomcatLocation;
	}

	public String getCoreElementsFolder() {
		return coresLocation;
	}

}
