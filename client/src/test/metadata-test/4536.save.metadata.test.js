import { getMetadata, checkTablesAvailability } from "../../components/hi-metadata/utils";

describe("SAVE WITHOUT ANY TABLES ADDED TO METADATA", function() {
    test('checkig if tables are added to metadata - invalid arguments', function() {
        expect(checkTablesAvailability({})).toBeFalsy()
    })

    test('checkig if tables are added to metadata - empty tables', function () {
        expect(checkTablesAvailability({tables : {}})).toBeFalsy()
    })

    test('checkig if tables are added to metadata - empty tables', function () {
        expect(checkTablesAvailability({ tables: {dimdata : {name : 'dimdate', alias : 'dimDate'}} })).not.toBeFalsy()
    })
})