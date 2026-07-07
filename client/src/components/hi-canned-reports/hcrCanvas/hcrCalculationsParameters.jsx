import { Dropdown, Menu, Space, Tooltip } from "antd";
import { DownOutlined } from "@ant-design/icons";
import { useDrag } from "react-dnd";
import { useSelector } from "react-redux";
import { useEffect, useState } from "react";
import { hcrDSParameter } from "../hcr-constants";
import { getCalcParamsNode } from "../hcrHelperMethods";
import {tooltipDescriptions} from "../hcr-constants"

const CalculationParametersItem = ({ variable }) => {
    const canvasMargin = useSelector(state =>
        state.cannedReports.present.hcrTabData.panes.find(
            pane => pane.key === state.cannedReports.present.hcrTabData.activeKey
        )?.canvasProperties?.margin || {}
    );

    const [{ }, drag] = useDrag(() => ({
        type: "customNodes",
        item: { record: variable, canvasMargin, type:"customNodes" },
        collect: monitor => ({
            opacity: monitor.isDragging() ? 0.5 : 1,
        }),
    }), [canvasMargin]);

    const description = tooltipDescriptions[variable.name] || "No description available.";

    return (
        <div ref={drag} style={{ fontSize: 12 }}>
            <Tooltip title={description} >
                <span>{variable.name}</span>
            </Tooltip>
        </div>
    );
};


const getCalculationsParamsMenu = ({ modifiedBuiltVariables }) => {
    return <Menu selectedKeys={['dummy']} selectable={true}>
        {modifiedBuiltVariables?.map(variable => {
            return <Menu.Item
                style={{ padding: "1px 12px" }}
                onClick={() => {
                    // dispatch(hcrActions.handleEditingDsPaneItem({ dataSourcePane: record.dataSourcePane, itemId, key: reqKey, value: conn.id, setForSql }));
                }}
                key={variable.name}
                value={variable.name}
            >
                <CalculationParametersItem variable={variable} />
            </Menu.Item>
        })}
    </Menu>
}

const CalculationsParameters = ({ modifiedBuiltVariables, setModifiedBuiltVariables }) => {
    const HCR = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HCR?.HCR || {});
    const { builtInVariables } = HCR.designerProperties?.variables || {};
    const canvasProperties = useSelector(state => state.cannedReports.present.hcrTabData.panes.find(pane => pane.key === state.cannedReports.present.hcrTabData.activeKey)?.canvasProperties || {});
    const hcrParametersArr = useSelector(state => state.cannedReports.present.hcrTabData.panes.find(pane => pane.key === state.cannedReports.present.hcrTabData.activeKey)?.dsPaneTypes?.find(ele => ele.dataSourcePane === hcrDSParameter)?.menu || []);
    const newCalOptions = canvasProperties?.calculations?.options || [];
    const newGrpOptions = canvasProperties?.groupProperties?.options || [];

    useEffect(() => {
        const calculatedList = builtInVariables?.map(ele => {
            const nodeObj = getCalcParamsNode({ name: ele.name, label: ((ele.name === "Current_Time") || (ele.name === "Current_Date")) ? ele.value : `$V{${ele.value}}` });
            if (ele.name === "Current_Date") {
                nodeObj.pattern = "MMMMM dd, yyyy";
            } else if (ele.name === "Current_Time") {
                nodeObj.pattern = "HH:mm";
            }
            return nodeObj;
        }) || []

        const newlyAddedCal = newCalOptions.map(ele => {
            const nodeObj = getCalcParamsNode({ name: ele.name, label: `$V{${ele.name}}` });
            nodeObj.isNewCalculation = true;
            return nodeObj;
        }) || []

        const newlyAddedGrp = newGrpOptions.map(ele => {
            const name = `${ele.name}_COUNT`;
            return getCalcParamsNode({ name, label: `$V{${name}}` });
        }) || []

        const paraArr = hcrParametersArr?.map(ele => {
            return getCalcParamsNode({ name: ele.name, label: `$P{${ele.name}}` });
        }) || []

        setModifiedBuiltVariables([...calculatedList, ...paraArr, ...newlyAddedCal, ...newlyAddedGrp]);

    }, [builtInVariables, hcrParametersArr, newCalOptions, newGrpOptions])

    return <Dropdown
        trigger={['click']}
        overlay={getCalculationsParamsMenu({ modifiedBuiltVariables })}
    >
        <Space style={{ fontSize: 12 }}>Calculations / Parameters <DownOutlined /></Space>
    </Dropdown>

}

export default CalculationsParameters;