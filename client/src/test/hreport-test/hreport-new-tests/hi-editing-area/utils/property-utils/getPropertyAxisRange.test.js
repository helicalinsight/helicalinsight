import { getPropertyAxisRange } from "../../../../../../components/hi-reports/hi-editing-area/utils/property-utils";

describe("getPropertyAxisRange function", () => {
  test("to check the functionality of getPropertyAxisRange function", () => {
    const field = {
      id: "ee78559a-c99c-40d0-886d-18346527d26f",
      data: {
        applyRangeOn: "ee78559a-c99c-40d0-886d-18346527d26f",
        minRange: "50",
        maxRange: "500",
        name: "sum_dim_id",
        dataType: "numeric",
      },
    };

    const chartType = "antChart";

    const result = getPropertyAxisRange(field, chartType);
    const expectedResult = { max: 500, min: 50, "tickInterval": 50 };

    expect(result).toEqual(expectedResult);
  });
});
