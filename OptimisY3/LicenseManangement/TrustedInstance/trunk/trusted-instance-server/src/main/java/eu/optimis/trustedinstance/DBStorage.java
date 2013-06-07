/* 
 * Copyright (c) 2007, Fraunhofer-Gesellschaft
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the disclaimer at the end.
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 * 
 * (2) Neither the name of Fraunhofer nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 * 
 * DISCLAIMER
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 */
package eu.optimis.trustedinstance;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationFactory;
import org.apache.log4j.Logger;

/**
 *
 * @author hrasheed
 * @author s.reiser
 */
public class DBStorage {

    private EntityManagerFactory emf = null;

    private static final Logger LOG = Logger.getLogger(DBStorage.class);
    
    private static Configuration config ;

    public DBStorage() {
        init();
        String pUnit = config.getString("dbstorage.persistenceUnit") ;
        emf = Persistence.createEntityManagerFactory(pUnit);
    }

    private void init() {
        try {
            ConfigurationFactory factory = new ConfigurationFactory("config.xml");
            config = factory.getConfiguration();
        } catch (ConfigurationException ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("finally")
	public boolean store(DBStorageEntry entry) throws Exception {
    	
        if (update(entry)) {
            return true;
        }

        boolean result = true;
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(entry);
            em.getTransaction().commit();
        } catch (Exception e) {
            result = false;
            throw e ;
        } finally {
            em.close();
            return result;
        }
    }
    
    @SuppressWarnings("finally")
	public boolean update(DBStorageEntry entry) {
    	
        boolean result = true;
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            DBStorageEntry update = em.find(DBStorageEntry.class, entry.getKey());
            update.setLicenseToken(entry.getLicenseToken());
            em.getTransaction().commit();
        } catch (Exception e) {
            result = false;
        } finally {
            em.close();
            return result;
        }
    }

    @SuppressWarnings("finally")
	public DBStorageEntry get(String resourceKey) throws Exception {
    	
    	DBStorageEntry result = null;
    	EntityManager em = emf.createEntityManager();
    	
    	try {
            em.getTransaction().begin();
            result = (DBStorageEntry) em.find(DBStorageEntry.class, resourceKey);
            em.getTransaction().commit();
    	} catch (Exception e) {
    		result = null;
            throw e ;
        } finally {
            em.close();
            return result;
        }
    }

    @SuppressWarnings("finally")
	public boolean delete(String resourceKey) throws Exception {
    	
    	boolean result = true;
    	EntityManager em = emf.createEntityManager();
    	
    	try {
            String jpql = "delete from DBStorageEntry se where se.key=\"" + resourceKey + "\"";
            em.getTransaction().begin();
            Query query = em.createQuery(jpql);
            int row = query.executeUpdate();
            em.getTransaction().commit();
            if (row == 0) {
            	result = false;
            } 
    	} catch (Exception e) {
    		result = false;
            throw e ;
        } finally {
            em.close();
            return result;
        }
    }

//    @SuppressWarnings("finally")
//	public List<DBStorageEntry> filterByType(int type) {
//
//    	List<DBStorageEntry> queryList = null;
//    	EntityManager em = emf.createEntityManager();
//
//    	try {
//    		em.getTransaction().begin();
//            String jpql = "select se from DBStorageEntry se where se.type=" + type;
//            Query query = em.createQuery(jpql);
//            queryList = query.getResultList();
//            em.getTransaction().commit();
//    	} catch (Exception e) {
//    		queryList = null;
//        } finally {
//            em.close();
//            return queryList;
//        }
//    }
}
