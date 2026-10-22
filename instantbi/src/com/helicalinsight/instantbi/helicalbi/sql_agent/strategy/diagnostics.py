"""Diagnostic capability matching, use resolution, and measure-mention heuristics."""
from __future__ import annotations

import json
import re
from typing import Any, Mapping, Optional, Sequence

from helicalbi.sql_agent.constants import (
    ADDITIVITY_ADDITIVE,
    ADDITIVITY_NON_ADDITIVE_LABELS,
    ADDITIVITY_SEMI_ADDITIVE,
    APPLICABLE_NAME_KEYS,
    CAP_KEY_APPLICABLE_TO,
    CAP_KEY_APPLICABLE_TO_SNAKE,
    CAP_KEY_CAPABILITY,
    CAP_KEY_MATCHED_MEASURES,
    CAP_KEY_MEASURE_TRAITS,
    CAP_KEY_USES,
    DIRECTION_NEGATIVE,
    DIRECTION_NEUTRAL,
    DIRECTION_POSITIVE,
    EXPLICIT_STRATEGY_HINT_KINDS,
    GENERIC_MEASURE_TOKENS,
    KIND_DIMENSION,
    KIND_MEASURE,
    KIND_OTHER,
    KIND_STRATEGY,
    LONG_TOKEN_LENGTH,
    MIN_FORMULA_MEASURE_NAME_LENGTH,
    MIN_MATCHING_TOKENS,
    MIN_PHRASE_LENGTH,
    MIN_TOKEN_LENGTH,
    OPERATION_ALIASES,
    OPERATION_DECOMPOSE,
    OPERATION_KEY,
    SINGLE_TOKEN_MIN_LENGTH,
    STRATEGY_LOOKUP_HINT_KINDS,
    SYNONYM_SPLIT_PATTERN,
    TOKEN_PATTERN,
    TRAIT_ADDITIVE_NOTE,
    TRAIT_GRAIN_NOTE,
    TRAIT_KEY_ADDITIVITY,
    TRAIT_KEY_DIRECTION,
    TRAIT_KEY_GRAIN,
    TRAIT_KEY_NAME,
    TRAIT_NEGATIVE_NOTE,
    TRAIT_NEUTRAL_NOTE,
    TRAIT_NON_ADDITIVE_NOTE,
    TRAIT_NOTE_SEPARATOR,
    TRAIT_POSITIVE_NOTE,
    TRAIT_SEMI_ADDITIVE_NOTE,
    USE_FALLBACK_NAME_KEYS,
    USE_NAME_KEYS,
    DIAGNOSTIC_ACTIVITY_CAPABILITY,
    DIAGNOSTIC_ACTIVITY_DIMENSION,
    DIAGNOSTIC_ACTIVITY_MEASURE,
    DIAGNOSTIC_ACTIVITY_RULE,
    DIAGNOSTIC_ACTIVITY_STRATEGY,
    THINK_SCOPE_LOCK_DIMENSIONS,
    THINK_SCOPE_LOCK_MEASURES,
    THINK_SCOPE_LOCK_RULE,
    THINK_SCOPE_LOCK_STRATEGIES,
    USE_INPUT_RESERVED_KEYS,
    USE_PLAN_KEY_CAPABILITY,
    USE_PLAN_KEY_DIMENSIONS,
    USE_PLAN_KEY_KIND,
    USE_PLAN_KEY_MEASURES,
    USE_PLAN_KEY_NAME,
    USE_PLAN_KEY_SOURCE,
    USE_REF_KEY_HINTED_KIND,
    USE_REF_KEY_INPUTS,
    USE_SOURCE_FORMULA,
    USE_STRATEGY_KEYS,
    USE_TYPE_KEYS,
)
from helicalbi.sql_agent.strategy.text_utils import compact


def _unique_labels(names: Sequence[Any]) -> list[str]:
    seen: set[str] = set()
    labels: list[str] = []
    for name in names:
        label = str(name or "").strip()
        key = compact(label)
        if not label or key in seen:
            continue
        seen.add(key)
        labels.append(label)
    return labels


def _binding_names(value: Any) -> list[str]:
    """Names stored on a diagnostic input: a string, a list, or a JSON array string."""
    if isinstance(value, str):
        text = value.strip()
        if text.startswith("[") and text.endswith("]"):
            try:
                parsed = json.loads(text)
            except json.JSONDecodeError:
                parsed = None
            if isinstance(parsed, list):
                return _binding_names(parsed)
        return [text] if text else []
    if isinstance(value, Mapping):
        for key in ("measure", "name", "dimension", "fields"):
            found = _binding_names(value.get(key))
            if found:
                return found
        return []
    if isinstance(value, Sequence) and not isinstance(value, (str, bytes)):
        names: list[str] = []
        for item in value:
            names.extend(_binding_names(item))
        return names
    return []


def _within_one_edit(left: str, right: str) -> bool:
    """True when two tokens differ by a single insertion, deletion, or substitution."""
    if abs(len(left) - len(right)) > 1:
        return False
    if len(left) > len(right):
        left, right = right, left
    mismatches = 0
    index = 0
    longer = 0
    while index < len(left) and longer < len(right):
        if left[index] == right[longer]:
            index += 1
            longer += 1
            continue
        mismatches += 1
        if mismatches > 1:
            return False
        if len(left) == len(right):
            index += 1
        longer += 1
    return True


def _token_in_text(token: str, blob: str, blob_tokens: set[str]) -> bool:
    """Match a measure token exactly, or as a one-character typo when it is distinctive."""
    if token in blob:
        return True
    if len(token) < SINGLE_TOKEN_MIN_LENGTH:
        return False
    for other in blob_tokens:
        if abs(len(other) - len(token)) > 1:
            continue
        if _within_one_edit(token, other):
            return True
    return False


def _input_kind(key: str) -> str:
    token = compact(key)
    if "dimension" in token or token == "entityidentifier":
        return KIND_DIMENSION
    if token == "benchmark" or token in {"startingvalue", "endingvalue", "changecomponents"} or "measure" in token:
        return KIND_MEASURE
    return ""


class MeasureMentionDetector:
    """True when a measure name, synonym, or its distinctive tokens appear in text."""

    def mentioned(
        self,
        name: str,
        text: str,
        extra_phrases: Sequence[str] = (),
    ) -> bool:
        blob = str(text or "").lower()
        for phrase in (name, *extra_phrases):
            label = str(phrase or "").strip().lower()
            if len(label) >= MIN_PHRASE_LENGTH and label in blob:
                return True
        tokens = [
            token
            for token in re.findall(TOKEN_PATTERN, str(name or "").lower())
            if len(token) >= MIN_TOKEN_LENGTH and token not in GENERIC_MEASURE_TOKENS
        ]
        if not tokens:
            return False
        if len(tokens) == 1:
            return len(tokens[0]) >= SINGLE_TOKEN_MIN_LENGTH and tokens[0] in blob
        if any(len(token) >= LONG_TOKEN_LENGTH and token in blob for token in tokens):
            return True
        blob_tokens = set(re.findall(TOKEN_PATTERN, blob))
        return (
            len([token for token in tokens if _token_in_text(token, blob, blob_tokens)])
            >= MIN_MATCHING_TOKENS
        )


class ApplicableMeasuresNormalizer:
    """Normalize applicableTo entries that may be measure names or objects."""

    def normalize(self, applicable: Any) -> list[str]:
        names: list[str] = []
        for item in applicable or []:
            if isinstance(item, str):
                name = item.strip()
            elif isinstance(item, Mapping):
                name = ""
                for key in APPLICABLE_NAME_KEYS:
                    if str(item.get(key) or "").strip():
                        name = str(item.get(key)).strip()
                        break
            else:
                name = ""
            if name:
                names.append(name)
        return names


class DiagnosticUsesNormalizer:
    """Normalize diagnostic ``uses`` entries that may be names or typed objects."""

    def normalize(self, uses: Any) -> list[dict[str, Any]]:
        refs: list[dict[str, Any]] = []
        for item in uses or []:
            hinted = ""
            name = ""
            if isinstance(item, str):
                name = item.strip()
            elif isinstance(item, Mapping):
                for key in USE_TYPE_KEYS:
                    if str(item.get(key) or "").strip():
                        hinted = str(item.get(key)).strip().lower()
                        break
                for key in USE_NAME_KEYS:
                    if str(item.get(key) or "").strip():
                        name = str(item.get(key)).strip()
                        break
                if not name:
                    for key in USE_FALLBACK_NAME_KEYS:
                        if str(item.get(key) or "").strip():
                            name = str(item.get(key)).strip()
                            hinted = hinted or (
                                KIND_STRATEGY if key in USE_STRATEGY_KEYS else key
                            )
                            break
            if not name:
                continue
            ref: dict[str, Any] = {
                USE_PLAN_KEY_NAME: name,
                USE_REF_KEY_HINTED_KIND: hinted,
            }
            if isinstance(item, Mapping):
                measures, dimensions, inputs = self._input_bindings(item)
                if measures:
                    ref[USE_PLAN_KEY_MEASURES] = measures
                if dimensions:
                    ref[USE_PLAN_KEY_DIMENSIONS] = dimensions
                if inputs:
                    ref[USE_REF_KEY_INPUTS] = inputs
            refs.append(ref)
        return refs

    @staticmethod
    def _input_bindings(item: Mapping[str, Any]) -> tuple[list[str], list[str], dict[str, list[str]]]:
        measures: list[str] = []
        dimensions: list[str] = []
        inputs: dict[str, list[str]] = {}
        for key, value in item.items():
            if key in USE_INPUT_RESERVED_KEYS:
                continue
            names = _unique_labels(_binding_names(value))
            if not names:
                continue
            inputs[str(key)] = names
            kind = _input_kind(str(key))
            if kind == KIND_MEASURE:
                measures.extend(names)
            elif kind == KIND_DIMENSION:
                dimensions.extend(names)
        return _unique_labels(measures), _unique_labels(dimensions), inputs


class OperationNameCanonicalizer:
    """Map a diagnostic use onto an analytical operation, including aliases."""

    def canonical(
        self,
        name: str,
        operations: Sequence[Mapping[str, Any]],
    ) -> str:
        key = compact(name)
        if not key:
            return ""
        by_compact = {
            compact(str(op.get(OPERATION_KEY) or "")): str(
                op.get(OPERATION_KEY) or ""
            ).strip()
            for op in operations
            if str(op.get(OPERATION_KEY) or "").strip()
        }
        if key in by_compact:
            return by_compact[key]
        alias = OPERATION_ALIASES.get(key)
        if alias and compact(alias) in by_compact:
            return by_compact[compact(alias)]
        return ""


class DiagnosticCapabilityMatcher:
    """Return capabilities whose applicable measures (or name) match the question."""

    def __init__(
        self,
        *,
        mention: Optional[MeasureMentionDetector] = None,
        measures: Optional[ApplicableMeasuresNormalizer] = None,
    ) -> None:
        self._mention = mention or MeasureMentionDetector()
        self._measures = measures or ApplicableMeasuresNormalizer()

    def match(
        self,
        question: str,
        capabilities: Optional[Sequence[Mapping[str, Any]]],
        *,
        metric_names: Optional[Sequence[str]] = None,
        synonyms_for: Optional[Any] = None,
    ) -> list[dict[str, Any]]:
        text = str(question or "")
        metrics = {
            str(name).strip().lower()
            for name in (metric_names or [])
            if str(name).strip()
        }
        matched: list[dict[str, Any]] = []
        for cap in capabilities or []:
            if not isinstance(cap, Mapping):
                continue
            cap_name = str(cap.get(CAP_KEY_CAPABILITY) or "").strip()
            applicable = self._measures.normalize(
                cap.get(CAP_KEY_APPLICABLE_TO) or cap.get(CAP_KEY_APPLICABLE_TO_SNAKE)
            )
            hit_measures: list[str] = []
            for app in applicable:
                extras: list[str] = []
                if synonyms_for is not None:
                    raw_syn = synonyms_for(app)
                    if isinstance(raw_syn, str):
                        extras = [
                            part.strip()
                            for part in re.split(SYNONYM_SPLIT_PATTERN, raw_syn)
                            if part.strip()
                        ]
                    elif isinstance(raw_syn, (list, tuple)):
                        extras = [
                            str(part).strip() for part in raw_syn if str(part).strip()
                        ]
                if app.lower() in metrics or self._mention.mentioned(app, text, extras):
                    hit_measures.append(app)
            name_hit = bool(cap_name) and (
                cap_name.lower() in text.lower() or self._mention.mentioned(cap_name, text)
            )
            if not hit_measures and not name_hit:
                continue
            copied = dict(cap)
            copied[CAP_KEY_MATCHED_MEASURES] = hit_measures
            matched.append(copied)
        return matched


class DiagnosticUseResolver:
    """Classify each diagnostic use as strategy, measure, dimension, or other."""

    def __init__(
        self,
        *,
        uses: Optional[DiagnosticUsesNormalizer] = None,
        ops: Optional[OperationNameCanonicalizer] = None,
    ) -> None:
        self._uses = uses or DiagnosticUsesNormalizer()
        self._ops = ops or OperationNameCanonicalizer()

    def resolve(
        self,
        capabilities: Sequence[Mapping[str, Any]],
        *,
        operations: Sequence[Mapping[str, Any]],
        measures: Sequence[str] = (),
        dimensions: Sequence[str] = (),
    ) -> list[dict[str, Any]]:
        measure_map = {
            compact(name): str(name).strip() for name in measures if str(name).strip()
        }
        dimension_map = {
            compact(name): str(name).strip()
            for name in dimensions
            if str(name).strip()
        }
        resolved: list[dict[str, Any]] = []
        seen: set[tuple[str, str]] = set()
        for cap in capabilities:
            if not isinstance(cap, Mapping):
                continue
            cap_name = str(cap.get(CAP_KEY_CAPABILITY) or "").strip()
            for ref in self._uses.normalize(cap.get(CAP_KEY_USES)):
                raw = ref[USE_PLAN_KEY_NAME]
                hint = ref[USE_REF_KEY_HINTED_KIND]
                op_name = ""
                if hint in STRATEGY_LOOKUP_HINT_KINDS:
                    op_name = self._ops.canonical(raw, operations)
                if hint == KIND_MEASURE or (
                    not hint and not op_name and compact(raw) in measure_map
                ):
                    kind = KIND_MEASURE
                    label = measure_map.get(compact(raw), raw)
                elif hint == KIND_DIMENSION or (
                    not hint and not op_name and compact(raw) in dimension_map
                ):
                    kind = KIND_DIMENSION
                    label = dimension_map.get(compact(raw), raw)
                elif op_name or hint in EXPLICIT_STRATEGY_HINT_KINDS:
                    kind = KIND_STRATEGY
                    label = op_name or raw
                elif compact(raw) in measure_map:
                    kind = KIND_MEASURE
                    label = measure_map[compact(raw)]
                elif compact(raw) in dimension_map:
                    kind = KIND_DIMENSION
                    label = dimension_map[compact(raw)]
                else:
                    kind = KIND_OTHER
                    label = raw
                key = (kind, compact(label))
                if key in seen:
                    continue
                seen.add(key)
                entry: dict[str, Any] = {
                    USE_PLAN_KEY_KIND: kind,
                    USE_PLAN_KEY_NAME: label,
                    USE_PLAN_KEY_SOURCE: raw,
                    USE_PLAN_KEY_CAPABILITY: cap_name,
                }
                bound_measures, bound_dimensions = self._bound_fields(
                    ref, measure_map, dimension_map
                )
                if kind == KIND_STRATEGY:
                    if bound_measures:
                        entry[USE_PLAN_KEY_MEASURES] = bound_measures
                    if bound_dimensions:
                        entry[USE_PLAN_KEY_DIMENSIONS] = bound_dimensions
                resolved.append(entry)
                if kind != KIND_STRATEGY:
                    continue
                for measure_name in bound_measures:
                    self._append_field(
                        resolved, seen, KIND_MEASURE, measure_name, cap_name
                    )
                for dimension_name in bound_dimensions:
                    self._append_field(
                        resolved, seen, KIND_DIMENSION, dimension_name, cap_name
                    )
        return resolved

    @staticmethod
    def _bound_fields(
        ref: Mapping[str, Any],
        measure_map: Mapping[str, str],
        dimension_map: Mapping[str, str],
    ) -> tuple[list[str], list[str]]:
        measures = [
            measure_map.get(compact(name), str(name).strip())
            for name in (ref.get(USE_PLAN_KEY_MEASURES) or [])
        ]
        dimensions = [
            dimension_map.get(compact(name), str(name).strip())
            for name in (ref.get(USE_PLAN_KEY_DIMENSIONS) or [])
        ]
        for key, names in (ref.get(USE_REF_KEY_INPUTS) or {}).items():
            if _input_kind(str(key)):
                continue
            for name in names:
                token = compact(name)
                if token in measure_map:
                    measures.append(measure_map[token])
                elif token in dimension_map:
                    dimensions.append(dimension_map[token])
        return _unique_labels(measures), _unique_labels(dimensions)

    @staticmethod
    def _append_field(
        resolved: list[dict[str, Any]],
        seen: set[tuple[str, str]],
        kind: str,
        name: str,
        capability: str,
    ) -> None:
        label = str(name or "").strip()
        key = (kind, compact(label))
        if not label or key in seen:
            return
        seen.add(key)
        resolved.append(
            {
                USE_PLAN_KEY_KIND: kind,
                USE_PLAN_KEY_NAME: label,
                USE_PLAN_KEY_SOURCE: label,
                USE_PLAN_KEY_CAPABILITY: capability,
            }
        )


class FormulaComponentExtractor:
    """Measure names that appear inside a calculated-measure formula."""

    def extract(
        self,
        formula: str,
        measures: Sequence[str],
        *,
        exclude: Sequence[str] = (),
    ) -> list[str]:
        text = str(formula or "").lower()
        if not text:
            return []
        skipped = {compact(name) for name in exclude}
        found: list[str] = []
        for name in sorted(
            (str(item).strip() for item in measures), key=len, reverse=True
        ):
            if (
                not name
                or len(name) < MIN_FORMULA_MEASURE_NAME_LENGTH
                or compact(name) in skipped
            ):
                continue
            if name.lower() in text:
                found.append(name)
                skipped.add(compact(name))
        return found


class DecomposeMeasureExpander:
    """When measure decomposition is required, add formula component measures."""

    def __init__(self, *, formulas: Optional[FormulaComponentExtractor] = None) -> None:
        self._formulas = formulas or FormulaComponentExtractor()

    def expand(
        self,
        use_plan: Sequence[Mapping[str, Any]],
        formulas_by_measure: Mapping[str, str],
        known_measures: Sequence[str],
    ) -> list[dict[str, str]]:
        plan = [dict(item) for item in use_plan]
        if not any(
            str(item.get(USE_PLAN_KEY_KIND) or "") == KIND_STRATEGY
            and str(item.get(USE_PLAN_KEY_NAME) or "").lower() == OPERATION_DECOMPOSE
            for item in plan
        ):
            return plan
        existing = {compact(str(item.get(USE_PLAN_KEY_NAME) or "")) for item in plan}
        for formula in formulas_by_measure.values():
            for component in self._formulas.extract(
                formula,
                known_measures,
                exclude=list(formulas_by_measure),
            ):
                key = compact(component)
                if key in existing:
                    continue
                existing.add(key)
                plan.append(
                    {
                        USE_PLAN_KEY_KIND: KIND_MEASURE,
                        USE_PLAN_KEY_NAME: component,
                        USE_PLAN_KEY_SOURCE: USE_SOURCE_FORMULA,
                        USE_PLAN_KEY_CAPABILITY: "",
                    }
                )
        return plan


class MeasureTraitGuidance:
    """How a question or query should use a measure's grain, additivity, and direction."""

    def build(self, grain: str = "", additivity: str = "", direction: str = "") -> str:
        notes: list[str] = []
        grain_label = str(grain or "").strip()
        if grain_label:
            notes.append(TRAIT_GRAIN_NOTE.format(grain=grain_label))
        additivity_label = " ".join(
            str(additivity or "")
            .strip()
            .lower()
            .replace("_", " ")
            .replace("-", " ")
            .split()
        )
        if additivity_label in ADDITIVITY_NON_ADDITIVE_LABELS:
            notes.append(TRAIT_NON_ADDITIVE_NOTE)
        elif additivity_label == ADDITIVITY_SEMI_ADDITIVE:
            notes.append(TRAIT_SEMI_ADDITIVE_NOTE)
        elif additivity_label == ADDITIVITY_ADDITIVE:
            notes.append(TRAIT_ADDITIVE_NOTE)
        direction_label = str(direction or "").strip().lower()
        if direction_label == DIRECTION_NEGATIVE:
            notes.append(TRAIT_NEGATIVE_NOTE)
        elif direction_label == DIRECTION_POSITIVE:
            notes.append(TRAIT_POSITIVE_NOTE)
        elif direction_label == DIRECTION_NEUTRAL:
            notes.append(TRAIT_NEUTRAL_NOTE)
        return TRAIT_NOTE_SEPARATOR.join(notes)


_MENTION = MeasureMentionDetector()
_APPLICABLE = ApplicableMeasuresNormalizer()
_USES = DiagnosticUsesNormalizer()
_OPS = OperationNameCanonicalizer()
_MATCHER = DiagnosticCapabilityMatcher()
_RESOLVER = DiagnosticUseResolver()
_FORMULAS = FormulaComponentExtractor()
_EXPANDER = DecomposeMeasureExpander()
_TRAITS = MeasureTraitGuidance()


def measure_mentioned(name: str, text: str, extra_phrases: Sequence[str] = ()) -> bool:
    """True when a measure name, synonym, or its distinctive tokens appear in text."""
    return _MENTION.mentioned(name, text, extra_phrases)


def iter_applicable_measures(applicable: Any) -> list[str]:
    """Normalize applicableTo entries that may be measure names or objects."""
    return _APPLICABLE.normalize(applicable)


def iter_diagnostic_uses(uses: Any) -> list[dict[str, Any]]:
    """Normalize diagnostic ``uses`` entries that may be names or typed objects."""
    public: list[dict[str, Any]] = []
    for ref in _USES.normalize(uses):
        public.append(
            {key: value for key, value in ref.items() if key != USE_REF_KEY_INPUTS}
        )
    return public


def canonical_operation_name(name: str, operations: Sequence[Mapping[str, Any]]) -> str:
    """Map a diagnostic use onto an analytical operation, including aliases."""
    return _OPS.canonical(name, operations)


def match_diagnostic_capabilities(
    question: str,
    capabilities: Optional[Sequence[Mapping[str, Any]]],
    *,
    metric_names: Optional[Sequence[str]] = None,
    synonyms_for: Optional[Any] = None,
) -> list[dict[str, Any]]:
    """Return capabilities whose applicable measures (or name) match the question."""
    return _MATCHER.match(
        question,
        capabilities,
        metric_names=metric_names,
        synonyms_for=synonyms_for,
    )


def resolve_diagnostic_uses(
    capabilities: Sequence[Mapping[str, Any]],
    *,
    operations: Sequence[Mapping[str, Any]],
    measures: Sequence[str] = (),
    dimensions: Sequence[str] = (),
) -> list[dict[str, str]]:
    """Classify each diagnostic use as a strategy, measure, dimension, or other item."""
    return _RESOLVER.resolve(
        capabilities,
        operations=operations,
        measures=measures,
        dimensions=dimensions,
    )


def diagnostic_field_scope(
    use_plan: Sequence[Mapping[str, Any]],
    capabilities: Optional[Sequence[Mapping[str, Any]]] = None,
) -> dict[str, list[str]]:
    """Strategies, measures, and dimensions the user configured for think mode."""
    strategies: list[str] = []
    bound_measures: list[str] = []
    bound_dimensions: list[str] = []
    listed_measures: list[str] = []
    formula_measures: list[str] = []
    listed_dimensions: list[str] = []
    for item in use_plan or []:
        if not isinstance(item, Mapping):
            continue
        kind = str(item.get(USE_PLAN_KEY_KIND) or "")
        name = str(item.get(USE_PLAN_KEY_NAME) or "").strip()
        if kind == KIND_STRATEGY and name:
            strategies.append(name)
            bound_measures.extend(str(n).strip() for n in (item.get(USE_PLAN_KEY_MEASURES) or []))
            bound_dimensions.extend(
                str(n).strip() for n in (item.get(USE_PLAN_KEY_DIMENSIONS) or [])
            )
        elif kind == KIND_MEASURE and name:
            if str(item.get(USE_PLAN_KEY_SOURCE) or "") == USE_SOURCE_FORMULA:
                formula_measures.append(name)
            else:
                listed_measures.append(name)
        elif kind == KIND_DIMENSION and name:
            listed_dimensions.append(name)
    applicable: list[str] = []
    if not bound_measures:
        for cap in capabilities or []:
            if not isinstance(cap, Mapping):
                continue
            matched = [
                str(name).strip()
                for name in (cap.get(CAP_KEY_MATCHED_MEASURES) or [])
                if str(name).strip()
            ]
            if matched:
                applicable.extend(matched)
                continue
            applicable.extend(
                iter_applicable_measures(
                    cap.get(CAP_KEY_APPLICABLE_TO) or cap.get(CAP_KEY_APPLICABLE_TO_SNAKE)
                )
            )
    measures = _unique_labels(
        (bound_measures or applicable) + listed_measures + formula_measures
    )
    return {
        "strategies": _unique_labels(strategies),
        "measures": measures,
        "dimensions": _unique_labels(bound_dimensions + listed_dimensions),
    }


def diagnostic_activity_reason(
    capabilities: Optional[Sequence[Mapping[str, Any]]],
    scope: Mapping[str, Sequence[str]],
) -> str:
    """Activity text naming the picked capability, measure, strategy, and dimension."""
    capability_names = _unique_labels(
        str(cap.get(CAP_KEY_CAPABILITY) or "")
        for cap in (capabilities or [])
        if isinstance(cap, Mapping)
    )
    measures = _unique_labels(scope.get("measures") or [])
    strategies = _unique_labels(scope.get("strategies") or [])
    dimensions = _unique_labels(scope.get("dimensions") or [])
    lines: list[str] = []
    if capability_names:
        lines.append(DIAGNOSTIC_ACTIVITY_CAPABILITY.format(names=", ".join(capability_names)))
    if measures:
        lines.append(DIAGNOSTIC_ACTIVITY_MEASURE.format(names=", ".join(measures)))
    if strategies:
        lines.append(DIAGNOSTIC_ACTIVITY_STRATEGY.format(names=", ".join(strategies)))
    if dimensions:
        lines.append(DIAGNOSTIC_ACTIVITY_DIMENSION.format(names=", ".join(dimensions)))
    if lines:
        lines.append(DIAGNOSTIC_ACTIVITY_RULE)
    return " ".join(lines)


def diagnostic_scope_lock(scope: Mapping[str, Sequence[str]]) -> str:
    """Prompt rule that forbids strategies and fields outside the configured scope."""
    strategies = _unique_labels(scope.get("strategies") or [])
    if not strategies:
        return ""
    lines = [THINK_SCOPE_LOCK_STRATEGIES.format(strategies=", ".join(strategies))]
    measures = _unique_labels(scope.get("measures") or [])
    dimensions = _unique_labels(scope.get("dimensions") or [])
    if measures:
        lines.append(THINK_SCOPE_LOCK_MEASURES.format(measures=", ".join(measures)))
    if dimensions:
        lines.append(THINK_SCOPE_LOCK_DIMENSIONS.format(dimensions=", ".join(dimensions)))
    lines.append(THINK_SCOPE_LOCK_RULE)
    return "\n".join(lines)


def charts_within_diagnostic_scope(
    charts: Sequence[Mapping[str, Any]],
    scope: Mapping[str, Sequence[str]],
) -> list[dict[str, Any]]:
    """Keep charts whose strategy is configured, and drop fields outside that scope."""
    strategies = _unique_labels(scope.get("strategies") or [])
    if not strategies:
        return [dict(chart) for chart in charts if isinstance(chart, Mapping)]
    allowed_ops = {compact(name) for name in strategies}
    measures = _unique_labels(scope.get("measures") or [])
    dimensions = _unique_labels(scope.get("dimensions") or [])
    measure_keys = {name.lower() for name in measures}
    field_keys = measure_keys | {name.lower() for name in dimensions}
    kept: list[dict[str, Any]] = []
    for chart in charts:
        if not isinstance(chart, Mapping):
            continue
        operation = str(chart.get("operation") or chart.get("analytical_strategy") or "").strip()
        if compact(operation) not in allowed_ops:
            continue
        copied = dict(chart)
        copied["operation"] = operation
        copied["analytical_strategy"] = operation
        question = str(chart.get("question") or "")
        if measure_keys:
            raw_hints = [
                str(name).strip()
                for name in (chart.get("measure_hints") or [])
                if str(name).strip()
            ]
            if any(name.lower() not in measure_keys for name in raw_hints):
                continue
            copied["measure_hints"] = raw_hints
            if not raw_hints and not any(name.lower() in question.lower() for name in measures):
                continue
        if dimensions:
            raw_components = [
                str(name).strip()
                for name in (chart.get("components") or [])
                if str(name).strip()
            ]
            if any(name.lower() not in field_keys for name in raw_components):
                continue
            copied["components"] = raw_components
            uses_dimension = any(name.lower() in question.lower() for name in dimensions) or any(
                name.lower() in {component.lower() for component in raw_components}
                for name in dimensions
            )
            if not uses_dimension:
                continue
        kept.append(copied)
    return kept


def operations_for_diagnostic_uses(
    use_plan: Sequence[Mapping[str, Any]],
    operations: Sequence[Mapping[str, Any]],
) -> list[dict[str, Any]]:
    """Analytical operations required by resolved diagnostic strategy uses, in order."""
    by_name = {
        str(op.get(OPERATION_KEY) or "").strip().lower(): op
        for op in operations
        if str(op.get(OPERATION_KEY) or "").strip()
    }
    chosen: list[dict[str, Any]] = []
    seen: set[str] = set()
    for item in use_plan:
        if str(item.get(USE_PLAN_KEY_KIND) or "") != KIND_STRATEGY:
            continue
        key = str(item.get(USE_PLAN_KEY_NAME) or "").strip().lower()
        op = by_name.get(key)
        if op is None or key in seen:
            continue
        seen.add(key)
        chosen.append(op)
    return chosen


def formula_component_measures(
    formula: str,
    measures: Sequence[str],
    *,
    exclude: Sequence[str] = (),
) -> list[str]:
    """Measure names that appear inside a calculated-measure formula."""
    return _FORMULAS.extract(formula, measures, exclude=exclude)


def expand_decompose_measures(
    use_plan: Sequence[Mapping[str, Any]],
    formulas_by_measure: Mapping[str, str],
    known_measures: Sequence[str],
) -> list[dict[str, str]]:
    """When measure decomposition is required, add formula component measures."""
    return _EXPANDER.expand(use_plan, formulas_by_measure, known_measures)


def measure_trait_guidance(
    grain: str = "",
    additivity: str = "",
    direction: str = "",
) -> str:
    """How a question or query should use a measure's grain, additivity, and direction."""
    return _TRAITS.build(grain, additivity, direction)
