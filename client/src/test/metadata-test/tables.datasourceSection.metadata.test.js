import { datasourceListToRender, record, tableRecord, employee_details_record, meeting_details_record, geo_cordinates_record } from './constants'
import { isObject } from '../../utils/is-object'
import { cloneDeep } from 'lodash-es'
import { handleDSTableCheck } from '../../components/hi-metadata/utils/handleDSTableCheck'
import { handleDSTableOptionsClick } from '../../components/hi-metadata/utils/handleDSTableOptionsClick'


describe('validating arguments for handleDSTableCheck', () => {
    test('valid args', () => {
        let dsListToRender = cloneDeep(datasourceListToRender)
        let record = cloneDeep(tableRecord)
        let result = handleDSTableCheck({
            checked: true,
            record,
            dispatch: () => { },
            dsListToRender,
            returnData: true
        })
        expect(result[0].children[2].children[3].children[0].selected).toBeTruthy()
    })

    test('invalid dsListToRender', () => {
        let dsListToRender = cloneDeep(datasourceListToRender)
        let record = cloneDeep(tableRecord)
        let result = handleDSTableCheck({
            checked: true,
            record,
            dispatch: () => { },
            dsListToRender: false,
            returnData: true
        })
        expect(result).toBe(false)
    })

    test('invalid record', () => {
        let dsListToRender = cloneDeep(datasourceListToRender)
        let record = cloneDeep(tableRecord)
        let result = handleDSTableCheck({
            checked: true,
            record: null,
            dispatch: () => { },
            dsListToRender,
            returnData: true
        })
        expect(result).toBe(dsListToRender)
    })

    test('invalid checked', () => {
        let dsListToRender = cloneDeep(datasourceListToRender)
        let record = cloneDeep(tableRecord)
        let result = handleDSTableCheck({
            checked: 'true',
            record,
            dispatch: () => { },
            dsListToRender,
            returnData: true
        })
        expect(result).toBe(dsListToRender)
    })

    test('invalid checked and dslisttorender', () => {
        let dsListToRender = cloneDeep(datasourceListToRender)
        let record = cloneDeep(tableRecord)
        let result = handleDSTableCheck({
            checked: 'true',
            record,
            dispatch: () => { },
            dsListToRender: false,
            returnData: true
        })
        expect(result).toBe(false)
    })
})

describe('(Un)Selecting tables individually - datasource-section - metadata-page ', () => {
    test('selecting single table - checking selected tables', () => {
        let dsListToRender = cloneDeep(datasourceListToRender)
        let record = cloneDeep(tableRecord)
        let result = handleDSTableCheck({
            checked: true,
            record,
            dispatch: () => { },
            dsListToRender,
            returnData: true
        })
        expect(result[0].children[2].children[3].children[0].selected).toBeTruthy()
    })

    test('selecting single table - checking uselected tables', () => {
        let dsListToRender = cloneDeep(datasourceListToRender)
        let record = cloneDeep(tableRecord)
        let result = handleDSTableCheck({
            checked: true,
            record,
            dispatch: () => { },
            dsListToRender,
            returnData: true
        })
        expect(result[0].children[2].children[3].children[1].selected).not.toBeTruthy()
    })

    test('selecting single table - checking uselected tables -2', () => {
        let dsListToRender = cloneDeep(datasourceListToRender)
        let record = cloneDeep(tableRecord)
        let result = handleDSTableCheck({
            checked: true,
            record,
            dispatch: () => { },
            dsListToRender,
            returnData: true
        })
        expect(result[0].children[2].children[3].children[2].selected).not.toBeTruthy()
    })

    test('selecting single table - checking uselected tables - 3 ', () => {
        let dsListToRender = cloneDeep(datasourceListToRender)
        let record = cloneDeep(tableRecord)
        let result = handleDSTableCheck({
            checked: true,
            record,
            dispatch: () => { },
            dsListToRender,
            returnData: true
        })
        expect(result[0].children[2].children[3].children[3].selected).not.toBeTruthy()
    })

    test('unselecting single table - checking selected tables', () => {
        let dsListToRender = cloneDeep(datasourceListToRender)
        let record = cloneDeep(tableRecord)
        let result = handleDSTableCheck({
            checked: false,
            record,
            dispatch: () => { },
            dsListToRender,
            returnData: true
        })
        expect(result[0].children[2].children[3].children[0].selected).toEqual(false);
    })

    test('unselecting single table - checking other tables - 1', () => {
        let dsListToRender = cloneDeep(datasourceListToRender)
        let record = cloneDeep(tableRecord)
        let result = handleDSTableCheck({
            checked: false,
            record,
            dispatch: () => { },
            dsListToRender,
            returnData: true
        })
        expect(result[0].children[2].children[3].children[1].selected).toEqual(undefined);
    })

    test('unselecting single table - checking other tables - 2', () => {
        let dsListToRender = cloneDeep(datasourceListToRender)
        let record = cloneDeep(tableRecord)
        let result = handleDSTableCheck({
            checked: false,
            record,
            dispatch: () => { },
            dsListToRender,
            returnData: true
        })
        expect(result[0].children[2].children[3].children[2].selected).toEqual(undefined);
    })

    test('unselecting single table - checking other tables - 3', () => {
        let dsListToRender = cloneDeep(datasourceListToRender)
        let record = cloneDeep(tableRecord)
        let result = handleDSTableCheck({
            checked: false,
            record,
            dispatch: () => { },
            dsListToRender,
            returnData: true
        })
        expect(result[0].children[2].children[3].children[3].selected).toEqual(undefined);
    })
})


describe('validating arguments for handleDSTableOptionsClick', () => {
    test('valid inputs', () => {
        let dsListToRender = cloneDeep(datasourceListToRender)
        let record = cloneDeep(tableRecord)
        let result = handleDSTableOptionsClick({
            record,
            option: 'selectAll',
            dsListToRender,
            dispatch: () => { },
            store: null,
            type: 'selectTables',
            returnData: true
        })
        expect(Array.isArray(result)).toBe(true)
    })

    test('invalid arguments - dslistToRender', () => {
        let dsListToRender = cloneDeep(datasourceListToRender)
        let record = cloneDeep(tableRecord)
        let result = handleDSTableOptionsClick({
            record,
            option: 'selectAll',
            dsListToRender: false,
            dispatch: () => { },
            store: null,
            type: 'selectTables',
            returnData: true
        })
        expect(result).toBe(false)
    })

    test('invalid arguments - record', () => {
        let dsListToRender = cloneDeep(datasourceListToRender)
        let record = cloneDeep(tableRecord)
        let result = handleDSTableOptionsClick({
            record: false,
            option: 'selectAll',
            dsListToRender,
            dispatch: () => { },
            store: null,
            type: 'selectTables',
            returnData: true
        })
        expect(result).toBe(dsListToRender)
    })

    test('invalid arguments - passing both record, dslistToRender', () => {
        let dsListToRender = cloneDeep(datasourceListToRender)
        let record = cloneDeep(tableRecord)
        let result = handleDSTableOptionsClick({
            record: false,
            option: 'selectAll',
            dsListToRender: false,
            dispatch: () => { },
            store: null,
            type: 'selectTables',
            returnData: true
        })
        expect(result).toBe(false)
    })
})


describe('selecting all tables at once', () => {
    test('selecting all - checking if any unselected', () => {
        let dsListToRender = cloneDeep(datasourceListToRender)
        let record = cloneDeep(tableRecord)
        let result = handleDSTableOptionsClick({
            record,
            option: 'selectAll',
            dsListToRender,
            dispatch: () => { },
            store: null,
            type: 'selectTables',
            returnData: true
        })
        let children = result[0].children[2].children[3].children
        let expected = children.filter(child => child.selected !== true)
        expect(expected.length).toBe(0)
    })

    test('selecting all - checking for selected', () => {
        let dsListToRender = cloneDeep(datasourceListToRender)
        let record = cloneDeep(tableRecord)
        let result = handleDSTableOptionsClick({
            record,
            option: 'selectAll',
            dsListToRender,
            dispatch: () => { },
            store: null,
            type: 'selectTables',
            returnData: true
        })
        let children = result[0].children[2].children[3].children
        let expected = children.filter(child => child.selected === true)
        expect(expected.length).toBe(children.length)
    })

    test('UNselecting all - checking for selected', () => {
        let dsListToRender = cloneDeep(datasourceListToRender)
        let record = cloneDeep(tableRecord)
        let result = handleDSTableOptionsClick({
            record,
            option: 'reset',
            dsListToRender,
            dispatch: () => { },
            store: null,
            type: 'selectTables',
            returnData: true
        })
        let children = result[0].children[2].children[3].children
        let expected = children.filter(child => child.selected === true)
        expect(expected.length).toBe(0)
    })

    test('UNselecting all - checking for unselected', () => {
        let dsListToRender = cloneDeep(datasourceListToRender)
        let record = cloneDeep(tableRecord)
        let result = handleDSTableOptionsClick({
            record,
            option: 'reset',
            dsListToRender,
            dispatch: () => { },
            store: null,
            type: 'selectTables',
            returnData: true
        })
        let children = result[0].children[2].children[3].children
        let expected = children.filter(child => child.selected === false)
        expect(expected.length).toBe(children.length)
    })
})

// describe('add tables to metadata', () => {

//     test('add tables to metadata - adding all tables - type reload', () => {
//         let dsListToRender = cloneDeep(datasourceListToRender)
//         let record = cloneDeep(tableRecord)
//         let type = 'reload'
//         let result = handleDSTableOptionsClick({
//             record, option: 'selectAll', dsListToRender, dispatch: null, store: null, type, returnData: true
//         })
//         result = handleDSTableOptionsClick({
//             record, option: 'addToMetadata', dsListToRender: result, dispatch: null, store: null, type, returnData: true
//         })
//         expect(Object.keys(result).length).toBe(5)
//     })

//     test('add tables to metadata - adding all tables - type merge', () => {
//         let dsListToRender = cloneDeep(datasourceListToRender)
//         let record = cloneDeep(tableRecord)
//         let type = 'merge'
//         let result = handleDSTableOptionsClick({
//             record, option: 'selectAll', dsListToRender, dispatch: null, store: null, type, returnData: true
//         })
//         result = handleDSTableOptionsClick({
//             record, option: 'addToMetadata', dsListToRender: result, dispatch: null, store: null, type, returnData: true
//         })
//         expect(Object.keys(result).length).toBe(5)
//     })

//     test('add tables to metadata - addin one tables', () => {
//         let dsListToRender = cloneDeep(datasourceListToRender)
//         let record = cloneDeep(tableRecord)
//         let result = handleDSTableCheck({
//             checked: true,
//             record,
//             dispatch: () => { },
//             dsListToRender,
//             returnData: true
//         })
//         let type = 'merge'
//         result = handleDSTableOptionsClick({
//             record, option: 'addToMetadata', dsListToRender: result, dispatch: null, store: null, type, returnData: true
//         })
//         expect(Object.keys(result).length).toBe(1)
//     })

//     test('add tables to metadata - when no table is selected', () => {
//         let dsListToRender = cloneDeep(datasourceListToRender)
//         let record = cloneDeep(tableRecord)
//         let type = 'reload'
//         let result = handleDSTableOptionsClick({
//             record, option: 'addToMetadata', dsListToRender, dispatch: null, store: null, type, returnData: true
//         })
//         expect(result).toBeFalsy()
//     })

// })

// describe('add tables to metadata - MERGE/RELOAD', () => {
//     test('add one table and unselect then add second table then MERGE', () => {
//         let dsListToRender = cloneDeep(datasourceListToRender)
//         let record = cloneDeep(tableRecord)
//         let type = 'merge'
//         dsListToRender = handleDSTableCheck({
//             checked: true,
//             record: geo_cordinates_record,
//             dispatch: () => { },
//             dsListToRender,
//             returnData: true
//         })
//         let result = handleDSTableOptionsClick({
//             record, option: 'addToMetadata', dsListToRender: dsListToRender, dispatch: null, store: null, type, returnData: true
//         })
//         dsListToRender = handleDSTableCheck({
//             checked: false,
//             record: geo_cordinates_record,
//             dispatch: () => { },
//             dsListToRender,
//             returnData: true
//         })
//         dsListToRender = handleDSTableCheck({
//             checked: true,
//             record: employee_details_record,
//             dispatch: () => { },
//             dsListToRender,
//             returnData: true
//         })
//         result = handleDSTableOptionsClick({
//             record, option: 'addToMetadata', dsListToRender: dsListToRender, dispatch: null, store: null, type, returnData: true
//         })
//         expect(Object.keys(result).length).toBe(1)
//     })

//     test('add one table and unselect then add second table then RELOAD', () => {
//         let dsListToRender = cloneDeep(datasourceListToRender)
//         let record = cloneDeep(tableRecord)
//         let type = 'reload'
//         dsListToRender = handleDSTableCheck({
//             checked: true,
//             record: geo_cordinates_record,
//             dispatch: () => { },
//             dsListToRender,
//             returnData: true
//         })
//         let result = handleDSTableOptionsClick({
//             record, option: 'addToMetadata', dsListToRender: dsListToRender, dispatch: null, store: null, type, returnData: true
//         })
//         dsListToRender = handleDSTableCheck({
//             checked: false,
//             record: geo_cordinates_record,
//             dispatch: () => { },
//             dsListToRender,
//             returnData: true
//         })
//         dsListToRender = handleDSTableCheck({
//             checked: true,
//             record: employee_details_record,
//             dispatch: () => { },
//             dsListToRender,
//             returnData: true
//         })
//         result = handleDSTableOptionsClick({
//             record, option: 'addToMetadata', dsListToRender: dsListToRender, dispatch: null, store: null, type, returnData: true
//         })
//         expect(Object.keys(result).length).toBe(1)
//     })

//     test('add one table and then unselect and then select two tables and then add to metadata RELOAD', () => {
//         let dsListToRender = cloneDeep(datasourceListToRender)
//         let record = cloneDeep(tableRecord)
//         let type = 'reload'
//         dsListToRender = handleDSTableCheck({
//             checked: true,
//             record: geo_cordinates_record,
//             dispatch: () => { },
//             dsListToRender,
//             returnData: true
//         })
//         let result = handleDSTableOptionsClick({
//             record, option: 'addToMetadata', dsListToRender: dsListToRender, dispatch: null, store: null, type, returnData: true
//         })
//         dsListToRender = handleDSTableCheck({
//             checked: false,
//             record: geo_cordinates_record,
//             dispatch: () => { },
//             dsListToRender,
//             returnData: true
//         })
//         dsListToRender = handleDSTableCheck({
//             checked: true,
//             record: employee_details_record,
//             dispatch: () => { },
//             dsListToRender,
//             returnData: true
//         })
//         dsListToRender = handleDSTableCheck({
//             checked: true,
//             record: meeting_details_record,
//             dispatch: () => { },
//             dsListToRender,
//             returnData: true
//         })
//         result = handleDSTableOptionsClick({
//             record, option: 'addToMetadata', dsListToRender: dsListToRender, dispatch: null, store: null, type, returnData: true
//         })
//         expect(Object.keys(result).length).toBe(2)
//     })

//     test('add one table and then unselect and then select two tables and then add to metadata MERGE', () => {
//         let dsListToRender = cloneDeep(datasourceListToRender)
//         let record = cloneDeep(tableRecord)
//         let type = 'merge'
//         dsListToRender = handleDSTableCheck({
//             checked: true,
//             record: geo_cordinates_record,
//             dispatch: () => { },
//             dsListToRender,
//             returnData: true
//         })
//         let result = handleDSTableOptionsClick({
//             record, option: 'addToMetadata', dsListToRender: dsListToRender, dispatch: null, store: null, type, returnData: true
//         })
//         dsListToRender = handleDSTableCheck({
//             checked: false,
//             record: geo_cordinates_record,
//             dispatch: () => { },
//             dsListToRender,
//             returnData: true
//         })
//         dsListToRender = handleDSTableCheck({
//             checked: true,
//             record: employee_details_record,
//             dispatch: () => { },
//             dsListToRender,
//             returnData: true
//         })
//         dsListToRender = handleDSTableCheck({
//             checked: true,
//             record: meeting_details_record,
//             dispatch: () => { },
//             dsListToRender,
//             returnData: true
//         })
//         result = handleDSTableOptionsClick({
//             record, option: 'addToMetadata', dsListToRender: dsListToRender, dispatch: null, store: null, type, returnData: true
//         })
//         expect(Object.keys(result).length).toBe(2)
//     })
// })