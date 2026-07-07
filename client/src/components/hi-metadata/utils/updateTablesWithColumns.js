import { cloneDeep } from 'lodash-es';
import { metadataActions } from '../../../redux/actions';
import { getColumnUniqueKey, getTableUniqueKey } from '../../../utils/reportQuery/utils/handleGetUniqueKey';
import { uuid } from '../../../utils/uuid';
import { genrateUniqueName } from './utils';

export const updateTablesWithColumns = ({
	result,
	currentTable,
	dispatch = false, 
	tables,
	duplicateTable = false,
	returnTables = false,
	mode
}) => {
	tables = cloneDeep(tables);
	let { metadata } = cloneDeep(result);
	let keyName = currentTable.keyName;
	let actualKeyName = keyName;
	if (!(actualKeyName in metadata.table)) {
		/**
		 * when fetching columns for tables from 2nd connection(not from 1st conn)
		 * the keyname is not present in the fetched data that is in current table
		 * so if there is only one key in current table and if keyname table key in ucrrent table dosen't match then taking ket from current table as keyName
		 */
		if (Object.keys(metadata.table).length === 1) {
			actualKeyName = Object.keys(metadata.table)[0];
		}
	}
	let _columns = metadata.table[actualKeyName].columns;
	let manipulatedCols = {};
	Object.keys(_columns).forEach((key) => {
		let col = {..._columns[key]};
		const [tableName, columnName] = col.fullyQualifiedColumn.split('.');
		col.category = 'column';
		if(!col.uniqueKey) {
			col.uniqueKey = !["test"].includes(process.env.NODE_ENV) && getColumnUniqueKey({mode: 'create', id: col.id, dbId: currentTable.connId})
		}
		col.name = col.name ?? columnName;
		col.tableKey = keyName;
		col.connId = currentTable.connId;
		col.columnKey = key;
		col.tableId= currentTable.id || currentTable.originalId
		manipulatedCols[key] = col;
		if(col.duplicate) {
			col.originalName = col.originalName ?? columnName;
		}
	});
	if(tables[keyName]){
		tables[keyName].columns = manipulatedCols;
		tables[keyName].columnsFetched = true;
	}
	if (duplicateTable) {
		//TODO: Genrate New unique keyName for duplicate table. (Keep multiconnection in notice)
		let newTable = { ...tables[keyName] };
		delete newTable.isSaved
		let _uuid = uuid();
		const newName = genrateUniqueName({ allItems: Object.values(tables).map(table => table.name), item: tables[keyName].name });
		newTable.originalName = tables[keyName].name ;
		newTable.name = newName;
		newTable.alias = genrateUniqueName({ allItems: Object.values(tables).map(table => table.alias), item: tables[keyName].alias })
		newTable.originalId = tables[keyName].id;
		newTable.duplicate = true;
		if (newTable.keyPath) {
			newTable.keyPath = newTable.keyPath?.replace(newTable.uuid, _uuid);
		}
		newTable.keyName = newName;
		//for duplicate table uuid will be generated and will be used as id
		newTable.id = _uuid;
		delete newTable.cacheId; // 6271
		delete newTable.selected; // 6271
		newTable.uniqueKey = !["test"].includes(process.env.NODE_ENV) && getTableUniqueKey({mode: 'create', id: _uuid, dbId: newTable.connId})
		newTable.columns = cloneDeep(newTable.columns);
		Object.values(newTable.columns).map((col) => {
			let colUuid = uuid()
			col.tableKey = newName;
			col.category = 'column';
			col.tableId = newTable.id;
			col.originalId = col.originalId ?? col.id;
			col.id = colUuid;
			col.uniqueKey = !["test"].includes(process.env.NODE_ENV) && getColumnUniqueKey({mode: 'create', id: colUuid, dbId: newTable.connId})
			return col;
		});
		//dispatch a action which will update the store with duplicated tableid(uuid)
		// TODO: also dispatch action when removing duplicate table. so that it should be removed from `duplicateTableList` array in store.
		dispatch(metadataActions.addDuplicateTable(newTable))
		tables[newName] = newTable;
	}
	//adds column to table and only updates that table in store
	dispatch(metadataActions.addColumnsToTable(tables[currentTable.keyName]));
	if (returnTables) {
		return tables;
	}
};