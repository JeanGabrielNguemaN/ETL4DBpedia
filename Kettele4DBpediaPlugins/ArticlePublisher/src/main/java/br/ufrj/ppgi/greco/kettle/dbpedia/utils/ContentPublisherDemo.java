package br.ufrj.ppgi.greco.kettle.dbpedia.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;



public class ContentPublisherDemo {

	public static void main(String[] args) {
		
		testEdit();
		
		//extractCaptchaId();
		
		
	}


	private static void extractCaptchaId() {
		String json="{\"warnings\":"
				+ "{\"main\":{\"*\":\"Subscribe to the mediawiki-api-announce "
				+ "mailing list at <https://lists.wikimedia.org/mailman/listinfo/"
				+ "mediawiki-api-announce> for notice of API deprecations and breaking "
				+ "changes. Use [[Special:ApiFeatureUsage]] to see usage of deprecated "
				+ "features by your application.\"},\"edit\":{\"*\":\"Passing \\\"\\\" "
				+ "for timestamp parameter \\\"basetimestamp\\\" has been deprecated. "
				+ "If for some reason you need to explicitly specify the "
				+ "current time without calculating it client-side, use \\\"now\\\".\"}},"
				+ "\"edit\":{\"captcha\":{\"type\":\"image\","
				+ "\"mime\":\"image/png\",\"id\":\"2009751204\","
				+ "\"url\":\"/w/index.php?title=Special:Captcha/image&wpCaptchaId=2009751204\"},"
				+ "\"result\":\"Failure\"}}";
		
		JSONObject jsonObj= new JSONObject(json);
		
		JSONObject jsonCaptcha = 
				jsonObj.getJSONObject("edit").getJSONObject("captcha");
		
		String idImage=jsonCaptcha.get("id").toString();
		
		String url= jsonCaptcha.getString("url");
		
		System.out.println("id="+idImage);
		System.out.println("url="+url);
	}


	private static void testEdit() {
		
		ContentPublisher contentPublisher= new ContentPublisher();
		
		//String endPoint="https://pt.wikipedia.org/w/api.php";
		String endPoint="https://test.wikipedia.org/w/api.php";
		 
		//Credenciais
		String lgname="BOTUSERNAME";
		String lgpassword="BOTPASSWORD";
		boolean createonly=true;
	    //configurar a edição
		contentPublisher.configureEditOperation(endPoint, lgname, lgpassword, createonly);
		
		//System.out.println("csrf Token= "+ contentPublisher.getCsrftoken());
		//System.out.println("Login Token= "+ contentPublisher.getLgtoken());
		//boolean createonly=false;
		
		//primeira edição
		String title="TestThing";
		/*
		String content="{{Info/Taxonomia\r\n" + 
				"|nome= Teste\r\n" + 
				"| domínio=[[Domain]]\r\n" + 
				"| reino=[[Plantae]]\r\n" + 
				"| espécie=Esculentus\r\n" + 
				"| família=[[Malvaceae]]\r\n" + 
				"| gênero= [[Abelmoschus]]\r\n" + 
				"}}\r\n" + 
				"\r\n" + 
				"'''''Teste''''' é um sssshsjsjsjsjs [[Test]] '' lnKlLocalizada no [[Test]] "
				+" - 1st -" 
				+"<ref name=\"WikiMarkup\">{{cite web |url=http://en.wikipedia.org/w/index.php?title=Help:Wiki_markup |title=Help:Wiki markup |publisher=Wikimedia Foundation}}</ref>.\r\n" + 
				"\r\n" + 
				"== References ==\r\n" + 
				"{{Reflist}} \r\n" + 
				"\r\n" + 
				"[[Category:Test]]\r\n" + 
				"";
		
		content="{{Info/Taxonomia\r\n" + 
				"| genero=[[Abuta]]\r\n" + 
				"| espécie=convexa\r\n" + 
				"| nome=Teste\r\n" + 
				"| família=[[Menispermaceae]]\r\n" + 
				"| reino=[[Plantae]]\r\n" + 
				"| domínio=[[Domain]]\r\n" +
				" }}\r\n" + 
				"'''''Teste''''' é umhsjsjsjsjs [[Test]] '' lnKlLocalizada no [[Test]] "
				+" - 1st -" 
				+"<ref name=\"WikiMarkup\">{{cite web "
				+ "|url=http://en.wikipedia.org/w/index.php?title=Help:Wiki_markup "
				+ "|title=Help:Wiki markup "
				+ "|publisher=Wikimedia Foundation}}</ref>.\r\n" + 
				
				"== References ==\r\n" + 
				"{{Reflist}}\r\n" + 
				"[[Category:Test]]";
		*/
		String infobox="{{Info/Taxonomia\r\n" + 
				"| genero=[[Abuta]]\r\n" + 
				"| espécie=convexa\r\n" + 
				"| nome=Teste\r\n" + 
				"| família=[[Menispermaceae]]\r\n" + 
				"| reino=[[Plantae]]\r\n" + 
				"| domínio=[[Domain]]\r\n" +
				" }}";
		
		String introduction= "'''''TestJustATest''''' is just a [[Test]]  "
				+ "for testing [[Test]] - 2st -";
		
		String titleRef = "CBPM Coleção Botânica de Plantas Medicinais";
		String publisherRef = "Fiocruz";
		String wikiMarkup = "WikiMarkup";
		
		String reference="<ref name=\""+ wikiMarkup+ "\">{{cite web "
				+ " |url="
				//+ "http://en.wikipedia.org/w/index.php?title=Help:Wiki_markup "
				+ "http://cbpm.fiocruz.br/ "
				+ " |title="
				+titleRef
				//+ "Help:Wiki markup "
				//+ " |publisher="
				//+ "Wikimedia Foundation"
				+ publisherRef
				+ " |accessdate=2020-01-25"
				+ " }}</ref>."; 
		
		reference="";
		String categories="[[Category:Test]]";
		
		String content=generateContent(title, introduction, infobox.toString(),
				reference, categories);
		
		Article article=new Article(title,content);
		
		String summary="created with basic information - (by bot) "+ " - (Article title :"+title+")";
		
		System.out.println("Content ="+article.getText());
		
		EditResult editResult = contentPublisher.edit(article, summary);
		
		//DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		//Data hora atual
		Date lastEdit= new Date();
		
		System.out.println("Sucesso= "+ editResult.isSuccess());
		System.out.println("lastEdit= "+ contentPublisher.getLastEdit());
		System.out.println("CreateOnly= "+ contentPublisher.isCreateOnly());
		
		System.out.println("Response Message= "+ editResult.getResponse());
		
		
		try {		
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		/*
		//SEGUNDA EDIÇÃO
		content="{{Info/Taxonomia\r\n" + 
				"|nome= Teste\r\n" + 
				"| domínio=[[Eukaryota]]\r\n" + 
				"| reino=[[Plantae]]\r\n" + 
				"| espécie=Esculentus\r\n" + 
				"| família=[[Malvaceae]]\r\n" + 
				"| gênero= [[Abelmoschus]]\r\n" + 
				"}}\r\n" + 
				"\r\n" + 
				"'''''Teste-2nd''''' é umashsjsjsjsjs [[Test]] '' lnKlLocalizada no [[Test]] <ref name=\"WikiMarkup\">{{cite web |url=http://en.wikipedia.org/w/index.php?title=Help:Wiki_markup |title=Help:Wiki markup |publisher=Wikimedia Foundation}}</ref>.\r\n" + 
				"\r\n" + 
				"== References ==\r\n" + 
				"{{Reflist}} \r\n" + 
				"\r\n" + 
				"[[Category:Test]]\r\n" + 
				"";
		article=new Article(title,content);
		
		Date now= new Date();
		
		long duracao = obterTempoDecorrido(lastEdit, now, TimeUnit.SECONDS);
		
		System.out.println("LastEdit :"+lastEdit);
		System.out.println("NOW :"+now);
		System.out.println("DURACAO :"+ duracao);
		
		long intervalo_epm=20;
		
		if(duracao<intervalo_epm) {
			try {		
				TimeUnit.SECONDS.sleep(intervalo_epm-duracao+1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//editar
		editResult = contentPublisher.edit(endPoint, article, createonly);
		System.out.println("\n\nSucesso= "+ editResult.isSuccess());
		System.out.println("Response Message= "+ editResult.getResponse());
		
		
		System.out.println("LastEdit...:"+lastEdit);
		System.out.println("NOW .......:"+now);
		System.out.println("DATA FIM ..:"+new Date());
		System.out.println("DURACAO ...:"+ duracao);
	   */
	}

	
	/**
	 * obter o tempo decorrido entre duas datas
	 * @param antes the oldest date
	 * @param agora the newest date
	 * @param timeUnit the unit in which you want the diff
	 * @return tempo decorrido na unidade especificada.Ex: segundos. 
	 */
	public static long obterTempoDecorrido(Date antes, Date agora, TimeUnit timeUnit) {
	   
		long duracaoMilliseconds = agora.getTime() - antes.getTime();
		
	    return timeUnit.convert(duracaoMilliseconds,TimeUnit.MILLISECONDS);
	}
	
	static private String generateContent(String title, String introduction,
			String infobox, String reference, String categories) {
		
		String references_sec="== References ==\r\n" + 
				"{{Reflist}}"; 
		
		return  introduction+"("+ reference +")\r\n"
				+ infobox +"\r\n"
				+ references_sec+"\r\n"
				+ categories+"\r\n";
	}
	
	
	static private String gerarCategorias(String categories) {
		
		if(categories==null) {
			return "";
		}
		if (categories.trim().equals("")) {
			return "";
		}
		
		//cada token é uma categoria sem [[Categoria:
		String[] tokens=categories.split(",");
		StringBuffer buffer= new StringBuffer("");
		for (String token: tokens) {
			buffer.append("[[Category:"+ token+"]]");
		}
		
		return buffer.toString();
	}
}