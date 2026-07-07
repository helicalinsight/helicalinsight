export const app5505 = {
    "shouldBlockNavigation": false,
    "value": true,
    "isAuthenticated": true,
    "isLogoutManually": false,
    "hasError": false,
    "showLicenseNotification": false,
    "isLicenseRendered": false,
    "applicationSettingsData": {
        "map": {
            "mapbox": {
                "token": "token_value"
            }
        },
        "userData": {
            "serverTime": 1699893963295,
            "expiryTime": 1699895763295,
            "sessionUserName": "shrt",
            "sessionUserEmail": "shrt@gmail.com",
            "user": {
                "name": "shrt",
                "email": "shrt@gmail.com",
                "actualUserName": "hiadmin",
                "organization": "",
                "roles": [
                    "ROLE_USER"
                ],
                "profile": []
            },
            "rootDirectoryPermission": "4",
            "provideHTMLExport": false,
            "enableReportSave": "false",
            "defaultEmailResourceType": "url",
            "saml": {
                "enabled": false,
                "logoutLink": "/saml/logout/?"
            },
            "baseUrl": "http://127.0.0.1:8085/hi-ee/",
            "clientTime": 1699893963617
        },
        "contentId": "Static/DashboardGlobals",
        "showExperimentalFeatures": false,
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
            "url": "/welcome",
            "roles": [
                "ROLE_USER",
                "ROLE_VIEWER",
                "ROLE_DOWNLOAD"
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
    "activeRoute": "/welcome",
    "isApplicationSettingsServiceCheck": true,
    "nxtRoute": "",
    "toggleSidebar": false,
    "showNavbar": true,
    "logType": "",
    "isUrlAuthenticating": false,
    "isSessionOver": false,
    "viewModeInfo": {
        "file": {
            "path": "5984Gpl/GroovyPlainReportSampleTravelData.hr",
            "name": "GroovyPlainReportSampleTravelData.hr",
            "title": "GroovyPlainReportSampleTravelData"
        },
        "mode": "open",
        "filters": [],
        "extension": "hr"
    },
    "editModeInfo": null,
    "aboutToChangeRoute": null,
    "viewerEmailModalVisible": false,
    "viewerScheduleModalVisible": false,
    "keysPressed": [],
    "currentSCLocation": "",
    "lastModified": 1699893980569,
    "toggleNavbarArrow": false
}