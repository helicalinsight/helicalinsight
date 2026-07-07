import { v4 as uuidv4 } from "uuid";
import { dateFunctions } from "../../components/hi-reports/utils/constants"
const intialAnalytics = `
[
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
]
`

const intialMetadataState = `
{
	"classifier": "db.generic",
	"name": "SampleTravelData",
	"dataSource": {
		"sync": false,
		"id": "1202",
		"catSchemaPredicted": false,
		"catalog": "SampleTravelData",
		"schema": "",
		"type": "dynamicDataSource",
		"baseType": "global.jdbc"
	},
	"uniqueId": "942a93a5-778f-4cb8-98cf-2a1595eae08d",
	"tables": {
		"employee_details": {
			"id": "152371825108bf241d5e58d460282bf0",
			"alias": "employee_details",
			"columns": {
				"employee_id": {
					"alias": "employee_id",
					"fullyQualifiedColumn": "employee_details.employee_id",
					"columnId": "0802fa6d-f96f-4d60-adca-42919b23f657",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Integer": "numeric"
					}
				},
				"employee_name": {
					"alias": "employee_name",
					"fullyQualifiedColumn": "employee_details.employee_name",
					"columnId": "ebb38d8a-ea9c-4de2-a24e-43b6ff238457",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"age": {
					"alias": "age",
					"fullyQualifiedColumn": "employee_details.age",
					"columnId": "255aeb7e-4378-4686-8db0-c092172ef2f6",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Integer": "numeric"
					}
				},
				"address": {
					"alias": "address",
					"fullyQualifiedColumn": "employee_details.address",
					"columnId": "0ccf1d28-42c1-42ba-9af9-42924261edbf",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				}
			},
			"name": "employee_details",
			"key": "845ccc3c-179d-4be2-9bd1-d1d436dd25a1"
		},
		"geo_cordinates": {
			"id": "16639182c9f9434f6c23adc92c13ca91",
			"alias": "geo_cordinates",
			"columns": {
				"location_id": {
					"alias": "location_id",
					"fullyQualifiedColumn": "geo_cordinates.location_id",
					"columnId": "964498e2-53ab-4154-848a-3d3258d192e4",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Integer": "numeric"
					}
				},
				"location": {
					"alias": "location",
					"fullyQualifiedColumn": "geo_cordinates.location",
					"columnId": "eb35761c-6809-4232-b335-b421058ddd84",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"latitude": {
					"alias": "latitude",
					"fullyQualifiedColumn": "geo_cordinates.latitude",
					"columnId": "b51c23a7-3670-493e-846e-0a55394be4c6",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Double": "numeric"
					}
				},
				"longitude": {
					"alias": "longitude",
					"fullyQualifiedColumn": "geo_cordinates.longitude",
					"columnId": "0eb3133d-4b56-43bf-9e25-a8b92c5b4de7",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Double": "numeric"
					}
				}
			},
			"name": "geo_cordinates",
			"key": "87a94954-95ec-47d8-bf8e-868ca7c15375"
		},
		"dimdate": {
			"id": "9d13652ec6bfac5f3776d2c344a6ed69",
			"alias": "dimdate",
			"columns": {
				"dim_id": {
					"alias": "dim_id",
					"fullyQualifiedColumn": "dimdate.dim_id",
					"columnId": "08e4054c-631a-4809-b73a-92b5ec12395d",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Integer": "numeric"
					}
				},
				"fiscal_year": {
					"alias": "fiscal_year",
					"fullyQualifiedColumn": "dimdate.fiscal_year",
					"columnId": "7f0e321d-99a9-4278-aa0e-d56c3b4388d9",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.sql.Date": "date"
					}
				},
				"modified_date": {
					"alias": "modified_date",
					"fullyQualifiedColumn": "dimdate.modified_date",
					"columnId": "95e19c18-8dfc-42c5-a24a-237c4fd874e5",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.sql.Timestamp": "dateTime"
					}
				},
				"date_key": {
					"alias": "date_key",
					"fullyQualifiedColumn": "dimdate.date_key",
					"columnId": "c68a9b06-95bc-44e9-b906-de28b829a346",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"day_number": {
					"alias": "day_number",
					"fullyQualifiedColumn": "dimdate.day_number",
					"columnId": "d4fb8b98-6928-42e2-ac81-86dbec33e767",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"fiscal_month_name": {
					"alias": "fiscal_month_name",
					"fullyQualifiedColumn": "dimdate.fiscal_month_name",
					"columnId": "eb66aa35-b4d8-4f99-bfbf-402704bdbbc0",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"fiscal_month_label": {
					"alias": "fiscal_month_label",
					"fullyQualifiedColumn": "dimdate.fiscal_month_label",
					"columnId": "19f72b89-c12c-4ec2-a44f-d60a29dde6db",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"created_date": {
					"alias": "created_date",
					"fullyQualifiedColumn": "dimdate.created_date",
					"columnId": "8379e935-63e0-4cab-9ee6-d5c1c2cad990",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"created_time": {
					"alias": "created_time",
					"fullyQualifiedColumn": "dimdate.created_time",
					"columnId": "f9944947-dc5e-438c-bf7a-b757f53a7f17",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"rating": {
					"alias": "rating",
					"fullyQualifiedColumn": "dimdate.rating",
					"columnId": "76d02078-56dc-4ebe-8323-c2b8c1425756",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				}
			},
			"name": "dimdate",
			"key": "7b9fb34c-aa61-4a81-9ae5-db2f4311558c"
		},
		"travel_details": {
			"id": "0d08fba10235e4dea8cb57fd92e29e1d",
			"alias": "travel_details",
			"columns": {
				"travel_id": {
					"alias": "travel_id",
					"fullyQualifiedColumn": "travel_details.travel_id",
					"columnId": "e7acb658-34a4-4f3f-82a5-2054b0f4c63e",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Integer": "numeric"
					}
				},
				"travel_date": {
					"alias": "travel_date",
					"fullyQualifiedColumn": "travel_details.travel_date",
					"columnId": "2b122d7c-2b8f-4943-911c-8852f433e286",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.sql.Timestamp": "dateTime"
					}
				},
				"travel_type": {
					"alias": "travel_type",
					"fullyQualifiedColumn": "travel_details.travel_type",
					"columnId": "311bff54-318f-4f55-91f3-7aa23c70aa88",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"travel_medium": {
					"alias": "travel_medium",
					"fullyQualifiedColumn": "travel_details.travel_medium",
					"columnId": "5493c458-239e-41f3-b538-a3e1d70fc261",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"source_id": {
					"alias": "source_id",
					"fullyQualifiedColumn": "travel_details.source_id",
					"columnId": "73c7c9b8-7d2c-465c-a34b-957a49437d76",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Integer": "numeric"
					}
				},
				"source": {
					"alias": "source",
					"fullyQualifiedColumn": "travel_details.source",
					"columnId": "a628244f-d28a-415e-afac-7b5d966cfa90",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"destination_id": {
					"alias": "destination_id",
					"fullyQualifiedColumn": "travel_details.destination_id",
					"columnId": "d21a6501-fbb7-40a2-819a-d2a5e8b26f07",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Integer": "numeric"
					}
				},
				"destination": {
					"alias": "destination",
					"fullyQualifiedColumn": "travel_details.destination",
					"columnId": "9fe13078-11b2-40d4-b252-fae123b29591",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"travel_cost": {
					"alias": "travel_cost",
					"fullyQualifiedColumn": "travel_details.travel_cost",
					"columnId": "ce787f95-665e-42be-8611-b24cc3d9dbc5",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Integer": "numeric"
					}
				},
				"mode_of_payment": {
					"alias": "mode_of_payment",
					"fullyQualifiedColumn": "travel_details.mode_of_payment",
					"columnId": "9d1a1ba2-7556-413c-a484-94755d28e38e",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"booking_platform": {
					"alias": "booking_platform",
					"fullyQualifiedColumn": "travel_details.booking_platform",
					"columnId": "a2970386-a9ea-40ab-8f94-a7e0da5c9b8c",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"travelled_by": {
					"alias": "travelled_by",
					"fullyQualifiedColumn": "travel_details.travelled_by",
					"columnId": "870fa9e2-1ab2-4abc-a7f3-3cff5b40d30f",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Integer": "numeric"
					}
				}
			},
			"name": "travel_details",
			"key": "71d10833-37ef-48c3-8e75-9ed9f71512b3"
		},
		"meeting_details": {
			"id": "28527591b9b87216cf89e68720df9c87",
			"alias": "meeting_details",
			"columns": {
				"meeting_id": {
					"alias": "meeting_id",
					"fullyQualifiedColumn": "meeting_details.meeting_id",
					"columnId": "8e8a65d2-581e-4279-b7b8-6b13f8e2971c",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Integer": "numeric"
					}
				},
				"meeting_date": {
					"alias": "meeting_date",
					"fullyQualifiedColumn": "meeting_details.meeting_date",
					"columnId": "e3e10b7a-9619-4b95-b337-a0bfcb0e0415",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.sql.Timestamp": "dateTime"
					}
				},
				"meeting_by": {
					"alias": "meeting_by",
					"fullyQualifiedColumn": "meeting_details.meeting_by",
					"columnId": "61e08e6f-3dd8-46bb-b2eb-80b4ce217d3e",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Integer": "numeric"
					}
				},
				"client_name": {
					"alias": "client_name",
					"fullyQualifiedColumn": "meeting_details.client_name",
					"columnId": "511ac68d-55bb-4cc8-8d9a-a6cdaf54deb2",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"meeting_purpose": {
					"alias": "meeting_purpose",
					"fullyQualifiedColumn": "meeting_details.meeting_purpose",
					"columnId": "30a8249a-22fb-43c5-ad78-2eb92cab8b5e",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"meeting_impact": {
					"alias": "meeting_impact",
					"fullyQualifiedColumn": "meeting_details.meeting_impact",
					"columnId": "be94b59c-5933-4112-89c4-58ba65a68cbc",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"meet_cancellation_status": {
					"alias": "meet_cancellation_status",
					"fullyQualifiedColumn": "meeting_details.meet_cancellation_status",
					"columnId": "6ccecf26-9757-415b-a065-80505731cc56",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"cancellation_reason": {
					"alias": "cancellation_reason",
					"fullyQualifiedColumn": "meeting_details.cancellation_reason",
					"columnId": "6642ab3b-644a-4836-9b93-216c84d26322",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				}
			},
			"name": "meeting_details",
			"key": "9227421e-578f-4c5d-bd77-f931a5b32c28"
		}
	},
	"sets": [
		[
			"geo_cordinates"
		],
		[
			"meeting_details"
		],
		[
			"employee_details"
		],
		[
			"dimdate"
		],
		[
			"travel_details"
		]
	],
	"metadataName": "testy",
	"metadataDir": "naresh",
	"formData": {
		"location": "1642396928027",
		"metadataFileName": "942a93a5-778f-4cb8-98cf-2a1595eae08d.metadata"
	},
	"uid": "fd93fa23-6eb6-42c5-8cc4-de8ac7394ae0"
}
`
const metadataTables = JSON.parse(`{
	"status": 1,
	"response": {
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
		"uniqueId": "e9be6771-995b-40eb-a01c-304857a100a1",
		"tables": {
			"meeting_details": {
				"id": "9645c648a1c0dbeec1287aaf1e996db3",
				"alias": "meeting_details",
				"columns": {
					"meeting_id": {
						"alias": "meeting_id",
						"fullyQualifiedColumn": "meeting_details.meeting_id",
						"columnId": "dff5938e-eb78-410c-ad57-145ed3fa4500",
						"defaultFunction": "db.generic.aggregate.sum",
						"type": {
							"java.lang.Integer": "numeric"
						}
					},
					"meeting_date": {
						"alias": "meeting_date",
						"fullyQualifiedColumn": "meeting_details.meeting_date",
						"columnId": "1292750c-f0f5-46b4-9524-924948d3e06b",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.sql.Timestamp": "dateTime"
						}
					},
					"meeting_by": {
						"alias": "meeting_by",
						"fullyQualifiedColumn": "meeting_details.meeting_by",
						"columnId": "74f064fa-1639-4555-9b37-c29658775598",
						"defaultFunction": "db.generic.aggregate.sum",
						"type": {
							"java.lang.Integer": "numeric"
						}
					},
					"client_name": {
						"alias": "client_name",
						"fullyQualifiedColumn": "meeting_details.client_name",
						"columnId": "5697901d-3bbc-4312-af78-74221421a90c",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.lang.String": "text"
						}
					},
					"meeting_purpose": {
						"alias": "meeting_purpose",
						"fullyQualifiedColumn": "meeting_details.meeting_purpose",
						"columnId": "01026ef3-56b7-4503-b966-2e0f1e776844",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.lang.String": "text"
						}
					},
					"meeting_impact": {
						"alias": "meeting_impact",
						"fullyQualifiedColumn": "meeting_details.meeting_impact",
						"columnId": "ccf7994c-6fec-491a-bc0d-bcb124d6f1ae",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.lang.String": "text"
						}
					},
					"meet_cancellation_status": {
						"alias": "meet_cancellation_status",
						"fullyQualifiedColumn": "meeting_details.meet_cancellation_status",
						"columnId": "7a5f76d7-f365-445c-a228-927c87702c39",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.lang.String": "text"
						}
					},
					"cancellation_reason": {
						"alias": "cancellation_reason",
						"fullyQualifiedColumn": "meeting_details.cancellation_reason",
						"columnId": "caff99b5-29f6-48eb-9f63-372cc3d7b497",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.lang.String": "text"
						}
					}
				},
				"name": "meeting_details"
			},
			"travel_details": {
				"id": "8a28627d07d04ef096d9935f12e0c7e9",
				"alias": "travel_details",
				"columns": {
					"travel_id": {
						"alias": "travel_id",
						"fullyQualifiedColumn": "travel_details.travel_id",
						"columnId": "7c1cc79e-3203-4332-a042-345f116e50cc",
						"defaultFunction": "db.generic.aggregate.sum",
						"type": {
							"java.lang.Integer": "numeric"
						}
					},
					"travel_date": {
						"alias": "travel_date",
						"fullyQualifiedColumn": "travel_details.travel_date",
						"columnId": "c18188cb-89ef-4a8d-94e2-111a1f9e6b64",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.sql.Timestamp": "dateTime"
						}
					},
					"travel_type": {
						"alias": "travel_type",
						"fullyQualifiedColumn": "travel_details.travel_type",
						"columnId": "a7739e81-9ffc-420b-9a10-e0e6b1bb2ea2",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.lang.String": "text"
						}
					},
					"travel_medium": {
						"alias": "travel_medium",
						"fullyQualifiedColumn": "travel_details.travel_medium",
						"columnId": "5e238de2-4c61-4672-b5ce-a629208bb495",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.lang.String": "text"
						}
					},
					"source_id": {
						"alias": "source_id",
						"fullyQualifiedColumn": "travel_details.source_id",
						"columnId": "4ce15085-67f2-4038-8a1a-68597b6a3ee3",
						"defaultFunction": "db.generic.aggregate.sum",
						"type": {
							"java.lang.Integer": "numeric"
						}
					},
					"source": {
						"alias": "source",
						"fullyQualifiedColumn": "travel_details.source",
						"columnId": "3eda6a68-ed53-4ae2-898a-ef83f35d6b6a",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.lang.String": "text"
						}
					},
					"destination_id": {
						"alias": "destination_id",
						"fullyQualifiedColumn": "travel_details.destination_id",
						"columnId": "bb650022-743e-4ffd-ae21-38edbca8092f",
						"defaultFunction": "db.generic.aggregate.sum",
						"type": {
							"java.lang.Integer": "numeric"
						}
					},
					"destination": {
						"alias": "destination",
						"fullyQualifiedColumn": "travel_details.destination",
						"columnId": "bfdc49c3-852b-4d5f-87dd-22119ccda84d",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.lang.String": "text"
						}
					},
					"travel_cost": {
						"alias": "travel_cost",
						"fullyQualifiedColumn": "travel_details.travel_cost",
						"columnId": "ad0cafd9-8265-4844-97fd-e96b2aac9925",
						"defaultFunction": "db.generic.aggregate.sum",
						"type": {
							"java.lang.Integer": "numeric"
						}
					},
					"mode_of_payment": {
						"alias": "mode_of_payment",
						"fullyQualifiedColumn": "travel_details.mode_of_payment",
						"columnId": "c5a4fa44-8c74-4829-ada9-ffabeba2de2e",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.lang.String": "text"
						}
					},
					"booking_platform": {
						"alias": "booking_platform",
						"fullyQualifiedColumn": "travel_details.booking_platform",
						"columnId": "e54cfa6f-6bf0-4ac2-be12-0c88f6b58577",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.lang.String": "text"
						}
					},
					"travelled_by": {
						"alias": "travelled_by",
						"fullyQualifiedColumn": "travel_details.travelled_by",
						"columnId": "5b153c28-0fea-415b-93c0-baa0066e744a",
						"defaultFunction": "db.generic.aggregate.sum",
						"type": {
							"java.lang.Integer": "numeric"
						}
					}
				},
				"name": "travel_details"
			},
			"dimdate": {
				"id": "4ac5d9f68b58bd7c0d179146e46795be",
				"alias": "dimdate",
				"columns": {
					"dim_id": {
						"alias": "dim_id",
						"fullyQualifiedColumn": "dimdate.dim_id",
						"columnId": "56692a4c-0d81-4652-96c3-525aeb723fac",
						"defaultFunction": "db.generic.aggregate.sum",
						"type": {
							"java.lang.Integer": "numeric"
						}
					},
					"fiscal_year": {
						"alias": "fiscal_year",
						"fullyQualifiedColumn": "dimdate.fiscal_year",
						"columnId": "e8c6b258-ea2a-4c24-b8ac-2df05ff6bf8f",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.sql.Date": "date"
						}
					},
					"modified_date": {
						"alias": "modified_date",
						"fullyQualifiedColumn": "dimdate.modified_date",
						"columnId": "5e4f525f-176c-4722-90e6-96dec4957e46",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.sql.Timestamp": "dateTime"
						}
					},
					"date_key": {
						"alias": "date_key",
						"fullyQualifiedColumn": "dimdate.date_key",
						"columnId": "ff9261dc-fcce-4b49-9d31-dbc3c5db8c9c",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.lang.String": "text"
						}
					},
					"day_number": {
						"alias": "day_number",
						"fullyQualifiedColumn": "dimdate.day_number",
						"columnId": "8ecc0b24-3b70-4dd1-b616-218d0955ae90",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.lang.String": "text"
						}
					},
					"fiscal_month_name": {
						"alias": "fiscal_month_name",
						"fullyQualifiedColumn": "dimdate.fiscal_month_name",
						"columnId": "92245d80-8743-4749-807a-cf986e66e6f5",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.lang.String": "text"
						}
					},
					"fiscal_month_label": {
						"alias": "fiscal_month_label",
						"fullyQualifiedColumn": "dimdate.fiscal_month_label",
						"columnId": "7bb5ed45-0632-478c-9071-9f1aca31b749",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.lang.String": "text"
						}
					},
					"created_date": {
						"alias": "created_date",
						"fullyQualifiedColumn": "dimdate.created_date",
						"columnId": "beec622b-7e1b-4ef5-bedc-33ae6d8b459f",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.lang.String": "text"
						}
					},
					"created_time": {
						"alias": "created_time",
						"fullyQualifiedColumn": "dimdate.created_time",
						"columnId": "47bd4f83-1bb8-4cfa-8b14-e7d38801c62c",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.lang.String": "text"
						}
					},
					"rating": {
						"alias": "rating",
						"fullyQualifiedColumn": "dimdate.rating",
						"columnId": "47d19691-1422-4fb0-a8f1-272a9f8218df",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.lang.String": "text"
						}
					}
				},
				"name": "dimdate"
			},
			"employee_details": {
				"id": "4e1fd245f4d13b77be423a43f01d80b2",
				"alias": "employee_details",
				"columns": {
					"employee_id": {
						"alias": "employee_id",
						"fullyQualifiedColumn": "employee_details.employee_id",
						"columnId": "4c10d3ea-167a-464d-b9fd-ab0a9028ad31",
						"defaultFunction": "db.generic.aggregate.sum",
						"type": {
							"java.lang.Integer": "numeric"
						}
					},
					"employee_name": {
						"alias": "employee_name",
						"fullyQualifiedColumn": "employee_details.employee_name",
						"columnId": "7bf280a2-2a69-4e9a-a285-306d75012b93",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.lang.String": "text"
						}
					},
					"age": {
						"alias": "age",
						"fullyQualifiedColumn": "employee_details.age",
						"columnId": "b930d4ad-070a-4972-8c32-8b8398f99a7e",
						"defaultFunction": "db.generic.aggregate.sum",
						"type": {
							"java.lang.Integer": "numeric"
						}
					},
					"address": {
						"alias": "address",
						"fullyQualifiedColumn": "employee_details.address",
						"columnId": "4e574cad-7252-4907-923d-e42ad2b1511a",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.lang.String": "text"
						}
					}
				},
				"name": "employee_details"
			},
			"geo_cordinates": {
				"id": "be534112989b616b194bc59c2fb25a42",
				"alias": "geo_cordinates",
				"columns": {
					"location_id": {
						"alias": "location_id",
						"fullyQualifiedColumn": "geo_cordinates.location_id",
						"columnId": "1788dbff-4b08-4d99-b8cb-c9edf051d2e5",
						"defaultFunction": "db.generic.aggregate.sum",
						"type": {
							"java.lang.Integer": "numeric"
						}
					},
					"location": {
						"alias": "location",
						"fullyQualifiedColumn": "geo_cordinates.location",
						"columnId": "6abbc3c5-f00e-4b6f-907b-ef96a8da64bd",
						"defaultFunction": "db.generic.groupBy.group",
						"type": {
							"java.lang.String": "text"
						}
					},
					"latitude": {
						"alias": "latitude",
						"fullyQualifiedColumn": "geo_cordinates.latitude",
						"columnId": "88c00c69-661d-4505-a1a8-c031e85302ea",
						"defaultFunction": "db.generic.aggregate.sum",
						"type": {
							"java.lang.Double": "numeric"
						}
					},
					"longitude": {
						"alias": "longitude",
						"fullyQualifiedColumn": "geo_cordinates.longitude",
						"columnId": "d2776de5-5718-4b0e-ab80-8d5296a8fa60",
						"defaultFunction": "db.generic.aggregate.sum",
						"type": {
							"java.lang.Double": "numeric"
						}
					}
				},
				"name": "geo_cordinates"
			}
		},
		"sets": [
			[
				"geo_cordinates",
				"travel_details",
				"dimdate",
				"employee_details",
				"meeting_details"
			]
		],
		"metadataName": "Sample Travel MD",
		"metadataDir": "Adhoc Metadata"
	}
}`)
const allColumnFunctions = JSON.parse(`{
	"status": 1,
	"response": {
		"databaseFunctions": {
			"date": [
				{
					"key": "sql.date.dateadd",
					"value": "DATEADD",
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
					"value": "TODAY",
					"parameters": []
				},
				{
					"key": "sql.date.makedate",
					"value": "MAKEDATE",
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
					"value": "DATEDIFF",
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
					"value": "HOUR",
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
					"value": "MAKETIME",
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
					"value": "MAKEDATETIME",
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
					"value": "DATETIMEDIFF",
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
					"value": "DATETIMEADD",
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
					"value": "MONTH",
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
					"value": "MINUTE",
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
					"value": "SECOND",
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
					"value": "QUARTER",
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
					"value": "DAY",
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
					"value": "MONTHNAME",
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
					"value": "NOW",
					"parameters": []
				},
				{
					"key": "sql.dateTime.year",
					"value": "YEAR",
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
					"value": "CAST",
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
					"value": "TOCHAR",
					"parameters": [
						{
							"name": "column",
							"column": true
						}
					]
				},
				{
					"key": "sql.typeConversion.tonum",
					"value": "TONUM",
					"parameters": [
						{
							"name": "column",
							"column": true
						}
					]
				},
				{
					"key": "sql.typeConversion.todecimal",
					"value": "TODECIMAL",
					"parameters": [
						{
							"name": "column",
							"column": true
						}
					]
				},
				{
					"key": "sql.typeConversion.todate",
					"value": "TODATE",
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
					"value": "TODATETIME",
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
					"value": "TOTIME",
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
					"value": "dateToString",
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
					"value": "numericToString",
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
					"value": "dateTimeToString",
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
					"value": "timeToString",
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
					"value": "date",
					"parameters": [
						{
							"name": "column",
							"column": true
						}
					]
				},
				{
					"key": "sql.date.monthyear",
					"value": "month-year",
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
					"value": "currenttime",
					"parameters": []
				}
			],
			"numeric": [
				{
					"key": "sql.numeric.abs",
					"value": "ABS",
					"parameters": [
						{
							"name": "number",
							"column": true
						}
					]
				},
				{
					"key": "sql.numeric.acos",
					"value": "ACOS",
					"parameters": [
						{
							"name": "number",
							"column": true
						}
					]
				},
				{
					"key": "sql.numeric.asin",
					"value": "ASIN",
					"parameters": [
						{
							"name": "number",
							"column": true
						}
					]
				},
				{
					"key": "sql.numeric.atan",
					"value": "ATAN",
					"parameters": [
						{
							"name": "number",
							"column": true
						}
					]
				},
				{
					"key": "sql.numeric.atan2",
					"value": "ATAN2",
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
					"value": "FLOOR",
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
					"value": "CEILING",
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
					"value": "COS",
					"parameters": [
						{
							"name": "number",
							"column": true
						}
					]
				},
				{
					"key": "sql.numeric.cot",
					"value": "COT",
					"parameters": [
						{
							"name": "number",
							"column": true
						}
					]
				},
				{
					"key": "sql.numeric.degrees",
					"value": "DEGREES",
					"parameters": [
						{
							"name": "number",
							"column": true
						}
					]
				},
				{
					"key": "sql.numeric.div",
					"value": "DIV",
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
					"value": "EXP",
					"parameters": [
						{
							"name": "number",
							"column": true
						}
					]
				},
				{
					"key": "sql.numeric.ln",
					"value": "LN",
					"parameters": [
						{
							"name": "number",
							"column": true
						}
					]
				},
				{
					"key": "sql.numeric.log",
					"value": "LOG",
					"parameters": [
						{
							"name": "number",
							"column": true
						}
					]
				},
				{
					"key": "sql.numeric.pi",
					"value": "PI",
					"parameters": []
				},
				{
					"key": "sql.numeric.radians",
					"value": "RADIANS",
					"parameters": [
						{
							"name": "number",
							"column": true
						}
					]
				},
				{
					"key": "sql.numeric.sign",
					"value": "SIGN",
					"parameters": [
						{
							"name": "number",
							"column": true
						}
					]
				},
				{
					"key": "sql.numeric.sin",
					"value": "SIN",
					"parameters": [
						{
							"name": "number",
							"column": true
						}
					]
				},
				{
					"key": "sql.numeric.sqrt",
					"value": "SQRT",
					"parameters": [
						{
							"name": "number",
							"column": true
						}
					]
				},
				{
					"key": "sql.numeric.square",
					"value": "SQUARE",
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
					"value": "TAN",
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
					"value": "CONCAT",
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
					"value": "CONTAINS",
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
					"value": "ENDSWITH",
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
					"value": "STARTSWITH",
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
					"value": "FIND",
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
					"value": "LEFT",
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
					"value": "LENGTH",
					"parameters": [
						{
							"name": "string",
							"column": true
						}
					]
				},
				{
					"key": "sql.text.lower",
					"value": "LOWER",
					"parameters": [
						{
							"name": "string",
							"column": true
						}
					]
				},
				{
					"key": "sql.text.upper",
					"value": "UPPER",
					"parameters": [
						{
							"name": "string",
							"column": true
						}
					]
				},
				{
					"key": "sql.text.right",
					"value": "RIGHT",
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
					"value": "LTRIM",
					"parameters": [
						{
							"name": "string",
							"column": true
						}
					]
				},
				{
					"key": "sql.text.mid",
					"value": "MID",
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
					"value": "RTRIM",
					"parameters": [
						{
							"name": "string",
							"column": true
						}
					]
				},
				{
					"key": "sql.text.trim",
					"value": "TRIM",
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
					"value": "AND",
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
					"value": "CASE",
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
					"value": "ELSE",
					"parameters": [
						{
							"name": "statement_list",
							"column": true
						}
					]
				},
				{
					"key": "sql.logical.elseif",
					"value": "ELSEIF",
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
					"value": "IF",
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
					"value": "IFNULL",
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
					"value": "IIF",
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
					"value": "ISNULL",
					"parameters": []
				},
				{
					"key": "sql.logical.not",
					"value": "NOT",
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
					"value": "OR",
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
					"value": "WHEN",
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
					"value": "ZN",
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
	}
}`)
const reportData = JSON.parse(`
{"status":1,"response":{"data":[{"travel_details_booking_platform":"Agent"},{"travel_details_booking_platform":"Makemytrip"},{"travel_details_booking_platform":"Website"}],"metadata":[{"1":{"name":"travel_details_booking_platform","type":"text"}},{"rows":3}]}}
`)
const reportDataQury = JSON.parse(`
	{
		"status": 1,
		"response": {
			"classifier": "db.generic",
			"query": ""
		}
	}
`)
const saveResponse = JSON.parse(`
	{
		"status":1,
		"response":{
			"uuid":"9940ce07-1642-4a2f-83aa-eba874bca01b.hr"
		}
	}
`)

const reportState = JSON.parse(`
{
	"status": 1,
	"response": {
		"canvas": {
			"columns": [
				{
					"floatingType": "discrete",
					"hiddenIncludeInResultSet": false,
					"functionsDefinition": "",
					"addedAs": "column",
					"applyBeforeAggregate": false,
					"autogen_alias": "booking_platform",
					"label": "booking_platform",
					"groupBy": [
						"db.generic.groupBy.group"
					],
					"column": "travel_details.booking_platform",
					"type": {
						"dataType": "text",
						"backendDatatype": "java.lang.String"
					},
					"id": "watu211jwwf",
					"orderByColumn": false,
					"isNormalTable": true,
					"showOrderByColumn": false
				}
			]
		},
		"reportName": "test",
		"metadata": {
			"location": "1463377807724/1463377836985",
			"metadataFileName": "e9be6771-995b-40eb-a01c-304857a100a1.metadata",
			"databaseName": "SampleTravelData",
			"data": {
				"classifier": "db.generic",
				"name": "SampleTravelData",
				"dataSource": {
					"sync": false,
					"id": "1000",
					"catSchemaPredicted": false,
					"catalog": "SampleTravelData",
					"schema": "",
					"type": "dynamicDataSource",
					"baseType": "global.jdbc"
				},
				"uniqueId": "e9be6771-995b-40eb-a01c-304857a100a1",
				"tables": {
					"meeting_details": {
						"id": "28527591b9b87216cf89e68720df9c87",
						"alias": "meeting_details",
						"columns": {
							"meeting_id": {
								"alias": "meeting_id",
								"fullyQualifiedColumn": "meeting_details.meeting_id",
								"columnId": "dff5938e-eb78-410c-ad57-145ed3fa4500",
								"defaultFunction": "db.generic.aggregate.sum",
								"type": {
									"java.lang.Integer": "numeric"
								}
							},
							"meeting_date": {
								"alias": "meeting_date",
								"fullyQualifiedColumn": "meeting_details.meeting_date",
								"columnId": "1292750c-f0f5-46b4-9524-924948d3e06b",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.sql.Timestamp": "dateTime"
								}
							},
							"meeting_by": {
								"alias": "meeting_by",
								"fullyQualifiedColumn": "meeting_details.meeting_by",
								"columnId": "74f064fa-1639-4555-9b37-c29658775598",
								"defaultFunction": "db.generic.aggregate.sum",
								"type": {
									"java.lang.Integer": "numeric"
								}
							},
							"client_name": {
								"alias": "client_name",
								"fullyQualifiedColumn": "meeting_details.client_name",
								"columnId": "5697901d-3bbc-4312-af78-74221421a90c",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.lang.String": "text"
								}
							},
							"meeting_purpose": {
								"alias": "meeting_purpose",
								"fullyQualifiedColumn": "meeting_details.meeting_purpose",
								"columnId": "01026ef3-56b7-4503-b966-2e0f1e776844",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.lang.String": "text"
								}
							},
							"meeting_impact": {
								"alias": "meeting_impact",
								"fullyQualifiedColumn": "meeting_details.meeting_impact",
								"columnId": "ccf7994c-6fec-491a-bc0d-bcb124d6f1ae",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.lang.String": "text"
								}
							},
							"meet_cancellation_status": {
								"alias": "meet_cancellation_status",
								"fullyQualifiedColumn": "meeting_details.meet_cancellation_status",
								"columnId": "7a5f76d7-f365-445c-a228-927c87702c39",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.lang.String": "text"
								}
							},
							"cancellation_reason": {
								"alias": "cancellation_reason",
								"fullyQualifiedColumn": "meeting_details.cancellation_reason",
								"columnId": "caff99b5-29f6-48eb-9f63-372cc3d7b497",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.lang.String": "text"
								}
							}
						},
						"name": "meeting_details"
					},
					"travel_details": {
						"id": "0d08fba10235e4dea8cb57fd92e29e1d",
						"alias": "travel_details",
						"columns": {
							"travel_id": {
								"alias": "travel_id",
								"fullyQualifiedColumn": "travel_details.travel_id",
								"columnId": "7c1cc79e-3203-4332-a042-345f116e50cc",
								"defaultFunction": "db.generic.aggregate.sum",
								"type": {
									"java.lang.Integer": "numeric"
								}
							},
							"travel_date": {
								"alias": "travel_date",
								"fullyQualifiedColumn": "travel_details.travel_date",
								"columnId": "c18188cb-89ef-4a8d-94e2-111a1f9e6b64",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.sql.Timestamp": "dateTime"
								}
							},
							"travel_type": {
								"alias": "travel_type",
								"fullyQualifiedColumn": "travel_details.travel_type",
								"columnId": "a7739e81-9ffc-420b-9a10-e0e6b1bb2ea2",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.lang.String": "text"
								}
							},
							"travel_medium": {
								"alias": "travel_medium",
								"fullyQualifiedColumn": "travel_details.travel_medium",
								"columnId": "5e238de2-4c61-4672-b5ce-a629208bb495",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.lang.String": "text"
								}
							},
							"source_id": {
								"alias": "source_id",
								"fullyQualifiedColumn": "travel_details.source_id",
								"columnId": "4ce15085-67f2-4038-8a1a-68597b6a3ee3",
								"defaultFunction": "db.generic.aggregate.sum",
								"type": {
									"java.lang.Integer": "numeric"
								}
							},
							"source": {
								"alias": "source",
								"fullyQualifiedColumn": "travel_details.source",
								"columnId": "3eda6a68-ed53-4ae2-898a-ef83f35d6b6a",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.lang.String": "text"
								}
							},
							"destination_id": {
								"alias": "destination_id",
								"fullyQualifiedColumn": "travel_details.destination_id",
								"columnId": "bb650022-743e-4ffd-ae21-38edbca8092f",
								"defaultFunction": "db.generic.aggregate.sum",
								"type": {
									"java.lang.Integer": "numeric"
								}
							},
							"destination": {
								"alias": "destination",
								"fullyQualifiedColumn": "travel_details.destination",
								"columnId": "bfdc49c3-852b-4d5f-87dd-22119ccda84d",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.lang.String": "text"
								}
							},
							"travel_cost": {
								"alias": "travel_cost",
								"fullyQualifiedColumn": "travel_details.travel_cost",
								"columnId": "ad0cafd9-8265-4844-97fd-e96b2aac9925",
								"defaultFunction": "db.generic.aggregate.sum",
								"type": {
									"java.lang.Integer": "numeric"
								}
							},
							"mode_of_payment": {
								"alias": "mode_of_payment",
								"fullyQualifiedColumn": "travel_details.mode_of_payment",
								"columnId": "c5a4fa44-8c74-4829-ada9-ffabeba2de2e",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.lang.String": "text"
								}
							},
							"booking_platform": {
								"alias": "booking_platform",
								"fullyQualifiedColumn": "travel_details.booking_platform",
								"columnId": "e54cfa6f-6bf0-4ac2-be12-0c88f6b58577",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.lang.String": "text"
								}
							},
							"travelled_by": {
								"alias": "travelled_by",
								"fullyQualifiedColumn": "travel_details.travelled_by",
								"columnId": "5b153c28-0fea-415b-93c0-baa0066e744a",
								"defaultFunction": "db.generic.aggregate.sum",
								"type": {
									"java.lang.Integer": "numeric"
								}
							}
						},
						"name": "travel_details"
					},
					"dimdate": {
						"id": "9d13652ec6bfac5f3776d2c344a6ed69",
						"alias": "dimdate",
						"columns": {
							"dim_id": {
								"alias": "dim_id",
								"fullyQualifiedColumn": "dimdate.dim_id",
								"columnId": "56692a4c-0d81-4652-96c3-525aeb723fac",
								"defaultFunction": "db.generic.aggregate.sum",
								"type": {
									"java.lang.Integer": "numeric"
								}
							},
							"fiscal_year": {
								"alias": "fiscal_year",
								"fullyQualifiedColumn": "dimdate.fiscal_year",
								"columnId": "e8c6b258-ea2a-4c24-b8ac-2df05ff6bf8f",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.sql.Date": "date"
								}
							},
							"modified_date": {
								"alias": "modified_date",
								"fullyQualifiedColumn": "dimdate.modified_date",
								"columnId": "5e4f525f-176c-4722-90e6-96dec4957e46",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.sql.Timestamp": "dateTime"
								}
							},
							"date_key": {
								"alias": "date_key",
								"fullyQualifiedColumn": "dimdate.date_key",
								"columnId": "ff9261dc-fcce-4b49-9d31-dbc3c5db8c9c",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.lang.String": "text"
								}
							},
							"day_number": {
								"alias": "day_number",
								"fullyQualifiedColumn": "dimdate.day_number",
								"columnId": "8ecc0b24-3b70-4dd1-b616-218d0955ae90",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.lang.String": "text"
								}
							},
							"fiscal_month_name": {
								"alias": "fiscal_month_name",
								"fullyQualifiedColumn": "dimdate.fiscal_month_name",
								"columnId": "92245d80-8743-4749-807a-cf986e66e6f5",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.lang.String": "text"
								}
							},
							"fiscal_month_label": {
								"alias": "fiscal_month_label",
								"fullyQualifiedColumn": "dimdate.fiscal_month_label",
								"columnId": "7bb5ed45-0632-478c-9071-9f1aca31b749",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.lang.String": "text"
								}
							},
							"created_date": {
								"alias": "created_date",
								"fullyQualifiedColumn": "dimdate.created_date",
								"columnId": "beec622b-7e1b-4ef5-bedc-33ae6d8b459f",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.lang.String": "text"
								}
							},
							"created_time": {
								"alias": "created_time",
								"fullyQualifiedColumn": "dimdate.created_time",
								"columnId": "47bd4f83-1bb8-4cfa-8b14-e7d38801c62c",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.lang.String": "text"
								}
							},
							"rating": {
								"alias": "rating",
								"fullyQualifiedColumn": "dimdate.rating",
								"columnId": "47d19691-1422-4fb0-a8f1-272a9f8218df",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.lang.String": "text"
								}
							}
						},
						"name": "dimdate"
					},
					"employee_details": {
						"id": "152371825108bf241d5e58d460282bf0",
						"alias": "employee_details",
						"columns": {
							"employee_id": {
								"alias": "employee_id",
								"fullyQualifiedColumn": "employee_details.employee_id",
								"columnId": "4c10d3ea-167a-464d-b9fd-ab0a9028ad31",
								"defaultFunction": "db.generic.aggregate.sum",
								"type": {
									"java.lang.Integer": "numeric"
								}
							},
							"employee_name": {
								"alias": "employee_name",
								"fullyQualifiedColumn": "employee_details.employee_name",
								"columnId": "7bf280a2-2a69-4e9a-a285-306d75012b93",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.lang.String": "text"
								}
							},
							"age": {
								"alias": "age",
								"fullyQualifiedColumn": "employee_details.age",
								"columnId": "b930d4ad-070a-4972-8c32-8b8398f99a7e",
								"defaultFunction": "db.generic.aggregate.sum",
								"type": {
									"java.lang.Integer": "numeric"
								}
							},
							"address": {
								"alias": "address",
								"fullyQualifiedColumn": "employee_details.address",
								"columnId": "4e574cad-7252-4907-923d-e42ad2b1511a",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.lang.String": "text"
								}
							}
						},
						"name": "employee_details"
					},
					"geo_cordinates": {
						"id": "16639182c9f9434f6c23adc92c13ca91",
						"alias": "geo_cordinates",
						"columns": {
							"location_id": {
								"alias": "location_id",
								"fullyQualifiedColumn": "geo_cordinates.location_id",
								"columnId": "1788dbff-4b08-4d99-b8cb-c9edf051d2e5",
								"defaultFunction": "db.generic.aggregate.sum",
								"type": {
									"java.lang.Integer": "numeric"
								}
							},
							"location": {
								"alias": "location",
								"fullyQualifiedColumn": "geo_cordinates.location",
								"columnId": "6abbc3c5-f00e-4b6f-907b-ef96a8da64bd",
								"defaultFunction": "db.generic.groupBy.group",
								"type": {
									"java.lang.String": "text"
								}
							},
							"latitude": {
								"alias": "latitude",
								"fullyQualifiedColumn": "geo_cordinates.latitude",
								"columnId": "88c00c69-661d-4505-a1a8-c031e85302ea",
								"defaultFunction": "db.generic.aggregate.sum",
								"type": {
									"java.lang.Double": "numeric"
								}
							},
							"longitude": {
								"alias": "longitude",
								"fullyQualifiedColumn": "geo_cordinates.longitude",
								"columnId": "d2776de5-5718-4b0e-ab80-8d5296a8fa60",
								"defaultFunction": "db.generic.aggregate.sum",
								"type": {
									"java.lang.Double": "numeric"
								}
							}
						},
						"name": "geo_cordinates"
					}
				},
				"sets": [
					[
						"geo_cordinates",
						"travel_details",
						"dimdate",
						"employee_details",
						"meeting_details"
					]
				],
				"metadataName": "Sample Travel MD",
				"metadataDir": "HI Sample Reports/Adhoc Metadata"
			}
		},
		"state": {
			"columns": [
				{
					"floatingType": "discrete",
					"hiddenIncludeInResultSet": false,
					"functionsDefinition": "",
					"addedAs": "column",
					"applyBeforeAggregate": false,
					"autogen_alias": "booking_platform",
					"label": "booking_platform",
					"groupBy": [
						"db.generic.groupBy.group"
					],
					"column": "travel_details.booking_platform",
					"type": {
						"dataType": "text",
						"backendDatatype": "java.lang.String"
					},
					"id": "watu211jwwf",
					"orderByColumn": false,
					"isNormalTable": true,
					"showOrderByColumn": false
				}
			],
			"dateFunctions": {
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
			},
			"filters": [],
			"customFilterExpression": "",
			"customHavingExpression": "",
			"customFilterExpressionObj": {},
			"activeTool": "1",
			"customHavingExpressionObj": {},
			"havingExpressionIndexs": {},
			"filterExpressionIndexs": {},
			"options": {
				"limitBy": 1000,
				"prependTableNameToAlias": false
			},
			"visualisation": {
				"chartGroup": "",
				"selectedType": "Table",
				"settings": {
					"script": null,
					"vizscriptsEditMultipleMode": false
				},
				"vizSelectedScripts": [],
				"reportData": {}
			},
			"scripts": [],
			"styles": "",
			"customStyles": "",
			"customScripts": [],
			"isReportGenerated": true,
			"marks": [
				{
					"value": "_all_",
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
					}
				}
			],
			"interactiveMode": false,
			"analytics": ${intialAnalytics},
			"drillDown": false,
			"drillThrough": false,
			"drillThroughList": [],
			"drillDownList": [],
			"currentDrillDown": "",
			"toolbarConfig": {
				"selectable": false
			},
			"properties": {
				"titleConfig": {
					"position": "top",
					"align": "left",
					"title": "",
					"showTitle": false,
					"padding": 4,
					"color": "#000"
				},
				"subTitleConfig": {
					"position": "top",
					"align": "right",
					"subtitle": "",
					"showSubtitle": false,
					"padding": 4,
					"color": "#000"
				}
			}
		}
	}
}
`)


const intialMarks = `
[
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
]
`
const intialScripts = [
	{
		id: `hdi-custom-script-${uuidv4()}`,
		value: ""
	}
]
const intialOptions = {
	limitBy: 1000, sample: 'sample', prependTableNameToAlias: false
}

const intialProperties = `
{
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
}`
export const intialState = {
	activeReportId: null,
	reports: [
	],
	layout: {
		metadataShelf: true,
		toolsAreaShelf: true,
		fieldsAreaShelf: true,
	}
}

export const hreport_intial_view_state = JSON.parse(`
{
	"activeReportId": "test_123",
	"reports": [
		{
			"id": "test_123",
			"mode": "create",
			"active": true,
			"metadata": null,
			"functions": {},
			"databaseFunctions": {}, 
			"fields": [],
			"filters": [],
			"defaultValueDisplayMap": {},
			"editingField": null,
			"marksList": ${intialMarks} ,
			"activeMark": "2b7a6a89-884e-4850-81c3-196b43e863c3",
			"activeTool": "1",
			"scripts": [
				{
					"id": "hdi-custom-script-d0c03451-cfa9-4270-9f5e-03bf42e9f767",
					"value": ""
				}
			],
			"selectedScript": "hdi-custom-script-d0c03451-cfa9-4270-9f5e-03bf42e9f767",
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
			"analytics": ${intialAnalytics} ,
			"properties": ${intialProperties} ,
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
		"metadataShelf": true,
		"toolsAreaShelf": true,
		"fieldsAreaShelf": true
	}
}
`)

export const hreport_with_metadata = {
    "activeReportId": "test_123",
    "reports": [
        {
            "id": "test_123",
            "mode": "edit",
            "active": true,
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
                        "name": "geo_cordinates"
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
                        "name": "meeting_details"
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
                        "name": "employee_details"
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
                        "name": "dimdate"
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
                        "name": "travel_details"
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
                "uid": "e1c74da1-4327-47ce-922b-2908bad888e0"
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
            },
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
            "fields": [],
            "filters": [],
            "defaultValueDisplayMap": {},
            "editingField": null,
            "marksList": [
                {
                    "value": "_all_",
                    "id": "cc69720c-3ec8-40d9-865b-b0000e1e1315",
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
                    }
                }
            ],
            "activeMark": "cc69720c-3ec8-40d9-865b-b0000e1e1315",
            "activeTool": "1",
            "scripts": [
                {
                    "id": "hdi-custom-script-68263a83-a27e-472c-ab5d-65cfa5309eb8",
                    "value": ""
                }
            ],
            "selectedScript": "hdi-custom-script-68263a83-a27e-472c-ab5d-65cfa5309eb8",
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
            "selectedType": "Table",
            "reportData": {},
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
                    "fontColor": {a: 0,b: 0,g: 0,r: 0},
                    "alignment": "left",
                    "position": "top"
                },
                "subTitle": {
                    "show": false,
                    "value": "",
                    "padding": 0,
                    "fontSize": 12,
                    "fontColor": {a: 0,b: 0,g: 0,r: 0},
                    "alignment": "left",
                    "position": "top"
                }
            },
            "reportInfo": {
                "location": "",
                "uuid": "",
                "reportName": ""
            },
            "cellMenuData": null,
            "showHiddenColumns": false,
            "showHiddenRows": false,
            "database": "HIUSER",
            "dateFunctions": dateFunctions
        }
    ],
    "layout": {
        "metadataShelf": true,
        "toolsAreaShelf": true,
        "fieldsAreaShelf": true
    }
}
export const intailMetadata1 = {
	"classifier": "db.generic",
	"name": "SampleTravelData",
	"dataSource": {
		"sync": false,
		"id": "1202",
		"catSchemaPredicted": false,
		"catalog": "SampleTravelData",
		"schema": "",
		"type": "dynamicDataSource",
		"baseType": "global.jdbc"
	},
	"uniqueId": "942a93a5-778f-4cb8-98cf-2a1595eae08d",
	"tables": {
		"employee_details": {
			"id": "152371825108bf241d5e58d460282bf0",
			"alias": "employee_details",
			"columns": {
				"employee_id": {
					"alias": "employee_id",
					"fullyQualifiedColumn": "employee_details.employee_id",
					"columnId": "0802fa6d-f96f-4d60-adca-42919b23f657",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Integer": "numeric"
					}
				},
				"employee_name": {
					"alias": "employee_name",
					"fullyQualifiedColumn": "employee_details.employee_name",
					"columnId": "ebb38d8a-ea9c-4de2-a24e-43b6ff238457",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"age": {
					"alias": "age",
					"fullyQualifiedColumn": "employee_details.age",
					"columnId": "255aeb7e-4378-4686-8db0-c092172ef2f6",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Integer": "numeric"
					}
				},
				"address": {
					"alias": "address",
					"fullyQualifiedColumn": "employee_details.address",
					"columnId": "0ccf1d28-42c1-42ba-9af9-42924261edbf",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				}
			},
			"name": "employee_details",
			"key": "845ccc3c-179d-4be2-9bd1-d1d436dd25a1"
		},
		"geo_cordinates": {
			"id": "16639182c9f9434f6c23adc92c13ca91",
			"alias": "geo_cordinates",
			"columns": {
				"location_id": {
					"alias": "location_id",
					"fullyQualifiedColumn": "geo_cordinates.location_id",
					"columnId": "964498e2-53ab-4154-848a-3d3258d192e4",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Integer": "numeric"
					}
				},
				"location": {
					"alias": "location",
					"fullyQualifiedColumn": "geo_cordinates.location",
					"columnId": "eb35761c-6809-4232-b335-b421058ddd84",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"latitude": {
					"alias": "latitude",
					"fullyQualifiedColumn": "geo_cordinates.latitude",
					"columnId": "b51c23a7-3670-493e-846e-0a55394be4c6",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Double": "numeric"
					}
				},
				"longitude": {
					"alias": "longitude",
					"fullyQualifiedColumn": "geo_cordinates.longitude",
					"columnId": "0eb3133d-4b56-43bf-9e25-a8b92c5b4de7",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Double": "numeric"
					}
				}
			},
			"name": "geo_cordinates",
			"key": "87a94954-95ec-47d8-bf8e-868ca7c15375"
		},
		"dimdate": {
			"id": "9d13652ec6bfac5f3776d2c344a6ed69",
			"alias": "dimdate",
			"columns": {
				"dim_id": {
					"alias": "dim_id",
					"fullyQualifiedColumn": "dimdate.dim_id",
					"columnId": "08e4054c-631a-4809-b73a-92b5ec12395d",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Integer": "numeric"
					}
				},
				"fiscal_year": {
					"alias": "fiscal_year",
					"fullyQualifiedColumn": "dimdate.fiscal_year",
					"columnId": "7f0e321d-99a9-4278-aa0e-d56c3b4388d9",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.sql.Date": "date"
					}
				},
				"modified_date": {
					"alias": "modified_date",
					"fullyQualifiedColumn": "dimdate.modified_date",
					"columnId": "95e19c18-8dfc-42c5-a24a-237c4fd874e5",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.sql.Timestamp": "dateTime"
					}
				},
				"date_key": {
					"alias": "date_key",
					"fullyQualifiedColumn": "dimdate.date_key",
					"columnId": "c68a9b06-95bc-44e9-b906-de28b829a346",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"day_number": {
					"alias": "day_number",
					"fullyQualifiedColumn": "dimdate.day_number",
					"columnId": "d4fb8b98-6928-42e2-ac81-86dbec33e767",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"fiscal_month_name": {
					"alias": "fiscal_month_name",
					"fullyQualifiedColumn": "dimdate.fiscal_month_name",
					"columnId": "eb66aa35-b4d8-4f99-bfbf-402704bdbbc0",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"fiscal_month_label": {
					"alias": "fiscal_month_label",
					"fullyQualifiedColumn": "dimdate.fiscal_month_label",
					"columnId": "19f72b89-c12c-4ec2-a44f-d60a29dde6db",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"created_date": {
					"alias": "created_date",
					"fullyQualifiedColumn": "dimdate.created_date",
					"columnId": "8379e935-63e0-4cab-9ee6-d5c1c2cad990",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"created_time": {
					"alias": "created_time",
					"fullyQualifiedColumn": "dimdate.created_time",
					"columnId": "f9944947-dc5e-438c-bf7a-b757f53a7f17",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"rating": {
					"alias": "rating",
					"fullyQualifiedColumn": "dimdate.rating",
					"columnId": "76d02078-56dc-4ebe-8323-c2b8c1425756",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				}
			},
			"name": "dimdate",
			"key": "7b9fb34c-aa61-4a81-9ae5-db2f4311558c"
		},
		"travel_details": {
			"id": "0d08fba10235e4dea8cb57fd92e29e1d",
			"alias": "travel_details",
			"columns": {
				"travel_id": {
					"alias": "travel_id",
					"fullyQualifiedColumn": "travel_details.travel_id",
					"columnId": "e7acb658-34a4-4f3f-82a5-2054b0f4c63e",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Integer": "numeric"
					}
				},
				"travel_date": {
					"alias": "travel_date",
					"fullyQualifiedColumn": "travel_details.travel_date",
					"columnId": "2b122d7c-2b8f-4943-911c-8852f433e286",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.sql.Timestamp": "dateTime"
					}
				},
				"travel_type": {
					"alias": "travel_type",
					"fullyQualifiedColumn": "travel_details.travel_type",
					"columnId": "311bff54-318f-4f55-91f3-7aa23c70aa88",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"travel_medium": {
					"alias": "travel_medium",
					"fullyQualifiedColumn": "travel_details.travel_medium",
					"columnId": "5493c458-239e-41f3-b538-a3e1d70fc261",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"source_id": {
					"alias": "source_id",
					"fullyQualifiedColumn": "travel_details.source_id",
					"columnId": "73c7c9b8-7d2c-465c-a34b-957a49437d76",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Integer": "numeric"
					}
				},
				"source": {
					"alias": "source",
					"fullyQualifiedColumn": "travel_details.source",
					"columnId": "a628244f-d28a-415e-afac-7b5d966cfa90",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"destination_id": {
					"alias": "destination_id",
					"fullyQualifiedColumn": "travel_details.destination_id",
					"columnId": "d21a6501-fbb7-40a2-819a-d2a5e8b26f07",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Integer": "numeric"
					}
				},
				"destination": {
					"alias": "destination",
					"fullyQualifiedColumn": "travel_details.destination",
					"columnId": "9fe13078-11b2-40d4-b252-fae123b29591",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"travel_cost": {
					"alias": "travel_cost",
					"fullyQualifiedColumn": "travel_details.travel_cost",
					"columnId": "ce787f95-665e-42be-8611-b24cc3d9dbc5",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Integer": "numeric"
					}
				},
				"mode_of_payment": {
					"alias": "mode_of_payment",
					"fullyQualifiedColumn": "travel_details.mode_of_payment",
					"columnId": "9d1a1ba2-7556-413c-a484-94755d28e38e",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"booking_platform": {
					"alias": "booking_platform",
					"fullyQualifiedColumn": "travel_details.booking_platform",
					"columnId": "a2970386-a9ea-40ab-8f94-a7e0da5c9b8c",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"travelled_by": {
					"alias": "travelled_by",
					"fullyQualifiedColumn": "travel_details.travelled_by",
					"columnId": "870fa9e2-1ab2-4abc-a7f3-3cff5b40d30f",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Integer": "numeric"
					}
				}
			},
			"name": "travel_details",
			"key": "71d10833-37ef-48c3-8e75-9ed9f71512b3"
		},
		"meeting_details": {
			"id": "28527591b9b87216cf89e68720df9c87",
			"alias": "meeting_details",
			"columns": {
				"meeting_id": {
					"alias": "meeting_id",
					"fullyQualifiedColumn": "meeting_details.meeting_id",
					"columnId": "8e8a65d2-581e-4279-b7b8-6b13f8e2971c",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Integer": "numeric"
					}
				},
				"meeting_date": {
					"alias": "meeting_date",
					"fullyQualifiedColumn": "meeting_details.meeting_date",
					"columnId": "e3e10b7a-9619-4b95-b337-a0bfcb0e0415",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.sql.Timestamp": "dateTime"
					}
				},
				"meeting_by": {
					"alias": "meeting_by",
					"fullyQualifiedColumn": "meeting_details.meeting_by",
					"columnId": "61e08e6f-3dd8-46bb-b2eb-80b4ce217d3e",
					"defaultFunction": "db.generic.aggregate.sum",
					"type": {
						"java.lang.Integer": "numeric"
					}
				},
				"client_name": {
					"alias": "client_name",
					"fullyQualifiedColumn": "meeting_details.client_name",
					"columnId": "511ac68d-55bb-4cc8-8d9a-a6cdaf54deb2",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"meeting_purpose": {
					"alias": "meeting_purpose",
					"fullyQualifiedColumn": "meeting_details.meeting_purpose",
					"columnId": "30a8249a-22fb-43c5-ad78-2eb92cab8b5e",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"meeting_impact": {
					"alias": "meeting_impact",
					"fullyQualifiedColumn": "meeting_details.meeting_impact",
					"columnId": "be94b59c-5933-4112-89c4-58ba65a68cbc",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"meet_cancellation_status": {
					"alias": "meet_cancellation_status",
					"fullyQualifiedColumn": "meeting_details.meet_cancellation_status",
					"columnId": "6ccecf26-9757-415b-a065-80505731cc56",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				},
				"cancellation_reason": {
					"alias": "cancellation_reason",
					"fullyQualifiedColumn": "meeting_details.cancellation_reason",
					"columnId": "6642ab3b-644a-4836-9b93-216c84d26322",
					"defaultFunction": "db.generic.groupBy.group",
					"type": {
						"java.lang.String": "text"
					}
				}
			},
			"name": "meeting_details",
			"key": "9227421e-578f-4c5d-bd77-f931a5b32c28"
		}
	},
	"sets": [
		[
			"geo_cordinates"
		],
		[
			"meeting_details"
		],
		[
			"employee_details"
		],
		[
			"dimdate"
		],
		[
			"travel_details"
		]
	],
	"metadataName": "testy",
	"metadataDir": "naresh",
	"formData": {
		"location": "1642396928027",
		"metadataFileName": "942a93a5-778f-4cb8-98cf-2a1595eae08d.metadata"
	},
	"uid": "fd93fa23-6eb6-42c5-8cc4-de8ac7394ae0"
}
export const intailFields1 = [
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
]

 
export const getVisulisationState = (selectedType) =>{
	return {
		"activeReportId": "test_123",
		"reports": [
			{
				"id": "test_123",
				"mode": "create",
				"active": true,
				"metadata": intailMetadata1,
				"functions": {},
				"databaseFunctions": {},
				"fields": intailFields1,
				"filters": [],
				"defaultValueDisplayMap": {},
				"editingField": null,
				"marksList": JSON.parse(`${intialMarks}`),
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
				"sqlString": "",
				"options": {
					"limitBy": 1000,
					"sample": "sample",
					"prependTableNameToAlias": false
				},
				"interactiveMode": true,
				"drillDown": true,
				"drillThrough": true,
				"drillDownList": [],
				"currentDrillDown": "",
				"drillThroughList": [],
				"toolbarConfig": {
					"selectable": false
				},
				"selectedType": selectedType,
				"reportData": {
					"data": [
						{
							"booking_platform": "Agent",
							"mode_of_payment": "Cash",
							"sum_travel_cost": 2280590
						},
						{
							"booking_platform": "Agent",
							"mode_of_payment": "Cheque",
							"sum_travel_cost": 1031135
						},
						{
							"booking_platform": "Agent",
							"mode_of_payment": "Credit",
							"sum_travel_cost": 169338
						},
						{
							"booking_platform": "Agent",
							"mode_of_payment": "Net Banking",
							"sum_travel_cost": 160182
						},
						{
							"booking_platform": "Makemytrip",
							"mode_of_payment": "Cash",
							"sum_travel_cost": 874000
						},
						{
							"booking_platform": "Makemytrip",
							"mode_of_payment": "Cheque",
							"sum_travel_cost": 125600
						},
						{
							"booking_platform": "Makemytrip",
							"mode_of_payment": "Credit",
							"sum_travel_cost": 570572
						},
						{
							"booking_platform": "Makemytrip",
							"mode_of_payment": "Net Banking",
							"sum_travel_cost": 5149416
						},
						{
							"booking_platform": "Website",
							"mode_of_payment": "Cash",
							"sum_travel_cost": 464547
						},
						{
							"booking_platform": "Website",
							"mode_of_payment": "Cheque",
							"sum_travel_cost": 477696
						},
						{
							"booking_platform": "Website",
							"mode_of_payment": "Credit",
							"sum_travel_cost": 4714544
						},
						{
							"booking_platform": "Website",
							"mode_of_payment": "Net Banking",
							"sum_travel_cost": 2516350
						}
					],
					"metadata": [
						{
							"1": {
								"name": "booking_platform",
								"type": "text"
							},
							"2": {
								"name": "mode_of_payment",
								"type": "text"
							},
							"3": {
								"name": "sum_travel_cost",
								"type": "numeric"
							}
						},
						{
							"rows": 12
						}
					],
					"marksList":JSON.parse(`${intialMarks}`),
					"fields": intailFields1,
					"properties":JSON.parse(`${intialProperties}`),
					"filters": [],
					"limitBy": 1000,
					"offset": 0,
					"pageSize": 1000,
					"dataId": "15ffb6bf-c05b-43f9-89a0-593d4942b033"
				},
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
				"properties": JSON.parse(`${intialProperties}`),
				"reportInfo": {
					"location": "",
					"uuid": "",
					"reportName": "New1"
				},
				"cellMenuData": null,
				"showHiddenColumns": false,
				"showHiddenRows": false,
				"database": "HIUSER",
				"dateFunctions": dateFunctions,

			}
		],
		"layout": {
			"metadataShelf": true,
			"toolsAreaShelf": true,
			"fieldsAreaShelf": true
		}
	}
} 


const mocks = {
	metadataTables,
	allColumnFunctions,
	dateFunctions,
	reportData,
	reportDataQury,
	saveResponse,
	reportState,
	intialState,
	getVisulisationState
};
export default mocks;
