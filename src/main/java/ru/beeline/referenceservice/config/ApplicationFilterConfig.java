/*
 * Copyright (c) 2024 PJSC VimpelCom
 */
package ru.beeline.referenceservice.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.beeline.referenceservice.filter.AuthFilter;
import ru.beeline.referenceservice.filter.RoutingFilter;

@Configuration
public class ApplicationFilterConfig {

    @Bean
    public RoutingFilter routingFilter(RouteConfig routeConfig, RestTemplate restTemplate) {
        return new RoutingFilter(routeConfig, restTemplate);
    }

    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterRegistration(AuthFilter authFilter) {
        FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(authFilter);
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<RoutingFilter> routingFilterRegistration(RoutingFilter routingFilter) {
        FilterRegistrationBean<RoutingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(routingFilter);
        registrationBean.setOrder(2);
        return registrationBean;
    }
}
