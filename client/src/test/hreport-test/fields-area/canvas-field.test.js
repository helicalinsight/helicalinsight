import "core-js/stable";
import "regenerator-runtime/runtime";
import { v4 as uuidv4 } from "uuid";
import { configureStore } from '@reduxjs/toolkit';
import { render, screen, fireEvent,act,waitFor, cleanup } from '@testing-library/react';
import { Provider } from 'react-redux';
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import reducers from '../../../redux';
import FieldsArea from "../../../components/hi-reports/hi-fields-area/hi-fields-area"
import { hiMockAxios } from '../../../app/mock-axios';
import actionTypes from "../../../redux/actions/actionTypes";
import { openMetadata } from "../../../components/hi-reports/utils/base";
import { addFieldToReport, handleAddFeild, handleAddFilter } from "../../../components/hi-reports/utils/utilities";
import { addFieldToCanvas, updateAggregations } from "../../../redux/actions/hreport.actions";

const flushPromises = () => new Promise(setImmediate);

const App = (props) => {
    let {store,...rest} = props
	return (
        <DndProvider backend={HTML5Backend}>
            <Provider store={store}>
                <FieldsArea {...rest}  />
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
    test("rendering fields",async () => {
        let reportId = uuidv4()
        dispatch({type: actionTypes.LOAD_INTIAL_REPORT,payload:{reportId}})
        await flushPromises(openMetadata({location:"naresh",metadataFileName:"Metadata_1.metadata"},dispatch)) 
        await flushPromises( render(<App store={store} reportId={reportId} /> ))
        let report = getState().hreport.present.reports[0]
        let {metadata} = report
        let table = metadata.tables["meeting_details"]
        let column = table.columns["meeting_date"]
        dispatch(addFieldToCanvas({column,table,addedAs:"column"}))
        expect(screen.queryByTestId(/canvas-column-meeting_date/i)).toBeTruthy()
        fireEvent.doubleClick(screen.queryByTestId(/canvas-column-meeting_date/i))
        expect(screen.queryByTestId(/rename-field-input-meeting_date/i)).toBeTruthy()
        fireEvent.doubleClick(screen.queryByTestId(/rename-field-input-meeting_date/i))
        expect(getState().hreport.present.reports[0].customColumnData).toBeFalsy()
        expect(screen.queryByTestId(/hr-query-editor-container/i)).toBeFalsy()
        fireEvent.change(screen.queryByTestId(/rename-field-input-meeting_date/i), {target: { value: "modified_alias" }})
        fireEvent.blur(screen.queryByTestId(/rename-field-input-meeting_date/i));
        fireEvent.doubleClick(screen.queryByTestId(/canvas-column-modified_alias/i))
    });
    test("caanvas field label when changing aggregations",async () => {
        
        let reportId = uuidv4()
        dispatch({type: actionTypes.LOAD_INTIAL_REPORT,payload:{reportId}})
         await flushPromises(openMetadata({location:"naresh",metadataFileName:"Metadata_1.metadata"},dispatch)) 
         await flushPromises( render(<App store={store} reportId={reportId} /> ))
        let report = getState().hreport.present.reports[0]
        let {metadata} = report
        let table = metadata.tables["travel_details"]
        let column = table.columns["travel_cost"]
        dispatch(addFieldToCanvas({column,table,addedAs:"column"}))
        expect(screen.queryByTestId(/canvas-column-sum_travel_cost/i)).toBeTruthy()
        let field = getState().hreport.present.reports[0].fields[0]
        dispatch(updateAggregations({ id:field.id, group:"aggregate", key:'db.generic.aggregate.sum' }));
        expect(screen.queryByTestId(/canvas-column-travel_cost/i)).toBeTruthy()
        dispatch(updateAggregations({ id:field.id, group:"aggregate", key:'db.generic.aggregate.count' }));
        expect(screen.queryByTestId(/canvas-column-count_travel_cost/i)).toBeTruthy()
        dispatch(updateAggregations({ id:field.id, group:"aggregate", key:'db.generic.aggregate.sum' }));
        expect(screen.queryByTestId(/canvas-column-count_sum_travel_cost/i)).toBeTruthy()
    });
    test("rendering fields pane dropdwon",async () => {
        let reportId = uuidv4()
        dispatch({type: actionTypes.LOAD_INTIAL_REPORT,payload:{reportId}})
        await flushPromises( render(<App store={store} reportId={reportId} /> ))
        fireEvent.click(screen.queryByTestId(/column-shelf-dropdown/i))
        expect(screen.queryByText(/Show Hidden Columns/i)).toBeTruthy();
        fireEvent.click(screen.queryByTestId(/row-shelf-dropdown/i))
        expect(screen.queryByText(/Show Hidden Rows/i)).toBeTruthy();
    });
    test("renaming fields",async () => {
        let reportId = uuidv4()
        dispatch({type: actionTypes.LOAD_INTIAL_REPORT,payload:{reportId}})
        // await waitFor(()=>openMetadata({location:"naresh",metadataFileName:"Metadata_1.metadata"},dispatch)) 
        await flushPromises(openMetadata({location:"naresh",metadataFileName:"Metadata_1.metadata"},dispatch))
        await flushPromises( render(<App store={store} reportId={reportId} /> ))
        let report = getState().hreport.present.reports[0]
        let {metadata} = report
        let table = metadata.tables["meeting_details"]
        let column = table.columns["meeting_date"]
        dispatch(addFieldToCanvas({column,table,addedAs:"column"}))
        expect(screen.queryByTestId(/canvas-column-meeting_date/i)).toBeTruthy()
        fireEvent.doubleClick(screen.queryByTestId(/canvas-column-meeting_date/i))
        expect(screen.queryByTestId(/rename-field-input-meeting_date/i)).toBeTruthy()
        fireEvent.doubleClick(screen.queryByTestId(/rename-field-input-meeting_date/i))
        expect(getState().hreport.present.reports[0].customColumnData).toBeFalsy()
        expect(screen.queryByTestId(/hr-query-editor-container/i)).toBeFalsy()
        fireEvent.change(screen.queryByTestId(/rename-field-input-meeting_date/i), {target: { value: "modified_alias" }})
        fireEvent.blur(screen.queryByTestId(/rename-field-input-meeting_date/i));
        expect(screen.queryByTestId(/canvas-column-modified_alias/i)).toBeTruthy()
    });
});