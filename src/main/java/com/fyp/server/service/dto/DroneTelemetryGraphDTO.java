package com.fyp.server.service.dto;

import com.fyp.server.config.Constants;
import com.fyp.server.domain.Authority;
import com.fyp.server.domain.User;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.*;

/**
 * A DTO representing a user, with his authorities.
 */
public class DroneTelemetryGraphDTO {

    boolean status;
    
    List<Instant> dateList;
    
    List<DroneTelemetryDTO> droneTelemetryDTOList;
    
    DroneTelemetryDTO latestTelemetry;

	public List<DroneTelemetryDTO> getDroneTelemetryDTOList() {
		return droneTelemetryDTOList;
	}

	public void setDroneTelemetryDTOList(List<DroneTelemetryDTO> droneTelemetryDTOList) {
		this.droneTelemetryDTOList = droneTelemetryDTOList;
	}

	public DroneTelemetryDTO getLatestTelemetry() {
		return latestTelemetry;
	}

	public void setLatestTelemetry(DroneTelemetryDTO latestTelemetry) {
		this.latestTelemetry = latestTelemetry;
	}

	public List<Instant> getDateList() {
		return dateList;
	}

	public void setDateList(List<Instant> dateList) {
		this.dateList = dateList;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
	
	
    
    
    
}
