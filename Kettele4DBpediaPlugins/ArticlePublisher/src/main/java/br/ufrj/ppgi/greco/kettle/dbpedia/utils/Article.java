package br.ufrj.ppgi.greco.kettle.dbpedia.utils;

public class Article {

	private String title;
	private String text;
	
	public Article(String title, String text) {
		this.title=title;
		this.text=text;
	}
	
	public Article() {
	}
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
	
}
