# ETL4DBpedia
 Framework for publishing data on Wikipedia to serve [Portuguese Edition of DBpedia](http://pt.dbpedia.org/). ETL4DBpedia is a two layers-architecture framework based on  Kettle (Pentaho Data Ingration).

## Persistence Layer
Responsible for managing the data used in the steps of the framework, such as templates and templates mapping in DML (DBpedia Mapping Language) obtaining from Wikipedia and DBpedia Wiki Mapping platform. 

## Publication Layer
Responsible for transformation of the fo specific domain data and its respective publication on Wikipedia, so that they  result in the data extraction for DBpedia In Portuguese Edition.
