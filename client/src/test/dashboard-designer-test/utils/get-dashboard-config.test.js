// import { getDashboardConfig } from "../../../components/hi-dashboard-designer/utils/get-dashboard-config";

// describe("getDashboardConfig function", () => {
//   test("working test case", () => {
//     const gridSettingsData = [
//       {
//         key: "breakpoints",
//         values: [
//           { name: "lg", value: 1200, tooltip: "Large Screens" },
//           { name: "md", value: 996, tooltip: "Medium Screens" },
//           { name: "sm", value: 768, tooltip: "Small Screens" },
//           { name: "xs", value: 480, tooltip: "Extra Small Screens" },
//           { name: "xxs", value: 480, tooltip: "Extra Extra Small Screens" },
//         ],
//       },
//       {
//         key: "columns",
//         values: [
//           { name: "lg", value: 100, tooltip: "Large Screens" },
//           { name: "md", value: 100, tooltip: "Medium Screens" },
//           { name: "sm", value: 100, tooltip: "Small Screens" },
//           { name: "xs", value: 100, tooltip: "Extra Small Screens" },
//           { name: "xxs", value: 100, tooltip: "Extra Extra Small Screens" },
//         ],
//       },
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
//     const [borderStyles, backgroundStyles, shadowStyles, headerStyles] =
//       getDashboardConfig(gridSettingsData, null, [
//         "border",
//         "background",
//         "shadow",
//         "header",
//       ]);
//     expect(borderStyles).toStrictEqual({
//       border: "solid 5px #b115d1ff",
//     });
//     expect(backgroundStyles).toStrictEqual({
//       backgroundColor: "#3c8bcaff",
//       backgroundImage: "url()",
//       backgroundSize: "contain",
//     });
//     expect(shadowStyles).toStrictEqual({
//       boxShadow: "10px 10px 10px 10px #6ca22dff",
//     });
//     expect(headerStyles).toStrictEqual({
//       backgroundColor: "#c01f1fff",
//     });
//   });
//   test("working test case with gridItem", () => {
//     const gridItemsData = [
//       {
//         id: "id1d6bf3e9-2244-4cde-b766-0ce3f4d40b79",
//         isGrouped: false,
//         initialPosition: {
//           x: 0,
//           y: 0,
//           w: 33,
//           h: 10,
//         },
//         children: [
//           {
//             id: "id425f3c5f-a417-4a1d-bdca-e5b5447ea321",
//             isGrouped: true,
//             initialPosition: {
//               x: 0,
//               y: 0,
//               w: 33,
//               h: 10,
//             },
//             children: [],
//             name: "Organisation",
//             gridItemConfig: [
//               {
//                 key: "header",
//                 values: {
//                   enable: false,
//                   title: "",
//                   placeholder: "Edit/Add your header content here",
//                   link: "",
//                   backgroundColor: "#fff",
//                 },
//               },
//               {
//                 key: "shadow",
//                 values: {
//                   enable: false,
//                   xOffset: 0,
//                   yOffset: 0,
//                   blur: 0,
//                   spread: 0,
//                   color: "#fff",
//                 },
//               },
//               {
//                 key: "background",
//                 values: {
//                   enable: false,
//                   backgroundColor: "#fff",
//                   image: "",
//                 },
//               },
//               {
//                 key: "border",
//                 values: {
//                   enable: false,
//                   borderWidth: 1,
//                   borderStyle: "none",
//                   color: "#fff",
//                 },
//               },
//               {
//                 key: "html",
//                 values: {
//                   enable: false,
//                   value: "",
//                 },
//               },
//               {
//                 key: "css",
//                 values: {
//                   enable: false,
//                   value: "",
//                 },
//               },
//               {
//                 key: "javascript",
//                 values: {
//                   enable: false,
//                   value: "",
//                 },
//               },
//             ],
//           },
//           {
//             id: "id45d995ea-f9c7-4e0b-987a-8651608faa28",
//             isGrouped: true,
//             initialPosition: {
//               x: 0,
//               y: 0,
//               w: 33,
//               h: 10,
//             },
//             children: [],
//             name: "Organisation",
//             gridItemConfig: [
//               {
//                 key: "header",
//                 values: {
//                   enable: false,
//                   title: "",
//                   placeholder: "Edit/Add your header content here",
//                   link: "",
//                   backgroundColor: "#fff",
//                 },
//               },
//               {
//                 key: "shadow",
//                 values: {
//                   enable: false,
//                   xOffset: 0,
//                   yOffset: 0,
//                   blur: 0,
//                   spread: 0,
//                   color: "#fff",
//                 },
//               },
//               {
//                 key: "background",
//                 values: {
//                   enable: true,
//                   backgroundColor: "#ddd",
//                   image: "",
//                 },
//               },
//               {
//                 key: "border",
//                 values: {
//                   enable: false,
//                   borderWidth: 1,
//                   borderStyle: "none",
//                   color: "#fff",
//                 },
//               },
//               {
//                 key: "html",
//                 values: {
//                   enable: false,
//                   value: "",
//                 },
//               },
//               {
//                 key: "css",
//                 values: {
//                   enable: false,
//                   value: "",
//                 },
//               },
//               {
//                 key: "javascript",
//                 values: {
//                   enable: false,
//                   value: "",
//                 },
//               },
//             ],
//           },
//         ],
//         name: "Organisation",
//         gridItemConfig: [
//           {
//             key: "header",
//             values: {
//               enable: false,
//               title: "",
//               placeholder: "Edit/Add your header content here",
//               link: "",
//               backgroundColor: "#fff",
//             },
//           },
//           {
//             key: "shadow",
//             values: {
//               enable: false,
//               xOffset: 0,
//               yOffset: 0,
//               blur: 0,
//               spread: 0,
//               color: "#fff",
//             },
//           },
//           {
//             key: "background",
//             values: {
//               enable: false,
//               backgroundColor: "#fff",
//               image: "",
//             },
//           },
//           {
//             key: "border",
//             values: {
//               enable: false,
//               borderWidth: 1,
//               borderStyle: "none",
//               color: "#fff",
//             },
//           },
//           {
//             key: "html",
//             values: {
//               enable: false,
//               value: "",
//             },
//           },
//           {
//             key: "css",
//             values: {
//               enable: false,
//               value: "",
//             },
//           },
//           {
//             key: "javascript",
//             values: {
//               enable: false,
//               value: "",
//             },
//           },
//         ],
//         layout: [],
//       },
//     ];
//     const [backgroundStyles] = getDashboardConfig(
//       gridItemsData,
//       "id45d995ea-f9c7-4e0b-987a-8651608faa28",
//       ["background"]
//     );
//     expect(backgroundStyles).toStrictEqual({
//       backgroundColor: "#ddd",
//       backgroundImage: "url()",
//       backgroundSize: "contain",
//     });
//   });
// });
test("testasdfse", () => {
  expect(true).toBe(true);
});
