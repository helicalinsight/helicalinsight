import initialStates from './initialStates';
import actionTypes from '../actions/actionTypes';

export const propertyPaneReducer = (state = initialStates.propertyPaneInitialState, action) => {
    switch (action.type) {
        case actionTypes.SHOW_MORE_CARD_ICONS: {
			return {
				...state,
				showMore: action.payload
			};
		}
		default:
			return { ...state };

    }
}