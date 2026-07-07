import { configureStore } from '@reduxjs/toolkit';
import { fireEvent, render, screen } from '@testing-library/react';
import "core-js/stable";
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import { Provider } from 'react-redux';
import { v4 as uuidv4 } from "uuid";
import { hiMockAxios } from '../../../app/mock-axios';
import VizListNew from "../../../components/hi-reports/hi-editing-area/components/viz-items/viz-list-new";
import reducers from "../../../redux";
import actionTypes from "../../../redux/actions/actionTypes";


const flushPromises = () => new Promise(setImmediate);

const App = (props) => {
    let { store, ...rest } = props
    return (
        <DndProvider backend={HTML5Backend}>
            <Provider store={store}>
                <VizListNew {...rest} />
            </Provider>
        </DndProvider>
    );
};


describe("Render new visualization area", () => {
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

    afterAll(() => {
        global.gc && global.gc()
    })


    test("render new viz list", async () => {
        let reportId = uuidv4()
        let vizRef = {}
        dispatch({ type: actionTypes.LOAD_INTIAL_REPORT, payload: { reportId } })
        await flushPromises(render(<App store={store} reportId={reportId} vizRef={vizRef} />))
        expect(screen.queryByTestId(/hi-viz-list-area-new/i)).toBeTruthy()
        expect(screen.queryByTestId(/hr-editing-area-viz-switch/i)).toBeTruthy()

        // render viz items headers
        expect(screen.queryByTestId(/hi-header-item-Grid_Chart/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-header-item-More_Chart/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-header-item-MapChart/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-header-item-Table/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-header-item-S2/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-header-item-VF/i)).toBeTruthy()

        // render viz item children
        fireEvent.click(screen.queryByTestId(/hi-header-item-Grid_Chart/i))
        expect(screen.queryByTestId(/hi-item-child-grid-bar/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-item-child-grid-line/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-item-child-grid-area/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-item-child-grid-point/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-item-child-grid-tick/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-item-child-grid-arc/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-item-child-grid-doughnut/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-item-child-grid-heatmap/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-item-child-grid-text/i)).toBeTruthy()

        fireEvent.click(screen.queryByTestId(/hi-header-item-More_Chart/i))
        expect(screen.queryByTestId(/hi-item-child-ant-bar/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-item-child-ant-line/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-item-child-ant-text/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-item-child-ant-arc/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-item-child-ant-area/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-item-child-ant-doughnut/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-item-child-ant-point/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-item-child-ant-waterfall/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-item-child-ant-radar/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-item-child-ant-progress/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-item-child-ant-relation/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-item-child-ant-calendar/i)).toBeTruthy()

        fireEvent.click(screen.queryByTestId(/hi-header-item-MapChart/i))
        expect(screen.queryByTestId(/hi-item-child-map-line/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-item-child-map-point/i)).toBeTruthy()
        expect(screen.queryByTestId(/hi-item-child-map-heatmap/i)).toBeTruthy()

        fireEvent.click(screen.queryByTestId(/hi-header-item-Table/i))
        expect(screen.queryByTestId(/hi-item-child-table/i)).toBeTruthy()

        fireEvent.click(screen.queryByTestId(/hi-header-item-S2/i))
        expect(screen.queryByTestId(/hi-item-child-s2/i)).toBeTruthy()

        fireEvent.click(screen.queryByTestId(/hi-header-item-VF/i))
        expect(screen.queryByTestId(/hi-item-child-vf/i)).toBeTruthy()

    })
});