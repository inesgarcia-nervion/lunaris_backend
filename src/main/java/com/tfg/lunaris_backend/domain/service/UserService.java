package com.tfg.lunaris_backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.tfg.lunaris_backend.data.repository.UserRepository;
import com.tfg.lunaris_backend.domain.dto.BookStatusRequest;
import com.tfg.lunaris_backend.domain.model.User;
import com.tfg.lunaris_backend.presentation.exceptions.UserNotFoundException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio que maneja la lógica de negocio relacionada con los usuarios.
 * 
 * Proporciona métodos para crear, actualizar, eliminar y obtener usuarios.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Obtiene todos los usuarios.
     * 
     * @return lista de usuarios
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Obtiene un usuario por su ID.
     * 
     * @param id ID del usuario
     * @return usuario encontrado
     * @throws UserNotFoundException si no se encuentra el usuario con el id
     *                               proporcionado
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id " + id));
    }

    /**
     * Crea un nuevo usuario.
     * 
     * @param user objeto con los datos del usuario a crear
     * @return usuario creado
     */
    public User createUser(User user) {
        if (user.getUsername() != null && userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        if (user.getEmail() != null && userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    /**
     * Actualiza un usuario existente.
     * 
     * @param id          ID del usuario a actualizar
     * @param userDetails detalles del usuario a actualizar
     * @return usuario actualizado
     * @throws UserNotFoundException si no se encuentra el usuario con el id
     *                               proporcionado
     */
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id " + id));
        if (userDetails.getUsername() != null && !userDetails.getUsername().isEmpty()) {
            user.setUsername(userDetails.getUsername());
        }
        if (userDetails.getEmail() != null && !userDetails.getEmail().isEmpty()) {
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        return userRepository.save(user);
    }

    /**
     * Elimina un usuario por su ID.
     * 
     * @param id ID del usuario a eliminar
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Actualiza un usuario existente por su nombre de usuario.
     * 
     * @param username    nombre de usuario del usuario a actualizar
     * @param userDetails detalles del usuario a actualizar
     * @return usuario actualizado
     * @throws UserNotFoundException si no se encuentra el usuario con el nombre de
     *                               usuario proporcionado
     */
    public User updateUserByUsername(String username, User userDetails) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con username " + username));
        if (userDetails.getUsername() != null && !userDetails.getUsername().isEmpty()) {
            user.setUsername(userDetails.getUsername());
        }
        if (userDetails.getEmail() != null && !userDetails.getEmail().isEmpty()) {
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        if (userDetails.getAvatarUrl() != null) {
            user.setAvatarUrl(userDetails.getAvatarUrl());
        }
        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con username " + username));
    }

    /**
     * Devuelve las tres listas de estado de lectura del usuario como un mapa
     * con claves "planParaLeer", "leyendo" y "leido", cada una con un array de
     * objetos libro.
     *
     * @param username nombre del usuario
     * @return mapa con las tres listas de estado
     */
    public Map<String, List<Object>> getBookStatusLists(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con username " + username));
        Map<String, List<Object>> result = new LinkedHashMap<>();
        result.put("planParaLeer", parseBookList(user.getPlanParaLeerJson()));
        result.put("leyendo", parseBookList(user.getLeyendoJson()));
        result.put("leido", parseBookList(user.getLeidoJson()));
        return result;
    }

    /**
     * Establece el estado de lectura de un libro para el usuario dado.
     * El libro se elimina de cualquier otro estado antes de ser añadido al nuevo.
     * Si status es null o vacío, el libro se elimina de todos los estados.
     *
     * @param username nombre del usuario
     * @param request  solicitud con bookId, status y bookData
     * @return mapa actualizado con las tres listas de estado
     */
    public Map<String, List<Object>> setBookStatus(String username, BookStatusRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con username " + username));

        String bookId = request.getBookId();
        String status = request.getStatus();
        Object bookData = request.getBookData();

        List<Object> planList = parseBookList(user.getPlanParaLeerJson());
        List<Object> leyendoList = parseBookList(user.getLeyendoJson());
        List<Object> leidoList = parseBookList(user.getLeidoJson());

        planList.removeIf(b -> bookIdMatches(b, bookId));
        leyendoList.removeIf(b -> bookIdMatches(b, bookId));
        leidoList.removeIf(b -> bookIdMatches(b, bookId));

        if (status != null && !status.isBlank()) {
            switch (status) {
                case "Plan para leer" -> planList.add(bookData);
                case "Leyendo" -> leyendoList.add(bookData);
                case "Leído" -> leidoList.add(bookData);
                default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Estado no válido: " + status + ". Use 'Plan para leer', 'Leyendo' o 'Leído'");
            }
        }

        user.setPlanParaLeerJson(serializeBookList(planList));
        user.setLeyendoJson(serializeBookList(leyendoList));
        user.setLeidoJson(serializeBookList(leidoList));
        userRepository.save(user);

        Map<String, List<Object>> result = new LinkedHashMap<>();
        result.put("planParaLeer", planList);
        result.put("leyendo", leyendoList);
        result.put("leido", leidoList);
        return result;
    }

    /**
     * Es un método auxiliar para convertir una cadena JSON que representa una lista
     * de
     * objetos en una lista de objetos Java. Si la cadena es nula o vacía, devuelve
     * una lista vacía.
     *
     * @param json cadena JSON que representa una lista de objetos
     * @return lista de objetos Java
     */
    private List<Object> parseBookList(String json) {
        if (json == null || json.isBlank())
            return new ArrayList<>();
        try {
            return objectMapper.readValue(json, new TypeReference<List<Object>>() {
            });
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Es un método auxiliar para convertir una lista de objetos Java en una cadena
     * JSON
     * que representa esa lista. Si ocurre un error durante la conversión, devuelve
     * una cadena JSON vacía "[]".
     * 
     * @param list lista de objetos Java a convertir
     * @return cadena JSON que representa la lista de objetos
     */
    private String serializeBookList(List<Object> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }

    /**
     * Es un método auxiliar para verificar si un objeto libro coincide con un
     * bookId dado.
     * El método intenta extraer los campos "key" y "apiId" del objeto libro
     * (asumiendo que es un mapa) y compara su valor con el bookId proporcionado. Si
     * alguno de los campos coincide, devuelve true. Si el objeto libro no tiene
     * esos campos o si ocurre cualquier error durante la verificación, devuelve
     * false.
     * 
     * @param book   objeto libro a verificar, se espera que sea un mapa con campos
     *               "key" y/o "api
     * @param bookId ID del libro a comparar con los campos del objeto libro
     * @return true si el objeto libro coincide con el bookId, false en caso
     *         contrario
     */
    @SuppressWarnings("unchecked")
    private boolean bookIdMatches(Object book, String bookId) {
        if (book == null || bookId == null)
            return false;
        try {
            Map<String, Object> map = (Map<String, Object>) book;
            Object key = map.get("key");
            if (key != null && bookId.equals(key.toString()))
                return true;
            Object apiId = map.get("apiId");
            if (apiId != null && bookId.equals(apiId.toString()))
                return true;
        } catch (Exception e) {
            // ignore
        }
        return false;
    }
}
