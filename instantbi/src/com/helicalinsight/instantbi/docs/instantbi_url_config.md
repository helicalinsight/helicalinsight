# URL Configuration

Helical Insight (hi-ee) and InstantBI call each other. Both sides must be configured before the application can run.

```
hi-ee  ──serviceUrl──►  InstantBI  (setting.xml)
InstantBI ──base_url──►  hi-ee     (llm_config.yaml)
```

---

## 1. hi-ee → InstantBI

**File:** `hi-repository/System/Admin/setting.xml`

Configure the InstantBI service URL under `instantbiConfig`. This block is **mandatory**.

```xml
<instantbiConfig mandatory="true">
    <serviceUrl>http://instantbi:8000/</serviceUrl>
</instantbiConfig>
```

| Setting | Description |
|---------|-------------|
| `serviceUrl` | Base URL of the InstantBI service (host and port). Trailing `/` is recommended. |

| Environment | `serviceUrl` |
|-------------|--------------|
| Docker | `http://instantbi:8000/` |
| Local (non-Docker) | `http://localhost:8000/` |

---

## 2. InstantBI → hi-ee

**File:** `helicalbi/config/llm_config.yaml`

Docker bind-mounts `hi-repository/System/InstantBI` (a copy/link of this folder) onto `/app/helicalbi/config`. Edit the files in `helicalbi/config` for local runs.

InstantBI uses this file for:

- Downstream URL of hi-ee (`base_url`)
- LLM provider, model, and API key

**Configuring the API provider and its key is mandatory.** The application will not run correctly without a valid provider and API key.

### Example

```yaml
# Provide the LLM provider for Instant BI
default_provider: openai

# Provide base URL of the hi-ee application along with port number
# Docker:  http://hiee:8080/hi-ee
# Local:   http://localhost:8085/hi-ee
base_url: http://hiee:8080/hi-ee

providers:
  openai:
    package: langchain-openai
    model: gpt-4.1-mini
    parameters:
      temperature: 0.1
      max_tokens: 4000
      api_key: ${OPENAI_API_KEY}

  anthropic:
    package: langchain-anthropic
    model: claude-opus-4-6
    parameters:
      temperature: 0.1
      max_tokens: 4000
      api_key: ${ANTHROPIC_API_KEY}

  google-genai:
    package: langchain-google-genai
    model: gemini-2.5-flash
    parameters:
      temperature: 0.1
      max_tokens: 4000
      api_key: ${GOOGLE_API_KEY}
```

API keys live in the project `.env` (see `.env.example`). Switch providers by changing only `default_provider`.

| Setting | Description |
|---------|-------------|
| `default_provider` | Active LLM provider (`openai`, `anthropic`, `google-genai`, `ollama`, …). **Required.** |
| `base_url` | Base URL of the hi-ee application, including context path and port. **Required.** |
| `providers.<name>.package` | LangChain provider package (installed on demand). **Required.** |
| `providers.<name>.model` | Model name for that provider. **Required** for the selected provider. |
| `providers.<name>.parameters.api_key` | API key (use `${ENV_VAR}` from `.env`). **Required** for cloud providers. |

| Environment | `base_url` |
|-------------|------------|
| Docker | `http://hiee:8080/hi-ee` |
| Local (non-Docker) | `http://localhost:8085/hi-ee` |

### Notes

- When running locally (outside Docker), use `http://localhost:8085/hi-ee` for `base_url`.
- Set the env var for the provider named in `default_provider`; unused providers can leave their keys empty.
- Provider packages are installed automatically the first time that provider is selected.
---

## Checklist

- [ ] `setting.xml` → `instantbiConfig.serviceUrl` points to InstantBI
- [ ] `llm_config.yaml` → `base_url` points to hi-ee
- [ ] `llm_config.yaml` → `default_provider` is set
- [ ] `.env` → selected provider API key (or Ollama `OLLAMA_BASE_URL`) is set
