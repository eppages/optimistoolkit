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
import eu.optimis.trec.common.db.ip.model.IpEcoVmTable;
import java.util.*;
import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author jsubirat
 */
public class EcoVMTableDAO {

    private static Logger log = Logger.getLogger(EcoVMTableDAO.class);

    public static Integer addEcoAssessment(String vmId, double energyEffAssessment, double ecologicalEffAssessment, double performance, double power, double grCO2) throws Exception {
        Session session = null;
        Integer ret = null;
        IpEcoVmTable vmTable = new IpEcoVmTable();
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try {
            session = sf.openSession();
            Transaction tx = session.beginTransaction();
            tx.begin();

            vmTable.setVmId(vmId);
            vmTable.setEnergyEffVm(energyEffAssessment);
            vmTable.setEcologicalEffVm(ecologicalEffAssessment);
            vmTable.setPerformanceVm(performance);
            vmTable.setPowerVm(power);
            vmTable.setGrCo2sVm(grCO2);

            ret = (Integer) session.save(vmTable);
            tx.commit();
            if (tx.wasCommitted()) {
                log.debug("VM " + vmId + " assessment was commited correctly with id " + ret.toString() + ".");
            } else {
                log.error("Unable to insert VM " + vmId + " assessment data.");
            }
        } catch (Exception e) {
            log.error("Unable to insert VM " + vmId + " assessment data: " + e.getMessage());
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
    public static List<EcoValue> getEcoAssessments(String vmId, String ini, String end, String metric) throws Exception {
        List<EcoValue> assessments = new LinkedList<EcoValue>();
        Session session = null;
        List<IpEcoVmTable> results = null;
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try {
            session = sf.openSession();
            Transaction tx = session.beginTransaction();
            tx.begin();
            // Obtain ecoAssessments in a range of time.
            Criteria criteria = session.createCriteria(IpEcoVmTable.class);
            Date startDate = DateFormatter.getDateFromString(ini);
            Date endDate = DateFormatter.getDateFromString(end);
            //criteria.add(Expression.between("timestamp", new Date(startDate.getTime()), new Date(endDate.getTime())));
            criteria.add(Restrictions.like("vmId", vmId));
            criteria.add(Restrictions.between("timestamp", startDate, endDate));
            results = (List<IpEcoVmTable>) criteria.list();
            tx.commit();
        } catch (Exception e) {
            log.error("Error while obtaining node " + vmId + " assessments: " + e.getMessage());
            throw new Exception(e.toString());
        }

        if ("ecological".equalsIgnoreCase(metric)) {
            for (IpEcoVmTable table : results) {
                assessments.add(new EcoValue(table.getTimestamp(), new Double(table.getEcologicalEffVm())));
            }
        } else if ("energy".equalsIgnoreCase(metric)) {
            for (IpEcoVmTable table : results) {
                assessments.add(new EcoValue(table.getTimestamp(), new Double(table.getEnergyEffVm())));
            }
        } else if ("performance".equalsIgnoreCase(metric)) {
            for (IpEcoVmTable table : results) {
                assessments.add(new EcoValue(table.getTimestamp(), new Double(table.getPerformanceVm())));
            }
        } else if ("power".equalsIgnoreCase(metric)) {
            for (IpEcoVmTable table : results) {
                assessments.add(new EcoValue(table.getTimestamp(), new Double(table.getPowerVm())));
            }
        } else if ("co2".equalsIgnoreCase(metric)) {
            for (IpEcoVmTable table : results) {
                assessments.add(new EcoValue(table.getTimestamp(), new Double(table.getGrCo2sVm())));
            }
        }

        return assessments;
    }

    public static IpEcoVmTable getEcoAssessmentById(Integer id) throws Exception {
        Session session = null;
        List<IpEcoVmTable> result = null;
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try {
            session = sf.openSession();
            Transaction tx = session.beginTransaction();
            tx.begin();
            Criteria criteria = session.createCriteria(IpEcoVmTable.class);
            criteria.add(Restrictions.like("id", id));
            result = (List<IpEcoVmTable>) criteria.list();
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
            IpEcoVmTable toRemove = getEcoAssessmentById(id);
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
