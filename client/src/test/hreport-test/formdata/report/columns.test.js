import "core-js/stable";
import { v4 as uuidv4 } from "uuid";
import { configureStore } from '@reduxjs/toolkit';
import { waitFor } from '@testing-library/react';
import { hiMockAxios } from "../../../../app/mock-axios";
import { generateReport, openMetadata } from "../../../../components/hi-reports/utils/base";
import reducers from "../../../../redux";
import actionTypes from "../../../../redux/actions/actionTypes";
import { addFieldToCanvas } from "../../../../redux/actions/hreport.actions";

const flushPromises = () => new Promise(setImmediate);
describe("Helical Report Utilities", () => { 
    const store = configureStore({
		reducer: reducers,
		middleware: (getDefaultMiddleware) =>{
            return getDefaultMiddleware({
				thunk: {
                    // serializableCheck: false,
                    immutableCheck: false,
                    extraArgument: hiMockAxios,
                },
			})
        }
			
	});
    let dispatch = store.dispatch
    let getState = store.getState
    test("jest example",async () => {
        expect(1+1).toBeTruthy();
    });
    test("formdata for export table report as csv ",async () => {
        let reportId = uuidv4()
        dispatch({type: actionTypes.LOAD_INTIAL_REPORT,payload:{reportId}})
       await flushPromises(openMetadata({location:"naresh",metadataFileName:"Metadata_1.metadata"},dispatch)) 
        let {metadata} = getState().hreport.present.reports[0]
        let table = metadata.tables["travel_details"]
        let column = table.columns["destination"]
        dispatch(addFieldToCanvas({column,table,addedAs:"column"}))
        let avtiveReport = getState().hreport.present.reports[0]
        let formdata = await generateReport({...avtiveReport,"requestId":"test_1234",printFormat:"csv",getFormData:true},{})
        expect(formdata.requestId).toEqual("test_1234");
        expect(formdata.limitBy).toEqual(1000);
    });
    test("formdata for export table report as xls ",async () => {
        let reportId = uuidv4()
        dispatch({type: actionTypes.LOAD_INTIAL_REPORT,payload:{reportId}})
       await flushPromises(openMetadata({location:"naresh",metadataFileName:"Metadata_1.metadata"},dispatch)) 
        let {metadata} = getState().hreport.present.reports[0]
        let table = metadata.tables["travel_details"]
        let column = table.columns["destination"]
        dispatch(addFieldToCanvas({column,table,addedAs:"column"}))
        let avtiveReport = getState().hreport.present.reports[0]
        let formdata = await generateReport({...avtiveReport,"requestId":"test_1234",printFormat:"xls",getFormData:true},{})
        expect(formdata.requestId).toEqual("test_1234");
        expect(formdata.limitBy).toEqual(1000);
    });
    test("formdata for export table report as email ",async () => {
        let reportId = uuidv4()
        dispatch({type: actionTypes.LOAD_INTIAL_REPORT,payload:{reportId}})
       await flushPromises(openMetadata({location:"naresh",metadataFileName:"Metadata_1.metadata"},dispatch)) 
        let {metadata} = getState().hreport.present.reports[0]
        let table = metadata.tables["travel_details"]
        let column = table.columns["destination"]
        dispatch(addFieldToCanvas({column,table,addedAs:"column"}))
        let avtiveReport = getState().hreport.present.reports[0]
        let formdata = await generateReport({...avtiveReport,"requestId":"test_1234",printFormat:"email",getFormData:true},{})
        expect(formdata.requestId).toEqual("test_1234");
        expect(formdata.limitBy).toEqual(1000);
    });
}); 