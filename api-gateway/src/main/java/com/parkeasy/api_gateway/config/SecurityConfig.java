package com.parkeasy.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Configure security filter chain for reactive (WebFlux) environment
     * Modern Spring Security 6.1+ API (without deprecated methods)
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        // Public endpoints (no authentication required)
                        .pathMatchers("/health").permitAll()
                        .pathMatchers("/health/**").permitAll()
                        .pathMatchers("/api/v1/auth/**").permitAll()
                        .pathMatchers("/api/v1/users/validate-token").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        // All other endpoints require authentication
                        .anyExchange().authenticated()
                )
                .httpBasic(basic -> {});

        return http.build();
    }

    /**
     * CORS configuration for reactive applications
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Allow all origins (customize for production)
        corsConfig.setAllowedOriginPatterns(Collections.singletonList("*"));

        // Allow common HTTP methods
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Allow common headers
        corsConfig.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-User-Id",
                "X-User-Name",
                "X-User-Role",
                "X-Gateway-Id",
                "Accept",
                "Origin"
        ));

        // Allow credentials
        corsConfig.setAllowCredentials(true);

        // Cache CORS for 1 hour
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }
}
