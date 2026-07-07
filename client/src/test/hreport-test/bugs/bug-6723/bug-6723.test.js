import { sortPropertyPane } from "../../../../components/hi-reports/hi-editing-area/components/properties/properties";

describe("sortPropertyPane", () => {
  test("should sort groupItems based on label", () => {

    const unsortedGroupItems = [
      {
        label: { props: { children: "Title" }, text: "T" },
        key: "title",
      },
      {
        label: { props: { children: "Sub Title" }, text: "U" },
        key: "subTitle",
      },
      {
        label: { props: { children: "Format" }, text: "F" },
        key: "format",
      },
      {
        label: { props: { children: "Cache" }, text: "E" },
        key: "cache",
      },
      {
        label: { props: { children: "Color" }, text: "C" },
        key: "color",
      },
      {
        label: { props: { children: "Bar" }, text: "B" },
        key: "bar",
      },
      {
        label: { props: { children: "Radial" }, text: "I" },
        key: "radial",
      },
      {
        label: { props: { children: "Legend" }, text: "L" },
        key: "legend",
      },
      {
        label: { props: { children: "Axis" }, text: "X" },
        key: "axisRange",
      },
      {
        label: { props: { children: "Card" }, text: "D" },
        key: "card",
      },
      {
        label: { props: { children: "Rotate Labels" }, text: "O" },
        key: "labels",
      },
      {
        label: { props: { children: "Crosstab" }, text: "Y" },
        key: "crosstab",
      },
      {
        label: { props: { children: "Table" }, text: "Z" },
        key: "table",
      },
    ];

    const sortedGroupItems = [
      {
        label: {
          props: {
            children: "Axis",
          },
          text: "X",
        },
        key: "axisRange",
      },
      {
        label: {
          props: {
            children: "Bar",
          },
          text: "B",
        },
        key: "bar",
      },
      {
        label: {
          props: {
            children: "Cache",
          },
          text: "E",
        },
        key: "cache",
      },
      {
        label: {
          props: {
            children: "Card",
          },
          text: "D",
        },
        key: "card",
      },
      {
        label: {
          props: {
            children: "Color",
          },
          text: "C",
        },
        key: "color",
      },
      {
        label: {
          props: {
            children: "Crosstab",
          },
          text: "Y",
        },
        key: "crosstab",
      },
      {
        label: {
          props: {
            children: "Format",
          },
          text: "F",
        },
        key: "format",
      },
      {
        label: {
          props: {
            children: "Legend",
          },
          text: "L",
        },
        key: "legend",
      },
      {
        label: {
          props: {
            children: "Radial",
          },
          text: "I",
        },
        key: "radial",
      },
      {
        label: {
          props: {
            children: "Rotate Labels",
          },
          text: "O",
        },
        key: "labels",
      },
      {
        label: {
          props: {
            children: "Sub Title",
          },
          text: "U",
        },
        key: "subTitle",
      },
      {
        label: {
          props: {
            children: "Table",
          },
          text: "Z",
        },
        key: "table",
      },
      {
        label: {
          props: {
            children: "Title",
          },
          text: "T",
        },
        key: "title",
      },
    ];
    expect(sortedGroupItems).toEqual(sortPropertyPane(unsortedGroupItems));
  });
});
