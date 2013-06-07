/*
 Copyright (C) 2012-2013 Umeå University

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package eu.optimis.sd.stubs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;


import eu.optimis.sd.util.SDConfigurationKeys;
import eu.optimis.sd.util.config.Configuration;
import eu.optimis.sd.util.config.ConfigurationFactory;

public class SpTrecDBStub
{
	protected final static Logger logger = Logger.getLogger(SpTrecDBStub.class);
	
	private String dbUrl;
	private String dbUserName;
	private String dbPassword;
	
	private static SpTrecDBStub instance;
	
	private SpTrecDBStub(String dbUrl, String dbUserName, String dbPassword)
	{
		super();
		this.dbUrl = dbUrl;
		this.dbUserName = dbUserName;
		this.dbPassword = dbPassword;
	}
	
	public static SpTrecDBStub getInstance(String configurationFile) throws Exception
	{
		if (instance == null)
		{
			Configuration config = ConfigurationFactory.getConfig(configurationFile);
			String dbUrl = config.getString(SDConfigurationKeys.SP_TREC_DB_URL);
			String dbUserName= config.getString(SDConfigurationKeys.SP_TREC_DB_USERNAME);
			String dbPassword = config.getString(SDConfigurationKeys.SP_TREC_DB_PASSWORD);
			logger.debug("Building SpTrecDBStub: dbUrl=" + dbUrl
					+ ", dbUserName=" + dbUserName + ", dbPassword="
					+ dbPassword);
			instance = new SpTrecDBStub(dbUrl, dbUserName, dbPassword);
		}
		return instance;
	}
	
	public  boolean  updateSpTrecDB(String serviceId, String manifestXML, boolean is_brokerage, String brokerHost, int brokerPort)
	{
		logger.debug("Updating SP TRECDB using serviceId=" + serviceId
				+ ", brokerHost=" + brokerHost+ ", brokerPort=" + brokerPort);
		
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(this.dbUrl, this.dbUserName, this.dbPassword);
			
			String delete ="delete from manifest_raw where service_id = ?;";
			PreparedStatement pstmt = conn.prepareStatement(delete);
			pstmt.setString(1, serviceId);
			pstmt.executeUpdate();
			logger.debug("Old record (if exists) with service id = "+ serviceId+ " deleted.");
			
			String insert = "insert into manifest_raw(service_id, service_manifest, is_broker, broker_host, broker_port) values(?, ?, ?, ?, ?)";
			pstmt = conn.prepareStatement(insert);
			pstmt.setString(1, serviceId);
			pstmt.setString(2, manifestXML);
			if(is_brokerage)
				pstmt.setInt(3, 1);
			else
				pstmt.setInt(3, 0);
			pstmt.setString(4, brokerHost);
			pstmt.setInt(5, brokerPort);
			pstmt.executeUpdate();
			logger.debug("New record insterted.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.debug("Update SP TRECDB FAILED.");
			return false;
		}
		return true;
	}
	
	public static void main(String args[])
	{
		try
		{
			
			String serviceId = "ServiceId***";
			String manifestXML = "<xml></xml>";
			String brokerHost = "159.1002.32.35";
			int brokerPort = 8090;
			
			Class.forName("com.mysql.jdbc.Driver");
			String dbUrl = "jdbc:mysql://optimis-database.atosorigin.es:3306/sptrecdb";
			String dbUserName = "trecdb_usr";
			String dbPassword= "L84VA8cStVf5bV7q";
			Connection conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
			
			String delete ="delete from manifest_raw where service_id = ?;";
			PreparedStatement pstmt = conn.prepareStatement(delete);
			pstmt.setString(1, serviceId);
			pstmt.executeUpdate();
			logger.debug("Old record (if exists) with service id = "+ serviceId+ " deleted.");
			
			String insert = "insert into manifest_raw(service_id, service_manifest, broker_host, broker_port) values(?, ?, ?, ?)";
			pstmt = conn.prepareStatement(insert);
			pstmt.setString(1, serviceId);
			pstmt.setString(2, manifestXML);
			pstmt.setString(3, brokerHost);
			pstmt.setInt(4, brokerPort);
			pstmt.executeUpdate();
			logger.debug("New record insterted.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
