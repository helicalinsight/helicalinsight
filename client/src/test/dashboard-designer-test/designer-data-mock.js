const designer_initial_view_state = JSON.parse(
  `
  {
    "parameterDrawerStatus": false,
    "designerSettings": [
      {
        "key": "parameters",
        "values": {
          "enable": false,
          "orientation": "right"
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
    "gridWidthOption": 100,
    "toggleIframes": false,
    "gridItemsData": [],
    "layout": [],
    "dashboardDrawerStatus": false,
    "gridItemDrawerStatus": false,
    "currentGroupId": "",
    "groupId": "background",
    "gridItemId": "",
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
            "name": "lg",
            "value": 1200,
            "tooltip": "Large Screens"
          },
          {
            "name": "md",
            "value": 996,
            "tooltip": "Medium Screens"
          },
          {
            "name": "sm",
            "value": 768,
            "tooltip": "Small Screens"
          },
          {
            "name": "xs",
            "value": 480,
            "tooltip": "Extra Small Screens"
          },
          {
            "name": "xxs",
            "value": 480,
            "tooltip": "Extra Extra Small Screens"
          }
        ]
      },
      {
        "key": "columns",
        "values": [
          {
            "name": "lg",
            "value": 100,
            "tooltip": "Large Screens"
          },
          {
            "name": "md",
            "value": 100,
            "tooltip": "Medium Screens"
          },
          {
            "name": "sm",
            "value": 100,
            "tooltip": "Small Screens"
          },
          {
            "name": "xs",
            "value": 100,
            "tooltip": "Extra Small Screens"
          },
          {
            "name": "xxs",
            "value": 100,
            "tooltip": "Extra Extra Small Screens"
          }
        ]
      },
      {
        "key": "grid",
        "values": {
          "autoSize": true,
          "compactType": "",
          "rowHeight": 1,
          "isDroppable": true,
          "preventCollision": true,
          "measureBeforeMount": true,
          "isDraggable": true,
          "isResizable": true
        }
      },
      {
        "key": "header",
        "values": {
          "enable": false,
          "title": "",
          "placeholder": "Edit/Add your header content here",
          "link": "",
          "backgroundColor": "#fff"
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
          "color": "#fff"
        }
      },
      {
        "key": "background",
        "values": {
          "enable": false,
          "backgroundColor": "#fff",
          "image": ""
        }
      },
      {
        "key": "border",
        "values": {
          "enable": false,
          "borderWidth": 1,
          "borderStyle": "none",
          "color": "#fff"
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
    "filterItemsData": []
  }
    `
);

const mocks = {
  designer_initial_view_state,
};
export default mocks;
