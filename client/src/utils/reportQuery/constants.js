const constants = {
    functions: {
        aggregate: {
            avg: "db.generic.aggregate.avg",
            count: "db.generic.aggregate.count",
            distinct: "db.generic.aggregate.distinct",
            max: "db.generic.aggregate.max",
            min: "db.generic.aggregate.min",
            sum: "db.generic.aggregate.sum",
        },
        db: {
            "DATEADD": {
                "key": "sql.date.dateadd",
                "description": "Returns the specified date with the specified number of interval added to the specified unit of that date.Example:(date({fn timestampadd(SQL_TSI_YEAR, 5, date('2010-09-21'))})) result:2015-09-21 supported units:SQL_TSI_DAY, SQL_TSI_MONTH, SQL_TSI_YEAR.",
                "value": "DATEADD",
                "signature": "(date({fn timestampadd(${unit}, ${value}, date(${date}))}))",
                "returns": "date",
                "parameters": [
                    {
                        "name": "date",
                        "column": true,
                        "defaultValue": "'2014-03-08'"
                    },
                    {
                        "name": "value",
                        "column": true,
                        "defaultValue": "2"
                    },
                    {
                        "name": "unit",
                        "column": true,
                        "defaultValue": "SQL_TSI_YEAR"
                    }
                ],
                "dataTypeCategory": "date"
            },
            "TODAY": {
                "key": "sql.date.today",
                "description": "Displays Current date.",
                "value": "TODAY",
                "signature": "(CURRENT_DATE)",
                "returns": "date",
                "parameters": [],
                "dataTypeCategory": "date"
            },
            "MAKEDATE": {
                "key": "sql.date.makedate",
                "description": "Returns a date for given year, month and day. Example: date(char('2019',4)||'-'||char('11',2)||'-'||char('23',2)) result : 2019-17-23",
                "value": "MAKEDATE",
                "signature": "date(${year}||'-'||${month}||'-'||${day})",
                "returns": "date",
                "parameters": [
                    {
                        "name": "year",
                        "defaultValue": "'2013'"
                    },
                    {
                        "name": "month",
                        "defaultValue": "'7'"
                    },
                    {
                        "name": "day",
                        "defaultValue": "'15'"
                    }
                ],
                "dataTypeCategory": "date"
            },
            "DATEDIFF": {
                "key": "sql.date.datediff",
                "description": "Returns the difference between date1 and date2 expressed in terms of unit. Example: {fn timestampdiff(SQL_TSI_YEAR, date('2018-03-08'), date('2022-03-08'))} result: 4 supported units:SQL_TSI_YEAR,SQL_TSI_MONTH,SQL_TSI_DAY.",
                "value": "DATEDIFF",
                "signature": "({fn timestampdiff(${unit}, date(${date1}), date(${date2}))})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "unit",
                        "column": true,
                        "defaultValue": "SQL_TSI_YEAR"
                    },
                    {
                        "name": "date1",
                        "column": true,
                        "defaultValue": "'2014-03-08'"
                    },
                    {
                        "name": "date2",
                        "column": true,
                        "defaultValue": "'2019-03-08'"
                    }
                ],
                "dataTypeCategory": "date"
            },
            "HOUR": {
                "key": "sql.dateTime.hour",
                "description": "Return hour for timestamp or a valid timestamp string. Example: hour('2014-03-08 12:20:19') result:12",
                "value": "HOUR",
                "signature": "hour(${datetime})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "datetime",
                        "column": true,
                        "defaultValue": "'2014-03-08 12:20:19'"
                    }
                ],
                "dataTypeCategory": "dateTime"
            },
            "MAKETIME": {
                "key": "sql.dateTime.maketime",
                "description": "Returns time value from the hour, minute and seconds.Example:time('11'||':'||'25'||':'||'30') Result:11:25:30\n\t ",
                "value": "MAKETIME",
                "signature": "time(${hour}||':'||${minute}||':'||${second})",
                "returns": "time",
                "parameters": [
                    {
                        "name": "hour",
                        "column": true,
                        "defaultValue": "'12'"
                    },
                    {
                        "name": "minute",
                        "column": true,
                        "defaultValue": "'30'"
                    },
                    {
                        "name": "second",
                        "column": true,
                        "defaultValue": "'40'"
                    }
                ],
                "dataTypeCategory": "dateTime"
            },
            "MAKEDATETIME": {
                "key": "sql.dateTime.makedatetime",
                "description": "Returns a datetime that combines a year,month,day,hour,minute,second. Example: timestamp('2019'||'-'||'11'||'-'||'22')||' '||'10'||':'||'25'||':'||'22.3') result: 2019-11-22 10:25:22.300000.",
                "value": "MAKEDATETIME",
                "signature": "timestamp(${year}||'-'||${month}||'-'||${day}||' '||${hour}||':'||${minute}||':'||${second})",
                "returns": "dateTime",
                "parameters": [
                    {
                        "name": "year",
                        "column": true,
                        "defaultValue": "'2013'"
                    },
                    {
                        "name": "month",
                        "column": true,
                        "defaultValue": "'7'"
                    },
                    {
                        "name": "day",
                        "column": true,
                        "defaultValue": "'15'"
                    },
                    {
                        "name": "hour",
                        "column": true,
                        "defaultValue": "'8'"
                    },
                    {
                        "name": "minute",
                        "column": true,
                        "defaultValue": "'15'"
                    },
                    {
                        "name": "second",
                        "column": true,
                        "defaultValue": "'23.5'"
                    }
                ],
                "dataTypeCategory": "dateTime"
            },
            "DATETIMEDIFF": {
                "key": "sql.dateTime.datetimediff",
                "description": "Returns the difference between timestamp1 and timestamp2 expressed in terms of unit. Example:{fn timestampdiff(SQL_TSI_YEAR, timestamp( '2018-03-08 11:10:27'), timestamp('2022-03-08 11:10:27'))} result: 4. supported units:SQL_TSI_YEAR,SQL_TSI_MONTH,SQL_TSI_DAY,SQL_TSI_HOUR,SQL_TSI_MINUTE,SQL_TSI_SECOND.",
                "value": "DATETIMEDIFF",
                "signature": "{fn timestampdiff(${unit}, timestamp(${datetime1}), timestamp(${datetime2}))}",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "unit",
                        "column": true,
                        "defaultValue": "SQL_TSI_YEAR"
                    },
                    {
                        "name": "datetime1",
                        "column": true,
                        "defaultValue": "'2014-03-08 09:11:20'"
                    },
                    {
                        "name": "datetime2",
                        "column": true,
                        "defaultValue": "'2019-03-08 09:08:40'"
                    }
                ],
                "dataTypeCategory": "dateTime"
            },
            "DATETIMEADD": {
                "key": "sql.dateTime.datetimeadd",
                "description": " Returns the specified datetime with the specified number interval added to the date_part of that datetime. Example:  {fn timestampadd(SQL_TSI_YEAR, 1, timestamp('2010-09-21 10:21:11'))} result:2011-09-21 10:21:11.000000.supported units:SQL_TSI_YEAR,SQL_TSI_MONTH,SQL_TSI_DAY,SQL_TSI_HOUR,SQL_TSI_MINUTE,SQL_TSI_SECOND.",
                "value": "DATETIMEADD",
                "signature": "{fn timestampadd(${unit}, ${value}, timestamp(${datetime}))}",
                "returns": "dateTime",
                "parameters": [
                    {
                        "name": "datetime",
                        "column": true,
                        "defaultValue": "'2014-03-08 11:10:27'"
                    },
                    {
                        "name": "value",
                        "column": true,
                        "defaultValue": "2"
                    },
                    {
                        "name": "unit",
                        "column": true,
                        "defaultValue": "SQL_TSI_YEAR"
                    }
                ],
                "dataTypeCategory": "dateTime"
            },
            "MONTH": {
                "key": "sql.dateTime.month",
                "description": "Returns the month of the year for date/datetime. Example: month('2007-02-03 09:00:00')/month('2007-02-03') result:2",
                "value": "MONTH",
                "signature": "month(${datetime})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "datetime",
                        "column": true,
                        "defaultValue": "'2007-02-03 09:12:30'"
                    }
                ],
                "dataTypeCategory": "dateTime"
            },
            "MINUTE": {
                "key": "sql.dateTime.minute",
                "description": "Returns minute for timestamp or a valid timestamp string. Example: minute('2014-03-08 12:20:19') result: 20",
                "value": "MINUTE",
                "signature": "minute(${datetime})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "datetime",
                        "column": true,
                        "defaultValue": "'2007-02-03 09:12:30'"
                    }
                ],
                "dataTypeCategory": "dateTime"
            },
            "SECOND": {
                "key": "sql.dateTime.second",
                "description": "Returns the seconds for timestamp or a valid timestamp string. Example:second('2014-03-08 12:20:19'). result: 19. NOTE:If the argument is a timestamp: The result will contains fraction of seconds along with second.",
                "value": "SECOND",
                "signature": "second(${datetime})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "datetime",
                        "column": true,
                        "defaultValue": "'2007-02-03 09:12:30'"
                    }
                ],
                "dataTypeCategory": "dateTime"
            },
            "QUARTER": {
                "key": "sql.dateTime.quarter",
                "description": "Returns the quarter of the year for date/datetime. Example: quarter('2014-03-08 12:20:19') result:1",
                "value": "QUARTER",
                "signature": "(CASE MONTH(${datetime}) WHEN < 4 THEN 1 WHEN BETWEEN 4 AND 6 then 2 WHEN BETWEEN 7 AND 9 then 3 WHEN BETWEEN 10 AND 12 then 4 END)",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "datetime",
                        "column": true,
                        "defaultValue": "'2014-03-08 12:20:19'"
                    }
                ],
                "dataTypeCategory": "dateTime"
            },
            "DAY": {
                "key": "sql.dateTime.day",
                "description": "Returns day of the month for date/datetime. Example: day('2014-03-08 09:00:00')/day('2014-03-08') result: 3",
                "value": "DAY",
                "signature": "day(${datetime})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "datetime",
                        "column": true,
                        "defaultValue": "'2007-02-03 09:00:00'"
                    }
                ],
                "dataTypeCategory": "dateTime"
            },
            "MONTHNAME": {
                "key": "sql.dateTime.monthname",
                "description": "Returns the month name based on the given date/datetime. Example: monthname('2014-08-08 08:00:00.000') result: August ",
                "value": "MONTHNAME",
                "signature": "(case when (month(${datetime})=01 OR month(${datetime})=1) then 'January' \n\t     when (month(${datetime})=02 OR month(${datetime})=2) then 'February'\n\t     when (month(${datetime})=03 OR month(${datetime})=3) then 'March'\n\t     when (month(${datetime})=04 OR month(${datetime})=4) then 'April'\n\t     when (month(${datetime})=05 OR month(${datetime})=5) then 'May'\n\t     when (month(${datetime})=06 OR month(${datetime})=6) then 'June'\n\t     when (month(${datetime})=07 OR month(${datetime})=7) then 'July'\n\t     when (month(${datetime})=08 OR month(${datetime})=8) then 'August'\n\t     when (month(${datetime})=09 OR month(${datetime})=9) then 'September'\n\t     when (month(${datetime})=10) then 'October'\n\t     when (month(${datetime})=11) then 'November'\n\t     when (month(${datetime})=12) then 'December'\n\t     else null end)",
                "returns": "text",
                "parameters": [
                    {
                        "name": "datetime",
                        "column": true,
                        "defaultValue": "'2014-08-08 08:00:00.000'"
                    }
                ],
                "dataTypeCategory": "dateTime"
            },
            "NOW": {
                "key": "sql.dateTime.now",
                "description": "Displays Current date and time. This function equivalent to current_timestamp.",
                "value": "NOW",
                "signature": "(CURRENT_TIMESTAMP)",
                "returns": "dateTime",
                "parameters": [],
                "dataTypeCategory": "dateTime"
            },
            "YEAR": {
                "key": "sql.dateTime.year",
                "description": "Return year for date/dateTime. Example: year('2014-03-08 09:00:00')/year('2014-03-08') result: 2014",
                "value": "YEAR",
                "signature": "year(${datetime})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "datetime",
                        "column": true,
                        "defaultValue": "'2007-02-03 09:00:00'"
                    }
                ],
                "dataTypeCategory": "dateTime"
            },
            "CAST": {
                "key": "sql.typeConversion.cast",
                "description": "Cast function converts one dataType to another datatype. Note:All Values should be in single quotes if user provide's value.Example: CAST('2019-03-22 17:34:03.000' AS varchar(23)) result:2019-03-22 17:34:03.0",
                "value": "CAST",
                "signature": "CAST(${column} AS ${dataType})",
                "returns": "text",
                "parameters": [
                    {
                        "name": "column",
                        "column": true
                    },
                    {
                        "name": "dataType",
                        "column": false
                    }
                ],
                "dataTypeCategory": "type conversion"
            },
            "TOCHAR": {
                "key": "sql.typeConversion.tochar",
                "description": "Converts value to char type. NOTE:field should be in single quotes if you are typing manually.Example1:char(date('2019-11-22'))result:2019-11-22 Example2:char(12345) result:'12345'",
                "value": "TOCHAR",
                "signature": "CHAR(${column})",
                "returns": "text",
                "parameters": [
                    {
                        "name": "column",
                        "column": true
                    }
                ],
                "dataTypeCategory": "type conversion"
            },
            "TONUM": {
                "key": "sql.typeConversion.tonum",
                "description": "This function is used to convert character based integer value to integer type.(format is not required)Example:BIGINT('456') result:456",
                "value": "TONUM",
                "signature": "BIGINT(${column})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "column",
                        "column": true
                    }
                ],
                "dataTypeCategory": "type conversion"
            },
            "TODECIMAL": {
                "key": "sql.typeConversion.todecimal",
                "description": "This function is used to convert character based decimal value to decimal type.(format is not required)Example:DOUBLE('456.34') result:456.34",
                "value": "TODECIMAL",
                "signature": "DOUBLE(${column})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "column",
                        "column": true
                    }
                ],
                "dataTypeCategory": "type conversion"
            },
            "TODATE": {
                "key": "sql.typeConversion.todate",
                "description": "This function is used to convert character based date value to date type.(format is not required)Example:CAST('2018-08-30' as DATE)) result:2018-08-30",
                "value": "TODATE",
                "signature": "CAST(${column} AS DATE)",
                "returns": "date",
                "parameters": [
                    {
                        "name": "column",
                        "column": true
                    }
                ],
                "dataTypeCategory": "type conversion"
            },
            "TODATETIME": {
                "key": "sql.typeConversion.todatetime",
                "description": "This function is used to convert character based dateTime value to dateTime type.(format is not required)Example:CAST('2018-08-30 10:15:30' as TIMESTAMP)) result:2018-08-30 10:15:30",
                "value": "TODATETIME",
                "signature": "CAST(${column} AS TIMESTAMP)",
                "returns": "dateTime",
                "parameters": [
                    {
                        "name": "column",
                        "column": true
                    }
                ],
                "dataTypeCategory": "type conversion"
            },
            "TOTIME": {
                "key": "sql.typeConversion.totime",
                "description": "This function is used to convert character based time value to time type.(format is not required) Example:CAST('10:15:30' as TIME)) result:10:15:30",
                "value": "TOTIME",
                "signature": "CAST(${column} AS TIME)",
                "returns": "time",
                "parameters": [
                    {
                        "name": "column",
                        "column": true
                    }
                ],
                "dataTypeCategory": "type conversion"
            },
            "dateToString": {
                "key": "sql.text.dateToString",
                "description": "Converts the date to string",
                "value": "dateToString",
                "signature": "CAST (${column} AS VARCHAR(100))",
                "returns": "text",
                "parameters": [
                    {
                        "name": "column",
                        "column": true
                    }
                ],
                "dataTypeCategory": "derby specific"
            },
            "numericToString": {
                "key": "sql.text.numericToString",
                "description": "Converts the time to string",
                "value": "numericToString",
                "signature": "CAST (${column} AS CHAR(100))",
                "returns": "text",
                "parameters": [
                    {
                        "name": "column",
                        "column": true
                    }
                ],
                "dataTypeCategory": "derby specific"
            },
            "dateTimeToString": {
                "key": "sql.text.dateTimeToString",
                "description": "Converts the datetime to string",
                "value": "dateTimeToString",
                "signature": "CAST (${column} AS VARCHAR(100))",
                "returns": "text",
                "parameters": [
                    {
                        "name": "column",
                        "column": true
                    }
                ],
                "dataTypeCategory": "derby specific"
            },
            "timeToString": {
                "key": "sql.text.timeToString",
                "description": "Converts the time to string",
                "value": "timeToString",
                "signature": "CAST (${column} AS VARCHAR(100))",
                "returns": "text",
                "parameters": [
                    {
                        "name": "column",
                        "column": true
                    }
                ],
                "dataTypeCategory": "derby specific"
            },
            "date": {
                "key": "sql.text.date",
                "description": "Extracts the date from the date and time value",
                "value": "date",
                "signature": "DATE(${column})",
                "returns": "date",
                "parameters": [
                    {
                        "name": "column",
                        "column": true
                    }
                ],
                "dataTypeCategory": "derby specific"
            },
            "month-year": {
                "key": "sql.date.monthyear",
                "description": "Displays month and year in (month-year) format",
                "value": "month-year",
                "signature": "CAST(month(${column}) AS CHAR(20) )||  '-'  ||CAST(YEAR(${column}) AS CHAR(20) )",
                "returns": "text",
                "parameters": [
                    {
                        "name": "column",
                        "column": true,
                        "defaultValue": "0"
                    }
                ],
                "dataTypeCategory": "derby specific"
            },
            "currenttime": {
                "key": "sql.dateTime.currenttime",
                "description": "The CURRENT_TIME function returns the current time.",
                "value": "currenttime",
                "signature": "(VALUES CURRENT_TIME)",
                "returns": "time",
                "parameters": [],
                "dataTypeCategory": "derby specific"
            },
            "ABS": {
                "key": "sql.numeric.abs",
                "description": "Returns the absolute value of a number. Example:abs(-24) result:24",
                "value": "ABS",
                "signature": "abs(${number})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "number",
                        "column": true
                    }
                ],
                "dataTypeCategory": "numeric"
            },
            "ACOS": {
                "key": "sql.numeric.acos",
                "description": "Returns the arc cosine of number. Example: acos(0.25) result: 1.318116071652818 ",
                "value": "ACOS",
                "signature": "acos(${number})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "number",
                        "column": true
                    }
                ],
                "dataTypeCategory": "numeric"
            },
            "ASIN": {
                "key": "sql.numeric.asin",
                "description": "Returns the arc sine of number. Example: asin(0.25) result: 0.25268025514207865 ",
                "value": "ASIN",
                "signature": "asin(${number})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "number",
                        "column": true
                    }
                ],
                "dataTypeCategory": "numeric"
            },
            "ATAN": {
                "key": "sql.numeric.atan",
                "description": "Returns the arc tangent of number. Example: atan(0.25) result: 0.24497866312686414 ",
                "value": "ATAN",
                "signature": "atan(${number})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "number",
                        "column": true
                    }
                ],
                "dataTypeCategory": "numeric"
            },
            "ATAN2": {
                "key": "sql.numeric.atan2",
                "description": "Returns the arc tangent of given number. Example: atan2(0.50,1) result: 0.4636476090008061",
                "value": "ATAN2",
                "signature": "atan2(${number1},${number2})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "number1",
                        "column": true
                    },
                    {
                        "name": "number2",
                        "column": true
                    }
                ],
                "dataTypeCategory": "numeric"
            },
            "FLOOR": {
                "key": "sql.numeric.floor",
                "description": "Returns number rounded down to the nearest number. Example:floor(3.1415) result:3",
                "value": "FLOOR",
                "signature": "floor(${number})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "number",
                        "column": true,
                        "defaultValue": "0"
                    }
                ],
                "dataTypeCategory": "numeric"
            },
            "CEILING": {
                "key": "sql.numeric.ceiling",
                "description": "Returns number rounded up to the nearest integer. Example:ceiling(0.25) result:1",
                "value": "CEILING",
                "signature": "CEILING(${number})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "number",
                        "column": true,
                        "defaultValue": "0"
                    }
                ],
                "dataTypeCategory": "numeric"
            },
            "COS": {
                "key": "sql.numeric.cos",
                "description": "Returns the cosine of number. Example: cos(0.25) result: 0.9689124217106447 ",
                "value": "COS",
                "signature": "cos(${number})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "number",
                        "column": true
                    }
                ],
                "dataTypeCategory": "numeric"
            },
            "COT": {
                "key": "sql.numeric.cot",
                "description": "Returns the cotangent of an angle. Specify the angle in radians. Example: 1/tan(0.25) result: 3.9163173646459399 ",
                "value": "COT",
                "signature": "1/tan(${number})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "number",
                        "column": true
                    }
                ],
                "dataTypeCategory": "numeric"
            },
            "DEGREES": {
                "key": "sql.numeric.degrees",
                "description": "Converts angle number in radians to degrees. Example: degrees(0.25) result: 14.32394487827058",
                "value": "DEGREES",
                "signature": "degrees(${number})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "number",
                        "column": true
                    }
                ],
                "dataTypeCategory": "numeric"
            },
            "DIV": {
                "key": "sql.numeric.div",
                "description": "Returns the integer part of a division operation. Example: (10/5) result: 2",
                "value": "DIV",
                "signature": "(${dividend} / NULLIF(${divisor}, 0))",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "dividend",
                        "column": true
                    },
                    {
                        "name": "divisor",
                        "column": true
                    }
                ],
                "dataTypeCategory": "numeric"
            },
            "EXP": {
                "key": "sql.numeric.exp",
                "description": "Returns Euler’s number raised to the power of the given number. Example: exp(2) result: 7.389",
                "value": "EXP",
                "signature": "exp(${number})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "number",
                        "column": true
                    }
                ],
                "dataTypeCategory": "numeric"
            },
            "LN": {
                "key": "sql.numeric.ln",
                "description": "Returns the natural logarithm of number. Example: ln(2) result: 0.6931471805599453 ",
                "value": "LN",
                "signature": "ln(${number})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "number",
                        "column": true
                    }
                ],
                "dataTypeCategory": "numeric"
            },
            "LOG": {
                "key": "sql.numeric.log",
                "description": "Returns the base 10 logarithm of number. Example: log(2) result: 0.3010299956639812 ",
                "value": "LOG",
                "signature": "log(${number})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "number",
                        "column": true
                    }
                ],
                "dataTypeCategory": "numeric"
            },
            "PI": {
                "key": "sql.numeric.pi",
                "description": "Returns the constant Pi. Example: pi() result: 3.14159 ",
                "value": "PI",
                "signature": "pi()",
                "returns": "numeric",
                "parameters": [],
                "dataTypeCategory": "numeric"
            },
            "RADIANS": {
                "key": "sql.numeric.radians",
                "description": "Converts given number from degrees to radians. Example : radians(4) result : 0.06981317007977318 ",
                "value": "RADIANS",
                "signature": "radians(${number})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "number",
                        "column": true
                    }
                ],
                "dataTypeCategory": "numeric"
            },
            "SIGN": {
                "key": "sql.numeric.sign",
                "description": "Returns the signum function of number, that is:\n\t\t0 if the argument is 0,\n\t\t1 if the argument is greater than 0,\n\t\t-1 if the argument is less than 0. Example: sign(0.5) result: 1.",
                "value": "SIGN",
                "signature": "sign(${number})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "number",
                        "column": true
                    }
                ],
                "dataTypeCategory": "numeric"
            },
            "SIN": {
                "key": "sql.numeric.sin",
                "description": "Returns the sine of number. Example: sin(0.25) result: 0.24740395925452294",
                "value": "SIN",
                "signature": "sin(${number})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "number",
                        "column": true
                    }
                ],
                "dataTypeCategory": "numeric"
            },
            "SQRT": {
                "key": "sql.numeric.sqrt",
                "description": "It displays the square root of a positive number. Example: sqrt(5) result: 2.23606797749979",
                "value": "SQRT",
                "signature": "sqrt(${number})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "number",
                        "column": true
                    }
                ],
                "dataTypeCategory": "numeric"
            },
            "SQUARE": {
                "key": "sql.numeric.square",
                "description": "It displays the square of a given number. Example: square(4) result: 16",
                "value": "SQUARE",
                "signature": "(${number} * ${number})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "number",
                        "column": true
                    }
                ],
                "dataTypeCategory": "numeric"
            },
            "TAN": {
                "key": "sql.numeric.tan",
                "description": "Returns the tangent of number. Example: tan(0.25) result: 0.25534192122103627 ",
                "value": "TAN",
                "signature": "tan(${number})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "number",
                        "column": true
                    }
                ],
                "dataTypeCategory": "numeric"
            },
            "CONCAT": {
                "key": "sql.text.concat",
                "description": "Returns the concatenation of string1, string2. Example:('Beng'||'aluru') result: Bengaluru ",
                "value": "CONCAT",
                "signature": "(${string1}||${string2})",
                "returns": "text",
                "parameters": [
                    {
                        "name": "string1",
                        "column": true,
                        "defaultValue": "'Beng'"
                    },
                    {
                        "name": "string2",
                        "column": true,
                        "defaultValue": "'aluru'"
                    }
                ],
                "dataTypeCategory": "text"
            },
            "CONTAINS": {
                "key": "sql.text.contains",
                "description": "Returns true if the given string contains the specified substring. Example: case when(locate('g', 'Bengaluru'))>0 then true else false end result: true ",
                "value": "CONTAINS",
                "signature": "(case when(locate(${substring}, ${string}))>0 then true else false end)",
                "returns": "boolean",
                "parameters": [
                    {
                        "name": "substring",
                        "column": true
                    },
                    {
                        "name": "string",
                        "column": true
                    }
                ],
                "dataTypeCategory": "text"
            },
            "ENDSWITH": {
                "key": "sql.text.endswith",
                "description": "Returns true if the given string endswith specified substring. Example:case when('postgres' like ('%'||'res')) then true else false end result: true. Note:Please provide single quotes if you are directly typing the substring value.",
                "value": "ENDSWITH",
                "signature": "case when(${string} like ('%'||${substring})) then true else false end",
                "returns": "boolean",
                "parameters": [
                    {
                        "name": "string",
                        "column": true
                    },
                    {
                        "name": "substring",
                        "column": true
                    }
                ],
                "dataTypeCategory": "text"
            },
            "STARTSWITH": {
                "key": "sql.text.startswith",
                "description": "Returns true if string starts with substring. Example: case when('bengaluru' like ('ben'||'%')) then true else false end result: true.  Note:Please provide single quotes if you are directly typing the substring value.",
                "value": "STARTSWITH",
                "signature": "case when(${string} like (${substring}||'%')) then true else false end",
                "returns": "boolean",
                "parameters": [
                    {
                        "name": "string",
                        "column": true
                    },
                    {
                        "name": "substring",
                        "column": true
                    }
                ],
                "dataTypeCategory": "text"
            },
            "FIND": {
                "key": "sql.text.find",
                "description": "Returns the starting position of the first instance of substring in string. Positions start with 1. If not found, 0 is returned. Example :locate('z' in 'Bengaluru') result : 0, locate('aluru' in 'Bengaluru') result : 5",
                "value": "FIND",
                "signature": "locate(${substring},${string})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "substring",
                        "column": true
                    },
                    {
                        "name": "string",
                        "column": true
                    }
                ],
                "dataTypeCategory": "text"
            },
            "LEFT": {
                "key": "sql.text.left",
                "description": "Returns the left most (length) character from the string . Example: substr('bengaluru',1, 4) result: beng",
                "value": "LEFT",
                "signature": "trim(substr(${string},1, ${length}))",
                "returns": "text",
                "parameters": [
                    {
                        "name": "string",
                        "column": true
                    },
                    {
                        "name": "length",
                        "column": true,
                        "defaultValue": "0"
                    }
                ],
                "dataTypeCategory": "text"
            },
            "LENGTH": {
                "key": "sql.text.length",
                "description": "Returns the number of characters in text. Example: length('Bengaluru') result:9",
                "value": "LENGTH",
                "signature": "length(${string})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "string",
                        "column": true
                    }
                ],
                "dataTypeCategory": "text"
            },
            "LOWER": {
                "key": "sql.text.lower",
                "description": "Converts all characters in the specified string to lowercase. Example: LOWER('BENGALURU') result: bengaluru ",
                "value": "LOWER",
                "signature": "lower(${string})",
                "returns": "text",
                "parameters": [
                    {
                        "name": "string",
                        "column": true
                    }
                ],
                "dataTypeCategory": "text"
            },
            "UPPER": {
                "key": "sql.text.upper",
                "description": "Converts all characters in a string to uppercase. Example: UPPER('bengaluru') result: BENGALURU",
                "value": "UPPER",
                "signature": "upper(${string})",
                "returns": "text",
                "parameters": [
                    {
                        "name": "string",
                        "column": true
                    }
                ],
                "dataTypeCategory": "text"
            },
            "RIGHT": {
                "key": "sql.text.right",
                "description": "Returns the rightmost character from the string. If length is negative extract all the characters from the right side except 3 leftmost characters Example: (case when 4 > length('bengaluru') then 'bengaluru' else substr('bengaluru',length('bengaluru')-(4-1),4) end) result: 'luru'.NOTE:if the provided length is grater than the length of the string then the whole string will be returned.",
                "value": "RIGHT",
                "signature": "(case when ${length} > length(${string}) then ${string} else trim(substr(${string},length(${string})-(${length}-1),${length})) end)",
                "returns": "text",
                "parameters": [
                    {
                        "name": "string",
                        "column": true
                    },
                    {
                        "name": "length",
                        "column": true
                    }
                ],
                "dataTypeCategory": "text"
            },
            "LTRIM": {
                "key": "sql.text.ltrim",
                "description": " Removes leading whitespace from string Example: LTRIM(' Bengaluru') result: Bengaluru\n        ",
                "value": "LTRIM",
                "signature": "ltrim(${string})",
                "returns": "text",
                "parameters": [
                    {
                        "name": "string",
                        "column": true
                    }
                ],
                "dataTypeCategory": "text"
            },
            "MID": {
                "key": "sql.text.mid",
                "description": "Returns the text starting from specified position. If position is more than string or length is less than 1 it will return empty string. Example: substr('bengaluru',2,5); result: engal",
                "value": "MID",
                "signature": "trim(substr(${string},${position},${length}))",
                "returns": "text",
                "parameters": [
                    {
                        "name": "string",
                        "column": true
                    },
                    {
                        "name": "position",
                        "column": true
                    },
                    {
                        "name": "length",
                        "column": true
                    }
                ],
                "dataTypeCategory": "text"
            },
            "RTRIM": {
                "key": "sql.text.rtrim",
                "description": "Removes trailing whitespace from string. Example: RTRIM('Bengaluru  ') Result: Bengaluru ",
                "value": "RTRIM",
                "signature": "rtrim(${string})",
                "returns": "text",
                "parameters": [
                    {
                        "name": "string",
                        "column": true
                    }
                ],
                "dataTypeCategory": "text"
            },
            "TRIM": {
                "key": "sql.text.trim",
                "description": "Removes whitespace from string. Example:TRIM(' Bengaluru ') result: Bengaluru\n        ",
                "value": "TRIM",
                "signature": "trim(${string})",
                "returns": "text",
                "parameters": [
                    {
                        "name": "string",
                        "column": true
                    }
                ],
                "dataTypeCategory": "text"
            },
            "AND": {
                "key": "sql.logical.and",
                "description": "Inside IF we will use AND. performs a logical conjunction on two expressions.\n            In 'column' paramter we will 'drag column'.\n\t\t\tIn 'condition' parameter provide conditions like (>,= etc.,like).\n\t\t\tIn 'value' parameter provide condition value.\n\t\t\tIn 'moreconditions' parameter we will use nested 'OR' condition , 'AND' conditions. \n\t\t\tExample: CASE WHEN 'Washington' like '%sh%' \n             AND 'Washington' like 'W%' THEN 'returnl washington' \n             else 'NotMatched' end  ",
                "value": "AND",
                "signature": "AND (${column} ${condition} ${value}) ${moreconditions} ",
                "returns": "text",
                "parameters": [
                    {
                        "name": "column",
                        "column": true
                    },
                    {
                        "name": "condition",
                        "defaultValue": ""
                    },
                    {
                        "name": "value",
                        "defaultValue": ""
                    },
                    {
                        "name": "moreconditions",
                        "column": true,
                        "defaultValue": ""
                    }
                ],
                "dataTypeCategory": "logical"
            },
            "CASE": {
                "key": "sql.logical.case",
                "description": "Inside case we will use when condition. Evaluates each condition from left to right and returns the result when the first condition met. If no condition met return from else if exist, otherwise return null. Example : CASE WHEN Quantity > 30 THEN 'The quantity is greater than 30'  ELSE 'The quantity is under 30' END ",
                "value": "CASE",
                "signature": "(CASE ${condition} END)",
                "returns": "text",
                "parameters": [
                    {
                        "name": "condition",
                        "column": true
                    }
                ],
                "dataTypeCategory": "logical"
            },
            "ELSE": {
                "key": "sql.logical.else",
                "description": "Returns from statement_list when condition gets fail.We will use ELSE inside case function. Example: CASE when 50 > 0 then 'true' else 'false' end",
                "value": "ELSE",
                "signature": "ELSE ${statement_list}",
                "returns": "text",
                "parameters": [
                    {
                        "name": "statement_list",
                        "column": true
                    }
                ],
                "dataTypeCategory": "logical"
            },
            "ELSEIF": {
                "key": "sql.logical.elseif",
                "description": "Evaluates conditions and returns 'conditiontrue' if condition is true, otherwise return 'conditionfalse'.  We will use nested condition inside else 'conditiontrue' parameter. Example:case when creditlim > 50000 then 'PLATINUM' when (creditlim > = 50000) then 'GOLD' else 'SILVER' end ",
                "value": "ELSEIF",
                "signature": "(case when ${column} ${condition} ${value} then ${conditiontrue} when ${elseIfcolumn} ${elseIfcondition} ${elseIfvalue} then ${elseIfconditiontrue} else ${conditionfalse} ${moreconditions} end)",
                "returns": "text",
                "parameters": [
                    {
                        "name": "column",
                        "column": true
                    },
                    {
                        "name": "condition",
                        "defaultValue": ""
                    },
                    {
                        "name": "value",
                        "defaultValue": ""
                    },
                    {
                        "name": "conditiontrue",
                        "column": true
                    },
                    {
                        "name": "elseIfcolumn",
                        "column": true
                    },
                    {
                        "name": "elseIfcondition",
                        "defaultValue": ""
                    },
                    {
                        "name": "elseIfvalue",
                        "defaultValue": ""
                    },
                    {
                        "name": "elseIfconditiontrue",
                        "column": true
                    },
                    {
                        "name": "conditionfalse",
                        "column": true,
                        "defaultValue": ""
                    },
                    {
                        "name": "moreconditions",
                        "column": true,
                        "defaultValue": ""
                    }
                ],
                "dataTypeCategory": "logical"
            },
            "IF": {
                "key": "sql.logical.if",
                "description": "Inside IF we will use AND, OR conditions. Evaluates and returns 'conditiontrue' if condition is true, otherwise return 'conditionfalse'. \n\t\t\tIn 'column' paramter we will drag column.\n\t\t\tIn 'condition' parameter provide conditions like (>,= etc.,).\n\t\t\tIn 'value' parameter provide value.\n\t\t\tIn 'moreconditions' parameter we will use nested 'OR', 'AND' conditions. Instead of dragging column directly we will write expression in column parameter like 50 > 0 (Note : In such case don't provide anything in 'condition' parameter and 'value' parameter). Example : case when (creditlim > 50000) then 'PLATINUM' else 'SILVER' end",
                "value": "IF",
                "signature": "(case when ${column} ${condition} ${value} ${moreconditions} then ${conditiontrue} else ${conditionfalse} end)",
                "returns": "text",
                "parameters": [
                    {
                        "name": "column",
                        "column": true
                    },
                    {
                        "name": "condition",
                        "defaultValue": ""
                    },
                    {
                        "name": "value",
                        "defaultValue": ""
                    },
                    {
                        "name": "moreconditions",
                        "column": true,
                        "defaultValue": ""
                    },
                    {
                        "name": "conditiontrue",
                        "column": true,
                        "defaultValue": ""
                    },
                    {
                        "name": "conditionfalse",
                        "column": true
                    }
                ],
                "dataTypeCategory": "logical"
            },
            "IFNULL": {
                "key": "sql.logical.ifnull",
                "description": "Returns Expr1 if it is not null otherwise return expr2. Example : coalesce(profit, 0).NOTE:Manually entered null will not work it should be part of column, datatype of both the expressions should be match.",
                "value": "IFNULL",
                "signature": "(coalesce(${expr1}, ${expr2}))",
                "returns": "text",
                "parameters": [
                    {
                        "name": "expr1",
                        "column": true
                    },
                    {
                        "name": "expr2",
                        "column": true
                    }
                ],
                "dataTypeCategory": "logical"
            },
            "IIF": {
                "key": "sql.logical.iif",
                "description": "Evaluates and returns 'conditiontrue' if condition is true, otherwise return 'conditionfalse'. Example : case when 'washington'like 'W%' then 'true' else 'false' end",
                "value": "IIF",
                "signature": "(case when ${column} ${condition} ${value} then ${conditiontrue} else ${conditionfalse} end)",
                "returns": "text",
                "parameters": [
                    {
                        "name": "column",
                        "column": true
                    },
                    {
                        "name": "condition",
                        "defaultValue": ""
                    },
                    {
                        "name": "value",
                        "defaultValue": ""
                    },
                    {
                        "name": "conditiontrue",
                        "column": true
                    },
                    {
                        "name": "conditionfalse",
                        "column": true
                    }
                ],
                "dataTypeCategory": "logical"
            },
            "ISNULL": {
                "key": "sql.logical.isnull",
                "description": "Inside WHEN condition we will use ISNULL. Evalutes and returns 'Conditiontrue' if the expression contain Null. Example1 : CASE WHEN 1 ISNULL THEN Conditionfalse. Example2 : CASE WHEN NULL ISNULL THEN Conditiontrue. NOTE: Manually entered null will not work it should be part of column.",
                "value": "ISNULL",
                "signature": "IS NULL",
                "returns": "boolean",
                "parameters": [],
                "dataTypeCategory": "logical"
            },
            "NOT": {
                "key": "sql.logical.not",
                "description": "Evaluates and returns 'conditiontrue' if condition is false, otherwise returns 'conditionfalse'. We will use NOT inside IF. Example :  NOT(500 > 1000) result :true",
                "value": "NOT",
                "signature": "(NOT(${column} ${condition} ${value}))",
                "returns": "boolean",
                "parameters": [
                    {
                        "name": "column",
                        "column": true
                    },
                    {
                        "name": "condition",
                        "column": true,
                        "defaultValue": ""
                    },
                    {
                        "name": "value",
                        "column": true,
                        "defaultValue": ""
                    }
                ],
                "dataTypeCategory": "logical"
            },
            "OR": {
                "key": "sql.logical.or",
                "description": "Inside IF we will use OR.Performs a logical disjunction on two expressions. \n\t\t\tIn 'column' paramter we will drag column.\n\t\t\tIn 'condition' parameter provide conditions like (>,= etc.,).\n\t\t\tIn 'value' parameter provide value.\n\t\t\tIn 'moreconditions' parameter we will use nested 'OR' , 'AND' functions. Example : CASE WHEN 'Washington' like '%sh%' \n             OR  'Washington' like 'W%' THEN 'return washington' \n             else 'NotMatched' end",
                "value": "OR",
                "signature": " OR ${column} ${condition} ${value} ${moreconditions}",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "column",
                        "column": true
                    },
                    {
                        "name": "condition",
                        "column": true,
                        "defaultValue": ""
                    },
                    {
                        "name": "value",
                        "column": true,
                        "defaultValue": ""
                    },
                    {
                        "name": "moreconditions",
                        "column": true,
                        "defaultValue": ""
                    }
                ],
                "dataTypeCategory": "logical"
            },
            "WHEN": {
                "key": "sql.logical.when",
                "description": "Returns 'statement_list' when condition get satisfied .\n\t\t\tIn column paramter we will drag column.\n\t\t\tIn searchcondition parameter provide conditions like (>, =, IS Null etc .,).\n\t\t\tIn value parameter provide value(Note : IS Null used in 'condition' parameter then don't provide anything in 'value' parameter). \n\t\t\tIn moreconditions parameter we will use nested when conditions, Else condition . We will use WHEN inside CASE. Example1 : CASE WHEN 1 > 0  THEN 'one' else 'TWO' end. Example2 : CASE WHEN 'Singapore' IS NULL THEN 'Singa' ELSE 'pore'. Example3 : CASE WHEN Washington like '%sh%' THEN 'return washington' else 'NotMatched' end",
                "value": "WHEN",
                "signature": "WHEN ${column} ${searchcondition} ${value} THEN ${statement_list}  ${moreconditions}",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "column",
                        "column": true
                    },
                    {
                        "name": "searchcondition",
                        "column": true,
                        "defaultValue": ""
                    },
                    {
                        "name": "value",
                        "column": true,
                        "defaultValue": ""
                    },
                    {
                        "name": "statement_list",
                        "column": true
                    },
                    {
                        "name": "moreconditions",
                        "column": true,
                        "defaultValue": ""
                    }
                ],
                "dataTypeCategory": "logical"
            },
            "ZN": {
                "key": "sql.logical.zn",
                "description": "Returns \"expression\" if it is not null, otherwise returns zero.Example :(CASE WHEN '123' IS NULL THEN '0' ELSE '123' end) result :0 ",
                "value": "ZN",
                "signature": "(CASE WHEN ${expr} IS NULL THEN '0' ELSE ${expr} end)",
                "returns": "text",
                "parameters": [
                    {
                        "name": "expr",
                        "column": true
                    }
                ],
                "dataTypeCategory": "logical"
            },
            "ROUND": {
                "key": "sql.numeric.round",
                "description": "Displays a value rounded to a certain number of decimal places. Example: round(34.4158,2) result: 34.42",
                "value": "ROUND",
                "signature": "round(${number}, ${decimalpoint})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "number",
                        "column": true
                    },
                    {
                        "name": "decimalpoint",
                        "defaultValue": "1"
                    }
                ],
                "dataTypeCategory": "numeric"
            }
        }
    }
}
export default constants