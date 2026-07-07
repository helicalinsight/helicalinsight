import { handleTableAction } from "../../../../components/hi-metadata/utils"
import {store6082, tableRecord, columnRecord} from "./6082.mock.data"

describe('Metadata update alias for column or table action', () => {
    test('To Test: updating alias of table to already existing name', () => {
        const result = handleTableAction({
            action: "updateAlias",
            dispatch: false,
            store: store6082,
            record: tableRecord,
            value: "dimdate_1",
            returnResultForJest: true
        })
        expect(result.length).toBe(1);
        expect(result[0].alias).toBe("dimdate_2");   
    });

    test('To Test: updating alias of column to already existing name', () => {
        const result = handleTableAction({
            action: "updateAlias",
            dispatch: false,
            store: store6082,
            record: columnRecord,
            value: "age_2",
            returnResultForJest: true
        });
        expect(result.length).toBe(3);
        expect(result[2].alias).toBe("age_3");   
    });
})