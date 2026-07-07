import { getFileName } from "../../components/hi-fileBrowser/components/ExportModal";

describe('Testing export Modal', () => {
	test('checking file name', (done) => {
        const data = [{
            type: 'file',
            name: 'dummy.ext',
            path:'narendra/dummy.ext',
            value: 'dummy.ext'
        }, {
            type: 'folder',
            name: 'DUMMY',
            value: ''
        }]
        data.map(ele =>{
            expect(getFileName(ele)).toEqual(ele.value);
        })
		done();
	});
});
