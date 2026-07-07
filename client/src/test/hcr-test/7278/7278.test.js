import { handleDeletingGroup } from "../../../components/hi-canned-reports/hcrHelperMethods";

const reqPain = {
    canvasProperties: {
        groupProperties: {
            options: [
                {
                    id: 1, name: 'group_dim_id', nodeId: 'node-123'
                },
                {
                    id: 2, name: 'group_fiscal', nodeId: 'node-456'
                }
            ],
            selectGroup: 1
        }
    },
    hcrDiagramNodesData: [
        { repeat: 'gp1', groupBy: 'node-123' },
        { id: 'node-123', isGrp: true, groupId: 1 }
    ]
}

const output = {
    canvasProperties: {
        groupProperties: {
            keyValuePairs: {},
            options: [
                {
                    id: 2, name: 'group_fiscal', nodeId: 'node-456'
                }
            ],
            selectGroup: ''
        }
    },
    hcrDiagramNodesData: [
        { repeat: 'na', groupBy: '' },
        { id: 'node-123', isGrp: '', groupId: '', groupName: '' }
    ]
}

describe('Testing HCR deletingGroup function', () => {
    test('matching in and output', (done) => {
        expect(handleDeletingGroup(reqPain)).toEqual(output);
        done();
    })
});
