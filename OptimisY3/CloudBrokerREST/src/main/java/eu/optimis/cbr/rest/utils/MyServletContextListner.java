package eu.optimis.cbr.rest.utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import eu.optimis.cbr.rest.CloudBrokerAPI;
import eu.optimis.cbr.rest.utils.IPRegistry;
/**
 * @author Pramod Pawar
 */


public class MyServletContextListner implements ServletContextListener{
		private ServletContext context = null;

	  /*This method is invoked when the Web Application has been removed 
	  and is no longer able to accept requests
	  */

	  public void contextDestroyed(ServletContextEvent event)
	  {
	    //Output a simple message to the server's console
	    System.out.println("The Simple Web App. Has Been Removed");
	    this.context = null;

	  }


	  //This method is invoked when the Web Application
	  //is ready to service requests

	  public void contextInitialized(ServletContextEvent event)
	  {
		
	    this.context = event.getServletContext();
	    
	    MyServletContext.setContext(this.context); 

 
	    //Output a simple message to the server's console
	    //System.out.println("The Simple Web App. Is Ready");
		IPRegistry reg1 = IPRegistry.getSingletonObject();
		IPInfoList iplist; 
		
		
		JAXBContext context = null;
		try {
			context = JAXBContext.newInstance(IPInfoList.class);
		} catch (JAXBException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		Unmarshaller um = null;
		String path = getPath("filepath");
		
		ServletContext ctext =  MyServletContext.getContext();
		String realPath = ctext.getRealPath(path);
		
		try {
			um = context.createUnmarshaller();
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
		     //iplist = (IPInfoList)um.unmarshal(new FileReader("src/main/resources/IP-Store.xml"));
		     //iplist = (IPInfoList)um.unmarshal(new FileReader(path));
		     iplist = (IPInfoList)um.unmarshal(new FileReader(realPath));

			
			reg1.setIPInfoList(iplist);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		//System.out.println("Numbe of IPs in the Regisry :" + reg1.getIPInfoList().getIPList().toArray().length);
	    //IPRegistry.getSingletonObject().setIPInfoList(reg1.getIPInfoList());	    
		
}


		public String getPath(String param){
			String path=null;
		    Properties properties = new Properties();
			try {
//			    properties.load(new FileInputStream("C:/documents and settings/605474046/workspace/CloudBrokerREST/src/main/resources/BrokerServerProperties"));
//			    properties.load(new FileInputStream("src/main/resources/BrokerServerProperties"));
//			    properties.load(new FileInputStream("BrokerServerProperties"));

//			    System.out.println(new File(".").getAbsolutePath()); 
			    InputStream is = getClass().getClassLoader().getResourceAsStream("BrokerServerProperties");
			    properties.load(is); 
			    
			    path = properties.getProperty(param);

			    //System.out.println("PATH :" + path);
			} catch (IOException e) {
				System.out.println("File Read Exception  " + e.getMessage());
			}
			return path;
		}
	  
}


