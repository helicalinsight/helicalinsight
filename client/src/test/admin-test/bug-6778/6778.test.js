import { configureStore } from "@reduxjs/toolkit";
import reducers from "../../../redux";
import actionTypes from "../../../redux/actions/actionTypes";

describe("Hreport Fetchdata Action", () => {
    const store = configureStore({
        reducer: reducers,
        middleware: (getDefaultMiddleware) =>
            getDefaultMiddleware({
                thunk: {},
                immutableCheck: false,
                serializableCheck: false,
            })
    });
    let dispatch = store.dispatch
    test("Without lastmodified", (done) => {
        dispatch({ type: actionTypes.LOAD_REPORT_DATA, payload: {reportData: {abc: 1}} })
        expect(store.getState().app.lastModified).toBeTruthy();
        done();
    });
    test("With lastmodified", (done) => {
        dispatch({ type: actionTypes.LOAD_REPORT_DATA, payload: {reportData: {abc: 1, lastModified: 123456}} })
        expect(store.getState().app.lastModified).toBe(123456);
        done();
    });
});