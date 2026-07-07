import { useState } from "react";
import { Divider, Row, Button } from "antd";

export const useFieldSelection = ({
  selectedFields = [],
  selectedColumnFields = [],
  selectedRowFields = [],
  selectedMeasures = [],
  onPropertyChange,
  selectedGroupFields
}) => {
  const [searchStr, setSearchStr] = useState("");
  const [columnSearchStr, setColumnSearchStr] = useState("");
  const [rowSearchStr, setRowSearchStr] = useState("");
  const [measureSearchStr, setMeasureSearchStr] = useState("");

  const handleSelectFieldsChange = (value) => {
    const updatedColumnFields = selectedColumnFields?.filter((field) =>
      value?.includes(field),
    );
    const updatedRowFields = selectedRowFields?.filter((field) =>
      value?.includes(field),
    );
    const updatedMeasures = selectedMeasures?.filter((field) =>
      value?.includes(field),
    );
    const updatedGroupFields = selectedGroupFields?.filter((field) =>
      value?.includes(field),
    );
    onPropertyChange(
      {
        key: "selectedFields",
        value,
      },
      {
        selectedColumnFields: updatedColumnFields,
        selectedRowFields: updatedRowFields,
        selectedMeasures: updatedMeasures,
        selectedGroupFields: updatedGroupFields,
      },
    );
  };

  const handleSelectColumnFieldsChange = (value) => {
    const updatedRowFields = selectedRowFields?.filter(
      (field) => !value?.includes(field),
    );
    onPropertyChange(
      { key: "selectedColumnFields", value },
      {
        selectedRowFields: updatedRowFields,
      },
    );
  };
  const handleSelectRowFieldsChange = (value) => {
    onPropertyChange({ key: "selectedRowFields", value });
  };
  const handleSelectMeasuresChange = (value) => {
    onPropertyChange({ key: "selectedMeasures", value });
  };
  const handleSelectGroupFields = (value) => {
    onPropertyChange({ key: "selectedGroupFields", value });
  };
  const getAvailableColumnFields = () => {
    return (
      selectedFields?.map((field) => ({ label: field, value: field })) || []
    );
  };
  const getFields = (fields, searchStr) => {
    if (!fields) return [];
    return fields.filter((field) => {
      const fieldName = field.name || field;
      return fieldName.toLowerCase().includes(searchStr.toLowerCase());
    });
  };
  const dropdownRender = (
    menu,
    onClick = () => { },
    fields,
    currentSelectedValues = [],
    isColumnSelect = false,
    isRowSelect = false,
    isMeasureSelect = false,
  ) => {
    if (!fields?.length) return menu;
    const filteredFieldNames = fields?.map((field) => field.name || field);
    let fieldsToCheck = filteredFieldNames;

    if (isColumnSelect) {
      fieldsToCheck = filteredFieldNames?.filter((field) =>
        selectedFields.includes(field),
      );
    } else if (isRowSelect) {
      fieldsToCheck = filteredFieldNames?.filter(
        (field) =>
          selectedFields.includes(field) &&
          !selectedColumnFields.includes(field),
      );
    } else if (isMeasureSelect) {
      fieldsToCheck = filteredFieldNames?.filter((field) =>
        selectedFields.includes(field),
      );
    }
    const shouldShowSelectAll = fieldsToCheck && fieldsToCheck.length > 0;
    const allFilteredSelected =
      fieldsToCheck.length > 0 &&
      fieldsToCheck?.every((field) => currentSelectedValues?.includes(field));
    const buttonText = allFilteredSelected ? "Deselect All" : "Select All";
    return (
      <>
        {menu}
        <Divider style={{ margin: "4px 0" }} />
        <Row justify="center">
          <Button type="link" onClick={onClick}>
            {buttonText}
          </Button>
        </Row>
      </>
    );
  };
  const handleSelectAllClick = (
    key,
    fields,
    searchStr,
    currentSelectedValues = [],
    isColumnSelect = false,
    isRowSelect = false,
    isMeasureSelect = false,
  ) => {
    let fieldNames = fields.map((field) => (field.name ? field.name : field));
    if (searchStr) {
      fieldNames = fieldNames.filter((field) =>
        field.toLowerCase().includes(searchStr.toLowerCase()),
      );
    }
    if (isColumnSelect) {
      fieldNames = fieldNames.filter((field) => selectedFields.includes(field));
    } else if (isRowSelect) {
      fieldNames = fieldNames.filter(
        (field) =>
          selectedFields.includes(field) &&
          !selectedColumnFields.includes(field),
      );
    } else if (isMeasureSelect) {
      fieldNames = fieldNames.filter((field) => selectedFields.includes(field));
    }
    const allSelected =
      fieldNames.length > 0 &&
      fieldNames.every((field) => currentSelectedValues.includes(field));
    const newSelection = allSelected
      ? currentSelectedValues.filter((field) => !fieldNames.includes(field))
      : [...new Set([...currentSelectedValues, ...fieldNames])];
    const otherProperties = {};
    if (key === "selectedFields") {
      otherProperties.selectedColumnFields = selectedColumnFields?.filter((f) =>
        newSelection.includes(f),
      );
      otherProperties.selectedRowFields = selectedRowFields?.filter((f) =>
        newSelection.includes(f),
      );
      otherProperties.selectedMeasures = selectedMeasures?.filter((f) =>
        newSelection.includes(f),
      );
    } else if (key === "selectedColumnFields") {
      otherProperties.selectedRowFields = selectedRowFields?.filter(
        (f) => !newSelection.includes(f),
      );
    }
    onPropertyChange({ key, value: newSelection }, otherProperties);
  };
  return {
    searchStr,
    setSearchStr,
    columnSearchStr,
    setColumnSearchStr,
    rowSearchStr,
    setRowSearchStr,
    measureSearchStr,
    setMeasureSearchStr,
    handleSelectFieldsChange,
    handleSelectColumnFieldsChange,
    handleSelectRowFieldsChange,
    handleSelectMeasuresChange,
    getAvailableColumnFields,
    getFields,
    dropdownRender,
    handleSelectAllClick,
    handleSelectGroupFields
  };
};
