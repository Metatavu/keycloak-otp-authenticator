package fi.metatavu.keycloak.otp.authenticator;

import lombok.extern.jbosslog.JBossLog;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * Authenticator providing a form for selecting Email or SMS OTP
 */
@JBossLog
public class OtpStrategyForm implements Authenticator {

    static final String ID = "otp-strategy-form";
    private static final String EMAIL_FORM_KEY = "email";
    private static final String SMS_FORM_KEY = "sms";

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        challenge(context);
    }

    private void challenge(AuthenticationFlowContext context) {

        LoginFormsProvider form = context.form().setExecution(context.getExecution().getId());

        Response response = form.createForm("otp-strategy.ftl");
        context.challenge(response);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        if (formData.containsKey(EMAIL_FORM_KEY)) {
            context.getAuthenticationSession().setAuthNote(OtpConstants.OTP_STRATEGY, OtpConstants.OTP_STRATEGY_EMAIL);
        } else if (formData.containsKey(SMS_FORM_KEY)) {
            context.getAuthenticationSession().setAuthNote(OtpConstants.OTP_STRATEGY, OtpConstants.OTP_STRATEGY_SMS);
        }

        context.success();
    }
    @Override
    public boolean requiresUser() {
        return false;
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
