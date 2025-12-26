package com.parkeasy.api_gateway.filter;

import com.parkeasy.api_gateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthenticationGatewayFilter extends AbstractGatewayFilterFactory<JwtAuthenticationGatewayFilter.Config> {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationGatewayFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();

            // Skip authentication for public endpoints
            if (path.startsWith("/health") ||
                    path.startsWith("/api/v1/auth") ||
                    path.startsWith("/actuator")) {
                return chain.filter(exchange);
            }

            String token = extractTokenFromRequest(exchange);

            if (token == null) {
                return onError(exchange, "Missing or invalid authorization header");
            }

            if (!jwtUtil.validateToken(token)) {
                return onError(exchange, "Invalid or expired JWT token");
            }

            // Extract user information from token
            String username = jwtUtil.extractUsername(token);
            Long userId = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractRole(token);

            // Add user information to request headers for downstream services
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(r -> r
                            .header("X-User-Id", String.valueOf(userId))
                            .header("X-User-Name", username)
                            .header("X-User-Role", role != null ? role : "USER")
                            .header("X-Original-URL", exchange.getRequest().getURI().toString())
                    )
                    .build();

            return chain.filter(modifiedExchange);
        };
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String extractTokenFromRequest(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }

    /**
     * Handle authentication errors
     */
    private Mono<Void> onError(
            ServerWebExchange exchange,
            String error
    ) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorResponse = String.format(
                "{\"error\":\"%s\",\"status\":%d}",
                error,
                HttpStatus.UNAUTHORIZED.value()
        );

        return exchange.getResponse()
                .writeWith(
                        Mono.just(
                                exchange.getResponse()
                                        .bufferFactory()
                                        .wrap(errorResponse.getBytes(StandardCharsets.UTF_8))
                        )
                );
    }

    public static class Config {
        // Filter configuration class
    }
}
