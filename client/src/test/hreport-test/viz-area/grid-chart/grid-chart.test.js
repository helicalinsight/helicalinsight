
import { v4 as uuidv4 } from "uuid";
import { configureStore } from '@reduxjs/toolkit';
import { render, screen, fireEvent,act,waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import reducers from '../../../../redux';
import { hiMockAxios } from '../../../../app/mock-axios';
import actionTypes from "../../../../redux/actions/actionTypes";
import { openMetadata } from "../../../../components/hi-reports/utils/base";
import GridChart from "../../../../components/hi-reports/hi-viz-area/grid-chart/grid-chart";




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
        
    test("jest example",async () => {
        expect(1+1).toBeTruthy();
    });
    test("rendering grid chart with invalid config",async () => {
        let reportId = "test_1234"
        const marksList = [
            {
                "value": "_all_",
                "id": "bd6aa553-842b-4f68-b302-0a4c3ee5db8f",
                "subVizType": "point",
                "color": {
                    "fields": []
                },
                "size": {
                    "fields": []
                },
                "label": {
                    "fields": []
                },
                "tooltip": {
                    "fields": []
                },
                "shape": {
                    "fields": []
                },
                "detail": {
                    "fields": []
                }
            },
            {
                "value": "sum_travel_cost",
                "id": "b5e37af5-cc2c-4e9a-a884-1a41b4f90d5d",
                "subVizType": "",
                "color": {
                    "fields": []
                },
                "size": {
                    "fields": []
                },
                "label": {
                    "fields": []
                },
                "tooltip": {
                    "fields": []
                },
                "shape": {
                    "fields": []
                },
                "detail": {
                    "fields": []
                }
            },
            {
                "value": "sum_travelled_by",
                "id": "a9bbaea7-679b-4afb-ad06-2ed6549e0a06",
                "subVizType": "arc",
                "color": {
                    "fields": []
                },
                "size": {
                    "fields": []
                },
                "label": {
                    "fields": []
                },
                "tooltip": {
                    "fields": []
                },
                "shape": {
                    "fields": []
                },
                "detail": {
                    "fields": []
                }
            }
        ]
        
        await waitFor(() => render(<GridChart marksList={marksList} reportId={reportId} /> ))
        expect(screen.queryByTestId(/muze-chart-test_1234/i)).toBeTruthy()

    });  
});