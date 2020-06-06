package br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter.demo;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter.ComplexDeclaration;
import br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter.Declaration;
import br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter.Utilities;

public class ParserDemo2 {

	public static void main(String[] args) {
		
		System.out.println("BEGIN ...\n");
		
		gerarMapeamentos("Info/Taxonomia");
		
		System.out.println("\n\nEND ...");			

	}

	/**
	 * 
	 */
	public static void testesMain() {
		String mappings=null;
		/*
		mappings = "{{TemplateMapping "
		       + "\n| mapToClass = Actor"
				+ "\n| mappings =   {{PropertyMapping"
				+ "\n| ontologyProperty = foaf:name"
				+"\n<!-- {{PropertyMapping | templateProperty = imagem_legenda | ontologyProperty = caption }} -->"
				+ "\n| templateProperty = nome"
				+ "\n}}     \n"
				+ "\n{{PropertyMapping"
				+"\n<!-- {{PropertyMapping | templateProperty = imagem_legenda | ontologyProperty = caption }} -->"
				+"\n<!-- {{PropertyMapping | templateProperty = imagem_legenda | ontologyProperty = caption }} -->"
				
				+ "\n| ontologyProperty = birthDate"
				+ "\n| templateProperty = nascimento_data"
				+ "\n}}"
				+"\n<!-- {{PropertyMapping | templateProperty = imagem_legenda | ontologyProperty = caption }} -->"
				+ "\n | mapToClass2 = Actor2"
				+ "\n}}";
		*/
		/*
		String mappings = "{{TemplateMapping "
				+ "| mapToClass = Actor"
				+ "| mapToClass2 = Actor2"
				+ "}}";
		*/
	}

	/**
	 * @param mappings
	 */
	public static void testExtractColeçãoDeclaraçoes(String mappings) {
		
		String[] arr = extrairDMLDeclarations(mappings.replace("\n", ""));
		
		for(String str: arr){
			System.out.println(str);
		}
	}

	/**
	 * @param mappings
	 */
	public static void gerarMapeamentos(String nomeTemplate) {
		
		
		String mappings= Utilities.extractDMLMappings(nomeTemplate);
		
		//mappings= mappings.substring(56);
		mappings=mappings.replace("\n", "").trim();
		
		mappings=normalizarDMLDeclarations(mappings);
		
		ComplexDeclaration  mappingsAtor=null;  
		
		System.out.println("\n\nExtraindo declarações..................... \n");
			
		mappingsAtor=extractDeclarationV3(mappingsAtor, mappings);
		
	    System.out.println("\nImprimir ...............\n");
		mappingsAtor.print();
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
		/*System.out.println("Qde_fechadas = "+qde_fechadas);
		System.out.println("Qde_abertas = "+qde_abertas);
		System.out.println("Index = "+i);
		System.out.println("STR = "+str);
		System.out.println("STR Lenght = "+str.length());
		*/
		return str;
	}

	/**
	 * @param mappings
	 * @param first_bar_ver
	 * @return 
	 */
	public static ComplexDeclaration extractDeclaration(ComplexDeclaration  declaration, 
			String mappings, boolean first_bar_ver) {
		
		String[] tokens=mappings.trim().split("[|]",2);
		String part_1=tokens[0];
		ComplexDeclaration  complexDeclaration=declaration;  
		
		if(first_bar_ver){
			//extrair declaração
			String term=part_1.trim().split("[{][{]")[1];
			
			System.out.println("Term ="+term );
			first_bar_ver=false;
			
			complexDeclaration=new ComplexDeclaration(term);
		}
		
		//segunda parte
		String part_2=tokens[1]; 
		//remover }} do fim da declaração
		part_2= part_2.trim().substring(0,part_2.trim().length()-2);
		//tokens[1].substring(0,part_3[1].length()-2);
			
		String[] part_3 = part_2.split("=", 2);
		
		String parameter=part_3[0].trim();
		
		//String parameter=part_2.split("=", 2)[0].trim();
			
		System.out.println("Parameter="+parameter);
			
		//extrair valor: part_3[1]
		System.out.println("Para valor ="+part_3[1]);
				
		//valor é conjunto de declarações
		if (part_3[1].trim().substring(0, 2).equals("{{")){
			
			System.out.println("valor é conjunto de declarações");
			
			List<Declaration> declarations= new ArrayList<Declaration>();
				
			//String valor = part_3[1].substring(0,part_3[1].length()-2);
			
			
			String dml_declaracao = extrairDMLDeclaration(part_3[1].trim());
			
			
			//extrair declarações
			
			complexDeclaration.add(parameter, declarations);
		}
		else { //valor é string
				System.out.println("valor é string");
				
				String[] part_4 = part_3[1].split("[|]");
				
				String value = part_4[0];
			
				System.out.println(value);
				
				complexDeclaration.add(parameter, value);
				
		}
		
		return complexDeclaration;
	}
	
	
	
	/**
	 * @param mappings
	 * @param first_bar_ver
	 * @return 
	 */
	public static ComplexDeclaration extractDeclarationV2(ComplexDeclaration  declaration, 
			String mappings, boolean first_bar_ver) {
		
		
		if(mappings==null){
			return null;
		}
		
		//divir em 2 tokens
		String[] tokens=mappings.trim().split("[|]",2);
		String part_1=tokens[0];
		
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
			first_bar_ver=false;
			
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
				String[] arr = extrairDMLDeclarations(part_3[1].trim());
				
				for (String dmlDecl: arr){
					
					System.out.println("DML ="+ dmlDecl);
					ComplexDeclaration cdeclaration = 
							extractDeclarationV2(null, dmlDecl, false);
					declarations.add(cdeclaration);
				}
				
				complexDeclaration.add(parameter, declarations);
				
		}
		else { //valor é string
				//System.out.println("valor é string");
				
				String[] part_4 = part_3[1].split("[|]");
				
				String value = part_4[0];
			
				//System.out.println("VALOR = "+value);
				
				complexDeclaration.add(parameter, value);
				
		}
		//System.out.println(" UMA RODADA");
		
		 extractDeclarationV2(complexDeclaration, 
				 part_3[1], false);
		return complexDeclaration;
	}
	
	
	public static ComplexDeclaration extractDeclarationV3(ComplexDeclaration  declaration, 
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
					/*
					System.out.println("-----------");
					System.out.println("SIZE ="+value_len);
					System.out.println("VALUE ="+value);
					System.out.println("value.substring(value_len-2) ="+value.substring(value_len-2)+"#");
					System.out.println("-----------");
				    */
					
					value= value.substring(0,value_len-2);
				}
			   
				
				complexDeclaration.add(parameter, value);
				
				
				
		}
		//System.out.println(" UMA RODADA");
		
		 extractDeclarationV3(complexDeclaration, 
				 part_3[1]);
		return complexDeclaration;
	}
	
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
	public static String removerCommentarios(String mappings) {
		
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
		

}
