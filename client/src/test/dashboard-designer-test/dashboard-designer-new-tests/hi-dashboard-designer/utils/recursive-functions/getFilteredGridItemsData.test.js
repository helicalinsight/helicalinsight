import {
    
    getFilteredGridItemsData
    
  } from "../../../../../../components/hi-dashboard-designer/utils/recursive-functions";

 
  describe("getFilteredGridItemsData", () => {
    const data = [
      {
        id: 1,
        children: [{ id: 2, children: [{ id: 3, children: [{ id: 4 }] }] }],
      },
      {
        id: 5,
        children: [
          {
            id: 6,
            children: [
              {
                id: 7,
                children: [
                  {
                    id: 8,
                  },
                ],
              },
            ],
          },
        ],
      },
    ];
  
    test("should return the filtered data without the item with the specified id", () => {
      const result = getFilteredGridItemsData(data, 3);
      expect(result).toEqual([
        { children: [{ children: [], id: 2 }], id: 1 },
        {
          children: [{ children: [{ children: [{ id: 8 }], id: 7 }], id: 6 }],
          id: 5,
        },
      ]);
    });
  
    test("should return the original data if the id is not found", () => {
      const result = getFilteredGridItemsData(data, 9);
      expect(result).toEqual(data);
    });
  });
  

  