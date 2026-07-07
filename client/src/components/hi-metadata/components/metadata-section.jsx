
import { CalendarOutlined, CheckOutlined, CloseOutlined, CopyOutlined, EditOutlined, ExclamationCircleOutlined, FileTextOutlined, NumberOutlined, SearchOutlined, TableOutlined } from '@ant-design/icons';
import { Col, Dropdown, Input, Menu, Modal, Popover, Row, Table, Tooltip, Typography } from 'antd';
import { cloneDeep, isEqual } from 'lodash-es';
import React, { useRef } from 'react';
import { useDrag, useDrop } from 'react-dnd';
import { useDispatch, useSelector, useStore } from "react-redux";
import { v4 as uuidv4 } from "uuid";
import { VList } from "virtuallist-antd";
import { useWindowSize } from '../../../customHooks/useWindowSize';
import { handleJoinHighlight, handleMetadataSectionRowsExpand, handleTextEditingObj, metadataActions, metadataOutsideClicked, setIsInfoShow, setSecurityKeysChecked } from "../../../redux/actions";
import { classnames } from "../../../utils/classNames";
import TutorialInfo from '../../common/hi-tutorial';
import HIIcon from '../../common/icons/hi-icons';
import { handleTableAction, handleTableDropToMetadata, handleTableExpand, manipulateTableDataToRender } from '../utils';
import constants from '../../../constants'
import LoadingBar from '../../common/components/hi-loading-bar';


const { Text, Paragraph } = Typography;
const { Column } = Table;
const tooltipDelay = constants.mouseEnterDelay

const BACKGROUND_COLOR = '#f0ecec'

const replaceSpaces = (name = '') => (name.split(' ').join('__'))

function MetadataSection(props = {}) {
    const dispatch = useDispatch();
    const dataSource = useSelector(state => state.metadata.present.dataSource)
    const metadataSectionExpandedRows = useSelector((state) => state.metadata.present.metadataSectionExpandedRows);
    const tables = useSelector(state => state.metadata.present.tables, isEqual)
    const views = useSelector(state => state.metadata.present.views, isEqual)
    const { metadataName, mode, edit, securityTableData, addOneMoreSecurity, textEditingObj, isInfoShow } = useSelector(state => {
        const { metadataName, mode, edit, securityTableData, addOneMoreSecurity, textEditingObj, isInfoShow } = state.metadata.present
        return { metadataName, mode, edit, securityTableData, addOneMoreSecurity, textEditingObj, isInfoShow }
    })
    const setMetadataLoading = useSelector(state => state.metadata.present.setMetadataLoading)
    const loadingStatus = useSelector((state) => state.metadata.present.loadingStatus); //isAllowServiceCall
    const isAllowServiceCall = useSelector((state) => state.metadata.present.isAllowServiceCall);
    const isViewFetching = useSelector((state) => state.metadata.present.isViewFetching);
    const [editable, setEditable] = React.useState(false)
    const [, offsetHeight] = useWindowSize()
    const [height, setHeight] = React.useState('0px')
    const [columnAliasInfo, setColumnAliasInfo] = React.useState({})
    const metadataNameRef = React.useRef(null)
    const [searchText, setSearchText] = React.useState('')
    const [showSearch, setShowSearch] = React.useState(false)
    let store = useStore()
    const [requestId, setRequestId] = React.useState('');
    const metadataTableRef = React.useRef(null)
    const nameInputRef = React.useRef('metadataName');
    const apiRef = useRef(null);
    // const [tablesListToRender, setTablesListToRender] = React.useState([])

    function getApi(apiInstance) {
        apiRef.current = apiInstance;
    }

    function abortRequest() {
        apiRef.current?.abort();
    }

    const [, drop] = useDrop(() => ({
        accept: "tableFromDS",
        drop: record => {
            let tables = store.getState().metadata.present.tables;
            let isAfterSaveMode = store.getState().metadata.present.isAfterSaveMode;
            if (!Object.keys(tables).length) {
                handleTableDropToMetadata({ record, dispatch, datasourceListToRender: cloneDeep(store.getState().metadata.present.datasourceListToRender), tables, store, type: 'reload' })
                return
            }
            Modal.confirm({
                title: 'Add To Metadata',
                icon: <ExclamationCircleOutlined />,
                content: <Row>
                    <Col>
                        <strong>Changes will be reflected based on below actions:</strong>
                    </Col>
                    <Col>
                        <strong>Merge </strong>:The changes made in metadata panel will be kept as is and changes in the datasource panel will be synched
                    </Col>
                    <Col>
                        <strong>Reload</strong>:The metadata will be overriden by the selection made in the datasource panel
                    </Col>
                </Row>,
                okText: "Reload",
                okButtonProps: { disabled: (mode === "edit" || isAfterSaveMode) ? true : false },
                cancelText: "Merge",
                okType: 'danger',
                cancelType: 'primary',
                onOk: () => {
                    if (mode === 'edit') {
                        return;
                    }
                    handleTableDropToMetadata({ record, dispatch, datasourceListToRender: cloneDeep(store.getState().metadata.present.datasourceListToRender), tables, store, type: 'reload' })
                },
                onCancel: (e) => {
                    if (e?.triggerCancel) {
                        return
                    }
                    if (e.name) {
                        handleTableDropToMetadata({ record, dispatch, datasourceListToRender: cloneDeep(store.getState().metadata.present.datasourceListToRender), tables, store, type: 'merge' })
                    }
                    if (typeof e === 'function') {
                        e()
                    }
                }
            })
        },
    }))

    React.useEffect(() => {
        document.querySelector('.hi-metadata-metadata-section') && calculateHeight()
    }, [offsetHeight])

    React.useEffect(() => {
        if (metadataName) {
            document.title = `${metadataName} | HI:Metadata`
        }
    }, [metadataName]);

    const calculateHeight = () => {
        let topPart = 40
        let bottomPart = 10
        let pageHeightExceptNav = document.querySelector('.hi-metadata-metadata-section')?.offsetHeight || 500
        let finalHeight = pageHeightExceptNav - topPart - bottomPart
        setHeight(`${finalHeight}px`)
    }
    // const vc = React.useMemo(() => {
    //     console.log('in use memo called')
    //     return VList({
    //         height: height,
    //         resetTopWhenDataChange: false,
    //         onScroll: () => { },
    //         vid: 'metadata-page-metadata-section',

    //     });
    // }, [height]);

    let tablesListToRender = manipulateTableDataToRender({ tables, views, searchText })
    // React.useEffect(() => {
    //     setTablesListToRender(manipulateTableDataToRender({ tables, views, searchText }))
    // }, [tables, views, searchText]);

    return (
        <Row className='height100percent b1pxddd metadata-table-section' ref={drop}>
            <Col span={24} className="filter-area ">
                <Row className='metadata-section-search'>
                    <Col span={24} ref={metadataNameRef} className='hi-background-blue metadata-name-edit hi-pd-l-r-5' id='metadata-name-blinker'>
                        <TutorialInfo elementKey='hi-metadata-metadata-section'>
                            <Row className='metadata-name-edit-row'>
                                <Col span={2} className='metadata-name-edit-icon'><TableOutlined /></Col>
                                {!showSearch && <Col span={19} className='metadata-name-edit-name'>
                                    <Paragraph
                                        ellipsis={{ tooltip: metadataName }}
                                        editable={
                                            {
                                                onStart: () => {
                                                    if (typeof (metadataNameRef?.current?.style?.height) !== 'undefined') {
                                                        metadataNameRef.current.style.height = '32px'
                                                    }
                                                },
                                                onEnd: (e) => {
                                                    if (typeof (metadataNameRef?.current?.style?.height) !== 'undefined') {
                                                        metadataNameRef.current.style.height = '30px'
                                                    }
                                                },
                                                onCancel: () => {
                                                    if (typeof (metadataNameRef?.current?.style?.height) !== 'undefined') {
                                                        metadataNameRef.current.style.height = '30px'
                                                    }
                                                },
                                                onChange: (e) => {
                                                    nameInputRef.current = e
                                                    dispatch(metadataActions.updateMetadataState({ key: 'metadataName', value: e }))
                                                }
                                            }
                                        }
                                    >
                                        {metadataName}
                                    </Paragraph>
                                </Col>}
                                {showSearch && <Col span={19} className='metadata-section-search-tables'>
                                    <Tooltip
                                        title={<span>There might be more matching results.<br />Please click on table and search again.</span>}
                                        mouseEnterDelay={0.8}
                                    >
                                        <Input
                                            placeHolder={'Search tables/columns'}
                                            autoFocus
                                            allowClear onChange={(e) => setSearchText(e.target.value)} />
                                    </Tooltip>

                                </Col>}
                                <Col span={3} className='metadata-name-search'>
                                    {!showSearch && <SearchOutlined
                                        onClick={() => {
                                            if (showSearch) {
                                                setSearchText('')
                                            }
                                            setShowSearch(!showSearch)
                                        }

                                        }
                                    />}
                                    {showSearch && <CloseOutlined onClick={() => {
                                        setSearchText('')
                                        setShowSearch(false)
                                    }} />
                                    }
                                </Col>
                                {!setMetadataLoading?.isMetadataFetched && mode === 'edit' && <Col span={24} onClick={() => props.handleAbort && props.handleAbort()} ><LoadingBar /></Col>}
                            </Row>
                        </TutorialInfo>
                    </Col>
                    <Col span={24} className={classnames({ 'no-tables-text-metadata-section': !!!(tablesListToRender?.length) })}>
                        {!!(tablesListToRender?.length) &&
                            <Table
                                size="small"
                                dataSource={tablesListToRender}
                                showHeader={false}
                                className='custom-table-scroll-bar hi-tree-table'
                                expandedRowKeys={metadataSectionExpandedRows}
                                onExpand={(isExpanded, record) => {
                                    dispatch(handleMetadataSectionRowsExpand(record.key))
                                    if (isExpanded) {
                                        handleTableExpand({
                                            setRequestId,
                                            currentTable: record,
                                            dataSource,
                                            dispatch,
                                            tables,
                                            getApi,
                                            mode
                                        })
                                    }
                                }}
                                scroll={{ y: height, x: "100%" }}
                                expandable={{ indentSize: 2 }}
                                components={() => VList({
                                    height: height,
                                    resetTopWhenDataChange: false,
                                    onScroll: () => { },
                                    vid: 'metadata-page-metadata-section',

                                })}
                                pagination={false}
                                ref={metadataTableRef}
                            >
                                <Column
                                    ellipsis={{
                                        showTitle: true,
                                    }}
                                    key="action"
                                    render={(text, record) => (
                                        <DragAssist
                                            addOneMoreSecurity={addOneMoreSecurity}
                                            securityTableData={securityTableData}
                                            requestId={requestId}
                                            isAllowServiceCall={isAllowServiceCall}
                                            edit={edit}
                                            textEditingObj={textEditingObj}
                                            mode={mode}
                                            record={record}
                                            editable={editable}
                                            setEditable={setEditable}
                                            item={record}
                                            dispatch={dispatch}
                                            tables={tables}
                                            dataSource={dataSource}
                                            store={store}
                                            columnAliasInfo={columnAliasInfo}
                                            setColumnAliasInfo={setColumnAliasInfo}
                                            loadingStatus={loadingStatus}
                                            isViewFetching={isViewFetching}
                                            metadataTableRef={metadataTableRef}
                                            abortRequest={abortRequest}
                                            isInfoShow={isInfoShow}
                                            getApi={getApi}
                                        >{record.name}</DragAssist>
                                    )}
                                />
                            </Table>
                        }
                        {
                            !!!(tablesListToRender?.length) && !searchText && <Text strong className="no-tables-text-metadata-section hi-pd-l-r-5">
                                For adding table(s) to metadata, use drag and drop or right-click option on the selected table(s).
                            </Text>
                        }
                    </Col>
                </Row>
            </Col>
        </Row>
    )
}

export { MetadataSection };


const tableMenu = ({ record, dispatch, setEditable, tables, dataSource, store, isViewFetching, getApi }) => {
    if (record.category === 'table' || record.category === 'view')
        return (
            <Menu onClick={({ key }) => { handleTableAction({ action: key, dataSource, store, tables, record, dispatch, setEditable, getApi }) }}>
                {(record.type !== 'view' && !record.duplicate) && <Menu.Item key="duplicate" value={record}>Duplicate</Menu.Item>}
                <Menu.Item key="remove" disabled={record.type === 'view' && isViewFetching[record.uniqueKey]} value={record}>Remove</Menu.Item>
            </Menu>
        );
    if (record.category === 'column')
        return (
            <Menu onClick={({ key }) => { handleTableAction({ action: key, dataSource, store, tables, record, dispatch, setEditable }) }}>
                {(record.parentCategory !== 'view' && !record.duplicate) && <Menu.Item key="duplicate" value={record}>Duplicate</Menu.Item>}
                <Menu.Item key="remove" disabled={record.parentCategory === 'view' && isViewFetching[record.tableId]} value={record}>Remove</Menu.Item>
            </Menu>
        )
    return <></>
};

const RenderToolTip = ({ item }) => {
    let data = {
        Name: item.name,
        Alias: item.alias,
    }

    if (item.category === 'table' || item.category === 'view') { // 5027
        data.Datasource = item?.dataSource?.datasourceName;
        data.Id = item?.dataSource?.id;
        // if (item.duplicate) {
        //     data.original = item.originalName ?? item.name;
        // }
    }

    // Runs for both duplicate table and column
    if (item.duplicate) {
        data.original = item.originalName ?? item.name;
    }
    /**
     * TODO
     * test case to check the popover exist when it is hovered
     * based on disc with nitin keeping this on hold as this is taking more time than expected
     */
    return (
        <table
            data-testid={`metadata-section-table-popover`}
            className={`metadata-section-table-popover`}
        >
            <tbody>
                {Object.keys(data).map(key => (
                    <tr key={key}>
                        <td>{key[0].toUpperCase() + key?.slice(1)} :&nbsp;</td>
                        <td>{data[key]}</td>
                    </tr>
                ))}
            </tbody>
        </table>
    );

}

const handleTableOrColumnKey = ({ securityTableData = [], item, dispatch, edit, addOneMoreSecurity, mode, isInfoShow }) => {
    dispatch(metadataOutsideClicked(false));
    let dbId = item.dataSource?.dbId || item.dbId || item.connId;
    let data = { category: item.category, dbId };
    // tooltipInfo: [{ Id: item.dataSource?.id, Datasource: item.dataSource?.datasourceName, Alias: item.alias, key: item.uniqueKey }]
    let joinNameData = { category: item.category, dbId };
    if (item.category !== 'table' && item.category !== 'view') {
        //TODO change item.columnKey to item.id. when B.E will provide columnId in joins
        joinNameData.value = item.alias;
        joinNameData.tableId = item.tableId;
        data.key = item.alias;
        // data.tooltipInfo[0].Name = item.columnKey;
        // if (mode === 'edit') {
        //     data.id = item.id;
        // }
    } else {
        joinNameData.value = item.id;
        // data.tableId = item.id
        data.key = item.alias;
        // data.tooltipInfo[0].Name = item.keyName
        //     if (mode === 'edit') {
        //         data.tableId = item.tableId;
        //     }
    }
    if (((!securityTableData.length) && !edit) || addOneMoreSecurity) {
        // dispatch(setSecurityKeysChecked([item.uniqueKey]));
        dispatch(metadataActions.metadataUndoRedoPurpose([{ key: "securityKeysChecked", value: [item.uniqueKey] }]))
        isInfoShow && dispatch(setIsInfoShow(false));
        dispatch(metadataActions.setSelectedTableOrColumnKey(data));
        // if (data.category === 'column') {
        // } else {
        //     dispatch(setSecurityKeysChecked([data.tableUuid]));
        // }
    }
    dispatch(handleJoinHighlight(joinNameData))
}

const DragAssist = ({ addOneMoreSecurity, securityTableData, mode, edit, requestId: requestIdToCancel, isAllowServiceCall, item, dispatch, dataSource, tables, record, editable, setEditable, store, loadingStatus, textEditingObj, metadataTableRef, abortRequest, isViewFetching, isInfoShow, getApi }) => {
    const requestId = uuidv4();
    // console.log(item, 'drag')
    const [{ }, drag] = useDrag(
        () => {
            return {
                type: "metadataColumn",
                item: item,
                collect: (monitor) => ({
                    opacity: monitor.isDragging() ? 0.5 : 1
                })
            }
        },
        [item]
    )

    return (
        <div ref={item.category !== 'table' ? drag : null} style={{ width: '100%' }}
            className={`metadata-section-table-text hover-bg-f0ecec ${replaceSpaces(item.keyName)}`}
            onMouseEnter={() => {
                try {
                    if (item.category !== 'table' && item.category !== 'view') {
                        metadataTableRef.current.querySelector(`.${replaceSpaces(item.tableKey)}`).style.background = BACKGROUND_COLOR
                    }
                } catch (e) { }
            }}
            onMouseLeave={() => {
                try {
                    if (item.category !== 'table' && item.category !== 'view') {
                        metadataTableRef.current.querySelector(`.${replaceSpaces(item.tableKey)}`).style.background = 'unset'
                    }
                } catch (e) { }
            }}
            onClick={e => e.stopPropagation()}
        >
            <React.Fragment>
                {
                    (item.category === 'table' || item.category === 'view') &&
                    <Dropdown
                        // data-testid={item.alias}
                        className={`metadata-section-table-${item.alias}`}
                        // visible={item.category === 'view'}
                        overlay={tableMenu({ isViewFetching, store, dataSource, record: item, dispatch, editable, setEditable, tables, getApi })}
                        trigger={['contextMenu']}
                    >
                        <Popover
                            mouseEnterDelay={tooltipDelay}
                            overlayClassName='hi-metadata-section-table-popover-overlay'
                            placement='topLeft'
                            content={<RenderToolTip item={item} />}>
                            <Row data-testid={`metadata-section-table-${item.alias}`}>
                                <Col span={2} onClick={() => { handleTableOrColumnKey({ addOneMoreSecurity, securityTableData, item, dispatch, mode, edit, isInfoShow }) }}>
                                    {item.type === 'view' ? <HIIcon name='hi-object-group' /> : item.duplicate ? <CopyOutlined /> : <TableOutlined />}
                                </Col>
                                <Col span={22}>
                                    <Paragraph
                                        //  editable={true}
                                        // style={{ width: 100 }}
                                        onClick={() => { handleTableOrColumnKey({ addOneMoreSecurity, securityTableData, item, dispatch, mode, edit, isInfoShow }) }}
                                        ellipsis={true}
                                        editable={{
                                            icon: <EditOutlined onClick={(e) => {
                                                //Check: why to use this function
                                                dispatch(handleTextEditingObj({ tableId: item.uniqueKey }));
                                                e.stopPropagation()
                                            }} />,
                                            editing: textEditingObj?.tableId === item.uniqueKey,
                                            onChange: (e) => {
                                                (item.alias !== e) && handleTableAction({
                                                    action: 'updateAlias',
                                                    value: e,
                                                    record,
                                                    dispatch,
                                                    tables,
                                                    store
                                                });
                                                dispatch(handleJoinHighlight({ category: 'table', value: e, dbId: item.dataSource?.dbId || item.dataSource?.connId || item.connId }))
                                                dispatch(handleTextEditingObj({}))
                                            },
                                        }}
                                    >{item.alias}</Paragraph>
                                    {loadingStatus[item.key] && <Col onClick={() => {
                                        abortRequest && abortRequest()
                                    }} span={24}><LoadingBar /></Col>}
                                </Col>
                            </Row>
                        </Popover>
                    </Dropdown>
                }
                {
                    item.category !== 'table' && item.category !== 'view' && // category is column
                    <Dropdown overlay={tableMenu({ dataSource, store, record: item, dispatch, editable, setEditable, tables, isViewFetching, getApi })} trigger={['contextMenu']}>
                        <Row>
                            <Col span={24} >
                                <Popover
                                    mouseEnterDelay={tooltipDelay}
                                    content={<RenderToolTip item={item} />}
                                    placement='topLeft'
                                    overlayClassName='hi-metadata-section-table-popover-overlay'
                                >
                                    <Row>
                                        <Col span={2} onClick={() => { handleTableOrColumnKey({ addOneMoreSecurity, securityTableData, item: { ...item, dataSource }, dispatch, mode, edit, isInfoShow }) }}>
                                            {(Object.values(item?.type || {})[0] === 'numeric') ?
                                                <NumberOutlined />
                                                :
                                                ((Object.values(item?.type || {})[0] || '').includes('date')) ?
                                                    <CalendarOutlined />
                                                    :
                                                    ((Object.values(item?.type || {})[0] || '').includes('boolean')) ?
                                                        <CheckOutlined />
                                                        :
                                                        <FileTextOutlined />
                                            }
                                        </Col>
                                        <Col span={22}>
                                            <Paragraph
                                                onClick={() => { handleTableOrColumnKey({ addOneMoreSecurity, securityTableData, item: { ...item, dataSource }, dispatch, mode, edit, isInfoShow }) }}
                                                ellipsis={true}
                                                editable={{
                                                    icon: <EditOutlined onClick={(e) => {
                                                        // Check here why to use this 
                                                        dispatch(handleTextEditingObj({ columnId: item.uniqueKey }));
                                                        e.stopPropagation()
                                                    }} />,
                                                    editing: textEditingObj?.columnId === item.uniqueKey,
                                                    onChange: e => {
                                                        (item.alias !== e) && handleTableAction({
                                                            action: 'updateAlias',
                                                            value: e,
                                                            record,
                                                            dispatch,
                                                            tables,
                                                            store
                                                        })
                                                        dispatch(handleTextEditingObj({}))
                                                    }
                                                }}
                                            >
                                                {item.alias}
                                            </Paragraph>
                                        </Col>
                                    </Row>
                                </Popover>
                            </Col>
                        </Row>
                    </Dropdown>
                }
            </React.Fragment >

        </div >
    )
}