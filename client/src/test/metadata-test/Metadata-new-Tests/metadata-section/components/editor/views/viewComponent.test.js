import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../../../../../app/mock-axios";
import Views from "../../../../../../../components/hi-metadata/components/editor/views";
const flushPromises = () => new Promise(setImmediate);

const state = {
  activeView: null,
  dataFetchedFor: {
    getDatasource: true,
    joins: false,
    viewSessionVariables: true,
    listDataSources: true,
    "ps8z-ixl2-dpd0-in7p-7d": false,
    "88a4-15zy-6zi2-7ck2-wq": false,
    f03c8be87d50f9986799efbd8c2d5104_u45o5: false,
    "n7go-grv8-v0r8-ef3m-7o": false,
  },
  dataSourcesAddedToMetadata: [
    {
      id: "1000",
      type: "dynamicDataSource",
      baseType: "global.jdbc",
      catSchemaPredicted: false,
      sync: false,
      catalog: "",
      schema: "HIUSER",
      connId: "u45o5",
      dbId: "u45o5",
      classifier: "db.workflow",
      datasourceName: "hiee",
      dsKeyPath:
        "he12-bk9e-ropj-xx4b-my/ps8z-ixl2-dpd0-in7p-7d/88a4-15zy-6zi2-7ck2-wq",
      driverType: "Derby",
      database: "HIUSER",
    },
  ],
  views: [
    {
      name: "View 1",
      alias: "View 1",
      columns: {
        ID: {
          alias: "ID",
          id: "86211e34-471e-45a8-8927-6749a32d8bd6",
          columnId: "86211e34-471e-45a8-8927-6749a32d8bd6",
          type: {
            "java.lang.Float": "numeric",
          },
          category: "column",
          parentCategory: "view",
          columnKey: "ID",
          fullyQualifiedColumn: "View 1.ID",
          tableId: "96237e4e-9368-4374-89fc-23394125ac1c",
          connId: "u45o5",
          name: "ID",
          uniqueKey: "86211e34-471e-45a8-8927-6749a32d8bd6_u45o5",
          tableKey: "View 1",
        },
        CREATED_TIME: {
          alias: "CREATED_TIME",
          id: "1ca53d4b-4c71-40f4-8efd-536332980d9d",
          columnId: "1ca53d4b-4c71-40f4-8efd-536332980d9d",
          type: {
            "java.sql.Timestamp": "dateTime",
          },
          category: "column",
          parentCategory: "view",
          columnKey: "CREATED_TIME",
          fullyQualifiedColumn: "View 1.CREATED_TIME",
          tableId: "96237e4e-9368-4374-89fc-23394125ac1c",
          connId: "u45o5",
          name: "CREATED_TIME",
          uniqueKey: "1ca53d4b-4c71-40f4-8efd-536332980d9d_u45o5",
          tableKey: "View 1",
        },
        CACHE_KEY: {
          alias: "CACHE_KEY",
          id: "3a881aab-b827-4465-b2ce-67e6cffefca5",
          columnId: "3a881aab-b827-4465-b2ce-67e6cffefca5",
          type: {
            "java.lang.String": "text",
          },
          category: "column",
          parentCategory: "view",
          columnKey: "CACHE_KEY",
          fullyQualifiedColumn: "View 1.CACHE_KEY",
          tableId: "96237e4e-9368-4374-89fc-23394125ac1c",
          connId: "u45o5",
          name: "CACHE_KEY",
          uniqueKey: "3a881aab-b827-4465-b2ce-67e6cffefca5_u45o5",
          tableKey: "View 1",
        },
        PAGE: {
          alias: "PAGE",
          id: "339e455c-65c3-4f1b-b592-a276dc40ff39",
          columnId: "339e455c-65c3-4f1b-b592-a276dc40ff39",
          type: {
            "java.lang.Float": "numeric",
          },
          category: "column",
          parentCategory: "view",
          columnKey: "PAGE",
          fullyQualifiedColumn: "View 1.PAGE",
          tableId: "96237e4e-9368-4374-89fc-23394125ac1c",
          connId: "u45o5",
          name: "PAGE",
          uniqueKey: "339e455c-65c3-4f1b-b592-a276dc40ff39_u45o5",
          tableKey: "View 1",
        },
        STATUS: {
          alias: "STATUS",
          id: "d7d2f29b-49f8-4929-8d8d-d8a40c48e6bb",
          columnId: "d7d2f29b-49f8-4929-8d8d-d8a40c48e6bb",
          type: {
            "java.lang.String": "text",
          },
          category: "column",
          parentCategory: "view",
          columnKey: "STATUS",
          fullyQualifiedColumn: "View 1.STATUS",
          tableId: "96237e4e-9368-4374-89fc-23394125ac1c",
          connId: "u45o5",
          name: "STATUS",
          uniqueKey: "d7d2f29b-49f8-4929-8d8d-d8a40c48e6bb_u45o5",
          tableKey: "View 1",
        },
        CACHE_TYPE: {
          alias: "CACHE_TYPE",
          id: "ecfc15c1-0e04-4291-8579-06c42ec619da",
          columnId: "ecfc15c1-0e04-4291-8579-06c42ec619da",
          type: {
            "java.lang.String": "text",
          },
          category: "column",
          parentCategory: "view",
          columnKey: "CACHE_TYPE",
          fullyQualifiedColumn: "View 1.CACHE_TYPE",
          tableId: "96237e4e-9368-4374-89fc-23394125ac1c",
          connId: "u45o5",
          name: "CACHE_TYPE",
          uniqueKey: "ecfc15c1-0e04-4291-8579-06c42ec619da_u45o5",
          tableKey: "View 1",
        },
      },
      uuid: "n7go-grv8-v0r8-ef3m-7o",
      query: "select * from GENERIC_CACHE",
      queryType: "conditionIf",
      labels: [
        {
          name: "ID",
          type: "numeric",
          checked: true,
        },
        {
          name: "CREATED_TIME",
          type: "dateTime",
          checked: true,
        },
        {
          name: "CACHE_KEY",
          type: "text",
          checked: true,
        },
        {
          name: "PAGE",
          type: "numeric",
          checked: true,
        },
        {
          name: "STATUS",
          type: "text",
          checked: true,
        },
        {
          name: "CACHE_TYPE",
          type: "text",
          checked: true,
        },
        {
          name: "CACHE_VALUE",
          type: "other",
          checked: false,
        },
      ],
      category: "view",
      dataSource: {
        id: "1000",
        type: "dynamicDataSource",
        baseType: "global.jdbc",
        catSchemaPredicted: false,
        sync: false,
        catalog: "",
        schema: "HIUSER",
        connId: "u45o5",
        dbId: "u45o5",
        classifier: "db.workflow",
        datasourceName: "hiee",
        dsKeyPath:
          "he12-bk9e-ropj-xx4b-my/ps8z-ixl2-dpd0-in7p-7d/88a4-15zy-6zi2-7ck2-wq",
        driverType: "Derby",
        database: "HIUSER",
      },
      connId: "u45o5",
      schema: "HIUSER",
      catalog: "",
      error: false,
      validate: true,
      processedQuery:
        "select * from (select * from GENERIC_CACHE) foo fetch first 1 rows only",
      conditionIf_query: "select * from ${user}.id",
      groovy_query: 'check("${filter}.label","value")',
      id: "96237e4e-9368-4374-89fc-23394125ac1c",
      type: "view",
      uniqueKey: "96237e4e-9368-4374-89fc-23394125ac1c_u45o5",
      isModified: true,
    },
    {
      name: "View 2",
      alias: "View 2",
      columns: {
        EMAILADDRESS: {
          alias: "EMAILADDRESS",
          id: "b5ad4f91-b40e-46cf-a265-922b86941ab4",
          columnId: "b5ad4f91-b40e-46cf-a265-922b86941ab4",
          type: {
            "java.lang.String": "text",
          },
          category: "column",
          parentCategory: "view",
          columnKey: "EMAILADDRESS",
          fullyQualifiedColumn: "View 2.EMAILADDRESS",
          tableId: "611b0504-8254-4ca8-9826-51d7c7c3ca53",
          connId: "u45o5",
          name: "EMAILADDRESS",
          uniqueKey: "b5ad4f91-b40e-46cf-a265-922b86941ab4_u45o5",
          tableKey: "View 2",
        },
        ENABLED: {
          alias: "ENABLED",
          id: "27a996f5-f4d2-4909-b195-c39140a07065",
          columnId: "27a996f5-f4d2-4909-b195-c39140a07065",
          type: {
            "java.lang.Boolean": "boolean",
          },
          category: "column",
          parentCategory: "view",
          columnKey: "ENABLED",
          fullyQualifiedColumn: "View 2.ENABLED",
          tableId: "611b0504-8254-4ca8-9826-51d7c7c3ca53",
          connId: "u45o5",
          name: "ENABLED",
          uniqueKey: "27a996f5-f4d2-4909-b195-c39140a07065_u45o5",
          tableKey: "View 2",
        },
        ISEXTERNALLYAUTHENTICATED: {
          alias: "ISEXTERNALLYAUTHENTICATED",
          id: "62838b57-0cb2-4cab-b7d2-8db6e5192c89",
          columnId: "62838b57-0cb2-4cab-b7d2-8db6e5192c89",
          type: {
            "java.lang.Boolean": "boolean",
          },
          category: "column",
          parentCategory: "view",
          columnKey: "ISEXTERNALLYAUTHENTICATED",
          fullyQualifiedColumn: "View 2.ISEXTERNALLYAUTHENTICATED",
          tableId: "611b0504-8254-4ca8-9826-51d7c7c3ca53",
          connId: "u45o5",
          name: "ISEXTERNALLYAUTHENTICATED",
          uniqueKey: "62838b57-0cb2-4cab-b7d2-8db6e5192c89_u45o5",
          tableKey: "View 2",
        },
      },
      uuid: "sucq-i6es-rdiz-mp7f-8t",
      query: "select * from H_USERS",
      queryType: "conditionIf",
      labels: [
        {
          name: "ID",
          type: "numeric",
          checked: false,
        },
        {
          name: "EMAILADDRESS",
          type: "text",
          checked: true,
        },
        {
          name: "ENABLED",
          type: "boolean",
          checked: true,
        },
        {
          name: "ISEXTERNALLYAUTHENTICATED",
          type: "boolean",
          checked: true,
        },
        {
          name: "ORG_ID",
          type: "numeric",
          checked: false,
        },
        {
          name: "PASSWORD",
          type: "text",
          checked: false,
        },
        {
          name: "USERNAME",
          type: "text",
          checked: false,
        },
      ],
      category: "view",
      dataSource: {
        id: "1000",
        type: "dynamicDataSource",
        baseType: "global.jdbc",
        catSchemaPredicted: false,
        sync: false,
        catalog: "",
        schema: "HIUSER",
        connId: "u45o5",
        dbId: "u45o5",
        classifier: "db.workflow",
        datasourceName: "hiee",
        dsKeyPath:
          "he12-bk9e-ropj-xx4b-my/ps8z-ixl2-dpd0-in7p-7d/88a4-15zy-6zi2-7ck2-wq",
        driverType: "Derby",
        database: "HIUSER",
      },
      connId: "u45o5",
      schema: "HIUSER",
      catalog: "",
      error: false,
      validate: true,
      processedQuery:
        "select * from (select * from H_USERS) foo fetch first 1 rows only",
      id: "611b0504-8254-4ca8-9826-51d7c7c3ca53",
      type: "view",
      uniqueKey: "611b0504-8254-4ca8-9826-51d7c7c3ca53_u45o5",
      isModified: true,
    },
  ],
}

const App = () => {
  const store = configureStore({
    reducer: reducers,
    middleware: (getDefaultMiddleware) =>
      getDefaultMiddleware({
        thunk: {
          extraArgument: hiMockAxios,
        },
        immutableCheck: false,
        serializableCheck: false,
      }),
    preloadedState: { metadata: state },
  });

  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <Views />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering Views component", () => {
  document.createRange = () => {
    const range = new Range();

    range.getBoundingClientRect = () => {
      return {
        x: 0,
        y: 0,
        bottom: 0,
        height: 0,
        left: 0,
        right: 0,
        top: 0,
        width: 0,
        toJSON: () => { },
      };
    };

    range.getClientRects = () => {
      return {
        item: (index) => null,
        length: 0,
        *[Symbol.iterator]() { },
      };
    };

    return range;
  };

  test("Views component", async () => {
    await flushPromises(render(<App />));
    const row = screen.queryByTestId(/editor-views-multiple-conn-warning/i);

    expect(row).toBeFalsy();
  });
});
