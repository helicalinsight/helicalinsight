## Helical Insight

> Community driven innovation via Helical Insight CE.

## What is it

Helical Insight is world's first Open Source Business Intelligence framework which can help you derive insights out of your one or multiple datasources. Helical Insight is having all the features which you generally expect out of any BI tool (be it open source or proprietary). Being a framework, Helical Insight is highly extensible via APIs and SDK, thus features can be extended whenever required without compromising on your business requirement. 

Helical Insight also comes with a unique Workflow rule engine, allowing you to call any functionality of Helical Insight or external functionality and thus empowering you to implement any sort of custom business process.

Use HTML skillset and Java skillset to add functionalities at the frontend and backend respectively.
  
See the [Quick start](https://helicalinsight.github.io/helicalinsight/#/quickstart) for more details.

## Features

* New generation UI with one click access
* Backend EFW method of reports, dashboards and other data analysis creation
* User Role Management
* Exporting to Multiple Formats
* Email scheduling
* Data Security
* XML driven Workflow 
* API Driven Framework
* Community Support
* Community Upgrades
* Direct links to tutorials
* Mobile & Cloud compatable
* Cache for faster Performance
* Compatible with All Modern Browsers


## Supported Databases

We support all the JDBC4 complaint datbases, NoSQL, Big Data, RDBMS, Cloud db, Columnar database etc

#### RDBMS

* Mysql
* PostgreSQL
* SQL Server
* Oracle
* Firebird
* Informix
* Ingres
* MariaDB
* Presto
* Progress
* SQlite

#### NoSQL & Big Data

* Cassandra
* Druid
* HBase
* MongoDb
* Hive
* NuoDB
* Neo4j
* Druid

#### Cloud

* Microsoft Azure SQL
* Amazon RedShift Database
* Google Cloud Sql

#### Flat Files

* CSV
* TSV
* JSON



## How to build ?
 
To build this project you need to have 
* `Maven 3` or higher installed.
* `Java 1.7` higher
* Database created with name `hice`


1. Download this project
2. Change the variables in the `pom.xml`  present in hi-ce module
```text
		<systemDirectory>path/to/SystemDirectory</systemDirectory>
                <logLocation>path/to/log/folder</logLocation>
                <dbUser>database-user-name</dbUser>
                <dbPassword>database-password</dbPassword>
                <dbServer>database-server-host</dbServer>
                <dbPort>database-port</dbPort>
                <dbName>hice</dbName>
    
    eg:
		<systemDirectory>E:/hi-repository</systemDirectory> 
		<!--This is the path which points to the hi-repository folder present with the download-->
                <logLocation>E:/logs</logLocation> <!--log location-->
                <dbUser>hiuser</dbUser>
                <dbPassword>hiuser</dbPassword>
                <dbServer>localhost</dbServer>
                <dbPort>3306</dbPort>
                <dbName>hice</dbName>
```

3. Run the command 

```text
  mvn clean package -Denv=production
```



## Directly deploy
You may also directly deploy  the `hi-ce.war` file in the application server from the `hi-ce/target` module
You need `tomcat` or any other server. Please follow the instructions given [here](https://helicalinsight.github.io/helicalinsight/#/quickstart?id=manual-installation)









## Application Screenshots

![CommunityEdition](docs/_media/screens/login.png) 

![CommunityEdition](docs/_media/screens/welcome.png) 

![CommunityEdition](docs/_media/screens/data_sources.png) 

![CommunityEdition](docs/_media/screens/reports.png) 

![CommunityEdition](docs/_media/screens/file_browser.png)  
#### Sample Report
![CommunityEdition](docs/_media/screens/sample_report.png) 
#### Admin Page
![CommunityEdition](docs/_media/screens/admin.png)  


## Examples

Check out the Demos to Helical Insight in use.

* [Industry specific demo](http://www.helicalinsight.com/industry-specific-demo/) 

* [Job functions specific demo](http://www.helicalinsight.com/job-functions-specific-demo/) 

* [Miscellaneous Use Cases](http://www.helicalinsight.com/miscellaneous-use-cases/) 
 

## Try Enterprise Edition

Helical Insight Enterprise Edition comes with many addon features like self service interface for reports and dashboards creation, multitenancy, machine learning and NLP, UI driven workflow etc.

Try Now [Download Free Trial.](http://www.helicalinsight.com/register/)



## License

Copyright (c) Helical Insight. All rights reserved.

Licensed under the  Apache License.
