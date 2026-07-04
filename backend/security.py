from fastapi import Request, HTTPException, status
from starlette.middleware.base import BaseHTTPMiddleware
VALID_DEVICE_TOKENS = ["secure-device-123", "debug-emulator-001"]
class AuthMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request: Request, call_next):
      
        if request.url.path == "/health":
            return await call_next(request)
        token = request.headers.get("X-Device-Token")
        if token not in VALID_DEVICE_TOKENS:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Invalid or Missing Device token"
            )
        response=await call_next(request)
        return response
