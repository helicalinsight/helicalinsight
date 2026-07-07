import { configureStore } from '@reduxjs/toolkit';
import reducers from "../../redux/index";
import {store5303, actionData} from './5303.mock.data'
import axios from 'axios';


describe("5303 metadata connection testing", () => {
    const store = configureStore({
        reducer: reducers,
        middleware: (getDefaultMiddleware) =>
            getDefaultMiddleware({
                thunk: {
                    extraArgument: axios
                },
                immutableCheck: false,
                serializableCheck: false,
            }),
        preloadedState: { metadata: store5303 }
    });
    let dispatch = store.dispatch
    test("merge sqlite tables", (done) => {
        dispatch(actionData)
        expect(store.getState().metadata.present.dataSourcesAddedToMetadata.length).toEqual(1)
        done();
    });
    
});