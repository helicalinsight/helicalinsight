const onEdit = (id, data) => {
  const editedData = data.map((eachData) => {
    if (eachData.id === id) {
      return {
        ...eachData,
        isEditClicked: !eachData.isEditClicked,
      };
    } else {
      return eachData;
    }
  });
  return editedData;
};

const handleBlur = (e, id, data, dispatch, storeData, Notify) => {
  const nameCheck = data.map((data) => {
    if (data.id == id && data.name === e.target.value) {
      return false;
    } else if (data.name === e.target.value) {
      return true;
    }
  });

  if (nameCheck.includes(true)) {
    Notify.error({
      message: "Name already exists",
      type: "warning",
    });
  } else {
    const filteredData = data.map((eachData) => {
      if (eachData.id === id) {
        return {
          ...eachData,
          name: e.target.value,
          label: e.target.value,
          isEditClicked: false,
        };
      } else {
        return eachData;
      }
    });
    // return filteredData; // uncomment this for testing
    dispatch(storeData(filteredData));
  }
};

const handleKeyDown = (e, id, data, dispatch, storeData, Notify) => {
  const nameCheck = data.map((data) => data.name === e.target.value);
  if (e.key === "Enter") {
    if (nameCheck.includes(true)) {
      Notify.error({
        message: "Name already exists",
        type: "warning",
      });
    } else {
      const filteredData = data.map((eachData) => {
        if (eachData.id === id) {
          return {
            ...eachData,
            name: e.target.value,
            label: e.target.value,
            isEditClicked: false,
          };
        } else {
          return eachData;
        }
      });
      // return filteredData; // uncomment this for testing
      dispatch(storeData(filteredData));
    }
  }
};

const onDelete = (id, data) => {
  const filteredData = data.filter((eachData) => eachData.id !== id);
  return filteredData;
};

const handleSqlTypeChange = (id, name, data, dispatch, storeData) => {
  const filteredData = data.map((eachData) => {
    if (eachData.id === id) {
      return {
        ...eachData,
        sql: { ...eachData.sql, type: name },
      };
    } else {
      return eachData;
    }
  });
  // return filteredData; // uncomment for testing
  dispatch(storeData(filteredData));
};

const onChangeDataSource = (id, checkedValues, dataSourceData, data, dispatch, storeData) => {
  const selectedDataSource = dataSourceData.filter((eachData) => {
    if (checkedValues.includes(eachData.id)) {
      return true;
    }
    return false;
  });

  const updatedData = data.map((eachData) => {
    if (eachData.id === id) {
      return {
        ...eachData,
        sql: { ...eachData.sql, dataSource: selectedDataSource },
      };
    }
    return eachData;
  });

  dispatch(storeData(updatedData));
};

const onChangeParameters = (id, checkedValues, parametersData, dispatch, storeData) => {
  const selectedParmater = parametersData.filter((eachData) => {
    if (checkedValues.includes(eachData.id)) {
      return true;
    }
    return false;
  });

  const updatedData = parametersData.map((eachData) => {
    if (eachData.id === id) {
      return {
        ...eachData,
        sql: { ...eachData.sql, parameters: selectedParmater },
      };
    }
    return eachData;
  });

  dispatch(storeData(updatedData));
};

export {
  onEdit,
  handleBlur,
  handleKeyDown,
  onDelete,
  handleSqlTypeChange,
  onChangeDataSource,
  onChangeParameters,
};
