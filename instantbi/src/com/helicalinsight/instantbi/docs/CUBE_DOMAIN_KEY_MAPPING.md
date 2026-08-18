# Cube & Domain Key Mapping

Reference for which keys are used where in cube/domain configuration payloads (dimensions, measures, and domain topic components).

## Dimensions

| Key | Travel Date | Client Name | Payment Mode | Where used |
|---|---|---|---|---|
| `dimensionName` | Travel Date | Client Name | Payment Mode | Cube dimension label |
| `semanticType` | Datetime | Person Name | Category | Semantic typing |
| `tableId` | 1519 | 1517 | 1519 | Source table |
| `columnName` | travel_details.travel_date | meeting_details.client_name | travel_details.mode_of_payment | Physical column |
| `columnId` | 14016 | 14006 | 14024 | Column identity; also referenced in domain `components.id` |
| `defaultFunction` | db.generic.groupBy.group | same | same | Group-by behavior |
| `sort` | Ascending | Ascending | Ascending | Sort order |
| `formatString` | yyyy-mm-dd hh:mm:ss | *(empty)* | *(empty)* | Display format |
| `metric.formula` | *(empty)* | *(empty)* | *(empty)* | Not used for dimensions here |
| `aiContext.synonyms` | Date, Date of Travel | Customer, Customer Names | Mode, Payments | AI synonyms |

## Measures

| Key | Travel Cost | Total Travel | Where used |
|---|---|---|---|
| `measureName` | Travel Cost | Total Travel | Cube measure label |
| `aggregator` | Sum | None | Aggregation mode |
| `columnId` | 14023 | *(empty)* | Physical column id (Travel Cost only) |
| `metricId` | *(not set)* | a0f009ce-d772-4c1d-b8d8-d801bf09cf58 | Calculated metric id (Total Travel only) |
| `tableId` | 1519 | *(empty)* | Source table (Travel Cost only) |
| `columnName` | travel_details.travel_cost | *(empty)* | Physical column (Travel Cost only) |
| `defaultFunction` | db.generic.aggregate.sum | *(empty)* | Sum for Travel Cost |
| `formatString` | $#,##0.00 | 0.00 | Display format |
| `semanticType` | Currency | Number | Semantic typing |
| `metric.formula` | *(empty)* | COUNT(travel_details.travel_id) | Calculated measure definition |
| `aiContext.synonyms` | Cost, Travel Cost, Travel Expense, Total Travel Cost | Travel, Total Travel | AI synonyms |

## Domain → Component Linkage

Domain `components.id` bridges to cube fields:

| Domain component `id` | Name | Points to | Key type in cube |
|---|---|---|---|
| 14023 | Travel Cost | Measure `Travel Cost` | `columnId` |
| 14016 | Travel Date | Dimension `Travel Date` | `columnId` |
| 14006 | Client Name | Dimension `Client Name` | `columnId` |
| 14024 | Payment Mode | Dimension `Payment Mode` | `columnId` |
| a0f009ce-d772-4c1d-b8d8-d801bf09cf58 | Total Travel | Measure `Total Travel` | `metricId` |

## Key Usage Summary

| Key | Used in | Purpose |
|---|---|---|
| `columnId` | Dimensions + physical measure + domain `components.id` | Links physical fields across cube and domain |
| `metricId` | Calculated measure + domain `components.id` | Links formula-based metrics across cube and domain |
| `tableId` | Dimensions + physical measure | Source table reference |
| `columnName` | Dimensions + physical measure | Fully qualified column path |
| `metric.formula` | Calculated measure only | Expression (e.g. `COUNT(...)`) |
| `aggregator` / `defaultFunction` | Measures (and groupBy on dims) | How values are aggregated/grouped |
| `aiContext.synonyms` | All fields | NLP / AI matching aliases |
| `formatString` | Fields with display formatting | UI number/date formatting |

## Rule of Thumb

Domain `components.id` matches cube `columnId` for physical fields, and cube `metricId` for calculated metrics like **Total Travel**.
