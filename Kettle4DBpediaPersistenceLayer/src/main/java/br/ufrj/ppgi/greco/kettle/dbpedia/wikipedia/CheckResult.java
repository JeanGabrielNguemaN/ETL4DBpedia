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
