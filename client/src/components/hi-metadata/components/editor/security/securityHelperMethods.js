// import { v4 as uuidv4 } from '../../../../../utils/uuid';
// import { handleTableExpand, manipulateTableDataToRender } from '../../../utils';
import { BorderOuterOutlined, TableOutlined } from '@ant-design/icons';
import metadataRequests from '../../../../../base/requests/metadata.requests';
import notify from '../../../../hi-notifications/notify';
import { doGetSecurityCall, metadataUndoRedoPurpose, saveExpressionData, setEntityNames, setGetSecurityTableData } from '../../../../../redux/actions';
import { Tooltip, Typography } from 'antd';

export let modalTreeData = [];

export const entityNameHandler = ({ entityValues, dispatch }) => {
	let entityNameItems = [];
	entityValues.forEach(cur => {
		entityNameItems.push({entityName: cur.parentTitle + (cur.childTitle ? `.${cur.childTitle}` : ''), key: cur.key, tooltipInfoObj: cur.tooltipInfoObj})
	}, '');
	if(["test"].includes(process.env.NODE_ENV)) {
		return entityNameItems;
	} else {
		dispatch(setEntityNames(entityNameItems));
	}
};

export const tablesToTableDataConverter = ({
	tables = {},
	isSearching,
	searchedVal,
	setIsModalTreeExist,
	isModalTreeExist,
	category,
	formData = {},
	edit,
	mode,
	setKeysExpanded
}) => {
	if(edit) {
		if(!formData['Expression Type'])
		throw new Error('Err in tablesToTableDataConverter:- "Expression Type" is missing in formData prop');
	}
	// if(!(category === 'column' || category === 'view' || category === 'table')) {
	// 	console.log('Err in tablesToTableDataConverter:- category prop is not correct');
	// }
	let modalTree = [];
	for (let i in tables) {
		if(!(tables[i].alias && (tables[i].keyName ||  tables[i].name) && tables[i].uniqueKey && tables[i].dataSource && tables[i].id)) {
			throw new Error('Err in tablesToTableDataConverter:- Some keys are missing in table');
		}
		let parent = {
			icon: ["test"].includes(process.env.NODE_ENV) ? '' : <TableOutlined />,
			title: tables[i]['alias'],  //tableAlias
			Name: tables[i].name,
			// disabled: true,
			checkable: edit ? formData['Expression Type'] !== 'column' : category !== 'column',
			key: tables[i].uniqueKey,
			dbId: tables[i].dataSource?.dbId,
			tableId: tables[i].id,
			Id: tables[i].dataSource?.id,
			Datasource: tables[i].dataSource?.datasourceName,
			Original: tables[i].originalName,
			// tableUuid: tables[i].uuid,
			category: 'table',
			children: []
		};
		// if(tables[i].originalName) {
		// 	parent.Original = tables[i].originalName;
		// }
		let tableChildren;
		if ('columns' in tables[i]) {
			tableChildren = { ...tables[i]['columns'] };
		} else {
			tableChildren = {};
		}
		let addTable = false;
		if (Object.keys(tableChildren).length) {
			for (let j in tableChildren) {
				if(!(tableChildren[j].alias && tableChildren[j].columnKey && tableChildren[j].uniqueKey && tableChildren[j].id)) {
					throw new Error('Err in tablesToTableDataConverter:- Some keys are missing in column');
				}
				let parentChild = {
					title: tableChildren[j].alias, // columnAlias
					key: tableChildren[j].uniqueKey, //tableChildren[j].fullyQualifiedColumn,
					Name: tableChildren[j].columnKey,
					dbId: parent?.dbId,
					category: 'column',
					// tableId: tables[i].id,
					// tableAlias: tables[i].alias,
					Id: tables[i].dataSource?.id,
					Datasource: tables[i].dataSource?.datasourceName,
					columnId: tableChildren[j].id,
					Original: tableChildren[j].originalName,
					icon: ["test"].includes(process.env.NODE_ENV) ? '' : <BorderOuterOutlined />,
					checkable: edit ? formData['Expression Type'] === 'column' : category === 'column'
				};
				if (!isSearching && !["test"].includes(process.env.NODE_ENV)) {
					if(j.toString().toLowerCase().match(searchedVal.toString().toLowerCase()) && searchedVal !== '') {
						parent.children.push(parentChild);
						addTable = true;
						setKeysExpanded(prev => [...prev, parent.key]);
					}
				} else {
					parent.children.push(parentChild);
				}
			}
			parent.children.sort((a, b) => (a.title <= b.title ? -1 : 1));
		}
		if (!isSearching && !["test"].includes(process.env.NODE_ENV)) {
			if(i.toString().toLowerCase().match(searchedVal.toString().toLowerCase()) || addTable) {
				modalTree.push(parent);
			} 
		} else {
			modalTree.push(parent);
		}
	}
	modalTree.sort((a, b) => (a.title <= b.title ? -1 : 1));
	if (!isSearching) {
		isModalTreeExist && !modalTree.length && setIsModalTreeExist(false);
	} else {
		!isModalTreeExist && setIsModalTreeExist(true);
	}
	modalTreeData = modalTree;
	return modalTree;
};

// export const handleSecurityTableSelection = ({ selectedRows, setSelectedKeys }) => {
// 	let newRows = selectedRows.map((ele) => {
// 		if (ele) {
// 			return ele.key;
// 		}
// 	});
// 	setSelectedKeys(newRows);
// };

export const mapTablesColumnsWithUniqueKey = ({tables}) => {
   let mappedTablesColumns = new Map();
   let individualTables = Object.values(tables);
   individualTables.forEach(table => {
		let tableUniqueKey = table.uniqueKey;
		mappedTablesColumns.set(tableUniqueKey, {tableAlias: table.alias, key: tableUniqueKey, tooltipInfoObj: { Name: table.name, Id: table.dataSource?.id, Datasource: table.dataSource?.datasourceName, Alias: table.alias, key: tableUniqueKey, Original: table.originalName }});
		Object.values(table.columns || {}).forEach(col => {
			let columnUniqueKey = col.uniqueKey;
			mappedTablesColumns.set(columnUniqueKey, {tableAlias: table.alias, columnAlias: col.alias, key: columnUniqueKey, tooltipInfoObj: { Table: table.alias, Name: col.columnKey, Id: table.dataSource?.id, Datasource: table.dataSource?.datasourceName, Alias: col.alias, key: columnUniqueKey, Original: col.originalName }});
		})
   })
   return mappedTablesColumns;
}

export const handleGetSecurityData = ({
	saveDetails,
	dispatch,
	setSecurityTableData,
	setIsInfoShow,
	setShowValidatedTable,
	setShowContent,
	tables, 
	mode,
	isGetSecurityCallDone,
	isInfoShow, 
	isValidatedTableShow
}) => {
	const { getSecurityData } = metadataRequests(dispatch);
	const Notify = notify(dispatch);
	getSecurityData(
		{ metadataFileName: saveDetails.uuid, location: saveDetails.location },
		'',
		(res) => {
			!isGetSecurityCallDone && dispatch(doGetSecurityCall(true));
			let dupGetSecuriyTableData = { tables: [], columns: [] };
			const mappedTablesColumns = mapTablesColumnsWithUniqueKey({tables, mode});
			let expressionArr = res.expressions.map((ele, i) => {
				const entryNamesArrObj = ele.on.map(id => ((id === null) ? id+'' : mappedTablesColumns.get(id))).filter(id => id);
				let data = {
					'Expression Name': ele.expressionName,
					'Entity Names': entryNamesArrObj.reduce((acc, cur) => {
						if(cur === 'null') {
							return acc;
						}
						if (ele.expressionType !== 'column') { 
							return (acc ? `${acc},` : '') + (cur?.tableAlias ? cur.tableAlias : '' );
						}
						return (acc ? `${acc},` : '') + ((cur?.tableAlias && cur?.columnAlias) ? `${cur?.tableAlias}.${cur.columnAlias}` : '');
					}, ''),
					'Expression Type': ele.expressionType,
					'Access Type': ele.accessType,
					'Execution Type': ele.executionType,
					Condition: ele.condition,
					expressionId: ele.expressionId,
					key: i,
					securityKeysToBeCheck: [], //entryNamesArrObj.map(entity => entity.key),
					tooltipInfo: [] //entryNamesArrObj.map(entity => entity.tooltipInfoObj)
				};
				entryNamesArrObj?.forEach(entity => {
					if(entity !== 'null') {
						data.securityKeysToBeCheck.push(entity.key);
						data.tooltipInfo.push(entity.tooltipInfoObj);
					} else {
						data.securityKeysToBeCheck.push(entity);
					}
				})
				ele.filter && (data.Filter = ele.filter);
				if (ele.accessType === 'deny') {
					if (ele.expressionType !== 'column') {
						dupGetSecuriyTableData.tables = [ ...dupGetSecuriyTableData.tables, ...entryNamesArrObj.map(entryName => (entryName !== 'null' ? entryName.tableAlias : '' )).filter(fil => fil) ];
					} else {
						entryNamesArrObj.forEach((entryName) => {
							// let arr = name.split('.');
							if(entryName !== 'null') {
								dupGetSecuriyTableData.columns = [
									...dupGetSecuriyTableData.columns,
									{ table: entryName.tableAlias, column: entryName.columnAlias, key: entryName.key }
								];
							}
						});
					}
				}
				// retriveExprns.push(ele.expressionId);
				return data;
			});
			dispatch(setGetSecurityTableData(dupGetSecuriyTableData));
			dispatch(
				saveExpressionData({
					// expressionIds: retriveExprns,
					action: 'retrive'
				})
			);
			if (res.expressions.length) {
				// !isValidatedTableShow && dispatch(setShowValidatedTable(true));
				dispatch(metadataUndoRedoPurpose([{key: 'isInfoShow', value: false}, {key: 'isValidatedTableShow', value: true}]));
			} else {
				// isValidatedTableShow && dispatch(setShowValidatedTable(false));
				dispatch(metadataUndoRedoPurpose([{key: 'isInfoShow', value: true},{key: 'isValidatedTableShow', value: false},{key: 'securityTableData', value: []},{key: 'expressionName', value: ""} ]));
			}
			expressionArr?.length && dispatch(setSecurityTableData(expressionArr));
			setShowContent(true);
		},
		(err) => {
			// Notify.error({ ...err, type: 'Network Call' });
			!isInfoShow && dispatch(setIsInfoShow(true));
			setShowContent(true);
		}
	);
};

// export const handleSecurityFormDelete = ({ setSecurityTableData, securityFormData, selectedKeys }) => {
// 	let nonDeletedRows = securityFormData.filter((ele) => !selectedKeys.includes(ele.key));
// 	setSecurityTableData(nonDeletedRows);
// };
