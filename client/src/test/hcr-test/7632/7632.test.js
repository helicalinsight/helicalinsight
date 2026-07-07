import { getDataSourceType } from "../../../components/hi-canned-reports/hcrHelperMethods";

const arr = [{
    input: {
        type: 'sql.jdbc.groovy',
        dataSourceTypes: [{
            type: 'sql.jdbc.groovy',
            name: "Groovy Plain Jdbc DataSource"
        }]
    },
    output: "Groovy Plain Jdbc DataSource"
}, {
    input: {
        type: 'sql.jdbc.groovy.managed',
        dataSourceTypes: [{
            type: 'sql.jdbc.groovy.managed',
            name: "Groovy Managed Jdbc DataSource"
        }]
    },
    output: "Groovy Managed Jdbc DataSource"
}]

arr.forEach(ele => {
    test("Testing getDataSourceType func", (done) => {
        expect(getDataSourceType(ele.input.dataSourceTypes, ele.input.type)).toEqual(ele.output);
        done();
    })
});
