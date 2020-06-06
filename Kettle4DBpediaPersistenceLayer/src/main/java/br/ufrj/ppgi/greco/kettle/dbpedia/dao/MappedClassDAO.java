package br.ufrj.ppgi.greco.kettle.dbpedia.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import br.ufrj.ppgi.greco.kettle.dbpedia.db.DatabaseConnectionFactory;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.TemplateMapping;

import java.sql.PreparedStatement ;
import java.sql.ResultSet;
import java.sql.SQLException;



//So par testes: nao será usada
public class MappedClassDAO {
	
	private Connection connection;

	public MappedClassDAO(){
		
		this.connection=DatabaseConnectionFactory.getConnection();
	}
	
	public List<MappedClass> list(){
		
		List<MappedClass> mappedClasses=null;
		
		 try {
			 
             
             PreparedStatement stmt = this.connection.
                     prepareStatement("select * from MappedClass");
             
             ResultSet rs = stmt.executeQuery();
             
             if (rs!=null)
            	 mappedClasses= new ArrayList<MappedClass>();
             
             
             while (rs.next()) {
                 // criando o objeto TemplateMapping
            	 
            	 int id=rs.getInt("id");
            	 int idtemplate=rs.getInt("idtemplate");
            	 String ontologyclass=rs.getString("ontologyclass");
            	 
            	 
            	 MappedClass mappedClass = new MappedClass(id, idtemplate, ontologyclass);
                
                 mappedClasses.add(mappedClass);
             }
             rs.close();
             stmt.close();
            
         } catch (SQLException e) {
             throw new RuntimeException(e);
         }
		
		return mappedClasses;
	}
	
	
	
    public List<MappedClass> listByOntologyClass(String ontologyclass_){
		
		List<MappedClass> mappedClasses=null;
		
		 try {
			 
             
             PreparedStatement stmt = 
            		 this.connection.prepareStatement(""+
                           "select * from MappedClass where ontologyclass=\""
                    		 				+ontologyclass_+"\"");
             
             ResultSet rs = stmt.executeQuery();
             
             
             if (rs!=null)
            	 mappedClasses= new ArrayList<MappedClass>();
             
             
             while (rs.next()) {
                 // criando o objeto TemplateMapping
            	 
            	 int id=rs.getInt("id");
            	 int idtemplate=rs.getInt("idtemplate");
            	 String ontologyclass=rs.getString("ontologyclass");
            	 
            	 
            	 MappedClass mappedClass = new MappedClass(id, idtemplate, ontologyclass);
                
                 mappedClasses.add(mappedClass);
             }
             rs.close();
             stmt.close();
            
         } catch (SQLException e) {
             throw new RuntimeException(e);
         }
		
		return mappedClasses;
	}
	
	public Template getTemplateMappingByTemplateName(String templatename){
		
		Template template=null;
		
		return template;
		
	}
	
	
    public TemplateMapping getTemplateMappingByTemplateId(String id){
		
		TemplateMapping Template=null;
		return Template;
		
		
		
	}
	
    //inserir template apenas com campo nome do template
    public void insert(Template template){
		
		String sql = "insert into template " +
            "(templatename, pageid)" +
            " values (?,?)";
		
		try {
			// prepared statement para inserção
			Connection con = DatabaseConnectionFactory.getConnection();
    	
			PreparedStatement stmt = con.prepareStatement(sql);

			//Confifigurar os valores

			stmt.setString(1,template.getTitle());
			
			stmt.setString(2,template.getPageid());

			stmt.execute();

			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
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
    
    public int getTemplateIdByName(String templateName){
		
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
	

} 
