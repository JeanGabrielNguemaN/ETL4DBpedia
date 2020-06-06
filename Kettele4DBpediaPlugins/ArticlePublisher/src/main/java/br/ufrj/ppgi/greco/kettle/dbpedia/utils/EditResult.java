package br.ufrj.ppgi.greco.kettle.dbpedia.utils;

public class EditResult {

	private boolean success=false; 

	private String pageid = null;
	private String contentmodel = null;
	private String oldrevid = null;
	private String newrevid = null;
	private String newtimestamp = null;

	private String response=null;
	
	public EditResult(boolean success, String pageid, String contentmodel, 
			String oldrevid , String newrevid ,String newtimestamp, String jsonResponse){
		this(jsonResponse);
		this.success=success; 
		this.pageid =pageid;
		this.contentmodel = contentmodel;
		this. oldrevid = oldrevid;
		this.newrevid = newrevid;
		this.newtimestamp = newtimestamp;
		
	}

	public EditResult(String jsonResponse) {
		// TODO Auto-generated constructor stub
		this.setResponse(jsonResponse);
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getPageid() {
		return pageid;
	}

	public void setPageid(String pageid) {
		this.pageid = pageid;
	}

	public String getContentmodel() {
		return contentmodel;
	}

	public void setContentmodel(String contentmodel) {
		this.contentmodel = contentmodel;
	}

	public String getOldrevid() {
		return oldrevid;
	}

	public void setOldrevid(String oldrevid) {
		this.oldrevid = oldrevid;
	}

	public String getNewrevid() {
		return newrevid;
	}

	public void setNewrevid(String newrevid) {
		this.newrevid = newrevid;
	}

	public String getNewtimestamp() {
		return newtimestamp;
	}

	public void setNewtimestamp(String newtimestamp) {
		this.newtimestamp = newtimestamp;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

}
