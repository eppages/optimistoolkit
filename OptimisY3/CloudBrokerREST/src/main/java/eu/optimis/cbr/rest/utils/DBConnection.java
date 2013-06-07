package eu.optimis.cbr.rest.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
	private String dbClass;
	private Connection con;
	
	public DBConnection(String dburl, String user, String password){
		this.dbClass =  "com.mysql.jdbc.Driver";
		try {
			Class.forName(this.dbClass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.con = DriverManager.getConnection(dburl, user, password);
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	ResultSet DBExecuteQuery(String query){
		Statement stmt = null;
		try {
			stmt = this.con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rs;
	}
	
	public boolean updateSPTRECdb(String serviceId, String manifestString, boolean is_brokerage, String brokerHost, int brokerPort){
		String delete ="delete from manifest_raw where service_id = ?;";
        PreparedStatement pstmt;
		try {
			pstmt = con.prepareStatement(delete);
			pstmt.setString(1, serviceId);

        pstmt.executeUpdate();
        //logger.debug("Old record (if exists) with service id = "+ serviceId+ " deleted.");
         
         String insert = "insert into manifest_raw(service_id, service_manifest, is_broker, broker_host, broker_port) values(?, ?, ?, ?, ?)";
         pstmt = con.prepareStatement(insert);
         pstmt.setString(1, serviceId);
         pstmt.setString(2, manifestString);
         if(is_brokerage)
                 pstmt.setInt(3, 1);
         else
                 pstmt.setInt(3, 0);
         pstmt.setString(4, brokerHost);
         pstmt.setInt(5, brokerPort);
         pstmt.executeUpdate();
         //logger.debug("New record insterted.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}
	
}
