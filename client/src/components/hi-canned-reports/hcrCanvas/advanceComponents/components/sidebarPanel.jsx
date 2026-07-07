import {
    EditorPanels
} from "@ant-design/flowchart";
import { Drawer } from 'antd';
import { isEmpty } from "lodash";
import { useEffect, useState } from "react";
import CellProperties from "./cellProperties";
import OutlineDSFieldProperties from "./outlineDSFieldProperties";
import NodeProperties from "./nodeProperties";
import TableOutlineProperties from "./TableOutlineProperties";
import { useDispatch } from "react-redux";
import CalculationProperties from "./calculationProperties";
import GroupProperties from "./groupProperties";
import ParameterProperties from "./parameterProperties";
import TableStyles from "./tableStyles";

const Wrapper = ({ children }) => {
    return (
        <div className="property-wrapper xflow-json-schema-form-body">
            {children}
        </div>
    )
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
        selectedStyle = []
    } = data || {}
    const { tableStyles = [] } = activeTab || {}
    const [active, setActive] = useState("default")
    const [title, setTitle] = useState("Property Pane")
    const dispatch = useDispatch()

    useEffect(() => {
        if (selectedCells.length) {
            setActive("cell")
            setTitle("Cell Properties")
        }
        if (selectedNodes.length) {
            setActive("element")
            setTitle("Property Pane")
        }
        if (outlineDsSelectedField) {
            setActive("dsField")
            setTitle("Field Properties")
        }
        if (selectedTable) {
            setActive("table")
            setTitle("Table")
        }
        if (selectedCalculation.length) {
            setActive("calculation")
            setTitle("Calculation")
        }
        if (selectedGroup.length) {
            setActive("group")
            setTitle("Group")
        }
        if (selectedParameter.length) {
            setActive("parameter")
            setTitle("Parameter")
        }
        if (selectedStyle.length) {
            setActive("styles")
            setTitle("Styles")
        }
    }, [selectedCells, selectedNodes, outlineDsSelectedField, selectedTable, selectedCalculation, selectedGroup, selectedParameter, selectedStyle])

    const handleTabChange = (key) => {
        setActive(key)
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
            {
                {
                    cell: (
                        <Wrapper>
                            <CellProperties
                                EditorPanels={EditorPanels}
                                {...{
                                    onCellPropertyChange,
                                    data,
                                    tableStyles
                                }}
                            />
                        </Wrapper>
                    ),
                    element: (
                        <Wrapper>
                            <NodeProperties
                                EditorPanels={EditorPanels}
                                onNodeConfigChange={onNodeConfigChange}
                                nodeConfig={nodeConfig}
                            />
                        </Wrapper>
                    ),
                    dsField: (
                        <Wrapper>
                            <OutlineDSFieldProperties
                                EditorPanels={EditorPanels}
                                outlineDsSelectedField={outlineDsSelectedField}
                                onChange={onOutlineDsItemChange}
                                tableData={data}
                                classNames={classNames}
                                dispatch={dispatch}
                                selectedSubDS={selectedSubDS}
                            />
                        </Wrapper>
                    ),
                    table: (
                        <Wrapper>
                            <TableOutlineProperties
                                EditorPanels={EditorPanels}
                                tableData={data}
                                onChange={onTablePropertiesChange}
                                queriesMenu={queriesMenu}
                                selectedSubDS={selectedSubDS}
                                dispatch={dispatch}
                            />
                        </Wrapper>
                    ),
                    calculation: (
                        <Wrapper>
                            <CalculationProperties
                                tableData={data}
                                {...{
                                    dispatch,
                                    EditorPanels,
                                    selectedCalculation,
                                    onClose,
                                    selectedSubDS
                                }}
                            />
                        </Wrapper>
                    ),
                    group: (
                        <Wrapper>
                            <GroupProperties
                                tableData={data}
                                {...{
                                    dispatch,
                                    EditorPanels,
                                    selectedGroup,
                                    onClose,
                                    selectedSubDS
                                }}
                            />
                        </Wrapper>
                    ),
                    parameter: (
                        <Wrapper>
                            <ParameterProperties
                                tableData={data}
                                {...{
                                    dispatch,
                                    EditorPanels,
                                    selectedParameter,
                                    onClose,
                                    selectedSubDS,
                                    classNames
                                }}
                            />
                        </Wrapper>
                    ),
                    styles: (
                        <Wrapper>
                            <TableStyles
                                tableData={data}
                                {...{
                                    dispatch,
                                    EditorPanels,
                                    selectedStyle,
                                    onClose,
                                    tableStyles
                                }}
                            />
                        </Wrapper>
                    ),
                    default: null
                }[active]
            }
        </Drawer >
    );
}

export default SidebarPanel
