package br.ufrj.ppgi.greco.kettle.dbpedia.utils;

public class StartAndBaseTimes {

	
	 private String starttimestamp=null;
	 private String basetimestamp=null;
	 
	 public StartAndBaseTimes( String starttimestamp, String basetimestamp){
		 
		 this.starttimestamp=starttimestamp;
		 this.basetimestamp= basetimestamp;
		 
	 }

	public String getStarttimestamp() {
		return starttimestamp;
	}

	public void setStarttimestamp(String starttimestamp) {
		this.starttimestamp = starttimestamp;
	}

	public String getBasetimestamp() {
		return basetimestamp;
	}

	public void setBasetimestamp(String basetimestamp) {
		this.basetimestamp = basetimestamp;
	}
}
