import requests from '../../../../../base/requests';
import { uriConfig } from '../../../../../base/requests/admin.request';
import { updateIsFetched, storeMetadataGenerationData } from '../../../../../redux/actions/admin.actions';
import '../index.scss';
import notify from '../../../../hi-notifications/notify';

const BaseUrlIP = "127.0.0.1:8085"; // your ip address and port number

const fetchMetadataGeneration = (refresh = false, isFetched, dispatch) => {
	(!isFetched || refresh) &&
		requests.admin(dispatch).postAdminRequest(
			{
				contentId: 'Static/samlData',
				hiBaseURL: `http://${BaseUrlIP}/hi-ee/`
			},
			uriConfig.contentStaticgetContents,
			(res) => {
				dispatch(storeMetadataGenerationData(res));
				dispatch(updateIsFetched({ type: 'metadataGeneration', value: true }));
			},
			(e) => {
				const Notify = notify(dispatch);
				dispatch(updateIsFetched({ type: 'metadataGeneration', value: true }));
				// Notify.error({ ...e, type: "Network Call" });
			}
		);
};
export { fetchMetadataGeneration };
