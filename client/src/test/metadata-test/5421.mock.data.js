const mockStore = {
    metadata: {
      dataFetchedFor: {
        getDatasource: true,
        joins: false,
        viewSessionVariables: false,
        listDataSources: true,
        'b83p-y5rg-7dyr-glpv-z9': false,
        'a8na-05qh-5cei-jjin-6y': false
      },
      loadingStatus: {
        getDatasource: true,
        listDataSources: true,
        'b83p-y5rg-7dyr-glpv-z9': false,
        'a8na-05qh-5cei-jjin-6y': false
      },
      serviceErrorStatus: {},
      fetchedDSInfo: {},
      listDataSource: [],
      supportedDataSourceTypes: [
        {
          name: 'Access',
          categoryType: 'supported',
          categoryName: 'Supported'
        },
        {
          name: 'Amazon Dynamodb',
          categoryType: 'supported',
          categoryName: 'Supported'
        },
        {
          name: 'Amazon Redshift',
          categoryType: 'supported',
          categoryName: 'Supported'
        },
        {
          name: 'Dremio',
          categoryType: 'supported',
          categoryName: 'Supported'
        },
        {
          name: 'Google Bigquery',
          categoryType: 'supported',
          categoryName: 'Supported'
        },
        {
          name: 'IBM Db2',
          categoryType: 'supported',
          categoryName: 'Supported'
        },
        {
          name: 'Informix',
          categoryType: 'supported',
          categoryName: 'Supported'
        },
        {
          name: 'Microsoft Sqlserver',
          categoryType: 'supported',
          categoryName: 'Supported'
        },
        {
          name: 'Oracle',
          categoryType: 'supported',
          categoryName: 'Supported'
        },
        {
          name: 'Presto',
          categoryType: 'supported',
          categoryName: 'Supported'
        },
        {
          name: 'Teradata',
          categoryType: 'supported',
          categoryName: 'Supported'
        },
        {
          name: 'Ξ Add Driver Ξ',
          categoryType: 'supported',
          categoryName: 'Supported'
        }
      ],
      allDataSourceTypes: [
        {
          name: 'Access',
          categoryType: 'supported',
          categoryName: 'Supported'
        },
        {
          name: 'Amazon Dynamodb',
          categoryType: 'supported',
          categoryName: 'Supported'
        },
        {
          name: 'Amazon Redshift',
          categoryType: 'supported',
          categoryName: 'Supported'
        },
        {
          driver: 'org.apache.drill.jdbc.Driver',
          databaseDialect: 'drill',
          enabledTypes: false,
          name: 'Apache Drill',
          categoryName: 'Big Data',
          categoryType: 'big_data',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:drill:{{hostName}}:{{port}}',
          parameters: {
            port: '31010',
            hostName: 'drillbit=localhost'
          }
        },
        {
          driver: 'com.simba.athena.jdbc.Driver',
          databaseDialect: 'athena',
          name: 'Athena',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:awsathena://{{hostName}}',
          parameters: {
            hostName: 'localhost'
          }
        },
        {
          driver: 'ru.yandex.clickhouse.ClickHouseDriver',
          databaseDialect: 'clickhouse',
          name: 'Clickhouse',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:clickhouse://{{hostName}}:{{port}}/{{database}}',
          parameters: {
            port: '8123',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          driver: 'com.databricks.client.jdbc.Driver',
          databaseDialect: 'databricks',
          name: 'Databricks Client',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:databricks://{{hostName}}:{{port}};{{database}}',
          parameters: {
            port: '443',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          driver: 'org.apache.derby.jdbc.ClientDriver',
          databaseDialect: 'derby',
          name: 'Derby',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:derby://{{hostName}}:{{port}}/{{database}}',
          parameters: {
            port: '1527',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          driver: 'org.apache.derby.jdbc.AutoloadedDriver',
          databaseDialect: 'derby',
          name: 'Derby',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:derby:{{database}}',
          parameters: {
            database: 'database'
          }
        },
        {
          name: 'Dremio',
          categoryType: 'supported',
          categoryName: 'Supported'
        },
        {
          driver: 'dynamicSwitch',
          databaseDialect: '',
          name: 'DynamicSwitch',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png'
        },
        {
          name: 'Google Bigquery',
          categoryType: 'supported',
          categoryName: 'Supported'
        },
        {
          driver: 'com.helical.mongodb.MongoJdbcDriver',
          databaseDialect: 'himongo',
          name: 'Helical Mongodb',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png'
        },
        {
          driver: 'org.apache.hive.jdbc.HiveDriver',
          databaseDialect: 'spark',
          name: 'Hive',
          categoryName: 'Big Data',
          categoryType: 'big_data',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:hive2://{{hostName}}:{{port}}/{{database}}',
          parameters: {
            port: '10001',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          name: 'IBM Db2',
          categoryType: 'supported',
          categoryName: 'Supported'
        },
        {
          name: 'Informix',
          categoryType: 'supported',
          categoryName: 'Supported'
        },
        {
          driver: 'org.mariadb.jdbc.Driver',
          databaseDialect: 'mysql',
          name: 'Mariadb',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:mariadb://{{hostName}}:{{port}}/{{database}}',
          parameters: {
            port: '3306',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          name: 'Microsoft Sqlserver',
          categoryType: 'supported',
          categoryName: 'Supported'
        },
        {
          driver: 'net.sourceforge.jtds.jdbc.Driver',
          databaseDialect: 'sqlserver',
          name: 'Microsoft Sqlserver(sourceforge)',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:jtds:sqlserver://{{hostName}}:{{port}}/{{database}}',
          parameters: {
            port: '1433',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          driver: 'com.mysql.jdbc.Driver',
          databaseDialect: 'mysql',
          name: 'Mysql',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:mysql://{{hostName}}:{{port}}/{{database}}',
          parameters: {
            port: '3306',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          driver: 'com.mysql.cj.jdbc.Driver',
          databaseDialect: 'mysql',
          name: 'Mysql Cj',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:mysql://{{hostName}}:{{port}}/{{database}}',
          parameters: {
            port: '3306',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          name: 'Oracle',
          categoryType: 'supported',
          categoryName: 'Supported'
        },
        {
          driver: 'org.postgresql.Driver',
          databaseDialect: 'postgresql',
          name: 'Postgresql',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:postgresql://{{hostName}}:{{port}}/{{database}}',
          parameters: {
            port: '5432',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          name: 'Presto',
          categoryType: 'supported',
          categoryName: 'Supported'
        },
        {
          driver: 'org.sqlite.JDBC',
          databaseDialect: 'sqlite',
          name: 'Sqlite',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:sqlite:{{database}}',
          parameters: {
            database: 'database'
          }
        },
        {
          name: 'Teradata',
          categoryType: 'supported',
          categoryName: 'Supported'
        },
        {
          name: 'Ξ Add Driver Ξ',
          categoryType: 'supported',
          categoryName: 'Supported'
        }
      ],
      metaDataSourceList: [
        {
          driver: 'org.apache.drill.jdbc.Driver',
          databaseDialect: 'drill',
          enabledTypes: false,
          name: 'Apache Drill',
          categoryName: 'Big Data',
          categoryType: 'big_data',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:drill:{{hostName}}:{{port}}',
          parameters: {
            port: '31010',
            hostName: 'drillbit=localhost'
          }
        },
        {
          driver: 'com.simba.athena.jdbc.Driver',
          databaseDialect: 'athena',
          name: 'Athena',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:awsathena://{{hostName}}',
          parameters: {
            hostName: 'localhost'
          }
        },
        {
          driver: 'ru.yandex.clickhouse.ClickHouseDriver',
          databaseDialect: 'clickhouse',
          name: 'Clickhouse',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:clickhouse://{{hostName}}:{{port}}/{{database}}',
          parameters: {
            port: '8123',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          driver: 'com.databricks.client.jdbc.Driver',
          databaseDialect: 'databricks',
          name: 'Databricks Client',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:databricks://{{hostName}}:{{port}};{{database}}',
          parameters: {
            port: '443',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          driver: 'org.apache.derby.jdbc.ClientDriver',
          databaseDialect: 'derby',
          name: 'Derby',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:derby://{{hostName}}:{{port}}/{{database}}',
          parameters: {
            port: '1527',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          driver: 'org.apache.derby.jdbc.AutoloadedDriver',
          databaseDialect: 'derby',
          name: 'Derby',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:derby:{{database}}',
          parameters: {
            database: 'database'
          }
        },
        {
          driver: 'dynamicSwitch',
          databaseDialect: '',
          name: 'DynamicSwitch',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png'
        },
        {
          driver: 'com.helical.mongodb.MongoJdbcDriver',
          databaseDialect: 'himongo',
          name: 'Helical Mongodb',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png'
        },
        {
          driver: 'org.apache.hive.jdbc.HiveDriver',
          databaseDialect: 'spark',
          name: 'Hive',
          categoryName: 'Big Data',
          categoryType: 'big_data',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:hive2://{{hostName}}:{{port}}/{{database}}',
          parameters: {
            port: '10001',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          driver: 'org.mariadb.jdbc.Driver',
          databaseDialect: 'mysql',
          name: 'Mariadb',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:mariadb://{{hostName}}:{{port}}/{{database}}',
          parameters: {
            port: '3306',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          driver: 'net.sourceforge.jtds.jdbc.Driver',
          databaseDialect: 'sqlserver',
          name: 'Microsoft Sqlserver(sourceforge)',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:jtds:sqlserver://{{hostName}}:{{port}}/{{database}}',
          parameters: {
            port: '1433',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          driver: 'com.mysql.jdbc.Driver',
          databaseDialect: 'mysql',
          name: 'Mysql',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:mysql://{{hostName}}:{{port}}/{{database}}',
          parameters: {
            port: '3306',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          driver: 'com.mysql.cj.jdbc.Driver',
          databaseDialect: 'mysql',
          name: 'Mysql Cj',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:mysql://{{hostName}}:{{port}}/{{database}}',
          parameters: {
            port: '3306',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          driver: 'org.postgresql.Driver',
          databaseDialect: 'postgresql',
          name: 'Postgresql',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:postgresql://{{hostName}}:{{port}}/{{database}}',
          parameters: {
            port: '5432',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          driver: 'org.sqlite.JDBC',
          databaseDialect: 'sqlite',
          name: 'Sqlite',
          categoryName: 'RDBMS',
          categoryType: 'rdbms',
          type: 'global.jdbc',
          dataSourceProvider: 'tomcat',
          classifier: 'global',
          imgUrl: '../images/data_sources/defaut_datasource.png',
          url: 'jdbc:sqlite:{{database}}',
          parameters: {
            database: 'database'
          }
        }
      ],
      driversList: [
        {
          driver: 'dynamicSwitch',
          showInDatasource: 'false',
          available: 'true',
          parameters: []
        },
        {
          url: 'jdbc:derby://{{hostName}}:{{port}}/{{database}}',
          driver: 'org.apache.derby.jdbc.ClientDriver',
          available: 'true',
          parameters: {
            port: '1527',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          url: 'jdbc:derby:{{database}}',
          driver: 'org.apache.derby.jdbc.AutoloadedDriver',
          available: 'true',
          parameters: {
            database: 'database'
          }
        },
        {
          url: 'jdbc:mysql://{{hostName}}:{{port}}/{{database}}',
          driver: 'com.mysql.cj.jdbc.Driver',
          available: 'true',
          parameters: {
            port: '3306',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          url: 'jdbc:mysql://{{hostName}}:{{port}}/{{database}}',
          driver: 'com.mysql.jdbc.Driver',
          available: 'true',
          parameters: {
            port: '3306',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          url: 'jdbc:awsathena://{{hostName}}',
          driver: 'com.simba.athena.jdbc.Driver',
          available: 'true',
          parameters: {
            hostName: 'localhost'
          }
        },
        {
          url: 'jdbc:jtds:sqlserver://{{hostName}}:{{port}}/{{database}}',
          driver: 'net.sourceforge.jtds.jdbc.Driver',
          available: 'true',
          parameters: {
            port: '1433',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          url: 'jdbc:clickhouse://{{hostName}}:{{port}}/{{database}}',
          driver: 'ru.yandex.clickhouse.ClickHouseDriver',
          available: 'true',
          parameters: {
            port: '8123',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          available: 'true',
          driver: 'com.helical.mongodb.MongoJdbcDriver'
        },
        {
          url: 'jdbc:hive2://{{hostName}}:{{port}}/{{database}}',
          driver: 'org.apache.hive.jdbc.HiveDriver',
          available: 'true',
          parameters: {
            port: '10001',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          url: 'jdbc:drill:{{hostName}}:{{port}}',
          driver: 'org.apache.drill.jdbc.Driver',
          available: 'true',
          parameters: {
            port: '31010',
            hostName: 'drillbit=localhost'
          }
        },
        {
          url: 'jdbc:mariadb://{{hostName}}:{{port}}/{{database}}',
          driver: 'org.mariadb.jdbc.Driver',
          available: 'true',
          parameters: {
            port: '3306',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          url: 'jdbc:databricks://{{hostName}}:{{port}};{{database}}',
          driver: 'com.databricks.client.jdbc.Driver',
          available: 'true',
          parameters: {
            port: '443',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          url: 'jdbc:postgresql://{{hostName}}:{{port}}/{{database}}',
          driver: 'org.postgresql.Driver',
          available: 'true',
          parameters: {
            port: '5432',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          url: 'jdbc:derby://{{hostName}}:{{port}}/{{database}}',
          driver: 'org.apache.derby.jdbc.ClientDriver',
          available: 'true',
          parameters: {
            port: '1527',
            hostName: 'localhost',
            database: 'database'
          }
        },
        {
          url: 'jdbc:sqlite:{{database}}',
          driver: 'org.sqlite.JDBC',
          available: 'true',
          parameters: {
            database: 'database'
          }
        }
      ],
      allDataSources: [
        {
          data: {
            id: '1',
            type: 'dynamicDataSource'
          },
          dataSourceProvider: 'tomcat',
          type: 'dynamicDataSource',
          permissionLevel: 5,
          driver: 'org.apache.derby.jdbc.ClientDriver',
          name: 'SampleTravelDataDerby',
          classifier: 'global',
          dataSourceType: 'Managed DataSource'
        },
        {
          data: {
            id: '1000',
            type: 'dynamicDataSource'
          },
          dataSourceProvider: 'tomcat',
          type: 'dynamicDataSource',
          permissionLevel: 5,
          driver: 'org.sqlite.JDBC',
          name: 'SqlLite',
          classifier: 'global',
          dataSourceType: 'Managed DataSource'
        },
        {
          data: {
            id: '1001',
            type: 'dynamicDataSource'
          },
          dataSourceProvider: 'tomcat',
          type: 'dynamicDataSource',
          permissionLevel: 5,
          driver: 'com.mysql.jdbc.Driver',
          name: ' Mysql',
          classifier: 'global',
          dataSourceType: 'Managed DataSource'
        }
      ],
      dataSourceTypes: [
        {
          type: 'sql.jdbc.groovy.managed',
          name: 'Groovy Managed Jdbc DataSource',
          classifier: 'efwd',
          categoryName: 'advanced',
          categoryType: 'advanced'
        },
        {
          type: 'sql.jdbc.groovy',
          name: 'Groovy Plain Jdbc DataSource',
          classifier: 'efwd',
          categoryName: 'advanced',
          categoryType: 'advanced'
        },
        {
          type: 'global.jdbc',
          name: 'Managed DataSource',
          classifier: 'global',
          categoryName: 'advanced',
          categoryType: 'advanced'
        },
        {
          type: 'sql.jdbc',
          name: 'Plain Jdbc DataSource',
          classifier: 'efwd',
          categoryName: 'advanced',
          categoryType: 'advanced'
        }
      ],
      datasourceListToRender: [
        {
          name: 'Derby',
          children: [
            {
              data: {
                id: '1',
                type: 'dynamicDataSource'
              },
              dataSourceProvider: 'tomcat',
              type: 'dynamicDataSource',
              permissionLevel: 5,
              driver: 'org.apache.derby.jdbc.ClientDriver',
              name: 'SampleTravelDataDerby',
              classifier: 'global',
              dataSourceType: 'Managed DataSource',
              category: 'dataSource',
              children: [
                {
                  name: 'SQLJ',
                  children: [],
                  keyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/spea-gax9-cmek-5r0x-7j',
                  key: 'spea-gax9-cmek-5r0x-7j',
                  uuid: 'spea-gax9-cmek-5r0x-7j',
                  data: {
                    id: '1',
                    type: 'dynamicDataSource'
                  },
                  category: 'schema',
                  datasourceName: 'SampleTravelDataDerby'
                },
                {
                  name: 'SYSFUN',
                  children: [],
                  keyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/9bcr-u1vx-1exm-m83u-0i',
                  key: '9bcr-u1vx-1exm-m83u-0i',
                  uuid: '9bcr-u1vx-1exm-m83u-0i',
                  data: {
                    id: '1',
                    type: 'dynamicDataSource'
                  },
                  category: 'schema',
                  datasourceName: 'SampleTravelDataDerby'
                },
                {
                  name: 'SYSCAT',
                  children: [],
                  keyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/ct84-hh4x-tlqf-6sri-h1',
                  key: 'ct84-hh4x-tlqf-6sri-h1',
                  uuid: 'ct84-hh4x-tlqf-6sri-h1',
                  data: {
                    id: '1',
                    type: 'dynamicDataSource'
                  },
                  category: 'schema',
                  datasourceName: 'SampleTravelDataDerby'
                },
                {
                  name: 'HIUSER',
                  children: [
                    {
                      id: '4ac5d9f68b58bd7c0d179146e46795be',
                      name: 'dimdate',
                      data: {
                        id: '1',
                        type: 'dynamicDataSource'
                      },
                      keyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/a8na-05qh-5cei-jjin-6y/o29a-1g2t-84cz-ah6z-rm',
                      key: 'o29a-1g2t-84cz-ah6z-rm',
                      alias: 'dimdate',
                      uuid: 'o29a-1g2t-84cz-ah6z-rm',
                      connId: 'n8tju',
                      dataSource: {
                        id: '1',
                        type: 'dynamicDataSource',
                        baseType: 'global.jdbc',
                        catSchemaPredicted: false,
                        sync: false,
                        catalog: '',
                        schema: 'HIUSER',
                        connId: 'n8tju',
                        classifier: 'db.workflow',
                        datasourceName: 'SampleTravelDataDerby',
                        dsKeyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/a8na-05qh-5cei-jjin-6y',
                        driverType: 'Derby',
                        database: 'HIUSER'
                      },
                      dataSourceName: 'SampleTravelDataDerby',
                      category: 'table',
                      nameWithConnId: 'dimdate_n8tju',
                      database: 'HIUSER',
                      schema: 'HIUSER',
                      selected: true
                    },
                    {
                      id: '4e1fd245f4d13b77be423a43f01d80b2',
                      name: 'employee_details',
                      data: {
                        id: '1',
                        type: 'dynamicDataSource'
                      },
                      keyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/a8na-05qh-5cei-jjin-6y/rlls-2qov-pxtl-ysgq-lo',
                      key: 'rlls-2qov-pxtl-ysgq-lo',
                      alias: 'employee_details',
                      uuid: 'rlls-2qov-pxtl-ysgq-lo',
                      connId: 'n8tju',
                      dataSource: {
                        id: '1',
                        type: 'dynamicDataSource',
                        baseType: 'global.jdbc',
                        catSchemaPredicted: false,
                        sync: false,
                        catalog: '',
                        schema: 'HIUSER',
                        connId: 'n8tju',
                        classifier: 'db.workflow',
                        datasourceName: 'SampleTravelDataDerby',
                        dsKeyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/a8na-05qh-5cei-jjin-6y',
                        driverType: 'Derby',
                        database: 'HIUSER'
                      },
                      dataSourceName: 'SampleTravelDataDerby',
                      category: 'table',
                      nameWithConnId: 'employee_details_n8tju',
                      database: 'HIUSER',
                      schema: 'HIUSER',
                      selected: true
                    },
                    {
                      id: 'be534112989b616b194bc59c2fb25a42',
                      name: 'geo_cordinates',
                      data: {
                        id: '1',
                        type: 'dynamicDataSource'
                      },
                      keyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/a8na-05qh-5cei-jjin-6y/li0s-xj3b-4efs-5mrz-z2',
                      key: 'li0s-xj3b-4efs-5mrz-z2',
                      alias: 'geo_cordinates',
                      uuid: 'li0s-xj3b-4efs-5mrz-z2',
                      connId: 'n8tju',
                      dataSource: {
                        id: '1',
                        type: 'dynamicDataSource',
                        baseType: 'global.jdbc',
                        catSchemaPredicted: false,
                        sync: false,
                        catalog: '',
                        schema: 'HIUSER',
                        connId: 'n8tju',
                        classifier: 'db.workflow',
                        datasourceName: 'SampleTravelDataDerby',
                        dsKeyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/a8na-05qh-5cei-jjin-6y',
                        driverType: 'Derby',
                        database: 'HIUSER'
                      },
                      dataSourceName: 'SampleTravelDataDerby',
                      category: 'table',
                      nameWithConnId: 'geo_cordinates_n8tju',
                      database: 'HIUSER',
                      schema: 'HIUSER',
                      selected: true
                    },
                    {
                      id: '9645c648a1c0dbeec1287aaf1e996db3',
                      name: 'meeting_details',
                      data: {
                        id: '1',
                        type: 'dynamicDataSource'
                      },
                      keyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/a8na-05qh-5cei-jjin-6y/43cy-8iyh-wue4-zb8j-ia',
                      key: '43cy-8iyh-wue4-zb8j-ia',
                      alias: 'meeting_details',
                      uuid: '43cy-8iyh-wue4-zb8j-ia',
                      connId: 'n8tju',
                      dataSource: {
                        id: '1',
                        type: 'dynamicDataSource',
                        baseType: 'global.jdbc',
                        catSchemaPredicted: false,
                        sync: false,
                        catalog: '',
                        schema: 'HIUSER',
                        connId: 'n8tju',
                        classifier: 'db.workflow',
                        datasourceName: 'SampleTravelDataDerby',
                        dsKeyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/a8na-05qh-5cei-jjin-6y',
                        driverType: 'Derby',
                        database: 'HIUSER'
                      },
                      dataSourceName: 'SampleTravelDataDerby',
                      category: 'table',
                      nameWithConnId: 'meeting_details_n8tju',
                      database: 'HIUSER',
                      schema: 'HIUSER',
                      selected: true
                    },
                    {
                      id: '8a28627d07d04ef096d9935f12e0c7e9',
                      name: 'travel_details',
                      data: {
                        id: '1',
                        type: 'dynamicDataSource'
                      },
                      keyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/a8na-05qh-5cei-jjin-6y/nt9h-7rtu-cfut-37sj-zz',
                      key: 'nt9h-7rtu-cfut-37sj-zz',
                      alias: 'travel_details',
                      uuid: 'nt9h-7rtu-cfut-37sj-zz',
                      connId: 'n8tju',
                      dataSource: {
                        id: '1',
                        type: 'dynamicDataSource',
                        baseType: 'global.jdbc',
                        catSchemaPredicted: false,
                        sync: false,
                        catalog: '',
                        schema: 'HIUSER',
                        connId: 'n8tju',
                        classifier: 'db.workflow',
                        datasourceName: 'SampleTravelDataDerby',
                        dsKeyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/a8na-05qh-5cei-jjin-6y',
                        driverType: 'Derby',
                        database: 'HIUSER'
                      },
                      dataSourceName: 'SampleTravelDataDerby',
                      category: 'table',
                      nameWithConnId: 'travel_details_n8tju',
                      database: 'HIUSER',
                      schema: 'HIUSER',
                      selected: true
                    }
                  ],
                  keyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/a8na-05qh-5cei-jjin-6y',
                  key: 'a8na-05qh-5cei-jjin-6y',
                  uuid: 'a8na-05qh-5cei-jjin-6y',
                  data: {
                    id: '1',
                    type: 'dynamicDataSource'
                  },
                  category: 'schema',
                  datasourceName: 'SampleTravelDataDerby',
                  fetched: true
                },
                {
                  name: 'SYSCS_DIAG',
                  children: [],
                  keyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/jinu-x22k-tjff-9zf9-58',
                  key: 'jinu-x22k-tjff-9zf9-58',
                  uuid: 'jinu-x22k-tjff-9zf9-58',
                  data: {
                    id: '1',
                    type: 'dynamicDataSource'
                  },
                  category: 'schema',
                  datasourceName: 'SampleTravelDataDerby'
                },
                {
                  name: 'SYSCS_UTIL',
                  children: [],
                  keyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/izq6-n05p-1rcj-oa79-bl',
                  key: 'izq6-n05p-1rcj-oa79-bl',
                  uuid: 'izq6-n05p-1rcj-oa79-bl',
                  data: {
                    id: '1',
                    type: 'dynamicDataSource'
                  },
                  category: 'schema',
                  datasourceName: 'SampleTravelDataDerby'
                },
                {
                  name: 'SYSIBM',
                  children: [],
                  keyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/4va1-0185-iqho-aurk-hg',
                  key: '4va1-0185-iqho-aurk-hg',
                  uuid: '4va1-0185-iqho-aurk-hg',
                  data: {
                    id: '1',
                    type: 'dynamicDataSource'
                  },
                  category: 'schema',
                  datasourceName: 'SampleTravelDataDerby'
                },
                {
                  name: 'APP',
                  children: [],
                  keyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/onml-w0cv-8o8h-56v7-38',
                  key: 'onml-w0cv-8o8h-56v7-38',
                  uuid: 'onml-w0cv-8o8h-56v7-38',
                  data: {
                    id: '1',
                    type: 'dynamicDataSource'
                  },
                  category: 'schema',
                  datasourceName: 'SampleTravelDataDerby'
                },
                {
                  name: 'NULLID',
                  children: [],
                  keyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/6ett-gjx3-p2vc-bc00-5z',
                  key: '6ett-gjx3-p2vc-bc00-5z',
                  uuid: '6ett-gjx3-p2vc-bc00-5z',
                  data: {
                    id: '1',
                    type: 'dynamicDataSource'
                  },
                  category: 'schema',
                  datasourceName: 'SampleTravelDataDerby'
                },
                {
                  name: 'SYSPROC',
                  children: [],
                  keyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/b41p-9lt3-8vuw-f84y-pz',
                  key: 'b41p-9lt3-8vuw-f84y-pz',
                  uuid: 'b41p-9lt3-8vuw-f84y-pz',
                  data: {
                    id: '1',
                    type: 'dynamicDataSource'
                  },
                  category: 'schema',
                  datasourceName: 'SampleTravelDataDerby'
                },
                {
                  name: 'SYS',
                  children: [],
                  keyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/02j9-s36t-11a7-nequ-a5',
                  key: '02j9-s36t-11a7-nequ-a5',
                  uuid: '02j9-s36t-11a7-nequ-a5',
                  data: {
                    id: '1',
                    type: 'dynamicDataSource'
                  },
                  category: 'schema',
                  datasourceName: 'SampleTravelDataDerby'
                },
                {
                  name: 'SYSSTAT',
                  children: [],
                  keyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/1s4y-m032-nhv5-en0n-wa',
                  key: '1s4y-m032-nhv5-en0n-wa',
                  uuid: '1s4y-m032-nhv5-en0n-wa',
                  data: {
                    id: '1',
                    type: 'dynamicDataSource'
                  },
                  category: 'schema',
                  datasourceName: 'SampleTravelDataDerby'
                }
              ],
              driverType: 'Derby',
              keyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9',
              key: 'b83p-y5rg-7dyr-glpv-z9',
              uuid: 'b83p-y5rg-7dyr-glpv-z9',
              fetched: true
            }
          ],
          key: 'dy82-j75c-2s0l-vppb-ax',
          uuid: 'dy82-j75c-2s0l-vppb-ax',
          keyPath: 'dy82-j75c-2s0l-vppb-ax',
          category: 'dsGroup',
          imgUrl: 'images/data_sources/defaut_datasource.png'
        },
        {
          name: 'Mysql',
          children: [
            {
              data: {
                id: '1001',
                type: 'dynamicDataSource'
              },
              dataSourceProvider: 'tomcat',
              type: 'dynamicDataSource',
              permissionLevel: 5,
              driver: 'com.mysql.jdbc.Driver',
              name: ' Mysql',
              classifier: 'global',
              dataSourceType: 'Managed DataSource',
              category: 'dataSource',
              children: [],
              driverType: 'Mysql',
              keyPath: 'izvt-2o5o-yb7k-goqm-9m/8sbp-eklo-0i68-nmoa-uq',
              key: '8sbp-eklo-0i68-nmoa-uq',
              uuid: '8sbp-eklo-0i68-nmoa-uq'
            }
          ],
          key: 'izvt-2o5o-yb7k-goqm-9m',
          uuid: 'izvt-2o5o-yb7k-goqm-9m',
          keyPath: 'izvt-2o5o-yb7k-goqm-9m',
          category: 'dsGroup',
          imgUrl: 'images/data_sources/defaut_datasource.png'
        },
        {
          name: 'Sqlite',
          children: [
            {
              data: {
                id: '1000',
                type: 'dynamicDataSource'
              },
              dataSourceProvider: 'tomcat',
              type: 'dynamicDataSource',
              permissionLevel: 5,
              driver: 'org.sqlite.JDBC',
              name: 'SqlLite',
              classifier: 'global',
              dataSourceType: 'Managed DataSource',
              category: 'dataSource',
              children: [],
              driverType: 'Sqlite',
              keyPath: 'l7zz-926f-iu5q-dyx9-dk/jvsl-1x1w-4fo6-bg05-2g',
              key: 'jvsl-1x1w-4fo6-bg05-2g',
              uuid: 'jvsl-1x1w-4fo6-bg05-2g'
            }
          ],
          key: 'l7zz-926f-iu5q-dyx9-dk',
          uuid: 'l7zz-926f-iu5q-dyx9-dk',
          keyPath: 'l7zz-926f-iu5q-dyx9-dk',
          category: 'dsGroup',
          imgUrl: 'images/data_sources/defaut_datasource.png'
        }
      ],
      workFlow: {
        dataList: [
          {
            fetchSchemas: true,
            fetchCatalogs: true,
            working: true,
            connData: {
              id: '1',
              type: 'dynamicDataSource'
            },
            connId: '1',
            dsUUID: 'b83p-y5rg-7dyr-glpv-z9',
            classifier: 'db.workflow',
            metadata: {
              catalogs: [
                {
                  name: 'Null',
                  schemas: [
                    {
                      name: 'SQLJ'
                    },
                    {
                      name: 'SYSFUN'
                    },
                    {
                      name: 'SYSCAT'
                    },
                    {
                      name: 'HIUSER'
                    },
                    {
                      name: 'SYSCS_DIAG'
                    },
                    {
                      name: 'SYSCS_UTIL'
                    },
                    {
                      name: 'SYSIBM'
                    },
                    {
                      name: 'APP'
                    },
                    {
                      name: 'NULLID'
                    },
                    {
                      name: 'SYSPROC'
                    },
                    {
                      name: 'SYS'
                    },
                    {
                      name: 'SYSSTAT'
                    }
                  ]
                }
              ]
            }
          },
          {
            classifier: 'db.workflow',
            metadata: {
              catalogs: [
                {
                  name: 'Null',
                  schemas: [
                    {
                      name: 'HIUSER',
                      tables: [
                        {
                          id: 'be534112989b616b194bc59c2fb25a42',
                          name: 'geo_cordinates'
                        },
                        {
                          id: '9645c648a1c0dbeec1287aaf1e996db3',
                          name: 'meeting_details'
                        },
                        {
                          id: '4e1fd245f4d13b77be423a43f01d80b2',
                          name: 'employee_details'
                        },
                        {
                          id: '4ac5d9f68b58bd7c0d179146e46795be',
                          name: 'dimdate'
                        },
                        {
                          id: '8a28627d07d04ef096d9935f12e0c7e9',
                          name: 'travel_details'
                        }
                      ]
                    }
                  ]
                }
              ],
              dataSource: {
                id: '1',
                type: 'dynamicDataSource',
                baseType: 'global.jdbc',
                catSchemaPredicted: false,
                sync: false,
                catalog: '',
                schema: 'HIUSER',
                connId: 'n8tju',
                classifier: 'db.workflow',
                datasourceName: 'SampleTravelDataDerby',
                dsKeyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/a8na-05qh-5cei-jjin-6y',
                driverType: 'Derby',
                database: 'HIUSER'
              },
              name: 'HIUSER'
            }
          }
        ]
      },
      dataSource: [
        {
          id: '1',
          type: 'dynamicDataSource',
          baseType: 'global.jdbc',
          catSchemaPredicted: false,
          sync: false,
          catalog: '',
          schema: 'HIUSER',
          connId: 'n8tju',
          classifier: 'db.workflow',
          datasourceName: 'SampleTravelDataDerby',
          dsKeyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/a8na-05qh-5cei-jjin-6y',
          driverType: 'Derby',
          database: 'HIUSER'
        }
      ],
      tables: {
        dimdate: {
          id: '4ac5d9f68b58bd7c0d179146e46795be',
          name: 'dimdate',
          data: {
            id: '1',
            type: 'dynamicDataSource'
          },
          keyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/a8na-05qh-5cei-jjin-6y/o29a-1g2t-84cz-ah6z-rm',
          key: 'o29a-1g2t-84cz-ah6z-rm',
          alias: 'dimdate',
          uuid: 'o29a-1g2t-84cz-ah6z-rm',
          connId: 'n8tju',
          dataSource: {
            id: '1',
            type: 'dynamicDataSource',
            baseType: 'global.jdbc',
            catSchemaPredicted: false,
            sync: false,
            catalog: '',
            schema: 'HIUSER',
            connId: 'n8tju',
            classifier: 'db.workflow',
            datasourceName: 'SampleTravelDataDerby',
            dsKeyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/a8na-05qh-5cei-jjin-6y',
            driverType: 'Derby',
            database: 'HIUSER'
          },
          dataSourceName: 'SampleTravelDataDerby',
          category: 'table',
          nameWithConnId: 'dimdate_n8tju',
          database: 'HIUSER',
          schema: 'HIUSER',
          selected: true,
          keyName: 'dimdate'
        },
        employee_details: {
          id: '4e1fd245f4d13b77be423a43f01d80b2',
          name: 'employee_details',
          data: {
            id: '1',
            type: 'dynamicDataSource'
          },
          keyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/a8na-05qh-5cei-jjin-6y/rlls-2qov-pxtl-ysgq-lo',
          key: 'rlls-2qov-pxtl-ysgq-lo',
          alias: 'employee_details',
          uuid: 'rlls-2qov-pxtl-ysgq-lo',
          connId: 'n8tju',
          dataSource: {
            id: '1',
            type: 'dynamicDataSource',
            baseType: 'global.jdbc',
            catSchemaPredicted: false,
            sync: false,
            catalog: '',
            schema: 'HIUSER',
            connId: 'n8tju',
            classifier: 'db.workflow',
            datasourceName: 'SampleTravelDataDerby',
            dsKeyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/a8na-05qh-5cei-jjin-6y',
            driverType: 'Derby',
            database: 'HIUSER'
          },
          dataSourceName: 'SampleTravelDataDerby',
          category: 'table',
          nameWithConnId: 'employee_details_n8tju',
          database: 'HIUSER',
          schema: 'HIUSER',
          selected: true,
          keyName: 'employee_details'
        },
        geo_cordinates: {
          id: 'be534112989b616b194bc59c2fb25a42',
          name: 'geo_cordinates',
          data: {
            id: '1',
            type: 'dynamicDataSource'
          },
          keyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/a8na-05qh-5cei-jjin-6y/li0s-xj3b-4efs-5mrz-z2',
          key: 'li0s-xj3b-4efs-5mrz-z2',
          alias: 'geo_cordinates',
          uuid: 'li0s-xj3b-4efs-5mrz-z2',
          connId: 'n8tju',
          dataSource: {
            id: '1',
            type: 'dynamicDataSource',
            baseType: 'global.jdbc',
            catSchemaPredicted: false,
            sync: false,
            catalog: '',
            schema: 'HIUSER',
            connId: 'n8tju',
            classifier: 'db.workflow',
            datasourceName: 'SampleTravelDataDerby',
            dsKeyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/a8na-05qh-5cei-jjin-6y',
            driverType: 'Derby',
            database: 'HIUSER'
          },
          dataSourceName: 'SampleTravelDataDerby',
          category: 'table',
          nameWithConnId: 'geo_cordinates_n8tju',
          database: 'HIUSER',
          schema: 'HIUSER',
          selected: true,
          keyName: 'geo_cordinates'
        },
        meeting_details: {
          id: '9645c648a1c0dbeec1287aaf1e996db3',
          name: 'meeting_details',
          data: {
            id: '1',
            type: 'dynamicDataSource'
          },
          keyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/a8na-05qh-5cei-jjin-6y/43cy-8iyh-wue4-zb8j-ia',
          key: '43cy-8iyh-wue4-zb8j-ia',
          alias: 'meeting_details',
          uuid: '43cy-8iyh-wue4-zb8j-ia',
          connId: 'n8tju',
          dataSource: {
            id: '1',
            type: 'dynamicDataSource',
            baseType: 'global.jdbc',
            catSchemaPredicted: false,
            sync: false,
            catalog: '',
            schema: 'HIUSER',
            connId: 'n8tju',
            classifier: 'db.workflow',
            datasourceName: 'SampleTravelDataDerby',
            dsKeyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/a8na-05qh-5cei-jjin-6y',
            driverType: 'Derby',
            database: 'HIUSER'
          },
          dataSourceName: 'SampleTravelDataDerby',
          category: 'table',
          nameWithConnId: 'meeting_details_n8tju',
          database: 'HIUSER',
          schema: 'HIUSER',
          selected: true,
          keyName: 'meeting_details'
        },
        travel_details: {
          id: '8a28627d07d04ef096d9935f12e0c7e9',
          name: 'travel_details',
          data: {
            id: '1',
            type: 'dynamicDataSource'
          },
          keyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/a8na-05qh-5cei-jjin-6y/nt9h-7rtu-cfut-37sj-zz',
          key: 'nt9h-7rtu-cfut-37sj-zz',
          alias: 'travel_details',
          uuid: 'nt9h-7rtu-cfut-37sj-zz',
          connId: 'n8tju',
          dataSource: {
            id: '1',
            type: 'dynamicDataSource',
            baseType: 'global.jdbc',
            catSchemaPredicted: false,
            sync: false,
            catalog: '',
            schema: 'HIUSER',
            connId: 'n8tju',
            classifier: 'db.workflow',
            datasourceName: 'SampleTravelDataDerby',
            dsKeyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/a8na-05qh-5cei-jjin-6y',
            driverType: 'Derby',
            database: 'HIUSER'
          },
          dataSourceName: 'SampleTravelDataDerby',
          category: 'table',
          nameWithConnId: 'travel_details_n8tju',
          database: 'HIUSER',
          schema: 'HIUSER',
          selected: true,
          keyName: 'travel_details'
        }
      },
      views: [],
      activeView: false,
      categories: {
        'dy82-j75c-2s0l-vppb-ax': {
          ds: {
            data: {
              id: '1',
              type: 'dynamicDataSource'
            },
            dataSourceProvider: 'tomcat',
            type: 'dynamicDataSource',
            permissionLevel: 5,
            driver: 'org.apache.derby.jdbc.ClientDriver',
            name: 'SampleTravelDataDerby',
            classifier: 'global',
            dataSourceType: 'Managed DataSource'
          },
          category: {
            driver: 'org.apache.derby.jdbc.ClientDriver',
            databaseDialect: 'derby',
            name: 'Derby',
            categoryName: 'RDBMS',
            categoryType: 'rdbms',
            type: 'global.jdbc',
            dataSourceProvider: 'tomcat',
            classifier: 'global',
            imgUrl: '../images/data_sources/defaut_datasource.png',
            url: 'jdbc:derby://{{hostName}}:{{port}}/{{database}}',
            parameters: {
              port: '1527',
              hostName: 'localhost',
              database: 'database'
            }
          }
        },
        'izvt-2o5o-yb7k-goqm-9m': {
          ds: {
            data: {
              id: '1001',
              type: 'dynamicDataSource'
            },
            dataSourceProvider: 'tomcat',
            type: 'dynamicDataSource',
            permissionLevel: 5,
            driver: 'com.mysql.jdbc.Driver',
            name: ' Mysql',
            classifier: 'global',
            dataSourceType: 'Managed DataSource'
          },
          category: {
            driver: 'com.mysql.jdbc.Driver',
            databaseDialect: 'mysql',
            name: 'Mysql',
            categoryName: 'RDBMS',
            categoryType: 'rdbms',
            type: 'global.jdbc',
            dataSourceProvider: 'tomcat',
            classifier: 'global',
            imgUrl: '../images/data_sources/defaut_datasource.png',
            url: 'jdbc:mysql://{{hostName}}:{{port}}/{{database}}',
            parameters: {
              port: '3306',
              hostName: 'localhost',
              database: 'database'
            }
          }
        },
        'l7zz-926f-iu5q-dyx9-dk': {
          ds: {
            data: {
              id: '1000',
              type: 'dynamicDataSource'
            },
            dataSourceProvider: 'tomcat',
            type: 'dynamicDataSource',
            permissionLevel: 5,
            driver: 'org.sqlite.JDBC',
            name: 'SqlLite',
            classifier: 'global',
            dataSourceType: 'Managed DataSource'
          },
          category: {
            driver: 'org.sqlite.JDBC',
            databaseDialect: 'sqlite',
            name: 'Sqlite',
            categoryName: 'RDBMS',
            categoryType: 'rdbms',
            type: 'global.jdbc',
            dataSourceProvider: 'tomcat',
            classifier: 'global',
            imgUrl: '../images/data_sources/defaut_datasource.png',
            url: 'jdbc:sqlite:{{database}}',
            parameters: {
              database: 'database'
            }
          }
        }
      },
      activeEditorTab: 'info',
      dataSourcesAddedToMetadata: [
        {
          id: '1',
          type: 'dynamicDataSource',
          baseType: 'global.jdbc',
          catSchemaPredicted: false,
          sync: false,
          catalog: '',
          schema: 'HIUSER',
          connId: 'n8tju',
          classifier: 'db.workflow',
          datasourceName: 'SampleTravelDataDerby',
          dsKeyPath: 'dy82-j75c-2s0l-vppb-ax/b83p-y5rg-7dyr-glpv-z9/a8na-05qh-5cei-jjin-6y',
          driverType: 'Derby',
          database: 'HIUSER'
        }
      ],
      changeDSList: {},
      changedTables: [],
      changedColumns: [],
      removedTables: [],
      removedColumns: [],
      removedDataSources: [],
      duplicateColumnList: [],
      duplicateTableList: [],
      unsavedViews: [],
      saveDetails: false,
      savedTableIds: [],
      savedColumnIds: [],
      joins: [],
      mode: 'create',
      allTablesKeys: [
        'li0s-xj3b-4efs-5mrz-z2',
        '43cy-8iyh-wue4-zb8j-ia',
        'rlls-2qov-pxtl-ysgq-lo',
        'o29a-1g2t-84cz-ah6z-rm',
        'nt9h-7rtu-cfut-37sj-zz'
      ],
      selectedTableKeys: [
        'o29a-1g2t-84cz-ah6z-rm',
        'rlls-2qov-pxtl-ysgq-lo',
        'li0s-xj3b-4efs-5mrz-z2',
        '43cy-8iyh-wue4-zb8j-ia',
        'nt9h-7rtu-cfut-37sj-zz'
      ],
      metadataName: 'Metadata_1',
      activeDataSource: false,
      metadataToEdit: false,
      isSavingInProgress: false,
      editViewsTempData: {},
      inititalStateFromJest: false,
      timeStamp: 1662095894665,
      initialEditResponse: false,
      editorFullView: false,
      selectedTableOrColumnKey: {},
      expressionObj: [],
      securityConstants: {},
      edit: false,
      isAllowServiceCall: true,
      isValidatedTableShow: false,
      securityTableData: [],
      addOneMoreSecurity: false,
      viewSessionVariables: false,
      textEditingObj: {},
      selectedJoinNameData: {},
      filterbyData: [
        {
          value: 'All',
          isChecked: true
        },
        {
          value: 'Table',
          isChecked: true
        },
        {
          value: 'Column',
          isChecked: true
        },
        {
          value: 'Global',
          isChecked: true
        }
      ],
      isFirstRender: true,
      securityFormData: {},
      accessType: 'deny',
      entityNames: '',
      executionType: 'conditionIf',
      expressionName: '',
      expressionType: '',
      isApplyDisabled: true,
      isInfoShow: true,
      securityKeysChecked: [],
      hasUnsavedData: true
    }
  }