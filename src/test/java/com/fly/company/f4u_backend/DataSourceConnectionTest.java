package com.fly.company.f4u_backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DataSourceConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void dataSourceShouldProvideValidConnection() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            assertThat(conn).isNotNull();
            assertThat(conn.isValid(5)).isTrue();
        }
    }
}
