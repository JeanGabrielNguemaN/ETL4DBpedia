package br.ufrj.ppgi.greco.kettle.dbpedia.test;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.mysql.jdbc.Statement;

import br.ufrj.ppgi.greco.kettle.dbpedia.db.DatabaseConnectionFactory;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Article;

public class RegisterEntryAccessor {

    
	 public static void main(String[] args) {
    	
    	
    	Connection connection = DatabaseConnectionFactory.getConnection();
    	
    	String sql = "SELECT * FROM publicationlog ";
    	PreparedStatement stmt;
		try {
			stmt = connection.prepareStatement(sql);
			
			ResultSet rs = stmt.executeQuery();

			int i=0;
	    	while (rs.next()) {
	    		
	    		String message= rs.getString(3);
	    		
	    		i++;
	    		System.out.println("MESSAGE "+i
	    				+ ": "+ message);
	    		
	    		if(i==1) {
	    			break;
	    		}
		     
		     } 
	    	
	    	
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    
		
	}

    
    
 }