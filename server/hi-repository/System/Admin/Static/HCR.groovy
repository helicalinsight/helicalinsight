import java.awt.GraphicsEnvironment;
import groovy.json.*;
String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
def jsonObj='''
{
	"HCR": {
		"designerProperties": {
			"pageProperties": {
				"LETTER": {
					"pixel": {
						"width": 612,
						"height": 792
					},
					"inch": {
						"height": 11,
						"width": 8.5
					}
				},
				"NOTE": {
					"pixel": {
						"width": 540,
						"height": 720
					},
					"inch": {
						"height": 10,
						"width": 7.5
					}
				},
				"LEGAL": {
					"pixel": {
						"width": 612,
						"height": 1008
					},
					"inch": {
						"height": 14,
						"width": 8.5
					}
				},
				"A0": {
					"pixel": {
						"width": 2384,
						"height": 3370
					},
					"inch": {
						"height": 46.8056,
						"width": 33.1111
					}
				},
				"A1": {
					"pixel": {
						"width": 1684,
						"height": 2384
					},
					"inch": {
						"height": 33.1111,
						"width": 23.3889
					}
				},
				"A2": {
					"pixel": {
						"width": 1191,
						"height": 1684
					},
					"inch": {
						"height": 23.3889,
						"width": 16.5417
					}
				},
				"A3": {
					"pixel": {
						"width": 842,
						"height": 1191
					},
					"inch": {
						"height": 16.5417,
						"width": 11.6944
					}
				},
				"A4": {
					"pixel": {
						"width": 595,
						"height": 842
					},
					"inch": {
						"height": 11.6944,
						"width": 8.2639
					}
				},
				"A5": {
					"pixel": {
						"width": 420,
						"height": 595
					},
					"inch": {
						"height": 8.2639,
						"width": 5.8333
					}
				},
				"A6": {
					"pixel": {
						"width": 298,
						"height": 420
					},
					"inch": {
						"height": 5.8333,
						"width": 4.1389
					}
				},
				"A7": {
					"pixel": {
						"width": 210,
						"height": 298
					},
					"inch": {
						"height": 4.1389,
						"width": 2.9167
					}
				},
				"A8": {
					"pixel": {
						"width": 147,
						"height": 210
					},
					"inch": {
						"height": 2.9167,
						"width": 2.0417
					}
				},
				"A9": {
					"pixel": {
						"width": 105,
						"height": 147
					},
					"inch": {
						"height": 2.0417,
						"width": 1.4583
					}
				},
				"A10": {
					"pixel": {
						"width": 74,
						"height": 105
					},
					"inch": {
						"height": 1.4583,
						"width": 1.0278
					}
				},
				"B0": {
					"pixel": {
						"width": 2836,
						"height": 4008
					},
					"inch": {
						"height": 55.6667,
						"width": 39.3889
					}
				},
				"B1": {
					"pixel": {
						"width": 2004,
						"height": 2836
					},
					"inch": {
						"height": 39.3889,
						"width": 27.8333
					}
				},
				"B2": {
					"pixel": {
						"width": 1418,
						"height": 2004
					},
					"inch": {
						"height": 27.8333,
						"width": 19.6944
					}
				},
				"B3": {
					"pixel": {
						"width": 1002,
						"height": 1418
					},
					"inch": {
						"height": 19.6944,
						"width": 13.9167
					}
				},
				"B4": {
					"pixel": {
						"width": 709,
						"height": 1002
					},
					"inch": {
						"height": 13.9167,
						"width": 9.8472
					}
				},
				"B5": {
					"pixel": {
						"width": 501,
						"height": 709
					},
					"inch": {
						"height": 9.8472,
						"width": 6.9583
					}
				}
			},
			"whenNoDataType": {
				"No Pages" : "NoPages",
				"Blank Page" : "BlankPage",
				"All Sections No Detail" : "AllSectionsNoDetail",
				"No Data Section" : "NoDataSection"
			},
			"printOrder": [
				"Vertical",
				"Horizontal"
			],
			"orientation": [
				"Landscape",
				"Portrait"
			],
			"splitType": [
				"STRETCH",
				"IMMEDIATE",
				"PREVENT"
			],
			"footerPosition": [
				"Normal",
				"StackAtBottom",
				"ForceAtBottom",
				"CollateAtBottom"
			],
			"mode": [
				"Opaque",
				"Transparent"
			],
			"fontName": '''+JsonOutput.toJson(fonts)+''',
			"positionType": [
				"FixRelativeToTop",
				"FixRelativeToBottom",
				"Float"
			],
			"horizontalTextAlign": [
				"Left",
				"Right",
				"Center",
				"Justified"
			],
			"verticalTextAlign": [
				"Top",
				"Middle",
				"Bottom",
				"Justified"
			],
			"mode": [
				"Opaque",
				"Transparent"
			],
			"lineSpacing": [
				"Single",
				"1_1_2",
				"Double",
				"AtLeast",
				"Fixed",
				"Proportional"
			],
			"lineStyle": [
				"Dotted",
				"Double",
				"Dashed",
				"Solid"
			],
			"linePositionType": [
				"FixRelativeToTop",
				"FixRelativeToBottom",
				"Float"
			],
			"expressionType": [
				"simpleText",
				"default"
			],
			"stretchType": [
				"ContainerBottom",
				"ContainerHeight",
				"ElementGroupBottom",
				"ElementGroupHeight",
				"NoStretch"
			],
			"rotationType": [
				"Left",
				"Right",
				"UpsideDown",
				"None"
			],
			"lineDirection": [
				"BottomUp",
				"TopDown"
			],
			"langauge": [
				"java",
				"groovy",
				"javascript"
			],
			"incrementType": [
				"None",
				"Column",
				"Group",
				"Report",
				"Page"
			],
			"markUp": [
				"none",
				"styled",
				"html",
				"rtf"
			],
			"tabStopAlignment": [
				"Center",
				"Left",
				"Right"
			],
			"evaluationTime": [
				"Auto",
				"Band",
				"Column",
				"Group",
				"Master",
				"Now",
				"Report",
				"Page"
			],
			"calculations": [
				"Nothing",
				"Count",
				"Sum",
				"Average",
				"Lowest",
				"Highest",
				"Standard Deviation",
				"Variance",
				"System",
				"First",
				"Distinct Count"
			],
			"resetType": [
				"Report",
				"Page",
				"Column",
				"Group",
				"None",
				"Master"
			],
			"fill":["Solid"],
			"scaleImage":["Clip","FillFrame","RetainShape","RealHeight","RealSize"],
			"onErrorType":["Error","Blank","Icon"],
			"horizontalImageAlign":["Left","Center","Right"],
			"verticalImageAlign":["Top","Middle","Bottom"],
			"compositeElements": [],
			"title": {},
			"designerStyles": [],
			"parameters": {
				"classNames" : {
					"String" : "java.lang.String",
					"Collection" : "java.util.Collection",
					"Numeric" : "java.lang.Integer"
				}
			},
			"fields": [],
			"variables": {
				"classNames":{
					"Double" : "java.lang.Double",
					"Float" : "java.lang.Float",
					"Integer" : "java.lang.Integer",
					"Long" : "java.lang.Long",
					"Short" : "java.lang.Short",
					"Big Decimal" : "java.math.BigDecimal",
					"Time" : "java.sql.Time",
					"Boolean" : "java.lang.Boolean",
					"Sql Date" : "java.sql.Date",
					"Util Date" : "java.util.Date",
					"Timestamp" : "java.sql.Timestamp",
					"String" : "java.lang.String",
					"Collection" : "java.util.Collection"
				},
				"resetTypes" : {
					"byPage" : "Page",
					"byRecord" : "Report",
					"byColumn" : "Column",
					"byReport" : "Report"
					
				},
				"calculationsFuntionClassMap" : {
					"Nothing" : "java.lang.Integer",
					"Count": "java.lang.Integer",
					"Sum": "java.lang.Integer",
					"Average": "java.lang.Integer",
					"Lowest": "java.lang.Integer",
					"Highest": "java.lang.Integer",
					"StandardDeviation": "java.lang.Integer",
					"Variance": "java.lang.Integer",
					"System": "java.lang.Integer",
					"First": "java.lang.Integer",
					"DistinctCount": "java.lang.Integer"
				},
				"calculationsMapping": {
					"java.lang.Integer": {
						"Count" : "Count",
						"Sum" : "Sum",
						"Average" : "Average",
						"Lowest" : "Lowest",
						"Highest" : "Highest",
						"Standard Deviation" : "StandardDeviation",
						"Variance" : "Variance",
						"System" : "System",
						"First" : "First",
						"Distinct Count" : "DistinctCount"
					},
					"other": {
						"Count" : "Count", 
						"Distinct Count" : "DistinctCount"
					},
					"allDataTypes" :{
						"Count" : "Count", 
						"Sum" : "Sum",
						"Average" : "Average",
						"Lowest" : "Lowest",
						"Highest" : "Highest",
						"Standard Deviation" : "StandardDeviation",
						"Variance" : "Variance",
						"System" : "System",
						"First" : "First",
						"Distinct Count" : "DistinctCount"
					}
				},
				"builtInVariables": [
					{
						"name": "Page_Number",
						"value": "PAGE_NUMBER"
					},
					{
						"name": "Column_Number",
						"value": "COLUMN_NUMBER"
					},
					{
						"name": "Report_Count",
						"value": "REPORT_COUNT"
					},
					{
						"name": "Page_Count",
						"value": "PAGE_COUNT"
					},
					{
						"name": "Column_Count",
						"value": "COLUMN_COUNT"
					},
					{
						"name": "Current_Date",
						"value": "new java.util.Date()",
						"isExpression": false,
						"properties": {
							"pattern": "MMMMM dd, yyyy"
						}
					},
					{
						"name": "Current_Time",
						"value": "new java.util.Date()",
						"isExpression": false,
						"properties": {
							"pattern": "HH:mm"
						}
					},
					{
						"name": "Total_Pages",
						"value": "PAGE_NUMBER",
						"properties": {
							"evaluationTime": "Report"
						}
					}
				]
			},
			"pageHeader": [],
			"columnHeader": [],
			"columnFooter": [],
			"details": [],
			"pageFooter": [],
			"summary": {
				"someValue": ""
			},
			"pattern": [
				"#,##0.###",
				"#,##0.##%",
				"¤#,##0.##;¤-#,##0.##",
				"M/d/yy"
			],
			"pattern_": {
				"number": {
					"-10,023.123": "#,##0.###",
					"10,023.123-": "#,##0.###;#,##0.###-",
					"(10,023.123)": "#,##0.###;(#,##0.###)",
					"(-10,023.123)": "#,##0.###;(-#,##0.###)",
					"(10,023.123-)": "#,##0.###;(#,##0.###-)"
				},
				"percentage": {
					"-1,002,312.35%": "#,##0.##%",
					"-1,002,312.35‰": "#,##0.##‰",
					"(1,002,312.35": "#,##0.##%;(#,##0.##",
					"(-1,002,312.35": "#,##0.##%;(-#,##0.##",
					"(1,002,312.35": "#,##0.##%;(#,##0.##"
				},
				"currency": {
					"$-10,023.12": "¤#,##0.##;¤-#,##0.##",
					"10,023.12- $": "#,##0.##¤;#,##0.##- ¤",
					"(10,023.12) $": "#,##0.##¤;(#,##0.##) ¤",
					"$(-10,023.123)": "¤#,##0.###;¤(-#,##0.###)",
					"$(10,023.123-)": "¤#,##0.###;¤(#,##0.###-)"
				},
				"scientific": {
					"-1.002E4": "0.0##E0"
				},
				"date": {
					"12/11/19": "M/d/yy",
					"Dec 11, 2019": "MMM d, yyyy",
					"December 11, 2019": "MMMM d, yyyy",
					"12/11/19 1:58 PM": "M/d/yy h:mm a",
					"Dec 11, 2019 1:58:05 PM": "MMM d, yyyy h:mm:ss a",
					"Dec 11, 2019 1:58:05 PM IST": "MMM d, yyyy h:mm:ss a z"
				},
				"time": {
					"1:58 PM": "h:mm a",
					"1:58:05 PM": "h:mm:ss a",
					"1:58:05 PM IST": "h:mm:ss a z",
					"13:58 PM": "HH:mm a",
					"13:58:05 PM": "HH:mm:ss a",
					"13:58:05 India Standard Time": "HH:mm:ss zzzz"
				}
			},
			"parametersInPreview" : {
				"position" : ["Right", "Left", "Top", "Bottom"]
			}
		},
		"shortcuts":{
			"showPreview" : ["ctrl", "p"],
			"closePreview" : ["Escape"],
			"validateBands" : ["ctrl", "shift", "q"],
			"changePage" : ["ctrl", "shift", "l"],
			"export-pdf":["shift", "p"],
			"export-html":["shift", "l"],
			"export-xls":["shift", "m"],
			"showTutorialPage" : ["ctrl", "shift", "h"]
		},
		"errorText" : {
			"queryIsEmpty" : "Please provide query for ",
			"fieldsInCanvasChanged" : "Please check the canvas area as the fields added to canvas may or may not exist as the main data set query changed ",
			"quotesIssueInTextArea" : "Expression might be invalid. Please rectify it ",
			"parameterConfiguration":"There is a problem with parameters configuration ",
			"calulationIncrementGroupError" : "There might be a problem with increment Type for calculation ",
			"calulationResetGroupError" : "There might be a problem with reset type for calculation ",
			"widthInvalid":"Width specified for the component is exceeding the page size. Please check again ",
			"cantCreateCalculation" : "Cannot create calculation on the selected component. Please check again",
			"cantCreateCalculationStaticText" : "Cannot create calculation for the selected text component. Please check again",
			"variableNameNotValid" : "Variable name is not valid, Please rectify it ",
			"colorInvalid" : "Selected color is invalid, Please rectify it",
			"bandHierarchyLineError": "Please check component placement hierarchy for the highlighted Line component(s) ",
			"bandtHierarchyTextError": "Please check component placement hierarchy for the highlighted component(s) ",
			"fieldNameInValidDatasourcePage" : "Field name is invalid, will not be added to datasource pane in elements page. Please check again in ",
			"fieldNameInValidElementsPage" : "Field name is invalid. Please check again in "
		}
	}
}'''
return jsonObj