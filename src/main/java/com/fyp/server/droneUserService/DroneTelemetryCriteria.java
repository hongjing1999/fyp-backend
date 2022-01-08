package com.fyp.server.droneUserService;

import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.LongFilter;

public class DroneTelemetryCriteria {
	InstantFilter createdDateStart;
	InstantFilter createdDateEnd;
	LongFilter droneId;
	public InstantFilter getCreatedDateStart() {
		return createdDateStart;
	}
	public void setCreatedDateStart(InstantFilter createdDateStart) {
		this.createdDateStart = createdDateStart;
	}
	public InstantFilter getCreatedDateEnd() {
		return createdDateEnd;
	}
	public void setCreatedDateEnd(InstantFilter createdDateEnd) {
		this.createdDateEnd = createdDateEnd;
	}
	public LongFilter getDroneId() {
		return droneId;
	}
	public void setDroneId(LongFilter droneId) {
		this.droneId = droneId;
	}
	
	
	
	
}
