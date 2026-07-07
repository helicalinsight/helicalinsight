export const data = {
    "record": {
        "id": "9645c648a1c0dbeec1287aaf1e996db3",
        "name": "meeting_details",
        "data": {
            "id": "1101",
            "type": "dynamicDataSource"
        },
        "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by/xyiy-pyg7-801t-h95l-69",
        "key": "xyiy-pyg7-801t-h95l-69",
        "alias": "meeting_details",
        "uuid": "xyiy-pyg7-801t-h95l-69",
        "connId": "jbzae",
        "dataSource": {
            "id": "1101",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "connId": "jbzae",
            "classifier": "db.workflow",
            "datasourceName": "derby2",
            "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by",
            "driverType": "Derby",
            "database": "HIUSER"
        },
        "dataSourceName": "derby2",
        "category": "table",
        "nameWithConnId": "meeting_details_jbzae",
        "database": "HIUSER",
        "schema": "HIUSER",
        "selected": true
    },
    "datasourceListToRender": [
        {
            "name": "Derby",
            "children": [
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/xfud-xgza-3t59-q0a8-gw",
                            "key": "xfud-xgza-3t59-q0a8-gw",
                            "uuid": "xfud-xgza-3t59-q0a8-gw",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/53fs-b6rl-odjw-5vzz-7u",
                            "key": "53fs-b6rl-odjw-5vzz-7u",
                            "uuid": "53fs-b6rl-odjw-5vzz-7u",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/shl1-ymzk-todf-a7cm-c9",
                            "key": "shl1-ymzk-todf-a7cm-c9",
                            "uuid": "shl1-ymzk-todf-a7cm-c9",
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
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm/31tn-meza-6ao7-hy19-bd",
                                    "key": "31tn-meza-6ao7-hy19-bd",
                                    "alias": "dimdate",
                                    "uuid": "31tn-meza-6ao7-hy19-bd",
                                    "connId": "qxxuk",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "qxxuk",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_qxxuk",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "4e1fd245f4d13b77be423a43f01d80b2",
                                    "name": "employee_details",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm/nuuw-pef8-5igy-kq7b-75",
                                    "key": "nuuw-pef8-5igy-kq7b-75",
                                    "alias": "employee_details",
                                    "uuid": "nuuw-pef8-5igy-kq7b-75",
                                    "connId": "qxxuk",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "qxxuk",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_qxxuk",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "be534112989b616b194bc59c2fb25a42",
                                    "name": "geo_cordinates",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm/6hme-7kpc-crdq-cuf9-pp",
                                    "key": "6hme-7kpc-crdq-cuf9-pp",
                                    "alias": "geo_cordinates",
                                    "uuid": "6hme-7kpc-crdq-cuf9-pp",
                                    "connId": "qxxuk",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "qxxuk",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_qxxuk",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "9645c648a1c0dbeec1287aaf1e996db3",
                                    "name": "meeting_details",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm/xj50-npdl-a283-e4c4-tw",
                                    "key": "xj50-npdl-a283-e4c4-tw",
                                    "alias": "meeting_details",
                                    "uuid": "xj50-npdl-a283-e4c4-tw",
                                    "connId": "qxxuk",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "qxxuk",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_qxxuk",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "8a28627d07d04ef096d9935f12e0c7e9",
                                    "name": "travel_details",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm/ttpw-89l8-v0c7-v7yz-zf",
                                    "key": "ttpw-89l8-v0c7-v7yz-zf",
                                    "alias": "travel_details",
                                    "uuid": "ttpw-89l8-v0c7-v7yz-zf",
                                    "connId": "qxxuk",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "qxxuk",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_qxxuk",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                }
                            ],
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm",
                            "key": "oxpa-36wy-jhna-fcf8-jm",
                            "uuid": "oxpa-36wy-jhna-fcf8-jm",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/uwv5-2uu9-kwkp-stdq-h5",
                            "key": "uwv5-2uu9-kwkp-stdq-h5",
                            "uuid": "uwv5-2uu9-kwkp-stdq-h5",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/lr5e-z8hm-3n0p-x20h-ew",
                            "key": "lr5e-z8hm-3n0p-x20h-ew",
                            "uuid": "lr5e-z8hm-3n0p-x20h-ew",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/ih6b-qxvv-kmkk-dwen-vp",
                            "key": "ih6b-qxvv-kmkk-dwen-vp",
                            "uuid": "ih6b-qxvv-kmkk-dwen-vp",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/9mzn-o5bj-ooex-onys-r8",
                            "key": "9mzn-o5bj-ooex-onys-r8",
                            "uuid": "9mzn-o5bj-ooex-onys-r8",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/fttg-eb7h-jcvy-v82l-co",
                            "key": "fttg-eb7h-jcvy-v82l-co",
                            "uuid": "fttg-eb7h-jcvy-v82l-co",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/idmk-mrmc-k8bz-ix89-37",
                            "key": "idmk-mrmc-k8bz-ix89-37",
                            "uuid": "idmk-mrmc-k8bz-ix89-37",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/tfxa-v62d-0ch0-f75n-4m",
                            "key": "tfxa-v62d-0ch0-f75n-4m",
                            "uuid": "tfxa-v62d-0ch0-f75n-4m",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/nuiv-624t-qbgv-gwnu-4h",
                            "key": "nuiv-624t-qbgv-gwnu-4h",
                            "uuid": "nuiv-624t-qbgv-gwnu-4h",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8",
                    "key": "lbbi-8w2i-h0om-qbbp-e8",
                    "uuid": "lbbi-8w2i-h0om-qbbp-e8",
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
                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/csd3-hp7k-qqud-k97a-x7",
                    "key": "csd3-hp7k-qqud-k97a-x7",
                    "uuid": "csd3-hp7k-qqud-k97a-x7"
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/5ux8-yo4s-29dj-8fgy-lh",
                            "key": "5ux8-yo4s-29dj-8fgy-lh",
                            "uuid": "5ux8-yo4s-29dj-8fgy-lh",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/0u29-so05-jhtj-k2lo-1t",
                            "key": "0u29-so05-jhtj-k2lo-1t",
                            "uuid": "0u29-so05-jhtj-k2lo-1t",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/9zxg-nbww-e92u-ghqb-u8",
                            "key": "9zxg-nbww-e92u-ghqb-u8",
                            "uuid": "9zxg-nbww-e92u-ghqb-u8",
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
                                    "data": {
                                        "id": "1101",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by/tg10-7u77-o1gv-k2ul-bs",
                                    "key": "tg10-7u77-o1gv-k2ul-bs",
                                    "alias": "dimdate",
                                    "uuid": "tg10-7u77-o1gv-k2ul-bs",
                                    "connId": "jbzae",
                                    "dataSource": {
                                        "id": "1101",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "jbzae",
                                        "classifier": "db.workflow",
                                        "datasourceName": "derby2",
                                        "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "derby2",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_jbzae",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "4e1fd245f4d13b77be423a43f01d80b2",
                                    "name": "employee_details",
                                    "data": {
                                        "id": "1101",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by/7wm3-3qto-lw8c-grwh-gb",
                                    "key": "7wm3-3qto-lw8c-grwh-gb",
                                    "alias": "employee_details",
                                    "uuid": "7wm3-3qto-lw8c-grwh-gb",
                                    "connId": "jbzae",
                                    "dataSource": {
                                        "id": "1101",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "jbzae",
                                        "classifier": "db.workflow",
                                        "datasourceName": "derby2",
                                        "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "derby2",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_jbzae",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "be534112989b616b194bc59c2fb25a42",
                                    "name": "geo_cordinates",
                                    "data": {
                                        "id": "1101",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by/f080-6ikn-bk2i-oxmj-dp",
                                    "key": "f080-6ikn-bk2i-oxmj-dp",
                                    "alias": "geo_cordinates",
                                    "uuid": "f080-6ikn-bk2i-oxmj-dp",
                                    "connId": "jbzae",
                                    "dataSource": {
                                        "id": "1101",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "jbzae",
                                        "classifier": "db.workflow",
                                        "datasourceName": "derby2",
                                        "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "derby2",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_jbzae",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "9645c648a1c0dbeec1287aaf1e996db3",
                                    "name": "meeting_details",
                                    "data": {
                                        "id": "1101",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by/xyiy-pyg7-801t-h95l-69",
                                    "key": "xyiy-pyg7-801t-h95l-69",
                                    "alias": "meeting_details",
                                    "uuid": "xyiy-pyg7-801t-h95l-69",
                                    "connId": "jbzae",
                                    "dataSource": {
                                        "id": "1101",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "jbzae",
                                        "classifier": "db.workflow",
                                        "datasourceName": "derby2",
                                        "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "derby2",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_jbzae",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "8a28627d07d04ef096d9935f12e0c7e9",
                                    "name": "travel_details",
                                    "data": {
                                        "id": "1101",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by/s3pv-6mdb-o4in-vgmj-5s",
                                    "key": "s3pv-6mdb-o4in-vgmj-5s",
                                    "alias": "travel_details",
                                    "uuid": "s3pv-6mdb-o4in-vgmj-5s",
                                    "connId": "jbzae",
                                    "dataSource": {
                                        "id": "1101",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "jbzae",
                                        "classifier": "db.workflow",
                                        "datasourceName": "derby2",
                                        "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "derby2",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_jbzae",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                }
                            ],
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by",
                            "key": "u6n5-16ep-0v09-ej3i-by",
                            "uuid": "u6n5-16ep-0v09-ej3i-by",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/noep-dfg2-qghj-nx5p-1v",
                            "key": "noep-dfg2-qghj-nx5p-1v",
                            "uuid": "noep-dfg2-qghj-nx5p-1v",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/6rxg-zzdh-06r9-gqhr-dv",
                            "key": "6rxg-zzdh-06r9-gqhr-dv",
                            "uuid": "6rxg-zzdh-06r9-gqhr-dv",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/9zd4-ffjd-8x4x-ij6l-9v",
                            "key": "9zd4-ffjd-8x4x-ij6l-9v",
                            "uuid": "9zd4-ffjd-8x4x-ij6l-9v",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/83p8-x38t-k5ky-swpp-ce",
                            "key": "83p8-x38t-k5ky-swpp-ce",
                            "uuid": "83p8-x38t-k5ky-swpp-ce",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/0fo6-ikrl-cqrn-9wvr-wf",
                            "key": "0fo6-ikrl-cqrn-9wvr-wf",
                            "uuid": "0fo6-ikrl-cqrn-9wvr-wf",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/6qml-xr3c-u8tg-4vv5-iu",
                            "key": "6qml-xr3c-u8tg-4vv5-iu",
                            "uuid": "6qml-xr3c-u8tg-4vv5-iu",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/546x-s1xr-vaav-2a9v-cu",
                            "key": "546x-s1xr-vaav-2a9v-cu",
                            "uuid": "546x-s1xr-vaav-2a9v-cu",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/4j4z-6uuf-o5ig-239t-uc",
                            "key": "4j4z-6uuf-o5ig-239t-uc",
                            "uuid": "4j4z-6uuf-o5ig-239t-uc",
                            "data": {
                                "id": "1101",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "derby2"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw",
                    "key": "kdum-2nmg-r8s1-ija3-kw",
                    "uuid": "kdum-2nmg-r8s1-ija3-kw",
                    "fetched": true
                }
            ],
            "key": "i5jo-54um-wzkv-vxkd-qb",
            "uuid": "i5jo-54um-wzkv-vxkd-qb",
            "keyPath": "i5jo-54um-wzkv-vxkd-qb",
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
                        "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                        "jdbcUrl": null,
                        "driver": "dynamicSwitch"
                    },
                    "dataSourceType": "",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "DynamicSwitch",
                    "keyPath": "num3-p23p-psyt-t3di-li/tvz5-dkqf-4whb-czs1-a4",
                    "key": "tvz5-dkqf-4whb-czs1-a4",
                    "uuid": "tvz5-dkqf-4whb-czs1-a4"
                }
            ],
            "key": "num3-p23p-psyt-t3di-li",
            "uuid": "num3-p23p-psyt-t3di-li",
            "keyPath": "num3-p23p-psyt-t3di-li",
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
                    "keyPath": "ma2f-256r-bt0x-cu3y-8k/y0ij-csak-psfs-91tz-r1",
                    "key": "y0ij-csak-psfs-91tz-r1",
                    "uuid": "y0ij-csak-psfs-91tz-r1"
                }
            ],
            "key": "ma2f-256r-bt0x-cu3y-8k",
            "uuid": "ma2f-256r-bt0x-cu3y-8k",
            "keyPath": "ma2f-256r-bt0x-cu3y-8k",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        }
    ],
    "type": "merge",
    "tablesToAdd": false,
    "returnData": false,
    "tablesAddedToMetadata_test": false,
    "fetchedTables": false,
    "noCatSchema": false
}

export const storeData = {
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": false,
        "viewSessionVariables": false,
        "listDataSources": true,
        "lbbi-8w2i-h0om-qbbp-e8": false,
        "oxpa-36wy-jhna-fcf8-jm": false,
        "kdum-2nmg-r8s1-ija3-kw": false,
        "u6n5-16ep-0v09-ej3i-by": false
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "lbbi-8w2i-h0om-qbbp-e8": false,
        "oxpa-36wy-jhna-fcf8-jm": false,
        "kdum-2nmg-r8s1-ija3-kw": false,
        "u6n5-16ep-0v09-ej3i-by": false
    },
    "serviceErrorStatus": {},
    "fetchedDSInfo": {},
    "listDataSource": [],
    "supportedDataSourceTypes": [
        {
            "name": "Access",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Amazon Dynamodb",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Amazon Redshift",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Dremio",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Google Bigquery",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Microsoft Sqlserver",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Oracle",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Teradata",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Ξ Add Driver Ξ",
            "categoryType": "supported",
            "categoryName": "Supported"
        }
    ],
    "allDataSourceTypes": [
        {
            "name": "Access",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Amazon Dynamodb",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Amazon Redshift",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "driver": "org.apache.drill.jdbc.Driver",
            "databaseDialect": "drill",
            "enabledTypes": false,
            "name": "Apache Drill",
            "categoryName": "Big Data",
            "categoryType": "big_data",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:drill:{{hostName}}:{{port}}",
            "parameters": {
                "port": "31010",
                "hostName": "drillbit=localhost"
            }
        },
        {
            "driver": "com.simba.athena.jdbc.Driver",
            "databaseDialect": "athena",
            "name": "Athena",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:awsathena://{{hostName}}",
            "parameters": {
                "hostName": "localhost"
            }
        },
        {
            "driver": "ru.yandex.clickhouse.ClickHouseDriver",
            "databaseDialect": "clickhouse",
            "name": "Clickhouse",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:clickhouse://{{hostName}}:{{port}}/{{database}}",
            "parameters": {
                "port": "8123",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "driver": "com.databricks.client.jdbc.Driver",
            "databaseDialect": "databricks",
            "name": "Databricks Client",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:databricks://{{hostName}}:{{port}};{{database}}",
            "parameters": {
                "port": "443",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "driver": "org.apache.derby.jdbc.ClientDriver",
            "databaseDialect": "derby",
            "name": "Derby",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:derby://{{hostName}}:{{port}}/{{database}}",
            "parameters": {
                "port": "1527",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "databaseDialect": "derby",
            "name": "Derby",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:derby:{{database}}",
            "parameters": {
                "database": "database"
            }
        },
        {
            "name": "Dremio",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "driver": "dynamicSwitch",
            "databaseDialect": "",
            "name": "DynamicSwitch",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "org.elasticsearch.xpack.sql.jdbc.EsDriver",
            "databaseDialect": "",
            "name": "Elasticsearch",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Google Bigquery",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "driver": "com.helical.mongodb.MongoJdbcDriver",
            "databaseDialect": "himongo",
            "name": "Helical Mongodb",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "org.apache.hive.jdbc.HiveDriver",
            "databaseDialect": "spark",
            "name": "Hive",
            "categoryName": "Big Data",
            "categoryType": "big_data",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:hive2://{{hostName}}:{{port}}/{{database}}",
            "parameters": {
                "port": "10001",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "driver": "com.ibm.db2.jcc.DB2Driver",
            "databaseDialect": "db2",
            "name": "IBM Db2",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:db2://{{hostName}}:{{port}}/{{database}}",
            "parameters": {
                "port": "50000",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "driver": "com.informix.jdbc.IfxDriver",
            "databaseDialect": "informix",
            "name": "Informix",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:informix-sqli://{{hostName}}:{{port}}/{{database}}:delimident=y",
            "parameters": {
                "port": "1526",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "driver": "org.mariadb.jdbc.Driver",
            "databaseDialect": "mysql",
            "name": "Mariadb",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:mariadb://{{hostName}}:{{port}}/{{database}}",
            "parameters": {
                "port": "3306",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "name": "Microsoft Sqlserver",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "driver": "net.sourceforge.jtds.jdbc.Driver",
            "databaseDialect": "sqlserver",
            "name": "Microsoft Sqlserver(sourceforge)",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:jtds:sqlserver://{{hostName}}:{{port}}/{{database}}",
            "parameters": {
                "port": "1433",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "driver": "com.mysql.jdbc.Driver",
            "databaseDialect": "mysql",
            "name": "Mysql",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:mysql://{{hostName}}:{{port}}/{{database}}",
            "parameters": {
                "port": "3306",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "driver": "com.mysql.cj.jdbc.Driver",
            "databaseDialect": "mysql",
            "name": "Mysql Cj",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:mysql://{{hostName}}:{{port}}/{{database}}",
            "parameters": {
                "port": "3306",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "name": "Oracle",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "driver": "org.postgresql.Driver",
            "databaseDialect": "postgresql",
            "name": "Postgresql",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:postgresql://{{hostName}}:{{port}}/{{database}}",
            "parameters": {
                "port": "5432",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "driver": "com.facebook.presto.jdbc.PrestoDriver",
            "databaseDialect": "presto",
            "name": "Presto",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:presto://{{hostName}}:{{port}}",
            "parameters": {
                "port": "8090",
                "hostName": "localhost"
            }
        },
        {
            "driver": "org.sqlite.JDBC",
            "databaseDialect": "sqlite",
            "name": "Sqlite",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:sqlite:{{database}}",
            "parameters": {
                "database": "database"
            }
        },
        {
            "name": "Teradata",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Ξ Add Driver Ξ",
            "categoryType": "supported",
            "categoryName": "Supported"
        }
    ],
    "metaDataSourceList": [
        {
            "driver": "org.apache.drill.jdbc.Driver",
            "databaseDialect": "drill",
            "enabledTypes": false,
            "name": "Apache Drill",
            "categoryName": "Big Data",
            "categoryType": "big_data",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:drill:{{hostName}}:{{port}}",
            "parameters": {
                "port": "31010",
                "hostName": "drillbit=localhost"
            }
        },
        {
            "driver": "com.simba.athena.jdbc.Driver",
            "databaseDialect": "athena",
            "name": "Athena",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:awsathena://{{hostName}}",
            "parameters": {
                "hostName": "localhost"
            }
        },
        {
            "driver": "ru.yandex.clickhouse.ClickHouseDriver",
            "databaseDialect": "clickhouse",
            "name": "Clickhouse",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:clickhouse://{{hostName}}:{{port}}/{{database}}",
            "parameters": {
                "port": "8123",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "driver": "com.databricks.client.jdbc.Driver",
            "databaseDialect": "databricks",
            "name": "Databricks Client",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:databricks://{{hostName}}:{{port}};{{database}}",
            "parameters": {
                "port": "443",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "driver": "org.apache.derby.jdbc.ClientDriver",
            "databaseDialect": "derby",
            "name": "Derby",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:derby://{{hostName}}:{{port}}/{{database}}",
            "parameters": {
                "port": "1527",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "databaseDialect": "derby",
            "name": "Derby",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:derby:{{database}}",
            "parameters": {
                "database": "database"
            }
        },
        {
            "driver": "dynamicSwitch",
            "databaseDialect": "",
            "name": "DynamicSwitch",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "org.elasticsearch.xpack.sql.jdbc.EsDriver",
            "databaseDialect": "",
            "name": "Elasticsearch",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "com.helical.mongodb.MongoJdbcDriver",
            "databaseDialect": "himongo",
            "name": "Helical Mongodb",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "org.apache.hive.jdbc.HiveDriver",
            "databaseDialect": "spark",
            "name": "Hive",
            "categoryName": "Big Data",
            "categoryType": "big_data",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:hive2://{{hostName}}:{{port}}/{{database}}",
            "parameters": {
                "port": "10001",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "driver": "com.ibm.db2.jcc.DB2Driver",
            "databaseDialect": "db2",
            "name": "IBM Db2",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:db2://{{hostName}}:{{port}}/{{database}}",
            "parameters": {
                "port": "50000",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "driver": "com.informix.jdbc.IfxDriver",
            "databaseDialect": "informix",
            "name": "Informix",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:informix-sqli://{{hostName}}:{{port}}/{{database}}:delimident=y",
            "parameters": {
                "port": "1526",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "driver": "org.mariadb.jdbc.Driver",
            "databaseDialect": "mysql",
            "name": "Mariadb",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:mariadb://{{hostName}}:{{port}}/{{database}}",
            "parameters": {
                "port": "3306",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "driver": "net.sourceforge.jtds.jdbc.Driver",
            "databaseDialect": "sqlserver",
            "name": "Microsoft Sqlserver(sourceforge)",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:jtds:sqlserver://{{hostName}}:{{port}}/{{database}}",
            "parameters": {
                "port": "1433",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "driver": "com.mysql.jdbc.Driver",
            "databaseDialect": "mysql",
            "name": "Mysql",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:mysql://{{hostName}}:{{port}}/{{database}}",
            "parameters": {
                "port": "3306",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "driver": "com.mysql.cj.jdbc.Driver",
            "databaseDialect": "mysql",
            "name": "Mysql Cj",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:mysql://{{hostName}}:{{port}}/{{database}}",
            "parameters": {
                "port": "3306",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "driver": "org.postgresql.Driver",
            "databaseDialect": "postgresql",
            "name": "Postgresql",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:postgresql://{{hostName}}:{{port}}/{{database}}",
            "parameters": {
                "port": "5432",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "driver": "com.facebook.presto.jdbc.PrestoDriver",
            "databaseDialect": "presto",
            "name": "Presto",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:presto://{{hostName}}:{{port}}",
            "parameters": {
                "port": "8090",
                "hostName": "localhost"
            }
        },
        {
            "driver": "org.sqlite.JDBC",
            "databaseDialect": "sqlite",
            "name": "Sqlite",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:sqlite:{{database}}",
            "parameters": {
                "database": "database"
            }
        }
    ],
    "driversList": [
        {
            "driver": "dynamicSwitch",
            "showInDatasource": "false",
            "available": "true",
            "parameters": []
        },
        {
            "url": "jdbc:derby:{{database}}",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "available": "true",
            "parameters": {
                "database": "database"
            }
        },
        {
            "url": "jdbc:mysql://{{hostName}}:{{port}}/{{database}}",
            "driver": "com.mysql.cj.jdbc.Driver",
            "available": "true",
            "parameters": {
                "port": "3306",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "url": "jdbc:mysql://{{hostName}}:{{port}}/{{database}}",
            "driver": "com.mysql.jdbc.Driver",
            "available": "true",
            "parameters": {
                "port": "3306",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "available": "true",
            "driver": "org.elasticsearch.xpack.sql.jdbc.EsDriver"
        },
        {
            "url": "jdbc:awsathena://{{hostName}}",
            "driver": "com.simba.athena.jdbc.Driver",
            "available": "true",
            "parameters": {
                "hostName": "localhost"
            }
        },
        {
            "url": "jdbc:jtds:sqlserver://{{hostName}}:{{port}}/{{database}}",
            "driver": "net.sourceforge.jtds.jdbc.Driver",
            "available": "true",
            "parameters": {
                "port": "1433",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "url": "jdbc:clickhouse://{{hostName}}:{{port}}/{{database}}",
            "driver": "ru.yandex.clickhouse.ClickHouseDriver",
            "available": "true",
            "parameters": {
                "port": "8123",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "available": "true",
            "driver": "com.helical.mongodb.MongoJdbcDriver"
        },
        {
            "url": "jdbc:hive2://{{hostName}}:{{port}}/{{database}}",
            "driver": "org.apache.hive.jdbc.HiveDriver",
            "available": "true",
            "parameters": {
                "port": "10001",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "url": "jdbc:drill:{{hostName}}:{{port}}",
            "driver": "org.apache.drill.jdbc.Driver",
            "available": "true",
            "parameters": {
                "port": "31010",
                "hostName": "drillbit=localhost"
            }
        },
        {
            "url": "jdbc:informix-sqli://{{hostName}}:{{port}}/{{database}}:delimident=y",
            "driver": "com.informix.jdbc.IfxDriver",
            "available": "true",
            "parameters": {
                "port": "1526",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "url": "jdbc:db2://{{hostName}}:{{port}}/{{database}}",
            "driver": "com.ibm.db2.jcc.DB2Driver",
            "available": "true",
            "parameters": {
                "port": "50000",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "url": "jdbc:mariadb://{{hostName}}:{{port}}/{{database}}",
            "driver": "org.mariadb.jdbc.Driver",
            "available": "true",
            "parameters": {
                "port": "3306",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "url": "jdbc:presto://{{hostName}}:{{port}}",
            "driver": "com.facebook.presto.jdbc.PrestoDriver",
            "available": "true",
            "parameters": {
                "port": "8090",
                "hostName": "localhost"
            }
        },
        {
            "url": "jdbc:databricks://{{hostName}}:{{port}};{{database}}",
            "driver": "com.databricks.client.jdbc.Driver",
            "available": "true",
            "parameters": {
                "port": "443",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "available": "true",
            "driver": "org.elasticsearch.xpack.sql.jdbc.EsDriver"
        },
        {
            "url": "jdbc:postgresql://{{hostName}}:{{port}}/{{database}}",
            "driver": "org.postgresql.Driver",
            "available": "true",
            "parameters": {
                "port": "5432",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "url": "jdbc:derby://{{hostName}}:{{port}}/{{database}}",
            "driver": "org.apache.derby.jdbc.ClientDriver",
            "available": "true",
            "parameters": {
                "port": "1527",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "url": "jdbc:sqlite:{{database}}",
            "driver": "org.sqlite.JDBC",
            "available": "true",
            "parameters": {
                "database": "database"
            }
        }
    ],
    "allDataSources": [
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
                "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                "jdbcUrl": null,
                "driver": "dynamicSwitch"
            },
            "dataSourceType": ""
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
            "dataSourceType": "Managed DataSource"
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
            "dataSourceType": "Managed DataSource"
        },
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
            "dataSourceType": "Managed DataSource"
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
            "dataSourceType": "Managed DataSource"
        }
    ],
    "dataSourceTypes": [
        {
            "type": "sql.jdbc.groovy.managed",
            "name": "Groovy Managed Jdbc DataSource",
            "classifier": "efwd",
            "categoryName": "advanced",
            "categoryType": "advanced"
        },
        {
            "type": "sql.jdbc.groovy",
            "name": "Groovy Plain Jdbc DataSource",
            "classifier": "efwd",
            "categoryName": "advanced",
            "categoryType": "advanced"
        },
        {
            "type": "global.jdbc",
            "name": "Managed DataSource",
            "classifier": "global",
            "categoryName": "advanced",
            "categoryType": "advanced"
        },
        {
            "type": "sql.jdbc",
            "name": "Plain Jdbc DataSource",
            "classifier": "efwd",
            "categoryName": "advanced",
            "categoryType": "advanced"
        }
    ],
    "datasourceListToRender": [
        {
            "name": "Derby",
            "children": [
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/xfud-xgza-3t59-q0a8-gw",
                            "key": "xfud-xgza-3t59-q0a8-gw",
                            "uuid": "xfud-xgza-3t59-q0a8-gw",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/53fs-b6rl-odjw-5vzz-7u",
                            "key": "53fs-b6rl-odjw-5vzz-7u",
                            "uuid": "53fs-b6rl-odjw-5vzz-7u",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/shl1-ymzk-todf-a7cm-c9",
                            "key": "shl1-ymzk-todf-a7cm-c9",
                            "uuid": "shl1-ymzk-todf-a7cm-c9",
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
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm/31tn-meza-6ao7-hy19-bd",
                                    "key": "31tn-meza-6ao7-hy19-bd",
                                    "alias": "dimdate",
                                    "uuid": "31tn-meza-6ao7-hy19-bd",
                                    "connId": "qxxuk",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "qxxuk",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_qxxuk",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "4e1fd245f4d13b77be423a43f01d80b2",
                                    "name": "employee_details",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm/nuuw-pef8-5igy-kq7b-75",
                                    "key": "nuuw-pef8-5igy-kq7b-75",
                                    "alias": "employee_details",
                                    "uuid": "nuuw-pef8-5igy-kq7b-75",
                                    "connId": "qxxuk",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "qxxuk",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_qxxuk",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "be534112989b616b194bc59c2fb25a42",
                                    "name": "geo_cordinates",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm/6hme-7kpc-crdq-cuf9-pp",
                                    "key": "6hme-7kpc-crdq-cuf9-pp",
                                    "alias": "geo_cordinates",
                                    "uuid": "6hme-7kpc-crdq-cuf9-pp",
                                    "connId": "qxxuk",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "qxxuk",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_qxxuk",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "9645c648a1c0dbeec1287aaf1e996db3",
                                    "name": "meeting_details",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm/xj50-npdl-a283-e4c4-tw",
                                    "key": "xj50-npdl-a283-e4c4-tw",
                                    "alias": "meeting_details",
                                    "uuid": "xj50-npdl-a283-e4c4-tw",
                                    "connId": "qxxuk",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "qxxuk",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_qxxuk",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "8a28627d07d04ef096d9935f12e0c7e9",
                                    "name": "travel_details",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm/ttpw-89l8-v0c7-v7yz-zf",
                                    "key": "ttpw-89l8-v0c7-v7yz-zf",
                                    "alias": "travel_details",
                                    "uuid": "ttpw-89l8-v0c7-v7yz-zf",
                                    "connId": "qxxuk",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "qxxuk",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_qxxuk",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                }
                            ],
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm",
                            "key": "oxpa-36wy-jhna-fcf8-jm",
                            "uuid": "oxpa-36wy-jhna-fcf8-jm",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/uwv5-2uu9-kwkp-stdq-h5",
                            "key": "uwv5-2uu9-kwkp-stdq-h5",
                            "uuid": "uwv5-2uu9-kwkp-stdq-h5",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/lr5e-z8hm-3n0p-x20h-ew",
                            "key": "lr5e-z8hm-3n0p-x20h-ew",
                            "uuid": "lr5e-z8hm-3n0p-x20h-ew",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/ih6b-qxvv-kmkk-dwen-vp",
                            "key": "ih6b-qxvv-kmkk-dwen-vp",
                            "uuid": "ih6b-qxvv-kmkk-dwen-vp",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/9mzn-o5bj-ooex-onys-r8",
                            "key": "9mzn-o5bj-ooex-onys-r8",
                            "uuid": "9mzn-o5bj-ooex-onys-r8",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/fttg-eb7h-jcvy-v82l-co",
                            "key": "fttg-eb7h-jcvy-v82l-co",
                            "uuid": "fttg-eb7h-jcvy-v82l-co",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/idmk-mrmc-k8bz-ix89-37",
                            "key": "idmk-mrmc-k8bz-ix89-37",
                            "uuid": "idmk-mrmc-k8bz-ix89-37",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/tfxa-v62d-0ch0-f75n-4m",
                            "key": "tfxa-v62d-0ch0-f75n-4m",
                            "uuid": "tfxa-v62d-0ch0-f75n-4m",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/nuiv-624t-qbgv-gwnu-4h",
                            "key": "nuiv-624t-qbgv-gwnu-4h",
                            "uuid": "nuiv-624t-qbgv-gwnu-4h",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8",
                    "key": "lbbi-8w2i-h0om-qbbp-e8",
                    "uuid": "lbbi-8w2i-h0om-qbbp-e8",
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
                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/csd3-hp7k-qqud-k97a-x7",
                    "key": "csd3-hp7k-qqud-k97a-x7",
                    "uuid": "csd3-hp7k-qqud-k97a-x7"
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/5ux8-yo4s-29dj-8fgy-lh",
                            "key": "5ux8-yo4s-29dj-8fgy-lh",
                            "uuid": "5ux8-yo4s-29dj-8fgy-lh",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/0u29-so05-jhtj-k2lo-1t",
                            "key": "0u29-so05-jhtj-k2lo-1t",
                            "uuid": "0u29-so05-jhtj-k2lo-1t",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/9zxg-nbww-e92u-ghqb-u8",
                            "key": "9zxg-nbww-e92u-ghqb-u8",
                            "uuid": "9zxg-nbww-e92u-ghqb-u8",
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
                                    "data": {
                                        "id": "1101",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by/tg10-7u77-o1gv-k2ul-bs",
                                    "key": "tg10-7u77-o1gv-k2ul-bs",
                                    "alias": "dimdate",
                                    "uuid": "tg10-7u77-o1gv-k2ul-bs",
                                    "connId": "jbzae",
                                    "dataSource": {
                                        "id": "1101",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "jbzae",
                                        "classifier": "db.workflow",
                                        "datasourceName": "derby2",
                                        "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "derby2",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_jbzae",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "4e1fd245f4d13b77be423a43f01d80b2",
                                    "name": "employee_details",
                                    "data": {
                                        "id": "1101",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by/7wm3-3qto-lw8c-grwh-gb",
                                    "key": "7wm3-3qto-lw8c-grwh-gb",
                                    "alias": "employee_details",
                                    "uuid": "7wm3-3qto-lw8c-grwh-gb",
                                    "connId": "jbzae",
                                    "dataSource": {
                                        "id": "1101",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "jbzae",
                                        "classifier": "db.workflow",
                                        "datasourceName": "derby2",
                                        "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "derby2",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_jbzae",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "be534112989b616b194bc59c2fb25a42",
                                    "name": "geo_cordinates",
                                    "data": {
                                        "id": "1101",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by/f080-6ikn-bk2i-oxmj-dp",
                                    "key": "f080-6ikn-bk2i-oxmj-dp",
                                    "alias": "geo_cordinates",
                                    "uuid": "f080-6ikn-bk2i-oxmj-dp",
                                    "connId": "jbzae",
                                    "dataSource": {
                                        "id": "1101",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "jbzae",
                                        "classifier": "db.workflow",
                                        "datasourceName": "derby2",
                                        "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "derby2",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_jbzae",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "9645c648a1c0dbeec1287aaf1e996db3",
                                    "name": "meeting_details",
                                    "data": {
                                        "id": "1101",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by/xyiy-pyg7-801t-h95l-69",
                                    "key": "xyiy-pyg7-801t-h95l-69",
                                    "alias": "meeting_details",
                                    "uuid": "xyiy-pyg7-801t-h95l-69",
                                    "connId": "jbzae",
                                    "dataSource": {
                                        "id": "1101",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "jbzae",
                                        "classifier": "db.workflow",
                                        "datasourceName": "derby2",
                                        "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "derby2",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_jbzae",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "8a28627d07d04ef096d9935f12e0c7e9",
                                    "name": "travel_details",
                                    "data": {
                                        "id": "1101",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by/s3pv-6mdb-o4in-vgmj-5s",
                                    "key": "s3pv-6mdb-o4in-vgmj-5s",
                                    "alias": "travel_details",
                                    "uuid": "s3pv-6mdb-o4in-vgmj-5s",
                                    "connId": "jbzae",
                                    "dataSource": {
                                        "id": "1101",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "jbzae",
                                        "classifier": "db.workflow",
                                        "datasourceName": "derby2",
                                        "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "derby2",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_jbzae",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                }
                            ],
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by",
                            "key": "u6n5-16ep-0v09-ej3i-by",
                            "uuid": "u6n5-16ep-0v09-ej3i-by",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/noep-dfg2-qghj-nx5p-1v",
                            "key": "noep-dfg2-qghj-nx5p-1v",
                            "uuid": "noep-dfg2-qghj-nx5p-1v",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/6rxg-zzdh-06r9-gqhr-dv",
                            "key": "6rxg-zzdh-06r9-gqhr-dv",
                            "uuid": "6rxg-zzdh-06r9-gqhr-dv",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/9zd4-ffjd-8x4x-ij6l-9v",
                            "key": "9zd4-ffjd-8x4x-ij6l-9v",
                            "uuid": "9zd4-ffjd-8x4x-ij6l-9v",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/83p8-x38t-k5ky-swpp-ce",
                            "key": "83p8-x38t-k5ky-swpp-ce",
                            "uuid": "83p8-x38t-k5ky-swpp-ce",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/0fo6-ikrl-cqrn-9wvr-wf",
                            "key": "0fo6-ikrl-cqrn-9wvr-wf",
                            "uuid": "0fo6-ikrl-cqrn-9wvr-wf",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/6qml-xr3c-u8tg-4vv5-iu",
                            "key": "6qml-xr3c-u8tg-4vv5-iu",
                            "uuid": "6qml-xr3c-u8tg-4vv5-iu",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/546x-s1xr-vaav-2a9v-cu",
                            "key": "546x-s1xr-vaav-2a9v-cu",
                            "uuid": "546x-s1xr-vaav-2a9v-cu",
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
                            "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/4j4z-6uuf-o5ig-239t-uc",
                            "key": "4j4z-6uuf-o5ig-239t-uc",
                            "uuid": "4j4z-6uuf-o5ig-239t-uc",
                            "data": {
                                "id": "1101",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "derby2"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw",
                    "key": "kdum-2nmg-r8s1-ija3-kw",
                    "uuid": "kdum-2nmg-r8s1-ija3-kw",
                    "fetched": true
                }
            ],
            "key": "i5jo-54um-wzkv-vxkd-qb",
            "uuid": "i5jo-54um-wzkv-vxkd-qb",
            "keyPath": "i5jo-54um-wzkv-vxkd-qb",
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
                        "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                        "jdbcUrl": null,
                        "driver": "dynamicSwitch"
                    },
                    "dataSourceType": "",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "DynamicSwitch",
                    "keyPath": "num3-p23p-psyt-t3di-li/tvz5-dkqf-4whb-czs1-a4",
                    "key": "tvz5-dkqf-4whb-czs1-a4",
                    "uuid": "tvz5-dkqf-4whb-czs1-a4"
                }
            ],
            "key": "num3-p23p-psyt-t3di-li",
            "uuid": "num3-p23p-psyt-t3di-li",
            "keyPath": "num3-p23p-psyt-t3di-li",
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
                    "keyPath": "ma2f-256r-bt0x-cu3y-8k/y0ij-csak-psfs-91tz-r1",
                    "key": "y0ij-csak-psfs-91tz-r1",
                    "uuid": "y0ij-csak-psfs-91tz-r1"
                }
            ],
            "key": "ma2f-256r-bt0x-cu3y-8k",
            "uuid": "ma2f-256r-bt0x-cu3y-8k",
            "keyPath": "ma2f-256r-bt0x-cu3y-8k",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        }
    ],
    "workFlow": {
        "dataList": [
            {
                "fetchSchemas": true,
                "fetchCatalogs": true,
                "working": true,
                "connData": {
                    "id": "1",
                    "type": "dynamicDataSource"
                },
                "connId": "1",
                "dsUUID": "lbbi-8w2i-h0om-qbbp-e8",
                "classifier": "db.workflow",
                "metadata": {
                    "catalogs": [
                        {
                            "name": "Null",
                            "schemas": [
                                {
                                    "name": "SQLJ"
                                },
                                {
                                    "name": "SYSFUN"
                                },
                                {
                                    "name": "SYSCAT"
                                },
                                {
                                    "name": "HIUSER"
                                },
                                {
                                    "name": "SYSCS_DIAG"
                                },
                                {
                                    "name": "SYSCS_UTIL"
                                },
                                {
                                    "name": "SYSIBM"
                                },
                                {
                                    "name": "APP"
                                },
                                {
                                    "name": "NULLID"
                                },
                                {
                                    "name": "SYSPROC"
                                },
                                {
                                    "name": "SYS"
                                },
                                {
                                    "name": "SYSSTAT"
                                }
                            ]
                        }
                    ]
                }
            },
            {
                "classifier": "db.workflow",
                "metadata": {
                    "catalogs": [
                        {
                            "name": "Null",
                            "schemas": [
                                {
                                    "name": "HIUSER",
                                    "tables": [
                                        {
                                            "id": "be534112989b616b194bc59c2fb25a42",
                                            "name": "geo_cordinates"
                                        },
                                        {
                                            "id": "9645c648a1c0dbeec1287aaf1e996db3",
                                            "name": "meeting_details"
                                        },
                                        {
                                            "id": "4e1fd245f4d13b77be423a43f01d80b2",
                                            "name": "employee_details"
                                        },
                                        {
                                            "id": "4ac5d9f68b58bd7c0d179146e46795be",
                                            "name": "dimdate"
                                        },
                                        {
                                            "id": "8a28627d07d04ef096d9935f12e0c7e9",
                                            "name": "travel_details"
                                        }
                                    ]
                                }
                            ]
                        }
                    ],
                    "dataSource": {
                        "id": "1101",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc",
                        "catSchemaPredicted": false,
                        "sync": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "connId": "jbzae",
                        "classifier": "db.workflow",
                        "datasourceName": "derby2",
                        "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by",
                        "driverType": "Derby",
                        "database": "HIUSER"
                    },
                    "name": "HIUSER"
                }
            },
            {
                "fetchSchemas": true,
                "fetchCatalogs": true,
                "working": true,
                "connData": {
                    "id": "1101",
                    "type": "dynamicDataSource"
                },
                "connId": "1101",
                "dsUUID": "kdum-2nmg-r8s1-ija3-kw",
                "classifier": "db.workflow",
                "metadata": {
                    "catalogs": [
                        {
                            "name": "Null",
                            "schemas": [
                                {
                                    "name": "SQLJ"
                                },
                                {
                                    "name": "SYSFUN"
                                },
                                {
                                    "name": "SYSCAT"
                                },
                                {
                                    "name": "HIUSER"
                                },
                                {
                                    "name": "SYSCS_DIAG"
                                },
                                {
                                    "name": "SYSCS_UTIL"
                                },
                                {
                                    "name": "SYSIBM"
                                },
                                {
                                    "name": "APP"
                                },
                                {
                                    "name": "NULLID"
                                },
                                {
                                    "name": "SYSPROC"
                                },
                                {
                                    "name": "SYS"
                                },
                                {
                                    "name": "SYSSTAT"
                                }
                            ]
                        }
                    ]
                }
            }
        ]
    },
    "dataSource": [
        {
            "id": "1",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "connId": "qxxuk",
            "dbId": "qxxuk",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm",
            "driverType": "Derby",
            "database": "HIUSER"
        },
        {
            "id": "1101",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "connId": "jbzae",
            "dbId": "jbzae",
            "classifier": "db.workflow",
            "datasourceName": "derby2",
            "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/kdum-2nmg-r8s1-ija3-kw/u6n5-16ep-0v09-ej3i-by",
            "driverType": "Derby",
            "database": "HIUSER"
        }
    ],
    "tables": {
        "dimdate": {
            "id": "4ac5d9f68b58bd7c0d179146e46795be",
            "name": "dimdate",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm/31tn-meza-6ao7-hy19-bd",
            "key": "31tn-meza-6ao7-hy19-bd",
            "alias": "dimdate",
            "uuid": "31tn-meza-6ao7-hy19-bd",
            "connId": "qxxuk",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "qxxuk",
                "dbId": "qxxuk",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "dimdate_qxxuk",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "dimdate"
        },
        "employee_details": {
            "id": "4e1fd245f4d13b77be423a43f01d80b2",
            "name": "employee_details",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm/nuuw-pef8-5igy-kq7b-75",
            "key": "nuuw-pef8-5igy-kq7b-75",
            "alias": "employee_details",
            "uuid": "nuuw-pef8-5igy-kq7b-75",
            "connId": "qxxuk",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "qxxuk",
                "dbId": "qxxuk",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "employee_details_qxxuk",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "employee_details"
        },
        "geo_cordinates": {
            "id": "be534112989b616b194bc59c2fb25a42",
            "name": "geo_cordinates",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm/6hme-7kpc-crdq-cuf9-pp",
            "key": "6hme-7kpc-crdq-cuf9-pp",
            "alias": "geo_cordinates",
            "uuid": "6hme-7kpc-crdq-cuf9-pp",
            "connId": "qxxuk",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "qxxuk",
                "dbId": "qxxuk",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "geo_cordinates_qxxuk",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "geo_cordinates"
        },
        "meeting_details": {
            "id": "9645c648a1c0dbeec1287aaf1e996db3",
            "name": "meeting_details",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm/xj50-npdl-a283-e4c4-tw",
            "key": "xj50-npdl-a283-e4c4-tw",
            "alias": "meeting_details",
            "uuid": "xj50-npdl-a283-e4c4-tw",
            "connId": "qxxuk",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "qxxuk",
                "dbId": "qxxuk",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "meeting_details_qxxuk",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "meeting_details"
        },
        "travel_details": {
            "id": "8a28627d07d04ef096d9935f12e0c7e9",
            "name": "travel_details",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm/ttpw-89l8-v0c7-v7yz-zf",
            "key": "ttpw-89l8-v0c7-v7yz-zf",
            "alias": "travel_details",
            "uuid": "ttpw-89l8-v0c7-v7yz-zf",
            "connId": "qxxuk",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "qxxuk",
                "dbId": "qxxuk",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "travel_details_qxxuk",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "travel_details"
        }
    },
    "views": [],
    "activeView": false,
    "categories": {
        "i5jo-54um-wzkv-vxkd-qb": {
            "ds": {
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
                "dataSourceType": "Managed DataSource"
            },
            "category": {
                "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                "databaseDialect": "derby",
                "name": "Derby",
                "categoryName": "RDBMS",
                "categoryType": "rdbms",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png",
                "url": "jdbc:derby:{{database}}",
                "parameters": {
                    "database": "database"
                }
            }
        },
        "num3-p23p-psyt-t3di-li": {
            "ds": {
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
                    "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                    "jdbcUrl": null,
                    "driver": "dynamicSwitch"
                },
                "dataSourceType": ""
            },
            "category": {
                "driver": "dynamicSwitch",
                "databaseDialect": "",
                "name": "DynamicSwitch",
                "categoryName": "RDBMS",
                "categoryType": "rdbms",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
            }
        },
        "ma2f-256r-bt0x-cu3y-8k": {
            "ds": {
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
                "dataSourceType": "Managed DataSource"
            },
            "category": {
                "driver": "org.postgresql.Driver",
                "databaseDialect": "postgresql",
                "name": "Postgresql",
                "categoryName": "RDBMS",
                "categoryType": "rdbms",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png",
                "url": "jdbc:postgresql://{{hostName}}:{{port}}/{{database}}",
                "parameters": {
                    "port": "5432",
                    "hostName": "localhost",
                    "database": "database"
                }
            }
        }
    },
    "activeEditorTab": "info",
    "dataSourcesAddedToMetadata": [
        {
            "id": "1",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "connId": "qxxuk",
            "dbId": "qxxuk",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "i5jo-54um-wzkv-vxkd-qb/lbbi-8w2i-h0om-qbbp-e8/oxpa-36wy-jhna-fcf8-jm",
            "driverType": "Derby",
            "database": "HIUSER"
        }
    ],
    "changeDSList": {},
    "changedTables": [],
    "changedColumns": [],
    "removedTables": [],
    "removedColumns": [],
    "removedDataSources": [],
    "duplicateColumnList": [],
    "duplicateTableList": [],
    "unsavedViews": [],
    "saveDetails": false,
    "savedTableIds": [],
    "savedColumnIds": [],
    "joins": [],
    "mode": "create",
    "allTablesKeys": [
        "6hme-7kpc-crdq-cuf9-pp",
        "xj50-npdl-a283-e4c4-tw",
        "nuuw-pef8-5igy-kq7b-75",
        "31tn-meza-6ao7-hy19-bd",
        "ttpw-89l8-v0c7-v7yz-zf",
        "f080-6ikn-bk2i-oxmj-dp",
        "xyiy-pyg7-801t-h95l-69",
        "7wm3-3qto-lw8c-grwh-gb",
        "tg10-7u77-o1gv-k2ul-bs",
        "s3pv-6mdb-o4in-vgmj-5s"
    ],
    "selectedTableKeys": [
        "31tn-meza-6ao7-hy19-bd",
        "nuuw-pef8-5igy-kq7b-75",
        "6hme-7kpc-crdq-cuf9-pp",
        "xj50-npdl-a283-e4c4-tw",
        "ttpw-89l8-v0c7-v7yz-zf"
    ],
    "metadataName": "Metadata_1",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false,
    "editViewsTempData": {},
    "inititalStateFromJest": false,
    "timeStamp": 1667394760954,
    "initialEditResponse": false,
    "editorFullView": false,
    "selectedTableOrColumnKey": {},
    "expressionObj": [],
    "securityConstants": {},
    "edit": false,
    "isAllowServiceCall": true,
    "isValidatedTableShow": false,
    "securityTableData": [],
    "addOneMoreSecurity": false,
    "viewSessionVariables": false,
    "textEditingObj": {},
    "selectedJoinNameData": {},
    "filterbyData": [
        {
            "value": "All",
            "isChecked": true
        },
        {
            "value": "Table",
            "isChecked": true
        },
        {
            "value": "Column",
            "isChecked": true
        },
        {
            "value": "Global",
            "isChecked": true
        }
    ],
    "isFirstRender": true,
    "securityFormData": {},
    "accessType": "deny",
    "entityNames": "",
    "executionType": "conditionIf",
    "expressionName": null,
    "expressionType": "",
    "isApplyDisabled": true,
    "isInfoShow": true,
    "securityKeysChecked": [],
    "hasUnsavedData": true,
    "getSecurityTableData": {
        "tables": [],
        "columns": []
    },
    "doResetFormData": false,
    "tablesMergeType": "reload"
}