package fi.metatavu.keycloak.otp.authenticator;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordForm;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.forms.login.freemarker.LoginFormsUtil;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.utils.FormMessage;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.List;

public class OtpUsernameForm extends UsernamePasswordForm {

    public OtpUsernameForm() {
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        if (context.getUser() != null) {
            List<IdentityProviderModel> identityProviders = LoginFormsUtil.filterIdentityProviders(context.getRealm().getIdentityProvidersStream(), context.getSession(), context);
            if (identityProviders.isEmpty()) {
                context.success();
                return;
            }
        }

        if (OtpConstants.OTP_STRATEGY_EMAIL.equals(context.getAuthenticationSession().getAuthNote(OtpConstants.OTP_STRATEGY))) {
            super.authenticate(context);
        } else {
            context.success();
        }
    }

    @Override
    public boolean validateForm(AuthenticationFlowContext context, MultivaluedMap<String, String> formData) {
        boolean validUsername = this.validateUser(context, formData);
        if (!validUsername){
            context.getEvent().error("invalid_email");
            Response challengeResponse = failureChallenge(context, "invalid email", "username");
            context.failureChallenge(AuthenticationFlowError.GENERIC_AUTHENTICATION_ERROR, challengeResponse);

            return false;
        }

        return true;
    }

    @Override
    protected Response challenge(AuthenticationFlowContext context, MultivaluedMap<String, String> formData) {
        LoginFormsProvider form = context.form();
        if (!formData.isEmpty()) {
            form.setFormData(formData);
        }

        return form.createLoginUsername();
    }

    private Response failureChallenge(AuthenticationFlowContext context, String error, String field) {
        LoginFormsProvider form = context.form().setExecution(context.getExecution().getId());
        if (error != null) {
            if (field != null) {
                form.addError(new FormMessage(field, error));
            } else {
                form.setError(error, new Object[0]);
            }
        }

        return form.createLoginUsername();
    }
}
