import { handleStaticPathValue } from '../../components/hi-canned-reports/hcrHelperMethods';

export const HCR_test = {
	HCR: {
		designerProperties: {
			pageProperties: {
				LETTER: {
					pixel: {
						width: 612,
						height: 792
					},
					inch: {
						height: 11,
						width: 8.5
					}
				},
				NOTE: {
					pixel: {
						width: 540,
						height: 720
					},
					inch: {
						height: 10,
						width: 7.5
					}
				},
				LEGAL: {
					pixel: {
						width: 612,
						height: 1008
					},
					inch: {
						height: 14,
						width: 8.5
					}
				},
				A0: {
					pixel: {
						width: 2384,
						height: 3370
					},
					inch: {
						height: 46.8056,
						width: 33.1111
					}
				},
				A1: {
					pixel: {
						width: 1684,
						height: 2384
					},
					inch: {
						height: 33.1111,
						width: 23.3889
					}
				},
				A2: {
					pixel: {
						width: 1191,
						height: 1684
					},
					inch: {
						height: 23.3889,
						width: 16.5417
					}
				},
				A3: {
					pixel: {
						width: 842,
						height: 1191
					},
					inch: {
						height: 16.5417,
						width: 11.6944
					}
				},
				A4: {
					pixel: {
						width: 595,
						height: 842
					},
					inch: {
						height: 11.6944,
						width: 8.2639
					}
				},
				A5: {
					pixel: {
						width: 420,
						height: 595
					},
					inch: {
						height: 8.2639,
						width: 5.8333
					}
				},
				A6: {
					pixel: {
						width: 298,
						height: 420
					},
					inch: {
						height: 5.8333,
						width: 4.1389
					}
				},
				A7: {
					pixel: {
						width: 210,
						height: 298
					},
					inch: {
						height: 4.1389,
						width: 2.9167
					}
				},
				A8: {
					pixel: {
						width: 147,
						height: 210
					},
					inch: {
						height: 2.9167,
						width: 2.0417
					}
				},
				A9: {
					pixel: {
						width: 105,
						height: 147
					},
					inch: {
						height: 2.0417,
						width: 1.4583
					}
				},
				A10: {
					pixel: {
						width: 74,
						height: 105
					},
					inch: {
						height: 1.4583,
						width: 1.0278
					}
				},
				B0: {
					pixel: {
						width: 2836,
						height: 4008
					},
					inch: {
						height: 55.6667,
						width: 39.3889
					}
				},
				B1: {
					pixel: {
						width: 2004,
						height: 2836
					},
					inch: {
						height: 39.3889,
						width: 27.8333
					}
				},
				B2: {
					pixel: {
						width: 1418,
						height: 2004
					},
					inch: {
						height: 27.8333,
						width: 19.6944
					}
				},
				B3: {
					pixel: {
						width: 1002,
						height: 1418
					},
					inch: {
						height: 19.6944,
						width: 13.9167
					}
				},
				B4: {
					pixel: {
						width: 709,
						height: 1002
					},
					inch: {
						height: 13.9167,
						width: 9.8472
					}
				},
				B5: {
					pixel: {
						width: 501,
						height: 709
					},
					inch: {
						height: 9.8472,
						width: 6.9583
					}
				}
			},
			whenNoDataType: {
				'No Pages': 'NoPages',
				'Blank Page': 'BlankPage',
				'All Sections No Detail': 'AllSectionsNoDetail',
				'No Data Section': 'NoDataSection'
			},
			printOrder: ['Vertical', 'Horizontal'],
			orientation: ['Landscape', 'Portrait'],
			splitType: ['STRETCH', 'IMMEDIATE', 'PREVENT'],
			footerPosition: ['Normal', 'StackAtBottom', 'ForceAtBottom', 'CollateAtBottom'],
			mode: ['Opaque', 'Transparent'],
			fontName: [
				'C059',
				'Century Schoolbook L',
				'D050000L',
				'DejaVu Math TeX Gyre',
				'DejaVu Sans',
				'DejaVu Sans Condensed',
				'DejaVu Sans Light',
				'DejaVu Sans Mono',
				'DejaVu Serif',
				'DejaVu Serif Condensed',
				'Dialog',
				'DialogInput',
				'Dingbats',
				'Liberation Mono',
				'Liberation Sans',
				'Liberation Sans Narrow',
				'Liberation Serif',
				'Monospaced',
				'Nimbus Mono L',
				'Nimbus Mono PS',
				'Nimbus Roman',
				'Nimbus Roman No9 L',
				'Nimbus Sans',
				'Nimbus Sans L',
				'Nimbus Sans Narrow',
				'P052',
				'SansSerif',
				'Serif',
				'Standard Symbols L',
				'Standard Symbols PS',
				'URW Bookman',
				'URW Bookman L',
				'URW Chancery L',
				'URW Gothic',
				'URW Gothic L',
				'URW Palladio L',
				'Z003'
			],
			positionType: ['FixRelativeToTop', 'FixRelativeToBottom', 'Float'],
			horizontalTextAlign: ['Left', 'Right', 'Center', 'Justified'],
			verticalTextAlign: ['Top', 'Middle', 'Bottom', 'Justified'],
			mode: ['Opaque', 'Transparent'],
			lineSpacing: ['Single', '1_1_2', 'Double', 'AtLeast', 'Fixed', 'Proportional'],
			lineStyle: ['Dotted', 'Double', 'Dashed', 'Solid'],
			linePositionType: ['FixRelativeToTop', 'FixRelativeToBottom', 'Float'],
			expressionType: ['simpleText', 'default'],
			stretchType: [
				'ContainerBottom',
				'ContainerHeight',
				'ElementGroupBottom',
				'ElementGroupHeight',
				'NoStretch'
			],
			rotationType: ['Left', 'Right', 'UpsideDown', 'None'],
			lineDirection: ['BottomUp', 'TopDown'],
			langauge: ['java', 'groovy', 'javascript'],
			incrementType: ['None', 'Column', 'Group', 'Report', 'Page'],
			markUp: ['none', 'styled', 'html', 'rtf'],
			tabStopAlignment: ['Center', 'Left', 'Right'],
			evaluationTime: ['Auto', 'Band', 'Column', 'Group', 'Master', 'Now', 'Report', 'Page'],
			calculations: [
				'Nothing',
				'Count',
				'Sum',
				'Average',
				'Lowest',
				'Highest',
				'Standard Deviation',
				'Variance',
				'System',
				'First',
				'Distinct Count'
			],
			resetType: ['Report', 'Page', 'Column', 'Group', 'None', 'Master'],
			fill: ['Solid'],
			scaleImage: ['Clip', 'FillFrame', 'RetainShape', 'RealHeight', 'RealSize'],
			onErrorType: ['Error', 'Blank', 'Icon'],
			horizontalImageAlign: ['Left', 'Center', 'Right'],
			verticalImageAlign: ['Top', 'Middle', 'Bottom'],
			compositeElements: [],
			title: {},
			designerStyles: [],
			parameters: {
				classNames: {
					String: 'java.lang.String',
					Collection: 'java.util.Collection',
					Numeric: 'java.lang.Integer'
				}
			},
			fields: [],
			variables: {
				classNames: {
					Double: 'java.lang.Double',
					Float: 'java.lang.Float',
					Integer: 'java.lang.Integer',
					Long: 'java.lang.Long',
					Short: 'java.lang.Short',
					'Big Decimal': 'java.math.BigDecimal',
					Time: 'java.sql.Time',
					Boolean: 'java.lang.Boolean',
					'Sql Date': 'java.sql.Date',
					'Util Date': 'java.util.Date',
					Timestamp: 'java.sql.Timestamp',
					String: 'java.lang.String'
				},
				resetTypes: {
					byPage: 'Page',
					byRecord: 'Report',
					byColumn: 'Column',
					byReport: 'Report'
				},
				calculationsFuntionClassMap: {
					Nothing: 'java.lang.Integer',
					Count: 'java.lang.Integer',
					Sum: 'java.lang.Integer',
					Average: 'java.lang.Integer',
					Lowest: 'java.lang.Integer',
					Highest: 'java.lang.Integer',
					StandardDeviation: 'java.lang.Integer',
					Variance: 'java.lang.Integer',
					System: 'java.lang.Integer',
					First: 'java.lang.Integer',
					DistinctCount: 'java.lang.Integer'
				},
				calculationsMapping: {
					'java.lang.Integer': {
						Count: 'Count',
						Sum: 'Sum',
						Average: 'Average',
						Lowest: 'Lowest',
						Highest: 'Highest',
						'Standard Deviation': 'StandardDeviation',
						Variance: 'Variance',
						System: 'System',
						First: 'First',
						'Distinct Count': 'DistinctCount'
					},
					other: {
						Count: 'Count',
						'Distinct Count': 'DistinctCount'
					},
					allDataTypes: {
						Count: 'Count',
						Sum: 'Sum',
						Average: 'Average',
						Lowest: 'Lowest',
						Highest: 'Highest',
						'Standard Deviation': 'StandardDeviation',
						Variance: 'Variance',
						System: 'System',
						First: 'First',
						'Distinct Count': 'DistinctCount'
					}
				},
				builtInVariables: [
					{
						name: 'Page_Number',
						value: 'PAGE_NUMBER'
					},
					{
						name: 'Column_Number',
						value: 'COLUMN_NUMBER'
					},
					{
						name: 'Report_Count',
						value: 'REPORT_COUNT'
					},
					{
						name: 'Page_Count',
						value: 'PAGE_COUNT'
					},
					{
						name: 'Column_Count',
						value: 'COLUMN_COUNT'
					},
					{
						name: 'Current_Date',
						value: 'new java.util.Date()',
						isExpression: false,
						properties: {
							pattern: 'MMMMM dd, yyyy'
						}
					},
					{
						name: 'Current_Time',
						value: 'new java.util.Date()',
						isExpression: false,
						properties: {
							pattern: 'HH:mm'
						}
					},
					{
						name: 'Total_Pages',
						value: 'PAGE_NUMBER',
						properties: {
							evaluationTime: 'Report'
						}
					}
				]
			},
			pageHeader: [],
			columnHeader: [],
			columnFooter: [],
			details: [],
			pageFooter: [],
			summary: {
				someValue: ''
			},
			pattern: ['#,##0.###', '#,##0.##%', '¤#,##0.##;¤-#,##0.##', 'M/d/yy'],
			pattern_: {
				number: {
					'-10,023.123': '#,##0.###',
					'10,023.123-': '#,##0.###;#,##0.###-',
					'(10,023.123)': '#,##0.###;(#,##0.###)',
					'(-10,023.123)': '#,##0.###;(-#,##0.###)',
					'(10,023.123-)': '#,##0.###;(#,##0.###-)'
				},
				percentage: {
					'-1,002,312.35%': '#,##0.##%',
					'-1,002,312.35‰': '#,##0.##‰',
					'(1,002,312.35': '#,##0.##%;(#,##0.##',
					'(-1,002,312.35': '#,##0.##%;(-#,##0.##',
					'(1,002,312.35': '#,##0.##%;(#,##0.##'
				},
				currency: {
					'$-10,023.12': '¤#,##0.##;¤-#,##0.##',
					'10,023.12- $': '#,##0.##¤;#,##0.##- ¤',
					'(10,023.12) $': '#,##0.##¤;(#,##0.##) ¤',
					'$(-10,023.123)': '¤#,##0.###;¤(-#,##0.###)',
					'$(10,023.123-)': '¤#,##0.###;¤(#,##0.###-)'
				},
				scientific: {
					'-1.002E4': '0.0##E0'
				},
				date: {
					'12/11/19': 'M/d/yy',
					'Dec 11, 2019': 'MMM d, yyyy',
					'December 11, 2019': 'MMMM d, yyyy',
					'12/11/19 1:58 PM': 'M/d/yy h:mm a',
					'Dec 11, 2019 1:58:05 PM': 'MMM d, yyyy h:mm:ss a',
					'Dec 11, 2019 1:58:05 PM IST': 'MMM d, yyyy h:mm:ss a z'
				},
				time: {
					'1:58 PM': 'h:mm a',
					'1:58:05 PM': 'h:mm:ss a',
					'1:58:05 PM IST': 'h:mm:ss a z',
					'13:58 PM': 'HH:mm a',
					'13:58:05 PM': 'HH:mm:ss a',
					'13:58:05 India Standard Time': 'HH:mm:ss zzzz'
				}
			},
			parametersInPreview: {
				position: ['Right', 'Left', 'Top', 'Bottom']
			}
		},
		shortcuts: {
			showPreview: ['ctrl', 'p'],
			closePreview: ['Escape'],
			validateBands: ['ctrl', 'shift', 'q'],
			changePage: ['ctrl', 'shift', 'l'],
			'export-pdf': ['shift', 'p'],
			'export-html': ['shift', 'l'],
			'export-xls': ['shift', 'm'],
			showTutorialPage: ['ctrl', 'shift', 'h']
		},
		errorText: {
			queryIsEmpty: 'Please provide query for ',
			fieldsInCanvasChanged:
				'Please check the canvas area as the fields added to canvas may or may not exist as the main data set query changed ',
			quotesIssueInTextArea: 'Expression might be invalid. Please rectify it ',
			parameterConfiguration: 'There is a problem with parameters configuration ',
			calulationIncrementGroupError: 'There might be a problem with increment Type for calculation ',
			calulationResetGroupError: 'There might be a problem with reset type for calculation ',
			widthInvalid: 'Width specified for the component is exceeding the page size. Please check again ',
			cantCreateCalculation: 'Cannot create calculation on the selected component. Please check again',
			cantCreateCalculationStaticText:
				'Cannot create calculation for the selected text component. Please check again',
			variableNameNotValid: 'Variable name is not valid, Please rectify it ',
			colorInvalid: 'Selected color is invalid, Please rectify it',
			bandHierarchyLineError: 'Please check component placement hierarchy for the highlighted Line component(s) ',
			bandtHierarchyTextError: 'Please check component placement hierarchy for the highlighted component(s) ',
			fieldNameInValidDatasourcePage:
				'Field name is invalid, will not be added to datasource pane in elements page. Please check again in ',
			fieldNameInValidElementsPage: 'Field name is invalid. Please check again in '
		}
	}
};
export const propertyPaneData_test = {
	activeEleId: 'textFieldDummy',
	$activeShape: 'textFieldDummy',
	hcrGroupId: '',
	hcrGroups: [],
	newConfiguration: '',
	columnCount: '',
	columnSpacing: '',
	columnWidth: '',
	printOrder: '',
	whenNoDataType: '',
	designerStylesNameForEdit: '',
	variablesEditName: '',
	showParametersInPreview: '',
	groupPropertiesEditName: '',
	groupPropertiesEdit: {
		minHeightToStartNewPage: '',
		minDetailsToStartFromTop: ''
	},
	parametersInPreview: {
		position: ''
	},
	variablesForEdit: {
		name: '',
		className: '',
		calculation: '',
		resetType: '',
		resetGroup: '',
		incrementType: '',
		incrementGroup: '',
		expression: '',
		initialValueExpression: '',
		incrementFactoryClassName: ''
	},
	//'PAGE STYLES'
	designerStylesEdit: {
		name: '',
		fontName: '',
		textFontSize: '',
		mode: '',
		textForecolor: '',
		textBackcolor: '',
		rotationType: '',
		markup: '',
		pattern: '',
		border: {
			padding: {
				topPadding: '',
				bottomPadding: '',
				leftPadding: '',
				rightPadding: ''
			},
			line: {
				topLine: {
					lineColor: '',
					lineStyle: ''
				}
			}
		}
	},
	pageLayoutInfo: {
		margin: {
			top: 20,
			left: 20,
			bottom: 20,
			right: 20
		},
		layoutName: 'A4',
		orientation: 'Portrait',
		size: {
			fullPage: {
				width: '595px',
				height: '842px'
			},
			canvasPage: {
				width: '555px',
				height: '802px'
			}
		}
	},
	shapeIds: {
		textFieldDummy: {
			properties: {
				x: 0,
				y: 0,
				fontName: 'Serif',
				configFontName: 'Aharoni',
				newChkbx: false,
				pattern: '',
				fontWeight: '',
				textForecolor: '#000000',
				textBackcolor: '#ffffff',
				bold: false,
				italic: false,
				underline: false,
				strikeThrough: false,
				textFieldExpression: '"Text Field"',
				textWidth: 50,
				textHeight: 50,
				rotationType: 'None',
				markUp: 'none',
				mode: 'Transparent',
				stretchType: 'NoStretch',
				positionType: 'FixRelativeTop',
				printRepeatedValues: true,
				repeat: false,
				removeLineWhenBlank: false,
				textFontSize: 14,
				horizontalTextAlign: 'Center',
				verticalTextAlign: 'Top',
				isCommonBorder: false,
				key: '',
				styleNameReference: '',
				printWhenExpression: '',
				propertyExpression: '',
				evaluationTime: 'Now',
				evaluationGroupName: '',
				stretchWithOverFlow: false,
				blankWhenNull: false,
				printWhenDetailOverflows: false,
				printInFirstWholeBand: false,
				patternExpression: '',
				paragraph: {
					lineSpacing: null,
					firstLineIndent: null,
					leftIndent: null,
					lineSpacingSize: null,
					rightIndent: null,
					spacingAfter: null,
					spacingBefore: null,
					tabStopWidth: null,
					tabStopAlignment: null,
					tabStopPosition: null
				},
				border: {
					padding: {
						bottomPadding: 1,
						topPadding: 1,
						leftPadding: 1,
						rightPadding: 1,
						padding: 1
					},
					line: {
						leftLine: {
							lineWidth: 0,
							lineColor: '#000000',
							lineStyle: 'SOLID'
						},
						rightLine: {
							lineWidth: 0,
							lineColor: '#000000',
							lineStyle: 'SOLID'
						},
						bottomLine: {
							lineWidth: 0,
							lineColor: '#000000',
							lineStyle: 'SOLID'
						},
						topLine: {
							lineWidth: 0,
							lineColor: '#000000',
							lineStyle: 'SOLID'
						}
					}
				}
			}
		}
	}
};
export const diagramAreaOldConfig = [
	{
		name: 'PAGE SETUP',
		type: 'settingsGroup',
		value: 'pageLayout',
		open: true,
		info: ['tooltip_pageSetup'],
		content: [
			{
				name: 'Page Size',
				type: 'DropDown',
				value: 'pageLayout',
				path: ['pageLayoutInfo', 'layoutName'],
				placeHolder: 'Layout',
				options: ['A3', 'A4', 'normal'],
				callBack: 'pageSizeChange',
				fromStatic: true,
				optionsStaticPath: ['designerProperties', 'pageProperties'],
				staticType: 'keys',
				info: ['tooltip_pageSize']
			},
			{
				name: 'Orientation',
				type: 'DropDown',
				value: 'Orientation',
				fromStatic: true,
				path: ['pageLayoutInfo', 'orientation'],
				placeHolder: 'Orientation',
				options: ['landScape', 'Portrait'],
				optionsStaticPath: ['designerProperties', 'orientation'],
				callBack: 'pageOrientationChange'
			}
		]
	},
	{
		name: 'MARGIN',
		type: 'settingsGroup',
		value: 'margin',
		// bootstrap: [ 12, 12, 12, 12 ],
		content: [
			{
				name: 'Top',
				type: 'numericDropdown',
				value: 'top',
				path: ['pageLayoutInfo', 'margin', 'top'],
				options: '',
				// bootstrap: [ 12, 12, 6, 6 ],
				callBack: 'pageMarginChange',
				info: {
					show: true,
					onIcon: false,
					content: [
						{
							type: 'infoDisplay',
							fromStatic: false,
							displayContent: 'Change top margin'
						}
					]
				}
			},
			{
				name: 'Left',
				type: 'numericDropdown',
				value: 'left',
				path: ['pageLayoutInfo', 'margin', 'left'],
				options: '',
				// bootstrap: [ 12, 12, 6, 6 ],
				info: {
					show: true,
					onIcon: false,
					content: [
						{
							type: 'infoDisplay',
							fromStatic: false,
							displayContent: 'Change left margin'
						}
					]
				},
				callBack: 'pageMarginChange'
			},
			{
				name: 'Bottom',
				type: 'numericDropdown',
				value: 'bottom',
				path: ['pageLayoutInfo', 'margin', 'bottom'],
				options: '',
				// bootstrap: [ 12, 12, 6, 6 ],
				info: {
					show: true,
					onIcon: false,
					content: [
						{
							type: 'infoDisplay',
							fromStatic: false,
							isHTML: true,
							displayContent: 'Change bottom margin'
						}
					]
				},
				callBack: 'pageMarginChange'
			},
			{
				name: 'Right',
				type: 'numericDropdown',
				value: 'right',
				path: ['pageLayoutInfo', 'margin', 'right'],
				options: '',
				// bootstrap: [ 12, 12, 6, 6 ],
				callBack: 'pageMarginChange'
			}
		]
	}
	// {
	// 	name: 'PAGE PROPERTIES',
	// 	type: 'settingsGroup',
	// 	value: 'report properties',
	// 	open: false,
	// 	content: [
	// 		// not there in UI
	// 		{
	// 			label: 'Report Name',
	// 			display: false,
	// 			type: 'textBox',
	// 			value: 'reportName',
	// 			placeHolder: 'Enter Name',
	// 			path: [ 'reportName' ],
	// 			callBack: 'handleStateChange'
	// 		},
	// 		{
	// 			name: 'Column Count',
	// 			path: [ 'columnCount' ],
	// 			fromStatic: false,
	// 			type: 'numericDropdown',
	// 			min: 0,
	// 			max: 10,
	// 			quotes: false,
	// 			info: [ 'tooltip_columnCount' ],
	// 			callBack: 'updateStateWithPath'
	// 		},
	// 		{
	// 			name: 'Column Spacing',
	// 			path: [ 'columnSpacing' ],
	// 			fromStatic: false,
	// 			type: 'numericDropdown',
	// 			bounds: 'free',
	// 			quotes: false,
	// 			info: [ 'tooltip_columnSpacing' ],
	// 			callBack: 'updateStateWithPath'
	// 		},
	// 		{
	// 			name: 'Column Width',
	// 			path: [ 'columnWidth' ],
	// 			fromStatic: false,
	// 			type: 'numericDropdown',
	// 			bounds: 'free',
	// 			quotes: false,
	// 			info: [ 'tooltip_columnWidth' ],
	// 			callBack: 'updateStateWithPath'
	// 		},
	// 		{
	// 			name: 'Print Order',
	// 			type: 'DropDown',
	// 			value: 'printOrder',
	// 			fromStatic: true,
	// 			options: [],
	// 			optionsStaticPath: [ 'designerProperties', 'printOrder' ],
	// 			path: [ 'printOrder' ],
	// 			info: [ 'tooltip_printOrder' ],
	// 			callBack: 'updateStateWithPath'
	// 		},
	// 		{
	// 			name: 'When No Data',
	// 			type: 'DropDown',
	// 			value: 'whenNoDataType',
	// 			placeHolder: 'Enter style name',
	// 			fromState: false,
	// 			fromStatic: true,
	// 			extractValues: true,
	// 			staticType: 'keys',
	// 			returnValue: 'value',
	// 			path: [ 'whenNoDataType' ],
	// 			optionsStaticPath: [ 'designerProperties', 'whenNoDataType' ],
	// 			info: [ 'tooltip_whenNoData' ],
	// 			callBack: 'updateStateWithPath'
	// 		},
	// 		{
	// 			label: 'summary with header and footer',
	// 			type: 'checkBox',
	// 			value: 'summaryWithPageHeaderAndFooter',
	// 			path: [ 'summaryWithPageHeaderAndFooter' ],
	// 			info: [ 'tooltip_summaryWithHeaderAndFooter' ],
	// 			title: true,
	// 			className: 'titleName',
	// 			callBack: 'updateStateWithPath'
	// 		},
	// 		{
	// 			label: 'Float column footer',
	// 			type: 'checkBox',
	// 			value: 'floatColumnFooter',
	// 			path: [ 'floatColumnFooter' ],
	// 			info: [ 'tooltip_floatingColumnFooter' ],
	// 			title: true,
	// 			className: 'titleName',
	// 			callBack: 'updateStateWithPath'
	// 		},
	// 		{
	// 			label: 'Title in new page',
	// 			type: 'checkBox',
	// 			value: 'titleNewPage',
	// 			path: [ 'titleNewPage' ],
	// 			info: [ 'tooltip_titleInNewPage' ],
	// 			title: true,
	// 			className: 'titleName',
	// 			callBack: 'updateStateWithPath'
	// 		},
	// 		{
	// 			label: 'Summary in new page',
	// 			type: 'checkBox',
	// 			value: 'summaryNewPage',
	// 			path: [ 'summaryNewPage' ],
	// 			title: true,
	// 			info: [ 'tooltip_summaryInNewPage' ],
	// 			className: 'titleName',
	// 			callBack: 'updateStateWithPath'
	// 		},
	// 		{
	// 			label: 'Ignore pagination',
	// 			type: 'checkBox',
	// 			value: 'ignorePagination',
	// 			path: [ 'ignorePagination' ],
	// 			title: true,
	// 			info: [ 'tooltip_ignorePagination' ],
	// 			className: 'titleName',
	// 			callBack: 'updateStateWithPath'
	// 		}
	// 	]
	// },
	// // designerStylesEdit
	// {
	// 	name: 'PAGE STYLES',
	// 	type: 'settingsGroup',
	// 	value: 'designerStyles',
	// 	open: false,
	// 	content: [
	// 		{
	// 			name: 'Select Styles',
	// 			type: 'DropDown',
	// 			value: 'selectDesignerStyles',
	// 			fromState: true,
	// 			fromStatic: false,
	// 			extractValues: true,
	// 			keyName: 'name',
	// 			path: [ 'designerStylesNameForEdit' ],
	// 			bootstrap: [ 12 ],
	// 			optionsStatePath: [ 'designerStyles' ],
	// 			callBack: 'handleDesignerStyleForEdit'
	// 		},
	// 		{
	// 			label: 'Style name',
	// 			type: 'textBox',
	// 			value: 'styleName',
	// 			placeHolder: 'Enter style',
	// 			path: [ 'designerStylesEdit', 'name' ],
	// 			info: [ 'tooltip_StyleName' ],
	// 			bootstrap: [ 12 ],
	// 			callBack: 'handleStateChange'
	// 		},
	// 		{
	// 			name: 'Family',
	// 			type: 'DropDown',
	// 			value: 'fontFamily',
	// 			fromStatic: true,
	// 			options: [],
	// 			optionsStaticPath: [ 'designerProperties', 'fontName' ],
	// 			path: [ 'designerStylesEdit', 'fontName' ],
	// 			bootstrap: [ 7 ],
	// 			callBack: 'handleStateChange'
	// 		},
	// 		{
	// 			name: 'Size',
	// 			path: [ 'designerStylesEdit', 'textFontSize' ],
	// 			fromStatic: false,
	// 			bootstrap: [ 5 ],
	// 			type: 'numericDropdown',
	// 			quotes: false,
	// 			callBack: 'handleStateChange'
	// 		},
	// 		{
	// 			name: 'Mode',
	// 			type: 'DropDown',
	// 			value: 'mode',
	// 			fromStatic: true,
	// 			options: [],
	// 			optionsStaticPath: [ 'designerProperties', 'mode' ],
	// 			path: [ 'designerStylesEdit', 'mode' ],
	// 			bootstrap: [ 4 ],
	// 			info: [ 'tooltip_mode' ],
	// 			callBack: 'handleStateChange'
	// 		},
	// 		{
	// 			name: 'Fore Color',
	// 			title: 'Fore Color',
	// 			fromStatic: false,
	// 			bootstrap: [ 4 ],
	// 			type: 'colorPicker',
	// 			quotes: true,
	// 			callBack: 'handleStateChange',
	// 			desc: 'pageStylesCommonColor',
	// 			additionalPaths: [
	// 				[ 'designerStylesEdit', 'imageForecolor' ],
	// 				[ 'designerStylesEdit', 'textForecolor' ]
	// 			],
	// 			path: [ 'designerStylesEdit', 'textForecolor' ]
	// 		},
	// 		{
	// 			name: 'Back Color',
	// 			title: 'Back Color',
	// 			fromStatic: false,
	// 			bootstrap: [ 4 ],
	// 			type: 'colorPicker',
	// 			quotes: true,
	// 			callBack: 'handleStateChange',
	// 			desc: 'pageStylesCommonColor',
	// 			additionalPaths: [
	// 				[ 'designerStylesEdit', 'imageBackcolor' ],
	// 				[ 'designerStylesEdit', 'textBackcolor' ]
	// 			],
	// 			path: [ 'designerStylesEdit', 'textBackcolor' ]
	// 		},
	// 		{
	// 			name: 'Styles',
	// 			type: 'iconsButtonGroup',
	// 			controlled: false,
	// 			content: [
	// 				{
	// 					name: 'Bold',
	// 					title: 'Bold',
	// 					iconClass: 'boldIcon',
	// 					path: [ 'designerStylesEdit', 'bold' ],
	// 					bootstrap: [ 6, 6, 6, 6 ],
	// 					callBack: 'handleStateChange',
	// 					fromStatic: false,
	// 					type: 'iconButton'
	// 				},
	// 				{
	// 					name: 'Italic',
	// 					title: 'Italic',
	// 					iconClass: 'italicIcon',
	// 					path: [ 'designerStylesEdit', 'italic' ],
	// 					bootstrap: [ 6, 6, 6, 6 ],
	// 					callBack: 'handleStateChange',
	// 					fromStatic: false,
	// 					type: 'iconButton'
	// 				},
	// 				{
	// 					name: 'Strike Through',
	// 					title: 'Strike Through',
	// 					iconClass: 'strikeThroughIcon',
	// 					path: [ 'designerStylesEdit', 'strikeThrough' ],
	// 					bootstrap: [ 6, 6, 6, 6 ],
	// 					callBack: 'handleStateChange',
	// 					fromStatic: false,
	// 					type: 'iconButton'
	// 				},
	// 				{
	// 					name: 'UnderLine',
	// 					title: 'UnderLine',
	// 					iconClass: 'underLineIcon',
	// 					path: [ 'designerStylesEdit', 'underline' ],
	// 					bootstrap: [ 6, 6, 6, 6 ],
	// 					callBack: 'handleStateChange',
	// 					fromStatic: false,
	// 					type: 'iconButton'
	// 				}
	// 			]
	// 		},
	// 		{
	// 			name: 'Alignment',
	// 			type: 'iconsButtonGroup',
	// 			bootStrap: [ 4, 4, 4, 4 ],
	// 			controlled: true,
	// 			content: [
	// 				{
	// 					name: 'left Align',
	// 					title: 'Left Align',
	// 					iconClass: 'leftAlignIcon',
	// 					value: 'Left',
	// 					additionalPaths: [
	// 						[ 'designerStylesEdit', 'horizontalImageAlign' ],
	// 						[ 'designerStylesEdit', 'horizontalTextAlign' ]
	// 					],
	// 					path: [ 'designerStylesEdit', 'horizontalTextAlign' ],
	// 					bootstrap: [ 6, 6, 6, 6 ],
	// 					callBack: 'handleStateChange',
	// 					fromStatic: false,
	// 					type: 'iconButton',
	// 					className: 'styleshcrIconButton'
	// 				},
	// 				{
	// 					name: 'Center Align',
	// 					title: 'Center Align',
	// 					iconClass: 'centerAlignIcon',
	// 					value: 'Center',
	// 					path: [ 'designerStylesEdit', 'horizontalTextAlign' ],
	// 					additionalPaths: [
	// 						[ 'designerStylesEdit', 'horizontalImageAlign' ],
	// 						[ 'designerStylesEdit', 'horizontalTextAlign' ]
	// 					],
	// 					bootstrap: [ 6, 6, 6, 6 ],
	// 					callBack: 'handleStateChange',
	// 					fromStatic: false,
	// 					type: 'iconButton',
	// 					className: 'styleshcrIconButton'
	// 				},
	// 				{
	// 					name: 'Right Align',
	// 					title: 'Right Align',
	// 					value: 'Right',
	// 					iconClass: 'rightAlignIcon',
	// 					path: [ 'designerStylesEdit', 'horizontalTextAlign' ],
	// 					additionalPaths: [
	// 						[ 'designerStylesEdit', 'horizontalImageAlign' ],
	// 						[ 'designerStylesEdit', 'horizontalTextAlign' ]
	// 					],
	// 					bootstrap: [ 6, 6, 6, 6 ],
	// 					callBack: 'handleStateChange',
	// 					fromStatic: false,
	// 					type: 'iconButton',
	// 					className: 'styleshcrIconButton'
	// 				},
	// 				{
	// 					name: 'Horizontal Justify',
	// 					title: 'Horizontal Justify',
	// 					value: 'Justified',
	// 					iconClass: 'justifyIcon',
	// 					path: [ 'designerStylesEdit', 'horizontalTextAlign' ],
	// 					bootstrap: [ 6, 6, 6, 6 ],
	// 					callBack: 'handleStateChange',
	// 					fromStatic: false,
	// 					type: 'iconButton',
	// 					className: 'styleshcrIconButton'
	// 				}
	// 			]
	// 		},
	// 		{
	// 			type: 'iconsButtonGroup',
	// 			controlled: true,
	// 			content: [
	// 				{
	// 					name: 'Top Align',
	// 					title: 'Top Align',
	// 					iconClass: 'topAlign',
	// 					value: 'Top',
	// 					path: [ 'designerStylesEdit', 'verticalTextAlign' ],
	// 					bootstrap: [ 6 ],
	// 					additionalPaths: [
	// 						[ 'designerStylesEdit', 'verticalImageAlign' ],
	// 						[ 'designerStylesEdit', 'verticalTextAlign' ]
	// 					],
	// 					callBack: 'handleStateChange',
	// 					fromStatic: false,
	// 					type: 'iconButton'
	// 				},
	// 				{
	// 					name: 'Center Align',
	// 					title: 'Center Align',
	// 					iconClass: 'centerAlign',
	// 					value: 'Middle',
	// 					path: [ 'designerStylesEdit', 'verticalTextAlign' ],
	// 					bootstrap: [ 6, 6, 6, 6 ],
	// 					additionalPaths: [
	// 						[ 'designerStylesEdit', 'verticalImageAlign' ],
	// 						[ 'designerStylesEdit', 'verticalTextAlign' ]
	// 					],
	// 					callBack: 'handleStateChange',
	// 					fromStatic: false,
	// 					type: 'iconButton'
	// 				},
	// 				{
	// 					name: 'Bottom Align',
	// 					title: 'Bottom Align',
	// 					value: 'Bottom',
	// 					iconClass: 'bottomAlign',
	// 					path: [ 'designerStylesEdit', 'verticalTextAlign' ],
	// 					bootstrap: [ 6, 6, 6, 6 ],
	// 					additionalPaths: [
	// 						[ 'designerStylesEdit', 'verticalImageAlign' ],
	// 						[ 'designerStylesEdit', 'verticalTextAlign' ]
	// 					],
	// 					callBack: 'handleStateChange',
	// 					fromStatic: false,
	// 					type: 'iconButton'
	// 				},
	// 				{
	// 					name: 'Vertical Justify',
	// 					title: 'Vertical Justify',
	// 					value: 'Justified',
	// 					iconClass: 'justifyIcon',
	// 					path: [ 'designerStylesEdit', 'verticalTextAlign' ],
	// 					bootstrap: [ 6, 6, 6, 6 ],
	// 					callBack: 'handleStateChange',
	// 					fromStatic: false,
	// 					type: 'iconButton'
	// 				}
	// 			]
	// 		},
	// 		{
	// 			name: 'Rotation',
	// 			type: 'DropDown',
	// 			value: 'rotationType',
	// 			fromStatic: true,
	// 			options: [],
	// 			optionsStaticPath: [ 'designerProperties', 'rotationType' ],
	// 			path: [ 'designerStylesEdit', 'rotationType' ],
	// 			bootstrap: [ 6 ],
	// 			info: [ 'tooltip_rotationType' ],
	// 			callBack: 'handleStateChange'
	// 		},
	// 		{
	// 			name: 'Mark up',
	// 			type: 'DropDown',
	// 			value: 'markup',
	// 			fromStatic: true,
	// 			options: [],
	// 			optionsStaticPath: [ 'designerProperties', 'markUp' ],
	// 			path: [ 'designerStylesEdit', 'markup' ],
	// 			bootstrap: [ 6 ],
	// 			info: [ 'tooltip_markUp' ],
	// 			callBack: 'handleStateChange'
	// 		},
	// 		{
	// 			label: 'Blank when null',
	// 			type: 'checkBox',
	// 			value: 'blankWhenNull',
	// 			path: [ 'designerStylesEdit', 'blankWhenNull' ],
	// 			bootstrap: [ 12 ],
	// 			title: true,
	// 			className: 'titleName',
	// 			info: [ 'tooltip_blankWhenNull' ],
	// 			callBack: 'updateStateWithPath'
	// 		},
	// 		{
	// 			label: 'Default style',
	// 			type: 'checkBox',
	// 			value: 'isDefault',
	// 			path: [ 'designerStylesEdit', 'isDefault' ],
	// 			bootstrap: [ 12 ],
	// 			title: true,
	// 			className: 'titleName',
	// 			info: [ 'tooltip_isDefaultStyle' ],
	// 			callBack: 'updateStateWithPath'
	// 		},
	// 		{
	// 			label: 'Pattern',
	// 			type: 'textBox',
	// 			value: 'pattern',
	// 			placeHolder: 'Enter pattern',
	// 			path: [ 'designerStylesEdit', 'pattern' ],
	// 			bootstrap: [ 12 ],
	// 			info: [ 'tooltip_pattern' ],
	// 			callBack: 'handleStateChange'
	// 		},
	// 		// discuss
	// 		{
	// 			name: 'Padding',
	// 			type: 'title',
	// 			bootstrap: [ 12, 12, 12, 12 ]
	// 		},
	// 		{
	// 			name: 'Top',
	// 			title: 'Top',
	// 			fromStatic: false,
	// 			bootstrap: [ 12, 12, 6, 6 ],
	// 			type: 'numericDropdown',
	// 			quotes: false,
	// 			callBack: 'handleStateChange',
	// 			path: [ 'designerStylesEdit', 'border', 'padding', 'topPadding' ]
	// 		},
	// 		{
	// 			name: 'Bottom',
	// 			title: 'Bottom',
	// 			fromStatic: false,
	// 			bootstrap: [ 12, 12, 6, 6 ],
	// 			type: 'numericDropdown',
	// 			quotes: false,
	// 			callBack: 'handleStateChange',
	// 			path: [ 'designerStylesEdit', 'border', 'padding', 'bottomPadding' ]
	// 		},
	// 		{
	// 			name: 'Left',
	// 			title: 'Left',
	// 			fromStatic: false,
	// 			bootstrap: [ 12, 12, 6, 6 ],
	// 			type: 'numericDropdown',
	// 			quotes: false,
	// 			callBack: 'handleStateChange',
	// 			path: [ 'designerStylesEdit', 'border', 'padding', 'leftPadding' ]
	// 		},
	// 		{
	// 			name: 'Right',
	// 			title: 'Right',
	// 			fromStatic: false,
	// 			bootstrap: [ 12, 12, 6, 6 ],
	// 			type: 'numericDropdown',
	// 			quotes: false,
	// 			callBack: 'handleStateChange',
	// 			path: [ 'designerStylesEdit', 'border', 'padding', 'rightPadding' ]
	// 		},
	// 		{
	// 			type: 'seperator'
	// 		},
	// 		{
	// 			name: 'Top',
	// 			type: 'title',
	// 			bootstrap: [ 12, 12, 12, 12 ]
	// 		},
	// 		{
	// 			name: 'Stroke',
	// 			title: 'Top',
	// 			fromStatic: false,
	// 			bootstrap: [ 12, 12, 4, 4 ],
	// 			type: 'numericDropdown',
	// 			quotes: false,
	// 			callBack: 'handleStateChange',
	// 			path: [ 'designerStylesEdit', 'border', 'line', 'topLine', 'lineWidth' ]
	// 		},
	// 		{
	// 			name: 'Color',
	// 			title: 'color',
	// 			fromStatic: false,
	// 			bootstrap: [ 12, 12, 4, 4 ],
	// 			type: 'colorPicker',
	// 			quotes: false,
	// 			callBack: 'handleStateChange',
	// 			path: [ 'designerStylesEdit', 'border', 'line', 'topLine', 'lineColor' ]
	// 		},
	// 		{
	// 			name: 'Style',
	// 			title: 'Style',
	// 			type: 'DropDown',
	// 			value: 'lineStyle',
	// 			bootstrap: [ 12, 12, 4, 4 ],
	// 			options: [],
	// 			fromStatic: true,
	// 			optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
	// 			path: [ 'designerStylesEdit', 'border', 'line', 'topLine', 'lineStyle' ],
	// 			callBack: 'handleStateChange'
	// 		},
	// 		{
	// 			type: 'seperator'
	// 		},
	// 		{
	// 			name: 'Bottom',
	// 			type: 'title',
	// 			bootstrap: [ 12, 12, 12, 12 ]
	// 		},
	// 		{
	// 			name: 'Stroke',
	// 			title: 'Stroke',
	// 			fromStatic: false,
	// 			bootstrap: [ 12, 12, 4, 4 ],
	// 			type: 'numericDropdown',
	// 			quotes: false,
	// 			callBack: 'handleStateChange',
	// 			path: [ 'designerStylesEdit', 'border', 'line', 'bottomLine', 'lineWidth' ]
	// 		},
	// 		{
	// 			name: 'Color',
	// 			title: 'Color',
	// 			fromStatic: false,
	// 			bootstrap: [ 12, 12, 4, 4 ],
	// 			type: 'colorPicker',
	// 			quotes: false,
	// 			callBack: 'handleStateChange',
	// 			path: [ 'designerStylesEdit', 'border', 'line', 'bottomLine', 'lineColor' ]
	// 		},
	// 		{
	// 			name: 'Style',
	// 			title: 'Style',
	// 			type: 'DropDown',
	// 			value: 'lineStyle',
	// 			bootstrap: [ 12, 12, 4, 4 ],
	// 			options: [],
	// 			fromStatic: true,
	// 			optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
	// 			path: [ 'designerStylesEdit', 'border', 'line', 'bottomLine', 'lineStyle' ],
	// 			callBack: 'handleStateChange'
	// 		},
	// 		{
	// 			type: 'seperator'
	// 		},
	// 		{
	// 			name: 'Left',
	// 			type: 'title',
	// 			bootstrap: [ 12, 12, 12, 12 ]
	// 		},
	// 		{
	// 			name: 'Stroke',
	// 			title: 'Stroke',
	// 			fromStatic: false,
	// 			bootstrap: [ 12, 12, 4, 4 ],
	// 			type: 'numericDropdown',
	// 			quotes: false,
	// 			callBack: 'handleStateChange',
	// 			path: [ 'designerStylesEdit', 'border', 'line', 'leftLine', 'lineWidth' ]
	// 		},
	// 		{
	// 			name: 'Color',
	// 			title: 'Color',
	// 			fromStatic: false,
	// 			bootstrap: [ 12, 12, 4, 4 ],
	// 			type: 'colorPicker',
	// 			quotes: false,
	// 			callBack: 'handleStateChange',
	// 			path: [ 'designerStylesEdit', 'border', 'line', 'leftLine', 'lineColor' ]
	// 		},
	// 		{
	// 			name: 'Style',
	// 			title: 'Style',
	// 			type: 'DropDown',
	// 			value: 'lineStyle',
	// 			bootstrap: [ 12, 12, 4, 4 ],
	// 			options: [],
	// 			fromStatic: true,
	// 			optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
	// 			path: [ 'designerStylesEdit', 'border', 'line', 'leftLine', 'lineStyle' ],
	// 			callBack: 'handleStateChange'
	// 		},
	// 		{
	// 			name: 'Right',
	// 			type: 'title',
	// 			bootstrap: [ 12, 12, 12, 12 ]
	// 		},
	// 		{
	// 			name: 'Stroke',
	// 			title: 'Stroke',
	// 			fromStatic: false,
	// 			bootstrap: [ 12, 12, 4, 4 ],
	// 			type: 'numericDropdown',
	// 			quotes: false,
	// 			callBack: 'handleStateChange',
	// 			path: [ 'designerStylesEdit', 'border', 'line', 'rightLine', 'lineWidth' ]
	// 		},
	// 		{
	// 			name: 'Color',
	// 			title: 'Color',
	// 			fromStatic: false,
	// 			bootstrap: [ 12, 12, 4, 4 ],
	// 			type: 'colorPicker',
	// 			quotes: true,
	// 			callBack: 'handleStateChange',
	// 			path: [ 'designerStylesEdit', 'border', 'line', 'rightLine', 'lineColor' ]
	// 		},
	// 		{
	// 			name: 'Style',
	// 			title: 'Style',
	// 			type: 'DropDown',
	// 			value: 'lineStyle',
	// 			bootstrap: [ 12, 12, 4, 4 ],
	// 			options: [],
	// 			fromStatic: true,
	// 			optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
	// 			path: [ 'designerStylesEdit', 'border', 'line', 'rightLine', 'lineStyle' ],
	// 			callBack: 'handleStateChange'
	// 		},
	// 		{
	// 			name: 'Line Styles',
	// 			type: 'title',
	// 			bootstrap: [ 12 ]
	// 		},
	// 		{
	// 			name: 'Stroke',
	// 			type: 'numericDropdown',
	// 			value: 2,
	// 			fromStatic: false,
	// 			path: [ 'designerStylesEdit', 'lineStyle', 'penLineWidth' ],
	// 			bootstrap: [ 12, 12, 4, 4 ],
	// 			callBack: 'handleStateChange'
	// 		},
	// 		{
	// 			name: 'Color',
	// 			type: 'colorPicker',
	// 			value: 'foreColor',
	// 			fromStatic: true,
	// 			path: [ 'designerStylesEdit', 'lineStyle', 'lineForecolor' ],
	// 			bootstrap: [ 12, 12, 4, 4 ],
	// 			callBack: 'handleStateChange'
	// 		},
	// 		{
	// 			name: 'Style',
	// 			type: 'DropDown',
	// 			value: 'lineStyle',
	// 			options: [],
	// 			fromStatic: true,
	// 			optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
	// 			path: [ 'designerStylesEdit', 'lineStyle', 'lineStyle' ],
	// 			bootstrap: [ 12, 12, 4, 4 ],
	// 			callBack: 'handleStateChange'
	// 		},
	// 		{
	// 			name: 'Delete',
	// 			type: 'button',
	// 			className: '',
	// 			showName: false,
	// 			bootstrap: [ 4 ],
	// 			callBack: 'handleDesignerStyleDelete'
	// 		},
	// 		{
	// 			name: 'Update',
	// 			type: 'button',
	// 			className: '',
	// 			showName: false,
	// 			bootstrap: [ 4 ],
	// 			callBack: 'handleStylesPropertiesUpdate'
	// 		},
	// 		{
	// 			name: 'Add',
	// 			type: 'button',
	// 			className: '',
	// 			showName: false,
	// 			bootstrap: [ 4 ],
	// 			highlight: true,
	// 			when: 'designerStyleEdit',
	// 			callBack: 'handleStylesPropertiesAdd'
	// 		}
	// 	]
	// },
	// // variablesForEdit
	// {
	// 	name: 'CALCULATIONS',
	// 	type: 'settingsGroup',
	// 	value: 'variables',
	// 	open: false,
	// 	bootstrap: [ 12 ],
	// 	content: [
	// 		{
	// 			name: 'Select Calculation',
	// 			type: 'DropDown',
	// 			value: 'selectedVariableEdit',
	// 			fromState: true,
	// 			fromStatic: false,
	// 			extractValues: true,
	// 			keyName: 'name',
	// 			path: [ 'variablesEditName' ],
	// 			bootstrap: [ 12 ],
	// 			optionsStatePath: [ 'variables' ],
	// 			callBack: 'handleVariableChangeForEdit'
	// 		},
	// 		{
	// 			label: 'Name',
	// 			type: 'textBox',
	// 			value: 'name',
	// 			placeHolder: 'Enter name',
	// 			path: [ 'variablesForEdit', 'name' ],
	// 			bootstrap: [ 12 ],
	// 			info: [ 'tooltip_Name' ],
	// 			callBack: 'handleStateChange'
	// 		},
	// 		{
	// 			name: 'Class Name',
	// 			type: 'DropDown',
	// 			value: 'className',
	// 			options: [],
	// 			fromStatic: true,
	// 			staticType: 'keys',
	// 			optionsStaticPath: [ 'designerProperties', 'variables', 'classNames' ],
	// 			formatOutput: true,
	// 			formatOptions: {
	// 				display: 'keys',
	// 				value: 'values'
	// 			},
	// 			path: [ 'variablesForEdit', 'className' ],
	// 			bootstrap: [ 6 ],
	// 			info: [ 'tooltip_varClassName' ],
	// 			callBack: 'handleStateChange'
	// 		},
	// 		{
	// 			name: 'Calculation',
	// 			type: 'DropDown',
	// 			value: 'calculation',
	// 			options: [],
	// 			fromStatic: true,
	// 			optionsStaticPath: [ 'designerProperties', 'calculations' ],
	// 			path: [ 'variablesForEdit', 'calculation' ],
	// 			bootstrap: [ 6 ],
	// 			info: [ 'tooltip_varCalculation' ],
	// 			callBack: 'handleStateChange'
	// 		},
	// 		{
	// 			name: 'Reset Type',
	// 			type: 'DropDown',
	// 			value: 'resetType',
	// 			options: [],
	// 			fromStatic: true,
	// 			optionsStaticPath: [ 'designerProperties', 'resetType' ],
	// 			path: [ 'variablesForEdit', 'resetType' ],
	// 			bootstrap: [ 6 ],
	// 			info: [ 'tooltip_varResetType' ],
	// 			callBack: 'handleStateChange'
	// 		},
	// 		{
	// 			name: 'Reset Group',
	// 			type: 'DropDown',
	// 			value: 'ResetGroup',
	// 			fromState: true,
	// 			fromStatic: false,
	// 			extractValues: true,
	// 			keyName: 'name',
	// 			path: [ 'variablesForEdit', 'resetGroup' ],
	// 			bootstrap: [ 6 ],
	// 			info: [ 'tooltip_varResetGroup' ],
	// 			optionsStatePath: [ 'groupBandInfo' ],
	// 			callBack: 'handleStateChange'
	// 		},
	// 		{
	// 			name: 'Increment',
	// 			type: 'DropDown',
	// 			value: 'incrementType',
	// 			options: [],
	// 			fromStatic: true,
	// 			optionsStaticPath: [ 'designerProperties', 'incrementType' ],
	// 			path: [ 'variablesForEdit', 'incrementType' ],
	// 			bootstrap: [ 6 ],
	// 			info: [ 'tooltip_varIncrement' ],
	// 			callBack: 'handleStateChange'
	// 		},
	// 		{
	// 			name: 'Increment Group',
	// 			type: 'DropDown',
	// 			value: 'IncrementGroup',
	// 			fromState: true,
	// 			fromStatic: false,
	// 			extractValues: true,
	// 			keyName: 'name',
	// 			path: [ 'variablesForEdit', 'incrementGroup' ],
	// 			bootstrap: [ 6 ],
	// 			info: [ 'tooltip_varIncrementGroup' ],
	// 			optionsStatePath: [ 'groupBandInfo' ],
	// 			callBack: 'handleStateChange'
	// 		},
	// 		{
	// 			label: 'Expression',
	// 			type: 'textBox',
	// 			value: 'expression',
	// 			placeHolder: 'Enter expression',
	// 			path: [ 'variablesForEdit', 'expression' ],
	// 			bootstrap: [ 12 ],
	// 			info: [ 'tooltip_varExpression' ],
	// 			callBack: 'handleStateChange'
	// 		},
	// 		{
	// 			label: 'Initial Value Expression',
	// 			type: 'textBox',
	// 			value: 'initialValueExpression',
	// 			placeHolder: 'Enter Initial Value',
	// 			path: [ 'variablesForEdit', 'initialValueExpression' ],
	// 			bootstrap: [ 12 ],
	// 			info: [ 'tooltip_varInitialValueExpression' ],
	// 			callBack: 'handleStateChange'
	// 		},
	// 		{
	// 			label: 'Increment Factory ClassName',
	// 			type: 'textBox',
	// 			value: 'incrementFactoryClassName',
	// 			placeHolder: 'Enter Initial Value',
	// 			path: [ 'variablesForEdit', 'incrementFactoryClassName' ],
	// 			bootstrap: [ 12 ],
	// 			info: [ 'tooltip_varIncrementFactoryClassName' ],
	// 			callBack: 'handleStateChange'
	// 		},
	// 		{
	// 			name: 'Clear',
	// 			type: 'button',
	// 			showName: false,
	// 			className: '',
	// 			bootstrap: [ 6 ],
	// 			callBack: 'handleVariableClear'
	// 		},
	// 		{
	// 			name: 'Save',
	// 			type: 'button',
	// 			className: '',
	// 			showName: false,
	// 			bootstrap: [ 6 ],
	// 			highlight: true,
	// 			when: 'calulationEdit',
	// 			callBack: 'handleVariableUpdate'
	// 		}
	// 	]
	// },
	// {
	// 	name: 'PREVIEW PARAMETERS',
	// 	type: 'settingsGroup',
	// 	value: 'previewParameters',
	// 	open: false,
	// 	bootstrap: [ 12 ],
	// 	content: [
	// 		{
	// 			label: 'Show Parameters',
	// 			type: 'checkBox',
	// 			value: 'showParametersInPreview',
	// 			path: [ 'showParametersInPreview' ],
	// 			bootstrap: [ 12 ],
	// 			title: true,
	// 			className: 'titleName',
	// 			callBack: 'updateStateWithPath'
	// 		},
	// 		{
	// 			name: 'Position',
	// 			type: 'DropDown',
	// 			value: 'Position',
	// 			fromStatic: true,
	// 			path: [ 'parametersInPreview', 'position' ],
	// 			placeHolder: 'Orientation',
	// 			options: [ 'landScape', 'Portrait' ],
	// 			optionsStaticPath: [ 'designerProperties', 'parametersInPreview', 'position' ],
	// 			bootstrap: [ 12, 12, 12, 12 ],
	// 			callBack: 'handleStateChange'
	// 		}
	// 	]
	// },
	// {
	// 	name: 'GROUP PROPERTIES',
	// 	type: 'settingsGroup',
	// 	value: 'groupParameters',
	// 	open: false,
	// 	bootstrap: [ 12 ],
	// 	content: [
	// 		{
	// 			name: 'Select Group',
	// 			type: 'DropDown',
	// 			value: 'selectGroup',
	// 			fromState: true,
	// 			fromStatic: false,
	// 			extractValues: true,
	// 			keyName: 'name',
	// 			path: [ 'groupPropertiesEditName' ],
	// 			bootstrap: [ 12 ],
	// 			optionsStatePath: [ 'groupBandInfo' ],
	// 			callBack: 'handleGroupPropertiesChange',
	// 			desc: 'selectGroupNameForPropertiesEdit'
	// 		},
	// 		{
	// 			name: 'Minimum height to start new page',
	// 			type: 'numericDropdown',
	// 			value: 'minHeightToStartNewPage',
	// 			path: [ 'groupPropertiesEdit', 'minHeightToStartNewPage' ],
	// 			bootstrap: [ 6 ],
	// 			bounds: 'free',
	// 			callBack: 'handleGroupPropertiesChange',
	// 			info: [ 'tooltip_minHeightToStartNewPage' ],
	// 			desc: 'propertyChanged'
	// 		},
	// 		{
	// 			name: 'Minimum records to start from top',
	// 			type: 'numericDropdown',
	// 			value: 'minDetailsToStartFromTop',
	// 			path: [ 'groupPropertiesEdit', 'minDetailsToStartFromTop' ],
	// 			bootstrap: [ 6 ],
	// 			bounds: 'free',
	// 			callBack: 'handleGroupPropertiesChange',
	// 			info: [ 'tooltip_minDetailsToStartFromTop' ],
	// 			desc: 'propertyChanged'
	// 		},
	// 		{
	// 			label: 'Reprint header on each page',
	// 			type: 'checkBox',
	// 			value: 'reprintHeaderOnEachPage',
	// 			path: [ 'groupPropertiesEdit', 'reprintHeaderOnEachPage' ],
	// 			bootstrap: [ 12 ],
	// 			title: true,
	// 			className: 'titleName',
	// 			callBack: 'handleGroupPropertiesChange',
	// 			info: [ 'tooltip_reprintHeaderOnEachPage' ],
	// 			desc: 'propertyChanged'
	// 		},
	// 		{
	// 			label: 'Keep Together',
	// 			type: 'checkBox',
	// 			value: 'keepTogether',
	// 			path: [ 'groupPropertiesEdit', 'keepTogether' ],
	// 			bootstrap: [ 12 ],
	// 			title: true,
	// 			className: 'titleName',
	// 			info: [ 'tooltip_keepTogether' ],
	// 			callBack: 'handleGroupPropertiesChange',
	// 			desc: 'propertyChanged'
	// 		},
	// 		{
	// 			label: 'Start new column',
	// 			type: 'checkBox',
	// 			value: 'startNewColumn',
	// 			path: [ 'groupPropertiesEdit', 'startNewColumn' ],
	// 			bootstrap: [ 12 ],
	// 			title: true,
	// 			className: 'titleName',
	// 			info: [ 'tooltip_startNewColumn' ],
	// 			callBack: 'handleGroupPropertiesChange',
	// 			desc: 'propertyChanged'
	// 		},
	// 		{
	// 			label: 'Start new page',
	// 			type: 'checkBox',
	// 			value: 'startNewPage',
	// 			path: [ 'groupPropertiesEdit', 'startNewPage' ],
	// 			bootstrap: [ 12 ],
	// 			title: true,
	// 			className: 'titleName',
	// 			info: [ 'tooltip_startNewPage' ],
	// 			callBack: 'handleGroupPropertiesChange',
	// 			desc: 'propertyChanged'
	// 		},
	// 		{
	// 			label: 'Reset page number',
	// 			type: 'checkBox',
	// 			value: 'resetPageNumber',
	// 			path: [ 'groupPropertiesEdit', 'resetPageNumber' ],
	// 			bootstrap: [ 12 ],
	// 			title: true,
	// 			className: 'titleName',
	// 			info: [ 'tooltip_resetPageNumber' ],
	// 			callBack: 'handleGroupPropertiesChange',
	// 			desc: 'propertyChanged'
	// 		},
	// 		{
	// 			label: 'Prevent Orphan Footer',
	// 			type: 'checkBox',
	// 			value: 'preventOrphanFooter',
	// 			path: [ 'groupPropertiesEdit', 'preventOrphanFooter' ],
	// 			info: [ 'tooltip_preventOrphanFooter' ],
	// 			bootstrap: [ 12 ],
	// 			title: true,
	// 			className: 'titleName',
	// 			callBack: 'handleGroupPropertiesChange',
	// 			desc: 'propertyChanged'
	// 		}
	// 	]
	// }
];
export const expectedDiagramAreaNewConfig = [
	{
		key: 'Page Size',
		label: 'Page Size',
		selectedKey: 'A4',
		value: [
			{
				key: 'LETTER',
				label: 'LETTER'
			},
			{
				key: 'NOTE',
				label: 'NOTE'
			},
			{
				key: 'LEGAL',
				label: 'LEGAL'
			},
			{
				key: 'A0',
				label: 'A0'
			},
			{
				key: 'A1',
				label: 'A1'
			},
			{
				key: 'A2',
				label: 'A2'
			},
			{
				key: 'A3',
				label: 'A3'
			},
			{
				key: 'A4',
				label: 'A4'
			},
			{
				key: 'A5',
				label: 'A5'
			},
			{
				key: 'A6',
				label: 'A6'
			},
			{
				key: 'A7',
				label: 'A7'
			},
			{
				key: 'A8',
				label: 'A8'
			},
			{
				key: 'A9',
				label: 'A9'
			},
			{
				key: 'A10',
				label: 'A10'
			},
			{
				key: 'B0',
				label: 'B0'
			},
			{
				key: 'B1',
				label: 'B1'
			},
			{
				key: 'B2',
				label: 'B2'
			},
			{
				key: 'B3',
				label: 'B3'
			},
			{
				key: 'B4',
				label: 'B4'
			},
			{
				key: 'B5',
				label: 'B5'
			}
		],
		groupId: 'PAGE SETUP',
		elementType: 'Select',
		callBack: 'pageSizeChange',
		path: ['pageLayoutInfo', 'layoutName'],
		staticPathValue: handleStaticPathValue({
			hcrStaticData: HCR_test,
			optionsStaticPath: ['designerProperties', 'pageProperties']
		})
	},
	{
		key: 'Orientation',
		label: 'Orientation',
		selectedKey: 'Portrait',
		value: [
			{
				key: 'Landscape',
				label: 'Landscape'
			},
			{
				key: 'Portrait',
				label: 'Portrait'
			}
		],
		groupId: 'PAGE SETUP',
		elementType: 'Select',
		callBack: 'pageOrientationChange',
		path: ['pageLayoutInfo', 'orientation'],
		staticPathValue: handleStaticPathValue({
			hcrStaticData: HCR_test,
			optionsStaticPath: ['designerProperties', 'orientation']
		})
	},
	{
		key: 'Top',
		label: 'Top',
		tooltip: 'Top',
		value: 20,
		elementType: 'InputNumber',
		groupId: 'MARGIN',
		callBack: 'pageMarginChange',
		path: ['pageLayoutInfo', 'margin', 'top']
	},
	{
		key: 'Left',
		label: 'Left',
		tooltip: 'Left',
		value: 20,
		elementType: 'InputNumber',
		groupId: 'MARGIN',
		callBack: 'pageMarginChange',
		path: ['pageLayoutInfo', 'margin', 'left']
	},
	{
		key: 'Bottom',
		label: 'Bottom',
		tooltip: 'Bottom',
		value: 20,
		elementType: 'InputNumber',
		groupId: 'MARGIN',
		callBack: 'pageMarginChange',
		path: ['pageLayoutInfo', 'margin', 'bottom']
	},
	{
		key: 'Right',
		label: 'Right',
		tooltip: 'Right',
		value: 20,
		elementType: 'InputNumber',
		groupId: 'MARGIN',
		callBack: 'pageMarginChange',
		path: ['pageLayoutInfo', 'margin', 'right']
	}
];
export const textFieldOldConfig = [
	{
		name: 'Typography',
		type: 'settingsGroup',
		value: 'typography',
		open: true,
		content: [
			{
				name: 'Content',
				title: 'Content',
				path: ['shapeIds', '$activeShape', 'properties', 'textFieldExpression'],
				info: ['tooltip_content'],
				callBack: 'handleShapePropertyChanges',
				fromStatic: false,
				type: 'textArea'
			},
			// {
			// 	name: 'Fonts',
			// 	bootstrap: [ 12, 12, 12, 12 ],
			// 	type: 'title'
			// },
			{
				name: 'Family',
				type: 'DropDown',
				value: 'fontFamily',
				fromStatic: true,
				options: [],
				optionsStaticPath: ['designerProperties', 'fontName'],
				path: ['shapeIds', '$activeShape', 'properties', 'fontName'],
				bootstrap: [7],
				callBack: 'handleShapePropertyChanges'
			},
			{
				name: 'Size',
				path: ['shapeIds', '$activeShape', 'properties', 'textFontSize'],
				fromStatic: false,
				bootstrap: [5],
				type: 'numericDropdown',
				quotes: false,
				callBack: 'handleShapePropertyChanges'
			}
		]
	}
];
export const expectedTextFieldNewConfig = [
	{
		label: 'Content',
		key: 'Content',
		groupId: 'Typography',
		path: ['shapeIds', '$activeShape', 'properties', 'textFieldExpression'],
		callBack: 'handleShapePropertyChanges',
		value: { text: '"Text Field"' },
		elementType: 'NormalTextArea'
	},
	{
		key: 'Family',
		label: 'Family',
		selectedKey: 'Serif',
		value: [
			{ key: 'C059', label: 'C059' },
			{ key: 'Century Schoolbook L', label: 'Century Schoolbook L' },
			{ key: 'D050000L', label: 'D050000L' },
			{ key: 'DejaVu Math TeX Gyre', label: 'DejaVu Math TeX Gyre' },
			{ key: 'DejaVu Sans', label: 'DejaVu Sans' },
			{ key: 'DejaVu Sans Condensed', label: 'DejaVu Sans Condensed' },
			{ key: 'DejaVu Sans Light', label: 'DejaVu Sans Light' },
			{ key: 'DejaVu Sans Mono', label: 'DejaVu Sans Mono' },
			{ key: 'DejaVu Serif', label: 'DejaVu Serif' },
			{ key: 'DejaVu Serif Condensed', label: 'DejaVu Serif Condensed' },
			{ key: 'Dialog', label: 'Dialog' },
			{ key: 'DialogInput', label: 'DialogInput' },
			{ key: 'Dingbats', label: 'Dingbats' },
			{ key: 'Liberation Mono', label: 'Liberation Mono' },
			{ key: 'Liberation Sans', label: 'Liberation Sans' },
			{ key: 'Liberation Sans Narrow', label: 'Liberation Sans Narrow' },
			{ key: 'Liberation Serif', label: 'Liberation Serif' },
			{ key: 'Monospaced', label: 'Monospaced' },
			{ key: 'Nimbus Mono L', label: 'Nimbus Mono L' },
			{ key: 'Nimbus Mono PS', label: 'Nimbus Mono PS' },
			{ key: 'Nimbus Roman', label: 'Nimbus Roman' },
			{ key: 'Nimbus Roman No9 L', label: 'Nimbus Roman No9 L' },
			{ key: 'Nimbus Sans', label: 'Nimbus Sans' },
			{ key: 'Nimbus Sans L', label: 'Nimbus Sans L' },
			{ key: 'Nimbus Sans Narrow', label: 'Nimbus Sans Narrow' },
			{ key: 'P052', label: 'P052' },
			{ key: 'SansSerif', label: 'SansSerif' },
			{ key: 'Serif', label: 'Serif' },
			{ key: 'Standard Symbols L', label: 'Standard Symbols L' },
			{ key: 'Standard Symbols PS', label: 'Standard Symbols PS' },
			{ key: 'URW Bookman', label: 'URW Bookman' },
			{ key: 'URW Bookman L', label: 'URW Bookman L' },
			{ key: 'URW Chancery L', label: 'URW Chancery L' },
			{ key: 'URW Gothic', label: 'URW Gothic' },
			{ key: 'URW Gothic L', label: 'URW Gothic L' },
			{ key: 'URW Palladio L', label: 'URW Palladio L' },
			{ key: 'Z003', label: 'Z003' }
		],
		groupId: 'Typography',
		elementType: 'Select',
		callBack: 'handleShapePropertyChanges',
		path: ['shapeIds', '$activeShape', 'properties', 'fontName'],
		staticPathValue: handleStaticPathValue({
			hcrStaticData: HCR_test,
			optionsStaticPath: ['designerProperties', 'fontName']
		})
	},
	{
		key: 'Size',
		label: 'Size',
		tooltip: 'Size',
		value: 14,
		elementType: 'InputNumber',
		groupId: 'Typography',
		callBack: 'handleShapePropertyChanges',
		path: ['shapeIds', '$activeShape', 'properties', 'textFontSize']
	}
];

export const tableOutlinedData = {
	"tableNode": {
		"width": 200,
		"height": 125,
		"nodeWidth": 100,
		"nodeHeight": 125,
		"name": "advancedTable",
		"label": "Table",
		"renderKey": "advancedTable",
		"parentKey": "elements",
		"isLeaf": true,
		"repeat": "rt",
		"category": "advancedTable",
		"zIndex": 10,
		"type": "defaultNodes",
		"fontSize": 10,
		"fontFamily": "Serif",
		"borders": {
			"Top": {
				"stroke": 0,
				"style": "SOLID",
				"color": "#000000"
			},
			"Bottom": {
				"stroke": 0,
				"style": "SOLID",
				"color": "#000000"
			},
			"Right": {
				"stroke": 0,
				"style": "SOLID",
				"color": "#000000"
			},
			"Left": {
				"stroke": 0,
				"style": "SOLID",
				"color": "#000000"
			}
		},
		"padding": {
			"Top": 1,
			"Bottom": 1,
			"Right": 1,
			"Left": 1
		},
		"isAppliedClicked": true,
		"id": "node-0a15f4e8-62b1-4fec-bcea-65291c991d3e",
		"x": 80,
		"y": 50,
		"printRepeatedValues": true,
		"copiedConfig": {},
		"fontFill": "#000000",
		"fill": "#fefefe",
		"selectedQueryID": 1,
		"selectedFields": [
			"travel_id",
			"travel_date"
		],
		"selectedColumnFields": [
			"travel_id",
			"travel_date"
		],
		"selectedRowFields": [],
		"selectedMeasures": [],
		"rt": "header",
		"pg": null,
		"cl": null,
		"rd": null,
		"lpf": null,
		"nd": null,
		"columns": {
			"b8778cd3-636a-4d88-a48a-19c45c68135d": {
				"id": "b8778cd3-636a-4d88-a48a-19c45c68135d",
				"width": 100,
				"name": "Column 1"
			},
			"28fde315-d261-4348-809a-83065bdf8682": {
				"id": "28fde315-d261-4348-809a-83065bdf8682",
				"width": 100,
				"name": "Column 2"
			}
		},
		"cells": {
			"b8778cd3-636a-4d88-a48a-19c45c68135d-tableHeader": {
				"id": "b8778cd3-636a-4d88-a48a-19c45c68135d-tableHeader",
				"columnId": "b8778cd3-636a-4d88-a48a-19c45c68135d",
				"bandType": "tableHeader",
				"width": 100,
				"height": 25,
				"nodeIds": []
			},
			"b8778cd3-636a-4d88-a48a-19c45c68135d-columnHeaderOfTable": {
				"id": "b8778cd3-636a-4d88-a48a-19c45c68135d-columnHeaderOfTable",
				"columnId": "b8778cd3-636a-4d88-a48a-19c45c68135d",
				"bandType": "columnHeaderOfTable",
				"width": 100,
				"height": 25,
				"nodeIds": [
					"node-4e83a1b0-6f91-460a-8203-be98c04e7af8"
				]
			},
			"b8778cd3-636a-4d88-a48a-19c45c68135d-columnData": {
				"id": "b8778cd3-636a-4d88-a48a-19c45c68135d-columnData",
				"columnId": "b8778cd3-636a-4d88-a48a-19c45c68135d",
				"bandType": "columnData",
				"width": 100,
				"height": 25,
				"nodeIds": [
					"node-c596e394-8ebe-4603-a8a4-b971c289fd8a"
				]
			},
			"b8778cd3-636a-4d88-a48a-19c45c68135d-columnFooterOfTable": {
				"id": "b8778cd3-636a-4d88-a48a-19c45c68135d-columnFooterOfTable",
				"columnId": "b8778cd3-636a-4d88-a48a-19c45c68135d",
				"bandType": "columnFooterOfTable",
				"width": 100,
				"height": 25,
				"nodeIds": []
			},
			"b8778cd3-636a-4d88-a48a-19c45c68135d-tableFooter": {
				"id": "b8778cd3-636a-4d88-a48a-19c45c68135d-tableFooter",
				"columnId": "b8778cd3-636a-4d88-a48a-19c45c68135d",
				"bandType": "tableFooter",
				"width": 100,
				"height": 25,
				"nodeIds": []
			},
			"28fde315-d261-4348-809a-83065bdf8682-tableHeader": {
				"id": "28fde315-d261-4348-809a-83065bdf8682-tableHeader",
				"columnId": "28fde315-d261-4348-809a-83065bdf8682",
				"bandType": "tableHeader",
				"width": 100,
				"height": 25,
				"nodeIds": []
			},
			"28fde315-d261-4348-809a-83065bdf8682-columnHeaderOfTable": {
				"id": "28fde315-d261-4348-809a-83065bdf8682-columnHeaderOfTable",
				"columnId": "28fde315-d261-4348-809a-83065bdf8682",
				"bandType": "columnHeaderOfTable",
				"width": 100,
				"height": 25,
				"nodeIds": [
					"node-6fa5f497-bd02-4d79-8f77-dc32b11af046"
				]
			},
			"28fde315-d261-4348-809a-83065bdf8682-columnData": {
				"id": "28fde315-d261-4348-809a-83065bdf8682-columnData",
				"columnId": "28fde315-d261-4348-809a-83065bdf8682",
				"bandType": "columnData",
				"width": 100,
				"height": 25,
				"nodeIds": [
					"node-67b6ff63-4b5c-4aef-8fe9-0b0b6a514235"
				]
			},
			"28fde315-d261-4348-809a-83065bdf8682-columnFooterOfTable": {
				"id": "28fde315-d261-4348-809a-83065bdf8682-columnFooterOfTable",
				"columnId": "28fde315-d261-4348-809a-83065bdf8682",
				"bandType": "columnFooterOfTable",
				"width": 100,
				"height": 25,
				"nodeIds": []
			},
			"28fde315-d261-4348-809a-83065bdf8682-tableFooter": {
				"id": "28fde315-d261-4348-809a-83065bdf8682-tableFooter",
				"columnId": "28fde315-d261-4348-809a-83065bdf8682",
				"bandType": "tableFooter",
				"width": 100,
				"height": 25,
				"nodeIds": []
			}
		},
		"nodes": {
			"node-4e83a1b0-6f91-460a-8203-be98c04e7af8": {
				"id": "node-4e83a1b0-6f91-460a-8203-be98c04e7af8",
				"label": "travel_id",
				"borders": {},
				"padding": {},
				"width": 100,
				"height": 25,
				"name": "text",
				"renderKey": "text",
				"parentKey": "elements",
				"isLeaf": true,
				"repeat": "na",
				"category": "text",
				"zIndex": 10,
				"type": "defaultNodes",
				"fontSize": 10,
				"x": 80,
				"y": 75,
				"isTableCell": true,
				"band": "columnHeaderOfTable",
				"cellId": "b8778cd3-636a-4d88-a48a-19c45c68135d-columnHeaderOfTable"
			},
			"node-c596e394-8ebe-4603-a8a4-b971c289fd8a": {
				"id": "node-c596e394-8ebe-4603-a8a4-b971c289fd8a",
				"name": "travel_id",
				"width": 100,
				"height": 25,
				"label": "$F{travel_id}",
				"renderKey": "text",
				"isLeaf": true,
				"zIndex": 10,
				"fontSize": 10,
				"type": "queryField",
				"category": "text",
				"repeat": "rd",
				"borders": {},
				"padding": {},
				"backendDataType": "java.lang.Integer",
				"x": 80,
				"y": 100,
				"isTableCell": true,
				"band": "columnData",
				"cellId": "b8778cd3-636a-4d88-a48a-19c45c68135d-columnData"
			},
			"node-6fa5f497-bd02-4d79-8f77-dc32b11af046": {
				"id": "node-6fa5f497-bd02-4d79-8f77-dc32b11af046",
				"label": "travel_date",
				"borders": {},
				"padding": {},
				"width": 100,
				"height": 25,
				"name": "text",
				"renderKey": "text",
				"parentKey": "elements",
				"isLeaf": true,
				"repeat": "na",
				"category": "text",
				"zIndex": 10,
				"type": "defaultNodes",
				"fontSize": 10,
				"x": 180,
				"y": 75,
				"isTableCell": true,
				"band": "columnHeaderOfTable",
				"cellId": "28fde315-d261-4348-809a-83065bdf8682-columnHeaderOfTable"
			},
			"node-67b6ff63-4b5c-4aef-8fe9-0b0b6a514235": {
				"id": "node-67b6ff63-4b5c-4aef-8fe9-0b0b6a514235",
				"name": "travel_date",
				"width": 100,
				"height": 25,
				"label": "$F{travel_date}",
				"renderKey": "text",
				"isLeaf": true,
				"zIndex": 10,
				"fontSize": 10,
				"type": "queryField",
				"category": "text",
				"repeat": "rd",
				"borders": {},
				"padding": {},
				"backendDataType": "java.sql.Timestamp",
				"x": 180,
				"y": 100,
				"isTableCell": true,
				"band": "columnData",
				"cellId": "28fde315-d261-4348-809a-83065bdf8682-columnData"
			}
		},
		"columnOrder": [
			"b8778cd3-636a-4d88-a48a-19c45c68135d",
			"28fde315-d261-4348-809a-83065bdf8682"
		],
		"bands": {
			"tableHeader": {
				"height": 25,
				"styles": {
					"backgroundColor": "#f0f8ff",
					"borderColor": "#000000"
				}
			},
			"columnHeaderOfTable": {
				"height": 25,
				"styles": {
					"backgroundColor": "#bfe1ff",
					"borderColor": "#000000"
				}
			},
			"tableGroupHeaders": {
				"height": 25,
				"styles": {
					"backgroundColor": "#f0f8ff",
					"borderColor": "#000000"
				},
				"groupFields": []
			},
			"columnData": {
				"height": 25,
				"styles": {
					"backgroundColor": "#ffffff",
					"borderColor": "#000000"
				}
			},
			"tableGroupFooters": {
				"height": 25,
				"styles": {
					"backgroundColor": "#f0f8ff",
					"borderColor": "#000000"
				},
				"groupFields": []
			},
			"columnFooterOfTable": {
				"height": 25,
				"styles": {
					"backgroundColor": "#bfe1ff",
					"borderColor": "#000000"
				}
			},
			"tableFooter": {
				"height": 25,
				"styles": {
					"backgroundColor": "#f0f8ff",
					"borderColor": "#000000"
				}
			}
		},
		"selectedColumnId": "",
		"selectedBandType": "",
		"selectedCellId": "",
		"selectedNodes": [],
		"tableConfig": {
			"addTableHeader": true,
			"addTableFooter": true,
			"addColumnHeader": true,
			"addColumnFooter": true,
			"addGroupHeader": true,
			"addGroupFooter": true,
			"bodyColor": "#ffffff",
			"borderStyle": "all",
			"bordersColor": "#000000",
			"columnHeaderColor": "#bfe1ff",
			"headerColor": "#f0f8ff"
		}
	},
	"otherOptions": [
		{
			"label": "Parameters",
			"value": "parameters",
			"children": [
				{
					"label": "Parameter1",
					"value": "$P{Parameter1}"
				},
				{
					"label": "Parameter2",
					"value": "$P{Parameter2}"
				}
			]
		},
		{
			"label": "Variables",
			"value": "variables",
			"children": [
				{
					"label": "Page_Number",
					"value": "$V{PAGE_NUMBER}"
				},
				{
					"label": "Column_Number",
					"value": "$V{COLUMN_NUMBER}"
				},
				{
					"label": "Report_Count",
					"value": "$V{REPORT_COUNT}"
				},
				{
					"label": "Page_Count",
					"value": "$V{PAGE_COUNT}"
				},
				{
					"label": "Column_Count",
					"value": "$V{COLUMN_COUNT}"
				},
				{
					"label": "Current_Date",
					"value": "new java.util.Date()"
				},
				{
					"label": "Current_Time",
					"value": "new java.util.Date()"
				},
				{
					"label": "Total_Pages",
					"value": "$V{PAGE_NUMBER}"
				}
			]
		},
		{
			"label": "Calculations",
			"value": "calculations",
			"children": [
				{
					"label": "test",
					"value": "$V{test}"
				}
			]
		}
	],
	"selectedQuery": {
		"id": 1,
		"name": "Query1",
		"config": "select * from \"travel_details\"",
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
		"parameterList": [],
		"temp_uuid": "147hi_hcr_db",
		"isSaved": true,
		"isNameEditable": false
	}
}