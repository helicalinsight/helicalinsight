import { generateReport, getAppliedDatabaseFunctions } from "../../../components/hi-reports/utils/base";
import { stateDataWithNestedDBFunctions, stateDataWithFilterDbFunctions } from "./mock.data";

const dbFnsExpectedResult = {
  dbFunctions: [{
    "key": "sql.text.lower",
    "description": "Converts all characters in the specified string to lowercase. Example: LOWER('BENGALURU') result: bengaluru ",
    "value": "LOWER",
    "signature": "lower(${string})",
    "returns": "text",
    "parameters": [
      {
        "name": "string",
        "column": false
      }
    ]
  },
  {
    "key": "sql.text.upper",
    "description": "Converts all characters in a string to uppercase. Example: UPPER('bengaluru') result: BENGALURU",
    "value": "UPPER",
    "signature": "upper(${string})",
    "returns": "text",
    "parameters": [
      {
        "name": "string",
        "column": true,

      }
    ]
  }
  ]
}




const filterDbFunctionExpectedResult = {
  dbFunctions: [{
    "key": "sql.text.lower",
    "description": "Converts all characters in the specified string to lowercase. Example: LOWER('BENGALURU') result: bengaluru ",
    "value": "LOWER",
    "signature": "lower(${string})",
    "returns": "text",
    "parameters": [
      {
        "name": "string",
        "column": false
      }
    ]
  },
  {
    "key": "sql.text.upper",
    "description": "Converts all characters in a string to uppercase. Example: UPPER('bengaluru') result: BENGALURU",
    "value": "UPPER",
    "signature": "upper(${string})",
    "returns": "text",
    "parameters": [
      {
        "name": "string",
        "column": true,

      }
    ]
  },
  {
    "key": "sql.numeric.abs",
    "description": "Returns the absolute value of a number. Example:abs(-24) result:24",
    "value": "ABS",
    "signature": "abs(${number})",
    "returns": "numeric",
    "parameters": [
      {
        "name": "number",
        "column": true,
        "value": "HIUSER.employee_details.employee_id"
      }
    ]
  }
  ]
}

describe("generate Report test cases", () => {
  test("loadReportData is dispatched when mode is not 'edit' or 'create'", () => {
    const dispatch = jest.fn();
    const getApi = jest.fn();
    const activeReport = { id: 1, name: "Report 1", fields: ["!"], generateQuery: false, getFormData: false };
    const mode = "open";
    generateReport({ ...activeReport, mode }, dispatch, getApi);

    expect(dispatch).toHaveBeenCalledTimes(1);
  });

  test("to get the database function from fields", () => {
    const appliedDbFunctions = getAppliedDatabaseFunctions(stateDataWithNestedDBFunctions)
    expect(appliedDbFunctions).toEqual(dbFnsExpectedResult)
  })

  test("to get the database function from filters and fields", () => {
    const appliedDbFunctions = getAppliedDatabaseFunctions(stateDataWithFilterDbFunctions)
    expect(appliedDbFunctions).toEqual(filterDbFunctionExpectedResult)
  })
})