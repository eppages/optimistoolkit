/**
 * Copyright (C) 2010-2013 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package eu.optimis.ecoefficiencytool.trecdb.sp.utils;

import java.io.File;
import java.net.URL;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            //URL hibernateConfigIP = HibernateUtil.class.getResource("/hibernateSP.cfg.xml");//new File("src/main/resources/hibernateIP.cfg.xml");//ConfigManager.getHibernateConfigurationFile("IP");
            //sessionFactory = new Configuration().configure(hibernateConfigIP).buildSessionFactory();
            String optimisHome = System.getenv("OPTIMIS_HOME");
            if(optimisHome == null) {
                optimisHome = "/opt/optimis";
            }
            String path = optimisHome +"/etc/EcoEfficiencyToolSP/hibernateSP.cfg.xml";
            File hibernateConfig = new File(path);
            if(hibernateConfig.exists()) {
                sessionFactory = new Configuration().configure(hibernateConfig).buildSessionFactory(); 
            } else {
                //Used for testing.
                URL hibernateConfigSP = HibernateUtil.class.getResource("/hibernateSP.cfg.xml");
                sessionFactory = new Configuration().configure(hibernateConfigSP).buildSessionFactory();
            }
        } catch (HibernateException he) {
            System.err.println("Error while oppening session in sessionFactory: " + he);
            throw new ExceptionInInitializerError(he);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}