import { getTabsInfo } from "../../../../../../components/hi-dashboard-designer/utils/recursive-functions";


describe('getTabsInfo', () => {
  test('should update tabsInfo based on editValues', () => {
    const data = [
      {
        id: '123',
        gridItemConfig: [
          {
            key: 'edit',
            values: {
              tabNames: 'Tab1,Tab2',
              numberOfTabs: '2'
            }
          }
        ],
        tabsInfo: [
          { tabId: '123-0', item: ['item1'] },
          { tabId: '123-1', item: ['item2'] }
        ]
      }
    ];
    
    const expectedResult = [
      {
        gridItemConfig: [
          { key: "edit", values: { numberOfTabs: "2", tabNames: "Tab1,Tab2" } },
        ],
        id: "123",
        tabsInfo: [
          { item: ["item1"], layout: [], name: "Tab1", tabId: "123-0" },
          { item: ["item2"], layout: [], name: "Tab2", tabId: "123-1" },
        ],
      },
    ];

    const result = getTabsInfo(data, '123');
    expect(result).toEqual(expectedResult);
  });

  test('should handle case with different id', () => {
    const data = [
      {
        id: '123',
        gridItemConfig: [
          {
            key: 'edit',
            values: {
              tabNames: 'Tab1,Tab2',
              numberOfTabs: '2'
            }
          }
        ],
        tabsInfo: [
          { tabId: '123-0', item: ['item1'] },
          { tabId: '123-1', item: ['item2'] }
        ]
      }
    ];

    const expectedResult = [
      {
        gridItemConfig: [
          { key: "edit", values: { numberOfTabs: "2", tabNames: "Tab1,Tab2" } },
        ],
        id: "123",
        tabsInfo: [
          { item: ["item1"], tabId: "123-0" },
          { item: ["item2"], tabId: "123-1" },
        ],
      },
    ];

    const result = getTabsInfo(data, '456');
    expect(result).toEqual(expectedResult);
  });

  test('should handle case with more tabs than names provided', () => {
    const data = [
      {
        id: '123',
        gridItemConfig: [
          {
            key: 'edit',
            values: {
              tabNames: 'Tab1',
              numberOfTabs: '2'
            }
          }
        ],
        tabsInfo: [
          { tabId: '123-0', item: ['item1'] },
          { tabId: '123-1', item: ['item2'] }
        ]
      }
    ];

    const expectedResult = [
      {
        gridItemConfig: [
          { key: "edit", values: { numberOfTabs: "2", tabNames: "Tab1" } },
        ],
        id: "123",
        tabsInfo: [
          { item: ["item1"], layout: [], name: "Tab1", tabId: "123-0" },
          { item: ["item2"], layout: [], name: "Tab 2", tabId: "123-1" },
        ],
      },
    ];

    const result = getTabsInfo(data, '123');
    expect(result).toEqual(expectedResult);
  });

  test('should handle case with no previous tabsInfo', () => {
    const data = [
      {
        id: '123',
        gridItemConfig: [
          {
            key: 'edit',
            values: {
              tabNames: 'Tab1,Tab2',
              numberOfTabs: '2'
            }
          }
        ],
        tabsInfo: []
      }
    ];

    const expectedResult = [
      {
        gridItemConfig: [
          { key: "edit", values: { numberOfTabs: "2", tabNames: "Tab1,Tab2" } },
        ],
        id: "123",
        tabsInfo: [
          { item: [], layout: [], name: "Tab1", tabId: "123-0" },
          { item: [], layout: [], name: "Tab2", tabId: "123-1" },
        ],
      },
    ];

    const result = getTabsInfo(data, '123');
    expect(result).toEqual(expectedResult);
  });
});
