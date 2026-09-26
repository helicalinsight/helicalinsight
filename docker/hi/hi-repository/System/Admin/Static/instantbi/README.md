# InstantBI Settings

Configuration-only assets for **Admin → Configurations → InstantBI Settings**.

JSON here describes **what to render** (ui-generator layouts). Rendering and API
calls live in the React `InstantBISettingsEditor` + common `ui-generator` module.

## Files

| File | Purpose |
|------|---------|
| `instantbi-settings.ui.json` | Manifest: panels, layout ids, utility endpoints |
| `llm.ui.layout.json` | Active provider/model form |
| `provider.ui.layout.json` | Add/edit provider form |
| `logging.ui.layout.json` | Logging form |
| `application.ui.layout.json` | KPI / flags / SQL / cache form |

Layouts are loaded with `content/static/getContents` using content ids such as
`Static/instantbi/llm.ui.layout` (`.json` is appended by the server).

Do **not** store HTML/CSS/JS markup in this folder.
