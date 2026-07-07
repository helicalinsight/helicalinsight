import {
  
    getGridItem
  } from "../../../../../../components/hi-dashboard-designer/utils/recursive-functions";
  
  describe("getGridItem", () => {
    const data = [
      {
        id: 1,
        gridItemConfig: {},
        children: [
          {
            id: 2,
            gridItemConfig: {},
            children: [{ id: 3, gridItemConfig: {}, children: [] }],
          },
          {
            id: 4,
            gridItemConfig: {},
            children: [],
          },
        ],
      },
    ];
  
    test("should return null if data or id is null or undefined", () => {
      expect(getGridItem(null, 1)).toBeNull();
      expect(getGridItem(data, null)).toBeNull();
      expect(getGridItem(undefined, 1)).toBeNull();
      expect(getGridItem(data, undefined)).toBeNull();
    });
  
    test("should return null if the item with the specified id is not found in the data", () => {
      expect(getGridItem(data, 5)).toBeNull();
    });
  
    test("should return the grid item with the specified id if it exists in the data", () => {
      const gridItem = getGridItem(data, 3);
      expect(gridItem).toEqual({
        id: 3,
        gridItemConfig: {},
        children: [],
      });
    });
  
    test("should update the grid item config if updatedGridItemConfig is provided", () => {
      const updatedGridItemConfig = { x: 1, y: 2 };
      const gridItem = getGridItem(data, 2, updatedGridItemConfig);
      expect(gridItem.gridItemConfig).toEqual(updatedGridItemConfig);
    });
  
    test("should update the grid item config for nested items", () => {
      const updatedGridItemConfig = { x: 1, y: 2 };
      const gridItem = getGridItem(data, 3, updatedGridItemConfig);
      expect(gridItem.gridItemConfig).toEqual(updatedGridItemConfig);
    });
  });
  