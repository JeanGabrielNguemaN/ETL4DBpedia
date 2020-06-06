package br.ufrj.ppgi.greco.kettle.dbpedia.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import br.ufrj.ppgi.greco.kettle.dbpedia.db.DatabaseConnectionFactory;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.OntologyClass;


import java.sql.PreparedStatement ;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OntologyClassDAO {
	
	private Connection connection;

	public OntologyClassDAO(){
		
		this.connection=DatabaseConnectionFactory.getConnection();
		
	}

    
    /**
     * Insersão pelo nome do template
     * @param templateName
     */
    public int insert(String ontologyclass){
		
		String sql = "insert into ontologyclass " +
            "(ontologyclass)" +
            " values (?)";
		
		try {
			// prepared statement para inserção
			Connection con = DatabaseConnectionFactory.getConnection();
    	
			PreparedStatement stmt = con.prepareStatement(sql);

			//Confifigurar os valores
			stmt.setString(1,ontologyclass);
	
			stmt.execute();

			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return getId(ontologyclass);
	}
    
    /**
     * Obter id do template property
     * @param ontologyClass
     * @return
     */
    public int getId(String ontologyClass){
		
    	int id=0;
		String sql = "select id from ontologyclass where ontologyclass=\"" +ontologyClass+"\"";
		
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
     * @param ontologyclass
     * @return
     */
	public int retrieveOntClassId(String ontologyClass) {

		int id= getId(ontologyClass);
		
		if(id!=0){
			return id;
		}

		return insert(ontologyClass);
	}


	public OntologyClass getOntologyClass(int idontologyclass) {
		List<OntologyClass> ontologyClasses=null;
		
		try {
			 
            
            PreparedStatement stmt = this.connection.
                    prepareStatement("select * from ontologyclass where id="+idontologyclass);
            ResultSet rs = stmt.executeQuery();
            
            if (rs!=null){
           	 ontologyClasses= new ArrayList<OntologyClass>();
            }
           
            while (rs.next()) {
                // criando o objeto OntologyClass
           	 	String ontclass=rs.getString("ontologyclass");
          
           	 	OntologyClass ontologyclass = new OntologyClass(idontologyclass, ontclass);

           	 	// adicionando o objeto à lista
           	 	ontologyClasses.add(ontologyclass);
                
                break;
            }
            rs.close();
            stmt.close();
           
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
		
		return ontologyClasses.get(0);
	}
	

} 
