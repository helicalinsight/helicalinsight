import ShortCutText from "../../common/hi-shortcuts/hi-shortcuts";
import { sortingFunctionForContextMenu, addItem } from "./common-functions";

export const freeFloatFilterDefaultSettings = {
  key: "filterComponentSettings",
  values: {
    bringToFront: true,
    transparent: true,
  }
}

export const freeFloatFilterKeyName = "filterComponentSettings";
export const initialConfig = ({ reportName = "", compType, id }) => {
  let config = [
    {
      key: "header",
      values: {
        enable: compType === "dashboard-designer-component",
        title: reportName,
        placeholder: "Edit/Add your header content here",
        link: "",
        backgroundColor: { r: 24, g: 144, b: 255, a: 100 },
        position: "left",
        customColor: { r: 255, g: 255, b: 255, a: 100 },
        enableTooltip: false,
        tooltipText: "",
      },
    },
    {
      key: "shadow",
      values: {
        enable: false,
        xOffset: 1,
        yOffset: 1,
        blur: 1,
        spread: 1,
        color: { r: 24, g: 144, b: 255, a: 100 },
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
        color: { r: 24, g: 144, b: 255, a: 100 },
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
    ...(compType === "dashboard-designer-component"
      ? [
        {
          key: "griditemsettings",
          values: {
            export: false,
            edit: false,
            maximize: false,
          },
        },
      ]
      : []),
  ];

  if (compType === "text") {
    config.push({
      key: "edit",
      values: {
        enable: true,
        text: "<p><br></p>",
        link: "",
        placeholder: "Edit/Add your text content here",
      },
    });
  }
  if (compType === "image") {
    config.push({
      key: "edit",
      values: { enable: true, url: "", opacity: 50, imageSize: "fill", imageRepeat: "initial" },
    });
  }
  if (compType === "tab") {
    config.push({
      key: "edit",
      values: {
        enable: true,
        numberOfTabs: 1,
        tabNames: "Tab 1",
        tabType: "line",
        centered: false,
        activeTab: "Tab 1",
      },
    });
  }
  if (compType === "grouped-component" || compType === "tab") {
    config.push({
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
        verticalMargin: 10,
        containerMarginHorizontal: 0,
        containerMarginVertical: 0,
        allowOverlap: false,
      },
    });
    config.push({
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
      })
  }

  if (compType === "select-dropdown") {
    config.push({
      key: "edit",
      values: {
        enable: true,
        dropdownValues: "",
        dropdownSize: "small",
        bordered: true,
        placement: "bottomLeft",
        allowClear: true,
      },
    })
  }


  if (compType === "filter-component") {
    config.push(freeFloatFilterDefaultSettings)
  }

  return config;
};

const dashboardLevelLiquidJs = [
  "You can access the following variables in any  text or code",
  " input field :-",

  "1. user -  (actualUserName , email , name , profile , roles , organization).",

  "2. filter's name will give the value of the filter.",

  "Examples  :- ",

  " •  {{user.name}}  gives the value of ", "the user's name.",

  " •  {{filter's name}} will give the value ", "of the respective filter.",

  " •  {{user.roles | join }} will give all the roles with spaces between them.",
  " • setVariable , you can use this function in js editor, it will allow you to change the variable value, i.e setVariable('filter's name',['filter value'])"
];

const reportLevelLiquidjs = [
  "You can access the following variables in any  text or code input field :-",
  "1. report  -   ( name,id)",
  "2. user -  (actualUserName ,email ,name ,profile ,roles ,organization)",
  "3. filter's name will give the value of the filter",
  "Examples:-  1. {{user.name}}  gives the value of the user's name.",
  "2. {{filter's name}} will give the value of the respective filter",
  "3. {{user.roles | join }} will give all the user's roles with spaces between them",
  "4. {{report.id}} gives the id of the report"
  // "5. setVariable , you can use this function in js editor, it will allow you to change the variable value, i.e setVariable('booking_platform',['Agent'])"
];

const dropdownLevelLiquidjs = [
  "You can access the following variables in any  text or code input field :-",
  "1. report  -   ( name,id)",
  "2. user -  (actualUserName ,email ,name ,profile ,roles ,organization)",
  "3. filter's name will give the value of the filter",
  "Examples:-  1. {{user.name}}  gives the value of the user's name.",
  "2. {{filter's name}} will give the value of the respective filter",
  "3. {{user.roles | join }} will give all the user's roles with spaces between them",
  "4. {{report.id}} gives the id of the report",
  "5. setVariable , you can use this function in js editor, it will allow you to change the variable value, i.e setVariable('filter's name',['filter value'])",
  "6. getAllVariables , you can use this function in js editor, it will allow you to get all the variables in scope, i.e getAllVariables()"
];

export const getLabelForDashboardGroup = ({ text, label }) => {
  return (
    <ShortCutText {...{ text, menuItem: true, scLocation: "DD" }}>
      {label}
    </ShortCutText>
  );
};

export const getGroup = ({ type, compType, dispatch }) => {
  switch (type) {
    case "gridItem":
      let group = [
        {
          label: getLabelForDashboardGroup({ label: "Header", text: "H" }),
          key: "header",
          shortcuts: [["h"]],
        },
        {
          label: getLabelForDashboardGroup({ label: "Shadow", text: "W" }),
          key: "shadow",
          shortcuts: [["w"]],
        },
        {
          label: getLabelForDashboardGroup({ label: "Background", text: "B" }),
          key: "background",
          shortcuts: [["b"]],
        },
        {
          label: getLabelForDashboardGroup({ label: "Border", text: "O" }),
          key: "border",
          shortcuts: [["o"]],
        },
        {
          label: "Advance",
          key: "advanced",
          children: [
            {
              label: getLabelForDashboardGroup({ label: "HTML", text: "M" }),
              key: "html",
              shortcuts: [["m"]],
            },
            {
              label: getLabelForDashboardGroup({ label: "CSS", text: "C" }),
              key: "css",
              shortcuts: [["c"]],
            },
            {
              label: getLabelForDashboardGroup({ label: "JS", text: "J" }),
              key: "javascript",
              shortcuts: [["j"]],
            },
          ],
        },
        ...(compType === "text" || compType === "image" || compType === "tab" || compType === "select-dropdown"
          ? [
            {
              label: getLabelForDashboardGroup({ label: "Edit", text: "4" }),
              key: "edit",
              shortcuts: [["4"]],
            },
          ]
          : []),
        ...(compType === "dashboard-designer-component"
          ? [
            {
              label: getLabelForDashboardGroup({
                label: "Settings",
                text: "3",
              }),
              key: "griditemsettings",
              shortcuts: [["3"]],
            },
          ]
          : []),
        ...(compType === "grouped-component" || compType === "tab"
          ? [
            {
              label: "Grid Settings",
              key: "gridsettings",
              children: [
                {
                  label: getLabelForDashboardGroup({ label: "Grid", text: "G" }),
                  key: "grid",
                  shortcuts: [["g"]],
                },
                {
                  label: getLabelForDashboardGroup({
                    label: "Breakpoints",
                    text: "K",
                  }),
                  key: "breakpoints",
                  shortcuts: [["k"]],
                },
                {
                  label: getLabelForDashboardGroup({ label: "Columns", text: "U" }),
                  key: "columns",
                  shortcuts: [["u"]],
                },

              ],
            },
          ]
          : []),
        {
          label: getLabelForDashboardGroup({ label: "Alignment", text: "5" }),
          key: "alignment",
          shortcuts: [["5"]],
        },
      ];
      return sortingFunctionForContextMenu(group);
    case "grid":
      let gridGroup = [
        {
          label: getLabelForDashboardGroup({ label: "Header", text: "H" }),
          key: "header",
          shortcuts: [["h"]],
        },
        {
          label: getLabelForDashboardGroup({ label: "Shadow", text: "W" }),
          key: "shadow",
          shortcuts: [["w"]],
        },
        {
          label: getLabelForDashboardGroup({ label: "Background", text: "B" }),
          key: "background",
          shortcuts: [["b"]],
        },
        {
          label: getLabelForDashboardGroup({ label: "Border", text: "O" }),
          key: "border",
          shortcuts: [["o"]],
        },
        {
          label: getLabelForDashboardGroup({ label: "Filters", text: "P" }),
          key: "parameters",
          shortcuts: [["p"]],
        },
        {
          // label: getLabelForDashboardGroup({
          //   label: "Grid Settings",
          //   text: "9",
          // }),
          // shortcuts: [["9"]],
          label: "Grid Settings",
          key: "gridsettings",
          children: [
            {
              label: getLabelForDashboardGroup({
                label: "Breakpoints",
                text: "K",
              }),
              key: "breakpoints",
              shortcuts: [["k"]],
            },
            {
              label: getLabelForDashboardGroup({ label: "Columns", text: "U" }),
              key: "columns",
              shortcuts: [["u"]],
            },
            {
              label: getLabelForDashboardGroup({ label: "Grid", text: "G" }),
              key: "grid",
              shortcuts: [["g"]],
            },
          ],
        },
        {
          label: "Add",
          key: "add",
          children: [
            {
              label: getLabelForDashboardGroup({ label: "Image", text: "1" }),
              key: "image",
              onClick: () => {
                addItem({ dispatch, compType: "image" });
              },
              shortcuts: [["1"]],
            },
            {
              label: getLabelForDashboardGroup({ label: "Text", text: "2" }),
              key: "text",
              onClick: () => {
                addItem({ dispatch, compType: "text" });
              },
              shortcuts: [["2"]],
            },
            {
              label: getLabelForDashboardGroup({ label: "Tab", text: "3" }),
              key: "tab",
              onClick: () => {
                addItem({ dispatch, compType: "tab" });
              },
              shortcuts: [["3"]],
            },
            {
              label: getLabelForDashboardGroup({ label: "HTML", text: "4" }),
              key: "html-component",
              onClick: () => {
                addItem({ dispatch, compType: "html-component" });
              },
              shortcuts: [["4"]],
            },
            {
              label: getLabelForDashboardGroup({ label: "Dropdown", text: "5" }),
              key: "select-dropdown",
              onClick: () => {
                addItem({ dispatch, compType: "select-dropdown" });
              },
              shortcuts: [["5"]],
            },
          ],
        },
        {
          label: "Advance",
          key: "advanced",
          children: [
            {
              label: getLabelForDashboardGroup({ label: "HTML", text: "M" }),
              key: "html",
              shortcuts: [["m"]],
            },
            {
              label: getLabelForDashboardGroup({ label: "CSS", text: "C" }),
              key: "css",
              shortcuts: [["c"]],
            },
            {
              label: getLabelForDashboardGroup({ label: "JS", text: "J" }),
              key: "javascript",
              shortcuts: [["j"]],
            },
          ],
        },
      ];
      return sortingFunctionForContextMenu(gridGroup);
    default:
      return [];
  }
};

export const getInfoItems = ({ type, compType }) => {
  let infoItems = [];
  const reportOrDropdownLevelLiquidjs = compType === "select-dropdown"
    ? dropdownLevelLiquidjs
    : reportLevelLiquidjs;
  switch (type) {
    case "gridItem":
      infoItems = [
        {
          key: "alignment",
          infos: [
            "Define grid attributes, it take inputs in grid unit not in pixel.",
            "For example if columns is 12 then top,left and width take values from 1 to 12.",
            "No overlay",
          ],
        },
        {
          key: "background",
          infos: [
            "The background property sets the background of grid-item.",
            ...reportLevelLiquidjs,
          ],
        },
        {
          key: "border",
          infos: ["Set the styles of grid-item's border"],
        },
        {
          key: "header",
          infos: ["Set the header of grid-item", ...reportLevelLiquidjs],
        },
        {
          key: "shadow",
          infos: ["Set the shadow of grid-item"],
        },
        {
          key: "css",
          infos: ["Apply css on grid-item", ...reportLevelLiquidjs],
        },
        {
          key: "html",
          infos: ["Apply html on grid-item", ...dropdownLevelLiquidjs],
        },
        {
          key: "javascript",
          infos: ["Apply javascript on grid-item", ...(compType === "html-component" ? dropdownLevelLiquidjs : reportLevelLiquidjs)],
        },
        {
          key: "edit",
          infos: ["Set image/text properties", ...reportOrDropdownLevelLiquidjs],
        },
      ];
      return infoItems;
    case "grid":
      infoItems = [
        {
          key: "breakpoints",
          infos: [
            "Breakpoints - breakpoints are pixel values.",
            "When a dashboard reaches those pixel values, a transformation occurs so that the website offers an optimal user experience.",
          ],
        },
        {
          key: "columns",
          infos: [
            "Grid columns - The screens will be divided among how many columns.",
          ],
        },
        {
          key: "grid",
          infos: ["Grid - Configure the grid"],
        },

        {
          key: "filters",
          infos: [
            "This is a filter pane.",
            "All the filters/parameters will be added in this pane.",
          ],
        },
        {
          key: "background",
          infos: [
            "The background property sets the background of dashboard.",
            ...dashboardLevelLiquidJs,
          ],
        },
        {
          key: "border",
          infos: ["Set the styles of dashboard's border"],
        },
        {
          key: "header",
          infos: ["Set the header of dashboard", ...dashboardLevelLiquidJs],
        },
        {
          key: "shadow",
          infos: ["Set the shadow of dashboard"],
        },
        {
          key: "css",
          infos: ["Apply css on dashboard", ...dashboardLevelLiquidJs],
        },
        {
          key: "html",
          infos: ["Apply html on dashboard", ...dashboardLevelLiquidJs],
        },
        {
          key: "javascript",
          infos: ["Apply javascript on dashboard", ...dashboardLevelLiquidJs],
        },
      ];
      return infoItems;
    default:
      return infoItems;
  }
};

export const getInfoPanelItems = ({ type, gridItemId }) => {
  let infoPanelItems = [];
  switch (type) {
    case "grid":
      infoPanelItems = [
        {
          key: "html",
          info: "Component id :  hi-grid",
          copyable: { text: "#hi-grid" },
          strong: true,
        },
        {
          key: "css",
          info: "Component id :  hi-grid",
          copyable: { text: "#hi-grid" },
          strong: true,
        },
        {
          key: "javascript",
          info: "Component id :  hi-grid",
          copyable: { text: "#hi-grid" },
          strong: true,
        },
      ];
      return infoPanelItems;
    case "gridItem":
      infoPanelItems = [
        {
          key: "html",
          info: `Component id :  ${gridItemId}`,
          copyable: { text: `#${gridItemId}` },
          strong: true,
        },
        {
          key: "css",
          info: `Component id :  ${gridItemId}`,
          copyable: { text: `#${gridItemId}` },
          strong: true,
        },
        {
          key: "javascript",
          info: `Component id :  ${gridItemId}`,
          copyable: { text: `#${gridItemId}` },
          strong: true,
        },
      ];
      return infoPanelItems;
    default:
      return infoPanelItems;
  }
};

export const HIBASEVERSION = '5.2.3.2023 GA'