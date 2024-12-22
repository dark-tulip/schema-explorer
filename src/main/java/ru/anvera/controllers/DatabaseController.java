//package ru.anvera.controllers;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RestController;
//import ru.anvera.repos.DatabaseMetadataRepository;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController("db/")
//@RequiredArgsConstructor
//public class DatabaseController {
//
//    private final DatabaseMetadataRepository databaseMetadataRepository;
//1
//
//    @GetMapping("/schemas")
//    public List<String> getSchemas() {
//        return databaseMetadataRepository.getSchemas();
//    }
//
//    @GetMapping("/tables/{schema}")
//    public List<String> getTables(@PathVariable String schema) {
//        return databaseMetadataRepository.getTableNames(schema);
//    }
//
//    @GetMapping("/columns/{schema}/{table}")
//    public List<Map<String, Object>> getColumns(@PathVariable String schema, @PathVariable String table) {
//        return databaseMetadataRepository.getColumnMetadataBySchemaNameAndTableName(schema, table);
//    }
//}
