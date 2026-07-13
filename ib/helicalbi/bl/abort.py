import logging

from flask import request

from bl.helpers import json_response, log_endpoint_input
from helicalbi.common.RequestCancellation import request_cancellation

logger = logging.getLogger(__name__)


def register(flask_app) -> None:
    @flask_app.route("/abort", methods=["POST"])
    def abort_request():
        """Cancel an in-flight ``/interactive`` (or similar) request by ``requestId``."""
        data = request.get_json(silent=True) or {}
        log_endpoint_input("/abort", data)
        request_id = data.get("requestId") or request.args.get("requestId")
        if not request_id:
            logger.warning("Abort endpoint called without requestId")
            return json_response({
                "status": 0,
                "error": "requestId is required",
            }), 400

        was_active = request_cancellation.cancel(request_id)
        logger.info("Abort endpoint called for requestId=%s active=%s", request_id, was_active)
        return json_response({
            "status": 1,
            "requestId": str(request_id),
            "active": was_active,
            "message": "Request cancelled successfully",
        })
