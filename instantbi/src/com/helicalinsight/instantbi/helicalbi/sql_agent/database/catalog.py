from __future__ import annotations

from dataclasses import dataclass, field
from typing import Any, Dict, Iterable, List, Optional


def _norm(name: Optional[str]) -> str:
    return str(name or "").strip().strip('"').strip("`").strip("[]").lower()


@dataclass
class ColumnMeta:
    name: str
    data_type: str = ""
    description: str = ""
    is_primary_key: bool = False
    sample_values: List[str] = field(default_factory=list)


@dataclass
class ForeignKeyMeta:
    column: str
    ref_table: str
    ref_column: str


@dataclass
class TableMeta:
    name: str
    description: str = ""
    schema_name: str = ""
    columns: List[ColumnMeta] = field(default_factory=list)
    primary_keys: List[str] = field(default_factory=list)
    foreign_keys: List[ForeignKeyMeta] = field(default_factory=list)

    def column_map(self) -> Dict[str, ColumnMeta]:
        return {_norm(col.name): col for col in self.columns if col.name}


class SchemaCatalog:
    """In-memory table/column catalog used by AST validation and retrieval."""

    def __init__(self, tables: Optional[Iterable[TableMeta]] = None):
        self._tables: Dict[str, TableMeta] = {}
        for table in tables or []:
            self.add_table(table)

    def add_table(self, table: TableMeta) -> None:
        if not table.name:
            return
        self._tables[_norm(table.name)] = table

    def tables(self) -> List[TableMeta]:
        return list(self._tables.values())

    def get(self, name: str) -> Optional[TableMeta]:
        return self._tables.get(_norm(name))

    def has_table(self, name: str) -> bool:
        return _norm(name) in self._tables

    def has_column(self, table_name: str, column_name: str) -> bool:
        table = self.get(table_name)
        if table is None:
            return False
        return _norm(column_name) in table.column_map()

    def related_tables(self, table_name: str) -> List[str]:
        """One-hop neighbors via outbound and inbound foreign keys."""
        seed = self.get(table_name)
        if seed is None:
            return []
        related = set()
        for fk in seed.foreign_keys:
            if self.has_table(fk.ref_table):
                related.add(self.get(fk.ref_table).name)
        seed_key = _norm(table_name)
        for other in self._tables.values():
            if _norm(other.name) == seed_key:
                continue
            for fk in other.foreign_keys:
                if _norm(fk.ref_table) == seed_key:
                    related.add(other.name)
        return sorted(related)

    def to_prompt(self, table_names: Optional[Iterable[str]] = None) -> str:
        selected = []
        if table_names is None:
            selected = self.tables()
        else:
            for name in table_names:
                table = self.get(name)
                if table is not None:
                    selected.append(table)
        return "\n\n".join(_format_table(table) for table in selected)


def _format_table(table: TableMeta) -> str:
    pk = {_norm(name) for name in table.primary_keys}
    lines = [f"TABLE {table.name}"]
    if table.schema_name:
        lines.append(f"  schema: {table.schema_name}")
    if table.description:
        lines.append(f"  description: {table.description}")
    col_parts = []
    for col in table.columns:
        flags = []
        if col.is_primary_key or _norm(col.name) in pk:
            flags.append("PK")
        type_bit = col.data_type or "unknown"
        extra = f" [{' '.join(flags)}]" if flags else ""
        desc = f" -- {col.description}" if col.description else ""
        samples = ""
        if col.sample_values:
            shown = ", ".join(str(v) for v in col.sample_values[:8])
            samples = f" samples=[{shown}]"
        col_parts.append(f"    {col.name} {type_bit}{extra}{samples}{desc}")
    if col_parts:
        lines.append("  columns:")
        lines.extend(col_parts)
    if table.foreign_keys:
        lines.append("  foreign_keys:")
        for fk in table.foreign_keys:
            lines.append(f"    {table.name}.{fk.column} -> {fk.ref_table}.{fk.ref_column}")
    return "\n".join(lines)


def tables_from_cube_metadata(
    cube_metadata: Any,
    relationships: Optional[Any] = None,
) -> List[TableMeta]:
    """Convert InstantBI cube_metadata (+ optional join map) into TableMeta rows."""
    cubes = cube_metadata or []
    if isinstance(cubes, dict):
        cubes = cubes.get("cube_metadata") or cubes.get("cubes") or [cubes]
    tables: List[TableMeta] = []
    for cube in cubes:
        if not isinstance(cube, dict):
            continue
        name = cube.get("database_table") or cube.get("table") or cube.get("name")
        if not name:
            continue
        columns: List[ColumnMeta] = []
        pk_names = []
        uid = cube.get("unique_identifier_of_table")
        if uid:
            pk_names.append(str(uid))
        for bucket in ("columns", "measures"):
            for item in cube.get(bucket) or []:
                if not isinstance(item, dict):
                    continue
                col_name = item.get("column_name") or item.get("name")
                if not col_name:
                    continue
                is_pk = bool(item.get("is_primary_key")) or _norm(col_name) in {
                    _norm(p) for p in pk_names
                }
                samples = item.get("sample_values") or item.get("enum_values") or []
                if not isinstance(samples, list):
                    samples = [samples]
                columns.append(
                    ColumnMeta(
                        name=str(col_name),
                        data_type=str(item.get("data_type") or item.get("type") or ""),
                        description=str(item.get("description") or ""),
                        is_primary_key=is_pk,
                        sample_values=[str(v) for v in samples if v is not None],
                    )
                )
        tables.append(
            TableMeta(
                name=str(name),
                description=str(cube.get("description") or ""),
                schema_name=str(cube.get("schema") or cube.get("schema_name") or ""),
                columns=columns,
                primary_keys=[str(p) for p in pk_names],
                foreign_keys=_foreign_keys_for(str(name), relationships),
            )
        )
    return tables


def _foreign_keys_for(table_name: str, relationships: Any) -> List[ForeignKeyMeta]:
    if not relationships:
        return []
    rows = relationships
    if isinstance(relationships, dict):
        rows = relationships.get(table_name) or relationships.get(_norm(table_name)) or []
        if isinstance(rows, dict):
            rows = [rows]
    fks: List[ForeignKeyMeta] = []
    for item in rows or []:
        if not isinstance(item, dict):
            continue
        src_table = item.get("table") or item.get("from_table") or table_name
        if _norm(src_table) != _norm(table_name):
            continue
        column = item.get("column") or item.get("from_column") or item.get("fk_column")
        ref_table = item.get("ref_table") or item.get("to_table") or item.get("referenced_table")
        ref_column = (
            item.get("ref_column")
            or item.get("to_column")
            or item.get("referenced_column")
        )
        if column and ref_table and ref_column:
            fks.append(
                ForeignKeyMeta(
                    column=str(column),
                    ref_table=str(ref_table),
                    ref_column=str(ref_column),
                )
            )
    return fks
