"""Runtime configuration utility service (LLM + application settings)."""

__all__ = ["UtilityService"]


def __getattr__(name: str):
    if name == "UtilityService":
        from helicalbi.service.utility.UtilityService import UtilityService

        return UtilityService
    raise AttributeError(f"module {__name__!r} has no attribute {name!r}")
