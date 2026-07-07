import { getRequest, postRequest } from '../service';

function hreportRequests(dispatch) {
	const getMetadata = (formData, uri, callback = () => {}, errback = () => {}) => {
		uri = 'adhoc/metadata/get';
		return postRequest(dispatch, uri, formData, callback, errback);
	};
	const getFunctions = (formData, uri, callback = () => {}, errback = () => {}) => {
		uri = 'adhoc/metadata/getFunctions';
		return postRequest(dispatch, uri, formData, callback, errback);
	};
	const getDateFunctions = (formData, uri, callback = () => {}, errback = () => {}) => {
		uri = 'content/static/getContents';
		return postRequest(dispatch, uri, formData, callback, errback);
	};
	const fetchData = (formData, uri, callback = () => {}, errback = () => {}) => {
		uri = 'adhoc/report/fetchData';
		return postRequest(dispatch, uri, formData, callback, errback);
	};
	const generateQuery = (formData, uri, callback = () => {}, errback = () => {}) => {
		uri = 'adhoc/report/generateQuery';
		return postRequest(dispatch, uri, formData, callback, errback);
	};
	const saveReport = (formData, uri, callback = () => {}, errback = () => {}) => {
		uri = 'adhoc/report/saveReport';
		postRequest(dispatch, uri, formData, callback, errback);
	};
	const getReportForEdit = (formData, uri, callback = () => {}, errback = () => {}) => {
		uri = 'adhoc/report/getReportForEdit';
		return postRequest(dispatch, uri, formData, callback, errback);
	};
	const getReport = (formData, uri, callback = () => { }, errback = () => {}) => {
		uri = 'adhoc/report/getReport';
		postRequest(dispatch, uri, formData, callback, errback);
	};
	const getCube = (formData, uri, callback = () => {}, errback = () => {}) => {
		uri = 'adhoc/cube/getCube';
		postRequest(dispatch, uri, formData, callback, errback);
	};
	const getGeoJsonData = (formData, uri, callback = () => { }, errback = () => { }) => {
		uri = `/getExternalResource?path=System/Map/${formData}`;
		getRequest(dispatch, uri, {}, callback, errback)
	}
	const getGeojsonListingData = (formData, uri, callback = () => { }, errback = () => { }) => {
		uri = `services?type=monitor&serviceType=system&service=list&formData=${formData}`;
		getRequest(dispatch, uri, '', callback, errback)
	}
	return {
		getReportForEdit,
		getReport,
		getFunctions,
		generateQuery,
		fetchData,
		getDateFunctions,
		saveReport,
		getMetadata,
		getCube,
		getGeojsonListingData,
		getGeoJsonData
	};
}

export default hreportRequests;
