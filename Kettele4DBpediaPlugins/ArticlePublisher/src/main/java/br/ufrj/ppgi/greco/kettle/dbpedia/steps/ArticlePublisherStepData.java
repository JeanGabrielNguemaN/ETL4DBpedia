/*
 * Copyright 2020 Jean Gabriel Nguema Ngomo

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.ufrj.ppgi.greco.kettle.dbpedia.steps;


import java.util.HashMap;
import java.util.HashSet;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

import br.ufrj.ppgi.greco.kettle.dbpedia.utils.ContentPublisher;
import br.ufrj.ppgi.greco.kettle.dbpedia.wikipedia.Entity;


/**
* This class is responsable to store processing state, 
* and to declare and serve as a place for field variables during row processing
* 
* @version 1.01 03 Mar 2021
* @author Jean Gabriel Nguema Ngomo
*/
public class ArticlePublisherStepData extends BaseStepData implements StepDataInterface {
	
	public RowMetaInterface outputRowMeta;

	public int inputRowSize;

	public String properttyForTitle=null;
	
	public boolean titleUsedInInfoboxComparison=false;
	
	//propriedades separadas por virgulas
	public String[] selectedMappedProperties=null;

	public HashMap<String, Integer> fieldsIndexesHashMap;

	public HashSet<Entity> entities=null;
	
	
	//NOVO
	public ContentPublisher publisher=null;
	
	
	//quantidade de edições por minuno 
	// 3 para test, e 5 para produção
	int epm=3;



	public String summary=null;

	public String referenceUrl=null;

	public String referenceTitle=null;

	public String referencePublisher=null;

	public String referenceAccessdate=null;

	public String referenceAuthor=null;

	public String referenceDate=null;

	public boolean isTestPhase=true;
	
		
}
