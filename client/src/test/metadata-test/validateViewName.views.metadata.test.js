import { validateViewName } from "../../components/hi-metadata/utils/views/validateViewName";
import {sampleViews, sampleTables} from './viewsConstants'

let activeView = 'onu9-uipd-07ul-x7u8-yp'
describe('validatin view name for editing or adding', () => {
    test('View name canot be empty', () => {
        let result = validateViewName('', {}, activeView, sampleViews)
        expect(result.isValid).not.toBeTruthy()
    })
    test('view name validation - pasing view name whihc is already in use', () => {
        let result = validateViewName('dimdate', sampleTables, activeView, sampleViews)
        expect(result.isValid).not.toBeTruthy();
        expect(result.msg).toBe("View Name already exists");

    })

    test('view name validation - invalid name - string with 2 chars', () => {
        let result = validateViewName('up', {}, activeView, sampleViews)
        expect(result.isValid).toBe(false);
        expect(result.msg).toBe("Please provide valid name for view");
    })

    test('view name validation - valid view name', () => {
        let result = validateViewName('View 2', sampleTables, activeView, sampleViews)
        expect(result.isValid).toBe(true)
    })

})