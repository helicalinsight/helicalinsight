import { Button, Checkbox, Input, Space, Switch } from "antd";
import { isEmpty } from "lodash";
import { useEffect } from "react";
import { useSelector } from "react-redux";
import useHCRCascadeSelector from "../../../../hooks/useHCRCascadeSelector";
import { hcrActions } from "../../../../redux/actions";
import notify from "../../../hi-notifications/notify";
import FieldSelector from "../fieldSelector";


export default function CanvasGroupProperties(props = {}) {
    const {
        SelectField,
        dispatch,
        InputNumberFiled,
        getLabel,
        fromAdvanceComp = false,
        fields = [],
        calculations = [],
        groupOptions = [],
        selectedGroup = null,
        handleChange = () => { },
        handleDelete = () => { },
    } = props || {};
    const activeTab = useSelector(state => state.cannedReports.present.hcrTabData.panes.find(pane => pane.key === state.cannedReports.present.hcrTabData.activeKey)) || {};
    const { getCascaderOptions } = useHCRCascadeSelector({ node: {}, fields, calculations })
    const cascaderOptions = getCascaderOptions()
    const { canvasProperties } = activeTab;
    const groupProperties = canvasProperties?.groupProperties;
    const HcrPropertiesConfiguration = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HcrPropertiesConfiguration || {});
    const { tooltipInfo } = HcrPropertiesConfiguration;
    const {
        tooltip_minHeightToStartNewPage,
        tooltip_minDetailsToStartFromTop,
        tooltip_reprintHeaderOnEachPage,
        tooltip_keepTogether,
        tooltip_startNewColumn,
        tooltip_startNewPage,
        tooltip_resetPageNumber,
        tooltip_preventOrphanFooter,
        tooltip_groupPropertiesEditGroup,
        tooltip_groupPropertiesGroupExpression,
        tooltip_groupPropertiesGroupId,
        tooltip_groupPropertiesGroupName,
        tooltip_groupPropertiesSelectGroup
    } = tooltipInfo;
    let { options, selectGroup, keyValuePairs = {} } = groupProperties || {};

    if (fromAdvanceComp) {
        options = groupOptions;
        selectGroup = selectedGroup;
    }

    let reqOpt = options?.find(ele => ele.id === selectGroup) || {};
    if (!isEmpty(keyValuePairs)) reqOpt = keyValuePairs;
    const Notify = notify(dispatch);

    const inputNumTypes = [{
        label: 'Minimum height to start new page',
        key: 'minHeightToStartNewPage',
        tooltip: tooltip_minHeightToStartNewPage
    }, {
        label: 'Minimum records to start from top',
        key: 'minRecordsToStartFromTop',
        tooltip: tooltip_minDetailsToStartFromTop
    }];

    const checkOptions = [
        {
            label: 'Reprint header on each page',
            key: 'reprintHeaderOnEachPage',
            tooltip: tooltip_reprintHeaderOnEachPage
        },
        {
            label: 'Keep together',
            key: 'keepTogether',
            tooltip: tooltip_keepTogether
        },
        {
            label: 'Start new column',
            key: 'startNewColumn',
            tooltip: tooltip_startNewColumn
        },
        {
            label: 'Start new page',
            key: 'startNewPage',
            tooltip: tooltip_startNewPage
        },
        {
            label: 'Reset page number',
            key: 'resetPageNumber',
            tooltip: tooltip_resetPageNumber
        },
        {
            label: 'Prevent orphan footer',
            key: 'preventOrphanFooter',
            tooltip: tooltip_preventOrphanFooter
        },
    ];

    const onChange = (isChecked, key) => {
        if (fromAdvanceComp) {
            handleChange({ key, value: isChecked })
            return;
        }
        dispatch(hcrActions.setHcrCanvasGroupProperties({ key, value: isChecked, type: "update" }))
    };

    const handlePropertyChange = (key, value) => {
        if (fromAdvanceComp) {
            handleChange({ key, value })
            return;
        }
        dispatch(hcrActions.setHcrCanvasGroupProperties({ key, value, type: "update" }))
    }

    const defaultGroupProps = {
        minHeightToStartNewPage: reqOpt.minHeightToStartNewPage || 0,
        minRecordsToStartFromTop: reqOpt.minRecordsToStartFromTop || 0,
        reprintHeaderOnEachPage: reqOpt.reprintHeaderOnEachPage || false,
        keepTogether: reqOpt.keepTogether || false,
        startNewColumn: reqOpt.startNewColumn || false,
        startNewPage: reqOpt.startNewPage || false,
        resetPageNumber: reqOpt.resetPageNumber || false,
        preventOrphanFooter: reqOpt.preventOrphanFooter || false,
        ...reqOpt
    };

    function onDelete() {
        if (fromAdvanceComp) {
            handleDelete();
            return;
        }
        dispatch(hcrActions.deleteGroup());
        Notify.success({ message: 'Group deleted successfully.' });
    }

    function onSave() {
        dispatch(hcrActions.setHcrCanvasGroupProperties({ type: "save" }))
        Notify.success({ message: "Group saved successfully" });
    }

    function onClear() {
        dispatch(hcrActions.setHcrCanvasGroupProperties({ type: "clear" }))
    }

    useEffect(() => {
        return () => {
            if (!fromAdvanceComp) {
                dispatch(hcrActions.setHcrCanvasGroupProperties({ type: "clear" }))
            }
        }
    }, [])

    return <>
        <div className="common-group">
            {!fromAdvanceComp && <SelectField
                label={<div className="property-label">{getLabel({ label: "Select Group", tooltip: tooltip_groupPropertiesSelectGroup })}</div>}
                value={reqOpt.name}
                options={(options || []).map(item => {
                    return {
                        label: item.name,
                        value: item.id
                    }
                })}
                width={119}
                onChange={(value) => {
                    handlePropertyChange('selectGroup', value)
                }}
            />}
            {!fromAdvanceComp && <Space style={{ width: 150, alignItems: 'flex-end' }}>
                <div className="parameter-label">{getLabel({ label: "Edit", tooltip: tooltip_groupPropertiesEditGroup })}</div>
                <Switch disabled={!selectGroup} checked={defaultGroupProps.isChecked} onChange={(value) => {
                    handlePropertyChange('isChecked', value)
                }} />
            </Space>}
            {
                [
                    { key: 'id', disable: true, label: 'Id', tooltip: tooltip_groupPropertiesGroupId },
                    { key: 'name', label: 'Name', disable: !defaultGroupProps.isChecked, tooltip: tooltip_groupPropertiesGroupName, placement: "left" },
                    { key: 'expression', label: 'Expression', disable: !defaultGroupProps.isChecked, tooltip: tooltip_groupPropertiesGroupExpression, expressionEditor: true }
                ].map(item => {
                    return <div>
                        <div className="property-label">{getLabel({ label: item.label, tooltip: item.tooltip, placement: item.placement || "topLeft" })}</div>
                        {
                            item.expressionEditor ?
                                <FieldSelector
                                    onChange={(valueObj = {}) => {
                                        const { value } = valueObj || {}
                                        let isNameUnique = true;
                                        if (item.key === 'name') {
                                            if (options.find(opt => (opt.name === value) && (opt.id !== selectGroup))) {
                                                isNameUnique = false;
                                            }
                                        }
                                        if (isNameUnique) {
                                            handlePropertyChange(item.key, value)
                                        } else {
                                            Notify.warning({ message: 'Group name is already exist. Please provide a different name.' });
                                        }
                                    }}
                                    value={defaultGroupProps[item.key] ? (item.key === 'id' ? `gp${defaultGroupProps[item.key]}` : defaultGroupProps[item.key]) : ''}
                                    options={cascaderOptions}
                                    disabled={item.disable}
                                    styles={{ width: 240 }}
                                />
                                :
                                <Input
                                    disabled={item.disable}
                                    type="text"
                                    value={defaultGroupProps[item.key] ? (item.key === 'id' ? `gp${defaultGroupProps[item.key]}` : defaultGroupProps[item.key]) : ''}
                                    style={{ width: 120 }}
                                    onChange={(e) => {
                                        let isNameUnique = true;
                                        if (item.key === 'name') {
                                            if (options.find(opt => (opt.name === e.target.value) && (opt.id !== selectGroup))) {
                                                isNameUnique = false;
                                            }
                                        }
                                        if (isNameUnique) {
                                            handlePropertyChange(item.key, e.target.value)
                                        } else {
                                            Notify.warning({ message: 'Group name is already exist. Please provide a different name.' });
                                        }
                                    }} />
                        }
                    </div>
                })
            }
            {inputNumTypes.map(ele => {
                return <div>
                    <div className="property-label">{getLabel({ label: ele.label, tooltip: ele.tooltip })}</div>
                    <Input
                        style={{
                            width: 120,
                        }}
                        disabled={!selectGroup}
                        value={defaultGroupProps[ele.key]}
                        onChange={(e) => {
                            handlePropertyChange(ele.key, e.target.value)
                        }}
                        placeholder="Input a number"
                        maxLength={16}
                    />
                </div>
                // <InputNumberFiled
                //     label={getLabel({ label: ele.label, tooltip: ele.tooltip })}
                //     value={defaultGroupProps[ele.key]}
                //     width={119}
                //     onChange={(value) => {
                //         dispatch(hcrActions.setHcrCanvasGroupProperties({ key: ele.key, value }))
                //     }}
                // />
            })}
        </div>
        {checkOptions.map(ele => {
            return <div>
                <Checkbox disabled={!selectGroup} style={{ marginLeft: 0 }} onChange={(e) => { onChange(e.target.checked, ele.key) }} checked={defaultGroupProps[ele.key]}>
                    {getLabel({ label: ele.label, tooltip: ele.tooltip })}
                </Checkbox>
            </div>
        })}
        <Space>
            {
                !fromAdvanceComp &&
                <>
                    <Button type="link" onClick={onClear}>Clear</Button>
                    <Button type="link" onClick={onSave}>Save</Button>
                </>
            }
            <Button disabled={!selectGroup} type="link" onClick={onDelete}>Delete</Button>
        </Space>
    </>
}