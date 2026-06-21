import os
from fastapi import FastAPI, HTTPException, Request
from fastapi.responses import JSONResponse
from security import AuthMiddleware
from schemas import SmsProcessRequest, StructuredTransaction
# IMPORT YOUR PIPELINE ENGINE HERE:
from services.genai_engine import GenAIPipeline

app = FastAPI(
    title="PaisaPulse: AI Ledger",
    description="Full-stack AI Financial Engine for Android",
    version="1.0.0"
)

# Instantiate the service only when an API key is provided.
genai_service = None
if os.getenv("GEMINI_API_KEY"):
    genai_service = GenAIPipeline()
else:
    print("WARNING: GEMINI_API_KEY is not set. GenAI pipeline will remain disabled.")

app.add_middleware(AuthMiddleware)

@app.get("/health")
async def health():
    return {"status": "ok"}

# --- GLOBAL EXCEPTION HANDLER ---
@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    print(f"CRITICAL ERROR: {exc}")
    return JSONResponse(
        status_code=500,
        content={"error": "An internal server error occurred. Please try again later."}
    )

# --- ROUTE ENDPOINTS ---
@app.post("/api/v1/process", response_model=StructuredTransaction)
async def process_sms(request: SmsProcessRequest):
    if not genai_service:
        raise HTTPException(status_code=503, detail="AI Service unavailable")
    
    # Pass raw_sms to the engine
    return genai_service.process(request.raw_sms)