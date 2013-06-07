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

import integratedtoolkit.components.JobStatus.JobEndStatus;
import integratedtoolkit.util.RequestDispatcher;
import integratedtoolkit.util.RequestQueue;
import integratedtoolkit.util.Serializer;
import integratedtoolkit.util.ThreadPool;

import integratedtoolkit.api.ITExecution.*;
import integratedtoolkit.types.Parameter.DependencyParameter.*;
import integratedtoolkit.types.Parameter.*;
import integratedtoolkit.types.data.DataAccessId;
import integratedtoolkit.types.data.DataAccessId.RAccessId;
import integratedtoolkit.types.data.DataAccessId.RWAccessId;

import org.apache.cxf.endpoint.Client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.cxf.endpoint.ClientCallback;

public class WSJob extends Job {

    private static RequestQueue<WSJob> callerQueue;
    private static WSCaller caller;
    // Pool of worker threads and queue of requests
    private static ThreadPool callerPool;
    private static final int POOL_SIZE = 10;
    private static final String POOL_NAME = "WS";
    private static final String THREAD_POOL_ERR = "Error starting pool of threads";
    private static final String SUBMIT_ERROR = "Error calling Web Service";
    private Object returnValue;
    private static Map<String, Object> renameToObject;
    private static HashMap<String, ServiceInstance> wsdlToInstance;

    public static void init() throws Exception {
        // Create thread that will handle job submission requests
        if (callerQueue == null) {
            callerQueue = new RequestQueue<WSJob>();
        } else {
            callerQueue.clear();
        }

        caller = new WSCaller(callerQueue);
        callerPool = new ThreadPool(POOL_SIZE, POOL_NAME, caller);
        try {
            callerPool.startThreads();
        } catch (Exception e) {
            logger.error(THREAD_POOL_ERR, e);
            throw e;
        }

        renameToObject = Collections.synchronizedSortedMap(new TreeMap<String, Object>());
        wsdlToInstance = new HashMap<String, ServiceInstance>();
    }

    public static void end() {
        try {
            callerPool.stopThreads();
        } catch (Exception e) {
        }
    }

    public static void setServiceInstances(List<ServiceInstance> services) {
        for (ServiceInstance service : services) {
            wsdlToInstance.put(service.getWsdl(), service);
        }
    }

    public static void setObjectVersionValue(String name, Object value) {
        renameToObject.put(name, value);
    }

    public static Object getObjectVersionValue(String name) {
        return renameToObject.get(name);
    }

    public static boolean isInMemory(String name) {
        return renameToObject.get(name) != null;
    }

    public static void obsoleteObject(String name) {
        renameToObject.remove(name);
    }

    public WSJob(Task task) {
        jobId = nextJobId++;
        this.task = task;
        history = JobHistory.NEW;
        this.returnValue = null;
    }

    public JobKind getKind() {
        return JobKind.SERVICE;
    }

    public void submit() {
        callerQueue.enqueue(this);
    }

    public void stop() {
    }

    public Object getReturnValue() {
        return returnValue;
    }

    static class WSCaller extends RequestDispatcher<WSJob> {

        public WSCaller(RequestQueue<WSJob> queue) {
            super(queue);
        }

        public void processRequests() {
            while (true) {
                WSJob job = null;
                job = queue.dequeue();
                if (job == null) {
                    break;
                }

                try {
                    ArrayList<Object> input = new ArrayList<Object>();
                    Service service = (Service) job.task.getCore();
                    Parameter[] parameters = service.parameters;
                    for (int i = 0; i < service.parameters.length; i++) {
                        if (parameters[i].getDirection() == ParamDirection.IN) {
                            switch (parameters[i].getType()) {
                                case OBJECT_T:
                                    ObjectParameter otPar = (ObjectParameter) parameters[i];
                                    checkIfInMemoryOrFile(otPar);
                                    input.add(otPar.getValue());
                                    break;
                                case FILE_T:
                                    //¿¿¿¿?????
                                    //CAN'T USE A FILE AS A PARAMETER
                                    //SKIP!
                                    break;
                                default:// Basic or String
                                    BasicTypeParameter btParB = (BasicTypeParameter) parameters[i];
                                    input.add(btParB.getValue());

                            }

                        }
                    }

                    String wsdlLocation = job.getExecutionParams().getHost();
                    String portName = service.getPortName();
                    String operationName = service.getName();
                    ServiceInstance serviceInstance = wsdlToInstance.get(wsdlLocation);
                    if (serviceInstance == null) { //First Invocation & not predefined
                        serviceInstance = new ServiceInstance(wsdlLocation, service.getServiceName(), service.getNamespace());
                        wsdlToInstance.put(wsdlLocation, serviceInstance);
                    }

                    Client client = serviceInstance.getClient(portName);


                    //Object[] result = client.invoke(operationName, input.toArray());

                    ClientCallback cb = new ClientCallback();
                    client.invoke(cb, operationName, input.toArray());
                    /*for (Object o : input.toArray()) {
                     logger.debug("Service parameter " + o);
                     }*/
                    Object[] result = cb.get();

                    if (result.length > 0) {
                        job.returnValue = result[0];
                    }
                    //logger.debug("Service result: " + job.returnValue);
                    associatedJM.jobStatusNotification(job, JobEndStatus.OK);
                } catch (Throwable e) {
                    associatedJM.jobStatusNotification(job, JobEndStatus.EXECUTION_FAILED);
                    logger.error(SUBMIT_ERROR, e);
                    return;
                }

            }
        }

        private void checkIfInMemoryOrFile(ObjectParameter otPar) {
            String rename;
            DataAccessId oaId = otPar.getDataAccessId();
            switch (otPar.getDirection()) {
                case IN:
                    rename = ((RAccessId) oaId).getReadDataInstance().getRenaming();
                    break;
                case INOUT:
                    rename = ((RWAccessId) oaId).getReadDataInstance().getRenaming();
                    break;
                case OUT:
                    return;
                default:
                    return;
            }

            // Check if the object was the return value of a service
            Object value = renameToObject.get(rename);
            if (value != null) {
                otPar.setValue(value);
                return;
            }

            // Check if the object is in a local file	
            String path = ((DependencyParameter) otPar).getDataRemotePath();
            try {
                value = Serializer.deserialize(path);
            } catch (Exception e) {
                // Object is not in file
                return;
            }
            otPar.setValue(value);
        }
    }
}
