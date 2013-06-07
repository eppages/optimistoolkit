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
package eu.optimis.ip.gui.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;
import eu.optimis.ip.gui.client.IPManagerWebService;
import eu.optimis.ip.gui.client.model.COInfrastructureOperationData;
import eu.optimis.ip.gui.client.model.COServiceOperationData;
import eu.optimis.ip.gui.client.model.IP;
import eu.optimis.ip.gui.client.model.InfrastructureDataResource;
import eu.optimis.ip.gui.client.model.ServiceDataResource;
import eu.optimis.ip.gui.client.resources.Accounting;
import eu.optimis.ip.gui.client.resources.ConfigManager;
import eu.optimis.ip.gui.client.resources.Constants;
import eu.optimis.ip.gui.client.userwidget.graph.GraphicReportIPConfigDiagramPanel;
import eu.optimis.schemas.trec.blo.BusinessDescription;
import eu.optimis.schemas.trec.blo.Constraints;
import eu.optimis.schemas.trec.blo.Objective;
import eu.optimis.schemas.trec.blo.ObjectiveType;
import eu.optimis.cbr.client.CBRClient;
import eu.optimis.ipdiscovery.datamodel.Provider;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.hsqldb.Server;
import org.mindrot.jbcrypt.BCrypt;

@SuppressWarnings("serial")
public class IPManagerWebServiceImpl extends RemoteServiceServlet implements IPManagerWebService {

    public static String MMANAGER_URL;
    private static Logger logger = Logger.getLogger(IPManagerWebServiceImpl.class);
    ;
    PropertiesConfiguration configIPManagerWeb;
    private static ArrayList<String> session_ids = new ArrayList<String>();// = -1;
    private static ArrayList<Double> session_times = new ArrayList<Double>();// = -1;
    private static ArrayList<String> session_users = new ArrayList<String>();// = -1;
    //Key newUserKeyUnique;
    private static String userKeyUnique;
    private Accounting accounting;
    //DatastoreService datastore;

    public IPManagerWebServiceImpl() {
        PropertyConfigurator.configure(ConfigManager.getFilePath(ConfigManager.LOG4J_CONFIG_FILE));

        configIPManagerWeb = ConfigManager.getPropertiesConfiguration(ConfigManager.IPMANAGER_CONFIG_FILE);
        try {
            accounting = new Accounting();
            //accounting.start();
        } catch (SQLException ex) {
            logger.error("Could not initialize database.");
            logger.error(ex);
        }
        if (session_ids.size() == 0) {
            session_ids.add("-1");
            session_users.add("no_user");
            session_times.add(0.0);
        }
        userKeyUnique = "1";
    }

    @Override
    public void setBLO(String BLO, List<String> constraints) {

        BusinessDescription bd = new BusinessDescription();
        bd.setSender("http://localhost:8080/IPManagerWeb");
        CloudOptimizerRESTClient co = null;
        co = new CloudOptimizerRESTClient();
        Objective obj = new Objective();
        bd.setObjective(obj);
        if (BLO.equalsIgnoreCase(GraphicReportIPConfigDiagramPanel.STR_BLO_TRUST)) {
            obj.setType(ObjectiveType.MAX_TRUST);
        } else if (BLO.equalsIgnoreCase(GraphicReportIPConfigDiagramPanel.STR_BLO_RISK)) {
            obj.setType(ObjectiveType.MIN_RISK);
        } else if (BLO.equalsIgnoreCase(GraphicReportIPConfigDiagramPanel.STR_BLO_ECOLOGICAL)) {
            obj.setType(ObjectiveType.MAX_ECO);
        } else if (BLO.equalsIgnoreCase(GraphicReportIPConfigDiagramPanel.STR_BLO_ENERGY)) {
            obj.setType(ObjectiveType.MAX_ENERGY_EFF);
        } else if (BLO.equalsIgnoreCase(GraphicReportIPConfigDiagramPanel.STR_BLO_TRUST)) {
            obj.setType(ObjectiveType.MIN_COST);
        }
        Constraints ct = new Constraints();
        boolean hasConstraints = false;
        if (constraints != null) {
            Iterator iter = constraints.iterator();
            while (iter.hasNext()) {
                String c = iter.next().toString();
                if (c != null) {
                    c = c.trim();
                    if (c.startsWith(GraphicReportIPConfigDiagramPanel.STR_CONSTRAINT_TRUST_GET)) {
                        try {
                            Double v = new Double(c.substring(GraphicReportIPConfigDiagramPanel.STR_CONSTRAINT_TRUST_GET.length()).trim());
                            ct.setTrustGreaterThan(v);
                            hasConstraints = true;
                        } catch (Exception e) {
                            logger.warn("It seems that '" + GraphicReportIPConfigDiagramPanel.STR_CONSTRAINT_TRUST_GET + "' constraint has an error: " + e.getMessage());
                        }
                    } else if (c.startsWith(GraphicReportIPConfigDiagramPanel.STR_CONSTRAINT_RISK_LET)) {
                        try {
                            Integer v = new Integer(c.substring(GraphicReportIPConfigDiagramPanel.STR_CONSTRAINT_RISK_LET.length()).trim());
                            ct.setRiskLessThan(v);
                            hasConstraints = true;
                        } catch (Exception e) {
                            logger.warn("It seems that '" + GraphicReportIPConfigDiagramPanel.STR_CONSTRAINT_RISK_LET + "' constraint has an error: " + e.getMessage());
                        }
                    } else if (c.startsWith(GraphicReportIPConfigDiagramPanel.STR_CONSTRAINT_ECOLOGICAL_GET)) {
                        try {
                            Double v = new Double(c.substring(GraphicReportIPConfigDiagramPanel.STR_CONSTRAINT_ECOLOGICAL_GET.length()).trim());
                            ct.setEcoGreaterThan(v);
                            hasConstraints = true;
                        } catch (Exception e) {
                            logger.warn("It seems that '" + GraphicReportIPConfigDiagramPanel.STR_CONSTRAINT_ECOLOGICAL_GET + "' constraint has an error: " + e.getMessage());
                        }
                    } else if (c.startsWith(GraphicReportIPConfigDiagramPanel.STR_CONSTRAINT_ENERGY_GET)) {
                        try {
                            Double v = new Double(c.substring(GraphicReportIPConfigDiagramPanel.STR_CONSTRAINT_ENERGY_GET.length()).trim());
                            ct.setEnergyEfficiencyGreaterThan(v);
                            hasConstraints = true;
                        } catch (Exception e) {
                            logger.warn("It seems that '" + GraphicReportIPConfigDiagramPanel.STR_CONSTRAINT_ENERGY_GET + "' constraint has an error: " + e.getMessage());
                        }
                    } else if (c.startsWith(GraphicReportIPConfigDiagramPanel.STR_CONSTRAINT_COST_LET)) {
                        try {
                            Double v = new Double(c.substring(GraphicReportIPConfigDiagramPanel.STR_CONSTRAINT_COST_LET.length()).trim());
                            ct.setCostLessThan(v);
                            hasConstraints = true;
                        } catch (Exception e) {
                            logger.warn("It seems that '" + GraphicReportIPConfigDiagramPanel.STR_CONSTRAINT_COST_LET + "' constraint has an error: " + e.getMessage());
                        }
                    }
                }
            }
        }
        if (hasConstraints) {
            bd.setConstraints(ct);
        }
        co.addBLO(bd);
    }

    @Override
    public String getTRECUrl() {
        return configIPManagerWeb.getString("url.TREC");
    }

    @Override
    public String getMonitoringUrl() {
        return configIPManagerWeb.getString("url.Monitoring");
    }

    @Override
    public String getDMUrl() {
        return configIPManagerWeb.getString("url.DM");
    }

    @Override
    public String getEmotiveUrl() {
        return configIPManagerWeb.getString("url.EMOTIVE");
    }

    @Override
    public String getCOUrl() {
        return configIPManagerWeb.getString("url.CO");
    }

    @Override
    public String getACUrl() {
        return configIPManagerWeb.getString("url.AC");
    }

    //***************************COMPONENT OUTPUT*****************************//
    @Override
    public ArrayList<String> getComponentLogList() {

        ArrayList<String> ret = new ArrayList<String>();
        String path = null;

        File configDir = new File(ConfigManager.getFilePath(ConfigManager.COMPONENT_LOGGING_FOLDER));
        File[] folders = configDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                logger.debug(dir.getAbsolutePath().concat("/").concat(name));
                return (new File(dir.getAbsolutePath().concat("/").concat(name))).isDirectory();
            }
        });
        for (File file : folders) {
            ret.add(file.getName());
        }

        return ret;
    }

    @Override
    public ArrayList<String> getLogList(String selectedComponent) {

        ArrayList<String> ret = new ArrayList<String>();

        File dir = new File(ConfigManager.getFilePath(ConfigManager.COMPONENT_LOGGING_FOLDER) + "/" + selectedComponent);
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                File file = new File(dir.getAbsolutePath() + "/" + name);
                return !file.isDirectory();
            }
        });
        for (File file : files) {
            ret.add(file.getName());
        }

        return ret;
    }

    @Override
    public String getLog(String selectedComponent, String file, int lines) {

        String ret = null;

        try {
            ret = readFileTail(ConfigManager.getFilePath(ConfigManager.COMPONENT_LOGGING_FOLDER) + "/" + selectedComponent + "/" + file, lines);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(IPManagerWebServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            ret = "Error while reading file";
        }

        return ret;
    }

    //Component configuration
    @Override
    public ArrayList<String> getComponentConfigurationList() {

        ArrayList<String> ret = new ArrayList<String>();
        String path = null;

        File configDir = new File(ConfigManager.getFilePath(ConfigManager.COMPONENT_CONFIGURATION_FOLDER));
        File[] folders = configDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                logger.debug(dir.getAbsolutePath().concat("/").concat(name));
                return (new File(dir.getAbsolutePath().concat("/").concat(name))).isDirectory();
            }
        });
        for (File file : folders) {
            ret.add(file.getName());
        }
        ret.add("OPTIMIS Global");

        return ret;
    }

    @Override
    public ArrayList<String> getFileList(String selectedComponent) {

        ArrayList<String> ret = new ArrayList<String>();
        File dir = null;
        if (!selectedComponent.equalsIgnoreCase("OPTIMIS Global")) {
            dir = new File(ConfigManager.getFilePath(ConfigManager.COMPONENT_CONFIGURATION_FOLDER) + "/" + selectedComponent);
        } else {
            dir = new File(ConfigManager.getFilePath(ConfigManager.COMPONENT_CONFIGURATION_FOLDER));
        }
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".properties");
            }
        });
        for (File file : files) {
            ret.add(file.getName());
        }

        return ret;
    }

    @Override
    public String getFile(String selectedComponent, String file) {
        String ret = null;

        try {
            if (!selectedComponent.equalsIgnoreCase("OPTIMIS Global")) {
                ret = readFileTail(ConfigManager.getFilePath(ConfigManager.COMPONENT_CONFIGURATION_FOLDER) + "/" + selectedComponent + "/" + file, 0);
            } else {
                ret = readFileTail(ConfigManager.getFilePath(ConfigManager.COMPONENT_CONFIGURATION_FOLDER) + "/" + file, 0);
            }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(IPManagerWebServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            ret = "Error while reading file";
        }

        return ret;
    }

    private String readFileTail(String file, int lines) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        List<String> read = new ArrayList<String>();

        while ((line = reader.readLine()) != null) {
            read.add(line);
        }

        if (lines == 0) {
            read = read.subList(0, read.size());
        } else {
            if(read.size() <= lines) {
                read = read.subList(0, read.size());
            } else {
                read = read.subList(read.size() - lines, read.size());
            }
        }

        String ret = new String();
        for (String lineRead : read) {
            ret = ret.concat(lineRead + "\n");
        }
        return ret;
    }

    //****************************CO INFORMATION*****************************//
    @Override
    public COServiceOperationData getCOServiceOperationData() {

        CloudOptimizerRESTClient co = new CloudOptimizerRESTClient();

        COServiceOperationData resource = new COServiceOperationData();
        for (String serviceId : co.getRunningServices()) {
            resource.addServiceDataResource(new ServiceDataResource(serviceId,
                    "",
                    "",
                    "",
                    "Service TREC"));

            for (String vmId : co.getVMsIdsOfService(serviceId)) {
                resource.addServiceDataResource(new ServiceDataResource("",
                        vmId,
                        co.getVirtualResource(vmId).getType(),
                        co.getVirtualResource(vmId).getComments(),
                        "VM TREC"));
            }
        }
        return resource;
    }

    @Override
    public COInfrastructureOperationData getCOInfrastructureOperationData() {

        CloudOptimizerRESTClient co = new CloudOptimizerRESTClient();

        COInfrastructureOperationData resource = new COInfrastructureOperationData();

        for (String nodeId : co.getNodesId()) {
            resource.addInfrastructureDataResource(new InfrastructureDataResource(nodeId, "", "", "", "Node TREC"));

            for (String vmId : co.getVMsId(nodeId)) {
                resource.addInfrastructureDataResource(new InfrastructureDataResource("", vmId, co.getVirtualResource(vmId).getPublic_ip_address(), co.getVirtualResource(vmId).getPrivate_ip_address(), "VM TREC"));
            }
        }

        return resource;
    }

    //****************************IP Registry*****************************//
    public ArrayList<IP> ipRegistry() {

        ArrayList<IP> ips = new ArrayList<IP>();

        try {
            CBRClient cbrClient = new CBRClient(configIPManagerWeb.getString("cbrClientHost"), configIPManagerWeb.getString("cbrClientPort"));

            List<Provider> ipList = cbrClient.getAllIP().getIPList();
            for (Provider ip : ipList) {
                ips.add(new IP(ip.getName(), 
                                ip.getIpAddress(), 
                                ip.getIdentifier(), 
                                ip.getProviderType(), 
                                ip.getCloudQosUrl(),
                                ip.getDMUrl()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ips;
    }

    //****************************AUTHENTICATION*****************************//
    @Override
    public ArrayList<Object> loginUser(String name, String pass) {
        String res = "0";
        String id;
        ArrayList<Object> returnedArray = new ArrayList<Object>();

        if (accounting.doesUserExist(name)) {
            String storedPasswordHash = accounting.getUserPassword(name);
            boolean valid = BCrypt.checkpw(pass, storedPasswordHash);
            if (valid) {
                res = "Welcome to IP Dashboard, " + name + ".";
                returnedArray.add(0, res);
                id = ((Integer) (int) (Math.random() * 100000000)).toString();
                session_ids.add(id);
                session_users.add(name);
                session_times.add((double) System.currentTimeMillis());

                logger.info("Login successful. Session created; id = " + id + "; name = " + name + ";"
                        + "time = " + session_times.get(session_times.size() - 1)
                        + "; session_ids.size() = " + session_ids.size() + "; session_users.size() = " + session_users.size());

                returnedArray.add(1, id.toString());
            } else {
                res = "User/pass are wrong! Please correct input data or register an account";
                returnedArray.add(0, res);
                logger.info("User/pass are wrong. Reason: password is wrong");
            }
        } else {
            res = "User/pass are wrong! Please correct input data or register an account";
            returnedArray.add(0, res);
            logger.info("User/pass are wrong. Reason: name not found");
        }

        return returnedArray;
    }

    @Override
    public Boolean logoutUser(String sess_id, String name) {
        int index = session_ids.indexOf(sess_id);

        session_ids.remove(index);
        session_users.remove(index);
        session_times.remove(index);

        logger.info("Logout successful. Session deleted; id = " + sess_id + "; name = " + name + "; index = " + index + //"; index2 = " + index2 + ";" +
                "time = " + session_times.get(session_times.size() - 1)
                + " session_ids.size() = " + session_ids.size() + "; session_users.size() = " + session_users.size());

        return true;
    }

    @Override
    public String newAccount(String name, String pass) {

        String res = null;

        if (accounting.doesUserExist(name)) {
            res = "User " + name + " already exists. Please select another user name.";
        } else {
            userKeyUnique = ((Integer) (int) ((Math.random() * 100000000))).toString();
            String hash = BCrypt.hashpw(pass, BCrypt.gensalt());
            accounting.addUser(name, hash);
            res = "New account created for user: " + name + ". Please login with your credentials.";
        }
        logger.info(res);

        return res;
    }
}
