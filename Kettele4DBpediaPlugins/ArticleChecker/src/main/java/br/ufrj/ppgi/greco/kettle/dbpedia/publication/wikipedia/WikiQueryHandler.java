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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

public class WikiQueryHandler {
	
	 public static String executeGet(String urlString) {
		    URL url;
		    StringBuffer content= new StringBuffer("");
		    
			try {
				url = new URL(urlString);
			
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
	    * Obter categorias de página(s) fornecendo título(s) ou id(s)
	    * @param type. Opções: "titles" ou "pageids".Dizer se forncerá título(s) ou id(s).  
	    * @param value. Páginas ou título(s), cada item serparado por | .
	    * @return array de categorias.
	    */
	 
	   public static String[] getPageCategories(String type, String value ){
	    	String[] categories=null;
	    	
	    	String request="https://pt.wikipedia.org/w/api.php?action=query&prop=categories"
	    			+ "&clshow=!hidden&cllimit=30&utf8&format=json";
	    	
	    	String result=null;
	    	if(type!=null){
   		
	    		if(type.equals("titles")){
	    			request+="&titles="+ value;
	    
	    		} else if(type.equals("pageids")){
	    			request+="&pageids="+ value;
	    		}
	    		
	    		result=executeGet(request);
	    	}
	    	
	    	if (result!=null && result.contains("categories")){
	    		JSONObject queryKey=getQueryFromJson(result);
	    		
	    		 JSONObject pagesKey = new JSONObject(queryKey.get("pages").toString());
	    		 
	    		 JSONObject pageKey  = new JSONObject(pagesKey.get(value).toString());
	    		 
	    		 JSONArray json_cateries =pageKey.getJSONArray("categories");
	    		 
	    		 int length=json_cateries.length();
	    		 
	    		 categories= new String[length];
	    		 
	    		 for (int i=0; i<length; i++){
	    			 JSONObject categoryJson =json_cateries.getJSONObject(i);
	    			 //String category= categoryJson.get("title").toString();
	    			 categories[i]=categoryJson.get("title").toString();
	    		 }
	    		
	    	}
	    	return categories;
	    }
	   
	   
	     public static JSONObject getQueryFromJson(String json){
			
			JSONObject parser = new JSONObject(json);
			
			//System.out.println(" parser = "+parser.toString());
			
			JSONObject query = new JSONObject(parser.get("query").toString());
			
			return query;
		}
	     
	     /**
	      * 
	      * @param firstSection
	      * @return
	      */
	    public static String getInfoboxBySection(String firstSection, String infoboxName){
			 
			 String infobox=null;

	         if(firstSection==null)
	        	 return null;
	         
	         //inicio
			 String[] parts=firstSection.split("[{][{]"+infoboxName);
			 
			 String content="{{Info";
			 //se tiver mais de duas partes
			 if (parts.length>1){
				 for (int i=1; i<parts.length; i++){
					 //descarta o primeiro
					 content+=parts[i];
				 }
				// System.out.println("Infobox ="+ content);
				 
				 parts=content.split("[}][}]");
				 content="";
				 if (parts.length==2){
					infobox=parts[0]+"}}"; 
				 }
				 else  if (parts.length>2){
					 for (int i=0; i<parts.length-1; i++){
						 //descarta o primeiro
						 content+=parts[i];
					 }
					 infobox=content+"}}";
				 }
				 else{
					 infobox=parts[0]+"}}";  
				 }
				 
				// System.out.println("Infobox ="+ infobox);
			 }
			 
			return infobox;
		  }
	    
	    
	    public static String getInfoboxBySectionV2(String firstSection, String infoboxName){
			 
			 String infobox=null;

	         if(firstSection==null)
	        	 return null;
	         
	         //inicio
			 String[] parts=firstSection.split("[{][{]"+infoboxName);
			 
			 String content="{{"+infoboxName;
			 //se tiver mais de duas partes
			 if (parts.length>1){
				 for (int i=1; i<parts.length; i++){
					 //descarta o primeiro
					 content+=parts[i];
				 }
				 System.out.println("Infobox + INTRO="+ content);
				 
				 
				 
				 //
				 parts=content.split("[}][}]");
				 content="";
				 if (parts.length==2){
					infobox=parts[0]+"}}"; 
				 }
				 else  if (parts.length>2){
					 for (int i=0; i<parts.length-1; i++){
						 //descarta o primeiro
						
						 if((parts[i].contains("=") && parts[i].contains("|")) 
								 ||(parts[i].trim().contains("\n") ||parts[i].trim().contains("") )){
							 content+=parts[i]+"}}";
						 }
						 
						
					 }//
					 infobox=content;//+"}}";
				 }
				 else{
					 infobox=parts[0]+"}}";  
				 }
				 
				// System.out.println("Infobox ="+ infobox);
			 }
			 
			return infobox;
		  }
	    
	    /**
	     * Extrair Infobox apartir da seção do artigo.
	     * @param firstSection. primeira seção do artigo
	     * @param infoboxName. nome do infobox. Formato: Info/Nome
	     * @return. Retorna infobox {{}}
	     */
	    public static String getInfoboxBySectionV3(String firstSection, String infoboxName){
			 
			 String infobox=null;

	         if(firstSection==null)
	        	 return null;
	         
	         //inicio
			 String[] parts=firstSection.split("[{][{]"+infoboxName);
			 
			 //partes da seção que começa com ....
			 //String infoboxStart="{{"+infoboxName;
			 
			 String content="{{"+infoboxName;//"";//
			 //se tiver mais de duas partes
			 if (parts.length>1){
				 
				 for (int i=1; i<parts.length; i++){
					 //descarta o primeiro
					 content+=parts[i];
				 }
				 	 
				 //
				 int num_chaves_abertas=0,num_chaves_fechadas=0 ;
				 parts=content.split("[}][}]");
				 content="";
				 if (parts.length==2){
					infobox=parts[0]+"}}"; 
				 }
				 else  if (parts.length>2){
					 for (int i=0; i<parts.length-1; i++){
						 //descarta o primeiro
						
						 if(num_chaves_abertas!=0 && num_chaves_abertas==num_chaves_fechadas){
							 break;
						 }
						 
						 if((parts[i].contains("=") && parts[i].contains("|")) 
								 ||(parts[i].trim().contains("\n") ||parts[i].trim().contains("") )){
							 
							 content+=parts[i]+"}}";
							 num_chaves_fechadas++;
							 String[] tokens = parts[i].split("[{][{]");
							 int qde_abertas=tokens.length-1;
							 
							 if(qde_abertas>=1){
								 num_chaves_abertas+=qde_abertas;
								// System.out.println(i+" - "+parts[i]+"   - OK");
							 }
						 }
						 
						
					 }//
					 //infobox=infoboxStart+content;//+"}}";
					 infobox=content;//+"}}";
				 }
				 
				 
				// System.out.println("Infobox ="+ infobox);
			 }
			 
			return infobox;
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
			if(articleTitle!=null){
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
									
			}else{
				return null;
			}
			//System.out.println("URL : "+ url);
			return  executeGet(url);		
		
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
}