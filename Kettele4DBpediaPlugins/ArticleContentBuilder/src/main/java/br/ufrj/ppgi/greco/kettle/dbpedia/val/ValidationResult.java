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

package br.ufrj.ppgi.greco.kettle.dbpedia.val;

public class ValidationResult {

	private boolean isValid=false;
	private String derivedValue=null;
	
	public ValidationResult(boolean b, String value) {
		setValid(b);
		setDerivedValue(value);
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public String getValue() {
		return derivedValue;
	}

	public void setDerivedValue(String derivedValue) {
		this.derivedValue = derivedValue;
	}
	
}
