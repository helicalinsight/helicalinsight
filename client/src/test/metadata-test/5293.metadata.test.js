
import { configureStore } from '@reduxjs/toolkit';
import reducers from "../../redux/index";
import { store5293, } from './5293.mock'
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
        preloadedState: { metadata: store5293 }
    });
    test("merge sqlite tables", (done) => {
        expect(store.getState().metadata.present.allTablesKeys.length).toEqual(5)
        done();
    });

});