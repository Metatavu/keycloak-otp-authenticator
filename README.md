# keycloak-otp-authenticator

This extension adds support for OTP login via SMS (Twilio) and Email (Keycloak builtin EmailProvider).

It requires following environment variables for Twilio to work.

```
      KC_TWILIO_ACCOUNT_SID: Twillio Account SID
      KC_TWILIO_AUTH_TOKEN: Twilio Account Auth Token
      KC_TWILIO_PHONE_NUMBER: Phone Number of Twilio SMS Service to use
```

Copy `target/fi.metatavu.keycloak-1.0.0-SNAPSHOT-jar-with-dependencies.jar` into `/opt/keycloak/providers`.
Copy `themes/otp-login` into `/opt/keycloak/themes`.

