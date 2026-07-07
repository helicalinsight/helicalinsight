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
