package com.parkeasy.parking_lot_service.repository;

import com.parkeasy.parking_lot_service.model.ParkingLot;
import com.parkeasy.parking_lot_service.model.ParkingLotStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
@Slf4j
public class ParkingLotRepositoryImpl implements ParkingLotRepositoryCustom{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ParkingLot> findNearby(
            Double latitude,
            Double longitude,
            Double radiusKm,
            BigDecimal maxPrice,
            Integer minSpots) {

        log.debug("Searching parking lots near lat={}, lon={}, radius={}km", latitude, longitude, radiusKm);

        // Build SQL query with Haversine formula
        StringBuilder sql = getStringBuilder(maxPrice, minSpots);

        // Create and configure query
        Query query = entityManager.createNativeQuery(sql.toString(), ParkingLot.class);
        query.setParameter("lat", latitude);
        query.setParameter("lon", longitude);
        query.setParameter("radius", radiusKm);
        query.setParameter("status", ParkingLotStatus.ACTIVE.name());

        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice);
        }

        if (minSpots != null) {
            query.setParameter("minSpots", minSpots);
        }

        @SuppressWarnings("unchecked")
        List<ParkingLot> results = query.getResultList();

        log.debug("Found {} parking lots within {}km", results.size(), radiusKm);

        return results;
    }

    private static StringBuilder getStringBuilder(BigDecimal maxPrice, Integer minSpots) {
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT * FROM ( ");
        sql.append(" SELECT p.*, ");
        sql.append(" (6371 * acos( ");
        sql.append("   cos(radians(:lat)) * cos(radians(p.latitude)) * ");
        sql.append("   cos(radians(p.longitude) - radians(:lon)) + ");
        sql.append("   sin(radians(:lat)) * sin(radians(p.latitude)) ");
        sql.append(" )) AS distance ");
        sql.append(" FROM parking_lots p ");
        sql.append(" WHERE p.status = :status ");

        // Optional filters (these MUST stay inside subquery)
        if (maxPrice != null) {
            sql.append(" AND p.price_per_hour <= :maxPrice ");
        }
        if (minSpots != null) {
            sql.append(" AND p.available_spots >= :minSpots ");
        }

        sql.append(") AS t ");  // close subquery

        // Now we can safely use alias "t.distance"
        sql.append("WHERE t.distance < :radius ");
        sql.append("ORDER BY t.distance ASC");

        return sql;
    }
}
