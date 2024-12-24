package org.demo.keycloak;

import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;

/**
 * Hello world!
 *
 */
public class AppConstraint {
    public static final String TEMP_PASS_ID = "temp-passwd-exp-authenticator";
    public static final String REST_PASS_ID = "user-reset-password";
    public static final String DISPLAY = "Temporary Password Expiry";
    public static final String HELPER_TEXT = "Temporary Password Expiry";
    public static final String PASS_EXP_TEXT = "TEMPORARY_PASSWORD_EXPIRY_DURATION";
    public static final String TEMP_PASS_ERROR_MESSAGE = "Temporary password expired";
    public static final String USER_NOT_FOUND_MESSAGE = "User not found";
    public static final String INVALID_CREDENTIAL_MESSAGE = "Invalid current password";
    public static final String PASS_CHANGED_MESSAGE = "Password reset successfully";

    public static final String USER_ACTION = "UPDATE_PASSWORD";
    public static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES =
            new AuthenticationExecutionModel.Requirement[]{Requirement.REQUIRED, Requirement.ALTERNATIVE, Requirement.DISABLED};
}
