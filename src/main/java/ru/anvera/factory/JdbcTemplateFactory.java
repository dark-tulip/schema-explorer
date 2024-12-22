package ru.anvera.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;


@Component
@RequiredArgsConstructor
public class JdbcTemplateFactory {

    private final DynamicDataSourceFactory dynamicDataSourceFactory;

    public JdbcTemplate createJdbcTemplate(String dbType, String url, String username, String password) {
        DataSource dataSource = dynamicDataSourceFactory.createDataSource(dbType, url, username, password);
        return new JdbcTemplate(dataSource);
    }
}
