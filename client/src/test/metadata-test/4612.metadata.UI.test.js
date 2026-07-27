import { render, screen } from '@testing-library/react'
import { store4559 } from './4559.mock.data'
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
        preloadedState: { metadata: store4559 }
    });
    return (
        <DndProvider backend={HTML5Backend}>
            <Provider store={store}><HIMetadataPage /></Provider>
        </DndProvider>
    );
};


describe('UI testcases', () => {
    test('testing usecases', async () => {
        const { container } = render(
            <App />,
        );
        expect(screen.queryByText(/Metadata/i)).toBeTruthy();


        expect(screen.queryByText(/Metadata/i)).toBeTruthy();
        expect(container.querySelectorAll('table .ant-table-row-expand-icon')[0].click).toBeTruthy();
        container.querySelectorAll('table .ant-table-row-expand-icon')[0].click()
        expect(container.querySelectorAll('table input')).toBeTruthy();
    })
})
