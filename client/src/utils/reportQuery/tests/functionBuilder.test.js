import ReportQuery from '../index'
import 'core-js';
const getQuery = () => new ReportQuery({
    "location": "1639026125991",
    "metadataFileName": "59ced249-8d96-4175-aa88-75a17b200b5c.metadata",
})

/**
 * checing functionBuilder
 * query.functionBuilder((builder) => {builder.sum(coluname) ....}, aliasName) 
 * aliasName is not mandatory in all the cases. there are few cases which are mandatory
 */

describe('testing default FunctionBuilder (without RAW)', () => {
    test('empty arguments (cd, alias) for select builder', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .groupBy(arg2.alias, arg.alias)
            .functionBuilder()
            .reportFormData({
                returnData: true
            })
        expect((() => {
            return typeof result != undefined && typeof result != null
        })()).toBeTruthy()
    })

    test('empty cb and empty alias for select builder', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .groupBy(arg2.alias, arg.alias)
            .functionBuilder(null, null)
            .reportFormData({
                returnData: true
            })
        expect((() => {
            return typeof result != undefined && typeof result != null
        })()).toBeTruthy()
    })

    test('empty cb and valid alias for select builder', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .groupBy(arg.alias, arg2.alias)
            .functionBuilder(null, arg.alias)
            .reportFormData({
                returnData: true
            })
        expect((() => {
            return typeof result != undefined && typeof result != null
        })()).toBeTruthy()
    })

    test('empty cb and valid alias for select builder', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .groupBy(arg.alias, arg2.alias)
            .functionBuilder(null, arg.alias)
            .reportFormData({
                returnData: true
            })
        expect((() => {
            return typeof result != undefined && typeof result != null
        })()).toBeTruthy()
    })

    test('simple sum (aggregate) function', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .functionBuilder((builder) => builder.sum(arg.alias), arg.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 2 &&
                'aggregate' in functions &&
                functions.aggregate.length == 1 &&
                functions.aggregate[0].function.includes('sum') &&
                functions.aggregate[0].column == arg.column &&
                functions.aggregate[0].alias == arg.alias
        })()).toBeTruthy()
    })
    /**
     * .functionBuilder((builder) =>{builder.abs(arg.alias)}, arg.alias)
     */
    test('simple abs (dbfunction) function', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .functionBuilder((builder) => builder.abs(arg.alias), arg.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 2 &&
                'databaseFunction' in columns[0] &&
                !('databaseFunction' in columns[1]) &&
                columns[0].databaseFunction.functionName.includes('abs') &&
                columns[0].databaseFunction.parameters.number == arg.column
        })()).toBeTruthy()
    })

    test('two level aggregate function', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .functionBuilder((builder) => builder.sum(builder.count(arg.alias)))
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 2 &&
                Array.isArray(functions.aggregate) &&
                functions.aggregate[0].alias == arg.alias &&
                functions.aggregate[0].column == arg.column &&
                functions.aggregate[0].function.includes('sum') &&
                functions.aggregate[0].function.includes('count') &&
                functions.aggregate[0].function.indexOf('sum') < functions.aggregate[0].function.indexOf('count')
        })()).toBeTruthy()
    })

    test('theree level aggregate function', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .functionBuilder((builder) => builder.sum(builder.count(builder.min(arg.alias))))
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 2 &&
                Array.isArray(functions.aggregate) &&
                functions.aggregate[0].alias == arg.alias &&
                functions.aggregate[0].column == arg.column &&
                functions.aggregate[0].function.includes('sum') &&
                functions.aggregate[0].function.includes('count') &&
                functions.aggregate[0].function.includes('min') &&
                functions.aggregate[0].function.indexOf('sum') < functions.aggregate[0].function.indexOf('count') &&
                functions.aggregate[0].function.indexOf('count') < functions.aggregate[0].function.indexOf('min')
        })()).toBeTruthy()
    })

    test('four level aggregate function', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .functionBuilder((builder) => builder.sum(builder.count(builder.min(builder.max(arg.alias)))))
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 2 &&
                Array.isArray(functions.aggregate) &&
                functions.aggregate[0].alias == arg.alias &&
                functions.aggregate[0].column == arg.column &&
                functions.aggregate[0].function.includes('sum') &&
                functions.aggregate[0].function.includes('count') &&
                functions.aggregate[0].function.includes('min') &&
                functions.aggregate[0].function.includes('max') &&
                functions.aggregate[0].function.indexOf('sum') < functions.aggregate[0].function.indexOf('count') &&
                functions.aggregate[0].function.indexOf('count') < functions.aggregate[0].function.indexOf('min') &&
                functions.aggregate[0].function.indexOf('min') < functions.aggregate[0].function.indexOf('max')
        })()).toBeTruthy()
    })

    test('applying same agg function twice', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .functionBuilder((builder) => builder.sum(builder.sum(arg.alias)))
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 2 &&
                Array.isArray(functions.aggregate) &&
                functions.aggregate[0].alias == arg.alias &&
                functions.aggregate[0].column == arg.column &&
                functions.aggregate[0].function.split('sum').length - 1 == 1

        })()).toBeTruthy()
    })

    test('applying same agg function thrice', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .functionBuilder((builder) => builder.sum(builder.sum(builder.sum(arg.alias))))
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 2 &&
                Array.isArray(functions.aggregate) &&
                functions.aggregate[0].alias == arg.alias &&
                functions.aggregate[0].column == arg.column &&
                functions.aggregate[0].function.split('sum').length - 1 == 1

        })()).toBeTruthy()
    })

    test('applying same agg function four times', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .functionBuilder((builder) => builder.sum(builder.sum(builder.sum(builder.sum(arg.alias)))))
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 2 &&
                Array.isArray(functions.aggregate) &&
                functions.aggregate[0].alias == arg.alias &&
                functions.aggregate[0].column == arg.column &&
                functions.aggregate[0].function.split('sum').length - 1 == 1

        })()).toBeTruthy()
    })

    test('applying two level nested db function', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .functionBuilder((builder) => builder.abs(builder.sqrt(arg2.alias)), arg.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 2 &&
                columns[0].databaseFunction &&
                columns[0].databaseFunction.functionName.includes('abs') &&
                columns[0].databaseFunction.parameters.number.functionName.includes('sqrt') &&
                columns[0].databaseFunction.parameters.number.parameters.number == arg2.column
        })()).toBeTruthy()
    })

    test('applying three level nested db function', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .functionBuilder((builder) => builder.abs(builder.sqrt(builder.square(arg2.alias))), arg.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 2 &&
                columns[0].column == arg.column &&
                columns[0].databaseFunction &&
                columns[0].databaseFunction.functionName.includes('abs') &&
                columns[0].databaseFunction.parameters.number.functionName.includes('sqrt') &&
                columns[0].databaseFunction.parameters.number.parameters.number.functionName.includes('square') &&
                columns[0].databaseFunction.parameters.number.parameters.number.parameters.number == arg2.column
        })()).toBeTruthy()
    })

    test('applying dbfunction with two arguments', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .functionBuilder((builder) => builder.concat(builder.lower(arg2.alias), builder.upper(arg2.alias)), arg.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        let [col1, col2] = columns
        expect((() => {
            return columns.length == 2 && col1 && col2 &&
                'databaseFunction' in col1 &&
                !('databaseFunction' in col2) &&
                col1.databaseFunction.functionName.includes('concat') &&
                Object.keys(col1.databaseFunction.parameters).length == 2 &&
                col1.databaseFunction.parameters.string1.functionName.includes('lower') &&
                col1.databaseFunction.parameters.string1.parameters.string == arg2.column &&
                col1.databaseFunction.parameters.string2.parameters.string == arg2.column &&
                col1.databaseFunction.parameters.string2.functionName.includes('upper')
        })()).toBeTruthy()
    })

    test('nested db functions with two arguments (concat inside concat)', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .functionBuilder((builder) => builder.concat(builder.lower(arg2.alias), builder.concat(builder.lower(arg2.alias), builder.upper(arg2.alias))), arg.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        let [col1, col2] = columns
        expect((() => {
            return col1 && col2 &&
                'databaseFunction' in col1 &&
                !('databaseFunction' in col2) &&
                col1.databaseFunction.functionName.includes('concat') &&
                col1.databaseFunction.parameters.string1.functionName.includes('lower') &&
                col1.databaseFunction.parameters.string1.parameters.string == arg2.column &&
                col1.databaseFunction.parameters.string2.functionName.includes('concat') &&
                col1.databaseFunction.parameters.string2.parameters.string1.functionName.includes('lower') &&
                col1.databaseFunction.parameters.string2.parameters.string1.parameters.string == arg2.column &&
                col1.databaseFunction.parameters.string2.parameters.string2.functionName.includes('upper') &&
                col1.databaseFunction.parameters.string2.parameters.string2.parameters.string == arg2.column
        })()).toBeTruthy()
    })

    test('nested db functions with two arguments (concat inside concat)', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .functionBuilder((builder) => builder.abs(builder.sum(arg2.alias)), arg.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        let [col1, col2] = columns
        expect((() => {
            return col1 && col2 &&
                'databaseFunction' in col1 &&
                !('databaseFunction' in col2) &&
                col1.databaseFunction.functionName.includes('abs') &&
                col1.databaseFunction.parameters.number == arg2.column &&
                col2.aggregateList[0].includes('sum') &&
                functions.aggregate[0].function.includes('sum') &&
                functions.aggregate[0].column == arg2.column
        })()).toBeTruthy()
    })

    /**
     * testing logical operations
     */

    test('nested if condition', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .functionBuilder((builder) =>
                builder.if(arg.alias, '=', "'Agra'", builder.and(arg.alias, '=', "'Agra'", builder.or(arg.alias, '=', "'Hyderabad'", '')), "'Agra'", "'Not at all Agra'")
                , arg.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        let [col1, col2] = columns
        expect((() => {
            return columns.length == 2 &&
                col1 &&
                col2 &&
                col1.column == arg.column &&
                col1.databaseFunction.functionName.includes('logical.if') &&
                col1.databaseFunction.parameters.condition == '=' &&
                col1.databaseFunction.parameters.value.includes('Agra') &&
                col1.databaseFunction.parameters.moreconditions.functionName.includes('logical.and') &&
                col1.databaseFunction.parameters.moreconditions.parameters.column == arg.column &&
                col1.databaseFunction.parameters.moreconditions.parameters.moreconditions.parameters.column == arg.column &&
                col1.databaseFunction.parameters.moreconditions.parameters.moreconditions.functionName.includes('logical.or')
        })()).toBeTruthy()
    })

    test('case statement', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let output = {
            "location": "1639026125991",
            "metadataFileName": "59ced249-8d96-4175-aa88-75a17b200b5c.metadata",
            "columns": [
                {
                    "column": "HIUSER.travel_details.destination",
                    "alias": "destination",
                    "databaseFunction": {
                        "functionName": "sql.logical.case",
                        "dataType": "text",
                        "parameters": {
                            "condition": {
                                "functionName": "sql.logical.when",
                                "dataType": "numeric",
                                "parameters": {
                                    "column": "HIUSER.travel_details.destination",
                                    "searchcondition": "=",
                                    "value": "'Agra'",
                                    "statement_list": "'Agra'",
                                    "moreconditions": {
                                        "functionName": "sql.logical.else",
                                        "dataType": "text",
                                        "parameters": {
                                            "statement_list": {
                                                "functionName": "sql.logical.when",
                                                "dataType": "numeric",
                                                "parameters": {
                                                    "column": "HIUSER.travel_details.destination",
                                                    "searchcondition": "=",
                                                    "value": "'Hyderabad'",
                                                    "statement_list": "'Hyderabad'",
                                                    "moreconditions": {
                                                        "functionName": "sql.logical.else",
                                                        "dataType": "text",
                                                        "parameters": {
                                                            "statement_list": "'Others'"
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    "floatingType": "discrete"
                },
                {
                    "column": "HIUSER.travel_details.source",
                    "alias": "source",
                    "floatingType": "discrete"
                }
            ],
            "functions": {},
            "limitBy": 50,
            "prependTableNameToAlias": false
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .functionBuilder((builder) =>
                builder.case(builder.when(arg.alias, '=', "'Agra'", "'Agra'", builder.else(
                    builder.when(arg.alias, '=', "'Hyderabad'", "'Hyderabad'", builder.else("'Others'"))
                )))
                , arg.alias)
            .reportFormData({
                returnData: true
            })
        delete result.requestId
        let { columns, functions } = result
        let [col1, col2] = columns
        expect((() => {
            return JSON.stringify(result) === JSON.stringify(output)
        })()).toBeTruthy()
    })

    test('checking CAST', () => {
        // let query = getQuery()
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias).select(arg3.column, arg3.alias)
            .select(arg4.column, arg4.alias)
            .functionBuilder(builder => builder.cast(arg4.alias, 'date'), arg4.alias)
            .where(builder => builder.whereNotIn('travel_date', ['2015-01-04', '2015-01-10'])
                .orWhere('destination', '=', 'Hyderabad')).reportFormData({
                    returnData: true
                })

        let { columns, filters, customFilterExpression } = result
        let [filter1, filter2] = filters
        expect(columns.length == 4 &&
            filters.length == 2 &&
            filter1.operator == 'AND' &&
            filter2.operator == 'OR' &&
            filter1.condition == 'CUSTOM' &&
            filter2.condition == 'EQUALS' &&
            filter1.databaseFunction.functionName.includes('cast') &&
            //     filter1.customCondition.includes('IN') &&
            customFilterExpression.includes('${0}') &&
            customFilterExpression.includes('OR') &&
            customFilterExpression.includes(')') &&
            customFilterExpression.includes('(') &&
            customFilterExpression.includes('${1}')
        ).toBeTruthy()
        //

    })
})

describe('testing Raw FunctionBuilder', () => {
    test('raw function', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let rawInfo = {
            "functionName": "sql.numeric.abs",
            "dataType": "numeric",
            "parameters": {
                "number": {
                    "functionName": "sql.numeric.sqrt",
                    "dataType": "numeric",
                    "parameters": {
                        "number": "destination"
                    }
                }
            }
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .functionBuilder((builder) =>
                builder.raw(rawInfo)
                , arg.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        let [col1, col2] = columns
        expect((() => {
            return columns.length == 2 &&
                col1 && col2 &&
                JSON.stringify(col1.databaseFunction) == JSON.stringify(rawInfo)

        })()).toBeTruthy()
    })


})
