import requests from '../../../../../base/requests';
import { uriConfig } from '../../../../../base/requests/admin.request';
import { updateIsFetched, storeMetadataAdministrationData } from '../../../../../redux/actions/admin.actions';
import '../index.scss';
import notify from '../../../../hi-notifications/notify';

const fetchMetadataAdministration = (refresh = false, isFetched, dispatch) => {
	(!isFetched || refresh) &&
		requests.admin(dispatch).postAdminRequest(
			{ contentId: 'Static/samlProviders' },
			uriConfig.contentStaticgetContents,
			(res) => {
				dispatch(storeMetadataAdministrationData(res));
				dispatch(updateIsFetched({ type: 'metadataAdministration', value: true }));
			},
			(e) => {
				const Notify = notify(dispatch);
				dispatch(updateIsFetched({ type: 'metadataAdministration', value: true }));
				// Notify.error({ ...e, type: "Network Call" });
			}
		);
};
export { fetchMetadataAdministration };
