package com.tfg.lunaris_backend.data.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Test para las interfaces de repositorio.
 */
class RepositoryInterfacesTest {

    private static final String[] REPOS = new String[]{
            "com.tfg.lunaris_backend.data.repository.UserRepository",
            "com.tfg.lunaris_backend.data.repository.UserListRepository",
            "com.tfg.lunaris_backend.data.repository.SagaRepository",
            "com.tfg.lunaris_backend.data.repository.ReviewRepository",
            "com.tfg.lunaris_backend.data.repository.PostRepository",
            "com.tfg.lunaris_backend.data.repository.PasswordResetTokenRepository",
            "com.tfg.lunaris_backend.data.repository.GenreRepository",
            "com.tfg.lunaris_backend.data.repository.BookRequestRepository",
            "com.tfg.lunaris_backend.data.repository.BookRepository",
            "com.tfg.lunaris_backend.data.repository.AuthorRepository"
    };

    /**
     * Verifica que las interfaces de repositorio extienden JpaRepository.
     */
    @Test
    void repositoryInterfaces_extendJpaRepository() throws Exception {
        for (String name : REPOS) {
            Class<?> cls = Class.forName(name);
            assertTrue(cls.isInterface(), name + " should be an interface");
            assertTrue(JpaRepository.class.isAssignableFrom(cls), name + " should extend JpaRepository");
        }
    }

    /**
     * Verifica que las interfaces de repositorio declaran los métodos esperados.
     * @throws Exception si ocurre un error al cargar las clases o acceder a los métodos
     */
    @Test
    void repositoryInterfaces_declareExpectedMethods() throws Exception {
        Map<String, Map<String, List<Integer>>> expectations = new HashMap<>();

        expectations.put("com.tfg.lunaris_backend.data.repository.UserRepository", Map.of(
            "findByUsername", List.of(1),
            "findByEmail", List.of(1)
        ));

        expectations.put("com.tfg.lunaris_backend.data.repository.SagaRepository", Map.of(
            "findByName", List.of(1),
            "findByBookTitleIgnoreCase", List.of(1)
        ));

        expectations.put("com.tfg.lunaris_backend.data.repository.ReviewRepository", Map.of(
            "findByBookApiId", List.of(1),
            "findAllByOrderByIdDesc", List.of(0)
        ));

        expectations.put("com.tfg.lunaris_backend.data.repository.PostRepository", Map.of(
            "findAllByOrderByIdDesc", List.of(0)
        ));

        expectations.put("com.tfg.lunaris_backend.data.repository.PasswordResetTokenRepository", Map.of(
            "findByToken", List.of(1),
            "deleteByUser", List.of(1)
        ));

        expectations.put("com.tfg.lunaris_backend.data.repository.GenreRepository", Map.of(
            "findByNameIgnoreCase", List.of(1)
        ));

        expectations.put("com.tfg.lunaris_backend.data.repository.BookRepository", Map.of(
            "findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase", List.of(2, 3),
            "findByApiId", List.of(1)
        ));

        expectations.forEach((repo, methods) -> {
            try {
                Class<?> cls = Class.forName(repo);
                for (Entry<String, List<Integer>> e : methods.entrySet()) {
                    String methodName = e.getKey();
                    List<Integer> expectedParamsList = e.getValue();
                    boolean foundAny = false;
                    for (Method m : cls.getDeclaredMethods()) {
                        if (!m.getName().equals(methodName)) continue;
                        int p = m.getParameterCount();
                        if (expectedParamsList.contains(p)) {
                            foundAny = true;
                            break;
                        }
                    }
                    assertTrue(foundAny, repo + " should declare method " + methodName + " with one of param counts " + expectedParamsList);
                }
            } catch (ClassNotFoundException ex) {
                fail("Repository class not found: " + repo);
            }
        });
    }
}
