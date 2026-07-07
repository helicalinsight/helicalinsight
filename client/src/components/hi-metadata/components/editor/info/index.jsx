import { EditOutlined } from '@ant-design/icons';
import { Button, Col, Divider, Drawer, Input, Row, Table, Tag, Tooltip, Typography } from 'antd';
import { isEqual } from 'lodash-es';
import React, { useEffect, useRef } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { metadataActions, updateTablesWithNewViews } from '../../../../../redux/actions';
import CustomMetadataIcon from '../../../customMetadataIcon';

const { Paragraph } = Typography;
const ChangeDataSource = React.lazy(() => import('./changeDataSource'))


const { Text } = Typography
export default function Info() {
    const dataSource = useSelector(state => state.metadata.present.dataSourcesAddedToMetadata, isEqual)
    const allDataSources = useSelector(state => state.metadata.present.allDataSources, isEqual)
    const saveDetails = useSelector(state => state.metadata.present.saveDetails, isEqual)
    const activeDS = useSelector(state => state.metadata.present.activeDsInfoId)
    const views = useSelector(state => state.metadata.present.views)
    const tables = useSelector(state => state.metadata.present.tables)
    const { dataSourceTypes = [] } = useSelector((state) => state.metadata.present);
    const activeEditorTab = useSelector(state => state.metadata.present.activeEditorTab)

    // const [activeDS, setActiveDS] = React.useState(false)
    const [showDrawer, setShowDrawer] = React.useState(false)
    const dispatch = useDispatch()
    const setActiveDS = (data) => {
        if (data !== activeEditorTab) {
            dispatch(metadataActions.updateActiveDsInfoId(data))
        }
    }
    const apiRef = useRef(null);

    function handleAbort(prop = {}) {
        apiRef.current?.abort(prop);
    }

    const handleInputChange = ({ value, type }) => {
        let currentDS = null
        dataSource.forEach(ds => {
            if (ds.connId === activeDS) {
                ds = { ...ds };
                if (!('originalCatalog' in ds)) {
                    ds.originalCatalog = ds.catalog;
                    ds.originalSchema = ds.schema;
                }
                ds[type] = value;
                if (ds.catalog && ds.schema) {
                    ds.database = `${ds.catalog}.${ds.schema}`
                } else if (ds.catalog) {
                    ds.database = ds.catalog
                } else {
                    ds.database = ds.schema
                }
                currentDS = ds
            }
            // return ds
        })
        dispatch(metadataActions.updateDataSourceInfo({ data: currentDS, type: 'dataSourcesAddedToMetadata' }))
    }

    useEffect(() => {
        if (views.length && Object.keys(tables).length) {
            // views.forEach(view => {
            dispatch(updateTablesWithNewViews())
            // })
        }
        //meta-info
    }, [tables])

    React.useEffect(() => {
        if (dataSource.length === 1) {
            setActiveDS(dataSource[0].connId)
        }
    }, [dataSource])

    const handleDSChange = () => {
        setShowDrawer(true)
    }

    const onDrawerClose = () => {
        handleAbort();
        apiRef.current = undefined;
        setShowDrawer(false)
    }

    let { schema = '', catalog = '', database = '', id, type, driver } = activeDS && dataSource.filter(ds => ds.connId === activeDS) && dataSource.filter(ds => ds.connId === activeDS)[0] ? dataSource.filter(ds => ds.connId === activeDS)[0] : {}
    if (catalog && schema) {
        database = `${catalog}.${schema}`
    } else if (catalog) {
        database = catalog
    } else {
        database = schema
    }

    if ((typeof driver === 'string') || !driver) {
        driver = allDataSources.find(ds => {
            return ds.data.id === id && ds.data.type === type;
        })
    }

    const idTypeData = [
        {
            key: '1',
            name: 'ID',
            data: id || '',
        },
        {
            key: '2',
            name: 'Type',
            data: type || '',
        },
        {
            key: '3',
            name: 'DataSourceType',
            data: driver?.dataSourceType || dataSourceTypes.find(source => source.type === type)?.name || '',
        }
    ];

    const idTypeKeys = [
        {
            title: 'Name',
            dataIndex: 'name',
            key: 'name',
        },
        {
            title: 'Age',
            dataIndex: 'data',
            key: 'data',
        },
    ];

    let showDsButton = !!saveDetails
    return <Row>
        <Col span={24} className='connections-container-1'>
            <Row>
                <Col span={24}>
                    {/* <Row className='hi-p10'>
                        {`Current Datasource${dataSource.length > 1 ? 's' : ''}`}
                    </Row> */}
                    <Divider orientation="left" className='datasource-names-title'>
                        Current DataSource(s){/*{`Current Datasource${dataSource.length > 1 ? 's' : ''}`}*/}
                    </Divider>
                </Col>
                <Col span={24}>
                    <Row justify="center">
                        {dataSource.map((ds, index) => {
                            return <Col
                                span={
                                    index === 0 && dataSource.length === 1 || index === dataSource.length - 1 ? 12 :
                                        dataSource.length % 2 !== 0 && index === dataSource.length - 1 ? 24 : 12
                                }
                                key={ds.connId || index}
                            >
                                <Row justify="center">
                                    <Col
                                        span={20}
                                    >
                                        <Tooltip title={ds.connId === activeDS ? (ds.datasourceName || ds?.driver?.name) : "Show Details"}>
                                            <Tag onClick={() => { setActiveDS(activeDS !== ds.connId ? ds.connId : !!0) }}
                                                color={ds.connId === activeDS ? "processing" : "default"}
                                                className={`ds-conn-tag width100pcnt ${index > 2 ? 'hi-pt10' : ''}`}
                                            >
                                                <Row>
                                                    <Col span={3}>
                                                        {['athena', 'dynamicswitch', "snowflake", "trino", "elasticsearch", "duckdb", "yugabyte" , "celerdata","cockroach","sapdb","databricks","api","flatfile","flatfilecsv","flatfileexcel","flatfiletsv","flatfilejson","flatfileaws","flatfileazureblobstorage","flatfilecloudfarer2","flatfilegcs","flatfileparquet","flatfilegooglespreadsheet"].includes(ds?.driverType?.toLowerCase()?.replace(/ /g, "")) ?
                                                            <div className="data-source-image-block" >
                                                                <CustomMetadataIcon type={ds?.vendorName || ds?.driverType} />
                                                            </div>
                                                            :
                                                            <div className="data-source-image-block" >
                                                                <span className={"databases " + ds?.driverType?.toLowerCase()?.replace(/ /g, "") + "-icon"}
                                                                >
                                                                </span>
                                                            </div>}
                                                    </Col>
                                                    <Col span={19} className='info-datasource-name-tag'>
                                                        <Paragraph ellipsis={true}>
                                                            {ds.datasourceName || ds?.driver?.name}
                                                        </Paragraph>
                                                    </Col>
                                                    {/* <Col span={2} className='info-datasource-edit-icon'>
                                                        {!!(saveDetails) && <EditOutlined
                                                        onClick={() => { setActiveDS(activeDS !== ds.connId ? ds.connId : !!0) }}
                                                    />}
                                                         {<EditOutlined
                                                        onClick={() => { setActiveDS(activeDS !== ds.connId ? ds.connId : !!0) }}
                                                    />} 
                                                    </Col> */}
                                                </Row>
                                            </Tag>
                                        </Tooltip>
                                    </Col>
                                </Row>
                            </Col>
                        })}
                    </Row>
                </Col>

            </Row>
        </Col >
        {activeDS && <Col span={24} className='hi-pt10'>
            <Divider orientation="left">
                {`Datasource Details`}
            </Divider>
            <Row gutter={16}>
                <Col span={24}>
                    <Table
                        dataSource={idTypeData}
                        columns={idTypeKeys}
                        showHeader={false}
                        pagination={false}
                        size="small"
                        bordered={true}
                        className='hi-pb10'
                    />
                </Col>
                <Col className="gutter-row" xs={24} sm={24} md={12} lg={showDsButton ? 6 : 8} xl={showDsButton ? 6 : 8}>
                    <Row>
                        <Col span={24}>
                            Catalog Name
                        </Col>
                        <Col span={24}>
                            <Input placeholder='Catalog Name' value={catalog} onChange={e => handleInputChange({ value: e.target.value, type: 'catalog' })} />
                        </Col>
                    </Row>
                </Col>
                <Col className="gutter-row" xs={24} sm={24} md={12} lg={showDsButton ? 6 : 8} xl={showDsButton ? 6 : 8}>
                    <Row>
                        <Col span={24}>
                            Schema Name
                        </Col>
                        <Col span={24}>
                            <Input placeholder='Schema Name' value={schema} onChange={e => handleInputChange({ value: e.target.value, type: 'schema' })} />
                        </Col>
                    </Row>
                </Col>
                <Col className="gutter-row" xs={24} sm={24} md={12} lg={showDsButton ? 6 : 8} xl={showDsButton ? 6 : 8}>
                    <Row justify='center'>
                        <Col span={24}>
                            Database Name
                        </Col>
                        <Col span={24}>
                            <Input placeholder='Database Name' disabled value={database} onChange={e => handleInputChange({ value: e.target.value, type: 'database' })} />
                        </Col>
                    </Row>
                </Col>
                {showDsButton && <Col className="gutter-row" xs={24} sm={24} md={12} lg={6} xl={6}>
                    <Col span={24}>
                        &nbsp;
                    </Col>
                    <Col span={24}>
                        <Button type='primary' block onClick={handleDSChange}>
                            <Text
                                // style={{ width: '100%', color: 'inherit' }}
                                ellipsis={{ tooltip: 'Change Datasource' }}
                                className='change-ds-button-info-meta'
                            >
                                Change Datasource
                            </Text>
                        </Button>
                    </Col>
                </Col>}
            </Row>
        </Col>
        }
        <Drawer title="Select Datasource" placement="right" size={'large'} onClose={onDrawerClose} visible={showDrawer}>
            {showDrawer && <React.Suspense fallback={<div>Loading...</div>}>
                <ChangeDataSource activeDS={activeDS} onDrawerClose={onDrawerClose} apiRef={apiRef} />
            </React.Suspense>}
        </Drawer>

    </Row >;
}
