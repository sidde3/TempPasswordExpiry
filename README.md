# Temporary Password Expiry
This is a POC model of setting expiry on the temporary password. 

## Building SPI
Building this package using maven executing the command ``mvn clean package``

## Deployment of SPI
- Since this package is based on Keycloak version 18.0, the deployment of this module can be achieved through jboss-cli
```commandline
$KEYCLOAK_HOME/bin/jboss-cli.sh --command="module add --name=org.demo.keycloak.temporary-password-expiry-spi --resources=target/temporary-password-expiry-spi.jar --dependencies=org.keycloak.keycloak-core,org.keycloak.keycloak-services,org.keycloak.keycloak-server-spi,org.keycloak.keycloak-server-spi-private,org.jboss.logging"
```
- Configure keycloak to include the SPI
```commandline
$KEYCLOAK_HOME/bin/jboss-cli.sh --command="/subsystem=keycloak-server:write-attribute(name=providers,value=[module:org.demo.keycloak.temporary-password-expiry-spi])"
```
- Restart keycloak
```commandline
$KEYCLOAK_HOME/bin/jboss-cli.sh --command="shutdown --restart=true"
```