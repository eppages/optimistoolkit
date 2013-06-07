/**
 * 
 * @author Daniel Espling (<a href="mailto:espling@cs.umu.se">espling@cs.umu.se</a>)
 * Copyright (C) 2011 Umea University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.optimis_project.monitoring;

public class ServiceMonitoringConfigSettings {
	public static final String CONFIG_FILE_PATH = "file:///etc/optimis/modules/service-monitoring/";
	public static final String CONFIG_FILE_NAME = "monitoring.properties";
    
	public static final String CONF_MON_PORT_KEY = "monitoring_port";
	public static final String CONF_MON_HOSTNAME_KEY = "monitoring_host";
	public static final String CONF_CO_PORT_KEY = "cloudoptimizer_port";
	public static final String CONF_CO_HOSTNAME_KEY = "cloudoptimizer_host";

	public static final String CONF_OWN_PORT_KEY = "own_port";
}
