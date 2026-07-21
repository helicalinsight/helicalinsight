import logging

from flask import request

from bl.helpers import log_endpoint_input

logger = logging.getLogger(__name__)


def register(app) -> None:
    @app.route("/")
    def hello():
        log_endpoint_input("/", dict(request.args))
        logger.debug("Health check endpoint called")
        return "AI and HI bridge connector"
