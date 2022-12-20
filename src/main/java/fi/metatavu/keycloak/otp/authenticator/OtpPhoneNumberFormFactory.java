package fi.metatavu.keycloak.otp.authenticator;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

/**
 * Authenticator provider factory for OTP Phone Number Authenticator
 */
@AutoService(AuthenticatorFactory.class)
public class OtpPhoneNumberFormFactory implements AuthenticatorFactory {
    public static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES;

    public Authenticator create(KeycloakSession session) {
        return new OtpPhoneNumberForm();
    }

    public void init(Config.Scope config) {
        // Not used in this implementation
    }

    public void postInit(KeycloakSessionFactory factory) {
        // Not used in this implementation
    }

    public void close() {
        // Not used in this implementation
    }

    public String getId() {
        return OtpPhoneNumberForm.ID;
    }

    public String getReferenceCategory() {
        return "password";
    }

    public boolean isConfigurable() {
        return false;
    }

    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    public String getDisplayType() {
        return "OTP Phone Number Form";
    }

    public String getHelpText() {
        return "Selects a user from their phone number.";
    }

    public List<ProviderConfigProperty> getConfigProperties() {
        return null;
    }

    public boolean isUserSetupAllowed() {
        return false;
    }

    static {
        REQUIREMENT_CHOICES = new AuthenticationExecutionModel.Requirement[]{
                AuthenticationExecutionModel.Requirement.REQUIRED,
                AuthenticationExecutionModel.Requirement.ALTERNATIVE
        };
    }
}
