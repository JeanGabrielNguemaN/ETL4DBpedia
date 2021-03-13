package br.ufrj.ppgi.greco.kettle.dbpedia.wikipedia.template;

import br.ufrj.ppgi.greco.kettle.dbpedia.wikipedia.WikiQueryHandler;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Extrair os templates de infoboxes da Wikipedia
 * @author Jean Gabriel Nguema Ngomo
 *
 */

public class TemplateExtractor {

	
	public static void processCategories(TemplateExtractor demo, ArrayList<Template> templates) {
		Set<String> categories=demo.getCategories(templates);
		
		Iterator<String> iter=categories.iterator();
		
		int i=1;
		while(iter.hasNext()){
			System.out.println(i+++" - Categoria: "+ iter.next());
		}
	}
	/**
	 * Pesquisa páginas contendo um termo no título.
	 * @param srsearch: termo no título.
	 * @param srnamespace: dominio. 10 para templates. O para artigos comuns.
	 * @param srlimit: quantidades de itens retornados. Máximo de 500.
	 * @param sroffset: a partir de que itens recuperar.
	 * @return
	 */
	
	public String searchByName(String srsearch, int srnamespace, int srlimit, int sroffset){
		String result=null;
		
		String request="https://pt.wikipedia.org/w/api.php?action=query&list=search&utf8=&srsearch="
				+ srsearch+"&srnamespace="+srnamespace+"&srlimit="+srlimit+"&sroffset="+sroffset+
				"&format=json";
		
		result= WikiQueryHandler.executeGet(request);
		
		return result;
		
	}
	
	/**
	 * obter todos os templates iniciando com pattern
	 * @param pattern. Exemplo: "Info/"
	 * @return
	 */
	public ArrayList<Template> getAllTemplates(String pattern){
		
		ArrayList<Template> templates=null;
		
		//Primeiro, executar uma busca básica para obter 
		//para obter a quantidade de templates (páginas)
		//que começam com este pattern
		String json=searchByName(pattern, 10, 10, 0);
		
		//recuperar o conteúdo da chave "query"
		JSONObject query = getQueryFromJson(json);
		
		//recuperar a quantidade
		int quantidade= new JSONObject(query.get("searchinfo").toString()).getInt("totalhits");
		
		if(quantidade<=0){
			return null;
		}
		
		//Caso exista pelo menos um template,recuperar por iteração
		//A recuperação é feita página a página, de 500 em 500
		//if (quantidade>0){
			
		//templates=new TemplatePage[quantidade];
			
		templates=new ArrayList<Template>();
			
		//agora recuperar página a pagina
		int srlimit=500, sroffset=0, j=0, k=0;
			
		//while(sroffset<quantidade && k==0){
		while(sroffset<quantidade){
			
			int srnamespace=10;
			
			String resultSet= searchByName(pattern, srnamespace, srlimit, sroffset);
		
			query = getQueryFromJson(resultSet);
				
			//array de templates
			JSONArray search = query.getJSONArray("search");
				
			int size=search.length();
				
			for (int i=0; i<size;i++){
				Template template=null;
					
				JSONObject pageKey= search.getJSONObject(i);
				
				String pageTitle= pageKey.getString("title");
					
				String pageid= pageKey.get("pageid").toString();
					
				//Construir um novo template a adiciona-lo para a lista	
				template= new Template(pageTitle, pageid);
					
				templates.add(template);
					
			}//for
				
			sroffset+=srlimit+1;
			
			//evitar sobrecarga do servidor
           
            try {
            	
				java.util.concurrent.TimeUnit.SECONDS.sleep(2);
				
			} catch (InterruptedException e) {
				
				e.printStackTrace();
				
			}
            
		}//while
		
		return templates;
	}
	
	/**
	 * A partir de json resultado de uma query, retorna json query.
	 * @param json
	 * @return
	 */
	
	private JSONObject getQueryFromJson(String json){
		
		JSONObject parser = new JSONObject(json);
		
		JSONObject query = new JSONObject(parser.get("query").toString());
		
		return query;
	}
	
	/**
	 * Seleciona os templates que são de fato Template de Infobox.
	 * Alguns templates serve apenas para formatação, etc.
	 * @param title
	 * @return
	 */
	private boolean isInfoboxTemplate(String title){
		boolean answer=false;
		
		if(hasInfoTemplateStart(title) && hasInfoTemplateEnd(title)
				&& isWellFormed(title))
			return true;
		
		return answer;
	}
	
	/**
	 * Filtro que elimina certos templates
	 * @param title
	 * @return
	 */
	private boolean isWellFormed(String title) {
		// TODO Auto-generated method stub
		
	if(!(title.contains("Eurovisão") || title.contains("Eurovisão") || title.contains(":Info/Lista ") 
			|| title.contains("Olympics") || title.contains("Jogos")|| title.contains("/doc/") 
			|| title.contains("/Doc/") || title.contains("Portugal")|| title.contains("Wikipédia")
			|| title.contains("WikidataProp") || title.contains("WikidataProp2")|| title.contains("/Assentamento/"))){
			return true;
		}
		
		return false;
	}
	
	/**
	 * Filtro de template para eliminar templates que não terminam bem 
	 * @param title
	 * @return
	 */
	private boolean hasInfoTemplateEnd(String title) {
		// TODO Auto-generated method stub
		
		String[] tokens= title.split("/");
		
		int lenght= tokens.length;
		if (lenght>=2 && 
				!(tokens[lenght-1].equals("doc")|| tokens[lenght-1].equals("Testes")
						|| tokens[lenght-1].equals("testes") || tokens[lenght-1].equals("Dados")
						|| tokens[lenght-1].equals("dados")|| tokens[lenght-1].contains("testes")
						|| tokens[lenght-1].equals("Cor")|| tokens[lenght-1].equals("Cor2")
						||tokens[lenght-1].equals("Wikidata")|| tokens[lenght-1].equals("imagem")
						||tokens[lenght-1].equals("row")|| tokens[lenght-1].equals("img")
						||tokens[lenght-1].equals("cor")|| tokens[lenght-1].equals("Rodapé")
						||tokens[lenght-1].equals("topo")|| tokens[lenght-1].equals("Rodapé")))
			return true;
		
		
		return false;
	}
	
	/**
	 * Filtro que determina se o infobox começa bem.
	 * @param title
	 * @return
	 */
	private boolean hasInfoTemplateStart(String title){
		
		String[] tokens= title.split("Info/");
		//good start
		
		if(tokens[0].equals("Predefinição:"))
			return true;
		
		return false;
	}
	
	/**
	 * 
	 * @param templates
	 * @return
	 */
	Set<String> getCategories(ArrayList<Template> templates){
		
		Set<String> templatesCategories=null;
		
		if(templates!=null){
			
			templatesCategories= new HashSet<String>();
			
			for (Template template: templates){
				
				String[] categories= WikiQueryHandler.getPageCategories("pageids", template.getPageid());
				
				if(categories!=null){
					
					//inserir as categorias do template no repositorio
					for (String category: categories){
						
						templatesCategories.add(category);
						
					}
				}
				
			}
		}
		return templatesCategories;
	}
	
	/**
	 * Obter os templates de infobox a partir de um conjunto de templates aplicando 
	 * uma serie de filtros. 
	 * @param templates
	 * @return
	 */
	public ArrayList<Template> getInfoboxTemplates(ArrayList<Template> templates){
		
		
		if(templates==null) {
			return null;
		}
		
		ArrayList<Template> infoboxTemplates= new ArrayList<Template>();

	    
		for (Template template: templates){
			
			String title=template.getTitle();
			
			if(isInfoboxTemplate(title)){
				
				//retirar "Predefinição:"
				template.setTitle(title.split(":")[1]);
				//inserir
				infoboxTemplates.add(template);
			}	
		}
			
		return infoboxTemplates;
	}
}
