package com.tfg.lunaris_backend.presentation.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test para {@link JwtAuthenticationFilter}.
 */
class JwtAuthenticationFilterTest {

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Verifica que un token válido establece la autenticación en el contexto de seguridad.
     * @throws ServletException si ocurre un error de servlet
     * @throws IOException si ocurre un error de E/S
     */
    @Test
    void validTokenSetsAuthentication() throws ServletException, IOException {
        JwtUtils ju = new JwtUtils();
        ReflectionTestUtils.setField(ju, "jwtSecret", "01234567012345670123456701234567");
        ReflectionTestUtils.setField(ju, "jwtExpirationMs", 3600000L);
        ju.init();

        String token = ju.generateToken("tester");

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter();
        ReflectionTestUtils.setField(filter, "jwtUtils", ju);

        UserDetailsService uds = mock(UserDetailsService.class);
        when(uds.loadUserByUsername("tester")).thenReturn(
                new User("tester", "pwd", List.of(new SimpleGrantedAuthority("ROLE_USER"))));
        ReflectionTestUtils.setField(filter, "userDetailsService", uds);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(req.getHeader("Authorization")).thenReturn("Bearer " + token);

        filter.doFilterInternal(req, res, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(req, res);
    }

    /**
     * Verifica que si no hay encabezado de autorización, no se establece la autenticación y se continúa con la cadena de filtros.
     * @throws ServletException si ocurre un error de servlet
     * @throws IOException si ocurre un error de E/S
     */
    @Test
    void noAuthorizationHeader_doesNotSetAuthentication() throws ServletException, IOException {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter();
        JwtUtils ju = mock(JwtUtils.class);
        ReflectionTestUtils.setField(filter, "jwtUtils", ju);
        UserDetailsService uds = mock(UserDetailsService.class);
        ReflectionTestUtils.setField(filter, "userDetailsService", uds);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(req.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(req, res, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(req, res);
    }

    /**
     * Verifica que si el encabezado de autorización no comienza con "Bearer ", no se establece la autenticación y 
     * se continúa con la cadena de filtros.
     * @throws ServletException si ocurre un error de servlet
     * @throws IOException si ocurre un error de E/S
     */
    @Test
    void headerNotBearer_doesNotSetAuthentication() throws ServletException, IOException {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter();
        JwtUtils ju = mock(JwtUtils.class);
        ReflectionTestUtils.setField(filter, "jwtUtils", ju);
        UserDetailsService uds = mock(UserDetailsService.class);
        ReflectionTestUtils.setField(filter, "userDetailsService", uds);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(req.getHeader("Authorization")).thenReturn("Basic somebase64==");

        filter.doFilterInternal(req, res, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(req, res);
    }

    /**
     * Verifica que si el token es inválido y lanza una excepción, no se establece la autenticación y se 
     * continúa con la cadena de filtros.
     * @throws ServletException si ocurre un error de servlet
     * @throws IOException si ocurre un error de E/S
     */
    @Test
    void validateTokenThrowsException_doesNotSetAuthentication() throws ServletException, IOException {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter();
        JwtUtils ju = mock(JwtUtils.class);
        when(ju.validateToken(anyString())).thenThrow(new RuntimeException("parse error"));
        ReflectionTestUtils.setField(filter, "jwtUtils", ju);
        UserDetailsService uds = mock(UserDetailsService.class);
        ReflectionTestUtils.setField(filter, "userDetailsService", uds);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(req.getHeader("Authorization")).thenReturn("Bearer sometoken");

        filter.doFilterInternal(req, res, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(req, res);
    }
}
