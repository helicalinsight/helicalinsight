import { appendDesignerConstants, appendDesignerObjectConstants, designerStateComponenetsInitialPositionConstants, removeDesignerConstants, removeDesignerObjectConstants, removeLayoutConstants } from "../../../components/hi-dashboard-designer/utils/config-dashboard-gridSettings"
import { designerLayoutData, gridItemConfigTestData, gridSettingsTestData, initialPositionTestData } from "./config-dashboard-constants-mockdata"

describe('testing appendDesignerConstants function', () => {

    test('checking when gridItemConfigConstants is sent', () => {
        gridItemConfigTestData.append.forEach(ele => {
            expect(appendDesignerConstants({constantData:ele.constantData, apiData:ele.apiData})).toEqual(ele.result)
        })
    })

    test('checking when gridSettingsConstants is sent', () => {
        gridSettingsTestData.append.forEach(ele => {
            expect(appendDesignerConstants({constantData:ele.constantData, apiData:ele.apiData})).toEqual(ele.result)
        })
    })
    
})

describe('testing removeDesignerConstants function', () => {

    test('checking when gridItemConfigConstants is sent', () => {
        gridItemConfigTestData.remove.forEach(ele => {
            expect(removeDesignerConstants({constantData:ele.constantData, apiData:ele.apiData})).toEqual(ele.result)
        })
    })

    test('checking when gridSettingsConstants is sent', () => {
        gridSettingsTestData.remove.forEach(ele => {
            expect(removeDesignerConstants({constantData:ele.constantData, apiData:ele.apiData})).toEqual(ele.result)
        })
    })
    
})

describe('testing appendDesignerObjectConstants function', () => {

    test('checking when designerStateComponenetsInitialPositionConstants is sent', () => {
        initialPositionTestData.append.forEach(ele => {
            expect(appendDesignerObjectConstants({apiObj: ele.data, constantObj: designerStateComponenetsInitialPositionConstants})).toEqual(ele.result)
        })
    })
    
})

describe('testing removeDesignerObjectConstants function', () => {

    test('checking when designerStateComponenetsInitialPositionConstants is sent', () => {
        initialPositionTestData.remove.forEach(ele => {
            expect(removeDesignerObjectConstants({apiObj: ele.data, constantObj: designerStateComponenetsInitialPositionConstants})).toEqual(ele.result)
        })
    })
    
})


test('testing removeLayoutConstants function', () => {
    designerLayoutData.forEach(ele => {
        expect(removeLayoutConstants(ele.data)).toEqual(ele.result)
    })
})