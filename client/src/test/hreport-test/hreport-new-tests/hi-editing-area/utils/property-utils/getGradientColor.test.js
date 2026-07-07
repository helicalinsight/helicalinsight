import { getGradientColor } from "../../../../../../components/hi-reports/hi-editing-area/utils/property-utils";

describe("getGradientColor function", () => {
  test("to check the functionality of getGradientColor function", () => {
    const obj = {
      sum_travel_cost: 0,
    };
    const formatColor = {
      defaultColor: {
        r: 84,
        g: 108,
        b: 230,
        a: 1,
      },
      minimum: {
        r: 183,
        g: 192,
        b: 232,
        a: 1,
      },
      maximum: {
        r: 84,
        g: 108,
        b: 230,
        a: 1,
      },
      showAll: false,
      dataColors: [
        ["formatColorField", "48b04a95-d0e5-4c12-aa89-ec7f2faa3bb1"],
        ["formatColorStyle", "gradient"],
        [
          "minimum",
          {
            r: 183,
            g: 192,
            b: 232,
            a: 1,
          },
        ],
        [
          "maximum",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        ["backgroundColor", null],
      ],
      formatColorField: "48b04a95-d0e5-4c12-aa89-ec7f2faa3bb1",
      formatColorStyle: "gradient",
    };
    const data = [
      {
        sum_travel_cost: 3641245,
        booking_platform: "Agent",
      },
      {
        sum_travel_cost: 6719588,
        booking_platform: "Makemytrip",
      },
      {
        sum_travel_cost: 8173137,
        booking_platform: "Website",
      },
    ];

    const result = getGradientColor(obj, formatColor, data);
    const expectedResult = "rgba(84, 108, 230, 1)";
    expect(result).toEqual(expectedResult);
  });
});
