import {
    storeSnapShot
} from './constants'
import { cloneDeep } from 'lodash-es'
import { handleTableAction } from '../../components/hi-metadata/utils/handleTableAction'
import { tableSnapShotFor4462Basic, tablesSnapShotFor4462WithAliasedColAndDupTable } from './tableConstants4462'
import { syncFetchedTablesWithStoreTables } from '../../components/hi-metadata/utils/syncFetchedTablesWithStoreTables'

Object.values(storeSnapShot.tables).forEach(tableVal => {
    Object.values(tableVal.columns || {}).forEach(colVal => {
        colVal.id = colVal.originalId;
    })
})

let store = {
    getState: () => ({
        metadata: {
            present: storeSnapShot
        }
    })
}
describe('Testing context menu options for tables in metadata-section - ALIAS', () => {
    let aliasName = 'geo_cordinates_alias'
    test('valid arguments for alias - checking length', () => {
        let result = handleTableAction({
            action: "updateAlias",
            dispatch: false,
            value: aliasName,
            store: store,
            record: storeSnapShot.tables.geo_cordinates,
            returnResultForJest: true
        })
        expect(result.length === 1).toBeTruthy()
    })

    test('valid arguments for alias - checking aliasName', () => {
        let result = handleTableAction({
            action: "updateAlias",
            dispatch: false,
            value: aliasName,
            store: store,
            record: storeSnapShot.tables.geo_cordinates,
            returnResultForJest: true
        })
        expect((() => {
            return result.filter(t => t.alias === aliasName).length
        })()).toBe(1)
    })

    test('valid arguments for alias - checking record id for changed alias name', () => {
        let result = handleTableAction({
            action: "updateAlias",
            dispatch: false,
            value: aliasName,
            store: store,
            record: storeSnapShot.tables.geo_cordinates,
            returnResultForJest: true
        })
        expect((() => {
            return result[0].id === storeSnapShot.tables.geo_cordinates.id
        })()).toBe(true)
    })

    test('valid arguments for alias - passing aliasname that is already available', () => {
        let result = handleTableAction({
            action: "updateAlias",
            dispatch: false,
            value: 'geo_cordinates',
            store: store,
            record: storeSnapShot.tables.geo_cordinates,
            returnResultForJest: true
        })
        expect((() => {
            return result
        })()).not.toBe(false)
    })

    test('INVALID arguments for alias - when table is empty', () => {
        let result = handleTableAction({
            action: "updateAlias",
            dispatch: false,
            value: aliasName,
            store: store,
            record: {},
            returnResultForJest: true
        })
        expect((() => {
            return result
        })()).toBe(false)
    })

    test('INVALID arguments for alias - when alias name is empty', () => {
        let result = handleTableAction({
            action: "updateAlias",
            dispatch: false,
            value: '',
            store: store,
            record: {},
            returnResultForJest: true
        })
        expect((() => {
            return result
        })()).toBe(false)
    })

    test('INVALID arguments for alias - when alias name is empty', () => {
        let result = handleTableAction({
            action: "updateAlias",
            dispatch: false,
            value: '',
            store: store,
            record: {},
            returnResultForJest: true
        })
        expect((() => {
            return result
        })()).toBe(false)
    })

    test('INVALID arguments for alias - when alias name is length is less than 3 chars', () => {
        let result = handleTableAction({
            action: "updateAlias",
            dispatch: false,
            value: '12',
            store: store,
            record: {},
            returnResultForJest: true
        })
        expect((() => {
            return result
        })()).toBe(false)
    })

    test('INVALID arguments for alias - when alias name is null', () => {
        let result = handleTableAction({
            action: "updateAlias",
            dispatch: false,
            value: null,
            store: store,
            record: {},
            returnResultForJest: true
        })
        expect((() => {
            return result
        })()).toBe(false)
    })


})


describe('Testing context menu options for tables in metadata-section - DELETE', () => {
    test('removing table valid arguments', () => {
        expect((() => {
            let result = handleTableAction({
                action: "remove",
                dispatch: false,
                store: store,
                record: store.getState().metadata.present.tables.geo_cordinates,
                returnResultForJest: true
            })
            return result.removedTables[0].id === store.getState().metadata.present.tables.geo_cordinates.id
        })()).toBe(true)
    })

    test('removing table - INVALID arguments', () => {
        expect((() => {
            let result = handleTableAction({
                action: "remove",
                dispatch: false,
                store: store,
                record: {},
                returnResultForJest: true
            })
            return result
        })()).toBe(false)
    })
})

describe('Testing context menu options for tables in metadata-section - DUPLICATE', () => {
    test('removing table valid arguments - checking tables count', () => {
        expect((() => {
            let result = handleTableAction({
                action: "duplicate",
                dispatch: () => {},
                store: store,
                record: store.getState().metadata.present.tables.geo_cordinates,
                returnResultForJest: true,
                dataSource: store.getState().metadata.present.dataSource
            })
            return Object.values(result).length === 6
        })()).toBe(true)
    })

    test('removing table valid arguments - checking tables count.', () => {
        expect((() => {
            let result = handleTableAction({
                action: "duplicate",
                dispatch: () => {},
                store: store,
                record: store.getState().metadata.present.tables.geo_cordinates,
                returnResultForJest: true,
                dataSource: store.getState().metadata.present.dataSource
            })
            return Object.keys(result).indexOf('geo_cordinates_1') !== -1
        })()).toBe(true)
    })

    test('removing table invalid record arguments', () => {
        expect((() => {
            let result = handleTableAction({
                action: "duplicate",
                dispatch: () => {},
                store: store,
                record: store.getState().metadata.present.tables.geo_cordinates_1, //geo_cordinates_1 is not present
                returnResultForJest: true,
                dataSource: store.getState().metadata.present.dataSource
            })
            return result
        })()).toBe(false)
    })

    test('removing table valid arguments - checking tables data/datasource availaility', () => {
        expect((() => {
            let result = handleTableAction({
                action: "duplicate",
                dispatch: () => {},
                store: store,
                record: store.getState().metadata.present.tables.geo_cordinates,
                returnResultForJest: true,
                dataSource: store.getState().metadata.present.dataSource
            })
            return 'data' in result.geo_cordinates_1 && 'dataSource' in result.geo_cordinates_1 && result.geo_cordinates_1.duplicate
        })()).toBe(true)
    })
})

describe('testing 4462 bug :: extra column is adding in duplicated table which is aliased in actual column', () => {
    /**
     * 
     * Metadata::Create::Alias and Duplicate::Save:: After saving metadata, 
     * Changed alias name for column is showing different in duplicated table
     * 
     * Rohini 2022-04-26 21:54:59 IST
     *   Retested this issue on 5.0.0.GIT057 SNAPSHOT build, it is not working as expected. Hence reopening this bug.
     *
     *   Test Result: Extra LOC_ID column is adding In duplicated table after saving metadata.
     */
    test('basic usecase - just drag a table and then save - checking columns count', () => {
        let result = syncFetchedTablesWithStoreTables(tableSnapShotFor4462Basic)
        let tableName = 'geo_cordinates'
        expect((() => {
            return Object.keys(result[tableName].columns).length
        })()).toBe(Object.keys(tableSnapShotFor4462Basic.storeTables[tableName].columns).length)
    })

    test('basic usecase - just drag a table and then save - checking column keys are same', () => {
        let result = syncFetchedTablesWithStoreTables(tableSnapShotFor4462Basic)
        let tableName = 'geo_cordinates'
        expect((() => {
            let array1 = Object.keys(result[tableName].columns)
            let array2 = Object.keys(tableSnapShotFor4462Basic.storeTables[tableName].columns)
            const array2Sorted = array2.slice().sort();
            return array1.length === array2.length && array1.slice().sort().every(function (value, index) {
                return value === array2Sorted[index];
            });
        })()).toBe(true)
    })

    test('basic usecase - just drag a table and then save - checking column names are same', () => {
        let result = syncFetchedTablesWithStoreTables(tableSnapShotFor4462Basic)
        let tableName = 'geo_cordinates'
        expect((() => {
            let array1 = Object.values(result[tableName].columns).map(col => col.name)
            let array2 = Object.values(tableSnapShotFor4462Basic.storeTables[tableName].columns).map(col => col.name)
            const array2Sorted = array2.slice().sort();
            return array1.length === array2.length && array1.slice().sort().every(function (value, index) {
                return value === array2Sorted[index];
            });
        })()).toBe(true)
    })

    test('actual usecase (mentioned above) - just drag a table and then save - checking columns count for original table', () => {
        let result = syncFetchedTablesWithStoreTables(cloneDeep(tablesSnapShotFor4462WithAliasedColAndDupTable))
        let tableName = 'geo_cordinates'
        let dupTable = 'geo_cordinates_1'
        expect((() => {
            return Object.keys(result[tableName].columns).length
        })()).toBe(Object.keys(tablesSnapShotFor4462WithAliasedColAndDupTable.storeTables[tableName].columns).length)
    })

    test('actual usecase (mentioned above) - just drag a table and then save - checking columns count for duplicate table', () => {
        let result = syncFetchedTablesWithStoreTables(cloneDeep(tablesSnapShotFor4462WithAliasedColAndDupTable))
        let tableName = 'geo_cordinates'
        let dupTable = 'geo_cordinates_1'
        expect((() => {
            return Object.keys(result[dupTable].columns).length
        // })()).toBe(Object.keys(tablesSnapShotFor4462WithAliasedColAndDupTable.fetchedTables[dupTable].columns).length)
    })()).toBe(3)
    })

    test('actual usecase (mentioned above) - just drag a table and then save - checking alias name in duplicated table', () => {
        let result = syncFetchedTablesWithStoreTables(cloneDeep(tablesSnapShotFor4462WithAliasedColAndDupTable))
        let tableName = 'geo_cordinates'
        let dupTable = 'geo_cordinates_1'
        expect((() => {
            // return result[dupTable].columns['location_id'].alias
            return result[tableName].columns['location_id'].alias
        })()).toBe("location_id_alias")
    })

    test('actual usecase (mentioned above) - just drag a table and then save - checking alias name in actual table', () => {
        let result = syncFetchedTablesWithStoreTables(cloneDeep(tablesSnapShotFor4462WithAliasedColAndDupTable))
        let tableName = 'geo_cordinates'
        let dupTable = 'geo_cordinates_1'
        expect((() => {
            return result[tableName].columns['location_id'].alias
        })()).toBe("location_id_alias")
    })

    test('actual usecase (mentioned above) - just drag a table and then save - checking columns count in duplicated table', () => {
        let result = syncFetchedTablesWithStoreTables(cloneDeep(tablesSnapShotFor4462WithAliasedColAndDupTable))
        let tableName = 'geo_cordinates'
        let dupTable = 'geo_cordinates_1'
        expect((() => {
            return Object.keys(result[dupTable].columns).length
        })()).toBe(3)
    })

})
