package com.parkeasy.parking_lot_service.mapper;

import com.parkeasy.parking_lot_service.dto.ParkingLotRequest;
import com.parkeasy.parking_lot_service.dto.ParkingLotResponse;
import com.parkeasy.parking_lot_service.model.ParkingLot;
import com.parkeasy.parking_lot_service.model.ParkingLotStatus;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

@Component
public class ParkingLotMapper {
    public static ParkingLotResponse toParkingLotResponse(ParkingLot request){
        return ParkingLotResponse.builder()
                .id(request.getId())
                .ownerId(request.getOwnerId())
                .name(request.getName())
                .address(request.getAddress())
                .availableSpots(request.getAvailableSpots())
                .totalSpots(request.getTotalSpots())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .pricePerHour(request.getPricePerHour())
                .images(request.getImages())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .build();
    }

    public ParkingLot toEntity(@Valid ParkingLotRequest request, String ownerId) {
        return ParkingLot.builder()
                .ownerId(Long.valueOf(ownerId))
                .name(request.getName())
                .status(ParkingLotStatus.ACTIVE)
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .availableSpots(request.getAvailableSpots())
                .totalSpots(request.getTotalSpots())
                .pricePerHour(request.getPricePerHour())
                .images(request.getImages())
                .build();
    }
}
