import { fallsInBreakpoint, getComponentHeight, getComponentWidth } from "./common-functions";
import { getGridItem } from "./recursive-functions";

export const getPropertyPaneItemsConfig = ({
  array,
  id,
  compType,
  type,
  getGridItemLayoutObj,
}) => {
  let data = array;
  let headerValues;
  let shadowValues;
  let backgroundValues;
  let borderValues;
  let htmlValues;
  let cssValues;
  let jsValues;
  let parameterValues;
  let gridValues;
  let settingsValues;
  let editValues;
  let container;
  let columnValues;
  let breakpointValues;
  if (id) {
    data = data ? getGridItem(data, id) : null;
    container = "grid-item";
    headerValues = data?.gridItemConfig?.find(
      (item) => item.key === "header"
    )?.values;
    shadowValues = data?.gridItemConfig?.find(
      (item) => item.key === "shadow"
    )?.values;
    backgroundValues = data?.gridItemConfig?.find(
      (item) => item.key === "background"
    )?.values;
    borderValues = data?.gridItemConfig?.find(
      (item) => item.key === "border"
    )?.values;
    htmlValues = data?.gridItemConfig?.find(
      (item) => item.key === "html"
    )?.values;
    cssValues = data?.gridItemConfig?.find(
      (item) => item.key === "css"
    )?.values;
    jsValues = data?.gridItemConfig?.find(
      (item) => item.key === "javascript"
    )?.values;
    settingsValues = data?.gridItemConfig?.find(
      (item) => item.key === "griditemsettings"
    )?.values;
    editValues = data?.gridItemConfig?.find(
      (item) => item.key === "edit"
    )?.values;
    gridValues = data?.gridItemConfig?.find(
      (item) => item.key === "grid"
    )?.values;
    columnValues = data?.gridItemConfig?.find(
      (item) => item.key === "columns"
    )?.values;
    breakpointValues = data?.gridItemConfig?.find(
      (item) => item.key === "breakpoints"
    )?.values;
    //  = data?.gridItemConfig?.find(
    //   (italignmentValuesem) => item.key === "alignment"
    // )?.values;
  } else {
    container = "dashboard";
    headerValues = data.find((item) => item.key === "header")?.values;
    shadowValues = data.find((item) => item.key === "shadow")?.values;
    backgroundValues = data.find((item) => item.key === "background")?.values;
    borderValues = data.find((item) => item.key === "border")?.values;
    htmlValues = data.find((item) => item.key === "html")?.values;
    cssValues = data.find((item) => item.key === "css")?.values;
    jsValues = data.find((item) => item.key === "javascript")?.values;
    parameterValues = data.find((item) => item.key === "parameters")?.values;
    gridValues = data.find((item) => item.key === "grid")?.values;
    columnValues = data.find((item) => item.key === "columns")?.values;
    breakpointValues = data.find((item) => item.key === "breakpoints")?.values;
  }
  return type.map((item) => {
    switch (item) {
      case "header":
        return [
          {
            key: "enable",
            label: "Enable",
            value: headerValues?.enable,
            elementType: "Switch",
            groupId: "header",
            tooltip: "Enable/Disable the changes.Re Enable if not applied",
          },
          {
            key: "title",
            label: "Title",
            placeholder: headerValues?.placeholder,
            value: headerValues?.title,
            customColor:headerValues?.customColor,
            elementType: "TextEditor",
            groupId: "header",
            tooltip: "Set the title property.",
          },
          {
            key: "link",
            label: "Link",
            value: headerValues?.link,
            elementType: "Input",
            groupId: "header",
            tooltip:
              "Set any link.The set link will open the link in a new tab.Add https:// for external links.",
          },
          {
            key: "backgroundColor",
            label: "Background Color",
            value: headerValues?.backgroundColor,
            elementType: "ColorPicker",
            groupId: "header",
            tooltip: "Set the background color",
          },
          {
            key: "customColor",
            label: "Color",
            value: headerValues?.customColor,
            elementType: "ColorPicker",
            groupId: "header",
            tooltip: "Use this option to customize text editor color pallet with advanced color options",
          },
          ...(container === "grid-item"
            ? [
              {
                key: "enableTooltip",
                label: "Enable Tooltip",
                value: headerValues?.enableTooltip,
                elementType: "Switch",
                groupId: "header",
                tooltip: "Enable Tooltip",
              },
              {
                key: "tooltipText",
                label: "Tooltip Text",
                value: headerValues?.tooltipText,
                elementType:"Input",
                groupId: "header",
                tooltip: "Set the Tooltip Text",
              },
              ]
            : []),
           
        ];
      case "shadow":
        return [
          {
            key: "enable",
            label: "Enable",
            value: shadowValues?.enable,
            elementType: "Switch",
            groupId: "shadow",
            tooltip: "Enable/Disable the changes.Re Enable if not applied",
          },
          {
            key: "xOffset",
            label: "X Offset",
            value: shadowValues?.xOffset,
            elementType: "InputNumber",
            groupId: "shadow",
            tooltip: `Set the horizontal distance of ${container}'s shadow`,
          },
          {
            key: "yOffset",
            label: "Y Offset",
            value: shadowValues?.yOffset,
            elementType: "InputNumber",
            groupId: "shadow",
            tooltip: `Set the vertical distance of ${container}'s shadow`,
          },
          {
            key: "blur",
            label: "Blur",
            value: shadowValues?.blur,
            elementType: "InputNumber",
            groupId: "shadow",
            tooltip: `Set the blur of ${container}'s shadow.The larger this value, the bigger the blur, so the shadow becomes bigger and lighter. Negative values are not allowed. If not specified, it will be 0 (the shadow's edge is sharp).`,
          },
          {
            key: "spread",
            label: "Spread",
            value: shadowValues?.spread,
            elementType: "InputNumber",
            groupId: "shadow",
            tooltip: `Set the spread of ${container}'s shadow.Positive values will cause the shadow to expand and grow bigger, negative values will cause the shadow to shrink. If not specified, it will be 0 (the shadow will be the same size as the element).`,
          },
          {
            key: "color",
            label: "Color",
            value: shadowValues?.color,
            elementType: "ColorPicker",
            groupId: "shadow",
            tooltip: `Set the color of ${container}'s shadow.`,
          },
        ];
      case "background":
        return [
          {
            key: "enable",
            label: "Enable",
            value: backgroundValues?.enable,
            elementType: "Switch",
            groupId: "background",
            tooltip: "Enable/Disable the changes.Re Enable if not applied.",
          },
          {
            key: "backgroundColor",
            label: "Color",
            value: backgroundValues?.backgroundColor,
            elementType: "ColorPicker",
            groupId: "background",
            tooltip: `Set the color of ${container}'s background.`,
          },
          {
            key: "image",
            label: "Image",
            value: backgroundValues?.image,
            elementType: "Input",
            groupId: "background",
            tooltip: `Set image as ${container}'s background. Provide external urls or local images.`,
          },
          {
            key: "opacity",
            label: "Opacity",
            min: 0,
            max: 100,
            step: 1,
            value: backgroundValues?.opacity,
            elementType: "Slider",
            groupId: "background",
            tooltip:
              "Set image opacity.This property can take a value from 0 - 100. The lower the value, the more transparent:",
          },
          {
            key: "backgroundSize",
            label: "Image Size",
            values: [
              { key: "auto", label: "Auto" },
              { key: "contain", label: "Contain" },
              { key: "cover", label: "Cover" },
              { key: "initial", label: "Initial" },
              { key: "inherit", label: "inherit" },
            ],
            value: backgroundValues?.backgroundSize,
            elementType: "Select",
            groupId: "background",
            tooltip: `Set the size of ${container}'s background image`,
          },
          {
            key: "backgroundRepeat",
            label: "Image Repeat",
            values: [
              { key: "repeat", label: "Repeat" },
              { key: "repeat-x", label: "Repeat-x" },
              { key: "repeat-y", label: "Repeat-y" },
              { key: "no-repeat", label: "No repeat" },
              { key: "initial", label: "Initial" },
              { key: "inherit", label: "inherit" },
              { key: "space", label: "Space" },
              { key: "round", label: "round" },
            ],
            value: backgroundValues?.backgroundRepeat,
            elementType: "Select",
            groupId: "background",
            tooltip: `Set the repeatition of ${container}'s background image`,
          },
        ];
      case "border":
        return [
          {
            key: "enable",
            label: "Enable",
            value: borderValues?.enable,
            elementType: "Switch",
            groupId: "border",
            tooltip: "Enable/Disable the changes.Re Enable if not applied.",
          },
          {
            key: "borderWidth",
            label: "Border Width",
            value: borderValues?.borderWidth,
            elementType: "InputNumber",
            groupId: "border",
            tooltip: `Set the width of ${container}'s border`,
          },
          {
            key: "borderStyle",
            label: "Border Style",
            values: [
              { key: "dotted", label: "dotted" },
              { key: "dashed", label: "dashed" },
              { key: "solid", label: "solid" },
              { key: "double", label: "double" },
              { key: "groove", label: "groove" },
              { key: "ridge", label: "ridge" },
              { key: "inset", label: "inset" },
              { key: "outset", label: "outset" },
              { key: "none", label: "none" },
              { key: "hidden", label: "hidden" },
            ],
            value: borderValues?.borderStyle,
            elementType: "Select",
            groupId: "border",
            tooltip: `Set the style of ${container}'s border`,
          },
          {
            key: "color",
            label: "Color",
            value: borderValues?.color,
            elementType: "ColorPicker",
            groupId: "border",
            tooltip: `Set the color of ${container}'s border`,
          },
          {
            key: "borderPosition",
            label: "Border Position",
            values: [
              { key: "border", label: "All Sides" },
              { key: "borderTop", label: "Top" },
              { key: "borderLeft", label: "Left" },
              { key: "borderBottom", label: "Bottom" },
              { key: "borderRight", label: "Right" },
            ],
            value: borderValues?.borderPosition,
            elementType: "Select",
            multiSelect: true,
            groupId: "border",
            tooltip: `Set the position of ${container}'s border`,
          },
          {
            key: "borderRadius",
            label: "Border Radius",
            value: borderValues?.borderRadius,
            elementType: "InputNumber",
            groupId: "border",
            tooltip: `Set the border radius of ${container}'s border`,
          },
        ];
      case "html":
        return [
          {
            key: "enable",
            label: "Enable",
            value: htmlValues?.enable,
            elementType: "Switch",
            groupId: "html",
            tooltip: "Enable/Disable the changes.Re Enable if not applied.",
          },
          {
            key: "value",
            label: "HTML Editor",
            mode: "xml",
            value: htmlValues?.value,
            elementType: "CodeEditor",
            groupId: "html",
            tooltip: `Add html to ${container}`,
          },
        ];
      case "css":
        return [
          {
            key: "enable",
            label: "Enable",
            value: cssValues?.enable,
            elementType: "Switch",
            groupId: "css",
            tooltip: "Enable/Disable the changes.Re Enable if not applied.",
          },
          {
            key: "value",
            label: "CSS Editor",
            mode: "css",
            value: cssValues?.value,
            elementType: "CodeEditor",
            groupId: "css",
            tooltip: `Add css to ${container}`,
          },
        ];
      case "javascript":
        return [
          {
            key: "enable",
            label: "Enable",
            value: jsValues?.enable,
            elementType: "Switch",
            groupId: "javascript",
            tooltip: "Enable/Disable the changes.Re Enable if not applied.",
          },
          {
            key: "value",
            label: "JS Editor",
            mode: "javascript",
            showJSDrawerIcon: container === "dashboard" || ["tab","text","image"].includes(compType),
            isDashboard: container === "dashboard",
            value: jsValues?.value,
            elementType: "CodeEditor",
            groupId: "javascript",
            tooltip: `Add javascript to ${container}`,
          },
        ];
      case "parameters":
        return [
          {
            key: "enable",
            label: "Enable",
            value: parameterValues?.enable,
            elementType: "Switch",
            groupId: "parameters",
            tooltip: "Enable/Disable filter pane.",
          },
          {
            key: "orientation",
            label: "Orientation",
            value: parameterValues?.orientation,
            values: [
              { key: "top", label: "Top" },
              { key: "bottom", label: "Bottom" },
              { key: "left", label: "Left" },
              { key: "right", label: "Right" },
            ],
            elementType: "Radio",
            groupId: "parameters",
            tooltip:
              "Set the position of the filter pane (where it should be shown).",
          },
          {
            key: "enableApplyButton",
            label: "Enable Apply Button",
            value: parameterValues?.enableApplyButton,
            elementType: "Switch",
            groupId: "parameters",
            tooltip: "Enable/Disable Apply Button for filter pane.",
          },
           {
            key: "closeOnApply",
            label: "Close On Apply",
            value: parameterValues?.closeOnApply,
            elementType: "Switch",
            groupId: "parameters",
            tooltip: "Close the filter panel on clicking 'Apply'.",
          },
          {
            key: "floatingFilter",
            label: "Free Float",
            value: parameterValues?.floatingFilter,
            elementType: "Switch",
            groupId: "parameters",
            tooltip: "If this setting is enabled, it will be used as the grid item. Note that the 'close on apply' setting will not function if this is enabled.",
          },
        ];
      case "grid":
        return [
          {
            key: "autoSize",
            label: "Auto Size",
            value: gridValues?.autoSize,
            elementType: "Switch",
            groupId: "grid",
            tooltip:
              "If true, the container height swells and contracts to fit contents.",
          },
          {
            key: "isDraggable",
            label: "Draggable Grid Items",
            value: gridValues?.isDraggable,
            elementType: "Switch",
            groupId: "grid",
          },
          {
            key: "compactType",
            label: "Compact Type",
            value: gridValues?.compactType,
            values: [
              { key: "vertical", label: "Vertical" },
              { key: "horizontal", label: "Horizontal" },
              { key: null, label: "Free" },
            ],
            elementType: "Radio",
            groupId: "grid",
            tooltip:
              "In which direction the grids will move in case of responsiveness.",
          },
          {
            key: "rowHeight",
            label: "Row Height",
            value: gridValues?.rowHeight,
            elementType: "InputNumber",
            groupId: "grid",
            tooltip: "Grid rows have a static height",
          },

          {
            key: "preventCollision",
            label: "Prevent Collision",
            value: gridValues?.preventCollision,
            elementType: "Switch",
            groupId: "grid",
            tooltip:
              "If true, grid items won't change position when being dragged over",
          },
          {
            key: "horizontalMargin",
            label: "Horizontal Margin",
            value: gridValues?.horizontalMargin,
            elementType: "InputNumber",
            groupId: "grid",
            tooltip: "Set the horizontal space between grid items.",
          },
          {
            key: "verticalMargin",
            label: "Vertical Margin",
            value: gridValues?.verticalMargin,
            elementType: "InputNumber",
            groupId: "grid",
            tooltip: "Set the vertical space between grid items.",
          },
          {
            key: "containerMarginHorizontal",
            label: "Container Margin Horizontal",
            value: gridValues?.containerMarginHorizontal,
            elementType: "InputNumber",
            groupId: "grid",
            tooltip: "Set the horizontal space between container.",
          },
          {
            key: "containerMarginVertical",
            label: "Container Margin Vertical",
            value: gridValues?.containerMarginVertical,
            elementType: "InputNumber",
            groupId: "grid",
            tooltip: "Set the vertical space between container.",
          },
          {
            key: "allowOverlap",
            label: "Allow Overlap",
            value: gridValues?.allowOverlap,
            elementType: "Switch",
            groupId: "grid",
            tooltip: "Allow the grid items to overlap.",
          },
        ];
      case "griditemsettings":
        return [
          {
            key: "export",
            label: "Export",
            value: settingsValues?.export,
            elementType: "Switch",
            groupId: "griditemsettings",
            tooltip: "Enable/Disable export option for grid-item.",
          },
          {
            key: "edit",
            label: "Edit",
            value: settingsValues?.edit,
            elementType: "Switch",
            groupId: "griditemsettings",
            tooltip: "Enable/Disable edit option for grid-item",
          },
          {
            key: "maximize",
            label: "Maximize",
            value: settingsValues?.maximize,
            elementType: "Switch",
            groupId: "griditemsettings",
            tooltip: "Enable/Disable maximize option for grid-item.",
          },
        ];
      case "alignment":
        return [
          {
            key: "x",
            label: "Grid-Left",
            minInput: 0,
            value:
              getGridItemLayoutObj?.x !== undefined
                ? getGridItemLayoutObj?.x
                : data?.initialPosition?.x,
            elementType: "InputNumber",
            groupId: "alignment",
            tooltip:
              "Set the horizontal distance from top-left of the dashboard",
          },
          {
            key: "y",
            label: "Grid-Top",
            minInput: 0,
            value:
              getGridItemLayoutObj?.y !== undefined
                ? getGridItemLayoutObj?.y
                : data?.initialPosition?.y,
            elementType: "InputNumber",
            groupId: "alignment",
            tooltip: "Set the vertical distance from top-left of the dashboard",
          },
          {
            key: "h",
            label: "Grid-Height",
            minInput: 1,
            value:
              getGridItemLayoutObj?.h !== undefined
                ? getGridItemLayoutObj?.h
                : data?.initialPosition?.h,
            elementType: "InputNumber",
            groupId: "alignment",
            tooltip: "Set the height of the grid-item",
          },
          {
            key: "w",
            label: "Grid-Width",
            minInput: 1,
            value:
              getGridItemLayoutObj?.w !== undefined
                ? getGridItemLayoutObj?.w
                : data?.initialPosition?.w,
            elementType: "InputNumber",
            groupId: "alignment",
            tooltip: "Set the width of the grid-item",
          },
        ];
      case "text":
        return [
          {
            key: "enable",
            label: "Enable",
            value: editValues?.enable,
            elementType: "Switch",
            groupId: "edit",
            tooltip: "Enable/Disable the changes.Re Enable if not applied.",
          },
          {
            key: "text",
            label: "Text",
            value: editValues?.text,
            placeholder: editValues?.placeholder,
            elementType: "TextEditor",
            groupId: "edit",
            tooltip: "Add text to the text component",
          },
          {
            key: "link",
            label: "Link",
            value: editValues?.link,
            elementType: "Input",
            groupId: "edit",
            tooltip:
              "Set any link.The set link will open the link in a new tab.Add https:// for external links.",
          },
        ];
      case "image":
        return [
          {
            key: "enable",
            label: "Enable",
            value: editValues?.enable,
            elementType: "Switch",
            groupId: "edit",
            tooltip: "Enable/Disable the changes.Re Enable if not applied.",
          },
          {
            key: "url",
            label: "Image Url",
            value: editValues?.url,
            placeholder: "",
            elementType: "Input",
            groupId: "edit",
            tooltip:
              "Set image for image component. Provide external urls or local images",
          },
          {
            key: "opacity",
            label: "Opacity",
            min: 0,
            max: 100,
            step: 1,
            value: editValues?.opacity,
            elementType: "Slider",
            groupId: "edit",
            tooltip:
              "Set image opacity.This property can take a value from 0 - 100. The lower the value, the more transparent:",
          },
          {
            key: "imageSize",
            label: "Image Size",
            values: [
              { key: "auto", label: "Auto" },
              { key: "fill", label: "Fill" },
              { key: "contain", label: "Contain" },
              { key: "cover", label: "Cover" },
              { key: "none", label: "None" },
              { key: "scale-down", label: "Scale down" },
            ],
            value: editValues?.imageSize,
            elementType: "Select",
            groupId: "edit",
            tooltip: `Set the size of ${container}'s image`,
          },
          {
            key: "imageRepeat",
            label: "Image Repeat",
            values: [
              { key: "repeat", label: "Repeat" },
              { key: "repeat-x", label: "Repeat-x" },
              { key: "repeat-y", label: "Repeat-y" },
              { key: "no-repeat", label: "No repeat" },
              { key: "initial", label: "Initial" },
              { key: "inherit", label: "inherit" },
              { key: "space", label: "Space" },
              { key: "round", label: "round" },
            ],
            value: editValues?.imageRepeat,
            elementType: "Select",
            groupId: "edit",
            tooltip: `Set the repeatition of ${container}'s background image`,
          },
        ];
      case "tab":
        return [
          {
            key: "enable",
            label: "Enable",
            value: editValues?.enable,
            elementType: "Switch",
            groupId: "edit",
            tooltip: "Enable/Disable the changes.Re Enable if not applied.",
          },
          {
            key: "numberOfTabs",
            label: "Number of Tabs",
            value: editValues?.numberOfTabs,
            elementType: "InputNumber",
            groupId: "edit",
            tooltip: "Please choose the desired number of tabs.",
          },
          {
            key: "tabNames",
            label: "Name of Tabs",
            value: editValues?.tabNames,
            elementType: "Input",
            groupId: "edit",
            tooltip: "Please provide the names of the tabs in a comma-separated format.",
          },
          {
            key: "tabType",
            label: "Type of Tabs",
            values: [
              { key: "line", label: "Line" },
              { key: "card", label: "Card" },
            ],
            value: editValues?.tabType,
            elementType: "Select",
            groupId: "edit",
            tooltip: "The line tab represents a minimalistic view with only an underline, while the card will display tabs within a bordered box.",
          },
          {
            key: "centered",
            label: "Centered",
            value: editValues?.centered,
            elementType: "Switch",
            groupId: "edit",
            tooltip: "This enables us to position the tabs centrally.",
          },
          {
            key: "activeTab",
            label: "Active Tab",
            value: editValues?.activeTab,
            elementType: "Input",
            groupId: "edit",
            tooltip: "This enables us to navigate directly to the currently active tab.",
          },
        ];
      case "columns":
        return  [
          {
            key: "lg",
            label: "Large Screens",
            groupId: "columns",
            elementType: "InputNumber",
            value: columnValues?.find(cv => cv.key === "lg")?.value,
            tooltip: "Large Screens(lg)",
            putAsterisk: fallsInBreakpoint(breakpointValues, getComponentWidth(id, 217)) === "lg"
          },
          {
            key: "md",
            label: "Medium Screens",
            groupId: "columns",
            elementType: "InputNumber",
            value: columnValues?.find(cv => cv.key === "md")?.value,
            tooltip: "Medium Screens(md)",
            putAsterisk: fallsInBreakpoint(breakpointValues, getComponentWidth(id, 217)) === "md"
          },
          {
            key: "sm",
            label: "Small Screens",
            groupId: "columns",
            elementType: "InputNumber",
            value: columnValues?.find(cv => cv.key === "sm")?.value,
            tooltip: "Small Screens(sm)",
            putAsterisk: fallsInBreakpoint(breakpointValues, getComponentWidth(id, 217)) === "sm"
          },
          {
            key: "xs",
            label: "Extra Small Screens",
            groupId: "columns",
            elementType: "InputNumber",
            value: columnValues?.find(cv => cv.key === "xs")?.value,
            tooltip: "Extra Small Screens(xs)",
            putAsterisk: fallsInBreakpoint(breakpointValues, getComponentWidth(id, 217)) === "xs"
          },
          {
            key: "xxs",
            label: "Extra Extra Small Screens",
            groupId: "columns",
            elementType: "InputNumber",
            value: columnValues?.find(cv => cv.key === "xxs")?.value,
            tooltip: "Extra Extra Small Screens(xxs)",
            putAsterisk: fallsInBreakpoint(breakpointValues, getComponentWidth(id, 217)) === "xxs"
          },
        ];
      case "breakpoints":
        return [
          {
            key: "lg",
            label: "Large Screens",
            value: breakpointValues?.find(bv => bv.key === "lg")?.value,
            elementType: "InputNumber",
            groupId: "breakpoints",
            tooltip: "Large Screens(lg)",
            putAsterisk: fallsInBreakpoint(breakpointValues, getComponentWidth(id, 217)) === "lg"
          },
          {
            key: "md",
            label: "Medium Screens",
            value: breakpointValues?.find(bv => bv.key === "md")?.value,
            elementType: "InputNumber",
            groupId: "breakpoints",
            tooltip: "Medium Screens(md)",
            putAsterisk: fallsInBreakpoint(breakpointValues, getComponentWidth(id, 217)) === "md"
          },
          {
            key: "sm",
            label: "Small Screens",
            value: breakpointValues?.find(bv => bv.key === "sm")?.value,
            elementType: "InputNumber",
            groupId: "breakpoints",
            tooltip: "Small Screens(sm)",
            putAsterisk: fallsInBreakpoint(breakpointValues, getComponentWidth(id, 217)) === "sm"
          },
          {
            key: "xs",
            label: "Extra Small Screens",
            value: breakpointValues?.find(bv => bv.key === "xs")?.value,
            elementType: "InputNumber",
            groupId: "breakpoints",
            tooltip: "Extra Small Screens(xs)",
            putAsterisk: fallsInBreakpoint(breakpointValues, getComponentWidth(id, 217)) === "xs"
          },
          {
            key: "xxs",
            label: "Extra Extra Small Screens",
            value: breakpointValues?.find(bv => bv.key === "xxs")?.value,
            elementType: "InputNumber",
            groupId: "breakpoints",
            tooltip: "Extra Extra Small Screens(xxs)",
            putAsterisk: fallsInBreakpoint(breakpointValues, getComponentWidth(id, 217)) === "xxs"
          },
        ];
        case "select-dropdown":
          return [
            {
              key: "enable",
              label: "Enable",
              value: editValues?.enable,
              elementType: "Switch",
              groupId: "edit",
              tooltip: "Enable/Disable the changes.Re Enable if not applied.",
            },
            {
              key: "dropdownValues",
              label: "Values",
              value: editValues?.dropdownValues,
              elementType: "Input",
              groupId: "edit",
              tooltip: "Please provide the values of dropdown in a comma-separated format.",
            },
            {
              key: "dropdownSize",
              label: "Size",
              values: [
                { key: "small", label: "Small" },
                { key: "middle", label: "Medium" },
                { key: "large", label: "Large" },
              ],
              value: editValues?.dropdownSize,
              elementType: "Select",
              groupId: "edit",
              tooltip: "Select the size of the dropdown.",
            },
            {
              key: "bordered",
              label: "Border",
              value: editValues?.bordered,
              elementType: "Switch",
              groupId: "edit",
              tooltip: "This property applies a border to the dropdown.",
            },
            {
              key: "placement",
              label: "Placement",
              values: [
                { key: "bottomLeft", label: "Bottom" },
                { key: "topLeft", label: "Top" },
              ],
              value: editValues?.placement,
              elementType: "Select",
              groupId: "edit",
              tooltip: "This property sets the position of the dropdown list.",
            },
            {
              key: "allowClear",
              label: "Allow Clear",
              value: editValues?.allowClear	,
              elementType: "Switch",
              groupId: "edit",
              tooltip: "This property displays a clear button in the dropdown select field.",
            },
            {
              key: "jsValue",
              label: "On Change Event",
              mode: "javascript",
              value: editValues?.jsValue,
              showJSDrawerIcon: true,
              isDashboard: false,
              elementType: "CodeEditor",
              groupId: "edit",
              tooltip:"1. Write your JavaScript, and the on change event in the dropdown will trigger the JavaScript function. 2. For accessing the dropdown value , example: You can try console.log(value), it will return the selected value. 3. Only user variables are accessible. For more information, read the help icon above.",

            },
          ];
          
      default:
        return null;
    }
  });
};
