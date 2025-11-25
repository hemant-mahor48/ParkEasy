package com.parkeasy.parking_lot_service.feignClients;

import com.parkeasy.parking_lot_service.dto.ApiResponse;
import com.parkeasy.parking_lot_service.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "user-service", url = "${application.config.user-service-url}")
public interface UserServiceClient {

    @GetMapping("/id/{userId}")
    Optional<ApiResponse<UserResponse>> getUserByUserId(@PathVariable Long userId);

    @GetMapping("/email/{email}")
    Optional<ApiResponse<UserResponse>> getUserByEmail(@PathVariable Long email);
}
