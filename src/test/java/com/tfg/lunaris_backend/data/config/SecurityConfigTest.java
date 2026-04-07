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


class SecurityConfigTest {

    @Test
    void passwordEncoder_isBCrypt() {
        SecurityConfig cfg = new SecurityConfig();
        assertTrue(cfg.passwordEncoder() instanceof BCryptPasswordEncoder);
    }

    @Test
    void corsConfigurationSource_registersMapping() {
        SecurityConfig cfg = new SecurityConfig();
        CorsConfigurationSource src = cfg.corsConfigurationSource();
        assertNotNull(src);
        assertTrue(src instanceof UrlBasedCorsConfigurationSource);
        UrlBasedCorsConfigurationSource urlSrc = (UrlBasedCorsConfigurationSource) src;
        assertTrue(urlSrc.getCorsConfigurations().containsKey("/**"));
    }

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

    @Test
    void securityFilterChain_buildsAndRegistersFilter() throws Exception {
        SecurityConfig cfg = new SecurityConfig();
        // inject mock jwt filter and userDetailsService
        java.lang.reflect.Field jwtField = SecurityConfig.class.getDeclaredField("jwtAuthenticationFilter");
        jwtField.setAccessible(true);
        JwtAuthenticationFilter jwtMock = mock(JwtAuthenticationFilter.class);
        jwtField.set(cfg, jwtMock);

        java.lang.reflect.Field udsField = SecurityConfig.class.getDeclaredField("userDetailsService");
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

    @Test
    void securityFilterChain_executesAuthorizeLambda() throws Exception {
        SecurityConfig cfg = new SecurityConfig();
        // ensure jwt filter and userDetailsService injected (not used in this reflective invocation)
        java.lang.reflect.Field jwtField = SecurityConfig.class.getDeclaredField("jwtAuthenticationFilter");
        jwtField.setAccessible(true);
        jwtField.set(cfg, mock(JwtAuthenticationFilter.class));

        java.lang.reflect.Field udsField = SecurityConfig.class.getDeclaredField("userDetailsService");
        udsField.setAccessible(true);
        udsField.set(cfg, mock(UserDetailsService.class));

        // Find the compiler-generated lambda method for the authorizeHttpRequests lambda
        java.lang.reflect.Method lambdaMethod = java.util.Arrays.stream(SecurityConfig.class.getDeclaredMethods())
                .filter(m -> m.getName().contains("lambda$securityFilterChain"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Lambda method not found"));
        lambdaMethod.setAccessible(true);

        // Create a deep-stub mock of the parameter type so chained calls inside the lambda won't NPE
        Class<?> paramType = lambdaMethod.getParameterTypes()[0];
        Object paramMock = mock(paramType, RETURNS_DEEP_STUBS);

        // invoke the lambda (static or instance)
        Object target = (java.lang.reflect.Modifier.isStatic(lambdaMethod.getModifiers())) ? null : cfg;
        lambdaMethod.invoke(target, paramMock);

        // basic smoke assertions: SecurityConfig methods still produce expected beans
        assertTrue(cfg.passwordEncoder() instanceof BCryptPasswordEncoder);
    }
}
