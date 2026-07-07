import { divStyles, getCalendarData, getPropertyColorField, getTickValues, prepareHierarchicalData, prepareHierarchyData, setStyles, subVizTypes } from "../../../components/hi-reports/hi-viz-area/ant-charts/ant-utils";

describe("ant-chart test cases", () => {
  describe('setStyles function', () => {
    let container;

    beforeEach(() => {
      container = document.createElement('div');
    });

    it('sets the styles correctly', () => {
      setStyles(container, { width: '100px', height: '200px' });
      expect(container.style.width).toBe('100px');
      expect(container.style.height).toBe('200px');
    });

    it('does not set styles that are not in the object', () => {
      setStyles(container, { width: '100px', height: '200px' });
      expect(container.style.backgroundColor).toBe('');
    });

    it('overrides existing styles', () => {
      container.style.width = '50px';
      setStyles(container, { width: '100px', height: '200px' });
      expect(container.style.width).toBe('100px');
    });
  });

  describe('divStyles object', () => {
    it('contains all expected style properties', () => {
      expect(divStyles).toHaveProperty('position');
      expect(divStyles).toHaveProperty('color');
      expect(divStyles).toHaveProperty('background');
      expect(divStyles).toHaveProperty('boxShadow');
      expect(divStyles).toHaveProperty('borderRadius');
      expect(divStyles).toHaveProperty('padding');
      expect(divStyles).toHaveProperty('fontSize');
    });

    it('has the expected property values', () => {
      expect(divStyles.position).toBe('absolute');
      expect(divStyles.color).toBe('white');
      expect(divStyles.background).toBe('#616161');
      expect(divStyles.boxShadow).toBe('rgb(174, 174, 174) 0px 0px 10px');
      expect(divStyles.borderRadius).toBe('4px');
      expect(divStyles.padding).toBe('5px');
      expect(divStyles.fontSize).toBe('12px');
    });
  });

  describe("subVizTypes", () => {
    test("contains 'bar'", () => {
      expect(subVizTypes).toContain("bar");
    });

    test("contains 'line'", () => {
      expect(subVizTypes).toContain("line");
    });

    test("contains 'area'", () => {
      expect(subVizTypes).toContain("area");
    });

    test("contains 'point'", () => {
      expect(subVizTypes).toContain("point");
    });

    test("contains 'tick'", () => {
      expect(subVizTypes).toContain("tick");
    });

    test("contains 'arc'", () => {
      expect(subVizTypes).toContain("arc");
    });

    test("contains 'doughnut'", () => {
      expect(subVizTypes).toContain("doughnut");
    });

    test("contains 'heatmap'", () => {
      expect(subVizTypes).toContain("heatmap");
    });

    test("contains 'text'", () => {
      expect(subVizTypes).toContain("text");
    });
  });
})

describe('test prepareHierarchyData', () => {
  it('should return an empty array when data is undefined', () => {
    expect(prepareHierarchyData(undefined, 'name', 'value')).toEqual([]);
  });

  it('should return an empty array when data is an empty array', () => {
    expect(prepareHierarchyData([], 'name', 'value')).toEqual([]);
  });

  it('should return an array of objects with the correct properties', () => {
    const data = [
      { name: 'John', value: 10 },
      { name: 'Jane', value: 20 },
    ];
    const result = prepareHierarchyData(data, 'name', 'value');
    expect(result).toEqual([
      { name: 'John', value: 10 },
      { name: 'Jane', value: 20 },
    ]);
  });

  it('should include additional properties from the input objects', () => {
    const data = [
      { name: 'John', value: 10, age: 30 },
      { name: 'Jane', value: 20, age: 40 },
    ];
    const result = prepareHierarchyData(data, 'name', 'value');
    expect(result).toEqual([
      { name: 'John', value: 10, age: 30 },
      { name: 'Jane', value: 20, age: 40 },
    ]);
  });
});

describe('test getTickValues', () => {
  it('should return an array of tick values when given an array of data', () => {
    const data = [1, 2, 3, 4, 5];
    const result = getTickValues(data);
    expect(result).toEqual([0, 0.2, 0.4, 0.6, 0.8, 1]);
  });

  it('should handle an empty array', () => {
    const data = [];
    const result = getTickValues(data);
    expect(result).toEqual([NaN]);
  });

  it('should handle an array with a single element', () => {
    const data = [10];
    const result = getTickValues(data);
    expect(result).toEqual([0, 1]);
  });
});

describe('test prepareHierarchicalData function', () => {

  it('should returns an empty array for empty dimensions or measures', () => {
    expect(prepareHierarchicalData([], ['dim1'], ['measure1'])).toEqual([]);
    expect(prepareHierarchicalData([{ id: 1 }], [], ['measure1'])).toEqual([]);
  });

  it('should returns a flat hierarchy for single dimension and measure', () => {
    const data = [{ id: 1, dim1: 'a', measure1: 10 }];
    const result = prepareHierarchicalData(data, ['dim1'], ['measure1']);
    expect(result).toEqual([{ dim1: 'a', name: 'a', id: 1, measure1: 10, value: 10 }]);
  });

  it('should returns a nested hierarchy for multiple dimensions and measures', () => {
    const data = [
      { id: 1, dim1: 'a', dim2: 'x', measure1: 10, measure2: 20 },
      { id: 2, dim1: 'a', dim2: 'y', measure1: 30, measure2: 40 },
      { id: 3, dim1: 'b', dim2: 'x', measure1: 50, measure2: 60 },
    ];
    const result = prepareHierarchicalData(data, ['dim1', 'dim2'], ['measure1', 'measure2']);
    expect(result).toEqual([
      {
        id: 1,
        name: 'a',
        dim1: 'a',
        dim2: 'x',
        measure1: 40,
        measure2: 60,
        children: [
          { dim1: 'a', dim2: 'x', name: 'x', id: 1, measure1: 10, measure2: 20, value: 20 },
          { dim1: 'a', dim2: 'y', name: 'y', id: 2, measure1: 30, measure2: 40, value: 40 },
        ],
      },
      {
        name: 'b',
        dim2: 'x',
        dim1: 'b',
        id: 3,
        measure1: 50,
        measure2: 60,
        children: [
          { name: 'x', dim1: 'b', dim2: 'x', id: 3, measure1: 50, measure2: 60, value: 60 },
        ],
      },
    ]);
  });

  it('should calculates sum of measures for last dimension', () => {
    const data = [
      { id: 1, dim1: 'a', measure1: 10 },
      { id: 2, dim1: 'a', measure1: 20 },
    ];
    const result = prepareHierarchicalData(data, ['dim1'], ['measure1']);
    expect(result).toEqual([{ name: 'a', dim1: 'a', id: 1, measure1: 30, value: 30 }]);
  });

})

describe('test getPropertyColorField function', () => {
  it('shuold returns null with empty formatColor object', () => {
    const result = getPropertyColorField({});
    expect(result).toBeNull();
  });

  it('should returns null with formatColor object without formatColorStyle property', () => {
    const formatColor = { notFormatColorStyle: 'test' };
    const result = getPropertyColorField(formatColor);
    expect(result).toBeNull();
  });

  it('returns null with formatColor object with empty formatColorStyle property and empty formatColorField', () => {
    const formatColor = { formatColorStyle: '', formatColorField: '' };
    const result = getPropertyColorField(formatColor);
    expect(result).toBeNull();
  });

  it('should returns propertyColorField with formatColor object with formatColorStyle property and valid formatColorField', () => {
    const formatColor = { formatColorStyle: {}, formatColorField: '1234' };
    const report = { fields: [{ id: '1234', alias: 'Test' }] };
    const result = getPropertyColorField(formatColor, report);
    expect(result).toBe('Test');
  });

  it('should returns null with report object without fields property', () => {
    const formatColor = { formatColorStyle: {}, formatColorField: '1234' };
    const report = {};
    const result = getPropertyColorField(formatColor, report);
    expect(result).toBeNull();
  });

  it('should returns empty sting with report object with fields property but no matching formatColorField', () => {
    const formatColor = { formatColorStyle: {}, formatColorField: '1234' };
    const report = { fields: [{ id: '12345', alias: 'test' }] };
    const result = getPropertyColorField(formatColor, report);
    expect(result).toEqual("");
  });
});


describe('test getCalendarData function', () => {
  it('returns original data when data is empty', () => {
    const data = [];
    const xField = 'x';
    const yField = 'y';
    expect(getCalendarData(data, xField, yField)).toEqual(data);
  });

  it('returns original data when xField or yField is missing', () => {
    const data = [{ x: 1, y: 2 }];
    const xField = 'x';
    const yField = null;
    expect(getCalendarData(data, xField, yField)).toEqual(data);
  });

  it('converts xField and yField values to strings', () => {
    const data = [{ x: 1, y: 2 }];
    const xField = 'x';
    const yField = 'y';
    const result = getCalendarData(data, xField, yField);
    expect(result[0][xField]).toBe('1');
    expect(result[0][yField]).toBe('2');
  });

  it('handles non-string values for xField and yField', () => {
    const data = [{ x: true, y: null }];
    const xField = 'x';
    const yField = 'y';
    const result = getCalendarData(data, xField, yField);
    expect(result[0][xField]).toBe('true');
    expect(result[0][yField]).toBe('null');
  });
});
