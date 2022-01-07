package com.fyp.server.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fyp.server.config.Constants;
import com.fyp.server.service.dto.DroneDTO;
import com.fyp.server.service.dto.DroneTelemetryDTO;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.BatchSize;

/**
 * A user.
 */
@Entity
@Table(name = "drone_telemetry")
public class DroneTelemetry extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "drone_id")
    private Long droneId;

    @Column(name = "global_lat")
    private Double globalLat;
    
    @Column(name = "global_lon")
    private Double globalLon;
    
    @Column(name = "global_alt")
    private Double globalAlt;
    
    @Column(name = "relative_lat")
    private Double relativeLat;
    
    @Column(name = "relative_lon")
    private Double relativeLon;
    
    @Column(name = "relative_alt")
    private Double relativeAlt;
    
    @Column(name = "pitch")
    private Double pitch;
    
    @Column(name = "yaw")
    private Double yaw;
    
    @Column(name = "roll")
    private Double roll;
    
    @Column(name = "velocity_x")
    private Double velocityX;
    
    @Column(name = "velocity_y")
    private Double velocityY;
    
    @Column(name = "velocity_z")
    private Double velocityZ;
    
    @Column(name = "gps_fix")
    private Long gpsFix;
    
    @Column(name = "gps_num_sat")
    private Long gpsNumSat;
    
    @Column(name = "bat_voltage")
    private Double batVoltage;
    
    @Column(name = "bat_current")
    private Double batCurrent;
    
    @Column(name = "bat_level")
    private Double batLevel;
    
    @Column(name = "is_armable")
    private boolean isArmable;
    
    @Column(name = "armed")
    private boolean armed;
    
    
    @Size(max = 20)
    @Column(name = "system_status", length = 20)
    private String systemStatus;
    
    @Column(name = "groundSpeed")
    private Double groundSpeed;
    
    @Size(max = 20)
    @Column(name = "mode", length = 20)
    private String mode;
    
    
    public DroneTelemetry() {
    	
    }
    

   
	public DroneTelemetry(DroneTelemetryDTO droneTelemetryDTO) {
		super();
		this.droneId = droneTelemetryDTO.getDroneId();
		this.globalLat = droneTelemetryDTO.getGlobalLat();
		this.globalLon = droneTelemetryDTO.getGlobalLon();
		this.globalAlt = droneTelemetryDTO.getGlobalAlt();
		this.relativeLat = droneTelemetryDTO.getRelativeLat();
		this.relativeLon = droneTelemetryDTO.getRelativeLon();
		this.relativeAlt = droneTelemetryDTO.getRelativeAlt();
		this.pitch = droneTelemetryDTO.getPitch();
		this.yaw = droneTelemetryDTO.getYaw();
		this.roll = droneTelemetryDTO.getRoll();
		this.velocityX = droneTelemetryDTO.getVelocityX();
		this.velocityY = droneTelemetryDTO.getVelocityY();
		this.velocityZ = droneTelemetryDTO.getVelocityZ();
		this.gpsFix = droneTelemetryDTO.getGpsFix();
		this.gpsNumSat = droneTelemetryDTO.getGpsNumSat();
		this.batVoltage = droneTelemetryDTO.getBatVoltage();
		this.batCurrent = droneTelemetryDTO.getBatCurrent();
		this.batLevel = droneTelemetryDTO.getBatLevel();
		this.isArmable = droneTelemetryDTO.isArmable();
		this.armed = droneTelemetryDTO.isArmed();
		this.systemStatus = droneTelemetryDTO.getSystemStatus();
		this.groundSpeed = droneTelemetryDTO.getGroundSpeed();
		this.mode = droneTelemetryDTO.getMode();
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






	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        return id != null && id.equals(((DroneTelemetry) o).id);
    }



    

    
}
