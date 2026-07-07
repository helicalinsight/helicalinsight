import { getGroup } from "../../../components/hi-dashboard-designer/utils/constants";
import { getPropertyPaneItemsConfig } from "../../../components/hi-dashboard-designer/utils/get-property-pane-items-config";

describe("getDashboardConfig function", () => {
  test("working test case", () => {
    const gridSettingsData = [
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
        // values: [
        // 	{ name: 'lg', value: 100, tooltip: 'Large Screens' },
        // 	{ name: 'md', value: 8, tooltip: 'Medium Screens' },
        // 	{ name: 'sm', value: 6, tooltip: 'Small Screens' },
        // 	{ name: 'xs', value: 4, tooltip: 'Extra Small Screens' },
        // 	{ name: 'xxs', value: 2, tooltip: 'Extra Extra Small Screens' }
        // ]
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
          // resizeHandles: ["se"],
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
          containerMarginVertical: 0
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
    ];
    const [
      headerItems,
      shadowItems,
      backgroundItems,
      borderItems,
      htmlItems,
      cssItems,
      jsItems,
    ] = getPropertyPaneItemsConfig(
      {
        array: gridSettingsData,
        id: null,
        type: [
          "header",
          "shadow",
          "background",
          "border",
          "html",
          "css",
          "javascript",
        ],
        // getGridItemLayoutObj,
      });
    expect(headerItems).toStrictEqual([
      {
        elementType: "Switch",
        groupId: "header",
        key: "enable",
        label: "Enable",
        tooltip: "Enable/Disable the changes.Re Enable if not applied",
        value: false,
      },
      {
        elementType: "TextEditor",
        groupId: "header",
        key: "title",
        label: "Title",
        customColor: undefined,
        placeholder: "Edit/Add your header content here",
        tooltip: "Set the title property.",
        value: "<p><br></p>",
      },
      {
        elementType: "Input",
        groupId: "header",
        key: "link",
        label: "Link",
        tooltip: "Set any link.The set link will open the link in a new tab.Add https:// for external links.",
        value: "",
      },
      {
        elementType: "ColorPicker",
        groupId: "header",
        key: "backgroundColor",
        label: "Background Color",
        tooltip: "Set the background color",
        value: {
          "a": 100,
          "b": 255,
          "g": 255,
          "r": 255,
        },
      },
      {
        key: "customColor",
        label: "Color",
        value: undefined,
        elementType: "ColorPicker",
        groupId: "header",
        tooltip: "Use this option to customize text editor color pallet with advanced color options",
      },
    ]);
    expect(shadowItems).toStrictEqual([
      {
        elementType: "Switch",
        groupId: "shadow",
        key: "enable",
        label: "Enable",
        "tooltip": "Enable/Disable the changes.Re Enable if not applied",
        "value": false,
      },
      {
        elementType: "InputNumber",
        groupId: "shadow",
        key: "xOffset",
        label: "X Offset",
        "tooltip": "Set the horizontal distance of dashboard's shadow",
        "value": 0,
      },
      {
        elementType: "InputNumber",
        groupId: "shadow",
        key: "yOffset",
        label: "Y Offset",
        "tooltip": "Set the vertical distance of dashboard's shadow",
        "value": 0,
      },
      {
        elementType: "InputNumber",
        groupId: "shadow",
        key: "blur",
        label: "Blur",
        "tooltip": "Set the blur of dashboard's shadow.The larger this value, the bigger the blur, so the shadow becomes bigger and lighter. Negative values are not allowed. If not specified, it will be 0 (the shadow's edge is sharp).",
        "value": 0,
      },
      {
        elementType: "InputNumber",
        groupId: "shadow",
        key: "spread",
        label: "Spread",
        "tooltip": "Set the spread of dashboard's shadow.Positive values will cause the shadow to expand and grow bigger, negative values will cause the shadow to shrink. If not specified, it will be 0 (the shadow will be the same size as the element).",
        "value": 0,
      },
      {
        elementType: "ColorPicker",
        groupId: "shadow",
        key: "color",
        label: "Color",
        tooltip: "Set the color of dashboard's shadow.",
        value: {
          "a": 100,
          "b": 255,
          "g": 255,
          "r": 255,
        },
      },
    ]);
    expect(backgroundItems).toStrictEqual([
      {
        key: "enable",
        label: "Enable",
        value: false,
        elementType: "Switch",
        groupId: "background",
        tooltip: "Enable/Disable the changes.Re Enable if not applied.",
      },
      {
        key: "backgroundColor",
        label: "Color",
        value: {
          "a": 100,
          "b": 255,
          "g": 255,
          "r": 255,
        },
        elementType: "ColorPicker",
        groupId: "background",
        tooltip: `Set the color of dashboard's background.`,
      },
      {
        key: "image",
        label: "Image",
        value: "",
        elementType: "Input",
        groupId: "background",
        tooltip: `Set image as dashboard's background. Provide external urls or local images.`,
      },
      {
        key: "opacity",
        label: "Opacity",
        min: 0,
        max: 100,
        step: 1,
        value: 100,
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
        value: "auto",
        elementType: "Select",
        groupId: "background",
        tooltip: `Set the size of dashboard's background image`,
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
        value: "initial",
        elementType: "Select",
        groupId: "background",
        tooltip: `Set the repeatition of dashboard's background image`,
      },
    ]);
    expect(borderItems).toStrictEqual([
      {
        key: "enable",
        label: "Enable",
        value: false,
        elementType: "Switch",
        groupId: "border",
        tooltip: "Enable/Disable the changes.Re Enable if not applied.",
      },
      {
        key: "borderWidth",
        label: "Border Width",
        value: 1,
        elementType: "InputNumber",
        groupId: "border",
        tooltip: `Set the width of dashboard's border`,
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
        value: "solid",
        elementType: "Select",
        groupId: "border",
        tooltip: `Set the style of dashboard's border`,
      },
      {
        key: "color",
        label: "Color",
        value: {
          "a": 100,
          "b": 255,
          "g": 255,
          "r": 255,
        },
        elementType: "ColorPicker",
        groupId: "border",
        tooltip: `Set the color of dashboard's border`,
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
        value: "border",
        elementType: "Select",
        groupId: "border",
        multiSelect: true,
        tooltip: `Set the position of dashboard's border`,
      },
      {
        key: "borderRadius",
        label: "Border Radius",
        value: 0,
        elementType: "InputNumber",
        groupId: "border",
        tooltip: `Set the border radius of dashboard's border`,
      },
    ]);
    expect(htmlItems).toStrictEqual([
      {
        elementType: "Switch",
        groupId: "html",
        key: "enable",
        label: "Enable",
        "tooltip": "Enable/Disable the changes.Re Enable if not applied.",
        "value": false,
      },
      {
        elementType: "CodeEditor",
        groupId: "html",
        key: "value",
        label: "HTML Editor",
        tooltip: "Add html to dashboard",
        mode: "xml",
        value: "",
      },
    ]);
    expect(cssItems).toStrictEqual([
      {
        elementType: "Switch",
        groupId: "css",
        key: "enable",
        label: "Enable",
        "tooltip": "Enable/Disable the changes.Re Enable if not applied.",
        value: false,
      },
      {
        elementType: "CodeEditor",
        groupId: "css",
        key: "value",
        label: "CSS Editor",
        "tooltip": "Add css to dashboard",
        mode: "css",
        value: "",
      },
    ]);
    expect(jsItems).toStrictEqual([
      {
        elementType: "Switch",
        groupId: "javascript",
        key: "enable",
        label: "Enable",
        "tooltip": "Enable/Disable the changes.Re Enable if not applied.",
        value: false,
      },
      {
        elementType: "CodeEditor",
        groupId: "javascript",
        isDashboard: true,
        key: "value",
        label: "JS Editor",
        mode: "javascript",
        showJSDrawerIcon: true,
        value: "",
        "tooltip": "Add javascript to dashboard",
      },
    ]);
  });

  test("get group", () => {
    const dispatch = jest.fn();
    let options = getGroup({ type: "grid", dispatch })
    expect(options.length).toBe(8);
    let addOption = options.find((option) => option.key === "add");
    expect(addOption.children.length).toBe(5);
  })
});
