package br.ufrj.ppgi.greco.kettle.dbpedia.registration;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Article;

public class RegisterDemo {

	  public static void main(String[] args) {
  		
	    	
			test();
	    }

	public static void test() {
		
		PublicationRegister  register=new PublicationRegister();
    	
    	String articleContent="Article content 2";
    				
    	String message="Message 2";
    	
    	String endpoint="Endpoint 2";	
    	
    	Timestamp 	entrydatetime = new Timestamp(System.currentTimeMillis());
     				        
    	String pageid="111";
		String summary="summary";
		String botaccount="bot";
		
		//register.notifyCreation(articleContent, pageid, summary, message, endpoint, botaccount, entrydatetime); 
		
		try {
			Timestamp t = new Timestamp(provideDateFormat().parse("2020-04-15T03:04:52.001Z").getTime());
			
			System.out.println("Time :"+t);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
public static void test2() {
		
		PublicationRegister  register=new PublicationRegister();
    	
    	String articleContent="Article content 2";
    				
    	String message="Message 2";
    	
    	String endpoint="Endpoint 2";	
    	
    	Timestamp 	entrydatetime = new Timestamp(System.currentTimeMillis());
     				        
    	String pageid="111";
		String summary="summary";
		String botaccount="bot";
		
		//register.notifyCreation(articleContent, pageid, summary, message, endpoint, botaccount, entrydatetime); 
		
	
	}


	   static String ISO_DATE_FORMAT_ZERO_OFFSET = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	    static String UTC_TIMEZONE_NAME = "UTC";

	    static SimpleDateFormat provideDateFormat() {
	      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO_DATE_FORMAT_ZERO_OFFSET);
	      simpleDateFormat.setTimeZone(TimeZone.getTimeZone(UTC_TIMEZONE_NAME));
	      return simpleDateFormat;
	    }
	  
}
