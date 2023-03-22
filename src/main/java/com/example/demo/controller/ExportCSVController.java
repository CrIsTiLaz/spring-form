package com.example.demo.controller;

import com.example.demo.utils.DatabaseOperation;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class ExportCSVController {
    @Autowired
    private DatabaseOperation databaseOperation;
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");

        var tableNames = databaseOperation.getTableNames();
        var finalName = databaseOperation.formatName("export", ".csv");
        String s = String.format("attachment; filename=\"%s\"", finalName);
        response.setHeader("Content-Disposition", s);

        List<Map<String, Object>> rows = new ArrayList<>();

        for (String tableName : tableNames) {
            var fileOutputStream = new FileWriter(finalName);
            rows.addAll(databaseOperation.getRowsForTable(tableName));

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
}