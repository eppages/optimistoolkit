/**

Copyright 2013 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf.ip.service.trust;

import org.jboss.logging.Logger;

import eu.optimis.common.trec.db.ip.TrecServiceInfoDAO;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.manifest.api.sp.ServiceProviderExtension;
import eu.optimis.manifest.api.sp.VirtualMachineComponentConfiguration;
import eu.optimis.tf.ip.service.operators.Opinion;
import eu.optimis.trec.common.db.ip.model.ServiceInfo;

public class SPSecurityAspects {

	Logger log = Logger.getLogger(this.getClass().getName());		

	public SPSecurityAspects() 
	{
		log.info("Calculating security aspects");		
	}

	public double calculateSecurityAspects(String serviceId) 
	{		
		Manifest mani = getSPManifest(serviceId);
		if (mani == null)
		{
			log.error("Manifest could not be retrieved!!");
			return 0.0;
		}
		log.info("SP Manifest imported correctly");
		
		ServiceProviderExtension spe = mani.getServiceProviderExtensionSection();		
		int positive = 0;
		int maxSecurity = 0;
		
		// Positive point if there is black list IPs
		try 
		{
			if (spe.getBlackListIPs() != null) 
			{
				positive++;
				maxSecurity++;
			}
		} 
		catch (Exception e) 
		{
			log.error(e.getMessage());			
		}
		
		// Positive point if there is a key for the DataManager
		try 
		{
			if (spe.getDataManagerKey() != null) 
			{
				positive++;
				maxSecurity++;
			}			
			
		} 
		catch (Exception e) 
		{
			log.error(e.getMessage());			
		}
		
		// More positive points got if specific info is added for each component VM
		try 
		{
			if (spe.getVirtualMachineComponentConfigurationArray() != null) 
			{
				positive++;
				maxSecurity++;
				VirtualMachineComponentConfiguration [] myList = spe.getVirtualMachineComponentConfigurationArray();				
				maxSecurity = maxSecurity + myList.length * 6;
				for (VirtualMachineComponentConfiguration vm : myList)
				{
					positive = positive + getPositivePointsPerVM (vm);
				}
			}			
			
		} 
		catch (Exception e) 
		{
			log.error(e.getMessage());			
		}		
		
		int negative = maxSecurity - positive;

		Opinion op = new Opinion(positive, negative);
		double result = op.getExpectation();
		log.info("Security aspects :" + result);
		return result;
	}	
	
	private int getPositivePointsPerVM(VirtualMachineComponentConfiguration vm)
	{
		int positive = 0;
		
		if (vm.isEncryptedSpaceEnabled()) positive ++;
		
		if (vm.isIPSEnabled()) positive ++;
		
		if (vm.isSecuritySSHbased()) positive ++;
		
		if (vm.isSecurityVPNbased()) positive ++;
		
		if (vm.getSSHKey() != null) 
		{
			positive++;			
		}	
		
		if (vm.getTokenArray() != null) 
		{
			positive++;			
		}
		
		return positive;
	}

	private Manifest getSPManifest (String idService)
	{
		Manifest myManifest = null;
		log.debug("Retrieving SP manifest from the db");
		try {
			TrecServiceInfoDAO tsiDAO = new TrecServiceInfoDAO();
			ServiceInfo si = tsiDAO.getService(idService);
			String stringManifest = si.getServiceManifest();
			myManifest = Manifest.Factory.newInstance(stringManifest);
			
		} catch (Exception e) {
			log.error("Unable to retrieve manifest from the db");
			log.error(e.getCause().getMessage());
			log.error(e.getMessage());			
		}
		
		return myManifest;
	}
}
