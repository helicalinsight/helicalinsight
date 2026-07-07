import { updateDatasourceListToRender } from "../../components/hi-metadata/utils";
import { b_6197 } from "./6187.constants";

describe('Test updateDatasourceListToRender functionality for removing table', () => {
    const {removeNrmlTable, removeTableFromCatalog} = b_6197;
	test('test when remove normal dimdate table from schema', (done) =>{
        const {input, result} = removeNrmlTable;
		expect(updateDatasourceListToRender({ ...input })).toEqual(result);
        done();
	})
    test('test when remove normal dimdate table from catalog', (done) =>{
        const {input, result} = removeTableFromCatalog;
        let returnVal = updateDatasourceListToRender({ ...input });
		expect(returnVal).toEqual(result);
        done();
	})
});
