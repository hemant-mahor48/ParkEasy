package com.parkeasy.parking_lot_service.service;

import com.parkeasy.parking_lot_service.dto.*;
import com.parkeasy.parking_lot_service.exceptions.InvalidOwnerException;
import com.parkeasy.parking_lot_service.exceptions.ParkingLotNotFoundException;
import com.parkeasy.parking_lot_service.exceptions.UnauthorizedAccessException;
import com.parkeasy.parking_lot_service.exceptions.UserServiceUnavailableException;
import com.parkeasy.parking_lot_service.feignClients.UserServiceClient;
import com.parkeasy.parking_lot_service.mapper.ParkingLotMapper;
import com.parkeasy.parking_lot_service.model.ParkingLot;
import com.parkeasy.parking_lot_service.model.ParkingLotStatus;
import com.parkeasy.parking_lot_service.repository.ParkingLotRepository;
import com.parkeasy.parking_lot_service.util.LocationUtils;
import feign.FeignException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;
    private final UserServiceClient userServiceClient;
    private final ParkingLotMapper mapper;

    @Transactional
    public ParkingLotResponse createParkingLot(@Valid ParkingLotRequest request, String userId, String userRole) {
        // Verify user is OWNER
        if (!"ROLE_OWNER".equals(userRole)) {
            log.error("User {} is not an OWNER. Role: {}", userId, userRole);
            throw new InvalidOwnerException("Only users with OWNER role can create parking lots");
        }

        // Verify user exists
        try {
            Optional<ApiResponse<UserResponse>> user = userServiceClient.getUserByUserId(Long.valueOf(userId));
            log.info("Owner verified: {} ({})", user.get().getData().getId(), user.get().getData().getRole());
        } catch (FeignException.NotFound e) {
            log.error("User not found: {}", userId);
            throw new UserServiceUnavailableException("User not found");
        } catch (FeignException e) {
            log.error("Failed to verify user: {}", userId, e);
            throw new UserServiceUnavailableException("User service is unavailable");
        }

        // Create parking lot
        ParkingLot parkingLot = mapper.toEntity(request, userId);

        ParkingLot saved = parkingLotRepository.save(parkingLot);
        log.info("Parking lot created successfully with ID: {}", saved.getId());

        return ParkingLotMapper.toParkingLotResponse(saved);
    }

    @Cacheable(value = "parking-lots", key = "#id")
    @Transactional(readOnly = true)
    public ParkingLotResponse getParkingLotById(Long id) {
        log.info("Fetching parking lot details for ID: {}", id);

        // Find parking lot
        ParkingLot parkingLot = parkingLotRepository.findById(id)
                .orElseThrow(() -> new ParkingLotNotFoundException("Parking lot not found with ID: " + id));

        log.info("Parking lot details fetched successfully for ID: {}", id);

        return ParkingLotMapper.toParkingLotResponse(parkingLot);
    }

    @CachePut(value = "parking-lots", key = "#id")
    @Transactional
    public ParkingLotResponse updateParkingLot(Long id, ParkingLotRequest request, String userId) {
        log.info("Updating parking lot {} by user {}", id, userId);

        // Find parking lot
        ParkingLot parkingLot = parkingLotRepository.findById(id)
                .orElseThrow(() -> new ParkingLotNotFoundException("Parking lot not found with ID: " + id));

        // Verify ownership
        if (!parkingLot.getOwnerId().equals(Long.valueOf(userId))) {
            log.error("User {} is not the owner of parking lot {}", userId, id);
            throw new UnauthorizedAccessException("You are not authorized to update this parking lot");
        }

        // Update fields (only non-null values)
        if (request.getName() != null) {
            parkingLot.setName(request.getName());
        }
        if (request.getAddress() != null) {
            parkingLot.setAddress(request.getAddress());
        }
        if (request.getPricePerHour() != null) {
            parkingLot.setPricePerHour(request.getPricePerHour());
        }
        if (request.getImages() != null) {
            parkingLot.setImages(Collections.singletonList(String.join(",", request.getImages())));
        }
        if (request.getAvailableSpots() != null) {
            parkingLot.setAvailableSpots(request.getAvailableSpots());
        }
        if (request.getTotalSpots() != null) {
            parkingLot.setTotalSpots(request.getTotalSpots());
        }
        if (request.getLatitude() != null) {
            parkingLot.setLatitude(request.getLatitude());
        }

        ParkingLot updated = parkingLotRepository.save(parkingLot);
        log.info("Parking lot {} updated successfully", id);

        return ParkingLotMapper.toParkingLotResponse(updated);
    }

    @CacheEvict(value = "parking-lots", key = "#id")
    @Transactional
    public void deleteParkingLot(Long id, String userId) {
        log.info("Deleting parking lot {} by user {}", id, userId);

        // Find parking lot
        ParkingLot parkingLot = parkingLotRepository.findById(id)
                .orElseThrow(() -> new ParkingLotNotFoundException("Parking lot not found with ID: " + id));

        // Verify ownership
        if (!parkingLot.getOwnerId().equals(Long.valueOf(userId))) {
            log.error("User {} is not the owner of parking lot {}", userId, id);
            throw new UnauthorizedAccessException("You are not authorized to delete this parking lot");
        }

        // Soft delete
        parkingLot.setStatus(ParkingLotStatus.INACTIVE);
        parkingLotRepository.save(parkingLot);

        log.info("Parking lot {} deleted (soft) successfully", id);
    }

    @Transactional(readOnly = true)
    public List<ParkingLotResponse> getMyParkingLots(Long userId) {
        log.info("Fetching parking lots for owner: {}", userId);

        List<ParkingLot> parkingLots = parkingLotRepository.findByOwnerIdAndStatus(userId,ParkingLotStatus.ACTIVE);

        log.info("Found {} active parking lots for user {}", parkingLots.size(), userId);

        return parkingLots.stream()
                .map(ParkingLotMapper::toParkingLotResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ParkingLotSearchResult> searchNearby(SearchParkingLotRequest request) {
        log.info("Searching parking lots near ({}, {}) within {}km",
                request.getLatitude(), request.getLongitude(), request.getRadiusKm());

        // Search using custom repository method
        List<ParkingLot> parkingLots = parkingLotRepository.findNearby(
                request.getLatitude(),
                request.getLongitude(),
                request.getRadiusKm(),
                request.getMaxPrice(),
                request.getMinSpots()
        );

        log.info("Found {} parking lots within search criteria", parkingLots.size());

        // Map to search result DTOs
        return parkingLots.stream()
                .map(lot -> {
                    // Calculate distance
                    double distance = LocationUtils.calculateDistance(
                            request.getLatitude(),
                            request.getLongitude(),
                            lot.getLatitude(),
                            lot.getLongitude()
                    );

                    // Get first image if available
                    String mainImage = (lot.getImages() != null && !lot.getImages().isEmpty())
                            ? lot.getImages().get(0)
                            : null;

                    return ParkingLotSearchResult.builder()
                            .id(lot.getId())
                            .name(lot.getName())
                            .address(lot.getAddress())
                            .latitude(lot.getLatitude())
                            .longitude(lot.getLongitude())
                            .availableSpots(lot.getAvailableSpots())
                            .pricePerHour(lot.getPricePerHour())
                            .distanceKm(distance)
                            .mainImage(mainImage)
                            .status(lot.getStatus().name())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @CachePut(value = "parking-lots", key = "#id")
    @Transactional
    public void updateAvailableSpots(Long id, Integer change) {
        log.info("Updating available spots for parking lot {}: change={}", id, change);

        // Find with pessimistic lock to prevent race conditions
        ParkingLot parkingLot = parkingLotRepository.findByIdWithLock(id)
                .orElseThrow(() -> new ParkingLotNotFoundException("Parking lot not found with ID: " + id));

        int newSpots = parkingLot.getAvailableSpots() + change;

        // Validate constraints
        if (newSpots < 0) {
            throw new IllegalArgumentException("Available spots cannot be negative");
        }
        if (newSpots > parkingLot.getTotalSpots()) {
            throw new IllegalArgumentException("Available spots cannot exceed total spots");
        }

        parkingLot.setAvailableSpots(newSpots);

        // Update status based on availability
        if (newSpots == 0) {
            parkingLot.setStatus(ParkingLotStatus.FULL);
            log.info("Parking lot {} is now FULL", id);
        } else if (parkingLot.getStatus() == ParkingLotStatus.FULL) {
            parkingLot.setStatus(ParkingLotStatus.ACTIVE);
            log.info("Parking lot {} is now ACTIVE (was FULL)", id);
        }

        parkingLotRepository.save(parkingLot);
        log.info("Available spots updated for parking lot {}: {}/{}",
                id, newSpots, parkingLot.getTotalSpots());
    }
}
