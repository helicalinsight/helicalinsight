import { getUpdatedLayout } from "../../../../../../components/hi-dashboard-designer/utils/recursive-functions";

describe("getUpdatedLayout", () => {
  const data = [
    {
      id: "1",
      children: [
        {
          id: "2",
          children: [
            {
              id: "3",
              layout: [
                { i: "4", x: 0, y: 0, w: 1, h: 1 },
                { i: "5", x: 1, y: 0, w: 1, h: 1 },
              ],
            },
          ],
        },
      ],
    },
  ];
  const layout = [
    { i: "1", x: 0, y: 0, w: 2, h: 2 },
    { i: "2", x: 0, y: 2, w: 1, h: 1 },
    { i: "3", x: 1, y: 2, w: 1, h: 1 },
    { i: "4", x: 0, y: 0, w: 1, h: 1 },
    { i: "5", x: 1, y: 0, w: 1, h: 1 },
  ];

  test("should update layout of the item with the given id", () => {
    const updatedItem = { w: 2, h: 2 };
    const result = getUpdatedLayout({ data, id: "1", layout, updatedItem });

    expect(result.resultData).toEqual(data);
    expect(result.resultLayout).toEqual([
      { h: 2, i: "1", w: 2, x: 0, y: 0 },
      { h: 1, i: "2", w: 1, x: 0, y: 2 },
      { h: 1, i: "3", w: 1, x: 1, y: 2 },
      { h: 1, i: "4", w: 1, x: 0, y: 0 },
      { h: 1, i: "5", w: 1, x: 1, y: 0 },
    ]);
  });

  test("should update layout of a child item with the given id", () => {
    const updatedItem = { w: 2, h: 2 };
    const result = getUpdatedLayout({ data, id: "3", layout, updatedItem });

    expect(result.resultData).toEqual([
      {
        children: [
          {
            children: [
              {
                id: "3",
                layout: [
                  { h: 1, i: "4", w: 1, x: 0, y: 0 },
                  { h: 1, i: "5", w: 1, x: 1, y: 0 },
                ],
              },
            ],
            id: "2",
          },
        ],
        id: "1",
      },
    ]);
    expect(result.resultLayout).toEqual([
      { h: 2, i: "1", w: 2, x: 0, y: 0 },
      { h: 1, i: "2", w: 1, x: 0, y: 2 },
      { h: 2, i: "3", w: 2, x: 1, y: 2 },
      { h: 1, i: "4", w: 1, x: 0, y: 0 },
      { h: 1, i: "5", w: 1, x: 1, y: 0 },
    ]);
  });
});
