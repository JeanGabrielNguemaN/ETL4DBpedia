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

package br.ufrj.ppgi.greco.dbpedia.service;

public class CandidateClass implements Comparable<Object>{

	private String classname;
	private double numberOfInstance=0;
	
	public  CandidateClass(String name, double number){
		this.classname=name;
		this.numberOfInstance=number;
	}
	
	
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public double getNumberOfInstance() {
		return numberOfInstance;
	}
	public void setNumberOfInstance(int numberOfInstance) {
		this.numberOfInstance = numberOfInstance;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((classname == null) ? 0 : classname.hashCode());
		result = prime * result + (int)numberOfInstance;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		CandidateClass other = (CandidateClass) obj;
		if (classname == null) {
			if (other.classname != null)
				return false;
		} else if (!classname.equals(other.classname))
			return false;
		if (numberOfInstance != other.numberOfInstance)
			return false;
		return true;
	}
	@Override
	public  int compareTo(Object o) {
		// TODO Auto-generated method stub
		
		
		CandidateClass other= (CandidateClass)o;
		
		if (this!=null && other==null ){
			return -1;
		}
		if (this!=null && other!=null){
			if (this.getNumberOfInstance()== other.getNumberOfInstance()){
		
				return 0;
			}
		
			if (this.getNumberOfInstance()> other.getNumberOfInstance()){
				return -1;	
			}
			
			return 1; 
		}
		 return 1; 
	}
	
}
