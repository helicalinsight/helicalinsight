import { configureStore } from "@reduxjs/toolkit";
import { render, screen } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../../../../../app/mock-axios";
import ViewEditorDrawer from "../../../../../../../components/hi-metadata/components/editor/views/viewEditorDrawer";
const flushPromises = () => new Promise(setImmediate);

const state = {
  activeView: "5da08b97-306f-4249-91f2-0eaad0611d3e",
  viewName: "View 1",
  editViewsTempData: {
    "5da08b97-306f-4249-91f2-0eaad0611d3e": {
      type: "view",
      id: "10465",
      alias: "View 1",
      columns: {
        dim_id: {
          alias: "dim_id",
          fullyQualifiedColumn: "View 1.dim_id",
          id: "28493",
          defaultFunction: "db.generic.aggregate.sum",
          type: {
            "java.lang.Float": "numeric",
          },
          category: "column",
          columnKey: "dim_id",
          name: "dim_id",
          tableKey: "View 1",
          connId: "7926",
          tableId: "10465",
          parentCategory: "view",
          uniqueKey: "28493",
        },
        fiscal_year: {
          alias: "fiscal_year",
          fullyQualifiedColumn: "View 1.fiscal_year",
          id: "28494",
          defaultFunction: "db.generic.groupBy.group",
          type: {
            "java.sql.Date": "date",
          },
          category: "column",
          columnKey: "fiscal_year",
          name: "fiscal_year",
          tableKey: "View 1",
          connId: "7926",
          tableId: "10465",
          parentCategory: "view",
          uniqueKey: "28494",
        },
        modified_date: {
          alias: "modified_date",
          fullyQualifiedColumn: "View 1.modified_date",
          id: "28495",
          defaultFunction: "db.generic.groupBy.group",
          type: {
            "java.sql.Timestamp": "dateTime",
          },
          category: "column",
          columnKey: "modified_date",
          name: "modified_date",
          tableKey: "View 1",
          connId: "7926",
          tableId: "10465",
          parentCategory: "view",
          uniqueKey: "28495",
        },
        date_key: {
          alias: "date_key",
          fullyQualifiedColumn: "View 1.date_key",
          id: "28496",
          defaultFunction: "db.generic.groupBy.group",
          type: {
            "java.lang.String": "text",
          },
          category: "column",
          columnKey: "date_key",
          name: "date_key",
          tableKey: "View 1",
          connId: "7926",
          tableId: "10465",
          parentCategory: "view",
          uniqueKey: "28496",
        },
        day_number: {
          alias: "day_number",
          fullyQualifiedColumn: "View 1.day_number",
          id: "28497",
          defaultFunction: "db.generic.groupBy.group",
          type: {
            "java.lang.String": "text",
          },
          category: "column",
          columnKey: "day_number",
          name: "day_number",
          tableKey: "View 1",
          connId: "7926",
          tableId: "10465",
          parentCategory: "view",
          uniqueKey: "28497",
        },
        fiscal_month_name: {
          alias: "fiscal_month_name",
          fullyQualifiedColumn: "View 1.fiscal_month_name",
          id: "28498",
          defaultFunction: "db.generic.groupBy.group",
          type: {
            "java.lang.String": "text",
          },
          category: "column",
          columnKey: "fiscal_month_name",
          name: "fiscal_month_name",
          tableKey: "View 1",
          connId: "7926",
          tableId: "10465",
          parentCategory: "view",
          uniqueKey: "28498",
        },
        fiscal_month_label: {
          alias: "fiscal_month_label",
          fullyQualifiedColumn: "View 1.fiscal_month_label",
          id: "28499",
          defaultFunction: "db.generic.groupBy.group",
          type: {
            "java.lang.String": "text",
          },
          category: "column",
          columnKey: "fiscal_month_label",
          name: "fiscal_month_label",
          tableKey: "View 1",
          connId: "7926",
          tableId: "10465",
          parentCategory: "view",
          uniqueKey: "28499",
        },
        created_date: {
          alias: "created_date",
          fullyQualifiedColumn: "View 1.created_date",
          id: "28500",
          defaultFunction: "db.generic.groupBy.group",
          type: {
            "java.lang.String": "text",
          },
          category: "column",
          columnKey: "created_date",
          name: "created_date",
          tableKey: "View 1",
          connId: "7926",
          tableId: "10465",
          parentCategory: "view",
          uniqueKey: "28500",
        },
        created_time: {
          alias: "created_time",
          fullyQualifiedColumn: "View 1.created_time",
          id: "28501",
          defaultFunction: "db.generic.groupBy.group",
          type: {
            "java.lang.String": "text",
          },
          category: "column",
          columnKey: "created_time",
          name: "created_time",
          tableKey: "View 1",
          connId: "7926",
          tableId: "10465",
          parentCategory: "view",
          uniqueKey: "28501",
        },
        rating: {
          alias: "rating",
          fullyQualifiedColumn: "View 1.rating",
          id: "28502",
          defaultFunction: "db.generic.groupBy.group",
          type: {
            "java.lang.String": "text",
          },
          category: "column",
          columnKey: "rating",
          name: "rating",
          tableKey: "View 1",
          connId: "7926",
          tableId: "10465",
          parentCategory: "view",
          uniqueKey: "28502",
        },
      },
      name: "View 1",
      cacheId: "49909adac91fdeca0399c8a679349f78",
      keyName: "View 1",
      dataSource: {
        sync: false,
        id: "1601",
        catSchemaPredicted: false,
        catalog: "",
        schema: "HIUSER",
        type: "dynamicDataSource",
        baseType: "global.jdbc",
        dbId: "7926",
        driver: {
          data: {
            id: "1601",
            type: "dynamicDataSource",
          },
          dataSourceProvider: "tomcat",
          type: "dynamicDataSource",
          vendorName: null,
          baseType: "global.jdbc",
          permissionLevel: 5,
          driver: "org.apache.derby.jdbc.ClientDriver",
          name: "SampleTravelDataDerby",
          classifier: "global",
          dataSourceType: "Managed DataSource",
        },
        datasourceName: "SampleTravelDataDerby",
        driverType: "Derby",
        database: "HIUSER",
        connId: "7926",
        classifier: "db.generic",
        joinsFetched: true,
        oldDbId: "nkud5",
      },
      category: "view",
      selected: true,
      columnsFetched: true,
      keyPath: "d7a126d4-d906-42fa-a9fc-cdf459210d5e",
      uniqueKey: "10465",
      connId: "7926",
      isSaved: true,
      oldDbId: "nkud5",
      uuid: "5da08b97-306f-4249-91f2-0eaad0611d3e",
      query: 'select * from "dimdate"',
      queryType: "conditionIf",
      labels: [
        {
          name: "dim_id",
          type: "numeric",
          checked: true,
        },
        {
          name: "fiscal_year",
          type: "date",
          checked: true,
        },
        {
          name: "modified_date",
          type: "dateTime",
          checked: true,
        },
        {
          name: "date_key",
          type: "text",
          checked: true,
        },
        {
          name: "day_number",
          type: "text",
          checked: true,
        },
        {
          name: "fiscal_month_name",
          type: "text",
          checked: true,
        },
        {
          name: "fiscal_month_label",
          type: "text",
          checked: true,
        },
        {
          name: "created_date",
          type: "text",
          checked: true,
        },
        {
          name: "created_time",
          type: "text",
          checked: true,
        },
        {
          name: "rating",
          type: "text",
          checked: true,
        },
      ],
      isExecuted: true,
      error: false,
      validate: true,
      data: [
        {
          dim_id: 1,
          fiscal_year: "2013-01-01",
          modified_date: "2018-06-01 09:07:21.1",
          date_key: "2013-01-01",
          day_number: "1",
          fiscal_month_name: "3",
          fiscal_month_label: "FY2013-Jan",
          created_date: "2018-06-01 01:56:47",
          created_time: "09:01:24",
          rating: "0.1",
        },
        {
          dim_id: 2,
          fiscal_year: "2013-01-02",
          modified_date: "2018-06-07 19:07:21.1",
          date_key: "2013-01-02",
          day_number: "2",
          fiscal_month_name: "3",
          fiscal_month_label: "FY2013-Jan",
          created_date: "2018-06-07 11:56:47",
          created_time: "19:07:21",
          rating: "0.2",
        },
        {
          dim_id: 3,
          fiscal_year: "2013-01-03",
          modified_date: "2018-06-07 19:20:44.11",
          date_key: "2013-01-03",
          day_number: "3",
          fiscal_month_name: "3",
          fiscal_month_label: "FY2013-Jan",
          created_date: "2018-06-07 12:07:53",
          created_time: "19:20:44",
          rating: "0.3",
        },
        {
          dim_id: 4,
          fiscal_year: "2013-01-04",
          modified_date: "2018-06-11 11:46:09.12",
          date_key: "2013-01-04",
          day_number: "4",
          fiscal_month_name: "3",
          fiscal_month_label: "FY2013-Jan",
          created_date: "2018-06-07 12:10:06",
          created_time: "11:46:09",
          rating: "0.4",
        },
        {
          dim_id: 5,
          fiscal_year: "2013-01-05",
          modified_date: "2018-06-11 12:23:09.13",
          date_key: "2013-01-05",
          day_number: "5",
          fiscal_month_name: "3",
          fiscal_month_label: "FY2013-Jan",
          created_date: "2018-06-07 12:22:51",
          created_time: "12:23:09",
          rating: "0.5",
        },
        {
          dim_id: 6,
          fiscal_year: "2013-01-06",
          modified_date: "2018-06-11 13:11:26.15",
          date_key: "2013-01-06",
          day_number: "6",
          fiscal_month_name: "3",
          fiscal_month_label: "FY2013-Jan",
          created_date: "2018-06-07 12:37:15",
          created_time: "13:11:26",
          rating: "0.6",
        },
        {
          dim_id: 7,
          fiscal_year: "2013-01-07",
          modified_date: "2018-06-12 18:16:40.17",
          date_key: "2013-01-07",
          day_number: "7",
          fiscal_month_name: "3",
          fiscal_month_label: "FY2013-Jan",
          created_date: "2018-06-07 13:31:16",
          created_time: "18:16:40",
          rating: "0.7",
        },
        {
          dim_id: 8,
          fiscal_year: "2013-01-08",
          modified_date: "2018-06-12 19:29:17.22",
          date_key: "2013-01-08",
          day_number: "8",
          fiscal_month_name: "3",
          fiscal_month_label: "FY2013-Jan",
          created_date: "2018-06-07 17:08:27",
          created_time: "19:29:17",
          rating: "0.8",
        },
        {
          dim_id: 9,
          fiscal_year: "2013-01-09",
          modified_date: "2018-06-12 19:43:53.34",
          date_key: "2013-01-09",
          day_number: "9",
          fiscal_month_name: "3",
          fiscal_month_label: "FY2013-Jan",
          created_date: "2018-06-07 17:20:52",
          created_time: "19:43:53",
          rating: "0.9",
        },
        {
          dim_id: 10,
          fiscal_year: "2013-01-10",
          modified_date: "2018-06-12 19:47:13.4",
          date_key: "2013-01-10",
          day_number: "10",
          fiscal_month_name: "3",
          fiscal_month_label: "FY2013-Jan",
          created_date: "2018-06-07 17:37:22",
          created_time: "19:47:13",
          rating: "1",
        },
      ],
      processedQuery:
        'select * from (select * from "dimdate") foo fetch first 10 rows only',
    },
  },
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
        <ViewEditorDrawer />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering ViewEditorDrawer component", () => {
  beforeAll(() => {
    delete window.matchMedia;
    window.matchMedia = (query) => ({
      matches: false,
      media: query,
      onchange: null,
      addListener: jest.fn(), // deprecated
      removeListener: jest.fn(), // deprecated
      addEventListener: jest.fn(),
      removeEventListener: jest.fn(),
      dispatchEvent: jest.fn(),
    });
    window.HTMLElement.prototype.scrollBy = jest.fn();
  });

  afterAll(() => {
    global.gc && global.gc();
  });

  test("ViewEditorDrawer component", async () => {
    await flushPromises(render(<App />));
    const row = screen.queryByTestId(/view-editor-drawer/i);
    expect(row).toBeTruthy();
  });
});
