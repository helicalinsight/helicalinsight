import { customPostRequest, postRequest } from '../service';

function usermodule(dispatch, settings) {
	const postSendMail = (formData, callback = () => { }, errback = () => { }) => {
		return customPostRequest({
			dispatch,
			url: settings.DashboardGlobals.sendMail,
			data: formData,
			callback,
			errback
		});
	};

	const postScheduleReport = (formData, callback = () => { }, errback = () => { }) => {
		return customPostRequest({
			dispatch,
			url: settings.DashboardGlobals.saveReport,
			data: formData,
			callback,
			errback
		});
	};

	const previewEmail = (formData, callback = () => { }, errback = () => { }) => {
		let uri = 'util/io/previewEmail'
		return postRequest(dispatch, uri, formData, callback, errback)
	};

	return {
		postSendMail,
		postScheduleReport,
		previewEmail
	};
}

export default usermodule;