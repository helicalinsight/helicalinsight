import json
import logging
import traceback
from typing import Any, Dict

from flask import request

from helicalbi.controller.app_context import app
from helicalbi.controller.helpers import (
    json_response,
    log_endpoint_input,
    resolve_sql_from_request,
)
from helicalbi.common.app_config import is_debug
from helicalbi.common.auth import bind_request_identity
from helicalbi.sql_to_formdata import sql_to_form_data

logger = logging.getLogger(__name__)


def _resolve_metadata_inputs(
    user_input: dict, session_cookie: str
) -> tuple[str, str, str, dict | None]:
    """Resolve location, metadata_dir, metadata_file_name, and optional metadata payload."""
    location = (
        user_input.get("location")
        or user_input.get("md_location")
        or user_input.get("mdLocation")
        or ""
    )
    metadata_dir = (
        user_input.get("metadata_dir")
        or user_input.get("metadataDir")
        or location
    )
    metadata_file_name = (
        user_input.get("metadata_file_name")
        or user_input.get("metadataFileName")
        or user_input.get("md_file_name")
        or user_input.get("mdFileName")
        or ""
    )
    metadata: dict | None = None

    model = user_input.get("model")
    if model and not (metadata_dir and metadata_file_name):
        logger.debug("Resolving metadata from model for instant-to-hr")
        helper = app().ModelLayerHelper(session_cookie, model["file"], model["dir"])
        metadata_file_name = helper.get_metadata_layerfile()
        metadata_dir = helper.get_metadata_layerlocation()
        metadata = helper.get_metadata()
        if not location:
            location = metadata_dir
    elif model and not metadata:
        # Model is present; prefer agent-load metadata over a separate metadata get.
        try:
            helper = app().ModelLayerHelper(session_cookie, model["file"], model["dir"])
            metadata = helper.get_metadata()
            if not metadata_file_name:
                metadata_file_name = helper.get_metadata_layerfile()
            if not metadata_dir:
                metadata_dir = helper.get_metadata_layerlocation()
            if not location:
                location = metadata_dir
        except Exception:
            logger.exception(
                "instant-to-hr could not load metadata from agent; falling back"
            )

    if not location:
        location = metadata_dir

    return location, metadata_dir, metadata_file_name, metadata


def register(flask_app) -> None:
    @flask_app.route("/instant-to-hr", methods=["POST"])
    def instant_to_hr():
        """Convert SQL + metadata refs into a wire formData object via sql_to_form_data."""
        logger.info("Instant-to-hr endpoint invoked")
        data = request.get_json()
        log_endpoint_input("/instant-to-hr", data)

        user_input = data.get("input", data) if data else {}
        session_cookie, username, _user_id, _org_id = bind_request_identity(data, user_input)

        thread_id = user_input.get("thread_id", user_input.get("chatid", ""))
        chat_seq_id = user_input.get("chat_seq_id")
        sql = resolve_sql_from_request(
            user_input, thread_id, chat_seq_id, context="instant-to-hr"
        )
        location = ""
        metadata_dir = ""
        metadata_file_name = ""
        metadata = None
        dialect = user_input.get("dialect") or None
        include_layers = bool(user_input.get("include_layers") or user_input.get("layers"))

        to_send: Dict[str, Any] = {}
        try:
            location, metadata_dir, metadata_file_name, metadata = _resolve_metadata_inputs(
                user_input, session_cookie
            )
            logger.info(
                "Instant-to-hr request user=%s thread=%s chat_seq_id=%s sql_len=%s "
                "location=%s metadata_dir=%s file=%s",
                username,
                thread_id,
                chat_seq_id,
                len(sql),
                location,
                metadata_dir,
                metadata_file_name,
            )
            if not sql:
                raise RuntimeError("No SQL found for instant-to-hr request.")
            if not (location or metadata_dir) or not metadata_file_name:
                raise RuntimeError(
                    "metadata_dir/location and metadata_file_name are required for instant-to-hr."
                )

            form_data = sql_to_form_data(
                sql,
                location=location,
                metadata_dir=metadata_dir,
                metadata_file_name=metadata_file_name,
                session_cookie=session_cookie,
                dialect=dialect,
                metadata=metadata,
                include_layers=include_layers,
            )
            to_send = form_data
            logger.info(
                "Instant-to-hr completed user=%s columns=%s  form_data=%s",
                username,
                len(form_data.get("columns") or []),
                json.dumps(to_send)
            )
        except Exception as e:
            logger.exception("Error while converting SQL to formData user=%s", username)
            to_send["error"] = str(e)
            if is_debug():
                to_send["stack"] = traceback.format_exc()

        return json_response(to_send)
