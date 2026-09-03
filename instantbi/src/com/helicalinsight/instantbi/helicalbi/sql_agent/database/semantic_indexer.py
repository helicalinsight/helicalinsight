"""RAG over the InstantBI semantic model (domains, topics, metrics, AI context)."""
from __future__ import annotations

from typing import Any, Callable, Dict, List, Optional

from helicalbi.common.CubeInfoModel import apply_ai_context, _topic_name
from helicalbi.sql_agent.config import DEFAULT_SCHEMA_TOP_K
from helicalbi.sql_agent.database.schema_indexer import (
    InMemoryVectorIndex,
    VectorIndex,
    hashing_embed,
    tokenize,
)

_INDEXERS: Dict[str, "SemanticLayerIndexer"] = {}
DEFAULT_CATALOG_ID = "default"
_MIN_SUFFICIENT_SCORE = 0.12


def _join_bits(parts: List[str]) -> str:
    return " ".join(part for part in parts if part and str(part).strip())


def _synonyms_text(value: Any) -> str:
    if value is None:
        return ""
    if isinstance(value, list):
        return " ".join(str(item).strip() for item in value if item)
    return str(value).strip()


def _component_labels(components: Any) -> List[str]:
    labels: List[str] = []
    for item in components or []:
        if isinstance(item, dict):
            name = str(item.get("name") or item.get("id") or "").strip()
            kind = str(item.get("kind") or "").strip()
            if name and kind:
                labels.append(f"{name} ({kind})")
            elif name:
                labels.append(name)
            continue
        if item:
            labels.append(str(item))
    return labels


class SemanticLayerIndexer:
    """Embeds semantic-model units and retrieves enriched domain/topic context."""

    def __init__(
        self,
        *,
        embed: Callable[[str], List[float]] = hashing_embed,
        vector_index: Optional[VectorIndex] = None,
    ) -> None:
        self._embed = embed
        self._index = vector_index or InMemoryVectorIndex()
        self._chunks: Dict[str, Dict[str, Any]] = {}

    def index_model(
        self,
        model_data: Optional[dict[str, Any]] = None,
        prepared: Optional[dict[str, Any]] = None,
    ) -> None:
        model_data = model_data or {}
        prepared = prepared or {}
        chunks = _build_chunks(model_data, prepared)
        if not chunks:
            return
        ids = [chunk["id"] for chunk in chunks]
        documents = [chunk["document"] for chunk in chunks]
        metadatas = [chunk["metadata"] for chunk in chunks]
        embeddings = [self._embed(document) for document in documents]
        self._index.add(ids, documents, metadatas, embeddings)
        self._chunks = {chunk["id"]: chunk for chunk in chunks}

    def overview(self) -> str:
        """Compact domain/topic map for the planner bootstrap prompt."""
        domains: Dict[str, Dict[str, Any]] = {}
        for chunk in self._chunks.values():
            meta = chunk.get("metadata") or {}
            if meta.get("kind") != "topic" and meta.get("kind") != "domain":
                continue
            domain = str(meta.get("domain") or "Model").strip() or "Model"
            bucket = domains.setdefault(domain, {"description": "", "topics": []})
            if meta.get("kind") == "domain" and meta.get("description"):
                bucket["description"] = str(meta["description"])
            if meta.get("kind") == "topic":
                topic = str(meta.get("topic") or "").strip()
                if topic and topic not in bucket["topics"]:
                    bucket["topics"].append(topic)
        if not domains:
            if self._chunks:
                kinds = sorted({(c.get("metadata") or {}).get("kind") or "chunk" for c in self._chunks.values()})
                return "Semantic model indexed units: " + ", ".join(kinds)
            return ""
        lines = ["Semantic model domains and topics:"]
        for domain, payload in domains.items():
            desc = f" — {payload['description']}" if payload["description"] else ""
            topics = ", ".join(payload["topics"]) if payload["topics"] else "(no topics listed)"
            lines.append(f"- {domain}{desc}")
            lines.append(f"  topics: {topics}")
        return "\n".join(lines)

    def retrieve(self, query: str, top_k: int = DEFAULT_SCHEMA_TOP_K) -> dict[str, Any]:
        """Return enriched semantic hits for *query*, plus a sufficiency flag."""
        empty = {
            "sufficient": False,
            "prompt": "",
            "domains": [],
            "topics": [],
            "kinds": [],
            "score": 0.0,
        }
        if not query or top_k <= 0 or not self._chunks:
            return empty

        query_tokens = set(tokenize(query))
        lexical_scores: Dict[str, float] = {}
        for chunk_id, chunk in self._chunks.items():
            doc_tokens = set(tokenize(chunk.get("document") or ""))
            overlap = len(query_tokens & doc_tokens)
            if not overlap:
                continue
            name = str((chunk.get("metadata") or {}).get("name") or "")
            name_bonus = 2.0 if name and name.lower() in query.lower() else 0.0
            lexical_scores[chunk_id] = overlap + name_bonus

        dense_hits = self._index.query(self._embed(query), max(top_k, 8))
        combined: Dict[str, float] = dict(lexical_scores)
        for hit in dense_hits:
            chunk_id = str(hit.get("id") or "")
            if not chunk_id:
                continue
            combined[chunk_id] = combined.get(chunk_id, 0.0) + float(hit.get("score") or 0.0)

        ranked = sorted(combined.items(), key=lambda item: item[1], reverse=True)
        selected: List[Dict[str, Any]] = []
        seen = set()
        for chunk_id, score in ranked:
            if chunk_id in seen or chunk_id not in self._chunks:
                continue
            seen.add(chunk_id)
            chunk = dict(self._chunks[chunk_id])
            chunk["score"] = score
            selected.append(chunk)
            if len(selected) >= top_k:
                break

        domains: List[str] = []
        topics: List[str] = []
        kinds: List[str] = []
        blocks: List[str] = []
        best = 0.0
        for chunk in selected:
            meta = chunk.get("metadata") or {}
            kind = str(meta.get("kind") or "")
            if kind:
                kinds.append(kind)
            domain = str(meta.get("domain") or "").strip()
            topic = str(meta.get("topic") or "").strip()
            if domain and domain not in domains:
                domains.append(domain)
            if topic and topic not in topics:
                topics.append(topic)
            best = max(best, float(chunk.get("score") or 0.0))
            block = str(chunk.get("prompt") or "").strip()
            if block:
                blocks.append(block)

        named_hit = any(
            str((chunk.get("metadata") or {}).get("name") or "").lower() in query.lower()
            for chunk in selected
            if (chunk.get("metadata") or {}).get("name")
        )
        sufficient = bool(selected) and (
            named_hit or best >= _MIN_SUFFICIENT_SCORE or bool(topics) or "metric" in kinds
        )
        return {
            "sufficient": sufficient,
            "prompt": "\n\n".join(blocks),
            "domains": domains,
            "topics": topics,
            "kinds": list(dict.fromkeys(kinds)),
            "score": best,
        }


def _build_chunks(model_data: dict[str, Any], prepared: dict[str, Any]) -> List[Dict[str, Any]]:
    chunks: List[Dict[str, Any]] = []
    for entry in model_data.get("domain") or []:
        if not isinstance(entry, dict):
            continue
        domain_name = str(entry.get("domain_name") or "").strip()
        domain_desc = str(entry.get("description") or "").strip()
        if domain_name or domain_desc:
            chunks.append(
                _chunk(
                    f"domain:{domain_name or domain_desc}",
                    kind="domain",
                    name=domain_name or domain_desc,
                    domain=domain_name,
                    document=_join_bits([domain_name, domain_desc, "domain"]),
                    prompt=_format_domain(domain_name, domain_desc),
                    description=domain_desc,
                )
            )
        for topic in entry.get("topics") or []:
            chunks.extend(_topic_chunks(domain_name, topic))

    mappings = prepared.get("topic_mappings") or model_data.get("topic_mappings") or []
    for mapping in mappings:
        if not isinstance(mapping, dict):
            continue
        topic_name = str(mapping.get("topic_name") or "").strip()
        if not topic_name:
            continue
        labels = _component_labels(mapping.get("components") or mapping.get("component"))
        description = str(mapping.get("description") or "").strip()
        domain_name = str(mapping.get("domain_name") or "").strip()
        chunks.append(
            _chunk(
                f"mapping:{domain_name}:{topic_name}",
                kind="topic",
                name=topic_name,
                domain=domain_name,
                topic=topic_name,
                document=_join_bits([topic_name, domain_name, description, " ".join(labels), "topic mapping"]),
                prompt=_format_topic(domain_name, topic_name, description, labels, ""),
                description=description,
            )
        )

    metrics = prepared.get("business_metrics") or model_data.get("business_metrics") or []
    for index, metric in enumerate(metrics):
        if not isinstance(metric, dict):
            continue
        name = str(
            metric.get("metric")
            or metric.get("name")
            or metric.get("measureName")
            or f"metric-{index}"
        ).strip()
        description = str(metric.get("description") or "").strip()
        formula = str(metric.get("formula") or "").strip()
        tables = " ".join(str(table) for table in (metric.get("tables") or []) if table)
        chunks.append(
            _chunk(
                f"metric:{name}",
                kind="metric",
                name=name,
                document=_join_bits([name, description, formula, tables, "business metric"]),
                prompt=_format_metric(name, description, formula, tables),
            )
        )

    cube_info = model_data.get("cube_info") or model_data.get("cube") or []
    for cube in cube_info:
        if not isinstance(cube, dict):
            continue
        cube_name = str(cube.get("cubeName") or cube.get("name") or "").strip()
        for item in list(cube.get("dimensions") or []) + list(cube.get("measures") or []):
            if not isinstance(item, dict):
                continue
            normalized = apply_ai_context(dict(item))
            name = str(
                normalized.get("dimensionName")
                or normalized.get("measureName")
                or normalized.get("name")
                or ""
            ).strip()
            if not name:
                continue
            description = str(normalized.get("description") or "").strip()
            instructions = str(normalized.get("ai_instructions") or "").strip()
            examples = str(normalized.get("ai_examples") or "").strip()
            synonyms = _synonyms_text(normalized.get("synonyms"))
            kind = "measure" if normalized.get("measureName") else "dimension"
            chunks.append(
                _chunk(
                    f"{kind}:{cube_name}:{name}",
                    kind=kind,
                    name=name,
                    document=_join_bits(
                        [name, cube_name, description, instructions, examples, synonyms, kind]
                    ),
                    prompt=_format_field(
                        kind, name, cube_name, description, instructions, examples, synonyms
                    ),
                )
            )

    domain_context = str(prepared.get("domain_context") or "").strip()
    if domain_context:
        chunks.append(
            _chunk(
                "domain_context",
                kind="context",
                name="domain_context",
                document=domain_context,
                prompt=f"SEMANTIC CONTEXT\n{domain_context}",
            )
        )

    ai_instructions = prepared.get("ai_instructions") or {}
    if isinstance(ai_instructions, dict):
        for field_name, instructions in ai_instructions.items():
            text = str(instructions or "").strip()
            if not text:
                continue
            chunks.append(
                _chunk(
                    f"ai:{field_name}",
                    kind="instruction",
                    name=str(field_name),
                    document=_join_bits([str(field_name), text, "ai instructions query explanation"]),
                    prompt=f"AI INSTRUCTIONS for {field_name}: {text}",
                )
            )
    return chunks


def _topic_chunks(domain_name: str, topic: Any) -> List[Dict[str, Any]]:
    name = _topic_name(topic)
    if not name:
        return []
    description = ""
    examples = ""
    instructions = ""
    labels: List[str] = []
    if isinstance(topic, dict):
        description = str(topic.get("description") or "").strip()
        examples = str(
            topic.get("examples")
            or topic.get("query_examples")
            or topic.get("query_explanation")
            or ""
        ).strip()
        instructions = str(topic.get("ai_instructions") or topic.get("instructions") or "").strip()
        labels = _component_labels(topic.get("components"))
    return [
        _chunk(
            f"topic:{domain_name}:{name}",
            kind="topic",
            name=name,
            domain=domain_name,
            topic=name,
            document=_join_bits(
                [name, domain_name, description, examples, instructions, " ".join(labels), "topic"]
            ),
            prompt=_format_topic(domain_name, name, description, labels, examples, instructions),
            description=description,
        )
    ]


def _chunk(
    chunk_id: str,
    *,
    kind: str,
    name: str,
    document: str,
    prompt: str,
    domain: str = "",
    topic: str = "",
    description: str = "",
) -> Dict[str, Any]:
    return {
        "id": chunk_id,
        "document": document,
        "prompt": prompt,
        "metadata": {
            "kind": kind,
            "name": name,
            "domain": domain,
            "topic": topic,
            "description": description,
        },
    }


def _format_domain(name: str, description: str) -> str:
    lines = [f"DOMAIN {name or '(unnamed)'}"]
    if description:
        lines.append(f"  definition: {description}")
    return "\n".join(lines)


def _format_topic(
    domain: str,
    name: str,
    description: str,
    components: List[str],
    examples: str,
    instructions: str = "",
) -> str:
    lines = [f"TOPIC {name}"]
    if domain:
        lines.append(f"  domain: {domain}")
    if description:
        lines.append(f"  definition: {description}")
    if components:
        lines.append(f"  components: {', '.join(components)}")
    if examples:
        lines.append(f"  query explanation / examples: {examples}")
    if instructions:
        lines.append(f"  AI instructions: {instructions}")
    return "\n".join(lines)


def _format_metric(name: str, description: str, formula: str, tables: str) -> str:
    lines = [f"METRIC {name}"]
    if description:
        lines.append(f"  definition: {description}")
    if formula:
        lines.append(f"  formula: {formula}")
    if tables:
        lines.append(f"  tables: {tables}")
    return "\n".join(lines)


def _format_field(
    kind: str,
    name: str,
    cube_name: str,
    description: str,
    instructions: str,
    examples: str,
    synonyms: str,
) -> str:
    lines = [f"{kind.upper()} {name}"]
    if cube_name:
        lines.append(f"  cube: {cube_name}")
    if description:
        lines.append(f"  definition: {description}")
    if synonyms:
        lines.append(f"  synonyms: {synonyms}")
    if examples:
        lines.append(f"  query explanation / examples: {examples}")
    if instructions:
        lines.append(f"  AI instructions: {instructions}")
    return "\n".join(lines)


def get_semantic_indexer(catalog_id: str = DEFAULT_CATALOG_ID) -> SemanticLayerIndexer:
    indexer = _INDEXERS.get(catalog_id)
    if indexer is None:
        indexer = SemanticLayerIndexer()
        _INDEXERS[catalog_id] = indexer
    return indexer


def set_semantic_indexer(
    indexer: SemanticLayerIndexer, catalog_id: str = DEFAULT_CATALOG_ID
) -> SemanticLayerIndexer:
    _INDEXERS[catalog_id] = indexer
    return indexer


def clear_semantic_indexers() -> None:
    _INDEXERS.clear()
