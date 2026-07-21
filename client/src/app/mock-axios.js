import qs from "qs";
import {
  getReportState,
  getMetadata,
  getDataBaseFunctions,
  getDateFunctions,
  getReportData,
} from "./mock-data";
import { metadataMockData } from "../test/metadata-test/multi-connection/mock.data";
import {
  getInstantBINlpstringAPI,
  getInstantBIReportState,
  getInstantBISaveResponse,
} from "./instant-bi-mock-data";
import {
  getDatasourceGetContentsResponse,
  getDatasourceGetReportResponse,
} from "./datasource-mock-data";
import { designerEditAPIMockData } from "./dashboard-designer-mock-data";
import {getReleaseNotes} from "./admin-mock-data";

export const hiMockAxios = () => {
  return {
    instance: {
      post: function (url, data, config) {
        if (url?.url === "/hi-ee/downloadReport.html") {
          return new Promise((resolve, reject) => {
            resolve({
              data: new ArrayBuffer(),
            });
          });
        }
        let payload = qs.parse(data);
        let { type, serviceType, service, formData } = payload;
        formData = window.atob(formData);
        formData = JSON.parse(formData);
        return new Promise((resolve, reject) => {
          if (
            type === "adhoc" &&
            serviceType === "report" &&
            service === "getReportForEdit"
          ) {
            let { dir, file } = formData;
            if (dir && file) {
              return resolve({
                data: JSON.stringify(getReportState(formData)),
              });
            }
          }
          if (
            type === "adhoc" &&
            serviceType === "report" &&
            service === "getReport"
          ) {
            let { dir, file } = formData;
            if (dir && file) {
              return resolve({
                data: JSON.stringify(getReportState(formData)),
              });
            }
          }
          if (
            type === "adhoc" &&
            serviceType === "metadata" &&
            service === "get"
          ) {
            let { location, metadataFileName } = formData;
            if (
              location === "Gagan" &&
              metadataFileName === "Metadata_1.metadata"
            ) {
              return resolve({
                data: JSON.stringify(metadataMockData.getGetMetadata_1()),
              });
            } else if (
              location === "Gagan" &&
              metadataFileName === "Metadata_derby_pg.metadata"
            ) {
              console.log(
                "in get metata mock axios",
                location,
                metadataFileName
              );
              return resolve({
                data: JSON.stringify(
                  metadataMockData.getGetMetadata_derby_pg()
                ),
              });
            } else if (location && metadataFileName) {
              return resolve({
                data: JSON.stringify(getMetadata(formData)),
              });
            }
          }
          if (
            type === "adhoc" &&
            serviceType === "metadata" &&
            service === "getMetadataForEdit"
          ) {
            let { location, metadataFileName } = formData;
            if (
              location === "Gagan" &&
              metadataFileName === "Metadata_1.metadata"
            ) {
              return resolve({
                data: JSON.stringify(metadataMockData.getGetMetadata_1()),
              });
            } else if (
              location === "Gagan" &&
              metadataFileName === "Metadata_derby_pg.metadata"
            ) {
              console.log(
                "in get metata mock axios",
                location,
                metadataFileName
              );
              return resolve({
                data: JSON.stringify(
                  metadataMockData.getGetMetadata_derby_pg()
                ),
              });
            } else if (location && metadataFileName) {
              return resolve({
                data: JSON.stringify(getMetadata(formData)),
              });
            }
          }
          if (
            type === "adhoc" &&
            serviceType === "metadata" &&
            service === "getFunctions"
          ) {
            let {
              location,
              classifier,
              metadataFileName,
              metadataName,
              metadataDir,
            } = formData;
            if (location && classifier && metadataFileName) {
              return resolve({
                data: JSON.stringify(getDataBaseFunctions()),
              });
            }
          }
          if (
            type === "content" &&
            serviceType === "static" &&
            service === "getContents"
          ) {
            let { contentId } = formData;
            if (contentId === "Static/standardDate") {
              return resolve({
                data: JSON.stringify(getDateFunctions()),
              });
            }
            if (contentId === "Static/DataSourcesList") {
              return resolve({
                data: JSON.stringify(
                  metadataMockData.getStaticDataSourceList()
                ),
              });
            }
            if (contentId === "Static/semantictypes") {
              return resolve({
                data: JSON.stringify({
                  semanticTypes: [
                    {
                      label: "Temporal",
                      value: "TEMPORAL",
                      options: [
                        { label: "Date", value: "DATE" },
                        { label: "Time", value: "TIME" },
                        { label: "Datetime", value: "DATETIME" },
                      ],
                    },
                    {
                      label: "Numeric",
                      value: "NUMERIC",
                      options: [
                        { label: "Integer", value: "INTEGER" },
                        { label: "Decimal", value: "DECIMAL" },
                        { label: "Currency", value: "CURRENCY" },
                      ],
                    },
                    {
                      label: "Person",
                      value: "PERSON",
                      options: [
                        { label: "Person Name", value: "PERSON_NAME" },
                        { label: "Email", value: "EMAIL" },
                      ],
                    },
                  ],
                }),
              });
            }
            return resolve({
              data: JSON.stringify(getDatasourceGetContentsResponse(formData)),
            });
          }
          if (
            type === "adhoc" &&
            serviceType === "instant" &&
            service === "getReportForEdit"
          ) {
            let { dir, file } = formData;
            if (dir && file) {
              return resolve({
                data: JSON.stringify(getInstantBIReportState(formData)),
              });
            }
          }
          if (
            type === "adhoc" &&
            serviceType === "instant" &&
            service === "saveReport"
          ) {
            return resolve({
              data: JSON.stringify(getInstantBISaveResponse(formData)),
            });
          }
          if (
            type === "adhoc" &&
            serviceType === "report" &&
            service === "fetchData"
          ) {
            return resolve({
              data: JSON.stringify(getReportData(formData)),
            });
          }
          if (
            type === "monitor" &&
            serviceType === "system" &&
            service === "reportStats"
          ) {
            return resolve({
              data: JSON.stringify(getDatasourceGetReportResponse(formData)),
            });
          }
          if (
            type === "adhoc" &&
            serviceType === "metadata" &&
            service === "metadataWorkflow"
          ) {
            let {
              id,
              type,
              parameters: {
                fetchSchemas,
                fetchCatalogs,
                fetchTables,
                fetchData,
              },
            } = formData;
            console.log(
              "in metadata workflow",
              id,
              type,
              fetchSchemas,
              fetchCatalogs,
              fetchTables,
              fetchData
            );
            if (
              id === "1" &&
              type === "dynamicDataSource" &&
              fetchSchemas &&
              fetchCatalogs &&
              !fetchData
            ) {
              return resolve({
                data: JSON.stringify(metadataMockData.getMetadataWorkflow_1()),
              });
            } else if (
              id === "1" &&
              type === "dynamicDataSource" &&
              !fetchSchemas &&
              !fetchCatalogs &&
              fetchTables &&
              fetchData[0].schemas[0].name === "HIUSER"
            ) {
              return resolve({
                data: JSON.stringify(
                  metadataMockData.getMetadataWorkflow_1_schema_HIUSER()
                ),
              });
            } else if (
              id === "1000" &&
              type === "dynamicDataSource" &&
              fetchSchemas &&
              fetchCatalogs &&
              !fetchData
            ) {
              return resolve({
                data: JSON.stringify(
                  metadataMockData.getMetadataWorkflow_1000()
                ),
              });
            } else if (
              id === "1000" &&
              type === "dynamicDataSource" &&
              !fetchSchemas &&
              !fetchCatalogs &&
              fetchTables &&
              fetchData[0].schemas[0].name === "HIUSER"
            ) {
              return resolve({
                data: JSON.stringify(
                  metadataMockData.getMetadataWorkflow_1000_schema_HIUSER()
                ),
              });
            }
          }
          if (
            type === "adhoc" &&
            serviceType === "report" &&
            service === "getDerivedFormdata"
          ) {
            return resolve({
              data: JSON.stringify(getInstantBINlpstringAPI({ formData })),
            });
          }
          if (
            type === "dashboard" &&
            serviceType === "efwdd" &&
            service === "fetch"
          ) {
            return resolve({
              data: JSON.stringify(designerEditAPIMockData({ formData })),
            });
          }
          if (
            type === "monitor" &&
            serviceType === "system" &&
            service === "release"
          ) {
              return resolve({
                data: JSON.stringify(getReleaseNotes()),
              });
          }
          console.log("POST REQ", type, serviceType, service, formData);
          return new Promise((resolve, reject) => {
            resolve({
              data: JSON.stringify(getReportState()),
            });
          });
        });
      },
      get: (url, data, config) => {
        let { params } = data || {};
        if (url === "applicationSettings") {
          return new Promise((resolve, reject) => {
            resolve({
              data: JSON.stringify({
                userData: {
                  serverTime: 1666089184042,
                  expiryTime: 1666090984042,
                  sessionUserName: "hiadmin",
                  sessionUserEmail: "admin@helicalinsight.com",
                  user: {
                    name: "hiadmin",
                    email: "admin@helicalinsight.com",
                    actualUserName: "hiadmin",
                    roles: ["ROLE_ADMIN", "ROLE_USER", "ROLE_VIEWER"],
                    profile: [],
                  },
                  rootDirectoryPermission: "4",
                  provideHTMLExport: false,
                  enableReportSave: "false",
                  defaultEmailResourceType: "url",
                  saml: {
                    enabled: false,
                    logoutLink: "/saml/logout/?",
                  },
                  baseUrl: "http://localhost:7085/hi-ee/",
                },
                contentId: "Static/DashboardGlobals",
                settings: {
                  jwtToken: null,
                  adminPaths: {
                    users: "http://localhost:7085/hi-ee/admin/users",
                    profiles: "http://localhost:7085/hi-ee/admin/profiles",
                    roles: "http://localhost:7085/hi-ee/admin/roles",
                    organisations:
                      "http://localhost:7085/hi-ee/admin/organisations",
                    services: "http://localhost:7085/hi-ee/services.html",
                  },
                  services: "http://localhost:7085/hi-ee/services",
                  optionalReportParams: {},
                  recursiveDirectoryLoad: false,
                  HDI: {
                    adhoc: {
                      urls: {
                        services: "http://localhost:7085/hi-ee//services",
                        createDataSource:
                          "http://localhost:7085/hi-ee//createDataSource",
                        listDataSources:
                          "http://localhost:7085/hi-ee//listDataSources",
                        listLocations:
                          "http://localhost:7085/hi-ee//listLocations",
                        getResources:
                          "http://localhost:7085/hi-ee//getResources",
                        adhocReport: "http://localhost:7085/hi-ee//adhocReport",
                      },
                    },
                  },
                  DashboardGlobals: {
                    solutionLoader:
                      "http://localhost:7085/hi-ee//getSolutionResources",
                    resourceLoader:
                      "http://localhost:7085/hi-ee//getEFWSolution.html",
                    updateService:
                      "http://localhost:7085/hi-ee//executeDatasource.html",
                    chartingService:
                      "http://localhost:7085/hi-ee//visualizeData.html",
                    exportData: "http://localhost:7085/hi-ee//exportData.html",
                    reportDownload:
                      "http://localhost:7085/hi-ee//downloadReport.html",
                    productInfo:
                      "http://localhost:7085/hi-ee//getProductInformation.html",
                    sendMail: "http://localhost:7085/hi-ee//sendMail.html",
                    updateEFWTemplate:
                      "http://localhost:7085/hi-ee//updateEFWTemplate.html",
                    "openHcr ": "http://localhost:7085/hi-ee//hcr-report.html",
                    "editHcr ":
                      "http://localhost:7085/hi-ee//hcr-report-edit.html",
                    controllers: {
                      efw: "http://localhost:7085/hi-ee//getEFWSolution.html",
                      efwsr:
                        "http://localhost:7085/hi-ee//executeSavedReport.html",
                      efwfav:
                        "http://localhost:7085/hi-ee//executeFavourite.html",
                      report: "http://localhost:7085/hi-ee//hi.html",
                    },
                    saveReport: "http://localhost:7085/hi-ee//saveReport.html",
                    fsop: "http://localhost:7085/hi-ee//fileSystemOperations",
                    importFile: "http://localhost:7085/hi-ee//importFile.html",
                    downloadEnableSavedReport:
                      "http://localhost:7085/hi-ee//downloadEnableSavedReport.html",
                    scheduling: {
                      get: "http://localhost:7085/hi-ee//getScheduleData.html",
                      update:
                        "http://localhost:7085/hi-ee//updateScheduleData.html",
                    },
                    services: "http://localhost:7085/hi-ee//services",
                    designerCreate:
                      "http://localhost:7085/hi-ee//designer.html",
                    designerEdit:
                      "http://localhost:7085/hi-ee//designer-edit.html",
                    ceReportCreate:
                      "http://localhost:7085/hi-ee//ce-report-create.html",
                    ceReportEdit:
                      "http://localhost:7085/hi-ee//ce-report-edit.html",
                    adhocEdit:
                      "http://localhost:7085/hi-ee//adhoc/report-edit.html",
                    datasourceCreate:
                      "http://localhost:7085/hi-ee//adhoc/datasources.html",
                    metadataEdit:
                      "http://localhost:7085/hi-ee//adhoc/metadata-edit.html",
                    metadataCreate:
                      "http://localhost:7085/hi-ee//adhoc/metadata-create.html",
                    adhocReportCreate:
                      "http://localhost:7085/hi-ee//adhoc/report-create.html",
                    helicalReportCreate:
                      "http://localhost:7085/hi-ee//adhoc/helical-report.html",
                    helicalReportEdit:
                      "http://localhost:7085/hi-ee//adhoc/helical-report-edit.html",
                    openAdhoc: "http://localhost:7085/hi-ee//hi.html",
                    openEfw: "http://localhost:7085/hi-ee//hi.html",
                    visualizeAdhoc:
                      "http://localhost:7085/hi-ee//visualizeAdhoc.html",
                  },
                },
              }),
            });
          });
        }
        if (url === "listDataSources" && params.classifier === "all") {
          return new Promise((resolve, reject) => {
            resolve({
              data: JSON.stringify(metadataMockData.getListDSClassifierAll()),
            });
          });
        }
        console.log("GET REQ", url, data);
        return new Promise((resolve, reject) => {
          resolve({
            data: {},
          });
        });
      },
    },
  };
};
