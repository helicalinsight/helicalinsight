import { findAllNestedObj } from "../../utils/find-nested-obj";


const dsListToRenderFromRedux = [
    {
        "name": "Derby",
        "children": [
            {
                "permissionLevel": 5,
                "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                "name": "Plain Jdbc",
                "classifier": "efwd",
                "type": "sql.jdbc",
                "data": {
                    "dir": "datasources",
                    "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                    "type": "sql.jdbc",
                    "id": 501,
                    "userName": "hiuser",
                    "password": "hiuser",
                    "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
                },
                "dataSourceType": "Plain Jdbc DataSource",
                "category": "dataSource",
                "children": [],
                "driverType": "Derby",
                "keyPath": "u1zg-8js1-lnw4-jf9d-c0/8vgl-w01s-0w1j-g3j5-92",
                "key": "8vgl-w01s-0w1j-g3j5-92",
                "uuid": "8vgl-w01s-0w1j-g3j5-92"
            },
            {
                "permissionLevel": 5,
                "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                "name": "5722",
                "classifier": "efwd",
                "type": "sql.jdbc",
                "data": {
                    "dir": "sai_ganesh",
                    "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                    "type": "sql.jdbc",
                    "id": 701,
                    "userName": "hiuser",
                    "password": "hiuser",
                    "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
                },
                "dataSourceType": "Plain Jdbc DataSource",
                "category": "dataSource",
                "children": [],
                "driverType": "Derby",
                "keyPath": "u1zg-8js1-lnw4-jf9d-c0/id49-049z-77cq-0a4k-n5",
                "key": "id49-049z-77cq-0a4k-n5",
                "uuid": "id49-049z-77cq-0a4k-n5"
            },
            {
                "data": {
                    "id": "1",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                "name": "SampleTravelDataDerby",
                "classifier": "global",
                "dataSourceType": "Managed DataSource",
                "category": "dataSource",
                "children": [],
                "driverType": "Derby",
                "keyPath": "u1zg-8js1-lnw4-jf9d-c0/v2fh-4k1r-hp1x-38hk-md",
                "key": "v2fh-4k1r-hp1x-38hk-md",
                "uuid": "v2fh-4k1r-hp1x-38hk-md"
            },
            {
                "data": {
                    "id": "1000",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                "name": "hiee",
                "classifier": "global",
                "dataSourceType": "Managed DataSource",
                "category": "dataSource",
                "children": [],
                "driverType": "Derby",
                "keyPath": "u1zg-8js1-lnw4-jf9d-c0/jsig-9xpu-xgvv-ngy1-sb",
                "key": "jsig-9xpu-xgvv-ngy1-sb",
                "uuid": "jsig-9xpu-xgvv-ngy1-sb"
            },
            {
                "data": {
                    "id": "1101",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                "name": "derby2",
                "classifier": "global",
                "dataSourceType": "Managed DataSource",
                "category": "dataSource",
                "children": [],
                "driverType": "Derby",
                "keyPath": "u1zg-8js1-lnw4-jf9d-c0/ona4-7ogz-wril-94p7-of",
                "key": "ona4-7ogz-wril-94p7-of",
                "uuid": "ona4-7ogz-wril-94p7-of"
            },
            {
                "data": {
                    "id": "1801",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                "name": "Dump",
                "classifier": "global",
                "dataSourceType": "Managed DataSource",
                "category": "dataSource",
                "children": [],
                "driverType": "Derby",
                "keyPath": "u1zg-8js1-lnw4-jf9d-c0/qftj-k7im-igq2-8lgy-ra",
                "key": "qftj-k7im-igq2-8lgy-ra",
                "uuid": "qftj-k7im-igq2-8lgy-ra"
            },
            {
                "data": {
                    "id": "2301",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                "name": "SampleTravelData",
                "classifier": "global",
                "dataSourceType": "Managed DataSource",
                "category": "dataSource",
                "children": [],
                "driverType": "Derby",
                "keyPath": "u1zg-8js1-lnw4-jf9d-c0/t85s-7t10-qy9d-s7sy-mc",
                "key": "t85s-7t10-qy9d-s7sy-mc",
                "uuid": "t85s-7t10-qy9d-s7sy-mc"
            },
            {
                "data": {
                    "id": "2601",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                "name": "AnotherDerby",
                "classifier": "global",
                "dataSourceType": "Managed DataSource",
                "category": "dataSource",
                "children": [],
                "driverType": "Derby",
                "keyPath": "u1zg-8js1-lnw4-jf9d-c0/s7qv-f3zx-9r3j-5q4u-du",
                "key": "s7qv-f3zx-9r3j-5q4u-du",
                "uuid": "s7qv-f3zx-9r3j-5q4u-du"
            }
        ],
        "key": "u1zg-8js1-lnw4-jf9d-c0",
        "uuid": "u1zg-8js1-lnw4-jf9d-c0",
        "keyPath": "u1zg-8js1-lnw4-jf9d-c0",
        "category": "dsGroup",
        "imgUrl": "images/data_sources/defaut_datasource.png"
    },
    {
        "name": "DynamicSwitch",
        "children": [
            {
                "permissionLevel": 5,
                "driver": "dynamicSwitch",
                "name": "Groovy 1",
                "classifier": "efwd",
                "type": "sql.jdbc.groovy.managed",
                "data": {
                    "dir": "Gagan",
                    "driverName": null,
                    "type": "sql.jdbc.groovy.managed",
                    "id": 1,
                    "userName": null,
                    "password": null,
                    "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1001);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                    "jdbcUrl": null,
                    "driver": "dynamicSwitch"
                },
                "dataSourceType": "",
                "category": "dataSource",
                "children": [],
                "driverType": "DynamicSwitch",
                "keyPath": "0xzt-009t-dopt-5bhs-6l/1kdq-i9a1-rweq-ee4k-hz",
                "key": "1kdq-i9a1-rweq-ee4k-hz",
                "uuid": "1kdq-i9a1-rweq-ee4k-hz"
            },
            {
                "permissionLevel": 5,
                "driver": "dynamicSwitch",
                "name": "6148b",
                "classifier": "efwd",
                "type": "sql.jdbc.groovy.managed",
                "data": {
                    "dir": "03_03",
                    "driverName": null,
                    "type": "sql.jdbc.groovy.managed",
                    "id": 801,
                    "userName": null,
                    "password": null,
                    "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 1001);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                    "jdbcUrl": null,
                    "driver": "dynamicSwitch"
                },
                "dataSourceType": "",
                "category": "dataSource",
                "children": [],
                "driverType": "DynamicSwitch",
                "keyPath": "0xzt-009t-dopt-5bhs-6l/8i09-bhdw-uj71-1taj-k7",
                "key": "8i09-bhdw-uj71-1taj-k7",
                "uuid": "8i09-bhdw-uj71-1taj-k7"
            },
            {
                "permissionLevel": 5,
                "driver": "dynamicSwitch",
                "name": "GroovyManaged",
                "classifier": "efwd",
                "type": "sql.jdbc.groovy.managed",
                "data": {
                    "dir": "sai_ganesh",
                    "driverName": null,
                    "type": "sql.jdbc.groovy.managed",
                    "id": 601,
                    "userName": null,
                    "password": null,
                    "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 300);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                    "jdbcUrl": null,
                    "driver": "dynamicSwitch"
                },
                "dataSourceType": "",
                "category": "dataSource",
                "children": [],
                "driverType": "DynamicSwitch",
                "keyPath": "0xzt-009t-dopt-5bhs-6l/wyay-rh5z-nxev-orsz-kf",
                "key": "wyay-rh5z-nxev-orsz-kf",
                "uuid": "wyay-rh5z-nxev-orsz-kf"
            }
        ],
        "key": "0xzt-009t-dopt-5bhs-6l",
        "uuid": "0xzt-009t-dopt-5bhs-6l",
        "keyPath": "0xzt-009t-dopt-5bhs-6l",
        "category": "dsGroup",
        "imgUrl": "images/data_sources/defaut_datasource.png"
    },
    {
        "name": "Mysql",
        "children": [
            {
                "data": {
                    "id": "2402",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "com.mysql.jdbc.Driver",
                "name": "mysql",
                "classifier": "global",
                "dataSourceType": "Managed DataSource",
                "category": "dataSource",
                "children": [],
                "driverType": "Mysql",
                "keyPath": "e3no-otp4-4gal-iexb-xs/7lc6-sbhy-iuex-dsxr-v8",
                "key": "7lc6-sbhy-iuex-dsxr-v8",
                "uuid": "7lc6-sbhy-iuex-dsxr-v8"
            }
        ],
        "key": "e3no-otp4-4gal-iexb-xs",
        "uuid": "e3no-otp4-4gal-iexb-xs",
        "keyPath": "e3no-otp4-4gal-iexb-xs",
        "category": "dsGroup",
        "imgUrl": "images/data_sources/defaut_datasource.png"
    },
    {
        "name": "Oracle",
        "children": [
            {
                "data": {
                    "id": "2001",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "oracle.jdbc.OracleDriver",
                "name": "Oracle_12",
                "classifier": "global",
                "dataSourceType": "Managed DataSource",
                "category": "dataSource",
                "children": [],
                "driverType": "Oracle",
                "keyPath": "43py-3bq0-58ee-cnu7-r3/ru1l-r9im-m09x-j6e5-2g",
                "key": "ru1l-r9im-m09x-j6e5-2g",
                "uuid": "ru1l-r9im-m09x-j6e5-2g"
            }
        ],
        "key": "43py-3bq0-58ee-cnu7-r3",
        "uuid": "43py-3bq0-58ee-cnu7-r3",
        "keyPath": "43py-3bq0-58ee-cnu7-r3",
        "category": "dsGroup",
        "imgUrl": "images/data_sources/defaut_datasource.png"
    },
    {
        "name": "Postgresql",
        "children": [
            {
                "data": {
                    "id": "1001",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.postgresql.Driver",
                "name": "booking",
                "classifier": "global",
                "dataSourceType": "Managed DataSource",
                "category": "dataSource",
                "children": [],
                "driverType": "Postgresql",
                "keyPath": "eoxv-a41s-y02h-qgbx-df/2jop-h768-i3ve-wg9g-mg",
                "key": "2jop-h768-i3ve-wg9g-mg",
                "uuid": "2jop-h768-i3ve-wg9g-mg"
            },
            {
                "data": {
                    "id": "1401",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.postgresql.Driver",
                "name": "booking",
                "classifier": "global",
                "dataSourceType": "Managed DataSource",
                "category": "dataSource",
                "children": [],
                "driverType": "Postgresql",
                "keyPath": "eoxv-a41s-y02h-qgbx-df/7cou-80na-31i9-3he0-51",
                "key": "7cou-80na-31i9-3he0-51",
                "uuid": "7cou-80na-31i9-3he0-51"
            },
            {
                "data": {
                    "id": "2202",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.postgresql.Driver",
                "name": "SampleTravelData",
                "classifier": "global",
                "dataSourceType": "Managed DataSource",
                "category": "dataSource",
                "children": [
                    {
                        "name": "SampleTravelData",
                        "schemas": [
                            {
                                "name": "pg_catalog",
                                "data": {
                                    "id": "2202",
                                    "type": "dynamicDataSource"
                                },
                                "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/gvw2-0gm3-2y14-oj1f-xd",
                                "key": "gvw2-0gm3-2y14-oj1f-xd",
                                "uuid": "gvw2-0gm3-2y14-oj1f-xd",
                                "category": "schema",
                                "children": [],
                                "catalog": "SampleTravelData",
                                "datasourceName": "SampleTravelData"
                            },
                            {
                                "name": "public",
                                "data": {
                                    "id": "2202",
                                    "type": "dynamicDataSource"
                                },
                                "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf",
                                "key": "sd5d-x303-9pl9-m0x9-yf",
                                "uuid": "sd5d-x303-9pl9-m0x9-yf",
                                "category": "schema",
                                "children": [
                                    {
                                        "id": "bc974c57fc724d0f9aba709054ccf15a",
                                        "name": "dimdate",
                                        "alias": "dimdate",
                                        "dataSource": {
                                            "id": "2202",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "SampleTravelData",
                                            "schema": "public",
                                            "connId": "g4nir",
                                            "dbId": "g4nir",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelData",
                                            "dsKeyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf",
                                            "driverType": "Postgresql",
                                            "database": "public"
                                        },
                                        "category": "table",
                                        "connId": "g4nir",
                                        "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf/t7ws-m4xa-e1ap-y9on-d2",
                                        "uniqueKey": "bc974c57fc724d0f9aba709054ccf15a_g4nir",
                                        "catalog": "SampleTravelData",
                                        "schema": "public",
                                        "selected": false
                                    },
                                    {
                                        "id": "f1ae3bc473373b740fa44b8e4f9dcff7",
                                        "name": "employee",
                                        "alias": "employee",
                                        "dataSource": {
                                            "id": "2202",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "SampleTravelData",
                                            "schema": "public",
                                            "connId": "g4nir",
                                            "dbId": "g4nir",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelData",
                                            "dsKeyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf",
                                            "driverType": "Postgresql",
                                            "database": "public"
                                        },
                                        "category": "table",
                                        "connId": "g4nir",
                                        "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf/siti-ujzw-wdr1-k97k-1g",
                                        "uniqueKey": "f1ae3bc473373b740fa44b8e4f9dcff7_g4nir",
                                        "catalog": "SampleTravelData",
                                        "schema": "public",
                                        "selected": false
                                    },
                                    {
                                        "id": "bc2a74cd93c41a055342b008cecc18ed",
                                        "name": "employee_details",
                                        "alias": "employee_details",
                                        "dataSource": {
                                            "id": "2202",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "SampleTravelData",
                                            "schema": "public",
                                            "connId": "g4nir",
                                            "dbId": "g4nir",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelData",
                                            "dsKeyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf",
                                            "driverType": "Postgresql",
                                            "database": "public"
                                        },
                                        "category": "table",
                                        "connId": "g4nir",
                                        "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf/d2id-h2w9-lzui-kpi1-by",
                                        "uniqueKey": "bc2a74cd93c41a055342b008cecc18ed_g4nir",
                                        "catalog": "SampleTravelData",
                                        "schema": "public",
                                        "selected": false
                                    },
                                    {
                                        "id": "1356a1d85d523020df6f63065be01623",
                                        "name": "geo_cordinates",
                                        "alias": "geo_cordinates",
                                        "dataSource": {
                                            "id": "2202",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "SampleTravelData",
                                            "schema": "public",
                                            "connId": "g4nir",
                                            "dbId": "g4nir",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelData",
                                            "dsKeyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf",
                                            "driverType": "Postgresql",
                                            "database": "public"
                                        },
                                        "category": "table",
                                        "connId": "g4nir",
                                        "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf/ow3p-6xfv-c4s2-vcnq-uy",
                                        "uniqueKey": "1356a1d85d523020df6f63065be01623_g4nir",
                                        "catalog": "SampleTravelData",
                                        "schema": "public",
                                        "selected": false
                                    },
                                    {
                                        "id": "9160c5d9d2af1507148f1ed2554dcf96",
                                        "name": "meeting_details",
                                        "alias": "meeting_details",
                                        "dataSource": {
                                            "id": "2202",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "SampleTravelData",
                                            "schema": "public",
                                            "connId": "g4nir",
                                            "dbId": "g4nir",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelData",
                                            "dsKeyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf",
                                            "driverType": "Postgresql",
                                            "database": "public"
                                        },
                                        "category": "table",
                                        "connId": "g4nir",
                                        "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf/r0bo-ywee-rpys-pl97-92",
                                        "uniqueKey": "9160c5d9d2af1507148f1ed2554dcf96_g4nir",
                                        "catalog": "SampleTravelData",
                                        "schema": "public",
                                        "selected": false
                                    },
                                    {
                                        "id": "407487cdac2f4fc9328137ffb5413ddf",
                                        "name": "travel_details",
                                        "alias": "travel_details",
                                        "dataSource": {
                                            "id": "2202",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "SampleTravelData",
                                            "schema": "public",
                                            "connId": "g4nir",
                                            "dbId": "g4nir",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelData",
                                            "dsKeyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf",
                                            "driverType": "Postgresql",
                                            "database": "public"
                                        },
                                        "category": "table",
                                        "connId": "g4nir",
                                        "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf/rik5-hztv-0wyu-5buq-nw",
                                        "uniqueKey": "407487cdac2f4fc9328137ffb5413ddf_g4nir",
                                        "catalog": "SampleTravelData",
                                        "schema": "public",
                                        "selected": false
                                    }
                                ],
                                "catalog": "SampleTravelData",
                                "datasourceName": "SampleTravelData",
                                "fetched": true
                            },
                            {
                                "name": "information_schema",
                                "data": {
                                    "id": "2202",
                                    "type": "dynamicDataSource"
                                },
                                "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/wz4a-7rjq-g3mr-8qqs-96",
                                "key": "wz4a-7rjq-g3mr-8qqs-96",
                                "uuid": "wz4a-7rjq-g3mr-8qqs-96",
                                "category": "schema",
                                "children": [],
                                "catalog": "SampleTravelData",
                                "datasourceName": "SampleTravelData"
                            }
                        ],
                        "children": [
                            {
                                "name": "pg_catalog",
                                "data": {
                                    "id": "2202",
                                    "type": "dynamicDataSource"
                                },
                                "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/gvw2-0gm3-2y14-oj1f-xd",
                                "key": "gvw2-0gm3-2y14-oj1f-xd",
                                "uuid": "gvw2-0gm3-2y14-oj1f-xd",
                                "category": "schema",
                                "children": [],
                                "catalog": "SampleTravelData",
                                "datasourceName": "SampleTravelData"
                            },
                            {
                                "name": "public",
                                "data": {
                                    "id": "2202",
                                    "type": "dynamicDataSource"
                                },
                                "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf",
                                "key": "sd5d-x303-9pl9-m0x9-yf",
                                "uuid": "sd5d-x303-9pl9-m0x9-yf",
                                "category": "schema",
                                "children": [
                                    {
                                        "id": "bc974c57fc724d0f9aba709054ccf15a",
                                        "name": "dimdate",
                                        "alias": "dimdate",
                                        "dataSource": {
                                            "id": "2202",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "SampleTravelData",
                                            "schema": "public",
                                            "connId": "g4nir",
                                            "dbId": "g4nir",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelData",
                                            "dsKeyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf",
                                            "driverType": "Postgresql",
                                            "database": "public"
                                        },
                                        "category": "table",
                                        "connId": "g4nir",
                                        "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf/t7ws-m4xa-e1ap-y9on-d2",
                                        "uniqueKey": "bc974c57fc724d0f9aba709054ccf15a_g4nir",
                                        "catalog": "SampleTravelData",
                                        "schema": "public",
                                        "selected": false
                                    },
                                    {
                                        "id": "f1ae3bc473373b740fa44b8e4f9dcff7",
                                        "name": "employee",
                                        "alias": "employee",
                                        "dataSource": {
                                            "id": "2202",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "SampleTravelData",
                                            "schema": "public",
                                            "connId": "g4nir",
                                            "dbId": "g4nir",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelData",
                                            "dsKeyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf",
                                            "driverType": "Postgresql",
                                            "database": "public"
                                        },
                                        "category": "table",
                                        "connId": "g4nir",
                                        "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf/siti-ujzw-wdr1-k97k-1g",
                                        "uniqueKey": "f1ae3bc473373b740fa44b8e4f9dcff7_g4nir",
                                        "catalog": "SampleTravelData",
                                        "schema": "public",
                                        "selected": false
                                    },
                                    {
                                        "id": "bc2a74cd93c41a055342b008cecc18ed",
                                        "name": "employee_details",
                                        "alias": "employee_details",
                                        "dataSource": {
                                            "id": "2202",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "SampleTravelData",
                                            "schema": "public",
                                            "connId": "g4nir",
                                            "dbId": "g4nir",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelData",
                                            "dsKeyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf",
                                            "driverType": "Postgresql",
                                            "database": "public"
                                        },
                                        "category": "table",
                                        "connId": "g4nir",
                                        "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf/d2id-h2w9-lzui-kpi1-by",
                                        "uniqueKey": "bc2a74cd93c41a055342b008cecc18ed_g4nir",
                                        "catalog": "SampleTravelData",
                                        "schema": "public",
                                        "selected": false
                                    },
                                    {
                                        "id": "1356a1d85d523020df6f63065be01623",
                                        "name": "geo_cordinates",
                                        "alias": "geo_cordinates",
                                        "dataSource": {
                                            "id": "2202",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "SampleTravelData",
                                            "schema": "public",
                                            "connId": "g4nir",
                                            "dbId": "g4nir",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelData",
                                            "dsKeyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf",
                                            "driverType": "Postgresql",
                                            "database": "public"
                                        },
                                        "category": "table",
                                        "connId": "g4nir",
                                        "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf/ow3p-6xfv-c4s2-vcnq-uy",
                                        "uniqueKey": "1356a1d85d523020df6f63065be01623_g4nir",
                                        "catalog": "SampleTravelData",
                                        "schema": "public",
                                        "selected": false
                                    },
                                    {
                                        "id": "9160c5d9d2af1507148f1ed2554dcf96",
                                        "name": "meeting_details",
                                        "alias": "meeting_details",
                                        "dataSource": {
                                            "id": "2202",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "SampleTravelData",
                                            "schema": "public",
                                            "connId": "g4nir",
                                            "dbId": "g4nir",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelData",
                                            "dsKeyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf",
                                            "driverType": "Postgresql",
                                            "database": "public"
                                        },
                                        "category": "table",
                                        "connId": "g4nir",
                                        "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf/r0bo-ywee-rpys-pl97-92",
                                        "uniqueKey": "9160c5d9d2af1507148f1ed2554dcf96_g4nir",
                                        "catalog": "SampleTravelData",
                                        "schema": "public",
                                        "selected": false
                                    },
                                    {
                                        "id": "407487cdac2f4fc9328137ffb5413ddf",
                                        "name": "travel_details",
                                        "alias": "travel_details",
                                        "dataSource": {
                                            "id": "2202",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "SampleTravelData",
                                            "schema": "public",
                                            "connId": "g4nir",
                                            "dbId": "g4nir",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelData",
                                            "dsKeyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf",
                                            "driverType": "Postgresql",
                                            "database": "public"
                                        },
                                        "category": "table",
                                        "connId": "g4nir",
                                        "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf/rik5-hztv-0wyu-5buq-nw",
                                        "uniqueKey": "407487cdac2f4fc9328137ffb5413ddf_g4nir",
                                        "catalog": "SampleTravelData",
                                        "schema": "public",
                                        "selected": false
                                    }
                                ],
                                "catalog": "SampleTravelData",
                                "datasourceName": "SampleTravelData",
                                "fetched": true
                            },
                            {
                                "name": "information_schema",
                                "data": {
                                    "id": "2202",
                                    "type": "dynamicDataSource"
                                },
                                "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/wz4a-7rjq-g3mr-8qqs-96",
                                "key": "wz4a-7rjq-g3mr-8qqs-96",
                                "uuid": "wz4a-7rjq-g3mr-8qqs-96",
                                "category": "schema",
                                "children": [],
                                "catalog": "SampleTravelData",
                                "datasourceName": "SampleTravelData"
                            }
                        ],
                        "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0",
                        "key": "vbr1-xbh9-v2dg-11fy-i0",
                        "uuid": "vbr1-xbh9-v2dg-11fy-i0",
                        "data": {
                            "id": "2202",
                            "type": "dynamicDataSource"
                        },
                        "category": "catalog",
                        "fetched": true,
                        "datasourceName": "SampleTravelData"
                    }
                ],
                "driverType": "Postgresql",
                "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe",
                "key": "0x3o-kf9q-lzux-f7a6-xe",
                "uuid": "0x3o-kf9q-lzux-f7a6-xe"
            }
        ],
        "key": "eoxv-a41s-y02h-qgbx-df",
        "uuid": "eoxv-a41s-y02h-qgbx-df",
        "keyPath": "eoxv-a41s-y02h-qgbx-df",
        "category": "dsGroup",
        "imgUrl": "images/data_sources/defaut_datasource.png"
    },
    {
        "name": "Snowflake Client",
        "children": [
            {
                "data": {
                    "id": "1901",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "net.snowflake.client.jdbc.SnowflakeDriver",
                "name": "SnowflakeDS",
                "classifier": "global",
                "dataSourceType": "Managed DataSource",
                "category": "dataSource",
                "children": [],
                "driverType": "Snowflake Client",
                "keyPath": "xgdw-e605-aprf-8tuw-o6/wdys-p7zc-mkvi-snqp-t7",
                "key": "wdys-p7zc-mkvi-snqp-t7",
                "uuid": "wdys-p7zc-mkvi-snqp-t7"
            },
            {
                "data": {
                    "id": "1902",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "net.snowflake.client.jdbc.SnowflakeDriver",
                "name": "SnowFlakeDSWorking",
                "classifier": "global",
                "dataSourceType": "Managed DataSource",
                "category": "dataSource",
                "children": [],
                "driverType": "Snowflake Client",
                "keyPath": "xgdw-e605-aprf-8tuw-o6/vvkz-i2yv-atzh-m72h-gq",
                "key": "vvkz-i2yv-atzh-m72h-gq",
                "uuid": "vvkz-i2yv-atzh-m72h-gq"
            },
            {
                "data": {
                    "id": "2403",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "net.snowflake.client.jdbc.SnowflakeDriver",
                "name": "testdata",
                "classifier": "global",
                "dataSourceType": "Managed DataSource",
                "category": "dataSource",
                "children": [],
                "driverType": "Snowflake Client",
                "keyPath": "xgdw-e605-aprf-8tuw-o6/ojl2-lo02-r1xk-s1yg-qd",
                "key": "ojl2-lo02-r1xk-s1yg-qd",
                "uuid": "ojl2-lo02-r1xk-s1yg-qd"
            },
            {
                "data": {
                    "id": "2501",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "net.snowflake.client.jdbc.SnowflakeDriver",
                "name": "60045",
                "classifier": "global",
                "dataSourceType": "Managed DataSource",
                "category": "dataSource",
                "children": [],
                "driverType": "Snowflake Client",
                "keyPath": "xgdw-e605-aprf-8tuw-o6/xtiy-ib2b-4d1v-q8iz-mf",
                "key": "xtiy-ib2b-4d1v-q8iz-mf",
                "uuid": "xtiy-ib2b-4d1v-q8iz-mf"
            }
        ],
        "key": "xgdw-e605-aprf-8tuw-o6",
        "uuid": "xgdw-e605-aprf-8tuw-o6",
        "keyPath": "xgdw-e605-aprf-8tuw-o6",
        "category": "dsGroup",
        "imgUrl": "images/data_sources/defaut_datasource.png"
    },
    {
        "name": "Sqlite",
        "children": [
            {
                "data": {
                    "id": "1201",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.sqlite.JDBC",
                "name": "SampleTravelData",
                "classifier": "global",
                "dataSourceType": "Managed DataSource",
                "category": "dataSource",
                "children": [],
                "driverType": "Sqlite",
                "keyPath": "r6fl-lgtv-onwd-e3x9-rl/dh5f-sp7t-fwt8-xf00-is",
                "key": "dh5f-sp7t-fwt8-xf00-is",
                "uuid": "dh5f-sp7t-fwt8-xf00-is"
            },
            {
                "data": {
                    "id": "1302",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.sqlite.JDBC",
                "name": "Sqlite",
                "classifier": "global",
                "dataSourceType": "Managed DataSource",
                "category": "dataSource",
                "children": [],
                "driverType": "Sqlite",
                "keyPath": "r6fl-lgtv-onwd-e3x9-rl/z978-l1v0-awn3-03qh-d2",
                "key": "z978-l1v0-awn3-03qh-d2",
                "uuid": "z978-l1v0-awn3-03qh-d2"
            }
        ],
        "key": "r6fl-lgtv-onwd-e3x9-rl",
        "uuid": "r6fl-lgtv-onwd-e3x9-rl",
        "keyPath": "r6fl-lgtv-onwd-e3x9-rl",
        "category": "dsGroup",
        "imgUrl": "images/data_sources/defaut_datasource.png"
    }
]
const expResult = [
    {
        "id": "bc974c57fc724d0f9aba709054ccf15a",
        "name": "dimdate",
        "alias": "dimdate",
        "dataSource": {
            "id": "2202",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "SampleTravelData",
            "schema": "public",
            "connId": "g4nir",
            "dbId": "g4nir",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelData",
            "dsKeyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf",
            "driverType": "Postgresql",
            "database": "public"
        },
        "category": "table",
        "connId": "g4nir",
        "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf/t7ws-m4xa-e1ap-y9on-d2",
        "uniqueKey": "bc974c57fc724d0f9aba709054ccf15a_g4nir",
        "catalog": "SampleTravelData",
        "schema": "public",
        "selected": false
    },
    {
        "id": "f1ae3bc473373b740fa44b8e4f9dcff7",
        "name": "employee",
        "alias": "employee",
        "dataSource": {
            "id": "2202",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "SampleTravelData",
            "schema": "public",
            "connId": "g4nir",
            "dbId": "g4nir",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelData",
            "dsKeyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf",
            "driverType": "Postgresql",
            "database": "public"
        },
        "category": "table",
        "connId": "g4nir",
        "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf/siti-ujzw-wdr1-k97k-1g",
        "uniqueKey": "f1ae3bc473373b740fa44b8e4f9dcff7_g4nir",
        "catalog": "SampleTravelData",
        "schema": "public",
        "selected": false
    },
    {
        "id": "bc2a74cd93c41a055342b008cecc18ed",
        "name": "employee_details",
        "alias": "employee_details",
        "dataSource": {
            "id": "2202",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "SampleTravelData",
            "schema": "public",
            "connId": "g4nir",
            "dbId": "g4nir",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelData",
            "dsKeyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf",
            "driverType": "Postgresql",
            "database": "public"
        },
        "category": "table",
        "connId": "g4nir",
        "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf/d2id-h2w9-lzui-kpi1-by",
        "uniqueKey": "bc2a74cd93c41a055342b008cecc18ed_g4nir",
        "catalog": "SampleTravelData",
        "schema": "public",
        "selected": false
    },
    {
        "id": "1356a1d85d523020df6f63065be01623",
        "name": "geo_cordinates",
        "alias": "geo_cordinates",
        "dataSource": {
            "id": "2202",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "SampleTravelData",
            "schema": "public",
            "connId": "g4nir",
            "dbId": "g4nir",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelData",
            "dsKeyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf",
            "driverType": "Postgresql",
            "database": "public"
        },
        "category": "table",
        "connId": "g4nir",
        "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf/ow3p-6xfv-c4s2-vcnq-uy",
        "uniqueKey": "1356a1d85d523020df6f63065be01623_g4nir",
        "catalog": "SampleTravelData",
        "schema": "public",
        "selected": false
    },
    {
        "id": "9160c5d9d2af1507148f1ed2554dcf96",
        "name": "meeting_details",
        "alias": "meeting_details",
        "dataSource": {
            "id": "2202",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "SampleTravelData",
            "schema": "public",
            "connId": "g4nir",
            "dbId": "g4nir",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelData",
            "dsKeyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf",
            "driverType": "Postgresql",
            "database": "public"
        },
        "category": "table",
        "connId": "g4nir",
        "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf/r0bo-ywee-rpys-pl97-92",
        "uniqueKey": "9160c5d9d2af1507148f1ed2554dcf96_g4nir",
        "catalog": "SampleTravelData",
        "schema": "public",
        "selected": false
    },
    {
        "id": "407487cdac2f4fc9328137ffb5413ddf",
        "name": "travel_details",
        "alias": "travel_details",
        "dataSource": {
            "id": "2202",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "SampleTravelData",
            "schema": "public",
            "connId": "g4nir",
            "dbId": "g4nir",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelData",
            "dsKeyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf",
            "driverType": "Postgresql",
            "database": "public"
        },
        "category": "table",
        "connId": "g4nir",
        "keyPath": "eoxv-a41s-y02h-qgbx-df/0x3o-kf9q-lzux-f7a6-xe/vbr1-xbh9-v2dg-11fy-i0/sd5d-x303-9pl9-m0x9-yf/rik5-hztv-0wyu-5buq-nw",
        "uniqueKey": "407487cdac2f4fc9328137ffb5413ddf_g4nir",
        "catalog": "SampleTravelData",
        "schema": "public",
        "selected": false
    }
];

describe('Testing findAllNestedObj func', () => {
    test('check if returned array is matching with given inputs', (done) =>{
        let result = findAllNestedObj(dsListToRenderFromRedux, 'category', 'table')
        expect(expResult).toEqual(result);
        done();
    })
})