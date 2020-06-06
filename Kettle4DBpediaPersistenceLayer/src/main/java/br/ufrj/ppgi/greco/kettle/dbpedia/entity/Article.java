package br.ufrj.ppgi.greco.kettle.dbpedia.entity;

public class Article {

	private String  content;
	private String pageid;
	private int id;
	public Article(String content) {
		this.content=content;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPageid() {
		return pageid;
	}
	public void setPageid(String pageid) {
		this.pageid = pageid;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
}
