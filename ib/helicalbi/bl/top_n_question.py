import logging

from flask import request

from bl.app_context import app
from bl.helpers import error_messages_from_exception, json_response, log_endpoint_input
from helicalbi.audit.llm_usage_audit import audit_llm_usage_async
from helicalbi.common.CubeInfoModel import is_cube_info_model, prepare_cube_info_model_data
from helicalbi.common.auth import bind_request_identity
from helicalbi.common.JsonToPara import has_table_column_info, prevalidate_cube_metadata

logger = logging.getLogger(__name__)


def register(flask_app) -> None:
    @flask_app.route("/topNQuestion", methods=["POST"])
    def top_n_question():
        data = request.get_json()
        log_endpoint_input("/topNQuestion", data)
        domain = data["domain"]
        top_n = data["topN"]
        session_cookie, username, user_id, _org_id = bind_request_identity(data)
        model_file_name = data["model"]["file"]
        location = data["model"]["dir"]
        base_url = data.get("baseUrl") or ""
        logger.info(
            "Top N questions requested user=%s domain=%s topN=%s model=%s",
            username,
            domain,
            top_n,
            model_file_name,
        )
        token_usage = {}
        request_status = "SUCCESS"
        error_message = None
        user_query = f"Suggest KPIs for domain: {domain}"
        try:
            helper = app().ModelLayerHelper(session_cookie, model_file_name, location)
            model_data = helper.get_model_semantic_layer()
            try:
                md_file_name = helper.get_metadata_layerfile()
                md_location = helper.get_metadata_layerlocation()
                metadata_response = app().get_json_data_metadata(
                    session_cookie, md_file_name, md_location
                )
                if is_cube_info_model(model_data):
                    cube_info_prepared = prepare_cube_info_model_data(
                        model_data,
                        metadata_response,
                    )
                    model_data = {
                        **model_data,
                        "cube_metadata": cube_info_prepared.get("cube_metadata") or [],
                        "topic_mappings": cube_info_prepared.get("topic_mappings") or [],
                        "business_metrics": cube_info_prepared.get("business_metrics") or [],
                        "synonyms": cube_info_prepared.get("synonyms") or [],
                    }
                else:
                    original_cube_metadata = model_data.get("cube_metadata")
                    cube_metadata = prevalidate_cube_metadata(
                        original_cube_metadata,
                        metadata_response,
                    )
                    if not has_table_column_info(original_cube_metadata) and cube_metadata:
                        logger.info(
                            "Top N using metadata fallback table/column context file=%s location=%s tables=%s",
                            md_file_name,
                            md_location,
                            len(cube_metadata),
                        )
                    model_data = {
                        **model_data,
                        "cube_metadata": cube_metadata,
                    }
            except Exception:
                logger.exception(
                    "Top N metadata fallback unavailable; continuing with model semantic layer"
                )
            kpi_provider = app().KpiProvider(model_data, domain)
            user_query = kpi_provider.user_query
            found, token_usage = kpi_provider.top_kpis()
            logger.info("Top N questions resolved count=%s domain=%s", len(found), domain)
            return "\n".join(found)
        except Exception as exc:
            request_status = "ERROR"
            error_message = "; ".join(error_messages_from_exception(exc))
            messages = error_messages_from_exception(exc)
            logger.exception(
                "Top N questions failed user=%s domain=%s model=%s messages=%s",
                username,
                domain,
                model_file_name,
                messages,
            )
            return json_response({"error": messages})
        finally:
            audit_llm_usage_async(
                endpoint="/analyst",
                user_id=user_id,
                session_cookie=session_cookie,
                base_url=base_url,
                user_query=user_query,
                token_usage=token_usage,
                request_status=request_status,
                error_message=error_message,
            )
