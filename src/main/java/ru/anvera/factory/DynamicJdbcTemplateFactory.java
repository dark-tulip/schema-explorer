package ru.anvera.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;


@Component
@RequiredArgsConstructor
public class DynamicJdbcTemplateFactory {

    private final DynamicDataSourceConnectionFactory dynamicDataSourceConnectionFactory;

    public JdbcTemplate createJdbcTemplate(String dbType, String url, String username, String password) {
        DataSource dataSource = dynamicDataSourceConnectionFactory.createDataSource(dbType, url, username, password);
        return new JdbcTemplate(dataSource);
    }
}
