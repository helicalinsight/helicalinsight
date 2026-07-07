import { intToStringFunc } from "../../components/hi-metadata/utils";

const dataSources = [{data: {id: 1}}];
const result = [{data: {id: '1'}}]

describe('Test intToStringFunc functionality', () => {
	test('conversion num to string', (done) =>{
		expect(intToStringFunc(dataSources)).toEqual(result);
        done();
	})
});
