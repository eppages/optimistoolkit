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
package integratedtoolkit.components;

import integratedtoolkit.types.Parameter;


/* To initiate the creation of a task
 * To inform that the application will request no more tasks
 * To synchronize with the creation of all previous tasks
 */
public interface TaskCreation {

    void newTask(Long appId, String methodClass, String methodName, boolean hasTarget, Parameter[] parameters);

    void newTask(Long appId, String namespace, String service, String port, String operation, boolean hasTarget, Parameter[] parameters);

    void noMoreTasks(Long appId);
    
}
