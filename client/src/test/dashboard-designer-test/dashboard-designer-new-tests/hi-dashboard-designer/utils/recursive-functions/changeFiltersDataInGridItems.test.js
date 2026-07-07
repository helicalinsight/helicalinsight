import { changeFiltersDataInGridItems } from "../../../../../../components/hi-dashboard-designer/utils/recursive-functions";

describe("changeFiltersDataInGridItems", () => {
  test("should change filters data in grid items", () => {
    const data = [
      {
        id: 1,
        filters: [
          { uid: "filter1", value: "value1" },
          { uid: "filter2", value: "value2" },
        ],
        children: [
          {
            id: 2,
            filters: [{ uid: "filter3", value: "value3" }],
          },
        ],
      },
    ];

    const expectedResult = [
      {
        id: 1,
        filters: [
          { uid: "filter1", value: "value1" },
          { uid: "filter2", value: "value2" },
        ],
        children: [
          {
            id: 2,
            filters: [{ uid: "filter3", value: "value3" }],
          },
        ],
      },
    ];

    expect(changeFiltersDataInGridItems({ data })).toEqual(expectedResult);
  });

  test("should handle null and undefined inputs", () => {
    expect(changeFiltersDataInGridItems({ data: null })).toEqual([]);
    expect(changeFiltersDataInGridItems({ data: undefined })).toEqual([]);
  });
});
