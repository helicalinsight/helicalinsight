import { CaretDownOutlined, DeleteOutlined, ExclamationCircleOutlined, InfoCircleOutlined, PlusOutlined, QuestionCircleOutlined, SwapOutlined } from '@ant-design/icons';
import { AutoComplete, Col, Dropdown, Image, Menu, Modal, Popconfirm, Popover, Row, Tooltip, Typography } from 'antd';
import { cloneDeep } from 'lodash-es';
import React from 'react';
import { useDrop } from 'react-dnd';
import { useDispatch, useSelector, useStore } from 'react-redux';
import constants from '../../../../../constants';
import { metadataActions } from '../../../../../redux/actions';
import { classnames } from '../../../../../utils/classNames';
import { fetchJoins, handleJoinActions, handleTableExpand } from '../../../utils';
import { utilsJoinsActions } from '../../../utils/joins/joins';
import SortableTable from './hi-sortable-table';
import JoinsPopover from './joinsPopover';
import PopconfirmBody from '../../../../common/components/Hi-Popconfirm';

const { Text } = Typography


function Joins() {
    const dataSource = useSelector(state => state.metadata.present.dataSourcesAddedToMetadata)
    const tables = useSelector(state => state.metadata.present.tables)
    const tablesMergeType = useSelector(state => state.metadata.present.tablesMergeType)
    const { joins, selectedJoinNameData, mode } = useSelector(state => state.metadata.present)
    const dataFetchedForJoins = useSelector(state => state.metadata.present.dataFetchedFor.joins)
    const isShowJoinModal = useSelector(state => state.metadata.present.isShowJoinModal)
    const outsideClicked = useSelector(state => state.metadata.present.outsideClicked);
    const dispatch = useDispatch()
    const store = useStore()
    const [data, setData] = React.useState([])
    const [allOptions, setAllOptions] = React.useState([])
    const [autoCompleteValue, setAutoCompleteValue] = React.useState({ left: {}, right: {} });
    const [options, setOptions] = React.useState([]);
    const [selectedJoins, setSelectedJoins] = React.useState([])
    const joinsRef = React.useRef(null)
    const joinsLoadingStatus = useSelector(state => !!state.metadata?.present.joinsLoadingStatus?.length)
    const { handleJoinUpdate } = utilsJoinsActions({ dispatch, joins })
    const [listOfTableNames, setListOfTableNames] = React.useState((() => {
        let tabCols = []
        Object.values(tables).map(eachTable => {
            if (!eachTable.columns) {
                tabCols.push(eachTable.alias)
            }
            Object.keys(eachTable.columns || []).map(col => {
                eachTable.columns[col] && eachTable.columns[col]?.alias && tabCols.push(`${eachTable.alias}.${eachTable.columns[col].alias}`)
            }
            )
        })
        return tabCols
    })());
    const buttonAddRef = React.useRef(null);

    // function addDataSourceToJoin(column) {
    //     // const sourceTable = Object.values(tables).find(table => table.name === column.tableKey)
    //     if (!isObject(tables)) return undefined
    //     const sourceTable = tables[column?.tableKey]
    //     return sourceTable?.dataSource;

    // }

    function getDataSourceName(record, text) {
        const source = dataSource.find(source => source?.dbId === record?.dbId || source?.connId === record?.connId);
        const [tableName, columnName] = text.split('.');
        return <table
            className={`metadata-section-table-popover`}
        >
            <tbody>
                <tr>
                    <td>Table :&nbsp;</td>
                    <td>{tableName}</td>
                </tr>
                <tr>
                    <td>Column :&nbsp;</td>
                    <td>{columnName}</td>
                </tr>
                <tr>
                    <td>DataSource :&nbsp;</td>
                    <td>{source?.datasourceName}</td>
                </tr>
                <tr>
                    <td>Id :&nbsp;</td>
                    <td>{source?.id}</td>
                </tr>
            </tbody>
        </table>
    }

    React.useEffect(() => {
        getAutoCompleteOptions({})
        let tabCols = []
        Object.values(tables).map(eachTable => {
            if (!eachTable.columns) {
                tabCols.push(eachTable.alias)
            }
            Object.keys(eachTable.columns || []).map(col => {
                tabCols.push(`${eachTable.alias}.${eachTable.columns[col].alias}`)
            }
            )
        })
        setListOfTableNames(tabCols)
    }, [tables])

    React.useEffect(() => {
        prepareJoinsToRender()
    }, [tables, joins]);

    React.useEffect(() => { // component did mount
        tablesMergeType !== 'merge' && !(dataFetchedForJoins) && fetchJoins({ dataSource, tables, dispatch });
        tablesMergeType === 'merge' && !(dataFetchedForJoins) && !joins.length && fetchJoins({ dataSource, tables, dispatch, mergeType: 'merge' });
    }, []);

    React.useEffect(() => { // component did mount
        isShowJoinModal && tablesMergeType === 'merge' && joins.length && Modal.confirm({
            title: 'Joins',
            icon: <ExclamationCircleOutlined />,
            content: <Row>
                <Col>
                    <strong>Changes will be reflected based on below actions:</strong>
                </Col>
                <Col>
                    <strong>Merge </strong>:The changes made in joins will be kept as is and new join(s) will be added.
                </Col>
                <Col>
                    <strong>Reload</strong>:The joins will be overriden as per the table(s) in the metadata panel
                </Col>
            </Row>,
            okText: "Reload",
            cancelText: "Merge",
            okType: 'danger',
            cancelType: 'primary',
            onOk: () => {
                fetchJoins({ dataSource, tables, dispatch })
            },
            onCancel: (e) => {
                if (e.name) {
                    fetchJoins({ dataSource, tables, dispatch, mergeType: 'merge', existingJoins: cloneDeep(joins) })
                }
                if (typeof e === 'function') {
                    e()
                }
            }
        })
    }, [isShowJoinModal]);

    const handleKeyPress = (event) => {
        try {
            if (event.altKey && !event.ctrlKey && !event.shiftKey) {
                if (event.key?.toLowerCase() === 'n') {
                    (typeof buttonAddRef?.current?.click === 'function') && buttonAddRef?.current?.click()
                    event.preventDefault();
                }
            }
        }
        catch (e) { }
    }

    React.useEffect(() => {
        document.addEventListener('keydown', handleKeyPress);
        return () => document.removeEventListener('keydown', handleKeyPress);
    }, []);

    const getAutoCompleteOptions = ({ searchText = '' }) => {
        let data = store.getState().metadata.present.tables
        let options = []
        Object.values(data).map(table => {
            options.push({ value: table.alias, display: table.alias, keyname: table.keyName, category: 'table', arecolumnsavailable: `${!!table.columns}`, key: table.keyName, tableId: table.id, dbId: table.connId })
            if (table.columns) {
                Object.values(table.columns).map(col => {
                    options.push({ value: `${table.alias}.${col.alias}`, display: `${table.alias}.${col.alias}`, category: 'column', key: col.uuid, tableId: col.tableId, dbId: table.connId, columnId: col.id, tableName: col.tableKey, columnName: col.name, })
                })
            }
        })
        setAllOptions(options)
        setOptions(options)
    }

    const onSelect = ({ record, data, type, currentOption }) => {
        if (!data.includes('.')) {
            // let currentOption = (options?.filter(opt => opt.value === data) || [{}])[0];
            if (currentOption.category === 'table') {
                if (currentOption.arecolumnsavailable === 'false') {
                    let currentTable = tables[currentOption.keyname]
                    handleTableExpand({
                        currentTable,
                        dispatch,
                        dataSource,
                        tables,
                        mode
                    })
                }
            }
        }

        if (data.includes('.') && type && record) {
            addJoinOnOptionSelect({ record, type, currentOption })
            setAutoCompleteValue(() => {
                delete autoCompleteValue['left'][record.key]
                delete autoCompleteValue['right'][record.key]
                return autoCompleteValue
            })
        }
        else {
            setAutoCompleteValue(() => {
                autoCompleteValue[type][record.key] = data
                return autoCompleteValue
            })
        }
    };

    const onChange = ({ data, record, type }) => {
        if (data.includes('.')) {
            const [searchedTableName] = data.split('.');
            const searchedTable = tables[searchedTableName]
            if (searchedTable && !searchedTable.columnsFetched) {
                handleTableExpand({
                    currentTable: searchedTable,
                    dispatch,
                    dataSource,
                    tables,
                    mode
                });
            }
        }
        setAutoCompleteValue(() => {
            autoCompleteValue[type][record.key] = data
            return autoCompleteValue
        })

    };

    const columnOptions = ({ record, type }) => (
        <Menu onClick={({ key }) => {
            if (key === 'swapColumns') {
                setAutoCompleteValue(() => {
                    let left = autoCompleteValue['left'][record.key]
                    autoCompleteValue['left'][record.key] = autoCompleteValue['right'][record.key]
                    autoCompleteValue['right'][record.key] = left
                    return autoCompleteValue
                })
            }
            handleJoinActions({ action: key, record, type, dispatch, joins })
        }}>
            <Menu.Item key='discardColumn' icon={<DeleteOutlined />}>
                <a>
                    Discard column
                </a>
            </Menu.Item>
            <Menu.Item key='swapColumns' icon={<SwapOutlined />}>
                <a>
                    Swap columns
                </a>
            </Menu.Item>
            <Menu.Divider />
            <Menu.Item key='discardJoin' icon={<DeleteOutlined />}>
                <a>
                    Discard join
                </a>
            </Menu.Item>
        </Menu>
    )

    const typeOptions = ({ record, type }) => {
        return <Menu
            defaultSelectedKeys={[record.type.toLocaleLowerCase()]}
            onClick={({ key }) => {
                handleJoinActions({ action: key, record, type, dispatch, joins })
            }}>
            {['Inner', 'Left', 'Right', 'Full'].map(key => (
                <Menu.Item key={key.toLocaleLowerCase()}>
                    <Image
                        preview={false}
                        src={`images/hi-metadata/Joins_images/${key}.png`}
                    />
                    {key}
                </Menu.Item>
            ))}
        </Menu>
    }

    const addJoinOnOptionSelect = ({ record, type, currentOption }) => {
        let value = autoCompleteValue[type][record.key];
        if (currentOption.tableName) {
            const currentTable = tables[currentOption.tableName];
            currentOption.tableName = currentTable.name;
        }
        if ((value || '').includes('.') && allOptions.filter(opt => opt.value === value)?.length) {
            /*let _joins = cloneDeep(joins) // 5956
            _joins.forEach(function (join) {
                if (join.uuid === record.key) {
                    join[type === 'left' ? 'leftColumn' : 'rightColumn'] = value
                    join[type] = {
                        ...join[type],
                        table: value.split('.')[0],
                        column: value.split('.')[1],
                        dataSource: dataSource
                    }
                }
            })
            dispatch(metadataActions.updateJoinsData({ data: _joins, override: true })) 5956*/
            handleJoinUpdate({ record, type, currentOption });
        }
    }

    const handleColumnDrop = ({ column, type, join, currentOption }) => {
        /* let result = { // 5956
            table: tables[column.tableKey].name,
            column: column.name,
            alias: column.alias,
        }
        let joinColumn = `${result.table}.${result.column}`
        let updatedJoins = cloneDeep(store.getState().metadata.joins)
        updatedJoins = updatedJoins.map(result => {
            let eachJoin = cloneDeep(result)
            if (eachJoin.uuid === join.uuid) {
                eachJoin[type === 'left' ? 'leftColumn' : 'rightColumn'] = joinColumn
                eachJoin[type] = {
                    ...eachJoin[type],
                    table: tables[column.tableKey].name,
                    column: column.name,
                    columnId: column.originalId,
                    dataSource: addDataSourceToJoin(column)
                };
            }
            return eachJoin
        })
        dispatch(metadataActions.updateJoinsData({ data: updatedJoins, override: true }))  5956*/
        // `${tables[column.tableKey].name}.${column.name}`
        // value = `${currentOption.tableAlias}.${currentOption.columnAlias}`
        let record = { join, key: join.uuid }; //
        if (currentOption.tableName) {
            const currentTable = tables[currentOption.tableName];
            currentOption.tableName = currentTable.name;
        }
        handleJoinUpdate({ record, type, currentOption });
    }

    const validateTableColumn = (record, isLeftColumn) => {
        if (isLeftColumn && !record.left?.isValid) {
            return true;
        }
        if (!isLeftColumn && !record.right?.isValid) {
            return true;
        }
        return false;
        // const allTableIds = Object.values(tables).map(table => table.id);
        // let joinName;
        // if(isLeftColumn) {
        //     if(!allTableIds.includes(record.left?.tableId)) return true;
        //     joinName = record.leftColumn;    
        // } else {
        //     if(!allTableIds.includes(record.right?.tableId)) return true;
        //     joinName = record.rightColumn;    
        // }

        // const [table, column] = joinName.split('.');
        // if (table && column) {
        //     if (listOfTableNames.includes(joinName)) {
        //         return false
        //     }
        //     else {
        //         return !(listOfTableNames.includes(`${table}`))
        //     }
        // }
        // else if (table && !column) {
        //     if (listOfTableNames.includes(joinName)) {
        //         return false
        //     }
        //     else {
        //         return !(listOfTableNames.includes(`${table}`))
        //     }
        // }
        // else if (!table) {
        //     return true
        // }
        // return false
    }

    function checkJoinSelection(record, joinName) {
        if (outsideClicked || !joinName || !record.tableId || !record.column) return false;
        if (selectedJoinNameData.category === "column") {
            const columnName = joinName.split('.')[1];
            if (columnName === selectedJoinNameData.value && selectedJoinNameData.tableId === record.tableId && selectedJoinNameData.dbId === record.dbId) return true;
        } else {
            // for table selection
            if (selectedJoinNameData.value === record.tableId && selectedJoinNameData.dbId === record.dbId) return true;
        }
        return false;
    }

    const columns = [
        {
            title: 'S.No',
            dataIndex: 'index',
            width: 50,
        },
        {
            title: 'Left Column',
            dataIndex: 'leftColumn',
            className: classnames('drag-visible'),
            render: (text, record) => {
                if (!record.left.table) {
                    return <DropAssist
                        record={record}
                        handleColumnDrop={handleColumnDrop}
                        join={record}
                        tables={tables}
                        type='left'
                    >
                        <AutoComplete
                            value={autoCompleteValue['left'][record.key] || ''}
                            options={options}
                            onSelect={(data, currentOption) => onSelect({ record, data, type: 'left', currentOption })}
                            onSearch={(searchText) => getAutoCompleteOptions({ record, searchText, type: 'left' })}
                            className='width100pcnt'
                            onChange={data => onChange({ record, data, type: 'left' })}
                            placeholder="select left column"
                            filterOption
                            onFocus={e => { getAutoCompleteOptions({ record, searchText: e?.target?.value || '', type: 'left' }) }}
                        />
                    </DropAssist >
                }
                const toolTipText = getDataSourceName(record.left, text);
                const validColumn = validateTableColumn(record, true);
                return <Dropdown
                    overlay={columnOptions({ record, type: 'left' })}
                    key={record.key}
                    trigger={['click', 'contextMenu']}
                    className={classnames({
                        'invalid-join-background': validColumn
                    })}
                >
                    <div className={`join-cell-wrap ${(!validColumn && checkJoinSelection(record.left, record.leftColumn)) ? 'slctn-bg-clr' : ''}`}>
                        <Row style={{ minWidth: '87%' }}>
                            <Col span={validColumn ? 22 : 24} className='hi-metadata-join-section-name'>
                                <Row>
                                    <Col span={22}>
                                        <Popover content={toolTipText} mouseEnterDelay={0.6} overlayClassName='hi-metadata-section-table-popover-overlay'>
                                            <Text ellipsis>{text}</Text>
                                        </Popover>
                                    </Col>
                                    <Col span={2}>
                                        <CaretDownOutlined />
                                    </Col>
                                </Row>
                            </Col>
                            {
                                validColumn && <Col span={2}>
                                    <Tooltip title="The following table is required by the join is not included in the metadata panel, Please include it or discard this join.">
                                        <QuestionCircleOutlined />
                                    </Tooltip>
                                </Col>
                            }
                        </Row>
                    </div>
                </Dropdown>
            }
        },
        {
            title: 'Join',
            dataIndex: 'type',
            width: 60,
            render: (text, record) => {
                return <Dropdown
                    overlay={typeOptions({ record, type: 'type' })}
                    key={record.key}
                    destroyPopupOnHide={true}
                    trigger={['click', 'contextMenu']}
                >
                    <Image
                        preview={false}
                        src={`images/hi-metadata/Joins_images/${text[0].toUpperCase() + text.slice(1)}.png`}
                    />
                </Dropdown>
            }
        },
        {
            title: 'Right Column',
            dataIndex: 'rightColumn',
            render: (text, record) => {
                if (!record.right.table) {
                    return <DropAssist
                        record={record}
                        handleColumnDrop={handleColumnDrop}
                        join={record}
                        tables={tables}
                        type='right'
                    >
                        <AutoComplete
                            value={autoCompleteValue['right'][record.key] || ''}
                            options={options}
                            onSelect={(data, currentOption) => onSelect({ record, data, type: 'right', currentOption })}
                            onSearch={(searchText) => getAutoCompleteOptions({ record, searchText, type: 'right' })}
                            filterOption
                            className='width100pcnt'
                            onChange={data => onChange({ record, data, type: 'right' })}
                            placeholder="select right column"
                            onFocus={e => { getAutoCompleteOptions({ record, searchText: e?.target?.value || '', type: 'right' }) }}
                        />
                    </DropAssist>
                }
                const toolTipText = getDataSourceName(record.right, text);
                const validColumn = validateTableColumn(record, false);
                return <Dropdown
                    overlay={columnOptions({ record, type: 'right' })}
                    key={record.key}
                    trigger={['click', 'contextMenu']}
                    className={classnames({
                        'invalid-join-background': validColumn
                    })}
                >
                    <div className={`join-cell-wrap ${(!validColumn && checkJoinSelection(record.right, record.rightColumn)) ? 'slctn-bg-clr' : ''}`}>
                        <Row style={{ minWidth: '87%' }}>
                            <Col span={validColumn ? 22 : 24} className='hi-metadata-join-section-name'>
                                <Row>
                                    <Col span={22}>
                                        <Popover content={toolTipText} overlayClassName='hi-metadata-section-table-popover-overlay' mouseEnterDelay={0.6}>
                                            <Text ellipsis>{text}</Text>
                                        </Popover>
                                    </Col>
                                    <Col span={2}>
                                        <CaretDownOutlined />
                                    </Col>
                                </Row>
                            </Col>
                            {
                                validColumn && <Col span={2}>
                                    <Tooltip title="The following table is required by the join is not included in the metadata panel, Please include it or discard this join.">
                                        <QuestionCircleOutlined />
                                    </Tooltip>
                                </Col>
                            }
                        </Row>
                    </div>
                </Dropdown>
            },
        },
        {
            title: 'Action',
            dataIndex: '',
            key: 'x',
            width: 60,
            render: (text, record) => <Popconfirm
                title={<PopconfirmBody intent="delete" description={"Are you sure want to delete the selected join(s)? You cannot undo this action."} />}

                onConfirm={() => handleJoinActions({ action: 'discardJoin', record, dispatch, joins })}
                onCancel={() => { }}
                okText="Ok"
                cancelText="Cancel"
                placement='left'
                overlayClassName={'hi-antd-popconfirm-min-width-300'}
            >
                {/* 
                /**
                 * TODO
                 * Test cases are pending
                 * based on discussion with Nitin parking this as it is taking more time than expected
                 
                */}
                <DeleteOutlined className={`'metadata-join-delete-icon '${record.uuid}`} data-testid={`metadata-join-delete-icon-${record.uuid}`} />
            </Popconfirm>
        },
    ];

    function getJoinDataFromTables(joinItem) {
        try {
            const { tableId, column, dbId } = joinItem;
            let joinTable = Object.values(tables).find(table => (table.cacheId === tableId || table.id === tableId) && table.connId === dbId);
            if (!joinTable) {
                return {
                    // joinName: `${joinTable.alias}.${column}`,
                    isValid: false
                }
            }
            if (joinTable && !joinTable?.columnsFetched) {
                // return `${joinTable.alias}.${column}`;
                return {
                    joinName: `${joinTable.alias}.${column}`,
                    isValid: true
                }
            }
            let joinTableColumn = Object.values(joinTable.columns).find(col => col.name === column);
            if (!joinTableColumn) {
                return {
                    joinName: `${joinTable.alias}.${column}`,
                    isValid: false
                }
            }
            // return `${joinTable.alias}.${joinTableColumn.alias}`;
            return {
                joinName: `${joinTable.alias}.${joinTableColumn.alias}`,
                isValid: true
            }

        } catch (error) {
            console.log("Error in joins/index.jsx file", error)
        }
    }

    const prepareJoinsToRender = () => {
        let result = joins.map((join) => {
            if (join.action === 'delete') return false;
            let leftJoinAlias, rightJoinAlias;
            if (join.left?.table && join.left?.column) {
                leftJoinAlias = getJoinDataFromTables(join.left);
            }
            if (join.right?.table && join.right?.column) {
                rightJoinAlias = getJoinDataFromTables(join.right)
            }
            return {
                ...join,
                leftColumn: leftJoinAlias?.joinName ?? `${join.left.table}.${join.left.column}`,
                rightColumn: rightJoinAlias?.joinName ?? `${join.right.table}.${join.right.column}`,
                left: { ...join.left, isValid: leftJoinAlias?.isValid },
                right: { ...join.right, isValid: rightJoinAlias?.isValid },

                key: join.uuid
            }


        }).filter(Boolean)
        setData(result)
    }

    const updateData = (data) => {
        setData(data)
        dispatch(metadataActions.updateJoinsData({ data: data, override: true }))
    }
    const updateJoinsToRedux = data => {
        dispatch(metadataActions.updateJoinsData({ data: data, override: true }))
    }

    const onSelectionChange = () => {
    }

    const deleteMenu = <Menu
        onClick={({ key }) => {
            handleJoinActions({ action: key, record: [], type: '', dispatch, joins, listOfTableNames, selectedJoins, listOfTableColumnNames: listOfTableNames, tables })
        }}
    >
        <Menu.Item key='discardSelectedJoins' icon={<DeleteOutlined />}>
            <a>
                Delete selected joins
            </a>
        </Menu.Item>
        <Menu.Item key='deleteInvalid' icon={<DeleteOutlined />}>
            <a>
                Delete invalid joins
            </a>
        </Menu.Item>
    </Menu>
    return (
        <Row data-testid='metadata-joins-section' ref={joinsRef}>
            <Col span={24}>
                <Row justify="end">
                    <Col span={3} className='text-align-end'>
                        <Row>
                            <Col span={8}>
                                <Tooltip mouseEnterDelay={constants.mouseEnterDelay} title='Add new join'>
                                    <PlusOutlined ref={buttonAddRef} onClick={() => {
                                        handleJoinActions({ action: 'addNewJoin', dispatch, joins })
                                    }} />
                                </Tooltip>
                            </Col>
                            <Col span={8}>
                                <Dropdown overlay={deleteMenu} placement="bottomLeft">
                                    <DeleteOutlined />
                                </Dropdown>
                            </Col>
                            <Col span={8}>
                                <Tooltip title={JoinsPopover} placement="leftTop">
                                    <InfoCircleOutlined />
                                </Tooltip>
                            </Col>
                        </Row>
                    </Col>
                </Row>
            </Col>
            <Col span={24} className='hi-pt10'>
                <SortableTable
                    dataSource={data}
                    columns={columns}
                    calculatedHeight={joinsRef?.current?.parentElement?.clientHeight ? joinsRef?.current?.parentElement?.clientHeight - 70 : false}
                    onSortEnd={(data) => { updateData(data) }}
                    onSelectionChange={onSelectionChange}
                    updateJoinsToRedux={updateJoinsToRedux}
                    setSelectedJoins={setSelectedJoins}
                    loading={joinsLoadingStatus}
                    joins={joins}
                />
            </Col>
        </Row>
    )
}

export default Joins;

const DropAssist = ({ join, children, handleColumnDrop, type, tables }) => {
    const [, drop] = useDrop(() => ({
        accept: "metadataColumn",
        drop: record => {
            handleColumnDrop({ column: record, type, join, currentOption: { tableId: record.tableId, dbId: record.dbId ?? record.connId, columnId: record.id, columnName: record.name, tableName: record.tableKey, } })
        }
    }), [tables])
    return <div ref={drop}>{children}</div>
}