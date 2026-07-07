import { findNestedObj } from "../../../../../components/hi-metadata/utils/findNestedObject";
import { mock_data } from "./findNestedObject.mock.data";

describe("To Test findNestedObj function", () => {
  test("Metadata table select functionality", () => {
    const dsListToRender = mock_data.dsListToRender;
    const record = mock_data.record;
    let result = findNestedObj(dsListToRender, "id", record);
    let expectedResult = {
      id: "9d0d7c2d929cc2f20de2c45f70b9640f",
      name: "CACHE",
      alias: "CACHE",
      dataSource: {
        id: "1000",
        type: "dynamicDataSource",
        baseType: "global.jdbc",
        catSchemaPredicted: false,
        sync: false,
        catalog: "",
        schema: "HIUSER",
        connId: "67iuz",
        dbId: "67iuz",
        classifier: "db.workflow",
        datasourceName: "hiee",
        dsKeyPath:
          "m0lo-7fr0-x2bf-jbwe-a1/b85b-nugy-dcq8-c6q1-on/2byp-yzmz-23sy-1xhd-j2",
        driverType: "Derby",
        database: "HIUSER",
      },
      category: "table",
      connId: "67iuz",
      keyPath:
        "m0lo-7fr0-x2bf-jbwe-a1/b85b-nugy-dcq8-c6q1-on/2byp-yzmz-23sy-1xhd-j2/bc8e-75da-3q0t-fnmb-00",
      uniqueKey: "9d0d7c2d929cc2f20de2c45f70b9640f_67iuz",
      schema: "HIUSER",
      selected: false,
    };
    expect(result).toEqual(expectedResult);
  });
});
