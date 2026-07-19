import os

from google import genai
from google.genai import types

from ..schemas import StructuredTransaction


class GenAIPipeline:
    def __init__(self):
        api_key = os.getenv("GEMINI_API_KEY")
        if not api_key:
            raise RuntimeError("GEMINI_API_KEY is required")
        self.client = genai.Client(api_key=api_key)
        self.model = os.getenv("GEMINI_MODEL", "gemini-2.5-flash")

    def process(self, raw_text: str) -> StructuredTransaction:
        response = self.client.models.generate_content(
            model=self.model,
            contents=raw_text,
            config=types.GenerateContentConfig(
                system_instruction=(
                    "Extract one financial transaction from this SMS. Return only the schema. "
                    "For invalid messages use amount 0, merchant Unknown, category Uncategorized, "
                    "is_debit false, date as an empty string, and confidence_score 0."
                ),
                response_mime_type="application/json",
                response_schema=StructuredTransaction,
            ),
        )
        if response.parsed is None:
            raise RuntimeError("AI returned no structured transaction")
        return response.parsed
