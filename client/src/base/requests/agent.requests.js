import {
	setAgentMetadataTablesData,
	setAgentSemanticTypes,
} from '../../redux/actions/agent.actions';
import { postRequest } from '../service';

export default function agentRequests(dispatch) {
	const getAgentMetadataTablesData = ({ path, fileName, callback, errback }) => {
		const uri = 'adhoc/metadata/get';
		const formData = { location: path, metadataFileName: fileName };
		return postRequest(
			dispatch,
			uri,
			formData,
			(res) => {
				dispatch(setAgentMetadataTablesData(res));
				callback?.(res);
			},
			(err) => {
				errback?.(err);
			},
		);
	};

	const getSemanticTypes = (callback = () => {}, errback = () => {}) => {
		const uri = 'content/static/getContents';
		const formData = { contentId: 'Static/semantictypes' };
		return postRequest(
			dispatch,
			uri,
			formData,
			(res) => {
				dispatch(setAgentSemanticTypes(res?.semanticTypes || []));
				callback?.(res);
			},
			(err) => {
				errback?.(err);
			},
		);
	};

	return { getAgentMetadataTablesData, getSemanticTypes };
}
