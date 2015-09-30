package com.eavictor.restful.settings;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api")
public class RESTServiceConfig extends ResourceConfig {
	public RESTServiceConfig() {
		packages("com.eavictor.restful.api");
	}
}
