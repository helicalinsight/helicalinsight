import { changeExapandSettings, getLastNode, isAggregatedTitle } from "../../../../components/hi-reports/hi-viz-area/pivot-view/utils/pivot-table-utils";

describe('getLastNode', () => {
    const rootNode = {
      children: [
        {
          className: 'e-collapse e-icons',
          children: []
        },
        {
          className: 'leaf-node',
          children: []
        },
        {
          className: 'branch-node',
          children: [
            {
              className: 'leaf-node',
              children: []
            }
          ]
        }
      ]
    };
  
    it('returns the node itself if it has no children', () => {
      const node = {
        children: []
      };
      expect(getLastNode(node)).toBe(node);
    });
  
    it('returns the last non-"e-collapse e-icons" child node', () => {
      expect(getLastNode(rootNode)).toEqual({
        className: 'leaf-node',
        children: []
      });
    });
  
});

describe('isAggregatedTitle', () => {
  it('should return true for valid aggregated titles', () => {
    expect(isAggregatedTitle('Total Sum of Sales')).toEqual({isTrue: true, text: 'Sales'});
    expect(isAggregatedTitle('Total Avg of Orders')).toEqual({isTrue: true, text: 'Orders'});
    expect(isAggregatedTitle('Total Min of Prices')).toEqual({isTrue: true, text: 'Prices'});
    expect(isAggregatedTitle('Total Max of Revenue')).toEqual({isTrue: true, text: 'Revenue'});
    expect(isAggregatedTitle('Total Count of Customers')).toEqual({isTrue: true, text: 'Customers'});
  });

  it('should return false for invalid aggregated titles', () => {
    expect(isAggregatedTitle('Total of Sales')).toEqual({isTrue: false, text: ''}); // missing aggregation function
    expect(isAggregatedTitle('Avg of Orders')).toEqual({isTrue: false, text: ''}); // missing "Total" prefix
    expect(isAggregatedTitle('Total Average of Orders')).toEqual({isTrue: false, text: ''}); // invalid aggregation function
    expect(isAggregatedTitle('Total Max of')).toEqual({isTrue: false, text: ""}); // missing target value
    expect(isAggregatedTitle('Total Sum of Total Sales')).toEqual({isTrue: true, text: 'Total Sales'}); // target value should not start with "Total"
    expect(isAggregatedTitle('Total Sum of Sales by Region')).toEqual({isTrue: true, text: 'Sales by Region'}); // should not include additional text
    expect(isAggregatedTitle(null)).toEqual({isTrue: false, text: ''}); // null input
    expect(isAggregatedTitle(undefined)).toEqual({isTrue: false, text: ''}); // undefined input
    expect(isAggregatedTitle('')).toEqual({isTrue: false, text: ''}); // empty string
  });

  it('should be case sensitive', () => {
    expect(isAggregatedTitle('Total sum of sales')).toEqual({isTrue: false, text: ''});
    expect(isAggregatedTitle('TOTAL Sum of Sales')).toEqual({isTrue: false, text: ''});
    expect(isAggregatedTitle('Total SUM of Sales')).toEqual({isTrue: false, text: ''});
  });
});

describe('test changeExapandSettings function', () => {
  it('should return an empty array if fields is undefined', () => {
    const result = changeExapandSettings(undefined, [{ name: 'John' }]);
    expect(result).toEqual([]);
  });

  it('should return an array of objects with name and items properties', () => {
    const fields = [{ name: 'name' }, { name: 'age' }];
    const data = [{ name: 'John', age: 25 }, { name: 'Jane', age: 30 }];
    const result = changeExapandSettings(fields, data);

    expect(result).toEqual([
      { name: 'name', items: ['John', 'Jane'] },
      { name: 'age', items: [25, 30] },
    ]);
  });

  it('should handle empty data array', () => {
    const fields = [{ name: 'name' }, { name: 'age' }];
    const data = [];
    const result = changeExapandSettings(fields, data);

    expect(result).toEqual([
      { name: 'name', items: [] },
      { name: 'age', items: [] },
    ]);
  });

  it('should handle missing name property in data objects', () => {
    const fields = [{ name: 'name' }, { name: 'age' }];
    const data = [{ age: 25 }, { name: 'Jane' }];
    const result = changeExapandSettings(fields, data);

    expect(result).toEqual([
      { name: 'name', items: ['Jane'] },
      { name: 'age', items: [25] },
    ]);
  });
});

  