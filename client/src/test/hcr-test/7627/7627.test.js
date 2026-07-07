import { hcrParaDate, hcrParaDateAndTime, hcrParaInput, hcrParaQueryBasedDropdownList } from "../../../components/hi-canned-reports/hcr-constants";
import { getFilterParams } from "../../../components/hi-canned-reports/hcrHelperMethods";

const parametersList = [{
    id: 1,
    canvasValues: {
        filterType: 'abc',
        defaultValue: 'xyz'
    }
}, {
    id: 2,
    canvasValues: {
        filterType: 'abc',
        defaultValue: 'xyz'
    }
}, {
    id: 3,
    canvasValues: {
        filterType: 'abc',
        defaultValue: 'xyz'
    }
}, {
    id: 4,
    canvasValues: {
        filterType: 'abc',
        defaultValue: 'xyz'
    }
}, 
// {
//     id: 5,
//     canvasValues: {
//         filterType: 'abc',
//         defaultValue: 'xyz'
//     }
// }, {
//     id: 6,
//     canvasValues: {
//         filterType: 'abc',
//         defaultValue: 'xyz'
//     }
// }
]

const filters = [{
    uid: 1,
    condition: "IS_ONE_OF",
    values: [1, 2]
}, {
    uid: 2,
    condition: "CONTAINS",
    values: [1]
}, {
    uid: 3,
    condition: "EQUALS",
    values: ['22-10-2024'],
    backendDataType: "java.sql.Date",
    valueFormat: 'dd-mm-yyyy',
    displayFormat: 'yyyy-mm-dd'
},  {
    uid: 4,
    condition: "EQUALS",
    values: ['22-10-2024 02:30:01'],
    backendDataType: "java.sql.Timestamp",
    valueFormat: 'dd-mm-yyyy hh:mm:ss',
    displayFormat: 'yyyy-mm-dd hh:mm:ss'
}]

const reqParametersList = [{
    id: 1,
    canvasValues: {
        filterType: hcrParaQueryBasedDropdownList,
        defaultValue: '1,2'
    }
}, {
    id: 2,
    canvasValues: {
        filterType: hcrParaInput,
        defaultValue: 1
    }
}, {
    id: 3,
    canvasValues: {
        filterType: hcrParaDate,
        defaultValue: '22-10-2024',
        valueFormat: 'dd-mm-yyyy',
        displayFormat: 'yyyy-mm-dd'
    }
}, {
    id: 4,
    canvasValues: {
        filterType: hcrParaDateAndTime,
        defaultValue: '22-10-2024 02:30:01', 
        valueFormat: 'dd-mm-yyyy hh:mm:ss',
        displayFormat: 'yyyy-mm-dd hh:mm:ss'
    }
}]

test('Testing getFilterParams function' , (done) => {
    expect(getFilterParams({parametersList, filters})).toEqual(reqParametersList);
    done();
})
