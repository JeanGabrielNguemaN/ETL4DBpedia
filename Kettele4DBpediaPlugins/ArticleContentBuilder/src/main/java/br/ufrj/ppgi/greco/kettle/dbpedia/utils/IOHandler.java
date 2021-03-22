/*
* %W% %E% Jean Gabriel Nguema Ngomo
*
* Copyright 2021 Jean Gabriel Nguema Ngomo
*
*Licensed under the Apache License, Version 2.0 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
*/
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

public final class IOHandler {

	
	public static void save(String filename, StringBuffer fileContent){
		try {
			BufferedWriter bwr = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(filename, 
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
	
	private static void reeadCSV(String filename, String separator){
		try {
			File file = new File(filename);      
		
			int number=0, n=0;

			FileReader fr = new FileReader(file);

															
			//BufferedReader br = new BufferedReader(fr);
			BufferedReader br = new BufferedReader(
					  new InputStreamReader(new FileInputStream(filename), "UTF-8"));
		       
			   
			while (br.ready()) {
				String[] fields=(String[])br.readLine().trim().split(",", 2);
				       
				if(fields[0].equals("URI")){
				    	int k=0;
				 }
				    else{
				    	
			    		
			    		if(fields!=null ){
			    			number++; //cidades
			    			n=fields.length;
			    			//System.out.println("SIZE ="+n);
			    			String municipio=fields[0];
			    			//municipios.add(municipio);
			    			
			    			String comment="";
			    			//if (fields.length==3)
			    				//comment=fields[2];
			    			//else 
			    				if (fields.length==2)
			    				comment=fields[1];
			    			
			    			//comments.add(comment);
			    		}
				    }
		    	}

			System.out.println("City/Settlement DBpedia EN : =>"+number);
			
			} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();														
			} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();														
			} 		
	}
	
}
