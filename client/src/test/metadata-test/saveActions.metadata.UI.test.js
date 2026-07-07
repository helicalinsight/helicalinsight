import { cleanup, fireEvent, render, waitFor, screen } from '@testing-library/react'
import { store4515 } from './4515.constants'
import { HIMetadataPage } from '../../pages/metadata-page'
import { configureStore } from '@reduxjs/toolkit';
import axios from 'axios';
import reducers from '../../redux';
import { Provider } from 'react-redux';
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import SaveActions from '../../components/hi-metadata/components/editor/saveActions'

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
        preloadedState: { metadata: store4515 }
    });
    return (
        <DndProvider backend={HTML5Backend}>
            <Provider store={store}><SaveActions /></Provider>
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
        const { container, queryByTestId, queryByText } = render(
            <App />,
        );
        // console.log(Object.keys(a))
        expect(1).toBe(1)
        // jest.setTimeout(30000);
        // await waitFor(() => render(<App />))
        // await new Promise((r) => setTimeout(r, 2000));
        // expect(container.getElementsByClassName('sider-menu-joins-icon')[0]).toBeTruthy()
        // expect(container.getElementsByClassName('anticon-bulb')).toBeTruthy();
        // expect(queryByTestId(/hi-bulb-icon-test-id/i)).toBeTruthy();
        // expect(screen.queryAllByTestId(/metadata-join-delete-icon-lev5-k9as-pjtv-3jk7-i7/i)[0]).toBeTruthy();
        // await waitFor(() => fireEvent.click(screen.queryAllByTestId(/metadata-join-delete-icon-lev5-k9as-pjtv-3jk7-i7/i)[0]))
        // expect(screen.queryByTestId(/joins-delete-popup-message/i)).toBeTruthy();
        // let elem = screen.queryByTestId(/joins-delete-popup-message/i).parentElement.style
        // console.log('in console in jest', elem)
        // // screen.getelementById('jest-popup-message')
        // expect(screen.queryByTestId(/hi-bulb-icon-test-id/i)).toBe(100);
        // let elem = screen.queryByText(/CREATE/i)
        // expect([...elem.parentElement.classList].indexOf('hi-tutorial-info') !== -1).toBeTruthy()

        // expect(container.querySelector('[data-testid="hi-bulb-icon-test-id"]')).toBeTruthy()
        // let elem1 = screen.queryByText(/hiadmin/i)
        // elem1.parentElement.parentElement.parentElement.parentElement.getElementsByClassName('anticon-bulb')[0].click()
        expect(screen.queryByText(/Metadata Saved Successfully/i)).toBeTruthy()
        expect(screen.queryByText(/Share/i)).toBeTruthy()
        // expect(screen.queryByText(/CREATE/i)).toBeTruthy()
    })
})