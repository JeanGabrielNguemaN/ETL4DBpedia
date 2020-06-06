package br.ufrj.ppgi.greco.kettle.dbpedia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.ufrj.ppgi.greco.kettle.dbpedia.db.DatabaseConnectionFactory;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Condition;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.TemplateMapping;

public class ConditionDAO {

	private Connection connection;

	public ConditionDAO(){
		
		this.connection=DatabaseConnectionFactory.getConnection();
	}
	public List<Condition> listConditionByTemplateId(int templateid){
		List<Condition> conditions=null;
		
		 try {
			 
             
             PreparedStatement stmt = this.connection.
                     prepareStatement("select * from Condition where idtemplate=?");
             ResultSet rs = stmt.executeQuery();
             
             if (rs!=null)
            	 conditions= new ArrayList<Condition>();
             
             
             while (rs.next()) {
                 // criando o objeto Condition
            	 
                  Condition condition = 
                		 new Condition(rs.getInt("id"), rs.getString("templateproperty"), 
                		 rs.getString("operator"), rs.getString("propertyvalue"), rs.getString("maptoclass"));
               
                 condition.setTemplateid(templateid);
                
                
                 conditions.add(condition);
             }
             rs.close();
             stmt.close();
            
         } catch (SQLException e) {
             throw new RuntimeException(e);
         }
		
		return conditions;
	}
	
	public Template getTemplateMappingByTemplateName(String templatename){
		
		Template template=null;
		
		return template;
		
	}
	
	
    public TemplateMapping getTemplateMappingByTemplateId(String id){
		
		TemplateMapping Template=null;
		return Template;
		
		
		
	}
	
    public void insertTemplateMapping(TemplateMapping template){
		
		
	String sql = "insert into templatemapping " +
            "(idtemplate, dmlmapping, conditionalmapping)" +
            " values (?,?,?)";

    try {
        // prepared statement para inserção
    	Connection con = DatabaseConnectionFactory.getConnection();
    	
       PreparedStatement stmt = con.prepareStatement(sql);

        // seta os valores

        String conditionalMapping=template.isConditionalMapping()?"s":"n";
        
        stmt.setInt(1,1);
        stmt.setString(2,template.getMappings());
        stmt.setString(3,conditionalMapping);
       
        stmt.execute();
        
        
        stmt.close();
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }

	}
}
