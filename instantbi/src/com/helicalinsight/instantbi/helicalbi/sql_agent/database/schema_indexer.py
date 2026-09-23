from __future__ import annotations

import hashlib
import logging
import math
import re
from typing import Any, Callable, Dict, List, Optional, Protocol, Sequence

from helicalbi.sql_agent.config import DEFAULT_EMBEDDING_DIM, DEFAULT_SCHEMA_TOP_K
from helicalbi.sql_agent.database.catalog import (
    SchemaCatalog,
    TableMeta,
    tables_from_cube_metadata,
)

logger = logging.getLogger(__name__)

_TOKEN_RE = re.compile(r"[a-z0-9_]+")
_INDEXERS: Dict[str, "SchemaIndexer"] = {}
DEFAULT_CATALOG_ID = "default"


def tokenize(text: str) -> List[str]:
    tokens = _TOKEN_RE.findall((text or "").lower())
    bigrams = [f"{tokens[i]}_{tokens[i + 1]}" for i in range(len(tokens) - 1)]
    return tokens + bigrams


def hashing_embed(text: str, dim: int = DEFAULT_EMBEDDING_DIM) -> List[float]:
    """Deterministic hashing-trick embedding. No model download required."""
    vec = [0.0] * dim
    for token in tokenize(text):
        digest = hashlib.md5(token.encode("utf-8")).digest()
        value = int.from_bytes(digest[:8], "little")
        idx = value % dim
        sign = 1.0 if ((value // dim) % 2 == 0) else -1.0
        vec[idx] += sign
    norm = math.sqrt(sum(x * x for x in vec))
    if norm:
        vec = [x / norm for x in vec]
    return vec


def cosine_similarity(left: Sequence[float], right: Sequence[float]) -> float:
    if not left or not right or len(left) != len(right):
        return 0.0
    return float(sum(a * b for a, b in zip(left, right)))


class VectorIndex(Protocol):
    def add(
        self,
        ids: List[str],
        documents: List[str],
        metadatas: List[Dict[str, Any]],
        embeddings: List[List[float]],
    ) -> None: ...

    def query(self, embedding: List[float], top_k: int) -> List[Dict[str, Any]]: ...


class InMemoryVectorIndex:
    """Cosine-similarity store used when ChromaDB is unavailable."""

    def __init__(self) -> None:
        self._rows: List[Dict[str, Any]] = []

    def add(
        self,
        ids: List[str],
        documents: List[str],
        metadatas: List[Dict[str, Any]],
        embeddings: List[List[float]],
    ) -> None:
        existing = {row["id"] for row in self._rows}
        for doc_id, document, metadata, embedding in zip(ids, documents, metadatas, embeddings):
            row = {
                "id": doc_id,
                "document": document,
                "metadata": metadata,
                "embedding": embedding,
            }
            if doc_id in existing:
                self._rows = [row if item["id"] == doc_id else item for item in self._rows]
            else:
                self._rows.append(row)
                existing.add(doc_id)

    def query(self, embedding: List[float], top_k: int) -> List[Dict[str, Any]]:
        scored = []
        for row in self._rows:
            scored.append(
                {
                    "id": row["id"],
                    "document": row["document"],
                    "metadata": row["metadata"],
                    "score": cosine_similarity(embedding, row["embedding"]),
                }
            )
        scored.sort(key=lambda item: item["score"], reverse=True)
        return scored[: max(0, top_k)]


class ChromaVectorIndex:
    """Persistent Chroma collection. Embeddings are supplied by the indexer."""

    def __init__(self, persist_directory: str, collection_name: str = "schema_tables"):
        import chromadb

        self._client = chromadb.PersistentClient(path=persist_directory)
        self._collection = self._client.get_or_create_collection(
            name=collection_name,
            metadata={"hnsw:space": "cosine"},
        )

    def add(
        self,
        ids: List[str],
        documents: List[str],
        metadatas: List[Dict[str, Any]],
        embeddings: List[List[float]],
    ) -> None:
        self._collection.upsert(
            ids=ids,
            documents=documents,
            metadatas=metadatas,
            embeddings=embeddings,
        )

    def query(self, embedding: List[float], top_k: int) -> List[Dict[str, Any]]:
        if top_k <= 0:
            return []
        result = self._collection.query(
            query_embeddings=[embedding],
            n_results=top_k,
            include=["documents", "metadatas", "distances"],
        )
        ids = (result.get("ids") or [[]])[0]
        documents = (result.get("documents") or [[]])[0]
        metadatas = (result.get("metadatas") or [[]])[0]
        distances = (result.get("distances") or [[]])[0]
        rows = []
        for doc_id, document, metadata, distance in zip(ids, documents, metadatas, distances):
            score = 1.0 - float(distance) if distance is not None else 0.0
            rows.append(
                {
                    "id": doc_id,
                    "document": document,
                    "metadata": metadata or {},
                    "score": score,
                }
            )
        return rows


def table_document(table: TableMeta) -> str:
    """Chunk metadata at table granularity for embedding."""
    col_bits = []
    for col in table.columns:
        bit = col.name
        if col.description:
            bit += f" {col.description}"
        if col.sample_values:
            bit += " " + " ".join(str(v) for v in col.sample_values[:8])
        col_bits.append(bit)
    fk_bits = [
        f"{fk.column} {fk.ref_table} {fk.ref_column}" for fk in table.foreign_keys
    ]
    pk_bits = " ".join(table.primary_keys)
    # Repeat the table name so lexical hits dominate near-collisions.
    return " ".join(
        [
            table.name,
            table.name,
            table.schema_name,
            table.description,
            pk_bits,
            " ".join(col_bits),
            " ".join(fk_bits),
        ]
    ).strip()


class SchemaIndexer:
    """Embeds each table's metadata and retrieves a pruned schema subset."""

    def __init__(
        self,
        *,
        persist_directory: Optional[str] = None,
        embed: Callable[[str], List[float]] = hashing_embed,
        vector_index: Optional[VectorIndex] = None,
    ) -> None:
        self.catalog = SchemaCatalog()
        self._embed = embed
        if vector_index is not None:
            self._index = vector_index
        elif persist_directory:
            try:
                self._index = ChromaVectorIndex(persist_directory)
            except ImportError:
                logger.warning(
                    "chromadb is not installed; using in-memory schema index"
                )
                self._index = InMemoryVectorIndex()
        else:
            self._index = InMemoryVectorIndex()

    def index_tables(self, tables: Sequence[TableMeta]) -> None:
        ids: List[str] = []
        documents: List[str] = []
        metadatas: List[Dict[str, Any]] = []
        embeddings: List[List[float]] = []
        for table in tables:
            if not table.name:
                continue
            self.catalog.add_table(table)
            document = table_document(table)
            ids.append(table.name)
            documents.append(document)
            metadatas.append({"table": table.name, "schema": table.schema_name})
            embeddings.append(self._embed(document))
        if ids:
            self._index.add(ids, documents, metadatas, embeddings)

    def index_from_cube_metadata(
        self, cube_metadata: Any, relationships: Any = None
    ) -> None:
        self.index_tables(tables_from_cube_metadata(cube_metadata, relationships))

    def retrieve(self, query: str, top_k: int = DEFAULT_SCHEMA_TOP_K) -> List[str]:
        """Return top-N table names plus one-hop foreign-key neighbors.

        Dense cosine hits are merged with token-overlap scores so distinctive
        table/column names still win in 100+ table catalogs.
        """
        if not query or top_k <= 0:
            return []
        query_tokens = set(tokenize(query))
        lexical_scores: Dict[str, float] = {}
        for table in self.catalog.tables():
            doc_tokens = set(tokenize(table_document(table)))
            overlap = len(query_tokens & doc_tokens)
            if overlap:
                name_bonus = 2.0 if table.name.lower() in query.lower() else 0.0
                lexical_scores[table.name] = overlap + name_bonus

        dense_hits = self._index.query(self._embed(query), max(top_k, 8))
        combined: Dict[str, float] = dict(lexical_scores)
        for hit in dense_hits:
            table_name = (hit.get("metadata") or {}).get("table") or hit.get("id")
            if not table_name:
                continue
            combined[str(table_name)] = combined.get(str(table_name), 0.0) + float(
                hit.get("score") or 0.0
            )

        ranked = sorted(combined.items(), key=lambda item: item[1], reverse=True)
        names: List[str] = []
        seen = set()
        for table_name, _score in ranked:
            key = table_name.lower()
            if key in seen:
                continue
            seen.add(key)
            names.append(table_name)
            if len(names) >= top_k:
                break

        expanded = list(names)
        for name in names:
            for related in self.catalog.related_tables(name):
                key = related.lower()
                if key not in seen:
                    seen.add(key)
                    expanded.append(related)
        return expanded

    def retrieve_schema(self, query: str, top_k: int = DEFAULT_SCHEMA_TOP_K) -> str:
        """Return a compact DDL-like schema for the tables relevant to *query*."""
        table_names = self.retrieve(query, top_k=top_k)
        if not table_names:
            return ""
        return self.catalog.to_prompt(table_names)

    def relation_exploration_pack(
        self,
        query: str,
        *,
        seed_names: Optional[Sequence[str]] = None,
        top_k: int = 5,
        max_tables: int = 6,
        max_chars: int = 3500,
    ) -> tuple[str, List[str], List[str]]:
        """Same-table + join-neighbor columns for investigation planning.

        Returns ``(prompt_text, allowed_labels, table_names)`` where labels include
        physical column names and humanized variants so planners can cite either form.
        """
        from helicalbi.sql_agent.modes import truncate_text

        seed_names = [str(n).strip() for n in (seed_names or []) if str(n).strip()]
        seed_query = " ".join([query or "", *seed_names]).strip() or (query or "")
        ranked = self.retrieve(seed_query, top_k=top_k) if seed_query else []
        seed_tables = self._tables_matching_seeds(seed_names)
        ordered: List[str] = []
        seen: set[str] = set()
        for name in seed_tables + ranked:
            key = name.lower()
            if key in seen:
                continue
            seen.add(key)
            ordered.append(name)
            if len(ordered) >= max_tables:
                break
        if not ordered:
            return "", [], []

        # Expand one-hop join neighbors after seed ranking.
        for name in list(ordered):
            for related_name in self.catalog.related_tables(name):
                key = related_name.lower()
                if key in seen:
                    continue
                seen.add(key)
                ordered.append(related_name)
                if len(ordered) >= max_tables:
                    break
            if len(ordered) >= max_tables:
                break
        ordered = ordered[:max_tables]

        lines = [
            "Schema relation exploration (same-table columns and join neighbors).",
            "Use these as complementary dimensions/measures for chart breakdowns:",
        ]
        allowed: List[str] = []

        def _add_label(label: str) -> None:
            text = str(label or "").strip()
            if not text:
                return
            if text not in allowed:
                allowed.append(text)
            human = text.replace("_", " ").strip()
            if human and human not in allowed and human.lower() != text.lower():
                allowed.append(human)

        for name in ordered:
            table = self.catalog.get(name)
            if table is None:
                continue
            lines.append(f"TABLE {table.name}")
            same_cols = [col.name for col in table.columns if col.name]
            if same_cols:
                lines.append(f"  same_table_columns: {', '.join(same_cols)}")
                for col in same_cols:
                    _add_label(col)
            if table.foreign_keys:
                lines.append("  joins:")
                for fk in table.foreign_keys:
                    lines.append(
                        f"    {table.name}.{fk.column} -> {fk.ref_table}.{fk.ref_column}"
                    )
            related = [
                related_name
                for related_name in self.catalog.related_tables(table.name)
                if related_name.lower() in seen
            ]
            for related_name in related:
                related_table = self.catalog.get(related_name)
                if related_table is None:
                    continue
                related_cols = [col.name for col in related_table.columns if col.name]
                preview = ", ".join(related_cols[:12])
                if len(related_cols) > 12:
                    preview += ", ..."
                lines.append(
                    f"  related_table {related_table.name}: {preview or '(no columns)'}"
                )
                for col in related_cols:
                    _add_label(col)

        pack = truncate_text("\n".join(lines), max(500, int(max_chars or 3500)))
        return pack, allowed, list(ordered)

    def _tables_matching_seeds(self, seed_names: Sequence[str]) -> List[str]:
        if not seed_names:
            return []
        seed_tokens = [set(tokenize(name)) for name in seed_names]
        scored: List[tuple[float, str]] = []
        for table in self.catalog.tables():
            best = 0.0
            table_tokens = set(tokenize(table.name))
            for tokens in seed_tokens:
                if tokens and tokens <= table_tokens:
                    best = max(best, 0.8)
            for col in table.columns:
                col_tokens = set(tokenize(col.name))
                col_blob = set(tokenize(f"{col.name} {col.description}"))
                for tokens in seed_tokens:
                    if not tokens:
                        continue
                    overlap = len(tokens & col_blob) / max(1, len(tokens))
                    if tokens <= col_tokens or tokens <= col_blob:
                        overlap = max(overlap, 1.0)
                    best = max(best, overlap)
            if best >= 0.5:
                scored.append((best, table.name))
        scored.sort(key=lambda item: item[0], reverse=True)
        return [name for _score, name in scored]


def get_indexer(catalog_id: str = DEFAULT_CATALOG_ID) -> SchemaIndexer:
    indexer = _INDEXERS.get(catalog_id)
    if indexer is None:
        indexer = SchemaIndexer()
        _INDEXERS[catalog_id] = indexer
    return indexer


def set_indexer(indexer: SchemaIndexer, catalog_id: str = DEFAULT_CATALOG_ID) -> SchemaIndexer:
    _INDEXERS[catalog_id] = indexer
    return indexer


def clear_indexers() -> None:
    _INDEXERS.clear()
