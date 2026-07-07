import { replacePathsInJson } from "../../../../../../components/hi-dashboard-designer/utils/recursive-functions";

describe('replacePathsInJson', () => {
  test('handles empty dashboards', () => {
    const json = {};
    const pathList = [];
    expect(replacePathsInJson(json, pathList)).toEqual({});
  });

  test('handles a single report', () => {
    const json = {
      components: [
        {
          reportInfo: {
            file: {
              path: 'path/to/report1.hr',
            },
          },
        },
      ],
    };
    const pathList = [
      { order: 0, path: 'updated/path/to/report1.hr' },
    ];
    const expected = {
      components: [
        {
          reportInfo: {
            file: {
              path: 'updated/path/to/report1.hr',
            },
          },
        },
      ],
    };
    expect(replacePathsInJson(json, pathList)).toEqual(expected);
  });

  test('handles a report with filters', () => {
    const json = {
      components: [
        {
          reportInfo: {
            file: {
              path: 'path/to/report2.hr',
            },
          },
          filters: [
            {
              label: 'filter1',
              values: [],
            },
          ],
        },
      ],
    };
    const pathList = [
      { order: 0, path: 'updated/path/to/report2.hr' },
    ];
    const expected = {
      components: [
        {
          reportInfo: {
            file: {
              path: 'updated/path/to/report2.hr',
            },
          },
          filters: [
            {
              label: 'filter1',
              values: [],
            },
          ],
        },
      ],
    };
    expect(replacePathsInJson(json, pathList)).toEqual(expected);
  });

  test('handles static (common styles)', () => {
    const json = {
      css: '',
      script: '',
    };
    const pathList = [];
    expect(replacePathsInJson(json, pathList)).toEqual(json);
  });

  test('handles nested children (tab and grouped components)', () => {
    const json = {
      components: [
        {
          compType: 'tab',
          children: [
            {
              reportInfo: {
                file: {
                  path: 'path/to/report3.hr',
                },
              },
            },
          ],
        },
      ],
    };
    const pathList = [
      { order: 0, path: 'updated/path/to/report3.hr' },
    ];
    const expected = {
      components: [
        {
          compType: 'tab',
          children: [
            {
              reportInfo: {
                file: {
                  path: 'updated/path/to/report3.hr',
                },
              },
            },
          ],
        },
      ],
    };
    expect(replacePathsInJson(json, pathList)).toEqual(expected);
  });

  test('handles dropdown components', () => {
    const json = {
      components: [
        {
          compType: 'dropdown',
          reportInfo: {
            file: {
              path: 'path/to/report4.hr',
            },
          },
        },
      ],
    };
    const pathList = [
      { order: 0, path: 'updated/path/to/report4.hr' },
    ];
    const expected = {
      components: [
        {
          compType: 'dropdown',
          reportInfo: {
            file: {
              path: 'updated/path/to/report4.hr',
            },
          },
        },
      ],
    };
    expect(replacePathsInJson(json, pathList)).toEqual(expected);
  });

  test('handles multiple reports in proper order', () => {
    const json = {
      components: [
        {
          reportInfo: {
            file: {
              path: 'path/to/report5.hr',
            },
          },
        },
        {
          reportInfo: {
            file: {
              path: 'path/to/report6.hr',
            },
          },
        },
      ],
    };
    const pathList = [
      { order: 0, path: 'updated/path/to/report5.hr' },
      { order: 1, path: 'updated/path/to/report6.hr' },
    ];
    const expected = {
      components: [
        {
          reportInfo: {
            file: {
              path: 'updated/path/to/report5.hr',
            },
          },
        },
        {
          reportInfo: {
            file: {
              path: 'updated/path/to/report6.hr',
            },
          },
        },
      ],
    };
    expect(replacePathsInJson(json, pathList)).toEqual(expected);
  });

  test('handles components with and without resource IDs', () => {
    const json = {
      components: [
        {
          reportInfo: {
            resourceId: 123,
            file: {
              path: 'path/to/report7.hr',
            },
          },
        },
        {
          reportInfo: {
            file: {
              path: 'path/to/report8.hr',
            },
          },
        },
      ],
    };
    const pathList = [
      { order: 0, path: 'updated/path/to/report7.hr' },
      { order: 1, path: 'updated/path/to/report8.hr' },
    ];
    const expected = {
      components: [
        {
          reportInfo: {
            resourceId: 123,
            file: {
              path: 'updated/path/to/report7.hr',
            },
          },
        },
        {
          reportInfo: {
            file: {
              path: 'updated/path/to/report8.hr',
            },
          },
        },
      ],
    };
    expect(replacePathsInJson(json, pathList)).toEqual(expected);
  });

  test('handles reportInfo set to null', () => {
    const json = {
      components: [
        {
          reportInfo: null,
        },
      ],
    };
    const pathList = [];
    expect(replacePathsInJson(json, pathList)).toEqual(json);
  });

  test('handles components without path info', () => {
    const json = {
      components: [
        {
          reportInfo: {
            file: {},
          },
        },
      ],
    };
    const pathList = [];
    expect(replacePathsInJson(json, pathList)).toEqual(json);
  });

  test('handles dashboards without any reports', () => {
    const json = {
      components: []
    };
    const pathList = [];
    expect(replacePathsInJson(json, pathList)).toEqual(json);
  });

  test('handles dashboards with resource IDs', () => {
    const json = {
      components: [
        {
          reportInfo: {
            resourceId: 456,
            file: {
              path: 'path/to/report9.hr',
            },
          },
        },
      ],
    };
    const pathList = [
      { order: 0, path: 'updated/path/to/report9.hr' },
    ];
    const expected = {
      components: [
        {
          reportInfo: {
            resourceId: 456,
            file: {
              path: 'updated/path/to/report9.hr',
            },
          },
        },
      ],
    };
    expect(replacePathsInJson(json, pathList)).toEqual(expected);
  });

  test('handles deeply nested components with mixed types', () => {
    const json = {
      components: [
        {
          compType: 'group',
          children: [
            {
              compType: 'tab',
              children: [
                {
                  reportInfo: {
                    file: {
                      path: 'path/to/deeplyNestedReport.hr',
                    },
                  },
                },
              ],
            },
            {
              reportInfo: {
                file: {
                  path: 'path/to/anotherNestedReport.hr',
                },
              },
            },
          ],
        },
      ],
    };
    const pathList = [
      { order: 0, path: 'updated/path/to/deeplyNestedReport.hr' },
      { order: 1, path: 'updated/path/to/anotherNestedReport.hr' },
    ];
    const expected = {
      components: [
        {
          compType: 'group',
          children: [
            {
              compType: 'tab',
              children: [
                {
                  reportInfo: {
                    file: {
                      path: 'updated/path/to/deeplyNestedReport.hr',
                    },
                  },
                },
              ],
            },
            {
              reportInfo: {
                file: {
                  path: 'updated/path/to/anotherNestedReport.hr',
                },
              },
            },
          ],
        },
      ],
    };
    expect(replacePathsInJson(json, pathList)).toEqual(expected);
  });
});
