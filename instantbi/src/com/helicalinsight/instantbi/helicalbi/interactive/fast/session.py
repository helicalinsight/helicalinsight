"""Load InstantBI model / metadata for a fast interactive turn."""
from __future__ import annotations

import logging
from dataclasses import dataclass, field
from typing import Any

from helicalbi.common.CubeInfoModel import is_cube_info_model, prepare_cube_info_model_data
from helicalbi.common.JsonToPara import has_table_column_info, prevalidate_cube_metadata
from helicalbi.controller.app_context import app

logger = logging.getLogger(__name__)


@dataclass
class FastModelSession:
    """Resolved model layer artifacts for one interactive request."""

    model_file_name: str
    location: str
    med_file_name: str
    md_location: str
    actual_md: dict[str, Any]
    cube_metadata: Any
    use_cube_info_flow: bool
    cube_info_prepared: dict[str, Any] = field(default_factory=dict)
    joins: list[Any] = field(default_factory=list)
    metadata_fun_ref: dict[str, Any] = field(default_factory=dict)


class FastModelSessionLoader:
    """Single responsibility: resolve semantic layer + DB function catalog."""

    def load(
        self,
        *,
        session_cookie: str,
        model_file_name: str,
        location: str,
    ) -> FastModelSession:
        helper = app().ModelLayerHelper(session_cookie, model_file_name, location)
        model_data = helper.get_model_semantic_layer()
        med_file_name = helper.get_metadata_layerfile()
        md_location = helper.get_metadata_layerlocation()
        actual_md = helper.get_metadata()
        use_cube_info_flow = is_cube_info_model(model_data)
        cube_info_prepared: dict[str, Any] = {}

        if use_cube_info_flow:
            cube_info_prepared = prepare_cube_info_model_data(model_data, actual_md)
            cube_metadata = cube_info_prepared["cube_metadata"]
            logger.info(
                "Interactive chat using cube_info model flow file=%s location=%s tables=%s",
                model_file_name,
                location,
                len(cube_metadata),
            )
        else:
            original_cube_metadata = model_data.get("cube_metadata")
            cube_metadata = prevalidate_cube_metadata(original_cube_metadata, actual_md)
            if not has_table_column_info(original_cube_metadata) and cube_metadata:
                logger.info(
                    "Interactive chat using metadata API fallback for cube schema "
                    "file=%s location=%s tables=%s",
                    med_file_name,
                    md_location,
                    len(cube_metadata),
                )

        metadata_fun_ref = app().get_db_function_of_metadata(
            session_cookie, med_file_name, md_location
        )
        return FastModelSession(
            model_file_name=model_file_name,
            location=location,
            med_file_name=med_file_name,
            md_location=md_location,
            actual_md=actual_md,
            cube_metadata=cube_metadata,
            use_cube_info_flow=use_cube_info_flow,
            cube_info_prepared=cube_info_prepared,
            joins=actual_md.get("joins") or [],
            metadata_fun_ref=metadata_fun_ref,
        )
