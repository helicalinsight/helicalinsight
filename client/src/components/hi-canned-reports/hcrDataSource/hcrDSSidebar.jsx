import {
    CheckOutlined,
    DeleteOutlined,
    EditOutlined,
    InfoCircleOutlined,
    InfoOutlined,
    MoreOutlined,
    PlusOutlined,
} from '@ant-design/icons';
import { Collapse, Divider, Dropdown, Menu, Space, Tooltip, Typography } from 'antd';
import { isEmpty } from 'lodash';
import { useEffect, useRef } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { hcrActions } from '../../../redux/actions';
import notify from '../../hi-notifications/notify';
import '../cannedReports.scss';
import { hcrDSParameter, hcrDSQuery } from '../hcr-constants';
import { getActiveSubDSParameterType } from '../hcrCanvas/advanceComponents/utils';
const { Paragraph } = Typography;

export default function HcrDSSidebar() {
    const activeTab = useSelector(
        (state) =>
            state.cannedReports.present.hcrTabData.panes.find(
                (pane) =>
                    pane.key ===
                    state.cannedReports.present.hcrTabData.activeKey
            ) || {}
    );
    const activeKey = useSelector(
        (state) => state.cannedReports.present.hcrTabData.activeKey
    );
    const { dsPaneTypes, selectedDS, subDataSets = [] } = activeTab;
    const dispatch = useDispatch();
    const Notify = notify(dispatch);
    const inputRef = useRef();
    const parameters = useSelector(
        (state) =>
            state.cannedReports.present.hCROldConfigurations?.HCR?.HCR
                ?.designerProperties?.parameters
    );

    const dsPaneActions = ({ record, item, subDS = null }) => {
        const actionMenu = [
            {
                label: 'Edit',
                key: 'edit',
                icon: <EditOutlined />,
                onClick: ({ domEvent: e }) => {
                    e.stopPropagation();
                    dispatch(
                        hcrActions.handleEditingDsPaneItem({
                            dataSourcePane: record.dataSourcePane,
                            itemId: item.id,
                            key: 'isNameEditable',
                            value: true,
                        })
                    );
                },
            },
            {
                label: 'Delete',
                key: 'delete',
                icon: <DeleteOutlined />,
                onClick: ({ domEvent: e }) => {
                    e.stopPropagation();
                    dispatch(
                        hcrActions.handleDeletingDsPaneItem({
                            dataSourcePane: record.dataSourcePane,
                            itemId: item.id,
                        })
                    );
                },
            },
            (subDS && {
                label: 'Add Parameter',
                key: 'add_parameter',
                icon: <PlusOutlined />,
                onClick: ({ domEvent: e }) => {
                    e.stopPropagation();
                    const { id: subDSId } = subDS || {}
                    const payload = {
                        actionType: "addNewParameter",
                        id: subDSId,
                    }
                    dispatch(hcrActions.hcrUpdateSubdataSets(payload))
                },
            })
        ];

        if (record.dataSourcePane === hcrDSParameter) {
            actionMenu.push({
                key: 'type',
                label: 'Type',
                children: Object.entries(parameters?.classNames || {}).map(
                    (paraType) => ({
                        key: paraType[1],
                        label: paraType[0],
                        onClick: () => {
                            dispatch(
                                hcrActions.handleEditingDsPaneItem({
                                    dataSourcePane: record.dataSourcePane,
                                    itemId: item.id,
                                    key: 'type',
                                    value: paraType[1],
                                })
                            );
                        },
                    })
                ),
            });
        }

        return (
            <Menu
                selectedKeys={[item.type]}
                selectable={true}
                items={actionMenu}
            />
        );
    };

    const dsPaneParametersActions = ({ subDS = {}, parameter = {}, parameters: subDSParameters = [] }) => {
        const { id: paramId, type } = parameter || {}
        let active = getActiveSubDSParameterType(type)

        const { id: subDSId } = subDS || {}
        const actionMenu = [
            {
                label: 'Edit',
                key: 'edit',
                icon: <EditOutlined />,
                onClick: ({ domEvent: e }) => {
                    e.stopPropagation();
                    const payload = {
                        actionType: "updateParameters",
                        id: subDSId,
                        parameters: subDSParameters.map((param) => {
                            if (param.id === paramId) {
                                return {
                                    ...param,
                                    isNameEditable: true
                                }
                            }
                            return param
                        })
                    }
                    dispatch(hcrActions.hcrUpdateSubdataSets(payload))
                },
            },
            {
                label: 'Delete',
                key: 'delete',
                icon: <DeleteOutlined />,
                onClick: ({ domEvent: e }) => {
                    e.stopPropagation();
                    const payload = {
                        actionType: "updateParameters",
                        id: subDSId,
                        parameters: subDSParameters.filter((param) => param.id !== paramId)
                    }
                    dispatch(hcrActions.hcrUpdateSubdataSets(payload))
                },
            },
            {
                key: 'type',
                label: 'Type',
                children: Object.entries(parameters?.classNames || {}).map(
                    (paraType) => ({
                        key: paraType[1],
                        label: paraType[0],
                        onClick: () => {
                            const payload = {
                                actionType: "updateParameters",
                                id: subDSId,
                                parameters: subDSParameters.map((param) => {
                                    if (param.id === paramId) {
                                        return {
                                            ...param,
                                            type: paraType[1],
                                            canvasValues: {
                                                ...param.canvasValues,
                                                open: paraType === "Numeric" ? "" : "'",
                                                close: paraType === "Numeric" ? "" : "'"
                                            }
                                        }
                                    }
                                    return param
                                })
                            }
                            dispatch(hcrActions.hcrUpdateSubdataSets(payload))
                        },
                    })
                ),
            }
        ];

        return (
            <Menu
                selectedKeys={[active]}
                selectable={true}
                items={actionMenu}
            />
        );
    }

    const checkIsSubDS = (paneType, ds) => {
        if (paneType !== hcrDSQuery) return false;
        if (paneType === hcrDSQuery) {
            const { id } = ds || {}
            return subDataSets.find((subDS) => subDS.id === id)
        }
        return false;
    }

    const renderSubDS = (ds = {}, ele) => {
        const { id, name = "" } = ds || {}
        const subDS = subDataSets?.find((subDS) => subDS.id === id) || {}
        const { parameters = [] } = subDS || {}

        function renderName(name, info) {
            return (
                <Paragraph Paragraph className="ds-item-title" ellipsis>
                    {name}
                    {info && <Tooltip title={"Sub Dataset"}><InfoCircleOutlined style={{ fontSize: 11, borderRadius: 4, marginLeft: 8, cursor: "pointer" }} /></Tooltip>}
                </Paragraph>
            )
        }

        function renderHeader(name, overlay, tag) {
            return (
                <div className="ds-pane-collapse-panel" style={{ width: "100%" }}>
                    {renderName(name, tag)}
                    <Dropdown
                        trigger={['click']}
                        overlay={overlay}
                    >
                        <MoreOutlined
                            onClick={(e) => e.stopPropagation()}
                        />
                    </Dropdown>
                </div>
            )
        }

        function renderParameter(parameter, subDS) {
            const { name = "", id: parameterId, isNameEditable } = parameter || {}
            const { id: subDSId } = subDS || {}
            if (isNameEditable) {
                return (
                    <div className="side-input-over">
                        <input
                            className="ds-item-title side-tick-icon"
                            id={`ds-parameter-item-title-${parameterId}`}
                            defaultValue={name}
                            onKeyDown={(e) => {
                                if (
                                    (e.ctrlKey || e.metaKey) &&
                                    (e.key === 'z' || e.key === 'y')
                                ) {
                                    e.stopPropagation();
                                } else if (e.key === 'Enter') {
                                    const v = e.target.value.trim();
                                    if (!v) return;
                                    const payload = {
                                        actionType: "updateParameters",
                                        id: subDSId,
                                        parameters: parameters.map((param) => {
                                            if (param.id === parameterId) {
                                                return {
                                                    ...param,
                                                    name: v,
                                                }
                                            }
                                            return param
                                        })
                                    }
                                    dispatch(hcrActions.hcrUpdateSubdataSets(payload))
                                } else if (e.key === 'Escape') {
                                    const payload = {
                                        actionType: "updateParameters",
                                        id: subDSId,
                                        parameters: parameters.map((param) => {
                                            if (param.id === parameterId) {
                                                return {
                                                    ...param,
                                                    isNameEditable: false
                                                }
                                            }
                                            return param;
                                        })
                                    }
                                    dispatch(hcrActions.hcrUpdateSubdataSets(payload))
                                }
                            }}
                        />
                        <CheckOutlined
                            className="side-tick-check"
                            onClick={() => {
                                let inputEl = document.getElementById(`ds-parameter-item-title-${parameterId}`);
                                let v = inputEl?.value?.trim();
                                if (!v) return;
                                const payload = {
                                    actionType: "updateParameters",
                                    id: subDSId,
                                    parameters: parameters.map((param) => {
                                        if (param.id === parameterId) {
                                            return {
                                                ...param,
                                                name: v,
                                                isNameEditable: false,
                                            }
                                        }
                                        return param;
                                    })
                                }
                                dispatch(hcrActions.hcrUpdateSubdataSets(payload))
                            }}
                        />
                    </div>
                )
            }
            return (
                <div>
                    {renderHeader(name, dsPaneParametersActions({ subDS, parameter, parameters }))}
                    <Divider className="group-divider" />
                </div>
            )
        }


        if (!subDS || !parameters.length) {
            return renderHeader(name, dsPaneActions({ record: ele, item: ds, subDS: !isEmpty(subDS) ? subDS : null }), true)
        }

        return (
            <Collapse
                size="small"
                className="ds-pane-parameters-collapse"
                ghost
                style={{ width: "100%" }}
            >
                <Collapse.Panel
                    header={renderHeader(name, dsPaneActions({ record: ele, item: ds, subDS }), true)}
                    key={"hcr-sub-ds-panel" + id}
                >
                    {parameters.map((parameter) => renderParameter(parameter, subDS))}
                </Collapse.Panel>
            </Collapse>
        )

    }

    useEffect(() => {
        if (!selectedDS && activeKey) {
            const quiries = dsPaneTypes[0].menu;
            const parameters = dsPaneTypes[1].menu;
            if (quiries.length) {
                dispatch(
                    hcrActions.hcrSetSelectedDS({
                        dataSourcePane: dsPaneTypes[0].dataSourcePane,
                        id: quiries[0].id,
                    })
                );
            } else if (parameters.length) {
                dispatch(
                    hcrActions.hcrSetSelectedDS({
                        dataSourcePane: dsPaneTypes[1].dataSourcePane,
                        id: parameters[0].id,
                    })
                );
            } else {
                dispatch(
                    hcrActions.handleAddingDsPaneItem(
                        dsPaneTypes[0].dataSourcePane,
                        false
                    )
                );
                dispatch(
                    hcrActions.hcrSetSelectedDS({
                        dataSourcePane: dsPaneTypes[0].dataSourcePane,
                        id: 1,
                    })
                );
            }
        }
    }, [activeKey]);

    return dsPaneTypes.map((ele) => (
        <Collapse
            size="small"
            className="ds-pane-collapse"
            defaultActiveKey={['1']}
        >
            <Collapse.Panel
                header={
                    <div className="ds-pane-collapse-panel">
                        <h4 className="ds-pane-title">{ele.dataSourcePane}</h4>
                        <PlusOutlined
                            onClick={(e) => {
                                dispatch(
                                    hcrActions.handleAddingDsPaneItem(
                                        ele.dataSourcePane
                                    )
                                );
                                e.stopPropagation();
                            }}
                        />
                    </div>
                }
                key={ele.key}
            >
                <Space
                    className="ds-item-space"
                    size="middle"
                    direction="vertical"
                >
                    {ele.menu.map((item) => (
                        <div
                            key={`${ele.dataSourcePane}-${item.id}-${item.name}`}
                            className={`ds-pane-actions ${selectedDS?.id === item.id &&
                                selectedDS?.dataSourcePane ===
                                ele.dataSourcePane
                                ? 'highlight-ds-item'
                                : ''
                                }`}
                            onClick={() => {
                                dispatch(
                                    hcrActions.hcrSetSelectedDS({
                                        dataSourcePane: ele.dataSourcePane,
                                        id: item.id,
                                    })
                                );
                            }}
                        >
                            {item.isNameEditable ? (
                                <div className="side-input-over">
                                    <input
                                        ref={inputRef}
                                        className="ds-item-title side-tick-icon"
                                        id={`ds-item-title-${item.id}`}
                                        defaultValue={item.name}
                                        // autoFocus
                                        onKeyDown={(e) => {
                                            if (
                                                (e.ctrlKey || e.metaKey) &&
                                                (e.key === 'z' || e.key === 'y')
                                            ) {
                                                e.stopPropagation();
                                            } else if (e.key === 'Enter') {
                                                const v = e.target.value.trim();
                                                if (!v) return;
                                                dispatch(
                                                    hcrActions.handleEditingDsPaneItem(
                                                        {
                                                            dataSourcePane:
                                                                ele.dataSourcePane,
                                                            itemId: item.id,
                                                            key: 'name',
                                                            value: v,
                                                        },
                                                        true
                                                    )
                                                );
                                                dispatch(
                                                    hcrActions.handleEditingDsPaneItem(
                                                        {
                                                            dataSourcePane:
                                                                ele.dataSourcePane,
                                                            itemId: item.id,
                                                            key: 'isNameEditable',
                                                            value: false,
                                                        }
                                                    )
                                                );
                                            } else if (e.key === 'Escape') {
                                                dispatch(
                                                    hcrActions.handleEditingDsPaneItem(
                                                        {
                                                            dataSourcePane:
                                                                ele.dataSourcePane,
                                                            itemId: item.id,
                                                            key: 'isNameEditable',
                                                            value: false,
                                                        }
                                                    )
                                                );
                                            }
                                        }}
                                    />
                                    <CheckOutlined
                                        className="side-tick-check"
                                        onClick={() => {
                                            // const v =
                                            //     inputRef.current?.value?.trim();
                                            let inputEl = document.getElementById(`ds-item-title-${item.id}`);
                                            let v = inputEl?.value?.trim();
                                            if (!v) return;
                                            dispatch(
                                                hcrActions.handleEditingDsPaneItem(
                                                    {
                                                        dataSourcePane:
                                                            ele.dataSourcePane,
                                                        itemId: item.id,
                                                        key: 'name',
                                                        value: v,
                                                    },
                                                    true
                                                )
                                            );
                                            dispatch(
                                                hcrActions.handleEditingDsPaneItem(
                                                    {
                                                        dataSourcePane:
                                                            ele.dataSourcePane,
                                                        itemId: item.id,
                                                        key: 'isNameEditable',
                                                        value: false,
                                                    }
                                                )
                                            );
                                        }}
                                    />
                                </div>
                            ) : (
                                checkIsSubDS(ele.dataSourcePane, item) ? renderSubDS(item, ele) : (
                                    <Paragraph Paragraph className="ds-item-title" ellipsis>
                                        {item.name}
                                    </Paragraph>
                                )

                            )}

                            {!checkIsSubDS(ele.dataSourcePane, item) &&
                                (<Dropdown
                                    trigger={['click']}
                                    overlay={dsPaneActions({ record: ele, item })}
                                >
                                    <MoreOutlined
                                        onClick={(e) => e.stopPropagation()}
                                    />
                                </Dropdown>)
                            }
                        </div>
                    ))
                    }
                </Space >
            </Collapse.Panel >
        </Collapse >
    ));
}
