// import { v4 as uuidv4 } from 'uuid';

import { Space, Tooltip, Typography } from 'antd';
import { TableOutlined, CaretRightOutlined, CaretDownOutlined } from '@ant-design/icons';
import { cubeFileDataAfterSave, savedCubeDetailsForEdit, saveCubeMetadataFileDetails, setCubeFieldsData, setCubeIndeterminate, setCubeMode, setVisibleCheckValue, updateFieldValues } from '../../redux/actions/cube.actions';
// import { CubeDrag } from './hrCubeDrag';
import cubeRequests from '../../base/requests/cube.requests';
import { isError, isObjectLike } from 'lodash-es';
import notify from '../hi-notifications/notify';
import ErrorList from 'antd/lib/form/ErrorList';
import { fileBrowserActions } from '../../redux/actions';
import { v4 as uuidv4 } from "uuid";
import { cubeEditorMeasureData } from './cubeConstants';
import { formatArrayAsCommaSeparated } from './cubeSemanticFields';
import { cloneDeep } from 'lodash-es';
const { Text, Paragraph } = Typography;
// const { handleCubeSaveRequest } = cubeRequests(dispatch);

export const parseCubeDirAndFile = (path = '') => {
	const pathParts = path.split('/').filter(Boolean);
	const file = pathParts.pop() || '';
	const dir = pathParts.join('/');
	return { dir, file };
};

export const buildCubeFieldsDataFromCubes = (cubes) => {
	const cubeData = { children: [], hierarchyData: { isHierarchyPresent: false, hierarchyList: [] } };
	if (!cubes?.length) {
		return cubeData;
	}
	cubeData.id = cubes[0].id;
	cubeData.domainName = cubes[0].domainName || '';
	cubeData.cubeDescription = cubes[0].description || '';
	cubeData.children = cubes[0].dimensions.map(dimension => {
	  const {hierarchies} = dimension;
	  const {levels} = hierarchies[0];
	  let key = uuidv4();
	  const obj = {
	    resDimensionId : dimension.id,
	    resHierarchyId: dimension.hierarchies[0].id,
	    key,
	    fields: hierarchies[0].hierarchyName,
	    columnId: hierarchies[0].primaryColumnId,
	    tableId: hierarchies[0].tableId,
	    fieldsDropdownOpen: false,
	  };
	  cubeData.hierarchyData.hierarchyList.push({
		hierarchyName: obj.fields,
		hierarchyKey: key
	  })
	  cubeData.hierarchyData.isHierarchyPresent = true;
	  if(levels.length === 1 && hierarchies[0].hierarchyName === levels[0].levelName && hierarchies[0].primaryColumnId === levels[0].columnId) {
	    obj.resLevelId = levels[0].id;
	    obj.dataType= Object.values(levels[0].levelType)[0];
		obj.type= levels[0].levelType;
	    obj.isDimensionCheck = true;
	    obj.measure= { isMeasureCheck: false, DataType: '', Format: '' };
	    obj.isVisible = levels[0].visible;
	    obj.sort = { isSortCheck: true, value: 'Ascending' };
	    obj.aggregation = { isAggregationCheck: false, value: '' };
	    obj.isPartitionCheck= false;
		obj.table = levels[0].table;
		obj.column = levels[0].column;
		obj.columnName = levels[0].columnName;
        obj.semanticType = levels[0].semanticType || '';
        obj.synonyms = formatArrayAsCommaSeparated(levels[0].synonyms);
        obj.topic = formatArrayAsCommaSeparated(levels[0].topic);
        obj.formula = levels[0].formula || '';
        obj.filter = levels[0].filter || '';
        obj.example = levels[0].example || '';
        obj.description = levels[0].description || '';
	  } else {
	    obj.isHierarchy= true;
		obj.table = hierarchies[0].table;
		obj.column = hierarchies[0].column;
		obj.columnName = hierarchies[0].columnName;
	    obj.children = levels.map(level => {
	      return {
	        key: uuidv4(),
	        fieldsDropdownOpen: false,
	        isHierarchyChild: true,
	        parentKey: key,
	        columnId: level.columnId,
	        tableId: level.tableId,
			fields: level.levelName,
	        resLevelId : level.id,
	        dataType: Object.values(level.levelType)[0],
		    type: level.levelType,
	        isDimensionCheck : true,
	        measure: { isMeasureCheck: false, DataType: '', Format: '' },
	        isVisible : level.visible,
	        sort : { isSortCheck: true, value: 'Ascending' },
	        aggregation : { isAggregationCheck: false, value: '' },
	        isPartitionCheck: false,
			table: level.table,
			column: level.column,
			columnName: level.columnName,
            semanticType: level.semanticType || '',
            synonyms: formatArrayAsCommaSeparated(level.synonyms),
            topic: formatArrayAsCommaSeparated(level.topic),
            formula: level.formula || '',
            filter: level.filter || '',
            example: level.example || '',
            description: level.description || '',
	      }
	    })
	  }
	  return obj;
	})
	const measures = cubes[0].measures.map(measure => {
		let key = uuidv4();
		return {
		key,
		type: measure.measureType,
		isDimensionCheck : false,
		measure: { isMeasureCheck: true, DataType: cubeEditorMeasureData[0].dataType, Format: cubeEditorMeasureData[0].format[0]},
		isVisible : measure.visible,
		sort : { isSortCheck: false, value: '' },
		aggregation : {
			isAggregationCheck: true,
			value: measure.aggregator || 'Sum',
		},
		isPartitionCheck: false,
		columnId: measure.columnId,
		tableId: measure.tableId,
		fields: measure.measureName,
		fieldsDropdownOpen: false,
		resLevelId : measure.id,
		table: measure.table,
		column: measure.column,
		columnName: measure.columnName,
		semanticType: measure.semanticType || '',
		synonyms: formatArrayAsCommaSeparated(measure.synonyms),
		topic: formatArrayAsCommaSeparated(measure.topic),
		formula: measure.formula || '',
		filter: measure.filter || '',
		example: measure.example || '',
		description: measure.description || '',
		}
	  })
	cubeData.children.push(...measures);
	return cubeData;
};

export const convertResponseToCubeFieldsData = ({ dispatch, cubes }) => {
	const cubeData = buildCubeFieldsDataFromCubes(cubes);
	dispatch(setCubeFieldsData({ mode: 'edit', value: cubeData })); //pending
	return cubeData;
};

const syncCubeFieldsDataFromServer = ({ dispatch, dir, file, cubes }) => {
	const applyCubes = (cubesData) => {
		if (cubesData?.length && cubesData[0]?.id != null) {
			convertResponseToCubeFieldsData({ dispatch, cubes: cubesData });
		}
	};
	if (cubes?.length && cubes[0]?.id != null) {
		applyCubes(cubes);
		return;
	}
	cubeRequests(dispatch).getCubeForEdit({ dir, file }, '', (res) => {
		applyCubes(res?.cubes);
	});
};

export const fbOnClickHandler = (dispatch) => {
	dispatch(fileBrowserActions.setGlobalFbVisibility(true));
	dispatch(fileBrowserActions.setShowFileBrowser(true));
}

export const saveCubeSelectedMetadataFileDetails = ({dispatch, record, cubeMode}) => { 
	// let fileDtls = record.path.split('/');
	// dispatch(saveCubeMetadataFileDetails({
	// 	path: fileDtls[0],
	// 	fileName: fileDtls[1],
	// }))
	// if(cubeMode === 'edit') {
	// 	dispatch(setCubeFieldsData({ mode: 'edit', title, key: uuidv4() }));
	// }
}
export const validateCubeDomainAndDescription = ({
  cubeFieldsData,
  dispatch,
  domainLabel = "Domain",
  descriptionLabel = "Description",
}) => {
	const domain = formatArrayAsCommaSeparated(cubeFieldsData.domainName || []).trim();
	const description = (cubeFieldsData.cubeDescription ?? '').trim();
	const missing = [];
	if (!domain) missing.push(domainLabel);
	if (!description) missing.push(descriptionLabel);
	if (missing.length) {
		notify(dispatch).warning({
			message: `${missing.join(' and ')} ${missing.length > 1 ? 'are' : 'is'} required before saving.`,
			type: 'Front End',
		});
		return false;
	}
	return true;
};

// formdata
export const handleCubeFormdata = ({ dispatch, location, type, fileName, cubeFieldsData, metadataTablesData, metadataDetails, cubeMode }) => {
	if (!validateCubeDomainAndDescription({ cubeFieldsData, dispatch })) {
		return;
	}
	const submitCubeSave = (fieldsData) => {
	const { dataSource } = metadataTablesData;
	const formData = {
		dataSource, //: {  // discussion
		fileName, //: 'Cube Creation',
		location, //: '1463377807724',
		cubes: [
			{
				setCache: true,
				caption: 'Cube Creation',
				defaultMeasure: 'defaultMeasureName',
				description: (fieldsData.cubeDescription ?? '').trim(),
				enabled: true,
				cubeName: fileName, //'Sample Cube',
				annotation: 'To be discussed',
                domainName: formatArrayAsCommaSeparated(fieldsData.domainName || []),
				dimensions: fieldsData.children.reduce((acc, child) => {
					if (child.isHierarchy) {
						let obj = {
							dimensionName: child.fields, //
							dimensionType: child.type, // need discussion
							visible: true, // need discussion
							table: child.table,
							column: child.column,
							tableId: child.tableId,
							columnName: child.columnName,
							columnId: child.columnId,
							hierarchies: [
								{
									hierarchyName: child.fields,
									visible: true, // need discussion,
									primaryColumnId: child.columnId, // need discussion,
									tableId: child.tableId,
									hierarchyType: child.type,
									table: child.table,
									column: child.column,
									columnName: child.columnName,
									levels: child.children.map((hChild) => {
										let childObj = {
											levelName: hChild.fields, //.split('_').join(' '), //meet cancellation status',
											columnId: hChild.columnId, // What erver the column id for  meet_cancellation_status
											visible: hChild.isVisible,
											table: hChild.table,
											column: hChild.column,
											columnName: hChild.columnName,
											levelType: hChild.type, // data type
											tableId: hChild.tableId, // id for that table
                                            semanticType: hChild.semanticType || '',
											synonyms: hChild.synonyms || '',
											topic: hChild.topic || '',
											formula: hChild.formula || '',
											filter: hChild.filter || '',
											example: hChild.example || '',
											description: hChild.description || '',
										};
										if(type !== 'saveAs') {
											(hChild.resLevelId) && (childObj.id = hChild.resLevelId);
										}
										(cubeMode === 'edit' ) && hChild.isDelete && (childObj.action = 'delete');
										return childObj;
									})
								}
							]
						};
						if(type !== 'saveAs') {
							(child.resDimensionId) && (obj.id = child.resDimensionId);
							(child.resHierarchyId) && (obj.hierarchies[0].id = child.resHierarchyId);
						}
						(cubeMode === 'edit' ) && child.isDelete && (obj.action = 'delete');
						acc.push(obj);
					} else if (child.isDimensionCheck) {
						let obj = {
							dimensionName: child.fields, //
							dimensionType: child.type, // dataType
							visible: child.isVisible,
							table: child.table,
							column: child.column,
							tableId: child.tableId,
							columnName: child.columnName,
							columnId: child.columnId,
							hierarchies: [
								{
									hierarchyName: child.fields,
									visible: child.isVisible,
									primaryColumnId: child.columnId, // first row feild
									tableId: child.tableId,
									hierarchyType: child.type,
									table: child.table,
									column: child.column,
									columnName: child.columnName,
									levels: [
										{
											levelName: child.fields, //.split('_').join(' '), //meet cancellation status',
											columnId: child.columnId, // What erver the column id for  meet_cancellation_status
											visible: child.isVisible,
											table: child.table,
											column: child.column,
											columnName: child.columnName,
											levelType: child.type, // data type
											tableId: child.tableId,// id for that table
                                            semanticType: child.semanticType || '',
											synonyms: child.synonyms || '',
											topic: child.topic || '',
											formula: child.formula || '',
											filter: child.filter || '',
											example: child.example || '',
											description: child.description || '',
										}
									]
								}
							]
						};
						if(type !== 'saveAs') {
							(child.resLevelId) && (obj.hierarchies[0].levels[0].id = child.resLevelId);
							(child.resDimensionId) && (obj.id = child.resDimensionId);
							(child.resHierarchyId) && (obj.hierarchies[0].id = child.resHierarchyId);
						}
						(cubeMode === 'edit' ) && child.isDelete && (obj.action = 'delete');
						acc.push(obj);
					}
					return acc;
				}, []),
				measures: fieldsData.children.reduce((acc, child) => {
					if (!child.isHierarchy && child.measure.isMeasureCheck) {
						let obj = {
							measureName: child.fields, // column name
							visible: child.isVisible,
							aggregator: child.aggregation.value, // need discussion
							columnId: child.columnId,
							tableId: child.tableId,
							formatString: child.measure.Format,
							measureType: child.type,
							columnName: child.columnName,
							table: child.table,
							column: child.column,
                            semanticType: child.semanticType || '',
							synonyms: child.synonyms || '',
							topic: child.topic || '',
							formula: child.formula || '',
							filter: child.filter || '',
							example: child.example || '',
							description: child.description || '',
						};
						(cubeMode === 'edit' ) && child.isDelete && (obj.action = 'delete');
						if(type !== 'saveAs') {
							(child.resLevelId) && (obj.id = child.resLevelId);
						}
						acc.push(obj);
					}
					return acc;
				}, [])
			}
		],
		metadata:{location:metadataDetails?.path, metadataFileName:metadataDetails?.fileName},
	};
	if(type === 'saveAs') {
		formData.newLocation = location;
		delete formData.location;
	}
    if(cubeMode !== 'edit' || type === 'saveAs') {
		formData.uniqueId = true;
	} else if(cubeMode === 'edit' && type !== 'saveAs') {
		formData.cubes[0].id = fieldsData.id;
	}
	cubeRequests(dispatch).handleCubeSaveRequest(
		formData,
		'',
		(res) => {
			const persistSavedCubeState = ({ dir, file, cubes }) => {
				dispatch(cubeFileDataAfterSave({ dir, file }));
				dispatch(savedCubeDetailsForEdit({ location: dir, fileName: file }));
				dispatch(setCubeMode('edit'));
				syncCubeFieldsDataFromServer({ dispatch, dir, file, cubes });
			};

			if('cubes' in res.data[0]) { // remove
				const {cubes, ...fileDetails } = res.data[0];
				const {dir, file } = parseCubeDirAndFile(fileDetails.path);
				persistSavedCubeState({ dir, file, cubes });
				dispatch(fileBrowserActions.saveFileinFb([fileDetails]));
				document.title = `${fileDetails.title} | HI:Cube`;
			} else {
				const {dir, file} = parseCubeDirAndFile(res.data[0].path);
				persistSavedCubeState({ dir, file, cubes: res.data[0].cubes });
				dispatch(fileBrowserActions.saveFileinFb(res.data));
				document.title = `${res.data[0].title} | HI:Cube`;
			}
			// dispatch(setCubeFieldsData({mode: 'reset'}));
			// dispatch(setCubeMetadataTablesData(null));
			// notify(dispatch).success({
			// 	type: 'Network Call',
			// 	...res
			// });
		},
		() => {}
	);
	};

	if (cubeMode === 'edit' && type !== 'saveAs' && cubeFieldsData?.id == null) {
		cubeRequests(dispatch).getCubeForEdit({ dir: location, file: fileName }, '', (res) => {
			if (res?.cubes?.length && res.cubes[0]?.id != null) {
				const cubeData = buildCubeFieldsDataFromCubes(res.cubes);
				dispatch(setCubeFieldsData({ mode: 'edit', value: cubeData }));
				submitCubeSave({ ...cubeFieldsData, id: res.cubes[0].id });
			} else {
				notify(dispatch).error({
					type: 'Front End',
					message: 'Unable to save: cube identifier is missing. Please reopen the cube and try again.',
				});
			}
		});
		return;
	}
	submitCubeSave(cubeFieldsData);
};

export const cubeTableDataSource = ({dataSource}) => {
	dataSource = cloneDeep(dataSource);
	dataSource = dataSource.filter(ele => {
		if(ele.isDelete) {
			return false;
		} else {
			if(ele.isHierarchy) {
			    ele.children = ele.children.filter(child => !child.isDelete);
			}
		}
		return true;
	})
	return dataSource;
}

export const handleLevelToHierarchy = ({restCurchild, state, dupCubeFieldsData, parentKey}) => { // test cases
	restCurchild.fields = handleHierarachyTitle({
		value: restCurchild.fields,
		cubeFieldsData: state.cubeFieldsData,
		ignoreKey: parentKey
	});
	dupCubeFieldsData.hierarchyData.hierarchyList.push({
		hierarchyName: restCurchild.fields,
		hierarchyKey: restCurchild.key
	});
	dupCubeFieldsData.hierarchyData.isHierarchyPresent = true;
	if(["test"].includes(process.env.NODE_ENV)) {
		return dupCubeFieldsData;
	}
}

export const handleCubeTitleEdit = ({ value, cubeInitialList, dispatch }) => {
	// let isAliasTaken = (cubeInitialList || []).some((listItem) => listItem.cubeFieldsData.title === value);
	// if (isAliasTaken) {
	// 	let indexs = [],
	// 		reqIndex;
	// 	let splitedArr = value.split('_');
	// 	cubeInitialList.forEach((listItem) => {
	// 		const curSplitArr = listItem.cubeFieldsData.title.split('_');
	// 		const curLstWrd = curSplitArr.pop();
	// 		if (
	// 			listItem.cubeFieldsData.title.match(value) &&
	// 			splitedArr[0] === curSplitArr[0] &&
	// 			splitedArr.length === curSplitArr.length &&
	// 			!isNaN(Number(curLstWrd)) //&&
	// 			// record.name !== table.name
	// 		) {
	// 			indexs.push(curLstWrd);
	// 		}
	// 	});
	// 	if (indexs.length) {
	// 		for (let i = 1; i <= indexs.length; i++) {
	// 			if (!indexs.includes(i + '')) {
	// 				reqIndex = i;
	// 				i = indexs.length + 1;
	// 			}
	// 		}
	// 		!reqIndex && (reqIndex = indexs.length + 1);
	// 	} else {
	// 		reqIndex = 1;
	// 	}
	// 	value += `_${reqIndex}`;
	// }
	// dispatch(updateFieldValues({ updateName: 'title', value }));
};

export const handleHierarchyData = ({state, record}) => { // test cases
	let hierarchyData = {
		hierarchyList: [ ...state.cubeFieldsData.hierarchyData.hierarchyList ],
		isHierarchyPresent: state.cubeFieldsData.hierarchyData.isHierarchyPresent
	};
	hierarchyData.hierarchyList.push({
		hierarchyName: record.fields,
		hierarchyKey: record.key
	});
	hierarchyData.isHierarchyPresent = true;
	return hierarchyData;
}

export const handleHierarachyTitle = ({ // test cases
	updateName,
	value,
	recordKey,
	isHierarchyChild,
	hierarchyKey,
	dispatch,
	cubeFieldsData,
	ignoreKey
}) => {
	let hLists = cubeFieldsData.children;
	if(isHierarchyChild) {
		hLists = cubeFieldsData.children.find(ele => ele.key === hierarchyKey).children;
	}
	let isAliasTaken = (hLists || []).some((listItem) => !listItem.isDelete && (listItem.fields === value) && (ignoreKey !== listItem.key));
	if (isAliasTaken) {
		let indexs = [],
			reqIndex;
		let splitedArr = value.split('_');
		hLists.forEach((listItem) => {
			const curSplitArr = listItem.fields.split('_');
			const curLstWrd = curSplitArr.pop();
			if (
				listItem.fields.match(value) &&
				splitedArr[0] === curSplitArr[0] &&
				splitedArr.length === curSplitArr.length &&
				!isNaN(Number(curLstWrd)) //&&
				// record.name !== table.name
			) {
				indexs.push(curLstWrd);
			}
		});
		if (indexs.length) {
			for (let i = 1; i <= indexs.length; i++) {
				if (!indexs.includes(i + '')) {
					reqIndex = i;
					i = indexs.length + 1;
				}
			}
			!reqIndex && (reqIndex = indexs.length + 1);
		} else {
			reqIndex = 1;
		}
		value += `_${reqIndex}`;
	}
	!["test"].includes(process.env.NODE_ENV) && updateName && dispatch(updateFieldValues({ updateName, value, recordKey, isHierarchyChild, hierarchyKey, parentRecord: hLists }));
	return value;
};

export const uniqueHierarchyTitle = (cubeFieldsData) => { // test cases
	let title;
		// arr = [],
		// hLists = cubeFieldsData.children;
	// for (let i = 0; i < hLists.length; i++) {
	// 	arr.push(hLists[i].fields.split('_')[1]);
	// }
	// no = arr.length + 1;
	for (let i = 1; i <= cubeFieldsData.children.length; i++) {
		let title = handleHierarachyTitle({
			value: `hierarchy_${i}`,
			cubeFieldsData: cubeFieldsData
		});
		if(`hierarchy_${i}` === title) {
			i = cubeFieldsData.children.length + 1;
		}
	}
	return title;
};

export const handleRemoveHierarchy = ({dupCubeFieldsData, record}) => { // test cases
	dupCubeFieldsData.hierarchyData.hierarchyList = dupCubeFieldsData.hierarchyData.hierarchyList.filter(
		(lstItem) => lstItem.hierarchyKey !== record.key
	);
	if (!dupCubeFieldsData.hierarchyData.hierarchyList.length) {
		dupCubeFieldsData.hierarchyData.isHierarchyPresent = false;
	}
}

export const uniqueCubeTitle = (cubeInitialList) => {
	// let no,
	// 	arr = [];
	// for (let i = 0; i < cubeInitialList.length; i++) {
	// 	arr.push(cubeInitialList[i].cubeFieldsData.title.split('_')[1]);
	// }
	// no = arr.length + 1;
	// for (let i = 1; i < arr.length; i++) {
	// 	if (!arr.includes(i + '')) {
	// 		no = i;
	// 		i = arr.length;
	// 	}
	// }
	// return `Cube_${no}`;
};

// export const uniqueDimensionTitle = (cubeFieldsData) => {
// 	let no,
// 		arr = [],
// 		hLists = cubeFieldsData.children;
// 	for (let i = 0; i < hLists.length; i++) {
// 		arr.push(hLists[i].fields.split('_')[1]);
// 	}
// 	no = arr.length + 1;
// 	for (let i = 1; i < arr.length; i++) {
// 		if (!arr.includes(i + '')) {
// 			no = i;
// 			i = arr.length;
// 		}
// 	}
// 	return `hierarchy_${no}`;
// };

export const handleVisibleCheckStatus = ({ cubeFieldsData, dispatch, e, record }) => {
	let checkedListLen = 0;
	let cubeFieldsDataLngth = 0;
	cubeFieldsData.children.forEach((obj) => {
		if (obj.isHierarchy) {
			obj.children.forEach((objChild) => {
				cubeFieldsDataLngth++;
				if (objChild.key === record.key) {
					if (e.target.checked) {
						checkedListLen++;
					}
				} else if (objChild.isVisible) {
					checkedListLen++;
				}
			});
		} else {
			cubeFieldsDataLngth++;
			if (obj.key === record.key) {
				if (e.target.checked) {
					checkedListLen++;
				}
			} else if (obj.isVisible) {
				checkedListLen++;
			}
		}
	});
	if (checkedListLen === 0) {
		dispatch(setVisibleCheckValue(false));
		dispatch(setCubeIndeterminate(false));
	} else if (checkedListLen === cubeFieldsDataLngth) {
		dispatch(setVisibleCheckValue(true));
		dispatch(setCubeIndeterminate(false));
	} else {
		dispatch(setVisibleCheckValue(false));
		dispatch(setCubeIndeterminate(true));
	}
};

export const handleHeader = ({ title, isCubeTableModeNormal }) => ({
	// initial: {
	// 	text: { title: 'Cubes', editable: false },
	// 	firstButton: {
	// 		title: 'Schedule'
	// 		// callBack: () => {
	// 		// 	dispatch(setCubeState('Schedule'));
	// 		// }
	// 	},
	// 	secondButton: {
	// 		title: 'Add New'
	// 		// callBack: () => {
	// 		// 	dispatch(setCubeState('Add New'));
	// 		// }
	// 	}
	// },
	// Schedule: {
	// 	text: { title, editable: true },
	// 	firstButton: {
	// 		title: 'Cancel'
	// 		// callBack: () => {
	// 		// 	dispatch(setCubeState('initial'));
	// 		// }
	// 	},
	// 	secondButton: {
	// 		title: isCubeTableModeNormal ? 'Switch to Advance' : 'Switch to Normal'
	// 		// callBack: () => {
	// 		// 	dispatch(setCubeState('initial'));
	// 		// }
	// 	}
	// }
});

export const cubeSecOneColumns = ({}) => {
	let columnsData = [];
	[ 'row' ].forEach((ele) => {
		columnsData.push({
			title: ele,
			dataIndex: ele,
			key: ele,
			className: 'table-ellipsis',
			render: (name, record) => {
				// console.log(name, record);
				if (!record.columnId)
					return (
						<Space>
							{/* <TableOutlined /> */}
							<Tooltip title={name}>
								<Text style={{ marginBottom: 0 }} ellipsis={true}>
									{name}
								</Text>
							</Tooltip>
						</Space>
					);
				return (
					<Tooltip title={name}>
						<Text style={{ marginBottom: 0 }} ellipsis={true}>
							{/* <CubeDrag column={record} /> */}
						</Text>
					</Tooltip>
				);
			}
		});
	});
	return columnsData;
};

// export const cubeSecOneDataSourceList = ({ tables }) => {
// 	let data = [];
// 	for (let ele in tables) {
// 		let rowData = {
// 			key: tables[ele].name,
// 			row: tables[ele].name,
// 			columnsData: Object.values(tables[ele].columns)
// 		};
// 		data.push(rowData);
// 	}
// 	return data;
// };
