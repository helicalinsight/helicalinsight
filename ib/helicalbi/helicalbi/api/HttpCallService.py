import json
import logging

import requests
import urllib3

from helicalbi.api.ApiCallCache import get as cache_get, set as cache_set
from helicalbi.common import app_config
from helicalbi.common.auth import get_api_cache_orgname, get_api_cache_username
from helicalbi.common.configuration import baseUrl

logger = logging.getLogger(__name__)

_BORDER = "--------------"

_NON_CACHEABLE_SERVICES = {"executeQuery", "generateQuery"}


def _is_cacheable(service_json: dict) -> bool:
    return service_json.get("service") not in _NON_CACHEABLE_SERVICES


def fetch_service_api(*, session_cookie: str, service_json: dict) -> dict:
    if app_config.show_api_call_log:
        logger.info("Calling service API with session cookie")
    username = get_api_cache_username()
    orgname = get_api_cache_orgname()
    form_data = str(service_json.get("formData") or "")

    if _is_cacheable(service_json):
        cached = cache_get(form_data, username, orgname)
        if cached is not None:
            _log_api_io(service_json, cached, cached=True)
            return cached

    api_response_json = None
    api_url = "%s/services" % baseUrl

    session = requests.Session()

    # disable this when you are moving to production.
    urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

    session.cookies.set("JSESSIONID", session_cookie)
    api_response = session.post(api_url, data=service_json, verify=False)

    if api_response.status_code == 200:
        api_response_json = api_response.json()
        if _is_cacheable(service_json) and isinstance(api_response_json, dict):
            cache_set(form_data, username, orgname, api_response_json)
        _log_api_io(service_json, api_response_json)
    else:
        logger.warning(
            "Service API call failed with status code=%s body=%s",
            api_response.status_code,
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


