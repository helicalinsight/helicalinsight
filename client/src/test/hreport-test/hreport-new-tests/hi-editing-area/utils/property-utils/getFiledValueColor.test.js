import { getFiledValueColor } from "../../../../../../components/hi-reports/hi-editing-area/utils/property-utils";

describe("getFiledValueColor function", () => {
  test("to check the functionality of getFiledValueColor function", () => {
    const obj = {
      employee_name: "Ahmed Haider",
    };

    const formatColor = {
      defaultColor: {
        r: 84,
        g: 108,
        b: 230,
        a: 1,
      },
      minimum: {
        r: 89,
        g: 96,
        b: 126,
        a: 1,
      },
      maximum: {
        r: 51,
        g: 81,
        b: 237,
        a: 1,
      },
      showAll: true,
      dataColors: [
        ["formatColorField", "aab5bb73-886a-48d6-a002-163db5f71e0c"],
        ["formatColorStyle", "fieldValue"],
        [
          "defaultColor",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        ["showAll", true],
        [
          "50",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "33",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "46",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "42",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "22",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "49",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "28",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "47",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "40",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "41",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "43",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "51",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "38",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "26",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "34",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "31",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "23",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "52",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "44",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "37",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "29",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "25",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "30",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "27",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "39",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
      ],
      formatColorField: "aab5bb73-886a-48d6-a002-163db5f71e0c",
      formatColorStyle: "fieldValue",
    };

    const result = getFiledValueColor(obj, formatColor);
    const expectedResult = undefined;

    expect(result).toEqual(expectedResult);
  });
});
