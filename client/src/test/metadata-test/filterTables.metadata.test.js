import { filterTables } from '../../components/hi-metadata/utils/filterTables'
import { tablesData, tablesWithoutColumns } from './filterTables.mock.data'

describe('testing filter search tables in metadata-section - checking mockdata with tables and columns', () => {
    test('checking with empty sting', () => {
        let result = filterTables({ tables: tablesData, searchText: '' })
        expect(Object.values(result).length).toBe(5)
    })

    test('not passing searchText', () => {
        let result = filterTables({ tables: tablesData })
        expect(Object.values(result).length).toBe(5)
    })

    test('passing searchText as geo -- table nme', () => {
        let result = filterTables({ tables: tablesData, searchText: 'geo' })

        expect(Object.values(result).length).toBe(1)
    })

    test('checking table name passing "g"', () => {
        let result = filterTables({ tables: tablesData, searchText: 'g' })
        expect(Object.values(result).length).toBe(5)
    })

    test('checking table name passing "g" checkig inner tbales', () => {
        let result = filterTables({ tables: tablesData, searchText: 'g' })
        expect(result.dimdate.columns.rating).toBeTruthy()
    })

    test('checking table name passing "g" checkig inner tbales', () => {
        let result = filterTables({ tables: tablesData, searchText: 'g' })
        expect(result.employee_details.columns.age).toBeTruthy()
    })

    test('checking table name passing "g" checkig inner tbales', () => {
        let result = filterTables({ tables: tablesData, searchText: 'g' })
        expect(result.travel_details.columns.booking_platform).toBeTruthy()
    })

    test('checking columnName name travel_date', () => {
        let result = filterTables({ tables: tablesData, searchText: 'travel_date' })
        expect(Object.keys(result.travel_details.columns).length).toBe(1)
    })

    test('checking column travel_id', () => {
        let result = filterTables({ tables: tablesData, searchText: 'travel_id' })
        expect(result.travel_details.columns.travel_id).toBeTruthy()
    })

    test('checking omplete table name', () => {
        let result = filterTables({ tables: tablesData, searchText: 'travel_details' })
        expect(Object.values(result.travel_details.columns).length).toBe(12)
    })

    test('checking tablename.columnNme checking col length', () => {
        let result = filterTables({ tables: tablesData, searchText: 'travel_details.travel_id' })
        expect(Object.values(result.travel_details.columns).length).toBe(1)
    })

    test('checking tablename.columnNme checking col name', () => {
        let result = filterTables({ tables: tablesData, searchText: 'travel_details.travel_id' })
        expect(Object.values(result.travel_details.columns.travel_id)).toBeTruthy()
    })
})


describe('testing filter search tables in metadata-section - checking mockdata with tables and columns', () => {
    test('checking with empty sting', () => {
        let result = filterTables({ tables: tablesWithoutColumns, searchText: '' })
        expect(Object.values(result).length).toBe(5)
    })

    test('not passing searchText', () => {
        let result = filterTables({ tables: tablesWithoutColumns })
        expect(Object.values(result).length).toBe(5)
    })

    test('passing searchText as geo -- table nme', () => {
        let result = filterTables({ tables: tablesWithoutColumns, searchText: 'geo' })

        expect(Object.values(result).length).toBe(1)
    })

    test('checking table name passing "g"', () => {
        let result = filterTables({ tables: tablesWithoutColumns, searchText: 'g' })
        expect(Object.values(result).length).toBe(2)
    })

    test('checking omplete table name', () => {
        let result = filterTables({ tables: tablesData, searchText: 'travel_details' })
        expect(Object.values(result).length).toBe(1)
    })

    test('checking tablename.columnNme checking col length', () => {
        let result = filterTables({ tables: tablesData, searchText: 'travel_details.travel_id' })
        expect(Object.values(result).length).toBe(1)
    })

})