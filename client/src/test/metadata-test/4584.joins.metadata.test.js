import { configureStore } from '@reduxjs/toolkit';
import reducers from "../../redux/index";
import actionTypes from "../../redux/actions/actionTypes";

describe("Metadata Actions", () => {
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
    test("Load intial page state", (done) => {
        dispatch({ type: actionTypes.METADATA.UPDATE_METADATA_STATE, payload: { 
            mode : 'updateTables',
            metadataForEdit : true,
            value : {},
            mergeType :'reload'
         } })
        expect(store.getState().metadata.present.dataFetchedFor.joins).toEqual(true)
        done();
    });
});