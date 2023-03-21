package com.example.demo.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class NewController {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");

        var tableNames = getTableNames();
        var finalName = formatName("export", ".csv");
        String s = String.format("attachment; filename=\"%s\"", finalName);
        response.setHeader("Content-Disposition", s);

        List<Map<String, Object>> rows = new ArrayList<>();

        for (String tableName : tableNames) {
            var fileOutputStream = new FileWriter(finalName);
            rows.addAll(getRowsForTable(tableName));

            var writer = new PrintWriter(fileOutputStream);
            var csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(rows.get(0).keySet().toArray(new String[0])));
                System.out.println(Arrays.toString(rows.get(0).keySet().toArray(new String[0])));
                for (Map<String, Object> row : rows) {
                    csvPrinter.printRecord(row.values());
                    System.out.println(row.values());
                }
                csvPrinter.flush();
                csvPrinter.close();
                fileOutputStream.close();

        }
    }
    private String formatName(String initialName, String format) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());

        return initialName.concat(timestamp).concat(format);

    }
    private List<String> getTableNames() {
        String query = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' AND table_type = 'BASE TABLE'";
        return jdbcTemplate.queryForList(query, String.class);
    }
    private List<Map<String, Object>> getRowsForTable(String tableName) {
        String query = "SELECT * FROM " + tableName;
        return jdbcTemplate.queryForList(query);
    }
}