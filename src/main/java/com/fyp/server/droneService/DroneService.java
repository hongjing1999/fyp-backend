package com.fyp.server.droneService;

import com.fyp.server.config.Constants;
import com.fyp.server.domain.Authority;
import com.fyp.server.domain.Drone;
import com.fyp.server.domain.DroneUser;
import com.fyp.server.domain.User;
import com.fyp.server.repository.AuthorityRepository;
import com.fyp.server.repository.DroneRepository;
import com.fyp.server.repository.DroneUserRepository;
import com.fyp.server.repository.UserRepository;
import com.fyp.server.security.AuthoritiesConstants;
import com.fyp.server.security.SecurityUtils;
import com.fyp.server.service.EmailAlreadyUsedException;
import com.fyp.server.service.InvalidPasswordException;
import com.fyp.server.service.UsernameAlreadyUsedException;
import com.fyp.server.service.dto.AdminUserDTO;
import com.fyp.server.service.dto.DroneDTO;
import com.fyp.server.service.dto.DroneUserDTO;
import com.fyp.server.service.dto.UserDTO;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.security.RandomUtil;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class DroneService {

    private final Logger log = LoggerFactory.getLogger(DroneService.class);

    private final DroneRepository droneRepository;
    
    private final DroneUserRepository droneUserRepository;

    private final PasswordEncoder passwordEncoder;
    
    private final AuthorityRepository authorityRepository;

    public DroneService(DroneRepository droneRepository, PasswordEncoder passwordEncoder, DroneUserRepository droneUserRepository, AuthorityRepository authorityRepository) {
        this.droneRepository = droneRepository;
        this.passwordEncoder = passwordEncoder;
        this.droneUserRepository = droneUserRepository;
        this.authorityRepository = authorityRepository;
    }


    public Optional<Drone> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return droneRepository
            .findOneByResetKey(key)
            .filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400)))
            .map(
                user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    user.setResetKey(null);
                    user.setResetDate(null);
                    return user;
                }
            );
    }

    public Optional<Drone> requestPasswordReset(String login, DroneUser droneUser) {

        return droneRepository
            .findOneByLoginAndDroneUserId(login, droneUser.getId())
            .map(
                user -> {
                    user.setResetKey(RandomUtil.generateResetKey());
                    user.setResetDate(Instant.now());
                    return user;
                }
            );
    }

    public Drone registerDrone(DroneDTO droneDTO, DroneUser droneUser, String password) {
    	droneRepository
            .findOneByLogin(droneDTO.getLogin().toLowerCase())
            .ifPresent(
                existingUser -> {
                	throw new UsernameAlreadyUsedException();
                }
            );
    	
    	Drone newDrone = new Drone();
        String encryptedPassword = passwordEncoder.encode(password);
        newDrone.setLogin(droneDTO.getLogin().toLowerCase());
        // new user gets initially a generated password
        newDrone.setPassword(encryptedPassword);
        newDrone.setName(droneDTO.getName());
//        newDrone.setIpAddress(droneDTO.getIpAddress());
        newDrone.setDroneUserId(droneUser.getId());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.DRONE).ifPresent(authorities::add);
        newDrone.setAuthorities(authorities);
        droneRepository.save(newDrone);
        log.debug("Created Information for Drone: {}", newDrone);
        return newDrone;
    }


//    public DroneUser createDrone(DroneUserDTO droneUserDTO) {
//    	DroneUser user = new DroneUser();
//        user.setLogin(droneUserDTO.getLogin().toLowerCase());
//        user.setFirstName(droneUserDTO.getFirstName());
//        user.setLastName(droneUserDTO.getLastName());
//        if (droneUserDTO.getEmail() != null) {
//            user.setEmail(droneUserDTO.getEmail().toLowerCase());
//        }
//        user.setImageUrl(droneUserDTO.getImageUrl());
//        if (droneUserDTO.getLangKey() == null) {
//            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
//        } else {
//            user.setLangKey(droneUserDTO.getLangKey());
//        }
//        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
//        user.setPassword(encryptedPassword);
//        user.setResetKey(RandomUtil.generateResetKey());
//        user.setResetDate(Instant.now());
//        user.setActivated(true);
//        
//        droneUserRepository.save(user);
//        log.debug("Created Information for User: {}", user);
//        return user;
//    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    public Optional<DroneDTO> updateDrone(DroneDTO droneDTO) {
        return Optional
            .of(droneRepository.findById(droneDTO.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(
                drone -> {
                	drone.setLogin(droneDTO.getLogin().toLowerCase());
                	drone.setName(droneDTO.getName());
                	Set<Authority> managedAuthorities = drone.getAuthorities();
                    managedAuthorities.clear();
                    droneDTO
                        .getAuthorities()
                        .stream()
                        .map(authorityRepository::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(managedAuthorities::add);
                    log.debug("Changed Information for Drone: {}", drone);
                    return drone;
                }
            )
            .map(DroneDTO::new);
    }

    public void deleteDrone(String login) {
    	droneRepository
            .findOneByLogin(login)
            .ifPresent(
                drone -> {
                	droneRepository.delete(drone);
                    log.debug("Deleted User: {}", drone);
                }
            );
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user.
     * @param lastName  last name of user.
     * @param email     email id of user.
     * @param langKey   language key.
     * @param imageUrl  image URL of user.
     */
//    public void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl) {
//        SecurityUtils
//            .getCurrentUserLogin()
//            .flatMap(droneUserRepository::findOneByLogin)
//            .ifPresent(
//                user -> {
//                    user.setFirstName(firstName);
//                    user.setLastName(lastName);
//                    if (email != null) {
//                        user.setEmail(email.toLowerCase());
//                    }
//                    user.setLangKey(langKey);
//                    user.setImageUrl(imageUrl);
//                    log.debug("Changed Information for User: {}", user);
//                }
//            );
//    }

    @Transactional
    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtils
            .getCurrentUserLogin()
            .flatMap(droneUserRepository::findOneByLogin)
            .ifPresent(
                user -> {
                    String currentEncryptedPassword = user.getPassword();
                    if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                        throw new InvalidPasswordException();
                    }
                    String encryptedPassword = passwordEncoder.encode(newPassword);
                    user.setPassword(encryptedPassword);
                    log.debug("Changed password for User: {}", user);
                }
            );
    }

    @Transactional(readOnly = true)
    public Page<DroneUserDTO> getAllManagedUsers(Pageable pageable) {
        return droneUserRepository.findAll(pageable).map(DroneUserDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<DroneUserDTO> getAllPublicUsers(Pageable pageable) {
        return droneUserRepository.findAllByIdNotNullAndActivatedIsTrue(pageable).map(DroneUserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<DroneUser> getUserWithAuthoritiesByLogin(String login) {
        return droneUserRepository.findOneWithAuthoritiesByLogin(login);
    }

    @Transactional(readOnly = true)
    public Optional<DroneUser> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(droneUserRepository::findOneWithAuthoritiesByLogin);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
    	droneUserRepository
            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
            .forEach(
                user -> {
                    log.debug("Deleting not activated user {}", user.getLogin());
                    droneUserRepository.delete(user);
                }
            );
    }

//    /**
//     * Gets a list of all the authorities.
//     * @return a list of all the authorities.
//     */
//    @Transactional(readOnly = true)
//    public List<String> getAuthorities() {
//        return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
//    }
}
