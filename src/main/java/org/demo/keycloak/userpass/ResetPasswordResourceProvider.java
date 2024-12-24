package org.demo.keycloak.userpass;

import org.demo.keycloak.AppConstraint;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.resource.RealmResourceProvider;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ResetPasswordResourceProvider implements RealmResourceProvider {
    private static final Logger log = Logger.getLogger(ResetPasswordResourceProvider.class);
    private KeycloakSession session;

    public ResetPasswordResourceProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Object getResource() {
        return this;
    }

    @Override
    public void close() {

    }
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetPassword(@FormParam("username") String username,
                                  @FormParam("currentPassword") String currentPassword,
                                  @FormParam("newPassword") String newPassword) {

        UserModel user = session.users().getUserByUsername(session.getContext().getRealm(), username);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(AppConstraint.USER_NOT_FOUND_MESSAGE).build();
        }

        boolean isValid = session.userCredentialManager().isValid(session.getContext().getRealm(), user,
                UserCredentialModel.password(currentPassword));

        if (!isValid) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(AppConstraint.INVALID_CREDENTIAL_MESSAGE).build();
        }

        UserCredentialModel newPasswordCredential = UserCredentialModel.password(newPassword);
        session.userCredentialManager().updateCredential(session.getContext().getRealm(), user, newPasswordCredential);
        return Response.ok().entity(AppConstraint.PASS_CHANGED_MESSAGE).build();
    }
}
