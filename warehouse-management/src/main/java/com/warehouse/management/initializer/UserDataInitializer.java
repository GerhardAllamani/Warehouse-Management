package com.warehouse.management.initializer;

import com.warehouse.management.model.Role;
import com.warehouse.management.model.User;
import com.warehouse.management.repository.UserRepository;
import com.warehouse.management.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class UserDataInitializer {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public UserDataInitializer(UserService userService, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void initializeUserData(){
        List<User> users = Arrays.asList(
                createUser("client", "client", "client@example.com", "client", "Password!23", Role.CLIENT),
                createUser("manager", "manager", "manager@example.com", "manager", "Password!23", Role.WAREHOUSE_MANAGER),
                createUser("admin", "admin", "admin@example.com", "admin", "Password!23", Role.SYSTEM_ADMIN)
        );

        for (User user : users) {
            User existingUser = userService.findByUsername(user.getUsername());
            if (existingUser != null) {
                updateUser(existingUser, user);
            } else {
                userRepository.save(user);
            }
        }
    }

    private User createUser(String firstName, String lastName, String email, String username, String password, Role role) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        return user;
    }

    private void updateUser(User existingUser, User newUser) {
        existingUser.setFirstName(newUser.getFirstName());
        existingUser.setLastName(newUser.getLastName());
        existingUser.setEmail(newUser.getEmail());
        existingUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        existingUser.setRole(newUser.getRole());
        userRepository.save(existingUser);
    }
}
