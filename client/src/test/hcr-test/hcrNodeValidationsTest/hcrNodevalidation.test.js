import { validateNodes } from "../../../components/hi-canned-reports/hcrHelperMethods";
import { nodeValidationTest } from "./hcrNodeValidationTest-constant";

describe('Testing Node validation', () => {
    nodeValidationTest.forEach(ele => {
        test(ele.title, (done) => {
            expect(validateNodes(ele.input).isValid).toBe(ele.output);
            done();
        })
    });
});
