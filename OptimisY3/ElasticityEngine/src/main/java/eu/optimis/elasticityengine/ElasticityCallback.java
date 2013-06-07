package eu.optimis.elasticityengine;

/**
 * Callback interface for the Elasiticity engine 
 * 
 * @author Ahmed Ali-Eldin
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
public interface ElasticityCallback {

    /**
     * Called to give a recommendation of the amount of VMs of a specific type
     */
    // TODO Update javadoc
    public String getNrInstances(String serviceID, String imageID);

    /**
     * Called to indicate the amount of VMs of a specific type should be
     * increased.
     * 
     * @param serviceID
     *            The service ID
     * @param imageID
     *            The image type of VM to increase
     * @param serviceManifest
     *            The manifest of service which the type belongs to
     * @param delta
     *            The number of new VMs to add
     * @param spAddress
     * 			  The address of the controlling SP (received when starting elastictiy)
     */
    public void addVM(String serviceID, String serviceManifest, String imageID, int delta, String spAddress);

    /**
     * Called to indicate the amount of VMs of a specific type should be
     * decreased.
     * 
     * @param serviceID
     *            The service ID
     * @param imageID
     *            The image type of VM to decrease
     * @param delta
     *            The number String getNrInstances(String serviceID);
}of new VMs to remove
     * @param spAddress
     * 			  The address of the controlling SP (received when starting elastictiy)
     */
    public void removeVM(String serviceID, String imageID, int delta, String spAddress);

	String Fake();

}
