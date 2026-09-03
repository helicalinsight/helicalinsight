"""Token-aware agent modes: fast | balanced | research."""
from __future__ import annotations

from dataclasses import dataclass
from typing import Any, Dict, Mapping, Optional, Sequence, Tuple


MODE_FAST = "fast"
MODE_BALANCED = "balanced"
MODE_RESEARCH = "research"
DEFAULT_MODE = MODE_BALANCED
VALID_MODES = (MODE_FAST, MODE_BALANCED, MODE_RESEARCH)

# Dashboard graph cycle is planner → tools → apply_patches (3 nodes per tool loop),
# then synthesizer → dashboard. LangGraph recursion_limit counts node executions,
# not planner loops — so it must be at least max_tool_loops * 3 plus exit overhead.
DASHBOARD_NODES_PER_TOOL_LOOP = 3
DASHBOARD_GRAPH_EXIT_OVERHEAD = 8


def recursion_limit_for_loops(max_tool_loops: int) -> int:
    """LangGraph steps needed so max_tool_loops can fire before GRAPH_RECURSION_LIMIT."""
    loops = max(1, int(max_tool_loops or 1))
    return loops * DASHBOARD_NODES_PER_TOOL_LOOP + DASHBOARD_GRAPH_EXIT_OVERHEAD

# Tool names available per mode (planner bind list).
FAST_TOOLS: Tuple[str, ...] = (
    "retrieve_semantic_model",
    "generate_sql",
    "execute_query",
    "build_report",
    "finish_dashboard",
)
BALANCED_TOOLS: Tuple[str, ...] = (
    "retrieve_semantic_model",
    "retrieve_schema",
    "generate_sql",
    "execute_query",
    "analyze_result",
    "build_report",
    "finish_dashboard",
)
RESEARCH_TOOLS: Tuple[str, ...] = (
    "retrieve_semantic_model",
    "retrieve_schema",
    "generate_sql",
    "validate_sql",
    "execute_query",
    "analyze_result",
    "build_report",
    "finish_dashboard",
)


@dataclass(frozen=True)
class AgentModeProfile:
    name: str
    max_charts: int
    max_tool_loops: int
    schema_top_k: int
    overview_chars: int
    findings_result_chars: int
    recursion_limit: int
    use_llm_synthesizer: bool
    history_tool_rounds: int
    reuse_semantic: bool
    skip_intent_rephrase: bool
    skip_viz_polish: bool
    tool_names: Tuple[str, ...]
    mode_rules: str


AGENT_MODES: Dict[str, AgentModeProfile] = {
    MODE_FAST: AgentModeProfile(
        name=MODE_FAST,
        max_charts=2,
        max_tool_loops=10,
        schema_top_k=3,
        overview_chars=1200,
        findings_result_chars=200,
        recursion_limit=recursion_limit_for_loops(10),
        use_llm_synthesizer=False,
        history_tool_rounds=2,
        reuse_semantic=True,
        skip_intent_rephrase=True,
        skip_viz_polish=True,
        tool_names=FAST_TOOLS,
        mode_rules=(
            "MODE=fast (low tokens). Aim for 1-2 charts only. Call retrieve_semantic_model "
            "once; later facets reuse that context. Skip retrieve_schema / validate_sql / "
            "analyze_result (not available). Prefer chart steps. Finish as soon as the core "
            "question has a usable chart."
        ),
    ),
    MODE_BALANCED: AgentModeProfile(
        name=MODE_BALANCED,
        max_charts=5,
        max_tool_loops=24,
        schema_top_k=5,
        overview_chars=3500,
        findings_result_chars=800,
        recursion_limit=recursion_limit_for_loops(24),
        use_llm_synthesizer=True,
        history_tool_rounds=4,
        reuse_semantic=True,
        skip_intent_rephrase=True,
        skip_viz_polish=False,
        tool_names=BALANCED_TOOLS,
        mode_rules=(
            "MODE=balanced. Build a clear multi-step picture with about 3 complementary "
            "charts (KPI, breakdown, trend). Use semantic RAG first; metadata RAG only if "
            "needed. Reuse semantic context when possible. Finish when the picture is enough."
        ),
    ),
    MODE_RESEARCH: AgentModeProfile(
        name=MODE_RESEARCH,
        max_charts=8,
        max_tool_loops=40,
        schema_top_k=8,
        overview_chars=8000,
        findings_result_chars=2000,
        recursion_limit=recursion_limit_for_loops(40),
        use_llm_synthesizer=True,
        history_tool_rounds=8,
        reuse_semantic=False,
        skip_intent_rephrase=False,
        skip_viz_polish=False,
        tool_names=RESEARCH_TOOLS,
        mode_rules=(
            "MODE=research (deeper, more tokens). Prefer thorough multi-step investigation: "
            "lookups plus complementary KPI/breakdown/trend/comparison charts. Use semantic "
            "and metadata RAG carefully. Do not finish until the picture is complete."
        ),
    ),
}


def normalize_mode(mode: Optional[str], default: str = DEFAULT_MODE) -> str:
    raw = str(mode or default or DEFAULT_MODE).strip().lower()
    aliases = {
        "quick": MODE_FAST,
        "cheap": MODE_FAST,
        "default": MODE_BALANCED,
        "normal": MODE_BALANCED,
        "deep": MODE_RESEARCH,
        "thorough": MODE_RESEARCH,
    }
    raw = aliases.get(raw, raw)
    if raw not in AGENT_MODES:
        return default if default in AGENT_MODES else DEFAULT_MODE
    return raw


def resolve_mode_profile(
    mode: Optional[str] = None,
    *,
    default_mode: str = DEFAULT_MODE,
    config_max_charts: Optional[int] = None,
) -> AgentModeProfile:
    """Resolve mode and apply config chart ceiling when provided."""
    name = normalize_mode(mode, default_mode)
    profile = AGENT_MODES[name]
    if config_max_charts is None:
        return profile
    ceiling = max(1, int(config_max_charts))
    capped = min(profile.max_charts, ceiling)
    if capped == profile.max_charts:
        return profile
    return AgentModeProfile(
        name=profile.name,
        max_charts=capped,
        max_tool_loops=profile.max_tool_loops,
        schema_top_k=profile.schema_top_k,
        overview_chars=profile.overview_chars,
        findings_result_chars=profile.findings_result_chars,
        recursion_limit=profile.recursion_limit,
        use_llm_synthesizer=profile.use_llm_synthesizer,
        history_tool_rounds=profile.history_tool_rounds,
        reuse_semantic=profile.reuse_semantic,
        skip_intent_rephrase=profile.skip_intent_rephrase,
        skip_viz_polish=profile.skip_viz_polish,
        tool_names=profile.tool_names,
        mode_rules=profile.mode_rules,
    )


def truncate_text(text: str, limit: int) -> str:
    value = text or ""
    if limit <= 0 or len(value) <= limit:
        return value
    return value[: max(0, limit - 16)].rstrip() + "\n...[truncated]"


def mode_to_public_dict(profile: AgentModeProfile) -> Dict[str, Any]:
    return {
        "name": profile.name,
        "max_charts": profile.max_charts,
        "max_tool_loops": profile.max_tool_loops,
        "use_llm_synthesizer": profile.use_llm_synthesizer,
        "history_tool_rounds": profile.history_tool_rounds,
        "reuse_semantic": profile.reuse_semantic,
        "skip_intent_rephrase": profile.skip_intent_rephrase,
        "skip_viz_polish": profile.skip_viz_polish,
    }


def profile_from_state(state: Mapping[str, Any]) -> AgentModeProfile:
    return resolve_mode_profile(state.get("agent_mode"), config_max_charts=None)


def tool_names_for_mode(mode: Optional[str]) -> Sequence[str]:
    return resolve_mode_profile(mode).tool_names
