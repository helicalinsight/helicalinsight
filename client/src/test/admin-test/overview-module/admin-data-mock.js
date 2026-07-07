const admin_initial_view_state = JSON.parse(
  `
    {
        "users": {
        "data": null,
        "viewPage": 0,
        "currentPage": 0,
        "viewEntries": 10,
        "pageSize": 1000,
        "fetchMore": true,
        "showMore": true,
        "form": {
        "name": "",
        "slno": ""
        }
        },
        "isFetched": {
        "diskData": true,
        "jvmData": true,
        "tempData": true,
        "cacheSize": true,
        "cachedReports": true,
        "cachedDataSources": true,
        "productData": true,
        "currentLogLevel": true,
        "reportStats": false,
        "dataSourceCount": false,
        "metadataAdministration": false,
        "metadataGeneration": false,
        "metadataPreview": false
        },
        "diskData": {
        "totalDiskSize": 92252,
        "usedSpace": 19096,
        "freeSpace": 73156
        },
        "jvmData": {
        "totalMemory": 785,
        "freeMemory": 140,
        "usedMemory": 644,
        "maxMemory": 2926,
        "unit": "MB"
        },
        "tempData": {
        "tempFileArray": [
        {
        "fileName": "Temp",
        "fileSize": 4096,
        "lastModified": 1650596464523
        }
        ]
        },
        "cacheSize": {
        "size": 8
        },
        "cachedReports": [],
        "cachedDataSources": {
        "dataSources": [
        {
        "baseType": "global.jdbc",
        "dataSourceProvider": "tomcat",
        "id": "1",
        "name": "SampleTravelDataDerby",
        "type": "dynamicDatasource",
        "isDatabaseMetadataCached": "connection"
        }
        ]
        },
        "reportStats": {},
        "dataSourceCount": {},
        "currentLevel": "DEBUG",
        "logOptions": [
        "ALL",
        "DEBUG",
        "INFO",
        "WARN",
        "ERROR",
        "FATAL",
        "OFF",
        "TRACE"
        ],
        "productData": {
        "Product Type": "Business Intelligence Framework",
        "Version": "5.0.0.21062 SNAPSHOT",
        "Build": "R20221904_GIT019 SNAPSHOT",
        "Product Name": "Helical Insight",
        "Expiration": "13/05/2022",
        "License Type": "Trial"
        },
        "reloadAppMessage": "",
        "reloadValidMessage": "",
        "reloadCacheMessage": "",
        "osTableExpand": false,
        "jvmTableExpand": false,
        "pluginsData": null,
        "userData": [],
        "orgData": [],
        "roleData": [],
        "rolesList": [
        {
        "id": 1,
        "role": "ROLE_ADMIN"
        },
        {
        "id": 2,
        "role": "ROLE_USER"
        },
        {
        "id": 3,
        "role": "ROLE_VIEWER"
        },
        {
        "id": 4,
        "role": "ROLE_DOWNLOAD"
        },
        {
        "id": 5,
        "role": "ROLE_READ"
        }
        ],
        "visibleDrawersUM": {
        "addUser": false,
        "addRole": false,
        "addOrg": false,
        "addProfile": false,
        "editUser": false
        },
        "editUser": {
        "type": "all",
        "userId": 1
        },
        "profileId": 1
        }
    `
);

const mocks = {
  admin_initial_view_state,
};
export default mocks;
