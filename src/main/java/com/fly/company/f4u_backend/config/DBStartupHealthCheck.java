package com.fly.company.f4u_backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
public class DBStartupHealthCheck {

    private static final Logger log = LoggerFactory.getLogger(DBStartupHealthCheck.class);

    @Bean
    public CommandLineRunner checkDatabase(DataSource dataSource) {
        return args -> {
            log.info("DBStartupHealthCheck: Comprobando conexión a la base de datos...");
            try (Connection conn = dataSource.getConnection()) {
                boolean valid = conn.isValid(5);
                if (valid) {
                    log.info("DBStartupHealthCheck: Conexión a la BD OK - URL: {} | Usuario: {}",
                            conn.getMetaData().getURL(), conn.getMetaData().getUserName());
                } else {
                    log.error("DBStartupHealthCheck: La conexión a la BD NO ES VÁLIDA");
                }
            } catch (Exception e) {
                log.error("DBStartupHealthCheck: Error al conectar a la BD: {}", e.getMessage(), e);
            }
        };
    }
}
