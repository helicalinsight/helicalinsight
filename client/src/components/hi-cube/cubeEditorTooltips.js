export const CUBE_EDITOR_TOOLTIPS = {
  Domain:
    "The business domain this cube belongs to (e.g., Sales, Finance). It scopes fields and helps route questions to the right data.",
  "Cube Description":
    "A short summary of what this cube contains and how it should be used. Required when saving the cube.",
  "Semantic Model Description":
    "A short summary of what this semantic model covers and how it should be used. Required when saving the semantic model.",
  Fields:
    "Display name for each column or hierarchy member in the cube. Right-click for more field actions; double-click the title to rename hierarchies.",
  Sort:
    "Enable sorting on this field and choose ascending or descending order from the menu.",
  Aggregation:
    "Choose how values roll up (e.g., Sum, Count) when this field is used as a measure in reports.",
  Partition:
    "Mark a field for partitioning when you need data split or grouped along this dimension.",
  Dimension:
    "Convert this dimension into a measure so it can be aggregated.",
  Measure:
    "Convert this measure into a dimension.",
  "Semantic Type":
    "Classify the column (e.g., currency, email) so semantic models and reports interpret values correctly.",
  Synonyms:
    "Alternate names users might say for this field. Enter comma-separated values (e.g., users, clients).",
  Instructions:
    "Guidance for how the semantic model should interpret or use this field when answering questions.",
  Examples:
    "Examples teach the semantic model how to map plain language to database values (e.g., term -> column or value). Add more examples for better matching.",
  "AI Context":
    "Extra context that helps the semantic model understand this field: instructions, synonyms, and examples.",
  Topic:
    "Business topic for this field. Enter comma-separated values (e.g., Sales, Travel).",
  "Semantic Model Topic":
    "Business topics for this semantic model. Enter comma-separated values (e.g., Sales, Travel).",
  Formula:
    "Define the calculation or expression for this field. Reference columns using cube notation (e.g., [Measures].[Amount]) or aggregation functions (e.g., SUM, AVG).",
  "Filter (optional)":
    "Only calculate or include this field for records that match your condition (e.g., status = 'completed').",
  Example:
    "Examples teach the semantic model how to map plain language to database values (e.g., term -> column or value). Add more examples for better matching.",
  Description:
    "A short business description of this field so users and the semantic model understand its meaning and usage.",
  "Dimension/Measure":
    "A Dimension lets you group and filter; a Measure is a number you can total or average. Expand to set Sort for Dimensions, or Format and Aggregation for Measures.",
  "Clear field values": "Clear the field values from the cube",
  "Remove field": "Removes this field from the cube",
};

export const AGENT_EDITOR_TOOLTIPS = {
  Domain:
    "A domain organizes your data into business areas (e.g., Sales, HR, Finance). When a user asks a question, the semantic model looks inside the right domain to find the answer faster and more accurately.",
  "Domain Description":
    "Explain what this domain covers (e.g., Sales performance, employee records). Helps the semantic model choose the right business area for a question.",
  "Topic Description":
    "Explain what this topic covers within the domain (e.g., Orders, Returns). Helps the semantic model narrow questions to the right group of fields.",
  "Delete Domain":
    "Removes this domain and all of its topics from the Business View. Fields stay in the Fields list; only the domain grouping is deleted.",
  "Delete Topic":
    "Removes this topic from the domain. Fields stay in the Fields list; only the topic grouping is deleted.",
  "Remove field from topic":
    "Removes this field from this topic only. The field remains available in the Fields list and in any other topics it belongs to.",
  Fields:
    "Display name for each column in your semantic model metadata. Right-click for more field actions.",
  Aggregation:
    "Choose how values roll up (e.g., Sum, Count) when this field is used as a measure.",
  "Semantic Type":
    "Classify the column (e.g., currency, email) so the semantic model interprets values correctly.",
  Formula:
    "Define the calculation used for this field. Reference columns from your source tables using aggregation functions (e.g., SUM(order_amount), AVG(total_price)).",
  Instructions:
    "Guidance for how the semantic model should interpret or use this field when answering questions.",
  Synonyms:
    "Alternate names users might say for this field. Enter comma-separated values (e.g., users, clients).",
  Examples:
    "Examples teach the semantic model how to map plain language to database values (e.g., term -> column or value).",
  "AI Context":
    "Extra context that helps the semantic model understand this field: instructions, synonyms, and examples.",
  "Dimension/Measure":
    "A Dimension lets you group and filter; a Measure is a number you can total or average. Expand to set Sort for Dimensions, or Format and Aggregation for Measures.",
  "Semantic Model Topic":
    "Business topics for this semantic model. Enter comma-separated values (e.g., Sales, Travel).",
  Topic:
    "A topic groups related fields inside a domain (e.g., Orders under Sales). Drop fields onto a topic to assign them. Drag a hierarchy to assign all of its columns at once. The same field can belong to multiple topics.",
  "Business Domain":
    "A business domain organizes your data into areas like Sales, HR, or Finance. Add topics inside a domain to group related fields. Right-click the domain name to add a description.",
  "Business Topic":
    "A business topic groups related fields inside a domain (e.g., Orders under Sales). Drop fields from the Fields list onto a topic to assign them. Drag a hierarchy to assign all of its columns at once. The same field can belong to multiple topics. Right-click the topic name to add a description.",
  "Semantic Model":
    "The name of this semantic model file (e.g., Model_1). This is used when saving the semantic model.",
  "Delete Description":
    "Clears only the description text. The domain or topic itself is not deleted.",
  "Clear Description": "Clears the description text for this domain or topic.",
  "Clear field values": "Clear the field values for this field",
  "Remove field": "Removes this field from the semantic model metadata",
};

export function getCubeEditorTooltipText(label, variant = "cube") {
  if (!label) return "";
  const key = String(label);
  if (variant === "agent") {
    return AGENT_EDITOR_TOOLTIPS[key] || CUBE_EDITOR_TOOLTIPS[key] || "";
  }
  return CUBE_EDITOR_TOOLTIPS[key] || "";
}
