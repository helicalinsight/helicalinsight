import { checkInvalidJoins } from "../../../../components/hi-metadata/utils/joins/utils";
import { tables, joins } from "./6098.mock.data";

describe('To Test: Invalid joins are identified', () => {
    test('Invalid joins are present', () => {
        const invalidJoins = checkInvalidJoins(joins, tables);
        const invalidIds = invalidJoins.map(join => join.id);
        
        expect(invalidJoins.length).toBe(3);
        expect(new Set(invalidIds).size === invalidJoins.length).toBe(true);
    })
})