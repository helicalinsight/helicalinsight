import { handleJoinActions } from "../../../../components/hi-metadata/utils/handleJoinActions";
import { functionParams, result } from "./6301.mock.data";

describe('Metadata joins delete selected and then update one join', () => {
    const joins = handleJoinActions(functionParams);
    test('After updating one join other deleted joins should not be removed from joins data', () => {
        expect(joins).toEqual(result);
    });
});