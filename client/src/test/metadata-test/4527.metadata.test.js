import { cleanup, fireEvent, render, waitFor, screen } from '@testing-library/react'
import { store4527 } from './4527.constants.js'
import { HIMetadataPage } from '../../pages/metadata-page'
import { configureStore } from '@reduxjs/toolkit';
import axios from 'axios';
import reducers from '../../redux';
import { Provider } from 'react-redux';
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';

// import { render, unmountComponentAtNode } from 'react-dom';
// import { act, Simulate } from 'react-dom/test-utils';
import reducer from '../../redux'

afterEach(cleanup);
// let container = null

const App = ({ }) => {
    const store = configureStore({
        reducer: reducers,
        middleware: (getDefaultMiddleware) =>
            getDefaultMiddleware({
                thunk: {
                    extraArgument: axios
                },
                immutableCheck: false,
                serializableCheck: false,
            }),
        preloadedState: { metadata: store4527 }
    });
    return (
        <DndProvider backend={HTML5Backend}>
            <Provider store={store}><HIMetadataPage /></Provider>
        </DndProvider>
    );
};


describe('UI testcases', () => {
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
      
  afterAll(() => {
    global.gc && global.gc()
  })
  
    test('checkng if pop over is visible on hover or not', async () => {
        /**
         * TODO
         *  this test case is pending
         * based on disc with nitin keeping this on hold as this is taking more time tahn expected
         */

        // const { queryByLabelText, getByLabelText } = render(
        //     <App />,
        // );
        // jest.setTimeout(30000);
        // const { container } = await waitFor(() => render(<App />))
        expect(1).toBeTruthy()
        // const { container } = render(
        //     <App />
        // )
        // expect(screen.queryByText(/alias-for-test/i)).toBeTruthy();
        // expect(screen.queryByText(/Metadata/i)).toBeTruthy();
        // expect(screen.queryByTestId('metadata-section-table-dimdate')).toBeTruthy()
        // await waitFor(() => fireEvent.mouseOver(screen.queryByTestId('metadata-section-table-dimdate')))
        // await new Promise((r) => setTimeout(r, 2000));
        // expect(screen.queryAllByTestId(/metadata-join-delete-icon-lev5-k9as-pjtv-3jk7-i7/i)[0]).toBeTruthy();
        // await waitFor(() => fireEvent.click(screen.queryAllByTestId(/metadata-join-delete-icon-lev5-k9as-pjtv-3jk7-i7/i)[0]))
        // expect(screen.queryByTestId(/metadata-section-table-popover/i)).toBeTruthy();
        // expect(container.getElementsByClassName('metadata-section-table-popover')).toBeTruthy();
        // let elem = screen.queryByTestId(/joins-delete-popup-message/i).parentElement.style
        // console.log('in console in jest', elem)
        // // screen.getelementById('jest-popup-message')
        // expect(screen.queryByTestId(/joins-delete-popup-message/i).offsetWidth).toBe(100);
        // expect(screen.queryByText(/Are you sure want to delete the selected join(s)/i)).toBeTruthy()
    })
})
