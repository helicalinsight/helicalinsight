import { handleSave } from "../../../components/hi-metadata/utils"
import { mdStore, mdStore2, mdStore3, mdStoreWithViews, mdStore_dup_table_in_second_conn, mdStore_update_with_dup_tables, md_Edit } from "./save.mock.data"

describe('testing save formdata form metadata with multiple connections', () => {
    test('adding only one table but fetching catalogs and schemas for different tables', () => {
        let store = {
            getState() {
                return { metadata: {
                    present: mdStore
                } }
            }
        }
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        expect(result.connections).toBeFalsy()
    })

    test('edit a metadata with two connections and just save again without any modifications', () => {
        let store = {
            getState() {
                return { metadata: {
                    present: mdStore2
                } }
            }
        }
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        expect(result.connections[0].addItem.tables.length).toBe(5)
        expect(result.addItem.tables.length).toBe(6)
    })

    test('update metadata with multi connections check for databaseconnnection ID', () => {
        let store = {
            getState() {
                return { metadata: {
                    present: mdStore3
                } }
            }
        }
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        expect(result.connections[0].addItem.tables.length).toBe(1)
        // expect(result.connections[0].dataSource.connectionDatabaseId).toBe('4dce92d7-4cc1-4309-bed3-a9e51e9f91d8')
        expect(result.addItem.tables.length).toBe(2)
    })

    test('checking views in case of multi connection - expected : views are not applicable in case of multiconnection', () => {
        let store = {
            getState() {
                return { metadata: {
                    present: mdStoreWithViews
                } }
            }
        }
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        expect(result.addItem.views.length).toBe(0)
        expect(result.connections[0].addItem.views.length).toBe(0)
    })

    test('checking formdata for updating metadata without any modifications', () => {
        let store = {
            getState() {
                return { metadata: {
                    present: md_Edit
                } }
            }
        }
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        expect(result.addItem.tables.length).toBe(1)
        expect(result.connections[0].addItem.tables.length).toBe(1)
    })

    test('checking formdata with a duplicate table in second connection', () => {
        let store = {
            getState() {
                return { metadata: {
                    present: mdStore_dup_table_in_second_conn
                } }
            }
        }
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        expect(result.addItem.tables.length).toBe(1)
        expect(result.connections[0].addItem.tables.filter(Boolean).length).toBe(1)
        // expect(result.connections[0].addItem.tables[1]).toBe(null)       

    })

    test('checking md update formdata with a duplicate table', () => {
        let store = {
            getState() {
                return { metadata: {
                    present: mdStore_update_with_dup_tables
                } }
            }
        }
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        expect(result.addItem.tables.length).toBe(2)
        expect(result.duplicate.table.length).toBe(0)
        expect(result.connections[0].duplicate.table.length).toBe(0)     
    })

})