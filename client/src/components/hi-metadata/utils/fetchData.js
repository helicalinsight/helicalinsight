import requests from '../../../base/requests'
import { updateFetchedDataSources, updateLoadingStatus, setMetadataLoadingStatus } from '../../../redux/actions'
import { uuid } from '../../../utils/uuid'
import { getMetadata } from './getMetadata'
const randomString = uuid

export function intToStringFunc(dataSources) {
    dataSources?.forEach(ele => {
        ele.data && (ele.data.id += '') // 6234
    })
    return dataSources;
}

export const fetchGetDataSource = ({ apiRef, dispatch, mode = 'create', store, location = false, uuid = false, activeDataSource = false, getApi = () => { } }) => {
    let data = {};
    dispatch(setMetadataLoadingStatus({ isDataSourceFetched: false }))
    const apiInstance = requests.metadata(dispatch).getDataSource((res) => {
        data = res;
        dispatch(updateLoadingStatus({ type: 'getDatasource', status: true }))
        const apiInstance = requests.metadata(dispatch).listDataSources({ "classifier": "all" }, (listDsRes) => {
            intToStringFunc(listDsRes.dataSources) // 6234
            dispatch(setMetadataLoadingStatus({ isDataSourceFetched: true }))
            let result = { allDataSources: listDsRes.dataSources, ...data }
            dataProcessor({ apiRef, data: result, dispatch, mode, store, location, uuid, activeDataSource, getApi })
            dispatch(updateLoadingStatus({ type: 'listDataSources', status: true }))
        }, () => {
            dispatch(updateLoadingStatus({ type: 'listDataSources', status: true }))
        })
        getApi(apiInstance)

    }, () => {
        dispatch(updateLoadingStatus({ type: 'getDatasource', status: true }))
    })
    getApi(apiInstance)
}

export const dataProcessor = ({ apiRef, data, dispatch, mode, store, location, uuid, activeDataSource, returnDsToRender = false, getApi = () => { } }) => {
    let sortedAllDataTypes, advancedDataSourceTypes;
    let metaDataSourceList = [];
    let filteredSupportedList = [];
    let categoryTypes = [];
    let categoryInfo = {};
    let categories = {};
    sortedAllDataTypes = data.dataSources.sort((a, b) => a["name"].localeCompare(b["name"]));
    advancedDataSourceTypes = data.dataSourceTypes.sort((a, b) => a.name.localeCompare(b.name));
    sortedAllDataTypes.map(function (val) {
        if (categoryTypes.indexOf(val.categoryType.toLowerCase()) == -1) {
            categoryTypes.push(val.categoryType);
            categoryInfo[val.categoryType] = val.categoryName;
        }
    })
    sortedAllDataTypes.map(function (val) {
        let result = val;
        if (val.categoryType.toLowerCase() === "supported") {
            filteredSupportedList.push(result);
        }
        else if (val.categoryType.toLowerCase() != "supported") {
            metaDataSourceList.push(result);
        }
    })
    let dataSources = data.dataSources, allDataSources = data.allDataSources
    let datasourceListToRender = {}
    allDataSources = allDataSources.map(ds => {
        if (!ds.driver) {
            ds.driver = 'dynamicSwitch'
            ds.data.driver = 'dynamicSwitch'
        }
        return ds
    })
    dataSources.map(category => {
        allDataSources.map(ds => {
            if (category.driver && ((category.sendVendorName && ds.vendorName === category.name)
            || (!category.sendVendorName && !ds.vendorName && ds.driver === category.driver))) {
                let key = randomString()
                if (!(category.name in datasourceListToRender)) {
                    categories[key] = { ds: ds, category }
                    datasourceListToRender[category.name] = {
                        name: category.name,
                        children: [],
                        key,
                        uuid: key,
                        keyPath: key,
                        category: 'dsGroup',
                        imgUrl: (category?.imgUrl || '')?.split('../')[1] || ''
                    }
                }
                let _key = randomString()
                datasourceListToRender[category.name].children.push({ ...ds, category: 'dataSource', children: [], driverType: category.name, keyPath: `${datasourceListToRender[category.name].keyPath}/${_key}`, key: _key, uuid: _key })
            }
        })

    })
    datasourceListToRender = Object.values(datasourceListToRender)
    if (returnDsToRender) {
        return datasourceListToRender
    }

    let dataForRedux = {
        supportedDataSourceTypes: filteredSupportedList,
        allDataSourceTypes: sortedAllDataTypes,
        metaDataSourceList: metaDataSourceList,
        driversList: data.driversList,
        allDataSources: data.allDataSources,
        dataSourceTypes: advancedDataSourceTypes,
        datasourceListToRender,
        categories,
        mode
    }
    if (mode === 'edit') {
        dispatch(setMetadataLoadingStatus({ isMetadataFetched: false}));
        const apiInstance = getMetadata({
            store, dispatch,
            location,
            uuid,
            returnFetched: true,
            // returnFetched: false,
            datasourceListToRender,
            mode
        })
        getApi(apiInstance)
    }
    let expandKeysHierarchy = []
    const expandTableUI = ({ expandKeysHierarchy = [], index = 0 }) => {
        if (!expandKeysHierarchy.length) {
            return
        }
        let elem = document.getElementById(expandKeysHierarchy[index])?.previousElementSibling
        if (!elem) return
        elem.click()
        try {
            elem.scrollIntoView({
                behavior: 'smooth'
            })
        }
        catch (e) {
            console.log('failed scrolling to view')
        }
        if (expandKeysHierarchy.length >= index + 1) {
            setTimeout(() => {
                expandTableUI({ expandKeysHierarchy, index: index + 1 })
            }, 1000);
        }
    }
    if (activeDataSource) {
        let currentDS = null
        let { id, type, dir } = activeDataSource.data
        datasourceListToRender.every(eachDS => {
            let result = false
            // let dataFound = false
            eachDS.children.every(child => {
                if (child?.data?.id == id &&
                    child?.data?.type === type
                    && ('dir' in child.data ? child.data.dir === dir : true)
                ) {
                    result = true
                    // dataFound = child
                    currentDS = child
                    return false
                }
                if (result) {
                    return false
                }
                return true
            })
            if (currentDS) {
                return false
            }
            return true
        })
        expandKeysHierarchy = currentDS.keyPath.split('/')
    }

    dispatch(updateFetchedDataSources(dataForRedux))
    setTimeout(() => {
        expandTableUI({ expandKeysHierarchy })
    }, 1000);

}
