import { handleJoinActions } from "../../../../components/hi-metadata/utils";
import { functionData } from "./6312.mock.data";


describe('Add new Join', () => {
    test('To Test: newly added join should contain proper data (id, key, type, operator, left, right, action etc)', () => {
        const joins = handleJoinActions(functionData);
        expect(joins.length).toEqual(1);
        expect(joins[0]).toHaveProperty('id', 'index', "key", "Left", "operator", "right", "type", "uuid", "action")
    })
});