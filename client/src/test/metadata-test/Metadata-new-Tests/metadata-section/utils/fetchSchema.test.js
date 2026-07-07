import { fetchSchema } from "../../../../../components/hi-metadata/utils/fetchSchema";

import requests from "../../../../../base/requests";
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

describe("fetchSchema function", () => {
  test("To test fetchSchema function on clicking fetchSchema", () => {
    let params;
    params = {
      setRequestId: jest.fn(),
      record: {
        name: "HIUSER",
        children: [],
        keyPath:
          "lm1m-2ocn-vx82-bbly-3b/gz8r-v0dc-wldx-jorv-i5/9mbn-kwc4-frff-0ieu-ss",
        key: "9mbn-kwc4-frff-0ieu-ss",
        uuid: "9mbn-kwc4-frff-0ieu-ss",
        data: {
          id: "1000",
          type: "dynamicDataSource",
        },
        category: "schema",
        datasourceName: "hiee",
      },

      dispatch: jest.fn(),
      store: {},
      fetchedMetadata: false,
      info: false,
      afterFetching: false,
      updateDSToRenderToRedux: true,
      dsListToRender: false,
      catSchema: false,
      getApi: jest.fn(),
    };

    const formData = {
      id: "1000",
      type: "dynamicDataSource",
      parameters: {
        fetchTables: true,
        fetchData: [
          {
            schemas: [
              {
                name: "HIUSER",
              },
            ],
          },
        ],
      },
    };

    fetchSchema(params);
    expect(requests.metadata().getMetadataWorkflow).toHaveBeenCalled();
    expect(requests.metadata().getMetadataWorkflow).toHaveBeenCalledWith(
      formData,
      expect.any(Function),
      expect.any(Function)
    );
  });

  test("To test fetchSchema function on clicking fetchCatalog", () => {
    let params;
    params = {
      setRequestId: jest.fn(),
      record: {
        name: "pg_catalog",
        data: {
          id: "1001",
          type: "dynamicDataSource",
        },
        keyPath:
          "99x0-pvtt-nx5v-vjzt-5m/9kzu-8kyi-azuf-gkl6-2n/d0e7-f2i5-6lgf-dszr-xl/e2q2-ym3d-p2k7-5eov-7a",
        key: "e2q2-ym3d-p2k7-5eov-7a",
        uuid: "e2q2-ym3d-p2k7-5eov-7a",
        category: "schema",
        children: [],
        catalog: "booking1662345944710kenxhxumioueuzfj",
        datasourceName: "booking",
      },

      dispatch: jest.fn(),
      store: {},
      fetchedMetadata: false,
      info: false,
      afterFetching: false,
      updateDSToRenderToRedux: true,
      dsListToRender: false,
      catSchema: false,
      getApi: jest.fn(),
    };

    const formData = {
      id: "1001",
      type: "dynamicDataSource",
      parameters: {
        fetchTables: true,
        fetchData: [
          {
            schemas: [
              {
                name: "pg_catalog",
              },
            ],
            catalog: "booking1662345944710kenxhxumioueuzfj",
          },
        ],
      },
    };

    fetchSchema(params);
    expect(requests.metadata().getMetadataWorkflow).toHaveBeenCalled();
    expect(requests.metadata().getMetadataWorkflow).toHaveBeenCalledWith(
      formData,
      expect.any(Function),
      expect.any(Function)
    );
  });



});
