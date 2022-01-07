package com.fyp.server.droneUserService;

import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.LongFilter;

public class DroneCriteria {
	InstantFilter lastHeartbeat;
	LongFilter droneUserFilter;
	public InstantFilter getLastHeartbeat() {
		return lastHeartbeat;
	}
	public void setLastHeartbeat(InstantFilter lastHeartbeat) {
		this.lastHeartbeat = lastHeartbeat;
	}
	public LongFilter getDroneUserFilter() {
		return droneUserFilter;
	}
	public void setDroneUserFilter(LongFilter droneUserFilter) {
		this.droneUserFilter = droneUserFilter;
	}
	
	
}
