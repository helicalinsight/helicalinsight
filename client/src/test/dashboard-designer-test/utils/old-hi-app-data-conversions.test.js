// import {
//   RGBAToHexA,
//   convertOldConfigToNewConfig,
// } from "../../../components/hi-dashboard-designer/utils/old-hi-app-data-converstions";

// describe("RGBA Convertor", () => {
//   test("normal RGBA values", () => {
//     let hexValue = RGBAToHexA(1, 2, 3, 1);
//     expect(hexValue).toBe("#010203ff");
//   });
//   test("without R in RGBA values", () => {
//     let hexValue = RGBAToHexA(undefined, 2, 3, 1);
//     expect(hexValue).toBe("#ffffff");
//   });
//   test("without G in RGBA values", () => {
//     let hexValue = RGBAToHexA(1, null, 3, 1);
//     expect(hexValue).toBe("#ffffff");
//   });
//   test("without B in RGBA values", () => {
//     let hexValue = RGBAToHexA(1, 2, undefined, 1);
//     expect(hexValue).toBe("#ffffff");
//   });
//   test("without A in RGBA values", () => {
//     let hexValue = RGBAToHexA(1, 2, 3, undefined);
//     expect(hexValue).toBe("#ffffff");
//   });
//   test("R as string in RGBA values", () => {
//     let hexValue = RGBAToHexA("1", 3, 3, 1);
//     expect(hexValue).toBe("#010303ff");
//   });
//   test("G as object in RGBA values", () => {
//     let hexValue = RGBAToHexA(1, { hello: 1 }, 3, 1);
//     expect(hexValue).toBe("#ffffff");
//   });
//   test("B as array in RGBA values", () => {
//     let hexValue = RGBAToHexA(1, 3, [3, 2, 1], 1);
//     expect(hexValue).toBe("#ffffff");
//   });
//   test("A as boolean in RGBA values", () => {
//     let hexValue = RGBAToHexA(1, 3, 3, true);
//     expect(hexValue).toBe("#ffffff");
//   });
// });

// describe("convertOldConfigToNewConfig Function", () => {
//   test("normal gridItemConfig values working test case", () => {
//     const gridItemConfigOld = {
//       shadow: {
//         enable: true,
//         xoffset: "10",
//         yoffset: "10",
//         blur: "10",
//         spread: "10",
//         color: {
//           r: 108,
//           g: 162,
//           b: 45,
//           a: 1,
//         },
//         hexCode: "#6ca22d",
//       },
//       gs_attr: {
//         x: 13,
//         y: 5,
//         height: 16,
//         width: 33,
//       },
//       name: "cn7hlyni5hj",
//       border: {
//         enable: true,
//         color: {
//           r: 177,
//           g: 21,
//           b: 209,
//           a: 1,
//         },
//         hexCode: "#b115d1",
//         weight: "5",
//         borderStyle: "solid",
//       },
//       header: {
//         enable: true,
//         text: "<p>Simple Bubble Chartdsafsfd</p>",
//         href: "",
//         enableColor: true,
//         color: {
//           r: 192,
//           g: 31,
//           b: 31,
//           a: 1,
//         },
//       },
//       background: {
//         enable: true,
//         color: {
//           r: "60",
//           g: "139",
//           b: "202",
//           a: "1",
//         },
//         imgUrl: "",
//         imgOpacity: 50,
//         size: "auto",
//         bgWidth: 1350,
//         bgHeight: 650,
//         sizeType: "default",
//       },
//       metadata: {
//         dir: "/HI Sample Reports/Adhoc Advanced Charts/Bubble Chart/",
//         name: "Simple Bubble Chart",
//       },
//       uid: "cn7hlyni5hj",
//       label: "Simple Bubble Chartdsafsfd",
//       context_lable: "Simple Bubble Chart",
//       executeAtStart: true,
//       type: "dashboard-component",
//       options: {
//         dir: "1463377807724/1463983915686/1463838054907",
//         file: "d1560c88-be0d-4380-8225-8a8df4eb53bf.report",
//         ext: "report",
//         compType: "Adhoc",
//         uid: "cn7hlyni5hj",
//         iframe: false,
//         styles: {
//           borderStyle: "solid",
//           value: true,
//           newValue: true,
//           boxPx: "10px 10px 10px 10px",
//           borderPx: "5px",
//           borderColor: "#b115d1",
//           boxColor: "#6ca22d",
//           boxComp: "Adhoc",
//           borderComp: "Adhoc",
//         },
//       },
//     };

//     const gridItemConfigNew = [
//       {
//         key: "header",
//         values: {
//           backgroundColor: "#c01f1fff",
//           enable: true,
//           title: "<p>Simple Bubble Chartdsafsfd</p>",
//           placeholder: "Edit/Add your header content here",
//           link: "",
//         },
//       },
//       {
//         key: "shadow",
//         values: {
//           blur: "10",
//           color: "#6ca22dff",
//           enable: true,
//           spread: "10",
//           xOffset: "10",
//           yOffset: "10",
//         },
//       },
//       {
//         key: "background",
//         values: {
//           backgroundColor: "#3c8bcaff",
//           enable: true,
//           image: "",
//         },
//       },
//       {
//         key: "border",
//         values: {
//           borderStyle: "solid",
//           borderWidth: "5",
//           color: "#b115d1ff",
//           enable: true,
//         },
//       },
//       {
//         key: "html",
//         values: {
//           enable: false,
//           value: "",
//         },
//       },
//       {
//         key: "css",
//         values: {
//           enable: false,
//           value: "",
//         },
//       },
//       {
//         key: "javascript",
//         values: {
//           enable: false,
//           value: "",
//         },
//       },
//     ];
//     let convertedGridConfig = convertOldConfigToNewConfig(gridItemConfigOld);
//     expect(convertedGridConfig).toStrictEqual(gridItemConfigNew);
//   });
//   test("gridItemConfig without header values working test case", () => {
//     const gridItemConfigOld = {
//       shadow: {
//         enable: true,
//         xoffset: "10",
//         yoffset: "10",
//         blur: "10",
//         spread: "10",
//         color: {
//           r: 108,
//           g: 162,
//           b: 45,
//           a: 1,
//         },
//         hexCode: "#6ca22d",
//       },
//       gs_attr: {
//         x: 13,
//         y: 5,
//         height: 16,
//         width: 33,
//       },
//       name: "cn7hlyni5hj",
//       border: {
//         enable: true,
//         color: {
//           r: 177,
//           g: 21,
//           b: 209,
//           a: 1,
//         },
//         hexCode: "#b115d1",
//         weight: "5",
//         borderStyle: "solid",
//       },

//       background: {
//         enable: true,
//         color: {
//           r: "60",
//           g: "139",
//           b: "202",
//           a: "1",
//         },
//         imgUrl: "",
//         imgOpacity: 50,
//         size: "auto",
//         bgWidth: 1350,
//         bgHeight: 650,
//         sizeType: "default",
//       },
//       metadata: {
//         dir: "/HI Sample Reports/Adhoc Advanced Charts/Bubble Chart/",
//         name: "Simple Bubble Chart",
//       },
//       uid: "cn7hlyni5hj",
//       label: "Simple Bubble Chartdsafsfd",
//       context_lable: "Simple Bubble Chart",
//       executeAtStart: true,
//       type: "dashboard-component",
//       options: {
//         dir: "1463377807724/1463983915686/1463838054907",
//         file: "d1560c88-be0d-4380-8225-8a8df4eb53bf.report",
//         ext: "report",
//         compType: "Adhoc",
//         uid: "cn7hlyni5hj",
//         iframe: false,
//         styles: {
//           borderStyle: "solid",
//           value: true,
//           newValue: true,
//           boxPx: "10px 10px 10px 10px",
//           borderPx: "5px",
//           borderColor: "#b115d1",
//           boxColor: "#6ca22d",
//           boxComp: "Adhoc",
//           borderComp: "Adhoc",
//         },
//       },
//     };

//     const gridItemConfigNew = [
//       {
//         key: "header",
//         values: {
//           backgroundColor: "#fff",
//           enable: false,
//           title: "",
//           placeholder: "Edit/Add your header content here",
//           link: "",
//         },
//       },
//       {
//         key: "shadow",
//         values: {
//           blur: "10",
//           color: "#6ca22dff",
//           enable: true,
//           spread: "10",
//           xOffset: "10",
//           yOffset: "10",
//         },
//       },
//       {
//         key: "background",
//         values: {
//           backgroundColor: "#3c8bcaff",
//           enable: true,
//           image: "",
//         },
//       },
//       {
//         key: "border",
//         values: {
//           borderStyle: "solid",
//           borderWidth: "5",
//           color: "#b115d1ff",
//           enable: true,
//         },
//       },
//       {
//         key: "html",
//         values: {
//           enable: false,
//           value: "",
//         },
//       },
//       {
//         key: "css",
//         values: {
//           enable: false,
//           value: "",
//         },
//       },
//       {
//         key: "javascript",
//         values: {
//           enable: false,
//           value: "",
//         },
//       },
//     ];
//     let convertedGridConfig = convertOldConfigToNewConfig(gridItemConfigOld);
//     expect(convertedGridConfig).toStrictEqual(gridItemConfigNew);
//   });
//   test("gridItemConfig without shadow values working test case", () => {
//     const gridItemConfigOld = {
//       gs_attr: {
//         x: 13,
//         y: 5,
//         height: 16,
//         width: 33,
//       },
//       name: "cn7hlyni5hj",
//       border: {
//         enable: true,
//         color: {
//           r: 177,
//           g: 21,
//           b: 209,
//           a: 1,
//         },
//         hexCode: "#b115d1",
//         weight: "5",
//         borderStyle: "solid",
//       },
//       header: {
//         enable: true,
//         text: "<p>Simple Bubble Chartdsafsfd</p>",
//         href: "",
//         enableColor: true,
//         color: {
//           r: 192,
//           g: 31,
//           b: 31,
//           a: 1,
//         },
//       },
//       background: {
//         enable: true,
//         color: {
//           r: "60",
//           g: "139",
//           b: "202",
//           a: "1",
//         },
//         imgUrl: "",
//         imgOpacity: 50,
//         size: "auto",
//         bgWidth: 1350,
//         bgHeight: 650,
//         sizeType: "default",
//       },
//       metadata: {
//         dir: "/HI Sample Reports/Adhoc Advanced Charts/Bubble Chart/",
//         name: "Simple Bubble Chart",
//       },
//       uid: "cn7hlyni5hj",
//       label: "Simple Bubble Chartdsafsfd",
//       context_lable: "Simple Bubble Chart",
//       executeAtStart: true,
//       type: "dashboard-component",
//       options: {
//         dir: "1463377807724/1463983915686/1463838054907",
//         file: "d1560c88-be0d-4380-8225-8a8df4eb53bf.report",
//         ext: "report",
//         compType: "Adhoc",
//         uid: "cn7hlyni5hj",
//         iframe: false,
//         styles: {
//           borderStyle: "solid",
//           value: true,
//           newValue: true,
//           boxPx: "10px 10px 10px 10px",
//           borderPx: "5px",
//           borderColor: "#b115d1",
//           boxColor: "#6ca22d",
//           boxComp: "Adhoc",
//           borderComp: "Adhoc",
//         },
//       },
//     };

//     const gridItemConfigNew = [
//       {
//         key: "header",
//         values: {
//           backgroundColor: "#c01f1fff",
//           enable: true,
//           title: "<p>Simple Bubble Chartdsafsfd</p>",
//           placeholder: "Edit/Add your header content here",
//           link: "",
//         },
//       },
//       {
//         key: "shadow",
//         values: {
//           blur: 0,
//           color: "#fff",
//           enable: false,
//           spread: 0,
//           xOffset: 0,
//           yOffset: 0,
//         },
//       },
//       {
//         key: "background",
//         values: {
//           backgroundColor: "#3c8bcaff",
//           enable: true,
//           image: "",
//         },
//       },
//       {
//         key: "border",
//         values: {
//           borderStyle: "solid",
//           borderWidth: "5",
//           color: "#b115d1ff",
//           enable: true,
//         },
//       },
//       {
//         key: "html",
//         values: {
//           enable: false,
//           value: "",
//         },
//       },
//       {
//         key: "css",
//         values: {
//           enable: false,
//           value: "",
//         },
//       },
//       {
//         key: "javascript",
//         values: {
//           enable: false,
//           value: "",
//         },
//       },
//     ];
//     let convertedGridConfig = convertOldConfigToNewConfig(gridItemConfigOld);
//     expect(convertedGridConfig).toStrictEqual(gridItemConfigNew);
//   });
// });
test("testasdf", () => {
  expect(true).toBe(true);
});
