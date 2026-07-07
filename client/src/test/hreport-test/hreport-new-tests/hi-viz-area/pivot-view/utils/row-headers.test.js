import {
  getRowHeaders1,
  createRowPanes1,
} from "../../../../../../components/hi-reports/hi-viz-area/pivot-view/utils/row-headers";

describe('getRowHeaders1', () => {
  const data = [
    { category: 'electronics', type: 'mobile', color: 'red', price: 1 },
    { category: 'electronics', type: 'laptop', color: 'black', price: 2 },
    { category: 'clothes', type: 'tshirt', color: 'orange', price: 3 },
    { category: 'clothes', type: 'pant', color: 'green', price: 4 },
  ];

  test('returns an array of unique row headers', () => {
    const rows = ['category', 'type'];
    const expectedOutput = ['electronics.mobile', 'electronics.laptop', 'clothes.tshirt', 'clothes.pant'];
    const output = getRowHeaders1(data, rows);
    expect(output).toEqual(expectedOutput);
  });

  test('returns an empty array when no rows are specified', () => {
    const rows = [];
    const expectedOutput = [];
    const output = getRowHeaders1(data, rows);
    expect(output).toEqual(expectedOutput);
  });

  test('handles missing fields gracefully', () => {
    const rows = ['category', 'origin'];
    const expectedOutput = ["electronics.undefined", "electronics.undefined", "clothes.undefined", "clothes.undefined"];
    const output = getRowHeaders1(data, rows);
    expect(output).toEqual(expectedOutput);
  });
});

describe("createRowPanes1", () => {
  const data = [
    { category: "Furniture", subcategory: "Chairs", price: 100 },
    { category: "Furniture", subcategory: "Tables", price: 200 },
    { category: "books", subcategory: "hindi", price: 300 },
    { category: "books", subcategory: "english", price: 400 },
  ];

  it("should return an array of row panes", () => {
    const rows = ["category", "subcategory"];
    const cellHeight = 50;
    const expectedOutput = [
      {
        axis: "category",
        hasChild: true,
        height: 100,
        levelName: "Furniture",
        members: [
          {
            axis: "subcategory",
            hasChild: false,
            height: 50,
            levelName: "Furniture.Chairs",
          },
          {
            axis: "subcategory",
            hasChild: false,
            height: 50,
            levelName: "Furniture.Tables",
          },
        ],
      },
      {
        axis: "category",
        hasChild: true,
        height: 100,
        levelName: "books",
        members: [
          {
            axis: "subcategory",
            hasChild: false,
            height: 50,
            levelName: "books.hindi",
          },
          {
            axis: "subcategory",
            hasChild: false,
            height: 50,
            levelName: "books.english",
          },
        ],
      },
    ];
    const output = createRowPanes1(data, rows, cellHeight);
    expect(output).toEqual(expectedOutput);
  });
});
