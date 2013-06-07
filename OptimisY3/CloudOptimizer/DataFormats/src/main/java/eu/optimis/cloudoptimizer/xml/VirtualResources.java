/**
 * Copyright (C) 2010-2012 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public 
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package eu.optimis.cloudoptimizer.xml;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "virtual_resources")
public class VirtualResources {

    private List<VirtualResource> VirtualResource;

    public List<VirtualResource> getVirtualResource() {
        return VirtualResource;
    }

    public void setVirtualResource(
            List<VirtualResource> VirtualResource) {
        this.VirtualResource = VirtualResource;
    }

    @Override
    public String toString() {
        return toXml();
    }
    public String toXml() {
        StringBuilder sb = new StringBuilder("<virtual_resources>\n");
        for(VirtualResource vr : VirtualResource) {
            sb.append(vr.toString()).append("\n");
        }
        sb.append("</virtual_resources>");
        return sb.toString();
    }

}
