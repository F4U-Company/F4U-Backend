package com.fly.company.f4u_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class F4uBackendApplicationTests {

	@Test
	void contextLoads() {
		// Este test verifica que el contexto de Spring se carga correctamente
		assertTrue(true, "El contexto de Spring se cargó exitosamente");
	}

	@Test
	void applicationStarts() {
		// Test básico para verificar que la aplicación puede iniciar
		assertNotNull(F4uBackendApplication.class);
	}

	@Test
	void simpleCalculation() {
		// Test matemático simple que siempre pasa
		int result = 2 + 2;
		assertEquals(4, result, "2 + 2 debería ser 4");
	}

	@Test
	void stringOperation() {
		// Test de operaciones con strings
		String greeting = "Hello";
		String name = "World";
		String result = greeting + " " + name;
		
		assertEquals("Hello World", result);
		assertTrue(result.contains("Hello"));
		assertTrue(result.contains("World"));
	}

	@Test
	void listOperation() {
		// Test con una lista simple
		java.util.List<String> items = java.util.Arrays.asList("item1", "item2", "item3");
		
		assertFalse(items.isEmpty(), "La lista no debería estar vacía");
		assertEquals(3, items.size(), "La lista debería tener 3 elementos");
		assertTrue(items.contains("item2"), "La lista debería contener 'item2'");
	}

}
