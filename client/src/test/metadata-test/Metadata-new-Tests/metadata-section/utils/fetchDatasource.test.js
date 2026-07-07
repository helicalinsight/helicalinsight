import { fetchDatasource } from "../../../../../components/hi-metadata/utils";
import { updateDSToRenderWithCatalogSchema } from "../../../../../components/hi-metadata/utils/updateDSToRenderWithCatalogSchema";
import requests from "../../../../../base/requests";
import { mock_data } from "./fetchDatasource.mock.data";
jest.mock("uuid", () => ({
  v4: () => "12345",
}));

jest.mock("../../../../../../../client/src/base/requests", () => {
  const mockgetMetadataWorkflow = jest.fn();
  return {
    metadata: () => {
      return { getMetadataWorkflow: mockgetMetadataWorkflow };
    },
  };
});


describe("fetchDatasource function", () => {
    test("To test fetchDatasource on clicking", () => {
      let params;

      params = {
        setRequestId: jest.fn(),
        record: {
          data: {
            id: "1000",
            type: "dynamicDataSource",
          },
          dataSourceProvider: "tomcat",
          type: "dynamicDataSource",
          permissionLevel: 5,
          driver: "org.apache.derby.jdbc.AutoloadedDriver",
          name: "hiee",
          classifier: "global",
          dataSourceType: "Managed DataSource",
          category: "dataSource",
          children: [],
          driverType: "Derby",
          keyPath: "due1-yrsh-7v1y-kiwl-vr/9shu-t6w0-m397-qyfv-x7",
          key: "9shu-t6w0-m397-qyfv-x7",
          uuid: "9shu-t6w0-m397-qyfv-x7",
        },
        dispatch: jest.fn(),
        store: {},
        afterFetching: false,
        config: { edit: false, info: {} },
        updateDSToRenderToRedux: true,
        noCatSchema: false,
        fetchedMetadata: false,
        handleError: jest.fn(),
        getApi: jest.fn(),
        refreshDataSource: undefined,
      };

      const formData = {
        id: "1000",
        type: "dynamicDataSource",
        parameters: {
          fetchCatalogs: true,
          fetchSchemas: true,
          view: "tree",
          skipped: true,
        },
      };

      fetchDatasource(params);
      expect(requests.metadata().getMetadataWorkflow).toHaveBeenCalled();
      expect(requests.metadata().getMetadataWorkflow).toHaveBeenCalledWith(
        formData,
        expect.any(Function),
        expect.any(Function)
      );
    });

  test("To test updateDSToRenderWithCatalogSchema function in the fetchDatasource", () => {
    const result = mock_data.result;
    const record = mock_data.record;
    const datasourceListToRender = mock_data.datasourceListToRender;
    const recordNeeded = {};
    const info = {};
    const dispatch = jest.fn();
    const store = {};
    const afterFetching = false;
    const noCatSchema = false;
    const fetchedMetadata = false;
    const updateDSToRenderToRedux = true;

    const res = updateDSToRenderWithCatalogSchema({
      result,
      record,
      datasourceListToRender,
      recordNeeded,
      info,
      dispatch,
      store,
      afterFetching,
      noCatSchema,
      fetchedMetadata,
      updateDSToRenderToRedux,
    });
    const expectedResult = mock_data.expectedResult;

    expect(res).toEqual(expectedResult);

  });
});
