import Editor from '../../components/hi-metadata/components/editor/index'
import { cleanup, fireEvent, render, waitFor, screen } from '@testing-library/react'
import { store4515 } from './4515.constants'
import { HIMetadataPage } from '../../pages/metadata-page'
import { configureStore } from '@reduxjs/toolkit';
import axios from 'axios';
import reducers from '../../redux';
import { Provider } from 'react-redux';
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
// import { app4529_2, store4529_1 } from './4529.mock.data'
import reducer from '../../redux'

afterEach(cleanup);

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
        preloadedState: {
            metadata: store4515,
            // app: app4529_2
        }
    });
    return (
        <DndProvider backend={HTML5Backend}>
            <Provider store={store}><Editor /></Provider>
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
  
    test('testing usecases', async () => {
        const { container, queryByLabelText, getByLabelText } = render(
            <App />,
        );
        jest.setTimeout(30000);
        // const { container, queryByLabelText, getByLabelText } = await waitFor(() => render(<App />))
        // expect(container.getElementsByClassName('metadata-layout')).toBeTruthy()
        // hi - navbar - toggle - sidebar - icon
        // expect(screen.queryByTestId(/hi-metadata-sidebar-hidden/i)).toBeTruthy();
        // await new Promise((r) => setTimeout(r, 2000));
        // console.log('type of ', screen.queryByTestId('hi-navbar-toggle-sidebar-icon')[0])
        // expect(screen.queryByTestId(/hi-navbar-toggle-sidebar-icon/i)).toBeTruthy();
        // await waitFor(() => fireEvent.click())
        // container.getElementsByClassName('hi-navbar-toggle-sidebar-icon')[0].click()
        // expect(screen.queryByTestId(/hi-metadata-sidebar-hidden/i)).toBeTruthy();
        // expect(screen.queryByText(/Info/i)).toBeTruthy();
        expect(container.getElementsByClassName('sider-menu-joins-icon')[0]).toBeTruthy()
        expect(container.getElementsByClassName('sider-menu-joins-icon')[0].innerHTML === 'Joins').toBeTruthy()

        // expect(screen.queryByTestId(/hi-metadata-sidebar-visible/i)).toBeTruthy();

        // expect(1).toBe(1)
        /**
         * TODO 
         * test cases are pending
         */
        // await waitFor(() => render(<App />))
        // expect(screen.queryByText(/dimdate.dim_id/i)).toBeTruthy();
        // expect(screen.queryByTestId(/metadata-joins-section/i)).toBeTruthy();
        // expect(screen.queryAllByTestId(/metadata-join-delete-icon-lev5-k9as-pjtv-3jk7-i7/i)[0]).toBeTruthy();
        // await waitFor(() => fireEvent.click(screen.queryAllByTestId(/metadata-join-delete-icon-lev5-k9as-pjtv-3jk7-i7/i)[0]))
        // // await new Promise((r) => setTimeout(r, 2000));
        // expect(screen.queryByTestId(/joins-delete-popup-message/i)).toBeTruthy();
        // let elem = screen.queryByTestId(/joins-delete-popup-message/i).parentElement.style
        // console.log('in console in jest', elem)
        // // screen.getelementById('jest-popup-message')
        // expect(screen.queryByTestId(/joins-delete-popup-message/i).offsetWidth).toBe(100);
        // expect(screen.queryByText(/Are you sure want to delete the selected join(s)/i)).toBeTruthy()
    })
})