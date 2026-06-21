import os
from google import genai
from google.genai import types
# Import the schema defined in your Pydantic model
from schemas import StructuredTransaction

class GenAIPipeline:
    def __init__(self):
        api_key = os.getenv("GEMINI_API_KEY")
        if not api_key:
            raise RuntimeError("CRITICAL: GEMINI_API_KEY environment variable is missing.")
            
        self.client = genai.Client(api_key=api_key)
        self.model_name = "gemini-2.5-flash"
        
        self.system_instruction = (
            "You are a low-latency, deterministic core banking data extraction kernel. "
            "Your role is strictly to process raw text (such as financial SMS alerts or notifications) "
            "and map them into the requested schema fields. Do not engage in dialogue, do not correct "
            "grammar, and do not provide markdown formatting code blocks. If the string is not a valid "
            "financial transaction record, populate the fields with empty defaults and set confidence_score to 0.0."
        )

    def process(self, raw_text: str):
        response = self.client.models.generate_content(
            model=self.model_name,
            contents=raw_text,
            config=types.GenerateContentConfig(
                system_instruction=self.system_instruction,
                response_mime_type="application/json",
                # The Gemini SDK uses the Pydantic model to enforce structure
                response_schema=StructuredTransaction, 
            ),
        )
        return response.parsed