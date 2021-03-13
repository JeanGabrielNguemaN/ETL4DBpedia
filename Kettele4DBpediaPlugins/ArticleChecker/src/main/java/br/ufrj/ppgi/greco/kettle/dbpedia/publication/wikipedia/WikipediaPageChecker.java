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
package br.ufrj.ppgi.greco.kettle.dbpedia.publication.wikipedia;

import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import br.ufrj.ppgi.greco.dbpedia.wikipedia.WikipediaRequestHandler ;

/**
 * Verifica se um artigo existe na Wikipedia
 * @author Jean Gabriel Nguema
 *
 */
public class WikipediaPageChecker {

	
	 WikipediaRequestHandler  wikipediaRequestHandler=null;
	 
	 public WikipediaPageChecker(){
		 wikipediaRequestHandler= new WikipediaRequestHandler ();
	 }
	 
	//precisar mudar
	private String[] templateProperties=null;
	/**
	 * Checar se a entidade possui um artigo na wikipedia
	 * @param entity
	 * @param infoboxName
	 * @return
	 */
	public CheckResult checarEntidadePorInfobox(Entity entity, String infoboxName) {
		
		CheckResult checkResult=null;
		
		//CASO 1:Ver se existe um artigo com título da planta
		boolean tituloFound=false;
		
		boolean pageFound=existeArtigoComTituloV2(entity, infoboxName);
		
		//CASO 1.1:Sem conexao de internet
		
		if(wikipediaRequestHandler.isUnknownHostException()) {
					
			return new CheckResult(null, false, false);
					
		}
		
		//Página ainda nao foi achada
		Page page=null;
		
		checkResult= new CheckResult(page, pageFound, tituloFound);
		
		if(pageFound){
			
			tituloFound=true; 
			
			String titulo =entity.getTitle();
			
			page=new Page(titulo);
			
			//criar um novo resultado da verificação
			checkResult= new CheckResult(page, pageFound, tituloFound);
			
			return checkResult;	 	 
		}
			
		//***************************************************************************************
		//CASO 2: O infobox é nulo, 
		//entao checar pela pesquisa
		//Page page=buscarArtigo(planta);
		//***************************************************************************************
		
		Page[] pages=buscarArtigoV3(entity, infoboxName);
		
			
		//infoboxes não existem
		if(pages==null){
			return checkResult;
		}
			
		//processar cada um infobox ate achar o correto
		for(Page correntPage: pages){
			
			String infobox= correntPage.getInfobox();
	
			//infobox atual é da entidade procurada?
			boolean found=verificaEObterPageDoInfoboxV2_(entity, infobox);
			//Interromper pois achou
			
			if(found){
				
				pageFound = true;
				
				checkResult=new CheckResult(correntPage, pageFound, tituloFound);

				return checkResult;
			}	
		}//for
			
		return checkResult;
	}

	private boolean existeArtigoComTituloV2(Entity entity, String tamplateName){
		boolean pageFound=false;
			
		if(entity.getTitle()==null){
				return false;
		}
		
		String titulo= entity.getTitle().trim();
		
		//obter a primeira seção
		String firstSection =  wikipediaRequestHandler.getFirstSection( titulo );

		
		if(firstSection==null) {
			return false;
		}
		pageFound=artigoExisteComTitulo(firstSection);
			
		//artigo com titulo especificado não existe.
		if(!pageFound){
			return false;
		}
			
		//artigo com titulo especificado existe.
		//obter infobox
		String infobox = WikiQueryHandler.getInfoboxBySectionV3(firstSection,tamplateName);
			 
		if(infobox!=null){
				 
				//infobox nao é nulo
				//infobox corresponde à planta?
				//Page page=verificaEObterPageDoInfobox(planta, infobox);
					 
				pageFound=verificaEObterPageDoInfoboxV2_(entity, infobox);
				
			 } //if
			 
			return pageFound;
		}
		

		
	public Page[] buscarArtigoV3(Entity entity, String templateName){
			
			//Page page=null;
		    //String[] infoboxes=null;
		    Page[] pages=null;
			
			//Procurar artigo
			String jsonString=wikipediaRequestHandler.searchArticle(entity.getTitle());
			
			pages=getInfoboxesByResearch(entity, jsonString,templateName);
			
			return pages;
	 }

	
	  
	/**
	 * Dado um infobox e uma planta, verifica se a planta corresponde ao infobox da planta
	 * E retorna a página. Retornando nulo, significa nao corresponder
	 * @param entity
	 * @param infobox
	 * @param pageTitle
	 * @return
	 */
	public boolean verificaEObterPageDoInfoboxV2_(Entity entity, String infobox){
	  
	   
	 HashMap<String, String> selectedPropertiesValuesHashMap=entity.getPropertiesValuesHaspMap(); ///argument--> Vem do step
	   
	  boolean pageFound=false;
	  
	  //EXTRAIR valores dos infoboxes
	  HashMap<String, String> infoboxPropertiesValuesHashMap = extrairValoresDoInfoboxV2(infobox);
	  
	  //System.out.println("SIZE INFO MAP ="+ infoboxPropertiesValuesHashMap.size());
	  
	  if(infoboxPropertiesValuesHashMap==null){
			 return false;
	  }
	  
	 String[] selectedProperties = 
			 selectedPropertiesValuesHashMap.keySet().toArray(new String[0]);
	 
	 String[] infoboxInWikiProperties = 
			 infoboxPropertiesValuesHashMap.keySet().toArray(new String[0]);
	  
	  String[] propertiesValues= 
			  new String[infoboxInWikiProperties.length];
	  
	  for(int i=0; i<propertiesValues.length;i++ ){
		  
		 propertiesValues[i]= 
			infoboxPropertiesValuesHashMap.get(infoboxInWikiProperties[i]);
		 
		 //System.out.println(infoboxInWikiProperties[i]+" ---> "+ propertiesValues[i]);
	  }
	   
	  if(isNull_or_EmptyV2_(selectedProperties)){
		  return false;
	  }
	    
	  for(int i=0; i<selectedProperties.length;i++ ){
		  
		 String propriedade=selectedProperties[i];
		  
		 String  infoboxPropertyValue=infoboxPropertiesValuesHashMap.get(propriedade);
		
		 String  selectedPropertyValue=selectedPropertiesValuesHashMap.get(propriedade);
		
		 if( (infoboxPropertyValue==null || selectedPropertyValue==null )){
			
			 return false;
		
		 }
		
		 //Se não contiver
		 if( ! infoboxPropertyValue.contains(selectedPropertyValue)){
			 //contar
			 return false;
		  }	
	  }
	  
	  return true;
   }

/**Extair valores do infobox
 * @param infobox. Formato: {{Info/Nome ...}}
 */
public HashMap<String, String> extrairValoresDoInfoboxV2(String infobox) {
	 
	HashMap<String, String> propertiesValuesHashMap= null;
	//obter tokens válidos
	String[] infoboxTokens= splitInfoboxByIgual(infobox);
	
	if(infoboxTokens==null){
		return null;
	}
	//
	propertiesValuesHashMap= new HashMap<String, String>();
	
	// extair as propriedades e  seus valores
	for (int i=0; i<(infoboxTokens.length-1); i++){

		// extair a propriedade (propertyAndValue[0]) e seu valor (propertyAndValue[1])
		String[] propertyAndValue = extrairValorPropriedadeV2(infoboxTokens, i);
		
		//armazenar
		propertiesValuesHashMap.put(propertyAndValue[0], propertyAndValue[1]);
 	 }
	
	return propertiesValuesHashMap; 
}

/** Quebrar infobox pelo caractere "="
 * @param infobox
 * @return 
 */
public String[] splitInfoboxByIgual(String infobox) {
	int index=-1;
	 String[] valid_tokens= new String[1000];
	 String temp_token="";
	 
	 //Estrategia: quebrar de duas em duas.
	 //Quebrar em duas partes: tokens[0] e tokens[1]
	 //tokens[0],a primeira parte, deve conter uma propriedade
	 //tokens[2], a segunda o restante do infobox ate aqui
	 
	 if (infobox==null){
		//System.out.println(" NULL Infobox"); 
		return null;
	 }
	 String[] tokens = infobox.split("=",2);
	 
	 //Enquanto existir duas partes
	 while(tokens.length>1){

		 //primeiro token deve conter uma propriedade
		 if(hasTemplateProperty(tokens[0])){
			//ou seja o token[0] é válido 
			valid_tokens[++index]= temp_token+tokens[0];
				
			//esvaziar o token temporario
			//temp_token: serva para junta aqueles tokens que não deveriam ser quebrados.
			//pois às vezes, há o caractere "=" dentro do valor de uma propriedade, 
			//mas o = que é valido é aquele que captura o valor da propriedade.
			temp_token="";
			
		 }else{
			 //devolver o "=": Deve ser depois, pois a divisão  foi tokens[0]+"="+tokens[1] 
			 temp_token+=tokens[0]+"=";
		 }
			 
		 tokens = tokens[1].split("=",2);
		 //último token
		 if(tokens.length==1){
			 //fim do loop, guardar o último token, pois será valor de uma propriedade.
			 valid_tokens[++index]= temp_token+tokens[0];	
		 }
		
	 }// while
	 
	 
	 tokens=null;
	 
	 //armamazenar os tokens validos
	 if(index>=0){
		 tokens= new String[index+1];
		 for (int j=0; j<=index; j++){
			 
			 tokens[j]=valid_tokens[j];
		 }	 
	 }
	 
	return tokens; 
}

	/***
	 * Verifica se o artigo existe com titulo especificado
	 * @param firstSection
	 * @return
	 */
	
	private static  boolean artigoExisteComTitulo(String firstSection) {
		
		if( firstSection==null ){
			return  false;
		}
		
		//String retornado quando o artigo cnão existe com titulo especificado
		String strTituleEnexistente="{\"batchcomplete\":\"\",\"query\":{\"pages\":"
				+ "{\"-1\":{\"ns\":0,\"title\":";
		
		//Se não deve cconter esta string
		return  ! firstSection.contains(strTituleEnexistente);
	}


	/**
	 * Dada uma lista de páginas contidas no resultado da pesquisa (jsonString),
	 * analisa, identifica a página correta, se existir.
	 * Ou seja, retorna o infobox que correspondente á Planta informada.
	 * @param entity
	 * @param jsonString
	* @param infoboxTemplateName 
	 * @return
	 */
	private Page[] getInfoboxesByResearch(Entity entity, String jsonString, String infoboxTemplateName){
			
	   		//String[] infoboxes=null;
		
			Page[] pages=null;
		
			if (jsonString==null){
				return pages;
			}
			String stringToParse=jsonString.trim();
			
			JSONObject parser = new JSONObject(stringToParse);
			
			JSONObject query = new JSONObject(parser.get("query").toString());
			
			JSONArray search = query.getJSONArray("search");
			
			int size=search.length();
			
			pages=new Page[size];
			
			for (int i=0; i<size;i++){
				
				JSONObject pageKey= search.getJSONObject(i);
				
				String pageTitle= pageKey.getString("title");
				
				String pageid_tmp= pageKey.get("pageid").toString();
				
			
				String jsonPageContent=wikipediaRequestHandler.getInfoboxInstanceByPageid(pageid_tmp);
				
				//System.out.println("jsonPageContent = "+jsonPageContent);
				
				//String firstSection=getFirstSection( pageTitle.trim());

				 //obter infobox e armazenar
				 String infobox = WikiQueryHandler.getInfoboxBySectionV3(jsonPageContent,infoboxTemplateName);
				 
				 Page page= new Page(pageTitle, pageid_tmp);
					
				 page.setInfobox(infobox);
				 pages[i]=page;
				 //infoboxes[i]=infobox;
			
			}//for
		
			
			return pages;
		}

/**
 * Nao é nulo se todos nao tiverem nulos
 * @param selectedProperties
 * @return
 */
public static boolean isNull_or_EmptyV2_(String[] selectedProperties) {
	
	if(selectedProperties==null){
		return true;
	}
	
	for(String prop: selectedProperties){
		
		if(prop==null || prop.trim().equals("")){
			return true;
		}
	}
	
		return false;
}

/**
 * Retorna o a propriedade contida no indice do e seu valor.
 * @param infoboxTokens. String[] dos tokens válidos do infobox. 
 * @param index
 * @return. String[2], onde a propriedade fica no [0] e valor no [1]
 */
public String[] extrairValorPropriedadeV2(String[] infoboxTokens, int index) {

	//propriedade fica no propertyAndValue[0] e valor no propertyAndValue[1]
	String[] propertyAndValue= new String[2];
	
	String[] parts= infoboxTokens[index].split("[|]");
	
	//ultimo token pode ser ou nao propriedade
	String lastPart = parts[parts.length-1].trim(); 
	
	//System.out.println("TOKEN"+index+" ="+infoboxTokens[index] );
	
	if(isTemplateProperty(lastPart)){
		//armazena a propridade
		propertyAndValue[0]=lastPart;
		
		//extrair valor
		if(index<=infoboxTokens.length-2){
			//armazena o valor
			propertyAndValue[1]=extrairValorV2(infoboxTokens, index, lastPart);
		}
	}   

	
	return propertyAndValue;
}

	/**
	 * Verifica se a propriedade está contida na string
	 * @param token
	 * @return
	 */
	 private boolean hasTemplateProperty(String token) { 
		 boolean answer=false;
		 
		 for (String prop: getTemplateProperties()){
			 if(hasExactProperty(token,prop))
				 return true;
		 }
		 
		 return answer;
	}
	 
	 
	private boolean hasExactProperty(String token, String prop) {
		
		String similar_prop=prop+"_";
		if(token.contains(similar_prop))
			return false;
		
		if(token.contains(prop))
			return true;
		
		return false;
	}

	
	
	/**
	 * Extrair o valor de uma propriedade de infobox. 
	 * @param infoboxTokens
	 * @param index. int: index do token que contem a propriedade
	 * @param lastPart. propriedade
	 * @return
	 */
	public String extrairValorV2(String[] infoboxTokens, int index, String lastPart) {
		//buffer valor
		String valor_property="";
		
		String[] parts=null;
		
		//exception: deve ser tratato apenas tokens.length-2
		if(index>=(infoboxTokens.length-1)){
			System.out.println("Exeption: deve ser tratato apenas tokens.length-2");
			return null;
		}
		
		//o valor é fica no token seguinte.
		//Então, dividir o token para retirar a próxima propriedade, se existir
		parts= infoboxTokens[index+1].split("[|]");
		
		//potencial propriedade, fica no último token
		String potencialPropriedade= parts[parts.length-1];
		
		// Se nao tiver barra vertical ( ou parts.length==1) ou
		// ultimo parte nao for propriedade de template retorna sem mudar
		// retirando apenas os dois ultimos caracteres
		 
		if (parts.length==1 || !isTemplateProperty(potencialPropriedade.trim()) ){
			
			valor_property=infoboxTokens[index+1].replace("\\n", "");
			
			//System.out.println(lastPart+" = "+valor_property);
			
			return normalizarValor(valor_property);
		}
		
		/*
		 * Chegando aqui: ultimo é propriedade de template, 
		 * então tratar esta situação
		 */
		
		for (int j=0; j < parts.length-1; j++){
			
			if(index!=infoboxTokens.length-1){
				valor_property+=parts[j];
			}
		}
		
		valor_property=valor_property.replace("\\n", "");
		
		return normalizarValor(valor_property);
	}
	
	/**
	 * Remover última chaves fechadas no valor da propriedades:
	 * sstas 2 últimas chaves correspondem à declaração de infobox
	 * @param propertyValue
	 * @return Valor da propriedade sem as duas últimas chaves.
	 */
	private String normalizarValor(String propertyValue) {
		//não contem chaves fechadas, então tudo ok
		if(!propertyValue.contains("}}")){
			return propertyValue;
		}
		
		// quantidade de chaves chaves_abertas igual a quantidade de chaves fechadas
		//ok então
		
		if(contarQuantidadeCharacterNaString(propertyValue, "{")==contarQuantidadeCharacterNaString(propertyValue, "}")){
			return propertyValue;
		}
		
		String[] tokens_por_chaves_fechadas=propertyValue.split("[}][}]");
		//chaves fechadas no final, sem nada depois.
		//nesse caso, remover as 2 chaves fechadas
		if(tokens_por_chaves_fechadas.length==1 
				&& contarQuantidadeCharacterNaString(tokens_por_chaves_fechadas[0], "{")==contarQuantidadeCharacterNaString(tokens_por_chaves_fechadas[0], "}")){
		
			
			return tokens_por_chaves_fechadas[0];
		}
		
		//Outros casos: Contem várias chaves fechadas
		//Então, remover apenas as 2 últimas chaves fechadas
		
		String newPropertyValue="";
		//Quando não há espaço entre sequencias de chaves duplas ex"}}}}", é contado apenas uma vez
		// por isso este tratamento
		/*
		if (tokens_por_chaves_fechadas[0].contains("{{") && tokens_por_chaves_fechadas.length==1){
			
			//a quantidade de fechadadas deve ser a quantidade de abertas
			int num_duplas_abertas=contarQuantidadeCharacterNaString(tokens_por_chaves_fechadas[0], "{")/2;
			
			newPropertyValue=tokens_por_chaves_fechadas[0];
		
			for (int i=0; i<num_duplas_abertas;i++){
				newPropertyValue+="}}";
			}
			
			return newPropertyValue;
		}
		*/
	
		for (int j=0; j < tokens_por_chaves_fechadas.length; j++){
			//usada para fechar ou não chaves
			String fechar="";
		
			//Não fechar o último, mas fechar os demais
			if(j==tokens_por_chaves_fechadas.length-1){
				//último token, entao não fechar
				fechar="";
			}
			else{
				fechar="}}"; 
			}
			newPropertyValue+=tokens_por_chaves_fechadas[j]+fechar;
			//System.out.println(" newPropertyValue parcial= "+newPropertyValue);
		}
		
		//a quantidade de fechadadas deve ser a quantidade de abertas
		int qde_abertas=contarQuantidadeCharacterNaString(newPropertyValue, "{");
		int qde_fechadas=contarQuantidadeCharacterNaString(newPropertyValue, "}");
		
		int num_duplas_abertas=(qde_abertas-qde_fechadas)/2;
		System.out.println("");
	    //Acrescentar o que falta
		for (int i=0; i<num_duplas_abertas;i++){
			newPropertyValue+="}}";
		}
		
		return newPropertyValue.replace("\n", "");
	}
	
	
	/**
	  * Contar a quantidade de char em uma string
	  * @param str
	  * @param char_
	  * @return
	  */
	public int contarQuantidadeCharacterNaString(String str,String char_){
		int num=0;
		String newStr=str.replace(char_, "");
		
		num=str.length()-newStr.length();
		return num;
	}
	
	private boolean isTemplateProperty(String token){
		 boolean answer=false;
		 
		 for (String prop: getTemplateProperties()){
			 if(token.equals(prop))
				 return true;
		 }
		 return answer;
	 }

	
	public String[] getTemplateProperties() {
		return templateProperties;
	}

	public void setTemplateProperties(String[] templateProperties) {
		this.templateProperties = templateProperties;
	}
}