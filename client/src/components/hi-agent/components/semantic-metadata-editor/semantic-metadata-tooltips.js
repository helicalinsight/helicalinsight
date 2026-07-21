export const SEMANTIC_TOOLTIPS = {
  "Domain Information": "Configure the high-level business context and scope for this semantic model.",
  "Business Domain(s)":
    "Defines the business domain (e.g., Retail, Finance, or HR) to which this entry belongs, used for scoping and categorizing metadata.",
  Description:
    "A short business description of the domain/table/metric. This helps users and the semantic model understand the business intent.",
  "Metric Description":"A short business description for metric.",
  "Table Description":"A short business description for Table.",
  "Overview": "A quick dashboard summary showing the total counts of all configured data components for this semantic model.",
  "Domain name": "A domain organizes your data into business areas (e.g., Sales, HR, Finance). When a user asks a question, the semantic model looks inside the right domain to find the answer faster and more accurately.",
   "Topics":"A focused subject area within a domain (e.g., Meetings, Travel) that groups related data together, helping the semantic model answer questions more accurately.",
  "Database table": "A table is like a spreadsheet it stores your raw data in rows and columns. Add the table(s) here so the semantic model knows where to find the data for your metric.",
  "Unique identifier (PK)":
    "The unique column that identifies each row (e.g., user_id, order_id). No two rows should share the same value in this column.",
  "Refresh frequency":
    "Table data is refreshed from the database. Use Daily for most cases, Hourly for fast-changing data, or Weekly for data that rarely changes.",
  "Topic name(s)":
    "Provide a name or tag for the concepts covered in this table",
  "Metric name": "Use a clear, descriptive name that reflects what you're measuring (e.g., total_revenue, monthly_active_users).",
  Formula: "Define the calculation used to compute this metric. Reference columns from your source tables using aggregation functions (e.g., SUM(order_amount), AVG(total_price)or other complex formula).",
  "Filter (optional)":
    "Only calculate this metric for records that match your condition (e.g., status = 'completed').",
  "Source Tables":
    "Tell the semantic model where your data lives. Add the database table(s) that contain the columns used in your formula. So it knows exactly where to look when calculating your metric.",
  "Topic - Components":
    "Map a topic to the components (tables/metrics/etc.) that should be considered first.",
  Columns:
    "Column-level descriptions and semantic types used for better interpretation and matching.",
  Synonyms:
    "Alternate words users might say when referring to this table or column. For example, if your table is named usr_mstr_tb, add synonyms like users, customers, or clients so the semantic model understands natural language questions correctly.",
  "Domains - Topics":
    "Organize your data into business domains (e.g., Sales, HR) and topics (e.g., Meetings, Travel). This helps the semantic model route user questions to the right data quickly and accurately.",
  "Topic - Components":
    "Map each topic to its database tables (e.g., Travel → travel_details). This helps the semantic model query only the relevant data, making answers faster and more accurate.",
  Topic: "Select a business subject area (e.g., Meetings, Travel) and link it to the database tables that hold that data. This tells the semantic model exactly where to look when users ask questions about that topic.",
  Components:
    "Select the database tables that belong to this topic so the semantic model knows exactly where to fetch data from when answering related questions.",
  Examples :"Examples teach the semantic model how to translate plain language into database values. The more examples you add, the more accurately the semantic model answers user questions."
};

export function getSemanticTooltipText(label) {
  if (!label) return "";
  const key = String(label);
  return SEMANTIC_TOOLTIPS[key] || "";
}

