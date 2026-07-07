import { handleTableExpand } from "../../../../../components/hi-metadata/utils/handleTableExpand";
import metadataRequests from "../../../../../../../client/src/base/requests/metadata.requests";



const formData = {
  dataSource: {
    id: "1201",
    type: "dynamicDataSource",
    baseType: "global.jdbc",
    catSchemaPredicted: false,
    sync: false,
    catalog: "",
    schema: "",
    connId: "jefz1",
    dbId: "jefz1",
    classifier: "db.workflow",
    dsKeyPath: "0kps-ypcq-b428-lj02-9p/fwxj-6xvv-yyaq-j17r-u2",
    driverType: "Sqlite",
    database: "",
  },
  classifier: "db.workflow",
  metadata: {
    catalog: "",
    schema: "",
    table: "dimdate",
  },
  refresh: true,
  requestId : "12345"
};

jest.mock("uuid", () => ({
  v4: () => "12345",
}));



jest.mock(
  "../../../../../../../client/src/base/requests/metadata.requests",
  () => {
    const mockFetchColumns = jest.fn();
    return () => {
      return { fetchColumns: mockFetchColumns };
    };
  }
);

describe("handleTableExpand function", () => {
  let params;


   params = {
    setRequestId:  jest.fn(),
    currentTable: {
      id: "d324e793296ff76020c708f1c8fbb704",
      name: "dimdate",
      alias: "dimdate",
      dataSource: {
        id: "1201",
        type: "dynamicDataSource",
        baseType: "global.jdbc",
        catSchemaPredicted: false,
        sync: false,
        catalog: "",
        schema: "",
        connId: "jefz1",
        dbId: "jefz1",
        classifier: "db.workflow",
        datasourceName: "SampleTravelData",
        dsKeyPath: "0kps-ypcq-b428-lj02-9p/fwxj-6xvv-yyaq-j17r-u2",
        driverType: "Sqlite",
        database: "SampleTravelData",
      },
      category: "table",
      connId: "jefz1",
      keyPath:
        "0kps-ypcq-b428-lj02-9p/fwxj-6xvv-yyaq-j17r-u2/cv3q-7t0l-ovc9-i7s1-4s",
      uniqueKey: "d324e793296ff76020c708f1c8fbb704_jefz1",
      keyName: "dimdate",
      key: "d324e793296ff76020c708f1c8fbb704_jefz1",
      children: [],
    },
  
    dispatch: jest.fn(),
  
    dataSource: [
      {
        id: "1201",
        type: "dynamicDataSource",
        baseType: "global.jdbc",
        catSchemaPredicted: false,
        sync: false,
        catalog: "",
        schema: "",
        connId: "jefz1",
        dbId: "jefz1",
        classifier: "db.workflow",
        datasourceName: "SampleTravelData",
        dsKeyPath: "0kps-ypcq-b428-lj02-9p/fwxj-6xvv-yyaq-j17r-u2",
        driverType: "Sqlite",
        database: "",
      },
    ],
    tables: {
      dimdate: {
        id: "d324e793296ff76020c708f1c8fbb704",
        name: "dimdate",
        alias: "dimdate",
        dataSource: {
          id: "1201",
          type: "dynamicDataSource",
          baseType: "global.jdbc",
          catSchemaPredicted: false,
          sync: false,
          catalog: "",
          schema: "",
          connId: "jefz1",
          dbId: "jefz1",
          classifier: "db.workflow",
          datasourceName: "SampleTravelData",
          dsKeyPath: "0kps-ypcq-b428-lj02-9p/fwxj-6xvv-yyaq-j17r-u2",
          driverType: "Sqlite",
          database: "SampleTravelData",
        },
        category: "table",
        connId: "jefz1",
        keyPath:
          "0kps-ypcq-b428-lj02-9p/fwxj-6xvv-yyaq-j17r-u2/cv3q-7t0l-ovc9-i7s1-4s",
        uniqueKey: "d324e793296ff76020c708f1c8fbb704_jefz1",
        selected: true,
        keyName: "dimdate",
      },
    },
  
    duplicateTable: false,
    getApi: jest.fn(),
    mode: "create",
  };

  test("When clicking on fetch columns an api is getting called", () => {
    handleTableExpand(params);
    expect(metadataRequests().fetchColumns).toHaveBeenCalled();
    expect(metadataRequests().fetchColumns).toHaveBeenCalledWith(
      formData,
      expect.any(Function),
      expect.any(Function)
    );
  });
});
