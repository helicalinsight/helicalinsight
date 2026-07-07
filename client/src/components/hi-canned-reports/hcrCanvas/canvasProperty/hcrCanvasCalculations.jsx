import { useSelector } from "react-redux";
import { hcrActions } from "../../../../redux/actions";
import { Button, Space } from "antd";
import notify from "../../../hi-notifications/notify";
import FieldSelector from "../fieldSelector";
import useHCRCascadeSelector from "../../../../hooks/useHCRCascadeSelector";
import { isEmpty } from "lodash";
import { getCategoryClassNames } from "../advanceComponents/utils";

export default function CanvasCalculations({
    SelectField,
    dispatch,
    InputFiled,
    classNames,
    calculationOptions,
    resetType,
    incrementType,
    getLabel,
    fromAdvanceComp = false,
    saveCB,
    deleteCB,
    editValues = {},
    groupOptions = [],
    fields = [],
    calculationsFromAdvanceComp = []
}) {
    const activeTab = useSelector(state => state.cannedReports.present.hcrTabData.panes.find(pane => pane.key === state.cannedReports.present.hcrTabData.activeKey)) || {};
    const { getCascaderOptions } = useHCRCascadeSelector({ node: {}, fields, calculations: calculationsFromAdvanceComp })
    const cascaderOptions = getCascaderOptions()
    const { canvasProperties } = activeTab;
    const { calculations, groupProperties } = canvasProperties || {};
    let { selectCalculation, options, keyValuePairs } = calculations;
    const HcrPropertiesConfiguration = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HcrPropertiesConfiguration || {});
    const { tooltipInfo } = HcrPropertiesConfiguration;
    const { tooltip_Name, tooltip_varClassName, tooltip_varCalculation, tooltip_varResetType, tooltip_varResetGroup, tooltip_varIncrement, tooltip_varIncrementGroup, tooltip_varExpression, tooltip_varInitialValueExpression, tooltip_varIncrementFactoryClassName, tooltip_selectCalcutations } = tooltipInfo;
    const Notify = notify(dispatch);
    const categoryClassNames = getCategoryClassNames(classNames);

    const selectTypes = [{
        title: "Class Name",
        options: categoryClassNames,
        key: 'className',
        tooltip: tooltip_varClassName,
        isRequired: true,
        type: "expressionEditor"
    }, {
        title: "Calculation",
        options: calculationOptions,
        key: 'calculation',
        tooltip: tooltip_varCalculation,
        isRequired: true,
        placement: 'left'
    }, {
        title: "Reset Type",
        options: resetType,
        key: "resetType",
        tooltip: tooltip_varResetType,
        isRequired: true,
    }, {
        title: "Reset Group",
        options: fromAdvanceComp ? groupOptions : groupProperties.options,
        key: 'resetGroup',
        tooltip: tooltip_varResetGroup,
        placement: 'left'
    }, {
        title: "Increment",
        options: incrementType,
        key: 'increment',
        tooltip: tooltip_varIncrement
    }, {
        title: "increment Group",
        options: fromAdvanceComp ? groupOptions : groupProperties.options,
        key: 'incrementGroup',
        tooltip: tooltip_varIncrementGroup,
        placement: 'left'
    }];

    const inputTypes = [{
        label: "Expression",
        key: 'expression',
        tooltip: tooltip_varExpression,
        isRequired: true,
        expressionEditor: true
    }, {
        label: "Initial Value Expression",
        key: 'initialValueExp',
        tooltip: tooltip_varInitialValueExpression,
        expressionEditor: true
    }, {
        label: "Increment Factory ClassName",
        key: 'incrementFactoryClassName',
        tooltip: tooltip_varIncrementFactoryClassName
    }];

    function onSave() {
        if (keyValuePairs.name?.includes('"') || keyValuePairs.name?.includes("'")) {
            Notify.warning({ message: 'Variable name is not valid, Please rectify it' });
        } else if (keyValuePairs.name && keyValuePairs.className && keyValuePairs.calculation && keyValuePairs.resetType && keyValuePairs.expression) {
            if (typeof saveCB === 'function' && fromAdvanceComp) {
                saveCB(keyValuePairs);
                return;
            }
            dispatch(hcrActions.setHcrCanvasCalculations({ saveOption: true }));
            Notify.success({ message: 'Variable updated succesfully' });
        } else {
            Notify.warning({ message: 'Please fill up all the mandatory fields' });
        }
    }

    function onClear() {
        dispatch(hcrActions.setHcrCanvasCalculations({ clearKeyValuePairs: true }));
    }

    function onDelete() {
        if (typeof deleteCB === 'function' && fromAdvanceComp) {
            deleteCB(keyValuePairs.id);
            return;
        }
        dispatch(hcrActions.setHcrCanvasCalculations({ isDeleted: true }));

    }

    return <>
        {!fromAdvanceComp && <SelectField
            label={<div className={`property-label`}>{getLabel({ label: "Select Calculation", tooltip: tooltip_selectCalcutations })}</div>}
            value={selectCalculation}
            options={options.map(opt => {
                return {
                    label: opt.name,
                    value: opt.id
                }
            })}
            width={'100%'}
            onChange={(value) => {
                dispatch(hcrActions.setHcrCanvasCalculations({ key: 'selectCalculation', value }))
            }}
        />}
        <InputFiled
            label={getLabel({ label: 'Name', tooltip: tooltip_Name, isRequired: true })}
            value={keyValuePairs.name || ''}
            onChange={(value) => {
                dispatch(hcrActions.setHcrCanvasCalculations({ isKeyValuePair: true, key: 'name', value }))
            }}
        />
        {
            <div className="common-group">
                {
                    selectTypes.map(ele => {
                        if (ele.type === 'expressionEditor') {
                            return (
                                <>
                                    {getLabel({ label: ele.title, tooltip: ele.tooltip, isRequired: ele.isRequired, placement: ele.placement })}
                                    <FieldSelector
                                        onChange={(valueObj) => {
                                            const { value } = valueObj || {}
                                            dispatch(hcrActions.setHcrCanvasCalculations({ isKeyValuePair: true, key: ele.key, value }))
                                        }}
                                        value={keyValuePairs[ele.key] || ''}
                                        options={ele.options}
                                        appendValue={false}
                                    />
                                </>
                            )
                        }
                        return <SelectField
                            label={getLabel({ label: ele.title, tooltip: ele.tooltip, isRequired: ele.isRequired, placement: ele.placement })}
                            value={keyValuePairs[ele.key] || ''}
                            options={ele.options.map(opt => {
                                if (ele.key === 'className') {
                                    return {
                                        label: opt[0],
                                        value: opt[1]
                                    }
                                }
                                if (ele.key === 'incrementGroup' || ele.key === 'resetGroup') {
                                    return {
                                        label: opt.name,
                                        value: opt.name
                                    }
                                }
                                return {
                                    label: opt,
                                    value: opt
                                }
                            })}
                            width={119}
                            onChange={(value) => {
                                dispatch(hcrActions.setHcrCanvasCalculations({ isKeyValuePair: true, key: ele.key, value }))
                            }}
                        />
                    })
                }
            </div>
        }
        {inputTypes.map(ele => {
            if (ele.expressionEditor) {
                return (
                    <div>
                        {getLabel({ label: ele.label, tooltip: ele.tooltip, isRequired: ele.isRequired })}
                        <FieldSelector
                            onChange={(valueObj = {}) => {
                                const { value } = valueObj || {}
                                dispatch(hcrActions.setHcrCanvasCalculations({ isKeyValuePair: true, key: ele.key, value }));
                            }}
                            options={cascaderOptions}
                            value={keyValuePairs[ele.key] || ''}
                        />
                    </div>
                )
            }
            return <InputFiled
                label={getLabel({ label: ele.label, tooltip: ele.tooltip, isRequired: ele.isRequired })}
                value={keyValuePairs[ele.key] || ''}
                onChange={(value) => {
                    dispatch(hcrActions.setHcrCanvasCalculations({ isKeyValuePair: true, key: ele.key, value }));
                }}
            />
        })}
        <Space>
            <Button type="link" onClick={onClear}>Clear</Button>
            <Button type="link" onClick={onSave}>Save</Button>
            <Button disabled={fromAdvanceComp ? isEmpty(editValues) : !selectCalculation} type="link" onClick={onDelete}>Delete</Button>
        </Space>
    </>
}