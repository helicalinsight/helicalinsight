import {
    EditorPanels
} from "@ant-design/flowchart";
import { Drawer } from 'antd';
import { useMemo } from "react";
import { useDispatch } from "react-redux";
import CalculationProperties from "./calculationProperties";
import CellProperties from "./cellProperties";
import GroupProperties from "./groupProperties";
import NodeProperties from "./nodeProperties";
import OutlineDSFieldProperties from "./outlineDSFieldProperties";
import ParameterProperties from "./parameterProperties";
import TableOutlineProperties from "./TableOutlineProperties";
import TableStyles from "./tableStyles";
import { getCellsFromCrosstab } from "../utils";
import CrosstabGroup from "./crosstabGroup";
import CrosstabMeasure from "./crosstabMeasure";
const Wrapper = ({ children }) => {
    return (
        <div className="property-wrapper xflow-json-schema-form-body">
            {children}
        </div>
    )
}
const resolveActivePanel = (data = {}) => {
    const {
        selectedCells = [],
        selectedNodes = [],
        outlineDsSelectedField = null,
        selectedTable = null,
        selectedCalculation = [],
        selectedGroup = [],
        selectedParameter = [],
        selectedStyle = [],
        selectedCTGroup = [],
        selectedCTMeasure = [],
        selectedCrosstab = null
    } = data;

    if (selectedCells.length) return { active: "cell", title: "Cell Properties" };
    if (selectedNodes.length) return { active: "element", title: "Property Pane" };
    if (outlineDsSelectedField) return { active: "dsField", title: "Field Properties" };
    if (selectedTable) return { active: "table", title: "Table" };
    if (selectedCalculation.length) return { active: "calculation", title: "Calculation" };
    if (selectedGroup.length) return { active: "group", title: "Group" };
    if (selectedParameter.length) return { active: "parameter", title: "Parameter" };
    if (selectedStyle.length) return { active: "styles", title: "Styles" };
    if (selectedCTGroup.length) return { active: "ctGroup", title: "Crosstab Group" };
    if (selectedCTMeasure.length) return { active: "ctMeasure", title: "Crosstab Measure" };
    if (selectedCrosstab) return { active: "table", title: "Crosstab" };
    return { active: "default", title: "Property Pane" };
}

const SidebarPanel = (props = {}) => {
    const {
        open,
        onClose = () => { },
        onNodeConfigChange = () => { },
        nodeConfig = {},
        onCellPropertyChange = () => { },
        data = {},
        onOutlineDsItemChange = () => { },
        classNames = {},
        queriesMenu = [],
        onTablePropertiesChange = () => { },
        onCrosstabPropertiesChange = () => { },
        selectedSubDS = {},
        activeTab = {}
    } = props || {}
    const {
        selectedCells = [],
        selectedNodes = [],
        outlineDsSelectedField = null,
        selectedTable = null,
        selectedCalculation = [],
        selectedQueryID,
        selectedGroup = [],
        selectedParameter = [],
        selectedStyle = [],
        selectedCTGroup = [],
        selectedCTMeasure = [],
        category
    } = data || {}
    const { tableStyles = [] } = activeTab || {}
    const isCrosstab = category === "crosstabv2"
    const dispatch = useDispatch()

    const { active, title } = useMemo(() => resolveActivePanel(data), [data]);

    const renderActivePanel = () => {
        if (!open) return null;

        switch (active) {
            case "cell":
                return (
                    <Wrapper>
                        <CellProperties
                            EditorPanels={EditorPanels}
                            onCellPropertyChange={onCellPropertyChange}
                            data={{
                                ...data,
                                ...(isCrosstab && { cells: getCellsFromCrosstab(data) })
                            }}
                            tableStyles={tableStyles}
                        />
                    </Wrapper>
                );
            case "element":
                return (
                    <Wrapper>
                        <NodeProperties
                            EditorPanels={EditorPanels}
                            onNodeConfigChange={onNodeConfigChange}
                            nodeConfig={nodeConfig}
                        />
                    </Wrapper>
                );
            case "dsField":
                return (
                    <Wrapper>
                        <OutlineDSFieldProperties
                            EditorPanels={EditorPanels}
                            outlineDsSelectedField={data.outlineDsSelectedField}
                            onChange={onOutlineDsItemChange}
                            tableData={data}
                            classNames={classNames}
                            selectedSubDS={selectedSubDS}
                            dispatch={dispatch}
                        />
                    </Wrapper>
                );
            case "table":
                return (
                    <Wrapper>
                        <TableOutlineProperties
                            EditorPanels={EditorPanels}
                            componentData={data}
                            onChange={isCrosstab ? onCrosstabPropertiesChange : onTablePropertiesChange}
                            queriesMenu={queriesMenu}
                            selectedSubDS={selectedSubDS}
                            dispatch={dispatch}
                        />
                    </Wrapper>
                );
            case "calculation":
                return (
                    <Wrapper>
                        <CalculationProperties
                            tableData={data}
                            EditorPanels={EditorPanels}
                            selectedCalculation={data.selectedCalculation}
                            onClose={onClose}
                            selectedSubDS={selectedSubDS}
                            dispatch={dispatch}
                        />
                    </Wrapper>
                );
            case "group":
                return (
                    <Wrapper>
                        <GroupProperties
                            tableData={data}
                            EditorPanels={EditorPanels}
                            selectedGroup={selectedGroup}
                            onClose={onClose}
                            selectedSubDS={selectedSubDS}
                            dispatch={dispatch}
                        />
                    </Wrapper>
                );
            case "parameter":
                return (
                    <Wrapper>
                        <ParameterProperties
                            tableData={data}
                            EditorPanels={EditorPanels}
                            selectedParameter={selectedParameter}
                            onClose={onClose}
                            selectedSubDS={selectedSubDS}
                            classNames={classNames}
                            dispatch={dispatch}
                        />
                    </Wrapper>
                );
            case "styles":
                return (
                    <Wrapper>
                        <TableStyles
                            tableData={data}
                            EditorPanels={EditorPanels}
                            selectedStyle={selectedStyle}
                            onClose={onClose}
                            tableStyles={tableStyles}
                            dispatch={dispatch}
                        />
                    </Wrapper>
                );
            case "ctGroup":
                return (
                    <Wrapper>
                        <CrosstabGroup
                            data={data}
                            EditorPanels={EditorPanels}
                            onClose={onClose}
                            dispatch={dispatch}
                            selectedGroup={selectedCTGroup}
                            selectedSubDS={selectedSubDS}
                            classNames={classNames}
                        />
                    </Wrapper>
                );
            case "ctMeasure":
                return (
                    <Wrapper>
                        <CrosstabMeasure
                            data={data}
                            EditorPanels={EditorPanels}
                            onClose={onClose}
                            dispatch={dispatch}
                            selectedMeasure={selectedCTMeasure}
                            selectedSubDS={selectedSubDS}
                            classNames={classNames}
                        />
                    </Wrapper>
                );
            default:
                return null;
        }
    }

    return (
        <Drawer
            title={title}
            placement="right"
            closable={true}
            onClose={onClose}
            open={open}
            getContainer={false}
            style={{
                position: 'fixed',
                right: 25,
                top: 40,
            }}
            mask={false}
            className="hcr-side-bar-panel"
        >
            {renderActivePanel()}
        </Drawer >
    );
}

export default SidebarPanel
