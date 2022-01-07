package com.fyp.server.droneUserService;

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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import tech.jhipster.security.RandomUtil;
import tech.jhipster.service.filter.LongFilter;

/**
 * Service class for managing users.
 */
@EnableAsync
@Service
@Transactional
public class DroneUserDroneService {

    private final Logger log = LoggerFactory.getLogger(DroneUserDroneService.class);
    
    private final DroneService droneService;
    
    private final DroneRepository droneRepository;
    
    private final DroneUserRepository droneUserRepository;
    
    private final DroneQueryService droneQueryService;

    public DroneUserDroneService(DroneRepository droneRepository, DroneService droneService, DroneUserRepository droneUserRepository,
    		DroneQueryService droneQueryService) {
    	this.droneRepository = droneRepository;
    	this.droneService = droneService;
    	this.droneUserRepository = droneUserRepository;
    	this.droneQueryService = droneQueryService;
    }
    
    @Async
    @Scheduled(fixedRate = 10000000)
    public void getFromGoogleDrive() {
    	List<Drone> droneList = droneRepository.findAllByIpAddressIsNull();
    	for(Drone drone: droneList) {
    		Long droneUserId = drone.getDroneUserId();
    		String droneUserName = drone.getLogin();
    		if(droneUserId != null) {
    			Optional<DroneUser> droneUserOptional = droneUserRepository.findById(droneUserId);
        		if(droneUserOptional.isPresent()) {
        			DroneUser droneUser = droneUserOptional.get();
        			String directoryName = droneUser.getLogin();
        			String fileName = directoryName + "@" + droneUserName + ".conf";
        			log.debug("FILE NAME!!! {}", fileName);
        			String directoryId = GoogleDriveAPIUtil.searchFile(directoryName, true, null);
        			if(directoryId != null) {
        				String fileId = GoogleDriveAPIUtil.searchFile(fileName, false, directoryId);
        				log.debug("DIRECTORY FOUND!!!! {}", directoryId);
        				if(fileId != null) {
        					log.debug("FILE FOUND!!!! {}", fileId);
        					String fileContent = GoogleDriveAPIUtil.downloadFile(fileId);
        					String ipAddress = fileContent.split("Address = ")[1].split(" ,")[0];
        					drone.setIpAddress(ipAddress);
        					droneRepository.save(drone);
            				
        				}
        				
        			}
        		}
    		}
    		
    	}
    }
    
    public Page<DroneDTO> getDrone(DroneUser droneUser, Pageable pageable) {
    	DroneCriteria droneCriteria = new DroneCriteria();
    	LongFilter droneUserFilter = new LongFilter();
    	droneUserFilter.setEquals(droneUser.getId());
    	droneCriteria.setDroneUserFilter(droneUserFilter);
    	
    	return droneQueryService.read(droneCriteria, pageable);
    }
    
    public Drone createDrone(DroneUser droneUser, DroneDTO droneDTO) {

    	String createDroneUrl = "http://157.245.94.152:5000/create-user";
    	
    	
    	RestTemplate restTemplate = new RestTemplate();
    	HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Drone drone = droneService.registerDrone(droneDTO, droneUser, droneDTO.getPassword());
        
        log.debug("Drone Created, Drone ID : {}", drone.getId());
        String droneName = droneUser.getLogin()+"@" + droneDTO.getLogin();
        JSONObject droneRequest = new JSONObject();
        try {
        	VPNRequest body = new VPNRequest();
        	body.setUsername(droneName);
        	droneRequest.put("username", droneName);
        	HttpEntity<String> request = new HttpEntity<String>(droneRequest.toString(), headers);
        	Mono<String> wasd = WebClient.create()
        			.post()
        			.uri(createDroneUrl)
        			.contentType(MediaType.APPLICATION_JSON)
        			.bodyValue(body).retrieve()
        			.bodyToMono(String.class);
        	wasd.subscribe(
//       	result -> {
//        		String vpnConfiguration = result;
//        		log.debug("flux result:: {}", vpnConfiguration);
//            	
//                String ipAddress = vpnConfiguration.split("Address = ")[1].split(" ,")[0];
//                drone.setIpAddress(ipAddress);
//                log.debug("VPN User Created, Drone IP: {}", ipAddress);
//                droneRepository.save(drone);
//                String googleDriveFolderId = null;
//                try {
//                	googleDriveFolderId = GoogleDriveAPIUtil.searchFile(droneUser.getLogin(), true);
//                }
//                catch(Exception e) {
//                	googleDriveFolderId = null;
//                }
//                
//            	if(googleDriveFolderId == null) {
//            		googleDriveFolderId = GoogleDriveAPIUtil.uploadFile("application/vnd.google-apps.folder", droneUser.getLogin(), null, null);
//            	}
//                GoogleDriveAPIUtil.uploadFile(MediaType.TEXT_PLAIN_VALUE, droneName+".conf",googleDriveFolderId, vpnConfiguration);
//        	}
        );
        	
        	
//            ResponseEntity<String> result = restTemplate.postForEntity(createDroneUrl, request, String.class);
//            if(result.getStatusCodeValue() == 200) {
//            	String vpnConfiguration = result.getBody();
//            	String googleDriveFolderId = GoogleDriveAPIUtil.searchFile(droneUser.getLogin(), true);
//            	if(googleDriveFolderId == null) {
//            		googleDriveFolderId = GoogleDriveAPIUtil.uploadFile("application/vnd.google-apps.folder", droneUser.getLogin(), null, null);
//            	}
//            	GoogleDriveAPIUtil.uploadFile(MediaType.TEXT_PLAIN_VALUE, droneName+".conf",googleDriveFolderId, vpnConfiguration);
//                String ipAddress = vpnConfiguration.split("Address = ")[1].split(" ,")[0];
//                
//                droneDTO.setIpAddress(ipAddress);
//                Drone drone = droneService.registerDrone(droneDTO, droneUser, droneDTO.getPassword());
//
//                return drone;
//            }
//            else {
//            	return null;
//            }
            
        }
        catch (JSONException e) {
        	throw e;
        }
        return drone;
    }
    
    private class VPNRequest{
    	String username;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}
    	
    }
    

}

