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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import javax.wsdl.BindingOperation;
import javax.wsdl.Port;
import javax.wsdl.Service;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import com.ibm.wsdl.BindingOperationImpl;
import com.ibm.wsdl.PortImpl;
import com.ibm.wsdl.ServiceImpl;

import es.bsc.servicess.ide.Activator;

public class ServicePartsSelectionDialog extends FilteredItemsSelectionDialog {
	private class ServiceLabelProvider implements ILabelProvider {

		@Override
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub

		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub

		}

		@Override
		public Image getImage(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getText(Object item) {
			if (item instanceof ServiceImpl) {

				return ((Service) item).getQName().toString();
			} else if (item instanceof PortImpl)
				return ((Port) item).getName();
			else if (item instanceof BindingOperationImpl)
				return ((BindingOperation) item).getOperation().getName();
			else {
				System.out.println("Unknown type of item");
				return item.toString();
			}
		}

	}

	private class ResourceSelectionHistory extends SelectionHistory {

		@Override
		protected Object restoreItemFromMemento(IMemento element) {
			return element.getString("resource");
		}

		@Override
		protected void storeItemToMemento(Object item, IMemento element) {
			element.putString("resource", item.toString());

		}
	}

	private static Collection resources = new ArrayList();
	private static final String DIALOG_SETTINGS = "ServiceSelectionDialogSettings";

	public ServicePartsSelectionDialog(Shell shell, Collection c) {
		super(shell, false);
		resources = c;
		setListLabelProvider(new ServiceLabelProvider());
		setDetailsLabelProvider(new ServiceLabelProvider());
		// setSelectionHistory(new ResourceSelectionHistory());

	}

	public void setResources(Collection c) {
		resources = c;
	}

	@Override
	protected Control createExtendedContentArea(Composite parent) {
		return null;
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		IDialogSettings settings = Activator.getDefault().getDialogSettings()
				.getSection(DIALOG_SETTINGS);
		if (settings == null) {
			settings = Activator.getDefault().getDialogSettings()
					.addNewSection(DIALOG_SETTINGS);
		}
		return settings;
	}

	@Override
	protected IStatus validateItem(Object item) {
		return Status.OK_STATUS;
	}

	@Override
	protected ItemsFilter createFilter() {
		return new ItemsFilter() {
			public boolean matchItem(Object item) {
				if (item instanceof ServiceImpl)
					return matches(((Service) item).getQName().toString());
				else if (item instanceof PortImpl)
					return matches(((Port) item).getName());
				else if (item instanceof BindingOperationImpl)
					return matches(((BindingOperation) item).getOperation()
							.getName());
				else {
					System.out.println("Unknown type of item");
					return matches(item.toString());
				}
			}

			public boolean isConsistentItem(Object item) {
				return true;
			}
		};
	}

	@Override
	protected Comparator getItemsComparator() {
		return new Comparator() {
			public int compare(Object item, Object item2) {
				if (item instanceof ServiceImpl)
					return ((Service) item).getQName().toString()
							.compareTo(((Service) item2).getQName().toString());
				else if (item instanceof PortImpl)
					return ((Port) item).getName().compareTo(
							((Port) item2).getName());
				else if (item instanceof BindingOperationImpl)
					return ((BindingOperation) item)
							.getOperation()
							.getName()
							.compareTo(
									((BindingOperation) item2).getOperation()
											.getName());
				else {
					System.out.println("Unknown type of item ("
							+ item.getClass() + ")");

					return item.toString().compareTo(item2.toString());
				}
			}
		};
	}

	@Override
	protected void fillContentProvider(AbstractContentProvider contentProvider,
			ItemsFilter itemsFilter, IProgressMonitor progressMonitor)
			throws CoreException {
		System.out.println("Adding " + resources.size() + " items to the list");
		progressMonitor.beginTask("Searching", resources.size()); //$NON-NLS-1$
		for (Iterator iter = resources.iterator(); iter.hasNext();) {
			Object o = iter.next();
			System.out.println("Item class: " + o.getClass());
			contentProvider.add(o, itemsFilter);
			progressMonitor.worked(1);
		}
		progressMonitor.done();
		System.out.println("Resources added to the list");

	}

	@Override
	public String getElementName(Object item) {
		if (item instanceof ServiceImpl) {

			return ((Service) item).getQName().toString();
		} else if (item instanceof PortImpl)
			return ((Port) item).getName();
		else if (item instanceof BindingOperationImpl)
			return ((BindingOperation) item).getOperation().getName();
		else {
			System.out.println("Unknown type of item");
			return item.toString();
		}
	}

}
