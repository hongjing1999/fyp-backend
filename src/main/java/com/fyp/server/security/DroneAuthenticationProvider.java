package com.fyp.server.security;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DroneAuthenticationProvider extends DaoAuthenticationProvider {
	public DroneAuthenticationProvider(DroneDetailsService userDetailsService) {
		this.setPasswordEncoder(new BCryptPasswordEncoder());
		this.setUserDetailsService(userDetailsService);
	}
}
