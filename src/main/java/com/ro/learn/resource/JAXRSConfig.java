package com.ro.learn.resource;

import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;

public class JAXRSConfig extends ResourceConfig {

    public JAXRSConfig() {
        register(RestResource.class);
        register(JacksonJaxbJsonProvider.class);
    }
}
