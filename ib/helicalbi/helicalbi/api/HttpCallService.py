import logging

import requests
import urllib3

from helicalbi.common.configuration import baseUrl

logger = logging.getLogger(__name__)


def fetch_service_api(*, session_cookie: str, service_json: dict) -> dict:
    logger.info("Calling service API with session cookie")
    api_response_json = None
    api_url = "%s/services" % baseUrl

    session = requests.Session()

    # disable this when you are moving to production.
    urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

    session.cookies.set("JSESSIONID", session_cookie)
    api_response = session.post(api_url, data=service_json, verify=False)

    if api_response.status_code == 200:
        api_response_json = api_response.json()
    else:
        logger.warning(
            "Service API call failed with status code=%s body=%s",
            api_response.status_code,
            api_response.text,
        )
    return api_response_json
