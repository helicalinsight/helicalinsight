export const mock_data_checkIsFound = {
  newValue: {
    value: "CHAR",
    type: "function",
  },

  databaseFunctions: {
    date: [
      {
        key: "sql.date.dateadd",
        description:
          "Returns the specified date string with the specified number of interval added to the specified unit of that date.Example:date('1991-03-01', '+3 year') result:'1994-03-01' supported units:day, month, year.",
        value: "DATEADD",
        signature: "date(${date}, '+'||${value}||' '||${unit})",
        returns: "date",
        parameters: [
          {
            name: "date",
            column: true,
            defaultValue: "'2014-03-08'",
          },
          {
            name: "value",
            column: true,
            defaultValue: "'2'",
          },
          {
            name: "unit",
            column: true,
            defaultValue: "'year'",
          },
        ],
      },
      {
        key: "sql.date.datediff",
        description:
          "Returns the difference between date1 and date2 by comparing the provided unit. Example: (strftime('%d','2011-10-10') - strftime('%d','2012-01-01')) result: 9.\nSupported units:\n%d\t\tday of month: 00,\n%m\t\tmonth: 01-12,\n%Y\t\tyear: 0000-9999,",
        value: "DATEDIFF",
        signature: "(strftime(${unit},${date1}) - strftime(${unit},${date2}))",
        returns: "numeric",
        parameters: [
          {
            name: "unit",
            column: true,
            defaultValue: "'%Y'",
          },
          {
            name: "date1",
            column: true,
            defaultValue: "'2014-03-08'",
          },
          {
            name: "date2",
            column: true,
            defaultValue: "'2019-03-08'",
          },
        ],
      },
      {
        key: "sql.date.makedate",
        description:
          "Returns a date for given year and day-of-year values. dayofyear must be greater than 0 otherwise the result is NULL. Example: date('2019'||'-'||'02'||'-'||'25') result: '2019-02-25'.",
        value: "MAKEDATE",
        signature: "date(${year}||'-'||${month}||'-'||${day})",
        returns: "date",
        parameters: [
          {
            name: "year",
            column: true,
            defaultValue: "'2019'",
          },
          {
            name: "month",
            column: true,
            defaultValue: "'11'",
          },
          {
            name: "day",
            column: true,
            defaultValue: "'23'",
          },
        ],
      },
      {
        key: "sql.date.datetrunc",
        description:
          "Truncates the specified date to the accuracy specified by the format(unit). Example:date(strftime('%Y-01-01','2019-11-20')) result: '2019-01-01', Example2: date(strftime('%Y-%m-01','2019-11-20')) result: '20019-11-01' Example3:date(strftime('%Y-%m-%d','2019-11-20')) result: '2014-03-08' ",
        value: "DATETRUNC",
        signature: "date(strftime(${unit},${date}))",
        returns: "date",
        parameters: [
          {
            name: "date",
            column: true,
            defaultValue: "'2014-03-08'",
          },
          {
            name: "unit",
            column: true,
            defaultValue: "'%Y-%m-01'",
          },
        ],
      },
      {
        key: "sql.date.datepart",
        description:
          "Returns an integer that represents the specified unit of the specified date. Example: cast(strftime('%Y','2011-10-17') as integer) result: 2011. Supported units:\n%d\t\tday of month: 00,\n%j\t    Day of the year (001-366),\n%J\t    Julian day number (DDDDDDD.ddddddd),\n%w\t    Weekday (0-6)(0=Sunday, 1=Monday, 2=Tuesday, 3=Wednesday, 4=Thursday, 5=Friday, 6=Saturday),\n%W\t    Week number in the year (00-53),\n%m\t\tmonth: 01-12,\n%Y\t\tyear: 0000-9999.\n",
        value: "DATEPART",
        signature: "cast(strftime(${unit},${date}) as integer)",
        returns: "numeric",
        parameters: [
          {
            name: "unit",
            column: true,
            defaultValue: "'%Y'",
          },
          {
            name: "date",
            column: true,
            defaultValue: "'2014-03-08'",
          },
        ],
      },
      {
        key: "sql.date.today",
        description: "Displays Current date.",
        value: "TODAY",
        signature: "DATE()",
        returns: "date",
        parameters: [],
      },
    ],
    dateTime: [
      {
        key: "sql.dateTime.day",
        description:
          "Returns day of the month for date/datetime (1-31). Example: cast(strftime('%d','2011-10-17') as integer)result: 17\nExample2:cast(strftime('%d','2019-12-22 12:22:23.45') as integer) result:22",
        value: "DAY",
        signature: "cast(strftime('%d',${datetime}) as integer)",
        returns: "numeric",
        parameters: [
          {
            name: "datetime",
            column: true,
            defaultValue: "'2007-02-03 09:00:00'",
          },
        ],
      },
      {
        key: "sql.dateTime.datetimeadd",
        description:
          "Returns the specified datetime with the specified number interval added to the unit of that datetime. Example:datetime('1991-03-01 10:22:24.27','+2 month') result:1991-05-01 10:22:24 supported units:second, minute, hour, day, month, year.",
        value: "DATETIMEADD",
        signature: "datetime(${datetime},'+'||${value}||' '||${unit})",
        returns: "dateTime",
        parameters: [
          {
            name: "datetime",
            column: true,
            defaultValue: "'2014-03-08 11:10:27'",
          },
          {
            name: "value",
            column: true,
            defaultValue: "'2'",
          },
          {
            name: "unit",
            column: true,
            defaultValue: "'year'",
          },
        ],
      },
      {
        key: "sql.dateTime.datetimediff",
        description:
          "Returns the difference between datetime1 and datetime2 with provided unit. Example:(strftime('%d','2011-10-10 12:25:33') - strftime('%d','2012-01-01 12:22:23')) result: 9.\nSupported units:\n%d\t\tday of month: 00,\n%m\t\tmonth: 01-12,\n%Y\t\tyear: 0000-9999,\n%H      Hour on 24-hour clock (00-23),\n%M      Minute (00-59),\n%S      Seconds (00-59),\n",
        value: "DATETIMEDIFF",
        signature:
          "(strftime(${unit},${datetime1}) - strftime(${unit},${datetime2}))",
        returns: "numeric",
        parameters: [
          {
            name: "unit",
            column: true,
            defaultValue: "'%Y'",
          },
          {
            name: "datetime1",
            column: true,
            defaultValue: "'2014-03-08 09:00:00'",
          },
          {
            name: "datetime2",
            column: true,
            defaultValue: "'2019-03-08 09:00:00'",
          },
        ],
      },
      {
        key: "sql.dateTime.dayname",
        description:
          "Retruns the name of the week day for date/dateTime. Example:  (case cast (strftime('%w', '2019-08-25') as integer)\n  when 0 then 'Sunday'\n  when 1 then 'Monday'\n  when 2 then 'Tuesday'\n  when 3 then 'Wednesday'\n  when 4 then 'Thursday'\n  when 5 then 'Friday'\n  else 'Saturday' end) result: 'Sunday'",
        value: "DAYNAME",
        signature:
          "(case cast(strftime('%w', ${datetime}) as integer)\n  when 0 then 'Sunday'\n  when 1 then 'Monday'\n  when 2 then 'Tuesday'\n  when 3 then 'Wednesday'\n  when 4 then 'Thursday'\n  when 5 then 'Friday'\n  else 'Saturday' end)",
        returns: "text",
        parameters: [
          {
            name: "datetime",
            column: true,
            defaultValue: "'2007-02-03 09:00:00'",
          },
        ],
      },
      {
        key: "sql.dateTime.datetimepart",
        description:
          "Returns an integer that represents the specified datepart of the specified datetime. Example: cast(strftime('%Y','2011-10-17 10:22:24') as integer) result: 2011. \nSupported units:\n%d\tDay of the month (1-31),\n%f\tSeconds with fractional seconds (SS.sss),\n%H\tHour on 24-hour clock (00-23),\n%j\tDay of the year (001-366),\n%J\tJulian day number (DDDDDDD.ddddddd),\n%m\tMonth (01-12),\n%M\tMinute (00-59),\n%s\tSeconds since 1970-01-01,\n%S\tSeconds (00-59),\n%w\tWeekday (0-6)(0=Sunday, 1=Monday, 2=Tuesday, 3=Wednesday, 4=Thursday, 5=Friday, 6=Saturday),\n%W\tWeek number in the year (00-53)The first Monday is the beginning of week 1,\n%Y\tYear with century (yyyy).",
        value: "DATETIMEPART",
        signature: "cast(strftime(${unit},${datetime}) as integer)",
        returns: "numeric",
        parameters: [
          {
            name: "unit",
            column: true,
            defaultValue: "'%Y'",
          },
          {
            name: "datetime",
            column: true,
            defaultValue: "'2014-03-08 12:32:42'",
          },
        ],
      },
      {
        key: "sql.dateTime.month",
        description:
          "Returns the month of the year for date/dateTime. Example:cast(strftime('%m','2007-02-03 09:00:00') as integer)/cast(strftime('%m','2007-02-03') as integer) result: 2",
        value: "MONTH",
        signature: "cast(strftime('%m',${datetime}) as integer)",
        returns: "numeric",
        parameters: [
          {
            name: "datetime",
            column: true,
            defaultValue: "'2007-02-03 09:00:00'",
          },
        ],
      },
      {
        key: "sql.dateTime.datetimetrunc",
        description:
          "Truncates the specified datetime to the accuracy specified by the format(unit). Example :datetime(strftime('%Y-%m-%d %H:%M:%S','2014-03-08 12:20:19')) result: 2014-03-08 12:20:19,Example2: datetime(strftime('%Y-%m-%d %H:00:00','2014-03-08 12:20:19')) result: 2014-03-08 12:00:00,Example3: datetime(strftime('%Y-%m-01 00:00:00','2014-03-08 12:20:19')) result: 2014-03-01 00:00:00 ",
        value: "DATETIMETRUNC",
        signature: "datetime(strftime(${unit},${datetime}))",
        returns: "dateTime",
        parameters: [
          {
            name: "datetime",
            column: true,
            defaultValue: "'2014-03-08 12:20:19'",
          },
          {
            name: "unit",
            column: true,
            defaultValue: "'%Y-%m-%d %H:%M:%S'",
          },
        ],
      },
      {
        key: "sql.dateTime.year",
        description:
          "Returns the year for date/datetime. Example:cast(strftime('%Y','2014-03-08 12:20:19') as integer)/cast(strftime('%Y','2014-03-08') as integer) result: 2014",
        value: "YEAR",
        signature: "cast(strftime('%Y',${datetime}) as integer)",
        returns: "numeric",
        parameters: [
          {
            name: "datetime",
            column: true,
            defaultValue: "'2007-02-03 09:00:00'",
          },
        ],
      },
      {
        key: "sql.dateTime.monthname",
        description:
          "Returns the month name based on month number. The value ranges from 1 (January) to 12 (December).  Example: (case cast(strftime('%m', '2014-02-08 12:20:19') as integer)\n  when 1 then 'January'\n  when 2 then 'February'\n  when 3 then 'March'\n  when 4 then 'April'\n  when 5 then 'May'\n  when 6 then 'June'\n  when 7 then 'July'\n  when 8 then 'August'\n  when 9 then 'September'\n  when 10 then 'October'\n  when 11 then 'November'\n  when 12 then 'December'\n  else null end)result: 'February'",
        value: "MONTHNAME",
        signature:
          " (case cast(strftime('%m', ${datetime}) as integer)\n  when 1 then 'January'\n  when 2 then 'February'\n  when 3 then 'March'\n  when 4 then 'April'\n  when 5 then 'May'\n  when 6 then 'June'\n  when 7 then 'July'\n  when 8 then 'August'\n  when 9 then 'September'\n  when 10 then 'October'\n  when 11 then 'November'\n  when 12 then 'December'\n  else null end)",
        returns: "text",
        parameters: [
          {
            name: "datetime",
            column: true,
            defaultValue: "'2007-02-03 09:00:00'",
          },
        ],
      },
      {
        key: "sql.dateTime.dayofweek",
        description:
          "Returns the ISO day of the week for date/datetime. The value ranges from 0 (sunday) to 6 (saturday). \n         Example: cast(strftime('%w',2007-02-03 09:00:00') as integer)/cast(strftime('%w',2007-02-03')result: 6.",
        value: "DAYOFWEEK",
        signature: "cast(strftime('%w',${datetime}) as integer)",
        returns: "numeric",
        parameters: [
          {
            name: "datetime",
            column: true,
            defaultValue: "'2007-02-03 09:00:00'",
          },
        ],
      },
      {
        key: "sql.dateTime.now",
        description:
          "Displays Current date and time. This function equivalent to current_timestamp.",
        value: "NOW",
        signature: "datetime()",
        returns: "dateTime",
        parameters: [],
      },
      {
        key: "sql.dateTime.format",
        description:
          "Return a DATETIME format as a string.Example: strftime('%Y-%m-%d %H:%M:%S', '2019-12-22 12:22:23.45') result:'2019-12-22 12:22:23.45'",
        value: "FORMAT",
        signature: "strftime(${format}, ${datetime})",
        returns: "text",
        parameters: [
          {
            name: "datetime",
            column: true,
            defaultValue: "'2014-03-08 09:00:00'",
          },
          {
            name: "format",
            column: true,
            defaultValue: "'%Y-%m-%d %H:%M:%S'",
          },
        ],
      },
      {
        key: "sql.dateTime.makedatetime",
        description:
          "Returns a datetime that combines a date and time. Example:datetime('2019'||'-'||'12'||'-'||'22'||' '||'12'||':'||'20'||':'||'26')result: 2019-12-22 12:20:26.",
        value: "MAKEDATETIME",
        signature:
          "datetime(${year}||'-'||${month}||'-'||${day}||' '||${hour}||':'||${minute}||':'||${second})",
        returns: "date",
        parameters: [
          {
            name: "year",
            column: true,
            defaultValue: "'2012'",
          },
          {
            name: "month",
            column: true,
            defaultValue: "'10'",
          },
          {
            name: "day",
            column: true,
            defaultValue: "'11'",
          },
          {
            name: "hour",
            column: true,
            defaultValue: "'10'",
          },
          {
            name: "minute",
            column: true,
            defaultValue: "'40'",
          },
          {
            name: "second",
            column: true,
            defaultValue: "'30'",
          },
        ],
      },
      {
        key: "sql.dateTime.maketime",
        description:
          "Returns time value from the hour, minute and seconds.Example:  time('12'||':'||'20'||':'||'26') result:12:20:26",
        value: "MAKETIME",
        signature: " time(${hour}||':'||${minute}||':'||${second})",
        returns: "time",
        parameters: [
          {
            name: "hour",
            column: true,
            defaultValue: "'12'",
          },
          {
            name: "minute",
            column: true,
            defaultValue: "'15'",
          },
          {
            name: "second",
            column: true,
            defaultValue: "'30'",
          },
        ],
      },
      {
        key: "sql.dateTime.hour",
        description:
          "Returns the hour for timestamp. The value ranges from 0 to 24. Example: cast(strftime('%H','2014-03-08 24:20:19') as integer) result: 24 ",
        value: "HOUR",
        signature: "cast(strftime('%H',${datetime}) as integer)",
        returns: "numeric",
        parameters: [
          {
            name: "datetime",
            column: true,
            defaultValue: "'2014-03-08 12:20:19'",
          },
        ],
      },
      {
        key: "sql.dateTime.minute",
        description:
          "Returns the minute for time. Example: cast(strftime('%M','2014-03-08 22:20:19') as integer) result:20",
        value: "MINUTE",
        signature: "cast(strftime('%M',${datetime}) as integer)",
        returns: "numeric",
        parameters: [
          {
            name: "datetime",
            column: true,
            defaultValue: "'2014-03-08 12:20:19'",
          },
        ],
      },
      {
        key: "sql.dateTime.second",
        description:
          "Returns the second for time. Example: cast(strftime('%S','2014-03-08 22:20:19') as integer) result:19",
        value: "SECOND",
        signature: "cast(strftime('%S',${datetime}) as integer)",
        returns: "numeric",
        parameters: [
          {
            name: "datetime",
            column: true,
            defaultValue: "'2014-03-08 12:20:19'",
          },
        ],
      },
      {
        key: "sql.dateTime.millisecond",
        description:
          "Returns the millisecond of the second for time. Example: cast(strftime('%f','2014-03-08 12:20:19.344') as decimal) result:19.344",
        value: "MILLISECOND",
        signature: "cast(strftime('%f',${datetime}) as decimal)",
        returns: "numeric",
        parameters: [
          {
            name: "datetime",
            column: true,
            defaultValue: "'2014-03-08 12:20:19.344'",
          },
        ],
      },
      {
        key: "sql.dateTime.quarter",
        description:
          "Returns the quarter of the year for date/datetime. Example:((strftime('%m', '2014-03-08 12:20:19.344') + 2) / 3)/((strftime('%m', '2014-03-08') + 2) / 3)  result:1",
        value: "QUARTER",
        signature: "((strftime('%m', ${datetime}) + 2) / 3)",
        returns: "numeric",
        parameters: [
          {
            name: "datetime",
            column: true,
            defaultValue: "'2014-03-08 12:20:19'",
          },
        ],
      },
      {
        key: "sql.dateTime.week",
        description:
          "Returns the ISO week of the year for date/dateTime. The value ranges from 0 to 52. Example: cast(strftime('%W','2014-03-08 22:20:19') as integer)/cast(strftime('%W','2014-03-08') as integer) result: 9",
        value: "WEEK",
        signature: "cast(strftime('%W',${datetime}) as integer)",
        returns: "numeric",
        parameters: [
          {
            name: "datetime",
            column: true,
            defaultValue: "'2014-03-08 12:20:19'",
          },
        ],
      },
    ],
    "type conversion": [
      {
        key: "sql.typeConversion.cast",
        description:
          "Cast function converts one datatype to another. Note:All Values should be in single quotes if you are typing the value. Example: CAST('2019-03-22 17:34:03.000' AS char) result:2019-03-22 17:34:03.000",
        value: "CAST",
        signature: "CAST(${column} AS ${dataType})",
        returns: "text",
        parameters: [
          {
            name: "column",
            column: true,
          },
          {
            name: "dataType",
            column: false,
          },
        ],
      },
      {
        key: "sql.typeConversion.tochar",
        description:
          "Converts value to char type. Example:\tcast('2015-01-01' AS CHAR) result:2015-01-01 ",
        value: "TOCHAR",
        signature: "cast(${column} AS CHAR)",
        returns: "text",
        parameters: [
          {
            name: "column",
            column: true,
          },
        ],
      },
      {
        key: "sql.typeConversion.tonum",
        description:
          "This function converts value to a number type. Example: cast(23.3 AS INTEGER) result:23",
        value: "TONUM",
        signature: "CAST(${column} as INTEGER)",
        returns: "numeric",
        parameters: [
          {
            name: "column",
            column: true,
          },
        ],
      },
      {
        key: "sql.typeConversion.totime",
        description:
          'Converts value to TIME type. Format is "HH:MM:SS" (Supported range is from "-838:59:59" to "838:59:59").\tExample: TIME(\'2019-02-12 14:06:10\') RESULTS: 14:06:10. NOTE: column\'s value should be in quotes if the values is manually typing and its data type is "text/date/dateTime/time" type',
        value: "TOTIME",
        signature: "TIME(${column})",
        returns: "time",
        parameters: [
          {
            name: "column",
            column: true,
          },
        ],
      },
      {
        key: "sql.typeConversion.todate",
        description:
          'Converts value to DATE type. Format is "YYYY-MM-DD" (Supported range is from "1000-01-01" to "9999-12-31").Example: DATE(\'2019-02-12 14:06:10\') RESULTS: 2019-02-12.NOTE: column\'s value should be in quotes if the values is manually typing',
        value: "TODATE",
        signature: "DATE(${column})",
        returns: "date",
        parameters: [
          {
            name: "column",
            column: true,
          },
        ],
      },
      {
        key: "sql.typeConversion.todatetime",
        description:
          'Converts value to DATETIME type. Format is "YYYY-MM-DD HH:MM:SS" (Supported range is from "1000-01-01 00:00:00" to "9999-12-31 23:59:59"). Example:DATETIME(\'2019-02-12\') RESULTS: 2019-02-12 00:00:00.NOTE: column\'s value should be in quotes if the values is manually typing',
        value: "TODATETIME",
        signature: "DATETIME(${column})",
        returns: "dateTime",
        parameters: [
          {
            name: "column",
            column: true,
          },
        ],
      },
      {
        key: "sql.typeConversion.todecimal",
        description:
          "Converts the value to decimal type. Example: cast(23.3 as decimal) result: 23.3",
        value: "TODECIMAL",
        signature: "cast(${column} AS DECIMAL)",
        returns: "numeric",
        parameters: [
          {
            name: "column",
            column: true,
          },
        ],
      },
      {
        key: "sql.typeConversion.typeof",
        description: "Returns the name of the type of the provided expression",
        value: "TYPEOF",
        signature: "typeof(${column})",
        returns: "text",
        parameters: [
          {
            name: "column",
            column: true,
          },
        ],
      },
    ],
    "sqlite specific": [
      {
        key: "sql.date.datetime.unix",
        description: "It shows the date and time for a given UNIX timestamp.",
        value: "datetime_unixepoch",
        signature: "datetime(${column},${unixepoch})",
        returns: "dateTime",
        parameters: [
          {
            name: "column",
            column: true,
          },
          {
            name: "unixepoch",
            defaultValue: "'unixepoch'",
          },
        ],
      },
      {
        key: "sql.date.strftime",
        description: "It shows current UNIX timestamp.",
        value: "strftime(date)",
        signature: "strftime('%Y-%m-%d',${timestring})",
        returns: "date",
        parameters: [
          {
            name: "timestring",
            column: true,
          },
        ],
      },
      {
        key: "sql.date.strftimeDateTime",
        description: "It shows current UNIX timestamp.",
        value: "strftime(dateTime)",
        signature: "strftime('%Y-%m-%d %H:%M:%S',${timestring})",
        returns: "dateTime",
        parameters: [
          {
            name: "timestring",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.quote",
        description: "Displays a string in single quote for a specified value.",
        value: "quote",
        signature: "quote(${column})",
        returns: "text",
        parameters: [
          {
            name: "column",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.like",
        description:
          "Allows to search for a specified pattern in a column.\n\tIf the search expression can be matched to the pattern expression, \n\tthe LIKE operator will return true, which is 1",
        value: "like",
        signature: "like(${col1},${col2})",
        returns: "text",
        parameters: [
          {
            name: "col1",
            column: true,
          },
          {
            name: "col2",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.like2",
        description:
          "This is used to implement the \"Y LIKE X ESCAPE Z\" expression.Allows to search for a specified pattern in a column.\n\tIf the search expression can be matched to the pattern expression, \n\tthe LIKE operator will return true, which is 1. eg: WHERE LIKE('%r%%',descrip,'\\')=1;",
        value: "like",
        signature: "like(${X},${Y},${Z})",
        returns: "text",
        parameters: [
          {
            name: "X",
            column: true,
          },
          {
            name: "Y",
            column: true,
          },
          {
            name: "Z",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.likelihood",
        description:
          "It provides a hint to the query planner that the argument X is a boolean that is true with a probability of approximately Y.",
        value: "likelihood",
        signature: "likelihood(${col1},${floatVal})",
        returns: "other",
        parameters: [
          {
            name: "col1",
            column: true,
          },
          {
            name: "floatVal",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.hex",
        description:
          "This function  interprets its argument as a BLOB and returns a string which is the upper-case hexadecimal rendering of the content of that blob.",
        value: "hex",
        signature: "hex(${col1})",
        returns: "text",
        parameters: [
          {
            name: "col1",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.glob",
        description:
          "The SQLite GLOB operator is used to match only text values against a pattern using wildcards.",
        value: "glob",
        signature: "glob(${xVal},${yVal})",
        returns: "text",
        parameters: [
          {
            name: "xVal",
            column: true,
          },
          {
            name: "yVal",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.dateToString",
        description: "Converts the date to string",
        value: "dateToString",
        signature: "cast(${column} as text) ",
        returns: "text",
        parameters: [
          {
            name: "column",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.numericToString",
        description: "Converts the number to string",
        value: "numericToString",
        signature: "cast(${column} as text) ",
        returns: "text",
        parameters: [
          {
            name: "column",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.dateTimeToString",
        description: "Converts the datetime to string",
        value: "dateTimeToString",
        signature: "cast(${column} as text) ",
        returns: "text",
        parameters: [
          {
            name: "column",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.timeToString",
        description: "Converts the time to string",
        value: "timeToString",
        signature: "cast(${column} as text) ",
        returns: "text",
        parameters: [
          {
            name: "column",
            column: true,
          },
        ],
      },
    ],
    numeric: [
      {
        key: "sql.numeric.abs",
        description:
          "It returns the absolute value of a number. Example: abs(-24)Result: 24",
        value: "ABS",
        signature: "abs(${number})",
        returns: "numeric",
        parameters: [
          {
            name: "number",
            column: true,
          },
        ],
      },
      {
        key: "sql.numeric.acos",
        description:
          "Returns the arc cosine of number. Example: acos(0.25) result: 1.318116071652818 ",
        value: "ACOS",
        signature: "acos(${number})",
        returns: "numeric",
        parameters: [
          {
            name: "number",
            column: true,
          },
        ],
      },
      {
        key: "sql.numeric.asin",
        description:
          "Returns the arc sine of number. Example: asin(0.25) result: 0.25268025514207865 ",
        value: "ASIN",
        signature: "asin(${number})",
        returns: "numeric",
        parameters: [
          {
            name: "number",
            column: true,
          },
        ],
      },
      {
        key: "sql.numeric.atan",
        description:
          "Returns the arc tangent of number. Example: atan(0.25) result: 0.24497866312686414 ",
        value: "ATAN",
        signature: "atan(${number})",
        returns: "numeric",
        parameters: [
          {
            name: "number",
            column: true,
          },
        ],
      },
      {
        key: "sql.numeric.atan2",
        description:
          "Returns the arc tangent of given number. Example: atan2(0.50,1) result: 0.4636476090008061",
        value: "ATAN2",
        signature: "atan2(${number1},${number2})",
        returns: "numeric",
        parameters: [
          {
            name: "number1",
            column: true,
          },
          {
            name: "number2",
            column: true,
          },
        ],
      },
      {
        key: "sql.numeric.round",
        description:
          "Displays a value rounded to a certain number of decimal places. Example: round(34.4158,2) result: 34.42",
        value: "ROUND",
        signature: "round(${number}, ${decimalpoint})",
        returns: "numeric",
        parameters: [
          {
            name: "number",
            column: true,
          },
          {
            name: "decimalpoint",
            defaultValue: "1",
          },
        ],
      },
      {
        key: "sql.numeric.ceiling",
        description:
          "Returns number rounded up to the nearest integer. Example: ceil(0.25) result: 1 ",
        value: "CEILING",
        signature: "ceil(${number})",
        returns: "numeric",
        parameters: [
          {
            name: "number",
            column: true,
            defaultValue: "0",
          },
        ],
      },
      {
        key: "sql.numeric.cos",
        description:
          "Returns the cosine of number. Example: cos(0.25) result: 0.9689124217106447 ",
        value: "COS",
        signature: "cos(${number})",
        returns: "numeric",
        parameters: [
          {
            name: "number",
            column: true,
          },
        ],
      },
      {
        key: "sql.numeric.cot",
        description:
          "Returns the cotangent of an angle. Specify the angle in radians. Example: cot(0.25) result: 3.9163173646459399 ",
        value: "COT",
        signature: "cot(${number})",
        returns: "numeric",
        parameters: [
          {
            name: "number",
            column: true,
          },
        ],
      },
      {
        key: "sql.numeric.degrees",
        description:
          "Converts angle number in radians to degrees. Example: degrees(0.25) result: 14.32394487827058",
        value: "DEGREES",
        signature: "degrees(${number})",
        returns: "numeric",
        parameters: [
          {
            name: "number",
            column: true,
          },
        ],
      },
      {
        key: "sql.numeric.div",
        description:
          "Returns the integer part of a division operation. Example: (10/5) result: 2",
        value: "DIV",
        signature: "(${dividend} / NULLIF(${divisor}, 0))",
        returns: "numeric",
        parameters: [
          {
            name: "divisor",
            column: true,
          },
          {
            name: "dividend",
            column: true,
          },
        ],
      },
      {
        key: "sql.numeric.exp",
        description:
          "Returns Euler’s number raised to the power of the given number. Example: exp(2) result: 7.389",
        value: "EXP",
        signature: "exp(${number})",
        returns: "numeric",
        parameters: [
          {
            name: "number",
            column: true,
          },
        ],
      },
      {
        key: "sql.numeric.floor",
        description:
          "Returns number rounded down to the nearest number. Example: floor(3.1415) result: 3 ",
        value: "FLOOR",
        signature: "floor(${number})",
        returns: "numeric",
        parameters: [
          {
            name: "number",
            column: true,
            defaultValue: "0",
          },
        ],
      },
      {
        key: "sql.numeric.ln",
        description:
          "Returns the natural logarithm for a positive number. Example: log(2) result: 0.6931471805599453 ",
        value: "LN",
        signature: "log(${number})",
        returns: "numeric",
        parameters: [
          {
            name: "number",
            column: true,
          },
        ],
      },
      {
        key: "sql.numeric.log",
        description:
          "Returns the base 10 logarithm for a positive number. Example: log10(2) result: 0.3010299956639812 ",
        value: "LOG",
        signature: "log10(${number})",
        returns: "numeric",
        parameters: [
          {
            name: "number",
            column: true,
          },
        ],
      },
      {
        key: "sql.numeric.max",
        description:
          "Returns the largest of the provided values. Example: max(4,13) result: 13",
        value: "MAX",
        signature: "max(${number1},${number2})",
        returns: "numeric",
        parameters: [
          {
            name: "number1",
            column: true,
          },
          {
            name: "number2",
            column: true,
          },
        ],
      },
      {
        key: "sql.numeric.min",
        description:
          "Returns the smallest of the provided values. Example: min(13,4) result: 4",
        value: "MIN",
        signature: "min(${number1},${number2})",
        returns: "numeric",
        parameters: [
          {
            name: "number1",
            column: true,
          },
          {
            name: "number2",
            column: true,
          },
        ],
      },
      {
        key: "sql.numeric.pi",
        description:
          "Returns the constant Pi. Example: pi() result: 3.141592653589793 ",
        value: "PI",
        signature: "pi()",
        returns: "numeric",
        parameters: [],
      },
      {
        key: "sql.numeric.sign",
        description:
          "Returns the signum function of number, that is:\n\t\t0 if the argument is 0,\n\t\t1 if the argument is greater than 0,\n\t\t-1 if the argument is less than 0\n        1.0 if the argument is decimal. Example: sign(0.5) result: 1.0.",
        value: "SIGN",
        signature: "sign(${number})",
        returns: "numeric",
        parameters: [
          {
            name: "number",
            column: true,
          },
        ],
      },
      {
        key: "sql.numeric.sin",
        description:
          "Returns the sine of number. Example: sin(0.25) result: 0.24740395925452294",
        value: "SIN",
        signature: "sin(${number})",
        returns: "numeric",
        parameters: [
          {
            name: "number",
            column: true,
          },
        ],
      },
      {
        key: "sql.numeric.power",
        description:
          "Displays the value of a number raised to the power of another number. Example: power(4,2) result: 16.0",
        value: "POWER",
        signature: "power(${number},${exponent})",
        returns: "numeric",
        parameters: [
          {
            name: "number",
            column: true,
            defaultValue: "0",
          },
          {
            name: "exponent",
            defaultValue: "10",
          },
        ],
      },
      {
        key: "sql.numeric.radians",
        description:
          "Converts the value of a number from degrees to radians. Example : radians(4) result : 0.06981317007977318",
        value: "RADIANS",
        signature: "radians(${number})",
        returns: "numeric",
        parameters: [
          {
            name: "number",
            column: true,
          },
        ],
      },
      {
        key: "sql.numeric.sqrt",
        description:
          "Displays the square root of a non-negative number. Example: sqrt(5) result: 2.23606797749979",
        value: "SQRT",
        signature: "sqrt(${number})",
        returns: "numeric",
        parameters: [
          {
            name: "number",
            column: true,
          },
        ],
      },
      {
        key: "sql.numeric.square",
        description:
          "It displays the square of a given number. Example: square(4) result: 16",
        value: "SQUARE",
        signature: "square(${number})",
        returns: "numeric",
        parameters: [
          {
            name: "number",
            column: true,
          },
        ],
      },
      {
        key: "sql.numeric.tan",
        description:
          "Returns the tangent of number. Example: tan(0.25) result: 0.25534192122103627 ",
        value: "TAN",
        signature: "tan(${number})",
        returns: "numeric",
        parameters: [
          {
            name: "number",
            column: true,
          },
        ],
      },
      {
        key: "sql.numeric.truncate",
        description:
          "Displays a number truncated to a certain number of decimal places. Example: cast(substr(1.289, 1, instr(1.289, '.') + 2)as decimal) result: 1.28",
        value: "TRUNCATE",
        signature:
          "cast(substr(${number}, 1, instr(${number}, '.') + ${digit})as decimal)",
        returns: "numeric",
        parameters: [
          {
            name: "number",
            column: true,
            defaultValue: "0",
          },
          {
            name: "digit",
            defaultValue: "10",
          },
        ],
      },
    ],
    text: [
      {
        key: "sql.text.ascii",
        description:
          "Returns the Unicode of the first character of string. Example: unicode('A') result:65",
        value: "ASCII",
        signature: "unicode(${string})",
        returns: "numeric",
        parameters: [
          {
            name: "string",
            column: true,
            defaultValue: "'A'",
          },
        ],
      },
      {
        key: "sql.text.char",
        description:
          "Returns the character encoded by the ASCII code. Example:char(65) result:A ",
        value: "CHAR",
        signature: "char(${number})",
        returns: "text",
        parameters: [
          {
            name: "number",
            column: true,
            defaultValue: "65",
          },
        ],
      },
      {
        key: "sql.text.length",
        description:
          "Returns the number of characters in string. Example : LENGTH('Bengaluru') result:9  ",
        value: "LENGTH",
        signature: "length(${string})",
        returns: "numeric",
        parameters: [
          {
            name: "string",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.lower",
        description:
          "Converts all characters in the specified string to lowercase. Example:  LOWER('BENGALURU') result: bengaluru",
        value: "LOWER",
        signature: "lower(${string})",
        returns: "text",
        parameters: [
          {
            name: "string",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.upper",
        description:
          "Converts all characters in a string to uppercase. Example: UPPER('bengaluru') result: BENGALURU.",
        value: "UPPER",
        signature: "upper(${string})",
        returns: "text",
        parameters: [
          {
            name: "string",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.trim",
        description:
          "It removes leading and trailing whitespace from string. Example: TRIM('   Bengaluru   ') result:Bengaluru\n        ",
        value: "TRIM",
        signature: "trim(${string})",
        returns: "text",
        parameters: [
          {
            name: "string",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.ltrim",
        description:
          "Removes leading whitespace from string. Example: LTRIM('     Bengaluru') result: Bengaluru",
        value: "LTRIM",
        signature: "ltrim(${string})",
        returns: "text",
        parameters: [
          {
            name: "string",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.rtrim",
        description:
          "Removes trailing whitespace from string. Example: RTRIM('Bengaluru   ') result: Bengaluru.\n        ",
        value: "RTRIM",
        signature: "rtrim(${string})",
        returns: "text",
        parameters: [
          {
            name: "string",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.space",
        description:
          "Returns a string consisting of space characters. Example:  length(printf('%*s', 5, '')) result:5.\n        ",
        value: "SPACE",
        signature: "printf('%*s', ${noOfSpace}, '')",
        returns: "text",
        parameters: [
          {
            name: "noOfSpace",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.mid",
        description:
          "Returns the string starting from specified position. If position is more than string or length is less than 1 it will return empty string. Example: substr('Bengaluru', 2, 5) result: engal",
        value: "MID",
        signature: "trim(substr(${string},${position},${length}))",
        returns: "text",
        parameters: [
          {
            name: "string",
            column: true,
          },
          {
            name: "position",
            column: true,
          },
          {
            name: "length",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.replace",
        description:
          "String search for substring and replace it with replacestring. If substring is not found, the string is not changed. Example: REPLACE('bengaluru','b','Z') result: Zengaluru",
        value: "REPLACE",
        signature: "trim(replace(${string},${substring},${replacestring}))",
        returns: "text",
        parameters: [
          {
            name: "string",
            column: true,
          },
          {
            name: "substring",
            column: true,
          },
          {
            name: "replacestring",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.right",
        description:
          "Returns the rightmost character from the string. Example: substr('bengaluru', (length('bengaluru')+1) - 4,length('bengaluru')) result: luru",
        value: "RIGHT",
        signature:
          "trim(substr(${string},(length(${string})+1)-${length}, length(${string})))",
        returns: "text",
        parameters: [
          {
            name: "string",
            column: true,
          },
          {
            name: "length",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.lpad",
        description:
          "Displays a string that is left padded with a specified string to a certain length. If length is less than string, return value is truncated to length characters. Example: substr('Hills' || 'Wellesley', -14, 14) result:HillsWellesley. ",
        value: "LPAD",
        signature: "substr(${padString} || ${string},-${length},${length})",
        returns: "text",
        parameters: [
          {
            name: "string",
            column: true,
          },
          {
            name: "length",
            defaultValue: "0",
          },
          {
            name: "padString",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.left",
        description:
          "Returns the left most character from the string. If length is more than string it will return entire string. Note: Array index starts with 1. Example: substr('bengaluru', 1, 4) result: 'beng'",
        value: "LEFT",
        signature: "trim(substr(${string}, 1, ${length}))",
        returns: "text",
        parameters: [
          {
            name: "string",
            column: true,
          },
          {
            name: "length",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.find",
        description:
          "Returns the starting position of the first instance of substring in string. Positions start with 1. If not found, 0 is returned. Example :instr('Bengaluru','Z') result : 0, instr('Bengaluru','aluru') result : 5",
        value: "FIND",
        signature: "instr(${string},${substring})",
        returns: "numeric",
        parameters: [
          {
            name: "string",
            column: true,
          },
          {
            name: "substring",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.endswith",
        description:
          "Returns true if the given string endswith specified substring. Example:case when('postgres' like ('%'||'res')) then 'true' else 'false' end result: 'true'. Note:Please provide single quotes if you are directly typing the substring value.",
        value: "ENDSWITH",
        signature:
          "case when(${string} like ('%'||${substring})) then 'true' else 'false' end",
        returns: "boolean",
        parameters: [
          {
            name: "string",
            column: true,
          },
          {
            name: "substring",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.startswith",
        description:
          "Returns true if string starts with substring. Example: case when('bengaluru' like ('ben'||'%')) then 'true' else 'false' end result: 'true'.  Note:Please provide single quotes if you are directly typing the substring value.",
        value: "STARTSWITH",
        signature:
          "case when(${string} like (${substring}||'%')) then 'true' else 'false' end",
        returns: "boolean",
        parameters: [
          {
            name: "string",
            column: true,
          },
          {
            name: "substring",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.contains",
        description:
          " Returns true if the given string contains the specified substring. Example : case when (instr('refrigerator','g'))>0 then 'true' else 'false' end result:true",
        value: "CONTAINS",
        signature:
          "case when (instr(${string},${substring}))>0 then 'true' else 'false' end",
        returns: "boolean",
        parameters: [
          {
            name: "substring",
            column: true,
          },
          {
            name: "string",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.rpad",
        description:
          "Displays a string that is right padded with a specified string to a certain length. If length is less than string, return value is truncated to length characters.Length must not be negative and padString must not be non-empty. Example:substr('Wellesley' || 'Hills', 1, 14) result : Wellesley Hills. ",
        value: "RPAD",
        signature: "substr(${string}|| ${padString}, 1, ${length})",
        returns: "text",
        parameters: [
          {
            name: "string",
            column: true,
          },
          {
            name: "length",
            defaultValue: "0",
          },
          {
            name: "padString",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.reverse",
        description:
          "Returns string with the characters in reverse order. Example: REVERSE('Bengaluru')  result : urulagneB ",
        value: "REVERSE",
        signature: "reverse(${string})",
        returns: "text",
        parameters: [
          {
            name: "string",
            column: true,
          },
        ],
      },
      {
        key: "sql.text.concat",
        description:
          "Returns the concatenation of string1, string2. Example: CONCAT('Beng','aluru') result : Bengaluru",
        value: "CONCAT",
        signature: "(${string1}||${string2})",
        returns: "text",
        parameters: [
          {
            name: "string1",
            column: true,
            defaultValue: "'Beng'",
          },
          {
            name: "string2",
            column: true,
            defaultValue: "'aluru'",
          },
        ],
      },
    ],
    logical: [
      {
        key: "sql.logical.and",
        description:
          "Inside IF we will use AND condition. performs a logical conjunction on two expressions.\n            In 'column' paramter we will 'drag column'.\n\t\t\tIn 'condition' parameter provide conditions like (>,= etc.,like).\n\t\t\tIn 'value' parameter provide condition value.\n\t\t\tIn 'moreconditions' parameter we will use nested 'OR' condition , 'AND' conditions. \n\t\t\tExample: CASE WHEN 'Washington' like '%sh%' \n             AND 'Washington' like 'W%' THEN 'return washington' \n             else 'NotMatched' end  ",
        value: "AND",
        signature: "AND (${column} ${condition} ${value}) ${moreconditions} ",
        returns: "text",
        parameters: [
          {
            name: "column",
            column: true,
          },
          {
            name: "condition",
            defaultValue: "",
          },
          {
            name: "value",
            defaultValue: "",
          },
          {
            name: "moreconditions",
            column: true,
            defaultValue: "",
          },
        ],
      },
      {
        key: "sql.logical.case",
        description:
          'Inside case we will use when condition. Evaluates each condition from left to right and returns the result when the first condition met. If no condition met return from else if exist, otherwise return null. Example : CASE WHEN Quantity > 30 THEN "The quantity is greater than 30"  ELSE "The quantity is under 30" END ',
        value: "CASE",
        signature: "(CASE ${condition} END)",
        returns: "text",
        parameters: [
          {
            name: "condition",
            column: true,
          },
        ],
      },
      {
        key: "sql.logical.else",
        description:
          "Returns from statement_list when condition gets fail.We will use ELSE inside case function. Example: CASE when 50 > 0 then 'true' else 'false'",
        value: "ELSE",
        signature: "ELSE ${statement_list}",
        returns: "text",
        parameters: [
          {
            name: "statement_list",
            column: true,
          },
        ],
      },
      {
        key: "sql.logical.elseif",
        description:
          "Evaluates conditions and returns 'conditiontrue' if condition is true, otherwise return 'conditionfalse'.  We will use nested condition inside else 'conditiontrue' parameter. Example:case when creditlim > 50000 then 'PLATINUM' else when (creditlim > = 50000) then 'GOLD' else 'SILVER' end ",
        value: "ELSEIF",
        signature:
          "(case when ${column} ${condition} ${value} then ${conditiontrue} when ${elseIfcolumn} ${elseIfcondition} ${elseIfvalue} then ${elseIfconditiontrue} else ${conditionfalse} ${moreconditions} end)",
        returns: "text",
        parameters: [
          {
            name: "column",
            column: true,
          },
          {
            name: "condition",
            defaultValue: "",
          },
          {
            name: "value",
            defaultValue: "",
          },
          {
            name: "conditiontrue",
            column: true,
          },
          {
            name: "elseIfcolumn",
            column: true,
          },
          {
            name: "elseIfcondition",
            defaultValue: "",
          },
          {
            name: "elseIfvalue",
            defaultValue: "",
          },
          {
            name: "elseIfconditiontrue",
            column: true,
          },
          {
            name: "conditionfalse",
            column: true,
            defaultValue: "",
          },
          {
            name: "moreconditions",
            column: true,
            defaultValue: "",
          },
        ],
      },
      {
        key: "sql.logical.if",
        description:
          "Inside IF we will use AND, OR conditions. Evaluates and returns 'conditiontrue' if condition is true, otherwise return 'conditionfalse'. \n\t\t\tIn 'column' paramter we will drag column.\n\t\t\tIn 'condition' parameter provide conditions like (>,= etc.,).\n\t\t\tIn 'value' parameter provide value.\n\t\t\tIn 'moreconditions' parameter we will use nested 'OR', 'AND' conditions. Instead of dragging column directly we will write expression in column parameter like 50 > 0 (Note : In such case don't provide anything in 'condition' parameter and 'value' parameter). Example : case when 'washington'like 'W%' then 'true' else 'false' end",
        value: "IF",
        signature:
          "(case when ${column} ${condition} ${value} ${moreconditions} then ${conditiontrue} else ${conditionfalse} end)",
        returns: "text",
        parameters: [
          {
            name: "column",
            column: true,
          },
          {
            name: "condition",
            defaultValue: "",
          },
          {
            name: "value",
            defaultValue: "",
          },
          {
            name: "moreconditions",
            column: true,
            defaultValue: "",
          },
          {
            name: "conditiontrue",
            column: true,
            defaultValue: "",
          },
          {
            name: "conditionfalse",
            column: true,
          },
        ],
      },
      {
        key: "sql.logical.ifnull",
        description:
          "Returns Expr1 if it is not null otherwise return expr2. Example : IFNULL(profit, 0)",
        value: "IFNULL",
        signature: "(coalesce(${expr1}, ${expr2}))",
        returns: "text",
        parameters: [
          {
            name: "expr1",
            column: true,
          },
          {
            name: "expr2",
            column: true,
          },
        ],
      },
      {
        key: "sql.logical.iif",
        description:
          "Evaluates and returns 'conditiontrue' if condition is true, otherwise return 'conditionfalse'. Example : case when 'washington'like 'W%' then 'true' else 'false' end",
        value: "IIF",
        signature:
          "(case when ${column} ${condition} ${value} then ${conditiontrue} else ${conditionfalse} end)",
        returns: "text",
        parameters: [
          {
            name: "column",
            column: true,
          },
          {
            name: "condition",
            defaultValue: "",
          },
          {
            name: "value",
            defaultValue: "",
          },
          {
            name: "conditiontrue",
            column: true,
          },
          {
            name: "conditionfalse",
            column: true,
          },
        ],
      },
      {
        key: "sql.logical.isnull",
        description:
          "Inside WHEN condition we will use ISNULL. Evalutes and returns 'Conditiontrue' if the expression contain Null. Example1 : CASE WHEN 1 ISNULL THEN Conditionfalse. Example2 : CASE WHEN NULL ISNULL THEN Conditiontrue ",
        value: "ISNULL",
        signature: "IS NULL",
        returns: "boolean",
        parameters: [],
      },
      {
        key: "sql.logical.not",
        description:
          "Evaluates and returns 'conditiontrue' if condition is false, otherwise returns 'conditionfalse'. We will use NOT inside IF. Example :  NOT(500 > 1000) result :true",
        value: "NOT",
        signature:
          "(case when NOT(${column} ${condition} ${value}) =1 then 'true' else 'false' end)",
        returns: "boolean",
        parameters: [
          {
            name: "column",
            column: true,
          },
          {
            name: "condition",
            column: true,
            defaultValue: "",
          },
          {
            name: "value",
            column: true,
            defaultValue: "",
          },
        ],
      },
      {
        key: "sql.logical.or",
        description:
          "Performs a logical disjunction on two expressions. \n\t\t\tIn 'column' paramter we will drag column.\n\t\t\tIn 'condition' parameter provide conditions like (>,= etc.,).\n\t\t\tIn 'value' parameter provide value.\n\t\t\tIn 'moreconditions' parameter we will use nested 'OR' , 'AND' functions.  We will use OR inside IF. Example : CASE WHEN 'Washington' like '%sh%' \n             OR  'Washington' like 'W%' THEN 'return washington' \n             else 'NotMatched' end",
        value: "OR",
        signature: " OR ${column} ${condition} ${value} ${moreconditions}",
        returns: "numeric",
        parameters: [
          {
            name: "column",
            column: true,
          },
          {
            name: "condition",
            column: true,
            defaultValue: "",
          },
          {
            name: "value",
            column: true,
            defaultValue: "",
          },
          {
            name: "moreconditions",
            column: true,
            defaultValue: "",
          },
        ],
      },
      {
        key: "sql.logical.when",
        description:
          "Returns 'statement_list' when condition get satisfied .\n\t\t\tIn column paramter we will drag column.\n\t\t\tIn searchcondition parameter provide conditions like (>, =, IS Null etc .,).\n\t\t\tIn value parameter provide value(Note : IS Null used in 'condition' parameter then don't provide anything in 'value' parameter). \n\t\t\tIn moreconditions parameter we will use nested when conditions, Else condition . We will use WHEN inside CASE. Example1 : CASE WHEN 1 > 0  THEN 'one' else 'TWO'. Example2 : CASE WHEN 'Singapore' IS NULL THEN 'Singa' ELSE 'pore'. Example3 : CASE WHEN Washington like '%sh%' THEN 'return washington' else 'NotMatched' ",
        value: "WHEN",
        signature:
          "WHEN ${column} ${searchcondition} ${value} THEN ${statement_list}  ${moreconditions}",
        returns: "numeric",
        parameters: [
          {
            name: "column",
            column: true,
          },
          {
            name: "searchcondition",
            column: true,
            defaultValue: "",
          },
          {
            name: "value",
            column: true,
            defaultValue: "",
          },
          {
            name: "statement_list",
            column: true,
          },
          {
            name: "moreconditions",
            column: true,
            defaultValue: "",
          },
        ],
      },
      {
        key: "sql.logical.zn",
        description:
          "Returns \"expression\" if it is not null, otherwise returns zero.Example :(CASE WHEN null IS NULL THEN '0' ELSE null end) result :'0' ",
        value: "ZN",
        signature: "(CASE WHEN ${expr} IS NULL THEN '0' ELSE ${expr} end)",
        returns: "text",
        parameters: [
          {
            name: "expr",
            column: true,
          },
        ],
      },
    ],
  },
  expectedResult:"CHAR",
};
