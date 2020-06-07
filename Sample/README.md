# ETL4DBpedia
 Framework for publishing data on Wikipedia to serve [Portuguese Edition of DBpedia](http://pt.dbpedia.org/). ETL4DBpedia is a two layers-architecture framework based on  Kettle (Pentaho Data Ingration).
 * Author: Jean Gabriel Nguema Ngomo (mvojgnn@gmail.com).
 * Licence: Apache License, Version 2.0.

## Sample
This sample shows a typical example of ETL4DBpedia using. Prerequisites: ETL4DBpedia installed on Kettle.
1. Run the persistence scenario in Kettle (Transf_persistence_scenario_V03.ktr).

2. Run the publication scenario. This scenario uses CBPM data from Fiocruz(Transf_CBPM_Fiocruz_V03.ktr). If you want, adjust and settup it according to your data. The execution requires a bot user with approved authorization for publication on Wikipedia/DBpedia.Despite this, you can use ETL4DBpedia without the publishing step.

