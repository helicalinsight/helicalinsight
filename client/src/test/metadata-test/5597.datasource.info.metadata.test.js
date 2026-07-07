import { configureStore } from '@reduxjs/toolkit';
import axios from 'axios';
import reducers from '../../redux';
import { store5597, actionPayload } from './5597.mock.data';

describe('Metadata editing report and checking if info section is valid', () => {
    const store = configureStore({
        reducer: reducers,
        middleware: (getDefaultMiddleware) =>
            getDefaultMiddleware({
                thunk: {
                    extraArgument: axios
                },
                immutableCheck: false,
                serializableCheck: false
            }),
        preloadedState: { metadata: store5597 }
    });

    let dispatch = store.dispatch;

    test('View Query Test Case', (done) => {
        dispatch(actionPayload);
        expect(store.getState().metadata.present.dataSourcesAddedToMetadata.length).toEqual(1)
        let dsAdded = store.getState().metadata.present.dataSourcesAddedToMetadata
        expect(dsAdded[0].id).toBe('1')
        expect(dsAdded[0].datasourceName).toBe('SampleTravelDataDerby')
        done();
    });
});
