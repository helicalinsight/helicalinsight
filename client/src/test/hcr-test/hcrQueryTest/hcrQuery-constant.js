
export const queryTestConstant = [
    {
        title: 'testing simple query',
        input: {
            "connectionDetails": {
                "id": "1",
                "baseType": "global.jdbc",
                "dataSourceType": "Managed DataSource",
                "name": "SampleTravelDataDerby",
                "type": "dynamicDataSource"
            },
            "reqQuery": {
                "id": 1,
                "name": "Query1",
                "config": "select * from \"dimdate\"",
                "connectionDetails": {
                    "id": "1",
                    "baseType": "global.jdbc",
                    "dataSourceType": "Managed DataSource",
                    "name": "SampleTravelDataDerby",
                    "type": "dynamicDataSource"
                },
                "executeQueryData": {
                    "data": [],
                    "field": []
                },
                "isNameEditable": false,
                "isSaved": true,
                "parameterList": []
            }
        },
        output: {
            "name": "_temp_filename",
            "version": 5,
            "efwd": {
                "dataSources": {
                    "connections": [
                        {
                            "connection": {
                                "id": "1",
                                "type": "global.jdbc",
                                "connDetails": {
                                    "globalId": "1"
                                }
                            }
                        }
                    ]
                },
                "dataMaps": [
                    {
                        "dataMap": {
                            "name": "Query1",
                            "id": 1,
                            "connection": "1",
                            "type": "sql",
                            "query": "select * from \"dimdate\"",
                            "parameters": []
                        }
                    }
                ]
            }
        }
    }, {
        title: 'testing query with parameter',
        input: {
            "connectionDetails": {
                "id": "1",
                "baseType": "global.jdbc",
                "dataSourceType": "Managed DataSource",
                "name": "SampleTravelDataDerby",
                "type": "dynamicDataSource"
            },
            "reqQuery": {
                "id": 1,
                "name": "Query1",
                "config": "select * from \"dimdate\" where \"dim_id\" =  ${dim_id}",
                "connectionDetails": {
                    "id": "1",
                    "baseType": "global.jdbc",
                    "dataSourceType": "Managed DataSource",
                    "name": "SampleTravelDataDerby",
                    "type": "dynamicDataSource"
                },
                "executeQueryData": {
                    "data": [],
                    "field": []
                },
                "isNameEditable": false,
                "isSaved": true,
                "parameterList": [
                    {
                        "id": 1,
                        "type": "java.lang.Integer",
                        "name": "dim_id",
                        "value": "2"
                    }
                ],
                "temp_uuid": "_temp_6cf3f474-473c-4a47-b9bb-1bb2e1955cf6"
            },
            paraList: [
                {
                    id: 1,
                    canvasValues: {
                        open: "'",
                        close: "'"
                    }
                }
            ]
        }, 
        output: {
            "name": "_temp_filename",
            "version": 5,
            "efwd": {
                "dataSources": {
                    "connections": [
                        {
                            "connection": {
                                "id": "1",
                                "type": "global.jdbc",
                                "connDetails": {
                                    "globalId": "1"
                                }
                            }
                        }
                    ]
                },
                "dataMaps": [
                    {
                        "dataMap": {
                            "name": "Query1",
                            "id": 1,
                            "connection": "1",
                            "type": "sql",
                            "query": "select * from \"dimdate\" where \"dim_id\" =  ${dim_id}",
                            "parameters": [
                                {
                                    "parameter": {
                                        "default": 2,
                                        "name": "dim_id",
                                        "type": "Numeric",
                                        openQuote: "'",
                                        closeQuote: "'"
                                    }
                                }
                            ]
                        }
                    }
                ]
            }
        }
    }, {
        title: 'testing date query with parameters',
        input: {
            "connectionDetails": {
                "id": "1",
                "baseType": "global.jdbc",
                "dataSourceType": "Managed DataSource",
                "name": "SampleTravelDataDerby",
                "type": "dynamicDataSource"
            },
            "reqQuery": {
                "id": 1,
                "name": "Query1",
                "config": "select * from \"travel_details\" where cast(\"travel_date\" as date) between ${startdate} and ${enddate}",
                "connectionDetails": {
                    "id": "1",
                    "baseType": "global.jdbc",
                    "dataSourceType": "Managed DataSource",
                    "name": "SampleTravelDataDerby",
                    "type": "dynamicDataSource"
                },
                "executeQueryData": {
                    "data": [
                        {
                            "travel_id": 1,
                            "travel_date": "2015-09-04 08:22:00.0",
                            "travel_type": "Domestic",
                            "travel_medium": "Bus",
                            "source_id": 8,
                            "source": "Bangalore",
                            "destination_id": 16,
                            "destination": "Chennai",
                            "travel_cost": 1350,
                            "mode_of_payment": "Credit",
                            "booking_platform": "Makemytrip",
                            "travelled_by": 29
                        },
                        {
                            "travel_id": 2,
                            "travel_date": "2015-09-25 10:22:00.0",
                            "travel_type": "Domestic",
                            "travel_medium": "Bus",
                            "source_id": 57,
                            "source": "New Delhi",
                            "destination_id": 31,
                            "destination": "Jaipur",
                            "travel_cost": 1200,
                            "mode_of_payment": "Credit",
                            "booking_platform": "Website",
                            "travelled_by": 11
                        },
                        {
                            "travel_id": 3,
                            "travel_date": "2015-09-25 14:13:00.0",
                            "travel_type": "Domestic",
                            "travel_medium": "Bus",
                            "source_id": 57,
                            "source": "New Delhi",
                            "destination_id": 31,
                            "destination": "Jaipur",
                            "travel_cost": 1200,
                            "mode_of_payment": "Credit",
                            "booking_platform": "Website",
                            "travelled_by": 11
                        },
                        {
                            "travel_id": 4,
                            "travel_date": "2015-10-20 11:33:00.0",
                            "travel_type": "Domestic",
                            "travel_medium": "Cab",
                            "source_id": 16,
                            "source": "Chennai",
                            "destination_id": 19,
                            "destination": "Coimbatore",
                            "travel_cost": 800,
                            "mode_of_payment": "Cash",
                            "booking_platform": "Agent",
                            "travelled_by": 11
                        },
                        {
                            "travel_id": 5,
                            "travel_date": "2015-08-12 13:19:00.0",
                            "travel_type": "Domestic",
                            "travel_medium": "Misc",
                            "source_id": 65,
                            "source": "Pune",
                            "destination_id": 29,
                            "destination": "Hyderabad",
                            "travel_cost": 1200,
                            "mode_of_payment": "Cash",
                            "booking_platform": "Agent",
                            "travelled_by": 14
                        },
                        {
                            "travel_id": 6,
                            "travel_date": "2015-10-09 13:18:00.0",
                            "travel_type": "Domestic",
                            "travel_medium": "Train",
                            "source_id": 55,
                            "source": "Nagpur",
                            "destination_id": 31,
                            "destination": "Jaipur",
                            "travel_cost": 2400,
                            "mode_of_payment": "Credit",
                            "booking_platform": "Makemytrip",
                            "travelled_by": 23
                        },
                        {
                            "travel_id": 7,
                            "travel_date": "2015-05-21 10:23:00.0",
                            "travel_type": "Domestic",
                            "travel_medium": "Flight",
                            "source_id": 11,
                            "source": "Bhubaneshwar",
                            "destination_id": 33,
                            "destination": "Jammu",
                            "travel_cost": 10000,
                            "mode_of_payment": "Credit",
                            "booking_platform": "Website",
                            "travelled_by": 11
                        },
                        {
                            "travel_id": 8,
                            "travel_date": "2015-07-08 13:29:00.0",
                            "travel_type": "International",
                            "travel_medium": "Flight",
                            "source_id": 16,
                            "source": "Chennai",
                            "destination_id": 63,
                            "destination": "Philippines",
                            "travel_cost": 42000,
                            "mode_of_payment": "Net Banking",
                            "booking_platform": "Makemytrip",
                            "travelled_by": 45
                        },
                        {
                            "travel_id": 9,
                            "travel_date": "2015-11-10 13:29:00.0",
                            "travel_type": "Domestic",
                            "travel_medium": "Cab",
                            "source_id": 8,
                            "source": "Bangalore",
                            "destination_id": 54,
                            "destination": "Mysore",
                            "travel_cost": 800,
                            "mode_of_payment": "Net Banking",
                            "booking_platform": "Website",
                            "travelled_by": 41
                        },
                        {
                            "travel_id": 10,
                            "travel_date": "2015-12-24 10:23:00.0",
                            "travel_type": "Domestic",
                            "travel_medium": "Misc",
                            "source_id": 15,
                            "source": "Chandigarh",
                            "destination_id": 25,
                            "destination": "Gurgaon",
                            "travel_cost": 1200,
                            "mode_of_payment": "Cheque",
                            "booking_platform": "Agent",
                            "travelled_by": 33
                        }
                    ],
                    "field": [
                        {
                            "name": "travel_id",
                            "clazz": "java.lang.Integer"
                        },
                        {
                            "name": "travel_date",
                            "clazz": "java.sql.Timestamp"
                        },
                        {
                            "name": "travel_type",
                            "clazz": "java.lang.String"
                        },
                        {
                            "name": "travel_medium",
                            "clazz": "java.lang.String"
                        },
                        {
                            "name": "source_id",
                            "clazz": "java.lang.Integer"
                        },
                        {
                            "name": "source",
                            "clazz": "java.lang.String"
                        },
                        {
                            "name": "destination_id",
                            "clazz": "java.lang.Integer"
                        },
                        {
                            "name": "destination",
                            "clazz": "java.lang.String"
                        },
                        {
                            "name": "travel_cost",
                            "clazz": "java.lang.Integer"
                        },
                        {
                            "name": "mode_of_payment",
                            "clazz": "java.lang.String"
                        },
                        {
                            "name": "booking_platform",
                            "clazz": "java.lang.String"
                        },
                        {
                            "name": "travelled_by",
                            "clazz": "java.lang.Integer"
                        }
                    ]
                },
                "isNameEditable": false,
                "isSaved": true,
                "parameterList": [
                    {
                        "id": 1,
                        "type": "java.lang.String",
                        "name": "startdate",
                        "value": "2000-01-01"
                    },
                    {
                        "id": 2,
                        "type": "java.lang.String",
                        "name": "enddate",
                        "value": "2020-01-01"
                    }
                ],
                "temp_uuid": "_temp_4618f9dd-3178-4ec9-8d26-f165952edc24"
            },
            paraList: [
                {
                    id: 1,
                    canvasValues: {
                        open: "",
                        close: ""
                    }
                },
                {
                    id: 2,
                    canvasValues: {
                        open: "",
                        close: ""
                    }
                }
            ]
        },
        output: {
            "name": "_temp_filename",
            "version": 5,
            "efwd": {
                "dataSources": {
                    "connections": [
                        {
                            "connection": {
                                "id": "1",
                                "type": "global.jdbc",
                                "connDetails": {
                                    "globalId": "1"
                                }
                            }
                        }
                    ]
                },
                "dataMaps": [
                    {
                        "dataMap": {
                            "name": "Query1",
                            "id": 1,
                            "connection": "1",
                            "type": "sql",
                            "query": "select * from \"travel_details\" where cast(\"travel_date\" as date) between ${startdate} and ${enddate}",
                            "parameters": [
                                {
                                    "parameter": {
                                        "default": "2000-01-01",
                                        "name": "startdate",
                                        "type": "String",
                                        openQuote: '',
                                        closeQuote: ''
                                    }
                                },
                                {
                                    "parameter": {
                                        "default": "2020-01-01",
                                        "name": "enddate",
                                        "type": "String",
                                        openQuote: '',
                                        closeQuote: ''
                                    }
                                }
                            ]
                        }
                    }
                ]
            }
        }
    }
]