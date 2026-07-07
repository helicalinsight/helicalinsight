import { configureStore } from '@reduxjs/toolkit';
import axios from 'axios';
import reducers from '../../redux';
import actionTypes from '../../redux/actions/actionTypes';
import { store5420 } from './5420.constant';

describe('Metadata Views', () => {
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
		preloadedState: { metadata: store5420 }
	});

	let dispatch = store.dispatch;

	test('View Query Test Case', (done) => {
		dispatch({ type: actionTypes.METADATA.UPDATE_ACTIVE_EDITOR_TAB, payload: 'security' });
		dispatch({ type: actionTypes.METADATA.UPDATE_ACTIVE_EDITOR_TAB, payload: 'views' });
		expect(store.getState().metadata.present['sw2k-1kff-8pm1-gbdl-qn'].query).toEqual('select * from "dimdate"');
		done();
	});
});
