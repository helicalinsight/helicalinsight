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
| `subject` | No | JSON string | Agent context; must contain `agent` (see below) |
| `requestId` | No | string | Used for cancellable requests |

**`subject` example:**

```json
{
  "agent": {
    "dir": "MyFolder",
    "file": "SalesAgent.agent"
  }
}
```

### Example Request

```http
POST /ai/interactive-chat
Content-Type: application/x-www-form-urlencoded

input=Show total sales by region&chatid=abc123&chat_sequence_id=1&subject={"agent":{"dir":"MyFolder","file":"SalesAgent.agent"}}
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

## 5. Load Past Chat

**`GET | POST`** `/ai/load-chat`

Reloads a previous chat turn from a saved instant report.

### Request Parameters

| Parameter | Required | Type | Description |
|-----------|----------|------|-------------|
| `chat_sequence_id` | Yes | string | Sequence ID of the chat turn to reload |
| `formData` | Yes | JSON string | Must contain `input`, `location`, `fileName` |
| `requestId` | No | string | Used for cancellable requests |

**`formData` example:**

```json
{
  "input": "Show sales by region",
  "location": "MyReports",
  "fileName": "SalesReport.hr"
}
```

### Example Request

```http
POST /ai/load-chat
Content-Type: application/x-www-form-urlencoded

chat_sequence_id=1&formData={"input":"Show sales by region","location":"MyReports","fileName":"SalesReport.hr"}
```

### Response

```json
{
  "status": 1,
  "response": {
    "...": "Parsed JSON from Instant BI /load-chat service"
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

Used by `/ai/load-chat` and `/ai/data-insight`:

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
| `/ai/data-insight` | `/data-insight` |
| `/ai/load-chat` | `/load-chat` |
| `/ai/chat-context` | `/chat` (+ `/metadataInsight` when context is metadata) |

