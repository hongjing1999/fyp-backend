package com.fyp.server.web.droneRest;

import com.fyp.server.config.Constants;
import com.fyp.server.domain.Drone;
import com.fyp.server.domain.User;
import com.fyp.server.droneService.DroneCommunicationService;
import com.fyp.server.repository.UserRepository;
import com.fyp.server.security.AuthoritiesConstants;
import com.fyp.server.service.MailService;
import com.fyp.server.service.UserService;
import com.fyp.server.service.dto.AdminUserDTO;
import com.fyp.server.service.dto.DroneDTO;
import com.fyp.server.web.rest.errors.BadRequestAlertException;
import com.fyp.server.web.rest.errors.EmailAlreadyUsedException;
import com.fyp.server.web.rest.errors.LoginAlreadyUsedException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Collections;
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
   
	private final DroneCommunicationService droneCommunicationService;
    public DroneResource(DroneCommunicationService droneCommunicationService) {
    	this.droneCommunicationService = droneCommunicationService;
    }
    
    @PostMapping("/heartbeat")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DRONE + "\")")
    public ResponseEntity<?> heartbeatSignal(@Valid @RequestBody DroneDTO droneDTO){
        log.debug("Heartbeat REST request from drone: {}", droneDTO.getIpAddress());
        
        Drone drone = droneCommunicationService.receiveHeartbeat(droneDTO);
        
        if(drone != null) {
        	return new ResponseEntity<>(droneDTO, HttpStatus.OK);
        }
        else {
        	return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

 


    
}
