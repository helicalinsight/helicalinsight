import { postRequest } from '../service';

export const postDashboardGlobalsData = (dispatch, formdata, callback, errback) => {
	// dispatch((dispatch, getState, services) => {
	postRequest(dispatch, `content/static/getContents`, formdata, callback, errback);
	// })
};

export const abortRequest = ({ dispatch, requestIdToCancel, requestId, callback, errback }) => {
	postRequest(
		dispatch,
		`common/abort/abortRequest`,
		{ abort: true, requestIdToAbort: requestIdToCancel, requestId },
		callback,
		errback
	);
};
