package fi.metatavu.keycloak.otp.authenticator;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.config.ConfigProvider;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.AuthenticationFlowException;
import org.keycloak.authentication.Authenticator;
import org.keycloak.common.util.SecretGenerator;
import org.keycloak.email.EmailException;
import org.keycloak.email.EmailTemplateProvider;
import org.keycloak.events.Errors;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.services.messages.Messages;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fi.metatavu.keycloak.otp.authenticator.OtpConstants.*;

/**
 * Authenticator for sending and verifying OTP code
 */
@JBossLog
public class OtpCodeAuthenticatorForm implements Authenticator {

    static final String ID = "otp-code-authenticator-form";
    private static final String TWILIO_ACCOUNT_SID = ConfigProvider.getConfig().getValue("kc.twilio.account.sid", String.class);
    private static final String TWILIO_AUTH_TOKEN = ConfigProvider.getConfig().getValue("kc.twilio.auth.token", String.class);
    private static final String TWILIO_PHONE_NUMBER = ConfigProvider.getConfig().getValue("kc.twilio.phone.number", String.class);

    private final KeycloakSession session;

    /**
     * Constructor
     *
     * @param session Keycloak session
     */
    public OtpCodeAuthenticatorForm(KeycloakSession session) {
        Twilio.init(TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN);
        this.session = session;
    }

    /**
     * This method is being run by Keycloak upon executing.
     *
     * @param context context
     */
    @Override
    public void authenticate(AuthenticationFlowContext context) {
        challenge(context, null);

    }

    /**
     * Validates form data and appends possible error message to form
     *
     * @param context context
     * @param errorMessage error message
     */
    private void challenge(AuthenticationFlowContext context, FormMessage errorMessage) {
        final Map<String, String> authenticatorConfig = context.getAuthenticatorConfig().getConfig();
        final int otpCodeLength = Integer.parseInt(authenticatorConfig.get(OtpConstants.OTP_CODE_LENGTH_CONFIG));

        generateAndSendOtpCode(context, otpCodeLength);

        LoginFormsProvider form = context.form().setExecution(context.getExecution().getId());
        if (errorMessage != null) {
            form.setErrors(List.of(errorMessage));
        }

        Response response = form
                .setAttribute("codeLength", otpCodeLength)
                .createForm("otp-code-form.ftl");
        context.challenge(response);
    }

    /**
     * Generates OTP code and sends it via SMS or Email.
     *
     * @param context Authentication flow context
     * @param otpCodeLength Length of the code to be generated
     */
    private void generateAndSendOtpCode(AuthenticationFlowContext context, Integer otpCodeLength) {
        if (context.getAuthenticationSession().getAuthNote(OTP_AUTH_NOTE) != null) {
            return;
        }

        String otpCode = SecretGenerator.getInstance().randomString(otpCodeLength, SecretGenerator.DIGITS);
        String otpStrategy = context.getAuthenticationSession().getAuthNote(OTP_STRATEGY);

        context.getAuthenticationSession().setAuthNote(OTP_AUTH_NOTE, otpCode);

        switch (otpStrategy) {
            case OTP_STRATEGY_EMAIL: sendEmailWithCode(context.getRealm(), context.getUser(), otpCode);
                break;
            case OTP_STRATEGY_SMS: sendSmsWithCode(context.getRealm(), context.getUser(), otpCode);
                break;
            default: throw new AuthenticationFlowException(AuthenticationFlowError.GENERIC_AUTHENTICATION_ERROR);
        }
    }

    /**
     * Called when form is being submitted.
     * Checks what form button is pressed and acts accordingly.
     * If correct OTP code is given, this Authentication Flow Context is marked successful.
     *
     * @param context context
     */
    @Override
    public void action(AuthenticationFlowContext context) {
        final Map<String, String> authenticatorConfig = context.getAuthenticatorConfig().getConfig();
        final int otpCodeLength = Integer.parseInt(authenticatorConfig.get(OtpConstants.OTP_CODE_LENGTH_CONFIG));
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();

        if (formData.containsKey(RESEND_CODE_FIELD_NAME)) {
            resetOtpCode(context);
            challenge(context, null);
            return;
        }

        if (formData.containsKey(CANCEL_FIELD_NAME)) {
            resetOtpCode(context);
            context.resetFlow();
            return;
        }
        StringBuilder submittedCode = new StringBuilder();
        for (int i = 1; i <= otpCodeLength; i++) {
            submittedCode.append(formData.getFirst(String.format("code-%s", i)));
        }

        int givenSmsCode = Integer.parseInt(String.valueOf(submittedCode));
        boolean valid = validateCode(context, givenSmsCode);

        if (!valid) {
            context.getEvent().error(Errors.INVALID_USER_CREDENTIALS);
            challenge(context, new FormMessage(Messages.INVALID_ACCESS_CODE));
            return;
        }

        resetOtpCode(context);
        context.success();
    }

    /**
     * Resets current valid SMS OTP code.
     *
     * @param context context
     */
    private void resetOtpCode(AuthenticationFlowContext context) {
        context.getAuthenticationSession().removeAuthNote(OTP_AUTH_NOTE);
    }

    /**
     * Validates that given SMS OTP code is correct.
     *
     * @param context context
     * @param givenCode given code
     * @return Whether given code is correct
     */
    private boolean validateCode(AuthenticationFlowContext context, int givenCode) {
        int smsCode = Integer.parseInt(context.getAuthenticationSession().getAuthNote(OTP_AUTH_NOTE));
        return givenCode == smsCode;
    }


    /**
     * Sends Email with OTP code to user.
     * Throws error if email is not found on user.
     *
     * @param realm realm
     * @param user user
     * @param otpCode otp code
     */
    private void sendEmailWithCode(RealmModel realm, UserModel user, String otpCode) {
        if (user.getEmail() == null) {
            log.warnf("Could not send access code email due to missing email. realm=%s user=%s", realm.getId(), user.getUsername());
            throw new AuthenticationFlowException(AuthenticationFlowError.INVALID_USER);
        }

        Map<String, Object> mailBodyAttributes = new HashMap<>();
        mailBodyAttributes.put("code", otpCode);

        String realmName = realm.getDisplayName() != null ? realm.getDisplayName() : realm.getName();
        List<Object> subjectParams = List.of(realmName);
        try {
            EmailTemplateProvider emailProvider = session.getProvider(EmailTemplateProvider.class);
            emailProvider.setRealm(realm);
            emailProvider.setUser(user);

            emailProvider.send("emailCodeSubject", subjectParams, "otp-email.ftl", mailBodyAttributes);
        } catch (EmailException eex) {
            log.errorf(eex, "Failed to send access code email. realm=%s user=%s", realm.getId(), user.getUsername());
        }
    }

    /**
     * Sends SMS with OTP code to user.
     * Throws error if phone number is not found on user.
     *
     * @realm realm
     * @param user user
     * @param otpCode otp code
     */
    private void sendSmsWithCode(RealmModel realm, UserModel user, String otpCode) {
        String userPhoneNumber = user.getFirstAttribute(OtpPhoneNumberForm.PHONE_NUMBER_ATTRIBUTE_NAME);

        if (userPhoneNumber == null) {
            log.warnf("Could not send OTP Code SMS due to missing phone number. Realm=%s User=%s", realm.getId(), user.getUsername());
            throw new AuthenticationFlowException(AuthenticationFlowError.INVALID_USER);
        }

        Message.creator(
                new PhoneNumber(userPhoneNumber),
                new PhoneNumber(TWILIO_PHONE_NUMBER),
                String.format("Your one time code for Votech is: %s", otpCode)
        ).create();
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // NOOP
    }

    @Override
    public void close() {
        // NOOP
    }
}
