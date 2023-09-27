package org.keycube.checkpassport.util;

import io.quarkus.runtime.configuration.ConfigUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class Checks {

    public void checkProfile() {
        if (!ConfigUtils.getProfiles().contains("dev")) {
            throw new WebApplicationException("Only available for dev profile");
        }
    }
}
