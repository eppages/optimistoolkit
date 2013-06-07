/*
 Copyright (C) 2012-2013 Umeå University

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package eu.optimis.demogui.sdo.frontend;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.zkoss.io.Files;
import org.zkoss.util.media.Media;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
//import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
//import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
//import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
//import org.zkoss.zul.Listcell;
//import org.zkoss.zul.Listitem;
import org.zkoss.zul.Vlayout;

import eu.optimis._do.schemas.Objective;

/**
 * @author Wubin.Li (Viali)
 * @author Petter Svärd
 */

public class GuiComposer extends GenericForwardComposer
{
	private static final long serialVersionUID = -1459816149796244957L;

	private static final Logger logger = Logger.getLogger(GuiComposer.class);
	
	public static String manifestFilesPath = "uploadedFiles" + File.separator + "manifests" + File.separator;
	public static String sdoConfigFilesPath = "uploadedFiles" + File.separator + "config" + File.separator;
	
	private Vlayout vlayoutBottom;
	
	private boolean useNewPFile;
	private Hlayout hlayoutPFileALL;
	private Button btnPFileUpload;
	
	private Hlayout hlayoutPFile;
	private Label pFileLabel;

	private Button btnMFileUpload;
	private Button btnMFileDelete;

	private Hlayout hlayoutMFile;
	private Label mFileLabel;

	private Button btnDeploy;
	//private Button btnUndeploy;
	
	private Listbox statusList;
	private Listbox trecList;
	
//	private Vlayout trecLayout;
	
	private Listbox objectiveList;
	
	private File manifestfile;
	
	private String sdo_config_file;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception
	{
		super.doAfterCompose(comp);
		btnMFileUpload.setDisabled(false);
		btnDeploy.setDisabled(true);
		//btnUndeploy.setDisabled(true);
		statusList.getItems().clear();

		this.useNewPFile = false;
		this.sdo_config_file = null;
		
		//TREC LAYOUT
		//this.trecLayout.setVisible(false);
		this.trecList.getItems().clear();
		
		//TO BE REMOVED.
//		Listitem item = new Listitem();
//		Listcell cellIP = new Listcell("atos");
//		cellIP.setParent(item);
//		
//		A a = new A("XML");
//		a.setHref("http://www.google.com");
//		a.setTarget("_blank");
//		Listcell cellTRECLink = new Listcell();
//		a.setParent(cellTRECLink);
//		cellTRECLink.setParent(item);
//		this.trecList.appendChild(item);
	}
	
//	//Event on case Select
//	public void onSelect$caseList(SelectEvent event)
//	{
//		String deployCase = (String) caseList.getSelectedItem().getValue();
//		if (deployCase.equalsIgnoreCase("NON_BROKER"))
//		{
//			btnDeploy.setLabel("Deploy");
//		}
//		else if (deployCase.equalsIgnoreCase("BROKER"))
//		{
//			btnDeploy.setLabel("Call Broker to Deploy");
//		}
//		else
//		{}
//	}

	//Event on Manifest File Upload button
	public void onUpload$btnMFileUpload(UploadEvent event)
	{
		Media media = event.getMedia();
		if(!media.getName().endsWith(".xml"))
		{
			try
			{
				Messagebox.show("The service manifest file must be an .xml file. Please select another file:-)", "Wrong file", Messagebox.OK, Messagebox.ERROR);
			}
			catch (InterruptedException e)
			{
				logger.error("Failed to show the Messagebox: "+this.getClass());
				e.printStackTrace();
			}
			return;
		}
			
		try
		{
			Reader reader = media.getReaderData();
			SimpleDateFormat formatter =new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String fileName =  formatter.format(new Date()) + "." +RandomUtils.nextInt(10) + ".xml";
			manifestfile=new File(manifestFilesPath + fileName);
			Files.copy(manifestfile,reader,null);
			Files.close(reader);  
		}
		catch (IOException e)
		{
			logger.error("Failed to save the servicemanifest file: "+this.getClass());
			try
			{
				Messagebox.show("Failed to save the servicemanifest file due to :"+e, "File save failure", Messagebox.OK, Messagebox.ERROR);
			}
			catch (InterruptedException ex)
			{
				ex.printStackTrace();
			}
			e.printStackTrace();
			return;
		}  
        
		logger.info("Service manifest file uploaded and save as " + manifestfile.getAbsolutePath());
        
		btnMFileUpload.setDisabled(true);
		
		hlayoutMFile.setVisible(true);
		hlayoutMFile.setSpacing("6px");
		mFileLabel.setValue(media.getName());
		
		statusList.getItems().clear();
		btnDeploy.setDisabled(false);
	}
	
	//Event on Manifest File Delete button
	public void onClick$btnMFileDelete(Event event)
	{
		hlayoutMFile.setVisible(false);
		btnMFileUpload.setDisabled(false); // Enable Upload Again
		btnDeploy.setDisabled(true); //  Disable Deploy
		manifestfile.delete(); //Delete the file uploaded
		statusList.getItems().clear();
	}
	
	//Event on CheckBox Properties File
	public void onClick$cbxPFile(Event event)
	{
		if (this.useNewPFile)//decide to not to use a new properties file
		{
			hlayoutPFileALL.setVisible(false);
			if (this.sdo_config_file != null)
			{
				hlayoutPFile.setVisible(false);
				btnPFileUpload.setDisabled(false); // Enable Upload Again
				
				//delete files
				Files.deleteAll(new File(this.sdo_config_file));
				this.sdo_config_file = null;
				logger.info("the SDO configuration properties file is deleted.");
			}
			
		}
		else//decide to use a new properties file
		{
			hlayoutPFileALL.setVisible(true);
		}
		this.useNewPFile = !this.useNewPFile;
	}
	
	//Event on Configuration File Upload button
	public void onUpload$btnPFileUpload(UploadEvent event)
	{
		Media media = event.getMedia();
		if(!media.getName().endsWith(".properties"))
		{
			try
			{
				Messagebox.show("The configuration file must be an .properties file. Please select another file:-)", "Wrong file", Messagebox.OK, Messagebox.ERROR);
			}
			catch (InterruptedException e)
			{
				logger.error("Failed to show the Messagebox: "+this.getClass());
				e.printStackTrace();
			}
			return;
		}
		
		try
		{
			InputStream ins = media.getStreamData();
			SimpleDateFormat formatter =new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String fileName =  formatter.format(new Date()) + "." +RandomUtils.nextInt(10) + ".properties";
			File cfgFile = new File(sdoConfigFilesPath + fileName);
			Files.copy(cfgFile, ins);
			Files.close(ins);  
			this.sdo_config_file = cfgFile.getAbsolutePath();
		}
		catch (IOException e)
		{
			logger.error("Failed to save the configuration file: "+this.getClass());
			try
			{
				Messagebox.show("Failed to save the configuration file due to :"+e, "File save failure", Messagebox.OK, Messagebox.ERROR);
			}
			catch (InterruptedException ex)
			{
				ex.printStackTrace();
			}
			e.printStackTrace();
			return;
		}  
		
		logger.info("SDO configuration file uploaded and save as "+ this.sdo_config_file);
		
		btnPFileUpload.setDisabled(true);
		hlayoutPFile.setVisible(true);
		hlayoutPFile.setSpacing("6px");
		pFileLabel.setValue(media.getName());
	}
	
	public void onClick$btnPFileDelete(Event event)
	{
		hlayoutPFile.setVisible(false);
		btnPFileUpload.setDisabled(false); // Enable Upload Again
		
		//delete files
		Files.deleteAll(new File(this.sdo_config_file));
		this.sdo_config_file = null;
		
		logger.info("the SDO configuration properties file is deleted.");
	}
	
	//Event on Deploy button
	public void onClick$btnDeploy(Event event)
	{
		//Start a new thread to deploy the service and update UI components.
		this.desktop.enableServerPush(true);
		
		String obj = (String) this.objectiveList.getSelectedItem().getValue();		
		Objective objective = null;
		if (obj.equalsIgnoreCase("COST"))
			objective = Objective.COST;
		else if (obj.equalsIgnoreCase("RISK"))
			objective = Objective.RISK;
		
		logger.info("Objective : ==" + objective);
		

		logger.info("Starting a working thread for private scenario.");
		WorkingThread thread = new WorkingThread(this.sdo_config_file, this.manifestfile, objective,
					this.statusList, this.trecList, this.vlayoutBottom);
		thread.start();
		btnMFileDelete.setDisabled(true);	
	}
	
	//Event on Undeploy button
	public void onClick$btnUndeploy(Event event)
	{
		//TODO 
	}
}
