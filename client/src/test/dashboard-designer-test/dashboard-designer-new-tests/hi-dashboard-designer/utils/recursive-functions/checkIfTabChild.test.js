import { checkIfTabChild } from "../../../../../../components/hi-dashboard-designer/utils/recursive-functions";

describe('checkIfTabChild', () => {
  const gridItemsData = [
    {
      compType: 'tab',
      tabsInfo: [
        {
          item: ['item1', 'item2'],
          layout: [
            { i: 'item1', x: 0, y: 0, w: 10, h: 10 },
            { i: 'item2', x: 10, y: 0, w: 10, h: 10 }
          ]
        }
      ]
    },
    {
      compType: 'tab',
      tabsInfo: [
        {
          item: ['item3'],
          layout: [
            { i: 'item3', x: 0, y: 10, w: 10, h: 10 }
          ]
        }
      ]
    },
    {
      compType: 'notTab',
      tabsInfo: []
    }
  ];

  test('returns true and layout when gridItem is a child of a tab component', () => {
    const gridItem = { id: 'item1' };
    const result = checkIfTabChild(gridItem, gridItemsData);
    expect(result).toEqual([true, { i: 'item1', x: 0, y: 0, w: 10, h: 10 }]);
  });

  test('returns false and null when gridItem is not a child of a tab component', () => {
    const gridItem = { id: 'item4' };
    const result = checkIfTabChild(gridItem, gridItemsData);
    expect(result).toEqual([false, null]);
  });

  test('returns true and layout when gridItem is a child of a different tab component', () => {
    const gridItem = { id: 'item3' };
    const result = checkIfTabChild(gridItem, gridItemsData);
    expect(result).toEqual([true, { i: 'item3', x: 0, y: 10, w: 10, h: 10 }]);
  });

  test('returns false and null when gridItem is undefined', () => {
    const gridItem = undefined;
    const result = checkIfTabChild(gridItem, gridItemsData);
    expect(result).toEqual([false, null]);
  });

  test('returns false and null when gridItem has no id', () => {
    const gridItem = {};
    const result = checkIfTabChild(gridItem, gridItemsData);
    expect(result).toEqual([false, null]);
  });
});
