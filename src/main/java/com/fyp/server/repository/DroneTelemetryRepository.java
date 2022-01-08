package com.fyp.server.repository;

import com.fyp.server.domain.DroneTelemetry;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DroneTelemetryRepository extends JpaRepository<DroneTelemetry, Long>, JpaSpecificationExecutor<DroneTelemetry> {
	
	@Query(nativeQuery = true, value = "SELECT * FROM drone_telemetry u WHERE u.drone_id = :droneId ORDER BY u.created_date DESC LIMIT 1")
	Optional<DroneTelemetry> findFirstByDroneIdOrderByCreatedDateByDesc( @Param("droneId") Long droneId);
}
