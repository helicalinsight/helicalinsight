import { getGridChartColorSchemeFromPalette } from "../../../../components/hi-reports/hi-viz-area/utils/grid-chart-utils";

describe('test getGridChartColorSchemeFromPalette', () => {
  it('should return default colors when palette is empty', () => {
    const result = getGridChartColorSchemeFromPalette();
    expect(result).toEqual(["#a9d3f2", "#f4a4c7"]);
  });

  it('should return palette when data is empty', () => {
    const palette = ["#123456", "#789012"];
    const result = getGridChartColorSchemeFromPalette(palette);
    expect(result).toEqual(palette);
  });

  it('should return palette when data length is less than or equal to palette length', () => {
    const palette = ["#123456", "#789012"];
    const data = [1, 2];
    const result = getGridChartColorSchemeFromPalette(palette, data);
    expect(result).toEqual(palette);
  });

  it('should return concatenated palette when data length is greater than palette length', () => {
    const palette = ["#123456", "#789012"];
    const data = [1, 2, 3, 4];
    const result = getGridChartColorSchemeFromPalette(palette, data);
    expect(result).toEqual(["#123456", "#789012", "#123456", "#789012"]);
  });

  it('should return default colors when palette is not an array', () => {
    const palette = "not an array";
    const result = getGridChartColorSchemeFromPalette(palette);
    expect(result).toEqual(["#a9d3f2", "#f4a4c7"]);
  });

  it('should return default colors when data is not an array', () => {
    const palette = ["#123456", "#789012"];
    const data = "not an array";
    const result = getGridChartColorSchemeFromPalette(palette, data);
    expect(result).toEqual(["#a9d3f2", "#f4a4c7"]);
  });
});