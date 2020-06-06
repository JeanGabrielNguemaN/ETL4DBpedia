package br.ufrj.ppgi.greco.kettle.dbpedia.mappings.parser;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter.ComplexDeclaration;
//import org.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter.ComplexDeclaration;
import br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter.ConditionDeclaration;
import br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter.ConditionalMappingDeclaration;
import br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter.Declaration;
import br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter.PropertyMappingDeclaration;
import br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter.TemplateMappingDeclaration;
import br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter.Utilities;

public class MappingParser {

	private static MappingParser mappingParser=null;
    private MappingParser(){
    }
    
	public static MappingParser  getInstance(){
		if (mappingParser==null){
			mappingParser=new MappingParser();
		}
		return mappingParser;
	};
	
	
	/** extrair declaracao dml apos mappings= por exemplo, ou cases=
	 * @param mappings
	 */
	private String extrairDMLDeclaration(String mappings) {
		
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

	/**
	 * Extrair a proxima declaração em uma mapping
	 * @param declaration_in
	 * @param mappingsDML
	 * @return
	 */
	//private ComplexDeclaration extractDeclaration(ComplexDeclaration  declaration, 
	//		String mappingsDML) {
	
	private Declaration extractDeclaration(Declaration  declaration_in, 
				String mappingsDML) {
		
		Declaration declaration= declaration_in;
		
		if(mappingsDML==null || mappingsDML.trim().equals("")){
			return declaration_in;
		}
		
		String mappings = mappingsDML.trim();
		
		if(mappingsDML.charAt(0)=='}'){
				
			return declaration_in;
		}
		
		String restOfMapping=mappings;//mappingsDML.trim();
		
		//Nova declaração?
		//(mappings.substring(0,2).equals("{{")){
		if((mappings.substring(0,2).equals("{{"))
				|| (mappings.charAt(1)=='{' && mappings.charAt(2)=='{')){
			
			//System.out.println("DENTRO IF ="+mappings.substring(0,2)+"***");
			
			//divir em 2 tokens
			String[] tokens=mappings.trim().split("[|]",2);
			
			String term=tokens[0].trim().split("[{][{]")[1].trim();
			
			restOfMapping= "|"+tokens[1].trim();		
		
			term= term.trim();
			
			//Instanciar a declaração em funçao do termo
			if(term.equals("TemplateMapping")){
				
				declaration=new TemplateMappingDeclaration(term);	
				System.out.println("TemplateMapping created");
				
			} else if(term.equals("ConditionalMapping")){
				
				declaration=new ConditionalMappingDeclaration(term);
				System.out.println("ConditionalMapping created");
				
			}else if(term.equals("Condition")){
				
				declaration=new ConditionDeclaration(term);
				System.out.println("Condition created");
				
			}else if(term.equals("PropertyMapping")){
				
				declaration=new PropertyMappingDeclaration(term);
				//System.out.println("PropertyMapping created");
				
			}else {
			 declaration=new ComplexDeclaration(term.trim());
			}
			
	    }// {{
		
		//extrair parametro
		restOfMapping= restOfMapping.trim();
		
		//proxima sequencia é parametro?
		if(restOfMapping.charAt(0)=='|'){
			
			String[] tokens=restOfMapping.split("=",2);
			//parametro
			String parameter=tokens[0].replace("|","").trim();
			
			String valueAndRestOfMappings = tokens[1].trim();
			
			//System.out.println("Parameter="+parameter);
			
			//valores e restos dos mapeamentos
			ValueAndDMLMappings valueAndDMLMappings = extracvalueAndDMLMappings(valueAndRestOfMappings);
			
			String stringValue=valueAndDMLMappings.getValue().trim();
			
			String stringRestOfMapping=valueAndDMLMappings.getRestOfDMLMappings().trim();
			
			//System.out.println("Value ="+ stringValue);
			
			//valor são declarações ou string?
			if(stringValue.trim().charAt(0)=='{'){
				//System.out.println("Rest ="+ stringRestOfMapping);
				
				//#################################
				//System.out.println("valor é conjunto de declarações");
				List<Declaration> declarations= new ArrayList<Declaration>();

				String[] arr = extrairDMLDeclarations(valueAndRestOfMappings);
				
				for (String dmlDecl: arr){
					
					//ComplexDeclaration cdeclaration = 
					Declaration cdeclaration = 
							extractDeclaration(null, dmlDecl);
							
					declarations.add(cdeclaration);
				}
				
				declaration.add(parameter, declarations);
				
				//###############################
				mappings=stringRestOfMapping;
				declaration=extractDeclaration(declaration, mappings);
				
				//extractDeclarationV3(complexDeclaration, mappings);
				
			}else{ //valor é String
				
				declaration.add(parameter, stringValue);
				//System.out.println("IMPRINIDO >>>");
				//complexDeclaration.print();
				
				mappings=stringRestOfMapping;
				declaration=extractDeclaration(declaration, mappings);
				
				//extractDeclarationV3(complexDeclaration, mappings);
						
			}
		}
			
		return declaration;
	}
	
	/**
	 * extrair declaraçoes em um mappings depois de um atributo (mappings= , cases=, etc)
	 * @param mappings
	 * @return
	 */
	private String[] extrairDMLDeclarations(String strDeclarationsList) {
	
		String[] lista= new String[1000];
		int cursor=0;
		String mappings=strDeclarationsList.trim();
		
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
			//while(index<length && 
			//		(mappings.charAt(index)==' ' || mappings.charAt(index)=='\t')){
			//	index++;
				
			}
			//declarações restantes
			temp=mappings.substring(index);	
		}
		
		//reduzir o array
		String[] arr= new String[cursor];
		
		String buff = "";
		for (int i=0; i<cursor; i++){
			arr[i]=lista[i];
			buff+=lista[i];
		}
		
		return arr;
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
		
		//int index=0;
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
	 * Gerar mapeamentos através de declaração
	 * @param nomeTemplate
	 * @return
	 */	
	public Declaration gerarMapeamentos(String nomeTemplate) {
		
		if(nomeTemplate==null){
			return null;
		}
		
		String mappings= Utilities.extractDMLMappings(nomeTemplate);
		
		//Limpeza: remover novas linhas e espaços finais/iniciais
		mappings = cleanMappings(mappings);
		
		//Gerar a declaração
		//ComplexDeclaration  complexDeclaration=extractDeclaration(null, mappings);
		
		Declaration  complexDeclaration=extractDeclaration(null, mappings);

		return complexDeclaration;
	}

	
	/**
	 * Gerar mapeamentos através de declaração
	 * @param nomeTemplate
	 * @return
	 */
	public Declaration gerarMapeamentos(String nomeTemplate, String mappings) {
		
		if(nomeTemplate==null || mappings==null){
			return null;
		}
		
		//Limpeza: remover novas linhas e espaços finais/iniciais
		mappings = cleanMappings(mappings);
		
		//System.out.println("MAPPINGS ="+ mappings);
		
		//Gerar a declaração
		//ComplexDeclaration  complexDeclaration=extractDeclaration(null, mappings);
		
		Declaration  declaration=extractDeclaration(null, mappings);

		return declaration;
	}

	/**Limpeza: remoção de comentarios e novas linhas e trim
	 * @param mappings
	 * @return
	 */
	public String cleanMappings(String mappings) {
		//limpeza
		mappings=mappings.replace("\n", "").trim();
		
		//remover Comentários
		mappings= removerCommentarios(mappings).trim();
		return mappings;
	}

	/**
	 * Extração de valor de um parametro de declaração e resto do mappings
	 * @param valueAndRestOfMappings
	 * @return
	 */
	private ValueAndDMLMappings extracvalueAndDMLMappings(String valueAndRestOfMappings) {
		
		ValueAndDMLMappings valueAndDMLMappings=null;
		
		if(valueAndRestOfMappings==null){
			return null;
		}
		
		String stringValue="";
		String stringRestOfMapping="";
		String mappings =valueAndRestOfMappings.trim();
		String temp=mappings;
		//int length=mappings.length();
		int index=0;
		int cursor=0;
		
		//value são declarações?
		if(mappings.charAt(0)=='{'){
			
			String[] lista= new String[1000];
			String str="--";
			
			int j=0;
			while(mappings.charAt(0)=='{'){
				
				str = extrairDMLDeclaration(mappings);
				
				if(!str.equals("")){
					
					//
					mappings=mappings.substring(str.length()).trim();
					
					stringValue+=str;
				} 
				
				//System.out.println(++j+" Declaraton ="+str);
				
				
			} //while
			stringRestOfMapping= mappings.trim();
		}else{ 	//value é string 
			//o que ocorre primeiro, | ou }--> interromper
			
			
			String str="";
			
			index=0;
			
			while (index<mappings.length() &&
					(mappings.charAt(index)!='}' || mappings.charAt(index)!='|')){
				
				if(mappings.charAt(index)=='}' || mappings.charAt(index)=='|'){
					break;
				}
				stringValue+= mappings.charAt(index);
				//System.out.println(mappings.charAt(index));
			
				index++;
				
			} //while
			
			stringRestOfMapping=mappings.substring(index).trim();	
		}
		
		valueAndDMLMappings= new ValueAndDMLMappings(stringValue,stringRestOfMapping);
		
		return valueAndDMLMappings;
	}
}
