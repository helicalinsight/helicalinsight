import logging

from flask import request

from bl.helpers import json_response, log_endpoint_input
from helicalbi.api.ApiCallCache import clear as clear_api_cache

logger = logging.getLogger(__name__)


def register(flask_app) -> None:
    @flask_app.route("/clear-api-cache", methods=["POST"])
    def clear_api_cache_endpoint():
        """Clear the in-memory cache of HI service API responses."""
        data = request.get_json(silent=True) or {}
        log_endpoint_input("/clear-api-cache", data)
        cleared = clear_api_cache()
        logger.info("Clear API cache endpoint cleared entries=%s", cleared)
        return json_response({
            "status": 1,
            "cleared": cleared,
            "message": "API cache cleared successfully",
        })
