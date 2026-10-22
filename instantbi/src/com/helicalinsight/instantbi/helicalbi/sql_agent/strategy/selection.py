"""Pick a dashboard consulting strategy via hint or the JSON decision tree."""
from __future__ import annotations

import logging
from typing import Any, Mapping, Optional, Sequence

from helicalbi.sql_agent.constants import (
    CAP_KEY_APPLICABLE_TO,
    CAP_KEY_APPLICABLE_TO_SNAKE,
    CAP_KEY_CAPABILITY,
    CATALOG_KEY_DECISION_TREE,
    CATALOG_KEY_DEFAULT_STRATEGY,
    CATALOG_KEY_INTENTS,
    CATALOG_KEY_LAYOUT,
    CATALOG_KEY_RULES,
    CONTEXT_INTENT,
    CONTEXT_PERSONA,
    CONTEXT_STRATEGY_HINT,
    INTENT_OVERVIEW,
    LOG_STRATEGY_SELECTED,
    PERSONA_KEY_NAME,
    PERSONA_KEY_STRATEGY_HINT,
    PERSONA_KEY_STRATEGY_ID,
    PERSONA_KEY_STRATEGIES,
    PERSONA_KEY_STRATEGY,
    ATTACH_DROPPED_KEYS,
    SELECTED_DIAGNOSTIC_CAPABILITY_KEY,
    SELECTION_KEY,
    SELECTION_SOURCE_DECISION_TREE,
    SELECTION_SOURCE_HINT,
    LAYOUT_ALIASES,
    STRATEGY_MECE,
    TREE_KEY_CASES,
    TREE_KEY_DEFAULT,
    TREE_KEY_EQUALS,
    TREE_KEY_IN,
    TREE_KEY_MATCH,
    TREE_KEY_STRATEGY,
    TREE_KEY_THEN,
)
from helicalbi.sql_agent.strategy.catalog import get_strategy, list_strategies, load_strategy_catalog
from helicalbi.sql_agent.strategy.diagnostics import (
    iter_applicable_measures,
    measure_mentioned,
)

logger = logging.getLogger(__name__)


class IntentClassifier:
    """First matching intent from the catalog keyword list; otherwise overview."""

    def classify(self, question: str, catalog: Optional[Mapping[str, Any]] = None) -> str:
        text = f" {str(question or '').strip().lower()} "
        intents = (catalog or load_strategy_catalog()).get(CATALOG_KEY_INTENTS) or []
        for intent in intents:
            if not isinstance(intent, Mapping):
                continue
            intent_id = str(intent.get("id") or "").strip()
            keywords = intent.get("keywords") or []
            if not intent_id:
                continue
            for keyword in keywords:
                needle = str(keyword or "").strip().lower()
                if needle and needle in text:
                    return intent_id
        return INTENT_OVERVIEW


class DecisionTreeWalker:
    """Walk the catalog decision tree with persona / intent / hint context."""

    @staticmethod
    def _context_value(context: Mapping[str, Any], field: str) -> str:
        return str(context.get(field) or "").strip().lower()

    @staticmethod
    def _case_matches(case: Mapping[str, Any], value: str) -> bool:
        if case.get(TREE_KEY_DEFAULT):
            return False
        if TREE_KEY_EQUALS in case:
            return value == str(case.get(TREE_KEY_EQUALS) or "").strip().lower()
        allowed = case.get(TREE_KEY_IN) or []
        if isinstance(allowed, (list, tuple)):
            return value in {str(item).strip().lower() for item in allowed}
        return False

    def walk(
        self,
        node: Mapping[str, Any],
        context: Mapping[str, Any],
        catalog: Mapping[str, Any],
    ) -> str:
        if not isinstance(node, Mapping):
            return str(catalog.get(CATALOG_KEY_DEFAULT_STRATEGY) or STRATEGY_MECE)
        if node.get(TREE_KEY_STRATEGY):
            return str(node[TREE_KEY_STRATEGY])
        field = str(node.get(TREE_KEY_MATCH) or "").strip()
        value = self._context_value(context, field) if field else ""
        cases = node.get(TREE_KEY_CASES) or []
        default_case: Optional[Mapping[str, Any]] = None
        for case in cases:
            if not isinstance(case, Mapping):
                continue
            if case.get(TREE_KEY_DEFAULT):
                default_case = case
                continue
            if self._case_matches(case, value):
                if case.get(TREE_KEY_STRATEGY):
                    return str(case[TREE_KEY_STRATEGY])
                if isinstance(case.get(TREE_KEY_THEN), Mapping):
                    return self.walk(case[TREE_KEY_THEN], context, catalog)
        if default_case:
            if default_case.get(TREE_KEY_STRATEGY):
                return str(default_case[TREE_KEY_STRATEGY])
            if isinstance(default_case.get(TREE_KEY_THEN), Mapping):
                return self.walk(default_case[TREE_KEY_THEN], context, catalog)
        return str(catalog.get(CATALOG_KEY_DEFAULT_STRATEGY) or STRATEGY_MECE)

    def select_id(
        self,
        catalog: Mapping[str, Any],
        context: Mapping[str, Any],
    ) -> str:
        """First matching layout rule, otherwise the nested decision tree."""
        rules = catalog.get(CATALOG_KEY_RULES) or []
        if isinstance(rules, list) and rules:
            intent = self._context_value(context, CONTEXT_INTENT)
            persona = self._context_value(context, CONTEXT_PERSONA)
            for rule in rules:
                if not isinstance(rule, Mapping):
                    continue
                rule_intent = str(rule.get(CONTEXT_INTENT) or "").strip().lower()
                rule_persona = str(rule.get(CONTEXT_PERSONA) or "").strip().lower()
                if rule_intent and rule_intent != intent:
                    continue
                if rule_persona and rule_persona != persona:
                    continue
                layout = str(rule.get(CATALOG_KEY_LAYOUT) or rule.get(TREE_KEY_STRATEGY) or "").strip()
                if layout:
                    return LAYOUT_ALIASES.get(layout, layout)
        tree = catalog.get(CATALOG_KEY_DECISION_TREE)
        if isinstance(tree, Mapping):
            return self.walk(tree, context, catalog)
        return str(catalog.get(CATALOG_KEY_DEFAULT_STRATEGY) or STRATEGY_MECE)


class StrategySelector:
    """Pick exactly one consulting strategy using hint, then the JSON decision tree."""

    def __init__(
        self,
        *,
        intents: Optional[IntentClassifier] = None,
        walker: Optional[DecisionTreeWalker] = None,
    ) -> None:
        self._intents = intents or IntentClassifier()
        self._walker = walker or DecisionTreeWalker()

    def select(
        self,
        question: str,
        *,
        persona: Optional[Mapping[str, Any]] = None,
        hint: Optional[str] = None,
        catalog: Optional[Mapping[str, Any]] = None,
        diagnostic_capabilities: Optional[Sequence[Mapping[str, Any]]] = None,
    ) -> dict[str, Any]:
        data = catalog or load_strategy_catalog()
        strategies = list_strategies(data)
        raw_hint = hint
        if not raw_hint and persona:
            raw_hint = persona.get(PERSONA_KEY_STRATEGY_HINT) or persona.get(
                PERSONA_KEY_STRATEGY_ID
            )
        hinted = str(raw_hint or "").strip().replace("-", "_").replace(" ", "_")
        hinted = LAYOUT_ALIASES.get(hinted, hinted)
        if hinted and hinted in strategies:
            chosen = dict(strategies[hinted])
            chosen[SELECTION_KEY] = {
                "source": SELECTION_SOURCE_HINT,
                "intent": self._intents.classify(question, data),
            }
            return chosen

        persona_name = str((persona or {}).get(PERSONA_KEY_NAME) or "").strip()
        intent = self._intents.classify(question, data)
        strategy_id = self._walker.select_id(
            data,
            {
                CONTEXT_PERSONA: persona_name,
                CONTEXT_INTENT: intent,
                CONTEXT_STRATEGY_HINT: hinted,
            },
        )
        chosen = get_strategy(strategy_id, data)
        matching_cap = self._match_capability(question, diagnostic_capabilities)
        chosen[SELECTION_KEY] = {
            "source": SELECTION_SOURCE_DECISION_TREE,
            "intent": intent,
            "persona": persona_name,
            "strategy_id": chosen.get("id"),
            SELECTED_DIAGNOSTIC_CAPABILITY_KEY: (
                matching_cap.get(CAP_KEY_CAPABILITY) if matching_cap else None
            ),
        }
        if matching_cap:
            chosen[SELECTED_DIAGNOSTIC_CAPABILITY_KEY] = matching_cap
        logger.info(
            LOG_STRATEGY_SELECTED,
            chosen.get("id"),
            intent,
            persona_name,
            matching_cap.get(CAP_KEY_CAPABILITY) if matching_cap else None,
        )
        return chosen

    @staticmethod
    def _match_capability(
        question: str,
        diagnostic_capabilities: Optional[Sequence[Mapping[str, Any]]],
    ) -> Optional[dict[str, Any]]:
        if not diagnostic_capabilities:
            return None
        q_lower = str(question or "").lower()
        for cap in diagnostic_capabilities:
            if not isinstance(cap, Mapping):
                continue
            applicable = iter_applicable_measures(
                cap.get(CAP_KEY_APPLICABLE_TO) or cap.get(CAP_KEY_APPLICABLE_TO_SNAKE)
            )
            cap_name = str(cap.get(CAP_KEY_CAPABILITY) or "").strip().lower()
            if (cap_name and cap_name in q_lower) or any(
                measure_mentioned(app, q_lower) for app in applicable
            ):
                return dict(cap)
        return None


class StrategyAttacher:
    """Copy persona and stash the selected strategy id."""

    def attach(
        self, persona: Mapping[str, Any], strategy: Mapping[str, Any]
    ) -> dict[str, Any]:
        attached = dict(persona or {})
        strategy_id = str(strategy.get("id") or "").strip()
        attached[PERSONA_KEY_STRATEGY_ID] = strategy_id
        attached[PERSONA_KEY_STRATEGIES] = [strategy_id] if strategy_id else []
        # Compact map for internal helpers; public_persona strips it.
        attached[PERSONA_KEY_STRATEGY] = {"id": strategy_id}
        for key in ATTACH_DROPPED_KEYS:
            attached.pop(key, None)
        return attached


_SELECTOR = StrategySelector()
_ATTACHER = StrategyAttacher()
_INTENTS = IntentClassifier()


def classify_intent(question: str, catalog: Optional[Mapping[str, Any]] = None) -> str:
    """First matching intent from the catalog keyword list; otherwise overview."""
    return _INTENTS.classify(question, catalog)


def select_strategy(
    question: str,
    *,
    persona: Optional[Mapping[str, Any]] = None,
    hint: Optional[str] = None,
    catalog: Optional[Mapping[str, Any]] = None,
    diagnostic_capabilities: Optional[Sequence[Mapping[str, Any]]] = None,
) -> dict[str, Any]:
    """Pick exactly one consulting strategy using hint, then the JSON decision tree."""
    return _SELECTOR.select(
        question,
        persona=persona,
        hint=hint,
        catalog=catalog,
        diagnostic_capabilities=diagnostic_capabilities,
    )


def attach_strategy(persona: Mapping[str, Any], strategy: Mapping[str, Any]) -> dict[str, Any]:
    """Copy persona and stash the selected strategy id (full catalog used at runtime)."""
    return _ATTACHER.attach(persona, strategy)
