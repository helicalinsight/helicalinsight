import '../../utils/mockJsdom'
import { configureStore } from '@reduxjs/toolkit';
import reducers from '../../../redux';
import { exportPrintedReport } from '../../../utils/utilities';
const crypto = require('crypto');

const hiMockAxios = () => {
    return {
      instance: (url, data, config)=> {
        if(url.url === "/hi-ee/downloadReport.html"){
          return new Promise((resolve, reject) => {
            resolve({
              data: new ArrayBuffer(),
            })
          })
        }
      },
    };
  };

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