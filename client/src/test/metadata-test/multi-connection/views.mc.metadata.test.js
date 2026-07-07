import "core-js/stable";
import "regenerator-runtime/runtime";
import { configureStore } from "@reduxjs/toolkit";
import { render, screen, fireEvent, act, waitFor, cleanup } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { BrowserRouter } from "react-router-dom";
import { HIMetadataPage } from "../../../pages";
import { hiMockAxios } from "../../../app/mock-axios";
import initialStates from "../../../redux/reducers/initialStates";
import { appActions } from "../../../redux/actions";
import { viewsMockAxios } from "./views.mock-axios";
import { mdStore1, mdStore2 } from "./view.mock-data";
const crypto = require("crypto");
const flushPromises = () => new Promise(setImmediate);
// const intialState = {
//     ...initialStates.metadataInitialState, editMdFromHomeInfo: {
//         dir: 'Gagan/Metadata_1_68.metadata',
//         file: 'Metadata_1_68.metadata'
//     },
//     mode: 'edit',
//     timeStamp: 1667204866784
// }

const initialState = {}

const App = ({ intialState = {}, store }) => {
    return (
        <DndProvider backend={HTML5Backend}>
            <Provider store={store}>
                <HIMetadataPage />
            </Provider>
        </DndProvider>
    );
};
afterEach(cleanup);

describe("Metadata multi connection views usecase", () => {
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
      
    // test("views should be visible in case of tsingle connection", async () => {
    //     const store = configureStore({
    //         reducer: reducers,
    //         middleware: (getDefaultMiddleware) =>
    //             getDefaultMiddleware({
    //                 thunk: {
    //                     extraArgument: viewsMockAxios,
    //                 },
    //                 immutableCheck: false,
    //                 serializableCheck: false,
    //             }),
    //         preloadedState: { metadata: mdStore2 }
    //     });
    //     await waitFor(() => render(<BrowserRouter>
    //         <App store={store} />
    //     </BrowserRouter>))
    //     expect(store.getState().metadata.dataSourcesAddedToMetadata.length).toBe(1)
    //     expect(screen.getByText('Views')).toBeTruthy()
    //     await waitFor(() => fireEvent.click(screen.getByText('Views')))
    //     expect(screen.getByTestId('editor-views-multiple-conn-warning')).toBeTruthy()
    //     expect(screen.getByTestId('view-button-to-add-new-view')).toBeTruthy()
    //     expect(screen.getByText('No views available')).toBeTruthy()
    // });

    test("views should not be visible in case of there is multi connections", async () => {
        const store = configureStore({
            reducer: reducers,
            middleware: (getDefaultMiddleware) =>
                getDefaultMiddleware({
                    thunk: {
                        extraArgument: viewsMockAxios,
                    },
                    immutableCheck: false,
                    serializableCheck: false,
                }),
            preloadedState: { metadata: mdStore1 }
        });
        await flushPromises( render(<BrowserRouter>
            <App store={store} />
        </BrowserRouter>))
        // expect(Object.keys(store.getState().metadata.tables).length).toBe(3)
        expect(store.getState().metadata.present.dataSourcesAddedToMetadata.length).toBe(2)
        // expect(store.getState().metadata.dataSource.length).toBe(2)
        expect(screen.getByText('Views')).toBeTruthy()
        await flushPromises( fireEvent.click(screen.getByText('Views')))
        expect(screen.getByTestId('editor-views-multiple-conn-warning')).toBeTruthy()
        expect(screen.getByText('Cannot use views with multiple connections')).toBeTruthy()
    });
});
