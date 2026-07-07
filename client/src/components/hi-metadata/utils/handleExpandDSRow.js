import { v4 as uuidv4 } from 'uuid';
import { fetchCatalogs, fetchDatasource, fetchSchema } from '../utils';

export const handleExpandDSRow = ({
	isExpanded = false,
	record,
	dispatch,
	store,
	refresh = false,
	mode = 'create',
	refreshDataSource = false,
	setRequestId,
	getApi
}) => {
	let requestId = uuidv4();
	(!record.fetched || refreshDataSource) && setRequestId(requestId);

	if (!refreshDataSource) {
		if (!isExpanded) {
			// if it is not expanded and if refresh is false then return
			if (!refresh) {
				return;
			}
		}
	}
	if (record.category === 'schema') {
		!record.fetched && fetchSchema({ requestId, isExpanded, record, dispatch, store, getApi });
	} else if (record.category === 'catalog') {
		!record.fetched &&
			(mode === 'edit' ? !record.children.length : record?.children?.length ? false : true) &&
			fetchCatalogs({ requestId, isExpanded, record, dispatch, store, getApi });
	} else {
		if (refreshDataSource) {
			fetchDatasource({ requestId, isExpanded, record, dispatch, store, refresh, refreshDataSource, getApi });
		} else {
			!record.fetched &&
				// (mode === 'edit' ? !record.children : true) &&
				(mode === 'edit' ? !record.children.length : record?.children?.length ? false : true) &&
				fetchDatasource({ requestId, isExpanded, record, dispatch, store, refresh, getApi });
		}
	}
};
