import { hrCubeColumns } from "../../../components/hi-sidebar/hr-hreportSidebar/hr-sidebar-helperMethods";
import { handleHrCubeDataSource } from "../../../components/hi-sidebar/hr-hreportSidebar/hr-sidebar-helperMethods";

describe("hrCubeColumns", () => {
  test("should return an array of columns data", () => {
    const inputData = {};

    const result = hrCubeColumns(inputData);

    expect(result).toBeInstanceOf(Array);

    result.forEach((column) => {
      expect(column).toHaveProperty("title");
      expect(column).toHaveProperty("dataIndex");
      expect(column).toHaveProperty("key");
      expect(column).toHaveProperty("className");
      expect(column).toHaveProperty("render");
    });
  });
});

describe("handleHrCubeDataSource", () => {
  test("should filter the data source based on search text", () => {
    const datasourceData = [
      {
        alias: "Item 1",
        children: [
          {
            alias: "Subitem 1",
            children: [],
          },
        ],
      },
      {
        alias: "Item 2",
        children: [
          {
            alias: "Subitem 2",
            children: [],
          },
        ],
      },
    ];

    const searchText = "item 1";

    const result = handleHrCubeDataSource({ datasourceData, searchText });

    expect(result).toBeInstanceOf(Array);

    expect(result.length).toBe(1);
    expect(result[0].alias).toBe("Item 1");
    expect(result[0].children.length).toBe(1);
    expect(result[0].children[0].alias).toBe("Subitem 1");
  });

  test("should return an empty array if no matching items are found", () => {
    const datasourceData = [
      {
        alias: "Item 1",
        children: [],
      },
      {
        alias: "Item 2",
        children: [],
      },
    ];

    const searchText = "item 3";

    const result = handleHrCubeDataSource({ datasourceData, searchText });

    expect(result).toBeInstanceOf(Array);
    expect(result.length).toBe(0);
  });
});
