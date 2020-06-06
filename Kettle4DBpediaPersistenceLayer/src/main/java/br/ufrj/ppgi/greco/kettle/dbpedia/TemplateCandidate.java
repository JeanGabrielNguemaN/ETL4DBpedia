package br.ufrj.ppgi.greco.kettle.dbpedia;

import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;

public class TemplateCandidate extends Template implements Comparable<Object>{
	
	public double getFrequence() {
		return frequence;
	}


	public void setFrequence(long frequence) {
		this.frequence = frequence;
	}
    
	//Maior frequência de ocorrências da classe que mapeia ao template
	private double frequence=0.000;
	
	public TemplateCandidate(Template template, double freq){
		super(template.getTitle(), template.getPageid());
		this.setId(template.getId());
		this.frequence=freq;
		
		
	}
	
	
	@Override
	public  int compareTo(Object o) {
		// TODO Auto-generated method stub
		
		
		TemplateCandidate other= (TemplateCandidate)o;
		
		if (this!=null && other==null ){
			return -1;
		}
		if (this!=null && other!=null){
			if (this.getFrequence()== other.getFrequence()){
		
				return 0;
			}
		
			if (this.getFrequence()> other.getFrequence()){
				return -1;	
			}
			
			return 1; 
		}
		 return 1; 
	}

	

}
