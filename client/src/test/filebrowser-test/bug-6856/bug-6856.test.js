import { getPasteFormData } from "../../../components/hi-fileBrowser/helperMethods";


    describe("Testing getPasteFormData func", () => {
        test("testing for cut", () => {
            const inputData = {
                copyOrCutItemDetails: {action: 'cut', sourceUrl: 'test/a', sourcePermission: '4'},
                record: {path: 'b', permissionLevel: '3'},
                item: {isSkipped: false}
            }; 
            const output = {
                sourceArray: JSON.stringify([""]),
                action: 'cut',
                formData: {
                  "sourceUrl": 'test/a',
                  "destinationUrl": 'b',
                  "sourcePermission": '4',
                  "destPermission": '3',
                  "isConflictSkip": false
                }
            };
            expect(getPasteFormData({...inputData})).toEqual(output);
        });
        test("testingfor copy", () => {
            const inputData = {
                copyOrCutItemDetails: {action: 'copy', sourceUrl: 'test/a', sourcePermission: '2'},
                record: {path: 'b', permissionLevel: '3'},
                item: {isSkipped: true},
            }; 
            const output = {
                sourceArray: JSON.stringify([""]),
                action: 'copy',
                formData: {
                  "sourceUrl": 'test/a',
                  "destinationUrl": 'b',
                  "sourcePermission": '2',
                  "destPermission": '3',
                  "isConflictSkip": true
                }
            };
            expect(getPasteFormData({...inputData})).toEqual(output);
        });
    })