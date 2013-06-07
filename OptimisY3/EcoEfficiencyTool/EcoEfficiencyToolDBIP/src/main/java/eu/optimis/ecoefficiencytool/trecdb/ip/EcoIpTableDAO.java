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

//import eu.optimis.trec.common.db.ip.App
import eu.optimis.ecoefficiencytool.trecdb.ip.utils.DateFormatter;
import eu.optimis.ecoefficiencytool.trecdb.ip.utils.EcoValue;
import eu.optimis.ecoefficiencytool.trecdb.ip.utils.HibernateUtil;
import eu.optimis.trec.common.db.ip.model.IpEcoIpTable;
import java.util.*;
import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;

public class EcoIpTableDAO {

    private static Logger log = Logger.getLogger(EcoIpTableDAO.class);

    public static Integer addEcoAssessment(double energyEffAssessment, double ecologicalEffAssessment, double performance, double power, double grCO2) throws Exception {
        Session session = null;
        Integer ret = null;
        IpEcoIpTable ipTable = new IpEcoIpTable();
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try {
            session = sf.openSession();
            Transaction tx = session.beginTransaction();
            tx.begin();

            ipTable.setEnergyEffIp(energyEffAssessment);
            ipTable.setEcologicalEffIp(ecologicalEffAssessment);
            ipTable.setPerformanceIp(performance);
            ipTable.setPowerIp(power);
            ipTable.setGrCo2sIp(grCO2);

            ret = (Integer) session.save(ipTable);
            tx.commit();
            if (tx.wasCommitted()) {
                log.debug("Infrastructure assessment was commited correctly with id " + ret.toString() + ".");
            } else {
                log.error("Unable to insert infrastructure assessment data.");
            }
        } catch (Exception e) {
            log.error("Unable to insert infrastructure assessment data: " + e.getMessage());
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
    public static List<EcoValue> getEcoAssessments(String ini, String end, String metric) throws Exception {
        List<EcoValue> assessments = new LinkedList<EcoValue>();
        Session session = null;
        List<IpEcoIpTable> results = null;
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try {
            session = sf.openSession();
            Transaction tx = session.beginTransaction();
            tx.begin();
            // Obtain ecoAssessments in a range of time.
            Criteria criteria = session.createCriteria(IpEcoIpTable.class);
            Date startDate = DateFormatter.getDateFromString(ini);
            Date endDate = DateFormatter.getDateFromString(end);
            //criteria.add(Expression.between("timestamp", new Date(startDate.getTime()), new Date(endDate.getTime())));
            criteria.add(Restrictions.between("timestamp", startDate, endDate));
            results = (List<IpEcoIpTable>) criteria.list();
            tx.commit();
        } catch (Exception e) {
            log.error("Error while obtaining assessments: " + e.getMessage());
            throw new Exception(e.toString());
        }
        
        if ("ecological".equalsIgnoreCase(metric)) {
            for (IpEcoIpTable table : results) {
                assessments.add(new EcoValue(table.getTimestamp(), new Double(table.getEcologicalEffIp())));
            }
        } else if ("energy".equalsIgnoreCase(metric)) {
            for (IpEcoIpTable table : results) {
                assessments.add(new EcoValue(table.getTimestamp(), new Double(table.getEnergyEffIp())));
            }
        } else if ("performance".equalsIgnoreCase(metric)) {
            for (IpEcoIpTable table : results) {
                assessments.add(new EcoValue(table.getTimestamp(), new Double(table.getPerformanceIp())));
            }
        } else if ("power".equalsIgnoreCase(metric)) {
            for (IpEcoIpTable table : results) {
                assessments.add(new EcoValue(table.getTimestamp(), new Double(table.getPowerIp())));
            }
        } else if ("co2".equalsIgnoreCase(metric)) {
            for (IpEcoIpTable table : results) {
                assessments.add(new EcoValue(table.getTimestamp(), new Double(table.getGrCo2sIp())));
            }
        }

        return assessments;
    }

    public static IpEcoIpTable getEcoAssessmentById(Integer id) throws Exception {
        Session session = null;
        List<IpEcoIpTable> result = null;
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try {
            session = sf.openSession();
            Transaction tx = session.beginTransaction();
            tx.begin();
            Criteria criteria = session.createCriteria(IpEcoIpTable.class);
            criteria.add(Restrictions.like("id", id));
            result = (List<IpEcoIpTable>) criteria.list();
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
            IpEcoIpTable toRemove = getEcoAssessmentById(id);
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
