import { handleCheckFileInNewResource } from "../../../components/hi-fileBrowser/helperMethods";

    describe("Testing handleCheckFileInNewResource func", () => {
        test("testing for cut/cpy", () => {
            const files= [{name: 'abc', description: 'abc.1', type: 'file'}, {name: 'xyz', description: 'xyz.1', type: 'file'}, {name: 'abc', type: 'folder'}];
            expect(handleCheckFileInNewResource({files, sourceName: 'abc', sourceType: 'folder'})).toBeTruthy();
            expect(handleCheckFileInNewResource({files, sourceName: 'abc.1', sourceType: 'file'})).toBeTruthy();
            expect(handleCheckFileInNewResource({files, sourceName: 'abc', sourceType: 'file'})).toBeFalsy();
            expect(handleCheckFileInNewResource({files, sourceName: 'abc.1', sourceType: 'folder'})).toBeFalsy();
            expect(handleCheckFileInNewResource({files, sourceName: 'xyz.1', sourceType: 'file'})).toBeTruthy();
        });
    })