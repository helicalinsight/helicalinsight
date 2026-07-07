import { convertChildComponents } from "../../../../../../components/hi-dashboard-designer/utils/old-hi-app-data-converstions";

describe("convertChildComponents", () => {
  test("should convert child components correctly", () => {
    const components = {
      1: {
        gs_attr: { x: 1, y: 2, height: 3, width: 4 },
        childComponents: [],
        name: "Component 1",
        options: { dir: "path/to/dir", file: "file1", ext: "csv" },
      },
      2: {
        gs_attr: { x: 5, y: 6, height: 7, width: 8 },
        childComponents: ["comp_3", "comp_4"],
        name: "Component 2",
        options: null,
      },
      3: {
        gs_attr: { x: 9, y: 10, height: 11, width: 12 },
        childComponents: [],
        label: "Component 3",
        options: { dir: "path/to/another/dir", file: "file2", ext: "xlsx" },
      },
      4: {
        gs_attr: { x: 13, y: 14, height: 15, width: 16 },
        childComponents: [],
        name: "Component 4",
        options: null,
      },
    };

    const currentGridItem = {
      gs_attr: {},
      childComponents: ["comp_2"],
    };

    const allChilds = [];

    convertChildComponents(components, currentGridItem, allChilds);

    expect(currentGridItem.layout).toEqual([
      { i: "2", x: 5, y: 6, h: 7, w: 8 },
    ]);

    expect(currentGridItem.children).toEqual([
      {
        childComponents: ["comp_3", "comp_4"],
        children: [
          {
            childComponents: [],
            gridItemConfig: [
              {
                key: "header",
                values: {
                  backgroundColor: "#fff",
                  enable: false,
                  link: "",
                  placeholder: "Edit/Add your header content here",
                  title: "",
                },
              },
              {
                key: "shadow",
                values: {
                  blur: 0,
                  color: "#fff",
                  enable: false,
                  spread: 0,
                  xOffset: 0,
                  yOffset: 0,
                },
              },
              {
                key: "background",
                values: { backgroundColor: "#fff", enable: false, image: "" },
              },
              {
                key: "border",
                values: {
                  borderStyle: "none",
                  borderWidth: 1,
                  color: "#fff",
                  enable: false,
                },
              },
              { key: "html", values: { enable: false, value: "" } },
              { key: "css", values: { enable: false, value: "" } },
              { key: "javascript", values: { enable: false, value: "" } },
            ],
            gs_attr: { height: 11, width: 12, x: 9, y: 10 },
            id: "3",
            isGrouped: true,
            label: "Component 3",
            name: "Component 3",
            options: { dir: "path/to/another/dir", ext: "xlsx", file: "file2" },
            reportInfo: {
              extension: "xlsx",
              file: { name: "file2", path: "path/to/another/dir/file2" },
              filters: [],
              mode: "dashboard",
            },
          },
          {
            childComponents: [],
            gridItemConfig: [
              {
                key: "header",
                values: {
                  backgroundColor: "#fff",
                  enable: false,
                  link: "",
                  placeholder: "Edit/Add your header content here",
                  title: "",
                },
              },
              {
                key: "shadow",
                values: {
                  blur: 0,
                  color: "#fff",
                  enable: false,
                  spread: 0,
                  xOffset: 0,
                  yOffset: 0,
                },
              },
              {
                key: "background",
                values: { backgroundColor: "#fff", enable: false, image: "" },
              },
              {
                key: "border",
                values: {
                  borderStyle: "none",
                  borderWidth: 1,
                  color: "#fff",
                  enable: false,
                },
              },
              { key: "html", values: { enable: false, value: "" } },
              { key: "css", values: { enable: false, value: "" } },
              { key: "javascript", values: { enable: false, value: "" } },
            ],
            gs_attr: { height: 15, width: 16, x: 13, y: 14 },
            id: "4",
            isGrouped: true,
            name: "Component 4",
            options: null,
            reportInfo: undefined,
          },
        ],
        gridItemConfig: [
          {
            key: "header",
            values: {
              backgroundColor: "#fff",
              enable: false,
              link: "",
              placeholder: "Edit/Add your header content here",
              title: "",
            },
          },
          {
            key: "shadow",
            values: {
              blur: 0,
              color: "#fff",
              enable: false,
              spread: 0,
              xOffset: 0,
              yOffset: 0,
            },
          },
          {
            key: "background",
            values: { backgroundColor: "#fff", enable: false, image: "" },
          },
          {
            key: "border",
            values: {
              borderStyle: "none",
              borderWidth: 1,
              color: "#fff",
              enable: false,
            },
          },
          { key: "html", values: { enable: false, value: "" } },
          { key: "css", values: { enable: false, value: "" } },
          { key: "javascript", values: { enable: false, value: "" } },
        ],
        gs_attr: { height: 7, width: 8, x: 5, y: 6 },
        id: "2",
        isGrouped: true,
        layout: [
          { h: 11, i: "3", w: 12, x: 9, y: 10 },
          { h: 15, i: "4", w: 16, x: 13, y: 14 },
        ],
        name: "Component 2",
        options: null,
        reportInfo: undefined,
      },
    ]);

    expect(allChilds).toEqual(["3", "4", "2"]);
  });
});
