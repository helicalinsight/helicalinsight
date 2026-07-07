
import { v4 as uuidv4 } from "uuid";
import { configureStore } from '@reduxjs/toolkit';
import reducers from "../../redux/index";
import actionTypes from "../../redux/actions/actionTypes";
import {store5304, actionData} from './5304.mock.data'
import axios from 'axios';


describe("5304 metadata tables testing", () => {
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
        preloadedState: { metadata: store5304 }
    });
    let dispatch = store.dispatch;
    test("checking sqlite connection with table delete", (done) => {
        dispatch(actionData)
        expect(store.getState().metadata.present.dataSource.length).toEqual(1)
        done();
    });
    
});