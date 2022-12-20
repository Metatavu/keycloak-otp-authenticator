package fi.metatavu.keycloak.otp.authenticator;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Authenticator provider factory for OTP Username Authenticator
 */
@AutoService(AuthenticatorFactory.class)
public class OtpUsernameFormFactory implements AuthenticatorFactory {
    public static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES;

    /**
     * Creates new OTP Username Form
     *
     * @param session Keycloak Session
     * @return OTP Username Form
     */
    @Override
    public Authenticator create(KeycloakSession session) {
        return new OtpUsernameForm();
    }

    @Override
    public void init(Config.Scope config) {
        // Not used in this implementation
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Not used in this implementation
    }

    @Override
    public void close() {
        // Not used in this implementation
    }

    @Override
    public String getId() {
        return OtpUsernameForm.ID;
    }

    @Override
    public String getReferenceCategory() {
        return "password";
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public String getDisplayType() {
        return "OTP Username Form";
    }

    @Override
    public String getHelpText() {
        return "Selects a user from their username.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return new ArrayList<>();
    }

    @Override
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
