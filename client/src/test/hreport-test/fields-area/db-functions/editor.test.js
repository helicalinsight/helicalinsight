

import { configureStore } from '@reduxjs/toolkit';
import { render, screen, fireEvent,waitFor } from '@testing-library/react';
import { intialStore,canvasField } from "./mock.data";
import { Provider } from 'react-redux';
import reducers from '../../../../redux';
import axios from 'axios';
import FunctionsEditor from "../../../../components/hi-reports/hi-fields-area/db-functions/editor";
import { DndProvider } from 'react-dnd'; 
import { HTML5Backend } from 'react-dnd-html5-backend';

const App = ({intial_hreport_state}) => {
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
		    preloadedState:{hreport:intial_hreport_state}
	});
	return (
		<DndProvider backend={HTML5Backend}>
			<Provider store={store}><FunctionsEditor field={canvasField} /></Provider>
		</DndProvider>
	);
};


describe("Database functions editor", () => {
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
      
    test("jest example",async () => {
        expect(1+1).toBeTruthy();
    });
    test("render db functions area",async () => {
        const container = await waitFor(() => render(<App intial_hreport_state={intialStore} />))
        let inputBox = container.getByPlaceholderText("search for functions")
        expect(inputBox).toBeTruthy();
        // expect(inputBox).toHaveFocus();
        
    });
    
});