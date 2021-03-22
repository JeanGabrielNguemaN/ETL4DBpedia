/*
* %W% %E% Jean Gabriel Nguema Ngomo
*
* Copyright 2021 Jean Gabriel Nguema Ngomo
*
*Licensed under the Apache License, Version 2.0 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
*/
package br.ufrj.ppgi.greco.kettle.dbpedia.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//singleton
public final class DatabaseConnectionFactory {
	
	static{
		 try {
	            // The newInstance() call is a work around for some
	            // broken Java implementations

	          //  Class.forName("com.mysql.jdbc.Driver").newInstance();
	        } catch (Exception ex) {
	            // handle the error
	       }
		 
		 
	}
	static private Connection connection = null;
	
	public static synchronized Connection getConnection(){
		
		//a CONEX�O � NULA?
		if (connection!=null){
			
			return connection;
		}
			
	     try {
	    	 
	    	 		// Este é um dos meios para registrar um driver 
	          		Class.forName("com.mysql.jdbc.Driver");
	          		
				    String database = "dbpediaexpresstest";
				    String user="dbpedia";
				    String password="dbpedia";
					String connectionURL = "jdbc:mysql://localhost/"+database+"?" 
											+"user="+user
											+"&password="+ password
											+"&serverTimezone=UTC"
											+"&useUnicode=yes"
											+"&characterEncoding=UTF-8";
					connection =
				    DriverManager.getConnection(connectionURL);
					
	     		 } catch (ClassNotFoundException ex) {
	     			 	// handle any errors
	     			 	System.out.println("SQLException: " + ex.getMessage());
			
				  } catch (SQLException ex) {
				    // handle any errors
				    System.out.println("SQLException: " + ex.getMessage());
				    System.out.println("SQLState: " + ex.getSQLState());
				    System.out.println("VendorError: " + ex.getErrorCode());
				} 
	
				
		
		return connection;
	}
}
