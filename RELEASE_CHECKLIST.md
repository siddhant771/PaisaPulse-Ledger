# PaisaPulse release checklist

## Device verification

- [ ] Fresh install: consent and permissions are clear and optional.
- [ ] Deny SMS: manual transactions still work.
- [ ] Grant SMS: a transaction-like banking SMS creates one record and one generic notification.
- [ ] Re-send the exact SMS: no duplicate transaction is created.
- [ ] No internet/backend outage: the app stays usable and explains the failed categorization.
- [ ] Biometric cancellation leaves financial data locked.
- [ ] Export creates a readable CSV with correct columns and values.

## Policy and security

- [ ] Privacy policy, support email, and deletion process are publicly available.
- [ ] Prominent SMS/AI disclosure is reviewed by legal/privacy counsel.
- [ ] SMS Permissions Declaration and Data Safety form match actual behavior.
- [ ] Release API URL uses HTTPS and no API secrets are packaged in the APK.
- [ ] Server secrets are set in hosting-provider secret storage.

## Release build

- [ ] `./gradlew bundleRelease` succeeds.
- [ ] Test signed AAB through Play Console internal testing.
- [ ] Review Firebase/Crashlytics and server error monitoring after testing.
