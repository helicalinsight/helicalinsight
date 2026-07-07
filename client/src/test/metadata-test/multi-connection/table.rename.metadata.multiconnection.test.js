import "core-js/stable";
import "regenerator-runtime/runtime";
import { configureStore } from "@reduxjs/toolkit";
import { render, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { BrowserRouter } from "react-router-dom";
import { HIMetadataPage } from "../../../pages";
import { hiMockAxios } from "../../../app/mock-axios";
import { metadataActions } from "../../../redux/actions";
import { preloadedStateMetadata } from "./table.rename.mock.data";
const crypto = require("crypto");


const App = ({ intialState = {}, store }) => {
    return (
        <DndProvider backend={HTML5Backend}>
            <Provider store={store}>
                <HIMetadataPage />
            </Provider>
        </DndProvider>
    );
};

describe("Metadata multi connection edit test", () => {
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
        window.crypto = {};
        window.console.error = jest.fn()
        window.crypto.getRandomValues = (arr) => crypto.randomBytes(arr.length);
    });
  
    afterAll(() => {
        global.gc && global.gc()
      })
      
    test("Metadata Module multi connection derby and postgres connections", async () => {
        const store = configureStore({
            reducer: reducers,
            preloadedState: {
                metadata: preloadedStateMetadata
            },
            middleware: (getDefaultMiddleware) =>
                getDefaultMiddleware({
                    thunk: {
                        extraArgument: hiMockAxios,
                    },
                    immutableCheck: false,
                    serializableCheck: false,
                }),
        });
        await waitFor(() => render(<BrowserRouter>
            <App store={store} />
        </BrowserRouter>))
        await waitFor(() => store.dispatch(metadataActions.handleTextEditingObj({
            tableId: 'bibn-4i89-n6lv-ic8r-2e'
        })))
        expect(Object.keys(store.getState().metadata.present.tables).length).toBe(2)
        expect(store.getState().metadata.present.textEditingObj.tableId).toBe('bibn-4i89-n6lv-ic8r-2e')


    });
});
