package org.demo.keycloak.userpass;

import org.demo.keycloak.AppConstraint;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ResetPasswordResourceProviderFactory implements RealmResourceProviderFactory {
    private static final Logger log = Logger.getLogger(ResetPasswordResourceProviderFactory.class);
    private static List<String> realmNames = new ArrayList<>();
    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return realmNames.contains(session.getContext().getRealm().getName()) ? new ResetPasswordResourceProvider(session) : null ;
    }

    @Override
    public void init(Config.Scope config) {
        log.infof("%s initialized for realm: %s", AppConstraint.REST_PASS_ID,config.get("realms"));
        realmNames = Arrays.stream(config.get("realms").split(",")).collect(Collectors.toList());
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        //factory.getSpis().forEach(spi -> {log.infof(spi.getProviderFactoryClass().getName());});

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return AppConstraint.REST_PASS_ID;
    }
}
