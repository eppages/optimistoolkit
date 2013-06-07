/*
 *  Copyright 2002-2011 Barcelona Supercomputing Center (www.bsc.es)
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
package integratedtoolkit.util;

import integratedtoolkit.ITConstants;
import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class RuntimeConfigManager {
    /*
     * -Dlog4j.configuration=$IT_HOME/log/it-log4j \
     -Dit.project.file=$projFile \
     -Dit.resources.file=$resFile  \
     -Dit.lib=$IT_HOME/integratedtoolkit/lib
     -Dit.lang=java \
     -Dit.appName=$fullAppPath \
     -Dit.to.file=false \
     -Dit.graph=true \
     -Dit.monitor=1000\
     -Dit.tracing=false \
     -Dit.gat.broker.adaptor=sshtrilead,azure \
     -Dit.gat.file.adaptor=sshtrilead,azure" 
     */

    private PropertiesConfiguration config;

    public RuntimeConfigManager(String pathToConfigFile) throws ConfigurationException {
        config = new PropertiesConfiguration(pathToConfigFile);
    }

    public RuntimeConfigManager(File file) throws ConfigurationException {
        config = new PropertiesConfiguration(file);
    }

    public String getProjectFile() {
        return config.getString(ITConstants.IT_PROJ_FILE);
    }

    public void setProjectFile(String location) {
        config.setProperty(ITConstants.IT_PROJ_FILE, location);
    }

    public String getProjectSchema() {
        return config.getString(ITConstants.IT_PROJ_SCHEMA);
    }

    public void setProjectSchema(String location) {
        config.setProperty(ITConstants.IT_PROJ_SCHEMA, location);
    }

    public String getResourcesFile() {
        return config.getString(ITConstants.IT_RES_FILE);
    }

    public void setResourcesFile(String location) {
        config.setProperty(ITConstants.IT_RES_FILE, location);
    }

    public String getResourcesSchema() {
        return config.getString(ITConstants.IT_RES_SCHEMA);
    }

    public void setResourcesSchema(String location) {
        config.setProperty(ITConstants.IT_RES_SCHEMA, location);
    }

    public String getLog4jConfiguration() {
        return config.getString(ITConstants.LOG4J);
    }

    public void setLog4jConfiguration(String location) {
        config.setProperty(ITConstants.LOG4J, location);
    }

    public String getGATBrokerAdaptor() {
        return config.getString(ITConstants.GAT_BROKER_ADAPTOR);
    }

    public void setGATBrokerAdaptor(String adaptor) {
        config.setProperty(ITConstants.GAT_BROKER_ADAPTOR, adaptor);
    }

    public String getGATFileAdaptor() {
        return config.getString(ITConstants.GAT_FILE_ADAPTOR);
    }

    public void setGATFileAdaptor(String adaptor) {
        config.setProperty(ITConstants.GAT_FILE_ADAPTOR, adaptor);
    }

    public void setGraph(boolean graph) {
        config.setProperty(ITConstants.IT_GRAPH, graph);
    }

    public boolean isGraph() {
        return config.getBoolean(ITConstants.IT_GRAPH, false);
    }

    public void setTracing(boolean tracing) {
        config.setProperty(ITConstants.IT_TRACING, tracing);
    }

    public boolean isTracing() {
        return config.getBoolean(ITConstants.IT_TRACING, false);
    }

    public void setMonitorInterval(long seconds) {
        config.setProperty(ITConstants.IT_MONITOR, seconds);
    }

    public long getMonitorInterval() {
        return config.getLong(ITConstants.IT_MONITOR);
    }

    public void save() throws ConfigurationException {
        config.save();
    }

    public String getITLib() {
        return config.getString(ITConstants.IT_LIB);
    }

    public void setITLib(String location) {
        config.setProperty(ITConstants.IT_LIB, location);
    }

    public String getLang() {
        return config.getString(ITConstants.IT_LANG, "java");
    }

    public void setLang(String lang) {
        config.setProperty(ITConstants.IT_LANG, lang);
    }

    public void setToFile(boolean graph) {
        config.setProperty(ITConstants.IT_TO_FILE, graph);
    }

    public boolean isToFile() {
        return config.getBoolean(ITConstants.IT_TO_FILE, false);
    }

    public String getContext() {
        return config.getString(ITConstants.IT_CONTEXT);
    }

    public void setContext(String context) {
        config.setProperty(ITConstants.IT_CONTEXT, context);
    }

    public String getGATAdaptor() {
        return config.getString(ITConstants.GAT_ADAPTOR, System.getenv("GAT_LOCATION") + ITConstants.GAT_ADAPTOR_LOC);
    }

    public void setGATAdaptor(String adaptorPath) {
        config.setProperty(ITConstants.GAT_ADAPTOR, adaptorPath);
    }

    public void setOptimisPeriod(long seconds) {
        config.setProperty(ITConstants.IT_INTERACT_PERIOD, seconds);
    }

    public long getOptimisPeriod() {
        return config.getLong(ITConstants.IT_INTERACT_PERIOD, 20000l);
    }

    
    public String getCertificatesContext() {
        return config.getString(ITConstants.IT_LICENSE_CERTIFICATES, System.getProperty("user.home") + File.separator + "optimis_context");
    }

    public void setCertificatesContext(String certificatesPath) {
        config.setProperty(ITConstants.IT_LICENSE_CERTIFICATES, certificatesPath);
    }
    
	public String getManifestLocation(){
		return config.getString(ITConstants.IT_MANIFEST_LOCATION, "");
	}
	
	public void setManifestLocation(String manifestLocation) {
		config.setProperty(ITConstants.IT_MANIFEST_LOCATION, manifestLocation);
	}
    public static void main(String[] args) {
        try {
            RuntimeConfigManager config = new RuntimeConfigManager("/home/jorgee/it.properties");
            config.setProjectFile("/home/jorgee/project.xml");
            config.setResourcesFile("/home/jorgee/resources.xml");
            config.setGraph(true);
            config.setTracing(false);
            config.setLog4jConfiguration("/home/jorgee/log4j.properties");
            config.setGATBrokerAdaptor("sshtrilled");
            config.setGATFileAdaptor("sshtrilled");

            config.save();

            config = new RuntimeConfigManager("/home/jorgee/it.properties");
            System.out.println(ITConstants.IT_PROJ_FILE + "=" + config.getProjectFile());
            System.out.println(ITConstants.IT_RES_FILE + "=" + config.getResourcesFile());
            System.out.println(ITConstants.LOG4J + "=" + config.getLog4jConfiguration());
            System.out.println(ITConstants.GAT_BROKER_ADAPTOR + config.getGATBrokerAdaptor());
            System.out.println(ITConstants.GAT_FILE_ADAPTOR + "=" + config.getGATFileAdaptor());
            System.out.println(ITConstants.IT_GRAPH + "=" + config.isGraph());
            System.out.println(ITConstants.IT_TRACING + "=" + config.isTracing());
            System.out.println(ITConstants.IT_TO_FILE + "=" + config.isToFile());
            System.out.println(ITConstants.IT_LIB + "=" + config.getITLib());
            System.out.println(ITConstants.IT_LANG + "=" + config.getLang());
            System.out.println(ITConstants.IT_MONITOR + "=" + config.getMonitorInterval());
            System.out.println(ITConstants.IT_INTERACT_PERIOD + "=" + config.getOptimisPeriod());
            System.out.println(ITConstants.GAT_ADAPTOR + "=" + config.getGATAdaptor());


        } catch (ConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getSchedulerComponent() {
        return config.getString(ITConstants.IT_SCHEDULER_COMPONENT, null);
    }

    public void setSchedulerComponent(String componentName) {
        config.setProperty(ITConstants.IT_SCHEDULER_COMPONENT, componentName);
    }
    
    public String getComponent() {
        return config.getString(ITConstants.IT_COMPONENT, null);
    }

    public void setComponent(String componentName) {
        config.setProperty(ITConstants.IT_COMPONENT, componentName);
    }    
}
