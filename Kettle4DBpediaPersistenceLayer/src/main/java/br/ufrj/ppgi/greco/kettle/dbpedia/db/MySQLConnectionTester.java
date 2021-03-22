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
package br.ufrj.ppgi.greco.kettle.dbpedia.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.ufrj.ppgi.greco.kettle.dbpedia.dao.MappedClass;
import br.ufrj.ppgi.greco.kettle.dbpedia.dao.MappedClassDAO;
import br.ufrj.ppgi.greco.kettle.dbpedia.dao.TemplateDAO;
import br.ufrj.ppgi.greco.kettle.dbpedia.dao.TemplateManager;
import br.ufrj.ppgi.greco.kettle.dbpedia.dao.TemplateMappingDAO;
import br.ufrj.ppgi.greco.kettle.dbpedia.dao.TemplateMappingManager;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.TemplateMapping;

public class MySQLConnectionTester {

	public static void main(String[] args) {
		
		
		TemplateManager templateManger= new TemplateManager();
		
		Template t= new Template("Info/Template 32", null);
		
		templateManger.instert(t);
		
		testeTemplateAcess();
		//listMappedClass();
		
		//testeTemplaMappingteAcess();
		
		//System.out.println("ID = "+new TemplateDAO().getTemplateIdByName("Info/Taxonomia"));
		//listTemplateMapping();
		
		//teste1();
	}

	/**
	 * consultar templates
	 */
	public static void testeTemplateAcess() {
		TemplateManager templateManger= new TemplateManager();
		
		List<Template> templates = templateManger.listTemplates();
		
		int i=0;
		for(Template tm: templates){
			
			 System.out.print(++i+ " - id ="+tm.getId()+"\tTítulo ="+tm.getTitle());
			 
			 System.out.println("\tPageid ="+tm.getPageid());
			 
			 //if (i==5)
			 // break;
		 }
	}

	public static void testeTemplaMappingteAcess() {
		TemplateMappingManager tmManger= new TemplateMappingManager();
		//para contar iterações
		int i=0;
		 
		TemplateMapping templateMapping = 
				//tmManger.getTemplateMappingByTemplateId(674);
				tmManger.getTemplateMappingByTemplateId(7);
		
		 System.out.println("---------------------------------------------\n"
		    		+ "Consulta do templateMapping para do template com id 7 :");
		    
		System.out.print("id do template="+templateMapping.getTemplateId()+""
							+"\t Conditional ="+templateMapping.isConditionalMapping()+"\n\n\n"
							+"\tDML ="+templateMapping.getMappings()+""
							+"\n\n");
	    
	    
		/*
		int idtemplate=674;
	    Set<String> mappedClasses = tmManger.listMappedClassesByTemplateId(idtemplate);

         
	     System.out.println("---------------------------------------------\n"
	    		+ "Classes para quais mapea o template com id :"+idtemplate);
	    		
	     for (String maptoclass: mappedClasses){
	    	 System.out.println("Class "+(++i)+" ="+ maptoclass);
	     }
	   
	    */
	   //LISTAR Templates mapeados a uma classe
		
		/*
	    String classeName = "Plant";
	    
		Set<Template> templates = tmManger.listTemplatesMappedToClasses(classeName);
	    
	    System.out.println("---------------------------------------------\n"
	    		+ "Templates mapeados a uma classe :"+classeName);
	     
	    for (Template template: templates){
	    	
	    	 System.out.println("Template "+(++i)+" ="+ template.getTitle());
	    	 
	    }
	    */
	    
	}
	
	

	
	public static void listMappedClass() {
		
		MappedClassDAO mappedClassDAO= new MappedClassDAO();
		 
		List<MappedClass> mappedClasses = mappedClassDAO.list();
		
	   //List<MappedClass> mappedClasses = mappedClassDAO.listByOntologyClass("Species");
		
		int i=0;
		
		Set<String> classes= new HashSet<String>();
		
		for(MappedClass mappedClass: mappedClasses){
			
			 System.out.print(++i+" - id ="+mappedClass.getId()+"\tClasse ="+mappedClass.getOntologyclass());
			 
			 System.out.println("\tTemplateId ="+mappedClass.getIdtemplate());
			 
			 classes.add(mappedClass.getOntologyclass());
			 
			 
			 
			// if (i==5)
			//	 break;
		 }// for
		
		i=0;
		for(String clas: classes){
			System.out.println(++i+" - "+clas);
		}
	}
	/**
	 * 
	 */
	public static void listTemplateMapping() {
		 TemplateMappingDAO templateMappingDao= new TemplateMappingDAO();
		 List<TemplateMapping> tms = templateMappingDao.getTemplateMapppings();
		 
		 for(TemplateMapping tm: tms){
			 System.out.println("id ="+tm.getTemplateId());
			 System.out.println("isCond? ="+tm.getTemplateId());
			 System.out.println("Mappings ="+tm.getMappings());
			
		 }
	}

	/**
	 * 
	 */
	public static void teste1() {
		Connection conn= DatabaseConnectionFactory.getConnection();
		
		
		Statement stmt = null;
		ResultSet rs = null;

		try {
		    stmt = conn.createStatement();
		    
		    rs = stmt.executeQuery("SELECT dmlmapping FROM templatemapping WHERE idtemplate=1");

		    /*//or alternatively, if you don't know ahead of time that
		     //the query will be a SELECT...

		    if (stmt.execute("SELECT foo FROM bar")) {
		        rs = stmt.getResultSet();
		    }
			*/
		    while (rs.next()) {
	            String dmlmapping = rs.getString("dmlmapping");
	            /*String nome = rs.getString("NOMEMUNICIPIO");
	            String estado = rs.getString("estado");
	            String misoreg = rs.getString("MESORREGIAOGEO");
	            String microreg = rs.getString("MICROREGIAOgeo");
	            */
	            System.out.println("***"+dmlmapping.replace(" ",""));
	        }
		}
		catch (SQLException ex){
		    // handle any errors
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		finally {
		    // it is a good idea to release
		    // resources in a finally{} block
		    // in reverse-order of their creation
		    // if they are no-longer needed

		    if (rs != null) {
		        try {
		            rs.close();
		        } catch (SQLException sqlEx) { } // ignore

		        rs = null;
		    }

		    if (stmt != null) {
		        try {
		            stmt.close();
		        } catch (SQLException sqlEx) { } // ignore

		        stmt = null;
		    }
		}
	}
}
