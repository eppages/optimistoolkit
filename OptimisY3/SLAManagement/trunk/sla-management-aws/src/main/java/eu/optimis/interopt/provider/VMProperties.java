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
Oriol Collell, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.interopt.provider;

import java.util.HashMap;
import java.util.Map;

public class VMProperties extends HashMap<String, String>
    implements Map<String, String>
{
    private static final long serialVersionUID = 1L;

    private String id;
    private String status;
    private String hostname;
    private String ip;

    public static final String AWS_INSTANCE_TYPE = "com.amazon.aws.instance.type";

    /**
     * Returns the id of a particular VM instance.
     * 
     * @return the id of the VM instance
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id of a VM instance.
     * 
     * @param id
     *            the VM id
     */
    public void setId( String id )
    {
        this.id = id;
    }

    /**
     * Retrieves the status of a VM. The status should be compliant to the states as defined by the OCCI
     * specification.
     * 
     * @return VM status
     */
    public String getStatus()
    {
        return status;
    }

    /**
     * Sets the status of a VM. The status should be compliant to the states as defined by the OCCI
     * specification.
     * 
     * @param status
     *            the VM status.
     */
    public void setStatus( String status )
    {
        this.status = status;
    }
    
    public String getHostname() {
        return this.hostname;
    }
    
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    
    public String getIp() {
        return this.ip;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }

}
