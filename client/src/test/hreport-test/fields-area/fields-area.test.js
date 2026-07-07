
import { v4 as uuidv4 } from "uuid";
import { configureStore } from '@reduxjs/toolkit';
import { render, screen, fireEvent, act, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import reducers from '../../../redux';
import FieldsArea from "../../../components/hi-reports/hi-fields-area/hi-fields-area"
import { hiMockAxios } from '../../../app/mock-axios';
import actionTypes from "../../../redux/actions/actionTypes";

const App = (props) => {
    let { store, ...rest } = props
    return (
        <DndProvider backend={HTML5Backend}>
            <Provider store={store}>
                <FieldsArea {...rest} />
            </Provider>
        </DndProvider>
    );
};


describe("Rendering filters pane", () => {
    beforeAll(() => {
        delete window.matchMedia
        window.matchMedia = (query) => ({
            matches: false,
            media: query,
            onchange: null,
            addListener: jest.fn(), // deprecated
            removeListener: jest.fn(), // deprecated
            addEventListener: jest.fn(),
            removeEventListener: jest.fn(),
            dispatchEvent: jest.fn(),
        })
        window.HTMLElement.prototype.scrollBy = jest.fn();
        document.createRange = () => {
            const range = new Range();

            range.getBoundingClientRect = () => {
                return {
                    x: 0,
                    y: 0,
                    bottom: 0,
                    height: 0,
                    left: 0,
                    right: 0,
                    top: 0,
                    width: 0,
                    toJSON: () => { }
                };
            };

            range.getClientRects = () => {
                return {
                    item: (index) => null,
                    length: 0,
                    *[Symbol.iterator]() { }
                };
            };

            return range;
        }
    });
    const store = configureStore({
        reducer: reducers,
        middleware: (getDefaultMiddleware) =>
            getDefaultMiddleware({
                thunk: {
                    extraArgument: hiMockAxios
                },
                immutableCheck: false,
                serializableCheck: false,
            }),
    });
    const dispatch = store.dispatch
    const getState = store.getState

    afterAll(() => {
        global.gc && global.gc()
      })
      
    test("jest example", async () => {
        expect(1 + 1).toBeTruthy();
    });
    test("rendering fields area", async () => {
        let reportId = uuidv4()
        dispatch({ type: actionTypes.LOAD_INTIAL_REPORT, payload: { reportId } })
        await waitFor(() => render(<App store={store} reportId={reportId} />))
        expect(screen.queryByTestId(/canvas-column-fields-list/i)).toBeTruthy()
        fireEvent.doubleClick(screen.queryByTestId(/canvas-column-fields-list/i))
        expect(screen.queryByText(/Alias/i)).toBeTruthy()
        expect(screen.queryByText(/Query/i)).toBeTruthy()
    });
});