/*
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
package eu.optimis.serviceproviderriskassessmenttool.core.historicaldatabase.dao.populate;

import com.mysql.jdbc.Driver;
import java.sql.*;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;

/**
 * Class for propagating values to the risk user interface in either
 * continuously mode or once depending on the service phase. See main method for
 * example usage and standalone test cases.
 * 
 * @author Django Armstrong
 */
public class RiskPropagator extends Thread {

	protected static final Logger LOGGER = Logger
			.getLogger(RiskPropagator.class);

	// Start of constants
	private static final double RISK_VALUE_MIN = 0.0;

	private static final double RISK_VALUE_MAX = 7.0;

	private static final int THREAD_SLEEP_LONG = 5000;

	private static final int THREAD_SLEEP_SHORT = 1000;

	private static final String MESSAGE_SQL_DEBUG = "RiskPropagator: SQL query is '";

	private static final String MESSAGE_UNKNOWN_GRAPH_TYPE = "RiskPropagator: graphType unknown for given providerType and servicePhase";

	private static final String MESSAGE_CAUGHT_INITIALISATION_EXCEPTION = "RiskPropagator Test: Caught InitialisationException";

	private static final String MESSAGE_INITIALISATION_FAILED = "RiskPropagator: Initialisation failed!";

	/**
	 * Graph type for the risk level of an IP relative to other know IPs given
	 * an associated service's SLA
	 */
	public static final int GRAPHTYPE_SP_DEPLOYMENT_RELATIVE_IP_SLA_RISKLEVEL = 1;
	/**
	 * Graph type for SP normalised risk level received from an IP
	 */
	public static final int GRAPHTYPE_SP_DEPLOYMENT_NORMALISED_SLA_RISKLEVEL = 2;

	/**
	 * Graph type for IP deployment time risk of accepting a given service's SLA
	 */
	public static final int GRAPHTYPE_IP_DEPLOYMENT_SLA_RISKLEVEL = 1;

	/**
	 * Graph type for SP risk level for a service's SLA at operation time
	 */
	public static final int GRAPHTYPE_SP_OPERATION_SLA_RISKLEVEL = 1;

	/**
	 * Graph type for normalised physical host machine failure risk level
	 */
	public static final int GRAPHTYPE_IP_OPERATION_PHYSICAL_HOST_RISKLEVEL = 1;
	/**
	 * Graph type for normalised virtual machine failure risk level
	 */
	public static final int GRAPHTYPE_IP_OPERATION_VIRTUAL_MACHINE_RISKLEVEL = 2;
	/**
	 * Graph type for IP operation time risk level associated with a service's
	 * SLA
	 */
	public static final int GRAPHTYPE_IP_OPERATION_SLA_RISKLEVEL = 3;
	/**
	 * Graph type for IP operation time total risk level
	 */
	public static final int GRAPHTYPE_IP_OPERATION_TOTAL_RISKLEVEL = 4;

	public static final String PROVIDERTYPE_SP = "sp";
	public static final String PROVIDERTYPE_IP = "ip";

	public static final String SERVICEPHASE_DEPLOYMENT = "deployment";
	public static final String SERVICEPHASE_OPERATION = "operation";

	// Start of private member variables
	private Connection conn = null;
	private Statement stmt = null;

	private String url = null;
	private String username = null;
	private String password = null;

	private String providerType = null;
	private String servicePhase = null;

	private int graphType = -1;

	private String sqlTableName = null;
	private String sqlProviderId = null;
	private String sqlServiceId = null;

	private boolean executing = false;

	private double riskValue = -1;

	/**
	 * Custom initialisation exception thrown by constructor of failure to
	 * instantiate.
	 * 
	 * @author Django Armstrong
	 */
	class InitialisationException extends Exception {

		private static final long serialVersionUID = -4728039516295404216L;

		public InitialisationException() {
			super();
		}

		public InitialisationException(String message, Throwable cause) {
			super(message, cause);
		}

		public InitialisationException(String message) {
			super(message);
		}

		public InitialisationException(Throwable cause) {
			super(cause);
		}
	}

	/**
	 * Validate the arguments passed to the class's constructor
	 * 
	 * @param providerType
	 *            the type of provider, valid arguments are "sp", "ip"
	 * @param servicePhase
	 *            the phase in which a service is running, valid arguments are
	 *            "deployment", "operation"
	 * @param providerId
	 *            the human readable providerId or name, e.g. "Atos"
	 * @param serviceId
	 *            the not so human readable serviceId hash value, e.g.
	 *            "76c44bda-4f5a-4f97-806d-011d174bea44"
	 * @param graphType
	 *            the graphType the data will be rendered to in the UI
	 * @return true on success, false on an error
	 */
	private boolean validateConstructorInput(String providerType,
			String servicePhase, String providerId, String serviceId,
			int graphType) {

		if (providerType == null || servicePhase == null || providerId == null
				|| serviceId == null) {
			LOGGER.error("RiskPropagator: Null values provided as argument to constructor");
			return false;
		} else {
			this.providerType = providerType;
			this.servicePhase = servicePhase;
			LOGGER.debug("RiskPropagator: Constuctor called with follow arguments - providerType: "
					+ providerType
					+ " servicePhase: "
					+ servicePhase
					+ " providerId: "
					+ providerId
					+ " serviceId: "
					+ serviceId
					+ " graphType: " + graphType);
			return true;
		}
	}

	/**
	 * Validates graph types for SP deployment
	 * 
	 * @param graphType
	 *            the graphType the data will be rendered to in the UI
	 * @return true on success, false on an error
	 */
	private boolean validateSpDeploymentGraph(int graphType) {
		// Check graph type
		if (graphType == GRAPHTYPE_SP_DEPLOYMENT_RELATIVE_IP_SLA_RISKLEVEL
				|| graphType == GRAPHTYPE_SP_DEPLOYMENT_NORMALISED_SLA_RISKLEVEL) {
			this.graphType = graphType;
			switch (this.graphType) {
			case GRAPHTYPE_SP_DEPLOYMENT_RELATIVE_IP_SLA_RISKLEVEL:
				LOGGER.debug("RiskPropagator: graphType is GRAPHTYPE_SP_DEPLOYMENT_RELATIVE_IP_SLA_RISKLEVEL");
				break;
			case GRAPHTYPE_SP_DEPLOYMENT_NORMALISED_SLA_RISKLEVEL:
				LOGGER.debug("RiskPropagator: graphType is GRAPHTYPE_SP_DEPLOYMENT_NORMALISED_SLA_RISKLEVEL");
				break;
			}
			return true;
		} else {
			LOGGER.error(MESSAGE_UNKNOWN_GRAPH_TYPE);
			return false;
		}
	}

	/**
	 * Validates graph types for SP Operation
	 * 
	 * @param graphType
	 *            the graphType the data will be rendered to in the UI
	 * @return true on success, false on an error
	 */
	private boolean validateSpOperationGraph(int graphType) {
		// Check graph type
		if (graphType == GRAPHTYPE_SP_OPERATION_SLA_RISKLEVEL) {
			this.graphType = graphType;
			LOGGER.debug("RiskPropagator: graphType is GRAPHTYPE_SP_OPERATION_SLA_RISKLEVEL");
			return true;
		} else {
			LOGGER.error(MESSAGE_UNKNOWN_GRAPH_TYPE);
			return false;
		}
	}

	/**
	 * Validates graph types for IP deployment
	 * 
	 * @param graphType
	 *            the graphType the data will be rendered to in the UI
	 * @return true on success, false on an error
	 */
	private boolean validateIpDeploymentGraph(int graphType) {
		// Check graph type
		if (graphType == GRAPHTYPE_IP_DEPLOYMENT_SLA_RISKLEVEL) {
			this.graphType = graphType;
			LOGGER.debug("RiskPropagator: graphType is GRAPHTYPE_IP_DEPLOYMENT_SLA_RISKLEVEL");
			return true;
		} else {
			LOGGER.error(MESSAGE_UNKNOWN_GRAPH_TYPE);
			return false;
		}
	}

	/**
	 * Validates graph types for IP Operation
	 * 
	 * @param graphType
	 *            the graphType the data will be rendered to in the UI
	 * @return true on success, false on an error
	 */
	private boolean validateIpOperationGraph(int graphType) {
		// Check graph type
		if (graphType == GRAPHTYPE_IP_OPERATION_PHYSICAL_HOST_RISKLEVEL
				|| graphType == GRAPHTYPE_IP_OPERATION_SLA_RISKLEVEL
				|| graphType == GRAPHTYPE_IP_OPERATION_TOTAL_RISKLEVEL
				|| graphType == GRAPHTYPE_IP_OPERATION_VIRTUAL_MACHINE_RISKLEVEL) {
			this.graphType = graphType;
			switch (this.graphType) {
			case GRAPHTYPE_IP_OPERATION_PHYSICAL_HOST_RISKLEVEL:
				LOGGER.debug("RiskPropagator: graphType is GRAPHTYPE_IP_OPERATION_PHYSICAL_HOST_RISKLEVEL");
				break;
			case GRAPHTYPE_IP_OPERATION_SLA_RISKLEVEL:
				LOGGER.debug("RiskPropagator: graphType is GRAPHTYPE_IP_OPERATION_TOTAL_RISKLEVEL");
				break;
			case GRAPHTYPE_IP_OPERATION_TOTAL_RISKLEVEL:
				LOGGER.debug("RiskPropagator: graphType is GRAPHTYPE_IP_OPERATION_TOTAL_RISKLEVEL");
				break;
			case GRAPHTYPE_IP_OPERATION_VIRTUAL_MACHINE_RISKLEVEL:
				LOGGER.debug("RiskPropagator: graphType is GRAPHTYPE_IP_OPERATION_VIRTUAL_MACHINE_RISKLEVEL");
				break;
			}
			return true;
		} else {
			LOGGER.error(MESSAGE_UNKNOWN_GRAPH_TYPE);
			return false;
		}
	}

	/**
	 * Selects an appropriate database table given the a set of input parameters
	 * 
	 * @param graphType
	 *            the graphType the data will be rendered to in the UI
	 * @return true on success, false on an error
	 */
	private boolean tableSelect(int graphType) {

		// Table selection and graph type validation
		if (this.providerType.equals("sp")) {
			if (this.servicePhase.equals(SERVICEPHASE_DEPLOYMENT)) {
				// Select Service Provider Deployment table
				sqlTableName = "sp_deployment";
				return validateSpDeploymentGraph(graphType);
			} else if (this.servicePhase.equals(SERVICEPHASE_OPERATION)) {
				// Select Service Provider Operation table
				sqlTableName = "sp_operation";
				return validateSpOperationGraph(graphType);
			} else {
				LOGGER.error("RiskPropagator: servicePhase unknown must be either `deployment' or 'operation'");
				return false;
			}
		} else if (this.providerType.equals("ip")) {
			if (this.servicePhase.equals(SERVICEPHASE_DEPLOYMENT)) {
				// Select Infrastructure Provider Deployment table
				sqlTableName = "ip_deployment";
				return validateIpDeploymentGraph(graphType);
			} else if (this.servicePhase.equals(SERVICEPHASE_OPERATION)) {
				// Select Service Provider Operation table
				sqlTableName = "ip_operation";
				return validateIpOperationGraph(graphType);
			} else {
				LOGGER.error("RiskPropagator: servicePhase unknown must be either `deployment' or 'operation'");
				return false;
			}
		} else {
			LOGGER.error("RiskPropagator: servicePhase unknown must be either `sp' or 'ip'");
			return false;
		}
	}

	/**
	 * Resolve the ID of the provider from its human readable name
	 * 
	 * @param providerId
	 *            the human readable provider ID
	 * @return true on success, false on an error
	 */
	private boolean resolveProviderId(String providerId) {

		ResultSet rs = null;

		String sql = "SELECT id FROM provider_id WHERE name = \""
				+ providerId.toLowerCase() + "\"";
		LOGGER.debug("RiskPropagator: Resolving provider ID");

		try {
			LOGGER.debug(MESSAGE_SQL_DEBUG + sql + "'");
			rs = stmt.executeQuery(sql);
			if (!rs.next()) {
				rs.close();
				// Add new Id it does not exist
				sql = "INSERT INTO `provider_id` VALUES ( NULL, \""
						+ providerId.toLowerCase() + "\" )";
				LOGGER.debug(MESSAGE_SQL_DEBUG + sql + "'");
				stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
				rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					sqlProviderId = Integer.toString(rs.getInt(1));
					LOGGER.debug("RiskPropagator: Inserted new sqlProviderId as: "
							+ sqlProviderId);
				} else {
					LOGGER.error("RiskPropagator: No provider ID returned after inserting");
					return false;
				}
			} else {
				sqlProviderId = rs.getString(1);
				LOGGER.debug("RiskPropagator: sqlProviderId already present value is: "
						+ sqlProviderId);
				rs.close();
			}
		} catch (SQLException e) {
			LOGGER.error("RiskPropagator: Database query failed", e);
			return false;
		} finally {
			LOGGER.debug("RiskPropagator: finally called, closing result set");
			try {
				rs.close();
			} catch (SQLException e) {
				LOGGER.info("RiskPropagator: Caught SQLException in finally, doing nothing");
			}
		}

		return true;
	}

	/**
	 * Resolve the ID of the service from its human readable name
	 * 
	 * @param serviceId
	 *            the human readable service ID
	 * @return true on success, false on an error
	 */
	private boolean resolveServiceId(String serviceId) {

		ResultSet rs = null;

		String sql = "SELECT id FROM service_id WHERE name = \""
				+ serviceId.toLowerCase() + "\"";
		LOGGER.debug("RiskPropagator: Resolving service ID");

		try {
			LOGGER.debug(MESSAGE_SQL_DEBUG + sql + "'");
			rs = stmt.executeQuery(sql);
			if (!rs.next()) {
				rs.close();
				// Add new Id it does not exist
				sql = "INSERT INTO `service_id` VALUES ( NULL, \""
						+ serviceId.toLowerCase() + "\" )";
				LOGGER.debug(MESSAGE_SQL_DEBUG + sql + "'");
				stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
				rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					sqlServiceId = Integer.toString(rs.getInt(1));
					LOGGER.debug("RiskPropagator: Inserted new sqlServiceId as: "
							+ sqlServiceId);
				} else {
					LOGGER.error("RiskPropagator: No service ID returned after inserting");
					return false;
				}
				rs.close();
			} else {
				sqlServiceId = rs.getString(1);
				LOGGER.debug("RiskPropagator: sqlServiceId already present value is: "
						+ sqlServiceId);
				rs.close();
			}
		} catch (SQLException e) {
			LOGGER.error("RiskPropagator: Database query failed", e);
			return false;
		} finally {
			LOGGER.debug("RiskPropagator: finally called, closing result set");
			try {
				rs.close();
			} catch (SQLException e) {
				LOGGER.info("RiskPropagator: Caught SQLException in finally, doing nothing");
			}
		}

		return true;
	}

	/**
	 * Generic initialisation code used in both constructors
	 * 
	 * @param providerType
	 *            the type of provider, valid arguments are "sp", "ip"
	 * @param servicePhase
	 *            the phase in which a service is running, valid arguments are
	 *            "deployment", "operation"
	 * @param providerId
	 *            the human readable providerId or name, e.g. "Atos"
	 * @param serviceId
	 *            the not so human readable serviceId hash value, e.g.
	 *            "76c44bda-4f5a-4f97-806d-011d174bea44"
	 * @param graphType
	 *            the graphType the data will be rendered to in the UI
	 * @return true on success, false on an error
	 */
	private boolean initialise(String providerType, String servicePhase,
			String providerId, String serviceId, int graphType) {

		// Validate input parameters
		if (!validateConstructorInput(providerType, servicePhase, providerId,
				serviceId, graphType)) {
			// There was an error so we return
			return false;
		}

		// Select an appropriate table given the input parameters
		if (!tableSelect(graphType)) {
			// There was an error so we return
			return false;
		}

		// Initialise the connection to the database
		try {
			LOGGER.debug("RiskPropagator: Connecting to database");
			Driver myDriver = new com.mysql.jdbc.Driver();
			DriverManager.registerDriver(myDriver);
			conn = DriverManager.getConnection(url, username, password);
			stmt = conn.createStatement();
			LOGGER.debug("RiskPropagator: Connected to database");
		} catch (SQLException e) {
			LOGGER.error("RiskPropagator: Database connection failed", e);
			cleanUp();
			return false;
		}

		// Resolve the human readable providerId to that stored in the database
		// or add new ID's to the Database if they do not exist
		if (!resolveProviderId(providerId)) {
			// There was an error so we return
			return false;
		}
		if (!resolveServiceId(serviceId)) {
			// There was an error so we return
			return false;
		}

		return true;
	}

	/**
	 * Constructor for initialising hibernate specific connection properties
	 * read from hibernate.cfg.xml in classpath
	 * 
	 * @param providerType
	 *            the type of provider, valid arguments are "sp", "ip"
	 * @param servicePhase
	 *            the phase in which a service is running, valid arguments are
	 *            "deployment", "operation"
	 * @param providerId
	 *            the human readable providerId or name, e.g. "Atos"
	 * @param serviceId
	 *            the not so human readable serviceId hash value, e.g.
	 *            "76c44bda-4f5a-4f97-806d-011d174bea44"
	 * @param graphType
	 *            the graphType the data will be rendered to in the UI
	 * @throws InitialisationException
	 *             thrown on failure to initialise class due to invalid input
	 *             parameters
	 */
	public RiskPropagator(String providerType, String servicePhase,
			String providerId, String serviceId, int graphType)
			throws InitialisationException {

		super("riskPropagatorThread(" + providerType + "-" + servicePhase + "-" + graphType + ")");

		LOGGER.debug("RiskPropagator: Constuctor called for hibernater specific connections properties");

		Configuration cfg = null;

		try {
			// Fetch the hibernate config
			cfg = new Configuration().configure();
		} catch (HibernateException e) {
			LOGGER.error("RiskPropagator: Hibernate configuration missing", e);
			throw new InitialisationException(MESSAGE_INITIALISATION_FAILED, e);
		}

		if (cfg != null) {
			String oldUrl = cfg.getProperty("hibernate.connection.url");
			url = oldUrl.substring(0, oldUrl.lastIndexOf('/')) + "/risk_bridge";
			username = cfg.getProperty("hibernate.connection.username");
			password = cfg.getProperty("hibernate.connection.password");

			LOGGER.debug("RiskPropagator: Url is: " + url);

			// Call generic initialisation code
			if (!initialise(providerType, servicePhase, providerId, serviceId,
					graphType)) {
				throw new InitialisationException(MESSAGE_INITIALISATION_FAILED);
			}
		} else {
			LOGGER.error("RiskPropagator: Hibernate configuration is null");
			throw new InitialisationException(MESSAGE_INITIALISATION_FAILED);
		}
	}

	/**
	 * Constructor for initialising with JDBC specific connection properties
	 * 
	 * @param url
	 *            the url to connect to the database, e.g.
	 *            jdbc:mysql://optimis-database:3306/risk_bridge
	 * @param username
	 *            the username to connect to the database with
	 * @param password
	 *            the password to connect to the database with
	 * @param providerType
	 *            the type of provider, valid arguments are "sp", "ip"
	 * @param servicePhase
	 *            the phase in which a service is running, valid arguments are
	 *            "deployment", "operation"
	 * @param providerId
	 *            the human readable providerId or name, e.g. "Atos"
	 * @param serviceId
	 *            the not so human readable serviceId hash value, e.g.
	 *            "76c44bda-4f5a-4f97-806d-011d174bea44"
	 * @param graphType
	 *            the graphType the data will be rendered to in the UI
	 * @throws InitialisationException
	 *             thrown on failure to initialise class due to invalid input
	 *             parameters
	 */
	public RiskPropagator(String url, String username, String password,
			String providerType, String servicePhase, String providerId,
			String serviceId, int graphType) throws InitialisationException {

		super("riskPropagatorThread(" + providerType + "-" + servicePhase + "-" + graphType + ")");

		LOGGER.debug("RiskPropagator: Constuctor called for JDBC specific connection properties");

		// Validate extra arguments unique to this constructor
		if (url == null || username == null || password == null) {
			LOGGER.error("RiskPropagator: A null value was provided as an argument to the JDBC constructor");
			throw new InitialisationException(MESSAGE_INITIALISATION_FAILED);
		} else {
			this.url = url;
			this.username = username;
			this.password = password;
			LOGGER.debug("RiskPropagator: Url is: " + url);
		}

		// Call generic initialisation code
		if (!initialise(providerType, servicePhase, providerId, serviceId,
				graphType)) {
			throw new InitialisationException(MESSAGE_INITIALISATION_FAILED);
		}
	}

	/**
	 * Adds a riskValue to the database set with setRiskValue
	 */
	private void propagateRiskValue() {

		if (riskValue == -1) {
			LOGGER.error("RiskPropagator: riskValue is not set");
			Thread.currentThread().interrupt();
			return;
		}

		LOGGER.debug("RiskPropagator: propagateRiskValue() called for servicePhase: "
				+ servicePhase);

		final int milliSeconds = 1000;
		Long currentTimeSeconds = System.currentTimeMillis() / milliSeconds;
		LOGGER.debug("RiskPropagator: propagateRiskValue() current unix time is: "
				+ currentTimeSeconds);

		try {
			String sql;
			// Insert to Risk TREC UI Database
			if (servicePhase.equals(SERVICEPHASE_DEPLOYMENT)) {
				sql = "INSERT INTO `" + sqlTableName + "` VALUES ( NULL, "
						+ sqlProviderId + ", " + sqlServiceId + ", "
						+ currentTimeSeconds.toString() + ", " + riskValue
						+ ", " + graphType + ")";
			} else if (servicePhase.equals(SERVICEPHASE_OPERATION)) {
				sql = "INSERT INTO `" + sqlTableName + "` VALUES ( NULL, "
						+ sqlProviderId + ", " + sqlServiceId + ", "
						+ currentTimeSeconds.toString() + ", " + riskValue
						+ ", " + graphType + ")";
			} else {
				LOGGER.error("RiskPropagator: servicePhase is not set");
				Thread.currentThread().interrupt();
				return;
			}

			LOGGER.debug(MESSAGE_SQL_DEBUG + sql + "'");
			stmt.executeUpdate(sql);
			LOGGER.debug("RiskPropagator: propagateRiskValue() executed insert query: \""
					+ sql + "\"");
		} catch (SQLException e) {
			LOGGER.error("RiskPropagator: Database query failed", e);
			Thread.currentThread().interrupt();
			return;
		}
	}

	/*
	 * Continuously updates the database with the riskValue set by
	 * setRiskValue()
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {

		if (!servicePhase.equals(SERVICEPHASE_OPERATION)) {
			LOGGER.error("RiskPropagator: Service phase is not operation, will not continiously update database. Revise you intialisation of this class.");
			return;
		}

		executing = true;
		LOGGER.debug("RiskPropagator: Continuous update mode actived");

		try {
			while (executing) {
				propagateRiskValue();
				Thread.sleep(THREAD_SLEEP_SHORT);
			}
			LOGGER.debug("RiskPropagator: Kill received");
		} catch (InterruptedException e) {
			LOGGER.error("RiskPropagator: Thread interrupted", e);
		}

		// Clean up the database connection what ever happens
		cleanUp();
		
		LOGGER.debug("RiskPropagator: Continious update mode deactivated");
	}

	/**
	 * Closes the database connection
	 */
	private void cleanUp() {

		LOGGER.debug("RiskPropagator: cleanUp() called, closing database connection");

		try {
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			LOGGER.info("RiskPropagator: caught SQLException in cleanUp(), doing nothing");
		}

	}

	/**
	 * Stops the current thread and cleans up database connection
	 * 
	 * @throws InterruptedException
	 *             If thread has been interrupted elsewhere
	 */
	public void kill() throws InterruptedException {
		executing = false;
		LOGGER.debug("RiskPropagator: kill() sent to: " + this.getName() + ", waiting for thread to terminate");
		// Wait for the thread to finish
		this.join();
		LOGGER.debug("RiskPropagator: Terminated thread: " + this.getName());
	}

	/**
	 * Commits the current riskValue to the database, deployment only
	 */
	public void addRiskValue() {
		if (!servicePhase.equals(SERVICEPHASE_DEPLOYMENT)) {
			LOGGER.error("RiskPropagator: Service phase is not deployment, does not make sense to add single value to database in operation phase. Revise you intialisation of this class");
			return;
		}

		LOGGER.debug("RiskPropagator: addRiskValue() called for servicePhase: deployment");

		// Propagate the single risk value to the database
		propagateRiskValue();
		
		// Clean up the database connection
		cleanUp();
	}

	/**
	 * Returns the current riskValue
	 * 
	 * @return the riskValue set
	 */
	public double getRiskValue() {
		LOGGER.debug("RiskPropagator: getRiskValue() called, riskValue is: "
				+ riskValue);
		return riskValue;
	}

	/**
	 * Sets the risk value to be added to the database with addRiskValue()
	 * 
	 * @param the
	 *            riskValue to set must be between RISK_VALUE_MIN (0.0) and
	 *            RISK_VALUE_MAX (7.0)
	 */
	public void setRiskValue(double riskValue) {

		if (!(riskValue >= RISK_VALUE_MIN || riskValue <= RISK_VALUE_MAX)) {
			LOGGER.error("RiskPropagator: riskValue not in range");
			return;
		}

		this.riskValue = riskValue;

		LOGGER.debug("RiskPropagator: setRiskValue() called with riskValue: "
				+ riskValue);
	}

	/**
	 * Provides a mechanism to test whether the RiskPropagator is running.
	 * Useful for testing whether to start the riskPropagator inside inside a
	 * loop rather than duplicating risk value calculating code outside the
	 * loop.
	 * 
	 * @return the executing status of the RiskPropagator instance
	 */
	public boolean isExecuting() {
		return executing;
	}

	/**
	 * Main method test of single update for SP deployment time example
	 * 
	 * @param url
	 *            Database JDBC URL to connect with
	 * @param username
	 *            Database username
	 * @param password
	 *            Database password
	 * @param providerIdOne
	 *            ID of first example provider
	 * @param serviceIdOne
	 *            ID of first example service
	 * @param providerIdTwo
	 *            ID of second example provider
	 * @param serviceIdTwo
	 *            ID of second example service
	 * @throws InitialisationException
	 *             thrown on failure to initialise class due to invalid input
	 *             parameters
	 */
	private static void singleUpdateSpDeployment(String url, String username,
			String password, String providerIdOne, String serviceIdOne,
			String providerIdTwo, String serviceIdTwo)
			throws InitialisationException {

		// The risk value to set in this example
		final double riskValue = 1.0;

		// Test with an EXISTING provider and service ID
		// GRAPHTYPE_SP_DEPLOYMENT_RELATIVE_IP_SLA_RISKLEVEL
		RiskPropagator riskPropagator;
		riskPropagator = new RiskPropagator(
				url,
				username,
				password,
				RiskPropagator.PROVIDERTYPE_SP,
				RiskPropagator.SERVICEPHASE_DEPLOYMENT,
				// Test with an existing provider ID
				providerIdOne,
				// Test with an existing service ID
				serviceIdOne,
				RiskPropagator.GRAPHTYPE_SP_DEPLOYMENT_RELATIVE_IP_SLA_RISKLEVEL);
		// Set the value we want to save
		riskPropagator.setRiskValue(riskValue);
		// Commit the value to the database
		riskPropagator.addRiskValue();

		// Test with an NON-EXISTENT provider and service ID
		// GRAPHTYPE_SP_DEPLOYMENT_RELATIVE_IP_SLA_RISKLEVEL
		riskPropagator = new RiskPropagator(
				url,
				username,
				password,
				RiskPropagator.PROVIDERTYPE_SP,
				RiskPropagator.SERVICEPHASE_DEPLOYMENT,
				// Test with an non-existent provider ID
				providerIdTwo,
				// Test with an non-existent service ID
				serviceIdTwo,
				RiskPropagator.GRAPHTYPE_SP_DEPLOYMENT_RELATIVE_IP_SLA_RISKLEVEL);
		// Set the value we want to save
		riskPropagator.setRiskValue(riskValue);
		// Commit the value to the database
		riskPropagator.addRiskValue();

		// GRAPHTYPE_SP_DEPLOYMENT_NORMALISED_SLA_RISKLEVEL
		riskPropagator = new RiskPropagator(url, username, password,
				RiskPropagator.PROVIDERTYPE_SP,
				RiskPropagator.SERVICEPHASE_DEPLOYMENT, providerIdTwo,
				serviceIdTwo,
				RiskPropagator.GRAPHTYPE_SP_DEPLOYMENT_NORMALISED_SLA_RISKLEVEL);

		riskPropagator.setRiskValue(riskValue);
		riskPropagator.addRiskValue();
	}

	/**
	 * Main method test of single update for IP deployment time example
	 * 
	 * @param url
	 *            Database JDBC URL to connect with
	 * @param username
	 *            Database username
	 * @param password
	 *            Database password
	 * @param providerId
	 *            ID of example provider
	 * @param serviceId
	 *            ID of example service
	 * @throws InitialisationException
	 *             thrown on failure to initialise class due to invalid input
	 *             parameters
	 */
	private static void singleUpdateIpDeployment(String url, String username,
			String password, String providerId, String serviceId)
			throws InitialisationException {

		// The risk value to set in this example
		final double riskValue = 1.0;

		// GRAPHTYPE_IP_DEPLOYMENT_SLA_RISKLEVEL
		RiskPropagator riskPropagator = new RiskPropagator(url, username,
				password, RiskPropagator.PROVIDERTYPE_IP,
				RiskPropagator.SERVICEPHASE_DEPLOYMENT, providerId, serviceId,
				RiskPropagator.GRAPHTYPE_IP_DEPLOYMENT_SLA_RISKLEVEL);
		riskPropagator.setRiskValue(riskValue);
		riskPropagator.addRiskValue();
	}

	/**
	 * Main method test of continuous update for SP operation time example
	 * 
	 * @param url
	 *            Database JDBC URL to connect with
	 * @param username
	 *            Database username
	 * @param password
	 *            Database password
	 * @param providerId
	 *            ID of example provider
	 * @param serviceId
	 *            ID of example service
	 * @throws InitialisationException
	 *             thrown on failure to initialise class due to invalid input
	 *             parameters
	 */
	private static void continuousUpdateSpOperation(String url,
			String username, String password, String providerId,
			String serviceId) throws InitialisationException {

		// The risk values to set in this example
		final double initialRiskValue = 1.0;
		final double newRiskValueOne = 1.1;

		// GRAPHTYPE_SP_OPERATION_SLA_RISKLEVEL
		RiskPropagator riskPropagator = new RiskPropagator(url, username,
				password, RiskPropagator.PROVIDERTYPE_SP,
				RiskPropagator.SERVICEPHASE_OPERATION, providerId, serviceId,
				RiskPropagator.GRAPHTYPE_SP_OPERATION_SLA_RISKLEVEL);
		// Initial value we want to save
		riskPropagator.setRiskValue(initialRiskValue);

		// Check to see riskPropagatorOne is running (i.e this would be used
		// in a while loop to start the riskPropagator instance once only
		// instead of duplicating code outside the while loop to set the
		// first risk value)
		if (!riskPropagator.isExecuting()) {
			// Start up the thread committing the initial value to the
			// database
			riskPropagator.start();
		}

		try {
			// Let the riskPropagator commit this value some more
			Thread.sleep(THREAD_SLEEP_LONG);
			// New value we want to set some time later
			riskPropagator.setRiskValue(newRiskValueOne);
			Thread.sleep(THREAD_SLEEP_LONG);
			// Check to see if the thread is still running before we try to kill
			// it
			if (riskPropagator.isExecuting()) {
				// Stop the thread from committing values to the database
				riskPropagator.kill();
			}
		} catch (InterruptedException e) {
			LOGGER.error("RiskPropagator Test: Thread interrupted", e);
			return;
		}
	}

	/**
	 * Main method test of continuous update for IP operation time example
	 * 
	 * @param url
	 *            Database JDBC URL to connect with
	 * @param username
	 *            Database username
	 * @param password
	 *            Database password
	 * @param providerId
	 *            ID of example provider
	 * @param serviceId
	 *            ID of example service
	 * @throws InitialisationException
	 *             thrown on failure to initialise class due to invalid input
	 *             parameters
	 */
	private static void continuousUpdateIpOperation(String url,
			String username, String password, String providerId,
			String serviceId) throws InitialisationException {

		/**
		 * NOTE: See continuousUpdateSpOperation() for detailed explanation of
		 * code and annotations.
		 */

		// The risk values to set in this example
		final double initialRiskValue = 1.0;
		final double newRiskValueOne = 1.1;
		final double newRiskValueTwo = 1.2;
		final double newRiskValueThree = 1.3;
		final double newRiskValueFour = 1.4;

		// Concurrently add values for:
		// GRAPHTYPE_IP_OPERATION_PHYSICAL_HOST_RISKLEVEL
		// GRAPHTYPE_IP_OPERATION_VIRTUAL_MACHINE_RISKLEVEL
		// GRAPHTYPE_IP_OPERATION_SLA_RISKLEVEL
		// GRAPHTYPE_IP_OPERATION_TOTAL_RISKLEVEL
		RiskPropagator riskPropagatorOne = new RiskPropagator(url, username,
				password, RiskPropagator.PROVIDERTYPE_IP,
				RiskPropagator.SERVICEPHASE_OPERATION, providerId, serviceId,
				RiskPropagator.GRAPHTYPE_IP_OPERATION_PHYSICAL_HOST_RISKLEVEL);

		riskPropagatorOne.setRiskValue(initialRiskValue);
		RiskPropagator riskPropagatorTwo = new RiskPropagator(url, username,
				password, RiskPropagator.PROVIDERTYPE_IP,
				RiskPropagator.SERVICEPHASE_OPERATION, providerId, serviceId,
				RiskPropagator.GRAPHTYPE_IP_OPERATION_VIRTUAL_MACHINE_RISKLEVEL);
		riskPropagatorTwo.setRiskValue(initialRiskValue);
		RiskPropagator riskPropagatorThree = new RiskPropagator(url, username,
				password, RiskPropagator.PROVIDERTYPE_IP,
				RiskPropagator.SERVICEPHASE_OPERATION, providerId, serviceId,
				RiskPropagator.GRAPHTYPE_IP_OPERATION_SLA_RISKLEVEL);
		riskPropagatorThree.setRiskValue(initialRiskValue);
		RiskPropagator riskPropagatorFour = new RiskPropagator(url, username,
				password, RiskPropagator.PROVIDERTYPE_IP,
				RiskPropagator.SERVICEPHASE_OPERATION, providerId, serviceId,
				RiskPropagator.GRAPHTYPE_IP_OPERATION_TOTAL_RISKLEVEL);
		riskPropagatorFour.setRiskValue(initialRiskValue);

		if (!riskPropagatorOne.isExecuting()) {
			riskPropagatorOne.start();
		}
		if (!riskPropagatorTwo.isExecuting()) {
			riskPropagatorTwo.start();
		}
		if (!riskPropagatorThree.isExecuting()) {
			riskPropagatorThree.start();
		}
		if (!riskPropagatorFour.isExecuting()) {
			riskPropagatorFour.start();
		}

		try {
			Thread.sleep(THREAD_SLEEP_LONG);
			riskPropagatorOne.setRiskValue(newRiskValueOne);
			riskPropagatorTwo.setRiskValue(newRiskValueTwo);
			riskPropagatorThree.setRiskValue(newRiskValueThree);
			riskPropagatorFour.setRiskValue(newRiskValueFour);
			Thread.sleep(THREAD_SLEEP_LONG);

			if (riskPropagatorOne.isExecuting()) {
				riskPropagatorOne.kill();
			}
			if (riskPropagatorTwo.isExecuting()) {
				riskPropagatorTwo.kill();
			}
			if (riskPropagatorThree.isExecuting()) {
				riskPropagatorThree.kill();
			}
			if (riskPropagatorFour.isExecuting()) {
				riskPropagatorFour.kill();
			}
		} catch (InterruptedException e) {
			LOGGER.error("RiskPropagator Test: Thread interrupted", e);
			return;
		}
	}

	/**
	 * Main method containing example code for executing this class
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// Setup standalone logging
		Properties props = new Properties();
		props.setProperty("log4j.rootLogger", "DEBUG, A1");
		props.setProperty("log4j.appender.A1",
				"org.apache.log4j.ConsoleAppender");
		props.setProperty("log4j.appender.A1.layout",
				"org.apache.log4j.PatternLayout");
		props.setProperty("log4j.appender.A1.layout.ConversionPattern",
				"%d{ISO8601} [%t] %-5p (%F:%L) - %m%n");
		PropertyConfigurator.configure(props);

		// Optional database connection variables if no "hibernate.cfg.xml" is
		// on the classpath
		String url = "jdbc:mysql://some_hostname:3306/risk_bridge_test";
		String username = "username";
		String password = "password";

		// Example provider and service IDs (second exmaple ID's do not exist in
		// the database)
		String providerIdOne = "Atos";
		String serviceIdOne = "76c44bda-4f5a-4f97-806d-011d174bea44";
		String providerIdTwo = "Test";
		String serviceIdTwo = "00000000-0000-0000-0000-000000000000";

		/**
		 * NOTE: Reference static final variables for valid constructor
		 * arguments
		 */

		try {
			// Single update for deployment time examples
			singleUpdateSpDeployment(url, username, password, providerIdOne,
					serviceIdOne, providerIdTwo, serviceIdTwo);

			singleUpdateIpDeployment(url, username, password, providerIdTwo,
					serviceIdTwo);

			// Continuous update for operation time examples
			continuousUpdateSpOperation(url, username, password, providerIdTwo,
					serviceIdTwo);
			continuousUpdateIpOperation(url, username, password, providerIdTwo,
					serviceIdTwo);
		} catch (InitialisationException e) {
			LOGGER.error(MESSAGE_CAUGHT_INITIALISATION_EXCEPTION, e);
		}
	}
}
