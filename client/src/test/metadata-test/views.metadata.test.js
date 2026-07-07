import { getMetadata, handleFetchedViews } from "../../components/hi-metadata/utils";
import { FDForSaveView } from "../../components/hi-metadata/utils/views/FDForSaveView";
import { dispatch, mockAxios } from '../../__mocks__/axios'
import { mockUtil } from './utils/mockImplementation'
import { retireveViewLabelsForDerby, retireveViewLabelsForGroovyMan, sampleViews } from './viewsConstants'

describe('testing handle fetched views', () => {

    test('checking if all the checking if the required keys and values are available or not', () => {
        let result = handleFetchedViews({
            views: sampleViews,
            returnData: true
        })
        let validation = true
        result.forEach(function (eachView) {
            if (!eachView.uuid) {
                validation = false
            }
            if (eachView.type !== 'view') {
                validation = false
            }
            if (!eachView.name.length) {
                validation = false
            }
            if (!Object.keys(eachView.columns).length) {
                validation = false
            }
        })
        expect(validation).toBe(true)
    })

    test('checking if all the views are returned and if uuid is added or not', () => {
        let result = handleFetchedViews({
            views: sampleViews,
            returnData: true
        })
        let validation = true
        result.forEach(function (eachView) {
            if (!eachView.uuid) {
                validation = false
            }
        })
        if (sampleViews.length !== result.length) {
            validation = false
        }
        expect(validation).toBe(true)
    })

})

describe('testing retireve view lables formdata', () => {
    test('testing for groovy managed connection', () => {
        let result = FDForSaveView(retireveViewLabelsForGroovyMan)
        expect(result.dir).toEqual('Gagan')
    })

    test('testing for normal derby connection', () => {
        let result = FDForSaveView(retireveViewLabelsForDerby)
        expect(result.dir).toBeFalsy()
    })
})