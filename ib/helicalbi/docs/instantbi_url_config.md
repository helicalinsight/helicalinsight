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
    model: gpt-5.4-mini
    temperature: 0.1
    max_tokens: 1000
    api_key: <YOUR_OPENAI_API_KEY>

  anthropic:
    model: claude-opus-4-6
    temperature: 0.1
    max_tokens: 1500
    api_key: <YOUR_ANTHROPIC_API_KEY>

  gemini:
    model: gemini-2.5-flash
    temperature: 0.1
    max_tokens: 1500
    api_key: <YOUR_GEMINI_API_KEY>
```

| Setting | Description |
|---------|-------------|
| `default_provider` | Active LLM provider (`openai`, `anthropic`, or `gemini`). **Required.** |
| `base_url` | Base URL of the hi-ee application, including context path and port. **Required.** |
| `providers.<name>.model` | Model name for that provider. **Required** for the selected provider. |
| `providers.<name>.api_key` | API key for that provider. **Required** for the selected provider. |

| Environment | `base_url` |
|-------------|------------|
| Docker | `http://hiee:8080/hi-ee` |
| Local (non-Docker) | `http://localhost:8085/hi-ee` |

### Notes

- When running locally (outside Docker), use `http://localhost:8085/hi-ee` for `base_url`.
- Set `api_key` only for the provider named in `default_provider`; unused providers can leave `api_key` empty.
---

## Checklist

- [ ] `setting.xml` → `instantbiConfig.serviceUrl` points to InstantBI
- [ ] `llm_config.yaml` → `base_url` points to hi-ee
- [ ] `llm_config.yaml` → `default_provider` is set
- [ ] `llm_config.yaml` → selected provider has `model` and `api_key`
