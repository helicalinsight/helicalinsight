import {
  getChartsList,
  getPivotCharts,
} from "../../../../../components/hi-reports/hi-editing-area/utils/constants";

describe("getChartsList", () => {
  test('returns an array containing "Hilo"', () => {
    const result = getChartsList();
    expect(result).toContain("Hilo");
  });

  test("returns an array", () => {
    const result = getPivotCharts();
    expect(result).toEqual([
      "Column",
      "Bar",
      "Line",
      "Spline",
      "Area",
      "SplineArea",
      "StepLine",
      "StepArea",
      "StackingColumn",
      "StackingBar",
      "StackingArea",
      "StackingColumn100",
      "StackingBar100",
      "StackingArea100",
      "Scatter",
      "Bubble",
      "Polar",
      "Radar",
      "Pareto",
    ]);
  });
});
