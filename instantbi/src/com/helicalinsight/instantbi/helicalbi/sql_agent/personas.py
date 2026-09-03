"""Map user role/profile to BI personas."""
from __future__ import annotations

from typing import Any, Dict, Iterable, List, Mapping, Optional, Sequence, Tuple

from helicalbi.sql_agent.strategy_tree import (
    STRATEGY_INVERTED_PYRAMID,
    STRATEGY_INTENT_LAYOUT,
    STRATEGY_MECE,
    STRATEGY_PROGRESSIVE_DISCLOSURE,
    STRATEGY_SEMANTIC_COLOR,
    STRATEGY_SO_WHAT,
)

PERSONA_EXECUTIVE = "executive"
PERSONA_OPERATIONAL = "operational_manager"
PERSONA_TACTICAL = "tactical_manager"
PERSONA_ANALYST = "analyst"
DEFAULT_PERSONA = PERSONA_TACTICAL

TIER_STRATEGIC = "strategic"
TIER_OPERATIONAL = "operational"
TIER_TACTICAL = "tactical"
TIER_ANALYTICAL = "analytical"

PERSONA_TIERS: Dict[str, str] = {
    PERSONA_EXECUTIVE: TIER_STRATEGIC,
    PERSONA_OPERATIONAL: TIER_OPERATIONAL,
    PERSONA_TACTICAL: TIER_TACTICAL,
    PERSONA_ANALYST: TIER_ANALYTICAL,
}

PERSONA_FOCUS = {
    PERSONA_EXECUTIVE: "Strategic: high-level health, macro trends, and financial goals.",
    PERSONA_OPERATIONAL: "Operational: real-time tracking, immediate blockers, and daily tasks.",
    PERSONA_TACTICAL: "Tactical: mid-term optimization, campaigns, and short-term trends.",
    PERSONA_ANALYST: "Analytical: deep exploration, hypotheses, and root-cause analysis.",
}

PERSONA_ALIASES = {
    "executive": PERSONA_EXECUTIVE,
    "c-suite": PERSONA_EXECUTIVE,
    "csuite": PERSONA_EXECUTIVE,
    "ceo": PERSONA_EXECUTIVE,
    "cfo": PERSONA_EXECUTIVE,
    "cmo": PERSONA_EXECUTIVE,
    "cto": PERSONA_EXECUTIVE,
    "coo": PERSONA_EXECUTIVE,
    "chief": PERSONA_EXECUTIVE,
    "board": PERSONA_EXECUTIVE,
    "vp": PERSONA_EXECUTIVE,
    "president": PERSONA_EXECUTIVE,
    "operational": PERSONA_OPERATIONAL,
    "operational_manager": PERSONA_OPERATIONAL,
    "operations": PERSONA_OPERATIONAL,
    "ops": PERSONA_OPERATIONAL,
    "store manager": PERSONA_OPERATIONAL,
    "store_mgr": PERSONA_OPERATIONAL,
    "supply chain": PERSONA_OPERATIONAL,
    "warehouse": PERSONA_OPERATIONAL,
    "fulfillment": PERSONA_OPERATIONAL,
    "campaign manager": PERSONA_OPERATIONAL,
    "tactical": PERSONA_TACTICAL,
    "tactical_manager": PERSONA_TACTICAL,
    "product owner": PERSONA_TACTICAL,
    "product manager": PERSONA_TACTICAL,
    "marketing manager": PERSONA_TACTICAL,
    "marketing mgr": PERSONA_TACTICAL,
    "analyst": PERSONA_ANALYST,
    "data analyst": PERSONA_ANALYST,
    "data scientist": PERSONA_ANALYST,
    "bi specialist": PERSONA_ANALYST,
    "power user": PERSONA_ANALYST,
    "power_user": PERSONA_ANALYST,
}

PERSONA_KEYWORD_GROUPS: Tuple[Tuple[str, Tuple[str, ...]], ...] = (
    (
        PERSONA_EXECUTIVE,
        ("ceo", "cfo", "cmo", "cto", "coo", "chief", "c-suite", "csuite", "executive", "board", "president", "vice president"),
    ),
    (
        PERSONA_ANALYST,
        ("analyst", "data scientist", "bi specialist", "power user", "statistician"),
    ),
    (
        PERSONA_OPERATIONAL,
        (
            "supply chain",
            "store manager",
            "warehouse",
            "fulfillment",
            "operations",
            "operational",
            "shift lead",
            "campaign manager",
        ),
    ),
    (
        PERSONA_TACTICAL,
        ("product owner", "product manager", "marketing manager", "tactical"),
    ),
)


def _as_list(value: Any) -> List[Any]:
    if value is None:
        return []
    if isinstance(value, list):
        return list(value)
    return [value]


def role_names(user_role: Optional[Sequence[Any]]) -> List[str]:
    names: List[str] = []
    for item in _as_list(user_role):
        if isinstance(item, str):
            text = item.strip()
        elif isinstance(item, Mapping):
            text = str(
                item.get("roleName")
                or item.get("role_name")
                or item.get("name")
                or item.get("role")
                or ""
            ).strip()
        else:
            text = str(item or "").strip()
        if text:
            names.append(text)
    return names


def profile_pairs(user_profile: Optional[Sequence[Any]]) -> List[Tuple[str, str]]:
    pairs: List[Tuple[str, str]] = []
    for item in _as_list(user_profile):
        if not isinstance(item, Mapping):
            continue
        key = str(item.get("name") or item.get("key") or item.get("id") or "").strip()
        value = str(item.get("value") or item.get("profile_value") or "").strip()
        if key or value:
            pairs.append((key, value))
    return pairs


def _normalize_hint(hint: Optional[str]) -> str:
    raw = " ".join(str(hint or "").strip().lower().replace("-", " ").replace("_", " ").split())
    return PERSONA_ALIASES.get(raw, "")


def _blob(parts: Iterable[str]) -> str:
    return " ".join(part.strip().lower() for part in parts if str(part or "").strip())


def resolve_persona(
    user_role: Optional[Sequence[Any]] = None,
    user_profile: Optional[Sequence[Any]] = None,
    *,
    hint: Optional[str] = None,
) -> Dict[str, Any]:
    """Pick a dashboard persona from an explicit hint, then roles, then profile."""
    hinted = _normalize_hint(hint)
    roles = role_names(user_role)
    pairs = profile_pairs(user_profile)
    profile_values = [value for _, value in pairs]
    title_keys = {"persona", "title", "designation", "job_title", "jobtitle", "role", "user_type"}
    title_values = [value for key, value in pairs if key.lower().replace(" ", "_") in title_keys]
    search = _blob([hinted, *roles, *title_values, *profile_values])

    persona = hinted or DEFAULT_PERSONA
    if not hinted:
        for candidate, keywords in PERSONA_KEYWORD_GROUPS:
            if any(keyword.strip() in search for keyword in keywords if keyword.strip()):
                persona = candidate
                break

    return {
        "name": persona,
        "tier": PERSONA_TIERS.get(persona, TIER_TACTICAL),
        "focus": PERSONA_FOCUS.get(persona, PERSONA_FOCUS[DEFAULT_PERSONA]),
        "roles": roles,
        "profile": [{"name": key, "value": value} for key, value in pairs],
    }


def persona_prompt_block(persona: Mapping[str, Any]) -> str:
    strategy = persona.get("strategy") if isinstance(persona.get("strategy"), Mapping) else {}
    strategy_id = str(
        persona.get("strategy_id") or strategy.get("id") or ""
    ).strip()
    if not strategy_id:
        names = [str(item) for item in (persona.get("strategies") or []) if item]
        strategy_id = names[0] if names else ""
    return "\n".join(
        [
            f"Persona: {persona.get('name') or DEFAULT_PERSONA}",
            f"Dashboard tier: {persona.get('tier') or TIER_TACTICAL}",
            f"Analytical focus: {persona.get('focus') or ''}",
            f"User roles: {', '.join(persona.get('roles') or []) or '(none)'}",
            "User profile: "
            + (
                ", ".join(
                    f"{item.get('name')}={item.get('value')}"
                    for item in (persona.get("profile") or [])
                    if isinstance(item, Mapping)
                )
                or "(none)"
            ),
            f"Selected strategy: {strategy_id or '(pending decision tree)'}",
        ]
    )
