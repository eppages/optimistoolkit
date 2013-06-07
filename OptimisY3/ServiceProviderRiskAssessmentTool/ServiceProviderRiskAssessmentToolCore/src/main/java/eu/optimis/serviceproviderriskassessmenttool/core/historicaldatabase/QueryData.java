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
package eu.optimis.serviceproviderriskassessmenttool.core.historicaldatabase;

import java.sql.*;

	/**
	 * Example of how to query a database using JDBC.
	 *
	 * <p>The program demonstrates</p>
	 * <ul>
	 *   <li>Use of properties to hold JDBC driver and database details</li>
	 *   <li>Use of the SQL command SELECT</li>
	 *   <li>Processing of ResultSet objects</li>
	 * </ul>
	 *
	 * @author Karim Djemame
	 * @author James Padgett
	 * @version 1.0 [2006-10-20]
	 * <br> 2.0 [2007-08-01] </br>
	 */


public class QueryData {

      public static HistoricalDatabase getData_OSNAME(String providername)// throws SQLException
        {
          HistoricalDatabase results = new HistoricalDatabase();
           /* if (database != null) {
		          try {
		            database.close();
		          }
		          catch (Exception error) {}
		        }*/
          return results;
      }

	  public static HistoricalDatabase getData_OSNAME(String providername, String osname, Connection database)
		   throws SQLException
		  {
		    Statement statement = database.createStatement(); 
		    HistoricalDatabase results = new HistoricalDatabase();
		    int i=0;
		    double[] risk;

		    /* 
		    // Takes providername and queries dB for matching provider_id
		    try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.print("\n");
			System.out.print("\n");
			System.err.print(new Timestamp(new Date().getTime()));
			System.err.print("\n");
			System.out.print("INFO: Fetching Historical Data");

		    */


			String sql1 = "SELECT provider_id FROM providers WHERE provider_dn = '" + providername + "'";
		    ResultSet query1 = statement.executeQuery(sql1);
		    while (query1.next()) {
			results.setProvider_id(query1.getInt("provider_id"));
		    }

		    String sql2 = "SELECT d.sla_id, d.provider_id, d.risk, d.state FROM resourcesTerms a LEFT JOIN serviceDescriptionTerms b ON a.resourceTermID = b.resourceTermID " +
		    "LEFT JOIN mapSLAandSDT c ON b.sdTermID = c.sdTermID LEFT JOIN slaOffers d ON c.slaID = d.sla_id " +
		    "WHERE a.operatingSystemType = '" + osname + "' AND d.provider_id  = '" + results.getProvider_id() + "' ORDER BY d.sla_id";

		    String sql3 = "SELECT COUNT(d.sla_id) FROM resourcesTerms a LEFT JOIN serviceDescriptionTerms b ON a.resourceTermID = b.resourceTermID " +
		    "LEFT JOIN mapSLAandSDT c ON b.sdTermID = c.sdTermID LEFT JOIN slaOffers d ON c.slaID = d.sla_id " +
		    "WHERE a.operatingSystemType = '" + osname + "' AND d.provider_id  = '" + results.getProvider_id() + "' ORDER BY d.sla_id";

		    String sql4 = "SELECT COUNT(d.sla_id) FROM resourcesTerms a LEFT JOIN serviceDescriptionTerms b ON a.resourceTermID = b.resourceTermID " +
		    "LEFT JOIN mapSLAandSDT c ON b.sdTermID = c.sdTermID LEFT JOIN slaOffers d ON c.slaID = d.sla_id " +
		    "WHERE a.operatingSystemType = '" + osname + "' AND d.provider_id  = '" + results.getProvider_id() + "' AND d.state = '1' ORDER BY d.sla_id";

		    // Queries no of slas which specified a operating system type == LINUX from provider with provider_id 

			ResultSet query2 = statement.executeQuery(sql3);

			while (query2.next()) {
			results.setNo_slas(query2.getInt("COUNT(d.sla_id)"));
		    }

		    //  Queries no of FAILED slas from provider with provider_id

			ResultSet query3 = statement.executeQuery(sql4);
		    while (query3.next()) {
			results.setNo_failed_slas(query3.getInt("COUNT(d.sla_id)"));
			}

		    //  For each sla from provider with provider_id returns an array(double) of risk values
  

			ResultSet query4 = statement.executeQuery(sql2);

			risk = new double[results.getNo_slas()];
			while (query4.next()) {
				risk[i] = query4.getDouble("risk");
				i++;
		    }

			results.setRisk(risk);
		    statement.close();

		        if (database != null) {
		          try {
		            database.close();
		          }
		          catch (Exception error) {}
		        }
		        database.close();

		    return results;
		  }
		

	  /**
	   * Queries the database to find student names.
	   *
	   * @param forename forename to search for in database
	   * @param database connection to database
	   * @throws SQLException if query fails
	   **/

		public static HistoricalDatabase getData(String providername, Connection database)
			throws SQLException {

		    Statement statement = database.createStatement(); 
		    HistoricalDatabase results = new HistoricalDatabase();
		    int i=0;
		    double[] risk;
		    /*
		    // Takes providername and queries dB for matching provider_id
		    try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}

			System.out.print("\n");
			System.out.print("\n");
			System.err.print(new Timestamp(new Date().getTime()));
			System.err.print("\n");
			System.out.print("INFO: Fetching Historical Data");
		    */

		    ResultSet query1 = statement.executeQuery(
		    		"SELECT provider_id FROM providers WHERE provider_dn = '" + providername + "'");
		    while (query1.next()) {
			results.setProvider_id(query1.getInt("provider_id"));
		    }

		    // Queries no of slas from provider with provider_id
			ResultSet query2 = statement.executeQuery(
					"SELECT COUNT(sla_id) FROM slaOffers WHERE provider_id = '" + results.getProvider_id() + "'");
		    while (query2.next()) {
			results.setNo_slas(query2.getInt("COUNT(sla_id)"));
		    }

		    //  Queries no of FAILED slas from provider with provider_id

			ResultSet query3 = statement.executeQuery(
				     "SELECT COUNT(sla_id) FROM slaOffers WHERE provider_id = '" + results.getProvider_id() + "' AND state=1");
		    while (query3.next()) {
			results.setNo_failed_slas(query3.getInt("COUNT(sla_id)"));
			}

		    //  For each sla from provider with provider_id returns an array(double) of risk values

			ResultSet query4 = statement.executeQuery(
				     "SELECT risk FROM slaOffers WHERE provider_id = '" + results.getProvider_id() + "'");
			risk = new double[results.getNo_slas()];
			while (query4.next()) {
				risk[i] = query4.getDouble("risk");
				i++;
		    }
			results.setRisk(risk);
		    statement.close();		    

		        if (database != null) {
		          try {
		            database.close();
		          }
		          catch (Exception error) {}
		        }
		    database.close();
		    return results;
		  }

	  /**
	   * Main program. Only used for testing
	   **/
	  public static void main(String[] argv)
	  {
	    if (argv.length == 0) {
	      System.err.println("usage: java QueryDB <providername>");
	      System.exit(1);
	    }
	    }
	  }
	





