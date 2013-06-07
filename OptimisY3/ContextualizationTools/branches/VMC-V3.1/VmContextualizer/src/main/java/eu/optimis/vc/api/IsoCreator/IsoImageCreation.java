/**
 *  Copyright 2013 University of Leeds
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
package eu.optimis.vc.api.IsoCreator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.log4j.Logger;

import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.vc.api.Core.SystemCall;
import eu.optimis.vc.api.Core.SystemCallException;
import eu.optimis.vc.api.DataModel.ContextData;
import eu.optimis.vc.api.DataModel.GlobalConfiguration;
import eu.optimis.vc.api.DataModel.VirtualMachine;
import eu.optimis.vc.api.DataModel.ContextDataTypes.EndPoint;
import eu.optimis.vc.api.DataModel.ContextDataTypes.LicenseToken;
import eu.optimis.vc.api.DataModel.ContextDataTypes.SecurityKey;
import eu.optimis.vc.api.DataModel.ContextDataTypes.SoftwareDependency;
import eu.optimis.vc.api.DataModel.Image.Iso;

/**
 * Class to create ISO images for containing contextualization data.
 * 
 * @author Django Armstrong (ULeeds)
 * @version 0.0.4
 */
public class IsoImageCreation {

	protected final static Logger log = Logger
			.getLogger(IsoImageCreation.class);

	private Iso iso;
	private GlobalConfiguration configuration;
	private SystemCall systemCall;
	private String isoDataDirectory;

	private Manifest manifest;

	/**
	 * Initialises an instance of the ISO Image creator.
	 * 
	 * @param iso
	 *            The ISO to create.
	 * @param configuration
	 *            Configuration details used when creating the ISO.
	 */
	public IsoImageCreation(Iso iso, GlobalConfiguration configuration, Manifest manifest) {
		this.iso = iso;
		this.configuration = configuration;
		this.manifest = manifest;
		systemCall = new SystemCall(configuration.getInstallDirectory());
		isoDataDirectory = configuration.getContextDataDirectory() + File.separator
				+ iso.getFileName();
	}
	
	/**
	 * Create new IsoImageCreation object based on already existing ISO-file.
	 * @param configuration The global configuration
	 * @param existingIsoPath The path to the existing ISO file
	 * @throws IOException If the existing ISO cannot be found, or if a temp directory cannot be created.
	 */
	public IsoImageCreation(GlobalConfiguration configuration) throws IOException {
		this.configuration = configuration;
		systemCall = new SystemCall(configuration.getInstallDirectory());
		String tempDir = UUID.randomUUID().toString();
		isoDataDirectory = configuration.getContextDataDirectory() + File.separator + tempDir;
		File isoDataDirFile = new File(isoDataDirectory);
		
		//Create temp dir
		if(!(isoDataDirFile.mkdir())) {
	        throw new IOException("Could not create temp directory: " + isoDataDirectory);
	    }
	}

	/**
	 * Store context data to the local repository. The following context data is
	 * stored: 1) Security Keys 2) End Points 3) Software Dependencies 4)
	 * License Tokens 5) Entire SP manifest 6) Bootstrap script 7) Security Agents
	 * 
	 * TODO: Refactor reducing duplication of code used for directory and file
	 * operations...
	 * 
	 * @param contextdata
	 *            Used to access the security keys applied at the service level.
	 * @param virtualMachine
	 *            The virtual machine to store context data for.
	 */
	public void storeContextData(ContextData contextdata,
			VirtualMachine virtualMachine) {
		log.debug("Iso Data Directory is: " + isoDataDirectory + File.separator);

		// Create the directory
		new File(isoDataDirectory).mkdirs();

		// 1) Store the security keys if the are to be added to this VM
		// instance
		File securityKeysDirectory = new File(isoDataDirectory
				+ File.separator + "securitykeys");
		securityKeysDirectory.mkdirs();

		if (virtualMachine.isHasVPNKey() || virtualMachine.isHasSSHKey() || virtualMachine.isHasBTKey() || virtualMachine.isHasDMKey()) {
			if (contextdata.getSecurityKeys().size() != 0) {
				for (SecurityKey securityKey : contextdata.getSecurityKeys()
						.values()) {
					String name = securityKey.getName();
					byte[] keyData = securityKey.getKeyData();
					
					File securityKeyFile = null;
					// TODO: change to switch... meh
					if (name.equals("SSH")) {
						securityKeyFile = new File( securityKeysDirectory + File.separator
							+ "SSH.key");
					} else if (name.equals("VPN")) {
						securityKeyFile = new File( securityKeysDirectory + File.separator
								+ "VPN.key");
					} else if (name.equals("BT")) {
						securityKeyFile = new File( securityKeysDirectory + File.separator
								+ "BT.key");
					} else if (name.equals("DM")) {
						securityKeyFile = new File( securityKeysDirectory + File.separator
								+ "DM.key");
					} else {
						log.warn("Unknown key name type!");
					}
					
					try {
						log.debug("Attempting to create file: "
								+ securityKeyFile.getPath());
						securityKeyFile.createNewFile();
						log.debug("Created file: " + securityKeyFile.getPath());
					} catch (IOException e) {
						log.error("Failed to create security key file with name: "
								+ name + ".key", e);
					}
					
					// Write out security key...
					log.warn("Writing out security key with path: " + securityKeyFile.getPath());
					try {
						FileOutputStream fos = new FileOutputStream(securityKeyFile);
						fos.write(keyData);
						fos.close();
						log.debug("Writing security key complete!");
						
					} catch (FileNotFoundException e) {
						log.error("FileNotFoundException : " + e);
					} catch (IOException e) {
						log.error("IOException : " + e);
					}
				}
			} else {
				log.warn("No security keys to write!");
			}
		}

		// 2) Write out the end points to each to there own file in a
		// sub-directory:
		File endPointDirectory = new File(isoDataDirectory + File.separator + "endpoints");
		endPointDirectory.mkdirs();

		if (virtualMachine.getEndPoints().size() != 0) {
			for (EndPoint endPoint : virtualMachine.getEndPoints().values()) {
				String name = endPoint.getName();
				String uri = endPoint.getUri();

				// Create the end point with the given name
				File endPointFile = new File(endPointDirectory + File.separator + name + ".properties");
				try {
					log.debug("Attempting to create file: "
							+ endPointFile.getPath());
					endPointFile.createNewFile();
					log.debug("Created file: " + endPointFile.getPath());
				} catch (IOException e) {
					log.error("Failed to create endpoint file with name: "
							+ name + ".properties", e);
				}
				
			    Properties props = new Properties();
			    props.setProperty(name, uri); 
			    try {
			    	FileOutputStream fileOutputStream = new FileOutputStream(endPointFile);
					props.store(fileOutputStream, "VMC properties file for service endpoints:");
					fileOutputStream.close();
					log.debug("Writing endpoint complete!");
					
				} catch (FileNotFoundException e) {
					log.error("FileNotFoundException : " + e);
				} catch (IOException e) {
					log.error("IOException : " + e);
				}

			}
		} else {
			log.warn("No end points to write!");
		}

		// 3) Provide per VM instance configuration information for software
		// dependencies
		File softwareDependenciesDirectory = new File(isoDataDirectory
				+ File.separator + "softwaredeps");
		softwareDependenciesDirectory.mkdirs();

		if (virtualMachine.getSoftwareDependencies().size() != 0) {
			for (SoftwareDependency softwareDependency : virtualMachine
					.getSoftwareDependencies().values()) {
				String name = softwareDependency.getArtifactId() + "_"
						+ softwareDependency.getGroupId() + "_"
						+ softwareDependency.getVersion() + ".dep";

				// Create the software dependency files here...
				File softwareDependencyFile = new File(
						softwareDependenciesDirectory + File.separator + name);
				try {
					log.debug("Attempting to create file: "
							+ softwareDependencyFile.getPath());
					softwareDependencyFile.createNewFile();
					log.debug("Created file: "
							+ softwareDependencyFile.getPath());
				} catch (IOException e) {
					log.error(
							"Failed to create softwareDependency config file with name: "
									+ name, e);
				}

				// TODO Write out some configuration data to the new file.
				log.warn("Writing out software dependency configuration data not implemented yet, no support in service manifest!");
			}
		} else {
			log.warn("No software dependency configuration files to write!");
		}

		// 4) Store the license tokens
		File licenseTokenDirectory = new File(isoDataDirectory
				+ File.separator + "licensetoken");
		licenseTokenDirectory.mkdirs();

		if (virtualMachine.getLicenseTokens().size() != 0) {
			for (LicenseToken licenseToken : virtualMachine.getLicenseTokens()
					.values()) {
				String name = "license.token." + licenseToken.getId();

				// Create the end point with the given name and write the
				File licenseTokenFile = new File(licenseTokenDirectory + File.separator
						+ name);
				try {
					log.debug("Attempting to create file: "
							+ licenseTokenFile.getPath());
					licenseTokenFile.createNewFile();
					log.debug("Created file: " + licenseTokenFile.getPath());
				} catch (IOException e) {
					log.error("Failed to create licenseToken file with name: "
							+ name, e);
				}

				// Write out license tokens...
				log.warn("Writing out license token...");
				try {
					FileOutputStream fos = new FileOutputStream(licenseTokenFile);
					fos.write(licenseToken.getToken());
					fos.close();
					log.warn("Writing license token complete!");
				} catch (FileNotFoundException e) {
					log.error("FileNotFoundException : " + e);
				} catch (IOException e) {
					log.error("IOException : " + e);
				}
			}
		}
		
		// 5) Store the entire manifest
		File serviceManifestFile = new File( isoDataDirectory + File.separator
				+ "manifest.xml");
		try {
			log.debug("Attempting to create file: "
					+ serviceManifestFile.getPath());
			serviceManifestFile.createNewFile();
			log.debug("Created file: " + serviceManifestFile.getPath());
			
			// Write out the manifest file
			FileOutputStream fos = new FileOutputStream(serviceManifestFile.getPath());
			fos.write(manifest.toString().getBytes());
			fos.close();	
			log.debug("Writing service manifest complete!");
		    
		} catch (IOException e) {
			log.error("Failed to create service manfiest file with name: "
					+ serviceManifestFile.getName(), e);
		}
		
		// 6) Bootstrap script 
		File scriptsDirectory = new File(isoDataDirectory
				+ File.separator + "scripts");
		scriptsDirectory.mkdirs();
		
		File bootStrapFile = new File( scriptsDirectory + File.separator
				+ "bootstrap.sh");
		try {
			log.debug("Attempting to create file: "
					+ bootStrapFile.getPath());
			bootStrapFile.createNewFile();
			log.debug("Created file: " + bootStrapFile.getPath());
			
			// TODO: This should be stored somewhere else and not hardcoded
			// Mount location is currently hard coded in the init.d scripts of the base VM /mnt/context/
			String bootStrapScript = "#!/bin/bash\n" +
					"if [ -f /mnt/context/scripts/bootstrap.sh ]; then\n" +
					"  #Get the public SSH key:\n" +
					"  PUBLICKEY=`cat /mnt/context/securitykeys/SSH.key | grep ssh-rsa`\n" +
					"  echo ${PUBLICKEY/user@hostname/root@`hostname`} > /root/.ssh/authorized_keys\n" +
					"  chmod 700 /root/.ssh\n" +
					"  chmod 600 /root/.ssh/authorized_keys\n" +
					"  #Get the private SSH key:\n" +
					"  cat /mnt/context/securitykeys/SSH.key | head -27 > /root/.ssh/id_rsa\n" +
					"  chmod 700 /root/.ssh/id_rsa\n" +
					"  #Run agent script if present\n" +
					"  if [ -f /mnt/context/scripts/agents.sh ]; then\n" +
					"    sh /mnt/context/scripts/agents.sh\n" +
					"  fi\n" +
					"fi\n";
			
			// Write out the boostrap file
			FileOutputStream fos = new FileOutputStream(bootStrapFile.getPath());
			fos.write(bootStrapScript.getBytes());
			fos.close();	
			log.debug("Writing boobstrap script complete!");
		    
		} catch (IOException e) {
			log.error("Failed to create boobstrap script file with name: "
					+ serviceManifestFile.getName(), e);
		}
		
		// 7) Security Agents
		if (virtualMachine.isHasIPS() || virtualMachine.isHasBTKey() || virtualMachine.isHasVPNKey()) {
			log.debug("Adding agents to ISO");
			
			//Add the agent tar ball
			String agentsTarBallName = "vpn.tar.gz";
			//Agents tar ball source
			File agentsTarBallFileSource = new File (configuration.getAgentsDirectory() + File.separator + agentsTarBallName);
			log.debug("agentsTarBallFileSource is: " + agentsTarBallFileSource.getPath());
			//Destination folder
			File agentsIsoDirectory = new File(isoDataDirectory
					+ File.separator + "agents");
			agentsIsoDirectory.mkdirs();
			log.debug("agentsIsoDirectory is: " + agentsIsoDirectory.getPath());
			//Agents tar ball destination
			File agentsTarBallFileDestination = new File(agentsIsoDirectory + File.separator + agentsTarBallName);
			log.debug("agentsTarBallFileDestination is: " + agentsTarBallFileDestination.getPath());
			
			//Copy the file to the iso directory
			log.debug("Copying agent file to ISO directory...");
		    FileChannel source = null;
		    FileChannel destination = null;
		    try {
			    if(!agentsTarBallFileDestination.exists()) {
			    	agentsTarBallFileDestination.createNewFile();
			    }
		        source = new FileInputStream(agentsTarBallFileSource).getChannel();
		        destination = new FileOutputStream(agentsTarBallFileDestination).getChannel();
		        destination.transferFrom(source, 0, source.size());
		        log.debug("Copied agent file to ISO directory, size is : " + agentsTarBallFileDestination.length());
		        agentsTarBallFileDestination.getTotalSpace();
		    } catch (IOException e) {
				log.error("Failed to create agents tar ball with name: "
						+ serviceManifestFile.getName(), e);    	
		    }
		    finally {
		        if(source != null) {
		            try {
						source.close();
					} catch (IOException e) {
						log.error("Failed to create agents tar ball file with name: "
								+ serviceManifestFile.getName(), e);
					}
		        }
		        if(destination != null) {
		            try {
						destination.close();
					} catch (IOException e) {
						log.error("Failed to create agents tar ball file with name: "
								+ serviceManifestFile.getName(), e);
					}
		        }
		    }
		    
			
			//Add the agent script 
			File agentsFile = new File( scriptsDirectory + File.separator
					+ "agents.sh");
			try {
				log.debug("Attempting to create file: "
						+ agentsFile.getPath());
				agentsFile.createNewFile();
				log.debug("Created file: " + agentsFile.getPath());
				
				// TODO: This should be stored somewhere else and not hardcoded
				// Mount location is currently hard coded in the init.d scripts of the base VM /mnt/context/
				String agentsScript = "#!/bin/bash\n" +
						"#Setup environment\n" +
						"touch /var/lock/subsys/local\n" +
						"source /etc/profile\n" +
						"\n" +
						"#Extract the agent from the ISO agent directory to /opt/optimis/vpn/\n" +
						"mkdir -p /opt/optimis\n" +
						"tar zxvf /mnt/context/agents/" + agentsTarBallName + " -C /opt/optimis/\n" +
						"chmod -R 777 /opt/optimis/vpn\n" +
						"\n" +
						"#Install and start the agents\n" +
						"\n";
				
				//Add VPN install and init script to /mnt/context/scripts/agents.sh
				if (virtualMachine.isHasVPNKey()) {
					agentsScript += "#VPN\n" +
					"/opt/optimis/vpn/VPN_Meta.sh\n";
				}
				
				//Add VPN install and init script to /mnt/context/scripts/agents.sh ?
				if (virtualMachine.isHasBTKey()) { //KMS
					agentsScript += "#KMS\n" +
					"/bin/date > /opt/optimis/vpn/kms.log\n" +
					"/opt/optimis/vpn/KMS_Meta.sh\n" +
					"\n";
				}
				
				//Add VPN install and init script to /mnt/context/scripts/agents.sh
				if (virtualMachine.isHasIPS()) {
					agentsScript += "#IPS\n" +
					"/bin/date > /opt/optimis/vpn/dsa.log\n" +
					"/opt/optimis/vpn/IPS_Meta.sh\n" +
					"\n";
				}
				
				// Write out the agents file
				FileOutputStream fos = new FileOutputStream(agentsFile.getPath());
				fos.write(agentsScript.getBytes());
				fos.close();	
				log.debug("Writing agents script complete!");
			    
			} catch (IOException e) {
				log.error("Failed to create agents script file with name: "
						+ serviceManifestFile.getName(), e);
			}
		} else {
			log.debug("Agents not not needed by service!");
		}
	}

	/**
	 * Create an ISO using its associated attributes and stored context data.
	 * 
	 * @return The ISO created.
	 * @throws SystemCallException
	 *             Thrown if the command to create the ISO via a system call
	 *             fails.
	 */
	public Iso create() throws SystemCallException {
		
		//Detect Linux distribution
		String commandName;
		if (new File("/etc/debian-version").exists()) {
			log.info("Debian distribution Variant detected using \"genisoimage\"");
			commandName = "genisoimage";
		}		
		else if (new File("/etc/redhat-release").exists()) {
			log.info("Redhat distribution variant detected using \"mkisofs\"");
			commandName = "mkisofs";
		} else {
			log.info("Unknown linux distribution detected using default \"mkisofs\"");
			commandName = "mkisofs";			
		}

		ArrayList<String> arguments = new ArrayList<String>();

		arguments.add("-R"); // Generate SUSP and RR records	
		arguments.add("-r"); // File ownership and modes
		arguments.add("-J"); // Generate Joliet directory records
		arguments.add("-l"); // Allow full 31 character filenames
		arguments.add("-allow-leading-dots");
		arguments.add("-allow-lowercase");
		arguments.add("-allow-multidot");
		arguments.add("-o"); // filename
		arguments.add(iso.getUri());
		arguments.add(isoDataDirectory);

		// Executed command looks like so:
		// "mkisofs -o iso.getFileName() isoDataDirectory"
		try {
			systemCall.runCommand(commandName, arguments);
		} catch (SystemCallException e) {
			if (configuration.isDefaultValues()) {
				log.warn(
						"Failed to run command, is this invocation in a unit test?",
						e);
			} else {
				throw e;
			}
		}

		if (systemCall.getReturnValue() == 0) {
			iso.setCreated(true);
			log.info("Iso created with uri: " + iso.getUri());
		} else {
			log.error("Iso Creation Failed! Return value was: "
					+ systemCall.getReturnValue());
		}

		// Print out the directory tree structure for debug purposes:
		log.debug("Files in directory: " + isoDataDirectory + File.separator);
		try {
			List<File> files = getFileListing(new File(isoDataDirectory));
			for (File file : files) {
				log.debug(file);
			}
		} catch (Exception e) {
			log.error("File was not found while listing directory!", e);
		}

		// Remove isoDataDirectory recursively after creating the ISO:
		try {
			deleteRecursive(new File(isoDataDirectory));
			log.debug("Recursively deleted isoDataDirectory: "
					+ isoDataDirectory + File.separator);
		} catch (FileNotFoundException e) {
			log.error("Cannot recursively delete isoDataDirectory: "
					+ isoDataDirectory, e);
		}

		return iso;
	}

	/**
	 * Delete a directory recursively, this does the equivalent of "rm -r".
	 * 
	 * TODO: move this to a utils class..
	 * 
	 * @param path
	 *            Root File Path.
	 * @return True if the file and all sub files/directories have been removed.
	 * @throws FileNotFoundException.
	 */
	private static boolean deleteRecursive(File path)
			throws FileNotFoundException {
		if (!path.exists())
			throw new FileNotFoundException(path.getAbsolutePath());
		boolean ret = true;
		if (path.isDirectory()) {
			for (File f : path.listFiles()) {
				ret = ret && deleteRecursive(f);
			}
		}
		return ret && path.delete();
	}

	/**
	 * Recursively walk a directory tree and return a List of all Files found;
	 * the List is sorted using File.compareTo().
	 * 
	 * TODO: move this to a utils class..
	 * 
	 * @param aStartingDir
	 *            is a valid directory, which can be read.
	 */
	static private List<File> getFileListing(File aStartingDir)
			throws FileNotFoundException {
		validateDirectory(aStartingDir);
		List<File> result = getFileListingNoSort(aStartingDir);
		Collections.sort(result);
		return result;
	}

	/**
	 * Get file list without sorting.
	 * 
	 * TODO: Move this to a utils class...
	 * 
	 * @param aStartingDir
	 *            The starting directory.
	 * @return List of files found.
	 * @throws FileNotFoundException
	 */
	static private List<File> getFileListingNoSort(File aStartingDir)
			throws FileNotFoundException {
		List<File> result = new ArrayList<File>();
		File[] filesAndDirs = aStartingDir.listFiles();
		List<File> filesDirs = Arrays.asList(filesAndDirs);
		for (File file : filesDirs) {
			result.add(file); // always add, even if directory
			if (!file.isFile()) {
				// must be a directory
				// recursive call!
				List<File> deeperList = getFileListingNoSort(file);
				result.addAll(deeperList);
			}
		}
		return result;
	}

	/**
	 * Directory is valid if it exists, does not represent a file, and can be
	 * read.
	 * 
	 * TODO: Move this to a utils class...
	 */
	static private void validateDirectory(File aDirectory)
			throws FileNotFoundException {
		if (aDirectory == null) {
			throw new IllegalArgumentException("Directory should not be null.");
		}
		if (!aDirectory.exists()) {
			throw new FileNotFoundException("Directory does not exist: "
					+ aDirectory);
		}
		if (!aDirectory.isDirectory()) {
			throw new IllegalArgumentException("Is not a directory: "
					+ aDirectory);
		}
		if (!aDirectory.canRead()) {
			throw new IllegalArgumentException("Directory cannot be read: "
					+ aDirectory);
		}
	}
	
	//TODO Add javadoc here
	// Creates a new recontextualization image (with test data), eventually will take as input recontextualization data specific to an IP
	public String recontext() {
		
		String isoPath = null;
		
		//TODO Need to figure out a way to provide this function with a list of recontext data that an IP can specify
		
		//TODO For testing purposes, create a new service end points here to be stored in the recontext ISO
		
		//TODO Create the new recontext ISO and return its location on the file system
		
		return isoPath;
	}
}
