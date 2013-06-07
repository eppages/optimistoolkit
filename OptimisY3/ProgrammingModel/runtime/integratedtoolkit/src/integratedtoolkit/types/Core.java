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
package integratedtoolkit.types;

import java.io.Serializable;
import java.util.Map;
import integratedtoolkit.api.ITExecution.*;

public abstract class Core implements Serializable {

    public static Map<String, Integer> signatureToId;
    public static int coreCount = 0;
    protected Integer coreId;
    protected String coreName;
    protected Parameter[] parameters;
    protected boolean hasTarget;
    protected boolean hasReturn;

    public Core() {
    }

    public Core(String methodName, boolean hasTarget, Parameter[] parameters) {
        this.coreName = methodName;
        this.hasTarget = hasTarget;
        this.parameters = parameters;
        Parameter lastParam = parameters[parameters.length - 1];
        this.hasReturn = (lastParam.getDirection() == ParamDirection.OUT && lastParam.getType() == ParamType.OBJECT_T);
    }

    public Integer getCoreId() {
        return coreId;
    }

    public void setCoreId(Integer coreId) {
        this.coreId = coreId;
    }

    public String getCoreName() {
        return coreName;
    }

    public void setCoreName(String methodName) {
        this.coreName = methodName;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public void setParameters(Parameter[] parameters) {
        this.parameters = parameters;
    }

    public boolean isHasTarget() {
        return hasTarget;
    }

    public void setHasTarget(boolean hasTarget) {
        this.hasTarget = hasTarget;
    }

    public boolean isHasReturn() {
        return hasReturn;
    }

    public void setHasReturn(boolean hasReturn) {
        this.hasReturn = hasReturn;
    }

    public boolean hasTargetObject() {
        return hasTarget;
    }

    public boolean hasReturnValue() {
        return hasReturn;
    }

    public int getId() {
        return coreId;
    }

    public String getName() {
        return coreName;
    }
}
