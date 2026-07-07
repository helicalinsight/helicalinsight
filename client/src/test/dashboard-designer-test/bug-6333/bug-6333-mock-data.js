export const bug6333appreduxData={
    "shouldBlockNavigation": false,
    "value": true,
    "isAuthenticated": true,
    "isLogoutManually": false,
    "hasError": false,
    "showLicenseNotification": false,
    "isLicenseRendered": false,
    "applicationSettingsData": {
        "userData": {
            "serverTime": 1682337796358,
            "expiryTime": 1682339596358,
            "sessionUserName": "hiadmin",
            "sessionUserEmail": "admin@helicalinsight.com",
            "user": {
                "name": "hiadmin",
                "email": "admin@helicalinsight.com",
                "actualUserName": "hiadmin",
                "organization": "",
                "roles": [
                    "ROLE_ADMIN",
                    "ROLE_USER"
                ],
                "profile": []
            },
            "rootDirectoryPermission": "4",
            "provideHTMLExport": false,
            "enableReportSave": "true",
            "defaultEmailResourceType": "url",
            "saml": {
                "enabled": false,
                "logoutLink": "/saml/logout/?"
            },
            "baseUrl": "http://127.0.0.1:7085/hi-ee/",
            "clientTime": 1682337797131
        },
        "contentId": "Static/DashboardGlobals",
        "showExperimentalFeatures": true,
        "hideDefaultLoginButtons": false,
        "settings": {
            "jwtToken": null,
            "adminPaths": {
                "users": "admin/users",
                "profiles": "admin/profiles",
                "roles": "admin/roles",
                "organisations": "admin/organisations",
                "services": "services.html"
            },
            "services": "services",
            "optionalReportParams": {},
            "recursiveDirectoryLoad": false,
            "HDI": {
                "adhoc": {
                    "urls": {
                        "services": "/services",
                        "createDataSource": "/createDataSource",
                        "listDataSources": "/listDataSources",
                        "listLocations": "/listLocations",
                        "getResources": "/getResources",
                        "adhocReport": "/adhocReport"
                    }
                }
            },
            "DashboardGlobals": {
                "solutionLoader": "getSolutionResources",
                "resourceLoader": "/getEFWSolution.html",
                "updateService": "/executeDatasource.html",
                "chartingService": "/visualizeData.html",
                "exportData": "/exportData.html",
                "reportDownload": "/downloadReport.html",
                "productInfo": "getProductInformation.html",
                "sendMail": "/sendMail.html",
                "updateEFWTemplate": "/updateEFWTemplate.html",
                "openHcr ": "/hcr-report.html",
                "editHcr ": "/hcr-report-edit.html",
                "controllers": {
                    "efw": "/getEFWSolution.html",
                    "efwsr": "/executeSavedReport.html",
                    "efwfav": "/executeFavourite.html",
                    "report": "/hi.html"
                },
                "saveReport": "/saveReport.html",
                "fsop": "/fileSystemOperations",
                "importFile": "/importFile.html",
                "downloadEnableSavedReport": "/downloadEnableSavedReport.html",
                "scheduling": {
                    "get": "/getScheduleData.html",
                    "update": "/updateScheduleData.html"
                },
                "services": "/services",
                "designerCreate": "/designer.html",
                "designerEdit": "/designer-edit.html",
                "ceReportCreate": "/ce-report-create.html",
                "ceReportEdit": "/ce-report-edit.html",
                "adhocEdit": "/adhoc/report-edit.html",
                "datasourceCreate": "/adhoc/datasources.html",
                "metadataEdit": "/adhoc/metadata-edit.html",
                "metadataCreate": "/adhoc/metadata-create.html",
                "adhocReportCreate": "/adhoc/report-create.html",
                "helicalReportCreate": "/adhoc/helical-report.html",
                "helicalReportEdit": "/adhoc/helical-report-edit.html",
                "openAdhoc": "/hi.html",
                "openEfw": "/hi.html",
                "visualizeAdhoc": "/visualizeAdhoc.html"
            }
        }
    },
    "routes": [
        {
            "title": "Home",
            "addInNavbar": true,
            "tutorialElementKey": "hi-navbar-home",
            "url": "/admin",
            "roles": [
                "ROLE_ADMIN"
            ]
        },
        {
            "title": "HI: Login",
            "url": "/",
            "addInNavbar": false,
            "roles": [],
            "isExpFeature": false
        },
        {
            "title": "Data Sources",
            "url": "/datasource",
            "addInNavbar": true,
            "roles": [
                "ROLE_ADMIN"
            ],
            "tutorialElementKey": "hi-navbar-data-sources",
            "isExpFeature": false
        },
        {
            "title": "Meta Data",
            "url": "/metadata",
            "addInNavbar": true,
            "roles": [
                "ROLE_ADMIN"
            ],
            "tutorialElementKey": "hi-navbar-metadata",
            "isExpFeature": false
        },
        {
            "title": "Reports",
            "url": "/helical-report",
            "addInNavbar": true,
            "roles": [
                "ROLE_ADMIN",
                "ROLE_USER",
                "ROLE_DOWNLOAD"
            ],
            "tutorialElementKey": "hi-navbar-reports",
            "isExpFeature": false
        },
        {
            "title": "Dashboard Designer",
            "url": "/dashboard-designer",
            "addInNavbar": true,
            "roles": [
                "ROLE_ADMIN",
                "ROLE_USER",
                "ROLE_DOWNLOAD"
            ],
            "tutorialElementKey": "hi-navbar-dashboard-designer",
            "isExpFeature": false
        },
        {
            "title": "Report View",
            "url": "/report-viewer",
            "addInNavbar": false,
            "roles": [
                "ROLE_ADMIN",
                "ROLE_USER",
                "ROLE_DOWNLOAD",
                "ROLE_VIEWER"
            ],
            "isExpFeature": false
        },
        {
            "title": "HI",
            "url": "/hi",
            "addInNavbar": false,
            "roles": [],
            "accessbleForAll": true,
            "isExpFeature": false
        },
        {
            "expId": "cube",
            "title": "Cube",
            "url": "/cube",
            "addInNavbar": true,
            "roles": [
                "ROLE_ADMIN"
            ],
            "tutorialElementKey": "hi-navbar-cube",
            "isExpFeature": true
        },
        {
            "expId": "instant-bi",
            "title": "Instant",
            "url": "/instant-bi",
            "addInNavbar": true,
            "roles": [
                "ROLE_ADMIN",
                "ROLE_USER",
                "ROLE_DOWNLOAD"
            ],
            "tutorialElementKey": "hi-navbar-instant-bi",
            "isExpFeature": true
        },
        {
            "expId": "hice",
            "title": "Community Report",
            "url": "/hice",
            "addInNavbar": true,
            "roles": [
                "ROLE_ADMIN",
                "ROLE_USER",
                "ROLE_DOWNLOAD"
            ],
            "tutorialElementKey": "hi-navbar-community-report",
            "isExpFeature": true
        },
        {
            "expId": "hwf",
            "title": "Workflow",
            "url": "/hwf",
            "addInNavbar": true,
            "roles": [
                "ROLE_ADMIN",
                "ROLE_USER",
                "ROLE_DOWNLOAD"
            ],
            "tutorialElementKey": "hi-navbar-workflow",
            "isExpFeature": true
        }
    ],
    "activeRoute": "/admin/overview",
    "isApplicationSettingsServiceCheck": true,
    "nxtRoute": "",
    "toggleSidebar": false,
    "showNavbar": true,
    "logType": "",
    "isUrlAuthenticating": false,
    "isSessionOver": false,
    "viewModeInfo": null,
    "editModeInfo": null,
    "aboutToChangeRoute": null,
    "viewerEmailModalVisible": false,
    "viewerScheduleModalVisible": false,
    "keysPressed": [],
    "currentSCLocation": ""
}
export const bug6333adminReduxData={
    "isAdminTabsDataSet": true,
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
        "usedSpace": 71546,
        "freeSpace": 20706
    },
    "jvmData": {
        "totalMemory": 793,
        "freeMemory": 332,
        "usedMemory": 460,
        "maxMemory": 2926,
        "unit": "MB"
    },
    "tempData": {
        "tempFileArray": [
            {
                "fileName": "01a3fc94-c1ad-4c0c-bfc2-295a3a450b0b.xml",
                "fileSize": 418,
                "lastModified": 1681715334976
            },
            {
                "fileName": "0239b629-69c1-46cb-82e1-0fdb75a4ca66.xml",
                "fileSize": 2148,
                "lastModified": 1681713948870
            },
            {
                "fileName": "0244a440-39c3-445f-8b5a-71c69dad603d.xml",
                "fileSize": 2148,
                "lastModified": 1681794448887
            },
            {
                "fileName": "0253ab1e-d55e-49e1-96a6-e1cb1d6082aa.xml",
                "fileSize": 371,
                "lastModified": 1681715299033
            },
            {
                "fileName": "03807757-54d9-4a61-9ce2-2f6f1ef59d8e.xml",
                "fileSize": 525,
                "lastModified": 1681882418464
            },
            {
                "fileName": "03ca72f9-95ce-4990-8ca0-0b1fe3c129a5.xml",
                "fileSize": 344,
                "lastModified": 1682057860173
            },
            {
                "fileName": "043c021a-294b-4e4f-ae0a-9d554ee5e654.xml",
                "fileSize": 439,
                "lastModified": 1681814808569
            },
            {
                "fileName": "045792f8-0a09-4766-909e-e7405873359f.xml",
                "fileSize": 354,
                "lastModified": 1681713931832
            },
            {
                "fileName": "045cf80d-94d2-4f46-acb8-dbd515a77f28.xml",
                "fileSize": 374,
                "lastModified": 1682057802116
            },
            {
                "fileName": "0565d25e-b973-40b0-bebc-1b33039f49ce.xml",
                "fileSize": 1706,
                "lastModified": 1682057773225
            },
            {
                "fileName": "05ba564b-9b35-4423-b991-67cdee65571d.xml",
                "fileSize": 1706,
                "lastModified": 1681912827393
            },
            {
                "fileName": "0651d082-fcde-4c5c-b44e-d80338763c3a.xml",
                "fileSize": 352,
                "lastModified": 1681713931516
            },
            {
                "fileName": "0657d701-2afd-44f8-8441-7f2b1f659c82.xml",
                "fileSize": 409,
                "lastModified": 1681400361717
            },
            {
                "fileName": "06925fc7-462c-4c98-98f2-b122b7bee081.xml",
                "fileSize": 355,
                "lastModified": 1681708460140
            },
            {
                "fileName": "06a8408a-c8ca-41ae-b9eb-fa687f08a6e1.xml",
                "fileSize": 371,
                "lastModified": 1681882380381
            },
            {
                "fileName": "080c5755-fda2-41fe-b5dd-1de6092d82b7.xml",
                "fileSize": 357,
                "lastModified": 1681708460484
            },
            {
                "fileName": "0855d103-9dd7-420a-bd5f-a2a3745f58fb.xml",
                "fileSize": 366,
                "lastModified": 1681715274787
            },
            {
                "fileName": "097f4691-56a3-41ba-90d5-b686c53a5ba8.xml",
                "fileSize": 391,
                "lastModified": 1681966522128
            },
            {
                "fileName": "09ddd565-c452-4f88-9955-370da66f56cf.xml",
                "fileSize": 418,
                "lastModified": 1681708513997
            },
            {
                "fileName": "0a21ee4c-4604-4b53-9789-fd517550b1b4.xml",
                "fileSize": 364,
                "lastModified": 1681713930120
            },
            {
                "fileName": "0ac6541a-09e6-43f3-9e18-e3e2f0f6c2e3.xml",
                "fileSize": 370,
                "lastModified": 1681882350830
            },
            {
                "fileName": "0ba4c62e-8356-428d-8d12-435c10522bbb.xml",
                "fileSize": 364,
                "lastModified": 1681708459096
            },
            {
                "fileName": "0bc3ff89-228b-467a-a820-f96bf56fc4e5.xml",
                "fileSize": 1980,
                "lastModified": 1681966549066
            },
            {
                "fileName": "0c65b6f0-280a-4e1b-8d21-26a83f5862db.xml",
                "fileSize": 1706,
                "lastModified": 1681882350746
            },
            {
                "fileName": "0cd6945e-e121-4e1b-b252-4636ccebaa3a.xml",
                "fileSize": 409,
                "lastModified": 1681708449679
            },
            {
                "fileName": "0d939f30-89d7-42c5-a359-c41dd10841df.xml",
                "fileSize": 432,
                "lastModified": 1681794408735
            },
            {
                "fileName": "0db64338-9952-4029-8c6a-f431e7dbc807.xml",
                "fileSize": 368,
                "lastModified": 1681882379297
            },
            {
                "fileName": "0e38d647-b184-45c4-8ebb-d3f545aa1fdd.xml",
                "fileSize": 1706,
                "lastModified": 1681794359854
            },
            {
                "fileName": "0f0f7c68-7729-465b-8703-c85a4db6e2c2.xml",
                "fileSize": 1706,
                "lastModified": 1681899613982
            },
            {
                "fileName": "0f3fcd6a-c4eb-4fd8-9dd3-291498326480.xml",
                "fileSize": 368,
                "lastModified": 1681400369181
            },
            {
                "fileName": "0fa2da2a-ed02-44f1-948f-19342436d73c.xml",
                "fileSize": 1980,
                "lastModified": 1681794446706
            },
            {
                "fileName": "10512404-0e32-4e3d-a504-3531c5db9948.xml",
                "fileSize": 341,
                "lastModified": 1681715290808
            },
            {
                "fileName": "109e0202-1efb-4bf3-a338-c270842b6cf6.xml",
                "fileSize": 1706,
                "lastModified": 1681966438468
            },
            {
                "fileName": "109e899a-c137-4653-8f53-bb6b9dc09dd1.xml",
                "fileSize": 432,
                "lastModified": 1681882354999
            },
            {
                "fileName": "11e3cf9b-d764-47f0-9c89-24eb1553d276.xml",
                "fileSize": 374,
                "lastModified": 1681966534401
            },
            {
                "fileName": "134cf383-d536-482c-8256-cc8e9ada715a.xml",
                "fileSize": 344,
                "lastModified": 1681713976892
            },
            {
                "fileName": "13761bda-a996-46af-84c0-61bd7cafac4d.xml",
                "fileSize": 366,
                "lastModified": 1681966534217
            },
            {
                "fileName": "138c5969-3a02-42c4-92aa-8e2413fffbcf.xml",
                "fileSize": 1706,
                "lastModified": 1681897687878
            },
            {
                "fileName": "14284242-6755-43dc-aa7c-ebf12d09c76a.xml",
                "fileSize": 1706,
                "lastModified": 1681715292068
            },
            {
                "fileName": "143ca1af-cd0b-4a65-8e0c-655c5735cd7f.xml",
                "fileSize": 418,
                "lastModified": 1681713974364
            },
            {
                "fileName": "14e1fa74-701f-46cf-b796-7e5f9cea56dd.xml",
                "fileSize": 1706,
                "lastModified": 1681708431581
            },
            {
                "fileName": "155ffbb0-dd29-4ba9-b721-2822783744a1.xml",
                "fileSize": 731,
                "lastModified": 1681713951986
            },
            {
                "fileName": "15b5c3b2-338f-4bcc-a306-7e2713398a0e.xml",
                "fileSize": 2148,
                "lastModified": 1681966551375
            },
            {
                "fileName": "15d68b0c-ee17-47ba-a734-387168707d57.xml",
                "fileSize": 1706,
                "lastModified": 1681715226090
            },
            {
                "fileName": "1681277333281",
                "fileSize": 4096,
                "lastModified": 1681470383560
            },
            {
                "fileName": "Mahesh",
                "fileSize": 4096,
                "lastModified": 1681470383560
            },
            {
                "fileName": "resources",
                "fileSize": 4096,
                "lastModified": 1681470383560
            },
            {
                "fileName": "1681277341254",
                "fileSize": 4096,
                "lastModified": 1681470383560
            },
            {
                "fileName": "Mahesh",
                "fileSize": 4096,
                "lastModified": 1681470383560
            },
            {
                "fileName": "resources",
                "fileSize": 4096,
                "lastModified": 1681470383564
            },
            {
                "fileName": "1681277348856",
                "fileSize": 4096,
                "lastModified": 1681470383564
            },
            {
                "fileName": "Mahesh",
                "fileSize": 4096,
                "lastModified": 1681470383564
            },
            {
                "fileName": "resources",
                "fileSize": 4096,
                "lastModified": 1681470383564
            },
            {
                "fileName": "1681277469750",
                "fileSize": 4096,
                "lastModified": 1681470383564
            },
            {
                "fileName": "CubeExport",
                "fileSize": 4096,
                "lastModified": 1681470383564
            },
            {
                "fileName": "resources",
                "fileSize": 4096,
                "lastModified": 1681470383564
            },
            {
                "fileName": "1681277487624",
                "fileSize": 4096,
                "lastModified": 1681470383564
            },
            {
                "fileName": "CubeExport",
                "fileSize": 4096,
                "lastModified": 1681470383564
            },
            {
                "fileName": "resources",
                "fileSize": 4096,
                "lastModified": 1681470383568
            },
            {
                "fileName": "1681448279057",
                "fileSize": 4096,
                "lastModified": 1681448279056
            },
            {
                "fileName": "EmptyFolder",
                "fileSize": 4096,
                "lastModified": 1681448279056
            },
            {
                "fileName": "EmptyFolder.zip",
                "fileSize": 1223,
                "lastModified": 1681448279056
            },
            {
                "fileName": "Manifest.json",
                "fileSize": 165,
                "lastModified": 1681448279056
            },
            {
                "fileName": "cache",
                "fileSize": 4096,
                "lastModified": 1681448279056
            },
            {
                "fileName": "dw",
                "fileSize": 4096,
                "lastModified": 1681448279056
            },
            {
                "fileName": "resources",
                "fileSize": 4096,
                "lastModified": 1681448279056
            },
            {
                "fileName": "EmptyFolder.efwfolder",
                "fileSize": 131,
                "lastModified": 1681448279056
            },
            {
                "fileName": "1681712663030",
                "fileSize": 4096,
                "lastModified": 1681712663028
            },
            {
                "fileName": "Empty_folder",
                "fileSize": 4096,
                "lastModified": 1681712663028
            },
            {
                "fileName": "Empty_folder.zip",
                "fileSize": 1687,
                "lastModified": 1681712663028
            },
            {
                "fileName": "Manifest.json",
                "fileSize": 270,
                "lastModified": 1681712663032
            },
            {
                "fileName": "cache",
                "fileSize": 4096,
                "lastModified": 1681712663028
            },
            {
                "fileName": "dw",
                "fileSize": 4096,
                "lastModified": 1681712663028
            },
            {
                "fileName": "resources",
                "fileSize": 4096,
                "lastModified": 1681712663028
            },
            {
                "fileName": "Empty_folder.efwfolder",
                "fileSize": 132,
                "lastModified": 1681712663032
            },
            {
                "fileName": "Nested_Folder.efwfolder",
                "fileSize": 133,
                "lastModified": 1681712663032
            },
            {
                "fileName": "1681712681079",
                "fileSize": 4096,
                "lastModified": 1681712681078
            },
            {
                "fileName": "Empty_folder",
                "fileSize": 4096,
                "lastModified": 1681712681082
            },
            {
                "fileName": "Empty_folder.zip",
                "fileSize": 1687,
                "lastModified": 1681712681078
            },
            {
                "fileName": "Manifest.json",
                "fileSize": 270,
                "lastModified": 1681712681082
            },
            {
                "fileName": "cache",
                "fileSize": 4096,
                "lastModified": 1681712681078
            },
            {
                "fileName": "dw",
                "fileSize": 4096,
                "lastModified": 1681712681078
            },
            {
                "fileName": "resources",
                "fileSize": 4096,
                "lastModified": 1681712681082
            },
            {
                "fileName": "Empty_folder.efwfolder",
                "fileSize": 132,
                "lastModified": 1681712681082
            },
            {
                "fileName": "Nested_Folder.efwfolder",
                "fileSize": 133,
                "lastModified": 1681712681082
            },
            {
                "fileName": "1681712927573",
                "fileSize": 4096,
                "lastModified": 1681712927572
            },
            {
                "fileName": "Empty_folder",
                "fileSize": 4096,
                "lastModified": 1681712927572
            },
            {
                "fileName": "Empty_folder.zip",
                "fileSize": 1687,
                "lastModified": 1681712927572
            },
            {
                "fileName": "Manifest.json",
                "fileSize": 270,
                "lastModified": 1681712927576
            },
            {
                "fileName": "cache",
                "fileSize": 4096,
                "lastModified": 1681712927572
            },
            {
                "fileName": "dw",
                "fileSize": 4096,
                "lastModified": 1681712927572
            },
            {
                "fileName": "resources",
                "fileSize": 4096,
                "lastModified": 1681712927572
            },
            {
                "fileName": "Empty_folder.efwfolder",
                "fileSize": 132,
                "lastModified": 1681712927576
            },
            {
                "fileName": "Nested_Folder.efwfolder",
                "fileSize": 133,
                "lastModified": 1681712927576
            },
            {
                "fileName": "1681714099957",
                "fileSize": 4096,
                "lastModified": 1681714099955
            },
            {
                "fileName": "Empty_Folder",
                "fileSize": 4096,
                "lastModified": 1681714099955
            },
            {
                "fileName": "Empty_Folder.zip",
                "fileSize": 22235,
                "lastModified": 1681714099955
            },
            {
                "fileName": "Manifest.json",
                "fileSize": 775,
                "lastModified": 1681714099959
            },
            {
                "fileName": "cache",
                "fileSize": 4096,
                "lastModified": 1681714099955
            },
            {
                "fileName": "dw",
                "fileSize": 4096,
                "lastModified": 1681714099955
            },
            {
                "fileName": "resources",
                "fileSize": 4096,
                "lastModified": 1681714099955
            },
            {
                "fileName": "Empty_Folder.efwfolder",
                "fileSize": 132,
                "lastModified": 1681714099959
            },
            {
                "fileName": "HReport.hr",
                "fileSize": 5171,
                "lastModified": 1681714099959
            },
            {
                "fileName": "Metadata.efwfolder",
                "fileSize": 128,
                "lastModified": 1681714099959
            },
            {
                "fileName": "Parent_Folder.efwfolder",
                "fileSize": 133,
                "lastModified": 1681714099963
            },
            {
                "fileName": "SampletravelDerbyMD.metadata",
                "fileSize": 10851,
                "lastModified": 1681714099963
            },
            {
                "fileName": "SampletravelDerbyMD.metadata_datasource",
                "fileSize": 2860,
                "lastModified": 1681714099959
            },
            {
                "fileName": "1681720214766",
                "fileSize": 4096,
                "lastModified": 1681720214763
            },
            {
                "fileName": "Nested",
                "fileSize": 4096,
                "lastModified": 1681720214767
            },
            {
                "fileName": "Nested.zip",
                "fileSize": 1917,
                "lastModified": 1681720214763
            },
            {
                "fileName": "Manifest.json",
                "fileSize": 305,
                "lastModified": 1681720214767
            },
            {
                "fileName": "cache",
                "fileSize": 4096,
                "lastModified": 1681720214763
            },
            {
                "fileName": "dw",
                "fileSize": 4096,
                "lastModified": 1681720214763
            },
            {
                "fileName": "resources",
                "fileSize": 4096,
                "lastModified": 1681720214767
            },
            {
                "fileName": "Level1.efwfolder",
                "fileSize": 126,
                "lastModified": 1681720214767
            },
            {
                "fileName": "Level2.efwfolder",
                "fileSize": 126,
                "lastModified": 1681720214767
            },
            {
                "fileName": "Nested.efwfolder",
                "fileSize": 126,
                "lastModified": 1681720214767
            },
            {
                "fileName": "1681888638852",
                "fileSize": 4096,
                "lastModified": 1681888638846
            },
            {
                "fileName": "export",
                "fileSize": 4096,
                "lastModified": 1681888638850
            },
            {
                "fileName": "Manifest.json",
                "fileSize": 448,
                "lastModified": 1681888638850
            },
            {
                "fileName": "cache",
                "fileSize": 4096,
                "lastModified": 1681888638846
            },
            {
                "fileName": "dw",
                "fileSize": 4096,
                "lastModified": 1681888638846
            },
            {
                "fileName": "resources",
                "fileSize": 4096,
                "lastModified": 1681888638850
            },
            {
                "fileName": "Metadata_1.metadata",
                "fileSize": 10809,
                "lastModified": 1681888638854
            },
            {
                "fileName": "Metadata_1.metadata_datasource",
                "fileSize": 2047,
                "lastModified": 1681888638850
            },
            {
                "fileName": "import_export.efwfolder",
                "fileSize": 133,
                "lastModified": 1681888638850
            },
            {
                "fileName": "table.hr",
                "fileSize": 3807,
                "lastModified": 1681888638854
            },
            {
                "fileName": "import_export.zip",
                "fileSize": 18810,
                "lastModified": 1681888638846
            },
            {
                "fileName": "1681888643455",
                "fileSize": 4096,
                "lastModified": 1681888643451
            },
            {
                "fileName": "export",
                "fileSize": 4096,
                "lastModified": 1681888643455
            },
            {
                "fileName": "Manifest.json",
                "fileSize": 448,
                "lastModified": 1681888643455
            },
            {
                "fileName": "cache",
                "fileSize": 4096,
                "lastModified": 1681888643451
            },
            {
                "fileName": "dw",
                "fileSize": 4096,
                "lastModified": 1681888643451
            },
            {
                "fileName": "resources",
                "fileSize": 4096,
                "lastModified": 1681888643455
            },
            {
                "fileName": "Metadata_1.metadata",
                "fileSize": 10809,
                "lastModified": 1681888643459
            },
            {
                "fileName": "Metadata_1.metadata_datasource",
                "fileSize": 2047,
                "lastModified": 1681888643455
            },
            {
                "fileName": "import_export.efwfolder",
                "fileSize": 133,
                "lastModified": 1681888643455
            },
            {
                "fileName": "table.hr",
                "fileSize": 3807,
                "lastModified": 1681888643459
            },
            {
                "fileName": "import_export.zip",
                "fileSize": 18810,
                "lastModified": 1681888643451
            },
            {
                "fileName": "1681888650133",
                "fileSize": 4096,
                "lastModified": 1681888650131
            },
            {
                "fileName": "export",
                "fileSize": 4096,
                "lastModified": 1681888650131
            },
            {
                "fileName": "Manifest.json",
                "fileSize": 448,
                "lastModified": 1681888650135
            },
            {
                "fileName": "cache",
                "fileSize": 4096,
                "lastModified": 1681888650131
            },
            {
                "fileName": "dw",
                "fileSize": 4096,
                "lastModified": 1681888650131
            },
            {
                "fileName": "resources",
                "fileSize": 4096,
                "lastModified": 1681888650131
            },
            {
                "fileName": "Metadata_1.metadata",
                "fileSize": 10809,
                "lastModified": 1681888650135
            },
            {
                "fileName": "Metadata_1.metadata_datasource",
                "fileSize": 2047,
                "lastModified": 1681888650135
            },
            {
                "fileName": "import_export.efwfolder",
                "fileSize": 133,
                "lastModified": 1681888650135
            },
            {
                "fileName": "table.hr",
                "fileSize": 3807,
                "lastModified": 1681888650139
            },
            {
                "fileName": "import_export.zip",
                "fileSize": 18810,
                "lastModified": 1681888650131
            },
            {
                "fileName": "17ffe034-5fc9-4729-884e-79382d0dd20b.xml",
                "fileSize": 731,
                "lastModified": 1681794451307
            },
            {
                "fileName": "185e807a-89e2-4f99-a743-e0aa44a9e714.xml",
                "fileSize": 519,
                "lastModified": 1681882354183
            },
            {
                "fileName": "191c59e6-2bc7-4562-bb8b-915a404cf1d3.xml",
                "fileSize": 429,
                "lastModified": 1681794359914
            },
            {
                "fileName": "19d40f7e-3a76-4c08-80a2-1df9616b243d.xml",
                "fileSize": 352,
                "lastModified": 1681708460300
            },
            {
                "fileName": "1b5d8ff4-b775-422d-b3ee-395d01eec528.xml",
                "fileSize": 357,
                "lastModified": 1681400371290
            },
            {
                "fileName": "1cb4044d-b432-4941-80a3-486d3ef1ca51.xml",
                "fileSize": 525,
                "lastModified": 1681708515513
            },
            {
                "fileName": "1eae628c-593a-4ccf-9193-6cc4fbf85863.xml",
                "fileSize": 1706,
                "lastModified": 1681882378749
            },
            {
                "fileName": "204354c8-be58-48df-99aa-37308db49e5e.xml",
                "fileSize": 344,
                "lastModified": 1681966572240
            },
            {
                "fileName": "20748df5-081d-4f3a-accd-fec01b56bad6.xml",
                "fileSize": 366,
                "lastModified": 1681713900413
            },
            {
                "fileName": "207be854-00ff-4b79-abbd-aad3306cf56f.xml",
                "fileSize": 344,
                "lastModified": 1681794469188
            },
            {
                "fileName": "2535c142-86a1-4c61-af13-d41d9c27dbe8.xml",
                "fileSize": 519,
                "lastModified": 1682057776197
            },
            {
                "fileName": "255ef7da-8530-4d7b-b545-5a504050e507.xml",
                "fileSize": 368,
                "lastModified": 1681715297989
            },
            {
                "fileName": "25b4fbd5-34a8-4315-94f0-07815f62f480.xml",
                "fileSize": 1706,
                "lastModified": 1681735889268
            },
            {
                "fileName": "27af27d6-15be-4627-b2fd-5ff0d5eeb096.xml",
                "fileSize": 370,
                "lastModified": 1681713896941
            },
            {
                "fileName": "282be815-36b9-4aa6-8a8f-3620869f14f2.xml",
                "fileSize": 1706,
                "lastModified": 1681735989217
            },
            {
                "fileName": "28f3cc9e-4d30-4450-8f49-fe663a3bbff0.xml",
                "fileSize": 1706,
                "lastModified": 1681737798241
            },
            {
                "fileName": "29e9e9aa-1177-48dc-a5e6-184eba5ea761.xml",
                "fileSize": 435,
                "lastModified": 1681816133678
            },
            {
                "fileName": "29f4759d-00ab-46af-b366-c2072295fb5d.xml",
                "fileSize": 370,
                "lastModified": 1681715272062
            },
            {
                "fileName": "2a332ee6-6495-459d-9c5c-34e410f0812d.xml",
                "fileSize": 1706,
                "lastModified": 1681708457403
            },
            {
                "fileName": "2a596fd3-3d5e-41ad-a0d0-4c36e72bad75.xml",
                "fileSize": 374,
                "lastModified": 1681882379761
            },
            {
                "fileName": "2a85ba38-b0c1-4cd2-a2f5-80f31c5ded65.xml",
                "fileSize": 2291,
                "lastModified": 1681882354191
            },
            {
                "fileName": "2c382dd2-5a4f-4c78-8773-44edf955bb2e.xml",
                "fileSize": 1706,
                "lastModified": 1681911236232
            },
            {
                "fileName": "2c438a34-e320-4145-bc9e-ff47bea0a2f5.xml",
                "fileSize": 1706,
                "lastModified": 1681400343971
            },
            {
                "fileName": "2c7b699a-94d4-4ead-8041-2b42cb5a3f5a.xml",
                "fileSize": 1980,
                "lastModified": 1681400382959
            },
            {
                "fileName": "2c8f92f2-1706-4043-8c8e-bcf85d12d572.xml",
                "fileSize": 335,
                "lastModified": 1682057820781
            },
            {
                "fileName": "2dd963a9-58d7-4c8a-92e1-ad08e623a99a.xml",
                "fileSize": 731,
                "lastModified": 1682057821073
            },
            {
                "fileName": "2f258caa-977e-4774-8515-cd87ac546cf7.xml",
                "fileSize": 391,
                "lastModified": 1681400358729
            },
            {
                "fileName": "2f81fa92-536e-4581-830d-d9c3cd2571ed.xml",
                "fileSize": 380,
                "lastModified": 1681882401815
            },
            {
                "fileName": "302681f5-3eb4-4047-8113-2731aeeac390.xml",
                "fileSize": 1706,
                "lastModified": 1681713928168
            },
            {
                "fileName": "30cb8c91-e033-405d-ade0-8907aeeba7e0.xml",
                "fileSize": 352,
                "lastModified": 1682057803628
            },
            {
                "fileName": "31409f55-8bdd-473e-9f4c-31a51a2bd995.xml",
                "fileSize": 380,
                "lastModified": 1681400391163
            },
            {
                "fileName": "317ec037-cb04-428c-9b6b-72c8132400b3.xml",
                "fileSize": 429,
                "lastModified": 1681400278201
            },
            {
                "fileName": "318ac346-b205-4787-b087-0bb9331eb741.xml",
                "fileSize": 1980,
                "lastModified": 1681912114852
            },
            {
                "fileName": "32a9b15b-b077-496e-8eb1-1b305b454ced.xml",
                "fileSize": 366,
                "lastModified": 1681794409055
            },
            {
                "fileName": "33493d45-30c9-4e84-93cc-18bb78c58560.xml",
                "fileSize": 1706,
                "lastModified": 1681905030560
            },
            {
                "fileName": "344a3b6d-cbc4-4202-95ac-29ce4735c755.xml",
                "fileSize": 341,
                "lastModified": 1681966525680
            },
            {
                "fileName": "3557941f-de76-4c99-936a-fe9def877332.xml",
                "fileSize": 1706,
                "lastModified": 1681732721976
            },
            {
                "fileName": "3640dbfc-d400-460c-b849-57254738d5b9.xml",
                "fileSize": 1706,
                "lastModified": 1681819059028
            },
            {
                "fileName": "3746ce1c-a0cf-46a4-8eb0-d592af395d1b.xml",
                "fileSize": 519,
                "lastModified": 1681726939681
            },
            {
                "fileName": "385ab33f-89a0-46da-b27e-cebfd8704f0b.xml",
                "fileSize": 374,
                "lastModified": 1681912715443
            },
            {
                "fileName": "397e1c32-2dff-4348-98ce-6915f153276a.xml",
                "fileSize": 355,
                "lastModified": 1681966535877
            },
            {
                "fileName": "397ea617-5af2-4c2f-a113-d802b058c5dc.xml",
                "fileSize": 409,
                "lastModified": 1681715290260
            },
            {
                "fileName": "39c0a2d3-3b67-438a-a2cd-09ca13fd3dae.xml",
                "fileSize": 375,
                "lastModified": 1681966522076
            },
            {
                "fileName": "3a7936f7-3f8e-45af-bb72-4a8badaced7e.xml",
                "fileSize": 870,
                "lastModified": 1681715336072
            },
            {
                "fileName": "3af7e6ef-6079-4466-9a2c-dbfa5188e328.xml",
                "fileSize": 439,
                "lastModified": 1681815465319
            },
            {
                "fileName": "3b4836ed-b154-4529-8566-2c15a1c30597.xml",
                "fileSize": 409,
                "lastModified": 1681966525204
            },
            {
                "fileName": "3bb459e7-929f-4678-b926-e446fea8712c.xml",
                "fileSize": 355,
                "lastModified": 1681713931336
            },
            {
                "fileName": "3c5dd6bc-f944-4b7f-bc8c-62da858f3792.xml",
                "fileSize": 1706,
                "lastModified": 1681400368717
            },
            {
                "fileName": "3db87139-d6fc-43e1-a1ed-dda6a98a882c.xml",
                "fileSize": 2148,
                "lastModified": 1682057818581
            },
            {
                "fileName": "3e3cd45d-06f1-46b6-b1ca-36109bec8120.xml",
                "fileSize": 525,
                "lastModified": 1681713977156
            },
            {
                "fileName": "3ef7149f-565b-4d70-b56c-f5c11c760194.xml",
                "fileSize": 435,
                "lastModified": 1681818004522
            },
            {
                "fileName": "3f24fa8b-d5b4-4112-993e-65006d1d9dbb.xml",
                "fileSize": 366,
                "lastModified": 1682057801864
            },
            {
                "fileName": "3fd1430c-80fd-4996-abaf-1efc3a140e6a.xml",
                "fileSize": 1706,
                "lastModified": 1681733763402
            },
            {
                "fileName": "4010bb47-1d75-4635-9f66-88d47423479d.xml",
                "fileSize": 364,
                "lastModified": 1682057802412
            },
            {
                "fileName": "403611b4-c996-4f21-a628-dbf576948392.xml",
                "fileSize": 366,
                "lastModified": 1681708434453
            },
            {
                "fileName": "403cd380-53fc-42ba-8c64-8c41712b6c0f.xml",
                "fileSize": 870,
                "lastModified": 1681708514773
            },
            {
                "fileName": "407ffd6a-8817-49d5-8730-d224cf4531bd.xml",
                "fileSize": 371,
                "lastModified": 1681794433909
            },
            {
                "fileName": "40b9a8d9-fdc0-4df7-b64b-3c53de5c2aa7.xml",
                "fileSize": 870,
                "lastModified": 1681794468568
            },
            {
                "fileName": "4119ec7a-4373-4db9-81f6-3662ef572452.xml",
                "fileSize": 1706,
                "lastModified": 1681882378401
            },
            {
                "fileName": "42a6c186-4955-439e-baff-58c1a55e9cd6.xml",
                "fileSize": 418,
                "lastModified": 1681794467212
            },
            {
                "fileName": "434c7ac1-7aeb-46d3-a838-eda170f554dc.xml",
                "fileSize": 368,
                "lastModified": 1681966533913
            },
            {
                "fileName": "4392f9e9-ca17-4586-8fa2-33cdf8742f4f.xml",
                "fileSize": 368,
                "lastModified": 1681708458356
            },
            {
                "fileName": "44430ae8-1404-427a-9c5c-eb00d38a415f.xml",
                "fileSize": 1706,
                "lastModified": 1681713921683
            },
            {
                "fileName": "454f0b9e-1d8a-46bc-bd0e-38d4d9dbddb2.xml",
                "fileSize": 622,
                "lastModified": 1681717306368
            },
            {
                "fileName": "46ff1727-e02e-4cea-8b9e-ccef40c79ccd.xml",
                "fileSize": 870,
                "lastModified": 1681713976172
            },
            {
                "fileName": "4732eaeb-fcd3-491b-aea9-7ca647b0cccd.xml",
                "fileSize": 1706,
                "lastModified": 1682057801076
            },
            {
                "fileName": "4a639c4f-6165-48ab-b218-759c2da66e65.xml",
                "fileSize": 370,
                "lastModified": 1682057773329
            },
            {
                "fileName": "4a96a825-c83f-45ad-a5b5-997baf0c348a.xml",
                "fileSize": 731,
                "lastModified": 1681882399115
            },
            {
                "fileName": "4ac95c23-3313-4fcc-80e3-d511295c64c2.xml",
                "fileSize": 409,
                "lastModified": 1681794424216
            },
            {
                "fileName": "4c094204-97f4-4937-a17b-f0785ea6d7cc.xml",
                "fileSize": 366,
                "lastModified": 1681794433261
            },
            {
                "fileName": "5018fbf7-3001-41eb-a365-ec6e4172202a.xml",
                "fileSize": 375,
                "lastModified": 1681794421212
            },
            {
                "fileName": "52efe608-4b97-440b-a4eb-3b07ff242ec2.xml",
                "fileSize": 435,
                "lastModified": 1681816185263
            },
            {
                "fileName": "534ee65c-f920-4c88-9ba7-feec51278c82.xml",
                "fileSize": 335,
                "lastModified": 1681966553547
            },
            {
                "fileName": "545698e8-b813-46b8-9708-929de536939b.xml",
                "fileSize": 375,
                "lastModified": 1682057790091
            },
            {
                "fileName": "549f1d2d-cf35-4bf4-be99-26f7baca470f.xml",
                "fileSize": 375,
                "lastModified": 1681708446791
            },
            {
                "fileName": "565f3d01-11ee-4597-94d4-e05115467fcb.xml",
                "fileSize": 1706,
                "lastModified": 1681794426101
            },
            {
                "fileName": "569ecfcc-00a7-4d3b-95eb-947390986330.xml",
                "fileSize": 1980,
                "lastModified": 1681708472345
            },
            {
                "fileName": "5743475e-5002-44f6-9552-6f545c11b070.xml",
                "fileSize": 1706,
                "lastModified": 1681907729520
            },
            {
                "fileName": "575b1564-c028-4f41-ad0c-6eea1c095371.xml",
                "fileSize": 418,
                "lastModified": 1682057858357
            },
            {
                "fileName": "58f996c7-584d-4638-9a12-56ccb433b11e.xml",
                "fileSize": 366,
                "lastModified": 1681966509503
            },
            {
                "fileName": "595d8716-a813-4b1b-98ad-c3b31738836c.xml",
                "fileSize": 1706,
                "lastModified": 1681735713500
            },
            {
                "fileName": "59d1db14-e1eb-41ea-aa2a-f816d4290d97.xml",
                "fileSize": 354,
                "lastModified": 1681715300233
            },
            {
                "fileName": "5af48edd-ffb3-4627-aca2-dea43c607445.xml",
                "fileSize": 903,
                "lastModified": 1681900319466
            },
            {
                "fileName": "5b70f801-91c1-49ab-b805-942fadac54c6.xml",
                "fileSize": 525,
                "lastModified": 1681966572445
            },
            {
                "fileName": "5d98cc06-41b9-4fcb-932f-5d4040831908.xml",
                "fileSize": 1706,
                "lastModified": 1681400368421
            },
            {
                "fileName": "5dba0c67-d490-4f16-ad3d-6fcb3526e322.xml",
                "fileSize": 519,
                "lastModified": 1681966508335
            },
            {
                "fileName": "5decf72c-29fe-433f-b68e-85dc1d85a825.xml",
                "fileSize": 2291,
                "lastModified": 1682057776205
            },
            {
                "fileName": "5ed2f9bf-b510-47b0-ae8d-1037200a8897.xml",
                "fileSize": 335,
                "lastModified": 1681882398839
            },
            {
                "fileName": "605036d2-2760-4b93-90cf-866200bd8201.xml",
                "fileSize": 354,
                "lastModified": 1682057803928
            },
            {
                "fileName": "61c21717-f3f3-4b84-b035-bdfbe5bfe59d.xml",
                "fileSize": 1706,
                "lastModified": 1681715271926
            },
            {
                "fileName": "634a0760-b671-46fe-a7c0-19598da1aede.xml",
                "fileSize": 366,
                "lastModified": 1681400369481
            },
            {
                "fileName": "65cfd3ba-7724-40ad-bb61-f66e69ebbb43.xml",
                "fileSize": 418,
                "lastModified": 1681966570616
            },
            {
                "fileName": "66d1bb3e-d728-4980-b629-ec2c130c3715.xml",
                "fileSize": 1980,
                "lastModified": 1681882394002
            },
            {
                "fileName": "67183bba-c20c-413d-9e13-862623d83d2b.xml",
                "fileSize": 370,
                "lastModified": 1681400344051
            },
            {
                "fileName": "673d6863-ec48-48c3-a271-d306bc0601b5.xml",
                "fileSize": 368,
                "lastModified": 1681794432941
            },
            {
                "fileName": "67c13533-0753-42ec-a402-e95c49ceda71.xml",
                "fileSize": 1706,
                "lastModified": 1681966505234
            },
            {
                "fileName": "683865a1-c143-4441-b026-b8baa278b6be.xml",
                "fileSize": 731,
                "lastModified": 1681715318123
            },
            {
                "fileName": "6a32d9a5-a4b6-4dca-9c07-bd2a11092e2f.xml",
                "fileSize": 380,
                "lastModified": 1681708479922
            },
            {
                "fileName": "6aebc6d0-ab08-4bad-b592-85a92c75c733.xml",
                "fileSize": 429,
                "lastModified": 1682057726037
            },
            {
                "fileName": "6b0bc738-1141-498a-9ed9-152846ee9b93.xml",
                "fileSize": 418,
                "lastModified": 1681400405033
            },
            {
                "fileName": "6b9d8279-a4af-4b84-9bf3-69ec16c1f703.xml",
                "fileSize": 1706,
                "lastModified": 1681713896805
            },
            {
                "fileName": "6ced080b-37b6-447a-8d82-16505526c1e2.xml",
                "fileSize": 1706,
                "lastModified": 1681908270149
            },
            {
                "fileName": "6d79f126-e177-44c3-9924-35818d0eaa07.xml",
                "fileSize": 380,
                "lastModified": 1682057823602
            },
            {
                "fileName": "707ce861-2cd6-40a2-a6d7-1c30a20bc6d1.xml",
                "fileSize": 354,
                "lastModified": 1681794435045
            },
            {
                "fileName": "70bb6619-d07a-4f1d-802d-d748119517c4.xml",
                "fileSize": 391,
                "lastModified": 1681708446839
            },
            {
                "fileName": "7100b8ab-185a-463d-a336-50d61fb320ef.xml",
                "fileSize": 374,
                "lastModified": 1681715298465
            },
            {
                "fileName": "721ab7c8-304a-4924-b319-d4afb625acfe.xml",
                "fileSize": 357,
                "lastModified": 1681715300093
            },
            {
                "fileName": "728057de-c7ad-44eb-8670-fb2ac74917b8.xml",
                "fileSize": 448,
                "lastModified": 1681717585622
            },
            {
                "fileName": "735008d4-1ca6-469b-9260-89325aa19abf.xml",
                "fileSize": 335,
                "lastModified": 1681708476777
            },
            {
                "fileName": "73b1a545-29a8-4c6d-b9d9-ef8a08ca59ca.xml",
                "fileSize": 1706,
                "lastModified": 1681882373128
            },
            {
                "fileName": "74e0f06a-dfa8-48a9-bac7-966590478d4b.xml",
                "fileSize": 1706,
                "lastModified": 1681794432357
            },
            {
                "fileName": "75857535-eec8-4291-a631-a02f8fe1e2c3.xml",
                "fileSize": 375,
                "lastModified": 1681882367964
            },
            {
                "fileName": "77ac7fb4-5f23-42aa-98d9-0ebbcd1ed39c.xml",
                "fileSize": 1706,
                "lastModified": 1681966533389
            },
            {
                "fileName": "789ceff7-02a2-49c9-a8db-b8258770cc1a.xml",
                "fileSize": 1706,
                "lastModified": 1681708457716
            },
            {
                "fileName": "7a5e46ab-ee75-469b-8eb7-de4682e456e6.xml",
                "fileSize": 364,
                "lastModified": 1681715298717
            },
            {
                "fileName": "7a719425-2f5a-49ff-873d-3110fe8e0952.xml",
                "fileSize": 1706,
                "lastModified": 1681908589878
            },
            {
                "fileName": "7a94f023-953e-4f56-847c-ccdedaf910b7.xml",
                "fileSize": 2148,
                "lastModified": 1681708474681
            },
            {
                "fileName": "7ae0fbab-758e-4d53-a5d9-43f36d093918.xml",
                "fileSize": 2290,
                "lastModified": 1681707762124
            },
            {
                "fileName": "7c5d21cc-829e-4bfe-8ba9-30346b94ee48.xml",
                "fileSize": 1706,
                "lastModified": 1681738285013
            },
            {
                "fileName": "7ce9f3d5-66d0-42e1-b7be-38e1c5500600.xml",
                "fileSize": 341,
                "lastModified": 1681400362245
            },
            {
                "fileName": "7d4bdecd-00e4-479b-a376-bf9f54876352.xml",
                "fileSize": 324,
                "lastModified": 1681718048500
            },
            {
                "fileName": "7d5d5372-39fc-41fe-8ede-4bd675f4ad0d.xml",
                "fileSize": 731,
                "lastModified": 1681400388503
            },
            {
                "fileName": "7e5a4c33-b8df-4525-8c88-e0d6169616c1.xml",
                "fileSize": 2291,
                "lastModified": 1681794407923
            },
            {
                "fileName": "7ed921b7-46ab-4b08-ab10-cfa24a7c5696.xml",
                "fileSize": 1706,
                "lastModified": 1681713928540
            },
            {
                "fileName": "80fbc9ce-e068-41bb-9d65-2481ebfc1e69.xml",
                "fileSize": 370,
                "lastModified": 1681966505310
            },
            {
                "fileName": "831faa61-5141-4737-8c06-ba3b4230137f.xml",
                "fileSize": 355,
                "lastModified": 1681715299753
            },
            {
                "fileName": "83d0686e-458b-4bd7-80e0-7a54fbd47085.xml",
                "fileSize": 435,
                "lastModified": 1681824754801
            },
            {
                "fileName": "83ed27d7-8904-47d1-80f6-a36c28d22efc.xml",
                "fileSize": 354,
                "lastModified": 1681882381481
            },
            {
                "fileName": "8431d6ce-ed65-4840-9a8d-7154cf1b52cc.xml",
                "fileSize": 366,
                "lastModified": 1681713929492
            },
            {
                "fileName": "85709742-bbdb-4d44-9db1-84af34d0bb19.xml",
                "fileSize": 344,
                "lastModified": 1681882418208
            },
            {
                "fileName": "86740266-9fd8-47c6-b6c5-4c29e3cb163f.xml",
                "fileSize": 371,
                "lastModified": 1681708459408
            },
            {
                "fileName": "86d2fe98-1edd-4648-af44-fbb75f4cc35d.xml",
                "fileSize": 1706,
                "lastModified": 1681819119558
            },
            {
                "fileName": "8741e844-15c7-45ed-abc7-49b833c3c595.xml",
                "fileSize": 870,
                "lastModified": 1681966571596
            },
            {
                "fileName": "87a900db-6f58-4456-8e00-1963c35c6c23.xml",
                "fileSize": 432,
                "lastModified": 1681713899973
            },
            {
                "fileName": "89f10796-d34d-442a-b1a4-ff2f1cdd3785.xml",
                "fileSize": 366,
                "lastModified": 1681708458652
            },
            {
                "fileName": "89f75727-5018-4add-9b51-e637180d3c8b.xml",
                "fileSize": 380,
                "lastModified": 1681794453747
            },
            {
                "fileName": "8a2d2689-fb40-45d9-8ecd-63fed4aa1a93.xml",
                "fileSize": 870,
                "lastModified": 1682057859593
            },
            {
                "fileName": "8a342204-7574-4e94-baa6-879ac252d55b.xml",
                "fileSize": 374,
                "lastModified": 1681713929816
            },
            {
                "fileName": "8a691449-b524-411c-80cf-b34cd31541ac.xml",
                "fileSize": 1980,
                "lastModified": 1681905564241
            },
            {
                "fileName": "8b9a490e-96dc-406c-bf70-8bcaae3622e7.xml",
                "fileSize": 409,
                "lastModified": 1681882371220
            },
            {
                "fileName": "8cf57744-400d-4272-8c76-cbd5337cebe4.xml",
                "fileSize": 364,
                "lastModified": 1681882380029
            },
            {
                "fileName": "8d6682fc-6ee3-4d52-a941-237a30a96aba.xml",
                "fileSize": 341,
                "lastModified": 1682057793911
            },
            {
                "fileName": "8dbe1c5f-d52a-4d31-ba4a-9fc11fec42c3.xml",
                "fileSize": 731,
                "lastModified": 1681708477193
            },
            {
                "fileName": "8e8e2465-a393-42dc-a6cf-0929f82bfeb3.xml",
                "fileSize": 364,
                "lastModified": 1681794433621
            },
            {
                "fileName": "8e90b74c-d1d0-4a66-93b1-9407d46606d4.xml",
                "fileSize": 357,
                "lastModified": 1681713931680
            },
            {
                "fileName": "8f84c38c-df78-4bc2-b959-f12eda3c8592.xml",
                "fileSize": 1980,
                "lastModified": 1681715312478
            },
            {
                "fileName": "90709234-1066-41b5-8528-49d28fad1264.xml",
                "fileSize": 1706,
                "lastModified": 1681737102942
            },
            {
                "fileName": "9192bf01-ac11-48b0-8a67-af9e74143755.xml",
                "fileSize": 1980,
                "lastModified": 1681713946137
            },
            {
                "fileName": "92ab8cd5-5c4d-41cf-be6a-0c6474e21aa7.xml",
                "fileSize": 519,
                "lastModified": 1681794407915
            },
            {
                "fileName": "92e7160d-a4c7-4845-a1de-bf49f66b9b82.xml",
                "fileSize": 355,
                "lastModified": 1682057803480
            },
            {
                "fileName": "93558f75-1f65-4a40-8f25-bf323c0a523f.xml",
                "fileSize": 380,
                "lastModified": 1681715320635
            },
            {
                "fileName": "93fc0c25-caf3-4058-a6ac-8e93aa0468e6.xml",
                "fileSize": 355,
                "lastModified": 1681400370986
            },
            {
                "fileName": "945ae5c4-1031-4f43-a7e9-02f9bece984d.xml",
                "fileSize": 731,
                "lastModified": 1681966553831
            },
            {
                "fileName": "94b0cd26-d620-4e67-b1b1-64a7cc61e8fc.xml",
                "fileSize": 375,
                "lastModified": 1681715287136
            },
            {
                "fileName": "9669c1d3-3e8b-4ba9-9e61-7731c73ea394.xml",
                "fileSize": 352,
                "lastModified": 1681882381133
            },
            {
                "fileName": "9713382c-19b0-4c9a-a814-32036007d678.xml",
                "fileSize": 1706,
                "lastModified": 1681966527088
            },
            {
                "fileName": "99927cf0-463c-4d63-bdba-c317e69930f5.xml",
                "fileSize": 335,
                "lastModified": 1681713951606
            },
            {
                "fileName": "9b0fc476-95ab-4ec8-84f4-3fa4101847e3.xml",
                "fileSize": 374,
                "lastModified": 1681794433433
            },
            {
                "fileName": "9b5adfdb-a7be-49d6-9e22-3eaffb65fd77.xml",
                "fileSize": 357,
                "lastModified": 1682057803784
            },
            {
                "fileName": "9b6f8e9b-4a87-453d-93a1-cc4d29c4b8ef.xml",
                "fileSize": 352,
                "lastModified": 1681400371118
            },
            {
                "fileName": "9b8a3291-8a20-491b-bbd6-3900320788e1.xml",
                "fileSize": 341,
                "lastModified": 1681794424760
            },
            {
                "fileName": "9bb1aca5-4b7d-4f2e-a0a3-949433d975b2.xml",
                "fileSize": 391,
                "lastModified": 1681882368016
            },
            {
                "fileName": "9c752bc6-fbd9-4397-b06f-ba497fcba953.xml",
                "fileSize": 357,
                "lastModified": 1681966536277
            },
            {
                "fileName": "9e342336-73a0-41d9-95a1-c0ce5a85e8c8.xml",
                "fileSize": 354,
                "lastModified": 1681966536473
            },
            {
                "fileName": "a110bdb2-9295-4df5-b434-fda308a53fed.xml",
                "fileSize": 364,
                "lastModified": 1681400369962
            },
            {
                "fileName": "a1828afe-c93b-4e8f-a969-f0dd2551d397.xml",
                "fileSize": 870,
                "lastModified": 1681400406321
            },
            {
                "fileName": "a217806b-3f8a-456a-a99c-0d8141070873.xml",
                "fileSize": 374,
                "lastModified": 1681708458876
            },
            {
                "fileName": "a32c6177-5628-486c-a5c0-511328fb4c07.xml",
                "fileSize": 1706,
                "lastModified": 1681882283212
            },
            {
                "fileName": "a34a92af-f87f-4d76-b70a-7ac5dad046df.xml",
                "fileSize": 368,
                "lastModified": 1682057801568
            },
            {
                "fileName": "a3a19741-a67b-476f-bdfd-5ee57d27f1cb.xml",
                "fileSize": 525,
                "lastModified": 1681715336860
            },
            {
                "fileName": "a6effe53-889b-4656-a576-dbc5e78a2914.xml",
                "fileSize": 355,
                "lastModified": 1681882381001
            },
            {
                "fileName": "a7b41bc0-8465-45ba-8498-5b8d9817e2de.xml",
                "fileSize": 409,
                "lastModified": 1682057793411
            },
            {
                "fileName": "a81c0836-d607-4648-9c31-c4ba6d619fc6.xml",
                "fileSize": 391,
                "lastModified": 1681713915643
            },
            {
                "fileName": "a84c50dd-597c-40bd-959e-646164b13c0b.xml",
                "fileSize": 429,
                "lastModified": 1681708366439
            },
            {
                "fileName": "acc4725b-4e23-456e-a745-760fabbcd201.xml",
                "fileSize": 341,
                "lastModified": 1681713920063
            },
            {
                "fileName": "ad9fb5f8-bce0-47c6-a376-18a67d8f8384.xml",
                "fileSize": 429,
                "lastModified": 1681882283296
            },
            {
                "fileName": "af2d297e-20ec-4ad3-8f90-4233defb783e.xml",
                "fileSize": 357,
                "lastModified": 1681794434889
            },
            {
                "fileName": "af5482d1-88f4-40ff-89df-f2036312a29f.xml",
                "fileSize": 370,
                "lastModified": 1681708431661
            },
            {
                "fileName": "b1519c25-4886-402e-ac22-59793807bce5.xml",
                "fileSize": 335,
                "lastModified": 1681794450971
            },
            {
                "fileName": "b21b2746-7d91-47f7-a20e-388e534dfb11.xml",
                "fileSize": 366,
                "lastModified": 1681400346671
            },
            {
                "fileName": "b3afef66-ce42-47b0-ac8a-6aafd1a782d1.xml",
                "fileSize": 2148,
                "lastModified": 1681400385895
            },
            {
                "fileName": "b5c7ada4-39a7-417d-800a-e68b405ec5fe.xml",
                "fileSize": 435,
                "lastModified": 1681817615613
            },
            {
                "fileName": "b7ba515b-c98f-4c79-bfdb-c2f85406fc14.xml",
                "fileSize": 391,
                "lastModified": 1682057790195
            },
            {
                "fileName": "b89ec1bb-0fe2-432d-a926-18cd220e6085.xml",
                "fileSize": 364,
                "lastModified": 1681966534625
            },
            {
                "fileName": "b8a9c526-eb2c-4886-bbe9-ec4fa9f4ec52.xml",
                "fileSize": 1706,
                "lastModified": 1681794431965
            },
            {
                "fileName": "ba738457-580b-4a5e-b364-4f46101dac26.xml",
                "fileSize": 371,
                "lastModified": 1681400370282
            },
            {
                "fileName": "bb5a5fc7-fd10-4daf-a4b2-216f3cdcb724.xml",
                "fileSize": 432,
                "lastModified": 1681400346391
            },
            {
                "fileName": "be46d75e-2335-459f-8e21-9a6299e7f466.xml",
                "fileSize": 2291,
                "lastModified": 1681966508343
            },
            {
                "fileName": "bf98524a-0816-4d44-b148-04e8ddfd8286.xml",
                "fileSize": 1706,
                "lastModified": 1681400278141
            },
            {
                "fileName": "c010f6cb-307c-4949-b92c-356bb24c8168.xml",
                "fileSize": 352,
                "lastModified": 1681715299897
            },
            {
                "fileName": "c0fb57d1-ee93-4884-8adf-b8e0cb242cac.xml",
                "fileSize": 366,
                "lastModified": 1681882379573
            },
            {
                "fileName": "c101b38d-eb43-4119-9072-6483a814d124.xml",
                "fileSize": 1706,
                "lastModified": 1681966533005
            },
            {
                "fileName": "c113e9ac-ccee-455a-9a0d-8b955521cc8a.xml",
                "fileSize": 429,
                "lastModified": 1681713822810
            },
            {
                "fileName": "c1d917e9-3d13-419f-a446-96a7a29d355f.xml",
                "fileSize": 1706,
                "lastModified": 1681794404979
            },
            {
                "fileName": "c1e9ce18-29ea-446d-b432-8226bb6d7563.xml",
                "fileSize": 371,
                "lastModified": 1681713930592
            },
            {
                "fileName": "c23595be-39cb-4853-ad53-dde08bcb3e92.xml",
                "fileSize": 432,
                "lastModified": 1682057777121
            },
            {
                "fileName": "c5a50624-2ee3-43fb-8821-e977ddc32652.xml",
                "fileSize": 1706,
                "lastModified": 1681897770878
            },
            {
                "fileName": "c5a5769b-030e-4d7f-8f52-224f0e4d276d.xml",
                "fileSize": 391,
                "lastModified": 1681794421260
            },
            {
                "fileName": "c631f8e6-dbcb-465c-adcd-020c1e3b946a.xml",
                "fileSize": 352,
                "lastModified": 1681794434733
            },
            {
                "fileName": "c848f565-4f4f-4355-8427-151e402ae17c.xml",
                "fileSize": 1706,
                "lastModified": 1681708366387
            },
            {
                "fileName": "c8c30cca-7da4-4158-9421-72af3a8ed6ed.xml",
                "fileSize": 335,
                "lastModified": 1681400388147
            },
            {
                "fileName": "c8fce331-95ae-43f7-84ee-c0c078d2bcfa.xml",
                "fileSize": 442,
                "lastModified": 1681818796332
            },
            {
                "fileName": "ca7c620f-e498-45a8-83a5-9bbe62967819.xml",
                "fileSize": 432,
                "lastModified": 1681708434109
            },
            {
                "fileName": "ca83349d-ed1e-4d9b-9cba-1a5d5081954f.xml",
                "fileSize": 391,
                "lastModified": 1681715287188
            },
            {
                "fileName": "cab2f675-1536-4432-9f83-998cfb2984a5.xml",
                "fileSize": 435,
                "lastModified": 1681816761621
            },
            {
                "fileName": "cb58bf05-c7fa-41e3-a5b7-ec1c0c8733a6.xml",
                "fileSize": 519,
                "lastModified": 1681707194936
            },
            {
                "fileName": "cc231b6e-5d2b-469e-a5d6-7fec226c5205.xml",
                "fileSize": 418,
                "lastModified": 1681882416512
            },
            {
                "fileName": "ccb9aa4d-2d57-41e9-b37c-fd370a5c338a.xml",
                "fileSize": 429,
                "lastModified": 1681715226146
            },
            {
                "fileName": "ce482305-5f71-42b5-8316-780678508f9e.xml",
                "fileSize": 525,
                "lastModified": 1681794469492
            },
            {
                "fileName": "cf297fd6-ec55-4234-bfa9-c77a3a3e1b45.xml",
                "fileSize": 2148,
                "lastModified": 1681882396506
            },
            {
                "fileName": "check 2301161646_1681385427150.pdf",
                "fileSize": 8131,
                "lastModified": 1681385461282
            },
            {
                "fileName": "check 2301161646_1681471827090.pdf",
                "fileSize": 8131,
                "lastModified": 1681471861147
            },
            {
                "fileName": "check 2301161646_1681558227036.pdf",
                "fileSize": 8131,
                "lastModified": 1681558261041
            },
            {
                "fileName": "check 2301161646_1681644627038.pdf",
                "fileSize": 8131,
                "lastModified": 1681644661614
            },
            {
                "fileName": "check 2301161646_1681731027177.pdf",
                "fileSize": 8131,
                "lastModified": 1681731063340
            },
            {
                "fileName": "check 2301161646_1681817427109.pdf",
                "fileSize": 8131,
                "lastModified": 1681817461067
            },
            {
                "fileName": "check 2301161646_1682076627099.pdf",
                "fileSize": 8131,
                "lastModified": 1682076661376
            },
            {
                "fileName": "check 2301161646_1682163027042.pdf",
                "fileSize": 8131,
                "lastModified": 1682163061209
            },
            {
                "fileName": "check 2301161646_1682249427027.pdf",
                "fileSize": 8131,
                "lastModified": 1682249461428
            },
            {
                "fileName": "check_report_daily 2301171105_1681451421044.pdf",
                "fileSize": 38074,
                "lastModified": 1681451455275
            },
            {
                "fileName": "check_report_daily 2301171105_1681537821042.pdf",
                "fileSize": 38074,
                "lastModified": 1681537855214
            },
            {
                "fileName": "check_report_daily 2301171105_1681624221043.pdf",
                "fileSize": 38074,
                "lastModified": 1681624255247
            },
            {
                "fileName": "check_report_daily 2301171105_1681797021039.pdf",
                "fileSize": 38074,
                "lastModified": 1681797055261
            },
            {
                "fileName": "check_report_daily 2301171105_1681883421267.pdf",
                "fileSize": 38074,
                "lastModified": 1681883458344
            },
            {
                "fileName": "check_report_daily 2301171105_1681969821045.pdf",
                "fileSize": 38074,
                "lastModified": 1681969855346
            },
            {
                "fileName": "check_report_daily 2301171105_1682142621039.pdf",
                "fileSize": 38074,
                "lastModified": 1682142655209
            },
            {
                "fileName": "check_report_daily 2301171105_1682229021043.pdf",
                "fileSize": 38074,
                "lastModified": 1682229055242
            },
            {
                "fileName": "d1f0607d-40a1-4dfe-a56b-5620eadb7240.xml",
                "fileSize": 370,
                "lastModified": 1681794405075
            },
            {
                "fileName": "d3b9e8d3-a4d4-4f13-9df1-a3a44d81955a.xml",
                "fileSize": 366,
                "lastModified": 1681882355315
            },
            {
                "fileName": "d4b459f6-edfb-4235-8f4a-504c3deaab43.xml",
                "fileSize": 344,
                "lastModified": 1681715336608
            },
            {
                "fileName": "d88380d0-8ac6-4097-a4b6-f9b3f1310e5d.xml",
                "fileSize": 870,
                "lastModified": 1681882417504
            },
            {
                "fileName": "db715b78-eaa5-43aa-aa00-3afd02136fba.xml",
                "fileSize": 433,
                "lastModified": 1681821875846
            },
            {
                "fileName": "dc943c21-acd8-412d-8050-49242fcf16d4.xml",
                "fileSize": 525,
                "lastModified": 1682057860401
            },
            {
                "fileName": "dead31a1-eda0-454d-8ed7-18648bf009a6.xml",
                "fileSize": 380,
                "lastModified": 1681966556467
            },
            {
                "fileName": "designer 2301171724_1681733354122.pdf",
                "fileSize": 30631,
                "lastModified": 1681733388336
            },
            {
                "fileName": "df5a939c-2e56-4348-8992-b3adaa120f0a.xml",
                "fileSize": 335,
                "lastModified": 1681715317779
            },
            {
                "fileName": "e116ece4-ed22-4cda-bfe3-4a1819cfaf4d.xml",
                "fileSize": 355,
                "lastModified": 1681794434633
            },
            {
                "fileName": "e197dbbe-93a9-4855-bccf-2d6732028369.xml",
                "fileSize": 344,
                "lastModified": 1681400406893
            },
            {
                "fileName": "e218b064-5828-4fb4-9d82-f7a449768077.xml",
                "fileSize": 429,
                "lastModified": 1681966438520
            },
            {
                "fileName": "e2b6057e-a298-4ba9-a049-f268db4b1764.xml",
                "fileSize": 341,
                "lastModified": 1681882371792
            },
            {
                "fileName": "e2cd4648-5952-4d75-8cac-f46039b7354b.xml",
                "fileSize": 1706,
                "lastModified": 1681715297481
            },
            {
                "fileName": "e318e6f8-6988-4c37-b74f-d936309d095a.xml",
                "fileSize": 371,
                "lastModified": 1681966535073
            },
            {
                "fileName": "e38b6bbf-4703-47bb-ae53-5c7cb468df25.xml",
                "fileSize": 357,
                "lastModified": 1681882381357
            },
            {
                "fileName": "e44ba0f4-7bbf-48c2-be2d-b994abebfbd0.xml",
                "fileSize": 432,
                "lastModified": 1681966509191
            },
            {
                "fileName": "e548636d-df02-47c2-976c-52f349e3aa9f.xml",
                "fileSize": 1706,
                "lastModified": 1682057795331
            },
            {
                "fileName": "e7a05923-048a-4d03-9e3e-25a05b4c495b.xml",
                "fileSize": 368,
                "lastModified": 1681713929128
            },
            {
                "fileName": "e8300857-22ae-4adc-9ab3-d4801a7cb505.xml",
                "fileSize": 371,
                "lastModified": 1682057802764
            },
            {
                "fileName": "e879963e-5d86-487c-941b-bfa4d8b5020b.xml",
                "fileSize": 525,
                "lastModified": 1681400407173
            },
            {
                "fileName": "eb22b2da-3633-4935-8427-c1376599081c.xml",
                "fileSize": 1706,
                "lastModified": 1682057725953
            },
            {
                "fileName": "ec295bdb-c18c-4f10-a159-5c40d09c8389.xml",
                "fileSize": 344,
                "lastModified": 1681708515313
            },
            {
                "fileName": "ec46ce4d-0e66-41c7-9b3a-e6bac85a29b5.xml",
                "fileSize": 1706,
                "lastModified": 1682057800732
            },
            {
                "fileName": "ed9dd63f-71fe-45db-b1b4-75b915b41d42.xml",
                "fileSize": 1706,
                "lastModified": 1681708451423
            },
            {
                "fileName": "f21053b6-63a5-4be8-9fc1-5e469ba25249.xml",
                "fileSize": 352,
                "lastModified": 1681966536053
            },
            {
                "fileName": "f3d7fd88-787c-4fea-bca8-1fc1738bf1b1.xml",
                "fileSize": 354,
                "lastModified": 1681400371422
            },
            {
                "fileName": "f4264ea5-6720-45e5-9690-69e00809045c.xml",
                "fileSize": 1706,
                "lastModified": 1681713822742
            },
            {
                "fileName": "f469c1f5-771a-47f6-9af4-2463cf828a6c.xml",
                "fileSize": 375,
                "lastModified": 1681713915587
            },
            {
                "fileName": "f507ee2a-2323-465b-9577-f0fbb7666c22.xml",
                "fileSize": 366,
                "lastModified": 1681715298253
            },
            {
                "fileName": "f5a7c2c6-4c9f-4569-b68a-b8419e174beb.xml",
                "fileSize": 375,
                "lastModified": 1681400358680
            },
            {
                "fileName": "f7690d91-1b94-4869-9c9f-e1e769041339.xml",
                "fileSize": 1980,
                "lastModified": 1682057816029
            },
            {
                "fileName": "f7b3f0a0-d7a2-4f9e-a925-2c6962f48b7c.xml",
                "fileSize": 1706,
                "lastModified": 1681400363513
            },
            {
                "fileName": "f94aaf9f-5805-4e17-8085-3dbe1b1edd85.xml",
                "fileSize": 380,
                "lastModified": 1681713955154
            },
            {
                "fileName": "f9ec063e-5932-4f97-8b9f-096907a7a5bb.xml",
                "fileSize": 435,
                "lastModified": 1681826283872
            },
            {
                "fileName": "fa824633-e1f5-4802-85e1-6d43cf9b9d82.xml",
                "fileSize": 374,
                "lastModified": 1681400369689
            },
            {
                "fileName": "fb4eba3e-6f8a-4bed-a334-7ac6acefed98.xml",
                "fileSize": 2148,
                "lastModified": 1681715315538
            },
            {
                "fileName": "fb60f0e9-cfd8-48a8-a12f-a47dd7784952.xml",
                "fileSize": 366,
                "lastModified": 1682057777413
            },
            {
                "fileName": "fc34ee1b-f667-4ce7-a516-090b108ebbeb.xml",
                "fileSize": 432,
                "lastModified": 1681715274443
            },
            {
                "fileName": "fcfbbfd3-ed41-4f43-94ac-eea9bcd04f27.xml",
                "fileSize": 1706,
                "lastModified": 1681715297173
            },
            {
                "fileName": "fdb6caff-a696-45b0-95f6-32379710463c.xml",
                "fileSize": 341,
                "lastModified": 1681708450227
            },
            {
                "fileName": "fef5eb79-e706-46eb-96c4-5c8c2edb67cd.xml",
                "fileSize": 409,
                "lastModified": 1681713919367
            },
            {
                "fileName": "ff0b697b-1a7c-4557-b3f2-6a2f583e1c8b.xml",
                "fileSize": 354,
                "lastModified": 1681708460664
            },
            {
                "fileName": "ffbc8327-b3ca-47a7-a993-c8e2c9ac57f1.xml",
                "fileSize": 2291,
                "lastModified": 1681727279789
            },
            {
                "fileName": "saveReport 2302201009_1681620865040.pdf",
                "fileSize": 38133,
                "lastModified": 1681620899905
            },
            {
                "fileName": "saveReport 2302201009_1682225665047.pdf",
                "fileSize": 38133,
                "lastModified": 1682225699946
            },
            {
                "fileName": "saveReport 2302201052_1681450613126.pdf",
                "fileSize": 38133,
                "lastModified": 1681450651741
            },
            {
                "fileName": "saveReport 2302201052_1681450614290.csv",
                "fileSize": 1819,
                "lastModified": 1681450614290
            },
            {
                "fileName": "saveReport 2302201052_1681537013089.pdf",
                "fileSize": 38133,
                "lastModified": 1681537047972
            },
            {
                "fileName": "saveReport 2302201052_1681537014026.csv",
                "fileSize": 1819,
                "lastModified": 1681537014025
            },
            {
                "fileName": "saveReport 2302201052_1681623413047.pdf",
                "fileSize": 38133,
                "lastModified": 1681623447529
            },
            {
                "fileName": "saveReport 2302201052_1681623413575.csv",
                "fileSize": 1819,
                "lastModified": 1681623413570
            },
            {
                "fileName": "saveReport 2302201052_1681709813127.pdf",
                "fileSize": 38133,
                "lastModified": 1681709849459
            },
            {
                "fileName": "saveReport 2302201052_1681709813836.csv",
                "fileSize": 1819,
                "lastModified": 1681709813835
            },
            {
                "fileName": "saveReport 2302201052_1681796213236.pdf",
                "fileSize": 38133,
                "lastModified": 1681796250151
            },
            {
                "fileName": "saveReport 2302201052_1681796213919.csv",
                "fileSize": 1819,
                "lastModified": 1681796213916
            },
            {
                "fileName": "saveReport 2302201052_1681969013134.pdf",
                "fileSize": 38133,
                "lastModified": 1681969051028
            },
            {
                "fileName": "saveReport 2302201052_1681969014552.csv",
                "fileSize": 1819,
                "lastModified": 1681969014549
            },
            {
                "fileName": "saveReport 2302201052_1682141813069.pdf",
                "fileSize": 38133,
                "lastModified": 1682141849040
            },
            {
                "fileName": "saveReport 2302201052_1682141814625.csv",
                "fileSize": 1819,
                "lastModified": 1682141814632
            },
            {
                "fileName": "saveReport 2302201052_1682228213047.pdf",
                "fileSize": 38133,
                "lastModified": 1682228247380
            },
            {
                "fileName": "saveReport 2302201052_1682228213487.csv",
                "fileSize": 1819,
                "lastModified": 1682228213485
            },
            {
                "fileName": "saveReport 2302201117_1681970510049.pdf",
                "fileSize": 38133,
                "lastModified": 1681970546153
            },
            {
                "fileName": "saveReport 2302201117_1681970510500.csv",
                "fileSize": 1819,
                "lastModified": 1681970510494
            },
            {
                "fileName": "saveReport 2302201117_1681970511597.xlsx",
                "fileSize": 4465,
                "lastModified": 1681970511850
            },
            {
                "fileName": "saveReport 2302221141_1681885500045.pdf",
                "fileSize": 38133,
                "lastModified": 1681885534447
            },
            {
                "fileName": "saveReport 2302221204_1682146149521.xlsx",
                "fileSize": 4467,
                "lastModified": 1682146149883
            },
            {
                "fileName": "saveReport 2302221859_1681453531023.pdf",
                "fileSize": 38133,
                "lastModified": 1681453565017
            },
            {
                "fileName": "saveReport 2302221859_1681539931045.pdf",
                "fileSize": 38133,
                "lastModified": 1681539965323
            },
            {
                "fileName": "saveReport 2302221859_1681626331049.pdf",
                "fileSize": 38133,
                "lastModified": 1681626365163
            },
            {
                "fileName": "saveReport 2302221859_1681712731086.pdf",
                "fileSize": 38133,
                "lastModified": 1681712767521
            },
            {
                "fileName": "saveReport 2302221859_1681799131047.pdf",
                "fileSize": 38133,
                "lastModified": 1681799165290
            },
            {
                "fileName": "saveReport 2302221859_1681885531024.pdf",
                "fileSize": 38133,
                "lastModified": 1681885565094
            },
            {
                "fileName": "saveReport 2302221859_1681971931042.pdf",
                "fileSize": 38133,
                "lastModified": 1681971965515
            },
            {
                "fileName": "saveReport 2302221859_1682058331216.pdf",
                "fileSize": 38133,
                "lastModified": 1682058367423
            },
            {
                "fileName": "saveReport 2302221859_1682144731040.pdf",
                "fileSize": 38133,
                "lastModified": 1682144765301
            },
            {
                "fileName": "saveReport 2302221859_1682231131049.pdf",
                "fileSize": 38133,
                "lastModified": 1682231165115
            },
            {
                "fileName": "test 2301171216_1681455658045.pdf",
                "fileSize": 38074,
                "lastModified": 1681455692221
            },
            {
                "fileName": "test 2301171216_1681542058043.pdf",
                "fileSize": 38074,
                "lastModified": 1681542092239
            },
            {
                "fileName": "test 2301171216_1681628458043.pdf",
                "fileSize": 38074,
                "lastModified": 1681628492313
            },
            {
                "fileName": "test 2301171216_1681801258025.pdf",
                "fileSize": 38074,
                "lastModified": 1681801292169
            },
            {
                "fileName": "test 2301171216_1681887658022.pdf",
                "fileSize": 38074,
                "lastModified": 1681887692176
            },
            {
                "fileName": "test 2301171216_1681974058042.pdf",
                "fileSize": 38074,
                "lastModified": 1681974092281
            },
            {
                "fileName": "test 2301171216_1682060458069.pdf",
                "fileSize": 38074,
                "lastModified": 1682060492610
            },
            {
                "fileName": "test 2301171216_1682146858026.pdf",
                "fileSize": 38074,
                "lastModified": 1682146892491
            },
            {
                "fileName": "test 2301171216_1682233258046.pdf",
                "fileSize": 38074,
                "lastModified": 1682233292317
            },
            {
                "fileName": "unittest 2301171241_1681629995040.pdf",
                "fileSize": 38074,
                "lastModified": 1681630029049
            },
            {
                "fileName": "unittest 2301171241_1682234795037.pdf",
                "fileSize": 38074,
                "lastModified": 1682234829234
            }
        ]
    },
    "cacheSize": {
        "size": 21
    },
    "cachedReports": [
        {
            "path": "02_03/1122views.metadata"
        },
        {
            "path": "Manish_18/Metadata_new.metadata"
        },
        {
            "path": "Sibtain/Metadata_1.metadata"
        },
        {
            "path": "create4share/MetadataSave.metadata"
        },
        {
            "path": "sai_ganesh/Metadata_1.metadata"
        },
        {
            "path": "scheduling/Metadata_for_scheduling.metadata"
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
            },
            {
                "baseType": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "id": "1000",
                "name": "hiee",
                "type": "dynamicDatasource",
                "isDatabaseMetadataCached": "connection"
            },
            {
                "baseType": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "id": "1201",
                "name": "SampleTravelData",
                "type": "dynamicDatasource",
                "isDatabaseMetadataCached": "partial"
            },
            {
                "baseType": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "id": "1302",
                "name": "Sqlite",
                "type": "dynamicDatasource",
                "isDatabaseMetadataCached": "partial"
            }
        ]
    },
    "reportStats": {},
    "dataSourceCount": {},
    "currentLevel": "ERROR",
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
        "Version": "5.0.0.884 RC2",
        "Build": "R20232404_GIT884",
        "Product Name": "Helical Insight",
        "Expiration": "30/06/2023",
        "License Type": "Trial"
    },
    "reloadAppMessage": "",
    "reloadValidMessage": "",
    "reloadCacheMessage": "",
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
    "profileId": 1,
    "osDetails": null,
    "jvmThreadDetails": null,
    "osTableExpand": false,
    "jvmTableExpand": false,
    "managementData": null,
    "managementStaticData": null,
    "managementAdvancedData": null,
    "isDiceApiRunning": false,
    "noSqlData": null,
    "diceStorage": {
        "metadataDetails": {
            "fileName": "",
            "path": ""
        },
        "tabName": "metadata",
        "diceStorageTableData": {
            "metadata": [],
            "cube": []
        },
        "diceStorageFieldSearchText": "",
        "diceStorageSearchedColumn": "",
        "skeletonRowKeys": []
    },
    "pluginsData": null,
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
}

export const bug6333fileBrowserReduxData={
    "globalFilters": {
        "filterByType": null,
        "groupBy": null
    },
    "files": {
        "loading": false,
        "data": [],
        "error": null
    },
    "filteredFiles": null,
    "searchResults": null,
    "clickedContextItemDetails": {
        "contextItem": null,
        "clickedRecord": null
    },
    "expandedRow": null,
    "saveFileDetails": {
        "name": "",
        "path": ""
    },
    "isShareModalVisible": false,
    "tableColumns": null,
    "showFileBrowser": null,
    "shareTableData": null,
    "globalSearch": null,
    "compactModeCoordinates": null,
    "globalFbEnabled": false,
    "exportModalData": {
        "visible": false,
        "recordData": {}
    }
}