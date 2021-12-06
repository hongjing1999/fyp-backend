package com.fyp.server.repository;

import com.fyp.server.domain.Drone;
import com.fyp.server.domain.DroneUser;
import com.fyp.server.domain.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DroneRepository extends JpaRepository<Drone, Long> {
    Optional<Drone> findOneByLogin(String login);
    
    Optional<Drone> findOneByResetKey(String resetKey);
    
    Optional<Drone> findOneByLoginAndDroneUserId(String login, long droneUserId);
    
    @EntityGraph(attributePaths = "authorities")
    Optional<Drone> findOneWithAuthoritiesByLogin(String login);
}
