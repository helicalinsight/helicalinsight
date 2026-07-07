import { initialConfig } from "../../../components/hi-dashboard-designer/utils/constants";

describe("intital config function test cases",()=>{
test("dashboard designer component",()=>{
    const result = initialConfig({ reportName: "My Report", compType: "dashboard-designer-component" });
expect(result).toEqual([
  {
    key: "header",
    values: {
      enable: true,
      title: "My Report",
      placeholder: "Edit/Add your header content here",
      link: "",
      backgroundColor: { r: 24, g: 144, b: 255, a: 100 },
      position: "left",
      customColor:{ r: 255, g: 255, b: 255, a: 100 },
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
  {
    key: "griditemsettings",
    values: {
      export: false,
      edit: false,
      maximize: false,
    },
  }
]);

})
})