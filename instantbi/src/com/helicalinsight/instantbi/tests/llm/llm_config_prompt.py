"""Browser form that writes the live LLM provider into llm_config.yaml.

Provider name is required. API key and base URL are optional: a blank field
leaves the value already stored for that provider.
"""
from __future__ import annotations

import html
import threading
import webbrowser
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from typing import Any
from urllib.parse import parse_qs

from helicalbi.core.ConfigLoader import ConfigLoader


def apply_llm_run_config(
    provider: str,
    api_key: str = "",
    base_url: str = "",
) -> dict[str, Any]:
    """Set the default provider and, when given, its api key and base URL."""
    name = (provider or "").strip()
    if not name:
        raise ValueError("provider is required")

    raw = ConfigLoader.load_raw_config("llm_config.yaml")
    providers = raw.get("providers") or {}
    if name not in providers:
        known = ", ".join(sorted(str(key) for key in providers))
        raise ValueError(
            f"Provider '{name}' is not in llm_config.yaml. Known providers: {known}"
        )

    raw["default_provider"] = name
    block = providers[name]
    if not isinstance(block, dict):
        raise ValueError(f"Provider '{name}' is not a config block")
    params = block.get("parameters")
    if not isinstance(params, dict):
        params = {}
        block["parameters"] = params

    key = (api_key or "").strip()
    url = (base_url or "").strip()
    if key:
        params["api_key"] = key
    if url:
        params["base_url"] = url

    ConfigLoader.save_config("llm_config.yaml", raw)
    return {
        "default_provider": name,
        "api_key_updated": bool(key),
        "base_url_updated": bool(url),
    }


def _field(form: dict[str, list[str]], name: str) -> str:
    values = form.get(name) or [""]
    return str(values[0] if values else "")


def _page(providers: list[str], default: str, message: str = "", error: str = "") -> bytes:
    options = "\n".join(
        f'<option value="{html.escape(name, quote=True)}">' for name in providers
    )
    notice = f"<p class='ok'>{html.escape(message)}</p>" if message else ""
    problem = f"<p class='err'>{html.escape(error)}</p>" if error else ""
    selected = html.escape(default, quote=True)
    document = f"""<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>LLM configuration</title>
  <style>
    body {{ font-family: Segoe UI, sans-serif; margin: 2rem auto; max-width: 32rem; }}
    label {{ display: block; margin-top: 1rem; font-weight: 600; }}
    input {{ width: 100%; padding: 0.4rem; margin-top: 0.25rem; }}
    button {{ margin-top: 1.25rem; padding: 0.5rem 1rem; }}
    .hint {{ font-weight: 400; color: #555; }}
    .ok {{ color: #0a7a32; }}
    .err {{ color: #a32020; }}
  </style>
</head>
<body>
  <h1>LLM configuration</h1>
  <p>These values are written to llm_config.yaml before the live LLM run.</p>
  {notice}
  {problem}
  <form method="post" action="/">
    <label>Provider name
      <input name="provider" list="providers" value="{selected}" required>
      <datalist id="providers">{options}</datalist>
    </label>
    <label>API key <span class="hint">(optional)</span>
      <input name="api_key" type="password" autocomplete="off">
    </label>
    <label>Base URL <span class="hint">(optional)</span>
      <input name="base_url" type="text" placeholder="https://api.example.com">
    </label>
    <button type="submit">Update llm_config</button>
  </form>
</body>
</html>
"""
    return document.encode("utf-8")


class LlmConfigPrompt:
    """Local page that collects the provider and updates llm_config.yaml."""

    def __init__(self) -> None:
        self.port = 0
        self.result: dict[str, Any] | None = None
        self._httpd: ThreadingHTTPServer | None = None
        self._ready = threading.Event()
        self._done = threading.Event()

    @property
    def url(self) -> str:
        return f"http://127.0.0.1:{self.port}/"

    def serve(self, open_browser: bool = True) -> None:
        prompt = self

        class Handler(BaseHTTPRequestHandler):
            def do_GET(self) -> None:  # noqa: N802
                raw = ConfigLoader.load_raw_config("llm_config.yaml")
                providers = sorted(str(name) for name in (raw.get("providers") or {}))
                default = str(raw.get("default_provider") or "")
                body = _page(providers, default)
                self._send(200, body)

            def do_POST(self) -> None:  # noqa: N802
                length = int(self.headers.get("Content-Length") or 0)
                payload = self.rfile.read(length).decode("utf-8")
                form = parse_qs(payload, keep_blank_values=True)
                try:
                    prompt.result = apply_llm_run_config(
                        _field(form, "provider"),
                        api_key=_field(form, "api_key"),
                        base_url=_field(form, "base_url"),
                    )
                except ValueError as exc:
                    raw = ConfigLoader.load_raw_config("llm_config.yaml")
                    providers = sorted(str(name) for name in (raw.get("providers") or {}))
                    default = _field(form, "provider") or str(raw.get("default_provider") or "")
                    self._send(400, _page(providers, default, error=str(exc)))
                    return
                name = prompt.result["default_provider"]
                self._send(
                    200,
                    _page(
                        [],
                        name,
                        message=f"Updated llm_config. Default provider is {name}.",
                    ),
                )
                prompt._done.set()

            def log_message(self, fmt: str, *args: Any) -> None:
                return

            def _send(self, status: int, body: bytes) -> None:
                self.send_response(status)
                self.send_header("Content-Type", "text/html; charset=utf-8")
                self.send_header("Content-Length", str(len(body)))
                self.end_headers()
                self.wfile.write(body)

        server = ThreadingHTTPServer(("127.0.0.1", 0), Handler)
        self._httpd = server
        self.port = int(server.server_address[1])
        self._ready.set()
        if open_browser:
            webbrowser.open(self.url)
        server.serve_forever()

    def wait_until_listening(self, timeout: float = 5) -> None:
        if not self._ready.wait(timeout):
            raise TimeoutError("LLM config form did not start")

    def wait_for_submit(self, timeout: float | None = None) -> dict[str, Any]:
        if not self._done.wait(timeout):
            raise TimeoutError("LLM config form was not submitted")
        return self.result or {}

    def close(self) -> None:
        if self._httpd is not None:
            self._httpd.shutdown()
            self._httpd = None


def prompt_llm_config(open_browser: bool = True) -> dict[str, Any]:
    """Open the form and block until llm_config.yaml is updated."""
    prompt = LlmConfigPrompt()
    thread = threading.Thread(
        target=prompt.serve,
        kwargs={"open_browser": open_browser},
        daemon=True,
    )
    thread.start()
    prompt.wait_until_listening()
    print(f"LLM config form: {prompt.url}", flush=True)
    try:
        return prompt.wait_for_submit()
    finally:
        prompt.close()
