const diskSpaceMock = {
  data: {
    status: 1,
    response: { totalDiskSize: 97915, usedSpace: 88745, freeSpace: 9169 },
  },
};

const jvmMemoryMock = {
  data: {
    status: 1,
    response: {
      totalMemory: 732,
      freeMemory: 251,
      usedMemory: 481,
      maxMemory: 931,
      unit: "MB",
    },
  },
};

const tempDirectoryMock = {
  data: {
    status: 1,
    response: {
      tempFileArray: [
        {
          fileName: "CONTAINS 2110261613_1635245031697.html",
          fileSize: 46477,
          lastModified: 1635245041000,
        },
        {
          fileName: "CONTAINS 2110261613_1635245031697.jpg",
          fileSize: 143841,
          lastModified: 1635245041000,
        },
        {
          fileName: "CONTAINS 2110261613_1635245031697.pdf",
          fileSize: 48135,
          lastModified: 1635245041000,
        },
        {
          fileName: "CONTAINS 2110261613_1635245031697.png",
          fileSize: 79027,
          lastModified: 1635245041000,
        },
        {
          fileName: "CONTAINS 2110261613_1635245031837.csv",
          fileSize: 1178,
          lastModified: 1635245031000,
        },
        {
          fileName: "CONTAINS 2110261613_1635245031937.xlsx",
          fileSize: 4294,
          lastModified: 1635245031000,
        },
      ],
    },
  },
};

const tempDirectoryDeleteMock = {
  data: {
    status: 1,
    response: { message: "Resource(s) deleted successfully" },
  },
};

const tempDirectoryDeleteSelectedMock = {
  data: {
    status: 1,
    response: { message: "Resource(s) deleted successfully" },
  },
};
const cacheSizeMock = {
  data: { status: 1, response: { size: 195 } },
};

const cacheSizeDeleteMock = {
  data: { status: 1, response: { message: "Resource cleaned successfully." } },
};

const cachedReportsMock = {
  data: {
    status: 1,
    response: {
      reportList: [
        {
          path: "1463377807724/1463377836985/e9be6771-995b-40eb-a01c-304857a100a1.metadata",
        },
        { path: "1571310105241/560cfea3-da16-4d3f-8614-c7523308c30f.metadata" },
        { path: "1635403726467/097c79e7-faf4-4985-9133-eb0c83221918.metadata" },
      ],
    },
  },
};

const cachedReportsDeleteMock = {
  data: {
    status: 1,
    response: { message: "All cache files deleted  successfully." },
  },
};

const cachedReportsDeleteSelectedMock = {
  data: {
    status: 1,
    response: { message: "Cache files cleaned successfully" },
  },
};

const dataSourcesMock = {
  data: {
    status: 1,
    response: {
      dataSources: [
        {
          baseType: "global.jdbc",
          dataSourceProvider: "tomcat",
          id: "1",
          name: "SampleTravelDataDerby",
          type: "dynamicDatasource",
          isDatabaseMetadataCached: "connection",
        },
        {
          baseType: "global.jdbc",
          dataSourceProvider: "tomcat",
          id: "1002",
          name: "Sample",
          type: "dynamicDatasource",
          isDatabaseMetadataCached: "partial",
        },
      ],
    },
  },
};

const dataSourcesDeleteSelectedMock = {
  data: {
    status: 1,
    response: {
      message:
        "The requested DataSource(s) is/are shutdown successfully. The database cache(if any) entries are also cleared. ",
    },
  },
};

const loggerMock = {
  data: { status: 1, response: { currentLevel: "ERROR" } },
};

const loggerSettingMock = {
  data: {
    status: 1,
    response: { message: "Log level is set to WARN", currentLevel: "WARN" },
  },
};

const reloadApplicationMock = {
  data: {
    status: 1,
    response: { message: "Application settings are reloaded" },
  },
};

const reloadValidationMock = {
  data: {
    status: 1,
    response: { message: "Successfully refreshed validation settings" },
  },
};

const reloadCacheMock = {
  data: {
    status: 1,
    response: { message: "Successfully refreshed cache settings" },
  },
};

const getProductInformationMock = {
  data: {
    "Product Type": "Business Intelligence Framework",
    Version: "5.0.0.19076 SNAPSHOT",
    Build: "R20212110_19076 SNAPSHOT",
    "Product Name": "Helical Insight",
    Expiration: "31/12/2021",
    "License Type": "Trial",
  },
};

const errorMock = {
  data: {
    status: 0,
    response: {
      message:
        "Error: FormValidationException: The formData does not have logLevel information",
    },
  },
};

const mocks = {
  errorMock,
  getProductInformationMock,
  diskSpaceMock,
  jvmMemoryMock,
  tempDirectoryMock,
  cacheSizeMock,
  tempDirectoryDeleteSelectedMock,
  tempDirectoryDeleteMock,
  cacheSizeDeleteMock,
  cachedReportsMock,
  cachedReportsDeleteMock,
  cachedReportsDeleteSelectedMock,
  dataSourcesMock,
  dataSourcesDeleteSelectedMock,
  loggerMock,
  loggerSettingMock,
  reloadApplicationMock,
  reloadValidationMock,
  reloadCacheMock,
};
export default mocks;
