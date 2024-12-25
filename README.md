# Keycloak Custom SPI: Temporary Reset Credential and Intentional Password Reset

This project provides two custom modules for Keycloak:

1. **Temporary Reset Credential Module**: Implements an expiry mechanism for temporary passwords.
2. **Intentional Password Reset**: Exposes a REST API to allow users to reset their password by providing the current valid password.

---

## Features

### 1. Temporary Reset Credential Module

- **Functionality**:  
  This module introduces an expiry mechanism for temporary passwords.  
  Admins can define an expiry duration when setting up a temporary password.

- **Configuration**:
    - Configure the module as an authenticator (`Temporary Password Expiry`) in the **browser authentication flow**.
    - When setting up a temporary password, the admin must add an attribute:
      ```
      TEMPORARY_PASSWORD_EXPIRY_DURATION
      ```
      The value of this attribute specifies the duration (in seconds) after which the password expires.

---

### 2. Intentional Password Reset Module

- **Functionality**:  
  This module exposes a REST API that allows users to reset their credentials by providing the current valid password.

- **Endpoint**:  
  ````commandline
    POST {KEYCLOAK_URL}/auth/realms/{REALM_NAME}/user-reset-password
   ````
  - **Request Parameters**:
    - `username`: The username of the account.
    - `currentPassword`: The current valid password of the user.
    - `newPassword`: The new password to be set for the account.

    - **Example Usage**:
      ````bash
      curl -X POST -H "Content-Type: application/x-www-form-urlencoded" \
      -d "username=user1" \
      -d "currentPassword=secret" \
      -d "newPassword=newSecret" \
      http://localhost:18080/auth/realms/master/user-reset-password
      ````
### Building the SPIs
To build the package, use Maven:
````bash
mvn clean package
````
After successful execution, the compiled JAR file will be located in the target/ directory.

### Deployment
1. **Add the module to Keycloak:**
   ````bash
   $KEYCLOAK_HOME/bin/jboss-cli.sh --command="module add --name=org.demo.keycloak.credential-spi \
   --resources=target/credential-spi.jar \
   --dependencies=org.keycloak.keycloak-core,org.keycloak.keycloak-services,org.keycloak.keycloak-server-spi,org.keycloak.keycloak-server-spi-private,org.jboss.logging"
   ````
2. **Configure Keycloak to include the SPI:**
   ````bash
   $KEYCLOAK_HOME/bin/jboss-cli.sh --command="/subsystem=keycloak-server:write-attribute(name=providers,value=[module:org.demo.keycloak.credential-spi])"
   ````
3. **Restart Keycloak**
   ````bash
   $KEYCLOAK_HOME/bin/jboss-cli.sh --command="shutdown --restart=true"
   ````
**Enabling user-reset-credential for a Specific Realm**
- To enable the `user-reset-credential` feature for a realm, use the following command:
  ````bash
  /subsystem=keycloak-server/spi=realm-restapi-extension:add()
  /subsystem=keycloak-server/spi=realm-restapi-extension/provider=user-reset-password:add(properties={realms=>demo},enabled=true)
  ````


