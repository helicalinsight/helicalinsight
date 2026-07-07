import { useEffect, useMemo, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { v4 as uuidv4 } from "uuid";
import { setKeysPressed, setShotCutCurrentLocation } from "../../../../../redux/actions";
import { addCustomChartColorPalette, addFieldToCanvas, updateReportProperty, updateTrendField } from "../../../../../redux/actions/hreport.actions";
import { getCanvasFieldDataType } from "../../../../../utils/filter-utils";
import { HIPropertyPane } from "../../../../common";
import ShortCutText from "../../../../common/hi-shortcuts/hi-shortcuts";
import { generateReport } from "../../../utils/base";
import { isMatchingShortcut, isMatchingShortcutWithShiftKey } from "../../../utils/utilities";
import { getAppliedUpdatedProperties, getCustomFieldDataType, getDataTypeValues, getDefaultAFDataTypeValues, getDefaultValue, getFieldData, getInfoItems, getPropertyItems } from "../../utils/property-utils";
import types from "../../../../../constants/metadata";
import { Button, Col, Drawer, Row, Space, Tooltip } from "antd";
import { CloseOutlined, InfoCircleOutlined } from "@ant-design/icons";
import { formats, toolbarOptions } from "../../../../common/hi-property-pane/utils/react-quill-configurations";
import ReactQuill, { Quill } from "react-quill";
import "react-quill/dist/quill.snow.css";
import { getFieldDisplayName } from "../../../../../utils/utilities";
import TooltipTemplateEditor from "./tooltip-template-editor";


const getLabelForHreportGroup = ({ text, label }) => {
  return <ShortCutText scLocation="HR PR" {...{ text, menuItem: true }}>{label}</ShortCutText>;
};

export const propertyPaneGroupItems = [
  {
    label: getLabelForHreportGroup({ label: "Title", text: "T" }),
    key: "title",
  },
  {
    label: getLabelForHreportGroup({ label: "Sub Title", text: "U" }),
    key: "subTitle",
  },
  {
    label: getLabelForHreportGroup({ label: "Format", text: "F" }),
    key: "format",
  },
  {
    label: getLabelForHreportGroup({ label: "Cache", text: "E" }),
    key: "cache",
  },
  {
    label: getLabelForHreportGroup({ label: "Color", text: "C" }),
    key: "color",
  },
  {
    label: getLabelForHreportGroup({ label: "Bar", text: "B" }),
    key: "bar",
  },
  {
    label: getLabelForHreportGroup({ label: "Radial", text: "I" }),
    key: "radial",
  },
  {
    label: getLabelForHreportGroup({ label: "Legend", text: "L" }),
    key: "legend",
  },
  {
    label: getLabelForHreportGroup({ label: "Axis", text: "X" }),
    key: "axisRange",
  },
  {
    label: getLabelForHreportGroup({ label: "Card", text: "D" }),
    key: "card",
  },
  {
    label: getLabelForHreportGroup({ label: "Labels", text: "O" }),
    key: "labels",
  },
  {
    label: getLabelForHreportGroup({ label: "Grid Table", text: "Y" }),
    key: "crosstab",
  },
  {
    label: getLabelForHreportGroup({ label: "Table", text: "Z" }),
    key: "table",
  },
  {
    label: getLabelForHreportGroup({ label: "Map", text: "M" }),
    key: "map",
  },
  {
    label: getLabelForHreportGroup({ label: "Line", text: "N" }),
    key: "line",
  },
  {
    label: getLabelForHreportGroup({ label: "Progress", text: "P" }),
    key: "progress",
  },
  {
    label: getLabelForHreportGroup({ label: "Radar", text: "G" }),
    key: "radar",
  },
  {
    label: getLabelForHreportGroup({ label: "Shape", text: "H" }),
    key: "shape",
  },
  {
    label: getLabelForHreportGroup({ label: "Color Palette", text: "J" }),
    key: "chartTheme",
  },
  {
    label: getLabelForHreportGroup({ label: "Charts", text: "K" }),
    key: "charts",
  },
  {
    label: getLabelForHreportGroup({ label: "Relation", text: "V" }),
    key: "relationChart",
  },
  {
    label: getLabelForHreportGroup({ label: "Canvas", text: "Q" }),
    key: "canvas",
  },
  {
    label: getLabelForHreportGroup({ label: "Filter", text: "Shft + F" }),
    key: "filter",
  },
  {
    label: getLabelForHreportGroup({ label: "Tooltip", text: "Shft + T" }),
    key: "tooltip",
  },
];

export function sortPropertyPane(groupItems) {
  return groupItems.sort((a, b) => a.label.props.children.localeCompare(b.label.props.children))
}

const intialGenerateReportProp = {
  table: {
    changed: false,
    fetchAllRecords: false
  }
}

const HrProperties = (props) => {
  const dispatch = useDispatch();
  const applyRef = useRef()
  const resetRef = useRef()
  const searchRef = useRef()
  const quillRef = useRef(null);

  const generateReportPropRef = useRef(intialGenerateReportProp)
  const [propertyItems, setPropertyItems] = useState([]);
  const [groupId, setGroupId] = useState("title");
  const [showAllColorFields, setShowAllColorFields] = useState(false);
  const [showAllFormatFields, setShowAllFormatFields] = useState(false);
  const [formatColorStyle, setFormatFormatColorStyle] = useState("");
  const [formatColorField, setFormatColorField] = useState("");
  const [formatColorEnableSteps, setFormatColorEnableSteps] = useState(false);
  const [formatColorSteps, setFormatColorSteps] = useState(10);
  const [formatColorEnableReverse, setFormatColorEnableReverse] = useState(false);
  const [formatMinColor, setFormatMinColor] = useState({ r: 183, g: 192, b: 232, a: 1 });
  const [formatMaxColor, setFormatMaxColor] = useState({ r: 84, g: 108, b: 230, a: 1 });
  const [formatColorMinValue, setFormatColorMinValue] = useState(0)
  const [formatColorMaxValue, setFormatColorMaxValue] = useState(0)
  const [formatColorCenterValue, setFormatColorCenterValue] = useState(0)
  const [enableAdvanceSteps, setEnableAdvanceSteps] = useState(false)

  const [isPercentageEnabled, setIsPercentageEnabled] = useState(false)
  const [spaceBeforeDisplayUnits, setSpaceBeforeDisplayUnits] = useState(false)
  const [fieldId, setFieldId] = useState("");
  const [formatDatatype, setFormatDatatype] = useState("");
  const [gridLines, setGridLines] = useState([])
  const [showAxisName, setShowAxisName] = useState(false)
  const [showGridChartAxisName, setShowGridChartAxisName] = useState(true)
  const [synchronize, setSynchronize] = useState(false)
  const [recordsPerPage, setRecordsPerPage] = useState(10)
  const [horizontalScroll, setHorizontalScroll] = useState(false)
  const [fetchAllRecords, setFetchAllRecords] = useState(false)
  const [smooth, setSmooth] = useState(false);
  const [mapStyle, setMapStyle] = useState('normal')
  // const [mapType, setMapType] = useState('normal')
  const [longitude, setLongitude] = useState(0)
  const [latitude, setLatitude] = useState(0)
  const [mapZoom, setMapZoom] = useState(2)
  const [axisRangeId, setAxisRangeId] = useState("");
  const [axisRangeDataType, setAxisRangeDatatype] = useState("");
  const [cardTitle, setCardTitle] = useState("");
  const [cardPrefixType, setCardPrefixType] = useState("");
  const [cardSuffixType, setCardSuffixType] = useState("");
  const [cardPrefixColor, setCardPrefixColor] = useState("");
  const [cardSuffixColor, setCardSuffixColor] = useState("");
  const [cardPrefix, setCardPrefix] = useState("");
  const [cardSuffix, setCardSuffix] = useState("");
  const [key, setKey] = useState(new Date());
  const [isProgressTargetStatic, setProgressTargetStatic] = useState(true);
  const [progressTarget, setProgressTarget] = useState(0);
  const [progressChartType, setProgressChartType] = useState('');
  const [showTargetAndValue, setShowTargetAndValue] = useState(false);
  const [statisticType, setstatisticType] = useState('');
  // const [progressRange, setProgressRange] = useState(5);
  const [progressRangeColors, setProgressRangeColors] = useState({})

  const [doughnutPrefixType, setDoughnutPrefixType] = useState("");
  const [doughnutSuffixType, setDoughnutSuffixType] = useState("");
  const [doughnutPrefix, setDoughnutPrefix] = useState("");
  const [doughnutSuffix, setDoughnutSuffix] = useState("");
  const [showDoughnutTitle, setShowDoughnutTitle] = useState("");
  const [doughnutTitle, setDoughnutTitle] = useState("");
  const [radarChartType, setRadarChartType] = useState('');

  const [mapDefaultShape, setMapDefaultShape] = useState('circle');
  const [mapShowAllShapes, setMapShowAllShapes] = useState(false);

  const [selectedMColor, setSelectedMColor] = useState('#ffffff');
  const [customColors, setCustomColors] = useState([])
  const [enableCustomColors, setEnableCustomColors] = useState(false)
  const [customColorPaletteName, setCustomColorPaletteName] = useState('')

  const [enablePagination, setEnablePagination] = useState(false)
  const [relationChartType, setRelationChartType] = useState('');
  const [disableDefaultOptions, setDisableDefaultOptions] = useState(false);
  const [isTrend, setIsTrend] = useState(false);
  const [showCardTitle, setShowCardTitle] = useState(false);

  const [trendPagination, setTrendPagination] = useState(2)
  const [displayTrend, setDisplayTrend] = useState(['trend', 'value'])
  const [trendPrefix, setTrendPrefix] = useState('vs.');
  const [trendPrefixPosition, setTrendPrefixPosition] = useState('center');

  const [canvasView, setCanvasView] = useState('standard')
  const [canvasWidth, setCanvasWidth] = useState(0)
  const [canvasHeight, setCanvasHeight] = useState(0)
  const [canvasAlign, setCanvasAlign] = useState('center')

  const [cardView, setCardView] = useState('entireView')
  const [cardWidth, setCardWidth] = useState(0)
  const [cardHeight, setCardHeight] = useState(0)

  const [showTooltip, setShowTooltip] = useState(true)
  const [isTemplateEdited, setIsTemplateEdited] = useState(false)
  const [tooltipTemplate, setTooltipTemplate] = useState("")
  const [enableTemplate, setEnableTemplate] = useState(false)
  const [tdOpen, setTDOpen] = useState(false)
  const [enableCustomFormatting, setEnableCustomFormatting] = useState(false)
  const [customFormat, setCustomFormat] = useState("")


  const keysPressed = useSelector((state) => state.app.keysPressed);
  const showMoreCardIcons = useSelector((state) => state.propertyPane.showMore);
  const currentSCLocation = useSelector((state) => state.app.currentSCLocation);
  const report = useSelector((state) => {
    let activeReport = state.hreport.present.reports.find((report) => report.active);
    return activeReport;
  });

  // let { title, subTitle, format, formatColor } = report.properties;
  let { reportData, chartColorPalette = {} } = report || {}
  const { data, metadata } = reportData || {}
  let { title = {}, subTitle = {}, format = {}, formatColor = {}, axisRange = {}, card = {}, table = {}, map = {}, line = {}, progress = {}, radial = {}, radar = {}, shape = {}, chartTheme = {}, charts = {}, relationChart = {}, canvas = {}, tooltip = {} } = report?.reportData?.properties || {};
  const [fields, setFields] = useState(format?.formatFields);
  const [axisRangeFields, setAxisRangeFields] = useState(axisRange?.fields)
  title = title || {};
  subTitle = subTitle || {};
  format = format || {};
  formatColor = formatColor || {};

  const removeFormatColorFieldHandler = () => {
    setFormatColorField("");
    setFormatFormatColorStyle("");
    setFormatColorEnableSteps(false);
    setFormatColorEnableReverse(false);
    setFormatColorSteps(data?.length || 10);
  }

  const onCustomColorPaletteAdd = () => {
    if (!customColorPaletteName || customColors?.length < 10) return;
    dispatch(addCustomChartColorPalette({ [customColorPaletteName]: customColors })) // 
    setCustomColorPaletteName('')
    setCustomColors([])
  }

  const onOpenTooltipTemplateDrawer = () => {
    setTDOpen(true)
  }

  const values = {
    report,
    formatDatatype,
    fields,
    fieldId,
    data,
    showAllColorFields,
    showAllFormatFields,
    formatColorField,
    formatColorStyle,
    formatColorEnableSteps,
    formatColorSteps,
    formatColorEnableReverse,
    formatMinColor,
    formatMaxColor,
    isPercentageEnabled,
    spaceBeforeDisplayUnits,
    gridLines,
    showAxisName,
    showGridChartAxisName,
    synchronize,
    axisRangeId,
    axisRangeDataType,
    axisRangeFields,
    cardPrefixType,
    cardSuffixType,
    cardTitle,
    cardPrefix,
    cardSuffix,
    cardPrefixColor,
    cardSuffixColor,
    showMoreCardIcons,
    recordsPerPage,
    mapStyle,
    // mapType,
    longitude,
    latitude,
    mapZoom,
    horizontalScroll,
    smooth,
    fetchAllRecords,
    onRemoveFormatColorField: removeFormatColorFieldHandler,
    progressTarget,
    progressChartType,
    isProgressTargetStatic,
    showTargetAndValue,
    statisticType,
    doughnutPrefixType,
    doughnutSuffixType,
    doughnutPrefix,
    doughnutSuffix,
    doughnutTitle,
    showDoughnutTitle,
    radarChartType,
    progressRangeColors,
    mapDefaultShape,
    mapShowAllShapes,
    selectedMColor,
    setSelectedMColor,
    customColors,
    enableCustomColors,
    customColorPaletteName,
    onCustomColorPaletteAdd,
    chartColorPalette,
    setCustomColors,
    setEnableCustomColors,
    setCustomColorPaletteName,
    dispatch,
    enablePagination,
    relationChartType,
    disableDefaultOptions,
    isTrend,
    showCardTitle,
    trendPagination,
    displayTrend,
    canvasView,
    canvasWidth,
    canvasHeight,
    canvasAlign,
    trendPrefix,
    trendPrefixPosition,
    cardView,
    cardWidth,
    cardHeight,
    showTooltip,
    tooltipTemplate,
    isTemplateEdited,
    onOpenTooltipTemplateDrawer,
    formatColorMinValue,
    formatColorMaxValue,
    formatColorCenterValue,
    enableAdvanceSteps,
    enableTemplate,
    formatColorMinValue,
    formatColorMaxValue,
    formatColorCenterValue,
    enableAdvanceSteps,
    enableCustomFormatting,
    customFormat
  }

  useEffect(() => {
    setPropertyItems(getPropertyItems(values));
    setKey(new Date());
  }, [
    formatDatatype,
    fieldId,
    report?.fields,
    fields,
    axisRangeId,
    axisRangeFields,
    report?.reportId,
    data,
    showAllColorFields,
    showAllFormatFields,
    formatColorField,
    formatColorStyle,
    formatColorEnableSteps,
    formatColorSteps,
    formatColorEnableReverse,
    formatMinColor,
    formatMaxColor,
    gridLines,
    showAxisName,
    showGridChartAxisName,
    synchronize,
    isPercentageEnabled,
    spaceBeforeDisplayUnits,
    cardPrefixType,
    cardSuffixType,
    cardTitle,
    cardPrefix,
    cardSuffix,
    cardPrefixColor,
    cardSuffixColor,
    showMoreCardIcons,
    recordsPerPage,
    mapStyle,
    // mapType,
    longitude,
    latitude,
    mapZoom,
    horizontalScroll,
    smooth,
    fetchAllRecords,
    progressTarget,
    progressChartType,
    isProgressTargetStatic,
    showTargetAndValue,
    statisticType,
    doughnutPrefixType,
    doughnutSuffixType,
    doughnutPrefix,
    doughnutSuffix,
    doughnutTitle,
    showDoughnutTitle,
    radarChartType,
    progressRangeColors,
    mapDefaultShape,
    mapShowAllShapes,
    selectedMColor,
    customColors,
    enableCustomColors,
    customColorPaletteName,
    enablePagination,
    relationChartType,
    chartColorPalette,
    disableDefaultOptions,
    isTrend,
    showCardTitle,
    trendPagination,
    displayTrend,
    canvasView,
    canvasWidth,
    canvasHeight,
    canvasAlign,
    trendPrefix,
    trendPrefixPosition,
    cardView,
    cardWidth,
    cardHeight,
    showTooltip,
    tooltipTemplate,
    isTemplateEdited,
    formatColorMinValue,
    formatColorMaxValue,
    formatColorCenterValue,
    enableAdvanceSteps,
    enableTemplate,
    formatColorMinValue,
    formatColorMaxValue,
    formatColorCenterValue,
    enableAdvanceSteps,
    enableCustomFormatting,
    customFormat
  ]);

  useEffect(() => {
    setFieldId(format.activeFieldId);
    setFormatDatatype(format.formatDatatype);
    setFields(format.formatFields);
    if (format?.formatFields) {
      const activeField = format.formatFields.find((field) => field.id === format?.activeFieldId)
      if (activeField) {
        if (activeField?.values?.percentage) {
          setIsPercentageEnabled(activeField.values?.percentage)
        }
        if (activeField?.values?.enableCustomFormatting) {
          setEnableCustomFormatting(activeField.values.enableCustomFormatting)
        }
        if (activeField?.values?.customFormat) {
          setCustomFormat(activeField.values.customFormat)
        }
        if (activeField?.values?.spaceBeforeDisplayUnits) {
          setSpaceBeforeDisplayUnits(activeField.values?.spaceBeforeDisplayUnits)
        }
      }
    }
  }, [format, report?.reportId]);

  useEffect(() => {
    if (currentSCLocation === 'HR PR') {
      let groupId;
      if (isMatchingShortcut(keysPressed, ['p', 't'])) {
        groupId = 'title';
      } else if (isMatchingShortcut(keysPressed, ['p', 'u'])) {
        groupId = 'subTitle';
      } else if (isMatchingShortcut(keysPressed, ['p', 'f'])) {
        groupId = 'format';
      } else if (isMatchingShortcut(keysPressed, ['p', 'e'])) {
        groupId = 'cache';
      } else if (isMatchingShortcut(keysPressed, ['p', 'c'])) {
        groupId = 'color';
      } else if (isMatchingShortcut(keysPressed, ['p', 'b'])) {
        groupId = 'bar';
      } else if (isMatchingShortcut(keysPressed, ['p', 'l'])) {
        groupId = 'legend';
      } else if (isMatchingShortcut(keysPressed, ['p', 'i'])) {
        groupId = 'radial';
      } else if (isMatchingShortcut(keysPressed, ['p', 'x'])) {
        groupId = 'axisRange';
      } else if (isMatchingShortcut(keysPressed, ['p', 'd'])) {
        groupId = 'card';
      } else if (isMatchingShortcut(keysPressed, ['p', 'o'])) {
        groupId = 'labels';
      } else if (isMatchingShortcut(keysPressed, ['p', 'y'])) {
        groupId = 'crosstab';
      } else if (isMatchingShortcut(keysPressed, ['p', 'm'])) {
        groupId = 'map'
      } else if (isMatchingShortcut(keysPressed, ['p', 'n'])) {
        groupId = 'line'
      } else if (isMatchingShortcut(keysPressed, ['p', 'p'])) {
        groupId = 'progress'
      } else if (isMatchingShortcut(keysPressed, ['p', 'g'])) {
        groupId = 'radar'
      } else if (isMatchingShortcut(keysPressed, ['p', 'h'])) {
        groupId = 'shape'
      } else if (isMatchingShortcut(keysPressed, ['p', 'j'])) {
        groupId = 'chartTheme'
      } else if (isMatchingShortcut(keysPressed, ['p', 'k'])) {
        groupId = 'charts'
      } else if (isMatchingShortcut(keysPressed, ['p', 'v'])) {
        groupId = 'relationChart'
      } else if (isMatchingShortcut(keysPressed, ['p', 'q'])) {
        groupId = 'canvas'
      } else if (isMatchingShortcutWithShiftKey(keysPressed, ['p', 'f'])) {
        groupId = 'filter'
      } else if (isMatchingShortcutWithShiftKey(keysPressed, ['p', 't'])) {
        groupId = 'tooltip'
      }
      else if (isMatchingShortcut(keysPressed, ['p', 'a'])) {
        if (applyRef.current) {
          applyRef.current.click()
        }
      } else if (isMatchingShortcut(keysPressed, ['p', 'r'])) {
        if (resetRef.current) {
          resetRef.current.click()
        }
      } else if (isMatchingShortcut(keysPressed, ['p', 's'])) {
        if (searchRef.current) {
          searchRef.current.click()
        }
      } else if (isMatchingShortcut(keysPressed, ['p', 'z'])) {
        groupId = 'table'
      }
      if (groupId) {
        setGroupId(groupId);
        dispatch(setKeysPressed('reset'));
        dispatch(setShotCutCurrentLocation(""))
      }
    }
  }, [keysPressed])

  useEffect(() => {
    setShowAllColorFields(formatColor.showAll);
  }, [formatColor.showAll]);

  useEffect(() => {
    setShowAllFormatFields(format.showAll);
  }, [format.showAll]);

  useEffect(() => {
    setFormatColorField(formatColor.formatColorField)
  }, [formatColor.formatColorField]);

  useEffect(() => {
    setFormatFormatColorStyle(formatColor.formatColorStyle)
  }, [formatColor.formatColorStyle])

  useEffect(() => {
    setFormatColorEnableSteps(formatColor.enableSteps)
  }, [formatColor.enableSteps])

  useEffect(() => {
    setFormatColorSteps(formatColor.steps)
  }, [formatColor.steps])

  useEffect(() => {
    setFormatColorEnableReverse(formatColor.enableReverse)
  }, [formatColor.enableReverse])

  useEffect(() => {
    setFormatMinColor(formatColor.minimum)
  }, [formatColor.minimum])

  useEffect(() => {
    setFormatMaxColor(formatColor.maximum)
  }, [formatColor.maximum])

  useEffect(() => {
    setFormatColorMinValue(formatColor.minValue)
  }, [formatColor.minValue])

  useEffect(() => {
    setFormatColorMaxValue(formatColor.maxValue)
  }, [formatColor.maxValue])

  useEffect(() => {
    setFormatColorCenterValue(formatColor.centerValue)
  }, [formatColor.centerValue])

  useEffect(() => {
    setEnableAdvanceSteps(formatColor.enableAdvanceSteps)
  }, [formatColor.enableAdvanceSteps])

  useEffect(() => {
    setCardPrefixType(card.prefixType)
  }, [card.prefixType])
  useEffect(() => {
    setCardPrefix(card.prefix)
  }, [card.prefix])
  useEffect(() => {
    setCardSuffixType(card.suffixType)
  }, [card.suffixType])
  useEffect(() => {
    setCardPrefixColor(card.prefixColor)
  }, [card.prefixColor])
  useEffect(() => {
    setCardSuffixColor(card.suffixColor)
  }, [card.suffixColor])
  useEffect(() => {
    setCardSuffix(card.suffix)
  }, [card.suffix])
  useEffect(() => {
    setCardTitle(card.title)
  }, [card.title])
  useEffect(() => {
    setIsTrend(card.isTrend)
  }, [card.isTrend])
  useEffect(() => {
    setShowCardTitle(card.showTitle)
  }, [card.showTitle])
  useEffect(() => {
    setTrendPagination(card.trendPagination)
  }, [card.trendPagination])
  useEffect(() => {
    setDisplayTrend(card.displayTrend)
  }, [card.displayTrend])
  useEffect(() => {
    setTrendPrefix(card.trendPrefix)
  }, [card.trendPrefix])
  useEffect(() => {
    setTrendPrefixPosition(card.trendPrefixPosition)
  }, [card.trendPrefixPosition])

  useEffect(() => {
    if (formatDatatype) {
      setGroupId("format");
    }
  }, [formatDatatype]);

  useEffect(() => {
    setAxisRangeId(axisRange.activeId);
    setAxisRangeDatatype(axisRange.activeDatatype);
    setAxisRangeFields(axisRange.fields);
    setGridLines(axisRange.gridLines)
    setSynchronize(axisRange.synchronize)
    setShowAxisName(axisRange.showAxisName)
    setShowGridChartAxisName(axisRange.showGridChartAxisName)
  }, [axisRange, report.reportId]);

  useEffect(() => {
    setRecordsPerPage(table.recordsPerPage)
  }, [table.recordsPerPage])

  useEffect(() => {
    setHorizontalScroll(table.horizontalScroll)
  }, [table.horizontalScroll])

  useEffect(() => {
    setFetchAllRecords(table.fetchAllRecords)
  }, [table.fetchAllRecords])

  useEffect(() => {
    setDisableDefaultOptions(table.disableDefaultOptions)
  }, [table.disableDefaultOptions])

  useEffect(() => {
    setSmooth(line.smooth);
  }, [line.smooth]);

  useEffect(() => {
    setMapStyle(map.mapStyle)
  }, [map.mapStyle])

  // useEffect(() => {
  //   setMapType(map.mapType)
  // }, [map.mapType])

  useEffect(() => {
    setLongitude(map.longitude)
  }, [map.longitude])

  useEffect(() => {
    setLatitude(map.latitude)
  }, [map.latitude])

  useEffect(() => {
    setMapZoom(map.zoom)
  }, [map.zoom])

  useEffect(() => {
    setProgressTargetStatic(progress.isStatic)
  }, [progress.isStatic])

  useEffect(() => {
    setProgressTarget(progress.target)
  }, [progress.target])

  useEffect(() => {
    setProgressChartType(progress.chartType)
  }, [progress.chartType])

  useEffect(() => {
    setShowTargetAndValue(progress.showTargetAndValue)
  }, [progress.showTargetAndValue])

  useEffect(() => {
    setstatisticType(progress.statisticType)
  }, [progress.statisticType])

  useEffect(() => {
    setProgressRangeColors((prev) => ({ ...prev, ['20%']: progress['20%'], ['40%']: progress['40%'], ['60%']: progress['60%'], ['80%']: progress['80%'], ['100%']: progress['100%'] }))
  }, [progress['20%'], progress['40%'], progress['60%'], progress['80%'], progress['100%']])

  useEffect(() => {
    setDoughnutPrefixType(radial.doughnutPrefixType)
  }, [radial.doughnutPrefixType])

  useEffect(() => {
    setDoughnutSuffixType(radial.doughnutSuffixType)
  }, [radial.doughnutSuffixType])

  useEffect(() => {
    setDoughnutPrefix(radial.doughnutPrefix)
  }, [radial.doughnutPrefix])

  useEffect(() => {
    setDoughnutSuffix(radial.doughnutSuffix)
  }, [radial.doughnutSuffix])

  useEffect(() => {
    setShowDoughnutTitle(radial.showDoughnutTitle)
  }, [radial.showDoughnutTitle])

  useEffect(() => {
    setDoughnutTitle(radial.doughnutTitle)
  }, [radial.doughnutTitle])

  useEffect(() => {
    setRadarChartType(radar.radarChartType)
  }, [radar.radarChartType])


  useEffect(() => {
    setMapDefaultShape(shape.mapDefaultShape)
  }, [shape.mapDefaultShape])


  useEffect(() => {
    setMapShowAllShapes(shape.mapShowAllShapes)
  }, [shape.mapShowAllShapes])

  useEffect(() => {
    let tempP = { ...generateReportPropRef?.current }
    tempP['table'] = {
      ...tempP[table],
      changed: fetchAllRecords ? true : false,
      fetchAllRecords
    }
    generateReportPropRef.current = tempP;
  }, [fetchAllRecords])

  useEffect(() => {
    setCustomColors(chartTheme?.customColors)
  }, [chartTheme?.customColors])

  useEffect(() => {
    setEnableCustomColors(chartTheme?.enableCustomColors)
  }, [chartTheme?.enableCustomColors])

  useEffect(() => {
    setCustomColorPaletteName(chartTheme?.paletteName)
  }, [chartTheme?.paletteName])

  useEffect(() => {
    setEnablePagination(charts?.enablePagination)
  }, [charts?.enablePagination])

  useEffect(() => {
    setRelationChartType(relationChart.chartType)
  }, [relationChart.chartType])

  useEffect(() => {
    setCanvasView(canvas.view)
  }, [canvas.view])

  useEffect(() => {
    setCanvasWidth(canvas.width)
  }, [canvas.width])

  useEffect(() => {
    setCanvasHeight(canvas.height)
  }, [canvas.height])

  useEffect(() => {
    setCanvasAlign(canvas.align)
  }, [canvas.align])

  useEffect(() => {
    setCardView(card.cardView)
  }, [card.cardView])

  useEffect(() => {
    setCardWidth(card.cardWidth)
  }, [card.cardWidth])

  useEffect(() => {
    setCardHeight(card.cardHeight)
  }, [card.cardHeight])

  useEffect(() => {
    setShowTooltip(tooltip.showTooltip)
  }, [tooltip.showTooltip])

  useEffect(() => {
    setIsTemplateEdited(tooltip.isTemplateEdited)
  }, [tooltip.isTemplateEdited])

  useEffect(() => {
    setTooltipTemplate(tooltip.tooltipTemplate)
  }, [tooltip.tooltipTemplate])

  useEffect(() => {
    setEnableTemplate(tooltip.enableTemplate)
  }, [tooltip.enableTemplate])


  const handleApplyProperties = (itemsData) => {
    const updatedProperties = getAppliedUpdatedProperties({
      itemsData,
      synchronize,
      fields,
      fieldId,
      showAllColorFields,
      showAllFormatFields,
      formatColorField,
      formatColorStyle,
      axisRangeId,
      axisRangeDataType,
      axisRangeFields,
      reportData,
      recordsPerPage,
      mapStyle,
      // mapType,
      longitude,
      latitude,
      mapZoom,
      horizontalScroll,
      smooth,
      fetchAllRecords,
      progressTarget,
      progressChartType,
      isProgressTargetStatic,
      progressRangeColors,
      relationChartType,
      report,
      disableDefaultOptions,
      isTemplateEdited,
      tooltipTemplate
    })
    dispatch(updateReportProperty({
      properties: updatedProperties
    }))

    let { table = {} } = generateReportPropRef?.current || {}
    if (table?.changed && table?.fetchAllRecords) {
      let tempReportProperties = { ...report?.properties, ...updatedProperties }
      let tempReportData = { ...report?.reportData, properties: tempReportProperties, }
      let tempReport = { ...report, properties: tempReportProperties, reportData: tempReportData }
      generateReport({ ...tempReport }, dispatch)
      generateReportPropRef.current = intialGenerateReportProp
    }
  };

  const updateFormatField = (data, id, key, disableAutoFormat) => {
    setFields((prevFields) => {
      return prevFields.map((field) => {
        if (field.id === id) {
          let tempValues = { ...field.values, [key]: data };
          if (key === 'autoFormatting' && !disableAutoFormat) {
            let actualField = report.fields.find((field) => field.id === id)
            let fieldType = getCanvasFieldDataType(actualField)
            if (data) {
              // set auto formatting properties of field according to the field type
              tempValues = { ...tempValues, ...getDefaultAFDataTypeValues(fieldType, "create") }
            } else {
              // set the default properties of field according to field type
              tempValues = { ...tempValues, ...getDataTypeValues(fieldType) }
            }
          } else {
            if (tempValues.autoFormatting) {
              tempValues.autoFormatting = false; //disable auto format if any other fields have changed
            }
          }
          return {
            ...field, values: tempValues
          };
        }
        return field;
      });
    })
  }

  const getTemplate = () => {
    const templateFields = report.fields.filter((field) => !["drillthrough_field", "measure_field"].includes(field.addedAs))
    const template = templateFields.map((field) => {
      let fieldName = getFieldDisplayName(field);
      return `<p>${fieldName} : {{${fieldName}}}</p>`
    }).join("");
    return template
  }

  const updateTooltipTemplate = () => {
    if (isTemplateEdited) return;
    setTooltipTemplate(getTemplate())
  }

  const getData = ({ value, key, groupId, record }) => {
    if (key === "field" && groupId === "format") {
      const selectedField = report.fields.find((field) => field.id === value);
      let dataType = selectedField ? getCanvasFieldDataType(selectedField) : "";
      if (dataType === "text" && selectedField?.floatingType === "continous") {
        dataType = "numeric"
      }
      if (selectedField?.custom && selectedField?.addedAs !== 'trend_field') {
        dataType = getCustomFieldDataType(metadata, selectedField)
      }
      if (selectedField?.addedAs === 'trend_field') {
        dataType = 'numeric'
      }
      setFormatDatatype(dataType);
      setFieldId(value);
      const isPresent = fields?.find((field) => field.id === value);
      if (isPresent === undefined) {
        setFields((prevState) => [
          ...prevState,
          { id: value, values: getDataTypeValues(dataType, report.mode) },
        ]);
      }
      let currentField = fields?.find(({ id: fId }) => fId === value)
      if (currentField) {
        setIsPercentageEnabled(currentField?.values?.percentage ?? false);
        setEnableCustomFormatting(currentField?.values?.enableCustomFormatting ?? false);
        setCustomFormat(currentField?.values?.customFormat ?? '');
      } else {
        setIsPercentageEnabled(false)
        setEnableCustomFormatting(false)
        setCustomFormat('')
      }
    }
    if (key === "gridLines" && groupId === "axisRange") {
      setGridLines(value)
    }
    if (key === 'showAxisName' && groupId === 'axisRange') {
      setShowAxisName(value)
    }
    if (key === 'showGridChartAxisName' && groupId === 'axisRange') {
      setShowGridChartAxisName(value)
    }
    if (key === "synchronize" && groupId === "axisRange") {
      setSynchronize(value)
    }
    if (key === "applyRangeOn" && groupId === "axisRange") {
      const selectedField = report.fields.find((field) => field.id === value);
      let dataType = selectedField
        ? getCanvasFieldDataType(selectedField)
        : "";
      if (dataType === "text" && selectedField?.floatingType === "continous") {
        dataType = "numeric"
      }
      if (selectedField?.custom) {
        dataType = getCustomFieldDataType(metadata, selectedField)
      }
      setAxisRangeDatatype(dataType);
      setAxisRangeId(value);
      const isPresent = axisRangeFields?.find((field) => field.id === value);
      if (isPresent === undefined) {
        setAxisRangeFields((prevState) => [
          ...prevState,
          {
            id: value, data: {
              name: "", minRange: "", maxRange: "", dataType, fontSize: 12,
              fontColor: {},
            }
          },
        ]);
      }
    }
    if (key === "showAllFormatFields" && groupId === "format") {
      setShowAllFormatFields(value);
    }
    if (groupId === "color") {
      switch (key) {
        case "showAll":
          setShowAllColorFields(value);
          break;
        case "formatColorField":
          if (value) {
            const fieldLabel = getDefaultValue(report, formatColorField)
            setFormatColorField(value)
            setFormatFormatColorStyle(fieldLabel)
          }
          break;
        case "formatColorStyle":
          setFormatFormatColorStyle(value)
          break;
        case "enableSteps":
          setFormatColorEnableSteps(value)
          break;
        case "steps":
          setFormatColorSteps(value)
          break;
        case "enableReverse":
          setFormatColorEnableReverse(value)
          let tempMinColor = formatMinColor, tempMaxColor = formatMaxColor;
          setFormatMinColor(tempMaxColor)
          setFormatMaxColor(tempMinColor)
          break;
        case "minimum":
          setFormatMinColor(value)
          break;
        case "maximum":
          setFormatMaxColor(value)
          break;
        case "minValue":
          setFormatColorMinValue(value)
          break;
        case "maxValue":
          setFormatColorMaxValue(value)
          break;
        case "centerValue":
          setFormatColorCenterValue(value)
          break;
        case "enableAdvanceSteps":
          setEnableAdvanceSteps(value)
          break;
        default:
          break;
      }
    }
    if (key === "percentage" && groupId === "format") {
      updateFormatField(false, fieldId, 'autoFormatting', true)
      setIsPercentageEnabled(value)
    }
    if (key === "enableCustomFormatting" && groupId === "format") {
      setEnableCustomFormatting(value)
    }
    if(key === "customFormat" && groupId === "format"){
        setCustomFormat(value)
    }
    if(key === "selectedFormat" && groupId === "format" && value){
        setCustomFormat(value)
    }
    if (key === "spaceBeforeDisplayUnits" && groupId === "format") {
      updateFormatField(false, fieldId, 'autoFormatting', true)
      setSpaceBeforeDisplayUnits(value)
    }
    if (key === "prefixType" && groupId === "card") {
      setCardPrefixType(value)
      setCardPrefix("")
    }
    if (key === "suffixType" && groupId === "card") {
      setCardSuffixType(value)
      setCardSuffix("")
    }
    if (key === "prefixColor" && groupId === "card") {
      setCardPrefixColor(value)
    }
    if (key === "suffixColor" && groupId === "card") {
      setCardSuffixColor(value)
    }
    if (key === "prefix" && groupId === "card") {
      setCardPrefix(value)
    }
    if (key === "suffix" && groupId === "card") {
      setCardSuffix(value)
    }
    if (key === "cardTitle" && groupId === "card") {
      setCardTitle(value)
    }
    if (key === 'isTrend' && groupId === 'card') {
      setIsTrend(value)
      // trend field add and update
      if (value) {
        setDisplayTrend(['trend', 'value'])
        setTrendPrefix("vs.")
        setTrendPrefixPosition("center")
        dispatch(addFieldToCanvas({ column: "frontend_custom_measure_value", alias: "Trend", addedAs: 'trend_field', genre: types.CUSTOM_FORMULA, custom_frontend_field: true }))
      } else {
        dispatch(updateTrendField())
      }
    }
    if (key === 'showTitle' && groupId === 'card') {
      setShowCardTitle(value)
    }
    if (key === 'trendPagination' && groupId === 'card') {
      setTrendPagination(value)
    }
    if (key === 'displayTrend' && groupId === 'card') {
      setDisplayTrend(value)
    }
    if (key === 'trendPrefix' && groupId === 'card') {
      setTrendPrefix(value)
    }
    if (key === 'trendPrefixPosition' && groupId === 'card') {
      setTrendPrefixPosition(value)
    }

    if (groupId === "card") {
      switch (key) {
        case 'cardView':
          setCardView(value)
          break;
        case 'cardWidth':
          setCardWidth(value)
          break;
        case 'cardHeight':
          setCardHeight(value)
          break;
        default:
          break;
      }
    }

    if (key === "recordsPerPage" && groupId === "table") {
      setRecordsPerPage(value)
    }
    if (key === "mapStyle" && groupId === "map") {
      setMapStyle(value)
    }
    // if (key === "mapType" && groupId === "map") {
    //   setMapType(value)
    // }
    if (groupId === "table") {
      switch (key) {
        case 'horizontalScroll':
          setHorizontalScroll(value)
          break;
        case 'fetchAllRecords':
          setFetchAllRecords(value)
          break;
        case 'DisableDefaultOptions':
          setDisableDefaultOptions(value)
          break;
        case 'smooth':
          setSmooth(value);
          break;
        default:
          break;
      }
    }
    if (groupId === "progress") {

      switch (key) {
        case 'target':
          setProgressTarget(value)
          break;
        case 'chartType':
          setProgressChartType(value)
          break;
        case 'isStatic':
          setProgressTargetStatic(value)
          if (value) setProgressTarget(0)
          break;
        case 'showTargetAndValue':
          setShowTargetAndValue(value)
          break;
        case 'statisticType':
          setstatisticType(value)
          break;
        // case 'progressRange':
        //   setProgressRange(value)
        default:
          break;
      }
      if (['20%', '40%', '60%', '80%', '100%'].includes(key)) {
        setProgressRangeColors((prev) => ({
          ...prev,
          [key]: value
        }))
      }
    }
    if (groupId === "radial") {
      switch (key) {
        case "doughnutPrefixType":
          setDoughnutPrefixType(value)
          break;
        case "doughnutSuffixType":
          setDoughnutSuffixType(value)
          break;
        case "doughnutPrefix":
          setDoughnutPrefix(value)
          break;
        case "doughnutSuffix":
          setDoughnutSuffix(value)
          break;
        case "doughnutTitle":
          setDoughnutTitle(value)
          break;
        case 'showDoughnutTitle':
          setShowDoughnutTitle(value)
          break;
        default:
          break;
      }
    }
    if (groupId === "format" && !['percentage', 'showAllFormatFields', 'field', 'spaceBeforeDisplayUnits'].includes(key)) {
      updateFormatField(value, fieldId, key)
    }
    if (groupId === "radar" && key === "radarChartType") {
      setRadarChartType(value)
    }
    if (groupId === 'shape') {
      switch (key) {
        case 'mapDefaultShape':
          setMapDefaultShape(value);
          break;
        case 'mapShowAllShapes':
          setMapShowAllShapes(value);
          break;
        default:
          break;
      }
    }
    if (groupId === 'chartTheme') {
      switch (key) {
        case 'customColors':
          setCustomColors(value)
          break;
        case 'enableCustomColors':
          setEnableCustomColors(value)
          break;
        case 'paletteName':
          setCustomColorPaletteName(value);
          break;
        default:
          break;
      }
    }
    if (groupId === 'charts') {
      if (key === 'enablePagination') {
        setEnablePagination(value)
      }
    }
    if (groupId === 'relationChart') {
      switch (key) {
        case 'chartType':
          setRelationChartType(value)
          break;
        default:
          break;
      }
    }
    if (groupId === 'canvas') {
      switch (key) {
        case 'view':
          setCanvasView(value)
          break;
        case 'width':
          setCanvasWidth(value)
          break;
        case 'height':
          setCanvasHeight(value)
          break;
        case 'align':
          setCanvasAlign(value)
          break;
        default:
          break;
      }
    }
    if (groupId === 'tooltip') {
      switch (key) {
        case 'showTooltip':
          setShowTooltip(value)
          break;
        case "enableTemplate":
          setEnableTemplate(value)
          if (value) {
            updateTooltipTemplate()
          }
          break;
        default:
          break;
      }
    }
  };
  const onReset = () => {
    // dispatch(updateReportProperty({ reset: true }));
    if (fieldId) {
      const selectedField = report.fields.find((field) => field.id === fieldId);
      let dataType = selectedField ? getCanvasFieldDataType(selectedField) : "";
      setFields((prevFields) => {
        if (format?.formatFields?.length) {
          let currentField = format.formatFields.find((field) => field.id === fieldId);
          return prevFields.map((field) => {
            if (field.id === fieldId && currentField?.id === fieldId) {
              return {
                ...currentField
              };
            }
            return field;
          });
        } else {
          return prevFields.map((field) => {
            if (field.id === fieldId) {
              return {
                ...field,
                values: getDataTypeValues(dataType)
              };
            }
            return field;
          });
        }
      })
    };
  }

  const handleTemplateReset = () => {
    setTooltipTemplate(getTemplate());
  }

  return (
    <div className="hi-properties-container" data-testid="hr-properties">
      <HIPropertyPane
        group={sortPropertyPane(propertyPaneGroupItems)}
        getData={getData}
        groupId={groupId}
        infoItems={getInfoItems()}
        getDataOnApply={handleApplyProperties}
        showApply={true}
        items={propertyItems}
        newKey={key}
        menuWidth={14}
        menuToolbarWidth={10}
        // onReset={onReset}  6436 - fix
        onReset={onReset}
        shortCutConfig={{
          apply: {
            ref: applyRef,
            scLocation: "HR PR",
            text: "A"
          },
          reset: {
            ref: resetRef,
            scLocation: "HR PR",
            text: "R"
          },
          search: {
            ref: searchRef,
            scLocation: "HR PR",
            text: "S"
          }
        }}
      />
      <TooltipTemplateEditor
        template={tooltipTemplate}
        open={tdOpen}
        onClose={() => setTDOpen(false)}
        onChange={(value) => {
          setTooltipTemplate(value)
          setIsTemplateEdited(true)
          setTDOpen(false)
        }}
        onReset={handleTemplateReset}
      />
    </div>
  );
};

export default HrProperties;
