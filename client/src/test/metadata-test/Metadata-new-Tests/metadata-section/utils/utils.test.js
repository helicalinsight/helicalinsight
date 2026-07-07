import { genrateUniqueName ,areJoinsFetched} from "../../../../../components/hi-metadata/utils/utils";


 describe("metadata utils function", () => {

    test('removing a file in a table', () => {
        const  allItems = [
            "CACHE_ID",
            "CACHE_FILE_EXPIRY",
            "CACHE_FILE_PATH",
            "CACHE_FILE_SIZE",
            "CACHE_FILE_TIME_STAMP",
            "CONNECTION_FILE_PATH",
            "CONNECTION_ID",
            "CONNECTION_TYPE",
            "MAP_ID",
            "NO_OF_RECORDS",
            "REPORT_PARAMETERS",
            "PRIORITY",
            "QUERY",
            "STATUS"
        ];
        const item = "CACHE_FILE_EXPIRY";
    
        let result = genrateUniqueName({ allItems, item });
        let expectedResult = "CACHE_FILE_EXPIRY_1";
        expect(result).toEqual(expectedResult);
    
      
      });


      test('performing an action in a table', () => {
        const  joins = [];
        const currentDbId = "cs6cn";
    
        let result = areJoinsFetched(joins, currentDbId);
        let expectedResult = false;
        expect(result).toEqual(expectedResult);
    
      
      });

});