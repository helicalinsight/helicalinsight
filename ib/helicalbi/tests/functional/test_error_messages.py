"""Functional tests for ``helicalbi.common.ErrorMessages``."""

import pytest

from helicalbi.common.ErrorMessages import (
    extract_message_from_stack_trace,
    normalize_service_error_message,
)


pytestmark = pytest.mark.functional


class TestErrorMessages:
    def test_extract_message_from_stack_trace(self):
        stack = (
            "Traceback (most recent call last):\n"
            '  File "app.py", line 10, in handler\n'
            '    raise RuntimeError("metadata unavailable")\n'
            "RuntimeError: metadata unavailable\n"
        )
        assert extract_message_from_stack_trace(stack) == "metadata unavailable"

    def test_normalize_service_error_message_strips_error_prefix(self):
        message = normalize_service_error_message(
            "Error: The file missing.agent doesn't exists. Aborting operation."
        )
        assert message == "The file missing.agent doesn't exists. Aborting operation."
