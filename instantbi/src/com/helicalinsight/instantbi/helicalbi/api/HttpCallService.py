import json
import logging

import requests
import urllib3

from helicalbi.api.ApiCallCache import get as cache_get, set as cache_set
from helicalbi.common import app_config
from helicalbi.common.ErrorMessages import service_api_error_message
from helicalbi.common.auth import (
    get_api_cache_org_id,
    get_api_cache_orgname,
    get_api_cache_username,
    downstream_request_headers,
)
from helicalbi.common.configuration import baseUrl

logger = logging.getLogger(__name__)

_BORDER = "--------------"

_NON_CACHEABLE_SERVICES = {"executeQuery", "generateQuery"}
_SOFT_FAILURE_SERVICES = {"executeQuery"}


class ServiceApiError(RuntimeError):
    """Raised when an HI ``/services`` call returns a non-success status."""


def _is_cacheable(service_json: dict) -> bool:
    return service_json.get("service") not in _NON_CACHEABLE_SERVICES


def raise_if_service_failed(api_response, service: str = "") -> None:
    """Stop processing when a HI service payload has ``status`` other than 1."""
    if not isinstance(api_response, dict):
        raise ServiceApiError("Service API call failed.")
    if "status" not in api_response or api_response.get("status") == 1:
        return
    message = service_api_error_message(api_response)
    logger.error(
        "Service API returned failure service=%s status=%s message=%s",
        service or api_response.get("service") or "",
        api_response.get("status"),
        message,
    )
    raise ServiceApiError(message)


def fetch_service_api(*, session_cookie: str, service_json: dict) -> dict:
    if app_config.show_api_call_log:
        logger.info("Calling service API with session cookie")
    username = get_api_cache_username()
    orgname = get_api_cache_orgname()
    org_id = get_api_cache_org_id()
    form_data = str(service_json.get("formData") or "")
    service_name = str(service_json.get("service") or "")

    if _is_cacheable(service_json):
        cached = cache_get(form_data, username, orgname, org_id)
        if cached is not None:
            _log_api_io(service_json, cached, cached=True)
            if service_name not in _SOFT_FAILURE_SERVICES:
                raise_if_service_failed(cached, service_name)
            return cached

    api_response_json = None
    api_url = "%s/services" % baseUrl

    session = requests.Session()

    # disable this when you are moving to production.
    urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

    if session_cookie:
        session.cookies.set("JSESSIONID", session_cookie)
    try:
        forwarded_headers = downstream_request_headers()
        api_response = session.post(
            api_url,
            data=service_json,
            headers=forwarded_headers or None,
            verify=False,
        )
    except requests.RequestException:
        logger.exception(
            "Service API request failed service=%s url=%s",
            service_name,
            api_url,
        )
        raise

    if api_response.status_code == 200:
        try:
            api_response_json = api_response.json()
        except ValueError:
            logger.exception(
                "Service API returned non-JSON body status=%s service=%s",
                api_response.status_code,
                service_name,
            )
            raise
        _log_api_io(service_json, api_response_json)
        if service_name not in _SOFT_FAILURE_SERVICES:
            raise_if_service_failed(api_response_json, service_name)
        if _is_cacheable(service_json) and isinstance(api_response_json, dict):
            cache_set(form_data, username, orgname, api_response_json, org_id)
    else:
        logger.error(
            "Service API call failed with status code=%s service=%s body=%s",
            api_response.status_code,
            service_name,
            api_response.text,
        )
        _log_api_io(
            service_json,
            {"status_code": api_response.status_code, "body": api_response.text},
        )
    return api_response_json


def _log_api_io(service_json: dict, output: object, *, cached: bool = False) -> None:
    if not app_config.show_api_call_log:
        return
    output_text = (
        json.dumps(output, indent=2, default=str)
        if isinstance(output, (dict, list))
        else str(output)
    )
    logger.info(
        "%s\nAPI %sInput:\n%s\n%s\nAPI Output:\n%s\n%s",
        _BORDER,
        "Cache " if cached else "",
        json.dumps(service_json, indent=2, default=str),
        _BORDER,
        output_text,
        _BORDER,
    )


