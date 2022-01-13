package com.fyp.server.web.droneRest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fyp.server.domain.Drone;
import com.fyp.server.repository.DroneRepository;
import com.fyp.server.security.DroneAuthenticationProvider;
import com.fyp.server.security.DroneUserAuthenticationProvider;
import com.fyp.server.security.jwt.JWTFilter;
import com.fyp.server.security.jwt.TokenProvider;
import com.fyp.server.web.rest.errors.BadRequestAlertException;
import com.fyp.server.web.rest.vm.LoginVM;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/droneApi")
public class DroneJWTController {

    private final TokenProvider tokenProvider;
    
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    
    private final DroneAuthenticationProvider authenticationProvider;
    
    private final DroneRepository droneRepository;

    public DroneJWTController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder,
    		DroneAuthenticationProvider authenticationProvider,
    		DroneRepository droneRepository) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.authenticationProvider = authenticationProvider;
        this.droneRepository = droneRepository;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginVM.getUsername(),
            loginVM.getPassword()
        );
        System.out.println(authenticationToken);
        //Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        Authentication authentication = authenticationProvider.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication, Long.valueOf(26298000000L));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }
    
    @PostMapping("/authenticateFromUI")
    public ResponseEntity<JWTToken> authorizeFromUI(@Valid @RequestBody LoginVM loginVM) {
    	Optional<Drone> droneOptional = droneRepository.findById(loginVM.getDroneId());
    	if(droneOptional.isPresent()) {
    		Drone drone = droneOptional.get();
    		Optional<Drone> droneOptionalLogin = droneRepository.findOneByLogin(loginVM.getUsername());
    		if(droneOptionalLogin.isPresent()) {
    			Drone droneLogin = droneOptionalLogin.get();
    			if(drone.getId().equals(droneLogin.getId())) {
    				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
    			            loginVM.getUsername(),
    			            loginVM.getPassword()
    			        );
    			        System.out.println(authenticationToken);
    			        //Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    			        Authentication authentication = authenticationProvider.authenticate(authenticationToken);
    			        SecurityContextHolder.getContext().setAuthentication(authentication);
    			        String jwt = tokenProvider.createToken(authentication, Long.valueOf(26298000000L));
    			        HttpHeaders httpHeaders = new HttpHeaders();
    			        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
    			        return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
    			}
    			else {
    				throw new BadRequestAlertException("INVALID_AUTHORIZATION", null, null);
    			}
    		}
    		else {
    			throw new BadRequestAlertException("INVALID_DRONE", null, null);
    		}
    	}
    	else {
    		throw new BadRequestAlertException("INVALID_DRONE", null, null);
    	}
        
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}
