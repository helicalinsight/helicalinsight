import { injectLayoutToGroupedGridItem } from "../../../../../../components/hi-dashboard-designer/utils/recursive-functions";

describe("injectLayoutToGroupedGridItem", () => {
  const data = [
    {
      id: 1,
      layout: null,
      children: [
        {
          id: 2,
          layout: null,
          children: [
            {
              id: 3,
              layout: null,
              children: [],
            },
          ],
        },
      ],
    },
  ];

  test("should update the layout property of the item with the given id and return the modified data", () => {
    const layout = { x: 0, y: 0, w: 2, h: 2 };
    const id = 3;

    const result = injectLayoutToGroupedGridItem(data, layout, id);

    expect(result).toEqual([
      {
        id: 1,
        layout: null,
        children: [
          {
            id: 2,
            layout: null,
            children: [
              {
                id: 3,
                layout: { x: 0, y: 0, w: 2, h: 2 },
                children: [],
              },
            ],
          },
        ],
      },
    ]);
  });

  test("should not modify the data if the given id is not found", () => {
    const layout = { x: 0, y: 0, w: 2, h: 2 };
    const id = 4;

    const result = injectLayoutToGroupedGridItem(data, layout, id);

    expect(result).toEqual(data);
  });
});
