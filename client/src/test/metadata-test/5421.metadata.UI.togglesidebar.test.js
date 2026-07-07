import { cleanup, fireEvent, render, waitFor, screen } from '@testing-library/react'
// import { store4515 } from './4515.constants'
import { mockStore } from './5421.mock.data'
import { HIMetadataPage } from '../../pages/metadata-page'
import { configureStore } from '@reduxjs/toolkit';
import {hiMockAxios} from '../../app/mock-axios';
// import axios from 'axios'
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
                    extraArgument: hiMockAxios
                },
                immutableCheck: false,
                serializableCheck: false,
            }),
        preloadedState: { metadata: mockStore }
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
        const { queryByLabelText, getByLabelText, container, document } = render(
            <App />,
        );
        expect(screen.queryByText(/Metadata_1/i)).toBeTruthy();
        // expect(container.scrollHeight < container.clientHeight).toBeTruthy();
        // expect(container.scrollHeight).toBe(1);
    })
})