import { changeIsDraggableInLayout } from "../../../../../../components/hi-dashboard-designer/utils/recursive-functions";

describe("changeIsDraggableInLayout", () => {
  const data = [
    {
      id: "1",
      children: [
        {
          id: "2",
          children: [],
          layout: [{ i: "2", static: true }],
        },
      ],
      layout: [{ i: "1", static: true }],
    },
  ];

  const layout = [
    { i: "1", static: true },
    { i: "2", static: true },
  ];

  test("should toggle the `static` property of the layout item with the specified ID", () => {
    const id = "2";
    const result = changeIsDraggableInLayout(data, id, layout);
    const expectedLayout = [
      { i: "1", static: true },
      { i: "2", static: false },
    ];
    expect(result.resultLayout).toEqual(expectedLayout);
  });

  test("should not modify the data if the layout item with the specified ID is not found", () => {
    const id = "3";
    const result = changeIsDraggableInLayout(data, id, layout);
    expect(result.resultData).toEqual(data);
    expect(result.resultLayout).toEqual(layout);
  });

  test("should recursively update the layout and data of child items", () => {
    const id = "1";
    const result = changeIsDraggableInLayout(data, id, layout);
    const expectedData = [
      {
        children: [
          { children: [], id: "2", layout: [{ i: "2", static: true }] },
        ],
        id: "1",
        layout: [{ i: "1", static: true }],
      },
    ];
    const expectedLayout = [
      { i: "1", static: false },
      { i: "2", static: false },
    ];
    expect(result.resultData).toEqual(expectedData);
    expect(result.resultLayout).toEqual(expectedLayout);
  });
});
