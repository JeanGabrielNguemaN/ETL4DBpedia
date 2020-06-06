package br.ufrj.ppgi.greco.kettle.dbpedia.steps;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import br.ufrj.ppgi.greco.kettle.plugin.tools.datatable.DataTable;
import br.ufrj.ppgi.greco.kettle.dbpedia.registration.PublicationRegister;
import br.ufrj.ppgi.greco.kettle.dbpedia.utils.Article;
import br.ufrj.ppgi.greco.kettle.dbpedia.utils.ContentPublisher;
import br.ufrj.ppgi.greco.kettle.dbpedia.utils.EditResult;
import br.ufrj.ppgi.greco.kettle.dbpedia.utils.EditValidator;
import br.ufrj.ppgi.greco.kettle.dbpedia.utils.EpmException;
import br.ufrj.ppgi.greco.kettle.dbpedia.wikipedia.CheckResult;
import br.ufrj.ppgi.greco.kettle.dbpedia.wikipedia.Entity;
import br.ufrj.ppgi.greco.kettle.dbpedia.wikipedia.EntityManager;
import br.ufrj.ppgi.greco.kettle.dbpedia.wikipedia.WikipediaPageChecker;

public class ArticlePublisherStep extends BaseStep implements StepInterface {

	public ArticlePublisherStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

		ArticlePublisherStepMeta meta = (ArticlePublisherStepMeta) smi;
		ArticlePublisherStepData data = (ArticlePublisherStepData) sdi;

		Object[] row = getRow();

		if (row == null) {
			
			setOutputDone();
			
			return false;
		}

		if (first) { 
			// Executa apenas uma vez, first eh definido na superclasse
			first = false;
			
			//salvar endPoint e credenciais
			String lgname=meta.getBotUser();
			String lgpassword=meta.getBotPassword();
			String endPoint=meta.getEndPoint();
			data.summary=meta.getSummary();
			data.referenceUrl=meta.getRefUrl();
			data.referenceTitle=meta.getRefTitle();
			data.referencePublisher=meta.getRefPublisher();
			data.referenceAuthor=meta.getRefAuthor();
			data.referenceDate=meta.getRefDate();
			data.referenceAccessdate=meta.getRefAccessDate();
			
			data.epm=Integer.parseInt(meta.getEpm());
			
			validateEpm(meta.isTestPhase(), meta.getEpm());
			
			ContentPublisher contentPublisher= new ContentPublisher();
			
			//configurar o operação de edição/criação de artigos
			contentPublisher.configureEditOperation(endPoint, 
					lgname, lgpassword, meta.isCreateOnly());
			
			data.publisher=contentPublisher;
				
			//verificarHeadersLogin(data.publisher.getHeaders());
			
			RowMetaInterface rowMeta = getInputRowMeta(); // chamar apenas apos											// chamar getRow()
			//data.inputRowSize = rowMeta.size();
			data.outputRowMeta = rowMeta.clone();
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this);
			
			//guardar os fields com seus indexes
			data.fieldsIndexesHashMap=obterFieldIndexOf(data.outputRowMeta);
		} //first
		
		//Pagina existe na wikipedia?
		boolean thisPageExists="true".equals(extrairValorField(row, "articleExists"));
		//this.logBasic("==== This Page Exists : "+thisPageExists);
		
		//wiki não é de teste
		//if(thisPageExists && !meta.getEndPoint().contains("test")) {
		if(thisPageExists) {
			
			putOutPageExists(row, meta, data);

			return true;
		}
		
		//Obter o titulo
		String proposedTitle= extrairValorField(row, "potentialArticleTitle");
		
		//Obter primeira seção
		String firstSection= extrairValorField(row, "firstSection");
				
		//Obter o conteudo do artigo em JSON
		String infoboxContentJSON= extrairValorField(row, "infoboxContent");
	
		//Gerar o conteúdo do artigo
		Article article=generateArticle(meta, data, proposedTitle,firstSection, infoboxContentJSON);
		
		String summary=data.summary + " (Article: "+article.getTitle()+")";
		
		//lastDate é nulo antes de editar
		Date lastEdit = data.publisher.getLastEdit();
		
		//resultado da edição
		EditResult editResult = null;
	
		if(lastEdit==null){
			//primeira edição
			editResult = data.publisher.edit(article, summary);
		
			//Adicionar o resultado no fluxo
			putOutFields(row, meta, data, editResult);
			
		    //registrar criação do artigo
			registerArticleCreation(meta, article, summary, editResult);
	  
			return true;
		} 
	
		//Outras edições
		Date now= new Date();
		
		long duracao = obterTempoDecorrido(lastEdit, now, TimeUnit.SECONDS);
		
		long edit_frequence=60/data.epm;
		
		//aguardar o tempo necessário
		if(duracao<edit_frequence) {
			try {		
				TimeUnit.SECONDS.sleep(edit_frequence-duracao+1);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
		
		editResult = data.publisher.edit(article, summary);
		
		putOutFields(row, meta, data, editResult);
		
		//registrar criação do artigo
		registerArticleCreation(meta, article, summary, editResult);
  
		return true;
	}

	public void registerArticleCreation(ArticlePublisherStepMeta meta, Article article, String summary,
			EditResult editResult) {
		PublicationRegister  register=new PublicationRegister();
		
		String articleContent=article.getText();
					
		String message=editResult.getResponse();
		
		String endpoint=meta.getEndPoint();	
		
		String pageid= editResult.getPageid();
		
		String newtimestamp=editResult.getNewtimestamp();
		
		Timestamp entrydatetime = new Timestamp(System.currentTimeMillis());
					        
		String botaccount=meta.getBotUser();
		
		/*
		String logmessage="===PAGEID :"+pageid+ "\n===SUMMARY :"+summary+ "\n===MESSAGE :"+message+
				"\n===ENDPOINT :"+endpoint+"\n===BOT :"+botaccount+"\n===ENTRYDATETIME :"+entrydatetime+
				"\n===NEWTIMESTAMP :"+newtimestamp;
		
		logBasic(logmessage);
		*/
		register.notifyCreation(articleContent, pageid, summary, message, endpoint, botaccount, entrydatetime);
	}

	private void putOutPageExists(Object[] row, ArticlePublisherStepMeta meta, ArticlePublisherStepData data) throws KettleStepException {
		//manter os dados de entrada
		Object[] outputRow =row;
				
				//index do field
		int index=-1;
			
		//Incluir campo do status da edição
		index=data.fieldsIndexesHashMap.get("successOnEdit");
		//incluir no row de saída
		outputRow = RowDataUtil.addValueData(outputRow, index, false);
				 
		//Incluir Campo mensagem do servidor
		index=data.fieldsIndexesHashMap.get("serverResponse");
					
		outputRow = RowDataUtil.addValueData(outputRow, index, "NA");
									
		// Coloca linha no fluxo
		putRow(data.outputRowMeta, outputRow);
	}

	private void putOutFields(Object[] inputRow, ArticlePublisherStepMeta meta, 
			ArticlePublisherStepData data, EditResult editResult) throws KettleException {
		//manter os dados de entrada
		Object[] outputRow =inputRow;
		
		//index do field
		int index=-1;
	
		//Incluir campo do status da edição
		index=data.fieldsIndexesHashMap.get("successOnEdit");
		
		if(editResult==null) {
			
			//incluir no row de saída
			 outputRow = RowDataUtil.addValueData(outputRow, index, false);
			 
			//Incluir Campo mensagem do servidor
			 index=data.fieldsIndexesHashMap.get("serverResponse");
				
			 outputRow = RowDataUtil.addValueData(outputRow, index, "Server not found");
								
			 // Coloca linha no fluxo
		     putRow(data.outputRowMeta, outputRow);
		     
			 return ;
		}
		//incluir no row de saída
		 outputRow = RowDataUtil.addValueData(outputRow, index, editResult.isSuccess());
		 
		//Incluir Campo mensagem do servidor
		 index=data.fieldsIndexesHashMap.get("serverResponse");
			
		 outputRow = RowDataUtil.addValueData(outputRow, index, editResult.getResponse());
							
		 // Coloca linha no fluxo
	     putRow(data.outputRowMeta, outputRow);
	}

    /**
	 * Obter field de entrada com seus indexes dentro do row
	 * @param rowMetaInterface
	 * @return
	 */
	private HashMap<String, Integer> obterFieldIndexOf(RowMetaInterface rowMetaInterface) {
		
		HashMap<String, Integer> fieldIndexHashMap= null;
		 
	    //objeto do RowMetaInterface é singleton. Ou seja, mudanças refletem imediatmente na saida.
		//RowMetaInterface rowMetaInterface = data.outputRowMeta;
		
		if(rowMetaInterface==null) {
			return null;
		}
		
		String[] fields=rowMetaInterface.getFieldNames();
		 
		if(fields==null) {
			return null;
		}
		
		fieldIndexHashMap=new HashMap<String, Integer>();
		
	    for (String field: fields) {
			 
			 int index=rowMetaInterface.indexOfValue(field);
			 
			 fieldIndexHashMap.put(field, index);
		 }
		 
		return fieldIndexHashMap;
		
	}
	
	/**
	 * extrair valor do field(campo) no row (registro).
	 * @param row
	 * @param field
	 * @return String. Retorna valor do field no row corrente.
	 * @throws KettleValueException
	 */
	private String extrairValorField(Object[] row, String field) throws KettleValueException {
		
		RowMetaInterface rowMetaInterface = getInputRowMeta();
		
		int index=rowMetaInterface.indexOfValue(field);
		
		//Obter o tipo do campo
		ValueMetaInterface valueMetaData=rowMetaInterface.getValueMeta(index);
		
		//logBasic("==== valueMetaData : ="+ valueMetaData);
		
		if(valueMetaData==null) {
			return null;
		}
		
		int tipo = valueMetaData.getType();
		
		String valorField="";
		
		if (tipo==ValueMetaInterface.TYPE_STRING) {
			//Valor da propriedade
			valorField = rowMetaInterface.getString(row,index);
			
	
		} else if (tipo==ValueMetaInterface.TYPE_BOOLEAN) {

			//obter o valor
			valorField= ""+rowMetaInterface.getBoolean(row,index);

		}
		return valorField;
	}
	
	/**
	 * Gerar o artigo no formato wikitext
	 * @param data 
	 * @param meta 
	 * @param infoboxContentJson
	 * @return
	 */
	public  Article generateArticle(ArticlePublisherStepMeta meta, ArticlePublisherStepData data,
			String proposedTitle, String firstSection, String infoboxContentJson){
			
		JSONObject parser = new JSONObject(infoboxContentJson);
	    
		//Obter Infobox
		String infobox= gerarInfobox(parser);
		
		//gerar referência
	    String references =null;
	    
	    references= getReference(data);
	    
	    //Conteudo do artigo
	    String content= generateContent(proposedTitle, firstSection, infobox.toString(),
	    		references, gerarCategorias(meta.getCategories()));
	    
		return new Article(proposedTitle, content);
	}

	private String getReference(ArticlePublisherStepData data) {
		
		String referenceURL= data.referenceUrl;
		String referenceTitle = data.referenceTitle;
		String referencePublisher = data.referencePublisher;
		String referenceAccessDate = data.referenceAccessdate;
		
		
		String reference="<ref name=\"wikiMarkup\">{{cite web "
				+ " |url="+referenceURL
				+ " |title="+ referenceTitle
				+ " |publisher="+ referencePublisher
				+ " |accessdate=" + referenceAccessDate
				+ " }}</ref>.";
		
		return reference;
	}
	
	
	/**
	 * Used for tests proposes of getReference(data)
	 * @return
	 */
	private String getDefaultReference() {
		
		String reference="<ref name=\"wikiMarkup\">{{cite web "
				+ " |url="
				+ "http://en.wikipedia.org/w/index.php?title=Help:Wiki_markup "
				+ " |title="+ "Help:Wiki markup "
				+ " |publisher="+ "Wikimedia Foundation"
				+ " |accessdate=2020-01-25"
				+ " }}</ref>.";
		
		return reference;
	}
	

	private String gerarInfobox(JSONObject articleJson) {
		
		StringBuffer infobox= new StringBuffer();
		
		JSONObject infoboxKey=articleJson.getJSONObject("infobox");
		
		//nome do infobox
		String templateName=infoboxKey.getString("nome");
		
		infobox.append("{{"+templateName+"\r\n");
		
		JSONArray propertiesvalues = infoboxKey.getJSONArray("propertiesvalues");
		
		int length=propertiesvalues.length();
		
		for(int i=0;i<length; i++){
			
			JSONObject propertyValueKey = propertiesvalues.getJSONObject(i);
			
			String property = propertyValueKey.getString("property");
			
			String value = propertyValueKey.getString("value");
			
			infobox.append("| "+property+"="+value+"\r\n");

		}
	
	    infobox.append("}}");
		return infobox.toString();
	}

	
	private String generateContent(String title, String introduction, String infobox, String reference, String categories) {
		
		String references_sec="== References ==\r\n" + 
				"{{Reflist}}"; 
		
		return  introduction+"("+ reference +")\r\n"
					+ infobox +"\r\n"
					+ references_sec+"\r\n"
					+ categories+"\r\n";
	}
	
	/**
	 * Gerar as lista de categorias no final do artigo
	 * @param categories String. Lista de categorias separadas por virgula.
	 * @return String Lista de categorias no formato da Wikipedia.
	 */
	private String gerarCategorias(String categories) {
		
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
	
	public void validateEpm(boolean testPhase, String epm) throws KettleException {
		if(epm==null) {
			
			throw new EpmException("campo epm não informado");
		}
		
		if(epm.equals("")) {
			
			throw new EpmException("campo epm não informado");
		}
		
		int  number =0;
		
		try{
			number = Integer.parseInt(epm);
		 
		} catch (NumberFormatException nfe) {
			
			throw new EpmException("Valor incorreto para epm");
				
		}
		//não pode ser nulo
		if(number==0) {
			
			throw new EpmException("epm igual a 0");
				
		}
		//Na fase de teste: até 3 edições
		if(testPhase && (number > 3 )) {
		
			throw new EpmException("epm deve ter valor de 1 a 3 para testes");
			
		}
		
		//Nas outras fases: até 5 edições
		if( number > 5) {
				throw new EpmException("epm deve ter valores de 1 a 5");
		}
	}

	
	private  String[] extractCaptchaId(String json) {
		
		String[] arr= new String[2];
		JSONObject jsonObj= new JSONObject(json);
		
		JSONObject jsonCaptcha = 
				jsonObj.getJSONObject("edit").getJSONObject("captcha");
		
		String idImage=jsonCaptcha.get("id").toString();
		
		String url= jsonCaptcha.getString("url");
		
		arr[0]=idImage;
		arr[1]=url;
		//System.out.println("id="+idImage);
		//System.out.println("url="+url);
		
		//return idImage;
		return arr;
	}

	
}
