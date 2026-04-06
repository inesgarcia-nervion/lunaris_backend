package com.tfg.lunaris_backend.domain.service;

import com.tfg.lunaris_backend.data.repository.UserRepository;
import com.tfg.lunaris_backend.domain.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * Servicio que maneja la lógica de negocio relacionada con los detalles de usuario para la autenticación.
 * 
 * Implementa UserDetailsService para cargar los detalles de usuario desde la base de datos.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Carga los detalles de un usuario por su nombre de usuario.
     * @param username nombre de usuario
     * @return detalles del usuario
     * @throws UsernameNotFoundException si no se encuentra el usuario
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        String roleName = (user.getRole() != null && !user.getRole().isBlank()) ? user.getRole() : "USER";
        Collection<GrantedAuthority> authorities = List
                .of(new SimpleGrantedAuthority("ROLE_" + roleName.toUpperCase()));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities);
    }
}
