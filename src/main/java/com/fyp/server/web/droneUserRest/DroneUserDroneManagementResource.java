package com.fyp.server.web.droneUserRest;

import com.fyp.server.config.Constants;
import com.fyp.server.domain.Drone;
import com.fyp.server.domain.DroneUser;
import com.fyp.server.domain.User;
import com.fyp.server.droneUserService.DroneUserDroneService;
import com.fyp.server.droneUserService.DroneUserService;
import com.fyp.server.repository.DroneUserRepository;
import com.fyp.server.repository.UserRepository;
import com.fyp.server.security.AuthoritiesConstants;
import com.fyp.server.security.SecurityUtils;
import com.fyp.server.service.MailService;
import com.fyp.server.service.UserService;
import com.fyp.server.service.dto.AdminUserDTO;
import com.fyp.server.service.dto.DroneDTO;
import com.fyp.server.service.dto.DroneUserDTO;
import com.fyp.server.web.rest.errors.BadRequestAlertException;
import com.fyp.server.web.rest.errors.EmailAlreadyUsedException;
import com.fyp.server.web.rest.errors.LoginAlreadyUsedException;
import com.fyp.utils.GoogleDriveAPIUtil;

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
@RequestMapping("/droneUserApi")
public class DroneUserDroneManagementResource {


    private final Logger log = LoggerFactory.getLogger(DroneUserDroneManagementResource.class);
    
    private final DroneUserDroneService droneUserDroneService;
    
    private final DroneUserRepository droneUserRepository;
    

    @Value("${jhipster.clientApp.name}")
    private String applicationName;


    public DroneUserDroneManagementResource(DroneUserDroneService droneUserDroneService, DroneUserRepository droneUserRepository) {
    	this.droneUserDroneService = droneUserDroneService;
    	this.droneUserRepository = droneUserRepository;
    }

    @PostMapping("/create-drone")
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

  
}
