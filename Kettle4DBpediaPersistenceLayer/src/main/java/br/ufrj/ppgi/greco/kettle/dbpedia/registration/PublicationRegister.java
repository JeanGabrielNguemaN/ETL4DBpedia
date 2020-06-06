package br.ufrj.ppgi.greco.kettle.dbpedia.registration;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.mysql.jdbc.Statement;

import br.ufrj.ppgi.greco.kettle.dbpedia.db.DatabaseConnectionFactory;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Article;

public class PublicationRegister {

    /**
     * Notificar a criação do artigo    
     * @param article
     * @param message
     * @param endpoint
     * @param entrydatetime
     */

	public void notifyCreation(String articleContent, String pageid, String summary, String message
			  , String endpoint, String botaccount, Timestamp 	entrydatetime) {
		try {
    		
    		int idarticle= insertArticle(articleContent, pageid);
    		
    		
			saveEntry(idarticle,summary, message, endpoint,botaccount, entrydatetime);
						
   
    		} catch (SQLException ex) {
    				    
    			 System.out.println("SQLException: " + ex.getMessage());
    			 System.out.println("SQLState:     " + ex.getSQLState());
    			 System.out.println("VendorError:   " + ex.getErrorCode());
    			
    		}
	}
	
    /**
     * Registrar a publicação
     * @param idarticle
     * @param message
     * @param endpoint
     * @param entrydatetime
     * @throws SQLException
     */
    private void saveEntry(int idarticle, String summary, String message, String endpoint, String botaccount, Timestamp entrydatetime) throws SQLException {
    	
    	
    	Connection connection = DatabaseConnectionFactory.getConnection();
    	
    	String sql = "insert into publicationlog " +
				 "(idarticle, message, endpoint ,entrydate, summary, botaccount )" +
				 " values (?,?,?,?,?,?)";
    	
    	PreparedStatement stmt = connection.prepareStatement(sql);

		stmt.setInt(1, idarticle);
		stmt.setString(2,message);
		stmt.setString(3,endpoint);
		stmt.setTimestamp(4,entrydatetime);
		stmt.setString(5,summary);
		stmt.setString(6,botaccount);
		
		stmt.execute();

		stmt.close();
		
	}

    /**
     * Armazenar artigo publicado
     * @param content
     * @return
     * @throws SQLException
     */
	private int insertArticle(String content, String pageid) throws SQLException {

		int id=-1;
		Connection connection = DatabaseConnectionFactory.getConnection();
	
		 java.sql.Statement stmt = connection.createStatement();
		
		//
	    // Inserir um registro que irá gerar um AUTO INCREMENT
	    // Na chave primária
	    //

	    stmt.executeUpdate(
	            "INSERT INTO article (content,pageid) "
	            + "values ('"+ content + "'"
	            		+ ","
	            		+ "'"+pageid
	            		+ "')",
	            Statement.RETURN_GENERATED_KEYS);

	  
	    ResultSet rs = stmt.getGeneratedKeys();

	    if (rs.next()) {
	    	id = rs.getInt(1);
	    } else {
	    	
	    	  throw new RuntimeException("NAO FOI POSSIVEL OBTER O ID");
	    }

	    System.out.println("Chave primária :" + id);
	
		stmt.close();
		
		return id;
	}

    
 }