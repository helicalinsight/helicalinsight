import { Menu } from "antd";
import {
  hideField,
  removeFieldFromCanvas,
  updateAggregations,
  updateCanvasField,
  createFilter,
  editFieldFunctions,
  toggleQueryEditor,
  toggleFloating,
  updateGeographicType,
  addConvertedDimensionToMarks,
  addConvertedDimensionToReference,
  updateColumn,
  updateFormatProperty,
  updateOrderBy
} from "../../../redux/actions/hreport.actions";
import { useDispatch } from "react-redux";
import { createDbFunc, getCanvasFieldDataType, getFloatingType } from "../../../utils/filter-utils";
import { getGeoJsonData } from "../utils/base";
import { geographicalSubTypes, intialMarks, initialReferenceLineList, orderBySubMenuOptions } from "../utils/constants";
import { getFieldDisplayName } from "../../../utils/utilities";
import React from "react";
import { getActualFieldDataType } from "../hi-viz-area/utils/grid-chart-utils";
import { cloneDeep, isEmpty } from "lodash";
import { getDefaultAFDataTypeValues } from "../hi-editing-area/utils/property-utils";
const { SubMenu } = Menu;



const FieldMenu = (props) => {
  const dispatch = useDispatch();
  let { field, functions, closeDropdown, reportId, marksList, referenceLineList, dateFunctions, databaseFunctions, onRename = () => { } } = props;
  let { custom, databaseFunction } = field;
  let isOrderByColumnDisabled = field.showOrderByColumn == false ? true : false;
  const isCustomFrontendField = field?.custom_frontend_field;
  let functionsList = {
    groupBy: [],
    aggregate: [],
  };

  const { id, orderBy } = field || {}

  // const fieldDataType = getActualFieldDataType(field)
  const fieldDataType = getCanvasFieldDataType(field)
  let dateFunctionsToRender = []
  if (['dateTime', 'date'].includes(fieldDataType) && !isEmpty(dateFunctions)) {
    dateFunctionsToRender = dateFunctions[fieldDataType]
    dateFunctionsToRender = dateFunctionsToRender.filter(({ key }) => key !== 'individual')
  }

  Object.keys(functions).map((key) => {
    let group = key.split(".")[2];
    if (group === "orderBy") return null;
    functionsList[group].push({ key, value: functions[key] });
  });
  const handleDelete = () => {
    dispatch(removeFieldFromCanvas({ field }));
  };
  const updateField = (key) => {
    dispatch(updateCanvasField({ id: field.id, key }));
  };
  const handleAggregates = (id, group, key) => {
    dispatch(updateAggregations({ id, group, key }));
    let fieldDataType = getCanvasFieldDataType(field)
    if (fieldDataType === 'text' && (!field.aggregate || !field.aggregate.includes(key))) {
      updateAutoFormatting(field, 'numeric');
    }
  };
  const handleToggleHidden = () => {
    dispatch(hideField({ id: field.id }));
  };
  const handleUseAsFilter = () => {
    dispatch(createFilter({ ...field, reportId }));
  };
  const handleShowFuncEditor = () => {
    dispatch(editFieldFunctions({ id: field.id }));
  };
  const editColumn = () => {
    dispatch(toggleQueryEditor({ id: field.id }));
  };
  const handleGeographicType = async (id, group, key) => {
    dispatch(updateGeographicType({ id, group, key }))
    let hreportState = {}
    dispatch((_, getState, services) => {
      hreportState = getState()?.hreport?.present;
    })
    if (!["lat", 'long'].includes(key) && !hreportState?.geoJsonData[key]?.length) {
      if (key === "city") {
        await getGeoJsonData({ value: 'world_cities.json', type: key }, dispatch)
      }
      if (key === "state") {
        await getGeoJsonData({ value: "world_states.json", type: key }, dispatch)
      }
      if (["world", "country"].includes(key)) {
        await getGeoJsonData({ value: "world_countries.json", type: key }, dispatch)
      }
    }
  }

  const updateAutoFormatting = (field, dataType) => {
    let newFormatProperties = {
      id: field.id,
      values: getDefaultAFDataTypeValues(dataType, "create"),
    }
    dispatch(updateFormatProperty(newFormatProperties))
  }

  const createFnDefinition = (value) => {
    let fieldName = getFieldDisplayName(field)
    return `${value}(${fieldName})`
  }

  const handleDateFnsType = (key) => {
    let tempField = cloneDeep(field)
    if (tempField?.databaseFunction && key === tempField?.databaseFunction?.key) {
      tempField.databaseFunction = null;
      tempField.functionsDefinition = '';
      dispatch(updateColumn({ editingField: tempField }))
      // let fieldDataType = getCanvasFieldDataType(tempField)
      // updateAutoFormatting(tempField, fieldDataType)
      return;
    }
    let { part, value } = dateFunctionsToRender.find((item) => item.key === key);
    let updatedField = createDbFunc(tempField, part, databaseFunctions, dateFunctions)
    let fieldDataType = getCanvasFieldDataType(updatedField)
    if (updatedField.databaseFunction) {
      updatedField.functionsDefinition = createFnDefinition(value)
      const { returns = "" } = updatedField.databaseFunction || {}
      if (['numeric'].includes(returns)) {
        updatedField.floatingType = ""
        // updateAutoFormatting(field, 'numeric')
      } else {
        // updateAutoFormatting(updatedField, returns)
      }
    } else {
      updatedField.functionsDefinition = ''
      // updateAutoFormatting(updatedField, fieldDataType)
    }
    dispatch(updateColumn({ editingField: updatedField }))
  }

  const handleOrderBy = (key) => {
    let newKey = key;
    if (!isEmpty(orderBy) && orderBy.includes(key)) newKey = null
    dispatch(updateOrderBy({ id, key: newKey }))
  }

  const handleRename = () => {
    onRename({
      stopPropagation: () => { }
    })
  }

  let selectedKeys = [];
  if (databaseFunction && Object.keys(databaseFunction).length) {
    selectedKeys.push("5");

    if (dateFunctionsToRender?.length) { //2375 changes
      const allDbFunction = Object.values(databaseFunctions)?.flat(1) ?? [];
      let dbFn = allDbFunction?.find((item) => item.key === databaseFunction.key);
      if (dbFn) {
        const { key = '' } = dbFn || {}
        allDbFunction.map(item => {
          if (item.key === key) {
            selectedKeys.push(key);
          }
        })
      }
    }
  }
  if (field.groupBy && field.groupBy.length) {
    selectedKeys.push("groupby");
  }
  if (field.hiddenIncludeInResultSet) {
    selectedKeys.push("hiddenIncludeInResultSet");
  }
  if (field.applyBeforeAggregate) {
    selectedKeys.push("applyBeforeAggregate");
  }
  if (field.aggregate && field.aggregate.length) {
    selectedKeys = [...selectedKeys, ...field.aggregate];
  }
  if (field.hidden) {
    selectedKeys.push("hide-field")
  }

  if (field?.geographicType?.length) {
    selectedKeys.push(field.geographicType)
  }

  if (!isEmpty(orderBy)) {
    selectedKeys.push(orderBy[0])
  }

  const handleMenuClick = (e) => {
    if (e.key === "1") {
      editColumn()
    } else if (e.key === "filter") {
      handleUseAsFilter()
      closeDropdown()
    } else if (e.key === "hiddenIncludeInResultSet") {
      updateField("hiddenIncludeInResultSet")
      closeDropdown()
    } else if (e.key === "hide-field") {
      handleToggleHidden()
      closeDropdown()
    } else if (e.key === "5") {
      handleShowFuncEditor()
      closeDropdown()
    } else if (e.key === "applyBeforeAggregate") {
      updateField("applyBeforeAggregate")
    } else if (e.key === "groupby") {
      handleAggregates(field.id, "groupBy", functionsList.groupBy[0].key)
    } else if (e.key === "remove-field") {
      handleDelete()
      closeDropdown()
    } else if (e.keyPath.length === 2) {
      e.keyPath[1] === "aggregate" && handleAggregates(field.id, "aggregate", e.key);
      e.keyPath[1] === "geographic" && handleGeographicType(field.id, "geographic", e.key)
      if (e.keyPath[1] === 'date_time_db_functions') {
        handleDateFnsType(e.key);
        return;
      }
      if (e.keyPath[1] === "order_by") {
        handleOrderBy(e.key);
        return;
      }
    } else if (e.key === "rename") {
      handleRename(e.key)
      return
    }
    else if (e.key === "discrete" || e.key === "continous") {
      toggleFloatingType(e.key)
    }
  }

  const getNewMarks = (markPresent = false) => {
    if (!markPresent) {
      let { id } = field;
      let newMark = { ...intialMarks }
      newMark.id = id;
      newMark.value = getFieldDisplayName(field);
      return [...marksList, newMark]
    }
    if (markPresent) {
      const filteredMarks = marksList.filter((item) => {
        return item?.id !== field?.id
      })
      return filteredMarks
    }
  }

  const getNewReferenceLine = (referenceLinePresent = false) => {
    if (!referenceLinePresent) {
      let { id } = field;
      let newReferenceLine = { ...initialReferenceLineList };
      newReferenceLine.id = id;
      newReferenceLine.display = getFieldDisplayName(field);
      return [...referenceLineList, newReferenceLine];
    }
    if (referenceLinePresent) {
      const filteredReferenceLines = referenceLineList?.filter((item) => {
        return item?.id !== field?.id;
      });
      return filteredReferenceLines;
    }
  };

  const toggleFloatingType = floatingType => {
    if (field.floatingType === floatingType) return null;
    dispatch(toggleFloating({ reportId, floatingType, id: field.id }))
    const newMarkFields = getNewMarks(floatingType === "discrete")
    dispatch(addConvertedDimensionToMarks({ marksList: newMarkFields }))
    const newReferenceFields = getNewReferenceLine(floatingType === "discrete")
    dispatch(addConvertedDimensionToReference({ referenceLineList: newReferenceFields }))

  }
  let { isMeasure, floatingType } = getFloatingType(field)
  selectedKeys.push(floatingType)

  return (
    <Menu
      className="hi-field-menu"
      data-testid="hi-field-menu"
      selectedKeys={selectedKeys}
      onClick={handleMenuClick}
      multiple={true}
    // triggerSubMenuAction="click"
    >
      {!isCustomFrontendField ? <React.Fragment>
        {custom && (
          <Menu.Item key="1">
            Edit
          </Menu.Item>
        )}
        {!custom && (
          <Menu.Item
            key="filter"
            className="group-end"
            data-testid={`create-filter-${field.column}`}
          >
            Filter
          </Menu.Item>
        )}
        <Menu.Item
          key="hiddenIncludeInResultSet"
          data-testid="hide-include-field"
        >
          Fetch and Hide
        </Menu.Item>
        <Menu.Item
          key="hide-field"
          data-testid="hide-field"
          className="group-end"
        >
          Hide
        </Menu.Item>
        <SubMenu key="aggregate" title={"Aggregate"} data-testid="field-aggregate-list">
          {functionsList.aggregate.map((func) => {
            const item = func.value;
            return (
              <Menu.Item
                className="aggregation-item"
                data-testid={`aggregate-item`}
                key={"db.generic.aggregate." + item}
              >
                {item[0].toUpperCase() + item.slice(1)}
              </Menu.Item>
            );
          })}
        </SubMenu>
        <Menu.Item key="applyBeforeAggregate">
          Apply Before Aggregate
        </Menu.Item>
        <Menu.Item
          key="5"
          data-testid={`db-functions-${field.column}`}
        >
          Functions
        </Menu.Item>
        <Menu.Item
          className="group-end"
          data-testid="field-groupby"
          key={"groupby"}
        >
          Group by
        </Menu.Item>
        {/* {isAggregate && (
        <SubMenu key="sub2" title="Add Table Calculation" popupClassName="field-calculations">
          <Menu.Item key="sub21">Running Total</Menu.Item>
          <Menu.Item key="sub22">Difference From</Menu.Item>
          <Menu.Item key="sub23">Percent Difference From</Menu.Item>
          <Menu.Item key="sub24">Percent From</Menu.Item>
        </SubMenu>
      )}
      */}
        {isMeasure && (
          <Menu.Item key="discrete"  >
            Discrete
          </Menu.Item>
        )}
        {isMeasure && (
          <Menu.Item key="continous" className="group-end">
            Continous
          </Menu.Item>
        )}
        {!isOrderByColumnDisabled && <Menu.Item key="9">Order By Column</Menu.Item>}

        <SubMenu key="geographic" title={"Geographic"} data-testid="geographic-menu-list">
          {geographicalSubTypes.map(({ key, value, label }) => {
            return (
              <Menu.Item
                className="aggregation-item"
                key={value}
              >
                {label}
              </Menu.Item>
            );
          })}
        </SubMenu>
      </React.Fragment> : null}

      {/* date function render in case of date/dateTime data type */}

      {dateFunctionsToRender?.length ?
        <SubMenu key="date_time_db_functions" title={`Date Functions`} data-testid="date-time-fns-list">
          {dateFunctionsToRender.map(({ label, key }) => {
            return (
              <Menu.Item
                className="aggregation-item"
                key={key}
              >
                {label}
              </Menu.Item>
            );
          })}
        </SubMenu> : null}

      <SubMenu key="order_by" title={"Order By"} data-testid="order-by-menu-list" className="group-start">
        {orderBySubMenuOptions.map(({ value, label }) => {
          return (
            <Menu.Item
              className="aggregation-item"
              key={value}
            >
              {label}
            </Menu.Item>
          );
        })}
      </SubMenu>
      <Menu.Item key="rename" >
        Rename
      </Menu.Item>
      <Menu.Item key="remove-field" data-testid="remove-field">
        Remove
      </Menu.Item>
    </Menu>
  );
};

export default FieldMenu;
