import { getGridItemLayoutObject } from "../../../../../../components/hi-dashboard-designer/utils/recursive-functions";

describe("getGridItemLayoutObject", () => {
  const data = [
    {
      id: "item1",
      layout: [
        { i: "item1", x: 0, y: 0, w: 1, h: 1 },
        { i: "item2", x: 1, y: 0, w: 1, h: 1 },
      ],
      children: [
        {
          id: "item2",
          layout: [{ i: "item2", x: 0, y: 0, w: 1, h: 1 }],
          children: [],
        },
      ],
    },
  ];

  const layout = [
    { i: "item1", x: 0, y: 0, w: 1, h: 1 },
    { i: "item2", x: 1, y: 0, w: 1, h: 1 },
  ];

  test("returns the layout object for the specified item ID", () => {
    const result = getGridItemLayoutObject({ data, id: "item1", layout });
    expect(result).toEqual({ i: "item1", x: 0, y: 0, w: 1, h: 1 });
  });

  test("returns an empty object if no layout object is found for the specified item ID", () => {
    const result = getGridItemLayoutObject({ data, id: "item3", layout });
    expect(result).toEqual({});
  });

  test("handles nested children objects correctly", () => {
    const result = getGridItemLayoutObject({ data, id: "item2", layout });
    expect(result).toEqual({ i: "item2", x: 1, y: 0, w: 1, h: 1 });
  });
});
