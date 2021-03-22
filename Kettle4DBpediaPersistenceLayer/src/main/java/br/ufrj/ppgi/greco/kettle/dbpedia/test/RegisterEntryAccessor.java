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