import { entityNameHandler, mapTablesColumnsWithUniqueKey, tablesToTableDataConverter } from "../../../components/hi-metadata/components/editor/security/securityHelperMethods";
import { transformSecurityTable } from "../../../utils/reportQuery/utils/handleGetUniqueKey";
import { bug_6327, entityNameHandlerConstants, mapTablesColumnsWithUniqueKeyConstants, tablesToTableDataConverterConstants } from "./securityTestConstants";

describe("Testing securityHelperMethods file:", () => {
    describe("Testing entityNameHandler func:", () => {
        const {table, column} = entityNameHandlerConstants;
        test('Return Value must match with given output for Table selection', (done) => {
            const {args, returnVal} = table;
            expect(
                entityNameHandler({ entityValues: args })
            ).toEqual(returnVal);
            done();
        });
        test('Return Value must match with given output for Column selection', (done) => {
            const {args, returnVal} = column;
            expect(
                entityNameHandler({ entityValues: args })
            ).toEqual(returnVal);
            done();
        });
    });

    describe("Testing mapTablesColumnsWithUniqueKey func:", () => {
        test('Return Value must match with given output for input table', (done) => {
            const {args, returnVal} = mapTablesColumnsWithUniqueKeyConstants;
            let mappedVals =  mapTablesColumnsWithUniqueKey({tables: args});
            expect(
                Array.from(mappedVals, (entry) => {
                    return {key: entry[0], value: entry[1]}
                })
            ).toEqual(returnVal);
            done();
        });
    });

    describe("Testing tablesToTableDataConverter func:", () => {
        const {createExpWithTable, createExpWithColumn, editExpWithTableType} = tablesToTableDataConverterConstants;
        test('Test create expression with table', (done) => {
            const {args, result} = createExpWithTable;
            let output =  tablesToTableDataConverter({...args});
            expect(output).toEqual(result);
            done();
        });
        test('Test create expression with column', (done) => {
            const {args, result} = createExpWithColumn;
            let output =  tablesToTableDataConverter({...args});
            expect(output).toEqual(result);
            done();
        });
        test('Test edit expression with table type', (done) => {
            const {args, result} = editExpWithTableType;
            let output =  tablesToTableDataConverter({...args});
            expect(output).toEqual(result);
            done();
        });
    });

    describe("Testing transformSecurityTable func 6327:", () => {
        test('Test create expression with table', (done) => {
            const {args, result} = bug_6327;
            let output =  transformSecurityTable({...args, testPurpose: true});
            expect(output).toEqual(result);
            done();
        });
    });

  });