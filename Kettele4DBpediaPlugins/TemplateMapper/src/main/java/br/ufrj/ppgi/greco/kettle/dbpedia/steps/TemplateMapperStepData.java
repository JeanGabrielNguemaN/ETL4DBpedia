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

package br.ufrj.ppgi.greco.kettle.dbpedia.steps;

import java.util.HashMap;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

/**
* This class is responsable to store processing state, 
* and to declare and serve as a place for field variables during row processing
* 
* @version 1.01 03 Mar 2021
* @author Jean Gabriel Nguema Ngomo
*/
public class TemplateMapperStepData extends BaseStepData implements StepDataInterface {

	public RowMetaInterface outputRowMeta;
	public HashMap<String, Integer> fieldsIndexesHashMap=null;
	
	public int[] inputFieldIndex=null;
	public String mappings=null;
	public String mappingsField=null;
	public String[] mappedTemplateProperties=null;
	public Integer templateFieldIndex=-1;	
}
