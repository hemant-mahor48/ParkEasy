package com.parkeasy.parking_lot_service.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Autowired
    private HttpServletRequest request;

    @Override
    public void apply(RequestTemplate template) {
        String authHeader = request.getHeader("Authorization");
        String userId = request.getHeader("X-User-Id");
        String role = request.getHeader("X-User-Role");
        String email = request.getHeader("X-User-Email");

        if (authHeader != null) {
            template.header("Authorization", authHeader);
        }
        if (userId != null)
            template.header("X-User-Id", userId);
        if (role != null)
            template.header("X-User-Role", role);
        if (email != null)
            template.header("X-User-Email", email);
    }
}

