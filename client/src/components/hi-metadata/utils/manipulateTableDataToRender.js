import { uuid } from '../../../utils/uuid';
import { filterTables } from './filterTables';
import { cloneDeep } from 'lodash-es';



export const manipulateTableDataToRender = ({ tables, searchText = false }) => {
	let treeResult = [];
	tables = filterTables({ tables, searchText });
	let notSortedTables = cloneDeep(tables)
	tables = Object.keys(notSortedTables)
		.sort()
		.reduce((acc, key) => ({
			...acc, [key]: notSortedTables[key]
		}), {})

	Object.keys(tables).map((key) => {
		let eachTable = tables[key];
		let result = { ...eachTable, key: eachTable.uniqueKey };
		let columns = Object.keys(eachTable.columns || {}).map((col) => {
			// let __uuid = eachTable.columns[col].uuid
			// if (!__uuid) {
			// 	__uuid = uuid()
			// }
			let result = {
				...eachTable.columns[col],
				name: col,
				// keyPath: `${eachTable.keyPath}/${__uuid}`,
				// key: __uuid,
				// uuid: __uuid,
				duplicateParent: eachTable.duplicate,
				tableId: eachTable.id || eachTable.originalId,
				connId: eachTable.connId,
				tableKey: eachTable.columns[col].tableKey || eachTable.name,
			};
			return result;
		});
		columns = columns.sort(function (x, y) { return x.alias.localeCompare(y.alias) })
		delete result.columns;
		delete result.nameWithConnId;
		delete result.selected;
		result.name = eachTable.name;
		// result.key = __uuid
		// result.uuid = __uuid

		result.children = columns;
		treeResult.push(result);
	});
	return treeResult;
};
