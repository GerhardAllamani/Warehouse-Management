package com.warehouse.management.controller;

import com.warehouse.management.Constants;
import com.warehouse.management.exception.CustomException;
import com.warehouse.management.model.Error;
import com.warehouse.management.model.User;
import com.warehouse.management.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        try {
            logger.info("Creating user: {}", user.getUsername());
            User newUser = userService.createUser(user);
            logger.info("User created successfully: {}", newUser.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        }catch (CustomException e) {
            Error error = e.getError();
            logger.error(e.getMessage(), error.getReason(), e.getMessage());
            return ResponseEntity.status(e.getError().getCode()).body(error);
        }
        catch (Exception e) {
            Error error = new Error();
            error.setMessage(Constants.ERROR);
            error.setCode(500);
            logger.error("Error occurred while creating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Integer id) {
        try {
            logger.info("Retrieving user with ID: {}", id);
            User user = userService.getUserById(id);
            if (user != null) {
                logger.info("User retrieved successfully: {}", user.getUsername());
                return ResponseEntity.ok(user);
            } else {
                logger.warn("User not found with ID: {}", id);
                return ResponseEntity.ok().body("[]");
            }
        } catch (Exception e) {
            Error error = new Error();
            error.setMessage(Constants.ERROR);
            error.setCode(500);
            logger.error("Error occurred while retrieving user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        try {
            logger.info("Retrieving all users");
            List<User> users = userService.getAllUsers();
            logger.info("Users retrieved successfully");
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            Error error = new Error();
            error.setMessage(Constants.ERROR);
            error.setCode(500);
            logger.error("Error occurred while retrieving all users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("")
    public ResponseEntity<Object> updateUser( @RequestBody User user) {
        try {
            String username = user.getUsername();
            logger.info("Updating user: {}", username);
            User updatedUser = userService.updateUser(username, user);
            if (updatedUser != null) {
                logger.info("User updated successfully: {}", username);
                return ResponseEntity.ok(updatedUser);
            } else {
                logger.warn("User not found with username: {}", username);
                return ResponseEntity.ok().body("[]");
            }
        }catch (CustomException e) {
            Error error = e.getError();
            logger.error(e.getMessage(), error.getReason(), e.getMessage());
            return ResponseEntity.status(e.getError().getCode()).body(error);
        }
        catch (Exception e) {
            Error error = new Error();
            error.setMessage(Constants.ERROR);
            error.setCode(500);
            logger.error("Error occurred while updating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Integer userId) {
        try {
            logger.info("Deleting user with ID: {}", userId);

            User user = userService.getUserById(userId);
            if (user != null) {
                user.getTokens().forEach(token -> token.setUser(null));
                user.getTokens().clear();
                userService.updateUser(user.getUsername(), user);
                userService.deleteUser(userId);
                logger.info("User deleted successfully: {}", userId);
                return ResponseEntity.ok().build();
            } else {
                logger.warn("User not found with ID: {}", userId);
                return ResponseEntity.ok().body("[]");
            }
        } catch (Exception e) {
            Error error = new Error();
            error.setMessage(Constants.ERROR);
            error.setCode(500);
            logger.error("Error occurred while deleting user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
