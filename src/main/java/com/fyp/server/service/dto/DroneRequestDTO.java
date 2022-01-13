package com.fyp.server.service.dto;

import java.time.Instant;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fyp.server.config.Constants;
import com.fyp.server.domain.Drone;

public class DroneRequestDTO {

	private String username;
	private String password;
	private Double altitude;

    public DroneRequestDTO() {
        // Empty constructor needed for Jackson.
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}
    
    
    
	


   	
}
