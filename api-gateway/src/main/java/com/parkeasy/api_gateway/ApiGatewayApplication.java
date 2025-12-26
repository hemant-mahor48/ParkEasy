package com.parkeasy.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

    /**
     * Configure routes dynamically via discovery
     * This will automatically discover services from Eureka
     */
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // User Service Routes
                .route("user-service", r -> r
                        .path("/api/v1/users/**", "/api/v1/auth/**")
                        .filters(f -> f
                                .rewritePath("/api/v1/(?<segment>.*)", "/${segment}")
                                .addRequestHeader("X-Gateway-Id", "api-gateway")
                        )
                        .uri("lb://user-service")
                )

                // Parking Lot Service Routes
                .route("parking-lot-service", r -> r
                        .path("/api/v1/parking-lots/**")
                        .filters(f -> f
                                .rewritePath("/api/v1/(?<segment>.*)", "/${segment}")
                                .addRequestHeader("X-Gateway-Id", "api-gateway")
                        )
                        .uri("lb://parking-lot-service")
                )

                // Booking Service Routes
                .route("booking-service", r -> r
                        .path("/api/v1/bookings/**")
                        .filters(f -> f
                                .rewritePath("/api/v1/(?<segment>.*)", "/${segment}")
                                .addRequestHeader("X-Gateway-Id", "api-gateway")
                        )
                        .uri("lb://booking-service")
                )

                // Payment Service Routes
                .route("payment-service", r -> r
                        .path("/api/v1/payments/**")
                        .filters(f -> f
                                .rewritePath("/api/v1/(?<segment>.*)", "/${segment}")
                                .addRequestHeader("X-Gateway-Id", "api-gateway")
                        )
                        .uri("lb://payment-service")
                )

                // Health and Info endpoints (public, no auth required)
                .route("health-check", r -> r
                        .path("/health", "/health/**")
                        .filters(f -> f.setStatus(200))
                        .uri("http://localhost:8080")
                )

                .build();
    }
}

