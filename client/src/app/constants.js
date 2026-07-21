export const authTypes = {
  JWT: "jwt",
  DIRECT: "direct",
  SSO_TOKEN: "token",
};

export const routesUrl = {
  loginUrl: "/",
  adminHomeUrl: "/admin",
  userHomeUrl: "/welcome",
  dataSourceUrl: "/datasource",
  metadataUrl: "/metadata",
  // reportCEUrl: '/report-ce',
  helicalReportUrl: "/helical-report",
  cannedReportsUrl: '/hcr',
  dashboardDesignerUrl: "/dashboard-designer",
  reportViewUrl: "/report-viewer",
  cubeUrl: "/cube",
  hiUrl: "/hi",
  instantBIUrl: "/instant-bi",
  agentUrl:"/semantic-model",
  hiceUrl: "/hice",
  hwfUrl: "/hwf"
};

export const roleTypes = {
  roleAdmin: "ROLE_ADMIN",
  roleUser: "ROLE_USER",
  roleViewer: "ROLE_VIEWER",
  roledownload: "ROLE_DOWNLOAD",
};

export const defaultRoutes = [
  {
    title: "HI: Login",
    url: routesUrl.loginUrl,
    addInNavbar: false,
    roles: [],
    isExpFeature: false
  },
  {
    title: "Data Sources",
    url: routesUrl.dataSourceUrl,
    addInNavbar: true,
    roles: ["ROLE_ADMIN"],
    tutorialElementKey: "hi-navbar-data-sources",
    isExpFeature: false
  },
  {
    title: "Meta Data",
    url: routesUrl.metadataUrl,
    addInNavbar: true,
    roles: ["ROLE_ADMIN"],
    tutorialElementKey: "hi-navbar-metadata",
    isExpFeature: false
  },
   {
    expId: "agent",
    title: "Semantic Model",
    url: routesUrl.agentUrl,
    addInNavbar: true,
    roles: ["ROLE_ADMIN"],
    tutorialElementKey: "hi-navbar-agent",
    isExpFeature: true
  },
  {
    title: "Reports",
    url: routesUrl.helicalReportUrl,
    addInNavbar: true,
    roles: ["ROLE_ADMIN", "ROLE_USER", "ROLE_DOWNLOAD"],
    tutorialElementKey: "hi-navbar-reports",
    isExpFeature: false
  },
  {
    title: "Dashboard Designer",
    url: routesUrl.dashboardDesignerUrl,
    addInNavbar: true,
    roles: ["ROLE_ADMIN", "ROLE_USER", "ROLE_DOWNLOAD"],
    tutorialElementKey: "hi-navbar-dashboard-designer",
    isExpFeature: false
  },
  // {
  // 	title: 'Report CE',
  // 	url: routesUrl.reportCEUrl,
  // 	addInNavbar: true,
  // 	roles: [ 'ROLE_ADMIN', 'ROLE_USER', 'ROLE_DOWNLOAD' ],
  // 	tutorialElementKey: 'hi-navbar-reports-ce'
  // },
  {
    title: "Report View",
    url: routesUrl.reportViewUrl,
    addInNavbar: false,
    roles: ["ROLE_ADMIN", "ROLE_USER", "ROLE_DOWNLOAD", "ROLE_VIEWER"],
    isExpFeature: false
  },
  {
    title: "HI",
    url: routesUrl.hiUrl,
    addInNavbar: false,
    roles: [],
    accessbleForAll: true,
    isExpFeature: false
  },
  {
    expId: "cube",
    title: "Cube",
    url: routesUrl.cubeUrl,
    addInNavbar: true,
    roles: ["ROLE_ADMIN"],
    tutorialElementKey: "hi-navbar-cube",
    isExpFeature: true
  },
  {
    expId: "hcr",
    title: 'Canned Report',
    url: routesUrl.cannedReportsUrl,
    addInNavbar: true,
    roles: ['ROLE_ADMIN', 'ROLE_USER', 'ROLE_DOWNLOAD'],
    tutorialElementKey: 'hi-navbar-canned-reports',
    // isExpFeature: true
  },
  {
    expId: "instant-bi",
    title: "Instant",
    url: routesUrl.instantBIUrl,
    addInNavbar: true,
    roles: ["ROLE_ADMIN", "ROLE_USER", "ROLE_DOWNLOAD"],
    tutorialElementKey: "hi-navbar-instant-bi",
    // isExpFeature: true
  },
  {
    expId: "hice",
    title: "Community Report",
    url: routesUrl.hiceUrl,
    addInNavbar: true,
    roles: ["ROLE_ADMIN", "ROLE_USER", "ROLE_DOWNLOAD"],
    tutorialElementKey: "hi-navbar-community-report",
    isExpFeature: true
  },
  {
    expId: "hwf",
    title: "Workflow",
    url: routesUrl.hwfUrl,
    addInNavbar: true,
    roles: ["ROLE_ADMIN", "ROLE_USER", "ROLE_DOWNLOAD"],
    tutorialElementKey: "hi-navbar-workflow",
    isExpFeature: true
  },
];

export const serviceUrls = {
  contentId: "Static/DashboardGlobals",
  settings: {
    jwtToken: null,
    successNotification: true,
    adminPaths: {
      users: "admin/users",
      profiles: "admin/profiles",
      roles: "admin/roles",
      organisations: "admin/organisations",
      services: "services.html",
    },
    services: "services",
    optionalReportParams: {},
    recursiveDirectoryLoad: false,
    HDI: {
      adhoc: {
        urls: {
          services: "/services",
          createDataSource: "/createDataSource",
          listDataSources: "/listDataSources",
          listLocations: "/listLocations",
          getResources: "/getResources",
          adhocReport: "/adhocReport",
        },
      },
    },
    DashboardGlobals: {
      solutionLoader: "getSolutionResources",
      resourceLoader: "/getEFWSolution.html",
      updateService: "/executeDatasource.html",
      chartingService: "/visualizeData.html",
      exportData: "/exportData.html",
      reportDownload: "/downloadReport",
      productInfo: "getProductInformation.html",
      sendMail: "/sendMail.html",
      updateEFWTemplate: "/updateEFWTemplate.html",
      "openHcr ": "/hcr-report.html",
      "editHcr ": "/hcr-report-edit.html",
      controllers: {
        efw: "/getEFWSolution.html",
        efwsr: "/executeSavedReport.html",
        efwfav: "/executeFavourite.html",
        report: "/hi.html",
      },
      saveReport: "/saveReport.html",
      fsop: "/fileSystemOperations",
      importFile: "/importFile.html",
      downloadEnableSavedReport: "/downloadEnableSavedReport.html",
      scheduling: {
        get: "/getScheduleData.html",
        update: "/updateScheduleData.html",
      },
      services: "/services",
      designerCreate: "/designer.html",
      designerEdit: "/designer-edit.html",
      ceReportCreate: "/ce-report-create.html",
      ceReportEdit: "/ce-report-edit.html",
      adhocEdit: "/adhoc/report-edit.html",
      datasourceCreate: "/adhoc/datasources.html",
      metadataEdit: "/adhoc/metadata-edit.html",
      metadataCreate: "/adhoc/metadata-create.html",
      adhocReportCreate: "/adhoc/report-create.html",
      helicalReportCreate: "/adhoc/helical-report.html",
      helicalReportEdit: "/adhoc/helical-report-edit.html",
      openAdhoc: "/hi.html",
      openEfw: "/hi.html",
      visualizeAdhoc: "/visualizeAdhoc.html",
    },
    exportType: {
      HCR: {
        PDF: {
          icon: "",
          name: "PDF document (.pdf)",
          tooltip: "Print as Portable Document Format",
          format: "pdf"
        },
        DOCX: {
          icon: "",
          name: "Microsoft Word (.docx)",
          tooltip: "Export as Microsoft Word Document",
          format: "docx"
        },
        CSV: {
          icon: "",
          name: "Comma Separated Values (.csv)",
          tooltip: "Export as Comma-Separated Values",
          format: "csv"
        },
        XLSX: {
          icon: "",
          name: "Microsoft Excel  Metadata(.xlsx)",
          tooltip: "Export as Microsoft Excel Workbook",
          format: "xlsx"
        },
        XLS: {
          icon: "",
          name: "Microsoft Excel (.xls)",
          tooltip: "Export as Microsoft Excel 97-2003 Workbook",
          format: "xls"
        },
        PNG: {
          icon: "",
          name: "PNG Image (.png)",
          tooltip: "Export as Portable Network Graphics Image",
          format: "png"
        },
        JPEG: {
          icon: "",
          name: "JPEG Image (.jpeg)",
          tooltip: "Export as JPEG Image",
          format: "jpeg"
        },
        JPG: {
          icon: "",
          name: "JPG Image (.jpg)",
          tooltip: "Export as JPG Image",
          format: "jpg"
        },
        XML: {
          icon: "",
          name: "Extensible Markup Language (.xml)",
          tooltip: "Export as XML Document",
          format: "xml"
        },
        HTML: {
          icon: "",
          name: "Web Page (.html)",
          tooltip: "Export as HTML Document",
          format: "html"
        },
        RTF: {
          icon: "",
          name: "Rich Text Format (.rtf)",
          tooltip: "Export as Rich Text Format",
          format: "rtf"
        },
        ODT: {
          icon: "",
          name: "OpenDocument Text (.odt)",
          tooltip: "Export as OpenDocument Text",
          format: "odt"
        },
        ODS: {
          icon: "",
          name: "OpenDocument Spreadsheet (.ods)",
          tooltip: "Export as OpenDocument Spreadsheet",
          format: "ods"
        },
        PPTX: {
          icon: "",
          name: "Microsoft PowerPoint (.pptx)",
          tooltip: "Export as Microsoft PowerPoint Presentation",
          format: "pptx"
        },
        TXT: {
          icon: "",
          name: "Plain Text (.txt)",
          tooltip: "Export as Plain Text",
          format: "txt"
        }
      },
      HReport: {
        CSV: {
          icon: "",
          name: "Comma Separated Values (.csv)",
          tooltip: "Export as Comma-Separated Values",
          format: "csv"
        },
        XLSX: {
          icon: "",
          name: "Microsoft Excel Metadata (.xlsx)",
          tooltip: "Export as Microsoft Excel Workbook",
          format: "xlsx"
        },
        PDF: {
          icon: "",
          name: "PDF document (.pdf)",
          tooltip: "Print as Portable Document Format",
          format: "pdf"
        },
        PPTX: {
          icon: "",
          name: "Microsoft PowerPoint (.pptx)",
          tooltip: "Export as Microsoft PowerPoint Presentation",
          format: "pptx"
        },
        PNG: {
          icon: "",
          name: "PNG Image (.png)",
          tooltip: "Export as Portable Network Graphics Image",
          format: "png"
        }
      },
      Efwdd: {
        PDF: {
          icon: "",
          name: "PDF document (.pdf)",
          tooltip: "Print as Portable Document Format",
          format: "pdf"
        },
        PPTX: {
          icon: "",
          name: "Microsoft PowerPoint (.pptx)",
          tooltip: "Export as Microsoft PowerPoint Presentation",
          format: "pptx"
        },
        PNG: {
          icon: "",
          name: "PNG Image (.png)",
          tooltip: "Export as Portable Network Graphics Image",
          format: "png"
        }
      }
    }
  },
};

export const localApplicationSettings = ({
  name,
  email,
  // initial_page,
  // dev_base_url,
  actualUserName,
  roles,
  profile,
  organization,
}) => {
  const returnObj = {
    userData: {
      serverTime: 1646287216766,
      expiryTime: 1646289016766,
      sessionUserName: "hiadmin",
      sessionUserEmail: "mailto:admin@helicalinsight.com",
      user: {
        name,
        email,
        actualUserName,
        roles,
        profile,
      },
      rootDirectoryPermission: "4",
      provideHTMLExport: false,
      enableReportSave: "false",
      defaultEmailResourceType: "url",
      saml: {
        enabled: false,
        logoutLink: "/saml/logout/?",
        samlIdValues: ["helicaltech"],
        samlIds: ["https://helicaltech.com/adfs/services/trust"],
        serverTime: 1647930070628,
        metadataDownload: "/saml/metadata?", // 'hard-coded'
      },
      baseURL: "",
    },
    contentId: "Static/DashboardGlobals",
    settings: {
      successNotification: true,
      jwtToken: null,
      adminPaths: {
        users: "admin/users",
        profiles: "admin/profiles",
        roles: "admin/roles",
        organisations: "admin/organisations",
        services: "services.html",
      },
      services: "services",
      optionalReportParams: {},
      recursiveDirectoryLoad: false,
      HDI: {
        adhoc: {
          urls: {
            services: "/services",
            createDataSource: "/createDataSource",
            listDataSources: "/listDataSources",
            listLocations: "/listLocations",
            getResources: "/getResources",
            adhocReport: "/adhocReport",
          },
        },
      },
      DashboardGlobals: {
        solutionLoader: "getSolutionResources",
        resourceLoader: "/getEFWSolution.html",
        updateService: "/executeDatasource.html",
        chartingService: "/visualizeData.html",
        exportData: "/exportData.html",
        reportDownload: "/downloadReport",
        productInfo: "getProductInformation.html",
        sendMail: "/sendMail.html",
        updateEFWTemplate: "/updateEFWTemplate.html",
        "openHcr ": "/hcr-report.html",
        "editHcr ": "/hcr-report-edit.html",
        controllers: {
          efw: "/getEFWSolution.html",
          efwsr: "/executeSavedReport.html",
          efwfav: "/executeFavourite.html",
          report: "/hi.html",
        },
        saveReport: "/saveReport.html",
        fsop: "/fileSystemOperations",
        importFile: "/importFile.html",
        downloadEnableSavedReport: "/downloadEnableSavedReport.html",
        scheduling: {
          get: "/getScheduleData.html",
          update: "/updateScheduleData.html",
        },
        services: "/services",
        designerCreate: "/designer.html",
        designerEdit: "/designer-edit.html",
        ceReportCreate: "/ce-report-create.html",
        ceReportEdit: "/ce-report-edit.html",
        adhocEdit: "/adhoc/report-edit.html",
        datasourceCreate: "/adhoc/datasources.html",
        metadataEdit: "/adhoc/metadata-edit.html",
        metadataCreate: "/adhoc/metadata-create.html",
        adhocReportCreate: "/adhoc/report-create.html",
        helicalReportCreate: "/adhoc/helical-report.html",
        helicalReportEdit: "/adhoc/helical-report-edit.html",
        openAdhoc: "/hi.html",
        openEfw: "/hi.html",
        visualizeAdhoc: "/visualizeAdhoc.html",
      },
    },
  };
  if (organization) returnObj["userData"]["user"].organization = organization;
  return returnObj;
};
