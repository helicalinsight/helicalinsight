import Icon from "@ant-design/icons";
import {
  UndoSvg,
  RedoSvg,
  ExportSvg,
  CacheRefreshSvg,
  ShareSvg,
  PreviewSvg,
  ColumnsSvg,
  RowsSvg,
  ReportCESvg,
  TextWidthSvg,
  CSVfileSvg,
  HourGlassSvg,
  NewPaperSvg,
  ObjectGroupIcon,
  TouchSvg,
  EditBoxSvg,
  InstantBISvg,
  HIRestoreSvg,
  OwnershipTransferSvg,
  HCRDataSourceSvg,
  hcrHorJustifySvg,
  hcrVerticalJustifySvg,
  hcrShrtCutArwSvg,
  hcrPageBreakSvg,
  hcrCrossTabSvg
} from "./hi-icons-svg";
const HIUndoIcon = () => <Icon component={UndoSvg} />;
const HIRedoIcon = () => <Icon component={RedoSvg} />;
const HIExportIcon = () => <Icon component={ExportSvg} />;
const HICacheRefreshIcon = () => <Icon component={CacheRefreshSvg} />;
const HIShareIcon = (props) => (
  <Icon
    className={`hi-navbar-share-icon ${props.testId}`}
    component={ShareSvg}
  />
);
const HIPreviewIcon = ({ color }) => (
  <Icon component={() => <PreviewSvg color={color} />} />
);
const HIReportCEIcon = () => (
  <Icon className="hi-navbar-icon" component={ReportCESvg} />
);
const HIColumnsIcon = () => <Icon component={ColumnsSvg} />;
const HIRowsIcon = () => <Icon component={RowsSvg} />;
const HITextWidthIcon = () => <Icon component={TextWidthSvg} />;
const HICSVFileIcon = (props) => (
  <Icon
    component={CSVfileSvg}
    className={props.className}
    style={props.style}
  />
);
const HIHourGlassIcon = () => <Icon component={HourGlassSvg} />;
const HINewsPaperIcon = () => <Icon component={NewPaperSvg} />;
const HIObjectGroupIcon = () => <Icon component={ObjectGroupIcon} />;
const HITouchIcon = () => <Icon component={TouchSvg} />;
const HIEditBoxSvg = (props) => <Icon {...props} component={EditBoxSvg} />;
const HIInstantBISvg = (props) => (
  <Icon className={props.className} component={InstantBISvg} />
);
const HiRestoreIcon = ({ color }) => <Icon component={() => <HIRestoreSvg color={color} />} />;
const OwnershipTransferIcon = ({ color }) => <Icon component={() => <OwnershipTransferSvg color={color} />} />;
const DataSourceSvg = (props) => (
  <Icon className={props.className} component={HCRDataSourceSvg} />
);
const HcrHorJustifyIcon = () => <Icon component={hcrHorJustifySvg} />;

const HcrShrtCutArw = () => <Icon component={hcrShrtCutArwSvg} className={`hi-navbar-share-icon`} />;
const HcrVerticalJustifyIcon = () => <Icon component={hcrVerticalJustifySvg} />;
const HcrPageBreakIcon = (props) => <Icon component={hcrPageBreakSvg} className={props.className} />;
const HCRCrosstTabIcon = () => <Icon component={hcrCrossTabSvg} />;

const HIIcon = (props) => {
  const { name, color } = props;
  switch (name) {
    case "hcr-page-break":
      return <HcrPageBreakIcon {...props} />;
    case "hi-hcr-horizontal-justify":
      return <HcrHorJustifyIcon />;
    case "hi-hcr-shrt-cut-arw":
      return <HcrShrtCutArw />;
    case "hi-hcr-vertical-justify":
      return <HcrVerticalJustifyIcon />;
    case "hi-hcr-datasource":
      return <DataSourceSvg {...props} />;
    case "hi-restore":
      return <HiRestoreIcon color={color} />;
    case "hi-ownership-transfer":
      return <OwnershipTransferIcon color={color} />;
    case "hi-undo":
      return <HIUndoIcon />;
    case "hi-redo":
      return <HIRedoIcon />;
    case "hi-export":
      return <HIExportIcon />;
    case "hi-cache-refresh":
      return <HICacheRefreshIcon />;
    case "hi-share":
      return <HIShareIcon testId={props.testId || ""} />;
    case "hi-preview":
      return <HIPreviewIcon color={color} />;
    case "hi-columns":
      return <HIColumnsIcon />;
    case "hi-rows":
      return <HIRowsIcon />;
    case "hi-report-ce":
      return <HIReportCEIcon />;
    case "hi-text-width":
      return <HITextWidthIcon />;
    case "hi-csv-file":
      return <HICSVFileIcon {...props} />;
    case "hi-hour-glass":
      return <HIHourGlassIcon />;
    case "hi-news-paper":
      return <HINewsPaperIcon />;
    case "hi-object-group":
      return <HIObjectGroupIcon />;
    case "hi-touch-drag":
      return <HITouchIcon />;
    case "hi-edit-box":
      return <HIEditBoxSvg {...props} />;
    case "hi-instant-bi-svg":
      return <HIInstantBISvg {...props} />;
    case "hi-hcr-crosstab":
      return <HCRCrosstTabIcon />;
    default:
      return <HIColumnsIcon />;
  }
};

export default HIIcon;
