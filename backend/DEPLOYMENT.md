# Production deployment checklist

## Container

Build and validate locally from the repository root:

```text
docker compose up --build -d
docker compose ps
curl http://localhost:8000/health
curl http://localhost:8000/ready
```

`/health` reports that the process is alive. `/ready` returns `200` only when the Gemini service has been configured.

## Hosting requirements

Deploy the container to a managed HTTPS service. Configure these server-side secrets in the hosting provider; never commit them or place them in the Android app:

```text
GEMINI_API_KEY=
DEVICE_API_TOKEN=
```

The platform must route public HTTPS traffic to container port `8000`, restart unhealthy containers, and retain application logs. Configure a custom API domain only after HTTPS is active.

## Android release configuration

The release build must use an HTTPS `API_BASE_URL`. Do not use `localhost`, `10.0.2.2`, a LAN IP, or cleartext HTTP outside debug testing.

## Before publishing

- Publish a privacy-policy URL and put it in the app settings screen.
- Implement account/authentication architecture before using the app with real customers; an APK-embedded device token is not a production authentication system.
- Complete the Play SMS Permissions Declaration and Data Safety form.
- Test on physical devices and verify retry/duplicate behavior with real SMS formats.
