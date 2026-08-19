"""In-memory cache for HI service API responses."""

from __future__ import annotations

import copy
import hashlib
import json
import logging
import threading
from collections import OrderedDict
from typing import Any, Optional

from helicalbi.common import app_config

logger = logging.getLogger(__name__)

_lock = threading.Lock()
_cache: OrderedDict[str, dict[str, Any]] = OrderedDict()


def build_cache_key(
    form_data: str,
    username: str,
    orgname: str,
    org_id: Optional[int] = None,
) -> str:
    payload = {
        "formData": form_data or "",
        "username": username or "",
        "orgname": orgname or "",
        "orgId": org_id,
    }
    raw = json.dumps(payload, sort_keys=True, separators=(",", ":"))
    return hashlib.sha256(raw.encode("utf-8")).hexdigest()


def _is_enabled() -> bool:
    return app_config.api_cache_enabled


def _max_entries() -> int:
    return app_config.api_cache_max_entries


def get(
    form_data: str,
    username: str,
    orgname: str,
    org_id: Optional[int] = None,
) -> Optional[dict[str, Any]]:
    if not _is_enabled():
        return None

    key = build_cache_key(form_data, username, orgname, org_id)
    with _lock:
        cached = _cache.get(key)
        if cached is None:
            return None
        _cache.move_to_end(key)
    logger.info(
        "API cache hit username=%s orgname=%s org_id=%s key=%s",
        username or "<empty>",
        orgname or "<empty>",
        org_id,
        key[:12],
    )
    return copy.deepcopy(cached)


def set(
    form_data: str,
    username: str,
    orgname: str,
    response: dict[str, Any],
    org_id: Optional[int] = None,
) -> None:
    if not _is_enabled():
        return

    key = build_cache_key(form_data, username, orgname, org_id)
    with _lock:
        if key in _cache:
            del _cache[key]
        _cache[key] = copy.deepcopy(response)
        while len(_cache) > _max_entries():
            evicted_key, _ = _cache.popitem(last=False)
            logger.debug("API cache evicted oldest entry key=%s", evicted_key[:12])
    logger.debug(
        "API cache stored username=%s orgname=%s org_id=%s key=%s entries=%s",
        username or "<empty>",
        orgname or "<empty>",
        org_id,
        key[:12],
        len(_cache),
    )


def clear() -> int:
    with _lock:
        count = len(_cache)
        _cache.clear()
    logger.info("API cache cleared entries=%s", count)
    return count
