# PaisaPulse API

Set `GEMINI_API_KEY` and `DEVICE_API_TOKEN` in the server environment, then run:

```text
uvicorn backend.main:app --host 0.0.0.0 --port 8000
```

For Docker Compose, put the environment variables in a root `.env` file and run
`docker compose up --build -d` from the repository root.

The same root `.env` is loaded when running Uvicorn locally from the repository root.

For an Android emulator, add these untracked entries to the project's `local.properties`:

```text
API_BASE_URL=http://10.0.2.2:8000/
DEVICE_API_TOKEN=the-same-value-as-the-server
```

For a physical phone, use an HTTPS API URL (or the computer's reachable LAN address only for local development).
