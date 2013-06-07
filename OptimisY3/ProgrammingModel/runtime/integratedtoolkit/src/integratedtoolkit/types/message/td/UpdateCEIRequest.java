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
 *  Copyright 2002-2012 Barcelona Supercomputing Center (www.bsc.es)
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
package integratedtoolkit.types.message.td;

import integratedtoolkit.types.ResourceDescription;
import java.util.HashMap;
import java.util.LinkedList;

public class UpdateCEIRequest extends TDMessage {

    private Object[] signatures;
    private String[] constraints;
    private ResourceDescription[] rds;
    private LinkedList<String>[] licenses;
    private HashMap<String, Integer> response;

    public UpdateCEIRequest() {
        super(TDMessageType.UPDATE_CEI);
    }

    public UpdateCEIRequest(Object[] signatures, String[] constraints, ResourceDescription[] rds, LinkedList<String>[] licenses) {
        super(TDMessageType.UPDATE_CEI);
        this.signatures = signatures;
        this.constraints = constraints;
        this.rds = rds;
        this.licenses = licenses;
    }

    public Object[] getSignatures() {
        return signatures;
    }

    public void setSignatures(Object[] signatures) {
        this.signatures = signatures;
    }

    public String[] getConstraints() {
        return constraints;
    }

    public void setConstraints(String[] constraints) {
        this.constraints = constraints;
    }

    public ResourceDescription[] getRds() {
        return rds;
    }

    public void setRds(ResourceDescription[] rds) {
        this.rds = rds;
    }

    public HashMap<String, Integer> getResponse() {
        return response;
    }

    public void setResponse(HashMap<String, Integer> response) {
        this.response = response;
    }

    public LinkedList<String>[] getLicenses() {
        return this.licenses;
    }

    public void setLicenses(LinkedList<String>[] licenses) {
        this.licenses = licenses;
    }
}
