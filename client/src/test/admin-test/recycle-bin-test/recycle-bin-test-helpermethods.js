
export const recycleBinSlonoTestObj = {
    data: {rec: {slno: 5}, i: 1},
    value: {slno: 2 }
}

export const fetchDetailsMockData = {
    input: {
        "recyclebinItemRows": [
            {
                "recycleBinId": 901,
                "name": "sv_as"
            },
            {
                "recycleBinId": 902,
                "name": "sv_as_report"
            },
            {
                "recycleBinId": 903,
                "name": "test_save_as1"
            },
            {
                "recycleBinId": 904,
                "name": "test_save_as1"
            },
            {
                "recycleBinId": 905,
                "name": "test_save_as"
            },
            {
                "recycleBinId": 912,
                "name": "raj"
            },
            {
                "recycleBinId": 913,
                "name": "test123"
            },
            {
                "recycleBinId": 914,
                "name": "test2"
            },
            {
                "recycleBinId": 915,
                "name": "mko"
            },
            {
                "recycleBinId": 916,
                "name": "test5o"
            }
        ],
        "res": {
            "901": {
                "data": {}
            },
            "902": {
                "data": {}
            },
            "903": {
                "data": {}
            },
            "904": {
                "data": {}
            },
            "905": {
                "data": {}
            },
            "912": {
                "data": {}
            },
            "913": {
                "data": {
                    "users": [
                        {
                            "name": "raj",
                            "id": 902,
                            "deleted": true
                        }
                    ]
                }
            },
            "914": {
                "data": {
                    "users": [
                        {
                            "name": "test2",
                            "id": 302,
                            "deleted": false
                        }
                    ]
                }
            },
            "915": {
                "data": {}
            },
            "916": {
                "data": {}
            }
        }
    },
    output: [
            {
                "resourceName": "sv_as",
                "recycleBinId": 901,
                "key": 901,
                "associatedDetails": {
                    "resourcesData": [],
                    "dataSourcesData": [],
                    "usersData": []
                }
            },
            {
                "resourceName": "sv_as_report",
                "recycleBinId": 902,
                "key": 902,
                "associatedDetails": {
                    "resourcesData": [],
                    "dataSourcesData": [],
                    "usersData": []
                }
            },
            {
                "resourceName": "test_save_as1",
                "recycleBinId": 903,
                "key": 903,
                "associatedDetails": {
                    "resourcesData": [],
                    "dataSourcesData": [],
                    "usersData": []
                }
            },
            {
                "resourceName": "test_save_as1",
                "recycleBinId": 904,
                "key": 904,
                "associatedDetails": {
                    "resourcesData": [],
                    "dataSourcesData": [],
                    "usersData": []
                }
            },
            {
                "resourceName": "test_save_as",
                "recycleBinId": 905,
                "key": 905,
                "associatedDetails": {
                    "resourcesData": [],
                    "dataSourcesData": [],
                    "usersData": []
                }
            },
            {
                "resourceName": "raj",
                "recycleBinId": 912,
                "key": 912,
                "associatedDetails": {
                    "resourcesData": [],
                    "dataSourcesData": [],
                    "usersData": []
                }
            },
            {
                "resourceName": "test123",
                "recycleBinId": 913,
                "key": 913,
                "associatedDetails": {
                    "resourcesData": [],
                    "dataSourcesData": [],
                    "usersData": [
                        {
                            "name": "raj",
                            "id": 902,
                            "deleted": true
                        }
                    ]
                }
            },
            {
                "resourceName": "test2",
                "recycleBinId": 914,
                "key": 914,
                "associatedDetails": {
                    "resourcesData": [],
                    "dataSourcesData": [],
                    "usersData": [
                        {
                            "name": "test2",
                            "id": 302,
                            "deleted": false
                        }
                    ]
                }
            },
            {
                "resourceName": "mko",
                "recycleBinId": 915,
                "key": 915,
                "associatedDetails": {
                    "resourcesData": [],
                    "dataSourcesData": [],
                    "usersData": []
                }
            },
            {
                "resourceName": "test5o",
                "recycleBinId": 916,
                "key": 916,
                "associatedDetails": {
                    "resourcesData": [],
                    "dataSourcesData": [],
                    "usersData": []
                }
            }
        ]
}

export const ownershipMockFormdata = {
    input: {
        "recyclebinItem": {
            "slno": 50,
            "data": {
                "name": "Dashboard_080425.efwdd",
                "id": 2210,
                "path": "Sadakshya/Dashboard_080425_efwdd_00.efw"
            },
            "deletedOn": 1694694204691,
            "deletedBy": "hiadmin",
            "recycleBinId": 806,
            "type": "Files"
        },
        "selectedUser": [
            4
        ],
    },
    output: {
        "type": "Files",
        "resources": [
            2210
        ],
        "newOwnerId": 4
    }
}