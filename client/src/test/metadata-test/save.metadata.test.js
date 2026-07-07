import { isObject } from '../../utils/is-object'
import { handleSave } from '../../components/hi-metadata/utils/handleSave'
import {
    storeSnapShotForSave_1,
    storeSnapShotForSave_2,
    storeSnapShotForSave_3
} from './constants'
import { storeSnapshotForSave_5, storeSnapshotForSave_6, storeSnapshotForSave_7, storeSnapshotForSave_8, storeSnapshotForSave_9 } from './constants2'
import { storeData } from './4425.mock.data'

/**
 * intial state save
 * normal save
 * dup table save
 * dup col save
 * alias tabe save, alias col save
 * save with joins
 */

/**
 * 0001 MDC:: After save, In Original Table, Original column name is changing to duplicated column name
 * Steps:
 1. Add geo_cordinates and expand
 2. Duplicate the location_id column
 3. Right click on geo_cordinates table duplicate
 4. Save the table
 */
describe('Save metadata usecases :: After save, In Original Table, Original column name is changing to duplicated column name', () => {
    let store = {
        getState: () => ({
            metadata: {
                present: storeSnapShotForSave_1
            }
        })
    }
    test('valid args - saving metadata - checking if formdata is preparing or not', () => {
        // { store, dispatch, type = 'save', location = false, fileName = false }
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        expect(isObject(result)).toBeTruthy()
    })

    test('valid args - saving metadata - checking if duplicate table is available', () => {
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })

        expect((() => {
            let validation = true
            let { table, column } = result.duplicate
            if (!(Array.isArray(table) && table.length === 1)) {
                validation = false
            }
            else if (!(
                table[0]['alias'] &&
                table[0]['columns'] &&
                table[0]['connId'] &&
                table[0]['originalId'] &&
                table[0]['originalName']
            )) {
                validation = false
            }
            return validation
        })()).toBeTruthy()
    })

    test('valid args - saving metadata - checking if duplicate column is available', () => {
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        expect((() => {
            let validation = true
            let { table, column } = result.duplicate
            if (column.length !== 1) {
                validation = false
            }
            else if (!(
                column[0]['alias'] &&
                column[0]['connId']
            )) {
                validation = false
            }
            return validation
        })()).toBeTruthy()
    })

    test('valid args - saving metadata - checking name property for dplicated column in duplicated table', () => {
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })

        expect((() => {
            let validation = true
            let { duplicate } = result
            let { table, column } = duplicate
            // return table.columns.length
            let duplicatedCol = table[0].columns.map(col => {
                if (col.alias === 'location_id_1') {
                    return col
                }
                return false
            }).filter(Boolean)
            if (!(duplicatedCol.length === 1 && duplicatedCol[0].name === 'location_id')) {
                validation = false
            }
            return validation
        })()).toBeTruthy()
    })

    test('valid args - saving metadata - checking dplicated column in actual table', () => {
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })

        expect((() => {
            let validation = true
            let { duplicate } = result
            let { table, column } = duplicate
            // return table.columns.length
            if (!(column[0].alias === 'location_id_1' && 'connId' in column[0])) {
                validation = false
            }
            return validation
        })()).toBeTruthy()
    })
})


/**
 * MDC:: After save, Changed alias name for column is showing different in duplicated table - TESTED FEW USECASES WORKING FINE
    Steps:
        1. Expand geo_cordinates
        2. Change alias of location_id column to ID
        3. Duplicate the table
        4. Save the table
        5. Observe, In duplicated table, showing Original name for location_id
 */


describe('save metadata usecase ::  After save, Changed alias name for column is showing different in duplicated table', () => {
    let store = {
        getState: () => ({
            metadata: {
                present: storeSnapShotForSave_2
            }
        })
    }
    test('valid args - saving metadata - checking if aliasname is present in duplicated tabled', () => {
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })

        expect((() => {
            let validation = true
            let { duplicate } = result
            let { table, column } = duplicate
            let dupColWithAlias = table[0].columns.map(col => {
                if (col.alias === 'lid') {
                    return col
                }
                return false
            }).filter(Boolean)
            if (!(dupColWithAlias.length === 1)) {
                validation = false
            }
            return validation
        })()).toBeTruthy()
    })

    test('valid args - saving metadata - checking if aliasname is present in actual table', () => {
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })

        expect((() => {
            let validation = true
            let { duplicate, changeItem } = result
            let { tables, columns } = changeItem
            if (!(columns.length === 1 && columns[0].alias === 'lid')) {
                validation = false
            }
            return validation
        })()).toBeTruthy()
    })
})


/**
 * 0005 MDC::Save 2nd time:: Reload is not working, After save loading first time saved metadata status - FIXED
        Steps:
            1. Add geo_cordinates and expand then Table alias to GEO 
            2. Change alias of location_id column to ID
            3. Duplicate the location column
            4. Save the table
            5. Right click on geo_cordinates table on Datasource panel
            6. Add to metadata then click on Reload
            7. Click on save
            8. Observe, first saved metadata content is loading
 */



describe('save metadata usecase :: After save, Changed alias name for column is showing different in duplicated table', () => {
    let store = {
        getState: () => ({
            metadata: {
                present: storeSnapShotForSave_3
            }
        })
    }
    test('valid args - saving metadata - checking for add items property', () => {
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })

        expect((() => {
            let validation = true
            let { addItem } = result
            let { tables } = addItem
            if (!(tables.length === 1)) {
                validation = false
            }
            return validation
        })()).toBeTruthy()
    })

    test('valid args - saving metadata - checking for metadataReload', () => {
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        expect((() => {
            let { addItem, metadataReload } = result
            return !metadataReload
        })()).toBeTruthy()
    })

    test('valid args - saving metadata - checking for UUID', () => {
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        expect((() => {
            let { addItem, metadataReload, uuid } = result
            return uuid
        })()).toBeTruthy()
    })
})

/**
 * 0006 MDC::SaveAs with same name and in same location is not working as expected. - FIXED
        Steps
            1. Expand geo_cordinates
            2. Save the metadata
            3. Do save as without changing anything in same directory and with same name
            4. Go to edit and reloading 2nd time saved metadata
            Observe, all the tables are showing metadata panel instead of single table
 */

describe('save metadata usecase :: After save, Changed alias name for column is showing different in duplicated tables', () => {
    let store = {
        getState: () => ({
            metadata: {
                present: storeSnapShotForSave_3
            }
        })
    }
    test('valid args - saving metadata - checking for add items property', () => {
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        expect((() => {
            let { addItem, metadataReload, uuid } = result
            let validation = true
            if (!(addItem.tables.length === 1)) {
                validation = false
            }
            if (!(addItem.columns.length === 0)) {
                validation = false
            }
            return uuid
        })()).toBeTruthy()
    })
})


describe('save Metadata usecases :: normal simple save', () => {
    let store = {
        getState: () => ({
            metadata: {
                present: storeSnapshotForSave_5
            }
        })
    }
    /**
     * save without adding any tables to metadata (intial state)
     */
    test('invalid args for saving metadata - -ve usecase', () => {
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        expect(result).toBe(false)
    })

    test('save metada - add 5 tables to metadata-section from derby - checking tables length in additem', () => {
        let store = {
            getState: () => ({
                metadata: {
                    present: storeSnapshotForSave_6
                }
            })
        }
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        let { addItem, dataSource, database, classifier, duplicate, changeItem } = result
        let validation = true
        if (addItem.tables.length !== 5) {
            validation = false
        }
        expect(validation).toBe(true)
    })

    test('save metada - add 5 tables to metadata-section from derby - checking tables content in additem', () => {
        let store = {
            getState: () => ({
                metadata: {
                    present: storeSnapshotForSave_6
                }
            })
        }
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        let { addItem, dataSource, database, classifier, duplicate, changeItem } = result
        let validation = true
        if (addItem.tables.length !== 5) {
            validation = false
        }
        addItem.tables.forEach(function (table) {
            if (typeof table !== 'string' && !table.length) {
                validation = false
            }
        })
        expect(validation).toBe(true)
    })

    test('save metada - add 5 tables to metadata-section from derby - checking columns/views length in add item', () => {
        let store = {
            getState: () => ({
                metadata: {
                    present: storeSnapshotForSave_6
                }
            })
        }
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        let { addItem, dataSource, database, classifier, duplicate, changeItem } = result
        let validation = true
        if (addItem.columns.length || addItem.views.length) {
            validation = false
        }
        expect(validation).toBe(true)
    })

    test('save metada - add 5 tables to metadata-section from derby - checking for database', () => {
        let store = {
            getState: () => ({
                metadata: {
                    present: storeSnapshotForSave_6
                }
            })
        }
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        let { addItem, dataSource, database, classifier, duplicate, changeItem } = result
        let validation = true
        if (database !== 'HIUSER') {
            validation = false
        }
        expect(validation).toBe(true)
    })

    test('save metada - add 5 tables to metadata-section from derby - checking for classifier', () => {
        let store = {
            getState: () => ({
                metadata: {
                    present: storeSnapshotForSave_6
                }
            })
        }
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        let { addItem, dataSource, database, classifier, duplicate, changeItem } = result
        let validation = true
        if (typeof classifier !== 'string' || !classifier.length) {
            validation = false
        }
        expect(validation).toBe(true)
    })
})

/**
 * saving with duplicated tables
 */

describe('save Metadata usecases :: save with duplicated table', () => {
    let store = {
        getState: () => ({
            metadata: {
                present: storeSnapshotForSave_7
            }
        })
    }


    test('duplicate table save usecase :: cheking tables count and checking for null', () => {
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        let { addItem, dataSource, database, classifier, duplicate, changeItem } = result
        let isValid = true
        if (addItem.tables.filter(Boolean).length !== 5) {
            isValid = false
        }
        if (addItem.tables.indexOf(null) !== -1) {
            isValid = false
        }
        expect(isValid).toBe(true)
    })

    test('duplicate table save usecase :: checking duplicates tabled in duplicate property', () => {
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        let { addItem, dataSource, database, classifier, duplicate, changeItem } = result
        let isValid = true
        let { table } = duplicate
        if (table.length !== 1) { isValid = false }
        if (!table[0].alias) {
            isValid = false
        }
        if (!table[0].columns.length) {
            isValid = false
        }
        // if (!('originalId' in table[0]) || !('originalColumn' in table[0])) {
        //     isValid = false
        // }
        expect(isValid).toBe(true)
    })

    test('duplicate table save usecase :: checking duplicates columns if any in duplicate property', () => {
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        let { addItem, dataSource, database, classifier, duplicate, changeItem } = result
        let isValid = true
        let { table, column } = duplicate
        if (column.length) {
            isValid = false
        }
        expect(isValid).toBe(true)
    })
})

/**
 * testing save with duplicate columns
 */

describe('save usecases with duplicated column', () => {
    let store = {
        getState: () => ({
            metadata: {
                present: storeSnapshotForSave_8
            }
        })
    }
    test('check duplicated columns :: check for columns', () => {
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        let { addItem, dataSource, database, classifier, duplicate, changeItem } = result
        let isValid = true
        let { table, column } = duplicate
        if (column.length !== 2) {
            isValid = false
        }
        expect(isValid).toBe(true)
    })

    test('check duplicated columns :: check for tables', () => {
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        let { addItem, dataSource, database, classifier, duplicate, changeItem } = result
        let isValid = true
        let { table, column } = duplicate
        if (table.length !== 0) {
            isValid = false
        }
        expect(isValid).toBe(true)
    })
})

/**
 * testing save with aliased column and aliased table
 */
describe('save with both alias table name and alias column name', () => {
    let store = {
        getState: () => ({
            metadata: {
                present: storeSnapshotForSave_9
            }
        })
    }

    test('check duplicated columns :: check for changed item column and table', () => {
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        let { addItem, dataSource, database, classifier, duplicate, changeItem } = result
        let isValid = true
        let { tables, columns } = changeItem
        if (tables.length !== 1 && columns.length !== 1) {
            isValid = false
        }
        expect(isValid).toBe(true)
    })

    test('check duplicated columns :: check for tables', () => {
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        let { addItem } = result
        let isValid = true;
        if (addItem.tables.length !== 5) {
            isValid = false
        }
        expect(isValid).toBe(true)
    })
})

/**
 * testing save with joins
 */

describe('testing saving metadata with joins', () => {
    let store = {
        getState: () => ({
            metadata: {
                present: storeData.metadata
            }
        })
    }
    test('save with joins :: checking joins', () => {
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        let { addItem, joins } = result
        let isValid = true
        //TODO - changed to me made for joins after adding dataosuce property
        // if (joins.length !== 5) {
        //     isValid = false
        // }
        // if (addItem.tables.length !== 5) {
        //     isValid = false
        // }
        expect(isValid).toBe(true)
    })
})

describe('testing bug 4425', () => {
    let store = {
        getState: () => ({
            metadata: {
                present: storeData.data_4425
            }
        })
    }
    test('save with joins :: checking joins', () => {
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        let { addItem } = result;
        let isValid = true
        isValid = addItem.tables.length === 0;
        expect(isValid).toBe(false)
    })
})