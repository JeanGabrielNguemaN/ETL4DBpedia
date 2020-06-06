package br.ufrj.ppgi.greco.kettle.dbpedia.utils;

public class EditValidator {
	
	
	public boolean validEpm(boolean testPhase, String epm) {
		if(epm.equals("")) {
			return false;
		}
		
		int  number =0;
		
		try{
			number = Integer.parseInt(epm);
		 
		} catch (NumberFormatException nfe) {
		        return false;
		}
		//não pode ser nulo
		if(number==0) {
			return false;
		}
		//Na fase de teste: até 3 edições
		if(testPhase && (number > 3 )) {
			return false;
		}
		
		//Nas outras fases: até 5 edições
		if( number > 5) {
			return false;
		}
		return true;
	}

}
