package eu.optimis.elasticityengine.monitoring;

/**
 * This module tests the call to the Monitoring interface
* 
 * @author Ahmed Ali-Eldin (<a
 *         href="mailto:ahmeda@cs.umu.se">ahmeda@cs.umu.se</a>)
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

import java.io.IOException;
import java.util.List;

import eu.optimis.elasticityengine.monitoring.RESTMonitoringSource;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;

public class RESTMonitoringSourceTest {

    public static void main(String[] args) throws IOException, InterruptedException {
    	
    	RESTMonitoringSource monitor=new RESTMonitoringSource("hostName", 70);
    	List<MonitoringResourceDataset> data=monitor.getData("22", "KPI");
    	
		
		
	}

}
