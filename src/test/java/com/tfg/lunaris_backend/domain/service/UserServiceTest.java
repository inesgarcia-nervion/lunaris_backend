package com.tfg.lunaris_backend.domain.service;

import com.tfg.lunaris_backend.data.repository.UserRepository;
import com.tfg.lunaris_backend.domain.model.User;
import com.tfg.lunaris_backend.presentation.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repo;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserService svc;

    @Test
    void getAllAndGetById() {
        User u = new User(); u.setUsername("u");
        when(repo.findAll()).thenReturn(List.of(u));
        assertFalse(svc.getAllUsers().isEmpty());

        when(repo.findById(1L)).thenReturn(Optional.of(u));
        assertSame(u, svc.getUserById(1L));
    }

    @Test
    void getUserByIdNotFoundThrows() {
        when(repo.findById(9L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> svc.getUserById(9L));
    }

    @Test
    void createAndUpdateAndDelete() {
        User u = new User(); u.setPassword("p");
        when(encoder.encode("p")).thenReturn("encoded");
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));
        User created = svc.createUser(u);
        assertEquals("encoded", created.getPassword());

        User existing = new User(); existing.setUsername("old");
        when(repo.findById(2L)).thenReturn(Optional.of(existing));
        User details = new User(); details.setUsername("new"); details.setPassword("np");
        when(encoder.encode("np")).thenReturn("enc2");
        User updated = svc.updateUser(2L, details);
        assertEquals("new", updated.getUsername());

        svc.deleteUser(3L);
        verify(repo).deleteById(3L);
    }

    @Test
    void updateByUsername() {
        User existing = new User(); existing.setUsername("u1");
        when(repo.findByUsername("u1")).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(existing);
        User details = new User(); details.setEmail("e@x");
        User res = svc.updateUserByUsername("u1", details);
        assertEquals("e@x", res.getEmail());
    }

    @Test
    void updateUser_notFoundThrows() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> svc.updateUser(99L, new User()));
    }

    @Test
    void updateUserByUsername_notFoundThrows() {
        when(repo.findByUsername("x")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> svc.updateUserByUsername("x", new User()));
    }

    @Test
    void updateUser_allFieldsBranches() {
        User existing = new User(); existing.setUsername("old"); existing.setEmail("old@e");
        when(repo.findById(5L)).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(existing);

        // Set username, email AND password
        User details = new User();
        details.setUsername("new");
        details.setEmail("new@e");
        details.setPassword("newpass");
        when(encoder.encode("newpass")).thenReturn("enc");

        User updated = svc.updateUser(5L, details);
        assertEquals("new", updated.getUsername());
        assertEquals("new@e", updated.getEmail());
        assertEquals("enc", updated.getPassword());
    }

    @Test
    void updateUser_emptyFieldsNotOverwritten() {
        User existing = new User(); existing.setUsername("old"); existing.setEmail("old@e");
        when(repo.findById(6L)).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(existing);

        User details = new User();
        details.setUsername(""); // empty → not updated
        details.setEmail(null);  // null  → not updated
        details.setPassword(""); // empty → not updated

        User updated = svc.updateUser(6L, details);
        assertEquals("old", updated.getUsername());
        assertEquals("old@e", updated.getEmail());
    }

    @Test
    void updateUserByUsername_allFields() {
        User existing = new User(); existing.setUsername("u2"); existing.setEmail("u2@e");
        when(repo.findByUsername("u2")).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(existing);

        User details = new User();
        details.setUsername("u2-new");
        details.setPassword("pwd");
        when(encoder.encode("pwd")).thenReturn("encoded");

        User updated = svc.updateUserByUsername("u2", details);
        assertEquals("u2-new", updated.getUsername());
        assertEquals("encoded", updated.getPassword());
    }

    @Test
    void createUser_nullPassword_notEncoded() {
        User u = new User(); // password is null
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));
        User created = svc.createUser(u);
        assertNull(created.getPassword());
        verify(encoder, never()).encode(any());
    }
}
