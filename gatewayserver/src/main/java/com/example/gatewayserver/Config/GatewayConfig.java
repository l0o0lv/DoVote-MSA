package com.example.gatewayserver.Config;

import com.example.gatewayserver.Component.AuthorizationHeaderFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder,
                                      AuthorizationHeaderFilter filter){
        return builder.routes()
                .route("auth-server", r -> r.path("/auth/signup")
                        .uri("lb://auth-server"))
                .route("auth-server", r -> r.path("/auth/signin")
                        .uri("lb://auth-server"))
                .route("auth-server", r -> r.path("/auth/**")
                        .filters(f->f.filter(filter.apply(config -> config.setRequiredRole("ROLE_USER"))))
                        .uri("lb://auth-server"))


                .route("sms-server", r -> r.path("/sms/**") //sms-server에는 redis 설치 필요
                        .uri("lb://sms-server"))

                .route("message-server", r -> r.path("/message/**")
                        .filters(f->f.filter(filter.apply(config -> config.setRequiredRole("ROLE_USER"))))
                        .uri("lb://message-server"))

                .route("poll-server", r -> r.path("/polls/upload")
                        .filters(f->f.filter(filter.apply(config -> config.setRequiredRole("ROLE_USER"))))
                        .uri("lb://poll-server"))
                .route("poll-server", r -> r.path("/polls/close")
                        .filters(f->f.filter(filter.apply(config -> config.setRequiredRole("ROLE_USER"))))
                        .uri("lb://poll-server"))
                .route("poll-server", r -> r.path("/polls/**")
                        .uri("lb://poll-server"))
                .route("poll-server", r -> r.path("/choices/**")
                        .uri("lb://poll-server"))
                .route("poll-server", r -> r.path("/votes/ok/{nickname}")
                        .filters(f->f.filter(filter.apply(config -> config.setRequiredRole("ROLE_USER"))))
                        .uri("lb://poll-server"))

                .route("comment-server", r -> r.path("/comments/**")
                        .filters(f->f.filter(filter.apply(config -> config.setRequiredRole("ROLE_USER"))))
                        .uri("lb://comment-server"))

                .build();
    }
}
