package com.example.demo.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class DatabaseOperation {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public String formatName(String initialName, String format) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());

        return initialName.concat(timestamp).concat(format);

    }
    public List<String> getTableNames() {
        String query = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' AND table_type = 'BASE TABLE'";
        return jdbcTemplate.queryForList(query, String.class);
    }
    public List<Map<String, Object>> getRowsForTable(String tableName) {
        String query = "SELECT * FROM " + tableName;
        return jdbcTemplate.queryForList(query);
    }
}