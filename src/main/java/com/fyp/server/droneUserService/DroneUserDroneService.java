package com.fyp.server.droneUserService;

import com.fasterxml.jackson.databind.JsonNode;
import tech.jhipster.service.filter.InstantFilter;
import com.fyp.server.config.Constants;
import com.fyp.server.domain.Authority;
import com.fyp.server.domain.Drone;
import com.fyp.server.domain.DroneTelemetry;
import com.fyp.server.domain.DroneUser;
import com.fyp.server.domain.User;
import com.fyp.server.droneService.DroneService;
import com.fyp.server.repository.AuthorityRepository;
import com.fyp.server.repository.DroneRepository;
import com.fyp.server.repository.DroneTelemetryRepository;
import com.fyp.server.repository.DroneUserRepository;
import com.fyp.server.repository.UserRepository;
import com.fyp.server.security.AuthoritiesConstants;
import com.fyp.server.security.SecurityUtils;
import com.fyp.server.service.EmailAlreadyUsedException;
import com.fyp.server.service.InvalidPasswordException;
import com.fyp.server.service.UsernameAlreadyUsedException;
import com.fyp.server.service.dto.AdminUserDTO;
import com.fyp.server.service.dto.DroneDTO;
import com.fyp.server.service.dto.DroneTelemetryDTO;
import com.fyp.server.service.dto.DroneTelemetryGraphDTO;
import com.fyp.server.service.dto.DroneUserDTO;
import com.fyp.server.service.dto.UserDTO;
import com.fyp.server.web.rest.errors.BadRequestAlertException;
import com.fyp.utils.GoogleDriveAPIUtil;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    
    private final DroneTelemetryRepository droneTelemetryRepository;
    
    private final DroneQueryService droneQueryService;
    
    private final DroneTelemetryQueryService droneTelemetryQueryService;

    public DroneUserDroneService(DroneRepository droneRepository, DroneService droneService, DroneUserRepository droneUserRepository,
    		DroneQueryService droneQueryService,
    		DroneTelemetryRepository droneTelemetryRepository,
    		DroneTelemetryQueryService droneTelemetryQueryService) {
    	this.droneRepository = droneRepository;
    	this.droneService = droneService;
    	this.droneUserRepository = droneUserRepository;
    	this.droneQueryService = droneQueryService;
    	this.droneTelemetryRepository = droneTelemetryRepository;
    	this.droneTelemetryQueryService = droneTelemetryQueryService;
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
    
    public InputStream getDroneConfigurationFile(Long droneId) {
    	Optional<Drone> droneOptional = droneRepository.findById(droneId);
    	if(droneOptional.isPresent()) {
    		Drone drone = droneOptional.get();
    		Optional<DroneUser> droneUserOptional = droneUserRepository.findById(drone.getDroneUserId());
    		if(droneUserOptional.isPresent()) {
    			DroneUser droneUser = droneUserOptional.get();
    			String directoryName = droneUser.getLogin();
    			String directoryId = GoogleDriveAPIUtil.searchFile(directoryName, true, null);
    			if(directoryId != null) {
    				String fileName = directoryName + "@" + drone.getLogin() + ".conf";
    				String fileId =  GoogleDriveAPIUtil.searchFile(fileName, false, directoryId);
    				if(fileId != null) {
    					String fileContent = GoogleDriveAPIUtil.downloadFile(fileId);
    					InputStream is = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8));
    					return is;
    				}
    				else {
    					throw new BadRequestAlertException("File Not Found", null, null);
    				}
    			}
    			else {
    				throw new BadRequestAlertException("Directory Not Found", null, null);
    			}
    		}
    		else {
    			throw new BadRequestAlertException("Invalid Drone User", null, null);
    		}
    	}
    	else {
    		throw new BadRequestAlertException("Invalid drone", null, null);
    	}
    }
    
    public InputStream getInstallationScript() throws IOException {
    	
    	String fileName = "desktop.tar.xz";
		String fileId =  GoogleDriveAPIUtil.searchFile(fileName, false, null);
		if(fileId != null) {
			byte[] fileContent = GoogleDriveAPIUtil.downloadFileStream(fileId);
			InputStream is = new ByteArrayInputStream(fileContent);
			return is;
		}
		else {
			throw new BadRequestAlertException("File Not Found", null, null);
		}
    	
    			
    				
    			
    }
    
    public void takeOff(Long droneId) {
    	Optional<Drone> droneOptional = droneRepository.findById(droneId);
    	if(droneOptional.isPresent()) {
    		Drone drone = droneOptional.get();
    		String takeOffUrl = "http://" + "localhost" + ":5000/take-off";
    		TakeOffRequest body = new TakeOffRequest();
    		body.setAltitude(Double.valueOf(10));
    		Mono<String> wasd = WebClient.create()
        			.post()
        			.uri(takeOffUrl)
        			.contentType(MediaType.APPLICATION_JSON)
        			.bodyValue(body).retrieve()
        			.bodyToMono(String.class);
    		wasd.subscribe();
    	}
    }
    
    public void landing(Long droneId) {
    	Optional<Drone> droneOptional = droneRepository.findById(droneId);
    	if(droneOptional.isPresent()) {
    		Drone drone = droneOptional.get();
    		String takeOffUrl = "http://" + "localhost" + ":5000/land";
    		Mono<String> wasd = WebClient.create()
        			.post()
        			.uri(takeOffUrl)
        			.contentType(MediaType.APPLICATION_JSON)
        			.retrieve()
        			.bodyToMono(String.class);
    		wasd.subscribe();
    	}
    }
    
    public Page<DroneDTO> getDrone(DroneUser droneUser, Pageable pageable) {
    	DroneCriteria droneCriteria = new DroneCriteria();
    	LongFilter droneUserFilter = new LongFilter();
    	droneUserFilter.setEquals(droneUser.getId());
    	droneCriteria.setDroneUserFilter(droneUserFilter);
    	
    	return droneQueryService.read(droneCriteria, pageable);
    }
    
    public boolean deleteDrone(Long droneId) {
    	Optional<Drone> droneOptional = droneRepository.findById(droneId);
    	if(droneOptional.isPresent()) {
    		Drone drone = droneOptional.get();
    		List<DroneTelemetry> droneTelemetry = droneTelemetryRepository.findAllByDroneId(droneId);
    		droneTelemetryRepository.deleteInBatch(droneTelemetry);
    		droneRepository.delete(drone);
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
    public DroneTelemetryGraphDTO getDroneTelemetry(Long droneId, String range){
    	TemporalUnit unit = ChronoUnit.MINUTES;
    	Instant startInstant = ZonedDateTime.now().minusHours(1).toInstant();
    	if(range.equals("Month")) {
    		 unit = ChronoUnit.DAYS;
    		 startInstant = ZonedDateTime.now().minusMonths(1).toInstant();
    	}
    	else if(range.equals("Day")){
    		unit = ChronoUnit.HOURS;
    		startInstant = ZonedDateTime.now().minusDays(1).toInstant();
    	}
    	else if(range.equals("HOUR")){
    		unit = ChronoUnit.MINUTES;
    		startInstant = ZonedDateTime.now().minusHours(1).toInstant();
    	}
    	log.debug("starting date: {}" ,startInstant.toString());
    	
    	DroneTelemetryCriteria criteria = new DroneTelemetryCriteria();
    	InstantFilter createdDateStartFilter = new InstantFilter();
    	createdDateStartFilter.setGreaterThanOrEqual(startInstant);
    	
    	LongFilter droneIdFilter = new LongFilter();
    	droneIdFilter.setEquals(droneId);
    	
    	criteria.setCreatedDateStart(createdDateStartFilter);
    	criteria.setDroneId(droneIdFilter);
    	Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
    	List<DroneTelemetryDTO> list = droneTelemetryQueryService.read(criteria, sort);
    	Optional<DroneTelemetry> droneTelemetryOptional = droneTelemetryRepository.findFirstByDroneIdOrderByCreatedDateByDesc(droneId);
    	DroneTelemetryDTO droneTelemetryDTO;
    	if(droneTelemetryOptional.isPresent()) {
    		DroneTelemetry droneTelemetry = droneTelemetryOptional.get();
    		droneTelemetryDTO = new DroneTelemetryDTO(droneTelemetry);
    	}
    	else {
    		droneTelemetryDTO = new DroneTelemetryDTO();
    	}
    	List<DroneTelemetryDTO> averageDroneTelemetryDTOList = getAverage(list, unit);
    	DroneTelemetryGraphDTO droneTelemetryGraphDTO = getDateList(averageDroneTelemetryDTOList, startInstant, Instant.now(), unit);
    	droneTelemetryGraphDTO.setLatestTelemetry(droneTelemetryDTO);
    	if(droneTelemetryDTO.getCreatedDate() != null) {
    		if(ChronoUnit.SECONDS.between(droneTelemetryDTO.getCreatedDate(), Instant.now()) > 15) {
        		droneTelemetryGraphDTO.setStatus(false);
        	}
        	else {
        		droneTelemetryGraphDTO.setStatus(true);
        	}
    	}
    	else {
    		droneTelemetryGraphDTO.setStatus(false);
    	}
    	return droneTelemetryGraphDTO;
    	
    }
    
    private DroneTelemetryGraphDTO getDateList(List<DroneTelemetryDTO> droneTelemetryDTOList, Instant startInstant, Instant stopInstant, TemporalUnit unit){
    	List<Instant> dateList = new ArrayList<>();
    	List<DroneTelemetryDTO> droneTelemetryGraph = new ArrayList<>();
    	startInstant = roundUp(startInstant, unit);
    	stopInstant = roundUp(stopInstant, unit);
    	while(startInstant.compareTo(stopInstant)<=0) {
    		dateList.add(startInstant);
    		final Instant startInstantFinal = startInstant;
    		boolean removed =  droneTelemetryDTOList.removeIf(e -> {
    			if(e.getCreatedDate().equals(startInstantFinal)) {
    				droneTelemetryGraph.add(e);

    				return true;
    			}
    			else return false;
    		});
    		startInstant = ZonedDateTime.ofInstant(startInstant, ZoneId.of("Asia/Kuala_Lumpur")).plus(1, unit).truncatedTo(unit).toInstant();
    		if(!removed) {
    			droneTelemetryGraph.add(null);
    			continue;
    		}
    	}
    	DroneTelemetryGraphDTO droneTelemetryGraphDTO = new DroneTelemetryGraphDTO();
    	droneTelemetryGraphDTO.setDateList(dateList);
    	droneTelemetryGraphDTO.setDroneTelemetryDTOList(droneTelemetryGraph);
    	return droneTelemetryGraphDTO;
    }
    
    private List<DroneTelemetryDTO> getAverage(List<DroneTelemetryDTO> droneTelemetryDTOList, TemporalUnit unit){

    	log.debug("temporal unit::: {}",unit.toString());
    	log.debug("temporal unit::: {}",droneTelemetryDTOList.toString());
    	return droneTelemetryDTOList.stream()
        .collect(Collectors.groupingBy(arr 
                -> roundUp(arr.getCreatedDate(), unit))).entrySet().stream()
        .map(e -> {
        	DroneTelemetryDTO droneTelemetryDTO = new DroneTelemetryDTO();
        	List<DroneTelemetryDTO> droneTelemetryDTOList1 = e.getValue();
        	Double averageBatCurrent = (double) 0;
        	Double averageBatLevel = (double) 0;
        	Double averageBatVoltage = (double) 0;
        	Long averageGpsFix = (long) 0;
        	Long averageGpsNumSat = (long) 0;
        	Double averageGroundSpeed = (double) 0;
        	Double averagePitch = (double) 0;
        	Double averageRoll = (double) 0;
        	Double averageYaw = (double) 0;
        	Double averageVelocityX = (double) 0;
        	Double averageVelocityY = (double) 0;
        	Double averageVelocityZ = (double) 0;
        	
        	for(DroneTelemetryDTO droneTelemetryDTO1: droneTelemetryDTOList1) {
        		if(droneTelemetryDTO1.getBatCurrent() != null) {
        			averageBatCurrent +=droneTelemetryDTO1.getBatCurrent();
        		}
        		if(droneTelemetryDTO1.getBatLevel() != null) {
        			averageBatLevel += droneTelemetryDTO1.getBatLevel();
        		}
        		if(droneTelemetryDTO1.getBatVoltage() != null) {
        			averageBatVoltage += droneTelemetryDTO1.getBatVoltage();
        		}
        		if(droneTelemetryDTO1.getGpsFix() != null) {
        			averageGpsFix += droneTelemetryDTO1.getGpsFix();
        		}
        		if(droneTelemetryDTO1.getGpsNumSat() != null) {
        			averageGpsNumSat += droneTelemetryDTO1.getGpsNumSat();
        		}
        		if(droneTelemetryDTO1.getGroundSpeed() != null) {
        			averageGroundSpeed += droneTelemetryDTO1.getGroundSpeed();
        		}
        		if(droneTelemetryDTO1.getPitch() != null) {
        			averagePitch += droneTelemetryDTO1.getPitch();
        		}
        		if(droneTelemetryDTO1.getRoll() != null) {
        			averageRoll += droneTelemetryDTO1.getRoll();
        		}
        		if(droneTelemetryDTO1.getYaw() != null) {
        			averageYaw += droneTelemetryDTO1.getYaw();
        		}
        		if(droneTelemetryDTO1.getVelocityX() != null) {
        			averageVelocityX += droneTelemetryDTO1.getVelocityX();
        		}
        		if(droneTelemetryDTO1.getVelocityY() != null) {
        			averageVelocityY += droneTelemetryDTO1.getVelocityY();
        		}
        		if(droneTelemetryDTO1.getVelocityZ() != null) {
        			averageVelocityZ += droneTelemetryDTO1.getVelocityZ();
        		}

        		
        	}
        	if(droneTelemetryDTOList1.size() > 0) {
        		averageBatCurrent = averageBatCurrent/droneTelemetryDTOList1.size();
            	averageBatLevel = averageBatLevel/droneTelemetryDTOList1.size();
            	averageBatVoltage = averageBatVoltage/droneTelemetryDTOList1.size();
            	averageGpsFix = averageGpsFix/droneTelemetryDTOList1.size();
            	averageGpsNumSat = averageGpsNumSat/droneTelemetryDTOList1.size();
            	averageGroundSpeed = averageGroundSpeed/droneTelemetryDTOList1.size();
            	averagePitch = averagePitch/droneTelemetryDTOList1.size();
            	averageRoll = averageRoll/droneTelemetryDTOList1.size();
            	averageYaw = averageYaw/droneTelemetryDTOList1.size();
            	averageVelocityX = averageVelocityX/droneTelemetryDTOList1.size();
            	averageVelocityY = averageVelocityY/droneTelemetryDTOList1.size();
            	averageVelocityZ = averageVelocityZ/droneTelemetryDTOList1.size();
        	}
        	
        	droneTelemetryDTO.setBatCurrent(averageBatCurrent);
        	droneTelemetryDTO.setBatLevel(averageBatLevel);
        	droneTelemetryDTO.setBatVoltage(averageBatVoltage);
        	droneTelemetryDTO.setGpsFix(averageGpsFix);
        	droneTelemetryDTO.setGpsNumSat(averageGpsNumSat);
        	droneTelemetryDTO.setGroundSpeed(averageGroundSpeed);
        	droneTelemetryDTO.setPitch(averagePitch);
        	droneTelemetryDTO.setRoll(averageRoll);
        	droneTelemetryDTO.setYaw(averageYaw);
        	droneTelemetryDTO.setVelocityX(averageVelocityX);
        	droneTelemetryDTO.setVelocityY(averageVelocityY);
        	droneTelemetryDTO.setVelocityZ(averageVelocityZ);
        	droneTelemetryDTO.setCreatedDate(e.getKey());
        	
        	
        	return droneTelemetryDTO;
        }).sorted((o1, o2) -> o1.getCreatedDate().compareTo(o2.getCreatedDate())).collect(Collectors.toList());
    }
    
    private boolean isSame(ZonedDateTime date1, ZonedDateTime date2, TemporalUnit unit) {
        return date1.truncatedTo(unit).equals(date2.truncatedTo(unit));
    }
    
    private Instant roundUp(Instant datetime, TemporalUnit unit) {
    	return ZonedDateTime.ofInstant(datetime, ZoneId.of("Asia/Kuala_Lumpur")).truncatedTo(unit).toInstant();
    }
    	
    
    public Drone createDrone(DroneUser droneUser, DroneDTO droneDTO) {

    	String createDroneUrl = "http://157.245.94.152:5000/create-user";

        Drone drone = droneService.registerDrone(droneDTO, droneUser, droneDTO.getPassword());
        
        log.debug("Drone Created, Drone ID : {}", drone.getId());
        String droneName = droneUser.getLogin()+"@" + droneDTO.getLogin();
        JSONObject droneRequest = new JSONObject();
        try {
        	VPNRequest body = new VPNRequest();
        	body.setUsername(droneName);
        	droneRequest.put("username", droneName);
        	Mono<String> wasd = WebClient.create()
        			.post()
        			.uri(createDroneUrl)
        			.contentType(MediaType.APPLICATION_JSON)
        			.bodyValue(body).retrieve()
        			.bodyToMono(String.class);
        	wasd.subscribe();
        	
        	
            
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
    
    private class TakeOffRequest{
    	Double altitude;

		public Double getAltitude() {
			return altitude;
		}

		public void setAltitude(Double altitude) {
			this.altitude = altitude;
		}
    	
    	
    }
    

}

