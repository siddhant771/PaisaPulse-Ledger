import logging
import os

from dotenv import load_dotenv
from fastapi import FastAPI, HTTPException, Request
from fastapi.responses import JSONResponse

from .schemas import SmsProcessRequest, StructuredTransaction
from .security import AuthMiddleware
from .services.genai_engine import GenAIPipeline

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)
load_dotenv()

app = FastAPI(title="PaisaPulse: AI Ledger", version="1.0.0")
app.add_middleware(AuthMiddleware)
genai_service = GenAIPipeline() if os.getenv("GEMINI_API_KEY") else None


@app.get("/health")
async def health():
    return {"status": "ok", "ai_ready": genai_service is not None}


@app.get("/ready")
async def ready():
    if genai_service is None:
        raise HTTPException(status_code=503, detail="AI service is unavailable")
    return {"status": "ready"}


@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    logger.exception("Unhandled error while processing %s", request.url.path)
    return JSONResponse(status_code=500, content={"detail": "Internal server error"})


@app.post("/api/v1/process", response_model=StructuredTransaction)
async def process_sms(request: SmsProcessRequest):
    if genai_service is None:
        raise HTTPException(status_code=503, detail="AI service is unavailable")
    return genai_service.process(request.raw_sms)
