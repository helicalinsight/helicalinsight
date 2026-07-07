"""Business-layer route handlers for the HelicalBI Flask app."""

from bl.abort import register as register_abort
from bl.data_insight import register as register_data_insight
from bl.get_semantic_data import register as register_get_semantic_data
from bl.hello import register as register_hello
from bl.interactive import register as register_interactive
from bl.load_chat import register as register_load_chat
from bl.suggest_domain import register as register_suggest_domain
from bl.top_n_question import register as register_top_n_question


def register_routes(app) -> None:
    """Attach all HTTP endpoints to the Flask application."""
    register_hello(app)
    register_suggest_domain(app)
    register_top_n_question(app)
    register_interactive(app)
    register_load_chat(app)
    register_abort(app)
    register_data_insight(app)
    register_get_semantic_data(app)
