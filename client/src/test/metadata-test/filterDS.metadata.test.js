import { filterDS } from '../../components/hi-metadata/utils/filterDS'
import { datasourceListToRender, record } from './constants'
import { isObject } from '../../utils/is-object'
import { cloneDeep } from 'lodash-es'

/**
 * datasource to render is mandatory and it is an array
 * if datasourcetorender is not present then it returns []
 * if datasourcetorender is present and record is not present then it returns datasourceListToRender
 */

describe('Datasource search - invalid arguments', () => {
    test('invalid dsList', () => {
        let result = filterDS({ dsList: 'datasourceListToRender', record, text: '', type: '' })
        expect(Array.isArray(result) && result.length === 0).toBeTruthy()
    })

    test('invalid record', () => {
        let dsList = cloneDeep(datasourceListToRender)
        let result = filterDS({ dsList: dsList, record: null, text: '', type: '' })
        expect(result).toEqual(dsList)
    })

    test('invalid text', () => {
        let dsList = cloneDeep(datasourceListToRender)
        let result = filterDS({ dsList: dsList, record, text: 123, type: '' })
        expect(result).toEqual(dsList)
    })

    test('invalid dslist - not passing', () => {
        let result = filterDS({ record, text: 123, type: '' })
        expect(Array.isArray(result) && result.length === 0).toBeTruthy()
    })

    test('invalid text - not passing', () => {
        let dsList = cloneDeep(datasourceListToRender)
        let result = filterDS({ dsList: dsList, record, text: 123, type: '' })
        expect(result).toEqual(dsList)
    })
    /**
     * not checking for type as it is not mandatory and also not being used as of now
     */
})

describe('Datasource search - table search functionality', function () {
    test('searching table name with first 3 letters', () => {
        let searchString = 'geo'
        let dsList = cloneDeep(datasourceListToRender)
        let result = filterDS({ dsList: dsList, record, text: searchString, type: '' })
        expect(
            result[0].children[2].children[0].children.length === 1
            && result[0].children[2].children[0].name === 'HIUSER'
            && result[0].children[2].children.length === 1
            && result[0].children[2].children[0].children[0].name.startsWith(searchString)
        ).toEqual(true)
    })

    // test('searching table name with first 1 letter', () => { // should work on this later
    //     let searchString = 'g'
    //     let dsList = cloneDeep(datasourceListToRender)
    //     let result = filterDS({ dsList: dsList, record, text: searchString, type: '' })
    //     expect(
    //         result[0].children[2].children[0].children.length === 1
    //         && result[0].children[2].children[0].name === 'HIUSER'
    //         && result[0].children[2].children.length === 1
    //         && result[0].children[2].children[0].children[0].name.startsWith(searchString)
    //     ).toEqual(true)
    // })

    test('searching table name with full name', () => {
        let searchString = "geo_cordinates"
        let dsList = cloneDeep(datasourceListToRender)
        let result = filterDS({ dsList, record, text: searchString, type: '' })
        expect(
            result[0].children[2].children[0].children.length === 1
            && result[0].children[2].children[0].name === 'HIUSER'
            && result[0].children[2].children.length === 1
            && result[0].children[2].children[0].children[0].name.startsWith(searchString)
        ).toEqual(true)
    })

    test('searching table name with wrong name', () => {
        let searchString = "geo_cordinates_1"
        let dsList = cloneDeep(datasourceListToRender)
        let result = filterDS({ dsList, record, text: searchString, type: '' })
        expect(
            result[0].children[2].children.length === 0
        ).toEqual(true)
    })
})

describe('Datasource search - schema/catalog search functionality', function () {
    test('searching schema with proper name', () => {
        let dsList = cloneDeep(datasourceListToRender)
        let temp = cloneDeep(datasourceListToRender)
        let searchString = "HIUSER"
        let result = filterDS({ dsList, record, text: searchString, type: '' })
        expect(
            result[0].children[2].children.length  === 1
            && result[0].children[2].children[0].name === searchString
            && result[0].children[2].children[0].children.length === 5
            // ===

            // datasourceListToRender[0].children[2].children[0].children.length
        ).toBe(true)
    })

    test('searching schema with first three letters name', () => {
        let dsList = cloneDeep(datasourceListToRender)
        let temp = cloneDeep(datasourceListToRender)
        let searchString = "HIU"
        let result = filterDS({ dsList, record, text: searchString, type: '' })
        expect(
            result[0].children[2].children.length === 1
            && result[0].children[2].children[0].children.length === 5
        ).toBe(true)
    })

    test('searching schema with invalid name that is not available', () => {
        let dsList = cloneDeep(datasourceListToRender)
        let searchString = "HIU_"
        let result = filterDS({ dsList, record, text: searchString, type: '' })
        expect(
            result[0].children[2].children.length === 0
        ).toBe(true)
    })
})