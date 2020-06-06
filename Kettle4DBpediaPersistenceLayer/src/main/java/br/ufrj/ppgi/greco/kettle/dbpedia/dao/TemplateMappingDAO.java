package br.ufrj.ppgi.greco.kettle.dbpedia.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.ufrj.ppgi.greco.kettle.dbpedia.db.DatabaseConnectionFactory;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Condition;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.OntologyClass;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.OntologyProperty;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.TemplateMapping;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.TemplateProperty;

import java.sql.PreparedStatement ;
import java.sql.ResultSet;
import java.sql.SQLException;



public class TemplateMappingDAO {
	
	private Connection connection;

	public TemplateMappingDAO(){
		
		this.connection=DatabaseConnectionFactory.getConnection();
	}
	
	public List<TemplateMapping> getTemplateMapppings(){
		List<TemplateMapping> templateMappings=null;
		
		 try {
			 
             PreparedStatement stmt = this.connection.
                     prepareStatement("select * from templatemapping");
             ResultSet rs = stmt.executeQuery();
             
             if (rs!=null)
            	 templateMappings= new ArrayList<TemplateMapping>();
             
             
             while (rs.next()) {
                 // criando o objeto TemplateMapping
            	 
            	 
            	 String mappings=rs.getString("dmlmapping");
            	 int templateId=rs.getInt("idtemplate");
           
            	 
                 TemplateMapping TemplateMapping = new TemplateMapping(templateId, mappings);
                 
                 TemplateMapping.setTemplateId(templateId);
                 TemplateMapping.setMappings(mappings);
                 
                 TemplateMapping.setConditionalMapping(rs.getBoolean("conditionalmapping"));

                 // adicionando o objeto à lista
                 templateMappings.add(TemplateMapping);
             }
             rs.close();
             stmt.close();
            
         } catch (SQLException e) {
             throw new RuntimeException(e);
         }
		
		return templateMappings;
	}
	
	public List<TemplateMapping> listByName(int id){
		
		TemplateMapping Template=null;
		
		List<TemplateMapping> templateMappings=null;
		
		try {
			 
            String queryString = "select * from templatemapping "
            						+ " where idtemplate=\""+id+"\"";
			PreparedStatement stmt = this.connection.
                    prepareStatement(queryString);
            ResultSet rs = stmt.executeQuery();
            
            if (rs!=null)
           	 templateMappings= new ArrayList<TemplateMapping>();
            
            
            while (rs.next()) {
                // criando o objeto TemplateMapping
           	 
           	 
           	 String mappings=rs.getString("dmlmapping");
           	 int templateId=rs.getInt("idtemplate");
          
           	 
                TemplateMapping TemplateMapping = new TemplateMapping(templateId, mappings);
                
                TemplateMapping.setTemplateId(templateId);
                TemplateMapping.setMappings(mappings);
                
                TemplateMapping.setConditionalMapping(rs.getBoolean("conditionalmapping"));

                // adicionando o objeto à lista
                templateMappings.add(TemplateMapping);
            }
            rs.close();
            stmt.close();
           
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
		
		return templateMappings;
		
		//return Template;
		
	}
   public TemplateMapping getTemplateMappingByTemplateId(int id){
		
		
		List<TemplateMapping> templateMappings=null;
		
		try {
			 
            
            PreparedStatement stmt = this.connection.
                    prepareStatement("select * from templatemapping where idtemplate="+id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs==null) {
            	return null;
            }
            System.out.println("NAO NULO");
    		
           	templateMappings= new ArrayList<TemplateMapping>();
            
            
            while (rs.next()) {
                // criando o objeto TemplateMapping
           	 
           	 
            System.out.println("NO WHILE");
        		
           	 String mappings=rs.getString("dmlmapping");
           	 int templateId=rs.getInt("idtemplate");
          
             TemplateMapping templateMapping = new TemplateMapping(templateId, mappings);
                
             templateMapping.setTemplateId(templateId);
             templateMapping.setMappings(mappings);
             
             //obter mapeamento de proprieddades
             HashMap<String, String> propertiesMappingsHashMap = this.getPropertiesMappings(templateId);
             //configurar
             templateMapping.setAtributesMappings(propertiesMappingsHashMap);
             templateMapping.setConditionalMapping(rs.getBoolean("conditionalmapping"));
             
             
             if(templateMapping.isConditionalMapping()){
            	//obter mapeamento de proprieddades
                ArrayList<Condition> conditions = this.getConditions(templateId);
                
                 //configurar
                 templateMapping.setConditions(conditions);
             }

                // adicionando o objeto à lista
             templateMappings.add(templateMapping);
             
             System.out.println("ADD...");
                
             break;
            }
            rs.close();
            stmt.close();
           
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
		
		return templateMappings.get(0);
		
	
		
	}
   
   
   
   
   
   public TemplateMapping getTemplateMappingByTemplateByName(String templatename){
		
		
		TemplateMapping templateMapping=null;
		
		
		TemplateDAO templateDAO= new TemplateDAO();
		
		Template template=templateDAO.getTemplate(templatename);
		
		
		if(template==null) {
			return null;
		}
		//System.out.println("Template Name :"+template.getTitle());

		templateMapping= getTemplateMappingByTemplateId(template.getId());
			
		return templateMapping;
	}
	
	
    public void insertTemplateMapping(TemplateMapping templateMapping){
    	
    	
    	if(hasTemplateMapping(templateMapping)) {
    		
    		return;
    	}
		
		String sql = "insert into templatemapping " +
            "(idtemplate, dmlmapping, conditionalmapping)" +
            " values (?,?,?)";
		
		try {
			// prepared statement para inserção
			Connection con = DatabaseConnectionFactory.getConnection();
    	
			PreparedStatement stmt = con.prepareStatement(sql);

        // seta os valores

			boolean conditionalMapping=templateMapping.isConditionalMapping();
        
			stmt.setInt(1,templateMapping.getTemplateId());
			
			stmt.setString(2,templateMapping.getMappings());
			
			stmt.setBoolean(3,conditionalMapping);
       
			stmt.execute();
			
			//inserir propriedades mapeadas
			//insertMappedProperties(templateMapping);
			//inserir classes mapeadas
			//insertMappedClasses(templateMapping);
			//inserir condições mapeadas
			//insertMappedConditions(templateMapping);
			
			
			
			stmt.close();
			
			insertMappedPropertiesV2(templateMapping);
			//inserir classes mapeadas
			
			insertMappedClassesV2(templateMapping);
			
			//inserir condições mapeadas
			insertMappedConditionsV2(templateMapping);
			
			//
			
		} catch (SQLException e) {
			throw new RuntimeException("TEMPLATE :"+ templateMapping.getTemplateName()+
					"TEMPLATE ID :"+templateMapping.getTemplateId()+
					"\n\nEXCEPTION MESSAGE : "+e);
		}
		
	}
    
    
    
public boolean hasTemplateMapping(TemplateMapping templateMapping){
		
	    boolean exists=false;
		String sql = "SELECT idtemplate FROM templatemapping"
				    + "  WHERE idtemplate=" +templateMapping.getTemplateId();
		
		
		try {
			// prepared statement para inserção
			Connection con = DatabaseConnectionFactory.getConnection();
    	
			PreparedStatement stmt = con.prepareStatement(sql);
			
			
	        ResultSet rs = stmt.executeQuery();
	        
	        if (rs==null){
	        	return exists;
	        }

	  
	        HashSet<Integer> idontologyclassSet = new HashSet<Integer>();
	        while (rs.next()) {
	 
	        	int id=rs.getInt("idtemplate");
	        	
	        	if(id == templateMapping.getTemplateId()) {
	        		exists=true;
	        	}
	        }

			stmt.execute();
			
			stmt.close();
			
		} catch (SQLException e) {
			throw new RuntimeException("TEMPLATE :"+ templateMapping.getTemplateName()+
					"TEMPLATE ID :"+templateMapping.getTemplateId()+
					"\n\nEXCEPTION MESSAGE : "+e);
		}
		
		return exists;
	}
	

     private void insertMappedConditionsV2(TemplateMapping templateMapping) {
		// TODO Auto-generated method stub
		
		
		ArrayList<Condition> conditions=templateMapping.getConditions();
		int templateId=templateMapping.getTemplateId();
		
		if (conditions!=null){
			
			for(Condition condition:conditions){
					
					insertMappedConditionV2(templateId, condition);
			}
			
		}				
	}
	
	private void insertMappedConditions(TemplateMapping templateMapping) {
		// TODO Auto-generated method stub
		
		
		ArrayList<Condition> conditions=templateMapping.getConditions();
		int templateId=templateMapping.getTemplateId();
		
		if (conditions!=null){
			
			for(Condition condition:conditions){
					
					insertMappedCondition(templateId, condition);
			}
			
		}				
	}
	
	/**
	 * @param templateMapping
	 * @param condition
	 * @throws RuntimeException
	 */
	
	public void insertMappedConditionV2(int templateId, Condition condition) throws RuntimeException {
		try {
			
			
			//obter id do template property
			TemplatePropertyDAO templatePropertyDAO = new TemplatePropertyDAO();
			
			int idtemplateproperty=templatePropertyDAO.retrievePropertyId(templateId,condition.getTemplateProperty());
			
			//obter id da classe
			OntologyClassDAO ontologyClassDAO= new OntologyClassDAO();
			int idontologyclass=ontologyClassDAO.retrieveOntClassId(condition.getMapToClass());
			
			
			String sql = "";
			
			// prepared statement para inserção
			Connection con = DatabaseConnectionFactory.getConnection();
				  	
			PreparedStatement stmt = null;
						
			if(idtemplateproperty==0) {

				// seta os valores
		       sql+="insert into mappedcondition " +
				        "(idtemplate, idontologyclass,coperator, propertyvalue)" +
				        " values (?,?,?, ?)";
		       
		       stmt=con.prepareStatement(sql);
		       
		       System.out.println("SQL ="+sql);
				stmt.setInt(1,templateId);
				stmt.setInt(2,idontologyclass);
				stmt.setString(3,condition.getOperator());
				stmt.setString(4,condition.getPropertyValue());
			
		      
				stmt.execute();
				stmt.close();
				
				return ;
			}
			
			sql += "insert into mappedcondition " +
			        "(idtemplate, idtemplateproperty, idontologyclass,coperator, propertyvalue)" +
			        " values (?,?,?,?, ?)";
			
			
			stmt=con.prepareStatement(sql);
	      
			// seta os valores
			stmt.setInt(1,templateId);
			stmt.setInt(2,idtemplateproperty);
			stmt.setInt(3,idontologyclass);
			stmt.setString(4,condition.getOperator());
			stmt.setString(5,condition.getPropertyValue());
		
	      
			stmt.execute();
			stmt.close();
		
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void insertMappedCondition(int templateId, Condition condition) throws RuntimeException {
		try {
			
			String sql = "insert into mappedcondition " +
			        "(idtemplate, templateproperty, coperator, propertyvalue, maptoclass)" +
			        " values (?,?,?,?, ?)";
			
			// prepared statement para inserção
			Connection con = DatabaseConnectionFactory.getConnection();
	  	
			PreparedStatement stmt = con.prepareStatement(sql);
			
			// seta os valores
	      
			stmt.setInt(1,templateId);
			stmt.setString(2,condition.getTemplateProperty());
			stmt.setString(3,condition.getOperator());
			stmt.setString(4,condition.getPropertyValue());
			stmt.setString(5,condition.getMapToClass());
	      
			stmt.execute();
			stmt.close();
		
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Inserir classes mapeadas
	 * @param templateMapping
	 */
	private void insertMappedClassesV2(TemplateMapping templateMapping) {
		// TODO Auto-generated method stub
		
		ArrayList<String> mappedClasses=templateMapping.getMappedClasses();
		int templateId=templateMapping.getTemplateId();
		
		if (mappedClasses!=null){
			
			for(String mappedClass:mappedClasses){
					
					insertMappedClassV2(templateId, mappedClass);
					
			}
			
		}				
		
	}
	
	
	private void insertMappedClasses(TemplateMapping templateMapping) {
		// TODO Auto-generated method stub
		
		ArrayList<String> mappedClasses=templateMapping.getMappedClasses();
		int templateId=templateMapping.getTemplateId();
		
		if (mappedClasses!=null){
			
			for(String mappedClass:mappedClasses){
					
					insertMappedClass(templateId, mappedClass);
					
			}
			
		}				
		
	}
	
	/**
	 * Isnerir classe mapeada
	 * @param templateId
	 * @param mappedClass
	 */
	private void insertMappedClassV2(int templateId, String mappedClass) {
		try {
			
			
			
			
			OntologyClassDAO ontologyClassDAO= new OntologyClassDAO();
			
			int idontologyclass=ontologyClassDAO.retrieveOntClassId(mappedClass);
			
			
			String sql = "insert into mappedclass " +
			        "(idtemplate, idontologyclass)" +
			        " values (?,?)";
			
			// prepared statement para inserção
			Connection con = DatabaseConnectionFactory.getConnection();
	  	
			PreparedStatement stmt = con.prepareStatement(sql);
			
			// seta os valores
	      
			stmt.setInt(1,templateId);
			stmt.setInt(2,idontologyclass);
			
	      
			stmt.execute();
			stmt.close();
		
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	
	
	private void insertMappedClass(int templateId, String mappedClass) {
		try {
			String sql = "insert into mappedclass " +
			        "(idtemplate, ontologyclass)" +
			        " values (?,?)";
			
			// prepared statement para inserção
			Connection con = DatabaseConnectionFactory.getConnection();
	  	
			PreparedStatement stmt = con.prepareStatement(sql);
			
			// seta os valores
	      
			stmt.setInt(1,templateId);
			stmt.setString(2,mappedClass);
			
	      
			stmt.execute();
			stmt.close();
		
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	private void insertMappedProperties(TemplateMapping templateMapping) {
		ArrayList<Condition> conditions=templateMapping.getConditions();
		
		HashMap<String, String> propertiesHashMap = templateMapping.getAtributesMappings();
		
		
		int templateId=templateMapping.getTemplateId();
		
		if (propertiesHashMap!=null && propertiesHashMap.size()>0){
			
			Set<String> mappedProperties = propertiesHashMap.keySet();
			
			for(String mappedProperty:mappedProperties){
				
					String ontologyProperty=propertiesHashMap.get(mappedProperty);
					
					if(mappedProperty!=null && ontologyProperty!=null){
						
						insertMappedPropertyV2(templateId, mappedProperty, ontologyProperty);
						
					}
			}
			
		}				
		
	}
	
	private void insertMappedPropertiesV2(TemplateMapping templateMapping) {
		ArrayList<Condition> conditions=templateMapping.getConditions();
		
		HashMap<String, String> propertiesHashMap = templateMapping.getAtributesMappings();
		
		
		int templateId=templateMapping.getTemplateId();
		
		if (propertiesHashMap!=null && propertiesHashMap.size()>0){
			
			Set<String> mappedProperties = propertiesHashMap.keySet();
			
			for(String mappedProperty:mappedProperties){
				
					String ontologyProperty=propertiesHashMap.get(mappedProperty);
					
					if(mappedProperty!=null && ontologyProperty!=null){
						
						insertMappedPropertyV2(templateId, mappedProperty, ontologyProperty);
						
					}
			}
			
		}				
		
	}
    /**
     * Inserir mapeamentos de propriedades
     * @param templateId
     * @param mappedProperty
     * @param ontologyProperty
     */
	private void insertMappedProperty(int templateId, String mappedProperty, String ontologyProperty) {
		try {
			String sql = "insert into mappedproperty " +
			        "(idtemplate,templateproperty ,ontologyproperty)" +
			        " values (?,?,?)";
			
			// prepared statement para inserção
			Connection con = DatabaseConnectionFactory.getConnection();
	  	
			PreparedStatement stmt = con.prepareStatement(sql);
			
			// seta os valores
	      
			stmt.setInt(1,templateId);
			stmt.setString(2,mappedProperty);
			stmt.setString(3,ontologyProperty);
			
	      
			stmt.execute();
			stmt.close();
		
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	
	 /**
     * Inserir mapeamentos de propriedades
     * @param templateId
     * @param mappedProperty
     * @param ontologyProperty
     */
	
	private void insertMappedPropertyV2(int templateId, String mappedProperty, String ontologyProperty) {
		try {
			
			TemplatePropertyDAO templatePropertyDAO = new TemplatePropertyDAO();
			OntologyPropertyDAO ontologyPropertyDAO= new OntologyPropertyDAO();
			
			int idtemplateproperty=templatePropertyDAO.retrievePropertyId(templateId, mappedProperty);
			
			int idontologyproperty=ontologyPropertyDAO.retrievePropertyId(templateId, ontologyProperty);
			
			String sql = "insert into mappedproperty " +
			        "(idtemplate,idtemplateproperty ,idontologyproperty)" +
			        " values (?,?,?)";
			
			// prepared statement para inserção
			Connection con = DatabaseConnectionFactory.getConnection();
	  	
			PreparedStatement stmt = con.prepareStatement(sql);
			
			// seta os valores
	      
			stmt.setInt(1,templateId);
			stmt.setInt(2,idtemplateproperty);
			stmt.setInt(3,idontologyproperty);
			
	      
			stmt.execute();
			stmt.close();
		
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}

	

	/**
	 * Listar templates mapeadas a uma classe através de condições
	 * @param id
	 * @return
	 */
	public Set<String> listConditionMappedClassesByTemplateId(int id){

		Set<String> mappedClasses=null;
		
		try {

			System.out.println("ClasseName ="+id);
            /*
			PreparedStatement stmt = this.connection.
                    prepareStatement("select maptoclass from mappedcondition "
                    		+ " where idtemplate=\""+id+"\"");
            */
			
			PreparedStatement stmt = this.connection.
                    prepareStatement("select idontologyclass from mappedcondition "
                    		+ " where idtemplate=\""+id+"\"");
			
            ResultSet rs = stmt.executeQuery();
            
            if (rs==null){
            	return mappedClasses;
            }

            mappedClasses= new HashSet<String>();
            HashSet<Integer> idontologyclassSet = new HashSet<Integer>();
            while (rs.next()) {
            	/*
                // criando o objeto TemplateMapping
           	 	String maptoclass=rs.getString("maptoclass");

                // adicionando o objeto ao set
                mappedClasses.add(maptoclass);
                
                */
            	
            	int idontologyclass=rs.getInt("idontologyclass");
            	
            	idontologyclassSet.add(idontologyclass);
            	
            }
            
            
            
            rs.close();
            stmt.close();
         
          OntologyClassDAO ontoClassDAO= new 
        		  OntologyClassDAO();
            
         for (Integer idClass: idontologyclassSet) {
        	 
        	 mappedClasses.add(
        			 ontoClassDAO.getOntologyClass(idClass).getOntologyclass());
         }
           
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
		
		return mappedClasses;
		
	
		
	}
	
	/**
	 * Listar classes mapeados ao template através sem condition
	 * @param id
	 * @return
	 */
	public Set<String> listConditionnessMappedClassesByTemplateId(int id){

		Set<String> mappedClasses=null;
		
		try {

            PreparedStatement stmt = this.connection.
                    prepareStatement("select idontologyclass from mappedclass where idtemplate=\""+id+"\"");
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs==null){
            	return mappedClasses;
            }

            mappedClasses= new HashSet<String>();
            
            HashSet<Integer> idontologyclassSet = new HashSet<Integer>();
            
            while (rs.next()) {
                // criando o objeto TemplateMapping
           	 	//String maptoclass=rs.getString("idontologyclass");
            	int idontologyclass=rs.getInt("idontologyclass");


                // adicionando o objeto ao set
                //mappedClasses.add(maptoclass);
             	idontologyclassSet.add(idontologyclass);
            }
            rs.close();
            stmt.close();
            
           OntologyClassDAO ontoClassDAO= new 
          		  OntologyClassDAO();
              
           for (Integer idClass: idontologyclassSet) {
          	 
          	 mappedClasses.add(
          			 ontoClassDAO.getOntologyClass(idClass).getOntologyclass());
           }
           
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
		
		return mappedClasses;
	}
	
	/**
	 * Listar classes mapeados ao template
	 * @param id
	 * @return
	 */
	public Set<String> listMappedClassesByTemplateId(int id){
		Set<String> maptoclassesSet1=listConditionMappedClassesByTemplateId(id);
		
		Set<String> maptoclassesSet2=listConditionnessMappedClassesByTemplateId(id);
		//System.out.println("Chamou");
		
		if(maptoclassesSet1==null){
			
			return maptoclassesSet2;
		}
		
		//System.out.println("maptoclassesSet1 - SIZE="+ maptoclassesSet1.size());
		if(maptoclassesSet2==null){
			
			return maptoclassesSet1;
		}
		//System.out.println("maptoclassesSet2 - SIZE="+ maptoclassesSet2.size());
		
		//System.out.println("Chamou 3");
		
		
		//adicionar o primeiro set ao segundo
		
		maptoclassesSet2.addAll(maptoclassesSet1);
	    
		return maptoclassesSet2;
	}
	
	
	/**
	 * Listar templates mapeadas a uma classe através de condições
	 * @param classeName
	 * @return
	 */
	public Set<Template> listTemplatesMappedToClasses(String classeName){

		Set<Template> templates=null;
		//No máximo
		Set<String> tempaltes_ids=null;
		//cursor
		try {
			
			//Passo 1.1: obter os ids templates da classe existante na tabela 'mappedcondition'
            String tablenme="mappedcondition ";
            //String whereColumn=" maptoclass";
            String whereColumn=" idontologyclass";
            
            System.out.println("==== CLASSNAME ="+classeName);
            System.out.println("==== tablenme ="+tablenme);
            
            tempaltes_ids = getTemplatesIdSet(classeName, tablenme,whereColumn);
            
            //Passo 1.2: obter os ids templates da classe existante na tabela 'mappedclass'
            tablenme="mappedclass ";
            
            whereColumn=" idontologyclass";
			Set<String> tempaltes_ids_2 = getTemplatesIdSet(classeName, tablenme, whereColumn);
            
			//os dois são nulos--> templates nao possui mapeamentos
            if(tempaltes_ids==null && tempaltes_ids_2==null ){
            	return null;
            }
            
            //mapeamento direito não existe
            if(tempaltes_ids==null ){
            	tempaltes_ids=tempaltes_ids_2; 
            	
            //direito existe e mapeamento com condições tambem	
            } else if(tempaltes_ids_2!=null ){
            	tempaltes_ids.addAll(tempaltes_ids_2); 
            }
            
            //Passo 2: obter os templates dos ids obtidos
            //Primeiro obter o dao e iniciar o set dos templates

            TemplateDAO templateDAO= new TemplateDAO();
            templates = new HashSet<Template>();
            
            //para cada id, criar um template novo
            for (String template_id: tempaltes_ids){
            	//novo template?
            	Integer id= new Integer (template_id.trim());
            	
            	Template template = templateDAO.getTemplateById(id);
            	
            	if(template!=null){
            		templates.add(template);
            	}	
            } 
           
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
		
		return templates;	
	}
	/**
	 * Obter os id's dos templates que mapeam para uma classe, considerando 
	 * ou a tabela de mapeamento sem condição ("mappedclass") ou 
	 * a tabela de mapeamento com condições ("mappedcondition")
	 * @param classeName
     * @param tablenme. String "mappedcondition" ou "mappedclass"
	 * @param whereColumn
	 * @return
	 * @throws SQLException
	 */
	private Set<String> getTemplatesIdSet(String classeName, String tablenme, String whereColumn) throws SQLException {
		
		Set<String> tempaltes_ids=null;
		
		//for table mappedcondition
		PreparedStatement stmt = 
				getPreparedStatementOnTableByClasseName(classeName, tablenme, whereColumn);
        
		//
		if(stmt==null){
			return null;
		}
        ResultSet rs = stmt.executeQuery();
        
        if (rs==null){
        	return null;
        }
        //ids é nulo entao
      
        tempaltes_ids= new HashSet<String>();
       
		//Passo 1: Obter os ids dos templates
        while (rs.next()) {
            // criando o objeto TemplateMapping
       	 	int idtemplate=rs.getInt("idtemplate");

            // adicionando o objeto ao set
            //ids[index++]=idtemplate;  
       	 	if (idtemplate!=0)
       	      tempaltes_ids.add(""+idtemplate); 
        }
        return tempaltes_ids;
	}
	/**
	 *  Obter preparedStatementOnTableByClasseName para recuperaçao dos id's dos templates 
	 *  que mapeam para uma classe, considerando 
	 *  ou a tabela de mapeamento sem condição ("mappedclass") 
	 *  ou a tabela de mapeamento com condições ("mappedcondition")
	 * @param classeName
	 * @param tablenme
	 * @return  PreparedStatement
	 * @throws SQLException
	 */
	public PreparedStatement getPreparedStatementOnTableByClasseName(String classeName, String tablenme, String whereColumn)
			throws SQLException {
		
		OntologyClassDAO ontologyClassDAO= new OntologyClassDAO();
		
		int idontologyclass=ontologyClassDAO.getId(classeName);
		
		String query="select idtemplate from "
				+ tablenme
				+ " where "+whereColumn+"=\""+idontologyclass+"\"";
		
		PreparedStatement stmt = this.connection.
		        prepareStatement(query);
		return stmt;
	}
	
	/**
	 * Propriedades mapeadas ao template diretamente
	 * @param idtemplate
	 * @return
	 */
	private HashMap<String,String> getPropertiesMappings(int idtemplate){
		HashMap<String,String> atributesMappings=null;
		HashMap<String,String> atributesMappingsIds=null;
		
		 try {
			 
             /*
             PreparedStatement stmt = this.connection.
                     prepareStatement("select ontologyproperty,templateproperty from mappedproperty where idtemplate=?");
             */
			 PreparedStatement stmt = this.connection.
                     prepareStatement("select idontologyproperty,idtemplateproperty from mappedproperty where idtemplate="+idtemplate);
             
             
             ResultSet rs = stmt.executeQuery();
             
             if (rs!=null){
            	 atributesMappingsIds= new HashMap<String,String>();
            	 atributesMappings= new HashMap<String,String>();
             }
            
             
             while (rs.next()) {
                 // criando o objeto Condition
            	 
                 String idtemplateproperty=""+rs.getInt("idtemplateproperty");
                 String idontologyproperty=""+rs.getInt("idontologyproperty");
                 
                 atributesMappingsIds.put(idtemplateproperty, idontologyproperty);
                
             }
             rs.close();
             stmt.close();
            
         } catch (SQLException e) {
             throw new RuntimeException(e);
         }
		 
		 //Etapa 2: recuperar os valores
		 
		 Set<String> ids_set = atributesMappingsIds.keySet();
		 for(String id: ids_set){
			 int idtemplateproperty = new Integer (id);
			 int idontologyproperty= new Integer (atributesMappingsIds.get(id));
			 
			 
			 TemplatePropertyDAO templatePropertyDAO = new TemplatePropertyDAO();
			 
			 TemplateProperty templateproperty=templatePropertyDAO.getTemplateProperty(idtemplateproperty);
			 
			 OntologyPropertyDAO ontologyPropertyDAO= new OntologyPropertyDAO();
			
					 
			 OntologyProperty ontologyProperty=ontologyPropertyDAO.getOntologyProperty(idontologyproperty);
			
			
			atributesMappings.put(templateproperty.getTemplateproperty(), 
														ontologyProperty.getOntologyproperty());
		 }
		
		return atributesMappings;
	}
	
	
	
	/**
	 * Retornar as conditions
	 */
	private ArrayList<Condition>  getConditions(int idtemplate){
		
		ArrayList<Condition> conditions=null;
		
		HashMap<String,String> atributesMappings=null;
		HashMap<String,String> atributesMappingsIds=null;
		
		 try {
			 
             /*
             PreparedStatement stmt = this.connection.
                     prepareStatement("select ontologyproperty,templateproperty from mappedproperty where idtemplate=?");
             */
			 PreparedStatement stmt = this.connection.
                     prepareStatement("select * from mappedcondition where idtemplate="+idtemplate);
             
             
             ResultSet rs = stmt.executeQuery();
             
             if (rs!=null){
            	 conditions= new ArrayList<Condition>();
             }
            
             
             while (rs.next()) {
                 // criando o objeto Condition
            	 
            	 //int idCondition=rs.getInt("idtemplateproperty");
            	 int idCondition=rs.getInt("id");
            	 
                 int idtemplateproperty=rs.getInt("idtemplateproperty");
                 int idontologyclass=rs.getInt("idontologyclass");
                 
                 String operator=rs.getString("coperator");
                 String propertyvalue=""+rs.getString("propertyvalue");
                 
                 
                 //obter o valor da propriedade
                 TemplatePropertyDAO templatePropertyDAO = new TemplatePropertyDAO();
    			 
    			 TemplateProperty templateproperty=templatePropertyDAO.getTemplateProperty(idtemplateproperty);
    			 
    			 //Obter o valor da classe
    			 OntologyClassDAO ontologyClassDAO= new OntologyClassDAO();
    				 
    			 OntologyClass ontologyclass=ontologyClassDAO.getOntologyClass(idontologyclass);
    			 
                 String mapTotemplateproperty=null;
                 
                 if(templateproperty!=null) {
                	 mapTotemplateproperty=templateproperty.getTemplateproperty() ;
                 }
                 String mapToClass= ontologyclass.getOntologyclass();
                 
                 Condition condition =new Condition(idCondition, mapTotemplateproperty, 
                		 									operator, propertyvalue, mapToClass);
                 condition.setTemplateid(idtemplate);
                 
                 conditions.add(condition);
             }
             rs.close();
             stmt.close();
            
         } catch (SQLException e) {
             throw new RuntimeException(e);
         }
		
		
		return conditions;
	}

} 
