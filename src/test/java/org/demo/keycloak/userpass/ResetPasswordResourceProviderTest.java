package org.demo.keycloak.userpass;

import org.demo.keycloak.AppConstraint;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.models.*;

import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResetPasswordResourceProviderTest {
    private static final Logger log = Logger.getLogger(ResetPasswordResourceProviderTest.class);

    private ResetPasswordResourceProvider provider;
    private KeycloakSession mockSession;
    private RealmModel mockRealm;
    private UserModel mockUser;
    private UserCredentialManager mockCredentialManager;
    private UserProvider mockUserProvider;

    @BeforeEach
    void setUp() {
        mockSession = mock(KeycloakSession.class);
        mockRealm = mock(RealmModel.class);
        mockUser = mock(UserModel.class);
        mockCredentialManager = mock(UserCredentialManager.class);
        mockUserProvider = mock(UserProvider.class);

        when(mockSession.getContext()).thenReturn(mock(KeycloakContext.class));
        when(mockSession.getContext().getRealm()).thenReturn(mockRealm);
        when(mockSession.userCredentialManager()).thenReturn(mockCredentialManager);
        when(mockSession.users()).thenReturn(mockUserProvider);

        provider = new ResetPasswordResourceProvider(mockSession);
    }

    @Test
    void testResetPasswordUserNotFound() {
        when(mockUserProvider.getUserByUsername(mockRealm, "nonexistent")).thenReturn(null);

        Response response = provider.resetPassword("nonexistent", "oldpass", "newpass");

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(AppConstraint.USER_NOT_FOUND_MESSAGE, response.getEntity());
    }

    @Test
    void testResetPasswordInvalidCurrentPassword() {
        when(mockUserProvider.getUserByUsername(mockRealm, "testuser")).thenReturn(mockUser);
        when(mockCredentialManager.isValid(mockRealm, mockUser, UserCredentialModel.password("wrongpass"))).thenReturn(false);
        Response response = provider.resetPassword("testuser", "wrongpass", "newpass");
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(AppConstraint.INVALID_CREDENTIAL_MESSAGE, response.getEntity());
    }

    @Test
    void testResetPasswordSuccess() {
        when(mockUserProvider.getUserByUsername(mockRealm, "user1")).thenReturn(mockUser);
        when(mockSession.userCredentialManager()).thenReturn(mockCredentialManager);
        when(mockCredentialManager.isValid(eq(mockRealm), eq(mockUser), any(UserCredentialModel.class)))
                .thenReturn(true);

        Response response = provider.resetPassword("user1", "changeit", "secret");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(AppConstraint.PASS_CHANGED_MESSAGE, response.getEntity());
    }
}