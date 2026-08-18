"""Domain errors raised by the configuration utility service."""


class UtilityError(Exception):
    """Raised when a utility configuration operation cannot be completed."""

    def __init__(self, message: str, *, status: int = 0):
        super().__init__(message)
        self.message = message
        self.status = status
