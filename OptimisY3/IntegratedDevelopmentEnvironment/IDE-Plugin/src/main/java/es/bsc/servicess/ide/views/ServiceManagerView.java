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

package es.bsc.servicess.ide.views;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.*;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;

import es.bsc.servicess.ide.KeyValueTableComposite;
import es.bsc.servicess.ide.editors.deployers.TRECComposite;
import es.bsc.servicess.ide.editors.deployers.TRECValues;
import es.bsc.servicess.ide.views.ServiceManagerView.ServiceDataComposite;

public class ServiceManagerView extends ViewPart {

	class ServiceDataComposite {
		private Label status;
		private Label serviceID;
		private Button graphBt;
		private Button undeplBt;
		private Combo providers;
		private KeyValueTableComposite instances;
		private Map<String, Map<String, String>> machinesInIPs;
		private DeploymentChecker dc;
		private boolean diposed = false;
		private Button keepDataBt;
		private TRECComposite otherValues;
		private Map otherValuesMap;
		private Composite serviceComposite;

		public ServiceDataComposite(String serviceID, DeploymentChecker dc,
				String status, Composite parent) {
			this.dc = dc;
			machinesInIPs = new HashMap<String, Map<String, String>>();
			serviceComposite = new Composite(parent, SWT.BORDER);
			serviceComposite.setLayout(new GridLayout(2, false));
			GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			rd.grabExcessHorizontalSpace = true;
			serviceComposite.setLayoutData(rd);
			Composite compo = new Composite(serviceComposite, SWT.NONE);
			compo.setLayout(new GridLayout(2, false));
			rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			rd.grabExcessHorizontalSpace = true;
			compo.setLayoutData(rd);
			Composite c_id = new Composite(compo, SWT.NONE);
			c_id.setLayout(new GridLayout(2, false));
			rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			rd.grabExcessHorizontalSpace = true;
			c_id.setLayoutData(rd);
			Label sID_title = new Label(c_id, SWT.NONE | SWT.BOLD);
			sID_title.setText("Service ID: ");
			this.serviceID = new Label(c_id, SWT.NONE);
			this.serviceID.setText(serviceID);
			rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			rd.grabExcessHorizontalSpace = true;
			this.serviceID.setLayoutData(rd);
			Composite c_st = new Composite(compo, SWT.NONE);
			c_st.setLayout(new GridLayout(3, false));
			rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			rd.grabExcessHorizontalSpace = true;
			c_st.setLayoutData(rd);
			Label sSt_title = new Label(c_st, SWT.NONE | SWT.BOLD);
			sSt_title.setText(" Status:  ");
			this.status = new Label(c_st, SWT.NONE);
			this.status.setText(status);
			rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			rd.grabExcessHorizontalSpace = true;
			this.status.setLayoutData(rd);
			Composite c_ips = new Composite(compo, SWT.NONE);
			c_ips.setLayout(new GridLayout(2, false));
			rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.VERTICAL_ALIGN_BEGINNING);
			rd.grabExcessHorizontalSpace = true;
			c_ips.setLayoutData(rd);
			Label sPr_title = new Label(c_ips, SWT.NONE | SWT.BOLD);
			sPr_title.setText("Provider: ");
			this.providers = new Combo(c_ips, SWT.NONE);
			this.providers.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					loadVMs(providers.getSelectionIndex());
					loadOtherValues(providers.getSelectionIndex());
				}
			});
			rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			rd.grabExcessHorizontalSpace = true;
			this.providers.setLayoutData(rd);
			Composite combInst = new Composite(compo, SWT.NONE);
			combInst.setLayout(new GridLayout(2, false)); 
			rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			rd.grabExcessHorizontalSpace = true;
			combInst.setLayoutData(rd);
			Label sIns_title = new Label(combInst, SWT.NONE | SWT.BOLD);
			sIns_title.setText(" Instances:  ");
			rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.VERTICAL_ALIGN_BEGINNING);
			sIns_title.setLayoutData(rd);
			this.instances = new KeyValueTableComposite(getSite().getShell(),
					null, "Address", "Type", false, false);
			this.instances.createComposite(combInst);
			//TODO currently TREC Values
			Label sOther_title = new Label(combInst, SWT.NONE | SWT.BOLD);
			sOther_title.setText(" TREC:  ");
			this.otherValues = new TRECComposite(getSite().getShell());
			this.otherValues.createComposite(combInst);
			
			Composite combt = new Composite(c_st, SWT.NONE);
			combt.setLayout(new GridLayout(2, false)); 
			rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.VERTICAL_ALIGN_BEGINNING);
			combt.setLayoutData(rd);
			undeplBt = new Button(combt,SWT.NONE);
			undeplBt.setText("Undeploy Service");
			undeplBt.addSelectionListener(new SelectionListener(){ 
				 @Override public void widgetDefaultSelected(SelectionEvent arg0){ 
					 undeployService(); }
				 @Override public void widgetSelected(SelectionEvent arg0) {
					 undeployService(); } 
			});
			undeplBt.setEnabled(false);
			keepDataBt = new Button(combt,SWT.CHECK);
			keepDataBt.setText("Keep Service Data");
			keepDataBt.setEnabled(false);
			if (status.equals(DeploymentChecker.DEPLOYED)){
					undeplBt.setEnabled(true);
					keepDataBt.setEnabled(true);
			}
			
			 

		}

		protected void loadVMs(int selectionIndex) {
			String provider = this.providers.getItem(selectionIndex);
			instances.setKeyValueMap(machinesInIPs.get(provider));

		}
		
		protected void loadOtherValues(int selectionIndex) {
			String provider = this.providers.getItem(selectionIndex);
			this.otherValues.setValues(this.otherValuesMap.get(provider));

		}

		protected void undeployService() {
			if (dc!=null && this.serviceID.getText().trim()!=null && keepDataBt!=null){
				dc.undeploy(this.serviceID.getText().trim(), keepDataBt.getSelection());
				this.serviceComposite.dispose();
				composite.layout(true);
				this.diposed = true;
			}else
				System.out.print("DC is null");
		}

		protected void openGraph() {
			try {
				ServiceGraphView sgView = (ServiceGraphView) PlatformUI
						.getWorkbench()
						.getActiveWorkbenchWindow()
						.getActivePage()
						.showView("es.bsc.servicess.ide.views.ServiceGraphView");
				sgView.openGraph(map.get(serviceID));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private String getGraphContent() throws IOException {
			BufferedReader bufread = new BufferedReader(new FileReader(
					"/home/jorgee/graph.dot"));
			String graph = new String();
			String line = bufread.readLine();

			while (line != null) {
				graph = graph + line;
				line = bufread.readLine();
			}
			return graph;
		}

		public void updateStatus(String status) {
			this.status.setText(status);
			
			if (status.equals(DeploymentChecker.DEPLOYED)){
				undeplBt.setEnabled(true);
				keepDataBt.setEnabled(true);
			}else{
				undeplBt.setEnabled(false);
				keepDataBt.setEnabled(false);
			}
			 
		}

		public void updateProvider(String providerName,
				Map<String, String> vmsmap) {
			if (!providerExists(providerName))
				this.providers.add(providerName);
			this.machinesInIPs.put(providerName, vmsmap);
		}

		private boolean providerExists(String providerName) {
			String[] provs = this.providers.getItems();
			for (String prov : provs) {
				if (prov.equals(providerName))
					return true;
			}
			return false;
		}

		public void setMachines(Map<String, Map<String, String>> machinesInIPs2) {
			String currentIP = this.providers.getText().trim();
			this.machinesInIPs = machinesInIPs2;
			this.providers.setItems(machinesInIPs2.keySet().toArray(
					new String[machinesInIPs2.size()]));
			if (currentIP != null && currentIP.length() > 0) {
				Map<String, String> vms = this.machinesInIPs.get(currentIP);
				this.instances.setKeyValueMap(vms);
			}

		}

		public void setOtherValues(Map otherValues) {
			String currentIP = this.providers.getText().trim();
			this.otherValuesMap = otherValues;
			if (currentIP != null && currentIP.length() > 0) {
				Object o = this.otherValuesMap.get(currentIP);
				this.otherValues.setValues((TRECValues) o);
			}
			
			
		}

	}

	public class ServiceStatusCheckJob extends Job {
		private String serviceID;
		private ServiceDataComposite comp;
		private DeploymentChecker dc;
		private ServiceManagerView smv;
		private boolean toBeStopped;

		public ServiceStatusCheckJob(String serviceId, DeploymentChecker dc,
				ServiceDataComposite comp, ServiceManagerView smv) {
			super("Check status of " + serviceId);
			this.serviceID = serviceId;
			this.dc = dc;
			this.smv = smv;
			this.comp = comp;
		}

		@Override
		protected IStatus run(IProgressMonitor arg0) {
			final String status = dc.getStatus(serviceID);
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					comp.updateStatus(status);
				}
			});
			if (!comp.diposed
					&& (status.equalsIgnoreCase(DeploymentChecker.PENDING) || status
							.equalsIgnoreCase(DeploymentChecker.DEPLOYED))) {
				if (status.equalsIgnoreCase(DeploymentChecker.DEPLOYED)) {
					System.out.println("Service " + serviceID + " is Deployed");
					final Map<String, Map<String, String>> machinesInIPs = dc
							.getMachines(serviceID);
					if (machinesInIPs != null)
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								comp.setMachines(machinesInIPs);
							}
						});
					final Map<String, Object> otherValues = dc
							.getOtherValues(serviceID);
					if (otherValues != null)
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								comp.setOtherValues(otherValues);
							}
						});

				} else {
					System.out.println("Scheduling job for 10 secs");
					schedule(10000);
				}
			} else
				System.out.println("Service status is" + status);
			return Status.OK_STATUS;
		}

	}

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "es.bsc.servicess.ide.views.ServiceManagerView";

	private Composite composite;
	private HashMap<String, ServiceDataComposite> map;

	/**
	 * The constructor.
	 */
	public ServiceManagerView() {
		map = new HashMap<String, ServiceDataComposite>();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {

	}

	public void addNewDeployedService(String serviceID, DeploymentChecker dc,
			String status) {
		ServiceDataComposite sdcom = new ServiceDataComposite(serviceID, dc,
				status, composite);
		map.put(serviceID, sdcom);
		Job checkJob = new ServiceStatusCheckJob(serviceID, dc, sdcom, this);
		checkJob.schedule();
		composite.layout();
		composite.redraw();

	}

	@Override
	public void dispose() {
		for (ServiceDataComposite sdcom : map.values()) {
			sdcom.diposed = true;
		}
		super.dispose();
	}
	 
}