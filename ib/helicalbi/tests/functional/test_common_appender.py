"""Functional tests for ``helicalbi.common.CommonAppender``."""
import pytest

from helicalbi.common.CommonAppender import append_to_workflow


pytestmark = pytest.mark.functional


class TestAppendToWorkflow:
    def test_creates_flow_when_missing(self):
        state = {}
        append_to_workflow("step-1", state)
        assert state["flow"] == ["step-1"]

    def test_appends_to_existing_flow(self):
        state = {"flow": ["a", "b"]}
        append_to_workflow("c", state)
        assert state["flow"] == ["a", "b", "c"]

    def test_preserves_other_keys_in_state(self):
        state = {"flow": [], "user": "ada"}
        append_to_workflow("x", state)
        assert state["user"] == "ada"
        assert state["flow"] == ["x"]
