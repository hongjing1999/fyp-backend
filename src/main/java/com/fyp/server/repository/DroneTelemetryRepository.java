package com.fyp.server.repository;

import com.fyp.server.domain.DroneTelemetry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DroneTelemetryRepository extends JpaRepository<DroneTelemetry, Long>, JpaSpecificationExecutor<DroneTelemetry> {

}
