package fi.metatavu.keycloak.otp.authenticator;

/**
 * Constants used across OTP Authenticators
 */
public class OtpConstants {
    public static final String OTP_STRATEGY = "OTP_STRATEGY";
    public static final String OTP_STRATEGY_EMAIL = "EMAIL";
    public static final String OTP_STRATEGY_SMS = "SMS";
    public static final String OTP_AUTH_NOTE = "OTP_CODE";
    public static final String RESEND_CODE_FIELD_NAME = "resendCode";
    public static final String CANCEL_FIELD_NAME = "cancel";
    public static final String OTP_CODE_LENGTH_CONFIG = "otp.code.length";
}
