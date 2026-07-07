import { getPreviewLine, getPreviewTextField } from "../../../components/hi-canned-reports/hcrHelperMethods";
import { textfieldTest, lineNodeTest } from "./textfield-constant";

describe('Testing text node while previewing', () => {
    textfieldTest.forEach(ele => {
        test(ele.title, (done) => {
            expect(getPreviewTextField(ele.input)).toEqual(ele.output);
            done();
        })
    });
});

describe('Testing line node while previewing', () => {
    lineNodeTest.forEach(ele => {
        test(ele.title, (done) => {
            expect(getPreviewLine(ele.input)).toEqual(ele.output);
            done();
        })
    });
});