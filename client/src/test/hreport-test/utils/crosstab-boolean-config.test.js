import { getCrossTabBooleanConfig } from "../../../components/hi-reports/hi-viz-area/pivot-view/crosstab-boolean-config";

describe('getCrossTabBooleanConfig', () => {
  test('should return default values if crosstab is not provided', () => {
    const config = getCrossTabBooleanConfig({});
    
    expect(config.showGrandTotals).toBe(false);
    expect(config.showRowGrandTotals).toBe(false);
    expect(config.showColumnGrandTotals).toBe(false);
    expect(config.showSubTotals).toBe(false);
    expect(config.showRowSubTotals).toBe(false);
    expect(config.showColumnSubTotals).toBe(false);
    expect(config.grandTotalsPosition).toBeUndefined();
  });

  test('should set all grand totals and subtotals flags to true if showGrandTotals and showSubTotals are true', () => {
    const crosstab = {
      showGrandTotals: true,
      showSubTotals: true,
    };

    const config = getCrossTabBooleanConfig({ crosstab });

    expect(config.showGrandTotals).toBe(true);
    expect(config.showRowGrandTotals).toBe(true);
    expect(config.showColumnGrandTotals).toBe(true);
    expect(config.showSubTotals).toBe(true);
    expect(config.showRowSubTotals).toBe(true);
    expect(config.showColumnSubTotals).toBe(true);
    expect(config.grandTotalsPosition).toBeUndefined();
  });

  test('should set grand totals flags to true if showGrandTotals is true', () => {
    const crosstab = {
      showGrandTotals: true,
    };

    const config = getCrossTabBooleanConfig({ crosstab });

    expect(config.showGrandTotals).toBe(true);
    expect(config.showRowGrandTotals).toBe(true);
    expect(config.showColumnGrandTotals).toBe(true);
    expect(config.showSubTotals).toBe(false);
    expect(config.showRowSubTotals).toBe(false);
    expect(config.showColumnSubTotals).toBe(false);
    expect(config.grandTotalsPosition).toBeUndefined();
  });

  test('should set subtotals flags to true if showSubTotals is true', () => {
    const crosstab = {
      showSubTotals: true,
    };

    const config = getCrossTabBooleanConfig({ crosstab });

    expect(config.showGrandTotals).toBe(false);
    expect(config.showRowGrandTotals).toBe(false);
    expect(config.showColumnGrandTotals).toBe(false);
    expect(config.showSubTotals).toBe(true);
    expect(config.showRowSubTotals).toBe(true);
    expect(config.showColumnSubTotals).toBe(true);
    expect(config.grandTotalsPosition).toBeUndefined();
  });

//   test('should set grand totals flags based on showRowGrandTotals and showColumnGrandTotals', () => {
//     const crosstab = {
//       showRowGrandTotals: true,
//       showColumnGrandTotals: true,
//     };

//     const config = getCrossTabBooleanConfig({ crosstab });

//     expect(config.showGrandTotals).toBe(true);
//     expect(config.showRowGrandTotals).toBe(true);
//     expect(config.showColumnGrandTotals).toBe(true);
//     expect(config.showSubTotals).toBe(false);
//     expect(config.showRowSubTotals).toBe(false);
//     expect(config.showColumnSubTotals).toBe(false);
//     expect(config.grandTotalsPosition).toBeUndefined();
//   });

//   test('should set subtotals flags based on showRowSubTotals and showColumnSubTotals', () => {
//     const crosstab = {
//       showRowSubTotals: true,
//       showColumnSubTotals: true,
//     };

//     const config = getCrossTabBooleanConfig({ crosstab });

//     expect(config.showGrandTotals).toBe(false);
//     expect(config.showRowGrandTotals).toBe(false);
//     expect(config.showColumnGrandTotals).toBe(false);
//     expect(config.showSubTotals).toBe(true);
//     expect(config.showRowSubTotals).toBe(true);
//     expect(config.showColumnSubTotals).toBe(true);
//     expect(config.grandTotalsPosition).toBeUndefined();
//   });

  test('should set grand totals position if grandTotalsPosition is provided', () => {
    const crosstab = {
      grandTotalsPosition: 'Bottom',
    };

    const config = getCrossTabBooleanConfig({ crosstab });

    expect(config.showGrandTotals).toBe(false);
    expect(config.showRowGrandTotals).toBe(false);
    expect(config.showColumnGrandTotals).toBe(false);
    expect(config.showSubTotals).toBe(false);
    expect(config.showRowSubTotals).toBe(false);
    expect(config.showColumnSubTotals).toBe(false);
    expect(config.grandTotalsPosition).toBe('Bottom');
  });
});
