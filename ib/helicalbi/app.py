import logging
import os
from pathlib import Path

from dotenv import load_dotenv

load_dotenv(Path(__file__).resolve().parent / ".env")

from flask import Flask

from GraphBuilderManger import viz_graph
from bl import register_routes
from helicalbi.api.Metadata import get_db_function_of_metadata, get_json_data_metadata
from helicalbi.api.QueryExecutor import execute_query
from helicalbi.common.ChatGraphMemory import chat_graph_memory
from helicalbi.common.ConvertToBlankAgent import transform_json
from helicalbi.common.LlmInvokeHelper import invoke_llm
from helicalbi.common.RequestCancellation import request_cancellation
from helicalbi.service.agentservice.AgentLayerHelper import AgentLayerHelper
from helicalbi.service.kpi.KpiProvider import KpiProvider
from helicalbi.common.app_config import app_env, is_debug, is_production
from helicalbi.common.logging_config import configure_logging
from helicalbi.core.flows.MainFlowGraph import build_main_graph

logger = logging.getLogger(__name__)
app = Flask(__name__)

configure_logging()

app.config["ENV"] = "production" if is_production() else "development"
app.config["DEBUG"] = is_debug()

main_graph = build_main_graph()

register_routes(app)

if __name__ == "__main__":
    host = os.getenv("HOST", "0.0.0.0")
    port = int(os.getenv("PORT", "8000"))
    threads = int(os.getenv("WEB_CONCURRENCY", "4"))
    debug = is_debug()

    logger.info(
        "Starting HelicalBI service env=%s host=%s port=%s debug=%s",
        app_env,
        host,
        port,
        debug,
    )

    if is_production():
        try:
            from waitress import serve

            serve(app, host=host, port=port, threads=threads)
        except ImportError:
            logger.warning(
                "waitress is not installed; falling back to Flask server "
                "(pip install waitress for production)"
            )
            app.run(host=host, port=port, debug=False, threaded=True, use_reloader=False)
    else:
        app.run(host=host, port=port, debug=debug, threaded=True, use_reloader=debug)
