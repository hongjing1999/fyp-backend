package com.fyp.server.repository;

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
public interface DroneUserRepository extends JpaRepository<DroneUser, Long> {
    Optional<DroneUser> findOneByActivationKey(String activationKey);

    List<DroneUser> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);

    Optional<DroneUser> findOneByResetKey(String resetKey);

    Optional<DroneUser> findOneByEmailIgnoreCase(String email);

    Optional<DroneUser> findOneByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    Optional<DroneUser> findOneWithAuthoritiesByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    Optional<DroneUser> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    Page<DroneUser> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);
}
