
import { v4 as uuidv4 } from "uuid";
import { configureStore } from '@reduxjs/toolkit';
import { render, screen, fireEvent,act,waitFor } from '@testing-library/react';
import { hiMockAxios } from "../../../app/mock-axios";
import { generateReport, openMetadata } from "../../../components/hi-reports/utils/base";
import reducers from "../../../redux";
import { getVisulisationState,saveFormData } from "../hreport.request.mock.js"
import { 
    formdata_usecase_1,formdata_usecase_2,formdata_with_filter_custom_values_1,formdata_with_filter_custom_values_2,
    formdata_with_filter_range_conditions1,formdata_with_filter_range_conditions2,
    formdata_with_month_year_db_function,formdata_with_db_function_custom_values,formdata_with_numeric_column_aggreate
} from "./mock.data"
import actionTypes from "../../../redux/actions/actionTypes";
import { saveDataBaseFunction } from "../../../components/hi-reports/hi-fields-area/utils/utilities";
import { addFieldToCanvas } from "../../../redux/actions/hreport.actions";
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
    test("formdata with numeric column having db function and apply aggregate before ",async () => {
        let reportId = uuidv4()
        dispatch({type: actionTypes.LOAD_INTIAL_REPORT,payload:{reportId}})
        await waitFor(()=>openMetadata({location:"naresh",metadataFileName:"Metadata_1.metadata"},dispatch)) 
        let {metadata,databaseFunctions,dateFunctions} = getState().hreport.present.reports[0]
        let table = metadata.tables["meeting_details"]
        let column = table.columns["meeting_date"]
        dispatch(addFieldToCanvas({column,table,addedAs:"column"}))
        console.log("store.getstate.columns",getState().hreport.present.reports[0].fields)
        let meeting_date_as_column = getState().hreport.present.reports[0].fields[0]
        meeting_date_as_column = {...meeting_date_as_column,functionsDefinition:"NOW()"}
        let fields = getState().hreport.present.reports[0].fields
        saveDataBaseFunction({databaseFunctions,fields,editingField:meeting_date_as_column}, dispatch)
        let avtiveReport = getState().hreport.present.reports[0]
        let formdata = await generateReport({...avtiveReport,"requestId":"test_1234",getFormData:true},{})
        expect(formdata.requestId).toEqual("test_1234");
        expect(formdata.columns[0].column).toEqual("HIUSER.meeting_details.meeting_date");
        expect(formdata.columns[0].alias).toEqual("meeting_date");
        expect(formdata.columns[0].databaseFunction.functionName).toEqual('sql.dateTime.now');
        expect(formdata.columns[0].databaseFunction.dataType).toEqual('dateTime');
        expect(formdata.columns[0].databaseFunction.parameters).toEqual({});
    });
}); 