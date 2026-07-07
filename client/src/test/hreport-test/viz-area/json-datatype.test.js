import '../../utils/mockJsdom'
// import { configureStore } from '@reduxjs/toolkit';
// import { render, screen, fireEvent,act,waitFor } from '@testing-library/react';
// import { Provider } from 'react-redux'; 
// import reducers from '../../../redux';
// import {HelicalReports} from "../../../pages/helical-reports-page"
// import { DndProvider } from 'react-dnd'; 
// import { HTML5Backend } from 'react-dnd-html5-backend';
// import { hiMockAxios } from '../../../app/mock-axios';
// import { appActions } from '../../../redux/actions';
// import { generateReport, openMetadata } from '../../../components/hi-reports/utils/base';
// import { addFieldToCanvas, applyReportScripts, changeEditorContent } from '../../../redux/actions/hreport.actions';
const crypto = require('crypto');

// const App = ({store}) => {
// 	return (
// 		<DndProvider backend={HTML5Backend}>
// 			<Provider store={store}>
//                 <div id="report-drilldown-menu" ></div>
//                 <HelicalReports />
//             </Provider>
// 		</DndProvider>
// 	);
// };

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
    // test("pre execution test 1",async () => {
    //     const store = configureStore({
    //         reducer: reducers,
    //         middleware: (getDefaultMiddleware) =>
    //             getDefaultMiddleware({
    //                 thunk: {
    //                     extraArgument: hiMockAxios
    //                 },
    //                 immutableCheck: false,
    //                 serializableCheck: false,
    //             }),
    //     });
    //     const dispatch = store.dispatch
    //     const getState = store.getState
    //     await waitFor(()=> render(<App store={store}/>))
    //     await waitFor(()=>openMetadata({
    //         location:"Smoke_Testing/Sample_Metadata",
    //         metadataFileName:"JSONMetadata.metadata"
    //     },dispatch)) 
    //     let report = getState().hreport.present.reports[0]
    //     let {metadata} = report
    //     let table = metadata.tables["primer-dataset.json"]
    //     let column = table.columns["address"]
    //     dispatch(addFieldToCanvas({column,table,addedAs:"column"}))
    //     column = table.columns["borough"]
    //     dispatch(addFieldToCanvas({column,table,addedAs:"column"}))
    //     console.log("store.fields")
    //     console.log(store.getState().hreport.present.reports[0].fields)
    //     let activeReport = getState().hreport.present.reports[0]
    //     await waitFor(()=> generateReport(activeReport,dispatch))
    // });
}); 