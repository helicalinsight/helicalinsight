import { Button, Drawer } from "antd";
import { cloneDeep, filter, isEqual } from "lodash-es";
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import requests from "../../../base/requests";
import { hcrActions } from "../../../redux/actions";
import { checkRelativeDateFilter } from "../../../utils/filter-utils";
import {
  checkForAnchorRelativeParameters,
  getFieldDisplayName,
} from "../../../utils/utilities";
import notify from "../../hi-notifications/notify";
import { hcrDSParameter, hcrDSQuery } from "../hcr-constants";
import {
  getHcrParameterFilters,
  getPreviewFormData,
  handleUrlParamsFilters,
  updateDateRangeFilterValues,
  validateNodes,
} from "../hcrHelperMethods";
import HcrFilters from "./hcrFilters";
import {
  getErrorMessage,
  handleStreamResponse,
} from "../hcrCanvas/hcrCanvasPaneHelperMethods";

const HCRFiltersDrawer = ({
  hcrFiltersDrawerStatus,
  flowchartInstance,
  setIsPreviewLoading,
  reportMode,
  setAppliedFilters,
  urlParameters,
  setParametersReview = () => { },
  queryTempuuidsMap,
  streamEnabled,
  setPreviewError,
  onStreamSuccess = () => { },
  resetStream = () => { },
  reportKey,
  subDataSets = [],
  tableStyles = []
}) => {
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  const { previewRequest } = requests.cannedReport(dispatch);
  const activeReport =
    useSelector(
      (state) =>
        state.cannedReports.present.hcrTabData.panes.find(
          (pane) =>
            pane.key === state.cannedReports.present.hcrTabData.activeKey,
        ),
      isEqual,
    ) || {};
  const {
    dsPaneTypes,
    canvasProperties,
    hcrDiagramNodesData,
    title: reportName,
    isPreviewing,
    selectedQueryId,
    hcrExportProperties,
  } = activeReport || {};
  const parameters =
    useSelector((state) =>
      state.cannedReports.present.hcrTabData.panes
        .find(
          (pane) =>
            pane.key === state.cannedReports.present.hcrTabData.activeKey,
        )
        ?.dsPaneTypes?.find((pane) => pane.dataSourcePane === hcrDSParameter),
    ) || {};
  const { previewParameters } = canvasProperties || {};
  const queries =
    dsPaneTypes?.find((ele) => ele.dataSourcePane === hcrDSQuery) || {};
  const [filters, setFilters] = useState([]);

  useEffect(() => {
    urlParameters = checkForAnchorRelativeParameters(urlParameters);
    let reqFilters = getHcrParameterFilters({
      parameters,
      hcrDiagramNodesData,
    });
    if (Object.entries(urlParameters || {}).length) {
      reqFilters = handleUrlParamsFilters({
        urlParams: urlParameters,
        reqFilters,
      });
    }
    reqFilters = checkRelativeDateFilter(reqFilters, urlParameters);
    reqFilters = updateDateRangeFilterValues(reqFilters);
    setFilters(reqFilters);
    setParametersReviewFn(reqFilters);
  }, [parameters, JSON.stringify(urlParameters)]);

  useEffect(() => {
    let reqFilters = filters?.length
      ? filters
      : getHcrParameterFilters({ parameters, hcrDiagramNodesData }) || [];

    let allParas = { ...(urlParameters || {}) };
    delete allParas.mode;
    delete allParas.print;

    if (reportMode && Object.entries(allParas || {}).length) {
      urlParameters = checkForAnchorRelativeParameters(urlParameters);
      reqFilters = handleUrlParamsFilters({
        urlParams: allParas,
        reqFilters: [...reqFilters],
      });
      reqFilters = checkRelativeDateFilter(reqFilters, urlParameters);
      setFilters(reqFilters);
      if (isPreviewing) {
        applyChanges(reqFilters);
      }
    }
  }, [JSON.stringify(urlParameters)]);

  const setParametersReviewFn = (filters) => {
    let parameters = {};
    if (filters?.length) {
      filters.map((fltr) => {
        let fltrName = getFieldDisplayName(fltr);
        let filterValue = fltr.values;
        if (fltr.condition === "IN_RANGE") {
          filterValue = [fltr.values[0]];
        }
        parameters[fltrName] = filterValue;
      });
    }
    if (typeof setParametersReview === "function") {
      setParametersReview({ parameters });
    }
  };

  function applyChanges(filters) {
    resetStream(true);
    const query =
      queries.menu.find((query) => query.id === selectedQueryId) || {};
    const { isValid, bandLimits } = validateNodes({
      flowchartInstance,
      hcrDiagramNodesData,
      Notify,
      dispatch,
      canvasProperties,
    });
    filters = updateDateRangeFilterValues(filters);
    setAppliedFilters(cloneDeep(filters));
    if (isValid) {
      if (!isPreviewing) {
        dispatch(hcrActions.handleTogglePreview(true));
      }
      setIsPreviewLoading(true);
      previewRequest(
        getPreviewFormData({
          flowchartInstance,
          query,
          canvasProperties,
          filters,
          hcrDiagramNodesData,
          reportName,
          bandLimits,
          allQueries: queries.menu,
          hcrExportProperties,
          tempUUIDsMap: queryTempuuidsMap.current,
          subDataSets,
          tableStyles
        }),
        (res) => {
          if (streamEnabled) {
            onStreamSuccess(res);
          } else {
            dispatch(hcrActions.handlePreviewTag({ previewTag: res.response, reportKey }));
            dispatch(
              hcrActions.handlePageDetails({
                reportKey,
                pageDetails: {
                  totalPageCount: res?.reportPageInfo?.totalPageCount * 10 || 10,
                  currentPageNo: parseInt(res?.reportPageInfo?.currentPageNo) + 1,
                }
              }),
            );
            setIsPreviewLoading(false);
          }
        },
        (e) => {
          setIsPreviewLoading(false);
          setPreviewError(getErrorMessage(e));
        },
        streamEnabled,
      );
      setParametersReviewFn(filters);
    }
  }

  return (
    <Drawer
      title={<span className="hi-drawer-title">Filters</span>}
      placement={previewParameters?.position?.toLowerCase() || "right"}
      width={"24%"}
      zIndex={1000}
      mask={!isPreviewing}
      getContainer={false}
      className="hcr-filters-drawer"
      onClose={() => {
        dispatch(hcrActions.updateHcrFiltersDrawerStatus());
      }}
      style={{ position: "fixed" }}
      visible={hcrFiltersDrawerStatus}
      extra={
        <Button
          size="small"
          type="primary"
          disabled={!filters?.length}
          onClick={() => {
            applyChanges(filters);
          }}
        >
          Apply
        </Button>
      }
    >
      {hcrFiltersDrawerStatus && (
        <HcrFilters
          reportMode={reportMode}
          setFilters={setFilters}
          filters={filters}
        />
      )}
    </Drawer>
  );
};
export { HCRFiltersDrawer };
