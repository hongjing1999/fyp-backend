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
public class DroneUserDroneService {

    private final Logger log = LoggerFactory.getLogger(DroneUserDroneService.class);
    
    private final DroneService droneService;
    
    private final DroneRepository droneRepository;
    

    public DroneUserDroneService(DroneRepository droneRepository, DroneService droneService) {
    	this.droneRepository = droneRepository;
    	this.droneService = droneService;
    }
    
    public Drone createDrone(DroneUser droneUser, DroneDTO droneDTO) {

    	String createDroneUrl = "http://157.245.94.152:5000/create-user";
    	RestTemplate restTemplate = new RestTemplate();
    	HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String droneName = droneUser.getLogin()+"@" + droneDTO.getName();
        JSONObject droneRequest = new JSONObject();
        try {
        	droneRequest.put("username", droneName);
        	HttpEntity<String> request = new HttpEntity<String>(droneRequest.toString(), headers);
            ResponseEntity<String> result = restTemplate.postForEntity(createDroneUrl, request, String.class);
            if(result.getStatusCodeValue() == 200) {
            	String vpnConfiguration = result.getBody();
            	String googleDriveFolderId = GoogleDriveAPIUtil.searchFile(droneUser.getLogin(), true);
            	if(googleDriveFolderId == null) {
            		googleDriveFolderId = GoogleDriveAPIUtil.uploadFile("application/vnd.google-apps.folder", droneUser.getLogin(), null, null);
            	}
            	GoogleDriveAPIUtil.uploadFile(MediaType.TEXT_PLAIN_VALUE, droneName+".conf",googleDriveFolderId, vpnConfiguration);
                String ipAddress = vpnConfiguration.split("Address = ")[1].split(" ,")[0];
                
                droneDTO.setIpAddress(ipAddress);
                Drone drone = droneService.registerDrone(droneDTO, droneUser, droneDTO.getPassword());

                return drone;
            }
            else {
            	return null;
            }
            
        }
        catch (JSONException e) {
        	throw e;
        }
    }
    
    

}