package com.parkeasy.parking_lot_service.repository;


import com.parkeasy.parking_lot_service.model.ParkingLot;
import java.math.BigDecimal;
import java.util.List;

public interface ParkingLotRepositoryCustom {

    List<ParkingLot> findNearby(
            Double latitude,
            Double longitude,
            Double radiusKm,
            BigDecimal maxPrice,
            Integer minSpots
    );
}
