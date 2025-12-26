package com.parkeasy.api_gateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("Gateway error occurred: ", ex);

        HttpStatus httpStatus;
        String errorMessage;

        if (ex instanceof NotFoundException) {
            httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
            errorMessage = "Service not available";
        } else if (ex instanceof IllegalArgumentException) {
            httpStatus = HttpStatus.BAD_REQUEST;
            errorMessage = ex.getMessage();
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            errorMessage = "An unexpected error occurred";
        }

        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorResponse = buildErrorResponse(httpStatus, errorMessage);
        byte[] bytes = errorResponse.getBytes(StandardCharsets.UTF_8);

        return exchange.getResponse()
                .writeWith(
                        Mono.just(
                                exchange.getResponse()
                                        .bufferFactory()
                                        .wrap(bytes)
                        )
                );
    }

    private String buildErrorResponse(HttpStatus status, String message) {
        return String.format(
                "{\"error\":\"%s\",\"status\":%d,\"message\":\"%s\"}",
                status.getReasonPhrase(),
                status.value(),
                message
        );
    }
}
