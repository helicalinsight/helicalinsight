"""Helpers for turning exceptions and service failures into user-facing messages."""


def extract_message_from_stack_trace(stack_trace: str) -> str:
    """Return the user-facing message from a traceback string."""
    lines = [line.strip() for line in stack_trace.strip().splitlines() if line.strip()]
    for line in reversed(lines):
        if line.startswith("Traceback"):
            continue
        if line.startswith("File ") or line.startswith('File "'):
            continue
        if ": " in line and not line.endswith(":"):
            _, message = line.split(": ", 1)
            if message.strip():
                return message.strip()
        return line
    return stack_trace.strip()


def normalize_service_error_message(message: str) -> str:
    if message.startswith("Error: "):
        message = message[len("Error: ") :]
    if "Traceback (most recent call last)" in message:
        message = extract_message_from_stack_trace(message)
    return message.strip()


def service_api_error_message(
    api_response, default: str = "Service API call failed."
) -> str:
    """Read the user-facing ``message`` from a HI ``/services`` payload."""
    if not isinstance(api_response, dict):
        return default

    response = api_response.get("response")
    raw = ""
    if isinstance(response, dict):
        raw = response.get("message") or response.get("error") or ""
    elif isinstance(response, str):
        raw = response
    if not raw:
        raw = api_response.get("message") or ""
    if not raw:
        return default
    return normalize_service_error_message(str(raw)) or default
