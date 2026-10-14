import { cloneDeep } from "lodash-es";
import {
  designerSettingsConstants,
  gridSettingsConstants,
} from "../../hi-dashboard-designer/utils/config-dashboard-gridSettings";
import { makeid } from "../../hi-dashboard-designer/utils/common-functions";
import { initialConfig } from "../../hi-dashboard-designer/utils/constants";
import { DASHBOARD_CREATE_TILE, matchStepReportModel } from "../../bridges/dashboard/dashboard-bridge";
import { parseCssColor, unwrapConvertDashboardResponse } from "./convert-sql-to-hr-save";
import { INSTANT_EDIT_MODE } from "./save-instant-dashboard";
import { getFieldDisplayName } from "../../../utils/utilities";

const escapeHtml = (value) =>
  String(value || "")
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;");

const toDashboardRgba = (value) => {
  const parsed = parseCssColor(value);
  if (!parsed) {
    return null;
  }
  return { r: parsed.r, g: parsed.g, b: parsed.b, a: 100 };
};

const nextItemId = () => `item-${makeid({})}`;

const layoutEntry = ({ id, x = 0, y = 0, w = 6, h = 4 }) => {
  const width = Number(w);
  const height = Number(h);
  return {
    i: id,
    x: Number(x) || 0,
    y: Number(y) || 0,
    w: width > 0 ? width : 6,
    h: height > 0 ? height : 4,
    moved: false,
    static: false,
  };
};

const applyTheme = (gridItemConfig, theme = {}) => {
  const color = toDashboardRgba(theme.color);
  const background = toDashboardRgba(theme.background);
  return (gridItemConfig || []).map((entry) => {
    if (entry.key === "header" && color) {
      return {
        ...entry,
        values: {
          ...entry.values,
          backgroundColor: color,
        },
      };
    }
    if (entry.key === "background" && background) {
      return {
        ...entry,
        values: {
          ...entry.values,
          enable: true,
          backgroundColor: background,
        },
      };
    }
    return entry;
  });
};

const createGridItem = ({ id, layout, headerTitle = "", text = "", theme, showHeader = true, compType = "" }) => {
  const position = layoutEntry({
    id,
    ...layout,
    w: layout?.w > 0 ? layout.w : 6,
    h: layout?.h > 0 ? layout.h : 4,
  });
  const gridItem = {
    id,
    compType: compType || "text",
    isGrouped: false,
    isSaved: false,
    initialPosition: { ...position, static: false },
    gridItemConfig: applyTheme(initialConfig({ reportName: headerTitle, compType: "text", id }), theme),
    children: [],
    layout: [],
  };
  const headerCfg = gridItem.gridItemConfig.find((entry) => entry.key === "header");
  if (headerCfg) {
    headerCfg.values = {
      ...headerCfg.values,
      enable: Boolean(showHeader),
      title: showHeader ? headerTitle : "",
    };
  }
  const editConfig = gridItem.gridItemConfig.find((entry) => entry.key === "edit");
  if (editConfig) {
    editConfig.values = {
      ...editConfig.values,
      enable: true,
      text: text || `<p></p>`,
    };
  }
  return { gridItem, layout: position };
};

export const sanitizeDashboardSvg = (svg = "") => {
  const text = String(svg || "").trim();
  if (!text || !/<svg[\s>]/i.test(text)) {
    return "";
  }
  if (/<script|on\w+\s*=|javascript:|foreignObject|<iframe|xlink:href\s*=\s*["'](?!#)/i.test(text)) {
    return "";
  }
  return text.slice(0, 4000);
};

const layoutFromDashboardModel = (model = {}) => {
  const box = model.layout || model;
  return {
    x: Number(box.x) || 0,
    y: Number(box.y) || 0,
    w: Number(box.w || box.width) || 6,
    h: Number(box.h || box.height) || 4,
  };
};

const HEADERLESS_KINDS = new Set(["kpi", "filter", "svg", "image", "separator"]);

const tileTitle = (item = {}) => {
  const model = item || {};
  const vizModel = item.report_model?.viz_model || item.viz_model || {};
  const kind = String(model.kind || "viz").toLowerCase();
  // if (HEADERLESS_KINDS.has(kind)) {
  //   return "";
  // }
  return String(
    model.title ||
    vizModel.properties?.title ||
    item.user_query ||
    (kind === "summary" ? "Summary" : "") ||
    "Chart"
  ).trim();
};

const tileBody = (tile = {}) => {
  const kind = String(tile.kind || "").toLowerCase();
  const svg = sanitizeDashboardSvg(tile.html);
  if (svg || kind === "svg" || kind === "image" || kind === "separator") {
    return svg || `<p></p>`;
  }
  const raw = String(tile.html || "").trim();
  if (kind === "summary" && raw) {
    return raw.includes("<") ? raw : `<p>${escapeHtml(raw)}</p>`;
  }
  if (kind === "filter") {
    return `<p>Filter</p>`;
  }
  if (kind === "kpi") {
    const label = String(tile.label || tile.title || "Key metric").trim();
    return `<p>${escapeHtml(label)}</p>`;
  }
  return `<p></p>`;
};

const skeletonTiles = (parts = {}, requestItems) => {
  const items = requestItems || parts.items || [];
  if (items.some((item) => item)) {
    return items.map((item) => {
      const model = item || {};
      return {
        title: tileTitle(item),
        label: model.title || model.column || "",
        kind: String(model.kind || "viz"),
        html: model.html || "",
        ...layoutFromDashboardModel(model),
      };
    });
  }
  const tiles = [];
  const summary = parts.summary || {};
  if (String(summary.text || "").trim()) {
    tiles.push({
      title: "Summary",
      kind: "summary",
      html: summary.text,
      x: 0,
      y: 0,
      w: 12,
      h: String(summary.text).length > 140 ? 2 : 1,
    });
  }
  (parts.layout || []).forEach((row) => {
    const source = (items || []).find((entry) =>
      [entry?.id, entry?.component_id]
        .map((value) => String(value || ""))
        .includes(String(row.itemId))
    );
    tiles.push({
      title: tileTitle(source || { viz: { chart_name: "Chart" } }),
      kind: "viz",
      html: "",
      x: row.x,
      y: row.y,
      w: row.w,
      h: row.h,
    });
  });
  return tiles;
};

export const assembleInstantDashboardConfig = ({ payload = {}, items: requestItems } = {}) => {
  const parts = unwrapConvertDashboardResponse(payload);
  const theme = parts.theme || {};
  const tiles = skeletonTiles(parts, requestItems || parts.items);
  const gridItemsData = [];
  const layout = [];

  tiles.forEach((tile) => {
    const id = nextItemId();
    const built = createGridItem({
      id,
      layout: tile,
      headerTitle: tile.title,
      text: tileBody(tile),
      theme,
      showHeader: !HEADERLESS_KINDS.has(String(tile.kind || "").toLowerCase()),
    });
    gridItemsData.push(built.gridItem);
    layout.push(built.layout);
  });

  return {
    gridSettings: cloneDeep(gridSettingsConstants),
    gridItemsData,
    layout,
    designerLayout: layout,
    dashboardUUID: "",
    dashboardVariables: {},
    designerMode: INSTANT_EDIT_MODE,
    components: {},
    parameterDrawerStatus: false,
    gridIndex: 0,
    designerSettings: cloneDeep(designerSettingsConstants),
    savedReportName: "",
    css: "",
    script: "",
  };
};

const DASHBOARD_GRID_COLS = 12;

const clampSpan = (value, fallback) => {
  const parsed = Number(value);
  if (!Number.isFinite(parsed) || parsed <= 0) return fallback;
  return Math.max(1, Math.min(DASHBOARD_GRID_COLS, Math.round(parsed)));
};

const getDashboardItemId = (item) => {
  const { component_id = "" } = item || {};
  if (!component_id) return nextItemId();
  const [prefix, suffix] = String(component_id || "").split("-");
  if (!prefix) return nextItemId();
  if (!suffix) return `item-${prefix}`;
  return component_id;
}

const getChatSeqId = (dashboardItem) => {
  const { chat_sequence_id } = dashboardItem || {};
  return chat_sequence_id || "";
}

const getQuestionHistory = (questionsHistory = [], dashboardItem = {}) => {
  const chatSeqId = getChatSeqId(dashboardItem);
  return questionsHistory.find((q) => q.chat_seq_id === chatSeqId) || {};
}

export const createDashboardCreateItem = ({ dashboardItem = {}, reportModel = {}, theme, questionsHistory = [] } = {}) => {
  const id = getDashboardItemId(dashboardItem);
  const {
    report_model,
    fullChatResponse,
    dimensions,
    data,
    ...restQHistory
  } = getQuestionHistory(questionsHistory, dashboardItem)
  const hreportId = restQHistory.hreportId || restQHistory?.chat_response?.hreportId || fullChatResponse?.hreportId || "";
  const model = dashboardItem || {};
  const box = model.layout || {};
  const position = layoutEntry({
    id,
    x: Number(box.x) || 0,
    y: Number(box.y) || 0,
    w: clampSpan(box.w ?? box.width, 6),
    h: clampSpan(box.h ?? box.height, 4),
  });
  const title = tileTitle({ ...dashboardItem, report_model: reportModel });
  const gridItem = {
    id,
    compType: DASHBOARD_CREATE_TILE,
    isGrouped: false,
    isSaved: false,
    initialPosition: { ...position, static: false },
    gridItemConfig: applyTheme(
      initialConfig({ reportName: title, compType: DASHBOARD_CREATE_TILE, id }),
      theme
    ),
    children: [],
    layout: [],
    reportInfo: {
      inline: true,
      mode: "dashboard-create",
      extension: "hr",
      component_id: dashboardItem.component_id || id,
      dashboardItemId: id,
      kind: model.kind || "instant",
      title,
      layout: model.layout || {},
      header: model.header || {},
      file: {
        inline: true,
        dashboardItemId: id,
        title,
        instant_item: restQHistory,
      },
      hreportId,
    },
  };
  const headerCfg = gridItem.gridItemConfig.find((entry) => entry.key === "header");
  if (headerCfg) {
    headerCfg.values = {
      ...headerCfg.values,
      enable: true,
      title,
    };
  }
  return { gridItem, layout: position };
};

export const createDashboardTextItem = ({ dashboardItem = {}, theme } = {}) => {
  const id = nextItemId();
  const model = dashboardItem || {};
  let { gridItem, layout } = createGridItem({
    id,
    layout: layoutFromDashboardModel(model),
    headerTitle: tileTitle(dashboardItem),
    text: "<p></p>",
    theme,
    showHeader: true,
    compType: "html-component",
  });

  gridItem = {
    ...gridItem,
    gridItemConfig: gridItem.gridItemConfig.map(({ key, values }) => {
      if (key === "html") {
        return {
          key,
          values: {
            ...values,
            enable: true,
            value: model.html || "",
          }
        }
      }
      return { key, values };
    })
  }
  return { gridItem, layout };
};

export const createDashboardOtherItem = ({ dashboardItem = {}, theme } = {}) => {
  const id = `item-${dashboardItem?.component_id || dashboardItem?.itemId || nextItemId()}`;
  const model = dashboardItem || {};
  return createGridItem({
    id,
    layout: layoutFromDashboardModel(model),
    headerTitle: tileTitle(dashboardItem),
    text: "<p></p>",
    theme,
    showHeader: true,
  });
};

export const createDashboardFilterItem = ({ dashboardItem = {}, theme, instantItems = [], questionsHistory = [], index, reports = [] } = {}) => {
  const id = `item-${dashboardItem?.component_id || dashboardItem?.itemId || nextItemId()}`;
  const model = dashboardItem || {};
  const { sourceItemId } = model || {};

  if (sourceItemId) {
    const sourceItem = instantItems.find((item) => item.component_id === sourceItemId);
    if (sourceItem) {
      const {
        report_model,
        fullChatResponse,
        dimensions,
        data,
        ...restQHistory
      } = getQuestionHistory(questionsHistory, sourceItem)
      const title = tileTitle(dashboardItem);
      const hreportId = restQHistory.hreportId || restQHistory?.chat_response?.hreportId || fullChatResponse?.hreportId || "";

      const box = model.layout || {};
      const position = layoutEntry({
        id,
        x: Number(box.x) || 0,
        y: Number(box.y) || 0,
        w: clampSpan(box.w ?? box.width, 6),
        h: clampSpan(box.h ?? box.height, 4),
      });
      const gridItem = {
        id,
        compType: "filter-component",
        isGrouped: false,
        isSaved: false,
        initialPosition: { ...position, static: false },
        gridItemConfig: applyTheme(
          initialConfig({ reportName: title, compType: "filter-component", id }),
          theme
        ),
        layout: [],
        reportInfo: {
          inline: true,
          mode: "filter",
          filterType: "instant",
          extension: "hr",
          component_id: dashboardItem.component_id || id,
          sourceItemId: model.sourceItemId,
          dashboardItemId: id,
          kind: model.kind || "instant.filter",
          title,
          column: model.column,
          table: model.table,
          layout: model.layout || {},
          header: model.header || {},
          file: {
            inline: true,
            dashboardItemId: id,
            title,
            instant_item: restQHistory,
          },
          hreportId,
        },
        index,
        key: String(index + 1)
      };


      const report = reports.find((report) => report.id === hreportId) || {};
      const filter = report?.filters?.find(({ alias = "", label = "", autogen_alias = "" }) => [alias, label, autogen_alias].includes(model.column));
      if (filter) {
        gridItem.parameter = {
          dashboardFilter: {
            columnName: getFieldDisplayName(filter),
            uid: filter.uid,
          },
        }
        gridItem.listeners = [getFieldDisplayName(filter)]
      }

      const headerCfg = gridItem.gridItemConfig.find((entry) => entry.key === "header");
      if (headerCfg) {
        headerCfg.values = {
          ...headerCfg.values,
          enable: false,
          title,
        };
      }
      return { gridItem, layout: position };
    }
  }

  return createGridItem({
    id,
    layout: layoutFromDashboardModel(model),
    headerTitle: tileTitle(dashboardItem),
    text: "<p></p>",
    theme,
    showHeader: true,
  });
};

export const createDashboardApplyResetItem = ({ dashboardItem = {}, theme } = {}) => {
  const id = nextItemId();
  const model = dashboardItem || {};
  return createGridItem({
    id,
    layout: layoutFromDashboardModel(model),
    headerTitle: tileTitle(dashboardItem),
    text: "<p></p>",
    theme,
    showHeader: false,
    compType: "apply-reset"
  });
};

export const assembleInstantDashboardCreateConfig = ({ dashboardModel = {}, steps = [], theme, questionsHistory = [], isStatic = true, reports = [] } = {}) => {
  const base = assembleInstantDashboardConfig({ payload: {}, items: [] });
  const gridItemsData = [...base.gridItemsData];
  const layout = [...base.layout];
  const pushBuilt = (built) => {
    built.layout.static = isStatic;
    if (built.gridItem.initialPosition) {
      built.gridItem.initialPosition.static = isStatic;
    }
    gridItemsData.push(built.gridItem);
    layout.push(built.layout);
  };
  const items = Array.isArray(dashboardModel.items) ? dashboardModel.items : [];
  const instantItems = items.filter((item) => item.kind === "instant");
  const otherItems = items.filter((item) => item?.kind !== "instant");
  let cursor = 0;
  [...instantItems, ...otherItems].forEach((dashboardItem = {}, index) => {
    const kind = String(dashboardItem?.kind || "").toLowerCase();
    if (kind === "instant") {
      const { reportModel } = matchStepReportModel({ item: dashboardItem, steps, cursor });
      cursor += 1;
      if (reportModel && Object.keys(reportModel).length) {
        pushBuilt(createDashboardCreateItem({ dashboardItem, reportModel, theme, questionsHistory, reports }));
      } else {
        pushBuilt(createDashboardTextItem({ dashboardItem, theme }));
      }
      return;
    }
    if (kind === "text") {
      pushBuilt(createDashboardTextItem({ dashboardItem, theme }));
      return
    }
    if (kind === "instant.filter") {
      const filterGridItem = createDashboardFilterItem({ dashboardItem, theme, instantItems, questionsHistory, index, reports });
      pushBuilt(filterGridItem);
      pushBuilt({
        gridItem: {
          ...(cloneDeep(filterGridItem.gridItem)),
          compType: "filter",
          filterCompId: filterGridItem.gridItem.id,
          id: nextItemId()
        },
        layout: filterGridItem.layout
      });
      return
    }
    if (kind === "apply-reset") {
      pushBuilt(createDashboardApplyResetItem({ dashboardItem, theme }));
      return
    }
    pushBuilt(createDashboardOtherItem({ dashboardItem, theme }));
  });
  if (gridItemsData.some((item) => ["filter-component", "filter"].includes(item.compType))) {
    base.designerSettings = base.designerSettings.map((item) => {
      if (item.key === "parameters") {
        item.values = {
          ...item.values,
          floatingFilter: true
        }
      }
      return item
    })
  }
  return {
    ...base,
    gridItemsData,
    layout,
    designerLayout: layout,
  };
};

export const getGridItemsFiltersSettings = (gridItemsData, reports) => {
  const gridItemsFiltersInfo = [];
  gridItemsData.forEach((gridItem) => {
    if (gridItem.compType === DASHBOARD_CREATE_TILE) {
      const { reportInfo } = gridItem || {};
      const { hreportId = "" } = reportInfo || {};
      const report = reports.find((report) => report.id === hreportId);
      if (report && report?.filters.length) {
        gridItemsFiltersInfo.push({ filtersList: report.filters, designerItemId: gridItem.id, reportId: hreportId })
      }
    }
    return;
  })
  return gridItemsFiltersInfo;
}