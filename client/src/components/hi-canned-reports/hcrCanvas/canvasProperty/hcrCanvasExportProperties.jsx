
import {
    AutoComplete,
    Button,
    Input,
    Space,
    Tooltip
} from "antd";
import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { v4 as uuidv4 } from "uuid";
import { hcrActions } from "../../../../redux/actions";
import HCRExportPropertiesDrawer from "./hcrExportPropertiesDrawer";
import notify from "../../../hi-notifications/notify";


export default function CanvasExportProperties({ dispatch, InputFiled, getLabel = () => { } }) {
    const [drawerVisible, setDrawerVisible] = useState(false);
    const [options, setOptions] = useState([])
    let initialPropertyValue = {
        alias: "",
        key: "",
        value: "",
        description: ""
    }
    const [newProperty, setNewProperty] = useState(initialPropertyValue);
    const { hcrExportProperties = [] } = useSelector(
        (state) =>
            state.cannedReports.present.hcrTabData.panes.find(
                (pane) => pane.key === state.cannedReports.present.hcrTabData.activeKey
            )
    ) || {};
    const { hcrExportPropertiesData = {} } = useSelector(state => state.cannedReports.present) || {};
    const exportPropertiesOptions = Object.values(hcrExportPropertiesData)?.flat(1)?.map(({ key, value, ...rest }) => ({ actualValue: value, value: key, key, ...rest })) || [];

    const HcrPropertiesConfiguration = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HcrPropertiesConfiguration || {});
    const { tooltipInfo = {} } = HcrPropertiesConfiguration || {};
    const { tooltip_propertyName, tooltip_propertyValue } = tooltipInfo || {};

    const Notify = notify(dispatch);

    const checkIfPropertyAlreadyExist = (pKey) => hcrExportProperties.find(({ key }) => key === pKey);

    const handleAdd = () => {
        if (!newProperty.key || !newProperty.value) return;

        const existingProperty = checkIfPropertyAlreadyExist(newProperty.key)
        if (existingProperty) {
            const { id = "", key = "" } = existingProperty || {}
            handlePropertyEdit(id, { key, value: newProperty.value, alias: newProperty.alias }, true);
            setNewProperty(initialPropertyValue);
            return;
        }
        dispatch(hcrActions.hcrAddNewExportProperty({
            ...newProperty,
            id: uuidv4(),
            // description: "",
            default: false
        }));

        Notify.success({
            type: "Frontend",
            message: "Property added successfully.",
        });

        setNewProperty(initialPropertyValue);
    };


    const handleChangeProperty = (key, value) => {
        setNewProperty((prev) => ({
            ...prev,
            [key]: value,
        }))
    }

    const handleDrawerClose = () => {
        setDrawerVisible(false);
    }

    const handlePropertyEdit = (id, { key, value, alias }, fromAddFn = false) => {
        let deleteId = null;
        const existingProperty = checkIfPropertyAlreadyExist(key)
        if (existingProperty && existingProperty.id !== id && !fromAddFn) deleteId = existingProperty.id

        dispatch(
            hcrActions.hcrEditExportProperty({
                id,
                key: key || "",
                value: value || "",
                alias: alias || "",
                deleteId
            })
        );
        Notify.success({
            type: "Frontend",
            message: "Property updated successfully.",
        });
    }


    const handleDeleteProperty = (id) => {
        dispatch(hcrActions.hcrDeleteExportProperty(id));

        Notify.success({
            type: "Frontend",
            message: "Property deleted successfully.",
        });
    }




    const getPLabel = (label) => {
        return <div className="property-label">{getLabel({ label, tooltip: label === "Property" ? tooltip_propertyName : tooltip_propertyValue })}</div>
    }

    const getOptions = (value) => {
        if (!value) return exportPropertiesOptions;
        return exportPropertiesOptions.filter(({ key }) => key.includes(value))
    }

    const prepareOptions = (options) => {
        return options.filter((op) => !op.default).map(({ key, alias = "", description, ...rest }) => {
            return {
                key,
                label: <Tooltip title={description}><span>{alias || key}</span></Tooltip>,
                description,
                ...rest,
            }
        })
    }

    const handleSelect = (value) => {
        handleChangeProperty("key", value)
        let filteredItem = exportPropertiesOptions.find(({ key }) => key === value);
        if (filteredItem) {
            handleChangeProperty("value", filteredItem.actualValue)
            handleChangeProperty("alias", filteredItem.alias || "")
            handleChangeProperty("description", filteredItem.description || "")
        }
    }

    useEffect(() => {
        setOptions(exportPropertiesOptions)
    }, [exportPropertiesOptions.length])

    return (
        <div>
            <div>
                {getPLabel("Property")}
                <AutoComplete
                    popupMatchSelectWidth={252}
                    style={{ width: 255 }}
                    options={prepareOptions(options)}
                    onSelect={handleSelect}
                    onSearch={(value) => {
                        value = value.toLowerCase();
                        setOptions(getOptions(value))
                    }}
                    value={newProperty.key}
                >
                    <Input
                        size="middle"
                        onChange={(e) => {
                            handleChangeProperty("key", e.target.value)
                        }}
                        value={newProperty.key}
                    />
                </AutoComplete>
            </div>

            <div>
                {getPLabel("Value")}
                <Input
                    size="middle"
                    value={newProperty.value}
                    onChange={(e) => {
                        handleChangeProperty("value", e.target.value)
                    }}
                    role="inputBox"
                />
            </div>

            {/* 
            <InputFiled
                label={getPLabel("Value")}
                value={newProperty.value}
                onChange={(value) => {
                    handleChangeProperty("value", value)
                }}
            /> */}
            <Space style={{ margin: "8px 0" }}>
                <Button onClick={handleAdd} size="small" type="link" disabled={!newProperty.key || !newProperty.value}>
                    Add
                </Button>
            </Space>

            {hcrExportProperties?.length ?
                <div className="property-label" style={{ cursor: 'pointer' }} onClick={() => setDrawerVisible(true)}>
                    {hcrExportProperties.length} {hcrExportProperties.length === 1 ? "property" : "properties"} configured (click to edit)
                </div>
                : null}

            <HCRExportPropertiesDrawer
                title="Report Properties"
                visible={drawerVisible}
                onClose={handleDrawerClose}
                data={hcrExportProperties}
                size="large"
                onEditProperty={handlePropertyEdit}
                onDeleteProperty={handleDeleteProperty}
                {...{ InputFiled }}
            />
        </div >
    );
}
