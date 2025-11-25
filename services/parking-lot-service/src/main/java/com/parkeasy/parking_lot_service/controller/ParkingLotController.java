package com.parkeasy.parking_lot_service.controller;

import com.parkeasy.parking_lot_service.dto.*;
import com.parkeasy.parking_lot_service.service.ParkingLotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parking-lots")
@RequiredArgsConstructor
@Slf4j
public class ParkingLotController {

    private final ParkingLotService service;

    @PostMapping
    public ResponseEntity<ApiResponse<ParkingLotResponse>> createParkingLot(
            @Valid @RequestBody ParkingLotRequest request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole
    ){
        log.info("Creating Parking Lot for the Admin: {}", request.getName());
        ParkingLotResponse createdParkingLot = service.createParkingLot(request, userId, userRole);
        return ResponseEntity.ok(ApiResponse.success("Parking Lot Successfully created", createdParkingLot));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ParkingLotResponse>> getParkingLotById(@PathVariable Long id){
        log.info("Fetching parking lot by id: {}", id);
        ParkingLotResponse parkingLotResponse = service.getParkingLotById(id);
        return ResponseEntity.ok(ApiResponse.success("Fetched parking lot using this id", parkingLotResponse));
    }

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<ParkingLotResponse>> updateParkingLot(@RequestBody ParkingLotRequest request, @PathVariable
                                                                            Long id, @RequestHeader("X-User-Id") String userId){
        log.info("Updating parking lot by id: {}", id);
        ParkingLotResponse parkingLotResponse = service.updateParkingLot(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success("Updating parking lot using this id", parkingLotResponse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteParkingLot(@PathVariable Long id, @RequestHeader("X-User-Id") String userId){
        log.info("Deleting parking lot by id: {}", id);
        service.deleteParkingLot(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Deleting parking lot using this id", null));
    }

    @GetMapping("/my-parking-lots")
    public ResponseEntity<ApiResponse<List<ParkingLotResponse>>> getMyParkingLots(@RequestHeader("X-User-Id") Long userId){
        log.info("Fetching parking lots for user: {}", userId);

        List<ParkingLotResponse> response = service.getMyParkingLots(userId);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d parking lot(s)", response.size()),
                response
        ));
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<ParkingLotSearchResult>>> searchParkingLots(
            @Valid @RequestBody SearchParkingLotRequest request) {

        log.info("Search request: lat={}, lon={}, radius={}km",
                request.getLatitude(), request.getLongitude(), request.getRadiusKm());

        List<ParkingLotSearchResult> results = service.searchNearby(request);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d parking lot(s) within %.1fkm",
                        results.size(), request.getRadiusKm()),
                results
        ));
    }

    @PatchMapping("/{id}/spots")
    public ResponseEntity<ApiResponse<Void>> updateAvailableSpots(
            @PathVariable Long id,
            @Valid @RequestBody SpotsUpdateRequest request) {

        log.info("Updating available spots for parking lot {}: change={}", id, request.getChange());

        service.updateAvailableSpots(id, request.getChange());

        return ResponseEntity.ok(ApiResponse.success("Available spots updated successfully", null));
    }
}
