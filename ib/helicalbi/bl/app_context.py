"""Resolve patchable dependencies from ``app`` for integration tests."""


def app():
    import app as app_module

    return app_module
