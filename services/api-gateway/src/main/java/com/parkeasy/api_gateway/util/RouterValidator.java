package com.parkeasy.api_gateway.util;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
import org.springframework.util.AntPathMatcher;

@Component
public class RouterValidator {

    // Use Spring's AntPathMatcher for proper wildcard matching
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * List of API endpoints that DO NOT require authentication
     * Use Ant-style patterns: ** = any number of directories
     */
    public static final List<String> openApiEndpoints = List.of(
            "/api/v1/auth/**",

            // Parking Lot public APIs
            "/api/v1/parking-lots/search",

            // Public/health endpoints
            "/actuator/**",
            "/error",
            "/eureka/**"
    );

    /**
     * Check if the request requires authentication
     * Returns true if endpoint is SECURED (requires auth)
     * Returns false if endpoint is OPEN (no auth needed)
     */
    public Predicate<ServerHttpRequest> isSecured = request -> {
        String path = request.getURI().getPath();

        // Check if path matches any open endpoint pattern
        return openApiEndpoints.stream()
                .noneMatch(pattern -> pathMatcher.match(pattern, path));
    };
}
