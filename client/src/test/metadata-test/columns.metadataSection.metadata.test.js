import {
    storeSnapShot
} from './constants'
import { handleTableAction } from '../../components/hi-metadata/utils/handleTableAction'

let store = {
    getState: () => ({
        metadata: {
            present: storeSnapShot
        }
    })
}
describe('Testing context menu options for columns in metadata-section - ALIAS', () => {
    let aliasName = 'dim_id_alias'
    test('valid arguments for alias - checking length', () => {
        let result = handleTableAction({
            action: "updateAlias",
            dispatch: false,
            value: aliasName,
            store: store,
            record: storeSnapShot.tables.dimdate.columns.dim_id,
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
            record: storeSnapShot.tables.dimdate.columns.dim_id,
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
            record: storeSnapShot.tables.dimdate.columns.dim_id,
            returnResultForJest: true
        })
        expect((() => {
            return result[0].id === storeSnapShot.tables.dimdate.columns.dim_id.id
        })()).toBe(true)
    })

    test('valid arguments for alias - passing aliasname that is already available', () => {
        let result = handleTableAction({
            action: "updateAlias",
            dispatch: false,
            value: 'dim_id',
            store: store,
            record: storeSnapShot.tables.dimdate.columns.dim_id,
            returnResultForJest: true
        })
        expect((() => {
            return result
        })()).not.toBe(false)
    })

    test('INVALID arguments for alias - when col is empty', () => {
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
            record: storeSnapShot.tables.dimdate.columns.dim_id,
            returnResultForJest: true
        })
        expect((() => {
            return result
        })()).toBe(false)
    })

    test('INVALID arguments for alias -- when alias name is empty', () => {
        let result = handleTableAction({
            action: "updateAlias",
            dispatch: false,
            value: '',
            store: store,
            record: storeSnapShot.tables.dimdate.columns.dim_id,
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
            record: storeSnapShot.tables.dimdate.columns.dim_id,
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
            record: storeSnapShot.tables.dimdate.columns.dim_id,
            returnResultForJest: true
        })
        expect((() => {
            return result
        })()).toBe(false)
    })


})


describe('Testing context menu options for columns in metadata-section - DELETE', () => {
    test('removing column valid arguments', () => {
        expect((() => {
            let result = handleTableAction({
                action: "remove",
                dispatch: false,
                store: store,
                record: store.getState().metadata.present.tables.dimdate.columns.dim_id,
                returnResultForJest: true
            })
            return result[0].id === store.getState().metadata.present.tables.dimdate.columns.dim_id.originalId
        })()).toBe(true)
    })

    test('removing column - INVALID arguments', () => {
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

describe('Testing context menu options for columns in metadata-section - DUPLICATE', () => {
    test('valid column arguments - checking columns count in dim_date', () => {
        expect((() => {
            let result = handleTableAction({
                action: "duplicate",
                dispatch: false,
                store: store,
                record: store.getState().metadata.present.tables.dimdate.columns.dim_id,
                returnResultForJest: true,
                dataSource: store.getState().metadata.present.dataSource
            })
            return Object.values(result.tables.dimdate.columns).length === 11
        })()).toBe(true)
    })

    test('duplicating column valid arguments - checking columns in tables', () => {
        expect((() => {
            let result = handleTableAction({
                action: "duplicate",
                dispatch: false,
                store: store,
                record: store.getState().metadata.present.tables.dimdate.columns.dim_id,
                returnResultForJest: true,
                dataSource: store.getState().metadata.present.dataSource
            })
            return 'dim_id_1' in result.tables.dimdate.columns && !result.tables.dimdate.columns.dim_id_1.key && !result.tables.dimdate.columns.dim_id_1.uuid 
        })()).toBe(true)
    })

    test('duplicating column valid arguments - checking columns in changed columns', () => {
        expect((() => {
            let result = handleTableAction({
                action: "duplicate",
                dispatch: false,
                store: store,
                record: store.getState().metadata.present.tables.dimdate.columns.dim_id,
                returnResultForJest: true,
                dataSource: store.getState().metadata.present.dataSource
            })
            return 'dim_id_1' === result.changedColumns[0].alias
        })()).toBe(true)
    })

    test('removing table invalid record arguments.', () => {
        expect((() => {
            let result = handleTableAction({
                action: "duplicate",
                dispatch: false,
                store: store,
                record: store.getState().metadata.present.tables.dimdate.columns.dim_id_1, //geo_cordinates_1 is not present
                returnResultForJest: true,
                dataSource: store.getState().metadata.present.dataSource
            })
            return result
        })()).toBe(false)
    })

    test('removing table valid arguments - checking tables data/datasource availaility', () => {
        let result = handleTableAction({
            action: "duplicate",
            dispatch: false,
            store: store,
            record: store.getState().metadata.present.tables.dimdate.columns.dim_id,
            returnResultForJest: true,
            dataSource: store.getState().metadata.present.dataSource,
            tables: store.getState().metadata.present.tables
        })
        expect(result.tables.dimdate.columns['dim_id_1'].alias).toBe('dim_id_1');
        expect(result.tables.dimdate.columns['dim_id_1'].duplicate).toBeTruthy()
    })
})