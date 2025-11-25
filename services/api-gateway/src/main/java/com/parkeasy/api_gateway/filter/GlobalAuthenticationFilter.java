package com.parkeasy.api_gateway.filter;

import com.parkeasy.api_gateway.util.JwtUtil;
import com.parkeasy.api_gateway.util.RouterValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class GlobalAuthenticationFilter implements GlobalFilter, Ordered {

    private final RouterValidator routerValidator;
    private final JwtUtil jwtUtil;

    @Override
    public int getOrder() {
        return -1; // High priority
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        log.info("Processing request: {} {}", request.getMethod(), request.getURI().getPath());

        // Check if this route requires authentication
        if (routerValidator.isSecured.test(request)) {

            // Check if Authorization header exists
            if (isAuthMissing(request)) {
                log.error("Authorization header missing for secured endpoint: {}", request.getURI().getPath());
                return onError(exchange, "Authorization header is missing");
            }

            // Extract token
            final String token = getAuthHeader(request);
            if (token == null) {
                log.error("Could not extract Bearer token from Authorization header");
                return onError(exchange, "Invalid authorization format. Use: Bearer <token>");
            }

            // Validate token
            if (!jwtUtil.isValid(token)) {
                log.error("Invalid or expired token for request: {}", request.getURI().getPath());
                return onError(exchange, "Invalid or expired token");
            }

            // Extract user info and add to headers
            try {
                exchange = populateRequestWithHeaders(exchange, token);
                log.info("Successfully authenticated user and added headers");
            } catch (Exception e) {
                log.error("Failed to extract user information from token: {}", e.getMessage());
                return onError(exchange, "Invalid token claims");
            }
        } else {
            log.debug("Request to public endpoint: {}", request.getURI().getPath());
        }

        // Continue with the filter chain
        return chain.filter(exchange);
    }

    /**
     * Extract and populate user information from JWT token into request headers
     */
    private ServerWebExchange populateRequestWithHeaders(ServerWebExchange exchange, String token) {
        String email = jwtUtil.getEmailFromToken(token);
        Long userId = jwtUtil.getUserIdFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);

        log.debug("Extracted user info - Email: {}, UserId: {}, Role: {}", email, userId, role);

        // Create mutated request with user headers
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("X-User-Email", email)
                .header("X-User-Id", String.valueOf(userId))
                .header("X-User-Role", role)
                .build();

        // Return mutated exchange with new request
        return exchange.mutate().request(mutatedRequest).build();
    }

    /**
     * Check if Authorization header is present
     */
    private boolean isAuthMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String getAuthHeader(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * Return error response with proper JSON format
     */
    private Mono<Void> onError(ServerWebExchange exchange, String errorMessage) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");

        String errorBody = String.format(
                "{\"success\": false, \"message\": \"%s\", \"timestamp\": \"%s\"}",
                errorMessage,
                java.time.LocalDateTime.now().toString()
        );

        log.error("Returning 401 Unauthorized: {}", errorMessage);

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(errorBody.getBytes()))
        );
    }
}