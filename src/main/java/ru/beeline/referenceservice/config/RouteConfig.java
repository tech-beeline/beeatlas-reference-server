/*
 * Copyright (c) 2024 PJSC VimpelCom
 */
package ru.beeline.referenceservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app")
public class RouteConfig {
    private Map<String, String> routes;

    public Map<String, String> getRoutes() {
        return routes;
    }

    public void setRoutes(Map<String, String> routes) {
        this.routes = routes;
    }
}
