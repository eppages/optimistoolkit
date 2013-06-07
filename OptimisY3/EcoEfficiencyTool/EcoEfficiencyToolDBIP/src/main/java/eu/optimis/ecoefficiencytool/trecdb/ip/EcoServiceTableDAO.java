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
package eu.optimis.ecoefficiencytool.trecdb.ip;

import eu.optimis.ecoefficiencytool.trecdb.ip.utils.DateFormatter;
import eu.optimis.ecoefficiencytool.trecdb.ip.utils.EcoValue;
import eu.optimis.ecoefficiencytool.trecdb.ip.utils.HibernateUtil;
import eu.optimis.trec.common.db.ip.model.IpEcoService;
import java.util.*;
import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author jsubirat
 */
public class EcoServiceTableDAO {

    private static Logger log = Logger.getLogger(EcoServiceTableDAO.class);

    public static Integer addEcoAssessment(String serviceId, double energyEffAssessment, double ecologicalEffAssessment, double performance, double power, double grCO2) throws Exception {
        Session session = null;
        Integer ret = null;
        IpEcoService serviceTable = new IpEcoService();
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try {
            session = sf.openSession();
            Transaction tx = session.beginTransaction();
            tx.begin();

            serviceTable.setServiceId(serviceId);
            serviceTable.setEnergyEffService(energyEffAssessment);
            serviceTable.setEcologicalEffService(ecologicalEffAssessment);
            serviceTable.setPerformanceService(performance);
            serviceTable.setPowerService(power);
            serviceTable.setGrCo2sService(grCO2);

            ret = (Integer) session.save(serviceTable);
            tx.commit();
            if (tx.wasCommitted()) {
                log.debug("Service " + serviceId + " assessment was commited correctly with id " + ret.toString() + ".");
            } else {
                log.error("Unable to insert service " + serviceId + " assessment data.");
            }
        } catch (Exception e) {
            log.error("Unable to insert service " + serviceId + " assessment data: " + e.getMessage());
            throw new Exception(e.toString());
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (HibernateException e) {
                }
            }
        }
        sf.close();
        return ret;
    }

    @SuppressWarnings("unchecked")
    public static List<EcoValue> getEcoAssessments(String serviceId, String ini, String end, String metric) throws Exception {
        List<EcoValue> assessments = new LinkedList<EcoValue>();
        Session session = null;
        List<IpEcoService> results = null;
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try {
            session = sf.openSession();
            Transaction tx = session.beginTransaction();
            tx.begin();
            // Obtain ecoAssessments in a range of time.
            Criteria criteria = session.createCriteria(IpEcoService.class);
            Date startDate = DateFormatter.getDateFromString(ini);
            Date endDate = DateFormatter.getDateFromString(end);
            //criteria.add(Expression.between("timestamp", new Date(startDate.getTime()), new Date(endDate.getTime())));
            criteria.add(Restrictions.like("serviceId", serviceId));
            criteria.add(Restrictions.between("timestamp", startDate, endDate));
            results = (List<IpEcoService>) criteria.list();
            tx.commit();
        } catch (Exception e) {
            log.error("Error while obtaining service " + serviceId + " assessments: " + e.getMessage());
            throw new Exception(e.toString());
        }

        if ("ecological".equalsIgnoreCase(metric)) {
            for (IpEcoService table : results) {
                assessments.add(new EcoValue(table.getTimestamp(), new Double(table.getEcologicalEffService())));
            }
        } else if ("energy".equalsIgnoreCase(metric)) {
            for (IpEcoService table : results) {
                assessments.add(new EcoValue(table.getTimestamp(), new Double(table.getEnergyEffService())));
            }
        } else if ("performance".equalsIgnoreCase(metric)) {
            for (IpEcoService table : results) {
                assessments.add(new EcoValue(table.getTimestamp(), new Double(table.getPerformanceService())));
            }
        } else if ("power".equalsIgnoreCase(metric)) {
            for (IpEcoService table : results) {
                assessments.add(new EcoValue(table.getTimestamp(), new Double(table.getPowerService())));
            }
        } else if ("co2".equalsIgnoreCase(metric)) {
            for (IpEcoService table : results) {
                assessments.add(new EcoValue(table.getTimestamp(), new Double(table.getGrCo2sService())));
            }
        }

        return assessments;
    }

    public static IpEcoService getEcoAssessmentById(Integer id) throws Exception {
        Session session = null;
        List<IpEcoService> result = null;
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try {
            session = sf.openSession();
            Transaction tx = session.beginTransaction();
            tx.begin();
            Criteria criteria = session.createCriteria(IpEcoService.class);
            criteria.add(Restrictions.like("id", id));
            result = (List<IpEcoService>) criteria.list();
            tx.commit();
        } catch (Exception e) {
            log.error("Error while obtaining assessment with id " + id.toString());
            throw new Exception(e.toString());
        }

        if (result.size() == 0) {
            log.warn("Couldn't find any Eco Assessment with id " + id.toString());
            return null;
        }

        if (result.size() > 1) {
            log.error("Obtained non-unique association between id and assessment.");
            throw new Exception("Obtained non-unique association between id and assessment.");
        }
        return result.get(0);
    }

    public static void removeEcoAssessmentById(Integer id) throws Exception {
        Session session = null;
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try {
            IpEcoService toRemove = getEcoAssessmentById(id);
            session = sf.openSession();
            Transaction tx = session.beginTransaction();
            tx.begin();
            session.delete(toRemove);
            tx.commit();
        } catch (Exception e) {
            log.error("Error while deleting assessment with id " + id.toString());
            throw new Exception(e.toString());
        }
    }
}
