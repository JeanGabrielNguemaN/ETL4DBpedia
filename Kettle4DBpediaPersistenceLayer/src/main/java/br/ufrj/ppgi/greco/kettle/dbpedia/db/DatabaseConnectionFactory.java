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
