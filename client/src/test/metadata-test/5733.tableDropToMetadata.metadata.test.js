/**
 * 5733  ===   Retested this issue on 5.0.0.GIT571 SNAPSHOT build and observed Extra duplicate tables are adding under metadata panel after loading the metadata in edit mode.
 */
import { handleTableDropToMetadata } from "../../components/hi-metadata/utils"
import { mockDataForDuplicateTable } from "./5733.mock.data"

describe('Testing table drop to metadata with duplicate table', () => {
    test('check if tables in metadata section has only one duplicate table', () =>{
        let result = handleTableDropToMetadata(mockDataForDuplicateTable)
        console.log(Object.keys(result))
        expect(Object.keys(result).length).toBe(6)
        expect(Object.keys(result).filter(v => v === 'meeting_details_1').length).toBe(1)
    })
})