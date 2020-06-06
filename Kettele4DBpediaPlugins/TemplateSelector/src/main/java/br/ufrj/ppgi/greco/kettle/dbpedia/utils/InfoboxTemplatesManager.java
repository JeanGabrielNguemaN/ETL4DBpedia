package br.ufrj.ppgi.greco.kettle.dbpedia.utils;

import java.util.ArrayList;
import java.util.List;

public class InfoboxTemplatesManager {
	
    
	public static String[] getAttributes(){
		
		List<String> result = new ArrayList<String>();
		
		String[] infobox_attributes={"nome_oficial","nome_nativo","outro_nome","país","apelido", "lema",
				"tamanho_imagens","fuso_horário_DST","diferença_utc_DST","coord_título","coord_sufixo",
				"latg,latm","lats,latNS","longg","longm","longs",
				"longEW","altitude"};
	

		for (String attribute : infobox_attributes) {
	
			result.add(attribute);
		}
		

		return result.toArray(new String[result.size()]);
	}
	
	
	public static String[] getAttributes(String templateName){
		
		List<String> result = new ArrayList<String>();
		
		
		String[] infobox_attributes=null;

		if(templateName!=null){
			
			if(templateName.equals("cidade")){
		
			
				infobox_attributes=new String[]{"nome_oficial","nome_nativo","outro_nome","país","apelido", "lema",
						"tamanho_imagens","fuso_horário_DST","diferença_utc_DST","coord_título","coord_sufixo",
						"latg,latm","lats,latNS","longg","longm","longs",
						"longEW","altitude"};
				
				/*for (String attribute : infobox_attributes) {
					
					result.add(attribute);
				}*/
			
			//vira um dao depois por arquivo ou db
			}else if (templateName.equals("pessoa")) {
				infobox_attributes=new String[]{"nome_pessoa","nome_civil","data_nascimento",
						"local_c=nasciment","apelido"};
			
			} else if (templateName.equals("político")) {
				infobox_attributes=new String[]{"nome_pessoa","nome_civil","data_nascimento",
						"partido","mandato"};
			
			}else if (templateName.equals("organização")) {
				infobox_attributes=new String[]{"nome_organização","cidade_sede","data_fundação",
						"país","logo"};
				
			}else if (templateName.equals("organização")) {
				infobox_attributes=new String[]{"nome_pessoa","nome_civil","data_nascimento",
						"local_c=nasciment","apelido"};
			
			}else{
				infobox_attributes=new String[]{""};
			}
				
		
			for (String attribute : infobox_attributes) {
				
				result.add(attribute);
			}
		}//if !null

		return result.toArray(new String[result.size()]);
	}
	
	/**
	 * Obter todos os infoboxes
	 * @return
	 */
	public static String[] getInfoboxes(){
		
		List<String> result = new ArrayList<String>();
		
		String[] infoboxes={"pessoa","político","cidade", "país",
				"organização","estado","museu"};
	

		for (String infobox : infoboxes) {
	
			result.add(infobox);
		}
		

		return result.toArray(new String[result.size()]);
	}
}
