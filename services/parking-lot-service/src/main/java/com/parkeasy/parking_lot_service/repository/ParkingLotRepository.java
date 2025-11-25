package com.parkeasy.parking_lot_service.repository;

import com.parkeasy.parking_lot_service.model.ParkingLot;
import com.parkeasy.parking_lot_service.model.ParkingLotStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long>, ParkingLotRepositoryCustom {
    List<ParkingLot> findByOwnerIdAndStatus(Long userId, ParkingLotStatus parkingLotStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ParkingLot p WHERE p.id = :id")
    Optional<ParkingLot> findByIdWithLock(@Param("id") Long id);
}
