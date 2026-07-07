
import { v4 as uuidv4 } from "uuid";
import { configureStore } from '@reduxjs/toolkit';
import { render, screen, fireEvent,act,waitFor } from '@testing-library/react';
// import { Provider } from 'react-redux';
// import { DndProvider } from "react-dnd";
// import { HTML5Backend } from "react-dnd-html5-backend";
import reducers from '../../../redux';
// import Filters from "../../../components/hi-reports/hi-editing-area/components/filters/filters"
import { hiMockAxios } from '../../../app/mock-axios';
import actionTypes from "../../../redux/actions/actionTypes";
import {addFieldToCanvas, toggleFloating} from "../../../redux/actions/hreport.actions"; 
import { generateReport, openMetadata } from "../../../components/hi-reports/utils/base";
import { getFloatingType } from "../../../utils/filter-utils";
import { saveDataBaseFunction } from "../../../components/hi-reports/hi-fields-area/utils/utilities";
import { geographicalSubTypes } from "../../../components/hi-reports/utils/constants";

// const App = (props) => {
//     let {store,...rest} = props
// 	return (
//         <DndProvider backend={HTML5Backend}>
//             <Provider store={store}>
//                 <Filters {...rest}  />
//             </Provider>
//         </DndProvider>
// 	);
// };


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
    test("canvas field menu options",async () => {
        let reportId = uuidv4()
        dispatch({type: actionTypes.LOAD_INTIAL_REPORT,payload:{reportId}})
        await waitFor(()=>openMetadata({location:"naresh",metadataFileName:"Metadata_1.metadata"},dispatch)) 
        let {metadata} = getState().hreport.present.reports[0]
        let table = metadata.tables["travel_details"]
        let column = table.columns["source_id"]
        dispatch(addFieldToCanvas({column,table}))
        let {fields} = getState().hreport.present.reports[0]
        expect(getState().hreport.present.reports[0].fields.length).toEqual(1)
        expect(getState().hreport.present.reports[0].fields[0].floatingType).toEqual("")
        dispatch(toggleFloating({reportId,floatingType:"discrete",id:fields[0].id}))
        expect(getState().hreport.present.reports[0].fields[0].floatingType).toEqual("discrete")
        dispatch(toggleFloating({reportId,floatingType:"continous",id:fields[0].id}))
        expect(getState().hreport.present.reports[0].fields[0].floatingType).toEqual("continous")
    });   
    test("canvas field menu options with db function",async () => {
        let reportId = uuidv4()
        dispatch({type: actionTypes.LOAD_INTIAL_REPORT,payload:{reportId}})
        await waitFor(()=>openMetadata({location:"naresh",metadataFileName:"Metadata_1.metadata"},dispatch)) 
        let {metadata,databaseFunctions} = getState().hreport.present.reports[0]
        let table = metadata.tables["travel_details"]
        let column = table.columns["booking_platform"]
        dispatch(addFieldToCanvas({column,table}))
        let {fields} = getState().hreport.present.reports[0]
        let booking_platform_as_column = getState().hreport.present.reports[0].fields[0];
        booking_platform_as_column = {
        ...booking_platform_as_column,
        functionsDefinition: "LEFT(booking_platform)",
        };
        saveDataBaseFunction(
            { databaseFunctions, fields, editingField: booking_platform_as_column },
            dispatch
        );
        fields = getState().hreport.present.reports[0].fields;
        expect(getState().hreport.present.reports[0].fields.length).toEqual(1)
        expect(getFloatingType(fields[0]).floatingType).toEqual("discrete")
    });  
    test("canvas field menu options with db function",async () => {
        let reportId = uuidv4()
        dispatch({type: actionTypes.LOAD_INTIAL_REPORT,payload:{reportId}})
        await waitFor(()=>openMetadata({location:"naresh",metadataFileName:"Metadata_1.metadata"},dispatch)) 
        let {metadata,databaseFunctions} = getState().hreport.present.reports[0]
        let table = metadata.tables["geo_cordinates"]
        let column = table.columns["longitude"]
        dispatch(addFieldToCanvas({column,table}))
        let {fields} = getState().hreport.present.reports[0]
        let longitude_as_column = getState().hreport.present.reports[0].fields[0];
        longitude_as_column = {
        ...longitude_as_column,
        functionsDefinition: "CAST(sum_of_longitude,DECIMAL(20,3))",
        };
        saveDataBaseFunction(
            { databaseFunctions, fields, editingField: longitude_as_column },
            dispatch
        );
        
        let activeReport = getState().hreport.present.reports[0];
        let formData = await Promise.resolve(generateReport({...activeReport,getFormData:true},dispatch)) 
        expect(formData.columns[0].databaseFunction).toEqual({
            functionName: 'sql.typeConversion.cast',
            dataType: 'text',
            parameters: { column: 'sum_of_longitude', dataType: 'DECIMAL(20,3)' }
          })
    });  
    test("geographic menu options", () => {
        let options = geographicalSubTypes
        expect(options.length).toEqual(6)
    })
});