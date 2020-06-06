package br.ufrj.ppgi.greco.kettle.dbpedia.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import br.ufrj.ppgi.greco.kettle.dbpedia.db.DatabaseConnectionFactory;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.TemplateProperty;

import java.sql.PreparedStatement ;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TemplatePropertyDAO {
	
	private Connection connection;

	public TemplatePropertyDAO(){
		
		this.connection=DatabaseConnectionFactory.getConnection();
		
	}

    
    /**
     * Insersão pelo nome do template
     * @param templateName
     */
    public int insert(int templateid, String templateproperty){
		
		String sql = "insert into templateproperty " +
            "(idtemplate, templateproperty)" +
            " values (?,?)";
		
		try {
			// prepared statement para inserção
			Connection con = DatabaseConnectionFactory.getConnection();
    	
			PreparedStatement stmt = con.prepareStatement(sql);

			//Confifigurar os valores

			stmt.setInt(1,templateid);
			stmt.setString(2,templateproperty);
	
			stmt.execute();

			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return getPropertyId(templateid ,templateproperty);
	}
    
    /**
     * Obter id do template property
     * @param templateId 
     * @param templateproperty
     * @return
     */
    public int getPropertyId(int templateId, String templateproperty){
		
    	int id=0;
    	
		String sql = "select id from templateproperty where "
				+" idtemplate="+templateId
				+" AND "
				+ " templateproperty=\"" +templateproperty+"\"";
		
		try {
			// prepared statement para inserção
			Connection con = DatabaseConnectionFactory.getConnection();
    	
			PreparedStatement stmt = con.prepareStatement(sql);

			ResultSet rs = stmt.executeQuery();
             
            if(rs.next()) {
                 // criando o objeto TemplateProperty
   
            	 id=rs.getInt("id");

             }

			stmt.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return id;
	}
    


	public int retrievePropertyId(int templateId, String templateproperty) {

		
		if(templateproperty==null) {
			return 0;
		}
		int id= getPropertyId(templateId, templateproperty);
		
		if(id!=0){
			return id;
		}
		
		return insert(templateId, templateproperty);
		
	}


	public TemplateProperty getTemplateProperty(int idtemplateproperty) {
		
		//id inexistentes
		if(idtemplateproperty<=0) {
			return null;
		}
		
		List<TemplateProperty> templatePropertys=null;
		
		try {
			 
            
            PreparedStatement stmt = this.connection.
                    prepareStatement("select * from templateproperty where id="+idtemplateproperty);
            ResultSet rs = stmt.executeQuery();
            
            if (rs==null){
            	return null;
           	}
            
            templatePropertys= new ArrayList<TemplateProperty>();
            
           
            while (rs.next()) {
                // criando o objeto TemplateProperty
           	 	String templateproperty=rs.getString("templateproperty");
           	 	int templateId=rs.getInt("idtemplate");
          
           	 	System.out.println("idtemplate ="+templateId);
           	    System.out.println("templateproperty ="+templateproperty);
        	 	
           	 	TemplateProperty templateProperty = new TemplateProperty(idtemplateproperty,templateId, templateproperty);

           	 	// adicionando o objeto à lista
           	 	templatePropertys.add(templateProperty);
                
           	 	
                break;
            }
            rs.close();
            stmt.close();
           
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
		
		return templatePropertys.get(0);
	
	}
	

} 
