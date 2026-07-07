import { mockdata } from './5733.mock.data';
const { updateTablesWithColumns } = require("../../components/hi-metadata/utils")

describe('testing utility update tables with columns', () => {
    test('checking if all the tables along with duplicate tables are processed without any issue', () => {
        let result = updateTablesWithColumns(mockdata);
        expect(Object.keys(result)).toStrictEqual(['dimdate', 'employee_details', 'employee_details_1'])
    })
})