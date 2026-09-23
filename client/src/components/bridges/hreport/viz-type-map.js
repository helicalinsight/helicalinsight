/**
 * InstantBI viz_model.chart.mark (HI picker labels) → HelicalReports selectedType.
 *
 * Python emits picker display names ("Grid Table", "Maps", "Chart").
 * HI runtime uses internal types (S2Chart, MapChart, Antcharts).
 */
const MARK_TO_SELECTED_TYPE = {
  table: "Table",
  chart: "Antcharts",
  antcharts: "Antcharts",
  card: "Card",
  kpi: "Card",
  maps: "MapChart",
  mapchart: "MapChart",
  "grid table": "S2Chart",
  gridtable: "S2Chart",
  s2chart: "S2Chart",
  "grid chart": "GridChart",
  gridchart: "GridChart",
  vf: "VF",
};

export function resolveHreportSelectedType(markOrType) {
  const raw = String(markOrType || "").trim();
  if (!raw) {
    return "";
  }
  const spaced = raw.toLowerCase().replace(/\s+/g, " ");
  const compact = spaced.replace(/\s+/g, "");
  return MARK_TO_SELECTED_TYPE[spaced] || MARK_TO_SELECTED_TYPE[compact] || "";
}
