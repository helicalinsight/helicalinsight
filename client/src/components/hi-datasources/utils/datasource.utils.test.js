import { getJdbcUrlSubString } from "./datasource.utils";

describe("Datasource Utilities", () => {
    test("Without Other options",async () => { 
        expect(getJdbcUrlSubString("localhost", 0)).toEqual("localhost");
    });
    test("With Other options",async () => { 
        expect(getJdbcUrlSubString("170928392?ahajs", 0)).toEqual("170928392");
    });
}); 