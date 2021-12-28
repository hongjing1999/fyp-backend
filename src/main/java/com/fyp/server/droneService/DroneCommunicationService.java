package com.fyp.server.droneService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fyp.server.config.Constants;
import com.fyp.server.domain.Authority;
import com.fyp.server.domain.Drone;
import com.fyp.server.domain.DroneUser;
import com.fyp.server.domain.User;
import com.fyp.server.droneService.DroneService;
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
import com.fyp.utils.GoogleDriveAPIUtil;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import tech.jhipster.security.RandomUtil;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class DroneCommunicationService {

    private final Logger log = LoggerFactory.getLogger(DroneCommunicationService.class);
    
    private final DroneService droneService;
    
    private final DroneRepository droneRepository;
    

    public DroneCommunicationService(DroneRepository droneRepository, DroneService droneService) {
    	this.droneRepository = droneRepository;
    	this.droneService = droneService;
    }
    
    public Drone receiveHeartbeat(DroneDTO droneDTO) {
    	Optional<Drone> droneOptional = droneRepository.findOneByLogin(droneDTO.getLogin());
    	if(droneOptional.isPresent()) {
    		Drone drone = droneOptional.get();
    		drone.setLastHeartBeatTime(Instant.now());
    		//TODO set drone telemetry data
    		
    		droneRepository.save(drone);
    		return drone;
    	}
    	return null;
    	
    }
    
    

}
