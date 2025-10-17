package com.fly.company.f4u_backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuración para cargar variables de entorno desde el archivo .env
 * Esto permite usar ${VAR} en application.properties sin necesidad de establecer
 * las variables en el sistema operativo o IDE.
 */
public class DotenvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        try {
            // Cargar el archivo .env desde la raíz del proyecto
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing() // No fallar si el archivo no existe
                    .load();

            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            Map<String, Object> dotenvMap = new HashMap<>();

            // Transferir todas las variables del .env al entorno de Spring
            dotenv.entries().forEach(entry -> {
                dotenvMap.put(entry.getKey(), entry.getValue());
                System.out.println("✅ Cargada variable: " + entry.getKey());
            });

            // Agregar las variables como PropertySource con alta prioridad
            environment.getPropertySources()
                    .addFirst(new MapPropertySource("dotenvProperties", dotenvMap));

            System.out.println("✅ Archivo .env cargado exitosamente con " + dotenvMap.size() + " variables");

        } catch (Exception e) {
            System.err.println("⚠️ No se pudo cargar el archivo .env: " + e.getMessage());
            System.err.println("⚠️ La aplicación usará las variables de entorno del sistema o valores por defecto");
        }
    }
}
