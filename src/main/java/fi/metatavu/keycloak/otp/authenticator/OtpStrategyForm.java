package fi.metatavu.keycloak.otp.authenticator;

import lombok.extern.jbosslog.JBossLog;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.List;

@JBossLog
public class OtpStrategyForm implements Authenticator {

    static final String ID = "email-otp-form";

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
        if (formData.containsKey("email")) {
            context.getAuthenticationSession().setAuthNote(OtpConstants.OTP_STRATEGY, OtpConstants.OTP_STRATEGY_EMAIL);
        } else if (formData.containsKey("sms")) {
            context.getAuthenticationSession().setAuthNote(OtpConstants.OTP_STRATEGY, OtpConstants.OTP_STRATEGY_SMS);
        }

        context.attempted();
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
