package com.warehouse.management.service;

import com.warehouse.management.Constants;
import com.warehouse.management.exception.CustomException;
import com.warehouse.management.model.PasswordReset;
import com.warehouse.management.repository.PasswordResetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PasswordResetService {

    private final PasswordResetRepository passwordResetRepository;

    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";


    @Autowired
    public PasswordResetService(PasswordResetRepository passwordResetRepository) {
        this.passwordResetRepository = passwordResetRepository;
    }

    public PasswordReset savePasswordReset(PasswordReset passwordReset) throws CustomException {
        if(!isPasswordSecure(passwordReset.getNewPassword())){
            throw new CustomException(400, "Bad Request", Constants.PASSWORD_NOT_SECURE);
        }
        try{
        return passwordResetRepository.save(passwordReset);}
        catch (Exception e){
            throw new CustomException(400, "Bad Request", Constants.PASSWORD_REQUEST_DUPLICATE);
        }
    }

    public PasswordReset findByUsername(String username) {
        return passwordResetRepository.findByUsername(username);
    }

    public void deletePasswordReset(PasswordReset passwordReset) {
        passwordResetRepository.delete(passwordReset);
    }

    public boolean isPasswordSecure(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

}
