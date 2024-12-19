package org.demo.keycloak;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.credential.CredentialProvider;
import org.keycloak.credential.PasswordCredentialProvider;
import org.keycloak.credential.PasswordCredentialProviderFactory;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import javax.ws.rs.core.Response;
import static org.mockito.Mockito.*;



public class TemporaryPasswordExpiryAuthenticatorTest {
    private TemporaryPasswordExpiryAuthenticator authenticator;
    @Mock
    private AuthenticationFlowContext context;
    @Mock
    private UserModel user;
    @Mock
    private PasswordCredentialProvider passwordProvider;
    @Mock
    private KeycloakSession session;
    @Mock
    private RealmModel realm;

    @Mock
    private PasswordCredentialModel credentialModel;

    @Mock
    private AuthenticationSessionModel authSession;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
        authenticator = new TemporaryPasswordExpiryAuthenticator();
        when(context.getUser()).thenReturn(user);
        when(context.getSession()).thenReturn(session);
        when(session.getProvider(CredentialProvider.class, PasswordCredentialProviderFactory.PROVIDER_ID))
                .thenReturn(passwordProvider);
        when(context.getRealm()).thenReturn(realm);
        when(context.getAuthenticationSession()).thenReturn(authSession);
    }

    @Test
    public void testAuthenticateSuccess() {
        when(user.getFirstAttribute(AppConstraint.PASS_EXP_TEXT)).thenReturn("7");
        when(user.getRequiredActionsStream()).thenReturn(Stream.of(AppConstraint.USER_ACTION));
        when(passwordProvider.getPassword(realm, user)).thenReturn(credentialModel);
        when(credentialModel.getCreatedDate()).thenReturn(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(5));
        authenticator.authenticate(context);
        verify(context).success();
        verify(context, never()).failure(any(), any());
    }

    @Test
    public void testAuthenticateFailure() {
        when(user.getFirstAttribute(AppConstraint.PASS_EXP_TEXT)).thenReturn("7");
        when(user.getRequiredActionsStream()).thenReturn(Stream.of("UPDATE_PASSWORD"));
        when(passwordProvider.getPassword(realm, user)).thenReturn(credentialModel);
        when(credentialModel.getCreatedDate()).thenReturn(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(10));
        LoginFormsProvider mockFormProvider = mock(LoginFormsProvider.class);
        when(context.form()).thenReturn(mockFormProvider);
        when(mockFormProvider.addError(any(FormMessage.class))).thenReturn(mockFormProvider);
        Response responseMock = mock(Response.class);
        when(mockFormProvider.createErrorPage(Response.Status.OK)).thenReturn(responseMock);
        authenticator.authenticate(context);
        verify(context).failure(eq(AuthenticationFlowError.INVALID_USER), eq(responseMock));
        verify(context, never()).success();
    }
}