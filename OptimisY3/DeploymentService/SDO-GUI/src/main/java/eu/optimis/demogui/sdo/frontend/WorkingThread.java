/*
 Copyright (C) 2012-2013 Umeå University

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package eu.optimis.demogui.sdo.frontend;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.lang.Threads;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Progressmeter;

import eu.optimis._do.schemas.Objective;
import eu.optimis._do.schemas.Placement;
import eu.optimis._do.schemas.PlacementSolution;
import eu.optimis.demogui.sdo.backend.ServiceDeployerThread;
import eu.optimis.ipdiscovery.datamodel.Provider;
import eu.optimis.sd.schemas.st.CompletedStatus;
import eu.optimis.sd.schemas.st.ErrorStatus;
import eu.optimis.sd.schemas.st.NormalStatus;
import eu.optimis.sd.schemas.st.Status;
import eu.optimis.sd.util.SDConfigurationKeys;
import eu.optimis.sd.util.config.Configuration;
import eu.optimis.sd.util.config.ConfigurationFactory;

/**
 * @author Wubin.Li (Viali)
 * @author Petter Svärd
 * 
 */
public class WorkingThread extends Thread
{
	private static final Logger logger = Logger.getLogger(WorkingThread.class);

	private ServiceDeployerThread sdThread;
	private Desktop desktop;
	private Listbox stListbox;
	private Listbox trecListbox;
	
	private Component bottom;
	
	private String sdo_config_path;
	private File manifestfile;
	private Objective objective;

	private boolean keepUpdating;
	
	public WorkingThread(String sdo_config_path, File manifestfile,
			Objective objective, Listbox _statusList, Listbox _trecList, Component bottom)
	{
		this.sdo_config_path = sdo_config_path;
		this.manifestfile = manifestfile;
		this.objective = objective;
		
		this.stListbox = _statusList;
		this.trecListbox = _trecList;
		
		this.desktop = stListbox.getDesktop();
		
		this.keepUpdating =true;
		
		this.bottom = bottom;
	}

	@Override
	public void run()
	{
		if (desktop.isServerPushEnabled() == false)
			desktop.enableServerPush(true);

		logger.info("active WorkingThread : " + this.getName());
		try
		{
			// Start the deployment thread..
			sdThread = new ServiceDeployerThread(this.sdo_config_path, this.manifestfile, this.objective);
			sdThread.start();

			//while (sdThread.getState() != Thread.State.TERMINATED && this.keepUpdating)
			while (this.keepUpdating)
			{
				Executions.activate(desktop);
				//Clients.scrollIntoView(this.bottom);
				try
				{
					this.updateUI();
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					Messagebox.show(ex.getMessage(), "Error!", Messagebox.OK,
							Messagebox.ERROR);
					break;
				}
				finally
				{
					Executions.deactivate(desktop);
				}
				Threads.sleep(50); // Update every 0.05 seconds
			}
		}
		catch (InterruptedException ex)
		{
			logger.info("The server push thread interrupted", ex);
			ex.printStackTrace();
		}
		finally
		{
			if (desktop.isServerPushEnabled())
				desktop.enableServerPush(false);
			Executions.deactivate(desktop);
		}
		logger.info("The WorkingThread  ceased: " + this.getName());
	}

	private void updateUI() throws InterruptedException
	{
		List<Status> sts = sdThread.getDeploymentStatusList();
		//IMPORTANT to avoid java.util.concurrentmodificationexception.
		for (int i = 0; i < sts.size(); i++)
		{
			Status status = sts.get(i);
			//DONE!
			if (status.isCompletedType())
			{
				//Display TREC link layout
				try
				{
					this.updateTRECLinkLayout();
				}
				catch (Exception e)
				{
					e.printStackTrace();
					String msg = "Failed to update TREC Link layout: " + e.getMessage();
					throw new InterruptedException(msg);
				}
				
				//SUCCESS
				Messagebox.show(((CompletedStatus) status).getMessage(),
						"Congratulations!", Messagebox.OK, Messagebox.ON_OK);
				this.keepUpdating = false;
				//Clients.scrollIntoView(this.bottom);
			}
			//ERROR!
			else if (status.isErrorType())
			{
				logger.debug("Error Status Received!");
				
				ErrorStatus st = (ErrorStatus) status;
				this.keepUpdating = false;
				throw new InterruptedException(st.getComponent() + "/"
						+ st.getOperation() + ": " + st.getErrorMessage());
			}
			//NORMAL->CONTINUE!
			else if (status.isNormalType())
			{
				this.updateNormalStatusToUI((NormalStatus) status);
			}
			//ELSE!
			else
			{
				throw new InterruptedException(
						"Unknown Status Type Retrieved from the Service Deployer");
			}
			Clients.scrollIntoView(this.bottom);
		}
	}

	private String constructTREClink(String ipId, String serviceId)
	{
		Configuration config = ConfigurationFactory.getConfig(this.sdo_config_path);
		String trecLink = config.getString(SDConfigurationKeys.TREC_LINK_URL);
		String url = trecLink + "?" + "side=sp&stage=deployment&level=service&identifier="
				+ serviceId + "&providerId=" + ipId;
		return url;
	}

	private void updateTRECLinkLayout() throws Exception
	{
		PlacementSolution ps = this.sdThread.readPlacementResult();
		List<Placement> pList = ps.getPlacementList();
		for (Placement placement : pList)
		{
			Provider ip = placement.getProvider();
			String ipId = ip.getIdentifier();
			String serviceId = placement.getServiceId();

			Listitem item = new Listitem();
			Listcell cellIP = new Listcell(ipId);
			cellIP.setParent(item);

			Image image = new Image();
			image.setSrc("images/optimis_logo.gif");
			image.setAttribute("alt", "Click");
			
			String url = this.constructTREClink(ipId, serviceId);
			A a = new A();
			a.setHref(url);
			a.setTarget("_blank");
			
			image.setParent(a);
			
			Listcell cellTRECLink = new Listcell();
			a.setParent(cellTRECLink);
			
			cellTRECLink.setParent(item);
			
			this.trecListbox.appendChild(item);
			//Clients.scrollIntoView(item);
		}
	}
	
	private void updateNormalStatusToUI(NormalStatus status)
	{
		@SuppressWarnings("unchecked")
		List<Listitem> items = stListbox.getItems();
		for (Listitem listitem : items)
		{
			if (listitem.getId().equalsIgnoreCase(status.getComponent()))
			{
				this.updateGroup(listitem, status);
				return;
			}
		}
		this.addGroup(status);
	}

	// update a group..
	private void updateGroup(Listitem iGroup, NormalStatus status)
	{
		List<?> cells = iGroup.getChildren();
		Listcell progressCell = (Listcell) cells.get(1);
		Progressmeter progress = (Progressmeter) progressCell.getChildren().get(0);
		progress.setValue(status.getComponentProgress());

		int i = this.stListbox.getIndexOfItem(iGroup);
		for (i = i + 1; i < this.stListbox.getItems().size(); i++)
		{
			Listitem item = this.stListbox.getItemAtIndex(i);
			if (item.getId().equalsIgnoreCase(
					status.getComponent() + status.getOperation()))
			{
				this.updateItem(item, status);
				return;
			}
			if (item instanceof Listgroup)
				break;
		}
		this.insertItem(i, status);
	}

	private void updateItem(Listitem item, NormalStatus status)
	{
		if (status.isOperationDone() )
		{
			Listcell cellDone = (Listcell) item.getChildren().get(2);
			Checkbox cbox = (Checkbox) cellDone.getChildren().get(0);
			cbox.setChecked(true);
		}
	}

	private void insertItem(int index, NormalStatus status)
	{
		Listitem item = new Listitem();
		item.setId(status.getComponent() + status.getOperation()); // item id = component id +  operation id

		Listcell cellComponent = new Listcell(status.getComponent()); // cell component
		cellComponent.setParent(item);
		Listcell cellOperation = new Listcell(status.getOperation()); // cell operation
		cellOperation.setParent(item);

		Listcell cellDone = new Listcell(); //
		Checkbox cbox = new Checkbox();
		cbox.setChecked(false);
		if (status.isOperationDone())
			cbox.setChecked(true);
		cbox.setParent(cellDone);
		cbox.setDisabled(true);
		cellDone.setParent(item);

		if (index >= this.stListbox.getItemCount())
			this.stListbox.appendChild(item);
		else
		{
			Component insertBefore = this.stListbox.getItemAtIndex(index);
			this.stListbox.insertBefore(item, insertBefore);
		}
		//Clients.scrollIntoView(item);
	}

	private void addGroup(NormalStatus status)
	{
		Listgroup newGroup = new Listgroup();
		newGroup.setId(status.getComponent()); // group id = component ID

		Listcell component = new Listcell(status.getComponent()); // cell component
		component.setParent(newGroup);

		Progressmeter progress = new Progressmeter();
		progress.setValue(status.getComponentProgress());
		Listcell progressCell = new Listcell(); // cell progress
		progress.setParent(progressCell);
		progressCell.setParent(newGroup);

		this.stListbox.appendChild(newGroup);
		this.insertItem(this.stListbox.getItemCount(), status);
	}
}
