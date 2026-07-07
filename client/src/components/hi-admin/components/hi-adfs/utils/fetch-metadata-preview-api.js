import requests from '../../../../../base/requests';
import { uriConfig } from '../../../../../base/requests/admin.request';
import {
	updateIsFetched,
	storeMetadataPreviewData,
	storeMetadataGenerationFormValues
} from '../../../../../redux/actions/admin.actions';
import '../index.scss';
import notify from '../../../../hi-notifications/notify';

const fetchMetadataPreview = (
	refresh = false,
	isFetched,
	dispatch,
	formValuesCheck,
	metadataGenerationFormValues,
	entityId
) => {
	(!isFetched || refresh) &&
		requests.admin(dispatch).postAdminRequest(
			!formValuesCheck
				? {
						action: 'display',
						entityId
					}
				: metadataGenerationFormValues,
			uriConfig.monitorSamlSaveMD,
			(res) => {
				dispatch(storeMetadataPreviewData(res));
				formValuesCheck && dispatch(storeMetadataGenerationFormValues({}));
				dispatch(updateIsFetched({ type: 'metadataPreview', value: true }));
			},
			(e) => {
				const Notify = notify(dispatch);
				dispatch(updateIsFetched({ type: 'metadataPreview', value: true }));
				// Notify.error({ ...e, type: "Network Call" });
			}
		);
};

export { fetchMetadataPreview };
