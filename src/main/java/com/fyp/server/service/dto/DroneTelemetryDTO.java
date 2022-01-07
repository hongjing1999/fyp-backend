package com.fyp.server.service.dto;

import java.time.Instant;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fyp.server.config.Constants;
import com.fyp.server.domain.Drone;

public class DroneTelemetryDTO {

    private Long id;

    private Long droneId;

    private Double globalLat;

    private Double globalLon;

    private Double globalAlt;

    private Double relativeLat;

    private Double relativeLon;

    private Double relativeAlt;

    private Double pitch;

    private Double yaw;

    private Double roll;

    private Double velocityX;

    private Double velocityY;

    private Double velocityZ;

    private Long gpsFix;
    
    private Long gpsNumSat;
    
    private Double batVoltage;
    
    private Double batCurrent;

    private Double batLevel;
    
    private boolean isArmable;
    
    private boolean armed;
    
    private String systemStatus;

    private Double groundSpeed;
    
    private String mode;
    

    public DroneTelemetryDTO() {
        // Empty constructor needed for Jackson.
    }


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Long getDroneId() {
		return droneId;
	}


	public void setDroneId(Long droneId) {
		this.droneId = droneId;
	}


	public Double getGlobalLat() {
		return globalLat;
	}


	public void setGlobalLat(Double globalLat) {
		this.globalLat = globalLat;
	}


	public Double getGlobalLon() {
		return globalLon;
	}


	public void setGlobalLon(Double globalLon) {
		this.globalLon = globalLon;
	}


	public Double getGlobalAlt() {
		return globalAlt;
	}


	public void setGlobalAlt(Double globalAlt) {
		this.globalAlt = globalAlt;
	}


	public Double getRelativeLat() {
		return relativeLat;
	}


	public void setRelativeLat(Double relativeLat) {
		this.relativeLat = relativeLat;
	}


	public Double getRelativeLon() {
		return relativeLon;
	}


	public void setRelativeLon(Double relativeLon) {
		this.relativeLon = relativeLon;
	}


	public Double getRelativeAlt() {
		return relativeAlt;
	}


	public void setRelativeAlt(Double relativeAlt) {
		this.relativeAlt = relativeAlt;
	}


	public Double getPitch() {
		return pitch;
	}


	public void setPitch(Double pitch) {
		this.pitch = pitch;
	}


	public Double getYaw() {
		return yaw;
	}


	public void setYaw(Double yaw) {
		this.yaw = yaw;
	}


	public Double getRoll() {
		return roll;
	}


	public void setRoll(Double roll) {
		this.roll = roll;
	}


	public Double getVelocityX() {
		return velocityX;
	}


	public void setVelocityX(Double velocityX) {
		this.velocityX = velocityX;
	}


	public Double getVelocityY() {
		return velocityY;
	}


	public void setVelocityY(Double velocityY) {
		this.velocityY = velocityY;
	}


	public Double getVelocityZ() {
		return velocityZ;
	}


	public void setVelocityZ(Double velocityZ) {
		this.velocityZ = velocityZ;
	}


	public Long getGpsFix() {
		return gpsFix;
	}


	public void setGpsFix(Long gpsFix) {
		this.gpsFix = gpsFix;
	}


	public Long getGpsNumSat() {
		return gpsNumSat;
	}


	public void setGpsNumSat(Long gpsNumSat) {
		this.gpsNumSat = gpsNumSat;
	}


	public Double getBatVoltage() {
		return batVoltage;
	}


	public void setBatVoltage(Double batVoltage) {
		this.batVoltage = batVoltage;
	}


	public Double getBatCurrent() {
		return batCurrent;
	}


	public void setBatCurrent(Double batCurrent) {
		this.batCurrent = batCurrent;
	}


	public Double getBatLevel() {
		return batLevel;
	}


	public void setBatLevel(Double batLevel) {
		this.batLevel = batLevel;
	}


	public boolean isArmable() {
		return isArmable;
	}


	public void setArmable(boolean isArmable) {
		this.isArmable = isArmable;
	}


	public boolean isArmed() {
		return armed;
	}


	public void setArmed(boolean armed) {
		this.armed = armed;
	}


	public String getSystemStatus() {
		return systemStatus;
	}


	public void setSystemStatus(String systemStatus) {
		this.systemStatus = systemStatus;
	}


	public Double getGroundSpeed() {
		return groundSpeed;
	}


	public void setGroundSpeed(Double groundSpeed) {
		this.groundSpeed = groundSpeed;
	}


	public String getMode() {
		return mode;
	}


	public void setMode(String mode) {
		this.mode = mode;
	}
    
    
	
	


   	
}
