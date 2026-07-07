export const date_range = {
  past: [],
  present: {
    activeReportId: "7273b8af-4577-4715-af9d-9952b9ebd3b4",
    reports: [
      {
        id: "7273b8af-4577-4715-af9d-9952b9ebd3b4",
        mode: "create",
        active: true,
        metadata: {
          classifier: "db.generic",
          name: "HIUSER",
          dataSource: {
            sync: false,
            id: "1",
            catSchemaPredicted: false,
            catalog: "",
            schema: "HIUSER",
            type: "dynamicDataSource",
            baseType: "global.jdbc",
            dbId: "6813",
          },
          uniqueId: "Metadata_1",
          tables: {
            dimdate: {
              id: "7641",
              alias: "dimdate",
              columns: {
                dim_id: {
                  alias: "dim_id",
                  fullyQualifiedColumn: "dimdate.dim_id",
                  id: "20273",
                  defaultFunction: "db.generic.aggregate.sum",
                  type: {
                    "java.lang.Integer": "numeric",
                  },
                },
                fiscal_year: {
                  alias: "fiscal_year",
                  fullyQualifiedColumn: "dimdate.fiscal_year",
                  id: "20274",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.sql.Date": "date",
                  },
                },
                modified_date: {
                  alias: "modified_date",
                  fullyQualifiedColumn: "dimdate.modified_date",
                  id: "20275",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.sql.Timestamp": "dateTime",
                  },
                },
                date_key: {
                  alias: "date_key",
                  fullyQualifiedColumn: "dimdate.date_key",
                  id: "20276",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.lang.String": "text",
                  },
                },
                day_number: {
                  alias: "day_number",
                  fullyQualifiedColumn: "dimdate.day_number",
                  id: "20277",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.lang.String": "text",
                  },
                },
                fiscal_month_name: {
                  alias: "fiscal_month_name",
                  fullyQualifiedColumn: "dimdate.fiscal_month_name",
                  id: "20278",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.lang.String": "text",
                  },
                },
                fiscal_month_label: {
                  alias: "fiscal_month_label",
                  fullyQualifiedColumn: "dimdate.fiscal_month_label",
                  id: "20279",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.lang.String": "text",
                  },
                },
                created_date: {
                  alias: "created_date",
                  fullyQualifiedColumn: "dimdate.created_date",
                  id: "20280",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.lang.String": "text",
                  },
                },
                created_time: {
                  alias: "created_time",
                  fullyQualifiedColumn: "dimdate.created_time",
                  id: "20281",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.lang.String": "text",
                  },
                },
                rating: {
                  alias: "rating",
                  fullyQualifiedColumn: "dimdate.rating",
                  id: "20282",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.lang.String": "text",
                  },
                },
              },
              name: "dimdate",
              cacheId: "4ac5d9f68b58bd7c0d179146e46795be",
              key: "e16ed033-49e1-4cf0-a234-7c1cc5f297b0",
            },
            employee_details: {
              id: "7642",
              alias: "employee_details",
              columns: {
                employee_id: {
                  alias: "employee_id",
                  fullyQualifiedColumn: "employee_details.employee_id",
                  id: "20283",
                  defaultFunction: "db.generic.aggregate.sum",
                  type: {
                    "java.lang.Integer": "numeric",
                  },
                },
                employee_name: {
                  alias: "employee_name",
                  fullyQualifiedColumn: "employee_details.employee_name",
                  id: "20284",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.lang.String": "text",
                  },
                },
                age: {
                  alias: "age",
                  fullyQualifiedColumn: "employee_details.age",
                  id: "20285",
                  defaultFunction: "db.generic.aggregate.sum",
                  type: {
                    "java.lang.Integer": "numeric",
                  },
                },
                address: {
                  alias: "address",
                  fullyQualifiedColumn: "employee_details.address",
                  id: "20286",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.lang.String": "text",
                  },
                },
              },
              name: "employee_details",
              cacheId: "4e1fd245f4d13b77be423a43f01d80b2",
              key: "2ebbb1d8-4617-4952-bdd5-93ffd04f1005",
            },
            geo_cordinates: {
              id: "7643",
              alias: "geo_cordinates",
              columns: {
                location_id: {
                  alias: "location_id",
                  fullyQualifiedColumn: "geo_cordinates.location_id",
                  id: "20287",
                  defaultFunction: "db.generic.aggregate.sum",
                  type: {
                    "java.lang.Integer": "numeric",
                  },
                },
                location: {
                  alias: "location",
                  fullyQualifiedColumn: "geo_cordinates.location",
                  id: "20288",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.lang.String": "text",
                  },
                },
                latitude: {
                  alias: "latitude",
                  fullyQualifiedColumn: "geo_cordinates.latitude",
                  id: "20289",
                  defaultFunction: "db.generic.aggregate.sum",
                  type: {
                    "java.lang.Double": "numeric",
                  },
                },
                longitude: {
                  alias: "longitude",
                  fullyQualifiedColumn: "geo_cordinates.longitude",
                  id: "20290",
                  defaultFunction: "db.generic.aggregate.sum",
                  type: {
                    "java.lang.Double": "numeric",
                  },
                },
              },
              name: "geo_cordinates",
              cacheId: "be534112989b616b194bc59c2fb25a42",
              key: "8e5a0bf7-b299-4cf0-b480-64cceb965457",
            },
            meeting_details: {
              id: "7644",
              alias: "meeting_details",
              columns: {
                meeting_id: {
                  alias: "meeting_id",
                  fullyQualifiedColumn: "meeting_details.meeting_id",
                  id: "20291",
                  defaultFunction: "db.generic.aggregate.sum",
                  type: {
                    "java.lang.Integer": "numeric",
                  },
                },
                meeting_date: {
                  alias: "meeting_date",
                  fullyQualifiedColumn: "meeting_details.meeting_date",
                  id: "20292",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.sql.Timestamp": "dateTime",
                  },
                },
                meeting_by: {
                  alias: "meeting_by",
                  fullyQualifiedColumn: "meeting_details.meeting_by",
                  id: "20293",
                  defaultFunction: "db.generic.aggregate.sum",
                  type: {
                    "java.lang.Integer": "numeric",
                  },
                },
                client_name: {
                  alias: "client_name",
                  fullyQualifiedColumn: "meeting_details.client_name",
                  id: "20294",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.lang.String": "text",
                  },
                },
                meeting_purpose: {
                  alias: "meeting_purpose",
                  fullyQualifiedColumn: "meeting_details.meeting_purpose",
                  id: "20295",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.lang.String": "text",
                  },
                },
                meeting_impact: {
                  alias: "meeting_impact",
                  fullyQualifiedColumn: "meeting_details.meeting_impact",
                  id: "20296",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.lang.String": "text",
                  },
                },
                meet_cancellation_status: {
                  alias: "meet_cancellation_status",
                  fullyQualifiedColumn:
                    "meeting_details.meet_cancellation_status",
                  id: "20297",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.lang.String": "text",
                  },
                },
                cancellation_reason: {
                  alias: "cancellation_reason",
                  fullyQualifiedColumn: "meeting_details.cancellation_reason",
                  id: "20298",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.lang.String": "text",
                  },
                },
              },
              name: "meeting_details",
              cacheId: "9645c648a1c0dbeec1287aaf1e996db3",
              key: "7c0c8f37-b6ad-4842-954e-018ef4af5cb0",
            },
            travel_details: {
              id: "7645",
              alias: "travel_details",
              columns: {
                travel_id: {
                  alias: "travel_id",
                  fullyQualifiedColumn: "travel_details.travel_id",
                  id: "20299",
                  defaultFunction: "db.generic.aggregate.sum",
                  type: {
                    "java.lang.Integer": "numeric",
                  },
                },
                travel_date: {
                  alias: "travel_date",
                  fullyQualifiedColumn: "travel_details.travel_date",
                  id: "20300",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.sql.Timestamp": "dateTime",
                  },
                },
                travel_type: {
                  alias: "travel_type",
                  fullyQualifiedColumn: "travel_details.travel_type",
                  id: "20301",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.lang.String": "text",
                  },
                },
                travel_medium: {
                  alias: "travel_medium",
                  fullyQualifiedColumn: "travel_details.travel_medium",
                  id: "20302",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.lang.String": "text",
                  },
                },
                source_id: {
                  alias: "source_id",
                  fullyQualifiedColumn: "travel_details.source_id",
                  id: "20303",
                  defaultFunction: "db.generic.aggregate.sum",
                  type: {
                    "java.lang.Integer": "numeric",
                  },
                },
                source: {
                  alias: "source",
                  fullyQualifiedColumn: "travel_details.source",
                  id: "20304",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.lang.String": "text",
                  },
                },
                destination_id: {
                  alias: "destination_id",
                  fullyQualifiedColumn: "travel_details.destination_id",
                  id: "20305",
                  defaultFunction: "db.generic.aggregate.sum",
                  type: {
                    "java.lang.Integer": "numeric",
                  },
                },
                destination: {
                  alias: "destination",
                  fullyQualifiedColumn: "travel_details.destination",
                  id: "20306",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.lang.String": "text",
                  },
                },
                travel_cost: {
                  alias: "travel_cost",
                  fullyQualifiedColumn: "travel_details.travel_cost",
                  id: "20307",
                  defaultFunction: "db.generic.aggregate.sum",
                  type: {
                    "java.lang.Integer": "numeric",
                  },
                },
                mode_of_payment: {
                  alias: "mode_of_payment",
                  fullyQualifiedColumn: "travel_details.mode_of_payment",
                  id: "20308",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.lang.String": "text",
                  },
                },
                booking_platform: {
                  alias: "booking_platform",
                  fullyQualifiedColumn: "travel_details.booking_platform",
                  id: "20309",
                  defaultFunction: "db.generic.groupBy.group",
                  type: {
                    "java.lang.String": "text",
                  },
                },
                travelled_by: {
                  alias: "travelled_by",
                  fullyQualifiedColumn: "travel_details.travelled_by",
                  id: "20310",
                  defaultFunction: "db.generic.aggregate.sum",
                  type: {
                    "java.lang.Integer": "numeric",
                  },
                },
              },
              name: "travel_details",
              cacheId: "8a28627d07d04ef096d9935f12e0c7e9",
              key: "d3028415-a5ab-4e4c-b5d5-cc83b70d34c1",
            },
          },
          sets: [
            [
              "geo_cordinates",
              "dimdate",
              "travel_details",
              "employee_details",
              "meeting_details",
            ],
          ],
          metadataName: "Metadata_1",
          metadataDir: "sai_ganesh",
          formData: {
            location: "sai_ganesh",
            metadataFileName: "Metadata_1.metadata",
          },
          uid: "b0f6cde5-12cd-477c-b805-99532abc89e0",
          defaultexpandedRowKeys: ["d3028415-a5ab-4e4c-b5d5-cc83b70d34c1"],
        },
        metadataLoading: false,
        hreportLoading: false,
        functions: {
          "db.generic.aggregate.avg": "avg",
          "db.generic.aggregate.count": "count",
          "db.generic.aggregate.distinct": "distinct",
          "db.generic.aggregate.max": "max",
          "db.generic.aggregate.min": "min",
          "db.generic.aggregate.sum": "sum",
          "db.generic.groupBy.group": "group by",
          "db.generic.orderBy.order": "order by",
        },
        databaseFunctions: {
          date: [
            {
              key: "sql.date.dateadd",
              description:
                "Returns the specified date with the specified number of interval added to the specified unit of that date.Example:(date({fn timestampadd(SQL_TSI_YEAR, 5, date('2010-09-21'))})) result:2015-09-21 supported units:SQL_TSI_DAY, SQL_TSI_MONTH, SQL_TSI_YEAR.",
              value: "DATEADD",
              signature:
                "(date({fn timestampadd(${unit}, ${value}, date(${date}))}))",
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
                  defaultValue: "2",
                },
                {
                  name: "unit",
                  column: true,
                  defaultValue: "SQL_TSI_YEAR",
                },
              ],
            },
            {
              key: "sql.date.today",
              description: "Displays Current date.",
              value: "TODAY",
              signature: "(CURRENT_DATE)",
              returns: "date",
              parameters: [],
            },
            {
              key: "sql.date.makedate",
              description:
                "Returns a date for given year, month and day. Example: date(char('2019',4)||'-'||char('11',2)||'-'||char('23',2)) result : 2019-17-23",
              value: "MAKEDATE",
              signature: "date(${year}||'-'||${month}||'-'||${day})",
              returns: "date",
              parameters: [
                {
                  name: "year",
                  defaultValue: "'2013'",
                },
                {
                  name: "month",
                  defaultValue: "'7'",
                },
                {
                  name: "day",
                  defaultValue: "'15'",
                },
              ],
            },
            {
              key: "sql.date.datediff",
              description:
                "Returns the difference between date1 and date2 expressed in terms of unit. Example: {fn timestampdiff(SQL_TSI_YEAR, date('2018-03-08'), date('2022-03-08'))} result: 4 supported units:SQL_TSI_YEAR,SQL_TSI_MONTH,SQL_TSI_DAY.",
              value: "DATEDIFF",
              signature:
                "({fn timestampdiff(${unit}, date(${date1}), date(${date2}))})",
              returns: "numeric",
              parameters: [
                {
                  name: "unit",
                  column: true,
                  defaultValue: "SQL_TSI_YEAR",
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
          ],
          dateTime: [
            {
              key: "sql.dateTime.hour",
              description:
                "Return hour for timestamp or a valid timestamp string. Example: hour('2014-03-08 12:20:19') result:12",
              value: "HOUR",
              signature: "hour(${datetime})",
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
              key: "sql.dateTime.maketime",
              description:
                "Returns time value from the hour, minute and seconds.Example:time('11'||':'||'25'||':'||'30') Result:11:25:30\n\t ",
              value: "MAKETIME",
              signature: "time(${hour}||':'||${minute}||':'||${second})",
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
                  defaultValue: "'30'",
                },
                {
                  name: "second",
                  column: true,
                  defaultValue: "'40'",
                },
              ],
            },
            {
              key: "sql.dateTime.makedatetime",
              description:
                "Returns a datetime that combines a year,month,day,hour,minute,second. Example: timestamp('2019'||'-'||'11'||'-'||'22')||' '||'10'||':'||'25'||':'||'22.3') result: 2019-11-22 10:25:22.300000.",
              value: "MAKEDATETIME",
              signature:
                "timestamp(${year}||'-'||${month}||'-'||${day}||' '||${hour}||':'||${minute}||':'||${second})",
              returns: "dateTime",
              parameters: [
                {
                  name: "year",
                  column: true,
                  defaultValue: "'2013'",
                },
                {
                  name: "month",
                  column: true,
                  defaultValue: "'7'",
                },
                {
                  name: "day",
                  column: true,
                  defaultValue: "'15'",
                },
                {
                  name: "hour",
                  column: true,
                  defaultValue: "'8'",
                },
                {
                  name: "minute",
                  column: true,
                  defaultValue: "'15'",
                },
                {
                  name: "second",
                  column: true,
                  defaultValue: "'23.5'",
                },
              ],
            },
            {
              key: "sql.dateTime.datetimediff",
              description:
                "Returns the difference between timestamp1 and timestamp2 expressed in terms of unit. Example:{fn timestampdiff(SQL_TSI_YEAR, timestamp( '2018-03-08 11:10:27'), timestamp('2022-03-08 11:10:27'))} result: 4. supported units:SQL_TSI_YEAR,SQL_TSI_MONTH,SQL_TSI_DAY,SQL_TSI_HOUR,SQL_TSI_MINUTE,SQL_TSI_SECOND.",
              value: "DATETIMEDIFF",
              signature:
                "{fn timestampdiff(${unit}, timestamp(${datetime1}), timestamp(${datetime2}))}",
              returns: "numeric",
              parameters: [
                {
                  name: "unit",
                  column: true,
                  defaultValue: "SQL_TSI_YEAR",
                },
                {
                  name: "datetime1",
                  column: true,
                  defaultValue: "'2014-03-08 09:11:20'",
                },
                {
                  name: "datetime2",
                  column: true,
                  defaultValue: "'2019-03-08 09:08:40'",
                },
              ],
            },
            {
              key: "sql.dateTime.datetimeadd",
              description:
                " Returns the specified datetime with the specified number interval added to the date_part of that datetime. Example:  {fn timestampadd(SQL_TSI_YEAR, 1, timestamp('2010-09-21 10:21:11'))} result:2011-09-21 10:21:11.000000.supported units:SQL_TSI_YEAR,SQL_TSI_MONTH,SQL_TSI_DAY,SQL_TSI_HOUR,SQL_TSI_MINUTE,SQL_TSI_SECOND.",
              value: "DATETIMEADD",
              signature:
                "{fn timestampadd(${unit}, ${value}, timestamp(${datetime}))}",
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
                  defaultValue: "2",
                },
                {
                  name: "unit",
                  column: true,
                  defaultValue: "SQL_TSI_YEAR",
                },
              ],
            },
            {
              key: "sql.dateTime.month",
              description:
                "Returns the month of the year for date/datetime. Example: month('2007-02-03 09:00:00')/month('2007-02-03') result:2",
              value: "MONTH",
              signature: "month(${datetime})",
              returns: "numeric",
              parameters: [
                {
                  name: "datetime",
                  column: true,
                  defaultValue: "'2007-02-03 09:12:30'",
                },
              ],
            },
            {
              key: "sql.dateTime.minute",
              description:
                "Returns minute for timestamp or a valid timestamp string. Example: minute('2014-03-08 12:20:19') result: 20",
              value: "MINUTE",
              signature: "minute(${datetime})",
              returns: "numeric",
              parameters: [
                {
                  name: "datetime",
                  column: true,
                  defaultValue: "'2007-02-03 09:12:30'",
                },
              ],
            },
            {
              key: "sql.dateTime.second",
              description:
                "Returns the seconds for timestamp or a valid timestamp string. Example:second('2014-03-08 12:20:19'). result: 19. NOTE:If the argument is a timestamp: The result will contains fraction of seconds along with second.",
              value: "SECOND",
              signature: "second(${datetime})",
              returns: "numeric",
              parameters: [
                {
                  name: "datetime",
                  column: true,
                  defaultValue: "'2007-02-03 09:12:30'",
                },
              ],
            },
            {
              key: "sql.dateTime.quarter",
              description:
                "Returns the quarter of the year for date/datetime. Example: quarter('2014-03-08 12:20:19') result:1",
              value: "QUARTER",
              signature:
                "(CASE MONTH(${datetime}) WHEN < 4 THEN 1 WHEN BETWEEN 4 AND 6 then 2 WHEN BETWEEN 7 AND 9 then 3 WHEN BETWEEN 10 AND 12 then 4 END)",
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
              key: "sql.dateTime.day",
              description:
                "Returns day of the month for date/datetime. Example: day('2014-03-08 09:00:00')/day('2014-03-08') result: 3",
              value: "DAY",
              signature: "day(${datetime})",
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
                "Returns the month name based on the given date/datetime. Example: monthname('2014-08-08 08:00:00.000') result: August ",
              value: "MONTHNAME",
              signature:
                "(case when (month(${datetime})=01 OR month(${datetime})=1) then 'January' \n\t     when (month(${datetime})=02 OR month(${datetime})=2) then 'February'\n\t     when (month(${datetime})=03 OR month(${datetime})=3) then 'March'\n\t     when (month(${datetime})=04 OR month(${datetime})=4) then 'April'\n\t     when (month(${datetime})=05 OR month(${datetime})=5) then 'May'\n\t     when (month(${datetime})=06 OR month(${datetime})=6) then 'June'\n\t     when (month(${datetime})=07 OR month(${datetime})=7) then 'July'\n\t     when (month(${datetime})=08 OR month(${datetime})=8) then 'August'\n\t     when (month(${datetime})=09 OR month(${datetime})=9) then 'September'\n\t     when (month(${datetime})=10) then 'October'\n\t     when (month(${datetime})=11) then 'November'\n\t     when (month(${datetime})=12) then 'December'\n\t     else null end)",
              returns: "text",
              parameters: [
                {
                  name: "datetime",
                  column: true,
                  defaultValue: "'2014-08-08 08:00:00.000'",
                },
              ],
            },
            {
              key: "sql.dateTime.now",
              description:
                "Displays Current date and time. This function equivalent to current_timestamp.",
              value: "NOW",
              signature: "(CURRENT_TIMESTAMP)",
              returns: "dateTime",
              parameters: [],
            },
            {
              key: "sql.dateTime.year",
              description:
                "Return year for date/dateTime. Example: year('2014-03-08 09:00:00')/year('2014-03-08') result: 2014",
              value: "YEAR",
              signature: "year(${datetime})",
              returns: "numeric",
              parameters: [
                {
                  name: "datetime",
                  column: true,
                  defaultValue: "'2007-02-03 09:00:00'",
                },
              ],
            },
          ],
          "type conversion": [
            {
              key: "sql.typeConversion.cast",
              description:
                "Cast function converts one dataType to another datatype. Note:All Values should be in single quotes if user provide's value.Example: CAST('2019-03-22 17:34:03.000' AS varchar(23)) result:2019-03-22 17:34:03.0",
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
                "Converts value to char type. NOTE:field should be in single quotes if you are typing manually.Example1:char(date('2019-11-22'))result:2019-11-22 Example2:char(12345) result:'12345'",
              value: "TOCHAR",
              signature: "CHAR(${column})",
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
                "This function is used to convert character based integer value to integer type.(format is not required)Example:BIGINT('456') result:456",
              value: "TONUM",
              signature: "BIGINT(${column})",
              returns: "numeric",
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
                "This function is used to convert character based decimal value to decimal type.(format is not required)Example:DOUBLE('456.34') result:456.34",
              value: "TODECIMAL",
              signature: "DOUBLE(${column})",
              returns: "numeric",
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
                "This function is used to convert character based date value to date type.(format is not required)Example:CAST('2018-08-30' as DATE)) result:2018-08-30",
              value: "TODATE",
              signature: "CAST(${column} AS DATE)",
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
                "This function is used to convert character based dateTime value to dateTime type.(format is not required)Example:CAST('2018-08-30 10:15:30' as TIMESTAMP)) result:2018-08-30 10:15:30",
              value: "TODATETIME",
              signature: "CAST(${column} AS TIMESTAMP)",
              returns: "dateTime",
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
                "This function is used to convert character based time value to time type.(format is not required) Example:CAST('10:15:30' as TIME)) result:10:15:30",
              value: "TOTIME",
              signature: "CAST(${column} AS TIME)",
              returns: "time",
              parameters: [
                {
                  name: "column",
                  column: true,
                },
              ],
            },
          ],
          "derby specific": [
            {
              key: "sql.text.dateToString",
              description: "Converts the date to string",
              value: "dateToString",
              signature: "CAST (${column} AS VARCHAR(100))",
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
              description: "Converts the time to string",
              value: "numericToString",
              signature: "CAST (${column} AS CHAR(100))",
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
              signature: "CAST (${column} AS VARCHAR(100))",
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
              signature: "CAST (${column} AS VARCHAR(100))",
              returns: "text",
              parameters: [
                {
                  name: "column",
                  column: true,
                },
              ],
            },
            {
              key: "sql.text.date",
              description: "Extracts the date from the date and time value",
              value: "date",
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
              key: "sql.date.monthyear",
              description: "Displays month and year in (month-year) format",
              value: "month-year",
              signature:
                "CAST(month(${column}) AS CHAR(20) )||  '-'  ||CAST(YEAR(${column}) AS CHAR(20) )",
              returns: "text",
              parameters: [
                {
                  name: "column",
                  column: true,
                  defaultValue: "0",
                },
              ],
            },
            {
              key: "sql.dateTime.currenttime",
              description:
                "The CURRENT_TIME function returns the current time.",
              value: "currenttime",
              signature: "(VALUES CURRENT_TIME)",
              returns: "time",
              parameters: [],
            },
          ],
          numeric: [
            {
              key: "sql.numeric.abs",
              description:
                "Returns the absolute value of a number. Example:abs(-24) result:24",
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
              key: "sql.numeric.floor",
              description:
                "Returns number rounded down to the nearest number. Example:floor(3.1415) result:3",
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
              key: "sql.numeric.ceiling",
              description:
                "Returns number rounded up to the nearest integer. Example:ceiling(0.25) result:1",
              value: "CEILING",
              signature: "CEILING(${number})",
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
                "Returns the cotangent of an angle. Specify the angle in radians. Example: 1/tan(0.25) result: 3.9163173646459399 ",
              value: "COT",
              signature: "1/tan(${number})",
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
                  name: "dividend",
                  column: true,
                },
                {
                  name: "divisor",
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
              key: "sql.numeric.ln",
              description:
                "Returns the natural logarithm of number. Example: ln(2) result: 0.6931471805599453 ",
              value: "LN",
              signature: "ln(${number})",
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
                "Returns the base 10 logarithm of number. Example: log(2) result: 0.3010299956639812 ",
              value: "LOG",
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
              key: "sql.numeric.pi",
              description:
                "Returns the constant Pi. Example: pi() result: 3.14159 ",
              value: "PI",
              signature: "pi()",
              returns: "numeric",
              parameters: [],
            },
            {
              key: "sql.numeric.radians",
              description:
                "Converts given number from degrees to radians. Example : radians(4) result : 0.06981317007977318 ",
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
              key: "sql.numeric.sign",
              description:
                "Returns the signum function of number, that is:\n\t\t0 if the argument is 0,\n\t\t1 if the argument is greater than 0,\n\t\t-1 if the argument is less than 0. Example: sign(0.5) result: 1.",
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
              key: "sql.numeric.sqrt",
              description:
                "It displays the square root of a positive number. Example: sqrt(5) result: 2.23606797749979",
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
              signature: "(${number} * ${number})",
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
          ],
          text: [
            {
              key: "sql.text.concat",
              description:
                "Returns the concatenation of string1, string2. Example:('Beng'||'aluru') result: Bengaluru ",
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
            {
              key: "sql.text.contains",
              description:
                "Returns true if the given string contains the specified substring. Example: case when(locate('g', 'Bengaluru'))>0 then true else false end result: true ",
              value: "CONTAINS",
              signature:
                "(case when(locate(${substring}, ${string}))>0 then true else false end)",
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
              key: "sql.text.endswith",
              description:
                "Returns true if the given string endswith specified substring. Example:case when('postgres' like ('%'||'res')) then true else false end result: true. Note:Please provide single quotes if you are directly typing the substring value.",
              value: "ENDSWITH",
              signature:
                "case when(${string} like ('%'||${substring})) then true else false end",
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
                "Returns true if string starts with substring. Example: case when('bengaluru' like ('ben'||'%')) then true else false end result: true.  Note:Please provide single quotes if you are directly typing the substring value.",
              value: "STARTSWITH",
              signature:
                "case when(${string} like (${substring}||'%')) then true else false end",
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
              key: "sql.text.find",
              description:
                "Returns the starting position of the first instance of substring in string. Positions start with 1. If not found, 0 is returned. Example :locate('z' in 'Bengaluru') result : 0, locate('aluru' in 'Bengaluru') result : 5",
              value: "FIND",
              signature: "locate(${substring},${string})",
              returns: "numeric",
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
              key: "sql.text.left",
              description:
                "Returns the left most (length) character from the string . Example: substr('bengaluru',1, 4) result: beng",
              value: "LEFT",
              signature: "trim(substr(${string},1, ${length}))",
              returns: "text",
              parameters: [
                {
                  name: "string",
                  column: true,
                },
                {
                  name: "length",
                  column: true,
                  defaultValue: "0",
                },
              ],
            },
            {
              key: "sql.text.length",
              description:
                "Returns the number of characters in text. Example: length('Bengaluru') result:9",
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
                "Converts all characters in the specified string to lowercase. Example: LOWER('BENGALURU') result: bengaluru ",
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
                "Converts all characters in a string to uppercase. Example: UPPER('bengaluru') result: BENGALURU",
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
              key: "sql.text.right",
              description:
                "Returns the rightmost character from the string. If length is negative extract all the characters from the right side except 3 leftmost characters Example: (case when 4 > length('bengaluru') then 'bengaluru' else substr('bengaluru',length('bengaluru')-(4-1),4) end) result: 'luru'.NOTE:if the provided length is grater than the length of the string then the whole string will be returned.",
              value: "RIGHT",
              signature:
                "(case when ${length} > length(${string}) then ${string} else trim(substr(${string},length(${string})-(${length}-1),${length})) end)",
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
              key: "sql.text.ltrim",
              description:
                " Removes leading whitespace from string Example: LTRIM(' Bengaluru') result: Bengaluru\n        ",
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
              key: "sql.text.mid",
              description:
                "Returns the text starting from specified position. If position is more than string or length is less than 1 it will return empty string. Example: substr('bengaluru',2,5); result: engal",
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
              key: "sql.text.rtrim",
              description:
                "Removes trailing whitespace from string. Example: RTRIM('Bengaluru  ') Result: Bengaluru ",
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
              key: "sql.text.trim",
              description:
                "Removes whitespace from string. Example:TRIM(' Bengaluru ') result: Bengaluru\n        ",
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
          ],
          logical: [
            {
              key: "sql.logical.and",
              description:
                "Inside IF we will use AND. performs a logical conjunction on two expressions.\n            In 'column' paramter we will 'drag column'.\n\t\t\tIn 'condition' parameter provide conditions like (>,= etc.,like).\n\t\t\tIn 'value' parameter provide condition value.\n\t\t\tIn 'moreconditions' parameter we will use nested 'OR' condition , 'AND' conditions. \n\t\t\tExample: CASE WHEN 'Washington' like '%sh%' \n             AND 'Washington' like 'W%' THEN 'returnl washington' \n             else 'NotMatched' end  ",
              value: "AND",
              signature:
                "AND (${column} ${condition} ${value}) ${moreconditions} ",
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
                "Inside case we will use when condition. Evaluates each condition from left to right and returns the result when the first condition met. If no condition met return from else if exist, otherwise return null. Example : CASE WHEN Quantity > 30 THEN 'The quantity is greater than 30'  ELSE 'The quantity is under 30' END ",
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
                "Returns from statement_list when condition gets fail.We will use ELSE inside case function. Example: CASE when 50 > 0 then 'true' else 'false' end",
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
                "Evaluates conditions and returns 'conditiontrue' if condition is true, otherwise return 'conditionfalse'.  We will use nested condition inside else 'conditiontrue' parameter. Example:case when creditlim > 50000 then 'PLATINUM' when (creditlim > = 50000) then 'GOLD' else 'SILVER' end ",
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
                "Inside IF we will use AND, OR conditions. Evaluates and returns 'conditiontrue' if condition is true, otherwise return 'conditionfalse'. \n\t\t\tIn 'column' paramter we will drag column.\n\t\t\tIn 'condition' parameter provide conditions like (>,= etc.,).\n\t\t\tIn 'value' parameter provide value.\n\t\t\tIn 'moreconditions' parameter we will use nested 'OR', 'AND' conditions. Instead of dragging column directly we will write expression in column parameter like 50 > 0 (Note : In such case don't provide anything in 'condition' parameter and 'value' parameter). Example : case when (creditlim > 50000) then 'PLATINUM' else 'SILVER' end",
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
                "Returns Expr1 if it is not null otherwise return expr2. Example : coalesce(profit, 0).NOTE:Manually entered null will not work it should be part of column, datatype of both the expressions should be match.",
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
                "Inside WHEN condition we will use ISNULL. Evalutes and returns 'Conditiontrue' if the expression contain Null. Example1 : CASE WHEN 1 ISNULL THEN Conditionfalse. Example2 : CASE WHEN NULL ISNULL THEN Conditiontrue. NOTE: Manually entered null will not work it should be part of column.",
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
              signature: "(NOT(${column} ${condition} ${value}))",
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
                "Inside IF we will use OR.Performs a logical disjunction on two expressions. \n\t\t\tIn 'column' paramter we will drag column.\n\t\t\tIn 'condition' parameter provide conditions like (>,= etc.,).\n\t\t\tIn 'value' parameter provide value.\n\t\t\tIn 'moreconditions' parameter we will use nested 'OR' , 'AND' functions. Example : CASE WHEN 'Washington' like '%sh%' \n             OR  'Washington' like 'W%' THEN 'return washington' \n             else 'NotMatched' end",
              value: "OR",
              signature:
                " OR ${column} ${condition} ${value} ${moreconditions}",
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
                "Returns 'statement_list' when condition get satisfied .\n\t\t\tIn column paramter we will drag column.\n\t\t\tIn searchcondition parameter provide conditions like (>, =, IS Null etc .,).\n\t\t\tIn value parameter provide value(Note : IS Null used in 'condition' parameter then don't provide anything in 'value' parameter). \n\t\t\tIn moreconditions parameter we will use nested when conditions, Else condition . We will use WHEN inside CASE. Example1 : CASE WHEN 1 > 0  THEN 'one' else 'TWO' end. Example2 : CASE WHEN 'Singapore' IS NULL THEN 'Singa' ELSE 'pore'. Example3 : CASE WHEN Washington like '%sh%' THEN 'return washington' else 'NotMatched' end",
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
                "Returns \"expression\" if it is not null, otherwise returns zero.Example :(CASE WHEN '123' IS NULL THEN '0' ELSE '123' end) result :0 ",
              value: "ZN",
              signature:
                "(CASE WHEN ${expr} IS NULL THEN '0' ELSE ${expr} end)",
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
        fields: [
          {
            column: "travel_details.travel_date",
            columnID: "20300",
            label: "travel_date",
            id: "6c0e1599-443d-4beb-ac23-9f41c97121fa",
            type: {
              backendDataType: "java.sql.Timestamp",
              dataType: "dateTime",
            },
            autogen_alias: "travel_date",
            isNormalTable: true,
            tableAlias: "travel_details",
            groupBy: ["db.generic.groupBy.group"],
            orderByColumn: false,
            showOrderByColumn: false,
            addedAs: "column",
            floatingType: "discrete",
            functionsDefinition: "",
            applyBeforeAggregate: false,
            hiddenIncludeInResultSet: false,
            metaDataAlias: "travel_date",
            databaseName: "HIUSER",
          },
        ],
        filters: [
          {
            column: "travel_details.travel_date",
            label: "travel_date",
            databaseFunction: null,
            dataType: "dateTime",
            backendDataType: "java.sql.Timestamp",
            condition: "EQUALS",
            values: ["2023-05-19 13:22:23.8"],
            valuesMode: "custom",
            mode: "auto",
            groupBy: ["db.generic.groupBy.group"],
            orderBy: "",
            valuesRange: {},
            rangeValuesType: "",
            dateTimeToggle: false,
            rangeSelectionToggole: true,
            maxInput: "",
            minInput: "",
            valuesList: [],
            drillDownId: "",
            uid: "b08de6fb-bd7b-4e6d-973f-ee5225437c9e",
            configId: "26212490-f310-44bb-b619-6ca7cbc3b877",
            dataId: "28c83088-9172-42f3-a3a6-85b60605997c",
            columnID: "20300",
            datePart: "individual",
            currentDate:
              "Fri May 19 2023 13:22:23 GMT+0530 (India Standard Time)",
            dateValuesType: "relative-list",
            anchor: {
              anchorDate: "2023-05-19 13:22:23",
              isAnchor: false,
              active: 1,
              relativePart: "",
              part: "",
              value: 0,
              direction: "",
              lastInput: 3,
              nextInput: 3,
            },
            mapping: {
              isEnabled: true,
              unique: true,
              valueDBFunction: null,
              DisplayDBFunction: null,
              isDefaultFunction: true,
              valueDisplayMap: [],
              valueAliasName: "random",
              orderBy: {
                display: "asc",
                value: "none",
              },
              valueDBFuntionInfo: {},
              valueColumn: {
                alias: "travel_date",
                fullyQualifiedColumn: "travel_details.travel_date",
                id: "20300",
                defaultFunction: "db.generic.groupBy.group",
                type: {
                  "java.sql.Timestamp": "dateTime",
                },
              },
              displayColumn: {
                alias: "travel_date",
                fullyQualifiedColumn: "travel_details.travel_date",
                id: "20300",
                defaultFunction: "db.generic.groupBy.group",
                type: {
                  "java.sql.Timestamp": "dateTime",
                },
              },
            },
            cascade: {
              isEnabled: false,
              filters: [],
              filtersCount: 0,
            },
            active: true,
            reportId: "7273b8af-4577-4715-af9d-9952b9ebd3b4",
          },
        ],
        defaultValueDisplayMap: {},
        editingField: null,
        marksList: [
          {
            value: "_all_",
            id: "a4abd38b-1075-4d12-94b9-0257df3c0150",
            subVizType: "text",
            color: {
              fields: [],
            },
            size: {
              fields: [],
            },
            label: {
              fields: [],
            },
            tooltip: {
              fields: [],
            },
            shape: {
              fields: [],
            },
            detail: {
              fields: [],
            },
          },
        ],
        activeMark: "a4abd38b-1075-4d12-94b9-0257df3c0150",
        activeTool: "2",
        scripts: [
          {
            id: "pre-execution",
            value: "",
            title: "Pre Execution",
          },
          {
            id: "pre-fetch",
            value: "",
            title: "Pre Fetch",
          },
          {
            id: "post-fetch",
            value: "",
            title: "Post Fetch",
          },
          {
            id: "post-execution",
            value: "",
            title: "Post Execution",
          },
        ],
        selectedScript: "pre-execution",
        styles: "",
        stylesId: "hi-report-7273b8af",
        savedStyles: "",
        sqlString: "",
        options: {
          limitBy: 1000,
          sample: "sample",
          prependTableNameToAlias: false,
        },
        interactiveMode: false,
        drillDown: false,
        drillThrough: false,
        drillDownList: [],
        currentDrillDown: "",
        drillThroughList: [],
        toolbarConfig: {
          selectable: false,
        },
        selectedType: "Antcharts",
        reportData: {
          metadata: [
            {
              1: {
                name: "travel_date",
                type: "dateTime",
              },
            },
            {
              rows: 996,
            },
          ],
          metadata_file: {
            location: "sai_ganesh",
            metadataFileName: "Metadata_1.metadata",
          },
          database: "HIUSER",
          fields: [
            {
              column: "travel_details.travel_date",
              columnID: "20300",
              label: "travel_date",
              id: "6c0e1599-443d-4beb-ac23-9f41c97121fa",
              type: {
                backendDataType: "java.sql.Timestamp",
                dataType: "dateTime",
              },
              autogen_alias: "travel_date",
              isNormalTable: true,
              tableAlias: "travel_details",
              groupBy: ["db.generic.groupBy.group"],
              orderByColumn: false,
              showOrderByColumn: false,
              addedAs: "column",
              floatingType: "discrete",
              functionsDefinition: "",
              applyBeforeAggregate: false,
              hiddenIncludeInResultSet: false,
              metaDataAlias: "travel_date",
              databaseName: "HIUSER",
            },
          ],
          rows: [],
          columns: ["travel_details.travel_date"],
          filters: [],
          mark_fields: [],
          marks: [
            {
              value: "_all_",
              id: "a4abd38b-1075-4d12-94b9-0257df3c0150",
              subVizType: "",
              color: {
                fields: [],
              },
              size: {
                fields: [],
              },
              label: {
                fields: [],
              },
              tooltip: {
                fields: [],
              },
              shape: {
                fields: [],
              },
              detail: {
                fields: [],
              },
            },
          ],
          marksList: [
            {
              value: "_all_",
              id: "a4abd38b-1075-4d12-94b9-0257df3c0150",
              subVizType: "",
              color: {
                fields: [],
              },
              size: {
                fields: [],
              },
              label: {
                fields: [],
              },
              tooltip: {
                fields: [],
              },
              shape: {
                fields: [],
              },
              detail: {
                fields: [],
              },
            },
          ],
          visualisation: "Antcharts",
          properties: {
            title: {
              show: false,
              value: "",
              padding: 0,
              fontSize: 32,
              fontColor: {
                a: 1,
                b: 0,
                g: 0,
                r: 0,
              },
              alignment: "center",
              position: "top",
            },
            subTitle: {
              show: false,
              value: "",
              padding: 0,
              fontSize: 24,
              fontColor: {
                a: 1,
                b: 0,
                g: 0,
                r: 0,
              },
              alignment: "center",
              position: "top",
            },
            format: {
              formatFields: [],
              formatDatatype: "",
              activeFieldId: "",
              showAll: false,
            },
            axisRange: {
              fields: [],
              activeDatatype: "",
              activeId: "",
              gridLines: [],
            },
            cache: {
              isCacheEnabled: false,
              interval: "00:00:01",
            },
            card: {
              title: "",
              prefixType: "selectIcon",
              suffixType: "selectIcon",
              prefix: "",
              suffix: "",
              prefixColor: {
                a: 1,
                b: 0,
                g: 0,
                r: 0,
              },
              suffixColor: {
                a: 1,
                b: 0,
                g: 0,
                r: 0,
              },
            },
            bar: {
              barType: "stacked",
            },
            radial: {
              showRadial: false,
            },
            legend: {
              legendPosition: "right",
            },
            formatColor: {
              defaultColor: {
                r: 84,
                g: 108,
                b: 230,
                a: 1,
              },
              showAll: false,
              dataColors: [],
              formatColorStyle: "",
              formatColorField: "",
              minimum: {
                r: 183,
                g: 192,
                b: 232,
                a: 1,
              },
              maximum: {
                r: 84,
                g: 108,
                b: 230,
                a: 1,
              },
              backgroundColor: false,
            },
            labels: {
              rotateLabels: false,
            },
            crosstab: {
              showGrandTotals: false,
              showRowGrandTotals: false,
              showColumnGrandTotals: false,
              showSubTotals: false,
              showRowSubTotals: false,
              showColumnSubTotals: false,
              grandTotalsPosition: "Bottom",
              subTotalsPosition: "Auto",
            },
          },
          settings: {
            limitBy: 1000,
            sample: "sample",
            prependTableNameToAlias: false,
          },
          options: {
            limitBy: 1000,
            sample: "sample",
            prependTableNameToAlias: false,
          },
          defaultValueDisplayMap: {},
          databaseFunctions: {
            date: [
              {
                key: "sql.date.dateadd",
                description:
                  "Returns the specified date with the specified number of interval added to the specified unit of that date.Example:(date({fn timestampadd(SQL_TSI_YEAR, 5, date('2010-09-21'))})) result:2015-09-21 supported units:SQL_TSI_DAY, SQL_TSI_MONTH, SQL_TSI_YEAR.",
                value: "DATEADD",
                signature:
                  "(date({fn timestampadd(${unit}, ${value}, date(${date}))}))",
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
                    defaultValue: "2",
                  },
                  {
                    name: "unit",
                    column: true,
                    defaultValue: "SQL_TSI_YEAR",
                  },
                ],
              },
              {
                key: "sql.date.today",
                description: "Displays Current date.",
                value: "TODAY",
                signature: "(CURRENT_DATE)",
                returns: "date",
                parameters: [],
              },
              {
                key: "sql.date.makedate",
                description:
                  "Returns a date for given year, month and day. Example: date(char('2019',4)||'-'||char('11',2)||'-'||char('23',2)) result : 2019-17-23",
                value: "MAKEDATE",
                signature: "date(${year}||'-'||${month}||'-'||${day})",
                returns: "date",
                parameters: [
                  {
                    name: "year",
                    defaultValue: "'2013'",
                  },
                  {
                    name: "month",
                    defaultValue: "'7'",
                  },
                  {
                    name: "day",
                    defaultValue: "'15'",
                  },
                ],
              },
              {
                key: "sql.date.datediff",
                description:
                  "Returns the difference between date1 and date2 expressed in terms of unit. Example: {fn timestampdiff(SQL_TSI_YEAR, date('2018-03-08'), date('2022-03-08'))} result: 4 supported units:SQL_TSI_YEAR,SQL_TSI_MONTH,SQL_TSI_DAY.",
                value: "DATEDIFF",
                signature:
                  "({fn timestampdiff(${unit}, date(${date1}), date(${date2}))})",
                returns: "numeric",
                parameters: [
                  {
                    name: "unit",
                    column: true,
                    defaultValue: "SQL_TSI_YEAR",
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
            ],
            dateTime: [
              {
                key: "sql.dateTime.hour",
                description:
                  "Return hour for timestamp or a valid timestamp string. Example: hour('2014-03-08 12:20:19') result:12",
                value: "HOUR",
                signature: "hour(${datetime})",
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
                key: "sql.dateTime.maketime",
                description:
                  "Returns time value from the hour, minute and seconds.Example:time('11'||':'||'25'||':'||'30') Result:11:25:30\n\t ",
                value: "MAKETIME",
                signature: "time(${hour}||':'||${minute}||':'||${second})",
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
                    defaultValue: "'30'",
                  },
                  {
                    name: "second",
                    column: true,
                    defaultValue: "'40'",
                  },
                ],
              },
              {
                key: "sql.dateTime.makedatetime",
                description:
                  "Returns a datetime that combines a year,month,day,hour,minute,second. Example: timestamp('2019'||'-'||'11'||'-'||'22')||' '||'10'||':'||'25'||':'||'22.3') result: 2019-11-22 10:25:22.300000.",
                value: "MAKEDATETIME",
                signature:
                  "timestamp(${year}||'-'||${month}||'-'||${day}||' '||${hour}||':'||${minute}||':'||${second})",
                returns: "dateTime",
                parameters: [
                  {
                    name: "year",
                    column: true,
                    defaultValue: "'2013'",
                  },
                  {
                    name: "month",
                    column: true,
                    defaultValue: "'7'",
                  },
                  {
                    name: "day",
                    column: true,
                    defaultValue: "'15'",
                  },
                  {
                    name: "hour",
                    column: true,
                    defaultValue: "'8'",
                  },
                  {
                    name: "minute",
                    column: true,
                    defaultValue: "'15'",
                  },
                  {
                    name: "second",
                    column: true,
                    defaultValue: "'23.5'",
                  },
                ],
              },
              {
                key: "sql.dateTime.datetimediff",
                description:
                  "Returns the difference between timestamp1 and timestamp2 expressed in terms of unit. Example:{fn timestampdiff(SQL_TSI_YEAR, timestamp( '2018-03-08 11:10:27'), timestamp('2022-03-08 11:10:27'))} result: 4. supported units:SQL_TSI_YEAR,SQL_TSI_MONTH,SQL_TSI_DAY,SQL_TSI_HOUR,SQL_TSI_MINUTE,SQL_TSI_SECOND.",
                value: "DATETIMEDIFF",
                signature:
                  "{fn timestampdiff(${unit}, timestamp(${datetime1}), timestamp(${datetime2}))}",
                returns: "numeric",
                parameters: [
                  {
                    name: "unit",
                    column: true,
                    defaultValue: "SQL_TSI_YEAR",
                  },
                  {
                    name: "datetime1",
                    column: true,
                    defaultValue: "'2014-03-08 09:11:20'",
                  },
                  {
                    name: "datetime2",
                    column: true,
                    defaultValue: "'2019-03-08 09:08:40'",
                  },
                ],
              },
              {
                key: "sql.dateTime.datetimeadd",
                description:
                  " Returns the specified datetime with the specified number interval added to the date_part of that datetime. Example:  {fn timestampadd(SQL_TSI_YEAR, 1, timestamp('2010-09-21 10:21:11'))} result:2011-09-21 10:21:11.000000.supported units:SQL_TSI_YEAR,SQL_TSI_MONTH,SQL_TSI_DAY,SQL_TSI_HOUR,SQL_TSI_MINUTE,SQL_TSI_SECOND.",
                value: "DATETIMEADD",
                signature:
                  "{fn timestampadd(${unit}, ${value}, timestamp(${datetime}))}",
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
                    defaultValue: "2",
                  },
                  {
                    name: "unit",
                    column: true,
                    defaultValue: "SQL_TSI_YEAR",
                  },
                ],
              },
              {
                key: "sql.dateTime.month",
                description:
                  "Returns the month of the year for date/datetime. Example: month('2007-02-03 09:00:00')/month('2007-02-03') result:2",
                value: "MONTH",
                signature: "month(${datetime})",
                returns: "numeric",
                parameters: [
                  {
                    name: "datetime",
                    column: true,
                    defaultValue: "'2007-02-03 09:12:30'",
                  },
                ],
              },
              {
                key: "sql.dateTime.minute",
                description:
                  "Returns minute for timestamp or a valid timestamp string. Example: minute('2014-03-08 12:20:19') result: 20",
                value: "MINUTE",
                signature: "minute(${datetime})",
                returns: "numeric",
                parameters: [
                  {
                    name: "datetime",
                    column: true,
                    defaultValue: "'2007-02-03 09:12:30'",
                  },
                ],
              },
              {
                key: "sql.dateTime.second",
                description:
                  "Returns the seconds for timestamp or a valid timestamp string. Example:second('2014-03-08 12:20:19'). result: 19. NOTE:If the argument is a timestamp: The result will contains fraction of seconds along with second.",
                value: "SECOND",
                signature: "second(${datetime})",
                returns: "numeric",
                parameters: [
                  {
                    name: "datetime",
                    column: true,
                    defaultValue: "'2007-02-03 09:12:30'",
                  },
                ],
              },
              {
                key: "sql.dateTime.quarter",
                description:
                  "Returns the quarter of the year for date/datetime. Example: quarter('2014-03-08 12:20:19') result:1",
                value: "QUARTER",
                signature:
                  "(CASE MONTH(${datetime}) WHEN < 4 THEN 1 WHEN BETWEEN 4 AND 6 then 2 WHEN BETWEEN 7 AND 9 then 3 WHEN BETWEEN 10 AND 12 then 4 END)",
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
                key: "sql.dateTime.day",
                description:
                  "Returns day of the month for date/datetime. Example: day('2014-03-08 09:00:00')/day('2014-03-08') result: 3",
                value: "DAY",
                signature: "day(${datetime})",
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
                  "Returns the month name based on the given date/datetime. Example: monthname('2014-08-08 08:00:00.000') result: August ",
                value: "MONTHNAME",
                signature:
                  "(case when (month(${datetime})=01 OR month(${datetime})=1) then 'January' \n\t     when (month(${datetime})=02 OR month(${datetime})=2) then 'February'\n\t     when (month(${datetime})=03 OR month(${datetime})=3) then 'March'\n\t     when (month(${datetime})=04 OR month(${datetime})=4) then 'April'\n\t     when (month(${datetime})=05 OR month(${datetime})=5) then 'May'\n\t     when (month(${datetime})=06 OR month(${datetime})=6) then 'June'\n\t     when (month(${datetime})=07 OR month(${datetime})=7) then 'July'\n\t     when (month(${datetime})=08 OR month(${datetime})=8) then 'August'\n\t     when (month(${datetime})=09 OR month(${datetime})=9) then 'September'\n\t     when (month(${datetime})=10) then 'October'\n\t     when (month(${datetime})=11) then 'November'\n\t     when (month(${datetime})=12) then 'December'\n\t     else null end)",
                returns: "text",
                parameters: [
                  {
                    name: "datetime",
                    column: true,
                    defaultValue: "'2014-08-08 08:00:00.000'",
                  },
                ],
              },
              {
                key: "sql.dateTime.now",
                description:
                  "Displays Current date and time. This function equivalent to current_timestamp.",
                value: "NOW",
                signature: "(CURRENT_TIMESTAMP)",
                returns: "dateTime",
                parameters: [],
              },
              {
                key: "sql.dateTime.year",
                description:
                  "Return year for date/dateTime. Example: year('2014-03-08 09:00:00')/year('2014-03-08') result: 2014",
                value: "YEAR",
                signature: "year(${datetime})",
                returns: "numeric",
                parameters: [
                  {
                    name: "datetime",
                    column: true,
                    defaultValue: "'2007-02-03 09:00:00'",
                  },
                ],
              },
            ],
            "type conversion": [
              {
                key: "sql.typeConversion.cast",
                description:
                  "Cast function converts one dataType to another datatype. Note:All Values should be in single quotes if user provide's value.Example: CAST('2019-03-22 17:34:03.000' AS varchar(23)) result:2019-03-22 17:34:03.0",
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
                  "Converts value to char type. NOTE:field should be in single quotes if you are typing manually.Example1:char(date('2019-11-22'))result:2019-11-22 Example2:char(12345) result:'12345'",
                value: "TOCHAR",
                signature: "CHAR(${column})",
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
                  "This function is used to convert character based integer value to integer type.(format is not required)Example:BIGINT('456') result:456",
                value: "TONUM",
                signature: "BIGINT(${column})",
                returns: "numeric",
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
                  "This function is used to convert character based decimal value to decimal type.(format is not required)Example:DOUBLE('456.34') result:456.34",
                value: "TODECIMAL",
                signature: "DOUBLE(${column})",
                returns: "numeric",
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
                  "This function is used to convert character based date value to date type.(format is not required)Example:CAST('2018-08-30' as DATE)) result:2018-08-30",
                value: "TODATE",
                signature: "CAST(${column} AS DATE)",
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
                  "This function is used to convert character based dateTime value to dateTime type.(format is not required)Example:CAST('2018-08-30 10:15:30' as TIMESTAMP)) result:2018-08-30 10:15:30",
                value: "TODATETIME",
                signature: "CAST(${column} AS TIMESTAMP)",
                returns: "dateTime",
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
                  "This function is used to convert character based time value to time type.(format is not required) Example:CAST('10:15:30' as TIME)) result:10:15:30",
                value: "TOTIME",
                signature: "CAST(${column} AS TIME)",
                returns: "time",
                parameters: [
                  {
                    name: "column",
                    column: true,
                  },
                ],
              },
            ],
            "derby specific": [
              {
                key: "sql.text.dateToString",
                description: "Converts the date to string",
                value: "dateToString",
                signature: "CAST (${column} AS VARCHAR(100))",
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
                description: "Converts the time to string",
                value: "numericToString",
                signature: "CAST (${column} AS CHAR(100))",
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
                signature: "CAST (${column} AS VARCHAR(100))",
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
                signature: "CAST (${column} AS VARCHAR(100))",
                returns: "text",
                parameters: [
                  {
                    name: "column",
                    column: true,
                  },
                ],
              },
              {
                key: "sql.text.date",
                description: "Extracts the date from the date and time value",
                value: "date",
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
                key: "sql.date.monthyear",
                description: "Displays month and year in (month-year) format",
                value: "month-year",
                signature:
                  "CAST(month(${column}) AS CHAR(20) )||  '-'  ||CAST(YEAR(${column}) AS CHAR(20) )",
                returns: "text",
                parameters: [
                  {
                    name: "column",
                    column: true,
                    defaultValue: "0",
                  },
                ],
              },
              {
                key: "sql.dateTime.currenttime",
                description:
                  "The CURRENT_TIME function returns the current time.",
                value: "currenttime",
                signature: "(VALUES CURRENT_TIME)",
                returns: "time",
                parameters: [],
              },
            ],
            numeric: [
              {
                key: "sql.numeric.abs",
                description:
                  "Returns the absolute value of a number. Example:abs(-24) result:24",
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
                key: "sql.numeric.floor",
                description:
                  "Returns number rounded down to the nearest number. Example:floor(3.1415) result:3",
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
                key: "sql.numeric.ceiling",
                description:
                  "Returns number rounded up to the nearest integer. Example:ceiling(0.25) result:1",
                value: "CEILING",
                signature: "CEILING(${number})",
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
                  "Returns the cotangent of an angle. Specify the angle in radians. Example: 1/tan(0.25) result: 3.9163173646459399 ",
                value: "COT",
                signature: "1/tan(${number})",
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
                    name: "dividend",
                    column: true,
                  },
                  {
                    name: "divisor",
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
                key: "sql.numeric.ln",
                description:
                  "Returns the natural logarithm of number. Example: ln(2) result: 0.6931471805599453 ",
                value: "LN",
                signature: "ln(${number})",
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
                  "Returns the base 10 logarithm of number. Example: log(2) result: 0.3010299956639812 ",
                value: "LOG",
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
                key: "sql.numeric.pi",
                description:
                  "Returns the constant Pi. Example: pi() result: 3.14159 ",
                value: "PI",
                signature: "pi()",
                returns: "numeric",
                parameters: [],
              },
              {
                key: "sql.numeric.radians",
                description:
                  "Converts given number from degrees to radians. Example : radians(4) result : 0.06981317007977318 ",
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
                key: "sql.numeric.sign",
                description:
                  "Returns the signum function of number, that is:\n\t\t0 if the argument is 0,\n\t\t1 if the argument is greater than 0,\n\t\t-1 if the argument is less than 0. Example: sign(0.5) result: 1.",
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
                key: "sql.numeric.sqrt",
                description:
                  "It displays the square root of a positive number. Example: sqrt(5) result: 2.23606797749979",
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
                signature: "(${number} * ${number})",
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
            ],
            text: [
              {
                key: "sql.text.concat",
                description:
                  "Returns the concatenation of string1, string2. Example:('Beng'||'aluru') result: Bengaluru ",
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
              {
                key: "sql.text.contains",
                description:
                  "Returns true if the given string contains the specified substring. Example: case when(locate('g', 'Bengaluru'))>0 then true else false end result: true ",
                value: "CONTAINS",
                signature:
                  "(case when(locate(${substring}, ${string}))>0 then true else false end)",
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
                key: "sql.text.endswith",
                description:
                  "Returns true if the given string endswith specified substring. Example:case when('postgres' like ('%'||'res')) then true else false end result: true. Note:Please provide single quotes if you are directly typing the substring value.",
                value: "ENDSWITH",
                signature:
                  "case when(${string} like ('%'||${substring})) then true else false end",
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
                  "Returns true if string starts with substring. Example: case when('bengaluru' like ('ben'||'%')) then true else false end result: true.  Note:Please provide single quotes if you are directly typing the substring value.",
                value: "STARTSWITH",
                signature:
                  "case when(${string} like (${substring}||'%')) then true else false end",
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
                key: "sql.text.find",
                description:
                  "Returns the starting position of the first instance of substring in string. Positions start with 1. If not found, 0 is returned. Example :locate('z' in 'Bengaluru') result : 0, locate('aluru' in 'Bengaluru') result : 5",
                value: "FIND",
                signature: "locate(${substring},${string})",
                returns: "numeric",
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
                key: "sql.text.left",
                description:
                  "Returns the left most (length) character from the string . Example: substr('bengaluru',1, 4) result: beng",
                value: "LEFT",
                signature: "trim(substr(${string},1, ${length}))",
                returns: "text",
                parameters: [
                  {
                    name: "string",
                    column: true,
                  },
                  {
                    name: "length",
                    column: true,
                    defaultValue: "0",
                  },
                ],
              },
              {
                key: "sql.text.length",
                description:
                  "Returns the number of characters in text. Example: length('Bengaluru') result:9",
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
                  "Converts all characters in the specified string to lowercase. Example: LOWER('BENGALURU') result: bengaluru ",
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
                  "Converts all characters in a string to uppercase. Example: UPPER('bengaluru') result: BENGALURU",
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
                key: "sql.text.right",
                description:
                  "Returns the rightmost character from the string. If length is negative extract all the characters from the right side except 3 leftmost characters Example: (case when 4 > length('bengaluru') then 'bengaluru' else substr('bengaluru',length('bengaluru')-(4-1),4) end) result: 'luru'.NOTE:if the provided length is grater than the length of the string then the whole string will be returned.",
                value: "RIGHT",
                signature:
                  "(case when ${length} > length(${string}) then ${string} else trim(substr(${string},length(${string})-(${length}-1),${length})) end)",
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
                key: "sql.text.ltrim",
                description:
                  " Removes leading whitespace from string Example: LTRIM(' Bengaluru') result: Bengaluru\n        ",
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
                key: "sql.text.mid",
                description:
                  "Returns the text starting from specified position. If position is more than string or length is less than 1 it will return empty string. Example: substr('bengaluru',2,5); result: engal",
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
                key: "sql.text.rtrim",
                description:
                  "Removes trailing whitespace from string. Example: RTRIM('Bengaluru  ') Result: Bengaluru ",
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
                key: "sql.text.trim",
                description:
                  "Removes whitespace from string. Example:TRIM(' Bengaluru ') result: Bengaluru\n        ",
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
            ],
            logical: [
              {
                key: "sql.logical.and",
                description:
                  "Inside IF we will use AND. performs a logical conjunction on two expressions.\n            In 'column' paramter we will 'drag column'.\n\t\t\tIn 'condition' parameter provide conditions like (>,= etc.,like).\n\t\t\tIn 'value' parameter provide condition value.\n\t\t\tIn 'moreconditions' parameter we will use nested 'OR' condition , 'AND' conditions. \n\t\t\tExample: CASE WHEN 'Washington' like '%sh%' \n             AND 'Washington' like 'W%' THEN 'returnl washington' \n             else 'NotMatched' end  ",
                value: "AND",
                signature:
                  "AND (${column} ${condition} ${value}) ${moreconditions} ",
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
                  "Inside case we will use when condition. Evaluates each condition from left to right and returns the result when the first condition met. If no condition met return from else if exist, otherwise return null. Example : CASE WHEN Quantity > 30 THEN 'The quantity is greater than 30'  ELSE 'The quantity is under 30' END ",
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
                  "Returns from statement_list when condition gets fail.We will use ELSE inside case function. Example: CASE when 50 > 0 then 'true' else 'false' end",
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
                  "Evaluates conditions and returns 'conditiontrue' if condition is true, otherwise return 'conditionfalse'.  We will use nested condition inside else 'conditiontrue' parameter. Example:case when creditlim > 50000 then 'PLATINUM' when (creditlim > = 50000) then 'GOLD' else 'SILVER' end ",
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
                  "Inside IF we will use AND, OR conditions. Evaluates and returns 'conditiontrue' if condition is true, otherwise return 'conditionfalse'. \n\t\t\tIn 'column' paramter we will drag column.\n\t\t\tIn 'condition' parameter provide conditions like (>,= etc.,).\n\t\t\tIn 'value' parameter provide value.\n\t\t\tIn 'moreconditions' parameter we will use nested 'OR', 'AND' conditions. Instead of dragging column directly we will write expression in column parameter like 50 > 0 (Note : In such case don't provide anything in 'condition' parameter and 'value' parameter). Example : case when (creditlim > 50000) then 'PLATINUM' else 'SILVER' end",
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
                  "Returns Expr1 if it is not null otherwise return expr2. Example : coalesce(profit, 0).NOTE:Manually entered null will not work it should be part of column, datatype of both the expressions should be match.",
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
                  "Inside WHEN condition we will use ISNULL. Evalutes and returns 'Conditiontrue' if the expression contain Null. Example1 : CASE WHEN 1 ISNULL THEN Conditionfalse. Example2 : CASE WHEN NULL ISNULL THEN Conditiontrue. NOTE: Manually entered null will not work it should be part of column.",
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
                signature: "(NOT(${column} ${condition} ${value}))",
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
                  "Inside IF we will use OR.Performs a logical disjunction on two expressions. \n\t\t\tIn 'column' paramter we will drag column.\n\t\t\tIn 'condition' parameter provide conditions like (>,= etc.,).\n\t\t\tIn 'value' parameter provide value.\n\t\t\tIn 'moreconditions' parameter we will use nested 'OR' , 'AND' functions. Example : CASE WHEN 'Washington' like '%sh%' \n             OR  'Washington' like 'W%' THEN 'return washington' \n             else 'NotMatched' end",
                value: "OR",
                signature:
                  " OR ${column} ${condition} ${value} ${moreconditions}",
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
                  "Returns 'statement_list' when condition get satisfied .\n\t\t\tIn column paramter we will drag column.\n\t\t\tIn searchcondition parameter provide conditions like (>, =, IS Null etc .,).\n\t\t\tIn value parameter provide value(Note : IS Null used in 'condition' parameter then don't provide anything in 'value' parameter). \n\t\t\tIn moreconditions parameter we will use nested when conditions, Else condition . We will use WHEN inside CASE. Example1 : CASE WHEN 1 > 0  THEN 'one' else 'TWO' end. Example2 : CASE WHEN 'Singapore' IS NULL THEN 'Singa' ELSE 'pore'. Example3 : CASE WHEN Washington like '%sh%' THEN 'return washington' else 'NotMatched' end",
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
                  "Returns \"expression\" if it is not null, otherwise returns zero.Example :(CASE WHEN '123' IS NULL THEN '0' ELSE '123' end) result :0 ",
                value: "ZN",
                signature:
                  "(CASE WHEN ${expr} IS NULL THEN '0' ELSE ${expr} end)",
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
          dateFunctions: {
            dateTime: [
              {
                value: "TODATE",
                label: "Date",
                part: "date",
                key: "sql.typeConversion.todate",
                returns: "date",
                parameters: [
                  {
                    name: "column",
                  },
                ],
              },
              {
                value: "DAY",
                label: "Days",
                part: "day",
                key: "sql.dateTime.day",
                returns: "numeric",
                parameters: [
                  {
                    name: "datetime",
                  },
                ],
              },
              {
                value: "MONTH",
                label: "Months",
                part: "month",
                key: "sql.dateTime.month",
                returns: "numeric",
                parameters: [
                  {
                    name: "datetime",
                  },
                ],
              },
              {
                value: "QUARTER",
                label: "Quarters",
                part: "quarter",
                key: "sql.dateTime.quarter",
                returns: "numeric",
                parameters: [
                  {
                    name: "datetime",
                  },
                ],
              },
              {
                value: "YEAR",
                label: "Years",
                part: "year",
                key: "sql.dateTime.year",
                returns: "numeric",
                parameters: [
                  {
                    name: "datetime",
                  },
                ],
              },
              {
                value: "TOTIME",
                label: "Time",
                part: "time",
                key: "sql.typeConversion.totime",
                returns: "time",
                parameters: [
                  {
                    name: "column",
                  },
                ],
              },
              {
                value: "HOUR",
                label: "Hours",
                part: "hour",
                key: "sql.dateTime.hour",
                returns: "numeric",
                parameters: [
                  {
                    name: "datetime",
                  },
                ],
              },
              {
                value: "MINUTE",
                label: "Minutes",
                part: "minute",
                key: "sql.dateTime.minute",
                returns: "numeric",
                parameters: [
                  {
                    name: "datetime",
                  },
                ],
              },
              {
                value: "SECOND",
                label: "Seconds",
                part: "second",
                key: "sql.dateTime.second",
                returns: "numeric",
                parameters: [
                  {
                    name: "datetime",
                  },
                ],
              },
              {
                value: "INIAVIDUAL",
                label: "Individual",
                part: "individual",
                key: "individual",
                parameters: [
                  {
                    name: "datetime",
                  },
                ],
              },
            ],
            date: [
              {
                value: "TODATE",
                label: "Date",
                part: "date",
                key: "sql.typeConversion.todate",
                returns: "date",
                parameters: [
                  {
                    name: "column",
                  },
                ],
              },
              {
                value: "DAY",
                label: "Days",
                part: "day",
                key: "sql.dateTime.day",
                returns: "numeric",
                parameters: [
                  {
                    name: "datetime",
                  },
                ],
              },
              {
                value: "MONTH",
                label: "Months",
                part: "month",
                key: "sql.dateTime.month",
                returns: "numeric",
                parameters: [
                  {
                    name: "datetime",
                  },
                ],
              },
              {
                value: "QUARTER",
                label: "Quarters",
                part: "quarter",
                key: "sql.dateTime.quarter",
                returns: "numeric",
                parameters: [
                  {
                    name: "datetime",
                  },
                ],
              },
              {
                value: "YEAR",
                label: "Years",
                part: "year",
                key: "sql.dateTime.year",
                returns: "numeric",
                parameters: [
                  {
                    name: "datetime",
                  },
                ],
              },
              {
                value: "INIAVIDUAL",
                label: "Individual",
                part: "individual",
                key: "individual",
                parameters: [
                  {
                    name: "datetime",
                  },
                ],
              },
            ],
            time: [
              {
                value: "TOTIME",
                label: "Time",
                part: "time",
                key: "sql.typeConversion.totime",
                returns: "time",
                parameters: [
                  {
                    name: "column",
                  },
                ],
              },
              {
                value: "HOUR",
                label: "Hours",
                part: "hour",
                key: "sql.dateTime.hour",
                returns: "numeric",
                parameters: [
                  {
                    name: "datetime",
                  },
                ],
              },
              {
                value: "MINUTE",
                label: "Minutes",
                part: "minute",
                key: "sql.dateTime.minute",
                returns: "numeric",
                parameters: [
                  {
                    name: "datetime",
                  },
                ],
              },
              {
                value: "SECOND",
                label: "Seconds",
                part: "second",
                key: "sql.dateTime.second",
                returns: "numeric",
                parameters: [
                  {
                    name: "datetime",
                  },
                ],
              },
              {
                value: "INIAVIDUAL",
                label: "Individual",
                part: "individual",
                key: "individual",
                parameters: [
                  {
                    name: "datetime",
                  },
                ],
              },
            ],
          },
          user: {
            name: "hiadmin",
            email: "admin@helicalinsight.com",
            actualUserName: "hiadmin",
            organization: "",
            roles: ["ROLE_ADMIN", "ROLE_USER"],
            profile: [],
          },
          selectedType: "Antcharts",
          data: [
            {
              travel_date: "2015-05-09 10:34:00.0",
            },
            {
              travel_date: "2015-11-14 15:28:00.0",
            },
            {
              travel_date: "2015-10-15 16:29:00.0",
            },
            {
              travel_date: "2015-05-11 12:50:00.0",
            },
            {
              travel_date: "2015-08-12 14:39:00.0",
            },
            {
              travel_date: "2015-09-08 10:22:00.0",
            },
            {
              travel_date: "2015-03-06 08:12:00.0",
            },
            {
              travel_date: "2015-12-13 13:19:00.0",
            },
            {
              travel_date: "2015-02-14 16:29:00.0",
            },
            {
              travel_date: "2015-05-05 08:26:00.0",
            },
            {
              travel_date: "2015-03-08 11:36:00.0",
            },
            {
              travel_date: "2015-09-04 08:22:00.0",
            },
            {
              travel_date: "2015-12-09 11:11:00.0",
            },
            {
              travel_date: "2015-08-12 16:23:00.0",
            },
            {
              travel_date: "2015-03-12 16:44:00.0",
            },
            {
              travel_date: "2015-05-09 14:34:00.0",
            },
            {
              travel_date: "2015-08-14 11:39:00.0",
            },
            {
              travel_date: "2015-10-15 11:37:00.0",
            },
            {
              travel_date: "2015-05-15 11:26:00.0",
            },
            {
              travel_date: "2015-05-11 15:42:00.0",
            },
            {
              travel_date: "2015-09-10 16:46:00.0",
            },
            {
              travel_date: "2015-02-08 15:29:00.0",
            },
            {
              travel_date: "2015-05-11 14:18:00.0",
            },
            {
              travel_date: "2015-09-09 16:46:00.0",
            },
            {
              travel_date: "2015-02-08 15:21:00.0",
            },
            {
              travel_date: "2015-02-12 12:37:00.0",
            },
            {
              travel_date: "2015-12-09 15:11:00.0",
            },
            {
              travel_date: "2015-06-09 15:33:00.0",
            },
            {
              travel_date: "2015-11-09 15:36:00.0",
            },
            {
              travel_date: "2015-03-09 15:44:00.0",
            },
            {
              travel_date: "2015-06-11 13:33:00.0",
            },
            {
              travel_date: "2015-06-01 07:49:00.0",
            },
            {
              travel_date: "2015-10-15 08:13:00.0",
            },
            {
              travel_date: "2015-11-06 15:44:00.0",
            },
            {
              travel_date: "2015-03-06 15:28:00.0",
            },
            {
              travel_date: "2015-03-06 15:52:00.0",
            },
            {
              travel_date: "2015-05-03 12:10:00.0",
            },
            {
              travel_date: "2015-10-04 13:37:00.0",
            },
            {
              travel_date: "2015-09-04 13:22:00.0",
            },
            {
              travel_date: "2015-03-07 15:04:00.0",
            },
            {
              travel_date: "2015-10-04 15:29:00.0",
            },
            {
              travel_date: "2015-12-13 08:27:00.0",
            },
            {
              travel_date: "2015-02-04 15:53:00.0",
            },
            {
              travel_date: "2015-12-07 14:03:00.0",
            },
            {
              travel_date: "2015-11-07 13:52:00.0",
            },
            {
              travel_date: "2015-03-06 14:28:00.0",
            },
            {
              travel_date: "2015-08-02 10:47:00.0",
            },
            {
              travel_date: "2015-03-06 12:44:00.0",
            },
            {
              travel_date: "2015-06-05 09:09:00.0",
            },
            {
              travel_date: "2015-12-02 16:27:00.0",
            },
            {
              travel_date: "2015-09-29 17:14:00.0",
            },
            {
              travel_date: "2015-08-03 15:07:00.0",
            },
            {
              travel_date: "2015-09-02 13:46:00.0",
            },
            {
              travel_date: "2015-04-07 10:35:00.0",
            },
            {
              travel_date: "2015-09-07 10:22:00.0",
            },
            {
              travel_date: "2015-12-30 17:03:00.0",
            },
            {
              travel_date: "2015-12-09 07:43:00.0",
            },
            {
              travel_date: "2015-10-03 13:29:00.0",
            },
            {
              travel_date: "2015-10-04 12:37:00.0",
            },
            {
              travel_date: "2015-02-02 14:53:00.0",
            },
            {
              travel_date: "2015-05-25 10:50:00.0",
            },
            {
              travel_date: "2015-09-25 10:22:00.0",
            },
            {
              travel_date: "2015-12-24 09:51:00.0",
            },
            {
              travel_date: "2015-10-22 07:21:00.0",
            },
            {
              travel_date: "2015-07-30 15:40:00.0",
            },
            {
              travel_date: "2015-11-26 11:36:00.0",
            },
            {
              travel_date: "2015-08-29 14:39:00.0",
            },
            {
              travel_date: "2015-06-25 09:33:00.0",
            },
            {
              travel_date: "2015-11-28 14:20:00.0",
            },
            {
              travel_date: "2015-03-28 14:28:00.0",
            },
            {
              travel_date: "2015-08-22 08:31:00.0",
            },
            {
              travel_date: "2015-10-27 11:13:00.0",
            },
            {
              travel_date: "2015-08-28 15:23:00.0",
            },
            {
              travel_date: "2015-06-29 16:49:00.0",
            },
            {
              travel_date: "2015-04-24 11:27:00.0",
            },
            {
              travel_date: "2015-10-20 08:29:00.0",
            },
            {
              travel_date: "2015-06-29 15:49:00.0",
            },
            {
              travel_date: "2015-08-29 10:07:00.0",
            },
            {
              travel_date: "2015-07-29 10:32:00.0",
            },
            {
              travel_date: "2015-07-27 16:48:00.0",
            },
            {
              travel_date: "2015-09-28 10:54:00.0",
            },
            {
              travel_date: "2015-03-28 10:04:00.0",
            },
            {
              travel_date: "2015-10-31 11:21:00.0",
            },
            {
              travel_date: "2015-09-30 09:06:00.0",
            },
            {
              travel_date: "2015-11-30 09:52:00.0",
            },
            {
              travel_date: "2015-02-25 15:29:00.0",
            },
            {
              travel_date: "2015-07-21 14:08:00.0",
            },
            {
              travel_date: "2015-10-20 13:53:00.0",
            },
            {
              travel_date: "2015-10-22 15:45:00.0",
            },
            {
              travel_date: "2015-11-22 15:12:00.0",
            },
            {
              travel_date: "2015-09-19 12:22:00.0",
            },
            {
              travel_date: "2015-12-22 15:59:00.0",
            },
            {
              travel_date: "2015-11-22 15:52:00.0",
            },
            {
              travel_date: "2015-06-09 18:57:00.0",
            },
            {
              travel_date: "2015-12-21 14:51:00.0",
            },
            {
              travel_date: "2015-06-20 14:41:00.0",
            },
            {
              travel_date: "2015-11-21 13:52:00.0",
            },
            {
              travel_date: "2015-07-21 16:00:00.0",
            },
            {
              travel_date: "2015-08-22 13:55:00.0",
            },
            {
              travel_date: "2015-05-22 13:42:00.0",
            },
            {
              travel_date: "2015-05-21 15:50:00.0",
            },
            {
              travel_date: "2015-08-20 16:23:00.0",
            },
            {
              travel_date: "2015-09-11 17:30:00.0",
            },
            {
              travel_date: "2015-06-29 07:33:00.0",
            },
            {
              travel_date: "2015-08-12 17:23:00.0",
            },
            {
              travel_date: "2015-08-20 09:15:00.0",
            },
            {
              travel_date: "2015-11-17 14:36:00.0",
            },
            {
              travel_date: "2015-08-13 17:07:00.0",
            },
            {
              travel_date: "2015-12-16 14:27:00.0",
            },
            {
              travel_date: "2015-06-20 11:25:00.0",
            },
            {
              travel_date: "2015-05-25 08:10:00.0",
            },
            {
              travel_date: "2015-07-22 09:24:00.0",
            },
            {
              travel_date: "2015-12-19 14:43:00.0",
            },
            {
              travel_date: "2015-12-17 16:35:00.0",
            },
            {
              travel_date: "2015-12-14 15:26:00.0",
            },
            {
              travel_date: "2015-08-14 15:06:00.0",
            },
            {
              travel_date: "2015-12-09 10:02:00.0",
            },
            {
              travel_date: "2015-01-12 14:05:00.0",
            },
            {
              travel_date: "2015-10-14 16:44:00.0",
            },
            {
              travel_date: "2015-01-11 11:37:00.0",
            },
            {
              travel_date: "2015-08-13 16:30:00.0",
            },
            {
              travel_date: "2015-10-15 14:28:00.0",
            },
            {
              travel_date: "2015-01-12 15:37:00.0",
            },
            {
              travel_date: "2015-06-10 09:32:00.0",
            },
            {
              travel_date: "2015-04-09 12:10:00.0",
            },
            {
              travel_date: "2015-10-13 16:44:00.0",
            },
            {
              travel_date: "2015-12-08 11:18:00.0",
            },
            {
              travel_date: "2015-05-09 11:25:00.0",
            },
            {
              travel_date: "2015-03-06 06:59:00.0",
            },
            {
              travel_date: "2015-01-12 16:53:00.0",
            },
            {
              travel_date: "2015-05-10 10:09:00.0",
            },
            {
              travel_date: "2015-01-12 09:05:00.0",
            },
            {
              travel_date: "2015-12-09 14:26:00.0",
            },
            {
              travel_date: "2015-08-09 14:38:00.0",
            },
            {
              travel_date: "2015-01-14 11:37:00.0",
            },
            {
              travel_date: "2015-07-13 10:39:00.0",
            },
            {
              travel_date: "2015-11-13 10:43:00.0",
            },
            {
              travel_date: "2015-07-15 11:15:00.0",
            },
            {
              travel_date: "2015-08-12 10:22:00.0",
            },
            {
              travel_date: "2015-09-15 11:05:00.0",
            },
            {
              travel_date: "2015-02-08 14:20:00.0",
            },
            {
              travel_date: "2015-01-12 10:45:00.0",
            },
            {
              travel_date: "2015-07-08 14:31:00.0",
            },
            {
              travel_date: "2015-08-14 09:06:00.0",
            },
            {
              travel_date: "2015-03-09 16:43:00.0",
            },
            {
              travel_date: "2015-01-10 14:29:00.0",
            },
            {
              travel_date: "2015-09-11 13:53:00.0",
            },
            {
              travel_date: "2015-01-12 12:13:00.0",
            },
            {
              travel_date: "2015-12-09 15:42:00.0",
            },
            {
              travel_date: "2015-09-01 07:21:00.0",
            },
            {
              travel_date: "2015-02-09 15:44:00.0",
            },
            {
              travel_date: "2015-10-23 17:36:00.0",
            },
            {
              travel_date: "2015-06-15 08:32:00.0",
            },
            {
              travel_date: "2015-10-02 11:20:00.0",
            },
            {
              travel_date: "2015-09-05 13:53:00.0",
            },
            {
              travel_date: "2015-11-02 12:27:00.0",
            },
            {
              travel_date: "2015-12-05 16:10:00.0",
            },
            {
              travel_date: "2015-02-07 14:44:00.0",
            },
            {
              travel_date: "2015-12-05 16:50:00.0",
            },
            {
              travel_date: "2015-07-01 12:07:00.0",
            },
            {
              travel_date: "2015-08-13 07:46:00.0",
            },
            {
              travel_date: "2015-08-03 09:22:00.0",
            },
            {
              travel_date: "2015-09-07 13:37:00.0",
            },
            {
              travel_date: "2015-11-07 11:43:00.0",
            },
            {
              travel_date: "2015-05-11 07:41:00.0",
            },
            {
              travel_date: "2015-08-02 16:46:00.0",
            },
            {
              travel_date: "2015-05-07 11:17:00.0",
            },
            {
              travel_date: "2015-09-07 10:13:00.0",
            },
            {
              travel_date: "2015-03-09 08:35:00.0",
            },
            {
              travel_date: "2015-02-02 13:52:00.0",
            },
            {
              travel_date: "2015-02-01 16:52:00.0",
            },
            {
              travel_date: "2015-12-05 11:50:00.0",
            },
            {
              travel_date: "2015-01-02 14:05:00.0",
            },
            {
              travel_date: "2015-10-31 17:36:00.0",
            },
            {
              travel_date: "2015-09-25 10:13:00.0",
            },
            {
              travel_date: "2015-04-22 07:50:00.0",
            },
            {
              travel_date: "2015-12-29 13:50:00.0",
            },
            {
              travel_date: "2015-11-28 14:51:00.0",
            },
            {
              travel_date: "2015-03-27 11:27:00.0",
            },
            {
              travel_date: "2015-07-31 15:55:00.0",
            },
            {
              travel_date: "2015-07-22 08:31:00.0",
            },
            {
              travel_date: "2015-01-27 11:13:00.0",
            },
            {
              travel_date: "2015-07-25 09:47:00.0",
            },
            {
              travel_date: "2015-12-24 11:50:00.0",
            },
            {
              travel_date: "2015-07-31 14:55:00.0",
            },
            {
              travel_date: "2015-11-21 08:59:00.0",
            },
            {
              travel_date: "2015-07-25 12:15:00.0",
            },
            {
              travel_date: "2015-11-27 10:51:00.0",
            },
            {
              travel_date: "2015-07-27 10:07:00.0",
            },
            {
              travel_date: "2015-04-29 15:42:00.0",
            },
            {
              travel_date: "2015-08-24 12:06:00.0",
            },
            {
              travel_date: "2015-09-25 14:13:00.0",
            },
            {
              travel_date: "2015-11-27 16:43:00.0",
            },
            {
              travel_date: "2015-09-28 10:37:00.0",
            },
            {
              travel_date: "2015-11-25 13:51:00.0",
            },
            {
              travel_date: "2015-11-30 12:11:00.0",
            },
            {
              travel_date: "2015-07-05 17:39:00.0",
            },
            {
              travel_date: "2015-01-28 10:37:00.0",
            },
            {
              travel_date: "2015-09-30 09:05:00.0",
            },
            {
              travel_date: "2015-08-31 10:22:00.0",
            },
            {
              travel_date: "2015-05-29 12:17:00.0",
            },
            {
              travel_date: "2015-10-24 15:04:00.0",
            },
            {
              travel_date: "2015-05-30 09:57:00.0",
            },
            {
              travel_date: "2015-09-25 15:21:00.0",
            },
            {
              travel_date: "2015-07-28 12:15:00.0",
            },
            {
              travel_date: "2015-05-29 11:25:00.0",
            },
            {
              travel_date: "2015-06-20 13:24:00.0",
            },
            {
              travel_date: "2015-02-22 15:04:00.0",
            },
            {
              travel_date: "2015-03-28 06:59:00.0",
            },
            {
              travel_date: "2015-06-09 17:08:00.0",
            },
            {
              travel_date: "2015-09-19 11:21:00.0",
            },
            {
              travel_date: "2015-07-19 10:39:00.0",
            },
            {
              travel_date: "2015-02-28 07:20:00.0",
            },
            {
              travel_date: "2015-11-22 14:51:00.0",
            },
            {
              travel_date: "2015-08-28 08:22:00.0",
            },
            {
              travel_date: "2015-11-23 13:43:00.0",
            },
            {
              travel_date: "2015-03-19 09:59:00.0",
            },
            {
              travel_date: "2015-06-11 17:16:00.0",
            },
            {
              travel_date: "2015-05-21 10:09:00.0",
            },
            {
              travel_date: "2015-05-23 12:33:00.0",
            },
            {
              travel_date: "2015-12-19 16:50:00.0",
            },
            {
              travel_date: "2015-12-21 10:18:00.0",
            },
            {
              travel_date: "2015-06-19 16:48:00.0",
            },
            {
              travel_date: "2015-08-17 14:46:00.0",
            },
            {
              travel_date: "2015-11-17 13:51:00.0",
            },
            {
              travel_date: "2015-07-23 11:23:00.0",
            },
            {
              travel_date: "2015-03-19 15:11:00.0",
            },
            {
              travel_date: "2015-12-19 14:10:00.0",
            },
            {
              travel_date: "2015-07-21 12:15:00.0",
            },
            {
              travel_date: "2015-09-18 13:53:00.0",
            },
            {
              travel_date: "2015-11-18 13:03:00.0",
            },
            {
              travel_date: "2015-12-17 16:34:00.0",
            },
            {
              travel_date: "2015-09-19 14:13:00.0",
            },
            {
              travel_date: "2015-07-17 15:31:00.0",
            },
            {
              travel_date: "2015-07-20 12:31:00.0",
            },
            {
              travel_date: "2015-05-11 12:40:00.0",
            },
            {
              travel_date: "2015-02-08 09:51:00.0",
            },
            {
              travel_date: "2015-11-15 16:26:00.0",
            },
            {
              travel_date: "2015-08-13 14:29:00.0",
            },
            {
              travel_date: "2015-08-16 17:29:00.0",
            },
            {
              travel_date: "2015-11-15 16:02:00.0",
            },
            {
              travel_date: "2015-09-15 16:44:00.0",
            },
            {
              travel_date: "2015-01-06 08:20:00.0",
            },
            {
              travel_date: "2015-04-08 10:33:00.0",
            },
            {
              travel_date: "2015-10-04 06:59:00.0",
            },
            {
              travel_date: "2015-11-14 16:50:00.0",
            },
            {
              travel_date: "2015-10-14 16:27:00.0",
            },
            {
              travel_date: "2015-08-14 16:29:00.0",
            },
            {
              travel_date: "2015-01-12 15:28:00.0",
            },
            {
              travel_date: "2015-08-15 14:45:00.0",
            },
            {
              travel_date: "2015-11-12 15:58:00.0",
            },
            {
              travel_date: "2015-01-12 15:04:00.0",
            },
            {
              travel_date: "2015-04-10 09:09:00.0",
            },
            {
              travel_date: "2015-07-08 11:22:00.0",
            },
            {
              travel_date: "2015-02-08 12:43:00.0",
            },
            {
              travel_date: "2015-09-15 13:52:00.0",
            },
            {
              travel_date: "2015-08-15 13:13:00.0",
            },
            {
              travel_date: "2015-11-12 16:50:00.0",
            },
            {
              travel_date: "2015-06-09 11:39:00.0",
            },
            {
              travel_date: "2015-06-19 17:55:00.0",
            },
            {
              travel_date: "2015-09-15 12:52:00.0",
            },
            {
              travel_date: "2015-08-13 10:37:00.0",
            },
            {
              travel_date: "2015-11-12 09:42:00.0",
            },
            {
              travel_date: "2015-05-11 15:08:00.0",
            },
            {
              travel_date: "2015-03-09 13:18:00.0",
            },
            {
              travel_date: "2015-08-09 13:45:00.0",
            },
            {
              travel_date: "2015-03-10 16:02:00.0",
            },
            {
              travel_date: "2015-09-09 13:44:00.0",
            },
            {
              travel_date: "2015-11-10 13:50:00.0",
            },
            {
              travel_date: "2015-03-09 16:02:00.0",
            },
            {
              travel_date: "2015-07-08 15:54:00.0",
            },
            {
              travel_date: "2015-08-15 09:37:00.0",
            },
            {
              travel_date: "2015-11-14 10:10:00.0",
            },
            {
              travel_date: "2015-01-13 11:20:00.0",
            },
            {
              travel_date: "2015-12-10 14:49:00.0",
            },
            {
              travel_date: "2015-06-12 12:39:00.0",
            },
            {
              travel_date: "2015-07-07 16:54:00.0",
            },
            {
              travel_date: "2015-05-01 10:16:00.0",
            },
            {
              travel_date: "2015-12-05 13:57:00.0",
            },
            {
              travel_date: "2015-05-06 16:40:00.0",
            },
            {
              travel_date: "2015-11-06 13:10:00.0",
            },
            {
              travel_date: "2015-08-03 10:13:00.0",
            },
            {
              travel_date: "2015-08-26 17:29:00.0",
            },
            {
              travel_date: "2015-11-07 13:18:00.0",
            },
            {
              travel_date: "2015-01-06 14:04:00.0",
            },
            {
              travel_date: "2015-08-13 07:21:00.0",
            },
            {
              travel_date: "2015-08-12 08:45:00.0",
            },
            {
              travel_date: "2015-03-13 07:02:00.0",
            },
            {
              travel_date: "2015-03-10 08:34:00.0",
            },
            {
              travel_date: "2015-04-10 06:57:00.0",
            },
            {
              travel_date: "2015-08-09 07:29:00.0",
            },
            {
              travel_date: "2015-01-02 14:36:00.0",
            },
            {
              travel_date: "2015-03-09 07:50:00.0",
            },
            {
              travel_date: "2015-12-07 09:41:00.0",
            },
            {
              travel_date: "2015-09-01 15:12:00.0",
            },
            {
              travel_date: "2015-06-03 13:15:00.0",
            },
            {
              travel_date: "2015-04-04 12:25:00.0",
            },
            {
              travel_date: "2015-11-30 15:58:00.0",
            },
            {
              travel_date: "2015-06-23 08:15:00.0",
            },
            {
              travel_date: "2015-11-30 15:10:00.0",
            },
            {
              travel_date: "2015-03-28 14:26:00.0",
            },
            {
              travel_date: "2015-11-29 13:58:00.0",
            },
            {
              travel_date: "2015-08-20 07:37:00.0",
            },
            {
              travel_date: "2015-09-28 15:52:00.0",
            },
            {
              travel_date: "2015-02-20 07:43:00.0",
            },
            {
              travel_date: "2015-07-24 11:14:00.0",
            },
            {
              travel_date: "2015-06-02 17:47:00.0",
            },
            {
              travel_date: "2015-10-29 15:59:00.0",
            },
            {
              travel_date: "2015-07-28 16:38:00.0",
            },
            {
              travel_date: "2015-06-29 15:07:00.0",
            },
            {
              travel_date: "2015-11-28 16:10:00.0",
            },
            {
              travel_date: "2015-06-29 10:31:00.0",
            },
            {
              travel_date: "2015-11-28 09:18:00.0",
            },
            {
              travel_date: "2015-01-27 16:44:00.0",
            },
            {
              travel_date: "2015-05-28 09:48:00.0",
            },
            {
              travel_date: "2015-07-26 16:38:00.0",
            },
            {
              travel_date: "2015-11-24 14:50:00.0",
            },
            {
              travel_date: "2015-12-30 12:41:00.0",
            },
            {
              travel_date: "2015-07-31 10:38:00.0",
            },
            {
              travel_date: "2015-06-29 12:23:00.0",
            },
            {
              travel_date: "2015-06-28 11:39:00.0",
            },
            {
              travel_date: "2015-09-30 10:52:00.0",
            },
            {
              travel_date: "2015-09-24 16:44:00.0",
            },
            {
              travel_date: "2015-07-29 11:06:00.0",
            },
            {
              travel_date: "2015-12-24 16:01:00.0",
            },
            {
              travel_date: "2015-07-21 14:38:00.0",
            },
            {
              travel_date: "2015-03-09 18:42:00.0",
            },
            {
              travel_date: "2015-05-22 15:08:00.0",
            },
            {
              travel_date: "2015-06-16 10:15:00.0",
            },
            {
              travel_date: "2015-12-21 13:49:00.0",
            },
            {
              travel_date: "2015-01-23 15:36:00.0",
            },
            {
              travel_date: "2015-08-16 10:53:00.0",
            },
            {
              travel_date: "2015-05-17 09:48:00.0",
            },
            {
              travel_date: "2015-06-29 08:31:00.0",
            },
            {
              travel_date: "2015-09-20 15:36:00.0",
            },
            {
              travel_date: "2015-10-23 14:11:00.0",
            },
            {
              travel_date: "2015-02-21 16:03:00.0",
            },
            {
              travel_date: "2015-07-21 15:54:00.0",
            },
            {
              travel_date: "2015-06-16 12:15:00.0",
            },
            {
              travel_date: "2015-10-22 14:27:00.0",
            },
            {
              travel_date: "2015-04-20 16:49:00.0",
            },
            {
              travel_date: "2015-07-22 14:54:00.0",
            },
            {
              travel_date: "2015-11-22 14:50:00.0",
            },
            {
              travel_date: "2015-05-23 12:56:00.0",
            },
            {
              travel_date: "2015-08-12 17:05:00.0",
            },
            {
              travel_date: "2015-02-16 13:51:00.0",
            },
            {
              travel_date: "2015-10-22 12:27:00.0",
            },
            {
              travel_date: "2015-08-13 17:29:00.0",
            },
            {
              travel_date: "2015-02-13 17:35:00.0",
            },
            {
              travel_date: "2015-12-16 14:33:00.0",
            },
            {
              travel_date: "2015-12-19 14:41:00.0",
            },
            {
              travel_date: "2015-09-19 14:36:00.0",
            },
            {
              travel_date: "2015-12-16 15:17:00.0",
            },
            {
              travel_date: "2015-10-17 16:03:00.0",
            },
            {
              travel_date: "2015-11-22 09:58:00.0",
            },
            {
              travel_date: "2015-06-25 08:15:00.0",
            },
            {
              travel_date: "2015-01-21 12:28:00.0",
            },
            {
              travel_date: "2015-07-21 11:38:00.0",
            },
            {
              travel_date: "2015-08-15 17:29:00.0",
            },
            {
              travel_date: "2015-10-22 10:51:00.0",
            },
            {
              travel_date: "2015-03-19 13:34:00.0",
            },
            {
              travel_date: "2015-05-15 16:47:00.0",
            },
            {
              travel_date: "2015-09-11 12:35:00.0",
            },
            {
              travel_date: "2015-12-12 14:40:00.0",
            },
            {
              travel_date: "2015-02-08 10:50:00.0",
            },
            {
              travel_date: "2015-10-12 14:10:00.0",
            },
            {
              travel_date: "2015-07-07 07:05:00.0",
            },
            {
              travel_date: "2015-06-13 16:22:00.0",
            },
            {
              travel_date: "2015-11-14 13:09:00.0",
            },
            {
              travel_date: "2015-11-09 12:57:00.0",
            },
            {
              travel_date: "2015-08-13 16:52:00.0",
            },
            {
              travel_date: "2015-06-04 07:22:00.0",
            },
            {
              travel_date: "2015-11-12 15:09:00.0",
            },
            {
              travel_date: "2015-11-12 16:33:00.0",
            },
            {
              travel_date: "2015-05-09 11:55:00.0",
            },
            {
              travel_date: "2015-03-06 06:57:00.0",
            },
            {
              travel_date: "2015-07-08 13:29:00.0",
            },
            {
              travel_date: "2015-05-11 16:39:00.0",
            },
            {
              travel_date: "2015-08-13 10:52:00.0",
            },
            {
              travel_date: "2015-03-10 15:25:00.0",
            },
            {
              travel_date: "2015-12-09 14:56:00.0",
            },
            {
              travel_date: "2015-09-10 15:27:00.0",
            },
            {
              travel_date: "2015-10-09 13:18:00.0",
            },
            {
              travel_date: "2015-03-10 16:09:00.0",
            },
            {
              travel_date: "2015-10-12 10:50:00.0",
            },
            {
              travel_date: "2015-05-11 15:07:00.0",
            },
            {
              travel_date: "2015-01-12 10:35:00.0",
            },
            {
              travel_date: "2015-08-13 09:28:00.0",
            },
            {
              travel_date: "2015-02-02 08:58:00.0",
            },
            {
              travel_date: "2015-01-14 12:51:00.0",
            },
            {
              travel_date: "2015-08-12 11:36:00.0",
            },
            {
              travel_date: "2015-09-15 10:27:00.0",
            },
            {
              travel_date: "2015-08-10 13:52:00.0",
            },
            {
              travel_date: "2015-07-15 10:21:00.0",
            },
            {
              travel_date: "2015-02-14 09:58:00.0",
            },
            {
              travel_date: "2015-02-12 11:10:00.0",
            },
            {
              travel_date: "2015-08-13 12:44:00.0",
            },
            {
              travel_date: "2015-04-10 13:48:00.0",
            },
            {
              travel_date: "2015-03-09 16:57:00.0",
            },
            {
              travel_date: "2015-05-08 15:55:00.0",
            },
            {
              travel_date: "2015-03-09 15:17:00.0",
            },
            {
              travel_date: "2015-12-09 15:56:00.0",
            },
            {
              travel_date: "2015-01-12 12:43:00.0",
            },
            {
              travel_date: "2015-12-14 10:40:00.0",
            },
            {
              travel_date: "2015-04-09 15:08:00.0",
            },
            {
              travel_date: "2015-02-11 13:02:00.0",
            },
            {
              travel_date: "2015-01-06 15:51:00.0",
            },
            {
              travel_date: "2015-06-04 13:46:00.0",
            },
            {
              travel_date: "2015-10-02 12:50:00.0",
            },
            {
              travel_date: "2015-11-02 09:09:00.0",
            },
            {
              travel_date: "2015-12-05 16:24:00.0",
            },
            {
              travel_date: "2015-12-04 15:32:00.0",
            },
            {
              travel_date: "2015-11-05 16:41:00.0",
            },
            {
              travel_date: "2015-12-13 08:48:00.0",
            },
            {
              travel_date: "2015-09-05 15:27:00.0",
            },
            {
              travel_date: "2015-03-05 15:57:00.0",
            },
            {
              travel_date: "2015-05-04 16:23:00.0",
            },
            {
              travel_date: "2015-09-07 13:35:00.0",
            },
            {
              travel_date: "2015-11-02 10:33:00.0",
            },
            {
              travel_date: "2015-03-06 14:17:00.0",
            },
            {
              travel_date: "2015-03-06 14:49:00.0",
            },
            {
              travel_date: "2015-01-12 08:27:00.0",
            },
            {
              travel_date: "2015-05-13 07:39:00.0",
            },
            {
              travel_date: "2015-12-10 07:48:00.0",
            },
            {
              travel_date: "2015-10-07 12:10:00.0",
            },
            {
              travel_date: "2015-04-01 14:48:00.0",
            },
            {
              travel_date: "2015-12-11 08:48:00.0",
            },
            {
              travel_date: "2015-12-03 16:48:00.0",
            },
            {
              travel_date: "2015-01-02 16:35:00.0",
            },
            {
              travel_date: "2015-11-04 10:57:00.0",
            },
            {
              travel_date: "2015-03-05 09:01:00.0",
            },
            {
              travel_date: "2015-07-07 11:13:00.0",
            },
            {
              travel_date: "2015-09-07 10:27:00.0",
            },
            {
              travel_date: "2015-05-05 12:47:00.0",
            },
            {
              travel_date: "2015-06-08 07:30:00.0",
            },
            {
              travel_date: "2015-05-04 11:47:00.0",
            },
            {
              travel_date: "2015-02-04 11:02:00.0",
            },
            {
              travel_date: "2015-01-01 16:59:00.0",
            },
            {
              travel_date: "2015-08-06 10:36:00.0",
            },
            {
              travel_date: "2015-11-04 12:25:00.0",
            },
            {
              travel_date: "2015-07-02 14:45:00.0",
            },
            {
              travel_date: "2015-10-01 15:18:00.0",
            },
            {
              travel_date: "2015-07-31 16:37:00.0",
            },
            {
              travel_date: "2015-11-27 12:33:00.0",
            },
            {
              travel_date: "2015-11-24 09:25:00.0",
            },
            {
              travel_date: "2015-08-24 09:44:00.0",
            },
            {
              travel_date: "2015-07-30 15:53:00.0",
            },
            {
              travel_date: "2015-05-23 07:23:00.0",
            },
            {
              travel_date: "2015-11-30 16:25:00.0",
            },
            {
              travel_date: "2015-04-24 10:08:00.0",
            },
            {
              travel_date: "2015-05-25 09:39:00.0",
            },
            {
              travel_date: "2015-10-20 07:26:00.0",
            },
            {
              travel_date: "2015-03-28 15:57:00.0",
            },
            {
              travel_date: "2015-04-26 09:00:00.0",
            },
            {
              travel_date: "2015-03-20 07:49:00.0",
            },
            {
              travel_date: "2015-03-20 08:25:00.0",
            },
            {
              travel_date: "2015-01-29 15:59:00.0",
            },
            {
              travel_date: "2015-08-28 09:04:00.0",
            },
            {
              travel_date: "2015-12-24 13:32:00.0",
            },
            {
              travel_date: "2015-08-27 16:28:00.0",
            },
            {
              travel_date: "2015-05-31 12:39:00.0",
            },
            {
              travel_date: "2015-06-29 09:06:00.0",
            },
            {
              travel_date: "2015-12-25 13:08:00.0",
            },
            {
              travel_date: "2015-11-26 16:41:00.0",
            },
            {
              travel_date: "2015-08-28 11:28:00.0",
            },
            {
              travel_date: "2015-12-24 15:48:00.0",
            },
            {
              travel_date: "2015-07-31 10:45:00.0",
            },
            {
              travel_date: "2015-03-28 11:01:00.0",
            },
            {
              travel_date: "2015-06-29 12:30:00.0",
            },
            {
              travel_date: "2015-12-17 08:56:00.0",
            },
            {
              travel_date: "2015-08-25 15:52:00.0",
            },
            {
              travel_date: "2015-11-24 16:49:00.0",
            },
            {
              travel_date: "2015-10-21 14:58:00.0",
            },
            {
              travel_date: "2015-01-18 11:27:00.0",
            },
            {
              travel_date: "2015-04-08 17:16:00.0",
            },
            {
              travel_date: "2015-05-23 16:47:00.0",
            },
            {
              travel_date: "2015-11-30 08:57:00.0",
            },
            {
              travel_date: "2015-12-09 17:40:00.0",
            },
            {
              travel_date: "2015-05-31 07:55:00.0",
            },
            {
              travel_date: "2015-04-18 12:16:00.0",
            },
            {
              travel_date: "2015-06-29 08:14:00.0",
            },
            {
              travel_date: "2015-08-23 13:52:00.0",
            },
            {
              travel_date: "2015-07-16 12:13:00.0",
            },
            {
              travel_date: "2015-05-21 15:23:00.0",
            },
            {
              travel_date: "2015-02-19 09:58:00.0",
            },
            {
              travel_date: "2015-05-21 10:23:00.0",
            },
            {
              travel_date: "2015-08-22 12:44:00.0",
            },
            {
              travel_date: "2015-02-16 14:02:00.0",
            },
            {
              travel_date: "2015-08-20 11:36:00.0",
            },
            {
              travel_date: "2015-10-20 11:34:00.0",
            },
            {
              travel_date: "2015-03-23 10:57:00.0",
            },
            {
              travel_date: "2015-12-17 16:32:00.0",
            },
            {
              travel_date: "2015-11-17 16:41:00.0",
            },
            {
              travel_date: "2015-08-19 14:20:00.0",
            },
            {
              travel_date: "2015-08-17 15:28:00.0",
            },
            {
              travel_date: "2015-12-17 15:56:00.0",
            },
            {
              travel_date: "2015-01-20 12:27:00.0",
            },
            {
              travel_date: "2015-04-21 11:24:00.0",
            },
            {
              travel_date: "2015-01-21 11:27:00.0",
            },
            {
              travel_date: "2015-08-12 13:19:00.0",
            },
            {
              travel_date: "2015-04-12 13:15:00.0",
            },
            {
              travel_date: "2015-10-10 11:41:00.0",
            },
            {
              travel_date: "2015-04-12 13:55:00.0",
            },
            {
              travel_date: "2015-04-13 14:55:00.0",
            },
            {
              travel_date: "2015-05-11 11:38:00.0",
            },
            {
              travel_date: "2015-03-12 14:56:00.0",
            },
            {
              travel_date: "2015-09-12 14:26:00.0",
            },
            {
              travel_date: "2015-11-12 15:08:00.0",
            },
            {
              travel_date: "2015-08-13 16:03:00.0",
            },
            {
              travel_date: "2015-09-19 18:58:00.0",
            },
            {
              travel_date: "2015-07-18 17:36:00.0",
            },
            {
              travel_date: "2015-08-12 16:19:00.0",
            },
            {
              travel_date: "2015-08-10 10:51:00.0",
            },
            {
              travel_date: "2015-08-14 14:19:00.0",
            },
            {
              travel_date: "2015-02-15 13:09:00.0",
            },
            {
              travel_date: "2015-08-10 15:03:00.0",
            },
            {
              travel_date: "2015-07-12 09:20:00.0",
            },
            {
              travel_date: "2015-01-12 09:42:00.0",
            },
            {
              travel_date: "2015-01-11 15:26:00.0",
            },
            {
              travel_date: "2015-07-08 14:04:00.0",
            },
            {
              travel_date: "2015-04-10 16:15:00.0",
            },
            {
              travel_date: "2015-12-09 13:15:00.0",
            },
            {
              travel_date: "2015-01-12 10:02:00.0",
            },
            {
              travel_date: "2015-10-10 16:01:00.0",
            },
            {
              travel_date: "2015-03-11 15:08:00.0",
            },
            {
              travel_date: "2015-09-10 16:26:00.0",
            },
            {
              travel_date: "2015-02-08 15:41:00.0",
            },
            {
              travel_date: "2015-07-08 15:20:00.0",
            },
            {
              travel_date: "2015-05-11 14:54:00.0",
            },
            {
              travel_date: "2015-02-08 15:01:00.0",
            },
            {
              travel_date: "2015-05-11 14:14:00.0",
            },
            {
              travel_date: "2015-07-10 13:04:00.0",
            },
            {
              travel_date: "2015-05-11 13:30:00.0",
            },
            {
              travel_date: "2015-03-06 15:40:00.0",
            },
            {
              travel_date: "2015-09-07 15:50:00.0",
            },
            {
              travel_date: "2015-08-12 07:03:00.0",
            },
            {
              travel_date: "2015-02-02 09:01:00.0",
            },
            {
              travel_date: "2015-12-04 15:47:00.0",
            },
            {
              travel_date: "2015-10-04 16:25:00.0",
            },
            {
              travel_date: "2015-09-05 15:10:00.0",
            },
            {
              travel_date: "2015-05-12 08:30:00.0",
            },
            {
              travel_date: "2015-08-04 16:11:00.0",
            },
            {
              travel_date: "2015-05-11 08:38:00.0",
            },
            {
              travel_date: "2015-03-02 15:16:00.0",
            },
            {
              travel_date: "2015-05-02 15:06:00.0",
            },
            {
              travel_date: "2015-10-28 17:09:00.0",
            },
            {
              travel_date: "2015-09-04 09:58:00.0",
            },
            {
              travel_date: "2015-03-04 10:24:00.0",
            },
            {
              travel_date: "2015-09-07 11:02:00.0",
            },
            {
              travel_date: "2015-05-11 07:30:00.0",
            },
            {
              travel_date: "2015-10-05 09:01:00.0",
            },
            {
              travel_date: "2015-10-09 08:57:00.0",
            },
            {
              travel_date: "2015-09-04 11:50:00.0",
            },
            {
              travel_date: "2015-07-08 07:20:00.0",
            },
            {
              travel_date: "2015-01-02 13:50:00.0",
            },
            {
              travel_date: "2015-12-01 16:23:00.0",
            },
            {
              travel_date: "2015-08-01 16:51:00.0",
            },
            {
              travel_date: "2015-09-29 20:42:00.0",
            },
            {
              travel_date: "2015-03-06 10:56:00.0",
            },
            {
              travel_date: "2015-07-08 08:12:00.0",
            },
            {
              travel_date: "2015-01-02 14:10:00.0",
            },
            {
              travel_date: "2015-02-09 07:41:00.0",
            },
            {
              travel_date: "2015-09-28 13:10:00.0",
            },
            {
              travel_date: "2015-05-23 08:06:00.0",
            },
            {
              travel_date: "2015-11-30 15:24:00.0",
            },
            {
              travel_date: "2015-02-28 13:49:00.0",
            },
            {
              travel_date: "2015-12-24 10:23:00.0",
            },
            {
              travel_date: "2015-09-25 09:58:00.0",
            },
            {
              travel_date: "2015-12-23 07:23:00.0",
            },
            {
              travel_date: "2015-09-28 15:50:00.0",
            },
            {
              travel_date: "2015-05-29 16:38:00.0",
            },
            {
              travel_date: "2015-09-30 13:26:00.0",
            },
            {
              travel_date: "2015-06-20 07:45:00.0",
            },
            {
              travel_date: "2015-01-20 07:26:00.0",
            },
            {
              travel_date: "2015-04-28 15:47:00.0",
            },
            {
              travel_date: "2015-11-30 14:40:00.0",
            },
            {
              travel_date: "2015-03-26 10:08:00.0",
            },
            {
              travel_date: "2015-08-26 10:51:00.0",
            },
            {
              travel_date: "2015-02-28 16:25:00.0",
            },
            {
              travel_date: "2015-06-30 11:37:00.0",
            },
            {
              travel_date: "2015-11-27 16:56:00.0",
            },
            {
              travel_date: "2015-07-29 10:52:00.0",
            },
            {
              travel_date: "2015-08-28 09:43:00.0",
            },
            {
              travel_date: "2015-04-19 08:31:00.0",
            },
            {
              travel_date: "2015-04-25 13:47:00.0",
            },
            {
              travel_date: "2015-11-26 13:08:00.0",
            },
            {
              travel_date: "2015-11-07 17:00:00.0",
            },
            {
              travel_date: "2015-09-30 10:10:00.0",
            },
            {
              travel_date: "2015-08-28 12:11:00.0",
            },
            {
              travel_date: "2015-12-17 10:15:00.0",
            },
            {
              travel_date: "2015-03-18 11:08:00.0",
            },
            {
              travel_date: "2015-11-23 16:48:00.0",
            },
            {
              travel_date: "2015-08-31 07:19:00.0",
            },
            {
              travel_date: "2015-06-16 10:29:00.0",
            },
            {
              travel_date: "2015-05-23 15:54:00.0",
            },
            {
              travel_date: "2015-06-09 17:29:00.0",
            },
            {
              travel_date: "2015-12-30 08:23:00.0",
            },
            {
              travel_date: "2015-11-22 16:48:00.0",
            },
            {
              travel_date: "2015-12-29 08:07:00.0",
            },
            {
              travel_date: "2015-04-20 15:07:00.0",
            },
            {
              travel_date: "2015-08-20 16:27:00.0",
            },
            {
              travel_date: "2015-09-20 16:42:00.0",
            },
            {
              travel_date: "2015-10-22 14:49:00.0",
            },
            {
              travel_date: "2015-12-21 15:07:00.0",
            },
            {
              travel_date: "2015-04-22 14:31:00.0",
            },
            {
              travel_date: "2015-07-20 16:44:00.0",
            },
            {
              travel_date: "2015-09-18 15:50:00.0",
            },
            {
              travel_date: "2015-09-19 15:10:00.0",
            },
            {
              travel_date: "2015-11-22 12:56:00.0",
            },
            {
              travel_date: "2015-01-12 18:26:00.0",
            },
            {
              travel_date: "2015-11-17 13:40:00.0",
            },
            {
              travel_date: "2015-08-19 15:11:00.0",
            },
            {
              travel_date: "2015-10-20 11:33:00.0",
            },
            {
              travel_date: "2015-08-23 10:03:00.0",
            },
            {
              travel_date: "2015-12-17 16:23:00.0",
            },
            {
              travel_date: "2015-12-19 14:39:00.0",
            },
            {
              travel_date: "2015-08-20 12:51:00.0",
            },
            {
              travel_date: "2015-09-23 09:58:00.0",
            },
            {
              travel_date: "2015-02-16 16:49:00.0",
            },
            {
              travel_date: "2015-04-15 17:15:00.0",
            },
            {
              travel_date: "2015-12-13 14:14:00.0",
            },
            {
              travel_date: "2015-05-06 07:45:00.0",
            },
            {
              travel_date: "2015-11-14 15:47:00.0",
            },
            {
              travel_date: "2015-02-06 07:16:00.0",
            },
            {
              travel_date: "2015-03-09 10:23:00.0",
            },
            {
              travel_date: "2015-02-14 15:16:00.0",
            },
            {
              travel_date: "2015-10-09 09:32:00.0",
            },
            {
              travel_date: "2015-11-10 12:15:00.0",
            },
            {
              travel_date: "2015-11-12 15:07:00.0",
            },
            {
              travel_date: "2015-06-13 16:28:00.0",
            },
            {
              travel_date: "2015-06-15 14:36:00.0",
            },
            {
              travel_date: "2015-02-13 16:40:00.0",
            },
            {
              travel_date: "2015-04-10 10:38:00.0",
            },
            {
              travel_date: "2015-01-12 16:41:00.0",
            },
            {
              travel_date: "2015-01-10 10:49:00.0",
            },
            {
              travel_date: "2015-12-09 11:06:00.0",
            },
            {
              travel_date: "2015-10-12 16:24:00.0",
            },
            {
              travel_date: "2015-08-13 15:10:00.0",
            },
            {
              travel_date: "2015-08-13 10:26:00.0",
            },
            {
              travel_date: "2015-08-12 09:10:00.0",
            },
            {
              travel_date: "2015-03-03 08:15:00.0",
            },
            {
              travel_date: "2015-07-08 13:27:00.0",
            },
            {
              travel_date: "2015-02-08 13:24:00.0",
            },
            {
              travel_date: "2015-08-13 10:50:00.0",
            },
            {
              travel_date: "2015-03-10 15:47:00.0",
            },
            {
              travel_date: "2015-04-09 13:30:00.0",
            },
            {
              travel_date: "2015-06-08 14:28:00.0",
            },
            {
              travel_date: "2015-10-11 15:16:00.0",
            },
            {
              travel_date: "2015-07-08 15:11:00.0",
            },
            {
              travel_date: "2015-11-13 12:47:00.0",
            },
            {
              travel_date: "2015-01-09 16:01:00.0",
            },
            {
              travel_date: "2015-10-22 17:56:00.0",
            },
            {
              travel_date: "2015-09-12 12:49:00.0",
            },
            {
              travel_date: "2015-04-23 17:54:00.0",
            },
            {
              travel_date: "2015-02-09 15:16:00.0",
            },
            {
              travel_date: "2015-05-15 08:37:00.0",
            },
            {
              travel_date: "2015-01-04 13:49:00.0",
            },
            {
              travel_date: "2015-06-03 12:44:00.0",
            },
            {
              travel_date: "2015-12-03 12:06:00.0",
            },
            {
              travel_date: "2015-10-04 14:24:00.0",
            },
            {
              travel_date: "2015-08-01 09:10:00.0",
            },
            {
              travel_date: "2015-08-02 12:50:00.0",
            },
            {
              travel_date: "2015-02-05 16:48:00.0",
            },
            {
              travel_date: "2015-05-02 10:53:00.0",
            },
            {
              travel_date: "2015-12-13 07:38:00.0",
            },
            {
              travel_date: "2015-03-05 10:31:00.0",
            },
            {
              travel_date: "2015-09-05 10:49:00.0",
            },
            {
              travel_date: "2015-01-09 06:57:00.0",
            },
            {
              travel_date: "2015-11-04 09:31:00.0",
            },
            {
              travel_date: "2015-11-02 15:39:00.0",
            },
            {
              travel_date: "2015-06-29 17:28:00.0",
            },
            {
              travel_date: "2015-01-02 16:09:00.0",
            },
            {
              travel_date: "2015-03-06 12:23:00.0",
            },
            {
              travel_date: "2015-11-01 13:07:00.0",
            },
            {
              travel_date: "2015-07-02 16:27:00.0",
            },
            {
              travel_date: "2015-07-08 07:27:00.0",
            },
            {
              travel_date: "2015-04-07 10:54:00.0",
            },
            {
              travel_date: "2015-06-09 07:36:00.0",
            },
            {
              travel_date: "2015-04-02 14:54:00.0",
            },
            {
              travel_date: "2015-06-29 14:28:00.0",
            },
            {
              travel_date: "2015-09-25 10:57:00.0",
            },
            {
              travel_date: "2015-03-30 15:07:00.0",
            },
            {
              travel_date: "2015-09-24 09:57:00.0",
            },
            {
              travel_date: "2015-08-31 15:26:00.0",
            },
            {
              travel_date: "2015-12-22 08:14:00.0",
            },
            {
              travel_date: "2015-01-29 13:49:00.0",
            },
            {
              travel_date: "2015-11-26 12:47:00.0",
            },
            {
              travel_date: "2015-12-28 14:54:00.0",
            },
            {
              travel_date: "2015-06-29 16:36:00.0",
            },
            {
              travel_date: "2015-05-29 16:29:00.0",
            },
            {
              travel_date: "2015-11-28 15:55:00.0",
            },
            {
              travel_date: "2015-05-27 10:13:00.0",
            },
            {
              travel_date: "2015-09-26 09:41:00.0",
            },
            {
              travel_date: "2015-11-29 16:47:00.0",
            },
            {
              travel_date: "2015-06-25 11:52:00.0",
            },
            {
              travel_date: "2015-09-30 14:17:00.0",
            },
            {
              travel_date: "2015-06-24 12:28:00.0",
            },
            {
              travel_date: "2015-09-24 12:57:00.0",
            },
            {
              travel_date: "2015-10-31 13:40:00.0",
            },
            {
              travel_date: "2015-03-29 10:47:00.0",
            },
            {
              travel_date: "2015-03-28 10:47:00.0",
            },
            {
              travel_date: "2015-08-25 13:02:00.0",
            },
            {
              travel_date: "2015-06-30 09:20:00.0",
            },
            {
              travel_date: "2015-12-17 08:30:00.0",
            },
            {
              travel_date: "2015-05-25 16:29:00.0",
            },
            {
              travel_date: "2015-05-26 13:29:00.0",
            },
            {
              travel_date: "2015-11-25 15:47:00.0",
            },
            {
              travel_date: "2015-09-30 10:57:00.0",
            },
            {
              travel_date: "2015-06-25 15:12:00.0",
            },
            {
              travel_date: "2015-04-25 15:06:00.0",
            },
            {
              travel_date: "2015-04-30 07:22:00.0",
            },
            {
              travel_date: "2015-09-23 16:49:00.0",
            },
            {
              travel_date: "2015-12-21 13:54:00.0",
            },
            {
              travel_date: "2015-05-16 10:53:00.0",
            },
            {
              travel_date: "2015-06-16 10:04:00.0",
            },
            {
              travel_date: "2015-07-17 09:03:00.0",
            },
            {
              travel_date: "2015-11-21 16:39:00.0",
            },
            {
              travel_date: "2015-03-19 10:47:00.0",
            },
            {
              travel_date: "2015-04-20 15:38:00.0",
            },
            {
              travel_date: "2015-06-20 15:52:00.0",
            },
            {
              travel_date: "2015-03-17 12:39:00.0",
            },
            {
              travel_date: "2015-04-19 09:06:00.0",
            },
            {
              travel_date: "2015-08-20 16:34:00.0",
            },
            {
              travel_date: "2015-12-21 15:30:00.0",
            },
            {
              travel_date: "2015-08-23 12:02:00.0",
            },
            {
              travel_date: "2015-06-16 13:52:00.0",
            },
            {
              travel_date: "2015-12-19 16:46:00.0",
            },
            {
              travel_date: "2015-03-21 10:39:00.0",
            },
            {
              travel_date: "2015-08-20 09:58:00.0",
            },
            {
              travel_date: "2015-09-20 09:57:00.0",
            },
            {
              travel_date: "2015-09-20 10:01:00.0",
            },
            {
              travel_date: "2015-07-19 15:59:00.0",
            },
            {
              travel_date: "2015-02-23 11:24:00.0",
            },
            {
              travel_date: "2015-02-19 15:00:00.0",
            },
            {
              travel_date: "2015-05-27 07:05:00.0",
            },
            {
              travel_date: "2015-03-26 08:31:00.0",
            },
            {
              travel_date: "2015-11-25 08:15:00.0",
            },
            {
              travel_date: "2015-06-25 08:12:00.0",
            },
            {
              travel_date: "2015-09-19 14:41:00.0",
            },
            {
              travel_date: "2015-09-24 07:01:00.0",
            },
            {
              travel_date: "2015-09-18 14:41:00.0",
            },
            {
              travel_date: "2015-08-19 13:26:00.0",
            },
            {
              travel_date: "2015-02-17 15:24:00.0",
            },
            {
              travel_date: "2015-08-21 11:18:00.0",
            },
            {
              travel_date: "2015-05-09 10:20:00.0",
            },
            {
              travel_date: "2015-05-09 10:28:00.0",
            },
            {
              travel_date: "2015-11-09 10:14:00.0",
            },
            {
              travel_date: "2015-10-07 08:07:00.0",
            },
            {
              travel_date: "2015-06-09 09:11:00.0",
            },
            {
              travel_date: "2015-06-12 14:03:00.0",
            },
            {
              travel_date: "2015-01-12 15:40:00.0",
            },
            {
              travel_date: "2015-11-04 08:22:00.0",
            },
            {
              travel_date: "2015-07-08 12:18:00.0",
            },
            {
              travel_date: "2015-03-09 11:38:00.0",
            },
            {
              travel_date: "2015-11-09 11:06:00.0",
            },
            {
              travel_date: "2015-02-12 16:47:00.0",
            },
            {
              travel_date: "2015-08-12 16:57:00.0",
            },
            {
              travel_date: "2015-10-09 14:39:00.0",
            },
            {
              travel_date: "2015-08-13 10:01:00.0",
            },
            {
              travel_date: "2015-02-08 13:55:00.0",
            },
            {
              travel_date: "2015-01-12 09:00:00.0",
            },
            {
              travel_date: "2015-11-11 16:22:00.0",
            },
            {
              travel_date: "2015-11-09 14:30:00.0",
            },
            {
              travel_date: "2015-07-11 16:50:00.0",
            },
            {
              travel_date: "2015-01-11 15:16:00.0",
            },
            {
              travel_date: "2015-11-14 12:22:00.0",
            },
            {
              travel_date: "2015-09-11 15:08:00.0",
            },
            {
              travel_date: "2015-04-10 13:53:00.0",
            },
            {
              travel_date: "2015-05-11 14:28:00.0",
            },
            {
              travel_date: "2015-05-08 15:12:00.0",
            },
            {
              travel_date: "2015-02-10 13:39:00.0",
            },
            {
              travel_date: "2015-07-11 14:02:00.0",
            },
            {
              travel_date: "2015-04-08 15:37:00.0",
            },
            {
              travel_date: "2015-05-11 13:28:00.0",
            },
            {
              travel_date: "2015-08-13 11:17:00.0",
            },
            {
              travel_date: "2015-11-09 15:38:00.0",
            },
            {
              travel_date: "2015-02-08 16:07:00.0",
            },
            {
              travel_date: "2015-09-13 11:48:00.0",
            },
            {
              travel_date: "2015-09-07 16:40:00.0",
            },
            {
              travel_date: "2015-11-06 16:22:00.0",
            },
            {
              travel_date: "2015-04-01 09:45:00.0",
            },
            {
              travel_date: "2015-09-07 14:48:00.0",
            },
            {
              travel_date: "2015-07-01 12:26:00.0",
            },
            {
              travel_date: "2015-08-12 08:09:00.0",
            },
            {
              travel_date: "2015-11-06 14:22:00.0",
            },
            {
              travel_date: "2015-03-06 14:14:00.0",
            },
            {
              travel_date: "2015-11-04 16:14:00.0",
            },
            {
              travel_date: "2015-02-12 08:07:00.0",
            },
            {
              travel_date: "2015-11-02 15:38:00.0",
            },
            {
              travel_date: "2015-05-10 07:12:00.0",
            },
            {
              travel_date: "2015-04-02 15:05:00.0",
            },
            {
              travel_date: "2015-10-28 17:31:00.0",
            },
            {
              travel_date: "2015-12-07 11:37:00.0",
            },
            {
              travel_date: "2015-05-02 16:36:00.0",
            },
            {
              travel_date: "2015-09-08 07:40:00.0",
            },
            {
              travel_date: "2015-06-09 08:35:00.0",
            },
            {
              travel_date: "2015-03-09 07:22:00.0",
            },
            {
              travel_date: "2015-12-02 14:05:00.0",
            },
            {
              travel_date: "2015-04-03 13:37:00.0",
            },
            {
              travel_date: "2015-07-28 13:34:00.0",
            },
            {
              travel_date: "2015-11-27 11:38:00.0",
            },
            {
              travel_date: "2015-01-28 14:32:00.0",
            },
            {
              travel_date: "2015-07-28 14:10:00.0",
            },
            {
              travel_date: "2015-08-31 15:41:00.0",
            },
            {
              travel_date: "2015-10-26 12:39:00.0",
            },
            {
              travel_date: "2015-11-30 13:30:00.0",
            },
            {
              travel_date: "2015-05-31 14:36:00.0",
            },
            {
              travel_date: "2015-12-28 15:37:00.0",
            },
            {
              travel_date: "2015-05-29 16:12:00.0",
            },
            {
              travel_date: "2015-12-29 15:29:00.0",
            },
            {
              travel_date: "2015-06-30 14:27:00.0",
            },
            {
              travel_date: "2015-10-28 16:07:00.0",
            },
            {
              travel_date: "2015-11-28 16:54:00.0",
            },
            {
              travel_date: "2015-09-04 17:32:00.0",
            },
            {
              travel_date: "2015-04-29 10:45:00.0",
            },
            {
              travel_date: "2015-05-25 13:44:00.0",
            },
            {
              travel_date: "2015-05-29 09:36:00.0",
            },
            {
              travel_date: "2015-09-18 08:16:00.0",
            },
            {
              travel_date: "2015-05-25 13:36:00.0",
            },
            {
              travel_date: "2015-05-26 16:44:00.0",
            },
            {
              travel_date: "2015-11-27 15:54:00.0",
            },
            {
              travel_date: "2015-09-25 13:56:00.0",
            },
            {
              travel_date: "2015-08-27 15:17:00.0",
            },
            {
              travel_date: "2015-07-26 16:26:00.0",
            },
            {
              travel_date: "2015-09-19 07:24:00.0",
            },
            {
              travel_date: "2015-06-30 12:43:00.0",
            },
            {
              travel_date: "2015-08-31 10:33:00.0",
            },
            {
              travel_date: "2015-11-24 15:06:00.0",
            },
            {
              travel_date: "2015-08-30 09:01:00.0",
            },
            {
              travel_date: "2015-05-27 14:28:00.0",
            },
            {
              travel_date: "2015-08-25 16:41:00.0",
            },
            {
              travel_date: "2015-07-16 07:02:00.0",
            },
            {
              travel_date: "2015-11-07 17:30:00.0",
            },
            {
              travel_date: "2015-01-31 09:08:00.0",
            },
            {
              travel_date: "2015-07-16 08:34:00.0",
            },
            {
              travel_date: "2015-07-25 15:18:00.0",
            },
            {
              travel_date: "2015-12-21 14:53:00.0",
            },
            {
              travel_date: "2015-10-22 15:23:00.0",
            },
            {
              travel_date: "2015-03-22 15:06:00.0",
            },
            {
              travel_date: "2015-07-23 16:18:00.0",
            },
            {
              travel_date: "2015-06-30 07:03:00.0",
            },
            {
              travel_date: "2015-09-19 11:00:00.0",
            },
            {
              travel_date: "2015-08-09 17:01:00.0",
            },
            {
              travel_date: "2015-10-21 16:39:00.0",
            },
            {
              travel_date: "2015-09-20 15:16:00.0",
            },
            {
              travel_date: "2015-06-11 18:11:00.0",
            },
            {
              travel_date: "2015-08-17 11:17:00.0",
            },
            {
              travel_date: "2015-11-11 17:30:00.0",
            },
            {
              travel_date: "2015-04-22 14:37:00.0",
            },
            {
              travel_date: "2015-12-20 16:13:00.0",
            },
            {
              travel_date: "2015-09-20 09:56:00.0",
            },
            {
              travel_date: "2015-08-17 14:25:00.0",
            },
            {
              travel_date: "2015-04-22 11:13:00.0",
            },
            {
              travel_date: "2015-06-20 10:43:00.0",
            },
            {
              travel_date: "2015-08-18 16:49:00.0",
            },
            {
              travel_date: "2015-02-18 16:07:00.0",
            },
            {
              travel_date: "2015-03-17 13:38:00.0",
            },
            {
              travel_date: "2015-08-20 11:33:00.0",
            },
            {
              travel_date: "2015-05-23 10:44:00.0",
            },
            {
              travel_date: "2015-12-17 16:45:00.0",
            },
            {
              travel_date: "2015-09-25 07:48:00.0",
            },
            {
              travel_date: "2015-05-21 11:36:00.0",
            },
            {
              travel_date: "2015-09-19 13:32:00.0",
            },
            {
              travel_date: "2015-02-25 07:15:00.0",
            },
            {
              travel_date: "2015-05-19 13:44:00.0",
            },
            {
              travel_date: "2015-02-08 09:30:00.0",
            },
            {
              travel_date: "2015-12-14 15:12:00.0",
            },
            {
              travel_date: "2015-07-15 16:25:00.0",
            },
            {
              travel_date: "2015-06-07 08:26:00.0",
            },
            {
              travel_date: "2015-08-08 09:00:00.0",
            },
            {
              travel_date: "2015-08-08 09:24:00.0",
            },
            {
              travel_date: "2015-02-12 14:46:00.0",
            },
            {
              travel_date: "2015-11-09 09:21:00.0",
            },
            {
              travel_date: "2015-08-11 11:40:00.0",
            },
            {
              travel_date: "2015-02-06 08:30:00.0",
            },
            {
              travel_date: "2015-05-09 09:19:00.0",
            },
            {
              travel_date: "2015-07-12 14:41:00.0",
            },
            {
              travel_date: "2015-01-14 16:47:00.0",
            },
            {
              travel_date: "2015-07-08 11:25:00.0",
            },
            {
              travel_date: "2015-10-15 14:38:00.0",
            },
            {
              travel_date: "2015-09-15 13:47:00.0",
            },
            {
              travel_date: "2015-07-15 13:25:00.0",
            },
            {
              travel_date: "2015-12-12 16:52:00.0",
            },
            {
              travel_date: "2015-08-13 15:32:00.0",
            },
            {
              travel_date: "2015-10-15 13:38:00.0",
            },
            {
              travel_date: "2015-08-15 13:00:00.0",
            },
            {
              travel_date: "2015-03-09 11:21:00.0",
            },
            {
              travel_date: "2015-11-04 08:21:00.0",
            },
            {
              travel_date: "2015-12-13 15:28:00.0",
            },
            {
              travel_date: "2015-08-12 09:08:00.0",
            },
            {
              travel_date: "2015-04-10 15:52:00.0",
            },
            {
              travel_date: "2015-03-14 11:37:00.0",
            },
            {
              travel_date: "2015-10-10 15:06:00.0",
            },
            {
              travel_date: "2015-05-09 14:27:00.0",
            },
            {
              travel_date: "2015-05-11 16:27:00.0",
            },
            {
              travel_date: "2015-05-09 13:27:00.0",
            },
            {
              travel_date: "2015-07-08 14:49:00.0",
            },
            {
              travel_date: "2015-09-11 15:31:00.0",
            },
            {
              travel_date: "2015-05-12 10:51:00.0",
            },
            {
              travel_date: "2015-02-02 08:30:00.0",
            },
            {
              travel_date: "2015-11-10 13:29:00.0",
            },
            {
              travel_date: "2015-01-11 14:15:00.0",
            },
            {
              travel_date: "2015-02-15 10:38:00.0",
            },
            {
              travel_date: "2015-06-09 16:26:00.0",
            },
            {
              travel_date: "2015-09-09 16:07:00.0",
            },
            {
              travel_date: "2015-09-01 08:31:00.0",
            },
            {
              travel_date: "2015-11-09 16:37:00.0",
            },
            {
              travel_date: "2015-03-11 14:05:00.0",
            },
            {
              travel_date: "2015-11-12 12:13:00.0",
            },
            {
              travel_date: "2015-11-08 16:21:00.0",
            },
            {
              travel_date: "2015-01-15 09:23:00.0",
            },
            {
              travel_date: "2015-12-10 14:44:00.0",
            },
            {
              travel_date: "2015-01-04 13:47:00.0",
            },
            {
              travel_date: "2015-09-05 14:55:00.0",
            },
            {
              travel_date: "2015-05-05 14:43:00.0",
            },
            {
              travel_date: "2015-10-25 17:06:00.0",
            },
            {
              travel_date: "2015-12-13 08:12:00.0",
            },
            {
              travel_date: "2015-04-02 09:36:00.0",
            },
            {
              travel_date: "2015-09-07 14:47:00.0",
            },
            {
              travel_date: "2015-05-06 14:35:00.0",
            },
            {
              travel_date: "2015-07-02 10:49:00.0",
            },
            {
              travel_date: "2015-10-04 16:38:00.0",
            },
            {
              travel_date: "2015-01-12 08:23:00.0",
            },
            {
              travel_date: "2015-04-03 16:12:00.0",
            },
            {
              travel_date: "2015-06-02 15:50:00.0",
            },
            {
              travel_date: "2015-08-07 12:48:00.0",
            },
            {
              travel_date: "2015-04-03 16:36:00.0",
            },
            {
              travel_date: "2015-01-02 16:23:00.0",
            },
            {
              travel_date: "2015-01-02 16:39:00.0",
            },
            {
              travel_date: "2015-09-05 12:47:00.0",
            },
            {
              travel_date: "2015-12-06 10:52:00.0",
            },
            {
              travel_date: "2015-09-30 15:07:00.0",
            },
            {
              travel_date: "2015-07-23 08:01:00.0",
            },
            {
              travel_date: "2015-08-22 07:24:00.0",
            },
            {
              travel_date: "2015-07-31 15:25:00.0",
            },
            {
              travel_date: "2015-10-22 08:14:00.0",
            },
            {
              travel_date: "2015-08-31 15:08:00.0",
            },
            {
              travel_date: "2015-12-31 15:28:00.0",
            },
            {
              travel_date: "2015-09-29 13:39:00.0",
            },
            {
              travel_date: "2015-12-29 16:36:00.0",
            },
            {
              travel_date: "2015-09-28 15:15:00.0",
            },
            {
              travel_date: "2015-12-27 10:44:00.0",
            },
            {
              travel_date: "2015-01-28 15:47:00.0",
            },
            {
              travel_date: "2015-11-27 09:29:00.0",
            },
            {
              travel_date: "2015-12-27 09:20:00.0",
            },
            {
              travel_date: "2015-09-30 11:07:00.0",
            },
            {
              travel_date: "2015-08-27 16:40:00.0",
            },
            {
              travel_date: "2015-06-29 09:02:00.0",
            },
            {
              travel_date: "2015-08-27 15:24:00.0",
            },
            {
              travel_date: "2015-11-24 14:05:00.0",
            },
            {
              travel_date: "2015-11-18 08:53:00.0",
            },
            {
              travel_date: "2015-07-31 10:49:00.0",
            },
            {
              travel_date: "2015-09-25 16:39:00.0",
            },
            {
              travel_date: "2015-07-26 13:33:00.0",
            },
            {
              travel_date: "2015-03-28 11:53:00.0",
            },
            {
              travel_date: "2015-01-16 07:23:00.0",
            },
            {
              travel_date: "2015-04-25 16:04:00.0",
            },
            {
              travel_date: "2015-04-29 11:12:00.0",
            },
            {
              travel_date: "2015-07-29 11:49:00.0",
            },
            {
              travel_date: "2015-08-27 13:08:00.0",
            },
            {
              travel_date: "2015-09-16 08:31:00.0",
            },
            {
              travel_date: "2015-04-25 15:12:00.0",
            },
            {
              travel_date: "2015-05-09 18:11:00.0",
            },
            {
              travel_date: "2015-10-17 10:38:00.0",
            },
            {
              travel_date: "2015-09-21 14:47:00.0",
            },
            {
              travel_date: "2015-12-19 11:36:00.0",
            },
            {
              travel_date: "2015-08-16 10:32:00.0",
            },
            {
              travel_date: "2015-09-23 15:07:00.0",
            },
            {
              travel_date: "2015-09-23 15:39:00.0",
            },
            {
              travel_date: "2015-08-09 17:32:00.0",
            },
            {
              travel_date: "2015-09-23 15:23:00.0",
            },
            {
              travel_date: "2015-05-30 08:11:00.0",
            },
            {
              travel_date: "2015-08-20 15:16:00.0",
            },
            {
              travel_date: "2015-09-19 10:39:00.0",
            },
            {
              travel_date: "2015-10-20 16:06:00.0",
            },
            {
              travel_date: "2015-10-18 10:30:00.0",
            },
            {
              travel_date: "2015-09-19 16:39:00.0",
            },
            {
              travel_date: "2015-10-23 12:46:00.0",
            },
            {
              travel_date: "2015-07-18 15:49:00.0",
            },
            {
              travel_date: "2015-04-22 12:44:00.0",
            },
            {
              travel_date: "2015-02-23 11:22:00.0",
            },
            {
              travel_date: "2015-11-19 15:37:00.0",
            },
            {
              travel_date: "2015-09-18 13:55:00.0",
            },
            {
              travel_date: "2015-10-22 09:14:00.0",
            },
            {
              travel_date: "2015-06-16 15:18:00.0",
            },
            {
              travel_date: "2015-07-16 15:57:00.0",
            },
            {
              travel_date: "2015-12-16 15:12:00.0",
            },
            {
              travel_date: "2015-08-19 14:24:00.0",
            },
            {
              travel_date: "2015-05-16 15:03:00.0",
            },
            {
              travel_date: "2015-04-22 09:44:00.0",
            },
            {
              travel_date: "2015-09-17 16:47:00.0",
            },
            {
              travel_date: "2015-04-19 13:52:00.0",
            },
            {
              travel_date: "2015-12-19 13:36:00.0",
            },
            {
              travel_date: "2015-09-15 17:39:00.0",
            },
            {
              travel_date: "2015-08-17 15:16:00.0",
            },
            {
              travel_date: "2015-09-18 14:15:00.0",
            },
          ],
          lastModified: 1684415954169,
          limitBy: 1000,
          offset: 0,
          dataId: "00f7d193-5f95-4851-88a2-221d8eabdb3f",
        },
        customStyles: "",
        customScripts: [],
        analytics: [
          {
            value: false,
            key: "subTotals",
            label: "Row Sub Totals",
          },
        ],
        properties: {
          title: {
            show: false,
            value: "",
            padding: 0,
            fontSize: 32,
            fontColor: {
              a: 1,
              b: 0,
              g: 0,
              r: 0,
            },
            alignment: "center",
            position: "top",
          },
          subTitle: {
            show: false,
            value: "",
            padding: 0,
            fontSize: 24,
            fontColor: {
              a: 1,
              b: 0,
              g: 0,
              r: 0,
            },
            alignment: "center",
            position: "top",
          },
          format: {
            formatFields: [],
            formatDatatype: "",
            activeFieldId: "",
            showAll: false,
          },
          axisRange: {
            fields: [],
            activeDatatype: "",
            activeId: "",
            gridLines: [],
          },
          cache: {
            isCacheEnabled: false,
            interval: "00:00:01",
          },
          card: {
            title: "",
            prefixType: "selectIcon",
            suffixType: "selectIcon",
            prefix: "",
            suffix: "",
            prefixColor: {
              a: 1,
              b: 0,
              g: 0,
              r: 0,
            },
            suffixColor: {
              a: 1,
              b: 0,
              g: 0,
              r: 0,
            },
          },
          bar: {
            barType: "stacked",
          },
          radial: {
            showRadial: false,
          },
          legend: {
            legendPosition: "right",
          },
          formatColor: {
            defaultColor: {
              r: 84,
              g: 108,
              b: 230,
              a: 1,
            },
            showAll: false,
            dataColors: [],
            formatColorStyle: "",
            formatColorField: "",
            minimum: {
              r: 183,
              g: 192,
              b: 232,
              a: 1,
            },
            maximum: {
              r: 84,
              g: 108,
              b: 230,
              a: 1,
            },
            backgroundColor: false,
          },
          labels: {
            rotateLabels: false,
          },
          crosstab: {
            showGrandTotals: false,
            showRowGrandTotals: false,
            showColumnGrandTotals: false,
            showSubTotals: false,
            showRowSubTotals: false,
            showColumnSubTotals: false,
            grandTotalsPosition: "Bottom",
            subTotalsPosition: "Auto",
          },
        },
        reportInfo: {
          location: "",
          uuid: "",
          reportName: "Untitled 1",
        },
        cellMenuData: null,
        showHiddenColumns: false,
        showHiddenRows: false,
        database: "HIUSER",
        dateFunctions: {
          dateTime: [
            {
              value: "TODATE",
              label: "Date",
              part: "date",
              key: "sql.typeConversion.todate",
              returns: "date",
              parameters: [
                {
                  name: "column",
                },
              ],
            },
            {
              value: "DAY",
              label: "Days",
              part: "day",
              key: "sql.dateTime.day",
              returns: "numeric",
              parameters: [
                {
                  name: "datetime",
                },
              ],
            },
            {
              value: "MONTH",
              label: "Months",
              part: "month",
              key: "sql.dateTime.month",
              returns: "numeric",
              parameters: [
                {
                  name: "datetime",
                },
              ],
            },
            {
              value: "QUARTER",
              label: "Quarters",
              part: "quarter",
              key: "sql.dateTime.quarter",
              returns: "numeric",
              parameters: [
                {
                  name: "datetime",
                },
              ],
            },
            {
              value: "YEAR",
              label: "Years",
              part: "year",
              key: "sql.dateTime.year",
              returns: "numeric",
              parameters: [
                {
                  name: "datetime",
                },
              ],
            },
            {
              value: "TOTIME",
              label: "Time",
              part: "time",
              key: "sql.typeConversion.totime",
              returns: "time",
              parameters: [
                {
                  name: "column",
                },
              ],
            },
            {
              value: "HOUR",
              label: "Hours",
              part: "hour",
              key: "sql.dateTime.hour",
              returns: "numeric",
              parameters: [
                {
                  name: "datetime",
                },
              ],
            },
            {
              value: "MINUTE",
              label: "Minutes",
              part: "minute",
              key: "sql.dateTime.minute",
              returns: "numeric",
              parameters: [
                {
                  name: "datetime",
                },
              ],
            },
            {
              value: "SECOND",
              label: "Seconds",
              part: "second",
              key: "sql.dateTime.second",
              returns: "numeric",
              parameters: [
                {
                  name: "datetime",
                },
              ],
            },
            {
              value: "INIAVIDUAL",
              label: "Individual",
              part: "individual",
              key: "individual",
              parameters: [
                {
                  name: "datetime",
                },
              ],
            },
          ],
          date: [
            {
              value: "TODATE",
              label: "Date",
              part: "date",
              key: "sql.typeConversion.todate",
              returns: "date",
              parameters: [
                {
                  name: "column",
                },
              ],
            },
            {
              value: "DAY",
              label: "Days",
              part: "day",
              key: "sql.dateTime.day",
              returns: "numeric",
              parameters: [
                {
                  name: "datetime",
                },
              ],
            },
            {
              value: "MONTH",
              label: "Months",
              part: "month",
              key: "sql.dateTime.month",
              returns: "numeric",
              parameters: [
                {
                  name: "datetime",
                },
              ],
            },
            {
              value: "QUARTER",
              label: "Quarters",
              part: "quarter",
              key: "sql.dateTime.quarter",
              returns: "numeric",
              parameters: [
                {
                  name: "datetime",
                },
              ],
            },
            {
              value: "YEAR",
              label: "Years",
              part: "year",
              key: "sql.dateTime.year",
              returns: "numeric",
              parameters: [
                {
                  name: "datetime",
                },
              ],
            },
            {
              value: "INIAVIDUAL",
              label: "Individual",
              part: "individual",
              key: "individual",
              parameters: [
                {
                  name: "datetime",
                },
              ],
            },
          ],
          time: [
            {
              value: "TOTIME",
              label: "Time",
              part: "time",
              key: "sql.typeConversion.totime",
              returns: "time",
              parameters: [
                {
                  name: "column",
                },
              ],
            },
            {
              value: "HOUR",
              label: "Hours",
              part: "hour",
              key: "sql.dateTime.hour",
              returns: "numeric",
              parameters: [
                {
                  name: "datetime",
                },
              ],
            },
            {
              value: "MINUTE",
              label: "Minutes",
              part: "minute",
              key: "sql.dateTime.minute",
              returns: "numeric",
              parameters: [
                {
                  name: "datetime",
                },
              ],
            },
            {
              value: "SECOND",
              label: "Seconds",
              part: "second",
              key: "sql.dateTime.second",
              returns: "numeric",
              parameters: [
                {
                  name: "datetime",
                },
              ],
            },
            {
              value: "INIAVIDUAL",
              label: "Individual",
              part: "individual",
              key: "individual",
              parameters: [
                {
                  name: "datetime",
                },
              ],
            },
          ],
        },
      },
    ],
    layout: {
      metadataShelf: true,
      toolsAreaShelf: true,
      fieldsAreaShelf: true,
    },
    hasUnsavedData: true,
    hrSidebar: "metadata",
  },
  future: [],
};

export const props = {
  filter: {
    column: "travel_details.travel_date",
    label: "travel_date",
    databaseFunction: null,
    dataType: "dateTime",
    backendDataType: "java.sql.Timestamp",
    condition: "EQUALS",
    values: [],
    valuesMode: "custom",
    mode: "auto",
    groupBy: ["db.generic.groupBy.group"],
    orderBy: "",
    valuesRange: {},
    rangeValuesType: "",
    dateTimeToggle: false,
    rangeSelectionToggole: true,
    maxInput: "",
    minInput: "",
    valuesList: [],
    drillDownId: "",
    uid: "87d68e3a-f434-4f1f-9567-c455d00ffce4",
    configId: "28e437cd-3387-442e-91e5-3b111c3735f5",
    dataId: "8aeb0d66-3363-458b-815c-cc60dad5e4b6",
    columnID: "20300",
    datePart: "individual",
    currentDate: "Fri May 19 2023 13:14:09 GMT+0530 (India Standard Time)",
    dateValuesType: "datePicker",
    anchor: {
      anchorDate: "2023-05-19 13:14:09",
      isAnchor: false,
      active: 1,
      relativePart: "",
      part: "",
      value: 0,
      direction: "",
      lastInput: 3,
      nextInput: 3,
    },
    mapping: {
      isEnabled: true,
      unique: true,
      valueDBFunction: null,
      DisplayDBFunction: null,
      isDefaultFunction: true,
      valueDisplayMap: [],
      valueAliasName: "random",
      orderBy: {
        display: "asc",
        value: "none",
      },
      valueDBFuntionInfo: {},
      valueColumn: {
        alias: "travel_date",
        fullyQualifiedColumn: "travel_details.travel_date",
        id: "20300",
        defaultFunction: "db.generic.groupBy.group",
        type: {
          "java.sql.Timestamp": "dateTime",
        },
      },
      displayColumn: {
        alias: "travel_date",
        fullyQualifiedColumn: "travel_details.travel_date",
        id: "20300",
        defaultFunction: "db.generic.groupBy.group",
        type: {
          "java.sql.Timestamp": "dateTime",
        },
      },
    },
    cascade: {
      isEnabled: false,
      filters: [],
      filtersCount: 0,
    },
    active: true,
  },
  valueDateFunc: "individual",
};