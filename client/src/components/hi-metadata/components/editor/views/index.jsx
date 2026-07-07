import { PlusCircleOutlined, CloseCircleTwoTone } from '@ant-design/icons';
import { Button, Col, Empty, Row, Typography, Tooltip } from 'antd';
import { cloneDeep, isEqual } from 'lodash-es';
import React from 'react';
import { useDispatch, useSelector, useStore } from 'react-redux';
import metadataRequests from '../../../../../base/requests/metadata.requests';
import { metadataActions, metadataUndoRedoPurpose } from '../../../../../redux/actions';
import { uuid } from '../../../../../utils/uuid';
import notify from '../../../../hi-notifications/notify';
import { processSessionVariables } from '../../../utils';
import { getViewPayload } from '../../../utils/views/getViewPayload';
import ViewEditor from './viewEditor';

const { Paragraph } = Typography;

const Views = ({ }) => {
    const views = useSelector(state => state.metadata.present.views.filter(view => view.isDeleted !== false))
    const activeView = useSelector(state => state.metadata.present.activeView)
    const dataFetchedFor = useSelector(state => state.metadata.present.dataFetchedFor, isEqual)
    const dataSourcesAddedToMetadata = useSelector(state => state.metadata.present.dataSourcesAddedToMetadata, isEqual)
    const dispatch = useDispatch()
    const store = useStore()
    const buttonAddNewRef = React.useRef(null)
    const buttonAddNewRef2 = React.useRef(null);
    const isMultiConnection = dataSourcesAddedToMetadata.length > 1;

    React.useEffect(() => {
        //get static session variables
        if (dataFetchedFor.viewSessionVariables) {
            return
        }
        metadataRequests(dispatch).getSessionVariables({ "contentId": "Static/SessionVariablePattern" }, null, (res) => {
            dispatch(metadataActions.updateLoadingStatus({
                type: 'viewSessionVariables', status: true, onlyDataFetchedFor: true
            }))
            let result = processSessionVariables(res) // processing session variables here itself to increase performance
            dispatch(metadataActions.updateMetadataState({
                key: 'viewSessionVariables', value: result
            }))
        }, () => {
        })
    }, []);

    const handleKeyPress = (event) => {
        try {
            if (event.altKey && !event.ctrlKey && !event.shiftKey) {
                if (event.key?.toLowerCase() === 'n') {
                    // handleSaveView()
                    (typeof buttonAddNewRef?.current?.click === 'function') && buttonAddNewRef?.current?.click();
                    (typeof buttonAddNewRef2?.current?.click === 'function') && buttonAddNewRef2?.current?.click();
                    event.preventDefault();
                }
            }
        }
        catch (e) {

        }
    };

    React.useEffect(() => {
        // attach the event listener
        document.addEventListener('keydown', handleKeyPress);

        // remove the event listener
        return () => {
            document.removeEventListener('keydown', handleKeyPress);
        };
    }, []);

    const handleAddNewView = () => {
        let dataSource = store.getState().metadata.present.dataSourcesAddedToMetadata
        let editViewsTempData = store.getState().metadata.present.editViewsTempData
        if (dataSourcesAddedToMetadata.length > 1) return
        if (activeView && editViewsTempData[activeView] && !editViewsTempData[activeView]?.id && editViewsTempData[activeView]?.alias) {
            notify(dispatch).warning({
                type: 'Validation',
                message: `Please save current view to create new view`
            })
            return
        }
        let key = uuid()
        let viewName = `View ${views.length + 1}`
        let newView = {
            name: viewName,
            alias: viewName,
            columns: {},
            // key,
            uuid: key,
            query: '',
            queryType: "conditionIf",
            labels: [],
            category: 'view',
        }
        if (dataSource.length === 1) {
            // newView.dataSource = dataSource
            newView.dataSource = dataSource[0]
            newView.connId = dataSource[0].connId
            newView.schema = dataSource[0].schema
            newView.catalog = dataSource[0].catalog
            // newView.nameWithConnId = `${viewName}_${dataSource[0].connId}`
            // newView.database = dataSource[0].database
            // newView.data = { id: dataSource[0].id, type: dataSource[0].type }
        }
        dispatch(metadataActions.updateMetadataState({
            key: 'views',
            value: [...views, newView],
            others: [{
                key: 'editViewsTempData', value: { ...editViewsTempData, [key]: newView }
            }, {
                key: 'activeView', value: key
            }, {
                key: 'viewName', value: newView?.alias || ''
            }
            ]
        }))
    }

    // let viewListComp = () => {
    //     return <Row>
    //         <Col span={16} className='text-align-end'>
    //             <Select
    //                 value={views.filter(view => view.uuid === activeView)[0]?.uuid}
    //                 bordered={false}
    //                 onChange={e => null}
    //             // style={{ width: 300 }}
    //             >
    //                 {views.map(view => {
    //                     return (<Option value={view.uuid} key={view.uuid}>
    //                         <RenderViewTab
    //                             eachView={view}
    //                             activeView={activeView}
    //                             renderButton={false}
    //                             isOption={false}
    //                         />
    //                     </Option>)
    //                 }
    //                 )}
    //             </Select>
    //         </Col>
    //         <Col span={8}>
    //             <Button block icon={<PlusCircleOutlined />} onClick={handleAddNewView}>Add</Button>
    //         </Col>
    //     </Row>
    // }
    if ((!views.length || !(views.filter(v => !v.isDeleted).length))) {
        return (
            <Row className='margin-auto' data-testid='editor-views-multiple-conn-warning'>
                <Col span={24}>
                    <Empty
                        description={
                            <span>
                                {dataSourcesAddedToMetadata?.length > 1 ? 'Cannot use views with multiple connections' : 'No views available'}
                            </span>
                        }
                        {...(dataSourcesAddedToMetadata?.length > 1 ? { image: <CloseCircleTwoTone style={{ fontSize: '60px' }} /> } : {})}
                    >
                        {(dataSourcesAddedToMetadata?.length < 2) && <Tooltip title="(Alt + N)">
                            <Button icon={<PlusCircleOutlined />} ref={buttonAddNewRef} onClick={handleAddNewView} data-testid='view-button-to-add-new-view'>Add</Button>
                        </Tooltip>}
                    </Empty>
                </Col>
            </Row>
        )
    }
    return (
        <Row>
            <Col span={24}>
                <Row>
                    <Col span={activeView ? 6 : 20}>
                        {!activeView && Array.isArray(views) &&
                            <>
                                <Typography.Text strong ellipsis={true}>{`${views.filter(view => !view.isDeleted).length} View(s) found`}</Typography.Text>
                                {isMultiConnection && <div><Typography.Text type="danger" italic>Views are not supported with multiple connections</Typography.Text></div>}
                            </>
                        }

                    </Col>
                    {!activeView &&
                        <Col span={4}>
                            <Tooltip title="(Alt + N)">
                                <Button block icon={<PlusCircleOutlined />} ref={buttonAddNewRef2} onClick={handleAddNewView}>Add</Button>
                            </Tooltip>
                        </Col>
                    }
                </Row>
            </Col>
            {!activeView && <Col span={24} className={'hi-pt10'}>
                <Row gutter={[5, 5]}>
                    <Col span={24}>
                        <Row gutter={[5, 5]}>
                            {
                                views.map(eachView => {
                                    if (eachView.isDeleted) {
                                        return <></>
                                    }
                                    return <Col span={6} key={eachView.uuid}>
                                        <RenderViewTab eachView={eachView} activeView={activeView} isMultiConnection={isMultiConnection} />
                                    </Col>
                                })
                            }
                        </Row>
                    </Col>
                </Row>
            </Col>}
            <Col span={24}>
                {/* {activeView && <ViewEditor viewListComp={viewListComp} />} */}
                {activeView && <ViewEditor handleAddNewView={handleAddNewView} />}
            </Col>
        </Row>
    )
}

const RenderViewTab = ({ eachView, activeView, renderButton = true, isOption = false, isMultiConnection }) => {
    let activeProp = { type: "primary" }
    if (activeView !== eachView.uuid) {
        delete activeProp.type
    }
    const dispatch = useDispatch()
    const views = useSelector(state => state.metadata.present.views)
    const editViewsTempData = useSelector(state => state.metadata.present.editViewsTempData)
    const dataFetchedFor = useSelector(state => state.metadata.present.dataFetchedFor)
    const loadingStatus = useSelector(state => state.metadata.present.loadingStatus)

    const store = useStore()
    const Notify = notify(dispatch);

    const handleFetchView = ({ eachView }) => {
        let payload = getViewPayload({ activeView: eachView, views, dataSource: store.getState().metadata.present.dataSource, saveDetails: store.getState().metadata.present.saveDetails })
        if (!payload) {  // 6331
            let { dataSource } = eachView || {};
            let { schema, catSchemaPredicted = false, connId, baseType, datasourceName, changed = false, databaseType, catalog, type, id, sync = false, classifier, ...rest } = dataSource;
            if (dataSource && eachView) {
                payload = {
                    schema, catSchemaPredicted, connId, baseType, datasourceName, changed, databaseType, catalog, type, id, sync, classifier, viewId: eachView.id, queryType: eachView.queryType,
                    hasStoredProcedure: ('hasStoredProcedure' in eachView) ? eachView.hasStoredProcedure : false
                }
            } else {
                return
            }
        }
        if (dataFetchedFor[eachView.uuid]) {
            return
        }
        if (loadingStatus[eachView.uuid]) {
            return
        }
        dispatch(metadataActions.setViewFetching({
            type: eachView.uniqueKey,
            status: true
        }))
        dispatch(metadataActions.updateLoadingStatus({
            type: eachView.uuid,
            status: true
        }))
        metadataRequests(dispatch).retrieveView(payload, res => {
            handleFetchedView({ res, activeView: eachView })
        }, () => {
            Notify.error({
                message: 'Failed fetching view',
                type: 'error'
            })
            dispatch(metadataActions.setViewFetching({
                type: eachView.uniqueKey,
                status: false
            }))
        })
    }

    const handleFetchedView = ({ res, activeView }) => {
        let fetchedResult = { ...res }
        fetchedResult.labels = fetchedResult.labels.map(eachLabel => ({ ...eachLabel }))
        let result = cloneDeep(views).map(eachView => {
            if (eachView.uuid === activeView.uuid) {
                return { ...eachView, ...fetchedResult }
            }
            return eachView
        })
        let viewsTempData = cloneDeep(editViewsTempData)
        viewsTempData[activeView.uuid] = { ...activeView, ...fetchedResult }
        dispatch(metadataActions.updateLoadingStatus({
            type: eachView.uuid,
            status: false
        }))
        dispatch(metadataUndoRedoPurpose([{ key: 'views', value: result, }, { key: 'editViewsTempData', value: viewsTempData }]))
        // dispatch(metadataActions.updateMetadataState({
        //     key: 'views',
        //     value: result,
        //     others: [{
        //         key: 'editViewsTempData', value: viewsTempData,
        //     },
        //         // {
        //         //     type: 'loading', value: false, key: activeView.uniqueKey
        //         // }
        //     ]
        // }))
        dispatch(metadataActions.setViewFetching({
            type: activeView.uniqueKey,
            status: false
        }))
        // dispatch(metadataActions.updateLoadingStatus({
        //     type: eachView.uuid,
        //     status: false
        // }))
    }

    const handleUpdateActiveView = ({ eachView }) => {
        let updatedViewsTempData = {}
        if (activeView === eachView.uuid) {
            return
        }
        updatedViewsTempData[eachView.uuid] = eachView

        dispatch(metadataActions.updateMetadataState({
            key: 'activeView', value: eachView.uuid, others: [{
                key: 'editViewsTempData', value: (updatedViewsTempData || {})
            }, {
                key: 'viewName', value: eachView?.alias || ''
            }]
        }))
        handleFetchView({ eachView })

    }

    // This case is never encountered
    // if (!renderButton) {
    //     return <span onClick={() => {
    //         if (isOption) return
    //         handleUpdateActiveView({ eachView })
    //     }}><Paragraph
    //         ellipsis={{ tooltip: eachView.alias }}
    //         onClick={() => {
    //             if (isOption) {
    //                 return
    //             }
    //             handleUpdateActiveView({ eachView })
    //         }}
    //     >{eachView.alias}hello</Paragraph></span>
    // }
    return (
        <Button
            key={eachView.uuid}
            block
            style={isMultiConnection ? { background: '#F2DEDE' } : {}}
            disabled={isMultiConnection}
            onClick={() => {
                if (isOption) {
                    return
                }
                handleUpdateActiveView({ eachView })
            }}
        >
            <Paragraph ellipsis={{ tooltip: eachView.alias }}>{eachView.alias}</Paragraph>
        </Button>
    )
}

export default Views