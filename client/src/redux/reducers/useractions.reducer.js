import actionTypes from '../actions/actionTypes';
import initialStates from './initialStates';
// const userActionsInitialState = {
// 	notificationDrawerStatus: false,
// 	notificationItemDrawer: false,
// 	notificationData: [],
// 	notificationItemId: null
// };

export const resetStoreState = (action, state, initialStates) => {
	if (action.payload.logType === "impersonateLogin" && action.payload.successNotification) {
		if (state.notificationData.length) {
			const loginItem = state.notificationData.find((item) => item.message === "Login success" && item.type === "Network Call")
			if (loginItem) {
				return {...initialStates.userActionsInitialState, notificationData: [loginItem]};
			}
		}
	}
	return initialStates.userActionsInitialState;
}

const userActionsReducer = (state = initialStates.userActionsInitialState, action) => {
	switch (action.type) {
		case actionTypes.TOGGLE_NOTIFICATIONS:
			return {
				...state,
				notificationDrawerStatus: !state.notificationDrawerStatus
			};
		case actionTypes.UPDATE_NOTIFICATION_DATA:
			const newItem = action.payload;
			return {
				...state,
				notificationData: [ newItem, ...state.notificationData ]
			};
		case actionTypes.DELETE_NOTIFICATION_ITEM:
			const id = action.payload;
			const item = state.notificationData.filter((obj) => obj.id === id)[0];
			return {
				...state,
				notificationData: [ ...state.notificationData.filter((obj) => obj.message !== item.message) ]
			};
		case actionTypes.RESET_NOTIFICATION_DATA:
			return { ...state, notificationData: [] };
		case actionTypes.UPDATE_NOTIFICATION_ITEM:
			const { notificationItemId, status } = action.payload;
			return {
				...state,
				notificationItemDrawer: status,
				notificationItemId
			};
		case actionTypes.RESET_STORE:
			return resetStoreState(action, state, initialStates)
		default:
			return { ...state };
	}
};
export default userActionsReducer;
