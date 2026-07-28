/**
 * Build the minimum HelicalInsight report object that AntV S2 / GridTable needs.
 * Used by Instant BI VF templates so the LLM does not emit a huge report blob
 * (which was truncating under low max_tokens).
 *
 * @param {object} options
 * @param {string[]} [options.dimensions]
 * @param {string[]} [options.measures]
 * @param {Record<string, string>} [options.formatStrings] field name -> Excel format
 * @param {boolean} [options.showTotals]
 * @returns {object} minReport with fields, marksList, metadata, properties, reportData
 */
export function buildGridTableReport({
  dimensions = [],
  measures = [],
  formatStrings = {},
  showTotals = true,
} = {}) {
  const dims = (dimensions || []).map((d) => String(d || "").trim()).filter(Boolean);
  const meas = (measures || []).map((m) => String(m || "").trim()).filter(Boolean);

  const fields = [
    ...dims.map((name, i) => ({
      id: String(i + 1),
      label: name,
      autogen_alias: name,
      alias: name,
      addedAs: i === 0 ? "row" : "column",
      floatingType: "discrete",
      type: { backendDataType: "java.lang.String", dataType: "text" },
      hiddenIncludeInResultSet: false,
    })),
    ...meas.map((name, i) => ({
      id: String(dims.length + i + 1),
      label: name,
      autogen_alias: name,
      alias: name,
      addedAs: "row",
      floatingType: "continous",
      type: { backendDataType: "java.lang.Double", dataType: "numeric" },
      hiddenIncludeInResultSet: false,
    })),
  ];

  const metadataEntry = {};
  fields.forEach((field) => {
    metadataEntry[field.id] = {
      name: field.autogen_alias,
      type: field.floatingType === "continous" ? "numeric" : "text",
    };
  });

  const marksList = [
    {
      value: "_all_",
      id: "marks-all",
      subVizType: "",
      color: { fields: [] },
      size: { fields: [] },
      label: { fields: [] },
      tooltip: { fields: [] },
      shape: { fields: [] },
      detail: { fields: [] },
    },
  ];

  const apply = ["pane", "tooltip", "label", "axis", "actions", "legend"];
  const formatFields = [];
  fields.forEach((field) => {
    const name = field.autogen_alias;
    const fmt =
      formatStrings[name] ||
      Object.entries(formatStrings).find(([key]) => {
        const a = String(key).toLowerCase().replace(/\s+/g, "_");
        const b = name.toLowerCase().replace(/\s+/g, "_");
        return a === b || a.endsWith(`_${b}`) || b.endsWith(`_${a}`);
      })?.[1];
    if (!fmt) return;
    formatFields.push({
      id: field.id,
      values: {
        enableCustomFormatting: true,
        customFormat: String(fmt),
        isApplyClicked: true,
        apply,
        thousandSperator: false,
        decimalPlace: 2,
        prefix: "",
        suffix: "",
        displayUnits: "None",
        percentage: false,
      },
    });
  });

  const titleBase = {
    show: false,
    value: "",
    padding: 0,
    fontSize: 32,
    fontColor: { a: 1, b: 0, g: 0, r: 0 },
    alignment: "center",
    position: "top",
  };
  const properties = {
    title: { ...titleBase },
    subTitle: { ...titleBase, fontSize: 24 },
    format: {
      formatFields,
      formatDatatype: "",
      activeFieldId: "",
      showAll: false,
    },
    formatColor: {
      defaultColor: { r: 84, g: 108, b: 230, a: 1 },
      showAll: false,
      dataColors: [],
      formatColorStyle: "",
      formatColorField: "",
      minimum: { r: 183, g: 192, b: 232, a: 1 },
      maximum: { r: 84, g: 108, b: 230, a: 1 },
      backgroundColor: false,
      enableSteps: false,
      steps: null,
      enableReverse: false,
      minValue: 0,
      maxValue: 0,
      centerValue: 0,
      enableAdvanceSteps: false,
    },
    crosstab: {
      showGrandTotals: showTotals,
      showRowGrandTotals: showTotals,
      showColumnGrandTotals: showTotals,
      showSubTotals: showTotals,
      showRowSubTotals: showTotals,
      showColumnSubTotals: showTotals,
      grandTotalsPosition: "Bottom",
      subTotalsPosition: "Auto",
      crosstabCollapse: "None",
    },
    tooltip: {
      showTooltip: true,
      tooltipTemplate: "",
      isTemplateEdited: false,
      enableTemplate: false,
    },
    axisRange: {
      fields: [],
      activeDatatype: "",
      activeId: "",
      gridLines: [],
      synchronize: false,
      showAxisName: false,
      showGridChartAxisName: true,
    },
  };

  const metadata = [metadataEntry];
  return {
    mode: "create",
    fields,
    marksList,
    metadata,
    properties,
    reportData: { properties },
  };
}
