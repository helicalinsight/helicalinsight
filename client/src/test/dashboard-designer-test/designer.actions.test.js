import { configureStore } from "@reduxjs/toolkit";
import reducers from "../../redux/index";
import actionTypes from "../../redux/actions/actionTypes";
import mocks from "./designer-data-mock";
const { designer_initial_view_state } = mocks;

describe("Designer Actions", () => {
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
  test("Store Grid Items", (done) => {
    dispatch({ type: actionTypes.STORE_GRID_ITEMS_DATA, payload: [] });
    expect(store.getState().designer.present.gridItemsData).toEqual([]);
    done();
  });
  test("Toggle Dashboard Drawer", (done) => {
    dispatch({ type: actionTypes.TOGGLE_DASHBOARD_DRAWER, payload: true });
    expect(store.getState().designer.present.dashboardDrawerStatus).toEqual(true);
    done();
  });
});
