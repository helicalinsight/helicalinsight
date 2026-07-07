
export const b_6197 = {
   removeNrmlTable: {
    input: {
        datasourceListToRender: [
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
                        "keyPath": "5qz6-6400-bskp-m1dp-9k/smub-tzlc-otmk-71y7-uv",
                        "key": "smub-tzlc-otmk-71y7-uv",
                        "uuid": "smub-tzlc-otmk-71y7-uv"
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
                        "keyPath": "5qz6-6400-bskp-m1dp-9k/xvsl-j9ot-kugn-lb3r-36",
                        "key": "xvsl-j9ot-kugn-lb3r-36",
                        "uuid": "xvsl-j9ot-kugn-lb3r-36"
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
                        "children": [
                            {
                                "name": "SQLJ",
                                "children": [],
                                "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/ooi8-al35-u0qz-ye43-1p",
                                "key": "ooi8-al35-u0qz-ye43-1p",
                                "uuid": "ooi8-al35-u0qz-ye43-1p",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            },
                            {
                                "name": "SYSFUN",
                                "children": [],
                                "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/4k06-8yrs-8ari-ieap-j0",
                                "key": "4k06-8yrs-8ari-ieap-j0",
                                "uuid": "4k06-8yrs-8ari-ieap-j0",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            },
                            {
                                "name": "SYSCAT",
                                "children": [],
                                "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/vxc0-c050-jb4d-tuc2-a0",
                                "key": "vxc0-c050-jb4d-tuc2-a0",
                                "uuid": "vxc0-c050-jb4d-tuc2-a0",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            },
                            {
                                "name": "HIUSER",
                                "children": [
                                    {
                                        "id": "4ac5d9f68b58bd7c0d179146e46795be",
                                        "name": "dimdate",
                                        "alias": "dimdate",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "i14gc",
                                            "dbId": "i14gc",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/xh7q-vajc-5zxh-ouwr-yn",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "category": "table",
                                        "connId": "i14gc",
                                        "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/xh7q-vajc-5zxh-ouwr-yn/7x36-gi39-2nbu-6ir9-dg",
                                        "uniqueKey": "4ac5d9f68b58bd7c0d179146e46795be_i14gc",
                                        "schema": "HIUSER",
                                        "selected": true
                                    },
                                    {
                                        "id": "4e1fd245f4d13b77be423a43f01d80b2",
                                        "name": "employee_details",
                                        "alias": "employee_details",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "i14gc",
                                            "dbId": "i14gc",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/xh7q-vajc-5zxh-ouwr-yn",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "category": "table",
                                        "connId": "i14gc",
                                        "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/xh7q-vajc-5zxh-ouwr-yn/owsm-8r8e-kaex-l4ib-lo",
                                        "uniqueKey": "4e1fd245f4d13b77be423a43f01d80b2_i14gc",
                                        "schema": "HIUSER",
                                        "selected": true
                                    },
                                    {
                                        "id": "be534112989b616b194bc59c2fb25a42",
                                        "name": "geo_cordinates",
                                        "alias": "geo_cordinates",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "i14gc",
                                            "dbId": "i14gc",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/xh7q-vajc-5zxh-ouwr-yn",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "category": "table",
                                        "connId": "i14gc",
                                        "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/xh7q-vajc-5zxh-ouwr-yn/q7mw-u8xb-qj21-huwb-64",
                                        "uniqueKey": "be534112989b616b194bc59c2fb25a42_i14gc",
                                        "schema": "HIUSER",
                                        "selected": false
                                    },
                                    {
                                        "id": "9645c648a1c0dbeec1287aaf1e996db3",
                                        "name": "meeting_details",
                                        "alias": "meeting_details",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "i14gc",
                                            "dbId": "i14gc",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/xh7q-vajc-5zxh-ouwr-yn",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "category": "table",
                                        "connId": "i14gc",
                                        "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/xh7q-vajc-5zxh-ouwr-yn/1n75-k3as-qh9z-d9eh-kb",
                                        "uniqueKey": "9645c648a1c0dbeec1287aaf1e996db3_i14gc",
                                        "schema": "HIUSER",
                                        "selected": false
                                    },
                                    {
                                        "id": "8a28627d07d04ef096d9935f12e0c7e9",
                                        "name": "travel_details",
                                        "alias": "travel_details",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "i14gc",
                                            "dbId": "i14gc",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/xh7q-vajc-5zxh-ouwr-yn",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "category": "table",
                                        "connId": "i14gc",
                                        "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/xh7q-vajc-5zxh-ouwr-yn/4m9d-tnpr-oo3l-zk9b-fn",
                                        "uniqueKey": "8a28627d07d04ef096d9935f12e0c7e9_i14gc",
                                        "schema": "HIUSER",
                                        "selected": false
                                    }
                                ],
                                "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/xh7q-vajc-5zxh-ouwr-yn",
                                "key": "xh7q-vajc-5zxh-ouwr-yn",
                                "uuid": "xh7q-vajc-5zxh-ouwr-yn",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby",
                                "fetched": true
                            },
                            {
                                "name": "SYSCS_DIAG",
                                "children": [],
                                "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/v7io-jzwp-5547-j9mc-sw",
                                "key": "v7io-jzwp-5547-j9mc-sw",
                                "uuid": "v7io-jzwp-5547-j9mc-sw",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            },
                            {
                                "name": "SYSCS_UTIL",
                                "children": [],
                                "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/uehl-z3b4-m5y4-r5zl-y4",
                                "key": "uehl-z3b4-m5y4-r5zl-y4",
                                "uuid": "uehl-z3b4-m5y4-r5zl-y4",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            },
                            {
                                "name": "SYSIBM",
                                "children": [],
                                "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/hem7-y3pk-gef5-d10a-ub",
                                "key": "hem7-y3pk-gef5-d10a-ub",
                                "uuid": "hem7-y3pk-gef5-d10a-ub",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            },
                            {
                                "name": "APP",
                                "children": [],
                                "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/hzhy-47bq-5vfb-wk1b-j6",
                                "key": "hzhy-47bq-5vfb-wk1b-j6",
                                "uuid": "hzhy-47bq-5vfb-wk1b-j6",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            },
                            {
                                "name": "NULLID",
                                "children": [],
                                "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/eha3-anyy-dm4g-k2az-j3",
                                "key": "eha3-anyy-dm4g-k2az-j3",
                                "uuid": "eha3-anyy-dm4g-k2az-j3",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            },
                            {
                                "name": "SYSPROC",
                                "children": [],
                                "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/heng-8p5n-98uk-7omi-0q",
                                "key": "heng-8p5n-98uk-7omi-0q",
                                "uuid": "heng-8p5n-98uk-7omi-0q",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            },
                            {
                                "name": "SYS",
                                "children": [],
                                "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/vlgt-kbmp-1fws-o5zt-qr",
                                "key": "vlgt-kbmp-1fws-o5zt-qr",
                                "uuid": "vlgt-kbmp-1fws-o5zt-qr",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            },
                            {
                                "name": "SYSSTAT",
                                "children": [],
                                "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/m7xd-v88e-n0tq-hhzu-gd",
                                "key": "m7xd-v88e-n0tq-hhzu-gd",
                                "uuid": "m7xd-v88e-n0tq-hhzu-gd",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            }
                        ],
                        "driverType": "Derby",
                        "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz",
                        "key": "sxgh-9ufi-yuqq-68q7-wz",
                        "uuid": "sxgh-9ufi-yuqq-68q7-wz",
                        "fetched": true
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
                        "keyPath": "5qz6-6400-bskp-m1dp-9k/1vrl-ly7w-5y8n-p7ke-wg",
                        "key": "1vrl-ly7w-5y8n-p7ke-wg",
                        "uuid": "1vrl-ly7w-5y8n-p7ke-wg"
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
                        "children": [
                            {
                                "name": "SQLJ",
                                "children": [],
                                "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/6xhh-llxr-prqp-lqdt-id",
                                "key": "6xhh-llxr-prqp-lqdt-id",
                                "uuid": "6xhh-llxr-prqp-lqdt-id",
                                "data": {
                                    "id": "1101",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "derby2"
                            },
                            {
                                "name": "SYSFUN",
                                "children": [],
                                "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/jf2s-o16j-clmx-5b6r-2j",
                                "key": "jf2s-o16j-clmx-5b6r-2j",
                                "uuid": "jf2s-o16j-clmx-5b6r-2j",
                                "data": {
                                    "id": "1101",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "derby2"
                            },
                            {
                                "name": "SYSCAT",
                                "children": [],
                                "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/zcaa-yxi0-0tns-s4fc-sn",
                                "key": "zcaa-yxi0-0tns-s4fc-sn",
                                "uuid": "zcaa-yxi0-0tns-s4fc-sn",
                                "data": {
                                    "id": "1101",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "derby2"
                            },
                            {
                                "name": "HIUSER",
                                "children": [
                                    {
                                        "id": "4ac5d9f68b58bd7c0d179146e46795be",
                                        "name": "dimdate",
                                        "alias": "dimdate",
                                        "dataSource": {
                                            "id": "1101",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "4icm6",
                                            "dbId": "4icm6",
                                            "classifier": "db.workflow",
                                            "datasourceName": "derby2",
                                            "dsKeyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/qosm-r5rc-pvud-vv9p-qh",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "category": "table",
                                        "connId": "4icm6",
                                        "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/qosm-r5rc-pvud-vv9p-qh/niqg-n0hc-j7vt-wvfz-1d",
                                        "uniqueKey": "4ac5d9f68b58bd7c0d179146e46795be_4icm6",
                                        "schema": "HIUSER",
                                        "selected": true
                                    },
                                    {
                                        "id": "4e1fd245f4d13b77be423a43f01d80b2",
                                        "name": "employee_details",
                                        "alias": "employee_details",
                                        "dataSource": {
                                            "id": "1101",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "4icm6",
                                            "dbId": "4icm6",
                                            "classifier": "db.workflow",
                                            "datasourceName": "derby2",
                                            "dsKeyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/qosm-r5rc-pvud-vv9p-qh",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "category": "table",
                                        "connId": "4icm6",
                                        "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/qosm-r5rc-pvud-vv9p-qh/av6s-5i7f-mb3c-5351-qv",
                                        "uniqueKey": "4e1fd245f4d13b77be423a43f01d80b2_4icm6",
                                        "schema": "HIUSER",
                                        "selected": false
                                    },
                                    {
                                        "id": "be534112989b616b194bc59c2fb25a42",
                                        "name": "geo_cordinates",
                                        "alias": "geo_cordinates",
                                        "dataSource": {
                                            "id": "1101",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "4icm6",
                                            "dbId": "4icm6",
                                            "classifier": "db.workflow",
                                            "datasourceName": "derby2",
                                            "dsKeyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/qosm-r5rc-pvud-vv9p-qh",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "category": "table",
                                        "connId": "4icm6",
                                        "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/qosm-r5rc-pvud-vv9p-qh/0edi-k0gu-fm9a-ypjm-sv",
                                        "uniqueKey": "be534112989b616b194bc59c2fb25a42_4icm6",
                                        "schema": "HIUSER",
                                        "selected": false
                                    },
                                    {
                                        "id": "9645c648a1c0dbeec1287aaf1e996db3",
                                        "name": "meeting_details",
                                        "alias": "meeting_details",
                                        "dataSource": {
                                            "id": "1101",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "4icm6",
                                            "dbId": "4icm6",
                                            "classifier": "db.workflow",
                                            "datasourceName": "derby2",
                                            "dsKeyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/qosm-r5rc-pvud-vv9p-qh",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "category": "table",
                                        "connId": "4icm6",
                                        "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/qosm-r5rc-pvud-vv9p-qh/rrho-ggwa-2035-srpl-xq",
                                        "uniqueKey": "9645c648a1c0dbeec1287aaf1e996db3_4icm6",
                                        "schema": "HIUSER",
                                        "selected": false
                                    },
                                    {
                                        "id": "8a28627d07d04ef096d9935f12e0c7e9",
                                        "name": "travel_details",
                                        "alias": "travel_details",
                                        "dataSource": {
                                            "id": "1101",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "4icm6",
                                            "dbId": "4icm6",
                                            "classifier": "db.workflow",
                                            "datasourceName": "derby2",
                                            "dsKeyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/qosm-r5rc-pvud-vv9p-qh",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "category": "table",
                                        "connId": "4icm6",
                                        "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/qosm-r5rc-pvud-vv9p-qh/pny4-5ul8-p7lp-71vt-p3",
                                        "uniqueKey": "8a28627d07d04ef096d9935f12e0c7e9_4icm6",
                                        "schema": "HIUSER",
                                        "selected": false
                                    }
                                ],
                                "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/qosm-r5rc-pvud-vv9p-qh",
                                "key": "qosm-r5rc-pvud-vv9p-qh",
                                "uuid": "qosm-r5rc-pvud-vv9p-qh",
                                "data": {
                                    "id": "1101",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "derby2",
                                "fetched": true
                            },
                            {
                                "name": "SYSCS_DIAG",
                                "children": [],
                                "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/g6ha-foxd-j86r-po2c-yh",
                                "key": "g6ha-foxd-j86r-po2c-yh",
                                "uuid": "g6ha-foxd-j86r-po2c-yh",
                                "data": {
                                    "id": "1101",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "derby2"
                            },
                            {
                                "name": "SYSCS_UTIL",
                                "children": [],
                                "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/xil1-ry8m-bjuo-xf40-dr",
                                "key": "xil1-ry8m-bjuo-xf40-dr",
                                "uuid": "xil1-ry8m-bjuo-xf40-dr",
                                "data": {
                                    "id": "1101",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "derby2"
                            },
                            {
                                "name": "SYSIBM",
                                "children": [],
                                "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/6il8-6w9v-xayj-hdvw-b3",
                                "key": "6il8-6w9v-xayj-hdvw-b3",
                                "uuid": "6il8-6w9v-xayj-hdvw-b3",
                                "data": {
                                    "id": "1101",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "derby2"
                            },
                            {
                                "name": "APP",
                                "children": [],
                                "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/v23x-ni5n-t1e5-0wr5-y0",
                                "key": "v23x-ni5n-t1e5-0wr5-y0",
                                "uuid": "v23x-ni5n-t1e5-0wr5-y0",
                                "data": {
                                    "id": "1101",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "derby2"
                            },
                            {
                                "name": "NULLID",
                                "children": [],
                                "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/ooij-u9ci-3338-on5e-qq",
                                "key": "ooij-u9ci-3338-on5e-qq",
                                "uuid": "ooij-u9ci-3338-on5e-qq",
                                "data": {
                                    "id": "1101",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "derby2"
                            },
                            {
                                "name": "SYSPROC",
                                "children": [],
                                "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/2t93-h6ax-p9ep-jw0e-bh",
                                "key": "2t93-h6ax-p9ep-jw0e-bh",
                                "uuid": "2t93-h6ax-p9ep-jw0e-bh",
                                "data": {
                                    "id": "1101",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "derby2"
                            },
                            {
                                "name": "SYS",
                                "children": [],
                                "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/54zu-uqy0-e0wu-85nx-tx",
                                "key": "54zu-uqy0-e0wu-85nx-tx",
                                "uuid": "54zu-uqy0-e0wu-85nx-tx",
                                "data": {
                                    "id": "1101",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "derby2"
                            },
                            {
                                "name": "SYSSTAT",
                                "children": [],
                                "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/lfda-frfw-pt59-iuig-ww",
                                "key": "lfda-frfw-pt59-iuig-ww",
                                "uuid": "lfda-frfw-pt59-iuig-ww",
                                "data": {
                                    "id": "1101",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "derby2"
                            }
                        ],
                        "driverType": "Derby",
                        "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc",
                        "key": "zg9y-lbvm-feff-twvw-cc",
                        "uuid": "zg9y-lbvm-feff-twvw-cc",
                        "fetched": true
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
                        "keyPath": "5qz6-6400-bskp-m1dp-9k/xfpm-xlth-a1cr-om6p-d6",
                        "key": "xfpm-xlth-a1cr-om6p-d6",
                        "uuid": "xfpm-xlth-a1cr-om6p-d6"
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
                        "keyPath": "5qz6-6400-bskp-m1dp-9k/aody-8ev8-hzf2-ydtw-g0",
                        "key": "aody-8ev8-hzf2-ydtw-g0",
                        "uuid": "aody-8ev8-hzf2-ydtw-g0"
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
                        "keyPath": "5qz6-6400-bskp-m1dp-9k/28l8-rzyf-mh37-x3l8-zp",
                        "key": "28l8-rzyf-mh37-x3l8-zp",
                        "uuid": "28l8-rzyf-mh37-x3l8-zp"
                    }
                ],
                "key": "5qz6-6400-bskp-m1dp-9k",
                "uuid": "5qz6-6400-bskp-m1dp-9k",
                "keyPath": "5qz6-6400-bskp-m1dp-9k",
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
                        "keyPath": "tkei-tsrj-tx2x-meuv-xh/3fj1-0m5s-xd0r-snmu-wv",
                        "key": "3fj1-0m5s-xd0r-snmu-wv",
                        "uuid": "3fj1-0m5s-xd0r-snmu-wv"
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
                        "keyPath": "tkei-tsrj-tx2x-meuv-xh/uuft-rgfb-t7lz-cdex-u5",
                        "key": "uuft-rgfb-t7lz-cdex-u5",
                        "uuid": "uuft-rgfb-t7lz-cdex-u5"
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
                        "keyPath": "tkei-tsrj-tx2x-meuv-xh/fanb-sx0b-oxvp-zwz9-x8",
                        "key": "fanb-sx0b-oxvp-zwz9-x8",
                        "uuid": "fanb-sx0b-oxvp-zwz9-x8"
                    }
                ],
                "key": "tkei-tsrj-tx2x-meuv-xh",
                "uuid": "tkei-tsrj-tx2x-meuv-xh",
                "keyPath": "tkei-tsrj-tx2x-meuv-xh",
                "category": "dsGroup",
                "imgUrl": "images/data_sources/defaut_datasource.png"
            },
            {
                "name": "Elasticsearch",
                "children": [
                    {
                        "data": {
                            "id": "2701",
                            "type": "dynamicDataSource"
                        },
                        "dataSourceProvider": "tomcat",
                        "type": "dynamicDataSource",
                        "permissionLevel": 5,
                        "driver": "org.elasticsearch.xpack.sql.jdbc.EsDriver",
                        "name": "test",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "Elasticsearch",
                        "keyPath": "2jt0-08ff-ch93-m2oe-r6/55ao-kb19-i6wl-pmvd-mi",
                        "key": "55ao-kb19-i6wl-pmvd-mi",
                        "uuid": "55ao-kb19-i6wl-pmvd-mi"
                    }
                ],
                "key": "2jt0-08ff-ch93-m2oe-r6",
                "uuid": "2jt0-08ff-ch93-m2oe-r6",
                "keyPath": "2jt0-08ff-ch93-m2oe-r6",
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
                        "keyPath": "6vhy-eehy-a71b-3gjh-pp/4wzl-msjt-5ois-4rom-m7",
                        "key": "4wzl-msjt-5ois-4rom-m7",
                        "uuid": "4wzl-msjt-5ois-4rom-m7"
                    }
                ],
                "key": "6vhy-eehy-a71b-3gjh-pp",
                "uuid": "6vhy-eehy-a71b-3gjh-pp",
                "keyPath": "6vhy-eehy-a71b-3gjh-pp",
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
                        "keyPath": "t01v-qa67-hvyb-jr32-lo/tzn0-ekfp-ajif-72cc-3r",
                        "key": "tzn0-ekfp-ajif-72cc-3r",
                        "uuid": "tzn0-ekfp-ajif-72cc-3r"
                    }
                ],
                "key": "t01v-qa67-hvyb-jr32-lo",
                "uuid": "t01v-qa67-hvyb-jr32-lo",
                "keyPath": "t01v-qa67-hvyb-jr32-lo",
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
                        "keyPath": "3w2z-kvqm-05kc-wfqb-5a/askr-r8sj-jj9z-1hvl-ai",
                        "key": "askr-r8sj-jj9z-1hvl-ai",
                        "uuid": "askr-r8sj-jj9z-1hvl-ai"
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
                        "keyPath": "3w2z-kvqm-05kc-wfqb-5a/9hwv-5hlh-3i8t-dj4f-qn",
                        "key": "9hwv-5hlh-3i8t-dj4f-qn",
                        "uuid": "9hwv-5hlh-3i8t-dj4f-qn"
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
                        "children": [],
                        "driverType": "Postgresql",
                        "keyPath": "3w2z-kvqm-05kc-wfqb-5a/vy6x-ommg-tg25-5397-4s",
                        "key": "vy6x-ommg-tg25-5397-4s",
                        "uuid": "vy6x-ommg-tg25-5397-4s"
                    }
                ],
                "key": "3w2z-kvqm-05kc-wfqb-5a",
                "uuid": "3w2z-kvqm-05kc-wfqb-5a",
                "keyPath": "3w2z-kvqm-05kc-wfqb-5a",
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
                        "keyPath": "51pv-m1il-35zr-fybp-ih/iuod-0o74-r76a-3v4c-ap",
                        "key": "iuod-0o74-r76a-3v4c-ap",
                        "uuid": "iuod-0o74-r76a-3v4c-ap"
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
                        "keyPath": "51pv-m1il-35zr-fybp-ih/vy0n-m1r8-1iy7-znxd-bd",
                        "key": "vy0n-m1r8-1iy7-znxd-bd",
                        "uuid": "vy0n-m1r8-1iy7-znxd-bd"
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
                        "keyPath": "51pv-m1il-35zr-fybp-ih/9lf6-bakh-utwn-3y1h-ka",
                        "key": "9lf6-bakh-utwn-3y1h-ka",
                        "uuid": "9lf6-bakh-utwn-3y1h-ka"
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
                        "keyPath": "51pv-m1il-35zr-fybp-ih/mnd5-4flj-k7ou-semu-8g",
                        "key": "mnd5-4flj-k7ou-semu-8g",
                        "uuid": "mnd5-4flj-k7ou-semu-8g"
                    }
                ],
                "key": "51pv-m1il-35zr-fybp-ih",
                "uuid": "51pv-m1il-35zr-fybp-ih",
                "keyPath": "51pv-m1il-35zr-fybp-ih",
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
                        "keyPath": "4ao9-3eyq-k4mf-mvt5-uc/grs5-3jo2-ffgi-mres-m4",
                        "key": "grs5-3jo2-ffgi-mres-m4",
                        "uuid": "grs5-3jo2-ffgi-mres-m4"
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
                        "keyPath": "4ao9-3eyq-k4mf-mvt5-uc/1qsg-6sfy-yg49-cfyn-rx",
                        "key": "1qsg-6sfy-yg49-cfyn-rx",
                        "uuid": "1qsg-6sfy-yg49-cfyn-rx"
                    }
                ],
                "key": "4ao9-3eyq-k4mf-mvt5-uc",
                "uuid": "4ao9-3eyq-k4mf-mvt5-uc",
                "keyPath": "4ao9-3eyq-k4mf-mvt5-uc",
                "category": "dsGroup",
                "imgUrl": "images/data_sources/defaut_datasource.png"
            }
        ],
        record: {
            "id": "4ac5d9f68b58bd7c0d179146e46795be",
            "name": "dimdate",
            "alias": "dimdate",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "i14gc",
                "dbId": "i14gc",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/xh7q-vajc-5zxh-ouwr-yn",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "category": "table",
            "connId": "i14gc",
            "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/xh7q-vajc-5zxh-ouwr-yn/7x36-gi39-2nbu-6ir9-dg",
            "uniqueKey": "4ac5d9f68b58bd7c0d179146e46795be_i14gc",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "dimdate"
        },
        task: 'remove',
        options: {
            value : false,
            type : 'selectTables'
        }
    },
    result: [
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
                    "keyPath": "5qz6-6400-bskp-m1dp-9k/smub-tzlc-otmk-71y7-uv",
                    "key": "smub-tzlc-otmk-71y7-uv",
                    "uuid": "smub-tzlc-otmk-71y7-uv"
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
                    "keyPath": "5qz6-6400-bskp-m1dp-9k/xvsl-j9ot-kugn-lb3r-36",
                    "key": "xvsl-j9ot-kugn-lb3r-36",
                    "uuid": "xvsl-j9ot-kugn-lb3r-36"
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
                    "children": [
                        {
                            "name": "SQLJ",
                            "children": [],
                            "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/ooi8-al35-u0qz-ye43-1p",
                            "key": "ooi8-al35-u0qz-ye43-1p",
                            "uuid": "ooi8-al35-u0qz-ye43-1p",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        },
                        {
                            "name": "SYSFUN",
                            "children": [],
                            "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/4k06-8yrs-8ari-ieap-j0",
                            "key": "4k06-8yrs-8ari-ieap-j0",
                            "uuid": "4k06-8yrs-8ari-ieap-j0",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        },
                        {
                            "name": "SYSCAT",
                            "children": [],
                            "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/vxc0-c050-jb4d-tuc2-a0",
                            "key": "vxc0-c050-jb4d-tuc2-a0",
                            "uuid": "vxc0-c050-jb4d-tuc2-a0",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        },
                        {
                            "name": "HIUSER",
                            "children": [
                                {
                                    "id": "4ac5d9f68b58bd7c0d179146e46795be",
                                    "name": "dimdate",
                                    "alias": "dimdate",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "i14gc",
                                        "dbId": "i14gc",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/xh7q-vajc-5zxh-ouwr-yn",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "category": "table",
                                    "connId": "i14gc",
                                    "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/xh7q-vajc-5zxh-ouwr-yn/7x36-gi39-2nbu-6ir9-dg",
                                    "uniqueKey": "4ac5d9f68b58bd7c0d179146e46795be_i14gc",
                                    "schema": "HIUSER",
                                    "selected": false
                                },
                                {
                                    "id": "4e1fd245f4d13b77be423a43f01d80b2",
                                    "name": "employee_details",
                                    "alias": "employee_details",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "i14gc",
                                        "dbId": "i14gc",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/xh7q-vajc-5zxh-ouwr-yn",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "category": "table",
                                    "connId": "i14gc",
                                    "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/xh7q-vajc-5zxh-ouwr-yn/owsm-8r8e-kaex-l4ib-lo",
                                    "uniqueKey": "4e1fd245f4d13b77be423a43f01d80b2_i14gc",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "be534112989b616b194bc59c2fb25a42",
                                    "name": "geo_cordinates",
                                    "alias": "geo_cordinates",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "i14gc",
                                        "dbId": "i14gc",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/xh7q-vajc-5zxh-ouwr-yn",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "category": "table",
                                    "connId": "i14gc",
                                    "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/xh7q-vajc-5zxh-ouwr-yn/q7mw-u8xb-qj21-huwb-64",
                                    "uniqueKey": "be534112989b616b194bc59c2fb25a42_i14gc",
                                    "schema": "HIUSER",
                                    "selected": false
                                },
                                {
                                    "id": "9645c648a1c0dbeec1287aaf1e996db3",
                                    "name": "meeting_details",
                                    "alias": "meeting_details",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "i14gc",
                                        "dbId": "i14gc",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/xh7q-vajc-5zxh-ouwr-yn",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "category": "table",
                                    "connId": "i14gc",
                                    "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/xh7q-vajc-5zxh-ouwr-yn/1n75-k3as-qh9z-d9eh-kb",
                                    "uniqueKey": "9645c648a1c0dbeec1287aaf1e996db3_i14gc",
                                    "schema": "HIUSER",
                                    "selected": false
                                },
                                {
                                    "id": "8a28627d07d04ef096d9935f12e0c7e9",
                                    "name": "travel_details",
                                    "alias": "travel_details",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "i14gc",
                                        "dbId": "i14gc",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/xh7q-vajc-5zxh-ouwr-yn",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "category": "table",
                                    "connId": "i14gc",
                                    "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/xh7q-vajc-5zxh-ouwr-yn/4m9d-tnpr-oo3l-zk9b-fn",
                                    "uniqueKey": "8a28627d07d04ef096d9935f12e0c7e9_i14gc",
                                    "schema": "HIUSER",
                                    "selected": false
                                }
                            ],
                            "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/xh7q-vajc-5zxh-ouwr-yn",
                            "key": "xh7q-vajc-5zxh-ouwr-yn",
                            "uuid": "xh7q-vajc-5zxh-ouwr-yn",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby",
                            "fetched": true
                        },
                        {
                            "name": "SYSCS_DIAG",
                            "children": [],
                            "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/v7io-jzwp-5547-j9mc-sw",
                            "key": "v7io-jzwp-5547-j9mc-sw",
                            "uuid": "v7io-jzwp-5547-j9mc-sw",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        },
                        {
                            "name": "SYSCS_UTIL",
                            "children": [],
                            "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/uehl-z3b4-m5y4-r5zl-y4",
                            "key": "uehl-z3b4-m5y4-r5zl-y4",
                            "uuid": "uehl-z3b4-m5y4-r5zl-y4",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        },
                        {
                            "name": "SYSIBM",
                            "children": [],
                            "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/hem7-y3pk-gef5-d10a-ub",
                            "key": "hem7-y3pk-gef5-d10a-ub",
                            "uuid": "hem7-y3pk-gef5-d10a-ub",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        },
                        {
                            "name": "APP",
                            "children": [],
                            "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/hzhy-47bq-5vfb-wk1b-j6",
                            "key": "hzhy-47bq-5vfb-wk1b-j6",
                            "uuid": "hzhy-47bq-5vfb-wk1b-j6",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        },
                        {
                            "name": "NULLID",
                            "children": [],
                            "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/eha3-anyy-dm4g-k2az-j3",
                            "key": "eha3-anyy-dm4g-k2az-j3",
                            "uuid": "eha3-anyy-dm4g-k2az-j3",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        },
                        {
                            "name": "SYSPROC",
                            "children": [],
                            "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/heng-8p5n-98uk-7omi-0q",
                            "key": "heng-8p5n-98uk-7omi-0q",
                            "uuid": "heng-8p5n-98uk-7omi-0q",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        },
                        {
                            "name": "SYS",
                            "children": [],
                            "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/vlgt-kbmp-1fws-o5zt-qr",
                            "key": "vlgt-kbmp-1fws-o5zt-qr",
                            "uuid": "vlgt-kbmp-1fws-o5zt-qr",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        },
                        {
                            "name": "SYSSTAT",
                            "children": [],
                            "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz/m7xd-v88e-n0tq-hhzu-gd",
                            "key": "m7xd-v88e-n0tq-hhzu-gd",
                            "uuid": "m7xd-v88e-n0tq-hhzu-gd",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "5qz6-6400-bskp-m1dp-9k/sxgh-9ufi-yuqq-68q7-wz",
                    "key": "sxgh-9ufi-yuqq-68q7-wz",
                    "uuid": "sxgh-9ufi-yuqq-68q7-wz",
                    "fetched": true
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
                    "keyPath": "5qz6-6400-bskp-m1dp-9k/1vrl-ly7w-5y8n-p7ke-wg",
                    "key": "1vrl-ly7w-5y8n-p7ke-wg",
                    "uuid": "1vrl-ly7w-5y8n-p7ke-wg"
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
                    "children": [
                        {
                            "name": "SQLJ",
                            "children": [],
                            "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/6xhh-llxr-prqp-lqdt-id",
                            "key": "6xhh-llxr-prqp-lqdt-id",
                            "uuid": "6xhh-llxr-prqp-lqdt-id",
                            "data": {
                                "id": "1101",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "derby2"
                        },
                        {
                            "name": "SYSFUN",
                            "children": [],
                            "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/jf2s-o16j-clmx-5b6r-2j",
                            "key": "jf2s-o16j-clmx-5b6r-2j",
                            "uuid": "jf2s-o16j-clmx-5b6r-2j",
                            "data": {
                                "id": "1101",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "derby2"
                        },
                        {
                            "name": "SYSCAT",
                            "children": [],
                            "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/zcaa-yxi0-0tns-s4fc-sn",
                            "key": "zcaa-yxi0-0tns-s4fc-sn",
                            "uuid": "zcaa-yxi0-0tns-s4fc-sn",
                            "data": {
                                "id": "1101",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "derby2"
                        },
                        {
                            "name": "HIUSER",
                            "children": [
                                {
                                    "id": "4ac5d9f68b58bd7c0d179146e46795be",
                                    "name": "dimdate",
                                    "alias": "dimdate",
                                    "dataSource": {
                                        "id": "1101",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "4icm6",
                                        "dbId": "4icm6",
                                        "classifier": "db.workflow",
                                        "datasourceName": "derby2",
                                        "dsKeyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/qosm-r5rc-pvud-vv9p-qh",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "category": "table",
                                    "connId": "4icm6",
                                    "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/qosm-r5rc-pvud-vv9p-qh/niqg-n0hc-j7vt-wvfz-1d",
                                    "uniqueKey": "4ac5d9f68b58bd7c0d179146e46795be_4icm6",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "4e1fd245f4d13b77be423a43f01d80b2",
                                    "name": "employee_details",
                                    "alias": "employee_details",
                                    "dataSource": {
                                        "id": "1101",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "4icm6",
                                        "dbId": "4icm6",
                                        "classifier": "db.workflow",
                                        "datasourceName": "derby2",
                                        "dsKeyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/qosm-r5rc-pvud-vv9p-qh",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "category": "table",
                                    "connId": "4icm6",
                                    "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/qosm-r5rc-pvud-vv9p-qh/av6s-5i7f-mb3c-5351-qv",
                                    "uniqueKey": "4e1fd245f4d13b77be423a43f01d80b2_4icm6",
                                    "schema": "HIUSER",
                                    "selected": false
                                },
                                {
                                    "id": "be534112989b616b194bc59c2fb25a42",
                                    "name": "geo_cordinates",
                                    "alias": "geo_cordinates",
                                    "dataSource": {
                                        "id": "1101",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "4icm6",
                                        "dbId": "4icm6",
                                        "classifier": "db.workflow",
                                        "datasourceName": "derby2",
                                        "dsKeyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/qosm-r5rc-pvud-vv9p-qh",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "category": "table",
                                    "connId": "4icm6",
                                    "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/qosm-r5rc-pvud-vv9p-qh/0edi-k0gu-fm9a-ypjm-sv",
                                    "uniqueKey": "be534112989b616b194bc59c2fb25a42_4icm6",
                                    "schema": "HIUSER",
                                    "selected": false
                                },
                                {
                                    "id": "9645c648a1c0dbeec1287aaf1e996db3",
                                    "name": "meeting_details",
                                    "alias": "meeting_details",
                                    "dataSource": {
                                        "id": "1101",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "4icm6",
                                        "dbId": "4icm6",
                                        "classifier": "db.workflow",
                                        "datasourceName": "derby2",
                                        "dsKeyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/qosm-r5rc-pvud-vv9p-qh",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "category": "table",
                                    "connId": "4icm6",
                                    "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/qosm-r5rc-pvud-vv9p-qh/rrho-ggwa-2035-srpl-xq",
                                    "uniqueKey": "9645c648a1c0dbeec1287aaf1e996db3_4icm6",
                                    "schema": "HIUSER",
                                    "selected": false
                                },
                                {
                                    "id": "8a28627d07d04ef096d9935f12e0c7e9",
                                    "name": "travel_details",
                                    "alias": "travel_details",
                                    "dataSource": {
                                        "id": "1101",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "4icm6",
                                        "dbId": "4icm6",
                                        "classifier": "db.workflow",
                                        "datasourceName": "derby2",
                                        "dsKeyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/qosm-r5rc-pvud-vv9p-qh",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "category": "table",
                                    "connId": "4icm6",
                                    "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/qosm-r5rc-pvud-vv9p-qh/pny4-5ul8-p7lp-71vt-p3",
                                    "uniqueKey": "8a28627d07d04ef096d9935f12e0c7e9_4icm6",
                                    "schema": "HIUSER",
                                    "selected": false
                                }
                            ],
                            "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/qosm-r5rc-pvud-vv9p-qh",
                            "key": "qosm-r5rc-pvud-vv9p-qh",
                            "uuid": "qosm-r5rc-pvud-vv9p-qh",
                            "data": {
                                "id": "1101",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "derby2",
                            "fetched": true
                        },
                        {
                            "name": "SYSCS_DIAG",
                            "children": [],
                            "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/g6ha-foxd-j86r-po2c-yh",
                            "key": "g6ha-foxd-j86r-po2c-yh",
                            "uuid": "g6ha-foxd-j86r-po2c-yh",
                            "data": {
                                "id": "1101",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "derby2"
                        },
                        {
                            "name": "SYSCS_UTIL",
                            "children": [],
                            "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/xil1-ry8m-bjuo-xf40-dr",
                            "key": "xil1-ry8m-bjuo-xf40-dr",
                            "uuid": "xil1-ry8m-bjuo-xf40-dr",
                            "data": {
                                "id": "1101",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "derby2"
                        },
                        {
                            "name": "SYSIBM",
                            "children": [],
                            "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/6il8-6w9v-xayj-hdvw-b3",
                            "key": "6il8-6w9v-xayj-hdvw-b3",
                            "uuid": "6il8-6w9v-xayj-hdvw-b3",
                            "data": {
                                "id": "1101",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "derby2"
                        },
                        {
                            "name": "APP",
                            "children": [],
                            "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/v23x-ni5n-t1e5-0wr5-y0",
                            "key": "v23x-ni5n-t1e5-0wr5-y0",
                            "uuid": "v23x-ni5n-t1e5-0wr5-y0",
                            "data": {
                                "id": "1101",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "derby2"
                        },
                        {
                            "name": "NULLID",
                            "children": [],
                            "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/ooij-u9ci-3338-on5e-qq",
                            "key": "ooij-u9ci-3338-on5e-qq",
                            "uuid": "ooij-u9ci-3338-on5e-qq",
                            "data": {
                                "id": "1101",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "derby2"
                        },
                        {
                            "name": "SYSPROC",
                            "children": [],
                            "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/2t93-h6ax-p9ep-jw0e-bh",
                            "key": "2t93-h6ax-p9ep-jw0e-bh",
                            "uuid": "2t93-h6ax-p9ep-jw0e-bh",
                            "data": {
                                "id": "1101",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "derby2"
                        },
                        {
                            "name": "SYS",
                            "children": [],
                            "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/54zu-uqy0-e0wu-85nx-tx",
                            "key": "54zu-uqy0-e0wu-85nx-tx",
                            "uuid": "54zu-uqy0-e0wu-85nx-tx",
                            "data": {
                                "id": "1101",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "derby2"
                        },
                        {
                            "name": "SYSSTAT",
                            "children": [],
                            "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc/lfda-frfw-pt59-iuig-ww",
                            "key": "lfda-frfw-pt59-iuig-ww",
                            "uuid": "lfda-frfw-pt59-iuig-ww",
                            "data": {
                                "id": "1101",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "derby2"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "5qz6-6400-bskp-m1dp-9k/zg9y-lbvm-feff-twvw-cc",
                    "key": "zg9y-lbvm-feff-twvw-cc",
                    "uuid": "zg9y-lbvm-feff-twvw-cc",
                    "fetched": true
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
                    "keyPath": "5qz6-6400-bskp-m1dp-9k/xfpm-xlth-a1cr-om6p-d6",
                    "key": "xfpm-xlth-a1cr-om6p-d6",
                    "uuid": "xfpm-xlth-a1cr-om6p-d6"
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
                    "keyPath": "5qz6-6400-bskp-m1dp-9k/aody-8ev8-hzf2-ydtw-g0",
                    "key": "aody-8ev8-hzf2-ydtw-g0",
                    "uuid": "aody-8ev8-hzf2-ydtw-g0"
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
                    "keyPath": "5qz6-6400-bskp-m1dp-9k/28l8-rzyf-mh37-x3l8-zp",
                    "key": "28l8-rzyf-mh37-x3l8-zp",
                    "uuid": "28l8-rzyf-mh37-x3l8-zp"
                }
            ],
            "key": "5qz6-6400-bskp-m1dp-9k",
            "uuid": "5qz6-6400-bskp-m1dp-9k",
            "keyPath": "5qz6-6400-bskp-m1dp-9k",
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
                    "keyPath": "tkei-tsrj-tx2x-meuv-xh/3fj1-0m5s-xd0r-snmu-wv",
                    "key": "3fj1-0m5s-xd0r-snmu-wv",
                    "uuid": "3fj1-0m5s-xd0r-snmu-wv"
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
                    "keyPath": "tkei-tsrj-tx2x-meuv-xh/uuft-rgfb-t7lz-cdex-u5",
                    "key": "uuft-rgfb-t7lz-cdex-u5",
                    "uuid": "uuft-rgfb-t7lz-cdex-u5"
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
                    "keyPath": "tkei-tsrj-tx2x-meuv-xh/fanb-sx0b-oxvp-zwz9-x8",
                    "key": "fanb-sx0b-oxvp-zwz9-x8",
                    "uuid": "fanb-sx0b-oxvp-zwz9-x8"
                }
            ],
            "key": "tkei-tsrj-tx2x-meuv-xh",
            "uuid": "tkei-tsrj-tx2x-meuv-xh",
            "keyPath": "tkei-tsrj-tx2x-meuv-xh",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Elasticsearch",
            "children": [
                {
                    "data": {
                        "id": "2701",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.elasticsearch.xpack.sql.jdbc.EsDriver",
                    "name": "test",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Elasticsearch",
                    "keyPath": "2jt0-08ff-ch93-m2oe-r6/55ao-kb19-i6wl-pmvd-mi",
                    "key": "55ao-kb19-i6wl-pmvd-mi",
                    "uuid": "55ao-kb19-i6wl-pmvd-mi"
                }
            ],
            "key": "2jt0-08ff-ch93-m2oe-r6",
            "uuid": "2jt0-08ff-ch93-m2oe-r6",
            "keyPath": "2jt0-08ff-ch93-m2oe-r6",
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
                    "keyPath": "6vhy-eehy-a71b-3gjh-pp/4wzl-msjt-5ois-4rom-m7",
                    "key": "4wzl-msjt-5ois-4rom-m7",
                    "uuid": "4wzl-msjt-5ois-4rom-m7"
                }
            ],
            "key": "6vhy-eehy-a71b-3gjh-pp",
            "uuid": "6vhy-eehy-a71b-3gjh-pp",
            "keyPath": "6vhy-eehy-a71b-3gjh-pp",
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
                    "keyPath": "t01v-qa67-hvyb-jr32-lo/tzn0-ekfp-ajif-72cc-3r",
                    "key": "tzn0-ekfp-ajif-72cc-3r",
                    "uuid": "tzn0-ekfp-ajif-72cc-3r"
                }
            ],
            "key": "t01v-qa67-hvyb-jr32-lo",
            "uuid": "t01v-qa67-hvyb-jr32-lo",
            "keyPath": "t01v-qa67-hvyb-jr32-lo",
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
                    "keyPath": "3w2z-kvqm-05kc-wfqb-5a/askr-r8sj-jj9z-1hvl-ai",
                    "key": "askr-r8sj-jj9z-1hvl-ai",
                    "uuid": "askr-r8sj-jj9z-1hvl-ai"
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
                    "keyPath": "3w2z-kvqm-05kc-wfqb-5a/9hwv-5hlh-3i8t-dj4f-qn",
                    "key": "9hwv-5hlh-3i8t-dj4f-qn",
                    "uuid": "9hwv-5hlh-3i8t-dj4f-qn"
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
                    "children": [],
                    "driverType": "Postgresql",
                    "keyPath": "3w2z-kvqm-05kc-wfqb-5a/vy6x-ommg-tg25-5397-4s",
                    "key": "vy6x-ommg-tg25-5397-4s",
                    "uuid": "vy6x-ommg-tg25-5397-4s"
                }
            ],
            "key": "3w2z-kvqm-05kc-wfqb-5a",
            "uuid": "3w2z-kvqm-05kc-wfqb-5a",
            "keyPath": "3w2z-kvqm-05kc-wfqb-5a",
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
                    "keyPath": "51pv-m1il-35zr-fybp-ih/iuod-0o74-r76a-3v4c-ap",
                    "key": "iuod-0o74-r76a-3v4c-ap",
                    "uuid": "iuod-0o74-r76a-3v4c-ap"
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
                    "keyPath": "51pv-m1il-35zr-fybp-ih/vy0n-m1r8-1iy7-znxd-bd",
                    "key": "vy0n-m1r8-1iy7-znxd-bd",
                    "uuid": "vy0n-m1r8-1iy7-znxd-bd"
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
                    "keyPath": "51pv-m1il-35zr-fybp-ih/9lf6-bakh-utwn-3y1h-ka",
                    "key": "9lf6-bakh-utwn-3y1h-ka",
                    "uuid": "9lf6-bakh-utwn-3y1h-ka"
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
                    "keyPath": "51pv-m1il-35zr-fybp-ih/mnd5-4flj-k7ou-semu-8g",
                    "key": "mnd5-4flj-k7ou-semu-8g",
                    "uuid": "mnd5-4flj-k7ou-semu-8g"
                }
            ],
            "key": "51pv-m1il-35zr-fybp-ih",
            "uuid": "51pv-m1il-35zr-fybp-ih",
            "keyPath": "51pv-m1il-35zr-fybp-ih",
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
                    "keyPath": "4ao9-3eyq-k4mf-mvt5-uc/grs5-3jo2-ffgi-mres-m4",
                    "key": "grs5-3jo2-ffgi-mres-m4",
                    "uuid": "grs5-3jo2-ffgi-mres-m4"
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
                    "keyPath": "4ao9-3eyq-k4mf-mvt5-uc/1qsg-6sfy-yg49-cfyn-rx",
                    "key": "1qsg-6sfy-yg49-cfyn-rx",
                    "uuid": "1qsg-6sfy-yg49-cfyn-rx"
                }
            ],
            "key": "4ao9-3eyq-k4mf-mvt5-uc",
            "uuid": "4ao9-3eyq-k4mf-mvt5-uc",
            "keyPath": "4ao9-3eyq-k4mf-mvt5-uc",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        }
    ]
   },
   removeTableFromCatalog: {
    input: {
        datasourceListToRender: [
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
                        "keyPath": "0qej-gll1-lmtc-sdze-h8/k5ps-tqtt-hvih-2vk2-b2",
                        "key": "k5ps-tqtt-hvih-2vk2-b2",
                        "uuid": "k5ps-tqtt-hvih-2vk2-b2"
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
                        "keyPath": "0qej-gll1-lmtc-sdze-h8/ad98-pykh-0vpa-ewgx-2p",
                        "key": "ad98-pykh-0vpa-ewgx-2p",
                        "uuid": "ad98-pykh-0vpa-ewgx-2p"
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
                        "keyPath": "0qej-gll1-lmtc-sdze-h8/h3o2-oam8-o4sh-lhaz-90",
                        "key": "h3o2-oam8-o4sh-lhaz-90",
                        "uuid": "h3o2-oam8-o4sh-lhaz-90"
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
                        "keyPath": "0qej-gll1-lmtc-sdze-h8/0nte-pu35-fgi3-pl07-n2",
                        "key": "0nte-pu35-fgi3-pl07-n2",
                        "uuid": "0nte-pu35-fgi3-pl07-n2"
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
                        "keyPath": "0qej-gll1-lmtc-sdze-h8/2ecc-6onl-uxgi-kcpf-n7",
                        "key": "2ecc-6onl-uxgi-kcpf-n7",
                        "uuid": "2ecc-6onl-uxgi-kcpf-n7"
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
                        "keyPath": "0qej-gll1-lmtc-sdze-h8/qr85-n3fo-cvgz-871a-ej",
                        "key": "qr85-n3fo-cvgz-871a-ej",
                        "uuid": "qr85-n3fo-cvgz-871a-ej"
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
                        "keyPath": "0qej-gll1-lmtc-sdze-h8/15bq-5fx9-m8vi-y9cp-8p",
                        "key": "15bq-5fx9-m8vi-y9cp-8p",
                        "uuid": "15bq-5fx9-m8vi-y9cp-8p"
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
                        "keyPath": "0qej-gll1-lmtc-sdze-h8/st5v-su3u-r8xk-1pqq-rk",
                        "key": "st5v-su3u-r8xk-1pqq-rk",
                        "uuid": "st5v-su3u-r8xk-1pqq-rk"
                    }
                ],
                "key": "0qej-gll1-lmtc-sdze-h8",
                "uuid": "0qej-gll1-lmtc-sdze-h8",
                "keyPath": "0qej-gll1-lmtc-sdze-h8",
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
                        "children": [
                            {
                                "name": "booking1662345944710kenxhxumioueuzfj",
                                "schemas": [
                                    {
                                        "name": "pg_catalog",
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
                                        "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a8if-agy7-1gmt-uph7-wj",
                                        "key": "a8if-agy7-1gmt-uph7-wj",
                                        "uuid": "a8if-agy7-1gmt-uph7-wj",
                                        "category": "schema",
                                        "children": [],
                                        "catalog": "booking1662345944710kenxhxumioueuzfj",
                                        "datasourceName": "Groovy 1"
                                    },
                                    {
                                        "name": "public",
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
                                        "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly",
                                        "key": "a9lf-3253-v48u-c7ec-ly",
                                        "uuid": "a9lf-3253-v48u-c7ec-ly",
                                        "category": "schema",
                                        "children": [
                                            {
                                                "id": "ce367e8d42ec1b746afdd73c2a07e7b3",
                                                "name": "appartments",
                                                "alias": "appartments",
                                                "dataSource": {
                                                    "id": "1",
                                                    "type": "sql.jdbc.groovy.managed",
                                                    "baseType": "global.jdbc",
                                                    "catSchemaPredicted": false,
                                                    "sync": false,
                                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                    "schema": "public",
                                                    "dir": "Gagan",
                                                    "connId": "cwh9e",
                                                    "dbId": "cwh9e",
                                                    "classifier": "db.workflow",
                                                    "datasourceName": "Groovy 1",
                                                    "dsKeyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly",
                                                    "driverType": "DynamicSwitch",
                                                    "database": "public"
                                                },
                                                "category": "table",
                                                "connId": "cwh9e",
                                                "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly/3t73-d2jj-8y9j-5cpu-yk",
                                                "uniqueKey": "ce367e8d42ec1b746afdd73c2a07e7b3_cwh9e",
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "selected": false
                                            },
                                            {
                                                "id": "f98912a38a7295b2af4a21d9c8df2f89",
                                                "name": "bookings",
                                                "alias": "bookings",
                                                "dataSource": {
                                                    "id": "1",
                                                    "type": "sql.jdbc.groovy.managed",
                                                    "baseType": "global.jdbc",
                                                    "catSchemaPredicted": false,
                                                    "sync": false,
                                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                    "schema": "public",
                                                    "dir": "Gagan",
                                                    "connId": "cwh9e",
                                                    "dbId": "cwh9e",
                                                    "classifier": "db.workflow",
                                                    "datasourceName": "Groovy 1",
                                                    "dsKeyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly",
                                                    "driverType": "DynamicSwitch",
                                                    "database": "public"
                                                },
                                                "category": "table",
                                                "connId": "cwh9e",
                                                "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly/imqv-hspt-xy22-nf9c-99",
                                                "uniqueKey": "f98912a38a7295b2af4a21d9c8df2f89_cwh9e",
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "selected": false
                                            },
                                            {
                                                "id": "93620a4e4bdf955d0e9b132ee0ec9ae3",
                                                "name": "company",
                                                "alias": "company",
                                                "dataSource": {
                                                    "id": "1",
                                                    "type": "sql.jdbc.groovy.managed",
                                                    "baseType": "global.jdbc",
                                                    "catSchemaPredicted": false,
                                                    "sync": false,
                                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                    "schema": "public",
                                                    "dir": "Gagan",
                                                    "connId": "cwh9e",
                                                    "dbId": "cwh9e",
                                                    "classifier": "db.workflow",
                                                    "datasourceName": "Groovy 1",
                                                    "dsKeyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly",
                                                    "driverType": "DynamicSwitch",
                                                    "database": "public"
                                                },
                                                "category": "table",
                                                "connId": "cwh9e",
                                                "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly/1dpu-308k-zg1i-2zoc-q6",
                                                "uniqueKey": "93620a4e4bdf955d0e9b132ee0ec9ae3_cwh9e",
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "selected": false
                                            },
                                            {
                                                "id": "b7b0680aec5189c9db7dd9ed1414e709",
                                                "name": "users",
                                                "alias": "users",
                                                "dataSource": {
                                                    "id": "1",
                                                    "type": "sql.jdbc.groovy.managed",
                                                    "baseType": "global.jdbc",
                                                    "catSchemaPredicted": false,
                                                    "sync": false,
                                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                    "schema": "public",
                                                    "dir": "Gagan",
                                                    "connId": "cwh9e",
                                                    "dbId": "cwh9e",
                                                    "classifier": "db.workflow",
                                                    "datasourceName": "Groovy 1",
                                                    "dsKeyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly",
                                                    "driverType": "DynamicSwitch",
                                                    "database": "public"
                                                },
                                                "category": "table",
                                                "connId": "cwh9e",
                                                "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly/q34s-f9d4-vzgl-qs05-bd",
                                                "uniqueKey": "b7b0680aec5189c9db7dd9ed1414e709_cwh9e",
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "selected": false
                                            }
                                        ],
                                        "catalog": "booking1662345944710kenxhxumioueuzfj",
                                        "datasourceName": "Groovy 1",
                                        "fetched": true
                                    },
                                    {
                                        "name": "information_schema",
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
                                        "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/ltfl-ca1p-g5rj-5qj9-cv",
                                        "key": "ltfl-ca1p-g5rj-5qj9-cv",
                                        "uuid": "ltfl-ca1p-g5rj-5qj9-cv",
                                        "category": "schema",
                                        "children": [],
                                        "catalog": "booking1662345944710kenxhxumioueuzfj",
                                        "datasourceName": "Groovy 1"
                                    }
                                ],
                                "children": [
                                    {
                                        "name": "pg_catalog",
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
                                        "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a8if-agy7-1gmt-uph7-wj",
                                        "key": "a8if-agy7-1gmt-uph7-wj",
                                        "uuid": "a8if-agy7-1gmt-uph7-wj",
                                        "category": "schema",
                                        "children": [],
                                        "catalog": "booking1662345944710kenxhxumioueuzfj",
                                        "datasourceName": "Groovy 1"
                                    },
                                    {
                                        "name": "public",
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
                                        "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly",
                                        "key": "a9lf-3253-v48u-c7ec-ly",
                                        "uuid": "a9lf-3253-v48u-c7ec-ly",
                                        "category": "schema",
                                        "children": [
                                            {
                                                "id": "ce367e8d42ec1b746afdd73c2a07e7b3",
                                                "name": "appartments",
                                                "alias": "appartments",
                                                "dataSource": {
                                                    "id": "1",
                                                    "type": "sql.jdbc.groovy.managed",
                                                    "baseType": "global.jdbc",
                                                    "catSchemaPredicted": false,
                                                    "sync": false,
                                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                    "schema": "public",
                                                    "dir": "Gagan",
                                                    "connId": "cwh9e",
                                                    "dbId": "cwh9e",
                                                    "classifier": "db.workflow",
                                                    "datasourceName": "Groovy 1",
                                                    "dsKeyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly",
                                                    "driverType": "DynamicSwitch",
                                                    "database": "public"
                                                },
                                                "category": "table",
                                                "connId": "cwh9e",
                                                "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly/3t73-d2jj-8y9j-5cpu-yk",
                                                "uniqueKey": "ce367e8d42ec1b746afdd73c2a07e7b3_cwh9e",
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "selected": true
                                            },
                                            {
                                                "id": "f98912a38a7295b2af4a21d9c8df2f89",
                                                "name": "bookings",
                                                "alias": "bookings",
                                                "dataSource": {
                                                    "id": "1",
                                                    "type": "sql.jdbc.groovy.managed",
                                                    "baseType": "global.jdbc",
                                                    "catSchemaPredicted": false,
                                                    "sync": false,
                                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                    "schema": "public",
                                                    "dir": "Gagan",
                                                    "connId": "cwh9e",
                                                    "dbId": "cwh9e",
                                                    "classifier": "db.workflow",
                                                    "datasourceName": "Groovy 1",
                                                    "dsKeyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly",
                                                    "driverType": "DynamicSwitch",
                                                    "database": "public"
                                                },
                                                "category": "table",
                                                "connId": "cwh9e",
                                                "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly/imqv-hspt-xy22-nf9c-99",
                                                "uniqueKey": "f98912a38a7295b2af4a21d9c8df2f89_cwh9e",
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "selected": false
                                            },
                                            {
                                                "id": "93620a4e4bdf955d0e9b132ee0ec9ae3",
                                                "name": "company",
                                                "alias": "company",
                                                "dataSource": {
                                                    "id": "1",
                                                    "type": "sql.jdbc.groovy.managed",
                                                    "baseType": "global.jdbc",
                                                    "catSchemaPredicted": false,
                                                    "sync": false,
                                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                    "schema": "public",
                                                    "dir": "Gagan",
                                                    "connId": "cwh9e",
                                                    "dbId": "cwh9e",
                                                    "classifier": "db.workflow",
                                                    "datasourceName": "Groovy 1",
                                                    "dsKeyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly",
                                                    "driverType": "DynamicSwitch",
                                                    "database": "public"
                                                },
                                                "category": "table",
                                                "connId": "cwh9e",
                                                "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly/1dpu-308k-zg1i-2zoc-q6",
                                                "uniqueKey": "93620a4e4bdf955d0e9b132ee0ec9ae3_cwh9e",
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "selected": false
                                            },
                                            {
                                                "id": "b7b0680aec5189c9db7dd9ed1414e709",
                                                "name": "users",
                                                "alias": "users",
                                                "dataSource": {
                                                    "id": "1",
                                                    "type": "sql.jdbc.groovy.managed",
                                                    "baseType": "global.jdbc",
                                                    "catSchemaPredicted": false,
                                                    "sync": false,
                                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                    "schema": "public",
                                                    "dir": "Gagan",
                                                    "connId": "cwh9e",
                                                    "dbId": "cwh9e",
                                                    "classifier": "db.workflow",
                                                    "datasourceName": "Groovy 1",
                                                    "dsKeyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly",
                                                    "driverType": "DynamicSwitch",
                                                    "database": "public"
                                                },
                                                "category": "table",
                                                "connId": "cwh9e",
                                                "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly/q34s-f9d4-vzgl-qs05-bd",
                                                "uniqueKey": "b7b0680aec5189c9db7dd9ed1414e709_cwh9e",
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "selected": false
                                            }
                                        ],
                                        "catalog": "booking1662345944710kenxhxumioueuzfj",
                                        "datasourceName": "Groovy 1",
                                        "fetched": true
                                    },
                                    {
                                        "name": "information_schema",
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
                                        "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/ltfl-ca1p-g5rj-5qj9-cv",
                                        "key": "ltfl-ca1p-g5rj-5qj9-cv",
                                        "uuid": "ltfl-ca1p-g5rj-5qj9-cv",
                                        "category": "schema",
                                        "children": [],
                                        "catalog": "booking1662345944710kenxhxumioueuzfj",
                                        "datasourceName": "Groovy 1"
                                    }
                                ],
                                "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb",
                                "key": "v918-op1d-ynob-qv98-mb",
                                "uuid": "v918-op1d-ynob-qv98-mb",
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
                                "category": "catalog",
                                "fetched": true,
                                "datasourceName": "Groovy 1"
                            }
                        ],
                        "driverType": "DynamicSwitch",
                        "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp",
                        "key": "2r2s-51jx-3r6s-v6q4-lp",
                        "uuid": "2r2s-51jx-3r6s-v6q4-lp"
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
                        "children": [
                            {
                                "name": "SQLJ",
                                "children": [],
                                "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/vevp-5vn2-q5gi-xdrm-f0",
                                "key": "vevp-5vn2-q5gi-xdrm-f0",
                                "uuid": "vevp-5vn2-q5gi-xdrm-f0",
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
                                "category": "schema",
                                "datasourceName": "6148b"
                            },
                            {
                                "name": "SYSFUN",
                                "children": [],
                                "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/ukk4-kjy5-fh09-9r5r-eh",
                                "key": "ukk4-kjy5-fh09-9r5r-eh",
                                "uuid": "ukk4-kjy5-fh09-9r5r-eh",
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
                                "category": "schema",
                                "datasourceName": "6148b"
                            },
                            {
                                "name": "SYSCAT",
                                "children": [],
                                "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/jo1g-jty4-kplg-cc08-u7",
                                "key": "jo1g-jty4-kplg-cc08-u7",
                                "uuid": "jo1g-jty4-kplg-cc08-u7",
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
                                "category": "schema",
                                "datasourceName": "6148b"
                            },
                            {
                                "name": "HIUSER",
                                "children": [
                                    {
                                        "id": "4ac5d9f68b58bd7c0d179146e46795be",
                                        "name": "dimdate",
                                        "alias": "dimdate",
                                        "dataSource": {
                                            "id": "801",
                                            "type": "sql.jdbc.groovy.managed",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "dir": "03_03",
                                            "connId": "rlddk",
                                            "dbId": "rlddk",
                                            "classifier": "db.workflow",
                                            "datasourceName": "6148b",
                                            "dsKeyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/ynzp-3yf1-1k2u-mrdk-a9",
                                            "driverType": "DynamicSwitch",
                                            "database": "HIUSER"
                                        },
                                        "category": "table",
                                        "connId": "rlddk",
                                        "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/ynzp-3yf1-1k2u-mrdk-a9/1r9c-tuui-3u2a-ms4r-v3",
                                        "uniqueKey": "4ac5d9f68b58bd7c0d179146e46795be_rlddk",
                                        "schema": "HIUSER",
                                        "selected": false
                                    },
                                    {
                                        "id": "4e1fd245f4d13b77be423a43f01d80b2",
                                        "name": "employee_details",
                                        "alias": "employee_details",
                                        "dataSource": {
                                            "id": "801",
                                            "type": "sql.jdbc.groovy.managed",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "dir": "03_03",
                                            "connId": "rlddk",
                                            "dbId": "rlddk",
                                            "classifier": "db.workflow",
                                            "datasourceName": "6148b",
                                            "dsKeyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/ynzp-3yf1-1k2u-mrdk-a9",
                                            "driverType": "DynamicSwitch",
                                            "database": "HIUSER"
                                        },
                                        "category": "table",
                                        "connId": "rlddk",
                                        "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/ynzp-3yf1-1k2u-mrdk-a9/qwcu-bugp-f6cj-7t27-u5",
                                        "uniqueKey": "4e1fd245f4d13b77be423a43f01d80b2_rlddk",
                                        "schema": "HIUSER",
                                        "selected": false
                                    },
                                    {
                                        "id": "be534112989b616b194bc59c2fb25a42",
                                        "name": "geo_cordinates",
                                        "alias": "geo_cordinates",
                                        "dataSource": {
                                            "id": "801",
                                            "type": "sql.jdbc.groovy.managed",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "dir": "03_03",
                                            "connId": "rlddk",
                                            "dbId": "rlddk",
                                            "classifier": "db.workflow",
                                            "datasourceName": "6148b",
                                            "dsKeyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/ynzp-3yf1-1k2u-mrdk-a9",
                                            "driverType": "DynamicSwitch",
                                            "database": "HIUSER"
                                        },
                                        "category": "table",
                                        "connId": "rlddk",
                                        "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/ynzp-3yf1-1k2u-mrdk-a9/nnty-phe1-j28p-wxjq-u8",
                                        "uniqueKey": "be534112989b616b194bc59c2fb25a42_rlddk",
                                        "schema": "HIUSER",
                                        "selected": false
                                    },
                                    {
                                        "id": "9645c648a1c0dbeec1287aaf1e996db3",
                                        "name": "meeting_details",
                                        "alias": "meeting_details",
                                        "dataSource": {
                                            "id": "801",
                                            "type": "sql.jdbc.groovy.managed",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "dir": "03_03",
                                            "connId": "rlddk",
                                            "dbId": "rlddk",
                                            "classifier": "db.workflow",
                                            "datasourceName": "6148b",
                                            "dsKeyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/ynzp-3yf1-1k2u-mrdk-a9",
                                            "driverType": "DynamicSwitch",
                                            "database": "HIUSER"
                                        },
                                        "category": "table",
                                        "connId": "rlddk",
                                        "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/ynzp-3yf1-1k2u-mrdk-a9/1c85-ljnd-pk6b-il7n-md",
                                        "uniqueKey": "9645c648a1c0dbeec1287aaf1e996db3_rlddk",
                                        "schema": "HIUSER",
                                        "selected": false
                                    },
                                    {
                                        "id": "8a28627d07d04ef096d9935f12e0c7e9",
                                        "name": "travel_details",
                                        "alias": "travel_details",
                                        "dataSource": {
                                            "id": "801",
                                            "type": "sql.jdbc.groovy.managed",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "dir": "03_03",
                                            "connId": "rlddk",
                                            "dbId": "rlddk",
                                            "classifier": "db.workflow",
                                            "datasourceName": "6148b",
                                            "dsKeyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/ynzp-3yf1-1k2u-mrdk-a9",
                                            "driverType": "DynamicSwitch",
                                            "database": "HIUSER"
                                        },
                                        "category": "table",
                                        "connId": "rlddk",
                                        "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/ynzp-3yf1-1k2u-mrdk-a9/68lp-2z6t-2emi-fhhr-e4",
                                        "uniqueKey": "8a28627d07d04ef096d9935f12e0c7e9_rlddk",
                                        "schema": "HIUSER",
                                        "selected": false
                                    }
                                ],
                                "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/ynzp-3yf1-1k2u-mrdk-a9",
                                "key": "ynzp-3yf1-1k2u-mrdk-a9",
                                "uuid": "ynzp-3yf1-1k2u-mrdk-a9",
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
                                "category": "schema",
                                "datasourceName": "6148b",
                                "fetched": true
                            },
                            {
                                "name": "SYSCS_DIAG",
                                "children": [],
                                "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/3wbt-h81y-gzls-3tnf-4r",
                                "key": "3wbt-h81y-gzls-3tnf-4r",
                                "uuid": "3wbt-h81y-gzls-3tnf-4r",
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
                                "category": "schema",
                                "datasourceName": "6148b"
                            },
                            {
                                "name": "SYSCS_UTIL",
                                "children": [],
                                "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/um2u-ms9c-0gq5-ynqk-oz",
                                "key": "um2u-ms9c-0gq5-ynqk-oz",
                                "uuid": "um2u-ms9c-0gq5-ynqk-oz",
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
                                "category": "schema",
                                "datasourceName": "6148b"
                            },
                            {
                                "name": "SYSIBM",
                                "children": [],
                                "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/cx8c-3sc1-91j0-cgix-iq",
                                "key": "cx8c-3sc1-91j0-cgix-iq",
                                "uuid": "cx8c-3sc1-91j0-cgix-iq",
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
                                "category": "schema",
                                "datasourceName": "6148b"
                            },
                            {
                                "name": "APP",
                                "children": [],
                                "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/gptd-bbvl-8d98-6954-to",
                                "key": "gptd-bbvl-8d98-6954-to",
                                "uuid": "gptd-bbvl-8d98-6954-to",
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
                                "category": "schema",
                                "datasourceName": "6148b"
                            },
                            {
                                "name": "NULLID",
                                "children": [],
                                "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/4cwa-k7wz-4ngo-npr1-5t",
                                "key": "4cwa-k7wz-4ngo-npr1-5t",
                                "uuid": "4cwa-k7wz-4ngo-npr1-5t",
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
                                "category": "schema",
                                "datasourceName": "6148b"
                            },
                            {
                                "name": "SYSPROC",
                                "children": [],
                                "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/alyy-cmri-qvfe-4n3a-t1",
                                "key": "alyy-cmri-qvfe-4n3a-t1",
                                "uuid": "alyy-cmri-qvfe-4n3a-t1",
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
                                "category": "schema",
                                "datasourceName": "6148b"
                            },
                            {
                                "name": "SYS",
                                "children": [],
                                "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/t0ud-b6uq-bdkd-v7wg-g0",
                                "key": "t0ud-b6uq-bdkd-v7wg-g0",
                                "uuid": "t0ud-b6uq-bdkd-v7wg-g0",
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
                                "category": "schema",
                                "datasourceName": "6148b"
                            },
                            {
                                "name": "SYSSTAT",
                                "children": [],
                                "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/0g08-xedg-eg2c-jjwv-wv",
                                "key": "0g08-xedg-eg2c-jjwv-wv",
                                "uuid": "0g08-xedg-eg2c-jjwv-wv",
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
                                "category": "schema",
                                "datasourceName": "6148b"
                            }
                        ],
                        "driverType": "DynamicSwitch",
                        "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs",
                        "key": "136t-elkq-h4fm-1a8l-hs",
                        "uuid": "136t-elkq-h4fm-1a8l-hs",
                        "fetched": true
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
                        "keyPath": "7o38-l87u-gnsz-z490-er/vyjx-mx1n-ch6z-uz17-md",
                        "key": "vyjx-mx1n-ch6z-uz17-md",
                        "uuid": "vyjx-mx1n-ch6z-uz17-md"
                    }
                ],
                "key": "7o38-l87u-gnsz-z490-er",
                "uuid": "7o38-l87u-gnsz-z490-er",
                "keyPath": "7o38-l87u-gnsz-z490-er",
                "category": "dsGroup",
                "imgUrl": "images/data_sources/defaut_datasource.png"
            },
            {
                "name": "Elasticsearch",
                "children": [
                    {
                        "data": {
                            "id": "2701",
                            "type": "dynamicDataSource"
                        },
                        "dataSourceProvider": "tomcat",
                        "type": "dynamicDataSource",
                        "permissionLevel": 5,
                        "driver": "org.elasticsearch.xpack.sql.jdbc.EsDriver",
                        "name": "test",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "Elasticsearch",
                        "keyPath": "0ihj-63wq-3zlh-64yf-d4/bkuj-rilk-jjh4-ix4j-r7",
                        "key": "bkuj-rilk-jjh4-ix4j-r7",
                        "uuid": "bkuj-rilk-jjh4-ix4j-r7"
                    }
                ],
                "key": "0ihj-63wq-3zlh-64yf-d4",
                "uuid": "0ihj-63wq-3zlh-64yf-d4",
                "keyPath": "0ihj-63wq-3zlh-64yf-d4",
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
                        "keyPath": "3wl1-mpml-afzg-8yxz-dp/6xnh-qm1o-h6f4-1qft-cr",
                        "key": "6xnh-qm1o-h6f4-1qft-cr",
                        "uuid": "6xnh-qm1o-h6f4-1qft-cr"
                    }
                ],
                "key": "3wl1-mpml-afzg-8yxz-dp",
                "uuid": "3wl1-mpml-afzg-8yxz-dp",
                "keyPath": "3wl1-mpml-afzg-8yxz-dp",
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
                        "keyPath": "zcuf-vfxo-yvbz-e8t2-ri/4cvz-d4c6-2wk3-3jtz-gw",
                        "key": "4cvz-d4c6-2wk3-3jtz-gw",
                        "uuid": "4cvz-d4c6-2wk3-3jtz-gw"
                    }
                ],
                "key": "zcuf-vfxo-yvbz-e8t2-ri",
                "uuid": "zcuf-vfxo-yvbz-e8t2-ri",
                "keyPath": "zcuf-vfxo-yvbz-e8t2-ri",
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
                        "keyPath": "7k5k-im7o-w0fe-f966-7b/ja3p-z8zv-odzy-o9va-ue",
                        "key": "ja3p-z8zv-odzy-o9va-ue",
                        "uuid": "ja3p-z8zv-odzy-o9va-ue"
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
                        "keyPath": "7k5k-im7o-w0fe-f966-7b/uqk6-rhx7-51mw-1ski-42",
                        "key": "uqk6-rhx7-51mw-1ski-42",
                        "uuid": "uqk6-rhx7-51mw-1ski-42"
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
                        "children": [],
                        "driverType": "Postgresql",
                        "keyPath": "7k5k-im7o-w0fe-f966-7b/tn87-4ifs-vctj-xlev-dj",
                        "key": "tn87-4ifs-vctj-xlev-dj",
                        "uuid": "tn87-4ifs-vctj-xlev-dj"
                    }
                ],
                "key": "7k5k-im7o-w0fe-f966-7b",
                "uuid": "7k5k-im7o-w0fe-f966-7b",
                "keyPath": "7k5k-im7o-w0fe-f966-7b",
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
                        "keyPath": "o915-p7gt-1idb-3uwe-pg/w4xo-8w5g-elw8-k6ni-ne",
                        "key": "w4xo-8w5g-elw8-k6ni-ne",
                        "uuid": "w4xo-8w5g-elw8-k6ni-ne"
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
                        "keyPath": "o915-p7gt-1idb-3uwe-pg/6jjn-dgmv-rw56-3g30-7m",
                        "key": "6jjn-dgmv-rw56-3g30-7m",
                        "uuid": "6jjn-dgmv-rw56-3g30-7m"
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
                        "keyPath": "o915-p7gt-1idb-3uwe-pg/49no-j9b9-l5ms-jof2-bn",
                        "key": "49no-j9b9-l5ms-jof2-bn",
                        "uuid": "49no-j9b9-l5ms-jof2-bn"
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
                        "keyPath": "o915-p7gt-1idb-3uwe-pg/vag5-p2lf-2diu-a18d-lc",
                        "key": "vag5-p2lf-2diu-a18d-lc",
                        "uuid": "vag5-p2lf-2diu-a18d-lc"
                    }
                ],
                "key": "o915-p7gt-1idb-3uwe-pg",
                "uuid": "o915-p7gt-1idb-3uwe-pg",
                "keyPath": "o915-p7gt-1idb-3uwe-pg",
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
                        "keyPath": "h94w-8hf7-pvr3-ktkq-kn/ixak-nu3d-y68n-pmbe-h8",
                        "key": "ixak-nu3d-y68n-pmbe-h8",
                        "uuid": "ixak-nu3d-y68n-pmbe-h8"
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
                        "keyPath": "h94w-8hf7-pvr3-ktkq-kn/c8sa-p05p-8kan-r8un-sc",
                        "key": "c8sa-p05p-8kan-r8un-sc",
                        "uuid": "c8sa-p05p-8kan-r8un-sc"
                    }
                ],
                "key": "h94w-8hf7-pvr3-ktkq-kn",
                "uuid": "h94w-8hf7-pvr3-ktkq-kn",
                "keyPath": "h94w-8hf7-pvr3-ktkq-kn",
                "category": "dsGroup",
                "imgUrl": "images/data_sources/defaut_datasource.png"
            }
        ],
        record: {
            "id": "ce367e8d42ec1b746afdd73c2a07e7b3",
            "name": "appartments",
            "alias": "appartments",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc.groovy.managed",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "booking1662345944710kenxhxumioueuzfj",
                "schema": "public",
                "dir": "Gagan",
                "connId": "cwh9e",
                "dbId": "cwh9e",
                "classifier": "db.workflow",
                "datasourceName": "Groovy 1",
                "dsKeyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly",
                "driverType": "DynamicSwitch",
                "database": "public"
            },
            "category": "table",
            "connId": "cwh9e",
            "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly/3t73-d2jj-8y9j-5cpu-yk",
            "uniqueKey": "ce367e8d42ec1b746afdd73c2a07e7b3_cwh9e",
            "catalog": "booking1662345944710kenxhxumioueuzfj",
            "schema": "public",
            "selected": true,
            "keyName": "appartments",
            "columns": {
                "id": {
                    "alias": "id",
                    "fullyQualifiedColumn": "appartments.id",
                    "id": "7f58f0fc-c1c1-46d3-8845-214b61e8e51d",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "uniqueKey": "7f58f0fc-c1c1-46d3-8845-214b61e8e51d_cwh9e",
                    "name": "id",
                    "tableKey": "appartments",
                    "connId": "cwh9e",
                    "columnKey": "id",
                    "tableId": "ce367e8d42ec1b746afdd73c2a07e7b3"
                },
                "name": {
                    "alias": "name",
                    "fullyQualifiedColumn": "appartments.name",
                    "id": "c81dc71b-9fa6-4b0b-ab32-29fda41e141d",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "uniqueKey": "c81dc71b-9fa6-4b0b-ab32-29fda41e141d_cwh9e",
                    "name": "name",
                    "tableKey": "appartments",
                    "connId": "cwh9e",
                    "columnKey": "name",
                    "tableId": "ce367e8d42ec1b746afdd73c2a07e7b3"
                },
                "image": {
                    "alias": "image",
                    "fullyQualifiedColumn": "appartments.image",
                    "id": "2fdfd07b-4702-49bc-b9ab-7b90cf2dbe93",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "uniqueKey": "2fdfd07b-4702-49bc-b9ab-7b90cf2dbe93_cwh9e",
                    "name": "image",
                    "tableKey": "appartments",
                    "connId": "cwh9e",
                    "columnKey": "image",
                    "tableId": "ce367e8d42ec1b746afdd73c2a07e7b3"
                },
                "country": {
                    "alias": "country",
                    "fullyQualifiedColumn": "appartments.country",
                    "id": "eafde570-26d3-4678-99b0-f31d0554fd5e",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "uniqueKey": "eafde570-26d3-4678-99b0-f31d0554fd5e_cwh9e",
                    "name": "country",
                    "tableKey": "appartments",
                    "connId": "cwh9e",
                    "columnKey": "country",
                    "tableId": "ce367e8d42ec1b746afdd73c2a07e7b3"
                },
                "city": {
                    "alias": "city",
                    "fullyQualifiedColumn": "appartments.city",
                    "id": "b18a94aa-488d-43a5-96a4-5bc924ed82f0",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "uniqueKey": "b18a94aa-488d-43a5-96a4-5bc924ed82f0_cwh9e",
                    "name": "city",
                    "tableKey": "appartments",
                    "connId": "cwh9e",
                    "columnKey": "city",
                    "tableId": "ce367e8d42ec1b746afdd73c2a07e7b3"
                },
                "zip_code": {
                    "alias": "zip_code",
                    "fullyQualifiedColumn": "appartments.zip_code",
                    "id": "67763d08-0919-44e0-b0fa-5a89fa600159",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "uniqueKey": "67763d08-0919-44e0-b0fa-5a89fa600159_cwh9e",
                    "name": "zip_code",
                    "tableKey": "appartments",
                    "connId": "cwh9e",
                    "columnKey": "zip_code",
                    "tableId": "ce367e8d42ec1b746afdd73c2a07e7b3"
                },
                "address": {
                    "alias": "address",
                    "fullyQualifiedColumn": "appartments.address",
                    "id": "b6303298-c47d-4213-b2b5-be6219dc897f",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "uniqueKey": "b6303298-c47d-4213-b2b5-be6219dc897f_cwh9e",
                    "name": "address",
                    "tableKey": "appartments",
                    "connId": "cwh9e",
                    "columnKey": "address",
                    "tableId": "ce367e8d42ec1b746afdd73c2a07e7b3"
                },
                "address2": {
                    "alias": "address2",
                    "fullyQualifiedColumn": "appartments.address2",
                    "id": "0b6f879b-a3b3-48d7-b94c-652d6dbe625a",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "uniqueKey": "0b6f879b-a3b3-48d7-b94c-652d6dbe625a_cwh9e",
                    "name": "address2",
                    "tableKey": "appartments",
                    "connId": "cwh9e",
                    "columnKey": "address2",
                    "tableId": "ce367e8d42ec1b746afdd73c2a07e7b3"
                },
                "latitude": {
                    "alias": "latitude",
                    "fullyQualifiedColumn": "appartments.latitude",
                    "id": "ef5563c8-8626-40b4-b435-678cd13ec922",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    },
                    "category": "column",
                    "uniqueKey": "ef5563c8-8626-40b4-b435-678cd13ec922_cwh9e",
                    "name": "latitude",
                    "tableKey": "appartments",
                    "connId": "cwh9e",
                    "columnKey": "latitude",
                    "tableId": "ce367e8d42ec1b746afdd73c2a07e7b3"
                },
                "longitude": {
                    "alias": "longitude",
                    "fullyQualifiedColumn": "appartments.longitude",
                    "id": "8006382d-3658-4dc6-a390-6a3afea62af6",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    },
                    "category": "column",
                    "uniqueKey": "8006382d-3658-4dc6-a390-6a3afea62af6_cwh9e",
                    "name": "longitude",
                    "tableKey": "appartments",
                    "connId": "cwh9e",
                    "columnKey": "longitude",
                    "tableId": "ce367e8d42ec1b746afdd73c2a07e7b3"
                },
                "direction": {
                    "alias": "direction",
                    "fullyQualifiedColumn": "appartments.direction",
                    "id": "62487bc8-a9ae-44fc-b0f6-2daa18a3e518",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "uniqueKey": "62487bc8-a9ae-44fc-b0f6-2daa18a3e518_cwh9e",
                    "name": "direction",
                    "tableKey": "appartments",
                    "connId": "cwh9e",
                    "columnKey": "direction",
                    "tableId": "ce367e8d42ec1b746afdd73c2a07e7b3"
                },
                "booked": {
                    "alias": "booked",
                    "fullyQualifiedColumn": "appartments.booked",
                    "id": "1f1b2a6d-b768-44c2-a84e-ee9db37429a8",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "uniqueKey": "1f1b2a6d-b768-44c2-a84e-ee9db37429a8_cwh9e",
                    "name": "booked",
                    "tableKey": "appartments",
                    "connId": "cwh9e",
                    "columnKey": "booked",
                    "tableId": "ce367e8d42ec1b746afdd73c2a07e7b3"
                }
            },
            "columnsFetched": true
        },
        task: 'remove',
        options: {
            value : false,
            type : 'selectTables'
        }
    },
    result: [
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
                    "keyPath": "0qej-gll1-lmtc-sdze-h8/k5ps-tqtt-hvih-2vk2-b2",
                    "key": "k5ps-tqtt-hvih-2vk2-b2",
                    "uuid": "k5ps-tqtt-hvih-2vk2-b2"
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
                    "keyPath": "0qej-gll1-lmtc-sdze-h8/ad98-pykh-0vpa-ewgx-2p",
                    "key": "ad98-pykh-0vpa-ewgx-2p",
                    "uuid": "ad98-pykh-0vpa-ewgx-2p"
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
                    "keyPath": "0qej-gll1-lmtc-sdze-h8/h3o2-oam8-o4sh-lhaz-90",
                    "key": "h3o2-oam8-o4sh-lhaz-90",
                    "uuid": "h3o2-oam8-o4sh-lhaz-90"
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
                    "keyPath": "0qej-gll1-lmtc-sdze-h8/0nte-pu35-fgi3-pl07-n2",
                    "key": "0nte-pu35-fgi3-pl07-n2",
                    "uuid": "0nte-pu35-fgi3-pl07-n2"
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
                    "keyPath": "0qej-gll1-lmtc-sdze-h8/2ecc-6onl-uxgi-kcpf-n7",
                    "key": "2ecc-6onl-uxgi-kcpf-n7",
                    "uuid": "2ecc-6onl-uxgi-kcpf-n7"
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
                    "keyPath": "0qej-gll1-lmtc-sdze-h8/qr85-n3fo-cvgz-871a-ej",
                    "key": "qr85-n3fo-cvgz-871a-ej",
                    "uuid": "qr85-n3fo-cvgz-871a-ej"
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
                    "keyPath": "0qej-gll1-lmtc-sdze-h8/15bq-5fx9-m8vi-y9cp-8p",
                    "key": "15bq-5fx9-m8vi-y9cp-8p",
                    "uuid": "15bq-5fx9-m8vi-y9cp-8p"
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
                    "keyPath": "0qej-gll1-lmtc-sdze-h8/st5v-su3u-r8xk-1pqq-rk",
                    "key": "st5v-su3u-r8xk-1pqq-rk",
                    "uuid": "st5v-su3u-r8xk-1pqq-rk"
                }
            ],
            "key": "0qej-gll1-lmtc-sdze-h8",
            "uuid": "0qej-gll1-lmtc-sdze-h8",
            "keyPath": "0qej-gll1-lmtc-sdze-h8",
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
                    "children": [
                        {
                            "name": "booking1662345944710kenxhxumioueuzfj",
                            "schemas": [
                                {
                                    "name": "pg_catalog",
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
                                    "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a8if-agy7-1gmt-uph7-wj",
                                    "key": "a8if-agy7-1gmt-uph7-wj",
                                    "uuid": "a8if-agy7-1gmt-uph7-wj",
                                    "category": "schema",
                                    "children": [],
                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                    "datasourceName": "Groovy 1"
                                },
                                {
                                    "name": "public",
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
                                    "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly",
                                    "key": "a9lf-3253-v48u-c7ec-ly",
                                    "uuid": "a9lf-3253-v48u-c7ec-ly",
                                    "category": "schema",
                                    "children": [
                                        {
                                            "id": "ce367e8d42ec1b746afdd73c2a07e7b3",
                                            "name": "appartments",
                                            "alias": "appartments",
                                            "dataSource": {
                                                "id": "1",
                                                "type": "sql.jdbc.groovy.managed",
                                                "baseType": "global.jdbc",
                                                "catSchemaPredicted": false,
                                                "sync": false,
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "dir": "Gagan",
                                                "connId": "cwh9e",
                                                "dbId": "cwh9e",
                                                "classifier": "db.workflow",
                                                "datasourceName": "Groovy 1",
                                                "dsKeyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly",
                                                "driverType": "DynamicSwitch",
                                                "database": "public"
                                            },
                                            "category": "table",
                                            "connId": "cwh9e",
                                            "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly/3t73-d2jj-8y9j-5cpu-yk",
                                            "uniqueKey": "ce367e8d42ec1b746afdd73c2a07e7b3_cwh9e",
                                            "catalog": "booking1662345944710kenxhxumioueuzfj",
                                            "schema": "public",
                                            "selected": false
                                        },
                                        {
                                            "id": "f98912a38a7295b2af4a21d9c8df2f89",
                                            "name": "bookings",
                                            "alias": "bookings",
                                            "dataSource": {
                                                "id": "1",
                                                "type": "sql.jdbc.groovy.managed",
                                                "baseType": "global.jdbc",
                                                "catSchemaPredicted": false,
                                                "sync": false,
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "dir": "Gagan",
                                                "connId": "cwh9e",
                                                "dbId": "cwh9e",
                                                "classifier": "db.workflow",
                                                "datasourceName": "Groovy 1",
                                                "dsKeyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly",
                                                "driverType": "DynamicSwitch",
                                                "database": "public"
                                            },
                                            "category": "table",
                                            "connId": "cwh9e",
                                            "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly/imqv-hspt-xy22-nf9c-99",
                                            "uniqueKey": "f98912a38a7295b2af4a21d9c8df2f89_cwh9e",
                                            "catalog": "booking1662345944710kenxhxumioueuzfj",
                                            "schema": "public",
                                            "selected": false
                                        },
                                        {
                                            "id": "93620a4e4bdf955d0e9b132ee0ec9ae3",
                                            "name": "company",
                                            "alias": "company",
                                            "dataSource": {
                                                "id": "1",
                                                "type": "sql.jdbc.groovy.managed",
                                                "baseType": "global.jdbc",
                                                "catSchemaPredicted": false,
                                                "sync": false,
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "dir": "Gagan",
                                                "connId": "cwh9e",
                                                "dbId": "cwh9e",
                                                "classifier": "db.workflow",
                                                "datasourceName": "Groovy 1",
                                                "dsKeyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly",
                                                "driverType": "DynamicSwitch",
                                                "database": "public"
                                            },
                                            "category": "table",
                                            "connId": "cwh9e",
                                            "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly/1dpu-308k-zg1i-2zoc-q6",
                                            "uniqueKey": "93620a4e4bdf955d0e9b132ee0ec9ae3_cwh9e",
                                            "catalog": "booking1662345944710kenxhxumioueuzfj",
                                            "schema": "public",
                                            "selected": false
                                        },
                                        {
                                            "id": "b7b0680aec5189c9db7dd9ed1414e709",
                                            "name": "users",
                                            "alias": "users",
                                            "dataSource": {
                                                "id": "1",
                                                "type": "sql.jdbc.groovy.managed",
                                                "baseType": "global.jdbc",
                                                "catSchemaPredicted": false,
                                                "sync": false,
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "dir": "Gagan",
                                                "connId": "cwh9e",
                                                "dbId": "cwh9e",
                                                "classifier": "db.workflow",
                                                "datasourceName": "Groovy 1",
                                                "dsKeyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly",
                                                "driverType": "DynamicSwitch",
                                                "database": "public"
                                            },
                                            "category": "table",
                                            "connId": "cwh9e",
                                            "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly/q34s-f9d4-vzgl-qs05-bd",
                                            "uniqueKey": "b7b0680aec5189c9db7dd9ed1414e709_cwh9e",
                                            "catalog": "booking1662345944710kenxhxumioueuzfj",
                                            "schema": "public",
                                            "selected": false
                                        }
                                    ],
                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                    "datasourceName": "Groovy 1",
                                    "fetched": true
                                },
                                {
                                    "name": "information_schema",
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
                                    "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/ltfl-ca1p-g5rj-5qj9-cv",
                                    "key": "ltfl-ca1p-g5rj-5qj9-cv",
                                    "uuid": "ltfl-ca1p-g5rj-5qj9-cv",
                                    "category": "schema",
                                    "children": [],
                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                    "datasourceName": "Groovy 1"
                                }
                            ],
                            "children": [
                                {
                                    "name": "pg_catalog",
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
                                    "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a8if-agy7-1gmt-uph7-wj",
                                    "key": "a8if-agy7-1gmt-uph7-wj",
                                    "uuid": "a8if-agy7-1gmt-uph7-wj",
                                    "category": "schema",
                                    "children": [],
                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                    "datasourceName": "Groovy 1"
                                },
                                {
                                    "name": "public",
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
                                    "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly",
                                    "key": "a9lf-3253-v48u-c7ec-ly",
                                    "uuid": "a9lf-3253-v48u-c7ec-ly",
                                    "category": "schema",
                                    "children": [
                                        {
                                            "id": "ce367e8d42ec1b746afdd73c2a07e7b3",
                                            "name": "appartments",
                                            "alias": "appartments",
                                            "dataSource": {
                                                "id": "1",
                                                "type": "sql.jdbc.groovy.managed",
                                                "baseType": "global.jdbc",
                                                "catSchemaPredicted": false,
                                                "sync": false,
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "dir": "Gagan",
                                                "connId": "cwh9e",
                                                "dbId": "cwh9e",
                                                "classifier": "db.workflow",
                                                "datasourceName": "Groovy 1",
                                                "dsKeyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly",
                                                "driverType": "DynamicSwitch",
                                                "database": "public"
                                            },
                                            "category": "table",
                                            "connId": "cwh9e",
                                            "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly/3t73-d2jj-8y9j-5cpu-yk",
                                            "uniqueKey": "ce367e8d42ec1b746afdd73c2a07e7b3_cwh9e",
                                            "catalog": "booking1662345944710kenxhxumioueuzfj",
                                            "schema": "public",
                                            "selected": false
                                        },
                                        {
                                            "id": "f98912a38a7295b2af4a21d9c8df2f89",
                                            "name": "bookings",
                                            "alias": "bookings",
                                            "dataSource": {
                                                "id": "1",
                                                "type": "sql.jdbc.groovy.managed",
                                                "baseType": "global.jdbc",
                                                "catSchemaPredicted": false,
                                                "sync": false,
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "dir": "Gagan",
                                                "connId": "cwh9e",
                                                "dbId": "cwh9e",
                                                "classifier": "db.workflow",
                                                "datasourceName": "Groovy 1",
                                                "dsKeyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly",
                                                "driverType": "DynamicSwitch",
                                                "database": "public"
                                            },
                                            "category": "table",
                                            "connId": "cwh9e",
                                            "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly/imqv-hspt-xy22-nf9c-99",
                                            "uniqueKey": "f98912a38a7295b2af4a21d9c8df2f89_cwh9e",
                                            "catalog": "booking1662345944710kenxhxumioueuzfj",
                                            "schema": "public",
                                            "selected": false
                                        },
                                        {
                                            "id": "93620a4e4bdf955d0e9b132ee0ec9ae3",
                                            "name": "company",
                                            "alias": "company",
                                            "dataSource": {
                                                "id": "1",
                                                "type": "sql.jdbc.groovy.managed",
                                                "baseType": "global.jdbc",
                                                "catSchemaPredicted": false,
                                                "sync": false,
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "dir": "Gagan",
                                                "connId": "cwh9e",
                                                "dbId": "cwh9e",
                                                "classifier": "db.workflow",
                                                "datasourceName": "Groovy 1",
                                                "dsKeyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly",
                                                "driverType": "DynamicSwitch",
                                                "database": "public"
                                            },
                                            "category": "table",
                                            "connId": "cwh9e",
                                            "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly/1dpu-308k-zg1i-2zoc-q6",
                                            "uniqueKey": "93620a4e4bdf955d0e9b132ee0ec9ae3_cwh9e",
                                            "catalog": "booking1662345944710kenxhxumioueuzfj",
                                            "schema": "public",
                                            "selected": false
                                        },
                                        {
                                            "id": "b7b0680aec5189c9db7dd9ed1414e709",
                                            "name": "users",
                                            "alias": "users",
                                            "dataSource": {
                                                "id": "1",
                                                "type": "sql.jdbc.groovy.managed",
                                                "baseType": "global.jdbc",
                                                "catSchemaPredicted": false,
                                                "sync": false,
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "dir": "Gagan",
                                                "connId": "cwh9e",
                                                "dbId": "cwh9e",
                                                "classifier": "db.workflow",
                                                "datasourceName": "Groovy 1",
                                                "dsKeyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly",
                                                "driverType": "DynamicSwitch",
                                                "database": "public"
                                            },
                                            "category": "table",
                                            "connId": "cwh9e",
                                            "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/a9lf-3253-v48u-c7ec-ly/q34s-f9d4-vzgl-qs05-bd",
                                            "uniqueKey": "b7b0680aec5189c9db7dd9ed1414e709_cwh9e",
                                            "catalog": "booking1662345944710kenxhxumioueuzfj",
                                            "schema": "public",
                                            "selected": false
                                        }
                                    ],
                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                    "datasourceName": "Groovy 1",
                                    "fetched": true
                                },
                                {
                                    "name": "information_schema",
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
                                    "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb/ltfl-ca1p-g5rj-5qj9-cv",
                                    "key": "ltfl-ca1p-g5rj-5qj9-cv",
                                    "uuid": "ltfl-ca1p-g5rj-5qj9-cv",
                                    "category": "schema",
                                    "children": [],
                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                    "datasourceName": "Groovy 1"
                                }
                            ],
                            "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp/v918-op1d-ynob-qv98-mb",
                            "key": "v918-op1d-ynob-qv98-mb",
                            "uuid": "v918-op1d-ynob-qv98-mb",
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
                            "category": "catalog",
                            "fetched": true,
                            "datasourceName": "Groovy 1"
                        }
                    ],
                    "driverType": "DynamicSwitch",
                    "keyPath": "7o38-l87u-gnsz-z490-er/2r2s-51jx-3r6s-v6q4-lp",
                    "key": "2r2s-51jx-3r6s-v6q4-lp",
                    "uuid": "2r2s-51jx-3r6s-v6q4-lp"
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
                    "children": [
                        {
                            "name": "SQLJ",
                            "children": [],
                            "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/vevp-5vn2-q5gi-xdrm-f0",
                            "key": "vevp-5vn2-q5gi-xdrm-f0",
                            "uuid": "vevp-5vn2-q5gi-xdrm-f0",
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
                            "category": "schema",
                            "datasourceName": "6148b"
                        },
                        {
                            "name": "SYSFUN",
                            "children": [],
                            "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/ukk4-kjy5-fh09-9r5r-eh",
                            "key": "ukk4-kjy5-fh09-9r5r-eh",
                            "uuid": "ukk4-kjy5-fh09-9r5r-eh",
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
                            "category": "schema",
                            "datasourceName": "6148b"
                        },
                        {
                            "name": "SYSCAT",
                            "children": [],
                            "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/jo1g-jty4-kplg-cc08-u7",
                            "key": "jo1g-jty4-kplg-cc08-u7",
                            "uuid": "jo1g-jty4-kplg-cc08-u7",
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
                            "category": "schema",
                            "datasourceName": "6148b"
                        },
                        {
                            "name": "HIUSER",
                            "children": [
                                {
                                    "id": "4ac5d9f68b58bd7c0d179146e46795be",
                                    "name": "dimdate",
                                    "alias": "dimdate",
                                    "dataSource": {
                                        "id": "801",
                                        "type": "sql.jdbc.groovy.managed",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "03_03",
                                        "connId": "rlddk",
                                        "dbId": "rlddk",
                                        "classifier": "db.workflow",
                                        "datasourceName": "6148b",
                                        "dsKeyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/ynzp-3yf1-1k2u-mrdk-a9",
                                        "driverType": "DynamicSwitch",
                                        "database": "HIUSER"
                                    },
                                    "category": "table",
                                    "connId": "rlddk",
                                    "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/ynzp-3yf1-1k2u-mrdk-a9/1r9c-tuui-3u2a-ms4r-v3",
                                    "uniqueKey": "4ac5d9f68b58bd7c0d179146e46795be_rlddk",
                                    "schema": "HIUSER",
                                    "selected": false
                                },
                                {
                                    "id": "4e1fd245f4d13b77be423a43f01d80b2",
                                    "name": "employee_details",
                                    "alias": "employee_details",
                                    "dataSource": {
                                        "id": "801",
                                        "type": "sql.jdbc.groovy.managed",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "03_03",
                                        "connId": "rlddk",
                                        "dbId": "rlddk",
                                        "classifier": "db.workflow",
                                        "datasourceName": "6148b",
                                        "dsKeyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/ynzp-3yf1-1k2u-mrdk-a9",
                                        "driverType": "DynamicSwitch",
                                        "database": "HIUSER"
                                    },
                                    "category": "table",
                                    "connId": "rlddk",
                                    "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/ynzp-3yf1-1k2u-mrdk-a9/qwcu-bugp-f6cj-7t27-u5",
                                    "uniqueKey": "4e1fd245f4d13b77be423a43f01d80b2_rlddk",
                                    "schema": "HIUSER",
                                    "selected": false
                                },
                                {
                                    "id": "be534112989b616b194bc59c2fb25a42",
                                    "name": "geo_cordinates",
                                    "alias": "geo_cordinates",
                                    "dataSource": {
                                        "id": "801",
                                        "type": "sql.jdbc.groovy.managed",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "03_03",
                                        "connId": "rlddk",
                                        "dbId": "rlddk",
                                        "classifier": "db.workflow",
                                        "datasourceName": "6148b",
                                        "dsKeyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/ynzp-3yf1-1k2u-mrdk-a9",
                                        "driverType": "DynamicSwitch",
                                        "database": "HIUSER"
                                    },
                                    "category": "table",
                                    "connId": "rlddk",
                                    "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/ynzp-3yf1-1k2u-mrdk-a9/nnty-phe1-j28p-wxjq-u8",
                                    "uniqueKey": "be534112989b616b194bc59c2fb25a42_rlddk",
                                    "schema": "HIUSER",
                                    "selected": false
                                },
                                {
                                    "id": "9645c648a1c0dbeec1287aaf1e996db3",
                                    "name": "meeting_details",
                                    "alias": "meeting_details",
                                    "dataSource": {
                                        "id": "801",
                                        "type": "sql.jdbc.groovy.managed",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "03_03",
                                        "connId": "rlddk",
                                        "dbId": "rlddk",
                                        "classifier": "db.workflow",
                                        "datasourceName": "6148b",
                                        "dsKeyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/ynzp-3yf1-1k2u-mrdk-a9",
                                        "driverType": "DynamicSwitch",
                                        "database": "HIUSER"
                                    },
                                    "category": "table",
                                    "connId": "rlddk",
                                    "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/ynzp-3yf1-1k2u-mrdk-a9/1c85-ljnd-pk6b-il7n-md",
                                    "uniqueKey": "9645c648a1c0dbeec1287aaf1e996db3_rlddk",
                                    "schema": "HIUSER",
                                    "selected": false
                                },
                                {
                                    "id": "8a28627d07d04ef096d9935f12e0c7e9",
                                    "name": "travel_details",
                                    "alias": "travel_details",
                                    "dataSource": {
                                        "id": "801",
                                        "type": "sql.jdbc.groovy.managed",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "03_03",
                                        "connId": "rlddk",
                                        "dbId": "rlddk",
                                        "classifier": "db.workflow",
                                        "datasourceName": "6148b",
                                        "dsKeyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/ynzp-3yf1-1k2u-mrdk-a9",
                                        "driverType": "DynamicSwitch",
                                        "database": "HIUSER"
                                    },
                                    "category": "table",
                                    "connId": "rlddk",
                                    "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/ynzp-3yf1-1k2u-mrdk-a9/68lp-2z6t-2emi-fhhr-e4",
                                    "uniqueKey": "8a28627d07d04ef096d9935f12e0c7e9_rlddk",
                                    "schema": "HIUSER",
                                    "selected": false
                                }
                            ],
                            "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/ynzp-3yf1-1k2u-mrdk-a9",
                            "key": "ynzp-3yf1-1k2u-mrdk-a9",
                            "uuid": "ynzp-3yf1-1k2u-mrdk-a9",
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
                            "category": "schema",
                            "datasourceName": "6148b",
                            "fetched": true
                        },
                        {
                            "name": "SYSCS_DIAG",
                            "children": [],
                            "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/3wbt-h81y-gzls-3tnf-4r",
                            "key": "3wbt-h81y-gzls-3tnf-4r",
                            "uuid": "3wbt-h81y-gzls-3tnf-4r",
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
                            "category": "schema",
                            "datasourceName": "6148b"
                        },
                        {
                            "name": "SYSCS_UTIL",
                            "children": [],
                            "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/um2u-ms9c-0gq5-ynqk-oz",
                            "key": "um2u-ms9c-0gq5-ynqk-oz",
                            "uuid": "um2u-ms9c-0gq5-ynqk-oz",
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
                            "category": "schema",
                            "datasourceName": "6148b"
                        },
                        {
                            "name": "SYSIBM",
                            "children": [],
                            "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/cx8c-3sc1-91j0-cgix-iq",
                            "key": "cx8c-3sc1-91j0-cgix-iq",
                            "uuid": "cx8c-3sc1-91j0-cgix-iq",
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
                            "category": "schema",
                            "datasourceName": "6148b"
                        },
                        {
                            "name": "APP",
                            "children": [],
                            "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/gptd-bbvl-8d98-6954-to",
                            "key": "gptd-bbvl-8d98-6954-to",
                            "uuid": "gptd-bbvl-8d98-6954-to",
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
                            "category": "schema",
                            "datasourceName": "6148b"
                        },
                        {
                            "name": "NULLID",
                            "children": [],
                            "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/4cwa-k7wz-4ngo-npr1-5t",
                            "key": "4cwa-k7wz-4ngo-npr1-5t",
                            "uuid": "4cwa-k7wz-4ngo-npr1-5t",
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
                            "category": "schema",
                            "datasourceName": "6148b"
                        },
                        {
                            "name": "SYSPROC",
                            "children": [],
                            "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/alyy-cmri-qvfe-4n3a-t1",
                            "key": "alyy-cmri-qvfe-4n3a-t1",
                            "uuid": "alyy-cmri-qvfe-4n3a-t1",
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
                            "category": "schema",
                            "datasourceName": "6148b"
                        },
                        {
                            "name": "SYS",
                            "children": [],
                            "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/t0ud-b6uq-bdkd-v7wg-g0",
                            "key": "t0ud-b6uq-bdkd-v7wg-g0",
                            "uuid": "t0ud-b6uq-bdkd-v7wg-g0",
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
                            "category": "schema",
                            "datasourceName": "6148b"
                        },
                        {
                            "name": "SYSSTAT",
                            "children": [],
                            "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs/0g08-xedg-eg2c-jjwv-wv",
                            "key": "0g08-xedg-eg2c-jjwv-wv",
                            "uuid": "0g08-xedg-eg2c-jjwv-wv",
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
                            "category": "schema",
                            "datasourceName": "6148b"
                        }
                    ],
                    "driverType": "DynamicSwitch",
                    "keyPath": "7o38-l87u-gnsz-z490-er/136t-elkq-h4fm-1a8l-hs",
                    "key": "136t-elkq-h4fm-1a8l-hs",
                    "uuid": "136t-elkq-h4fm-1a8l-hs",
                    "fetched": true
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
                    "keyPath": "7o38-l87u-gnsz-z490-er/vyjx-mx1n-ch6z-uz17-md",
                    "key": "vyjx-mx1n-ch6z-uz17-md",
                    "uuid": "vyjx-mx1n-ch6z-uz17-md"
                }
            ],
            "key": "7o38-l87u-gnsz-z490-er",
            "uuid": "7o38-l87u-gnsz-z490-er",
            "keyPath": "7o38-l87u-gnsz-z490-er",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Elasticsearch",
            "children": [
                {
                    "data": {
                        "id": "2701",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.elasticsearch.xpack.sql.jdbc.EsDriver",
                    "name": "test",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Elasticsearch",
                    "keyPath": "0ihj-63wq-3zlh-64yf-d4/bkuj-rilk-jjh4-ix4j-r7",
                    "key": "bkuj-rilk-jjh4-ix4j-r7",
                    "uuid": "bkuj-rilk-jjh4-ix4j-r7"
                }
            ],
            "key": "0ihj-63wq-3zlh-64yf-d4",
            "uuid": "0ihj-63wq-3zlh-64yf-d4",
            "keyPath": "0ihj-63wq-3zlh-64yf-d4",
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
                    "keyPath": "3wl1-mpml-afzg-8yxz-dp/6xnh-qm1o-h6f4-1qft-cr",
                    "key": "6xnh-qm1o-h6f4-1qft-cr",
                    "uuid": "6xnh-qm1o-h6f4-1qft-cr"
                }
            ],
            "key": "3wl1-mpml-afzg-8yxz-dp",
            "uuid": "3wl1-mpml-afzg-8yxz-dp",
            "keyPath": "3wl1-mpml-afzg-8yxz-dp",
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
                    "keyPath": "zcuf-vfxo-yvbz-e8t2-ri/4cvz-d4c6-2wk3-3jtz-gw",
                    "key": "4cvz-d4c6-2wk3-3jtz-gw",
                    "uuid": "4cvz-d4c6-2wk3-3jtz-gw"
                }
            ],
            "key": "zcuf-vfxo-yvbz-e8t2-ri",
            "uuid": "zcuf-vfxo-yvbz-e8t2-ri",
            "keyPath": "zcuf-vfxo-yvbz-e8t2-ri",
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
                    "keyPath": "7k5k-im7o-w0fe-f966-7b/ja3p-z8zv-odzy-o9va-ue",
                    "key": "ja3p-z8zv-odzy-o9va-ue",
                    "uuid": "ja3p-z8zv-odzy-o9va-ue"
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
                    "keyPath": "7k5k-im7o-w0fe-f966-7b/uqk6-rhx7-51mw-1ski-42",
                    "key": "uqk6-rhx7-51mw-1ski-42",
                    "uuid": "uqk6-rhx7-51mw-1ski-42"
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
                    "children": [],
                    "driverType": "Postgresql",
                    "keyPath": "7k5k-im7o-w0fe-f966-7b/tn87-4ifs-vctj-xlev-dj",
                    "key": "tn87-4ifs-vctj-xlev-dj",
                    "uuid": "tn87-4ifs-vctj-xlev-dj"
                }
            ],
            "key": "7k5k-im7o-w0fe-f966-7b",
            "uuid": "7k5k-im7o-w0fe-f966-7b",
            "keyPath": "7k5k-im7o-w0fe-f966-7b",
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
                    "keyPath": "o915-p7gt-1idb-3uwe-pg/w4xo-8w5g-elw8-k6ni-ne",
                    "key": "w4xo-8w5g-elw8-k6ni-ne",
                    "uuid": "w4xo-8w5g-elw8-k6ni-ne"
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
                    "keyPath": "o915-p7gt-1idb-3uwe-pg/6jjn-dgmv-rw56-3g30-7m",
                    "key": "6jjn-dgmv-rw56-3g30-7m",
                    "uuid": "6jjn-dgmv-rw56-3g30-7m"
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
                    "keyPath": "o915-p7gt-1idb-3uwe-pg/49no-j9b9-l5ms-jof2-bn",
                    "key": "49no-j9b9-l5ms-jof2-bn",
                    "uuid": "49no-j9b9-l5ms-jof2-bn"
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
                    "keyPath": "o915-p7gt-1idb-3uwe-pg/vag5-p2lf-2diu-a18d-lc",
                    "key": "vag5-p2lf-2diu-a18d-lc",
                    "uuid": "vag5-p2lf-2diu-a18d-lc"
                }
            ],
            "key": "o915-p7gt-1idb-3uwe-pg",
            "uuid": "o915-p7gt-1idb-3uwe-pg",
            "keyPath": "o915-p7gt-1idb-3uwe-pg",
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
                    "keyPath": "h94w-8hf7-pvr3-ktkq-kn/ixak-nu3d-y68n-pmbe-h8",
                    "key": "ixak-nu3d-y68n-pmbe-h8",
                    "uuid": "ixak-nu3d-y68n-pmbe-h8"
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
                    "keyPath": "h94w-8hf7-pvr3-ktkq-kn/c8sa-p05p-8kan-r8un-sc",
                    "key": "c8sa-p05p-8kan-r8un-sc",
                    "uuid": "c8sa-p05p-8kan-r8un-sc"
                }
            ],
            "key": "h94w-8hf7-pvr3-ktkq-kn",
            "uuid": "h94w-8hf7-pvr3-ktkq-kn",
            "keyPath": "h94w-8hf7-pvr3-ktkq-kn",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        }
    ]
   }
}