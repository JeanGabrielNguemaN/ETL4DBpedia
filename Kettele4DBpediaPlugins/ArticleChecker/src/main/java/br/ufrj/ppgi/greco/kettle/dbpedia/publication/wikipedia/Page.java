package br.ufrj.ppgi.greco.kettle.dbpedia.publication.wikipedia;

public class Page {
	
	private String title;
	private String pageid;
	private String infobox=null;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getInfobox() {
		return infobox;
	}

	public void setInfobox(String infobox) {
		this.infobox = infobox;
	}

	public Page(String title){
		
		this.title= title;

	}
	
	public Page(String title, String pageid){
		
		this.title= title;
		this.setPageid(pageid);
	}

	public String getPageid() {
		return pageid;
	}

	public void setPageid(String pageid) {
		this.pageid = pageid;
	}
}
