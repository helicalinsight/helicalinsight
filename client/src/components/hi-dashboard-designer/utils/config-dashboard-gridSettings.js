import { cloneDeep } from "lodash-es";

//-------------------------------------------------     components initialPosition  ----------

export const designerStateComponenetsInitialPositionConstants = {
    "x": 0,
    "y": 0,
    "w": 2,
    "h": 2,
    "static": false
};

export const removeDesignerObjectConstants = ({apiObj={}, constantObj={}}) => {
    let newData = {...apiObj};
    Object.entries(constantObj).forEach(arr => {
        if(newData[arr[0]] === arr[1]) {
           delete newData[arr[0]]
        }
    })
    return newData;
}

export const appendDesignerObjectConstants = ({apiObj={}, constantObj={}}) => {
    let newData = {...apiObj};
    Object.entries(constantObj).forEach(arr => {
        if(!(arr[0] in newData)) {
            newData[arr[0]] = arr[1];
        }
    })
    return newData;
}

 //  --------------------------------------  components  gridItemConfig ------------------------

export const gridItemConfigConstants = [ {
        "key": "header",
        "values": {
            "enable": true,
            "placeholder": "Edit/Add your header content here",
            "link": "",
            "backgroundColor": {
                "r": 24,
                "g": 144,
                "b": 255,
                "a": 100
            },
            "position": "left",
            "customColor": {
                "r": 255,
                "g": 255,
                "b": 255,
                "a": 100
            },
            "enableTooltip": false,
            "tooltipText": ""
        }
    },
    {
        "key": "shadow",
        "values": {
            "enable": false,
            "xOffset": 1,
            "yOffset": 1,
            "blur": 1,
            "spread": 1,
            "color": {
                "r": 24,
                "g": 144,
                "b": 255,
                "a": 100
            }
        }
    },
    {
        "key": "background",
        "values": {
            "enable": false,
            "backgroundColor": {
                "r": 255,
                "g": 255,
                "b": 255,
                "a": 100
            },
            "image": "",
            "opacity": 100,
            "backgroundSize": "auto",
            "backgroundRepeat": "initial"
        }
    },
    {
        "key": "border",
        "values": {
            "enable": false,
            "borderWidth": 1,
            "borderStyle": "solid",
            "color": {
                "r": 24,
                "g": 144,
                "b": 255,
                "a": 100
            },
            "borderPosition": "border",
            "borderRadius": 0
        }
    },
    {
        "key": "html",
        "values": {
            "enable": false,
            "value": ""
        }
    },
    {
        "key": "css",
        "values": {
            "enable": false,
            "value": ""
        }
    },
    {
        "key": "javascript",
        "values": {
            "enable": false,
            "value": ""
        }
    },
    {
        "key": "griditemsettings",
        "values": {
            "export": false,
            "edit": false,
            "maximize": false
        }
    }
];

// --------------------------------------     designerSettingsConstants     ---------------------

export const designerSettingsConstants = [
  {
      "key": "parameters",
      "values": {
          "enable": true,
          "orientation": 'right',
          "enableApplyButton": true,
          "closeOnApply" : true,
          "floatingFilter" : false,
      }
  }
];

// --------------------------------------     gridSettingsConstants     ---------------------

export const gridSettingsConstants =  [
  {
    key: "breakpoints",
    values: [
      {
        key: "lg",
        name: "Large Screens",
        value: 1200,
        tooltip: "Large Screens(lg)",
      },
      {
        key: "md",
        name: "Medium Screens",
        value: 996,
        tooltip: "Medium Screens(md)",
      },
      {
        key: "sm",
        name: "Small Screens",
        value: 768,
        tooltip: "Small Screens(sm)",
      },
      {
        key: "xs",
        name: "Extra Small Screens",
        value: 480,
        tooltip: "Extra Small Screens(xs)",
      },
      {
        key: "xxs",
        name: "Extra Extra Small Screens",
        value: 240,
        tooltip: "Extra Extra Small Screens(xxs)",
      },
    ],
  },
  {
    key: "columns",
    values: [
      {
        key: "lg",
        name: "Large Screens",
        value: 12,
        tooltip: "Large Screens(lg)",
      },
      {
        key: "md",
        name: "Medium Screens",
        value: 12,
        tooltip: "Medium Screens(md)",
      },
      {
        key: "sm",
        name: "Small Screens",
        value: 6,
        tooltip: "Small Screens(sm)",
      },
      {
        key: "xs",
        name: "Extra Small Screens",
        value: 4,
        tooltip: "Extra Small Screens(xs)",
      },
      {
        key: "xxs",
        name: "Extra Extra Small Screens",
        value: 2,
        tooltip: "Extra Extra Small Screens(xxs)",
      },
    ],
  },
  {
    key: "grid",
    values: {
      autoSize: true,
      compactType: null,
      rowHeight: 100,
      isDroppable: false,
      preventCollision: true,
      measureBeforeMount: false,
      isDraggable: true,
      isResizable: true,
      horizontalMargin: 10,
      verticalMargin:10,
      containerMarginHorizontal:0,
      containerMarginVertical:0,
      allowOverlap: false,
    },
  },
  {
    key: "header",
    values: {
      enable: false,
      title: "<p><br></p>",
      placeholder: "Edit/Add your header content here",
      link: "",
      backgroundColor: { r: 255, g: 255, b: 255, a: 100 },
      position: "left",
      customColor:{ r: 255, g: 255, b: 255, a: 100 },
    },
  },
  {
    key: "shadow",
    values: {
      enable: false,
      xOffset: 0,
      yOffset: 0,
      blur: 0,
      spread: 0,
      color: { r: 255, g: 255, b: 255, a: 100 },
    },
  },
  {
    key: "background",
    values: {
      enable: false,
      backgroundColor: { r: 255, g: 255, b: 255, a: 100 },
      image: "",
      opacity: 100,
      backgroundSize: "auto",
      backgroundRepeat: "initial",
    },
  },
  {
    key: "border",
    values: {
      enable: false,
      borderWidth: 1,
      borderStyle: "solid",
      color: { r: 255, g: 255, b: 255, a: 100 },
      borderPosition: "border",
      borderRadius: 0,
    },
  },
  {
    key: "html",
    values: {
      enable: false,
      value: "",
    },
  },
  {
    key: "css",
    values: {
      enable: false,
      value: "",
    },
  },
  {
    key: "javascript",
    values: {
      enable: false,
      value: "",
    },
  },
]

const removeColumnsBreakpointsConstants = ({constantData= [], data= []}) => {
  const newArr = [];
  data.forEach(dataObj => {
  const reqConstObj = constantData.find(ele => ele.key === dataObj.key);
  if(reqConstObj) {
    if(reqConstObj.value !== dataObj.value) {
      newArr.push(dataObj);
    }
  } else {
    newArr.push(dataObj)
  }
  })
  return newArr;
}

export const removeDesignerConstants = ({constantData=[], apiData=[]}) => {
    let newData = cloneDeep(apiData);
    // newData = newData.filter(ele => (ele.key !== 'columns') && (ele.key !== 'breakpoints'))
    newData.forEach(obj => {
        const reqObjConst = constantData.find(ele => ele.key === obj.key);
        if(reqObjConst) {
          if(reqObjConst.key === 'breakpoints' || reqObjConst.key === 'columns') {
            obj.values = removeColumnsBreakpointsConstants({constantData: reqObjConst.values, data: obj.values});
          } else {
              Object.entries(reqObjConst.values).forEach(arr => {
                if(typeof arr[1] === 'object' && !Array.isArray(arr[1]) && arr[1] !== null) {
                    Object.entries(arr[1]).forEach(propArr => {
                        if(obj.values[arr[0]][propArr[0]] === propArr[1]) {
                          delete obj.values[arr[0]][propArr[0]]
                        }
                    })
                    if(!Object.keys(obj.values[arr[0]]).length) {
                        delete obj.values[arr[0]]
                    }
                } else {
                    if(obj.values[arr[0]] === arr[1]) {
                        delete obj.values[arr[0]];
                    }
                }
              })
          }
        }
    })
    return newData.filter(ele => (Array.isArray(ele.values) ? ele.values.length : Object.keys(ele.values).length));
};

const appendColumnsBreakpointsConstants = ({constantData= [], data= []}) => {
  constantData.forEach(constObj => {
    const dataObj = data.find(ele => ele.key === constObj.key);
    if(!dataObj) {
      data.push(constObj);
    }
  })
  return data;
}

export const appendDesignerConstants = ({constantData=[], apiData=[]}) => {
    if(!apiData.length) {
        return constantData;
    }
    const newData = cloneDeep(apiData);
    constantData.forEach(constObj => {
        let reqProp = newData.find(ele => ele.key === constObj.key);
        // reqProp.values ={...reqProp.values};
        if(reqProp) {
          if(reqProp.key === 'breakpoints' || reqProp.key === 'columns') {
            reqProp.values = appendColumnsBreakpointsConstants({constantData: constObj.values, data: reqProp.values})
          } else {
            reqProp.values ={...reqProp.values};
            Object.entries(constObj.values).forEach(arr => {
              if(!(arr[0] in reqProp.values)) {
                reqProp.values[arr[0]] = arr[1];
                // reqProp = {...reqProp, values: {...reqProp.values, [arr[0]]: arr[1]}}
              } else {
                  if(typeof arr[1] === 'object' && !Array.isArray(arr[1]) && arr[1] !== null) {
                    Object.entries(arr[1]).forEach(propArr => {
                      if(!(propArr[0] in reqProp.values[arr[0]])) {
                        reqProp.values[arr[0]][propArr[0]] = propArr[1];
                        // reqProp = {...reqProp, values: {...reqProp.values, [arr[0]]: {...reqProp.values[arr[0]], [propArr[0]]: propArr[1]} }};
                      }
                    })
                } else {
                  if(!(arr[0] in reqProp.values)) {
                    // reqProp = {...reqProp, values: {...reqProp.values, [arr[0]]: arr[1]}}
                        reqProp.values[arr[0]] = arr[1];
                  }
                }
              }
            })
          }
        } else {
            newData.push(constObj);
        }
    })

    // console.log(newData)
    return newData;
}

export const layoutConstantData = {
    // "w": 2,
    // "h": 2,
    // "x": 3,
    // "y": 0,
    // "i": "item-RsFRs",
    // "moved": false,
    // "static": false,
    "filterCounter": 0,
    "parameterDrawerStatus": false,
    "designerSettings": [
      {
        "key": "parameters",
        "values": {
          "enable": true,
          "orientation": "right",
          "enableApplyButton": true,
          "closeOnApply" : true,
          "floatingFilter" : false,
        }
      }
    ],
    "previewMode": false,
    "designerMode": "create",
    "dashboardVariables": {},
    "dashboardUUID": "",
    "variables": [],
    "components": {},
    "dashboardConfig": {
      "id": "dashboard"
    },
    "css": "",
    "script": "",
    "printOptions": {
      "templateId": "Dashboard",
      "title": "Dashboard"
    },
    "componentSettings": {},
    "toggleIframes": false,
    "gridItemsData": [
      {
        "id": "item-RsFRs",
        "compType": "dashboard-designer-component",
        "isGrouped": false,
        "isSaved": false,
        "initialPosition": {
          "x": 0,
          "y": 0,
          "w": 2,
          "h": 2,
          "i": "item-RsFRs",
          "static": false
        },
        "gridItemConfig": [
          {
            "key": "header",
            "values": {
              "enable": true,
              "title": "Report_1",
              "placeholder": "Edit/Add your header content here",
              "link": "",
              "backgroundColor": {
                "r": 24,
                "g": 144,
                "b": 255,
                "a": 100
              },
              "position": "left",
              "customColor": {
                "r": 255,
                "g": 255,
                "b": 255,
                "a": 100
              },
              "enableTooltip": false,
              "tooltipText": ""
            }
          },
          {
            "key": "shadow",
            "values": {
              "enable": false,
              "xOffset": 1,
              "yOffset": 1,
              "blur": 1,
              "spread": 1,
              "color": {
                "r": 24,
                "g": 144,
                "b": 255,
                "a": 100
              }
            }
          },
          {
            "key": "background",
            "values": {
              "enable": false,
              "backgroundColor": {
                "r": 255,
                "g": 255,
                "b": 255,
                "a": 100
              },
              "image": "",
              "opacity": 100,
              "backgroundSize": "auto",
              "backgroundRepeat": "initial"
            }
          },
          {
            "key": "border",
            "values": {
              "enable": false,
              "borderWidth": 1,
              "borderStyle": "solid",
              "color": {
                "r": 24,
                "g": 144,
                "b": 255,
                "a": 100
              },
              "borderPosition": "border",
              "borderRadius": 0
            }
          },
          {
            "key": "html",
            "values": {
              "enable": false,
              "value": ""
            }
          },
          {
            "key": "css",
            "values": {
              "enable": false,
              "value": ""
            }
          },
          {
            "key": "javascript",
            "values": {
              "enable": false,
              "value": ""
            }
          },
          {
            "key": "griditemsettings",
            "values": {
              "export": false,
              "edit": false,
              "maximize": false
            }
          }
        ],
        "reportInfo": {
          "file": {
            "path": "Sadakshya/Report_1.hr",
            "name": "Report_1.hr",
            "title": "Report_1"
          },
          "mode": "dashboard",
          "filters": [],
          "component": "hreport",
          "extension": "hr"
        },
        "filters": [],
        "listeners": [],
        "reportId": "8136c7b6-23be-4d80-b2ea-f4618c7d7f5a",
        "lastModified": 1692869243109
      }
    ],
    "layout": [
      {
        "w": 2,
        "h": 2,
        "x": 3,
        "y": 0,
        "i": "item-RsFRs",
        "moved": false,
        "static": false
      }
    ],
    "dashboardDrawerStatus": false,
    "gridItemDrawerStatus": true,
    "currentGroupId": "",
    "groupId": "background",
    // "gridItemId": "item-RsFRs",
    "drawerPositions": [
      "right",
      "bottom",
      "left",
      "top"
    ],
    "currentDrawerPosition": 0,
    "gridSettings": [
      {
        "key": "breakpoints",
        "values": [
          {
            "key": "lg",
            "name": "Large Screens",
            "value": 1200,
            "tooltip": "Large Screens(lg)"
          },
          {
            "key": "md",
            "name": "Medium Screens",
            "value": 996,
            "tooltip": "Medium Screens(md)"
          },
          {
            "key": "sm",
            "name": "Small Screens",
            "value": 768,
            "tooltip": "Small Screens(sm)"
          },
          {
            "key": "xs",
            "name": "Extra Small Screens",
            "value": 480,
            "tooltip": "Extra Small Screens(xs)"
          },
          {
            "key": "xxs",
            "name": "Extra Extra Small Screens",
            "value": 240,
            "tooltip": "Extra Extra Small Screens(xxs)"
          }
        ]
      },
      {
        "key": "columns",
        "values": [
          {
            "key": "lg",
            "name": "Large Screens",
            "value": 12,
            "tooltip": "Large Screens(lg)"
          },
          {
            "key": "md",
            "name": "Medium Screens",
            "value": 12,
            "tooltip": "Medium Screens(md)"
          },
          {
            "key": "sm",
            "name": "Small Screens",
            "value": 6,
            "tooltip": "Small Screens(sm)"
          },
          {
            "key": "xs",
            "name": "Extra Small Screens",
            "value": 4,
            "tooltip": "Extra Small Screens(xs)"
          },
          {
            "key": "xxs",
            "name": "Extra Extra Small Screens",
            "value": 2,
            "tooltip": "Extra Extra Small Screens(xxs)"
          }
        ]
      },
      {
        "key": "grid",
        "values": {
          "autoSize": true,
          "compactType": null,
          "rowHeight": 100,
          "isDroppable": false,
          "preventCollision": true,
          "measureBeforeMount": false,
          "isDraggable": true,
          "isResizable": true
        }
      },
      {
        "key": "header",
        "values": {
          "enable": false,
          "title": "<p><br></p>",
          "placeholder": "Edit/Add your header content here",
          "link": "",
          "backgroundColor": {
            "r": 255,
            "g": 255,
            "b": 255,
            "a": 100
          },
          "position": "left",
          "customColor": {
            "r": 255,
            "g": 255,
            "b": 255,
            "a": 100
          }
        }
      },
      {
        "key": "shadow",
        "values": {
          "enable": false,
          "xOffset": 0,
          "yOffset": 0,
          "blur": 0,
          "spread": 0,
          "color": {
            "r": 255,
            "g": 255,
            "b": 255,
            "a": 100
          }
        }
      },
      {
        "key": "background",
        "values": {
          "enable": false,
          "backgroundColor": {
            "r": 255,
            "g": 255,
            "b": 255,
            "a": 100
          },
          "image": "",
          "opacity": 100,
          "backgroundSize": "auto",
          "backgroundRepeat": "initial"
        }
      },
      {
        "key": "border",
        "values": {
          "enable": false,
          "borderWidth": 1,
          "borderStyle": "solid",
          "color": {
            "r": 255,
            "g": 255,
            "b": 255,
            "a": 100
          },
          "borderPosition": "border",
          "borderRadius": 0
        }
      },
      {
        "key": "html",
        "values": {
          "enable": false,
          "value": ""
        }
      },
      {
        "key": "css",
        "values": {
          "enable": false,
          "value": ""
        }
      },
      {
        "key": "javascript",
        "values": {
          "enable": false,
          "value": ""
        }
      }
    ],
    "filterItemsData": [],
    "maximizedGridItem": {},
    "maximizingStatus": false,
    "gridIndex": 0,
    "isLoading": false,
    "reportId": "",
    "applyDashboardFilters": null,
    "isSaving": false,
    "expandDesignerDrawers": false,
    "hasUnsavedData": true,
    "savedReportName": "",
    "designerLayout": [
      {
        "w": 2,
        "h": 2,
        "x": 3,
        "y": 0,
        "i": "item-RsFRs",
        "moved": false,
        "static": false
      }
    ],
    "toggleToolsAreaShelf": true,
    // "itemAddedStatus": 1692940903763
}

const dataShouldExcludeFromLayout = ['dashboardUUID', 'savedReportName', 'dashboardVariables', 'printOptions', 'designerSettings', 'gridItemsData', 'layout', 'designerLayout', 'gridSettings', 'drawerPositions', 'designerMode' ];

export const removeLayoutConstants = (data=[]) => {
  data = cloneDeep(data);
  const newArr = [];
  data.forEach(obj => {
    const newObj = {};
    Object.entries(obj).forEach(keyValArr => {
      if(!dataShouldExcludeFromLayout.includes(keyValArr[0])) {
        if(keyValArr[0] in layoutConstantData) {
          if(!Array.isArray(keyValArr[1]) && (typeof keyValArr[1] !== 'object') || (keyValArr[1] === null)) {
              if(layoutConstantData[keyValArr[0]] !== keyValArr[1]) {
                newObj[keyValArr[0]] =  keyValArr[1];
              }
          } else if(Array.isArray(keyValArr[1])) {
            if(keyValArr[1].length) {
              newObj[keyValArr[0]] =  keyValArr[1];
            }
          } else if(typeof keyValArr[1] === 'object') {
            if(Object.keys(keyValArr[1]).length) {
              newObj[keyValArr[0]] = {};
              Object.entries(keyValArr[1]).forEach(arr => {
                if(arr[0] in layoutConstantData[keyValArr[0]]) {
                  if(layoutConstantData[keyValArr[0]][arr[0]] !== arr[1]) {
                    newObj[keyValArr[0]][arr[0]] =  keyValArr[1][arr[0]];
                  }
                } else {
                  newObj[keyValArr[0]][arr[0]] =  keyValArr[1][arr[0]];
                }
              })
              if(!Object.keys(newObj[keyValArr[0]]).length) {
                delete newObj[keyValArr[0]];
              }
            }
          }
        } else {
          newObj[keyValArr[0]] =  keyValArr[1];
        }
      }
    })
    newArr.push(newObj);
  })
  return newArr;
}



export const checkForDefaultValuesInDashboard = (designerSettings = [], apiData = []) => { // for 7970
  let tempDesignerSettings = cloneDeep(designerSettings);

  tempDesignerSettings = tempDesignerSettings.map((item) => {
    const { key = '', values = {} } = item || {};
    if (key === 'parameters') {
      const parameterObj = apiData.find((obj) => obj.key === key);
      if (parameterObj) {
        item.values = {
          ...values,
          closeOnApply: parameterObj?.values?.closeOnApply ?? false,
        }
      } else {
        item.values = {
          ...values,
          closeOnApply: false,
        }
      }
    }

    return item;
  })

  return tempDesignerSettings;
}
