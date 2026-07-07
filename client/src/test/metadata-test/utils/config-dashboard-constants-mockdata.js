import { designerStateComponenetsInitialPositionConstants, gridItemConfigConstants, gridSettingsConstants, layoutConstantData } from "../../../components/hi-dashboard-designer/utils/config-dashboard-gridSettings";

const gridItemConfigWithHeaderChange = [
    {
        "key": "header",
        "values": {
            "enableTooltip": true,
            "tooltipText": "xyz",
            "enable": false,
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
            }
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
]

const gridSettingsWithHeaderChange = [
    {
        "key": "header",
        "values": {
            "enable": true,
            "link": "xyz",
            "position": "right",
            "title": "<p><br></p>",
            "placeholder": "Edit/Add your header content here",
            "backgroundColor": {
                "r": 255,
                "g": 255,
                "b": 255,
                "a": 100
            },
            "customColor": {
                "r": 255,
                "g": 255,
                "b": 255,
                "a": 100
            }
        }
    },
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
            "allowOverlap": false,
            "autoSize": true,
            "compactType": null,
            "containerMarginHorizontal": 0,
            "containerMarginVertical": 0,
            "horizontalMargin": 10,
            "rowHeight": 100,
            "isDroppable": false,
            "preventCollision": true,
            "measureBeforeMount": false,
            "isDraggable": true,
            "isResizable": true,
            "verticalMargin": 10,
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
]

export const gridItemConfigTestData = {append: [
    {apiData: [], result: gridItemConfigConstants, constantData: gridItemConfigConstants},
    {apiData: [ {"key": "header", "values": {
        "enableTooltip": true,
        "tooltipText": "xyz",
        "enable": false
    }}], result: gridItemConfigWithHeaderChange, constantData: gridItemConfigConstants}
], remove:  [
    {apiData: gridItemConfigConstants, result: [], constantData: gridItemConfigConstants},
    {apiData:gridItemConfigWithHeaderChange, result: [ {"key": "header", "values": {
        "enableTooltip": true,
        "tooltipText": "xyz",
        "enable": false
    }}], constantData: gridItemConfigConstants},
   
]}

const columnBreakPointsConstantsResult = [
    {
        "key": "breakpoints",
        "values": [
            {
                "key": "md",
                "name": "Medium Screens",
                "value": 258,
                "tooltip": "Medium Screens(md)"
            },
            {
                "key": "sm",
                "name": "Small Screens",
                "value": 256,
                "tooltip": "Small Screens(sm)"
            },
            {
                "key": "lg",
                "name": "Large Screens",
                "value": 1200,
                "tooltip": "Large Screens(lg)"
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
                "key": "md",
                "name": "Medium Screens",
                "value": 120,
                "tooltip": "Medium Screens(md)"
            },
            {
                "key": "sm",
                "name": "Small Screens",
                "value": 60,
                "tooltip": "Small Screens(sm)"
            },
            {
                "key": "lg",
                "name": "Large Screens",
                "value": 12,
                "tooltip": "Large Screens(lg)"
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
            "allowOverlap": false,
            "autoSize": true,
            "compactType": null,
            "containerMarginHorizontal": 0,
            "containerMarginVertical": 0,
            "horizontalMargin": 10,
            "rowHeight": 100,
            "isDroppable": false,
            "preventCollision": true,
            "measureBeforeMount": false,
            "isDraggable": true,
            "isResizable": true,
            "verticalMargin": 10,
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
]

export const gridSettingsTestData =  {append:[
    {apiData: [], result: gridSettingsConstants, constantData: gridSettingsConstants},
    {apiData: [ {
        key: "header",
        values: {
          enable: true,
          link: "xyz",
          position: "right",
        },
      }], result: gridSettingsWithHeaderChange, constantData: gridSettingsConstants},
      {apiData: [
        {
            "key": "breakpoints",
            "values": [
                {
                    "key": "md",
                    "name": "Medium Screens",
                    "value": 258,
                    "tooltip": "Medium Screens(md)"
                },
                {
                    "key": "sm",
                    "name": "Small Screens",
                    "value": 256,
                    "tooltip": "Small Screens(sm)"
                }
            ]
        },
        {
            "key": "columns",
            "values": [
                {
                    "key": "md",
                    "name": "Medium Screens",
                    "value": 120,
                    "tooltip": "Medium Screens(md)"
                },
                {
                    "key": "sm",
                    "name": "Small Screens",
                    "value": 60,
                    "tooltip": "Small Screens(sm)"
                }
            ]
        }
      ], result: columnBreakPointsConstantsResult, constantData: gridSettingsConstants},
], remove:  [
    {apiData: gridSettingsConstants, result: [], constantData: gridSettingsConstants},
    {apiData: gridSettingsWithHeaderChange, result: [ {
        key: "header",
        values: {
          enable: true,
          link: "xyz",
          position: "right",
        },
      }], constantData: gridSettingsConstants},
      {apiData: 
        [{
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
            value: 258,
            tooltip: "Medium Screens(md)",
          },
          {
            key: "sm",
            name: "Small Screens",
            value: 256,
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
      }, {
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
            value: 120,
            tooltip: "Medium Screens(md)",
          },
          {
            key: "sm",
            name: "Small Screens",
            value: 60,
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
      }], result: [
        {
            "key": "breakpoints",
            "values": [
                {
                    "key": "md",
                    "name": "Medium Screens",
                    "value": 258,
                    "tooltip": "Medium Screens(md)"
                },
                {
                    "key": "sm",
                    "name": "Small Screens",
                    "value": 256,
                    "tooltip": "Small Screens(sm)"
                }
            ]
        },
        {
            "key": "columns",
            "values": [
                {
                    "key": "md",
                    "name": "Medium Screens",
                    "value": 120,
                    "tooltip": "Medium Screens(md)"
                },
                {
                    "key": "sm",
                    "name": "Small Screens",
                    "value": 60,
                    "tooltip": "Small Screens(sm)"
                }
            ]
        }
    ], constantData: gridSettingsConstants}
]}

const initialPositionWithDataChangeTest = {
    "x": 0,
    "y": 0,
    "w": 3,
    "h": 2,
    "static": true
}

export const initialPositionTestData = {
    append: [
        {data: {}, result: designerStateComponenetsInitialPositionConstants},
        {data: { "w": 3, "static": true }, result: initialPositionWithDataChangeTest}
    ],
    remove: [
        {data: designerStateComponenetsInitialPositionConstants, result: {}},
        {data: initialPositionWithDataChangeTest, result: { "w": 3, "static": true }}
    ]
}

const layoutWithDashboardVariableChange = [
    {
        "w": 2,
        "h": 2,
        "x": 3,
        "y": 0
    }
]

export const designerLayoutData = [
    { data: [{...layoutConstantData, "w": 2,"h": 2,"x": 3,"y": 0}], result: layoutWithDashboardVariableChange },
    { data: [layoutConstantData], result: [{}] }
]