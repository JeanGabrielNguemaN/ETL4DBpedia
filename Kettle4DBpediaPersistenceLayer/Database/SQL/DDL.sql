#DBMS: MySQL 8.0

DROP DATABASE IF EXISTS dbpediaexpresstest;

CREATE SCHEMA `dbpediaexpresstest` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci ;

GRANT ALL PRIVILEGES ON dbpediaexpresstest.* TO 'dbpedia'@'localhost' IDENTIFIED BY 'dbpedia';

USE dbpediaexpresstest;

#SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

#####################################
CREATE TABLE `template`
(
  `id`           INT unsigned NOT NULL AUTO_INCREMENT, # Unique ID for the record
  `templatename`     VARCHAR(200)  NOT NULL UNIQUE,            
  `pageid`       VARCHAR(10), 
  PRIMARY KEY     (`id`)
)ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

#######################
CREATE TABLE templateproperty
(
  id    INT unsigned NOT NULL AUTO_INCREMENT,  
  idtemplate           INT unsigned NOT NULL, 
  templateproperty    VARCHAR(200) NOT NULL,    
  PRIMARY KEY     (id),
  CONSTRAINT FK_templateprop FOREIGN KEY (idtemplate)
  REFERENCES template(id)
)ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

#######################
CREATE TABLE ontologyclass
(
  id    INT unsigned NOT NULL AUTO_INCREMENT,  
  ontologyclass    VARCHAR(200) NOT NULL,    
  PRIMARY KEY     (id)
)ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;


####################### 
CREATE TABLE ontologyproperty
(
  id           INT unsigned NOT NULL AUTO_INCREMENT,
  ontologyproperty     VARCHAR(250),     
  PRIMARY KEY     (id)
)ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

##########################################
CREATE TABLE templatemapping
(
  idtemplate           INT unsigned NOT NULL, # Unique ID for the record
  dmlmapping           TEXT NOT NULL,            
  conditionalmapping BOOLEAN DEFAULT false,
  PRIMARY KEY     (idtemplate ),
  CONSTRAINT FK_template FOREIGN KEY (idtemplate)
  REFERENCES template(id)  
)ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;


##############################################
CREATE TABLE mappedcondition
(
  id           INT unsigned NOT NULL AUTO_INCREMENT, # Unique ID for the record,
  idtemplate    INT unsigned NOT NULL, #pode retirar
  idtemplateproperty INT unsigned, # NOT NULL, 
  idontologyclass INT unsigned NOT NULL, # maptoclass    VARCHAR(120),  
  coperator VARCHAR(120), 
  propertyvalue VARCHAR(120), #campo value
  
  PRIMARY KEY     (id),
  CONSTRAINT FK_mpdcond FOREIGN KEY (idtemplate)
  REFERENCES template(id),
  CONSTRAINT FK_mpdcondtemprop FOREIGN KEY (idtemplateproperty)
  REFERENCES templateproperty(id),
  CONSTRAINT FK_mpdcondontclass FOREIGN KEY (idontologyclass)
  REFERENCES ontologyclass(id)
)ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

############################################
CREATE TABLE mappedclass
(
  id           INT unsigned NOT NULL AUTO_INCREMENT, # Unique ID for the record,
  idtemplate    INT unsigned NOT NULL,
  idontologyclass    INT unsigned NOT NULL,
  PRIMARY KEY     (id),
  CONSTRAINT FK_mpdcl FOREIGN KEY (idtemplate)
  REFERENCES template(id),
  CONSTRAINT FK_mpdclontclass FOREIGN KEY (idontologyclass)
  REFERENCES ontologyclass(id)   
)ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

##########################################
CREATE TABLE mappedproperty
(
  id           INT unsigned NOT NULL AUTO_INCREMENT, # Unique ID for the record,
  idtemplate    INT unsigned NOT NULL,
  idontologyproperty INT unsigned NOT NULL,  
  idtemplateproperty INT unsigned NOT NULL,   
  PRIMARY KEY     (id),
  CONSTRAINT FK_mpdprop FOREIGN KEY (idtemplate)
  REFERENCES template(id),
  CONSTRAINT FK_mpdpropontprop FOREIGN KEY (idontologyproperty)
  REFERENCES ontologyproperty(id),
  CONSTRAINT FK_mpdproptemprop FOREIGN KEY (idtemplateproperty)
  REFERENCES templateproperty(id)
)ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;




##########################################################################################################################

CREATE TABLE geocoordinatemapping
(
  id           INT unsigned NOT NULL AUTO_INCREMENT, # Unique ID for the record,
  idtemplate    INT unsigned NOT NULL,
  #idtemplateproperty INT unsigned NOT NULL,  
  PRIMARY KEY     (id),
  CONSTRAINT FK_mpdprop FOREIGN KEY (idtemplate)
  REFERENCES template(id)#,
  #CONSTRAINT FK_geoctemprop FOREIGN KEY (idtemplateproperty)
  #REFERENCES templateproperty(id)
  
)ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;


#Relacionamentos para armazenar as propriedades
CREATE TABLE geoctemplateproperty
(
  idgeocoordinate           INT unsigned, # Unique ID for the record,
  idtemplateproperty INT unsigned NOT NULL, #  1) coordinates ou 2) latitude ou  3) latitudeDirection
  numberofproperty INT, # 1) 1 ou 2) 2 ou 3) 8.
  PRIMARY KEY     (idgeocoordinate,  idtemplateproperty),
  CONSTRAINT FK_tempprop FOREIGN KEY (idgeocoordinate )
  REFERENCES geocoordinatemapping(id),
  CONSTRAINT FK_geo FOREIGN KEY (idgeocoordinate)
  REFERENCES ontologyproperty(id)
  
)ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;


CREATE TABLE article
(
  id           INT unsigned NOT NULL AUTO_INCREMENT, # Unique ID for the record,
  pageid       VARCHAR(10), 
  content      TEXT,     
  PRIMARY KEY     (id)
)ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
#######################

####################### 10

CREATE TABLE publicationlog
(
  id           INT unsigned NOT NULL AUTO_INCREMENT, 
  idarticle    INT unsigned NOT NULL,
  message      TEXT NOT NULL,     
  endpoint     VARCHAR(120) NOT NULL,
  entrydate    TIMESTAMP NOT NULL,
  botaccount   VARCHAR(80) NOT NULL,
  summary      VARCHAR(120) NOT NULL,
  PRIMARY KEY     (id),
  CONSTRAINT FK_logarticle FOREIGN KEY (idarticle)
  REFERENCES article(id)
)ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

#######################


