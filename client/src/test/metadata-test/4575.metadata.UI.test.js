import { cleanup, fireEvent, render, waitFor, screen } from '@testing-library/react'
// import { store4515 } from './4515.constants'
import { store4559 } from './4559.mock.data'
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

// afterEach(cleanup);
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
        preloadedState: { metadata: store4559 }
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
  
    // beforeEach(() => {

    //     container = document.createElement("div");
    //     document.body.appendChild(container);

    // });

    // afterEach(() => {

    //     unmountComponentAtNode(container);
    //     container.remove();
    //     container = null;

    // });

    // it('App -> Displays modal on button click', async () => {

    //     act(async () => {

    //         render(<App />, container);
    //         await new Promise((r) => setTimeout(r, 2000));

    //         Simulate.click(container.querySelector('.lev5-k9as-pjtv-3jk7-i7'));

    //     });
    //     await new Promise((r) => setTimeout(r, 2000));
    //     // expect(container.querySelector('.Modal')).toBeTruthy();
    //     expect(container.querySelector('.joins-delete-popup-message')).toBeTruthy()

    // })

    test('testing usecases', async () => {
        // const { queryByLabelText, getByLabelText, container } = render(
        //     <App />,
        // );
        expect(1).toBe(1)
        /**
         * TODO 
         * test cases are pending
         */
        // jest.setTimeout(30000);
        // await waitFor(() => render(<App />))
        // expect(screen.queryByText(/Metadata/i)).toBeTruthy();


        // await new Promise((r) => setTimeout(r, 2000));
        // expect(screen.queryByText(/Metadata/i)).toBeTruthy();
        // expect(screen.queryByText(/Edit Datasource/i)).toBeTruthy();
        // expect(screen.queryByText(/dynamicDataSource/i)).toBeTruthy();
        // expect(screen.queryByText(/Change/i)).toBeTruthy();
        // expect(container.getElementsByClassName('change-ds-button-info-meta')[0]).toBeTruthy();
        // await waitFor(() => fireEvent.click(container.getElementsByClassName('change-ds-button-info-meta')[0]))
        // expect(screen.queryByText(/No dataSources are available/i)).toBeTruthy();


        // await new Promise((r) => setTimeout(r, 2000));
        // expect(screen.queryByTestId(/hi-metadata-info-change-ds-button/i)).toBeTruthy();
        // await new Promise((r) => setTimeout(r, 2000));
        // expect(screen.queryAllByTestId(/metadata-join-delete-icon-lev5-k9as-pjtv-3jk7-i7/i)[0]).toBeTruthy();
        // await waitFor(() => fireEvent.click(screen.queryAllByTestId(/metadata-join-delete-icon-lev5-k9as-pjtv-3jk7-i7/i)[0]))
        // expect(screen.queryByTestId(/joins-delete-popup-message/i)).toBeTruthy();
        // let elem = screen.queryByTestId(/joins-delete-popup-message/i).parentElement.style
        // console.log('in console in jest', elem)
        // // screen.getelementById('jest-popup-message')
        // expect(screen.queryByTestId(/joins-delete-popup-message/i).offsetWidth).toBe(100);
        // expect(screen.queryByText(/Are you sure want to delete the selected join(s)/i)).toBeTruthy()
    })
})