import { render, screen } from '@testing-library/react';
import HrPropertiesWrapper from '../../../../components/hi-reports/hi-editing-area/components/properties/properties-wrapper';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import reducers from '../../../../redux';
import axios from 'axios';
import { hrPropertiesReduxData } from './properties-wrapper-mock-data';

describe("HrProperties Wrapper",()=>{
  const notify = {
    success: jest.fn(),
    error: jest.fn(),
    info: jest.fn(),
    loading: jest.fn(),
    warning: jest.fn(),
  };
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
    const store = configureStore({
        reducer: reducers,
        middleware: (getDefaultMiddleware) =>
          getDefaultMiddleware({
            thunk: {
              extraArgument: axios,
            },
            immutableCheck: false,
            serializableCheck: false,
          }),
          preloadedState:{hreport:{past:[],present:{...hrPropertiesReduxData},future:[]}}
      });

      afterAll(() => {
        global.gc && global.gc()
      })
    
        test('renders component without crashing', () => {
      
      render(<Provider store={store}><HrPropertiesWrapper notify={notify}/></Provider>);
    });


    
describe('HrPropertiesWrapper', () => {
  test('should show a warning when there is no dataId', () => {
    render(
      <Provider store={store}>
        <HrPropertiesWrapper notify={notify}/>
      </Provider>
    );
    expect(notify.warning).toHaveBeenCalledTimes(1);
    expect(notify.warning).toHaveBeenCalledWith({
        type: 'Frontend',
        message: 'Please generate any report to view properties.', 
    });
  });
  test('should not show a warning when report is loading', () => {
    render(
      <Provider store={store}>
        <HrPropertiesWrapper notify={notify} loading={true}/>
      </Provider>
    );
    expect(notify.warning).not.toHaveBeenCalled();
  });
});


test('renders HrProperties component', () => {
  render(<Provider store={store}><HrPropertiesWrapper dataId="test-dataid" notify={notify}/></Provider>);
  expect(screen.queryByTestId(/hr-properties/i)).toBeTruthy();
});


})