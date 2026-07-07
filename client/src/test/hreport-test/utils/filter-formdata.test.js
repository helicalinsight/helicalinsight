import "core-js/stable";
import { v4 as uuidv4 } from "uuid";
import { configureStore } from "@reduxjs/toolkit";
import {
  render,
  screen,
  fireEvent,
  act,
  waitFor,
} from "@testing-library/react";

import {
  generateReport,
  openMetadata,
} from "../../../components/hi-reports/utils/base";
import reducers from "../../../redux";
import { getVisulisationState } from "../hreport.request.mock.js";
import { formdata_with_numeric_column_aggreate } from "./mock.data";
import actionTypes from "../../../redux/actions/actionTypes";
import { hiMockAxios } from "../../../app/mock-axios";
import {
  getTableTree,
} from "../../../components/hi-reports/utils/utilities";
import { saveDataBaseFunction } from "../../../components/hi-reports/hi-fields-area/utils/utilities";
import {
  addFieldToCanvas,
  changeFilterCondition,
  createFilter,
  toggleFilterUnique,
  updateOrderBy,
} from "../../../redux/actions/hreport.actions";
import {
  fetchFilterValues,
  getMinMaxValues,
} from "../../../utils/filter-utils";
const flushPromises = () => new Promise(setImmediate);

describe("Helical Report Utilities", () => {
  const store = configureStore({
    reducer: reducers,
    middleware: (getDefaultMiddleware) => {
      return getDefaultMiddleware({
        thunk: {
          extraArgument: hiMockAxios,
        },
        immutableCheck: false,
        serializableCheck: false,
      });
    },
  });
  let dispatch = store.dispatch;
  let getState = store.getState;
  test("jest example", async () => {
    expect(1 + 1).toBeTruthy();
  });
  test("formdata with MONTHNAME db function", async () => {
    let reportId = uuidv4();
    dispatch({ type: actionTypes.LOAD_INTIAL_REPORT, payload: { reportId } });
    await waitFor(() =>
      openMetadata(
        { location: "naresh", metadataFileName: "Metadata_1.metadata" },
        dispatch
      )
    );
    let { metadata, databaseFunctions, dateFunctions } =
      getState().hreport.present.reports[0];
    let table = metadata.tables["meeting_details"];
    let column = table.columns["meeting_date"];
    dispatch(addFieldToCanvas({ column, table, addedAs: "column" }))
    let meeting_date_as_column = getState().hreport.present.reports[0].fields[0];
    meeting_date_as_column = {
      ...meeting_date_as_column,
      functionsDefinition: "MONTHNAME(meeting_date)",
    };
    let fields = getState().hreport.present.reports[0].fields;
    saveDataBaseFunction(
      { databaseFunctions, fields, editingField: meeting_date_as_column },
      dispatch
    );
    meeting_date_as_column = getState().hreport.present.reports[0].fields[0];
    dispatch(createFilter(meeting_date_as_column));
    let { filters } = getState().hreport.present.reports[0];
    fields = getState().hreport.present.reports[0].fields;
    let meeting_date_as_filter = filters[0];
    let formdata = await Promise.resolve(
      fetchFilterValues(
        {
          filter: meeting_date_as_filter,
          filters,
          debouncedSearchTerm: "De",
          dateFunctions,
          metadata,
          reportId,
          returnFormData: true,
          databaseFunctions
        },
        dispatch
      )
    );
    let dbFunc = {
      dataType: "text",
      functionName: "sql.dateTime.monthname",
      parameters: { datetime: "meeting_details.meeting_date" },
    };
    expect(formdata.columns.length).toEqual(2);
    expect(formdata.filters.length).toEqual(1);
    expect(formdata.columns[0].databaseFunction).toEqual(dbFunc);
    expect(formdata.columns[1].databaseFunction).toEqual(dbFunc);
    expect(formdata.filters[0].databaseFunction).toEqual(dbFunc);
  });
  test("date filter with make time filters", async () => {
    let reportId = uuidv4();
    dispatch({ type: actionTypes.LOAD_INTIAL_REPORT, payload: { reportId } });
    await waitFor(() =>
      openMetadata(
        { location: "naresh", metadataFileName: "Metadata_1.metadata" },
        dispatch
      )
    );
    let { metadata, databaseFunctions, dateFunctions } =
      getState().hreport.present.reports[0];
    let table = metadata.tables["meeting_details"];
    let column = table.columns["meeting_date"];
    dispatch(addFieldToCanvas({ column, table, addedAs: "column" }))
    let meeting_date_as_column = getState().hreport.present.reports[0].fields[0];
    meeting_date_as_column = {
      ...meeting_date_as_column,
      functionsDefinition: "MAKETIME('12','30','40')",
    };
    let fields = getState().hreport.present.reports[0].fields;
    saveDataBaseFunction(
      { databaseFunctions, fields, editingField: meeting_date_as_column },
      dispatch
    );
    meeting_date_as_column = getState().hreport.present.reports[0].fields[0];
    dispatch(createFilter(meeting_date_as_column));
    let { filters } = getState().hreport.present.reports[0];
    fields = getState().hreport.present.reports[0].fields;
    let meeting_date_as_filter = filters[0];
    // let formdata = await waitFor(() =>
    //   fetchFilterValues(
    //     {
    //       filter: meeting_date_as_filter,
    //       filters,
    //       debouncedSearchTerm: "12",
    //       dateFunctions,
    //       metadata,
    //       reportId,
    //       returnFormData: true,
    //       databaseFunctions
    //     },
    //     dispatch
    //   )
    // );
    let formdata = await Promise.resolve(fetchFilterValues(
        {
          filter: meeting_date_as_filter,
          filters,
          debouncedSearchTerm: "12",
          dateFunctions,
          metadata,
          reportId,
          returnFormData: true,
          databaseFunctions
        },
        dispatch
      )
    );
    let dbFunc = {
      functionName: "sql.dateTime.maketime",
      dataType: "time",
      parameters: {
        hour: "'12'",
        minute: "'30'",
        second: "'40'",
      },
    };
    let searchDbFunc = {
      functionName: "sql.text.timeToString",
      dataType: "text",
      parameters: {
        column: dbFunc,
      },
    };
    expect(formdata.columns.length).toEqual(2);
    expect(formdata.filters.length).toEqual(1);
    expect(formdata.columns[0].databaseFunction).toEqual(dbFunc);
    expect(formdata.columns[1].databaseFunction).toEqual(dbFunc);
    expect(formdata.filters[0].databaseFunction).toEqual(searchDbFunc);
    expect(formdata.filters[0].values).toEqual(["'%12%'"]);
    expect(formdata.filters[0].condition).toEqual("CUSTOM");
    expect(formdata.filters[0].customCondition).toEqual("like");
  });
  test("date filter with makedatetime filters", async () => {
    let reportId = uuidv4();
    dispatch({ type: actionTypes.LOAD_INTIAL_REPORT, payload: { reportId } });
    // await waitFor(() =>
    //   openMetadata(
    //     { location: "naresh", metadataFileName: "Metadata_1.metadata" },
    //     dispatch
    //   )
    // );
    await flushPromises(openMetadata(
        { location: "naresh", metadataFileName: "Metadata_1.metadata" },
        dispatch
      )
    );
    let { metadata, databaseFunctions, dateFunctions } =
      getState().hreport.present.reports[0];
    let table = metadata.tables["meeting_details"];
    let column = table.columns["meeting_date"];
    dispatch(addFieldToCanvas({ column, table, addedAs: "column" }))
    let meeting_date_as_column = getState().hreport.present.reports[0].fields[0];
    meeting_date_as_column = {
      ...meeting_date_as_column,
      functionsDefinition: "MAKEDATETIME('2013', '7', '15', '8', '15', '23.5')",
    };
    let fields = getState().hreport.present.reports[0].fields;
    saveDataBaseFunction(
      { databaseFunctions, fields, editingField: meeting_date_as_column },
      dispatch
    );
    meeting_date_as_column = getState().hreport.present.reports[0].fields[0];
    dispatch(createFilter(meeting_date_as_column));
    let { filters } = getState().hreport.present.reports[0];
    fields = getState().hreport.present.reports[0].fields;
    let meeting_date_as_filter = filters[0];
    let formdata = await Promise.resolve(
      fetchFilterValues(
        {
          filter: meeting_date_as_filter,
          filters,
          debouncedSearchTerm: "12",
          dateFunctions,
          metadata,
          reportId,
          returnFormData: true,
          databaseFunctions
        },
        dispatch
      )
    );
    let dbFunc = {
      functionName: "sql.dateTime.makedatetime",
      dataType: "dateTime",
      parameters: {
        year: "'2013'",
        month: " '7'",
        day: " '15'",
        hour: " '8'",
        minute: " '15'",
        second: " '23.5'",
      },
    };
    let searchDbFunc = {
      functionName: "sql.text.dateTimeToString",
      dataType: "text",
      parameters: {
        column: dbFunc,
      },
    };
    expect(formdata.columns.length).toEqual(2);
    expect(formdata.filters.length).toEqual(1);
    expect(formdata.columns[0].databaseFunction).toEqual(dbFunc);
    expect(formdata.columns[1].databaseFunction).toEqual(dbFunc);
    expect(formdata.filters[0].databaseFunction).toEqual(searchDbFunc);
    expect(formdata.filters[0].values).toEqual(["'%12%'"]);
    expect(formdata.filters[0].condition).toEqual("CUSTOM");
    expect(formdata.filters[0].customCondition).toEqual("like");
  });
  test("filter with intial orderby", async () => {
    let reportId = uuidv4();
    dispatch({ type: actionTypes.LOAD_INTIAL_REPORT, payload: { reportId } });
    await flushPromises(openMetadata(
        { location: "naresh", metadataFileName: "Metadata_1.metadata" },
        dispatch
      )
    );
    let { metadata, databaseFunctions, dateFunctions } =
      getState().hreport.present.reports[0];
    let table = metadata.tables["travel_details"];
    let column = table.columns["travel_id"];
    dispatch(addFieldToCanvas({ column, table, addedAs: "column" }))
    let travel_id_as_column = getState().hreport.present.reports[0].fields[0];
    dispatch(updateOrderBy({ id: travel_id_as_column.id, key: "desc" }));
    travel_id_as_column = getState().hreport.present.reports[0].fields[0];
    dispatch(createFilter(travel_id_as_column));
    let { filters } = getState().hreport.present.reports[0];
    let travel_id_as_filter = filters[0];
    let formdata = await Promise.resolve(
      fetchFilterValues(
        {
          filter: travel_id_as_filter,
          filters,
          dateFunctions,
          metadata,
          reportId,
          returnFormData: true,
        },
        dispatch
      )
    );
    expect(formdata.columns.length).toEqual(2);
    expect(formdata.columns[0].alias).toEqual("display");
    expect(formdata.functions.orderBy).toEqual([
      { alias: "display", custom: true, order: "desc" },
    ]);
  });
  test("formdata : range filter with database function", async () => {
    let reportId = uuidv4();
    dispatch({ type: actionTypes.LOAD_INTIAL_REPORT, payload: { reportId } });
    await flushPromises(openMetadata(
        { location: "naresh", metadataFileName: "Metadata_1.metadata" },
        dispatch
      )
    );
    let { metadata, databaseFunctions, dateFunctions } =
      getState().hreport.present.reports[0];
    let table = metadata.tables["travel_details"];
    let column = table.columns["booking_platform"];
    dispatch(addFieldToCanvas({ column, table, addedAs: "column" }))
    let booking_platfoem_as_column = getState().hreport.present.reports[0].fields[0];
    booking_platfoem_as_column = {
      ...booking_platfoem_as_column,
      functionsDefinition: "LENGTH(booking_platform)",
    };
    let fields = getState().hreport.present.reports[0].fields;
    saveDataBaseFunction(
      { databaseFunctions, fields, editingField: booking_platfoem_as_column },
      dispatch
    );
    booking_platfoem_as_column = getState().hreport.present.reports[0].fields[0];
    dispatch(createFilter(booking_platfoem_as_column));
    let { uid } = getState().hreport.present.reports[0].filters[0];
    dispatch(changeFilterCondition({ uid, condition: "IN_RANGE" }));
    let { filters } = getState().hreport.present.reports[0];
    fields = getState().hreport.present.reports[0].fields;
    let booking_platform_as_filter = getState().hreport.present.reports[0].filters[0];
    let formdata = await Promise.resolve(
      getMinMaxValues(
        {
          filter: booking_platform_as_filter,
          filters,
          databaseFunctions,
          dateFunctions,
          metadata,
          reportId,
          returnFormData: true,
        },
        dispatch
      )
    );
    let dbFunc = {
      dataType: "numeric",
      functionName: "sql.text.length",
      parameters: { string: "travel_details.booking_platform" },
    };
    expect(formdata.columns.length).toEqual(2);
    expect(formdata.columns[0].databaseFunction).toEqual(dbFunc);
    expect(formdata.columns[0].aggregate).toEqual(true);
    expect(formdata.columns[1].databaseFunction).toEqual(dbFunc);
    expect(formdata.columns[1].aggregate).toEqual(true);
    expect(formdata.functions.aggregate[0].function).toEqual(
      "db.generic.aggregate.min"
    );
    expect(formdata.functions.aggregate[1].function).toEqual(
      "db.generic.aggregate.max"
    );
  });
  test("filter with intial aggregation", async () => {
    let reportId = uuidv4();
    dispatch({ type: actionTypes.LOAD_INTIAL_REPORT, payload: { reportId } });
    await flushPromises(
      openMetadata(
        { location: "naresh", metadataFileName: "Metadata_1.metadata" },
        dispatch
      )
    );
    let { metadata, dateFunctions } = getState().hreport.present.reports[0];
    let table = metadata.tables["travel_details"];
    let column = table.columns["travel_cost"];
    dispatch(addFieldToCanvas({ column, table, addedAs: "column" }))
    let travel_cost_as_column = getState().hreport.present.reports[0].fields[0];
    travel_cost_as_column = getState().hreport.present.reports[0].fields[0];
    dispatch(createFilter(travel_cost_as_column));
    let { filters } = getState().hreport.present.reports[0];
    let travel_id_as_filter = filters[0];
    let formdata = await Promise.resolve(
      fetchFilterValues(
        {
          filter: travel_id_as_filter,
          filters,
          dateFunctions,
          metadata,
          reportId,
          returnFormData: true,
        },
        dispatch
      )
    );
    expect(formdata.columns.length).toEqual(2);
    expect(formdata.columns[0].alias).toEqual("display");
    expect(formdata.functions.aggregate[0].function).toEqual(
      "db.generic.aggregate.sum"
    );
    expect(formdata.functions.aggregate[0].column).toEqual(
      "HIUSER.travel_details.travel_cost"
    );
    expect(formdata.functions.aggregate[1].function).toEqual(
      "db.generic.aggregate.sum"
    );
    expect(formdata.functions.aggregate[1].column).toEqual(
      "HIUSER.travel_details.travel_cost"
    );
  });
  test("filter with out unique", async () => {
    let reportId = uuidv4();
    dispatch({ type: actionTypes.LOAD_INTIAL_REPORT, payload: { reportId } });
    await flushPromises(
      openMetadata(
        { location: "naresh", metadataFileName: "Metadata_1.metadata" },
        dispatch
      )
    );
    let { metadata, dateFunctions } = getState().hreport.present.reports[0];
    let table = metadata.tables["travel_details"];
    let column = table.columns["travel_medium"];
    dispatch(addFieldToCanvas({ column, table, addedAs: "column" }))
    let travel_medium_as_column = getState().hreport.present.reports[0].fields[0];
    travel_medium_as_column = getState().hreport.present.reports[0].fields[0];
    dispatch(createFilter(travel_medium_as_column));
    let { filters } = getState().hreport.present.reports[0];
    let travel_medium_as_filter = filters[0];
    let formdata = await Promise.resolve(
      fetchFilterValues(
        {
          filter: travel_medium_as_filter,
          filters,
          dateFunctions,
          metadata,
          reportId,
          returnFormData: true,
        },
        dispatch
      )
    );
    expect(formdata.columns.length).toEqual(2);
    expect(formdata.columns[0].alias).toEqual("display");
    expect(formdata.distinctResults).toEqual(true);
  });
  test("filter with out unique", async () => {
    let reportId = uuidv4();
    dispatch({ type: actionTypes.LOAD_INTIAL_REPORT, payload: { reportId } });
    await flushPromises(openMetadata(
        { location: "naresh", metadataFileName: "Metadata_1.metadata" },
        dispatch
      )
    );
    let { metadata, dateFunctions } = getState().hreport.present.reports[0];
    let table = metadata.tables["travel_details"];
    let column = table.columns["travel_medium"];
    dispatch(addFieldToCanvas({ column, table, addedAs: "column" }))
    let travel_medium_as_column = getState().hreport.present.reports[0].fields[0];
    travel_medium_as_column = getState().hreport.present.reports[0].fields[0];
    dispatch(createFilter(travel_medium_as_column));
    dispatch(toggleFilterUnique({ uid: getState().hreport.present.reports[0].filters[0].uid, reportId }));
    let { filters } = getState().hreport.present.reports[0];
    let travel_medium_as_filter = filters[0];
    let formdata = await Promise.resolve(
      fetchFilterValues(
        {
          filter: travel_medium_as_filter,
          filters,
          dateFunctions,
          metadata,
          reportId,
          returnFormData: true,
        },
        dispatch
      )
    );
    expect(formdata.columns.length).toEqual(2);
    expect(formdata.columns[0].alias).toEqual("display");
    expect(formdata.distinctResults).toEqual(undefined);
  });
});
