package br.ufrj.ppgi.greco.dbpedia.wikipedia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;

public class WikipediaRequestHandler {
	
	
	private boolean isUnknownHostException=false;

	/**
	 * Submeter request para a API.
	 * @param urlString
	 * @return
	 */
	public String executeGet(String urlString) {
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
			e.printStackTrace();
			content=null;
		} catch (UnknownHostException e) {
			
			isUnknownHostException=true; 
			e.printStackTrace();
			content=null;
	   } catch (IOException e) {
			e.printStackTrace();
			content=null;
		} 
		
		
		
		if (content!=null) {
			return content.toString();
		}
		return null;
	}

	/**
	 * Obter a primeira seção do artigo pelo título
	 * @param articleTitle
	 * @return
	 * */
	public String getFirstSection(String articleTitle){
			
			if(articleTitle==null || articleTitle.equals("")){
				return null;
			}
			
			String urlPrefix="https://pt.wikipedia.org/w/api.php?action=query&"
										+ "prop=revisions&rvprop=content&format=json&titles=";
								
			String[] tokens= articleTitle.split(" ");
								
			String titles="";
			
			String url="";
		
			for (int i=0; i<tokens.length;i++){
					titles+=tokens[i];
					
					//espaço apenas se não for último
					if(i!=(tokens.length-1)){
						titles+="%20";
					}
			}
			 //
	
			url+=urlPrefix+titles+"&rvsection=0&utf8";
	
			return  executeGet(url);		
		
	 }
	
	/**
	 * Busca se o article existe a partir do título
	 * @param targetTitle
	 * @return
	 */
	
	public String searchArticle(String targetTitle){
		
		
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
	
	
	public String getInfoboxInstanceByPageid(String id){
		
		String url="";
		
		if(id==null){
			return null;
		}
		
		String urlPrefix="https://pt.wikipedia.org/w/api.php?action=query&"
					+ "prop=revisions&rvprop=content&pageids="+id;

		url+=urlPrefix+"&rvsection=0&format=json&utf8";
								
		return  executeGet(url);		
	
	  }

	public boolean isUnknownHostException() {
		return isUnknownHostException;
	}
	
}
