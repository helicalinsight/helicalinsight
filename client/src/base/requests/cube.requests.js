import notify from '../../components/hi-notifications/notify';
import { applySemanticListFieldsToCubePayload } from '../../components/hi-cube/cubeSemanticFields';
import { setCubeMetadataTablesData } from '../../redux/actions/cube.actions';
import { getRequest, postRequest } from '../service';

export default function cubeRequests(dispatch) {
	const getCubeMetadataTablesData = ({ path, fileName }) => {
		const Notify = notify(dispatch);
		const uri = 'adhoc/metadata/get';
		const formData = { location: path, metadataFileName: fileName };
		const callback = (res) => {
			// console.log(res, 'met');
			dispatch(setCubeMetadataTablesData(res));
		};
		const errback = (err) => {
			// Notify.error({ ...err, type: 'Network Call' });
		};
		postRequest(dispatch, uri, formData, callback, errback);
	};

	const getAgentMetadataTablesData = ({ path, fileName, onSuccess, onError }) => {
		const Notify = notify(dispatch);
		const uri = "instantbi/instant/getAiAgentForEdit";
		const formData = { dir: path, file: fileName };
		const callback = (res) => {
			if (onSuccess) {
				onSuccess(res);
			}
		};
		const errback = (err) => {
			if (onError) {
				onError(err);
			}
		};
		postRequest(dispatch, uri, formData, callback, errback);
	};

	const handleCubeSaveRequest = (formData, uri, callback = () => {}, errback = () => {}) => {
		uri = 'adhoc/cube/update';
		postRequest(
			dispatch,
			uri,
			applySemanticListFieldsToCubePayload(formData),
			callback,
			errback,
		);
	};

	const getCubeForEdit = (formData, uri, callback = () => {}, errback = () => {}) => {
		uri = 'adhoc/cube/getCube';
		postRequest(dispatch, uri, formData, callback, errback);
	};

	// const getCubeMetadataTablesData = ({fileName, path}) => { // && path && fileName
	// // { location: "sai_ganesh", metadataFileName: "Metadata_1.metadata" },
	// 	{ location: path, metadataFileName: fileName },
	// 	'',
	// 	(res) => {
	// 		dispatch(setCubeMetadataTablesData(res));
	// 	},
	// 	(err) => {
	// 		Notify.error({ ...err, type: "Network Call" })
	// 	}
	// };

	return { getCubeMetadataTablesData, handleCubeSaveRequest, getCubeForEdit,getAgentMetadataTablesData };
}
