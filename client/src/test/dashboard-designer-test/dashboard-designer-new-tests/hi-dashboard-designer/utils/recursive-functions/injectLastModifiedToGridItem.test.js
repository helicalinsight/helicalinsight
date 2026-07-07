import { injectLastModifiedToGridItem } from "../../../../../../components/hi-dashboard-designer/utils/recursive-functions";
describe("injectLastModifiedToGridItem", () => {
  test("should inject last modified data to grid item", () => {
    const data = [{ id: 1, children: [{ id: 2, children: [{ id: 3 }] }] }];
    const lastModified = { lastModified: "2022-05-01" };
    const id = 3;

    const expectedResult = [
      {
        id: 1,
        children: [
          { id: 2, children: [{ id: 3, lastModified: "2022-05-01" }] },
        ],
      },
    ];

    const result = injectLastModifiedToGridItem({ data, lastModified, id });

    expect(result).toEqual(expectedResult);
  });

  test("should return the original data if the given id is not found in any grid item", () => {
    const data = [{ id: 1, children: [{ id: 2, children: [{ id: 3 }] }] }];
    const lastModified = { lastModified: "2022-05-01" };
    const id = 4;

    const expectedResult = data;

    const result = injectLastModifiedToGridItem({ data, lastModified, id });

    expect(result).toEqual(expectedResult);
  });

  test("should return an empty array if the data is not defined", () => {
    const lastModified = { lastModified: "2022-05-01" };
    const id = 1;

    const expectedResult = [];

    const result = injectLastModifiedToGridItem({ lastModified, id });

    expect(result).toEqual(expectedResult);
  });
});
