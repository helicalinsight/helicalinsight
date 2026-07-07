const { dataProcessor } = require("../../components/hi-metadata/utils")
const { data } = require("./5586.mock.data")


describe('testing list of groovy managed connection in metadata page', () => {
    test('listing groovy conn in metadata page', () => {
        let result = dataProcessor({ ...data, returnDsToRender: true })
        expect(result.filter(obj => obj.name === 'DynamicSwitch').length).toBe(1)
    })

    test('listing groovy conn in metadata page no if children in dynamic switch', () => {
        let result = dataProcessor({ ...data, returnDsToRender: true })
        expect(result.filter(obj => obj.name === 'DynamicSwitch')[0].children.length).toBe(3)
    })

    test('listing groovy conn in metadata page testing driver of children', () => {
        let result = dataProcessor({ ...data, returnDsToRender: true })
        let dynamicSwitch = result.filter(obj => obj.name === 'DynamicSwitch')[0]
        let children = dynamicSwitch.children.filter(obj => obj.driver === 'dynamicSwitch')
        expect(children.length).toBe(3)
    })
})