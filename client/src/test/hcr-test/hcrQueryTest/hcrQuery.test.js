import { getQueryFormdata } from "../../../components/hi-canned-reports/hcrHelperMethods";
import { queryTestConstant } from "./hcrQuery-constant";

describe('Testing HCR query formdata', () => {
    queryTestConstant.forEach(ele => {
        test(ele.title, (done) => {
            expect(getQueryFormdata(ele.input)).toEqual(ele.output);
            done();
        })
    });
});
