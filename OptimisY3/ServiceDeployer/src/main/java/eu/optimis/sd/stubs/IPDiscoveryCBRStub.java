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
package eu.optimis.sd.stubs;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.optimis.cbr.client.CBRClient;
import eu.optimis.ipdiscovery.datamodel.Provider;
import eu.optimis.sd.util.SDConfigurationKeys;
import eu.optimis.sd.util.config.Configuration;
import eu.optimis.sd.util.config.ConfigurationFactory;

public class IPDiscoveryCBRStub
{
	protected final static Logger logger = Logger.getLogger(IPDiscoveryCBRStub.class);
	
	private static IPDiscoveryCBRStub instance;
	
	private CBRClient cbrClient;
	
	public static IPDiscoveryCBRStub getInstance(String configurationFile) throws Exception
	{
		if (instance ==null)
		{
			instance = new IPDiscoveryCBRStub();
			instance.buildIPC(configurationFile);
		}
		return instance;
	}
	
	private void buildIPC(String configurationFile) throws Exception
	{
		Configuration config = ConfigurationFactory
				.getConfig(configurationFile);
		String host = config.getString(SDConfigurationKeys.IP_DISCOVERY_HOST);
		String port = config.getString(SDConfigurationKeys.IP_DISCOVERY_PORT);
		logger.debug("Contacting IPDiscovery service at: " + host + " : "
				+ port);
		if (host == null || port == null)
		{
			String msg = "Host/Port for IPDiscovery is NOT set correctly in the configuration file.";
			logger.error(msg);
			throw new Exception(msg);
		}
		this.cbrClient = new CBRClient(host.trim(), port.trim());
	}

	public List<Provider> getAvailableIPs() throws Exception
	{
		List<Provider> availableIPs = this.cbrClient.getAllIP().getIPList();
		if (availableIPs == null)
		{
			throw new Exception("IP list is null");
		}
		if (availableIPs.size() == 0)
		{
			throw new Exception("IP list is empty");
		}

		logger.debug("There are " + availableIPs.size() + " IP(s) available");
		return availableIPs;
	}
	
	public List<Provider> ipFiltration(List<Provider> ipList,
			List<String> excludedIpIds) throws Exception
	{		
		List<Provider> filteredList = new ArrayList<Provider>();

		logger.debug("Filtering IPs using parameter 'excludedIpIds'.");
		logger.debug("Parameter excludedIpIds is null = " + (excludedIpIds == null));
		for (Provider provider : ipList)
		{
			String pId = provider.getIdentifier();
			if (excludedIpIds != null && excludedIpIds.contains(pId))
			{
				logger.debug("IP: " + pId + " is excluded!");
				continue;
			}			
			filteredList.add(provider);
		}
		return filteredList;
	}
}
