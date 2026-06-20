from fastapi import FastAPI, Depends, HTTPException


# In main.py
from backend.security import AuthMiddleware
from backend.schemas import SmsProcessRequest, StructuredTransaction
app = FastAPI(
    title="PaisaPulse: AI Ledger",
    description="Full-stack AI Financial Engine for Android",
    version="1.0.0"
)
app.add_middleware(AuthMiddleware)
@app.get("/health")
async def health():
    return {"status":"ok"}
@app.post("/api/v1/process", response_model=StructuredTransaction)

async def process_sms(request: SmsProcessRequest):
    # For now, we return a mock response. 
    # This proves the pipeline works.
    return {
        "amount": 450.0,
        "merchant": "Swiggy",
        "category": "Food",
        "is_debit": True,
        "date": "2026-06-13",
        "confidence_score": 0.95
    }