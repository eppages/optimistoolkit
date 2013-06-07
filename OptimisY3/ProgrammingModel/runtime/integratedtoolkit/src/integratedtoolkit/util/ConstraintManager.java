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
package integratedtoolkit.util;

import integratedtoolkit.types.Core;
import integratedtoolkit.types.ResourceDescription;
import integratedtoolkit.types.annotations.Constraints;
import integratedtoolkit.types.annotations.Parameter;
import integratedtoolkit.types.annotations.Service;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * The ConstraintManager is an utility to manage the relations between resource
 * features and the constraints imposed by each core.
 */
public class ConstraintManager {

    public static final String NO_CONSTR = "/ResourceList/Resource";
    public static final String METHOD_CONSTRUCTOR_ERROR = "Error loading method information ";
    /**
     * Annotated interface class
     */
    private static Class<?> annotItfClass;
    /**
     * Xpath constraints per core
     */
    private static String[] constraints;
    /**
     * Description of the resource ablt to run the core
     */
    private static ResourceDescription[] resourceConstraints;

    /**
     * Constructs a new ConstraintManager. The method loads the annotated class
     * and initilizes the data structures that contain the constraints. For each
     * method found in the annotated interface creates its signature and adds
     * the constraints to the structures.
     *
     * @param annotatedItf package and name of the Annotated Interface class
     * @throws ClassNotFoundException The package and className are not in the
     * CLASSPATH
     */
    public static void init(String annotatedItf) throws ClassNotFoundException {
        System.out.println("Reading Constraints");
        annotItfClass = Class.forName(annotatedItf);
        Core.coreCount = annotItfClass.getDeclaredMethods().length;
        constraints = new String[annotItfClass.getDeclaredMethods().length];
        resourceConstraints = new ResourceDescription[annotItfClass.getDeclaredMethods().length];
        Core.signatureToId = new TreeMap<String, Integer>();
        LicenseManager.init(annotItfClass.getDeclaredMethods().length);

        for (int method_id = 0; method_id < annotItfClass.getDeclaredMethods().length; method_id++) {

            Method m = annotItfClass.getDeclaredMethods()[method_id];
            //Computes the method's signature
            StringBuilder buffer = new StringBuilder();
            buffer.append(m.getName()).append("(");
            int numPars = m.getParameterAnnotations().length;
            String type;
            if (numPars > 0) {
                type = inferType(m.getParameterTypes()[0], ((Parameter) m.getParameterAnnotations()[0][0]).type());
                buffer.append(type);
                for (int i = 1; i < numPars; i++) {
                    type = inferType(m.getParameterTypes()[i], ((Parameter) m.getParameterAnnotations()[i][0]).type());
                    buffer.append(",").append(type);
                }
            }
            buffer.append(")");

            if (m.isAnnotationPresent(integratedtoolkit.types.annotations.Method.class)) {
                Annotation methodAnnot = m.getAnnotation(integratedtoolkit.types.annotations.Method.class);
                buffer.append(((integratedtoolkit.types.annotations.Method) methodAnnot).declaringClass());
                loadMethodConstraints(method_id, m.getAnnotation(Constraints.class));
            } else { // Service
                Service serviceAnnot = m.getAnnotation(Service.class);
                buffer.append(serviceAnnot.namespace()).append(',').append(serviceAnnot.name()).append(',').append(serviceAnnot.port());
                loadServiceConstraints(method_id, serviceAnnot);
            }

            String signature = buffer.toString();
            //Adds a new Signature-Id if not exists in the TreeMap
            Integer methodId = Core.signatureToId.get(signature);
            if (methodId == null) {
                Core.signatureToId.put(signature, method_id);
            }
        }
    }

    public static LinkedList<Integer> addMethods(Object[] signature, String[] constraint, ResourceDescription[] rd) {
        LinkedList newMethods = new LinkedList<Integer>();
        Integer[] present = new Integer[signature.length];
        boolean[] defined = new boolean[signature.length];
        int newCount = 0;

        for (int i = 0; i < signature.length; i++) {
            Integer methodId = Core.signatureToId.get((String) signature[i]);
            if (methodId != null) {
                present[i] = methodId;
                if (constraints[methodId] != null) {
                    defined[i] = true;
                }
            } else {
                newCount++;
            }
        }

        String[] tempConstraints = new String[Core.coreCount + newCount];
        ResourceDescription[] tempRD = new ResourceDescription[Core.coreCount + newCount];
        System.arraycopy(constraints, 0, tempConstraints, 0, Core.coreCount);
        System.arraycopy(resourceConstraints, 0, tempRD, 0, Core.coreCount);
        constraints = tempConstraints;
        resourceConstraints = tempRD;

        for (int i = 0; i < signature.length; i++) {
            if (defined[i]) {
                continue;
            }
            int core_id;
            if (present[i]==null){
                core_id = Core.coreCount;
                Core.signatureToId.put((String) signature[i], core_id);
                Core.coreCount++;
                
            }else{
                core_id=present[i];
            }
            newMethods.add(core_id);
            constraints[core_id] = constraint[i];
            resourceConstraints[core_id] = rd[i];
        }
        return newMethods;
    }

    /**
     * Gets the already stored constraints for all the cores in XPath format
     *
     * @return The already stored constraints for the cores in XPath format
     */
    public static String[] getConstraints() {
        return constraints;
    }

    /**
     * Gets the already stored constraints for the core in XPath format
     *
     * @param coreId Identifier of the core
     * @return The already stored constraints for that core in XPath format
     */
    public static String getConstraints(int coreId) {
        return constraints[coreId];
    }

    /**
     * Infers the type of a parameter. If the parameter is annotated as a FILE
     * or a STRING, the type is taken from the annotation. If the annotation is
     * UNSPECIFIED, the type is taken from the formal type.
     *
     * @param formalType Formal type of the parameter
     * @param annotType Annotation type of the parameter
     * @return A String representing the type of the parameter
     */
    private static String inferType(Class<?> formalType, Parameter.Type annotType) {
        if (annotType.equals(Parameter.Type.UNSPECIFIED)) {
            if (formalType.isPrimitive()) {
                if (formalType.equals(boolean.class)) {
                    return "BOOLEAN_T";
                } else if (formalType.equals(char.class)) {
                    return "CHAR_T";
                } else if (formalType.equals(byte.class)) {
                    return "BYTE_T";
                } else if (formalType.equals(short.class)) {
                    return "SHORT_T";
                } else if (formalType.equals(int.class)) {
                    return "INT_T";
                } else if (formalType.equals(long.class)) {
                    return "LONG_T";
                } else if (formalType.equals(float.class)) {
                    return "FLOAT_T";
                } else //if (formalType.equals(double.class))
                {
                    return "DOUBLE_T";
                }
            } /*else if (formalType.isArray()) { // If necessary
             }*/ else { // Object
                return "OBJECT_T";
            }
        } else {
            return annotType + "_T";
        }
    }

    /**
     * Loads the Constraints in case that core is a service. Only in Xpath
     * format since there are no resource where its tasks can run
     *
     * @param methodId identifier for that core
     * @param service Servive annotation describing the core
     */
    private static void loadServiceConstraints(int methodId, Service service) {
        StringBuilder constrXPath = new StringBuilder().append("/ResourceList/Service[Name[text()='");
        constrXPath.append(service.name());
        constrXPath.append("'] and Namespace[text()='");
        constrXPath.append(service.namespace());
        constrXPath.append("'] and Port[text()='");
        constrXPath.append(service.port());
        constrXPath.append("']]");

        String methodConstr = constrXPath.toString();
        constraints[methodId] = methodConstr;
    }

    /**
     * Loads the Constraints in case that core is a service in XPath format and
     * describing the features of the resources able to run its tasks
     *
     * @param coreId identifier for that core
     * @param service Method annotation describing the core
     */
    private static void loadMethodConstraints(int coreId, Constraints m) {
        ResourceDescription rm = new ResourceDescription();
        if (m == null) {
            constraints[coreId] = NO_CONSTR;
        } else {
            List<String> requirements = buildXPathConstraints(m);
            //Adds the Constraints to constraints
            StringBuilder constrXPath = new StringBuilder();
            for (Iterator<String> i = requirements.iterator(); i.hasNext();) {
                String content = i.next();
                constrXPath.append(content);
                if (i.hasNext()) {
                    constrXPath.append(" and ");
                }
            }


            String methodConstr;
            if (constrXPath.toString().length() == 0) {
                methodConstr = NO_CONSTR;
            } else {
                methodConstr = "/ResourceList/Resource[" + constrXPath.toString() + "]";
            }
            constraints[coreId] = methodConstr;

            //specifies the Resources needed to execute the task
            rm.addHostQueue(m.hostQueue());
            rm.setProcessorCPUCount(Integer.parseInt("" + m.processorCPUCount()));
            rm.setProcessorSpeed(Float.parseFloat("" + m.processorSpeed()));
            rm.setProcessorArchitecture(m.processorArchitecture());
            rm.setOperatingSystemType(m.operatingSystemType());
            rm.setStorageElemSize(Float.parseFloat("" + m.storageElemSize()));
            rm.setStorageElemAccessTime(Float.parseFloat("" + m.storageElemAccessTime()));
            rm.setStorageElemSTR(Float.parseFloat("" + m.storageElemSTR()));
            rm.setMemoryPhysicalSize(Float.parseFloat("" + m.memoryPhysicalSize()));
            rm.setMemoryVirtualSize(Float.parseFloat("" + m.memoryVirtualSize()));
            rm.setMemoryAccessTime(Float.parseFloat("" + m.memoryAccessTime()));
            rm.setMemorySTR(m.memorySTR());
            String software = m.appSoftware();
            if (software.compareTo("[unassigned]") != 0) {
                int last = 0;
                while (software.length() > 0) {
                    last = software.lastIndexOf(",");
                    rm.addAppSoftware(software.substring(last + 1, software.length()));
                    if (last == -1) {
                        software = "";
                    } else {
                        software = software.substring(0, last);
                    }
                }
            }
            String licenseName = m.licenseTokens();
            if (licenseName.compareTo("[unassigned]") != 0) {
                LicenseManager.addLicenseToken(coreId, licenseName);
            }
        }
        resourceConstraints[coreId] = rm;
    }

    /**
     * Returns the description of the resources that are able to run each core
     *
     * @return the description of the resources that are able to run each core
     */
    public static ResourceDescription[] getResourceConstraints() {
        return resourceConstraints;
    }

    /**
     * Returns the description of the resources that are able to run that core
     *
     * @param coreId identifier of the core
     * @return the description of the resources that are able to run that core
     */
    public static ResourceDescription getResourceConstraints(int coreId) {
        return resourceConstraints[coreId];
    }

    /**
     * Looks for all the cores from in the annotated Interface which constraint
     * are fullfilled by the resource description passed as a parameter
     *
     * @param rd description of the resource
     * @return the list of cores which constraints are fulfilled by rd
     */
    public static List<Integer> findExecutableCores(ResourceDescription rd) {
        LinkedList<Integer> executableList = new LinkedList<Integer>();
        for (int method_i = 0; method_i < annotItfClass.getDeclaredMethods().length; method_i++) {
            Method m = annotItfClass.getDeclaredMethods()[method_i];
            Constraints mc = (Constraints) m.getAnnotation(Constraints.class);
            boolean executable = true;
            if (mc != null) {
                //Check processor
                if (executable && mc.processorCPUCount() != 0) {
                    executable = (mc.processorCPUCount() <= rd.getProcessorCPUCount());
                }
                if (executable && mc.processorSpeed() != 0.0f) {
                    executable = (mc.processorSpeed() <= rd.getProcessorSpeed());
                }
                if (executable && mc.processorArchitecture().compareTo("[unassigned]") != 0) {
                    executable = (rd.getProcessorArchitecture().compareTo(mc.processorArchitecture()) == 0);
                }


                //Check Memory
                if (executable && mc.memoryPhysicalSize() != 0.0f) {
                    executable = ((int) ((Float) mc.memoryPhysicalSize() * (Float) 1024f) <= (int) ((Float) rd.getMemoryPhysicalSize() * (Float) 1024f));
                }
                if (executable && mc.memoryVirtualSize() != 0.0f) {
                    executable = ((int) ((Float) mc.memoryVirtualSize() * (Float) 1024f) <= (int) ((Float) rd.getMemoryVirtualSize() * (Float) 1024f));
                }
                if (executable && mc.memoryAccessTime() != 0.0f) {
                    executable = (mc.memoryAccessTime() >= rd.getMemoryAccessTime());
                }
                if (executable && mc.memorySTR() != 0.0f) {
                    executable = (mc.memorySTR() <= rd.getMemorySTR());
                }

                //Check disk
                if (executable && mc.storageElemSize() != 0.0f) {
                    executable = ((int) ((Float) mc.storageElemSize() * (Float) 1024f) <= (int) ((Float) rd.getStorageElemSize() * (Float) 1024f));
                }
                if (executable && mc.storageElemAccessTime() != 0.0f) {
                    executable = (mc.storageElemAccessTime() >= rd.getStorageElemAccessTime());
                }
                if (executable && mc.storageElemSTR() != 0.0f) {
                    executable = (mc.storageElemSTR() <= rd.getStorageElemSTR());
                }

                //Check OS
                if (executable && mc.operatingSystemType().compareTo("[unassigned]") != 0) {
                    executable = (rd.getOperatingSystemType().compareTo(mc.operatingSystemType()) == 0);
                }
            }
            if (executable) {
                //Computes the method's signature
                StringBuilder buffer = new StringBuilder();
                buffer.append(m.getName()).append("(");
                int numPars = m.getParameterAnnotations().length;
                String type;
                if (numPars > 0) {
                    type = inferType(m.getParameterTypes()[0], ((Parameter) m.getParameterAnnotations()[0][0]).type());
                    buffer.append(type);
                    for (int i = 1; i < numPars; i++) {
                        type = inferType(m.getParameterTypes()[i], ((Parameter) m.getParameterAnnotations()[i][0]).type());
                        buffer.append(",").append(type);
                    }
                }
                buffer.append(")");

                if (m.isAnnotationPresent(integratedtoolkit.types.annotations.Method.class)) {
                    Annotation methodAnnot = m.getAnnotation(integratedtoolkit.types.annotations.Method.class);
                    buffer.append(((integratedtoolkit.types.annotations.Method) methodAnnot).declaringClass());
                    String signature = buffer.toString();
                    executableList.add(Core.signatureToId.get(signature));
                }
            }
        }

        return executableList;
    }

    /**
     * Creates the Xpath constraints taking into account the annaotation of the
     * core
     *
     * @param constrAnnot Constraints annotation to convert to XPath
     * @return the constraints in XPath format
     */
    private static List<String> buildXPathConstraints(Annotation constrAnnot) {
        ArrayList<String> requirements = new ArrayList<String>();

        String procArch = ((Constraints) constrAnnot).processorArchitecture();
        float cpuSpeed = ((Constraints) constrAnnot).processorSpeed();
        int cpuCount = ((Constraints) constrAnnot).processorCPUCount();
        String osType = ((Constraints) constrAnnot).operatingSystemType();
        float physicalMemSize = ((Constraints) constrAnnot).memoryPhysicalSize();
        float virtualMemSize = ((Constraints) constrAnnot).memoryVirtualSize();
        float memoryAT = ((Constraints) constrAnnot).memoryAccessTime();
        float memorySTR = ((Constraints) constrAnnot).memorySTR();
        float diskSize = ((Constraints) constrAnnot).storageElemSize();
        float diskAT = ((Constraints) constrAnnot).storageElemAccessTime();
        float diskSTR = ((Constraints) constrAnnot).storageElemSTR();
        //String queue	   		= ((Constraints)constrAnnot).hostQueue();
        String appSoftware = ((Constraints) constrAnnot).appSoftware();

        // Translation of constraints : Java Annotations -> XPath expression
        if (!procArch.equals("[unassigned]")) {
            requirements.add("Capabilities/Processor/Architecture[text()='" + procArch + "']");
        }
        if (cpuSpeed > 0) {
            requirements.add("Capabilities/Processor/Speed[text()>=" + cpuSpeed + "]");
        }
        if (cpuCount > 0) {
            requirements.add("Capabilities/Processor/CPUCount[text()>=" + cpuCount + "]");
        }
        if (!osType.equals("[unassigned]")) {
            requirements.add("Capabilities/OS/OSType[text()='" + osType + "']");
        }
        if (physicalMemSize > 0) {
            requirements.add("Capabilities/Memory/PhysicalSize[text()>=" + physicalMemSize + "]");
        }
        if (virtualMemSize > 0) {
            requirements.add("Capabilities/Memory/VirtualSize[text()>=" + virtualMemSize + "]");
        }
        if (memoryAT > 0) {
            requirements.add("Capabilities/Memory/AccessTime[text()<=" + memoryAT + "]");
        }
        if (memorySTR > 0) {
            requirements.add("Capabilities/Memory/STR[text()>=" + memorySTR + "]");
        }
        if (diskSize > 0) {
            requirements.add("Capabilities/StorageElement/Size[text()>=" + diskSize + "]");
        }
        if (diskAT > 0) {
            requirements.add("Capabilities/StorageElement/AccessTime[text()<=" + diskAT + "]");
        }
        if (diskSTR > 0) {
            requirements.add("Capabilities/StorageElement/STR[text()>=" + diskSTR + "]");
        }
        if (!appSoftware.equals("[unassigned]")) {
            String[] software = appSoftware.split(",");
            for (String s : software) {
                requirements.add("Capabilities/ApplicationSoftware/Software[text()='" + s + "']");
            }
        }

        return requirements;
    }
}
