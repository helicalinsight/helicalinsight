import '../../utils/mockJsdom'
import { configureStore } from '@reduxjs/toolkit';
import { render, screen, fireEvent,act,waitFor } from '@testing-library/react';
import { Provider } from 'react-redux'; 
import reducers from '../../../redux';
import { DndProvider } from 'react-dnd'; 
import { HTML5Backend } from 'react-dnd-html5-backend';
import { hiMockAxios } from '../../../app/mock-axios';
import ReportField from '../../../components/hi-reports/hi-fields-area/field';
import { addFieldToCanvas, loadIntialReport, toggleQueryEditor, updateAggregations } from '../../../redux/actions/hreport.actions';
import types from '../../../constants/metadata';
import { generateReport, openMetadata } from '../../../components/hi-reports/utils/base';
const crypto = require('crypto');


describe("Hreport visualisation", () => {
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

    test("jest example",async () => {
        expect(1+1).toBeTruthy();
    });
    test("open child report using drill through field",async () => {
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
        dispatch(loadIntialReport({ reportId:"test_1234" }));
        await waitFor(()=>openMetadata({
            "location": "naresh",
            "metadataFileName": "Metadata_1.metadata"
        }, dispatch))
        dispatch(toggleQueryEditor({fieldType:"column"}))
        dispatch(addFieldToCanvas({ 
            column: '"HIUSER"."travel_details"."travel_id"', 
            alias: "travel_id_test", 
            genre: types.CUSTOM_FORMULA 
        }))
        let customField = getState().hreport.present.reports[0].fields[0]
        await waitFor(()=> render(<DndProvider backend={HTML5Backend}>
			<Provider store={store}>
                <ReportField field={customField} />
            </Provider>
		</DndProvider>))
        dispatch(updateAggregations({ id:customField.id, group:"aggregate", key:"db.generic.aggregate.sum" }));
        let report = getState().hreport.present.reports[0]
        let formData = await generateReport({...report,getFormData:true})
        expect(formData.columns[0].aggregate).toEqual(true)
        expect(formData.functions.aggregate).toEqual([
            {
              column: '"HIUSER"."travel_details"."travel_id"',
              function: 'db.generic.aggregate.sum',
              alias: 'travel_id_test',
              custom: true
            }
        ]) 
    });
}); 