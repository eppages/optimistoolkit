package eu.optimis.elasticityengine.monitoring;

import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;

/**
 * Interface for different monitoring sources
 * 
 * @author Daniel Espling (<a href="mailto:espling@cs.umu.se">espling@cs.umu.se</a>)
 *Copyright (C) 2012 Umeå University

* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.

* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
public interface MonitoringSource {

    public abstract List<MonitoringResourceDataset> getData(String serviceID, String kpiName);

    public abstract Map<String, List<MonitoringResourceDataset>> getData(String serviceID, Set<String> kpiNames);

	public abstract String[] getKPIs();

}