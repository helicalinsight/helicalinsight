"""CLI: python -m sql_to_formdata path/to.sql --location test --metadata-file ..."""

from __future__ import annotations

import argparse
import json
import sys
from pathlib import Path

from .assembler import sql_to_form_data
from .metadata import load_metadata_from_report


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(
        description="Convert SQL SELECT → wire formData JSON (uses getFunctions for aggregates/db fns)"
    )
    parser.add_argument("sql_file", type=Path, help="Path to .sql file")
    parser.add_argument("-o", "--output", type=Path, help="Write formData JSON here")

    parser.add_argument("--location", default="", help="Metadata location / dir")
    parser.add_argument("--metadata-dir", default="", help="Alias for --location when location omitted")
    parser.add_argument(
        "--metadata-file",
        default="",
        help="metadataFileName e.g. pg_sample_travel_data.metadata",
    )
    parser.add_argument(
        "--session-cookie",
        default="",
        help="JSESSIONID for getFunctions (required unless --functions-file)",
    )
    parser.add_argument(
        "--functions-file",
        type=Path,
        help="Use stored getFunctions JSON instead of calling the API",
    )

    parser.add_argument(
        "--meta",
        type=Path,
        help="Optional report JSON (formdatajson.json) for column types",
    )
    parser.add_argument(
        "--dialect",
        default="",
        help="Override sqlglot dialect (default: from getFunctions response.reference)",
    )
    parser.add_argument(
        "--layers",
        action="store_true",
        help="Include _layers debug breakdown in output",
    )
    args = parser.parse_args(argv)

    sql = args.sql_file.read_text(encoding="utf-8")
    column_meta = load_metadata_from_report(args.meta) if args.meta else {}

    location = args.location or args.metadata_dir or column_meta.get("location", "")
    metadata_file = args.metadata_file or column_meta.get("metadataFileName", "")

    if not args.functions_file and (not location or not metadata_file):
        parser.error(
            "--location and --metadata-file are required for getFunctions "
            "(or pass --functions-file / provide them via --meta)"
        )
    if not args.functions_file and not args.session_cookie:
        parser.error("--session-cookie is required unless --functions-file is provided")

    form_data = sql_to_form_data(
        sql,
        location=location,
        metadata_file_name=metadata_file,
        session_cookie=args.session_cookie,
        functions_file=args.functions_file,
        dialect=args.dialect or None,
        metadata=column_meta,
        include_layers=args.layers,
    )

    text = json.dumps(form_data, indent=2)
    if args.output:
        args.output.write_text(text, encoding="utf-8")
        print(f"Wrote {args.output}", file=sys.stderr)
    else:
        print(text)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
