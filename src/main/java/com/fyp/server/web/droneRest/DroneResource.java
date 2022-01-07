package com.fyp.server.web.droneRest;

import com.fyp.server.config.Constants;
import com.fyp.server.domain.Drone;
import com.fyp.server.domain.DroneUser;
import com.fyp.server.domain.User;
import com.fyp.server.droneService.DroneCommunicationService;
import com.fyp.server.repository.DroneRepository;
import com.fyp.server.repository.UserRepository;
import com.fyp.server.security.AuthoritiesConstants;
import com.fyp.server.security.SecurityUtils;
import com.fyp.server.service.MailService;
import com.fyp.server.service.UserService;
import com.fyp.server.service.dto.AdminUserDTO;
import com.fyp.server.service.dto.DroneDTO;
import com.fyp.server.service.dto.DroneTelemetryDTO;
import com.fyp.server.web.rest.errors.BadRequestAlertException;
import com.fyp.server.web.rest.errors.EmailAlreadyUsedException;
import com.fyp.server.web.rest.errors.LoginAlreadyUsedException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/droneApi/")
public class DroneResource {
	private final Logger log = LoggerFactory.getLogger(DroneResource.class);
   
	private final DroneRepository droneRepository;
	private final DroneCommunicationService droneCommunicationService;
    public DroneResource(DroneCommunicationService droneCommunicationService,
    		DroneRepository droneRepository) {
    	this.droneCommunicationService = droneCommunicationService;
    	this.droneRepository = droneRepository;
    }
    
    @PostMapping("/heartbeat")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DRONE + "\")")
    public ResponseEntity<?> heartbeatSignal(@Valid @RequestBody DroneTelemetryDTO droneTelemetryDTO){
        log.debug("Heartbeat REST request from drone: {}", droneTelemetryDTO.toString());
        Optional<String> userOptional = SecurityUtils.getCurrentUserLogin();
        if(userOptional.isPresent()) {
        	Optional<Drone> droneOptional = droneRepository.findOneByLogin(userOptional.get());
        	if(droneOptional.isPresent()) {
        		Drone drone = droneOptional.get();
        		droneTelemetryDTO.setDroneId(drone.getId());
        		droneCommunicationService.receiveHeartbeat(drone, droneTelemetryDTO);
        		return new ResponseEntity<>(HttpStatus.OK);
        	}
        	else {
        		return new ResponseEntity<>("Drone not found", HttpStatus.NOT_FOUND);
        	}
        }
        else {
        	return new ResponseEntity<>("Please login first", HttpStatus.UNAUTHORIZED);
        }
        
        
    }

 


    
}
