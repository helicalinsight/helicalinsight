import { configureStore } from "@reduxjs/toolkit";
import reducers from "../../../redux/index";
import actionTypes from "../../../redux/actions/actionTypes";
import mocks from "./admin-data-mock";

const { admin_initial_view_state } = mocks;

describe("Overview Actions", () => {
  const store = configureStore({
    reducer: reducers,
    middleware: (getDefaultMiddleware) =>
      getDefaultMiddleware({
        thunk: {},
        immutableCheck: false,
        serializableCheck: false,
      }),
  });
  let dispatch = store.dispatch;
  test("Disk data", (done) => {
    dispatch({
      type: actionTypes.STORE_DISK_DATA,
      payload: admin_initial_view_state.diskData,
    });
    expect(store.getState().admin.diskData).toEqual(
      admin_initial_view_state.diskData
    );
    done();
  });
  test("JVM Memory data", (done) => {
    dispatch({
      type: actionTypes.STORE_JVM_DATA,
      payload: admin_initial_view_state.jvmData,
    });
    expect(store.getState().admin.jvmData).toEqual(
      admin_initial_view_state.jvmData
    );
    done();
  });
  test("Temp Directory data", (done) => {
    dispatch({
      type: actionTypes.STORE_TEMP_DATA,
      payload: admin_initial_view_state.tempData,
    });
    expect(store.getState().admin.tempData).toEqual(
      admin_initial_view_state.tempData
    );
    done();
  });
  test("Cache Size", (done) => {
    dispatch({
      type: actionTypes.STORE_CACHE_SIZE,
      payload: admin_initial_view_state.cacheSize,
    });
    expect(store.getState().admin.cacheSize).toEqual(
      admin_initial_view_state.cacheSize
    );
    done();
  });
  test("Cached Reports", (done) => {
    dispatch({
      type: actionTypes.STORE_CACHED_REPORTS,
      payload: admin_initial_view_state.cachedReports,
    });
    expect(store.getState().admin.cachedReports).toEqual(
      admin_initial_view_state.cachedReports
    );
    done();
  });
  test("Data Sources", (done) => {
    dispatch({
      type: actionTypes.STORE_CACHED_DATASOURCES,
      payload: admin_initial_view_state.cachedDataSources,
    });
    expect(store.getState().admin.cachedDataSources).toEqual(
      admin_initial_view_state.cachedDataSources
    );
    done();
  });
  test("Current Level", (done) => {
    dispatch({
      type: actionTypes.STORE_CURRENT_LEVEL,
      payload: admin_initial_view_state.currentLevel,
    });
    expect(store.getState().admin.currentLevel).toEqual(
      admin_initial_view_state.currentLevel
    );
    done();
  });
  test("Log Options", (done) => {
    dispatch({
      type: actionTypes.STORE_LOG_OPTIONS,
      payload: admin_initial_view_state.logOptions,
    });
    expect(store.getState().admin.logOptions).toEqual(
      admin_initial_view_state.logOptions
    );
    done();
  });
  test("Product Data", (done) => {
    dispatch({
      type: actionTypes.STORE_PRODUCT_DATA,
      payload: admin_initial_view_state.productData,
    });
    expect(store.getState().admin.productData).toEqual(
      admin_initial_view_state.productData
    );
    done();
  });
});
