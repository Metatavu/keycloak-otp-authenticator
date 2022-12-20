package fi.metatavu.keycloak.otp.authenticator;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordForm;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.forms.login.freemarker.LoginFormsUtil;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * Authenticator for initiating OTP login with phone number
 */
public class OtpPhoneNumberForm extends UsernamePasswordForm {
    static final String ID = "otp-phone-number-authenticator-form";
    public static final String PHONE_NUMBER_ATTRIBUTE_NAME = "PHONE_NUMBER";
    public static final String PHONE_NUMBER_FORM_FIELD = "phoneNumber";
    private static final String USER_NOT_FOUND_MESSAGE = "User with provided phone number could not be found.";
    private static final String INVALID_PHONE_NUMBER_MESSAGE = "Invalid phone number.";

    /**
     * This method is being run by Keycloak upon executing.
     * If a user is already associated with the Authentication Flow Context, this form is skipped.
     *
     * @param context context
     */
    @Override
    public void authenticate(AuthenticationFlowContext context) {
        if (context.getUser() != null) {
            List<IdentityProviderModel> identityProviders = LoginFormsUtil.filterIdentityProviders(context.getRealm().getIdentityProvidersStream(), context.getSession(), context);
            if (identityProviders.isEmpty()) {
                context.success();
                return;
            }
        }

        if (OtpConstants.OTP_STRATEGY_SMS.equals(context.getAuthenticationSession().getAuthNote(OtpConstants.OTP_STRATEGY))) {
            super.authenticate(context);
        } else {
            context.success();
        }
    }

    /**
     * Validates form data.
     * If user is found with given phone number, the found user is added to the Authentication Flow Context.
     *
     * @param context context
     * @param formData form data
     * @return Whether user was found with forms phone number
     */
    @Override
    protected boolean validateForm(AuthenticationFlowContext context, MultivaluedMap<String, String> formData) {
        String phoneNumber = formData.getFirst(PHONE_NUMBER_FORM_FIELD).replace(" ", "");

        if (!validatePhoneNumber(phoneNumber)) {
            context.getEvent().error("invalid_phone_number");
            Response challengeResponse = failureChallenge(context, INVALID_PHONE_NUMBER_MESSAGE, PHONE_NUMBER_FORM_FIELD);
            context.failureChallenge(AuthenticationFlowError.GENERIC_AUTHENTICATION_ERROR, challengeResponse);

            return false;
        }

        UserModel foundUser = context.getSession().users().getUsersStream(context.getRealm(), false)
                .filter(user -> phoneNumber.equals(user.getFirstAttribute(PHONE_NUMBER_ATTRIBUTE_NAME)))
                .findFirst()
                .orElse(null);

        if (foundUser != null) {
            context.setUser(foundUser);
            return true;
        }

        context.getEvent().error("user_not_found");
        Response challengeResponse = failureChallenge(context, USER_NOT_FOUND_MESSAGE, PHONE_NUMBER_FORM_FIELD);
        context.failureChallenge(AuthenticationFlowError.INVALID_USER, challengeResponse);

        return false;
    }

    /**
     * Renders Phone number form from template.
     * Called by Keycloak when this Authentication Flow Step is executed.
     *
     * @param context context
     * @param formData form data
     * @return Response
     */
    @Override
    protected Response challenge(AuthenticationFlowContext context, MultivaluedMap<String, String> formData) {
        LoginFormsProvider form = context.form();
        if (formData != null) {
            form.setFormData(formData);
        }

        return createPhoneNumberForm(form);
    }

    /**
     * Fails current authentication flow challenge and returns response with errors.
     *
     * @param context context
     * @param error error message
     * @param field error field
     * @return Response
     */
    private Response failureChallenge(AuthenticationFlowContext context, String error, String field) {
        LoginFormsProvider form = context.form().setExecution(context.getExecution().getId());
        if (error != null) {
            if (field != null) {
                form.addError(new FormMessage(field, error));
            } else {
                form.setError(error, new Object[0]);
            }
        }

        return createPhoneNumberForm(form);
    }

    /**
     * Creates a response with phone number form template
     * @param form Form to render template into
     * @return Response from template
     */
    private Response createPhoneNumberForm(LoginFormsProvider form) {
        return form.createForm("otp-phone-number-form.ftl");
    }

    /**
     * Validates that given phone number is valid Finnish number or possible number in other countries.
     *
     * @param phoneNumber phone number
     * @return Whether given number is valid Finnish number or possible number in other countries
     */
    private boolean validatePhoneNumber(String phoneNumber) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

        return phoneNumberUtil.isPossibleNumber(phoneNumber, "FI");
    }
}
