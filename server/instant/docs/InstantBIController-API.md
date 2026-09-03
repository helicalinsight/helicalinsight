# InstantBIController API Reference

API documentation for all endpoints in `com.helicalinsight.instant.ai.InstantBIController`.

## Common Details

| Item | Value |
|------|--------|
| **Base path** | `{baseUrl}/ai/...` (e.g. `https://host/hi/ai/...`) |
| **Methods** | `GET` or `POST` |
| **Content type** | Query params (GET) or `application/x-www-form-urlencoded` (POST) — **not** JSON body |
| **Auth** | Logged-in session (`JSESSIONID` cookie). Roles: `ROLE_USER`, `ROLE_ADMIN`, or `ROLE_VIEWER` |
| **Encoding** | `agent`, `domain`, `subject`, and `formData` may be plain JSON/text or **Base64-encoded** strings |
| **Ajax header** | If the request is treated as Ajax, response is `application/json`; otherwise `text/html` |

---

## 1. Domain Recommendation

**`GET | POST`** `/ai/recommendation/domain`

Suggests a business domain for an AI agent.

### Request Parameters

| Parameter | Required | Type | Description |
|-----------|----------|------|-------------|
| `agent` | Yes | JSON string | Agent config object (see below) |

**`agent` example:**

```json
{
  "dir": "MyFolder",
  "file": "SalesAgent.agent"
}
```

### Example Request

```http
POST /ai/recommendation/domain
Content-Type: application/x-www-form-urlencoded

agent={"dir":"MyFolder","file":"SalesAgent.agent"}
```

### Response

```json
{
  "domain": "Sales Analytics"
}
```

---

## 2. Analyst Question Recommendations

**`GET | POST`** `/ai/recommendation/analyst`

Returns top-N analyst questions for a domain.

### Request Parameters

| Parameter | Required | Type | Default | Description |
|-----------|----------|------|---------|-------------|
| `agent` | Yes | JSON string | — | Agent config object |
| `domain` | Yes | string | — | Domain from `/ai/recommendation/domain` |
| `topN` | No | integer | `10` | Number of questions to return |

### Example Request

```http
POST /ai/recommendation/analyst
Content-Type: application/x-www-form-urlencoded

agent={"dir":"MyFolder","file":"SalesAgent.agent"}&domain=Sales Analytics&topN=5
```

### Response

```json
{
  "questions": [
    "What are total sales by region?",
    "Which products have the highest revenue?"
  ]
}
```

---

## 3. Interactive Chat

**`GET | POST`** `/ai/interactive-chat`

Main conversational BI endpoint. Supports request cancellation via `requestId`.

### Request Parameters

| Parameter | Required | Type | Description |
|-----------|----------|------|-------------|
| `input` | Yes | string | User question / prompt |
| `chatid` | Yes | string | Chat session ID |
| `chat_sequence_id` | Yes | string | Sequence ID for this message in the chat |
| `subject` | No | JSON string | Agent context; must contain `model` with `dir` and `file` |
| `requestId` | No | string | Used for cancellable requests |

**`subject` example:**

```json
{
  "model": {
    "dir": "MyFolder",
    "file": "SalesAgent.agent"
  }
}
```

### Example Request

```http
POST /ai/interactive-chat
Content-Type: application/x-www-form-urlencoded

input=Show total sales by region&chatid=abc123&chat_sequence_id=1&subject={"model":{"dir":"MyFolder","file":"SalesAgent.agent"}}
```

### Response

```json
{
  "status": 1,
  "response": {
    "...": "Parsed JSON from Instant BI /interactive service"
  }
}
```

---

## 3b. Agent Dashboard

**`GET | POST`** `/ai/agent-dashboard`

Decomposes one question into sub-questions, runs InstantBI per chart, and returns a dashboard layout. Same InstantBI envelope as interactive chat, except **`dashboardid` replaces `chatid`** and **`dashboard_sequence_id` replaces `chat_sequence_id`**. Chart count is configured in InstantBI `application_config.yaml` (`dashboard.max_sub_questions`), not in this request. Supports request cancellation via `requestId`.

### Request Parameters

| Parameter | Required | Type | Description |
|-----------|----------|------|-------------|
| `input` | Yes | string | User question / prompt |
| `dashboardid` | Yes | string | Dashboard session ID (forwarded as InstantBI `input.dashboardid`) |
| `dashboard_sequence_id` | Yes | string | Sequence ID for this dashboard turn (forwarded as InstantBI `input.dashboard_sequence_id`) |
| `subject` | Yes* | JSON string | Must contain `model` with `dir` and `file` (plain or Base64). Required by InstantBI `/agent-dashboard`. |
| `requestId` | No | string | Used for cancellable requests |

**`subject` example:**

```json
{
  "model": {
    "dir": "MyFolder",
    "file": "SalesAgent.agent"
  }
}
```

### Example Request

```http
POST /ai/agent-dashboard
Content-Type: application/x-www-form-urlencoded

input=Build a sales overview dashboard&dashboardid=abc123&dashboard_sequence_id=1&subject={"model":{"dir":"MyFolder","file":"SalesAgent.agent"}}
```

### Response

```json
{
  "status": 1,
  "response": {
    "original_question": "Build a sales overview dashboard",
    "dashboardid": "abc123",
    "final_answer": "...",
    "asked_questions": ["Total sales KPI", "Sales by region"],
    "attempt_count": 8,
    "investigation_steps": [
      {"step": 1, "question": "Total sales KPI", "kind": "chart", "analysis": "..."},
      {"step": 2, "question": "Sales by region", "kind": "chart", "analysis": "..."}
    ],
    "sub_questions": [],
    "dashboard": {},
    "token_usage": {}
  }
}
```

`asked_questions`, `attempt_count`, and `investigation_steps` are filled programmatically
(not by the LLM). They show the multi-step investigation picture — focused sub-questions
and how many planner attempts ran — rather than implying a one-shot answer.

### Agent modes (token control)

Pass `mode` on the InstantBI `/agent-dashboard` body (`input.mode`) or Java
`/ai/agent-dashboard?mode=...`. Default from config: `dashboard.default_mode`.

| Mode | Charts | Tool loops | Overview size | LLM synthesizer |
|------|--------|------------|---------------|-----------------|
| `fast` | ≤2 | ≤10 | short | no (rule-based) |
| `balanced` | ≤5 | ≤24 | medium | yes |
| `research` | ≤8* | ≤40 | large | yes |

\* Chart counts are also capped by `dashboard.max_sub_questions`.

Profiles live in `helicalbi/sql_agent/modes.py`.

---

## 4. Data Insight

**`GET | POST`** `/ai/data-insight`

Generates data insights for a chat turn (uses saved instant report state when `subject` is omitted).

### Request Parameters

| Parameter | Required | Type | Description |
|-----------|----------|------|-------------|
| `chat_sequence_id` | Yes | string | Chat sequence ID to load context from |
| `input` | Conditional | string | User input (required if `formData` is not sent) |
| `formData` | Conditional | JSON string | Required if `subject` is not sent; must include `input`, `location`, `fileName` |
| `chatid` | No | string | Chat ID (used when `subject` is provided) |
| `subject` | No | JSON string | Agent context with `agent` object |
| `requestId` | No | string | Used for cancellable requests |

**`formData` example (when loading from saved instant report):**

```json
{
  "input": "Explain the trend in this data",
  "location": "MyReports",
  "fileName": "SalesReport.hr"
}
```

### Validation Rules

- Either `input` or `formData` must be provided.
- If `subject` is **not** provided, `formData` is **required** (report state is loaded from DB using `location` + `fileName`).

### Example Request

```http
POST /ai/data-insight
Content-Type: application/x-www-form-urlencoded

chat_sequence_id=2&formData={"input":"Explain the trend","location":"MyReports","fileName":"SalesReport.hr"}
```

### Response

```json
{
  "status": 1,
  "response": {
    "insight": "Sales increased 15% in Q4...",
    "token_usage": { }
  }
}
```

---

## 5. Convert Dashboard

**`GET | POST`** `/ai/convert-dashboard`

Turns a chat viz/SQL array into dashboard **parts** (layout, sections, filters, theme, summary). Python does **not** return `.efwdd` or `.hr` save JSON. The InstantBI UI hydrates Dashboard Designer from those parts.

### Request Parameters

| Parameter | Required | Type | Description |
|-----------|----------|------|-------------|
| `chatid` | No | string | Chat / thread id. Used to walk memory if `items` is empty |
| `items` | No | JSON string | Array of `{ id, sql, viz, summary }`. May be Base64-encoded |
| `subject` | No | JSON string | `{ "model": { "dir", "file" } }` (plain or Base64) |
| `formData` | No | JSON string | Optional metadata `location` / `metadata_file_name` / `dialect` |
| `input` | No | string | Unused for layout; forwarded as `inputString` |

### Example Request

```http
POST /ai/convert-dashboard
Content-Type: application/x-www-form-urlencoded

chatid=abc123&items=[{"id":"seq-3","sql":"SELECT ...","viz":{"chart_name":"bar","viz_model":{}}}]&subject={"model":{"dir":"MyFolder","file":"Sales.model"}}
```

### Response

```json
{
  "status": 1,
  "response": {
    "items": [{ "id": "seq-3", "sql": "...", "viz": {}, "sql_parts": {}, "viz_parts": {} }],
    "theme": { "color": "#1677ff", "background": "#ffffff" },
    "summary": { "title": "Overview", "text": "...", "x": 0, "y": 0, "w": 12, "h": 1 },
    "sections": [{ "id": "overview", "title": "Overview" }],
    "filters": [{ "column": "region", "sourceItemId": "seq-3", "listeners": ["seq-3"], "x": 0, "y": 1, "w": 3, "h": 1 }],
    "layout": [{ "itemId": "seq-3", "sectionId": "overview", "x": 0, "y": 2, "w": 6, "h": 4 }]
  }
}
```

---

## 6. Chat Context

**`GET | POST`** `/ai/chat-context`

Classifies user input (metadata vs report) and optionally fetches metadata insights.

### Request Parameters

| Parameter | Required | Type | Description |
|-----------|----------|------|-------------|
| `input` | Yes | string | User input to classify |

### Example Request

```http
POST /ai/chat-context
Content-Type: application/x-www-form-urlencoded

input=Tell me about the metadata for sales data
```

### Response

```json
{
  "output": {
    "context": "metadata",
    "fileName": "Sales.metadata",
    "location": "MyFolder",
    "insightResponse": "..."
  }
}
```

Possible `context` values: `"metadata"` or `"report"`. On error:

```json
{
  "output": {
    "error": "error message",
    "output": "raw chat service response"
  }
}
```

---

## Shared JSON Structures

### Agent Object

Used in `agent` and `subject.agent`:

```json
{
  "dir": "folder/path",
  "file": "AgentName.agent"
}
```

### Instant Report `formData`

Used by `/ai/data-insight`:

```json
{
  "input": "user question",
  "location": "folder/path",
  "fileName": "ReportName.hr"
}
```

---

## Backend Service Mapping

Each endpoint forwards to the Instant BI Python service (`instantbiConfig.serviceUrl`, default `http://instantbi:8000/`):

| Controller Endpoint | Backend Path |
|---------------------|--------------|
| `/ai/recommendation/domain` | `/suggestDomain` |
| `/ai/recommendation/analyst` | `/topNQuestion` |
| `/ai/interactive-chat` | `/interactive` |
| `/ai/agent-dashboard` | `/agent-dashboard` |
| `/ai/data-insight` | `/data-insight` |
| `/ai/convert-hreport` | `/instant-to-hr` |
| `/ai/convert-dashboard` | `/convert-dashboard` |
| `/ai/chat-context` | `/chat` (+ `/metadataInsight` when context is metadata) |

