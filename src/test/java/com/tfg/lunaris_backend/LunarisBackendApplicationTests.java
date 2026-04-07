package com.tfg.lunaris_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class LunarisBackendApplicationTests {

	@MockitoBean
	JavaMailSender mailSender;

	@Test
	void contextLoads() {
	}

}
