package org.demo.keycloak;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.common.util.Time;
import org.keycloak.credential.CredentialModel;
import org.keycloak.credential.CredentialProvider;
import org.keycloak.credential.PasswordCredentialProvider;
import org.keycloak.credential.PasswordCredentialProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;

import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class TemporaryPasswordExpiryAuthenticator implements Authenticator {
    private static final Logger log = Logger.getLogger(TemporaryPasswordExpiryAuthenticator.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        UserModel user = context.getUser();
        int daysToExpirePassword = Optional.ofNullable(user.getFirstAttribute(AppConstraint.PASS_EXP_TEXT)).map(Integer::valueOf).orElse(0);
        boolean tempPass = user.getRequiredActionsStream().anyMatch(AppConstraint.USER_ACTION::equals);
        log.infof("Configured expiry %d, Temp Password enabled %s", daysToExpirePassword, tempPass);

        if(tempPass && daysToExpirePassword != 0) {
            PasswordCredentialProvider passwordProvider = (PasswordCredentialProvider) context.getSession().getProvider(CredentialProvider.class, PasswordCredentialProviderFactory.PROVIDER_ID);
            CredentialModel password = passwordProvider.getPassword(context.getRealm(), context.getUser());
            log.debugf("Password creation date %s", password.getCreatedDate());

            if (password.getCreatedDate() != null) {
                long timeElapsed = Time.toMillis(Time.currentTime()) - password.getCreatedDate();
                long timeToExpire = TimeUnit.DAYS.toMillis(daysToExpirePassword);
                log.debugf("Elapsed time %s, Expired time %s", timeElapsed, timeToExpire);
                if (timeElapsed > timeToExpire) {
                    user.setEnabled(false); //disabled user
                    Response challenge = context.form().addError(new FormMessage(AppConstraint.ERROR_MESSAGE)).createErrorPage(Response.Status.OK);
                    context.failure(AuthenticationFlowError.INVALID_USER,challenge);
                    return;
                }
            }
        }
        // If all checks pass, proceed with success
        context.success();
    }

    @Override
    public void action(AuthenticationFlowContext context) {

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

    }

    @Override
    public void close() {

    }
}
