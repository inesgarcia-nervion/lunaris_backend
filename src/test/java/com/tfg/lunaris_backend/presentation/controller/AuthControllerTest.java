package com.tfg.lunaris_backend.presentation.controller;

import com.tfg.lunaris_backend.domain.dto.AuthRequest;
import com.tfg.lunaris_backend.domain.dto.AuthResponse;
import com.tfg.lunaris_backend.domain.dto.NewPasswordRequest;
import com.tfg.lunaris_backend.domain.dto.PasswordResetRequest;
import com.tfg.lunaris_backend.domain.model.User;
import com.tfg.lunaris_backend.domain.service.PasswordResetService;
import com.tfg.lunaris_backend.data.repository.UserRepository;
import com.tfg.lunaris_backend.presentation.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthControllerTest {

    @Test
    void loginSuccessAndFailure() {
        AuthenticationManager am = mock(AuthenticationManager.class);
        JwtUtils ju = mock(JwtUtils.class);
        UserRepository ur = mock(UserRepository.class);
        PasswordResetService prs = mock(PasswordResetService.class);

        AuthController c = new AuthController();
        ReflectionTestUtils.setField(c, "authenticationManager", am);
        ReflectionTestUtils.setField(c, "jwtUtils", ju);
        ReflectionTestUtils.setField(c, "userRepository", ur);
        ReflectionTestUtils.setField(c, "passwordResetService", prs);

        AuthRequest req = new AuthRequest();
        req.setUsername("u");
        req.setPassword("p");

        when(am.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        User u = new User();
        u.setUsername("u");
        u.setRole("ADMIN");
        when(ur.findByUsername("u")).thenReturn(Optional.of(u));
        when(ju.generateToken("u", "ADMIN")).thenReturn("tok");

        ResponseEntity<?> r = c.login(req);
        assertEquals(200, r.getStatusCode().value());
        assertEquals("tok", ((AuthResponse) r.getBody()).getToken());

        // failure
        when(am.authenticate(Mockito.any())).thenThrow(new AuthenticationException("bad"){});
        ResponseEntity<?> rf = c.login(req);
        assertEquals(401, rf.getStatusCode().value());
    }

    @Test
    void forgotPasswordBranches() {
        AuthenticationManager am = mock(AuthenticationManager.class);
        JwtUtils ju = mock(JwtUtils.class);
        UserRepository ur = mock(UserRepository.class);
        PasswordResetService prs = mock(PasswordResetService.class);

        AuthController c = new AuthController();
        ReflectionTestUtils.setField(c, "authenticationManager", am);
        ReflectionTestUtils.setField(c, "jwtUtils", ju);
        ReflectionTestUtils.setField(c, "userRepository", ur);
        ReflectionTestUtils.setField(c, "passwordResetService", prs);

        PasswordResetRequest pr = new PasswordResetRequest();
        pr.setEmail("e");

        when(prs.requestPasswordReset("e")).thenReturn("SUCCESS");
        ResponseEntity<?> r1 = c.forgotPassword(pr);
        assertEquals(200, r1.getStatusCode().value());

        when(prs.requestPasswordReset("e")).thenReturn("EMAIL_NOT_FOUND");
        ResponseEntity<?> r2 = c.forgotPassword(pr);
        assertEquals(404, r2.getStatusCode().value());

        when(prs.requestPasswordReset("e")).thenReturn("OTHER");
        ResponseEntity<?> r3 = c.forgotPassword(pr);
        assertEquals(500, r3.getStatusCode().value());
    }

    @Test
    void validateTokenAndResetPassword() {
        AuthenticationManager am = mock(AuthenticationManager.class);
        JwtUtils ju = mock(JwtUtils.class);
        UserRepository ur = mock(UserRepository.class);
        PasswordResetService prs = mock(PasswordResetService.class);

        AuthController c = new AuthController();
        ReflectionTestUtils.setField(c, "authenticationManager", am);
        ReflectionTestUtils.setField(c, "jwtUtils", ju);
        ReflectionTestUtils.setField(c, "userRepository", ur);
        ReflectionTestUtils.setField(c, "passwordResetService", prs);

        when(prs.validateToken("t")).thenReturn(true);
        assertEquals(200, c.validateToken("t").getStatusCode().value());

        when(prs.validateToken("t")).thenReturn(false);
        assertEquals(400, c.validateToken("t").getStatusCode().value());

        NewPasswordRequest nr = new NewPasswordRequest();
        nr.setToken("t");
        nr.setNewPassword("123");
        // too short
        assertEquals(400, c.resetPassword(nr).getStatusCode().value());

        nr.setNewPassword("123456");
        when(prs.resetPassword("t", "123456")).thenReturn(true);
        assertEquals(200, c.resetPassword(nr).getStatusCode().value());

        when(prs.resetPassword("t", "123456")).thenReturn(false);
        assertEquals(400, c.resetPassword(nr).getStatusCode().value());
    }

    @Test
    void login_userNotInRepo_defaultsRoleUser() {
        AuthenticationManager am = mock(AuthenticationManager.class);
        JwtUtils ju = mock(JwtUtils.class);
        UserRepository ur = mock(UserRepository.class);
        PasswordResetService prs = mock(PasswordResetService.class);

        AuthController c = new AuthController();
        ReflectionTestUtils.setField(c, "authenticationManager", am);
        ReflectionTestUtils.setField(c, "jwtUtils", ju);
        ReflectionTestUtils.setField(c, "userRepository", ur);
        ReflectionTestUtils.setField(c, "passwordResetService", prs);

        AuthRequest req = new AuthRequest();
        req.setUsername("ghost");
        req.setPassword("p");

        when(am.authenticate(Mockito.any())).thenReturn(null);
        when(ur.findByUsername("ghost")).thenReturn(Optional.empty()); // not found → role "USER"
        when(ju.generateToken("ghost", "USER")).thenReturn("tok-user");

        ResponseEntity<?> r = c.login(req);
        assertEquals(200, r.getStatusCode().value());
        assertEquals("tok-user", ((AuthResponse) r.getBody()).getToken());
    }

    @Test
    void login_userWithNullRole_defaultsRoleUser() {
        AuthenticationManager am = mock(AuthenticationManager.class);
        JwtUtils ju = mock(JwtUtils.class);
        UserRepository ur = mock(UserRepository.class);
        PasswordResetService prs = mock(PasswordResetService.class);

        AuthController c = new AuthController();
        ReflectionTestUtils.setField(c, "authenticationManager", am);
        ReflectionTestUtils.setField(c, "jwtUtils", ju);
        ReflectionTestUtils.setField(c, "userRepository", ur);
        ReflectionTestUtils.setField(c, "passwordResetService", prs);

        AuthRequest req = new AuthRequest();
        req.setUsername("norole");
        req.setPassword("p");

        User u = new User(); u.setUsername("norole"); u.setRole(null); // null role
        when(am.authenticate(Mockito.any())).thenReturn(null);
        when(ur.findByUsername("norole")).thenReturn(Optional.of(u));
        when(ju.generateToken("norole", "USER")).thenReturn("tok-default");

        ResponseEntity<?> r = c.login(req);
        assertEquals(200, r.getStatusCode().value());
    }
}
