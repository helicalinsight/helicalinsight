import "core-js/stable";
import "regenerator-runtime/runtime";
import { configureStore } from "@reduxjs/toolkit";
import { render, screen, fireEvent, act, waitFor } from "@testing-library/react";
import { Provider, useDispatch } from "react-redux";
import reducers from "../../../redux";
import axios from "axios";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { HelicalReports } from "../../../pages/helical-reports-page"
import { hiMockAxios } from '../../../app/mock-axios';
import { openMetadata } from "../../../components/hi-reports/utils/base";
import { appActions } from "../../../redux/actions";
import { addFieldToCanvas } from "../../../redux/actions/hreport.actions";
const crypto = require("crypto");
const flushPromises = () => new Promise(setImmediate);

const App = ({ store, ...props }) => {
    return (
        <DndProvider backend={HTML5Backend}>
            <Provider store={store}>
                <HelicalReports {...props} />
            </Provider>
        </DndProvider>
    );
};

describe("hreport undo redo functionality", () => {
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
        window.createObjectURL = jest.fn();
        window.HTMLElement.prototype.scrollBy = jest.fn();
        window.crypto = {};
        window.crypto.getRandomValues = arr => crypto.randomBytes(arr.length)
    });

    afterAll(() => {
        global.gc && global.gc()
      })
        
    test("hreport undo and redo both disabled", async () => {
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
         await flushPromises( render(<App store={store} />));
        const undoButton = document.querySelectorAll("[data-testid='hi-report-undo']");
        console.log({ undoButton })
        const undoButtonComputedStyle = window.getComputedStyle(undoButton[0]);
        expect(undoButtonComputedStyle.cursor).toBe('not-allowed');
        const redoButton = document.querySelectorAll("[data-testid='hi-report-redo']");
        const redoButtonComputedStyle = window.getComputedStyle(redoButton[0]);
        expect(redoButtonComputedStyle.cursor).toBe('not-allowed');
    });

    test("hreport undo and redo working test", async () => {
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
         await flushPromises( dispatch(appActions.setEditModeInfo({
            dir: "naresh/parent.hr", file: "parent.hr", extension: "hr"
        })))
         await flushPromises( render(<App store={store} />))
        dispatch(addFieldToCanvas({ column: "travel_date", table: "travel_details", addedAs: "column" }))
        const undoButton = document.querySelectorAll("[data-testid='hi-report-undo']");
        const redoButton = document.querySelectorAll("[data-testid='hi-report-redo']");
        const undoButtonComputedStyle = window.getComputedStyle(undoButton[0]);
        const redoButtonComputedStyle = window.getComputedStyle(redoButton[0]);
        expect(undoButtonComputedStyle.opacity).toBe('1');
        expect(redoButtonComputedStyle.cursor).toBe('not-allowed');
         await flushPromises( fireEvent.click(screen.queryByTestId(/hi-report-undo/i)));
        expect(undoButtonComputedStyle.opacity).toBe('1');
        const redoButtonComputedStyleAfterClick = window.getComputedStyle(redoButton[0]);
        expect(redoButtonComputedStyleAfterClick.opacity).toBe('1');
         await flushPromises( fireEvent.click(screen.queryByTestId(/hi-report-redo/i)));
        expect(redoButtonComputedStyle.cursor).toBe('not-allowed');
    });
});