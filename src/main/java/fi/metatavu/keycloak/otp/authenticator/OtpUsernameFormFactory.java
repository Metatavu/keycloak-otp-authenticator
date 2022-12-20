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
 * Authenticator provider factory for OTP Username Authenticator
 */
@AutoService(AuthenticatorFactory.class)
public class OtpUsernameFormFactory implements AuthenticatorFactory {
    public static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES;

    public Authenticator create(KeycloakSession session) {
        return new OtpUsernameForm();
    }

    public void init(Config.Scope config) {
    }

    public void postInit(KeycloakSessionFactory factory) {
    }

    public void close() {
    }

    public String getId() {
        return OtpUsernameForm.ID;
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
        return "OTP Username Form";
    }

    public String getHelpText() {
        return "Selects a user from their username.";
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
