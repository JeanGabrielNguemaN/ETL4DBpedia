# ETL4DBpedia
 Framework for publishing data on Wikipedia to serve [Portuguese Edition of DBpedia](http://pt.dbpedia.org/). ETL4DBpedia is a two layers-architecture framework based on  Kettle (Pentaho Data Ingration).
Publication layers' steps are available at the following path:
*  [Publication Layer]( https://github.com/JeanGabrielNguemaN/ETL4DBpedia/tree/master/Kettele4DBpediaPlugins)
## Description
### Persistence Layer
Responsible for managing the data used in the steps of the framework, such as templates and templates mapping in DML (DBpedia Mapping Language) obtaining from Wikipedia and DBpedia Wiki Mapping platform.
Persistence layer's Artifacts and tools are available at the following path:
*  [Persistence Layer]( https://github.com/JeanGabrielNguemaN/ETL4DBpedia/tree/master/Kettle4DBpediaPersistenceLayer)

### Publication Layer
Responsible for transformation of the fo specific domain data and its respective publication on Wikipedia, so that they  result in the data extraction for DBpedia In Portuguese Edition.

### ETL4DBpedia Steps(Plugins)
ETL4DBpedia is currently provided with the following steps (kettle plugins):

* [TemplatesMaintainer]( https://github.com/JeanGabrielNguemaN/ETL4DBpedia/tree/master/Kettele4DBpediaPlugins/TemplatesMaintainer)
* [DBpediaMappingMaintainer]( https://github.com/JeanGabrielNguemaN/ETL4DBpedia/tree/master/Kettele4DBpediaPlugins/DBpediaMappingMaintainer)
* [ArticleChecher]( https://github.com/JeanGabrielNguemaN/ETL4DBpedia/tree/master/Kettele4DBpediaPlugins/ArticleChecher)
* [TemplateSelector]( https://github.com/JeanGabrielNguemaN/ETL4DBpedia/tree/master/Kettele4DBpediaPlugins/TemplateSelector)
* [TemplateMapper]( https://github.com/JeanGabrielNguemaN/ETL4DBpedia/tree/master/Kettele4DBpediaPlugins/TemplateMapper)
* [ArticleContentBuilder ]( https://github.com/JeanGabrielNguemaN/ETL4DBpedia/tree/master/Kettele4DBpediaPlugins/ArticleContentBuilder )
* [ArticlePublisher]( https://github.com/JeanGabrielNguemaN/ETL4DBpedia/tree/master/Kettele4DBpediaPlugins/ArticlePublisher)

## Installation

1.	Download and Install Kettle, Pentaho Data Integration (pdi-ce-8.2.0.0-342 or latest version).

2.	Download and Install MySQL database (8.0 version ). 
    * Create the database by the following queries: ETL4DBpedia/Kettle4DBpediaPersistenceLayer/Database/SQL/DDL.sql
	
3.	Add jars into  PDI_HOME\data-integration\lib directory.
     * MySQL driver (mysql-connector-5.1.18.jar)
     * Jena jars (3.10.0) 
	 
4.	Download, install and settup Maven.

5.	Install KettlePluginTools.jar in maven local repository: From Command Prompt, run a batch maven_install_local_libs.bat from the ETL4DBpedia\Kettele4DBpediaPlugins\libs directory
    ```sh
     $ mvn install:install-file -Dfile=./KettlePluginTools.jar -DgroupId=br.ufrj.ppgi.greco.kettle -DartifactId=KettlePluginTools -Dversion=1.0 -Dpackaging=jar
	```
	Or run:
	 ```sh
     $ ./maven_install_local_libs.bat   
	 ```
	
6.	Adjust  pdi.home property in ETL4DBpedia\pom.xml file.

7.	Build PersitenceLayer plugins:
	```sh
     $ mvn clean package
	```
	
8.	Register persistenceLayer components into directory publication layer:
	* Copy ETL4DBpedia\Kettle4DBpediaPersistenceLayer\target\ Kettle4DBpediaPersistenceLayer-1.0.jar file  to  ETL4DBpedia\Kettele4DBpediaPlugins\libs directory

9.	Install ET4DBpedia in PDI:
	```sh
     $ mvn clean install
	```

