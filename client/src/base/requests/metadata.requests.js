import { getRequest, postRequest } from '../service';

function metadataRequests(dispatch) {
	const getMetadata = (formData, uri, callback = () => { }, errback = () => { }) => {
		uri = 'adhoc/metadata/get';
		postRequest(dispatch, uri, formData, callback, errback);
	};
	const getMetadataForEdit = (formData, uri, callback = () => { }, errback = () => { }) => {
		uri = 'adhoc/metadata/getMetadataForEdit';
		return postRequest(dispatch, uri, formData, callback, errback);
	};
	const getFunctions = (formData, uri, callback = () => { }, errback = () => { }) => {
		uri = 'adhoc/metadata/getFunctions';
		postRequest(dispatch, uri, formData, callback, errback);
	};

	const getDateFunctions = (formData, uri, callback = () => { }, errback = () => { }) => {
		uri = 'content/static/getContent';
		postRequest(dispatch, uri, formData, callback, errback);
	};

	const listDataSources = (formData, callback = () => { }, errback = () => { }) => {
		return getRequest(dispatch, 'listDataSources', formData, callback, errback);
	};

	const getDataSource = ( callback = () => { }, errback = () => { }) => {
		return postRequest(
			dispatch,
			'content/static/getContents',
			{ contentId: 'Static/DataSourcesList' },
			callback,
			errback
		);
	};

	const getMetadataWorkflow = (formData = {}, callback = () => { }, errback = () => { }) => {
		return postRequest(dispatch, 'adhoc/metadata/metadataWorkflow', formData, callback, errback);
	};

	const fetchColumns = (formData = {}, callback = () => { }, errback = () => { }) => {
		return postRequest(dispatch, 'adhoc/metadata/fetchColumns', formData, callback, errback);
	};

	const quickTest = (formData = {}, callback = () => { }, errback = () => { }) => {
		return postRequest(dispatch, 'core/dataSource/quickTest', formData, callback, errback);
	};
	// type = adhoc & serviceType=metadata & service=fetchColumns &

	const saveMetadata = (formData = {}, callback = () => { }, errback = () => { }) => {
		return postRequest(dispatch, 'adhoc/metadata/update', formData, callback, errback);
	};
	// type = adhoc & serviceType=metadata & service=update

	const fetchJoins = (formData = {}, callback = () => { }, errback = () => { }) => {
		return postRequest(dispatch, 'adhoc/metadata/fetchJoins', formData, callback, errback);
	};
	//type=adhoc&serviceType=metadata&service=retrieveView

	const retrieveView = (formData = {}, callback = () => { }, errback = () => { }) => {
		return postRequest(dispatch, 'adhoc/metadata/retrieveView', formData, callback, errback);
	};

	// type=adhoc&serviceType=metadata&service=saveView

	const saveView = (formData = {}, callback = () => { }, errback = () => { }) => {
		return postRequest(dispatch, 'adhoc/metadata/saveView', formData, callback, errback);
	};

	// type=adhoc&serviceType=metadata&service=retrieveViewLabels
	const retrieveViewLabels = (formData = {}, callback = () => { }, errback = () => { }) => {
		return postRequest(dispatch, 'adhoc/metadata/retrieveViewLabels', formData, callback, errback);
	};

	const dumpMetadata = (formData = {}, callback = () => { }, errback = () => { }) => {
		return postRequest(dispatch, 'adhoc/metadata/dumpCube', formData, callback, errback);
	};

	const handleDumpMetadataServiceCancel = (formData ={}, callback = () => {}, errback = () => {}) => {
		return postRequest(dispatch, 'adhoc/metadata/cancelDump', formData, callback, errback);
	};

	const getSecurityData = (formData, uri, callback = () => { }, errback = () => { }) => {
		uri = 'adhoc/metadata/getSecurity';
		postRequest(dispatch, uri, formData, callback, errback);
	};
	const getSecurityConstants = (formData, uri, callback = () => { }, errback = () => { }) => {
		uri = 'adhoc/metadata/security';
		postRequest(dispatch, uri, formData, callback, errback);
	};
	const applyService = (formData, uri, callback = () => { }, errback = () => { }) => {
		uri = 'adhoc/metadata/access';
		postRequest(dispatch, uri, formData, callback, errback);
	};
	const ValidatorCheck = (formData, uri, callback = () => { }, errback = () => { }) => {
		uri = 'adhoc/metadata/evaluateSecurity';
		postRequest(dispatch, uri, formData, callback, errback);
	};

	const getSessionVariables = (formData = {}, uri, callback = () => { }, errback = () => { }) => {
		uri = 'content/static/getContents';
		postRequest(dispatch, uri, formData, callback, errback);
	};

	return {
		ValidatorCheck,
		getSecurityData,
		getSecurityConstants,
		applyService,
		getMetadata,
		getFunctions,
		listDataSources,
		getDataSource,
		getDateFunctions,
		getMetadataWorkflow,
		fetchColumns,
		quickTest,
		saveMetadata,
		fetchJoins,
		retrieveView,
		saveView,
		retrieveViewLabels,
		dumpMetadata,
		getSessionVariables,
		getMetadataForEdit,
		handleDumpMetadataServiceCancel
	};
}

export default metadataRequests;
