"""Minimal HelicalInsight report object for GridTable / pivot chart VF templates.

Mirrors the subset of ``getIntialReportState`` from
``client/src/redux/reducers/initialStates.js`` that AntV S2 / GridTable needs:
fields, marksList, properties (format, formatColor, crosstab, tooltip, title),
and reportData.properties (S2 reads crosstab/formatColor from reportData).
"""

from __future__ import annotations

import copy
import json
from typing import Any, Dict, List, Mapping, Optional, Sequence

# Matches client intialMarks (constants.js) with a stable placeholder id.
_DEFAULT_MARKS: Dict[str, Any] = {
    "value": "_all_",
    "id": "marks-all",
    "subVizType": "",
    "color": {"fields": []},
    "size": {"fields": []},
    "label": {"fields": []},
    "tooltip": {"fields": []},
    "shape": {"fields": []},
    "detail": {"fields": []},
}

# Matches getIntialReportState().properties — only keys S2 / GridTable consume.
_APPLY_ALL = ["pane", "tooltip", "label", "axis", "actions", "legend"]


def get_minimal_report_properties(
    *,
    show_totals: bool = True,
    format_fields: Optional[Sequence[Mapping[str, Any]]] = None,
) -> Dict[str, Any]:
    """Return ``properties`` shaped like ``getIntialReportState().properties``.

    Only includes the keys GridTable / S2Chart read from
    ``report.reportData.properties`` / ``report.properties``.
    """
    return {
        "title": {
            "show": False,
            "value": "",
            "padding": 0,
            "fontSize": 32,
            "fontColor": {"a": 1, "b": 0, "g": 0, "r": 0},
            "alignment": "center",
            "position": "top",
        },
        "subTitle": {
            "show": False,
            "value": "",
            "padding": 0,
            "fontSize": 24,
            "fontColor": {"a": 1, "b": 0, "g": 0, "r": 0},
            "alignment": "center",
            "position": "top",
        },
        "format": {
            "formatFields": list(format_fields or []),
            "formatDatatype": "",
            "activeFieldId": "",
            "showAll": False,
        },
        "formatColor": {
            "defaultColor": {"r": 84, "g": 108, "b": 230, "a": 1},
            "showAll": False,
            "dataColors": [],
            "formatColorStyle": "",
            "formatColorField": "",
            "minimum": {"r": 183, "g": 192, "b": 232, "a": 1},
            "maximum": {"r": 84, "g": 108, "b": 230, "a": 1},
            "backgroundColor": False,
            "enableSteps": False,
            "steps": None,
            "enableReverse": False,
            "minValue": 0,
            "maxValue": 0,
            "centerValue": 0,
            "enableAdvanceSteps": False,
        },
        "crosstab": {
            "showGrandTotals": show_totals,
            "showRowGrandTotals": show_totals,
            "showColumnGrandTotals": show_totals,
            "showSubTotals": show_totals,
            "showRowSubTotals": show_totals,
            "showColumnSubTotals": show_totals,
            "grandTotalsPosition": "Bottom",
            "subTotalsPosition": "Auto",
            "crosstabCollapse": "None",
        },
        "tooltip": {
            "showTooltip": True,
            "tooltipTemplate": "",
            "isTemplateEdited": False,
            "enableTemplate": False,
        },
        "axisRange": {
            "fields": [],
            "activeDatatype": "",
            "activeId": "",
            "gridLines": [],
            "synchronize": False,
            "showAxisName": False,
            "showGridChartAxisName": True,
        },
    }


def _normalize_name(value: Any) -> str:
    return str(value or "").strip()


def _field_entry(
    name: str,
    *,
    field_id: str,
    added_as: str,
    floating_type: str,
    data_type: str,
) -> Dict[str, Any]:
    """Minimal canvas field; ``autogen_alias`` is required by getFieldDisplayName."""
    return {
        "id": field_id,
        "label": name,
        "autogen_alias": name,
        "alias": name,
        "addedAs": added_as,
        "floatingType": floating_type,
        "type": {
            "backendDataType": (
                "java.lang.Double" if data_type == "numeric" else "java.lang.String"
            ),
            "dataType": data_type,
        },
        "hiddenIncludeInResultSet": False,
    }


def build_grid_fields(
    dimensions: Sequence[str],
    measures: Sequence[str],
    *,
    first_dim_as_row: bool = True,
) -> List[Dict[str, Any]]:
    """Build GridTable fields: first dim -> row, remaining dims -> column when 2+ dims."""
    dims = [_normalize_name(d) for d in dimensions if _normalize_name(d)]
    meas = [_normalize_name(m) for m in measures if _normalize_name(m)]
    fields: List[Dict[str, Any]] = []
    for i, name in enumerate(dims):
        if first_dim_as_row:
            added_as = "row" if i == 0 else "column"
        else:
            added_as = "row"
        fields.append(
            _field_entry(
                name,
                field_id=str(i + 1),
                added_as=added_as,
                floating_type="discrete",
                data_type="text",
            )
        )
    offset = len(dims)
    for i, name in enumerate(meas):
        fields.append(
            _field_entry(
                name,
                field_id=str(offset + i + 1),
                added_as="row",
                floating_type="continous",
                data_type="numeric",
            )
        )
    return fields


def build_metadata(fields: Sequence[Mapping[str, Any]]) -> List[Dict[str, Any]]:
    """S2 metadata entry keyed by 1-based field id."""
    entry: Dict[str, Any] = {}
    for field in fields:
        field_id = str(field.get("id") or "")
        if not field_id:
            continue
        floating = field.get("floatingType") or "discrete"
        entry[field_id] = {
            "name": field.get("autogen_alias") or field.get("label") or field_id,
            "type": "numeric" if floating == "continous" else "text",
        }
    return [entry]


def _match_format_string(
    field_name: str,
    format_strings: Mapping[str, str],
) -> Optional[str]:
    if not format_strings or not field_name:
        return None
    if field_name in format_strings and format_strings[field_name]:
        return str(format_strings[field_name])
    needle = field_name.casefold().replace(" ", "_")
    for key, fmt in format_strings.items():
        if not fmt:
            continue
        key_norm = str(key).casefold().replace(" ", "_")
        if (
            key_norm == needle
            or key_norm.endswith("_" + needle)
            or needle.endswith("_" + key_norm)
        ):
            return str(fmt)
    return None


def build_format_fields(
    fields: Sequence[Mapping[str, Any]],
    format_strings: Optional[Mapping[str, str]] = None,
) -> List[Dict[str, Any]]:
    """Build ``properties.format.formatFields`` for matching measure/column names."""
    if not format_strings:
        return []
    format_fields: List[Dict[str, Any]] = []
    for field in fields:
        name = str(field.get("autogen_alias") or field.get("label") or "")
        fmt = _match_format_string(name, format_strings)
        if not fmt:
            continue
        format_fields.append(
            {
                "id": str(field.get("id")),
                "values": {
                    "enableCustomFormatting": True,
                    "customFormat": fmt,
                    "isApplyClicked": True,
                    "apply": list(_APPLY_ALL),
                    "thousandSperator": False,
                    "decimalPlace": 2,
                    "prefix": "",
                    "suffix": "",
                    "displayUnits": "None",
                    "percentage": False,
                },
            }
        )
    return format_fields


def get_report_object(
    dimensions: Optional[Sequence[str]] = None,
    measures: Optional[Sequence[str]] = None,
    *,
    format_strings: Optional[Mapping[str, str]] = None,
    show_totals: bool = True,
    data: Optional[Sequence[Mapping[str, Any]]] = None,
) -> Dict[str, Any]:
    """Build a minimum report object for GridTable / pivot VF props.

    Shape aligns with ``getIntialReportState`` plus nested
    ``reportData.properties`` that S2Chart reads. Both ``properties`` and
    ``reportData.properties`` are set so formatters resolve via
    ``getPropertyFieldInfo(report)`` and crosstab via ``report.reportData``.
    """
    dims = list(dimensions or ["dimension_column"])
    meas = list(measures or ["measure_column"])
    fields = build_grid_fields(dims, meas)
    format_fields = build_format_fields(fields, format_strings)
    properties = get_minimal_report_properties(
        show_totals=show_totals,
        format_fields=format_fields,
    )
    metadata = build_metadata(fields)
    marks_list = [copy.deepcopy(_DEFAULT_MARKS)]

    report: Dict[str, Any] = {
        "mode": "create",
        "fields": fields,
        "marksList": marks_list,
        "metadata": metadata,
        "properties": properties,
        # S2Chart reads title/crosstab/formatColor/tooltip from reportData.properties
        "reportData": {
            "properties": copy.deepcopy(properties),
        },
    }
    if data is not None:
        report["data"] = list(data)
    return report


def report_object_to_js(report: Mapping[str, Any], *, indent: int = 2) -> str:
    """Serialize a report object as a JS object-literal string for VF templates."""
    return json.dumps(report, indent=indent, ensure_ascii=False)


def get_report_object_js_example(
    *,
    format_strings: Optional[Mapping[str, str]] = None,
    show_totals: bool = True,
) -> str:
    """Placeholder report JS used inside chart template instructions/code."""
    return report_object_to_js(
        get_report_object(
            ["dimension_column"],
            ["measure_column"],
            format_strings=format_strings or {"measure_column": "0.00"},
            show_totals=show_totals,
        )
    )
