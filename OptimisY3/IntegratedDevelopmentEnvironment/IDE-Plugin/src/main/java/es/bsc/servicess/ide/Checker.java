/*
 *  Copyright 2011-2012 Barcelona Supercomputing Center (www.bsc.es)
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

package es.bsc.servicess.ide;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;

/** Class to validate data introduced by users in the plug-in.
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 *
 */
public class Checker {
	private static Logger log = Logger.getLogger(Checker.class);
	
	/**Check the correct name of a project
	 * @param projectName Name of the project to be evaluated
	 * @return Status.OK if name is correct and Status.ERROR if the name is not correct 
	 */
	public static IStatus validateProjectName(String projectName){
		Pattern p = Pattern.compile("[^A-Za-z_0-9]");
		Matcher m = p.matcher(projectName);
		if (m.find()){ 
			return new Status(Status.ERROR, "IDE", "Project Name contains ilegal characters (/?:@-._~!$&'()*+,;=");
		}else{
			return Status.OK_STATUS;
		}	
	}
	
	/**Check the correct name of a package
	 * @param packageName Name of the package to be evaluated
	 * @return Status.OK if name is correct and Status.ERROR if the name is not correct
	 */
	public static IStatus validatePackageName(String packageName){
		return JavaConventions.validatePackageName(packageName, JavaCore.VERSION_1_3,  JavaCore.VERSION_1_3);
	}
	
	public static void main(String[] args) {
		IStatus status = validateProjectName("hola");
		System.out.println(status.getMessage());
		if (status.getSeverity() == Status.ERROR){
			System.out.println("Error validating project name");
		}else
			System.out.println("Valid name");
		status = validateProjectName("hola@@@");
		System.out.println(status.getMessage()+" Code: " + status.getSeverity());
		if (status.getSeverity() == Status.ERROR){
			System.out.println("Error validating project name");
		}else
			System.out.println("Valid name");
	}

	/** Check if the runtime location contains the IT.jar in the libs folder
	 * @param runtimeLocation
	 * @return Status.OK if name is correct and Status.ERROR if the name is not correct
	 */
	public static IStatus validateRuntimeLocation(String runtimeLocation) {
		//TODO add other folders checking
		File f = new File(runtimeLocation+File.separator+ProjectMetadata.ITJAR_EXT);
		log.debug("IT.jar path"+f.getAbsolutePath());
		if (!f.exists()){
			return new Status(Status.ERROR, "IDE", "IT.jar can not be found in "+ f.getAbsolutePath());
		}
		return Status.OK_STATUS;
	}
	
	/** Check if class name is correct
	 * @param className Name of the class to be evaluated
	 * @return Status.OK if name is correct and Status.ERROR if the name is not correct
	 */
	public static IStatus validateClassName(String className){
		return JavaConventions.validateJavaTypeName(className, JavaCore.VERSION_1_3,  JavaCore.VERSION_1_3);
	}
	
	/** Check if method name is correct
	 * @param methodName Name of the method to be evaluated
	 * @return Status.OK if name is correct and Status.ERROR if the name is not correct
	 */
	public static IStatus validateMethodName(String methodName){
		return JavaConventions.validateMethodName(methodName, JavaCore.VERSION_1_3,  JavaCore.VERSION_1_3);
	}
	
	/** Check if orchestration class is correct
	 * @param className Name of the orchestration class
	 * @param project Project which owns the orchestration class
	 * @return Status.OK if name is correct and Status.ERROR if the name is not correct
	 */
	public static IStatus validateOrchestrationClass(String className, IProject project){
		try{
			File meta = project.getFolder(	ProjectMetadata.METADATA_FOLDER).getFile(ProjectMetadata.METADATA_FILENAME).getRawLocation().toFile();
			if (!meta.exists()){
				return new Status(Status.ERROR, "IDE", "Incorrect project we can not find Project Metadata information");
			}
			ProjectMetadata prMetadata = new ProjectMetadata(meta);
			String[] classes = prMetadata.getAllOrchestrationClasses();
			for (String cl:classes){
				if (className.equals(cl))
					return Status.OK_STATUS;
			}
			return new Status(Status.ERROR, "IDE", "Orchestration class not found");
		}catch(Exception e){
			return new Status(Status.ERROR, "IDE", e.getMessage());
		}
		
		
	}
	
	/**  Check if the Core Element interface is correct
	 * @param interfaceName Interface to be evaluated
	 * @param project Project were the the interface belongs to
	 * @return Status.OK if name is correct and Status.ERROR if the name is not correct
	 */
	public static IStatus validateCoreElementInterface(String interfaceName, IProject project){
		try{
			File meta = project.getFolder(	ProjectMetadata.METADATA_FOLDER).getFile(ProjectMetadata.METADATA_FILENAME).getRawLocation().toFile();
			if (!meta.exists()){
				return new Status(Status.ERROR, "IDE", "Incorrect project we can not find Project Metadata information");
			}
			ProjectMetadata prMetadata = new ProjectMetadata(meta);
			String[] classes = prMetadata.getAllOrchestrationClasses();
			if (classes.length >0 ){
				for (String cl:classes){
					log.debug("Comparing " +  interfaceName +" with "+ cl+"Itf");
					if (interfaceName.equals(cl+"Itf"))
						return Status.OK_STATUS;
				}
				return new Status(Status.ERROR, "IDE", "Core Element Interface not found");
			}else
				return new Status(Status.ERROR, "IDE", "No orchestration classes found");
		}catch(Exception e){
			return new Status(Status.ERROR, "IDE", e.getMessage());
		}
	}
	
	/** Check if the project contains the Metadata folder and files
	 * @param project Project to be evaluated
	 * @return Status.OK if project is correct and Status.ERROR if the project is not correct
	 */
	public static IStatus validateServiceSsProject(IProject project){
		File meta = project.getFolder(	ProjectMetadata.METADATA_FOLDER).getFile(ProjectMetadata.METADATA_FILENAME).getRawLocation().toFile();
		if (!meta.exists()){
			return new Status(Status.ERROR, "IDE", "Incorrect project we can not find Project Metadata information");
		}else
			return Status.OK_STATUS;
		
	}
}
