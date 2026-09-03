import { cloneDeep } from "lodash-es";
import requests from "../../../base/requests";
import { getSaveData } from "../../hi-reports/utils/base";
import { getIntialReportState } from "../../../redux/reducers/initialStates";
import { isSavingDesigner } from "../../../redux/actions/dashboard-designer.actions";

export const INSTANT_EDIT_MODE = "instant-edit";

export const isInstantEditMode = (designerMode) => designerMode === INSTANT_EDIT_MODE;

export const shouldConfirmInstantChartSaves = ({
  designerMode,
  dashboardUUID,
  drafts = [],
} = {}) =>
  isInstantEditMode(designerMode) && !dashboardUUID && (drafts || []).length > 0;

export const sanitizeInstantReportName = (value = "") => {
  const cleaned = String(value || "")
    .replace(/\.hr$/i, "")
    .replace(/[\\/:*?"<>|]+/g, "_")
    .replace(/\s+/g, "_")
    .replace(/_+/g, "_")
    .replace(/^_+|_+$/g, "")
    .slice(0, 80);
  return cleaned || "Chart";
};

export const uniqueInstantReportName = (title, usedNames = new Set()) => {
  const base = sanitizeInstantReportName(title);
  let name = base;
  let index = 2;
  while (usedNames.has(name.toLowerCase())) {
    name = `${base}_${index}`;
    index += 1;
  }
  usedNames.add(name.toLowerCase());
  return name;
};

export const chartTitleFromGridItem = (item = {}) => {
  const header = (item.gridItemConfig || []).find((entry) => entry.key === "header");
  return (
    header?.values?.title ||
    item.reportInfo?.file?.title ||
    item.reportInfo?.file?.reportName ||
    "Chart"
  );
};

export const isInlineDashboardChart = (item = {}) =>
  item?.compType === "dashboard-designer-component" &&
  Boolean(item?.reportInfo?.inline || item?.reportInfo?.file?.inline);

export const buildInstantChartSaveDrafts = (gridItemsData = []) => {
  const usedNames = new Set();
  return (gridItemsData || []).filter(isInlineDashboardChart).map((item) => ({
    id: item.id,
    name: uniqueInstantReportName(chartTitleFromGridItem(item), usedNames),
  }));
};

export const namesByItemIdFromDrafts = (drafts = []) =>
  (drafts || []).reduce((acc, row) => {
    if (row?.id) {
      acc[row.id] = row.name;
    }
    return acc;
  }, {});

const activeReportFromInlineFile = (file = {}, { location, reportName }) => {
  const report = cloneDeep(getIntialReportState({ active: true }));
  report.mode = "create";
  report.metadata = file.metadata || {};
  report.fields = cloneDeep(file.fields || []);
  report.filters = cloneDeep(file.hydratedFilters || []);
  report.selectedType = file.selectedType || file.visualisationType || report.selectedType;
  report.properties = file.properties || report.properties;
  report.marksList = file.marksList || report.marksList;
  report.reportInfo = {
    ...(report.reportInfo || {}),
    location,
    reportName,
  };
  return report;
};

const fileRefFromSaveResponse = (res = {}, { location, reportName }) => {
  const name = res.data?.name || `${reportName}.hr`;
  const path = res.data?.path || `${String(location || "").replace(/[\\/]+$/, "")}/${name}`;
  return {
    path,
    name,
    title: res.data?.title || reportName,
  };
};

export const applySavedChartFileRefs = (gridItemsData = [], savedByItemId = {}) =>
  (gridItemsData || []).map((item) => {
    const file = item?.reportInfo?.file;
    if (!file?.inline && !item?.reportInfo?.inline) {
      return item;
    }
    const sourceId = file?.dashboardItemId || item.id;
    const saved = savedByItemId[sourceId];
    if (!saved) {
      return item;
    }
    return {
      ...item,
      reportInfo: {
        ...item.reportInfo,
        inline: false,
        resourceId: saved.uuid,
        file: {
          ...saved.file,
        },
      },
    };
  });

export const saveHrReportFromInlineFile = ({
  dispatch,
  file,
  location,
  reportName,
}) =>
  new Promise((resolve, reject) => {
    try {
      const saveData = getSaveData(
        activeReportFromInlineFile(file, { location, reportName })
      );
      requests.hreport(dispatch).saveReport(
        saveData,
        "",
        (res) => {
          if (!res?.uuid) {
            reject(new Error("Could not save chart report."));
            return;
          }
          resolve({
            uuid: res.uuid,
            file: fileRefFromSaveResponse(res, { location, reportName }),
          });
        },
        (err) => {
          reject(err || new Error("Could not save chart report."));
        }
      );
    } catch (error) {
      reject(error);
    }
  });

export const persistInstantDashboardChartReports = async ({
  dispatch,
  gridItemsData = [],
  location,
  Notify,
  reportNamesByItemId = {},
}) => {
  if (!location) {
    Notify?.error?.({
      type: "Frontend",
      message: "Please select a folder to save the dashboard.",
    });
    return { ok: false, gridItemsData };
  }
  const charts = (gridItemsData || []).filter(isInlineDashboardChart);
  if (!charts.length) {
    return { ok: true, gridItemsData };
  }
  dispatch(isSavingDesigner(true));
  const usedNames = new Set();
  const savedByItemId = {};
  try {
    for (const item of charts) {
      const reportName = uniqueInstantReportName(
        reportNamesByItemId[item.id] || chartTitleFromGridItem(item),
        usedNames
      );
      const saved = await saveHrReportFromInlineFile({
        dispatch,
        file: item.reportInfo.file,
        location,
        reportName,
      });
      savedByItemId[item.id] = saved;
    }
    return {
      ok: true,
      gridItemsData: applySavedChartFileRefs(gridItemsData, savedByItemId),
    };
  } catch (error) {
    Notify?.error?.({
      type: "Frontend",
      message: error?.message || "Could not save chart reports for this dashboard.",
    });
    dispatch(isSavingDesigner(false));
    return { ok: false, gridItemsData };
  }
};
