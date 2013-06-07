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
package eu.optimis.mi.gui.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.mysql.jdbc.Driver;
import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;
import eu.optimis.ecoefficiencytool.rest.client.EcoEfficiencyToolRESTClientIP;
import eu.optimis.ecoefficiencytool.rest.client.EcoEfficiencyToolRESTClientSP;

import eu.optimis.ecoefficiencytool.trecdb.ip.EcoNodeTableDAO;
import eu.optimis.ecoefficiencytool.trecdb.ip.EcoIpTableDAO;
import eu.optimis.ecoefficiencytool.trecdb.ip.EcoVMTableDAO;
import eu.optimis.mi.gui.client.MonitoringManagerWebService;
import eu.optimis.mi.gui.client.model.CostResourceIP;
import eu.optimis.mi.gui.client.model.EcoResource;
import eu.optimis.mi.gui.client.model.Ip2SpModel;
import eu.optimis.mi.gui.client.model.MonitoringResource;
import eu.optimis.mi.gui.client.model.RiskResource;
import eu.optimis.mi.gui.client.model.Sp2IpModel;
import eu.optimis.mi.gui.client.model.TrustResourceSP;

import eu.optimis.mi.gui.client.model.*;
import java.text.DecimalFormat;

@SuppressWarnings("serial")
public class MonitoringManagerWebServiceImpl extends RemoteServiceServlet
        implements MonitoringManagerWebService {

    public static String MMANAGER_URL;

    public MonitoringManagerWebServiceImpl() {
        System.out.println("in MonitoringManagerWebServiceImpl");
        try {
            ResourceBundle rb = ResourceBundle.getBundle("mmweb",
                    Locale.getDefault());
            System.out.println("h1");
            MMANAGER_URL = rb.getString("mmanager.url");
        } catch (java.util.MissingResourceException e) {
            GWT.log("Cannot find property file mmweb");
            e.printStackTrace();
            System.out.println("error 1" + e);
        } catch (Exception ex) {
            GWT.log("Cannot find property: mmanager.url");
            System.out.println("error 2" + ex);
        }
    }

    // Django: New risk starts here...
    @Override
    public List<RiskResource> getRiskResources(String serviceId,
            String providerId, String providerType, String servicePhase,
            String fromDate, String toDate, String test) {

        System.out.println("Risk Report: Fetching RiskResources");

        // List we're going to store variables in
        List<RiskResource> riskResourceList = new ArrayList<RiskResource>();

        // Read properties for DB access
        ResourceBundle rb = ResourceBundle.getBundle("mmweb",
                Locale.getDefault());
        // Using test data and thus test DB?
        String url;
        if (test.equals("false")) {
            url = rb.getString("db.risk.uri");
        } else {
            System.out.println("Risk Report: Using test data!");
            url = rb.getString("db.risk.uri") + "_test";
        }
        String user = rb.getString("db.risk.user");
        String password = rb.getString("db.risk.pass");

        System.out.println("Risk Report: called with getRiskResources(String "
                + serviceId + ", String " + providerId + ", String "
                + providerType + ", String " + servicePhase + ", String "
                + fromDate + ", String " + toDate + ")");

        System.out.println("Risk Report: db.risk.uri = " + url);

        System.out.println("Risk Report: Attempting to connect to risk DB...");

        Driver myDriver = null;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            myDriver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(myDriver);
            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();

            // First data point describes the data so we can access this client
            // side
            RiskResource riskResource = new RiskResource();
            riskResource.setServiceId(serviceId);
            riskResource.setProviderId(providerId);
            riskResource.setProviderType(providerType);
            riskResource.setServicePhase(servicePhase);
            riskResourceList.add(riskResource);

            // Creates a SQL string here dependent on the functions
            String serviceIdValue = null;
            String providerIdValue = null;
            String tableName = null;
            String sql = null;

            // Resolve the ID of the provider from the name
            sql = "SELECT id FROM provider_id WHERE name = \""
                    + providerId.toLowerCase() + "\"";
            System.out.println("Risk Report: Using query " + sql);

            try {
                rs = stmt.executeQuery(sql);
                rs.next();
                providerIdValue = rs.getString(1);
            } catch (SQLException e) {
                System.out
                        .println("Risk Report: Error can't find Provider ID with submitted name \""
                        + providerId.toLowerCase() + "\"!");
                e.printStackTrace();
                riskResource = new RiskResource();
                riskResource
                        .setErrorMessage("Error: Can't find Provider ID with submitted name \""
                        + providerId.toLowerCase()
                        + "\"! [Error Message: "
                        + e.getMessage()
                        + " Cause " + e.getStackTrace()[0] + "]");
                riskResourceList.clear();
                riskResourceList.add(riskResource);
                return riskResourceList;
            }

            rs.close();
            System.out.println("Risk Report: ProviderId resolved from name \""
                    + providerId + "\" to value \"" + providerIdValue + "\"");

            // Resolve the ID of the service from the name
            sql = "SELECT id FROM service_id WHERE name = \""
                    + serviceId.toLowerCase() + "\"";
            System.out.println("Risk Report: Using query " + sql);

            try {
                rs = stmt.executeQuery(sql);
                rs.next();
                serviceIdValue = rs.getString(1);
            } catch (SQLException e) {
                System.out
                        .println("Risk Report: Error can't find service ID with submitted name \""
                        + providerId.toLowerCase() + "\"!");
                e.printStackTrace();
                riskResource = new RiskResource();
                riskResource
                        .setErrorMessage("Error: Can't find Service ID with submitted name \""
                        + providerId.toLowerCase()
                        + "\"! [Error Message: "
                        + e.getMessage()
                        + " Cause " + e.getStackTrace()[0] + "]");
                riskResourceList.clear();
                riskResourceList.add(riskResource);
                return riskResourceList;
            }

            rs.close();
            System.out.println("Risk Report: ServiceId resolved from name \""
                    + serviceId + "\" to value \"" + serviceIdValue + "\"");

            String limit = "";
            
            // Tables and ID type selection mechanism
            if (providerType.equals("sp")) {
                if (servicePhase.equals("deployment")) {
                    // Service Provider Deployment
                    tableName = "sp_deployment";
                    limit = " DESC LIMIT 2";
                } else if (servicePhase.equals("operation")) {
                    // Service Provider Operation
                    tableName = "sp_operation";
                } else {
                    System.out
                            .println("Risk Report: Error, unknown servicePhase!");
                    riskResource = new RiskResource();
                    riskResource
                            .setErrorMessage("Error: Unknown servicePhase!");
                    riskResourceList.clear();
                    riskResourceList.add(riskResource);
                    return riskResourceList;
                }
            } else if (providerType.equals("ip")) {
                if (servicePhase.equals("deployment")) {
                    // Infrastructure Provider Deployment
                    tableName = "ip_deployment";
                    limit = " DESC LIMIT 1";
                } else if (servicePhase.equals("operation")) {
                    // Service Provider Operation
                    tableName = "ip_operation";
                } else {
                    System.out
                            .println("Risk Report: Error, unknown servicePhase!");
                    riskResource = new RiskResource();
                    riskResource
                            .setErrorMessage("Error: Unknown servicePhase!");
                    riskResourceList.clear();
                    riskResourceList.add(riskResource);
                    return riskResourceList;
                }
            } else {
                System.out.println("Risk Report: Error, unknown providerType!");
                riskResource = new RiskResource();
                riskResource.setErrorMessage("Error: Unknown providerType!");
                riskResourceList.clear();
                riskResourceList.add(riskResource);
                return riskResourceList;
            }

            // Create the SQL string here
            sql = "SELECT `timeStamp`, `riskValue`, `graphType` FROM "
                    + tableName + " WHERE `providerId` = \"" + providerIdValue
                    + "\" AND `serviceId` = \"" + serviceIdValue
                    + "\" AND `timeStamp` BETWEEN \""
                    + Long.parseLong(fromDate) + "\" AND \""
                    + Long.parseLong(toDate) + "\" ORDER BY `timeStamp`" + limit;

            System.out.println("Risk Report: Using query " + sql);

            // Execute the query
            rs = stmt.executeQuery(sql);

            if (rs.last() == false) {
                System.out
                        .println("Risk Report: Error, results set size is 0!");
                riskResource = new RiskResource();
                riskResource
                        .setErrorMessage("Error: results set size is 0, nothing to graph!");
                riskResourceList.clear();
                riskResourceList.add(riskResource);
                return riskResourceList;
            }
            System.out.println("Risk Report: Results set size is "
                    + rs.getRow());
            rs.beforeFirst();

            // Loop through the result set
            while (rs.next()) {
                riskResource = new RiskResource();
                riskResource.setTimeStamp(rs.getString(1));
                riskResource.setRiskValue(rs.getString(2)); // use 000
                // so that
                // we java
                // date
                // format
                riskResource.setGraphType(rs.getString(3));
                riskResourceList.add(riskResource);
            }

            // Close the result set, statement and the connection
            rs.close();
            stmt.close();
            conn.close();
            System.out.println("Risk Report: Closed risk DB connection");
        } catch (SQLException e) {
            System.out
                    .println("Risk Report: Error unable to fetch Risk data from DB");
            System.out.println("Risk Report: Stack trace is as follows...");
            e.printStackTrace();
            RiskResource riskResource = new RiskResource();
            riskResource
                    .setErrorMessage("Error: Unable to fetch Risk data from DB! [Message: "
                    + e.getMessage()
                    + ", Cause "
                    + e.getStackTrace()[0] + "]");
            riskResourceList.clear();
            riskResourceList.add(riskResource);
            return riskResourceList;
        } finally {
            System.out.println("Risk Report: Cleaning up DB connection");
            try {
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
            }

        }
        return riskResourceList;
    }

    // Django: Other crap starts here...
    public List<MonitoringResource> getMonitoringResources(String level,
            String id) {
        String xml = new String("");
        String urlString;
        if (level.equals("service")) {
            urlString = MMANAGER_URL + "QueryResources/group/complete/service/"
                    + id;
        } else if (level.equals("virtual")) {
            urlString = MMANAGER_URL + "QueryResources/group/complete/virtual/"
                    + id;
        } else if (level.equals("physical")) {
            urlString = MMANAGER_URL
                    + "QueryResources/group/complete/physical/" + id;
        } else if (level.equals("energy")) {
            urlString = MMANAGER_URL + "QueryResources/group/complete/energy/"
                    + id;
        } else {
            return new ArrayList<MonitoringResource>();
        }

        try {

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/XML");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String li;
            while ((li = br.readLine()) != null) {
                xml = xml.concat(li);
            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        XmlUtil util = new XmlUtil();
        MonitoringResource mr = new MonitoringResource();
        List<MonitoringResource> list;
        if (xml != null && xml.length() > 10) {
            list = util.getMonitoringRsModel(xml);
        } else {
            list = new ArrayList<MonitoringResource>();
            list.add(mr);
        }
        return list;
    }

    public List<MonitoringResource> getIdMetricDateListMonitoringResources(
            String id, String level, String metricName, String dfrom, String dto) {
        String xml = new String("");

        String urlString = MMANAGER_URL + "QueryResources/date/metric/"
                + metricName + "/" + level + "/" + id + "/" + dfrom + "." + dto;

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/XML");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            System.out.println("Output from Server... \n" + dfrom.toString());
            String li;
            while ((li = br.readLine()) != null) {
                xml = xml.concat(li);
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            GWT.log("The URL:" + urlString);
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        XmlUtil util = new XmlUtil();
        MonitoringResource mr = new MonitoringResource();
        List<MonitoringResource> list;
        if (xml != null && xml.length() > 10) {
            list = util.getMonitoringRsModel(xml);
        } else {
            list = new ArrayList<MonitoringResource>();
            list.add(mr);
        }
        return list;
    }

    public String getIdMetricDateStrMonitoringResources(String id,
            String level, String metricName, String dfrom, String dto) {
        String xml = new String("");

        String urlString = MMANAGER_URL + "QueryResources/date/metric/"
                + metricName + "/" + level + "/" + id + "/" + dfrom + "." + dto;
        System.out.println("Metric monitoring resource URL:" + urlString);
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/XML");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            System.out.println("Output from Server... \n" + dfrom.toString());
            String li;
            while ((li = br.readLine()) != null) {
                xml = xml.concat(li);
            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
        return xml;
    }

    @Override
    public List<TrustResourceSP> getSPTrustResources(String spId) {
        // TrecSPTrustDAO tsptdao = new TrecSPTrustDAO();
        // List<TrustResourceSP> trlist = new ArrayList<TrustResourceSP>();
        // try {
        // List<eu.optimis.trec.common.db.ip.model.SpTrust> sptList =
        // tsptdao.getSPTrusts(spId);
        // for (SpTrust spt : sptList){
        // TrustResourceSP tr = new TrustResourceSP();
        // tr.setSPId(spt.getSpInfo().getSpId());
        // tr.setSPTrust(String.valueOf(spt.getSpTrust()));
        // trlist.add(tr);
        // }
        // return trlist;
        // } catch (Exception e) {
        // return trlist;
        // }
        System.out.println("looking for SpId: " + spId);
        ResourceBundle rb = ResourceBundle.getBundle("mmweb",
                Locale.getDefault());
        List<TrustResourceSP> trlist = new ArrayList<TrustResourceSP>();
        try {
            String url = rb.getString("db.ip.host");
            String user = rb.getString("db.user");
            String password = rb.getString("db.pass");
            Driver myDriver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(myDriver);
            Connection conn = DriverManager.getConnection(url, user, password);
            // Get a statement from the connection
            Statement stmt = conn.createStatement();

            // Execute the query
            ResultSet rs = stmt
                    .executeQuery("SELECT  `sp_trust` FROM  `sp_trust` WHERE  `sp_id` =  '"
                    + spId + "' order by `tstamp` desc limit 100");

            // Loop through the result set
            while (rs.next()) {
                // System.out.println(rs.getString(1));
                TrustResourceSP tr = new TrustResourceSP();
                tr.setproviderId(spId);
                double spTrustDBValue = Double.valueOf(rs.getString(1)) * 5;
                tr.setproviderTrust(String.valueOf(spTrustDBValue));
                trlist.add(tr);
            }

            // Close the result set, statement and the connection
            rs.close();
            stmt.close();
            conn.close();
            return trlist;
        } catch (SQLException e) {
            System.out.println("Error: unable to load service provider trust");
            return trlist;
        }
    }

    public List<Sp2IpModel> getSp2IpInfo(String ipId) {
        ResourceBundle rb = ResourceBundle.getBundle("mmweb",
                Locale.getDefault());
        List<Sp2IpModel> trlist = new ArrayList<Sp2IpModel>();
        try {
            String url = rb.getString("db.sp.host");
            String user = rb.getString("db.user");
            String password = rb.getString("db.pass");
            Driver myDriver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(myDriver);
            Connection conn = DriverManager.getConnection(url, user, password);
            // Get a statement from the connection
            Statement stmt = conn.createStatement();

            // Execute the query
            ResultSet rs = stmt
                    .executeQuery("SELECT sp_id, service_id, service_well_formed, safety_run_gap, elasticity_closely, ip_reaction_time, sla_compliance, ip_compliance_with_legal, service_trust FROM `sp_to_ip` WHERE `ip_id`='"
                    + ipId
                    + "' ORDER BY `service_time` DESC LIMIT 100 ");

            // Loop through the result set
            while (rs.next()) {
                Sp2IpModel sp2ipmodel = new Sp2IpModel();
                sp2ipmodel.setIpId(ipId);
                // System.out.println(sp2ipmodel.getSpId());
                sp2ipmodel.setServiceId(rs.getString(2));
                // System.out.println(sp2ipmodel.getServiceId());
                sp2ipmodel.setServiceFormed(rs.getString(3));
                // System.out.println(sp2ipmodel.getServiceFormed());
                sp2ipmodel.setRunGap(rs.getString(4));
                // System.out.println(sp2ipmodel.getRunGap());
                sp2ipmodel.setElasticity(rs.getString(5));
                // System.out.println(sp2ipmodel.getElasticity());
                sp2ipmodel.setIpReaction(rs.getString(6));
                // System.out.println(sp2ipmodel.getIpReaction());
                sp2ipmodel.setSla(rs.getString(7));
                // System.out.println(sp2ipmodel.getSla());
                sp2ipmodel.setLegal(rs.getString(8));
                // System.out.println(sp2ipmodel.getLegal());
                sp2ipmodel.setServiceTrust(rs.getString(9));
                // System.out.println(sp2ipmodel.getServiceTrust());
                trlist.add(sp2ipmodel);
            }

            // Close the result set, statement and the connection
            rs.close();
            stmt.close();
            conn.close();
            return trlist;
        } catch (SQLException e) {
            System.out.println("Error: unable to load service provider trust");
            return trlist;
        }
    }

    public List<Ip2SpModel> getIp2SpInfo(String spId) {
        ResourceBundle rb = ResourceBundle.getBundle("mmweb",
                Locale.getDefault());
        List<Ip2SpModel> trlist = new ArrayList<Ip2SpModel>();
        try {
            String url = rb.getString("db.ip.host");
            String user = rb.getString("db.user");
            String password = rb.getString("db.pass");
            Driver myDriver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(myDriver);
            Connection conn = DriverManager.getConnection(url, user, password);
            // Get a statement from the connection
            Statement stmt = conn.createStatement();

            // Execute the query
            ResultSet rs = stmt
                    .executeQuery("SELECT `sp_id`, `service_id`, `service_risk`, `sercurity_assessment`, `service_reliability`, `performance`, `legal_openess`, `service_trust` FROM `ip_to_sp` WHERE  `sp_id` =  '"
                    + spId
                    + "' ORDER BY  `service_time` DESC LIMIT 100");

            // Loop through the result set
            while (rs.next()) {
                Ip2SpModel ip2spmodel = new Ip2SpModel();
                ip2spmodel.setSpId(spId);
                // System.out.println(ip2spmodel.getSpId());
                ip2spmodel.setServiceId(rs.getString(2));
                // System.out.println(ip2spmodel.getServiceId());
                ip2spmodel.setServiceRisk(rs.getString(3));
                // System.out.println(ip2spmodel.getServiceRisk());
                ip2spmodel.setSecurity(rs.getString(4));
                // System.out.println(ip2spmodel.getSecurity());
                ip2spmodel.setReliability(rs.getString(5));
                // System.out.println(ip2spmodel.getReliability());
                ip2spmodel.setPerformance(rs.getString(6));
                // System.out.println(ip2spmodel.getPerformance());
                ip2spmodel.setLegal(rs.getString(7));
                // System.out.println(ip2spmodel.getLegal());
                ip2spmodel.setServiceTrust(rs.getString(8));
                // System.out.println(ip2spmodel.getServiceTrust());
                trlist.add(ip2spmodel);
            }

            // Close the result set, statement and the connection
            rs.close();
            stmt.close();
            conn.close();
            return trlist;
        } catch (SQLException e) {
            System.out.println("Error: unable to load service provider trust");
            return trlist;
        }
    }

    @Override
    public List<TrustResourceSP> getIPTrustResources(String ipId) {
        System.out.println("looking for IpId: " + ipId);
        ResourceBundle rb = ResourceBundle.getBundle("mmweb",
                Locale.getDefault());
        List<TrustResourceSP> trlist = new ArrayList<TrustResourceSP>();
        try {
            String url = rb.getString("db.sp.host");
            String user = rb.getString("db.user");
            String password = rb.getString("db.pass");
            Driver myDriver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(myDriver);
            Connection conn = DriverManager.getConnection(url, user, password);
            // Get a statement from the connection
            Statement stmt = conn.createStatement();

            // Execute the query
            ResultSet rs = stmt
                    .executeQuery("SELECT  `ip_trust` FROM  `ip_trust` WHERE  `ip_id` =  '"
                    + ipId + "' order by `tstamp` desc limit 100");

            // Loop through the result set
            while (rs.next()) {
                // System.out.println(rs.getString(1));
                TrustResourceSP tr = new TrustResourceSP();
                tr.setproviderId(ipId);
                double ipTrustDBValue = Double.valueOf(rs.getString(1)) * 5;
                tr.setproviderTrust(String.valueOf(ipTrustDBValue));
                trlist.add(tr);
            }

            // Close the result set, statement and the connection
            rs.close();
            stmt.close();
            conn.close();
            return trlist;
        } catch (SQLException e) {
            System.out
                    .println("Error: unable to load infrastructure provider trust");
            return trlist;
        }
        // finally{
        // System.out.println("Error: unable to load infrastructure provider trust finally");
        // }
    }

    // ECO
    @Override
    public List<EcoResource> getNodesEcoResources(String ini, String end, String metric) {

        CloudOptimizerRESTClient co = new CloudOptimizerRESTClient(/* "optimis-ipvm" */);
        // CloudOptimizerRESTClient co = new
        // CloudOptimizerRESTClient("optimis-ipvm2.ds.cs.umu.se");
        List<String> nodes = co.getNodesId();
        List<EcoResource> nodesEco = new ArrayList<EcoResource>();
        for (String node : nodes) {
            nodesEco.addAll(getNodeEcoResources(node, ini, end, metric));
        }
        return nodesEco;
    }

    @Override
    public List<EcoResource> getNodeEcoResources(String nodeId, String ini, String end, String metric) {

        List<EcoResource> ecolist = new ArrayList<EcoResource>();
        try {
            List<eu.optimis.ecoefficiencytool.trecdb.ip.utils.EcoValue> ecos = EcoNodeTableDAO.getEcoAssessments(nodeId, ini, end, metric);
            String nodeEco = null;
            if (metric.equalsIgnoreCase("energy") || metric.equalsIgnoreCase("ecological")) {
                try {
                    EcoEfficiencyToolRESTClientIP ecoClient = new EcoEfficiencyToolRESTClientIP(/* "optimis-ipvm" */);
                    nodeEco = ecoClient.getNodeMaxEco(nodeId, metric);
                    DecimalFormat df = new DecimalFormat("#.##");
                    nodeEco = df.format(Double.parseDouble(nodeEco));
                } catch (Exception ex) {
                    System.out
                            .println("[MonitoringManagerWebServiceImpl] nodeEco is not available in this testbed.");
                }
            }
            
            for (eu.optimis.ecoefficiencytool.trecdb.ip.utils.EcoValue eco : ecos) {
                EcoResource er = new EcoResource();
                er.setNodeId(nodeId);
                if (nodeEco != null) {
                    er.setNodeEco(nodeEco);
                }
                er.setTimestamp(eco.getTimeStampString());
                er.setTimeLabel(getFormattedDate(eco.getTimeStampString()));
                er.setEcoValue(eco.getEcoValueString());
                er.setMetric(metric);
                ecolist.add(er);

            }
            return ecolist;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ecolist;
    }

    @Override
    public List<EcoResource> getServiceIPEcoResources(String serviceIPId, String ini, String end, String metric) {

        List<EcoResource> ecolist = new ArrayList<EcoResource>();
        try {
            List<eu.optimis.ecoefficiencytool.trecdb.ip.utils.EcoValue> ecos = eu.optimis.ecoefficiencytool.trecdb.ip.EcoServiceTableDAO
                    .getEcoAssessments(serviceIPId, ini, end, metric);
            for (eu.optimis.ecoefficiencytool.trecdb.ip.utils.EcoValue eco : ecos) {
                EcoResource er = new EcoResource();
                er.setNodeId(serviceIPId);
                er.setTimestamp(eco.getTimeStampString());
                er.setTimeLabel(getFormattedDate(eco.getTimeStampString()));
                er.setEcoValue(eco.getEcoValueString());
                er.setMetric(metric);
                ecolist.add(er);
            }
            /*
             * EcoResource er1 = new EcoResource(); er1.setNodeId(serviceIPId);
             * er1.setTimestamp("20120515000000"); er1.setEcoValue("12.23");
             * ecolist.add(er1); EcoResource er3 = new EcoResource();
             * er3.setNodeId(serviceIPId); er3.setTimestamp("20120515115959");
             * er3.setEcoValue("25.23"); ecolist.add(er3); EcoResource er2 = new
             * EcoResource(); er2.setNodeId(serviceIPId);
             * er2.setTimestamp("20120515235959"); er2.setEcoValue("15.23");
             * ecolist.add(er2); EcoResource er4 = new EcoResource();
             * er4.setNodeId(serviceIPId); er4.setTimestamp("20120515075959");
             * er4.setEcoValue("5.23"); ecolist.add(er4);
             */

            return ecolist;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ecolist;
    }

    @Override
    public List<EcoResource> getServiceSPEcoResources(String serviceSPId,
            String ini, String end, String metric) {

        List<EcoResource> ecolist = new ArrayList<EcoResource>();
        try {

            List<eu.optimis.ecoefficiencytool.trecdb.sp.utils.EcoValue> ecos = eu.optimis.ecoefficiencytool.trecdb.sp.EcoServiceTableDAO
                    .getEcoAssessments(serviceSPId, ini, end, metric);
            for (eu.optimis.ecoefficiencytool.trecdb.sp.utils.EcoValue eco : ecos) {
                EcoResource er = new EcoResource();
                er.setNodeId(serviceSPId);
                er.setTimestamp(eco.getTimeStampString());
                er.setTimeLabel(getFormattedDate(eco.getTimeStampString()));
                er.setEcoValue(eco.getEcoValueString());
                er.setMetric(metric);
                ecolist.add(er);
            }
            /*
             * EcoResource er1 = new EcoResource(); er1.setNodeId(nodeId);
             * er1.setTimestamp("20120515000000"); er1.setEcoValue("12.23");
             * ecolist.add(er1); EcoResource er3 = new EcoResource();
             * er3.setNodeId(nodeId); er3.setTimestamp("20120515115959");
             * er3.setEcoValue("25.23"); ecolist.add(er3); EcoResource er2 = new
             * EcoResource(); er2.setNodeId(nodeId);
             * er2.setTimestamp("20120515235959"); er2.setEcoValue("15.23");
             * ecolist.add(er2); EcoResource er4 = new EcoResource();
             * er4.setNodeId(nodeId); er4.setTimestamp("20120515075959");
             * er4.setEcoValue("5.23"); ecolist.add(er4);
             */
            return ecolist;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ecolist;
    }

    @Override
    public List<EcoResource> getVMEcoResources(String VMId, String ini,
            String end, String metric) {

        List<EcoResource> ecolist = new ArrayList<EcoResource>();
        try {

            List<eu.optimis.ecoefficiencytool.trecdb.ip.utils.EcoValue> ecos = EcoVMTableDAO.getEcoAssessments(VMId, ini,
                    end, metric);
            for (eu.optimis.ecoefficiencytool.trecdb.ip.utils.EcoValue eco : ecos) {
                EcoResource er = new EcoResource();
                er.setNodeId(VMId);
                er.setTimestamp(eco.getTimeStampString());
                er.setTimeLabel(getFormattedDate(eco.getTimeStampString()));
                er.setEcoValue(eco.getEcoValueString());
                er.setMetric(metric);
                ecolist.add(er);
            }
            /*
             * EcoResource er1 = new EcoResource(); er1.setNodeId(nodeId);
             * er1.setTimestamp("20120515000000"); er1.setEcoValue("42.23");
             * ecolist.add(er1); EcoResource er3 = new EcoResource();
             * er3.setNodeId(nodeId); er3.setTimestamp("20120515115959");
             * er3.setEcoValue("25.23"); ecolist.add(er3); EcoResource er2 = new
             * EcoResource(); er2.setNodeId(nodeId);
             * er2.setTimestamp("20120515235959"); er2.setEcoValue("45.23");
             * ecolist.add(er2); EcoResource er4 = new EcoResource();
             * er4.setNodeId(nodeId); er4.setTimestamp("20120515075959");
             * er4.setEcoValue("5.23"); ecolist.add(er4);
             */
            return ecolist;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ecolist;
    }

    @Override
    public List<EcoResource> getInfrastructureEcoResources(String ini,
            String end, String metric) {

        List<EcoResource> ecolist = new ArrayList<EcoResource>();
        try {

            List<eu.optimis.ecoefficiencytool.trecdb.ip.utils.EcoValue> ecos = EcoIpTableDAO.getEcoAssessments(ini, end, metric);
            for (eu.optimis.ecoefficiencytool.trecdb.ip.utils.EcoValue eco : ecos) {
                EcoResource er = new EcoResource();
                er.setTimestamp(eco.getTimeStampString());
                er.setTimeLabel(getFormattedDate(eco.getTimeStampString()));
                er.setEcoValue(eco.getEcoValueString());
                er.setMetric(metric);
                ecolist.add(er);
            }
            /*
             * EcoResource er1 = new EcoResource(); //er1.setNodeId(nodeId);
             * er1.setTimestamp("20120515000000"); er1.setEcoValue("1.23");
             * ecolist.add(er1); EcoResource er3 = new EcoResource();
             * //er3.setNodeId(nodeId); er3.setTimestamp("20120515115959");
             * er3.setEcoValue("2.23"); ecolist.add(er3); EcoResource er2 = new
             * EcoResource(); //er2.setNodeId(nodeId);
             * er2.setTimestamp("20120515235959"); er2.setEcoValue("1.23");
             * ecolist.add(er2); EcoResource er4 = new EcoResource();
             * //er4.setNodeId(nodeId); er4.setTimestamp("20120515075959");
             * er4.setEcoValue("5.23"); ecolist.add(er4);
             */
            return ecolist;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ecolist;
    }

    private String getFormattedDate(String timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date resultdate = new Date();
        resultdate.setTime(Long.parseLong(timeStamp));
        return sdf.format(resultdate);
    }

    @Override
    public EcoServiceDeploymentInfoData getServiceDeploymentEcoInfo() {

        CloudOptimizerRESTClient co = new CloudOptimizerRESTClient( /*"optimis-ipvm"*/ );
        EcoEfficiencyToolRESTClientIP ecoClient = new EcoEfficiencyToolRESTClientIP( /*"optimis-ipvm"*/ );

        EcoServiceDeploymentInfoData resource = new EcoServiceDeploymentInfoData();
        for (String nodeId : co.getNodesId()) {
            resource.addNodeInfoResource(new EcoNodeInfoResource(nodeId,
                    ecoClient.getCPUNumber(nodeId), ecoClient
                    .getNodeUsedCPUs(nodeId), ecoClient
                    .getMaxPerformance(nodeId), ecoClient
                    .getCPUPerformance(nodeId), ecoClient
                    .getPidle(nodeId), ecoClient.getPMax(nodeId),
                    ecoClient.getPIncr(nodeId)));
        }
        resource.setDeploymentOutput(ecoClient.getAllDeploymentMessages());
        return resource;
    }
    
    @Override
    public EcoServiceDeploymentInfoData getServiceDeploymentEcoInfoSP() {

        EcoEfficiencyToolRESTClientSP ecoClient = new EcoEfficiencyToolRESTClientSP( /*"optimis-spvm"*/ );
        EcoServiceDeploymentInfoData resource = new EcoServiceDeploymentInfoData();
        resource.setDeploymentOutput(ecoClient.getAllDeploymentMessages());
        return resource;
    }

    // COST
    @Override
    public List<CostResourceIP> getSPCostResources(String spId) {

        ResourceBundle rb = ResourceBundle.getBundle("mmweb",
                Locale.getDefault());
        List<CostResourceIP> trlist = new ArrayList<CostResourceIP>();
        try {
            String url = rb.getString("db.ip.host");
            String user = rb.getString("db.user");
            String password = rb.getString("db.pass");
            Driver myDriver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(myDriver);
            Connection conn = DriverManager.getConnection(url, user, password);
            // Get a statement from the connection
            Statement stmt = conn.createStatement();

            // Execute the query

            ResultSet rs = stmt
                    .executeQuery("SELECT `CostPerVCPU` FROM `cost_ip_assessed_cost` WHERE `id` = '"
                    + spId + "' limit 100");

            // Loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString(1));
                CostResourceIP tr = new CostResourceIP();
                tr.setProviderId(spId);
                // double spCostDBValue = Double.valueOf(rs.getString(1)) *5;
                // long spCostDBValue = Long.valueOf(rs.getString(1)) *5;
                // tr.setproviderCost(String.valueOf(spCostDBValue));
                tr.setCostPerVCPU(rs.getString(1));
                trlist.add(tr);
            }

            // Close the result set, statement and the connection
            rs.close();
            stmt.close();
            conn.close();
            return trlist;
        } catch (SQLException e) {
            System.out
                    .println("Error: unable to load infrastructure provider cost");
            return trlist;
        } finally {
            System.out
                    .println("Error: unable to load infrastructure provider cost finally");
        }
    }

    @Override
    public List<CostResourceSP> getSPPredictionCostResources(String ipId,
            String assessorId) {
        System.out.println("looking for IpId: " + ipId);
        System.out.println("looking for Assessor Id: " + assessorId);

        ResourceBundle rb = ResourceBundle.getBundle("mmweb",
                Locale.getDefault());
        List<CostResourceSP> trlist = new ArrayList<CostResourceSP>();

        try {
            String url = rb.getString("db.sp.host");
            String user = rb.getString("db.user");
            String password = rb.getString("db.pass");
            Driver myDriver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(myDriver);
            Connection conn = DriverManager.getConnection(url, user, password);
            // Get a statement from the connection
            Statement stmt = conn.createStatement();

            // Execute the query

            ResultSet rs = stmt
                    .executeQuery("SELECT `plan_cap` FROM `cost_sp_persisted_quote` WHERE `id` = '"
                    + ipId
                    + "' and `service_name` = '"
                    + assessorId
                    + "' limit 20");
            // Loop through the result set
            double average = 0;
            double count = 0;

            while (rs.next()) {
                average = Double.valueOf(rs.getString(1)) + average;
                count++;
                if (rs.isLast()) {
                    average = average / count;
                }
            }
            System.out.println("Count: " + count);
            System.out.println("Average: " + count);
            String averageString = Double.toString(average);
            rs.first();
            while (rs.next()) {
                CostResourceSP tr = new CostResourceSP();
                tr.setProviderId(ipId);
                System.out.println("planCAP: " + rs.getString(1));
                tr.setPlanCAP(rs.getString(1));
                System.out.println("average:" + averageString);
                tr.setCostAVRG(averageString);

                trlist.add(tr);
            }
            /*
             * while (rs.next()) { System.out.println("planCAP: " +
             * rs.getString(1)); CostResourceSP tr = new CostResourceSP();
             * tr.setProviderId(ipId); average = Double.valueOf(rs.getString(1))
             * + average;
             * 
             * tr.setPlanCAP(rs.getString(1));
             * 
             * 
             * trlist.add(tr); } CostResourceSP ta = new CostResourceSP();
             * average = average / 20; String averageString =
             * Double.toString(average); System.out.println("average:" +
             * averageString); ta.setCostAVRG(averageString);
             * 
             * trlist.add(ta);
             */

            // Close the result set, statement and the connection
            rs.close();
            stmt.close();
            conn.close();
            return trlist;
        } catch (SQLException e) {
            System.out
                    .println("Error: unable to load infrastructure provider cost");
            return trlist;
        } finally {
            System.out
                    .println("Error: unable to load infrastructure provider cost finally");
        }
    }

    @Override
    public List<CostResourceIP> getSPServiceCostResources(String ipId,
            String assessorId) {
        System.out.println("looking for IpId: " + ipId);
        System.out.println("looking for Assessor Id: " + assessorId);

        ResourceBundle rb = ResourceBundle.getBundle("mmweb",
                Locale.getDefault());
        List<CostResourceIP> trlist = new ArrayList<CostResourceIP>();

        try {
            String url = rb.getString("db.ip.host");
            String user = rb.getString("db.user");
            String password = rb.getString("db.pass");
            Driver myDriver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(myDriver);
            Connection conn = DriverManager.getConnection(url, user, password);
            // Get a statement from the connection
            Statement stmt = conn.createStatement();

            // Execute the query

            ResultSet rs = stmt
                    .executeQuery("SELECT `CostPerVCPU`, `CostPerMBMemory`, `CostPerGBStorage`, `CostPerGBUploaded`, `CostPerGBDownloaded`, `CostPerWatt` FROM `cost_ip_assessed_cost` WHERE `id` = '"
                    + ipId
                    + "' and `service_name` = '"
                    + assessorId
                    + "' limit 50");
            // Loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString(1));
                CostResourceIP tr = new CostResourceIP();
                tr.setProviderId(ipId);

                double totalCost = Double.valueOf(rs.getString(1))
                        + Double.valueOf(rs.getString(2))
                        + Double.valueOf(rs.getString(3))
                        + Double.valueOf(rs.getString(4))
                        + Double.valueOf(rs.getString(5))
                        + Double.valueOf(rs.getString(6));
                String totalCostString = Double.toString(totalCost);
                System.out.println("total:" + totalCostString);
                tr.setCostPerVCPU(rs.getString(1));
                tr.setCostPerMBMemory(rs.getString(2));
                tr.setCostPerGBStorage(rs.getString(3));
                tr.setCostPerGBUploaded(rs.getString(4));
                tr.setCostPerGBDownloaded(rs.getString(5));
                tr.setCostPerWatt(rs.getString(6));
                tr.setCostTotal(totalCostString);

                trlist.add(tr);
            }

            // Close the result set, statement and the connection
            rs.close();
            stmt.close();
            conn.close();
            return trlist;
        } catch (SQLException e) {
            System.out
                    .println("Error: unable to load infrastructure provider Service cost");
            return trlist;
        } finally {
            System.out
                    .println("Error: unable to load infrastructure provider Service cost finally");
        }
    }

    @Override
    public List<CostResourceIP> getSPComponentCostResources(String ipId,
            String assessorId) {
        System.out.println("looking for IpId: " + ipId);
        System.out.println("looking for Assessor Id: " + assessorId);

        ResourceBundle rb = ResourceBundle.getBundle("mmweb",
                Locale.getDefault());
        List<CostResourceIP> trlist = new ArrayList<CostResourceIP>();

        try {
            String url = rb.getString("db.ip.host");
            String user = rb.getString("db.user");
            String password = rb.getString("db.pass");
            Driver myDriver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(myDriver);
            Connection conn = DriverManager.getConnection(url, user, password);

            Statement stmt = conn.createStatement();

            ResultSet rs = stmt
                    .executeQuery("SELECT `CostPerVCPU`, `CostPerMBMemory`, `CostPerGBStorage`, `CostPerGBUploaded`, `CostPerGBDownloaded`, `CostPerWatt` FROM `cost_ip_assessed_cost` WHERE `id` = '"
                    + ipId
                    + "' and `component_name` = '"
                    + assessorId
                    + "' limit 50");

            while (rs.next()) {
                System.out.println(rs.getString(1));
                CostResourceIP tr = new CostResourceIP();
                tr.setProviderId(ipId);

                double totalCost = Double.valueOf(rs.getString(1))
                        + Double.valueOf(rs.getString(2))
                        + Double.valueOf(rs.getString(3))
                        + Double.valueOf(rs.getString(4))
                        + Double.valueOf(rs.getString(5))
                        + Double.valueOf(rs.getString(6));
                String totalCostString = Double.toString(totalCost);
                System.out.println("total:" + totalCostString);
                tr.setCostPerVCPU(rs.getString(1));
                tr.setCostPerMBMemory(rs.getString(2));
                tr.setCostPerGBStorage(rs.getString(3));
                tr.setCostPerGBUploaded(rs.getString(4));
                tr.setCostPerGBDownloaded(rs.getString(5));
                tr.setCostPerWatt(rs.getString(6));
                tr.setCostTotal(totalCostString);

                trlist.add(tr);

            }

            rs.close();
            stmt.close();
            conn.close();

            return trlist;

        } catch (SQLException e) {
            System.out.println("Error: unable to load Component cost");
            return trlist;
        } finally {
            System.out.println("Error: unable to load Component cost finally");
        }
    }

    @Override
    public List<CostResourceIP> getIPCostResources(String ipId) {
        System.out.println("looking for IpId: " + ipId);

        ResourceBundle rb = ResourceBundle.getBundle("mmweb",
                Locale.getDefault());
        List<CostResourceIP> trlist = new ArrayList<CostResourceIP>();

        try {
            String url = rb.getString("db.ip.host");
            String user = rb.getString("db.user");
            String password = rb.getString("db.pass");
            Driver myDriver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(myDriver);
            Connection conn = DriverManager.getConnection(url, user, password);
            // Get a statement from the connection
            Statement stmt = conn.createStatement();

            // Execute the query

            ResultSet rs = stmt
                    .executeQuery("SELECT `CostPerVCPU` FROM `cost_ip_assessed_cost` WHERE `id` = '"
                    + ipId + "' limit 100");

            // Loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString(1));
                CostResourceIP tr = new CostResourceIP();
                tr.setProviderId(ipId);
                // double ipCostDBValue = Double.valueOf(rs.getString(1)) *5;
                // long ipCostDBValue = Long.valueOf(rs.getString(1)) *5;
                // tr.setproviderCost(String.valueOf(ipCostDBValue));
                tr.setCostPerVCPU(rs.getString(1));
                trlist.add(tr);
            }

            // Close the result set, statement and the connection
            rs.close();
            stmt.close();
            conn.close();
            return trlist;
        } catch (SQLException e) {
            System.out
                    .println("Error: unable to load infrastructure provider cost");
            return trlist;
        } finally {
            System.out
                    .println("Error: unable to load infrastructure provider cost finally");
        }
    }

    @Override
    public List<CostResourceIP> getIPServiceCostResources(String ipId,
            String assessorId) {
        System.out.println("looking for IpId: " + ipId);
        System.out.println("looking for Assessor Id: " + assessorId);

        ResourceBundle rb = ResourceBundle.getBundle("mmweb",
                Locale.getDefault());
        List<CostResourceIP> trlist = new ArrayList<CostResourceIP>();

        try {
            String url = rb.getString("db.ip.host");
            String user = rb.getString("db.user");
            String password = rb.getString("db.pass");
            Driver myDriver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(myDriver);
            Connection conn = DriverManager.getConnection(url, user, password);
            // Get a statement from the connection
            Statement stmt = conn.createStatement();

            // Execute the query

            ResultSet rs = stmt
                    .executeQuery("SELECT `CostPerVCPU`, `CostPerMBMemory`, `CostPerGBStorage`, `CostPerGBUploaded`, `CostPerGBDownloaded`, `CostPerWatt` FROM `cost_ip_assessed_cost` WHERE `id` = '"
                    + ipId
                    + "' and `service_name` = '"
                    + assessorId
                    + "' order by `time` desc limit 50");
            // Loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString(1));
                CostResourceIP tr = new CostResourceIP();
                tr.setProviderId(ipId);

                double totalCost = Double.valueOf(rs.getString(1))
                        + Double.valueOf(rs.getString(2))
                        + Double.valueOf(rs.getString(3))
                        + Double.valueOf(rs.getString(4))
                        + Double.valueOf(rs.getString(5))
                        + Double.valueOf(rs.getString(6));
                String totalCostString = Double.toString(totalCost);
                System.out.println("total:" + totalCostString);
                tr.setCostPerVCPU(rs.getString(1));
                tr.setCostPerMBMemory(rs.getString(2));
                tr.setCostPerGBStorage(rs.getString(3));
                tr.setCostPerGBUploaded(rs.getString(4));
                tr.setCostPerGBDownloaded(rs.getString(5));
                tr.setCostPerWatt(rs.getString(6));
                tr.setCostTotal(totalCostString);

                trlist.add(tr);
            }

            // Close the result set, statement and the connection
            rs.close();
            stmt.close();
            conn.close();
            return trlist;
        } catch (SQLException e) {
            System.out
                    .println("Error: unable to load infrastructure provider Service cost");
            return trlist;
        } finally {
            System.out
                    .println("Error: unable to load infrastructure provider Service cost finally");
        }
    }

    @Override
    public List<CostResourceIP> getIPComponentCostResources(String ipId,
            String assessorId) {
        System.out.println("looking for IpId: " + ipId);
        System.out.println("looking for Assessor Id: " + assessorId);

        ResourceBundle rb = ResourceBundle.getBundle("mmweb",
                Locale.getDefault());
        List<CostResourceIP> trlist = new ArrayList<CostResourceIP>();

        try {
            String url = rb.getString("db.ip.host");
            String user = rb.getString("db.user");
            String password = rb.getString("db.pass");
            Driver myDriver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(myDriver);
            Connection conn = DriverManager.getConnection(url, user, password);

            Statement stmt = conn.createStatement();

            ResultSet rs = stmt
                    .executeQuery("SELECT `CostPerVCPU`, `CostPerMBMemory`, `CostPerGBStorage`, `CostPerGBUploaded`, `CostPerGBDownloaded`, `CostPerWatt` FROM `cost_ip_assessed_cost` WHERE `id` = '"
                    + ipId
                    + "' and `component_name` = '"
                    + assessorId
                    + "' limit 50");

            while (rs.next()) {
                System.out.println(rs.getString(1));
                CostResourceIP tr = new CostResourceIP();
                tr.setProviderId(ipId);

                double totalCost = Double.valueOf(rs.getString(1))
                        + Double.valueOf(rs.getString(2))
                        + Double.valueOf(rs.getString(3))
                        + Double.valueOf(rs.getString(4))
                        + Double.valueOf(rs.getString(5))
                        + Double.valueOf(rs.getString(6));
                String totalCostString = Double.toString(totalCost);
                System.out.println("total:" + totalCostString);
                tr.setCostPerVCPU(rs.getString(1));
                tr.setCostPerMBMemory(rs.getString(2));
                tr.setCostPerGBStorage(rs.getString(3));
                tr.setCostPerGBUploaded(rs.getString(4));
                tr.setCostPerGBDownloaded(rs.getString(5));
                tr.setCostPerWatt(rs.getString(6));
                tr.setCostTotal(totalCostString);

                trlist.add(tr);

            }

            rs.close();
            stmt.close();
            conn.close();

            return trlist;

        } catch (SQLException e) {
            System.out.println("Error: unable to load Component cost");
            return trlist;
        } finally {
            System.out.println("Error: unable to load Component cost finally");
        }
    }

    @Override
    public List<CostResourceIP> getIPNodeCostResources(String ipId,
            String assessorId) {
        System.out.println("looking for IpId: " + ipId);
        System.out.println("looking for Assessor Id: " + assessorId);

        ResourceBundle rb = ResourceBundle.getBundle("mmweb",
                Locale.getDefault());
        List<CostResourceIP> trlist = new ArrayList<CostResourceIP>();

        try {
            String url = rb.getString("db.ip.host");
            String user = rb.getString("db.user");
            String password = rb.getString("db.pass");
            Driver myDriver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(myDriver);
            Connection conn = DriverManager.getConnection(url, user, password);

            Statement stmt = conn.createStatement();

            ResultSet rs = stmt
                    .executeQuery("SELECT `CostPerVCPU`, `CostPerMBMemory`, `CostPerGBStorage`, `CostPerGBUploaded`, `CostPerGBDownloaded`, `CostPerWatt` FROM `cost_ip_assessed_cost` WHERE `id` = '"
                    + ipId
                    + "' and `node_name` = '"
                    + assessorId
                    + "' limit 50");

            while (rs.next()) {
                System.out.println("cpu:" + rs.getString(1));
                CostResourceIP tr = new CostResourceIP();
                tr.setProviderId(ipId);

                double totalCost = Double.valueOf(rs.getString(1))
                        + Double.valueOf(rs.getString(2))
                        + Double.valueOf(rs.getString(3))
                        + Double.valueOf(rs.getString(4))
                        + Double.valueOf(rs.getString(5))
                        + Double.valueOf(rs.getString(6));
                String totalCostString = Double.toString(totalCost);
                System.out.println("total:" + totalCostString);
                tr.setCostPerVCPU(rs.getString(1));
                tr.setCostPerMBMemory(rs.getString(2));
                tr.setCostPerGBStorage(rs.getString(3));
                tr.setCostPerGBUploaded(rs.getString(4));
                tr.setCostPerGBDownloaded(rs.getString(5));
                tr.setCostPerWatt(rs.getString(6));
                tr.setCostTotal(totalCostString);

                trlist.add(tr);
            }
            rs.close();
            stmt.close();
            conn.close();
            return trlist;

        } catch (SQLException e) {
            System.out.println("Error: unable to load Component cost");
            return trlist;
        } finally {
            System.out.println("Error: unable to load Component cost finally");
        }
    }
}
