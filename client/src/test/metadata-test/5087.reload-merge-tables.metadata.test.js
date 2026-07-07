import { handleDSTableCheck } from '../../components/hi-metadata/utils/handleDSTableCheck'
import { handleDSTableOptionsClick } from '../../components/hi-metadata/utils/handleDSTableOptionsClick'
import { store, tableRecord } from './5087.mock.data'

describe('add tables to metadata with VIEW usecases - MERGE/RELOAD', () => {
    let dsListToRender = store.getState().metadata.present.datasourceListToRender;
    test('add tables to metadata and MERGE - when there is already a view addded to metadata', () => {
        let record = tableRecord
        let type = 'merge'
        dsListToRender = handleDSTableCheck({
            checked: true,
            record,
            dispatch: () => { },
            dsListToRender,
            returnData: true
        })
        let result = handleDSTableOptionsClick({
            record, option: 'addToMetadata', dsListToRender: dsListToRender, dispatch: null, store, type, returnData: true
        })
        console.log(Object.keys(result))
        expect(Object.keys(result).length).toBe(6)
    })

    test('add tables to metadata and RELOAD - when there is already a view addded to metadata', () => {
        let record = tableRecord
        let type = 'reload'
        dsListToRender = handleDSTableCheck({
            checked: true,
            record,
            dispatch: () => { },
            dsListToRender,
            returnData: true
        })
        let result = handleDSTableOptionsClick({
            record, option: 'addToMetadata', dsListToRender: dsListToRender, dispatch: null, store, type, returnData: true
        })
        console.log(Object.keys(result))
        expect(Object.keys(result).length).toBe(5)
    })

})