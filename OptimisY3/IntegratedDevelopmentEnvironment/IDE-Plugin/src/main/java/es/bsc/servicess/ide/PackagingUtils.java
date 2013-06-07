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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.tools.ant.taskdefs.Sync.MyCopy;
import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.osgi.framework.Bundle;
import org.w3c.dom.Element;

import com.sun.tools.ws.WsGen;

import es.bsc.servicess.ide.editors.CommonFormPage;
import es.bsc.servicess.ide.editors.ServiceFormEditor;
import es.bsc.servicess.ide.editors.deployers.GridResourcesFile;
import es.bsc.servicess.ide.model.Dependency;
import es.bsc.servicess.ide.model.MethodCoreElement;
import es.bsc.servicess.ide.model.OrchestrationClass;
import es.bsc.servicess.ide.model.OrchestrationElement;
import es.bsc.servicess.ide.model.ServiceElement;

public class PackagingUtils {

	private static final String BUNDLE_NAME = "IDE";
	public static final String WAR_CLASSES_PATH = "WEB-INF/classes/";
	public static final String JAR_CLASSES_PATH = ".";
	public static final String ZIP_CLASSES_PATH = "classes/";
	private static Logger log = Logger.getLogger(PackagingUtils.class);
	
	/** Build Service Packages. 
	 * 
	 * @param project Java Project where the service is built
	 * @param pr_meta Project Metadata where the project is described
	 * @param monitor Eclipse progress monitor
	 * @throws Exception 
	 */
	public static void buildPackages(IJavaProject project, ProjectMetadata pr_meta, 
			IProgressMonitor monitor) throws Exception{
		monitor.beginTask("Building project classes...", 100);
		IFolder output = project.getProject().getFolder(
				ProjectMetadata.OUTPUT_FOLDER);
		if (output == null || !output.exists()) {
			output.create(true, true, monitor);
		}
		project.getProject().build(
				IncrementalProjectBuilder.INCREMENTAL_BUILD,
				monitor);
		monitor.done();
		monitor.beginTask("Creating packages...", 2);
		// Create folders to store packages 
		IFolder packagesFolder = output.getFolder(ProjectMetadata.PACKAGES_FOLDER);
		if (packagesFolder != null && packagesFolder.exists()) {
			log.debug("Deleting current packages folder");
			packagesFolder.delete(true, monitor);
		}
		log.debug("Creating packages folder");
		packagesFolder.create(true, true, monitor);

		IFolder srcFolder = project.getProject().getFolder(pr_meta.getSourceDir());
		//Get parameters to perform the package creation
		//String[] packs = pr_meta.getPackages();
		String runtime = pr_meta.getRuntimeLocation();
		//TODO Chak if should be all orchestration or only internal
		String[] orchClasses = pr_meta.getAllOrchestrationClasses();
		String[] extOrchClasses = pr_meta.getExternalOrchestrationClasses();
		IPackageFragmentRoot source = project.findPackageFragmentRoot(srcFolder.getFullPath());			
		IPackageFragmentRoot generated = project.findPackageFragmentRoot(project
					.getProject().getFolder(ProjectMetadata.GENERATED_FOLDER).getFullPath());
		if (source != null && source.exists()) {
			HashMap<String, ServiceElement> constraintsElements = CommonFormPage.getElements(orchClasses,
						ProjectMetadata.CORE_TYPE, project, pr_meta);
			//Create Core Element packages
			String[] corePacks = pr_meta.getPackagesWithCores();		
			if (corePacks != null && corePacks.length > 0) {
				log.debug("Creating core elements packages");
				monitor.subTask("Create Core Element packages");
				monitor.beginTask("Creating packages...", corePacks.length);
				for (String p : corePacks) {
					String[] elements = pr_meta.getElementsInPackage(p);
					if (elements != null && elements.length > 0) {
						PackagingUtils.createCorePackage(runtime, p, elements, pr_meta.getDependencies(elements),
								constraintsElements, source,output, packagesFolder, monitor);
					}
					monitor.worked(1);
				}
				monitor.done();
			} else {
				log.warn("Warning: No core packages built");
				monitor.setCanceled(true);
				throw (new InvocationTargetException(
						new Exception("No packages built")));
			}
			if (orchClasses!= null && orchClasses.length>0){
				String[] orchPacks = pr_meta.getPackagesWithOrchestration();
				
				if (orchPacks != null && orchPacks.length > 0) {
					log.debug("Creating orchestration elements packages");
					for (String p : orchPacks) {
						String[] allElements = pr_meta.getElementsInPackage(p);
						Map<String,List<String>> extElements = pr_meta.getOrchestrationClassesAndElements(allElements, true);
						Map<String,List<String>> intElements = pr_meta.getOrchestrationClassesAndElements(allElements, false);
						if (intElements != null && intElements.size() > 0) {
							PackagingUtils.createInternalOrchestrationPackage(runtime, 
								PackagingUtils.getClasspath(project), p, intElements,
								pr_meta.getDependencies(intElements), source, generated,
								output, packagesFolder, monitor, pr_meta.shouldBeWarFile(p));
							monitor.done();
						}
						if (extElements != null && extElements.size() > 0) {
							PackagingUtils.createExternalOrchestrationPackage(runtime, 
								PackagingUtils.getClasspath(project), p, extElements,
								source, generated, output, packagesFolder, monitor, pr_meta);
							monitor.done();
						}
					}
				} else {
					log.debug("Creating all orchestration element package");
					// Create All Orchestration packages (front-end)
					String[] allElements = CommonFormPage.getOrchestrationElementsLabels(orchClasses, project, pr_meta);
					Map<String,List<String>> extElements = pr_meta.getOrchestrationClassesAndElements(allElements, true);
					Map<String,List<String>> intElements = pr_meta.getOrchestrationClassesAndElements(allElements, false);
					if (intElements != null && intElements.size() > 0) {
						PackagingUtils.createInternalOrchestrationPackage(runtime,PackagingUtils.getClasspath(project),
								project.getProject().getName(),	intElements, pr_meta.getDependencies(intElements),
								source, generated, output, packagesFolder,
								monitor, ProjectMetadata.shouldBeWarFile(pr_meta.getOrchestrationClassesTypes()));
						monitor.done();
					}
					if (extElements != null && extElements.size() > 0) {
						PackagingUtils.createExternalOrchestrationPackage(runtime, PackagingUtils.getClasspath(project),
								project.getProject().getName(),	extElements, source, generated, output, 
								packagesFolder, monitor,pr_meta);
						monitor.done();
					}
				}
			}else{
				log.warn("Warning: No orchestration element packages built");
				monitor.setCanceled(true);
				throw (new InvocationTargetException(
						new Exception("No orchestration packages built")));
			}
			
		} else {
			log.error("Source dir not found");
			monitor.setCanceled(true);
			throw (new InvocationTargetException(new Exception(
					"Source dir " + srcFolder.getFullPath()
							+ " not found")));
		}
	}
	
	/** create the package for a group of core elements
	 * this method creates a jar and a zip file with the 
	 * element classes, their dependencies and runtime files  
	 * 
	 * @param runtimePath Path where the runtime is located
	 * @param packageName Name of the package to create
	 * @param elementsInPackage Array of Core elements in the package
	 * @param dependencies Array of Core Element dependencies of the package 
	 * @param elements Descriptions of all the Core Elements of the java project
	 * @param sourceDir Location of the source folder in the java project
	 * @param binariesFolder Location where the binaries (.class) are stored in the java project
	 * @param packagesFolder Location where the packages are stored
	 * @param myProgressMonitor Eclipse Progress Monitor to report the progress of the different tasks 
	 * @throws CoreException
	 * @throws InterruptedException
	 */
	public static void createCorePackage(String runtimePath, String packageName,
			String[] elementsInPackage, List<Dependency> dependencies,
			HashMap<String, ServiceElement> elements,
			IPackageFragmentRoot sourceDir, IFolder binariesFolder,
			IFolder packagesFolder, IProgressMonitor myProgressMonitor)
			throws CoreException, InterruptedException {
		IFolder jar_folder = packagesFolder.getFolder(packageName);
		if (jar_folder != null && jar_folder.exists()) {
			jar_folder.delete(true, myProgressMonitor);
		}
		jar_folder.create(true, true, myProgressMonitor);

		IFolder classes = jar_folder;
		IPackageFragment[] coreFragments = getCorePackageFragments(elementsInPackage,
				elements, sourceDir);
		if (coreFragments!=null && coreFragments.length>0){
			copyClasses(coreFragments, sourceDir.getPath(), binariesFolder,
				classes, myProgressMonitor);
		}
		IFolder lib = jar_folder.getFolder("lib");
		lib.create(true, true, myProgressMonitor);
		manageDependencies(dependencies, packageName, packagesFolder, classes, lib, myProgressMonitor);
		copyCoreRuntimeFiles(runtimePath, jar_folder);
		IFile jar = packagesFolder.getFile(packageName + ".jar");
		createJar(jar, jar_folder, myProgressMonitor);
		jar_folder.delete(true, myProgressMonitor);

	}

	/** Copy the runtime files required for the Core Elements.
	 * @param runtimePath Path to the programming model runtime
	 * @param jarFolder Folder to store the jars
	 */
	private static void copyCoreRuntimeFiles(String runtimePath, IFolder jarFolder) {

		File d = new File(runtimePath + "/../worker/");
		File it_jar = new File(runtimePath + File.separator + "lib"
				+ File.separator + "IT.jar");
		File jar_folder_file = jarFolder.getLocation().toFile();
		File jar_lib_folder = jarFolder.getLocation().append("lib").toFile();
		try {
			FileUtils.copyFileToDirectory(it_jar, jar_lib_folder);
		} catch (IOException e) {
			log.error("File " + it_jar.getAbsolutePath()
					+ "could not be copied to "
					+ jar_lib_folder.getAbsolutePath());
		}
		if (d.isDirectory()) {
			Iterator<File> fi = FileUtils.iterateFiles(d,
					new String[] { "sh" }, false);
			while (fi.hasNext()) {
				File f = fi.next();
				if (!isFileInDiscardList(f)) {
					try {
						// System.out.println("Trying to copy File "+
						// f.getAbsolutePath());
						FileUtils.copyFileToDirectory(f, jar_folder_file);
						File fc = new File(jar_folder_file.getAbsolutePath()
								+ File.separator + f.getName());
						fc.setExecutable(true);
						log.debug(" File copied "
								+ f.getAbsolutePath());
					} catch (IOException e) {
						log.error("File " + f.getAbsolutePath()
								+ "could not be copied to "
								+ jar_folder_file.getAbsolutePath());
					}
				}
			}
		} else
			log.error("File " + d.getAbsolutePath()
					+ "is not a directory");
	}

	/** Get the java packages of the Core Elements classes
	 * @param elementsInPackage Elements in the package
	 * @param elements Service Core Element description
	 * @param sourceDir Folder of the java project sources
	 * @return
	 */
	private static IPackageFragment[] getCorePackageFragments(
			String[] elementsInPackage,
			HashMap<String, ServiceElement> elements,
			IPackageFragmentRoot sourceDir) {
		ArrayList<IPackageFragment> pfs = new ArrayList<IPackageFragment>();
		for (String e : elementsInPackage) {
			ServiceElement se = elements.get(e);
			if (se instanceof MethodCoreElement){
				String dc = ((MethodCoreElement)se).getDeclaringClass();
				if (dc != null) {
					log.debug("Declaring class for " + e + " is " + dc);
					String p = Signature.getQualifier(dc);
					if (p != null) {
						IPackageFragment pf = sourceDir.getPackageFragment(p);
						log.debug("Package fragment for " + e + " is "
							+ pf.getElementName());
						if (pf != null && pf.exists()) {
							pfs.add(pf);
						}
					}
				}
			}
		}
		return pfs.toArray(new IPackageFragment[pfs.size()]);

	}

	/**Get Java packages for the orchestration Elements
	 * @param elements Orchestration Elements array
	 * @param sourceDir Service implementation source Folder
	 * @return Array of Java packages
	 */
	private static IPackageFragment[] getOrchestrationPackageFragments(
			String[] elements, IPackageFragmentRoot sourceDir) {
		ArrayList<IPackageFragment> pfs = new ArrayList<IPackageFragment>();
		for (String dc : elements) {
			if (dc != null) {
				String p = Signature.getQualifier(dc);
				if (p != null) {
					IPackageFragment pf = sourceDir.getPackageFragment(p);
					if (pf != null && pf.exists()) {
						pfs.add(pf);
					}
				}
			}
		}
		return pfs.toArray(new IPackageFragment[pfs.size()]);

	}

	
	/** Process the service element dependencies and make required actions for element package creation
	 * 
	 * - Copy the jar libraries to the package libraries folder
	 * - Copy class folders to the classes folder
	 * - Create a zip file with the normal files and folders
	 * - Other types do not need actions to do in the package creation phase
	 * 
	 * @param dependencies Array of dependencies to treat for the package
	 * @param packName Name of the element package
	 * @param packagesFolder Folder where element packages are created
	 * @param classesFolder Folder where the classes of the packages must be copied
	 * @param libFolder Folder where the libraries of the packages must be copied
	 * @param myProgressMonitor Eclipse progress monitoring object. 
	 * @throws CoreException
	 * @throws InterruptedException
	 */
	private static void manageDependencies(List<Dependency> dependencies, String packName,
			IFolder packagesFolder, IFolder classesFolder, IFolder libFolder,
			IProgressMonitor myProgressMonitor) throws CoreException,
			InterruptedException {
		// File pack_folder = packages.getLocation().toFile();
		File libF = libFolder.getLocation().toFile();
		File classesF = classesFolder.getLocation().toFile();
		IFolder depsFolder = null;
		for (Dependency d : dependencies) {
			if (d.getType().equalsIgnoreCase(ProjectMetadata.JAR_DEP_TYPE)) {
				log.debug("Copying library " + d);
				File f = new File(d.getLocation());
				if (f != null) {
					try {
						FileUtils.copyFileToDirectory(f, libF);
						log.debug("File " + f.getAbsolutePath()
								+ "has been copied to "
								+ libF.getAbsolutePath());
					} catch (IOException e) {
						log.debug("File " + f.getAbsolutePath()
								+ "could not be copied to "
								+ libF.getAbsolutePath());
						e.printStackTrace();
					}
				} else {
					log.error("File " + d + "does not exists");
				}
			} else if (d.getType().equalsIgnoreCase(
					ProjectMetadata.CLASS_FOLDER_DEP_TYPE)) {
				File f = new File(d.getLocation());
				if (f != null && f.isDirectory()) {
					copyClassDirectory(f, classesF);
				} else {
					log.error("Folder " + d + "does not exists");
				}

			} else if (d.getType().equalsIgnoreCase(
					ProjectMetadata.FOLDER_DEP_TYPE)) {
				if (depsFolder == null) {
					depsFolder = packagesFolder.getFolder(packName + "_deps");
					depsFolder.create(true, true, myProgressMonitor);
				}
				File f = new File(d.getLocation());
				if (f != null && f.isDirectory()) {
					try {
						File destFile = new File(depsFolder.getLocation()
								.toFile().getAbsolutePath()
								+ File.separator + f.getName());
						if (!destFile.exists() || !destFile.isDirectory()) {
							destFile.mkdir();
						}
						FileUtils.copyDirectory(f, destFile);
						log.debug("Folder "	+ f.getAbsolutePath()
								+ "has been copied to "
								+ depsFolder.getLocation().toFile()
										.getAbsolutePath());
					} catch (IOException e) {
						log.error("Folder "
								+ f.getAbsolutePath()
								+ "could not be copied to "
								+ depsFolder.getLocation().toFile()
										.getAbsolutePath(),e);
					}
				} else {
					log.error("Folder " + d + "does not exists");
				}

			} else if (d.getType().equalsIgnoreCase(
					ProjectMetadata.FILE_DEP_TYPE)) {
				if (depsFolder == null) {
					depsFolder = packagesFolder.getFolder(packName + "_deps");
					depsFolder.create(true, true, myProgressMonitor);
				}
				File f = new File(d.getLocation());
				if (f != null && f.isFile()) {
					try {
						FileUtils.copyFileToDirectory(f, depsFolder
								.getLocation().toFile());
						log.debug("File "
								+ f.getAbsolutePath()
								+ "has been copied to "
								+ depsFolder.getLocation().toFile()
										.getAbsolutePath());
					} catch (IOException e) {
						log.error("File "
								+ f.getAbsolutePath()
								+ "could not be copied to "
								+ depsFolder.getLocation().toFile()
										.getAbsolutePath(),e);
						e.printStackTrace();
					}
				} else {
					log.error("File " + d + "does not exists");
				}

			}
		}

		if (depsFolder != null) {
			IFile zip = packagesFolder.getFile(packName + "_deps.zip");
			createZip(zip, depsFolder, myProgressMonitor);
			depsFolder.delete(true, myProgressMonitor);
		}

	}

	/** Copy a folder class of classes to the class folder of a element package
	 * @param dir location of the folder which contains the classes
	 * @param classesFolder Element package of the class folder
	 */
	private static void copyClassDirectory(File dir, File classesFolder) {
		File[] files = dir.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				try {
					File destFile = new File(classesFolder.getAbsolutePath()
							+ File.separator + f.getName());
					if (!destFile.exists() || !destFile.isDirectory()) {
						destFile.mkdir();
					}
					FileUtils.copyDirectory(f, destFile);
					log.debug("File " + f.getAbsolutePath()
							+ "has been copied to "
							+ classesFolder.getAbsolutePath());
				} catch (IOException e) {
					log.error("File " + f.getAbsolutePath()
							+ "could not be copied to "
							+ classesFolder.getAbsolutePath(),e);
					e.printStackTrace();
				}
			} else {
				try {
					FileUtils.copyFileToDirectory(f, classesFolder);
					log.debug("File " + f.getAbsolutePath()
							+ "has been copied to "
							+ classesFolder.getAbsolutePath());
				} catch (IOException e) {
					log.error("File " + f.getAbsolutePath()
							+ "could not be copied to "
							+ classesFolder.getAbsolutePath(),e);
					e.printStackTrace();
				}
			}

		}

	}

	/** Create a zip file
	 * @param file Name of the file
	 * @param folder Folder to zip
	 * @param myProgressMonitor Eclipse Monitoring object to notify the progress 
	 * @throws CoreException
	 * @throws InterruptedException
	 */
	public static void createZip(IFile file, IFolder folder,
			IProgressMonitor myProgressMonitor) throws CoreException,
			InterruptedException {
		IFile buildFile = createZipBuildFile(file, folder, myProgressMonitor);
		runAnt(buildFile.getLocation().toOSString(), new String[] { "zip" },
				myProgressMonitor);
		buildFile.delete(true, myProgressMonitor);
	}

	/* Not used
	 * public static void createServiceOrchestrationPackage(IJavaProject project,
			IFolder packages, IProgressMonitor myProgressMonitor)
			throws CoreException, InterruptedException, ConfigurationException {
		// TODO: Add this method

	}*/

	/** Creates an element package for the front-end which includes all the internal 
	 * Orchestration Elements of a package.
	 * 
	 * Create the service stubs, instrument the classes which contains Orchestration elements, 
	 * manage dependencies, copy programming model runtime files and generate a war package
	 * 
	 * @param runtimePath
	 * @param projectClasspath
	 * @param projectName
	 * @param orchestrationClasses
	 * @param dependencies
	 * @param sourceDir
	 * @param generated
	 * @param outputFolder
	 * @param packagesFolder
	 * @param myProgressMonitor
	 * @param isWar
	 * @throws CoreException
	 * @throws InterruptedException
	 */
	public static void createInternalOrchestrationPackage(String runtimePath,
			String projectClasspath, String projectName, Map<String,List<String>> orchClassesAndElements,
			List<Dependency> dependencies, IPackageFragmentRoot sourceDir,
			IPackageFragmentRoot generated, IFolder outputFolder,
			IFolder packagesFolder, IProgressMonitor myProgressMonitor, boolean isWar)
			throws CoreException, InterruptedException {
		IFolder classes;
		IFolder lib;
		IFile war;
		String[] orchestrationClasses = orchClassesAndElements.keySet().toArray(
				new String[orchClassesAndElements.keySet().size()]);
		IFolder war_folder = packagesFolder.getFolder(projectName);
		if (war_folder != null && war_folder.exists()) {
			war_folder.delete(true, myProgressMonitor);
		}
		war_folder.create(true, true, myProgressMonitor);
		if (isWar){
			log.debug("Generating service package");
			IFolder web_inf = war_folder.getFolder("WEB-INF");
			web_inf.create(true, true, myProgressMonitor);
			IFile web_xml = web_inf.getFile("web.xml");
			InputStream web_info = generateWebInfo(projectName, orchestrationClasses);
			web_xml.create(web_info, true, myProgressMonitor);
			IFile sun_jaxws = web_inf.getFile("sun-jaxws.xml");
			InputStream jaxws = generateJAXWSInfo(projectName, orchestrationClasses);
			sun_jaxws.create(jaxws, true, myProgressMonitor);
			classes = web_inf.getFolder("classes");
			classes.create(true, true, myProgressMonitor);
			lib = web_inf.getFolder("lib");
			if (!lib.exists()) {
				lib.create(true, true, myProgressMonitor);
			}
			war = packagesFolder.getFile(projectName + ".war");
		}else{
			classes = war_folder;
			lib = war_folder.getFolder("lib");
			if (!lib.exists()) {
				lib.create(true, true, myProgressMonitor);
			}
			war = packagesFolder.getFile(projectName + ".jar");
		}
		// IPackageFragment[] packageFragments =
		// getOrchestrationPackageFragments(cls, source_dir);
		IPackageFragment[] packageFragments = getAllPackageFragments(sourceDir);
		copyClasses(packageFragments, sourceDir.getPath(), outputFolder,
				classes, myProgressMonitor);
		if (isWar){
			if (generated != null) {
				IPackageFragment[] genFragments = getAllPackageFragments(generated);
				copyClasses(genFragments, generated.getPath(), outputFolder,
					classes, myProgressMonitor);
			}
		}
		instrumentOrchestrations(runtimePath, orchestrationClasses, classes, dependencies, myProgressMonitor);
		if (isWar){
			generateServiceStubs(projectClasspath, orchestrationClasses, classes, myProgressMonitor);
			copyJaxWSLibraries(lib);
		}
		manageDependencies(dependencies, projectName, packagesFolder, classes, lib,
				myProgressMonitor);
		copyOrchestrationRuntimeFiles(runtimePath, lib);
		createJar(war, war_folder, myProgressMonitor);
		war_folder.delete(true, myProgressMonitor);

	}
	
	/** Creates an element package for the front-end which includes all the internal 
	 * Orchestration Elements of a package.
	 * 
	 * Create the service stubs, instrument the classes which contains Orchestration elements, 
	 * manage dependencies, copy programming model runtime files and generate a war package
	 * 
	 * @param runtimePath
	 * @param projectClasspath
	 * @param projectName
	 * @param orchestrationClasses
	 * @param dependencies
	 * @param sourceDir
	 * @param generated
	 * @param outputFolder
	 * @param packagesFolder
	 * @param myProgressMonitor
	 * @param isWar
	 * @throws Exception 
	 */
	public static void createExternalOrchestrationPackage(String runtimePath,
			String projectClasspath, String packageName, Map<String,List<String>> orchClassesAndElements,
			IPackageFragmentRoot sourceDir,	IPackageFragmentRoot generated, IFolder outputFolder,
			IFolder packagesFolder, IProgressMonitor myProgressMonitor,	ProjectMetadata prMeta)	
					throws Exception {
		
		IFolder extPacksFolder = packagesFolder.getFolder(ProjectMetadata.EXTERNAL_PACKS_FOLDER);
		if (extPacksFolder == null || !extPacksFolder.exists()) {
			extPacksFolder.create(true, true, myProgressMonitor);
		}
		IFolder packFolder = extPacksFolder.getFolder(packageName);
		if (packFolder == null || !packFolder.exists()) {
			packFolder.create(true, true, myProgressMonitor);
		}
		Map<String,List<String>> packBuilding = new HashMap<String,List<String>>();
		for(Entry<String,List<String>>entry:orchClassesAndElements.entrySet()){
			List<Dependency> dependencies = prMeta.getDependencies(
					entry.getValue().toArray(new String[entry.getValue().size()]));
			
			IFolder classes = checkPackageBuilding(entry.getKey(), packBuilding, prMeta, packFolder,
					dependencies, myProgressMonitor);
			processOrchestrationClassForExternalPackage(entry.getKey(), entry.getValue(), sourceDir, outputFolder, classes, 
					runtimePath, dependencies, myProgressMonitor);
		}
		for(Entry<String,List<String>>entry:packBuilding.entrySet()){
			processPackageBuilding(entry.getKey(), entry.getValue(), runtimePath, prMeta, packFolder, myProgressMonitor);
		}
	}

	private static IFolder checkPackageBuilding(String orchClass,
			Map<String, List<String>> packBuilding, ProjectMetadata prMeta,
			IFolder packFolder, List<Dependency> dependencies, IProgressMonitor monitor) 
					throws Exception {
		OrchestrationClass oe = prMeta.getOrchestrationClass(orchClass);
		String externalLocation = oe.getExternalLocation();
		String libraryLocation = oe.getLibraryLocation();
		IFolder importedFolder = packFolder.getProject().getProject().
				getFolder(ProjectMetadata.IMPORT_FOLDER);
		String importedPath = importedFolder.getRawLocation().toOSString();
		if (externalLocation.startsWith(importedPath)){
			Dependency dep = prMeta.getParentImportedDependency(
					externalLocation, importedPath);
			libraryLocation = externalLocation;
			externalLocation = dep.getLocation();
			
		}
			
		List<String> sps;
		if (packBuilding.containsKey(externalLocation)){
			sps = packBuilding.get(externalLocation);
		}else{
			sps = new ArrayList<String>();
			packBuilding.put(externalLocation, sps);
		}	
		if (libraryLocation!=null && libraryLocation.endsWith(".jar")){
			if (!sps.contains(libraryLocation)){
					sps.add(libraryLocation);
			}
			return processPackageExtraction(importedFolder, packFolder, libraryLocation, monitor);
			
		}else{
			IFolder folder = processPackageExtraction(importedFolder, packFolder, externalLocation, monitor);
			IFolder lib = folder.getFolder("WEB-INF").getFolder("lib");
			getDependenciesFormLibraryFolder(dependencies,
					lib.getRawLocation().toFile());
			IFolder classFolder = folder.getFolder("WEB-INF").getFolder("classes");
			if (classFolder.exists())
				return classFolder;
			else
				throw(new Exception("Class folder not found"));
		}
	}

	private static IFolder processPackageExtraction(IFolder importedFolder,
			IFolder packFolder, String libraryLocation, IProgressMonitor monitor) 
					throws Exception {
		String name = getPackageName(libraryLocation);
		IFolder destFolder = packFolder.getFolder(name);
		if (!destFolder.exists()){
			IFolder sourceFolder = importedFolder.getFolder(name);
			if (sourceFolder.exists()){
				FileUtils.copyDirectory(sourceFolder.getRawLocation().toFile(), 
						destFolder.getRawLocation().toFile());
			}else{
				IFolder tmp = packFolder.getFolder("tmp");
				tmp.create(true, true, monitor);
				extractZip(new File(libraryLocation),destFolder.getRawLocation().toOSString(),
						tmp,monitor);
				if (tmp.exists())
					tmp.delete(true, monitor);
			}
			packFolder.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
		return destFolder;
	}

	private static void processPackageBuilding(String pack, List<String> subPacks, String runtimePath,
			ProjectMetadata prMeta, IFolder packFolder, IProgressMonitor myProgressMonitor) 
					throws CoreException {
		try{
			for(String sp:subPacks){
				IFolder spFolder = packFolder.getFolder(getPackageName(sp));
				if (spFolder!=null && spFolder.exists())	{
					//TODO add runtimeLibraries
					log.warn("Jar packages not supported");
					IFile spFile = packFolder.getFile(getPackageNameWithExtension(sp));
					createJar(spFile, spFolder, myProgressMonitor);
					spFolder.delete(true, myProgressMonitor);
					FileUtils.copyFile(spFile.getRawLocation().toFile(), new File(sp));
				}else{
					throw(new CoreException(new Status(
						IStatus.ERROR, Activator.PLUGIN_ID, "Subpackage "+ sp +" not found")));
				}
			}
			IFolder spFolder = packFolder.getFolder(getPackageName(pack));
			if (spFolder!=null && spFolder.exists())	{	
				
				if (pack.endsWith(".jar")){
					//TODO Add runtime libraries
					log.warn("Jar packages not supported");
				}else{
					IFolder lib = spFolder.getFolder("WEB-INF").getFolder("lib");
					copyOrchestrationRuntimeFiles(runtimePath, lib);
				}
				IFile spFile = packFolder.getFile(getPackageNameWithExtension(pack));
				createJar(spFile, spFolder, myProgressMonitor);
				spFolder.delete(true, myProgressMonitor);
			}else{
				throw (new CoreException(new Status(
					IStatus.ERROR, Activator.PLUGIN_ID, "Package "+ pack +" not found")));
			}
			
		}catch (Exception e){
			if (e instanceof CoreException ){
				throw((CoreException)e);
			}else{
				CoreException ce = new CoreException(new Status(
					IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
				ce.setStackTrace(e.getStackTrace());
				throw (ce);
			}	
		}
	}

	private static void processOrchestrationClassForExternalPackage(String orchClass,
			List<String> methods, IPackageFragmentRoot sourceDir, IFolder outFolder, IFolder classes, String runtimePath,
			List<Dependency> dependencies, IProgressMonitor myProgressMonitor) 
					throws CoreException {
		IPackageFragment pf = sourceDir.getPackageFragment(Signature.getQualifier(orchClass));
		copyInterface(Signature.getSimpleName(orchClass), pf, sourceDir.getPath(), outFolder, classes, myProgressMonitor);
		preInstrumentOrchestration(runtimePath, orchClass, methods, classes, 
				dependencies, myProgressMonitor);
		instrumentOrchestrations(runtimePath, new String[]{orchClass}, classes, 
				dependencies, myProgressMonitor);
	}

	private static List<Dependency> getDependenciesFormLibraryFolder(List<Dependency> deps, File file) {
		if (file.isDirectory()){
			log.debug("Setting dependencies from dir "+  file.getAbsolutePath());
			File[] files = file.listFiles(new FilenameFilter(){
		        @Override
		        public boolean accept(File dir, String name) {
		            return name.endsWith(".jar"); // or something else
		        }});
			for (File f : files) {
				Dependency d = new Dependency(f.getAbsolutePath(),ProjectMetadata.JAR_DEP_TYPE,false);
				deps.add(d);
			}
		}else{
			log.debug("Setting dependencies from file "+  file.getAbsolutePath());
			if (file.getName().endsWith(".jar")){
				Dependency d = new Dependency(file.getAbsolutePath(),ProjectMetadata.JAR_DEP_TYPE,false);
				deps.add(d);
			}
		}
		return deps;
	}

	private static void preInstrumentOrchestration(String runtime,
			String orchClass, List<String> methods, IFolder classes,
			List<Dependency> depLibraries, IProgressMonitor myProgressMonitor) throws CoreException {
		Runtime rt = Runtime.getRuntime();
		if (runtime != null && orchClass != null 
				&& methods!= null && methods.size()>0) {
			String classpath = new String();
			for (Dependency d : depLibraries) {
				if (d.getType().equalsIgnoreCase(ProjectMetadata.JAR_DEP_TYPE)
						|| d.getType().equalsIgnoreCase(ProjectMetadata.CLASS_FOLDER_DEP_TYPE))
					classpath = classpath.concat(":" + d.getLocation());
			}
			boolean first = true;
			String methodsString = new String();
			for(String m:methods){
				if (first){
					methodsString = methodsString.concat(m);
					first = false;
				}else
					methodsString = methodsString.concat(" "+m);
			}
			String command = new String(runtime
					+ "/../scripts/pre_instrument.sh " + classes.getLocation().toOSString() + 
					classpath + " "	+ runtime + "/.." + " "	+ classes.getLocation().toOSString() + " "+
					orchClass + " " + methodsString);
			log.debug("Command to exec: " + command);
			Process ps;
			try {
				ps = rt.exec(command);
				BufferedReader stdInput = new BufferedReader(
						new InputStreamReader(ps.getInputStream()));

				BufferedReader stdError = new BufferedReader(
						new InputStreamReader(ps.getErrorStream()));
				String s = null;
				// read the output from the command
				log.debug("Here is the standard output of the command:\n");
				while ((s = stdInput.readLine()) != null) {
					log.debug(s);
				}

				// read any errors from the attempted command
				log.debug("Here is the standard error of the command (if any):\n");
				while ((s = stdError.readLine()) != null) {
					log.debug(s);
				}

				// if (ps.exitValue() != 0){
				if (ps.waitFor() != 0) {
					throw (new CoreException(new Status(
							IStatus.ERROR, Activator.PLUGIN_ID, "metadata info not found")));
				}
			} catch (IOException e) {
				CoreException ce = new CoreException(new Status(
						IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
				ce.setStackTrace(e.getStackTrace());
				throw (ce);
			} catch (InterruptedException e) {
				CoreException ce = new CoreException(new Status(
						IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
				ce.setStackTrace(e.getStackTrace());
				throw (ce);
			}
		} else {
			throw (new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"metadata info not found")));
		}
	}

	/** Get all java packages from a source folder
	 * @param sourceDir Java source folder
	 * @return Array of packages included in the source folder
	 */
	private static IPackageFragment[] getAllPackageFragments(
			IPackageFragmentRoot sourceDir) {
		ArrayList<IPackageFragment> frags = new ArrayList<IPackageFragment>();
		try {
			IJavaElement[] els = sourceDir.getChildren();
			if (els != null) {
				for (IJavaElement e : els) {
					if (e.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
						ICompilationUnit[] cus = ((IPackageFragment) e)
								.getCompilationUnits();
						if (cus != null && cus.length > 0) {
							frags.add((IPackageFragment) e);
						}
					}
				}
			} else {
				log.warn("There are no children in generated type");
			}
		} catch (JavaModelException e1) {
			log.error("Error getting children",e1);
			e1.printStackTrace();
		}
		return frags.toArray(new IPackageFragment[frags.size()]);
	}

	/** Copy the clases in java package fragments to a classes folder
	 * @param fragments Package fragments which classes are going to be copied 
	 * @param sourceDir Java source folder where source code of the classes is located
	 * @param outFolder Folder where Java project outputs are located (including compiled classes)
	 * @param destClassFolder Class folder in the elements package 
	 * @param monitor Eclipse monitor where progress is notified
	 * @throws CoreException
	 */
	private static void copyClasses(IPackageFragment[] fragments,
			IPath sourceDir, IFolder outFolder, IFolder destClassFolder,
			IProgressMonitor monitor) throws CoreException {
		if (fragments != null && fragments.length > 0) {
			for (IPackageFragment pf : fragments) {
				log.debug("Fragment " + pf.getElementName()
						+ " path: " + pf.getPath());
				IPath path = pf.getPath().makeRelativeTo(sourceDir);
				IFolder last = destClassFolder;

				log.debug("Creating " + (path.segmentCount() - 1)
						+ " folders");
				if (path.segmentCount() > 0) {
					for (int i = 0; i < (path.segmentCount() - 1); i++) {
						log.debug("Creating folder " + path.segment(i)
								+ " in " + last.getLocation().toString());
						IFolder folder = last.getFolder(path.segment(i));
						if (folder == null || !folder.exists()) {
							folder.create(true, true, monitor);
						}
						last = folder;
					}
					IFolder srcClassFolder = outFolder.getFolder("classes").getFolder(
							path);
					IFolder next = last.getFolder(srcClassFolder.getFullPath()
							.lastSegment());
					if (next != null && next.exists()) {
						log.debug("Folder " + next.getFullPath()
								+ " already exists");
						ICompilationUnit[] cus = pf.getCompilationUnits();
						for (ICompilationUnit cu : cus) {
							if (cu != null && cu.exists()) {
								String name = cu.getPath().lastSegment()
										.replace(".java", ".class");
								IFile f = srcClassFolder.getFile(name);
								if (!f.exists()) {
									log.debug("Copying file "
											+ f.getFullPath() + " to "
											+ next.getFullPath().append(name));
									f.copy(next.getFullPath().append(name),
											true, monitor);
								}
							}
						}
					} else {
						log.debug("Copying folder "
								+ srcClassFolder.getFullPath()
								+ " in "
								+ last.getFullPath().append(
										srcClassFolder.getFullPath().lastSegment()));
						srcClassFolder.copy(
								last.getFullPath().append(
										srcClassFolder.getFullPath().lastSegment()),
								false, monitor);
					}
				}
			}
		}
	}
	
	private static void copyInterface(String orchClass, IPackageFragment pf,
			IPath sourceDir, IFolder outFolder, IFolder destClassFolder,
			IProgressMonitor monitor) throws CoreException {
		log.debug("Fragment " + pf.getElementName()
				+ " path: " + pf.getPath());
		IPath path = pf.getPath().makeRelativeTo(sourceDir);
		IFolder last = destClassFolder;

		log.debug("Creating " + (path.segmentCount() - 1)
				+ " folders");
		if (path.segmentCount() > 0) {
			for (int i = 0; i < (path.segmentCount() - 1); i++) {
				IFolder folder = last.getFolder(path.segment(i));
				if (folder == null || !folder.exists()) {
					log.debug("Creating folder " + path.segment(i)
						+ " in " + last.getLocation().toString());
					folder.create(true, true, monitor);
				}
				last = folder;
			}
			outFolder.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			IFolder srcClassFolder = outFolder.getFolder("classes").getFolder(
					path);
			if (srcClassFolder!= null && srcClassFolder.exists()){
				IFolder next = last.getFolder(srcClassFolder.getFullPath()
					.lastSegment());
				if (next != null && next.exists()) {
					String name = orchClass+"Itf.class";
					IFile f = srcClassFolder.getFile(name);
					if (f.exists()) {
						log.debug("Copying file " + f.getFullPath() + " to "
								+ next.getFullPath().append(name));
						f.copy(next.getFullPath().append(name),	true, monitor);
					}else
						throw(new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,"File " +f.getFullPath()+" not found")));
				} else {
					throw(new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,"Folder " +next.getFullPath()+" not found")));
				}
			} else {
				throw(new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,"Folder " +srcClassFolder.getFullPath()+ " not found")));
			}
		}
	}
	

	/** Extract a zip file
	 * @param zipFile File compressed in zip format
	 * @param destDir Path of the folder where the zip file is going to be extracted
	 * @param tmpFolder Folder to write temporal files ( ant's build.xml)
	 * @param myProgressMonitor Eclipse progress monitor
	 * @throws IOException
	 * @throws CoreException
	 * @throws InterruptedException
	 */
	public static void extractZip(File zipFile, String destDir,
			IFolder tmpFolder, IProgressMonitor myProgressMonitor)
			throws IOException, CoreException, InterruptedException {
		log.info("Extracting "+ zipFile.getAbsolutePath() + " to " + destDir);
		IFile buildFile = createUnzipBuildFile(zipFile, destDir, tmpFolder,
				myProgressMonitor);
		runAnt(buildFile.getLocation().toOSString(), new String[] { "unzip" },
				myProgressMonitor);
		buildFile.delete(true, myProgressMonitor);
		File f = new File(destDir);
	}

	/** Creates the Ant build file for extracting a zip 
	 * @param zipFile File compressed in zip format
	 * @param destDir Path of the folder where the zip file is going to be extracted
	 * @param tmpFolder Folder to write temporal files ( ant's build.xml)
	 * @param myProgressMonitor Eclipse progress monitor@param zipFile
	 * @return ant's build file
	 * @throws CoreException
	 */
	private static IFile createUnzipBuildFile(File zipFile, String destDir,
			IFolder tmpFolder, IProgressMonitor myProgressMonitor)
			throws CoreException {

		IFile build = tmpFolder.getFile("buildStubs.xml");
		if (build.exists()) {
			build.delete(true, myProgressMonitor);
		}
		StringBuffer str = new StringBuffer();
		str.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		str.append("<project name=\"unzip\" default=\"\">\n");
		str.append("\t<target name=\"unzip\">\n");
		str.append("\t\t<unzip src=\"" + zipFile.getAbsolutePath()
				+ "\" dest=\"" + destDir + "\">\n");
		// str.append("\t\t\tclasspath=\""+
		// classes.getLocation().toOSString()+"\" resourcedestdir=\""+
		// classes.getLocation().toOSString() +"\" > \n");
		str.append("\t\t</unzip>\n");
		str.append("\t\t<chmod dir=\"" + destDir
				+ "\" perm=\"+x\" includes=\"**/*\">\n");
		str.append("\t\t</chmod>\n");
		str.append("\t</target> \n");
		str.append("</project>\n");
		String info = new String(str);
		log.debug("build file: " + info);
		build.create(new ByteArrayInputStream(info.getBytes()), true,
				myProgressMonitor);
		return build;
	}


	/** Add the programming model configuration file to a jar/war file
	 * @param jarFile The jar/war file
	 * @param properties The programming model runtime configuration file
	 * @param tmpFolder Folder to crate temporal files
	 * @param monitor Eclipse progress monitor
	 * @throws IOException
	 * @throws CoreException
	 * @throws InterruptedException
	 */
	public static void addRuntimeConfigTojar(IFile jarFile, File properties,
			IFolder tmpFolder, String classesPath, IProgressMonitor monitor) throws IOException,
			CoreException, InterruptedException {
		IFolder folder = tmpFolder.getFolder("tmp_folder");
		if (folder != null && folder.exists()) {
			folder.delete(true, monitor);
		}
		folder.create(true, true, monitor);
		updateJar(jarFile, folder, new File[] { properties },
				classesPath, monitor);
		folder.delete(true, monitor);

	}

	/** Update a jar/war file by including a set of files to a certain jar/war relative folder 
	 * @param jarFile The jar/war file
	 * @param tmpFolder Folder to crate temporal files
	 * @param includefiles Array of files to be included in the jar/war file
	 * @param path relative path where files are included in the jar/war
	 * @param myProgressMonitor Eclipse progress monitor
	 * @throws CoreException
	 * @throws InterruptedException
	 */
	public static void updateJar(IFile jarFile, IFolder tmpFolder,
			File[] includefiles, String path, IProgressMonitor myProgressMonitor)
			throws CoreException, InterruptedException {
		IFile buildFile = createUpdateJarBuildFile(jarFile, includefiles, tmpFolder,
				path, myProgressMonitor);
		runAnt(buildFile.getLocation().toOSString(), new String[] { "jar" },
				myProgressMonitor);
		buildFile.delete(true, myProgressMonitor);

	}
	/** Create a jar file
	 * @param file Name of the file
	 * @param folder Folder to package as a jar
	 * @param myProgressMonitor Eclipse progress monitor 
	 * @throws CoreException
	 * @throws InterruptedException
	 */
	public static void createJar(IFile file, IFolder folder,
			IProgressMonitor myProgressMonitor) throws CoreException,
			InterruptedException {
		IFile buildFile = createJarBuildFile(file, folder, myProgressMonitor);
		runAnt(buildFile.getLocation().toOSString(), new String[] { "jar" },
				myProgressMonitor);
		buildFile.delete(true, myProgressMonitor);
	}

	
	/** Run an ant process
	 * @param buildFile Ant build file
	 * @param target Project targets to be execute in the Ant process
	 * @param myProgressMonitor Eclipse progress monitor
	 * @throws CoreException
	 * @throws InterruptedException
	 */
	private static void runAnt(String buildFile, String[] target,
			IProgressMonitor myProgressMonitor) throws CoreException,
			InterruptedException {
		AntRunner antRun = new AntRunner();
		antRun.setBuildFileLocation(buildFile);
		antRun.setExecutionTargets(target);
		try {
			antRun.run(myProgressMonitor);
		} catch (CoreException e) {
			new File(buildFile).delete();
			throw e;
		}
	}

	/** Create an Ant build file to zip a folder
	 * @param file Zip file name
	 * @param folder Folder to zip
	 * @param myProgressMonitor Eclipse progress monitor 
	 * @return Ant build file
	 * @throws CoreException
	 */
	private static IFile createZipBuildFile(IFile file, IFolder folder,
			IProgressMonitor myProgressMonitor) throws CoreException {
		IFile build = folder.getFile("buildStubs.xml");
		if (build.exists()) {
			build.delete(true, myProgressMonitor);
		}
		StringBuffer str = new StringBuffer();
		str.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		str.append("<project name=\"zip\" default=\"\">\n");

		str.append("\t<target name=\"zip\">\n");
		str.append("\t\t<zip destfile=\"" + file.getLocation().toOSString()
				+ "\" basedir=\"" + folder.getLocation().toOSString()
				+ "\" excludes= \"buildStubs.xml\">\n");
		// str.append("\t\t\tclasspath=\""+
		// classes.getLocation().toOSString()+"\" resourcedestdir=\""+
		// classes.getLocation().toOSString() +"\" > \n");
		str.append("\t\t</zip>\n");
		str.append("\t</target> \n");
		str.append("</project>\n");
		String info = new String(str);
		log.debug("build file: " + info);
		build.create(new ByteArrayInputStream(info.getBytes()), true,
				myProgressMonitor);
		return build;
	}
	
	/** Create an Ant build file to package a folder a jar file
	 * @param file JAR file name
	 * @param folder Folder to package as a jar
	 * @param myProgressMonitor Eclipse progress monitor 
	 * @return Ant build file
	 * @throws CoreException
	 */
	private static IFile createJarBuildFile(IFile file, IFolder folder,
			IProgressMonitor myProgressMonitor) throws CoreException {
		IFile build = folder.getFile("buildStubs.xml");
		if (build.exists()) {
			build.delete(true, myProgressMonitor);
		}
		StringBuffer str = new StringBuffer();
		str.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		str.append("<project name=\"jar\" default=\"\">\n");

		str.append("\t<target name=\"jar\">\n");
		str.append("\t\t<jar destfile=\"" + file.getLocation().toOSString()
				+ "\" basedir=\"" + folder.getLocation().toOSString()
				+ "\" excludes= \"buildStubs.xml\">\n");
		// str.append("\t\t\tclasspath=\""+
		// classes.getLocation().toOSString()+"\" resourcedestdir=\""+
		// classes.getLocation().toOSString() +"\" > \n");
		str.append("\t\t</jar>\n");
		str.append("\t</target> \n");
		str.append("</project>\n");
		String info = new String(str);
		log.debug("build file: " + info);
		build.create(new ByteArrayInputStream(info.getBytes()), true,
				myProgressMonitor);
		return build;
	}

	/** Creates an Ant build file to update jar
	 * @param file JAR file name
	 * @param files Files to include in the update
	 * @param tmpFolder TFolder to crate the build file
	 * @param relPath relative path where the new files are included
	 * @param myProgressMonitor Eclipse progress monitor.
	 * @return Ant build file
	 * @throws CoreException
	 */
	private static IFile createUpdateJarBuildFile(IFile file, File[] files,
			IFolder tmpFolder, String relPath, IProgressMonitor myProgressMonitor)
			throws CoreException {
		IFile build = tmpFolder.getFile("buildStubs.xml");
		if (build.exists()) {
			build.delete(true, myProgressMonitor);
		}
		StringBuffer str = new StringBuffer();
		str.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		str.append("<project name=\"jar\" default=\"\">\n");

		str.append("\t<target name=\"jar\">\n");
		str.append("\t\t<jar destfile=\"" + file.getLocation().toOSString()
				+ "\" update = \"true\">\n");
		for (File f : files) {
			str.append("\t\t\t <zipfileset dir=\""
					+ f.getAbsolutePath().replace(f.getName(), "")
					+ "\" includes=\"" + f.getName() + "\" prefix=\"" + relPath
					+ "\"/>\n");
		}
		str.append("\t\t</jar>\n");
		str.append("\t</target> \n");
		str.append("</project>\n");
		String info = new String(str);
		log.debug("build file: " + info);
		build.create(new ByteArrayInputStream(info.getBytes()), true,
				myProgressMonitor);
		return build;
	}

	/** Copy runtime files for a package of Orchestration Elements.
	 * @param runtime Path to the runtime installation
	 * @param lib Package lib folder
	 * @throws CoreException
	 */
	private static void copyOrchestrationRuntimeFiles(String runtime,
			IFolder lib) throws CoreException {
		File lib_dir = new File(runtime + "/lib");
		File web_inf_lib = lib.getLocation().toFile();
		List<File> dirs = getRuntimeLibrariesDirs(lib_dir);
		for (File d : dirs) {
			if (d.isDirectory()) {
				Iterator<File> fi = FileUtils.iterateFiles(d,
						new String[] { "jar" }, false);
				while (fi.hasNext()) {
					File f = fi.next();
					if (!isFileInDiscardList(f)) {
						try {
							// System.out.println("Trying to copy File "+
							// f.getAbsolutePath());
							FileUtils.copyFileToDirectory(f, web_inf_lib);
							log.debug(" File copied "
									+ f.getAbsolutePath());
						} catch (IOException e) {
							log.error("File " + f.getAbsolutePath()
									+ "could not be copied to "
									+ web_inf_lib.getAbsolutePath(),e);
						}
					}
				}
			} else
				log.warn("File " + d.getAbsolutePath()
						+ "is not a directory");
		}

	}

	/** Delete a folder
	 * @param path Folder to remove
	 */
	public static void deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		path.delete();
	}
	
	/** Check if a file from a runtime libraries must not be copied to the package 
	 * @param f File to check
	 * @return True is it is in the list, false if not.
	 */
	private static boolean isFileInDiscardList(File f) {
		List<String> discardList = getDiscardList();
		return discardList.contains(f.getName());
	}

	/** Get the list of files to be discarded when 
	 * @return list of file names.
	 */
	private static List<String> getDiscardList() {
		// TODO get list from another place
		List<String> discardList = new LinkedList<String>();
		discardList.add("jsr311-api-1.1.1.jar");
		return discardList;
	}

	/** Get the directories of the runtime libraries which must be copied to the orchestration element packages
	 * @param lib_dir
	 * @return
	 */
	private static List<File> getRuntimeLibrariesDirs(File lib_dir) {
		String[] str = new String[] { ".", "log4j", "xalan", "javassist",
				"emotive", "emotive/jersey", "amazon", "cxf", "gat", "apache",
				"optimis" };
		LinkedList<File> l = new LinkedList<File>();
		for (String s : str) {
			File f = new File(lib_dir.getAbsolutePath() + "/" + s);
			l.add(f);
		}
		return l;
	}

	/** Copy JaxWS libraries to the front-end package
	 * @param lib front-end package library folder
	 */
	private static void copyJaxWSLibraries(IFolder lib) {
		File web_inf_lib = lib.getLocation().toFile();
		Bundle b = Platform.getBundle(BUNDLE_NAME);

		try {
			// TODO: Check if correct when installed
			String s = FileLocator.getBundleFile(b).getAbsolutePath()
					+ "/lib/jaxws/";
			File d = new File(s);
			if (d.isDirectory()) {
				Iterator<File> fi = FileUtils.iterateFiles(d,
						new String[] { "jar" }, false);
				while (fi.hasNext()) {
					File f = fi.next();
					try {
						FileUtils.copyFileToDirectory(f, web_inf_lib);
					} catch (IOException e) {
						log.error("File " + f.getAbsolutePath()
								+ "could not be copied to "
								+ web_inf_lib.getAbsolutePath(),e);
					}

				}
			} else {
				log.warn("File " + d.getAbsolutePath()
						+ " is not a directory");
			}
		} catch (IOException e) {
			log.error("Failing to locate bundle file",e);
			e.printStackTrace();
		}

	}

	/** Generate the content of the web.xml file of the service
	 * @param projectName Name of the project
	 * @param classes Classes with a Web Service interface.
	 * @return web.xml content
	 */
	private static InputStream generateWebInfo(String projectName,
			String[] classes) {

		StringBuffer str = new StringBuffer();
		str.append("<web-app version=\"2.4\" xmlns=\"http://java.sun.com/xml/ns/j2ee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd\">\n");
		str.append("\t<description>" + projectName + "</description>\n");
		str.append("\t<display-name>" + projectName + "</display-name>\n");
		str.append("\t<listener>\n");
		str.append("\t\t<listener-class>com.sun.xml.ws.transport.http.servlet.WSServletContextListener</listener-class>\n");
		str.append("\t</listener>\n");
		for (String cl : classes) {
			String s = Signature.getSimpleName(cl);
			str.append("\t<servlet>\n");
			str.append("\t\t<description>JAX-WS Endpoint " + s
					+ "</description>\n");
			str.append("\t\t<display-name>" + s + "</display-name>\n");
			str.append("\t\t<servlet-name>" + s + "</servlet-name>\n");
			str.append("\t\t<servlet-class>com.sun.xml.ws.transport.http.servlet.WSServlet</servlet-class>\n");
			str.append("\t\t<load-on-startup>1</load-on-startup>\n");
			str.append("\t</servlet>\n");
			str.append("\t<servlet-mapping>\n");
			str.append("\t\t<servlet-name>" + s + "</servlet-name>\n");
			str.append("\t\t<url-pattern>/" + s + "</url-pattern>\n");
			str.append("\t</servlet-mapping>\n");
		}
		str.append("\t<session-config>\n");
		str.append("\t\t<session-timeout>120</session-timeout>\n");
		str.append("\t</session-config>\n");
		str.append("</web-app>");
		String info = new String(str);
		return new ByteArrayInputStream(info.getBytes());
	}

	/** Get location of the deployment independent project.xml file
	 * @param editor Service editor where the method is invoked
	 * @return Absolute path to this file
	 * @throws CoreException
	 */
	public static String getSourceProjectFileLocation(ServiceFormEditor editor)
			throws CoreException {
		try {
			ProjectMetadata pr_meta = new ProjectMetadata(editor
					.getMetadataFile().getRawLocation().toFile());
			return editor.getProject().getProject().getLocation()
					+ File.separator + File.separator
					+ pr_meta.getMainPackageFolder() + File.separator
					+ "project.xml";
		} catch (Exception e) {
			e.printStackTrace();
			throw (new JavaModelException(e, 0));
		}

	}

	/** Get location of the deployment independent resources.xml file
	 * @param editor Service editor where the method is invoked
	 * @return Absolute path to this file
	 * @throws CoreException
	 */
	public static String getSourceResourcesFileLocation(ServiceFormEditor editor)
			throws CoreException {
		try {
			ProjectMetadata pr_meta = new ProjectMetadata(editor
					.getMetadataFile().getRawLocation().toFile());
			return editor.getProject().getProject().getLocation()
					+ File.separator + File.separator
					+ pr_meta.getMainPackageFolder() + File.separator
					+ "resources.xml";
		} catch (Exception e) {
			e.printStackTrace();
			throw (new JavaModelException(e, 0));
		}
	}

	/** Get location of the deployment dependent project.xml file
	 * @param editor Service editor where the method is invoked
	 * @return Absolute path to this file
	 * @throws CoreException
	 */
	public static String getPackagesProjectFileLocation(ServiceFormEditor editor)
			throws CoreException {
		try {
			ProjectMetadata pr_meta = new ProjectMetadata(editor
					.getMetadataFile().getRawLocation().toFile());
			return editor.getProject().getProject().getLocation()
					+ File.separator + pr_meta.OUTPUT_FOLDER + File.separator
					+ pr_meta.PACKAGES_FOLDER + File.separator + "project.xml";
		} catch (Exception e) {
			e.printStackTrace();
			throw (new JavaModelException(e, 0));
		}

	}

	/** Get location of the deployment dependent resources.xml file
	 * @param editor Service editor where the method is invoked
	 * @return Absolute path to this file
	 * @throws CoreException
	 */
	public static String getPackagesResourcesFileLocation(
			ServiceFormEditor editor) throws CoreException {
		try {
			ProjectMetadata pr_meta = new ProjectMetadata(editor
					.getMetadataFile().getRawLocation().toFile());
			return editor.getProject().getProject().getLocation()
					+ File.separator + pr_meta.OUTPUT_FOLDER + File.separator
					+ pr_meta.PACKAGES_FOLDER + File.separator
					+ "resources.xml";
		} catch (Exception e) {
			e.printStackTrace();
			throw (new JavaModelException(e, 0));
		}
	}

	/* NOT USED Copy configuration Files to the package
	 * @param editor
	 * @throws CoreException
	 
	public static void copyConfigFilesToPackageFolder(ServiceFormEditor editor)
			throws CoreException {
		try {
			ProjectMetadata pr_meta = new ProjectMetadata(editor
					.getMetadataFile().getRawLocation().toFile());
			copyFilesToDir(new String[] {
					getSourceResourcesFileLocation(editor),
					getSourceProjectFileLocation(editor) }, editor.getProject()
					.getProject().getLocation()
					+ File.separator
					+ pr_meta.OUTPUT_FOLDER
					+ File.separator
					+ pr_meta.PACKAGES_FOLDER);
		} catch (Exception e) {
			e.printStackTrace();
			throw (new JavaModelException(e, 0));
		}
	}*/

	/** Copy files to a folder
	 * @param origins Files to copy
	 * @param destination Destination folder
	 * @throws CoreException
	 */
	public static void copyFilesToDir(String[] origins, String destination)
			throws CoreException {
		File d = new File(destination);
		if (d.isDirectory()) {
			for (String s : origins) {
				File f = new File(s);
				try {
					FileUtils.copyFileToDirectory(f, d);
				} catch (IOException e) {
					log.error("File " + f.getAbsolutePath()
							+ "could not be copied to " + d.getAbsolutePath(),e);
					e.printStackTrace();
					throw (new JavaModelException(e, 0));
				}
			}
		} else {
			throw (new JavaModelException(new Exception(destination
					+ " is not a directory"), 0));
		}
	}

	/** Instrument the Orchestration Element classes
	 * @param project Java project
	 * @param classes Orchestration Element classes
	 * @param deps Orchestration Element dependencies
	 * @param myProgressMonitor Eclipse progress monitor
	 * @throws CoreException
	 */
	public static void instrumentOrchestrations(IJavaProject project,
			IFolder classes, List<Dependency> deps,
			IProgressMonitor myProgressMonitor) throws CoreException {
		IFile metadataFile = project.getProject()
				.getFolder(ProjectMetadata.METADATA_FOLDER)
				.getFile(ProjectMetadata.METADATA_FILENAME);
		if (metadataFile != null && metadataFile.exists()) {
			try {
				ProjectMetadata pr_meta = new ProjectMetadata(new File(
						metadataFile.getRawLocation().toOSString()));
				//TODO Chak if should be all orchestration or only internal
				instrumentOrchestrations(pr_meta.getRuntimeLocation(),
						pr_meta.getAllOrchestrationClasses(), classes, deps,
						myProgressMonitor);
				pr_meta.getRuntimeLocation();
			} catch (Exception e) {
				CoreException ce = new CoreException(new Status(
						IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(),e));
				ce.setStackTrace(e.getStackTrace());
				throw (ce);
			}
		} else {
			throw (new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, "metadata file not found")));
		}
	}
	
	private static void instrumentOrchestrations(String runtime, String[] cls,
			IFolder classes, List<Dependency> depLibraries,
			IProgressMonitor myProgressMonitor) throws CoreException {
		Runtime rt = Runtime.getRuntime();
		if (runtime != null && cls != null) {
			String classpath = new String();
			for (Dependency d : depLibraries) {
				if (d.getType().equalsIgnoreCase(ProjectMetadata.JAR_DEP_TYPE)
						|| d.getType().equalsIgnoreCase(
								ProjectMetadata.CLASS_FOLDER_DEP_TYPE))
					classpath = classpath.concat(":" + d.getLocation());
			}
			for (String cl : cls) {
				String command = new String(runtime
						+ "/../scripts/instrument.sh " + cl + " "
						+ classes.getLocation().toOSString() + classpath + " "
						+ runtime + "/.." + " "
						+ classes.getLocation().toOSString());
				log.debug("Command to exec: " + command);
				Process ps;
				try {
					ps = rt.exec(command);
					BufferedReader stdInput = new BufferedReader(
							new InputStreamReader(ps.getInputStream()));

					BufferedReader stdError = new BufferedReader(
							new InputStreamReader(ps.getErrorStream()));
					String s = null;
					// read the output from the command
					log.debug("Here is the standard output of the command:\n");
					while ((s = stdInput.readLine()) != null) {
						log.debug(s);
					}

					// read any errors from the attempted command
					log.debug("Here is the standard error of the command (if any):\n");
					while ((s = stdError.readLine()) != null) {
						log.debug(s);
					}

					// if (ps.exitValue() != 0){
					if (ps.waitFor() != 0) {
						throw (new CoreException(new Status(
								IStatus.ERROR, Activator.PLUGIN_ID, "metadata info not found")));
					}
				} catch (IOException e) {
					CoreException ce = new CoreException(new Status(
							IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
					ce.setStackTrace(e.getStackTrace());
					throw (ce);
				} catch (InterruptedException e) {
					CoreException ce = new CoreException(new Status(
							IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
					ce.setStackTrace(e.getStackTrace());
					throw (ce);
				}

			}
		} else {
			throw (new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"metadata info not found")));
		}

	}
	/** Generate the content of config file for the jaxws runtime
	 * @param projectName Name of the java project
	 * @param classes Classes implementing JaxWS services
	 * @return jaxws.xml file content
	 */
	private static InputStream generateJAXWSInfo(String projectName,
			String[] classes) {
		StringBuffer str = new StringBuffer();
		str.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		str.append("<endpoints xmlns='http://java.sun.com/xml/ns/jax-ws/ri/runtime' version='2.0'>\n");

		for (String cl : classes) {
			String s = Signature.getSimpleName(cl);
			str.append("\t<endpoint name='" + s + "' implementation='" + cl
					+ "' url-pattern='/" + s + "'/>\n");
		}
		str.append("</endpoints>\n");
		String info = new String(str);
		return new ByteArrayInputStream(info.getBytes());
	}

	

	/** Get the classpath of the project
	 * @param project
	 * @return
	 * @throws JavaModelException
	 */
	public static String getClasspath(IJavaProject project)
			throws JavaModelException {
		String classpath = new String();
		IPath path = project.getProject().getWorkspace().getRoot()
				.getLocation();
		boolean first = true;
		for (IClasspathEntry e : project.getResolvedClasspath(true)) {
			if (!first) {
				classpath = classpath.concat(":");

			} else {
				first = false;
			}
			if (e.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				IPath entryPath = e.getPath();
				path.isPrefixOf(entryPath);
				classpath = classpath.concat(path.append(
						entryPath.makeRelative()).toOSString());
			} else {
				classpath = classpath.concat(e.getPath().toOSString());
			}
		}
		return classpath;
	}

	/** Generate the WS stubs
	 * @param classpath project classpath
	 * @param classes Classes which implement a JAXWS service
	 * @param classFolder Folder to store package classes
	 * @param myProgressMonitor Eclipse progress monitor
	 * @throws CoreException
	 */
	private static void generateServiceStubs(String classpath, String[] classes,
			IFolder classFolder, IProgressMonitor myProgressMonitor)
			throws CoreException {
		String clpth = new String(classFolder.getLocation().toOSString() + ":"
				+ classpath);
		log.debug("Classpath: " + clpth);
		if (classes != null) {
			for (String cl : classes) {
				int i = -1;
				try {

					i = WsGen.doMain(new String[] { "-verbose", "-cp", clpth,
							cl, "-d", classFolder.getLocation().toOSString(),
							"-wsdl" });

				} catch (Throwable e) {
					CoreException ce = new CoreException(new Status(
							IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(),e));
					e.printStackTrace();
					ce.setStackTrace(e.getStackTrace());
					throw (ce);
				}
				if (i != 0) {
					throw (new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							"Error generating service stubs")));
				}

			}
		} else {
			throw (new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Classes not found exists")));
		}

	}

	/** Deploy Core Element packages in a set of remote host.
	 * @param workers Array of remote host descriptions
	 * @param jars JAR packages to be deployed in the remote hosts
	 * @param zips Zips file to be deployed in the remote hosts
	 * @param folder Folder to create temporal files
	 * @param monitor Eclipse progress monitor
	 * @throws CoreException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void deployWorkers(Element[] workers, String[] jars,
			String[] zips, IFolder folder, IProgressMonitor monitor)
			throws CoreException, InterruptedException, IOException {
		for (Element master : workers) {
			String hostname = master
					.getAttribute(GridResourcesFile.HOSTNAME_ATTR);
			String username = master
					.getAttribute(GridResourcesFile.USERNAME_ATTR);
			String installPath = master
					.getAttribute(GridResourcesFile.INSTALL_PATH_ATTR);
			IFile buildFile = createWorkerDeployBuildFile(hostname, username,
					installPath, jars, zips, folder, monitor);
			monitor.subTask("Deploying core elements in " + hostname);
			runAnt(buildFile.getLocation().toOSString(), new String[] { "jsch",
					"worker_deploy" }, monitor);
			buildFile.delete(true, monitor);
		}

	}

	/** Create an Ant build file for a deployment of the core element packages in a remote host (worker)
	 * @param hostname Hostname/IP address of the remote host
	 * @param username Username in the remote host
	 * @param installPath Path to install the packages
	 * @param jars JAR packages to be deployed in the remote host
	 * @param zips ZIP file to be extracted in the remote host
	 * @param folder Folder to create temporal files
	 * @param myProgressMonitor Eclipse progress monitor
	 * @return Ant build file
	 * @throws CoreException
	 * @throws IOException
	 */
	private static IFile createWorkerDeployBuildFile(String hostname,
			String username, String installPath, String[] jars, String[] zips,
			IFolder folder, IProgressMonitor myProgressMonitor)
			throws CoreException, IOException {
		String key_file = "/home/" + System.getProperty("user.name")
				+ File.separator + ".ssh/id_dsa";
		String ant_folder = "/home/" + System.getProperty("user.name")
				+ File.separator + ".ant/lib";
		String jsch_file = FileLocator.getBundleFile(
				Platform.getBundle(BUNDLE_NAME)).getAbsolutePath()
				+ "/lib/jsch-0.1.42.jar";
		IFile build = folder.getFile("buildStubs.xml");
		if (build.exists()) {
			build.delete(true, myProgressMonitor);
		}
		StringBuffer str = new StringBuffer();
		str.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		str.append("<project name=\"worker_deploy\" default=\"\">\n");
		str.append("\t<target name=\"jsch\">\n");
		str.append("\t\t<copy file=\"" + jsch_file + "\" todir=\"" + ant_folder
				+ "\"/>\n");
		str.append("\t</target>\n");
		str.append("\t<target name=\"worker_deploy\" depends=\"jsch\">\n");
		for (String f : jars) {
			str.append("\t\t<scp file=\"" + f + "\" todir=\"" + username + "@"
					+ hostname + ":" + installPath + "\" keyfile=\"" + key_file
					+ "\" passphrase=\"\" trust=\"true\"/>\n");
			String filename = f.substring(f.lastIndexOf(File.separator) + 1);
			str.append("\t\t<sshexec host=\"" + hostname + "\" username=\""
					+ username + "\" keyfile=\"" + key_file
					+ "\" passphrase=\"\" command=\"cd " + installPath
					+ "; unzip -o " + filename + "; rm " + filename
					+ ";\" trust=\"true\"/>\n");
		}
		for (String z : zips) {
			str.append("\t\t<scp file=\"" + z + "\" todir=\"" + username + "@"
					+ hostname + ":" + installPath + "\" keyfile=\"" + key_file
					+ "\" passphrase=\"\" trust=\"true\"/>");
			String filename = z.substring(z.lastIndexOf(File.separator) + 1);
			str.append("\t\t<sshexec host=\"" + hostname + "\" username=\""
					+ username + "\" keyfile=\"" + key_file
					+ "\" passphrase=\"\" command=\"cd " + installPath
					+ "; unzip -o " + filename + "; rm " + filename
					+ ";\" trust=\"true\"/>");
		}
		str.append("\t\t<sshexec host=\"" + hostname + "\" username=\""
				+ username + "\" keyfile=\"" + key_file
				+ "\" passphrase=\"\" command=\"chmod -R +x " + installPath
				+ ";\" trust=\"true\"/>");
		str.append("\t</target> \n");
		str.append("</project>\n");
		String info = new String(str);
		log.debug("build file: " + info);
		build.create(new ByteArrayInputStream(info.getBytes()), true,
				myProgressMonitor);
		return build;
	}

	/** Create an Ant build file for a deployment of the orchestration element packages in a remote host (worker)
	 * @param hostname Hostname/IP address of the remote host
	 * @param username Username in the remote host
	 * @param installPath Path to install the packages
	 * @param serverPath Path to the installation of the application server in the remote host
	 * @param serviceName Name of the service
	 * @param files Programming model runtime configuration files
	 * @param wars JAR packages to be deployed in the remote host
	 * @param zips ZIP file to be extracted in the remote host
	 * @param folder Folder to create temporal files
	 * @param myProgressMonitor Eclipse progress monitor
	 * @return Ant build file
	 * @throws CoreException
	 * @throws IOException
	 */
	private static IFile createMasterDeployBuildFile(String hostname,
			String username, String installPath, String serverPath,
			String serviceName, String[] files, String[] wars, String[] zips,
			IFolder folder, IProgressMonitor myProgressMonitor)
			throws CoreException, IOException {
		String key_file = "/home/" + System.getProperty("user.name")
				+ File.separator + ".ssh/id_dsa";
		String ant_folder = "/home/" + System.getProperty("user.name")
				+ File.separator + ".ant/lib";
		String jsch_file = FileLocator.getBundleFile(
				Platform.getBundle(BUNDLE_NAME)).getAbsolutePath()
				+ "/lib/jsch-0.1.42.jar";
		IFile build = folder.getFile("buildStubs.xml");
		if (build.exists()) {
			build.delete(true, myProgressMonitor);
		}
		StringBuffer str = new StringBuffer();
		str.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		str.append("<project name=\"master_deploy\" default=\"\">\n");
		str.append("\t<target name=\"jsch\">\n");
		str.append("\t\t<copy file=\"" + jsch_file + "\" todir=\"" + ant_folder
				+ "\"/>\n");
		str.append("\t</target>\n");
		str.append("\t<target name=\"master_deploy\" depends=\"jsch\">\n");
		for (String f : files) {
			str.append("\t\t<scp file=\"" + f + "\" todir=\"" + username + "@"
					+ hostname + ":" + installPath + "\" keyfile=\"" + key_file
					+ "\" passphrase=\"\" trust=\"true\"/>\n");
		}
		for (String w : wars) {
			String filename = w.substring(w.lastIndexOf(File.separator) + 1);
			log.debug("War file: " + filename);
			String name = filename.substring(0, filename.lastIndexOf(".war"));
			log.debug("War file: " + name);
			str.append("\t\t<sshexec host=\"" + hostname + "\" username=\""
					+ username + "\" keyfile=\"" + key_file
					+ "\" passphrase=\"\" command=\"cd " + serverPath
					+ "/webapps/; rm -rf " + name + "; rm -rf " + filename
					+ ";\" trust=\"true\"/>\n");
			str.append("\t\t<scp file=\"" + w + "\" todir=\"" + username + "@"
					+ hostname + ":" + serverPath + "/webapps/"
					+ "\" keyfile=\"" + key_file
					+ "\" passphrase=\"\" trust=\"true\"/>\n");
		}
		for (String z : zips) {
			str.append("\t\t<scp file=\"" + z + "\" todir=\"" + username + "@"
					+ hostname + ":" + installPath + "\" keyfile=\"" + key_file
					+ "\" passphrase=\"\" trust=\"true\"/>\n");
			String filename = z.substring(z.lastIndexOf(File.separator) + 1);
			str.append("\t\t<sshexec host=\"" + hostname + "\" username=\""
					+ username + "\" keyfile=\"" + key_file
					+ "\" passphrase=\"\" command=\"cd " + installPath
					+ "; unzip -o " + filename + "; rm " + filename
					+ ";\" trust=\"true\"/>\n");
		}
		str.append("\t\t<sshexec host=\"" + hostname + "\" username=\""
				+ username + "\" keyfile=\"" + key_file
				+ "\" passphrase=\"\" command=\"" + serverPath
				+ "/bin/catalina.sh start\" trust=\"true\"/>\n");
		str.append("\t</target>\n");
		str.append("</project>\n");
		String info = new String(str);
		log.debug("build file: " + info);
		build.create(new ByteArrayInputStream(info.getBytes()), true,
				myProgressMonitor);
		return build;
	}

	/** Deploy of the orchestration element package to a remote host.
	 * @param master Remote host description
	 * @param serviceName Name of the service
	 * @param files Programming model runtime configuration files
	 * @param wars WAR packages to be deployed in the remote host
	 * @param zips ZIP files to be extracted in the remote host
	 * @param folder Folder to create temporal files
	 * @param monitor Eclipse progress monitor
	 * @throws CoreException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void deployMaster(Element master, String serviceName,
			String[] files, String[] wars, String[] zips, IFolder folder,
			IProgressMonitor monitor) throws CoreException,
			InterruptedException, IOException {
		String hostname = master.getAttribute(GridResourcesFile.HOSTNAME_ATTR);
		String username = master.getAttribute(GridResourcesFile.USERNAME_ATTR);
		String installPath = master
				.getAttribute(GridResourcesFile.INSTALL_PATH_ATTR);
		String serverPath = master
				.getAttribute(GridResourcesFile.SERVER_PATH_ATTR);
		IFile buildFile = createMasterDeployBuildFile(hostname, username,
				installPath, serverPath, serviceName, files, wars, zips,
				folder, monitor);
		monitor.subTask("Deploying front end in " + hostname);
		runAnt(buildFile.getLocation().toOSString(), new String[] { "jsch",
				"master_deploy" }, monitor);
		buildFile.delete(true, monitor);
	}
	
	public static String getPackageName(File warFile) throws Exception{
		String name = warFile.getName();
		if (name!=null&& name.length()>0){
			if (name.endsWith(".war")){
				name = name.substring(0,name.indexOf(".war"));
			}else if (name.endsWith(".jar")){
				name = name.substring(0,name.indexOf(".jar"));
			}else if (name.endsWith(".zip")){
				name = name.substring(0,name.indexOf(".zip"));
			}
			return name;
		}else{
			throw(new Exception(" File name for "+warFile.toString()+" not found"));
		}
		
	}
	
	public static String getPackageName(String warFilePath) throws Exception{
		File warFile = new File(warFilePath);
		if (warFile.exists()){
			return getPackageName(warFile);
		}else{
			throw(new Exception(" File "+warFilePath+" does not exist."));
		}
	}
	
	public static String getPackageNameWithExtension(String warFilePath) throws Exception{
		File warFile = new File(warFilePath);
		if (warFile.exists()){
			return warFile.getName();
		}else{
			throw(new Exception(" File "+warFilePath+" does not exist."));
		}
	}
	

}
