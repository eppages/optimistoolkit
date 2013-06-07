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
package eu.optimis.sd.util;

/**
 * Configuration field names for config parameters, and custom utility methods.
 * The value is the name of the property defined in the configuration file.
 */

public class SDConfigurationKeys
{
	// configuration file monitor interval (in ms)
	public static final String CONFIG_REFRESHPERIOD = "configuration.refreshperiod";

	//IP Discovery Endpoint
	public static final String IP_DISCOVERY_ENDPOINT = "ip.discovery.endpoint";
	
	public static final String IP_DISCOVERY_HOST = "ip.discovery.host";
	public static final String IP_DISCOVERY_PORT = "ip.discovery.port";
	
	//VM Contextualization
	public static final String VMC_CONFIG_PATH = "vmc.config.file.path";
	
	// Address for TREC service
	public static final String TREC_SERVICE_HOST = "trec.service.host";
	public static final String TREC_SERVICE_PORT = "trec.service.port";
	
	public static final String TREC_LINK_URL = "trec.link.url";

	// Address for Service Manager
	public static final String SERVICE_MANAGER_HOST = "service.manager.host";
	public static final String SERVICE_MANAGER_PORT = "service.manager.port";
	
	//For Monitoring
	public static final String SP_TREC_DB_URL = "sp.trec.db.url";
	public static final String SP_TREC_DB_USERNAME = "sp.trec.db.username";
	public static final String SP_TREC_DB_PASSWORD = "sp.trec.db.password";
	
	//For Scenarios
	
	//BurstingCase: leg 1: Flexiant Enhanced --> Atos, no VMC/DM/SM
	//BurstingCase: leg 2: Atos --> Arsys/Amazon, need VMC&DM, no SM
	public static final String IS_SERVICE_BURSTING_CASE_LEG_ONE = "is.service.bursting.leg.one";
	
	public static final String IS_SERVICE_BURSTING_CASE_LEG_TWO = "is.service.bursting.leg.two";
	
	//For Manifest repository service
	public static final String MANIFEST_REPO_URL="manifest.repo.url";
	
	//For Special purpose
	public static final String LOCAL_PROVIDER_NAME = "eu.optimis.localprovider.name";
	
	//This is for FIRST-FIT ALGORITHMS.
	public static final String IS_BIG_SIZED_PROBLEM ="is.big.sized.problem";
	
	public static final String IS_CHECK_LEGAL_IGNORED="is.check.legal.ignored";
	public static final String IS_TREC_CALL_SKIPPED = "is.trec.call.skip";
}
