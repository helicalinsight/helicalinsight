import { fileBrowserActions } from '../../redux/actions';
import requests from '../../base/requests';
import Query from '../../utils/query';

export function deleteItemById(files, deleteKey, deleteValue) {
	const deleteRow = (data) => {
	  const updatedFiles = [];
	  if (!data) return [];
	  for (let i = 0; i < data.length; i++) {
		if (data[i][deleteKey] !== deleteValue) {
		  updatedFiles.push(data[i]);
		  if (data[i].children && data[i].children.length > 0) {
			data[i].children = deleteRow(data[i].children);
		  }
		}
	  }
	  return updatedFiles;
	};
	return deleteRow(JSON.parse(JSON.stringify(files)));
  }

export const getPasteFormData = ({copyOrCutItemDetails={}, record={}, item={}}) => {
	return {
	 sourceArray: JSON.stringify([""]),
	 action: copyOrCutItemDetails.action,
	 formData: {
	   "sourceUrl": copyOrCutItemDetails.sourceUrl,
	   "destinationUrl": record.path,
	   "sourcePermission": copyOrCutItemDetails.sourcePermission,
	   "destPermission": record.permissionLevel,
	   "isConflictSkip": item.isSkipped
	 }
   }
 }

 export const handleCheckFileInNewResource = ({files, sourceName, sourceType}) => {
	if (!files) return false;
	let key = 'name';
	if(sourceType === 'file') {
		key = 'description';
	}
	return files?.find(ele => {
		if(ele.type !== sourceType) {
			return false;
		}
		if (ele[key] !== sourceName) {
			return false;
		}
		return true;
	})
 }

//  export const handleCheckFileInNewResource = ({files, sourceName, key}) => {
// 	const deleteRow = (data) => {
// 		if (!data) return false;
// 		return data?.find(ele => {
// 			if (ele[key] !== sourceName) {
// 				if (ele.children && ele.children.length > 0) {
// 					return deleteRow(ele.children);
// 				}
// 				return false;
// 			} else {
// 				return true;
// 			}
// 		})
// 	};
// 	return deleteRow(files);
//  }

// export const handleCutCopyPasteDisable = ({item, copyOrCutItemDetails, record}) => {
// 	if(!record) {
// 		return true;
// 	}
//    if(item.id === 'pst') {
// 	if(Number(record.permissionLevel) >= 3) {
// 		return !Boolean(copyOrCutItemDetails);
// 	} else {
// 		return true;
// 	}
//    }
//    if(item.id === 'cpy') {
// 	return !(Number(record.permissionLevel) >= 2);
//    }
//    if(item.id === 'cut') {
// 	return !(Number(record.permissionLevel) >= 4);
//    }
// }

export const handleShareOptionsWithSearchTerm = ({tabData=[], debouncedSearchTerm={value: ''}, tabKey}) => {
	return tabData.filter(ele => {
		if (debouncedSearchTerm.col === null || debouncedSearchTerm.value === '') {
		  return true;
		}
		if ((debouncedSearchTerm.col?.toLowerCase() !== 'organization') && (debouncedSearchTerm.col?.toLowerCase() !== 'description')) { // 1st col after check box
		  return ele.name?.toLowerCase().includes(debouncedSearchTerm.value?.toLowerCase())
		}
		return (tabKey === "organization" ? ele.description : ele.orgName)?.toLowerCase().includes(debouncedSearchTerm.value?.toLowerCase())
	  })
}

export function searchOptions(files, addPath = false) {
	const paths = [];
	let temp = '';
	function calcPaths(data, parent = { path: '', breadcrumbs: [] }) {
		for (let i = 0; i < data.length; i++) {
			const name = data[i].type === 'file' ? 'title' : 'name';
			temp = parent.path + data[i][name];
			if (addPath) data[i].logicalPath = temp;
			paths.push({
				path: temp,
				breadcrumbs: [ ...parent.breadcrumbs, { name: data[i][name], id: data[i].path, type: data[i].type } ]
			});
			if (data[i].children && data[i].children.length > 0) {
				let newParent = {
					path: parent.path + data[i][name] + ' / ',
					breadcrumbs: [
						...parent.breadcrumbs,
						{ name: data[i][name], id: data[i].path, type: data[i].type }
					]
				};
				calcPaths(data[i].children, newParent);
			}
		}
		return data;
	}
	const newData = calcPaths(files);
	if (addPath) return [ newData, paths ];
	return paths;
}

export function filterByType(files, extArray) {
	const results = [];
	files.forEach((ele) => {
		if (ele.type === 'file' && extArray.includes(ele.extension)) {
			return results.push(ele);
		} else if (ele.type === 'folder') {
			if (ele.children.length > 0) {
				const res = filterByType(ele.children, extArray);
				if (!res.length) return;
				ele.children = res;
				return results.push(ele);
			} else {
				// this means "all" option has been selected, allowing empty folders
				// Please refer constants.js in filebrowser to check all filter options 
				if (extArray.length > 1) results.push(ele);
			}
		}
	});
	return results;
}

export function addFolder(files, folderObj) {
	const parentPath = folderObj.path.split('/').filter((e) => e !== '');
	if (parentPath.length === 1) {
		folderObj.path = parentPath[0];
		return files ? [ ...files, { ...folderObj } ] : [folderObj];
	}
	const addToFolder = (data, folderObj) => {
		for (let i = 0; i < data.length; i++) {
			const pathString =
				parentPath.length === 2 ? parentPath[0] : parentPath.slice(0, parentPath.length - 1).join('/');
			if (data[i].type === 'folder' && data[i].path === pathString) {
				//folderObj['logicalPath'] = `${data[i].logicalPath}/${folderObj.name}`;
				const children = [ ...data[i].children, folderObj ];
				data[i].children = children;
			}
			if (data[i].type === 'folder' && data[i].children.length > 0) addToFolder(data[i].children, folderObj);
		}
		return data;
	};
	return addToFolder(files, folderObj);
}

export function addFileToFolder(files, file, folderPath) {
	const addFile = (data) => {
	  for (let i = 0; i < data.length; i++) {
		if (data[i].type === "folder" && data[i].path === folderPath) {
		  let updatedData = null
		  if(Array.isArray(file)){
			  updatedData = file.map(e => {
				if(e.name?.split('.')?.length )
				  e.name = e.name.includes(`.${e.extension}`) ? e.name : `${e.name}.${e.extension}`
				  return e
			  })
		  }
		//   if(typeof file === "object"){
		// 	  updatedData = [{...file, name:`${file.name}.${file.extension}`}]
		//   }
		  if(updatedData){
			const filteredList = data[i].children.filter(e => !updatedData.find(j => j.path === e.path))
			const children = [...filteredList, ...updatedData];
			data[i].children = children;
		  }
		}
		if (
		  data[i].type === "folder" &&
		  data[i].children &&
		  data[i].children.length > 0
		)
		  addFile(data[i].children);
	  }
	  return data;
	};
	return addFile(files);
  }
export function filterByFileType(files, type, typeValue) {
	return new Query().from(files).select('*').where(type, 'equals', typeValue).execute();
}

export function renameItemById(files, renameKeyPath, renameValue, isPublic, permission) {
	const renameRow = (data) => {
		for (let i = 0; i < data.length; i++) {
			if (data[i].path === renameKeyPath) {
				const key = data[i].type === 'file' ? 'title' : 'name';
				data[i][key] = renameValue;
				data[i]['lastModified'] = new Date().getTime();
				if (isPublic !== undefined && permission !== undefined) {
					data[i]["public"] = isPublic;
					data[i]["permissionLevel"] = typeof permission === "string" ? permission : String(permission);
        		}
			}
			if (data[i].type === 'folder' && data[i].children.length > 0) renameRow(data[i].children);
		}
		return data;
	};
	return renameRow(files);
}

export function searchData(data, searchTerm) {
	let results = [];
	function searchItems(list) {
		list.forEach((ele) => {
			const searchQuery = ele.type === 'file' ? ele.title : ele.name;
			const found = searchQuery.toLowerCase().trim().indexOf(searchTerm.toLowerCase().trim()) > -1;
			if (found) {
				results.push(ele);
			}
			if (!ele.children || ele.children.length === 0) return;
			if (ele.children && ele.children.length > 0 && !found) {
				const res = searchItems(ele.children);
			}
		});
	}
	searchItems(data);
	const doesFileTypeExistInOuterIndex = results.findIndex(obj => obj.type === "file");
    if(doesFileTypeExistInOuterIndex > -1){
      let obj = {}
      obj = {...results[doesFileTypeExistInOuterIndex], children: []}
      results.splice(doesFileTypeExistInOuterIndex, 1);
      results = [...results, obj]
    }
	return results;
}

export function fbGroupBy(key, data) {
	return data.reduce((results, item) => {
		const setResults = (file) => {
			if (file.type === 'file') {
				const fileExt = file.extension;
				const itemIndex = results.findIndex((e) => e.name === fileExt);
				if (itemIndex > -1) {
					results[itemIndex].children = [ ...results[itemIndex].children, file ];
				} else {
					results.push({
						name: file[key],
						children: [ file ],
						type: 'folder',
						id: JSON.stringify(Math.random())
					});
				}
			} else {
				for (let i = 0; i < file.children.length; i++) {
					setResults(file.children[i]);
				}
			}
		};
		setResults(item);
		return results;
	}, []);
}

const addLogicalPaths = (files) => {
	if (files.length === 0) return [];
	const newFiles = searchOptions(files, true);
	return newFiles;
};

export const fetchFileBrowserData = (dispatch, refresh, settings) => {
	dispatch(fileBrowserActions.setFbLoading(true));
	const makeRequest = () => {
		const fbRequests = requests.filebrowser(dispatch, settings);
		fbRequests.getResources(
			'',
			(res) => {
				// const filesWithLocations = addLogicalPaths(res);
				dispatch(
					fileBrowserActions.setFbContent({
						// data: filesWithLocations.length ? filesWithLocations[0] : [],
						data: res,
						error: null,
						loading: false
					})
				);
			},
			(error) => {
				dispatch(fileBrowserActions.setFbError(error));
			}
		);
	};
	if (refresh) {
		makeRequest();
		return;
	}
	dispatch((dispatch, getState) => {
		const files = getState().fileBrowser.files.data;
		if (files.length === 0) makeRequest();
		if (files.length > 0) dispatch(fileBrowserActions.setFbLoading(false));
	});
};

export const containsSameNamedResource = (resource, resourceList) => {

	for (let res of resourceList) {
	  if (res.name === resource.name 
		&& res.type === resource.type
	  ) {
		return res;
	  }
	}
  
	return false;
  }