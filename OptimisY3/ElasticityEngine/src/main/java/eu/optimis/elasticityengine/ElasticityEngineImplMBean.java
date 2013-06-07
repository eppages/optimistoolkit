package eu.optimis.elasticityengine;

/**
 * 
 * MBean implementation to test the EE runtime using JMX.
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
public interface ElasticityEngineImplMBean {

    String triggerGetNrInstances(String serviceID, String imageID);

    String triggerAddVM(String serviceID, String imageID, int delta, String spAddress);

    String triggerRemoveVM(String serviceID, String imageID, int delta, String spAddress);

}
