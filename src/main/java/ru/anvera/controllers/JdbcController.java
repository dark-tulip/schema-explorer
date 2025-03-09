package ru.anvera.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import ru.anvera.services.JdbcService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class JdbcController {

    private final JdbcService jdbcService;

    public JdbcController(JdbcService jdbcService) {
        this.jdbcService = jdbcService;
    }

    @GetMapping("/rows")
    public List<List<String>> getRows(@RequestParam(defaultValue = "10") int limit) {
        return jdbcService.getRows(limit);
    }
}
