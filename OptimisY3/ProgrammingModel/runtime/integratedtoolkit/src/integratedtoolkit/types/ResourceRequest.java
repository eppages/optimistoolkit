/*
 *  Copyright 2002-2013 Barcelona Supercomputing Center (www.bsc.es)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package integratedtoolkit.types;

import java.util.List;

/**
 *
 * @author flordan
 */
public class ResourceRequest {

    private ResourceDescription requested;
    private int                 requestedTaskCount;
    private List<Integer>       requestedMethodIds;
    private ResourceDescription granted;

    public ResourceRequest(ResourceDescription requestedResource, int requestedTaskCount, List<Integer> methodIds ){
        requested=requestedResource;
        this.requestedTaskCount = requestedTaskCount;
        granted= new ResourceDescription();
        this.requestedMethodIds = methodIds;
    }


    public void grant(ResourceDescription grantedResource){
        granted=grantedResource;
    }

    public int getRequestedTaskCount(){
        return requestedTaskCount;
    }

    public List<Integer> getRequestedMethodIds(){
        return requestedMethodIds;
    }
    public ResourceDescription getRequested(){
        return requested;
    }

    public ResourceDescription getGranted(){
        return granted;
    }
}
