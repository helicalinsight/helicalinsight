import { Tooltip } from "antd";

export const hcrOldConfig = {
	diagramCanvas: {
		type: 'accordian',
		content: [
			{
				name: 'PAGE SETUP',
				type: 'settingsGroup',
				value: 'pageLayout',
				open: true,
				info: [ 'tooltip_pageSetup' ],
				content: [
					{
						name: 'Page Size',
						type: 'DropDown',
						value: 'pageLayout',
						path: [ 'pageLayoutInfo', 'layoutName' ],
						placeHolder: 'Layout',
						options: [ 'A3', 'A4', 'normal' ],
						callBack: 'pageSizeChange',
						fromStatic: true,
						optionsStaticPath: [ 'designerProperties', 'pageProperties' ],
						staticType: 'keys',
						info: [ 'tooltip_pageSize' ]
					},
					{
						name: 'Orientation',
						type: 'DropDown',
						value: 'Orientation',
						fromStatic: true,
						path: [ 'pageLayoutInfo', 'orientation' ],
						placeHolder: 'Orientation',
						options: [ 'landScape', 'Portrait' ],
						optionsStaticPath: [ 'designerProperties', 'orientation' ],
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
						path: [ 'pageLayoutInfo', 'margin', 'top' ],
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
						path: [ 'pageLayoutInfo', 'margin', 'left' ],
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
						path: [ 'pageLayoutInfo', 'margin', 'bottom' ],
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
						path: [ 'pageLayoutInfo', 'margin', 'right' ],
						options: '',
						// bootstrap: [ 12, 12, 6, 6 ],
						callBack: 'pageMarginChange'
					}
				]
			},
			{
				name: 'PAGE PROPERTIES',
				type: 'settingsGroup',
				value: 'report properties',
				open: false,
				content: [
					// not there in UI
					// {
					// 	label: 'Report Name',
					// 	display: false,
					// 	type: 'textBox',
					// 	value: 'reportName',
					// 	placeHolder: 'Enter Name',
					// 	path: [ 'reportName' ],
					// 	callBack: 'handleStateChange'
					// },
					{
						name: 'Column Count',
						path: [ 'columnCount' ],
						fromStatic: false,
						type: 'numericDropdown',
						min: 0,
						max: 10,
						quotes: false,
						info: [ 'tooltip_columnCount' ],
						callBack: 'updateStateWithPath'
					},
					{
						name: 'Column Spacing',
						path: [ 'columnSpacing' ],
						fromStatic: false,
						type: 'numericDropdown',
						bounds: 'free',
						quotes: false,
						info: [ 'tooltip_columnSpacing' ],
						callBack: 'updateStateWithPath'
					},
					{
						name: 'Column Width',
						path: [ 'columnWidth' ],
						fromStatic: false,
						type: 'numericDropdown',
						bounds: 'free',
						quotes: false,
						info: [ 'tooltip_columnWidth' ],
						callBack: 'updateStateWithPath'
					},
					{
						name: 'Print Order',
						type: 'DropDown',
						value: 'printOrder',
						fromStatic: true,
						options: [],
						optionsStaticPath: [ 'designerProperties', 'printOrder' ],
						path: [ 'printOrder' ],
						info: [ 'tooltip_printOrder' ],
						callBack: 'updateStateWithPath'
					},
					{
						name: 'When No Data',
						type: 'DropDown',
						value: 'whenNoDataType',
						placeHolder: 'Enter style name',
						fromState: false,
						fromStatic: true,
						extractValues: true,
						staticType: 'keys',
						returnValue: 'value',
						path: [ 'whenNoDataType' ],
						optionsStaticPath: [ 'designerProperties', 'whenNoDataType' ],
						info: [ 'tooltip_whenNoData' ],
						callBack: 'updateStateWithPath'
					}
					// {
					// 	label: 'summary with header and footer',
					// 	type: 'checkBox',
					// 	value: 'summaryWithPageHeaderAndFooter',
					// 	path: [ 'summaryWithPageHeaderAndFooter' ],
					// 	info: [ 'tooltip_summaryWithHeaderAndFooter' ],
					// 	title: true,
					// 	className: 'titleName',
					// 	callBack: 'updateStateWithPath'
					// },
					// {
					// 	label: 'Float column footer',
					// 	type: 'checkBox',
					// 	value: 'floatColumnFooter',
					// 	path: [ 'floatColumnFooter' ],
					// 	info: [ 'tooltip_floatingColumnFooter' ],
					// 	title: true,
					// 	className: 'titleName',
					// 	callBack: 'updateStateWithPath'
					// },
					// {
					// 	label: 'Title in new page',
					// 	type: 'checkBox',
					// 	value: 'titleNewPage',
					// 	path: [ 'titleNewPage' ],
					// 	info: [ 'tooltip_titleInNewPage' ],
					// 	title: true,
					// 	className: 'titleName',
					// 	callBack: 'updateStateWithPath'
					// },
					// {
					// 	label: 'Summary in new page',
					// 	type: 'checkBox',
					// 	value: 'summaryNewPage',
					// 	path: [ 'summaryNewPage' ],
					// 	title: true,
					// 	info: [ 'tooltip_summaryInNewPage' ],
					// 	className: 'titleName',
					// 	callBack: 'updateStateWithPath'
					// },
					// {
					// 	label: 'Ignore pagination',
					// 	type: 'checkBox',
					// 	value: 'ignorePagination',
					// 	path: [ 'ignorePagination' ],
					// 	title: true,
					// 	info: [ 'tooltip_ignorePagination' ],
					// 	className: 'titleName',
					// 	callBack: 'updateStateWithPath'
					// }
				]
			},
			// designerStylesEdit
			{
				name: 'PAGE STYLES',
				type: 'settingsGroup',
				value: 'designerStyles',
				open: false,
				content: [
					// {
					// 	name: 'Select Styles',
					// 	type: 'DropDown',
					// 	value: 'selectDesignerStyles',
					// 	fromState: true,
					// 	fromStatic: false,
					// 	extractValues: true,
					// 	keyName: 'name',
					// 	path: [ 'designerStylesNameForEdit' ],
					// 	bootstrap: [ 12 ],
					// 	optionsStatePath: [ 'designerStyles' ],
					// 	callBack: 'handleDesignerStyleForEdit'
					// },
					{
						label: 'Style name',
						type: 'textBox',
						value: 'styleName',
						placeHolder: 'Enter style',
						path: [ 'designerStylesEdit', 'name' ],
						info: [ 'tooltip_StyleName' ],
						bootstrap: [ 12 ],
						callBack: 'handleStateChange'
					},
					{
						name: 'Family',
						type: 'DropDown',
						value: 'fontFamily',
						fromStatic: true,
						options: [],
						optionsStaticPath: [ 'designerProperties', 'fontName' ],
						path: [ 'designerStylesEdit', 'fontName' ],
						bootstrap: [ 7 ],
						callBack: 'handleStateChange'
					},
					{
						name: 'Size',
						path: [ 'designerStylesEdit', 'textFontSize' ],
						fromStatic: false,
						bootstrap: [ 5 ],
						type: 'numericDropdown',
						quotes: false,
						callBack: 'handleStateChange'
					},
					{
						name: 'Mode',
						type: 'DropDown',
						value: 'mode',
						fromStatic: true,
						options: [],
						optionsStaticPath: [ 'designerProperties', 'mode' ],
						path: [ 'designerStylesEdit', 'mode' ],
						bootstrap: [ 4 ],
						info: [ 'tooltip_mode' ],
						callBack: 'handleStateChange'
					},
					{
						name: 'Fore Color',
						title: 'Fore Color',
						fromStatic: false,
						bootstrap: [ 4 ],
						type: 'colorPicker',
						quotes: true,
						callBack: 'handleStateChange',
						desc: 'pageStylesCommonColor',
						additionalPaths: [
							[ 'designerStylesEdit', 'imageForecolor' ],
							[ 'designerStylesEdit', 'textForecolor' ]
						],
						path: [ 'designerStylesEdit', 'textForecolor' ]
					},
					{
						name: 'Back Color',
						title: 'Back Color',
						fromStatic: false,
						bootstrap: [ 4 ],
						type: 'colorPicker',
						quotes: true,
						callBack: 'handleStateChange',
						desc: 'pageStylesCommonColor',
						additionalPaths: [
							[ 'designerStylesEdit', 'imageBackcolor' ],
							[ 'designerStylesEdit', 'textBackcolor' ]
						],
						path: [ 'designerStylesEdit', 'textBackcolor' ]
					},
					// {
					// 	name: 'Styles',
					// 	type: 'iconsButtonGroup',
					// 	controlled: false,
					// 	content: [
					// 		{
					// 			name: 'Bold',
					// 			title: 'Bold',
					// 			iconClass: 'boldIcon',
					// 			path: [ 'designerStylesEdit', 'bold' ],
					// 			bootstrap: [ 6, 6, 6, 6 ],
					// 			callBack: 'handleStateChange',
					// 			fromStatic: false,
					// 			type: 'iconButton'
					// 		},
					// 		{
					// 			name: 'Italic',
					// 			title: 'Italic',
					// 			iconClass: 'italicIcon',
					// 			path: [ 'designerStylesEdit', 'italic' ],
					// 			bootstrap: [ 6, 6, 6, 6 ],
					// 			callBack: 'handleStateChange',
					// 			fromStatic: false,
					// 			type: 'iconButton'
					// 		},
					// 		{
					// 			name: 'Strike Through',
					// 			title: 'Strike Through',
					// 			iconClass: 'strikeThroughIcon',
					// 			path: [ 'designerStylesEdit', 'strikeThrough' ],
					// 			bootstrap: [ 6, 6, 6, 6 ],
					// 			callBack: 'handleStateChange',
					// 			fromStatic: false,
					// 			type: 'iconButton'
					// 		},
					// 		{
					// 			name: 'UnderLine',
					// 			title: 'UnderLine',
					// 			iconClass: 'underLineIcon',
					// 			path: [ 'designerStylesEdit', 'underline' ],
					// 			bootstrap: [ 6, 6, 6, 6 ],
					// 			callBack: 'handleStateChange',
					// 			fromStatic: false,
					// 			type: 'iconButton'
					// 		}
					// 	]
					// },
					// {
					// 	name: 'Alignment',
					// 	type: 'iconsButtonGroup',
					// 	bootStrap: [ 4, 4, 4, 4 ],
					// 	controlled: true,
					// 	content: [
					// 		{
					// 			name: 'left Align',
					// 			title: 'Left Align',
					// 			iconClass: 'leftAlignIcon',
					// 			value: 'Left',
					// 			additionalPaths: [
					// 				[ 'designerStylesEdit', 'horizontalImageAlign' ],
					// 				[ 'designerStylesEdit', 'horizontalTextAlign' ]
					// 			],
					// 			path: [ 'designerStylesEdit', 'horizontalTextAlign' ],
					// 			bootstrap: [ 6, 6, 6, 6 ],
					// 			callBack: 'handleStateChange',
					// 			fromStatic: false,
					// 			type: 'iconButton',
					// 			className: 'styleshcrIconButton'
					// 		},
					// 		{
					// 			name: 'Center Align',
					// 			title: 'Center Align',
					// 			iconClass: 'centerAlignIcon',
					// 			value: 'Center',
					// 			path: [ 'designerStylesEdit', 'horizontalTextAlign' ],
					// 			additionalPaths: [
					// 				[ 'designerStylesEdit', 'horizontalImageAlign' ],
					// 				[ 'designerStylesEdit', 'horizontalTextAlign' ]
					// 			],
					// 			bootstrap: [ 6, 6, 6, 6 ],
					// 			callBack: 'handleStateChange',
					// 			fromStatic: false,
					// 			type: 'iconButton',
					// 			className: 'styleshcrIconButton'
					// 		},
					// 		{
					// 			name: 'Right Align',
					// 			title: 'Right Align',
					// 			value: 'Right',
					// 			iconClass: 'rightAlignIcon',
					// 			path: [ 'designerStylesEdit', 'horizontalTextAlign' ],
					// 			additionalPaths: [
					// 				[ 'designerStylesEdit', 'horizontalImageAlign' ],
					// 				[ 'designerStylesEdit', 'horizontalTextAlign' ]
					// 			],
					// 			bootstrap: [ 6, 6, 6, 6 ],
					// 			callBack: 'handleStateChange',
					// 			fromStatic: false,
					// 			type: 'iconButton',
					// 			className: 'styleshcrIconButton'
					// 		},
					// 		{
					// 			name: 'Horizontal Justify',
					// 			title: 'Horizontal Justify',
					// 			value: 'Justified',
					// 			iconClass: 'justifyIcon',
					// 			path: [ 'designerStylesEdit', 'horizontalTextAlign' ],
					// 			bootstrap: [ 6, 6, 6, 6 ],
					// 			callBack: 'handleStateChange',
					// 			fromStatic: false,
					// 			type: 'iconButton',
					// 			className: 'styleshcrIconButton'
					// 		}
					// 	]
					// },
					// {
					// 	type: 'iconsButtonGroup',
					// 	controlled: true,
					// 	content: [
					// 		{
					// 			name: 'Top Align',
					// 			title: 'Top Align',
					// 			iconClass: 'topAlign',
					// 			value: 'Top',
					// 			path: [ 'designerStylesEdit', 'verticalTextAlign' ],
					// 			bootstrap: [ 6 ],
					// 			additionalPaths: [
					// 				[ 'designerStylesEdit', 'verticalImageAlign' ],
					// 				[ 'designerStylesEdit', 'verticalTextAlign' ]
					// 			],
					// 			callBack: 'handleStateChange',
					// 			fromStatic: false,
					// 			type: 'iconButton'
					// 		},
					// 		{
					// 			name: 'Center Align',
					// 			title: 'Center Align',
					// 			iconClass: 'centerAlign',
					// 			value: 'Middle',
					// 			path: [ 'designerStylesEdit', 'verticalTextAlign' ],
					// 			bootstrap: [ 6, 6, 6, 6 ],
					// 			additionalPaths: [
					// 				[ 'designerStylesEdit', 'verticalImageAlign' ],
					// 				[ 'designerStylesEdit', 'verticalTextAlign' ]
					// 			],
					// 			callBack: 'handleStateChange',
					// 			fromStatic: false,
					// 			type: 'iconButton'
					// 		},
					// 		{
					// 			name: 'Bottom Align',
					// 			title: 'Bottom Align',
					// 			value: 'Bottom',
					// 			iconClass: 'bottomAlign',
					// 			path: [ 'designerStylesEdit', 'verticalTextAlign' ],
					// 			bootstrap: [ 6, 6, 6, 6 ],
					// 			additionalPaths: [
					// 				[ 'designerStylesEdit', 'verticalImageAlign' ],
					// 				[ 'designerStylesEdit', 'verticalTextAlign' ]
					// 			],
					// 			callBack: 'handleStateChange',
					// 			fromStatic: false,
					// 			type: 'iconButton'
					// 		},
					// 		{
					// 			name: 'Vertical Justify',
					// 			title: 'Vertical Justify',
					// 			value: 'Justified',
					// 			iconClass: 'justifyIcon',
					// 			path: [ 'designerStylesEdit', 'verticalTextAlign' ],
					// 			bootstrap: [ 6, 6, 6, 6 ],
					// 			callBack: 'handleStateChange',
					// 			fromStatic: false,
					// 			type: 'iconButton'
					// 		}
					// 	]
					// },
					{
						name: 'Rotation',
						type: 'DropDown',
						value: 'rotationType',
						fromStatic: true,
						options: [],
						optionsStaticPath: [ 'designerProperties', 'rotationType' ],
						path: [ 'designerStylesEdit', 'rotationType' ],
						bootstrap: [ 6 ],
						info: [ 'tooltip_rotationType' ],
						callBack: 'handleStateChange'
					},
					{
						name: 'Mark up',
						type: 'DropDown',
						value: 'markup',
						fromStatic: true,
						options: [],
						optionsStaticPath: [ 'designerProperties', 'markUp' ],
						path: [ 'designerStylesEdit', 'markup' ],
						bootstrap: [ 6 ],
						info: [ 'tooltip_markUp' ],
						callBack: 'handleStateChange'
					},
					// {
					// 	label: 'Blank when null',
					// 	type: 'checkBox',
					// 	value: 'blankWhenNull',
					// 	path: [ 'designerStylesEdit', 'blankWhenNull' ],
					// 	bootstrap: [ 12 ],
					// 	title: true,
					// 	className: 'titleName',
					// 	info: [ 'tooltip_blankWhenNull' ],
					// 	callBack: 'updateStateWithPath'
					// },
					// {
					// 	label: 'Default style',
					// 	type: 'checkBox',
					// 	value: 'isDefault',
					// 	path: [ 'designerStylesEdit', 'isDefault' ],
					// 	bootstrap: [ 12 ],
					// 	title: true,
					// 	className: 'titleName',
					// 	info: [ 'tooltip_isDefaultStyle' ],
					// 	callBack: 'updateStateWithPath'
					// },
					{
						label: 'Pattern',
						type: 'textBox',
						value: 'pattern',
						placeHolder: 'Enter pattern',
						path: [ 'designerStylesEdit', 'pattern' ],
						bootstrap: [ 12 ],
						info: [ 'tooltip_pattern' ],
						callBack: 'handleStateChange'
					},
					// discuss
					// {
					// 	name: 'Padding',
					// 	type: 'title',
					// 	bootstrap: [ 12, 12, 12, 12 ]
					// },
					{
						name: 'Top',
						title: 'Top',
						fromStatic: false,
						bootstrap: [ 12, 12, 6, 6 ],
						type: 'numericDropdown',
						quotes: false,
						callBack: 'handleStateChange',
						path: [ 'designerStylesEdit', 'border', 'padding', 'topPadding' ]
					},
					{
						name: 'Bottom',
						title: 'Bottom',
						fromStatic: false,
						bootstrap: [ 12, 12, 6, 6 ],
						type: 'numericDropdown',
						quotes: false,
						callBack: 'handleStateChange',
						path: [ 'designerStylesEdit', 'border', 'padding', 'bottomPadding' ]
					},
					{
						name: 'Left',
						title: 'Left',
						fromStatic: false,
						bootstrap: [ 12, 12, 6, 6 ],
						type: 'numericDropdown',
						quotes: false,
						callBack: 'handleStateChange',
						path: [ 'designerStylesEdit', 'border', 'padding', 'leftPadding' ]
					},
					{
						name: 'Right',
						title: 'Right',
						fromStatic: false,
						bootstrap: [ 12, 12, 6, 6 ],
						type: 'numericDropdown',
						quotes: false,
						callBack: 'handleStateChange',
						path: [ 'designerStylesEdit', 'border', 'padding', 'rightPadding' ]
					},
					// {
					// 	type: 'seperator'
					// },
					// {
					// 	name: 'Top',
					// 	type: 'title',
					// 	bootstrap: [ 12, 12, 12, 12 ]
					// },
					// {
					// 	name: 'Stroke',
					// 	title: 'Top',
					// 	fromStatic: false,
					// 	bootstrap: [ 12, 12, 4, 4 ],
					// 	type: 'numericDropdown',
					// 	quotes: false,
					// 	callBack: 'handleStateChange',
					// 	path: [ 'designerStylesEdit', 'border', 'line', 'topLine', 'lineWidth' ]
					// },
					{
						name: 'Color',
						title: 'color',
						fromStatic: false,
						bootstrap: [ 12, 12, 4, 4 ],
						type: 'colorPicker',
						quotes: false,
						callBack: 'handleStateChange',
						path: [ 'designerStylesEdit', 'border', 'line', 'topLine', 'lineColor' ]
					},
					{
						name: 'Style',
						title: 'Style',
						type: 'DropDown',
						value: 'lineStyle',
						bootstrap: [ 12, 12, 4, 4 ],
						options: [],
						fromStatic: true,
						optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
						path: [ 'designerStylesEdit', 'border', 'line', 'topLine', 'lineStyle' ],
						callBack: 'handleStateChange'
					}
					// {
					// 	type: 'seperator'
					// },
					// {
					// 	name: 'Bottom',
					// 	type: 'title',
					// 	bootstrap: [ 12, 12, 12, 12 ]
					// },
					// {
					// 	name: 'Stroke',
					// 	title: 'Stroke',
					// 	fromStatic: false,
					// 	bootstrap: [ 12, 12, 4, 4 ],
					// 	type: 'numericDropdown',
					// 	quotes: false,
					// 	callBack: 'handleStateChange',
					// 	path: [ 'designerStylesEdit', 'border', 'line', 'bottomLine', 'lineWidth' ]
					// },
					// {
					// 	name: 'Color',
					// 	title: 'Color',
					// 	fromStatic: false,
					// 	bootstrap: [ 12, 12, 4, 4 ],
					// 	type: 'colorPicker',
					// 	quotes: false,
					// 	callBack: 'handleStateChange',
					// 	path: [ 'designerStylesEdit', 'border', 'line', 'bottomLine', 'lineColor' ]
					// },
					// {
					// 	name: 'Style',
					// 	title: 'Style',
					// 	type: 'DropDown',
					// 	value: 'lineStyle',
					// 	bootstrap: [ 12, 12, 4, 4 ],
					// 	options: [],
					// 	fromStatic: true,
					// 	optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
					// 	path: [ 'designerStylesEdit', 'border', 'line', 'bottomLine', 'lineStyle' ],
					// 	callBack: 'handleStateChange'
					// },
					// {
					// 	type: 'seperator'
					// },
					// {
					// 	name: 'Left',
					// 	type: 'title',
					// 	bootstrap: [ 12, 12, 12, 12 ]
					// },
					// {
					// 	name: 'Stroke',
					// 	title: 'Stroke',
					// 	fromStatic: false,
					// 	bootstrap: [ 12, 12, 4, 4 ],
					// 	type: 'numericDropdown',
					// 	quotes: false,
					// 	callBack: 'handleStateChange',
					// 	path: [ 'designerStylesEdit', 'border', 'line', 'leftLine', 'lineWidth' ]
					// },
					// {
					// 	name: 'Color',
					// 	title: 'Color',
					// 	fromStatic: false,
					// 	bootstrap: [ 12, 12, 4, 4 ],
					// 	type: 'colorPicker',
					// 	quotes: false,
					// 	callBack: 'handleStateChange',
					// 	path: [ 'designerStylesEdit', 'border', 'line', 'leftLine', 'lineColor' ]
					// },
					// {
					// 	name: 'Style',
					// 	title: 'Style',
					// 	type: 'DropDown',
					// 	value: 'lineStyle',
					// 	bootstrap: [ 12, 12, 4, 4 ],
					// 	options: [],
					// 	fromStatic: true,
					// 	optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
					// 	path: [ 'designerStylesEdit', 'border', 'line', 'leftLine', 'lineStyle' ],
					// 	callBack: 'handleStateChange'
					// },
					// {
					// 	name: 'Right',
					// 	type: 'title',
					// 	bootstrap: [ 12, 12, 12, 12 ]
					// },
					// {
					// 	name: 'Stroke',
					// 	title: 'Stroke',
					// 	fromStatic: false,
					// 	bootstrap: [ 12, 12, 4, 4 ],
					// 	type: 'numericDropdown',
					// 	quotes: false,
					// 	callBack: 'handleStateChange',
					// 	path: [ 'designerStylesEdit', 'border', 'line', 'rightLine', 'lineWidth' ]
					// },
					// {
					// 	name: 'Color',
					// 	title: 'Color',
					// 	fromStatic: false,
					// 	bootstrap: [ 12, 12, 4, 4 ],
					// 	type: 'colorPicker',
					// 	quotes: true,
					// 	callBack: 'handleStateChange',
					// 	path: [ 'designerStylesEdit', 'border', 'line', 'rightLine', 'lineColor' ]
					// },
					// {
					// 	name: 'Style',
					// 	title: 'Style',
					// 	type: 'DropDown',
					// 	value: 'lineStyle',
					// 	bootstrap: [ 12, 12, 4, 4 ],
					// 	options: [],
					// 	fromStatic: true,
					// 	optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
					// 	path: [ 'designerStylesEdit', 'border', 'line', 'rightLine', 'lineStyle' ],
					// 	callBack: 'handleStateChange'
					// },
					// {
					// 	name: 'Line Styles',
					// 	type: 'title',
					// 	bootstrap: [ 12 ]
					// },
					// {
					// 	name: 'Stroke',
					// 	type: 'numericDropdown',
					// 	value: 2,
					// 	fromStatic: false,
					// 	path: [ 'designerStylesEdit', 'lineStyle', 'penLineWidth' ],
					// 	bootstrap: [ 12, 12, 4, 4 ],
					// 	callBack: 'handleStateChange'
					// },
					// {
					// 	name: 'Color',
					// 	type: 'colorPicker',
					// 	value: 'foreColor',
					// 	fromStatic: true,
					// 	path: [ 'designerStylesEdit', 'lineStyle', 'lineForecolor' ],
					// 	bootstrap: [ 12, 12, 4, 4 ],
					// 	callBack: 'handleStateChange'
					// },
					// {
					// 	name: 'Style',
					// 	type: 'DropDown',
					// 	value: 'lineStyle',
					// 	options: [],
					// 	fromStatic: true,
					// 	optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
					// 	path: [ 'designerStylesEdit', 'lineStyle', 'lineStyle' ],
					// 	bootstrap: [ 12, 12, 4, 4 ],
					// 	callBack: 'handleStateChange'
					// }
					// {
					// 	name: 'Delete',
					// 	type: 'button',
					// 	className: '',
					// 	showName: false,
					// 	bootstrap: [ 4 ],
					// 	callBack: 'handleDesignerStyleDelete'
					// },
					// {
					// 	name: 'Update',
					// 	type: 'button',
					// 	className: '',
					// 	showName: false,
					// 	bootstrap: [ 4 ],
					// 	callBack: 'handleStylesPropertiesUpdate'
					// },
					// {
					// 	name: 'Add',
					// 	type: 'button',
					// 	className: '',
					// 	showName: false,
					// 	bootstrap: [ 4 ],
					// 	highlight: true,
					// 	when: 'designerStyleEdit',
					// 	callBack: 'handleStylesPropertiesAdd'
					// }
				]
			},
			// variablesForEdit
			{
				name: 'CALCULATIONS',
				type: 'settingsGroup',
				value: 'variables',
				open: false,
				bootstrap: [ 12 ],
				content: [
					{
						name: 'Select Calculation',
						type: 'DropDown',
						value: 'selectedVariableEdit',
						fromState: true,
						fromStatic: false,
						extractValues: true,
						keyName: 'name',
						path: [ 'variablesEditName' ],
						bootstrap: [ 12 ],
						optionsStatePath: [ 'variables' ],
						callBack: 'handleVariableChangeForEdit'
					},
					{
						label: 'Name',
						type: 'textBox',
						value: 'name',
						placeHolder: 'Enter name',
						path: [ 'variablesForEdit', 'name' ],
						bootstrap: [ 12 ],
						info: [ 'tooltip_Name' ],
						callBack: 'handleStateChange'
					},
					{
						name: 'Class Name',
						type: 'DropDown',
						value: 'className',
						options: [],
						fromStatic: true,
						staticType: 'keys',
						optionsStaticPath: [ 'designerProperties', 'variables', 'classNames' ],
						formatOutput: true,
						formatOptions: {
							display: 'keys',
							value: 'values'
						},
						path: [ 'variablesForEdit', 'className' ],
						bootstrap: [ 6 ],
						info: [ 'tooltip_varClassName' ],
						callBack: 'handleStateChange'
					},
					{
						name: 'Calculation',
						type: 'DropDown',
						value: 'calculation',
						options: [],
						fromStatic: true,
						optionsStaticPath: [ 'designerProperties', 'calculations' ],
						path: [ 'variablesForEdit', 'calculation' ],
						bootstrap: [ 6 ],
						info: [ 'tooltip_varCalculation' ],
						callBack: 'handleStateChange'
					},
					{
						name: 'Reset Type',
						type: 'DropDown',
						value: 'resetType',
						options: [],
						fromStatic: true,
						optionsStaticPath: [ 'designerProperties', 'resetType' ],
						path: [ 'variablesForEdit', 'resetType' ],
						bootstrap: [ 6 ],
						info: [ 'tooltip_varResetType' ],
						callBack: 'handleStateChange'
					},
					{
						name: 'Reset Group',
						type: 'DropDown',
						value: 'ResetGroup',
						fromState: true,
						fromStatic: false,
						extractValues: true,
						keyName: 'name',
						path: [ 'variablesForEdit', 'resetGroup' ],
						bootstrap: [ 6 ],
						info: [ 'tooltip_varResetGroup' ],
						optionsStatePath: [ 'groupBandInfo' ],
						callBack: 'handleStateChange'
					},
					{
						name: 'Increment',
						type: 'DropDown',
						value: 'incrementType',
						options: [],
						fromStatic: true,
						optionsStaticPath: [ 'designerProperties', 'incrementType' ],
						path: [ 'variablesForEdit', 'incrementType' ],
						bootstrap: [ 6 ],
						info: [ 'tooltip_varIncrement' ],
						callBack: 'handleStateChange'
					},
					{
						name: 'Increment Group',
						type: 'DropDown',
						value: 'IncrementGroup',
						fromState: true,
						fromStatic: false,
						extractValues: true,
						keyName: 'name',
						path: [ 'variablesForEdit', 'incrementGroup' ],
						bootstrap: [ 6 ],
						info: [ 'tooltip_varIncrementGroup' ],
						optionsStatePath: [ 'groupBandInfo' ],
						callBack: 'handleStateChange'
					},
					{
						label: 'Expression',
						type: 'textBox',
						value: 'expression',
						placeHolder: 'Enter expression',
						path: [ 'variablesForEdit', 'expression' ],
						bootstrap: [ 12 ],
						info: [ 'tooltip_varExpression' ],
						callBack: 'handleStateChange'
					},
					{
						label: 'Initial Value Expression',
						type: 'textBox',
						value: 'initialValueExpression',
						placeHolder: 'Enter Initial Value',
						path: [ 'variablesForEdit', 'initialValueExpression' ],
						bootstrap: [ 12 ],
						info: [ 'tooltip_varInitialValueExpression' ],
						callBack: 'handleStateChange'
					},
					{
						label: 'Increment Factory ClassName',
						type: 'textBox',
						value: 'incrementFactoryClassName',
						placeHolder: 'Enter Initial Value',
						path: [ 'variablesForEdit', 'incrementFactoryClassName' ],
						bootstrap: [ 12 ],
						info: [ 'tooltip_varIncrementFactoryClassName' ],
						callBack: 'handleStateChange'
					}
					// {
					// 	name: 'Clear',
					// 	type: 'button',
					// 	showName: false,
					// 	className: '',
					// 	bootstrap: [ 6 ],
					// 	callBack: 'handleVariableClear'
					// },
					// {
					// 	name: 'Save',
					// 	type: 'button',
					// 	className: '',
					// 	showName: false,
					// 	bootstrap: [ 6 ],
					// 	highlight: true,
					// 	when: 'calulationEdit',
					// 	callBack: 'handleVariableUpdate'
					// }
				]
			},
			{
				name: 'PREVIEW PARAMETERS',
				type: 'settingsGroup',
				value: 'previewParameters',
				open: false,
				bootstrap: [ 12 ],
				content: [
					{
						label: 'Show Parameters',
						type: 'checkBox',
						value: 'showParametersInPreview',
						path: [ 'showParametersInPreview' ],
						bootstrap: [ 12 ],
						title: true,
						className: 'titleName',
						callBack: 'updateStateWithPath'
					},
					{
						name: 'Position',
						type: 'DropDown',
						value: 'Position',
						fromStatic: true,
						path: [ 'parametersInPreview', 'position' ],
						placeHolder: 'Orientation',
						options: [ 'landScape', 'Portrait' ],
						optionsStaticPath: [ 'designerProperties', 'parametersInPreview', 'position' ],
						bootstrap: [ 12, 12, 12, 12 ],
						callBack: 'handleStateChange'
					}
				]
			},
			{
				name: 'GROUP PROPERTIES',
				type: 'settingsGroup',
				value: 'groupParameters',
				open: false,
				bootstrap: [ 12 ],
				content: [
					{
						name: 'Select Group',
						type: 'DropDown',
						value: 'selectGroup',
						fromState: true,
						fromStatic: false,
						extractValues: true,
						keyName: 'name',
						path: [ 'groupPropertiesEditName' ],
						bootstrap: [ 12 ],
						optionsStatePath: [ 'groupBandInfo' ],
						callBack: 'handleGroupPropertiesChange',
						desc: 'selectGroupNameForPropertiesEdit'
					},
					{
						name: 'Minimum height to start new page',
						type: 'numericDropdown',
						value: 'minHeightToStartNewPage',
						path: [ 'groupPropertiesEdit', 'minHeightToStartNewPage' ],
						bootstrap: [ 6 ],
						bounds: 'free',
						callBack: 'handleGroupPropertiesChange',
						info: [ 'tooltip_minHeightToStartNewPage' ],
						desc: 'propertyChanged'
					},
					{
						name: 'Minimum records to start from top',
						type: 'numericDropdown',
						value: 'minDetailsToStartFromTop',
						path: [ 'groupPropertiesEdit', 'minDetailsToStartFromTop' ],
						bootstrap: [ 6 ],
						bounds: 'free',
						callBack: 'handleGroupPropertiesChange',
						info: [ 'tooltip_minDetailsToStartFromTop' ],
						desc: 'propertyChanged'
					}
					// {
					// 	label: 'Reprint header on each page',
					// 	type: 'checkBox',
					// 	value: 'reprintHeaderOnEachPage',
					// 	path: [ 'groupPropertiesEdit', 'reprintHeaderOnEachPage' ],
					// 	bootstrap: [ 12 ],
					// 	title: true,
					// 	className: 'titleName',
					// 	callBack: 'handleGroupPropertiesChange',
					// 	info: [ 'tooltip_reprintHeaderOnEachPage' ],
					// 	desc: 'propertyChanged'
					// },
					// {
					// 	label: 'Keep Together',
					// 	type: 'checkBox',
					// 	value: 'keepTogether',
					// 	path: [ 'groupPropertiesEdit', 'keepTogether' ],
					// 	bootstrap: [ 12 ],
					// 	title: true,
					// 	className: 'titleName',
					// 	info: [ 'tooltip_keepTogether' ],
					// 	callBack: 'handleGroupPropertiesChange',
					// 	desc: 'propertyChanged'
					// },
					// {
					// 	label: 'Start new column',
					// 	type: 'checkBox',
					// 	value: 'startNewColumn',
					// 	path: [ 'groupPropertiesEdit', 'startNewColumn' ],
					// 	bootstrap: [ 12 ],
					// 	title: true,
					// 	className: 'titleName',
					// 	info: [ 'tooltip_startNewColumn' ],
					// 	callBack: 'handleGroupPropertiesChange',
					// 	desc: 'propertyChanged'
					// },
					// {
					// 	label: 'Start new page',
					// 	type: 'checkBox',
					// 	value: 'startNewPage',
					// 	path: [ 'groupPropertiesEdit', 'startNewPage' ],
					// 	bootstrap: [ 12 ],
					// 	title: true,
					// 	className: 'titleName',
					// 	info: [ 'tooltip_startNewPage' ],
					// 	callBack: 'handleGroupPropertiesChange',
					// 	desc: 'propertyChanged'
					// },
					// {
					// 	label: 'Reset page number',
					// 	type: 'checkBox',
					// 	value: 'resetPageNumber',
					// 	path: [ 'groupPropertiesEdit', 'resetPageNumber' ],
					// 	bootstrap: [ 12 ],
					// 	title: true,
					// 	className: 'titleName',
					// 	info: [ 'tooltip_resetPageNumber' ],
					// 	callBack: 'handleGroupPropertiesChange',
					// 	desc: 'propertyChanged'
					// },
					// {
					// 	label: 'Prevent Orphan Footer',
					// 	type: 'checkBox',
					// 	value: 'preventOrphanFooter',
					// 	path: [ 'groupPropertiesEdit', 'preventOrphanFooter' ],
					// 	info: [ 'tooltip_preventOrphanFooter' ],
					// 	bootstrap: [ 12 ],
					// 	title: true,
					// 	className: 'titleName',
					// 	callBack: 'handleGroupPropertiesChange',
					// 	desc: 'propertyChanged'
					// }
				]
			}
		]
	},
	textField: {
		type: 'accordian',
		content: [
			{
				name: 'Typography',
				type: 'settingsGroup',
				value: 'typography',
				open: true,
				content: [
					{
						name: 'Content',
						title: 'Content',
						path: [ 'shapeIds', '$activeShape', 'properties', 'textFieldExpression' ],
						info: [ 'tooltip_content' ],
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
						optionsStaticPath: [ 'designerProperties', 'fontName' ],
						path: [ 'shapeIds', '$activeShape', 'properties', 'fontName' ],
						bootstrap: [ 7 ],
						callBack: 'handleShapePropertyChanges'
					},
					{
						name: 'Size',
						path: [ 'shapeIds', '$activeShape', 'properties', 'textFontSize' ],
						fromStatic: false,
						bootstrap: [ 5 ],
						type: 'numericDropdown',
						quotes: false,
						callBack: 'handleShapePropertyChanges'
					}
				]
			}
		]
	}
};

let nxt = [
	// {
	// 	type: 'seperator'
	// },
	{
		name: 'Styles',
		type: 'iconsButtonGroup',
		controlled: false,
		content: [
			{
				name: 'Bold',
				title: 'Bold',
				iconClass: 'boldIcon',
				path: [ 'shapeIds', '$activeShape', 'properties', 'bold' ],
				bootstrap: [ 6, 6, 6, 6 ],
				callBack: 'handleShapePropertyChanges',
				fromStatic: false,
				type: 'iconButton'
			},
			{
				name: 'Italic',
				title: 'Italic',
				iconClass: 'italicIcon',
				path: [ 'shapeIds', '$activeShape', 'properties', 'italic' ],
				bootstrap: [ 6, 6, 6, 6 ],
				callBack: 'handleShapePropertyChanges',
				fromStatic: false,
				type: 'iconButton'
			},
			{
				name: 'Strike Through',
				title: 'Strike Through',
				iconClass: 'strikeThroughIcon',
				path: [ 'shapeIds', '$activeShape', 'properties', 'strikeThrough' ],
				bootstrap: [ 6, 6, 6, 6 ],
				callBack: 'handleShapePropertyChanges',
				fromStatic: false,
				type: 'iconButton'
			},
			{
				name: 'UnderLine',
				title: 'UnderLine',
				iconClass: 'underLineIcon',
				path: [ 'shapeIds', '$activeShape', 'properties', 'underline' ],
				bootstrap: [ 6, 6, 6, 6 ],
				callBack: 'handleShapePropertyChanges',
				fromStatic: false,
				type: 'iconButton'
			}
		]
	},
	// {
	// 	type: 'seperator'
	// },
	// {
	// 	name: 'Color',
	// 	type: 'title'
	// },
	{
		name: 'Mode',
		type: 'DropDown',
		value: 'mode',
		fromStatic: true,
		options: [],
		optionsStaticPath: [ 'designerProperties', 'mode' ],
		path: [ 'shapeIds', '$activeShape', 'properties', 'mode' ],
		info: [ 'tooltip_mode' ],
		bootstrap: [ 12, 12, 4, 4 ],
		callBack: 'handleShapePropertyChanges'
	},
	{
		name: 'Fore Color',
		type: 'colorPicker',
		value: 'textColor',
		fromStatic: true,
		path: [ 'shapeIds', '$activeShape', 'properties', 'textForecolor' ],
		bootstrap: [ 12, 12, 4, 4 ],
		callBack: 'handleShapePropertyChanges'
	},
	{
		name: 'Back Color',
		type: 'colorPicker',
		value: 'backgroundColor',
		fromStatic: true,
		path: [ 'shapeIds', '$activeShape', 'properties', 'textBackcolor' ],
		bootstrap: [ 12, 12, 4, 4 ],
		callBack: 'handleShapePropertyChanges'
	},
	// {
	// 	type: 'seperator'
	// },
	{
		name: 'Alignment',
		type: 'iconsButtonGroup',
		bootStrap: [ 4, 4, 4, 4 ],
		controlled: true,
		content: [
			{
				name: 'left Align',
				title: 'Left Align',
				iconClass: 'leftAlignIcon',
				value: 'Left',
				path: [ 'shapeIds', '$activeShape', 'properties', 'horizontalTextAlign' ],
				bootstrap: [ 6, 6, 6, 6 ],
				callBack: 'handleShapePropertyChanges',
				fromStatic: false,
				type: 'iconButton',
				className: 'hcrIconButton'
			},
			{
				name: 'Center Align',
				title: 'Center Align',
				iconClass: 'centerAlignIcon',
				value: 'Center',
				path: [ 'shapeIds', '$activeShape', 'properties', 'horizontalTextAlign' ],
				bootstrap: [ 6, 6, 6, 6 ],
				callBack: 'handleShapePropertyChanges',
				fromStatic: false,
				type: 'iconButton',
				className: 'hcrIconButton'
			},
			{
				name: 'Right Align',
				title: 'Right Align',
				value: 'Right',
				iconClass: 'rightAlignIcon',
				path: [ 'shapeIds', '$activeShape', 'properties', 'horizontalTextAlign' ],
				bootstrap: [ 6, 6, 6, 6 ],
				callBack: 'handleShapePropertyChanges',
				fromStatic: false,
				type: 'iconButton',
				className: 'hcrIconButton'
			},
			{
				name: 'Horizontal Justify',
				title: 'Horizontal Justify',
				value: 'Justified',
				iconClass: 'hJustifyIcon',
				path: [ 'shapeIds', '$activeShape', 'properties', 'horizontalTextAlign' ],
				bootstrap: [ 6, 6, 6, 6 ],
				callBack: 'handleShapePropertyChanges',
				fromStatic: false,
				type: 'iconButton',
				className: 'hcrIconButton'
			}
		]
	},
	{
		type: 'iconsButtonGroup',
		controlled: true,
		content: [
			{
				name: 'Top Align',
				title: 'Top Align',
				iconClass: 'topAlign',
				value: 'Top',
				path: [ 'shapeIds', '$activeShape', 'properties', 'verticalTextAlign' ],
				bootstrap: [ 6, 6, 6, 6 ],
				callBack: 'handleShapePropertyChanges',
				fromStatic: false,
				type: 'iconButton',
				className: 'hcrIconButton'
			},
			{
				name: 'Center Align',
				title: 'Center Align',
				iconClass: 'centerAlign',
				value: 'Middle',
				path: [ 'shapeIds', '$activeShape', 'properties', 'verticalTextAlign' ],
				bootstrap: [ 6, 6, 6, 6 ],
				callBack: 'handleShapePropertyChanges',
				fromStatic: false,
				type: 'iconButton',
				className: 'hcrIconButton'
			},
			{
				name: 'Bottom Align',
				title: 'Bottom Align',
				value: 'Bottom',
				iconClass: 'bottomAlign',
				path: [ 'shapeIds', '$activeShape', 'properties', 'verticalTextAlign' ],
				bootstrap: [ 6, 6, 6, 6 ],
				callBack: 'handleShapePropertyChanges',
				fromStatic: false,
				type: 'iconButton',
				className: 'hcrIconButton'
			},
			{
				name: 'Vertical Justify',
				title: 'Vertical Justify',
				value: 'Justified',
				iconClass: 'vJustifyIcon',
				path: [ 'shapeIds', '$activeShape', 'properties', 'verticalTextAlign' ],
				bootstrap: [ 6, 6, 6, 6 ],
				callBack: 'handleShapePropertyChanges',
				fromStatic: false,
				type: 'iconButton',
				className: 'hcrIconButton'
			}
		]
	}
];

export const hcrOriginalConf = {
	HCR: JSON.parse(
		'\n{\n\t"HCR": {\n\t\t"designerProperties": {\n\t\t\t"pageProperties": {\n\t\t\t\t"LETTER": {\n\t\t\t\t\t"pixel": {\n\t\t\t\t\t\t"width": 612,\n\t\t\t\t\t\t"height": 792\n\t\t\t\t\t},\n\t\t\t\t\t"inch": {\n\t\t\t\t\t\t"height": 11,\n\t\t\t\t\t\t"width": 8.5\n\t\t\t\t\t}\n\t\t\t\t},\n\t\t\t\t"NOTE": {\n\t\t\t\t\t"pixel": {\n\t\t\t\t\t\t"width": 540,\n\t\t\t\t\t\t"height": 720\n\t\t\t\t\t},\n\t\t\t\t\t"inch": {\n\t\t\t\t\t\t"height": 10,\n\t\t\t\t\t\t"width": 7.5\n\t\t\t\t\t}\n\t\t\t\t},\n\t\t\t\t"LEGAL": {\n\t\t\t\t\t"pixel": {\n\t\t\t\t\t\t"width": 612,\n\t\t\t\t\t\t"height": 1008\n\t\t\t\t\t},\n\t\t\t\t\t"inch": {\n\t\t\t\t\t\t"height": 14,\n\t\t\t\t\t\t"width": 8.5\n\t\t\t\t\t}\n\t\t\t\t},\n\t\t\t\t"A0": {\n\t\t\t\t\t"pixel": {\n\t\t\t\t\t\t"width": 2384,\n\t\t\t\t\t\t"height": 3370\n\t\t\t\t\t},\n\t\t\t\t\t"inch": {\n\t\t\t\t\t\t"height": 46.8056,\n\t\t\t\t\t\t"width": 33.1111\n\t\t\t\t\t}\n\t\t\t\t},\n\t\t\t\t"A1": {\n\t\t\t\t\t"pixel": {\n\t\t\t\t\t\t"width": 1684,\n\t\t\t\t\t\t"height": 2384\n\t\t\t\t\t},\n\t\t\t\t\t"inch": {\n\t\t\t\t\t\t"height": 33.1111,\n\t\t\t\t\t\t"width": 23.3889\n\t\t\t\t\t}\n\t\t\t\t},\n\t\t\t\t"A2": {\n\t\t\t\t\t"pixel": {\n\t\t\t\t\t\t"width": 1191,\n\t\t\t\t\t\t"height": 1684\n\t\t\t\t\t},\n\t\t\t\t\t"inch": {\n\t\t\t\t\t\t"height": 23.3889,\n\t\t\t\t\t\t"width": 16.5417\n\t\t\t\t\t}\n\t\t\t\t},\n\t\t\t\t"A3": {\n\t\t\t\t\t"pixel": {\n\t\t\t\t\t\t"width": 842,\n\t\t\t\t\t\t"height": 1191\n\t\t\t\t\t},\n\t\t\t\t\t"inch": {\n\t\t\t\t\t\t"height": 16.5417,\n\t\t\t\t\t\t"width": 11.6944\n\t\t\t\t\t}\n\t\t\t\t},\n\t\t\t\t"A4": {\n\t\t\t\t\t"pixel": {\n\t\t\t\t\t\t"width": 595,\n\t\t\t\t\t\t"height": 842\n\t\t\t\t\t},\n\t\t\t\t\t"inch": {\n\t\t\t\t\t\t"height": 11.6944,\n\t\t\t\t\t\t"width": 8.2639\n\t\t\t\t\t}\n\t\t\t\t},\n\t\t\t\t"A5": {\n\t\t\t\t\t"pixel": {\n\t\t\t\t\t\t"width": 420,\n\t\t\t\t\t\t"height": 595\n\t\t\t\t\t},\n\t\t\t\t\t"inch": {\n\t\t\t\t\t\t"height": 8.2639,\n\t\t\t\t\t\t"width": 5.8333\n\t\t\t\t\t}\n\t\t\t\t},\n\t\t\t\t"A6": {\n\t\t\t\t\t"pixel": {\n\t\t\t\t\t\t"width": 298,\n\t\t\t\t\t\t"height": 420\n\t\t\t\t\t},\n\t\t\t\t\t"inch": {\n\t\t\t\t\t\t"height": 5.8333,\n\t\t\t\t\t\t"width": 4.1389\n\t\t\t\t\t}\n\t\t\t\t},\n\t\t\t\t"A7": {\n\t\t\t\t\t"pixel": {\n\t\t\t\t\t\t"width": 210,\n\t\t\t\t\t\t"height": 298\n\t\t\t\t\t},\n\t\t\t\t\t"inch": {\n\t\t\t\t\t\t"height": 4.1389,\n\t\t\t\t\t\t"width": 2.9167\n\t\t\t\t\t}\n\t\t\t\t},\n\t\t\t\t"A8": {\n\t\t\t\t\t"pixel": {\n\t\t\t\t\t\t"width": 147,\n\t\t\t\t\t\t"height": 210\n\t\t\t\t\t},\n\t\t\t\t\t"inch": {\n\t\t\t\t\t\t"height": 2.9167,\n\t\t\t\t\t\t"width": 2.0417\n\t\t\t\t\t}\n\t\t\t\t},\n\t\t\t\t"A9": {\n\t\t\t\t\t"pixel": {\n\t\t\t\t\t\t"width": 105,\n\t\t\t\t\t\t"height": 147\n\t\t\t\t\t},\n\t\t\t\t\t"inch": {\n\t\t\t\t\t\t"height": 2.0417,\n\t\t\t\t\t\t"width": 1.4583\n\t\t\t\t\t}\n\t\t\t\t},\n\t\t\t\t"A10": {\n\t\t\t\t\t"pixel": {\n\t\t\t\t\t\t"width": 74,\n\t\t\t\t\t\t"height": 105\n\t\t\t\t\t},\n\t\t\t\t\t"inch": {\n\t\t\t\t\t\t"height": 1.4583,\n\t\t\t\t\t\t"width": 1.0278\n\t\t\t\t\t}\n\t\t\t\t},\n\t\t\t\t"B0": {\n\t\t\t\t\t"pixel": {\n\t\t\t\t\t\t"width": 2836,\n\t\t\t\t\t\t"height": 4008\n\t\t\t\t\t},\n\t\t\t\t\t"inch": {\n\t\t\t\t\t\t"height": 55.6667,\n\t\t\t\t\t\t"width": 39.3889\n\t\t\t\t\t}\n\t\t\t\t},\n\t\t\t\t"B1": {\n\t\t\t\t\t"pixel": {\n\t\t\t\t\t\t"width": 2004,\n\t\t\t\t\t\t"height": 2836\n\t\t\t\t\t},\n\t\t\t\t\t"inch": {\n\t\t\t\t\t\t"height": 39.3889,\n\t\t\t\t\t\t"width": 27.8333\n\t\t\t\t\t}\n\t\t\t\t},\n\t\t\t\t"B2": {\n\t\t\t\t\t"pixel": {\n\t\t\t\t\t\t"width": 1418,\n\t\t\t\t\t\t"height": 2004\n\t\t\t\t\t},\n\t\t\t\t\t"inch": {\n\t\t\t\t\t\t"height": 27.8333,\n\t\t\t\t\t\t"width": 19.6944\n\t\t\t\t\t}\n\t\t\t\t},\n\t\t\t\t"B3": {\n\t\t\t\t\t"pixel": {\n\t\t\t\t\t\t"width": 1002,\n\t\t\t\t\t\t"height": 1418\n\t\t\t\t\t},\n\t\t\t\t\t"inch": {\n\t\t\t\t\t\t"height": 19.6944,\n\t\t\t\t\t\t"width": 13.9167\n\t\t\t\t\t}\n\t\t\t\t},\n\t\t\t\t"B4": {\n\t\t\t\t\t"pixel": {\n\t\t\t\t\t\t"width": 709,\n\t\t\t\t\t\t"height": 1002\n\t\t\t\t\t},\n\t\t\t\t\t"inch": {\n\t\t\t\t\t\t"height": 13.9167,\n\t\t\t\t\t\t"width": 9.8472\n\t\t\t\t\t}\n\t\t\t\t},\n\t\t\t\t"B5": {\n\t\t\t\t\t"pixel": {\n\t\t\t\t\t\t"width": 501,\n\t\t\t\t\t\t"height": 709\n\t\t\t\t\t},\n\t\t\t\t\t"inch": {\n\t\t\t\t\t\t"height": 9.8472,\n\t\t\t\t\t\t"width": 6.9583\n\t\t\t\t\t}\n\t\t\t\t}\n\t\t\t},\n\t\t\t"whenNoDataType": {\n\t\t\t\t"No Pages" : "NoPages",\n\t\t\t\t"Blank Page" : "BlankPage",\n\t\t\t\t"All Sections No Detail" : "AllSectionsNoDetail",\n\t\t\t\t"No Data Section" : "NoDataSection"\n\t\t\t},\n\t\t\t"printOrder": [\n\t\t\t\t"Vertical",\n\t\t\t\t"Horizontal"\n\t\t\t],\n\t\t\t"orientation": [\n\t\t\t\t"Landscape",\n\t\t\t\t"Portrait"\n\t\t\t],\n\t\t\t"splitType": [\n\t\t\t\t"STRETCH",\n\t\t\t\t"IMMEDIATE",\n\t\t\t\t"PREVENT"\n\t\t\t],\n\t\t\t"footerPosition": [\n\t\t\t\t"Normal",\n\t\t\t\t"StackAtBottom",\n\t\t\t\t"ForceAtBottom",\n\t\t\t\t"CollateAtBottom"\n\t\t\t],\n\t\t\t"mode": [\n\t\t\t\t"Opaque",\n\t\t\t\t"Transparent"\n\t\t\t],\n\t\t\t"fontName": ["Andale Mono","Arial","Arial Black","Comic Sans MS","Courier New","DejaVu Math TeX Gyre","DejaVu Sans","DejaVu Sans Condensed","DejaVu Sans Light","DejaVu Sans Mono","DejaVu Serif","DejaVu Serif Condensed","Dialog","DialogInput","Georgia","Impact","Liberation Mono","Liberation Sans","Liberation Sans Narrow","Liberation Serif","Monospaced","SansSerif","Serif","Times New Roman","Trebuchet MS","Verdana","Webdings"],\n\t\t\t"positionType": [\n\t\t\t\t"FixRelativeToTop",\n\t\t\t\t"FixRelativeToBottom",\n\t\t\t\t"Float"\n\t\t\t],\n\t\t\t"horizontalTextAlign": [\n\t\t\t\t"Left",\n\t\t\t\t"Right",\n\t\t\t\t"Center",\n\t\t\t\t"Justified"\n\t\t\t],\n\t\t\t"verticalTextAlign": [\n\t\t\t\t"Top",\n\t\t\t\t"Middle",\n\t\t\t\t"Bottom",\n\t\t\t\t"Justified"\n\t\t\t],\n\t\t\t"mode": [\n\t\t\t\t"Opaque",\n\t\t\t\t"Transparent"\n\t\t\t],\n\t\t\t"lineSpacing": [\n\t\t\t\t"Single",\n\t\t\t\t"1_1_2",\n\t\t\t\t"Double",\n\t\t\t\t"AtLeast",\n\t\t\t\t"Fixed",\n\t\t\t\t"Proportional"\n\t\t\t],\n\t\t\t"lineStyle": [\n\t\t\t\t"Dotted",\n\t\t\t\t"Double",\n\t\t\t\t"Dashed",\n\t\t\t\t"Solid"\n\t\t\t],\n\t\t\t"linePositionType": [\n\t\t\t\t"FixRelativeToTop",\n\t\t\t\t"FixRelativeToBottom",\n\t\t\t\t"Float"\n\t\t\t],\n\t\t\t"expressionType": [\n\t\t\t\t"simpleText",\n\t\t\t\t"default"\n\t\t\t],\n\t\t\t"stretchType": [\n\t\t\t\t"ContainerBottom",\n\t\t\t\t"ContainerHeight",\n\t\t\t\t"ElementGroupBottom",\n\t\t\t\t"ElementGroupHeight",\n\t\t\t\t"NoStretch"\n\t\t\t],\n\t\t\t"rotationType": [\n\t\t\t\t"Left",\n\t\t\t\t"Right",\n\t\t\t\t"UpsideDown",\n\t\t\t\t"None"\n\t\t\t],\n\t\t\t"lineDirection": [\n\t\t\t\t"BottomUp",\n\t\t\t\t"TopDown"\n\t\t\t],\n\t\t\t"langauge": [\n\t\t\t\t"java",\n\t\t\t\t"groovy",\n\t\t\t\t"javascript"\n\t\t\t],\n\t\t\t"incrementType": [\n\t\t\t\t"None",\n\t\t\t\t"Column",\n\t\t\t\t"Group",\n\t\t\t\t"Report",\n\t\t\t\t"Page"\n\t\t\t],\n\t\t\t"markUp": [\n\t\t\t\t"none",\n\t\t\t\t"styled",\n\t\t\t\t"html",\n\t\t\t\t"rtf"\n\t\t\t],\n\t\t\t"tabStopAlignment": [\n\t\t\t\t"Center",\n\t\t\t\t"Left",\n\t\t\t\t"Right"\n\t\t\t],\n\t\t\t"evaluationTime": [\n\t\t\t\t"Auto",\n\t\t\t\t"Band",\n\t\t\t\t"Column",\n\t\t\t\t"Group",\n\t\t\t\t"Master",\n\t\t\t\t"Now",\n\t\t\t\t"Report",\n\t\t\t\t"Page"\n\t\t\t],\n\t\t\t"calculations": [\n\t\t\t\t"Nothing",\n\t\t\t\t"Count",\n\t\t\t\t"Sum",\n\t\t\t\t"Average",\n\t\t\t\t"Lowest",\n\t\t\t\t"Highest",\n\t\t\t\t"Standard Deviation",\n\t\t\t\t"Variance",\n\t\t\t\t"System",\n\t\t\t\t"First",\n\t\t\t\t"Distinct Count"\n\t\t\t],\n\t\t\t"resetType": [\n\t\t\t\t"Report",\n\t\t\t\t"Page",\n\t\t\t\t"Column",\n\t\t\t\t"Group",\n\t\t\t\t"None",\n\t\t\t\t"Master"\n\t\t\t],\n\t\t\t"fill":["Solid"],\n\t\t\t"scaleImage":["Clip","FillFrame","RetainShape","RealHeight","RealSize"],\n\t\t\t"onErrorType":["Error","Blank","Icon"],\n\t\t\t"horizontalImageAlign":["Left","Center","Right"],\n\t\t\t"verticalImageAlign":["Top","Middle","Bottom"],\n\t\t\t"compositeElements": [],\n\t\t\t"title": {},\n\t\t\t"designerStyles": [],\n\t\t\t"parameters": {\n\t\t\t\t"classNames" : {\n\t\t\t\t\t"String" : "java.lang.String",\n\t\t\t\t\t"Collection" : "java.util.Collection",\n\t\t\t\t\t"Numeric" : "java.lang.Integer"\n\t\t\t\t}\n\t\t\t},\n\t\t\t"fields": [],\n\t\t\t"variables": {\n\t\t\t\t"classNames":{\n\t\t\t\t\t"Double" : "java.lang.Double",\n\t\t\t\t\t"Float" : "java.lang.Float",\n\t\t\t\t\t"Integer" : "java.lang.Integer",\n\t\t\t\t\t"Long" : "java.lang.Long",\n\t\t\t\t\t"Short" : "java.lang.Short",\n\t\t\t\t\t"Big Decimal" : "java.math.BigDecimal",\n\t\t\t\t\t"Time" : "java.sql.Time",\n\t\t\t\t\t"Boolean" : "java.lang.Boolean",\n\t\t\t\t\t"Sql Date" : "java.sql.Date",\n\t\t\t\t\t"Util Date" : "java.util.Date",\n\t\t\t\t\t"Timestamp" : "java.sql.Timestamp",\n\t\t\t\t\t"String" : "java.lang.String"\n\t\t\t\t},\n\t\t\t\t"resetTypes" : {\n\t\t\t\t\t"byPage" : "Page",\n\t\t\t\t\t"byRecord" : "Report",\n\t\t\t\t\t"byColumn" : "Column",\n\t\t\t\t\t"byReport" : "Report"\n\t\t\t\t\t\n\t\t\t\t},\n\t\t\t\t"calculationsFuntionClassMap" : {\n\t\t\t\t\t"Nothing" : "java.lang.Integer",\n\t\t\t\t\t"Count": "java.lang.Integer",\n\t\t\t\t\t"Sum": "java.lang.Integer",\n\t\t\t\t\t"Average": "java.lang.Integer",\n\t\t\t\t\t"Lowest": "java.lang.Integer",\n\t\t\t\t\t"Highest": "java.lang.Integer",\n\t\t\t\t\t"StandardDeviation": "java.lang.Integer",\n\t\t\t\t\t"Variance": "java.lang.Integer",\n\t\t\t\t\t"System": "java.lang.Integer",\n\t\t\t\t\t"First": "java.lang.Integer",\n\t\t\t\t\t"DistinctCount": "java.lang.Integer"\n\t\t\t\t},\n\t\t\t\t"calculationsMapping": {\n\t\t\t\t\t"java.lang.Integer": {\n\t\t\t\t\t\t"Count" : "Count",\n\t\t\t\t\t\t"Sum" : "Sum",\n\t\t\t\t\t\t"Average" : "Average",\n\t\t\t\t\t\t"Lowest" : "Lowest",\n\t\t\t\t\t\t"Highest" : "Highest",\n\t\t\t\t\t\t"Standard Deviation" : "StandardDeviation",\n\t\t\t\t\t\t"Variance" : "Variance",\n\t\t\t\t\t\t"System" : "System",\n\t\t\t\t\t\t"First" : "First",\n\t\t\t\t\t\t"Distinct Count" : "DistinctCount"\n\t\t\t\t\t},\n\t\t\t\t\t"other": {\n\t\t\t\t\t\t"Count" : "Count", \n\t\t\t\t\t\t"Distinct Count" : "DistinctCount"\n\t\t\t\t\t},\n\t\t\t\t\t"allDataTypes" :{\n\t\t\t\t\t\t"Count" : "Count", \n\t\t\t\t\t\t"Sum" : "Sum",\n\t\t\t\t\t\t"Average" : "Average",\n\t\t\t\t\t\t"Lowest" : "Lowest",\n\t\t\t\t\t\t"Highest" : "Highest",\n\t\t\t\t\t\t"Standard Deviation" : "StandardDeviation",\n\t\t\t\t\t\t"Variance" : "Variance",\n\t\t\t\t\t\t"System" : "System",\n\t\t\t\t\t\t"First" : "First",\n\t\t\t\t\t\t"Distinct Count" : "DistinctCount"\n\t\t\t\t\t}\n\t\t\t\t},\n\t\t\t\t"builtInVariables": [\n\t\t\t\t\t{\n\t\t\t\t\t\t"name": "Page_Number",\n\t\t\t\t\t\t"value": "PAGE_NUMBER"\n\t\t\t\t\t},\n\t\t\t\t\t{\n\t\t\t\t\t\t"name": "Column_Number",\n\t\t\t\t\t\t"value": "COLUMN_NUMBER"\n\t\t\t\t\t},\n\t\t\t\t\t{\n\t\t\t\t\t\t"name": "Report_Count",\n\t\t\t\t\t\t"value": "REPORT_COUNT"\n\t\t\t\t\t},\n\t\t\t\t\t{\n\t\t\t\t\t\t"name": "Page_Count",\n\t\t\t\t\t\t"value": "PAGE_COUNT"\n\t\t\t\t\t},\n\t\t\t\t\t{\n\t\t\t\t\t\t"name": "Column_Count",\n\t\t\t\t\t\t"value": "COLUMN_COUNT"\n\t\t\t\t\t},\n\t\t\t\t\t{\n\t\t\t\t\t\t"name": "Current_Date",\n\t\t\t\t\t\t"value": "new java.util.Date()",\n\t\t\t\t\t\t"isExpression": false,\n\t\t\t\t\t\t"properties": {\n\t\t\t\t\t\t\t"pattern": "MMMMM dd, yyyy"\n\t\t\t\t\t\t}\n\t\t\t\t\t},\n\t\t\t\t\t{\n\t\t\t\t\t\t"name": "Current_Time",\n\t\t\t\t\t\t"value": "new java.util.Date()",\n\t\t\t\t\t\t"isExpression": false,\n\t\t\t\t\t\t"properties": {\n\t\t\t\t\t\t\t"pattern": "HH:mm"\n\t\t\t\t\t\t}\n\t\t\t\t\t},\n\t\t\t\t\t{\n\t\t\t\t\t\t"name": "Total_Pages",\n\t\t\t\t\t\t"value": "PAGE_NUMBER",\n\t\t\t\t\t\t"properties": {\n\t\t\t\t\t\t\t"evaluationTime": "Report"\n\t\t\t\t\t\t}\n\t\t\t\t\t}\n\t\t\t\t]\n\t\t\t},\n\t\t\t"pageHeader": [],\n\t\t\t"columnHeader": [],\n\t\t\t"columnFooter": [],\n\t\t\t"details": [],\n\t\t\t"pageFooter": [],\n\t\t\t"summary": {\n\t\t\t\t"someValue": ""\n\t\t\t},\n\t\t\t"pattern": [\n\t\t\t\t"#,##0.###",\n\t\t\t\t"#,##0.##%",\n\t\t\t\t"¤#,##0.##;¤-#,##0.##",\n\t\t\t\t"M/d/yy"\n\t\t\t],\n\t\t\t"pattern_": {\n\t\t\t\t"number": {\n\t\t\t\t\t"-10,023.123": "#,##0.###",\n\t\t\t\t\t"10,023.123-": "#,##0.###;#,##0.###-",\n\t\t\t\t\t"(10,023.123)": "#,##0.###;(#,##0.###)",\n\t\t\t\t\t"(-10,023.123)": "#,##0.###;(-#,##0.###)",\n\t\t\t\t\t"(10,023.123-)": "#,##0.###;(#,##0.###-)"\n\t\t\t\t},\n\t\t\t\t"percentage": {\n\t\t\t\t\t"-1,002,312.35%": "#,##0.##%",\n\t\t\t\t\t"-1,002,312.35‰": "#,##0.##‰",\n\t\t\t\t\t"(1,002,312.35": "#,##0.##%;(#,##0.##",\n\t\t\t\t\t"(-1,002,312.35": "#,##0.##%;(-#,##0.##",\n\t\t\t\t\t"(1,002,312.35": "#,##0.##%;(#,##0.##"\n\t\t\t\t},\n\t\t\t\t"currency": {\n\t\t\t\t\t"$-10,023.12": "¤#,##0.##;¤-#,##0.##",\n\t\t\t\t\t"10,023.12- $": "#,##0.##¤;#,##0.##- ¤",\n\t\t\t\t\t"(10,023.12) $": "#,##0.##¤;(#,##0.##) ¤",\n\t\t\t\t\t"$(-10,023.123)": "¤#,##0.###;¤(-#,##0.###)",\n\t\t\t\t\t"$(10,023.123-)": "¤#,##0.###;¤(#,##0.###-)"\n\t\t\t\t},\n\t\t\t\t"scientific": {\n\t\t\t\t\t"-1.002E4": "0.0##E0"\n\t\t\t\t},\n\t\t\t\t"date": {\n\t\t\t\t\t"12/11/19": "M/d/yy",\n\t\t\t\t\t"Dec 11, 2019": "MMM d, yyyy",\n\t\t\t\t\t"December 11, 2019": "MMMM d, yyyy",\n\t\t\t\t\t"12/11/19 1:58 PM": "M/d/yy h:mm a",\n\t\t\t\t\t"Dec 11, 2019 1:58:05 PM": "MMM d, yyyy h:mm:ss a",\n\t\t\t\t\t"Dec 11, 2019 1:58:05 PM IST": "MMM d, yyyy h:mm:ss a z"\n\t\t\t\t},\n\t\t\t\t"time": {\n\t\t\t\t\t"1:58 PM": "h:mm a",\n\t\t\t\t\t"1:58:05 PM": "h:mm:ss a",\n\t\t\t\t\t"1:58:05 PM IST": "h:mm:ss a z",\n\t\t\t\t\t"13:58 PM": "HH:mm a",\n\t\t\t\t\t"13:58:05 PM": "HH:mm:ss a",\n\t\t\t\t\t"13:58:05 India Standard Time": "HH:mm:ss zzzz"\n\t\t\t\t}\n\t\t\t},\n\t\t\t"parametersInPreview" : {\n\t\t\t\t"position" : ["Right", "Left", "Top", "Bottom"]\n\t\t\t}\n\t\t},\n\t\t"shortcuts":{\n\t\t\t"showPreview" : ["ctrl", "p"],\n\t\t\t"closePreview" : ["Escape"],\n\t\t\t"validateBands" : ["ctrl", "shift", "q"],\n\t\t\t"changePage" : ["ctrl", "shift", "l"],\n\t\t\t"export-pdf":["shift", "p"],\n\t\t\t"export-html":["shift", "l"],\n\t\t\t"export-xls":["shift", "m"],\n\t\t\t"showTutorialPage" : ["ctrl", "shift", "h"]\n\t\t},\n\t\t"errorText" : {\n\t\t\t"queryIsEmpty" : "Please provide query for ",\n\t\t\t"fieldsInCanvasChanged" : "Please check the canvas area as the fields added to canvas may or may not exist as the main data set query changed ",\n\t\t\t"quotesIssueInTextArea" : "Expression might be invalid. Please rectify it ",\n\t\t\t"parameterConfiguration":"There is a problem with parameters configuration ",\n\t\t\t"calulationIncrementGroupError" : "There might be a problem with increment Type for calculation ",\n\t\t\t"calulationResetGroupError" : "There might be a problem with reset type for calculation ",\n\t\t\t"widthInvalid":"Width specified for the component is exceeding the page size. Please check again ",\n\t\t\t"cantCreateCalculation" : "Cannot create calculation on the selected component. Please check again",\n\t\t\t"cantCreateCalculationStaticText" : "Cannot create calculation for the selected text component. Please check again",\n\t\t\t"variableNameNotValid" : "Variable name is not valid, Please rectify it ",\n\t\t\t"colorInvalid" : "Selected color is invalid, Please rectify it",\n\t\t\t"bandHierarchyLineError": "Please check component placement hierarchy for the highlighted Line component(s) ",\n\t\t\t"bandtHierarchyTextError": "Please check component placement hierarchy for the highlighted component(s) ",\n\t\t\t"fieldNameInValidDatasourcePage" : "Field name is invalid, will not be added to datasource pane in elements page. Please check again in ",\n\t\t\t"fieldNameInValidElementsPage" : "Field name is invalid. Please check again in "\n\t\t}\n\t}\n}'
	),
	HcrPreviewFormdataConfig: {
		formData: {
			format: 'html',
			page: '0',
			generateXML: false,
			connectionDetails: {},
			designerProperties: {
				pageWidth: 0,
				pageHeight: 0,
				orientation: 'Portrait',
				columnWidth: 0,
				columnSpacing: 0,
				leftMargin: 0,
				rightMargin: 0,
				topMargin: 0,
				bottomMargin: 0,
				titleNewPage: false,
				floatColumnFooter: false,
				summaryNewPage: false,
				title: {
					bandHeight: 0,
					isImageAttached: 'false',
					staticText: [],
					textField: [],
					image: [],
					lines: []
				},
				pageHeader: {
					bandHeight: 0,
					isImageAttached: 'false',
					staticText: [],
					textField: [],
					image: [],
					lines: []
				},
				details: [
					{
						bandHeight: 0,
						isImageAttached: 'false',
						staticText: [],
						textField: [],
						image: [],
						lines: []
					}
				],
				groups: [],
				pageFooter: {
					bandHeight: 0,
					isImageAttached: 'false',
					staticText: [],
					textField: [],
					image: [],
					lines: []
				},
				summary: {
					bandHeight: 0,
					isImageAttached: 'false',
					staticText: [],
					textField: [],
					image: [],
					lines: []
				},
				noData: {
					bandHeight: 0,
					isImageAttached: 'false',
					staticText: [],
					textField: [],
					image: [],
					lines: []
				},
				lastPageFooter: {
					bandHeight: 0,
					isImageAttached: 'false',
					staticText: [],
					textField: [],
					image: [],
					lines: []
				},
				designerStyles: [],
				parameters: [],
				fields: [],
				variables: [],
				columnHeader: {
					bandHeight: 0,
					isImageAttached: 'false',
					staticText: [],
					textField: [],
					image: [],
					lines: []
				},
				columnFooter: {
					bandHeight: 0,
					isImageAttached: 'false',
					staticText: [],
					textField: [],
					image: [],
					lines: []
				}
			}
		},
		components: {
			page: {
				summaryWithPageHeaderAndFooter: 'summaryWithPageHeaderAndFooter',
				titleNewPage: 'titleNewPage',
				floatColumnFooter: 'floatColumnFooter',
				summaryNewPage: 'summaryNewPage',
				ignorePagination: 'ignorePagination',
				reportName: 'reportName',
				formatFactoryClass: 'formatFactoryClass',
				columnCount: 'columnCount',
				columnSpacing: 'columnSpacing',
				columnWidth: 'columnWidth',
				printOrder: 'printOrder',
				designerStyle: 'designerStyles',
				variables: 'variables',
				whenNoDataType: 'whenNoDataType',
				keepTogether: 'keepTogether',
				startNewColumn: 'startNewColumn',
				startNewPage: 'startNewPage',
				resetPageNumber: 'resetPageNumber',
				reprintHeaderOnEachPage: 'reprintHeaderOnEachPage'
			},
			basicLine: {
				X: 0,
				Y: 0,
				fill: 'true',
				lineWidth: 0,
				lineHeight: 0,
				mode: 'mode',
				penLineWidth: 'penLineWidth',
				lineStyle: 'lineStyle',
				lineBackcolor: 'lineBackcolor',
				lineForecolor: 'lineForecolor',
				linePositionType: 'linePositionType',
				lineDirection: 'lineDirection',
				stretchType: 'stretchType',
				rotationType: 'rotationType',
				printRepeatedValues: 'printRepeatedValues',
				removeLineWhenBlank: 'removeLineWhenBlank',
				printInFirstWholeBand: 'printInFirstWholeBand',
				printWhenDetailOverflows: 'printWhenDetailOverflows',
				evaluationTime: 'evaluationTime',
				styleNameReference: 'styleNameReference'
			},
			textField: {
				X: 0,
				Y: 0,
				textWidth: 0,
				textHeight: 0,
				textFieldExpression: 'textFieldExpression',
				fontName: 'fontName',
				mode: 'mode',
				positionType: 'positionType',
				textFontSize: 'textFontSize',
				bold: 'bold',
				horizontalTextAlign: 'horizontalTextAlign',
				verticalTextAlign: 'verticalTextAlign',
				textBackcolor: 'textBackcolor',
				textForecolor: 'textForecolor',
				italic: 'italic',
				underline: 'underline',
				strikeThrough: 'strikeThrough',
				removeLineWhenBlank: 'removeLineWhenBlank',
				printRepeatedValues: 'printRepeatedValues',
				markUp: 'markUp',
				stretchType: 'stretchType',
				rotationType: 'rotationType',
				printInFirstWholeBand: 'printInFirstWholeBand',
				printWhenDetailOverflows: 'printWhenDetailOverflows',
				configFontName: 'configFontName',
				pattern: 'pattern',
				border: 'border',
				key: 'key',
				styleNameReference: 'styleNameReference',
				printWhenExpression: 'printWhenExpression',
				propertyExpression: 'propertyExpression',
				stretchWithOverFlow: 'stretchWithOverFlow',
				blankWhenNull: 'blankWhenNull',
				paragraph: 'paragraph',
				evaluationTime: 'evaluationTime',
				evaluationGroupName: 'evaluationGroupName',
				patternExpression: 'patternExpression'
			},
			field: {
				X: 0,
				Y: 0,
				textWidth: 0,
				textHeight: 0,
				pattern: 'pattern',
				textFieldExpression: 'textFieldExpression',
				fontName: 'fontName',
				mode: 'mode',
				positionType: 'positionType',
				textFontSize: 'textFontSize',
				bold: 'bold',
				horizontalTextAlign: 'horizontalTextAlign',
				verticalTextAlign: 'verticalTextAlign',
				textBackcolor: 'textBackcolor',
				textForecolor: 'textForecolor',
				italic: 'italic',
				underline: 'underline',
				strikeThrough: 'strikeThrough',
				removeLineWhenBlank: 'removeLineWhenBlank',
				printRepeatedValues: 'printRepeatedValues',
				markUp: 'markUp',
				stretchType: 'stretchType',
				rotationType: 'rotationType',
				printInFirstWholeBand: 'printInFirstWholeBand',
				printWhenDetailOverflows: 'printWhenDetailOverflows',
				border: 'border',
				key: 'key',
				styleNameReference: 'styleNameReference',
				printWhenExpression: 'printWhenExpression',
				propertyExpression: 'propertyExpression',
				stretchWithOverFlow: 'stretchWithOverFlow',
				blankWhenNull: 'blankWhenNull',
				patternExpression: 'patternExpression',
				paragraph: 'paragraph',
				evaluationTime: 'evaluationTime',
				evaluationGroupName: 'evaluationGroupName'
			},
			image: {
				dir: 'dir',
				file: 'file',
				X: 0,
				Y: 0,
				imageWidth: 0,
				imageHeight: 0,
				styleNameReference: 'styleNameReference',
				mode: 'mode',
				imageBackcolor: 'imageBackcolor',
				imageForecolor: 'imageForecolor',
				printRepeatedValues: 'printRepeatedValues',
				removeLineWhenBlank: 'removeLineWhenBlank',
				key: 'key',
				evaluationTime: 'evaluationTime',
				evaluationGroupName: 'evaluationGroupName',
				isLazy: 'isLazy',
				isUsingCache: 'isUsingCache',
				fill: 'fill',
				scaleImage: 'scaleImage',
				onErrorType: 'onErrorType',
				horizontalImageAlign: 'horizontalImageAlign',
				verticalImageAlign: 'verticalImageAlign',
				border: 'border',
				positionType: 'positionType',
				stretchType: 'stretchType',
				printInFirstWholeBand: 'printInFirstWholeBand',
				printWhenExpression: 'printWhenExpression',
				hyperlinkTooltipExpression: 'hyperlinkTooltipExpression',
				printWhenDetailOverflows: 'printWhenDetailOverflows',
				hyperlinkParameterName: 'hyperlinkParameterName',
				hyperlinkParameterExpression: 'hyperlinkParameterExpression'
			}
		}
	},
	HcrPropertiesConfiguration: {
		diagramCanvas: {
			type: 'accordian',
			content: [
				{
					name: 'PAGE SETUP',
					type: 'settingsGroup',
					value: 'pageLayout',
					open: true,
					bootstrap: [ 12, 12, 12, 12 ],
					info: [ 'tooltip_pageSetup' ],
					content: [
						{
							name: 'Page Size',
							type: 'DropDown',
							value: 'pageLayout',
							path: [ 'pageLayoutInfo', 'layoutName' ],
							placeHolder: 'Layout',
							options: [ 'A3', 'A4', 'normal' ],
							bootstrap: [ 6 ],
							callBack: 'pageSizeChange',
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'pageProperties' ],
							staticType: 'keys',
							info: [ 'tooltip_pageSize' ]
						},
						{
							name: 'Orientation',
							type: 'DropDown',
							value: 'Orientation',
							fromStatic: true,
							path: [ 'pageLayoutInfo', 'orientation' ],
							placeHolder: 'Orientation',
							options: [ 'landScape', 'Portrait' ],
							optionsStaticPath: [ 'designerProperties', 'orientation' ],
							bootstrap: [ 6 ],
							callBack: 'pageOrientationChange'
						}
					]
				},
				{
					name: 'MARGIN',
					type: 'settingsGroup',
					value: 'margin',
					bootstrap: [ 12, 12, 12, 12 ],
					content: [
						{
							name: 'Top',
							type: 'numericDropdown',
							value: 'top',
							path: [ 'pageLayoutInfo', 'margin', 'top' ],
							options: '',
							bootstrap: [ 12, 12, 6, 6 ],
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
							path: [ 'pageLayoutInfo', 'margin', 'left' ],
							options: '',
							bootstrap: [ 12, 12, 6, 6 ],
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
							path: [ 'pageLayoutInfo', 'margin', 'bottom' ],
							options: '',
							bootstrap: [ 12, 12, 6, 6 ],
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
							path: [ 'pageLayoutInfo', 'margin', 'right' ],
							options: '',
							bootstrap: [ 12, 12, 6, 6 ],
							callBack: 'pageMarginChange'
						}
					]
				},
				{
					name: 'PAGE PROPERTIES',
					type: 'settingsGroup',
					value: 'report properties',
					open: false,
					bootstrap: [ 12, 12, 12, 12 ],
					content: [
						{
							label: 'Report Name',
							display: false,
							type: 'textBox',
							value: 'reportName',
							placeHolder: 'Enter Name',
							path: [ 'reportName' ],
							bootstrap: [ 12 ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Column Count',
							path: [ 'columnCount' ],
							fromStatic: false,
							bootstrap: [ 6 ],
							type: 'numericDropdown',
							min: 0,
							max: 10,
							quotes: false,
							info: [ 'tooltip_columnCount' ],
							callBack: 'updateStateWithPath'
						},
						{
							name: 'Column Spacing',
							path: [ 'columnSpacing' ],
							fromStatic: false,
							bootstrap: [ 6 ],
							type: 'numericDropdown',
							bounds: 'free',
							quotes: false,
							info: [ 'tooltip_columnSpacing' ],
							callBack: 'updateStateWithPath'
						},
						{
							name: 'Column Width',
							path: [ 'columnWidth' ],
							fromStatic: false,
							bootstrap: [ 6 ],
							type: 'numericDropdown',
							bounds: 'free',
							quotes: false,
							info: [ 'tooltip_columnWidth' ],
							callBack: 'updateStateWithPath'
						},
						{
							name: 'Print Order',
							type: 'DropDown',
							value: 'printOrder',
							fromStatic: true,
							options: [],
							optionsStaticPath: [ 'designerProperties', 'printOrder' ],
							path: [ 'printOrder' ],
							bootstrap: [ 6 ],
							info: [ 'tooltip_printOrder' ],
							callBack: 'updateStateWithPath'
						},
						{
							name: 'When No Data',
							type: 'DropDown',
							value: 'whenNoDataType',
							placeHolder: 'Enter style name',
							fromState: false,
							fromStatic: true,
							extractValues: true,
							staticType: 'keys',
							returnValue: 'value',
							path: [ 'whenNoDataType' ],
							bootstrap: [ 6 ],
							optionsStaticPath: [ 'designerProperties', 'whenNoDataType' ],
							info: [ 'tooltip_whenNoData' ],
							callBack: 'updateStateWithPath'
						},
						{
							label: 'summary with header and footer',
							type: 'checkBox',
							value: 'summaryWithPageHeaderAndFooter',
							path: [ 'summaryWithPageHeaderAndFooter' ],
							bootstrap: [ 12 ],
							info: [ 'tooltip_summaryWithHeaderAndFooter' ],
							title: true,
							className: 'titleName',
							callBack: 'updateStateWithPath'
						},
						{
							label: 'Float column footer',
							type: 'checkBox',
							value: 'floatColumnFooter',
							path: [ 'floatColumnFooter' ],
							bootstrap: [ 12 ],
							info: [ 'tooltip_floatingColumnFooter' ],
							title: true,
							className: 'titleName',
							callBack: 'updateStateWithPath'
						},
						{
							label: 'Title in new page',
							type: 'checkBox',
							value: 'titleNewPage',
							path: [ 'titleNewPage' ],
							bootstrap: [ 12 ],
							info: [ 'tooltip_titleInNewPage' ],
							title: true,
							className: 'titleName',
							callBack: 'updateStateWithPath'
						},
						{
							label: 'Summary in new page',
							type: 'checkBox',
							value: 'summaryNewPage',
							path: [ 'summaryNewPage' ],
							bootstrap: [ 12 ],
							title: true,
							info: [ 'tooltip_summaryInNewPage' ],
							className: 'titleName',
							callBack: 'updateStateWithPath'
						},
						{
							label: 'Ignore pagination',
							type: 'checkBox',
							value: 'ignorePagination',
							path: [ 'ignorePagination' ],
							bootstrap: [ 12 ],
							title: true,
							info: [ 'tooltip_ignorePagination' ],
							className: 'titleName',
							callBack: 'updateStateWithPath'
						}
					]
				},
				{
					name: 'PAGE STYLES',
					type: 'settingsGroup',
					value: 'designerStyles',
					open: false,
					bootstrap: [ 12 ],
					content: [
						{
							name: 'Select Styles',
							type: 'DropDown',
							value: 'selectDesignerStyles',
							fromState: true,
							fromStatic: false,
							extractValues: true,
							keyName: 'name',
							path: [ 'designerStylesNameForEdit' ],
							bootstrap: [ 12 ],
							optionsStatePath: [ 'designerStyles' ],
							callBack: 'handleDesignerStyleForEdit'
						},
						{
							label: 'Style name',
							type: 'textBox',
							value: 'styleName',
							placeHolder: 'Enter style',
							path: [ 'designerStylesEdit', 'name' ],
							info: [ 'tooltip_StyleName' ],
							bootstrap: [ 12 ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Family',
							type: 'DropDown',
							value: 'fontFamily',
							fromStatic: true,
							options: [],
							optionsStaticPath: [ 'designerProperties', 'fontName' ],
							path: [ 'designerStylesEdit', 'fontName' ],
							bootstrap: [ 7 ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Size',
							path: [ 'designerStylesEdit', 'textFontSize' ],
							fromStatic: false,
							bootstrap: [ 5 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleStateChange'
						},
						{
							name: 'Mode',
							type: 'DropDown',
							value: 'mode',
							fromStatic: true,
							options: [],
							optionsStaticPath: [ 'designerProperties', 'mode' ],
							path: [ 'designerStylesEdit', 'mode' ],
							bootstrap: [ 4 ],
							info: [ 'tooltip_mode' ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Fore Color',
							title: 'Fore Color',
							fromStatic: false,
							bootstrap: [ 4 ],
							type: 'colorPicker',
							quotes: true,
							callBack: 'handleStateChange',
							desc: 'pageStylesCommonColor',
							additionalPaths: [
								[ 'designerStylesEdit', 'imageForecolor' ],
								[ 'designerStylesEdit', 'textForecolor' ]
							],
							path: [ 'designerStylesEdit', 'textForecolor' ]
						},
						{
							name: 'Back Color',
							title: 'Back Color',
							fromStatic: false,
							bootstrap: [ 4 ],
							type: 'colorPicker',
							quotes: true,
							callBack: 'handleStateChange',
							desc: 'pageStylesCommonColor',
							additionalPaths: [
								[ 'designerStylesEdit', 'imageBackcolor' ],
								[ 'designerStylesEdit', 'textBackcolor' ]
							],
							path: [ 'designerStylesEdit', 'textBackcolor' ]
						},
						{
							name: 'Styles',
							type: 'iconsButtonGroup',
							controlled: false,
							content: [
								{
									name: 'Bold',
									title: 'Bold',
									iconClass: 'boldIcon',
									path: [ 'designerStylesEdit', 'bold' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleStateChange',
									fromStatic: false,
									type: 'iconButton'
								},
								{
									name: 'Italic',
									title: 'Italic',
									iconClass: 'italicIcon',
									path: [ 'designerStylesEdit', 'italic' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleStateChange',
									fromStatic: false,
									type: 'iconButton'
								},
								{
									name: 'Strike Through',
									title: 'Strike Through',
									iconClass: 'strikeThroughIcon',
									path: [ 'designerStylesEdit', 'strikeThrough' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleStateChange',
									fromStatic: false,
									type: 'iconButton'
								},
								{
									name: 'UnderLine',
									title: 'UnderLine',
									iconClass: 'underLineIcon',
									path: [ 'designerStylesEdit', 'underline' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleStateChange',
									fromStatic: false,
									type: 'iconButton'
								}
							]
						},
						{
							name: 'Alignment',
							type: 'iconsButtonGroup',
							bootStrap: [ 4, 4, 4, 4 ],
							controlled: true,
							content: [
								{
									name: 'left Align',
									title: 'Left Align',
									iconClass: 'leftAlignIcon',
									value: 'Left',
									additionalPaths: [
										[ 'designerStylesEdit', 'horizontalImageAlign' ],
										[ 'designerStylesEdit', 'horizontalTextAlign' ]
									],
									path: [ 'designerStylesEdit', 'horizontalTextAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleStateChange',
									fromStatic: false,
									type: 'iconButton',
									className: 'styleshcrIconButton'
								},
								{
									name: 'Center Align',
									title: 'Center Align',
									iconClass: 'centerAlignIcon',
									value: 'Center',
									path: [ 'designerStylesEdit', 'horizontalTextAlign' ],
									additionalPaths: [
										[ 'designerStylesEdit', 'horizontalImageAlign' ],
										[ 'designerStylesEdit', 'horizontalTextAlign' ]
									],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleStateChange',
									fromStatic: false,
									type: 'iconButton',
									className: 'styleshcrIconButton'
								},
								{
									name: 'Right Align',
									title: 'Right Align',
									value: 'Right',
									iconClass: 'rightAlignIcon',
									path: [ 'designerStylesEdit', 'horizontalTextAlign' ],
									additionalPaths: [
										[ 'designerStylesEdit', 'horizontalImageAlign' ],
										[ 'designerStylesEdit', 'horizontalTextAlign' ]
									],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleStateChange',
									fromStatic: false,
									type: 'iconButton',
									className: 'styleshcrIconButton'
								},
								{
									name: 'Horizontal Justify',
									title: 'Horizontal Justify',
									value: 'Justified',
									iconClass: 'justifyIcon',
									path: [ 'designerStylesEdit', 'horizontalTextAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleStateChange',
									fromStatic: false,
									type: 'iconButton',
									className: 'styleshcrIconButton'
								}
							]
						},
						{
							type: 'iconsButtonGroup',
							controlled: true,
							content: [
								{
									name: 'Top Align',
									title: 'Top Align',
									iconClass: 'topAlign',
									value: 'Top',
									path: [ 'designerStylesEdit', 'verticalTextAlign' ],
									bootstrap: [ 6 ],
									additionalPaths: [
										[ 'designerStylesEdit', 'verticalImageAlign' ],
										[ 'designerStylesEdit', 'verticalTextAlign' ]
									],
									callBack: 'handleStateChange',
									fromStatic: false,
									type: 'iconButton'
								},
								{
									name: 'Center Align',
									title: 'Center Align',
									iconClass: 'centerAlign',
									value: 'Middle',
									path: [ 'designerStylesEdit', 'verticalTextAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									additionalPaths: [
										[ 'designerStylesEdit', 'verticalImageAlign' ],
										[ 'designerStylesEdit', 'verticalTextAlign' ]
									],
									callBack: 'handleStateChange',
									fromStatic: false,
									type: 'iconButton'
								},
								{
									name: 'Bottom Align',
									title: 'Bottom Align',
									value: 'Bottom',
									iconClass: 'bottomAlign',
									path: [ 'designerStylesEdit', 'verticalTextAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									additionalPaths: [
										[ 'designerStylesEdit', 'verticalImageAlign' ],
										[ 'designerStylesEdit', 'verticalTextAlign' ]
									],
									callBack: 'handleStateChange',
									fromStatic: false,
									type: 'iconButton'
								},
								{
									name: 'Vertical Justify',
									title: 'Vertical Justify',
									value: 'Justified',
									iconClass: 'justifyIcon',
									path: [ 'designerStylesEdit', 'verticalTextAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleStateChange',
									fromStatic: false,
									type: 'iconButton'
								}
							]
						},
						{
							name: 'Rotation',
							type: 'DropDown',
							value: 'rotationType',
							fromStatic: true,
							options: [],
							optionsStaticPath: [ 'designerProperties', 'rotationType' ],
							path: [ 'designerStylesEdit', 'rotationType' ],
							bootstrap: [ 6 ],
							info: [ 'tooltip_rotationType' ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Mark up',
							type: 'DropDown',
							value: 'markup',
							fromStatic: true,
							options: [],
							optionsStaticPath: [ 'designerProperties', 'markUp' ],
							path: [ 'designerStylesEdit', 'markup' ],
							bootstrap: [ 6 ],
							info: [ 'tooltip_markUp' ],
							callBack: 'handleStateChange'
						},
						{
							label: 'Blank when null',
							type: 'checkBox',
							value: 'blankWhenNull',
							path: [ 'designerStylesEdit', 'blankWhenNull' ],
							bootstrap: [ 12 ],
							title: true,
							className: 'titleName',
							info: [ 'tooltip_blankWhenNull' ],
							callBack: 'updateStateWithPath'
						},
						{
							label: 'Default style',
							type: 'checkBox',
							value: 'isDefault',
							path: [ 'designerStylesEdit', 'isDefault' ],
							bootstrap: [ 12 ],
							title: true,
							className: 'titleName',
							info: [ 'tooltip_isDefaultStyle' ],
							callBack: 'updateStateWithPath'
						},
						{
							label: 'Pattern',
							type: 'textBox',
							value: 'pattern',
							placeHolder: 'Enter pattern',
							path: [ 'designerStylesEdit', 'pattern' ],
							bootstrap: [ 12 ],
							info: [ 'tooltip_pattern' ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Padding',
							type: 'title',
							bootstrap: [ 12, 12, 12, 12 ]
						},
						{
							name: 'Top',
							title: 'Top',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleStateChange',
							path: [ 'designerStylesEdit', 'border', 'padding', 'topPadding' ]
						},
						{
							name: 'Bottom',
							title: 'Bottom',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleStateChange',
							path: [ 'designerStylesEdit', 'border', 'padding', 'bottomPadding' ]
						},
						{
							name: 'Left',
							title: 'Left',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleStateChange',
							path: [ 'designerStylesEdit', 'border', 'padding', 'leftPadding' ]
						},
						{
							name: 'Right',
							title: 'Right',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleStateChange',
							path: [ 'designerStylesEdit', 'border', 'padding', 'rightPadding' ]
						},
						{
							type: 'seperator'
						},
						{
							name: 'Top',
							type: 'title',
							bootstrap: [ 12, 12, 12, 12 ]
						},
						{
							name: 'Stroke',
							title: 'Top',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleStateChange',
							path: [ 'designerStylesEdit', 'border', 'line', 'topLine', 'lineWidth' ]
						},
						{
							name: 'Color',
							title: 'color',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'colorPicker',
							quotes: false,
							callBack: 'handleStateChange',
							path: [ 'designerStylesEdit', 'border', 'line', 'topLine', 'lineColor' ]
						},
						{
							name: 'Style',
							title: 'Style',
							type: 'DropDown',
							value: 'lineStyle',
							bootstrap: [ 12, 12, 4, 4 ],
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
							path: [ 'designerStylesEdit', 'border', 'line', 'topLine', 'lineStyle' ],
							callBack: 'handleStateChange'
						},
						{
							type: 'seperator'
						},
						{
							name: 'Bottom',
							type: 'title',
							bootstrap: [ 12, 12, 12, 12 ]
						},
						{
							name: 'Stroke',
							title: 'Stroke',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleStateChange',
							path: [ 'designerStylesEdit', 'border', 'line', 'bottomLine', 'lineWidth' ]
						},
						{
							name: 'Color',
							title: 'Color',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'colorPicker',
							quotes: false,
							callBack: 'handleStateChange',
							path: [ 'designerStylesEdit', 'border', 'line', 'bottomLine', 'lineColor' ]
						},
						{
							name: 'Style',
							title: 'Style',
							type: 'DropDown',
							value: 'lineStyle',
							bootstrap: [ 12, 12, 4, 4 ],
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
							path: [ 'designerStylesEdit', 'border', 'line', 'bottomLine', 'lineStyle' ],
							callBack: 'handleStateChange'
						},
						{
							type: 'seperator'
						},
						{
							name: 'Left',
							type: 'title',
							bootstrap: [ 12, 12, 12, 12 ]
						},
						{
							name: 'Stroke',
							title: 'Stroke',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleStateChange',
							path: [ 'designerStylesEdit', 'border', 'line', 'leftLine', 'lineWidth' ]
						},
						{
							name: 'Color',
							title: 'Color',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'colorPicker',
							quotes: false,
							callBack: 'handleStateChange',
							path: [ 'designerStylesEdit', 'border', 'line', 'leftLine', 'lineColor' ]
						},
						{
							name: 'Style',
							title: 'Style',
							type: 'DropDown',
							value: 'lineStyle',
							bootstrap: [ 12, 12, 4, 4 ],
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
							path: [ 'designerStylesEdit', 'border', 'line', 'leftLine', 'lineStyle' ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Right',
							type: 'title',
							bootstrap: [ 12, 12, 12, 12 ]
						},
						{
							name: 'Stroke',
							title: 'Stroke',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleStateChange',
							path: [ 'designerStylesEdit', 'border', 'line', 'rightLine', 'lineWidth' ]
						},
						{
							name: 'Color',
							title: 'Color',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'colorPicker',
							quotes: true,
							callBack: 'handleStateChange',
							path: [ 'designerStylesEdit', 'border', 'line', 'rightLine', 'lineColor' ]
						},
						{
							name: 'Style',
							title: 'Style',
							type: 'DropDown',
							value: 'lineStyle',
							bootstrap: [ 12, 12, 4, 4 ],
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
							path: [ 'designerStylesEdit', 'border', 'line', 'rightLine', 'lineStyle' ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Line Styles',
							type: 'title',
							bootstrap: [ 12 ]
						},
						{
							name: 'Stroke',
							type: 'numericDropdown',
							value: 2,
							fromStatic: false,
							path: [ 'designerStylesEdit', 'lineStyle', 'penLineWidth' ],
							bootstrap: [ 12, 12, 4, 4 ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Color',
							type: 'colorPicker',
							value: 'foreColor',
							fromStatic: true,
							path: [ 'designerStylesEdit', 'lineStyle', 'lineForecolor' ],
							bootstrap: [ 12, 12, 4, 4 ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Style',
							type: 'DropDown',
							value: 'lineStyle',
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
							path: [ 'designerStylesEdit', 'lineStyle', 'lineStyle' ],
							bootstrap: [ 12, 12, 4, 4 ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Delete',
							type: 'button',
							className: '',
							showName: false,
							bootstrap: [ 4 ],
							callBack: 'handleDesignerStyleDelete'
						},
						{
							name: 'Update',
							type: 'button',
							className: '',
							showName: false,
							bootstrap: [ 4 ],
							callBack: 'handleStylesPropertiesUpdate'
						},
						{
							name: 'Add',
							type: 'button',
							className: '',
							showName: false,
							bootstrap: [ 4 ],
							highlight: true,
							when: 'designerStyleEdit',
							callBack: 'handleStylesPropertiesAdd'
						}
					]
				},
				{
					name: 'CALCULATIONS',
					type: 'settingsGroup',
					value: 'variables',
					open: false,
					bootstrap: [ 12 ],
					content: [
						{
							name: 'Select Calculation',
							type: 'DropDown',
							value: 'selectedVariableEdit',
							fromState: true,
							fromStatic: false,
							extractValues: true,
							keyName: 'name',
							path: [ 'variablesEditName' ],
							bootstrap: [ 12 ],
							optionsStatePath: [ 'variables' ],
							callBack: 'handleVariableChangeForEdit'
						},
						{
							label: 'Name',
							type: 'textBox',
							value: 'name',
							placeHolder: 'Enter name',
							path: [ 'variablesForEdit', 'name' ],
							bootstrap: [ 12 ],
							info: [ 'tooltip_Name' ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Class Name',
							type: 'DropDown',
							value: 'className',
							options: [],
							fromStatic: true,
							staticType: 'keys',
							optionsStaticPath: [ 'designerProperties', 'variables', 'classNames' ],
							formatOutput: true,
							formatOptions: {
								display: 'keys',
								value: 'values'
							},
							path: [ 'variablesForEdit', 'className' ],
							bootstrap: [ 6 ],
							info: [ 'tooltip_varClassName' ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Calculation',
							type: 'DropDown',
							value: 'calculation',
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'calculations' ],
							path: [ 'variablesForEdit', 'calculation' ],
							bootstrap: [ 6 ],
							info: [ 'tooltip_varCalculation' ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Reset Type',
							type: 'DropDown',
							value: 'resetType',
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'resetType' ],
							path: [ 'variablesForEdit', 'resetType' ],
							bootstrap: [ 6 ],
							info: [ 'tooltip_varResetType' ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Reset Group',
							type: 'DropDown',
							value: 'ResetGroup',
							fromState: true,
							fromStatic: false,
							extractValues: true,
							keyName: 'name',
							path: [ 'variablesForEdit', 'resetGroup' ],
							bootstrap: [ 6 ],
							info: [ 'tooltip_varResetGroup' ],
							optionsStatePath: [ 'groupBandInfo' ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Increment',
							type: 'DropDown',
							value: 'incrementType',
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'incrementType' ],
							path: [ 'variablesForEdit', 'incrementType' ],
							bootstrap: [ 6 ],
							info: [ 'tooltip_varIncrement' ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Increment Group',
							type: 'DropDown',
							value: 'IncrementGroup',
							fromState: true,
							fromStatic: false,
							extractValues: true,
							keyName: 'name',
							path: [ 'variablesForEdit', 'incrementGroup' ],
							bootstrap: [ 6 ],
							info: [ 'tooltip_varIncrementGroup' ],
							optionsStatePath: [ 'groupBandInfo' ],
							callBack: 'handleStateChange'
						},
						{
							label: 'Expression',
							type: 'textBox',
							value: 'expression',
							placeHolder: 'Enter expression',
							path: [ 'variablesForEdit', 'expression' ],
							bootstrap: [ 12 ],
							info: [ 'tooltip_varExpression' ],
							callBack: 'handleStateChange'
						},
						{
							label: 'Initial Value Expression',
							type: 'textBox',
							value: 'initialValueExpression',
							placeHolder: 'Enter Initial Value',
							path: [ 'variablesForEdit', 'initialValueExpression' ],
							bootstrap: [ 12 ],
							info: [ 'tooltip_varInitialValueExpression' ],
							callBack: 'handleStateChange'
						},
						{
							label: 'Increment Factory ClassName',
							type: 'textBox',
							value: 'incrementFactoryClassName',
							placeHolder: 'Enter Initial Value',
							path: [ 'variablesForEdit', 'incrementFactoryClassName' ],
							bootstrap: [ 12 ],
							info: [ 'tooltip_varIncrementFactoryClassName' ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Clear',
							type: 'button',
							showName: false,
							className: '',
							bootstrap: [ 6 ],
							callBack: 'handleVariableClear'
						},
						{
							name: 'Save',
							type: 'button',
							className: '',
							showName: false,
							bootstrap: [ 6 ],
							highlight: true,
							when: 'calulationEdit',
							callBack: 'handleVariableUpdate'
						}
					]
				},
				{
					name: 'PREVIEW PARAMETERS',
					type: 'settingsGroup',
					value: 'previewParameters',
					open: false,
					bootstrap: [ 12 ],
					content: [
						{
							label: 'Show Parameters',
							type: 'checkBox',
							value: 'showParametersInPreview',
							path: [ 'showParametersInPreview' ],
							bootstrap: [ 12 ],
							title: true,
							className: 'titleName',
							callBack: 'updateStateWithPath'
						},
						{
							name: 'Position',
							type: 'DropDown',
							value: 'Position',
							fromStatic: true,
							path: [ 'parametersInPreview', 'position' ],
							placeHolder: 'Orientation',
							options: [ 'landScape', 'Portrait' ],
							optionsStaticPath: [ 'designerProperties', 'parametersInPreview', 'position' ],
							bootstrap: [ 12, 12, 12, 12 ],
							callBack: 'handleStateChange'
						}
					]
				},
				{
					name: 'GROUP PROPERTIES',
					type: 'settingsGroup',
					value: 'groupParameters',
					open: false,
					bootstrap: [ 12 ],
					content: [
						{
							name: 'Select Group',
							type: 'DropDown',
							value: 'selectGroup',
							fromState: true,
							fromStatic: false,
							extractValues: true,
							keyName: 'name',
							path: [ 'groupPropertiesEditName' ],
							bootstrap: [ 12 ],
							optionsStatePath: [ 'groupBandInfo' ],
							callBack: 'handleGroupPropertiesChange',
							desc: 'selectGroupNameForPropertiesEdit'
						},
						{
							name: 'Minimum height to start new page',
							type: 'numericDropdown',
							value: 'minHeightToStartNewPage',
							path: [ 'groupPropertiesEdit', 'minHeightToStartNewPage' ],
							bootstrap: [ 6 ],
							bounds: 'free',
							callBack: 'handleGroupPropertiesChange',
							info: [ 'tooltip_minHeightToStartNewPage' ],
							desc: 'propertyChanged'
						},
						{
							name: 'Minimum records to start from top',
							type: 'numericDropdown',
							value: 'minDetailsToStartFromTop',
							path: [ 'groupPropertiesEdit', 'minDetailsToStartFromTop' ],
							bootstrap: [ 6 ],
							bounds: 'free',
							callBack: 'handleGroupPropertiesChange',
							info: [ 'tooltip_minDetailsToStartFromTop' ],
							desc: 'propertyChanged'
						},
						{
							label: 'Reprint header on each page',
							type: 'checkBox',
							value: 'reprintHeaderOnEachPage',
							path: [ 'groupPropertiesEdit', 'reprintHeaderOnEachPage' ],
							bootstrap: [ 12 ],
							title: true,
							className: 'titleName',
							callBack: 'handleGroupPropertiesChange',
							info: [ 'tooltip_reprintHeaderOnEachPage' ],
							desc: 'propertyChanged'
						},
						{
							label: 'Keep Together',
							type: 'checkBox',
							value: 'keepTogether',
							path: [ 'groupPropertiesEdit', 'keepTogether' ],
							bootstrap: [ 12 ],
							title: true,
							className: 'titleName',
							info: [ 'tooltip_keepTogether' ],
							callBack: 'handleGroupPropertiesChange',
							desc: 'propertyChanged'
						},
						{
							label: 'Start new column',
							type: 'checkBox',
							value: 'startNewColumn',
							path: [ 'groupPropertiesEdit', 'startNewColumn' ],
							bootstrap: [ 12 ],
							title: true,
							className: 'titleName',
							info: [ 'tooltip_startNewColumn' ],
							callBack: 'handleGroupPropertiesChange',
							desc: 'propertyChanged'
						},
						{
							label: 'Start new page',
							type: 'checkBox',
							value: 'startNewPage',
							path: [ 'groupPropertiesEdit', 'startNewPage' ],
							bootstrap: [ 12 ],
							title: true,
							className: 'titleName',
							info: [ 'tooltip_startNewPage' ],
							callBack: 'handleGroupPropertiesChange',
							desc: 'propertyChanged'
						},
						{
							label: 'Reset page number',
							type: 'checkBox',
							value: 'resetPageNumber',
							path: [ 'groupPropertiesEdit', 'resetPageNumber' ],
							bootstrap: [ 12 ],
							title: true,
							className: 'titleName',
							info: [ 'tooltip_resetPageNumber' ],
							callBack: 'handleGroupPropertiesChange',
							desc: 'propertyChanged'
						},
						{
							label: 'Prevent Orphan Footer',
							type: 'checkBox',
							value: 'preventOrphanFooter',
							path: [ 'groupPropertiesEdit', 'preventOrphanFooter' ],
							info: [ 'tooltip_preventOrphanFooter' ],
							bootstrap: [ 12 ],
							title: true,
							className: 'titleName',
							callBack: 'handleGroupPropertiesChange',
							desc: 'propertyChanged'
						}
					]
				}
			]
		},
		textField: {
			type: 'accordian',
			content: [
				{
					name: 'Typography',
					type: 'settingsGroup',
					value: 'typography',
					open: true,
					bootstrap: [ 12, 12, 12, 12 ],
					content: [
						{
							name: 'Content',
							title: 'Content',
							path: [ 'shapeIds', '$activeShape', 'properties', 'textFieldExpression' ],
							bootstrap: [ 12, 12, 12, 12 ],
							info: [ 'tooltip_content' ],
							callBack: 'handleShapePropertyChanges',
							fromStatic: false,
							type: 'textArea'
						},
						{
							name: 'Fonts',
							bootstrap: [ 12, 12, 12, 12 ],
							type: 'title'
						},
						{
							name: 'Family',
							type: 'DropDown',
							value: 'fontFamily',
							fromStatic: true,
							options: [],
							optionsStaticPath: [ 'designerProperties', 'fontName' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'fontName' ],
							bootstrap: [ 7 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Size',
							path: [ 'shapeIds', '$activeShape', 'properties', 'textFontSize' ],
							fromStatic: false,
							bootstrap: [ 5 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges'
						},
						{
							type: 'seperator'
						},
						{
							name: 'Styles',
							type: 'iconsButtonGroup',
							controlled: false,
							content: [
								{
									name: 'Bold',
									title: 'Bold',
									iconClass: 'boldIcon',
									path: [ 'shapeIds', '$activeShape', 'properties', 'bold' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton'
								},
								{
									name: 'Italic',
									title: 'Italic',
									iconClass: 'italicIcon',
									path: [ 'shapeIds', '$activeShape', 'properties', 'italic' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton'
								},
								{
									name: 'Strike Through',
									title: 'Strike Through',
									iconClass: 'strikeThroughIcon',
									path: [ 'shapeIds', '$activeShape', 'properties', 'strikeThrough' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton'
								},
								{
									name: 'UnderLine',
									title: 'UnderLine',
									iconClass: 'underLineIcon',
									path: [ 'shapeIds', '$activeShape', 'properties', 'underline' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton'
								}
							]
						},
						{
							type: 'seperator'
						},
						{
							name: 'Color',
							type: 'title'
						},
						{
							name: 'Mode',
							type: 'DropDown',
							value: 'mode',
							fromStatic: true,
							options: [],
							optionsStaticPath: [ 'designerProperties', 'mode' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'mode' ],
							info: [ 'tooltip_mode' ],
							bootstrap: [ 12, 12, 4, 4 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Fore Color',
							type: 'colorPicker',
							value: 'textColor',
							fromStatic: true,
							path: [ 'shapeIds', '$activeShape', 'properties', 'textForecolor' ],
							bootstrap: [ 12, 12, 4, 4 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Back Color',
							type: 'colorPicker',
							value: 'backgroundColor',
							fromStatic: true,
							path: [ 'shapeIds', '$activeShape', 'properties', 'textBackcolor' ],
							bootstrap: [ 12, 12, 4, 4 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							type: 'seperator'
						},
						{
							name: 'Alignment',
							type: 'iconsButtonGroup',
							bootStrap: [ 4, 4, 4, 4 ],
							controlled: true,
							content: [
								{
									name: 'left Align',
									title: 'Left Align',
									iconClass: 'leftAlignIcon',
									value: 'Left',
									path: [ 'shapeIds', '$activeShape', 'properties', 'horizontalTextAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton',
									className: 'hcrIconButton'
								},
								{
									name: 'Center Align',
									title: 'Center Align',
									iconClass: 'centerAlignIcon',
									value: 'Center',
									path: [ 'shapeIds', '$activeShape', 'properties', 'horizontalTextAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton',
									className: 'hcrIconButton'
								},
								{
									name: 'Right Align',
									title: 'Right Align',
									value: 'Right',
									iconClass: 'rightAlignIcon',
									path: [ 'shapeIds', '$activeShape', 'properties', 'horizontalTextAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton',
									className: 'hcrIconButton'
								},
								{
									name: 'Horizontal Justify',
									title: 'Horizontal Justify',
									value: 'Justified',
									iconClass: 'hJustifyIcon',
									path: [ 'shapeIds', '$activeShape', 'properties', 'horizontalTextAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton',
									className: 'hcrIconButton'
								}
							]
						},
						{
							type: 'iconsButtonGroup',
							controlled: true,
							content: [
								{
									name: 'Top Align',
									title: 'Top Align',
									iconClass: 'topAlign',
									value: 'Top',
									path: [ 'shapeIds', '$activeShape', 'properties', 'verticalTextAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton',
									className: 'hcrIconButton'
								},
								{
									name: 'Center Align',
									title: 'Center Align',
									iconClass: 'centerAlign',
									value: 'Middle',
									path: [ 'shapeIds', '$activeShape', 'properties', 'verticalTextAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton',
									className: 'hcrIconButton'
								},
								{
									name: 'Bottom Align',
									title: 'Bottom Align',
									value: 'Bottom',
									iconClass: 'bottomAlign',
									path: [ 'shapeIds', '$activeShape', 'properties', 'verticalTextAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton',
									className: 'hcrIconButton'
								},
								{
									name: 'Vertical Justify',
									title: 'Vertical Justify',
									value: 'Justified',
									iconClass: 'vJustifyIcon',
									path: [ 'shapeIds', '$activeShape', 'properties', 'verticalTextAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton',
									className: 'hcrIconButton'
								}
							]
						}
					]
				},
				{
					name: 'Borders',
					type: 'settingsGroup',
					value: 'borders',
					open: false,
					bootstrap: [ 12, 12, 12, 12 ],
					content: [
						{
							label: 'ALL',
							display: false,
							type: 'checkBox',
							value: 'isCommonBorder',
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'properties', 'isCommonBorder' ],
							bootstrap: [ 12 ],
							desc: 'conditionalRender',
							renderingValues: [ true, false ],
							renderingKeys: {
								true: 'contentTrue',
								false: 'contentFalse'
							},
							contentTrue: [
								{
									name: 'Stroke',
									title: 'Top',
									fromStatic: false,
									bootstrap: [ 12, 12, 4, 4 ],
									type: 'numericDropdown',
									quotes: false,
									callBack: 'handleShapePropertyChanges',
									path: [
										'shapeIds',
										'$activeShape',
										'properties',
										'border',
										'line',
										'topLine',
										'lineWidth'
									]
								}
							],
							contentFalse: [
								{
									name: 'content true',
									type: 'title',
									bootstrap: [ 12, 12, 12, 12 ]
								}
							]
						},
						{
							name: 'Top',
							type: 'title',
							bootstrap: [ 12, 12, 12, 12 ]
						},
						{
							name: 'Stroke',
							title: 'Top',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'properties', 'border', 'line', 'topLine', 'lineWidth' ]
						},
						{
							name: 'Style',
							title: 'Style',
							type: 'DropDown',
							value: 'lineStyle',
							bootstrap: [ 12, 12, 4, 4 ],
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'topLine',
								'lineStyle'
							],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Color',
							title: 'color',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'colorPicker',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'properties', 'border', 'line', 'topLine', 'lineColor' ]
						},
						{
							type: 'seperator'
						},
						{
							name: 'Bottom',
							type: 'title',
							bootstrap: [ 12, 12, 12, 12 ]
						},
						{
							name: 'Stroke',
							title: 'Stroke',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'bottomLine',
								'lineWidth'
							]
						},
						{
							name: 'Style',
							title: 'Style',
							type: 'DropDown',
							value: 'lineStyle',
							bootstrap: [ 12, 12, 4, 4 ],
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'bottomLine',
								'lineStyle'
							],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Color',
							title: 'Color',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'colorPicker',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'bottomLine',
								'lineColor'
							]
						},
						{
							type: 'seperator'
						},
						{
							name: 'Left',
							type: 'title',
							bootstrap: [ 12, 12, 12, 12 ]
						},
						{
							name: 'Stroke',
							title: 'Stroke',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'leftLine',
								'lineWidth'
							]
						},
						{
							name: 'Style',
							title: 'Style',
							type: 'DropDown',
							value: 'lineStyle',
							bootstrap: [ 12, 12, 4, 4 ],
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'leftLine',
								'lineStyle'
							],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Color',
							title: 'Color',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'colorPicker',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'leftLine',
								'lineColor'
							]
						},
						{
							type: 'seperator'
						},
						{
							name: 'Right',
							type: 'title',
							bootstrap: [ 12, 12, 12, 12 ]
						},
						{
							name: 'Stroke',
							title: 'Stroke',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'rightLine',
								'lineWidth'
							]
						},
						{
							name: 'Style',
							title: 'Style',
							type: 'DropDown',
							value: 'lineStyle',
							bootstrap: [ 12, 12, 4, 4 ],
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'rightLine',
								'lineStyle'
							],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Color',
							title: 'Color',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'colorPicker',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'rightLine',
								'lineColor'
							]
						}
					]
				},
				{
					name: 'Padding',
					tilte: 'Padding',
					type: 'settingsGroup',
					bootstrap: [ 12, 12, 12, 12 ],
					content: [
						{
							name: 'Top',
							title: 'Top',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'properties', 'border', 'padding', 'topPadding' ]
						},
						{
							name: 'Left',
							title: 'Left',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'properties', 'border', 'padding', 'leftPadding' ]
						},
						{
							name: 'Bottom',
							title: 'Bottom',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'properties', 'border', 'padding', 'bottomPadding' ]
						},
						{
							name: 'Right',
							title: 'Right',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'properties', 'border', 'padding', 'rightPadding' ]
						}
					]
				},
				{
					name: 'Alignment',
					type: 'settingsGroup',
					value: 'alignment',
					open: false,
					bootstrap: [ 12, 12, 12, 12 ],
					content: [
						{
							name: 'X',
							title: 'X',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'offset', 'x' ],
							desc: 'offsetX',
							bounds: 'free',
							event: 'onBlur1'
						},
						{
							name: 'Y',
							title: 'Y',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'offset', 'y' ],
							desc: 'offsetY',
							bounds: 'free',
							event: 'onBlur1'
						},
						{
							type: 'seperator'
						},
						{
							name: 'Height',
							title: 'Height',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'size', 'height' ],
							desc: 'height',
							bounds: 'pageHeight',
							event: 'onBlur1'
						},
						{
							name: 'Width',
							title: 'Width',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'size', 'width' ],
							desc: 'width',
							bounds: 'pageWidth',
							event: 'onBlur1'
						}
					]
				},
				{
					name: 'Text field',
					type: 'settingsGroup',
					value: 'additional',
					open: false,
					bootstrap: [ 12 ],
					content: [
						{
							name: 'Position',
							type: 'DropDown',
							value: 'positionType',
							fromStatic: true,
							options: [],
							optionsStaticPath: [ 'designerProperties', 'positionType' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'positionType' ],
							bootstrap: [ 6 ],
							info: [ 'tooltip_poition' ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Stretch',
							type: 'DropDown',
							value: 'stretchType',
							fromStatic: true,
							options: [],
							optionsStaticPath: [ 'designerProperties', 'stretchType' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'stretchType' ],
							bootstrap: [ 6 ],
							info: [ 'tooltip_stretch' ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Rotation',
							type: 'DropDown',
							value: 'rotationType',
							fromStatic: true,
							options: [],
							optionsStaticPath: [ 'designerProperties', 'rotationType' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'rotationType' ],
							bootstrap: [ 6 ],
							info: [ 'tooltip_rotationType' ],
							desc: 'nodeRotation',
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Mark up',
							type: 'DropDown',
							value: 'rotationType',
							fromStatic: true,
							options: [],
							optionsStaticPath: [ 'designerProperties', 'markUp' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'markUp' ],
							bootstrap: [ 6 ],
							info: [ 'tooltip_markUp' ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Eval Time',
							type: 'DropDown',
							value: 'evaluationTime',
							fromStatic: true,
							options: [],
							optionsStaticPath: [ 'designerProperties', 'evaluationTime' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'evaluationTime' ],
							bootstrap: [ 6 ],
							info: [ 'tooltip_evalTime' ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Eval Group',
							type: 'DropDown',
							value: 'evaluationGroupName',
							fromState: true,
							fromStatic: false,
							options: [],
							extractValues: true,
							keyName: 'name',
							path: [ 'shapeIds', '$activeShape', 'properties', 'evaluationGroupName' ],
							bootstrap: [ 6 ],
							info: [ 'tooltip_evalGroupName' ],
							optionsStatePath: [ 'groupBandInfo' ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Style Name',
							type: 'DropDown',
							value: 'styleNameReference',
							placeHolder: 'Enter style name',
							fromState: true,
							fromStatic: false,
							addNone: true,
							extractValues: true,
							keyName: 'name',
							path: [ 'shapeIds', '$activeShape', 'properties', 'styleNameReference' ],
							bootstrap: [ 6 ],
							optionsStatePath: [ 'designerStyles' ],
							desc: 'addStyleRef',
							info: [ 'tooltip_styleNameReference' ],
							callBack: 'handleStateChange'
						},
						{
							label: 'Pattern',
							type: 'textBox',
							showName: false,
							value: 'pattern',
							placeHolder: 'Enter pattern',
							path: [ 'shapeIds', '$activeShape', 'properties', 'pattern' ],
							bootstrap: [ 12 ],
							info: [ 'tooltip_pattern' ],
							callBack: 'handleStateChange'
						},
						{
							label: 'Pattern expression',
							type: 'textBox',
							value: 'patternExpression',
							placeHolder: 'Enter pattern expression',
							path: [ 'shapeIds', '$activeShape', 'properties', 'patternExpression' ],
							bootstrap: [ 12 ],
							info: [ 'tooltip_patternExpression' ],
							callBack: 'handleStateChange'
						},
						{
							label: 'Print When Expression',
							type: 'textBox',
							value: 'printWhenExpression',
							placeHolder: 'Enter expression',
							path: [ 'shapeIds', '$activeShape', 'properties', 'printWhenExpression' ],
							bootstrap: [ 12 ],
							info: [ 'tooltip_printWhenExpression' ],
							callBack: 'handleStateChange'
						},
						{
							label: 'Print repeated Values',
							type: 'checkBox',
							value: 'printRepeatedValues',
							path: [ 'shapeIds', '$activeShape', 'properties', 'printRepeatedValues' ],
							bootstrap: [ 12 ],
							title: true,
							className: 'titleName',
							info: [ 'tooltip_printRepeatedValues' ],
							callBack: 'updateStateWithPath'
						},
						{
							label: 'Remove Line When Blank',
							type: 'checkBox',
							value: 'removeLineWhenBlank',
							path: [ 'shapeIds', '$activeShape', 'properties', 'removeLineWhenBlank' ],
							bootstrap: [ 12 ],
							title: true,
							className: 'titleName',
							info: [ 'tooltip_removeLineWhenBlank' ],
							callBack: 'updateStateWithPath'
						},
						{
							label: 'Print in first whole section',
							type: 'checkBox',
							value: 'printInFirstWholeBand',
							path: [ 'shapeIds', '$activeShape', 'properties', 'printInFirstWholeBand' ],
							bootstrap: [ 12 ],
							title: true,
							className: 'titleName',
							info: [ 'tooltip_printInFirstWholeBand' ],
							callBack: 'updateStateWithPath'
						},
						{
							label: 'Print When Record Overflows',
							type: 'checkBox',
							value: 'printWhenDetailOverflows',
							path: [ 'shapeIds', '$activeShape', 'properties', 'printWhenDetailOverflows' ],
							bootstrap: [ 12 ],
							info: [ 'tooltip_printWhenDetailOverflows' ],
							title: true,
							className: 'titleName',
							callBack: 'updateStateWithPath'
						},
						{
							label: 'Stretch with overflow',
							type: 'checkBox',
							value: 'stretchWithOverFlow',
							path: [ 'shapeIds', '$activeShape', 'properties', 'stretchWithOverFlow' ],
							bootstrap: [ 12 ],
							title: true,
							className: 'titleName',
							info: [ 'tooltip_stretchWithOverflow' ],
							callBack: 'updateStateWithPath'
						},
						{
							label: 'Blank when null',
							type: 'checkBox',
							value: 'blankWhenNull',
							path: [ 'shapeIds', '$activeShape', 'properties', 'blankWhenNull' ],
							bootstrap: [ 12 ],
							title: true,
							className: 'titleName',
							info: [ 'tooltip_blankWhenNull' ],
							callBack: 'updateStateWithPath'
						}
					]
				},
				{
					name: 'Paragraph',
					type: 'settingsGroup',
					value: 'paragraph',
					open: true,
					display: false,
					bootstrap: [ 12, 12, 12, 12 ],
					content: [
						{
							name: 'Line Spacing',
							type: 'DropDown',
							value: 'lineSpacing',
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'lineSpacing' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'paragraph', 'lineSpacing' ],
							bootstrap: [ 6 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'First line indent',
							type: 'numericDropdown',
							fromStatic: false,
							path: [ 'shapeIds', '$activeShape', 'properties', 'paragraph', 'firstLineIndent' ],
							bootstrap: [ 6 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Left indent',
							type: 'numericDropdown',
							fromStatic: false,
							path: [ 'shapeIds', '$activeShape', 'properties', 'paragraph', 'leftIndent' ],
							bootstrap: [ 6 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Line spacing size',
							type: 'numericDropdown',
							fromStatic: false,
							path: [ 'shapeIds', '$activeShape', 'properties', 'paragraph', 'lineSpacingSize' ],
							bootstrap: [ 6 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Right indent',
							type: 'numericDropdown',
							fromStatic: false,
							path: [ 'shapeIds', '$activeShape', 'properties', 'paragraph', 'rightIndent' ],
							bootstrap: [ 6 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Spacing after',
							type: 'numericDropdown',
							fromStatic: false,
							path: [ 'shapeIds', '$activeShape', 'properties', 'paragraph', 'spacingAfter' ],
							bootstrap: [ 6 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Spacing before',
							type: 'numericDropdown',
							fromStatic: false,
							path: [ 'shapeIds', '$activeShape', 'properties', 'paragraph', 'spacingBefore' ],
							bootstrap: [ 6 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Tab stop width',
							type: 'numericDropdown',
							fromStatic: false,
							path: [ 'shapeIds', '$activeShape', 'properties', 'paragraph', 'tabStopWidth' ],
							bootstrap: [ 6 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Tab stop position',
							type: 'numericDropdown',
							fromStatic: false,
							path: [ 'shapeIds', '$activeShape', 'properties', 'paragraph', 'tabStopPosition' ],
							bootstrap: [ 6 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Tab stop alignment',
							type: 'DropDown',
							value: 'lineSpacing',
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'tabStopAlignment' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'paragraph', 'tabStopAlignment' ],
							bootstrap: [ 6 ],
							callBack: 'handleShapePropertyChanges'
						}
					]
				}
			]
		},
		field: {
			type: 'accordian',
			content: [
				{
					name: 'Typography',
					type: 'settingsGroup',
					value: 'typography',
					open: true,
					bootstrap: [ 12, 12, 12, 12 ],
					content: [
						{
							name: 'Content',
							title: 'Content',
							path: [ 'shapeIds', '$activeShape', 'properties', 'textFieldExpression' ],
							bootstrap: [ 12, 12, 12, 12 ],
							info: [ 'tooltip_content' ],
							callBack: 'handleShapePropertyChanges',
							fromStatic: false,
							type: 'textArea'
						},
						{
							name: 'Fonts',
							bootstrap: [ 12, 12, 12, 12 ],
							type: 'title'
						},
						{
							name: 'Family',
							type: 'DropDown',
							value: 'fontFamily',
							fromStatic: true,
							options: [],
							optionsStaticPath: [ 'designerProperties', 'fontName' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'fontName' ],
							bootstrap: [ 7 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Size',
							path: [ 'shapeIds', '$activeShape', 'properties', 'textFontSize' ],
							fromStatic: false,
							bootstrap: [ 5 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges'
						},
						{
							type: 'seperator'
						},
						{
							name: 'Styles',
							type: 'iconsButtonGroup',
							controlled: false,
							content: [
								{
									name: 'Bold',
									title: 'Bold',
									iconClass: 'boldIcon',
									path: [ 'shapeIds', '$activeShape', 'properties', 'bold' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton'
								},
								{
									name: 'Italic',
									title: 'Italic',
									iconClass: 'italicIcon',
									path: [ 'shapeIds', '$activeShape', 'properties', 'italic' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton'
								},
								{
									name: 'Strike Through',
									title: 'Strike Through',
									iconClass: 'strikeThroughIcon',
									path: [ 'shapeIds', '$activeShape', 'properties', 'strikeThrough' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton'
								},
								{
									name: 'UnderLine',
									title: 'UnderLine',
									iconClass: 'underLineIcon',
									path: [ 'shapeIds', '$activeShape', 'properties', 'underline' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton'
								}
							]
						},
						{
							type: 'seperator'
						},
						{
							name: 'Color',
							type: 'title'
						},
						{
							name: 'Mode',
							type: 'DropDown',
							value: 'mode',
							fromStatic: true,
							options: [],
							optionsStaticPath: [ 'designerProperties', 'mode' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'mode' ],
							info: [ 'tooltip_mode' ],
							bootstrap: [ 12, 12, 4, 4 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Fore Color',
							type: 'colorPicker',
							value: 'textColor',
							fromStatic: true,
							path: [ 'shapeIds', '$activeShape', 'properties', 'textForecolor' ],
							bootstrap: [ 12, 12, 4, 4 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Back Color',
							type: 'colorPicker',
							value: 'backgroundColor',
							fromStatic: true,
							path: [ 'shapeIds', '$activeShape', 'properties', 'textBackcolor' ],
							bootstrap: [ 12, 12, 4, 4 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							type: 'seperator'
						},
						{
							name: 'Alignment',
							type: 'iconsButtonGroup',
							bootStrap: [ 4, 4, 4, 4 ],
							controlled: true,
							content: [
								{
									name: 'left Align',
									title: 'Left Align',
									iconClass: 'leftAlignIcon',
									value: 'Left',
									path: [ 'shapeIds', '$activeShape', 'properties', 'horizontalTextAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton',
									className: 'hcrIconButton'
								},
								{
									name: 'Center Align',
									title: 'Center Align',
									iconClass: 'centerAlignIcon',
									value: 'Center',
									path: [ 'shapeIds', '$activeShape', 'properties', 'horizontalTextAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton',
									className: 'hcrIconButton'
								},
								{
									name: 'Right Align',
									title: 'Right Align',
									value: 'Right',
									iconClass: 'rightAlignIcon',
									path: [ 'shapeIds', '$activeShape', 'properties', 'horizontalTextAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton',
									className: 'hcrIconButton'
								},
								{
									name: 'Horizontal Justify',
									title: 'Horizontal Justify',
									value: 'Justified',
									iconClass: 'hJustifyIcon',
									path: [ 'shapeIds', '$activeShape', 'properties', 'horizontalTextAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton',
									className: 'hcrIconButton'
								}
							]
						},
						{
							type: 'iconsButtonGroup',
							controlled: true,
							content: [
								{
									name: 'Top Align',
									title: 'Top Align',
									iconClass: 'topAlign',
									value: 'Top',
									path: [ 'shapeIds', '$activeShape', 'properties', 'verticalTextAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton',
									className: 'hcrIconButton'
								},
								{
									name: 'Center Align',
									title: 'Center Align',
									iconClass: 'centerAlign',
									value: 'Middle',
									path: [ 'shapeIds', '$activeShape', 'properties', 'verticalTextAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton',
									className: 'hcrIconButton'
								},
								{
									name: 'Bottom Align',
									title: 'Bottom Align',
									value: 'Bottom',
									iconClass: 'bottomAlign',
									path: [ 'shapeIds', '$activeShape', 'properties', 'verticalTextAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton',
									className: 'hcrIconButton'
								},
								{
									name: 'Vertical Justify',
									title: 'Vertical Justify',
									value: 'Justified',
									iconClass: 'vJustifyIcon',
									path: [ 'shapeIds', '$activeShape', 'properties', 'verticalTextAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton',
									className: 'hcrIconButton'
								}
							]
						}
					]
				},
				{
					name: 'Borders',
					type: 'settingsGroup',
					value: 'borders',
					open: false,
					bootstrap: [ 12, 12, 12, 12 ],
					content: [
						{
							label: 'ALL',
							display: false,
							type: 'checkBox',
							value: 'isCommonBorder',
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'properties', 'isCommonBorder' ],
							bootstrap: [ 12 ],
							desc: 'conditionalRender',
							renderingValues: [ true, false ],
							renderingKeys: {
								true: 'contentTrue',
								false: 'contentFalse'
							},
							contentTrue: [
								{
									name: 'Stroke',
									title: 'Top',
									fromStatic: false,
									bootstrap: [ 12, 12, 4, 4 ],
									type: 'numericDropdown',
									quotes: false,
									callBack: 'handleShapePropertyChanges',
									path: [
										'shapeIds',
										'$activeShape',
										'properties',
										'border',
										'line',
										'topLine',
										'lineWidth'
									]
								}
							],
							contentFalse: [
								{
									name: 'content true',
									type: 'title',
									bootstrap: [ 12, 12, 12, 12 ]
								}
							]
						},
						{
							name: 'Top',
							type: 'title',
							bootstrap: [ 12, 12, 12, 12 ]
						},
						{
							name: 'Stroke',
							title: 'Top',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'properties', 'border', 'line', 'topLine', 'lineWidth' ]
						},
						{
							name: 'Style',
							title: 'Style',
							type: 'DropDown',
							value: 'lineStyle',
							bootstrap: [ 12, 12, 4, 4 ],
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'topLine',
								'lineStyle'
							],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Color',
							title: 'color',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'colorPicker',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'properties', 'border', 'line', 'topLine', 'lineColor' ]
						},
						{
							type: 'seperator'
						},
						{
							name: 'Bottom',
							type: 'title',
							bootstrap: [ 12, 12, 12, 12 ]
						},
						{
							name: 'Stroke',
							title: 'Stroke',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'bottomLine',
								'lineWidth'
							]
						},
						{
							name: 'Style',
							title: 'Style',
							type: 'DropDown',
							value: 'lineStyle',
							bootstrap: [ 12, 12, 4, 4 ],
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'bottomLine',
								'lineStyle'
							],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Color',
							title: 'Color',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'colorPicker',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'bottomLine',
								'lineColor'
							]
						},
						{
							type: 'seperator'
						},
						{
							name: 'Left',
							type: 'title',
							bootstrap: [ 12, 12, 12, 12 ]
						},
						{
							name: 'Stroke',
							title: 'Stroke',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'leftLine',
								'lineWidth'
							]
						},
						{
							name: 'Style',
							title: 'Style',
							type: 'DropDown',
							value: 'lineStyle',
							bootstrap: [ 12, 12, 4, 4 ],
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'leftLine',
								'lineStyle'
							],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Color',
							title: 'Color',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'colorPicker',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'leftLine',
								'lineColor'
							]
						},
						{
							type: 'seperator'
						},
						{
							name: 'Right',
							type: 'title',
							bootstrap: [ 12, 12, 12, 12 ]
						},
						{
							name: 'Stroke',
							title: 'Stroke',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'rightLine',
								'lineWidth'
							]
						},
						{
							name: 'Style',
							title: 'Style',
							type: 'DropDown',
							value: 'lineStyle',
							bootstrap: [ 12, 12, 4, 4 ],
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'rightLine',
								'lineStyle'
							],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Color',
							title: 'Color',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'colorPicker',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'rightLine',
								'lineColor'
							]
						}
					]
				},
				{
					name: 'Padding',
					tilte: 'Padding',
					type: 'settingsGroup',
					bootstrap: [ 12, 12, 12, 12 ],
					content: [
						{
							name: 'Top',
							title: 'Top',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'properties', 'border', 'padding', 'topPadding' ]
						},
						{
							name: 'Left',
							title: 'Left',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'properties', 'border', 'padding', 'leftPadding' ]
						},
						{
							name: 'Bottom',
							title: 'Bottom',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'properties', 'border', 'padding', 'bottomPadding' ]
						},
						{
							name: 'Right',
							title: 'Right',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'properties', 'border', 'padding', 'rightPadding' ]
						}
					]
				},
				{
					name: 'Alignment',
					type: 'settingsGroup',
					value: 'alignment',
					open: false,
					bootstrap: [ 12, 12, 12, 12 ],
					content: [
						{
							name: 'X',
							title: 'X',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'offset', 'x' ],
							desc: 'offsetX',
							bounds: 'free',
							event: 'onBlur'
						},
						{
							name: 'Y',
							title: 'Y',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'offset', 'y' ],
							desc: 'offsetY',
							bounds: 'free',
							event: 'onBlur'
						},
						{
							type: 'seperator'
						},
						{
							name: 'Height',
							title: 'Height',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'size', 'height' ],
							desc: 'height',
							bounds: 'pageHeight',
							event: 'onBlur'
						},
						{
							name: 'Width',
							title: 'Width',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'size', 'width' ],
							desc: 'width',
							bounds: 'pageWidth',
							event: 'onBlur'
						}
					]
				},
				{
					name: 'Text field',
					type: 'settingsGroup',
					value: 'additional',
					open: false,
					bootstrap: [ 12 ],
					content: [
						{
							name: 'Position',
							type: 'DropDown',
							value: 'positionType',
							fromStatic: true,
							options: [],
							optionsStaticPath: [ 'designerProperties', 'positionType' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'positionType' ],
							bootstrap: [ 6 ],
							info: [ 'tooltip_poition' ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Stretch',
							type: 'DropDown',
							value: 'stretchType',
							fromStatic: true,
							options: [],
							optionsStaticPath: [ 'designerProperties', 'stretchType' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'stretchType' ],
							bootstrap: [ 6 ],
							info: [ 'tooltip_stretch' ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Rotation',
							type: 'DropDown',
							value: 'rotationType',
							fromStatic: true,
							options: [],
							optionsStaticPath: [ 'designerProperties', 'rotationType' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'rotationType' ],
							bootstrap: [ 6 ],
							info: [ 'tooltip_rotationType' ],
							desc: 'nodeRotation',
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Mark up',
							type: 'DropDown',
							value: 'rotationType',
							fromStatic: true,
							options: [],
							optionsStaticPath: [ 'designerProperties', 'markUp' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'markUp' ],
							bootstrap: [ 6 ],
							info: [ 'tooltip_markUp' ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Eval Time',
							type: 'DropDown',
							value: 'evaluationTime',
							fromStatic: true,
							options: [],
							optionsStaticPath: [ 'designerProperties', 'evaluationTime' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'evaluationTime' ],
							bootstrap: [ 6 ],
							info: [ 'tooltip_evalTime' ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Eval Group',
							type: 'DropDown',
							value: 'evaluationGroupName',
							fromState: true,
							fromStatic: false,
							options: [],
							extractValues: true,
							keyName: 'name',
							path: [ 'shapeIds', '$activeShape', 'properties', 'evaluationGroupName' ],
							bootstrap: [ 6 ],
							info: [ 'tooltip_evalGroupName' ],
							optionsStatePath: [ 'groupBandInfo' ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Style Name',
							type: 'DropDown',
							value: 'styleNameReference',
							placeHolder: 'Enter style name',
							fromState: true,
							addNone: true,
							fromStatic: false,
							extractValues: true,
							keyName: 'name',
							path: [ 'shapeIds', '$activeShape', 'properties', 'styleNameReference' ],
							bootstrap: [ 6 ],
							info: [ 'tooltip_styleNameReference' ],
							optionsStatePath: [ 'designerStyles' ],
							desc: 'addStyleRef',
							callBack: 'handleStateChange'
						},
						{
							label: 'Pattern',
							name: 'Pattern',
							showName: false,
							type: 'textBox',
							value: 'pattern',
							path: [ 'shapeIds', '$activeShape', 'properties', 'pattern' ],
							bootstrap: [ 12 ],
							info: [ 'tooltip_pattern' ],
							callBack: 'handleStateChange'
						},
						{
							label: 'Pattern expression',
							type: 'textBox',
							value: 'patternExpression',
							placeHolder: 'Enter pattern expression',
							path: [ 'shapeIds', '$activeShape', 'properties', 'patternExpression' ],
							bootstrap: [ 12 ],
							info: [ 'tooltip_patternExpression' ],
							callBack: 'handleStateChange'
						},
						{
							label: 'Print When Expression',
							type: 'textBox',
							value: 'printWhenExpression',
							placeHolder: 'Enter expression',
							path: [ 'shapeIds', '$activeShape', 'properties', 'printWhenExpression' ],
							bootstrap: [ 12 ],
							info: [ 'tooltip_printWhenExpression' ],
							callBack: 'handleStateChange'
						},
						{
							label: 'Print repeated Values',
							type: 'checkBox',
							value: 'printRepeatedValues',
							path: [ 'shapeIds', '$activeShape', 'properties', 'printRepeatedValues' ],
							bootstrap: [ 12 ],
							title: true,
							className: 'titleName',
							info: [ 'tooltip_printRepeatedValues' ],
							callBack: 'updateStateWithPath'
						},
						{
							label: 'Remove Line When Blank',
							type: 'checkBox',
							value: 'removeLineWhenBlank',
							path: [ 'shapeIds', '$activeShape', 'properties', 'removeLineWhenBlank' ],
							bootstrap: [ 12 ],
							title: true,
							className: 'titleName',
							info: [ 'tooltip_removeLineWhenBlank' ],
							callBack: 'updateStateWithPath'
						},
						{
							label: 'Print In First Whole Section',
							type: 'checkBox',
							value: 'printInFirstWholeBand',
							path: [ 'shapeIds', '$activeShape', 'properties', 'printInFirstWholeBand' ],
							bootstrap: [ 12 ],
							title: true,
							className: 'titleName',
							info: [ 'tooltip_printInFirstWholeBand' ],
							callBack: 'updateStateWithPath'
						},
						{
							label: 'Print When Record Overflows',
							type: 'checkBox',
							value: 'printWhenDetailOverflows',
							path: [ 'shapeIds', '$activeShape', 'properties', 'printWhenDetailOverflows' ],
							bootstrap: [ 12 ],
							info: [ 'tooltip_printWhenDetailOverflows' ],
							title: true,
							className: 'titleName',
							callBack: 'updateStateWithPath'
						},
						{
							label: 'Stretch with overflow',
							type: 'checkBox',
							value: 'stretchWithOverFlow',
							path: [ 'shapeIds', '$activeShape', 'properties', 'stretchWithOverFlow' ],
							bootstrap: [ 12 ],
							title: true,
							className: 'titleName',
							info: [ 'tooltip_stretchWithOverflow' ],
							callBack: 'updateStateWithPath'
						},
						{
							label: 'Blank when null',
							type: 'checkBox',
							value: 'blankWhenNull',
							path: [ 'shapeIds', '$activeShape', 'properties', 'blankWhenNull' ],
							bootstrap: [ 12 ],
							title: true,
							className: 'titleName',
							info: [ 'tooltip_blankWhenNull' ],
							callBack: 'updateStateWithPath'
						}
					]
				},
				{
					name: 'Paragraph',
					type: 'settingsGroup',
					value: 'paragraph',
					display: false,
					open: true,
					bootstrap: [ 12, 12, 12, 12 ],
					content: [
						{
							name: 'Line Spacing',
							type: 'DropDown',
							value: 'lineSpacing',
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'lineSpacing' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'paragraph', 'lineSpacing' ],
							bootstrap: [ 6 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'First line indent',
							type: 'numericDropdown',
							fromStatic: false,
							path: [ 'shapeIds', '$activeShape', 'properties', 'paragraph', 'firstLineIndent' ],
							bootstrap: [ 6 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Left indent',
							type: 'numericDropdown',
							fromStatic: false,
							path: [ 'shapeIds', '$activeShape', 'properties', 'paragraph', 'leftIndent' ],
							bootstrap: [ 6 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Line spacing size',
							type: 'numericDropdown',
							fromStatic: false,
							path: [ 'shapeIds', '$activeShape', 'properties', 'paragraph', 'lineSpacingSize' ],
							bootstrap: [ 6 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Right indent',
							type: 'numericDropdown',
							fromStatic: false,
							path: [ 'shapeIds', '$activeShape', 'properties', 'paragraph', 'rightIndent' ],
							bootstrap: [ 6 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Spacing after',
							type: 'numericDropdown',
							fromStatic: false,
							path: [ 'shapeIds', '$activeShape', 'properties', 'paragraph', 'spacingAfter' ],
							bootstrap: [ 6 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Spacing before',
							type: 'numericDropdown',
							fromStatic: false,
							path: [ 'shapeIds', '$activeShape', 'properties', 'paragraph', 'spacingBefore' ],
							bootstrap: [ 6 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Tab stop width',
							type: 'numericDropdown',
							fromStatic: false,
							path: [ 'shapeIds', '$activeShape', 'properties', 'paragraph', 'tabStopWidth' ],
							bootstrap: [ 6 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Tab stop position',
							type: 'numericDropdown',
							fromStatic: false,
							path: [ 'shapeIds', '$activeShape', 'properties', 'paragraph', 'tabStopPosition' ],
							bootstrap: [ 6 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Tab stop alignment',
							type: 'DropDown',
							value: 'lineSpacing',
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'tabStopAlignment' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'paragraph', 'tabStopAlignment' ],
							bootstrap: [ 6 ],
							callBack: 'handleShapePropertyChanges'
						}
					]
				}
			]
		},
		basicLine: {
			type: 'accordian',
			content: [
				{
					name: 'Line Styles',
					type: 'settingsGroup',
					open: true,
					content: [
						{
							name: 'Stroke',
							type: 'numericDropdown',
							value: 2,
							fromStatic: false,
							path: [ 'shapeIds', '$activeShape', 'properties', 'penLineWidth' ],
							bootstrap: [ 6 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Style',
							type: 'DropDown',
							value: 'lineStyle',
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'lineStyle' ],
							bootstrap: [ 6 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Direction',
							type: 'DropDown',
							value: 'lineDirection',
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'lineDirection' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'lineDirection' ],
							bootstrap: [ 6 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Style Name',
							type: 'DropDown',
							value: 'styleNameReference',
							placeHolder: 'Enter style name',
							fromState: true,
							fromStatic: false,
							addNone: true,
							extractValues: true,
							keyName: 'name',
							path: [ 'shapeIds', '$activeShape', 'properties', 'styleNameReference' ],
							bootstrap: [ 6 ],
							optionsStatePath: [ 'designerStyles' ],
							desc: 'addStyleRef',
							callBack: 'handleStateChange'
						},
						{
							name: 'Color',
							type: 'title'
						},
						{
							name: 'Line',
							type: 'colorPicker',
							value: 'foreColor',
							fromStatic: true,
							path: [ 'shapeIds', '$activeShape', 'properties', 'lineForecolor' ],
							bootstrap: [ 12, 12, 6, 6 ],
							callBack: 'handleShapePropertyChanges'
						}
					]
				},
				{
					name: 'Alignment',
					type: 'settingsGroup',
					value: 'alignment',
					open: false,
					bootstrap: [ 12, 12, 12, 12 ],
					content: [
						{
							name: 'X',
							title: 'X',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'offset', 'x' ],
							desc: 'offsetX',
							bounds: 'pageX'
						},
						{
							name: 'Y',
							title: 'Y',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'offset', 'y' ],
							desc: 'offsetY',
							bounds: 'pageY'
						},
						{
							name: 'Height',
							title: 'Height',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'size', 'height' ],
							desc: 'height',
							bounds: 'pageHeight'
						},
						{
							name: 'Width',
							title: 'Width',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'size', 'width' ],
							desc: 'width',
							bounds: 'pageWidth'
						}
					]
				},
				{
					name: 'Additional',
					type: 'settingsGroup',
					value: 'additional',
					open: true,
					bootstrap: [ 12 ],
					display: false,
					content: [
						{
							name: 'Stretch',
							type: 'DropDown',
							value: 'stretchType',
							fromStatic: true,
							options: [],
							optionsStaticPath: [ 'designerProperties', 'stretchType' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'stretchType' ],
							bootstrap: [ 12 ],
							info: [ 'tooltip_stretch' ],
							callBack: 'handleStateChange'
						},
						{
							label: 'Print repeated Values',
							type: 'checkBox',
							value: 'printRepeatedValues',
							path: [ 'shapeIds', '$activeShape', 'properties', 'printRepeatedValues' ],
							bootstrap: [ 12 ],
							title: true,
							info: [ 'tooltip_printRepeatedValues' ],
							className: 'titleName',
							callBack: 'updateStateWithPath'
						},
						{
							label: 'Remove Line When Blank',
							type: 'checkBox',
							value: 'removeLineWhenBlank',
							path: [ 'shapeIds', '$activeShape', 'properties', 'removeLineWhenBlank' ],
							bootstrap: [ 12 ],
							title: true,
							className: 'titleName',
							info: [ 'tooltip_removeLineWhenBlank' ],
							callBack: 'updateStateWithPath'
						},
						{
							label: 'Print When Detail Overflows',
							type: 'checkBox',
							value: 'printWhenDetailOverflows',
							path: [ 'shapeIds', '$activeShape', 'properties', 'printWhenDetailOverflows' ],
							bootstrap: [ 12 ],
							title: true,
							info: [ 'tooltip_printWhenDetailOverflows' ],
							className: 'titleName',
							callBack: 'updateStateWithPath'
						}
					]
				}
			]
		},
		image: {
			type: 'accordian',
			content: [
				{
					name: 'Appearence',
					type: 'settingsGroup',
					open: true,
					content: [
						{
							label: 'File name',
							type: 'textBox',
							value: 'file',
							placeHolder: 'Enter file name',
							path: [ 'shapeIds', '$activeShape', 'properties', 'file' ],
							bootstrap: [ 8 ],
							showContentTooltip: true,
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Select',
							type: 'button',
							className: 'imageSelectButton',
							showName: false,
							bootstrap: [ 4 ],
							callBack: 'openFileBrowserToUpload'
						},
						{
							name: 'X',
							title: 'X',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'offset', 'x' ],
							desc: 'offsetX',
							bounds: 'free',
							event: 'onBlur1'
						},
						{
							name: 'Y',
							title: 'Y',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'offset', 'y' ],
							desc: 'offsetY',
							bounds: 'free',
							event: 'onBlur1'
						},
						{
							type: 'seperator'
						},
						{
							name: 'Height',
							title: 'Height',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'size', 'height' ],
							desc: 'height',
							bounds: 'pageHeight',
							event: 'onBlur1'
						},
						{
							name: 'Width',
							title: 'Width',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'size', 'width' ],
							desc: 'width',
							bounds: 'pageWidth',
							event: 'onBlur1'
						},
						{
							name: 'Alignment',
							type: 'title'
						},
						{
							name: 'Vertical',
							type: 'iconsButtonGroup',
							controlled: true,
							bootstrap: [ 6 ],
							content: [
								{
									name: 'Top Align',
									title: 'Top Align',
									iconClass: 'topAlign',
									value: 'Top',
									path: [ 'shapeIds', '$activeShape', 'properties', 'verticalImageAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton',
									className: 'hcrIconButton'
								},
								{
									name: 'Center Align',
									title: 'Center Align',
									iconClass: 'centerAlign',
									value: 'Middle',
									path: [ 'shapeIds', '$activeShape', 'properties', 'verticalImageAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton',
									className: 'hcrIconButton'
								},
								{
									name: 'Bottom Align',
									title: 'Bottom Align',
									value: 'Bottom',
									iconClass: 'bottomAlign',
									path: [ 'shapeIds', '$activeShape', 'properties', 'verticalImageAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton',
									className: 'hcrIconButton'
								}
							]
						},
						{
							name: 'Horizontal',
							type: 'iconsButtonGroup',
							bootstrap: [ 6 ],
							controlled: true,
							content: [
								{
									name: 'left Align',
									title: 'Left Align',
									iconClass: 'leftAlignIcon',
									value: 'Left',
									path: [ 'shapeIds', '$activeShape', 'properties', 'horizontalImageAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton',
									className: 'hcrIconButton'
								},
								{
									name: 'Center Align',
									title: 'Center Align',
									iconClass: 'centerAlignIcon',
									value: 'Center',
									path: [ 'shapeIds', '$activeShape', 'properties', 'horizontalImageAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton',
									className: 'hcrIconButton'
								},
								{
									name: 'Right Align',
									title: 'Right Align',
									value: 'Right',
									iconClass: 'rightAlignIcon',
									path: [ 'shapeIds', '$activeShape', 'properties', 'horizontalImageAlign' ],
									bootstrap: [ 6, 6, 6, 6 ],
									callBack: 'handleShapePropertyChanges',
									fromStatic: false,
									type: 'iconButton',
									className: 'hcrIconButton'
								}
							]
						},
						{
							type: 'title',
							name: 'Color'
						},
						{
							name: 'Mode',
							type: 'DropDown',
							value: 'mode',
							fromStatic: true,
							options: [],
							optionsStaticPath: [ 'designerProperties', 'mode' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'mode' ],
							bootstrap: [ 4 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Fore Color',
							type: 'colorPicker',
							value: 'imageForecolor',
							fromStatic: true,
							path: [ 'shapeIds', '$activeShape', 'properties', 'imageForecolor' ],
							bootstrap: [ 4 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Back Color',
							type: 'colorPicker',
							value: 'imageBackgroundcolor',
							fromStatic: true,
							path: [ 'shapeIds', '$activeShape', 'properties', 'imageBackcolor' ],
							bootstrap: [ 4 ],
							callBack: 'handleShapePropertyChanges'
						}
					]
				},
				{
					name: 'Padding',
					tilte: 'Padding',
					type: 'settingsGroup',
					bootstrap: [ 12, 12, 12, 12 ],
					content: [
						{
							name: 'Top',
							title: 'Top',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'properties', 'border', 'padding', 'topPadding' ]
						},
						{
							name: 'Left',
							title: 'Left',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'properties', 'border', 'padding', 'leftPadding' ]
						},
						{
							name: 'Bottom',
							title: 'Bottom',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'properties', 'border', 'padding', 'bottomPadding' ]
						},
						{
							name: 'Right',
							title: 'Right',
							fromStatic: false,
							bootstrap: [ 12, 12, 6, 6 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'properties', 'border', 'padding', 'rightPadding' ]
						}
					]
				},
				{
					name: 'Borders',
					type: 'settingsGroup',
					value: 'borders',
					open: false,
					bootstrap: [ 12, 12, 12, 12 ],
					content: [
						{
							label: 'ALL',
							display: false,
							type: 'checkBox',
							value: 'isCommonBorder',
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'properties', 'isCommonBorder' ],
							bootstrap: [ 12 ],
							desc: 'conditionalRender',
							renderingValues: [ true, false ],
							renderingKeys: {
								true: 'contentTrue',
								false: 'contentFalse'
							},
							contentTrue: [
								{
									name: 'Stroke',
									title: 'Top',
									fromStatic: false,
									bootstrap: [ 12, 12, 4, 4 ],
									type: 'numericDropdown',
									quotes: false,
									callBack: 'handleShapePropertyChanges',
									path: [
										'shapeIds',
										'$activeShape',
										'properties',
										'border',
										'line',
										'topLine',
										'lineWidth'
									]
								}
							],
							contentFalse: [
								{
									name: 'content true',
									type: 'title',
									bootstrap: [ 12, 12, 12, 12 ]
								}
							]
						},
						{
							name: 'Top',
							type: 'title',
							bootstrap: [ 12, 12, 12, 12 ]
						},
						{
							name: 'Stroke',
							title: 'Top',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'properties', 'border', 'line', 'topLine', 'lineWidth' ]
						},
						{
							name: 'Style',
							title: 'Style',
							type: 'DropDown',
							value: 'lineStyle',
							bootstrap: [ 12, 12, 4, 4 ],
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'topLine',
								'lineStyle'
							],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Color',
							title: 'color',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'colorPicker',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [ 'shapeIds', '$activeShape', 'properties', 'border', 'line', 'topLine', 'lineColor' ]
						},
						{
							type: 'seperator'
						},
						{
							name: 'Left',
							type: 'title',
							bootstrap: [ 12, 12, 12, 12 ]
						},
						{
							name: 'Stroke',
							title: 'Stroke',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'leftLine',
								'lineWidth'
							]
						},
						{
							name: 'Style',
							title: 'Style',
							type: 'DropDown',
							value: 'lineStyle',
							bootstrap: [ 12, 12, 4, 4 ],
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'leftLine',
								'lineStyle'
							],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Color',
							title: 'Color',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'colorPicker',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'leftLine',
								'lineColor'
							]
						},
						{
							type: 'seperator'
						},
						{
							name: 'Bottom',
							type: 'title',
							bootstrap: [ 12, 12, 12, 12 ]
						},
						{
							name: 'Stroke',
							title: 'Stroke',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'bottomLine',
								'lineWidth'
							]
						},
						{
							name: 'Style',
							title: 'Style',
							type: 'DropDown',
							value: 'lineStyle',
							bootstrap: [ 12, 12, 4, 4 ],
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'bottomLine',
								'lineStyle'
							],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Color',
							title: 'Color',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'colorPicker',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'bottomLine',
								'lineColor'
							]
						},
						{
							type: 'seperator'
						},
						{
							name: 'Right',
							type: 'title',
							bootstrap: [ 12, 12, 12, 12 ]
						},
						{
							name: 'Stroke',
							title: 'Stroke',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'numericDropdown',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'rightLine',
								'lineWidth'
							]
						},
						{
							name: 'Style',
							title: 'Style',
							type: 'DropDown',
							value: 'lineStyle',
							bootstrap: [ 12, 12, 4, 4 ],
							options: [],
							fromStatic: true,
							optionsStaticPath: [ 'designerProperties', 'lineStyle' ],
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'rightLine',
								'lineStyle'
							],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'Color',
							title: 'Color',
							fromStatic: false,
							bootstrap: [ 12, 12, 4, 4 ],
							type: 'colorPicker',
							quotes: false,
							callBack: 'handleShapePropertyChanges',
							path: [
								'shapeIds',
								'$activeShape',
								'properties',
								'border',
								'line',
								'rightLine',
								'lineColor'
							]
						}
					]
				},
				{
					name: 'Image',
					type: 'settingsGroup',
					open: true,
					content: [
						{
							name: 'Fill',
							type: 'DropDown',
							value: 'fill',
							fromStatic: true,
							options: [],
							optionsStaticPath: [ 'designerProperties', 'fill' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'fill' ],
							info: [ 'tooltip_fillImage' ],
							bootstrap: [ 6 ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Scale Image',
							type: 'DropDown',
							value: 'scaleImage',
							fromStatic: true,
							options: [],
							optionsStaticPath: [ 'designerProperties', 'scaleImage' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'scaleImage' ],
							info: [ 'tooltip_scaleImage' ],
							bootstrap: [ 6 ],
							callBack: 'handleShapePropertyChanges'
						},
						{
							name: 'On Error Type',
							type: 'DropDown',
							value: 'onErrorType',
							fromStatic: true,
							options: [],
							optionsStaticPath: [ 'designerProperties', 'onErrorType' ],
							path: [ 'shapeIds', '$activeShape', 'properties', 'onErrorType' ],
							info: [ 'tooltip_onErrorType' ],
							bootstrap: [ 6 ],
							callBack: 'handleStateChange'
						},
						{
							name: 'Style Name',
							type: 'DropDown',
							value: 'styleNameReference',
							placeHolder: 'Enter style name',
							fromState: true,
							fromStatic: false,
							addNone: true,
							extractValues: true,
							keyName: 'name',
							path: [ 'shapeIds', '$activeShape', 'properties', 'styleNameReference' ],
							bootstrap: [ 6 ],
							optionsStatePath: [ 'designerStyles' ],
							desc: 'addStyleRef',
							info: [ 'tooltip_styleNameReference' ],
							callBack: 'handleStateChange'
						},
						{
							label: 'Is Lazy',
							type: 'checkBox',
							value: 'printRepeatedValues',
							path: [ 'shapeIds', '$activeShape', 'properties', 'isLazy' ],
							bootstrap: [ 12 ],
							info: [ 'tooltip_isLazy' ],
							title: true,
							className: 'titleName',
							callBack: 'handleStateChange'
						},
						{
							label: 'Is Using Cache',
							type: 'checkBox',
							value: 'printRepeatedValues',
							path: [ 'shapeIds', '$activeShape', 'properties', 'isUsingCache' ],
							bootstrap: [ 12 ],
							info: [ 'tooltip_isUsingCache' ],
							title: true,
							className: 'titleName',
							callBack: 'handleStateChange'
						}
					]
				}
			]
		},
		tooltipInfo: {
			tooltip_pageSetup: {
				show: true,
				onIcon: true,
				isHtml: true,
				htmlId: 'pageSetupTooltip',
				content: [
					{
						type: 'infoDisplay',
						fromStatic: false,
						displayContent: 'page width : $__width and page height : $__height',
						displayValues: {
							width: [ 'pageLayoutInfo', 'size', 'fullPage', 'width' ],
							height: [ 'pageLayoutInfo', 'size', 'fullPage', 'height' ]
						}
					},
					{
						type: 'infoDisplay',
						fromStatic: false,
						displayContent:
							'page margins top : $__top, bottom : $__bottom, left : $__left, right : $__right',
						displayValues: {
							top: [ 'pageLayoutInfo', 'margin', 'top' ],
							bottom: [ 'pageLayoutInfo', 'margin', 'bottom' ],
							left: [ 'pageLayoutInfo', 'margin', 'left' ],
							right: [ 'pageLayoutInfo', 'margin', 'right' ]
						}
					},
					{
						type: 'infoDisplay',
						fromStatic: false,
						displayContent: 'editable page width : $__width and page height : $__height',
						displayValues: {
							width: [ 'pageLayoutInfo', 'size', 'canvasPage', 'width' ],
							height: [ 'pageLayoutInfo', 'size', 'canvasPage', 'height' ]
						}
					}
				]
			},
			tooltip_pageSize: {
				content: [
					{
						displayContent: 'Change the page dimensions'
					}
				]
			},
			tooltip_titleInNewPage: {
				content: [
					{
						displayContent: 'Specify if the title section should be printed on a separate initial page'
					}
				]
			},
			tooltip_summaryInNewPage: {
				content: [
					{
						displayContent: 'Specify if the summary section should be printed on a separate last page'
					}
				]
			},
			tooltip_floatingColumnFooter: {
				content: [
					{
						displayContent:
							'Specify if the column footer section should be printed at the bottom of the column or if it should immediately follow the last detail or group footer printed on the current column'
					}
				]
			},
			tooltip_summaryWithHeaderAndFooter: {
				content: [
					{
						displayContent:
							'Specify if the summary section should be accompained by the page Header and footer'
					}
				]
			},
			tooltip_reprintHeaderOnEachPage: {
				content: [
					{
						displayContent: 'Reprint header on each page'
					}
				]
			},
			tooltip_ignorePagination: {
				content: [
					{
						displayContent: 'Specify whether to use pagination'
					}
				]
			},
			tooltip_printInFirstWholeBand: {
				content: [
					{
						displayContent:
							'The element gets printed in the first section of a new page or column that is not an overflow from a previous page or column.'
					}
				]
			},
			tooltip_printRepeatedValues: {
				content: [
					{
						displayContent:
							'Allows suppressing the repeating values for the dynamic elements such as text field and image fields and to fully customize the bheviour of the static elements like line and static texts'
					}
				]
			},
			tooltip_removeLineWhenBlank: {
				content: [
					{
						displayContent:
							'Collapses the section if the element is not printing and no other element is occupying the same horizontal space.'
					}
				]
			},
			tooltip_pattern: {
				isHtml: true,
				content: [
					{
						displayContent:
							"Pattern to use when formatting the output of the text field expression <br><table style = 'border : 1px solid white'> <tr style = 'font-weight : bold'> <td style = 'border : 1px solid white'>Fromat</td><td style = 'border : 1px solid white'>Pattern</td><td style = 'border : 1px solid white'>Example</td> </tr> <tr> <td style = 'border : 1px solid white'>Numeric </td><td style = 'border : 1px solid white'> #,##0.###</td><td style = 'border : 1px solid white'>1,234.00</td> </tr> <tr> <td style = 'border : 1px solid white'>Date </td><td style = 'border : 1px solid white'> MMM d, yyyy</td><td style = 'border : 1px solid white'>Jan 01, 2020</td> </tr> <tr> <td style = 'border : 1px solid white'>Time </td><td style = 'border : 1px solid white'> h:mm:ss a z</td><td style = 'border : 1px solid white'>4:14:46 PM IST</td> </tr> <tr> <td style = 'border : 1px solid white'>Currency </td><td style = 'border : 1px solid white'> $#,##0</td><td style = 'border : 1px solid white'>$1,234</td> </tr> <tr> <td style = 'border : 1px solid white'>Percentage </td><td style = 'border : 1px solid white'> #,##0.##%</td><td style = 'border : 1px solid white'>84%</td> </tr> <tr> <td style = 'border : 1px solid white'>Scientific Notation </td><td style = 'border : 1px solid white'> 0.0##E0</td><td style = 'border : 1px solid white'>1.001E3</td> </tr> </table>"
					}
				]
			},
			tooltip_columnCount: {
				content: [
					{
						displayContent: 'Specify the number of columns in a page'
					}
				]
			},
			tooltip_columnSpacing: {
				content: [
					{
						displayContent: 'Space between the columns'
					}
				]
			},
			tooltip_columnWidth: {
				content: [
					{
						displayContent: 'Width of each column created'
					}
				]
			},
			tooltip_printOrder: {
				isHtml: true,
				content: [
					{
						displayContent:
							'Vertical - the filling process run first from top to bottom and then from left to right; the first column is entirely filled, then the second one, the third, etc <br> Horizontal - the filling process run first from left to right and then from top to bottom; the first row is filled in any column, then the second row, etc.'
					}
				]
			},
			tooltip_whenNoData: {
				content: [
					{
						displayContent: 'Allows users to customize the behaviour of the report when there is no data'
					}
				]
			},
			tooltip_varResetType: {
				content: [
					{
						displayContent: 'Reset level for variables that perform calculations'
					}
				]
			},
			tooltip_varIncrementFactoryClassName: {
				content: [
					{
						displayContent:
							"The name of a class that implements the net.sf.jasperreports.engine.fill.JRIncrementerFactory interface to use when creating the incrementer instance for this variable. Incrementers are objects that implement thenet.sf.jasperreports.engine.fill.JRIncrementer interface and handle the incremental calculation performed on the variable's current value with every iteration in the data source."
					}
				]
			},
			tooltip_varInitialValueExpression: {
				isHtml: true,
				content: [
					{
						displayContent:
							'Definition of the expression that will be used to calculate the initial value of the variable, before any calculations are made. <br> Example : Suppose to have a variable called variable1 with the expression "new Integer(5)". At every record read will be assign to the variable the integer value 5, so the initial value it isn\'t important in this context. But suppose to change the expression to "$V{variable1}+5", this means that at every iteration the variable is incremented by 5. In this case an initial value it\'s necessary otherwise at the first iteration the variable1 is undefined and this breaks all future evaluations.'
					}
				]
			},
			tooltip_StyleName: {
				content: [
					{
						displayContent: 'Name of the style.'
					}
				]
			},
			tooltip_Name: {
				content: [
					{
						displayContent: 'Name of the calculation.'
					}
				]
			},
			tooltip_varClassName: {
				content: [
					{
						displayContent: 'Class of the calculation values.'
					}
				]
			},
			tooltip_varCalculation: {
				content: [
					{
						displayContent:
							'Calulation to perform on the master report variable when returing the value from the subreport'
					}
				]
			},
			tooltip_varIncrement: {
				content: [
					{
						displayContent: 'Increment level for variables that perform calculations.'
					}
				]
			},
			tooltip_varExpression: {
				isHtml: true,
				content: [
					{
						displayContent:
							'Definition of the expression associated with the calculation.The value of this expression will be calculated at runtime and will represent the value of the corresponding calculation or it will be used in calculation to obtain the value of the calculated variable'
					}
				]
			},
			tooltip_resetPageNumber: {
				content: [
					{
						displayContent: 'Reset page number.'
					}
				]
			},
			tooltip_startNewPage: {
				content: [
					{
						displayContent: 'Start new page for new group'
					}
				]
			},
			tooltip_startNewColumn: {
				content: [
					{
						displayContent: 'Start new column for new group'
					}
				]
			},
			tooltip_keepTogether: {
				content: [
					{
						displayContent: 'Keep together.'
					}
				]
			},
			tooltip_preventOrphanFooter: {
				content: [
					{
						displayContent: 'Prevent Orphan Footer.'
					}
				]
			},
			tooltip_minDetailsToStartFromTop: {
				content: [
					{
						displayContent:
							'Minimum number of details to be rendered on the current column, to avoid starting the group on a new column'
					}
				]
			},
			tooltip_minHeightToStartNewPage: {
				content: [
					{
						displayContent: 'Minimum Height To Start New Page'
					}
				]
			},
			tooltip_poition: {
				content: [
					{
						displayContent: 'Specifies the object position when the report section is affected by stretch.'
					}
				]
			},
			tooltip_stretch: {
				content: [
					{
						displayContent:
							'Specifies the graphic element stretch behavior when the report section is affected by stretch.'
					}
				]
			},
			tooltip_rotationType: {
				content: [
					{
						displayContent: 'Type of the rotation of text object'
					}
				]
			},
			tooltip_markUp: {
				content: [
					{
						displayContent:
							'Name of the mark up language used to embed style information in text content. Supported values are none(Plain text),styled(styled text), rtf(RTF format) and html(HTML format)'
					}
				]
			},
			tooltip_evalTime: {
				isHtml: true,
				content: [
					{
						displayContent:
							'The text to be printed is supplied by the associated expression. This expression can be evaluated at a specified moment. This could be useful for example, when we want to have on the first page a text that will be generated only after fetching all the datasource rows'
					}
				]
			},
			tooltip_styleNameReference: {
				content: [
					{
						displayContent: 'Name of the report level style to use as base style(see <Page styles> element)'
					}
				]
			},
			tooltip_evalGroupName: {
				content: [
					{
						displayContent:
							'Specify group name at which the expression to be executed if the evaluation time is selected as group'
					}
				]
			},
			tooltip_patternExpression: {
				isHtml: true,
				content: [
					{
						displayContent:
							'Definition of Boolean expression that will return a pattern based on the expression <br> Example :$F{travel_cost}.equals(1350)? "#,##0.##%":"#,##0.###"  '
					}
				]
			},
			tooltip_printWhenExpression: {
				isHtml: true,
				content: [
					{
						displayContent:
							'Definition of Boolean expression that will determine if the element should be printed or not <br> Example : (($F{meet_cancellation_status}.equals("Yes")))'
					}
				]
			},
			tooltip_printWhenDetailOverflows: {
				content: [
					{
						displayContent:
							'The element will be printed when the record overflows to a new page or a new column'
					}
				]
			},
			tooltip_stretchWithOverflow: {
				content: [
					{
						displayContent:
							"Allows the text field to stretch downwards in order to display all its text when it doesn't fit in the defined text field height"
					}
				]
			},
			tooltip_blankWhenNull: {
				content: [
					{
						displayContent:
							'Specifies that the text field should display a blank character instead of null when the text field expression evaluates to null'
					}
				]
			},
			tooltip_varResetGroup: {
				content: [
					{
						displayContent:
							'Name of the group at which the variable is reinitialized, when the Reset Type property is set to Group.'
					}
				]
			},
			tooltip_varIncrementGroup: {
				content: [
					{
						displayContent:
							'Name of the group at which the variable is incremented, when the Increment Type property is set to Group.'
					}
				]
			},
			tooltip_isDefaultStyle: {
				content: [
					{
						displayContent:
							'The default report style is used as base style for all the elements that do not explicitly reference a report style definition'
					}
				]
			},
			tooltip_content: {
				isHtml: true,
				content: [
					{
						displayContent:
							'Define the expression to use for this text field <br> Note : Enclose static text in double quotes'
					}
				]
			},
			tooltip_mode: {
				content: [
					{
						displayContent: 'Specifies whether the background of an object is transparent or opaque'
					}
				]
			},
			tooltip_isUsingCache: {
				content: [
					{
						displayContent:
							'If true, tells the report engine to cache the images that are loaded from the same location.'
					}
				]
			},
			tooltip_isLazy: {
				content: [
					{
						displayContent:
							'Gives control over when the images are retrieved from their specified location.'
					}
				]
			},
			tooltip_onErrorType: {
				content: [
					{
						displayContent: 'Controls the behavior of the engine in case the image is not available.'
					}
				]
			},
			tooltip_scaleImage: {
				content: [
					{
						displayContent: 'Image displaying type.'
					}
				]
			},
			tooltip_fillImage: {
				content: [
					{
						displayContent: 'Type of the fill pattern used to fill objects.'
					}
				]
			}
		}
	},
	HcrShapeState: {
		report: {
			summaryWithPageHeaderAndFooter: false,
			showParametersInPreview: true,
			parametersInPreview: {
				position: 'Right'
			},
			titleNewPage: false,
			floatColumnFooter: false,
			summaryNewPage: false,
			ignorePagination: false,
			reportName: '',
			formatFactoryClass: '',
			columnCount: 1,
			columnSpacing: 0,
			columnWidth: 0,
			printOrder: 'Vertical',
			groupPropertiesEditName: '',
			groupPropertiesEditConstant: {
				reprintHeaderOnEachPage: false,
				startNewColumn: false,
				startNewPage: false,
				resetPageNumber: false,
				reprintHeaderOnEachColumn: false,
				keepTogether: false,
				minHeightToStartNewPage: 0,
				minDetailsToStartFromTop: 0,
				preventOrphanFooter: false
			},
			groupPropertiesEdit: {
				reprintHeaderOnEachPage: false,
				startNewColumn: false,
				startNewPage: false,
				resetPageNumber: false,
				reprintHeaderOnEachColumn: false,
				keepTogether: false,
				minHeightToStartNewPage: 0,
				minDetailsToStartFromTop: 0,
				preventOrphanFooter: false
			},
			groupProperties: [],
			designerStylesNameForEdit: '',
			designerStyles: [],
			designerStylesEdit: {
				name: '',
				textBackcolor: '#ffffff',
				textForecolor: '#000000',
				imageBackcolor: '#000000',
				imageForecolor: '#ffffff',
				fontName: 'Aharoni',
				textFontSize: 12,
				bold: false,
				italic: false,
				strikeThrough: false,
				underline: false,
				rotationType: '',
				horizontalTextAlign: 'Center',
				verticalTextAlign: 'Top',
				horizontalImageAlign: 'Center',
				verticalImageAlign: 'Top',
				pattern: '',
				markUp: 'none',
				blankWhenNull: false,
				mode: 'Transparent',
				isDefault: false,
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
							lineStyle: 'Solid'
						},
						rightLine: {
							lineWidth: 0,
							lineColor: '#000000',
							lineStyle: 'Solid'
						},
						bottomLine: {
							lineWidth: 0,
							lineColor: '#000000',
							lineStyle: 'Solid'
						},
						topLine: {
							lineWidth: 0,
							lineColor: '#000000',
							lineStyle: 'Solid'
						}
					}
				},
				lineStyle: {
					lineForecolor: '#000000',
					penLineWidth: 1,
					lineStyle: 'Solid'
				}
			},
			variablesForEdit: {
				name: '',
				className: '',
				calculation: '',
				resetType: '',
				incrementGroup: '',
				resetGroup: '',
				expression: '',
				incrementType: '',
				incrementFactoryClassName: '',
				initialValueExpression: ''
			}
		},
		designerStyles: {
			name: null
		},
		textField: {
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
		},
		field: {
			x: 0,
			y: 0,
			fontName: 'Serif',
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
			mode: 'Transparent',
			printRepeatedValues: true,
			positionType: 'FixRelativeTop',
			textFontSize: 14,
			horizontalTextAlign: 'Center',
			verticalTextAlign: 'Top',
			repeat: true,
			repeatType: 'byRecord',
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
		},
		basicLine: {
			X: 12,
			Y: 2,
			fill: [ true, false ],
			lineWidth: 1,
			lineHeight: 2,
			mode: [ 'Opaque', 'Opaque' ],
			penLineWidth: 2,
			lineStyle: 'Solid',
			lineBackcolor: '#000000',
			lineForecolor: '#000000',
			linePositionType: 'Float',
			lineDirection: 'TopDown',
			stretchType: 'NoStretch',
			positionType: 'FixRelativeTop',
			printRepeatedValues: true,
			removeLineWhenBlank: false,
			printInFirstWholeBand: false,
			printWhenDetailOverflows: false,
			styleNameReference: '',
			repeat: false
		},
		image: {
			X: 0,
			Y: 0,
			dir: '',
			file: '',
			repeat: false,
			imageWidth: 10,
			imageHeight: 10,
			styleNameReference: '',
			mode: 'Transparent',
			imageBackcolor: '#000000',
			imageForecolor: '#ffffff',
			printRepeatedValues: true,
			removeLineWhenBlank: false,
			key: '',
			rotationType: 'None',
			evaluationTime: 'Now',
			evaluationGroupName: '',
			isLazy: false,
			isUsingCache: false,
			fill: 'Solid',
			scaleImage: 'Clip',
			onErrorType: 'Icon',
			horizontalImageAlign: 'Center',
			verticalImageAlign: 'Top',
			positionType: 'FixRelativeTop',
			stretchType: 'NoStretch',
			printInFirstWholeBand: false,
			printWhenExpression: '',
			hyperlinkTooltipExpression: '',
			printWhenDetailOverflows: false,
			hyperlinkParameterName: '',
			hyperlinkParameterExpression: '',
			border: {
				padding: {
					bottomPadding: 0,
					topPadding: 0,
					leftPadding: 0,
					rightPadding: 0,
					padding: 0
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
	},
	exportType: {
		HCR: {
			PDF: {
				icon: '',
				tooltip: '',
				format: 'pdf'
			},
			DOCX: {
				icon: '',
				tooltip: '',
				format: 'docx'
			},
			CSV: {
				icon: '',
				tooltip: '',
				format: 'csv'
			},
			XLSX: {
				icon: '',
				tooltip: '',
				format: 'xlsx'
			},
			XLS: {
				icon: '',
				tooltip: '',
				format: 'xls'
			},
			PNG: {
				icon: '',
				tooltip: '',
				format: 'png'
			},
			JPEG: {
				icon: '',
				tooltip: '',
				format: 'jpeg'
			},
			JPG: {
				icon: '',
				tooltip: '',
				format: 'jpg'
			},
			XML: {
				icon: '',
				tooltip: '',
				format: 'xml'
			},
			HTML: {
				icon: '',
				tooltip: '',
				format: 'html'
			},
			RTF: {
				icon: '',
				tooltip: '',
				format: 'rtf'
			},
			ODT: {
				icon: '',
				tooltip: '',
				format: 'odt'
			},
			ODS: {
				icon: '',
				tooltip: '',
				format: 'ods'
			},
			PPTX: {
				icon: '',
				tooltip: '',
				format: 'pptx'
			},
			TXT: {
				icon: '',
				tooltip: '',
				format: 'txt'
			}
		}
	},
	allTypes: {
		sqlTypes: [
			{
				name: 'sql'
			},
			{
				name: 'sql.groovy'
			},
			{
				name: 'sql.adhoc'
			}
		],
		vizTypes: [
			{
				name: 'Area',
				type: 'EFW-c3',
				subtype: 'Area',
				icon: 'AreaChart'
			},
			{
				name: 'Area Spline',
				type: 'EFW-c3',
				subtype: 'AreaSpline',
				icon: 'AreaSplineChart'
			},
			{
				name: 'Area Step',
				type: 'EFW-c3',
				subtype: 'AreaStep',
				icon: 'AreaStepChart'
			},
			{
				name: 'Bar',
				type: 'EFW-c3',
				subtype: 'bar',
				icon: 'BarChart'
			},
			{
				name: 'Donut',
				type: 'EFW-c3',
				subtype: 'donut',
				icon: 'DonutChart'
			},
			{
				name: 'Gauge',
				type: 'EFW-c3',
				subtype: 'gauge',
				icon: 'HICircularGauge'
			},
			{
				name: 'Line',
				type: 'EFW-c3',
				subtype: 'Line',
				icon: 'LineChart'
			},
			{
				name: 'Pie',
				type: 'EFW-c3',
				subtype: 'Pie',
				icon: 'PieChart'
			},
			{
				name: 'Scatter',
				type: 'EFW-c3',
				subtype: 'Scatter',
				icon: 'ScatterChart'
			},
			{
				name: 'Spline',
				type: 'EFW-c3',
				subtype: 'Spline',
				icon: 'SplineChart'
			},
			{
				name: 'Step',
				type: 'EFW-c3',
				subtype: 'Step',
				icon: 'StepChart'
			},
			{
				name: 'Cross Tab',
				type: 'EFW-CrossTab',
				icon: 'HICrossTable'
			},
			{
				name: 'Table',
				type: 'EFW-Table',
				icon: 'HITable'
			},
			{
				name: 'Custom',
				type: 'Custom',
				icon: 'VF'
			}
		],
		connTypes: [
			{
				clazz: 'com.helicalinsight.datasource.GlobalJdbcDataSource',
				classifier: 'global',
				name: 'Managed DataSource',
				type: 'global.jdbc',
				hidden: 'false'
			},
			{
				clazz: 'com.helicalinsight.datasource.JDBCDriver',
				classifier: 'efwd',
				name: 'Plain Jdbc DataSource',
				type: 'sql.jdbc',
				hidden: 'false'
			},
			{
				clazz: 'com.helicalinsight.adhoc.SqlAdhocDriver',
				classifier: 'efwd',
				name: 'Adhoc DataSource',
				type: 'sql.adhoc',
				hidden: 'true'
			},
			{
				clazz: 'com.helicalinsight.datasource.ExtJDBCDriver',
				classifier: 'efwd',
				name: 'Groovy Plain Jdbc DataSource',
				type: 'sql.jdbc.groovy',
				hidden: 'false'
			},
			{
				clazz: 'com.helicalinsight.datasource.GroovyManagedDriver',
				classifier: 'efwd',
				name: 'Groovy Managed Jdbc DataSource',
				type: 'sql.jdbc.groovy.managed',
				hidden: 'false'
			}
		],
		parameterTypes: [
			{
				name: 'Collection'
			},
			{
				name: 'Numeric'
			},
			{
				name: 'String'
			}
		]
	}
};
export const items = [
	{
		key: 'D-Page Size',
		label: 'D-Page Size',
		selectedKey: 'A4',
		value: [
			{ key: 'A0', label: 'A0' },
			{ key: 'A1', label: 'A1' },
			{ key: 'A10', label: 'A10' },
			{ key: 'A2', label: 'A2' },
			{ key: 'A3', label: 'A3' },
			{ key: 'A4', label: 'A4' },
			{ key: 'A5', label: 'A5' },
			{ key: 'A6', label: 'A6' },
			{ key: 'A7', label: 'A7' },
			{ key: 'A8', label: 'A8' },
			{ key: 'A9', label: 'A9' }
		],
		groupId: 'D-Page Setup',
		elementType: 'Select'
	},
	{
		key: 'D-Orientation',
		label: 'D-Orientation',
		selectedKey: 'Portrait',
		value: [ { key: 'Landscape', label: 'Landscape' }, { key: 'Portrait', label: 'Portrait' } ],
		groupId: 'D-Page Setup',
		elementType: 'Select'
	}
];
