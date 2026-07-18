# Use the official Python slim image for a smaller footprint
FROM python:3.11-slim

# Set the working directory
WORKDIR /app

# Install dependencies first (leverages Docker layer caching)
COPY backend/requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy your source code
COPY backend /app/backend

RUN useradd --create-home --uid 10001 appuser && chown -R appuser:appuser /app
USER appuser

# Expose the API port
EXPOSE 8000

# Start the server
CMD ["uvicorn", "backend.main:app", "--host", "0.0.0.0", "--port", "8000"]
