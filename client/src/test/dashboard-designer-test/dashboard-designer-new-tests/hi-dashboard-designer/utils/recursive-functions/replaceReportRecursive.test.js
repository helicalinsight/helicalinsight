import { replaceReportRecursive} from '../../../../../../components/hi-dashboard-designer/utils/recursive-functions'; 

describe('replaceReportRecursive', () => {
    it('should replace report ID and update nested structures correctly', () => {
        const reportInfo = { name: 'New Report' };
        const replaceReportId = 'old-id';
        const newId = 'new-id';

        const gridItemsData = [
            {
                id: 'old-id',
                compType: 'dashboard-designer-component',
                gridItemConfig: [
                    { key: 'css', values: { value: 'some-css-code-old-id' } },
                    { key: 'javascript', values: { value: 'some-js-code-old-id' } },
                    { key: 'other', values: { value: 'unrelated' } }
                ],
            },
            {
                id: 'container-1',
                compType: 'container',
                layout: [{ i: 'old-id', x: 0, y: 0 }],
                children: [
                    {
                        id: 'nested-1',
                        compType: 'tab',
                        tabsInfo: [
                            {
                                item: ['old-id', 'nested-2'],
                                layout: [{ i: 'old-id', x: 0, y: 0 }],
                            },
                        ],
                        children: [],
                    },
                ],
            },
        ];

        const expected = [
            {
                id: 'new-id',
                compType: 'dashboard-designer-component',
                gridItemConfig: [
                    { key: 'css', values: { value: 'some-css-code-new-id' } },
                    { key: 'javascript', values: { value: 'some-js-code-new-id' } },
                    { key: 'other', values: { value: 'unrelated' } }
                ],
                reportInfo: { name: 'New Report' },
            },
            {
                id: 'container-1',
                compType: 'container',
                layout: [{ i: 'new-id', x: 0, y: 0 }],
                children: [
                    {
                        id: 'nested-1',
                        compType: 'tab',
                        tabsInfo: [
                            {
                                item: ['new-id', 'nested-2'],
                                layout: [{ i: 'new-id', x: 0, y: 0 }],
                            },
                        ],
                        children: [],
                        layout: undefined, // Explicitly matching the undefined layout
                    },
                ],
            },
        ];

        const result = replaceReportRecursive({ reportInfo, gridItemsData, replaceReportId, newId });

        expect(result).toEqual(expected);
    });

    test('should handle empty children gracefully', () => {
        const reportInfo = { name: 'Another Report' };
        const replaceReportId = 'id-to-replace';
        const newId = 'new-id';

        const gridItemsData = [
            {
                id: 'container',
                compType: 'container',
                layout: [],
                children: [],
            },
        ];

        const result = replaceReportRecursive({ reportInfo, gridItemsData, replaceReportId, newId });

        expect(result).toEqual(gridItemsData);
    });

    test('should return unchanged data when no matching ID is found', () => {
        const reportInfo = { name: 'Unchanged Report' };
        const replaceReportId = 'non-existent-id';
        const newId = 'new-id';

        const gridItemsData = [
            {
                id: 'existing-id',
                compType: 'container',
                layout: [],
                children: [],
            },
        ];

        const result = replaceReportRecursive({ reportInfo, gridItemsData, replaceReportId, newId });

        expect(result).toEqual(gridItemsData);
    });
});
