
import { v4 as uuidv4 } from "uuid";
import { configureStore } from '@reduxjs/toolkit';

import mocks, { intialState } from "./hreport.request.mock";
import { 
    metadata,getMetadataField,canvasField, getNumericMetadataField,getCanvasField_travel_details_source,
    getMetadataField_mode_of_payment,getMetadataField_travel_cost
} from "./utils/mock.data";
import reducers from "../../redux/index";
import actionTypes from "../../redux/actions/actionTypes";
describe("Helical Report Actions", () => {
    const store = configureStore({
		reducer: reducers,
		middleware: (getDefaultMiddleware) =>
			getDefaultMiddleware({
				thunk: {},
                immutableCheck: false,
                serializableCheck: false,
			})
	});
    let dispatch = store.dispatch
    test("jest example",async () => {
        expect(1+1).toBeTruthy();
    });
    test("Load intial page state", (done) => {
        let reportId = uuidv4()
        dispatch({type: actionTypes.LOAD_INTIAL_REPORT,payload:{reportId}})
        expect(store.getState().hreport.present.reports[0].id).toEqual(reportId)
        done();
    });
    test("Load metadata into page", (done) => {
        dispatch({type: actionTypes.LOAD_METADATA,payload:metadata})
        expect(store.getState().hreport.present.reports[0].metadata.uniqueId).toEqual("Metadata_1")
        done();
    }); 
    test("add field to columns pane", (done) => {
        dispatch({type: actionTypes.ADD_FIELD_TO_CANVAS,payload:getMetadataField()})
        expect(store.getState().hreport.present.reports[0].fields[0].column).toEqual("travel_details.destination")
        // start bug 4919
        expect(store.getState().hreport.present.reports[0].fields[0].autogen_alias).toEqual("destination")
        dispatch({type: actionTypes.ADD_FIELD_TO_CANVAS,payload:getMetadataField()})
        expect(store.getState().hreport.present.reports[0].fields[1].autogen_alias).toEqual("destination_1")
        dispatch({type: actionTypes.ADD_FIELD_TO_CANVAS,payload:getMetadataField()})
        expect(store.getState().hreport.present.reports[0].fields[2].autogen_alias).toEqual("destination_2")
        let destination_1_feild = store.getState().hreport.present.reports[0].fields[1]
        dispatch({type: actionTypes.REMOVE_FIELD_FROM_CANVAS,payload:{field:destination_1_feild}})
        dispatch({type: actionTypes.ADD_FIELD_TO_CANVAS,payload:getMetadataField()})
        expect(store.getState().hreport.present.reports[0].fields[2].autogen_alias).toEqual("destination_1")
        dispatch({type: actionTypes.ADD_FIELD_TO_CANVAS,payload:getNumericMetadataField()})
        expect(store.getState().hreport.present.reports[0].fields[3].autogen_alias).toEqual("sum_travel_cost")
        dispatch({type: actionTypes.ADD_FIELD_TO_CANVAS,payload:getNumericMetadataField()})
        expect(store.getState().hreport.present.reports[0].fields[4].autogen_alias).toEqual("sum_travel_cost_1")
        dispatch({type: actionTypes.ADD_FIELD_TO_CANVAS,payload:getNumericMetadataField()})
        expect(store.getState().hreport.present.reports[0].fields[5].autogen_alias).toEqual("sum_travel_cost_2")
        let sum_travel_cost_1_feild = store.getState().hreport.present.reports[0].fields[4]
        dispatch({type: actionTypes.REMOVE_FIELD_FROM_CANVAS,payload:{field:sum_travel_cost_1_feild}})
        dispatch({type: actionTypes.ADD_FIELD_TO_CANVAS,payload:getNumericMetadataField()})
        expect(store.getState().hreport.present.reports[0].fields[5].autogen_alias).toEqual("sum_travel_cost_1")
        // end bug 4919
        done();
    });
    test("create filter from canvas", (done) => {
        dispatch({type: actionTypes.CREATE_FILTER,payload:canvasField});
        let filter = store.getState().hreport.present.reports[0].filters[0]
        expect(filter.datePart).toEqual("year")
        expect(filter.databaseFunction.key).toEqual("sql.typeConversion.todatetime")
        expect(filter.databaseFunction.parameters.length).toEqual(1)
        expect(filter.databaseFunction.parameters[0].value).toEqual('dimdate.created_date')
        done();
    });
    test("create filter with db function", (done) => {
        dispatch({type: actionTypes.CREATE_FILTER,payload:getCanvasField_travel_details_source()})
        let filter = store.getState().hreport.present.reports[0].filters[0]
        expect(filter.condition).toEqual("EQUALS")
        done();
    });
    test("create filter with db function", (done) => {
        dispatch({type: actionTypes.CLEAR_FIELDS_SHELF,payload:"row"})
        dispatch({type: actionTypes.CLEAR_FIELDS_SHELF,payload:"column"})
        dispatch({type: actionTypes.ADD_FIELD_TO_CANVAS,payload:getMetadataField_mode_of_payment()})
        dispatch({type: actionTypes.ADD_FIELD_TO_CANVAS,payload:getMetadataField_travel_cost()})
        // expect(filter.condition).toEqual("EQUALS")
        done();
    });
});