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
import eu.optimis.trec.common.db.ip.model.IpEcoNodeTable;
import java.util.*;
import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author jsubirat
 */
public class EcoNodeTableDAO {

    private static Logger log = Logger.getLogger(EcoNodeTableDAO.class);

    public static Integer addEcoAssessment(String nodeId, double energyEffAssessment, double ecologicalEffAssessment, double performance, double power, double grCO2) throws Exception {
        Session session = null;
        Integer ret = null;
        IpEcoNodeTable nodeTable = new IpEcoNodeTable();
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try {
            session = sf.openSession();
            Transaction tx = session.beginTransaction();
            tx.begin();

            nodeTable.setNodeId(nodeId);
            nodeTable.setEnergyEffNode(energyEffAssessment);
            nodeTable.setEcologicalEffNode(ecologicalEffAssessment);
            nodeTable.setPerformanceNode(performance);
            nodeTable.setPowerNode(power);
            nodeTable.setGrCo2sNode(grCO2);

            ret = (Integer) session.save(nodeTable);
            tx.commit();
            if (tx.wasCommitted()) {
                log.debug("Node " + nodeId + " assessment was commited correctly with id " + ret.toString() + ".");
            } else {
                log.error("Unable to insert node " + nodeId + " assessment data.");
            }
        } catch (Exception e) {
            log.error("Unable to insert node " + nodeId + " assessment data: " + e.getMessage());
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
    public static List<EcoValue> getEcoAssessments(String nodeId, String ini, String end, String metric) throws Exception {
        List<EcoValue> assessments = new LinkedList<EcoValue>();
        Session session = null;
        List<IpEcoNodeTable> results = null;
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try {
            session = sf.openSession();
            Transaction tx = session.beginTransaction();
            tx.begin();
            // Obtain ecoAssessments in a range of time.
            Criteria criteria = session.createCriteria(IpEcoNodeTable.class);
            Date startDate = DateFormatter.getDateFromString(ini);
            Date endDate = DateFormatter.getDateFromString(end);
            //criteria.add(Expression.between("timestamp", new Date(startDate.getTime()), new Date(endDate.getTime())));
            criteria.add(Restrictions.like("nodeId", nodeId));
            criteria.add(Restrictions.between("timestamp", startDate, endDate));
            results = (List<IpEcoNodeTable>) criteria.list();
            tx.commit();
        } catch (Exception e) {
            log.error("Error while obtaining node " + nodeId + " assessments: " + e.getMessage());
            throw new Exception(e.toString());
        }

        if ("ecological".equalsIgnoreCase(metric)) {
            for (IpEcoNodeTable table : results) {
                assessments.add(new EcoValue(table.getTimestamp(), new Double(table.getEcologicalEffNode())));
            }
        } else if ("energy".equalsIgnoreCase(metric)) {
            for (IpEcoNodeTable table : results) {
                assessments.add(new EcoValue(table.getTimestamp(), new Double(table.getEnergyEffNode())));
            }
        } else if ("performance".equalsIgnoreCase(metric)) {
            for (IpEcoNodeTable table : results) {
                assessments.add(new EcoValue(table.getTimestamp(), new Double(table.getPerformanceNode())));
            }
        } else if ("power".equalsIgnoreCase(metric)) {
            for (IpEcoNodeTable table : results) {
                assessments.add(new EcoValue(table.getTimestamp(), new Double(table.getPowerNode())));
            }
        } else if ("co2".equalsIgnoreCase(metric)) {
            for (IpEcoNodeTable table : results) {
                assessments.add(new EcoValue(table.getTimestamp(), new Double(table.getGrCo2sNode())));
            }
        }

        return assessments;
    }

    public static IpEcoNodeTable getEcoAssessmentById(Integer id) throws Exception {
        Session session = null;
        List<IpEcoNodeTable> result = null;
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try {
            session = sf.openSession();
            Transaction tx = session.beginTransaction();
            tx.begin();
            Criteria criteria = session.createCriteria(IpEcoNodeTable.class);
            criteria.add(Restrictions.like("id", id));
            result = (List<IpEcoNodeTable>) criteria.list();
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
            IpEcoNodeTable toRemove = getEcoAssessmentById(id);
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
