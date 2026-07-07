const admin_initial_view_state = JSON.parse(
  `{
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
	"cachedReports": [
		{
			"path": "Gagan/4424 1.metadata"
		},
		{
			"path": "Gagan/Metadata_1.metadata"
		}
	],
	"cachedDataSources": {
		"dataSources": [
			{
				"baseType": "global.jdbc",
				"dataSourceProvider": "tomcat",
				"id": "1",
				"name": "SampleTravelDataDerby",
				"type": "dynamicDatasource",
				"isDatabaseMetadataCached": "partial"
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
	"userData": [
		{
			"id": 4,
			"name": "downloadManager",
			"email": "mailto:download@helicalinsight.com",
			"enabled": true,
			"organisation": "",
			"orgName": "Null",
			"roles": [
				{
					"id": 2,
					"role": "ROLE_USER"
				},
				{
					"id": 4,
					"role": "ROLE_DOWNLOAD"
				}
			],
			"profiles": [],
			"slno": "1"
		},
		{
			"id": 1,
			"name": "hiadmin",
			"email": "mailto:admin@helicalinsight.com",
			"enabled": true,
			"organisation": "",
			"orgName": "Null",
			"roles": [
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
				}
			],
			"profiles": [],
			"slno": "2"
		},
		{
			"id": 2,
			"name": "hiuser",
			"email": "mailto:user@helicalinsight.com",
			"enabled": true,
			"organisation": "",
			"orgName": "Null",
			"roles": [
				{
					"id": 2,
					"role": "ROLE_USER"
				}
			],
			"profiles": [],
			"slno": "3"
		},
		{
			"id": 3,
			"name": "hiviewer",
			"email": "mailto:viewer@helicalinsight.com",
			"enabled": true,
			"organisation": "",
			"orgName": "Null",
			"roles": [
				{
					"id": 3,
					"role": "ROLE_VIEWER"
				}
			],
			"profiles": [],
			"slno": "4"
		}
	],
	"orgData": [
		{
			"slno": 1,
			"id": 1,
			"name": "adsfasdf",
			"description": "asdfsadfdsfa"
		}
	],
	"roleData": [
		{
			"id": 102,
			"name": "ROLE_USER",
			"organisation": 1,
			"orgName": "adsfasdf",
			"slno": "1"
		},
		{
			"id": 101,
			"name": "ROLE_ADMIN",
			"organisation": 1,
			"orgName": "adsfasdf",
			"slno": "2"
		},
		{
			"id": 4,
			"name": "ROLE_DOWNLOAD",
			"organisation": "",
			"orgName": "Null",
			"slno": "3"
		},
		{
			"id": 3,
			"name": "ROLE_VIEWER",
			"organisation": "",
			"orgName": "Null",
			"slno": "4"
		},
		{
			"id": 2,
			"name": "ROLE_USER",
			"organisation": "",
			"orgName": "Null",
			"slno": "5"
		},
		{
			"id": 1,
			"name": "ROLE_ADMIN",
			"organisation": "",
			"orgName": "Null",
			"slno": "6"
		}
	],
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
		"addUser": true,
		"addRole": true,
		"addOrg": true,
		"addProfile": false,
		"editUser": false
	},
	"editUser": {
		"type": "all",
		"userId": 1
	},
	"profileId": 1,
	"osDetails": null,
	"jvmThreadDetails": null,
	"managementData": null,
	"managementStaticData": null,
	"managementAdvancedData": null,
	"schedulingList": null,
	"metadataAdministrationData": {},
	"metadataGenerationData": {},
	"metadataPreviewData": {},
	"metadataGenerationFormValues": {},
	"entityId": "",
	"showMetadataPages": {
		"metadataAdministration": true,
		"metadataGeneration": false,
		"metadataPreview": false
	}
}`
);

const mocks = {
  admin_initial_view_state,
};
export default mocks;
