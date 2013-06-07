/**

Copyright 2013 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Juan Luis Prieto, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.common.trec.db.ip.utils;

import java.io.File;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory; 
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

	 private static final SessionFactory sessionFactory; 
	 
	 static 
	 { 
        try 
        { 
        	String path;
        	if (System.getProperty("file.separator").equalsIgnoreCase("/")) {
        		String optimis_home = System.getenv("OPTIMIS_HOME");
        		if (optimis_home == null){
        			optimis_home = "/opt/optimis";
        		}
        		path = System.getenv("OPTIMIS_HOME")+"/etc/iptf/hibernate-ip.cfg.xml";
        	} else {
        		path = System.getenv("OPTIMIS_HOME")+"\\etc\\iptf\\hibernate-ip.cfg.xml";
        	}
        	File hibernateConfig = new File(path);
            sessionFactory = new Configuration().configure(hibernateConfig).buildSessionFactory();  
        } 	
        catch (HibernateException he) 
        { 
           System.err.println("Error while oppening session in sessionFactory: " + he); 
           throw new ExceptionInInitializerError(he); 
        } 
	 }  

	 public static SessionFactory getSessionFactory() 
	 { 
        return sessionFactory; 
	 }
}