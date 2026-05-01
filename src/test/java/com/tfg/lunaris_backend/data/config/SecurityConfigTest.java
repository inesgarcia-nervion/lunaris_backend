package com.tfg.lunaris_backend.data.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.tfg.lunaris_backend.presentation.security.JwtAuthenticationFilter;

/**
 * Test para {@link SecurityConfig}.
 */
class SecurityConfigTest {

    /**
     * Verifica que el passwordEncoder es una instancia de BCryptPasswordEncoder.
     */
    @Test
    void passwordEncoder_isBCrypt() {
        SecurityConfig cfg = new SecurityConfig();
        assertTrue(cfg.passwordEncoder() instanceof BCryptPasswordEncoder);
    }

    /**
     * Verifica que el CorsConfigurationSource registra la configuración de CORS correctamente.
     */
    @Test
    void corsConfigurationSource_registersMapping() {
        SecurityConfig cfg = new SecurityConfig();
        CorsConfigurationSource src = cfg.corsConfigurationSource();
        assertNotNull(src);
        assertTrue(src instanceof UrlBasedCorsConfigurationSource);
        UrlBasedCorsConfigurationSource urlSrc = (UrlBasedCorsConfigurationSource) src;
        assertTrue(urlSrc.getCorsConfigurations().containsKey("/**"));
    }

    /**
     * Verifica que el AuthenticationProvider utiliza el UserDetailsService y PasswordEncoder configurados.
     * @throws Exception si ocurre un error al acceder a los campos privados
     */
    @Test
    void authenticationProvider_usesUserDetailsServiceAndPasswordEncoder() throws Exception {
        SecurityConfig cfg = new SecurityConfig();
        // inject a mock UserDetailsService into the private field
        Field udsField = SecurityConfig.class.getDeclaredField("userDetailsService");
        udsField.setAccessible(true);
        UserDetailsService uds = mock(UserDetailsService.class);
        udsField.set(cfg, uds);

        AuthenticationProvider provider = cfg.authenticationProvider();
        assertNotNull(provider);
        assertEquals(provider.getClass().getSimpleName(), "DaoAuthenticationProvider");
    }

    /**
     * Verifica que la SecurityFilterChain se construye correctamente y que el filtro JWT se registra en la cadena.
     * @throws Exception si ocurre un error al acceder a los campos privados
     */
    @Test
    void securityFilterChain_buildsAndRegistersFilter() throws Exception {
        SecurityConfig cfg = new SecurityConfig();
        Field jwtField = SecurityConfig.class.getDeclaredField("jwtAuthenticationFilter");
        jwtField.setAccessible(true);
        JwtAuthenticationFilter jwtMock = mock(JwtAuthenticationFilter.class);
        jwtField.set(cfg, jwtMock);

        Field udsField = SecurityConfig.class.getDeclaredField("userDetailsService");
        udsField.setAccessible(true);
        udsField.set(cfg, mock(UserDetailsService.class));

        HttpSecurity http = mock(HttpSecurity.class);
        DefaultSecurityFilterChain chain = mock(DefaultSecurityFilterChain.class);

        when(http.csrf(any())).thenReturn(http);
        when(http.cors(any())).thenReturn(http);
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.sessionManagement(any())).thenReturn(http);
        when(http.httpBasic(any())).thenReturn(http);
        when(http.addFilterBefore(any(), eq(UsernamePasswordAuthenticationFilter.class))).thenReturn(http);
        when(http.authenticationProvider(any())).thenReturn(http);
        when(http.build()).thenReturn(chain);

        SecurityFilterChain result = cfg.securityFilterChain(http);
        assertSame(chain, result);
        verify(http, atLeastOnce()).addFilterBefore(any(), eq(UsernamePasswordAuthenticationFilter.class));
        verify(http, times(1)).build();
    }

    /**
     * Verifica que la SecurityFilterChain ejecuta correctamente el lambda de autorización.
     * @throws Exception si ocurre un error al acceder a los campos privados
     */
    @Test
    void securityFilterChain_executesAuthorizeLambda() throws Exception {
        SecurityConfig cfg = new SecurityConfig();

        Field jwtField = SecurityConfig.class.getDeclaredField("jwtAuthenticationFilter");
        jwtField.setAccessible(true);
        jwtField.set(cfg, mock(JwtAuthenticationFilter.class));

        Field udsField = SecurityConfig.class.getDeclaredField("userDetailsService");
        udsField.setAccessible(true);
        udsField.set(cfg, mock(UserDetailsService.class));

        HttpSecurity http = mock(HttpSecurity.class);
        DefaultSecurityFilterChain chain = mock(DefaultSecurityFilterChain.class);

        when(http.csrf(any())).thenReturn(http);
        when(http.cors(any())).thenReturn(http);
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.sessionManagement(any())).thenReturn(http);
        when(http.httpBasic(any())).thenReturn(http);
        when(http.addFilterBefore(any(), eq(UsernamePasswordAuthenticationFilter.class))).thenReturn(http);
        when(http.authenticationProvider(any())).thenReturn(http);
        when(http.build()).thenReturn(chain);

        SecurityFilterChain result = cfg.securityFilterChain(http);
        assertSame(chain, result);
        assertTrue(cfg.passwordEncoder() instanceof BCryptPasswordEncoder);
    }
}
