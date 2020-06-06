package br.ufrj.ppgi.greco.kettle.dbpedia.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class IOHandler {

	
	public static void save(String filename, String fileContent){
		try {
			
			BufferedWriter bwr = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(repositorydir+filename, 
									false), StandardCharsets.UTF_8));
					
			//write contents of StringBuffer to a file
			bwr.write(fileContent.toString());
			
			//flush the stream
			bwr.flush();
			
			//close the stream
			bwr.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Ler linhas de um arquivo CSV
	 * @param filename
	 * @param separator
	 * @return
	 */
	public static String[] readCSV(String filename, String separator){
		
		ArrayList<String> linhas= new ArrayList<String>();
		
		try {    

			BufferedReader br = new BufferedReader(
					  new InputStreamReader(new FileInputStream(repositorydir+filename), "UTF-8"));
		       
			while (br.ready()) {
				
				linhas.add(br.readLine().trim());
		    }

	         //fechar o arquivo
			 br.close();
			 
			} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();														
			} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();														
			}
		
		return linhas.toArray(new String[0]); 		
	}
		
	
	 private static String repositorydir= "C:\\DBpedia\\Kettle\\pdi-ce-8.2.0.0-342\\data-integration\\plugins\\steps\\etl4lodrepositories\\";
	
}
