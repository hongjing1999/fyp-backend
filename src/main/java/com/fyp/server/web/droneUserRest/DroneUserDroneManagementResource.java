package com.fyp.server.web.droneUserRest;

import com.fyp.server.config.Constants;
import com.fyp.server.domain.Drone;
import com.fyp.server.domain.DroneUser;
import com.fyp.server.domain.User;
import com.fyp.server.droneUserService.DroneCriteria;
import com.fyp.server.droneUserService.DroneUserDroneService;
import com.fyp.server.droneUserService.DroneUserService;
import com.fyp.server.repository.DroneRepository;
import com.fyp.server.repository.DroneUserRepository;
import com.fyp.server.repository.UserRepository;
import com.fyp.server.security.AuthoritiesConstants;
import com.fyp.server.security.SecurityUtils;
import com.fyp.server.service.MailService;
import com.fyp.server.service.UserService;
import com.fyp.server.service.dto.AdminUserDTO;
import com.fyp.server.service.dto.DroneDTO;
import com.fyp.server.service.dto.DroneTelemetryDTO;
import com.fyp.server.service.dto.DroneTelemetryGraphDTO;
import com.fyp.server.service.dto.DroneUserDTO;
import com.fyp.server.web.rest.errors.BadRequestAlertException;
import com.fyp.server.web.rest.errors.EmailAlreadyUsedException;
import com.fyp.server.web.rest.errors.LoginAlreadyUsedException;
import com.fyp.utils.GoogleDriveAPIUtil;
import com.fyp.utils.PaginationUtil;

import reactor.core.publisher.Flux;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;


import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;



@RestController
@RequestMapping("/droneUserApi")
public class DroneUserDroneManagementResource {


    private final Logger log = LoggerFactory.getLogger(DroneUserDroneManagementResource.class);
    
    @Autowired
    SimpMessagingTemplate template;
    
    private final DroneUserDroneService droneUserDroneService;
    
    private final DroneUserRepository droneUserRepository;
    
    private final DroneRepository droneRepository;
    

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    

    public DroneUserDroneManagementResource(DroneUserDroneService droneUserDroneService, DroneUserRepository droneUserRepository,
    		DroneRepository droneRepository) {
    	this.droneUserDroneService = droneUserDroneService;
    	this.droneUserRepository = droneUserRepository;
    	this.droneRepository = droneRepository;
    }

    @PostMapping("/drone")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DRONEUSER + "\")")
    public void createDrone(@Valid @RequestBody DroneDTO droneDTO){
        log.debug("REST request to create drone : {}", droneDTO);
        Optional<String> userOptional = SecurityUtils.getCurrentUserLogin();
        if(userOptional.isPresent()) {
        	log.debug("Login: {}", userOptional.get());
        	Optional<DroneUser> droneUserOptional = droneUserRepository.findOneByLogin(userOptional.get());
        	if(droneUserOptional.isPresent()) {
        		Drone result = droneUserDroneService.createDrone(droneUserOptional.get(), droneDTO);
        		
        		
        		log.debug("--------------Result: {}", result.getName());

        	}
        	
        }
    }
    
    @GetMapping("drone")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DRONEUSER + "\")")
    public ResponseEntity<?> getDrone(DroneCriteria droneCriteria, Pageable pageable) {
    	Optional<String> userOptional = SecurityUtils.getCurrentUserLogin();
        if(userOptional.isPresent()) {
        	log.debug("Login: {}", userOptional.get());
        	Optional<DroneUser> droneUserOptional = droneUserRepository.findOneByLogin(userOptional.get());
        	if(droneUserOptional.isPresent()) {
        		DroneUser droneUser = droneUserOptional.get();
        		Page<DroneDTO> page = droneUserDroneService.getDrone(droneUser, pageable);
            			
        		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/droneUserApi/drone");
        		
        		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);

        	}
        	else {
        		throw new BadRequestAlertException("DRONE_USER_NOT_EXIST", null, null);
        	}
        }
        else {
        	 throw new BadRequestAlertException("UNAUTHORIZED", null, null);
        }
    	
    }
    @DeleteMapping("drone/{droneId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DRONEUSER + "\")")
    public ResponseEntity<?> deleteDrone(@PathVariable Long droneId){
    	Optional<String> userOptional = SecurityUtils.getCurrentUserLogin();
        if(userOptional.isPresent()) {
        	log.debug("Login: {}", userOptional.get());
        	Optional<DroneUser> droneUserOptional = droneUserRepository.findOneByLogin(userOptional.get());
        	if(droneUserOptional.isPresent()) {
        		DroneUser droneUser = droneUserOptional.get();
        		Optional<Drone> droneOptional = droneRepository.findById(droneId);
        		if(droneOptional.isPresent()) {
        			Drone drone = droneOptional.get();
        			if(drone.getDroneUserId().equals(droneUser.getId())) {
        				droneUserDroneService.deleteDrone(droneId);
        				return new ResponseEntity<>( HttpStatus.ACCEPTED);
        			}
        			else {
        				throw new BadRequestAlertException("UNAUTHORIZED", null, null);
        			}
        			
        		}
        		else {
        			throw new BadRequestAlertException("DRONE_NOT_EXIST", null, null);
        		}


        	}
        	else {
        		throw new BadRequestAlertException("DRONE_USER_NOT_EXIST", null, null);
        	}
        }
        else {
        	 throw new BadRequestAlertException("UNAUTHORIZED", null, null);
        }
    }

    @GetMapping("droneTelemetry/{droneId}")
    public ResponseEntity<?> getDroneTelemetry(DroneCriteria droneCriteria, @PathVariable Long droneId, @RequestParam("range") String range) {
    	Optional<String> userOptional = SecurityUtils.getCurrentUserLogin();
        if(userOptional.isPresent()) {
        	log.debug("Login: {}", userOptional.get());
        	Optional<DroneUser> droneUserOptional = droneUserRepository.findOneByLogin(userOptional.get());
        	if(droneUserOptional.isPresent()) {
        		DroneUser droneUser = droneUserOptional.get();
        		DroneTelemetryGraphDTO graph = droneUserDroneService.getDroneTelemetry(droneId, range);
            			

        		return new ResponseEntity<>(graph, HttpStatus.OK);

        	}
        	else {
        		throw new BadRequestAlertException("DRONE_USER_NOT_EXIST", null, null);
        	}
        }
        else {
        	 throw new BadRequestAlertException("UNAUTHORIZED", null, null);
        }
    	
    }
    


    
    
    
    



  
}
