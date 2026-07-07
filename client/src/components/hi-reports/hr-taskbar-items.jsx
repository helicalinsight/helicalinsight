import {
  LineChartOutlined,
  SaveOutlined,
  SaveFilled,
  ExportOutlined,
  FileExcelOutlined,
  FullscreenOutlined,
  SyncOutlined,
  LayoutOutlined,
  CheckOutlined,
  UndoOutlined,
  RedoOutlined,
  LoadingOutlined
} from "@ant-design/icons";
import { hreportRedo, hreportUndo, loadMetadata, loadReportData, setHReportLoading, setHreportSidebarLoading, setMetadataLoading, updatePreviewState, updateReportLayout } from "../../redux/actions/hreport.actions";
import HIIcon from "../common/icons/hi-icons";
import { launchIntoFullscreen } from "../../utils/dom-utils";
import { generateReport } from "./utils/base";
import { exportReport } from "../../utils/utilities";
import notify from "../hi-notifications/notify";

export const handleFullScreenClick = () => {
  launchIntoFullscreen(document.body);
};

export const toggleToolsAreaShelf = (dispatch, cb = () => { }) => {
  dispatch(updateReportLayout({ pane: "toolsAreaShelf" }));
  cb();
};

export const getTooltipTitle = (text, shortcut) => {
  if (shortcut) {
    return <span> {text} ({shortcut}) </span>
  }
  else {
    return text ? <div> {text} </div>: null;  
  }
}

const getHrTaskBarItems = ({
  activeReport,
  dispatch,
  layout,
  handleShare,
  onSave,
  onSaveAs,
  preview,
  getApi,
  refs,
  hreportPast,
  hreportFuture,
  activeReportId,
  user,
  isSavingInProgress,
  handleToggle,
  fullscreen,
  getDropdownOptions = () => { }
}) => {
  let { metadataShelf, fieldsAreaShelf, toolsAreaShelf } = layout;
  const handlePreviewClick = () => {
    if (activeReport.reportData.dataId) {
      dispatch(updatePreviewState());
      handleToggle('preview', !preview)
    } else {
      const Notify = notify(dispatch);
      Notify.warning({ type: "Frontend", message: "Please generate any report." });
    }
  };
  const toggleFieldsAreaShelf = () => {
    dispatch(updateReportLayout({ pane: "fieldsAreaShelf" }));
  };
  const toggleMetadataShelf = () => {
    dispatch(updateReportLayout({ pane: "metadataShelf" }));
    handleToggle('metadataShelf', !metadataShelf)
  };

  const toolsAreaShelfCb = () => {
    handleToggle('toolsAreaShelf', !toolsAreaShelf)
  };

  const handleGenerate = () => {
    generateReport({ ...activeReport, user }, dispatch, getApi);
  };
  const handleExport = (format) => {
    exportReport({ id: activeReport.id, format }, dispatch);
  };
  const handleRefreshData = () => {
    generateReport({ ...activeReport, user, refresh: true }, dispatch, getApi);
  };
  const handleUndo = () => {
    hreportPast?.length && dispatch(hreportUndo());
  }
  const handleRedo = () => {
    hreportFuture?.length && dispatch(hreportRedo());
  }

  const handleHreportUndoClick = () => {
    if (hreportPast?.length) {
      dispatch(hreportUndo());
      dispatch(setHreportSidebarLoading({ undoRedoAction: true }))
      dispatch(setMetadataLoading({
        undoRedoAction: true,
      }))
      dispatch(setHReportLoading({
        id: activeReportId,
        loading: false
      }))
      // let activeReport = hreportPast[hreportPast.length - 1]?.reports?.find((report) => report.id === activeReportId);
      // if (activeReport?.reportData?.loading) {
      //   dispatch(loadReportData({ ...activeReport, reportData: { loading: false }, undoRedoAction: true }))
      // }
    }
  }

  refs.save.current = onSave
  refs.saveAs.current = onSaveAs
  refs.preview.current = handlePreviewClick
  refs.undo.current = handleHreportUndoClick
  refs.redo.current = handleRedo

  const taskBarItems = [
    {
      tooltip: "Generate",
      tutorialKey: "hr-generate-report",
      icon: <LineChartOutlined />,
      callBack: handleGenerate,
      ref: refs.generate,
      scText: "G",
      scLocation: "HR"
    },
    // {
    //     tooltip: "Undo",
    //     icon: <HIIcon name="hi-undo" />,
    //     callBack: () => { },
    //     disabled: true,
    // },
    // {
    //     tooltip: "Redo",
    //     icon: <HIIcon name="hi-redo" />,
    //     callBack: () => { },
    //     disabled: true,
    // },
    ...(isSavingInProgress ? [{
      icon: <LoadingOutlined />,
      tooltip: "Please wait while we save your report. This may take a few moments.",
    }] : [{
      tooltip: "Save",
      tutorialKey: "hr-save",
      icon: <SaveOutlined />,
      id: "hr-save",
      scText: "S",
      scLocation: "HR",
      ref: refs.saveDropDown,
      dropdown: [
        {
          tooltip: "Save",
          name: "Save",
          icon: <SaveOutlined />,
          callBack: onSave,
          scText: "1",
          scLocation: "HR SV",
          shortCut: "Ctrl+S"
        },
        {
          tooltip: "Save As",
          name: "Save As",
          icon: <SaveFilled />,
          callBack: onSaveAs,
          scText: "2",
          scLocation: "HR SV"
        },
      ],
    },]),
    {
      tooltip: "Export",
      id: "hr-export",
      tutorialKey: "hr-export",
      icon: <ExportOutlined />,
      scText: "E",
      scLocation: "HR",
      ref: refs.exportDropDown,
      dropdown: getDropdownOptions("hreport", (format) => handleExport(format)),
    },
    {
      tooltip: "Cache Refresh",
      tutorialKey: "hr-cache-refresh",
      icon: <SyncOutlined />,
      callBack: handleRefreshData,
      ref: refs.cacheRefresh,
      scText: "R",
      scLocation: "HR"
    },
    {
      tooltip: "Share",
      tutorialKey: "hr-report-share",
      icon: <HIIcon name="hi-share" />,
      callBack: handleShare,
      ref: refs.share,
      scText: "H",
      scLocation: "HR"
    },
    {
      tooltip: "Layout",
      tutorialKey: "hr-layout",
      icon: <LayoutOutlined />,
      scText: "L",
      scLocation: "HR",
      id: "hr-layout",
      ref: refs.dropDown,
      dropdown: [
        {
          tooltip: "Metadata Shelf",
          name: "Metadata Shelf",
          icon: metadataShelf ? <CheckOutlined /> : null,
          callBack: toggleMetadataShelf,
          scText: "1",
          scLocation: "HR LY",
        },
        {
          tooltip: "Tools Shelf ",
          name: "Tools Shelf",
          icon: toolsAreaShelf ? <CheckOutlined /> : null,
          callBack: () => toggleToolsAreaShelf(dispatch, toolsAreaShelfCb),
          scText: "2",
          scLocation: "HR LY",
        },
        {
          tooltip: "Fields Shelf",
          name: "Fields Shelf",
          icon: fieldsAreaShelf ? <CheckOutlined /> : null,
          callBack: toggleFieldsAreaShelf,
          scText: "3",
          scLocation: "HR LY",
        },
      ],
    },
    {
      tooltip: "Preview",
      tutorialKey: "hr-preview",
      icon: <HIIcon color={preview ? "#1890ff" : "currentColor"} name="hi-preview" />,
      callBack: handlePreviewClick,
      scText: "8",
      scLocation: "HR",
    },
    {
      tooltip: "FullScreen",
      tutorialKey: "hr-presentation",
      icon: <FullscreenOutlined />,
      callBack: handleFullScreenClick,
      scText: "9",
      scLocation: "HR",
    },
    {
      tooltip: "Undo",
      tutorialKey: "hi-hreport-undo",
      dataTestId: "hi-hreport-undo-click",
      icon: <UndoOutlined style={hreportPast?.length ? { opacity: 1 } : { cursor: "not-allowed", opacity: 0.5 }} data-testid="hi-report-undo" className="taskbar-icon" />,
      callBack: handleHreportUndoClick,
      scText: "Z",
      scLocation: "HR",
      shortCut: "Ctrl+Z"
    }, {
      tooltip: "Redo",
      tutorialKey: "hi-hreport-redo",
      dataTestId: "hi-hreport-redo-click",
      icon: <RedoOutlined style={hreportFuture?.length ? { opacity: 1 } : { cursor: "not-allowed", opacity: 0.5 }} data-testid="hi-report-redo" className="taskbar-icon" />,
      callBack: () => {
        hreportFuture?.length && dispatch(hreportRedo());
      },
      scText: "Y",
      scLocation: "HR",
      shortCut: "Ctrl+Y"
    },
  ];

  return taskBarItems;
};

export default getHrTaskBarItems;
