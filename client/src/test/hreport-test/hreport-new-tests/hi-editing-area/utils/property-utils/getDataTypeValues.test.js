import { getDataTypeValues } from "../../../../../../components/hi-reports/hi-editing-area/utils/property-utils";

describe("getDataTypeValues function", () => {
  test("when the dataType is 'numeric'", () => {
    const dataType = "numeric";
    const result = getDataTypeValues(dataType);
    const expectedResult = {
      apply: ["pane"],
      decimalPlace: 2,
      displayUnits: "None",
      isApplyClicked: false,
      numberCustom: "",
      percentage: false,
      prefix: "",
      suffix: "",
      thousandSperator: false,
      enableCustomFormatting: false
    };
    expect(result).toEqual(expectedResult);
  });

  test("when the dataType is 'date'", () => {
    const dataType = "date";
    const result = getDataTypeValues(dataType);
    const expectedResult = {
      apply: ["pane"],
      dateCustom: "",
      dateSeperator: "-",
      day: "dayNumWithZero",
      isApplyClicked: false,
      month: "monthNumWithZero",
      quarter: "none",
      week: "none",
      year: "4digit",
      enableCustomFormatting: false
    };
    expect(result).toEqual(expectedResult);
  });

  test("when the dataType is 'time'", () => {
    const dataType = "time";
    const result = getDataTypeValues(dataType);
    const expectedResult = {
      apply: ["pane"],
      hour: "24hr",
      isApplyClicked: false,
      milliSecond: "milliSecondsNumber",
      minute: "mintuesNumber",
      second: "secondsNumber",
      timeCustom: "",
      timeSeperator: ":",
      enableCustomFormatting: false
    };
    expect(result).toEqual(expectedResult);
  });

  test("when the dataType is 'dateTime'", () => {
    const dataType = "dateTime";
    const result = getDataTypeValues(dataType);
    const expectedResult = {
      apply: ["pane"],
      dateCustom: "",
      dateSeperator: "-",
      day: "dayNumWithZero",
      hour: "24hr",
      isApplyClicked: false,
      milliSecond: "milliSecondsNumber",
      minute: "mintuesNumber",
      month: "monthNumWithZero",
      quarter: "none",
      second: "secondsNumber",
      timeCustom: "",
      timeSeperator: ":",
      week: "none",
      year: "4digit",
      enableCustomFormatting: false
    };
    expect(result).toEqual(expectedResult);
  });
});
