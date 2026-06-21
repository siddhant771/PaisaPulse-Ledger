from pydantic import BaseModel, Field
from typing import Optional

# The input coming from your Android app
class SmsProcessRequest(BaseModel):
    raw_sms: str = Field(..., description="The raw SMS text from the banking channel")
    device_id: str = Field(..., description="The hashed anonymous user identifier")

# The output format we want from Gemini
class StructuredTransaction(BaseModel):
    amount: float = Field(..., description="The transaction amount")
    merchant: str = Field(..., description="The merchant name (e.g. Swiggy, Zomato)")
    category: str = Field(..., description="Budget category like Food, Transport, Rent")
    is_debit: bool = Field(..., description="True if debited, False if credited")
    date: str = Field(..., description="The date extracted from the SMS")
    confidence_score: float = Field(..., description="AI certainty (0.0 to 1.0)")