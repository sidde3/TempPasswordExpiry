package org.demo.keycloak.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResetPasswordModel {
    private String username;
    private String currentPassword;
    private String newPassword;
}
