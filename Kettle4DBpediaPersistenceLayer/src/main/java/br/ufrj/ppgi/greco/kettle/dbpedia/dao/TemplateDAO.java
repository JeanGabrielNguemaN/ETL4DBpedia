package br.ufrj.ppgi.greco.kettle.dbpedia.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import br.ufrj.ppgi.greco.kettle.dbpedia.db.DatabaseConnectionFactory;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;
import java.sql.PreparedStatement ;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TemplateDAO {
	
	private Connection connection;

	public TemplateDAO(){
		
		this.connection=DatabaseConnectionFactory.getConnection();
		
	}
	
	public List<Template> listTemplates(){
		List<Template> templates=null;
		
		 try {		 
             
             PreparedStatement stmt = this.connection.
                     prepareStatement("select * from template");
             
             ResultSet rs = stmt.executeQuery();
             
             if (rs!=null)
            	 templates= new ArrayList<Template>();
             
             
             while (rs.next()) {
                 // criando o objeto TemplateMapping
            	
            	 String templatename=rs.getString("templatename");
            	 String pageid=rs.getString("pageid");
            	 int id=rs.getInt("id");
            	 
                 Template template = new Template(templatename, pageid);
                 
                 template.setId(id);
                
                 templates.add(template);
             }
             rs.close();
             
             stmt.close();
             
         } catch (SQLException e) {
             throw new RuntimeException(e);
         }
		
		return templates;
	}
	
	
    /**
     * Obter template por id
     * @param id
     * @return
     */
    public Template getTemplateById(int id){
        Template template=null;
		
		try {
			
            String query="select * from template where id="+id;
            
			PreparedStatement stmt = 
            		this.connection.prepareStatement(query);
			
            ResultSet rs = stmt.executeQuery();
            
            if (rs==null){
           	  return null;
            }
            
            //mover cursor ao primeiro
            boolean first=rs.first();
            
            if (!first){
            	return null;
            }
            	
           	String templatename=rs.getString("templatename");
                
           	template = new Template(templatename, null);
           	
           	template.setId(id);
           
            rs.close();
            stmt.close();
           
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
		
		return template;
	}
	
	
    //inserir template apenas com campo nome do template
    public int insert(Template template){
    	
    	int id=0;
    	
    	String sql = "insert into template " +
				 "(templatename, pageid)" +
				 " values (?,?)";
    	
    	
    	try {
			// prepared statement para inserção
			Connection con = DatabaseConnectionFactory.getConnection();
    	
			PreparedStatement stmt = con.prepareStatement(sql);

			//Confifigurar os valores

			stmt.setString(1,template.getTitle());
			
			stmt.setString(2,""+template.getPageid());

			stmt.execute();

			stmt.close();
			
			id=getTemplateIdByName(template.getTitle());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
    	return id;
    }
    
    
    
 
    /**
     * Insersão pelo nome do template
     * @param templateName
     */
    public void insert(String templateName){
		
		String sql = "insert into template " +
            "(templatename)" +
            " values (?)";
		
		try {
			// prepared statement para inserção
			Connection con = DatabaseConnectionFactory.getConnection();
    	
			PreparedStatement stmt = con.prepareStatement(sql);

			//Confifigurar os valores

			stmt.setString(1,templateName);
	
			stmt.execute();

			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
    
    public static int getTemplateIdByName(String templateName){
		
    	int id=0;
		String sql = "select id from template where templatename=\"" +templateName+"\"";
		
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
    
    
    public Template getTemplate(String templateName){
		
    	
    	Template template=null;
 		
 		int id =  getTemplateIdByName(templateName);
 		
 		
 		if(id<=0){
 			return null;
 		}
 		template=new Template(templateName, null);
 			 template.setId(id);
 		
		return template;
	}

	public int instertOrUpdate(String templateName) {
		int id =  getTemplateIdByName(templateName);
 		
		if(id!=0) {
			return id;
		}
		
		insert(templateName);
		
		return getTemplateIdByName(templateName);
	}
} 
