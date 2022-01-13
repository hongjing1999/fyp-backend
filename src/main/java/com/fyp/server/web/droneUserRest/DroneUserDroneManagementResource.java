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
import com.fyp.server.service.dto.DroneRequestDTO;
import com.fyp.server.service.dto.DroneTelemetryDTO;
import com.fyp.server.service.dto.DroneTelemetryGraphDTO;
import com.fyp.server.service.dto.DroneUserDTO;
import com.fyp.server.web.rest.errors.BadRequestAlertException;
import com.fyp.server.web.rest.errors.EmailAlreadyUsedException;
import com.fyp.server.web.rest.errors.LoginAlreadyUsedException;
import com.fyp.server.web.rest.vm.LoginVM;
import com.fyp.utils.GoogleDriveAPIUtil;
import com.fyp.utils.PaginationUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;


import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
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
    public ResponseEntity<?> createDrone(@Valid @RequestBody DroneDTO droneDTO){
        log.debug("REST request to create drone : {}", droneDTO);
        Optional<String> userOptional = SecurityUtils.getCurrentUserLogin();
        if(userOptional.isPresent()) {
        	log.debug("Login: {}", userOptional.get());
        	Optional<DroneUser> droneUserOptional = droneUserRepository.findOneByLogin(userOptional.get());
        	if(droneUserOptional.isPresent()) {
        		Drone result = droneUserDroneService.createDrone(droneUserOptional.get(), droneDTO);
        		
        		return new ResponseEntity<>(result, HttpStatus.OK);

        	}
        	else {
        		throw new BadRequestAlertException("INVALID_DRONE", null, null);
        	}
        	
        }
        else {
        	throw new BadRequestAlertException("UNAUTHORIZED", null, null);
        }
    }
    
    @PutMapping("/drone")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DRONEUSER + "\")")
    public ResponseEntity<?> editDrone(@Valid @RequestBody DroneDTO droneDTO){
        log.debug("REST request to create drone : {}", droneDTO);
        Optional<String> userOptional = SecurityUtils.getCurrentUserLogin();
        if(userOptional.isPresent()) {
        	log.debug("Login: {}", userOptional.get());
        	Optional<DroneUser> droneUserOptional = droneUserRepository.findOneByLogin(userOptional.get());
        	if(droneUserOptional.isPresent()) {
        		Drone result = droneUserDroneService.editDrone(droneUserOptional.get(), droneDTO);
        		
        		return new ResponseEntity<>(result, HttpStatus.OK);

        	}
        	else {
        		throw new BadRequestAlertException("INVALID_DRONE_USER", null, null);
        	}
        }
        else {
        	throw new BadRequestAlertException("UNAUTHORIZED", null, null);
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
    
    @GetMapping("drone/{droneId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DRONEUSER + "\")")
    public ResponseEntity<?> getDrone(@PathVariable Long droneId) {
    	DroneDTO droneDTO = droneUserDroneService.getDrone(droneId);            			
		
		return new ResponseEntity<>(droneDTO, HttpStatus.OK);
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
        				droneUserDroneService.deleteDrone(drone);
        				return new ResponseEntity<>( HttpStatus.ACCEPTED);
        			}
        			else {
        				throw new BadRequestAlertException("UNAUTHORIZED_USER", null, null);
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
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DRONEUSER + "\")")
    public ResponseEntity<?> getDroneTelemetry(DroneCriteria droneCriteria, @PathVariable Long droneId, @RequestParam("range") String range) {
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
        				DroneTelemetryGraphDTO graph = droneUserDroneService.getDroneTelemetry(droneId, range);
            			

                		return new ResponseEntity<>(graph, HttpStatus.OK);
        			}
        			else {
        				throw new BadRequestAlertException("UNAUTHORIZED_USER", null, null);
        			}
        		}
        		else {
        			throw new BadRequestAlertException("INVALID_DRONE", null, null);
        		}

        	}
        	else {
        		throw new BadRequestAlertException("INVALID_DRONE_USER", null, null);
        	}
        }
        else {
        	 throw new BadRequestAlertException("UNAUTHORIZED", null, null);
        }
    	
    }
    
    
    @RequestMapping("/{droneId}/stream.mjpg")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DRONEUSER + "\")")
    public void getVideoStream(HttpServletResponse response, @PathVariable Long droneId) {
        RestTemplate restTemplate = new RestTemplate();
        Optional<String> loginOptional = SecurityUtils.getCurrentUserLogin();
        if(loginOptional.isPresent()) {
        	Optional<DroneUser> droneUserOptional = droneUserRepository.findOneByLogin(loginOptional.get());
        	if(droneUserOptional.isPresent()) {
        		DroneUser droneUser = droneUserOptional.get();
        		
        		Optional<Drone> droneOptional = droneRepository.findById(droneId);
                if(droneOptional.isPresent()) {
                	Drone drone = droneOptional.get();
                	if(drone.getDroneUserId().equals(droneUser.getId())) {
                		if(drone.getIpAddress() != null) {
                			restTemplate.execute(
                                    URI.create("http://" +  drone.getIpAddress() +":8000/stream.mjpg"),
                                    HttpMethod.GET,
                                    (ClientHttpRequest request) -> {},
                                    responseExtractor -> {
                                    	log.debug("THIS IS A RESPONSE, {} ", response.toString());
                                        response.setContentType("multipart/x-mixed-replace; boundary=FRAME");
                                        IOUtils.copy(responseExtractor.getBody(), response.getOutputStream());
                                        return null;
                                    }
                            );
                		}
                		
                	}
                	
                }
        	}
        }
        
        
    }
    
    
    @PostMapping("/take-off/{droneId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DRONEUSER + "\")")
    public void takeOffDrone(@PathVariable Long droneId, @Valid @RequestBody DroneRequestDTO droneRequestDTO){
        Optional<String> userOptional = SecurityUtils.getCurrentUserLogin();
        if(userOptional.isPresent()) {
        	Optional<DroneUser> droneUserOptional = droneUserRepository.findOneByLogin(userOptional.get());
        	if(droneUserOptional.isPresent()) {
        		droneUserDroneService.takeOff(droneUserOptional.get(), droneId, droneRequestDTO);
        	}
        	
        }
    }
    
    @PostMapping("/land/{droneId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DRONEUSER + "\")")
    public void landDrone(@PathVariable Long droneId, @Valid @RequestBody DroneRequestDTO droneRequestDTO){
        Optional<String> userOptional = SecurityUtils.getCurrentUserLogin();
        if(userOptional.isPresent()) {
        	Optional<DroneUser> droneUserOptional = droneUserRepository.findOneByLogin(userOptional.get());
        	if(droneUserOptional.isPresent()) {
        		droneUserDroneService.landing(droneUserOptional.get(), droneId, droneRequestDTO);
        	}
        	
        }
    }
    

    @GetMapping("/configuration-file/{droneId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DRONEUSER + "\")")
    public void getConfigurationFile(@PathVariable Long droneId, HttpServletResponse response) {
        try {
          // get your file as InputStream
          InputStream is = droneUserDroneService.getDroneConfigurationFile(droneId);
          // copy it to response's OutputStream
          IOUtils.copy(is, response.getOutputStream());
          response.flushBuffer();
        } catch (IOException ex) {
          throw new RuntimeException("IOError writing file to output stream");
        }

    }
    
    @GetMapping("/installation-script")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DRONEUSER + "\")")
    public void getInstalltionScript(HttpServletResponse response) {
        try {
          // get your file as InputStream
          InputStream is = droneUserDroneService.getInstallationScript();
          // copy it to response's OutputStream
          IOUtils.copy(is, response.getOutputStream());
          response.flushBuffer();
        } catch (IOException ex) {
          throw new RuntimeException("IOError writing file to output stream");
        }

    }


    
    
    
    



  
}
