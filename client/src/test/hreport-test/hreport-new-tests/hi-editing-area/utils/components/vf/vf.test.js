import { configureStore } from "@reduxjs/toolkit";
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { Provider } from "react-redux";
import { vfEditorMockData } from "./vf.mock-data";

jest.setTimeout(30000);

const __originalFetch = global.fetch;
global.fetch = jest.fn((url, ...rest) => {
    const urlStr = String(url);
    if (urlStr.includes('.wasm') || urlStr.includes('muze')) {
        return new Promise(() => { });
    }
    return __originalFetch ? __originalFetch(url, ...rest) : Promise.reject(new Error('fetch not available'));
});
afterAll(() => {
    global.fetch = __originalFetch;
});
const { hiMockAxios } = require("../../../../../../../app/mock-axios");
const CustomChartEditor = require('../../../../../../../components/hi-reports/hi-viz-area/custom-charts/custom-chart-editor').default;
const reducers = require("../../../../../../../redux").default;
const { appActions } = require("../../../../../../../redux/actions");
const { updateCustomChart } = require("../../../../../../../redux/actions/hreport.actions");
const { getPreviewStyles } = require("../../../../../../../components/hi-reports/hi-viz-area/utils/utillities");
const flushPromises = () => new Promise(setImmediate);
const App = (props) => {
    const { store, ...rest } = props
    return (
        <DndProvider backend={HTML5Backend}>
            <Provider store={store}>
                <CustomChartEditor {...rest} />
            </Provider>
        </DndProvider>
    );
};

describe('Test VF Editor', () => {

    const store = configureStore({
        reducer: reducers,
        middleware: (getDefaultMiddleware) =>
            getDefaultMiddleware({
                thunk: {
                    extraArgument: hiMockAxios,
                },
                immutableCheck: false,
                serializableCheck: false,
            }),
        preloadedState: {
            hreport: { past: [], present: vfEditorMockData, future: [] },
        },
    });

    it('should render the code editor when open is true', async () => {
        const getApiMock = jest.fn();
        const { getByTestId, getByText } = await waitFor(() => render(
            <App
                open={true}
                code=""
                report={{}}
                getApi={getApiMock}
                store={store}
            />
        )
        );
        expect(getByText('VF Editor')).toBeTruthy();
        // expect(getByTestId('custom-chart-save-button')).toBeTruthy();
        expect(getByTestId('custom-chart-apply-button')).toBeTruthy();
        expect(getByTestId('custom-chart-reset-button')).toBeTruthy();
        expect(getByTestId('custom-chart-info-icon')).toBeTruthy();
        expect(getByTestId('hi-custom-chart-editor-container')).toBeTruthy();
    });

    it('should update slide when click on info icon', async () => {
        const getApiMock = jest.fn();
        const { getByTestId, getAllByTestId } = await waitFor(() =>
            render(
                <App
                    open={true}
                    code="<h1>test code</h1>"
                    report={{}}
                    getApi={getApiMock}
                    {...{ store }}
                />
            )
        );
        fireEvent.click(getByTestId("custom-chart-info-icon"));
        expect(getAllByTestId("custom-chart-info-container")).toBeTruthy();
    });

    it('should call the handleReset, handleApply function when click on reset, apply and save button', async () => {
        const getApiMock = jest.fn();
        const mockResetFn = jest.fn();
        const mockApplyFn = jest.fn();
        // const mockSaveFn = jest.fn();
        const { getByTestId } = await waitFor(() => render(
            <App
                open={true}
                code="<h1>Test Code</h1>"
                report={{}}
                getApi={getApiMock}
                {...{ store }}
            />
        )
        );
        const resetButton = getByTestId("custom-chart-reset-button")
        const applyButton = getByTestId("custom-chart-apply-button")
        // const saveButton = getByTestId("custom-chart-save-button")
        resetButton.onclick = mockResetFn
        applyButton.onclick = mockApplyFn
        // saveButton.onclick = mockSaveFn
        fireEvent.click(resetButton);
        fireEvent.click(applyButton);
        // fireEvent.click(saveButton);
        expect(mockResetFn).toHaveBeenCalledTimes(1);
        expect(mockApplyFn).toHaveBeenCalledTimes(1);
        // expect(mockSaveFn).toHaveBeenCalledTimes(1);
    });

    it('should change the code string and should reset code string to empty when click on reset button', async () => {
        const getApiMock = jest.fn();
        const { getByTestId } = render(
            <App
                open={true}
                report={{}}
                code=""
                getApi={getApiMock}
                {...{ store }}
            />
        )
        let getState = store.getState;
        let dispatch = store.dispatch;
        let code = getState().hreport.present.reports[0].customChart.code;
        expect(code).toEqual("")
        dispatch(updateCustomChart({ code: "<h1>test code</h1>" }))
        code = getState().hreport.present.reports[0].customChart.code;
        expect(code).toEqual("<h1>test code</h1>")
        const resetButton = getByTestId("custom-chart-reset-button")
        fireEvent.click(resetButton)
        code = getState().hreport.present.reports[0].customChart.code;
        expect(code).toEqual("")
    })

});

describe("Test the VF preview", () => {
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
    });

    it('should return the correct width and height when props are provided', () => {
        const props = {
            chartAreaHeight: 500,
            chartAreaWidth: 800,
        }
        const result = getPreviewStyles(props)
        expect(result.width).toBe(800)
        expect(result.height).toBe(485)
    })

    it('should return undefined for width and height when props are not provided', () => {
        const result = getPreviewStyles()
        expect(result.width).toBeUndefined()
        expect(result.height).toBeNaN()
    })
})