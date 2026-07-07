import {
    unGroup
   
  } from "../../../../../../components/hi-dashboard-designer/utils/recursive-functions";

  describe("unGroup", () => {
    const testData = [
      {
        id: 1,
        name: "Parent 1",
        children: [
          { id: 2, name: "Child 1" },
          { id: 3, name: "Child 2" },
        ],
      },
      {
        id: 4,
        name: "Parent 2",
        children: [
          {
            id: 5,
            name: "Child 3",
            children: [
              { id: 6, name: "Grandchild 1" },
              { id: 7, name: "Grandchild 2" },
            ],
          },
          { id: 8, name: "Child 4" },
        ],
      },
    ];
  
    test("should ungroup children of a given id", () => {
      const result = unGroup(testData, 4);
      expect(result).toEqual([
        {
          id: 1,
          name: "Parent 1",
          children: [
            { id: 2, name: "Child 1" },
            { id: 3, name: "Child 2" },
          ],
        },
        {
          id: 5,
          name: "Child 3",
          children: [
            { id: 6, name: "Grandchild 1" },
            { id: 7, name: "Grandchild 2" },
          ],
        },
        { id: 8, name: "Child 4" },
      ]);
    });
  
    test("should return the original data if either data or id is undefined", () => {
      const result = unGroup(testData);
      expect(result).toEqual(testData);
  
      const result2 = unGroup(undefined, 4);
      expect(result2).toEqual(undefined);
    });
  });
  
