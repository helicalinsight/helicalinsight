import { configureStore } from '@reduxjs/toolkit';
import axios from 'axios';
import reducers from '../../redux';
import actionTypes from '../../redux/actions/actionTypes';
import { mockStore } from './5413.5414.mock.data.js';
import {FDForSaveView} from '../../components/hi-metadata/utils/views/FDForSaveView'

describe('Metadata 5413 and 5414', () => {
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
		preloadedState: { metadata: mockStore }
	});

	let dispatch = store.dispatch;

	test('checking the count of tables', (done) => {
		// dispatch(actionData);
		expect(FDForSaveView(mockStore).classifier).toBeTruthy()
		done();
	});
});
