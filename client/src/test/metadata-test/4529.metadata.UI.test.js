import { render, waitFor, screen } from '@testing-library/react'
import { store4515 } from './4515.constants'
import { HIMetadataPage } from '../../pages/metadata-page'
import { configureStore } from '@reduxjs/toolkit';
import axios from 'axios';
import reducers from '../../redux';
import { Provider } from 'react-redux';
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';



const App = () => {
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
         }
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
  
    test('testing usecases', async () => {
        await waitFor(() => render(<App />));
        expect(screen.queryByTestId(/hi-metadata-sidebar-hidden/i)).toBeTruthy();
    })
})