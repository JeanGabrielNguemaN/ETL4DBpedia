package br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


import br.ufrj.ppgi.greco.kettle.dbpedia.dao.TemplateDAO;
import br.ufrj.ppgi.greco.kettle.dbpedia.dao.TemplateManager;
import br.ufrj.ppgi.greco.kettle.dbpedia.dao.TemplateMappingManager;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Condition;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.TemplateMapping;
import br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter.Declaration;
import br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter.Utilities;
import br.ufrj.ppgi.greco.kettle.dbpedia.mappings.parser.MappingParser;

public class MappingParserDemo2 {

public static void main(String[] args) {
		
		System.out.println("BEGIN ...\n");
		
		MappingParser mparser= MappingParser.getInstance();
		
		//String mappings= obterConteudo("./files/mappingsDML.txt");
		
		//String nomeTemplate = "Info/Filme";
		String templateName = "Info/Taxonomia";
		
		TemplateManager tm= new TemplateManager();
		
		tm.instertOrUpdate(templateName);
		
		String mappings= obterConteudo("./files/mappingsDML.txt");
		
		/*
	    String templateName="Info/Aeroporto";

	    String mappings=Utilities.extractDMLMappings(templateName);
		*/
		Declaration declaration = mparser.gerarMapeamentos(templateName, mappings);
		
	
		//Declaration complexDeclaration = mparser.gerarMapeamentosV2("Info/Ator");
		
	   //System.out.println("\nImprimir ...............\n");
	    
	    declaration.print();
	    
	    testeInterpret(declaration, templateName, mappings);
	    
		System.out.println("\n\nEND ...");			
	}

	/**
	 * @param declaration
	 * @param templateName 
	 * @param mappings 
	 */
	public static void testeInterpret(Declaration declaration, String templateName, String mappings) {
		

		if(declaration==null){
			System.out.println("Declaraçao nula");
			return;
		}
		
		//template geração
		TemplateDAO templateDAO = new TemplateDAO();
		
		Template template = templateDAO.getTemplate(templateName);
		
		if(template==null){
			return;
		}
		System.out.println(" TEMPLATE ID ="+ template.getId());
		//criar a estrutura dos mapeamentos de um templates
		TemplateMapping templateMapping = 
				(TemplateMapping)declaration.interpret(template);
	
		if(templateMapping==null){
			return;
		}
		
		System.out.println("ID template : "+templateMapping.getTemplateId()+ "\n\n\nIs conditional "+ templateMapping.isConditionalMapping());
		
		HashMap<String, String> propsMap = templateMapping.getAtributesMappings();
		
		if(propsMap!=null){
			
			//System.out.println("Properties  size="+propsMap.size());
			
			Set<String> props=propsMap.keySet();
			
			for(String prop: props){
				
				System.out.println(prop+" --> "+ propsMap.get(prop));
				
			}
		}
		
	
		ArrayList<Condition> conditions = templateMapping.getConditions();
		
		
		if(conditions!=null){
			
			System.out.println(" ............CONDITIONS..............");
			
			System.out.println("templateProperty\toperator\tvalue\tmapToClass  (size ="+ conditions.size()+")");
			
			for(Condition cond: conditions){
				System.out.println("--------------");
				
				System.out.println(cond.getTemplateProperty()+"\t"
				+cond.getOperator()+"\t"
				+cond.getPropertyValue()+"\t"
				+cond.getMapToClass()+"\t");
				
			}
			
		}
		
		System.out.println("\n\nID template : "+templateMapping.getTemplateId()+ "\n\n\nIs conditional "+ templateMapping.isConditionalMapping());
		
		templateMapping.setMappings(mappings);
		
		
		//popuplar. Vericar sempre o database usado. Para testes: dbpediaexptest.
		new TemplateMappingManager().insertTemplateMapping(templateMapping);
	}

	/**
	 * @param mappings
	 */
	public void gerarMapeamentos(String nomeTemplate) {
		
	/*	
		String mappings= Utilities.extractDMLMappings(nomeTemplate);
		
		//mappings= mappings.substring(56);
		mappings=mappings.replace("\n", "").trim();
		
		mappings=normalizarDMLDeclarations(mappings);
		
		Declaration  mappingsAtor=null;  
		
		System.out.println("\n\nExtraindo declarações..................... \n");
			
		mappingsAtor=extractDeclarationV3(mappingsAtor, mappings);
		
	    System.out.println("\nImprimir ...............\n");
		mappingsAtor.print();
		*/
	}

	/** extrair declaracao dml apos mappings= por exemplo, ou cases=
	 * @param mappings
	 */
	public static String extrairDMLDeclaration(String mappings) {
		
		char[] cars = mappings.toCharArray();
		
		int i=0;
		int qde_fechadas=0;
		int qde_abertas=0;
		
		String str="";
		
		while ( i<cars.length){
			
			if(cars[i]=='{'){
				qde_abertas++;
			}
			else if(cars[i]=='}'){
				qde_fechadas++;
			}
			str+=cars[i];
			if (qde_fechadas!=0 && qde_fechadas==qde_abertas && (qde_fechadas%2==0)){
				break;
			}
			
			i++;
			
		}
		return str;
	}

	
	
	/*
	private ComplexDeclaration extractDeclarationV3(ComplexDeclaration  declaration, 
			String mappingsDML) {
		
		
		if(mappingsDML==null){
			return null;
		}
		
		String mappings= removerCommentarios(mappingsDML);
				
		//divir em 2 tokens
		String[] tokens=mappings.replace("\n","").trim().split("[|]",2);
		
		String part_1=tokens[0].trim();
		
		ComplexDeclaration  complexDeclaration=declaration;  
		
		//System.out.println(" CHAMOU ");
		if (tokens.length!=2){
			return   declaration;
		}
		
		//É declaração
		if(mappings.trim().substring(0, 2).equals("{{")){
			//extrair declaração
			String term=part_1.trim().split("[{][{]")[1];
			
			//System.out.println("Term ="+term );
		
			
			complexDeclaration=new ComplexDeclaration(term);
		}
		//segunda parte
		String part_2=tokens[1].trim(); 
		//remover }} do fim da declaração
		int part_2_len=part_2.length();
		
		if ( part_2.substring(part_2_len-2, part_2_len-1).equals("}}")){
		
			part_2= part_2.substring(0,part_2.length()-2);
			
		}
	
		//tokens[1].substring(0,part_3[1].length()-2);
			
		String[] part_3 = part_2.split("=", 2);
		
		String parameter=part_3[0].trim();
		
		//String parameter=part_2.split("=", 2)[0].trim();
			
		//System.out.println("Parameter="+parameter);
			
		//extrair valor: part_3[1]
		//System.out.println("Para valor ="+part_3[1]);
				
		//valor é conjunto de declarações
		if (part_3[1].trim().substring(0, 2).equals("{{")){
	
				//System.out.println("valor é conjunto de declarações");
				List<Declaration> declarations= new ArrayList<Declaration>();
				
				//String valor = part_3[1].substring(0,part_3[1].length()-2);
				//System.out.println(valor);
				//normalizar primeiro
				
				String normalized_part31= normalizarDMLDeclarations(part_3[1].trim());
				String[] arr = extrairDMLDeclarations(normalized_part31);
				
				int beginIndex =0;
				
				for (String dmlDecl: arr){
					
					beginIndex+=dmlDecl.length();
					
					//System.out.println("DML ="+ dmlDecl);
					
					ComplexDeclaration cdeclaration = 
							extractDeclarationV3(null, dmlDecl);
					declarations.add(cdeclaration);
					
				}
				
			
				complexDeclaration.add(parameter, declarations);
				
				//sobrou algo
				String str = normalized_part31.trim().substring(beginIndex);
				if(str!=null){
					 extractDeclarationV3(complexDeclaration, 
							 str);
					//System.out.println("TEST ="+ str);
				}
				
		}
		else { //valor é string
				//System.out.println("valor é string");
				
				String[] part_4 = part_3[1].split("[|]");
				
				String value = part_4[0].trim();
				
				
				
			   
				int value_len=value.length();
				
				
				if ( value.substring(value_len-2).equals("}}")){
					
					
					value= value.substring(0,value_len-2);
				}
			   
				
				complexDeclaration.add(parameter, value);
				
				
				
		}
		//System.out.println(" UMA RODADA");
		
		 extractDeclarationV3(complexDeclaration, 
				 part_3[1]);
		return complexDeclaration;
	}
	*/
	
	/**
	 * extrair declaraçoes em um mappings depois de um atributo (mappings= , cases=, etc)
	 * @param mappings
	 * @return
	 */
	public static String[] extrairDMLDeclarations(String original_mappings) {
	
		String[] lista= new String[1000];
		int cursor=0;
		String mappings=original_mappings.trim();
		
		String temp=mappings.trim();
		int length=mappings.length();
		int index=0;
		
		while(temp!=null && temp.length()>=2&& temp.substring(0, 2).equals("{{")){
			
			String str = extrairDMLDeclaration(temp);
			
			index+=str.length();
			
			lista[cursor++]=str;
			
			//enquanto nao tiver uma { avançar==> implica que estas declarações sao ultimas 
			//Principalmente há espaços em branco, tab entre  declarações
			while(index<length && mappings.charAt(index)!='{'){
				index++;
			}
			//declarações restantes
			temp=mappings.substring(index);
		
			
		}
		
		//reduzir o array
		String[] arr= new String[cursor];
		
		for (int i=0; i<cursor; i++){
			arr[i]=lista[i];
		}
		
		return arr;
	}
	
	/**
	 * Remover caracteres vazios antes de listas de declarações {{}}
	 * @param mappings
	 * @return
	 */
	public static String normalizarDMLDeclarations(String original_mappings) {
		
		String mappings=original_mappings;
		if(mappings==null){
			return null;
		}
		
		if(!mappings.contains("{{")){
			return mappings;
		}
		mappings=original_mappings.replace("\n", "").trim();
		//Preparar o retorno
		String new_mappings="";
		
		String temp=mappings; 
		int length=mappings.length();
		int index=0;
		while(temp!=null && temp.length()>=2&& temp.substring(0, 2).equals("{{")){
			String str = extrairDMLDeclaration(temp);
			
			new_mappings+=str;
			
			//ajustando o index
			int str_len=str.length();
			index+=str_len;
			
			//remover espaço em branco
			int i=index;
			
			while(i<length){
				
				if(mappings.charAt(i)!=' '){
					break;
				}
				//andar para remover
				i++;
			}
			//novo index
			index=i;
			
			temp=mappings.substring(index);
		}
		
		//Sobrou algo? Se sim, acrescentar...
		if(index<mappings.length()){
			new_mappings+=mappings.substring(index);
		}
		
		return new_mappings;
	}
	
	
	/**
	 * Remover caracteres vazios antes de listas de declarações {{}}
	 * @param mappings
	 * @return
	 */
	public static String normalizarMappingDML(String original_mappings) {
		
		
		String mappings= original_mappings;
		
		if(mappings==null){
			return null;
		}
		
		mappings=mappings.replace("\n","").trim();
		
		
		//Preparar o retorno
		String new_mappings="";
		
		String temp=mappings; //.trim(); deve vir com trim
		int length=mappings.length();
		int index=0;
		while(temp!=null && temp.length()>=2&& temp.substring(0, 2).equals("{{")){
			String str = extrairDMLDeclaration(temp);
			
			new_mappings+=str;
			
			//ajustando o index
			int str_len=str.length();
			index+=str_len;
			
			//remover espaço em branco
			int i=index;
			
			while(i<length){
				
				if(mappings.charAt(i)!=' '){
					break;
				}
				//andar para remover
				i++;
			}
			//novo index
			index=i;
			
			temp=mappings.substring(index);
		}
		
		//Sobrou algo? Se sim, acrescentar...
		if(index<mappings.length()){
			new_mappings+=mappings.substring(index);
		}
		
		return new_mappings;
	}
	
	/**
	 * Remover comentários
	 * @param mappings
	 * @return
	 */
	private String removerCommentarios(String mappings) {
		
		if(mappings==null){
			return null;
		}
		
		if(!mappings.contains("<!--")){
			return mappings;
		}
		
		//Preparar o retorno
		String new_mappings="";
		
		String temp=mappings; //.trim(); deve vir com trim
		
		int index=0;
		int length=2;
		while(temp!=null && length==2){
			
			String[] tokens= temp.split("[<][!][-][-]",2);
			
			length=tokens.length;
			//armazenar
			new_mappings+=tokens[0];
			
			if(length==2){
				
				//System.out.println("SIZE ==2 , tokens0 ="+ tokens[0]+"\n\n ");
				
				String[] tokens2 = tokens[1].split("[-][-][>]",2);
				
				temp=tokens2[1];
				
				//System.out.println("tokens2_1 ="+ tokens2[1]+"\n\n ");
				
			} 
			
		}
		
		
		return new_mappings;
	}
	
	/**
	 * Obter conteúdo do arquivo
	 * @param filename
	 */
	
	private static String obterConteudo(String filename){
		
		StringBuffer content = new StringBuffer("");
		
		try {     
												
			BufferedReader br = new BufferedReader(
					  new InputStreamReader(new FileInputStream(filename), "UTF-8"));
		       
			   
			String line="";
				
			while (br.ready() && line!=null) {
					
					line= br.readLine();
					content.append(line);
					
			}
				
			br.close();
	
		} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
			e.printStackTrace();														
		} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();														
		} 		
		
		return content.toString();
	}
}
