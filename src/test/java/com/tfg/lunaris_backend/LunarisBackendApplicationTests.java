package com.tfg.lunaris_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Test para {@link LunarisBackendApplication}.
 */
@SpringBootTest
class LunarisBackendApplicationTests {

	@MockitoBean
	JavaMailSender mailSender;

	/**
	 * Verifica que el contexto de la aplicación se carga correctamente.
	 */
	@Test
	void contextLoads() {
	}

}
