import { configureStore } from '@reduxjs/toolkit';
import axios from 'axios';
import reducers from '../../redux';
import actionTypes from '../../redux/actions/actionTypes';
import { mockStore, actionData } from './5298.mock.data';

describe('Metadata 5298', () => {
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
		dispatch(actionData);
		expect(store.getState().metadata.present.datasourceListToRender[0].children[0].children[3].children.length).toEqual(5);
		done();
	});
});
