import { DeleteOutlined, InfoCircleOutlined, PlusCircleOutlined } from "@ant-design/icons";
import { Button, Checkbox, Col, Form, Input, List, Radio, Row, Space, Tooltip, Typography, Drawer, Modal } from 'antd';
import { cloneDeep, isEqual } from 'lodash-es';
import React, { useEffect, useState } from 'react';
import { UnControlled as CodeMirror } from "react-codemirror2";
import { useDispatch, useSelector } from 'react-redux';
import metadataRequests from '../../../../../base/requests/metadata.requests';
import { metadataActions, metadataUndoRedoPurpose, setViewName } from '../../../../../redux/actions';
import notify from '../../../../hi-notifications/notify';
import { FDForSaveView } from '../../../utils/views/FDForSaveView';
import { updateTablesWithViews } from '../../../utils/views/updateTablesWithViews';
import { validateViewName } from '../../../utils/views/validateViewName';
import ViewPlaceholderSVG from './viewPlaceholder';
import { useDebounce } from "../../../../../hooks";
import constants from "../../../../../constants";
import { getColumnUniqueKey, getViewUniqueKey } from "../../../../../utils/reportQuery/utils/handleGetUniqueKey";
import { groupByKey } from "../../../utils/views/groupByKey";
import ViewEditorDrawer from "./viewEditorDrawer";

const { Text, Link } = Typography;

const tooltipDelay = constants.mouseEnterDelay
const ViewEditor = ({ handleAddNewView }) => {
    const views = useSelector(state => state.metadata.present.views, isEqual)
    const mode = useSelector(state => state.metadata.present.mode, isEqual)
    const tables = useSelector(state => state.metadata.present.tables, isEqual)
    const removedColumns = useSelector(state => state.metadata.present.removedColumns)
    const changedColumns = useSelector(state => state.metadata.present.changedColumns)
    const activeView = useSelector(state => state.metadata.present.activeView) // activeView is the uuid of view
    const dispatch = useDispatch()
    const editViewsTempData = useSelector(state => state.metadata.present.editViewsTempData, isEqual)
    const dataSourcesAddedToMetadata = useSelector(state => state.metadata.present.dataSourcesAddedToMetadata, isEqual)
    const loadingStatus = useSelector(state => state.metadata.present.loadingStatus, isEqual)
    const viewSessionVariables = useSelector(state => state.metadata.present.viewSessionVariables, isEqual)
    const viewName = useSelector(state => state.metadata.present.viewName, isEqual)

    const [activeViewInfo, setActiveViewInfo] = React.useState({})
    const [queryExecStatus, setQueryExecStatus] = React.useState([])
    const [viewSaveStatus, setViewSaveStatus] = React.useState([])
    // const [viewName, setViewName] = React.useState('')
    const [viewQuery, setViewQuery] = React.useState('')
    const [viewModalVisible, setViewModalVisible] = useState(false)
    const codeRef = React.useRef(null)
    const buttonExecuteRef = React.useRef(null)
    const buttonValidateRef = React.useRef(null)
    const buttonSaveRef = React.useRef(null)
    const buttonAddNewRef = React.useRef(null)
    const buttonDeleteRef = React.useRef(null)
    const buttonCancelRef = React.useRef(null)
    const Notify = notify(dispatch);
    const [isValidating, setIsValidating] = React.useState(false)
    const [queryInp, setQueryInp] = React.useState(null);
    const queryInpDebounceValue = useDebounce(queryInp, 500);
    const [showDrawer, setShowDrawer] = React.useState(null);
    const [openDrawer, setOpenDrawer] = React.useState(false);
    // let isValidating = React.useRef(false)
    // React.useEffect(() => { console.log('in show drawer useeffect', showDrawer) }, [showDrawer])
    const activeViewId = views?.find(v => v.uuid === activeView)?.id;

    React.useEffect(() => {
        if (queryInpDebounceValue !== null && queryInpDebounceValue) {
            let result = cloneDeep(editViewsTempData)
            if (!activeView) return
            if (!(activeView in result)) return
            result[activeView].query = queryInpDebounceValue;
            // result[activeView].labels = labelsBackUp
            if (editViewsTempData[activeView]?.query !== queryInpDebounceValue) {
                if ((queryInpDebounceValue || '').trim() !== editViewsTempData[activeView]?.query) {
                    result[activeView].isExecuted = false;
                }
                // dispatch(metadataActions.metadataUndoRedoPurpose([{ key: "editViewsTempData", value: result }]))
                dispatch(metadataActions.updateMetadataState({
                    key: 'editViewsTempData',
                    value: result
                }))
            }
        }
    }, [queryInpDebounceValue])

    useEffect(() => {
        if (codeRef?.current?.editor?.getValue() !== editViewsTempData[activeView]?.query) {
            codeRef?.current?.editor?.setValue(editViewsTempData[activeView]?.query || '')
        }
    }, [editViewsTempData])

    React.useEffect(() => {
        setActiveViewInfo(editViewsTempData[activeView] || {})
    }, [editViewsTempData, activeView, views])


    const handleKeyPress = React.useCallback((event) => {
        try {
            if (event.altKey && !event.ctrlKey && !event.shiftKey) {
                if (event.key?.toLowerCase() === 'e') {
                    (typeof buttonExecuteRef?.current?.click === 'function') && buttonExecuteRef?.current?.click()
                    event.preventDefault();
                }
                else if (event.key?.toLowerCase() === 's') {
                    (typeof buttonSaveRef?.current?.click === 'function') && buttonSaveRef?.current?.click()
                    event.preventDefault();
                }
                else if (event.key?.toLowerCase() === 'v') {
                    (typeof buttonValidateRef?.current?.click === 'function') && buttonValidateRef?.current?.click()
                    event.preventDefault();
                }
                else if (event.key?.toLowerCase() === 'n') {
                    (typeof buttonAddNewRef?.current?.click === 'function') && buttonAddNewRef?.current?.click()
                    event.preventDefault();
                }
                else if (event.key?.toLowerCase() === 'd') {
                    (typeof buttonDeleteRef?.current?.click === 'function') && buttonDeleteRef?.current?.click()
                    event.preventDefault();
                }
                else if (event.key?.toLowerCase() === 'c') {
                    (typeof buttonCancelRef?.current?.click === 'function') && buttonCancelRef?.current?.click()
                    event.preventDefault();
                }
            }
        }
        catch (e) { }
    }, [viewQuery]);

    React.useEffect(() => {
        document.addEventListener('keydown', handleKeyPress);
        setQueryInp(editViewsTempData[activeView]?.query);
        return () => {
            document.removeEventListener('keydown', handleKeyPress);
        };
    }, []);

    React.useEffect(() => {
        if (!codeRef?.current?.editor?.setValue || !editViewsTempData[activeView]?.query) {
            // setTimeout(() => {
            //     codeRef?.current?.editor?.setValue(editViewsTempData[activeView]?.query || '2')
            // })
        } else {
            codeRef?.current?.editor?.setValue(editViewsTempData[activeView]?.query || '')
        }
        if (editViewsTempData[activeView]?.alias !== viewName) {
            // dispatch(metadataUndoRedoPurpose([{ key: 'viewName', value: editViewsTempData[activeView]?.alias || '' }]))
            // dispatch(setViewName(editViewsTempData[activeView]?.alias || ''));
        }
        (editViewsTempData[activeView]?.query !== viewQuery) && setViewQuery(editViewsTempData[activeView]?.query || '')
    }, [activeView, views, loadingStatus])

    if (!activeViewInfo) {
        return <div>no active view </div>
    }
    const handleViewNameChange = (e) => {
        // TODO :: should make this performance efficient
        // let validation = validateViewName({ name: e.target.value, views, activeView })
        let validation = { isValid: true }
        if (!validation.isValid) {
            Notify.error({
                message: validation.msg,
                type: 'error'
            })
            return
        }
        dispatch(setViewName(e.target.value))
        return
    }
    if (loadingStatus[activeView]) {
        return (
            <ViewPlaceholderSVG />
        )
    }
    const handleSaveView = () => {
        if (!activeView) return
        let storeView = cloneDeep(views)
        let tempViews = cloneDeep(editViewsTempData)

        if (!viewQuery.length) {
            Notify.warning({
                type: 'Validation',
                message: 'Please enter valid query to save'
            })
            return
        }
        if (('isExecuted' in tempViews[activeView]) && !tempViews[activeView].isExecuted) {
            Notify.warning({
                type: 'Validation',
                message: 'Please Execute the query first and then save'
            })
            return
        }
        try {
            if (tempViews[activeView].error) {
                Notify.warning({
                    type: 'Validation',
                    message: 'Please enter valid query to save'
                })
                return
            }
        }
        catch (e) { }
        // let nameValidation = validateViewName({ name: viewName, views, activeView })
        let nameValidation = validateViewName(viewName, tables, activeView, views)
        if (!nameValidation.isValid) {
            Notify.error({
                message: nameValidation.msg,
                type: 'error'
            })
            return
        }
        storeView = storeView.map(view => {
            if (view.uuid === activeView && activeView in tempViews) {
                tempViews[activeView].name = viewName
                tempViews[activeView].alias = viewName
                tempViews[activeView].keyName = viewName
                tempViews[activeView].query = viewQuery
                return tempViews[activeView]
            }
            return view
        })
        let formData = FDForSaveView({ views: storeView, activeView, dataSourcesAddedToMetadata });
        setViewSaveStatus((() => {
            if (viewSaveStatus.includes(activeView)) {
                return [...viewSaveStatus]
            }
            else {
                return [...viewSaveStatus, activeView]
            }
        })())
        if (!formData) return
        metadataRequests(dispatch).saveView(formData, (res) => {
            setViewSaveStatus(viewSaveStatus.filter(uuid => uuid !== activeView))
            storeView = storeView.map(eachView => {
                if (eachView.uuid === activeView) {
                    eachView.id = res.viewId
                    eachView.type = res.tables[eachView.alias].type
                    eachView.columns = res.tables[eachView.alias].columns;
                    eachView.uniqueKey = !["test"].includes(process.env.NODE_ENV) && getViewUniqueKey({ mode: mode || 'create', id: res.viewId, dbId: eachView.connId })
                    for (const [key, value] of Object.entries(eachView.columns)) {
                        eachView.columns[key] = {
                            ...value,
                            category: 'column',
                            parentCategory: 'view',
                            columnKey: key,
                            fullyQualifiedColumn: eachView.alias + '.' + key,
                            tableId: eachView.id,
                            connId: eachView.connId,
                            name: value.alias,
                            uniqueKey: !["test"].includes(process.env.NODE_ENV) && getColumnUniqueKey({ mode: mode || "create", id: value.id, dbId: eachView.connId }),
                            // tableName: eachView.name || eachView.alias,
                            tableKey: value.tableKey || eachView.name || eachView.alias,
                        }
                    }
                    eachView.alias = eachView.alias || eachView.name
                    eachView.isModified = true
                }
                return eachView
            })
            let updatedTables = updateTablesWithViews({ tables, views: storeView, activeView })
            let newChangedColumns = changedColumns.filter(e => e.tableId !==  tempViews[activeView].id)
            let newRemovedColumns = removedColumns.filter(e => e.tableId !==  tempViews[activeView].id)

            dispatch(metadataActions.updateMetadataState({
                key: 'tables',
                value: updatedTables,
                others: [{
                    key: 'views',
                    value: storeView
                },
                {
                    key: 'activeView',
                    value: null
                }, {
                    key: 'viewName',
                    value: ''
                },
                {
                    key: 'changedColumns',
                    value: newChangedColumns
                },
                {
                    key: 'removedColumns',
                    value: newRemovedColumns
                }
                ]
            }))
        },
            err => {
                setViewSaveStatus(viewSaveStatus.filter(uuid => uuid !== activeView))
                err.message && Notify.error({
                    type: 'Network Call',
                    message: err.message
                })
            })
        // dispatch(metadataActions.updateMetadataState({
        //     key: 'views',
        //     value: storeView
        // }))
    }
    const handleQueryTypeChange = (e) => {
        if (!activeView) return
        let result = cloneDeep(editViewsTempData)
        if (!(activeView in result)) return
        try {
            result[activeView][`${result[activeView].queryType}_query`] = typeof codeRef?.current?.editor?.getValue === 'function' ? codeRef?.current?.editor?.getValue() : ''
        }
        catch (e) { }
        result[activeView].queryType = e.target.value
        try {
            result[activeView].query = result[activeView][`${e.target.value}_query`] || ''
        }
        catch (e) { }
        typeof codeRef?.current?.editor?.setValue === 'function' && codeRef?.current?.editor?.setValue(result[activeView].query || '')
        dispatch(metadataActions.updateMetadataState({
            key: 'editViewsTempData',
            value: result
        }))
    }

    const handleQueryChange = (value) => {
        let result = cloneDeep(editViewsTempData)
        if (!activeView) return
        if (!(activeView in result)) return
        result[activeView].query = value;
        (value !== viewQuery) && setViewQuery(value);
        if (value !== queryInp) {
            if (editViewsTempData[activeView]?.query !== value) {
                setQueryInp(value);
            }
        }
        return
        dispatch(metadataActions.updateMetadataState({
            key: 'editViewsTempData',
            value: result
        }))
    }

    const executeQuery = (validate = false) => {
        let storeView = cloneDeep(views)
        // let storeView = cloneDeep(views)
        let tempViews = cloneDeep(editViewsTempData)
        if (!viewQuery.length) {
            Notify.warning({
                type: 'Validation',
                message: 'Please enter valid query to execute'
            })
            return
        }
        setQueryExecStatus((() => {
            if (queryExecStatus.includes(activeView.uuid)) {
                return queryExecStatus.filter(id => id !== activeView.uuid)
            }
            else {
                return [...queryExecStatus, activeView.uuid]
            }
        })())
        storeView = storeView.map(view => {
            if (view.uuid === activeView && activeView in tempViews) {
                tempViews[activeView].query = viewQuery
                return tempViews[activeView]
            }
            return view
        })
        if (validate) {
            // isValidating = true
            setIsValidating(true)
        }
        let formData = FDForSaveView({ views: storeView, activeView, dataSourcesAddedToMetadata });
        metadataRequests(dispatch).retrieveViewLabels(formData, res => {
            // let result = updateViewWithLabels({ views: storeView, activeView, res })
            let result = cloneDeep(editViewsTempData)
            if (!activeView) return
            if (!(activeView in result)) return
            result[activeView].isExecuted = true;
            if (res.error) {
                result[activeView].error = res.error
            }
            else {
                result[activeView].error = false
            }
            result[activeView].validate = !!!res.error
            if (!validate) {
                result[activeView].labels = res.labels.map(column => ({ ...column, checked: true }))
                result[activeView].data = res.data
                result[activeView].processedQuery = res.processedQuery
                dispatch(metadataActions.updateMetadataState({ // fixed immediate validate after execute issue
                    key: 'editViewsTempData',
                    value: result
                }))
            }
            setQueryExecStatus(queryExecStatus.filter(id => id !== activeView.uuid))
            // isValidating = false
            Notify.success({
                type: 'Network Call',
                message: `${validate ? 'Code' : 'Query'} Execution Process Completed`
            })
            setIsValidating(false)
        }, err => {
            setQueryExecStatus(queryExecStatus.filter(id => id !== activeView.uuid))
            // isValidating = false
            setIsValidating(false)
            // Notify.error({
            //     type: 'Network Call',
            //     message: err.message
            // })
        })
    }

    const handleLabelCheck = ({ checked, item }) => {
        let result = cloneDeep(editViewsTempData)
        if (!activeView) return
        if (!(activeView in result)) return
        result[activeView].labels = result[activeView].labels.map(column => {
            if (item.name === column.name) {
                column.checked = checked
            }
            return column
        })
        dispatch(metadataActions.updateMetadataState({
            key: 'editViewsTempData',
            value: result
        }))
    }

    const handleAddParameter = (key) => {
        // codeRef?.current?.editor?.setValue((editViewsTempData[activeView]?.query || '') + `${key}`)
        codeRef?.current?.editor?.setValue((viewQuery || '') + `${key}`)
    }

    const handleCancel = () => {
        if (editViewsTempData[activeView]?.id) {
            dispatch(metadataActions.updateMetadataState({
                key: 'activeView',
                value: null
            }))
            return
        }
        handleDeleteView()
    }

    const handleDeleteView = () => {
        let deletedView = cloneDeep(views).filter(eachView => eachView.uuid === activeView)
        if (Array.isArray(deletedView)) (deletedView = deletedView[0])
        let updatedViews = cloneDeep(views).filter(eachView => eachView.uuid !== activeView)
        let tableToDelete = null
        let storeTables = cloneDeep(tables)
        Object.values(tables).forEach(data => {
            if (data.id === deletedView?.id) {
                tableToDelete = data.keyName
            }
        })
        if (tableToDelete)
            delete storeTables[tableToDelete]
        dispatch(metadataActions.updateMetadataState({
            key: 'activeView',
            value: null,
            others: [
                {
                    key: 'views',
                    value: updatedViews
                },
                {
                    key: 'tables',
                    value: storeTables
                },
                {
                    key: 'viewName', value: ''
                }
            ]
        }))
    }

    const renderParameters = () => {
        let data = {
            user: ['${user}.id', '${user}.email', '${user}.enabled', '${user}.name', '${user}.isExternalUser'],
            org: ['${org}.id', '${org}.name'],
            profile: ['${profile}.name', '${profile}.id', '${profile}.value'],
            role: ['${role}.id', '${role}.name']
        }
        if (viewSessionVariables && activeView && editViewsTempData[activeView].queryType && viewSessionVariables[editViewsTempData[activeView].queryType]?.expressions) {
            data = viewSessionVariables[editViewsTempData[activeView].queryType].expressions
        }

        const tooltips = {
            '${user}.name': 'Represents the user\'s name.',
            '${user}.email': 'Contains the user\'s email address.',
            '${user}.id': 'Holds the user\'s unique identifier.',
            '${user}.enabled': 'Indicates whether the user is enabled.',
            '${user}.isExternalUser': 'Specifies if the user is an external user.',  
            '${org}.name': 'Represents the organization\'s name.',
            '${org}.id': 'Contains the organization\'s unique identifier.',
            '${profile}.name': 'Represents all the profile\'s name in comma seperated values. e.g: \'department\',\'country\',\'area\' ',
            '${profile}.id': 'Contains the profile\'s unique identifier. e.g: 1,2,3',
            '${profile}.value': 'Holds the profile\'s value. e.g: \'Sales\',\'India\',\'APAC\'. Access specific profile value of a single profile. e.g: ${profile[\'country\']}.value = \'India\',\'USA\'',
            '${role}.name': 'Represents the role\'s name. e.g: \'ROLE_ADMIN\',\'ROLE_USER\'',
            '${role}.id': 'Contains the role\'s unique identifier. e.g: 1,2',
        }
        return (
            <Col span={24}>
                {Object.keys(data).map((key) => {
                    return <Row key={key}>
                        <Col span={24}>
                            <span ><strong>{key}:&nbsp;&nbsp;</strong></span>
                            <span >{data[key].map((value, index) => {
                                return <span className='md-view-parameter' key={index}>
                                     <Tooltip title={tooltips[value]} placement="top">
                                    <span className='cursor-pointer onhover-bold'
                                        onClick={(e) => e.detail === 2 && handleAddParameter(value)}
                                    >{value}&nbsp;&nbsp;</span>
                                </Tooltip>
                                </span>
                            })}
                            </span>
                        </Col>
                    </Row>
                })}
            </Col>
        )
    }
    return (
      <>
        <Modal
          title={"Save View Confirmation"}
          visible={viewModalVisible}
          
          onOk={() => {
            handleSaveView();
          }}
          onCancel={() => {
            // let updatedTables = updateTablesWithViews({ tables, views, activeView })
            // dispatch(metadataActions.updateMetadataState({
            //     key: 'tables',
            //     value: updatedTables,
            // }))
            setViewModalVisible(false);
          }}
        ><div style={{height: "180px",overflowY:"auto"}}>
          <Text>
            The view's definition has been updated, which may affect actions
            carried out on its columns. Would you like to proceed with updating
            the column details in the metadata?
          </Text>
          <ChangedOrRemovedFields 
          removedColumns={removedColumns} 
          changedColumns={changedColumns}
          currentViewId={activeViewId}/>
          </div>
        </Modal>
        <Row data-testid="hi-metadata-editor-container-" className='metadata-view-editor-container'>
            <Col span={12} >
                <Form
                    className='view-editor-form'
                // size={'small'}
                >
                    <Form.Item
                        label="View Name"
                    >
                        <Input
                            // value={activeViewInfo.alias}
                            value={viewName}
                            // defaultValue={activeViewInfo.alias}
                            onChange={handleViewNameChange}
                        />
                    </Form.Item>
                </Form>
            </Col>
            <Col span={12} className="text-align-end">
                <Space>
                    <Tooltip mouseEnterDelay={tooltipDelay} title="(Alt + D)"><Button icon={<DeleteOutlined />} ref={buttonDeleteRef} onClick={handleDeleteView}>Delete</Button></Tooltip>
                    <Tooltip mouseEnterDelay={tooltipDelay} title="(Alt + N)"><Button icon={<PlusCircleOutlined />} ref={buttonAddNewRef} onClick={handleAddNewView}>Add</Button></Tooltip>
                </Space>
            </Col>
            <Col span={24}>
                <Row gutter={[5, 5]}>
                    <Col span={18} className={'editor-code-mirror-part '}>
                        <Row>
                            <Col span={24}>
                                <Row>
                                    <Radio.Group
                                        onChange={handleQueryTypeChange}
                                        value={activeViewInfo.queryType}
                                    >
                                        <Radio value={'conditionIf'}>Query
                                            <span className='ds-fetch-error'>
                                                <Tooltip mouseEnterDelay={tooltipDelay} title={'You can use below expression to form view or subquery'}>
                                                    <InfoCircleOutlined                                                   
                                                        onClick={(e) => {
                                                        if (e.target) {
                                                        setShowDrawer('Query');
                                                        setOpenDrawer(true);
                                                        }
                                                        e.preventDefault();
                                                    }}
                                                    />
                                                </Tooltip>
                                            </span>
                                        </Radio>
                                        <Radio value={'groovy'}>
                                            Dynamic Query
                                            <span className='ds-fetch-error'>
                                                <Tooltip mouseEnterDelay={tooltipDelay} title={'You can build your dynamic query with the help of groovy script using the global variables and functions provided below'}>
                                                <InfoCircleOutlined 
                                                    onClick={(e) => {
                                                        if (e.target) {
                                                        setShowDrawer('Dynamic Query');
                                                        setOpenDrawer(true);
                                                        }
                                                        e.preventDefault();
                                                    }}
                                                    />
                                                </Tooltip>
                                            </span>
                                        </Radio>
                                    </Radio.Group>
                                </Row>
                            </Col>
                            <Col span={24}>
                                <CodeMirror
                                    // value={activeViewInfo?.query || ''}
                                    ref={codeRef}
                                    options={{
                                        lineWrapping: true,
                                        lint: true,
                                        mode: 'sql',
                                        // mode: 'java',
                                        lineNumbers: true,
                                        theme: "dracula",
                                        autoCloseTags: true,
                                        autoCloseBrackets: true,
                                        autofocus: true,
                                    }}
                                    defaultValue={activeViewInfo?.query || ''}
                                    editorDidMount={() => {
                                        codeRef?.current?.editor?.setValue(activeViewInfo?.query || '')
                                    }}
                                    onChange={(editor, data, value) => {
                                        handleQueryChange(value)
                                    }}
                                />
                            </Col>
                            <Col span={24} className='hi-pd-t-b-5'>
                                <Row>
                                    <Col span={10}>
                                        <Space>
                                            <Tooltip mouseEnterDelay={tooltipDelay} title="(Alt + E)">
                                                <Button
                                                    ref={buttonExecuteRef}
                                                    onClick={() => executeQuery(false)}
                                                    loading={queryExecStatus.includes(activeView.uuid) && !isValidating}
                                                >Execute</Button>
                                            </Tooltip>
                                            {activeViewInfo.queryType === 'groovy' &&
                                                <Tooltip mouseEnterDelay={tooltipDelay} title="(Alt + V)">
                                                    <Button
                                                        ref={buttonValidateRef}
                                                        onClick={() => executeQuery(true)}
                                                        // loading={queryExecStatus.includes(activeView.uuid) && isValidating}
                                                        loading={isValidating}
                                                    >Validate</Button>
                                                </Tooltip>
                                            }
                                        </Space>
                                    </Col>
                                    <Col span={14} className="text-align-end">
                                        <Space>
                                            <Tooltip mouseEnterDelay={tooltipDelay} title="(Alt + C)">
                                                <Button
                                                    ref={buttonCancelRef}
                                                    // className='views-query-cancel-button'
                                                    onClick={handleCancel}
                                                >Cancel</Button>
                                            </Tooltip>
                                            <Tooltip mouseEnterDelay={tooltipDelay} title="(Alt + S)">
                                                <Button
                                                    ref={buttonSaveRef}
                                                    onClick={() => {
                                                  let isTrue = false;
                                                  isTrue = removedColumns.find(
                                                    (ele) =>
                                                      ele.isView &&
                                                      ele.tableId === activeViewInfo.id
                                                  );
                                                  if (!isTrue) {
                                                    isTrue = changedColumns.find(
                                                      (ele) =>
                                                        ele.isView &&
                                                        ele.tableId === activeViewInfo.id
                                                    );
                                                  }
                                                  if (isTrue) {
                                                    setViewModalVisible(true);
                                                  } else {
                                                    handleSaveView();
                                                  }
                                                }}
                                                    type='primary'
                                                    // className='views-query-save-button'
                                                    loading={viewSaveStatus.includes(activeView)}
                                                >Save</Button>
                                            </Tooltip>

                                        </Space>
                                    </Col>
                                </Row>
                            </Col>
                        </Row>
                        <Row>
                            <Space direction="vertical" size='small' className="width100pcnt">
                                {activeViewInfo?.processedQuery && <Col span={24} className='hi-view-processed-query font-size-12'>
                                    <span ><strong>Processed Query : </strong>{activeViewInfo.processedQuery}</span>
                                </Col>}
                                {activeViewInfo.error && <Col span={24} className='hi-view-query-error font-size-12'>
                                    <span ><strong>Error : </strong>{activeViewInfo.error}</span>
                                </Col>}

                                <Col span={24} className='hi-view-processed-query font-size-12'>
                                    <strong>Double click the variables to use them in the script/query</strong>
                                    {/* <Col span={24} className='font-size-12'>
                                    </Col> */}
                                    <Row>
                                        {renderParameters()}
                                    </Row>
                                </Col>
                            </Space>
                        </Row>
                    </Col>
                    <Col span={6} className='border-left-grey padding-left-7'>
                        <Row gutter={8}>
                            <Col span={24} style={{ display: "flex"}}>
                                <strong>Columns</strong>
                    <Tooltip
                      mouseEnterDelay={tooltipDelay}
                      title={
                        "Executing a view query fetches columns from the underlying tables as defined in the view. For dynamic queries, it's essential to include all required columns in the final query to ensure they appear in the result set"
                      }
                    >
                      <InfoCircleOutlined className="ds-fetch-error" />
                    </Tooltip>  
                    <ViewEditorDrawer/>  
                            </Col>
                            <Col span={24}>
                                <List
                                    size="small"
                                    dataSource={activeViewInfo?.labels || []}
                                    renderItem={item => (
                                        <List.Item style={{
                                            display: "flex",
                                            justifyContent: "flex-start",
                                            gap: "5px",
                                        }}>
                                           {/* <Checkbox
                                                checked={item.checked}
                                                style={{ width: "20px" }}
                                                onChange={e => {
                                                    handleLabelCheck({ checked: e.target.checked, item })
                                                }}></Checkbox> */}
                                            <Tooltip overlayStyle={{ padding: "2px" }} title={item.name}><Typography.Text style={{ flex: "1", textOverflow: "ellipsis", overflow: "hidden", whiteSpace: "nowrap" }}>{item.name}</Typography.Text></Tooltip>
                                        </List.Item>
                                    )}
                                />
                            </Col>
                        </Row>
                    </Col>
                </Row>
            </Col>
            <Drawer title={showDrawer} width={"50%"} placement="right" onClose={() => setOpenDrawer(false)} visible={!!openDrawer}>
          <>      <Text level={3} strong>Example : {viewSessionVariables[showDrawer === 'Query' ? 'conditionIf' : 'groovy']?.placeHolder}</Text>
                <div dangerouslySetInnerHTML={{ __html: nl2br(viewSessionVariables[showDrawer === 'Query' ? 'conditionIf' : 'groovy']?.helpText) }} />
         </></Drawer>
        </Row>
      </>
    );
}

const ChangedOrRemovedFields = ({ removedColumns, changedColumns, currentViewId }) => {
    const modifications = [
        ...changedColumns.map(el => ({...el, modType: 'change'})),
        ...removedColumns.map(el => ({...el, modType: 'remove'}))
    ].filter(el => el.tableId === currentViewId);

    const groups = groupByKey(modifications, ({viewName}) => viewName);

    const modificationsEl = [];

    Object.entries(groups).forEach(e => {
        const viewName = e[0];
        const mods = e[1];

        const block = <div style = {{marginTop:"5px"}}>
            <p style = {{margin:"0px"}}>The actions related to the <strong>{viewName}</strong> view that are affected have been listed below and will be discarded:</p>
            <ul>
                {mods.map(m => {
                    if(m.modType == 'change'){
                        return <li>
                        <p key={m.columnId} style = {{margin:"0px"}}>
                        {`Changed `}
                        <strong>{m.columnName}</strong>
                        {` to `}
                        <strong>{m.alias}</strong>
                        </p>
                        </li>
                    }else{
                        return <li>
                        <p key={m.id} style = {{margin:"0px"}}>
                        {`Removed `}
                        <strong>{m.columnName}</strong>
                        </p>
                        </li>
                    }
                })}
            </ul>
        </div>

        modificationsEl.push(block)
    })
    
    return (
      <div>
        {modificationsEl}
      </div>
    );
}

function nl2br(string = '', is_xhtml) {
    // var breakTag = (is_xhtml || typeof is_xhtml === 'undefined') ? '<br />' : '<br>';
    // return (string + '').replace(/([^>\r\n]?)(\r\n|\n\r|\r|\n)/g, '$1' + breakTag + '$2');
    // if (!string.length) return ''
    // let result = string.replace(/(\t)/g, '____');
    let result = string.split('\t').join('&nbsp;&nbsp;&nbsp;&nbsp;')
    result = result.replace(/(\r\n|\r|\n)/g, '<br>');
    return result
}

export default ViewEditor