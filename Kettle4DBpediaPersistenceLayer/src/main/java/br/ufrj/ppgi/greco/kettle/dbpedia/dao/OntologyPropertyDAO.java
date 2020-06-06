package br.ufrj.ppgi.greco.kettle.dbpedia.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import br.ufrj.ppgi.greco.kettle.dbpedia.db.DatabaseConnectionFactory;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.OntologyProperty;

import java.sql.PreparedStatement ;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OntologyPropertyDAO {
	
	private Connection connection;

	public OntologyPropertyDAO(){
		
		this.connection=DatabaseConnectionFactory.getConnection();
		
	}

    
    /**
     * Insersão pelo nome do template
     * @param templateName
     */
    public int insert(String ontologyproperty){
		
		String sql = "insert into ontologyproperty " +
            "(ontologyproperty)" +
            " values (?)";
		
		try {
			// prepared statement para inserção
			Connection con = DatabaseConnectionFactory.getConnection();
    	
			PreparedStatement stmt = con.prepareStatement(sql);

			//Confifigurar os valores
			stmt.setString(1,ontologyproperty);
	
			stmt.execute();

			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return getPropertyId(ontologyproperty);
	}
    
    /**
     * Obter id do template property
     * @param ontologyproperty
     * @return
     */
    public int getPropertyId(String ontologyproperty){
		
    	int id=0;
		String sql = "select id from ontologyproperty where ontologyproperty=\"" +ontologyproperty+"\"";
		
		try {
			// prepared statement para inserção
			Connection con = DatabaseConnectionFactory.getConnection();
    	
			PreparedStatement stmt = con.prepareStatement(sql);

			ResultSet rs = stmt.executeQuery();
             
            if(rs.next()) {
                 // criando o objeto TemplateMapping
   
            	 id=rs.getInt("id");

             }

			stmt.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return id;
	}
    /**
     * Recupera o id ou insere
     * @param templateId
     * @param ontologyproperty
     * @return
     */
	public int retrievePropertyId(int templateId, String ontologyproperty) {

		int id= getPropertyId(ontologyproperty);
		
		if(id!=0){
			return id;
		}
		
		return insert(ontologyproperty);
	}


	public OntologyProperty getOntologyProperty(int idontologyproperty) {
		List<OntologyProperty> ontologyPropertys=null;
		
		try {
			 
            
            PreparedStatement stmt = this.connection.
                    prepareStatement("select * from ontologyproperty where id="+idontologyproperty);
            ResultSet rs = stmt.executeQuery();
            
            if (rs!=null){
           	 ontologyPropertys= new ArrayList<OntologyProperty>();
            }
    
            while (rs.next()) {   
            
            	// criando o objeto OntologyProperty
            	String ontologyproperty=rs.getString("ontologyproperty");
            	OntologyProperty OntologyProperty = new OntologyProperty(idontologyproperty, ontologyproperty);

            	// adicionando o objeto à lista
            	ontologyPropertys.add(OntologyProperty);
                
                break;
            }
            rs.close();
            stmt.close();
           
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
		
		return ontologyPropertys.get(0);
	}
	

} 
