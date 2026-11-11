import '../../utils/mockJsdom'
import { configureStore } from '@reduxjs/toolkit';
import reducers from '../../../redux';
import { exportPrintedReport } from '../../../utils/utilities';
const crypto = require('crypto');

const hiMockAxios = () => {
    return {
      instance: (url, data, config)=> {
        if(url.url === "/hi-ee/downloadReport.html"){
          return Promise.resolve({
            data: new ArrayBuffer(),
            headers: {},
          })
        }
        return Promise.resolve({ data: {}, headers: {} })
      },
      abort: jest.fn(),
      controller: { signal: {} },
      source: { token: {}, cancel: jest.fn() },
    };
  };

describe("Hreport visualisation", () => {
   
    test("exporting report as pdf",async () => {
        const store = configureStore({
            reducer: reducers,
            preloadedState: {
                app: {
                    applicationSettingsData:{
                        settings:{
                            DashboardGlobals:{
                                reportDownload:"/hi-ee/downloadReport.html"
                            } 
                        }
                    }
                },
            },
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
        exportPrintedReport(
            { 
                file:{path: '1_naresh/child.hr', name: 'child.hr', title: 'child'}, 
                format:'pdf', 
                parameters:{mode: 'open'}, 
                callback:()=>{} },
            dispatch
          )
    });
}); 