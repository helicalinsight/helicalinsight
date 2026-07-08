export const CUBE_EDITOR_TOOLTIPS = {
  Domain:
    "The business domain this cube belongs to (e.g., Sales, Finance). It scopes fields and helps route questions to the right data.",
  "Cube Description":
    "A short summary of what this cube contains and how it should be used. Required when saving the cube.",
  "Agent Description":
    "A short summary of what this agent covers and how it should be used. Required when saving the agent.",
  Fields:
    "Display name for each column or hierarchy member in the cube. Right-click for more field actions; double-click the title to rename hierarchies.",
  Sort:
    "Enable sorting on this field and choose ascending or descending order from the menu.",
  Aggregation:
    "Choose how values roll up (e.g., Sum, Count) when this field is used as a measure in reports.",
  Partition:
    "Mark a field for partitioning when you need data split or grouped along this dimension.",
  Dimension:
    "Treat this column as a dimension for grouping and filtering in analyses.",
  Measure:
    "Treat this column as a numeric measure. Configure data type and display format from the menu.",
  "Semantic Type":
    "Classify the column (e.g., currency, email) so agents and reports interpret values correctly.",
  Synonyms:
    "Alternate names users might say for this field. Enter comma-separated values (e.g., users, clients).",
  Topic:
    "Business topic for this field. Enter comma-separated values (e.g., Sales, Travel).",
  "Agent Topic":
    "Business topics for this agent. Enter comma-separated values (e.g., Sales, Travel).",
  Formula:
    "Define the calculation or expression for this field. Reference columns using cube notation (e.g., [Measures].[Amount]) or aggregation functions (e.g., SUM, AVG).",
  "Filter (optional)":
    "Only calculate or include this field for records that match your condition (e.g., status = 'completed').",
  Example:
    "Examples teach the agent how to map plain language to database values (e.g., term -> column or value). Add more examples for better matching.",
  Description:
    "A short business description of this field so users and the agent understand its meaning and usage.",
  "Dimension/Measure":
    "Toggle between Dimension and Measure. Dimensions are used for grouping and filtering; Measures are numeric values that can be aggregated. Use the menu (⋯) to configure data type and display format when Measure is selected.",
  "Clear field values": "Clear the field values from the cube",
  "Remove field": "Removes this field from the cube",
};

export const AGENT_EDITOR_TOOLTIPS = {
  Domain:
    "A domain organizes your data into business areas (e.g., Sales, HR, Finance). When a user asks a question, the agent looks inside the right domain to find the answer faster and more accurately.",
  Fields:
    "Display name for each column in your agent metadata. Right-click for more field actions.",
  Aggregation:
    "Choose how values roll up (e.g., Sum, Count) when this field is used as a measure.",
  "Semantic Type":
    "Classify the column (e.g., currency, email) so the agent interprets values correctly.",
  Formula:
    "Define the calculation used for this field. Reference columns from your source tables using aggregation functions (e.g., SUM(order_amount), AVG(total_price)).",
  "Dimension/Measure":
    "Toggle between Dimension and Measure. Dimensions are used for grouping and filtering; Measures are numeric values that can be aggregated. Use the menu (⋯) to configure data type and display format when Measure is selected.",
  "Agent Topic":
    "Business topics for this agent. Enter comma-separated values (e.g., Sales, Travel).",
  Agent:
    "The name of this agent file (e.g., Agent_1). This is used when saving the agent.",
  "Clear field values": "Clear the field values for this field",
  "Remove field": "Removes this field from the agent metadata",
};

export function getCubeEditorTooltipText(label, variant = "cube") {
  if (!label) return "";
  const key = String(label);
  if (variant === "agent") {
    return AGENT_EDITOR_TOOLTIPS[key] || CUBE_EDITOR_TOOLTIPS[key] || "";
  }
  return CUBE_EDITOR_TOOLTIPS[key] || "";
}
