package br.ufrj.ppgi.greco.kettle.dbpedia.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

import br.ufrj.ppgi.greco.kettle.dbpedia.wikipedia.WikiQueryHandler;

public class ContentPublisherOLD {
	 
	 /**
	  * Obter o token em função do tipo
	  * @param type
	  * @return
	  */
	
	private String csrftoken=null;
	private String lgtoken = null;
	private  String endPoint=null;
	private String lgname=null;
	private String lgpassword=null;
	
	
	 private String getToken(String endPoint, String type) {
		
		 String url=null;
		 String token=null;	
		 
		 if(!(type==null || type.equals(""))) {
			
			 url=endPoint
			 		+ "?action=query&meta=tokens"
				 		+ "&type="+type
				 		+ "&format=json";
			
			 String jsonResponse=WikiQueryHandler.executeGet(url);
			 
				//recuperar o conteúdo da chave "query"
			 JSONObject batchcomplete = new JSONObject(jsonResponse);
				
			 token=batchcomplete.getJSONObject("query").getJSONObject("tokens").get(type.trim()+"token").toString();
			 
			 return token;
	     }
		 
		 url="https://pt.wikipedia.org/w/api.php?action=query&meta=tokens&format=json";
		
		 String jsonResponse=WikiQueryHandler.executeGet(url);
		 
			//recuperar o conteúdo da chave "query"
		 JSONObject batchcomplete = new JSONObject(jsonResponse);
			
		 token=batchcomplete.getJSONObject("query").getJSONObject("tokens").get("csrftoken").toString();
		 
	        
		return token;
	}
	 
	
	public boolean publish(Article article){
		 
		 boolean published = false;
		 
		 //String endPoint="https://pt.wikipedia.org/w/api.php";
		 String endPoint="https://test.wikipedia.org/w/api.php";
		 
		 //------PASSO 1 ---------------------------------------

		 //Request 1: Obter token do login (METODO get)
		 String lgtoken = getToken(endPoint,"login");
		 
		 System.out.println("lgtoken= "+ lgtoken);
		 if(lgtoken==null){
			 //informar que o token está nulo
			 return false;
		 }
		 
		 //------PASSO 2 ---------------------------------------
		 //POST para completar o login
		 
		 String lgname="BOTUSERNAME";
		 String lgpassword="BOTPASSWORD";
		 
		 boolean login = login(endPoint,lgname,lgpassword, lgtoken );
		 
		 if(!login) {
			 System.out.println("FALHA NO LOGIN...");
			 //return false;
		 }
		 
		 //------PASSO 3 ---------------------------------------
		//obter csrf
		 String csrftoken = getToken(endPoint,"");
		 
		 System.out.println("csrftoken= "+ csrftoken);

		
		 
		 //------PASSO 4 ---------------------------------------
	    //editar
		 
		//published=edit(endPoint,article, csrftoken);
		 EditResult editResult = edit(endPoint,article);

		 //-----PASSO 5 
		 
		 //api.php?action=delete&title=Main%20Page&token=123ABC
		 //delete(endPoint,article, csrftoken);
		 
		//-----PASSO 6 
		//logout
		 logout(endPoint, csrftoken);
		
		 return published;
	 }
	
	
	public void configureEditOperation( String endPoint, String lgname, String lgpassword){
		
		this.endPoint=endPoint;
		this.lgname=lgname;
		this.lgname=lgpassword;
		
		this.csrftoken=getCSRFToken(endPoint, lgname, lgpassword);
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
		
		String url="https://pt.wikipedia.org/w/api.php?action=query&"
				+ "titles="+title
				+ "&prop=info%7Crevisions&inprop=&intoken=edit&rvprop=timestamp%7Ccontent&curtimestamp&format=json";
		
		 String jsonResponse=WikiQueryHandler.executeGet(url);
		 
		 //recuperar o conteúdo da chave "query"
		 JSONObject batchcomplete = new JSONObject(jsonResponse);
		 
		 JSONObject pagesJson = 
				 batchcomplete.getJSONObject("query").getJSONObject("pages");
		 
		 Iterator<String> keys = pagesJson.keys();
		 
		 String starttimestamp=null;
		 String basetimestamp=null;
		 
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
	 * Editar de fato o artigo na Wikipedia
	 * @param title
	 //* @param csrftoken
	 * @param csrftoken2 
	 */
	public EditResult edit(String endPoint, Article article) {
	//public boolean edit(String endPoint, Article article, String csrftoken) {	
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
		String token = csrftoken;
		
		//Edit summary. Also section title when section=new and sectiontitle is not set.
		String summary=title+" created with basic information - (by bot)";
		
		/*
		String editUrl ="https://pt.wikipedia.org/w/api.php?action=edit&title="+title
				+ "Test&summary="+summary
				+ "&text="+text
				+"starttimestamp="+starttimestamp
				+ "&basetimestamp="+basetimestamp
				+ "&token="+token;
		*/
		List<NameValuePair> params = 
				configureParameters(title, text, starttimestamp, basetimestamp, 
						summary, token);
		
		String url=endPoint + "?action=edit&utf8";
		
		String jsonResponse=null;
		
		jsonResponse = submitPostRequest(url, params);
		
		JSONObject json = new JSONObject(jsonResponse);
		
		if(!jsonResponse.contains("\"edit\":")) {
			//falha
			return null;
		}
		
		
		JSONObject jsonEdit=json.getJSONObject("edit");
		
		System.out.println("JSON edit ="+json.get("edit").toString());
		
		String result = jsonEdit.getString("result");
		
		if(result.equals("Success")) {
			
			
			String pageid = jsonEdit.get("pageid").toString();
			String contentmodel = jsonEdit.getString("contentmodel");
			String oldrevid = jsonEdit.get("oldrevid").toString();
			String newrevid = jsonEdit.get("newrevid").toString();
			String newtimestamp = jsonEdit.get("newtimestamp").toString();
			
			editResult= 
					new EditResult(true, pageid, contentmodel, oldrevid, newrevid, newtimestamp, jsonResponse);
			
			return editResult;
			
		}
		
		//Falha, resultado detalhado no JSON 
		editResult= new EditResult( jsonResponse);
		
		return editResult;
	}

	private List<NameValuePair> configureParameters(String title, String text, 
			String starttimestamp, String basetimestamp, String summary, String token) {
		
		/*
		String editUrl ="https://pt.wikipedia.org/w/api.php?action=edit&title="+title
				+ "Test&summary="+summary
				+ "&text="+text
				+ "&basetimestamp="+basetimestamp
				+ "&token="+token;
		*/
		
		 // add request parameter, form parameters
        List<NameValuePair> urlParameters = new ArrayList<>();
          
        urlParameters.add(new BasicNameValuePair("title", title));
        
        urlParameters.add(new BasicNameValuePair("text", text));
        
        urlParameters.add(new BasicNameValuePair("starttimestamp", starttimestamp));
        
        urlParameters.add(new BasicNameValuePair("basetimestamp", basetimestamp));
        
        urlParameters.add(new BasicNameValuePair("summary", summary));
        
        //Mark this edit as a bot edit.
        urlParameters.add(new BasicNameValuePair("bot", "true"));
        
        //Don't edit the page if it exists already.
        urlParameters.add(new BasicNameValuePair("createonly", "true"));
        
        //csrf token
        urlParameters.add(new BasicNameValuePair("token", token));
        
        urlParameters.add(new BasicNameValuePair("format", "json"));
        
		return urlParameters;
	}


	//private List<SimpleCookie> login(String endPoint, String lgname, String lgpassword, String lgtoken) {
	private boolean login(String endPoint, String lgname, String lgpassword, String lgtoken) {
		
		String url = endPoint+ "?action=login&format=json";
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
        
        //urlParameters.add(new BasicNameValuePair("action", "login"));
        
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
	        
	         CloseableHttpClient httpClient = HttpClients.createDefault();
	
	         CloseableHttpResponse response = httpClient.execute(httpPostRequest);
					
	         Header[] headers = response.getAllHeaders();
	        		
	         for (Header header: headers) {

	            	System.out.println("Header name="+ header.getName());
	            	System.out.println("Value="+header.getValue());
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

}
