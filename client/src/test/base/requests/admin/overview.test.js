// eslint-disable-next-line jest/no-mocks-import
import { mockAxios, dispatch } from "../../../../__mocks__/axios";
import admin from "../../../../base/requests/admin.request";
import mocks from "./overview.mocks";
import { uriConfig } from "../../../../base/requests/admin.request";
const ntwFailRes = "Request failed";
const {
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
} = mocks;

//overview test cases

describe("Overview Page API Calls", () => {
  test("jest example",async () => {
    expect(1+1).toBeTruthy();
});

});

// System details test cases
