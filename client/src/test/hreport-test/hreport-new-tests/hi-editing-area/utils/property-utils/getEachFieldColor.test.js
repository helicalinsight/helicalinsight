import { getEachFieldColor } from "../../../../../../components/hi-reports/hi-editing-area/utils/property-utils";

describe("getEachFieldColor function", () => {
  test("to check the functionality of getEachFieldColor function", () => {
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
      showAll: true,
      dataColors: [
        ["formatColorField", "19f76b12-aee7-4240-94b8-586c1f170463"],
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
          "Ahmed Haider",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Alec Lynch",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Alex Sharp",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Alvin Singh",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Andrew Campbell",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Bardia Houseman",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Bosco Tan",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Bradley Smith",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Cameron Adams",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Cliff Obrecht",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Daniel Friedman",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Dean McEvoy",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Dean Ramler",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Dorry Kordahi",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Eddie Machaalani",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Herbert Yeung",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Jack Delosa",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Jeremy Levitt",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Jonathan Barouch",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Jonathan Hallinan",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Josiah Humphreys",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Justin Cameron",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Karl Trouchet",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Lex Pedersen",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Luke Trouchet",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Mark Ackroyd",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Mark Harbottle",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Mark McDonald",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Matt Barrie",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Matt Mickiewicz",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Matthew Tripp",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Michael Fox",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Mike Cannon-Brookes",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Mike Knapp",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Mitchell Harper",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Murray Hurps",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Ned Dwyer",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Ned Moorefield",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Pete Moore",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Peter Murray",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Phillip Di Bella",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Ruslan Kogan",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Scott Farquhar",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Sebastien Eckersley Maslin",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Shaon Diwakar",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Sheng Yeo",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Sherman Ma",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Stuart Cook",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Tim Fung",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
      ],
      formatColorField: "19f76b12-aee7-4240-94b8-586c1f170463",
      formatColorStyle: "fieldValue",
    };
    const text = "Andrew Campbell";
    const result = getEachFieldColor(formatColor, text);
    const expectedResult = "rgba(84, 108, 230, 1)";

    expect(result).toEqual(expectedResult);
  });
});
