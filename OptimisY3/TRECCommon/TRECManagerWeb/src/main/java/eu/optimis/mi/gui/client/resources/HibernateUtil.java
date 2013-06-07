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
package eu.optimis.mi.gui.client.resources;

import java.io.FileInputStream;
import java.util.PropertyResourceBundle;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;

public class HibernateUtil {
	private static final boolean UPDATESCHEMA = false;
	
	private static SessionFactory sessionFactory;
	static {
		try {	    	
                   //PropertyResourceBundle rbconf = (PropertyResourceBundle)ResourceBundle.getBundle("ipraconfig");
                   //FileInputStream fis = new FileInputStream(rbconf.getString("config.iprapropfilepath"));
                    FileInputStream fis = new FileInputStream("c:/home/trec/risk/ipra.properties");
                   PropertyResourceBundle rb = new PropertyResourceBundle(fis);
                   Configuration cfg = new AnnotationConfiguration().configure();
                   cfg.setProperty("hibernate.dialect", rb.getString("hibernate.dialect"));
                   cfg.setProperty("hibernate.connection.driver_class", rb.getString("hibernate.connection.driver_class"));
                   cfg.setProperty("hibernate.connection.url", rb.getString("hibernate.connection.url"));
                   cfg.setProperty("hibernate.connection.username", rb.getString("hibernate.connection.username"));
                   cfg.setProperty("hibernate.connection.password", rb.getString("hibernate.connection.password"));
	    	if ( UPDATESCHEMA ) {
	    		SchemaUpdate schemaUpdate = new SchemaUpdate(cfg);
	    		schemaUpdate.execute(true, true);
	    	}
			
			sessionFactory = cfg.buildSessionFactory();
		} catch (Throwable ex) {
                    
                     ex.printStackTrace();
                     System.out.println("hibernate connection error:" + ex.getMessage());
			//throw new ExceptionInInitializerError(ex);
		}
	}
	
	public static SessionFactory getSessionFactory() {
		// Alternatively, you could look up in JNDI here
		return sessionFactory;
	}
}
