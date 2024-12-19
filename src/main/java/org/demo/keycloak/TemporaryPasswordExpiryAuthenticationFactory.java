package org.demo.keycloak;

import org.jboss.logging.Logger;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

public class TemporaryPasswordExpiryAuthenticationFactory implements AuthenticatorFactory {
    private static final Logger log = Logger.getLogger(TemporaryPasswordExpiryAuthenticationFactory.class);
    @Override
    public String getDisplayType() {
        return AppConstraint.DISPLAY;
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return AppConstraint.REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return AppConstraint.HELPER_TEXT;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return null;
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return new TemporaryPasswordExpiryAuthenticator();
    }

    @Override
    public void init(org.keycloak.Config.Scope config) {
        log.infof("%s initialized...",this.getId());
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return AppConstraint.ID;
    }
}
