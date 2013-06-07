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

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;
/*import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.dot.DotGraph;
import org.eclipse.zest.internal.dot.DotImport;
*/
import es.bsc.servicess.ide.views.ServiceManagerView.ServiceDataComposite;

public class ServiceGraphView extends ViewPart {

	class MonitorGraphJob extends Job {
		private String serviceID;
		private DeploymentChecker dc;
		private ServiceGraphView smv;
		private boolean toBeStopped = false;

		public MonitorGraphJob(String serviceId, DeploymentChecker dc,
				ServiceGraphView smv) {
			super("get graph for " + serviceId);
			this.serviceID = serviceId;
			this.dc = dc;
			this.smv = smv;
		}

		@Override
		protected IStatus run(IProgressMonitor arg0) {
			String dotGraph = dc.getGraph(serviceID);
			smv.updateGraph(dotGraph);
			if (!toBeStopped)
				schedule(10000);
			return Status.OK_STATUS;
		}

		public void stopJob() {
			toBeStopped = true;
		}

	}

	public static final String ID = "es.bsc.servicess.ide.views.ServiceGraphView";
	private static final RGB BACKGROUND = JFaceResources.getColorRegistry()
			.getRGB("org.eclipse.jdt.ui.JavadocView.backgroundColor");
	private Composite composite;
	//private Graph graph;
	private MonitorGraphJob monitorGraphJob = null;

	public ServiceGraphView() {

	}

	public void createPartControl(Composite parent) {
		composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		composite.setBackground(new Color(composite.getDisplay(), BACKGROUND));
	}

	public void updateGraph(String dotString) {
		/*if (graph != null) {
			graph.dispose();
		}
		if (composite != null) {
			DotImport dotImport = new DotImport(dotString);
			if (dotImport.getErrors().size() > 0) {
				String message = String.format(
						"Could not import DOT: %s, DOT: %s", //$NON-NLS-1$
						dotImport.getErrors(), dotString);
				System.out.println(message);
				return;
			}
			graph = dotImport.newGraphInstance(composite, SWT.NONE);
			setupLayout();
			composite.layout();
			graph.applyLayout();
		}*/
	}

	private void setupLayout() {
		/*if (graph != null) {
			GridData gd = new GridData(GridData.FILL_BOTH);
			graph.setLayout(new GridLayout());
			graph.setLayoutData(gd);
			Color color = new Color(graph.getDisplay(), BACKGROUND);
			graph.setBackground(color);
			graph.getParent().setBackground(color);
		}*/
	}

	public void dispose() {
		monitorGraphJob.stopJob();
		super.dispose();
	}

	public void setFocus() {
	}

	public void openGraph(ServiceDataComposite serviceDataComposite) {
		if (monitorGraphJob != null) {
			monitorGraphJob.stopJob();
		}

	}

}
