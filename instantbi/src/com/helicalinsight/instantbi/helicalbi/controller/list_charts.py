import logging

from flask import request

from helicalbi.controller.helpers import json_response, log_endpoint_input
from helicalbi.viz._charts import get_charts

logger = logging.getLogger(__name__)

_VF_PREFIX = "vf."


def register(flask_app) -> None:
    @flask_app.route("/list-charts", methods=["GET", "POST"])
    def list_charts_endpoint():
        """Return all chart catalog names prefixed with ``vf.``."""
        log_endpoint_input("/list-charts", dict(request.args) or (request.get_json(silent=True) or {}))
        charts = [f"{_VF_PREFIX}{name}" for name in sorted(get_charts().keys())]
        logger.info("List-charts returning %s charts", len(charts))
        return json_response({"charts": charts})
