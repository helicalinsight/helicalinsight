import { cloneDeep } from "lodash-es";
import {
  designerSettingsConstants,
  gridSettingsConstants,
} from "../../hi-dashboard-designer/utils/config-dashboard-gridSettings";
import { makeid } from "../../hi-dashboard-designer/utils/common-functions";
import { initialConfig } from "../../hi-dashboard-designer/utils/constants";
import { parseCssColor, unwrapConvertDashboardResponse } from "./convert-sql-to-hr-save";
import { INSTANT_EDIT_MODE } from "./save-instant-dashboard";

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

const createGridItem = ({ id, layout, headerTitle = "", text = "", theme, showHeader = true }) => {
  const position = layoutEntry({
    id,
    ...layout,
    w: layout?.w > 0 ? layout.w : 6,
    h: layout?.h > 0 ? layout.h : 4,
  });
  const gridItem = {
    id,
    compType: "text",
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
  const model = item.dashboard_model || {};
  const vizModel = item.report_model?.viz_model || item.viz_model || {};
  const kind = String(model.kind || "viz").toLowerCase();
  if (HEADERLESS_KINDS.has(kind)) {
    return "";
  }
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
  if (items.some((item) => item?.dashboard_model)) {
    return items.map((item) => {
      const model = item.dashboard_model || {};
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
