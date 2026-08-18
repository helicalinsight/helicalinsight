"""Controller route handlers for the HelicalBI Flask app."""

from helicalbi.controller.abort import register as register_abort
from helicalbi.controller.clear_api_cache import register as register_clear_api_cache
from helicalbi.controller.convert_chart import register as register_convert_chart
from helicalbi.controller.data_insight import register as register_data_insight
from helicalbi.controller.get_semantic_data import register as register_get_semantic_data
from helicalbi.controller.hello import register as register_hello
from helicalbi.controller.instant_to_hr import register as register_instant_to_hr
from helicalbi.controller.interactive import register as register_interactive
from helicalbi.controller.list_charts import register as register_list_charts
from helicalbi.controller.load_chat import register as register_load_chat
from helicalbi.controller.suggest_domain import register as register_suggest_domain
from helicalbi.controller.top_n_question import register as register_top_n_question
from helicalbi.controller.utility_config import register as register_utility_config


def register_routes(app) -> None:
    """Attach all HTTP endpoints to the Flask application."""
    register_hello(app)
    register_suggest_domain(app)
    register_top_n_question(app)
    register_interactive(app)
    register_load_chat(app)
    register_abort(app)
    register_clear_api_cache(app)
    register_data_insight(app)
    register_instant_to_hr(app)
    register_get_semantic_data(app)
    register_convert_chart(app)
    register_list_charts(app)
    register_utility_config(app)
