package com.fyp.server.web.droneRest;

import com.fyp.server.domain.Drone;
import com.fyp.server.domain.DroneUser;
import com.fyp.server.domain.User;
import com.fyp.server.droneService.DroneService;
import com.fyp.server.droneUserService.DroneUserService;
import com.fyp.server.repository.DroneUserRepository;
import com.fyp.server.repository.UserRepository;
import com.fyp.server.security.AuthoritiesConstants;
import com.fyp.server.security.SecurityUtils;
import com.fyp.server.service.MailService;
import com.fyp.server.service.UserService;
import com.fyp.server.service.dto.AdminUserDTO;
import com.fyp.server.service.dto.DroneUserDTO;
import com.fyp.server.service.dto.PasswordChangeDTO;
import com.fyp.server.service.dto.UserDTO;
import com.fyp.server.web.rest.errors.*;
import com.fyp.server.web.rest.vm.KeyAndPasswordVM;
import com.fyp.server.web.rest.vm.ManagedDroneUserVM;
import com.fyp.server.web.rest.vm.ManagedDroneVM;
import com.fyp.server.web.rest.vm.ManagedUserVM;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/droneApi")
public class DroneAccountResource {

    private static class AccountResourceException extends RuntimeException {

        private AccountResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(DroneAccountResource.class);

    private final DroneUserRepository droneUserRepository;

    private final DroneService droneService;

    private final MailService mailService;

    public DroneAccountResource( DroneUserRepository droneUserRepository, DroneService droneService, MailService mailService) {
        this.droneUserRepository = droneUserRepository;
        this.droneService = droneService;
        this.mailService = mailService;
    }

    /**
     * {@code POST  /register} : register the user.
     *
     * @param managedUserVM the managed user View Model.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already used.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DRONEUSER + "\")")
    public void registerAccount(@Valid @RequestBody ManagedDroneVM managedDroneVM) {
        if (isPasswordLengthInvalid(managedDroneVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        Optional<String> droneUserLogin = SecurityUtils.getCurrentUserLogin();
        DroneUser droneUser = null;
        if(droneUserLogin.isPresent()){
        	Optional<DroneUser> droneUserOptional = droneUserRepository.findOneByLogin(droneUserLogin.get());
        	if(droneUserOptional.isPresent()) {
        		droneUser = droneUserOptional.get();
        	}
        }
        if(droneUser != null) {
        	Drone drone = droneService.registerDrone(managedDroneVM, droneUser, managedDroneVM.getPassword());
        }
        
    }


    /**
     * {@code GET  /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @GetMapping("/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
    public DroneUserDTO getAccount() {
        return droneService
            .getUserWithAuthorities()
            .map(DroneUserDTO::new)
            .orElseThrow(() -> new AccountResourceException("User could not be found"));
    }

    /**
     * {@code POST  /account} : update the current user information.
     *
     * @param userDTO the current user information.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user login wasn't found.
     */
//    @PostMapping("/account")
//    public void saveAccount(@Valid @RequestBody AdminUserDTO userDTO) {
//        String userLogin = SecurityUtils
//            .getCurrentUserLogin()
//            .orElseThrow(() -> new AccountResourceException("Current user login not found"));
//        Optional<DroneUser> existingUser = droneUserRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
//        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userLogin))) {
//            throw new EmailAlreadyUsedException();
//        }
//        Optional<DroneUser> user = droneUserRepository.findOneByLogin(userLogin);
//        if (!user.isPresent()) {
//            throw new AccountResourceException("User could not be found");
//        }
//        droneUserService.updateUser(
//            userDTO.getFirstName(),
//            userDTO.getLastName(),
//            userDTO.getEmail(),
//            userDTO.getLangKey(),
//            userDTO.getImageUrl()
//        );
//    }

    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PostMapping(path = "/account/change-password")
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (isPasswordLengthInvalid(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        droneService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
    }

    /**
     * {@code POST   /account/reset-password/init} : Send an email to reset the password of the user.
     *
     * @param mail the mail of the user.
     */
    @PostMapping(path = "/account/reset-password/init")
    public void requestPasswordReset(@RequestBody String mail) {
    	Optional<String> droneUserLogin = SecurityUtils.getCurrentUserLogin();
        DroneUser droneUser = null;
        if(droneUserLogin.isPresent()){
        	Optional<DroneUser> droneUserOptional = droneUserRepository.findOneByLogin(droneUserLogin.get());
        	if(droneUserOptional.isPresent()) {
        		droneUser = droneUserOptional.get();
        	}
        }
        
        Optional<Drone> drone = droneService.requestPasswordReset(mail, droneUser);
        if (drone.isPresent()) {
            //mailService.sendPasswordResetMail(drone.get());
        } else {
            // Pretend the request has been successful to prevent checking which emails really exist
            // but log that an invalid attempt has been made
            log.warn("Password reset requested for non existing mail");
        }
    }

    /**
     * {@code POST   /account/reset-password/finish} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    @PostMapping(path = "/account/reset-password/finish")
    public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (isPasswordLengthInvalid(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        Optional<Drone> user = droneService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this reset key");
        }
    }

    private static boolean isPasswordLengthInvalid(String password) {
        return (
            StringUtils.isEmpty(password) ||
            password.length() < ManagedUserVM.PASSWORD_MIN_LENGTH ||
            password.length() > ManagedUserVM.PASSWORD_MAX_LENGTH
        );
    }
}
