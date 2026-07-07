/**
 * select - which all columns
 * from - table
 * alias -
 * where - filterings, filter/having exressions
 * sample - limit, offset
 * sort - asc, desc
 * aggregations -
 * joins -
 * dbfucntions -
 */

/**
 * TEST CASES .test.js
 * 1. connect to metadata (new ReportQuery(pass metadata path to contructor)).show() -> this will generate a formData which will have metadata
 * 2. ReportQuery.select(...columns).show() -> this will return the formdata with the columns added and also the abvoe metadata
 * 3. ReportQuery.from(...tableName).show() -> thi will include atble info with the above info
 *          this can aslo be executed before .select -> in such case form data with metadta and table name to be returned
 * 4.
 */
import ReportQuery from '../index'

describe("pass datasource as arg to constructor and checking datasource in generated formdata", () => {
    let dataSource = {
        "location": "1463377807724/1463377836985",
        "metadataFileName": "e9be6771-995b-40eb-a01c-304857a100a1.metadata"
    }
    let $ = jest.fn()
    
    let api = new ReportQuery(dataSource)
    let formData = api.generateFilterFormData().show()
    test('passing datasource as an empty obj -', () => {
        let dataSource = {}
        let queryApi = new ReportQuery(dataSource)
        let formData = queryApi.generateFilterFormData().show()
        expect(formData.location).toBeFalsy()
    })

    test('passing datasource as an empty string -', () => {
        let dataSource = ''
        let queryApi = new ReportQuery(dataSource)
        let formData = queryApi.generateFilterFormData().show()
        expect(formData.location).toBeFalsy()
    })

    test('passing datasource as an object with only location property :: formdatas should not have location prop -', () => {
        let dataSource = { location: '1463377807724/1463377836985' }
        let queryApi = new ReportQuery(dataSource)
        let formData = queryApi.generateFilterFormData().show()
        expect(formData.location).toBeFalsy()
    })

    test('passing datasource as an object with only location property :: returned formdata should not contain metadatafilename-', () => {
        let dataSource = { location: '1463377807724/1463377836985' }
        let queryApi = new ReportQuery(dataSource)
        let formData = queryApi.generateFilterFormData().show()
        expect(formData.metadataFileName).toBeFalsy()
    })

    test('passing dataSource as constructor +', () => {
        expect(typeof formData == 'object' && !Array.isArray(formData) && formData !== null).toBeTruthy()
    });
    test('checking dataSource output +', () => {
        expect(formData.location == dataSource.location && formData.metadataFileName == dataSource.metadataFileName).toBeTruthy()
    });
})

describe("pass select(column) to ReportQuery API", () => {
    let dataSource = {
        "location": "1463377807724/1463377836985",
        "metadataFileName": "e9be6771-995b-40eb-a01c-304857a100a1.metadata"
    }
    let query = new ReportQuery(dataSource)
    test('if col array is present in formdata +', () => {
        let column = 'travel_details.destination'
        let formData = query.select(column).generateFilterFormData().show()
        expect(Array.isArray(formData.columns)).toBeTruthy()
    })
    test('passing column name as string + ', () => {
        let column = 'travel_details.destination'
        let formData = query.select(column).generateFilterFormData().show()
        expect(formData.columns[0]?.column).toBe(column)
    })
    test('passing column name other than string - ', () => {
        let column = null
        let query = new ReportQuery(dataSource)
        let formData = query.select(column).generateFilterFormData().show()
        expect(formData.columns[0]?.column).toBe('') 
    })
})

describe("pass Alias for column to ReportQuery API", () => {
    let dataSource = {
        "location": "1463377807724/1463377836985",
        "metadataFileName": "e9be6771-995b-40eb-a01c-304857a100a1.metadata"
    }
    let column = 'travel_details.destination'
    let aliasName = 'travel_destination'
    let query = new ReportQuery(dataSource)
    test('pass proper alias name with proper column name +', () => {
        let formData = query.select(column).alias(aliasName).generateFilterFormData().show()
        expect(formData?.columns[0]?.alias).toBe(aliasName)
    })
    test('pass empty alias name with proper column name +', () => {
        let query = new ReportQuery(dataSource)
        let formData = query.select(column).alias().generateFilterFormData().show()
        expect(formData?.columns[0]?.alias).toBe(column.split('.').pop())
    })
    test('pass empty alias name with empty column name -', () => {
        let query = new ReportQuery(dataSource)
        let formData = query.select().alias().generateFilterFormData().show()
        expect(formData?.columns.length && formData.columns[0] && formData.columns[0].column == '' && formData.columns[0].alias == '').toBeTruthy()
    })
    test('pass alias name with empty column name -', () => {
        let query = new ReportQuery(dataSource)
        let formData = query.select().alias(aliasName).generateFilterFormData().show()
        expect(formData?.columns.length && formData.columns[0] && formData.columns[0].column == '' && formData.columns[0].alias == aliasName).toBeTruthy()
    })
})

describe("Limit for ReportQuery API", () => {
    let dataSource = {
        "location": "1463377807724/1463377836985",
        "metadataFileName": "e9be6771-995b-40eb-a01c-304857a100a1.metadata"
    }
    let column = 'travel_details.destination'
    let aliasName = 'travel_destination'
    test('check if default limitBy key is present in formdata +', () => {
        let query = new ReportQuery(dataSource).select(column).alias(aliasName)
        let formData = query.generateFilterFormData().show()
        expect('limitBy' in formData).toBeTruthy()
    })
    test('pass limitBy as empty -', () => {
        let query = new ReportQuery(dataSource).select(column).alias(aliasName)
        let formData = query.limit().generateFilterFormData().show()
        expect(formData.limitBy).toBe(50)
    })
    test('pass limitBy as -1 for FULL +', () => {
        let query = new ReportQuery(dataSource).select(column).alias(aliasName)
        let formData = query.limit(-1).generateFilterFormData().show()
        expect(formData.limitBy).toBe('full')
    })
    test('pass limitBy other than number > 0 and "full" -', () => {
        let query = new ReportQuery(dataSource).select(column).alias(aliasName)
        let formData = query.limit('all').generateFilterFormData().show()
        expect(formData.limitBy).toBe(50)
    })
    test('pass limitBy as full + ', () => {
        let query = new ReportQuery(dataSource).select(column).alias(aliasName)
        let formData = query.limit('full').generateFilterFormData().show()
        expect(formData.limitBy).toBe(50)
    })
})

describe("offset for ReportQuery API", () => {
    let dataSource = {
        "location": "1463377807724/1463377836985",
        "metadataFileName": "e9be6771-995b-40eb-a01c-304857a100a1.metadata"
    }
    let column = 'travel_details.destination'
    let aliasName = 'travel_destination'
    let query = new ReportQuery(dataSource).select(column).alias(aliasName)
    test('check if default offset key is present in formdata +', () => {
        let formData = query.generateFilterFormData().show()
        expect('offset' in formData).toBeTruthy()
    })
    test('pass offset as empty -', () => {
        let formData = query.offset().generateFilterFormData().show()
        expect(formData.offset).toBe(0)
    })
    test('pass offset as -1 -', () => {
        let formData = query.offset(-1).generateFilterFormData().show()
        expect(formData.offset).toBe(0)
    })
    test('pass offset string -', () => {
        let formData = query.offset('all').generateFilterFormData().show()
        expect(formData.offset).toBe(0)
    })
    test('pass offset as 21 + ', () => {
        let formData = query.offset(21).generateFilterFormData().show()
        expect(formData.offset).toBe(21)
    })
})