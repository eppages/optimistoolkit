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
package integratedtoolkit.util.ur;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;

import integratedtoolkit.types.annotations.Method;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.EnumMemberValue;

/**
 * User: michael
 * Date: Apr 23, 2008
 * Time: 11:14:21 AM
 */
public final class Annotator {

    // Counters
    private static int interfaceMethodsChecked = 0;
    private static int interfaceMethodsAdded = 0;
    private static int interfacesChecked = 0;
    private static int interfacesWritten = 0;
    private static int workerMethodsChecked = 0;
    private static int workerMethodsAdded = 0;
    private static int workersChecked = 0;
    private static int workersWritten = 0;

    // Where classes are coming from and going to
    private static String baseDir;
    // Flag so we don't re-write a class file if we don't need to
    private static boolean somethingAnnotated;
    // Classes we need
    private static final Logger logger = Logger.getLogger(Annotator.class);
    private static final ClassPool pool = ClassPool.getDefault();
    // Constants
    private static final String newMethodSuffix = "WithUR";
    private static final String interfaceSuffix = "Itf.class";

    /**
     * Helper method to return the path to the file given as a
     * string formatted as a class path entry
     *
     * @param file which we want to get its path for adding to the class path
     * @return path to file formatted for class path
     */
    private static String getClassPath(String classFile) {

        if (classFile.endsWith(".class")) {
            classFile = classFile.substring(0, classFile.indexOf(".class"));
        }

        classFile = classFile.substring(baseDir.length());
        classFile = classFile.replace(File.separatorChar, '.');

        if (classFile.startsWith(".")) {
            classFile = classFile.substring(1, classFile.length());
        }

        return classFile;
    }

    /**
     * Helper method that just overloads the previous method
     *
     * @param file which we want to get its path for adding to the class path
     * @return path to file formatted for class path
     */
    private static String getClassPath(File file) {
        return getClassPath(file.getPath());
    }

    /**
     *
     */
    private static HashMap<String, HashMap<String, String>> getImplClassMethods(File itf)
            throws NotFoundException, ClassNotFoundException {

        // Get the interface object and declared methods
        String classPath = getClassPath(itf);
        CtClass itfClass = pool.get(classPath);
        CtMethod[] methods = itfClass.getDeclaredMethods();

        // In hashmap we return < <impl class name>, < method, method signature> >
        HashMap<String, HashMap<String, String>> toAnnotate =
                new HashMap<String, HashMap<String, String>>();

        // Loop through the methods found
        for (CtMethod m : methods) {
                if (m.hasAnnotation(Method.class)) {
                        // If we find a method annotation, check it out.
                        String implClass = ((Method)m.getAnnotation(Method.class)).declaringClass();
                String methodName = m.getName();
                String methodSignature = m.getSignature();

                HashMap<String, String> methodInfo = toAnnotate.get(implClass);

                if (methodInfo == null) {
                    methodInfo = new HashMap<String, String>();
                    toAnnotate.put(implClass, methodInfo);
                }
                methodInfo.put(methodSignature, methodName);
                
                // if an entry doesn't exist, create one
                /*if (methodInfo == null) {
                    methodInfo = new HashMap<String, String>();
                    methodInfo.put(methodName, methodSignature);
                    toAnnotate.put(implClass, methodInfo);
                } // only add to entry if the name, signature combo doesn't exist
                else if (!methodSignature.equals(methodInfo.get(methodName))) {
                    methodInfo.put(methodName, methodSignature);
                    toAnnotate.put(implClass, methodInfo);
                }*/
            }
        }

        return toAnnotate;
    }

    /**
     * Method that will look recursively down a directory structure
     * from the base directory given looking for files to annotate
     *
     * @param dir directory to start looking for files
     * @throws NotFoundException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws CannotCompileException
     */
    private static void annotateFiles(File dir)
            throws NotFoundException, ClassNotFoundException, IOException, CannotCompileException {

        if (dir.isDirectory()) {
            String[] children = dir.list();

            for (String child : children) {
                annotateFiles(new File(dir, child));
            }
        } else {
            if (dir.getName().endsWith(interfaceSuffix)) {
                annotateInterface(dir);
                annotateImplementations(getImplClassMethods(dir));
            }
        }
    }

    /**
     * Annotate method m in class c
     *
     * @param m is the method to annotate
     * @param c is the class m is in
     */
    private static void annotateWorkerMethod(CtMethod m, CtClass c)
            throws CannotCompileException, NotFoundException {

    	CtClass[] newParameters = null;
        try {
            workerMethodsChecked++;
            
            // Add four parameters the list of parameters
            // 1st param = app name (string)
            // 2nd param = slaId  (string)
            // 3rd param = ur file name (string)
            // 4th param = primary host (as string - HACK!)
                // 5th param = id of job transfers (string)
            CtClass[] parameters = m.getParameterTypes();
            newParameters = new CtClass[parameters.length + 5];
            System.arraycopy(parameters, 0, newParameters, 0, parameters.length);

            CtClass stringClass = pool.get("java.lang.String");
            newParameters[parameters.length] = stringClass;
            newParameters[parameters.length + 1] = stringClass;
            newParameters[parameters.length + 2] = stringClass;
            newParameters[parameters.length + 3] = stringClass;
            newParameters[parameters.length + 4] = stringClass;

            checkIfAnnotated(c, m, newParameters);
        } catch (NotFoundException e) {
            CtMethod newM = duplicateMethod(c, m, newParameters);

            // Add and configure method body
            newM.setBody(m, null);
            String urClass = "integratedtoolkit.util.ur.UsageRecord";

            // String of params to pass to usage record
            StringBuilder params = new StringBuilder("\"" + c.getSimpleName() + "\""); // appName
            params.append(", ");
            params.append("(String) $args[$args.length-4]"); // slaId
            params.append(", ");
            params.append("(String) $args[$args.length-3]"); // ur file name
            params.append(", ");
            params.append("(String) $args[$args.length-2]"); // primary host
            params.append(", ");
            params.append("(String) $args[$args.length-1]"); // transfer id

            newM.insertBefore(urClass + ".start(" + params.toString() + ");");
            newM.insertAfter(urClass + ".end();", true);

            // Add new method to class
            c.addMethod(newM);
            workerMethodsAdded++;
            somethingAnnotated = true;
        }
    }

    /**
     * Helper method that factors out the method duplication
     *
     */
    private static CtMethod duplicateMethod(CtClass c, CtMethod m, CtClass[] newParameters)
            throws NotFoundException {
        // Copy method
        CtMethod newM = new CtMethod(m.getReturnType(), m.getName() + newMethodSuffix, newParameters, c);

        // Copy exception types and modifiers
        newM.setExceptionTypes(m.getExceptionTypes());
        newM.setModifiers(m.getModifiers());

        // Get annotations from existing method
        MethodInfo mi = m.getMethodInfo();
        AnnotationsAttribute aa = (AnnotationsAttribute) mi.getAttribute(AnnotationsAttribute.visibleTag);

        try {
            // May throw NPE if no annotations
            Annotation[] a = aa.getAnnotations();

            // Add annotations to new method via method info object
            MethodInfo newMi = newM.getMethodInfo();
            ConstPool cp = newMi.getConstPool();
            AnnotationsAttribute newAa = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
            newAa.setAnnotations(a);
            newMi.addAttribute(newAa);
        }
        catch (NullPointerException e) {
        }
        finally {
            return newM;
        }
    }

    /**
     *
     */
    private static void checkIfAnnotated(CtClass c, CtMethod m, CtClass[] newParams)
            throws NotFoundException {
        // if the method has already been annotated don't
        // test for the annotated version
        if (!m.getName().endsWith(newMethodSuffix)) {
            // method has not been annotated, so test for presence of the annotated
            // version - if method does not exist we throw exception and annotate
            //c.getDeclaredMethod(m.getName() + newMethodSuffix);
        	c.getDeclaredMethod(m.getName() + newMethodSuffix, newParams);
        }
    }

    /**
     *
     */
    private static void annotateInterface(File itf)
            throws NotFoundException, ClassNotFoundException, CannotCompileException, IOException {
        boolean interfaceAnnotated = false;
        interfacesChecked++;

        // Get the interface object and declared methods
        String classPath = getClassPath(itf);
        CtClass itfClass = pool.get(classPath);
        CtMethod[] methods = itfClass.getDeclaredMethods();
        CtClass[] newParameters = null;
        
        for (CtMethod m : methods) {
            try {
                interfaceMethodsChecked++;
                
                CtClass[] parameters = m.getParameterTypes();

                // Get array of the correct length and copy existing parameters into it
                newParameters = new CtClass[parameters.length + 5];
                System.arraycopy(parameters, 0, newParameters, 0, parameters.length);

                // Create four new parameters
                CtClass appNameParam = pool.get("java.lang.String");
                CtClass slaIdParam = pool.get("java.lang.String");
                CtClass usageRecordParam = pool.get("java.lang.String");
                CtClass primaryHostParam = pool.get("java.lang.String");
                CtClass transferIdParam = pool.get("java.lang.String");

                // Add new parameters to array
                newParameters[parameters.length] = appNameParam;
                newParameters[parameters.length + 1] = slaIdParam;
                newParameters[parameters.length + 2] = usageRecordParam;
                newParameters[parameters.length + 3] = primaryHostParam;
                newParameters[parameters.length + 4] = transferIdParam;

                checkIfAnnotated(itfClass, m, newParameters);
            //logger.debug(m.getName() + " is already annotated");
            } catch (NotFoundException e) {


                // Create and add method with new params
                CtMethod newM = duplicateMethod(itfClass, m, newParameters);

                // Get existing annotations
                MethodInfo mi = m.getMethodInfo();
                ParameterAnnotationsAttribute paa = (ParameterAnnotationsAttribute) mi.getAttribute(ParameterAnnotationsAttribute.visibleTag);
                Annotation[][] annotations = paa == null ? new Annotation[0][] : paa.getAnnotations();

                // Create new array for new annotations and copy existing annotations into it
                // POSSIBLE PROBLEM HERE AS WE DEFINE THE SECOND ARRAY TO BE 1 ELEMENT IN LENGTH
                Annotation[][] newAnnotations = new Annotation[annotations.length + 5][1];
                System.arraycopy(annotations, 0, newAnnotations, 0, annotations.length);

                // Set stuff up for in a moment
                ConstPool cp = mi.getConstPool();

                String paramMetadataClass = integratedtoolkit.types.annotations.Parameter.class.getCanonicalName();

                EnumMemberValue stringTypeEmv = new EnumMemberValue(cp);
                stringTypeEmv.setType(paramMetadataClass + "$Type");
                stringTypeEmv.setValue("STRING");

                EnumMemberValue fileTypeEmv = new EnumMemberValue(cp);
                fileTypeEmv.setType(paramMetadataClass + "$Type");
                fileTypeEmv.setValue("FILE");

                EnumMemberValue inDirectionEmv = new EnumMemberValue(cp);
                inDirectionEmv.setType(paramMetadataClass + "$Direction");
                inDirectionEmv.setValue("IN");

                EnumMemberValue outDirectionEmv = new EnumMemberValue(cp);
                outDirectionEmv.setType(paramMetadataClass + "$Direction");
                outDirectionEmv.setValue("OUT");

                // Create five new annotations
                Annotation appNameAnnotation = new Annotation(paramMetadataClass, cp);
                appNameAnnotation.addMemberValue("type", stringTypeEmv);
                appNameAnnotation.addMemberValue("direction", inDirectionEmv);

                Annotation slaIdAnnotation = new Annotation(paramMetadataClass, cp);
                slaIdAnnotation.addMemberValue("type", stringTypeEmv);
                slaIdAnnotation.addMemberValue("direction", inDirectionEmv);

                Annotation usageRecordAnnotation = new Annotation(paramMetadataClass, cp);
                usageRecordAnnotation.addMemberValue("type", fileTypeEmv);
                usageRecordAnnotation.addMemberValue("direction", outDirectionEmv);

                Annotation primaryHostAnnotation = new Annotation(paramMetadataClass, cp);
                primaryHostAnnotation.addMemberValue("type", stringTypeEmv);
                primaryHostAnnotation.addMemberValue("direction", inDirectionEmv);

                Annotation transferIdAnnotation = new Annotation(paramMetadataClass, cp);
                transferIdAnnotation.addMemberValue("type", stringTypeEmv);
                transferIdAnnotation.addMemberValue("direction", inDirectionEmv);

                // Add new annotations to array
                newAnnotations[annotations.length] = new Annotation[1];
                newAnnotations[annotations.length][0] = appNameAnnotation;
                newAnnotations[annotations.length + 1] = new Annotation[1];
                newAnnotations[annotations.length + 1][0] = slaIdAnnotation;
                newAnnotations[annotations.length + 2] = new Annotation[1];
                newAnnotations[annotations.length + 2][0] = usageRecordAnnotation;
                newAnnotations[annotations.length + 3] = new Annotation[1];
                newAnnotations[annotations.length + 3][0] = primaryHostAnnotation;
                newAnnotations[annotations.length + 4] = new Annotation[1];
                newAnnotations[annotations.length + 4][0] = transferIdAnnotation;

                // Add annotations to method
                ParameterAnnotationsAttribute newPaa = new ParameterAnnotationsAttribute(cp, ParameterAnnotationsAttribute.visibleTag);
                newPaa.setAnnotations(newAnnotations);
                newM.getMethodInfo().addAttribute(newPaa);

                itfClass.addMethod(newM);
                interfaceMethodsAdded++;

                interfaceAnnotated = true;
            }
        }

        // Only if something was annotated write the file
        if (interfaceAnnotated) {
            itfClass.writeFile(baseDir);
            interfacesWritten++;
        }
        itfClass.detach();
    }

    /**
     *
     */
    private static void annotateImplementations(HashMap<String, HashMap<String, String>> implClasses)
            throws NotFoundException, CannotCompileException, IOException {

        // Find each implementing class and annotate
        for (Iterator iter = implClasses.entrySet().iterator(); iter.hasNext();) {

            workersChecked++;

            Map.Entry entry = (Map.Entry) iter.next();
            String workerClassName = (String) entry.getKey();

            HashMap<String, String> methodInfo = (HashMap<String, String>) entry.getValue();

            // Get the worker class and declared methods
            CtClass workerClass = pool.get(workerClassName);
            CtMethod[] methods = workerClass.getDeclaredMethods();

            // Nothing annotated yet.
            somethingAnnotated = false;

            // Loop through and annotate the methods specified in interface
            for (CtMethod m : methods) {

            	String methodSignature = m.getSignature();

                // Check that this is a method to annotate with the same signature...
                if (methodInfo.containsKey(methodSignature)) {
                    // ...and only annotate if we haven't annotated before
                    if (!m.getName().endsWith(newMethodSuffix)) {
                        annotateWorkerMethod(m, workerClass);
                    }
                }
            	
                /*String methodName = m.getName();

                // Check that this is a method to annotate...
                if (methodInfo.containsKey(methodName)) {

                    // ... with the same signature...
                    if (methodInfo.get(methodName).equals(m.getSignature())) {

                        // ...and only annotate if we haven't annotated before
                        if (!m.getName().endsWith(newMethodSuffix)) {
                        	annotateWorkerMethod(m, workerClass);
                        }
                    }
                }*/
            }

            if (somethingAnnotated) {
                workerClass.writeFile(baseDir);
                workersWritten++;
            }
            workerClass.detach();
        }
    }

    /**
     * Main method to start the annotation process
     *
     * @param args should contain one entry:
     * the base directory to start looking for files
     *
     */
    public static void main(String[] args) {

        PropertyConfigurator.configure(System.getProperty("log4j.configuration"));
        baseDir = args[0];
        logger.info("Reading source files from " + baseDir);

        try {
            pool.appendPathList(baseDir);
            annotateFiles(new File(baseDir));
        } catch (NotFoundException e) {
            e.printStackTrace();
            logger.fatal("Something wasn't found");
            System.exit(1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.fatal("Couldn't find a class?");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            logger.fatal("Problem writing modified bytecode");
            System.exit(1);
        } catch (CannotCompileException e) {
            e.printStackTrace();
            logger.fatal("Problem compiling modified bytecode");
            System.exit(1);
        }

        logger.info("Interfaces scanned/re-written: " + interfacesChecked + "/" + interfacesWritten);
        logger.info("Interface methods scanned/added: " + interfaceMethodsChecked + "/" + interfaceMethodsAdded);
        logger.info("Workers scanned/re-written: " + workersChecked + "/" + workersWritten);
        logger.info("Worker methods scanned/added: " + workerMethodsChecked + "/" + workerMethodsAdded);
    }
}