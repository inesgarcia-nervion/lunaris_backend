package com.tfg.lunaris_backend.data.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void repositoryInterfaces_extendJpaRepository() throws Exception {
        for (String name : REPOS) {
            Class<?> cls = Class.forName(name);
            assertTrue(cls.isInterface(), name + " should be an interface");
            assertTrue(JpaRepository.class.isAssignableFrom(cls), name + " should extend JpaRepository");
        }
    }

    @Test
    void repositoryInterfaces_declareExpectedMethods() throws Exception {
        // map of repository to expected method name -> parameter counts
        java.util.Map<String, java.util.Map<String, java.util.List<Integer>>> expectations = new java.util.HashMap<>();

        expectations.put("com.tfg.lunaris_backend.data.repository.UserRepository", java.util.Map.of(
            "findByUsername", java.util.List.of(1),
            "findByEmail", java.util.List.of(1)
        ));

        expectations.put("com.tfg.lunaris_backend.data.repository.SagaRepository", java.util.Map.of(
            "findByName", java.util.List.of(1),
            "findByBookTitleIgnoreCase", java.util.List.of(1)
        ));

        expectations.put("com.tfg.lunaris_backend.data.repository.ReviewRepository", java.util.Map.of(
            "findByBookApiId", java.util.List.of(1),
            "findAllByOrderByIdDesc", java.util.List.of(0)
        ));

        expectations.put("com.tfg.lunaris_backend.data.repository.PostRepository", java.util.Map.of(
            "findAllByOrderByIdDesc", java.util.List.of(0)
        ));

        expectations.put("com.tfg.lunaris_backend.data.repository.PasswordResetTokenRepository", java.util.Map.of(
            "findByToken", java.util.List.of(1),
            "deleteByUser", java.util.List.of(1)
        ));

        expectations.put("com.tfg.lunaris_backend.data.repository.GenreRepository", java.util.Map.of(
            "findByNameIgnoreCase", java.util.List.of(1)
        ));

        expectations.put("com.tfg.lunaris_backend.data.repository.BookRepository", java.util.Map.of(
            "findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase", java.util.List.of(2, 3),
            "findByApiId", java.util.List.of(1)
        ));

        expectations.forEach((repo, methods) -> {
            try {
                Class<?> cls = Class.forName(repo);
                for (java.util.Map.Entry<String, java.util.List<Integer>> e : methods.entrySet()) {
                    String methodName = e.getKey();
                    java.util.List<Integer> expectedParamsList = e.getValue();
                    boolean foundAny = false;
                    for (java.lang.reflect.Method m : cls.getDeclaredMethods()) {
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
