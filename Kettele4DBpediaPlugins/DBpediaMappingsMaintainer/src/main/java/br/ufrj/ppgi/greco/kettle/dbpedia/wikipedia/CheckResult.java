package br.ufrj.ppgi.greco.kettle.dbpedia.wikipedia;

public class CheckResult {
	
	public boolean tituloFound=false;
	
	public boolean pageFound=false;
	
	public Page page=null;
	
	CheckResult(Page page, boolean  pageFound, boolean tituloFound){
		this.page=page;
		this.pageFound=pageFound;
		this.tituloFound=tituloFound;
	}

	public boolean isTituloFound() {
		return tituloFound;
	}

	public void setTituloFound(boolean tituloFound) {
		this.tituloFound = tituloFound;
	}

	public boolean isPageFound() {
		return pageFound;
	}

	public void setPageFound(boolean pageFound) {
		this.pageFound = pageFound;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}
}
