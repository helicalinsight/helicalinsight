import { v4 as uuidv4 } from "uuid";

export const databaseFunctions = {
    "date": [
        {
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
            ]
        },
        {
            "key": "sql.date.today",
            "description": "Displays Current date.",
            "value": "TODAY",
            "signature": "(CURRENT_DATE)",
            "returns": "date",
            "parameters": []
        },
        {
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
            ]
        },
        {
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
            ]
        }
    ],
    "dateTime": [
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
            "key": "sql.dateTime.now",
            "description": "Displays Current date and time. This function equivalent to current_timestamp.",
            "value": "NOW",
            "signature": "(CURRENT_TIMESTAMP)",
            "returns": "dateTime",
            "parameters": []
        },
        {
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
            ]
        }
    ],
    "type conversion": [
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        }
    ],
    "derby specific": [
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
            "key": "sql.dateTime.currenttime",
            "description": "The CURRENT_TIME function returns the current time.",
            "value": "currenttime",
            "signature": "(VALUES CURRENT_TIME)",
            "returns": "time",
            "parameters": []
        }
    ],
    "numeric": [
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
            "key": "sql.numeric.pi",
            "description": "Returns the constant Pi. Example: pi() result: 3.14159 ",
            "value": "PI",
            "signature": "pi()",
            "returns": "numeric",
            "parameters": []
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        }
    ],
    "text": [
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
                    "column": true
                }
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        }
    ],
    "logical": [
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
            "key": "sql.logical.isnull",
            "description": "Inside WHEN condition we will use ISNULL. Evalutes and returns 'Conditiontrue' if the expression contain Null. Example1 : CASE WHEN 1 ISNULL THEN Conditionfalse. Example2 : CASE WHEN NULL ISNULL THEN Conditiontrue. NOTE: Manually entered null will not work it should be part of column.",
            "value": "ISNULL",
            "signature": "IS NULL",
            "returns": "boolean",
            "parameters": []
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        },
        {
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
            ]
        }
    ]
}
export const dateFunctions = {
    "dateTime": [
        {
            "label": "Date",
            "part": "date",
            "key": "sql.typeConversion.todate",
            "returns": "date",
            "parameters": [
                {
                    "name": "column"
                }
            ]
        },
        {
            "label": "Time",
            "part": "time",
            "key": "sql.typeConversion.totime",
            "returns": "time",
            "parameters": [
                {
                    "name": "column"
                }
            ]
        },
        {
            "label": "Years",
            "part": "year",
            "key": "sql.dateTime.year",
            "returns": "numeric",
            "parameters": [
                {
                    "name": "datetime"
                }
            ]
        },
        {
            "label": "Quarters",
            "part": "quarter",
            "key": "sql.dateTime.quarter",
            "returns": "numeric",
            "parameters": [
                {
                    "name": "datetime"
                }
            ]
        },
        {
            "label": "Months",
            "part": "month",
            "key": "sql.dateTime.month",
            "returns": "numeric",
            "parameters": [
                {
                    "name": "datetime"
                }
            ]
        },
        {
            "label": "Days",
            "part": "day",
            "key": "sql.dateTime.day",
            "returns": "numeric",
            "parameters": [
                {
                    "name": "datetime"
                }
            ]
        },
        {
            "label": "Hours",
            "part": "hour",
            "key": "sql.dateTime.hour",
            "returns": "numeric",
            "parameters": [
                {
                    "name": "datetime"
                }
            ]
        },
        {
            "label": "Minutes",
            "part": "minute",
            "key": "sql.dateTime.minute",
            "returns": "numeric",
            "parameters": [
                {
                    "name": "datetime"
                }
            ]
        },
        {
            "label": "Seconds",
            "part": "second",
            "key": "sql.dateTime.second",
            "returns": "numeric",
            "parameters": [
                {
                    "name": "datetime"
                }
            ]
        },
        {
            "label": "Individual",
            "part": "individual",
            "key": "individual",
            "parameters": [
                {
                    "name": "datetime"
                }
            ]
        }
    ],
    "date": [
        {
            "label": "Date",
            "part": "date",
            "key": "sql.typeConversion.todate",
            "returns": "date",
            "parameters": [
                {
                    "name": "column"
                }
            ]
        },
        {
            "label": "Years",
            "part": "year",
            "key": "sql.dateTime.year",
            "returns": "numeric",
            "parameters": [
                {
                    "name": "datetime"
                }
            ]
        },
        {
            "label": "Quarters",
            "part": "quarter",
            "key": "sql.dateTime.quarter",
            "returns": "numeric",
            "parameters": [
                {
                    "name": "datetime"
                }
            ]
        },
        {
            "label": "Months",
            "part": "month",
            "key": "sql.dateTime.month",
            "returns": "numeric",
            "parameters": [
                {
                    "name": "datetime"
                }
            ]
        },
        {
            "label": "Days",
            "part": "day",
            "key": "sql.dateTime.day",
            "returns": "numeric",
            "parameters": [
                {
                    "name": "datetime"
                }
            ]
        },
        {
            "label": "Individual",
            "part": "individual",
            "key": "individual",
            "parameters": [
                {
                    "name": "datetime"
                }
            ]
        }
    ],
    "time": [
        {
            "label": "Time",
            "part": "time",
            "key": "sql.typeConversion.totime",
            "returns": "time",
            "parameters": [
                {
                    "name": "column"
                }
            ]
        },
        {
            "label": "Hours",
            "part": "hour",
            "key": "sql.dateTime.hour",
            "returns": "numeric",
            "parameters": [
                {
                    "name": "datetime"
                }
            ]
        },
        {
            "label": "Minutes",
            "part": "minute",
            "key": "sql.dateTime.minute",
            "returns": "numeric",
            "parameters": [
                {
                    "name": "datetime"
                }
            ]
        },
        {
            "label": "Seconds",
            "part": "second",
            "key": "sql.dateTime.second",
            "returns": "numeric",
            "parameters": [
                {
                    "name": "datetime"
                }
            ]
        },
        {
            "label": "Individual",
            "part": "individual",
            "key": "individual",
            "parameters": [
                {
                    "name": "datetime"
                }
            ]
        }
    ]
}


export const getSaveFormData = () => {
    return JSON.parse(`
    {
        "isHrReport": true,
        "columns": [
            {
                "column": "travel_details.booking_platform",
                "label": "booking_platform",
                "id": "ff1a8400-4465-4d36-8055-997ff8ef5b88",
                "type": {
                    "backendDataType": "java.lang.String",
                    "dataType": "text"
                },
                "autogen_alias": "booking_platform",
                "isNormalTable": true,
                "groupBy": [
                    "db.generic.groupBy.group"
                ],
                "orderByColumn": false,
                "showOrderByColumn": false,
                "addedAs": "column",
                "floatingType": "discrete",
                "functionsDefinition": "",
                "applyBeforeAggregate": false,
                "hiddenIncludeInResultSet": false
            },
            {
                "column": "travel_details.mode_of_payment",
                "label": "mode_of_payment",
                "id": "7edb1ea8-e2b3-44e0-b7e0-d4a34305ce02",
                "type": {
                    "backendDataType": "java.lang.String",
                    "dataType": "text"
                },
                "autogen_alias": "mode_of_payment",
                "isNormalTable": true,
                "groupBy": [
                    "db.generic.groupBy.group"
                ],
                "orderByColumn": false,
                "showOrderByColumn": false,
                "addedAs": "row",
                "floatingType": "discrete",
                "functionsDefinition": "",
                "applyBeforeAggregate": false,
                "hiddenIncludeInResultSet": false
            },
            {
                "column": "travel_details.travel_cost",
                "label": "travel_cost",
                "id": "949cf88a-d1bb-4d41-b870-84b565bc7364",
                "type": {
                    "backendDataType": "java.lang.Integer",
                    "dataType": "numeric"
                },
                "autogen_alias": "sum_travel_cost",
                "isNormalTable": true,
                "aggregate": [
                    "db.generic.aggregate.sum"
                ],
                "orderByColumn": false,
                "showOrderByColumn": false,
                "addedAs": "column",
                "floatingType": "continous",
                "functionsDefinition": "",
                "applyBeforeAggregate": false,
                "hiddenIncludeInResultSet": false
            }
        ],
        "state": {
            "fields": [
                {
                    "column": "travel_details.booking_platform",
                    "label": "booking_platform",
                    "id": "ff1a8400-4465-4d36-8055-997ff8ef5b88",
                    "type": {
                        "backendDataType": "java.lang.String",
                        "dataType": "text"
                    },
                    "autogen_alias": "booking_platform",
                    "isNormalTable": true,
                    "groupBy": [
                        "db.generic.groupBy.group"
                    ],
                    "orderByColumn": false,
                    "showOrderByColumn": false,
                    "addedAs": "column",
                    "floatingType": "discrete",
                    "functionsDefinition": "",
                    "applyBeforeAggregate": false,
                    "hiddenIncludeInResultSet": false
                },
                {
                    "column": "travel_details.mode_of_payment",
                    "label": "mode_of_payment",
                    "id": "7edb1ea8-e2b3-44e0-b7e0-d4a34305ce02",
                    "type": {
                        "backendDataType": "java.lang.String",
                        "dataType": "text"
                    },
                    "autogen_alias": "mode_of_payment",
                    "isNormalTable": true,
                    "groupBy": [
                        "db.generic.groupBy.group"
                    ],
                    "orderByColumn": false,
                    "showOrderByColumn": false,
                    "addedAs": "row",
                    "floatingType": "discrete",
                    "functionsDefinition": "",
                    "applyBeforeAggregate": false,
                    "hiddenIncludeInResultSet": false
                },
                {
                    "column": "travel_details.travel_cost",
                    "label": "travel_cost",
                    "id": "949cf88a-d1bb-4d41-b870-84b565bc7364",
                    "type": {
                        "backendDataType": "java.lang.Integer",
                        "dataType": "numeric"
                    },
                    "autogen_alias": "sum_travel_cost",
                    "isNormalTable": true,
                    "aggregate": [
                        "db.generic.aggregate.sum"
                    ],
                    "orderByColumn": false,
                    "showOrderByColumn": false,
                    "addedAs": "column",
                    "floatingType": "continous",
                    "functionsDefinition": "",
                    "applyBeforeAggregate": false,
                    "hiddenIncludeInResultSet": false
                }
            ],
            "filters": [],
            "marksList": [
                {
                    "value": "_all_",
                    "id": "2b7a6a89-884e-4850-81c3-196b43e863c3",
                    "subVizType": "bar",
                    "color": {
                        "fields": []
                    },
                    "size": {
                        "fields": []
                    },
                    "label": {
                        "fields": []
                    },
                    "tooltip": {
                        "fields": []
                    },
                    "shape": {
                        "fields": []
                    },
                    "detail": {
                        "fields": []
                    }
                }
            ],
            "activeMark": "cb2e04e4-b03a-4cbe-8810-6fb667172308",
            "activeTool": "1",
            "scripts": [
                {
                    "id": "hdi-custom-script-2a2f08dc-d253-468e-bd2b-fcd86c5605d3",
                    "value": ""
                }
            ],
            "selectedScript": "hdi-custom-script-2a2f08dc-d253-468e-bd2b-fcd86c5605d3",
            "styles": "",
            "options": {
                "limitBy": 1000,
                "sample": "sample",
                "prependTableNameToAlias": false
            },
            "interactiveMode": true,
            "drillDown": true,
            "drillThrough": true,
            "drillDownList": [],
            "appliedDbfs":  {},
            "currentDrillDown": "",
            "drillThroughList": [],
            "toolbarConfig": {
                "selectable": false
            },
            "selectedType": "Table",
            "customStyles": "",
            "customScripts": [],
            "analytics": [
                {
                    "value": false,
                    "key": "rowSubTotals",
                    "label": "Row Sub Totals"
                },
                {
                    "value": false,
                    "key": "columnSubTotals",
                    "label": "Column Sub Totals"
                },
                {
                    "value": false,
                    "key": "rowGrandTotals",
                    "label": "Row Grand Totals"
                },
                {
                    "value": false,
                    "key": "columnGrandTotals",
                    "label": "Column Grand Totals"
                }
            ],
            "properties": {
                "title": {
                    "show": false,
                    "value": "",
                    "padding": 0,
                    "fontSize": 12,
                    "fontColor": {"a": 0,"b": 0,"g": 0,"r": 0},
                    "alignment": "left",
                    "position": "top"
                },
                "subTitle": {
                    "show": false,
                    "value": "",
                    "padding": 0,
                    "fontSize": 12,
                    "fontColor": {"a": 0,"b": 0,"g": 0,"r": 0},
                    "alignment": "left",
                    "position": "top"
                }
            },
            "showHiddenColumns": false,
            "showHiddenRows": false,
            "database": "HIUSER",
            "version": 1
        },
        "metadata": {
            "location": "1642396928027",
            "metadataFileName": "942a93a5-778f-4cb8-98cf-2a1595eae08d.metadata"
        },
        "classifier": "db.generic",
        "reportName": "New1",
        "location": ""
    }
    `)
}

export const metadata = {
    "metadata": {
        "classifier": "db.generic",
        "name": "HIUSER",
        "dataSource": {
            "sync": false,
            "id": "1",
            "catSchemaPredicted": false,
            "catalog": "",
            "schema": "HIUSER",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc"
        },
        "uniqueId": "Metadata_1",
        "tables": {
            "geo_cordinates": {
                "id": "be534112989b616b194bc59c2fb25a42",
                "alias": "geo_cordinates",
                "columns": {
                    "location_id": {
                        "alias": "location_id",
                        "fullyQualifiedColumn": "geo_cordinates.location_id",
                        "columnId": "3af0625d-b7f1-4179-a7b3-35102a65d95b",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "location": {
                        "alias": "location",
                        "fullyQualifiedColumn": "geo_cordinates.location",
                        "columnId": "02faad34-7182-415a-ab90-08b21464ca8b",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "latitude": {
                        "alias": "latitude",
                        "fullyQualifiedColumn": "geo_cordinates.latitude",
                        "columnId": "f6da0338-623e-4045-800d-31712721c2e9",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Double": "numeric"
                        }
                    },
                    "longitude": {
                        "alias": "longitude",
                        "fullyQualifiedColumn": "geo_cordinates.longitude",
                        "columnId": "0d266e63-2de8-4db0-bed8-23a5d6598bcf",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Double": "numeric"
                        }
                    }
                },
                "name": "geo_cordinates",
                "key": "4cbe74c6-56a7-4867-96af-e173bdf6f733"
            },
            "meeting_details": {
                "id": "9645c648a1c0dbeec1287aaf1e996db3",
                "alias": "meeting_details",
                "columns": {
                    "meeting_id": {
                        "alias": "meeting_id",
                        "fullyQualifiedColumn": "meeting_details.meeting_id",
                        "columnId": "ab5e4d6d-99df-4dfc-9b94-44d7697fd4df",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "meeting_date": {
                        "alias": "meeting_date",
                        "fullyQualifiedColumn": "meeting_details.meeting_date",
                        "columnId": "ddd0d09c-ff0d-4cf3-8d27-be50526a96bd",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Timestamp": "dateTime"
                        }
                    },
                    "meeting_by": {
                        "alias": "meeting_by",
                        "fullyQualifiedColumn": "meeting_details.meeting_by",
                        "columnId": "06dbf5dd-88cc-44c2-826f-72ae2cc20d7f",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "client_name": {
                        "alias": "client_name",
                        "fullyQualifiedColumn": "meeting_details.client_name",
                        "columnId": "6a98c167-92f2-4d5d-b88e-1fb09bb97dd1",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "meeting_purpose": {
                        "alias": "meeting_purpose",
                        "fullyQualifiedColumn": "meeting_details.meeting_purpose",
                        "columnId": "d3e6e457-567c-4487-94b0-2344f1855e19",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "meeting_impact": {
                        "alias": "meeting_impact",
                        "fullyQualifiedColumn": "meeting_details.meeting_impact",
                        "columnId": "6b9aa1ba-3f03-475a-a820-b7530e7c9368",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "meet_cancellation_status": {
                        "alias": "meet_cancellation_status",
                        "fullyQualifiedColumn": "meeting_details.meet_cancellation_status",
                        "columnId": "eb59542a-1cee-4e2d-bb27-cbf76bd45d8f",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "cancellation_reason": {
                        "alias": "cancellation_reason",
                        "fullyQualifiedColumn": "meeting_details.cancellation_reason",
                        "columnId": "30224994-1179-44c7-9119-341ec132e90c",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    }
                },
                "name": "meeting_details",
                "key": "f045cad3-af47-420d-abb4-02abb768975f"
            },
            "employee_details": {
                "id": "4e1fd245f4d13b77be423a43f01d80b2",
                "alias": "employee_details",
                "columns": {
                    "employee_id": {
                        "alias": "employee_id",
                        "fullyQualifiedColumn": "employee_details.employee_id",
                        "columnId": "3162caaa-221e-4903-b64a-87e7e4a66d02",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "employee_name": {
                        "alias": "employee_name",
                        "fullyQualifiedColumn": "employee_details.employee_name",
                        "columnId": "870c5aeb-4e99-4f92-90cd-b965963d6bc6",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "age": {
                        "alias": "age",
                        "fullyQualifiedColumn": "employee_details.age",
                        "columnId": "1efcbe89-8413-4738-ac1d-af439c2e2e26",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "address": {
                        "alias": "address",
                        "fullyQualifiedColumn": "employee_details.address",
                        "columnId": "b4045a33-2768-49e6-874b-1e3f6d911dc0",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    }
                },
                "name": "employee_details",
                "key": "02c1b40b-ad96-4725-afb9-5976cf4f7621"
            },
            "dimdate": {
                "id": "4ac5d9f68b58bd7c0d179146e46795be",
                "alias": "dimdate",
                "columns": {
                    "dim_id": {
                        "alias": "dim_id",
                        "fullyQualifiedColumn": "dimdate.dim_id",
                        "columnId": "e6b3da30-d211-455b-bf30-d58478891011",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "fiscal_year": {
                        "alias": "fiscal_year",
                        "fullyQualifiedColumn": "dimdate.fiscal_year",
                        "columnId": "2e3bb569-d407-4cea-8c28-f446c9402772",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Date": "date"
                        }
                    },
                    "modified_date": {
                        "alias": "modified_date",
                        "fullyQualifiedColumn": "dimdate.modified_date",
                        "columnId": "d0cb56a1-0676-4df5-b773-3e191bbd1e18",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Timestamp": "dateTime"
                        }
                    },
                    "date_key": {
                        "alias": "date_key",
                        "fullyQualifiedColumn": "dimdate.date_key",
                        "columnId": "01c032c3-4f49-4368-876b-7275ff605fe6",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "day_number": {
                        "alias": "day_number",
                        "fullyQualifiedColumn": "dimdate.day_number",
                        "columnId": "97f686c4-d2a0-42cf-9c28-3e78d2bd64a5",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "fiscal_month_name": {
                        "alias": "fiscal_month_name",
                        "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                        "columnId": "4ec0d8a1-ed12-411b-9daa-0159ec78a058",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "fiscal_month_label": {
                        "alias": "fiscal_month_label",
                        "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                        "columnId": "d768e1f8-eb81-41a7-a61a-1bddf3c17e0b",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "created_date": {
                        "alias": "created_date",
                        "fullyQualifiedColumn": "dimdate.created_date",
                        "columnId": "a632a922-4916-4b36-8ea4-619451bb1799",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "created_time": {
                        "alias": "created_time",
                        "fullyQualifiedColumn": "dimdate.created_time",
                        "columnId": "b07b6507-2457-476c-b2ce-41107d8aeecd",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "rating": {
                        "alias": "rating",
                        "fullyQualifiedColumn": "dimdate.rating",
                        "columnId": "a8bfdfde-c98c-45c0-8def-dc7e607ba4b5",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    }
                },
                "name": "dimdate",
                "key": "c5eb4c50-09bd-4ca8-b0c8-da0c0ae021e6"
            },
            "travel_details": {
                "id": "8a28627d07d04ef096d9935f12e0c7e9",
                "alias": "travel_details",
                "columns": {
                    "travel_id": {
                        "alias": "travel_id",
                        "fullyQualifiedColumn": "travel_details.travel_id",
                        "columnId": "34cc6d8f-5f7c-4304-a231-c06d793d2aea",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "travel_date": {
                        "alias": "travel_date",
                        "fullyQualifiedColumn": "travel_details.travel_date",
                        "columnId": "347e737d-115a-4eac-893f-c04597e7d67a",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Timestamp": "dateTime"
                        }
                    },
                    "travel_type": {
                        "alias": "travel_type",
                        "fullyQualifiedColumn": "travel_details.travel_type",
                        "columnId": "caffce30-fcc5-4624-8a35-f174231502d2",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "travel_medium": {
                        "alias": "travel_medium",
                        "fullyQualifiedColumn": "travel_details.travel_medium",
                        "columnId": "98f921ed-8e23-47d5-84b0-6594649901b3",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "source_id": {
                        "alias": "source_id",
                        "fullyQualifiedColumn": "travel_details.source_id",
                        "columnId": "0650e044-f518-42ba-81b3-c16e479fee64",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "source": {
                        "alias": "source",
                        "fullyQualifiedColumn": "travel_details.source",
                        "columnId": "fb11d4ef-bcfc-4afd-908f-8ad1c7025cf1",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "destination_id": {
                        "alias": "destination_id",
                        "fullyQualifiedColumn": "travel_details.destination_id",
                        "columnId": "facc799a-561d-4cc9-a0e9-3b3439d00c4c",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "destination": {
                        "alias": "destination",
                        "fullyQualifiedColumn": "travel_details.destination",
                        "columnId": "7c2bb160-4389-4e18-9809-7129ff291299",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "travel_cost": {
                        "alias": "travel_cost",
                        "fullyQualifiedColumn": "travel_details.travel_cost",
                        "columnId": "aaae45ec-04e3-4192-8e30-5301ffaa6448",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "mode_of_payment": {
                        "alias": "mode_of_payment",
                        "fullyQualifiedColumn": "travel_details.mode_of_payment",
                        "columnId": "c7700d89-0765-4715-a0f3-43cf7fa3fb54",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "booking_platform": {
                        "alias": "booking_platform",
                        "fullyQualifiedColumn": "travel_details.booking_platform",
                        "columnId": "ebc1a0c5-ab3d-407d-85f0-4fed7cb9173d",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "travelled_by": {
                        "alias": "travelled_by",
                        "fullyQualifiedColumn": "travel_details.travelled_by",
                        "columnId": "1cc487d4-727e-447e-bf2d-55187c218ef0",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    }
                },
                "name": "travel_details",
                "key": "299f86b6-6ea0-4c85-bdda-cdcd9bce6ef5"
            }
        },
        "sets": [
            [
                "geo_cordinates",
                "dimdate",
                "travel_details",
                "employee_details",
                "meeting_details"
            ]
        ],
        "metadataName": "Metadata_1",
        "metadataDir": "naresh",
        "formData": {
            "location": "naresh",
            "metadataFileName": "Metadata_1.metadata"
        },
        "uid": "95c30129-d589-436e-ace7-d55ed61729a7"
    },
    "funcs": {
        "databaseFunctions": {
            "date": [
                {
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
                    ]
                },
                {
                    "key": "sql.date.today",
                    "description": "Displays Current date.",
                    "value": "TODAY",
                    "signature": "(CURRENT_DATE)",
                    "returns": "date",
                    "parameters": []
                },
                {
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
                    ]
                },
                {
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
                    ]
                }
            ],
            "dateTime": [
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
                    "key": "sql.dateTime.now",
                    "description": "Displays Current date and time. This function equivalent to current_timestamp.",
                    "value": "NOW",
                    "signature": "(CURRENT_TIMESTAMP)",
                    "returns": "dateTime",
                    "parameters": []
                },
                {
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
                    ]
                }
            ],
            "type conversion": [
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                }
            ],
            "derby specific": [
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
                    "key": "sql.dateTime.currenttime",
                    "description": "The CURRENT_TIME function returns the current time.",
                    "value": "currenttime",
                    "signature": "(VALUES CURRENT_TIME)",
                    "returns": "time",
                    "parameters": []
                }
            ],
            "numeric": [
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
                    "key": "sql.numeric.pi",
                    "description": "Returns the constant Pi. Example: pi() result: 3.14159 ",
                    "value": "PI",
                    "signature": "pi()",
                    "returns": "numeric",
                    "parameters": []
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                }
            ],
            "text": [
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                            "column": true
                        }
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                }
            ],
            "logical": [
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
                    "key": "sql.logical.isnull",
                    "description": "Inside WHEN condition we will use ISNULL. Evalutes and returns 'Conditiontrue' if the expression contain Null. Example1 : CASE WHEN 1 ISNULL THEN Conditionfalse. Example2 : CASE WHEN NULL ISNULL THEN Conditiontrue. NOTE: Manually entered null will not work it should be part of column.",
                    "value": "ISNULL",
                    "signature": "IS NULL",
                    "returns": "boolean",
                    "parameters": []
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                },
                {
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
                    ]
                }
            ]
        },
        "functions": {
            "db.generic.aggregate.avg": "avg",
            "db.generic.aggregate.count": "count",
            "db.generic.aggregate.distinct": "distinct",
            "db.generic.aggregate.max": "max",
            "db.generic.aggregate.min": "min",
            "db.generic.aggregate.sum": "sum",
            "db.generic.groupBy.group": "group by",
            "db.generic.orderBy.order": "order by"
        }
    },
    "dateFunctions": {
        "dateTime": [
            { value: "TODATE", label: "Date", part: "date", key: "sql.typeConversion.todate", "returns": "date", "parameters": [{ name: "column" }] },
            { value: "DAY", label: "Days", part: "day", key: "sql.dateTime.day", "returns": "numeric", "parameters": [{ name: "datetime" }] },
            { value: "MONTH", label: "Months", part: "month", key: "sql.dateTime.month", "returns": "numeric", "parameters": [{ name: "datetime" }] },
            { value: "QUARTER", label: "Quarters", part: "quarter", key: "sql.dateTime.quarter", "returns": "numeric", "parameters": [{ name: "datetime" }] },
            { value: "YEAR", label: "Years", part: "year", key: "sql.dateTime.year", "returns": "numeric", "parameters": [{ name: "datetime" }] },
            { value: "TOTIME", label: "Time", part: "time", key: "sql.typeConversion.totime", "returns": "time", "parameters": [{ name: "column" }] },
            { value: "HOUR", label: "Hours", part: "hour", key: "sql.dateTime.hour", "returns": "numeric", "parameters": [{ name: "datetime" }] },
            { value: "MINUTE", label: "Minutes", part: "minute", key: "sql.dateTime.minute", "returns": "numeric", "parameters": [{ name: "datetime" }] },
            { value: "SECOND", label: "Seconds", part: "second", key: "sql.dateTime.second", "returns": "numeric", "parameters": [{ name: "datetime" }] },
            { value: "INIAVIDUAL", label: "Individual", part: "individual", key: "individual", "parameters": [{ name: "datetime" }] }
        ],
        "date": [
            { value: "TODATE", label: "Date", part: "date", key: "sql.typeConversion.todate", "returns": "date", "parameters": [{ name: "column" }] },
            { value: "DAY", label: "Days", part: "day", key: "sql.dateTime.day", "returns": "numeric", "parameters": [{ name: "datetime" }] },
            { value: "MONTH", label: "Months", part: "month", key: "sql.dateTime.month", "returns": "numeric", "parameters": [{ name: "datetime" }] },
            { value: "QUARTER", label: "Quarters", part: "quarter", key: "sql.dateTime.quarter", "returns": "numeric", "parameters": [{ name: "datetime" }] },
            { value: "YEAR", label: "Years", part: "year", key: "sql.dateTime.year", "returns": "numeric", "parameters": [{ name: "datetime" }] },
            { value: "INIAVIDUAL", label: "Individual", part: "individual", key: "individual", "parameters": [{ name: "datetime" }] }
        ],
        "time": [
            { value: "TOTIME", label: "Time", part: "time", key: "sql.typeConversion.totime", "returns": "time", "parameters": [{ name: "column" }] },
            { value: "HOUR", label: "Hours", part: "hour", key: "sql.dateTime.hour", "returns": "numeric", "parameters": [{ name: "datetime" }] },
            { value: "MINUTE", label: "Minutes", part: "minute", key: "sql.dateTime.minute", "returns": "numeric", "parameters": [{ name: "datetime" }] },
            { value: "SECOND", label: "Seconds", part: "second", key: "sql.dateTime.second", "returns": "numeric", "parameters": [{ name: "datetime" }] },
            { value: "INIAVIDUAL", label: "Individual", part: "individual", key: "individual", "parameters": [{ name: "datetime" }] }
        ]
    }
}

export const getMetadataField = () => {
    return {
        "table": {
            "id": "8a28627d07d04ef096d9935f12e0c7e9",
            "alias": "travel_details",
            "name": "travel_details",
            "key": "4f088f77-4013-4923-84e8-a07398b5fbc3"
        },
        "column": {
            "alias": "destination",
            "fullyQualifiedColumn": "travel_details.destination",
            "columnId": "7c2bb160-4389-4e18-9809-7129ff291299",
            "defaultFunction": "db.generic.groupBy.group",
            "type": {
                "java.lang.String": "text"
            },
            "name": "destination",
            "showOrderByColumn": false,
            "orderByColumn": false,
            "isNormalTable": true
        },
        "type": {
            "backendDataType": "java.lang.String",
            "dataType": "text"
        },
        "defaultFunction": "db.generic.groupBy.group",
        "id": uuidv4(),
        "addedAs": "column",
        "genre": "column"
    }
}
export const getMetadataField_mode_of_payment = () => {
    return {
        "table": {
            "id": "8a28627d07d04ef096d9935f12e0c7e9",
            "alias": "travel_details",
            "name": "travel_details",
            "key": "4f088f77-4013-4923-84e8-a07398b5fbc3"
        },
        "column": {
            "alias": "mode_of_payment",
            "fullyQualifiedColumn": "travel_details.mode_of_payment",
            "columnId": "c7700d89-0765-4715-a0f3-43cf7fa3fb54",
            "defaultFunction": "db.generic.groupBy.group",
            "type": {
                "java.lang.String": "text"
            },
            "name": "mode_of_payment",
            "showOrderByColumn": false,
            "orderByColumn": false,
            "isNormalTable": true
        },
        "type": {
            "backendDataType": "java.lang.String",
            "dataType": "text"
        },
        "defaultFunction": "db.generic.groupBy.group",
        "id": uuidv4(),
        "addedAs": "row",
        "genre": "column"
    }
}
export const getMetadataField_travel_cost = () => {
    return getNumericMetadataField()
}
export const getCanvasField_travel_details_source = () => {
    return {
        "column": "travel_details.source",
        "label": "source",
        "id": "30800415-9682-4b97-a05e-b936a9fd6a78",
        "type": {
            "backendDataType": "java.lang.String",
            "dataType": "text"
        },
        "autogen_alias": "source",
        "isNormalTable": true,
        "groupBy": [
            "db.generic.groupBy.group"
        ],
        "orderByColumn": false,
        "showOrderByColumn": false,
        "addedAs": "column",
        "floatingType": "discrete",
        "functionsDefinition": "ENDSWITH(source,'d')",
        "applyBeforeAggregate": false,
        "hiddenIncludeInResultSet": false,
        "databaseFunction": {
            "key": "sql.text.endswith",
            "description": "Returns true if the given string endswith specified substring. Example:case when('postgres' like ('%'||'res')) then true else false end result: true. Note:Please provide single quotes if you are directly typing the substring value.",
            "value": "ENDSWITH",
            "signature": "case when(${string} like ('%'||${substring})) then true else false end",
            "returns": "boolean",
            "parameters": [
                {
                    "name": "string",
                    "column": true,
                    "value": "travel_details.source"
                },
                {
                    "name": "substring",
                    "column": false,
                    "value": "'d'"
                }
            ]
        }
    }
}
export const getNumericMetadataField = () => {
    return {
        "table": {
            "id": "8a28627d07d04ef096d9935f12e0c7e9",
            "alias": "travel_details",
            "name": "travel_details",
            "key": "4f088f77-4013-4923-84e8-a07398b5fbc3"
        },
        "column": {
            "alias": "travel_cost",
            "fullyQualifiedColumn": "travel_details.travel_cost",
            "columnId": "a3cfffc5-6ed4-4336-8fa3-ffbe0949a675",
            "defaultFunction": "db.generic.aggregate.sum",
            "type": {
                "java.lang.Integer": "numeric"
            },
            "name": "travel_cost",
            "showOrderByColumn": false,
            "orderByColumn": false,
            "isNormalTable": true
        },
        "type": {
            "backendDataType": "java.lang.Integer",
            "dataType": "numeric"
        },
        "defaultFunction": "db.generic.aggregate.sum",
        "id": uuidv4(),
        "addedAs": "column",
        "genre": "column"
    }
}

export const canvasField = {
    "floatingType": "discrete",
    "hiddenIncludeInResultSet": false,
    "functionsDefinition": "TODATETIME(created_date)",
    "addedAs": "column",
    "applyBeforeAggregate": false,
    "autogen_alias": "created_date",
    "databaseFunction": {
        "key": "sql.typeConversion.todatetime",
        "description": "This function is used to convert character based dateTime value to dateTime type.(format is not required)Example:CAST('2018-08-30 10:15:30' as TIMESTAMP)) result:2018-08-30 10:15:30",
        "value": "TODATETIME",
        "signature": "CAST(${column} AS TIMESTAMP)",
        "returns": "dateTime",
        "parameters": [
            {
                "name": "column",
                "value": "dimdate.created_date",
                "column": true
            }
        ]
    },
    "label": "created_date",
    "groupBy": [
        "db.generic.groupBy.group"
    ],
    "column": "dimdate.created_date",
    "type": {
        "backendDataType": "java.lang.String",
        "dataType": "text"
    },
    "id": "993612c2-59de-4d88-9610-a2e252b762a7",
    "orderByColumn": false,
    "isNormalTable": true,
    "showOrderByColumn": false
}


export const formdata_usecase_1 = {
    "location": "1642396928027",
    "metadataFileName": "942a93a5-778f-4cb8-98cf-2a1595eae08d.metadata",
    "columns": [
        {
            "addedAs": undefined,
            "column": "SampleTravelData.travel_details.booking_platform",
            "alias": "booking_platform",
            "fieldDataType": undefined,
            "fieldType": undefined,
            "floatingType": "discrete"
        }
    ],
    "functions": {
        "groupBy": [
            {
                "column": "booking_platform",
                "custom": true
            }
        ]
    },
    "filters": [
        {
            "values": [
                "'Cash')"
            ],
            "operator": "AND",
            "dataType": "java.lang.String",
            "customCondition": " IN (",
            "encloseInQuotes": false,
            "filterGroupId": undefined,
            "isCustomValue": true,
            "label": "mode_of_payment",
            "alias": "mode_of_payment",
            "mode": "auto",
            "column": "SampleTravelData.travel_details.mode_of_payment",
            "id": 0,
            "condition": "CUSTOM"
        }
    ],
    "customFilterExpression": " ${0} ",
    "limitBy": 10,
    "prependTableNameToAlias": false,
    "requestId": "test_1234"
}
export const formdata_usecase_2 = {
    "location": "1642396928027",
    "metadataFileName": "942a93a5-778f-4cb8-98cf-2a1595eae08d.metadata",
    "columns": [
        {
            "addedAs": undefined,
            "column": "SampleTravelData.travel_details.booking_platform",
            "alias": "booking_platform",
            "fieldDataType": undefined,
            "fieldType": undefined,
            "floatingType": "discrete"
        }
    ],
    "functions": {
        "groupBy": [
            {
                "column": "booking_platform",
                "custom": true
            }
        ]
    },
    "filters": [
        {
            "values": [
                "400)",
            ],
            "operator": "AND",
            "dataType": "java.lang.Integer",
            "customCondition": " IN (",
            "encloseInQuotes": false,
            "filterGroupId": undefined,
            "isCustomValue": true,
            "label": "travel_cost",
            "alias": "travel_cost",
            "mode": "auto",
            "column": "SampleTravelData.travel_details.travel_cost",
            "id": 0,
            "condition": "CUSTOM"
        }
    ],
    "customFilterExpression": " ${0} ",
    "limitBy": 10,
    "prependTableNameToAlias": false,
    "requestId": "test_1234"
}
export const formdata_with_filter_custom_values_1 = {
    args: {
        fields: [
            {
                "column": "travel_details.destination",
                "label": "destination",
                "id": "8625f138-cb7f-449e-9ec1-ccc6f5c797de",
                "type": {
                    "backendDataType": "java.lang.String",
                    "dataType": "text"
                },
                "autogen_alias": "destination",
                "isNormalTable": true,
                "groupBy": [
                    "db.generic.groupBy.group"
                ],
                "orderByColumn": false,
                "showOrderByColumn": false,
                "addedAs": "column",
                "floatingType": "discrete",
                "functionsDefinition": "",
                "applyBeforeAggregate": false,
                "hiddenIncludeInResultSet": false
            }
        ],
        filters: [{
            "column": "travel_details.destination",
            "label": "destination",
            "alias": "destination",
            "dataType": "text",
            "backendDataType": "java.lang.String",
            "condition": "STARTS_WITH",
            "values": [
                "G"
            ],
            "valuesMode": "custom",
            "mode": "auto",
            "groupBy": [
                "db.generic.groupBy.group"
            ],
            "orderBy": "",
            "valuesRange": {},
            "rangeValuesType": "",
            "dateTimeToggle": false,
            "rangeSelectionToggole": true,
            "maxInput": "",
            "minInput": "",
            "valuesList": [],
            "drillDownId": "",
            "uid": "23002de7-73dd-4be5-806a-49e3701e27f0",
            "mapping": {
                "isEnabled": true,
                "isDefaultFunction": true,
                "valueDisplayMap": [],
                "valueAliasName": "random",
                "orderBy": {
                    "display": "none",
                    "value": "asc"
                },
                "valueDBFuntionInfo": {},
                "valueColumn": {
                    "alias": "destination",
                    "fullyQualifiedColumn": "travel_details.destination",
                    "columnId": "76ee045f-7968-45e1-82e5-d71b4da24330",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "displayColumn": {
                    "alias": "destination",
                    "fullyQualifiedColumn": "travel_details.destination",
                    "columnId": "76ee045f-7968-45e1-82e5-d71b4da24330",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                }
            },
            "cascade": {
                "isEnabled": false,
                "filterIds": [],
                "filters": [],
                "filtersCount": 0,
                "filtersInfo": {},
                "filtersFormData": {},
                "autoUpdateCascadeInfoFromParent": true
            },
            "active": true,
            "anchor": {
                "anchorDate": "2022-05-10 15:13:15",
                "isAnchor": false,
                "active": 1,
                "relativePart": "",
                "part": "",
                "value": 0,
                "direction": "",
                "lastInput": 3,
                "nextInput": 3
            },
            "encloseInQuotes": false
        }]
    },
    output:
    {
        "location": "1642396928027",
        "metadataFileName": "942a93a5-778f-4cb8-98cf-2a1595eae08d.metadata",
        "columns": [
            {
                "column": "SampleTravelData.travel_details.destination",
                "addedAs": undefined,
                "alias": "destination",
                "fieldDataType": undefined,
                "fieldType": undefined,
                "floatingType": "discrete"
            }
        ],
        "functions": {
            "groupBy": [
                {
                    "column": "destination",
                    "custom": true
                }
            ]
        },
        "filters": [
            {
                "values": [
                    "'G%'",
                ],
                "operator": "AND",
                "dataType": "java.lang.String",
                "customCondition": "like",
                "encloseInQuotes": false,
                "filterGroupId": undefined,
                "column": "SampleTravelData.travel_details.destination",
                "id": 0,
                "mode": "auto",
                "label": "destination",
                "alias": "destination",
                "condition": "CUSTOM"
            }
        ],
        "customFilterExpression": " ${0} ",
        "limitBy": 10,
        "prependTableNameToAlias": false,
        "requestId": "test_1234"
    }
}
export const formdata_with_filter_custom_values_2 = {
    args: {
        fields: [
            {
                "column": "travel_details.destination",
                "label": "destination",
                "id": "8625f138-cb7f-449e-9ec1-ccc6f5c797de",
                "type": {
                    "backendDataType": "java.lang.String",
                    "dataType": "text"
                },
                "autogen_alias": "destination",
                "isNormalTable": true,
                "groupBy": [
                    "db.generic.groupBy.group"
                ],
                "orderByColumn": false,
                "showOrderByColumn": false,
                "addedAs": "column",
                "floatingType": "discrete",
                "functionsDefinition": "",
                "applyBeforeAggregate": false,
                "hiddenIncludeInResultSet": false
            }
        ],
        filters: [{
            "column": "travel_details.destination",
            "label": "destination",
            "dataType": "text",
            "backendDataType": "java.lang.String",
            "condition": "ENDS_WITH",
            "values": [
                "n"
            ],
            "valuesMode": "custom",
            "mode": "auto",
            "groupBy": [
                "db.generic.groupBy.group"
            ],
            "orderBy": "",
            "valuesRange": {},
            "rangeValuesType": "",
            "dateTimeToggle": false,
            "rangeSelectionToggole": true,
            "maxInput": "",
            "minInput": "",
            "valuesList": [],
            "drillDownId": "",
            "uid": "23002de7-73dd-4be5-806a-49e3701e27f0",
            "mapping": {
                "isEnabled": true,
                "isDefaultFunction": true,
                "valueDisplayMap": [],
                "valueAliasName": "random",
                "orderBy": {
                    "display": "none",
                    "value": "asc"
                },
                "valueDBFuntionInfo": {},
                "valueColumn": {
                    "alias": "destination",
                    "fullyQualifiedColumn": "travel_details.destination",
                    "columnId": "76ee045f-7968-45e1-82e5-d71b4da24330",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "displayColumn": {
                    "alias": "destination",
                    "fullyQualifiedColumn": "travel_details.destination",
                    "columnId": "76ee045f-7968-45e1-82e5-d71b4da24330",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                }
            },
            "cascade": {
                "isEnabled": false,
                "filterIds": [],
                "filters": [],
                "filtersCount": 0,
                "filtersInfo": {},
                "filtersFormData": {},
                "autoUpdateCascadeInfoFromParent": true
            },
            "active": true,
            "anchor": {
                "anchorDate": "2022-05-10 15:13:15",
                "isAnchor": false,
                "active": 1,
                "relativePart": "",
                "part": "",
                "value": 0,
                "direction": "",
                "lastInput": 3,
                "nextInput": 3
            },
            "encloseInQuotes": false
        }]
    },
    output: {
        "location": "1642396928027",
        "metadataFileName": "942a93a5-778f-4cb8-98cf-2a1595eae08d.metadata",
        "columns": [
            {
                "column": "SampleTravelData.travel_details.destination",
                "addedAs": undefined,
                "alias": "destination",
                "fieldDataType": undefined,
                "fieldType": undefined,
                "floatingType": "discrete"
            }
        ],
        "functions": {
            "groupBy": [
                {
                    "column": "destination",
                    "custom": true
                }
            ]
        },
        "filters": [
            {
                "values": [
                    "'%n'",
                ],
                "operator": "AND",
                "dataType": "java.lang.String",
                "customCondition": "like",
                "encloseInQuotes": false,
                "label": "destination",
                "alias": "destination",
                "mode": "auto",
                "filterGroupId": undefined,
                "column": "SampleTravelData.travel_details.destination",
                "id": 0,
                "condition": "CUSTOM"
            }
        ],
        "customFilterExpression": " ${0} ",
        "limitBy": 10,
        "prependTableNameToAlias": false,
        "requestId": "test_1234"
    }
}

export const grid_chart_config_with_dateTime = {
    args: {
        "data": [
            {
                "travel_date": "2015-01-01 16:59:00.0",
                "sum_travel_cost": 8000
            },
            {
                "travel_date": "2015-01-02 13:50:00.0",
                "sum_travel_cost": 1500
            },
            {
                "travel_date": "2015-01-02 14:05:00.0",
                "sum_travel_cost": 3500
            },
            {
                "travel_date": "2015-01-02 14:10:00.0",
                "sum_travel_cost": 56000
            },
            {
                "travel_date": "2015-01-02 14:36:00.0",
                "sum_travel_cost": 1200
            }
        ],
        "metadata": [
            {
                "1": {
                    "name": "travel_date",
                    "type": "dateTime"
                },
                "2": {
                    "name": "sum_travel_cost",
                    "type": "numeric"
                }
            },
            {
                "rows": 5
            }
        ],
        "fields": [
            {
                "column": "travel_details.travel_date",
                "label": "travel_date",
                "id": "55970bd8-455e-4c83-89a3-38f43376232b",
                "type": {
                    "backendDataType": "java.sql.Timestamp",
                    "dataType": "dateTime"
                },
                "autogen_alias": "travel_date",
                "isNormalTable": true,
                "groupBy": [
                    "db.generic.groupBy.group"
                ],
                "orderByColumn": false,
                "showOrderByColumn": false,
                "addedAs": "column",
                "floatingType": "discrete",
                "functionsDefinition": "",
                "applyBeforeAggregate": false,
                "hiddenIncludeInResultSet": false
            },
            {
                "column": "travel_details.travel_cost",
                "label": "travel_cost",
                "id": "236a6f5b-2a03-4e30-86b2-2a1666f8692a",
                "type": {
                    "backendDataType": "java.lang.Integer",
                    "dataType": "numeric"
                },
                "autogen_alias": "sum_travel_cost",
                "isNormalTable": true,
                "aggregate": [
                    "db.generic.aggregate.sum"
                ],
                "orderByColumn": false,
                "showOrderByColumn": false,
                "addedAs": "row",
                "floatingType": "continous",
                "functionsDefinition": "",
                "applyBeforeAggregate": false,
                "hiddenIncludeInResultSet": false
            }
        ],
        "marksList": [
            {
                "value": "_all_",
                "id": "2f27e9e0-5aa5-420a-bb42-c58a9c3992f6",
                "subVizType": "",
                "color": {
                    "fields": []
                },
                "size": {
                    "fields": []
                },
                "label": {
                    "fields": []
                },
                "tooltip": {
                    "fields": []
                },
                "shape": {
                    "fields": []
                },
                "detail": {
                    "fields": []
                }
            },
            {
                "value": "sum_travel_cost",
                "id": "236a6f5b-2a03-4e30-86b2-2a1666f8692a",
                "subVizType": "",
                "color": {
                    "fields": []
                },
                "size": {
                    "fields": []
                },
                "label": {
                    "fields": []
                },
                "tooltip": {
                    "fields": []
                },
                "shape": {
                    "fields": []
                },
                "detail": {
                    "fields": []
                }
            }
        ]
    },
    output: {
        "rows": [
            "sum_travel_cost"
        ],
        "columns": [
            "travel_date"
        ],
        "schema": [
            {
                "format": "%Y-%m-%d %H:%M:%S",
                "name": "travel_date",
                "type": "dimension",
                "subtype": "temporal",
                "funcName": "dateTime"
            },
            {
                "name": "sum_travel_cost",
                "type": "measure",
                "funcName": null,
                "defAggFn": "sum",
            }
        ],
        "chartData": [
            {
                "travel_date": "2015-01-01 16:59:00.0",
                "sum_travel_cost": 8000
            },
            {
                "travel_date": "2015-01-02 13:50:00.0",
                "sum_travel_cost": 1500
            },
            {
                "travel_date": "2015-01-02 14:05:00.0",
                "sum_travel_cost": 3500
            },
            {
                "travel_date": "2015-01-02 14:10:00.0",
                "sum_travel_cost": 56000
            },
            {
                "travel_date": "2015-01-02 14:36:00.0",
                "sum_travel_cost": 1200
            }
        ],
        "colorField": undefined,
        "colorStep": undefined,
        "detailField": undefined,
        "labelField": undefined,
        "shapeField": undefined,
        "sizeField": undefined,
        "tooltipField": undefined,
        "measuresLabelFields": {}
    }
}
export const grid_chart_config_with_date = {
    args: {
        "data": [
            {
                "travel_date": "2015-01-01",
                "sum_travel_cost": 8000
            },
            {
                "travel_date": "2015-01-02",
                "sum_travel_cost": 69900
            },
            {
                "travel_date": "2015-01-04",
                "sum_travel_cost": 10000
            },
            {
                "travel_date": "2015-01-06",
                "sum_travel_cost": 83800
            },
            {
                "travel_date": "2015-01-09",
                "sum_travel_cost": 3845
            }
        ],
        "metadata": [
            {
                "1": {
                    "name": "travel_date",
                    "type": "date"
                },
                "2": {
                    "name": "sum_travel_cost",
                    "type": "numeric"
                }
            },
            {
                "rows": 5
            }
        ],
        "fields": [
            {
                "floatingType": "discrete",
                "hiddenIncludeInResultSet": false,
                "functionsDefinition": "TODATE(travel_date)",
                "addedAs": "column",
                "applyBeforeAggregate": false,
                "autogen_alias": "travel_date",
                "databaseFunction": {
                    "key": "sql.typeConversion.todate",
                    "description": "This function is used to convert character based date value to date type.(format is not required)Example:CAST('2018-08-30' as DATE)) result:2018-08-30",
                    "value": "TODATE",
                    "signature": "CAST(${column} AS DATE)",
                    "returns": "date",
                    "parameters": [
                        {
                            "name": "column",
                            "value": "travel_details.travel_date",
                            "column": true
                        }
                    ]
                },
                "label": "travel_date",
                "groupBy": [
                    "db.generic.groupBy.group"
                ],
                "column": "travel_details.travel_date",
                "type": {
                    "backendDataType": "java.sql.Timestamp",
                    "dataType": "dateTime"
                },
                "id": "55970bd8-455e-4c83-89a3-38f43376232b",
                "orderByColumn": false,
                "isNormalTable": true,
                "showOrderByColumn": false
            },
            {
                "column": "travel_details.travel_cost",
                "label": "travel_cost",
                "id": "236a6f5b-2a03-4e30-86b2-2a1666f8692a",
                "type": {
                    "backendDataType": "java.lang.Integer",
                    "dataType": "numeric"
                },
                "autogen_alias": "sum_travel_cost",
                "isNormalTable": true,
                "aggregate": [
                    "db.generic.aggregate.sum"
                ],
                "orderByColumn": false,
                "showOrderByColumn": false,
                "addedAs": "row",
                "floatingType": "continous",
                "functionsDefinition": "",
                "applyBeforeAggregate": false,
                "hiddenIncludeInResultSet": false
            }
        ],
        "marksList": [
            {
                "value": "_all_",
                "id": "2f27e9e0-5aa5-420a-bb42-c58a9c3992f6",
                "subVizType": "",
                "color": {
                    "fields": []
                },
                "size": {
                    "fields": []
                },
                "label": {
                    "fields": []
                },
                "tooltip": {
                    "fields": []
                },
                "shape": {
                    "fields": []
                },
                "detail": {
                    "fields": []
                }
            },
            {
                "value": "sum_travel_cost",
                "id": "236a6f5b-2a03-4e30-86b2-2a1666f8692a",
                "subVizType": "",
                "color": {
                    "fields": []
                },
                "size": {
                    "fields": []
                },
                "label": {
                    "fields": []
                },
                "tooltip": {
                    "fields": []
                },
                "shape": {
                    "fields": []
                }
            }
        ]
    },
    output: {
        "rows": [
            "sum_travel_cost"
        ],
        "columns": [
            "travel_date"
        ],
        "shapeField": undefined,
        "tooltipField": undefined,
        "sizeField": undefined,
        "colorField": undefined,
        "colorStep": undefined,
        "detailField": undefined,
        "labelField": undefined,
        "measuresLabelFields": {},
        "schema": [
            {
                "format": "%Y-%m-%d",
                "name": "travel_date",
                "type": "dimension",
                "subtype": "temporal",
                "funcName": "TODATE"
            },
            {
                "name": "sum_travel_cost",
                "type": "measure",
                "funcName": null,
                "defAggFn": "sum",
            }
        ],
        "chartData": [
            {
                "travel_date": "2015-01-01",
                "sum_travel_cost": 8000
            },
            {
                "travel_date": "2015-01-02",
                "sum_travel_cost": 69900
            },
            {
                "travel_date": "2015-01-04",
                "sum_travel_cost": 10000
            },
            {
                "travel_date": "2015-01-06",
                "sum_travel_cost": 83800
            },
            {
                "travel_date": "2015-01-09",
                "sum_travel_cost": 3845
            }
        ]
    }
}
export const grid_chart_config_with_time = {
    args: {
        "data": [
            {
                "travel_date": "06:57:00",
                "sum_travel_cost": 5800
            },
            {
                "travel_date": "06:59:00",
                "sum_travel_cost": 14300
            },
            {
                "travel_date": "07:01:00",
                "sum_travel_cost": 6728
            },
            {
                "travel_date": "07:02:00",
                "sum_travel_cost": 31800
            },
            {
                "travel_date": "07:03:00",
                "sum_travel_cost": 91045
            }
        ],
        "metadata": [
            {
                "1": {
                    "name": "travel_date",
                    "type": "time"
                },
                "2": {
                    "name": "sum_travel_cost",
                    "type": "numeric"
                }
            },
            {
                "rows": 5
            }
        ],
        "fields": [
            {
                "floatingType": "discrete",
                "hiddenIncludeInResultSet": false,
                "functionsDefinition": "TOTIME(travel_date)",
                "addedAs": "column",
                "applyBeforeAggregate": false,
                "autogen_alias": "travel_date",
                "databaseFunction": {
                    "key": "sql.typeConversion.totime",
                    "description": "This function is used to convert character based time value to time type.(format is not required) Example:CAST('10:15:30' as TIME)) result:10:15:30",
                    "value": "TOTIME",
                    "signature": "CAST(${column} AS TIME)",
                    "returns": "time",
                    "parameters": [
                        {
                            "name": "column",
                            "value": "travel_details.travel_date",
                            "column": true
                        }
                    ]
                },
                "label": "travel_date",
                "groupBy": [
                    "db.generic.groupBy.group"
                ],
                "column": "travel_details.travel_date",
                "type": {
                    "backendDataType": "java.sql.Timestamp",
                    "dataType": "dateTime"
                },
                "id": "55970bd8-455e-4c83-89a3-38f43376232b",
                "orderByColumn": false,
                "isNormalTable": true,
                "showOrderByColumn": false
            },
            {
                "column": "travel_details.travel_cost",
                "label": "travel_cost",
                "id": "236a6f5b-2a03-4e30-86b2-2a1666f8692a",
                "type": {
                    "backendDataType": "java.lang.Integer",
                    "dataType": "numeric"
                },
                "autogen_alias": "sum_travel_cost",
                "isNormalTable": true,
                "aggregate": [
                    "db.generic.aggregate.sum"
                ],
                "orderByColumn": false,
                "showOrderByColumn": false,
                "addedAs": "row",
                "floatingType": "continous",
                "functionsDefinition": "",
                "applyBeforeAggregate": false,
                "hiddenIncludeInResultSet": false
            }
        ],
        "marksList": [
            {
                "value": "_all_",
                "id": "2f27e9e0-5aa5-420a-bb42-c58a9c3992f6",
                "subVizType": "",
                "color": {
                    "fields": []
                },
                "size": {
                    "fields": []
                },
                "label": {
                    "fields": []
                },
                "tooltip": {
                    "fields": []
                },
                "shape": {
                    "fields": []
                },
                "detail": {
                    "fields": []
                }
            },
            {
                "value": "sum_travel_cost",
                "id": "236a6f5b-2a03-4e30-86b2-2a1666f8692a",
                "subVizType": "",
                "color": {
                    "fields": []
                },
                "size": {
                    "fields": []
                },
                "label": {
                    "fields": []
                },
                "tooltip": {
                    "fields": []
                },
                "shape": {
                    "fields": []
                }
            }
        ]
    },
    output: {
        "rows": [
            "sum_travel_cost"
        ],
        "columns": [
            "travel_date"
        ],
        "schema": [
            {
                "name": "travel_date",
                "type": "dimension",
                "funcName": null
            },
            {
                "name": "sum_travel_cost",
                "type": "measure",
                "funcName": null,
                "defAggFn": "sum",
            }
        ],
        "chartData": [
            {
                "travel_date": "06:57:00",
                "sum_travel_cost": 5800
            },
            {
                "travel_date": "06:59:00",
                "sum_travel_cost": 14300
            },
            {
                "travel_date": "07:01:00",
                "sum_travel_cost": 6728
            },
            {
                "travel_date": "07:02:00",
                "sum_travel_cost": 31800
            },
            {
                "travel_date": "07:03:00",
                "sum_travel_cost": 91045
            }
        ],
        "colorField": undefined,
        "colorStep": undefined,
        "detailField": undefined,
        "labelField": undefined,
        "shapeField": undefined,
        "sizeField": undefined,
        "tooltipField": undefined,
        "measuresLabelFields": {}
    }
}

export const formdata_with_filter_range_conditions1 = {
    args: {
        fields: [
            {
                "column": "travel_details.booking_platform",
                "label": "booking_platform",
                "id": "ad84c984-8cc0-42b3-a932-96c336769041",
                "type": {
                    "backendDataType": "java.lang.String",
                    "dataType": "text"
                },
                "autogen_alias": "booking_platform",
                "isNormalTable": true,
                "groupBy": [],
                "orderByColumn": false,
                "showOrderByColumn": false,
                "addedAs": "column",
                "floatingType": "discrete",
                "functionsDefinition": "",
                "applyBeforeAggregate": false,
                "hiddenIncludeInResultSet": false
            },
            {
                "column": "travel_details.travel_cost",
                "label": "travel_cost",
                "id": "428fee70-e7e8-411b-a281-4ca65da21bd3",
                "type": {
                    "backendDataType": "java.lang.Integer",
                    "dataType": "numeric"
                },
                "autogen_alias": "travel_cost",
                "isNormalTable": true,
                "aggregate": [],
                "orderByColumn": false,
                "showOrderByColumn": false,
                "addedAs": "column",
                "floatingType": "continous",
                "functionsDefinition": "",
                "applyBeforeAggregate": false,
                "hiddenIncludeInResultSet": false
            }
        ],
        filters: [
            {
                "column": "travel_details.travel_cost",
                "filterGroupId": undefined,
                "label": "travel_cost",
                "dataType": "numeric",
                "backendDataType": "java.lang.Integer",
                "condition": "NOT_IN_RANGE",
                "values": [
                    29185,
                    100000
                ],
                "valuesMode": "custom",
                "mode": "auto",
                "orderBy": "",
                "valuesRange": {
                    "min": 300,
                    "max": 100000
                },
                "rangeValuesType": "",
                "dateTimeToggle": false,
                "rangeSelectionToggole": true,
                "maxInput": "",
                "minInput": "",
                "valuesList": [],
                "drillDownId": "",
                "uid": "e4990fbd-93f0-4231-ac5a-aec7fb0e29a9",
                "aggregate": [],
                "mapping": {
                    "isEnabled": true,
                    "isDefaultFunction": true,
                    "valueDisplayMap": [],
                    "valueAliasName": "random",
                    "orderBy": {
                        "display": "none",
                        "value": "asc"
                    },
                    "valueDBFuntionInfo": {},
                    "valueColumn": {
                        "alias": "travel_cost",
                        "fullyQualifiedColumn": "travel_details.travel_cost",
                        "columnId": "4b796e35-ca1e-4a40-9d1f-ca99b195d2e9",
                        "defaultFunction": "none",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "displayColumn": {
                        "alias": "travel_cost",
                        "fullyQualifiedColumn": "travel_details.travel_cost",
                        "columnId": "4b796e35-ca1e-4a40-9d1f-ca99b195d2e9",
                        "defaultFunction": "none",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    }
                },
                "cascade": {
                    "isEnabled": false,
                    "filterIds": [],
                    "filters": [],
                    "filtersCount": 0,
                    "filtersInfo": {},
                    "filtersFormData": {},
                    "autoUpdateCascadeInfoFromParent": true
                },
                "active": true,
                "anchor": {
                    "anchorDate": "2022-05-19 13:27:55",
                    "isAnchor": false,
                    "active": 1,
                    "relativePart": "",
                    "part": "",
                    "value": 0,
                    "direction": "",
                    "lastInput": 3,
                    "nextInput": 3
                },
                "encloseInQuotes": false
            }
        ]
    },
    output: {
        "location": "1642396928027",
        "metadataFileName": "942a93a5-778f-4cb8-98cf-2a1595eae08d.metadata",
        "columns": [
            {
                "addedAs": undefined,
                "alias": "booking_platform",
                "column": "SampleTravelData.travel_details.booking_platform",
                "fieldDataType": undefined,
                "fieldType": undefined,
                "floatingType": "discrete",
            },
            {
                "addedAs": undefined,
                "column": "SampleTravelData.travel_details.travel_cost",
                "fieldDataType": undefined,
                "fieldType": undefined,
                "alias": "travel_cost",
                "floatingType": "discrete"
            }
        ],
        "functions": {},
        "filters": [
            {
                "column": "SampleTravelData.travel_details.travel_cost",
                "label": "travel_cost",
                "alias": "travel_cost",
                "mode": "auto",
                "filterGroupId": undefined,
                "condition": "NOT_IN_RANGE",
                "values": [
                    29185,
                    100000
                ],
                "operator": "AND",
                "dataType": "java.lang.Integer",
                "id": 0,
                "encloseInQuotes": false
            }
        ],
        "limitBy": 10,
        "prependTableNameToAlias": false,
        "requestId": "test_1234",
        "customFilterExpression": " ${0} "
    }
}
export const formdata_with_filter_range_conditions2 = {
    args: {
        fields: [
            {
                "column": "travel_details.booking_platform",
                "label": "booking_platform",
                "id": "ad84c984-8cc0-42b3-a932-96c336769041",
                "type": {
                    "backendDataType": "java.lang.String",
                    "dataType": "text"
                },
                "autogen_alias": "booking_platform",
                "isNormalTable": true,
                "groupBy": [],
                "orderByColumn": false,
                "showOrderByColumn": false,
                "addedAs": "column",
                "floatingType": "discrete",
                "functionsDefinition": "",
                "applyBeforeAggregate": false,
                "hiddenIncludeInResultSet": false
            },
            {
                "column": "travel_details.travel_date",
                "label": "travel_date",
                "id": "538effb8-25f6-4b32-a3f5-27cb228513a9",
                "type": {
                    "backendDataType": "java.sql.Timestamp",
                    "dataType": "dateTime"
                },
                "autogen_alias": "travel_date",
                "isNormalTable": true,
                "groupBy": [],
                "orderByColumn": false,
                "showOrderByColumn": false,
                "addedAs": "column",
                "floatingType": "discrete",
                "functionsDefinition": "",
                "applyBeforeAggregate": false,
                "hiddenIncludeInResultSet": false
            }
        ],
        filters: [
            {
                "column": "travel_details.travel_date",
                "label": "travel_date",
                "databaseFunction": {
                    "key": "sql.dateTime.year",
                    "description": "Return year for date/dateTime. Example: year('2014-03-08 09:00:00')/year('2014-03-08') result: 2014",
                    "value": "YEAR",
                    "signature": "year(${datetime})",
                    "returns": "numeric",
                    "parameters": [
                        {
                            "name": "datetime",
                            "column": true,
                            "defaultValue": "'2007-02-03 09:00:00'",
                            "value": "travel_details.travel_date"
                        }
                    ]
                },
                "dataType": "dateTime",
                "backendDataType": "java.sql.Timestamp",
                "condition": "NOT_IN_RANGE",
                "values": [
                    2010,
                    2023
                ],
                "valuesMode": "custom",
                "mode": "auto",
                "groupBy": [],
                "orderBy": "",
                "valuesRange": {},
                "rangeValuesType": "",
                "dateTimeToggle": false,
                "rangeSelectionToggole": true,
                "maxInput": "",
                "minInput": "",
                "valuesList": [],
                "drillDownId": "",
                "uid": "d9deabb7-76c9-4581-b36f-623f924ad58e",
                "datePart": "year",
                "currentDate": "Thu May 19 2022 14:42:27 GMT+0530 (India Standard Time)",
                "dateValuesType": "datePicker",
                "showRelativeList": false,
                "anchor": {
                    "anchorDate": "2022-05-19 14:42:35",
                    "isAnchor": false,
                    "active": 1,
                    "relativePart": "",
                    "part": "",
                    "value": 0,
                    "direction": "",
                    "lastInput": 3,
                    "nextInput": 3
                },
                "mapping": {
                    "isEnabled": true,
                    "valueDBFunction": {
                        "key": "sql.dateTime.year",
                        "description": "Return year for date/dateTime. Example: year('2014-03-08 09:00:00')/year('2014-03-08') result: 2014",
                        "value": "YEAR",
                        "signature": "year(${datetime})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "datetime",
                                "column": true,
                                "defaultValue": "'2007-02-03 09:00:00'",
                                "value": "travel_details.travel_date"
                            }
                        ]
                    },
                    "DisplayDBFunction": {
                        "key": "sql.dateTime.year",
                        "description": "Return year for date/dateTime. Example: year('2014-03-08 09:00:00')/year('2014-03-08') result: 2014",
                        "value": "YEAR",
                        "signature": "year(${datetime})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "datetime",
                                "column": true,
                                "defaultValue": "'2007-02-03 09:00:00'",
                                "value": "travel_details.travel_date"
                            }
                        ]
                    },
                    "isDefaultFunction": true,
                    "valueDisplayMap": [],
                    "valueAliasName": "random",
                    "orderBy": {
                        "display": "none",
                        "value": "asc"
                    },
                    "valueDBFuntionInfo": {},
                    "valueColumn": {
                        "alias": "travel_date",
                        "fullyQualifiedColumn": "travel_details.travel_date",
                        "columnId": "975ecc7c-003b-4864-adf1-99c2c4989f6f",
                        "defaultFunction": "none",
                        "type": {
                            "java.sql.Timestamp": "dateTime"
                        }
                    },
                    "displayColumn": {
                        "alias": "travel_date",
                        "fullyQualifiedColumn": "travel_details.travel_date",
                        "columnId": "975ecc7c-003b-4864-adf1-99c2c4989f6f",
                        "defaultFunction": "none",
                        "type": {
                            "java.sql.Timestamp": "dateTime"
                        }
                    }
                },
                "cascade": {
                    "isEnabled": false,
                    "filterIds": [],
                    "filters": [],
                    "filtersCount": 0,
                    "filtersInfo": {},
                    "filtersFormData": {},
                    "autoUpdateCascadeInfoFromParent": true
                },
                "active": true,
                "encloseInQuotes": false
            }
        ]
    },
    output: {
        "location": "1642396928027",
        "metadataFileName": "942a93a5-778f-4cb8-98cf-2a1595eae08d.metadata",
        "columns": [
            {
                "addedAs": undefined,
                "alias": "booking_platform",
                "column": "SampleTravelData.travel_details.booking_platform",
                "fieldDataType": undefined,
                "fieldType": undefined,
                "floatingType": "discrete",
            },
            {
                "addedAs": undefined,
                "column": "SampleTravelData.travel_details.travel_date",
                "fieldDataType": undefined,
                "fieldType": undefined,
                "alias": "travel_date",
                "floatingType": "discrete"
            }
        ],
        "functions": {},
        "filters": [
            {
                "column": "SampleTravelData.travel_details.travel_date",
                "filterGroupId": undefined,
                "alias": "travel_date",
                "condition": "NOT_IN_RANGE",
                "values": [
                    2010,
                    2023
                ],
                "operator": "AND",
                "mode": "auto",
                "dataType": "java.lang.Integer",
                "label": "travel_date",
                "databaseFunction": {
                    "dataType": "numeric",
                    "functionName": "sql.dateTime.year",
                    "parameters": {
                        "datetime": "travel_details.travel_date",
                    },
                },
                "id": 0,
                "encloseInQuotes": false
            }
        ],
        "customFilterExpression": " ${0} ",
        "limitBy": 10,
        "prependTableNameToAlias": false,
        "requestId": "test_1234"
    }
}

export const intailStateForTabs = {
    "activeReportId": "359320f9-ea4b-4e72-a2ae-c8c28d08ce6c",
    "reports": [
        {
            "id": "359320f9-ea4b-4e72-a2ae-c8c28d08ce6c",
            "mode": "create",
            "active": true,
            "metadata": null,
            "functions": {},
            "databaseFunctions": {},
            "fields": [],
            "filters": [],
            "defaultValueDisplayMap": {},
            "editingField": null,
            "marksList": [],
            "activeMark": "30a4a847-a5aa-4c72-ad67-8c678873cf36",
            "activeTool": "1",
            "scripts": [],
            "selectedScript": "hdi-custom-script-bc68e025-39b4-416c-b0c9-f2e78c81045f",
            "styles": "",
            "sqlString": "",
            "options": {
                "limitBy": 1000,
                "sample": "sample",
                "prependTableNameToAlias": false
            },
            "interactiveMode": false,
            "drillDown": false,
            "drillThrough": false,
            "drillDownList": [],
            "currentDrillDown": "",
            "drillThroughList": [],
            "toolbarConfig": {
                "selectable": false
            },
            "selectedType": "",
            "reportData": {},
            "customStyles": "",
            "customScripts": [],
            "analytics": [],
            "properties": {},
            "reportInfo": {
                "location": "",
                "uuid": "",
                "reportName": "New1"
            },
            "cellMenuData": null,
            "showHiddenColumns": false,
            "showHiddenRows": false
        }
    ],
    "layout": {
        "metadataShelf": false,
        "toolsAreaShelf": false,
        "fieldsAreaShelf": false
    },
    geoJsonData: {}
}


export const formdata_with_month_year_db_function = {
    args: {
        fields: [
            {
                "column": "travel_details.travel_date",
                "label": "travel_date",
                "id": "d1793560-2474-4b1a-9629-543874da737f",
                "type": {
                    "backendDataType": "java.sql.Timestamp",
                    "dataType": "dateTime"
                },
                "autogen_alias": "travel_date",
                "isNormalTable": true,
                "groupBy": [
                    "db.generic.groupBy.group"
                ],
                "orderByColumn": false,
                "showOrderByColumn": false,
                "addedAs": "column",
                "floatingType": "discrete",
                "functionsDefinition": "month-year(travel_date)",
                "applyBeforeAggregate": false,
                "hiddenIncludeInResultSet": false,
                "databaseFunction": {
                    "key": "sql.date.monthyear",
                    "description": "Displays month and year in (month-year) format",
                    "value": "month-year",
                    "signature": "CAST(month(${column}) AS CHAR(20) )||  '-'  ||CAST(YEAR(${column}) AS CHAR(20) )",
                    "returns": "text",
                    "parameters": [
                        {
                            "name": "column",
                            "column": true,
                            "defaultValue": "0",
                            "value": "travel_details.travel_date"
                        }
                    ]
                }
            }
        ]
    },
    output:
    {
        "location": "1642396928027",
        "metadataFileName": "942a93a5-778f-4cb8-98cf-2a1595eae08d.metadata",
        "columns": [
            {
                "column": "SampleTravelData.travel_details.travel_date",
                "addedAs": undefined,
                "alias": "travel_date",
                "fieldDataType": undefined,
                "fieldType": undefined,
                "floatingType": "discrete",
                "databaseFunction": {
                    "dataType": "text",
                    "functionName": "sql.date.monthyear",
                    "parameters": {
                        "column": "travel_details.travel_date",
                    },
                }
            }
        ],
        "functions": {
            "groupBy": [
                {
                    "column": "travel_date",
                    "custom": true
                }
            ]
        },
        "limitBy": 10,
        "prependTableNameToAlias": false,
        "requestId": "test_1234"
    }
}
export const formdata_with_db_function_custom_values = {
    args: {
        fields: [
            {
                "column": "travel_details.travel_date",
                "label": "travel_date",
                "id": "d1793560-2474-4b1a-9629-543874da737f",
                "type": {
                    "backendDataType": "java.sql.Timestamp",
                    "dataType": "dateTime"
                },
                "autogen_alias": "travel_date",
                "isNormalTable": true,
                "groupBy": [
                    "db.generic.groupBy.group"
                ],
                "orderByColumn": false,
                "showOrderByColumn": false,
                "addedAs": "column",
                "floatingType": "discrete",
                "functionsDefinition": "month-year(travel_date)",
                "applyBeforeAggregate": false,
                "hiddenIncludeInResultSet": false,
                "databaseFunction": {
                    "key": "sql.text.concat",
                    "description": "Returns the concatenation of string1, string2. Example:('Beng'||'aluru') result: Bengaluru ",
                    "value": "CONCAT",
                    "signature": "(${string1}||${string2})",
                    "returns": "text",
                    "parameters": [
                        {
                            "name": "string1",
                            "column": false,
                            "defaultValue": "'Beng'",
                            "value": "'a'"
                        },
                        {
                            "name": "string2",
                            "column": false,
                            "defaultValue": "'aluru'",
                            "value": "'b'"
                        }
                    ]
                }
            }
        ]
    },
    output:
    {
        "location": "1642396928027",
        "metadataFileName": "942a93a5-778f-4cb8-98cf-2a1595eae08d.metadata",
        "columns": [
            {
                "column": "SampleTravelData.travel_details.travel_date",
                "addedAs": undefined,
                "alias": "travel_date",
                "fieldDataType": undefined,
                "fieldType": undefined,
                "floatingType": "discrete",
                "databaseFunction": {
                    "dataType": "text",
                    "functionName": "sql.text.concat",
                    "parameters": {
                        "string1": "'a'",
                        "string2": "'b'",
                    },
                }
            }
        ],
        "functions": {
            "groupBy": [
                {
                    "column": "travel_date",
                    "custom": true
                }
            ]
        },
        "limitBy": 10,
        "prependTableNameToAlias": false,
        "requestId": "test_1234"
    }
}
export const formdata_with_numeric_column_aggreate = {
    args: {
        fields: [
            {
                "column": "travel_details.travel_cost",
                "label": "sum_travel_cost",
                "id": "cfc108fe-dd73-4b78-9707-e7ed8fe5115e",
                "type": {
                    "backendDataType": "java.lang.Integer",
                    "dataType": "numeric"
                },
                "autogen_alias": "sum_travel_cost",
                "isNormalTable": true,
                "aggregate": [
                    "db.generic.aggregate.sum"
                ],
                "orderByColumn": false,
                "showOrderByColumn": false,
                "addedAs": "column",
                "floatingType": "continous",
                "functionsDefinition": "ABS(sum_travel_cost)",
                "applyBeforeAggregate": true,
                "hiddenIncludeInResultSet": false,
                "databaseFunction": {
                    "key": "sql.numeric.abs",
                    "description": "Returns the absolute value of a number. Example:abs(-24) result:24",
                    "value": "ABS",
                    "signature": "abs(${number})",
                    "returns": "numeric",
                    "parameters": [
                        {
                            "name": "number",
                            "column": true,
                            "value": "travel_details.travel_cost"
                        }
                    ]
                }
            }
        ]
    },
    output:
    {
        "location": "1642396928027",
        "metadataFileName": "942a93a5-778f-4cb8-98cf-2a1595eae08d.metadata",
        "columns": [
            {
                "column": "SampleTravelData.travel_details.travel_cost",
                "addedAs": undefined,
                "aggregate": true,
                "aggregateList": [
                    "db.generic.aggregate.sum"
                ],
                "alias": "sum_travel_cost",
                "fieldDataType": undefined,
                "fieldType": undefined,
                "floatingType": "discrete",
                "databaseFunction": {
                    "dataType": "numeric",
                    "functionName": "sql.numeric.abs",
                    "parameters": {
                        "number": "travel_details.travel_cost",
                    },
                }
            }
        ],
        "functions": {
            "aggregate": [
                {
                    "alias": "sum_travel_cost",
                    "applyBeforeAggregate": true,
                    "column": "SampleTravelData.travel_details.travel_cost",
                    "function": "db.generic.aggregate.sum",
                }
            ]
        },
        "limitBy": 10,
        "prependTableNameToAlias": false,
        "requestId": "test_1234"
    }
}

export const stateDataWithNestedDBFunctions = {
    state: {
        "metadataLoading": false,
        "hreportLoading": false,
        "fields": [
            {
                "column": "travel_details.booking_platform",
                "columnID": "65327",
                "label": "booking_platform",
                "id": "55b5b8d7-33c3-4976-a4e7-30db3436fd4b",
                "type": {
                    "backendDataType": "java.lang.String",
                    "dataType": "text"
                },
                "autogen_alias": "booking_platform",
                "isNormalTable": true,
                "tableAlias": "travel_details",
                "groupBy": [
                    "db.generic.groupBy.group"
                ],
                "orderByColumn": false,
                "showOrderByColumn": false,
                "addedAs": "column",
                "floatingType": "",
                "functionsDefinition": "LOWER(UPPER(booking_platform))",
                "applyBeforeAggregate": false,
                "hiddenIncludeInResultSet": false,
                "metaDataAlias": "booking_platform",
                "databaseName": "HIUSER",
                "geographicType": "",
                "isView": false,
                "databaseFunction": {
                    "key": "sql.text.lower",
                    "description": "Converts all characters in the specified string to lowercase. Example: LOWER('BENGALURU') result: bengaluru ",
                    "value": "LOWER",
                    "signature": "lower(${string})",
                    "returns": "text",
                    "parameters": [
                        {
                            "name": "string",
                            "column": false,
                            "value": {
                                "key": "sql.text.upper",
                                "description": "Converts all characters in a string to uppercase. Example: UPPER('bengaluru') result: BENGALURU",
                                "value": "UPPER",
                                "signature": "upper(${string})",
                                "returns": "text",
                                "parameters": [
                                    {
                                        "name": "string",
                                        "column": true,
                                        "value": "HIUSER.travel_details.booking_platform"
                                    }
                                ]
                            }
                        }
                    ]
                }
            }
        ],
        "filters": [],
        "marksList": [
            {
                "value": "_all_",
                "id": "0e772e12-eb1c-4a8f-b945-c2989c03fdc0",
                "subVizType": "",
                "color": {
                    "fields": []
                },
                "size": {
                    "fields": []
                },
                "label": {
                    "fields": []
                },
                "tooltip": {
                    "fields": []
                },
                "shape": {
                    "fields": []
                },
                "detail": {
                    "fields": []
                }
            }
        ],
        "activeMark": "0e772e12-eb1c-4a8f-b945-c2989c03fdc0",
        "activeTool": "1",
        "scripts": [
            {
                "id": "pre-execution",
                "value": "",
                "title": "Pre Execution"
            },
            {
                "id": "pre-fetch",
                "value": "",
                "title": "Pre Fetch"
            },
            {
                "id": "post-fetch",
                "value": "",
                "title": "Post Fetch"
            },
            {
                "id": "post-execution",
                "value": "",
                "title": "Post Execution"
            }
        ],
        "selectedScript": "pre-execution",
        "styles": "",
        "stylesId": "hi-report-d184fdf4",
        "savedStyles": "",
        "options": {
            "limitBy": 1000,
            "sample": "sample",
            "prependTableNameToAlias": false
        },
        "interactiveMode": false,
        "drillDown": false,
        "drillThrough": false,
        "drillDownList": [],
        "currentDrillDown": "",
        "drillThroughList": [],
        "toolbarConfig": {
            "selectable": false
        },
        "selectedType": "Table",
        "customStyles": "",
        "customScripts": [],
        "analytics": [
            {
                "value": false,
                "key": "subTotals",
                "label": "Row Sub Totals"
            }
        ],
        "properties": {},
        "showHiddenColumns": false,
        "showHiddenRows": false,
        "geoJsonData": {},
        "database": "HIUSER",
        "activeFunc": false
    },
    filters: []
}

export const stateDataWithFilterDbFunctions = {
    state: {
        "metadataLoading": false,
        "hreportLoading": false,
        "fields": [
            {
                "column": "travel_details.booking_platform",
                "columnID": "65327",
                "label": "booking_platform",
                "id": "55b5b8d7-33c3-4976-a4e7-30db3436fd4b",
                "type": {
                    "backendDataType": "java.lang.String",
                    "dataType": "text"
                },
                "autogen_alias": "booking_platform",
                "isNormalTable": true,
                "tableAlias": "travel_details",
                "groupBy": [
                    "db.generic.groupBy.group"
                ],
                "orderByColumn": false,
                "showOrderByColumn": false,
                "addedAs": "column",
                "floatingType": "",
                "functionsDefinition": "LOWER(UPPER(booking_platform))",
                "applyBeforeAggregate": false,
                "hiddenIncludeInResultSet": false,
                "metaDataAlias": "booking_platform",
                "databaseName": "HIUSER",
                "geographicType": "",
                "isView": false,
                "databaseFunction": {
                    "key": "sql.text.lower",
                    "description": "Converts all characters in the specified string to lowercase. Example: LOWER('BENGALURU') result: bengaluru ",
                    "value": "LOWER",
                    "signature": "lower(${string})",
                    "returns": "text",
                    "parameters": [
                        {
                            "name": "string",
                            "column": false,
                            "value": {
                                "key": "sql.text.upper",
                                "description": "Converts all characters in a string to uppercase. Example: UPPER('bengaluru') result: BENGALURU",
                                "value": "UPPER",
                                "signature": "upper(${string})",
                                "returns": "text",
                                "parameters": [
                                    {
                                        "name": "string",
                                        "column": true,
                                        "value": "HIUSER.travel_details.booking_platform"
                                    }
                                ]
                            }
                        }
                    ]
                }
            }
        ],
        "filters": [
            {
                "column": "employee_details.employee_id",
                "label": "employee_name",
                "dataType": "numeric",
                "backendDataType": "java.lang.Integer",
                "condition": "IS_ONE_OF",
                "values": [
                    37
                ],
                "valuesMode": "custom",
                "mode": "auto",
                "groupBy": [
                    "db.generic.groupBy.group"
                ],
                "orderBy": "",
                "valuesRange": {},
                "rangeValuesType": "",
                "dateTimeToggle": false,
                "rangeSelectionToggole": true,
                "maxInput": "",
                "minInput": "",
                "valuesList": [],
                "drillDownId": "",
                "uid": "24d569db-bccd-4aef-81ea-3d62d6b28a7f",
                "configId": "33170a40-400b-45ca-8993-aeb502f4629c",
                "dataId": "227db340-af33-4188-b450-13a9bee938df",
                "columnID": "129872",
                "aggregate": [],
                "mapping": {
                    "isEnabled": true,
                    "unique": true,
                    "valueDBFunction": {
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
                    },
                    "DisplayDBFunction": {
                        "key": "sql.text.upper",
                        "description": "Converts all characters in a string to uppercase. Example: UPPER('bengaluru') result: BENGALURU",
                        "value": "UPPER",
                        "signature": "upper(${string})",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "string",
                                "column": true,
                                "value": "HIUSER.employee_details.employee_name"
                            }
                        ]
                    },
                    "isDefaultFunction": true,
                    "valueDisplayMap": [],
                    "valueAliasName": "random",
                    "orderBy": {
                        "display": "asc",
                        "value": "none"
                    },
                    "valueColumn": {
                        "alias": "employee_id",
                        "fullyQualifiedColumn": "employee_details.employee_id",
                        "id": "129871",
                        "type": {
                            "java.lang.Integer": "numeric"
                        },
                        "defaultFunction": "db.generic.groupBy.group"
                    },
                    "displayColumn": {
                        "alias": "employee_name",
                        "fullyQualifiedColumn": "employee_details.employee_name",
                        "id": "129872",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    }
                },
                "cascade": {
                    "isEnabled": false,
                    "filters": [],
                    "filtersCount": 0
                },
                "active": false,
                "loading": false,
                "search": ""
            }
        ],
        "marksList": [
            {
                "value": "_all_",
                "id": "0e772e12-eb1c-4a8f-b945-c2989c03fdc0",
                "subVizType": "",
                "color": {
                    "fields": []
                },
                "size": {
                    "fields": []
                },
                "label": {
                    "fields": []
                },
                "tooltip": {
                    "fields": []
                },
                "shape": {
                    "fields": []
                },
                "detail": {
                    "fields": []
                }
            }
        ],
        "activeMark": "0e772e12-eb1c-4a8f-b945-c2989c03fdc0",
        "activeTool": "1",
        "scripts": [
            {
                "id": "pre-execution",
                "value": "",
                "title": "Pre Execution"
            },
            {
                "id": "pre-fetch",
                "value": "",
                "title": "Pre Fetch"
            },
            {
                "id": "post-fetch",
                "value": "",
                "title": "Post Fetch"
            },
            {
                "id": "post-execution",
                "value": "",
                "title": "Post Execution"
            }
        ],
        "selectedScript": "pre-execution",
        "styles": "",
        "stylesId": "hi-report-d184fdf4",
        "savedStyles": "",
        "options": {
            "limitBy": 1000,
            "sample": "sample",
            "prependTableNameToAlias": false
        },
        "interactiveMode": false,
        "drillDown": false,
        "drillThrough": false,
        "drillDownList": [],
        "currentDrillDown": "",
        "drillThroughList": [],
        "toolbarConfig": {
            "selectable": false
        },
        "selectedType": "Table",
        "customStyles": "",
        "customScripts": [],
        "analytics": [
            {
                "value": false,
                "key": "subTotals",
                "label": "Row Sub Totals"
            }
        ],
        "properties": {},
        "showHiddenColumns": false,
        "showHiddenRows": false,
        "geoJsonData": {},
        "database": "HIUSER",
        "activeFunc": false
    },
}
