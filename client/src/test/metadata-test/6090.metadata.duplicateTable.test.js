import { getDuplicateTableFormdata } from "../../components/hi-metadata/utils";
import { bug6090Constants } from "./6090Constants";

describe('Metadata 6090 checking getDuplicateTableFormdata in metadata handleSave function', () => {
    const {createMode, ImediateSave} = bug6090Constants;
	test('Checking in create mode with duplicate table', (done) => {
       const {withDuplicateTable, result} = createMode;
       expect(getDuplicateTableFormdata({...withDuplicateTable})).toEqual(result);
		done();
	});
    test('Checking in after save with duplicate table', (done) => {
        const {withNewDuplicateTable, result} = ImediateSave;
        expect(getDuplicateTableFormdata({...withNewDuplicateTable})).toEqual(result);
		done();
	});
});
