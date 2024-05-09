package com.warehouse.management;

import com.warehouse.management.controller.LoginController;
import com.warehouse.management.exception.CustomException;
import com.warehouse.management.model.AuthenticationResponse;
import com.warehouse.management.model.PasswordReset;
import com.warehouse.management.model.User;
import com.warehouse.management.model.Error;
import com.warehouse.management.service.AuthenticationService;
import com.warehouse.management.service.JwtService;
import com.warehouse.management.service.PasswordResetService;
import com.warehouse.management.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class LoggingControllerTest {

    @Mock
    private AuthenticationService authService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordResetService passwordResetService;

    @Mock
    private UserService userService;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRegisterSuccess() throws CustomException {
        User user = new User();
        user.setUsername("testUser");

        AuthenticationResponse response = new AuthenticationResponse("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ0ZXN0ZXIiLCJpYXQiOjE3MTUyMDc2NjAsImV4cCI6MTcxNTIxMTI2MH0.pGafwNRIxrXurVgBnObJ9hMLIubGO0OXcRIWCd68fsiHJ3BVmeQrmHq5e817g6yO", "\"message\": \"User login was successful!\"");

        when(authService.register(user)).thenReturn(response);

        ResponseEntity<Object> responseEntity = loginController.register(user);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
    }

    @Test
    public void testRegisterFailure() throws CustomException {
        User user = new User();
        user.setUsername("testUser");

        String errorMessage = "User already exists";
        Error error = new Error();
        error.setMessage(errorMessage);

        when(authService.register(user)).thenThrow(new CustomException(400, "Bad Request", errorMessage));

        ResponseEntity<Object> response = loginController.register(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, ((Error) response.getBody()).getMessage());
    }
    @Test
    public void testResetPasswordSuccess() {
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setUsername("testUser");

        when(jwtService.extractUsername(anyString())).thenReturn("testUser");

        ResponseEntity<Object> response = loginController.resetPassword("Bearer jwt_token", passwordReset);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"message\":\"Password reset request saved successfully\"}", response.getBody());
    }

    @Test
    public void testResetPasswordFailure() {
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setUsername("testUser");

        when(jwtService.extractUsername(anyString())).thenReturn("differentUser");

        ResponseEntity<Object> response = loginController.resetPassword("Bearer jwt_token", passwordReset);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Constants.USERNAME_MISSMATCH, response.getBody());
    }

    @Test
    public void testAuthorizePasswordSuccess() throws CustomException {
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setUsername("testUser");

        when(passwordResetService.findByUsername("testUser")).thenReturn(passwordReset);
        doNothing().when(userService).updateUser(eq("testUser"), any(User.class));
        doNothing().when(passwordResetService).deletePasswordReset(passwordReset);

        ResponseEntity<Object> response = loginController.authorizePassword(true, "testUser");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Constants.AUTHORIZED_PASSWORD, response.getBody());
    }

    @Test
    public void testAuthorizePasswordFailure() throws CustomException {
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setUsername("testUser");

        when(passwordResetService.findByUsername("testUser")).thenThrow(new CustomException(404, "Not Found", "Password reset request not found"));

        ResponseEntity<Object> response = loginController.authorizePassword(true, "testUser");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Password reset request not found", ((Error) response.getBody()).getMessage());
    }
}