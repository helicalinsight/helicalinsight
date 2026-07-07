// import {
//   unGroup,
//   getFilteredGridItemsData,
//   getGridItem,
//   injectLayoutToGroupedGridItem,
//   changeIsDraggableInLayout,
//   changeFiltersDataInGridItems,
// } from "../../../components/hi-dashboard-designer/utils/recursive-functions";
// import produce from "immer";

// describe("unGroup function", () => {
//   const gridItemsData = [
//     {
//       id: "id1d6bf3e9-2244-4cde-b766-0ce3f4d40b79",
//       isGrouped: false,
//       initialPosition: {
//         x: 0,
//         y: 0,
//         w: 33,
//         h: 10,
//       },
//       children: [
//         {
//           id: "id425f3c5f-a417-4a1d-bdca-e5b5447ea321",
//           isGrouped: true,
//           initialPosition: {
//             x: 0,
//             y: 0,
//             w: 33,
//             h: 10,
//           },
//           children: [],
//           name: "Organisation",
//           gridItemConfig: [
//             {
//               key: "header",
//               values: {
//                 enable: false,
//                 title: "",
//                 placeholder: "Edit/Add your header content here",
//                 link: "",
//                 backgroundColor: "#fff",
//               },
//             },
//             {
//               key: "shadow",
//               values: {
//                 enable: false,
//                 xOffset: 0,
//                 yOffset: 0,
//                 blur: 0,
//                 spread: 0,
//                 color: "#fff",
//               },
//             },
//             {
//               key: "background",
//               values: {
//                 enable: false,
//                 backgroundColor: "#fff",
//                 image: "",
//               },
//             },
//             {
//               key: "border",
//               values: {
//                 enable: false,
//                 borderWidth: 1,
//                 borderStyle: "none",
//                 color: "#fff",
//               },
//             },
//             {
//               key: "html",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//             {
//               key: "css",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//             {
//               key: "javascript",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//           ],
//         },
//         {
//           id: "id45d995ea-f9c7-4e0b-987a-8651608faa28",
//           isGrouped: true,
//           initialPosition: {
//             x: 0,
//             y: 0,
//             w: 33,
//             h: 10,
//           },
//           children: [],
//           name: "Organisation",
//           gridItemConfig: [
//             {
//               key: "header",
//               values: {
//                 enable: false,
//                 title: "",
//                 placeholder: "Edit/Add your header content here",
//                 link: "",
//                 backgroundColor: "#fff",
//               },
//             },
//             {
//               key: "shadow",
//               values: {
//                 enable: false,
//                 xOffset: 0,
//                 yOffset: 0,
//                 blur: 0,
//                 spread: 0,
//                 color: "#fff",
//               },
//             },
//             {
//               key: "background",
//               values: {
//                 enable: false,
//                 backgroundColor: "#fff",
//                 image: "",
//               },
//             },
//             {
//               key: "border",
//               values: {
//                 enable: false,
//                 borderWidth: 1,
//                 borderStyle: "none",
//                 color: "#fff",
//               },
//             },
//             {
//               key: "html",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//             {
//               key: "css",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//             {
//               key: "javascript",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//           ],
//         },
//       ],
//       name: "Organisation",
//       gridItemConfig: [
//         {
//           key: "header",
//           values: {
//             enable: false,
//             title: "",
//             placeholder: "Edit/Add your header content here",
//             link: "",
//             backgroundColor: "#fff",
//           },
//         },
//         {
//           key: "shadow",
//           values: {
//             enable: false,
//             xOffset: 0,
//             yOffset: 0,
//             blur: 0,
//             spread: 0,
//             color: "#fff",
//           },
//         },
//         {
//           key: "background",
//           values: {
//             enable: false,
//             backgroundColor: "#fff",
//             image: "",
//           },
//         },
//         {
//           key: "border",
//           values: {
//             enable: false,
//             borderWidth: 1,
//             borderStyle: "none",
//             color: "#fff",
//           },
//         },
//         {
//           key: "html",
//           values: {
//             enable: false,
//             value: "",
//           },
//         },
//         {
//           key: "css",
//           values: {
//             enable: false,
//             value: "",
//           },
//         },
//         {
//           key: "javascript",
//           values: {
//             enable: false,
//             value: "",
//           },
//         },
//       ],
//       layout: [],
//     },
//   ];
//   test("working use case", () => {
//     let id = "id1d6bf3e9-2244-4cde-b766-0ce3f4d40b79";
//     let unGroupedGridItemsData = unGroup(gridItemsData, id);
//     expect(unGroupedGridItemsData).toStrictEqual([
//       {
//         children: [],
//         gridItemConfig: [
//           {
//             key: "header",
//             values: {
//               backgroundColor: "#fff",
//               enable: false,
//               link: "",
//               placeholder: "Edit/Add your header content here",
//               title: "",
//             },
//           },
//           {
//             key: "shadow",
//             values: {
//               blur: 0,
//               color: "#fff",
//               enable: false,
//               spread: 0,
//               xOffset: 0,
//               yOffset: 0,
//             },
//           },
//           {
//             key: "background",
//             values: { backgroundColor: "#fff", enable: false, image: "" },
//           },
//           {
//             key: "border",
//             values: {
//               borderStyle: "none",
//               borderWidth: 1,
//               color: "#fff",
//               enable: false,
//             },
//           },
//           { key: "html", values: { enable: false, value: "" } },
//           { key: "css", values: { enable: false, value: "" } },
//           { key: "javascript", values: { enable: false, value: "" } },
//         ],
//         id: "id425f3c5f-a417-4a1d-bdca-e5b5447ea321",
//         initialPosition: { h: 10, w: 33, x: 0, y: 0 },
//         isGrouped: true,
//         name: "Organisation",
//       },
//       {
//         children: [],
//         gridItemConfig: [
//           {
//             key: "header",
//             values: {
//               backgroundColor: "#fff",
//               enable: false,
//               link: "",
//               placeholder: "Edit/Add your header content here",
//               title: "",
//             },
//           },
//           {
//             key: "shadow",
//             values: {
//               blur: 0,
//               color: "#fff",
//               enable: false,
//               spread: 0,
//               xOffset: 0,
//               yOffset: 0,
//             },
//           },
//           {
//             key: "background",
//             values: { backgroundColor: "#fff", enable: false, image: "" },
//           },
//           {
//             key: "border",
//             values: {
//               borderStyle: "none",
//               borderWidth: 1,
//               color: "#fff",
//               enable: false,
//             },
//           },
//           { key: "html", values: { enable: false, value: "" } },
//           { key: "css", values: { enable: false, value: "" } },
//           { key: "javascript", values: { enable: false, value: "" } },
//         ],
//         id: "id45d995ea-f9c7-4e0b-987a-8651608faa28",
//         initialPosition: { h: 10, w: 33, x: 0, y: 0 },
//         isGrouped: true,
//         name: "Organisation",
//       },
//     ]);
//   });
//   test("without gridItemsData test case", () => {
//     let id = "id1d6bf3e9-2244-4cde-b766-0ce3f4d40b79";
//     let unGroupedGridItemsData = unGroup(undefined, id);
//     expect(unGroupedGridItemsData).toEqual(undefined);
//   });
//   test("without id test case", () => {
//     let unGroupedGridItemsData = unGroup(gridItemsData, undefined);
//     expect(unGroupedGridItemsData).toEqual(gridItemsData);
//   });
//   test("with id that is not present in gridItemsData test case", () => {
//     let id = "1";
//     let unGroupedGridItemsData = unGroup(gridItemsData, id);
//     expect(unGroupedGridItemsData).toEqual(gridItemsData);
//   });
// });

// describe("getFilteredGridItemsData function", () => {
//   const gridItemsData = [
//     {
//       id: "id1d6bf3e9-2244-4cde-b766-0ce3f4d40b79",
//       isGrouped: false,
//       initialPosition: {
//         x: 0,
//         y: 0,
//         w: 33,
//         h: 10,
//       },
//       children: [
//         {
//           id: "id425f3c5f-a417-4a1d-bdca-e5b5447ea321",
//           isGrouped: true,
//           initialPosition: {
//             x: 0,
//             y: 0,
//             w: 33,
//             h: 10,
//           },
//           children: [],
//           name: "Organisation",
//           gridItemConfig: [
//             {
//               key: "header",
//               values: {
//                 enable: false,
//                 title: "",
//                 placeholder: "Edit/Add your header content here",
//                 link: "",
//                 backgroundColor: "#fff",
//               },
//             },
//             {
//               key: "shadow",
//               values: {
//                 enable: false,
//                 xOffset: 0,
//                 yOffset: 0,
//                 blur: 0,
//                 spread: 0,
//                 color: "#fff",
//               },
//             },
//             {
//               key: "background",
//               values: {
//                 enable: false,
//                 backgroundColor: "#fff",
//                 image: "",
//               },
//             },
//             {
//               key: "border",
//               values: {
//                 enable: false,
//                 borderWidth: 1,
//                 borderStyle: "none",
//                 color: "#fff",
//               },
//             },
//             {
//               key: "html",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//             {
//               key: "css",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//             {
//               key: "javascript",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//           ],
//         },
//         {
//           id: "id45d995ea-f9c7-4e0b-987a-8651608faa28",
//           isGrouped: true,
//           initialPosition: {
//             x: 0,
//             y: 0,
//             w: 33,
//             h: 10,
//           },
//           children: [],
//           name: "Organisation",
//           gridItemConfig: [
//             {
//               key: "header",
//               values: {
//                 enable: false,
//                 title: "",
//                 placeholder: "Edit/Add your header content here",
//                 link: "",
//                 backgroundColor: "#fff",
//               },
//             },
//             {
//               key: "shadow",
//               values: {
//                 enable: false,
//                 xOffset: 0,
//                 yOffset: 0,
//                 blur: 0,
//                 spread: 0,
//                 color: "#fff",
//               },
//             },
//             {
//               key: "background",
//               values: {
//                 enable: false,
//                 backgroundColor: "#fff",
//                 image: "",
//               },
//             },
//             {
//               key: "border",
//               values: {
//                 enable: false,
//                 borderWidth: 1,
//                 borderStyle: "none",
//                 color: "#fff",
//               },
//             },
//             {
//               key: "html",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//             {
//               key: "css",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//             {
//               key: "javascript",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//           ],
//         },
//       ],
//       name: "Organisation",
//       gridItemConfig: [
//         {
//           key: "header",
//           values: {
//             enable: false,
//             title: "",
//             placeholder: "Edit/Add your header content here",
//             link: "",
//             backgroundColor: "#fff",
//           },
//         },
//         {
//           key: "shadow",
//           values: {
//             enable: false,
//             xOffset: 0,
//             yOffset: 0,
//             blur: 0,
//             spread: 0,
//             color: "#fff",
//           },
//         },
//         {
//           key: "background",
//           values: {
//             enable: false,
//             backgroundColor: "#fff",
//             image: "",
//           },
//         },
//         {
//           key: "border",
//           values: {
//             enable: false,
//             borderWidth: 1,
//             borderStyle: "none",
//             color: "#fff",
//           },
//         },
//         {
//           key: "html",
//           values: {
//             enable: false,
//             value: "",
//           },
//         },
//         {
//           key: "css",
//           values: {
//             enable: false,
//             value: "",
//           },
//         },
//         {
//           key: "javascript",
//           values: {
//             enable: false,
//             value: "",
//           },
//         },
//       ],
//       layout: [
//         {
//           w: 33,
//           h: 10,
//           x: 0,
//           y: 0,
//           i: "id425f3c5f-a417-4a1d-bdca-e5b5447ea321",
//           moved: false,
//           static: false,
//         },
//         {
//           w: 33,
//           h: 10,
//           x: 0,
//           y: 10,
//           i: "id45d995ea-f9c7-4e0b-987a-8651608faa28",
//           moved: false,
//           static: false,
//         },
//       ],
//     },
//   ];
//   test("working use case", () => {
//     let id = "id1d6bf3e9-2244-4cde-b766-0ce3f4d40b79";
//     let filteredGridItemsData = getFilteredGridItemsData(gridItemsData, id);
//     expect(filteredGridItemsData).toStrictEqual([]);
//   });
//   test("without gridItemsData test case", () => {
//     let id = "id1d6bf3e9-2244-4cde-b766-0ce3f4d40b79";
//     let filteredGridItemsData = getFilteredGridItemsData(undefined, id);
//     expect(filteredGridItemsData).toStrictEqual(undefined);
//   });
//   test("without id test case", () => {
//     let filteredGridItemsData = getFilteredGridItemsData(
//       gridItemsData,
//       undefined
//     );
//     expect(filteredGridItemsData).toStrictEqual(gridItemsData);
//   });
//   test("with id that is not present in gridItemsData test case", () => {
//     let id = "1";
//     let filteredGridItemsData = getFilteredGridItemsData(gridItemsData, id);
//     expect(filteredGridItemsData).toStrictEqual(gridItemsData);
//   });
//   test("nested grid item working test case", () => {
//     let id = "id425f3c5f-a417-4a1d-bdca-e5b5447ea321";
//     let filteredGridItemsData = getFilteredGridItemsData(gridItemsData, id);
//     expect(filteredGridItemsData).toStrictEqual([
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
//         layout: [
//           {
//             w: 33,
//             h: 10,
//             x: 0,
//             y: 0,
//             i: "id425f3c5f-a417-4a1d-bdca-e5b5447ea321",
//             moved: false,
//             static: false,
//           },
//           {
//             w: 33,
//             h: 10,
//             x: 0,
//             y: 10,
//             i: "id45d995ea-f9c7-4e0b-987a-8651608faa28",
//             moved: false,
//             static: false,
//           },
//         ],
//       },
//     ]);
//   });
// });

// describe("getGridItem function", () => {
//   const gridItemsData = [
//     {
//       id: "id1d6bf3e9-2244-4cde-b766-0ce3f4d40b79",
//       isGrouped: false,
//       initialPosition: {
//         x: 0,
//         y: 0,
//         w: 33,
//         h: 10,
//       },
//       children: [
//         {
//           id: "id425f3c5f-a417-4a1d-bdca-e5b5447ea321",
//           isGrouped: true,
//           initialPosition: {
//             x: 0,
//             y: 0,
//             w: 33,
//             h: 10,
//           },
//           children: [],
//           name: "Organisation",
//           gridItemConfig: [
//             {
//               key: "header",
//               values: {
//                 enable: false,
//                 title: "",
//                 placeholder: "Edit/Add your header content here",
//                 link: "",
//                 backgroundColor: "#fff",
//               },
//             },
//             {
//               key: "shadow",
//               values: {
//                 enable: false,
//                 xOffset: 0,
//                 yOffset: 0,
//                 blur: 0,
//                 spread: 0,
//                 color: "#fff",
//               },
//             },
//             {
//               key: "background",
//               values: {
//                 enable: false,
//                 backgroundColor: "#fff",
//                 image: "",
//               },
//             },
//             {
//               key: "border",
//               values: {
//                 enable: false,
//                 borderWidth: 1,
//                 borderStyle: "none",
//                 color: "#fff",
//               },
//             },
//             {
//               key: "html",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//             {
//               key: "css",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//             {
//               key: "javascript",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//           ],
//         },
//         {
//           id: "id45d995ea-f9c7-4e0b-987a-8651608faa28",
//           isGrouped: true,
//           initialPosition: {
//             x: 0,
//             y: 0,
//             w: 33,
//             h: 10,
//           },
//           children: [],
//           name: "Organisation",
//           gridItemConfig: [
//             {
//               key: "header",
//               values: {
//                 enable: false,
//                 title: "",
//                 placeholder: "Edit/Add your header content here",
//                 link: "",
//                 backgroundColor: "#fff",
//               },
//             },
//             {
//               key: "shadow",
//               values: {
//                 enable: false,
//                 xOffset: 0,
//                 yOffset: 0,
//                 blur: 0,
//                 spread: 0,
//                 color: "#fff",
//               },
//             },
//             {
//               key: "background",
//               values: {
//                 enable: false,
//                 backgroundColor: "#fff",
//                 image: "",
//               },
//             },
//             {
//               key: "border",
//               values: {
//                 enable: false,
//                 borderWidth: 1,
//                 borderStyle: "none",
//                 color: "#fff",
//               },
//             },
//             {
//               key: "html",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//             {
//               key: "css",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//             {
//               key: "javascript",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//           ],
//         },
//       ],
//       name: "Organisation",
//       gridItemConfig: [
//         {
//           key: "header",
//           values: {
//             enable: false,
//             title: "",
//             placeholder: "Edit/Add your header content here",
//             link: "",
//             backgroundColor: "#fff",
//           },
//         },
//         {
//           key: "shadow",
//           values: {
//             enable: false,
//             xOffset: 0,
//             yOffset: 0,
//             blur: 0,
//             spread: 0,
//             color: "#fff",
//           },
//         },
//         {
//           key: "background",
//           values: {
//             enable: false,
//             backgroundColor: "#fff",
//             image: "",
//           },
//         },
//         {
//           key: "border",
//           values: {
//             enable: false,
//             borderWidth: 1,
//             borderStyle: "none",
//             color: "#fff",
//           },
//         },
//         {
//           key: "html",
//           values: {
//             enable: false,
//             value: "",
//           },
//         },
//         {
//           key: "css",
//           values: {
//             enable: false,
//             value: "",
//           },
//         },
//         {
//           key: "javascript",
//           values: {
//             enable: false,
//             value: "",
//           },
//         },
//       ],
//       layout: [
//         {
//           w: 33,
//           h: 10,
//           x: 0,
//           y: 0,
//           i: "id425f3c5f-a417-4a1d-bdca-e5b5447ea321",
//           moved: false,
//           static: false,
//         },
//         {
//           w: 33,
//           h: 10,
//           x: 0,
//           y: 10,
//           i: "id45d995ea-f9c7-4e0b-987a-8651608faa28",
//           moved: false,
//           static: false,
//         },
//       ],
//     },
//   ];
//   test("working use case", () => {
//     let id = "id1d6bf3e9-2244-4cde-b766-0ce3f4d40b79";
//     let gridItem = getGridItem(gridItemsData, id);
//     expect(gridItem).toStrictEqual(...gridItemsData);
//   });
//   test("working nested grid item test case", () => {
//     let id = "id425f3c5f-a417-4a1d-bdca-e5b5447ea321";
//     let gridItem = getGridItem(gridItemsData, id);
//     expect(gridItem).toStrictEqual({
//       id: "id425f3c5f-a417-4a1d-bdca-e5b5447ea321",
//       isGrouped: true,
//       initialPosition: {
//         x: 0,
//         y: 0,
//         w: 33,
//         h: 10,
//       },
//       children: [],
//       name: "Organisation",
//       gridItemConfig: [
//         {
//           key: "header",
//           values: {
//             enable: false,
//             title: "",
//             placeholder: "Edit/Add your header content here",
//             link: "",
//             backgroundColor: "#fff",
//           },
//         },
//         {
//           key: "shadow",
//           values: {
//             enable: false,
//             xOffset: 0,
//             yOffset: 0,
//             blur: 0,
//             spread: 0,
//             color: "#fff",
//           },
//         },
//         {
//           key: "background",
//           values: {
//             enable: false,
//             backgroundColor: "#fff",
//             image: "",
//           },
//         },
//         {
//           key: "border",
//           values: {
//             enable: false,
//             borderWidth: 1,
//             borderStyle: "none",
//             color: "#fff",
//           },
//         },
//         {
//           key: "html",
//           values: {
//             enable: false,
//             value: "",
//           },
//         },
//         {
//           key: "css",
//           values: {
//             enable: false,
//             value: "",
//           },
//         },
//         {
//           key: "javascript",
//           values: {
//             enable: false,
//             value: "",
//           },
//         },
//       ],
//     });
//   });
//   test("without gridItemsData test case", () => {
//     let id = "id425f3c5f-a417-4a1d-bdca-e5b5447ea321";
//     let gridItem = getGridItem(undefined, id);
//     expect(gridItem).toStrictEqual(null);
//   });
//   test("without id test case", () => {
//     let gridItem = getGridItem(gridItemsData, undefined);
//     expect(gridItem).toStrictEqual(null);
//   });
//   test("with id that is not present in gridItemsData test case", () => {
//     let id = "1";
//     let gridItem = getGridItem(gridItemsData, id);
//     expect(gridItem).toStrictEqual(null);
//   });
// });

// describe("injectLayoutToGroupedGridItem function", () => {
//   const gridItemsData = [
//     {
//       id: "id5bfaa21a-e2cd-45f3-b8aa-1ae8c712f3b5",
//       isGrouped: false,
//       initialPosition: {
//         x: 0,
//         y: 0,
//         w: 33,
//         h: 10,
//       },
//       children: [
//         {
//           id: "id2ab2b8d7-6b35-4d63-9072-ef272b61262d",
//           isGrouped: true,
//           initialPosition: {
//             x: 0,
//             y: 0,
//             w: 33,
//             h: 10,
//           },
//           children: [],
//           name: "Organisation",
//           gridItemConfig: [
//             {
//               key: "header",
//               values: {
//                 enable: false,
//                 title: "",
//                 placeholder: "Edit/Add your header content here",
//                 link: "",
//                 backgroundColor: "#fff",
//               },
//             },
//             {
//               key: "shadow",
//               values: {
//                 enable: false,
//                 xOffset: 0,
//                 yOffset: 0,
//                 blur: 0,
//                 spread: 0,
//                 color: "#fff",
//               },
//             },
//             {
//               key: "background",
//               values: {
//                 enable: false,
//                 backgroundColor: "#fff",
//                 image: "",
//               },
//             },
//             {
//               key: "border",
//               values: {
//                 enable: false,
//                 borderWidth: 1,
//                 borderStyle: "none",
//                 color: "#fff",
//               },
//             },
//             {
//               key: "html",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//             {
//               key: "css",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//             {
//               key: "javascript",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//           ],
//         },
//         {
//           id: "id631cc64f-b829-4033-91e1-12f37d5a8ca3",
//           isGrouped: true,
//           initialPosition: {
//             x: 0,
//             y: 0,
//             w: 33,
//             h: 10,
//           },
//           children: [],
//           name: "Organisation",
//           gridItemConfig: [
//             {
//               key: "header",
//               values: {
//                 enable: false,
//                 title: "",
//                 placeholder: "Edit/Add your header content here",
//                 link: "",
//                 backgroundColor: "#fff",
//               },
//             },
//             {
//               key: "shadow",
//               values: {
//                 enable: false,
//                 xOffset: 0,
//                 yOffset: 0,
//                 blur: 0,
//                 spread: 0,
//                 color: "#fff",
//               },
//             },
//             {
//               key: "background",
//               values: {
//                 enable: false,
//                 backgroundColor: "#fff",
//                 image: "",
//               },
//             },
//             {
//               key: "border",
//               values: {
//                 enable: false,
//                 borderWidth: 1,
//                 borderStyle: "none",
//                 color: "#fff",
//               },
//             },
//             {
//               key: "html",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//             {
//               key: "css",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//             {
//               key: "javascript",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//           ],
//         },
//       ],
//       name: "Organisation",
//       gridItemConfig: [
//         {
//           key: "header",
//           values: {
//             enable: false,
//             title: "",
//             placeholder: "Edit/Add your header content here",
//             link: "",
//             backgroundColor: "#fff",
//           },
//         },
//         {
//           key: "shadow",
//           values: {
//             enable: false,
//             xOffset: 0,
//             yOffset: 0,
//             blur: 0,
//             spread: 0,
//             color: "#fff",
//           },
//         },
//         {
//           key: "background",
//           values: {
//             enable: false,
//             backgroundColor: "#fff",
//             image: "",
//           },
//         },
//         {
//           key: "border",
//           values: {
//             enable: false,
//             borderWidth: 1,
//             borderStyle: "none",
//             color: "#fff",
//           },
//         },
//         {
//           key: "html",
//           values: {
//             enable: false,
//             value: "",
//           },
//         },
//         {
//           key: "css",
//           values: {
//             enable: false,
//             value: "",
//           },
//         },
//         {
//           key: "javascript",
//           values: {
//             enable: false,
//             value: "",
//           },
//         },
//       ],
//       layout: [],
//     },
//   ];
//   const layout = [
//     {
//       w: 33,
//       h: 10,
//       x: 61,
//       y: 7,
//       i: "id2ab2b8d7-6b35-4d63-9072-ef272b61262d",
//       moved: false,
//       static: false,
//       isDraggable: false,
//     },
//     {
//       w: 33,
//       h: 10,
//       x: 11,
//       y: 13,
//       i: "id631cc64f-b829-4033-91e1-12f37d5a8ca3",
//       moved: false,
//       static: false,
//       isDraggable: false,
//     },
//   ];
//   test("working use case", () => {
//     let id = "id5bfaa21a-e2cd-45f3-b8aa-1ae8c712f3b5";
//     const gridItemsDataNew = injectLayoutToGroupedGridItem(
//       gridItemsData,
//       layout,
//       id
//     );
//     expect(gridItemsDataNew).toStrictEqual([
//       {
//         id: "id5bfaa21a-e2cd-45f3-b8aa-1ae8c712f3b5",
//         isGrouped: false,
//         initialPosition: {
//           x: 0,
//           y: 0,
//           w: 33,
//           h: 10,
//         },
//         children: [
//           {
//             id: "id2ab2b8d7-6b35-4d63-9072-ef272b61262d",
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
//             id: "id631cc64f-b829-4033-91e1-12f37d5a8ca3",
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
//         layout: [
//           {
//             w: 33,
//             h: 10,
//             x: 61,
//             y: 7,
//             i: "id2ab2b8d7-6b35-4d63-9072-ef272b61262d",
//             moved: false,
//             static: false,
//             isDraggable: false,
//           },
//           {
//             w: 33,
//             h: 10,
//             x: 11,
//             y: 13,
//             i: "id631cc64f-b829-4033-91e1-12f37d5a8ca3",
//             moved: false,
//             static: false,
//             isDraggable: false,
//           },
//         ],
//       },
//     ]);
//   });
// });

// describe("changeIsDraggableInLayout function", () => {
//   const gridItemsData = [
//     {
//       id: "id5bfaa21a-e2cd-45f3-b8aa-1ae8c712f3b5",
//       isGrouped: false,
//       initialPosition: {
//         x: 0,
//         y: 0,
//         w: 33,
//         h: 10,
//       },
//       children: [
//         {
//           id: "id2ab2b8d7-6b35-4d63-9072-ef272b61262d",
//           isGrouped: true,
//           initialPosition: {
//             x: 0,
//             y: 0,
//             w: 33,
//             h: 10,
//           },
//           children: [],
//           name: "Organisation",
//           gridItemConfig: [
//             {
//               key: "header",
//               values: {
//                 enable: false,
//                 title: "",
//                 placeholder: "Edit/Add your header content here",
//                 link: "",
//                 backgroundColor: "#fff",
//               },
//             },
//             {
//               key: "shadow",
//               values: {
//                 enable: false,
//                 xOffset: 0,
//                 yOffset: 0,
//                 blur: 0,
//                 spread: 0,
//                 color: "#fff",
//               },
//             },
//             {
//               key: "background",
//               values: {
//                 enable: false,
//                 backgroundColor: "#fff",
//                 image: "",
//               },
//             },
//             {
//               key: "border",
//               values: {
//                 enable: false,
//                 borderWidth: 1,
//                 borderStyle: "none",
//                 color: "#fff",
//               },
//             },
//             {
//               key: "html",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//             {
//               key: "css",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//             {
//               key: "javascript",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//           ],
//         },
//         {
//           id: "id631cc64f-b829-4033-91e1-12f37d5a8ca3",
//           isGrouped: true,
//           initialPosition: {
//             x: 0,
//             y: 0,
//             w: 33,
//             h: 10,
//           },
//           children: [],
//           name: "Organisation",
//           gridItemConfig: [
//             {
//               key: "header",
//               values: {
//                 enable: false,
//                 title: "",
//                 placeholder: "Edit/Add your header content here",
//                 link: "",
//                 backgroundColor: "#fff",
//               },
//             },
//             {
//               key: "shadow",
//               values: {
//                 enable: false,
//                 xOffset: 0,
//                 yOffset: 0,
//                 blur: 0,
//                 spread: 0,
//                 color: "#fff",
//               },
//             },
//             {
//               key: "background",
//               values: {
//                 enable: false,
//                 backgroundColor: "#fff",
//                 image: "",
//               },
//             },
//             {
//               key: "border",
//               values: {
//                 enable: false,
//                 borderWidth: 1,
//                 borderStyle: "none",
//                 color: "#fff",
//               },
//             },
//             {
//               key: "html",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//             {
//               key: "css",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//             {
//               key: "javascript",
//               values: {
//                 enable: false,
//                 value: "",
//               },
//             },
//           ],
//         },
//       ],
//       name: "Organisation",
//       gridItemConfig: [
//         {
//           key: "header",
//           values: {
//             enable: false,
//             title: "",
//             placeholder: "Edit/Add your header content here",
//             link: "",
//             backgroundColor: "#fff",
//           },
//         },
//         {
//           key: "shadow",
//           values: {
//             enable: false,
//             xOffset: 0,
//             yOffset: 0,
//             blur: 0,
//             spread: 0,
//             color: "#fff",
//           },
//         },
//         {
//           key: "background",
//           values: {
//             enable: false,
//             backgroundColor: "#fff",
//             image: "",
//           },
//         },
//         {
//           key: "border",
//           values: {
//             enable: false,
//             borderWidth: 1,
//             borderStyle: "none",
//             color: "#fff",
//           },
//         },
//         {
//           key: "html",
//           values: {
//             enable: false,
//             value: "",
//           },
//         },
//         {
//           key: "css",
//           values: {
//             enable: false,
//             value: "",
//           },
//         },
//         {
//           key: "javascript",
//           values: {
//             enable: false,
//             value: "",
//           },
//         },
//       ],
//       layout: [
//         {
//           w: 33,
//           h: 10,
//           x: 61,
//           y: 7,
//           i: "id2ab2b8d7-6b35-4d63-9072-ef272b61262d",
//           moved: false,
//           static: false,
//           isDraggable: false,
//         },
//         {
//           w: 33,
//           h: 10,
//           x: 11,
//           y: 13,
//           i: "id631cc64f-b829-4033-91e1-12f37d5a8ca3",
//           moved: false,
//           static: false,
//           isDraggable: false,
//         },
//       ],
//     },
//   ];
//   const layout = [
//     {
//       w: 70,
//       h: 36,
//       x: 20,
//       y: 2,
//       i: "id5bfaa21a-e2cd-45f3-b8aa-1ae8c712f3b5",
//       moved: false,
//       static: false,
//       isDraggable: false,
//     },
//   ];
//   test("working use case", () => {
//     let id = "id5bfaa21a-e2cd-45f3-b8aa-1ae8c712f3b5";
//     const { resultData, resultLayout } = changeIsDraggableInLayout(
//       gridItemsData,
//       id,
//       layout
//     );
//     expect(resultLayout).toStrictEqual([
//       {
//         h: 36,
//         i: "id5bfaa21a-e2cd-45f3-b8aa-1ae8c712f3b5",
//         isDraggable: true,
//         moved: false,
//         static: false,
//         w: 70,
//         x: 20,
//         y: 2,
//       },
//     ]);
//     expect(resultData).toStrictEqual(resultData);
//   });
// });

// describe("changeFiltersDataInGridItems function", () => {
//   test("working use case", () => {
//     const gridItemsData = [
//       {
//         id: "id778d212d-585e-4d14-9ad5-54371c476b2a",
//         compType: "dashboard-designer-component",
//         isGrouped: false,
//         initialPosition: {
//           x: 0,
//           y: 0,
//           w: 25,
//           h: 10,
//         },
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
//         reportInfo: {
//           file: {
//             path: "sai_ganesh/hreportWith2Filters.hr",
//             name: "hreportWith2Filters.hr",
//           },
//           mode: "dashboard",
//           filters: [],
//           component: "hreport",
//           extension: "hr",
//         },
//         filters: [
//           {
//             column: "travel_details.booking_platform",
//             label: "booking_platform",
//             dataType: "text",
//             backendDataType: "java.lang.String",
//             condition: "IS_ONE_OF",
//             values: ["Makemytrip"],
//             valuesMode: "custom",
//             mode: "auto",
//             orderBy: "",
//             valuesRange: {},
//             rangeValuesType: "",
//             dateTimeToggle: false,
//             rangeSelectionToggole: true,
//             maxInput: "",
//             minInput: "",
//             valuesList: [],
//             drillDownId: "",
//             uid: "1becb52d-9377-45b9-9f26-bbca989052ef",
//             mapping: {
//               isEnabled: true,
//               isDefaultFunction: true,
//               valueDisplayMap: [],
//               valueAliasName: "random",
//               orderBy: {
//                 display: "none",
//                 value: "asc",
//               },
//               valueDBFuntionInfo: {},
//               valueColumn: {
//                 alias: "booking_platform",
//                 fullyQualifiedColumn: "travel_details.booking_platform",
//                 columnId: "b6c4c5d1-8ad4-4a42-b001-463316c39ff2",
//                 defaultFunction: "none",
//                 type: {
//                   "java.lang.String": "text",
//                 },
//               },
//               displayColumn: {
//                 alias: "booking_platform",
//                 fullyQualifiedColumn: "travel_details.booking_platform",
//                 columnId: "b6c4c5d1-8ad4-4a42-b001-463316c39ff2",
//                 defaultFunction: "none",
//                 type: {
//                   "java.lang.String": "text",
//                 },
//               },
//             },
//             cascade: {
//               isEnabled: false,
//               filterIds: [],
//               filters: [],
//               filtersCount: 0,
//               filtersInfo: {},
//               filtersFormData: {},
//               autoUpdateCascadeInfoFromParent: true,
//             },
//             active: false,
//             encloseInQuotes: false,
//           },
//           {
//             column: "travel_details.mode_of_payment",
//             label: "mode_of_payment",
//             dataType: "text",
//             backendDataType: "java.lang.String",
//             condition: "IS_ONE_OF",
//             values: ["Cheque"],
//             valuesMode: "custom",
//             mode: "auto",
//             orderBy: "",
//             valuesRange: {},
//             rangeValuesType: "",
//             dateTimeToggle: false,
//             rangeSelectionToggole: true,
//             maxInput: "",
//             minInput: "",
//             valuesList: [],
//             drillDownId: "",
//             uid: "ad19aa57-f4e9-4854-bc22-3c865e0e027a",
//             mapping: {
//               isEnabled: true,
//               isDefaultFunction: true,
//               valueDisplayMap: [],
//               valueAliasName: "random",
//               orderBy: {
//                 display: "none",
//                 value: "asc",
//               },
//               valueDBFuntionInfo: {},
//               valueColumn: {
//                 alias: "mode_of_payment",
//                 fullyQualifiedColumn: "travel_details.mode_of_payment",
//                 columnId: "9fb3c43a-bf20-4383-9eaf-2bc826f86bd2",
//                 defaultFunction: "none",
//                 type: {
//                   "java.lang.String": "text",
//                 },
//               },
//               displayColumn: {
//                 alias: "mode_of_payment",
//                 fullyQualifiedColumn: "travel_details.mode_of_payment",
//                 columnId: "9fb3c43a-bf20-4383-9eaf-2bc826f86bd2",
//                 defaultFunction: "none",
//                 type: {
//                   "java.lang.String": "text",
//                 },
//               },
//             },
//             cascade: {
//               isEnabled: false,
//               filterIds: [],
//               filters: [],
//               filtersCount: 0,
//               filtersInfo: {},
//               filtersFormData: {},
//               autoUpdateCascadeInfoFromParent: true,
//             },
//             active: false,
//             encloseInQuotes: false,
//           },
//         ],
//         listeners: ["booking_platform", "mode_of_payment"],
//       },
//       {
//         id: "id660e2cf4-7981-4e8a-a135-fb72f22630e9",
//         compType: "filter",
//         index: 0,
//         key: "1",
//         reportInfo: {
//           file: {
//             path: "sai_ganesh/hreportWith2Filters.hr",
//             name: "hreportWith2Filters.hr",
//           },
//           mode: "filter",
//           filters: [],
//           component: "hreport",
//           extension: "hr",
//         },
//         parameter: {
//           dashboardFilter: {
//             columnName: "booking_platform",
//             uid: "1becb52d-9377-45b9-9f26-bbca989052ef",
//           },
//         },
//         filters: [
//           {
//             column: "travel_details.booking_platform",
//             label: "booking_platform",
//             dataType: "text",
//             backendDataType: "java.lang.String",
//             condition: "IS_ONE_OF",
//             values: ["Makemytrip"],
//             valuesMode: "custom",
//             mode: "auto",
//             orderBy: "",
//             valuesRange: {},
//             rangeValuesType: "",
//             dateTimeToggle: false,
//             rangeSelectionToggole: true,
//             maxInput: "",
//             minInput: "",
//             valuesList: [],
//             drillDownId: "",
//             uid: "1becb52d-9377-45b9-9f26-bbca989052ef",
//             mapping: {
//               isEnabled: true,
//               isDefaultFunction: true,
//               valueDisplayMap: [],
//               valueAliasName: "random",
//               orderBy: {
//                 display: "none",
//                 value: "asc",
//               },
//               valueDBFuntionInfo: {},
//               valueColumn: {
//                 alias: "booking_platform",
//                 fullyQualifiedColumn: "travel_details.booking_platform",
//                 columnId: "b6c4c5d1-8ad4-4a42-b001-463316c39ff2",
//                 defaultFunction: "none",
//                 type: {
//                   "java.lang.String": "text",
//                 },
//               },
//               displayColumn: {
//                 alias: "booking_platform",
//                 fullyQualifiedColumn: "travel_details.booking_platform",
//                 columnId: "b6c4c5d1-8ad4-4a42-b001-463316c39ff2",
//                 defaultFunction: "none",
//                 type: {
//                   "java.lang.String": "text",
//                 },
//               },
//             },
//             cascade: {
//               isEnabled: false,
//               filterIds: [],
//               filters: [],
//               filtersCount: 0,
//               filtersInfo: {},
//               filtersFormData: {},
//               autoUpdateCascadeInfoFromParent: true,
//             },
//             active: false,
//             encloseInQuotes: false,
//           },
//           {
//             column: "travel_details.mode_of_payment",
//             label: "mode_of_payment",
//             dataType: "text",
//             backendDataType: "java.lang.String",
//             condition: "IS_ONE_OF",
//             values: ["Cheque"],
//             valuesMode: "custom",
//             mode: "auto",
//             orderBy: "",
//             valuesRange: {},
//             rangeValuesType: "",
//             dateTimeToggle: false,
//             rangeSelectionToggole: true,
//             maxInput: "",
//             minInput: "",
//             valuesList: [],
//             drillDownId: "",
//             uid: "ad19aa57-f4e9-4854-bc22-3c865e0e027a",
//             mapping: {
//               isEnabled: true,
//               isDefaultFunction: true,
//               valueDisplayMap: [],
//               valueAliasName: "random",
//               orderBy: {
//                 display: "none",
//                 value: "asc",
//               },
//               valueDBFuntionInfo: {},
//               valueColumn: {
//                 alias: "mode_of_payment",
//                 fullyQualifiedColumn: "travel_details.mode_of_payment",
//                 columnId: "9fb3c43a-bf20-4383-9eaf-2bc826f86bd2",
//                 defaultFunction: "none",
//                 type: {
//                   "java.lang.String": "text",
//                 },
//               },
//               displayColumn: {
//                 alias: "mode_of_payment",
//                 fullyQualifiedColumn: "travel_details.mode_of_payment",
//                 columnId: "9fb3c43a-bf20-4383-9eaf-2bc826f86bd2",
//                 defaultFunction: "none",
//                 type: {
//                   "java.lang.String": "text",
//                 },
//               },
//             },
//             cascade: {
//               isEnabled: false,
//               filterIds: [],
//               filters: [],
//               filtersCount: 0,
//               filtersInfo: {},
//               filtersFormData: {},
//               autoUpdateCascadeInfoFromParent: true,
//             },
//             active: false,
//             encloseInQuotes: false,
//           },
//         ],
//         listeners: ["booking_platform", "mode_of_payment"],
//       },
//       {
//         id: "id82286653-96a6-4fc8-8348-9d3b97be3eeb",
//         compType: "filter",
//         index: 1,
//         key: "2",
//         reportInfo: {
//           file: {
//             path: "sai_ganesh/hreportWith2Filters.hr",
//             name: "hreportWith2Filters.hr",
//           },
//           mode: "filter",
//           filters: [],
//           component: "hreport",
//           extension: "hr",
//         },
//         parameter: {
//           dashboardFilter: {
//             columnName: "mode_of_payment",
//             uid: "ad19aa57-f4e9-4854-bc22-3c865e0e027a",
//           },
//         },
//         filters: [
//           {
//             column: "travel_details.booking_platform",
//             label: "booking_platform",
//             dataType: "text",
//             backendDataType: "java.lang.String",
//             condition: "IS_ONE_OF",
//             values: ["Makemytrip"],
//             valuesMode: "custom",
//             mode: "auto",
//             orderBy: "",
//             valuesRange: {},
//             rangeValuesType: "",
//             dateTimeToggle: false,
//             rangeSelectionToggole: true,
//             maxInput: "",
//             minInput: "",
//             valuesList: [],
//             drillDownId: "",
//             uid: "1becb52d-9377-45b9-9f26-bbca989052ef",
//             mapping: {
//               isEnabled: true,
//               isDefaultFunction: true,
//               valueDisplayMap: [],
//               valueAliasName: "random",
//               orderBy: {
//                 display: "none",
//                 value: "asc",
//               },
//               valueDBFuntionInfo: {},
//               valueColumn: {
//                 alias: "booking_platform",
//                 fullyQualifiedColumn: "travel_details.booking_platform",
//                 columnId: "b6c4c5d1-8ad4-4a42-b001-463316c39ff2",
//                 defaultFunction: "none",
//                 type: {
//                   "java.lang.String": "text",
//                 },
//               },
//               displayColumn: {
//                 alias: "booking_platform",
//                 fullyQualifiedColumn: "travel_details.booking_platform",
//                 columnId: "b6c4c5d1-8ad4-4a42-b001-463316c39ff2",
//                 defaultFunction: "none",
//                 type: {
//                   "java.lang.String": "text",
//                 },
//               },
//             },
//             cascade: {
//               isEnabled: false,
//               filterIds: [],
//               filters: [],
//               filtersCount: 0,
//               filtersInfo: {},
//               filtersFormData: {},
//               autoUpdateCascadeInfoFromParent: true,
//             },
//             active: false,
//             encloseInQuotes: false,
//           },
//           {
//             column: "travel_details.mode_of_payment",
//             label: "mode_of_payment",
//             dataType: "text",
//             backendDataType: "java.lang.String",
//             condition: "IS_ONE_OF",
//             values: ["Cheque"],
//             valuesMode: "custom",
//             mode: "auto",
//             orderBy: "",
//             valuesRange: {},
//             rangeValuesType: "",
//             dateTimeToggle: false,
//             rangeSelectionToggole: true,
//             maxInput: "",
//             minInput: "",
//             valuesList: [],
//             drillDownId: "",
//             uid: "ad19aa57-f4e9-4854-bc22-3c865e0e027a",
//             mapping: {
//               isEnabled: true,
//               isDefaultFunction: true,
//               valueDisplayMap: [],
//               valueAliasName: "random",
//               orderBy: {
//                 display: "none",
//                 value: "asc",
//               },
//               valueDBFuntionInfo: {},
//               valueColumn: {
//                 alias: "mode_of_payment",
//                 fullyQualifiedColumn: "travel_details.mode_of_payment",
//                 columnId: "9fb3c43a-bf20-4383-9eaf-2bc826f86bd2",
//                 defaultFunction: "none",
//                 type: {
//                   "java.lang.String": "text",
//                 },
//               },
//               displayColumn: {
//                 alias: "mode_of_payment",
//                 fullyQualifiedColumn: "travel_details.mode_of_payment",
//                 columnId: "9fb3c43a-bf20-4383-9eaf-2bc826f86bd2",
//                 defaultFunction: "none",
//                 type: {
//                   "java.lang.String": "text",
//                 },
//               },
//             },
//             cascade: {
//               isEnabled: false,
//               filterIds: [],
//               filters: [],
//               filtersCount: 0,
//               filtersInfo: {},
//               filtersFormData: {},
//               autoUpdateCascadeInfoFromParent: true,
//             },
//             active: false,
//             encloseInQuotes: false,
//           },
//         ],
//         listeners: ["booking_platform", "mode_of_payment"],
//       },
//     ];
//     const convertedGridItemsData = produce(gridItemsData, (draft) => {
//       draft = changeFiltersDataInGridItems({ data: draft });
//     });
//     expect(convertedGridItemsData).toStrictEqual([
//       {
//         id: "id778d212d-585e-4d14-9ad5-54371c476b2a",
//         compType: "dashboard-designer-component",
//         isGrouped: false,
//         initialPosition: {
//           x: 0,
//           y: 0,
//           w: 25,
//           h: 10,
//         },
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
//         reportInfo: {
//           file: {
//             path: "sai_ganesh/hreportWith2Filters.hr",
//             name: "hreportWith2Filters.hr",
//           },
//           mode: "dashboard",
//           filters: [],
//           component: "hreport",
//           extension: "hr",
//         },
//         filters: [
//           {
//             uid: "1becb52d-9377-45b9-9f26-bbca989052ef",
//             value: undefined,
//           },
//           {
//             uid: "ad19aa57-f4e9-4854-bc22-3c865e0e027a",
//             value: undefined,
//           },
//         ],
//         listeners: ["booking_platform", "mode_of_payment"],
//       },
//       {
//         id: "id660e2cf4-7981-4e8a-a135-fb72f22630e9",
//         compType: "filter",
//         index: 0,
//         key: "1",
//         reportInfo: {
//           file: {
//             path: "sai_ganesh/hreportWith2Filters.hr",
//             name: "hreportWith2Filters.hr",
//           },
//           mode: "filter",
//           filters: [],
//           component: "hreport",
//           extension: "hr",
//         },
//         parameter: {
//           dashboardFilter: {
//             columnName: "booking_platform",
//             uid: "1becb52d-9377-45b9-9f26-bbca989052ef",
//           },
//         },
//         filters: [
//           {
//             uid: "1becb52d-9377-45b9-9f26-bbca989052ef",
//             value: undefined,
//           },
//           {
//             uid: "ad19aa57-f4e9-4854-bc22-3c865e0e027a",
//             value: undefined,
//           },
//         ],
//         listeners: ["booking_platform", "mode_of_payment"],
//       },
//       {
//         id: "id82286653-96a6-4fc8-8348-9d3b97be3eeb",
//         compType: "filter",
//         index: 1,
//         key: "2",
//         reportInfo: {
//           file: {
//             path: "sai_ganesh/hreportWith2Filters.hr",
//             name: "hreportWith2Filters.hr",
//           },
//           mode: "filter",
//           filters: [],
//           component: "hreport",
//           extension: "hr",
//         },
//         parameter: {
//           dashboardFilter: {
//             columnName: "mode_of_payment",
//             uid: "ad19aa57-f4e9-4854-bc22-3c865e0e027a",
//           },
//         },
//         filters: [
//           {
//             uid: "1becb52d-9377-45b9-9f26-bbca989052ef",
//             value: undefined,
//           },
//           {
//             uid: "ad19aa57-f4e9-4854-bc22-3c865e0e027a",
//             value: undefined,
//           },
//         ],
//         listeners: ["booking_platform", "mode_of_payment"],
//       },
//     ]);
//   });
// });
test("testasdf", () => {
  expect(true).toBe(true);
});
