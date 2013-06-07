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

package eu.optimis.demogui.sdo.backend;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import eu.optimis._do.schemas.Objective;
import eu.optimis._do.schemas.PlacementSolution;
//import eu.optimis._do.schemas.PlacementSolution;
//import eu.optimis.demogui.sdo.backend.endpoints.DeploymentServiceEndpoint;
//import eu.optimis.manifest.api.sp.Manifest;
//import eu.optimis.sd.BrokerSD;
import eu.optimis.sd.SD;
//import eu.optimis.sd.schemas.st.ErrorStatus;
import eu.optimis.sd.schemas.st.Status;

/**
 * @author Wubin.Li (Viali)
 * @author Petter Svärd
 * 
 */

public class ServiceDeployerThread extends Thread
{
	private Logger logger = Logger.getLogger(ServiceDeployerThread.class);
	
	private String sdo_config_path;
	private File manifestFile;
	private Objective objective;

	private SD sd = null;

	public ServiceDeployerThread(String sdo_config_path, File manifestfile, Objective objective)
	{
		this.sdo_config_path = sdo_config_path;
		this.manifestFile = manifestfile;
		this.objective = objective;
		
		logger.info("Using Confuguration Path: "+this.sdo_config_path);
		logger.info("Optimization Objective : " + this.objective);
		
		try
		{
				this.buildSD();		
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void buildSD()
	{
		if (this.sdo_config_path == null)
		{
			logger.info("No sdo-configure specified. Use the Default!");
			this.sd = new SD();
			this.sdo_config_path = this.sd.getConfigurationFile();
		}
		else
		{
			logger.info("User Specifed SDO-CONFIG file is used!");
			this.sd = new SD(this.sdo_config_path);
		}
	}
	/*
	private void buildBrokerSD()
	{
		if (this.sdo_config_path == null)
		{
			logger.info("No sdo-configure specified. Use the Default!");
			sd = new BrokerSD();
			this.sdo_config_path = sd.getConfigurationFile();
		}
		else
		{
			logger.info("User Specifed SDO-CONFIG file is used!");
			sd = new BrokerSD(this.sdo_config_path);
		}
	}*/

	@Override
	public void run()
	{
		logger.info("ServiceDeployerThread started...");
		
		this.runSD();
		/*
		else if (scenario.equals(ScenarioType.BROKER))
		{
			this.runBrokerSD();
		}*/
		logger.info("ServiceDeployerThread done...");
	}

	
	private void runSD()
	{
		this.sd.deploy(this.manifestFile, this.objective);		
		try
		{
			Thread.sleep(2000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	/*
	private void runBrokerSD()
	{
		String serviceId = null;
		try
		{
			XmlBeanServiceManifestDocument doc = XmlBeanServiceManifestDocument.Factory.parse(this.manifestFile);
			Manifest manifest = Manifest.Factory.newInstance(doc);
			serviceId = manifest.getVirtualMachineDescriptionSection().getServiceId();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			ErrorStatus status = new ErrorStatus("ServiceDeployerThread", "Manifest File Parsing", e.getMessage());
			sd.getDeploymentStatusList().add(status);
			return;
		}

		Configuration config = ConfigurationFactory.getConfig(this.sdo_config_path);
		final String host = config.getString(SDConfigurationKeys.CLOUD_BROKER_HOST);
		final String port = config.getString(SDConfigurationKeys.CLOUD_BROKER_PORT);
		int timeout = config.getInteger(SDConfigurationKeys.CLOUD_BROKER_TIMEOUT, 300);

		//Invoke the Broker
		boolean res = ((BrokerSD) sd).invokeCloudBroker(host, port,
				manifestFile, objective);
		if (res)
			logger.debug("CloudBroker is called successfully.");
		else
		{
			sd.getStatusDAO().addErrorStatus("BrokerSD", "Call Cloud Broker",
					"Failed");
			return;
		}

		logger.debug("NOW wait for the response from Broker, TIME OUT Setting = "+timeout +" seconds.");
		
		//Wait for the response
		int time = 0;
		while (time++ < timeout
				&& DeploymentServiceEndpoint.getBrokerQueue().containsKey(
						serviceId) == false)
		{
			try
			{
				Thread.sleep(1000);
				logger.debug("time = "+time);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
		
		if (time >= timeout
				&& DeploymentServiceEndpoint.getBrokerQueue().containsKey(
						serviceId) == false)
		{
			sd.getStatusDAO().addErrorStatus("BrokerSD", "Waiting for PlacementSolution from Broker", "TIME OUT!");
			return;
		}
		
		PlacementSolution placementSolution = DeploymentServiceEndpoint.getBrokerQueue().get(serviceId);
		try
		{
			((BrokerSD)sd).deployBrokerSolution(serviceId, placementSolution);
			
			//Deployed delete from the Queue
			DeploymentServiceEndpoint.getBrokerQueue().remove(serviceId);
		}
		catch (Exception et)
		{
			et.printStackTrace();
			logger.error(et.getMessage());
			sd.getStatusDAO().addErrorStatus("BrokerSD", "Deployment", et.getMessage());
		}
	}
*/
	public List<Status> getDeploymentStatusList()
	{
		return this.sd.getDeploymentStatusList();
	}

	public Status getLatestRootStatus()
	{
		return this.sd.getLatestRootStatus();
	}
	
	public PlacementSolution readPlacementResult()
	{
		PlacementSolution ps = this.sd.readPlacementSolution();
		return ps;
	}
}
