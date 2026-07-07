import { AutoComplete, Button, Input, Space, Tooltip } from 'antd';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { v4 as uuidv4 } from "uuid";
import notify from '../../hi-notifications/notify';
import HCRExportPropertiesDrawer from './canvasProperty/hcrExportPropertiesDrawer';


const ElementProperties = (props = {}) => {
    const {
        properties = [],
        getLabel = () => { },
        onPropertyChange = () => { },
        component = "",
        componentId = "",
        InputFiled,
    } = props;
    const [drawerVisible, setDrawerVisible] = useState(false);
    const [options, setOptions] = useState([])
    let initialPropertyValue = {
        alias: "",
        key: "",
        value: "",
        description: ""
    }
    const [newProperty, setNewProperty] = useState(initialPropertyValue);
    const dispatch = useDispatch();

    const { hcrExportPropertiesData = {} } = useSelector(state => state.cannedReports.present) || {};
    const exportPropertiesOptions = Object.values(hcrExportPropertiesData)?.flat(1)?.map(({ key, value, ...rest }) => ({ actualValue: value, value: key, key, ...rest })) || [];

    const HcrPropertiesConfiguration = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HcrPropertiesConfiguration || {});
    const { tooltipInfo = {} } = HcrPropertiesConfiguration || {};
    const { tooltip_propertyName = {}, tooltip_propertyValue = {} } = tooltipInfo || {};

    const Notify = notify(dispatch);

    const checkIfPropertyAlreadyExist = (pKey) => properties.find(({ key }) => key === pKey);

    const handleAddProperty = () => {
        if (!newProperty.key || !newProperty.value || !componentId) return;

        const existingProperty = checkIfPropertyAlreadyExist(newProperty.key)
        if (existingProperty) {
            const { id = "", key = "", ...rest } = existingProperty || {}
            let replacedProperties = properties.map((prop) => {
                if (prop.id === id) {
                    return { ...prop, ...rest, key, value: newProperty.value, }
                }
                return prop;
            });
            onPropertyChange(replacedProperties);
            setNewProperty(initialPropertyValue);
            return;
        }
        const newProp = { ...newProperty, id: uuidv4() }
        onPropertyChange([...properties, newProp]);
        Notify.success({
            type: "Frontend",
            message: "Property added successfully.",
        });
        setNewProperty(initialPropertyValue);
    };

    const handleEditProperty = (id, property) => {
        let replacedProperties = properties.map((prop) => {
            if (prop.id === id) {
                return { ...prop, ...property }
            }
            return prop;
        });
        onPropertyChange(replacedProperties);
    }

    const handleChangeProperty = (key, value) => {
        setNewProperty((prev) => ({
            ...prev,
            [key]: value,
        }))
    }

    const handleDrawerClose = () => {
        setDrawerVisible(false);
    }

    const handleDeleteProperty = (id) => {
        let filteredProperties = properties.filter(({ id: propId }) => propId !== id);
        onPropertyChange(filteredProperties);
        Notify.success({
            type: "Frontend",
            message: "Property deleted successfully.",
        });
    }

    const getPropertyLabel = (label) => {
        return <div className="property-label">{getLabel({ label, tooltip: label === "Property" ? tooltip_propertyName : tooltip_propertyValue })}</div>
    }

    const getOptions = (value) => {
        if (!value) return exportPropertiesOptions;
        return exportPropertiesOptions.filter(({ key }) => key.includes(value))
    }

    const prepareOptions = (options) => {
        return options.filter((op) => !op.default).map(({ key, alias = "", description = "", ...rest }) => {
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
            ["value", "alias", "description"].forEach((key) => handleChangeProperty(key, key === "value" ? filteredItem?.["actualValue"] : filteredItem[key] || ""))
        }
    }

    useEffect(() => {
        setOptions(exportPropertiesOptions)
    }, [exportPropertiesOptions.length])


    return (
        <div data-testid="hcr-components-properties-container">
            <div data-testid="hcr-components-properties-key-container">
                {getPropertyLabel("Property")}
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

            <div data-testid="hcr-components-properties-value-container">
                {getPropertyLabel("Value")}
                <Input
                    size="middle"
                    value={newProperty.value}
                    onChange={(e) => {
                        handleChangeProperty("value", e.target.value)
                    }}
                    role="inputBox"
                />
            </div>

            <Space style={{ margin: "8px 0" }}>
                <Button
                    onClick={handleAddProperty}
                    size="small"
                    type="link"
                    disabled={!newProperty.key || !newProperty.value}
                    data-testid="hcr-components-properties-add-button"
                >
                    Add
                </Button>
            </Space>

            {properties?.length ?
                <div className="property-label" style={{ cursor: 'pointer' }} onClick={() => setDrawerVisible(true)}>
                    {properties.length} {properties.length === 1 ? "property" : "properties"} configured (click to edit)
                </div>
                :
                null
            }

            <HCRExportPropertiesDrawer
                title={`${component} Properties`}
                visible={drawerVisible}
                onClose={handleDrawerClose}
                data={properties}
                size="large"
                onEditProperty={handleEditProperty}
                onDeleteProperty={handleDeleteProperty}
                showSystemDefined={false}
                labelPanelOne={`${component} Properties`}
                {...{ InputFiled }}
            />

        </div >
    )
}

export default ElementProperties