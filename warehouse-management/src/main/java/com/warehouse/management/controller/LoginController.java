package com.warehouse.management.controller;

import com.warehouse.management.Constants;
import com.warehouse.management.exception.CustomException;
import com.warehouse.management.model.*;
import com.warehouse.management.model.Error;
import com.warehouse.management.service.AuthenticationService;
import com.warehouse.management.service.JwtService;
import com.warehouse.management.service.PasswordResetService;
import com.warehouse.management.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final AuthenticationService authService;
    private final JwtService jwtService;
    private final PasswordResetService passwordResetService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public LoginController(AuthenticationService authService, JwtService jwtService, PasswordResetService passwordResetService, UserService userService, PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.passwordResetService = passwordResetService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody User request) {
        Object response;
        int statusCode = HttpStatus.OK.value();

        request.setRole(Role.CLIENT);
        try {
            logger.info("Registering user: {}", request.getUsername());
            response = authService.register(request);
            logger.info("User registered successfully: {}", request.getUsername());
        } catch (CustomException e) {
            response = e.getError();
            statusCode = e.getError().getCode();
            logger.error(e.getMessage(), request.getUsername(), e.getMessage());
        } catch (Exception e) {
            Error error = new Error();
            error.setMessage(Constants.ERROR);
            response = error;
            error.setCode(500);
            logger.error("Error occurred while registering user {}: {}", request.getUsername(), e.getMessage());
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        return ResponseEntity.status(statusCode).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody User request) {
        Object response;
        try {
            logger.info("Logging in user: {}", request.getUsername());
            response = authService.authenticate(request);
            logger.info("User logged in successfully: {}", request.getUsername());
            return ResponseEntity.ok(response);
        }catch (CustomException e) {
            Error error = e.getError();
            logger.error(e.getMessage(), error.getReason(), e.getMessage());
            return ResponseEntity.status(e.getError().getCode()).body(error);
        }
        catch (Exception e) {
            Error error = new Error();
            error.setMessage(Constants.ERROR);
            error.setCode(500);
            logger.error("Error occurred while logging in user {}: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<Object> resetPassword(@RequestHeader("Authorization") String authorizationHeader, @RequestBody PasswordReset passwordReset) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            String username = jwtService.extractUsername(token);

            if (passwordReset == null) {
                Error error = new Error();
                error.setMessage("Password reset request not found");
                error.setCode(HttpStatus.NOT_FOUND.value());
                logger.error("Password reset request not found for user: {}", username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            passwordReset.setUsername(username);
            if (!username.equalsIgnoreCase(passwordReset.getUsername())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Constants.USERNAME_MISSMATCH);
            }
            passwordResetService.savePasswordReset(passwordReset);
            return ResponseEntity.ok(Constants.PASSWORD_RESET_RESPONSE);
        }catch (CustomException e) {
            Error error = e.getError();
            logger.error(e.getMessage(), error.getReason(), e.getMessage());
            return ResponseEntity.status(e.getError().getCode()).body(error);
        }

        catch (Exception e) {
            Error error = new Error();
            error.setMessage(Constants.ERROR);
            error.setCode(500);
            logger.error("Error occurred while resetting password: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/authorize/{username}")
    public ResponseEntity<Object> authorizePassword(@RequestParam(required = true) boolean action, @PathVariable String username) {
        try {
            logger.error("Authorizing password change for: {}", username);

            PasswordReset passwordReset = passwordResetService.findByUsername(username);

            if (action) {
                User user = userService.findByUsername(username);
                user.setPassword(passwordReset.getNewPassword());
                userService.updateUser(username, user);
            }
                passwordResetService.deletePasswordReset(passwordReset);
            logger.error("Password change authorized/declined: {}", username);

            return ResponseEntity.ok(Constants.AUTHORIZED_PASSWORD);
        }catch (CustomException e) {
            Error error = e.getError();
            logger.error(e.getMessage(), error.getReason(), e.getMessage());
            return ResponseEntity.status(e.getError().getCode()).body(error);
        }

        catch (Exception e) {
            Error error = new Error();
            error.setMessage(Constants.ERROR);
            error.setCode(500);
            logger.error("Error occurred while authorizing password change: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}