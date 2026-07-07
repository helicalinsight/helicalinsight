import logging


logger = logging.getLogger(__name__)


def register(app) -> None:
    @app.route("/")
    def hello():
        logger.debug("Health check endpoint called")
        return "AI and HI bridge connector"
