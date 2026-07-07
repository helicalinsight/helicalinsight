import { getMatchedDriverTypes } from "../../../../components/hi-metadata/utils";

describe('Checking getMatchedDriverTypes function', () => {
    const allDataSourceTypes = [{
        driver: "org.apache.derby.jdbc.ClientDriver",
        name: "Derby"
    }]
    const tempDataSource = {
        driver: {
            driver: "org.apache.derby.jdbc.ClientDriver"
        }
    }
    const result = "Derby";

    test("To Test: Form-data contains changed column", () => {  
        expect(getMatchedDriverTypes({allDataSourceTypes, tempDataSource })).toBe(result);
    });
});