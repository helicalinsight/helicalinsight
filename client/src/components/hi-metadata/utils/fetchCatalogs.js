import { cloneDeep } from 'lodash-es';
import requests from '../../../base/requests';
import { metadataActions, updateLoadingStatus } from '../../../redux/actions';
import { updateDSToRenderWithTables } from '../utils';


//this function is only called in handleTableExpandDsRow in edit mode.
export const fetchCatalogs = ({
	record,
	dispatch,
	store,
	afterFetching = () => {},
	updateDSToRenderToRedux = true,
	fetchedMetadata,
	getApi
}) => {
	dispatch(updateLoadingStatus({ type: record.key, status: true }));
	let formData = {
		...record.data,
		parameters: {
			fetchTables: true,
			fetchData: [
				{
					catalog: record.name,
					schemas: []
				}
			]
		}
	};
	if(formData.requestId) {
		delete formData.requestId;
	}
	const apiInstance = requests.metadata(dispatch).getMetadataWorkflow(
		formData,
		(res) => {
			let result = res;
			let [
				datasourceListToRender,
				dataSource,
				newTablesAdded,
				tables,
				tablesInSchema,
				duplicateTables
			] = updateDSToRenderWithTables({
				result,
				record,
				datasourceListToRender: cloneDeep(store.getState().metadata.present.datasourceListToRender),
				catalog: record.name,
				fetchedMetadata,
				store,
				dispatch
			});
			afterFetching({
				dataSource,
				datasourceListToRender,
				result,
				tables,
				tablesInSchema,
				duplicateTables,
				updatedDS: dataSource
			});
			dispatch(updateLoadingStatus({ type: record.key, status: false }));
			dispatch(
				metadataActions.updateWorkflowDataList({
					mode: 'add',
					data: result,
					datasourceListToRender: updateDSToRenderToRedux ? datasourceListToRender : false,
					dataSource,
					newTablesAdded
				})
			);
			dispatch(metadataActions.setServiceErrorStatus({ key: record.key, status: false }));
		},
		(err) => {
			console.log('in error fetching catalogs', err);
			dispatch(updateLoadingStatus({ type: record.key, status: false }));
			err.message &&
				metadataActions.setServiceErrorStatus({ key: record.key, status: true, message: err.message });
			// notify(dispatch).error({
			// 	type: 'Network Call',
			// 	...err
			// });
		}
	);
	getApi && getApi(apiInstance);
};
