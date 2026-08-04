"""Utility endpoints for runtime LLM and application configuration."""

from __future__ import annotations

import json
import logging
from typing import Any

from flask import request

from bl.helpers import error_messages_from_exception, json_response, log_endpoint_input
from helicalbi.service.utility.exception.UtilityError import UtilityError
from helicalbi.service.utility.UtilityService import utility_service

logger = logging.getLogger(__name__)

# Session / proxy plumbing keys injected by Java InstantBIUtils.addSessionContext.
# Must never be persisted into llm_config.yaml / application_config.yaml.
_SESSION_KEYS = frozenset(
    {
        "body",
        "requestId",
        "htmlId",
        "sessionCookie",
        "username",
        "userId",
        "orgId",
    }
)


def _ok(payload: dict) -> str:
    body = {"status": 1, **payload}
    return json_response(body)


def _fail(exc: BaseException) -> str:
    if isinstance(exc, UtilityError):
        messages = [exc.message]
    elif isinstance(exc, ValueError):
        messages = [str(exc)]
    else:
        messages = error_messages_from_exception(exc)
        logger.exception("Utility endpoint failed: %s", messages)
    return json_response({"status": 0, "error": messages})


def _strip_session_keys(data: dict[str, Any]) -> dict[str, Any]:
    return {key: value for key, value in data.items() if key not in _SESSION_KEYS}


def _request_data() -> dict[str, Any]:
    """Parse JSON body, nested ``body`` form field, or flat form/query params.

    Java InstantBI proxies POST JSON and also injects sessionCookie/username/userId.
    Those session keys are stripped so they are never treated as config fields.
    """
    data = request.get_json(silent=True)
    if isinstance(data, dict):
        return _strip_session_keys(data)

    body_field = request.values.get("body")
    if body_field:
        try:
            parsed = json.loads(body_field)
            if isinstance(parsed, dict):
                return _strip_session_keys(parsed)
        except (TypeError, ValueError):
            logger.debug("Ignoring non-JSON body form field")

    merged: dict[str, Any] = {}
    for key in request.values:
        if key in _SESSION_KEYS:
            continue
        merged[key] = request.values.get(key)
    return merged


def register(flask_app) -> None:
    # ------------------------------------------------------------------
    # Bootstrap (Admin UI)
    # ------------------------------------------------------------------

    @flask_app.route("/utility/settings", methods=["GET", "POST"])
    def utility_settings_bootstrap():
        """Return LLM + application config in one payload for the Admin UI."""
        data = _request_data()
        log_endpoint_input("/utility/settings", data)
        try:
            return _ok(utility_service.get_settings_bootstrap())
        except Exception as exc:
            return _fail(exc)

    # ------------------------------------------------------------------
    # LLM configuration
    # ------------------------------------------------------------------

    @flask_app.route("/utility/llm", methods=["GET", "POST"])
    def utility_list_llm():
        """List configured LLM providers and the current default."""
        log_endpoint_input("/utility/llm", _request_data())
        try:
            return _ok(utility_service.list_llm_providers())
        except Exception as exc:
            return _fail(exc)

    @flask_app.route("/utility/llm/models", methods=["GET", "POST"])
    def utility_list_llm_models():
        """List available models for a LangChain provider package."""
        data = _request_data()
        package = data.get("package") or request.args.get("package")
        provider = data.get("provider") or request.args.get("provider")
        log_endpoint_input(
            "/utility/llm/models",
            {"package": package, "provider": provider},
        )
        try:
            return _ok(
                utility_service.list_models_for_package(
                    package=package, provider=provider
                )
            )
        except Exception as exc:
            return _fail(exc)

    @flask_app.route("/utility/llm/change-model", methods=["POST"])
    def utility_change_llm_model():
        """Change the model for a provider and optionally make it default."""
        data = _request_data()
        log_endpoint_input("/utility/llm/change-model", data)
        try:
            result = utility_service.change_model(
                data.get("model") or "",
                provider=data.get("provider"),
                set_as_default=bool(data.get("set_as_default", True)),
            )
            return _ok(result)
        except Exception as exc:
            return _fail(exc)

    @flask_app.route("/utility/llm/default-provider", methods=["PUT", "POST"])
    def utility_set_default_provider():
        """Switch the active default_provider."""
        data = _request_data()
        log_endpoint_input("/utility/llm/default-provider", data)
        try:
            result = utility_service.set_default_provider(data.get("provider") or "")
            return _ok({"config": result})
        except Exception as exc:
            return _fail(exc)

    @flask_app.route("/utility/llm/provider", methods=["POST", "PUT"])
    def utility_upsert_llm_provider():
        """Add or modify an LLM provider block in llm_config.yaml."""
        data = _request_data()
        log_endpoint_input("/utility/llm/provider", data)
        try:
            result = utility_service.upsert_llm_provider(data)
            return _ok(result)
        except Exception as exc:
            return _fail(exc)

    @flask_app.route("/utility/llm/config", methods=["GET", "POST", "PUT"])
    def utility_llm_config():
        """Get or partially update llm_config.yaml (providers / default / base_url)."""
        data = _request_data()
        if request.method == "GET" or not data:
            log_endpoint_input("/utility/llm/config", data)
            try:
                return _ok({"config": utility_service.llm.get_config(mask=True)})
            except Exception as exc:
                return _fail(exc)

        log_endpoint_input("/utility/llm/config", data)
        try:
            result = utility_service.update_llm_config(data)
            return _ok({"config": result})
        except Exception as exc:
            return _fail(exc)

    # ------------------------------------------------------------------
    # Application configuration
    # ------------------------------------------------------------------

    @flask_app.route("/utility/app-config", methods=["GET", "POST", "PUT"])
    def utility_app_config():
        """Get or update application_config.yaml sections at runtime."""
        data = _request_data()
        if request.method == "GET" or not data:
            log_endpoint_input("/utility/app-config", data)
            try:
                return _ok({"config": utility_service.get_app_config()})
            except Exception as exc:
                return _fail(exc)

        log_endpoint_input("/utility/app-config", data)
        try:
            result = utility_service.update_app_config(data)
            return _ok({"config": result})
        except Exception as exc:
            return _fail(exc)

    @flask_app.route("/utility/logging", methods=["POST", "PUT"])
    def utility_update_logging():
        """Update logging.* entries in application_config.yaml."""
        data = _request_data()
        log_endpoint_input("/utility/logging", data)
        try:
            updates = (
                data.get("logging") if isinstance(data.get("logging"), dict) else data
            )
            result = utility_service.update_logging(updates or {})
            return _ok({"config": result})
        except Exception as exc:
            return _fail(exc)

    @flask_app.route("/utility/question-config", methods=["POST", "PUT"])
    def utility_update_question_config():
        """Update KPI / question suggestion settings in application_config.yaml."""
        data = _request_data()
        log_endpoint_input("/utility/question-config", data)
        try:
            payload = data.get("kpi") if isinstance(data.get("kpi"), dict) else data
            result = utility_service.update_question_config(payload or {})
            return _ok({"config": result})
        except Exception as exc:
            return _fail(exc)
