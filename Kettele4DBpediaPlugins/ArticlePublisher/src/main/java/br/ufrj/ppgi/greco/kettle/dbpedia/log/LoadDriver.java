package br.ufrj.ppgi.greco.kettle.dbpedia.log;


import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mysql.jdbc.Statement;

// Notice, do not import com.mysql.cj.jdbc.*
// or you will have problems!

public class LoadDriver {

    public static void main(String[] args) {
    		
    	try {
    		
    	    String content="Article content";
    		int idarticle= insertArticle(content);
    		
    	    //String infobox="infobox";
			//insertInfobox(idarticle, infobox);
			
			String message="Message ";
			String endpoint="teste";	
			Timestamp 	entrydatetime = new Timestamp(System.currentTimeMillis());
 				        
			registerOperation(idarticle,message, endpoint,entrydatetime);
						
    		
    						
    		} catch (SQLException ex) {
    				    // handle any errors
    				    System.out.println("SQLException: " + ex.getMessage());
    				    System.out.println("SQLState: " + ex.getSQLState());
    				    System.out.println("VendorError: " + ex.getErrorCode());
    		} 
    	
    				
    		
    }
    
    private static void registerOperation(int idarticle, String message, String endpoint, Timestamp entrydatetime) throws SQLException {
    	
    	
    	connection= getConnection();
    	
    	String sql = "insert into publicationlog " +
				 "(idarticle, message, endpoint ,entrydate )" +
				 " values (?,?,?,?)";
    	
    	PreparedStatement stmt = connection.prepareStatement(sql);

			//Confifigurar os valores

		stmt.setInt(1, idarticle);
		stmt.setString(2,"Message 1");
		stmt.setString(3,"endpoint 1");
		stmt.setTimestamp(4,entrydatetime);
		
		stmt.execute();

		stmt.close();
		
	}

    
   private static void insertInfobox(int idarticle, String infobox) throws SQLException {
	   
	   connection= getConnection();
	   
	   String sql = "insert into infobox " +
				 "(idarticle, infobox )" +
				 " values (?,?)";
	   
	   PreparedStatement stmt = connection.prepareStatement(sql);

		stmt.setInt(1, idarticle);
		
		stmt.setString(2,"Infobox 1");
		
		stmt.execute();

		stmt.close();
		
	}

	private static int insertArticle(String content) throws SQLException {

		int id=-1;
		connection= getConnection();
	
		String sql = "insert into article " +
				 "(content)" +
				 " values (?)";
		
		 java.sql.Statement stmt = connection.createStatement();
		
		//
	    // Insert one row that will generate an AUTO INCREMENT
	    // key in the 'priKey' field
	    //

	    stmt.executeUpdate(
	            "INSERT INTO article (content) "
	            + "values ('"+ content + "')",
	            Statement.RETURN_GENERATED_KEYS);

	   

	    ResultSet rs = stmt.getGeneratedKeys();

	    if (rs.next()) {
	    	id = rs.getInt(1);
	    } else {
	    	
	    	  throw new RuntimeException("NAO FOI POSSIVEL OBTER O ID");
	    }

	     
	    System.out.println("Key returned from getGeneratedKeys():"
	        + id);
	
		stmt.close();
		return id;
	}

    
   static private Connection connection = null;
	
	public static synchronized Connection getConnection(){
		
		//a CONEX�O � NULA?
		if (connection!=null){
			
			return connection;
		}
			
	     try {
	    	    Class.forName("com.mysql.jdbc.Driver");
			    String database = "testlog";
			    String user="dbpedia";
			    String password="dbpedia";
				String connectionURL = "jdbc:mysql://localhost/"+ database+"?" 
										+"user="+user
										+"&password="+ password
										+ "&serverTimezone=UTC"
										+"&useUnicode=yes"
									
										+ "&characterEncoding=UTF-8";
				connection =DriverManager.getConnection(connectionURL);
				
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