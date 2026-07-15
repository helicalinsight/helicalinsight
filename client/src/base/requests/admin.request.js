import { customPostRequest, getRequest, postRequest, getDownloadRequest, uploadFilePostRequest } from '../service';

export const uriConfig = {
	monitorSystemDiskSpace: 'monitor/system/diskSpace',
	monitorSystemSystemInfo: 'monitor/system/systemInfo',
	monitorSystemTempFile: 'monitor/system/tempFile',
	monitorCacheSize: 'monitor/cache/size',
	monitorCacheDump: 'monitor/cache/dump',
	coreDataSourceCachedDS: 'core/dataSource/cachedDS',
	monitorSystemLog: 'monitor/system/log',
	monitorCacheUpdateConfiguration: 'monitor/cache/updateConfiguration',
	monitorCacheRefresh: 'monitor/cache/refresh',
	monitorCacheClean: 'monitor/cache/clean',
	coreDataSourceShutdown: 'core/dataSource/shutdown',
	monitorSystemReportStats: 'monitor/system/reportStats',
	monitorSystemdatasourceCount: 'monitor/system/datasourceCount',
	monitorSystemLlmUsageStats: 'monitor/system/llmUsageStats',
	contentStaticgetContents: 'content/static/getContents',
	monitorSamlSaveMD: 'monitor/saml/saveMD',
	monitorSystemRelease:"monitor/system/release",
	recycleBin:"adhoc/recycleBin/recycle",
	ownershipTransfer: "adhoc/owner/change",
};

function admin(dispatch) {
	const postRecycleBinResourceDetailsRequest = (formdata, callback = () => {}, errback = () => {}) => {
		return postRequest(dispatch, uriConfig.recycleBin, formdata, callback, errback);
	};
	const postOwnershipTransferRequest = (formdata, callback = () => {}, errback = () => {}) => {
		return postRequest(dispatch, uriConfig.ownershipTransfer, formdata, callback, errback);
	};
	const postRecycleBinListRequest = (callback = () => {}, errback = () => {}) => {
		return postRequest(dispatch, uriConfig.recycleBin, {action:"list"}, callback, errback);
	};

	const postRecycleBinClearRequest = (callback = () => {}, errback = () => {}) => {
		return postRequest(dispatch, uriConfig.recycleBin, {action:"clear"}, callback, errback);
	};

	const postRecycleBinDeleteRequest = (formdata, callback = () => {}, errback = () => {}) => {
		return postRequest(dispatch, uriConfig.recycleBin, formdata, callback, errback);
	};

	const postRecycleBinRestoreRequest = (formdata, callback = () => {}, errback = () => {}) => {
		return postRequest(dispatch, uriConfig.recycleBin, formdata, callback, errback);
	};

	const postAdminRequest = (formData, uri, callback = () => {}, errback = () => {}) => {
		return postRequest(dispatch, uri, formData, callback, errback);
	};

	const getDownloadAdminRequest = (url) => {
		getDownloadRequest({ dispatch: dispatch, url });
	};

	const getUserManagementRequest = (url, data, callback = () => {}, errback = () => {}) => {
		return getRequest(dispatch, url, data, callback, errback);
	};

	const postUserManagementRequest = ({ url, data, callback = () => {}, errback = () => {} }) => {
		customPostRequest({ dispatch, url, data, callback, errback });
	};

	const getProductInformation = (url, data, callback = () => {}, errback = () => {}) => {
		getRequest(dispatch, url, data, callback, errback);
	};

	//system details api calls

	const postJvmThreadDetails = (formData, uri, callback = () => {}, errback = () => {}) => {
		return postRequest(dispatch, uri, formData, callback, errback);
	};

	const postOsDetails = (formData, uri, callback = () => {}, errback = () => {}) => {
		return postRequest(dispatch, uri, formData, callback, errback);
	};

	//plugins tab api calls

	const postPluginsDetails = (formData, uri, callback = () => {}, errback = () => {}) => {
		return postRequest(dispatch, uri, formData, callback, errback);
	};

	const deletePluginsDetails = (formData, uri, callback = () => {}, errback = () => {}) => {
		postRequest(dispatch, uri, formData, callback, errback);
	};

	//Managemet tab api calls

	const postManagementData = (formData, uri, callback = () => {}, errback = () => {}) => {
		postRequest(dispatch, uri, formData, callback, errback);
	};

	const fetchNoSqlData = (formData, uri, callback = () => {}, errback = () => {}) => {
		postRequest(dispatch, uri, formData, callback, errback);
	};

	const postUploadFile = (formData, config, callback = () => {}, errback = () => {}) => {
		const url = '/importResource';
		return uploadFilePostRequest({
			dispatch,
			url,
			formData,
			config,
			callback,
			errback
		});
	};

	const fetchMetadataDumpList = (formData, uri, callback = () => {}, errback = () => {}) => {
		return postRequest(dispatch, uri, formData, callback, errback);
	};

	//scheduling tab api calls

	const postSchedulingData = (formData, uri, callback = () => {}, errback = () => {}) => {
		return postRequest(dispatch, uri, formData, callback, errback);
	};

	// const getCubeMetadataTablesData = (formData, uri, callback = () => {}, errback = () => {}) => {
	// 	uri = 'adhoc/metadata/get';
	// 	postRequest(dispatch, uri, formData, callback, errback);
	// };

	return {
		postRecycleBinResourceDetailsRequest,
		postAdminRequest,
		getDownloadAdminRequest,
		getUserManagementRequest,
		postUserManagementRequest,
		getProductInformation,
		postJvmThreadDetails,
		postOsDetails,
		postPluginsDetails,
		deletePluginsDetails,
		postManagementData,
		fetchNoSqlData,
		postSchedulingData,
		postUploadFile,
		fetchMetadataDumpList,
		postRecycleBinListRequest,
		postRecycleBinClearRequest,
		postRecycleBinDeleteRequest,
		postRecycleBinRestoreRequest,
		postOwnershipTransferRequest
	};
}

export default admin;
