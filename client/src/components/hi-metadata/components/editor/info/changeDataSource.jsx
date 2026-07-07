import React from 'react';
import { Select, Row, Col, Table, Button, Space, Empty, Tooltip, Skeleton } from 'antd';
import { useSelector, useStore } from 'react-redux';
import requests from '../../../../../base/requests';
import { useDispatch } from 'react-redux';
import { cloneDeep } from 'lodash-es';
import { metadataActions } from '../../../../../redux/actions';
import { uuid } from '../../../../../utils/uuid';
import notify from '../../../../hi-notifications/notify'
import { ReloadOutlined, InfoCircleOutlined } from '@ant-design/icons'
import { changeDatasourceUtil } from '../../../utils/changeDatasourceUtil';
import ChangeDataSourceSkeleton from '../../../../common/custom-icons/CustomSkeletons/changeDataSourceSkeleton';

const { Option } = Select;

export default function ChangeDataSource({ activeDS, onDrawerClose = () => { }, apiRef }) {
    const dataSourceTypes = useSelector(state => state.metadata.present.dataSourceTypes)
    const allDataSources = useSelector(state => state.metadata.present.allDataSources)
    const changeDSList = useSelector(state => state.metadata.present.changeDSList)
    const dataSourceState = useSelector(state => state.metadata.present.dataSourcesAddedToMetadata)
    const allDataSourceTypes = useSelector(state => state.metadata.present.allDataSourceTypes)
    const [testQueue, setTestQueue] = React.useState([])
    const [activeType, setActiveType] = React.useState('')
    const [loading, setLoading] = React.useState(false)
    const store = useStore()
    let loadingQueue = []
    const dispatch = useDispatch()
    const handleDSTypeChange = (e, refresh = false) => {
        setActiveType(e)
        if (!refresh && (e in changeDSList)) {
            setLoading(false)
            return
        }
        let currentType = cloneDeep(dataSourceTypes.filter(type => type.name === e)[0])
        if (!currentType) {
            return
        }
        delete currentType.categoryName
        delete currentType.categoryType
        setLoading(true)
        const index = loadingQueue.indexOf(e);
        if (index != -1) {
            return
        }
        loadingQueue.push(e)
        apiRef.current = requests.metadata(dispatch).listDataSources(currentType, (res) => {
            apiRef.current = undefined;
            let result = cloneDeep(changeDSList)
            result[e] = res.dataSources.map((ds, index) => {
                ds.data.id += '' // 6234
                ds.sNo = index + 1
                ds.connId = ds.data.id
                ds.dir = ds.data.dir
                ds.key = uuid()
                ds.test = false
                return ds
            })
            dispatch(metadataActions.updateMetadataState({ key: 'changeDSList', value: result }))
            setLoading(false)
            if (index > -1) {
                loadingQueue.splice(index, 1); // 2nd parameter means remove one item only
            }
        }, () => {
            setLoading(false)
        })
    }
    const reset = () => {
        // dispatch(metadataActions.updateMetadataState({ key: 'changeDSList', value: {} }))
        handleDSTypeChange(activeType, true)
    }
    function remove(arrOriginal, elementToRemove) {
        return arrOriginal.filter(function (el) { return el !== elementToRemove });
    }
    const handleConnectionTest = ({ record }) => {
        let { dir, connId, type, classifier } = record
        let formData = { dir, id: connId, type, classifier }
        if (formData.dir === false) {
            delete formData.dir
        }
        let _list = cloneDeep(store.getState().metadata.present.changeDSList)
        let data = _list[activeType]
        let isAlreadyLoading = false
        data.forEach(ds => {
            if (ds.key === record.key) {
                isAlreadyLoading = ds.test === 'loading'
                ds.test = 'loading'
            }
            return ds
        })
        if (isAlreadyLoading) {
            return
        }
        // dispatch(metadataActions.updateMetadataState({ key: 'changeDSList', value: _list }))
        setTestQueue([...testQueue, record.key])
        requests.metadata(dispatch).quickTest(formData, res => {
            let _list = cloneDeep(store.getState().metadata.present.changeDSList)
            let data = _list[activeType]
            data.forEach(ds => {
                if (ds.key === record.key) {
                    ds.test = res.message.includes('successful') ? 'success' : 'failed'
                    ds.testMessage = res.message
                }
                return ds
            })
            setTestQueue(remove(testQueue, record.key))
            // notify(dispatch).success({
            //     type: 'Network Call',
            //     message: res.message
            // })
            dispatch(metadataActions.updateMetadataState({ key: 'changeDSList', value: _list }))
        }, () => {
            let _list = cloneDeep(store.getState().metadata.present.changeDSList)
            let data = _list[activeType]
            data.forEach(ds => {
                if (ds.key === record.key) {
                    ds.test = 'failed'
                    ds.testMessage = 'failed'
                }
                return ds
            })
            notify(dispatch).error({
                type: 'Network Call',
                message: 'Test connection failed'
            })
            setTestQueue(remove(testQueue, record.key))
            dispatch(metadataActions.updateMetadataState({ key: 'changeDSList', value: _list }))
        })

    }
    const handleDoubleClick = ({ record }) => {
        // let dataSourceLocal = cloneDeep(dataSourceState)
        // let currentType = record.driver ? allDataSourceTypes.filter(type => type.driver === record.driver)[0] : allDataSourceTypes.filter(type => type.driver === 'dynamicSwitch')[0]
        // dataSourceLocal = dataSourceLocal.map(ds => {
        //     if (ds.connId === activeDS) {
        //         ds = { ...ds, ...record.data, name: record.name, classifier: record.classifier, changed: true, datasourceName: record.name, driver: record.driver, driverType: currentType.name }
        //     }
        //     return ds
        // })
        let dataSourceLocal = changeDatasourceUtil({ record, allDataSourceTypes, dataSourceState, activeDS, allDataSources })
        dispatch(metadataActions.updateMetadataState({ key: 'dataSource', value: dataSourceLocal, others: [{ key: 'dataSourcesAddedToMetadata', value: dataSourceLocal }] }))
        onDrawerClose && onDrawerClose()
    }
    const prepareColumns = (ds) => {
        if (!ds) return []
        let columns = [
            {
                title: 'S No',
                dataIndex: 'sNo',
                key: 'sNo',
                sorter: (a, b) => a.sNo - b.sNo
            },
            {
                title: 'Name',
                dataIndex: 'name',
                key: 'name',
                ellipsis: true,
                sorter: (a, b) => {
                    let aName = a.name.toLowerCase();
                    let bName = b.name.toLowerCase();
                    if (aName > bName) {
                        return 1;
                    } else if (aName < bName) {
                        return -1;
                    } else {
                        return 0;
                    }
                },
                filterSearch: true,
                filters: (ds || []).map(eachDS => ({ text: eachDS.name, value: eachDS.name })),
                onFilter: (value, record) => record.name === value
            },
            {
                title: 'Conn. Id',
                dataIndex: 'connId',
                key: 'connId',
                sorter: (a, b) => a.connId - b.connId,
                filterSearch: true,
                filters: (ds || []).map(eachDS => ({ text: eachDS.connId, value: eachDS.connId })),
                onFilter: (value, record) => record.connId === value
            }
        ]
        if (ds?.length && ds[0]?.dir) {
            columns.push({
                title: 'Directory',
                dataIndex: 'dir',
                key: 'dir',
                ellipsis: true
            })
        }
        columns.push({
            title: 'Action',
            key: 'action',
            render: (text, record) => {
                let properties = {}
                if (record.test === 'success') {
                    properties.className = 'hi-success-btn'
                }
                if (record.test === 'failed') {
                    properties.danger = true
                }
                return (
                    <Space size="middle">
                        <Button
                            size='small'
                            {...properties}
                            // loading={testQueue.indexOf(record.key) !== -1} 
                            onClick={() => handleConnectionTest({ record })}
                        >Test</Button>
                    </Space>
                )
            },
        })
        return columns

    }
    const tableDataSource = changeDSList[activeType] || []
    return <Row data-testid="hi-metadata-changeDataSource" className='hi-metadata-change-ds'>
        <Col span={20}>
            Datasource Type
        </Col>
        <Col span={4} className='text-align-end'>
            {!!(activeType && tableDataSource?.length) && <Tooltip placement='left' title='Double click on connection to use'><InfoCircleOutlined /></Tooltip>}
            &nbsp;&nbsp;&nbsp;&nbsp;
            {activeType && <Tooltip placement='left' title='Refresh'><ReloadOutlined onClick={reset} /></Tooltip>}
        </Col>
        <Col span={24}>
            <Select placeholder="Please select a datasource"
                // style={{ width: '100%' }}
                className="width100percent"
                onChange={e => handleDSTypeChange(e, false)}>
                {dataSourceTypes.map(type => {
                    return <Option key={type.name} value={type.name}>
                        <Row>
                            <Col span={24}>{type.name}</Col>
                        </Row>
                    </Option>
                })}
            </Select>
        </Col>
        <Col span={24} className='hi-pt10'>
            {activeType ? (loading ? <ChangeDataSourceSkeleton /> : <Table
                size="small"
                dataSource={tableDataSource}
                // scroll={{ y: 500, x: "100%" }}
                // components={vc}
                pagination={false}
                // loading={loading}
                rowKey={(record) => record.key}
                columns={prepareColumns(changeDSList[activeType])}
                onRow={(record) => {
                    return {
                        onDoubleClick: () => { handleDoubleClick({ record }) }, // double click row
                    };
                }}
            >
            </Table>)
                :
                // <Skeleton />
                <Empty
                    image={Empty.PRESENTED_IMAGE_SIMPLE}
                    description={
                        <span
                            data-testid={'hi-metadata-info-change-ds-no-data'}
                        >
                            No dataSources are available
                        </span>
                    }
                />
            }
        </Col>
    </Row>;
}
