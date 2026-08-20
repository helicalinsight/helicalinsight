"""Resolve patchable dependencies from ``app`` for integration tests."""

import sys


def app():
    existing = sys.modules.get("app")
    if existing is not None:
        return existing
    try:
        import app as app_module

        return app_module
    except ModuleNotFoundError:
        main = sys.modules.get("__main__")
        if main is not None:
            sys.modules["app"] = main
            return main
        raise
