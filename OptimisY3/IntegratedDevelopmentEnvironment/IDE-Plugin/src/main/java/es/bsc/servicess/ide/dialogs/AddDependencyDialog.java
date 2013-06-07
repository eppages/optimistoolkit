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
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import es.bsc.servicess.ide.Activator;
import es.bsc.servicess.ide.Logger;
import es.bsc.servicess.ide.ProjectMetadata;
import es.bsc.servicess.ide.model.Dependency;

/** Dialog for adding a dependency
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 *
 */
public class AddDependencyDialog extends Dialog {
	private String sType;
	private String path;
	private Combo type;
	private Text pathText;
	private boolean modified;
	private List<String> names;
	
	private static Logger log = Logger.getLogger(AddDependencyDialog.class);

	/**Constructor
	 * @param shell Eclipse runtime shell
	 */
	public AddDependencyDialog(Shell shell) {
		super(shell);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	public Composite createDialogArea(Composite parent) {
		Composite dialog_comp = (Composite) super.createDialogArea(parent);
		dialog_comp.setLayout(new GridLayout(1, false));
		Composite type_composite = new Composite(dialog_comp, SWT.NONE);
		type_composite.setLayout(new GridLayout(2, false));
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		type_composite.setLayoutData(rd);
		Label typeLabel = new Label(type_composite, SWT.NONE);
		typeLabel.setText("Dependency Type");
		type = new Combo(type_composite, SWT.SINGLE | SWT.BORDER);
		type.setItems(ProjectMetadata.DEP_OPTIONS);
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		type.setLayoutData(rd);
		type.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				updateType();
			}
		});

		Composite path_composite = new Composite(dialog_comp, SWT.NONE);
		path_composite.setLayout(new GridLayout(3, false));
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		path_composite.setLayoutData(rd);
		Label pathLabel = new Label(path_composite, SWT.NONE);
		pathLabel.setText("Path");
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		pathText = new Text(path_composite, SWT.SINGLE | SWT.BORDER);
		pathText.setEditable(false);
		pathText.setLayoutData(rd);
		Button select = new Button(path_composite, SWT.NONE);
		select.setText("Select...");
		select.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updatePath();
			}
		});
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		pathText.setLayoutData(rd);
		return dialog_comp;
	}

	/**Update the type of the dependency type.
	 * 
	 */
	protected void updateType() {
		sType = type.getItem(type.getSelectionIndex());

	}

	/**Update the path where the dependency is installed.
	 * 
	 */
	protected void updatePath() {
		if (sType.equalsIgnoreCase(ProjectMetadata.FOLDER_DEP_TYPE)
				|| sType.equalsIgnoreCase(ProjectMetadata.CLASS_FOLDER_DEP_TYPE)) {
			selectFolderLocation();
		} else
			selectFileLocation();

	}

	/** Open a dialog to select the location of a   
	 *  File dependency
	 */
	protected void selectFileLocation() {
		final FileDialog dialog = new FileDialog(this.getShell());
		dialog.setText("Select File");
		if (sType.equalsIgnoreCase(ProjectMetadata.JAR_DEP_TYPE)) {
			String[] filterExt = { "*.jar" };
			dialog.setFilterExtensions(filterExt);
		} else if (sType.equalsIgnoreCase(ProjectMetadata.ZIP_DEP_TYPE)) {
			String[] filterExt = { "*.zip" };
			dialog.setFilterExtensions(filterExt);
		} else if (sType.equalsIgnoreCase(ProjectMetadata.WAR_DEP_TYPE)) {
			String[] filterExt = { "*.war" };
			dialog.setFilterExtensions(filterExt);
		} else {
			String[] filterExt = { "*.*" };
			dialog.setFilterExtensions(filterExt);
		}
		String directoryName = pathText.getText().trim();
		if (directoryName.length() > 0) {
			File fpath = new File(directoryName);
			if (fpath.exists())
				dialog.setFilterPath(directoryName);
		}
		final String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			File fpath = new File(selectedDirectory);
			if (fpath.exists()) {
				pathText.setText(selectedDirectory);
				path = selectedDirectory;
			} else {
				ErrorDialog.openError(getShell(), "Error ",
						"The selected file doesn't exists", new Status(
								IStatus.ERROR, Activator.PLUGIN_ID, "Error file not found"));
			}
		}
	}

	/** Open a dialog to select the location of a  
	 *  Folder dependency
	 */
	protected void selectFolderLocation() {
		final DirectoryDialog dialog = new DirectoryDialog(this.getShell());
		dialog.setMessage("Select Folder");
		String directoryName = pathText.getText().trim();
		if (directoryName.length() > 0) {
			File fpath = new File(directoryName);
			if (fpath.exists() && fpath.isDirectory())
				dialog.setFilterPath(directoryName);
		}
		final String selectedDirectory = dialog.open();
		log.debug(" Selected Folder: " + selectedDirectory);
		if (selectedDirectory != null) {
			File fpath = new File(selectedDirectory);
			if (fpath.exists() && fpath.isDirectory()) {
				pathText.setText(selectedDirectory);
				path = selectedDirectory;
			} else {
				ErrorDialog.openError(getShell(), "Error ",
						"The selected folder is not found", new Status(
								IStatus.ERROR, Activator.PLUGIN_ID ,"Error folder not found"));
			}
		}
	}

	/** Get the selected dependency with the dialog
	 * @return selected dependency
	 */
	public Dependency getDependency() {
		return new Dependency(path, sType, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {

		if (sType != null && path != null && sType.length() > 0
				&& path.length() > 0) {
			super.okPressed();
		} else {
			ErrorDialog.openError(getShell(), "Error",
					"There are fields not filled", new Status(
							IStatus.ERROR, Activator.PLUGIN_ID, "Error missing parameters"));
		}
	}
}
