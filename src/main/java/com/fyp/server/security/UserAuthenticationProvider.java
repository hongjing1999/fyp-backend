package com.fyp.server.security;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserAuthenticationProvider extends DaoAuthenticationProvider {
	public UserAuthenticationProvider(DomainUserDetailsService userDetailsService) {
		this.setPasswordEncoder(new BCryptPasswordEncoder());
		this.setUserDetailsService(userDetailsService);
	}
}
