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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import es.bsc.servicess.ide.model.Dependency;

/** Implementation of the Classpath Container for the dependencies of the service elements.
 *  
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 *
 */
public class DependenciesClasspathContainer implements IClasspathContainer {
	
	IJavaProject project;
	private static Logger log = Logger.getLogger(DependenciesClasspathContainer.class);

	/**Default constructor.
	 */
	public DependenciesClasspathContainer() {

	}
	/** Constructor
	 * 
	 * @param project Java project where the Classpath constainer is created
	 */
	public DependenciesClasspathContainer(IJavaProject project) {
		this.project = project;
	}

	
	/** Gets the dependencies of the Service Elements which have to be included in the
	 * classpath (jars, class folders) 
	 * @return
	 */
	private String[] getDependencies() {
		if (project != null) {
			IFile metadataFile = project.getProject()
					.getFolder(ProjectMetadata.METADATA_FOLDER)
					.getFile(ProjectMetadata.METADATA_FILENAME);
			if (metadataFile != null) {
				try {
					ProjectMetadata pr_meta = new ProjectMetadata(new File(
							metadataFile.getRawLocation().toOSString()));
					Dependency[] classes = pr_meta.getDependencies();
					//Map<String, List<String>> externalPackages = pr_meta.getExternalOrchestrationClassesPackages();
					ArrayList<String> jarClasses = new ArrayList<String>();
					for (Dependency dep : classes) {
						/*log.debug(" Analysing " + dep.getType() + " "
								+ dep.getLocation() + "(compare with "
								+ ProjectMetadata.JAR_DEP_TYPE + " or "
								+ ProjectMetadata.CLASS_FOLDER_DEP_TYPE + ")");*/
						if (dep.getType().equalsIgnoreCase(
								ProjectMetadata.JAR_DEP_TYPE)
								|| dep.getType().equalsIgnoreCase(
										ProjectMetadata.CLASS_FOLDER_DEP_TYPE)) {
							log.info("Adding dependency " + dep.getLocation());
							jarClasses.add(dep.getLocation());
						}else if (dep.getType().equalsIgnoreCase(ProjectMetadata.WAR_DEP_TYPE)){
							
							if (dep.isImported()){
								try{
									String location = dep.getLocation();
									IFolder folder = project.getProject().getFolder(ProjectMetadata.IMPORT_FOLDER).getFolder(PackagingUtils.getPackageName(location)+"/WEB-INF/classes/");
									//log.debug("Import Folder for "+dep.getLocation()+ " is " + folder.getFullPath().toOSString());
									jarClasses.add(folder.getFullPath().toOSString());
								}catch(Exception e){
									log.warn(e.getMessage());
								}
							}
						}
					}
					return jarClasses.toArray(new String[jarClasses.size()]);
				} catch (Exception e) {
					log.error("Error getting information in metadata file",e);
					e.printStackTrace();
					return null;
				}
			} else {
				log.error("Error metadata file not found");
				return null;
			}
		} else {
			log.error("Project not found");
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.IClasspathContainer#getClasspathEntries()
	 */
	@Override
	public IClasspathEntry[] getClasspathEntries() {
		ArrayList<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
		String[] dependencies = getDependencies();
		if (dependencies != null) {
			for (String p : dependencies) {
				IPath path = new Path(p);
				IClasspathEntry ent = JavaCore.newLibraryEntry(path, null,
						null, false);
				if (ent != null) {
					entries.add(ent);
				}
			}
		}

		return entries.toArray(new IClasspathEntry[entries.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.IClasspathContainer#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Element Dependencies";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.IClasspathContainer#getKind()
	 */
	@Override
	public int getKind() {
		return IClasspathContainer.K_APPLICATION;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.IClasspathContainer#getPath()
	 */
	@Override
	public IPath getPath() {
		return new Path(ProjectMetadata.DEPENDENCY_ENTRYPATH);
	}

}
