package com.example.music.controller;

import com.example.music.common.result.Result;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TestDbController {

    private final JdbcTemplate jdbcTemplate;

    public TestDbController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/test/dbCharset")
    public Result<Map<String, String>> dbCharset() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SHOW VARIABLES WHERE Variable_name IN ('character_set_client','character_set_connection','character_set_results','collation_connection')"
        );
        Map<String, String> m = new LinkedHashMap<>();
        for (Map<String, Object> r : rows) {
            m.put(String.valueOf(r.get("Variable_name")), String.valueOf(r.get("Value")));
        }
        return Result.ok(m);
    }
}
