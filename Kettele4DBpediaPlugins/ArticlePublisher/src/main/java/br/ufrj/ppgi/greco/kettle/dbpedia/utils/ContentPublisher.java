package br.ufrj.ppgi.greco.kettle.dbpedia.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import br.ufrj.ppgi.greco.dbpedia.wikipedia.WikipediaRequestHandler ;
import br.ufrj.ppgi.greco.kettle.dbpedia.wikipedia.WikiQueryHandler;

public class ContentPublisher {
	
	/**
	  * Obter o token em função do tipo
	  * @param type
	  * @return
	  */
	
	private String csrftoken=null;
	private String lgtoken = null;
	private String endPoint=null;
	private String lgname=null;
	private String lgpassword=null;
	
	private Header[] headers;
	private boolean isLogin;
	private HashMap<String, String> cookies;
	private boolean first=true;

	private Date lastEdit=null;
	private boolean createOnly=true;
	
	private WikipediaRequestHandler    wikipediaRequestHandler ;
	 
	 
	
	 public boolean isCreateOnly() {
		return createOnly;
	}

	 public ContentPublisher() {
		 wikipediaRequestHandler = new WikipediaRequestHandler ();
	 }

	private String getToken(String endPoint, String type) {
		
		 String url=null;
		 String token=null;	
		 
		 if(!(type==null || type.equals(""))) {
			
			 url=endPoint
			 		+ "?action=query&meta=tokens"
				 		+ "&type="+type
				 		+ "&format=json";
			 
			 String jsonResponse=wikipediaRequestHandler .executeGet(url);
			 
			 if(wikipediaRequestHandler .isUnknownHostException() || jsonResponse==null) {
				 return null;
			 }
			 
				//recuperar o conteúdo da chave "query"
			 JSONObject batchcomplete = new JSONObject(jsonResponse);
				
			 token=batchcomplete.getJSONObject("query").getJSONObject("tokens").get(type.trim()+"token").toString();
			 
			 return token;
	     }
		 
		 url=endPoint+ "?action=query&meta=tokens&format=json";
		 
		 String jsonResponse=wikipediaRequestHandler .executeGet(url);
		 
		 if(wikipediaRequestHandler .isUnknownHostException() || jsonResponse==null) {
			 return null;
		 }
		 
			//recuperar o conteúdo da chave "query"
		 JSONObject batchcomplete = new JSONObject(jsonResponse);
			
		 token=batchcomplete.getJSONObject("query").getJSONObject("tokens").get("csrftoken").toString();
		 
	        
		return token;
	}
	 

	
	public void configureEditOperation( String endPoint, String lgname, String lgpassword){
		
		this.endPoint=endPoint;
		this.lgname=lgname;
		this.lgname=lgpassword;
		
		this.csrftoken=getCSRFToken(endPoint, lgname, lgpassword);
	}
	
	public void configureEditOperation( String endPoint, 
			String lgname, String lgpassword, boolean createOnly){
		
		configureEditOperation(endPoint, lgname, lgpassword);
		
		this.createOnly=createOnly; 
	}
	
	/**
	 * Realizar login via API e obter o Token
	 * @param title
	 * @param lgname String: conta de bot
	 * @param lgpassword String: Senha do bot
	 * @return String token csrf.
	 */
	private String getCSRFToken( String endPoint, String lgname, String lgpassword){
		 
		 //------PASSO 1 ---------------------------------------
		 //Request 1: Obter token do login (METODO get)
		 //String lgtoken = getToken(endPoint,"login");
		 
		 this.lgtoken = getToken(endPoint,"login");
		 
		 System.out.println("lgtoken= "+ lgtoken);
		 if(lgtoken==null){
			 //informar que o token está nulo
			 return null;
		 }
		 
		 //------PASSO 2 ---------------------------------------
		 //POST para completar o login
		
		 boolean login = login(endPoint,lgname,lgpassword, lgtoken );
		 
		 if(!login) {
			 System.out.println("FALHA NO LOGIN...");
			 //return null;
		 }
		 
		 //------PASSO 3 ---------------------------------------
		//obter csrf
		 String csrftoken = getToken(endPoint,"");
		 
		
		
		 return csrftoken;
	 }

	private void delete(String endPoint, Article article, String csrftoken) {
		//api.php?action=delete&title=Main%20Page&token=123ABC
		String url=endPoint+"?action=delete&title="+article.getTitle()
				+ "&format=json";
		
		// add request parameter, form parameters
        List<NameValuePair> params = new ArrayList<>();
        
        params.add(new BasicNameValuePair("token", csrftoken));
        
		String jsonResponse=submitPostRequest(url, params);
		
		System.out.println("DELETE = "+jsonResponse);
		
		
	}


	/**
	 * Para o edit conflict: recuperar starttimestamp e basetimestamp 
	 * @param title String. Titulo do artigo.
	 */
	private StartAndBaseTimes getStartAndBaseTime(String title) {
		
		String url=this.endPoint+ "?action=query&titles="+normalizarTitulo(title)
				+ "&prop=info%7Crevisions&inprop=&intoken=edit&rvprop=timestamp%7Ccontent&curtimestamp&format=json";
		
		 
		String jsonResponse=wikipediaRequestHandler .executeGet(url);
		 
		 if(wikipediaRequestHandler .isUnknownHostException() || jsonResponse==null) {
			 return null;
		 }
		 
		 //recuperar o conteúdo da chave "query"
		 JSONObject batchcomplete = new JSONObject(jsonResponse);
		 
		 JSONObject pagesJson = 
				 batchcomplete.getJSONObject("query").getJSONObject("pages");
		 
		 Iterator<String> keys = pagesJson.keys();
		 
		 String starttimestamp=null;
		 String basetimestamp=null;
		 
		 
		 //System.out.println("\n\n\n JSON START AND BASE TIMES = "+jsonResponse);
		
		 while(keys.hasNext()){
			 
			 String pageid= keys.next();
			 
			 JSONObject pageidJson = pagesJson .getJSONObject(pageid);
			 
			 starttimestamp=pageidJson.getString("starttimestamp");
			 
			 if(pageid.equals("-1")){
				break; 
			 }
			 
			 JSONObject revJsonArr = pageidJson.getJSONArray("revisions").getJSONObject(0);

			 basetimestamp=revJsonArr.getString("timestamp");
			 
			 break;
		 }
		 
		return new StartAndBaseTimes(starttimestamp, basetimestamp);
	}

	/**
	 * Substituir espações em branco por %20
	 * @param title
	 * @return
	 */
	private String normalizarTitulo(String title) {
		
		String title_=title.trim();
		
		if(title_.contains(" ")) {
			return title_.replace(" ", "%20");
		}
		
		return title_;
	}



	/**
	 * Editar de fato o artigo na Wikipedia
	 * @param createonly2 
	 * @param title
	 //* @param csrftoken
	 * @param csrftoken2 
	 * @throws EditException 
	 */
	public EditResult edit(String endPoint, Article article, boolean createonly)  {
		
		if (this.csrftoken==null){	
			try {
				
				throw new EditException("Exception: É priciso logar na API "
						+ "e obter o token antes de editar o artigo");
				
			} catch (EditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
		EditResult editResult=null;
		//Passo 1
		//Edit conflict: conflitos de edições
		
		String title=article.getTitle();
		StartAndBaseTimes startAndBaseTimes= getStartAndBaseTime(title);
		
		String starttimestamp=startAndBaseTimes.getStarttimestamp();
		
		String basetimestamp=startAndBaseTimes.getBasetimestamp();
		
		//PARAMETROS DE ediçao de fato
		//Page content.
		String text=article.getText();
		
		//A "csrf" token retrieved from action=query&meta=tokens
		//The token should always be sent as the last parameter, or at least after the text parameter.
		//This parameter is required.
		//String token = csrftoken;
		
		//Edit summary. Also section title when section=new and sectiontitle is not set.
		String summary=title+" created with basic information - (by bot)";
		
		
		boolean bot=true;
	
		List<NameValuePair> params = 
				configureParameters(title, text, starttimestamp, basetimestamp, 
						summary, this.csrftoken, createonly, bot);
		
		String url=endPoint + "?action=edit&utf8";
		
		String jsonResponse= submitPostRequest(url, params);
		
		//Registrar a data e  horário da edição
		this.lastEdit= new Date();
		
		//nao é mais o primeiro artigo
		this.first=false;
		
		JSONObject json = new JSONObject(jsonResponse);
		
		Iterator<String> keysIterator = json.keys();
		
		Set<String> keys= new HashSet<String>();
		
		while(keysIterator.hasNext()){
			keys.add(keysIterator.next());
		}
		
		
		//System.out.println("\n\n\nstarttimestamp = "+starttimestamp);
		//System.out.println("basetimestamp = "+basetimestamp);
		
		if (keys.contains("edit")){
			JSONObject jsonEdit=json.getJSONObject("edit");
			
			//System.out.println("JSON edit ="+json.get("edit").toString());
			
			String result = jsonEdit.getString("result");
			
			if(result.equals("Success")) {
				
				String pageid = jsonEdit.get("pageid").toString();
				String contentmodel = jsonEdit.getString("contentmodel");

				keys=getJSONKeys(jsonEdit);
				
				String oldrevid =null;
				if(keys.contains("oldrevid")) {
					oldrevid = jsonEdit.get("oldrevid").toString();
				}
				
				String newrevid = null;
				
				if(keys.contains("newrevid")) {
					newrevid = jsonEdit.get("newrevid").toString();
				}
				
				String newtimestamp = null;
				if(keys.contains("newtimestamp")) {
					newtimestamp = jsonEdit.get("newtimestamp").toString();
				}
				
				
				editResult= 
						new EditResult(true, pageid, contentmodel, oldrevid, newrevid, newtimestamp, jsonResponse);
				
				return editResult;
			}
		}
		
		//Falha, resultado detalhado no JSON 
		editResult= new EditResult( jsonResponse);
		
		
		//System.out.println("\n\n\nstarttimestamp = "+starttimestamp);
		//System.out.println("basetimestamp = "+basetimestamp);
		
		
		return editResult;
	}
	
	
	/**
	 * Editar de fato o artigo na Wikipedia
	 * @param createonly2 
	 * @param title
	 //* @param csrftoken
	 * @param csrftoken2 
	 * @throws EditException 
	 */
	public EditResult edit(Article article, String summary)  {
		
		if (this.csrftoken==null || this.endPoint==null){	
			try {
				
				throw new EditException("Exception: É priciso logar na API e obter o token antes de editar o artigo");
				
			} catch (EditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
		EditResult editResult=null;
		//Passo 1
		//Edit conflict: conflitos de edições
		
		String title=article.getTitle();
		StartAndBaseTimes startAndBaseTimes= getStartAndBaseTime(title);
		
		String starttimestamp=startAndBaseTimes.getStarttimestamp();
		
		String basetimestamp=startAndBaseTimes.getBasetimestamp();
		
		//PARAMETROS DE ediçao de fato
		//Page content.
		String text=article.getText();
		
		//A "csrf" token retrieved from action=query&meta=tokens
		//The token should always be sent as the last parameter, or at least after the text parameter.
		//This parameter is required.
		//String token = csrftoken;
		
		//Edit summary. Also section title when section=new and sectiontitle is not set.
		//String summary=title+" created with basic information - (by bot)";
		
		
		boolean bot=true;
	
		List<NameValuePair> params = 
				configureParameters(title, text, starttimestamp, basetimestamp, 
						summary, this.csrftoken, this.createOnly, bot);
		
		String url=endPoint + "?action=edit&utf8";
		
		String jsonResponse= submitPostRequest(url, params);
		
		//Registrar a data e  horário da edição
		this.lastEdit= new Date();
		
		//nao é mais o primeiro artigo
		this.first=false;
		
		JSONObject json = new JSONObject(jsonResponse);
		
		Iterator<String> keysIterator = json.keys();
		
		Set<String> keys= new HashSet<String>();
		
		while(keysIterator.hasNext()){
			keys.add(keysIterator.next());
		}
		
		
		//System.out.println("\n\n\nstarttimestamp = "+starttimestamp);
		//System.out.println("basetimestamp = "+basetimestamp);
		
		if (keys.contains("edit")){
			JSONObject jsonEdit=json.getJSONObject("edit");
			
			//System.out.println("JSON edit ="+json.get("edit").toString());
			
			String result = jsonEdit.getString("result");
			
			if(result.equals("Success")) {
				
				String pageid = jsonEdit.get("pageid").toString();
				String contentmodel = jsonEdit.getString("contentmodel");

				keys=getJSONKeys(jsonEdit);
				
				String oldrevid =null;
				if(keys.contains("oldrevid")) {
					oldrevid = jsonEdit.get("oldrevid").toString();
				}
				
				String newrevid = null;
				
				if(keys.contains("newrevid")) {
					newrevid = jsonEdit.get("newrevid").toString();
				}
				
				String newtimestamp = null;
				if(keys.contains("newtimestamp")) {
					newtimestamp = jsonEdit.get("newtimestamp").toString();
				}
				
				
				editResult= 
						new EditResult(true, pageid, contentmodel, oldrevid, newrevid, newtimestamp, jsonResponse);
				
				return editResult;
			}
		}
		
		//Falha, resultado detalhado no JSON 
		editResult= new EditResult( jsonResponse);
		
		
		//System.out.println("\n\n\nstarttimestamp = "+starttimestamp);
		//System.out.println("basetimestamp = "+basetimestamp);
		
		
		return editResult;
	}

	private List<NameValuePair> configureParameters(String title, String text, String starttimestamp, 
			String basetimestamp, String summary, String token, boolean createonly, boolean bot) {
		
		
		 // add request parameter, form parameters
        List<NameValuePair> urlParameters = new ArrayList<>();
          
        urlParameters.add(new BasicNameValuePair("title", title));
        
        urlParameters.add(new BasicNameValuePair("text", text));
        
        urlParameters.add(new BasicNameValuePair("starttimestamp", starttimestamp));
        
        urlParameters.add(new BasicNameValuePair("basetimestamp", basetimestamp));
        
        urlParameters.add(new BasicNameValuePair("summary", summary));
        
        //Mark this edit as a bot edit.
        if(bot){
        	 urlParameters.add(new BasicNameValuePair("bot", ""+bot));
        }
        
       
        //Don't edit the page if it exists already.
        if(createonly){
        	urlParameters.add(new BasicNameValuePair("createonly", ""+createonly));
        }
        
        urlParameters.add(new BasicNameValuePair("format", "json"));
        
        //csrf token
        urlParameters.add(new BasicNameValuePair("token", token));
        
       
        
		return urlParameters;
	}


	//private List<SimpleCookie> login(String endPoint, String lgname, String lgpassword, String lgtoken) {
	private boolean login(String endPoint, String lgname, String lgpassword, String lgtoken) {
		
		//String url = endPoint+ "?action=login&format=json";
		
		String url = endPoint+ "";
		this.isLogin=true;
		
		boolean sucess=false;
		List<NameValuePair> params = 
				configureLoginParameters(lgname, lgpassword, lgtoken);
		
		
		String jsonResponse=submitPostRequest(url, params);
		
		List<SimpleCookie> cookies=null;
		
		//Cookies names
		String cookieName_1="enwikiUserID";
		String cookieName_2="enwikiToken";
		String cookieName_3="enwikiUserName";
		String cookieName_4="enwiki_session";
		
		if(jsonResponse.contains("\"Success\"")) {
			sucess = true;
		}

		this.isLogin=false;
		
	    return sucess;
	}
	
	private void logout(String endPoint,  String token) {
		String url = endPoint+ "?action=logout&format=json";

		// add request parameter, form parameters
        List<NameValuePair> params = new ArrayList<>();
        
        params.add(new BasicNameValuePair("token", token));
        
		String jsonResponse=submitPostRequest(url, params);
		
	}
	private List<NameValuePair> configureLoginParameters(String lgname, String lgpassword, String lgtoken) {
		
		 // add request parameter, form parameters
        List<NameValuePair> urlParameters = new ArrayList<>();
        
        urlParameters.add(new BasicNameValuePair("action", "login"));
        
        urlParameters.add(new BasicNameValuePair("lgname", lgname));
        
        urlParameters.add(new BasicNameValuePair("lgpassword", lgpassword));
        
        urlParameters.add(new BasicNameValuePair("lgtoken", lgtoken));
        
        urlParameters.add(new BasicNameValuePair("format", "json"));
        
		return urlParameters;
	}

	
	/*
	 * https://meta.wikimedia.org/wiki/User-Agent_policy
	 */
	private String getUserAgent(String botName,String url, String email, String usedBaseLibrary){
		
		//The generic format is:
		//    <client name>/<version> (<contact information>) <library/framework name>/<version> 
		//[<library name>/<version> ...]. Parts that are not applicable can be omitted.
		//User-Agent: CoolToolName/0.0 (https://example.org/CoolTool/; CoolTool@example.org) UsedBaseLibrary/0.0
		
		String contactInformation = url+"; "+email;
		
		String userAgentSring=botName+" ("+contactInformation+") "+usedBaseLibrary;
		
		return userAgentSring;
	}
	
	
	 public String submitPostRequest(String url, List<NameValuePair> urlParameters) {
		 
	     HttpPost httpPostRequest = new HttpPost(url);
	     
	     String jsonResponse=null;
	        	
	     try {
	    	 
	    	 httpPostRequest.setEntity(
	    			 new UrlEncodedFormEntity(urlParameters, Consts.UTF_8));
	    	 /*
	    	 //parametros do user agent
	    	 String botName="Mvojgnnbot/1.0";
	    	 String contactUrl="http://www.ppgi.ufrj.br/";
	    	 String email= "mvojgnn@gmail.com";
	    	 String usedBaseLibrary=""; //"UsedBaseLibrary/0.0";
	    	 
	    	 //Configurar o user-agent
	         String userAgent = getUserAgent(botName, 
	        		 contactUrl, email, usedBaseLibrary);
	         httpPostRequest.addHeader(HttpHeaders.USER_AGENT,userAgent);
	          */   
	    	 
	    	 if(this.cookies!=null && !this.first) {
	    		 httpPostRequest.addHeader("testwikiSession",cookies.get("testwikiSession"));
	    		 httpPostRequest.addHeader("GeoIP",cookies.get("GeoIP"));   
	    	 }
	    	    
	        
	         CloseableHttpClient httpClient = HttpClients.createDefault();
	
	         CloseableHttpResponse response = httpClient.execute(httpPostRequest);
			
	         Header[] responseHeaders = response.getAllHeaders();
	         
	         if(this.isLogin) {
	        	 int i=0;
	        	 this.headers=new Header[responseHeaders.length];
	        	 
	        	 this.cookies= new  HashMap<String, String> ();
	        	 
	        	 for (Header header: responseHeaders) {
	        		 this.headers[i++]=header;
	        		 
	        		 if(header.getName().equals("Set-Cookie")) {
	        			 
	        			 System.out.println("Header name="+ header.getName());
			             //System.out.println("Value="
        				 // +value.split(";")[0].split("=")[1]);
        				 
	        			 //testwikiSession
	        			 String value = header.getValue();
	        			 
	        			 if(value.contains("testwikiSession")) {
	        				 
	        				 //System.out.println("Header name="+ header.getName());
				             //System.out.println("Value="
	        				 // +value.split(";")[0].split("=")[1]);
	        				 
	        				// System.out.println("Value="
			        		//		 +value);
	        				 cookies.put("testwikiSession", 
	        						 value.split(";")[0].split("=")[1]);
	        			 } else if(value.contains("GeoIP")) {
	        				 
	        				 System.out.println("Header name="+ header.getName());
				             //System.out.println("Value="
	        				 //+value.split(";")[0].split("=")[1]);
				             
	        				 cookies.put("GeoIP", 
	        						 value.split(";")[0].split("=")[1]);
				             System.out.println("Value="
			        				 +value);
	        			 }
	        			 
	        		 }
		             
		         }
	         }
	         
	         HttpEntity httpentity = response.getEntity();
	        		
	         InputStream inputStream = httpentity.getContent();
	        		
	         StringBuffer content= new StringBuffer("");
	        		
	         BufferedReader in = new BufferedReader(
	                    new InputStreamReader(inputStream , "UTF-8"));

	        	String inputLine=null;

	        	while ((inputLine = in.readLine()) != null) {
	        				System.out.println(inputLine);
	        				content.append(inputLine);
	        	}

	        	
	        	jsonResponse=content.toString();
	                
	        	//TemporyDataHandler.store("testeEdit.json",content.toString());
	        	
	             // System.out.println(EntityUtils.toString(response.getEntity()));
	        	in.close();
	                    
	    		
	        } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	        return jsonResponse;
	    }


	public Header[] getHeaders() {
		return headers;
	}



	/**
	 * Retorna as chaves de um JSON
	 * @param json JSONObject 
	 * @return
	 */
	private Set<String> getJSONKeys(JSONObject json){
		
		if(json==null) {
			return null;
		}
		
		Iterator<String> keysIterator = json.keys();
		
		Set<String> keys= new HashSet<String>();
		
		while(keysIterator.hasNext()){
			keys.add(keysIterator.next());
		}
		
		return keys;
	}
	 //getters
	public String getCsrftoken() {
		return csrftoken;
	}


	public String getLgtoken() {
		return lgtoken;
	}


	public String getEndPoint() {
		return endPoint;
	}


	public String getLgname() {
		return lgname;
	}



	public Date getLastEdit() {
		return lastEdit;
	}



	public void setLastEdit(Date lastEdit) {
		this.lastEdit = lastEdit;
	}


	public EditResult edit(Article article, String summary, String captchaId) {
		
		if (this.csrftoken==null || this.endPoint==null){	
			try {
				
				throw new EditException("Exception: É priciso logar na API e obter o token antes de editar o artigo");
				
			} catch (EditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
		EditResult editResult=null;
		//Passo 1
		//Edit conflict: conflitos de edições
		
		String title=article.getTitle();
		StartAndBaseTimes startAndBaseTimes= getStartAndBaseTime(title);
		
		String starttimestamp=startAndBaseTimes.getStarttimestamp();
		
		String basetimestamp=startAndBaseTimes.getBasetimestamp();
		
		//PARAMETROS DE ediçao de fato
		//Page content.
		String text=article.getText();
		
		//A "csrf" token retrieved from action=query&meta=tokens
		//The token should always be sent as the last parameter, or at least after the text parameter.
		//This parameter is required.
		//String token = csrftoken;
		
		//Edit summary. Also section title when section=new and sectiontitle is not set.
		//String summary=title+" created with basic information - (by bot)";
		
		
		boolean bot=true;
	
		List<NameValuePair> params = 
				configureParameters(title, text, starttimestamp, basetimestamp, 
						summary, this.csrftoken, this.createOnly, bot);
		
		params.add(new BasicNameValuePair("captchaid", captchaId));
		
		String url=endPoint + "?action=edit&utf8";
		
		String jsonResponse= submitPostRequest(url, params);
		
		//Registrar a data e  horário da edição
		this.lastEdit= new Date();
		
		//nao é mais o primeiro artigo
		this.first=false;
		
		JSONObject json = new JSONObject(jsonResponse);
		
		Iterator<String> keysIterator = json.keys();
		
		Set<String> keys= new HashSet<String>();
		
		while(keysIterator.hasNext()){
			keys.add(keysIterator.next());
		}
		
		
		//System.out.println("\n\n\nstarttimestamp = "+starttimestamp);
		//System.out.println("basetimestamp = "+basetimestamp);
		
		if (keys.contains("edit")){
			JSONObject jsonEdit=json.getJSONObject("edit");
			
			//System.out.println("JSON edit ="+json.get("edit").toString());
			
			String result = jsonEdit.getString("result");
			
			if(result.equals("Success")) {
				
				String pageid = jsonEdit.get("pageid").toString();
				String contentmodel = jsonEdit.getString("contentmodel");

				keys=getJSONKeys(jsonEdit);
				
				String oldrevid =null;
				if(keys.contains("oldrevid")) {
					oldrevid = jsonEdit.get("oldrevid").toString();
				}
				
				String newrevid = null;
				
				if(keys.contains("newrevid")) {
					newrevid = jsonEdit.get("newrevid").toString();
				}
				
				String newtimestamp = null;
				if(keys.contains("newtimestamp")) {
					newtimestamp = jsonEdit.get("newtimestamp").toString();
				}
				
				
				editResult= 
						new EditResult(true, pageid, contentmodel, oldrevid, newrevid, newtimestamp, jsonResponse);
				
				return editResult;
			}
		}
		
		//Falha, resultado detalhado no JSON 
		editResult= new EditResult( jsonResponse);
		
		
		//System.out.println("\n\n\nstarttimestamp = "+starttimestamp);
		//System.out.println("basetimestamp = "+basetimestamp);
		
		
		return editResult;
	}

}
