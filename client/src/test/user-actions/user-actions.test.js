import initialStates from "../../redux/reducers/initialStates";
import { resetStoreState } from "../../redux/reducers/useractions.reducer";

describe("resetStoreState", () => {
	const initialNotificationData = [		{ message: "Login success", type: "Network Call" },		{ message: "Logout success", type: "Network Call" },	];

	it("returns initialStates.userActionsInitialState when action.payload.logType is not 'impersonateLogin'", () => {
		const action = { payload: { logType: "someOtherType" } };
		const state = { notificationData: initialNotificationData };
		const expectedState = initialStates.userActionsInitialState;

		const actualState = resetStoreState(action, state, initialStates);

		expect(actualState).toEqual(expectedState);
	});

	it("returns initialStates.userActionsInitialState when notificationData is empty", () => {
		const action = { payload: { logType: "impersonateLogin" } };
		const state = { notificationData: [] };
		const expectedState = initialStates.userActionsInitialState;

		const actualState = resetStoreState(action, state, initialStates);

		expect(actualState).toEqual(expectedState);
	});

	it("returns initialStates.userActionsInitialState when there is no loginItem in notificationData", () => {
		const action = { payload: { logType: "impersonateLogin" } };
		const state = { notificationData: [{ message: "Logout success", type: "Network Call" }] };
		const expectedState = initialStates.userActionsInitialState;

		const actualState = resetStoreState(action, state, initialStates);

		expect(actualState).toEqual(expectedState);
	});

	it("returns initialStates.userActionsInitialState with only the loginItem in notificationData when there is a loginItem", () => {
		const action = { payload: { logType: "impersonateLogin", successNotification: true } };
		const state = { notificationData: initialNotificationData };
		const expectedState = { ...initialStates.userActionsInitialState, notificationData: [initialNotificationData[0]] };

		const actualState = resetStoreState(action, state, initialStates);

		expect(actualState).toEqual(expectedState);
	});
});
