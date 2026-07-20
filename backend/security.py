import hmac
import os

from fastapi.responses import JSONResponse
from starlette.middleware.base import BaseHTTPMiddleware


class AuthMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request, call_next):
        if request.url.path in {"/health", "/docs", "/openapi.json"}:
            return await call_next(request)
        expected_token = os.getenv("DEVICE_API_TOKEN")
        provided_token = request.headers.get("X-Device-Token")
        if not expected_token or not provided_token or not hmac.compare_digest(
            provided_token, expected_token
        ):
            return JSONResponse(status_code=403, content={"detail": "Invalid device token"})
        return await call_next(request)
