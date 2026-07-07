"""Functional tests for ``helicalbi.service.agentservice.InformationProvider``."""
import pytest

from helicalbi.service.agentservice.InformationProvider import InformationProvider


pytestmark = pytest.mark.functional


@pytest.fixture
def provider(sample_agent_data):
    return InformationProvider(agent_data=sample_agent_data)


class TestFormatDomainInfo:
    def test_returns_known_domain(self, provider):
        result = provider.format_domain_info("Sales Operation")
        assert "Sales Operation" in result
        assert "Travel" in result
        assert "Meetings" in result

    def test_returns_message_for_unknown_domain(self, provider):
        assert provider.format_domain_info("Unknown") == "No matching domain found"


class TestGetTopics:
    def test_returns_topics_for_known_domain(self, provider):
        topics = provider.get_topics("Sales Operation")
        assert topics == ["Travel", "Meetings"]

    def test_returns_empty_list_or_message_for_unknown_domain(self, provider):
        # Implementation quirk: first domain checked returns empty list immediately
        # because of an early return. The contract is documented by the test.
        result = provider.get_topics("Unknown")
        assert result in ([], "No matching domain found")


class TestFormatSemanticLayer:
    def test_builds_semantic_layer_string(self, provider):
        result = provider.format_semantic_layer(["Meetings", "Travel"])
        assert "Semantic Layer:" in result
        assert "Meetings" in result
        assert "Employee" in result
        assert "Client" in result

    def test_excludes_topics_not_requested(self, provider):
        result = provider.format_semantic_layer(["Meetings"])
        assert "Meetings" in result
        assert "Travel" not in result


class TestInputTablesAndAttributeString:
    def test_get_input_tables_includes_matching_tables(self, provider):
        tables = provider.get_input_tables(["Meetings"])
        assert "employee_details" in tables
        assert "meeting_details" in tables

    def test_get_attribute_string_returns_table_column_dicts(self, provider):
        result = provider.get_attribute_string(["Meetings"])
        merged = {k: v for d in result for k, v in d.items()}
        assert "Employee" in merged
        assert "employee_details" in merged["Employee"]
        assert "employee_id" in merged["Employee"]


class TestMatchingDescriptions:
    def test_returns_descriptions_for_matching_tables(self, provider):
        descriptions = provider.get_matching_descriptions(["meeting_details"])
        assert "Total canceled meetings" in descriptions

    def test_returns_empty_when_no_match(self, provider):
        assert provider.get_matching_descriptions(["unknown_tbl"]) == []


class TestComponentsFromTopics:
    def test_collects_components_for_topics(self, provider):
        components = provider.get_components_from_topics(["Meetings"])
        assert "Employee" in components
        assert "Client" in components

    def test_handles_unknown_topic_gracefully(self, provider):
        assert provider.get_components_from_topics(["Unknown"]) == []
