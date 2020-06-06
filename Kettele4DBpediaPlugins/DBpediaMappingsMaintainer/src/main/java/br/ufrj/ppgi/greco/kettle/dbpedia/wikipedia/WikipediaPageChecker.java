package br.ufrj.ppgi.greco.kettle.dbpedia.wikipedia;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Antigo Demo sem main
 * @author Jean Gabriel Nguema
 *
 */
public class WikipediaPageChecker {

	//precisar mudar
	private String[] templateProperties=null;
	/**
	 * Checar se a entidade possui um arquivo na wikipedia
	 * @param entity
	 * @param infoboxName
	 * @return
	 */
	public CheckResult checarEntidadePorInfobox(Entity entity, String infoboxName) {
		
		//página da wikipedia da planta
			 
		String genero=entity.getPropertiesValuesHaspMap().get("género");
		String familia=entity.getPropertiesValuesHaspMap().get("família");
		String espece=entity.getPropertiesValuesHaspMap().get("espécie");
		
		System.out.println("Nome: "+entity.getTitle()+"   Familia: "+familia+
					 "   Genero: "+genero+"   Especie: "+espece);
		
		
		 //***************************************************************************************
		//CASO 1:Ver se existe um artigo com título da planta
		
		boolean tituloFound=false;
		
		boolean pageFound=existeArtigoComTituloV2(entity, infoboxName);
		
		//Página ainda nao foi achada
		Page page=null;
		
		CheckResult checkResult= new CheckResult(page, pageFound, tituloFound);
		
		if(pageFound){
			
			tituloFound=pageFound; 
			
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
			String firstSection = getFirstSection( titulo );

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
		
	  /**
		 * Obter a primeira seção do artigo pelo título
		 * @param articleTitle
		 * @return
		 */
		public static String getFirstSection(String articleTitle){
			
			//String url= "https://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop="
			//		+ "content&format=xmlfm&titles=Barack%20Obama&rvsection=0";
			
			//return  executeGet(url);
			
			//https://en.wikipedia.org/w/api.php?action=query&prop=revisions&meta=siteinfo&titles=Main%20Page&rvprop=user%7Ccomment&continue=

			String url="";
			
			if(articleTitle==null || articleTitle.equals("")){
				return null;
			}
			
			String urlPrefix="https://pt.wikipedia.org/w/api.php?action=query&"
										+ "prop=revisions&rvprop=content&format=json&titles=";
								
			String[] tokens= articleTitle.split(" ");
								
			String titles="";
				
		
			for (int i=0; i<tokens.length;i++){
					titles+=tokens[i];
					
					//espaço apenas se não for último
					if(i!=(tokens.length-1)){
						titles+="%20";
					}
			}
			 //
	
			url+=urlPrefix+titles+"&rvsection=0&utf8";
									
			
			//System.out.println("URL : "+ url);
			return  executeGet(url);		
		
		  }
		
public static Page[] buscarArtigoV3(Entity entity, String templateName){
			
			//Page page=null;
		    //String[] infoboxes=null;
		    Page[] pages=null;
			
			//Procurar artigo
			String jsonString=searchArticle(entity.getTitle());
			
			pages=getInfoboxesByResearch(entity, jsonString,templateName);
			
			return pages;
	 }

	/**
	 * Busca se o article existe a partir do título
	 * @param targetTitle
	 * @return
	 */
	
	public static String searchArticle(String targetTitle){
		
		
		String url="";
		if(targetTitle!=null){
			String urlPrefix="https://pt.wikipedia.org/w/api.php?action=query&list=search&srsearch=";
					
			//Formatar titulo da url
			String[] tokens= targetTitle.split(" ");
							
			String titles="";
			
			for (int i=0; i<tokens.length;i++){
				titles+=tokens[i];
				
				//espaço apenas se não for último
				if(i!=(tokens.length-1)){
					titles+="%20";
				}
			}
			url+=urlPrefix+titles+"&utf8=&format=json";
								
		}else{
			return null;
		}
	
		return  executeGet(url);		
	
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
	 
	 
	 //for (String prop: infoboxInWikiProperties){
	//	 System.out.println("Prop :"+ prop);
	// }
	 
	 /* String nome=infoboxPropertiesValuesHashMap.get("nome");
	  String familia=infoboxPropertiesValuesHashMap.get("família");
	  String genero=infoboxPropertiesValuesHashMap.get("género");
	  String especie=infoboxPropertiesValuesHashMap.get("espécie");
	  */
	  
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
	 
	  
	 // System.out.println("Infobox species= "+especie);
	  
	  /*
	  if( planta.getFamilia()!=null && planta.getGenero()!=null && planta.getEspecie()!=null  &&
			         familia.contains(planta.getFamilia()) 
			        && genero.contains(planta.getGenero())
			        && especie.toLowerCase().contains(planta.getEspecie().toLowerCase())){
		*/
	 /*
	  if(familia.contains(planta.getFamilia()) 
		        && genero.contains(planta.getGenero())
		        && especie.toLowerCase().contains(planta.getEspecie().toLowerCase())){
	  
		 
		  //System.out.println("Infobox Familia= "+familia);
		  //System.out.println("Planta Familia= "+planta.getFamilia());
		  //System.out.println("Infobox genero= "+genero);
		  //System.out.println("Planta genero= "+planta.getGenero());
		  //System.out.println("Infobox species= "+especie);
		  //System.out.println("Planta species= "+planta.getEspecie());
		  
		  //page= new Page();
		  //page.setTitle(nome);
		  
		  pageFound= true;
		  
		 
	  }
	 */
	  
	 //System.out.println("SIZE ="+propertiesValues.length);
	  
	 for(int i=0; i<selectedProperties.length;i++ ){
		  
		 String propriedade=selectedProperties[i];
		 
		 //System.out.println(i+" - ITEM ="+propertiesValues[i]);
		 
		 
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
	  
	  //System.out.println("----------------------CHEGOU AQUI ------------------");
	 // System.out.println("----------------------CHEGOU AQUI ------------------"+page);
	  pageFound=true;
	  return pageFound;
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
	String strTituleEnexistente="{\"batchcomplete\":\"\",\"query\":{\"pages\":{\"-1\":{\"ns\":0,\"title\":";
	
	//Se não deve cconter esta string
	return  ! firstSection.contains(strTituleEnexistente);
}

		
/**
 * Submeter request para a API.
 * @param urlString
 * @return
 */
public static String executeGet(String urlString) {
		    URL url=null;
		    StringBuffer content= new StringBuffer("");
		    
			try {
				//garantir que o formato de encoding da resposta: utf8
				url = new URL(urlString+"&utf8");
			
				HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
				
				BufferedReader in = new BufferedReader(
                       new InputStreamReader(
                       		connection.getInputStream()));

				String inputLine;

				while ((inputLine = in.readLine()) != null) {
					//System.out.println(inputLine);
					content.append(inputLine);
				}

				in.close();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return content.toString();
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
	static Page[] getInfoboxesByResearch(Entity entity, String jsonString, String infoboxTemplateName){
			
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
				
			
				String jsonPageContent=getInfoboxInstanceByPageid(pageid_tmp);
				
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
		
		//System.out.println("Last part ("+lastPart+ ")  is property");
		
		//extrair valor
		if(index<=infoboxTokens.length-2){
			//armazena o valor
			propertyAndValue[1]=extrairValorV2(infoboxTokens, index, lastPart);
		}
	 	
		
	}   
	//else{
       //System.out.println(index+"-     "+lastPart+ "  is not property");
	//}
	
	return propertyAndValue;
}

/**
 * Verifica se a propriedade está contida na string
 * @param token
 * @return
 */
 private boolean hasTemplateProperty(String token) {
	// TODO Auto-generated method stub
	 
	 boolean answer=false;
	 
	 for (String prop: getTemplateProperties()){
		 if(hasExactProperty(token,prop))
			 return true;
	 }
	 
	 return answer;
}
 
	private static boolean hasExactProperty(String token, String prop) {
		
		String similar_prop=prop+"_";
		if(token.contains(similar_prop))
			return false;
		
		if(token.contains(prop))
			return true;
		
		return false;
	}

	public static String getInfoboxInstanceByPageid(String id){
		
		String url="";
		
		if(id!=null){
			String urlPrefix="https://pt.wikipedia.org/w/api.php?action=query&"
					+ "prop=revisions&rvprop=content&pageids="+id;

			url+=urlPrefix+"&rvsection=0&format=json&utf8";
								
		}else{
			return null;
		}
		//System.out.println("URL : "+ url);
		return  executeGet(url);		
	
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