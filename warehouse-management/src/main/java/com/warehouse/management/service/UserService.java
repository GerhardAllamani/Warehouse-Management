package com.warehouse.management.service;

import com.warehouse.management.Constants;
import com.warehouse.management.exception.CustomException;
import com.warehouse.management.model.User;
import com.warehouse.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) throws CustomException {
        if(!isPasswordSecure(user.getPassword())){
            throw new CustomException(400, "Bad Request", Constants.PASSWORD_NOT_SECURE);}
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Optional<User> optionalUser = userRepository.findByUsernameAndEmail(user.getUsername(), user.getEmail());
        if(optionalUser.isPresent()) {
            throw new CustomException(400,"Bad Request", Constants.USER_ALREADY_EXISTS);

        }
        return userRepository.save(user);
    }

    public User getUserById(Integer id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.orElse(null);
    }

    public User findByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        return optionalUser.orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(String username, User updatedUser) throws CustomException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isPresent()) {
        Integer id = optionalUser.get().getId();
        updatedUser.setId(id);
            if(!isPasswordSecure(updatedUser.getPassword())){
                throw new CustomException(400, "Bad Request", Constants.PASSWORD_NOT_SECURE);}
        updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            return userRepository.save(updatedUser);}
        else {
            throw new CustomException(400,"Bad Request", Constants.USER_NOT_FOUND);
        }
    }

    public boolean deleteUser(Integer id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            userRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public boolean isPasswordSecure(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
