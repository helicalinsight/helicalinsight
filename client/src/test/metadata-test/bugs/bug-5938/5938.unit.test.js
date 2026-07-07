import { handleJoinActions } from "../../../../components/hi-metadata/utils/handleJoinActions";
import { joinData } from "./5938.mock.data";

describe('metadata joins test', () => {
    const joins = handleJoinActions(joinData);
    test('User should be able to delete empty join as an invalid join', () => {
        expect(joins.length).toEqual(5);
    });
    test('Joins Index should be updated properly, after deleting invalid join', () => {
        expect(joins[4].index).toEqual(5);
        expect(joins[0].index).toEqual(1);
    });
});