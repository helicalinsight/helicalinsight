#!/usr/bin/env python3
"""Manual script to execute SQL against hi-ee using credentials and metadata."""
from __future__ import annotations

import json
import sys
import uuid

import requests
import urllib3

# --- edit these values ---
BASE_URL = "https://164.52.206.202/hi-ee"
USERNAME = "hiadmin"
PASSWORD = "hiadmin"
ORGANIZATION = ""
MD_LOCATION = "8916"
MD_FILE_NAME = "mds.metadata"
#SQL = "SELECT \"travel_id\" from \"tdd\""
#SQL = "select \"t\".\"travel_id\"  , \"tdd\".\"travel_type\" from \"travel_details\" \"t\" inner join \"tdd\" on \"t\".\"travel_id\" =\"tdd\".\"travel_id\" fetch first 10 rows only"
SQL = "select \"t\".\"travel_id\"  as \"tid\" , \"td\".\"travel_type\" from \"travel_details\" \"t\" inner join \"tdd\" \"td\" on \"t\".\"travel_id\" =\"td\".\"travel_id\" fetch first 10 rows only"
# -------------------------


class HiEeApiClient:
    """Login with credentials and call hi-ee service APIs."""

    def __init__(
        self,
        base_url: str,
        username: str,
        password: str,
        organization: str = "",
    ) -> None:
        self.base_url = base_url.rstrip("/")
        self.username = username
        self.password = password
        self.organization = organization
        self.session = requests.Session()
        urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

    def login(self) -> None:
        credentials = {"username": self.username, "password": self.password}
        if self.organization:
            credentials["j_organization"] = self.organization

        response = self.session.post(
            f"{self.base_url}/login",
            data=credentials,
            verify=False,
        )
        if response.status_code != 200:
            raise RuntimeError(
                f"Login failed with status {response.status_code}: {response.text}"
            )
        if not self.session.cookies.get("JSESSIONID"):
            raise RuntimeError("Login succeeded but JSESSIONID cookie was not set.")
        print("Login is success=================================")

    def logout(self) -> None:
        self.session.post(f"{self.base_url}/logout", verify=False)

    def call_service(self, service_json: dict) -> dict | None:
        response = self.session.post(
            f"{self.base_url}/ai/agent-dashboard",
            data=service_json,
            verify=False,
        )
        print(f"{self.base_url}/ai/agent-dashboard")
        print(f"{service_json}")
        if response.status_code == 200:
            return response.json()
        print(
            f"Service call failed with status {response.status_code}: {response.text}",
            file=sys.stderr,
        )
        return None


    def execute_query(
        self,
        md_location: str,
        md_file_name: str,
        sql: str,
        request_id: str | None = None,
    ) -> dict | None:

        payload = {
            "input": "Where are we spending too much on travel, and how can we reduce the cost?",
            "dashboardid": "0b4036ab-08f9-4d3c-8bea-144ddb928c98",
            "dashboard_sequence_id": "13",
            "subject": "eyJtb2RlbCI6eyJmaWxlIjoiYWlnZW4ubW9kZWwiLCJkaXIiOiIwMDA3In19",
            "requestId": request_id or str(uuid.uuid4()),
        }
        return self.call_service(payload)


def main() -> int:
    print(f"base_url  : {BASE_URL}")
    print(f"username  : {USERNAME}")
    print(f"location  : {MD_LOCATION}")
    print(f"file_name : {MD_FILE_NAME}")
    print(f"sql       : {SQL}")
    print()

    client = HiEeApiClient(BASE_URL, USERNAME, PASSWORD, ORGANIZATION)
    try:
        print("Logging in...")
        client.login()

        print("Executing query...")
        api_response = client.execute_query(MD_LOCATION, MD_FILE_NAME, SQL)
    finally:
        client.logout()

    if api_response is None:
        return 1

    print(json.dumps(api_response, indent=2, default=str))

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
